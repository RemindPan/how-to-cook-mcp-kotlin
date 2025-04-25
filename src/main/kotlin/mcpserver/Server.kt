package mcpserver

import io.ktor.utils.io.streams.asInput
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import mcpserver.network.HowToCookHttpClient

fun main() {
    val server: Server = createServer()
// Create a transport using standard IO for server communication
    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered()
    )

    runBlocking {
        server.connect(transport)
        val done = Job()
        server.onCloseCallback = {
            done.complete()
        }
        done.join()
    }
}


private fun createServer(): Server {
    return Server(
        Implementation(
            "HowToCookMcp",
            "1.0.0"
        ), ServerOptions(
            capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(true))
        )
    ).apply {
        addTool(
            name = "requestRecipes",
            description = "Get the how to cook guidance",
        ) {
            val response = HowToCookHttpClient.client.request()
            CallToolResult(content = response.map {
                TextContent(it.description)
            })
        }
    }

}

