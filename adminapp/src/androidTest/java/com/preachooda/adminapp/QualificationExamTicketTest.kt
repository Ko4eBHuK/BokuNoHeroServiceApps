package com.preachooda.adminapp

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class QualificationExamTicketTest {
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
    fun successQualificationExamTicket() {
        composeTestRule.onNodeWithText("Заявки на квалификационный экзамен").assertExists()
        composeTestRule.onNodeWithText("Заявки на квалификационный экзамен").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText("Создана").assertExists()
        composeTestRule.onNodeWithText("Создана").performClick()
        composeTestRule.waitUntilTimeout(1000L)

        composeTestRule.onNodeWithContentDescription("priority1").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Hero_1, , REINFORCEMENT, F, сила 0, скорость 0, техника 0, интеллект 0, работа в команде 0").assertExists()
        composeTestRule.onNodeWithText("Hero_1, , REINFORCEMENT, F, сила 0, скорость 0, техника 0, интеллект 0, работа в команде 0").performClick()

        composeTestRule.onNodeWithContentDescription("priority2").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Hero_2, , REINFORCEMENT, F, сила 0, скорость 0, техника 0, интеллект 0, работа в команде 0").assertExists()
        composeTestRule.onNodeWithText("Hero_2, , REINFORCEMENT, F, сила 0, скорость 0, техника 0, интеллект 0, работа в команде 0").performClick()

        composeTestRule.onNodeWithText("Подтвердить").assertExists()
        composeTestRule.onNodeWithText("Подтвердить").performClick()
        composeTestRule.waitUntilTimeout(2000L)

        composeTestRule.onNodeWithText("Заявка на экзамен №112233 сформирована").assertExists()
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