package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    private val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
    private val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)
    private val reminderDTO3 = ReminderDTO("title3", "description3", "location3", 12.0, 22.0)
    private val reminderDtos = listOf(reminderDTO1, reminderDTO2, reminderDTO3)


    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private val applicationContext: Application = ApplicationProvider.getApplicationContext()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupTest() {
        reminderDataSource = FakeDataSource(reminderDtos.toMutableList())
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    fun loadReminders_getRemindersFromDataSourceSuccess() {
        //Given the viewModel, when  reminders are loaded
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue()?.size, `is`(3))
    }

    @Test
    fun loadReminder_getRemindersFromDataSourceError() {
        //Make the datasource return an error
        reminderDataSource.setReturnError(true)
        //load reminders
        remindersListViewModel.loadReminders()
        //Display error
        assertThat(remindersListViewModel.showSnackBar.value, `is`("Test exception"))
    }

    @Test
    fun loadReminder_loading() {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()
        //load reminders
        remindersListViewModel.loadReminders()
        // Then progress indicator is shown.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}