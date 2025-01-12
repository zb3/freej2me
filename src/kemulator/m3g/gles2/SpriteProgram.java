package kemulator.m3g.gles2;

public class SpriteProgram extends ShaderProgram implements ICommonShader {
    public int aUV;
    public int aVertexID;
    public int uProjectionMatrix;
    public int uModelViewMatrix;
    public int uScaleX;
    public int uScaleY;
    public int uTexture;
    public int uAlphaFactor;

    public int uDepthRange;

    public int uFogType;
    public int uFogStartOrDensity;
    public int uFogEnd;
    public int uFogColor;

    public int uMinAlpha;

    public SpriteProgram() {
        super("sprite");
    }

    public void onLoad() {
        aUV = a("a_uv");
        aVertexID = a("a_vertexID");
        uProjectionMatrix = u("projectionMatrix");
        uModelViewMatrix = u("modelViewMatrix");
        uScaleX = u("scaleX");
        uScaleY = u("scaleY");
        uTexture = u("texture");
        uAlphaFactor = u("alphaFactor");
        uDepthRange = u("depthRange");
        uFogType = u("fogType");
        uFogStartOrDensity = u("fogStartOrDensity");
        uFogEnd = u("fogEnd");
        uFogColor = u("fogColor");
        uMinAlpha = u("minAlpha");
    }

    public int uProjectionMatrix() {
        return uProjectionMatrix;
    }

    public int uModelViewMatrix() {
        return uModelViewMatrix;
    }

    public int uDepthRange() {
        return uDepthRange;
    }

    public int uFogType() {
        return uFogType;
    }

    public int uFogStartOrDensity() {
        return uFogStartOrDensity;
    }

    public int uFogEnd() {
        return uFogEnd;
    }

    public int uFogColor() {
        return uFogColor;
    }

    public int uMinAlpha() {
        return uMinAlpha;
    }

}
