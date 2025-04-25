import kotlinx.coroutines.runBlocking
import mcpserver.network.HowToCookHttpClient


fun main() {
    runBlocking {
        val response = HowToCookHttpClient.client.request()
        println(response)
    }
}