package com.assessment.playersseelctionapplication.testutils;

import java.util.ArrayList;
import java.util.List;

import com.playersselectionapplication.model.Player;
import com.playersselectionapplication.model.Score;

public class MasterData {
	public static Player getPlayer() {
		Player player = new Player(1, "ABC", "XYZ", 33);
		return player;
	}

	public static List<Player> getPlayersList() {
		List<Player> players = new ArrayList<>();

		Player player = new Player(1, "ABC", "XYZ", 33);
		players.add(player);
		player = new Player(2, "PQR", "GHI", 40);
		players.add(player);

		return players;
	}

	public static Score getScore() {
		Score score = new Score(1, 100, getPlayer().getId());
		return score;
	}

	public static List<Score> getScoresList() {
		List<Score> scores = new ArrayList<>();

		Score score = new Score(1, 13, getPlayersList().get(0).getId());
		scores.add(score);
		score = new Score(2, 67, getPlayersList().get(0).getId());
		scores.add(score);

		return scores;
	}
}
