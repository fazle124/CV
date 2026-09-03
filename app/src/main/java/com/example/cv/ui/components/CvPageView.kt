package com.example.cv.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cv.R
import com.example.cv.model.CvHeader
import com.example.cv.model.CvSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CvPageView(
    header: CvHeader,
    sections: List<CvSection>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentDateStr = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // A4 Paper styled Card
        Card(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header: Name & Contact on left, Photo on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Resume of",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFF1F2937),
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = header.name,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color(0xFF0B69FF),
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Mailing address
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Address",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = header.address,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF475569),
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Phone with dial click
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${header.phone}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone",
                                tint = Color(0xFF0B69FF),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = header.phone,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF0B69FF),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email with mailto click
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${header.email}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF0B69FF),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = header.email,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF0B69FF),
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        if (header.linkedin.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    try {
                                        val url = if (header.linkedin.startsWith("http")) header.linkedin else "https://${header.linkedin}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {}
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "LinkedIn",
                                    tint = Color(0xFF0B69FF),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = header.linkedin,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF0B69FF),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Profile Picture Frame
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier
                            .width(90.dp)
                            .height(115.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.5.dp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(10.dp))

                // CV Sections
                sections.forEach { section ->
                    if (section.title.isNotBlank() || section.body.isNotBlank()) {
                        CvSectionItem(
                            section = section,
                            currentDateStr = currentDateStr
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "— Page 1 of 1 —",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                }
            }
        }
    }
}

@Composable
private fun CvSectionItem(
    section: CvSection,
    currentDateStr: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Section Title
        Text(
            text = section.title.uppercase(Locale.ROOT),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B)
            )
        )
        Spacer(modifier = Modifier.height(3.dp))
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(6.dp))

        // Section Body with line-by-line smart formatting
        val lines = section.body.split("\n")
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                i++
                continue
            }

            when {
                // Signature line
                line.startsWith("Signature", ignoreCase = true) -> {
                    SignatureItem()
                }
                // Date line
                line.startsWith("Date", ignoreCase = true) -> {
                    DateItem(dateStr = currentDateStr)
                }
                // Bullet points
                line.startsWith("•") || line.startsWith("-") || line.startsWith("*") -> {
                    val clean = line.replaceFirst(Regex("^[•\\-*]\\s*"), "")
                    BulletItem(text = clean)
                }
                // Detail pair (e.g., "Institute : XYZ" or "Name : ABC")
                line.contains(":") && !line.startsWith("http", ignoreCase = true) -> {
                    val parts = line.split(":", limit = 2)
                    val label = parts[0].trim().replaceFirst("^•\\s*".toRegex(), "")
                    val value = parts.getOrNull(1)?.trim() ?: ""
                    DetailRowItem(label = label, value = value)
                }
                // Subheadings (e.g., Company Names or Degree Headings)
                line.startsWith("Colossus Apparel", ignoreCase = true) ||
                line.startsWith("Pacific Quality", ignoreCase = true) ||
                line.startsWith("Bachelor of", ignoreCase = true) ||
                line.startsWith("Higher Secondary", ignoreCase = true) ||
                line.startsWith("Secondary School", ignoreCase = true) -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("Key Responsibilities", ignoreCase = true) -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                else -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(vertical = 1.5.dp)
                    )
                }
            }
            i++
        }
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = Color(0xFF0B69FF),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF334155),
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun DetailRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.widthIn(min = 120.dp, max = 150.dp)
        )
        Text(
            text = ": ",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF334155)
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SignatureItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Signature",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.widthIn(min = 100.dp)
        )
        Text(
            text = ": ",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.signature),
            contentDescription = "Signature",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(42.dp)
                .width(110.dp)
        )
    }
}

@Composable
private fun DateItem(dateStr: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Date",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.widthIn(min = 100.dp)
        )
        Text(
            text = ": ",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Text(
            text = dateStr,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1E293B)
            )
        )
    }
}
