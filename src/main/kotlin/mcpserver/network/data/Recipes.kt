package mcpserver.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeItem(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("source_path")
    val sourcePath: String? = null,
    val category: String,
    val difficulty: Int,
    val tags: List<String>,
    val servings: Int,
    val ingredients: List<IngredientItem>,
    val steps: List<CookStepItem>,
    @SerialName("additional_notes")
    val additionalNote: List<String>
) {
    override fun toString(): String {
        return mapOf(
            "id" to id,
            "name" to name,
            "category" to category,
            "description" to description,
            "ingredients" to ingredients,
            "steps" to steps
        ).toString()
    }
}

@Serializable
data class IngredientItem(
    val name: String,
    @SerialName("text_quantity")
    val quantity: String
)

@Serializable
data class CookStepItem(
    val step: Int,
    val description: String
)
