package mcpserver

import io.ktor.utils.io.streams.asInput
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import mcpserver.network.HowToCookHttpClient
import mcpserver.network.data.RecipeItem

fun main() {
    val mcpServer = HowToCookMcpServer()
    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered()
    )

    runBlocking {
        val realServer = mcpServer.createServer()
        realServer.connect(transport)
        val done = Job()
        realServer.onCloseCallback = {
            done.complete()
        }
        done.join()
    }
}

class HowToCookMcpServer {
    private val requestClient = HowToCookHttpClient.create()

    suspend fun createServer(): Server {
        val recipes = requestClient.request()
        val categories = getCategory(recipes)

        val server = Server(
            serverInfo = Implementation(
                name = "HowToCookMcp",
                version = "1.0.0"
            ), options = ServerOptions(
                capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(true))
            )
        )

        server.addCategoryTool(recipes, categories)

        return server
    }

    private fun getCategory(recipes: List<RecipeItem>): Set<String> {
        return mutableSetOf<String>().apply {
            recipes.forEach {
                this.add(it.category)
            }
        }
    }
}

fun Server.addCategoryTool(recipes: List<RecipeItem>, categories: Set<String>) {
    this.addTool(
        name = "getRecipesByCategory",
        description = "Search recipes by category. Optional categories include ${
            categories.toMutableSet().joinToString()
        }",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                putJsonObject("category") {
                    put("type", "String")
                }
            },
            required = listOf("category")
        ),
    ) { request ->
        val category = request.arguments["category"]?.jsonPrimitive?.content
        if (category.isNullOrEmpty()) {
            return@addTool CallToolResult(
                content = listOf(TextContent("The 'category' parameters is required."))
            )
        }
        val result = recipes.filter { it.category == category }.map {
            TextContent(
                text = it.toString()
            )
        }
        CallToolResult(content = result)
    }
}
