#include <jni.h>
#include <string>
#include <cstring>
#include <android/log.h>
#include "include/Vxc.h"
#include "include/VxcRequests.h"
#include "include/VxcResponses.h"
#include "include/VxcEvents.h"

#define LOG_TAG "VivoxBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static char g_connector_handle[256] = {0};
static char g_account_handle[256] = {0};
static char g_session_handle[256] = {0};
static char g_sessiongroup_handle[256] = {0};
static bool g_initialized = false;

static JNIEnv* g_env = nullptr;
static JavaVM* g_vm = nullptr;
static jobject g_callback = nullptr;
static jmethodID g_onEvent = nullptr;

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

static void sendEvent(const char* type, const char* json) {
    if (!g_vm || !g_callback) return;
    JNIEnv* env;
    bool attached = false;
    int status = g_vm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (status == JNI_EDETACHED) {
        g_vm->AttachCurrentThread(&env, nullptr);
        attached = true;
    }
    if (env && g_callback && g_onEvent) {
        jstring jtype = env->NewStringUTF(type);
        jstring jjson = env->NewStringUTF(json);
        env->CallVoidMethod(g_callback, g_onEvent, jtype, jjson);
        env->DeleteLocalRef(jtype);
        env->DeleteLocalRef(jjson);
    }
    if (attached) {
        g_vm->DetachCurrentThread();
    }
}

static void processMessages() {
    vx_message_base_t* msg = nullptr;
    while (vx_get_message(&msg) == 0) {
        switch (msg->type) {
            case msg_response: {
                vx_resp_base_t* resp = (vx_resp_base_t*)msg;
                switch (resp->type) {
                    case resp_connector_create: {
                        vx_resp_connector_create_t* r = (vx_resp_connector_create_t*)resp;
                        if (resp->return_code == 0) {
                            strncpy(g_connector_handle, r->connector_handle, sizeof(g_connector_handle) - 1);
                            LOGI("Connector created: %s", g_connector_handle);
                            char json[512];
                            snprintf(json, sizeof(json), "{\"handle\":\"%s\"}", g_connector_handle);
                            sendEvent("connector_created", json);
                        } else {
                            LOGE("Connector create failed: %d", resp->status_code);
                            char json[256];
                            snprintf(json, sizeof(json), "{\"error\":%d}", resp->status_code);
                            sendEvent("error", json);
                        }
                        break;
                    }
                    case resp_account_anonymous_login: {
                        vx_resp_account_anonymous_login_t* r = (vx_resp_account_anonymous_login_t*)resp;
                        if (resp->return_code == 0) {
                            strncpy(g_account_handle, r->account_handle, sizeof(g_account_handle) - 1);
                            LOGI("Logged in: %s", g_account_handle);
                            char json[512];
                            snprintf(json, sizeof(json), "{\"handle\":\"%s\"}", g_account_handle);
                            sendEvent("logged_in", json);
                        } else {
                            LOGE("Login failed: %d", resp->status_code);
                            char json[256];
                            snprintf(json, sizeof(json), "{\"error\":%d}", resp->status_code);
                            sendEvent("login_error", json);
                        }
                        break;
                    }
                    case resp_sessiongroup_add_session: {
                        vx_resp_sessiongroup_add_session_t* r = (vx_resp_sessiongroup_add_session_t*)resp;
                        if (resp->return_code == 0) {
                            LOGI("Joined channel");
                            sendEvent("channel_joined", "{}");
                        } else {
                            LOGE("Join channel failed: %d", resp->status_code);
                            char json[256];
                            snprintf(json, sizeof(json), "{\"error\":%d}", resp->status_code);
                            sendEvent("channel_error", json);
                        }
                        break;
                    }
                    case resp_session_send_message: {
                        if (resp->return_code == 0) {
                            sendEvent("message_sent", "{}");
                        }
                        break;
                    }
                    case resp_account_logout: {
                        LOGI("Logged out");
                        memset(g_account_handle, 0, sizeof(g_account_handle));
                        sendEvent("logged_out", "{}");
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case msg_event: {
                vx_evt_base_t* evt = (vx_evt_base_t*)msg;
                switch (evt->type) {
                    case evt_message: {
                        vx_evt_message_t* e = (vx_evt_message_t*)evt;
                        // Build JSON with escaped strings for safety
                        std::string body = e->message_body ? e->message_body : "";
                        std::string sender = e->participant_uri ? e->participant_uri : "";
                        std::string displayName = e->participant_displayname ? e->participant_displayname : "";
                        std::string lang = e->language ? e->language : "en";
                        // Escape quotes in body
                        std::string escapedBody;
                        for (char c : body) {
                            if (c == '"') escapedBody += "\\\"";
                            else if (c == '\\') escapedBody += "\\\\";
                            else if (c == '\n') escapedBody += "\\n";
                            else escapedBody += c;
                        }
                        std::string json = "{\"body\":\"" + escapedBody +
                            "\",\"sender\":\"" + sender +
                            "\",\"displayName\":\"" + displayName +
                            "\",\"language\":\"" + lang + "\"}";
                        sendEvent("text_message", json.c_str());
                        break;
                    }
                    case evt_participant_added: {
                        vx_evt_participant_added_t* e = (vx_evt_participant_added_t*)evt;
                        std::string uri = e->participant_uri ? e->participant_uri : "";
                        std::string name = e->displayname ? e->displayname : "";
                        std::string json = "{\"uri\":\"" + uri + "\",\"displayName\":\"" + name + "\"}";
                        sendEvent("participant_added", json.c_str());
                        break;
                    }
                    case evt_participant_removed: {
                        vx_evt_participant_removed_t* e = (vx_evt_participant_removed_t*)evt;
                        std::string uri = e->participant_uri ? e->participant_uri : "";
                        std::string json = "{\"uri\":\"" + uri + "\"}";
                        sendEvent("participant_removed", json.c_str());
                        break;
                    }
                    case evt_participant_updated: {
                        vx_evt_participant_updated_t* e = (vx_evt_participant_updated_t*)evt;
                        std::string uri = e->participant_uri ? e->participant_uri : "";
                        char json[512];
                        snprintf(json, sizeof(json),
                            "{\"uri\":\"%s\",\"isSpeaking\":%s,\"isMuted\":%s,\"volume\":%d,\"energy\":%f}",
                            uri.c_str(),
                            e->is_speaking ? "true" : "false",
                            e->is_moderator_muted ? "true" : "false",
                            e->volume,
                            e->energy);
                        sendEvent("participant_updated", json);
                        break;
                    }
                    case evt_media_stream_updated: {
                        vx_evt_media_stream_updated_t* e = (vx_evt_media_stream_updated_t*)evt;
                        char json[256];
                        snprintf(json, sizeof(json), "{\"state\":%d,\"incoming\":%s}",
                            e->state, e->incoming ? "true" : "false");
                        sendEvent("media_updated", json);
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
        vx_destroy_message(msg);
    }
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeInitialize(JNIEnv* env, jobject thiz) {
    if (g_initialized) return JNI_TRUE;

    vx_sdk_config_t config;
    memset(&config, 0, sizeof(config));
    int rc = vx_initialize3(&config, sizeof(config));
    if (rc != 0) {
        LOGE("vx_initialize3 failed: %d", rc);
        return JNI_FALSE;
    }
    g_initialized = true;
    LOGI("Vivox initialized");
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeSetCallback(JNIEnv* env, jobject thiz, jobject callback) {
    if (g_callback) {
        env->DeleteGlobalRef(g_callback);
    }
    g_callback = env->NewGlobalRef(callback);
    jclass cls = env->GetObjectClass(callback);
    g_onEvent = env->GetMethodID(cls, "onVivoxEvent", "(Ljava/lang/String;Ljava/lang/String;)V");
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeConnect(JNIEnv* env, jobject thiz, jstring server) {
    const char* serverStr = env->GetStringUTFChars(server, nullptr);

    vx_req_connector_create_t* req = nullptr;
    vx_req_connector_create_create(&req);
    req->acct_mgmt_server = vx_strdup(serverStr);

    int rc = vx_issue_request(&req->base);
    env->ReleaseStringUTFChars(server, serverStr);

    if (rc != 0) {
        LOGE("connector_create failed: %d", rc);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeLogin(
    JNIEnv* env, jobject thiz, jstring displayName, jstring acctName, jstring accessToken) {

    const char* displayNameStr = env->GetStringUTFChars(displayName, nullptr);
    const char* acctNameStr = env->GetStringUTFChars(acctName, nullptr);
    const char* tokenStr = env->GetStringUTFChars(accessToken, nullptr);

    vx_req_account_anonymous_login_t* req = nullptr;
    vx_req_account_anonymous_login_create(&req);
    req->connector_handle = vx_strdup(g_connector_handle);
    req->displayname = vx_strdup(displayNameStr);
    req->acct_name = vx_strdup(acctNameStr);
    req->access_token = vx_strdup(tokenStr);
    req->enable_buddies_and_presence = 1;
    req->participant_property_frequency = 100;

    int rc = vx_issue_request(&req->base);

    env->ReleaseStringUTFChars(displayName, displayNameStr);
    env->ReleaseStringUTFChars(acctName, acctNameStr);
    env->ReleaseStringUTFChars(accessToken, tokenStr);

    if (rc != 0) {
        LOGE("login failed: %d", rc);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeJoinChannel(
    JNIEnv* env, jobject thiz, jstring channelUri, jstring accessToken, jboolean connectAudio, jboolean connectText) {

    const char* uriStr = env->GetStringUTFChars(channelUri, nullptr);
    const char* tokenStr = env->GetStringUTFChars(accessToken, nullptr);

    vx_req_sessiongroup_add_session_t* req = nullptr;
    vx_req_sessiongroup_add_session_create(&req);
    req->uri = vx_strdup(uriStr);
    req->access_token = vx_strdup(tokenStr);
    req->account_handle = vx_strdup(g_account_handle);
    req->connect_audio = connectAudio ? 1 : 0;
    req->connect_text = connectText ? 1 : 0;

    // Save handles for later use
    if (req->session_handle) {
        strncpy(g_session_handle, req->session_handle, sizeof(g_session_handle) - 1);
    }
    if (req->sessiongroup_handle) {
        strncpy(g_sessiongroup_handle, req->sessiongroup_handle, sizeof(g_sessiongroup_handle) - 1);
    }

    int rc = vx_issue_request(&req->base);

    env->ReleaseStringUTFChars(channelUri, uriStr);
    env->ReleaseStringUTFChars(accessToken, tokenStr);

    if (rc != 0) {
        LOGE("join channel failed: %d", rc);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeLeaveChannel(JNIEnv* env, jobject thiz) {
    if (strlen(g_session_handle) == 0) return JNI_FALSE;

    vx_req_sessiongroup_remove_session_t* req = nullptr;
    vx_req_sessiongroup_remove_session_create(&req);
    req->session_handle = vx_strdup(g_session_handle);
    req->sessiongroup_handle = vx_strdup(g_sessiongroup_handle);

    int rc = vx_issue_request(&req->base);
    if (rc == 0) {
        memset(g_session_handle, 0, sizeof(g_session_handle));
        memset(g_sessiongroup_handle, 0, sizeof(g_sessiongroup_handle));
    }
    return rc == 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeSendMessage(
    JNIEnv* env, jobject thiz, jstring message) {

    if (strlen(g_session_handle) == 0) return JNI_FALSE;

    const char* msgStr = env->GetStringUTFChars(message, nullptr);

    vx_req_session_send_message_t* req = nullptr;
    vx_req_session_send_message_create(&req);
    req->session_handle = vx_strdup(g_session_handle);
    req->message_body = vx_strdup(msgStr);
    req->message_header = vx_strdup("text/plain");

    int rc = vx_issue_request(&req->base);
    env->ReleaseStringUTFChars(message, msgStr);

    return rc == 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeSetMicMute(
    JNIEnv* env, jobject thiz, jboolean muted) {

    if (strlen(g_connector_handle) == 0) return;

    vx_req_connector_mute_local_mic_t* req = nullptr;
    vx_req_connector_mute_local_mic_create(&req);
    req->connector_handle = vx_strdup(g_connector_handle);
    req->mute_level = muted ? 1 : 0;

    vx_issue_request(&req->base);
}

JNIEXPORT void JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativePollEvents(JNIEnv* env, jobject thiz) {
    processMessages();
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeLogout(JNIEnv* env, jobject thiz) {
    if (strlen(g_account_handle) == 0) return JNI_FALSE;

    vx_req_account_logout_t* req = nullptr;
    vx_req_account_logout_create(&req);
    req->account_handle = vx_strdup(g_account_handle);

    int rc = vx_issue_request(&req->base);
    return rc == 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeShutdown(JNIEnv* env, jobject thiz) {
    if (g_initialized) {
        vx_uninitialize();
        g_initialized = false;
        memset(g_connector_handle, 0, sizeof(g_connector_handle));
        memset(g_account_handle, 0, sizeof(g_account_handle));
        memset(g_session_handle, 0, sizeof(g_session_handle));
        memset(g_sessiongroup_handle, 0, sizeof(g_sessiongroup_handle));
        LOGI("Vivox shutdown");
    }
    if (g_callback) {
        JNIEnv* localEnv;
        g_vm->GetEnv((void**)&localEnv, JNI_VERSION_1_6);
        if (localEnv) {
            localEnv->DeleteGlobalRef(g_callback);
        }
        g_callback = nullptr;
        g_onEvent = nullptr;
    }
}

JNIEXPORT jstring JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeGetAccountHandle(JNIEnv* env, jobject thiz) {
    return env->NewStringUTF(g_account_handle);
}

JNIEXPORT jstring JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeGetSessionHandle(JNIEnv* env, jobject thiz) {
    return env->NewStringUTF(g_session_handle);
}

JNIEXPORT jboolean JNICALL
Java_com_calaratjada_insider_data_service_VivoxNative_nativeIsInitialized(JNIEnv* env, jobject thiz) {
    return g_initialized ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"
