package com.oscardnavarro.oscarfinance.services

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.oscardnavarro.oscarfinance.entities.Category

typealias categoriesList = ArrayList<Category>
public class DBService {
    private var TAG: String = "OscarFinance"
    private var db = FirebaseFirestore.getInstance()
    private var nCategories: Int = 0
    public fun addCategory(category: Category) {
        // Create a new user with a first and last name
        val user: MutableMap<String, String> = HashMap()
        user["index"] = category.getIndex().toString()
        user["name"] = category.getName()
        user["color"] = category.getColor().toString()
        user["icon"] = category.getIcon()
        val isAEntry: Boolean = category.getIsAEntry()
        val nameCategories: String = "${if (isAEntry) "categoriesEntry" else "categoriesSpending"}"

        // Add a new document with a generated ID
        db.collection(nameCategories)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }
    public fun getCategories(isAEntry: Boolean): categoriesList? {
        val nameCategories: String = "${if (isAEntry) "categoriesEntry" else "categoriesSpending"}"
        var categories: categoriesList? = null
        val dbDef = db.collection(nameCategories)
        dbDef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result!=null) {
                    categories = arrayListOf()
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " =-/=> " + document.data)
                        val data = document.data
                        categories!!.add(Category(
                            data["index"].toString().toInt(),
                            isAEntry,
                            data["name"].toString(),
                            data["color"].toString().toInt(),
                            data["icon"].toString()
                        ))
                        categories!!.sortBy {
                            it.getIndex()
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
        dbDef.orderBy("index")
//        while (categories==null)
//            Log.d(TAG, "x")
        return categories
    }
}