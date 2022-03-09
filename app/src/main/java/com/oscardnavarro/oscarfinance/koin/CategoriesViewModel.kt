package com.oscardnavarro.oscarfinance.koin

import androidx.lifecycle.ViewModel

class CategoriesViewModel(val repo: CategoriesDataRepository) : ViewModel() {

    fun sayHello() = "${repo.giveHello()} from CategoriesViewModel"
}