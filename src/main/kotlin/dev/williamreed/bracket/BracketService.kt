package dev.williamreed.bracket

/**
 * Handle bracket operations
 */
interface BracketService {
    fun saveBracket(bracket: Bracket): String
    fun loadBracket(id: String): Bracket?
}

/**
 * In memory store for brackets
 */
object LocalBracketService : BracketService {
    private val store: MutableMap<String, Bracket> = mutableMapOf()
    private val idGenerator: IdGenerator = LocalIdGenerator

    override fun saveBracket(bracket: Bracket): String {
        val id = idGenerator.getNewId()
        store[id] = bracket
        return id
    }

    override fun loadBracket(id: String) = store[id]
}
