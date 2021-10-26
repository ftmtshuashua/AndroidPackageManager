/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/26 15:42
 * </pre>
 */


data class Test(var index: Int = 1) {
    init {
        index = 2
    }
}

fun main() {
    val starApp = Test(123)
    println(starApp)
}