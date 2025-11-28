package com.dataguard.utils

import org.junit.Test
import kotlin.test.assertEquals

class DataFormatterTest {

    @Test
    fun testFormatBytes_Bytes() {
        val result = DataFormatter.formatBytes(512)
        assertEquals("512 B", result)
    }

    @Test
    fun testFormatBytes_KB() {
        val result = DataFormatter.formatBytes(1024 * 2)
        assertEquals("2.00 KB", result)
    }

    @Test
    fun testFormatBytes_MB() {
        val result = DataFormatter.formatBytes(1024 * 1024 * 100)
        assertEquals("100.00 MB", result)
    }

    @Test
    fun testFormatBytes_GB() {
        val result = DataFormatter.formatBytes(1024L * 1024L * 1024L * 2)
        assertEquals("2.00 GB", result)
    }

    @Test
    fun testFormatBytesShort_MB() {
        val result = DataFormatter.formatBytesShort(1024 * 1024 * 250)
        assertEquals("250.0 MB", result)
    }

    @Test
    fun testGetStartOfDay() {
        val timestamp = System.currentTimeMillis()
        val startOfDay = DataFormatter.getStartOfDay(timestamp)
        
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startOfDay
        
        assertEquals(0, calendar.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(java.util.Calendar.MINUTE))
        assertEquals(0, calendar.get(java.util.Calendar.SECOND))
    }

    @Test
    fun testGetStartOfWeek() {
        val timestamp = System.currentTimeMillis()
        val startOfWeek = DataFormatter.getStartOfWeek(timestamp)
        
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startOfWeek
        
        assertEquals(java.util.Calendar.MONDAY, calendar.get(java.util.Calendar.DAY_OF_WEEK))
    }

    @Test
    fun testGetStartOfMonth() {
        val timestamp = System.currentTimeMillis()
        val startOfMonth = DataFormatter.getStartOfMonth(timestamp)
        
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startOfMonth
        
        assertEquals(1, calendar.get(java.util.Calendar.DAY_OF_MONTH))
    }
}
