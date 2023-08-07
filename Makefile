all:
	javac src/builder/*.java src/generator/*.java src/parser/*.java src/resolver/*.java src/*.java -d out

run:
	java -Xmx256m -cp ./out Main