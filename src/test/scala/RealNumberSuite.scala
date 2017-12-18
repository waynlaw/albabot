import com.waynlaw.albabot.util.RealNumber
import org.scalatest.FunSuite

class RealNumberSuite extends FunSuite {
    test("from String") {
        assert(RealNumber("123") == RealNumber(123, 0))
        assert(RealNumber("123.4") == RealNumber(1234, -1))
        assert(RealNumber("123.45") == RealNumber(12345, -2))
        assert(RealNumber("123.456") == RealNumber(123456, -3))
    }

    test("add") {
        assert(RealNumber(10) + RealNumber(23) == RealNumber(33))
        assert(RealNumber("0.1") + RealNumber("0.01") == RealNumber("0.11"))
    }

    test("sub") {
        assert(RealNumber(10) - RealNumber(23) == RealNumber(-13))
        assert(RealNumber("0.1") - RealNumber("0.01") == RealNumber("0.09"))
    }

    test("div") {
        assert(RealNumber("0.444").divide(2, -2) == RealNumber("0.22"))
        assert(RealNumber("0.4").divide(3, -2) == RealNumber("0.13"))
    }

    test("div real number") {
        assert(RealNumber("0.2").divide(RealNumber("0.2"), -4) == RealNumber("1.0"))
        assert(RealNumber("0.04").divide(RealNumber("2"), -2) == RealNumber("0.02"))
        assert(RealNumber("0.04").divide(RealNumber("2"), -1) == RealNumber("0"))
        assert(RealNumber("40").divide(RealNumber("0.2"), -2) == RealNumber("200"))
        assert(RealNumber("10").divide(RealNumber("0.3"), -3) == RealNumber("33.333"))
    }
}
