package com.example.mobileshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Brand colours (Nike-inspired) ───────────────────────────────────────────
val OrangeRed   = Color(0xFFE8431A)
val DarkBg      = Color(0xFF1A1A1A)
val CardBg      = Color(0xFF2A2A2A)
val LightGray   = Color(0xFFF5F5F5)
val MutedText   = Color(0xFF999999)

// ─── Entry point ──────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ShopApp() }
    }
}

// ─── Data model ──────────────────────────────────────────────────────────────
data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Double,
    val rating: Float,
    val reviews: Int,
    val category: String,
    val imageUrl: String,
    val colors: List<Color> = listOf(OrangeRed, Color.Black, Color.White)
)

val allProducts = listOf(
    Product(1, "Air Max 90", "Nike", 137.50, 4.8f, 1120,  "Running",
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"),
    Product(2, "Creter Impact", "Nike", 99.56,  4.6f, 843,   "Lifestyle",
        "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400"),
    Product(3, "Air Force 1", "Nike", 110.00, 4.9f, 2041,  "Lifestyle",
        "https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=400"),
    Product(4, "React Infinity", "Nike", 159.99, 4.7f, 567,  "Running",
        "https://images.unsplash.com/photo-1539185441755-769473a23570?w=400"),
    Product(5, "Blazer Mid 77", "Nike", 100.00, 4.5f, 389,  "Basketball",
        "https://images.unsplash.com/photo-1603787081207-362bcef7c144?w=400"),
    Product(6, "Pegasus 40",    "Nike", 129.99, 4.8f, 712,  "Running",
        "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=400"),
)

val categories = listOf("All", "Lifestyle", "Running", "Basketball")

// ─── App root ─────────────────────────────────────────────────────────────────
@Composable
fun ShopApp() {
    val cart = remember { mutableStateMapOf<Int, Int>() }
    var currentScreen by remember { mutableStateOf("home") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    when (currentScreen) {
        "home"   -> HomeScreen(
            cart = cart,
            onProductClick = { product ->
                selectedProduct = product
                currentScreen = "detail"
            },
            onCartClick = { currentScreen = "cart" }
        )
        "detail" -> selectedProduct?.let {
            DetailScreen(
                product = it,
                quantity = cart[it.id] ?: 0,
                onAdd = { cart[it.id] = (cart[it.id] ?: 0) + 1 },
                onBack = { currentScreen = "home" },
                onCartClick = { currentScreen = "cart" }
            )
        }
        "cart"   -> CartScreen(
            cart = cart,
            onRemove = { id ->
                val q = cart[id] ?: 0
                if (q <= 1) cart.remove(id) else cart[id] = q - 1
            },
            onBack = { currentScreen = "home" }
        )
    }
}

// ─── Home screen ─────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    cart: Map<Int, Int>,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val totalItems = cart.values.sum()

    val filtered = if (selectedCategory == "All") allProducts
    else allProducts.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // ── Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Discover", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                Text("Find your perfect pair", fontSize = 13.sp, color = MutedText)
            }
            BadgedBox(badge = {
                if (totalItems > 0) Badge(containerColor = OrangeRed) { Text("$totalItems") }
            }) {
                IconButton(onClick = onCartClick) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = DarkBg)
                }
            }
        }

        // ── Hero banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkBg)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                contentDescription = "Hero banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text("New Release", fontSize = 11.sp, color = OrangeRed, fontWeight = FontWeight.Bold)
                Text("Nike Air Max 90", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onProductClick(allProducts[0]) },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Shop Now", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Category chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val selected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) OrangeRed else Color.White)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        cat,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color.White else DarkBg
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section header
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("New arrivals", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBg)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Product grid (2 columns using LazyColumn + rows)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val rows = filtered.chunked(2)
            items(rows) { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { product ->
                        ProductCard(
                            product = product,
                            quantity = cart[product.id] ?: 0,
                            modifier = Modifier.weight(1f),
                            onClick = { onProductClick(product) },
                            onAdd = { }
                        )
                    }
                    // Fill empty slot if odd number of items
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Product card ─────────────────────────────────────────────────────────────
@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                if (quantity > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(22.dp)
                            .background(OrangeRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$quantity", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.brand, fontSize = 10.sp, color = OrangeRed, fontWeight = FontWeight.Bold)
                Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkBg,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$${String.format("%.2f", product.price)}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkBg)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(OrangeRed, CircleShape)
                            .clickable { onClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add",
                            tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ─── Detail screen ────────────────────────────────────────────────────────────
@Composable
fun DetailScreen(
    product: Product,
    quantity: Int,
    onAdd: () -> Unit,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    var selectedSize by remember { mutableStateOf(42) }
    val sizes = listOf(40, 41, 42, 43, 44, 45)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkBg)
            }
            Text("Detail", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBg)
            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = DarkBg)
            }
        }

        // ── Product image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(LightGray)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
        }

        // ── Info panel
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Text(product.brand, fontSize = 12.sp, color = OrangeRed, fontWeight = FontWeight.Bold)
            Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkBg)

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐ ${product.rating}", fontSize = 13.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                Text("  (${product.reviews} reviews)", fontSize = 12.sp, color = MutedText)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select size", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkBg)
            Spacer(modifier = Modifier.height(10.dp))

            // Size selector
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                sizes.forEach { size ->
                    val selected = size == selectedSize
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) OrangeRed else LightGray)
                            .clickable { selectedSize = size },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$size",
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else DarkBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom bar: price + Add to bag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkBg)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Price", fontSize = 11.sp, color = MutedText)
                    Text("$${String.format("%.2f", product.price)}",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        if (quantity == 0) "Add to bag" else "Add to bag (×$quantity)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Cart screen ──────────────────────────────────────────────────────────────
@Composable
fun CartScreen(
    cart: Map<Int, Int>,
    onRemove: (Int) -> Unit,
    onBack: () -> Unit
) {
    val cartItems = allProducts.filter { (cart[it.id] ?: 0) > 0 }
    val total = cartItems.sumOf { it.price * (cart[it.id] ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkBg)
            }
            Text("My bag", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkBg,
                modifier = Modifier.padding(start = 8.dp))
        }

        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your bag is empty 👟", color = MutedText, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { product ->
                    CartItem(
                        product = product,
                        quantity = cart[product.id] ?: 0,
                        onRemove = { onRemove(product.id) }
                    )
                }
            }

            // Total + checkout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontSize = 16.sp, color = MutedText)
                    Text("$${String.format("%.2f", total)}",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ─── Cart item row ────────────────────────────────────────────────────────────
@Composable
fun CartItem(product: Product, quantity: Int, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.brand, fontSize = 10.sp, color = OrangeRed, fontWeight = FontWeight.Bold)
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkBg)
                Text("$${String.format("%.2f", product.price * quantity)}",
                    fontWeight = FontWeight.Bold, color = OrangeRed, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("×$quantity", fontWeight = FontWeight.Bold, color = DarkBg)
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = onRemove,
                    contentPadding = PaddingValues(0.dp)) {
                    Text("−", fontSize = 20.sp, color = OrangeRed)
                }
            }
        }
    }
}