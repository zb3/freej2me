#include "program.h"
#include <GLES2/gl2.h>


JNIEXPORT jint JNICALL Java_ru_woesss_j2me_micro3d_Program__1loadShader
        (JNIEnv *env, jclass, jboolean fragment, jstring sourceStr) {
    const char *source = (*env)->GetStringUTFChars(env, sourceStr, NULL);
    int shader = glCreateShader(fragment ? GL_FRAGMENT_SHADER : GL_VERTEX_SHADER);
    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);
    int status;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
    
    if (status == 0) {
        char info[4096];
        int len;
    	glGetShaderInfoLog(shader, 4095, &len, info);
    	info[len] = 0;
    	fprintf(stderr, "M3D: compileShader: %s", info);
    	fflush(stderr);
    }

    (*env)->ReleaseStringUTFChars(env, sourceStr, source);

    return shader;
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glUseProgram
        (JNIEnv *env, jclass, jint program) {
    glUseProgram(program);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glDeleteProgram
        (JNIEnv *env, jclass, jint program) {
    glDeleteProgram(program);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glReleaseShaderCompiler
        (JNIEnv *env, jclass) {
    glReleaseShaderCompiler();
}

JNIEXPORT jint JNICALL Java_ru_woesss_j2me_micro3d_Program__1glGetShaderLocation
        (JNIEnv *env, jclass, jint pid, jboolean uniform, jstring nameStr) {
    const char *name = (*env)->GetStringUTFChars(env, nameStr, NULL);
	int ret = uniform ? glGetUniformLocation(pid, name) : glGetAttribLocation(pid, name);	  
    (*env)->ReleaseStringUTFChars(env, nameStr, name);
    return ret;
}

JNIEXPORT jint JNICALL Java_ru_woesss_j2me_micro3d_Program__1doCreateProgram
        (JNIEnv *env, jclass, jint vertexId, jint fragmentId) {
    int program = glCreateProgram();             // create empty OpenGL Program
    glAttachShader(program, vertexId);   // add the vertex shader to program
    glAttachShader(program, fragmentId); // add the fragment shader to program
    
    glLinkProgram(program);                  // create OpenGL program executables
    int status;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0) {
        char info[4096];
        int len;
        glGetProgramInfoLog(program, 4095, &len, info);
        info[len] = 0;
        fprintf(stderr, "M3D: createProgram: %s", info);
        fflush(stderr);
    }
    glDeleteShader(vertexId);
    glDeleteShader(fragmentId);
    return program;
}


JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glUniform1f
    (JNIEnv *env, jclass, jint id, jfloat a) {
    glUniform1f(id, a);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glUniform1i
    (JNIEnv *env, jclass, jint id, jint a) {
    glUniform1i(id, a);
}
    
JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glUniform2f
    (JNIEnv *env, jclass, jint id, jfloat a, jfloat b) {
    glUniform2f(id, a, b);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glUniform3f
    (JNIEnv *env, jclass, jint id, jfloat a, jfloat b, jfloat c) {
    glUniform3f(id, a, b, c);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1glVertexAttrib3f
    (JNIEnv *env, jclass, jint id, jfloat a, jfloat b, jfloat c) {
    glVertexAttrib3f(id, a, b, c);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1bindMatrices(JNIEnv* env, jobject obj, jint uMatrix, jint uNormalMatrix, jfloatArray mvp, jfloatArray mv) {
    jfloat* mvpArray = (*env)->GetFloatArrayElements(env, mvp, NULL);
    jfloat* mvArray = (*env)->GetFloatArrayElements(env, mv, NULL);

    glUniformMatrix4fv(uMatrix, 1, GL_FALSE, mvpArray);
    glUniformMatrix3fv(uNormalMatrix, 1, GL_FALSE, mvArray);

    // Release the Java float arrays
    (*env)->ReleaseFloatArrayElements(env, mvp, mvpArray, JNI_ABORT);
    (*env)->ReleaseFloatArrayElements(env, mv, mvArray, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1bindTexture2D(JNIEnv* env, jobject obj, jint no, jint tid) {
    glActiveTexture(GL_TEXTURE0 + no);
    glBindTexture(GL_TEXTURE_2D, tid);
}

JNIEXPORT void JNICALL Java_ru_woesss_j2me_micro3d_Program__1unbindTexture2D(JNIEnv* env, jobject obj) {
    glBindTexture(GL_TEXTURE_2D, 0);
}
