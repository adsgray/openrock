/*
 * Copyright (C) 2009  Daniel Nilsson
 * 
 * This file is part of OpenRock.
 *
 * OpenRock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenRock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenRock.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.openrock.model;


public class Stone implements Cloneable {

	private int team;
	private Vect2d position;
	private Vect2d velocity;
	private double a;
	private double da;
	private double sweep;
	private boolean hit;
	private boolean freeGuard;
	private boolean shotstone = false;
	private CurlIF curl;

	public Stone(Vect2d pos, Vect2d vel, double a, double da, 
			int team) {
		position = pos;
		velocity = vel;
		this.a = a;
		this.da = da;
		this.team = team;
	}

	public void setCurl(CurlIF curl)
	{
		this.curl = curl;
	}

	private double getCurl(double s)
	{
		double curlamt = 0;

		// no curl until release point
		if (position.getY() > CurlingConstants.RELEASE_POINT)
		{
			curlamt = curl.getCurl(da, s, sweep);
		}

		return curlamt;
	}

	public void step(double dt) {
		double s = velocity.length();
		Vect2d acc = Vect2d.ZERO;
		double dda = 0;
		if (s > 0.01) {
			/* ADSG: how to put this into PhysicsIF */
			Vect2d dir = velocity.normalized();

			double curl = getCurl(s);
			double friction = getFriction(sweep);
			acc = dir.times(-friction).plus(dir.cross(curl));
			dda = -Math.max(0.02, Math.min(0.02 / s, 0.3)) * 
				Math.signum(da);

		} else {
			velocity = Vect2d.ZERO;
			dda = -0.3 * Math.signum(da);
		}
		if (Math.abs(da) < 0.01) {
			da = 0;
			dda = 0;
		}
		position = position.plus(velocity.times(dt)).plus(acc.times(dt*dt/2));
		a += da * dt + dda * dt*dt / 2;
		velocity = velocity.plus(acc.times(dt));
		da += dda * dt;
	}

	private double getFriction(double s) {
		return (1.0 - s) * 0.08 + s * 0.072;
	}
	
	public Vect2d getPosition() {
		return position;
	}

	public Vect2d getVelocity() {
		return velocity;
	}
	
	public double getA() {
		return a;
	}

	public double getDa() {
		return da;
	}

	public double getRadius() {
		return 0.145;
	}

	public double getMass() {
		return 18.0;
	}

	public double getMomentOfInertia() {
		return getMass() * getRadius()*getRadius() / 2;
	}
	
	public int getTeam() {
		return team;
	}

	public void setTeam(int newteam) {
		team = newteam;
	}
	
	/**
	 * Apply an impulse to the stone.
	 * @param i the impulse vector (Ns).
	 * @param p the position of the impulse relative to the stone center.
	 */
	public void applyImpulse(Vect2d i, Vect2d p) {
		velocity = velocity.plus(i.times(1/getMass()));
		da = da + p.cross(i) / getMomentOfInertia();
	}

	public boolean isLive() {
		return !velocity.isZero() || da != 0.0;
	}

	public Vect2d getExtrapolatedPosition() {
		double s = velocity.length();
		return position.plus(velocity.times(s/(2*getFriction(0.25))));
	}

	public void setSweep(double sweep) {
		this.sweep = sweep;
	}

	public double getSweep() {
		return sweep;
	}
	
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	public boolean isHit() {
		return hit;
	}

	public void setFreeGuard(boolean freeGuard) {
		this.freeGuard = freeGuard;
	}
	
	public boolean isFreeGuard() {
		return freeGuard;
	}

	public void setShot(boolean shotstone)
	{
		this.shotstone = shotstone;
	}

	public boolean getShot()
	{
		return shotstone;
	}
	
	@Override
	public Stone clone() {
		try {
			return (Stone) super.clone();
		} catch(CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
}
