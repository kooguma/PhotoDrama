/*
* cgeImageFilterWrapper.cpp
*
*  Created on: 2016-3-19
*      Author: Wang Yang
*        Mail: admin@wysaid.org
*/

#include "cgeImageFilterWrapper.h"

#include "cgeUtilFunctions.h"

using namespace CGE;

extern "C"
{

	JNIEXPORT jlong JNICALL Java_org_wysaid_nativePort_CGEImageFilter_createEnlarge2EyesFilter(JNIEnv *, jclass)
	{
		return 0;
	}

	JNIEXPORT jlong JNICALL Java_org_wysaid_nativePort_CGEImageFilter_createEnlarge2EyesAndMouthFilter(JNIEnv *, jclass)
	{
		return 0;
	}

	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_setEnlarge2EyesFilterArgs(JNIEnv *, jclass, jlong addr, jfloat leftEyeRadius, jfloat rightEyeRadius, jfloat leftEyePosX, jfloat leftEyePosY, jfloat rightEyePosX, jfloat rightEyePosY)
	{
	}

	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_setEnlarge2EyesFilterIntensity(JNIEnv *, jclass, jlong addr, jfloat intensity)
	{
	}

	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_setEnlarge2EyesAndMouthFilterArgs(JNIEnv *, jclass, jlong addr, jfloat mouthRadius, jfloat mouthPosX, jfloat mouthPosY)
	{
	}

	JNIEXPORT jlong JNICALL Java_org_wysaid_nativePort_CGEImageFilter_createNativeFilterByConfig
	  (JNIEnv *env, jclass, jstring config, jfloat intensity)
	{
		if(config == nullptr)
			return 0;

		CGEImageFilterInterfaceAbstract* f = nullptr;

		const char* configStr = env->GetStringUTFChars(config, 0);

		if(configStr == nullptr || *configStr == '\0')
		{
			CGE_LOG_INFO("Using empty filter config.");
		}
		else
		{
		}

		env->ReleaseStringUTFChars(config, configStr);

		return (jlong)f;
	}

	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_releaseNativeFilter
	  (JNIEnv *, jclass, jlong filter)
	{
		auto* f = (CGEImageFilterInterfaceAbstract*)filter;
		delete f;
	}


	JNIEXPORT jlong JNICALL Java_org_wysaid_nativePort_CGEImageFilter_createAlienLookFilter(JNIEnv *, jclass)
	{
		return 0;
	}

	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_setAlienLookFilterArgs(JNIEnv *, jclass, jlong addr, jfloat width, jfloat height, jfloat intensity)
	{

	}


	JNIEXPORT void JNICALL Java_org_wysaid_nativePort_CGEImageFilter_setAlienLookFilterPosition(JNIEnv *, jclass, jlong addr, jfloat leftEyePosX, jfloat leftEyePosY, jfloat rightEyePosX, jfloat rightEyePosY, jfloat mouthPosX, jfloat mouthPosY)
	{

	}

}





