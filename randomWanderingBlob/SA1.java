/*
@Author Sunint Bindra
April 2, 2020
CS10
 */

public class SA1 extends Blob {
    protected int count;
    protected int steps;
    public SA1(double x, double y) {
        super(x, y);
        steps = 30 - (int)(Math.random() * 19);
        count = 0;
        dx = 2 * (Math.random() - 0.5);
        dy = 2 * (Math.random() - 0.5);
    }

    @Override
    public void step() {
        // Counts steps until all steps have been taken, then changes dx and dy to new random value b/w -1 and 1
        count += 1;
        if (count == steps) {
            dx = 2 * (Math.random() - 0.5);
            dy = 2 * (Math.random() - 0.5);
            count = 0;
        }
        x += dx;
        y += dy;
    }
}

