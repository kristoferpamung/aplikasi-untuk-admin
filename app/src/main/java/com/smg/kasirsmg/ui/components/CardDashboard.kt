package com.smg.kasirsmg.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smg.kasirsmg.R
import com.smg.kasirsmg.ui.theme.KasirSMGTheme

@SuppressLint("DefaultLocale")
@Composable
fun CardDashboard(
    icon: Int,
    title : String,
    body: String,
    color: Color,
    persen: Double = 0.0,
    isPersen: Boolean
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(id = icon), contentDescription = "", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(text = title, style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = body, style = MaterialTheme.typography.displaySmall)
                }
                if (isPersen){
                    Spacer(modifier = Modifier.width(8.dp))
                    Row {
                        Icon(
                            painter = painterResource(id = if(persen > 0.0) R.drawable.graph_up_arrow else R.drawable.graph_down_arrow), 
                            contentDescription = "",
                            tint = if (persen > 0.0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${String.format("%.2f", persen)} %", color = if (persen > 0.0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}