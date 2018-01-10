package com.waynlaw.albabot.strategist.decision

import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.model.TradeAction.TradeActionVal

/**
  * 사고, 파는 경우에 대한 규칙들을 정의한다.
  */
trait TradeRule {
    /**
      * 사거나 파는 것을 결정하기 위한 정보를 갱신하고 거래를 결정한다.
      */
    def evaluate(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): (TradeRule, Option[TradeActionVal])
}
