LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := dalvikstager
LOCAL_SRC_FILES := dalvikstager.c
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
