package com.preachooda.bokunoheroservice.section.tickets

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.bokunoheroservice.R
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.navigation.Screens
import com.preachooda.bokunoheroservice.section.tickets.domain.AddFilterItemIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.CloseErrorIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.CloseMessageIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.RefreshTicketsIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.RemoveFilterItemIntent
import com.preachooda.bokunoheroservice.section.tickets.viewModel.TicketsViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.PurpleGrey40
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.assets.ui.theme.StatusAssignedColor
import com.preachooda.assets.ui.theme.TextGray
import com.preachooda.assets.ui.theme.StatusCompletedColor
import com.preachooda.assets.ui.theme.StatusDeclinedColor
import com.preachooda.assets.ui.theme.StatusNewColor
import com.preachooda.assets.ui.theme.StatusValuationColor
import com.preachooda.assets.ui.theme.StatusWipColor
import com.preachooda.domain.model.ActivityStatus

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun TicketsScreen(
    navController: NavController,
    viewModel: TicketsViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(RefreshTicketsIntent)
            }
            else -> {
                // do nothing
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value

    var searchFilterValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val ticketClick: (Long) -> Unit = { navController.navigate("${Screens.Ticket.route}/$it") }

    val focusManager = LocalFocusManager.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = screenState.isLoading,
        onRefresh = {
            viewModel.processIntent(RefreshTicketsIntent)
        }
    )

    if (screenState.isShowMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(CloseMessageIntent) },
            dialogText = screenState.message
        )
    }
    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(CloseErrorIntent) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(dialogText = screenState.message)
    }

    Scaffold(
        topBar = { TicketsTopBar() }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding(),
                )
        ) {
            OutlinedTextField( // Search bar
                value = searchFilterValue,
                onValueChange = {
                    searchFilterValue = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.default_padding)),
                minLines = 1,
                label = {
                    Text(
                        text = "Поиск по ключевым словам"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryGray,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.processIntent(AddFilterItemIntent(searchFilterValue))
                        searchFilterValue = ""
                    }
                )
            )

            FlowRow(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) { // Filters list
                screenState.filterItems.forEach {
                    FilterItem(
                        value = it,
                        onRemoveClick = {
                            viewModel.processIntent(RemoveFilterItemIntent(it))
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn { // Tickets
                    if (screenState.ticketsShowList.isNotEmpty()) {
                        screenState.ticketsShowList.forEach {
                            item(key = "${it.id}${it.status}") {
                                TicketItem(ticket = it) {
                                    ticketClick(it.id)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Нет истории заявок на оказание помощи.",
                                color = TextGray,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = screenState.isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = PrimaryRed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketsTopBar() {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.tickets_screen_top_bar_title))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@Composable
fun FilterItem(
    value: String,
    onRemoveClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = PrimaryGray,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value
            )
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Remove filter",
                modifier = Modifier.clickable { onRemoveClick() }
            )
        }
    }
}

@Preview
@Composable
fun FilterItemPreview() {
    FilterItem(value = "Lorem Ipsum")
}

@Composable
fun TicketItem(
    ticket: Ticket,
    onTicketClick: () -> Unit
) {
    ElevatedCard(
        // open tickets list
        onClick = {
            onTicketClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            contentColor = PrimaryBlack,
            containerColor = PrimaryWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.95f)
            ) {
                Text(
                    text = "№ заявки: ${ticket.id}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Дата создания: ${ticket.creationDate}")
                Text(
                    text = "Описание: ${ticket.description}",
                    color = SecondaryBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Log.d("TicketItem", "status ${ticket.status}")
                Text(
                    text = ticket.status.value,
                    color = when(ticket.status) {
                        ActivityStatus.CREATED -> StatusNewColor
                        ActivityStatus.IN_WORK -> StatusWipColor
                        ActivityStatus.EVALUATION -> StatusValuationColor
                        ActivityStatus.COMPLETED -> StatusCompletedColor
                        ActivityStatus.REJECTED -> StatusDeclinedColor
                        ActivityStatus.ASSIGNED -> StatusAssignedColor
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_open),
                contentDescription = "Open ticket",
                tint = PurpleGrey40
            )
        }
    }
}

@Preview
@Composable
fun TicketItemPreview() {
    TicketItem(
        ticket = Ticket(
            id = 75892,
            creationDate = "05.06.2000",
            description = "Jesus it is for test fff 555 use! Lorem ipsum dolor sit amet"
        ),
        onTicketClick = { }
    )
}
