package net.erikkarlsson.simplesleeptracker.features.details

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import net.erikkarlsson.composesample.ui.SleepTrackerTheme
import net.erikkarlsson.simplesleeptracker.core.util.formatDateDisplayName2
import net.erikkarlsson.simplesleeptracker.core.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.core.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.common.compose.rememberMutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun Details(state: DetailsState,
            actioner: (DetailsAction) -> Unit) {

    var openDialog by rememberMutableState { false }

    SleepTrackerTheme {
        Scaffold(
                topBar = {
                    TopAppBar(
                            title = {
                                Text(text = state.sleep?.toDate?.formatDateDisplayName2 ?: "")
                            },
                            navigationIcon = {
                                IconButton(onClick = { actioner(NavigateUp) }) {
                                    Icon(Icons.Default.ArrowBack)
                                }
                            },
                            actions = {
                                Text(
                                        modifier = Modifier.padding(end = 14.dp)
                                                .clickable(onClick = { openDialog = true }),
                                        text = stringResource(id = R.string.delete))
                            }
                    )
                }
        ) {
            DetailContent(state, actioner)

            if (openDialog) {
                ConfirmDeleteDialog(
                        actioner = actioner,
                        onDialogClosed = { openDialog = false }
                )
            }
        }
    }
}

@Composable
fun DetailContent(state: DetailsState,
                  actioner: (DetailsAction) -> Unit) {
    val iconDate = vectorResource(id = R.drawable.ic_today_black_24dp)
    val iconSleep = vectorResource(id = R.drawable.ic_hotel_black_24dp)
    val iconAwake = vectorResource(id = R.drawable.ic_wb_sunny_black_24dp)

    val sleep = state.sleep ?: return

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Image(iconDate, modifier = Modifier.padding(end = 8.dp))
            Text(text = stringResource(id = R.string.start_date))
            Spacer(modifier = Modifier.weight(1f))
            Text(modifier = Modifier.clickable { actioner(PickStartDateAction(sleep.fromDate)) },
                    text = sleep.fromDate.formatDateDisplayName2)
        }
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Image(iconSleep, modifier = Modifier.padding(end = 8.dp))
            Text(text = stringResource(id = R.string.time_asleep))
            Spacer(modifier = Modifier.weight(1f))
            Text(modifier = Modifier.clickable { actioner(PickTimeAsleepAction(sleep.fromDate.toLocalTime())) },
                    text = sleep.fromDate.formatHHMM)
        }
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Image(iconAwake, modifier = Modifier.padding(end = 8.dp))
            Text(text = stringResource(id = R.string.time_awake))
            Spacer(modifier = Modifier.weight(1f))
            Text(modifier = Modifier.clickable { actioner(PickTimeAwakeAction(sleep.toDate?.toLocalTime())) },
                    text = sleep.toDate?.formatHHMM ?: "")
        }
        Text(modifier = Modifier.padding(top = 8.dp),
                text = String.format("%s %s",
                        stringResource(R.string.you_have_slept_for),
                        state.hoursSlept.formatHoursMinutes))
    }

}

@Composable
@Preview
fun DetailsPreview() {
    Details(DetailsState(1, Sleep.from(fromDate = "2017-03-04T23:30:00+01:00", toDate = "2017-03-05T06:30:00+01:00")), {})
}

@Composable
private fun ConfirmDeleteDialog(
        actioner: (DetailsAction) -> Unit,
        onDialogClosed: () -> Unit
) {
    SleepTrackerAlertDialog(
            title = stringResource(R.string.confirm_delete_sleep),
            confirmText = stringResource(R.string.delete),
            onConfirm = {
                actioner(DeleteAction)
                onDialogClosed()
            },
            dismissText = stringResource(R.string.cancel),
            onDismissRequest = { onDialogClosed() }
    )
}

@Composable
fun SleepTrackerAlertDialog(
        title: String,
        confirmText: String,
        onConfirm: () -> Unit,
        dismissText: String,
        onDismissRequest: () -> Unit
) {
    AlertDialog(
            title = { Text(text = title) },
            confirmButton = {
                OutlinedButton(onClick = { onConfirm() }) {
                    Text(text = confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = dismissText)
                }
            },
            onDismissRequest = onDismissRequest
    )
}

