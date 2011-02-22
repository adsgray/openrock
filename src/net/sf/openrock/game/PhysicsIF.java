
package net.sf.openrock.game;

import net.sf.openrock.model.Vect2d;

public interface PhysicsIF
{
	double getSpeed(double speed); // calc speed from given gui speed
	Vect2d getDir(); // vector for stone direction
}
