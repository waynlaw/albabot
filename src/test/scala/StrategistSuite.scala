
import com.waynlaw.albabot.strategist.Strategist
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.StringUtils
import org.scalatest.FunSuite

class StrategistSuite extends FunSuite {

    test("시작상태 테스트") {
        val strategist = new Strategist()
        val (newState, actions) = strategist.compute(StrategistModel(), Event.Tick, 1000 * 1000)

        assert(newState.state == State.Init)
        assert(actions.contains(Action.RequestUserBalance))
        assert(actions.contains(Action.RequestCurrency))
    }

    test("사용자 정보 수신 테스트") {
        val strategist = new Strategist()
        val (newState, actions) = strategist.compute(StrategistModel(state = State.Init), Event.ReceiveUserBalance(100, 200), 1000 * 1000)

        assert(newState.state == State.WaitingCurrencyInfo)
        assert(actions.contains(Action.RequestCurrency))
        assert(newState.krw == 100)
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(200, 0, CryptoCurrencyState.UnknownPrice)))
    }

    test("화폐 정보 수신 테스트") {
        val strategist = new Strategist()

        var state: StrategistModel = StrategistModel(
            state = State.WaitingCurrencyInfo,
            cryptoCurrency = List(CryptoCurrencyInfo(200, 0, CryptoCurrencyState.UnknownPrice))
        )

        for (i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt) {
            val (newState, _) = strategist.compute(state, Event.ReceivePrice(i * 100, i * 200 + 100), i * 300)
            state = newState
        }

        assert(state.state == State.Trading)
        assert(state.cryptoCurrency == List(CryptoCurrencyInfo(200, 100, CryptoCurrencyState.Nothing)))
    }

    test("일반 상태 테스트") {
        val strategist = new Strategist(10, 20)

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
        val buyAmount = BigInt(10001) * StringUtils.SATOSHI_UNIT / buyPrice / 10 * 10

        assert(action.contains(Action.RequestBuy(buyAmount, buyPrice, timestamp)))
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(buyAmount, buyPrice, CryptoCurrencyState.TryToBuy(timestamp))))
    }

    test("구매 주문 반영 테스트") {
        val strategist = new Strategist(10, 20)

        val currencyHistory = for {
            i <- 0 to Strategist.HISTORY_MINIMUM_FOR_TRADING.toInt
        } yield CurrencyInfo(201, 200 * i)

        val timestamp = 300

        val state: StrategistModel = StrategistModel(
            krw = 10001,
            state = State.Trading,
            history = currencyHistory.toArray,
            cryptoCurrency = List(CryptoCurrencyInfo(200, 400, CryptoCurrencyState.TryToBuy(timestamp)))
        )



        val (newState, _) = strategist.compute(state, Event.BuyingOrderConfirmed(timestamp, "id123"), timestamp + 100)
        assert(newState.cryptoCurrency == List(CryptoCurrencyInfo(200, 400, CryptoCurrencyState.WaitingForBuying("id123", 200))))
    }
}
