import kotlinx.coroutines.runBlocking
import mcpserver.network.HowToCookHttpClient
import mcpserver.network.data.CookStepItem
import mcpserver.network.data.IngredientItem
import mcpserver.network.data.RecipeItem


//fun main() {
//    testRequest()
//    testGenerateJsonString()
//}

private fun testRequest() {
    runBlocking {
        val client = HowToCookHttpClient.create()
        val response = client.request()
        println(response)
    }
}

private fun testGenerateJsonString() {
    val mockItem = RecipeItem(
        id = "111",
        name = "111",
        description = "cook",
        category = "cook",
        difficulty = 1,
        tags = listOf("SeeFood"),
        servings = 1,
        ingredients = listOf(
            IngredientItem(
                name = "dish",
                quantity = "x5",
            )
        ),
        steps = listOf(
            CookStepItem(
                step = 1,
                description = "1st step"
            )
        ),
        additionalNote = listOf("Note")

    )
    val mockList = listOf(
        mockItem
    )

    println(mockItem.toString())
    println(mockList.toString())
}