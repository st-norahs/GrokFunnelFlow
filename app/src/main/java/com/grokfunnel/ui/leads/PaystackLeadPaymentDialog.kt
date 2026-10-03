package com.grokfunnel.ui.leads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.grokfunnel.data.local.entities.LeadEntity
import com.grokfunnel.ui.theme.*

@Composable
fun PaystackLeadPaymentDialog(
    lead: LeadEntity,
    isInitializing: Boolean,
    isVerifying: Boolean,
    lastCheckoutUrl: String?,
    lastReference: String?,
    onDismiss: () -> Unit,
    onInitializeCheckout: (amount: Double, currency: String) -> Unit,
    onVerifyPayment: (reference: String) -> Unit,
    onMarkPaidLocally: (amount: Double, reference: String) -> Unit
) {
    var amountText by remember { mutableStateOf(lead.dealValue.toString()) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("paystack_lead_payment_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text("Paystack Checkout", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100)
                            Text("Collect deal value from lead", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSlate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(lead.name, fontWeight = FontWeight.Bold, color = SoftSlate100, fontSize = 15.sp)
                            val (badgeBg, badgeText, badgeColor) = when (lead.paymentStatus) {
                                "PAID" -> Triple(EmeraldSuccess.copy(alpha = 0.2f), "PAID", EmeraldSuccess)
                                "PENDING" -> Triple(AmberWarning.copy(alpha = 0.2f), "PENDING", AmberWarning)
                                "FAILED" -> Triple(RoseError.copy(alpha = 0.2f), "FAILED", RoseError)
                                else -> Triple(Color.Gray.copy(alpha = 0.2f), "UNPAID", Color.LightGray)
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(badgeBg).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(badgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                            }
                        }
                        Text("${lead.company} • ${lead.email}", fontSize = 12.sp, color = SoftSlate400)
                        Text("Stage: ${lead.stage} | Agent: ${lead.assignedAgent}", fontSize = 11.sp, color = SoftSlate400)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Deal Amount") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = CyanPrimary) },
                        modifier = Modifier.weight(1f).testTag("paystack_amount_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, unfocusedBorderColor = SoftSlate700)
                    )
                    OutlinedTextField(
                        value = selectedCurrency,
                        onValueChange = { selectedCurrency = it.uppercase() },
                        label = { Text("Currency") },
                        modifier = Modifier.width(90.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, unfocusedBorderColor = SoftSlate700)
                    )
                }

                Button(
                    onClick = {
                        val amountVal = amountText.toDoubleOrNull() ?: lead.dealValue
                        onInitializeCheckout(amountVal, selectedCurrency)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_init_paystack_api"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isInitializing
                ) {
                    if (isInitializing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = SoftSlate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contacting Paystack…", color = SoftSlate950)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = SoftSlate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Initialize Checkout Link", fontWeight = FontWeight.SemiBold, color = SoftSlate950)
                    }
                }

                if (lastReference != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftSlate800)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Reference:", fontSize = 10.sp, color = SoftSlate400)
                        Text(lastReference, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CyanGlow)
                        if (lastCheckoutUrl != null) {
                            Text("URL: $lastCheckoutUrl", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SoftSlate400, maxLines = 2)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { clipboardManager.setText(AnnotatedString(lastCheckoutUrl ?: lastReference)) },
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SoftSlate700)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onVerifyPayment(lastReference) },
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess.copy(alpha = 0.9f)),
                                enabled = !isVerifying
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verify", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    val amt = amountText.toDoubleOrNull() ?: lead.dealValue
                                    onMarkPaidLocally(amt, lastReference)
                                },
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VioletSecondary)
                            ) {
                                Text("Mark Paid", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val SoftSlate100 = Slate100
private val SoftSlate400 = SoftSlate400Alias
private val SoftSlate700 = Slate700
private val SoftSlate800 = Slate800
private val SoftSlate950 = SoftSlate950Alias
private val SoftSlate400Alias = Slate400
private val SoftSlate950Alias = SoftSlate950Fixed
private val SoftSlate950Fixed = Slate950
