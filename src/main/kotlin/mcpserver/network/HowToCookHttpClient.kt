package mcpserver.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import mcpserver.network.data.RecipeItem

class HowToCookHttpClient private constructor(private val base: String) {
    private val httpClient = HttpClient {
        defaultRequest {
            url(base)
            headers {
                append("Accept", "application/json")
            }
            contentType(ContentType.Application.Json)
        }
        // Install content negotiation plugin for JSON serialization/deserialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun request(): List<RecipeItem> {
        return httpClient.get {}.body<List<RecipeItem>>()
    }

    companion object {
        const val RECIPES_URL = "https://mp-bc8d1f0a-3356-4a4e-8592-f73a3371baa2.cdn.bspapp.com/all_recipes.json"

        fun create(base: String = RECIPES_URL): HowToCookHttpClient {
            return HowToCookHttpClient(base)
        }
    }
}