package com.oscardnavarro.oscarfinance.entities

class Month(private var index: Int, private var days: Int = 30) {
    private var name: String = getName()
    public fun getName(): String {
        when(index) {
            1 -> return "Enero"
            2 -> return "Febrero"
            3 -> return "Marzo"
            4 -> return "Abril"
            5 -> return "Mayo"
            6 -> return "Junio"
            7 -> return "Julio"
            8 -> return "Agosto"
            9 -> return "Septiembre"
            10 -> return "Octubre"
            11 -> return "Noviembre"
            12 -> return "Diciembre"
        }
        return ""
    }
    public fun getIndex(): Int {
        return this.index
    }
    public fun getDays(): Int {
        return this.days
    }
}