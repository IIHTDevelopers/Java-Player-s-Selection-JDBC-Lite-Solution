package com.assessment.playersselectionapplication.functional;

import static com.assessment.playersseelctionapplication.testutils.TestUtils.businessTestFile;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.currentTest;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.testReport;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.yakshaAssert;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import com.playersselectionapplication.model.Player;
import com.playersselectionapplication.model.Score;
import com.playersselectionapplication.repository.PlayerDAO;
import com.playersselectionapplication.repository.PlayerDAOImpl;
import com.playersselectionapplication.repository.ScoreDAO;
import com.playersselectionapplication.repository.ScoreDAOImpl;

@Component
public class FunctionalTests {

	private static PlayerDAO playerDAO;
	private static ScoreDAO scoreDAO;
	private Player testPlayer;

	private final String url;
	private final String username;
	private final String password;

	private static void createDatabaseIfNotExists(String url, String username, String password) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement()) {

			String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS players_selection";
			statement.executeUpdate(createDatabaseQuery);
		}
	}

	private static void createTablesIfNotExists(String url, String username, String password) throws SQLException {
		String createPlayerTableQuery = "CREATE TABLE IF NOT EXISTS Player (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "name VARCHAR(10) NOT NULL," + "domesticTeam VARCHAR(255) NOT NULL,"
				+ "average INT NOT NULL DEFAULT 0" + ")";

		String createScoreTableQuery = "CREATE TABLE IF NOT EXISTS Score (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "score INT NOT NULL," + "playerId INT NOT NULL," + "FOREIGN KEY (playerId) REFERENCES Player(id)"
				+ ")";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement()) {

			statement.executeUpdate(createPlayerTableQuery);
			statement.executeUpdate(createScoreTableQuery);
		}
	}

	public FunctionalTests() {
		Properties properties = new Properties();
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (inputStream == null) {
				throw new RuntimeException("application.properties file not found");
			}
			properties.load(inputStream);
			url = properties.getProperty("db.url");
			username = properties.getProperty("db.username");
			password = properties.getProperty("db.password");

			try {
				createDatabaseIfNotExists(url, username, password);
				createTablesIfNotExists(url, username, password);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load application.properties file", e);
		}
	}

	@BeforeAll
	static void setup() {
		playerDAO = new PlayerDAOImpl();
		scoreDAO = new ScoreDAOImpl();
	}

	@BeforeEach
	void clearDatabase() {
		try {
			List<Player> players = new ArrayList<>();
			String query = "SELECT * FROM Player";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					Player player = new Player();
					player.setId(resultSet.getInt("id"));
					player.setName(resultSet.getString("name"));
					player.setDomesticTeam(resultSet.getString("domesticTeam"));
					player.setAverage(resultSet.getInt("average"));
					players.add(player);
				}

			}

			List<Score> scores = new ArrayList<>();
			query = "SELECT * FROM Score";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					Score score = new Score();
					score.setId(resultSet.getInt("id"));
					score.setPlayerId(resultSet.getInt("playerId"));
					score.setScore(resultSet.getInt("score"));
					scores.add(score);
				}
			}

			for (Player player : players) {
				playerDAO.deletePlayer(player);
			}
			for (Score score : scores) {
				scoreDAO.deleteScore(score);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	public static void afterAll() {
		testReport();
	}

	@Test
	void addAddPlayerTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);
			Player retrievedPlayer = null;

			String query = "SELECT * FROM Player WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, player.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						retrievedPlayer = new Player();
						retrievedPlayer.setId(resultSet.getInt("id"));
						retrievedPlayer.setName(resultSet.getString("name"));
						retrievedPlayer.setDomesticTeam(resultSet.getString("domesticTeam"));
						retrievedPlayer.setAverage(resultSet.getInt("average"));
					}
				}
			}

			try {
				yakshaAssert(currentTest(), player.getName().equals(retrievedPlayer.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void updateUpdatePlayerByIdTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);

			player.setName("John Doe");
			player.setDomesticTeam("Team B");
			playerDAO.updatePlayer(player);

			Player updatedPlayer = null;

			String query = "SELECT * FROM Player WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, player.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						updatedPlayer = new Player();
						updatedPlayer.setId(resultSet.getInt("id"));
						updatedPlayer.setName(resultSet.getString("name"));
						updatedPlayer.setDomesticTeam(resultSet.getString("domesticTeam"));
						updatedPlayer.setAverage(resultSet.getInt("average"));
					}
				}
			}

			try {
				yakshaAssert(currentTest(), player.getName().equals(updatedPlayer.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteDeletePlayerByIdTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);
			int updatedRows = playerDAO.deletePlayer(player);

			Player retrievedPlayer = null;

			String query = "SELECT * FROM Player WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, player.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						retrievedPlayer = new Player();
						retrievedPlayer.setId(resultSet.getInt("id"));
						retrievedPlayer.setName(resultSet.getString("name"));
						retrievedPlayer.setDomesticTeam(resultSet.getString("domesticTeam"));
						retrievedPlayer.setAverage(resultSet.getInt("average"));
					}
				}
			}

			try {
				yakshaAssert(currentTest(), updatedRows > 0 && retrievedPlayer == null ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void addAddScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);

			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);

			Score retrievedScore = null;
			String query = "SELECT * FROM Score WHERE playerId = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, testPlayer.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					List<Score> scores = new ArrayList<>();

					while (resultSet.next()) {
						Score score = new Score();
						score.setId(resultSet.getInt("id"));
						score.setPlayerId(resultSet.getInt("playerId"));
						score.setScore(resultSet.getInt("score"));
						scores.add(score);
					}
					if (scores.size() > 0) {
						retrievedScore = scores.get(0);
					}
				}
			}

			try {
				yakshaAssert(currentTest(),
						retrievedScore != null && testScore1.getScore() == retrievedScore.getScore()
								&& testScore1.getPlayerId() == retrievedScore.getPlayerId() ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void updateUpdateScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);

			String query = "SELECT * FROM Player WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, testPlayer.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						testPlayer = new Player();
						testPlayer.setId(resultSet.getInt("id"));
						testPlayer.setName(resultSet.getString("name"));
						testPlayer.setDomesticTeam(resultSet.getString("domesticTeam"));
						testPlayer.setAverage(resultSet.getInt("average"));
					}
				}
			}

			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);

			Score retrievedScore = null;
			query = "SELECT * FROM Score WHERE playerId = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, testPlayer.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					List<Score> scores = new ArrayList<>();

					while (resultSet.next()) {
						Score score = new Score();
						score.setId(resultSet.getInt("id"));
						score.setPlayerId(resultSet.getInt("playerId"));
						score.setScore(resultSet.getInt("score"));
						scores.add(score);
					}
					if (scores.size() > 0) {
						retrievedScore = scores.get(0);
					}
				}
			}

			if (retrievedScore != null) {
				retrievedScore.setScore(50);
			}
			scoreDAO.updateScore(retrievedScore);

			Score updatedScore = null;
			query = "SELECT * FROM Score WHERE playerId = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, testPlayer.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					List<Score> scores = new ArrayList<>();

					while (resultSet.next()) {
						Score score = new Score();
						score.setId(resultSet.getInt("id"));
						score.setPlayerId(resultSet.getInt("playerId"));
						score.setScore(resultSet.getInt("score"));
						scores.add(score);
					}
					if (scores.size() > 0) {
						updatedScore = scores.get(0);
					}
				}
			}

			try {
				yakshaAssert(currentTest(), 50 == updatedScore.getScore() ? true : false, businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteDeleteScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);

			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			scoreDAO.deleteScore(testScore1);

			Score retrievedScore = null;
			String query = "SELECT * FROM Score WHERE playerId = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setInt(1, testPlayer.getId());

				try (ResultSet resultSet = statement.executeQuery()) {
					List<Score> scores = new ArrayList<>();

					while (resultSet.next()) {
						Score score = new Score();
						score.setId(resultSet.getInt("id"));
						score.setPlayerId(resultSet.getInt("playerId"));
						score.setScore(resultSet.getInt("score"));
						scores.add(score);
					}
					if (scores.size() != 0) {
						retrievedScore = scores.get(0);
					}
				}
			}

			try {
				yakshaAssert(currentTest(), retrievedScore == null ? true : false, businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
