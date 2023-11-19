#include <stdio.h>
#include <jni.h>
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libswresample/swresample.h>

#include <unistd.h>
#include <fcntl.h>

#define logmsg(...) do {fprintf(stderr, "ffaudio: ");fprintf(stderr, __VA_ARGS__);fwrite("\n", 1, 1, stderr);fflush(stdout);} while (0)

// well this should be big enough for probing
#define AVIO_CTX_BUFSIZE 16384

static JavaVM *g_vm;
static jmethodID readMethodID;

typedef struct ff_state
{
    AVFormatContext *format_ctx;
    AVCodecContext *codec_ctx;
    AVPacket *packet;
    AVStream *audio_stream;
    AVIOContext *avio_ctx;
    enum AVSampleFormat targetSampleFormat;
    AVChannelLayout ch_layout;
    struct SwrContext *swr_ctx;
    AVFrame *frame;
    int has_packet;

    jobject inputStreamRef;

    unsigned char *buf;
    int buf_size;
    int buf_capacity;
    int buf_read_idx;
} ff_state;

inline int is_big_endian()
{
    int i=1;
    return ! *((char *)&i);
}

int read_callback(void *opaque, uint8_t *buf, int buf_size);
static JNIEnv *get_jni_env(jint *attachStatus);
static void release_jni_env(jint attachStatus);
void free_state(ff_state *state);

/*
 * Class:     pl_zb3_freej2me_audio_FFAudioInputStream
 * Method:    load
 * Signature: (Ljava/io/InputStream;)Lpl/zb3/freej2me/audio/FFAudioInputStream;
 */
JNIEXPORT jobject JNICALL Java_pl_zb3_freej2me_audio_FFAudioInputStream_load
  (JNIEnv *env, jclass ffAudioStream, jobject inputStream, jstring contentType)
{
    (*env)->GetJavaVM(env, &g_vm);

    ff_state *ret = NULL;

    char *content_type = NULL;

    if (contentType) {
        content_type = (*env)->GetStringUTFChars(env, contentType, 0);
    }

    ret = malloc(sizeof(ff_state));
    memset(ret, 0, sizeof(ff_state));

    av_log_set_level(AV_LOG_ERROR);
    av_log_set_flags(AV_LOG_PRINT_LEVEL);

    unsigned char *avio_ctx_buffer = (unsigned char *)av_malloc(AVIO_CTX_BUFSIZE);
    ret->inputStreamRef =  (*env)->NewGlobalRef(env, inputStream);
    
    ret->avio_ctx = avio_alloc_context(avio_ctx_buffer, AVIO_CTX_BUFSIZE, 0, ret->inputStreamRef, read_callback, NULL, NULL);

    jclass inputStreamClass = (*env)->GetObjectClass(env, inputStream);
    readMethodID = (*env)->GetMethodID(env, inputStreamClass, "read", "([B)I");

    ret->format_ctx = avformat_alloc_context();
    
    ret->format_ctx->probesize = ret->format_ctx->format_probesize = AVIO_CTX_BUFSIZE;
    ret->format_ctx->pb = ret->avio_ctx;

    ret->format_ctx->flags = AVFMT_FLAG_CUSTOM_IO; 
    
    AVInputFormat *input_format = content_type ? av_find_input_format(content_type) : NULL;
    
    int err;

    if ((err = avformat_open_input(&ret->format_ctx, NULL, input_format, NULL)) < 0) {
        uint8_t errstr[1024];
        av_strerror(err, errstr, sizeof(errstr));
        logmsg("did not open input: %s", errstr);
        goto state_err;
    }
   
    if (avformat_find_stream_info(ret->format_ctx, NULL) < 0) {
        goto state_err;
    }

    for (unsigned int i = 0; i < ret->format_ctx->nb_streams; i++) {
        if (ret->format_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            ret->audio_stream = ret->format_ctx->streams[i];
            break;
        }
    }
   
    if (!ret->audio_stream) {
        goto state_err;
    }

    AVCodec *codec = avcodec_find_decoder(ret->audio_stream->codecpar->codec_id);
    
    if (!codec) {
        goto state_err;
    }

    ret->codec_ctx = avcodec_alloc_context3(codec);
    if (!ret->codec_ctx) {
        goto state_err;
    }

    if (avcodec_parameters_to_context(ret->codec_ctx, ret->audio_stream->codecpar) < 0) {
        goto state_err;
    }

    if (avcodec_open2(ret->codec_ctx, codec, NULL) < 0) {
        goto state_err;
    }

    ret->targetSampleFormat = AV_SAMPLE_FMT_S16; // Use signed 16-bit PCM

    av_channel_layout_default(&ret->ch_layout, ret->codec_ctx->ch_layout.nb_channels);

    if (swr_alloc_set_opts2(&ret->swr_ctx,
        &ret->ch_layout,
        AV_SAMPLE_FMT_S16,
        ret->codec_ctx->sample_rate,
        &ret->codec_ctx->ch_layout,
        ret->codec_ctx->sample_fmt,
        ret->codec_ctx->sample_rate,
        0,
        NULL) < 0) {
        goto state_err;
    }

    if (swr_init(ret->swr_ctx) < 0) {
        goto state_err;
    }

    ret->packet = av_packet_alloc();
    ret->frame = av_frame_alloc();

    jclass audioFormatClass = (*env)->FindClass(env, "javax/sound/sampled/AudioFormat");
    jmethodID constructor = (*env)->GetMethodID(env, audioFormatClass, "<init>", "(FIIZZ)V");
    jobject audioFormat = (*env)->NewObject(env, audioFormatClass, constructor,
        (jfloat)ret->codec_ctx->sample_rate,
        16, // pcm signed 16 bit
        ret->codec_ctx->ch_layout.nb_channels,
        1,
        is_big_endian() // always the host order
    );

    jclass internalStreamClass = (*env)->FindClass(env, "pl/zb3/freej2me/audio/FFAudioInternalStream");
    constructor = (*env)->GetMethodID(env, internalStreamClass, "<init>", "(J)V");
    jobject internalStream = (*env)->NewObject(env, internalStreamClass, constructor, ret);


    // construct the stream with framelength not specified
    constructor = (*env)->GetMethodID(env, ffAudioStream, "<init>", "(Ljava/io/InputStream;Ljavax/sound/sampled/AudioFormat;J)V");

    
    jobject retObject = (*env)->NewObject(env, ffAudioStream, constructor, internalStream, audioFormat, -1);

    /*
    constructor = (*env)->GetStaticMethodID(env, ffAudioStream, "doInit", "(Ljava/io/InputStream;Ljavax/sound/sampled/AudioFormat;J)Lpl/zb3/freej2me/audio/FFAudioInputStream;");

    jobject retObject = (*env)->CallStaticObjectMethod(env, ffAudioStream, constructor, internalStream, audioFormat, -1);
    */

    return retObject;



state_err:
   free_state(ret);
   free(ret); // only this and the close function can free this directly
              // when the stream is open, close will be responsible for this
   
   return NULL;


}

int read_next_frame(ff_state *state) {
    // here we should only fill the buffer fully as we don't fill the java buffer here

    AVFrame *frame = state->frame;


    if (!state->has_packet) {
        try_fetch_new_packet:

        while (av_read_frame(state->format_ctx, state->packet) >= 0) {
            if (state->packet->stream_index == state->audio_stream->index) {
                if (avcodec_send_packet(state->codec_ctx, state->packet) == 0) {

                    state->has_packet = 1;
                    break;
                }                
            }

            // if we're here it means we won't use the packet
            av_packet_unref(state->packet);
        }

        if (!state->has_packet) {
            // eof
            return 0;
        }
    }

    if (avcodec_receive_frame(state->codec_ctx, frame) == 0) {

        int needed_size = av_samples_get_buffer_size(NULL, state->codec_ctx->ch_layout.nb_channels, frame->nb_samples, state->targetSampleFormat, 0);

        if (needed_size > state->buf_capacity) {
            if (state->buf) {
                av_freep(&state->buf);
            }

            state->buf = av_malloc(needed_size);
            state->buf_capacity = needed_size;
        }

        int samples = swr_convert(state->swr_ctx, &state->buf, frame->nb_samples,
            (const uint8_t**)frame->extended_data, frame->nb_samples);
  
        // btw this is because we don't change sample rate 
        if (samples < 0) {
            return -1;
        }

        state->buf_size = 2 * samples * state->codec_ctx->ch_layout.nb_channels; // hmm we want no alignment

        return samples;
    } else {
        av_packet_unref(state->packet);
        state->has_packet = 0;

        goto try_fetch_new_packet;
    }

    return 1;
}


void free_state(ff_state *state) {
    logmsg("inside free_state");

    if (state->frame) {
        av_frame_free(&state->frame);
    }

    if (state->has_packet) {
        av_packet_unref(state->packet);
    }

    if (state->packet) {
        av_packet_free(&state->packet);
    }

    if (state->swr_ctx) {
        swr_free(&state->swr_ctx);
    }

    if (state->format_ctx) {
        avformat_close_input(&state->format_ctx);
    }

    if (state->avio_ctx) {
        /* note: the internal buffer could have changed, and be != avio_ctx_buffer */
        av_freep(&state->avio_ctx->buffer);
        av_opt_free(state->avio_ctx);
        avio_context_free(&state->avio_ctx);
    }

    // the state object itself will be freed on close or on init error
}

int read_callback(void *opaque, uint8_t *buf, int buf_size)
{
    int attachStatus = 0;
    JNIEnv *env = get_jni_env(&attachStatus);

    jobject inputStream = (jobject)opaque;

    jbyteArray byteArray = (*env)->NewByteArray(env, buf_size);

    jint bytesRead = (*env)->CallIntMethod(env, inputStream, readMethodID, byteArray);
    if (bytesRead == -1)
    {
        bytesRead = AVERROR_EOF;
        goto ret;
    }

    (*env)->GetByteArrayRegion(env, byteArray, 0, bytesRead, (jbyte *)buf);

ret:
    (*env)->DeleteLocalRef(env, byteArray);

    release_jni_env(attachStatus);

    return bytesRead;
}

JNIEXPORT jint JNICALL Java_pl_zb3_freej2me_audio_FFAudioInternalStream_read___3BII
(JNIEnv *env, jobject obj, jbyteArray b, jint off, jint len)
{
    if (b == NULL) {
        jclass exClass = (*env)->FindClass(env, "java/lang/NullPointerException");
        (*env)->ThrowNew(env, exClass, "byte array b is null");
        return 0;
    }
    if (off < 0 || len < 0 || off + len > (*env)->GetArrayLength(env, b)) {
        jclass exClass = (*env)->FindClass(env, "java/lang/IndexOutOfBoundsException");
        (*env)->ThrowNew(env, exClass, "Offset or length out of bounds");
        return 0;
    }

    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, "stateHandle", "J");
    jlong stateHandle = (*env)->GetLongField(env, obj, fid);
    ff_state *state = (ff_state *)(intptr_t)stateHandle;

    jint bytesRead = 0;

    while (bytesRead < len) {
        if (state->buf_read_idx >= state->buf_size) {
            int result = read_next_frame(state);
            if (result <= 0) {
                if (result == 0 && bytesRead > 0) {
                    break;
                }
                return -1;
            }
            state->buf_read_idx = 0;
        }

        int bytesToRead = (state->buf_size - state->buf_read_idx) < (len - bytesRead) ?
                          (state->buf_size - state->buf_read_idx) : (len - bytesRead);

        (*env)->SetByteArrayRegion(env, b, off + bytesRead, bytesToRead, (jbyte *)(state->buf + state->buf_read_idx));
        state->buf_read_idx += bytesToRead;
        bytesRead += bytesToRead;
    }

    return bytesRead;
}

JNIEXPORT void JNICALL Java_pl_zb3_freej2me_audio_FFAudioInternalStream_close
(JNIEnv *env, jobject obj)
{
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, "stateHandle", "J");
    jlong stateHandle = (*env)->GetLongField(env, obj, fid);
    ff_state *state = (ff_state *)stateHandle;

    jclass inputStreamClass = (*env)->FindClass(env, "java/io/InputStream");
    jmethodID closeMethod = (*env)->GetMethodID(env, inputStreamClass, "close", "()V");
    
    (*env)->CallVoidMethod(env, state->inputStreamRef, closeMethod);

    (*env)->DeleteGlobalRef(env, state->inputStreamRef);

    free_state(state);
    free(state);

    (*env)->SetLongField(env, obj, fid, 0);
}

// these functions are here so that avio callbacks could be called from a different thread

static JNIEnv *get_jni_env(jint *attachStatus)
{
    JNIEnv *env;
    *attachStatus = (*g_vm)->GetEnv(g_vm, (void **)&env, JNI_VERSION_1_6);

    if (*attachStatus == JNI_EDETACHED)
    {
        if ((*g_vm)->AttachCurrentThread(g_vm, (void **)&env, NULL) != 0)
        {
            return NULL;
        }
    }
    else if (*attachStatus == JNI_EVERSION)
    {
        return NULL;
    }

    return env;
}

static void release_jni_env(jint attachStatus)
{
    if (attachStatus == JNI_EDETACHED)
    {
        (*g_vm)->DetachCurrentThread(g_vm);
    }
}
