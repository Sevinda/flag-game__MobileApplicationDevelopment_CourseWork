package com.example.madcoursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcoursework.ui.theme.MADCourseworkTheme
import com.example.madcoursework.ui.theme.PrimaryBlue
import com.example.madcoursework.utils.convertJsonStringToMap
import kotlinx.coroutines.delay
import kotlin.random.Random

class GuessHintsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADCourseworkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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

                    GuessHintsScreen(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
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
private fun GuessHintsScreen(
    modifier: Modifier,
    countryMap: Map<String, String>,
    listOfFlagIDs: List<Int>,
    isChecked: Boolean
) {
    val orientation = LocalConfiguration.current.orientation

    var randomFlagID by rememberSaveable { mutableIntStateOf(0) } // THE RANDOM FLAG ID OF THE RANDOM FLAG
    var randomFlagKey by rememberSaveable { mutableStateOf<String?>(null) } // THE RANDOM FLAG KEY -> i.e. => lk
    var randomSelectedCountry by rememberSaveable { mutableStateOf("") }

    var blanks by rememberSaveable { mutableStateOf("") }
    var guess by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableIntStateOf(1) }

    val incorrectGuesses = rememberSaveable { mutableListOf<String>() }
    var isSubmitBtnEnabled by rememberSaveable { mutableStateOf(false) }
    var submitText by rememberSaveable { mutableStateOf("Submit") }
    var setIsCorrect by rememberSaveable { mutableStateOf("") }

    // SELECTING A RANDOM IMAGE ONLY IF A RANDOM FLAG HAS NOT BEEN SELECTED YET
    if (randomFlagID == 0) {
        val randomIndex = Random.nextInt(listOfFlagIDs.size)
        randomFlagID = listOfFlagIDs[randomIndex]
        randomFlagKey = countryMap.entries.elementAt(randomIndex).key
        randomSelectedCountry = countryMap[randomFlagKey]!!

        for (letter in randomSelectedCountry) {
            blanks +=
                if (letter != ' ') "_"
                else " "
        }
    }

    var timerValue by rememberSaveable { mutableFloatStateOf(10f) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isChecked) {
                LaunchedEffect(randomFlagID) {
                    while (timerValue > 0 && submitText == "Submit") {
                        delay(1000L)
                        timerValue -= 1f
                    }

                    if (timerValue == 0f) {
                        val sizeOfArray = incorrectGuesses.size
                        for (i in 1..(3-sizeOfArray)) {
                            incorrectGuesses.add("")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    text = "Guess-Hints!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
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

                TextField(
                    value = guess,
                    onValueChange = {
                        if (it.length <= 1) guess = it
                    },
                    label = { Text(text = "Enter your guess") },
                    modifier = Modifier
                        .padding(top = 50.dp, bottom = 10.dp)
                        .size(180.dp, 50.dp)
                )

                Text(text = blanks)

                Row(modifier = Modifier.padding(top = 40.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                    ) {
                        Column {
                            Text(
                                text = "Selected Letter",
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            if (guess != "") {
                                Text(
                                    text = guess,
                                    modifier = Modifier
                                        .background(PrimaryBlue)
                                        .padding(vertical = 7.dp, horizontal = 15.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                    ) {
                        Column {
                            Text(
                                text = "Incorrect Guesses",
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            IncorrectGuesses(incorrectGuess = incorrectGuesses)
                        }
                    }
                }
            }

            if (orientation == 2) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    ) {
                        Column {
                            Text(
                                text = "Guess-Hints!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(180.dp),
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
                            .fillMaxHeight(0.75f)
                            .weight(1f)
                            .padding(start = 15.dp)
                    ) {
                        Column {
                            TextField(
                                value = guess,
                                onValueChange = {
                                    if (it.length <= 1) guess = it
                                },
                                label = { Text(text = "Enter your guess") },
                                modifier = Modifier
                                    .size(250.dp, 65.dp)
                                    .padding(bottom = 10.dp)
                            )

                            Text(
                                text = blanks,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            Row {
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                ) {
                                    Column {
                                        Text(
                                            text = "Selected Letter",
                                            modifier = Modifier.padding(bottom = 15.dp)
                                        )
                                        if (guess != "") {
                                            Text(
                                                text = guess,
                                                modifier = Modifier
                                                    .background(PrimaryBlue)
                                                    .padding(vertical = 7.dp, horizontal = 15.dp)
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                ) {
                                    Column {
                                        Text(
                                            text = "Incorrect Guesses",
                                            modifier = Modifier.padding(bottom = 15.dp)
                                        )
                                        IncorrectGuesses(incorrectGuess = incorrectGuesses)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        isSubmitBtnEnabled = guess != ""
        setIsCorrect =
            if (randomSelectedCountry.uppercase() == blanks.uppercase()) "CORRECT!"
            else "WRONG!"
        if (incorrectGuesses.size >= 3 || setIsCorrect.contains("CORRECT")) {
            submitText = "Next"
            isSubmitBtnEnabled = true

            Row {
                Text(
                    text = setIsCorrect,
                    color = if (setIsCorrect == "CORRECT!") Color.Green else Color.Red,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                if (setIsCorrect != "CORRECT!")
                    Text(
                        text = "\t-\tThe correct is \"$randomSelectedCountry\"",
                        color = Color(0xFF2196F3),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
            }
        }

        Button(
            onClick = {
                if (incorrectGuesses.size < 3 && submitText == "Submit") {
                    println(randomSelectedCountry)
                    val guessUppercase = guess.uppercase()
                    if (guessUppercase in randomSelectedCountry.uppercase()) {
                        val updatedBlanks = blanks.toCharArray()
                        for (i in randomSelectedCountry.indices) {
                            if (randomSelectedCountry[i].uppercaseChar() == guessUppercase[0] && updatedBlanks[i] == '_') {
                                updatedBlanks[i] = randomSelectedCountry[i].uppercaseChar()
                            }
                        }
                        blanks = String(updatedBlanks)
                    } else {
                        incorrectGuesses.add(guess)
                        count++
                    }
                    guess = ""
                } else {
                    timerValue = 10f
                    submitText = "Submit"
                    randomFlagID = 0
                    isSubmitBtnEnabled = false
                    incorrectGuesses.clear()
                    blanks = ""
                    guess = ""
                    setIsCorrect = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 16.dp, top = 10.dp)
                .align(Alignment.End),
            shape = RoundedCornerShape(12.dp),
            enabled = isSubmitBtnEnabled
        ) {
            Text(text = submitText, fontSize = 17.sp)
        }
    }
}

@Composable
private fun IncorrectGuesses(incorrectGuess: MutableList<String>) {
    Row {
        for (guess in incorrectGuess) {
            if (guess == "") continue
            Text(
                text = guess,
                modifier = Modifier
                    .background(PrimaryBlue)
                    .padding(vertical = 7.dp, horizontal = 15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
