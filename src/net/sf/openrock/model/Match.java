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


public class Match {

	private static final int STONE_COUNT = 8;

	public static final int NO_SCORE = -1;
	
	private String teamName[] = new String[2];
	private int stones[] = new int[2];
	private int score[][] = new int[2][];
	private int turn;
	private int end;
	private int ends;

	public Match(String team1, String team2, int ends) {
		this.teamName[0] = team1;
		this.teamName[1] = team2;
		this.ends = ends;
		stones[0] = STONE_COUNT;
		stones[1] = STONE_COUNT;
		score[0] = new int[ends + 1];
		score[1] = new int[ends + 1];
		for (int e = 0; e < ends + 1; e++) {
			score[0][e] = NO_SCORE;
			score[1][e] = NO_SCORE;
		}
	}

	public String getTeamName(int i) {
		return teamName[i];
	}
	
	public int getStones(int i) {
		return stones[i];
	}
	
	public void nextTurn() {
		stones[turn]--;
		turn = 1 - turn;
	}
	
	public int getTurn() {
		return turn;
	}

	public int getEnd() {
		return end;
	}
	
	public int getEnds() {
		return ends;
	}
	
	public int getScore(int i, int e) {
		return score[i][e];
	}
	
	public int getTotalScore(int i) {
		int s = 0;
		for (int e = 0; e < ends + 1; e++) {
			if (score[i][e] != NO_SCORE) {
				s +=score[i][e];
			}
		}
		return s;
	}

	public void setScore(int i, int points) {
		score[i][end] = points;
	}
	
	public boolean nextEnd(int first) {
		stones[0] = STONE_COUNT;
		stones[1] = STONE_COUNT;
		turn = first;
		end++;
		if (end < ends) {
			return true;
		}
		if (getTotalScore(0) == getTotalScore(1)) {
			// We have played all ends in the match and the score is tied
			// Play the extra end over and over until someone wins
			end = ends;
			score[0][end] = NO_SCORE;
			score[0][end] = NO_SCORE;
			return true;
		}
		stones[0] = 0;
		stones[1] = 0;
		return false;
	}

	public int getWinner() {
		if (getTotalScore(0) > getTotalScore(1)) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
