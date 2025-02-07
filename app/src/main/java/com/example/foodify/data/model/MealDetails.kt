import com.example.foodify.data.model.MealPreview

// MealDetails (For Details Screen)
data class MealDetails(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnail: String,
    val youtube: String?,
    val source: String?,
    val ingredients: List<String>,
    val measures: List<String>,
    var isFav: Boolean,
    var userId: String?


){
    companion object {
        val EMPTY = MealDetails(
            id = "",
            name = "",
            category = "",
            area = "",
            instructions = "",
            thumbnail = "",
            youtube = "",
            source = "",
            ingredients = emptyList(),
            measures = emptyList(),
            isFav = false,
            userId = ""
        )
    }
}
