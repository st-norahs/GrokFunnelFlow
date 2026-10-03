package com.grokfunnel.data.remote.paystack

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaystackInitRequest(
    val email: String,
    val amount: Long,
    val reference: String,
    val currency: String = "USD",
    val callback_url: String? = null,
    val metadata: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class PaystackInitResponse(
    val status: Boolean,
    val message: String,
    val data: PaystackInitData? = null
)

@JsonClass(generateAdapter = true)
data class PaystackInitData(
    val authorization_url: String,
    val access_code: String,
    val reference: String
)

@JsonClass(generateAdapter = true)
data class PaystackVerifyResponse(
    val status: Boolean,
    val message: String,
    val data: PaystackVerifyData? = null
)

@JsonClass(generateAdapter = true)
data class PaystackVerifyData(
    val id: Long? = null,
    val domain: String? = null,
    val status: String,
    val reference: String,
    val amount: Long,
    val message: String? = null,
    val gateway_response: String? = null,
    val paid_at: String? = null,
    val created_at: String? = null,
    val channel: String? = null,
    val currency: String = "USD",
    val ip_address: String? = null,
    val customer: PaystackCustomerInfo? = null,
    val metadata: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class PaystackCustomerInfo(
    val id: Long? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String,
    val phone: String? = null,
    val customer_code: String? = null
)

@JsonClass(generateAdapter = true)
data class PaystackCustomerListResponse(
    val status: Boolean,
    val message: String,
    val data: List<PaystackCustomerInfo>? = null
)

sealed class PaystackApiResult<out T> {
    data class Success<out T>(val data: T, val message: String) : PaystackApiResult<T>()
    data class Error(val code: Int, val message: String, val errorBody: String? = null) : PaystackApiResult<Nothing>()
    data class NetworkFailure(val exception: Throwable) : PaystackApiResult<Nothing>()
}
