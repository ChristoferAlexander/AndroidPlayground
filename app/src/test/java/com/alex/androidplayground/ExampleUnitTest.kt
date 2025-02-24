package com.alex.androidplayground

import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private suspend fun fetchData(): String {
        delay(1000L)
        return "Hello world"
    }

//    @Test
//    fun dataShouldBeHelloWorld() = runTest {
//        val data = fetchData()
//        assertEquals("Hello world", data)
//    }
}