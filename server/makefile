CC = gcc
FLAGS = -g -Wall
LIBS = -lm -lpthread

MAINS = server.c
SOURCES = message_parser.c vehicule.c joueur.c scores.c coords.c arena.c clientTreatment.c obstacle.c

EXECUTABLES = $(MAINS:%.c=%.exe)
OBJS = $(SOURCES:%.c=%.o)

all: $(EXECUTABLES)

server.exe: server.c $(OBJS)
	$(CC) $(FLAGS) $^ -o $@ $(LIBS)

test: test_message_parser.exe test_vehicule.exe

test_message_parser.exe: test_message_parser.c message_parser.o
	$(CC) $(FLAGS) $^ -o $@ $(LIBS)
	
test_vehicule.exe: test_vehicule.c vehicule.o
	$(CC) $(FLAGS) $^ -o $@ $(LIBS)

%.o: %.c
	$(CC) $(FLAGS) $< -c $(LIBS)

clean: 
	rm -f *.o
	rm -f *.exe
