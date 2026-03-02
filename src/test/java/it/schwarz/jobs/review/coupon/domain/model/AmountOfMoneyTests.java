package it.schwarz.jobs.review.coupon.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmountOfMoneyTests {

    @Test
    void testZero() {
        assertThat(AmountOfMoney.ZERO.toBigDecimal()).isZero();
    }

    @Test
    void testIsGreaterThan() { // can make parameterized test case and reduce no of lines
        var amount = AmountOfMoney.of("1.23");

        assertTrue(amount.isGreaterThan(AmountOfMoney.of("1.22")));
        assertTrue(amount.isGreaterThan(AmountOfMoney.of("1.00")));
        assertTrue(amount.isGreaterThan(AmountOfMoney.of("0")));
        assertTrue(amount.isGreaterThan(AmountOfMoney.of("-1.23")));
        assertFalse(amount.isGreaterThan(AmountOfMoney.of("1.23")));
        assertFalse(amount.isGreaterThan(AmountOfMoney.of("1.3")));
    }

    @Test
    void testIsLessThan() {
        var amount = AmountOfMoney.of("1.23");

        assertFalse(amount.isLessThan(AmountOfMoney.of("1.22")));
        assertFalse(amount.isLessThan(AmountOfMoney.of("1.00")));
        assertFalse(amount.isLessThan(AmountOfMoney.of("0")));
        assertFalse(amount.isLessThan(AmountOfMoney.of("-1.23")));
        assertFalse(amount.isLessThan(AmountOfMoney.of("1.23")));
        assertTrue(amount.isLessThan(AmountOfMoney.of("1.3")));
    }

    @Test
    void testComparisonWithNull() {
        var amount = AmountOfMoney.of("1.23");
        assertThatNullPointerException().isThrownBy(
                () -> amount.isGreaterThan(null)
        );
    }

    @Test
    void testCreationWithNull() {
        assertThatNullPointerException().isThrownBy(
                () -> AmountOfMoney.of((String)null)
        );
    }

    @Test
    void ofStringWithEmptyStringShouldThrow() {
        assertThatIllegalArgumentException().isThrownBy(() -> AmountOfMoney.of(""));
    }

    @Test
    void ofStringWithNonNumericStringShouldThrow() {
        assertThatIllegalArgumentException().isThrownBy(() -> AmountOfMoney.of("abc"));
    }

}