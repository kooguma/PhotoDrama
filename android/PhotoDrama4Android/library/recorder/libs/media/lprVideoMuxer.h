
#ifndef PHOTODRAMA4ANDROID_LPRVIDEOMUXER_H
#define PHOTODRAMA4ANDROID_LPRVIDEOMUXER_H

#include "lprCommonDefine.h"
#include "lprFFmpegHeaders.h"

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT void JNICALL
Java_com_loopeer_media_VideoMuxer_nativeConvert(JNIEnv *env, jobject instance, jstring inputPath_,
                                               jstring outputPath_);

#ifdef __cplusplus
}
#endif

#endif //PHOTODRAMA4ANDROID_LPRVIDEOMUXER_H
