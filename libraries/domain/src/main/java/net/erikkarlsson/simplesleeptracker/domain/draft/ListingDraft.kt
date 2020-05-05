package net.erikkarlsson.simplesleeptracker.domain.draft

import org.threeten.bp.LocalDate

data class ListingDraft(val id: Long? = null,
                   val templateName: String,
                   val dateCreated: LocalDate,
                   val draftContent: DraftContent
) {
    fun businesslogic() {

    }
}
