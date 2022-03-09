package com.oscardnavarro.oscarfinance.entities

class Category(
    private var index: Int,
    private var isAEntry: Boolean,
    private var name: String,
    private var color: Int,
    private var icon: String = "ic_other"
) {
    private lateinit var subCategories: ArrayList<String>
    private var listItems: ArrayList<Item>? = null

    private var totalBalance: Float = 0f

    public fun getName(): String {
        return name
    }
    public fun setSubcategories(subCategories: ArrayList<String>) {
        this.subCategories = subCategories
    }
    public fun getSubcategories(): ArrayList<String> {
        return this.subCategories
    }
    public fun addNewItem(item: Item){
        if (listItems==null) listItems = ArrayList()
        listItems!!.add(item)
    }
    public fun clearItems() {
        listItems = null
    }
    public fun calcBalance(): Float{
        var balance: Float = 0f
        if(listItems!=null)
            for (i in 0 until listItems!!.size)
                balance += listItems!![i].value
        return balance
    }
    public fun getIcon(): String {
        return this.icon
    }
    public fun getColor(): Int {
        return this.color
    }
    public fun getIndex(): Int {
        return this.index
    }
    public fun getIsAEntry(): Boolean {
        return this.isAEntry
    }
}