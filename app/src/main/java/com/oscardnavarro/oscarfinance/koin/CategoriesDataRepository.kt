package com.oscardnavarro.oscarfinance.koin

interface CategoriesDataRepository {
    fun giveHello(): String
}

internal class CategoriesDataRepositoryImpl : CategoriesDataRepository {
    override fun giveHello() = "Hello Koin"
}