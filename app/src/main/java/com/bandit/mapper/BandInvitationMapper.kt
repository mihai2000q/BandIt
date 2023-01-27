package com.bandit.mapper

import com.bandit.data.db.dto.BandInvitationDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation

object BandInvitationMapper {
    fun fromDtoToItem(dto: BandInvitationDto, band: Band, account: Account): BandInvitation {
        return BandInvitation(
            band,
            account,
            dto.accepted ?: false,
            dto.id ?: -1
        )
    }

    fun fromItemToDto(item: BandInvitation): BandInvitationDto {
        return BandInvitationDto(
            item.id,
            item.band.id,
            item.account.id,
            item.hasAccepted
        )
    }
}