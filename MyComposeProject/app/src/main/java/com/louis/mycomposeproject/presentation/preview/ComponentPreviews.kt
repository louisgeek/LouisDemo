package com.louis.mycomposeproject.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.louis.mycomposeproject.presentation.components.*
import com.louis.mycomposeproject.ui.theme.MyComposeProjectTheme

@Preview(showBackground = true)
@Composable
fun PasswordTextFieldPreview() {
    MyComposeProjectTheme {
        PasswordTextField(
            value = "password123",
            onValueChange = {},
            label = "Password"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingButtonPreview() {
    MyComposeProjectTheme {
        LoadingButton(
            text = "Login",
            isLoading = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    MyComposeProjectTheme {
        EmptyState(
            message = "No data available",
            actionText = "Retry",
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    MyComposeProjectTheme {
        InfoCard(
            title = "Welcome",
            content = "This is a sample card"
        )
    }
}
