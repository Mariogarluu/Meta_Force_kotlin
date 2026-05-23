package com.meta_force.meta_force

import com.meta_force.meta_force.data.model.Invoice
import com.meta_force.meta_force.data.model.Subscription
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.MeSubscriptionRepository
import com.meta_force.meta_force.ui.billing.me.MySubscriptionUiState
import com.meta_force.meta_force.ui.billing.me.MySubscriptionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MySubscriptionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when repository returns subscriptions then state is Success`() = runTest {
        val invoice = Invoice(
            id = "inv-1",
            number = "2026-00001",
            issueDate = "2026-05-09",
            total = 121.0
        )
        val subscription = Subscription(
            id = "sub-1",
            planName = "Standard",
            planCode = "standard",
            durationLabel = "6 meses",
            startDate = "2026-05-09",
            endDate = "2026-11-08",
            status = "active",
            total = 121.0,
            invoice = invoice
        )

        val repo = object : MeSubscriptionRepository {
            override suspend fun getMySubscriptions(): NetworkResult<List<Subscription>> {
                return NetworkResult.Success(listOf(subscription))
            }

            override suspend fun getInvoiceSignedUrl(invoiceId: String): NetworkResult<String> {
                return NetworkResult.Success("https://example.com/invoice.pdf")
            }
        }

        val viewModel = MySubscriptionViewModel(repo)

        val state = viewModel.uiState.value
        assertTrue(state is MySubscriptionUiState.Success)
    }

    @Test
    fun `when repository returns error then state is Error`() = runTest {
        val repo = object : MeSubscriptionRepository {
            override suspend fun getMySubscriptions(): NetworkResult<List<Subscription>> {
                return NetworkResult.Error(500, "Server error")
            }

            override suspend fun getInvoiceSignedUrl(invoiceId: String): NetworkResult<String> {
                return NetworkResult.Error(500, "Server error")
            }
        }

        val viewModel = MySubscriptionViewModel(repo)

        val state = viewModel.uiState.value
        assertTrue(state is MySubscriptionUiState.Error)
    }
}

