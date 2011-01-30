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


public class Vect2d {

	public static final Vect2d ZERO = new Vect2d(0.0, 0.0);
	
	private final double x;
	private final double y;
	
	public Vect2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Vect2d plus(Vect2d v) {
		return new Vect2d(x + v.x, y + v.y);
	}
	
	public Vect2d minus(Vect2d v) {
		return new Vect2d(x - v.x, y - v.y);
	}
	
	public Vect2d times(double s) {
		return new Vect2d(x * s, y * s);
	}
	
	// Here are three generalizations of the 3d cross product to 2d.
	// The 3d formula is:
	// a × b = (a2b3 − a3b2) i + (a3b1 − a1b3) j + (a1b2 − a2b1) k
	// If we only consider vectors that either lay in the x-y plane (z == 0)
	// or are perpendicular to is (x == 0 && y == 0) then we get
	// four cases (V=2dvector, S=scalar):
	// V x V = S
	// V x S = V
	// S x V = V
	// S x S = 0
	
	public Vect2d cross(double z) {
		return new Vect2d(y * z, -x * z);
	}
	
	public static Vect2d cross(double z, Vect2d v) {
		return new Vect2d(-z * v.y, z * v.x);
	}
	
	public double cross(Vect2d v) {
		return x * v.y - y * v.x;
	}
	
	public double dot(Vect2d v) {
		return x * v.x + y * v.y;
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public Vect2d normalized() {
		return times(1 / length());
	}

	public boolean isZero() {
		return x == 0.0 && y == 0.0;
	}
	
	@Override
	public String toString() {
		return String.format("<%.3f,%.3f>", x, y);
	}
}
