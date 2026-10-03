package com.grokfunnel.ui.gigs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.grokfunnel.data.local.entities.GigEntity
import com.grokfunnel.ui.theme.*

@Composable
fun PaymentGatewayDialog(
    gig: GigEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmPayment: (GigEntity, String) -> Unit
) {
    var selectedGateway by remember { mutableStateOf("Paystack Webhook") }
    var isProcessing by remember { mutableStateOf(false) }

    val gateways = listOf(
        "Paystack Webhook",
        "Stripe Direct",
        "Escrow Secure",
        "PayPal Business",
        "ACH Wire Transfer"
    )

    val feeRate = when {
        selectedGateway.contains("Paystack") -> 0.015
        selectedGateway.contains("Stripe") -> 0.029
        else -> 0.025
    }
    val processingFee = Math.round(gig.budget * feeRate * 100.0) / 100.0
    val netPayout = gig.budget - processingFee

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("payment_gateway_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Collect Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Secure Integrated Gateway", style = MaterialTheme.typography.labelSmall, color = Slate400)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(gig.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate100)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Client: ${gig.clientName} • ${gig.sourcePlatform}", style = MaterialTheme.typography.labelSmall, color = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Select Payment Gateway", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate200)
                Spacer(modifier = Modifier.height(6.dp))

                gateways.forEach { gateway ->
                    val isSelected = selectedGateway == gateway
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyanGlow.copy(alpha = 0.12f) else Slate800)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyanGlow else Slate700,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedGateway = gateway }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedGateway = gateway },
                                colors = RadioButtonDefaults.colors(selectedColor = CyanGlow)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = gateway,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Slate100
                            )
                        }
                        if (gateway.contains("Paystack")) {
                            Surface(shape = RoundedCornerShape(4.dp), color = CyanGlow.copy(alpha = 0.2f)) {
                                Text("WEBHOOK 1.5%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanGlow, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Invoice Gross:", fontSize = 12.sp, color = Slate400)
                            Text("$currencySymbol${String.format("%,.2f", gig.budget)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SoftSlate100)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gateway Fee (${(feeRate * 100).toInt()}%):", fontSize = 12.sp, color = Slate400)
                            Text("-$currencySymbol${String.format("%,.2f", processingFee)}", fontSize = 12.sp, color = RoseError)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate700)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Net Credited:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SoftSlate100)
                            Text("$currencySymbol${String.format("%,.2f", netPayout)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isProcessing = true
                        onConfirmPayment(gig, selectedGateway)
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("confirm_collect_payment_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Slate950, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authorizing…", color = Slate950, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = SoftSlate950, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authorize & Collect $currencySymbol${String.format("%,.2f", netPayout)}", fontWeight = FontWeight.Bold, color = Slate950)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SoftSlate400, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("256-bit SSL • PCI-DSS Compliant", fontSize = 10.sp, color = Slate400)
                }
            }
        }
    }
}

// local color aliases to avoid any import edge cases
private val SoftSlate100 = Slate100
private val SoftSlate950 = Slate950
private val SoftSlate400 = Slate400
