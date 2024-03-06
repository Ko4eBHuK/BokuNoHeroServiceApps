package com.preachooda.heroapp

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.preachooda.heroapp.composeTestUtils.MockDispatcher
import com.preachooda.heroapp.composeTestUtils.waitUntilTimeout
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
class AccessHeroTicketTest {
    @get:Rule(order=0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val mRuntimePermissionRuleLocation: GrantPermissionRule = GrantPermissionRule.grant(

    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        composeTestRule.waitUntilTimeout(5000L)
    }

    @Test
    fun successRateTheTicket() {
        composeTestRule.onNodeWithText("Заявка на оказание помощи").assertExists()
        composeTestRule.onNodeWithText("Заявка на оказание помощи").performClick()
        composeTestRule.waitUntilTimeout(10000L)

        composeTestRule.onNodeWithText("Начать").assertExists()
        composeTestRule.onNodeWithText("Начать").performClick()
        composeTestRule.waitUntilTimeout(3000L)

        composeTestRule.onNodeWithText("OK").assertExists()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText("Завершить").assertExists()
        composeTestRule.onNodeWithText("Завершить").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithText("Заявка сохранена.").assertExists()
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
                    appContext.getString(R.string.hero_network_token_key),
                    "test_token"
                )
                putLong(
                    appContext.getString(R.string.hero_id_key),
                    1
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