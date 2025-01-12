package kemulator.m3g.gles2;

public interface ICommonShader {
    public int uProjectionMatrix();
    public int uModelViewMatrix();
    public int uDepthRange();
    public int uFogType();
    public int uFogStartOrDensity();
    public int uFogEnd();
    public int uFogColor() ;
    public int uMinAlpha();

}
