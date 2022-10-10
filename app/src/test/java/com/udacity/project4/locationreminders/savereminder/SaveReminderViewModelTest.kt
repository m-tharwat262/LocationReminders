package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val reminderDataItem1 =
        ReminderDataItem("titleTest", "descriptionTest", "locationTest", 10.1, 10.2)

    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private val applicationContext: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setupTest() {
        reminderDataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    fun saveReminder_savesToDataSource() {
        // When a  reminder is saved
        saveReminderViewModel.saveReminder(reminderDataItem1)
        // Then showLoading is set to false and  reminder saved toast is displayed
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(), `is`
                (applicationContext.getString(R.string.reminder_saved))
        )
    }

    @Test
    fun saveReminder_validatesEmptyTitle() {
        // When the reminder title is not set
        val reminderDataItem1 = ReminderDataItem("", "descriptionTest", "locationTest", 10.1, 10.2)
        //Validate is called
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem1)
        //Then snackbar prompting user to set title is displayed and false is returned
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`
                (R.string.err_enter_title)
        )
        Assert.assertFalse(result)
    }

    @Test
    fun saveReminder_validatesEmptyLocation() {
        // When the reminder description is not set
        val reminderDataItem1 = ReminderDataItem("titleTest", "descriptionTest", "", 10.1, 10.2)
        //Validate is called
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem1)
        //Then snackbar prompting user to set title is displayed and false is returned
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`
                (R.string.err_select_location)
        )
        Assert.assertFalse(result)
    }

    @Test
    fun saveReminder_validatesCorrectData() {
        //When all data is correct and Validate is called
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem1)
        //Then true is returned
        Assert.assertTrue(result)
    }

    @Test
    fun saveReminder_clearsLiveData() {
        // When a  reminder is cleared
        saveReminderViewModel.onClear()
        // livedata is reset to null
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), nullValue())
        assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            nullValue()
        )
        assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            nullValue()
        )
        assertThat(
            saveReminderViewModel.selectedPOI.getOrAwaitValue(),
            nullValue()
        )
        assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            nullValue()
        )
        assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            nullValue()
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}