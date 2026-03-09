package edu.nd.pmcburne.hwapp.one.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity

@Composable
fun GameCard(game: GameEntity, modifier: Modifier = Modifier) {
    val isLive = game.gameState == "in"
    val isPost = game.gameState == "post"
    val isPre = game.gameState == "pre"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (badgeText, bgColor, textColor) = when {
                    isLive -> Triple(game.statusDetail ?: "In Progress", Color(0xFF1B5E20), Color.White)
                    isPost -> Triple("Final", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                    else  -> Triple("Upcoming", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = bgColor) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isLive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, RoundedCornerShape(50)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LIVE", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TeamRow(
                label = "Away",
                name = game.awayTeamName,
                score = game.awayScore,
                isWinner = game.awayWinner && isPost,
                showScore = !isPre
            )

            Spacer(modifier = Modifier.height(6.dp))

            TeamRow(
                label = "Home",
                name = game.homeTeamName,
                score = game.homeScore,
                isWinner = game.homeWinner && isPost,
                showScore = !isPre
            )

            if (isPre && game.statusDetail != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⏰ ${game.statusDetail}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TeamRow(label: String, name: String, score: String?, isWinner: Boolean, showScore: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(34.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (showScore && score != null) {
            Text(
                text = score,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(48.dp)
            )
        }
        if (isWinner) {
            Text("✓", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp))
        }
    }
}