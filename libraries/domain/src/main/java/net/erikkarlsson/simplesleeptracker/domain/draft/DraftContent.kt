package net.erikkarlsson.simplesleeptracker.domain.draft

import org.threeten.bp.LocalDate

data class DraftContent(val id: Int? = null,
                        val title: String,
                        val description: String,
                        val dateCreated: LocalDate,
                        val images: List<DraftImage>
)
