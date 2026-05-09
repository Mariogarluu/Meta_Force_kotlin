package com.meta_force.meta_force.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * API de Supabase (Data API) para obtener el historial de suscripciones del
 * usuario autenticado y generar URLs firmadas de facturas.
 *
 * Implementa las RPC:
 * - get_my_subscriptions()
 * - get_invoice_signed_url(invoice_id uuid)
 */
interface MeSubscriptionApi {

    /**
     * Devuelve el listado de suscripciones del usuario actual junto con su
     * factura principal asociada.
     */
    @Headers("Content-Type: application/json")
    @POST("rest/v1/rpc/get_my_subscriptions")
    suspend fun getMySubscriptions(
        @Body body: Map<String, @JvmSuppressWildcards Any?> = emptyMap()
    ): List<MySubscriptionDto>

    /**
     * Devuelve una URL firmada temporal para descargar la factura indicada.
     */
    @Headers("Content-Type: application/json")
    @POST("rest/v1/rpc/get_invoice_signed_url")
    suspend fun getInvoiceSignedUrl(
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): SignedUrlResponse
}

/**
 * DTO plano que representa una fila de historial devuelta por `get_my_subscriptions`.
 */
data class MySubscriptionDto(
    @SerializedName("subscription_id")
    val subscriptionId: String,
    @SerializedName("invoice_id")
    val invoiceId: String,
    @SerializedName("plan_name")
    val planName: String,
    @SerializedName("plan_code")
    val planCode: String,
    @SerializedName("duration_label")
    val durationLabel: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    val status: String,
    val total: Double,
    @SerializedName("invoice_number")
    val invoiceNumber: String?,
    @SerializedName("invoice_issue_date")
    val invoiceIssueDate: String
)

data class SignedUrlResponse(
    val url: String
)

