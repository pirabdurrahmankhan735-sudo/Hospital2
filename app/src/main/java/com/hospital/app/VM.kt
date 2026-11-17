package com.hospital.app
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VM(app: Application) : AndroidViewModel(app) {
    private val dao = Db.get(app).dao()
    val patients = dao.patients().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val doctors = dao.doctors().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val appts = dao.appointments().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun add(p: Patient) = viewModelScope.launch { dao.add(p) }
    fun add(d: Doctor) = viewModelScope.launch { dao.add(d) }
    fun add(a: Appointment) = viewModelScope.launch { dao.add(a) }
    fun del(p: Patient) = viewModelScope.launch { dao.del(p) }
    fun del(d: Doctor) = viewModelScope.launch { dao.del(d) }
    fun del(a: Appointment) = viewModelScope.launch { dao.del(a) }
}
