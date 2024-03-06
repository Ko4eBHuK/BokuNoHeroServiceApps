package com.preachooda.bokunoheroservice

import android.content.Context
import android.widget.RatingBar
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.preachooda.assets.ui.RateStarsRow
import com.preachooda.bokunoheroservice.composeTestUtils.MockDispatcher
import com.preachooda.bokunoheroservice.composeTestUtils.waitUntilTimeout
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RateTheTicket {
    @get:Rule(order=0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val mRuntimePermissionRuleLocation: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        //android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        composeTestRule.waitUntilTimeout(5000L)
    }

    @Test
    fun successRateTheTicket() {
        composeTestRule.onNodeWithText("Все заявки").assertExists()
        composeTestRule.onNodeWithText("Все заявки").performClick()
        composeTestRule.waitUntilTimeout(5000L)
        composeTestRule.onNodeWithText("На оценке").assertExists()
        composeTestRule.onNodeWithText("На оценке").performClick()
        composeTestRule.waitUntilTimeout(9000L)

//        composeTestRule.onNodeWithContentDescription("Hero_1 5").assertExists()
//        composeTestRule.onNodeWithContentDescription("Hero_1 5").performClick()
//
//        composeTestRule.onNodeWithContentDescription("Hero_2 4").assertExists()
//        composeTestRule.onNodeWithContentDescription("Hero_2 4").performClick()

        val resultText = "Хорошая работа"
        composeTestRule.onNodeWithContentDescription("ticket rate comment").performTextInput(resultText)

        composeTestRule.onNodeWithText("Сохранить").assertExists()
        composeTestRule.onNodeWithText("Сохранить").performClick()
        composeTestRule.waitUntilTimeout(1000L)
    }

    companion object {
        private lateinit var mockServer: MockWebServer

        @BeforeClass
        @JvmStatic
        fun initAll() {
            // Mock network
            mockServer = MockWebServer()
            mockServer.start(8000)
            mockServer.dispatcher = MockDispatcher
            mockServer.url("http://localhost:8000")

            // Mock login
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val sharedPref = appContext.getSharedPreferences(
                appContext.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putString(
                    appContext.getString(R.string.user_network_token_key),
                    "test_token"
                )
                apply()
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            mockServer.shutdown()
        }
    }
}