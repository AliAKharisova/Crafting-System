
enum class Rarity {
    COMMON, RARE, LEGENDARY
}

sealed class Item

data class Ingredient(
    val name: String,
    val rarity: Rarity
) : Item()

data class Equipment(
    val name: String,
    val durability: Int,
    val attackOrDefense: Int
) : Item()

data class Potion(
    val name: String,
    val effect: String
) : Item()

data class Recipe(
    val ingredients: Map<Ingredient, Int>,  // ингредиент -> количество
    val result: Item
)

class Inventory {
    private val items = mutableListOf<Item>()

    operator fun plusAssign(item: Item) {
        items.add(item)
        println("Добавлено в инвентарь: " + item)
    }

    operator fun minusAssign(item: Item) {
        if (items.remove(item)) {
            println("Удалено из инвентаря: " + item)
        } else {
            println("Предмет не найден в инвентаре: " + item)
        }
    }

    fun getAll(): List<Item> = items

    override fun toString(): String {
        return "Inventory(" + items.size + " предметов)"
    }
}

class CraftingTable {

    fun craft(ingredients: List<Item>, recipe: Recipe): Item? {
        // 1. Проверяем, что все предметы — ингредиенты (smart cast)
        for (item in ingredients) {
            if (item !is Ingredient) {
                println("Нельзя крафтить: среди ингредиентов есть не-ингредиент (" + item + ")")
                return null
            }
        }

        val counts = mutableMapOf<Ingredient, Int>()
        for (item in ingredients) {
            val ing = item as Ingredient
            counts[ing] = (counts[ing] ?: 0) + 1
        }

        // 3. Сравниваем с рецептом
        if (counts == recipe.ingredients) {
            println("Крафт успешен! Получено: " + recipe.result)
            return recipe.result
        } else {
            println("Крафт не удался: ингредиенты не совпадают с рецептом.")
            return null
        }
    }
}

fun main() {
    val fireRoot = Ingredient("Огненный корень", Rarity.COMMON)
    val waterLily = Ingredient("Водяная лилия", Rarity.COMMON)
    val dragonScale = Ingredient("Чешуя дракона", Rarity.LEGENDARY)

    val sword = Equipment("Стальной меч", durability = 100, attackOrDefense = 25)

    val healingPotion = Potion("Зелье лечения", "Восстанавливает 50 HP")

    val healingRecipe = Recipe(
        ingredients = mapOf(
            fireRoot to 2,
            waterLily to 1
        ),
        result = healingPotion
    )

    val inventory = Inventory()
    val table = CraftingTable()

    inventory += fireRoot
    inventory += fireRoot
    inventory += waterLily
    inventory += sword

    println(" Содержимое инвентаря ")
    println(inventory)

    inventory -= waterLily

    println(" Попытка крафта (правильные ингредиенты) ")
    val result1 = table.craft(listOf(fireRoot, fireRoot, waterLily), healingRecipe)
    println("Результат 1: " + result1)

    println(" Попытка крафта (неправильные ингредиенты) ")
    val result2 = table.craft(listOf(fireRoot, waterLily), healingRecipe)
    println("Результат 2: " + result2)

    println(" Попытка крафта с экипировкой (должен отменить) ")
    val result3 = table.craft(listOf(fireRoot, fireRoot, sword), healingRecipe)
    println("Результат 3: " + result3)
}