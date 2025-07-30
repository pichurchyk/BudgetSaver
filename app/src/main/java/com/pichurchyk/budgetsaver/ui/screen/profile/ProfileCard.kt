package com.pichurchyk.budgetsaver.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.user.User
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUserUiStatus
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUserViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun ProfileCard(
    viewState: ProfileUserViewState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .heightIn(min = 100.dp)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val uiStatus = viewState.status) {
            is ProfileUserUiStatus.Idle -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(46.dp),
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar"
                    )

                    Column {
                        Text(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            text = viewState.userData?.name ?: ""
                        )

                        Text(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            text = viewState.userData?.email ?: ""
                        )
                    }
                }
            }

            is ProfileUserUiStatus.Loading -> {
                ProfileCardLoader(
                    modifier = Modifier
                )
            }

            is ProfileUserUiStatus.Error -> {
                Text(
                    text = uiStatus.error.message ?: stringResource(R.string.error_occurred)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        ProfileCard(
            viewState = ProfileUserViewState(
                status = ProfileUserUiStatus.Idle,
                userData = PreviewMocks.user
            ),
        )
    }
}