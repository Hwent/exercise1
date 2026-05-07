package com.example.mobileshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobileshop.ui.theme.MobileShopTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ShopApp()
            }
        }
    }
}

// ─── Data model ──────────────────────────────────────────────────────────────
// A simple data class — like a blueprint for one product.
data class Product(
    val name: String,
    val emoji: String,
    val price: Double
)

// Our "database" — just a plain Kotlin list.
val allProducts = listOf(
    Product("Apple",  "🍎", 1.20),
    Product("Milk",   "🥛", 2.50),
    Product("Bread",  "🍞", 3.00),
    Product("Cheese", "🧀", 4.80),
    Product("Eggs",   "🥚", 3.50),
    Product("Butter", "🧈", 2.90),
    Product("Coffee", "☕", 6.00),
    Product("Juice",  "🍊", 3.20),
)

// ─── App root ─────────────────────────────────────────────────────────────────
// @Composable means this function draws UI.
// "remember" keeps the cart alive across recompositions (screen redraws).
// mutableStateMapOf is like a Map, but Compose watches it for changes.
@Composable
fun ShopApp() {
    // Cart: product name → quantity.  e.g. "Apple" → 2
    val cart = remember { mutableStateMapOf<String, Int>() }

    // Track which screen to show: "shop" or "cart"
    var currentScreen by remember { mutableStateOf("shop") }

    if (currentScreen == "shop") {
        ShopScreen(
            cart = cart,
            onAddToCart = { product ->
                // Elvis operator ?: means "or zero if not found"
                cart[product.name] = (cart[product.name] ?: 0) + 1
            },
            onViewCart = { currentScreen = "cart" }
        )
    } else {
        CartScreen(
            cart = cart,
            onRemoveOne = { product ->
                val current = cart[product.name] ?: 0
                if (current <= 1) cart.remove(product.name)
                else cart[product.name] = current - 1
            },
            onBack = { currentScreen = "shop" }
        )
    }
}

// ─── Shop screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    cart: Map<String, Int>,
    onAddToCart: (Product) -> Unit,
    onViewCart: () -> Unit
) {
    val totalItems = cart.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop") },
                actions = {
                    // Badge shows the number of items in the cart
                    BadgedBox(
                        badge = {
                            if (totalItems > 0) Badge { Text("$totalItems") }
                        }
                    ) {
                        IconButton(onClick = onViewCart) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "View cart")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        // LazyColumn = efficient scrolling list (only renders visible rows)
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(allProducts) { product ->
                ProductRow(
                    product = product,
                    quantity = cart[product.name] ?: 0,
                    onAdd = { onAddToCart(product) }
                )
                HorizontalDivider()
            }
        }
    }
}

// ─── Single product row ───────────────────────────────────────────────────────
@Composable
fun ProductRow(product: Product, quantity: Int, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji icon
        Text(product.emoji, fontSize = 32.sp)

        Spacer(modifier = Modifier.width(16.dp))

        // Name + price stacked vertically
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Medium)
            Text("$${String.format("%.2f", product.price)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp)
        }

        // Show quantity if > 0
        if (quantity > 0) {
            Text(
                "×$quantity",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // Add button
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "Add ${product.name}")
        }
    }
}

// ─── Cart screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cart: Map<String, Int>,
    onRemoveOne: (Product) -> Unit,
    onBack: () -> Unit
) {
    // Filter to only products that are in the cart
    val cartItems = allProducts.filter { (cart[it.name] ?: 0) > 0 }

    // Calculate total price
    val total = cartItems.sumOf { product ->
        product.price * (cart[product.name] ?: 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping bag") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        // Total panel pinned to the bottom of the screen
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "$${String.format("%.2f", total)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Your bag is empty 🛍", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                items(cartItems) { product ->
                    CartRow(
                        product = product,
                        quantity = cart[product.name] ?: 0,
                        onRemove = { onRemoveOne(product) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ─── Single cart row ──────────────────────────────────────────────────────────
@Composable
fun CartRow(product: Product, quantity: Int, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(product.emoji, fontSize = 28.sp)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Medium)
            Text(
                "$quantity × $${String.format("%.2f", product.price)}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Line total
        Text(
            "$${String.format("%.2f", product.price * quantity)}",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Remove one button
        TextButton(onClick = onRemove) {
            Text("−", fontSize = 18.sp)
        }
    }
}