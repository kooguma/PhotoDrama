
#ifndef PHOTODRAMA4ANDROID_LPRPLATFORMANDROID_H
#define PHOTODRAMA4ANDROID_LPRPLATFORMANDROID_H

#include <cstdio>
#include <android/log.h>
#include <jni.h>
#include <cstdlib>

#ifndef LPR_LOG_TAG
#define LPR_LOG_TAG "Loopeer"
#endif

#if defined(DEBUG) || defined(_DEBUG) || defined(_CGE_LOGS_)
#define  LPR_LOG_INFO(...) __android_log_print(4, LPR_LOG_TAG, __VA_ARGS__)
#define  LPR_LOG_ERROR(...) __android_log_print(6, LPR_LOG_TAG, __VA_ARGS__)
#define  LPR_LOG_ERROR_V(...) __android_log_vprint(6, LPR_LOG_TAG, __VA_ARGS__)
#else
#define  LPR_LOG_INFO(...)
#define  LPR_LOG_ERROR(...)
#endif

#endif //PHOTODRAMA4ANDROID_LPRPLATFORMANDROID_H
