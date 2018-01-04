package com.sdirin.java.newstracker;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.sdirin.java.newstracker.utils.Utils.atPosition;

/**
 * Created by SDirin on 03-Jan-18.
 */
public class MainActivityTest  {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkListIsVisible() {
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.news_list)).check(matches(isDisplayed()));
    }
    @Test
    public void checkTitleIsVisible() {
        activityTestRule.launchActivity(new  Intent());

        CountingIdlingResource countingIdlingResource = new CountingIdlingResource("MainActivity Network");
        activityTestRule.getActivity().setCountingIdlingResource(countingIdlingResource);
        activityTestRule.getActivity().getNewsFromNetwork();
        IdlingRegistry.getInstance().register(countingIdlingResource);

        onView(withId(R.id.news_list))
                .check(matches(atPosition(0, hasDescendant(withText("by")))));
//        onView(withId(R.id.news_list))
//                .check(matches(atPosition(0, hasDescendant(withDrawable(R.drawable.placeholder)))));
    }

}