#include <jni.h>

#ifdef IS_WRANGLE
#include <wrangle.h>
#include <wrangle_gles2.h>
#else
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#endif

#include <stdlib.h>
#include <string.h>

typedef struct {
    EGLDisplay display;
    EGLConfig config;
    EGLContext context;
    EGLSurface surface;
    int width;
    int height;
    int has_extension_list;
    GLfloat max_anisotropy;
} EGLData;


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

static jclass longClass; // they said keep global ref to prevent unloading.. Long actually won't be unloaded
static jclass glesClass;
static jmethodID initLong;
static jmethodID longValue;
static jmethodID doThrow;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ((*vm)->GetEnv(vm, (void *)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass tmp;
    tmp = (*env)->FindClass(env, "java/lang/Long");
    longClass = (*env)->NewGlobalRef(env, tmp);

    initLong = (*env)->GetMethodID(env, tmp, "<init>", "(J)V");
    longValue =  (*env)->GetMethodID(env, tmp, "longValue", "()J");

    (*env)->DeleteLocalRef(env, tmp);

    tmp = (*env)->FindClass(env, "pl/zb3/freej2me/bridge/gles2/GLES2");
    glesClass = (*env)->NewGlobalRef(env, tmp);

    doThrow = (*env)->GetStaticMethodID(env, tmp, "doThrow", "(Ljava/lang/String;)V");

    (*env)->DeleteLocalRef(env, tmp);

    return JNI_VERSION_1_6;

}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    (*vm)->GetEnv(vm, (void *)&env, JNI_VERSION_1_6);

    (*env)->DeleteGlobalRef(env, longClass);
}

// this doesn't actually throw - this is for future cheerpj compatibility
void doThrowException(JNIEnv *env, const char *message) {
    jstring javaString = (*env)->NewStringUTF(env, message);

    (*env)->CallStaticVoidMethod(env, glesClass, doThrow, javaString);
}

// jobject is used as handle for future cheerpj comparibility

jobject wrapPointer(JNIEnv *env, void *nativePtr) {
    if (nativePtr == 0) {
        return NULL;
    }

    jobject longObject = (*env)->NewObject(env, longClass, initLong, nativePtr);
    return longObject;
}

void *unwrapPointer(JNIEnv *env, jobject longObject) {
    if (longObject == NULL) {
        return 0;
    }

    jlong nativePtr = (*env)->CallLongMethod(env, longObject, longValue);
    return (void *)nativePtr;
}

JNIEXPORT jobject JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2__1create
  (JNIEnv *env, jclass cls) {
    EGLData *data = malloc(sizeof(EGLData));
    memset(data, 0, sizeof(EGLData));

    #ifdef IS_WRANGLE
    wrangleHintGLESVersion(2);
    #endif

    data->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (data->display == EGL_NO_DISPLAY) {
        doThrowException(env, "no egl display");
        goto err;
    }

    if (eglInitialize(data->display, NULL, NULL) == EGL_FALSE) {
       doThrowException(env, "could not initialize egl");
       goto err;
    }

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
        doThrowException(env, "no egl config");
        goto err_initialized;
    }

    eglBindAPI(EGL_OPENGL_ES_API);

    EGLint attrib_list[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
    };

    data->context = eglCreateContext(data->display, data->config, EGL_NO_CONTEXT, attrib_list);
    if (!data->context) {
        doThrowException(env, "no egl context");
        goto err_initialized;
    }

    return wrapPointer(env, data);

    err_initialized:
    eglTerminate(data->display);

    err:
    free(data);
    return 0;
}




JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_destroy
  (JNIEnv *env, jclass cls, jobject ptrObject) {
    EGLData *data = unwrapPointer(env, ptrObject);
    if (data) {
        // release
        eglMakeCurrent(data->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

        if (data->surface) {
            eglDestroySurface(data->display, data->surface);
        }

        if (data->context) {
            eglDestroyContext(data->display, data->context);
        }

        eglTerminate(data->display);

        free(data);
    }
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_setSurface
  (JNIEnv *env, jclass cls, jobject ptrObject, jint width, jint height) {
    EGLData *data = unwrapPointer(env, ptrObject);

    // this implementation only allows one surface size
    if (data->width != width || data->height != height) {
        if (data->surface) {
            eglDestroySurface(data->display, data->surface);
        }

        EGLint attribs[] = {
            EGL_WIDTH, width,
            EGL_HEIGHT, height,
            EGL_NONE
        };

        data->surface = eglCreatePbufferSurface(data->display, data->config, attribs);
        if (data->surface == EGL_NO_SURFACE) {
            doThrowException(env, "failed to create pbuffer surface");
            return;
        }

        data->width = width;
        data->height = height;
    }
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bind
  (JNIEnv *env, jclass cls, jobject ptrObject) {
    EGLData *data = unwrapPointer(env, ptrObject);

    if (eglMakeCurrent(data->display, data->surface, data->surface, data->context) == EGL_FALSE) {
        doThrowException(env, "failed to make EGL context current");
        return;
    }

    if (!data->has_extension_list) {
        const char *extensions = (const char *)glGetString(GL_EXTENSIONS);
        if (!extensions) {
            doThrowException(env, "no extensions string");
            return;
        }

        if (strstr(extensions, "GL_EXT_texture_filter_anisotropic")) {
            glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &data->max_anisotropy);
        }

        data->has_extension_list = 1;
    }
}


JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_release
  (JNIEnv *env, jclass cls, jobject ptrObject) {
    EGLData *data = unwrapPointer(env, ptrObject);
    eglMakeCurrent(data->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
}

JNIEXPORT jboolean JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_checkGLError
        (JNIEnv *env, jclass cls, jobject ptrObject) {
    EGLData *data = unwrapPointer(env, ptrObject);

    int err = glGetError();
    if (err) {
        doThrowException(env, gluErrorString(err));
        return 1;
    }

    return 0;
}

// now we'd want something to create a program from two shader sources

int loadShader(const char *source, int isFragment) {
    int shader = glCreateShader(isFragment ? GL_FRAGMENT_SHADER : GL_VERTEX_SHADER);

    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);

    int status;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &status);

    if (status == 0) {
        char info[4096];
        int len;
    	glGetShaderInfoLog(shader, 4095, &len, info);
    	info[len] = 0;
    	fprintf(stderr, "%s shader compilation error: %s", isFragment ? "fragment" : "vertex", info);
    	fflush(stderr);

        return 0;
    }

    return shader;
}

JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_createProgram
        (JNIEnv *env, jclass cls, jobject ptrObject, jstring vertexSourcePtr, jstring fragmentSourcePtr) {
    const char *vertexSource = (*env)->GetStringUTFChars(env, vertexSourcePtr, NULL);
    const char *fragmentSource = (*env)->GetStringUTFChars(env, fragmentSourcePtr, NULL);

    int vertexShader = loadShader(vertexSource, 0);
    int fragmentShader = loadShader(fragmentSource, 1);

    int program = 0;

    if (vertexShader && fragmentShader) {
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);
        int status;
        glGetProgramiv(program, GL_LINK_STATUS, &status);
        if (status == 0) {
            char info[4096];
            int len;
            glGetProgramInfoLog(program, 4095, &len, info);
            info[len] = 0;
            fprintf(stderr, "M3D: createProgram: %s", info);
            fflush(stderr);
            doThrowException(env, "failed to create program");

            glDeleteProgram(program);
            program = 0;
        }
    } else {
        doThrowException(env, "failed to compile shaders");
    }

    // detach? in webgl, you can't call detach as you can do it only on the "used" program

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    (*env)->ReleaseStringUTFChars(env, vertexSourcePtr, vertexSource);
    (*env)->ReleaseStringUTFChars(env, fragmentSourcePtr, fragmentSource);

    return program;
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_useProgram
        (JNIEnv *env, jclass cls, jobject ptrObject, jint program) {
    glUseProgram(program);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_deleteProgram
        (JNIEnv *env, jclass cls, jobject ptrObject, jint program) {
    glDeleteProgram(program);
}

JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_getAttribLocation
        (JNIEnv *env, jclass cls, jobject ptrObject, jint program, jstring name) {
    const char *cName = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = glGetAttribLocation(program, cName);
    (*env)->ReleaseStringUTFChars(env, name, cName);
    return ret;
}

JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_getUniformLocation
        (JNIEnv *env, jclass cls, jobject ptrObject, jint program, jstring name) {
    const char *cName = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = glGetUniformLocation(program, cName);
    (*env)->ReleaseStringUTFChars(env, name, cName);
    return ret;
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_vertexAttribPointer
        (JNIEnv *env, jclass cls, jobject ptrObject, jint attr, jint size, jint type, jboolean normalized, jint stride, jint offset) {
    glVertexAttribPointer(attr, size, type, normalized, stride, offset);
}


JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_vertexAttrib2f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint attr, jfloat f1, jfloat f2) {
    glVertexAttrib2f(attr, f1, f2);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_vertexAttrib3f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint attr, jfloat f1, jfloat f2, jfloat f3) {
    glVertexAttrib3f(attr, f1, f2, f3);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_vertexAttrib4f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint attr, jfloat f1, jfloat f2, jfloat f3, jfloat f4) {
    glVertexAttrib4f(attr, f1, f2, f3, f4);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform1f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloat f1) {
    glUniform1f(loc, f1);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform1i
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jint i1) {
    glUniform1i(loc, i1);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform2f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloat f1, jfloat f2) {
    glUniform2f(loc, f1, f2);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform3f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloat f1, jfloat f2, jfloat f3) {
    glUniform3f(loc, f1, f2, f3);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform4f
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloat f1, jfloat f2, jfloat f3, jfloat f4) {
    glUniform4f(loc, f1, f2, f3, f4);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform3fv
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloatArray fa) {
    jsize length = (*env)->GetArrayLength(env, fa);

    jfloat *cArray = (*env)->GetFloatArrayElements(env, fa, NULL);

    GLsizei count = length / 3;

    glUniform3fv((GLint)loc, count, (const GLfloat *)cArray);

    (*env)->ReleaseFloatArrayElements(env, fa, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniform4fv
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jfloatArray fa) {
    jsize length = (*env)->GetArrayLength(env, fa);

    jfloat *cArray = (*env)->GetFloatArrayElements(env, fa, NULL);

    GLsizei count = length / 4;

    glUniform4fv((GLint)loc, count, (const GLfloat *)cArray);

    (*env)->ReleaseFloatArrayElements(env, fa, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniformMatrix3fv
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jboolean transpose, jfloatArray fa) {
    jsize length = (*env)->GetArrayLength(env, fa);

    jfloat *cArray = (*env)->GetFloatArrayElements(env, fa, NULL);

    GLsizei count = length / 9;

    glUniformMatrix3fv((GLint)loc, count, transpose, (const GLfloat *)cArray);

    (*env)->ReleaseFloatArrayElements(env, fa, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_uniformMatrix4fv
        (JNIEnv *env, jclass cls, jobject ptrObject, jint loc, jboolean transpose, jfloatArray fa) {
    jsize length = (*env)->GetArrayLength(env, fa);

    jfloat *cArray = (*env)->GetFloatArrayElements(env, fa, NULL);

    GLsizei count = length / 16;

    glUniformMatrix4fv((GLint)loc, count, transpose, (const GLfloat *)cArray);

    (*env)->ReleaseFloatArrayElements(env, fa, cArray, JNI_ABORT);
}

JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_createBuffer
        (JNIEnv *env, jclass cls, jobject ptrObject) {

    GLuint buffer = 0;
    glGenBuffers(1, &buffer);

    return buffer;
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_deleteBuffers
        (JNIEnv *env, jclass cls, jobject ptrObject, jintArray idsArray) {
    jsize count = (*env)->GetArrayLength(env, idsArray);

    jint *ids = (*env)->GetIntArrayElements(env, idsArray, NULL);

    glDeleteBuffers(count, ids);

    (*env)->ReleaseIntArrayElements(env, idsArray, ids, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bindBuffer
        (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint id) {
    glBindBuffer(type, id);
}

// we only implement the size variant, we'll specialize subdata

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bufferData
        (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint size, jint usage) {
    glBufferData(type, size, NULL, usage);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bufferSubData__Ljava_lang_Object_2III_3B
  (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint offset, jint byteSize, jbyteArray vmArray) {
    jsize vmSize = (*env)->GetArrayLength(env, vmArray);

    jbyte *cArray = (*env)->GetByteArrayElements(env, vmArray, NULL);

    if (vmSize < byteSize) {
        byteSize = vmSize;
    }

    glBufferSubData(type, offset, byteSize, cArray);

    (*env)->ReleaseByteArrayElements(env, vmArray, cArray, JNI_ABORT);
}


JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bufferSubData__Ljava_lang_Object_2III_3S
  (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint offset, jint byteSize, jshortArray vmArray) {
    jsize vmSize = (*env)->GetArrayLength(env, vmArray) * 2;

    jshort *cArray = (*env)->GetShortArrayElements(env, vmArray, NULL);

    if (vmSize < byteSize) {
        byteSize = vmSize;
    }

    glBufferSubData(type, offset, byteSize, cArray);

    (*env)->ReleaseShortArrayElements(env, vmArray, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bufferSubData__Ljava_lang_Object_2III_3F
  (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint offset, jint byteSize, jfloatArray vmArray) {
    jsize vmSize = (*env)->GetArrayLength(env, vmArray) * 4;

    jfloat *cArray = (*env)->GetFloatArrayElements(env, vmArray, NULL);

    if (vmSize < byteSize) {
        byteSize = vmSize;
    }

    glBufferSubData(type, offset, byteSize, cArray);

    (*env)->ReleaseFloatArrayElements(env, vmArray, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bufferSubData__Ljava_lang_Object_2III_3I
  (JNIEnv *env, jclass cls, jobject ptrObject, jint type, jint offset, jint byteSize, jintArray vmArray) {
    jsize vmSize = (*env)->GetArrayLength(env, vmArray) * 4;

    jint *cArray = (*env)->GetIntArrayElements(env, vmArray, NULL);

    if (vmSize < byteSize) {
        byteSize = vmSize;
    }

    glBufferSubData(type, offset, byteSize, cArray);

    (*env)->ReleaseIntArrayElements(env, vmArray, cArray, JNI_ABORT);
}

// for webgl, this needs mapping
JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_createTexture
        (JNIEnv *env, jclass cls, jobject ptrObject) {
    GLuint id;
    glGenTextures(1, &id);
    return id;
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_deleteTexture
        (JNIEnv *env, jclass cls, jobject ptrObject, jint texture) {
    GLuint id = texture;
    glDeleteTextures(1, &id);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_activeTexture
        (JNIEnv *env, jclass cls, jobject ptrObject, jint texture) {
    glActiveTexture(texture);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_bindTexture
        (JNIEnv *env, jclass cls, jobject ptrObject, jint target, jint texture) {
    glBindTexture(target, texture);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_texParameterf
        (JNIEnv *env, jclass cls, jobject ptrObject, jint target, jint pname, jfloat param) {
    glTexParameterf(target, pname, param);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_texParameteri
        (JNIEnv *env, jclass cls, jobject ptrObject, jint target, jint pname, jint param) {
    glTexParameteri(target, pname, param);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_texImage2D
        (JNIEnv *env, jclass cls, jobject ptrObject, jint target, jint level, jint intFormat, jint width, jint height, jint border, jint format, jint type, jbyteArray javaBytes) {

    jbyte *cArray = (*env)->GetByteArrayElements(env, javaBytes, NULL);

    glTexImage2D(target, level, intFormat, width, height, border, format, type, cArray);

    (*env)->ReleaseByteArrayElements(env, javaBytes, cArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_generateMipmap
        (JNIEnv *env, jclass cls, jobject ptrObject, jint target) {
    glGenerateMipmap(target);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_clear
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mask) {
    glClear(mask);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_clearColor
        (JNIEnv *env, jclass cls, jobject ptrObject, jfloat r, jfloat g, jfloat b, jfloat a) {
    glClearColor(r, g, b, a);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_clearDepthf
        (JNIEnv *env, jclass cls, jobject ptrObject, jfloat depth) {
    glClearDepthf(depth);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_colorMask
        (JNIEnv *env, jclass cls, jobject ptrObject, jboolean r, jboolean g, jboolean b, jboolean a) {
    glColorMask(r, g, b, a);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_cullFace
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode) {
    glCullFace(mode);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_depthFunc
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode) {
    glDepthFunc(mode);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_depthMask
        (JNIEnv *env, jclass cls, jobject ptrObject, jboolean flag) {
    glDepthMask(flag);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_disable
        (JNIEnv *env, jclass cls, jobject ptrObject, jint flag) {
    glDisable(flag);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_disableVertexAttribArray
        (JNIEnv *env, jclass cls, jobject ptrObject, jint index) {
    glDisableVertexAttribArray(index);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_drawArrays
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode, jint first, jint count) {
    glDrawArrays(mode, first, count);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_drawElements
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode, jint count, jint type, jint offset) {
    glDrawElements(mode, count, type, (void *)offset);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_enable
        (JNIEnv *env, jclass cls, jobject ptrObject, jint flag) {
    glEnable(flag);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_enableVertexAttribArray
        (JNIEnv *env, jclass cls, jobject ptrObject, jint index) {
    glEnableVertexAttribArray(index);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_finish
        (JNIEnv *env, jclass cls, jobject ptrObject) {
    glFinish();
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_frontFace
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode) {
    glFrontFace(mode);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_pixelStorei
        (JNIEnv *env, jclass cls, jobject ptrObject, jint pname, jint param) {
    glPixelStorei(pname, param);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_polygonOffset
        (JNIEnv *env, jclass cls, jobject ptrObject, jfloat factor, jfloat units) {
    glPolygonOffset(factor, units);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_readPixels
        (JNIEnv *env, jclass cls, jobject ptrObject, jint x, jint y, jint width, jint height, jbyteArray pixels) {
    // buffer must be preallocated, width*height*4
    jbyte *cArray = (*env)->GetByteArrayElements(env, pixels, NULL);

    glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, cArray);

    (*env)->ReleaseByteArrayElements(env, pixels, cArray, JNI_COMMIT);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_scissor
        (JNIEnv *env, jclass cls, jobject ptrObject, jint x, jint y, jint width, jint height) {
    glScissor(x, y, width, height);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_viewport
        (JNIEnv *env, jclass cls, jobject ptrObject, jint x, jint y, jint width, jint height) {
    glViewport(x, y, width, height);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_blendColor
        (JNIEnv *env, jclass cls, jobject ptrObject, jfloat r, jfloat g, jfloat b, jfloat a) {
    glBlendColor(r, g, b, a);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_blendEquation
        (JNIEnv *env, jclass cls, jobject ptrObject, jint mode) {
    glBlendEquation(mode);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_blendFunc
        (JNIEnv *env, jclass cls, jobject ptrObject, jint sfactor, jint dfactor) {
    glBlendFunc(sfactor, dfactor);
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_blendFuncSeparate
        (JNIEnv *env, jclass cls, jobject ptrObject, jint srcRGB, jint dstRGB, jint srcAlpha, jint dstAlpha) {
    glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
}

JNIEXPORT jboolean JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_isTexture
        (JNIEnv *env, jclass cls, jobject ptrObject, jint texture) {
    return glIsTexture(texture);
}


JNIEXPORT void JNICALL Java_pl_zb3_freej2me_bridge_gles2_GLES2_toggleAnisotropy
        (JNIEnv *env, jclass cls, jobject ptrObject, jboolean enable) {

    EGLData *data = unwrapPointer(env, ptrObject);


    if (data->max_anisotropy) {
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, enable ? data->max_anisotropy : 0.0f);
    }
}

// umm.. depthrangef still exists in webgl and gles20.. maybe it didn't exist in GL20 or something?
