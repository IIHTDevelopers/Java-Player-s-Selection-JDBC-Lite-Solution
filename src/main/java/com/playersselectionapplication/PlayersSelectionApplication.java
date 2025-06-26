package com.playersselectionapplication;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import org.jboss.jandex.Main;

import com.playersselectionapplication.model.Player;
import com.playersselectionapplication.model.Score;
import com.playersselectionapplication.repository.PlayerDAO;
import com.playersselectionapplication.repository.PlayerDAOImpl;
import com.playersselectionapplication.repository.ScoreDAO;
import com.playersselectionapplication.repository.ScoreDAOImpl;

public class PlayersSelectionApplication {
	private static PlayerDAO playerDAO;
	private static ScoreDAO scoreDAO;
	private static String url;
	private static String username;
	private static String password;

	public static void main(String[] args) {
		try {
			InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties");
			Properties props = new Properties();
			props.load(input);
			url = props.getProperty("db.url");
			username = props.getProperty("db.username");
			password = props.getProperty("db.password");

			createDatabaseIfNotExists(url, username, password);
			createTablesIfNotExists(url, username, password);

			playerDAO = new PlayerDAOImpl();
			scoreDAO = new ScoreDAOImpl();

			showOptions();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createDatabaseIfNotExists(String url, String username, String password) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement()) {

			String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS your_database_name";
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

	private static void showOptions() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		int option = -1;

		while (option != 0) {
			System.out.println("Select an option:");
			System.out.println("1. Add a player");
			System.out.println("2. Add a score");
			System.out.println("3. Update player");
			System.out.println("4. Update score");
			System.out.println("5. Delete player");
			System.out.println("6. Delete score");
			System.out.println("0. Exit");

			try {
				option = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
			} catch (InputMismatchException e) {
				System.out.println("Invalid input!");
				scanner.nextLine(); // Consume the invalid input
				continue;
			}

			switch (option) {
			case 1:
				addPlayer(scanner);
				break;
			case 2:
				addScore(scanner);
				break;
			case 3:
				updatePlayer(scanner);
				break;
			case 4:
				updateScore(scanner);
				break;
			case 5:
				deletePlayer(scanner);
				break;
			case 6:
				deleteScore(scanner);
				break;
			case 0:
				System.out.println("Exiting the application.");
				break;
			default:
				System.out.println("Invalid option!");
				break;
			}
		}

		scanner.close();
	}

	private static void addPlayer(Scanner scanner) throws SQLException {
		System.out.println("Enter player details:");
		System.out.print("Name: ");
		String name = scanner.nextLine();
		System.out.print("Domestic Team: ");
		String domesticTeam = scanner.nextLine();

		Player player = new Player(name, domesticTeam);
		playerDAO.addPlayer(player);
		System.out.println("Player added successfully.");
	}

	private static void addScore(Scanner scanner) throws SQLException {
		System.out.println("Enter score details:");
		System.out.print("Player ID: ");
		int playerId = scanner.nextInt();
		System.out.print("Score: ");
		int score = scanner.nextInt();

		Score scoreObj = new Score(playerId, score);
		scoreDAO.addScore(scoreObj);
		System.out.println("Score added successfully.");
	}

	private static void updatePlayer(Scanner scanner) throws SQLException {
		System.out.print("Enter the player ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		Player player = null;
		String query = "SELECT * FROM Player WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					player = new Player();
					player.setId(resultSet.getInt("id"));
					player.setName(resultSet.getString("name"));
					player.setDomesticTeam(resultSet.getString("domesticTeam"));
					player.setAverage(resultSet.getInt("average"));
				}
			}
		}

		if (player == null) {
			System.out.println("Player not found!");
			return;
		}

		System.out.println("Current player details:");
		System.out.println(player);

		System.out.print("Enter new name (leave blank to keep the current value): ");
		String newName = scanner.nextLine();
		System.out.print("Enter new domestic team (leave blank to keep the current value): ");
		String newDomesticTeam = scanner.nextLine();

		if (!newName.isEmpty()) {
			player.setName(newName);
		}

		if (!newDomesticTeam.isEmpty()) {
			player.setDomesticTeam(newDomesticTeam);
		}

		playerDAO.updatePlayer(player);
		System.out.println("Player updated successfully.");
	}

	private static void updateScore(Scanner scanner) throws SQLException {
		System.out.print("Enter the score ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		Score score = null;
		String query = "SELECT * FROM Score WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					score = new Score();
					score.setId(resultSet.getInt("id"));
					score.setPlayerId(resultSet.getInt("playerId"));
					score.setScore(resultSet.getInt("score"));
				}
			}
		}

		if (score == null) {
			System.out.println("Score not found!");
			return;
		}

		System.out.println("Current score details:");
		System.out.println(score);

		System.out.print("Enter new score (leave blank to keep the current value): ");
		String newScoreStr = scanner.nextLine();

		if (!newScoreStr.isEmpty()) {
			int newScore = Integer.parseInt(newScoreStr);
			score.setScore(newScore);
		}

		scoreDAO.updateScore(score);
		System.out.println("Score updated successfully.");
	}

	private static void deletePlayer(Scanner scanner) throws SQLException {
		System.out.print("Enter the player ID to delete: ");
		int id = scanner.nextInt();

		Player player = null;
		String query = "SELECT * FROM Player WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					player = new Player();
					player.setId(resultSet.getInt("id"));
					player.setName(resultSet.getString("name"));
					player.setDomesticTeam(resultSet.getString("domesticTeam"));
					player.setAverage(resultSet.getInt("average"));
				}
			}
		}

		if (player == null) {
			System.out.println("Player not found!");
			return;
		}

		playerDAO.deletePlayer(player);
		System.out.println("Player deleted successfully.");
	}

	private static void deleteScore(Scanner scanner) throws SQLException {
		System.out.print("Enter the score ID to delete: ");
		int id = scanner.nextInt();

		Score score = null;
		String query = "SELECT * FROM Score WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					score = new Score();
					score.setId(resultSet.getInt("id"));
					score.setPlayerId(resultSet.getInt("playerId"));
					score.setScore(resultSet.getInt("score"));
				}
			}
		}

		if (score == null) {
			System.out.println("Score not found!");
			return;
		}

		scoreDAO.deleteScore(score);
		System.out.println("Score deleted successfully.");
	}
}
