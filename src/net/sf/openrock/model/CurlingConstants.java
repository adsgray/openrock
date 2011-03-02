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

import java.awt.Color;


public class CurlingConstants {

	public static final double ICE_WIDTH = footToMeter(16.0 + 5.0/12);
	public static final double ICE_LENGTH = footToMeter(150);
	public static final double TEE_TO_CENTER = footToMeter(114/2);
	public static final double TEE_TO_HOG = footToMeter(21);
	public static final double TEE_TO_HACK = footToMeter(12);
	public static final double TWELVE_FEET_RADIUS = footToMeter(6);
	public static final double EIGHT_FEET_RADIUS = footToMeter(4);
	public static final double FOUR_FEET_RADIUS = footToMeter(2);
	public static final double ONE_FEET_RADIUS = footToMeter(0.5);
	public static final double THIN_LINE = footToMeter(0.5 / 12);
	public static final double THICK_LINE = footToMeter(4.0 / 12);
	// have the rock run straight until 1/4 down the sheet
	public static final double RELEASE_POINT = -TEE_TO_HOG - TEE_TO_HACK; 
	
	public static final Vect2d STONE_START = new Vect2d(0.0, -CurlingConstants.TEE_TO_CENTER - CurlingConstants.TEE_TO_HOG + 0.5);

	public static final Color[] TEAM_COLORS = new Color[] { Color.YELLOW, Color.RED };

	public static final Color[] SHOTSTONE_COLORS = new Color [] {
		new Color (238, 221, 130, 255), // light goldenrod
		new Color (255, 99,   71, 255)  // tomato
	};
	
	private static double footToMeter(double f) {
		return f * 0.3048;
	}

}
