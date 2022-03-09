package com.oscardnavarro.oscarfinance

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.oscardnavarro.oscarfinance.databinding.ActivityBalanceBinding
import com.oscardnavarro.oscarfinance.entities.Category
import com.oscardnavarro.oscarfinance.entities.Item
import com.oscardnavarro.oscarfinance.entities.Month
import com.oscardnavarro.oscarfinance.services.DBService
typealias categoriesList = MutableMap<Int,Category>
class BalanceActivity : AppCompatActivity() {
    private var dbService: DBService = DBService()

    private lateinit var binding: ActivityBalanceBinding
    private var pieChart: PieChart? = null
    private var widthSreen:Float = 0f
    private lateinit var categoriesSpendind: categoriesList
    private lateinit var months: ArrayList<Month>
    private var newData: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_balance)

        createInitMonths()
        createInitCategoriesSpending()
        setMonths()
        setCellSize()
        setCategoryButtons()

        selectMonth(3)

        pieChart = findViewById<PieChart>(R.id.pcBalance)
        confPieChart()
        spendingItemsTest()
    }


    override fun onStart() {
        super.onStart()
        var money: Float = 0f
        var categoryIndex: Int = 0
        var subCategoryIndex: Int = 0
        // var isAEntry: Boolean = false

        var bundle: Bundle? = intent.extras
        if (bundle!=null) {
            newData=true
            money = bundle?.getFloat("money")
            categoryIndex = bundle?.getInt(("categoryIndex"))
            subCategoryIndex = bundle?.getInt("subCategoryIndex")
        }

        if(newData) {
            val subCategoryName = categoriesSpendind[categoryIndex]!!.getSubcategories().get(subCategoryIndex)
            categoriesSpendind[categoryIndex]!!.addNewItem(Item(subCategoryName, money))
        }
        newData=false
        loadPieChartData(categoriesSpendind)
    }
    private fun createInitMonths() {
        // ESTO SE DEBE OBTENER POR BASE DE DATOS (TAMBIEN DEBERIAMOS PODER ELEGIR LOS ANIOS)
        months = arrayListOf()
        for(i in 1..12) {
            months.add(Month(i, 30-i))
        }
    }
    private fun createInitCategoriesSpending() {
        // ESTO SE DEBE OBTENER POR BASE DE DATOS
        categoriesSpendind = mutableMapOf()
        categoriesSpendind[0] = Category(0, false,"Otros", Color.rgb(255, 24, 0),"ic_other")
        categoriesSpendind[0]!!.setSubcategories(arrayListOf("Otro"))
        categoriesSpendind[1] = Category(1,false,"Comida", Color.rgb(0, 235, 52), "ic_food")
        categoriesSpendind[1]!!.setSubcategories(arrayListOf("Desayuno","Almuerzo","Cena"))

        categoriesSpendind[2] = Category(2, false,"Transporte", Color.rgb(21, 69, 229),"ic_transport")
        categoriesSpendind[2]!!.setSubcategories(arrayListOf("Gasolina","Taxi","Propina"))
        categoriesSpendind[3] = Category(3, false,"Supermercado", Color.rgb(255, 255, 0),"ic_shop")
        categoriesSpendind[3]!!.setSubcategories(arrayListOf("Plaza vea","Bodega local"))

        categoriesSpendind[4] = Category(4, false,"Entretenimiento", Color.rgb(192, 251, 0), "ic_entretaiment")
        categoriesSpendind[4]!!.setSubcategories(arrayListOf("Cine","Paseo","Viaje"))
        categoriesSpendind[5] = Category(5, false,"Salud", Color.rgb(233, 0, 182),"ic_health")
        categoriesSpendind[5]!!.setSubcategories(arrayListOf("Medicina","Gym","Proteina"))

        categoriesSpendind[6] = Category(6, false,"Personal", Color.rgb(1, 213, 223), "ic_personal")
        categoriesSpendind[6]!!.setSubcategories(arrayListOf("Corte de pelo","Ropa"))
        categoriesSpendind[7] = Category(7, false,"Educación", Color.rgb(151, 10, 228),"ic_education")
        categoriesSpendind[7]!!.setSubcategories(arrayListOf("Útiles escolares","Univesidad","Curso online"))
    }
    private fun setMonths() {
        var lyMonths = findViewById<LinearLayout>(R.id.lyMonths)
        for(m in months) {
            val child = createMonth(m)
            lyMonths.addView(child)
        }
    }
    private fun createMonth(month: Month): TextView {
        var v: TextView = TextView(this)
        v.id = 1000 + month.getIndex()*100
        v.setPadding(30,8,30,8)
        v.text = month.getName()
        v.setTextColor(Color.rgb(255,255,255))
        v.textSize = 18f
        v.textAlignment = View.TEXT_ALIGNMENT_CENTER
        v.setBackgroundColor(ContextCompat.getColor(this,
            resources.getIdentifier("black", "color", packageName)
        ))

        v.setOnClickListener {
            selectMonth(month.getIndex())
        }
        return v
    }
    private fun setCellSize() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        widthSreen = width / resources.displayMetrics.density
    }
    private fun setCategoryButtons() {
        var lyCategories = findViewById<GridLayout>(R.id.lyCategories)
        for(i in categoriesSpendind.size-1 downTo 0) {
            val child = createCategoryButtom(i,4, categoriesSpendind.get(i)!!)
            lyCategories.addView(child)
        }
    }
    private fun createCategoryButtom(index: Int, numPerRow: Int, category: Category): LinearLayout {
        val padd: Int = 60
        val ly: LinearLayout = LinearLayout(this)
        val lado = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (widthSreen)/numPerRow,
            resources.displayMetrics
        ).toInt()
        ly.layoutParams = TableRow.LayoutParams(lado, lado)
        ly.orientation = LinearLayout.VERTICAL
        ly.gravity = Gravity.CENTER
        val img: ImageView = createImageViewCategory(lado-2*padd, category.getIcon())
        ly.addView(img)

        val vt: TextView = TextView(this)
        vt.text = category.getName()
        vt.textSize = 14f
        vt.setTextColor(category.getColor())
        vt.textAlignment = View.TEXT_ALIGNMENT_CENTER
        ly.addView(vt)

        return ly
    }
    private fun createImageViewCategory(lado: Int, icon: String): ImageView {
        var iv: ImageView = ImageView(this)
        iv.layoutParams = TableRow.LayoutParams(lado, lado)
        iv.setBackgroundColor(ContextCompat.getColor(this,
            resources.getIdentifier("transparent", "color", packageName)
        ))
        iv.setImageResource(resources.getIdentifier(icon, "drawable", packageName))
        return iv
    }
    private fun selectMonth(index: Int) {
        var tvMonth: TextView
        for(i in 0 until months.size) {
            tvMonth = findViewById(1000+months[i].getIndex()*100)
            tvMonth.setBackgroundColor(Color.rgb(0,0,0))
        }
        val month: Month = months[index-1]
        tvMonth = findViewById(1000+month.getIndex()*100)
        tvMonth.setBackgroundColor(Color.rgb(75,75,75))

        setDays(index)
        selectDay(1,index)
    }
    private fun setDays(index: Int) {
        var lyDays = findViewById<LinearLayout>(R.id.lyDays)
        lyDays.removeAllViews()
        for(d in 1..months[index-1].getDays()) {
            val child = createDay(d, index)
            lyDays.addView(child)
        }
    }
    private fun createDay(day: Int, month: Int): TextView {
        var v: TextView = TextView(this)
        v.id = 1000 + month*100 + day
        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            34f,
            resources.displayMetrics
        ).toInt()
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            34f,
            resources.displayMetrics
        ).toInt()
        v.layoutParams = TableRow.LayoutParams(width, height)
        v.setPadding(8,8,8,8)
        v.text = day.toString()
        v.setTextColor(Color.rgb(255,255,255))
        v.textSize = 18f
        v.textAlignment = View.TEXT_ALIGNMENT_CENTER
        v.setBackgroundColor(ContextCompat.getColor(this,
            resources.getIdentifier("black", "color", packageName)
        ))
        v.setOnClickListener {
            selectDay(day, month)
        }
        return v
    }
    private fun selectDay(day: Int, month: Int) {
        var tvDay: TextView
        for(i in 1..months[month-1].getDays()) {
            tvDay = findViewById(1000+months[month-1].getIndex()*100 + i)
            tvDay.setBackgroundColor(Color.rgb(0,0,0))
        }
        //val month: Month = months[month-1]
        tvDay = findViewById(1000+months[month-1].getIndex()*100 + day)
        tvDay.setBackgroundColor(Color.rgb(75,75,75))
    }
    private fun confPieChart() {
        pieChart!!.apply {
            isDrawHoleEnabled = true
            setUsePercentValues(false)
            setExtraOffsets(5f,10f,5f,5f)
            setDrawEntryLabels(false)
            setEntryLabelTextSize(16f)
            setEntryLabelColor(Color.BLACK)
            centerText = "Gastos del día: 23.75 S/."
            setCenterTextSize(18f)
            setCenterTextColor(Color.WHITE)
            description.isEnabled = false
            setHoleColor(Color.TRANSPARENT)
            transparentCircleRadius = 0f
            holeRadius = 45f
        }
    }
    private fun spendingItemsTest() {
        for (i in 0 until categoriesSpendind.size)
            categoriesSpendind[i]!!.addNewItem(Item(categoriesSpendind[i]!!.getSubcategories()[0], 10f))
    }
    private fun loadPieChartData(categories: MutableMap<Int, Category>) {
        var entries: ArrayList<PieEntry> = ArrayList<PieEntry>()
        var colors: ArrayList<Int> = arrayListOf()
        for (i in 0 until categories.size) {
            val value: Float = categories[i]!!.calcBalance()
            if(value > 0) {
                entries.add(PieEntry(value, categories[i]!!.getName()))
                colors.add(categories[i]!!.getColor())
            }
        }
        colors.add(ColorTemplate.getHoloBlue())

        var dataSet = PieDataSet(entries, "Texto de prueba")
        dataSet.colors = colors
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 1f

        var data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.WHITE)

        pieChart!!.data = data
        pieChart!!.highlightValues(null)
        pieChart!!.invalidate()
    }
    fun registerItem(v: View) {
        val tag: String = v.tag.toString()
        val isAEntry: Boolean = tag.last() == '2' // true: Ingreso

        var inte: Intent = Intent(this, RegisterActivity::class.java)
        inte.putExtra("isAEntry", isAEntry)
        startActivity(inte)
    }
}