package com.playersselectionapplication.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.playersselectionapplication.model.Player;

public class PlayerDAOImpl implements PlayerDAO {
	private final String url;
	private final String username;
	private final String password;

	public PlayerDAOImpl() {
		Properties properties = new Properties();
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (inputStream == null) {
				throw new RuntimeException("application.properties file not found");
			}
			properties.load(inputStream);
			url = properties.getProperty("db.url");
			username = properties.getProperty("db.username");
			password = properties.getProperty("db.password");
		} catch (IOException e) {
			throw new RuntimeException("Failed to load application.properties file", e);
		}
	}

	@Override
	public void addPlayer(Player player) throws SQLException {
		String query = "INSERT INTO Player (name, domesticTeam, average) VALUES (?, ?, ?)";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, player.getName());
			statement.setString(2, player.getDomesticTeam());
			statement.setInt(3, player.getAverage());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Adding player failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					player.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Adding player failed, no ID obtained.");
				}
			}
		}
	}

	@Override
	public void updatePlayer(Player player) throws SQLException {
		String query = "UPDATE Player SET name = ?, domesticTeam = ? WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, player.getName());
			statement.setString(2, player.getDomesticTeam());
			statement.setInt(3, player.getId());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Updating player failed, no rows affected.");
			}
		}
	}

	@Override
	public int deletePlayer(Player player) throws SQLException {
		String deleteScoresQuery = "DELETE FROM Score WHERE playerId = ?";
		String deletePlayerQuery = "DELETE FROM Player WHERE id = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement deleteScoresStatement = connection.prepareStatement(deleteScoresQuery);
				PreparedStatement deletePlayerStatement = connection.prepareStatement(deletePlayerQuery)) {
			deleteScoresStatement.setInt(1, player.getId());
			deleteScoresStatement.executeUpdate();
			deletePlayerStatement.setInt(1, player.getId());
			int updatedRows = deletePlayerStatement.executeUpdate();
			return updatedRows;
		}
	}
}
