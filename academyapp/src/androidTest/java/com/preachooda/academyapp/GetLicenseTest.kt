package com.preachooda.academyapp

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.preachooda.academyapp.composeTestUtils.MockDispatcher
import com.preachooda.academyapp.composeTestUtils.waitUntilTimeout
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
class GetLicenseTest {
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
    fun successGetLicense() {
        composeTestRule.onNodeWithText("Заявка на лицензию героя").assertExists()
        composeTestRule.onNodeWithText("Заявка на лицензию героя").performClick()
        composeTestRule.waitUntilTimeout(5000L)

        val resultText1 = "Безлицензионный Студент Героевич"
        val resultText2 = "МПИ"
        val resultText3 = "Работа с учебными материалами"
        val resultText4 = "1122334455"
        composeTestRule.onNodeWithContentDescription("full name").performTextInput(resultText1)
        composeTestRule.onNodeWithContentDescription("hero's nickname").performTextInput(resultText2)
        composeTestRule.onNodeWithContentDescription("quirk's name").performTextInput(resultText3)
        composeTestRule.onNodeWithContentDescription("document num").performTextInput(resultText4)
        composeTestRule.waitUntilTimeout(5000L)

        composeTestRule.onNodeWithText("Зарегистрировать заявку").assertExists()
        composeTestRule.onNodeWithText("Зарегистрировать заявку").performClick()
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