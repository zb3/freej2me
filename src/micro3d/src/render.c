#include "render.h"

#include <stdlib.h>
#include <string.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>

typedef struct {
    EGLDisplay display;
    EGLConfig config;
    EGLContext context;
    EGLSurface surface;
    int width;
    int height;
} EGLData;

static EGLData* initEGL() {
    EGLData *data = malloc(sizeof(EGLData));
    memset(data, 0, sizeof(EGLData));

    data->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);

    eglInitialize(data->display, NULL, NULL);

    EGLint num_config;
    EGLint attribs[] = {
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_DEPTH_SIZE, 16,
        EGL_STENCIL_SIZE, EGL_DONT_CARE,
        EGL_NONE
    };
  
    eglChooseConfig(data->display, attribs, &data->config, 1, &num_config);

    if (!num_config) {
        fprintf(stderr, "M3D: no egl config\n");
        fflush(stderr);
    }

    eglBindAPI(EGL_OPENGL_ES_API);

    EGLint attrib_list[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
    };
    
    data->context = eglCreateContext(data->display, data->config, EGL_NO_CONTEXT, attrib_list);
    if (!data->context) {
        fprintf(stderr, "M3D: no egl context\n");
        fflush(stderr);
    }

    return data;
}

static void bindEGL(EGLData *data) {
    if (eglMakeCurrent(data->display, data->surface, data->surface, data->context) == EGL_FALSE) {
        fprintf(stderr, "M3D: failed to make EGL context current\n");
        fflush(stderr);
    }
}

static void releaseEGL(EGLData *data) {
    if (eglMakeCurrent(data->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT) == EGL_FALSE) {
        fprintf(stderr, "M3D: failed to release EGL context\n");
        fflush(stderr);
    }
}

static void makeEGLSurface(EGLData *data, int width, int height) {
    if (data->width != width || data->height != height) {
        if (data->surface) {
            eglDestroySurface(data->display, data->surface);
        }

        // Create a PBuffer surface
        EGLint attribs[] = {
            EGL_WIDTH, width,
            EGL_HEIGHT, height,
            EGL_NONE
        };

        data->surface = eglCreatePbufferSurface(data->display, data->config, attribs);
        if (data->surface == EGL_NO_SURFACE) {
            fprintf(stderr, "M3D: Failed to create EGL PBuffer surface\n");
            fflush(stderr);
        }

        data->width = width;
        data->height = height;
    }

}

static void destroyEGL(EGLData *data) {
    releaseEGL(data);

    if (data->surface) {
        eglDestroySurface(data->display, data->surface);
    }

    if (data->context) {
        eglDestroyContext(data->display, data->context);
    }

    eglTerminate(data->display);

    free(data);
}


static char* gluErrorString(GLenum err) {
    switch (err) {
    case GL_NO_ERROR:
        return NULL;
    case GL_INVALID_ENUM:
        return "invalid enum";
    case GL_INVALID_VALUE:
        return "invalid value";
    case GL_INVALID_OPERATION:
        return "invalid operation";
    case GL_OUT_OF_MEMORY:
        return "out of memory";
    }

    return "unknown error";
}
    
JNIEXPORT jlong JNICALL Java_ru_woesss_j2me_micro3d_Render__1eglInit
    (JNIEnv *env, jclass) {
    return (jlong)initEGL();
}



JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1eglMakeSurface
    (JNIEnv *env, jclass, jlong ptr, jint width, jint height) {
    makeEGLSurface((EGLData *)ptr, width, height);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1eglBind
    (JNIEnv *env, jclass, jlong ptr) {
    bindEGL((EGLData *)ptr);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1eglRelease
    (JNIEnv *env, jclass, jlong ptr) {
    releaseEGL((EGLData *)ptr);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1eglDestroy
    (JNIEnv *env, jclass,jlong ptr) {
    destroyEGL((EGLData *)ptr);
}


JNIEXPORT jstring JNICALL Java_ru_woesss_j2me_micro3d_Render__1getGLError
        (JNIEnv *env, jclass) {
    jstring result = NULL;
    char *errStr = gluErrorString(glGetError());
    if (errStr) {
        result = (*env)->NewStringUTF(env, errStr); 
    }
    return result;
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glSetVp1
        (JNIEnv *env, jclass /*clazz*/,
         jint width, jint height) {
    glViewport(0, 0, width, height);
    glClearColor(0, 0, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glSetVp2
        (JNIEnv *env, jclass /*clazz*/,
         jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glSetClip1
        (JNIEnv *env, jclass /*clazz*/,
        jlong handle, int x, int y, jint width, jint height) {
    EGLData *data = (EGLData *)handle;

    if (x == 0 && y == 0 && width == data->width && height == data->height) {
	  glDisable(GL_SCISSOR_TEST);
	} else {
	  glEnable(GL_SCISSOR_TEST);
	  glScissor(x, y, width, height);
	}
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glClearDepth
        (JNIEnv *env, jclass /*clazz*/) {
    		glClear(GL_DEPTH_BUFFER_BIT);

}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glClearWithColor
        (JNIEnv *env, jclass /*clazz*/,
         jint clearColor) {

		glClearColor(
				((clearColor >> 16) & 0xff) / 255.0f,
				((clearColor >> 8) & 0xff) / 255.0f,
				(clearColor & 0xff) / 255.0f,
				1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

#define BLEND_HALF 2
#define BLEND_ADD 4
#define BLEND_SUB 6

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glApplyBlending
        (JNIEnv *env, jclass /*clazz*/,
         jint blendMode) {
        
        switch (blendMode) {
        	case BLEND_HALF:
        		glEnable(GL_BLEND);
        		glBlendColor(0.5f, 0.5f, 0.5f, 1.0f);
        		glBlendEquation(GL_FUNC_ADD);
        		glBlendFunc(GL_CONSTANT_COLOR, GL_CONSTANT_COLOR);
        		break;
        	case BLEND_ADD:
        		glEnable(GL_BLEND);
        		glBlendEquation(GL_FUNC_ADD);
        		glBlendFunc(GL_ONE, GL_ONE);
        		break;
        	case BLEND_SUB:
        		glEnable(GL_BLEND);
        		glBlendEquation(GL_FUNC_REVERSE_SUBTRACT);
        		glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ZERO, GL_ONE);
        		break;
        	default:
        		glDisable(GL_BLEND);
        }

}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glBindBgTexture
        (JNIEnv *env, jclass /*clazz*/,
         jintArray bgTextureIdArr, jboolean filter) {

    jint *bgTextureId = (*env)-> GetIntArrayElements(env, bgTextureIdArr, NULL);
    if (!glIsTexture(bgTextureId[0])) {
			glGenTextures(1, bgTextureId);
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, bgTextureId[0]);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		} else {
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, bgTextureId[0]);
		}


    (*env)-> ReleaseIntArrayElements(env, bgTextureIdArr, (jint*)bgTextureId, 0);

}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glBlitToTexture
        (JNIEnv *env, jclass /*clazz*/,
         jintArray pixelsArr, jint width, jint height) {

    jboolean isCopy;
    jint *jpixels = (*env)-> GetIntArrayElements(env, pixelsArr, &isCopy);
    jint *pixels = NULL;

    int npixels = width*height;
 
    if (!isCopy) {
        pixels = malloc(sizeof(jint) * npixels);
    } else {
        pixels = jpixels;
    }

    for (int t=0; t<npixels; t++) {
        // from argb32 to rgba8, which for LE means abgr32, so we just swap b and r
        pixels[t] = (jpixels[t] & 0xff00ff00) | ((jpixels[t] & 0xFF)<<16) | ((jpixels[t] >> 16) & 0xFF);
    }

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

    if (!isCopy) {
        free(pixels);
    }
    (*env)-> ReleaseIntArrayElements(env, pixelsArr, jpixels, JNI_ABORT);

}

float bgVert[] = {-1.0f, -1.0f,
                   1.0f, -1.0f,
                  -1.0f,  1.0f,
                   1.0f,  1.0f };

float bgUV[] = { 0.0f, 0.0f,
                 1.0f, 0.0f,
                 0.0f, 1.0f,
                 1.0f, 1.0f };



JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glcopy2DScreen
        (JNIEnv *env, jclass /*clazz*/,
         jint aposition, jint atexture, jboolean preProcess) {

    glVertexAttribPointer(aposition, 2, GL_FLOAT, GL_FALSE, 0, bgVert);
    glEnableVertexAttribArray(aposition);

    glVertexAttribPointer(atexture, 2, GL_FLOAT, GL_FALSE, 0, bgUV);
    glEnableVertexAttribArray(atexture);

    if (preProcess) {
        glDisable(GL_BLEND);
    } else {
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    glDisable(GL_DEPTH_TEST);
    glDepthMask(GL_FALSE);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glDisableVertexAttribArray(aposition);
    glDisableVertexAttribArray(atexture);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1bindBT1
        (JNIEnv *env, jclass /*clazz*/,
         jintArray bufHandlesArr, jobject vertices, jobject texCoords, jobject normals) {

    jint *bufHandles = (*env)->GetIntArrayElements(env, bufHandlesArr, NULL);


    if (bufHandles[0] == -1) {
        glGenBuffers(3, bufHandles);
    }

    // Assuming you have appropriate buffer handling functions
    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
    glBufferData(GL_ARRAY_BUFFER, (*env)->GetDirectBufferCapacity(env, vertices) * 4, (*env)->GetDirectBufferAddress(env, vertices), GL_STREAM_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
    glBufferData(GL_ARRAY_BUFFER, (*env)->GetDirectBufferCapacity(env, texCoords), (*env)->GetDirectBufferAddress(env, texCoords), GL_STREAM_DRAW);

    if (normals) {
        glBindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
        glBufferData(GL_ARRAY_BUFFER, (*env)->GetDirectBufferCapacity(env, normals) * 4, (*env)->GetDirectBufferAddress(env, normals), GL_STREAM_DRAW);
    }

    (*env)->ReleaseIntArrayElements(env, bufHandlesArr, bufHandles, 0);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1rfPart1
        (JNIEnv *env, jclass /*clazz*/,
         jboolean doDepthMask) {
        
		glEnable(GL_DEPTH_TEST);
		glDepthMask(doDepthMask);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1rfPolyT
        (JNIEnv *env, jclass /*clazz*/,
         jintArray bufHandlesArr, jint aPosition, jint aColorData, jint aMaterial, jint aNormal) {

    jint *bufHandles = (*env)->GetIntArrayElements(env, bufHandlesArr, NULL);

    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
    glEnableVertexAttribArray(aPosition);
    glVertexAttribPointer(aPosition, 3, GL_FLOAT, GL_FALSE, 3 * 4, 0);

    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
    glEnableVertexAttribArray(aColorData);
    glVertexAttribPointer(aColorData, 2, GL_UNSIGNED_BYTE, GL_FALSE, 5, 0);
    glEnableVertexAttribArray(aMaterial);
    glVertexAttribPointer(aMaterial, 3, GL_UNSIGNED_BYTE, GL_FALSE, 5, (void*)2); // yes, that's crazy but...

    if (aNormal != -1) {
        glBindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
        glEnableVertexAttribArray(aNormal);
        glVertexAttribPointer(aNormal, 3, GL_FLOAT, GL_FALSE, 3 * 4, 0);
    }

    (*env)->ReleaseIntArrayElements(env, bufHandlesArr, bufHandles, 0);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glEVAA
        (JNIEnv *env, jclass /*clazz*/,
        jint arr) {
    glEnableVertexAttribArray(arr);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDVAA
        (JNIEnv *env, jclass /*clazz*/,
        jint arr) {
    glDisableVertexAttribArray(arr);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glUnbindABuffer
        (JNIEnv *env, jclass /*clazz*/) {
    glBindBuffer(GL_ARRAY_BUFFER, 0);

}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDisableBlending
        (JNIEnv *env, jclass /*clazz*/) {
    glDisable(GL_BLEND);

}



JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1rfPolyC
        (JNIEnv *env, jclass /*clazz*/,
         jintArray bufHandlesArr, jint offset, jint aPosition, jint aColorData, jint aMaterial, jint aNormal) {

    jint *bufHandles = (*env)->GetIntArrayElements(env, bufHandlesArr, NULL);

    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[0]);
    glEnableVertexAttribArray(aPosition);
    glVertexAttribPointer(aPosition, 3, GL_FLOAT, GL_FALSE, 3 * 4, (void *)(3 * 4 * offset));

    glBindBuffer(GL_ARRAY_BUFFER, bufHandles[1]);
    glVertexAttribPointer(aColorData, 3, GL_UNSIGNED_BYTE, GL_TRUE, 5, (void *)(5 * offset));
    glEnableVertexAttribArray(aColorData);
    glEnableVertexAttribArray(aMaterial);
    glVertexAttribPointer(aMaterial, 2, GL_UNSIGNED_BYTE, GL_FALSE, 5, (void *)(5 * offset + 3));

    if (aNormal != -1) {
        glBindBuffer(GL_ARRAY_BUFFER, bufHandles[2]);
        glVertexAttribPointer(aNormal, 3, GL_FLOAT, GL_FALSE, 3 * 4, (void *)(3 * 4 * offset));
        glEnableVertexAttribArray(aNormal);
    }

    (*env)->ReleaseIntArrayElements(env, bufHandlesArr, bufHandles, 0);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glECFDA
        (JNIEnv *env, jclass /*clazz*/, jint e, jint pos, jint cnt) {
    if (e)
      glEnable(GL_CULL_FACE);
    else
      glDisable(GL_CULL_FACE);
    glDrawArrays(GL_TRIANGLES, pos, cnt);
}






JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glReadPixelsToBuffer
        (JNIEnv *env, jclass /*clazz*/,
         jint x, jint y, jint width, jint height, jobject buffer) {

    // writes to texture, so no conversion performed

    glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, (*env)->GetDirectBufferAddress(env, buffer));


}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glReadARGB32PixelsToArray
        (JNIEnv *env, jclass /*clazz*/,
         jint x, jint y, jint width, jint height, jintArray pixelsArr) {

    // note this assumes that we read the full size
    // otherwise this copy'd not work

    jint *pixels = (*env)->GetIntArrayElements(env, pixelsArr, NULL);
    

    glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);


    for (int t=0; t<width*height; t++) {
        // from argb32 to rgba8, which for LE means abgr32, so we just swap b and r
        pixels[t] = (pixels[t] & 0xff00ff00) | ((pixels[t] & 0xFF)<<16) | ((pixels[t] >> 16) & 0xFF);
    }


    (*env)->ReleaseIntArrayElements(env, pixelsArr, pixels, 0);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1flushGl1
        (JNIEnv *env, jclass /*clazz*/, jboolean flush) {
    glDisable(GL_BLEND);
	glDepthMask(GL_TRUE);
	glClear(GL_DEPTH_BUFFER_BIT);
    
    if (flush)
	glFlush();
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glVertexAttrib2f
        (JNIEnv *env, jclass /*clazz*/, jint attrib, jfloat f1, jfloat f2) {
    glVertexAttrib2f(attrib, f1, f2);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glVertexAttrib3f
        (JNIEnv *env, jclass /*clazz*/, jint attrib, jfloat f1, jfloat f2, jfloat f3) {
    glVertexAttrib3f(attrib, f1, f2, f3);
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glVertexAttribPointerf
        (JNIEnv *env, jclass /*clazz*/, jint attrib, jint cnt, jboolean sth, jint sth2, jobject buff) {
    glVertexAttribPointer(attrib, cnt, GL_FLOAT, sth, sth2, (*env)->GetDirectBufferAddress(env, buff));
}
JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glVertexAttribPointerb
        (JNIEnv *env, jclass /*clazz*/, jint attrib, jint cnt, jboolean sth, jint sth2, jobject buff) {
    glVertexAttribPointer(attrib, cnt, GL_UNSIGNED_BYTE, sth, sth2, (*env)->GetDirectBufferAddress(env, buff));
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDrawTriangles
        (JNIEnv *env, jclass /*clazz*/, jint i1, jint i2) {
    glDrawArrays(GL_TRIANGLES, i1, i2);
}
JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDrawPoints
        (JNIEnv *env, jclass /*clazz*/, jint i1, jint i2) {
    glDrawArrays(GL_POINTS, i1, i2);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDrawLines
        (JNIEnv *env, jclass /*clazz*/, jint i1, jint i2) {
    glDrawArrays(GL_LINES, i1, i2);
}


   
JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glSetCullFace
        (JNIEnv *env, jclass /*clazz*/, jboolean enable) {
    if (enable) {
        glEnable(GL_CULL_FACE);
    } else {
		glDisable(GL_CULL_FACE);
    }
}
JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glDepthFuncLess
        (JNIEnv *env, jclass /*clazz*/) {
    glDepthFunc(GL_LESS);

}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Render__1glRps1
        (JNIEnv *env, jclass /*clazz*/, jint uIsTransparency, jint val, jint numPrimitives) {
    glUniform1i(uIsTransparency, val);
	glDrawArrays(GL_TRIANGLES, 0, numPrimitives * 6);

}
