task=TaskD

all:
	javac -cp ./src/ -d ./out/ ./src/tasks/$(task).java

run:
	java -Xmx256m -cp ./out tasks/$(task)

test: all run

zip: all
	del "$(task).zip"
	7z a -tzip $(task).zip Makefile src out