package edu.nd.pmcburne.hwapp.one.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.local.BasketballDatabase
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.remote.RetrofitClient
import edu.nd.pmcburne.hwapp.one.data.repository.BasketballRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BasketballRepository(
        apiService = RetrofitClient.apiService,
        gameDao = BasketballDatabase.getDatabase(application).gameDao(),
        context = application
    )

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedGender = MutableStateFlow("men")
    val selectedGender: StateFlow<String> = _selectedGender.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    val games: StateFlow<List<GameEntity>> = combine(_selectedDate, _selectedGender) { date, gender ->
        Pair(date, gender)
    }.flatMapLatest { (date, gender) ->
        repository.getCachedGames(gender, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { refresh() }

    fun setDate(date: LocalDate) { _selectedDate.value = date; refresh() }
    fun setGender(gender: String) { _selectedGender.value = gender; refresh() }
    fun dismissError() { _error.value = null }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            if (repository.isNetworkAvailable()) {
                _isOffline.value = false
                try {
                    repository.refreshScoreboard(_selectedGender.value, _selectedDate.value)
                } catch (e: Exception) {
                    _error.value = "Failed to load: ${e.localizedMessage}"
                }
            } else {
                _isOffline.value = true
                _error.value = "Offline — showing cached data."
            }
            _isLoading.value = false
        }
    }
}