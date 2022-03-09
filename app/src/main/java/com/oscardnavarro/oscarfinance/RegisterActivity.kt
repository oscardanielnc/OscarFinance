package com.oscardnavarro.oscarfinance

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.oscardnavarro.oscarfinance.databinding.ActivityRegisterBinding
import com.oscardnavarro.oscarfinance.entities.Category
import com.oscardnavarro.oscarfinance.entities.Month

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var widthSreen:Float = 0f
    private var isAEntry: Boolean = false
    private lateinit var categoriesSpendind: MutableMap<Int, Category>
    private var categoryIndex: Int = 0
    private var subCategoryIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_register)

        var bundle: Bundle? = intent.extras
        if (bundle==null)
            isAEntry = false
        else isAEntry = bundle?.getBoolean("isAEntry")

        createInitCategoriesSpending()
        setCellSize()
        setCategoryButtons()
        selectCategory(1)
        changeStyleIsAEntry()
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
            val child = createCategoryButtom(i,3, categoriesSpendind[i]!!)
            lyCategories.addView(child)
        }
    }
    private fun createCategoryButtom(index: Int, numPerRow: Int, category: Category): LinearLayout {
        val padd: Int = 60
        val ly: LinearLayout = LinearLayout(this)
        ly.id = 3000 + index
        val lado = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (widthSreen - 30)/numPerRow,
            resources.displayMetrics
        ).toInt()
        ly.layoutParams = TableRow.LayoutParams(lado, lado)
        ly.orientation = LinearLayout.VERTICAL
        ly.gravity = Gravity.CENTER
        val img: ImageView = createImageViewCategory(lado-2*padd, category.getIcon())
        ly.addView(img)
        ly.setBackgroundColor(Color.rgb(0,0,0))

        val vt: TextView = TextView(this)
        vt.text = category.getName()
        vt.textSize = 14f
        vt.setTextColor(category.getColor())
        vt.textAlignment = View.TEXT_ALIGNMENT_CENTER
        ly.addView(vt)
        ly.setOnClickListener {
            selectCategory(index)
        }

        return ly
    }
    private fun createImageViewCategory(lado: Int, icon: String): ImageView {
        var iv: ImageView = ImageView(this)
        iv.layoutParams = TableRow.LayoutParams(lado, lado)
        iv.setBackgroundColor(
            ContextCompat.getColor(this,
            resources.getIdentifier("transparent", "color", packageName)
        ))
        iv.setImageResource(resources.getIdentifier(icon, "drawable", packageName))
        return iv
    }
    private fun selectCategory(index: Int) {
        categoryIndex = index
        var lyCategory: LinearLayout
        for(i in 0 until categoriesSpendind.size) {
            lyCategory = findViewById(3000+i)
            lyCategory.setBackgroundColor(Color.rgb(0,0,0))
        }
        lyCategory = findViewById(3000+index)
        lyCategory.setBackgroundColor(Color.rgb(75,75,75))

        setSubCategories(index)
        changeIconSelected(index)
    }
    private fun setSubCategories(index: Int) {
        var cgSubCategories = findViewById<ChipGroup>(R.id.cgSubCategories)
        var subCategories = categoriesSpendind[index]!!.getSubcategories()
        cgSubCategories.removeAllViews()
        for (i in 0 until subCategories.size) {
            val chipNew = Chip(this)
            chipNew.text = subCategories[i]
            chipNew.isChipIconVisible = true
            chipNew.isCheckable = true
            chipNew.setOnClickListener {
                cgSubCategories.removeAllViews()
                cgSubCategories.addView(chipNew)
                chipNew.isCheckable = false
                chipNew.textSize = 18f
                subCategoryIndex = i
            }
            cgSubCategories.addView(chipNew)
        }
    }
    private fun changeIconSelected(index: Int) {
        var icon: ImageView = findViewById(R.id.ivIcon)
        icon.setImageResource(resources.getIdentifier(
            categoriesSpendind[index]!!.getIcon(), "drawable", packageName
        ))
    }
    private fun changeStyleIsAEntry() {
        var etMoney = findViewById<EditText>(R.id.etMoney)
        var tvMoney = findViewById<TextView>(R.id.tvMoney)
        var tvTitle1 = findViewById<TextView>(R.id.tvTitle1)
        var tvTitle2 = findViewById<TextView>(R.id.tvTitle2)
        if(isAEntry) {
            etMoney.setBackgroundColor(ContextCompat.getColor(this,
                    resources.getIdentifier("green", "color", packageName)))
            tvMoney.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier("green", "color", packageName)))
            tvTitle1.text = getString(R.string.store)
            tvTitle2.text = getString(R.string.source)
        } else {
            etMoney.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier("red", "color", packageName)))
            tvMoney.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier("red", "color", packageName)))
            tvTitle1.text = getString(R.string.subcategory)
            tvTitle2.text = getString(R.string.category)
        }
    }
    fun returnBalanceWiew(v: View) {
        val tag: String = v.tag.toString()
        val isBtAdd: Boolean = tag == "btAdd"
        var inte: Intent = Intent(this, BalanceActivity::class.java)

        if(isBtAdd) {
            val money: Float = findViewById<EditText>(R.id.etMoney).text.toString().toFloat()
            inte.putExtra("money", money)
            inte.putExtra("categoryIndex", categoryIndex)
            inte.putExtra("subCategoryIndex", subCategoryIndex)
        }
        startActivity(inte)
    }
}