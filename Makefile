BUILD = build
SRC = src

all: default

default:
	@mkdir -p $(BUILD)
	javac -d $(BUILD) -sourcepath $(SRC) $(SRC)/BitNinja/*.java

run:
	cp BitNinja.cfg build/ && cp seed/* build/ && cd build && java BitNinja.BitNinja

run2:
	@mkdir -p build2/BitNinja && cp -r build/BitNinja/* build2/BitNinja/ && cp BitNinja2.cfg build2/BitNinja.cfg && cd build2 && java BitNinja.BitNinja

run3:
	@mkdir -p build3/BitNinja && cp -r build2/* build3 && cp BitNinja3.cfg build3/BitNinja.cfg && cd build3 && java BitNinja.BitNinja

run4:
	@mkdir -p build4/BitNinja && cp -r build2/* build4 && cp BitNinja4.cfg build4/BitNinja.cfg && cp build/music.mp3 build4 && cd build4 && java BitNinja.BitNinja

tests: clean default 
	java -ea -cp $(BUILD) BitNinja.LancerTests

clean: 
	@rm -rf build
	@rm -rf build
	@rm -rf build2
	@rm -rf build3
	@rm -rf build4
