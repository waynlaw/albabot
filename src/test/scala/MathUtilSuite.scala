
import com.waynlaw.albabot.strategist.model.CurrencyInfo
import com.waynlaw.albabot.util.MathUtil
import org.scalatest.FunSuite

class MathUtilSuite extends FunSuite {

    test("removeNoise") {
        val input = Array(
            CurrencyInfo(20, 1),
            CurrencyInfo(10, 2),
            CurrencyInfo(30, 3),
            CurrencyInfo(50, 4),
            CurrencyInfo(40, 5),
        )

        assert(MathUtil.removeNoise(input) sameElements Array(
            CurrencyInfo(20, 1),
            CurrencyInfo(30, 3),
            CurrencyInfo(40, 5),
        ))
    }

    test("removeNoise2") {
        val input = Array(
            CurrencyInfo(30, 1),
            CurrencyInfo(40, 2),
            CurrencyInfo(10, 3),
            CurrencyInfo(20, 4),
            CurrencyInfo(50, 5),
            CurrencyInfo(60, 6),
            CurrencyInfo(70, 7),
            CurrencyInfo(100, 8),
            CurrencyInfo(90, 9),
            CurrencyInfo(80, 10),
        )

        assert(MathUtil.removeNoise(input) sameElements Array(
            CurrencyInfo(30, 1),
            CurrencyInfo(40, 2),
            CurrencyInfo(20, 4),
            CurrencyInfo(50, 5),
            CurrencyInfo(60, 6),
            CurrencyInfo(70, 7),
            CurrencyInfo(90, 9),
            CurrencyInfo(80, 10),
        ))
    }

    test("computeAngle") {
        val input = Array(
            CurrencyInfo(10, 10),
            CurrencyInfo(20, 20),
            CurrencyInfo(30, 30),
            CurrencyInfo(40, 40),
            CurrencyInfo(50, 50)
        )

        assert(MathUtil.computeAngle(input) == 1)
    }

    test("computeAngle2") {
        val input = Array(
            CurrencyInfo(15, 10),
            CurrencyInfo(30, 20),
            CurrencyInfo(45, 30),
            CurrencyInfo(60, 40),
            CurrencyInfo(75, 50)
        )

        assert(MathUtil.computeAngle(input) == 1.5)
    }

    test("computeAngle3") {
        val input = Array(
            CurrencyInfo(-10, 10),
            CurrencyInfo(-20, 20),
            CurrencyInfo(-30, 30),
            CurrencyInfo(-40, 40),
            CurrencyInfo(-50, 50)
        )

        assert(MathUtil.computeAngle(input) == -1)
    }

    test("computeAngle4") {
        val input = Array(
            CurrencyInfo(-10, 20),
            CurrencyInfo(-20, 30),
            CurrencyInfo(-30, 40),
            CurrencyInfo(-40, 50),
            CurrencyInfo(-50, 60)
        )

        assert(MathUtil.computeAngle(input) == -1)
    }
}
