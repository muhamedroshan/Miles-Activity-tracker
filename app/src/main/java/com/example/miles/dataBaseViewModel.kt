package com.example.miles

import androidx.lifecycle.*
import com.example.miles.data.Item
import com.example.miles.data.ItemDao
import kotlinx.coroutines.launch


class dataBaseViewModel( private val dataBaseDao : ItemDao) : ViewModel() {

    val allItems : LiveData<List<Item>> = dataBaseDao.getItems().asLiveData()

    fun insertItem(timestamp: Long, avgSpeed: Int,distance: Int, time: Int, activity: String, cal: Int ) {
        viewModelScope.launch {
            val datadb = Item (
                null,
                timestamp,
                avgSpeed,
                distance,
                time,
                activity,
                cal
                    )
            dataBaseDao.insert(datadb)
        }
    }

    fun deleteItem( database: Item){
        viewModelScope.launch {
            dataBaseDao.delete(database)
        }
    }

    fun findItemById ( id : Int): LiveData<Item> {
        return dataBaseDao.getItem(id).asLiveData()
    }

    fun undoDelete ( item: Item){
        viewModelScope.launch {
            dataBaseDao.insert(item)
        }
    }
}

class dataBaseViewModelFactory( private val dataBaseDao: ItemDao) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create (modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(dataBaseViewModel::class.java)){
            return dataBaseViewModel(dataBaseDao) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel As Class")
    }

}