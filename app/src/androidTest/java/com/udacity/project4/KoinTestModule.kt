package com.udacity.project4

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.udacity.project4.locationreminders.data.local.FakeAndroidTestRepository
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val testModule = module {
    viewModel {
        RemindersListViewModel(
            getApplicationContext(),
            get() as FakeAndroidTestRepository
        )
    }
    single {
        SaveReminderViewModel(
            getApplicationContext(),
            get() as FakeAndroidTestRepository
        )
    }
    single { FakeAndroidTestRepository() }
    single { LocalDB.createRemindersDao(getApplicationContext()) }
}