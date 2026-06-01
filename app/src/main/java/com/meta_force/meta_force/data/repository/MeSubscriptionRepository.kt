package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Invoice
import com.meta_force.meta_force.data.model.Subscription
import com.meta_force.meta_force.data.network.MeSubscriptionApi
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import javax.inject.Inject

/**
 * Repositorio para el historial de suscripciones y descarga de facturas.
 */
interface MeSubscriptionRepository {

    /**
     * Obtiene el listado de suscripciones del usuario actual.
     */
    suspend fun getMySubscriptions(): NetworkResult<List<Subscription>>

    /**
     * Devuelve una URL firmada temporal para descargar la factura.
     */
    suspend fun getInvoiceSignedUrl(invoiceId: String): NetworkResult<String>
}

class MeSubscriptionRepositoryImpl @Inject constructor(
    private val api: MeSubscriptionApi
) : MeSubscriptionRepository {

    override suspend fun getMySubscriptions(): NetworkResult<List<Subscription>> {
        return safeApiCall {
            val rows = api.getMySubscriptions()
            rows.map { dto ->
                val invoice = Invoice(
                    id = dto.invoiceId,
                    number = dto.invoiceNumber,
                    issueDate = dto.invoiceIssueDate,
                    total = dto.total
                )
                Subscription(
                    id = dto.subscriptionId,
                    planName = dto.planName,
                    planCode = dto.planCode,
                    durationLabel = dto.durationLabel,
                    startDate = dto.startDate,
                    endDate = dto.endDate,
                    status = dto.status,
                    total = dto.total,
                    invoice = invoice
                )
            }
        }
    }

    override suspend fun getInvoiceSignedUrl(invoiceId: String): NetworkResult<String> {
        return safeApiCall {
            val response = api.getInvoiceSignedUrl(
                mapOf("p_invoice_id" to invoiceId)
            )
            response.url
        }
    }
}

