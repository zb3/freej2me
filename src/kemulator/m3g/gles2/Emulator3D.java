package kemulator.m3g.gles2;

import java.util.*;

import org.recompile.mobile.PlatformGraphics;

import kemulator.m3g.impl.*;
import kemulator.m3g.utils.*;
import pl.zb3.freej2me.bridge.gles2.BufferHelper;
import pl.zb3.freej2me.bridge.gles2.GLES2;
import pl.zb3.freej2me.bridge.gles2.TextureResourceManager;

import static pl.zb3.freej2me.bridge.gles2.GLES2.Constants.*;

import java.awt.image.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;

public final class Emulator3D {
	private static Emulator3D instance;
	private final RenderPipe renderPipe;
	private Object target;
	private boolean depthBufferEnabled;
	private int hints;
	private static int targetWidth;
	private static int targetHeight;
	private static byte[] buffer;
	private static BufferedImage awtBufferImage;
	private static final Hashtable<String, Object> properties = new Hashtable<>();
	private float depthRangeNear;
	private float depthRangeFar;
	private int viewportX;
	private int viewportY;
	private int viewportWidth;
	private int viewportHeight;

	public final TextureResourceManager textureResourceManager = new TextureResourceManager();

	public static final int MaxViewportWidth = 2048;
	public static final int MaxViewportHeight = 2048;
	public static final int MaxViewportDimension = 2048;
	public static final int NumTextureUnits = 10;
	public static final int MaxTextureDimension = 2048;
	public static final int MaxSpriteCropDimension = 1024;
	public static final int MaxLights = 8;
	public static final int MaxTransformsPerVertex = 4;
	private boolean exiting;

	private SpriteProgram spriteProgram;
	private BackgroundProgram backgroundProgram;
	private MeshProgram meshProgram;
	private ShaderProgram currentProgram;

	private BufferHelper bufferHelper;

	private boolean flatShade = false;

	private boolean noTextureFiltering = Boolean.getBoolean("freej2me.no-texture-filtering");

	private Emulator3D() {
		instance = this;
		properties.put("supportAntialiasing", Boolean.TRUE);
		properties.put("supportTrueColor", Boolean.TRUE);
		properties.put("supportDithering", Boolean.TRUE);
		properties.put("supportMipmapping", !noTextureFiltering);
		properties.put("supportPerspectiveCorrection", Boolean.TRUE);
		properties.put("supportLocalCameraLighting", Boolean.TRUE);
		properties.put("maxLights", MaxLights);
		properties.put("maxViewportWidth", MaxViewportWidth);
		properties.put("maxViewportHeight", MaxViewportHeight);
		properties.put("maxViewportDimension", Math.min(MaxViewportDimension, MaxViewportHeight));
		properties.put("maxTextureDimension", MaxTextureDimension);
		properties.put("maxSpriteCropDimension", MaxSpriteCropDimension);
		properties.put("maxTransformsPerVertex", MaxTransformsPerVertex);
		properties.put("numTextureUnits", NumTextureUnits);
		properties.put("coreID", "@KEmulator LWJ-OpenGL-M3G @liang.wu");

		renderPipe = new RenderPipe();
		spriteProgram = new SpriteProgram();
		backgroundProgram = new BackgroundProgram();
		meshProgram = new MeshProgram();

		bufferHelper = new BufferHelper();
	}

	public static Emulator3D getInstance() {
		if (instance == null) {
			instance = new Emulator3D();
		}

		return instance;
	}

	/*
	 * antialias setting here for easy implementation
	 * in webgl this will translate to { antialias: true } on the canvas
	 */
	public synchronized final void bindTarget(Object target, boolean antialias) {
		if (exiting) {
			// Infinite lock instead just throwing an exception
			try {
				wait();
			} catch (InterruptedException ignored) {}
			throw new IllegalStateException("exiting");
		}

		GLES2.ensure(antialias); // for webgl, here we'll need to put antialias

		int w;
		int h;

		if (target instanceof Graphics) {
			this.target = target;
			w = ((PlatformGraphics) this.target).getCanvas().getWidth();
			h = ((PlatformGraphics) this.target).getSafeHeight();
		} else {
			if (!(target instanceof Image2D)) {
				throw new IllegalArgumentException();
			}

			this.target = target;
			w = ((Image2D) this.target).getWidth();
			h = ((Image2D) this.target).getHeight();
		}

		try {
			try {
				GLES2.setSurface(w, h);
				GLES2.bind();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (targetWidth != w || targetHeight != h) {
				// this buffer doesn't need alpha
				// blending is done via OpenGL, not via the image layer
				awtBufferImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				buffer = new byte[w * h * 4];
				targetWidth = w;
				targetHeight = h;
			}

			GLES2.enable(GL_SCISSOR_TEST);
			GLES2.pixelStorei(GL_UNPACK_ALIGNMENT, 1);
		} catch (Exception e) {
			e.printStackTrace();
			this.target = null;
			throw new IllegalArgumentException();
		}
	}

	public synchronized void releaseTarget() {
		if (GLES2.bound) {
			GLES2.finish();
			swapBuffers();

			textureResourceManager.deleteUnusedTextures();

			if (exiting) {
				textureResourceManager.deleteAllTextures();
				return;
			}

			this.target = null;
			GLES2.release();
		}

	}

	public void swapBuffers() {
		swapBuffers(0, 0, targetWidth, targetHeight);
	}

	public final void swapBuffers(int x, int y, int width, int height) {
		if (this.target != null) {
			if (this.target instanceof Image2D) {
				Image2D targetImage = (Image2D) this.target;

				GLES2.readPixels(x, y, width, height, buffer);

				if (targetImage.getFormat() == Image2D.RGBA) {
					// Flip rows while copying to the target image.
					byte[] flippedBuffer = new byte[buffer.length];
					int rowSize = width * 4; // RGBA = 4 bytes per pixel

					for (int row = 0; row < height; row++) {
						System.arraycopy(buffer, row * rowSize, flippedBuffer, (height - 1 - row) * rowSize, rowSize);
					}

					targetImage.set(x, y, width, height, flippedBuffer);
				} else {
					// Convert buffer to RGB format and flip rows.
					byte[] rgbData = new byte[width * height * 3];
					int rowSize = width * 4; // RGBA = 4 bytes per pixel
					int rgbIndex = 0;

					for (int row = 0; row < height; row++) {
						int bufferRowStart = (height - 1 - row) * rowSize;

						for (int col = 0; col < width; col++) {
							int bufferIndex = bufferRowStart + (col * 4);
							rgbData[rgbIndex++] = buffer[bufferIndex];     // Red
							rgbData[rgbIndex++] = buffer[bufferIndex + 1]; // Green
							rgbData[rgbIndex++] = buffer[bufferIndex + 2]; // Blue
						}
					}

					targetImage.set(x, y, width, height, rgbData);
				}
			} else {
				// we can't blend here, we must overwrite
				// if the overwrite hint was not specified, the img was already drawn into the internal buffer
				// just blending without doing it would break compositemode interacting with the background


				GLES2.readPixels(x, y, width, height, buffer);

				int[] data = ((DataBufferInt) awtBufferImage.getRaster().getDataBuffer()).getData();

				// Flip rows
				for (int row = 0; row < height; row++) {
					int sourceOffset = (height - 1 - row) * width * 4;
					int destOffset = row * width;

					for (int col = 0; col < width; col++) {
						int srcIndex = sourceOffset + (col * 4);

						// Combine RGB channels into a single integer (without alpha).
						data[destOffset + col] = ((buffer[srcIndex] & 0xFF) << 16) | // Red
												  ((buffer[srcIndex + 1] & 0xFF) << 8) | // Green
												  (buffer[srcIndex + 2] & 0xFF);         // Blue
					}
				}

				((PlatformGraphics) this.target).drawImage2(awtBufferImage, x, y);
			}
		}
	}

	public final void enableDepthBuffer(boolean enabled) {
		depthBufferEnabled = enabled;
	}

	public final boolean isDepthBufferEnabled() {
		return depthBufferEnabled;
	}

	public final void setHints(int hints) {
		this.hints = hints;

		if (target != null) {
			setHintsInternal();
		}

	}

	private void setHintsInternal() {
		if ((hints & Graphics3D.DITHER) != 0) {
			GLES2.enable(GL_DITHER);
		} else {
			GLES2.disable(GL_DITHER);
		}
	}

	public final int getHints() {
		return hints;
	}

	public final Hashtable getProperties() {
		return properties;
	}

	public final void setDepthRange(float near, float far) {
		depthRangeNear = near;
		depthRangeFar = far;
	}

	private void setupDepth() {
		GLES2.uniform2f(((ICommonShader)currentProgram).uDepthRange(), depthRangeNear, depthRangeFar);
	}

	public final void setViewport(int x, int y, int w, int h) {
		viewportX = x;
		viewportY = y;
		viewportWidth = w;
		viewportHeight = h;
	}

	private void setupViewport() {
		GLES2.viewport(viewportX, targetHeight - viewportY - viewportHeight, viewportWidth, viewportHeight);
		GLES2.scissor(viewportX, targetHeight - viewportY - viewportHeight, viewportWidth, viewportHeight);
	}

	public final synchronized void clearBackgound(Object bgObj) {
		Background bg = (Background) bgObj;

		setupViewport();

		GLES2.clearDepthf(1);
		GLES2.depthMask(true);
		GLES2.colorMask(true, true, true, true);

		int bgColor = bg != null ? bg.getColor() : 0;
		GLES2.clearColor(
				G3DUtils.getFloatColor(bgColor, 16),
				G3DUtils.getFloatColor(bgColor, 8),
				G3DUtils.getFloatColor(bgColor, 0),
				G3DUtils.getFloatColor(bgColor, 24)
		);

		if (bg != null) {
			int colorClear = bg.isColorClearEnabled() ? GL_COLOR_BUFFER_BIT : 0;
			int depthClear = depthBufferEnabled && bg.isDepthClearEnabled() ? GL_DEPTH_BUFFER_BIT : 0;
			GLES2.clear(colorClear | depthClear);

			drawBackgroundImage(bg);
		} else {
			GLES2.clear(GL_COLOR_BUFFER_BIT | (depthBufferEnabled ? GL_DEPTH_BUFFER_BIT : 0));
		}
	}

	private void drawBackgroundImage(Background bg) {
		if (bg == null || bg.getImage() == null || bg.getCropWidth() <= 0 || bg.getCropHeight() <= 0) return;

		GLES2.disable(GL_BLEND);
		GLES2.depthFunc(GL_ALWAYS);
		GLES2.depthMask(false);

		float imgWidth = bg.getImage().getWidth();
		float imgHeight = bg.getImage().getHeight();

		float[] uvRect = new float[] {
			bg.getCropX() / imgWidth, bg.getCropY() / imgHeight,
			(bg.getCropX() + bg.getCropWidth()) / imgWidth,
			(bg.getCropY() + bg.getCropHeight()) / imgHeight
		};

		useProgram(backgroundProgram);

		bufferHelper.vertexFloatAttribPointer(backgroundProgram.aPos, 2, false, 0, 4, new float[] {
			1, 1,
			0, 1,
			1, 0,
			0, 0
		});

		bufferHelper.vertexFloatAttribPointer(backgroundProgram.aNormalizedUV, 2, false, 0, 4, new float[] {
			uvRect[2], uvRect[1],
			uvRect[0], uvRect[1],
			uvRect[2], uvRect[3],
			uvRect[0], uvRect[3]
		});

		GLES2.enableVertexAttribArray(backgroundProgram.aPos);
		GLES2.enableVertexAttribArray(backgroundProgram.aNormalizedUV);

		GLES2.uniform1i(backgroundProgram.uRepeatX, bg.getImageModeX() == Background.REPEAT ? 1 : 0);
		GLES2.uniform1i(backgroundProgram.uRepeatY, bg.getImageModeY() == Background.REPEAT ? 1 : 0);

		GLES2.activeTexture(GL_TEXTURE0);

		int textureId = useTexture(bg.getImage());

		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		ensureTextureLoaded(bg.getImage(), true);

		GLES2.uniform1i( backgroundProgram.uTexture, 0); // Texture unit 0
		GLES2.bindTexture(GL_TEXTURE_2D, textureId);

		GLES2.drawArrays(GL_TRIANGLE_STRIP, 0, 4);

		GLES2.bindTexture(GL_TEXTURE_2D, 0);

		GLES2.disableVertexAttribArray(backgroundProgram.aPos);
		GLES2.disableVertexAttribArray(backgroundProgram.aNormalizedUV);

		GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

		useFP();
	}

	private void setupCamera(Transform trans) {
		if (CameraCache.camera != null) {
			Transform tmpMat = new Transform();
			CameraCache.camera.getProjection(tmpMat);

			GLES2.uniformMatrix4fv(((ICommonShader)currentProgram).uProjectionMatrix(), true, ((Transform3D) tmpMat.getImpl()).m_matrix);

			tmpMat.set(CameraCache.invCam);
			if (trans != null) {
				tmpMat.postMultiply(trans);

			}

			GLES2.uniformMatrix4fv(((ICommonShader)currentProgram).uModelViewMatrix(), true, ((Transform3D) tmpMat.getImpl()).m_matrix);

		} else {
			throw new RuntimeException("camera cache null?");
		}
	}

	private void setupLights(Vector lights, Vector lightMats, int scope) {
		int usedLights = 0;
		Transform tmpMat = new Transform();

		for (int i = 0; i < lights.size() && usedLights < MaxLights; ++i) {
			Light light = (Light) lights.get(i);

			if (light == null || (light.getScope() & scope) == 0 || !renderPipe.isVisible(light)) {
				continue;
			}

			int lightId = usedLights;
			usedLights++;

			Transform lightMat = (Transform) lightMats.get(i);

			tmpMat.set(CameraCache.invCam);

			if (lightMat != null) {
				tmpMat.postMultiply(lightMat);
			}

			// the actual position is defined via matrix.. see it this is optimal
			GLES2.uniformMatrix4fv(meshProgram.uLMatrix[lightId], true, ((Transform3D) tmpMat.getImpl()).m_matrix);

			float[] tmpLightPos;
			if (light.getMode() == Light.DIRECTIONAL) {
				tmpLightPos = LightsCache.POSITIVE_Z_AXIS; //light direction!
			} else {
				tmpLightPos = LightsCache.LOCAL_ORIGIN;
			}

			GLES2.uniform4fv(meshProgram.uLPosition[lightId], tmpLightPos);

			float[] defaultColor = new float[]{0, 0, 0, 1}; //rgba

			float[] lightColor = new float[]{0, 0, 0, 1}; //rgba
			G3DUtils.fillFloatColor(lightColor, light.getColor());
			float lightIntensity = light.getIntensity();
			lightColor[0] *= lightIntensity;
			lightColor[1] *= lightIntensity;
			lightColor[2] *= lightIntensity;
			lightColor[3] = 1.0F;

			int lightMode = light.getMode();

			GLES2.uniform4fv(meshProgram.uLAmbient[lightId],
				(lightMode == Light.AMBIENT) ? lightColor : defaultColor
			);
			GLES2.uniform4fv(meshProgram.uLDiffuse[lightId],
				(lightMode == Light.AMBIENT) ? defaultColor : lightColor
			);
			GLES2.uniform4fv(meshProgram.uLSpecular[lightId],
				(lightMode == Light.AMBIENT) ? defaultColor : lightColor
			);

			GLES2.uniform1f(meshProgram.uLConstAtt[lightId],
				(lightMode == Light.SPOT || lightMode == Light.OMNI) ? light.getConstantAttenuation() : 1.0f
			);
			GLES2.uniform1f(meshProgram.uLLinAtt[lightId],
				(lightMode == Light.SPOT || lightMode == Light.OMNI) ? light.getLinearAttenuation() : 0.0f
			);
			GLES2.uniform1f(meshProgram.uLQuadAtt[lightId],
				(lightMode == Light.SPOT || lightMode == Light.OMNI) ? light.getQuadraticAttenuation() : 0.0f
			);

			if (lightMode == Light.SPOT) {
				GLES2.uniform3fv(meshProgram.uLDirection[lightId],
					LightsCache.NEGATIVE_Z_AXIS // actual direction is from the matrix
				);
				GLES2.uniform1f(meshProgram.uLSpotCutoffCos[lightId],
					(float)Math.cos(Math.toRadians(light.getSpotAngle()))
				);
				GLES2.uniform1f(meshProgram.uLShininess[lightId],
					light.getSpotExponent()
				);
			} else {
				GLES2.uniform1f(meshProgram.uLSpotCutoffCos[lightId], -1.0f);
			}
		}

		GLES2.uniform1i(meshProgram.uUsedLights, usedLights);

	}

	public final void render(VertexBuffer vb, IndexBuffer ib, Appearance ap, Transform trans, int scope) {
		renderVertex(vb, ib, ap, trans, scope, 1.0F);
	}

	private synchronized void renderVertex(VertexBuffer vb, IndexBuffer ib, Appearance ap, Transform trans, int scope, float alphaFactor) {
		if ((CameraCache.camera.getScope() & scope) != 0) {
			useProgram(meshProgram);

			setupViewport();
			setupDepth();
			setupCamera(trans);

			setupAppearance(ap, false, alphaFactor);
			if (ap.getMaterial() != null) {
				setupLights(LightsCache.m_lights, LightsCache.m_lightsTransform, scope);
			}

			draw(vb, ib, ap, alphaFactor);

			useFP();
		}
	}

	private void setupAppearance(Appearance ap, boolean spriteMode, float alphafactor) {
		if (!spriteMode) {
			setupPolygonMode(ap.getPolygonMode());
		}

		setupCompositingMode(ap.getCompositingMode());
		if (!spriteMode) {
			setupMaterial(ap.getMaterial(), alphafactor);
		}

		setupFog(ap.getFog());
	}

	private void setupPolygonMode(PolygonMode pm) {
		if (pm == null) {
			pm = new PolygonMode();
		}

		int var1 = pm.getCulling();
		if (var1 == PolygonMode.CULL_NONE) {
			GLES2.disable(GL_CULL_FACE);
		} else {
			GLES2.enable(GL_CULL_FACE);
			GLES2.cullFace(var1 == PolygonMode.CULL_FRONT ? GL_FRONT : GL_BACK);
		}

		flatShade = pm.getShading() == PolygonMode.SHADE_FLAT;
		GLES2.frontFace(pm.getWinding() == PolygonMode.WINDING_CW ? GL_CW : GL_CCW);


		GLES2.uniform1i(meshProgram.uIsTwoSided, pm.isTwoSidedLightingEnabled() ? 1 : 0);
		GLES2.uniform1i(meshProgram.uIsLocalViewer, pm.isLocalCameraLightingEnabled() ? 1 : 0);

		// note perspective correction should always work
	}

	private void setupCompositingMode(CompositingMode cm) {
		if (cm == null) {
			cm = new CompositingMode();
		}

		if (depthBufferEnabled) {
			GLES2.enable(GL_DEPTH_TEST);
		} else {
			GLES2.disable(GL_DEPTH_TEST);
		}

		GLES2.depthMask(cm.isDepthWriteEnabled());
		GLES2.depthFunc(cm.isDepthTestEnabled() ? GL_LEQUAL : GL_ALWAYS);
		GLES2.colorMask(cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isAlphaWriteEnabled());

		GLES2.uniform1f(((ICommonShader)currentProgram).uMinAlpha(), cm.getAlphaThreshold());

		if (cm.getBlending() == CompositingMode.REPLACE) {
			GLES2.disable(GL_BLEND);
		} else {
			GLES2.enable(GL_BLEND);
		}

		switch (cm.getBlending()) {
			case CompositingMode.ALPHA:
				GLES2.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				break;
			case CompositingMode.ALPHA_ADD:
				GLES2.blendFunc(GL_SRC_ALPHA, GL_ONE);
				break;
			case CompositingMode.MODULATE:
				GLES2.blendFunc(GL_DST_COLOR, GL_ZERO);
				break;
			case CompositingMode.MODULATE_X2:
				GLES2.blendFunc(GL_DST_COLOR, GL_SRC_COLOR);
				break;
			case CompositingMode.REPLACE:
				GLES2.blendFunc(GL_ONE, GL_ZERO);
				break;
			default:
				break;
		}

		// polygon offset is supported in webgl
		GLES2.polygonOffset(cm.getDepthOffsetFactor(), cm.getDepthOffsetUnits());
		if (cm.getDepthOffsetFactor() == 0.0F && cm.getDepthOffsetUnits() == 0.0F) {
			GLES2.disable(GL_POLYGON_OFFSET_FILL);
		} else {
			GLES2.enable(GL_POLYGON_OFFSET_FILL);
		}


	}

	private void setupMaterial(Material mat, float alphaFactor) {
		GLES2.uniform1i(meshProgram.uUseLighting, (mat != null) ? 1 : 0);

		if (mat != null) {
			GLES2.uniform1i(meshProgram.uTrackVertexColors, mat.isVertexColorTrackingEnabled() ? 1 : 0);

			float[] tmpCol = new float[4];

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.AMBIENT));
			GLES2.uniform4f(meshProgram.uMAmbient, tmpCol[0], tmpCol[1], tmpCol[2], tmpCol[3]);

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.DIFFUSE));
			GLES2.uniform4f(meshProgram.uMDiffuse, tmpCol[0], tmpCol[1], tmpCol[2], tmpCol[3] * alphaFactor);

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.EMISSIVE));
			GLES2.uniform4f(meshProgram.uMEmissive, tmpCol[0], tmpCol[1], tmpCol[2], tmpCol[3]);

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.SPECULAR));
			GLES2.uniform4f(meshProgram.uMSpecular, tmpCol[0], tmpCol[1], tmpCol[2], tmpCol[3]);

			GLES2.uniform1f(meshProgram.uMShininess, mat.getShininess());
		}
	}

	private void setupFog(Fog fog) {
		if (currentProgram != null) {
			ICommonShader cs = (ICommonShader)currentProgram;
			if (fog != null) {
				if (fog.getMode() == Fog.LINEAR) {
					GLES2.uniform1i(cs.uFogType(), 1);
					GLES2.uniform1f(cs.uFogStartOrDensity(), fog.getNearDistance());
					GLES2.uniform1f(cs.uFogEnd(), fog.getFarDistance());
				} else {
					GLES2.uniform1i(cs.uFogType(), 2);
					GLES2.uniform1f(cs.uFogStartOrDensity(), fog.getDensity());
				}

				float[] fogColor = new float[4];
				G3DUtils.fillFloatColor(fogColor, fog.getColor());
				GLES2.uniform4f(cs.uFogColor(), fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
			} else {
				GLES2.uniform1i(cs.uFogType(), 0);
			}

		} else {
			throw new IllegalStateException("no program");
		}
	}

	private void draw(VertexBuffer vb, IndexBuffer indices, Appearance ap, float alphaFactor) {
		float[] scaleBias = new float[4];
		VertexArray positions = vb.getPositions(scaleBias);

		if (flatShade) {
			positions.explodeFor((TriangleStripArray)indices, false);
		}

		int vCount = flatShade ? positions.getExplodedVertexCount() : positions.getVertexCount();

		VertexArray colors = vb.getColors();
		if (colors == null) {
			int col = vb.getDefaultColor();
			GLES2.vertexAttrib4f(meshProgram.aVColor, (col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, (alphaFactor * (col >> 24 & 255)) / 255.0f);
		} else {
			// this will be an array but need to disable it afterwards
			GLES2.enableVertexAttribArray(meshProgram.aVColor);

			if (flatShade) {
				colors.explodeFor((TriangleStripArray)indices, true); //substitute?
			}
			if (colors.getComponentType() == 1) {
				// getcolorbuffer converts it to 4 values and multiplies alphafactor
				byte[] colorsBArr = flatShade ? colors.getExplodedByteValues() : colors.getByteValues();
				byte[] color4Array = BUtils.getColorBuffer(colorsBArr, alphaFactor, vCount);

				bufferHelper.vertexByteAttribPointer(meshProgram.aVColor, 4, true, true, 0, vCount, color4Array);
			}
		}

		VertexArray normals = vb.getNormals();
		if (normals != null && ap.getMaterial() != null) {
			if (flatShade) {
				normals.explodeFor((TriangleStripArray)indices, true); //substitute?
			}

			GLES2.enableVertexAttribArray(meshProgram.aVNormal);

			if (normals.getComponentType() == 1) {
				bufferHelper.vertexByteAttribPointer(meshProgram.aVNormal, 3, false, true, 0, vCount, flatShade ? normals.getExplodedByteValues() : normals.getByteValues());
			} else {
				bufferHelper.vertexShortAttribPointer(meshProgram.aVNormal, 3, true, 0, vCount, flatShade ? normals.getExplodedShortValues() : normals.getShortValues());
			}
		}

		GLES2.uniform1i(meshProgram.uIsFlatShaded, flatShade ? 1 : 0);

		GLES2.vertexAttrib4f(meshProgram.aVPosSb, scaleBias[1], scaleBias[2], scaleBias[3], scaleBias[0]);

		GLES2.enableVertexAttribArray(meshProgram.aVPos);

		if (flatShade) {
			GLES2.enableVertexAttribArray(meshProgram.aVPosLight);
		}

		if (positions.getComponentType() == 1) {
			byte[] posesBArr = flatShade ? positions.getExplodedByteValues() : positions.getByteValues();

			bufferHelper.vertexByteAttribPointer(meshProgram.aVPos, 3, false, false, 0, vCount, posesBArr);

			if (flatShade) {
				short[] posesArrFlat = BUtils.getVertexBufferFlatLight(posesBArr);

				bufferHelper.vertexShortAttribPointer(meshProgram.aVPosLight, 3, false, 0, vCount, posesArrFlat);
			}
		} else {
			short[] posesSArr = flatShade ? positions.getExplodedShortValues() : positions.getShortValues();

			bufferHelper.vertexShortAttribPointer(meshProgram.aVPos, 3, false, 0, vCount, posesSArr);

			if (flatShade) {
				short[] posesArrFlat = BUtils.getVertexBufferFlatLight(posesSArr);

				bufferHelper.vertexShortAttribPointer(meshProgram.aVPosLight, 3, false, 0, vCount, posesArrFlat);
			}
		}


		TriangleStripArray triangleStripArray = (TriangleStripArray) indices;

		if (ap != null) {
			int usedTextures = 0;

			for (int i = 0; i < NumTextureUnits; ++i) {
				Texture2D texture2D = ap.getTexture(i);
				scaleBias[3] = 0.0F;
				VertexArray texCoords = vb.getTexCoords(i, scaleBias);

				if (texture2D == null || texCoords == null) continue;

				int textureIdx = usedTextures++;

				Image2D image2D = texture2D.getImage();

				GLES2.activeTexture(GL_TEXTURE0 + textureIdx);

				int glTextureId = useTexture(image2D); // calls bind but on acivetexture

				GLES2.uniform1i(meshProgram.uBlendMode[textureIdx], texture2D.getBlending());


				float[] blendColor = new float[4];
				G3DUtils.fillFloatColor(blendColor, texture2D.getBlendColor());
				blendColor[3] = 1.0F;

				GLES2.uniform4f(meshProgram.uBlendColor[textureIdx], blendColor[0], blendColor[1], blendColor[2], blendColor[3]);


				ensureTextureLoaded(image2D);

				boolean hasColor = true, hasAlpha = true;

				switch (image2D.getFormat()) {
					case Image2D.ALPHA:
						hasColor = false;
						break;
					case Image2D.LUMINANCE:
						hasAlpha = false;
						break;
					case Image2D.RGB:
						hasAlpha = false;
						break;
				}

				GLES2.uniform1i(meshProgram.uHasColor[textureIdx], hasColor ? 1 : 0);
				GLES2.uniform1i(meshProgram.uHasAlpha[textureIdx], hasAlpha ? 1 : 0);



				GLES2.texParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
						texture2D.getWrappingS() == Texture2D.WRAP_CLAMP ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);
				GLES2.texParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
						texture2D.getWrappingT() == Texture2D.WRAP_CLAMP ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);

				int levelFilter = texture2D.getLevelFilter();
				int imageFilter = texture2D.getImageFilter();

				int magFilter = GL_NEAREST, minFilter = GL_NEAREST;

				if (!noTextureFiltering) {
					if (imageFilter == Texture2D.FILTER_NEAREST) {
						minFilter = magFilter = GL_NEAREST;

						if (levelFilter == Texture2D.FILTER_NEAREST) minFilter = GL_NEAREST_MIPMAP_NEAREST;
						else if (levelFilter == Texture2D.FILTER_LINEAR) minFilter = GL_NEAREST_MIPMAP_LINEAR;
					} else if (imageFilter == Texture2D.FILTER_LINEAR) {
						minFilter = magFilter = GL_LINEAR;

						if (levelFilter == Texture2D.FILTER_NEAREST) minFilter = GL_LINEAR_MIPMAP_NEAREST;
						else if (levelFilter == Texture2D.FILTER_LINEAR) minFilter = GL_LINEAR_MIPMAP_LINEAR;
					}
				}

				GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
				GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

				if (!noTextureFiltering) {
					GLES2.toggleAnisotropy(imageFilter == Texture2D.FILTER_LINEAR);
				}

				// Bind the texture for use in the shader
				GLES2.uniform1i(meshProgram.uTexture[textureIdx], textureIdx); // Texture unit 0
				GLES2.bindTexture(GL_TEXTURE_2D, glTextureId);


				if (flatShade) {
					texCoords.explodeFor(triangleStripArray, false);
				}

				if (texCoords.getComponentType() == 1) {
					byte[] coords = flatShade ? texCoords.getExplodedByteValues() : texCoords.getByteValues();
					bufferHelper.vertexByteAttribPointer(meshProgram.aVCoords[textureIdx], 2, false, false, texCoords.getComponentCount() == 2 ? 0 : 3, vCount, coords);
				} else {
					short[] coords = flatShade ? texCoords.getExplodedShortValues() : texCoords.getShortValues();
					bufferHelper.vertexShortAttribPointer(meshProgram.aVCoords[textureIdx], 2, false, texCoords.getComponentCount() == 2 ? 0 : 3, vCount, coords);

				}


				GLES2.enableVertexAttribArray(meshProgram.aVCoords[textureIdx]);


				// not normalized

				Transform tmpMat = new Transform();
				texture2D.getCompositeTransform(tmpMat);

				tmpMat.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
				tmpMat.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);

				GLES2.uniformMatrix4fv(meshProgram.uVCoordsMat[textureIdx], true, ((Transform3D) tmpMat.getImpl()).m_matrix);
			}

			GLES2.uniform1i(meshProgram.uUsedTextures, usedTextures);


			if (flatShade) {
				GLES2.drawArrays(GL_TRIANGLES, 0, positions.getExplodedVertexCount());
			} else {
				int[] triangles = triangleStripArray.getTriangles();
				bufferHelper.setIndexBuffer(triangles, triangles.length);

				GLES2.drawElements(GL_TRIANGLE_STRIP, triangles.length, GL_UNSIGNED_SHORT, 0);

				GLES2.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			}


			for (int i = 0; i < usedTextures; ++i) {
				GLES2.activeTexture(GL_TEXTURE0 + i);
				GLES2.bindTexture(GL_TEXTURE_2D, 0);

			}
		} else {
			throw new IllegalStateException("appearance is null?");
		}

		GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

		/// do we need to do this beside vColor?
		GLES2.disableVertexAttribArray(meshProgram.aVColor);
		GLES2.disableVertexAttribArray(meshProgram.aVPos);
		GLES2.disableVertexAttribArray(meshProgram.aVPosLight);
		GLES2.disableVertexAttribArray(meshProgram.aVNormal);
		GLES2.disableVertexAttribArray(meshProgram.aVCoords[0]);
		GLES2.disableVertexAttribArray(meshProgram.aVCoords[1]);
	}

	public synchronized void render(World world) {
		Transform camTrans = new Transform();
		world.getActiveCamera().getTransformTo(world, camTrans);
		CameraCache.setCamera(world.getActiveCamera(), camTrans);

		clearBackgound(world.getBackground());
		LightsCache.addLightsFromWorld(world);
		renderPipe.pushRenderNode(world, null);

		renderPushedNodes();
	}

	public synchronized void render(Node node, Transform transform) {
		renderPipe.pushRenderNode(node, transform);
		renderPushedNodes();
	}

	private void renderPushedNodes() {
		renderPipe.sortNodes();

		for (int i = 0; i < renderPipe.getSize(); i++) {
			RenderObject ro = renderPipe.getRenderObj(i);

			if (ro.node instanceof Mesh) {
				Mesh mesh = (Mesh) ro.node;
				IndexBuffer indices = mesh.getIndexBuffer(ro.submeshIndex);
				Appearance ap = mesh.getAppearance(ro.submeshIndex);

				if (indices != null && ap != null) {
					VertexBuffer vb = MeshMorph.getInstance().getMorphedVertexBuffer(mesh);
					renderVertex(vb, indices, ap, ro.trans, mesh.getScope(), ro.alphaFactor);
				}
			} else {
				renderSprite((Sprite3D) ro.node, ro.trans, ro.alphaFactor);
			}
		}

		renderPipe.clear();
		MeshMorph.getInstance().clearCache();
	}

	private void renderSprite(Sprite3D sprite, Transform spriteTransform, float alphaFactor) {
		if (!G3DUtils.intersectRectangle(sprite.getCropX(), sprite.getCropY(), Math.abs(sprite.getCropWidth()), Math.abs(sprite.getCropHeight()), 0, 0, sprite.getImage().getWidth(), sprite.getImage().getHeight(), null)) {
			return;
		}

		// Define initial vectors for 3D coordinates (homogeneous coordinates with w=1.0).
		float[] origin = new float[]{0.0F, 0.0F, 0.0F, 1.0F};  // Origin point (0, 0, 0)
		float[] rightDirection = new float[]{1.0F, 0.0F, 0.0F, 1.0F};  // Point along x-axis (1, 0, 0)
		float[] upDirection = new float[]{0.0F, 1.0F, 0.0F, 1.0F};  // Point along y-axis (0, 1, 0)

		// Combine the camera's inverse transformation with the sprite's transformation
		Transform combinedTransform = new Transform(CameraCache.invCam);
		combinedTransform.postMultiply(spriteTransform);

		// Apply the combined transformation to the 3D coordinates
		Transform3D impl = (Transform3D) combinedTransform.getImpl();
		impl.transform(origin);
		impl.transform(rightDirection);
		impl.transform(upDirection);

		// Copy the transformed origin for future use
		float[] transformedOrigin = new float[]{origin[0], origin[1], origin[2], origin[3]};

		// Normalize the coordinates by their 'w' component (perspective division)
		Vector4f.mul(origin, 1.0F / origin[3]);
		Vector4f.mul(rightDirection, 1.0F / rightDirection[3]);
		Vector4f.mul(upDirection, 1.0F / upDirection[3]);

		// Calculate direction vectors relative to the origin
		Vector4f.sub(rightDirection, origin);
		Vector4f.sub(upDirection, origin);

		// Compute the lengths of the directional vectors (scaled in x and y)
		float[] scaledX = new float[]{Vector4f.length(rightDirection), 0.0F, 0.0F, 0.0F};
		float[] scaledY = new float[]{0.0F, Vector4f.length(upDirection), 0.0F, 0.0F};

		// Add the scaled vectors back to the transformed origin to get final screen-space positions
		Vector4f.add(scaledX, transformedOrigin);
		Vector4f.add(scaledY, transformedOrigin);

		// Get the camera's projection transform and apply it to the screen-space positions
		Transform projectionTransform = new Transform();
		CameraCache.camera.getProjection(projectionTransform);
		impl = (Transform3D) projectionTransform.getImpl();
		impl.transform(transformedOrigin);
		impl.transform(scaledX);
		impl.transform(scaledY);

		// Check if the sprite is in front of the camera (z/w ratio check)
		if (transformedOrigin[3] > 0.0F && -transformedOrigin[3] < transformedOrigin[2] && transformedOrigin[2] <= transformedOrigin[3]) {
			// Normalize again after projection
			Vector4f.mul(transformedOrigin, 1.0F / transformedOrigin[3]);
			Vector4f.mul(scaledX, 1.0F / scaledX[3]);
			Vector4f.mul(scaledY, 1.0F / scaledY[3]);

			// Adjust directions for screen-space (after projection)
			Vector4f.sub(scaledX, transformedOrigin);
			Vector4f.sub(scaledY, transformedOrigin);

			// what is scaledX and scaledY?
			// these are the width/height of an "3d unit" in clip space

			// Check if sprite needs to be scaled
			boolean isSpriteScaled = sprite.isScaled();

			// crop affects UVs and for unscaled sprites it also affects their size
			// btw looks like scaled sprites are always squares..
			float[] rect = new float[]{
				sprite.getCropX()/(float)sprite.getImage().getWidth(),
			    sprite.getCropY()/(float)sprite.getImage().getHeight(),
				(sprite.getCropX() + (sprite.getCropWidth() < 0 ? sprite.getImage().getWidth() : 0) + sprite.getCropWidth())/(float)sprite.getImage().getWidth(),
			    (sprite.getCropY() + (sprite.getCropHeight() < 0 ? sprite.getImage().getHeight() : 0) + sprite.getCropHeight())/(float)sprite.getImage().getHeight(),
			};

			float[] uvs = new float[]{
				rect[2], rect[1],
				rect[0], rect[1],
				rect[2], rect[3],
				rect[0], rect[3],
			};

			float scaleX, scaleY;

			if (!isSpriteScaled) {
				// these represent physical pixels on the screen
				// thus need to be normalized using current viewport area
				scaleX = Math.abs(sprite.getCropWidth());
				scaleY = Math.abs(sprite.getCropHeight());

				// turn to NDC
				scaleX = scaleX*2 / this.viewportWidth;
				scaleY = scaleY*2 / this.viewportHeight;
			} else {
				// these are in ndc for units
				scaleX = Vector4f.length(scaledX);
				scaleY = Vector4f.length(scaledY);
			}

			useProgram(spriteProgram);
			this.setupAppearance(sprite.getAppearance(), true, 1.0f);


			GLES2.uniformMatrix4fv(spriteProgram.uProjectionMatrix, true, ((Transform3D) projectionTransform.getImpl()).m_matrix);
			GLES2.uniformMatrix4fv(spriteProgram.uModelViewMatrix, true, ((Transform3D) combinedTransform.getImpl()).m_matrix);

			GLES2.uniform1f(spriteProgram.uScaleX, scaleX);
			GLES2.uniform1f(spriteProgram.uScaleY, scaleY);
			GLES2.uniform1f(spriteProgram.uAlphaFactor, alphaFactor);


			// we have "infra" for texture ids.. they're persistend, loaded on demand
			// ids are reserved and content can be invalidated

			int textureId = useTexture(sprite.getImage());

			// Set texture parameters
            GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		    GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			ensureTextureLoaded(sprite.getImage());

			// Bind the texture for use in the shader
			GLES2.uniform1i(spriteProgram.uTexture, 0); // Texture unit 0
			GLES2.activeTexture(GL_TEXTURE0);
			GLES2.bindTexture(GL_TEXTURE_2D, textureId);

			float[] ids = {0, 1, 2, 3};

			bufferHelper.vertexFloatAttribPointer(spriteProgram.aUV, 2, false, 0, 4, uvs);
			bufferHelper.vertexFloatAttribPointer(spriteProgram.aVertexID, 1, false, 0, 4, ids);

			GLES2.enableVertexAttribArray(spriteProgram.aUV); // Enable UV
			GLES2.enableVertexAttribArray(spriteProgram.aVertexID); // Enable UV

			// Draw the sprite as a triangle strip (rendered as screen-space pixels)
			GLES2.drawArrays(GL_TRIANGLE_STRIP, 0, 4);

			// Disable shaders and clean up
			GLES2.disableVertexAttribArray(spriteProgram.aUV);
			GLES2.disableVertexAttribArray(spriteProgram.aVertexID); // Enable UV

			GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);

			useFP();
		}
	}


	public int useTexture(Image2D image2D) {
		int id = image2D.getId();
		if (id == 0) {
			id = GLES2.createTexture();
			image2D.setId(id);
			textureResourceManager.registerHolder(image2D, id);
		}
		GLES2.bindTexture(GL_TEXTURE_2D, id);
		return id;
	}

	public void ensureTextureLoaded(Image2D image2D) {
		ensureTextureLoaded(image2D, false);
	}

	public void ensureTextureLoaded(Image2D image2D, boolean skipMipmaps) {
		if (!image2D.isLoaded()) {
			image2D.setLoaded(true);

			short texFormat = GL_RGB;
			switch (image2D.getFormat()) {
				case Image2D.ALPHA:
					texFormat = GL_ALPHA;
					break;
				case Image2D.LUMINANCE:
					texFormat = GL_LUMINANCE;
					break;
				case Image2D.LUMINANCE_ALPHA:
					texFormat = GL_LUMINANCE_ALPHA;
					break;
				case Image2D.RGB:
					texFormat = GL_RGB;
					break;
				case Image2D.RGBA:
					texFormat = GL_RGBA;
			}


			GLES2.texImage2D(GL_TEXTURE_2D, 0,
				texFormat, image2D.getWidth(), image2D.getHeight(), 0,
				texFormat, GL_UNSIGNED_BYTE,
				image2D.getImageData()
			);

			if (!skipMipmaps && !noTextureFiltering) {
				GLES2.generateMipmap(GL_TEXTURE_2D);
			}

		}

	}

	public synchronized void releaseTextures() {
		textureResourceManager.deleteAllTextures();
	}

	public void useProgram(ShaderProgram prog) {
		prog.use();
		currentProgram = prog;
	}

	public void useFP() {
		GLES2.useProgram(0);
		currentProgram = null;
	}

	public static void exit() {
		if (instance == null)
			return;
		Emulator3D inst = instance;
		if (inst.exiting) return;
		inst.exiting = true;
		synchronized (inst) {
			try {
				// try to make context current
				GLES2.bind();

				inst.bufferHelper.deallocate();
				inst.releaseTextures();
				// delete programs ???
				GLES2.release();
			} catch (Exception ignored) {}
		}
		GLES2.destroy(); // ?
	}

}