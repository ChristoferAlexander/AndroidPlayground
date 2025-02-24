package com.alex.androidplayground

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alex.androidplayground.model.ui.getNavDrawerItems
import com.alex.androidplayground.ui.layout.MainScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 */
@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testNavigationDrawerTitleUpdates() {
        // Set the content to your MainScreen composable
        composeTestRule.activity.apply {
            setContent {
                MainScreen()
            }
        }

        // Iterate through each navigation drawer item
        getNavDrawerItems().forEach { item ->
            // Open the navigation drawer
            composeTestRule.onNodeWithContentDescription("Menu toggle icon").performClick()
            // Click on the navigation drawer item
            composeTestRule.onNodeWithTag("NavItem_${item.text}").performClick()
            // Assert that the title in the TopAppBar matches the selected item's text
            composeTestRule.onNodeWithTag("TopAppBarTitle")
                .assertTextEquals(item.text)
                .assertIsDisplayed()
        }
    }
}
