#ifndef __ANGLE_WRANGLE_GLES_INTERNAL_H
#define __ANGLE_WRANGLE_GLES_INTERNAL_H

#ifdef __cplusplus
extern "C" {
#endif

#if defined(_WIN32) && defined(__i386__)

#define GL_AlphaFunc _GL_AlphaFunc
#define GL_AlphaFuncx _GL_AlphaFuncx
#define GL_ClearColorx _GL_ClearColorx
#define GL_ClearDepthx _GL_ClearDepthx
#define GL_ClientActiveTexture _GL_ClientActiveTexture
#define GL_ClipPlanef _GL_ClipPlanef
#define GL_ClipPlanex _GL_ClipPlanex
#define GL_Color4f _GL_Color4f
#define GL_Color4ub _GL_Color4ub
#define GL_Color4x _GL_Color4x
#define GL_ColorPointer _GL_ColorPointer
#define GL_DepthRangex _GL_DepthRangex
#define GL_DisableClientState _GL_DisableClientState
#define GL_EnableClientState _GL_EnableClientState
#define GL_Fogf _GL_Fogf
#define GL_Fogfv _GL_Fogfv
#define GL_Fogx _GL_Fogx
#define GL_Fogxv _GL_Fogxv
#define GL_Frustumf _GL_Frustumf
#define GL_Frustumx _GL_Frustumx
#define GL_GetClipPlanef _GL_GetClipPlanef
#define GL_GetClipPlanex _GL_GetClipPlanex
#define GL_GetFixedv _GL_GetFixedv
#define GL_GetLightfv _GL_GetLightfv
#define GL_GetLightxv _GL_GetLightxv
#define GL_GetMaterialfv _GL_GetMaterialfv
#define GL_GetMaterialxv _GL_GetMaterialxv
#define GL_GetTexEnvfv _GL_GetTexEnvfv
#define GL_GetTexEnviv _GL_GetTexEnviv
#define GL_GetTexEnvxv _GL_GetTexEnvxv
#define GL_GetTexParameterxv _GL_GetTexParameterxv
#define GL_LightModelf _GL_LightModelf
#define GL_LightModelfv _GL_LightModelfv
#define GL_LightModelx _GL_LightModelx
#define GL_LightModelxv _GL_LightModelxv
#define GL_Lightf _GL_Lightf
#define GL_Lightfv _GL_Lightfv
#define GL_Lightx _GL_Lightx
#define GL_Lightxv _GL_Lightxv
#define GL_LineWidthx _GL_LineWidthx
#define GL_LoadIdentity _GL_LoadIdentity
#define GL_LoadMatrixf _GL_LoadMatrixf
#define GL_LoadMatrixx _GL_LoadMatrixx
#define GL_LogicOp _GL_LogicOp
#define GL_Materialf _GL_Materialf
#define GL_Materialfv _GL_Materialfv
#define GL_Materialx _GL_Materialx
#define GL_Materialxv _GL_Materialxv
#define GL_MatrixMode _GL_MatrixMode
#define GL_MultMatrixf _GL_MultMatrixf
#define GL_MultMatrixx _GL_MultMatrixx
#define GL_MultiTexCoord4f _GL_MultiTexCoord4f
#define GL_MultiTexCoord4x _GL_MultiTexCoord4x
#define GL_Normal3f _GL_Normal3f
#define GL_Normal3x _GL_Normal3x
#define GL_NormalPointer _GL_NormalPointer
#define GL_Orthof _GL_Orthof
#define GL_Orthox _GL_Orthox
#define GL_PointParameterf _GL_PointParameterf
#define GL_PointParameterfv _GL_PointParameterfv
#define GL_PointParameterx _GL_PointParameterx
#define GL_PointParameterxv _GL_PointParameterxv
#define GL_PointSize _GL_PointSize
#define GL_PointSizex _GL_PointSizex
#define GL_PolygonOffsetx _GL_PolygonOffsetx
#define GL_PopMatrix _GL_PopMatrix
#define GL_PushMatrix _GL_PushMatrix
#define GL_Rotatef _GL_Rotatef
#define GL_Rotatex _GL_Rotatex
#define GL_SampleCoveragex _GL_SampleCoveragex
#define GL_Scalef _GL_Scalef
#define GL_Scalex _GL_Scalex
#define GL_ShadeModel _GL_ShadeModel
#define GL_TexCoordPointer _GL_TexCoordPointer
#define GL_TexEnvf _GL_TexEnvf
#define GL_TexEnvfv _GL_TexEnvfv
#define GL_TexEnvi _GL_TexEnvi
#define GL_TexEnviv _GL_TexEnviv
#define GL_TexEnvx _GL_TexEnvx
#define GL_TexEnvxv _GL_TexEnvxv
#define GL_TexParameterx _GL_TexParameterx
#define GL_TexParameterxv _GL_TexParameterxv
#define GL_Translatef _GL_Translatef
#define GL_Translatex _GL_Translatex
#define GL_VertexPointer _GL_VertexPointer
#define GL_ActiveTexture _GL_ActiveTexture
#define GL_AttachShader _GL_AttachShader
#define GL_BindBuffer _GL_BindBuffer
#define GL_BindFramebuffer _GL_BindFramebuffer
#define GL_BindRenderbuffer _GL_BindRenderbuffer
#define GL_BindTexture _GL_BindTexture
#define GL_BlendColor _GL_BlendColor
#define GL_BlendEquation _GL_BlendEquation
#define GL_BlendEquationSeparate _GL_BlendEquationSeparate
#define GL_BlendFunc _GL_BlendFunc
#define GL_BlendFuncSeparate _GL_BlendFuncSeparate
#define GL_BufferData _GL_BufferData
#define GL_BufferSubData _GL_BufferSubData
#define GL_CheckFramebufferStatus _GL_CheckFramebufferStatus
#define GL_Clear _GL_Clear
#define GL_ClearColor _GL_ClearColor
#define GL_ClearDepthf _GL_ClearDepthf
#define GL_ClearStencil _GL_ClearStencil
#define GL_ColorMask _GL_ColorMask
#define GL_CompileShader _GL_CompileShader
#define GL_CompressedTexImage2D _GL_CompressedTexImage2D
#define GL_CompressedTexSubImage2D _GL_CompressedTexSubImage2D
#define GL_CopyTexImage2D _GL_CopyTexImage2D
#define GL_CopyTexSubImage2D _GL_CopyTexSubImage2D
#define GL_CreateProgram _GL_CreateProgram
#define GL_CreateShader _GL_CreateShader
#define GL_CullFace _GL_CullFace
#define GL_DeleteBuffers _GL_DeleteBuffers
#define GL_DeleteFramebuffers _GL_DeleteFramebuffers
#define GL_DeleteProgram _GL_DeleteProgram
#define GL_DeleteRenderbuffers _GL_DeleteRenderbuffers
#define GL_DeleteShader _GL_DeleteShader
#define GL_DeleteTextures _GL_DeleteTextures
#define GL_DepthFunc _GL_DepthFunc
#define GL_DepthMask _GL_DepthMask
#define GL_DepthRangef _GL_DepthRangef
#define GL_DetachShader _GL_DetachShader
#define GL_Disable _GL_Disable
#define GL_DisableVertexAttribArray _GL_DisableVertexAttribArray
#define GL_DrawArrays _GL_DrawArrays
#define GL_DrawElements _GL_DrawElements
#define GL_Enable _GL_Enable
#define GL_EnableVertexAttribArray _GL_EnableVertexAttribArray
#define GL_Finish _GL_Finish
#define GL_Flush _GL_Flush
#define GL_FramebufferRenderbuffer _GL_FramebufferRenderbuffer
#define GL_FramebufferTexture2D _GL_FramebufferTexture2D
#define GL_FrontFace _GL_FrontFace
#define GL_GenBuffers _GL_GenBuffers
#define GL_GenFramebuffers _GL_GenFramebuffers
#define GL_GenRenderbuffers _GL_GenRenderbuffers
#define GL_GenTextures _GL_GenTextures
#define GL_GenerateMipmap _GL_GenerateMipmap
#define GL_GetBooleanv _GL_GetBooleanv
#define GL_GetBufferParameteriv _GL_GetBufferParameteriv
#define GL_GetError _GL_GetError
#define GL_GetFloatv _GL_GetFloatv
#define GL_GetFramebufferAttachmentParameteriv _GL_GetFramebufferAttachmentParameteriv
#define GL_GetIntegerv _GL_GetIntegerv
#define GL_GetProgramiv _GL_GetProgramiv
#define GL_GetRenderbufferParameteriv _GL_GetRenderbufferParameteriv
#define GL_GetShaderPrecisionFormat _GL_GetShaderPrecisionFormat
#define GL_GetShaderiv _GL_GetShaderiv
#define GL_GetString _GL_GetString
#define GL_GetTexParameterfv _GL_GetTexParameterfv
#define GL_GetTexParameteriv _GL_GetTexParameteriv
#define GL_GetUniformfv _GL_GetUniformfv
#define GL_GetUniformiv _GL_GetUniformiv
#define GL_GetVertexAttribPointerv _GL_GetVertexAttribPointerv
#define GL_GetVertexAttribfv _GL_GetVertexAttribfv
#define GL_GetVertexAttribiv _GL_GetVertexAttribiv
#define GL_Hint _GL_Hint
#define GL_IsBuffer _GL_IsBuffer
#define GL_IsEnabled _GL_IsEnabled
#define GL_IsFramebuffer _GL_IsFramebuffer
#define GL_IsProgram _GL_IsProgram
#define GL_IsRenderbuffer _GL_IsRenderbuffer
#define GL_IsShader _GL_IsShader
#define GL_IsTexture _GL_IsTexture
#define GL_LineWidth _GL_LineWidth
#define GL_LinkProgram _GL_LinkProgram
#define GL_PixelStorei _GL_PixelStorei
#define GL_PolygonOffset _GL_PolygonOffset
#define GL_ReadPixels _GL_ReadPixels
#define GL_ReleaseShaderCompiler _GL_ReleaseShaderCompiler
#define GL_RenderbufferStorage _GL_RenderbufferStorage
#define GL_SampleCoverage _GL_SampleCoverage
#define GL_Scissor _GL_Scissor
#define GL_ShaderBinary _GL_ShaderBinary
#define GL_StencilFunc _GL_StencilFunc
#define GL_StencilFuncSeparate _GL_StencilFuncSeparate
#define GL_StencilMask _GL_StencilMask
#define GL_StencilMaskSeparate _GL_StencilMaskSeparate
#define GL_StencilOp _GL_StencilOp
#define GL_StencilOpSeparate _GL_StencilOpSeparate
#define GL_TexImage2D _GL_TexImage2D
#define GL_TexParameterf _GL_TexParameterf
#define GL_TexParameterfv _GL_TexParameterfv
#define GL_TexParameteri _GL_TexParameteri
#define GL_TexParameteriv _GL_TexParameteriv
#define GL_TexSubImage2D _GL_TexSubImage2D
#define GL_Uniform1f _GL_Uniform1f
#define GL_Uniform1fv _GL_Uniform1fv
#define GL_Uniform1i _GL_Uniform1i
#define GL_Uniform1iv _GL_Uniform1iv
#define GL_Uniform2f _GL_Uniform2f
#define GL_Uniform2fv _GL_Uniform2fv
#define GL_Uniform2i _GL_Uniform2i
#define GL_Uniform2iv _GL_Uniform2iv
#define GL_Uniform3f _GL_Uniform3f
#define GL_Uniform3fv _GL_Uniform3fv
#define GL_Uniform3i _GL_Uniform3i
#define GL_Uniform3iv _GL_Uniform3iv
#define GL_Uniform4f _GL_Uniform4f
#define GL_Uniform4fv _GL_Uniform4fv
#define GL_Uniform4i _GL_Uniform4i
#define GL_Uniform4iv _GL_Uniform4iv
#define GL_UniformMatrix2fv _GL_UniformMatrix2fv
#define GL_UniformMatrix3fv _GL_UniformMatrix3fv
#define GL_UniformMatrix4fv _GL_UniformMatrix4fv
#define GL_UseProgram _GL_UseProgram
#define GL_ValidateProgram _GL_ValidateProgram
#define GL_VertexAttrib1f _GL_VertexAttrib1f
#define GL_VertexAttrib1fv _GL_VertexAttrib1fv
#define GL_VertexAttrib2f _GL_VertexAttrib2f
#define GL_VertexAttrib2fv _GL_VertexAttrib2fv
#define GL_VertexAttrib3f _GL_VertexAttrib3f
#define GL_VertexAttrib3fv _GL_VertexAttrib3fv
#define GL_VertexAttrib4f _GL_VertexAttrib4f
#define GL_VertexAttrib4fv _GL_VertexAttrib4fv
#define GL_VertexAttribPointer _GL_VertexAttribPointer
#define GL_Viewport _GL_Viewport
#define GL_BindAttribLocation _GL_BindAttribLocation
#define GL_GetActiveAttrib _GL_GetActiveAttrib
#define GL_GetActiveUniform _GL_GetActiveUniform
#define GL_GetAttribLocation _GL_GetAttribLocation
#define GL_GetProgramInfoLog _GL_GetProgramInfoLog
#define GL_GetShaderInfoLog _GL_GetShaderInfoLog
#define GL_GetShaderSource _GL_GetShaderSource
#define GL_GetUniformLocation _GL_GetUniformLocation
#define GL_ShaderSource _GL_ShaderSource

#endif

void GL_APIENTRY GL_AlphaFunc(GLenum func, GLfloat ref);
void GL_APIENTRY GL_AlphaFuncx(GLenum func, GLfixed ref);
void GL_APIENTRY GL_ClearColorx(GLfixed red,
                                             GLfixed green,
                                             GLfixed blue,
                                             GLfixed alpha);
void GL_APIENTRY GL_ClearDepthx(GLfixed depth);
void GL_APIENTRY GL_ClientActiveTexture(GLenum texture);
void GL_APIENTRY GL_ClipPlanef(GLenum p, const GLfloat *eqn);
void GL_APIENTRY GL_ClipPlanex(GLenum plane, const GLfixed *equation);
void GL_APIENTRY GL_Color4f(GLfloat red, GLfloat green, GLfloat blue, GLfloat alpha);
void GL_APIENTRY GL_Color4ub(GLubyte red, GLubyte green, GLubyte blue, GLubyte alpha);
void GL_APIENTRY GL_Color4x(GLfixed red, GLfixed green, GLfixed blue, GLfixed alpha);
void GL_APIENTRY GL_ColorPointer(GLint size,
                                              GLenum type,
                                              GLsizei stride,
                                              const void *pointer);
void GL_APIENTRY GL_DepthRangex(GLfixed n, GLfixed f);
void GL_APIENTRY GL_DisableClientState(GLenum array);
void GL_APIENTRY GL_EnableClientState(GLenum array);
void GL_APIENTRY GL_Fogf(GLenum pname, GLfloat param);
void GL_APIENTRY GL_Fogfv(GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_Fogx(GLenum pname, GLfixed param);
void GL_APIENTRY GL_Fogxv(GLenum pname, const GLfixed *param);
void GL_APIENTRY GL_Frustumf(GLfloat l, GLfloat r, GLfloat b, GLfloat t, GLfloat n, GLfloat f);
void GL_APIENTRY GL_Frustumx(GLfixed l, GLfixed r, GLfixed b, GLfixed t, GLfixed n, GLfixed f);
void GL_APIENTRY GL_GetClipPlanef(GLenum plane, GLfloat *equation);
void GL_APIENTRY GL_GetClipPlanex(GLenum plane, GLfixed *equation);
void GL_APIENTRY GL_GetFixedv(GLenum pname, GLfixed *params);
void GL_APIENTRY GL_GetLightfv(GLenum light, GLenum pname, GLfloat *params);
void GL_APIENTRY GL_GetLightxv(GLenum light, GLenum pname, GLfixed *params);
void GL_APIENTRY GL_GetMaterialfv(GLenum face, GLenum pname, GLfloat *params);
void GL_APIENTRY GL_GetMaterialxv(GLenum face, GLenum pname, GLfixed *params);
void GL_APIENTRY GL_GetTexEnvfv(GLenum target, GLenum pname, GLfloat *params);
void GL_APIENTRY GL_GetTexEnviv(GLenum target, GLenum pname, GLint *params);
void GL_APIENTRY GL_GetTexEnvxv(GLenum target, GLenum pname, GLfixed *params);
void GL_APIENTRY GL_GetTexParameterxv(GLenum target, GLenum pname, GLfixed *params);
void GL_APIENTRY GL_LightModelf(GLenum pname, GLfloat param);
void GL_APIENTRY GL_LightModelfv(GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_LightModelx(GLenum pname, GLfixed param);
void GL_APIENTRY GL_LightModelxv(GLenum pname, const GLfixed *param);
void GL_APIENTRY GL_Lightf(GLenum light, GLenum pname, GLfloat param);
void GL_APIENTRY GL_Lightfv(GLenum light, GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_Lightx(GLenum light, GLenum pname, GLfixed param);
void GL_APIENTRY GL_Lightxv(GLenum light, GLenum pname, const GLfixed *params);
void GL_APIENTRY GL_LineWidthx(GLfixed width);
void GL_APIENTRY GL_LoadIdentity();
void GL_APIENTRY GL_LoadMatrixf(const GLfloat *m);
void GL_APIENTRY GL_LoadMatrixx(const GLfixed *m);
void GL_APIENTRY GL_LogicOp(GLenum opcode);
void GL_APIENTRY GL_Materialf(GLenum face, GLenum pname, GLfloat param);
void GL_APIENTRY GL_Materialfv(GLenum face, GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_Materialx(GLenum face, GLenum pname, GLfixed param);
void GL_APIENTRY GL_Materialxv(GLenum face, GLenum pname, const GLfixed *param);
void GL_APIENTRY GL_MatrixMode(GLenum mode);
void GL_APIENTRY GL_MultMatrixf(const GLfloat *m);
void GL_APIENTRY GL_MultMatrixx(const GLfixed *m);
void GL_APIENTRY GL_MultiTexCoord4f(GLenum target, GLfloat s, GLfloat t, GLfloat r, GLfloat q);
void GL_APIENTRY GL_MultiTexCoord4x(GLenum texture, GLfixed s, GLfixed t, GLfixed r, GLfixed q);
void GL_APIENTRY GL_Normal3f(GLfloat nx, GLfloat ny, GLfloat nz);
void GL_APIENTRY GL_Normal3x(GLfixed nx, GLfixed ny, GLfixed nz);
void GL_APIENTRY GL_NormalPointer(GLenum type, GLsizei stride, const void *pointer);
void GL_APIENTRY GL_Orthof(GLfloat l, GLfloat r, GLfloat b, GLfloat t, GLfloat n, GLfloat f);
void GL_APIENTRY GL_Orthox(GLfixed l, GLfixed r, GLfixed b, GLfixed t, GLfixed n, GLfixed f);
void GL_APIENTRY GL_PointParameterf(GLenum pname, GLfloat param);
void GL_APIENTRY GL_PointParameterfv(GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_PointParameterx(GLenum pname, GLfixed param);
void GL_APIENTRY GL_PointParameterxv(GLenum pname, const GLfixed *params);
void GL_APIENTRY GL_PointSize(GLfloat size);
void GL_APIENTRY GL_PointSizex(GLfixed size);
void GL_APIENTRY GL_PolygonOffsetx(GLfixed factor, GLfixed units);
void GL_APIENTRY GL_PopMatrix();
void GL_APIENTRY GL_PushMatrix();
void GL_APIENTRY GL_Rotatef(GLfloat angle, GLfloat x, GLfloat y, GLfloat z);
void GL_APIENTRY GL_Rotatex(GLfixed angle, GLfixed x, GLfixed y, GLfixed z);
void GL_APIENTRY GL_SampleCoveragex(int value, GLboolean invert);
void GL_APIENTRY GL_Scalef(GLfloat x, GLfloat y, GLfloat z);
void GL_APIENTRY GL_Scalex(GLfixed x, GLfixed y, GLfixed z);
void GL_APIENTRY GL_ShadeModel(GLenum mode);
void GL_APIENTRY GL_TexCoordPointer(GLint size,
                                                 GLenum type,
                                                 GLsizei stride,
                                                 const void *pointer);
void GL_APIENTRY GL_TexEnvf(GLenum target, GLenum pname, GLfloat param);
void GL_APIENTRY GL_TexEnvfv(GLenum target, GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_TexEnvi(GLenum target, GLenum pname, GLint param);
void GL_APIENTRY GL_TexEnviv(GLenum target, GLenum pname, const GLint *params);
void GL_APIENTRY GL_TexEnvx(GLenum target, GLenum pname, GLfixed param);
void GL_APIENTRY GL_TexEnvxv(GLenum target, GLenum pname, const GLfixed *params);
void GL_APIENTRY GL_TexParameterx(GLenum target, GLenum pname, GLfixed param);
void GL_APIENTRY GL_TexParameterxv(GLenum target, GLenum pname, const GLfixed *params);
void GL_APIENTRY GL_Translatef(GLfloat x, GLfloat y, GLfloat z);
void GL_APIENTRY GL_Translatex(GLfixed x, GLfixed y, GLfixed z);
void GL_APIENTRY GL_VertexPointer(GLint size,
                                               GLenum type,
                                               GLsizei stride,
                                               const void *pointer);

void GL_APIENTRY GL_ActiveTexture(GLenum texture);
void GL_APIENTRY GL_AttachShader(GLuint program, GLuint shader);
void GL_APIENTRY GL_BindBuffer(GLenum target, GLuint buffer);
void GL_APIENTRY GL_BindFramebuffer(GLenum target, GLuint framebuffer);
void GL_APIENTRY GL_BindRenderbuffer(GLenum target, GLuint renderbuffer);
void GL_APIENTRY GL_BindTexture(GLenum target, GLuint texture);
void GL_APIENTRY GL_BlendColor(GLfloat red,
                                            GLfloat green,
                                            GLfloat blue,
                                            GLfloat alpha);
void GL_APIENTRY GL_BlendEquation(GLenum mode);
void GL_APIENTRY GL_BlendEquationSeparate(GLenum modeRGB, GLenum modeAlpha);
void GL_APIENTRY GL_BlendFunc(GLenum sfactor, GLenum dfactor);
void GL_APIENTRY GL_BlendFuncSeparate(GLenum sfactorRGB,
                                                   GLenum dfactorRGB,
                                                   GLenum sfactorAlpha,
                                                   GLenum dfactorAlpha);
void GL_APIENTRY GL_BufferData(GLenum target,
                                            GLsizeiptr size,
                                            const void *data,
                                            GLenum usage);
void GL_APIENTRY GL_BufferSubData(GLenum target,
                                               GLintptr offset,
                                               GLsizeiptr size,
                                               const void *data);
GLenum GL_APIENTRY GL_CheckFramebufferStatus(GLenum target);
void GL_APIENTRY GL_Clear(GLbitfield mask);
void GL_APIENTRY GL_ClearColor(GLfloat red,
                                            GLfloat green,
                                            GLfloat blue,
                                            GLfloat alpha);
void GL_APIENTRY GL_ClearDepthf(GLfloat d);
void GL_APIENTRY GL_ClearStencil(GLint s);
void GL_APIENTRY GL_ColorMask(GLboolean red,
                                           GLboolean green,
                                           GLboolean blue,
                                           GLboolean alpha);
void GL_APIENTRY GL_CompileShader(GLuint shader);
void GL_APIENTRY GL_CompressedTexImage2D(GLenum target,
                                                      GLint level,
                                                      GLenum internalformat,
                                                      GLsizei width,
                                                      GLsizei height,
                                                      GLint border,
                                                      GLsizei imageSize,
                                                      const void *data);
void GL_APIENTRY GL_CompressedTexSubImage2D(GLenum target,
                                                         GLint level,
                                                         GLint xoffset,
                                                         GLint yoffset,
                                                         GLsizei width,
                                                         GLsizei height,
                                                         GLenum format,
                                                         GLsizei imageSize,
                                                         const void *data);
void GL_APIENTRY GL_CopyTexImage2D(GLenum target,
                                                GLint level,
                                                GLenum internalformat,
                                                GLint x,
                                                GLint y,
                                                GLsizei width,
                                                GLsizei height,
                                                GLint border);
void GL_APIENTRY GL_CopyTexSubImage2D(GLenum target,
                                                   GLint level,
                                                   GLint xoffset,
                                                   GLint yoffset,
                                                   GLint x,
                                                   GLint y,
                                                   GLsizei width,
                                                   GLsizei height);
GLuint GL_APIENTRY GL_CreateProgram();
GLuint GL_APIENTRY GL_CreateShader(GLenum type);
void GL_APIENTRY GL_CullFace(GLenum mode);
void GL_APIENTRY GL_DeleteBuffers(GLsizei n, const GLuint *buffers);
void GL_APIENTRY GL_DeleteFramebuffers(GLsizei n, const GLuint *framebuffers);
void GL_APIENTRY GL_DeleteProgram(GLuint program);
void GL_APIENTRY GL_DeleteRenderbuffers(GLsizei n, const GLuint *renderbuffers);
void GL_APIENTRY GL_DeleteShader(GLuint shader);
void GL_APIENTRY GL_DeleteTextures(GLsizei n, const GLuint *textures);
void GL_APIENTRY GL_DepthFunc(GLenum func);
void GL_APIENTRY GL_DepthMask(GLboolean flag);
void GL_APIENTRY GL_DepthRangef(GLfloat n, GLfloat f);
void GL_APIENTRY GL_DetachShader(GLuint program, GLuint shader);
void GL_APIENTRY GL_Disable(GLenum cap);
void GL_APIENTRY GL_DisableVertexAttribArray(GLuint index);
void GL_APIENTRY GL_DrawArrays(GLenum mode, GLint first, GLsizei count);
void GL_APIENTRY GL_DrawElements(GLenum mode,
                                              GLsizei count,
                                              GLenum type,
                                              const void *indices);
void GL_APIENTRY GL_Enable(GLenum cap);
void GL_APIENTRY GL_EnableVertexAttribArray(GLuint index);
void GL_APIENTRY GL_Finish();
void GL_APIENTRY GL_Flush();
void GL_APIENTRY GL_FramebufferRenderbuffer(GLenum target,
                                                         GLenum attachment,
                                                         GLenum renderbuffertarget,
                                                         GLuint renderbuffer);
void GL_APIENTRY GL_FramebufferTexture2D(GLenum target,
                                                      GLenum attachment,
                                                      GLenum textarget,
                                                      GLuint texture,
                                                      GLint level);
void GL_APIENTRY GL_FrontFace(GLenum mode);
void GL_APIENTRY GL_GenBuffers(GLsizei n, GLuint *buffers);
void GL_APIENTRY GL_GenFramebuffers(GLsizei n, GLuint *framebuffers);
void GL_APIENTRY GL_GenRenderbuffers(GLsizei n, GLuint *renderbuffers);
void GL_APIENTRY GL_GenTextures(GLsizei n, GLuint *textures);
void GL_APIENTRY GL_GenerateMipmap(GLenum target);
void GL_APIENTRY GL_GetBooleanv(GLenum pname, GLboolean *data);
void GL_APIENTRY GL_GetBufferParameteriv(GLenum target, GLenum pname, GLint *params);
GLenum GL_APIENTRY GL_GetError();
void GL_APIENTRY GL_GetFloatv(GLenum pname, GLfloat *data);
void GL_APIENTRY GL_GetFramebufferAttachmentParameteriv(GLenum target,
                                                                     GLenum attachment,
                                                                     GLenum pname,
                                                                     GLint *params);
void GL_APIENTRY GL_GetIntegerv(GLenum pname, GLint *data);
void GL_APIENTRY GL_GetProgramiv(GLuint program, GLenum pname, GLint *params);
void GL_APIENTRY GL_GetRenderbufferParameteriv(GLenum target,
                                                            GLenum pname,
                                                            GLint *params);

void GL_APIENTRY GL_GetShaderPrecisionFormat(GLenum shadertype,
                                                          GLenum precisiontype,
                                                          GLint *range,
                                                          GLint *precision);

void GL_APIENTRY GL_GetShaderiv(GLuint shader, GLenum pname, GLint *params);
const GLubyte GL_APIENTRY *GL_GetString(GLenum name);
void GL_APIENTRY GL_GetTexParameterfv(GLenum target, GLenum pname, GLfloat *params);
void GL_APIENTRY GL_GetTexParameteriv(GLenum target, GLenum pname, GLint *params);
void GL_APIENTRY GL_GetUniformfv(GLuint program, GLint location, GLfloat *params);
void GL_APIENTRY GL_GetUniformiv(GLuint program, GLint location, GLint *params);
void GL_APIENTRY GL_GetVertexAttribPointerv(GLuint index,
                                                         GLenum pname,
                                                         void **pointer);
void GL_APIENTRY GL_GetVertexAttribfv(GLuint index, GLenum pname, GLfloat *params);
void GL_APIENTRY GL_GetVertexAttribiv(GLuint index, GLenum pname, GLint *params);
void GL_APIENTRY GL_Hint(GLenum target, GLenum mode);
GLboolean GL_APIENTRY GL_IsBuffer(GLuint buffer);
GLboolean GL_APIENTRY GL_IsEnabled(GLenum cap);
GLboolean GL_APIENTRY GL_IsFramebuffer(GLuint framebuffer);
GLboolean GL_APIENTRY GL_IsProgram(GLuint program);
GLboolean GL_APIENTRY GL_IsRenderbuffer(GLuint renderbuffer);
GLboolean GL_APIENTRY GL_IsShader(GLuint shader);
GLboolean GL_APIENTRY GL_IsTexture(GLuint texture);
void GL_APIENTRY GL_LineWidth(GLfloat width);
void GL_APIENTRY GL_LinkProgram(GLuint program);
void GL_APIENTRY GL_PixelStorei(GLenum pname, GLint param);
void GL_APIENTRY GL_PolygonOffset(GLfloat factor, GLfloat units);
void GL_APIENTRY GL_ReadPixels(GLint x,
                                            GLint y,
                                            GLsizei width,
                                            GLsizei height,
                                            GLenum format,
                                            GLenum type,
                                            void *pixels);
void GL_APIENTRY GL_ReleaseShaderCompiler();
void GL_APIENTRY GL_RenderbufferStorage(GLenum target,
                                                     GLenum internalformat,
                                                     GLsizei width,
                                                     GLsizei height);
void GL_APIENTRY GL_SampleCoverage(GLfloat value, GLboolean invert);
void GL_APIENTRY GL_Scissor(GLint x, GLint y, GLsizei width, GLsizei height);
void GL_APIENTRY GL_ShaderBinary(GLsizei count,
                                              const GLuint *shaders,
                                              GLenum binaryFormat,
                                              const void *binary,
                                              GLsizei length);
void GL_APIENTRY GL_StencilFunc(GLenum func, GLint ref, GLuint mask);
void GL_APIENTRY GL_StencilFuncSeparate(GLenum face,
                                                     GLenum func,
                                                     GLint ref,
                                                     GLuint mask);
void GL_APIENTRY GL_StencilMask(GLuint mask);
void GL_APIENTRY GL_StencilMaskSeparate(GLenum face, GLuint mask);
void GL_APIENTRY GL_StencilOp(GLenum fail, GLenum zfail, GLenum zpass);
void GL_APIENTRY GL_StencilOpSeparate(GLenum face,
                                                   GLenum sfail,
                                                   GLenum dpfail,
                                                   GLenum dppass);
void GL_APIENTRY GL_TexImage2D(GLenum target,
                                            GLint level,
                                            GLint internalformat,
                                            GLsizei width,
                                            GLsizei height,
                                            GLint border,
                                            GLenum format,
                                            GLenum type,
                                            const void *pixels);
void GL_APIENTRY GL_TexParameterf(GLenum target, GLenum pname, GLfloat param);
void GL_APIENTRY GL_TexParameterfv(GLenum target, GLenum pname, const GLfloat *params);
void GL_APIENTRY GL_TexParameteri(GLenum target, GLenum pname, GLint param);
void GL_APIENTRY GL_TexParameteriv(GLenum target, GLenum pname, const GLint *params);
void GL_APIENTRY GL_TexSubImage2D(GLenum target,
                                               GLint level,
                                               GLint xoffset,
                                               GLint yoffset,
                                               GLsizei width,
                                               GLsizei height,
                                               GLenum format,
                                               GLenum type,
                                               const void *pixels);
void GL_APIENTRY GL_Uniform1f(GLint location, GLfloat v0);
void GL_APIENTRY GL_Uniform1fv(GLint location, GLsizei count, const GLfloat *value);
void GL_APIENTRY GL_Uniform1i(GLint location, GLint v0);
void GL_APIENTRY GL_Uniform1iv(GLint location, GLsizei count, const GLint *value);
void GL_APIENTRY GL_Uniform2f(GLint location, GLfloat v0, GLfloat v1);
void GL_APIENTRY GL_Uniform2fv(GLint location, GLsizei count, const GLfloat *value);
void GL_APIENTRY GL_Uniform2i(GLint location, GLint v0, GLint v1);
void GL_APIENTRY GL_Uniform2iv(GLint location, GLsizei count, const GLint *value);
void GL_APIENTRY GL_Uniform3f(GLint location, GLfloat v0, GLfloat v1, GLfloat v2);
void GL_APIENTRY GL_Uniform3fv(GLint location, GLsizei count, const GLfloat *value);
void GL_APIENTRY GL_Uniform3i(GLint location, GLint v0, GLint v1, GLint v2);
void GL_APIENTRY GL_Uniform3iv(GLint location, GLsizei count, const GLint *value);
void GL_APIENTRY GL_Uniform4f(GLint location, GLfloat v0, GLfloat v1, GLfloat v2, GLfloat v3);
void GL_APIENTRY GL_Uniform4fv(GLint location, GLsizei count, const GLfloat *value);
void GL_APIENTRY GL_Uniform4i(GLint location, GLint v0, GLint v1, GLint v2, GLint v3);
void GL_APIENTRY GL_Uniform4iv(GLint location, GLsizei count, const GLint *value);
void GL_APIENTRY GL_UniformMatrix2fv(GLint location,
                                                  GLsizei count,
                                                  GLboolean transpose,
                                                  const GLfloat *value);
void GL_APIENTRY GL_UniformMatrix3fv(GLint location,
                                                  GLsizei count,
                                                  GLboolean transpose,
                                                  const GLfloat *value);
void GL_APIENTRY GL_UniformMatrix4fv(GLint location,
                                                  GLsizei count,
                                                  GLboolean transpose,
                                                  const GLfloat *value);
void GL_APIENTRY GL_UseProgram(GLuint program);
void GL_APIENTRY GL_ValidateProgram(GLuint program);
void GL_APIENTRY GL_VertexAttrib1f(GLuint index, GLfloat x);
void GL_APIENTRY GL_VertexAttrib1fv(GLuint index, const GLfloat *v);
void GL_APIENTRY GL_VertexAttrib2f(GLuint index, GLfloat x, GLfloat y);
void GL_APIENTRY GL_VertexAttrib2fv(GLuint index, const GLfloat *v);
void GL_APIENTRY GL_VertexAttrib3f(GLuint index, GLfloat x, GLfloat y, GLfloat z);
void GL_APIENTRY GL_VertexAttrib3fv(GLuint index, const GLfloat *v);
void GL_APIENTRY GL_VertexAttrib4f(GLuint index, GLfloat x, GLfloat y, GLfloat z, GLfloat w);
void GL_APIENTRY GL_VertexAttrib4fv(GLuint index, const GLfloat *v);
void GL_APIENTRY GL_VertexAttribPointer(GLuint index,
                                                     GLint size,
                                                     GLenum type,
                                                     GLboolean normalized,
                                                     GLsizei stride,
                                                     const void *pointer);
void GL_APIENTRY GL_Viewport(GLint x, GLint y, GLsizei width, GLsizei height);


void GL_APIENTRY GL_BindAttribLocation (GLuint program, GLuint index, const char *name);
void GL_APIENTRY GL_GetActiveAttrib (GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, char *name);
void GL_APIENTRY GL_GetActiveUniform (GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, char *name);
GLint GL_APIENTRY GL_GetAttribLocation (GLuint program, const char *name);
void GL_APIENTRY GL_GetProgramInfoLog (GLuint program, GLsizei bufSize, GLsizei *length, char *infoLog);
void GL_APIENTRY GL_GetShaderInfoLog (GLuint shader, GLsizei bufSize, GLsizei *length, char *infoLog);
void GL_APIENTRY GL_GetShaderSource (GLuint shader, GLsizei bufSize, GLsizei *length, char *source);
GLint GL_APIENTRY GL_GetUniformLocation (GLuint program, const char *name);
void GL_APIENTRY GL_ShaderSource (GLuint shader, GLsizei count, const char *const*string, const GLint *length);





#define glAlphaFunc GL_AlphaFunc
#define glClipPlanef GL_ClipPlanef
#define glColor4f GL_Color4f
#define glFogf GL_Fogf
#define glFogfv GL_Fogfv
#define glFrustumf GL_Frustumf
#define glGetClipPlanef GL_GetClipPlanef
#define glGetLightfv GL_GetLightfv
#define glGetMaterialfv GL_GetMaterialfv
#define glGetTexEnvfv GL_GetTexEnvfv
#define glLightModelf GL_LightModelf
#define glLightModelfv GL_LightModelfv
#define glLightf GL_Lightf
#define glLightfv GL_Lightfv
#define glLoadMatrixf GL_LoadMatrixf
#define glMaterialf GL_Materialf
#define glMaterialfv GL_Materialfv
#define glMultMatrixf GL_MultMatrixf
#define glMultiTexCoord4f GL_MultiTexCoord4f
#define glNormal3f GL_Normal3f
#define glOrthof GL_Orthof
#define glPointParameterf GL_PointParameterf
#define glPointParameterfv GL_PointParameterfv
#define glPointSize GL_PointSize
#define glRotatef GL_Rotatef
#define glScalef GL_Scalef
#define glTexEnvf GL_TexEnvf
#define glTexEnvfv GL_TexEnvfv
#define glTranslatef GL_Translatef
#define glAlphaFuncx GL_AlphaFuncx
#define glClearColorx GL_ClearColorx
#define glClearDepthx GL_ClearDepthx
#define glClientActiveTexture GL_ClientActiveTexture
#define glClipPlanex GL_ClipPlanex
#define glColor4ub GL_Color4ub
#define glColor4x GL_Color4x
#define glColorPointer GL_ColorPointer
#define glDepthRangex GL_DepthRangex
#define glDisableClientState GL_DisableClientState
#define glEnableClientState GL_EnableClientState
#define glFogx GL_Fogx
#define glFogxv GL_Fogxv
#define glFrustumx GL_Frustumx
#define glGetClipPlanex GL_GetClipPlanex
#define glGetFixedv GL_GetFixedv
#define glGetLightxv GL_GetLightxv
#define glGetMaterialxv GL_GetMaterialxv
#define glGetTexEnviv GL_GetTexEnviv
#define glGetTexEnvxv GL_GetTexEnvxv
#define glGetTexParameterxv GL_GetTexParameterxv
#define glLightModelx GL_LightModelx
#define glLightModelxv GL_LightModelxv
#define glLightx GL_Lightx
#define glLightxv GL_Lightxv
#define glLineWidthx GL_LineWidthx
#define glLoadIdentity GL_LoadIdentity
#define glLoadMatrixx GL_LoadMatrixx
#define glLogicOp GL_LogicOp
#define glMaterialx GL_Materialx
#define glMaterialxv GL_Materialxv
#define glMatrixMode GL_MatrixMode
#define glMultMatrixx GL_MultMatrixx
#define glMultiTexCoord4x GL_MultiTexCoord4x
#define glNormal3x GL_Normal3x
#define glNormalPointer GL_NormalPointer
#define glOrthox GL_Orthox
#define glPointParameterx GL_PointParameterx
#define glPointParameterxv GL_PointParameterxv
#define glPointSizex GL_PointSizex
#define glPolygonOffsetx GL_PolygonOffsetx
#define glPopMatrix GL_PopMatrix
#define glPushMatrix GL_PushMatrix
#define glRotatex GL_Rotatex
#define glSampleCoveragex GL_SampleCoveragex
#define glScalex GL_Scalex
#define glShadeModel GL_ShadeModel
#define glTexCoordPointer GL_TexCoordPointer
#define glTexEnvi GL_TexEnvi
#define glTexEnvx GL_TexEnvx
#define glTexEnviv GL_TexEnviv
#define glTexEnvxv GL_TexEnvxv
#define glTexParameterx GL_TexParameterx
#define glTexParameterxv GL_TexParameterxv
#define glTranslatex GL_Translatex
#define glVertexPointer GL_VertexPointer
#define glActiveTexture GL_ActiveTexture
#define glAttachShader GL_AttachShader
#define glBindAttribLocation GL_BindAttribLocation
#define glBindBuffer GL_BindBuffer
#define glBindFramebuffer GL_BindFramebuffer
#define glBindRenderbuffer GL_BindRenderbuffer
#define glBindTexture GL_BindTexture
#define glBlendColor GL_BlendColor
#define glBlendEquation GL_BlendEquation
#define glBlendEquationSeparate GL_BlendEquationSeparate
#define glBlendFunc GL_BlendFunc
#define glBlendFuncSeparate GL_BlendFuncSeparate
#define glBufferData GL_BufferData
#define glBufferSubData GL_BufferSubData
#define glCheckFramebufferStatus GL_CheckFramebufferStatus
#define glClear GL_Clear
#define glClearColor GL_ClearColor
#define glClearDepthf GL_ClearDepthf
#define glClearStencil GL_ClearStencil
#define glColorMask GL_ColorMask
#define glCompileShader GL_CompileShader
#define glCompressedTexImage2D GL_CompressedTexImage2D
#define glCompressedTexSubImage2D GL_CompressedTexSubImage2D
#define glCopyTexImage2D GL_CopyTexImage2D
#define glCopyTexSubImage2D GL_CopyTexSubImage2D
#define glCreateProgram GL_CreateProgram
#define glCreateShader GL_CreateShader
#define glCullFace GL_CullFace
#define glDeleteBuffers GL_DeleteBuffers
#define glDeleteFramebuffers GL_DeleteFramebuffers
#define glDeleteProgram GL_DeleteProgram
#define glDeleteRenderbuffers GL_DeleteRenderbuffers
#define glDeleteShader GL_DeleteShader
#define glDeleteTextures GL_DeleteTextures
#define glDepthFunc GL_DepthFunc
#define glDepthMask GL_DepthMask
#define glDepthRangef GL_DepthRangef
#define glDetachShader GL_DetachShader
#define glDisable GL_Disable
#define glDisableVertexAttribArray GL_DisableVertexAttribArray
#define glDrawArrays GL_DrawArrays
#define glDrawElements GL_DrawElements
#define glEnable GL_Enable
#define glEnableVertexAttribArray GL_EnableVertexAttribArray
#define glFinish GL_Finish
#define glFlush GL_Flush
#define glFramebufferRenderbuffer GL_FramebufferRenderbuffer
#define glFramebufferTexture2D GL_FramebufferTexture2D
#define glFrontFace GL_FrontFace
#define glGenBuffers GL_GenBuffers
#define glGenerateMipmap GL_GenerateMipmap
#define glGenFramebuffers GL_GenFramebuffers
#define glGenRenderbuffers GL_GenRenderbuffers
#define glGenTextures GL_GenTextures
#define glGetActiveAttrib GL_GetActiveAttrib
#define glGetActiveUniform GL_GetActiveUniform
#define glGetAttachedShaders GL_GetAttachedShaders
#define glGetAttribLocation GL_GetAttribLocation
#define glGetBooleanv GL_GetBooleanv
#define glGetBufferParameteriv GL_GetBufferParameteriv
#define glGetError GL_GetError
#define glGetFloatv GL_GetFloatv
#define glGetFramebufferAttachmentParameteriv GL_GetFramebufferAttachmentParameteriv
#define glGetIntegerv GL_GetIntegerv
#define glGetProgramiv GL_GetProgramiv
#define glGetProgramInfoLog GL_GetProgramInfoLog
#define glGetRenderbufferParameteriv GL_GetRenderbufferParameteriv
#define glGetShaderiv GL_GetShaderiv
#define glGetShaderInfoLog GL_GetShaderInfoLog
#define glGetShaderPrecisionFormat GL_GetShaderPrecisionFormat
#define glGetShaderSource GL_GetShaderSource
#define glGetString GL_GetString
#define glGetTexParameterfv GL_GetTexParameterfv
#define glGetTexParameteriv GL_GetTexParameteriv
#define glGetUniformfv GL_GetUniformfv
#define glGetUniformiv GL_GetUniformiv
#define glGetUniformLocation GL_GetUniformLocation
#define glGetVertexAttribfv GL_GetVertexAttribfv
#define glGetVertexAttribiv GL_GetVertexAttribiv
#define glGetVertexAttribPointerv GL_GetVertexAttribPointerv
#define glHint GL_Hint
#define glIsBuffer GL_IsBuffer
#define glIsEnabled GL_IsEnabled
#define glIsFramebuffer GL_IsFramebuffer
#define glIsProgram GL_IsProgram
#define glIsRenderbuffer GL_IsRenderbuffer
#define glIsShader GL_IsShader
#define glIsTexture GL_IsTexture
#define glLineWidth GL_LineWidth
#define glLinkProgram GL_LinkProgram
#define glPixelStorei GL_PixelStorei
#define glPolygonOffset GL_PolygonOffset
#define glReadPixels GL_ReadPixels
#define glReleaseShaderCompiler GL_ReleaseShaderCompiler
#define glRenderbufferStorage GL_RenderbufferStorage
#define glSampleCoverage GL_SampleCoverage
#define glScissor GL_Scissor
#define glShaderBinary GL_ShaderBinary
#define glShaderSource GL_ShaderSource
#define glStencilFunc GL_StencilFunc
#define glStencilFuncSeparate GL_StencilFuncSeparate
#define glStencilMask GL_StencilMask
#define glStencilMaskSeparate GL_StencilMaskSeparate
#define glStencilOp GL_StencilOp
#define glStencilOpSeparate GL_StencilOpSeparate
#define glTexImage2D GL_TexImage2D
#define glTexParameterf GL_TexParameterf
#define glTexParameterfv GL_TexParameterfv
#define glTexParameteri GL_TexParameteri
#define glTexParameteriv GL_TexParameteriv
#define glTexSubImage2D GL_TexSubImage2D
#define glUniform1f GL_Uniform1f
#define glUniform1fv GL_Uniform1fv
#define glUniform1i GL_Uniform1i
#define glUniform1iv GL_Uniform1iv
#define glUniform2f GL_Uniform2f
#define glUniform2fv GL_Uniform2fv
#define glUniform2i GL_Uniform2i
#define glUniform2iv GL_Uniform2iv
#define glUniform3f GL_Uniform3f
#define glUniform3fv GL_Uniform3fv
#define glUniform3i GL_Uniform3i
#define glUniform3iv GL_Uniform3iv
#define glUniform4f GL_Uniform4f
#define glUniform4fv GL_Uniform4fv
#define glUniform4i GL_Uniform4i
#define glUniform4iv GL_Uniform4iv
#define glUniformMatrix2fv GL_UniformMatrix2fv
#define glUniformMatrix3fv GL_UniformMatrix3fv
#define glUniformMatrix4fv GL_UniformMatrix4fv
#define glUseProgram GL_UseProgram
#define glValidateProgram GL_ValidateProgram
#define glVertexAttrib1f GL_VertexAttrib1f
#define glVertexAttrib1fv GL_VertexAttrib1fv
#define glVertexAttrib2f GL_VertexAttrib2f
#define glVertexAttrib2fv GL_VertexAttrib2fv
#define glVertexAttrib3f GL_VertexAttrib3f
#define glVertexAttrib3fv GL_VertexAttrib3fv
#define glVertexAttrib4f GL_VertexAttrib4f
#define glVertexAttrib4fv GL_VertexAttrib4fv
#define glVertexAttribPointer GL_VertexAttribPointer
#define glViewport GL_Viewport
#define glReadBuffer GL_ReadBuffer
#define glDrawRangeElements GL_DrawRangeElements
#define glTexImage3D GL_TexImage3D
#define glTexSubImage3D GL_TexSubImage3D
#define glCopyTexSubImage3D GL_CopyTexSubImage3D
#define glCompressedTexImage3D GL_CompressedTexImage3D
#define glCompressedTexSubImage3D GL_CompressedTexSubImage3D
#define glGenQueries GL_GenQueries
#define glDeleteQueries GL_DeleteQueries
#define glIsQuery GL_IsQuery
#define glBeginQuery GL_BeginQuery
#define glEndQuery GL_EndQuery
#define glGetQueryiv GL_GetQueryiv
#define glGetQueryObjectuiv GL_GetQueryObjectuiv
#define glUnmapBuffer GL_UnmapBuffer
#define glGetBufferPointerv GL_GetBufferPointerv
#define glDrawBuffers GL_DrawBuffers
#define glUniformMatrix2x3fv GL_UniformMatrix2x3fv
#define glUniformMatrix3x2fv GL_UniformMatrix3x2fv
#define glUniformMatrix2x4fv GL_UniformMatrix2x4fv
#define glUniformMatrix4x2fv GL_UniformMatrix4x2fv
#define glUniformMatrix3x4fv GL_UniformMatrix3x4fv
#define glUniformMatrix4x3fv GL_UniformMatrix4x3fv
#define glBlitFramebuffer GL_BlitFramebuffer
#define glRenderbufferStorageMultisample GL_RenderbufferStorageMultisample
#define glFramebufferTextureLayer GL_FramebufferTextureLayer
#define glMapBufferRange GL_MapBufferRange
#define glFlushMappedBufferRange GL_FlushMappedBufferRange
#define glBindVertexArray GL_BindVertexArray
#define glDeleteVertexArrays GL_DeleteVertexArrays
#define glGenVertexArrays GL_GenVertexArrays
#define glIsVertexArray GL_IsVertexArray
#define glGetIntegeri_v GL_GetIntegeri_v
#define glBeginTransformFeedback GL_BeginTransformFeedback
#define glEndTransformFeedback GL_EndTransformFeedback
#define glBindBufferRange GL_BindBufferRange
#define glBindBufferBase GL_BindBufferBase
#define glTransformFeedbackVaryings GL_TransformFeedbackVaryings
#define glGetTransformFeedbackVarying GL_GetTransformFeedbackVarying
#define glVertexAttribIPointer GL_VertexAttribIPointer
#define glGetVertexAttribIiv GL_GetVertexAttribIiv
#define glGetVertexAttribIuiv GL_GetVertexAttribIuiv
#define glVertexAttribI4i GL_VertexAttribI4i
#define glVertexAttribI4ui GL_VertexAttribI4ui
#define glVertexAttribI4iv GL_VertexAttribI4iv
#define glVertexAttribI4uiv GL_VertexAttribI4uiv
#define glGetUniformuiv GL_GetUniformuiv
#define glGetFragDataLocation GL_GetFragDataLocation
#define glUniform1ui GL_Uniform1ui
#define glUniform2ui GL_Uniform2ui
#define glUniform3ui GL_Uniform3ui
#define glUniform4ui GL_Uniform4ui
#define glUniform1uiv GL_Uniform1uiv
#define glUniform2uiv GL_Uniform2uiv
#define glUniform3uiv GL_Uniform3uiv
#define glUniform4uiv GL_Uniform4uiv
#define glClearBufferiv GL_ClearBufferiv
#define glClearBufferuiv GL_ClearBufferuiv
#define glClearBufferfv GL_ClearBufferfv
#define glClearBufferfi GL_ClearBufferfi
#define glGetStringi GL_GetStringi
#define glCopyBufferSubData GL_CopyBufferSubData
#define glGetUniformIndices GL_GetUniformIndices
#define glGetActiveUniformsiv GL_GetActiveUniformsiv
#define glGetUniformBlockIndex GL_GetUniformBlockIndex
#define glGetActiveUniformBlockiv GL_GetActiveUniformBlockiv
#define glGetActiveUniformBlockName GL_GetActiveUniformBlockName
#define glUniformBlockBinding GL_UniformBlockBinding
#define glDrawArraysInstanced GL_DrawArraysInstanced
#define glDrawElementsInstanced GL_DrawElementsInstanced
#define glFenceSync GL_FenceSync
#define glIsSync GL_IsSync
#define glDeleteSync GL_DeleteSync
#define glClientWaitSync GL_ClientWaitSync
#define glWaitSync GL_WaitSync
#define glGetInteger64v GL_GetInteger64v
#define glGetSynciv GL_GetSynciv
#define glGetInteger64i_v GL_GetInteger64i_v
#define glGetBufferParameteri64v GL_GetBufferParameteri64v
#define glGenSamplers GL_GenSamplers
#define glDeleteSamplers GL_DeleteSamplers
#define glIsSampler GL_IsSampler
#define glBindSampler GL_BindSampler
#define glSamplerParameteri GL_SamplerParameteri
#define glSamplerParameteriv GL_SamplerParameteriv
#define glSamplerParameterf GL_SamplerParameterf
#define glSamplerParameterfv GL_SamplerParameterfv
#define glGetSamplerParameteriv GL_GetSamplerParameteriv
#define glGetSamplerParameterfv GL_GetSamplerParameterfv
#define glVertexAttribDivisor GL_VertexAttribDivisor
#define glBindTransformFeedback GL_BindTransformFeedback
#define glDeleteTransformFeedbacks GL_DeleteTransformFeedbacks
#define glGenTransformFeedbacks GL_GenTransformFeedbacks
#define glIsTransformFeedback GL_IsTransformFeedback
#define glPauseTransformFeedback GL_PauseTransformFeedback
#define glResumeTransformFeedback GL_ResumeTransformFeedback
#define glGetProgramBinary GL_GetProgramBinary
#define glProgramBinary GL_ProgramBinary
#define glProgramParameteri GL_ProgramParameteri
#define glInvalidateFramebuffer GL_InvalidateFramebuffer
#define glInvalidateSubFramebuffer GL_InvalidateSubFramebuffer
#define glTexStorage2D GL_TexStorage2D
#define glTexStorage3D GL_TexStorage3D
#define glGetInternalformativ GL_GetInternalformativ
#define glDispatchCompute GL_DispatchCompute
#define glDispatchComputeIndirect GL_DispatchComputeIndirect
#define glDrawArraysIndirect GL_DrawArraysIndirect
#define glDrawElementsIndirect GL_DrawElementsIndirect
#define glFramebufferParameteri GL_FramebufferParameteri
#define glGetFramebufferParameteriv GL_GetFramebufferParameteriv
#define glGetProgramInterfaceiv GL_GetProgramInterfaceiv
#define glGetProgramResourceIndex GL_GetProgramResourceIndex
#define glGetProgramResourceName GL_GetProgramResourceName
#define glGetProgramResourceiv GL_GetProgramResourceiv
#define glGetProgramResourceLocation GL_GetProgramResourceLocation
#define glUseProgramStages GL_UseProgramStages
#define glActiveShaderProgram GL_ActiveShaderProgram
#define glCreateShaderProgramv GL_CreateShaderProgramv
#define glBindProgramPipeline GL_BindProgramPipeline
#define glDeleteProgramPipelines GL_DeleteProgramPipelines
#define glGenProgramPipelines GL_GenProgramPipelines
#define glIsProgramPipeline GL_IsProgramPipeline
#define glGetProgramPipelineiv GL_GetProgramPipelineiv
#define glProgramUniform1i GL_ProgramUniform1i
#define glProgramUniform2i GL_ProgramUniform2i
#define glProgramUniform3i GL_ProgramUniform3i
#define glProgramUniform4i GL_ProgramUniform4i
#define glProgramUniform1ui GL_ProgramUniform1ui
#define glProgramUniform2ui GL_ProgramUniform2ui
#define glProgramUniform3ui GL_ProgramUniform3ui
#define glProgramUniform4ui GL_ProgramUniform4ui
#define glProgramUniform1f GL_ProgramUniform1f
#define glProgramUniform2f GL_ProgramUniform2f
#define glProgramUniform3f GL_ProgramUniform3f
#define glProgramUniform4f GL_ProgramUniform4f
#define glProgramUniform1iv GL_ProgramUniform1iv
#define glProgramUniform2iv GL_ProgramUniform2iv
#define glProgramUniform3iv GL_ProgramUniform3iv
#define glProgramUniform4iv GL_ProgramUniform4iv
#define glProgramUniform1uiv GL_ProgramUniform1uiv
#define glProgramUniform2uiv GL_ProgramUniform2uiv
#define glProgramUniform3uiv GL_ProgramUniform3uiv
#define glProgramUniform4uiv GL_ProgramUniform4uiv
#define glProgramUniform1fv GL_ProgramUniform1fv
#define glProgramUniform2fv GL_ProgramUniform2fv
#define glProgramUniform3fv GL_ProgramUniform3fv
#define glProgramUniform4fv GL_ProgramUniform4fv
#define glProgramUniformMatrix2fv GL_ProgramUniformMatrix2fv
#define glProgramUniformMatrix3fv GL_ProgramUniformMatrix3fv
#define glProgramUniformMatrix4fv GL_ProgramUniformMatrix4fv
#define glProgramUniformMatrix2x3fv GL_ProgramUniformMatrix2x3fv
#define glProgramUniformMatrix3x2fv GL_ProgramUniformMatrix3x2fv
#define glProgramUniformMatrix2x4fv GL_ProgramUniformMatrix2x4fv
#define glProgramUniformMatrix4x2fv GL_ProgramUniformMatrix4x2fv
#define glProgramUniformMatrix3x4fv GL_ProgramUniformMatrix3x4fv
#define glProgramUniformMatrix4x3fv GL_ProgramUniformMatrix4x3fv
#define glValidateProgramPipeline GL_ValidateProgramPipeline
#define glGetProgramPipelineInfoLog GL_GetProgramPipelineInfoLog
#define glBindImageTexture GL_BindImageTexture
#define glGetBooleani_v GL_GetBooleani_v
#define glMemoryBarrier GL_MemoryBarrier
#define glMemoryBarrierByRegion GL_MemoryBarrierByRegion
#define glTexStorage2DMultisample GL_TexStorage2DMultisample
#define glGetMultisamplefv GL_GetMultisamplefv
#define glSampleMaski GL_SampleMaski
#define glGetTexLevelParameteriv GL_GetTexLevelParameteriv
#define glGetTexLevelParameterfv GL_GetTexLevelParameterfv
#define glBindVertexBuffer GL_BindVertexBuffer
#define glVertexAttribFormat GL_VertexAttribFormat
#define glVertexAttribIFormat GL_VertexAttribIFormat
#define glVertexAttribBinding GL_VertexAttribBinding
#define glVertexBindingDivisor GL_VertexBindingDivisor
#define glBlendBarrier GL_BlendBarrier
#define glCopyImageSubData GL_CopyImageSubData
#define glDebugMessageControl GL_DebugMessageControl
#define glDebugMessageInsert GL_DebugMessageInsert
#define glDebugMessageCallback GL_DebugMessageCallback
#define glGetDebugMessageLog GL_GetDebugMessageLog
#define glPushDebugGroup GL_PushDebugGroup
#define glPopDebugGroup GL_PopDebugGroup
#define glObjectLabel GL_ObjectLabel
#define glGetObjectLabel GL_GetObjectLabel
#define glObjectPtrLabel GL_ObjectPtrLabel
#define glGetObjectPtrLabel GL_GetObjectPtrLabel
#define glGetPointerv GL_GetPointerv
#define glEnablei GL_Enablei
#define glDisablei GL_Disablei
#define glBlendEquationi GL_BlendEquationi
#define glBlendEquationSeparatei GL_BlendEquationSeparatei
#define glBlendFunci GL_BlendFunci
#define glBlendFuncSeparatei GL_BlendFuncSeparatei
#define glColorMaski GL_ColorMaski
#define glIsEnabledi GL_IsEnabledi
#define glDrawElementsBaseVertex GL_DrawElementsBaseVertex
#define glDrawRangeElementsBaseVertex GL_DrawRangeElementsBaseVertex
#define glDrawElementsInstancedBaseVertex GL_DrawElementsInstancedBaseVertex
#define glFramebufferTexture GL_FramebufferTexture
#define glPrimitiveBoundingBox GL_PrimitiveBoundingBox
#define glGetGraphicsResetStatus GL_GetGraphicsResetStatus
#define glReadnPixels GL_ReadnPixels
#define glGetnUniformfv GL_GetnUniformfv
#define glGetnUniformiv GL_GetnUniformiv
#define glGetnUniformuiv GL_GetnUniformuiv
#define glMinSampleShading GL_MinSampleShading
#define glPatchParameteri GL_PatchParameteri
#define glTexParameterIiv GL_TexParameterIiv
#define glTexParameterIuiv GL_TexParameterIuiv
#define glGetTexParameterIiv GL_GetTexParameterIiv
#define glGetTexParameterIuiv GL_GetTexParameterIuiv
#define glSamplerParameterIiv GL_SamplerParameterIiv
#define glSamplerParameterIuiv GL_SamplerParameterIuiv
#define glGetSamplerParameterIiv GL_GetSamplerParameterIiv
#define glGetSamplerParameterIuiv GL_GetSamplerParameterIuiv
#define glTexBuffer GL_TexBuffer
#define glTexBufferRange GL_TexBufferRange
#define glTexStorage3DMultisample GL_TexStorage3DMultisample
#define glBeginPerfMonitorAMD GL_BeginPerfMonitorAMD
#define glDeletePerfMonitorsAMD GL_DeletePerfMonitorsAMD
#define glEndPerfMonitorAMD GL_EndPerfMonitorAMD
#define glGenPerfMonitorsAMD GL_GenPerfMonitorsAMD
#define glGetPerfMonitorCounterDataAMD GL_GetPerfMonitorCounterDataAMD
#define glGetPerfMonitorCounterInfoAMD GL_GetPerfMonitorCounterInfoAMD
#define glGetPerfMonitorCounterStringAMD GL_GetPerfMonitorCounterStringAMD
#define glGetPerfMonitorCountersAMD GL_GetPerfMonitorCountersAMD
#define glGetPerfMonitorGroupStringAMD GL_GetPerfMonitorGroupStringAMD
#define glGetPerfMonitorGroupsAMD GL_GetPerfMonitorGroupsAMD
#define glSelectPerfMonitorCountersAMD GL_SelectPerfMonitorCountersAMD
#define glDrawArraysInstancedBaseInstanceANGLE GL_DrawArraysInstancedBaseInstanceANGLE
#define glDrawElementsInstancedBaseVertexBaseInstanceANGLE \
    GL_DrawElementsInstancedBaseVertexBaseInstanceANGLE
#define glMultiDrawArraysInstancedBaseInstanceANGLE GL_MultiDrawArraysInstancedBaseInstanceANGLE
#define glMultiDrawElementsInstancedBaseVertexBaseInstanceANGLE \
    GL_MultiDrawElementsInstancedBaseVertexBaseInstanceANGLE
#define glCopySubTexture3DANGLE GL_CopySubTexture3DANGLE
#define glCopyTexture3DANGLE GL_CopyTexture3DANGLE
#define glBlitFramebufferANGLE GL_BlitFramebufferANGLE
#define glRenderbufferStorageMultisampleANGLE GL_RenderbufferStorageMultisampleANGLE
#define glGetCompressedTexImageANGLE GL_GetCompressedTexImageANGLE
#define glGetRenderbufferImageANGLE GL_GetRenderbufferImageANGLE
#define glGetTexImageANGLE GL_GetTexImageANGLE
#define glGetTexLevelParameterfvANGLE GL_GetTexLevelParameterfvANGLE
#define glGetTexLevelParameterivANGLE GL_GetTexLevelParameterivANGLE
#define glDrawArraysInstancedANGLE GL_DrawArraysInstancedANGLE
#define glDrawElementsInstancedANGLE GL_DrawElementsInstancedANGLE
#define glVertexAttribDivisorANGLE GL_VertexAttribDivisorANGLE
#define glLogicOpANGLE GL_LogicOpANGLE
#define glTexStorageMemFlags2DANGLE GL_TexStorageMemFlags2DANGLE
#define glTexStorageMemFlags2DMultisampleANGLE GL_TexStorageMemFlags2DMultisampleANGLE
#define glTexStorageMemFlags3DANGLE GL_TexStorageMemFlags3DANGLE
#define glTexStorageMemFlags3DMultisampleANGLE GL_TexStorageMemFlags3DMultisampleANGLE
#define glImportMemoryZirconHandleANGLE GL_ImportMemoryZirconHandleANGLE
#define glMultiDrawArraysANGLE GL_MultiDrawArraysANGLE
#define glMultiDrawArraysInstancedANGLE GL_MultiDrawArraysInstancedANGLE
#define glMultiDrawElementsANGLE GL_MultiDrawElementsANGLE
#define glMultiDrawElementsInstancedANGLE GL_MultiDrawElementsInstancedANGLE
#define glPolygonModeANGLE GL_PolygonModeANGLE
#define glProvokingVertexANGLE GL_ProvokingVertexANGLE
#define glDisableExtensionANGLE GL_DisableExtensionANGLE
#define glRequestExtensionANGLE GL_RequestExtensionANGLE
#define glCompressedTexImage2DRobustANGLE GL_CompressedTexImage2DRobustANGLE
#define glCompressedTexImage3DRobustANGLE GL_CompressedTexImage3DRobustANGLE
#define glCompressedTexSubImage2DRobustANGLE GL_CompressedTexSubImage2DRobustANGLE
#define glCompressedTexSubImage3DRobustANGLE GL_CompressedTexSubImage3DRobustANGLE
#define glGetActiveUniformBlockivRobustANGLE GL_GetActiveUniformBlockivRobustANGLE
#define glGetBooleani_vRobustANGLE GL_GetBooleani_vRobustANGLE
#define glGetBooleanvRobustANGLE GL_GetBooleanvRobustANGLE
#define glGetBufferParameteri64vRobustANGLE GL_GetBufferParameteri64vRobustANGLE
#define glGetBufferParameterivRobustANGLE GL_GetBufferParameterivRobustANGLE
#define glGetBufferPointervRobustANGLE GL_GetBufferPointervRobustANGLE
#define glGetFloatvRobustANGLE GL_GetFloatvRobustANGLE
#define glGetFramebufferAttachmentParameterivRobustANGLE \
    GL_GetFramebufferAttachmentParameterivRobustANGLE
#define glGetFramebufferParameterivRobustANGLE GL_GetFramebufferParameterivRobustANGLE
#define glGetInteger64i_vRobustANGLE GL_GetInteger64i_vRobustANGLE
#define glGetInteger64vRobustANGLE GL_GetInteger64vRobustANGLE
#define glGetIntegeri_vRobustANGLE GL_GetIntegeri_vRobustANGLE
#define glGetIntegervRobustANGLE GL_GetIntegervRobustANGLE
#define glGetInternalformativRobustANGLE GL_GetInternalformativRobustANGLE
#define glGetMultisamplefvRobustANGLE GL_GetMultisamplefvRobustANGLE
#define glGetPointervRobustANGLERobustANGLE GL_GetPointervRobustANGLERobustANGLE
#define glGetProgramInterfaceivRobustANGLE GL_GetProgramInterfaceivRobustANGLE
#define glGetProgramivRobustANGLE GL_GetProgramivRobustANGLE
#define glGetQueryObjecti64vRobustANGLE GL_GetQueryObjecti64vRobustANGLE
#define glGetQueryObjectivRobustANGLE GL_GetQueryObjectivRobustANGLE
#define glGetQueryObjectui64vRobustANGLE GL_GetQueryObjectui64vRobustANGLE
#define glGetQueryObjectuivRobustANGLE GL_GetQueryObjectuivRobustANGLE
#define glGetQueryivRobustANGLE GL_GetQueryivRobustANGLE
#define glGetRenderbufferParameterivRobustANGLE GL_GetRenderbufferParameterivRobustANGLE
#define glGetSamplerParameterIivRobustANGLE GL_GetSamplerParameterIivRobustANGLE
#define glGetSamplerParameterIuivRobustANGLE GL_GetSamplerParameterIuivRobustANGLE
#define glGetSamplerParameterfvRobustANGLE GL_GetSamplerParameterfvRobustANGLE
#define glGetSamplerParameterivRobustANGLE GL_GetSamplerParameterivRobustANGLE
#define glGetShaderivRobustANGLE GL_GetShaderivRobustANGLE
#define glGetTexLevelParameterfvRobustANGLE GL_GetTexLevelParameterfvRobustANGLE
#define glGetTexLevelParameterivRobustANGLE GL_GetTexLevelParameterivRobustANGLE
#define glGetTexParameterIivRobustANGLE GL_GetTexParameterIivRobustANGLE
#define glGetTexParameterIuivRobustANGLE GL_GetTexParameterIuivRobustANGLE
#define glGetTexParameterfvRobustANGLE GL_GetTexParameterfvRobustANGLE
#define glGetTexParameterivRobustANGLE GL_GetTexParameterivRobustANGLE
#define glGetUniformfvRobustANGLE GL_GetUniformfvRobustANGLE
#define glGetUniformivRobustANGLE GL_GetUniformivRobustANGLE
#define glGetUniformuivRobustANGLE GL_GetUniformuivRobustANGLE
#define glGetVertexAttribIivRobustANGLE GL_GetVertexAttribIivRobustANGLE
#define glGetVertexAttribIuivRobustANGLE GL_GetVertexAttribIuivRobustANGLE
#define glGetVertexAttribPointervRobustANGLE GL_GetVertexAttribPointervRobustANGLE
#define glGetVertexAttribfvRobustANGLE GL_GetVertexAttribfvRobustANGLE
#define glGetVertexAttribivRobustANGLE GL_GetVertexAttribivRobustANGLE
#define glGetnUniformfvRobustANGLE GL_GetnUniformfvRobustANGLE
#define glGetnUniformivRobustANGLE GL_GetnUniformivRobustANGLE
#define glGetnUniformuivRobustANGLE GL_GetnUniformuivRobustANGLE
#define glReadPixelsRobustANGLE GL_ReadPixelsRobustANGLE
#define glReadnPixelsRobustANGLE GL_ReadnPixelsRobustANGLE
#define glSamplerParameterIivRobustANGLE GL_SamplerParameterIivRobustANGLE
#define glSamplerParameterIuivRobustANGLE GL_SamplerParameterIuivRobustANGLE
#define glSamplerParameterfvRobustANGLE GL_SamplerParameterfvRobustANGLE
#define glSamplerParameterivRobustANGLE GL_SamplerParameterivRobustANGLE
#define glTexImage2DRobustANGLE GL_TexImage2DRobustANGLE
#define glTexImage3DRobustANGLE GL_TexImage3DRobustANGLE
#define glTexParameterIivRobustANGLE GL_TexParameterIivRobustANGLE
#define glTexParameterIuivRobustANGLE GL_TexParameterIuivRobustANGLE
#define glTexParameterfvRobustANGLE GL_TexParameterfvRobustANGLE
#define glTexParameterivRobustANGLE GL_TexParameterivRobustANGLE
#define glTexSubImage2DRobustANGLE GL_TexSubImage2DRobustANGLE
#define glTexSubImage3DRobustANGLE GL_TexSubImage3DRobustANGLE
#define glImportSemaphoreZirconHandleANGLE GL_ImportSemaphoreZirconHandleANGLE
#define glBeginPixelLocalStorageANGLE GL_BeginPixelLocalStorageANGLE
#define glEndPixelLocalStorageANGLE GL_EndPixelLocalStorageANGLE
#define glFramebufferMemorylessPixelLocalStorageANGLE \
    GL_FramebufferMemorylessPixelLocalStorageANGLE
#define glFramebufferPixelLocalClearValuefvANGLE GL_FramebufferPixelLocalClearValuefvANGLE
#define glFramebufferPixelLocalClearValueivANGLE GL_FramebufferPixelLocalClearValueivANGLE
#define glFramebufferPixelLocalClearValueuivANGLE GL_FramebufferPixelLocalClearValueuivANGLE
#define glFramebufferPixelLocalStorageInterruptANGLE GL_FramebufferPixelLocalStorageInterruptANGLE
#define glFramebufferPixelLocalStorageRestoreANGLE GL_FramebufferPixelLocalStorageRestoreANGLE
#define glFramebufferTexturePixelLocalStorageANGLE GL_FramebufferTexturePixelLocalStorageANGLE
#define glGetFramebufferPixelLocalStorageParameterfvANGLE \
    GL_GetFramebufferPixelLocalStorageParameterfvANGLE
#define glGetFramebufferPixelLocalStorageParameterfvRobustANGLE \
    GL_GetFramebufferPixelLocalStorageParameterfvRobustANGLE
#define glGetFramebufferPixelLocalStorageParameterivANGLE \
    GL_GetFramebufferPixelLocalStorageParameterivANGLE
#define glGetFramebufferPixelLocalStorageParameterivRobustANGLE \
    GL_GetFramebufferPixelLocalStorageParameterivRobustANGLE
#define glPixelLocalStorageBarrierANGLE GL_PixelLocalStorageBarrierANGLE
#define glInvalidateTextureANGLE GL_InvalidateTextureANGLE
#define glTexImage2DExternalANGLE GL_TexImage2DExternalANGLE
#define glGetMultisamplefvANGLE GL_GetMultisamplefvANGLE
#define glSampleMaskiANGLE GL_SampleMaskiANGLE
#define glTexStorage2DMultisampleANGLE GL_TexStorage2DMultisampleANGLE
#define glGetTranslatedShaderSourceANGLE GL_GetTranslatedShaderSourceANGLE
#define glAcquireTexturesANGLE GL_AcquireTexturesANGLE
#define glReleaseTexturesANGLE GL_ReleaseTexturesANGLE
#define glBindUniformLocationCHROMIUM GL_BindUniformLocationCHROMIUM
#define glCompressedCopyTextureCHROMIUM GL_CompressedCopyTextureCHROMIUM
#define glCopySubTextureCHROMIUM GL_CopySubTextureCHROMIUM
#define glCopyTextureCHROMIUM GL_CopyTextureCHROMIUM
#define glCoverageModulationCHROMIUM GL_CoverageModulationCHROMIUM
#define glLoseContextCHROMIUM GL_LoseContextCHROMIUM
#define glEGLImageTargetTexStorageEXT GL_EGLImageTargetTexStorageEXT
#define glEGLImageTargetTextureStorageEXT GL_EGLImageTargetTextureStorageEXT
#define glDrawArraysInstancedBaseInstanceEXT GL_DrawArraysInstancedBaseInstanceEXT
#define glDrawElementsInstancedBaseInstanceEXT GL_DrawElementsInstancedBaseInstanceEXT
#define glDrawElementsInstancedBaseVertexBaseInstanceEXT \
    GL_DrawElementsInstancedBaseVertexBaseInstanceEXT
#define glBindFragDataLocationEXT GL_BindFragDataLocationEXT
#define glBindFragDataLocationIndexedEXT GL_BindFragDataLocationIndexedEXT
#define glGetFragDataIndexEXT GL_GetFragDataIndexEXT
#define glGetProgramResourceLocationIndexEXT GL_GetProgramResourceLocationIndexEXT
#define glBufferStorageEXT GL_BufferStorageEXT
#define glClipControlEXT GL_ClipControlEXT
#define glCopyImageSubDataEXT GL_CopyImageSubDataEXT
#define glGetObjectLabelEXT GL_GetObjectLabelEXT
#define glLabelObjectEXT GL_LabelObjectEXT
#define glInsertEventMarkerEXT GL_InsertEventMarkerEXT
#define glPopGroupMarkerEXT GL_PopGroupMarkerEXT
#define glPushGroupMarkerEXT GL_PushGroupMarkerEXT
#define glDiscardFramebufferEXT GL_DiscardFramebufferEXT
#define glBeginQueryEXT GL_BeginQueryEXT
#define glDeleteQueriesEXT GL_DeleteQueriesEXT
#define glEndQueryEXT GL_EndQueryEXT
#define glGenQueriesEXT GL_GenQueriesEXT
#define glGetInteger64vEXT GL_GetInteger64vEXT
#define glGetQueryObjecti64vEXT GL_GetQueryObjecti64vEXT
#define glGetQueryObjectivEXT GL_GetQueryObjectivEXT
#define glGetQueryObjectui64vEXT GL_GetQueryObjectui64vEXT
#define glGetQueryObjectuivEXT GL_GetQueryObjectuivEXT
#define glGetQueryivEXT GL_GetQueryivEXT
#define glIsQueryEXT GL_IsQueryEXT
#define glQueryCounterEXT GL_QueryCounterEXT
#define glDrawBuffersEXT GL_DrawBuffersEXT
#define glBlendEquationSeparateiEXT GL_BlendEquationSeparateiEXT
#define glBlendEquationiEXT GL_BlendEquationiEXT
#define glBlendFuncSeparateiEXT GL_BlendFuncSeparateiEXT
#define glBlendFunciEXT GL_BlendFunciEXT
#define glColorMaskiEXT GL_ColorMaskiEXT
#define glDisableiEXT GL_DisableiEXT
#define glEnableiEXT GL_EnableiEXT
#define glIsEnablediEXT GL_IsEnablediEXT
#define glDrawElementsBaseVertexEXT GL_DrawElementsBaseVertexEXT
#define glDrawElementsInstancedBaseVertexEXT GL_DrawElementsInstancedBaseVertexEXT
#define glDrawRangeElementsBaseVertexEXT GL_DrawRangeElementsBaseVertexEXT
#define glMultiDrawElementsBaseVertexEXT GL_MultiDrawElementsBaseVertexEXT
#define glBufferStorageExternalEXT GL_BufferStorageExternalEXT
#define glNamedBufferStorageExternalEXT GL_NamedBufferStorageExternalEXT
#define glFramebufferTextureEXT GL_FramebufferTextureEXT
#define glDrawArraysInstancedEXT GL_DrawArraysInstancedEXT
#define glDrawElementsInstancedEXT GL_DrawElementsInstancedEXT
#define glVertexAttribDivisorEXT GL_VertexAttribDivisorEXT
#define glFlushMappedBufferRangeEXT GL_FlushMappedBufferRangeEXT
#define glMapBufferRangeEXT GL_MapBufferRangeEXT
#define glBufferStorageMemEXT GL_BufferStorageMemEXT
#define glCreateMemoryObjectsEXT GL_CreateMemoryObjectsEXT
#define glDeleteMemoryObjectsEXT GL_DeleteMemoryObjectsEXT
#define glGetMemoryObjectParameterivEXT GL_GetMemoryObjectParameterivEXT
#define glGetUnsignedBytei_vEXT GL_GetUnsignedBytei_vEXT
#define glGetUnsignedBytevEXT GL_GetUnsignedBytevEXT
#define glIsMemoryObjectEXT GL_IsMemoryObjectEXT
#define glMemoryObjectParameterivEXT GL_MemoryObjectParameterivEXT
#define glTexStorageMem2DEXT GL_TexStorageMem2DEXT
#define glTexStorageMem2DMultisampleEXT GL_TexStorageMem2DMultisampleEXT
#define glTexStorageMem3DEXT GL_TexStorageMem3DEXT
#define glTexStorageMem3DMultisampleEXT GL_TexStorageMem3DMultisampleEXT
#define glImportMemoryFdEXT GL_ImportMemoryFdEXT
#define glMultiDrawArraysIndirectEXT GL_MultiDrawArraysIndirectEXT
#define glMultiDrawElementsIndirectEXT GL_MultiDrawElementsIndirectEXT
#define glFramebufferTexture2DMultisampleEXT GL_FramebufferTexture2DMultisampleEXT
#define glRenderbufferStorageMultisampleEXT GL_RenderbufferStorageMultisampleEXT
#define glPolygonOffsetClampEXT GL_PolygonOffsetClampEXT
#define glPrimitiveBoundingBoxEXT GL_PrimitiveBoundingBoxEXT
#define glGetGraphicsResetStatusEXT GL_GetGraphicsResetStatusEXT
#define glGetnUniformfvEXT GL_GetnUniformfvEXT
#define glGetnUniformivEXT GL_GetnUniformivEXT
#define glReadnPixelsEXT GL_ReadnPixelsEXT
#define glDeleteSemaphoresEXT GL_DeleteSemaphoresEXT
#define glGenSemaphoresEXT GL_GenSemaphoresEXT
#define glGetSemaphoreParameterui64vEXT GL_GetSemaphoreParameterui64vEXT
#define glIsSemaphoreEXT GL_IsSemaphoreEXT
#define glSemaphoreParameterui64vEXT GL_SemaphoreParameterui64vEXT
#define glSignalSemaphoreEXT GL_SignalSemaphoreEXT
#define glWaitSemaphoreEXT GL_WaitSemaphoreEXT
#define glImportSemaphoreFdEXT GL_ImportSemaphoreFdEXT
#define glActiveShaderProgramEXT GL_ActiveShaderProgramEXT
#define glBindProgramPipelineEXT GL_BindProgramPipelineEXT
#define glCreateShaderProgramvEXT GL_CreateShaderProgramvEXT
#define glDeleteProgramPipelinesEXT GL_DeleteProgramPipelinesEXT
#define glGenProgramPipelinesEXT GL_GenProgramPipelinesEXT
#define glGetProgramPipelineInfoLogEXT GL_GetProgramPipelineInfoLogEXT
#define glGetProgramPipelineivEXT GL_GetProgramPipelineivEXT
#define glIsProgramPipelineEXT GL_IsProgramPipelineEXT
#define glProgramParameteriEXT GL_ProgramParameteriEXT
#define glProgramUniform1fEXT GL_ProgramUniform1fEXT
#define glProgramUniform1fvEXT GL_ProgramUniform1fvEXT
#define glProgramUniform1iEXT GL_ProgramUniform1iEXT
#define glProgramUniform1ivEXT GL_ProgramUniform1ivEXT
#define glProgramUniform1uiEXT GL_ProgramUniform1uiEXT
#define glProgramUniform1uivEXT GL_ProgramUniform1uivEXT
#define glProgramUniform2fEXT GL_ProgramUniform2fEXT
#define glProgramUniform2fvEXT GL_ProgramUniform2fvEXT
#define glProgramUniform2iEXT GL_ProgramUniform2iEXT
#define glProgramUniform2ivEXT GL_ProgramUniform2ivEXT
#define glProgramUniform2uiEXT GL_ProgramUniform2uiEXT
#define glProgramUniform2uivEXT GL_ProgramUniform2uivEXT
#define glProgramUniform3fEXT GL_ProgramUniform3fEXT
#define glProgramUniform3fvEXT GL_ProgramUniform3fvEXT
#define glProgramUniform3iEXT GL_ProgramUniform3iEXT
#define glProgramUniform3ivEXT GL_ProgramUniform3ivEXT
#define glProgramUniform3uiEXT GL_ProgramUniform3uiEXT
#define glProgramUniform3uivEXT GL_ProgramUniform3uivEXT
#define glProgramUniform4fEXT GL_ProgramUniform4fEXT
#define glProgramUniform4fvEXT GL_ProgramUniform4fvEXT
#define glProgramUniform4iEXT GL_ProgramUniform4iEXT
#define glProgramUniform4ivEXT GL_ProgramUniform4ivEXT
#define glProgramUniform4uiEXT GL_ProgramUniform4uiEXT
#define glProgramUniform4uivEXT GL_ProgramUniform4uivEXT
#define glProgramUniformMatrix2fvEXT GL_ProgramUniformMatrix2fvEXT
#define glProgramUniformMatrix2x3fvEXT GL_ProgramUniformMatrix2x3fvEXT
#define glProgramUniformMatrix2x4fvEXT GL_ProgramUniformMatrix2x4fvEXT
#define glProgramUniformMatrix3fvEXT GL_ProgramUniformMatrix3fvEXT
#define glProgramUniformMatrix3x2fvEXT GL_ProgramUniformMatrix3x2fvEXT
#define glProgramUniformMatrix3x4fvEXT GL_ProgramUniformMatrix3x4fvEXT
#define glProgramUniformMatrix4fvEXT GL_ProgramUniformMatrix4fvEXT
#define glProgramUniformMatrix4x2fvEXT GL_ProgramUniformMatrix4x2fvEXT
#define glProgramUniformMatrix4x3fvEXT GL_ProgramUniformMatrix4x3fvEXT
#define glUseProgramStagesEXT GL_UseProgramStagesEXT
#define glValidateProgramPipelineEXT GL_ValidateProgramPipelineEXT
#define glFramebufferFetchBarrierEXT GL_FramebufferFetchBarrierEXT
#define glPatchParameteriEXT GL_PatchParameteriEXT
#define glGetSamplerParameterIivEXT GL_GetSamplerParameterIivEXT
#define glGetSamplerParameterIuivEXT GL_GetSamplerParameterIuivEXT
#define glGetTexParameterIivEXT GL_GetTexParameterIivEXT
#define glGetTexParameterIuivEXT GL_GetTexParameterIuivEXT
#define glSamplerParameterIivEXT GL_SamplerParameterIivEXT
#define glSamplerParameterIuivEXT GL_SamplerParameterIuivEXT
#define glTexParameterIivEXT GL_TexParameterIivEXT
#define glTexParameterIuivEXT GL_TexParameterIuivEXT
#define glTexBufferEXT GL_TexBufferEXT
#define glTexBufferRangeEXT GL_TexBufferRangeEXT
#define glTexStorage1DEXT GL_TexStorage1DEXT
#define glTexStorage2DEXT GL_TexStorage2DEXT
#define glTexStorage3DEXT GL_TexStorage3DEXT
#define glBlendBarrierKHR GL_BlendBarrierKHR
#define glDebugMessageCallbackKHR GL_DebugMessageCallbackKHR
#define glDebugMessageControlKHR GL_DebugMessageControlKHR
#define glDebugMessageInsertKHR GL_DebugMessageInsertKHR
#define glGetDebugMessageLogKHR GL_GetDebugMessageLogKHR
#define glGetObjectLabelKHR GL_GetObjectLabelKHR
#define glGetObjectPtrLabelKHR GL_GetObjectPtrLabelKHR
#define glGetPointervKHR GL_GetPointervKHR
#define glObjectLabelKHR GL_ObjectLabelKHR
#define glObjectPtrLabelKHR GL_ObjectPtrLabelKHR
#define glPopDebugGroupKHR GL_PopDebugGroupKHR
#define glPushDebugGroupKHR GL_PushDebugGroupKHR
#define glMaxShaderCompilerThreadsKHR GL_MaxShaderCompilerThreadsKHR
#define glFramebufferParameteriMESA GL_FramebufferParameteriMESA
#define glGetFramebufferParameterivMESA GL_GetFramebufferParameterivMESA
#define glDeleteFencesNV GL_DeleteFencesNV
#define glFinishFenceNV GL_FinishFenceNV
#define glGenFencesNV GL_GenFencesNV
#define glGetFenceivNV GL_GetFenceivNV
#define glIsFenceNV GL_IsFenceNV
#define glSetFenceNV GL_SetFenceNV
#define glTestFenceNV GL_TestFenceNV
#define glBlitFramebufferNV GL_BlitFramebufferNV
#define glPolygonModeNV GL_PolygonModeNV
#define glEGLImageTargetRenderbufferStorageOES GL_EGLImageTargetRenderbufferStorageOES
#define glEGLImageTargetTexture2DOES GL_EGLImageTargetTexture2DOES
#define glCopyImageSubDataOES GL_CopyImageSubDataOES
#define glBlendEquationSeparateiOES GL_BlendEquationSeparateiOES
#define glBlendEquationiOES GL_BlendEquationiOES
#define glBlendFuncSeparateiOES GL_BlendFuncSeparateiOES
#define glBlendFunciOES GL_BlendFunciOES
#define glColorMaskiOES GL_ColorMaskiOES
#define glDisableiOES GL_DisableiOES
#define glEnableiOES GL_EnableiOES
#define glIsEnablediOES GL_IsEnablediOES
#define glDrawElementsBaseVertexOES GL_DrawElementsBaseVertexOES
#define glDrawElementsInstancedBaseVertexOES GL_DrawElementsInstancedBaseVertexOES
#define glDrawRangeElementsBaseVertexOES GL_DrawRangeElementsBaseVertexOES
#define glFramebufferTextureOES GL_FramebufferTextureOES
#define glGetProgramBinaryOES GL_GetProgramBinaryOES
#define glProgramBinaryOES GL_ProgramBinaryOES
#define glGetBufferPointervOES GL_GetBufferPointervOES
#define glMapBufferOES GL_MapBufferOES
#define glUnmapBufferOES GL_UnmapBufferOES
#define glPrimitiveBoundingBoxOES GL_PrimitiveBoundingBoxOES
#define glMinSampleShadingOES GL_MinSampleShadingOES
#define glCompressedTexImage3DOES GL_CompressedTexImage3DOES
#define glCompressedTexSubImage3DOES GL_CompressedTexSubImage3DOES
#define glCopyTexSubImage3DOES GL_CopyTexSubImage3DOES
#define glFramebufferTexture3DOES GL_FramebufferTexture3DOES
#define glTexImage3DOES GL_TexImage3DOES
#define glTexSubImage3DOES GL_TexSubImage3DOES
#define glGetSamplerParameterIivOES GL_GetSamplerParameterIivOES
#define glGetSamplerParameterIuivOES GL_GetSamplerParameterIuivOES
#define glGetTexParameterIivOES GL_GetTexParameterIivOES
#define glGetTexParameterIuivOES GL_GetTexParameterIuivOES
#define glSamplerParameterIivOES GL_SamplerParameterIivOES
#define glSamplerParameterIuivOES GL_SamplerParameterIuivOES
#define glTexParameterIivOES GL_TexParameterIivOES
#define glTexParameterIuivOES GL_TexParameterIuivOES
#define glTexBufferOES GL_TexBufferOES
#define glTexBufferRangeOES GL_TexBufferRangeOES
#define glTexStorage3DMultisampleOES GL_TexStorage3DMultisampleOES
#define glBindVertexArrayOES GL_BindVertexArrayOES
#define glDeleteVertexArraysOES GL_DeleteVertexArraysOES
#define glGenVertexArraysOES GL_GenVertexArraysOES
#define glIsVertexArrayOES GL_IsVertexArrayOES
#define glFramebufferTextureMultiviewOVR GL_FramebufferTextureMultiviewOVR
#define glShadingRateQCOM GL_ShadingRateQCOM
#define glDrawTexfOES GL_DrawTexfOES
#define glDrawTexfvOES GL_DrawTexfvOES
#define glDrawTexiOES GL_DrawTexiOES
#define glDrawTexivOES GL_DrawTexivOES
#define glDrawTexsOES GL_DrawTexsOES
#define glDrawTexsvOES GL_DrawTexsvOES
#define glDrawTexxOES GL_DrawTexxOES
#define glDrawTexxvOES GL_DrawTexxvOES
#define glBindFramebufferOES GL_BindFramebufferOES
#define glBindRenderbufferOES GL_BindRenderbufferOES
#define glCheckFramebufferStatusOES GL_CheckFramebufferStatusOES
#define glDeleteFramebuffersOES GL_DeleteFramebuffersOES
#define glDeleteRenderbuffersOES GL_DeleteRenderbuffersOES
#define glFramebufferRenderbufferOES GL_FramebufferRenderbufferOES
#define glFramebufferTexture2DOES GL_FramebufferTexture2DOES
#define glGenFramebuffersOES GL_GenFramebuffersOES
#define glGenRenderbuffersOES GL_GenRenderbuffersOES
#define glGenerateMipmapOES GL_GenerateMipmapOES
#define glGetFramebufferAttachmentParameterivOES GL_GetFramebufferAttachmentParameterivOES
#define glGetRenderbufferParameterivOES GL_GetRenderbufferParameterivOES
#define glIsFramebufferOES GL_IsFramebufferOES
#define glIsRenderbufferOES GL_IsRenderbufferOES
#define glRenderbufferStorageOES GL_RenderbufferStorageOES
#define glCurrentPaletteMatrixOES GL_CurrentPaletteMatrixOES
#define glLoadPaletteFromModelViewMatrixOES GL_LoadPaletteFromModelViewMatrixOES
#define glMatrixIndexPointerOES GL_MatrixIndexPointerOES
#define glWeightPointerOES GL_WeightPointerOES
#define glPointSizePointerOES GL_PointSizePointerOES
#define glQueryMatrixxOES GL_QueryMatrixxOES
#define glGetTexGenfvOES GL_GetTexGenfvOES
#define glGetTexGenivOES GL_GetTexGenivOES
#define glGetTexGenxvOES GL_GetTexGenxvOES
#define glTexGenfOES GL_TexGenfOES
#define glTexGenfvOES GL_TexGenfvOES
#define glTexGeniOES GL_TexGeniOES
#define glTexGenivOES GL_TexGenivOES
#define glTexGenxOES GL_TexGenxOES
#define glTexGenxvOES GL_TexGenxvOES

#ifdef __cplusplus
}
#endif

#endif