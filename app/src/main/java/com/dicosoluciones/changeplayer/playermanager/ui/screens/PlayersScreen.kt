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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.dicosoluciones.changeplayer.R
import com.dicosoluciones.changeplayer.playermanager.ui.state.PlayersUiState
import com.dicosoluciones.changeplayer.playermanager.ui.PlayersViewModel
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayersScreen(
    playersViewModel: PlayersViewModel,
    navController: NavController
) {

    val showAddPlayer: Boolean by playersViewModel.showDialog.observeAsState(initial = false)
    val showEditPlayer: Boolean by playersViewModel.showEditPlayer.observeAsState(initial = false)
    val playerEdit: PlayerModel by playersViewModel.playerEdit.observeAsState(
        initial = PlayerModel(
            id = 0,
            name = "No recuperado",
            stars = 0
        )
    )
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
                AddPlayerDialog(
                    showAddPlayer,
                    onDismiss = { playersViewModel.onDialogClose() },
                    onPlayerAdd = { playersViewModel.onPlayerCreated(it[0], it[1].toInt()) })

                EditPlayerDialog(
                    showEditPlayer,
                    player = playerEdit,
                    onDismiss = { playersViewModel.onDialogClose() },
                    onPlayerEdit = { playersViewModel.onPlayerEdited(it) },
                    onPlayerDelete = { playersViewModel.onPlayerDelete(it) }
                )

                Scaffold(
                    content = {

                        PlayersList((uiState as PlayersUiState.Success).players, playersViewModel)
                    },
                    floatingActionButton = {
                        FabDialog(
                            Modifier.align(Alignment.BottomEnd),
                            playersViewModel
                        )
                    },
                    floatingActionButtonPosition = FabPosition.Center
                )
            }
        }
    }


}

@Composable
fun PlayersList(players: List<PlayerModel>, playersViewModel: PlayersViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 87.dp)
    ) {
        items(players, key = { it.id }) {
            ItemPlayer(playerModel = it, playersViewModel = playersViewModel)
        }
    }
}

@Composable
fun ItemPlayer(
    playerModel: PlayerModel,
    playersViewModel: PlayersViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                playersViewModel.onUpdatePlayer(playerModel = playerModel)
                playersViewModel.onShowEditPlayer()
            }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.player),
                contentDescription = "playerPhoto",
                modifier = Modifier.padding(bottom = 5.dp)
            )
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

@Composable
fun FabDialog(modifier: Modifier, playersViewModel: PlayersViewModel) {
    FloatingActionButton(
        onClick = {
            playersViewModel.onShowDialogClick()
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Añadir jugador")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerDialog(show: Boolean, onDismiss: () -> Unit, onPlayerAdd: (List<String>) -> Unit) {

    var playerName by remember { mutableStateOf("") }
    var playerStars by remember { mutableStateOf("") }
    var dataPlayer: MutableList<String> = mutableListOf()

    if (show) {
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
                        text = "Crear jugador",
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    TextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        maxLines = 1,
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    TextField(
                        value = playerStars,
                        onValueChange = { playerStars = it },
                        maxLines = 1,
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = {
                        //Mandar tarea
                        dataPlayer.add(playerName)
                        dataPlayer.add(playerStars)

                        onPlayerAdd(dataPlayer)

                        playerName = ""
                        playerStars = ""
                        dataPlayer.clear()

                    }) {
                        Text(text = "Crear")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlayerDialog(
    showEdit: Boolean,
    player: PlayerModel,
    onDismiss: () -> Unit,
    onPlayerEdit: (PlayerModel) -> Unit,
    onPlayerDelete: (PlayerModel) -> Unit
) {

    if (showEdit) {

        var name by remember {
            mutableStateOf(player.name)
        }

        var stars by remember {
            mutableStateOf(player.stars.toString())
        }

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
                        text = "Editar jugador",
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        maxLines = 1,
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    TextField(
                        value = stars.toString(),
                        onValueChange = { stars = it },
                        maxLines = 1,
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = {
                        player.name = name
                        player.stars = stars.toInt()
                        onPlayerEdit(player)
                    }) {
                        Text(text = "Editar")
                    }
                    TextButton(onClick = {
                        onPlayerDelete(player)
                    }) {
                        Text(text = "Eliminar")
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewItemPlayer() {

    var players = listOf<String>("Player", "2")
    var listPlayers =
        listOf<List<String>>(players, players, players, players, players, players, players, players)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(bottom = 87.dp)
        ) {
            items(listPlayers) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.player),
                            contentDescription = "playerPhoto",
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
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

@Composable
fun PreviewFabDialog() {
    FloatingActionButton(
        onClick = {

        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Añadir jugador")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewEditPlayerDialog() {

    Dialog(
        onDismissRequest = { },

        ) {
        Surface(
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Editar jugador",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = "Hola",
                    onValueChange = { },
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = "3",
                    onValueChange = { },
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {

                }) {
                    Text(text = "Editar")
                }
                TextButton(onClick = {
                }) {
                    Text(text = "Eliminar")
                }
            }
        }
    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FakePreview() {
//    Scaffold(content = {
//        PreviewItemPlayer()
//    }, floatingActionButton = { PreviewFabDialog() },
//    floatingActionButtonPosition = FabPosition.Center)
    PreviewEditPlayerDialog()

}
