package com.truyencv.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterleaveUtilTest {

    @Test
    fun testBothEmpty() {
        val result = interleave(emptyList<String>(), emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun testFirstListEmpty() {
        val result = interleave(emptyList(), listOf("A", "B", "C"))
        assertEquals(listOf("A", "B", "C"), result)
    }

    @Test
    fun testSecondListEmpty() {
        val result = interleave(listOf(1, 2, 3), emptyList())
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testEqualLength() {
        val list1 = listOf("A1", "A2", "A3")
        val list2 = listOf("B1", "B2", "B3")
        val result = interleave(list1, list2)
        assertEquals(listOf("A1", "B1", "A2", "B2", "A3", "B3"), result)
    }

    @Test
    fun testFirstListLonger() {
        val list1 = listOf("A1", "A2", "A3", "A4", "A5")
        val list2 = listOf("B1", "B2")
        val result = interleave(list1, list2)
        assertEquals(listOf("A1", "B1", "A2", "B2", "A3", "A4", "A5"), result)
    }

    @Test
    fun testSecondListLonger() {
        val list1 = listOf("A1", "A2")
        val list2 = listOf("B1", "B2", "B3", "B4")
        val result = interleave(list1, list2)
        assertEquals(listOf("A1", "B1", "A2", "B2", "B3", "B4"), result)
    }
}
