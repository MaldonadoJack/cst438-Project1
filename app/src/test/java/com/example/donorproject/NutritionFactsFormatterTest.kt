package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionFactsFormatterTest {

    @Test
    fun grams_removesUnnecessaryDecimalFromWholeNumber() {
        assertEquals("31 g", NutritionFactsFormatter.grams(31.0))
    }

    @Test
    fun grams_preservesDecimalValue() {
        assertEquals("3.6 g", NutritionFactsFormatter.grams(3.6))
    }

    @Test
    fun grams_formatsZero() {
        assertEquals("0 g", NutritionFactsFormatter.grams(0.0))
    }
}