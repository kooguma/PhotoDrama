/*
* cgeFFmpegNativeLibrary.cpp
*
*  Created on: 2015-11-25
*      Author: Wang Yang
*        Mail: admin@wysaid.org
*/

#include "cgeFFmpegHeaders.h"
#include "cgeFFmpegNativeLibrary.h"
#include "cgeCommonDefine.h"


using namespace CGE;

extern "C"
{
////////// ffmpeg ////////////////////

    JNIEXPORT void JNICALL Java_org_wysaid_nativePort_FFmpegNativeLibrary_avRegisterAll(JNIEnv *,
                                                                                       jclass) {
    CGE_LOG_INFO("registerFFmpeg...");
        av_register_all();
        avcodec_register_all();
    av_log_set_callback(custom_log);
    }

    void custom_log(void *ptr, int level, const char *fmt, va_list vl) {
        CGE_LOG_ERROR_V(fmt, vl);
    }
}








