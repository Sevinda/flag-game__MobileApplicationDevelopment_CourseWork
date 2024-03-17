package com.example.madcoursework

// COURSEWORK

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcoursework.ui.theme.MADCourseworkTheme
import com.example.madcoursework.utils.addResourcesToList
import com.example.madcoursework.utils.convertJsonStringToMap
import com.example.madcoursework.utils.readJsonFromAssets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADCourseworkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        modifier = Modifier.padding(
                            horizontal = 20.dp, vertical = 15.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(modifier: Modifier) {
    val resources = LocalContext.current.resources

    val countryJsonString = readJsonFromAssets(LocalContext.current, "countries.json")
    val countryMap = convertJsonStringToMap(countryJsonString)
    val listOfFlagIDs = addResourcesToList(countryMap, resources, LocalContext.current.packageName)

    Column(
        modifier = modifier.padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select a game to play", fontSize = 28.sp, fontWeight = FontWeight.Bold
        )

        CustomButton(
            text = "Guess the Country",
            modifier = Modifier.padding(top = 40.dp),
            index = 0,
            countryJsonString = countryJsonString,
            listOfFlagIDsIntent = listOfFlagIDs
        )
        CustomButton(
            text = "Guess-Hints",
            modifier = Modifier.padding(top = 18.dp),
            index = 1,
            countryJsonString = countryJsonString,
            listOfFlagIDsIntent = listOfFlagIDs
        )
        CustomButton(
            text = "Guess the Flag",
            modifier = Modifier.padding(top = 18.dp),
            index = 2,
            countryJsonString = countryJsonString,
            listOfFlagIDsIntent = listOfFlagIDs
        )
        CustomButton(
            text = "Advanced Level",
            modifier = Modifier.padding(top = 18.dp),
            index = 3,
            countryJsonString = countryJsonString,
            listOfFlagIDsIntent = listOfFlagIDs
        )
    }
}

@Composable
private fun CustomButton(
    text: String,
    modifier: Modifier,
    index: Int,
    countryJsonString: String,
    listOfFlagIDsIntent: MutableList<Int>
) {
    val context = LocalContext.current
    Button(
        onClick = { chooseActivity(index, context, countryJsonString, listOfFlagIDsIntent) },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

private fun chooseActivity(
    index: Int,
    context: Context,
    countryJsonString: String,
    listOfFlagIDsIntent: MutableList<Int>
) {
    when (index) {
        0 -> changeActivity(
            context,
            GuessCountryActivity::class.java,
            countryJsonString,
            listOfFlagIDsIntent
        )

        1 -> changeActivity(
            context,
            GuessHintsActivity::class.java,
            countryJsonString,
            listOfFlagIDsIntent
        )

        2 -> changeActivity(
            context,
            GuessFlagActivity::class.java,
            countryJsonString,
            listOfFlagIDsIntent
        )

        3 -> changeActivity(
            context,
            AdvancedLevelActivity::class.java,
            countryJsonString,
            listOfFlagIDsIntent
        )
    }
}

private fun changeActivity(
    context: Context,
    newActivity: Class<*>,
    countryJsonString: String,
    listOfFlagIDsIntent: MutableList<Int>
) {
    val intent = Intent(context, newActivity)
    intent.putIntegerArrayListExtra(
        "listOfFlagIDsIntentData",
        listOfFlagIDsIntent as ArrayList<Int>
    )
    intent.putExtra("countryJsonStringData", countryJsonString)
    context.startActivity(intent)
}
