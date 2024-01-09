package ua.patronum.quicklink.restapi.url;

public enum Error {
    OK,
    INVALID_ID,
    INVALID_ACCESS,
    EMPTY_NEW_URL,
    INVALID_SHORT_URL,
    EXPIRED_URL,
    TIME_NOT_PASSED,
    INVALID_ORIGINAL_URL,
    CACHE_ERROR
}
