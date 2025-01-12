package kemulator.m3g.gles2;

public class MeshProgram extends ShaderProgram implements ICommonShader {
    public static final int MAX_TEXTURES = 2;
    public static final int MAX_LIGHTS = 8;

    public int aVPos;
    public int aVPosLight;
    public int aVPosSb;
    public int aVColor;
    public int aVNormal;
    public int[] aVCoords;
    public int[] uVCoordsMat;
    public int uProjectionMatrix;
    public int uModelViewMatrix;
    public int uTrackVertexColors;
    public int uUseLighting;
    public int uMAmbient;
    public int uMDiffuse;
    public int uMSpecular;
    public int uMEmissive;
    public int uMShininess;
    public int uUsedLights;
    public int[] uLAmbient;
    public int[] uLDiffuse;
    public int[] uLSpecular;
    public int[] uLPosition;
    public int[] uLMatrix;
    public int[] uLDirection;
    public int[] uLShininess;
    public int[] uLSpotCutoffCos;
    public int[] uLConstAtt;
    public int[] uLLinAtt;
    public int[] uLQuadAtt;
    public int uIsLocalViewer;
    public int uIsTwoSided;
    public int uDepthRange;
    public int uIsFlatShaded;
    public int uUsedTextures;
    public int[] uTexture;

    public int uFogType;
    public int uFogStartOrDensity;
    public int uFogEnd;
    public int uFogColor;
    public int uMinAlpha;

    public int[] uBlendColor;
    public int[] uBlendMode;
    public int[] uHasColor;
    public int[] uHasAlpha;

    public MeshProgram() {
        super("mesh");
    }

    public void onLoad() {
        aVPos = a("vPos");
        aVPosLight = a("vPosLight");
        aVPosSb = a("vPosSb");
        aVColor = a("vColor");
        aVNormal = a("vNormal");
        aVCoords = new int[MAX_TEXTURES];
        uVCoordsMat = new int[MAX_TEXTURES];
        uTexture = new int[MAX_TEXTURES];
        uBlendColor = new int[MAX_TEXTURES];
        uBlendMode = new int[MAX_TEXTURES];
        uHasColor = new int[MAX_TEXTURES];
        uHasAlpha = new int[MAX_TEXTURES];

        for (int i = 0; i < MAX_TEXTURES; i++) {
            aVCoords[i] = a("vCoords" + i);
            uVCoordsMat[i] = u("vCoordsMat", i);
            uTexture[i] = u("texture", i);
            uBlendColor[i] = u("blendColor", i);
            uBlendMode[i] = u("blendMode", i);
            uHasColor[i] = u("hasColor", i);
            uHasAlpha[i] = u("hasAlpha", i);
        }
        uProjectionMatrix = u("projectionMatrix");
        uModelViewMatrix = u("modelViewMatrix");
        uTrackVertexColors = u("trackVertexColors");
        uUseLighting = u("useLighting");
        uMAmbient = u("mAmbient");
        uMDiffuse = u("mDiffuse");
        uMSpecular = u("mSpecular");
        uMEmissive = u("mEmissive");
        uMShininess = u("mShininess");
        uUsedLights = u("usedLights");
        uLAmbient = new int[MAX_LIGHTS];
        uLDiffuse = new int[MAX_LIGHTS];
        uLSpecular = new int[MAX_LIGHTS];
        uLPosition = new int[MAX_LIGHTS];
        uLMatrix = new int[MAX_LIGHTS];
        uLDirection = new int[MAX_LIGHTS];
        uLShininess = new int[MAX_LIGHTS];
        uLSpotCutoffCos = new int[MAX_LIGHTS];
        uLConstAtt = new int[MAX_LIGHTS];
        uLLinAtt = new int[MAX_LIGHTS];
        uLQuadAtt = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            uLAmbient[i] = u("lAmbient", i);
            uLDiffuse[i] = u("lDiffuse", i);
            uLSpecular[i] = u("lSpecular", i);
            uLPosition[i] = u("lPosition", i);
            uLMatrix[i] = u("lMatrix", i);
            uLDirection[i] = u("lDirection", i);
            uLShininess[i] = u("lShininess", i);
            uLSpotCutoffCos[i] = u("lSpotCutoffCos", i);
            uLConstAtt[i] = u("lConstAtt", i);
            uLLinAtt[i] = u("lLinAtt", i);
            uLQuadAtt[i] = u("lQuadAtt", i);
        }

        uIsLocalViewer = u("isLocalViewer");
        uIsTwoSided = u("isTwoSided");
        uDepthRange = u("depthRange");
        uIsFlatShaded = u("isFlatShaded");
        uUsedTextures = u("usedTextures");

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
