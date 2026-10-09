package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {

    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")

    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    // Code implementation from Lab 5 walkthrough.
    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()

            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }

    val cities: List<City>
        get() = _cities

    // Function is resued from lab 3. Checks to see if a city exists already in the list.
    private fun doesCityExist(city: City, ignoreCity: City? = null): Boolean {
        val cityTrim = city.name.trim()
        val provinceTrim = city.province.trim()

        return _cities.any { existingCity ->
            existingCity != ignoreCity &&
                    existingCity.name.trim().equals(cityTrim, ignoreCase = true) &&
                    existingCity.province.trim().equals(provinceTrim, ignoreCase = true)
        }
    }

    // Suggest from AI to set a different document ID from lab, prevents deleting multiple
    // cities from the database that have the same name, but may be in a different province.
    private fun cityId(city: City): String {
        val name = city.name.trim().lowercase()
        val province = city.province.trim().lowercase()

        return "${name}_${province}"
    }

    // Updated addCity function as type boolean. Clean inputs from user for duplication
    // check. Then new city is added by the new small document id function.
    fun addCity(city: City): Boolean {
        val cityTrim = city.name.trim()
        val provinceTrim = city.province.trim()

        if (cityTrim.isBlank() ||
            provinceTrim.isBlank() ||
            doesCityExist(city)) {
            return false
        }

        val newCity = city.copy(
            name = cityTrim,
            province = provinceTrim
        )

        citiesRef.document(cityId(newCity)).set(newCity)

        return true
    }

    // Remove city from database, simply uses .delete()
    fun removeCity(city: City) {
        citiesRef.document(cityId(city)).delete()
    }

    // Updating city in database, again of type boolean for checks.
    // Trim input, then check to see if fields are blank. Then check to see
    // if city exists already. If it does, we do nothing, but just set it back to itself.
    // If the updates change something, we 'delete' the old city and replace it with the new one.
    fun updateCity(oldCity: City, updatedCity: City): Boolean {
        val cityTrim = updatedCity.name.trim()
        val provinceTrim = updatedCity.province.trim()

        if (cityTrim.isBlank() || provinceTrim.isBlank()) {
            return false
        }

        val newCity = updatedCity.copy(
            name = cityTrim,
            province = provinceTrim
        )

        if (doesCityExist(newCity, oldCity)) {
            return false
        }

        if (cityId(oldCity) == cityId(newCity)) {
            citiesRef.document(cityId(oldCity)).set(newCity)
        } else {
            citiesRef.document(cityId(oldCity)).delete()
            citiesRef.document(cityId(newCity)).set(newCity)
        }
        return true
    }
}