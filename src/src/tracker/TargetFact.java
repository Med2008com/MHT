package tracker;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import eu.anorien.mhl.Fact;
import java.awt.geom.Point2D;
import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public class TargetFact implements Fact {

    private static final Logger logger = Logger.getLogger(TargetFact.class);
    private final long id;
    private final long lastDetection;
    private final double x, y, velocityX, velocityY;

    public TargetFact(long id, long lastDetection, double x, double y, double velocityX, double velocityY) {
        this.id = id;
        this.lastDetection = lastDetection;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public boolean measurementInGate(Point2D measurement) {
        return measurement.distance(x + velocityX, y + velocityY) < 7 ? true : false;
    }

    public double measurementProbability(Point2D measurement) {
        double dist = measurement.distance(x + velocityX, y + velocityY);
        return measurementInGate(measurement) ? (dist < 1 ? 1.0 : 1 / dist) : 0.0;
    }

    public long getId() {
        return id;
    }

    public long getLastDetection() {
        return lastDetection;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
