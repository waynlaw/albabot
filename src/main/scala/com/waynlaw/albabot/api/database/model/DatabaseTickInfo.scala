package com.waynlaw.albabot.api.database.model

case class DatabaseTickInfo(
  id: Long,
  coinType: String,
  openingPrice: Long,
  closingPrice: Long,
  minPrice: Long,
  maxPrice: Long,
  averagePrice: Long,
  unitsTraded: Long,
  volume1day: Long,
  volume7day: Long,
  buyPrice: Long,
  sellPrice: Long,
  regDate: String
)
