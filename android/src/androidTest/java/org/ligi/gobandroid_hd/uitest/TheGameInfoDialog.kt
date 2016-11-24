package org.ligi.gobandroid_hd.uitest

import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.TestApp
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity
import org.ligi.trulesk.TruleskActivityRule
import org.ligi.trulesk.invokeMenu
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheGameInfoDialog {

    @get:Rule
    val rule = TruleskActivityRule(GameReviewActivity::class.java, false)

    @Inject
    lateinit var gameProvider: GameProvider

    @Before
    fun setUp() {
        TestApp.component().inject(this)
    }

    @Test
    fun testThatTheDialogShows() {
        val activity = openDialog()
        onView(withId(R.id.game_name_et)).check(matches(isDisplayed()))
        Spoon.screenshot(activity, "game_info_dialog")
    }

    fun openDialog(): GameReviewActivity {
        val activity = rule.launchActivity(null)

        invokeMenu(R.id.menu_game_info, R.string.game_info)

        return activity
    }

    @Test
    fun testThatFieldsWork() {
        val activity = openDialog()

        onView(withId(R.id.game_name_et)).perform(replaceText(CUSTOM_GAME_NAME))

        onView(withId(R.id.black_rank_et)).perform(replaceText(CUSTOM_BLACK_RANK))

        onView(withId(R.id.user_is_black_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.black_name_et)).perform(replaceText(CUSTOM_BLACK_NAME))
        onView(withId(R.id.user_is_black_btn)).check(matches(not(isDisplayed())))

        onView(withId(R.id.white_rank_et)).perform(scrollTo())
        onView(withId(R.id.white_rank_et)).perform(replaceText(CUSTOM_WHITE_RANK))
        onView(withId(R.id.white_name_et)).perform(replaceText(CUSTOM_WHITE_NAME))


        onView(withId(R.id.komi_et)).perform(scrollTo(), clearText(), replaceText(CUSTOM_KOMI))

        onView(withId(R.id.game_result_et)).perform(scrollTo(), clearText(), replaceText(CUSTOM_KOMI), closeSoftKeyboard())

        SystemClock.sleep(100)

        onView(withText(android.R.string.ok)).perform(scrollTo(), click())

        val metaData = gameProvider.get().metaData

        assertThat(metaData.name).isEqualTo(CUSTOM_GAME_NAME)
        assertThat(metaData.whiteName).isEqualTo(CUSTOM_WHITE_NAME)
        assertThat(metaData.whiteRank).isEqualTo(CUSTOM_WHITE_RANK)
        assertThat(metaData.blackName).isEqualTo(CUSTOM_BLACK_NAME)
        assertThat(metaData.blackRank).isEqualTo(CUSTOM_BLACK_RANK)
        assertThat(gameProvider.get().komi).isEqualTo(java.lang.Float.valueOf(CUSTOM_KOMI))

        Spoon.screenshot(activity, "game_info_dialog")
    }

    @Test
    fun testThatBadKomiIsRejected() {
        gameProvider.get().komi = java.lang.Float.valueOf(CUSTOM_KOMI)
        val activity = openDialog()
        onView(withId(R.id.komi_et)).perform(scrollTo(), clearText(), typeText("a"), closeSoftKeyboard())

        SystemClock.sleep(100)

        onView(withText(android.R.string.ok)).perform(scrollTo(), click())

        onView(withText(R.string.komi_must_be_a_number)).check(matches(isDisplayed()))

        assertThat(gameProvider.get().komi).isEqualTo(java.lang.Float.valueOf(CUSTOM_KOMI))
        Spoon.screenshot(activity, "game_info_reject_komi")
    }

    companion object {

        val CUSTOM_BLACK_RANK = "custom black rank"
        val CUSTOM_BLACK_NAME = "custom black name"
        val CUSTOM_GAME_NAME = "custom game name"
        val CUSTOM_WHITE_NAME = "custom white name"
        val CUSTOM_WHITE_RANK = "custom white rank"
        val CUSTOM_KOMI = "4.2"
    }

}
