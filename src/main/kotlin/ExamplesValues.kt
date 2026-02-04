/**
 * KOTLIN VALUE CLASSES - EXEMPLOS PRÁTICOS
 * 
 * Value classes são uma forma eficiente de criar wrappers de tipos
 * sem overhead de performance em runtime (inline classes).
 */

// ============================================
// 1. EXEMPLO BÁSICO - ID do Usuário
// ============================================

@JvmInline
value class UserId(val value: Long)

@JvmInline
value class Email(val value: String)

fun getUserInfo(userId: UserId, email: Email) {
    println("User ID: ${userId.value}")
    println("Email: ${email.value}")
}

// Uso:
fun exemploBasico() {
    val userId = UserId(12345L)
    val email = Email("user@example.com")
    getUserInfo(userId, email)
    
    // Evita erros de tipo: não pode passar Long onde espera UserId
    // getUserInfo(12345L, "email") // ❌ Erro de compilação
}

// ============================================
// 2. TIPOS DE MOEDAS
// ============================================

@JvmInline
value class USD(val amount: Double) {
    fun format(): String = "$%.2f".format(amount)
}

@JvmInline
value class EUR(val amount: Double) {
    fun format(): String = "€%.2f".format(amount)
}

@JvmInline
value class BRL(val amount: Double) {
    fun format(): String = "R$ %.2f".format(amount)
}

fun processPayment(price: USD) {
    println("Processing payment: ${price.format()}")
}

fun exemploMoedas() {
    val priceUSD = USD(99.99)
    val priceEUR = EUR(89.99)
    val priceBRL = BRL(549.90)
    
    processPayment(priceUSD)
    // processPayment(priceEUR) // ❌ Erro: não pode misturar moedas
    
    println("Prices: ${priceUSD.format()}, ${priceEUR.format()}, ${priceBRL.format()}")
}

// ============================================
// 3. VALIDAÇÃO COM INIT
// ============================================

@JvmInline
value class Password(val value: String) {
    init {
        require(value.length >= 8) { "Password must be at least 8 characters" }
        require(value.any { it.isUpperCase() }) { "Password must contain uppercase" }
        require(value.any { it.isDigit() }) { "Password must contain a digit" }
    }
}

@JvmInline
value class CPF(val value: String) {
    init {
        require(value.length == 11) { "CPF must have 11 digits" }
        require(value.all { it.isDigit() }) { "CPF must contain only digits" }
    }
    
    fun formatted(): String {
        return "${value.substring(0,3)}.${value.substring(3,6)}.${value.substring(6,9)}-${value.substring(9)}"
    }
}

fun exemploValidacao() {
    try {
        val password = Password("SecurePass123")
        println("Valid password created")
        
        val cpf = CPF("12345678901")
        println("CPF: ${cpf.formatted()}")
        
        // val weakPassword = Password("123") // ❌ Lança exceção
    } catch (e: IllegalArgumentException) {
        println("Validation error: ${e.message}")
    }
}

// ============================================
// 4. MÉTODOS E PROPRIEDADES
// ============================================

@JvmInline
value class Temperature(val celsius: Double) {
    val fahrenheit: Double
        get() = celsius * 9/5 + 32
    
    val kelvin: Double
        get() = celsius + 273.15
    
    fun isFreezing(): Boolean = celsius <= 0
    fun isBoiling(): Boolean = celsius >= 100
    
    operator fun plus(other: Temperature): Temperature {
        return Temperature(this.celsius + other.celsius)
    }
}

fun exemploTemperatura() {
    val temp1 = Temperature(25.0)
    val temp2 = Temperature(15.0)
    
    println("${temp1.celsius}°C = ${temp1.fahrenheit}°F = ${temp1.kelvin}K")
    println("Is freezing? ${temp1.isFreezing()}")
    
    val total = temp1 + temp2
    println("Total: ${total.celsius}°C")
}

// ============================================
// 5. DISTÂNCIAS E MEDIDAS
// ============================================

@JvmInline
value class Meters(val value: Double) {
    fun toKilometers(): Kilometers = Kilometers(value / 1000)
    fun toMiles(): Miles = Miles(value * 0.000621371)
}

@JvmInline
value class Kilometers(val value: Double) {
    fun toMeters(): Meters = Meters(value * 1000)
}

@JvmInline
value class Miles(val value: Double) {
    fun toMeters(): Meters = Meters(value / 0.000621371)
}

fun exemploDistancias() {
    val distance = Meters(5000.0)
    println("${distance.value} meters")
    println("${distance.toKilometers().value} km")
    println("${distance.toMiles().value} miles")
}

// ============================================
// 6. IDs TIPADOS PARA DIFERENTES ENTIDADES
// ============================================

@JvmInline
value class OrderId(val value: String)

@JvmInline
value class ProductId(val value: String)

@JvmInline
value class CustomerId(val value: String)

data class Order(
    val orderId: OrderId,
    val customerId: CustomerId,
    val products: List<ProductId>
)

fun exemploIdsTyped() {
    val order = Order(
        orderId = OrderId("ORD-001"),
        customerId = CustomerId("CUST-123"),
        products = listOf(
            ProductId("PROD-A"),
            ProductId("PROD-B")
        )
    )
    
    println("Order: ${order.orderId.value}")
    println("Customer: ${order.customerId.value}")
    println("Products: ${order.products.map { it.value }}")
    
    // Type safety: não pode confundir IDs
    // val wrongOrder = Order(
    //     orderId = CustomerId("CUST-123"), // ❌ Erro de tipo
    //     customerId = OrderId("ORD-001"),
    //     products = emptyList()
    // )
}

// ============================================
// 7. TOKENS E SEGURANÇA
// ============================================

@JvmInline
value class AuthToken(val value: String) {
    fun isExpired(currentTime: Long): Boolean {
        // Lógica simplificada
        return value.isEmpty()
    }
    
    fun masked(): String {
        return if (value.length > 8) {
            "${value.take(4)}****${value.takeLast(4)}"
        } else {
            "****"
        }
    }
}

@JvmInline
value class ApiKey(val value: String) {
    fun masked(): String = "****${value.takeLast(4)}"
}

fun exemploSeguranca() {
    val token = AuthToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    val apiKey = ApiKey("sk_live_1234567890abcdef")
    
    println("Token: ${token.masked()}")
    println("API Key: ${apiKey.masked()}")
}

// ============================================
// 8. WRAPPER DE COLEÇÕES
// ============================================

@JvmInline
value class Tags(val values: List<String>) {
    fun contains(tag: String): Boolean = values.contains(tag)
    fun isEmpty(): Boolean = values.isEmpty()
    fun size(): Int = values.size
}

@JvmInline
value class PhoneNumbers(val numbers: Set<String>) {
    fun primary(): String? = numbers.firstOrNull()
    fun count(): Int = numbers.size
}

fun exemploColecoes() {
    val tags = Tags(listOf("kotlin", "value-class", "performance"))
    println("Tags: ${tags.values}")
    println("Contains 'kotlin': ${tags.contains("kotlin")}")
    println("Size: ${tags.size()}")
    
    val phones = PhoneNumbers(setOf("+55 11 98765-4321", "+55 11 91234-5678"))
    println("Primary phone: ${phones.primary()}")
    println("Total phones: ${phones.count()}")
}

// ============================================
// 9. RESULTADO TIPADO (ALTERNATIVA A ENUM)
// ============================================

@JvmInline
value class HttpStatusCode(val code: Int) {
    fun isSuccess(): Boolean = code in 200..299
    fun isClientError(): Boolean = code in 400..499
    fun isServerError(): Boolean = code in 500..599
    
    companion object {
        val OK = HttpStatusCode(200)
        val CREATED = HttpStatusCode(201)
        val BAD_REQUEST = HttpStatusCode(400)
        val NOT_FOUND = HttpStatusCode(404)
        val INTERNAL_ERROR = HttpStatusCode(500)
    }
}

fun exemploHttpStatus() {
    val status = HttpStatusCode.OK
    println("Status: ${status.code}")
    println("Is success? ${status.isSuccess()}")
    
    val errorStatus = HttpStatusCode(404)
    println("Is client error? ${errorStatus.isClientError()}")
}

// ============================================
// 10. PERCENTUAL E FRAÇÕES
// ============================================

@JvmInline
value class Percentage(val value: Double) {
    init {
        require(value in 0.0..100.0) { "Percentage must be between 0 and 100" }
    }
    
    fun toDecimal(): Double = value / 100.0
    fun format(): String = "$value%"
    
    operator fun times(amount: Double): Double = amount * toDecimal()
}

fun exemploPercentual() {
    val discount = Percentage(15.0)
    val price = 100.0
    
    println("Discount: ${discount.format()}")
    println("Price: $$price")
    println("Discount amount: $${discount * price}")
    println("Final price: $${price - (discount * price)}")
}

// ============================================
// MAIN - EXECUTAR TODOS OS EXEMPLOS
// ============================================

fun main() {
    println("=== 1. EXEMPLO BÁSICO ===")
    exemploBasico()
    
    println("\n=== 2. MOEDAS ===")
    exemploMoedas()
    
    println("\n=== 3. VALIDAÇÃO ===")
    exemploValidacao()
    
    println("\n=== 4. TEMPERATURA ===")
    exemploTemperatura()
    
    println("\n=== 5. DISTÂNCIAS ===")
    exemploDistancias()
    
    println("\n=== 6. IDs TIPADOS ===")
    exemploIdsTyped()
    
    println("\n=== 7. SEGURANÇA ===")
    exemploSeguranca()
    
    println("\n=== 8. COLEÇÕES ===")
    exemploColecoes()
    
    println("\n=== 9. HTTP STATUS ===")
    exemploHttpStatus()
    
    println("\n=== 10. PERCENTUAL ===")
    exemploPercentual()
}
