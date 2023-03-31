import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntervalTest {

    private Interval interval;

    @BeforeEach
    public void setUp() {
        boolean bounded = true;
        interval = new Interval(1, 10, bounded);
    }

    @Test
    public void testBoundedInterval() {
        assert interval.lowerBound() == 1;
        assert interval.upperBound() == 10;
    }

    @Test
    public void testUnboundedInterval() {
        boolean bounded = false;
        interval = new Interval(1, 10, bounded);
        assert interval.lowerBound() == 1;
        assertThrows(IllegalCallerException.class, () -> interval.upperBound());
    }

    @Test
    public void testContains() {
        assert interval.contains(5);
        assert !interval.contains(11);
    }
}
