all: HEMKit

HEMKit: main.o bestPathfinder.o graph.o subGraphCreator.o
	g++ main.cpp bestPathfinder.cpp graph.cpp subGraphCreator.cpp -o HEMKit; mv HEMKit ../bin

main.o: main.cpp
	g++ -c main.cpp 

bestPathfinder.o: bestPathfinder.cpp
	g++ -c bestPathfinder.cpp

graph.o: graph.cpp
	g++ -c graph.cpp

subGraphCreator.o: subGraphCreator.cpp
	g++ -c subGraphCreator.cpp
clean:
	rm -rf *o ../bin/HEMKit