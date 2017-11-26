
import com.waynlaw.albabot.strategist.{DecisionMaker, Strategist}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.RealNumber
import org.scalatest.FunSuite

class StrategistSuite extends FunSuite {

    val decisionMaker = new DecisionMaker()

    test("시작상태 테스트") {
        val strategist = new Strategist(decisionMaker)
        val (newState, actions) = strategist.compute(StrategistModel(), Event.Tick, 1000 * 1000)

        assert(newState.state.getClass == State.Init().getClass)
        assert(actions.contains(Action.RequestUserBalance))
        assert(actions.contains(Action.RequestCurrency))
    }

    test("사용자 정보 수신 테스트") {
        val strategist = new Strategist(decisionMaker)
        val (newState, actions) = strategist.compute(StrategistModel(state = State.Init()), Event.ReceiveUserBalance(100, RealNumber(200)), 1000 * 1000)

        assert(newState.state == State.WaitingCurrencyInfo())
        assert(actions.contains(Action.RequestCurrency))
        assert(newState.krw == 100)
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(RealNumber(200), 0, CryptoCurrencyState.UnknownPrice)))
    }

    test("화폐 정보 수신 테스트") {
        val strategist = new Strategist(decisionMaker)

        var state: StrategistModel = StrategistModel(
            state = State.WaitingCurrencyInfo(),
            cryptoCurrency = List(CryptoCurrencyInfo(RealNumber(200), 0, CryptoCurrencyState.UnknownPrice))
        )

        for (i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt) {
            val (newState, _) = strategist.compute(state, Event.ReceivePrice(i * 100, i * 200 + 100), i * 300)
            state = newState
        }

        assert(state.state == State.Trading)
        assert(state.cryptoCurrency == List(CryptoCurrencyInfo(RealNumber(200), 100, CryptoCurrencyState.Nothing)))
    }

    test("일반 상태 테스트") {
        val strategist = new Strategist(decisionMaker, 20)

        val currencyHistory = for {
            i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt
        } yield CurrencyInfo(201, 200 * i)

        val state: StrategistModel = StrategistModel(
            krw = 10001,
            state = State.Trading,
            history = currencyHistory.toArray
        )

        val timestamp = 300

        val (newState, action) = strategist.compute(state, Event.ReceivePrice(0, 201), timestamp)
        val buyPrice = 201 / 20 * 20
        val buyAmount = RealNumber(10001).divide(buyPrice, 0)

        assert(action.contains(Action.RequestBuy(buyAmount, buyPrice, timestamp)))
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(buyAmount, buyPrice, CryptoCurrencyState.TryToBuy(timestamp))))
    }

    test("구매 주문 반영 테스트") {
        val strategist = new Strategist(decisionMaker, 20)

        val currencyHistory = for {
            i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt
        } yield CurrencyInfo(201, 200 * i)

        val timestamp = 300

        val state: StrategistModel = StrategistModel(
            krw = 10001,
            state = State.Trading,
            history = currencyHistory.toArray,
            cryptoCurrency = List(CryptoCurrencyInfo(RealNumber(200), 400, CryptoCurrencyState.TryToBuy(timestamp)))
        )



        val (newState, _) = strategist.compute(state, Event.BuyingOrderConfirmed(timestamp, "id123"), timestamp + 100)
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(RealNumber(200), 400, CryptoCurrencyState.WaitingForBuying("id123", Nil))))
    }

    test("구매 체결 테스트") {
        val strategist = new Strategist(decisionMaker, 20)

        val currencyHistory = for {
            i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt
        } yield CurrencyInfo(201, 200 * i)

        val timestamp = 300

        val beginKrw = 10000000
        val state: StrategistModel = StrategistModel(
            krw = beginKrw,
            state = State.Trading,
            history = currencyHistory.toArray,
            cryptoCurrency = List(CryptoCurrencyInfo(RealNumber(200), 400, CryptoCurrencyState.WaitingForBuying("id123", Nil)))
        )



        val (newState, action) = strategist.compute(state, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t1", RealNumber(50), 0, RealNumber(1)))), timestamp + 100)
        assert(action.contains(Action.RequestTradingInfo("id123", isBuying = true)))
        assert(newState.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(149), 400, CryptoCurrencyState.WaitingForBuying("id123", List("t1")))))
        assert(newState.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(50), 400, CryptoCurrencyState.Nothing)))
        assert(newState.krw == beginKrw)

        val (newState2, action2) = strategist.compute(newState, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t2", RealNumber(50), 10, RealNumber(1)))), timestamp + 100)
        assert(action2.contains(Action.RequestTradingInfo("id123", isBuying = true)))
        assert(newState2.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(98), 400, CryptoCurrencyState.WaitingForBuying("id123", List("t2", "t1")))))
        assert(newState2.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(100), 400, CryptoCurrencyState.Nothing)))
        assert(newState2.krw == beginKrw + 50 * 10)

        val (newState3, action3) = strategist.compute(newState2, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t3", RealNumber(97), 0, RealNumber(1)))), timestamp + 100)
        assert(newState3.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(197), 400, CryptoCurrencyState.Nothing)))

        val (_, action4) = strategist.compute(newState3, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t3", RealNumber(97), 0, RealNumber(1)))), timestamp + 100)
        assert(!action4.contains(Action.RequestTradingInfo("id123", isBuying = true)))
    }

    test("판매 주문 반영 테스트") {
        val strategist = new Strategist(decisionMaker, 20)

        val currencyHistory = for {
            i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt
        } yield CurrencyInfo(60001 - i, 200 * i)

        val timestamp = 1300

        val state: StrategistModel = StrategistModel(
            krw = 1,
            state = State.Trading,
            history = currencyHistory.toArray,
            cryptoCurrency = List(CryptoCurrencyInfo(RealNumber(200), 400, CryptoCurrencyState.Nothing))
        )

        val (newState, action) = strategist.compute(state, Event.Tick, timestamp)

        assert(action.contains(Action.RequestSell(RealNumber(200), 59940, timestamp)))
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(RealNumber(200), 59940, CryptoCurrencyState.TryToSell(timestamp))))


        val (newState2, _) = strategist.compute(newState, Event.SellingOrderConfirmed(timestamp, "id123"), timestamp + 100)
        assert(newState2.cryptoCurrency == List(CryptoCurrencyInfo(RealNumber(200), 59940, CryptoCurrencyState.WaitingForSelling("id123", Nil))))

        val (newState3, action2) = strategist.compute(newState2, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t1", RealNumber(50), 0, RealNumber(1)))), timestamp + 100)
        assert(action2.contains(Action.RequestTradingInfo("id123", isBuying = false)))
        assert(newState3.cryptoCurrency.contains(CryptoCurrencyInfo(RealNumber(150), 59940, CryptoCurrencyState.WaitingForSelling("id123", List("t1")))))

        val (newState4, action3) = strategist.compute(newState3, Event.ReceiveOrderInfo("id123", List(Event.ConfirmOrderInfo("t2", RealNumber(150), 0, RealNumber(1)))), timestamp + 100)
        assert(action3.contains(Action.RequestTradingInfo("id123", isBuying = false)))
        assert(newState4.cryptoCurrency.isEmpty)
    }
}
