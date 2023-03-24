package com.bandit.data.mapper

import com.bandit.data.dto.BandInvitationDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation

object BandInvitationMapper {
    fun fromDtoToItem(dto: BandInvitationDto, band: Band, account: Account): BandInvitation {
        return BandInvitation(
            band = band,
            account = account,
            hasAccepted = dto.accepted ?: false,
            id = dto.id
        )
    }

    fun fromItemToDto(item: BandInvitation): BandInvitationDto {
        return BandInvitationDto(
            id = item.id,
            bandId = item.band.id,
            accountId = item.account.id,
            accepted = item.hasAccepted
        )
    }
}