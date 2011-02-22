


package net.sf.openrock.game;

import java.util.logging.Logger;

import net.sf.openrock.model.World;
import net.sf.openrock.model.Vect2d;
import net.sf.openrock.model.CurlingConstants;
import net.sf.openrock.game.PhysicsIF;

// factory
public class Physics
{
	static PhysicsIF CreateDefaultPhysics(World world)
	{
		return new DefaultPhysics(world);
	}


	/* use common defaultPhysics object as flyweight */
	static PhysicsIF CreateErrorPhysics(PhysicsIF dflt,
				double speederror, double direrror)
	{
		PhysicsIF ret = dflt;

		/* introduce optional speed and dir error wrappers */
		if (speederror != 0.0)
		{
			ret = new PhysicsSpeedError(ret, speederror);
		}

		if (direrror != 0.0)
		{
			ret = new PhysicsDirError(ret, direrror);
		}

		return ret;
	}

	/* each player could have their own error wrappers */
	static PhysicsIF CreateErrorPhysics(World world,
			double speederror, double direrror)
	{
		PhysicsIF dflt = new DefaultPhysics(world);
		return CreateErrorPhysics(dflt, speederror, direrror);
	}

}


class DefaultPhysics implements PhysicsIF
{

	private final World world;

	public DefaultPhysics(World world)
	{
		this.world = world;
	}

	public double getSpeed(double speed)
	{
		speed = Math.pow(speed, 1.5);
		double v = (1.0 - speed) * 2.3 + 3.0 * speed;
		return v;
	}

	// no directional error
	public Vect2d getDir()
	{
		Vect2d broom = new Vect2d(world.getBroomX(), world.getBroomY());
		Vect2d dir = broom.minus(
				CurlingConstants.STONE_START).normalized();
		return dir;
	}
}

abstract class PhysicsDecorator implements PhysicsIF
{
	private PhysicsIF component;

	PhysicsDecorator(PhysicsIF component)
	{
		this.component = component;
	}

	public double getSpeed(double speed)
	{
		return component.getSpeed(speed);
	}

	public Vect2d getDir()
	{
		return component.getDir();
	}
}


// some static methods
class PhysicsHelper {

	/* return orig with random error plus or minus error
	   e.g. return as low as orig - orig*error or as high
	   as orig + orig*error
	   Call as val = introduceError(val, 0.10) for 10% error
	 */
	static double introduceError(double orig, double error)
	{
		return orig;
	}

	static Vect2d introduceError(Vect2d orig, double error)
	{
		double x = introduceError(orig.getX(), error);
		double y = introduceError(orig.getY(), error);

		return new Vect2d(x,y);
	}
}


class PhysicsSpeedError extends PhysicsDecorator
{

	private double speederror;

	public PhysicsSpeedError(PhysicsIF component, double speederror)
	{
		super(component);
		this.speederror = speederror;
	}

	public double getSpeed(double speed)
	{
		speed = super.getSpeed(speed);
		// modify speed by plus or minus 0-10%, randomly
		return PhysicsHelper.introduceError(speed, speederror);
	}
}

class PhysicsDirError extends PhysicsDecorator
{
	private double direrror;

	public PhysicsDirError(PhysicsIF component, double direrror)
	{
		super(component);
		this.direrror = direrror;
	}

	public Vect2d getDir()
	{
		Vect2d dir = super.getDir();
		// modify X and Y by plus or minus 0-10%, randomly
		return PhysicsHelper.introduceError(dir, direrror);
	}
}
