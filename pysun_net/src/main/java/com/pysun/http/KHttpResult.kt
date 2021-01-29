package com.pysun.http

abstract class KHttpResult<T> {
    companion object {
        const val CODE_SUCCESS = "code_success"
        const val CODE_ERROR = "code_error"
        const val CODE_UNKNOWN = "code_unknown"
        const val CODE_DATA_ERROR = "code_data_error"
        const val CODE_DATA_NULL = "code_data_null"

    }


    abstract fun responseCode(): String

    abstract fun getCode(): String

    abstract fun getMsg(): String?

    abstract fun getData(): T?



}