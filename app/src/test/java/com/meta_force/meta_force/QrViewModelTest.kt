package com.meta_force.meta_force

import com.meta_force.meta_force.data.model.SignedQr
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.QrRepository
import com.meta_force.meta_force.ui.qr.QrUiState
import com.meta_force.meta_force.ui.qr.QrViewModel
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
class QrViewModelTest {

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
    fun `when repository returns success then state is Success`() = runTest {
        val dummy = SignedQr(
            token = "dummy-token",
            userId = "user-1",
            planCode = "standard",
            endDate = "2026-12-31",
            role = "CLIENT",
            exp = 9999999999L,
            planName = "Standard",
            durationLabel = "6 meses",
            durationMonths = 6
        )

        val repo = object : QrRepository {
            override suspend fun getSignedQr(): NetworkResult<SignedQr> {
                return NetworkResult.Success(dummy)
            }
        }

        val viewModel = QrViewModel(repo)

        // El init dispara loadData() automáticamente
        val state = viewModel.uiState.value
        assertTrue(state is QrUiState.Success)
    }

    @Test
    fun `when repository returns error then state is Error`() = runTest {
        val repo = object : QrRepository {
            override suspend fun getSignedQr(): NetworkResult<SignedQr> {
                return NetworkResult.Error(400, "Bad Request")
            }
        }

        val viewModel = QrViewModel(repo)

        val state = viewModel.uiState.value
        assertTrue(state is QrUiState.Error)
    }
}

