package com.dicosoluciones.changeplayer.playermanager.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.dicosoluciones.changeplayer.R
import com.dicosoluciones.changeplayer.playermanager.ui.PlayersViewModel
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import com.dicosoluciones.changeplayer.playermanager.ui.state.PlayersUiState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MatchMakerScreen(
    playersViewModel: PlayersViewModel,
    navController: NavController
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle


    val uiState by produceState<PlayersUiState>(
        initialValue = PlayersUiState.Loading,
        key1 = lifecycle,
        key2 = playersViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            playersViewModel.uiState.collect { value = it }
        }
    }

    when (uiState) {
        is PlayersUiState.Error -> TODO()
        PlayersUiState.Loading -> {
            CircularProgressIndicator()
        }

        is PlayersUiState.Success -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Scaffold(
                    content = {
                        ListPlayers((uiState as PlayersUiState.Success).players, playersViewModel)
                    }
                )
            }
        }
    }
}

@Composable
fun ListPlayers(players: List<PlayerModel>, playersViewModel: PlayersViewModel) {

    ConstraintLayout() {
        val (text, grid, space, button) = createRefs()
        val topGuide = createGuidelineFromTop(0.1f)
        val bottomGuide = createGuidelineFromBottom(0.1f)

        Text(
            text = "Selecciona los jugador",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(topGuide)
                }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .constrainAs(grid) {
                    top.linkTo(text.bottom)
                }
        ) {
            items(players, key = { it.id }) {
                SelectPlayers(playerModel = it, playersViewModel = playersViewModel)
            }
        }

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(button) {
                    top.linkTo(bottomGuide)
                }
        ) {
            Text(text = "Botón")
        }

    }

}

@Composable
fun SelectPlayers(
    playerModel: PlayerModel,
    playersViewModel: PlayersViewModel,
) {
    var isSelected by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                isSelected = !isSelected
                playersViewModel.addOrDeleteListPlayers(playerModel = playerModel)
            },
        colors = CardDefaults.cardColors(

            containerColor = if (isSelected) Color.DarkGray else Color.LightGray

        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.player),
                contentDescription = "playerPhoto",
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = playerModel.name,
                    modifier = Modifier.padding(bottom = 5.dp),
                    fontWeight = FontWeight.Bold
                )
                Row() {
                    for (i in 1..playerModel.stars) {
                        Icon(Icons.Default.Star, contentDescription = "Estrella")
                    }

                }
            }
        }
    }
}

@Composable
fun PreviewSelectPlayer() {

    var players = listOf<String>("Player", "2")
    var listPlayers =
        listOf<List<String>>(players, players, players, players, players, players, players, players)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val boxText = createRef()
        val boxButtom = createRef()
        val boxPlayers = createRef()
        val topGuide = createGuidelineFromTop(0.2f)
        val bottomGuide = createGuidelineFromBottom(0.2f)


        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .constrainAs(boxText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(
                text = "Selecciona los jugador",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

        }

        Button(
            onClick = { /* Acción al hacer clic */ },
            modifier = Modifier.constrainAs(boxButtom) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(text = "Botón")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(boxPlayers) {
                    top.linkTo(boxText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(boxButtom.top)
                }

        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listPlayers) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray
                        )

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.player),
                                contentDescription = "playerPhoto",
                            )

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = it[0],
                                    modifier = Modifier.padding(bottom = 5.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Row() {
                                    for (i in 1..it[1].toInt()) {
                                        Icon(Icons.Default.Star, contentDescription = "Estrella")
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ConstraintLayoutExample2() {
    ConstraintLayout {
        val (text, grid, space, button) = createRefs()
        val topGuide = createGuidelineFromTop(0.1f)
        val bottomGuide = createGuidelineFromBottom(0.1f)

        Text(
            text = "Selecciona los jugador",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(topGuide)
                }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .background(Color.Blue)
                .constrainAs(grid) {
                    top.linkTo(text.bottom)
                }
        ) {
            items(10) { index ->
                Card(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Elemento $index",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .padding(16.dp)
                .constrainAs(button) {
                    top.linkTo(bottomGuide)
                }
        ) {
            Text(text = "Botón")
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FakePreviewSelect() {
    ConstraintLayoutExample2()
}
