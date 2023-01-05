package com.example.afreecasampleapp.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.afreecasampleapp.data.Broad
import com.example.afreecasampleapp.data.BroadCategory
import com.example.afreecasampleapp.data.repository.AfreecaTvRepository
import com.example.afreecasampleapp.utility.event.MutableEventFlow
import com.example.afreecasampleapp.utility.event.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AfreecaTvViewModel @Inject constructor(
    private val repository: AfreecaTvRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _broadData = MutableEventFlow<Event>()
    val broadData = _broadData.asEventFlow()

    var categoryInfo = ArrayList<BroadCategory>()
    var currentCategoryId : Int? = null
    private var currentBroadLists: Flow<PagingData<Broad>>? = null

    fun getCategories(){
        viewModelScope.launch {
            repository.getCategories().collectLatest {
                categoryInfo = it as ArrayList<BroadCategory>
                _broadData.emit(Event.BroadCategories(it))
            }
        }
    }

    fun getBroadList(categoryId: Int): Flow<PagingData<Broad>> {
        currentCategoryId = categoryId
        val newResult: Flow<PagingData<Broad>> =
            repository.getBroadList(categoryId).cachedIn(viewModelScope)
        currentBroadLists = newResult
        return newResult
    }


    sealed class Event {
        data class BroadCategories(val mountains : List<BroadCategory>) : Event()
        data class BroadLists(val mountains : ArrayList<Broad>) : Event()
    }

}