package pl.zb3.freej2me.bridge.gles2;

import pl.zb3.freej2me.NativeLoader;

public class GLES2 {
    static {
		NativeLoader.loadLibrary("gles2bridge");
	}

    public static boolean DEBUG = true;
    public static boolean bound = false;
    // one connection, so the class is static
    private static Object handle = null;
    private static String exceptionReason = null;

    public static void doThrow(String reason) {
        exceptionReason = reason;
    }

    private static void checkException() {
        if (DEBUG && exceptionReason == null) {
            //Thread.dumpStack();
            //checkGLError(handle);
        }
        if (exceptionReason != null) {
            String reason = exceptionReason;
            exceptionReason = null;

            throw new IllegalStateException(reason);
        }
    }

    /*
     *
     */

    // this will generally ensure "context"
    // for webgl compat, we'll put antialias in create as that is done
    // when creating the context, assuming no FBOs are used
    public static void ensure() {
        ensure(false);
    }

    public static void ensure(boolean antialias) {
        if (handle == null) {
            handle = _create(antialias);
            checkException();
        }
    }

    private static native Object _create(boolean antialias);

    // well this might either switch current surface (but not context)
    // or have map of surfaces to sizes (and use fbo in webgl)
    public static void setSurface(int width, int height) {
        setSurface(handle, width, height);
        checkException();
    }

    private static native void setSurface(Object handle, int width, int height);

    public static void bind() {
        bind(handle);
        checkException();
        bound = true;
    }

    private static native void bind(Object handle);

    public static void release() {
        release(handle);
        checkException();
        bound = false;
    }

    private static native void release(Object handle);

    public static void destroy() {
        destroy(handle);
        handle = null;
        bound = false;
    }

    private static native void destroy(Object handle);

    private static native void checkGLError(Object handle);

    public static int createProgram(String vertexSource, String fragmentSource) {
        int ret = createProgram(handle, vertexSource, fragmentSource);
        checkException();
        return ret;
    }

    public static native int createProgram(Object handle, String vertexSource, String fragmentSource);


    public static void useProgram(int program) {
        useProgram(handle, program);
        checkException();
    }
    public static native void useProgram(Object handle, int program);

    public static void deleteProgram(int program) {
        deleteProgram(handle, program);
        checkException();
    }
    public static native void deleteProgram(Object handle, int program);

    public static int getAttribLocation(int program, String name) {
        int ret = getAttribLocation(handle, program, name);
        checkException();
        return ret;
    }
    public static native int getAttribLocation(Object handle, int program, String name);

    public static int getUniformLocation(int program, String name) {
        int ret = getUniformLocation(handle, program, name);
        checkException();
        return ret;
    }
    public static native int getUniformLocation(Object handle, int program, String name);

    public static void vertexAttribPointer(int attr, int size, int type, boolean normalized, int stride, int offset) {
        vertexAttribPointer(handle, attr, size, type, normalized, stride, offset);
        checkException();
    }
    public static native void vertexAttribPointer(Object handle, int attr, int size, int type, boolean normalized, int stride, int offset);

    public static void vertexAttrib2f(int attr, float f1, float f2) {
        vertexAttrib2f(handle, attr, f1, f2);
        checkException();
    }
    public static native void vertexAttrib2f(Object handle, int attr, float f1, float f2);

    public static void vertexAttrib3f(int attr, float f1, float f2, float f3) {
        vertexAttrib3f(handle, attr, f1, f2, f3);
        checkException();
    }
    public static native void vertexAttrib3f(Object handle, int attr, float f1, float f2, float f3);

    public static void vertexAttrib4f(int attr, float f1, float f2, float f3, float f4) {
        vertexAttrib4f(handle, attr, f1, f2, f3, f4);
        checkException();
    }
    public static native void vertexAttrib4f(Object handle, int attr, float f1, float f2, float f3, float f4);

    public static void uniform1f(int loc, float f1) {
        uniform1f(handle, loc, f1);
        checkException();
    }
    public static native void uniform1f(Object handle, int loc, float f1);

    public static void uniform1i(int loc, int i1) {
        uniform1i(handle, loc, i1);
        checkException();
    }
    public static native void uniform1i(Object handle, int loc, int i1);

    public static void uniform2f(int loc, float f1, float f2) {
        uniform2f(handle, loc, f1, f2);
        checkException();
    }
    public static native void uniform2f(Object handle, int loc, float f1, float f2);

    public static void uniform3f(int loc, float f1, float f2, float f3) {
        uniform3f(handle, loc, f1, f2, f3);
        checkException();
    }
    public static native void uniform3f(Object handle, int loc, float f1, float f2, float f3);

    public static void uniform4f(int loc, float f1, float f2, float f3, float f4) {
        uniform4f(handle, loc, f1, f2, f3, f4);
        checkException();
    }
    public static native void uniform4f(Object handle, int loc, float f1, float f2, float f3, float f4);

    public static void uniform3fv(int loc, float[] fa) {
        uniform3fv(handle, loc, fa);
        checkException();
    }
    public static native void uniform3fv(Object handle, int loc, float[] fa);

    public static void uniform4fv(int loc, float[] fa) {
        uniform4fv(handle, loc, fa);
        checkException();
    }
    public static native void uniform4fv(Object handle, int loc, float[] fa);

    public static void uniformMatrix3fv(int loc, boolean transpose, float[] fa) {
        uniformMatrix3fv(handle, loc, transpose, fa);
        checkException();
    }
    public static native void uniformMatrix3fv(Object handle, int loc, boolean transpose, float[] fa);

    public static void uniformMatrix4fv(int loc, boolean transpose, float[] fa) {
        uniformMatrix4fv(handle, loc, transpose, fa);
        checkException();
    }
    public static native void uniformMatrix4fv(Object handle, int loc, boolean transpose, float[] fa);

    public static int createBuffer() {
        int ret = createBuffer(handle);
        checkException();
        return ret;
    }
    public static native int createBuffer(Object handle);

    public static void deleteBuffers(int[] idsArray) {
        deleteBuffers(handle, idsArray);
        checkException();
    }
    public static native void deleteBuffers(Object handle, int[] idsArray);

    public static void bindBuffer(int type, int id) {
        bindBuffer(handle, type, id);
        checkException();
    }
    public static native void bindBuffer(Object handle, int type, int id);

    public static void bufferData(int type, int size, int usage) {
        bufferData(handle, type, size, usage);
        checkException();
    }
    public static native void bufferData(Object handle, int type, int size, int usage);

    public static void bufferSubData(int type, int offset, int byteSize, byte[] vmArray) {
        bufferSubData(handle, type, offset, byteSize, vmArray);
        checkException();
    }
    public static native void bufferSubData(Object handle, int type, int offset, int byteSize, byte[] vmArray);

    public static void bufferSubData(int type, int offset, int byteSize, short[] vmArray) {
        bufferSubData(handle, type, offset, byteSize, vmArray);
        checkException();
    }
    public static native void bufferSubData(Object handle, int type, int offset, int byteSize, short[] vmArray);

    public static void bufferSubData(int type, int offset, int byteSize, float[] vmArray) {
        bufferSubData(handle, type, offset, byteSize, vmArray);
        checkException();
    }
    public static native void bufferSubData(Object handle, int type, int offset, int byteSize, float[] vmArray);

    public static void bufferSubData(int type, int offset, int byteSize, int[] vmArray) {
        bufferSubData(handle, type, offset, byteSize, vmArray);
        checkException();
    }
    public static native void bufferSubData(Object handle, int type, int offset, int byteSize, int[] vmArray);

    public static int createTexture() {
        int ret = createTexture(handle);
        checkException();
        return ret;
    }
    public static native int createTexture(Object handle);

    public static void deleteTexture(int texture) {
        deleteTexture(handle, texture);
        checkException();
    }
    public static native void deleteTexture(Object handle, int texture);

    public static void activeTexture(int texture) {
        activeTexture(handle, texture);
        checkException();
    }
    public static native void activeTexture(Object handle, int texture);

    public static void bindTexture(int target, int texture) {
        bindTexture(handle, target, texture);
        checkException();
    }
    public static native void bindTexture(Object handle, int target, int texture);

    public static void texParameterf(int target, int pname, float param) {
        texParameterf(handle, target, pname, param);
        checkException();
    }
    public static native void texParameterf(Object handle, int target, int pname, float param);

    public static void texParameteri(int target, int pname, int param) {
        texParameteri(handle, target, pname, param);
        checkException();
    }
    public static native void texParameteri(Object handle, int target, int pname, int param);

    public static void texImage2D(int target, int level, int intFormat, int width, int height, int border, int format, int type, byte[] javaBytes) {
        texImage2D(handle, target, level, intFormat, width, height, border, format, type, javaBytes);
        checkException();
    }
    public static native void texImage2D(Object handle, int target, int level, int intFormat, int width, int height, int border, int format, int type, byte[] javaBytes);

    public static void generateMipmap(int target) {
        generateMipmap(handle, target);
        checkException();
    }
    public static native void generateMipmap(Object handle, int target);

    public static void blendFunc(int sfactor, int dfactor) {
        blendFunc(handle, sfactor, dfactor);
        checkException();
    }
    public static native void blendFunc(Object handle, int sfactor, int dfactor);

    public static void clear(int mask) {
        clear(handle, mask);
        checkException();
    }
    public static native void clear(Object handle, int mask);

    public static void clearColor(float r, float g, float b, float a) {
        clearColor(handle, r, g, b, a);
        checkException();
    }
    public static native void clearColor(Object handle, float r, float g, float b, float a);

    public static void clearDepthf(float depth) {
        clearDepthf(handle, depth);
        checkException();
    }
    public static native void clearDepthf(Object handle, float depth);

    public static void colorMask(boolean r, boolean g, boolean b, boolean a) {
        colorMask(handle, r, g, b, a);
        checkException();
    }
    public static native void colorMask(Object handle, boolean r, boolean g, boolean b, boolean a);

    public static void cullFace(int mode) {
        cullFace(handle, mode);
        checkException();
    }
    public static native void cullFace(Object handle, int mode);

    public static void depthFunc(int mode) {
        depthFunc(handle, mode);
        checkException();
    }
    public static native void depthFunc(Object handle, int mode);

    public static void depthMask(boolean flag) {
        depthMask(handle, flag);
        checkException();
    }
    public static native void depthMask(Object handle, boolean flag);

    public static void disable(int flag) {
        disable(handle, flag);
        checkException();
    }
    public static native void disable(Object handle, int flag);

    public static void disableVertexAttribArray(int index) {
        disableVertexAttribArray(handle, index);
        checkException();
    }
    public static native void disableVertexAttribArray(Object handle, int index);

    public static void drawArrays(int mode, int first, int count) {
        drawArrays(handle, mode, first, count);
        checkException();
    }
    public static native void drawArrays(Object handle, int mode, int first, int count);

    public static void drawElements(int mode, int count, int type, int offset) {
        drawElements(handle, mode, count, type, offset);
        checkException();
    }
    public static native void drawElements(Object handle, int mode, int count, int type, int offset);

    public static void enable(int flag) {
        enable(handle, flag);
        checkException();
    }
    public static native void enable(Object handle, int flag);

    public static void enableVertexAttribArray(int index) {
        enableVertexAttribArray(handle, index);
        checkException();
    }
    public static native void enableVertexAttribArray(Object handle, int index);

    public static void finish() {
        finish(handle);
        checkException();
    }
    public static native void finish(Object handle);

    public static void frontFace(int mode) {
        frontFace(handle, mode);
        checkException();
    }
    public static native void frontFace(Object handle, int mode);

    public static void pixelStorei(int pname, int param) {
        pixelStorei(handle, pname, param);
        checkException();
    }
    public static native void pixelStorei(Object handle, int pname, int param);

    public static void polygonOffset(float factor, float units) {
        polygonOffset(handle, factor, units);
        checkException();
    }
    public static native void polygonOffset(Object handle, float factor, float units);

    public static void readPixels(int x, int y, int width, int height, byte[] pixels) {
        readPixels(handle, x, y, width, height, pixels);
        checkException();
    }
    public static native void readPixels(Object handle, int x, int y, int width, int height, byte[] pixels);

    public static void scissor(int x, int y, int width, int height) {
        scissor(handle, x, y, width, height);
        checkException();
    }
    public static native void scissor(Object handle, int x, int y, int width, int height);

    public static void viewport(int x, int y, int width, int height) {
        viewport(handle, x, y, width, height);
        checkException();
    }
    public static native void viewport(Object handle, int x, int y, int width, int height);

    public static void blendColor(float r, float g, float b, float a) {
        blendColor(handle, r, g, b, a);
        checkException();
    }
    public static native void blendColor(Object handle, float r, float g, float b, float a);

    public static void blendEquation(int mode) {
        blendEquation(handle, mode);
        checkException();
    }
    public static native void blendEquation(Object handle, int mode);

    public static void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        blendFuncSeparate(handle, srcRGB, dstRGB, srcAlpha, dstAlpha);
        checkException();
    }
    public static native void blendFuncSeparate(Object handle, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    public static boolean isTexture(int texture) {
        boolean ret = isTexture(handle, texture);
        checkException();
        return ret;
    }
    public static native boolean isTexture(Object handle, int texture);

    public static void toggleAnisotropy(boolean enable) {
        toggleAnisotropy(handle, enable);
        checkException();
    }
    public static native void toggleAnisotropy(Object handle, boolean enable);


    public static class Constants {
        public static final int GL_ACTIVE_ATTRIBUTES                = 0x8B89;
        public static final int GL_ACTIVE_TEXTURE                 = 0x84E0;
        public static final int GL_ACTIVE_UNIFORMS                  = 0x8B86;
        public static final int GL_ALIASED_LINE_WIDTH_RANGE       = 0x846E;
        public static final int GL_ALIASED_POINT_SIZE_RANGE       = 0x846D;
        public static final int GL_ALPHA                          = 0x1906;
        public static final int GL_ALPHA_BITS                     = 0x0D55;
        public static final int GL_ALWAYS                         = 0x0207;
        public static final int GL_ARRAY_BUFFER                   = 0x8892;
        public static final int GL_ARRAY_BUFFER_BINDING           = 0x8894;
        public static final int GL_ATTACHED_SHADERS                 = 0x8B85;
        public static final int GL_BACK                           = 0x0405;
        public static final int GL_BLEND                          = 0x0BE2;
        public static final int GL_BLEND_COLOR                    = 0x8005;
        public static final int GL_BLEND_DST_ALPHA                = 0x80CA;
        public static final int GL_BLEND_DST_RGB                  = 0x80C8;
        public static final int GL_BLEND_EQUATION                 = 0x8009;
        public static final int GL_BLEND_EQUATION_ALPHA           = 0x883D;
        public static final int GL_BLEND_EQUATION_RGB             = 0x8009;   /* same as BLEND_EQUATION */
        public static final int GL_BLEND_SRC_ALPHA                = 0x80CB;
        public static final int GL_BLEND_SRC_RGB                  = 0x80C9;
        public static final int GL_BLUE_BITS                      = 0x0D54;
        public static final int GL_BOOL                           = 0x8B56;
        public static final int GL_BOOL_VEC2                      = 0x8B57;
        public static final int GL_BOOL_VEC3                      = 0x8B58;
        public static final int GL_BOOL_VEC4                      = 0x8B59;
        public static final int GL_BUFFER_SIZE                    = 0x8764;
        public static final int GL_BUFFER_USAGE                   = 0x8765;
        public static final int GL_BYTE                           = 0x1400;
        public static final int GL_CCW                            = 0x0901;
        public static final int GL_CLAMP_TO_EDGE                  = 0x812F;
        public static final int GL_COLOR_ATTACHMENT0              = 0x8CE0;
        public static final int GL_COLOR_BUFFER_BIT               = 0x00004000;
        public static final int GL_COLOR_CLEAR_VALUE              = 0x0C22;
        public static final int GL_COLOR_WRITEMASK                = 0x0C23;
        public static final int GL_COMPILE_STATUS                 = 0x8B81;
        public static final int GL_COMPRESSED_TEXTURE_FORMATS     = 0x86A3;
        public static final int GL_CONSTANT_ALPHA                 = 0x8003;
        public static final int GL_CONSTANT_COLOR                 = 0x8001;
        public static final int GL_CULL_FACE                      = 0x0B44;
        public static final int GL_CULL_FACE_MODE                 = 0x0B45;
        public static final int GL_CURRENT_PROGRAM                  = 0x8B8D;
        public static final int GL_CURRENT_VERTEX_ATTRIB          = 0x8626;
        public static final int GL_CW                             = 0x0900;
        public static final int GL_DECR                           = 0x1E03;
        public static final int GL_DECR_WRAP                      = 0x8508;
        public static final int GL_DELETE_STATUS                    = 0x8B80;
        public static final int GL_DEPTH_ATTACHMENT               = 0x8D00;
        public static final int GL_DEPTH_BITS                     = 0x0D56;
        public static final int GL_DEPTH_BUFFER_BIT               = 0x00000100;
        public static final int GL_DEPTH_CLEAR_VALUE              = 0x0B73;
        public static final int GL_DEPTH_COMPONENT                = 0x1902;
        public static final int GL_DEPTH_COMPONENT16              = 0x81A5;
        public static final int GL_DEPTH_FUNC                     = 0x0B74;
        public static final int GL_DEPTH_RANGE                    = 0x0B70;
        public static final int GL_DEPTH_STENCIL                  = 0x84F9;
        public static final int GL_DEPTH_STENCIL_ATTACHMENT       = 0x821A;
        public static final int GL_DEPTH_TEST                     = 0x0B71;
        public static final int GL_DEPTH_WRITEMASK                = 0x0B72;
        public static final int GL_DITHER                         = 0x0BD0;
        public static final int GL_DONT_CARE                      = 0x1100;
        public static final int GL_DST_ALPHA                      = 0x0304;
        public static final int GL_DST_COLOR                      = 0x0306;
        public static final int GL_DYNAMIC_DRAW                   = 0x88E8;
        public static final int GL_ELEMENT_ARRAY_BUFFER           = 0x8893;
        public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING   = 0x8895;
        public static final int GL_EQUAL                          = 0x0202;
        public static final int GL_FASTEST                        = 0x1101;
        public static final int GL_FLOAT                          = 0x1406;
        public static final int GL_FLOAT_MAT2                     = 0x8B5A;
        public static final int GL_FLOAT_MAT3                     = 0x8B5B;
        public static final int GL_FLOAT_MAT4                     = 0x8B5C;
        public static final int GL_FLOAT_VEC2                     = 0x8B50;
        public static final int GL_FLOAT_VEC3                     = 0x8B51;
        public static final int GL_FLOAT_VEC4                     = 0x8B52;
        public static final int GL_FRAGMENT_SHADER                  = 0x8B30;
        public static final int GL_FRAMEBUFFER                    = 0x8D40;
        public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME           = 0x8CD1;
        public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE           = 0x8CD0;
        public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;
        public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL         = 0x8CD2;
        public static final int GL_FRAMEBUFFER_BINDING            = 0x8CA6;
        public static final int GL_FRAMEBUFFER_COMPLETE                      = 0x8CD5;
        public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT         = 0x8CD6;
        public static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS         = 0x8CD9;
        public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
        public static final int GL_FRAMEBUFFER_UNSUPPORTED                   = 0x8CDD;
        public static final int GL_FRONT                          = 0x0404;
        public static final int GL_FRONT_AND_BACK                 = 0x0408;
        public static final int GL_FRONT_FACE                     = 0x0B46;
        public static final int GL_FUNC_ADD                       = 0x8006;
        public static final int GL_FUNC_REVERSE_SUBTRACT          = 0x800B;
        public static final int GL_FUNC_SUBTRACT                  = 0x800A;
        public static final int GL_GENERATE_MIPMAP_HINT            = 0x8192;
        public static final int GL_GEQUAL                         = 0x0206;
        public static final int GL_GREATER                        = 0x0204;
        public static final int GL_GREEN_BITS                     = 0x0D53;
        public static final int GL_HIGH_FLOAT                     = 0x8DF2;
        public static final int GL_HIGH_INT                       = 0x8DF5;
        public static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B;
        public static final int GL_IMPLEMENTATION_COLOR_READ_TYPE   = 0x8B9A;
        public static final int GL_INCR                           = 0x1E02;
        public static final int GL_INCR_WRAP                      = 0x8507;
        public static final int GL_INT                            = 0x1404;
        public static final int GL_INT_VEC2                       = 0x8B53;
        public static final int GL_INT_VEC3                       = 0x8B54;
        public static final int GL_INT_VEC4                       = 0x8B55;
        public static final int GL_INVALID_ENUM                   = 0x0500;
        public static final int GL_INVALID_FRAMEBUFFER_OPERATION  = 0x0506;
        public static final int GL_INVALID_OPERATION              = 0x0502;
        public static final int GL_INVALID_VALUE                  = 0x0501;
        public static final int GL_INVERT                         = 0x150A;
        public static final int GL_KEEP                           = 0x1E00;
        public static final int GL_LEQUAL                         = 0x0203;
        public static final int GL_LESS                           = 0x0201;
        public static final int GL_LINEAR                         = 0x2601;
        public static final int GL_LINEAR_MIPMAP_LINEAR           = 0x2703;
        public static final int GL_LINEAR_MIPMAP_NEAREST          = 0x2701;
        public static final int GL_LINE_LOOP                      = 0x0002;
        public static final int GL_LINES                          = 0x0001;
        public static final int GL_LINE_STRIP                     = 0x0003;
        public static final int GL_LINE_WIDTH                     = 0x0B21;
        public static final int GL_LINK_STATUS                      = 0x8B82;
        public static final int GL_LOW_FLOAT                      = 0x8DF0;
        public static final int GL_LOW_INT                        = 0x8DF3;
        public static final int GL_LUMINANCE                      = 0x1909;
        public static final int GL_LUMINANCE_ALPHA                = 0x190A;
        public static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
        public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE      = 0x851C;
        public static final int GL_MAX_FRAGMENT_UNIFORM_VECTORS     = 0x8DFD;
        public static final int GL_MAX_RENDERBUFFER_SIZE          = 0x84E8;
        public static final int GL_MAX_TEXTURE_IMAGE_UNITS          = 0x8872;
        public static final int GL_MAX_TEXTURE_SIZE               = 0x0D33;
        public static final int GL_MAX_VARYING_VECTORS              = 0x8DFC;
        public static final int GL_MAX_VERTEX_ATTRIBS               = 0x8869;
        public static final int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS   = 0x8B4C;
        public static final int GL_MAX_VERTEX_UNIFORM_VECTORS       = 0x8DFB;
        public static final int GL_MAX_VIEWPORT_DIMS              = 0x0D3A;
        public static final int GL_MEDIUM_FLOAT                   = 0x8DF1;
        public static final int GL_MEDIUM_INT                     = 0x8DF4;
        public static final int GL_MIRRORED_REPEAT                = 0x8370;
        public static final int GL_NEAREST                        = 0x2600;
        public static final int GL_NEAREST_MIPMAP_LINEAR          = 0x2702;
        public static final int GL_NEAREST_MIPMAP_NEAREST         = 0x2700;
        public static final int GL_NEVER                          = 0x0200;
        public static final int GL_NICEST                         = 0x1102;
        public static final int GL_NO_ERROR                       = 0;
        public static final int GL_NONE                           = 0;
        public static final int GL_NOTEQUAL                       = 0x0205;
        public static final int GL_ONE                            = 1;
        public static final int GL_ONE_MINUS_CONSTANT_ALPHA       = 0x8004;
        public static final int GL_ONE_MINUS_CONSTANT_COLOR       = 0x8002;
        public static final int GL_ONE_MINUS_DST_ALPHA            = 0x0305;
        public static final int GL_ONE_MINUS_DST_COLOR            = 0x0307;
        public static final int GL_ONE_MINUS_SRC_ALPHA            = 0x0303;
        public static final int GL_ONE_MINUS_SRC_COLOR            = 0x0301;
        public static final int GL_OUT_OF_MEMORY                  = 0x0505;
        public static final int GL_PACK_ALIGNMENT                 = 0x0D05;
        public static final int GL_POINTS                         = 0x0000;
        public static final int GL_POLYGON_OFFSET_FACTOR          = 0x8038;
        public static final int GL_POLYGON_OFFSET_FILL            = 0x8037;
        public static final int GL_POLYGON_OFFSET_UNITS           = 0x2A00;
        public static final int GL_RED_BITS                       = 0x0D52;
        public static final int GL_RENDERBUFFER                   = 0x8D41;
        public static final int GL_RENDERBUFFER_ALPHA_SIZE        = 0x8D53;
        public static final int GL_RENDERBUFFER_BINDING           = 0x8CA7;
        public static final int GL_RENDERBUFFER_BLUE_SIZE         = 0x8D52;
        public static final int GL_RENDERBUFFER_DEPTH_SIZE        = 0x8D54;
        public static final int GL_RENDERBUFFER_GREEN_SIZE        = 0x8D51;
        public static final int GL_RENDERBUFFER_HEIGHT            = 0x8D43;
        public static final int GL_RENDERBUFFER_INTERNAL_FORMAT   = 0x8D44;
        public static final int GL_RENDERBUFFER_RED_SIZE          = 0x8D50;
        public static final int GL_RENDERBUFFER_STENCIL_SIZE      = 0x8D55;
        public static final int GL_RENDERBUFFER_WIDTH             = 0x8D42;
        public static final int GL_RENDERER                       = 0x1F01;
        public static final int GL_REPEAT                         = 0x2901;
        public static final int GL_REPLACE                        = 0x1E01;
        public static final int GL_RGB                            = 0x1907;
        public static final int GL_RGB565                         = 0x8D62;
        public static final int GL_RGB5_A1                        = 0x8057;
        public static final int GL_RGBA                           = 0x1908;
        public static final int GL_RGBA4                          = 0x8056;
        public static final int GL_RGBA8                          = 0x8058;
        public static final int GL_SAMPLE_ALPHA_TO_COVERAGE       = 0x809E;
        public static final int GL_SAMPLE_BUFFERS                 = 0x80A8;
        public static final int GL_SAMPLE_COVERAGE                = 0x80A0;
        public static final int GL_SAMPLE_COVERAGE_INVERT         = 0x80AB;
        public static final int GL_SAMPLE_COVERAGE_VALUE          = 0x80AA;
        public static final int GL_SAMPLER_2D                     = 0x8B5E;
        public static final int GL_SAMPLER_CUBE                   = 0x8B60;
        public static final int GL_SAMPLES                        = 0x80A9;
        public static final int GL_SCISSOR_BOX                    = 0x0C10;
        public static final int GL_SCISSOR_TEST                   = 0x0C11;
        public static final int GL_SHADER_TYPE                      = 0x8B4F;
        public static final int GL_SHADING_LANGUAGE_VERSION         = 0x8B8C;
        public static final int GL_SHORT                          = 0x1402;
        public static final int GL_SRC_ALPHA                      = 0x0302;
        public static final int GL_SRC_ALPHA_SATURATE             = 0x0308;
        public static final int GL_SRC_COLOR                      = 0x0300;
        public static final int GL_STATIC_DRAW                    = 0x88E4;
        public static final int GL_STENCIL_ATTACHMENT             = 0x8D20;
        public static final int GL_STENCIL_BACK_FAIL              = 0x8801;
        public static final int GL_STENCIL_BACK_FUNC              = 0x8800;
        public static final int GL_STENCIL_BACK_PASS_DEPTH_FAIL   = 0x8802;
        public static final int GL_STENCIL_BACK_PASS_DEPTH_PASS   = 0x8803;
        public static final int GL_STENCIL_BACK_REF               = 0x8CA3;
        public static final int GL_STENCIL_BACK_VALUE_MASK        = 0x8CA4;
        public static final int GL_STENCIL_BACK_WRITEMASK         = 0x8CA5;
        public static final int GL_STENCIL_BITS                   = 0x0D57;
        public static final int GL_STENCIL_BUFFER_BIT             = 0x00000400;
        public static final int GL_STENCIL_CLEAR_VALUE            = 0x0B91;
        public static final int GL_STENCIL_FAIL                   = 0x0B94;
        public static final int GL_STENCIL_FUNC                   = 0x0B92;
        public static final int GL_STENCIL_INDEX8                 = 0x8D48;
        public static final int GL_STENCIL_PASS_DEPTH_FAIL        = 0x0B95;
        public static final int GL_STENCIL_PASS_DEPTH_PASS        = 0x0B96;
        public static final int GL_STENCIL_REF                    = 0x0B97;
        public static final int GL_STENCIL_TEST                   = 0x0B90;
        public static final int GL_STENCIL_VALUE_MASK             = 0x0B93;
        public static final int GL_STENCIL_WRITEMASK              = 0x0B98;
        public static final int GL_STREAM_DRAW                    = 0x88E0;
        public static final int GL_SUBPIXEL_BITS                  = 0x0D50;
        public static final int GL_TEXTURE0                       = 0x84C0;
        public static final int GL_TEXTURE                        = 0x1702;
        public static final int GL_TEXTURE10                      = 0x84CA;
        public static final int GL_TEXTURE1                       = 0x84C1;
        public static final int GL_TEXTURE11                      = 0x84CB;
        public static final int GL_TEXTURE12                      = 0x84CC;
        public static final int GL_TEXTURE13                      = 0x84CD;
        public static final int GL_TEXTURE14                      = 0x84CE;
        public static final int GL_TEXTURE15                      = 0x84CF;
        public static final int GL_TEXTURE16                      = 0x84D0;
        public static final int GL_TEXTURE17                      = 0x84D1;
        public static final int GL_TEXTURE18                      = 0x84D2;
        public static final int GL_TEXTURE19                      = 0x84D3;
        public static final int GL_TEXTURE20                      = 0x84D4;
        public static final int GL_TEXTURE2                       = 0x84C2;
        public static final int GL_TEXTURE21                      = 0x84D5;
        public static final int GL_TEXTURE22                      = 0x84D6;
        public static final int GL_TEXTURE23                      = 0x84D7;
        public static final int GL_TEXTURE24                      = 0x84D8;
        public static final int GL_TEXTURE25                      = 0x84D9;
        public static final int GL_TEXTURE26                      = 0x84DA;
        public static final int GL_TEXTURE27                      = 0x84DB;
        public static final int GL_TEXTURE28                      = 0x84DC;
        public static final int GL_TEXTURE29                      = 0x84DD;
        public static final int GL_TEXTURE_2D                     = 0x0DE1;
        public static final int GL_TEXTURE30                      = 0x84DE;
        public static final int GL_TEXTURE3                       = 0x84C3;
        public static final int GL_TEXTURE31                      = 0x84DF;
        public static final int GL_TEXTURE4                       = 0x84C4;
        public static final int GL_TEXTURE5                       = 0x84C5;
        public static final int GL_TEXTURE6                       = 0x84C6;
        public static final int GL_TEXTURE7                       = 0x84C7;
        public static final int GL_TEXTURE8                       = 0x84C8;
        public static final int GL_TEXTURE9                       = 0x84C9;
        public static final int GL_TEXTURE_BINDING_2D             = 0x8069;
        public static final int GL_TEXTURE_BINDING_CUBE_MAP       = 0x8514;
        public static final int GL_TEXTURE_CUBE_MAP               = 0x8513;
        public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X    = 0x8516;
        public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y    = 0x8518;
        public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z    = 0x851A;
        public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X    = 0x8515;
        public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y    = 0x8517;
        public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z    = 0x8519;
        public static final int GL_TEXTURE_MAG_FILTER             = 0x2800;
        public static final int GL_TEXTURE_MIN_FILTER             = 0x2801;
        public static final int GL_TEXTURE_WRAP_S                 = 0x2802;
        public static final int GL_TEXTURE_WRAP_T                 = 0x2803;
        public static final int GL_TRIANGLE_FAN                   = 0x0006;
        public static final int GL_TRIANGLES                      = 0x0004;
        public static final int GL_TRIANGLE_STRIP                 = 0x0005;
        public static final int GL_UNPACK_ALIGNMENT               = 0x0CF5;
        public static final int GL_UNSIGNED_BYTE                  = 0x1401;
        public static final int GL_UNSIGNED_INT                   = 0x1405;
        public static final int GL_UNSIGNED_SHORT                 = 0x1403;
        public static final int GL_UNSIGNED_SHORT_4_4_4_4         = 0x8033;
        public static final int GL_UNSIGNED_SHORT_5_5_5_1         = 0x8034;
        public static final int GL_UNSIGNED_SHORT_5_6_5           = 0x8363;
        public static final int GL_VALIDATE_STATUS                  = 0x8B83;
        public static final int GL_VENDOR                         = 0x1F00;
        public static final int GL_VERSION                        = 0x1F02;
        public static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;
        public static final int GL_VERTEX_ATTRIB_ARRAY_ENABLED        = 0x8622;
        public static final int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED     = 0x886A;
        public static final int GL_VERTEX_ATTRIB_ARRAY_POINTER        = 0x8645;
        public static final int GL_VERTEX_ATTRIB_ARRAY_SIZE           = 0x8623;
        public static final int GL_VERTEX_ATTRIB_ARRAY_STRIDE         = 0x8624;
        public static final int GL_VERTEX_ATTRIB_ARRAY_TYPE           = 0x8625;
        public static final int GL_VERTEX_SHADER                    = 0x8B31;
        public static final int GL_VIEWPORT                       = 0x0BA2;
        public static final int GL_ZERO                           = 0;
    }

}
