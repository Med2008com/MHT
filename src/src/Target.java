
import static java.lang.Math.*;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public class Target {

    private static final Logger logger = Logger.getLogger(Target.class);
    private double x, y, heading, velocity;
     // Save our DataSet
    
    
    public Target(double ID, double a, double b) {
    	
    	
        x = min (800, a);
        y = min(800, b);
        heading = random() * 2 * PI;
        velocity = random() * 5;
    }

    public void update(double a, double b) { //  public void update()
        /*heading = (random() - 0.5) + heading;
        velocity = min(5, max(0, velocity + random()));
        x = min(390, max(10, x + velocity * cos(heading)));
        y = min(390, max(10, y + velocity * sin(heading)));
        */
    	x = min(790,a); 
    	y = min(790, b) ; 
    	
    	
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
