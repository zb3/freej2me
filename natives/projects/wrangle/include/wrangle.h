#ifndef __ANGLE_WRANGLE_H
#define __ANGLE_WRANGLE_H

#include <wrangle_egl_internal.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifdef WRANGLE_EXPORT
    #ifdef _WIN32
        #define WRANGLEAPI __declspec(dllexport)
    #else
        #define WRANGLEAPI __attribute__((visibility("default")))
    #endif
#else
    #ifdef _WIN32
        #define WRANGLEAPI __declspec(dllimport)
    #else
        #define WRANGLEAPI
    #endif
#endif

#if defined(_WIN32) && !defined(_WIN32_WCE)
    #define WRANGLECALL __stdcall
#else
    #define WRANGLECALL
#endif

#define eglGetDisplay __wrangle_egl_helper_get_display
#define eglInitialize __wrangle_egl_helper_initialize
#define eglTerminate __wrangle_egl_helper_terminate

WRANGLEAPI EGLDisplay WRANGLECALL eglGetDisplay(EGLNativeDisplayType display_id);
WRANGLEAPI EGLBoolean WRANGLECALL eglInitialize(EGLDisplay dpy, EGLint *major, EGLint *minor);
WRANGLEAPI EGLBoolean WRANGLECALL eglTerminate(EGLDisplay dpy);

// since support needs to be known at the time eglGetDisplay is called
WRANGLEAPI void WRANGLECALL wrangleHintGLESVersion(int version);


#ifdef __cplusplus
}
#endif

#endif
