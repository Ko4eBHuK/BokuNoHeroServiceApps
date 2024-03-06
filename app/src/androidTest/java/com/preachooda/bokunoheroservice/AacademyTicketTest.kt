package com.preachooda.bokunoheroservice

import android.content.Context
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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
class AacademyTicketTest {
    @get:Rule(order = 0)
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
    fun successCreateAcademyTicket() {
        composeTestRule.onNodeWithText("Заявка на поступление в академию").assertExists()
        composeTestRule.onNodeWithText("Заявка на поступление в академию").performClick()
        composeTestRule.waitUntilTimeout(5000L)

        val resultText1 = "Тестовый Герой Героевич"
        val resultText2 = "18"
        val resultText3 = "Работа с огнем"
        val resultText4 = "1122334455"

        composeTestRule.onNodeWithContentDescription("full name").performTextInput(resultText1)
        composeTestRule.onNodeWithContentDescription("age").performTextInput(resultText2)
        composeTestRule.onNodeWithContentDescription("quirk").performTextInput(resultText3)
        composeTestRule.onNodeWithContentDescription("document num").performTextInput(resultText4)
        composeTestRule.onNodeWithContentDescription("container").performScrollToNode(hasContentDescription("priority3"))

        composeTestRule.onNodeWithContentDescription("priority1").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Академия 1, , ").assertExists()
        composeTestRule.onNodeWithText("Академия 1, , ").performClick()

        composeTestRule.onNodeWithContentDescription("priority2").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Академия 2, , ").assertExists()
        composeTestRule.onNodeWithText("Академия 2, , ").performClick()

        composeTestRule.onNodeWithContentDescription("priority3").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Академия 3, , ").assertExists()
        composeTestRule.onNodeWithText("Академия 3, , ").performClick()

        composeTestRule.onNodeWithText("Подать заявление").assertExists()
        composeTestRule.onNodeWithText("Подать заявление").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithText(
            "Заявка на поступление отправлена, номер заявки: ${MockDispatcher.mocAcademyApp.id}"
        ).assertExists()

    }

    @Test
    fun failCreateAcademyTicket() {
        composeTestRule.onNodeWithText("Заявка на поступление в академию").assertExists()
        composeTestRule.onNodeWithText("Заявка на поступление в академию").performClick()
        composeTestRule.waitUntilTimeout(5000L)

        val resultText1 = "Тестовый Герой Героевич"
        val resultText2 = "18"
        val resultText3 = "Работа с огнем"
        val resultText4 = "1122334455"

        composeTestRule.onNodeWithContentDescription("full name").performTextInput(resultText1)
        composeTestRule.onNodeWithContentDescription("age").performTextInput(resultText2)
        composeTestRule.onNodeWithContentDescription("quirk").performTextInput(resultText3)
        composeTestRule.onNodeWithContentDescription("document num").performTextInput(resultText4)
        composeTestRule.onNodeWithContentDescription("container").performScrollToNode(hasContentDescription("priority3"))

        composeTestRule.onNodeWithContentDescription("priority1").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Академия 1, , ").assertExists()
        composeTestRule.onNodeWithText("Академия 1, , ").performClick()

        composeTestRule.onNodeWithText("Подать заявление").assertExists()
        composeTestRule.onNodeWithText("Подать заявление").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithText(
            "Выберите три учебных заведения в порядке приоритета"
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