package com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.agileandroidalpha.ui.theme.Charcoal
import com.example.agileandroidalpha.ui.theme.Chocolate
import com.example.agileandroidalpha.ui.theme.DeepTeal
import com.example.agileandroidalpha.ui.theme.MidnightPurple

@Composable
fun LabeledHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    textColor: Color = DeepTeal,
    hintColor: Color = Charcoal,
    label: String = "",
    placeholder: String = "",
    labelColor: Color = MidnightPurple,
    phColor: Color = Chocolate,
    bgColor: Color = Color.White,
    showHint: Boolean = true,
    fIcon: Int? = null,
    rIcon: Int? = null,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    maxLines: Int = 15,
    onFocusChange: (FocusState) -> Unit
) {

    Box(
        modifier = modifier
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            textStyle = textStyle,
            label = {Text(
                text = label,
                color = labelColor
            )},
            placeholder = {Text(
                text = placeholder,
                color = phColor
            )},
            singleLine = singleLine,
            maxLines = maxLines,
            modifier = Modifier
                .background(bgColor)
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                },
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor
            )
        )
        if (showHint) {
            Text(text = hint, style = textStyle, color = hintColor)
        }
    }
}