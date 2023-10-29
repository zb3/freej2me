//#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <GLES/egl.h>
#include <GLES/gl.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>

//#include <pthread.h>
//#include <zlib.h>


EGLDisplay eglDisplay;
EGLContext eglContext;
EGLSurface eglSurface;

const char* GetEGLErrorString(uint32_t error) {
    switch (error) {
        case EGL_SUCCESS:
            return "EGL_SUCCESS";
        case EGL_NOT_INITIALIZED:
            return "EGL_NOT_INITIALIZED";
        case EGL_BAD_ACCESS:
            return "EGL_BAD_ACCESS";
        case EGL_BAD_ALLOC:
            return "EGL_BAD_ALLOC";
        case EGL_BAD_ATTRIBUTE:
            return "EGL_BAD_ATTRIBUTE";
        case EGL_BAD_CONFIG:
            return "EGL_BAD_CONFIG";
        case EGL_BAD_CONTEXT:
            return "EGL_BAD_CONTEXT";
        case EGL_BAD_CURRENT_SURFACE:
            return "EGL_BAD_CURRENT_SURFACE";
        case EGL_BAD_DISPLAY:
            return "EGL_BAD_DISPLAY";
        case EGL_BAD_MATCH:
            return "EGL_BAD_MATCH";
        case EGL_BAD_NATIVE_PIXMAP:
            return "EGL_BAD_NATIVE_PIXMAP";
        case EGL_BAD_NATIVE_WINDOW:
            return "EGL_BAD_NATIVE_WINDOW";
        case EGL_BAD_PARAMETER:
            return "EGL_BAD_PARAMETER";
        case EGL_BAD_SURFACE:
            return "EGL_BAD_SURFACE";
        case EGL_CONTEXT_LOST:
            return "EGL_CONTEXT_LOST";
        default:
            return "UNKNOWN";
    }
}

// Returns the last EGL error as a string.
const char* GetLastEGLErrorString() {
    return GetEGLErrorString(eglGetError());
}
void checkEglError(const char* message) {
    EGLint error = eglGetError();
    if (error != EGL_SUCCESS) {
        fprintf(stderr, "%s: EGL error 0x%X\n", message, error);
        exit(EXIT_FAILURE);
    }
}



int main() {
    eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (eglDisplay == EGL_NO_DISPLAY) {
        fprintf(stderr, "Failed to get EGL display\n");
        exit(EXIT_FAILURE);
    }

    if (eglInitialize(eglDisplay, NULL, NULL) == EGL_FALSE) {
        fprintf(stderr, "Failed to initialize EGL\n");
        exit(EXIT_FAILURE);
    }

    // Choose an EGL configuration
    EGLConfig eglConfig;
    EGLint numConfigs;
    EGLint configAttribs[] = {
        EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_BIT,
        EGL_NONE
    };
    if (eglChooseConfig(eglDisplay, configAttribs, &eglConfig, 1, &numConfigs) == EGL_FALSE || numConfigs == 0) {
        fprintf(stderr, "Failed to choose EGL configuration\n");
        exit(EXIT_FAILURE);
    }

    // Create a PBuffer surface
    EGLint pbufferAttribs[] = { EGL_WIDTH, 640, EGL_HEIGHT, 480, EGL_NONE };
    eglSurface = eglCreatePbufferSurface(eglDisplay, eglConfig, pbufferAttribs);
    if (eglSurface == EGL_NO_SURFACE) {
        fprintf(stderr, "Failed to create EGL PBuffer surface\n");
        exit(EXIT_FAILURE);
    }

    eglBindAPI(EGL_OPENGL_ES_API);

    // Create an EGL context
    EGLint contextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 1,
        EGL_CONTEXT_MINOR_VERSION, 1,
        EGL_NONE
    };

    eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttribs);
    if (eglContext == EGL_NO_CONTEXT) {
        fprintf(stderr, "Failed to create EGL context\n");
        fprintf(stderr, "%s\n", GetLastEGLErrorString());
        exit(EXIT_FAILURE);
    }

    // Make the context current
    if (eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == EGL_FALSE) {
        fprintf(stderr, "Failed to make EGL context current\n");
        exit(EXIT_FAILURE);
    }

    checkEglError("EGL Initialization");

    // OpenGL rendering
    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // Read pixels into an array
    GLint viewport[4];
    glGetIntegerv(GL_VIEWPORT, viewport);
    unsigned char* pixels = (unsigned char*)malloc(viewport[2] * viewport[3] * 4);
    glReadPixels(0, 0, viewport[2], viewport[3], GL_RGBA, GL_UNSIGNED_BYTE, pixels);



    checkEglError("OpenGL Rendering");

    // You now have the pixel data in the 'pixels' array

    // Cleanup
    eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    checkEglError("EGL Cleanup");

    eglDestroyContext(eglDisplay, eglContext);
    checkEglError("EGL Context Cleanup");

    eglDestroySurface(eglDisplay, eglSurface);
    checkEglError("EGL Surface Cleanup");

    eglTerminate(eglDisplay);
    checkEglError("EGL Termination");

    free(pixels);

    return 0;
}
