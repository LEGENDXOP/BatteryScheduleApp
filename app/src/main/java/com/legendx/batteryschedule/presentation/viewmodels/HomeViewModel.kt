package com.legendx.batteryschedule.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.legendx.batteryschedule.data.database.BatteryDatabase
import com.legendx.batteryschedule.data.database.BatteryEntity
import com.legendx.batteryschedule.utils.BatteryUtils
import kotlinx.coroutines.launch

class HomeViewModel(
    appDB: BatteryDatabase
) : ViewModel() {
    val batteryDao = appDB.getBatteryDao()
    val batteryList = mutableStateListOf<BatteryEntity>()
    val sortedBatteryList: List<BatteryEntity>
        get() = batteryList.sortedByDescending { it.batteryPercentage }
    var textState by mutableStateOf("")
        private set
    var descriptionState by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            batteryDao.getAllBatteryDb().collect { data ->
                batteryList.clear()
                batteryList.addAll(data)
            }
        }
    }

    fun getBatteryLevel(context: Context): Int = BatteryUtils.getCurrentBatteryPercentage(context)

    fun addSchedule(
        title: String,
        description: String,
        batteryPercentage: Int
    ) {
        val batteryData = BatteryEntity(title, description, batteryPercentage)
        viewModelScope.launch {
            batteryDao.addScheduleDb(batteryData)
        }
    }

    fun updateTextState(value: String) {
        textState = value
    }

    fun updateDescriptionState(value: String) {
        descriptionState = value
    }

    fun resetTextStates() {
        textState = ""
        descriptionState = ""
    }

    fun checkForAdd(): Boolean {
        return textState.isNotEmpty() && descriptionState.isNotEmpty()
    }

    fun deleteItemFromBatteryList(item: BatteryEntity) {
        viewModelScope.launch {
            batteryDao.deleteScheduleDb(item)
            batteryList.remove(item)
        }
    }
}