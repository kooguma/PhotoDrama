#include "lprFFmpegNativeLibrary.h"
#include "lprFFmpegHeaders.h"
#include "lprCommonDefine.h"

extern "C" {

JNIEXPORT void JNICALL Java_com_loopeer_media_FFmpegNativeLibrary_avRegisterAll(JNIEnv *, jclass) {
    av_register_all();
    avfilter_register_all();
    avcodec_register_all();
    av_log_set_callback(custom_log);
}

void custom_log(void *ptr, int level, const char *fmt, va_list vl) {
    LPR_LOG_ERROR_V(fmt, vl);
}

}