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
    val isFav: Boolean
)
