package com.dataguard.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats
import com.dataguard.data.repository.DataRepository
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.MockitoAnnotations
import kotlin.test.assertIs

@Ignore("Requires Android runtime context - use instrumented tests instead")
class GetUsageStatsUseCaseTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var repository: DataRepository

    @Mock
    private lateinit var packageManager: PackageManager

    private lateinit var gson: Gson
    private lateinit var useCase: GetUsageStatsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gson = Gson()
        useCase = GetUsageStatsUseCase(context, repository, gson)
    }

    @Test
    fun testGetUsageStats_Success() = runTest {
        // Mock TrafficStats
        mockStatic(TrafficStats::class.java).use { trafficStatsMock ->
            trafficStatsMock.`when`<Long> { TrafficStats.getMobileTxBytes() }
                .thenReturn(100 * 1024 * 1024) // 100MB
            trafficStatsMock.`when`<Long> { TrafficStats.getMobileRxBytes() }
                .thenReturn(150 * 1024 * 1024) // 150MB

            val result = useCase("daily")
            assertIs<GetUsageStatsUseCase.Result.Success>(result)
        }
    }

    @Test
    fun testGetUsageStats_Error() = runTest {
        mockStatic(TrafficStats::class.java).use { trafficStatsMock ->
            trafficStatsMock.`when`<Long> { TrafficStats.getMobileTxBytes() }
                .thenThrow(RuntimeException("Test error"))

            val result = useCase("daily")
            assertIs<GetUsageStatsUseCase.Result.Error>(result)
        }
    }
}
