package com.dicosoluciones.changeplayer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dicosoluciones.changeplayer.navigation.AppScreens
import com.dicosoluciones.changeplayer.ui.theme.ChangePlayerTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route = AppScreens.PlayerDashboard.route)
            }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Jugadores",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route = AppScreens.MatchMakerScreen.route)
            }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Partido",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        containerColor = MaterialTheme.colorScheme.background,
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                text = {
//                    Text(
//                        text = "Add"
//                    )
//                },
//                onClick = {
//                    navController.navigate(route = AppScreens.PlayerDashboard.route)
//                },
//                icon = {
//                    Icon(
//                        Icons.Filled.Add,
//                        contentDescription = "Add"
//                    )
//                })
//        }
//    ) {
//
//    }
}

@Composable
fun PreviewHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Jugadores",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FakeHomeScreen() {
    ChangePlayerTheme() {
//        HomeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PreviewHomeScreen()
}