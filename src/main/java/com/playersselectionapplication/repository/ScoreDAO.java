package com.playersselectionapplication.repository;

import java.sql.SQLException;

import com.playersselectionapplication.model.Score;

public interface ScoreDAO {
	void addScore(Score score) throws SQLException;

	void updateScore(Score score) throws SQLException;

	void deleteScore(Score score) throws SQLException;
}
