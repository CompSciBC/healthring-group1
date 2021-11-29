package com.example.healthring

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HealthMonitorFragmentTest {

    // launch the activity
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun goToFitnessFragmentTest() {
        onView(withId(R.id.fitness_tracker_button)).perform(click())
        onView(withId(R.id.fitness_steps_tracker)).check(matches(isDisplayed()))
    }

    @Test
    fun goToTaskFragmentTest() {
        onView(withId(R.id.todays_task_button)).perform(click())
        onView(withId(R.id.todays_tasks_alarm)).check(matches(isDisplayed()))
    }

    @Test
    fun goToProfileFragmentTest() {
        onView(withId(R.id.profile_picture_button)).perform(click())
        onView(withId(R.id.profileImage)).check(matches(isDisplayed()))
    }

    @Test
    fun allViewsAreDisplayedTest() {
        onView(withId(R.id.health_ring)).check(matches(isDisplayed()))
        onView(withId(R.id.health_ring_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.health_monitor_header)).check(matches(isDisplayed()))
        onView(withId(R.id.profile_picture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.heart_rate_tracker)).check(matches(isDisplayed()))
        onView(withId(R.id.blood_pressure_tracker)).check(matches(isDisplayed()))
        onView(withId(R.id.blood_oxygen_tracker)).check(matches(isDisplayed()))
        onView(withId(R.id.health_monitor_button)).check(matches(isDisplayed()))
        onView(withId(R.id.fitness_tracker_button)).check(matches(isDisplayed()))
        onView(withId(R.id.todays_task_button)).check(matches(isDisplayed()))
    }
}