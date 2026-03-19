package com.calaratjada.insider.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.calaratjada.insider.data.service.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    private val adManager: AdManager
) : ViewModel() {
    val isAdFree = adManager.isAdFree
    val isRewardedAdReady get() = adManager.isRewardedAdReady
}
