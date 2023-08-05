all:
	javac src/builder/*.java src/parser/*.java src/*.java -d out

run:
	java -Xmx256m -cp ./out Main