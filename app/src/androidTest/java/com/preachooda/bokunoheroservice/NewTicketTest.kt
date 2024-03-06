package com.preachooda.bokunoheroservice

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
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
class NewTicketTest {
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

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        composeTestRule.waitUntilTimeout(10000L)
    }

    @Test
    fun successNewTicketTest() {
        composeTestRule.onNodeWithContentDescription("new ticket").assertExists()
        composeTestRule.onNodeWithContentDescription("new ticket").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithText("Пожар").assertExists()
        composeTestRule.onNodeWithText("Пожар").performClick()
        composeTestRule.onNodeWithText("Землетрясение").assertExists()
        composeTestRule.onNodeWithText("Землетрясение").performClick()

        val resultText = "Из-за землетрясения случился пожар"

        // Sets the TextField value
        composeTestRule.onNodeWithContentDescription("new ticket description").performTextInput(resultText)

        composeTestRule.onNodeWithText("Создать").assertExists()
        composeTestRule.onNodeWithText("Создать").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText(
            "Создана заявка с номером ${MockDispatcher.mockTicket.id}"
        ).assertExists()
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