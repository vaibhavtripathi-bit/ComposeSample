package com.globallogic.composesample.recompositionexamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * DEMO ACTIVITY
 *
 * Shows how to integrate recomposition examples into a real Android Activity.
 * Copy this pattern to use the examples in your own projects.
 */
class RecompositionDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    RecompositionDemoApp()
                }
            }
        }
    }
}

@Composable
fun RecompositionDemoApp() {
    var currentExample by remember { mutableStateOf("basic") }

    Column {
        // Navigation
        ExampleSelector { selectedExample ->
            currentExample = selectedExample
        }

        // Content based on selection
        when (currentExample) {
            "basic" -> BasicConceptsDemo()
            "stability" -> StabilityExamplesDemo()
            "state" -> StateManagementDemo()
            "performance" -> PerformanceDemo()
            "pitfalls" -> PitfallsDemo()
            "complete" -> TodoApp()
            "dynamic" -> DynamicListDemo()
            else -> BasicConceptsDemo()
        }
    }
}

@Composable
private fun ExampleSelector(onExampleSelected: (String) -> Unit) {
    Column {
        Text("Choose Example:")

        Button(onClick = { onExampleSelected("basic") }) {
            Text("1. Basic Concepts")
        }

        Button(onClick = { onExampleSelected("stability") }) {
            Text("2. Stability Examples")
        }

        Button(onClick = { onExampleSelected("state") }) {
            Text("3. State Management")
        }

        Button(onClick = { onExampleSelected("performance") }) {
            Text("4. Performance")
        }

        Button(onClick = { onExampleSelected("pitfalls") }) {
            Text("5. Common Pitfalls")
        }

        Button(onClick = { onExampleSelected("complete") }) {
            Text("6. Complete Todo App")
        }

        Button(onClick = { onExampleSelected("dynamic") }) {
            Text("7. Dynamic List Management")
        }
    }
}

// Demo wrappers that call the actual examples
@Composable
private fun BasicConceptsDemo() {
    Column {
        Text("BASIC RECOMPOSITION CONCEPTS")
        SimpleCounter()
        MultipleStates()
        ConditionalUI()
    }
}

@Composable
private fun StabilityExamplesDemo() {
    Column {
        Text("DATA STABILITY EXAMPLES")
        StabilityDemo()
        CollectionStabilityDemo()
    }
}

@Composable
private fun StateManagementDemo() {
    Column {
        Text("STATE MANAGEMENT PATTERNS")
        BasicStateExample()
        ComplexStateExample()
        StateHoistingExample()
    }
}

@Composable
private fun PerformanceDemo() {
    Column {
        Text("PERFORMANCE OPTIMIZATIONS")
        ExpensiveComputationExample()
        // Use the User class from PerformanceOptimizations.kt
        val performanceUser = User("1", "Sample", "sample@example.com", 25)
        DerivedStateExample(listOf(performanceUser))
        KeysExample(listOf(performanceUser))
    }
}

@Composable
private fun PitfallsDemo() {
    Column {
        Text("COMMON PITFALLS & SOLUTIONS")
        UnstableCollectionPitfall()
        ExpensiveComputationPitfall()
        CorrectStateUpdateSolution()
    }
}

@Composable
private fun DynamicListDemo() {
    Column {
        Text("DYNAMIC LIST MANAGEMENT")
        com.globallogic.composesample.recompositionexamples.dynamiclist.ui.PersonListScreen()
    }
}

/**
 * HOW TO USE IN YOUR PROJECT:
 *
 * 1. Copy the example files to your project
 * 2. Add this activity to your AndroidManifest.xml
 * 3. Launch this activity to see examples in action
 * 4. Modify the examples to fit your specific use cases
 * 5. Test performance differences between good and bad patterns
 *
 * Example AndroidManifest entry:
 * <activity
 *     android:name=".examples.RecompositionDemoActivity"
 *     android:exported="true">
 *     <intent-filter>
 *         <action android:name="android.intent.action.MAIN" />
 *         <category android:name="android.intent.category.LAUNCHER" />
 *     </intent-filter>
 * </activity>
 */
