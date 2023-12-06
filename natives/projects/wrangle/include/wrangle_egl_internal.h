#ifndef __ANGLE_WRANGLE_EGL_INTERNAL_H
#define __ANGLE_WRANGLE_EGL_INTERNAL_H

#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <EGL/eglext_angle.h>

#ifdef __cplusplus
extern "C" {
#endif

#if defined(_WIN32) && defined(__i386__)

#define EGL_ChooseConfig _EGL_ChooseConfig
#define EGL_CopyBuffers _EGL_CopyBuffers
#define EGL_CreateContext _EGL_CreateContext
#define EGL_CreatePbufferSurface _EGL_CreatePbufferSurface
#define EGL_CreatePixmapSurface _EGL_CreatePixmapSurface
#define EGL_CreateWindowSurface _EGL_CreateWindowSurface
#define EGL_DestroyContext _EGL_DestroyContext
#define EGL_DestroySurface _EGL_DestroySurface
#define EGL_GetConfigAttrib _EGL_GetConfigAttrib
#define EGL_GetConfigs _EGL_GetConfigs
#define EGL_GetCurrentDisplay _EGL_GetCurrentDisplay
#define EGL_GetCurrentSurface _EGL_GetCurrentSurface
#define EGL_GetDisplay _EGL_GetDisplay
#define EGL_GetError _EGL_GetError
#define EGL_GetProcAddress _EGL_GetProcAddress
#define EGL_Initialize _EGL_Initialize
#define EGL_MakeCurrent _EGL_MakeCurrent
#define EGL_QueryContext _EGL_QueryContext
#define EGL_QueryString _EGL_QueryString
#define EGL_QuerySurface _EGL_QuerySurface
#define EGL_SwapBuffers _EGL_SwapBuffers
#define EGL_Terminate _EGL_Terminate
#define EGL_WaitGL _EGL_WaitGL
#define EGL_WaitNative _EGL_WaitNative
#define EGL_BindTexImage _EGL_BindTexImage
#define EGL_ReleaseTexImage _EGL_ReleaseTexImage
#define EGL_SurfaceAttrib _EGL_SurfaceAttrib
#define EGL_SwapInterval _EGL_SwapInterval
#define EGL_BindAPI _EGL_BindAPI
#define EGL_CreatePbufferFromClientBuffer _EGL_CreatePbufferFromClientBuffer
#define EGL_QueryAPI _EGL_QueryAPI
#define EGL_ReleaseThread _EGL_ReleaseThread
#define EGL_WaitClient _EGL_WaitClient
#define EGL_GetCurrentContext _EGL_GetCurrentContext
#define EGL_ClientWaitSync _EGL_ClientWaitSync
#define EGL_CreateImage _EGL_CreateImage
#define EGL_CreatePlatformPixmapSurface _EGL_CreatePlatformPixmapSurface
#define EGL_CreatePlatformWindowSurface _EGL_CreatePlatformWindowSurface
#define EGL_CreateSync _EGL_CreateSync
#define EGL_DestroyImage _EGL_DestroyImage
#define EGL_DestroySync _EGL_DestroySync
#define EGL_GetPlatformDisplay _EGL_GetPlatformDisplay
#define EGL_GetSyncAttrib _EGL_GetSyncAttrib
#define EGL_WaitSync _EGL_WaitSync

#endif

EGLBoolean EGLAPIENTRY EGL_ChooseConfig(EGLDisplay dpy,
                                                     const EGLint *attrib_list,
                                                     EGLConfig *configs,
                                                     EGLint config_size,
                                                     EGLint *num_config);
EGLBoolean EGLAPIENTRY EGL_CopyBuffers(EGLDisplay dpy,
                                                    EGLSurface surface,
                                                    EGLNativePixmapType target);
EGLContext EGLAPIENTRY EGL_CreateContext(EGLDisplay dpy,
                                                      EGLConfig config,
                                                      EGLContext share_context,
                                                      const EGLint *attrib_list);
EGLSurface EGLAPIENTRY EGL_CreatePbufferSurface(EGLDisplay dpy,
                                                             EGLConfig config,
                                                             const EGLint *attrib_list);
EGLSurface EGLAPIENTRY EGL_CreatePixmapSurface(EGLDisplay dpy,
                                                            EGLConfig config,
                                                            EGLNativePixmapType pixmap,
                                                            const EGLint *attrib_list);
EGLSurface EGLAPIENTRY EGL_CreateWindowSurface(EGLDisplay dpy,
                                                            EGLConfig config,
                                                            EGLNativeWindowType win,
                                                            const EGLint *attrib_list);
EGLBoolean EGLAPIENTRY EGL_DestroyContext(EGLDisplay dpy, EGLContext ctx);
EGLBoolean EGLAPIENTRY EGL_DestroySurface(EGLDisplay dpy, EGLSurface surface);
EGLBoolean EGLAPIENTRY EGL_GetConfigAttrib(EGLDisplay dpy,
                                                        EGLConfig config,
                                                        EGLint attribute,
                                                        EGLint *value);
EGLBoolean EGLAPIENTRY EGL_GetConfigs(EGLDisplay dpy,
                                                   EGLConfig *configs,
                                                   EGLint config_size,
                                                   EGLint *num_config);
EGLDisplay EGLAPIENTRY EGL_GetCurrentDisplay();
EGLSurface EGLAPIENTRY EGL_GetCurrentSurface(EGLint readdraw);
EGLDisplay EGLAPIENTRY EGL_GetDisplay(EGLNativeDisplayType display_id);
EGLint EGLAPIENTRY EGL_GetError();
__eglMustCastToProperFunctionPointerType EGLAPIENTRY EGL_GetProcAddress(const char *procname);
EGLBoolean EGLAPIENTRY EGL_Initialize(EGLDisplay dpy, EGLint *major, EGLint *minor);
EGLBoolean EGLAPIENTRY EGL_MakeCurrent(EGLDisplay dpy,
                                                    EGLSurface draw,
                                                    EGLSurface read,
                                                    EGLContext ctx);
EGLBoolean EGLAPIENTRY EGL_QueryContext(EGLDisplay dpy,
                                                     EGLContext ctx,
                                                     EGLint attribute,
                                                     EGLint *value);
const char EGLAPIENTRY *EGL_QueryString(EGLDisplay dpy, EGLint name);
EGLBoolean EGLAPIENTRY EGL_QuerySurface(EGLDisplay dpy,
                                                     EGLSurface surface,
                                                     EGLint attribute,
                                                     EGLint *value);
EGLBoolean EGLAPIENTRY EGL_SwapBuffers(EGLDisplay dpy, EGLSurface surface);
EGLBoolean EGLAPIENTRY EGL_Terminate(EGLDisplay dpy);
EGLBoolean EGLAPIENTRY EGL_WaitGL();
EGLBoolean EGLAPIENTRY EGL_WaitNative(EGLint engine);

// EGL 1.1
EGLBoolean EGLAPIENTRY EGL_BindTexImage(EGLDisplay dpy,
                                                     EGLSurface surface,
                                                     EGLint buffer);
EGLBoolean EGLAPIENTRY EGL_ReleaseTexImage(EGLDisplay dpy,
                                                        EGLSurface surface,
                                                        EGLint buffer);
EGLBoolean EGLAPIENTRY EGL_SurfaceAttrib(EGLDisplay dpy,
                                                      EGLSurface surface,
                                                      EGLint attribute,
                                                      EGLint value);
EGLBoolean EGLAPIENTRY EGL_SwapInterval(EGLDisplay dpy, EGLint interval);

// EGL 1.2
EGLBoolean EGLAPIENTRY EGL_BindAPI(EGLenum api);
EGLSurface EGLAPIENTRY EGL_CreatePbufferFromClientBuffer(EGLDisplay dpy,
                                                                      EGLenum buftype,
                                                                      EGLClientBuffer buffer,
                                                                      EGLConfig config,
                                                                      const EGLint *attrib_list);
EGLenum EGLAPIENTRY EGL_QueryAPI();
EGLBoolean EGLAPIENTRY EGL_ReleaseThread();
EGLBoolean EGLAPIENTRY EGL_WaitClient();


// EGL 1.4
EGLContext EGLAPIENTRY EGL_GetCurrentContext();

// EGL 1.5
EGLint EGLAPIENTRY EGL_ClientWaitSync(EGLDisplay dpy,
                                                   EGLSync sync,
                                                   EGLint flags,
                                                   EGLTime timeout);
EGLImage EGLAPIENTRY EGL_CreateImage(EGLDisplay dpy,
                                                  EGLContext ctx,
                                                  EGLenum target,
                                                  EGLClientBuffer buffer,
                                                  const EGLAttrib *attrib_list);
EGLSurface EGLAPIENTRY EGL_CreatePlatformPixmapSurface(EGLDisplay dpy,
                                                                    EGLConfig config,
                                                                    void *native_pixmap,
                                                                    const EGLAttrib *attrib_list);
EGLSurface EGLAPIENTRY EGL_CreatePlatformWindowSurface(EGLDisplay dpy,
                                                                    EGLConfig config,
                                                                    void *native_window,
                                                                    const EGLAttrib *attrib_list);
EGLSync EGLAPIENTRY EGL_CreateSync(EGLDisplay dpy,
                                                EGLenum type,
                                                const EGLAttrib *attrib_list);
EGLBoolean EGLAPIENTRY EGL_DestroyImage(EGLDisplay dpy, EGLImage image);
EGLBoolean EGLAPIENTRY EGL_DestroySync(EGLDisplay dpy, EGLSync sync);
EGLDisplay EGLAPIENTRY EGL_GetPlatformDisplay(EGLenum platform,
                                                           void *native_display,
                                                           const EGLAttrib *attrib_list);
EGLBoolean EGLAPIENTRY EGL_GetSyncAttrib(EGLDisplay dpy,
                                                      EGLSync sync,
                                                      EGLint attribute,
                                                      EGLAttrib *value);
EGLBoolean EGLAPIENTRY EGL_WaitSync(EGLDisplay dpy, EGLSync sync, EGLint flags);






#define eglChooseConfig EGL_ChooseConfig
#define eglCopyBuffers EGL_CopyBuffers
#define eglCreateContext EGL_CreateContext
#define eglCreatePbufferSurface EGL_CreatePbufferSurface
#define eglCreatePixmapSurface EGL_CreatePixmapSurface
#define eglCreateWindowSurface EGL_CreateWindowSurface
#define eglDestroyContext EGL_DestroyContext
#define eglDestroySurface EGL_DestroySurface
#define eglGetConfigAttrib EGL_GetConfigAttrib
#define eglGetConfigs EGL_GetConfigs
#define eglGetCurrentDisplay EGL_GetCurrentDisplay
#define eglGetCurrentSurface EGL_GetCurrentSurface
#define eglGetError EGL_GetError
#define eglGetProcAddress EGL_GetProcAddress
#define eglMakeCurrent EGL_MakeCurrent
#define eglQueryContext EGL_QueryContext
#define eglQueryString EGL_QueryString
#define eglQuerySurface EGL_QuerySurface
#define eglSwapBuffers EGL_SwapBuffers
#define eglWaitGL EGL_WaitGL
#define eglWaitNative EGL_WaitNative
#define eglBindTexImage EGL_BindTexImage
#define eglReleaseTexImage EGL_ReleaseTexImage
#define eglSurfaceAttrib EGL_SurfaceAttrib
#define eglSwapInterval EGL_SwapInterval
#define eglBindAPI EGL_BindAPI
#define eglQueryAPI EGL_QueryAPI
#define eglCreatePbufferFromClientBuffer EGL_CreatePbufferFromClientBuffer
#define eglReleaseThread EGL_ReleaseThread
#define eglWaitClient EGL_WaitClient
#define eglGetCurrentContext EGL_GetCurrentContext
#define eglCreateSync EGL_CreateSync
#define eglDestroySync EGL_DestroySync
#define eglClientWaitSync EGL_ClientWaitSync
#define eglGetSyncAttrib EGL_GetSyncAttrib
#define eglCreateImage EGL_CreateImage
#define eglDestroyImage EGL_DestroyImage
#define eglGetPlatformDisplay EGL_GetPlatformDisplay
#define eglCreatePlatformWindowSurface EGL_CreatePlatformWindowSurface
#define eglCreatePlatformPixmapSurface EGL_CreatePlatformPixmapSurface
#define eglWaitSync EGL_WaitSync
#define eglSetBlobCacheFuncsANDROID EGL_SetBlobCacheFuncsANDROID
#define eglCreateNativeClientBufferANDROID EGL_CreateNativeClientBufferANDROID
#define eglGetCompositorTimingANDROID EGL_GetCompositorTimingANDROID
#define eglGetCompositorTimingSupportedANDROID EGL_GetCompositorTimingSupportedANDROID
#define eglGetFrameTimestampSupportedANDROID EGL_GetFrameTimestampSupportedANDROID
#define eglGetFrameTimestampsANDROID EGL_GetFrameTimestampsANDROID
#define eglGetNextFrameIdANDROID EGL_GetNextFrameIdANDROID
#define eglGetNativeClientBufferANDROID EGL_GetNativeClientBufferANDROID
#define eglDupNativeFenceFDANDROID EGL_DupNativeFenceFDANDROID
#define eglPresentationTimeANDROID EGL_PresentationTimeANDROID
#define eglCreateDeviceANGLE EGL_CreateDeviceANGLE
#define eglReleaseDeviceANGLE EGL_ReleaseDeviceANGLE
#define eglAcquireExternalContextANGLE EGL_AcquireExternalContextANGLE
#define eglReleaseExternalContextANGLE EGL_ReleaseExternalContextANGLE
#define eglQueryDisplayAttribANGLE EGL_QueryDisplayAttribANGLE
#define eglQueryStringiANGLE EGL_QueryStringiANGLE
#define eglCopyMetalSharedEventANGLE EGL_CopyMetalSharedEventANGLE
#define eglForceGPUSwitchANGLE EGL_ForceGPUSwitchANGLE
#define eglHandleGPUSwitchANGLE EGL_HandleGPUSwitchANGLE
#define eglReacquireHighPowerGPUANGLE EGL_ReacquireHighPowerGPUANGLE
#define eglReleaseHighPowerGPUANGLE EGL_ReleaseHighPowerGPUANGLE
#define eglPrepareSwapBuffersANGLE EGL_PrepareSwapBuffersANGLE
#define eglProgramCacheGetAttribANGLE EGL_ProgramCacheGetAttribANGLE
#define eglProgramCachePopulateANGLE EGL_ProgramCachePopulateANGLE
#define eglProgramCacheQueryANGLE EGL_ProgramCacheQueryANGLE
#define eglProgramCacheResizeANGLE EGL_ProgramCacheResizeANGLE
#define eglQuerySurfacePointerANGLE EGL_QuerySurfacePointerANGLE
#define eglCreateStreamProducerD3DTextureANGLE EGL_CreateStreamProducerD3DTextureANGLE
#define eglStreamPostD3DTextureANGLE EGL_StreamPostD3DTextureANGLE
#define eglSwapBuffersWithFrameTokenANGLE EGL_SwapBuffersWithFrameTokenANGLE
#define eglGetMscRateANGLE EGL_GetMscRateANGLE
#define eglExportVkImageANGLE EGL_ExportVkImageANGLE
#define eglWaitUntilWorkScheduledANGLE EGL_WaitUntilWorkScheduledANGLE
#define eglGetSyncValuesCHROMIUM EGL_GetSyncValuesCHROMIUM
#define eglQueryDeviceAttribEXT EGL_QueryDeviceAttribEXT
#define eglQueryDeviceStringEXT EGL_QueryDeviceStringEXT
#define eglQueryDisplayAttribEXT EGL_QueryDisplayAttribEXT
#define eglQueryDmaBufFormatsEXT EGL_QueryDmaBufFormatsEXT
#define eglQueryDmaBufModifiersEXT EGL_QueryDmaBufModifiersEXT
#define eglCreatePlatformPixmapSurfaceEXT EGL_CreatePlatformPixmapSurfaceEXT
#define eglCreatePlatformWindowSurfaceEXT EGL_CreatePlatformWindowSurfaceEXT
#define eglGetPlatformDisplayEXT EGL_GetPlatformDisplayEXT
#define eglDebugMessageControlKHR EGL_DebugMessageControlKHR
#define eglLabelObjectKHR EGL_LabelObjectKHR
#define eglQueryDebugKHR EGL_QueryDebugKHR
#define eglClientWaitSyncKHR EGL_ClientWaitSyncKHR
#define eglCreateSyncKHR EGL_CreateSyncKHR
#define eglDestroySyncKHR EGL_DestroySyncKHR
#define eglGetSyncAttribKHR EGL_GetSyncAttribKHR
#define eglCreateImageKHR EGL_CreateImageKHR
#define eglDestroyImageKHR EGL_DestroyImageKHR
#define eglLockSurfaceKHR EGL_LockSurfaceKHR
#define eglQuerySurface64KHR EGL_QuerySurface64KHR
#define eglUnlockSurfaceKHR EGL_UnlockSurfaceKHR
#define eglSetDamageRegionKHR EGL_SetDamageRegionKHR
#define eglSignalSyncKHR EGL_SignalSyncKHR
#define eglCreateStreamKHR EGL_CreateStreamKHR
#define eglDestroyStreamKHR EGL_DestroyStreamKHR
#define eglQueryStreamKHR EGL_QueryStreamKHR
#define eglQueryStreamu64KHR EGL_QueryStreamu64KHR
#define eglStreamAttribKHR EGL_StreamAttribKHR
#define eglStreamConsumerAcquireKHR EGL_StreamConsumerAcquireKHR
#define eglStreamConsumerGLTextureExternalKHR EGL_StreamConsumerGLTextureExternalKHR
#define eglStreamConsumerReleaseKHR EGL_StreamConsumerReleaseKHR
#define eglSwapBuffersWithDamageKHR EGL_SwapBuffersWithDamageKHR
#define eglWaitSyncKHR EGL_WaitSyncKHR
#define eglPostSubBufferNV EGL_PostSubBufferNV
#define eglStreamConsumerGLTextureExternalAttribsNV EGL_StreamConsumerGLTextureExternalAttribsNV

#ifdef __cplusplus
}
#endif

#endif