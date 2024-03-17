package com.example.madcoursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.madcoursework.utils.convertJsonStringToMap
import kotlin.random.Random

class AdvancedLevelActivity : ComponentActivity() {
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
                    if (extras != null) {
                        countryJsonString = extras.getString("countryJsonStringData").toString()
                        countryMap = convertJsonStringToMap(countryJsonString)
                        listOfFlagIDs =
                            extras.getIntegerArrayList("listOfFlagIDsIntentData") as List<Int>
                    }

                    AdvancedLevelScreen(
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 15.dp
                        ),
                        countryMap = countryMap,
                        listOfFlagIDs = listOfFlagIDs
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
    listOfFlagIDs: List<Int>
) {
    val orientation = LocalConfiguration.current.orientation

    var randomFlagID by rememberSaveable { mutableIntStateOf(0) } // THE RANDOM FLAG ID OF THE RANDOM FLAG
    var randomFlagKey by rememberSaveable { mutableStateOf<String?>(null) } // THE RANDOM FLAG KEY -> i.e. => lk

    var randomSelectedCountry by rememberSaveable { mutableStateOf("") }

    if (randomFlagID == 0) {
        val randomIndex = Random.nextInt(listOfFlagIDs.size)
        randomFlagID = listOfFlagIDs[randomIndex]
        randomFlagKey = countryMap.entries.elementAt(randomIndex).key
        randomSelectedCountry = countryMap[randomFlagKey]!!
    }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Advanced Level",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )
        if (orientation == 1) {
            Column {
                FlagContainer(
                    randomFlagID = randomFlagID,
                )
                FlagContainer(
                    randomFlagID = randomFlagID,
                )
                FlagContainer(
                    randomFlagID = randomFlagID,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Submit", fontSize = 17.sp)
            }
        }
    }
}


@Composable
private fun FlagContainer(randomFlagID: Int) {
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
    Row {
        TextField(
            value = "",
            onValueChange = { },
            label = { Text(text = "Enter your guess") },
            modifier = Modifier.weight(1f)
        )
        Text(text = "", modifier = Modifier.weight(1f))
    }
}
