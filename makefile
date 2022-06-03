javacMac:
	javac -cp libraries/postgresql-42.3.1.jar:. Ap3.java

javaMac:
	java -cp libraries/postgresql-42.3.1.jar:. Ap3

javacWin:
	javac -cp /libraries/postgresql-42.3.1.jar;. Ap3.java

javaWin:
	java -cp /libraries/postgresql-42.3.1.jar;. Ap3

clean:
	rm -rf *.class
