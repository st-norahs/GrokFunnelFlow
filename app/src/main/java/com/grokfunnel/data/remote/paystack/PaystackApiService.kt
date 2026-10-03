package com.grokfunnel.data.remote.paystack

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaystackApiService {

    @POST("transaction/initialize")
    suspend fun initializeTransaction(
        @Header("Authorization") authorization: String,
        @Body request: PaystackInitRequest
    ): Response<PaystackInitResponse>

    @GET("transaction/verify/{reference}")
    suspend fun verifyTransaction(
        @Header("Authorization") authorization: String,
        @Path("reference") reference: String
    ): Response<PaystackVerifyResponse>

    @GET("customer")
    suspend fun listCustomers(
        @Header("Authorization") authorization: String
    ): Response<PaystackCustomerListResponse>
}
