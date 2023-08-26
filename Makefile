all:
	javac -cp ./src/ -d ./out/ ./src/Main.java

run:
	java -Xmx256m -cp ./out Main