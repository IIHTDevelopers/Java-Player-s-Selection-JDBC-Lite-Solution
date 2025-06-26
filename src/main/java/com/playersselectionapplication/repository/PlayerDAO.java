package com.playersselectionapplication.repository;

import java.sql.SQLException;

import com.playersselectionapplication.model.Player;

public interface PlayerDAO {
	void addPlayer(Player player) throws SQLException;

	void updatePlayer(Player player) throws SQLException;

	int deletePlayer(Player player) throws SQLException;
}
