package com.pysun.http


class KCustomHttpException:Exception {

    companion object {
        val CODE_DEFAULT = "code_default"
        val CODE_HTTP_ERROR = "code_http_error"
        val CODE_UNKNOWN_HOST = "code_unknown_host"
    }

    private var mCode: String? = null

    constructor(code: String?, msg: String?):super(msg) {
        mCode = code
    }

    fun getCode(): String? {
        return mCode
    }

    override val message: String?
        get() = super.message

    override fun getLocalizedMessage(): String {
        return super.getLocalizedMessage()
    }
}