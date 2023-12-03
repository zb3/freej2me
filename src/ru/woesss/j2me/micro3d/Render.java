/*
 *  Copyright 2022 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ru.woesss.j2me.micro3d;

import org.recompile.mobile.PlatformGraphics;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

import com.mascotcapsule.micro3d.v3.Graphics3D;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.microedition.lcdui.Graphics;

import ru.woesss.j2me.micro3d.RenderNode.FigureNode;

/*
 * zb3: currently this only allows 1 surface at a time
 * is that a performance issue? so far no game seems to use more than one surface
 */

public class Render extends ClassWithNatives {
	private static final int PDATA_COLOR_MASK = (Graphics3D.PDATA_COLOR_PER_COMMAND | Graphics3D.PDATA_COLOR_PER_FACE);
	private static final int PDATA_COLOR_PER_VERTEX = PDATA_COLOR_MASK;
	private static final int PDATA_NORMAL_MASK = Graphics3D.PDATA_NORMAL_PER_VERTEX;
	private static final int PDATA_TEXCOORD_MASK = Graphics3D.PDATA_TEXURE_COORD;
	private static final int[] PRIMITIVE_SIZES = {0, 1, 2, 3, 4, 1};

	final Environment env = new Environment();

	private final int[] bgTextureId = {-1};
	private final float[] MVP_TMP = new float[16];

	private Graphics targetGraphics;
	private final Rectangle gClip = new Rectangle();
	private final Rectangle clip = new Rectangle();
	private boolean backCopied;
	private final LinkedList<RenderNode> stack = new LinkedList<>();
	private int flushStep;
	private final boolean postCopy2D = !Boolean.getBoolean("micro3d.v3.render.no-mix2D3D");
	private final boolean preCopy2D = !Boolean.getBoolean("micro3d.v3.render.background.ignore");
	private int[] bufHandles = {-1, -1, -1};
	private int clearColor;
	private TextureImpl targetTexture;

	private int[] pixelBuffer;

	private long eglHandle = 0;
	public native long _eglInit();
	public native void _eglMakeSurface(long ptr, int width, int height);
	public native void _eglBind(long ptr);
	public native void _eglRelease(long ptr);
	public native void _eglDestroy(long ptr);
	public static native String _getGLError();
	public native void _glSetVp1(int width, int height);
	public native void _glSetVp2(int width, int height);
	public native void _glSetClip1(long handle, int x, int y, int width, int height);
	public native void _glClearDepth(); 
	public native void _glClearWithColor(int clearColor);
	public static native void _glApplyBlending(int blendMode);
	public native void _glBindBgTexture(int[] bgTextureIdArr, boolean filter);
	public native void _glBlitToTexture(int[] pixelsArr, int width, int height);
	public native void _glcopy2DScreen(int aposition, int atexture, boolean preProcess);
	public native void _bindBT1(int[] bufHandlesArr, FloatBuffer vertices, ByteBuffer texCoords, FloatBuffer normals);
	public native void _rfPart1(boolean doDepthMask);
	public native void _rfPolyT(int[] bufHandlesArr, int aPosition, int aColorData, int aMaterial, int aNormal);
	public native void _glEVAA(int arr);
	public native void _glDVAA(int arr);  
	public native void _glUnbindABuffer();
	public native void _glDisableBlending();
	public native void _rfPolyC(int[] bufHandlesArr, int offset, int aPosition, int aColorData, int aMaterial, int aNormal); 
	public native void _glECFDA(int e, int pos, int cnt);
	public native void _glReadPixelsToBuffer(int x, int y, int width, int height, ByteBuffer buffer);
	public native void _glReadARGB32PixelsToArray(int x, int y, int width, int height, int[] pixelsArr);
	public native void _flushGl1(boolean flush);
	public native void _glVertexAttrib2f(int attrib, float f1, float f2);
	public native void _glVertexAttrib3f(int attrib, float f1, float f2, float f3);
	public native void _glVertexAttribPointerf(int attrib, int cnt, boolean sth, int sth2, FloatBuffer buff);
	public native void _glVertexAttribPointerb(int attrib, int cnt, boolean sth, int sth2, ByteBuffer buff);
	public native void _glDrawTriangles(int i1, int i2);
	public native void _glDrawPoints(int i1, int i2);  
	public native void _glDrawLines(int i1, int i2);
	public native void _glSetCullFace(boolean enable);
	public native void _glDepthFuncLess();
	public native void _glRps1(int uIsTransparency, int val, int numPrimitives);


	/**
	 * Utility method for debugging OpenGL calls.
	 * <p>
	 * If the operation is not successful, the check throws an error.
	 *
	 * @param glOperation - Name of the OpenGL call to check.
	 */
	static void checkGlError(String glOperation) {
		String glError = _getGLError();
		if (glError != null) {
			throw new RuntimeException(glOperation + ": glError " + glError);
		}
	}

	public static Render getRender() {
		return InstanceHolder.instance;
	}

	private void init() {
		eglHandle = _eglInit();
	}

	public synchronized void bind(Graphics graphics) {
		this.targetGraphics = graphics;

		BufferedImage canvas = ((PlatformGraphics)graphics).getCanvas();
		
		if (eglHandle == 0) {
			init();
		}

		final int clipX = graphics.getClipX() + graphics.getTranslateX();
		final int clipY = graphics.getClipY() + graphics.getTranslateY();
		final int width = graphics.getClipWidth();
		final int height = graphics.getClipHeight();

		//System.out.format("log: translate xy %d %d %d %d\n", graphics.getClipX(), graphics.getClipY(), graphics.getTranslateX(), graphics.getTranslateY());

		if (env.width != width || env.height != height) {

			System.out.format("log: new surface %d %d  %d %d  %d %d\n", graphics.getClipX(), graphics.getClipY(), graphics.getTranslateX(), graphics.getTranslateY(), graphics.getClipWidth(), graphics.getClipHeight());
			_eglMakeSurface(eglHandle, width, height);

			_eglBind(eglHandle);

			_glSetVp1(width, height);

			Program.create();
			env.width = width;
			env.height = height;

			pixelBuffer = new int[width * height];
		}
		_eglBind(eglHandle);


		// func
		gClip.setBounds(clipX, clipY, width, height);

		// clip.setBounds(clipX, clipY, width, height);
		//_glSetClip1(eglHandle, clipX, clipY, clipW, clipH);
		clip.setBounds(0, 0, width, height);
		_glSetClip1(eglHandle, 0, 0, width, height);

		_glClearDepth();
		
		// func
		backCopied = false;
		_eglRelease(eglHandle);
	}

	public synchronized void bind(TextureImpl tex) {

		targetTexture = tex;
		int width = tex.getWidth();
		int height = tex.getHeight();
		if (eglHandle == 0) {
			init();
		}

		if (env.width != width || env.height != height) {
			System.out.println("log: new texture surface");
			
			_eglMakeSurface(eglHandle, width, height);
			
			_eglBind(eglHandle);

			// func
			_glSetVp2(width, height);
			// func
			Program.create();
			env.width = width;
			env.height = height;
		}
		_eglBind(eglHandle);
		Rectangle clip = this.clip;
		clip.setBounds(0, 0, width, height);
		gClip.setBounds(0, 0, width, height);
		_glSetClip1(eglHandle, 0, 0, width, height);
        _glClearWithColor(clearColor);
		backCopied = false;
		_eglRelease(eglHandle);

	}

	private static void applyBlending(int blendMode) {
		_glApplyBlending(blendMode);
	}

	private void copy2d(boolean preProcess) {
		if (targetTexture != null) {// render to texture
			return;
		}

		_glBindBgTexture(bgTextureId, Boolean.getBoolean("micro3d.v3.background.filter"));
	
		BufferedImage targetImage = ((PlatformGraphics)targetGraphics).getCanvas();
		
		// gets "srgb" with pre?
		// instead of null we might pass the array
		// zb3: this also assumes one-texture

		//int[] pixels = targetImage.getRGB(0, 0, env.width, env.height, null, 0, env.width);
		int[] pixels = targetImage.getRGB(gClip.x, gClip.y, env.width, env.height, null, 0, env.width);
		
		_glBlitToTexture(pixels, env.width, env.height);


		final Program.Simple program = Program.simple;
		program.use();

		_glcopy2DScreen(program.aPosition, program.aTexture, preProcess);

		checkGlError("copy2d");
		if (!preProcess) {
			return;
		}
		if (postCopy2D) {
			// zb3: if this is the entire image, this call is not useful
			// so gclip should be checked here
			// NAH, we have no idea what it does
			// resets g2dso that if overlayed on top of our 3d it can draw over?
			
			Graphics2D g2 = targetImage.createGraphics();
			g2.setComposite(AlphaComposite.Src); // overwrite even if transparent
		    g2.setColor(new Color(0, 0, 0, 0)); // transparent
			g2.fillRect(gClip.x, gClip.y,  gClip.width, gClip.height);
			g2.setComposite(AlphaComposite.SrcOver); //?

		}
		backCopied = true;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (eglHandle != 0) {
				_eglDestroy(eglHandle);
				eglHandle = 0;
			}
		} finally {
			super.finalize();
		}
	}

	void renderFigure(Model model,
					  TextureImpl[] textures,
					  int attrs,
					  float[] projMatrix,
					  float[] viewMatrix,
					  FloatBuffer vertices,
					  FloatBuffer normals,
					  Light light,
					  TextureImpl specular,
					  int toonThreshold,
					  int toonHigh,
					  int toonLow) {		
		boolean isTransparency = (attrs & Graphics3D.ENV_ATTR_SEMI_TRANSPARENT) != 0;
		if (!isTransparency && flushStep == 2) {
			return;
		} else if (!model.hasPolyT && !model.hasPolyC) {
			return;
		}

		MathUtil.multiplyMM(MVP_TMP, projMatrix, viewMatrix);

		_rfPart1(flushStep == 1);


		try {
			boolean isLight = (attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && normals != null;
			
			_bindBT1(bufHandles, vertices, model.texCoordArray, isLight ? normals : null);

			if (model.hasPolyT) {
				final Program.Tex program = Program.tex;
				program.use();

				_rfPolyT(bufHandles, program.aPosition, program.aColorData, program.aMaterial,
				isLight ? program.aNormal : -1);

				if (isLight) {
					program.setToonShading(attrs, toonThreshold, toonHigh, toonLow);
					program.setLight(light);
					program.setSphere((attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) == 0 ? null : specular);
				} else {
					_glDVAA(program.aNormal);
					program.setLight(null);
				}

				program.bindMatrices(MVP_TMP, viewMatrix);
				// Draw triangles
				renderModel(textures, model, isTransparency);
				_glDVAA(program.aPosition);
				_glDVAA(program.aColorData);
				_glDVAA(program.aMaterial);
				_glDVAA(program.aNormal);
				_glUnbindABuffer();
			}

			if (model.hasPolyC) {
				final Program.Color program = Program.color;
				program.use();

				int offset = model.numVerticesPolyT;

				_rfPolyC(bufHandles, offset, program.aPosition, program.aColorData, program.aMaterial,
				isLight ? program.aNormal : -1);

				if (isLight) {
					program.setLight(light);
					program.setSphere((attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) == 0 ? null : specular);
					program.setToonShading(attrs, toonThreshold, toonHigh, toonLow);
				} else {
					_glDVAA(program.aNormal);
					program.setLight(null);
				}
				program.bindMatrices(MVP_TMP, viewMatrix);
				renderModel(model, isTransparency);
				_glDVAA(program.aPosition);
				_glDVAA(program.aColorData);
				_glDVAA(program.aMaterial);
				_glDVAA(program.aNormal);
			}
		} finally {
			_glUnbindABuffer();
		}
	}

	private void renderModel(TextureImpl[] textures, Model model, boolean enableBlending) {
		if (textures == null || textures.length == 0) return;

		Program.Tex program = Program.tex;
		int[][][] meshes = model.subMeshesLengthsT;
		int length = meshes.length;
		int blendMode = 0;
		int pos = 0;
		if (flushStep == 1) {
			if (enableBlending) length = 1;
			_glDisableBlending();
		} else {
			int[][] mesh = meshes[blendMode++];
			int cnt = 0;
			for (int[] lens : mesh) {
				for (int len : lens) {
					cnt += len;
				}
			}
			pos += cnt;
		}
		while (blendMode < length) {
			int[][] texMesh = meshes[blendMode];
			if (flushStep == 2) {
				applyBlending(blendMode << 1);
			}
			for (int face = 0; face < texMesh.length; face++) {
				int[] lens = texMesh[face];
				if (face >= textures.length) {
					program.setTex(null);
				} else {
					TextureImpl tex = textures[face];
					program.setTex(tex);
				}
				int cnt = lens[0];
				if (cnt > 0) {
					_glECFDA(1, pos, cnt);
					pos += cnt;
				}
				cnt = lens[1];
				if (cnt > 0) {
					_glECFDA(0, pos, cnt);
					pos += cnt;
				}
			}
			blendMode++;
		}
		checkGlError("glDrawArrays");
	}

	private void renderModel(Model model, boolean enableBlending) {
		int[][] meshes = model.subMeshesLengthsC;
		int length = meshes.length;
		int pos = 0;
		int blendMode = 0;
		if (flushStep == 1) {
			if (enableBlending) length = 1;
			_glDisableBlending();
		} else {
			int[] mesh = meshes[blendMode++];
			int cnt = 0;
			for (int len : mesh) {
				cnt += len;
			}
			pos += cnt;
		}
		while (blendMode < length) {
			int[] mesh = meshes[blendMode];
			if (flushStep == 2) {
				applyBlending(blendMode << 1);
			}
			int cnt = mesh[0];
			if (cnt > 0) {
				_glECFDA(1, pos, cnt);
				pos += cnt;
			}
			cnt = mesh[1];
			if (cnt > 0) {
				_glECFDA(0, pos, cnt);
				pos += cnt;
			}
			blendMode++;
		}
		checkGlError("glDrawArrays");
	}

	public synchronized void release() {
		stack.clear();
		bindEglContext();
		if (targetTexture != null) {
			_glReadPixelsToBuffer(0, 0, 256, 256, targetTexture.image.getRaster());
			targetTexture = null;
		} else if (targetGraphics != null) {
			if (postCopy2D) {
				// zb3: it appears that it'd only be useful if we want to draw 2d
				// after binding - this'd require cleared image in previous bg copy
				copy2d(false);
			}

			//_glReadARGB32PixelsToArray(gClip.x, gClip.y, gClip.width, gClip.height, pixelBuffer);
			_glReadARGB32PixelsToArray(0, 0, gClip.width, gClip.height, pixelBuffer);

			((PlatformGraphics)targetGraphics).getCanvas().setRGB(gClip.x, gClip.y, gClip.width, gClip.height, pixelBuffer, 0, gClip.width);	

			/*try {
			Thread.sleep(2000);
			} catch(InterruptedException e){}
			*/
			targetGraphics = null;
		}
		releaseEglContext();
	}

	public synchronized void flush() {
		if (stack.isEmpty()) {
			return;
		}
		bindEglContext();
		try {
			if (!backCopied && preCopy2D) {
				copy2d(true);
			}
			flushStep = 1;
			for (RenderNode r : stack) {
				r.render(this);
			}
			flushStep = 2;
			for (RenderNode r : stack) {
				r.render(this);
				r.recycle();
			}
			_flushGl1(true);
		} finally {
			stack.clear();
			releaseEglContext();
		}
	}

	private void renderMeshC(RenderNode.PrimitiveNode node) {
		int command = node.command;
		Program.Color program = Program.color;
		program.use();
		if ((node.attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && (command & Graphics3D.PATTR_LIGHTING) != 0 && node.normals != null) {
			TextureImpl sphere = node.specular;
			if ((node.attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) != 0 && (command & Graphics3D.PATTR_SPHERE_MAP) != 0 && sphere != null) {
				_glVertexAttrib2f(program.aMaterial, 1, 1);
				program.setSphere(sphere);
			} else {
				_glVertexAttrib2f(program.aMaterial, 1, 0);
				program.setSphere(null);
			}
			program.setLight(node.light);
			program.setToonShading(node.attrs, node.toonThreshold, node.toonHigh, node.toonLow);

			((Buffer)node.normals).rewind();
			_glVertexAttribPointerf(program.aNormal, 3, false, 3 * 4, node.normals);
			_glEVAA(program.aNormal);
		} else {
			_glVertexAttrib2f(program.aMaterial, 0, 0);
			program.setLight(null);
			_glDVAA(program.aNormal);
		}

		program.bindMatrices(MVP_TMP, node.viewMatrix);

		((Buffer)node.vertices).rewind();
		_glVertexAttribPointerf(program.aPosition, 3, false, 3 * 4, node.vertices);
		_glEVAA(program.aPosition);

		if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
			program.setColor(node.colors);
		} else {
			((Buffer)node.colors).rewind();
			_glVertexAttribPointerb(program.aColorData, 3, true, 3, node.colors);
			_glEVAA(program.aColorData);
		}

		_glDrawTriangles(0, node.vertices.capacity() / 3);
		_glDVAA(program.aPosition);
		_glDVAA(program.aColorData);
		_glDVAA(program.aNormal);
		checkGlError("renderMeshC");
	}

	private void renderMeshT(RenderNode.PrimitiveNode node) {
		int command = node.command;
		Program.Tex program = Program.tex;
		program.use();
		if ((node.attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && (command & Graphics3D.PATTR_LIGHTING) != 0 && node.normals != null) {
			TextureImpl sphere = node.specular;
			if ((node.attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) != 0 && (command & Graphics3D.PATTR_SPHERE_MAP) != 0 && sphere != null) {
				_glVertexAttrib3f(program.aMaterial, 1, 1, command & Graphics3D.PATTR_COLORKEY);
				program.setSphere(sphere);
			} else {
				_glVertexAttrib3f(program.aMaterial, 1, 0, command & Graphics3D.PATTR_COLORKEY);
				program.setSphere(null);
			}
			program.setLight(node.light);
			program.setToonShading(node.attrs, node.toonThreshold, node.toonHigh, node.toonLow);

			((Buffer)node.normals).rewind();
			_glVertexAttribPointerf(program.aNormal, 3, false, 3 * 4, node.normals);
			_glEVAA(program.aNormal);
		} else {
			_glVertexAttrib3f(program.aMaterial, 0, 0, command & Graphics3D.PATTR_COLORKEY);
			program.setLight(null);
			_glDVAA(program.aNormal);
		}

		program.bindMatrices(MVP_TMP, node.viewMatrix);

		((Buffer)node.vertices).rewind();
		_glVertexAttribPointerf(program.aPosition, 3, false, 3 * 4, node.vertices);
		_glEVAA(program.aPosition);

		((Buffer)node.texCoords).rewind();
		_glVertexAttribPointerb(program.aColorData, 2,  false, 2, node.texCoords);
		_glEVAA(program.aColorData);

		program.setTex(node.texture);

		_glDrawTriangles(0, node.vertices.capacity() / 3);

		_glDVAA(program.aPosition);
		_glDVAA(program.aColorData);
		_glDVAA(program.aNormal);
		checkGlError("renderMeshT");
	}

	public void drawCommandList(int[] cmds) {
		if (Graphics3D.COMMAND_LIST_VERSION_1_0 != cmds[0]) {
			throw new IllegalArgumentException("Unsupported command list version: " + cmds[0]);
		}
		for (int i = 1; i < cmds.length; ) {
			int cmd = cmds[i++];
			switch (cmd & 0xFF000000) {
				case Graphics3D.COMMAND_AFFINE_INDEX:
					selectAffineTrans(cmd & 0xFFFFFF);
					break;
				case Graphics3D.COMMAND_AMBIENT_LIGHT: {
					env.light.ambIntensity = i++;
					break;
				}
				case Graphics3D.COMMAND_ATTRIBUTE:
					env.attrs = cmd & 0xFFFFFF;
					break;
				case Graphics3D.COMMAND_CENTER:
					setCenter(cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_CLIP:
					System.out.println("zb3: clip used");
					Rectangle tmp = clip.intersection(new Rectangle(cmds[i++], cmds[i++], cmds[i++], cmds[i++]));
					clip.x = tmp.x;
					clip.y = tmp.y;
					clip.width = tmp.width;
					clip.height = tmp.height;
					updateClip();
					break;
				case Graphics3D.COMMAND_DIRECTION_LIGHT: {
					env.light.x = i++;
					env.light.y = i++;
					env.light.z = i++;
					env.light.dirIntensity = i++;
					break;
				}
				case Graphics3D.COMMAND_FLUSH:
					flush();
					break;
				case Graphics3D.COMMAND_NOP:
					i += cmd & 0xFFFFFF;
					break;
				case Graphics3D.COMMAND_PARALLEL_SCALE:
					setOrthographicScale(cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_PARALLEL_SIZE:
					setOrthographicWH(cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_PERSPECTIVE_FOV:
					setPerspectiveFov(cmds[i++], cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_PERSPECTIVE_WH:
					setPerspectiveWH(cmds[i++], cmds[i++], cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_TEXTURE_INDEX:
					int tid = cmd & 0xFFFFFF;
					if (tid > 0 && tid < 16) {
						env.textureIdx = tid;
					}
					break;
				case Graphics3D.COMMAND_THRESHOLD:
					setToonParam(cmds[i++], cmds[i++], cmds[i++]);
					break;
				case Graphics3D.COMMAND_END:
					return;
				default:
					int type = cmd & 0x7000000;
					if (type == 0 || cmd < 0) {
						throw new IllegalArgumentException();
					}
					int num = cmd >> 16 & 0xFF;
					int sizeOf = PRIMITIVE_SIZES[type >> 24];
					int len = num * 3 * sizeOf;
					int vo = i;
					i += len;
					int no = i;
					if ((cmd & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_FACE) {
						i += num * 3;
					} else if ((cmd & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_VERTEX) {
						i += len;
					}
					int to = i;
					if (type == Graphics3D.PRIMITVE_POINT_SPRITES) {
						if ((cmd & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_POINT_SPRITE_PARAMS_PER_CMD) {
							i += 8;
						} else if ((cmd & PDATA_TEXCOORD_MASK) != Graphics3D.PDATA_TEXURE_COORD_NONE) {
							i += num * 8;
						}
					} else if ((cmd & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
						i += num * 2 * sizeOf;
					}

					int co = i;
					if ((cmd & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
						i++;
					} else if ((cmd & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_FACE) {
						i += num;
					} else if ((cmd & PDATA_COLOR_MASK) == PDATA_COLOR_PER_VERTEX) {
						i += num * sizeOf;
					}
					if (i > cmds.length) {
						throw new IllegalArgumentException();
					}
					postPrimitives(cmd, cmds, vo, cmds, no, cmds, to, cmds, co);
					break;
			}
		}
	}

	private void updateClip() {
		bindEglContext();
		Rectangle clip = this.clip;
		_glSetClip1(eglHandle, clip.x, clip.y, clip.width, clip.height);
		releaseEglContext();
	}

	public synchronized void postFigure(FigureImpl figure) {
		FigureNode rn;
		if (figure.stack.empty()) {
			rn = new FigureNode(this, figure);
		} else {
			rn = figure.stack.pop();
			rn.setData(this);
		}
		stack.add(rn);
	}

	public synchronized void postPrimitives(int command,
											int[] vertices, int vo,
											int[] normals, int no,
											int[] textureCoords, int to,
											int[] colors, int co) {
		if (command < 0) {
			throw new IllegalArgumentException();
		}
		int numPrimitives = command >> 16 & 0xff;
		FloatBuffer vcBuf;
		FloatBuffer ncBuf = null;
		ByteBuffer tcBuf = null;
		ByteBuffer colorBuf = null;
		switch ((command & 0x7000000)) {
			case Graphics3D.PRIMITVE_POINTS: {
				int vcLen = numPrimitives * 3;
				vcBuf = BufferUtils.createFloatBuffer(vcLen);
				for (int i = 0; i < vcLen; i++) {
					vcBuf.put(vertices[vo++]);
				}

				if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = BufferUtils.createByteBuffer(3);
					int color = colors[co];
					colorBuf.put((byte) (color >> 16 & 0xFF));
					colorBuf.put((byte) (color >> 8 & 0xFF));
					colorBuf.put((byte) (color & 0xFF));
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = BufferUtils.createByteBuffer(vcLen);
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						colorBuf.put((byte) (color >> 16 & 0xFF));
						colorBuf.put((byte) (color >> 8 & 0xFF));
						colorBuf.put((byte) (color & 0xFF));
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_LINES: {
				int vcLen = numPrimitives * 2 * 3;
				vcBuf = BufferUtils.createFloatBuffer(vcLen);
				for (int i = 0; i < vcLen; i++) {
					vcBuf.put(vertices[vo++]);
				}

				if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = BufferUtils.createByteBuffer(3);
					int color = colors[co];
					colorBuf.put((byte) (color >> 16 & 0xFF));
					colorBuf.put((byte) (color >> 8 & 0xFF));
					colorBuf.put((byte) (color & 0xFF));
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = BufferUtils.createByteBuffer(vcLen);
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_TRIANGLES: {
				int vcLen = numPrimitives * 3 * 3;
				vcBuf = BufferUtils.createFloatBuffer(vcLen);
				for (int i = 0; i < vcLen; i++) {
					vcBuf.put(vertices[vo++]);
				}
				if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_FACE) {
					ncBuf = BufferUtils.createFloatBuffer(vcLen);
					for (int end = no + numPrimitives * 3; no < end; ) {
						float x = normals[no++];
						float y = normals[no++];
						float z = normals[no++];
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
					}
				} else if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_VERTEX) {
					ncBuf = BufferUtils.createFloatBuffer(vcLen);
					for (int end = no + vcLen; no < end; ) {
						ncBuf.put(normals[no++]);
					}
				}
				if ((command & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
					if (env.getTexture() == null) {
						return;
					}
					int tcLen = numPrimitives * 3 * 2;
					tcBuf = BufferUtils.createByteBuffer(tcLen);
					for (int i = 0; i < tcLen; i++) {
						tcBuf.put((byte) textureCoords[to++]);
					}
				} else if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = BufferUtils.createByteBuffer(3);
					int color = colors[co];
					colorBuf.put((byte) (color >> 16 & 0xFF));
					colorBuf.put((byte) (color >> 8 & 0xFF));
					colorBuf.put((byte) (color & 0xFF));
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = BufferUtils.createByteBuffer(vcLen);
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_QUADS: {
				vcBuf = BufferUtils.createFloatBuffer(numPrimitives * 6 * 3);
				for (int i = 0; i < numPrimitives; i++) {
					int offset = vo + i * 4 * 3;
					int pos = offset;
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos++]); // A
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos++]); // B
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos++]); // C
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos]);   // D
					pos = offset;
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos]);   // A
					pos = offset + 2 * 3;
					vcBuf.put(vertices[pos++]).put(vertices[pos++]).put(vertices[pos]);   // C
				}
				if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_FACE) {
					ncBuf = BufferUtils.createFloatBuffer(numPrimitives * 6 * 3);
					for (int end = no + numPrimitives * 3; no < end; ) {
						float x = normals[no++];
						float y = normals[no++];
						float z = normals[no++];
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
						ncBuf.put(x).put(y).put(z);
					}
				} else if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_VERTEX) {
					ncBuf = BufferUtils.createFloatBuffer(numPrimitives * 6 * 3);
					for (int i = 0; i < numPrimitives; i++) {
						int offset = no + i * 4 * 3;
						int pos = offset;
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos++]); // A
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos++]); // B
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos++]); // C
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos]);   // D
						pos = offset;
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos]);   // A
						pos = offset + 2 * 3;
						ncBuf.put(normals[pos++]).put(normals[pos++]).put(normals[pos]);   // C
					}
				}
				if ((command & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
					if (env.getTexture() == null) {
						return;
					}
					tcBuf = BufferUtils.createByteBuffer(numPrimitives * 6 * 2);
					for (int i = 0; i < numPrimitives; i++) {
						int offset = to + i * 4 * 2;
						int pos = offset;
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos++]); // A
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos++]); // B
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos++]); // C
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos]);   // D
						pos = offset;
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos]);   // A
						pos = offset + 2 * 2;
						tcBuf.put((byte) textureCoords[pos++]).put((byte) textureCoords[pos]);   // C
					}
				} else if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					// zb3: if this is global then why recreate
					colorBuf = BufferUtils.createByteBuffer(3);
					int color = colors[co];
					colorBuf.put((byte) (color >> 16 & 0xFF));
					colorBuf.put((byte) (color >> 8 & 0xFF));
					colorBuf.put((byte) (color & 0xFF));
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = BufferUtils.createByteBuffer(numPrimitives * 6 * 3);
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
						colorBuf.put(r).put(g).put(b);
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_POINT_SPRITES: {
				if (env.getTexture() == null) {
					return;
				}
				int psParams = command & PDATA_TEXCOORD_MASK;
				if (psParams == 0) {
					return;
				}

				float[] vertex = new float[6 * 4];

				vcBuf = BufferUtils.createFloatBuffer(numPrimitives * 6 * 4);
				tcBuf = BufferUtils.createByteBuffer(numPrimitives * 6 * 2);
				int angle = 0;
				float halfWidth = 0;
				float halfHeight = 0;
				byte tx0 = 0;
				byte ty0 = 0;
				byte tx1 = 0;
				byte ty1 = 0;
				MathUtil.multiplyMM(MVP_TMP, env.projMatrix, env.viewMatrix);
				for (int i = 0; i < numPrimitives; i++) {
					vertex[4] = vertices[vo++];
					vertex[5] = vertices[vo++];
					vertex[6] = vertices[vo++];
					vertex[7] = 1.0f;
					Utils.multiplyMV(vertex, MVP_TMP);

					if (psParams != Graphics3D.PDATA_POINT_SPRITE_PARAMS_PER_CMD || i == 0) {
						float width = textureCoords[to++];
						float height = textureCoords[to++];
						angle = textureCoords[to++];
						tx0 = (byte) textureCoords[to++];
						ty0 = (byte) textureCoords[to++];
						tx1 = (byte) (textureCoords[to++] - 1);
						ty1 = (byte) (textureCoords[to++] - 1);
						switch (textureCoords[to++]) {
							case Graphics3D.POINT_SPRITE_LOCAL_SIZE | Graphics3D.POINT_SPRITE_PERSPECTIVE:
								halfWidth = width * env.projMatrix[0] * 0.5f;
								halfHeight = height * env.projMatrix[5] * 0.5f;
								break;
							case Graphics3D.POINT_SPRITE_PIXEL_SIZE | Graphics3D.POINT_SPRITE_PERSPECTIVE:
								if (env.projection <= Graphics3D.COMMAND_PARALLEL_SIZE) {
									halfWidth = width / env.width;
									halfHeight = height / env.height;
								} else {
									halfWidth = width / env.width * env.near;
									halfHeight = height / env.height * env.near;
								}
								break;
							case Graphics3D.POINT_SPRITE_LOCAL_SIZE | Graphics3D.POINT_SPRITE_NO_PERS:
								if (env.projection <= Graphics3D.COMMAND_PARALLEL_SIZE) {
									halfWidth = width * env.projMatrix[0] * 0.5f;
									halfHeight = height * env.projMatrix[5] * 0.5f;
								} else {
									float near = env.near;
									halfWidth = width * env.projMatrix[0] / near * 0.5f * vertex[3];
									halfHeight = height * env.projMatrix[5] / near * 0.5f * vertex[3];
								}
								break;
							case Graphics3D.POINT_SPRITE_PIXEL_SIZE | Graphics3D.POINT_SPRITE_NO_PERS:
								halfWidth = width / env.width * vertex[3];
								halfHeight = height / env.height * vertex[3];
								break;
							default:
								throw new IllegalArgumentException();
						}
					}

					Utils.getSpriteVertex(vertex, angle, halfWidth, halfHeight);
					vcBuf.put(vertex);

					tcBuf.put(tx0).put(ty1);
					tcBuf.put(tx0).put(ty0);
					tcBuf.put(tx1).put(ty1);
					tcBuf.put(tx1).put(ty1);
					tcBuf.put(tx0).put(ty0);
					tcBuf.put(tx1).put(ty0);
				}
				break;
			}
			default:
				throw new IllegalArgumentException();
		}
		stack.add(new RenderNode.PrimitiveNode(this, command, vcBuf, ncBuf, tcBuf, colorBuf));
	}

	public synchronized void drawFigure(FigureImpl figure) {
		bindEglContext();
		if (!backCopied && preCopy2D) {
			copy2d(true);
		}
		try {
			Model model = figure.model;
			figure.prepareBuffers();

			flushStep = 1;
			for (RenderNode r : stack) {
				r.render(this);
			}
			renderFigure(model,
					env.textures,
					env.attrs,
					env.projMatrix,
					env.viewMatrix,
					model.vertexArray,
					model.normalsArray,
					env.light,
					env.specular,
					env.toonThreshold,
					env.toonHigh,
					env.toonLow);

			flushStep = 2;
			for (RenderNode r : stack) {
				r.render(this);
				r.recycle();
			}
			renderFigure(model,
					env.textures,
					env.attrs,
					env.projMatrix,
					env.viewMatrix,
					model.vertexArray,
					model.normalsArray,
					env.light,
					env.specular,
					env.toonThreshold,
					env.toonHigh,
					env.toonLow);

			_flushGl1(false);
		} finally {
			releaseEglContext();
		}
	}

	private void bindEglContext() {
		_eglBind(eglHandle);
	}

	private void releaseEglContext() {
		_eglRelease(eglHandle);
	}

	public void reset() {
		stack.clear();
	}

	public void setTexture(TextureImpl tex) {
		if (tex == null) {
			return;
		}
		env.textures[0] = tex;
		env.textureIdx = 0;
		env.texturesLen = 1;
	}

	public void setTextureArray(TextureImpl[] tex) {
		if (tex == null) {
			return;
		}
		int len = tex.length;
		System.arraycopy(tex, 0, env.textures, 0, len);
		env.texturesLen = len;
	}

	public float[] getViewMatrix() {
		return env.viewMatrix;
	}

	public void setLight(int ambIntensity, int dirIntensity, int x, int y, int z) {
		env.light.set(ambIntensity, dirIntensity, x, y, z);		
	}

	public int getAttributes() {
		return env.attrs;
	}

	public void setToonParam(int tress, int high, int low) {
		env.toonThreshold = tress;
		env.toonHigh = high;
		env.toonLow = low;
	}

	public void setSphereTexture(TextureImpl tex) {
		if (tex != null) {
			env.specular = tex;
		}
	}

	public void setAttribute(int attrs) {
		env.attrs = attrs;
	}

	void renderPrimitive(RenderNode.PrimitiveNode node) {
		int command = node.command;
		int blend = (node.attrs & Graphics3D.ENV_ATTR_SEMI_TRANSPARENT) != 0 ? (command & Graphics3D.PATTR_BLEND_SUB) >> 4 : 0;
		if (blend != 0) {
			if (flushStep == 1) {
				return;
			}
		} else if (flushStep == 2) {
			return;
		}
		MathUtil.multiplyMM(MVP_TMP, node.projMatrix, node.viewMatrix);


		_rfPart1(flushStep == 1);
		_glDepthFuncLess();
		_glSetCullFace(false);

		applyBlending(blend);
		int numPrimitives = command >> 16 & 0xff;
		switch ((command & 0x7000000)) {
			case Graphics3D.PRIMITVE_POINTS: {
				renderMesh(node, false);
				checkGlError("renderPrimitive[PRIMITIVE_POINTS]");
				break;
			}
			case Graphics3D.PRIMITVE_LINES: {
				renderMesh(node, true);
				checkGlError("renderPrimitive[PRIMITIVE_LINES]");
				break;
			}
			case Graphics3D.PRIMITVE_TRIANGLES:
			case Graphics3D.PRIMITVE_QUADS: {
				if ((command & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
					renderMeshT(node);
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					renderMeshC(node);
				}
				break;
			}
			case Graphics3D.PRIMITVE_POINT_SPRITES: {
				Program.Sprite program = Program.sprite;
				program.use();

				((Buffer)node.vertices).rewind();
				_glVertexAttribPointerf(program.aPosition, 4, false, 4 * 4, node.vertices);
				_glEVAA(program.aPosition);

				((Buffer)node.texCoords).rewind();
				_glVertexAttribPointerb(program.aColorData, 2, false, 2, node.texCoords);
				_glEVAA(program.aColorData);

				program.setTexture(node.texture);


				_glRps1(program.uIsTransparency, (command & Graphics3D.PATTR_COLORKEY), numPrimitives);
				_glDVAA(program.aPosition);
				_glDVAA(program.aColorData);

				checkGlError("renderPrimitive[PRIMITIVE_POINT_SPRITES]");
			}
		}

	}

	private void renderMesh(RenderNode.PrimitiveNode node, boolean lines) {		
		Program.Color program = Program.color;
		program.use();
		_glVertexAttrib2f(program.aMaterial, 0, 0);
		program.bindMatrices(MVP_TMP, node.viewMatrix);

		_glVertexAttribPointerf(program.aPosition, 3, false, 3 * 4, node.vertices.rewind());
		_glEVAA(program.aPosition);

		if ((node.command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
			program.setColor(node.colors);
		} else {
			((Buffer)node.colors).rewind();
			_glVertexAttribPointerb(program.aColorData, 3, true, 3, node.colors);
			_glEVAA(program.aColorData);
		}

		if (lines) {
			_glDrawLines(0, node.vertices.capacity() / 3);
		} else {
			_glDrawPoints(0, node.vertices.capacity() / 3);
		}
		_glDVAA(program.aPosition);
		_glDVAA(program.aColorData);
	}

	public void setOrthographicScale(int scaleX, int scaleY) {
		env.projection = Graphics3D.COMMAND_PARALLEL_SCALE;
		float vw = env.width;
		float vh = env.height;
		float w = vw * (4096.0f / scaleX);
		float h = vh * (4096.0f / scaleY);

		float sx = 2.0f / w;
		float sy = 2.0f / h;
		float sz = 1.0f / 65536.0f;
		float tx = 2.0f * env.centerX / vw - 1.0f;
		float ty = 2.0f * env.centerY / vh - 1.0f;
		float tz = 0.0f;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] = 0.0f; pm[12] =   tx;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] = 0.0f; pm[13] =   ty;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;
	}

	public void setOrthographicW(int w) {
		if (w <= 0) {
			return;
		}
		env.projection = Graphics3D.COMMAND_PARALLEL_SIZE;
		float vw = env.width;
		float vh = env.height;
		float sx = 2.0f / w;
		float sy = sx * (vw / vh);
		float sz = 1.0f / 65536.0f;
		float tx = 2.0f * env.centerX / vw - 1.0f;
		float ty = 2.0f * env.centerY / vh - 1.0f;
		float tz = 0.0f;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] = 0.0f; pm[12] =   tx;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] = 0.0f; pm[13] =   ty;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;
	}

	public void setOrthographicWH(int w, int h) {
		if (w <= 0 || h <= 0) {
			return;
		}
		env.projection = Graphics3D.COMMAND_PARALLEL_SIZE;
		float sx = 2.0f / w;
		float sy = 2.0f / h;
		float sz = 1.0f / 65536.0f;
		float tx = 2.0f * env.centerX / env.width - 1.0f;
		float ty = 2.0f * env.centerY / env.height - 1.0f;
		float tz = 0.0f;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] = 0.0f; pm[12] =   tx;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] = 0.0f; pm[13] =   ty;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;
	}

	public void setPerspectiveFov(int near, int far, int angle) {
		if (near <= 0 || far <= 0 || near >= far) {
			return;
		}
		angle = MathUtil.clamp(angle, 2, 2046);
		env.projection = Graphics3D.COMMAND_PERSPECTIVE_FOV;
		env.near = near;
		float rd = 1.0f / (near - far);
		float sx = 1.0f / (float) Math.tan(angle * MathUtil.TO_FLOAT * Math.PI);
		float vw = env.width;
		float vh = env.height;
		float sy = sx * (vw / vh);
		float sz = -(far + near) * rd;
		float tx = 2.0f * env.centerX / vw - 1.0f;
		float ty = 2.0f * env.centerY / vh - 1.0f;
		float tz = 2.0f * far * near * rd;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] =   tx; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] =   ty; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 1.0f; pm[15] = 0.0f;
	}

	public void setPerspectiveW(int near, int far, int w) {
		if (near <= 0 || far <= 0 || near >= far || w <= 0) {
			return;
		}
		env.projection = Graphics3D.COMMAND_PERSPECTIVE_WH;
		env.near = near;
		float vw = env.width;
		float vh = env.height;

		float rd = 1.0f / (near - far);
		float sx = 2.0f * near / (w * MathUtil.TO_FLOAT);
		float sy = sx * (vw / vh);
		float sz = -(near + far) * rd;
		float tx = 2.0f * env.centerX / vw - 1.0f;
		float ty = 2.0f * env.centerY / vh - 1.0f;
		float tz = 2.0f * far * near * rd;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] =   tx; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] =   ty; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 1.0f; pm[15] = 0.0f;
	}

	public void setPerspectiveWH(float near, float far, int w, int h) {
		if (near <= 0 || far <= 0 || near >= far || w == 0 || h == 0) {
			return;
		}
		env.projection = Graphics3D.COMMAND_PERSPECTIVE_WH;
		env.near = near;
		float width = w * MathUtil.TO_FLOAT;
		float height = h * MathUtil.TO_FLOAT;

		float rd = 1.0f / (near - far);
		float sx = 2.0f * near / width;
		float sy = 2.0f * near / height;
		float sz = -(near + far) * rd;
		float tx = 2.0f * env.centerX / env.width - 1.0f;
		float ty = 2.0f * env.centerY / env.height - 1.0f;
		float tz = 2.0f * far * near * rd;

		float[] pm = env.projMatrix;
		pm[0] =   sx; pm[4] = 0.0f; pm[ 8] =   tx; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] =   sy; pm[ 9] =   ty; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] =   sz; pm[14] =   tz;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 1.0f; pm[15] = 0.0f;
	}

	public void setViewTransArray(float[] matrices) {
		env.matrices = matrices;
	}

	private void selectAffineTrans(int n) {
		float[] matrices = env.matrices;
		if (matrices != null && matrices.length >= (n + 1) * 12) {
			System.arraycopy(matrices, n * 12, env.viewMatrix, 0, 12);
		}
	}

	public void setCenter(int cx, int cy) {
		env.centerX = cx;
		env.centerY = cy;
	}

	public void setClearColor(int color) {
		clearColor = color;
	}

	static class Environment {
		final TextureImpl[] textures = new TextureImpl[16];
		final Light light = new Light();
		final float[] viewMatrix = new float[12];
		final float[] projMatrix = new float[16];

		int projection;
		float near;
		float[] matrices;
		int centerX;
		int centerY;
		int width;
		int height;
		int toonThreshold;
		int toonHigh;
		int toonLow;
		int attrs;
		int textureIdx;
		int texturesLen;
		TextureImpl specular;

		Environment() {}

		TextureImpl getTexture() {
			if (textureIdx < 0 || textureIdx >= texturesLen) {
				return null;
			}
			return textures[textureIdx];
		}
	}

	private static final class InstanceHolder {
		static final Render instance = new Render();
	}
}
