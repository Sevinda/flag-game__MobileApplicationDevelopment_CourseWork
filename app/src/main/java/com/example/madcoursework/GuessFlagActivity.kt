package com.example.madcoursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcoursework.ui.theme.MADCourseworkTheme
import com.example.madcoursework.ui.theme.MCQButtonDark
import com.example.madcoursework.ui.theme.MCQButtonLight
import com.example.madcoursework.utils.convertJsonStringToMap
import kotlinx.coroutines.delay
import kotlin.random.Random

class GuessFlagActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADCourseworkTheme {
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

                    GuessFlagScreen(
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 15.dp
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
private fun GuessFlagScreen(
    modifier: Modifier,
    countryMap: Map<String, String>,
    listOfFlagIDs: List<Int>,
    isChecked: Boolean
) {
    val orientation = LocalConfiguration.current.orientation

    var randomFlagID by rememberSaveable { mutableIntStateOf(0) } // THE RANDOM FLAG ID OF THE RANDOM FLAG
    var randomFlagKey by rememberSaveable { mutableStateOf<String?>(null) } // THE RANDOM FLAG KEY -> i.e. => lk
    var randomSelectedCountry by rememberSaveable { mutableStateOf("") } // CORRECT RANDOM FLAG

    var setIsCorrect by rememberSaveable { mutableStateOf("") }
    val flagList = rememberSaveable { mutableListOf<Int>() } // FLAG LIST TO STORE ALL THE FLAGS
    val selectedOption = rememberSaveable { mutableStateOf("") }

    val submitText by rememberSaveable { mutableStateOf("Next") }
    var correctIndex by rememberSaveable { mutableIntStateOf(0) }
    var submitButtonNext by rememberSaveable { mutableStateOf(true) }
    var actualAnswer by rememberSaveable { mutableStateOf("") } // AFTER SUBMIT TEXT THE RESULT THAT POPS UP

    if (randomFlagID == 0) {
        val randomIndex = Random.nextInt(listOfFlagIDs.size)
        randomFlagID = listOfFlagIDs[randomIndex]
        randomFlagKey = countryMap.entries.elementAt(randomIndex).key
        randomSelectedCountry = countryMap[randomFlagKey]!!

        var wrongRandomFlag1: Int
        var wrongRandomFlag2: Int
        while (true) {
            val wrongRandomIndex1 = Random.nextInt(listOfFlagIDs.size)
            wrongRandomFlag1 = listOfFlagIDs[wrongRandomIndex1]
            val wrongRandomIndex2 = Random.nextInt(listOfFlagIDs.size)
            wrongRandomFlag2 = listOfFlagIDs[wrongRandomIndex2]
            if ((randomIndex != wrongRandomIndex1) && (randomIndex != wrongRandomIndex2) && (wrongRandomIndex1 != wrongRandomIndex2))
                break
        }
        val flagsToAdd = listOf(randomFlagID, wrongRandomFlag1, wrongRandomFlag2)
        flagList.addAll(flagsToAdd.shuffled()) // SHUFFLING AND ADDING THE FLAGS INTO THE LIST
        correctIndex = flagList.indexOf(randomFlagID)
    }

    var timerValue by rememberSaveable { mutableFloatStateOf(10f) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isChecked) {
            LaunchedEffect(randomFlagID) {
                while (timerValue > 0 && setIsCorrect == "") {
                    delay(1000L)
                    timerValue -= 1f

                    if (timerValue == 0f) {
                        if (selectedOption.value != "") {
                            setIsCorrect =
                                if (flagList[selectedOption.value.toInt() - 1] == randomFlagID) " - CORRECT!" else " - WRONG!"
                        } else {
                            selectedOption.value = " "
                            setIsCorrect = " - WRONG!"
                        }
                        submitButtonNext = false

                        actualAnswer = "Correct option was option number: ${correctIndex + 1}"
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
        Text(
            text = "Guess the Flag",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )
        if (orientation == 1) {
            Text(
                text = randomSelectedCountry,
                modifier = Modifier.padding(top = 40.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            for (flag in flagList) {
                FlagContainer(
                    count = flagList.indexOf(flag),
                    randomFlagID = flag,
                    onOptionSelected = {
                        selectedOption.value = (it.toInt() + 1).toString()
                    },
                    isEnabled = submitButtonNext
                )
                Spacer(modifier = Modifier.size(height = 15.dp, width = 10.dp))
            }
        }
        if (orientation == 2) {
            Text(
                text = randomSelectedCountry,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                item {
                    for (flag in flagList) {
                        FlagContainer(
                            count = flagList.indexOf(flag),
                            randomFlagID = flag,
                            onOptionSelected = {
                                selectedOption.value = (it.toInt() + 1).toString()
                            },
                            isEnabled = submitButtonNext
                        )
                        Spacer(modifier = Modifier.size(height = 15.dp, width = 10.dp))
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column {
                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(text = "Selected Option: ${selectedOption.value}")
                    Text(
                        text = setIsCorrect,
                        color = if (setIsCorrect == " - CORRECT!")
                            Color.Green else Color.Red
                    )
                }
                if (!setIsCorrect.contains("CORRECT")) {
                    Text(
                        text = actualAnswer,
                        modifier = Modifier.padding(bottom = 10.dp),
                        color = Color(0xFF2196F3)
                    )
                }
                Button(
                    onClick = {
                        if (selectedOption.value != "" && setIsCorrect == "") {
                            setIsCorrect =
                                if (flagList[selectedOption.value.toInt() - 1] == randomFlagID) " - CORRECT!" else " - WRONG!"
                            submitButtonNext = false
                            actualAnswer = "Correct option was option number: ${correctIndex + 1}"
                        } else {
                            flagList.clear()
                            selectedOption.value = ""
                            setIsCorrect = ""
                            randomFlagID = 0
                            actualAnswer = ""
                            submitButtonNext = true
                            timerValue = 10f
                        }
                    },
                    enabled = selectedOption.value != "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = submitText, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun FlagContainer(
    count: Int,
    randomFlagID: Int,
    onOptionSelected: (String) -> Unit,
    isEnabled: Boolean
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(width = 270.dp, height = 160.dp),
        onClick = {
            onOptionSelected(count.toString())
        },
        colors =
        if (isSystemInDarkTheme())
            ButtonDefaults.buttonColors(MCQButtonDark)
        else
            ButtonDefaults.buttonColors(MCQButtonLight),
        shape = RoundedCornerShape(5),
        enabled = isEnabled
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${count + 1}. ",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(0.1f),
                color = MaterialTheme.colorScheme.onTertiary
            )
            Image(
                painter = painterResource(id = randomFlagID),
                contentDescription = "description",
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(1f),
                contentScale = ContentScale.FillHeight,
            )
        }
    }
}
