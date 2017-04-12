
#ifndef PHOTODRAMA4ANDROID_LPRFFMPEGNATIVELIBRARY_H
#define PHOTODRAMA4ANDROID_LPRFFMPEGNATIVELIBRARY_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_loopeer_media_FFmpegNativeLibrary_avRegisterAll(JNIEnv *, jclass);

void custom_log(void *ptr, int level, const char *fmt, va_list vl);

#ifdef __cplusplus
}
#endif

#endif //PHOTODRAMA4ANDROID_LPRFFMPEGNATIVELIBRARY_H
