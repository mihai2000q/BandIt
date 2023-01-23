package com.bandit.data.db.entry

data class BandDBEntry(
    override val id: Long,
    val name: String,
    val membersIds: Array<Long>
) : BaseEntry(id) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BandDBEntry

        if (id != other.id) return false
        if (name != other.name) return false
        if (!membersIds.contentEquals(other.membersIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + membersIds.contentHashCode()
        return result
    }
}