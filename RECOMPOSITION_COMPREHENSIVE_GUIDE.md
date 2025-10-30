# Jetpack Compose Recomposition: A Comprehensive Guide

## 🎯 Overview

This guide provides an in-depth exploration of Jetpack Compose's recomposition system - the mechanism that makes declarative UI updates possible and efficient. Understanding recomposition is crucial for building performant Compose applications.

## 📚 Table of Contents

1. [Core Concepts](#core-concepts)
2. [Stability System](#stability-system)
3. [Immutability in Compose](#immutability-in-compose)
4. [State Management & Recomposition](#state-management--recomposition)
5. [Recomposition Mechanics](#recomposition-mechanics)
6. [Performance Optimization](#performance-optimization)
7. [Common Pitfalls & Solutions](#common-pitfalls--solutions)
8. [Debugging Tools & Techniques](#debugging-tools--techniques)
9. [Best Practices](#best-practices)
10. [Real-World Examples](#real-world-examples)

---

## 🎯 1. Core Concepts

### What is Recomposition?

**Recomposition** is the process by which Jetpack Compose updates the UI when state changes. Unlike traditional imperative UI frameworks where you manually update views, Compose automatically figures out what needs to be updated based on state changes.

```kotlin
// Traditional Android (Imperative)
textView.text = "New Text"  // Manual update

// Jetpack Compose (Declarative)
var text by remember { mutableStateOf("Initial Text") }
// UI automatically updates when 'text' changes
```

### How Recomposition Works

1. **State Change Detection**: Compose tracks dependencies on state objects
2. **Invalidation**: When state changes, Compose marks affected composables as "dirty"
3. **Re-execution**: Dirty composables are re-executed with new state values
4. **Diffing**: Compose compares old and new UI trees
5. **Application**: Only changed UI elements are updated on screen

### Recomposition vs. Traditional Updates

| Aspect | Traditional Views | Jetpack Compose |
|--------|-------------------|-----------------|
| **Update Trigger** | Manual method calls | Automatic state change detection |
| **Scope** | Specific view updates | Smart partial updates |
| **Performance** | Often full redraws | Minimal UI updates |
| **Code Complexity** | High (manual sync) | Low (declarative) |

---

## 🎯 2. Stability System

### What is Stability?

**Stability** refers to whether Compose can determine if two objects are equivalent across recompositions. Stable objects have reliable equality checks, while unstable objects may appear different even when their content hasn't changed.

### Stable vs. Unstable Types

```kotlin
// ✅ STABLE: Data classes are stable by default
data class User(val id: String, val name: String)

// ✅ STABLE: Remember provides stable wrapper
val stableList = remember { mutableListOf<User>() }

// ❌ UNSTABLE: Mutable collections are unstable
val unstableList = mutableListOf<User>()

// ❌ UNSTABLE: Classes without proper equals()
class CustomClass(val value: String) // No equals() override
```

### Stability Annotations

Compose provides annotations to explicitly mark stability:

```kotlin
// Explicitly mark as stable
@Stable
class StableUser(val name: String, val age: Int)

// Mark as immutable (strongest stability guarantee)
@Immutable
data class ImmutableUser(val name: String, val age: Int)

// Mark as unstable when necessary
@Unstable
class UnstableData(val timestamp: Long = System.currentTimeMillis())
```

### Impact of Stability on Performance

| Stability Level | Recomposition Behavior | Performance Impact |
|----------------|----------------------|-------------------|
| **Immutable** | Never recomposes unless reference changes | Best performance |
| **Stable** | Recomposition only when equals() returns false | Good performance |
| **Unstable** | Recomposition on every state change | Poor performance |

---

## 🎯 3. Immutability in Compose

### Why Immutability Matters

Immutable objects provide several benefits:
- **Predictable Updates**: Clear when changes occur
- **Safe Concurrency**: No race conditions
- **Efficient Equality**: Fast comparison operations
- **Better Caching**: Reliable memoization

### Creating Immutable Data

```kotlin
// ✅ Good: Immutable data class
data class User(
    val id: String,
    val name: String,
    val email: String
) {
    // No var properties, no mutable collections
}

// ✅ Good: Copy methods for updates
fun User.withName(newName: String) = copy(name = newName)
fun User.withEmail(newEmail: String) = copy(email = newEmail)
```

### Immutable Collections

```kotlin
// ✅ Good: Use immutable collections
val users: List<User> = listOf(user1, user2, user3)

// ✅ Good: Create new lists for updates
val updatedUsers = users + newUser  // Creates new list
val filteredUsers = users.filter { it.active }

// ❌ Avoid: Mutable collections as state
// val users = mutableListOf<User>() // Unstable!
```

### State Updates with Immutability

```kotlin
// ✅ Correct: Immutable state updates
var users by remember { mutableStateOf(listOf<User>()) }

fun addUser(newUser: User) {
    users = users + newUser  // Creates new list
}

// ✅ Correct: Complex updates
fun updateUser(userId: String, update: (User) -> User) {
    users = users.map { user ->
        if (user.id == userId) update(user) else user
    }
}
```

---

## 🎯 4. State Management & Recomposition

### State Types and Their Recomposition Behavior

#### 1. `mutableStateOf` (State<T>)

```kotlin
// Creates a State object that triggers recomposition when changed
var count by remember { mutableStateOf(0) }

@Composable
fun Counter() {
    Button(onClick = { count++ }) {  // Triggers recomposition
        Text("Count: $count")
    }
}
```

#### 2. `MutableStateFlow` / `StateFlow`

```kotlin
// ViewModel state (recommended for complex state)
class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun addUser(user: User) {
        _users.value = _users.value + user  // Triggers recomposition
    }
}
```

#### 3. `mutableStateListOf` / `mutableStateMapOf`

```kotlin
// For observable collections
var userList by remember { mutableStateListOf<User>() }

@Composable
fun UserList() {
    LazyColumn {
        items(userList) { user ->  // Recomposition when list changes
            UserCard(user)
        }
    }
}
```

### State Hoisting

State hoisting moves state up the composable tree for better control:

```kotlin
// ❌ Bad: State scattered across composables
@Composable
fun UserForm() {
    var name by remember { mutableStateOf("") }  // State here
    var email by remember { mutableStateOf("") } // State here

    UserNameField(name) { name = it }
    UserEmailField(email) { email = it }
}

// ✅ Good: Hoisted state
@Composable
fun UserForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    UserFormContent(
        name = name,
        email = email,
        onNameChange = { name = it },
        onEmailChange = { email = it }
    )
}
```

---

## 🎯 5. Recomposition Mechanics

### The Recomposition Process

1. **State Change**: A state object is modified
2. **Invalidation**: Compose marks dependent composables as "dirty"
3. **Scheduling**: Recomposition is scheduled on the next frame
4. **Execution**: Dirty composables re-execute with new state
5. **Diffing**: New UI tree is compared with old tree
6. **Application**: Only changed parts are rendered

### Recomposition Scopes

```kotlin
@Composable
fun UserProfile(user: User) {
    // This entire composable is a recomposition scope
    Column {
        UserHeader(user)  // Recomposes when user changes
        UserDetails(user) // Recomposes when user changes

        // Conditional composition
        if (user.isPremium) {
            PremiumBadge()  // Only recomposes when isPremium changes
        }
    }
}
```

### Smart Recomposition

Compose only recomposes what's necessary:

```kotlin
@Composable
fun UserList(users: List<User>, selectedUserId: String?) {
    Column {
        // Recomposes when users OR selectedUserId changes
        UserFilter(selectedUserId) { /* filter logic */ }

        // Recomposes ONLY when users changes (not selectedUserId)
        LazyColumn {
            items(users) { user ->
                UserItem(user, user.id == selectedUserId)
            }
        }
    }
}
```

---

## 🎯 6. Performance Optimization

### Remember for Expensive Computations

```kotlin
@Composable
fun ExpensiveScreen(data: List<Item>) {
    // ❌ Bad: Expensive work on every recomposition
    val processedData = processData(data)  // Runs every time!

    // ✅ Good: Remember expensive computations
    val processedData = remember(data) {
        processData(data)  // Only when data changes
    }

    // ✅ Good: Multiple dependencies
    val filteredData = remember(data, searchQuery) {
        data.filter { it.contains(searchQuery) }
    }
}
```

### Derived State for Computed Properties

```kotlin
@Composable
fun UserScreen(user: User) {
    // ✅ Good: Derived state for computed properties
    val displayName = remember(user.firstName, user.lastName) {
        "${user.firstName} ${user.lastName}".trim()
    }

    val isValidUser = remember(user.name, user.email, user.age) {
        user.name.isNotBlank() &&
        user.email.contains("@") &&
        user.age >= 18
    }

    Text("User: $displayName (Valid: $isValidUser)")
}
```

### Keyed Remember for Dynamic Data

```kotlin
@Composable
fun UserProfile(userId: String) {
    // ✅ Good: Use keys when computation depends on changing parameters
    val userData = remember(userId) {
        fetchUserData(userId)  // Only refetches when userId changes
    }

    // ✅ Good: Multiple keys for complex dependencies
    val currentTime = System.currentTimeMillis()
    val userStats = remember(userId, currentTime) {
        computeUserStats(userId, currentTime)
    }
}
```

### Lazy Loading for Large Lists

```kotlin
@Composable
fun LargeUserList(users: List<User>) {
    // ✅ Good: Lazy loading prevents composing all items at once
    LazyColumn {
        items(
            items = users,
            key = { user -> user.id }  // Stable keys for performance
        ) { user ->
            UserCard(user)  // Only composed when visible
        }
    }
}
```

---

## 🎯 7. Common Pitfalls & Solutions

### Pitfall 1: Unstable Collections

```kotlin
// ❌ Bad: Mutable collections are unstable
@Composable
fun UserList() {
    val users = remember { mutableListOf<User>() }

    LaunchedEffect(Unit) {
        users.addAll(fetchUsers())  // Triggers full recomposition!
    }

    LazyColumn {
        items(users) { user ->  // Recomposition on every change
            UserCard(user)
        }
    }
}

// ✅ Good: Use immutable state updates
@Composable
fun UserList() {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        users = fetchUsers()  // Single recomposition
    }
}
```

### Pitfall 2: Expensive Computations in Composables

```kotlin
// ❌ Bad: Heavy computation on every recomposition
@Composable
fun Dashboard(data: RawData) {
    val processedData = data.process()  // Expensive! Runs every time
    val chartData = data.generateChart()  // Even more expensive!

    DashboardContent(processedData, chartData)
}

// ✅ Good: Move expensive work out of composables
@Composable
fun Dashboard(viewModel: DashboardViewModel) {
    val processedData by viewModel.processedData.collectAsState()
    val chartData by viewModel.chartData.collectAsState()

    DashboardContent(processedData, chartData)
}
```

### Pitfall 3: Incorrect State Updates

```kotlin
// ❌ Bad: Mutating state directly
@Composable
fun Counter() {
    val state = remember { mutableStateOf(CounterState()) }

    Button(onClick = {
        state.value = state.value.copy(count = state.value.count + 1)  // ✅ Good
        // state.value.count++  // ❌ Bad: Direct mutation
    }) {
        Text("Count: ${state.value.count}")
    }
}
```

---

## 🎯 8. Debugging Tools & Techniques

### 1. Recomposition Counts

```kotlin
@Composable
fun DebuggableComposable() {
    Text(
        text = "Debug Info",
        modifier = Modifier.recomposeHighlighter()  // Visual highlighting
    )
}

// Enable in debug builds
@Composable
fun RecomposeHighlighter(modifier: Modifier = Modifier) =
    modifier.recomposeSeed { seed ->
        // Custom recomposition tracking
        println("Recomposed with seed: $seed")
    }
```

### 2. Performance Monitoring

```kotlin
// Enable recomposition counting in debug
@Composable
fun AppContent() {
    val context = LocalContext.current
    if (context.isDebuggable()) {
        // Add recomposition counting
        RecompositionCounter()
    }

    MainContent()
}
```

### 3. State Change Tracking

```kotlin
// Custom state wrapper for debugging
class DebugState<T>(initialValue: T) : MutableState<T> {
    private var _value by mutableStateOf(initialValue)

    override var value: T
        get() = _value
        set(value) {
            println("State changed from $_value to $value")
            _value = value
        }

    // ... implement other MutableState methods
}
```

### 4. Android Studio Tools

- **Layout Inspector**: Shows recomposition counts
- **Profile GPU Rendering**: Identifies rendering bottlenecks
- **Memory Profiler**: Detects memory leaks from improper state management

---

## 🎯 9. Best Practices

### 1. State Management Best Practices

```kotlin
// ✅ Good: Single source of truth in ViewModel
class UserViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    fun updateUser(updates: UserUpdates) {
        _state.update { current ->
            current.copy(user = current.user.merge(updates))
        }
    }
}
```

### 2. Performance Best Practices

```kotlin
@Composable
fun OptimizedList(items: List<Item>, modifier: Modifier = Modifier) {
    // ✅ Good: Stable keys
    LazyColumn(modifier = modifier) {
        items(
            items = items,
            key = { item -> item.stableId }
        ) { item ->
            // ✅ Good: Small, focused composables
            OptimizedItem(item)
        }
    }
}

@Composable
private fun OptimizedItem(item: Item) {
    // ✅ Good: Minimal composable scope
    Row {
        Text(item.name)
        Spacer(Modifier.weight(1f))
        Text(item.status)
    }
}
```

### 3. Architecture Best Practices

```kotlin
// ✅ Good: Clean separation of concerns
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    // ✅ Good: Event-driven updates
    UserScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}
```

---

## 🎯 10. Real-World Examples

### Example 1: Chat Application

```kotlin
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val typingUsers by viewModel.typingUsers.collectAsState()

    Column {
        // ✅ Good: Only recomposes when messages change
        MessageList(messages = messages)

        // ✅ Good: Conditional composition for typing indicators
        if (typingUsers.isNotEmpty()) {
            TypingIndicator(typingUsers)
        }

        // ✅ Good: Controlled state updates
        MessageInput { message ->
            viewModel.sendMessage(message)
        }
    }
}
```

### Example 2: Dashboard with Real-time Data

```kotlin
@Composable
fun Dashboard(viewModel: DashboardViewModel = viewModel()) {
    val metrics by viewModel.metrics.collectAsState()
    val lastUpdate by viewModel.lastUpdate.collectAsState()

    // ✅ Good: Derived state for computed values
    val trend = remember(metrics, lastUpdate) {
        calculateTrend(metrics)
    }

    Column {
        // ✅ Good: Separate composables for different data
        MetricsGrid(metrics)
        TrendChart(trend)
        LastUpdateTime(lastUpdate)
    }
}
```

### Example 3: Form with Validation

```kotlin
@Composable
fun UserForm(viewModel: UserFormViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    // ✅ Good: Real-time validation with debouncing
    val isValid = remember(state.name, state.email) {
        validateForm(state.name, state.email)
    }

    Column {
        // ✅ Good: Field-level state management
        NameField(
            value = state.name,
            error = state.nameError,
            onChange = { viewModel.updateName(it) }
        )

        EmailField(
            value = state.email,
            error = state.emailError,
            onChange = { viewModel.updateEmail(it) }
        )

        // ✅ Good: Computed submit button state
        SubmitButton(
            enabled = isValid && !state.isLoading,
            onClick = { viewModel.submit() }
        )
    }
}
```

---

## 🚀 Key Takeaways

1. **Recomposition is automatic** - Focus on state management, not UI updates
2. **Stability matters** - Use immutable data and stable types for performance
3. **Remember expensive work** - Cache computations that don't need to run on every recomposition
4. **Keep composables small** - Smaller scopes mean more efficient recomposition
5. **Use proper state management** - ViewModels for complex state, remember for simple state
6. **Debug actively** - Use tools to identify performance bottlenecks
7. **Follow best practices** - Clean architecture and proper state hoisting

## 📚 Further Reading

- [Official Compose Performance Guide](https://developer.android.com/jetpack/compose/performance)
- [Compose Stability Explained](https://developer.android.com/jetpack/compose/stability)
- [Thinking in Compose](https://developer.android.com/jetpack/compose/mental-model)
- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)

---

*This guide is based on Jetpack Compose 1.2+ and follows current best practices for building performant, maintainable Compose applications.*
