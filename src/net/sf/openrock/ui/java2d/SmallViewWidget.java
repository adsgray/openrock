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
package net.sf.openrock.ui.java2d;

import java.awt.Graphics2D;

class SmallViewWidget extends ViewWidget {

	private volatile double range = 35;

	SmallViewWidget()
	{
		//orientation = -1;
	}

	@Override
	protected void renderTranslate(Graphics2D g2d)
	{
		g2d.translate(getWidth()/2, getHeight()/2);
		double s = getHeight() / range;
		g2d.scale(s, s * orientation);
		g2d.translate(-scrollX, -scrollY);
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(renderWidth - (10 + renderWidth/10), 30, 
			renderWidth / 10, 5 * renderHeight/10);
	}

}
