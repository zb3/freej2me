#include "texture.h"
#include <GLES2/gl2.h>


JNIEXPORT jboolean JNICALL Java_ru_woesss_j2me_micro3d_TextureImpl__1glIsTexture
        (JNIEnv *env, jclass, jint id) {
    return glIsTexture(id);
}

JNIEXPORT jint JNICALL Java_ru_woesss_j2me_micro3d_TextureImpl__1glGenTextureId
        (JNIEnv *env, jclass, jint biggerThan) {
    int id = -1;
    do {
      glGenTextures(1, &id);
    } while(id <= biggerThan);

    return id;
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_TextureImpl__1loadToGl
        (JNIEnv *env, jclass, jint textId, jint width, jint height, jboolean filter, jobject dataBuff) {
    void *data = (*env)->GetDirectBufferAddress(env, dataBuff);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textId);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    glBindTexture(GL_TEXTURE_2D, 0);
}
