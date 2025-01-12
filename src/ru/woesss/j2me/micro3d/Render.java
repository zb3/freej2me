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

import static pl.zb3.freej2me.bridge.gles2.GLES2.Constants.*;

import java.awt.Rectangle;

import com.mascotcapsule.micro3d.v3.Graphics3D;

import pl.zb3.freej2me.bridge.gles2.BufferHelper;
import pl.zb3.freej2me.bridge.gles2.GLES2;
import pl.zb3.freej2me.bridge.gles2.TextureResourceManager;

import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.microedition.lcdui.Graphics;

import ru.woesss.j2me.micro3d.RenderNode.FigureNode;

/*
 * zb3: currently this only allows 1 surface at a time
 * is that a performance issue? so far no game seems to use more than one surface
 */

public class Render {
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
	private boolean backCopied; // this is ok - the copy is not done on bind but on first draw
	private final LinkedList<RenderNode> stack = new LinkedList<>();
	private int flushStep;

	private int[] bufHandles = {-1, -1, -1};
	private int clearColor;
	private TextureImpl targetTexture;

	private int[] pixelBuffer;
	private byte[] pixelBufferByte;

	private BufferHelper bufferHelper = new BufferHelper();
	private boolean isBound = false;

	public final TextureResourceManager textureResourceManager = new TextureResourceManager();

	/**
	 * Utility method for debugging OpenGL calls.
	 * <p>
	 * If the operation is not successful, the check throws an error.
	 *
	 * @param glOperation - Name of the OpenGL call to check.
	 */
	static void checkGlError(String glOperation) {
		// TODO: can't use this with angle, we auto check but this isn't useful anyway

	}

	public static Render getRender() {
		return InstanceHolder.instance;
	}


	public synchronized void bind(Graphics graphics) {
		this.targetGraphics = graphics;

		BufferedImage canvas = ((PlatformGraphics)graphics).getCanvas();

		GLES2.ensure(); // for webgl, here we'll need to put antialias

		final int clipX = graphics.getClipX() + graphics.getTranslateX();
		final int clipY = graphics.getClipY() + graphics.getTranslateY();
		final int width = graphics.getClipWidth();
		final int height = graphics.getClipHeight();

		// currently this only allows 1 surface at a time, but this is abstracted here
		GLES2.setSurface(width, height);
		GLES2.bind();
		isBound = true;

		//System.out.format("log: translate xy %d %d %d %d\n", graphics.getClipX(), graphics.getClipY(), graphics.getTranslateX(), graphics.getTranslateY());

		if (env.width != width || env.height != height) {
			GLES2.viewport(0, 0, width, height);
    		GLES2.clearColor(0, 0, 0, 1);
    		GLES2.clear(GL_COLOR_BUFFER_BIT);

			Program.create();
			env.width = width;
			env.height = height;

			pixelBuffer = new int[width * height];
			pixelBufferByte = new byte[width * height * 4];
		}


		// func
		gClip.setBounds(clipX, clipY, width, height);

		// clip.setBounds(clipX, clipY, width, height);
		// note that one would require enabling scissor
		// glEnable(GL_SCISSOR_TEST);
		// glScissor(clipX, clipY, width, height);

		clip.setBounds(0, 0, width, height);
		GLES2.disable(GL_SCISSOR_TEST);
		GLES2.clear(GL_DEPTH_BUFFER_BIT);

		// func
		backCopied = false;
	}

	public synchronized void bind(TextureImpl tex) {
		targetTexture = tex;
		int width = tex.getWidth();
		int height = tex.getHeight();

		GLES2.ensure();
		GLES2.setSurface(width, height);
		GLES2.bind();
		isBound = true;

		if (env.width != width || env.height != height) {
			GLES2.viewport(0, 0, width, height);
			// func
			Program.create();
			env.width = width;
			env.height = height;
		}

		Rectangle clip = this.clip;
		clip.setBounds(0, 0, width, height);
		gClip.setBounds(0, 0, width, height);
		GLES2.disable(GL_SCISSOR_TEST);

		GLES2.clearColor(
				((clearColor >> 16) & 0xff) / 255.0f,
				((clearColor >> 8) & 0xff) / 255.0f,
				(clearColor & 0xff) / 255.0f,
				1.0f);
		GLES2.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		backCopied = false;
	}

	private static void applyBlending(int blendMode) {
		switch (blendMode) {
        	case Model.Polygon.BLEND_HALF:
				GLES2.enable(GL_BLEND);
				GLES2.blendColor(0.5f, 0.5f, 0.5f, 1.0f);
				GLES2.blendEquation(GL_FUNC_ADD);
        		GLES2.blendFunc(GL_CONSTANT_COLOR, GL_CONSTANT_COLOR);
        		break;
        	case Model.Polygon.BLEND_ADD:
				GLES2.enable(GL_BLEND);
				GLES2.blendEquation(GL_FUNC_ADD);
        		GLES2.blendFunc(GL_ONE, GL_ONE);
        		break;
        	case Model.Polygon.BLEND_SUB:
				GLES2.enable(GL_BLEND);
				GLES2.blendEquation(GL_FUNC_REVERSE_SUBTRACT);
				GLES2.blendFuncSeparate(GL_ONE, GL_ONE, GL_ZERO, GL_ONE);
        		break;
        	default:
        		GLES2.disable(GL_BLEND);
        }
	}

	private void copyBackground() {
		if (targetTexture != null) {// render to texture
			return;
		}

		// blits the 2d image into the gl buffer so we can do blending with it

		boolean filter = Boolean.getBoolean("micro3d.v3.background.filter");

		if (bgTextureId[0] == -1) {
			bgTextureId[0] = GLES2.createTexture();
			GLES2.activeTexture(GL_TEXTURE1);
			GLES2.bindTexture(GL_TEXTURE_2D, bgTextureId[0]);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		} else {
			GLES2.activeTexture(GL_TEXTURE1);
			GLES2.bindTexture(GL_TEXTURE_2D, bgTextureId[0]);
		}

		BufferedImage targetImage = ((PlatformGraphics)targetGraphics).getCanvas();

		// gets "srgb" with pre?
		// instead of null we might pass the array
		// zb3: this also assumes one-texture

		//int[] pixels = targetImage.getRGB(0, 0, env.width, env.height, null, 0, env.width);
		int[] pixels = targetImage.getRGB(gClip.x, gClip.y, env.width, env.height, null, 0, env.width);

		// not very optimal.. but in m3g we do the same when we copy graphics to image2d
		byte[] pixelsRGBA = new byte[pixels.length * 4];

		for (int t=0; t<pixels.length; t++) {
			pixelsRGBA[t<<2] = (byte) (pixels[t] >> 16 & 0xff);
			pixelsRGBA[(t<<2)+1] = (byte) (pixels[t] >> 8 & 0xff);
			pixelsRGBA[(t<<2)+2] = (byte) (pixels[t] & 0xff);
			pixelsRGBA[(t<<2)+3] = (byte) (pixels[t] >> 24 & 0xff);
		}

		GLES2.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, env.width, env.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelsRGBA);

		final Program.Simple program = Program.simple;
		program.use();

		bufferHelper.vertexFloatAttribPointer(program.aPosition, 2, false, 0, 4, new float[] {
			-1.0f, -1.0f,
			1.0f, -1.0f,
			-1.0f,  1.0f,
			1.0f,  1.0f
		});

		bufferHelper.vertexFloatAttribPointer(program.aTexture, 2, false, 0, 4, new float[] {
			0.0f, 0.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f
		});

		GLES2.enableVertexAttribArray(program.aPosition);
		GLES2.enableVertexAttribArray(program.aTexture);

		GLES2.disable(GL_BLEND);

		GLES2.disable(GL_DEPTH_TEST);
		GLES2.depthMask(false);
		GLES2.drawArrays(GL_TRIANGLE_STRIP, 0, 4);

		GLES2.disableVertexAttribArray(program.aPosition);
		GLES2.disableVertexAttribArray(program.aTexture);

		GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);
		backCopied = true;
	}

	void renderFigure(Model model,
					  TextureImpl[] textures,
					  int attrs,
					  float[] projMatrix,
					  float[] viewMatrix,
					  float[] vertices,
					  float[] normals,
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

		// zb3: controversial, possibly incorrect for MCV3
		// but "two pass" renders must write to zbuffer
		// so we know what opaque polygons not to overwrite
		// yet it appears semc doesn't have this 2 pass render?
		GLES2.enable(GL_DEPTH_TEST);
		GLES2.depthMask(flushStep == 1);

		try {
			boolean isLight = (attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && normals != null;

			if (bufHandles[0] == -1) {
				bufHandles[0] = GLES2.createBuffer();
				bufHandles[1] = GLES2.createBuffer();
				bufHandles[2] = GLES2.createBuffer();
			}

			GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
			GLES2.bufferData(GL_ARRAY_BUFFER, vertices.length * 4, GL_STREAM_DRAW);
			GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, vertices.length * 4, vertices);

			GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
			GLES2.bufferData(GL_ARRAY_BUFFER, model.texCoordArray.length, GL_STREAM_DRAW);
			GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, model.texCoordArray.length, model.texCoordArray);

			if (isLight) {
				GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
				GLES2.bufferData(GL_ARRAY_BUFFER, normals.length * 4, GL_STREAM_DRAW);
				GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, normals.length * 4, normals);
			}

			if (model.hasPolyT) {
				final Program.Tex program = Program.tex;
				program.use();


				GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
				GLES2.enableVertexAttribArray(program.aPosition);
				GLES2.vertexAttribPointer(program.aPosition, 3, GL_FLOAT, false, 3 * 4, 0);

				GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
				GLES2.enableVertexAttribArray(program.aColorData);
				GLES2.vertexAttribPointer(program.aColorData, 2, GL_UNSIGNED_BYTE, false, 5, 0);
				GLES2.enableVertexAttribArray(program.aMaterial);
				GLES2.vertexAttribPointer(program.aMaterial, 3, GL_UNSIGNED_BYTE, false, 5, 2);

				if (isLight) {
					GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
					GLES2.enableVertexAttribArray(program.aNormal);
					GLES2.vertexAttribPointer(program.aNormal, 3, GL_FLOAT, false, 3 * 4, 0);
				}

				if (isLight) {
					program.setToonShading(attrs, toonThreshold, toonHigh, toonLow);
					program.setLight(light);
					program.setSphere((attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) == 0 ? null : specular);
				} else {
					GLES2.disableVertexAttribArray(program.aNormal);
					program.setLight(null);
				}

				program.bindMatrices(MVP_TMP, viewMatrix);
				// Draw triangles
				renderModel(textures, model, isTransparency);
				GLES2.disableVertexAttribArray(program.aPosition);
				GLES2.disableVertexAttribArray(program.aColorData);
				GLES2.disableVertexAttribArray(program.aMaterial);
				GLES2.disableVertexAttribArray(program.aNormal);
				GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);
			}

			if (model.hasPolyC) {
				final Program.Color program = Program.color;
				program.use();

				int offset = model.numVerticesPolyT;

				GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
				GLES2.enableVertexAttribArray(program.aPosition);
				GLES2.vertexAttribPointer(program.aPosition, 3, GL_FLOAT, false, 3 * 4, 3 * 4 * offset);

				GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
				GLES2.vertexAttribPointer(program.aColorData, 3, GL_UNSIGNED_BYTE, true, 5, 5 * offset);
				GLES2.enableVertexAttribArray(program.aColorData);
				GLES2.enableVertexAttribArray(program.aMaterial);
				GLES2.vertexAttribPointer(program.aMaterial, 2, GL_UNSIGNED_BYTE, false, 5, 5 * offset + 3);

				if (isLight) {
					GLES2.bindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
					GLES2.vertexAttribPointer(program.aNormal, 3, GL_FLOAT, false, 3 * 4, 3 * 4 * offset);
					GLES2.enableVertexAttribArray(program.aNormal);
				}

				if (isLight) {
					program.setLight(light);
					program.setSphere((attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) == 0 ? null : specular);
					program.setToonShading(attrs, toonThreshold, toonHigh, toonLow);
				} else {
					GLES2.disableVertexAttribArray(program.aNormal);
					program.setLight(null);
				}
				program.bindMatrices(MVP_TMP, viewMatrix);
				renderModel(model, isTransparency);
				GLES2.disableVertexAttribArray(program.aPosition);
				GLES2.disableVertexAttribArray(program.aColorData);
				GLES2.disableVertexAttribArray(program.aMaterial);
				GLES2.disableVertexAttribArray(program.aNormal);
			}
		} finally {
			GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);
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
			GLES2.disable(GL_BLEND);
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
					GLES2.enable(GL_CULL_FACE);
					GLES2.drawArrays(GL_TRIANGLES, pos, cnt);
					pos += cnt;
				}
				cnt = lens[1];
				if (cnt > 0) {
					GLES2.disable(GL_CULL_FACE);
					GLES2.drawArrays(GL_TRIANGLES, pos, cnt);
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
			GLES2.disable(GL_BLEND);
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
				GLES2.enable(GL_CULL_FACE);
				GLES2.drawArrays(GL_TRIANGLES, pos, cnt);
				pos += cnt;
			}
			cnt = mesh[1];
			if (cnt > 0) {
				GLES2.disable(GL_CULL_FACE);
				GLES2.drawArrays(GL_TRIANGLES, pos, cnt);
				pos += cnt;
			}
			blendMode++;
		}
		checkGlError("glDrawArrays");
	}

	public synchronized void release() {
		stack.clear();
		if (targetTexture != null) {
			// zb3: umm this is upside down..
			GLES2.readPixels(0, 0, 256, 256, targetTexture.image.getRaster());
			targetTexture = null;
		} else if (targetGraphics != null) {
			// zb3: upside down so is everything else...
			GLES2.readPixels(0, 0, gClip.width, gClip.height, pixelBufferByte);


			for (int row = 0; row < gClip.height; row++) {
				int sourceOffset = (row) * gClip.width * 4; // for some reason we don't flip (gClip.height - 1 - row) * gClip.width * 4;
				int destOffset = row * gClip.width;

				for (int col = 0; col < gClip.width; col++) {
					int srcIndex = sourceOffset + (col * 4);

					// with alpha? rly?
					pixelBuffer[destOffset + col] = ((pixelBufferByte[srcIndex+3] & 0xFF) << 24) |
													((pixelBufferByte[srcIndex] & 0xFF) << 16) |
												((pixelBufferByte[srcIndex + 1] & 0xFF) << 8) |
												(pixelBufferByte[srcIndex + 2] & 0xFF);
				}
			}

			// zb3: ee.. setRGB here means we overwrite alpha.. ??
			// in m3g we didn't so we somehow used inner "raster"
			// we should possibly conduct some experiments and unify this alpha model

			((PlatformGraphics)targetGraphics).getCanvas().setRGB(gClip.x, gClip.y, gClip.width, gClip.height, pixelBuffer, 0, gClip.width);

			/*try {
			Thread.sleep(2000);
			} catch(InterruptedException e){}
			*/
			targetGraphics = null;
		}

		textureResourceManager.deleteUnusedTextures();

		GLES2.release();
		isBound = false;
	}

	private synchronized void ensureBackgroundCopied() {
		if (!backCopied) {
			copyBackground();
		}
	}

	public synchronized void flush() {
		// don't return early if stack is empty - bg still needs to be copied
		try {
			ensureBackgroundCopied();
			flushStep = 1;
			for (RenderNode r : stack) {
				r.render(this);
			}
			flushStep = 2;
			for (RenderNode r : stack) {
				r.render(this);
				r.recycle();
			}

			GLES2.disable(GL_BLEND);
			GLES2.depthMask(true);
			GLES2.clear(GL_DEPTH_BUFFER_BIT);
		} finally {
			stack.clear();
		}
	}

	private void renderMeshC(RenderNode.PrimitiveNode node) {
		int command = node.command;
		Program.Color program = Program.color;
		program.use();
		if ((node.attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && (command & Graphics3D.PATTR_LIGHTING) != 0 && node.normals != null) {
			TextureImpl sphere = node.specular;
			if ((node.attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) != 0 && (command & Graphics3D.PATTR_SPHERE_MAP) != 0 && sphere != null) {
				GLES2.vertexAttrib2f(program.aMaterial, 1, 1);
				program.setSphere(sphere);
			} else {
				GLES2.vertexAttrib2f(program.aMaterial, 1, 0);
				program.setSphere(null);
			}
			program.setLight(node.light);
			program.setToonShading(node.attrs, node.toonThreshold, node.toonHigh, node.toonLow);

			bufferHelper.vertexFloatAttribPointer(program.aNormal, 3, false, 0, node.normals.length / 3, node.normals);

			GLES2.enableVertexAttribArray(program.aNormal);
		} else {
			GLES2.vertexAttrib2f(program.aMaterial, 0, 0);
			program.setLight(null);
			GLES2.disableVertexAttribArray(program.aNormal);
		}

		program.bindMatrices(MVP_TMP, node.viewMatrix);

		bufferHelper.vertexFloatAttribPointer(program.aPosition, 3, false, 0, node.vertices.length / 3, node.vertices);

		GLES2.enableVertexAttribArray(program.aPosition);

		if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
			program.setColor(node.colors);
		} else {
			bufferHelper.vertexByteAttribPointer(program.aColorData, 3, true, true, 0, node.colors.length / 3, node.colors);
			GLES2.enableVertexAttribArray(program.aColorData);
		}

		GLES2.drawArrays(GL_TRIANGLES, 0, node.vertices.length / 3);

		GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

		GLES2.disableVertexAttribArray(program.aPosition);
		GLES2.disableVertexAttribArray(program.aColorData);
		GLES2.disableVertexAttribArray(program.aNormal);
		checkGlError("renderMeshC");
	}

	private void renderMeshT(RenderNode.PrimitiveNode node) {
		int command = node.command;
		Program.Tex program = Program.tex;
		program.use();
		if ((node.attrs & Graphics3D.ENV_ATTR_LIGHTING) != 0 && (command & Graphics3D.PATTR_LIGHTING) != 0 && node.normals != null) {
			TextureImpl sphere = node.specular;
			if ((node.attrs & Graphics3D.ENV_ATTR_SPHERE_MAP) != 0 && (command & Graphics3D.PATTR_SPHERE_MAP) != 0 && sphere != null) {
				GLES2.vertexAttrib3f(program.aMaterial, 1, 1, command & Graphics3D.PATTR_COLORKEY);
				program.setSphere(sphere);
			} else {
				GLES2.vertexAttrib3f(program.aMaterial, 1, 0, command & Graphics3D.PATTR_COLORKEY);
				program.setSphere(null);
			}
			program.setLight(node.light);
			program.setToonShading(node.attrs, node.toonThreshold, node.toonHigh, node.toonLow);

			bufferHelper.vertexFloatAttribPointer(program.aNormal, 3, false, 0, node.normals.length / 3, node.normals);
			GLES2.enableVertexAttribArray(program.aNormal);
		} else {
			GLES2.vertexAttrib3f(program.aMaterial, 0, 0, command & Graphics3D.PATTR_COLORKEY);
			program.setLight(null);
			GLES2.disableVertexAttribArray(program.aNormal);
		}

		program.bindMatrices(MVP_TMP, node.viewMatrix);

		bufferHelper.vertexFloatAttribPointer(program.aPosition, 3, false, 0, node.vertices.length / 3, node.vertices);
		GLES2.enableVertexAttribArray(program.aPosition);

		bufferHelper.vertexByteAttribPointer(program.aColorData, 2, true, false, 0, node.texCoords.length / 2, node.texCoords);
		GLES2.enableVertexAttribArray(program.aColorData);

		program.setTex(node.texture);

		GLES2.drawArrays(GL_TRIANGLES, 0, node.vertices.length / 3);

		GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

		GLES2.disableVertexAttribArray(program.aPosition);
		GLES2.disableVertexAttribArray(program.aColorData);
		GLES2.disableVertexAttribArray(program.aNormal);
		checkGlError("renderMeshT");
	}

	public void drawCommandList(int[] cmds) {
		if (Graphics3D.COMMAND_LIST_VERSION_1_0 != cmds[0]) {
			throw new IllegalArgumentException("Unsupported command list version: " + cmds[0]);
		}

		// sems: even empty dcl copies the bg
		ensureBackgroundCopied();

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
					// defined in left top right bottom coordinates
					clip.x = cmds[i++];
					clip.y = cmds[i++];
					clip.width = cmds[i++] - clip.x;
					clip.height = cmds[i++] - clip.y;
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

	private synchronized void updateClip() {
		Rectangle clip = this.clip;

		if (clip.x == 0 && clip.y == 0 && clip.width == env.width && clip.width == env.height) {
			GLES2.disable(GL_SCISSOR_TEST);
		} else {
			GLES2.enable(GL_SCISSOR_TEST);
			GLES2.scissor(clip.x, clip.y, clip.width, clip.height);
		}
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

		float[] vcBuf = null;
		float[] ncBuf = null;
		byte[] tcBuf = null;
		byte[] colorBuf = null;

		switch ((command & 0x7000000)) {
			case Graphics3D.PRIMITVE_POINTS: {
				int vcLen = numPrimitives * 3;
				vcBuf = new float[vcLen];
				for (int i = 0; i < vcLen; i++) { // possible arraycopy
					vcBuf[i] = vertices[vo++];
				}

				if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = new byte[3];
					int color = colors[co];
					colorBuf[0] = (byte) (color >> 16 & 0xFF);
					colorBuf[1] = (byte) (color >> 8 & 0xFF);
					colorBuf[2] = (byte) (color & 0xFF);
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = new byte[vcLen];
					int cbPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						colorBuf[cbPos++] = (byte) (color >> 16 & 0xFF);
						colorBuf[cbPos++] = (byte) (color >> 8 & 0xFF);
						colorBuf[cbPos++] = (byte) (color & 0xFF);
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_LINES: {
				int vcLen = numPrimitives * 2 * 3;
				vcBuf = new float[vcLen];
				for (int i = 0; i < vcLen; i++) { // possible arraycopy
					vcBuf[i] = vertices[vo++];
				}

				if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = new byte[3];
					int color = colors[co];
					colorBuf[0] = (byte) (color >> 16 & 0xFF);
					colorBuf[1] = (byte) (color >> 8 & 0xFF);
					colorBuf[2] = (byte) (color & 0xFF);
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = new byte[vcLen];
					int cbPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_TRIANGLES: {
				int vcLen = numPrimitives * 3 * 3;
				vcBuf = new float[vcLen];
				for (int i = 0; i < vcLen; i++) { // possible arraycopy
					vcBuf[i] = vertices[vo++];
				}

				if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_FACE) {
					ncBuf = new float[vcLen];
					int ncPos = 0;
					for (int end = no + numPrimitives * 3; no < end; ) {
						float x = normals[no++];
						float y = normals[no++];
						float z = normals[no++];
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
					}
				} else if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_VERTEX) {
					ncBuf = new float[vcLen];
					int ncPos = 0;
					for (int end = no + vcLen; no < end; ) {
						ncBuf[ncPos++] = normals[no++];
					}
				}
				if ((command & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
					if (env.getTexture() == null) {
						return;
					}
					int tcLen = numPrimitives * 3 * 2;
					tcBuf = new byte[tcLen];
					for (int i = 0; i < tcLen; i++) {
						tcBuf[i] = (byte) textureCoords[to++];
					}
				} else if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					colorBuf = new byte[3];
					int color = colors[co];
					colorBuf[0] = (byte) (color >> 16 & 0xFF);
					colorBuf[1] = (byte) (color >> 8 & 0xFF);
					colorBuf[2] = (byte) (color & 0xFF);
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = new byte[vcLen];
					int cbPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
					}
				} else {
					return;
				}
				break;
			}
			case Graphics3D.PRIMITVE_QUADS: {
				vcBuf = new float[numPrimitives * 6 * 3];
				int vcPos = 0;
				for (int i = 0; i < numPrimitives; i++) {
					int offset = vo + i * 4 * 3;
					int pos = offset;
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; // A
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; // B
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; // C
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos];   // D
					pos = offset;
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos];   // A
					pos = offset + 2 * 3;
					vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos++]; vcBuf[vcPos++] = vertices[pos];   // C
				}
				if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_FACE) {
					ncBuf = new float[numPrimitives * 6 * 3];
					int ncPos = 0;
					for (int end = no + numPrimitives * 3; no < end; ) {
						float x = normals[no++];
						float y = normals[no++];
						float z = normals[no++];
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
						ncBuf[ncPos++] = x; ncBuf[ncPos++] = y; ncBuf[ncPos++] = z;
					}
				} else if ((command & PDATA_NORMAL_MASK) == Graphics3D.PDATA_NORMAL_PER_VERTEX) {
					ncBuf = new float[numPrimitives * 6 * 3];
					int ncPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int offset = no + i * 4 * 3;
						int pos = offset;
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; // A
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; // B
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; // C
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos];   // D
						pos = offset;
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos];   // A
						pos = offset + 2 * 3;
						ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos++]; ncBuf[ncPos++] = normals[pos];   // C
					}
				}
				if ((command & PDATA_TEXCOORD_MASK) == Graphics3D.PDATA_TEXURE_COORD) {
					if (env.getTexture() == null) {
						return;
					}
					tcBuf = new byte[numPrimitives * 6 * 2];
					int tcPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int offset = to + i * 4 * 2;
						int pos = offset;
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos++]; // A
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos++]; // B
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos++]; // C
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos];   // D
						pos = offset;
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos];   // A
						pos = offset + 2 * 2;
						tcBuf[tcPos++] = (byte) textureCoords[pos++]; tcBuf[tcPos++] = (byte) textureCoords[pos];   // C
					}
				} else if ((command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
					// zb3: if this is global then why recreate
					colorBuf = new byte[3];
					int color = colors[co];
					colorBuf[0] = (byte) (color >> 16 & 0xFF);
					colorBuf[1] = (byte) (color >> 8 & 0xFF);
					colorBuf[2] = (byte) (color & 0xFF);
				} else if ((command & PDATA_COLOR_MASK) != Graphics3D.PDATA_COLOR_NONE) {
					colorBuf = new byte[numPrimitives * 6 * 3];
					int cbPos = 0;
					for (int i = 0; i < numPrimitives; i++) {
						int color = colors[co++];
						byte r = (byte) (color >> 16 & 0xFF);
						byte g = (byte) (color >> 8 & 0xFF);
						byte b = (byte) (color & 0xFF);
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
						colorBuf[cbPos++] = r; colorBuf[cbPos++] = g; colorBuf[cbPos++] = b;
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

				vcBuf = new float[numPrimitives * 6 * 4];
				tcBuf = new byte[numPrimitives * 6 * 2];
				int vcPos = 0;
				int tcPos = 0;
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
					System.arraycopy(vertex, 0, vcBuf, vcPos, 6*4);
					vcPos += 6*4;

					tcBuf[tcPos++] = tx0; tcBuf[tcPos++] = ty1;
					tcBuf[tcPos++] = tx0; tcBuf[tcPos++] = ty0;
					tcBuf[tcPos++] = tx1; tcBuf[tcPos++] = ty1;
					tcBuf[tcPos++] = tx1; tcBuf[tcPos++] = ty1;
					tcBuf[tcPos++] = tx0; tcBuf[tcPos++] = ty0;
					tcBuf[tcPos++] = tx1; tcBuf[tcPos++] = ty0;
				}
				break;
			}
			default:
				throw new IllegalArgumentException();
		}
		stack.add(new RenderNode.PrimitiveNode(this, command, vcBuf, ncBuf, tcBuf, colorBuf));
	}

	public synchronized void drawFigure(FigureImpl figure) {
		// zb3: I mean.. for MC it seems to bypass depth buffer
		flush();

		Model model = figure.model;
		figure.prepareBuffers();

		flushStep = 1;

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

		flush();
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

		// zb3: controversial, possibly incorrect for MCV3
		// but "two pass" renders must write to zbuffer
		// so we know what opaque polygons not to overwrite
		// yet it appears semc doesn't have this 2 pass render?
		GLES2.enable(GL_DEPTH_TEST);
		GLES2.depthMask(flushStep == 1);

		GLES2.depthFunc(GL_LEQUAL); // hmm, this might conflict with sorting
		GLES2.disable(GL_CULL_FACE);

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

				bufferHelper.vertexFloatAttribPointer(program.aPosition, 4, false, 0, node.vertices.length / 4, node.vertices);
				GLES2.enableVertexAttribArray(program.aPosition);

				bufferHelper.vertexByteAttribPointer(program.aColorData, 2, true, false, 0, node.texCoords.length / 2, node.texCoords);
				GLES2.enableVertexAttribArray(program.aColorData);

				program.setTexture(node.texture);

				GLES2.uniform1i(program.uIsTransparency, (command & Graphics3D.PATTR_COLORKEY));
				GLES2.drawArrays(GL_TRIANGLES, 0, numPrimitives * 6);

				GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

				GLES2.disableVertexAttribArray(program.aPosition);
				GLES2.disableVertexAttribArray(program.aColorData);

				checkGlError("renderPrimitive[PRIMITIVE_POINT_SPRITES]");
			}
		}

	}

	private void renderMesh(RenderNode.PrimitiveNode node, boolean lines) {
		Program.Color program = Program.color;
		program.use();
		GLES2.vertexAttrib2f(program.aMaterial, 0, 0);
		program.bindMatrices(MVP_TMP, node.viewMatrix);

		bufferHelper.vertexFloatAttribPointer(program.aPosition, 3, false, 0, node.vertices.length / 3, node.vertices);
		GLES2.enableVertexAttribArray(program.aPosition);

		if ((node.command & PDATA_COLOR_MASK) == Graphics3D.PDATA_COLOR_PER_COMMAND) {
			program.setColor(node.colors);
		} else {
			bufferHelper.vertexByteAttribPointer(program.aColorData, 3, true, true, 0, node.colors.length / 3, node.colors);
			GLES2.enableVertexAttribArray(program.aColorData);
		}

		GLES2.drawArrays(lines ? GL_LINES : GL_POINTS, 0, node.vertices.length / 3);

		GLES2.disableVertexAttribArray(program.aPosition);
		GLES2.disableVertexAttribArray(program.aColorData);
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
