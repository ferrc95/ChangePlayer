package com.dicosoluciones.changeplayer.playermanager.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.GridView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.dicosoluciones.changeplayer.R
import com.dicosoluciones.changeplayer.playermanager.ui.PlayersViewModel
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import com.dicosoluciones.changeplayer.playermanager.ui.state.PlayersUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MatchMakerScreen(
    playersViewModel: PlayersViewModel,
    navController: NavController
) {
    val showTeams: Boolean by playersViewModel.showTeams.observeAsState(initial = false)
    val teamOne by playersViewModel.teamOne.observeAsState()
    val teamTwo by playersViewModel.teamTwo.observeAsState()

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
            ) {
                TeamsDialog(
                    showTeams = showTeams,
                    teamOne = teamOne!!,
                    teamTwo = teamTwo!!,
                    onDismiss = { playersViewModel.onDialogClose() }
                )

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

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (text, grid, button) = createRefs()
        val topGuide = createGuidelineFromTop(0.1f)
        val bottomGuide = createGuidelineFromBottom(0.1f)


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(topGuide)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selecciona los jugadores",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .padding(horizontal = 16.dp)
                .constrainAs(grid) {
                    top.linkTo(text.bottom)
                }
        ) {
            items(players, key = { it.id }) {
                SelectPlayers(playerModel = it, playersViewModel = playersViewModel)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .constrainAs(button) {
                    top.linkTo(bottomGuide)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    playersViewModel.createTeams()
                    playersViewModel.onShowTeams()
                },
            ) {
                Text(text = "Crear equipos")
            }
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
fun TeamsDialog(
    showTeams: Boolean,
    teamOne: ArrayList<PlayerModel>,
    teamTwo: ArrayList<PlayerModel>,
    onDismiss: () -> Unit
) {
    if (showTeams) {

        var teamSelected by remember {
            mutableStateOf(true)
        }


        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    Modifier
                        .background(Color.White)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (teamSelected) {
                        LazyVerticalGridTeam(nameTeam = "Equipo Azul", team = teamOne)
                    } else {
                        LazyVerticalGridTeam(nameTeam = "Equipo Amarillo", team = teamTwo)
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { teamSelected = !teamSelected }
                    ) {
                        Text(text = "Cambiar equipo")
                    }
                }
            }
        }
    }
}

@Composable
fun LazyVerticalGridTeam(nameTeam: String, team: ArrayList<PlayerModel>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column() {
            Text(
                text = nameTeam,
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            LazyVerticalGrid(columns = GridCells.Fixed((team.size.toDouble() / 2).roundToInt())) {
                items(team) { player ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.player),
                                contentDescription = "player"
                            )
                            Text(
                                text = player.name,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 2.dp)
                            ) {
                                for (i in 1..player.stars.toInt()) {
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


@Composable
fun TeamsDialog2(
    showTeams: Boolean,
    teamOne: ArrayList<PlayerModel>,
    teamTwo: ArrayList<PlayerModel>,
    onDismiss: () -> Unit
) {
    if (showTeams) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = teamOne.size.toString(),
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ConstraintLayoutExample2() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (text, grid, button) = createRefs()
        val topGuide = createGuidelineFromTop(0.1f)
        val bottomGuide = createGuidelineFromBottom(0.1f)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(topGuide)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selecciona los jugador",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .background(Color.Blue)
                .constrainAs(grid) {
                    top.linkTo(text.bottom)
                    bottom.linkTo(bottomGuide)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .constrainAs(button) {
                    top.linkTo(bottomGuide)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { /* Do something */ },
            ) {
                Text(text = "Botón")
            }
        }


    }
}

@Composable
fun PreviewLazyVerticalGrid(
) {

    var players = listOf<String>("Player", "2")
    var listPlayers = listOf<List<String>>(players, players, players, players, players)

    LazyVerticalGrid(
        columns = GridCells.Fixed((listPlayers.size.toDouble() / 2).roundToInt())
    ) {
        items(listPlayers) { player ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.player),
                        contentDescription = "player"
                    )
                    Text(
                        text = player[0],
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Row() {
                        for (i in 1..player[1].toInt()) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Estrella"
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun PreviewLazyColumnChunked() {
    var players = listOf<String>("Player", "2")
    var listPlayers = listOf<List<String>>(players, players, players, players, players)
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listPlayers.chunked(3).forEach { rowItems ->
            item {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    rowItems.forEach { item ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(85.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.player),
                                    contentDescription = "player"
                                )
                                Text(
                                    text = item[0],
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                                Row() {
                                    for (i in 1..item[1].toInt()) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Estrella"
                                        )
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
fun PreviewTeamsDialog(
) {
    Dialog(onDismissRequest = { }) {
        Surface(
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                PreviewLazyVerticalGrid()
                PreviewLazyColumnChunked()
            }
        }
    }
}


@Composable
fun PreviewBox() {
    var numRow = 4
    var numCol = 3
    var changeColor = false
    var color = Color.Red

    Box(modifier = Modifier.fillMaxSize()) {
        for (i in 1..numCol) {
            Column() {
                Box() {
                    for (j in 1..numRow) {
                        Row() {
                            changeColor = !changeColor
                            if (changeColor) {
                                color = Color.Red
                            } else {
                                color = Color.Blue
                            }
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)

                                    .background(color)
                            )
                        }
                    }
                }

            }

        }
    }
}

@Composable
fun FlexLayout() {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { item ->
                        Text(text = item)
                    }
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun FakePreviewSelect() {
    PreviewTeamsDialog()
}
