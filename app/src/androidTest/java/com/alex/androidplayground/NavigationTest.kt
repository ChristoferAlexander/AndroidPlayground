package com.alex.androidplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alex.androidplayground.core.ui.MainActivity
import com.alex.androidplayground.core.ui.model.navigation.getNavDrawerItems
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 */
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationDrawerTitleUpdates() = runTest {
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
