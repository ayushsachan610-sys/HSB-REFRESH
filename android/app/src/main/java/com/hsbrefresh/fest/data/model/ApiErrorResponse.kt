package com.hsbrefresh.fest.data.model

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(
    @SerializedName("detail")
    val detail: String? = null
)
