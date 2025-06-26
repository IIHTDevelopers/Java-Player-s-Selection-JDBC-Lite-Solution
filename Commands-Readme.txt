* To build your project use command:

	mvn clean package -Dmaven.test.skip

* Default credentials for MySQL:

	Username: root
	Password: pass@word1

* To login to mysql instance: Open new terminal and use following command:

      a.	sudo systemctl enable mysql
      b.	mysql -u root -p
The last command will ask for password which is ‘pass@word1’

*To run your project use command: 

	mvn clean install exec:java -Dexec.mainClass="com.playersselectionapplication.PlayersSelectionApplication"
