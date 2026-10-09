# CMPUT 301 : Lab 5 Participation Exercise

## Student Details

- **Full Name:** `Riley Whitford`
- **CCID:** `whitfor1`

## References and Resources

[1] Firebase unresolved issue |  https://stackoverflow.com/questions/70212541/firebase-in-android-firestore-is-unresolved-reference  

[2] Modern UIs with Jetpack Compose: Your Complete Guide to Background Modifiers and Beautiful Visual Effects | D. Raut | https://medium.com/@debasishraut.dev/modern-uis-with-jetpack-compose-your-complete-guide-to-background-modifiers-and-beautiful-visual-d45cd26984c3  

[3] Button background color | https://stackoverflow.com/questions/64376333/background-color-on-button-in-jetpack-compose  

Note that [3] was a suggestion from Lab 3 TA comments.  

[4] Hazel Campbell | Course Q&A Discussion Post | Here's a list of all the things you can do like Text and Button and stuff. Things like Column and LazyColumn are in a different section

https://developer.android.com/reference/kotlin/androidx/compose/material3/AlertDialog.composableLinks to an external site.

https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyColumn.composable

Portions of code were reused and slightly altered from CMPUT301 Lab 03 - rjwonderchild repository submission.  

# LLM Citation

**PROMPT 1 : Agent ChatGPT : October 8, 2026**

Chat, I am currently working on a lab code project. We are using Android Studio, and implementing Firebase to store data on the cloud.  
I am currently trying to rework some old lab code into this project (check for duplicates, maybe implement a dialog box later). I am  
running into an issue when I update a city now, which I think has to do with how the ID is set for Firebase implementation of the cities.  
The update city function is as follows: 
```C 
fun updateCity(oldCity: City, updatedCity: City): Boolean {
        if (doesCityExist(updatedCity, oldCity)) {
            return false
        }

        if (oldCity.name == updatedCity.name) {
            citiesRef.document(oldCity.name).set(updatedCity)
        } else {
            citiesRef.document(oldCity.name).delete()
            citiesRef.document(updatedCity.name).set(updatedCity)
        }

        return true
    }
}
```
My idea is that when I update a city, is to simply remove the old city and just add a new one by copying the contents of the old (its just the city  
name and province for now). But it deletes a city object when I do not want it to, for example:
Edmonton, AB  
Vancouver, BC  
Edmonton -> Vancouver, AB  
  
Vancouver, BC is deleted and replaced. What could be a remedy? This is also the duplicate checking function:  
```C
    private fun doesCityExist(city: City, ignoreCity: City? = null): Boolean {
        val cityTrim = city.name.trim()
        val provinceTrim = city.province.trim()

        return _cities.any { existingCity ->
            existingCity != ignoreCity &&
                    existingCity.name.trim().equals(cityTrim, ignoreCase = true) &&
                    existingCity.province.trim().equals(provinceTrim, ignoreCase = true)
        }
```

**PROMPT 1 ANSWER**  

es. The bug is caused by how you're assigning Firestore document IDs, rather than your doesCityExist() function.
Your duplicate checker considers a city unique based on two properties:
- City name
- Province
However, your Firestore documents are identified only by city name.
1. Why your current implementation fails
Consider your Firestore collection:
cities/
├── Edmonton
│   ├── name: "Edmonton"
│   └── province: "AB"
│
└── Vancouver
    ├── name: "Vancouver"
    └── province: "BC"


When you update Edmonton, AB to Vancouver, AB, this condition evaluates to false:
doesCityExist(updatedCity, oldCity)


That's expected, since Vancouver, AB isn't a duplicate of Vancouver, BC.
However, this code:
citiesRef.document(oldCity.name).delete()
citiesRef.document(updatedCity.name).set(updatedCity)


deletes Edmonton and overwrites the existing Vancouver document, regardless of its province.
2. Recommended fix: Use city and province as document IDs
Since your application defines uniqueness using both city and province, you can build document IDs using both properties.
For example:
cities/
├── edmonton_ab
│   ├── name: "Edmonton"
│   └── province: "AB"
│
├── vancouver_bc
│   ├── name: "Vancouver"
│   └── province: "BC"
│
└── vancouver_ab
    ├── name: "Vancouver"
    └── province: "AB"


First, create a helper function in CityRepository:
private fun cityDocumentId(city: City): String {
    val name = city.name.trim().lowercase()
    val province = city.province.trim().lowercase()

    return "${name}_${province}"
}


This assumes your city/province fields contain ordinary names without /, and that this normalization is sufficient for your assignment.
Now update all three repository operations to use this helper.


## Verbal Collaboration

| Student Name | CCID      |
| ------------ | --------- |
| `student`    | `student` |
| `<Add more>` | `<CCID>`  |
