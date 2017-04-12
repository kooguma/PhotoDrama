
#ifndef PHOTODRAMA4ANDROID_LPRAMIX_H
#define PHOTODRAMA4ANDROID_LPRAMIX_H

#include "lprFFmpegHeaders.h"
#include "lprCommonDefine.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_com_loopeer_media_VideoMixer_nativeMix(JNIEnv *env, jobject instance, jstring input1_,
                                            jstring input2_, jstring output_);

#ifdef __cplusplus
}
#endif
#endif //PHOTODRAMA4ANDROID_LPRAMIX_H
