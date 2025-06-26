package com.playersselectionapplication.model;

import javax.persistence.*;

@Entity
@Table(name = "Player")
public class Player {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name", nullable = false, length = 10)
	private String name;

	@Column(name = "domesticTeam", nullable = false)
	private String domesticTeam;

	@Column(name = "average", nullable = false)
	private int average;

	public Player() {
	}

	public Player(int id, String name, String domesticTeam, int average) {
		this.id = id;
		this.name = name;
		this.domesticTeam = domesticTeam;
		this.average = average;
	}
	
	public Player(String name, String domesticTeam) {
		this.name = name;
		this.domesticTeam = domesticTeam;
		this.average = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomesticTeam() {
		return domesticTeam;
	}

	public void setDomesticTeam(String domesticTeam) {
		this.domesticTeam = domesticTeam;
	}

	public int getAverage() {
		return average;
	}

	public void setAverage(int average) {
		this.average = average;
	}

	@Override
	public String toString() {
		return "Player{" + "id=" + id + ", name='" + name + '\'' + ", domesticTeam='" + domesticTeam + '\''
				+ ", average=" + average + '}';
	}
}
