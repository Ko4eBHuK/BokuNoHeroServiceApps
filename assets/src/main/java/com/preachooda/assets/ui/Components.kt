package com.preachooda.assets.ui

import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.preachooda.assets.BuildConfig
import com.preachooda.assets.R
import com.preachooda.assets.ui.theme.*
import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Rate

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_primary_btn_height)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.default_primary_btn_elevation),
            pressedElevation = dimensionResource(id = R.dimen.pressed_primary_btn_elevation)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed
        )
    ) {
        Text(
            text = text,
            fontSize = dimensionResource(id = R.dimen.default_primary_btn_text_size).value.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    PrimaryButton(
        onClick = {},
        text = stringResource(id = R.string.btn_sos_text)
    )
}

@Composable
fun ConfirmButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_primary_btn_height)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.default_primary_btn_elevation),
            pressedElevation = dimensionResource(id = R.dimen.pressed_primary_btn_elevation)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenConfirm
        )
    ) {
        Text(
            text = text,
            fontSize = dimensionResource(id = R.dimen.default_primary_btn_text_size).value.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmButtonPreview() {
    ConfirmButton(
        onClick = {},
        text = stringResource(id = R.string.alert_dialog_error_confirm_text)
    )
}

@Composable
fun CancelButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_primary_btn_height)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.default_primary_btn_elevation),
            pressedElevation = dimensionResource(id = R.dimen.pressed_primary_btn_elevation)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = CancelRed
        )
    ) {
        Text(
            text = text,
            fontSize = dimensionResource(id = R.dimen.default_primary_btn_text_size).value.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CancelButtonPreview() {
    CancelButton(
        onClick = {},
        text = stringResource(id = R.string.alert_dialog_cancel_text)
    )
}

@Composable
fun ServiceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_primary_btn_height)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.default_primary_btn_elevation),
            pressedElevation = dimensionResource(id = R.dimen.pressed_primary_btn_elevation)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryWhite
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceButtonPreview() {
    ServiceButton(
        onClick = {},
        content = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "",
                tint = PrimaryGray
            )
        }
    )
}

@Composable
fun ErrorAlertDialog(
    onDismissRequest: () -> Unit,
    dialogTitle: String = stringResource(id = R.string.alert_dialog_error_title),
    dialogText: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            PrimaryButton(
                onClick = onDismissRequest,
                text = stringResource(id = R.string.alert_dialog_error_confirm_text)
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorAlertDialogPreview() {
    ErrorAlertDialog(
        onDismissRequest = {},
        dialogTitle = "FooBar",
        dialogText = "Lorem ipsum"
    )
}

@Composable
fun ConfirmAlertDialog(
    onDismissRequest: () -> Unit,
    dialogTitle: String = stringResource(id = R.string.alert_dialog_confirm_title),
    dialogText: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ConfirmButton(
                onClick = onDismissRequest,
                text = stringResource(id = R.string.alert_dialog_error_confirm_text)
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
    )
}

@Preview(showBackground = true)
@Composable
fun ConfirmAlertDialogPreview() {
    ConfirmAlertDialog(
        onDismissRequest = {},
        dialogTitle = "FooBar",
        dialogText = "Lorem ipsum"
    )
}

@Composable
fun ConfirmCancelAlertDialog(
    dialogTitle: String = stringResource(id = R.string.alert_dialog_confirm_title),
    dialogText: String,
    confirmRequest: () -> Unit,
    cancelRequest: () -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ConfirmButton(
                onClick = confirmRequest,
                text = stringResource(id = R.string.alert_dialog_confirm_text)
            )
        },
        dismissButton = {
            CancelButton(
                onClick = cancelRequest,
                text = stringResource(id = R.string.alert_dialog_cancel_text)
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
    )
}

@Preview(showBackground = true)
@Composable
fun ConfirmCancelAlertDialogPreview() {
    ConfirmCancelAlertDialog(
        dialogTitle = "FooBar",
        dialogText = "Lorem ipsum",
        confirmRequest = {},
        cancelRequest = {},
        onDismissRequest = {},
    )
}

@Composable
fun LoadingDialog(
    dialogText: String,
) {
    Dialog(
        onDismissRequest = {
            // No logic
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = dialogText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingDialogPreview() {
    LoadingDialog(dialogText = "Lorem ipsum")
}

@Composable
fun TogglePlate(
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    var checkedState by remember { mutableStateOf(checked) }
    lateinit var plateColors: ButtonColors
    val plateBorder: BorderStroke?
    val plateElevation: ButtonElevation?

    if (checkedState) {
        plateColors = ButtonDefaults.buttonColors(
            containerColor = SecondaryRed
        )
        plateBorder = BorderStroke(
            width = 1.dp,
            color = PrimaryRed
        )
        plateElevation = null
    } else {
        plateColors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed
        )
        plateBorder = null
        plateElevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.default_primary_btn_elevation),
            pressedElevation = dimensionResource(id = R.dimen.pressed_primary_btn_elevation)
        )
    }

    Button(
        onClick = {
            onClick()
            checkedState = !checkedState
        },
        modifier = modifier
            .height(dimensionResource(id = R.dimen.default_toggle_palette_height)),
        shape = RoundedCornerShape(15.dp),
        colors = plateColors,
        elevation = plateElevation,
        border = plateBorder
    ) {
        Text(
            text = text,
            fontSize = fontDimensionResource(id = R.dimen.default_plate_text_size)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TogglePalettePreview() {
    TogglePlate(text = "Lorem ipsum")
}

@Composable
fun MediaIconButton(
    onClick: () -> Unit,
    @DrawableRes drawableRes: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = PrimaryGray,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_primary_btn_height)),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = "MediaIconBtn",
            tint = SecondaryBlack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediaIconButtonPreview() {
    MediaIconButton(
        onClick = {},
        drawableRes = R.drawable.ic_media_video
    )
}

@Composable
fun MapView(
    locationStatus: Status,
    location: LocationSimple?,
    reloadMapCallback: () -> Unit = {}
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(5.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            when (locationStatus) {
                Status.SUCCESS -> {
                    if (location != null) {
                        AndroidView(
                            factory = { context ->
                                Mapbox.getInstance(context)
                                val mapKey = BuildConfig.MAPTILER_API_KEY
                                val mapId = "streets-v2"
                                val mapStyleUrl =
                                    "https://api.maptiler.com/maps/$mapId/style.json?key=$mapKey"

                                val view = LayoutInflater.from(context).inflate(
                                    R.layout.simplemap_view,
                                    null,
                                    false
                                )
                                val mapView = view.findViewById<MapView>(R.id.map_view)

                                mapView.getMapAsync { map ->
                                    map.setStyle(mapStyleUrl)
                                    val mapTarget = LatLng(
                                        location.latitude.toDouble(),
                                        location.longitude.toDouble()
                                    )

                                    val markerOptions = MarkerOptions()
                                    markerOptions.position = mapTarget
                                    markerOptions.title = "Локация заявки"
                                    map.addMarker(markerOptions)

                                    map.cameraPosition = CameraPosition.Builder()
                                        .target(mapTarget)
                                        .zoom(12.0)
                                        .build()
                                }

                                mapView
                            },
                            update = { view ->
                                // Update the view
                            }
                        )
                    } else {
                        Text(
                            text = "Не удалось получить местоположение\n;(",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                            fontSize = fontDimensionResource(id = R.dimen.default_primary_btn_text_size),
                            color = Color.White
                        )
                    }
                }

                Status.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Status.ERROR -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = location?.description ?: "Не удалось получить местоположение\n;(",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .padding(dimensionResource(id = R.dimen.default_padding)),
                            textAlign = TextAlign.Center,
                            fontSize = fontDimensionResource(id = R.dimen.default_primary_btn_text_size),
                            color = Color.White
                        )
                        TextButton(
                            onClick = { reloadMapCallback() }
                        ) {
                            Text(text = "повторить")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MapViewPreview() {
    MapView(
        locationStatus = Status.SUCCESS,
        location = LocationSimple(),
        reloadMapCallback = {}
    )
}

@Composable
fun RateStarsRow(
    rate: Rate = Rate.NOT_RATED,
    enabled: Boolean = true,
    rateCallback: (Rate) -> Unit = {},
    itemKey: String = ""
) {
    var currentRate by rememberSaveable { mutableStateOf(rate) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (rateItem in Rate.entries.subList(1, Rate.entries.size)) {
            IconButton(
                onClick = {
                    rateCallback(rateItem)
                    currentRate = rateItem
                },
                modifier = Modifier.semantics { contentDescription = "$itemKey ${rateItem.name}" },
                enabled = enabled
            ) {
                Icon(
                    painter = painterResource(
                        id = if (rateItem.value <= currentRate.value)
                            R.drawable.ic_star_rate_active else R.drawable.ic_star_rate_inactive
                    ),
                    contentDescription = "${rateItem.value}",
                    modifier = Modifier.fillMaxSize(),
                    tint = if (rateItem.value <= currentRate.value)
                        RateStarColorActive else RateStarColorInactive
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RateStarsRowPreview() {
    RateStarsRow(
        rate = Rate.THREE,
        rateCallback = {},
    )
}

@Composable
fun ExpandableTextPlate(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    var _maxLines by remember { mutableStateOf(maxLines) }
    ElevatedCard(
        onClick = {
            if (_maxLines == maxLines) {
                _maxLines = Int.MAX_VALUE
            } else {
                _maxLines = maxLines
            }
        },
        modifier = modifier,
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
        Text(
            text = text,
            modifier = Modifier.padding(5.dp),
            fontSize = fontDimensionResource(id = R.dimen.default_plate_text_size),
            maxLines = _maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableTextPlatePreview() {
    ExpandableTextPlate(
        text = "Lorem ipsum dolor sit amet reks reks shmeks shreks",
        maxLines = 2
    )
}

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = PrimaryRed
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontDimensionResource(id = R.dimen.default_primary_btn_text_size),
        color = color,
        fontWeight = FontWeight.SemiBold
    )
}
