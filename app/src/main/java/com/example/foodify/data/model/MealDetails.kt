import com.example.foodify.data.model.IngredientItem
import com.example.foodify.data.model.MealPreview

// MealDetails (For Details Screen)
data class MealDetails(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: List<String>,
    val thumbnail: String,
    val youtube: String?,
    val source: String?,
    val ingredients: List<IngredientItem>,
    var isFav: Boolean,
    var userId: String?,
    var mealPlan:String



){
    companion object {
        val EMPTY = MealDetails(
            id = "",
            name = "",
            category = "",
            area = "",
            instructions = emptyList(),
            thumbnail = "",
            youtube = "",
            source = "",
            ingredients = emptyList(),
            isFav = false,
            userId = null,
            mealPlan = ""

        )
    }
}
