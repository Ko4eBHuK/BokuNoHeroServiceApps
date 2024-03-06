package com.preachooda.adminapp

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.preachooda.adminapp.composeTestUtils.MockDispatcher
import com.preachooda.adminapp.composeTestUtils.waitUntilTimeout
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
class AccessRathingTest {
    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val mRuntimePermissionRuleLocation: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        composeTestRule.waitUntilTimeout(5000L)
    }

    @Test
    fun successAccessRathing() {
        composeTestRule.onNodeWithText("Рейтинг героев").assertExists()
        composeTestRule.onNodeWithText("Рейтинг героев").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText("Hero_1").assertExists()
        composeTestRule.onNodeWithText("Hero_1").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        val resultText1 = "7"
        composeTestRule.onNodeWithContentDescription("new rating").performTextInput(resultText1)

        composeTestRule.onNodeWithText("Отправить").assertExists()
        composeTestRule.onNodeWithText("Отправить").performClick()
        composeTestRule.waitUntilTimeout(1000L)

    }

    @Test
    fun fastQualificationExamTicket() {
        composeTestRule.onNodeWithText("Рейтинг героев").assertExists()
        composeTestRule.onNodeWithText("Рейтинг героев").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText("Hero_1").assertExists()
        composeTestRule.onNodeWithText("Hero_1").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithText("Отправить").assertExists()
        composeTestRule.onNodeWithText("Отправить").performClick()
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