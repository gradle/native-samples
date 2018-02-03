/**
 * Copyright IBM Corporation 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

#ifndef CurlHelpers_h
#define CurlHelpers_h

#import <curl/curl.h>

#define CURL_TRUE  1
#define CURL_FALSE 0

static inline CURLcode curlHelperSetOptBool(CURL *curl, CURLoption option, int yesNo) {
    return curl_easy_setopt(curl, option, yesNo == CURL_TRUE ? 1L : 0L);
}

// set options list - CURLOPT_HTTPHEADER, CURLOPT_HTTP200ALIASES, CURLOPT_QUOTE, CURLOPT_TELNETOPTIONS, CURLOPT_MAIL_RCPT, etc.
static inline CURLcode curlHelperSetOptList(CURL *curl, CURLoption option, struct curl_slist *list) {
    return curl_easy_setopt(curl, option, list);
}

__attribute__((deprecated("curlHelperSetOptHeaders has been deprecated please use curlHelperSetOptList(curl, CURLOPT_HTTPHEADER, headers) instead")))
static inline CURLcode curlHelperSetOptHeaders(CURL *curl, struct curl_slist *headers) {
    return curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
}

static inline CURLcode curlHelperSetOptInt(CURL *curl, CURLoption option, long data) {
    return curl_easy_setopt(curl, option, data);
}

// const keyword is used so that Swift strings can be passed
static inline CURLcode curlHelperSetOptString(CURL *curl, CURLoption option, const char *data) {
    return curl_easy_setopt(curl, option, data);
}

static inline CURLcode curlHelperSetOptReadFunc(CURL *curl, void *userData, size_t (*read_cb) (char *buffer, size_t size, size_t nitems, void *userdata)) {
    
    CURLcode rc = curl_easy_setopt(curl, CURLOPT_READDATA, userData);
    if  (rc == CURLE_OK) {
        rc = curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_cb);
    }
    return rc;
}

static inline CURLcode curlHelperSetOptWriteFunc(CURL *curl, void *userData, size_t (*write_cb) (char *ptr, size_t size, size_t nmemb, void *userdata)) {

    CURLcode rc = curl_easy_setopt(curl, CURLOPT_WRITEDATA, userData);
    if  (rc == CURLE_OK) {
        rc = curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_cb);
    }
    return rc;
}

static inline CURLcode curlHelperSetOptHeaderFunc(CURL *curl, void *userData, size_t (*header_cb) (char *buffer, size_t size, size_t nmemb, void *userdata)) {

    CURLcode rc = curl_easy_setopt(curl, CURLOPT_HEADERDATA, userData);
    if (rc == CURLE_OK) {
        rc = curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, header_cb);
    }
    return rc;
}

static inline CURLcode curlHelperGetInfoCString(CURL *curl, CURLINFO info, char **data) {
    return curl_easy_getinfo(curl, info, data);
}


static inline CURLcode curlHelperGetInfoLong(CURL *curl, CURLINFO info, long *data) {
    return curl_easy_getinfo(curl, info, data);
}


#endif /* CurlHelpers_h */