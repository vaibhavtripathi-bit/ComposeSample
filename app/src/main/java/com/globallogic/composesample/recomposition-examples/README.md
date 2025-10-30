# Jetpack Compose Recomposition Examples

This folder contains practical, easy-to-understand examples that demonstrate key recomposition concepts in Jetpack Compose. Each file focuses on specific aspects of recomposition with both good and bad examples.

## 📁 File Structure

```
recomposition-examples/
├── README.md                          # This guide
├── RECOMPOSITION_COMPREHENSIVE_GUIDE.md  # Complete theory guide
├── 01_BasicConcepts.kt               # Fundamental recomposition concepts
├── 02_StabilityExamples.kt           # Data stability and performance
├── 03_StateManagement.kt             # Different state management approaches
├── 04_PerformanceOptimizations.kt    # Performance techniques
├── 05_CommonPitfalls.kt              # Common mistakes and solutions
├── 06_CompleteExample.kt             # Full Todo app
├── dynamic-list/                     # Dynamic list management (modular)
│   ├── models/
│   │   └── Person.kt                 # Stable data models
│   ├── viewmodel/
│   │   └── PersonListViewModel.kt    # Efficient state management
│   ├── ui/
│   │   ├── PersonListScreen.kt       # Main UI screen
│   │   ├── PersonItem.kt             # Individual item component
│   │   ├── PersonListHeader.kt       # Header with stats
│   │   └── AddPersonSection.kt       # Add person form
│   ├── performance/
│   │   └── PerformanceComparisonDemo.kt  # Performance comparison
│   └── utils/
│       └── SampleDataGenerator.kt    # Sample data utility
└── RecompositionDemoActivity.kt      # Android integration
```

## 🎯 What You'll Learn

### 1. Basic Concepts (`01_BasicConcepts.kt`)
- How recomposition works with simple state changes
- Multiple state dependencies
- Recomposition scopes
- Conditional composition

**Key Takeaway**: State changes → UI updates automatically!

### 2. Stability Examples (`02_StabilityExamples.kt`)
- Stable vs unstable data types
- Data class stability
- Collection stability
- Explicit stability annotations (@Stable, @Immutable)

**Key Takeaway**: Stable data = better performance, fewer recompositions

### 3. State Management (`03_StateManagement.kt`)
- `mutableStateOf` basics
- State lists and maps
- Complex state objects
- StateFlow integration
- State hoisting
- Side effects with state

**Key Takeaway**: Different state types have different recomposition behaviors

### 4. Performance Optimizations (`04_PerformanceOptimizations.kt`)
- `remember` for expensive computations
- Derived state for computed properties
- Keys for dynamic lists
- Conditional composition
- Debouncing for performance
- Small composables

**Key Takeaway**: Optimize by minimizing unnecessary work

### 5. Common Pitfalls (`05_CommonPitfalls.kt`)
- Unstable collections (and how to fix them)
- Expensive computations in composables
- Incorrect state updates
- Side effects in composables
- Over-relying on recomposition
- ViewModel state management
- Key management issues
- Lifecycle problems

**Key Takeaway**: Learn from mistakes to avoid performance issues

### 6. Dynamic List Management (`dynamic-list/`)
Modular implementation of efficient dynamic list handling:

**📁 Structure:**
- **`models/Person.kt`** - Stable Person data class with proper equality
- **`viewmodel/PersonListViewModel.kt`** - StateFlow-based list management
- **`ui/PersonListScreen.kt`** - Main screen with LazyColumn and stable keys
- **`ui/PersonItem.kt`** - Individual item component (recomposes only when needed)
- **`ui/PersonListHeader.kt`** - Header with derived statistics
- **`ui/AddPersonSection.kt`** - Form for adding new persons
- **`performance/PerformanceComparisonDemo.kt`** - Performance comparison demo
- **`utils/SampleDataGenerator.kt`** - Sample data utility

**🎯 Key Features:**
- Efficient handling of dynamic lists from backend APIs
- Stable data classes for optimal performance
- ViewModel patterns for list state management
- Add, delete, update operations without full recomposition
- LazyColumn with stable keys
- API integration patterns
- Real-time updates and batch operations

**Key Takeaway**: Handle dynamic data efficiently with minimal recompositions

## 🚀 How to Use These Examples

### 1. Copy Files to Your Project
Add these files to your Compose project in the `presentation/ui` or similar directory.

### 2. Run Complete Application
Use the complete Todo app example:

```kotlin
// In your Activity or any composable
setContent {
    com.globallogic.composesample.recompositionexamples.TodoApp()
}
```

### 3. Handle Dynamic Lists
For dynamic list management from backend APIs:

```kotlin
// Use the efficient dynamic list example
setContent {
    com.globallogic.composesample.recompositionexamples.dynamiclist.ui.PersonListScreen()
}

// Or use individual components
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person
import com.globallogic.composesample.recompositionexamples.dynamiclist.viewmodel.PersonListViewModel

@Composable
fun MyDynamicList() {
    val viewModel: PersonListViewModel = viewModel()
    // ... use the components
}
```

### 4. Run Individual Examples
Each example is a standalone composable. You can run them individually:

```kotlin
@Composable
fun MyScreen() {
    // Run basic examples
    com.globallogic.composesample.recompositionexamples.SimpleCounter()
    com.globallogic.composesample.recompositionexamples.MultipleStates()

    // Or stability examples
    com.globallogic.composesample.recompositionexamples.StabilityDemo()

    // Or performance examples
    com.globallogic.composesample.recompositionexamples.ExpensiveComputationExample()
}
```

### 5. Launch Demo Activity
Add the demo activity to your AndroidManifest.xml:

```xml
<activity
    android:name=".recomposition-examples.RecompositionDemoActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

The demo activity includes all examples:
- Basic recomposition concepts
- Data stability examples
- State management patterns
- Performance optimizations
- Common pitfalls and solutions
- Complete Todo app
- **Dynamic list management** (new!)

### 6. Compare Good vs Bad Patterns
Most files show both problematic and optimal approaches:

```kotlin
// ❌ BAD: This causes excessive recomposition
@Composable
fun BadExample() {
    val items = remember { mutableListOf<String>() }
    // ...
}

// ✅ GOOD: This is much more efficient
@Composable
fun GoodExample() {
    var items by remember { mutableStateOf<List<String>>(emptyList()) }
    // ...
}
```

## 🏃‍♂️ Quick Start Examples

### Simple Counter (Most Basic)
```kotlin
@Composable
fun SimpleCounter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Clicked $count times")
    }
}
```

### Remember for Performance
```kotlin
@Composable
fun OptimizedExample(data: List<Int>) {
    // ✅ Only recalculates when data changes
    val processedData = remember(data) {
        data.map { it * 2 }  // Expensive computation
    }

    Text("Result: ${processedData.joinToString()}")
}
```

### Stable Data
```kotlin
// ✅ Stable data class
data class User(val id: String, val name: String, val email: String, val age: Int = 25)

// ✅ Use immutable collections
var users by remember { mutableStateOf<List<User>>(emptyList()) }

fun addUser(newUser: User) {
    users = users + newUser  // Creates new list
}
```

## 🔧 Testing Recomposition

### Visual Testing
Run the examples and observe:
- When UI updates (recomposes)
- How quickly updates happen
- Whether animations are smooth

### Performance Testing
Look for these patterns:
- ✅ Fast, smooth updates = good optimization
- ❌ Jerky or slow updates = recomposition issues
- ❌ Excessive logging in console = too many recompositions

## 🐛 Common Issues and Solutions

### Issue: UI Updates Too Often
**Problem**: Composable recomposes on every state change
**Solution**: Use `remember` for expensive computations

### Issue: List Performance Poor
**Problem**: Using mutable lists or no keys
**Solution**: Use immutable state lists with stable keys

### Issue: State Not Updating
**Problem**: Direct state mutation instead of state updates
**Solution**: Always use copy methods or new state assignments

## 📚 Next Steps

1. **Start Simple**: Begin with `01_BasicConcepts.kt`
2. **Understand Stability**: Master `02_StabilityExamples.kt`
3. **Learn State Management**: Practice with `03_StateManagement.kt`
4. **Optimize Performance**: Apply techniques from `04_PerformanceOptimizations.kt`
5. **Avoid Pitfalls**: Study and avoid patterns in `05_CommonPitfalls.kt`

## 💡 Best Practices Summary

1. **Use stable data types** (data classes, immutable collections)
2. **Remember expensive computations** with `remember()`
3. **Keep composables small** and focused
4. **Use proper state management** (ViewModel for complex state)
5. **Add keys to dynamic lists** for better performance
6. **Use effects for side effects**, not in composables directly
7. **Test your assumptions** about when things recompose

## 🔍 Debugging Tips

1. **Add logging** to see when composables recompose
2. **Use Android Studio's Layout Inspector** to see recomposition counts
3. **Compare performance** between good and bad patterns
4. **Test on real devices** for accurate performance measurement

---

## 🎉 Happy Composing!

These examples will help you master recomposition and build performant Compose applications. Remember: the key to good Compose performance is understanding when and why recomposition happens, then optimizing accordingly.

**Start simple, master the basics, then optimize for performance!** 🚀
