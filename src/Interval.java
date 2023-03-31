public record Interval(int lowerBound, int upperBound, boolean bounded) {

    /**
     * @return the upper bound of the interval
     * @throws IllegalCallerException if the interval is unbounded
     */
    @Override
    public int upperBound() {
        if (bounded) {
            return upperBound;
        } else {
            throw new IllegalCallerException("Unbounded interval");
        }
    }

    public boolean contains(int i) {
        if (bounded) {
            return i >= lowerBound && i <= upperBound;
        } else {
            return i >= lowerBound;
        }
    }
}