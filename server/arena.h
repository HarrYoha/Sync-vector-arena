#ifndef SERVER_ARENA_H_
#define SERVER_ARENA_H_

#include <stddef.h>
#include <pthread.h>

struct arena_t {
	double width;
	double height;

	char* phase;

	struct joueur_t** joueurs;
	size_t nb_joueur;
	
	struct obstacle_t** obstacles;
	size_t nb_obstacles;
	
	

	double obj_x;
	double obj_y;
	double obj_radius;
	unsigned obj_value;

	unsigned point_goal;

	unsigned serverTickSpeed;
	
	pthread_mutex_t mutex;
	pthread_mutexattr_t Attr;

};

/**
 * Démarre la boucle d'execution d'une arene
 // */
void* startArena(void* args);

struct arena_t** getArena();

struct arena_t* newArena(double width, double heigh, unsigned int goal,
		unsigned serverTick);

int arenaTakeObjectif(struct arena_t*, struct joueur_t*);

int arenaNewObjectif(struct arena_t*);

int arenaAddPlayer(struct arena_t*, struct joueur_t*);

int arenaRemovePlayer(struct arena_t*, struct joueur_t*);

int arenaTick(struct arena_t*);

#endif /* SERVER_ARENA_H_ */
