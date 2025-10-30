# Jetpack Compose Sample Project - Clean Architecture

A comprehensive sample project demonstrating **Jetpack Compose** with **Clean Architecture** and modern Android development best practices.

## 🚀 Overview

This project showcases a complete Android application built with:
- **Jetpack Compose 1.2+** - Modern declarative UI toolkit
- **Clean Architecture** - Separation of concerns across layers
- **Kotlin** - Modern, concise programming language
- **MVVM Pattern** - Model-View-ViewModel with unidirectional data flow
- **SOLID Principles** - Maintainable, testable code design

## 🏗️ Architecture Overview

The project follows Clean Architecture principles with three distinct layers:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│  • Jetpack Compose UI                                 │
│  • ViewModels (State Owners)                           │
│  • UI State & Events                                   │
└─────────────────────┬───────────────────────────────────┘
                      │ Dependency Inversion
┌─────────────────────▼───────────────────────────────────┐
│                    Domain Layer                         │
│  • Business Logic (Use Cases)                          │
│  • Domain Entities                                     │
│  • Repository Interfaces                               │
└─────────────────────┬───────────────────────────────────┘
                      │ Dependency Inversion
┌─────────────────────▼───────────────────────────────────┐
│                     Data Layer                          │
│  • Repository Implementations                          │
│  • Data Sources (API, Database, Cache)                 │
│  • Data Transfer Objects                               │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

**Domain Layer** (`domain/`)
- Contains **business logic** and **use cases**
- Defines **repository interfaces** (Dependency Inversion)
- Houses **domain entities** (pure business objects)
- Independent of frameworks (Android, databases, etc.)

**Data Layer** (`data/`)
- Implements **repository interfaces** from domain layer
- Handles **data access** (networks, databases, files)
- Transforms between **data models** and **domain entities**
- Contains **data sources** (local, remote, cache)

**Presentation Layer** (`presentation/`)
- Contains **Jetpack Compose UI** components
- **ViewModels** own and manage UI state
- Processes **UI events** and updates state
- Uses **use cases** for business logic execution

## 🎯 Jetpack Compose Fundamentals

### Declarative UI Paradigm

**Before Compose (Imperative XML):**
```xml
<!-- XML Layout - Imperative -->
<LinearLayout>
    <TextView
        android:text="Hello World"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>
```

**With Compose (Declarative):**
```kotlin
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
```

**Key Differences:**
- **XML**: "How" to create UI (imperative commands)
- **Compose**: "What" UI should look like (declarative description)

### Problems with XML/View-based Approach

1. **No Enforced Single Source of Truth**
   - UI state scattered across Activities, Fragments, Views
   - Multiple components can modify the same data

2. **Boilerplate Code**
   - FindViewById, ViewHolder patterns
   - Manual state synchronization

3. **Difficult Unit Testing**
   - UI components tightly coupled to Android framework
   - Hard to test business logic in isolation

4. **Two Languages**
   - XML for layouts + Kotlin/Java for logic
   - Context switching reduces productivity

5. **Example Issue**: Corner radius requires XML drawable files

### Production Readiness of Compose

**Compose 1.0** was released on **July 28, 2021** and is production-ready.

**Latest Stable**: Compose 1.2 (August 2022)

**Industry Adoption:**
- **Google Play Store** - Fully migrated to Compose
- **Twitter** - Reported 30-50% productivity gains
- **Airbnb** - Successfully migrated large codebase
- **Square** - Cash App uses Compose extensively
- **Michael Kors** - Significant development speed improvements

> *"Compose has significantly improved our productivity and code quality" - Twitter Engineering*

## 🏃‍♂️ Thinking in Compose

### Data → UI Transformation

Compose transforms data models into UI declaratively:

```kotlin
@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserCard(user)
        }
    }
}
```

### Finite State Machine Nature

Compose UIs behave like **finite state machines**:
- **States**: Loading, Error, Success, Empty
- **Transitions**: Events trigger state changes
- **Single Source of Truth**: ViewModel owns all state

### Unidirectional Data Flow

```
User Action → Event → ViewModel → State Update → UI Recomposition
```

## 🎨 Compose Components Overview

### Side-Effects in Composables

**Composables should be side-effect-free:**
```kotlin
@Composable
fun UserProfile(user: User) {
    // ❌ Side-effect: Network call in composable
    val userData = remember { fetchUserData() }

    // ✅ Side-effect-free: UI description only
    Text("Hello ${user.name}")
}
```

**Use Effects for Side-Effects:**
```kotlin
@Composable
fun UserProfile(userId: String) {
    val user = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        user.value = fetchUserData(userId)
    }

    user.value?.let { UserCard(it) }
}
```

### Modifiers

**Modifier chains** for styling and behavior:
```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Blue)
        .clickable { /* handle click */ }
        .fillMaxWidth()
)
```

### CompositionLocal

**Dependency injection** for composables:
```kotlin
// Provider
CompositionLocalProvider(LocalUser provides currentUser) {
    UserProfile()
}

// Consumer
@Composable
fun UserProfile() {
    val user = LocalUser.current
    Text("Hello ${user.name}")
}
```

## 🔄 Lifecycle Overview

### Compose Phases

1. **Composition** - Build UI tree from composables
2. **Layout** - Measure and position components
3. **Drawing** - Render to screen

### Lifecycle States

- **Enter** - Composable enters composition
- **Recompose** - State changes trigger recomposition (0+ times)
- **Leave** - Composable leaves composition

### Lifecycle-Aware Effects

```kotlin
@Composable
fun UserProfile(userId: String) {
    // ✅ Correct: Runs on enter, cancels on leave
    DisposableEffect(userId) {
        val job = launchDataLoad()
        onDispose { job.cancel() }
    }

    // ✅ Correct: Survives recomposition
    val coroutineScope = rememberCoroutineScope()
}
```

## 🌐 Compose Interoperability

### Using Compose in XML Views

```kotlin
// In XML layout
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/compose_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// In Activity
composeView.apply {
    setViewCompositionStrategy(
        ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
    )
    setContent {
        UserList()
    }
}
```

### ViewCompositionStrategy Options

1. **`DisposeOnDetachedFromWindowOrReleasedFromPool`**
   - Disposes when View detached or released from pool
   - Default for ComposeView

2. **`DisposeOnLifecycleDestroyed`**
   - Disposes when LifecycleOwner destroyed
   - Use for Fragments or Activities

3. **`DisposeOnViewTreeLifecycleDestroyed`**
   - Disposes when ViewTreeLifecycleOwner destroyed

## ⚡ Function Behavior in Compose

### Execution Characteristics

- **Any Order**: Composables can execute in any order
- **Parallel Execution**: Composables can run in parallel
- **Recomposability**: Functions may be skipped or cancelled during recomposition

### Best Practices

```kotlin
@Composable
fun ExpensiveComposable() {
    // ❌ Bad: Expensive work on every recomposition
    val data = computeExpensiveValue()

    // ✅ Good: Remember expensive computations
    val data = remember { computeExpensiveValue() }

    // ✅ Good: Use keys for conditional remember
    val data = remember(key) { computeExpensiveValue() }
}
```

**Keep composables:**
- **Fast** - No heavy computations
- **Idempotent** - Same result every time
- **Side-effect-free** - No external mutations

## 📚 Learning and Best Practices

### ViewModel State Ownership

```kotlin
class UserViewModel : ViewModel() {
    // ✅ ViewModel owns state
    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.LoadUser -> loadUser()
            is UserEvent.UpdateUser -> updateUser(event.user)
        }
    }
}
```

### State Modification Pattern

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    // ✅ UI observes state, triggers events
    Column {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            UserContent(state.user)
        }

        Button(onClick = { viewModel.onEvent(UserEvent.Refresh) }) {
            Text("Refresh")
        }
    }
}
```

### Calculation Separation

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    // ❌ Bad: Calculation in composable
    val displayName = "${state.user.firstName} ${state.user.lastName}".uppercase()

    // ✅ Good: Calculation in ViewModel
    val displayName = state.displayName

    Text(displayName)
}
```

### Smaller Composables with Previews

```kotlin
@Composable
fun UserProfile(user: User) {
    Column {
        UserHeader(user)
        UserDetails(user)
        UserActions(user)
    }
}

@Preview
@Composable
fun UserProfilePreview() {
    UserProfile(User("John", "Doe", "john@example.com"))
}
```

### Lambda Passing for Frequent Updates

```kotlin
// ✅ Good: Pass lambda for frequent updates
@Composable
fun UserList(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn {
        items(users) { user ->
            UserItem(user, onUserClick)
        }
    }
}
```

### rememberSaveable Usage

```kotlin
@Composable
fun UserForm() {
    // ✅ Use rememberSaveable for form state
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    // ❌ Don't use rememberSaveable for ViewModel state
    // Use ViewModel for complex state management
}
```

## ⚖️ Performance Comparison

### Build Time Impact

- **Initial Migration**: Temporary APK/build time increase
- **Long-term**: Compose eliminates XML inflation overhead
- **Overall**: Eventually surpasses View system performance

### Runtime Performance

- **Single Layout Pass**: Compose enforces single layout pass
- **Efficient Recomputation**: Only changed composables recompose
- **Reduced Overhead**: No View inflation, direct Kotlin execution

## 🎉 Benefits Summary

### Developer Experience

- **Intuitive**: Declarative syntax matches mental model
- **Less Boilerplate**: No findViewById, ViewHolders, adapters
- **Fewer Bugs**: Less state synchronization issues
- **Better Testing**: FSM nature enables comprehensive testing

### Code Quality

- **Modular**: Small, reusable composables
- **Type-Safe**: Kotlin compiler catches UI errors
- **Concurrent-Safe**: Reduced race conditions
- **Maintainable**: Clear separation of concerns

### Productivity

- **Faster Development**: Less code, faster iteration
- **Better Debugging**: Clear data flow, easier issue tracking
- **Improved Testing**: Easier unit and integration testing

## 🚀 Getting Started

1. **Clone the repository**
2. **Open in Android Studio**
3. **Run the application**
4. **Explore the architecture layers**

## 📖 Key Files

- `domain/usecases/UseCase.kt` - Base use case interface
- `domain/entities/User.kt` - Domain entity example
- `data/repositories/UserRepository.kt` - Repository interface
- `presentation/viewmodels/UserListViewModel.kt` - ViewModel with state
- `presentation/ui/UserListScreen.kt` - Declarative UI example

## 🤝 Contributing

This project demonstrates modern Android development patterns. Feel free to explore, learn, and contribute!

---

**Built with ❤️ using Jetpack Compose and Clean Architecture**

