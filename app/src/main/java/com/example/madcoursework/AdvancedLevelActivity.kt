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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcoursework.ui.theme.MADCourseworkTheme
import com.example.madcoursework.utils.convertJsonStringToMap
import kotlinx.coroutines.delay
import kotlin.random.Random

class AdvancedLevelActivity : ComponentActivity() {
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

                    AdvancedLevelScreen(
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
private fun AdvancedLevelScreen(
    modifier: Modifier,
    countryMap: Map<String, String>,
    listOfFlagIDs: List<Int>,
    isChecked: Boolean
) {
    val orientation = LocalConfiguration.current.orientation

    var randomFlagID1 by rememberSaveable { mutableIntStateOf(0) } // THE RANDOM FLAG ID OF THE RANDOM FLAG
    var randomFlagKey1 by rememberSaveable { mutableStateOf<String?>(null) } // THE RANDOM FLAG KEY -> i.e. => lk
    var randomFlagID2 by rememberSaveable { mutableIntStateOf(0) }
    var randomFlagKey2 by rememberSaveable { mutableStateOf<String?>(null) }
    var randomFlagID3 by rememberSaveable { mutableIntStateOf(0) }
    var randomFlagKey3 by rememberSaveable { mutableStateOf<String?>(null) }

    var randomSelectedCountry1 by rememberSaveable { mutableStateOf("") }
    var randomSelectedCountry2 by rememberSaveable { mutableStateOf("") }
    var randomSelectedCountry3 by rememberSaveable { mutableStateOf("") }

    var guessCountry1: String by rememberSaveable { mutableStateOf("") }
    var guessCountry2: String by rememberSaveable { mutableStateOf("") }
    var guessCountry3: String by rememberSaveable { mutableStateOf("") }

    var submitText by rememberSaveable { mutableStateOf("Submit") }
    val submitCounter = rememberSaveable { mutableIntStateOf(1) }

    val isAnswer1Correct = rememberSaveable { mutableStateOf(false) }
    val isAnswer2Correct = rememberSaveable { mutableStateOf(false) }
    val isAnswer3Correct = rememberSaveable { mutableStateOf(false) }

    val pointCounter = rememberSaveable { mutableIntStateOf(0) }

    if (randomFlagID1 == 0) {
        var randomIndex1: Int
        var randomIndex2: Int
        var randomIndex3: Int

        while (true) {
            randomIndex1 = Random.nextInt(listOfFlagIDs.size)
            randomIndex2 = Random.nextInt(listOfFlagIDs.size)
            randomIndex3 = Random.nextInt(listOfFlagIDs.size)
            if ((randomIndex1 != randomIndex2) && (randomIndex1 != randomIndex3) && (randomIndex2 != randomIndex3)) break
        }

        randomFlagID1 = listOfFlagIDs[randomIndex1]
        randomFlagKey1 = countryMap.entries.elementAt(randomIndex1).key
        randomSelectedCountry1 = countryMap[randomFlagKey1]!!

        randomFlagID2 = listOfFlagIDs[randomIndex2]
        randomFlagKey2 = countryMap.entries.elementAt(randomIndex2).key
        randomSelectedCountry2 = countryMap[randomFlagKey2]!!

        randomFlagID3 = listOfFlagIDs[randomIndex3]
        randomFlagKey3 = countryMap.entries.elementAt(randomIndex3).key
        randomSelectedCountry3 = countryMap[randomFlagKey3]!!
    }

    println("COUNTRY 1 $randomSelectedCountry1")
    println("COUNTRY 2 $randomSelectedCountry2")
    println("COUNTRY 3 $randomSelectedCountry3")

    fun handleSubmit() {
        pointCounter.intValue = 0
        if (guessCountry1.lowercase() == randomSelectedCountry1.lowercase()) {
            isAnswer1Correct.value = true
            pointCounter.intValue += 1
        }
        if (guessCountry2.lowercase() == randomSelectedCountry2.lowercase()) {
            isAnswer2Correct.value = true
            pointCounter.intValue += 1
        }
        if (guessCountry3.lowercase() == randomSelectedCountry3.lowercase()) {
            isAnswer3Correct.value = true
            pointCounter.intValue += 1
        }
        submitCounter.intValue++
    }

    var timerValue by rememberSaveable { mutableFloatStateOf(10f) }

    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // REFERENCE (ANDROID) => https://developer.android.com/develop/ui/compose/side-effects
        if (isChecked) {
            LaunchedEffect(randomFlagID1) {
                while (timerValue > 0 && submitCounter.intValue <= 3) {
                    delay(1000L)
                    timerValue -= 1f

                    if (timerValue == 0f) {
                        handleSubmit()
                        submitText = "Next"
                        submitCounter.intValue = 4
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = timerValue / 10f,
                    modifier = Modifier.weight(1f)
                )
                Text(text = timerValue.toString(), modifier = Modifier.padding(start = 20.dp))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Advanced Level",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                modifier = Modifier.weight(1f)
            )
            Text(text = "${pointCounter.intValue}/3")
        }
        if (orientation == 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    FlagContainer(
                        randomFlagID = randomFlagID1,
                        guess = guessCountry1,
                        isAnswerCorrect = isAnswer1Correct.value,
                        onValueGuess = { guessCountry1 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry1
                    )
                    FlagContainer(
                        randomFlagID = randomFlagID2,
                        guess = guessCountry2,
                        isAnswerCorrect = isAnswer2Correct.value,
                        onValueGuess = { guessCountry2 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry2
                    )
                    FlagContainer(
                        randomFlagID = randomFlagID3,
                        guess = guessCountry3,
                        isAnswerCorrect = isAnswer3Correct.value,
                        onValueGuess = { guessCountry3 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry3
                    )
                }
            }
        }
        if (orientation == 2) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f),
            ) {
                item {
                    FlagContainer(
                        randomFlagID = randomFlagID1,
                        guess = guessCountry1,
                        isAnswerCorrect = isAnswer1Correct.value,
                        onValueGuess = { guessCountry1 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry1
                    )
                    FlagContainer(
                        randomFlagID = randomFlagID2,
                        guess = guessCountry2,
                        isAnswerCorrect = isAnswer2Correct.value,
                        onValueGuess = { guessCountry2 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry2
                    )
                    FlagContainer(
                        randomFlagID = randomFlagID3,
                        guess = guessCountry3,
                        isAnswerCorrect = isAnswer3Correct.value,
                        onValueGuess = { guessCountry3 = it },
                        submitCounter = submitCounter.intValue,
                        correctAnswer = randomSelectedCountry3
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.BottomCenter
        ) {
            if (isAnswer1Correct.value && isAnswer2Correct.value && isAnswer3Correct.value) submitText =
                "Next"
            if (submitCounter.intValue > 3) {
                submitText = "Next"
            }
            Column {
                if (submitCounter.intValue > 3) {
                    if (isAnswer1Correct.value && isAnswer2Correct.value && isAnswer3Correct.value) Text(
                        text = "CORRECT!",
                        color = Color.Green
                    )
                    else Text(text = "WRONG!", color = Color.Red)
                } else {
                    Text(
                        text = "Number of chances left: ${4 - submitCounter.intValue}",
                    )
                }

                Button(
                    onClick = {
                        if (submitText == "Submit") {
                            handleSubmit()
                        } else {
                            submitCounter.intValue = 1
                            pointCounter.intValue = 0
                            submitText = "Submit"
                            timerValue = 10f
                            randomFlagID1 = 0

                            guessCountry1 = ""
                            guessCountry2 = ""
                            guessCountry3 = ""

                            isAnswer1Correct.value = false
                            isAnswer2Correct.value = false
                            isAnswer3Correct.value = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(text = submitText, fontSize = 17.sp)
                }
            }
        }
    }
}

@Composable
private fun FlagContainer(
    randomFlagID: Int,
    guess: String,
    isAnswerCorrect: Boolean,
    onValueGuess: (String) -> Unit,
    submitCounter: Int,
    correctAnswer: String
) {
    var textFieldColor: Color = Color.White
    var isEnabled by rememberSaveable { mutableStateOf(true) }

    if (submitCounter == 1) isEnabled = true

    if (submitCounter > 1) {
        textFieldColor = if (isAnswerCorrect) Color.Green
        else Color.Red
    }

    if (isAnswerCorrect) isEnabled = false
    if (submitCounter > 3) isEnabled = false

    if (LocalConfiguration.current.orientation == 1) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .size(160.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = randomFlagID),
                contentDescription = "description",
                modifier = Modifier.padding(top = 15.dp),
                contentScale = ContentScale.FillHeight,
            )
        }
        Text(
            text = guess, modifier = Modifier.padding(vertical = 10.dp)
        )
        if (submitCounter > 3 && !isAnswerCorrect) Text(
            text = correctAnswer,
            color = Color(0xFF2196F3)
        )
        TextField(
            value = guess,
            onValueChange = {
                onValueGuess(it)
            },
            label = { Text(text = "Enter your guess") },
            modifier = Modifier.padding(bottom = 30.dp),
            enabled = isEnabled,
            textStyle = TextStyle(
                fontSize = 18.sp, color = textFieldColor
            )
        )
    } else {
        LazyColumn(modifier = Modifier.padding(end = 25.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 280.dp, height = 180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = randomFlagID),
                        contentDescription = "description",
                        modifier = Modifier.padding(top = 15.dp),
                        contentScale = ContentScale.FillHeight,
                    )
                }
                Text(
                    text = guess, modifier = Modifier.padding(vertical = 10.dp)
                )
                if (submitCounter > 3 && !isAnswerCorrect) Text(
                    text = correctAnswer,
                    color = Color(0xFF2196F3)
                )
                TextField(
                    value = guess,
                    onValueChange = {
                        onValueGuess(it)
                    },
                    label = { Text(text = "Enter your guess") },
                    modifier = Modifier.padding(bottom = 30.dp),
                    enabled = isEnabled,
                    textStyle = TextStyle(
                        fontSize = 18.sp, color = textFieldColor
                    )
                )
            }
        }
    }
}
