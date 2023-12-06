#include <stdio.h>

#include <wrangle.h>

#define logmsg(...) do {fprintf(stderr, "wrangle: ");fprintf(stderr, __VA_ARGS__);fwrite("\n", 1, 1, stderr);fflush(stdout);} while (0)

static struct WrappedDisplay {
    EGLDisplay display;
    EGLint major;
    EGLint minor;
    int initialized;
} __defaultEGLDisplay = {};

static int __requestedGLESVersion = 1;

static EGLDisplay getNativeDisplay() {
    EGLDisplay display = EGL_GetDisplay(EGL_DEFAULT_DISPLAY);

    if (display != EGL_NO_DISPLAY && EGL_Initialize(display, &__defaultEGLDisplay.major, &__defaultEGLDisplay.minor)) {
        return display;
    }

    #if defined(_WIN32) || defined (_WIN64)
    // second chance - D3D9

    EGLAttrib d3d_attribs[] = {
        EGL_PLATFORM_ANGLE_TYPE_ANGLE,
        EGL_PLATFORM_ANGLE_TYPE_D3D9_ANGLE,
        EGL_NONE
    };

    display = EGL_GetPlatformDisplay(EGL_PLATFORM_ANGLE_ANGLE, EGL_DEFAULT_DISPLAY, d3d_attribs);

    if (display != EGL_NO_DISPLAY && EGL_Initialize(display, &__defaultEGLDisplay.major, &__defaultEGLDisplay.minor)) {
        logmsg("using D3D9");
        return display;
    }

    #endif

    return EGL_NO_DISPLAY;
}

static int checkGLES3Supported(EGLDisplay display) {
    EGLConfig config;
    EGLint config_attribs[] = {
        EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
        EGL_NONE
    };

    EGLint numConfigs;
    if (!EGL_ChooseConfig(display, config_attribs, &config, 1, &numConfigs)) {
        logmsg("eglChooseConfig failed");
        return 0;
    }

    EGL_BindAPI(EGL_OPENGL_ES_API);

    EGLContext context = EGL_CreateContext(display, config, EGL_NO_CONTEXT, NULL);
    if (!context) {
        logmsg("eglCreateContext failed");
        return 0;
    }
    
    if (!EGL_MakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, context)) {
        logmsg("eglMakeCurrent failed");
        display = EGL_NO_DISPLAY;
        goto clean_ctx;
    }
    
    EGL_MakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

    clean_ctx:
    eglDestroyContext(display, context);

    return display != EGL_NO_DISPLAY;
}

WRANGLEAPI void WRANGLECALL wrangleHintGLESVersion(int version) {
    __requestedGLESVersion = version;
}

WRANGLEAPI EGLDisplay WRANGLECALL eglGetDisplay(EGLNativeDisplayType display_id) {
    if (display_id != EGL_DEFAULT_DISPLAY) {
        return EGL_GetDisplay(display_id);
    }

    if (__defaultEGLDisplay.initialized) {
        return __defaultEGLDisplay.display;
    }

    EGLDisplay nativeDisplay = getNativeDisplay();

    #if !defined(_WIN32) && !defined(__APPLE__)
    if (nativeDisplay && (__requestedGLESVersion == 1 || __requestedGLESVersion == 3)) {
        // on linux if we want GLES1 or GLES3, we need to check if it's really supported
        // GLES1 is emulated via GLES3
        if (!checkGLES3Supported(nativeDisplay)) {
            logmsg("GLES3 not supported");
            EGL_Terminate(nativeDisplay);
            nativeDisplay = EGL_NO_DISPLAY;
        }

    }
    #endif
    
    if (nativeDisplay) {
        __defaultEGLDisplay.display = nativeDisplay;
        __defaultEGLDisplay.initialized = 1;

        return nativeDisplay;
    } else {
        EGLAttrib ss_attribs[] = {
            EGL_PLATFORM_ANGLE_TYPE_ANGLE,
            EGL_PLATFORM_ANGLE_TYPE_VULKAN_ANGLE,
            EGL_PLATFORM_ANGLE_DEVICE_TYPE_ANGLE,
            EGL_PLATFORM_ANGLE_DEVICE_TYPE_SWIFTSHADER_ANGLE,
            EGL_NONE
        };
    
        EGLDisplay display = EGL_GetPlatformDisplay(EGL_PLATFORM_ANGLE_ANGLE, EGL_DEFAULT_DISPLAY, ss_attribs);
    
        if (display != EGL_NO_DISPLAY && EGL_Initialize(display, &__defaultEGLDisplay.major, &__defaultEGLDisplay.minor)) {
            logmsg("Using swiftshader fallback");
            __defaultEGLDisplay.display = display;
            __defaultEGLDisplay.initialized = 1;
    
            return display;
        }
    }    
}

WRANGLEAPI EGLBoolean WRANGLECALL eglInitialize(EGLDisplay dpy, EGLint *major, EGLint *minor) {
    if (dpy != __defaultEGLDisplay.display) {
        return EGL_Initialize(dpy, major, minor);
    }

    if (!__defaultEGLDisplay.initialized) {
        return EGL_FALSE;
    }

    if (major) *major = __defaultEGLDisplay.major;
    if (minor) *minor = __defaultEGLDisplay.minor;
    return EGL_TRUE;
}

WRANGLEAPI EGLBoolean WRANGLECALL eglTerminate(EGLDisplay dpy) {
    if (dpy != __defaultEGLDisplay.display) {
        return EGL_Terminate(dpy);
    }
    
    if (__defaultEGLDisplay.initialized) {
        EGL_Terminate(__defaultEGLDisplay.display);
        __defaultEGLDisplay.initialized = 0;
    }
}
