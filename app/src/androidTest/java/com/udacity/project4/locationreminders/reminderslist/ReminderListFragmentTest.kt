package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeAndroidTestRepository
import com.udacity.project4.testModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val repository: FakeAndroidTestRepository by inject()

    private lateinit var auth: FirebaseAuth

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        //declare a new koin module
        startKoin {
            androidContext(getApplicationContext())
            loadKoinModules(testModule)
        }
    }

    @After
    fun cleanup() = runBlockingTest {
        repository.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun noData_displayNoDataTextView() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun loadReminders_displayListView() = runBlockingTest {
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)

        repository.saveReminder(reminderDTO1)
        repository.saveReminder(reminderDTO2)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withText(reminderDTO1.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO2.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO1.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO2.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO1.location)).check(matches(isDisplayed()))
        onView(withText(reminderDTO2.location)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAddReminder_navigateToSaveReminderFragment() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.addReminderFAB))
            .perform(click())

        // THEN - Verify that we navigate to the save reminder screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }


}