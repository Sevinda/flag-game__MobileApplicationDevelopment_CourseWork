package com.example.madcoursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcoursework.ui.theme.MADCourseworkTheme
import com.example.madcoursework.utils.convertJsonStringToMap
import kotlinx.coroutines.delay
import kotlin.random.Random

class GuessCountryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADCourseworkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val extras = intent.extras
                    val countryJsonString: String
                    var countryMap: Map<String, String> = emptyMap()
                    var listOfFlagIDs: List<Int> = emptyList()
                    var isChecked = false
                    if (extras != null) {
                        countryJsonString = extras.getString("countryJsonStringData").toString()
                        countryMap = convertJsonStringToMap(countryJsonString)
                        listOfFlagIDs =
                            extras.getIntegerArrayList("listOfFlagIDsIntentData") as List<Int>
                        isChecked = extras.getBoolean("isChecked")
                    }
                    GuessCountryScreen(
                        modifier = Modifier.padding(
                            horizontal = 20.dp, vertical = 5.dp
                        ),
                        countryMap = countryMap,
                        listOfFlagIDs = listOfFlagIDs,
                        isChecked = isChecked
                    )
                }
            }
        }
    }
}

@Composable
private fun GuessCountryScreen(
    modifier: Modifier,
    countryMap: Map<String, String>,
    listOfFlagIDs: List<Int>,
    isChecked: Boolean
) {
    val orientation = LocalConfiguration.current.orientation

    var randomFlagID by rememberSaveable { mutableIntStateOf(0) } // THE RANDOM FLAG ID OF THE RANDOM FLAG
    var randomFlagKey by rememberSaveable { mutableStateOf<String?>(null) } // THE RANDOM FLAG KEY -> i.e. => lk
    var userSelectedCountry by rememberSaveable { mutableStateOf("Select a country") }

    var setIsCorrect by rememberSaveable { mutableStateOf("") } // CORRECT! AND WRONG! TEXT
    var isButtonsEnabled by rememberSaveable { mutableStateOf(true) }
    var isSubmitButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var correctCountry by rememberSaveable { mutableStateOf("") } // THE COUNTRY NAME OF THE RANDOM FLAG -> i.e. => Sri Lanka
    var submitText by rememberSaveable { mutableStateOf("Submit") }

    // SELECTING A RANDOM IMAGE ONLY IF A RANDOM FLAG HAS NOT BEEN SELECTED YET
    if (randomFlagID == 0) {
        val randomIndex = Random.nextInt(listOfFlagIDs.size)
        randomFlagID = listOfFlagIDs[randomIndex]
        randomFlagKey = countryMap.entries.elementAt(randomIndex).key
    }

    // AN INNER FUNCTION TO HANDLE ON SUBMIT EITHER WHEN THE BUTTON IS CLICKED OR THE TIME RUNS OUT
    fun handleOnSubmit() {
        correctCountry = countryMap[randomFlagKey].toString()
        isButtonsEnabled = false
        setIsCorrect =
            if (countryMap.entries.find { it.value == userSelectedCountry }?.key == randomFlagKey) " - CORRECT!" else " - WRONG!"
        println("USER SELECTED COUNTRY: $userSelectedCountry")
        submitText = "Next"
    }

    println("CHOSEN COUNTRY ${countryMap[randomFlagKey].toString()}")

    var timerValue by rememberSaveable { mutableFloatStateOf(10f) }

    Column(modifier = modifier) {
        if (isChecked) {
            LaunchedEffect(randomFlagID)  {
                while (timerValue > 0 && isButtonsEnabled) {
                    delay(1000L)
                    timerValue -= 1f

                    if (timerValue == 0f) {
                        handleOnSubmit()
                        isSubmitButtonEnabled = true
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = timerValue / 10f,
                    modifier = Modifier.weight(1f)
                )
                Text(text = timerValue.toString(), modifier = Modifier.padding(start = 20.dp))
            }
        }
        if (orientation == 1) {
            Text(
                text = "Guess the Country!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = randomFlagID),
                    contentDescription = "description",
                    modifier = Modifier.padding(top = 15.dp),
                    contentScale = ContentScale.FillHeight,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 15.dp)
            ) {
                CountryItems(
                    countryMap = countryMap,
                    onCountrySelect = { country ->
                        userSelectedCountry = country
                        isSubmitButtonEnabled = true
                    },
                    isSubmitted = isButtonsEnabled
                )
            }
        }

        if (orientation == 2) {
            Row {
                Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                    Column {
                        Text(
                            text = "Guess the Country!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(180.dp)
                        ) {
                            Image(
                                painter = painterResource(id = randomFlagID),
                                contentDescription = "description",
                                modifier = Modifier.padding(top = 15.dp),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(0.6f)
                        .weight(1f)
                        .padding(start = 15.dp)
                ) {
                    CountryItems(
                        countryMap = countryMap,
                        onCountrySelect = { country ->
                            userSelectedCountry = country
                            isSubmitButtonEnabled = true
                        },
                        isSubmitted = isButtonsEnabled
                    )
                }
            }
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = userSelectedCountry,
                    textDecoration = TextDecoration.Underline
                )
                Text(
                    text = setIsCorrect,
                    color = if (setIsCorrect == " - CORRECT!")
                        Color.Green else Color.Red
                )
            }
            if (userSelectedCountry != correctCountry)
                Text(
                    text = correctCountry,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
        }

        Button(
            onClick = {
                if (submitText == "Submit") {
                    handleOnSubmit()
                } else {
                    randomFlagID = 0
                    timerValue = 10f
                    isButtonsEnabled = true
                    submitText = "Submit"
                    isSubmitButtonEnabled = false
                    setIsCorrect = ""
                    correctCountry = ""
                    userSelectedCountry = "Select a country"
                }
            },
            enabled = isSubmitButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = submitText, fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun CountryItems(
    countryMap: Map<String, String>,
    onCountrySelect: (String) -> Unit,
    isSubmitted: Boolean
) {
    LazyColumn {
        items(
            count = countryMap.size,
            key = { index -> countryMap.keys.elementAt(index) },
            itemContent = { index ->
                val countryEntry = countryMap.entries.elementAt(index)
                SelectButton(
                    text = countryEntry.value,
                    onCountrySelect = { onCountrySelect(countryEntry.value) },
                    isSubmitted = isSubmitted
                )
            }
        )
    }
}

@Composable
private fun SelectButton(text: String, onCountrySelect: (String) -> Unit, isSubmitted: Boolean) {
    Button(
        onClick = { onCountrySelect(text) },
        enabled = isSubmitted,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(text = text, fontSize = 17.sp)
    }
}
