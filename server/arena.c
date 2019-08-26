#include "arena.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <pthread.h>
#include <math.h>

#include "joueur.h"
#include "vehicule.h"
#include "obstacle.h"

struct arena_t* arene;


struct arena_t** getArena(){
	return &arene;
}

struct arena_t* newArena(double width, double heigh, unsigned int goal,
		unsigned serverTick) {

	srand((unsigned) time(NULL));
	struct arena_t* retour = malloc(sizeof(struct arena_t));

	if (retour == NULL) {
		perror("ERREUR : malloc arena#newArena");
		return NULL;
	}

	retour->joueurs = malloc(0);
	if (retour->joueurs == NULL) {
		perror("ERREUR : malloc arena#newArena 2");
		return NULL;
	}
	retour->nb_joueur = 0;

	retour->width = width;
	retour->height = heigh;

	retour->point_goal = goal;
	retour->phase = "jeu";
	
	retour->nb_obstacles = (rand()%4)+1;
	retour->obstacles = malloc(sizeof(struct obstacle_t*)*retour->nb_obstacles);
	
	if(retour->obstacles == NULL){
		perror("ERREUR malloc newArena obstacles");
		return NULL;
	}
	
	for(size_t i = 0 ; i < retour->nb_obstacles ; ++i){
		retour->obstacles[i] = newObstacle();
		if(retour->obstacles[i] == NULL){
			perror("ERREUR malloc arena#newArena obstacle");
			return NULL;
		}
	}
	
	retour->serverTickSpeed = serverTick;
	
	pthread_mutexattr_init(&retour->Attr);
	pthread_mutexattr_settype(&retour->Attr, PTHREAD_MUTEX_RECURSIVE);
	pthread_mutex_init(&retour->mutex, &retour->Attr);
	
	arenaNewObjectif(retour);

	
	return retour;

}

void* startArena(void* args){
	
	struct arena_t* arena = (struct arena_t*) args;
	
//	struct timespec timer, t;
//	timer.tv_sec = 0;
//	//timer.tv_nsec = (long)((1.0f/(double)arena->serverTickSpeed)*1000000000);	
//	timer.tv_nsec = 1000000L;	
	
	long t = 0 ; 
	
	while(1){
		arenaTick(arena);
		++t;
		usleep((1/((double)arena->serverTickSpeed))*1000000);
		//nanosleep(&timer, &t);
	}
	
	return NULL;
}

int arenaTakeObjectif(struct arena_t* arena, struct joueur_t* player) {
	
//	printf("take LOCK\n");
	pthread_mutex_lock(&arena->mutex);
	
	player->points += arena->obj_value;
	arenaNewObjectif(arena);
	pthread_mutex_unlock(&arena->mutex);
//	printf("UNLOCK\n");
	return 0;
}

int arenaNewObjectif(struct arena_t* arena) {
//	printf("new LOCK\n");
	pthread_mutex_lock(&arena->mutex);

	arena->obj_value = rand() % 5;
	arena->obj_x = (((double)rand()/(double)(RAND_MAX)) * 2)-1;
	arena->obj_y = (((double)rand()/(double)(RAND_MAX)) * 2)-1;
	printf("%lf %lf\n", arena->obj_x, arena->obj_y);
	
	arena->obj_radius = 0.025;
	
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		sendNewObj(arena->joueurs[i], arena->obj_x, arena->obj_y,
				arena->joueurs, arena->nb_joueur);
	}
	
	pthread_mutex_unlock(&arena->mutex);
//	printf("UNLOCK\n");

	return 0;
}

int arenaAddPlayer(struct arena_t* arena, struct joueur_t* joueur) {
	//printf("add LOCK\n");
	pthread_mutex_lock(&arena->mutex);
	
	struct joueur_t** tmp = realloc(arena->joueurs,
			(arena->nb_joueur + 1) * sizeof(struct joueur_t*));
	if (tmp == NULL) {
		perror("ERREUR : realloc arena#arenaAddPlayer");
		return -1;
	}

	arena->joueurs = tmp;
	arena->joueurs[arena->nb_joueur++] = joueur;

	sendWelcomeMessage(joueur, arena->phase, arena->joueurs, arena->nb_joueur, arena->obj_x, arena->obj_y, arena->obstacles, arena->nb_obstacles);
	
	sendSessionMessage(joueur, arena->joueurs, arena->nb_joueur, arena->obj_x, arena->obj_y, arena->obstacles, arena->nb_obstacles);
	
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		sendNewPlayerMessage(arena->joueurs[i], joueur->nom);
	}
	
	pthread_mutex_unlock(&arena->mutex);	
//printf("UNLOCK\n");
	return 0;
}

int arenaRemovePlayer(struct arena_t* arena, struct joueur_t* joueur) {
	//printf("rem LOCK\n");
	pthread_mutex_lock(&arena->mutex);
	struct joueur_t** tmp = realloc(arena->joueurs,
			(arena->nb_joueur - 1) * sizeof(struct joueur_t*));
	if (tmp == NULL) {
		perror("ERREUR : realloc arena#arenaRemovePlayer");
		//return -1;
	}

	arena->joueurs = tmp;
	arena->nb_joueur--;
	
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		if (strcmp(joueur->nom, arena->joueurs[i]->nom) != 0) {
			sendPlayerLeftMessage(arena->joueurs[i], joueur->nom);
		}
	}
	pthread_mutex_unlock(&arena->mutex);
//printf("UNLOCK\n");
	return 0;
}

static double distance(double x1, double y1, double x2, double y2){
	return sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2));
}

int arenaTick(struct arena_t* arena) {
	//printf("tick LOCK\n");
	pthread_mutex_lock(&arena->mutex);
	//Deplacement des véhicules
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		vehicule_move(arena->joueurs[i]->vehicule);
	}
	
	//Collision avec obstacle
	for(size_t i = 0; i < arena->nb_joueur; ++i){
		for(size_t j = 0; j < arena->nb_obstacles; ++j){
			double dist = distance(arena->obstacles[j]->x, arena->obstacles[j]->y, arena->joueurs[i]->vehicule->x, arena->joueurs[i]->vehicule->y);
			
			
			if(dist < arena->obstacles[j]->radius + arena->joueurs[i]->vehicule->radius){
				printf("collision\n");
				arena->joueurs[i]->vehicule->dx *=-1;
				arena->joueurs[i]->vehicule->dy *=-1;
			}
		}
	}
	
	//Collision avec joueur
	for(size_t i = 0; i < arena->nb_joueur; ++i){
		for(size_t j = 0; j < arena->nb_joueur; ++j){			
			if(i!=j){
				
				printf("passe");
				double dist = distance(arena->joueurs[i]->vehicule->x, arena->joueurs[i]->vehicule->y, arena->joueurs[j]->vehicule->x, arena->joueurs[j]->vehicule->y);
				printf("OK\n");
				
				if(dist < arena->joueurs[j]->vehicule->radius + arena->joueurs[i]->vehicule->radius){
					printf("collision vehicule\n");
					arena->joueurs[i]->vehicule->dx *=-1;
					arena->joueurs[i]->vehicule->dy *=-1;
				}
			}
		}
	}
	
	// Donner un point au joueur dans l'objectis
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		double dist = distance(arena->obj_x, arena->obj_y, arena->joueurs[i]->vehicule->x, arena->joueurs[i]->vehicule->y);
		
		//printf("%lf, %lf, %lf, %lf\n", arena->obj_x, arena->obj_y, arena->joueurs[i]->vehicule->x, arena->joueurs[i]->vehicule->y);
		
		//printf("%lf %lf\n", dist, arena->obj_radius + arena->joueurs[i]->vehicule->radius);
		if(dist < arena->obj_radius + arena->joueurs[i]->vehicule->radius){
			fprintf(stderr, "Joueur [%s] a marqué %u points\n", arena->joueurs[i]->nom, arena->obj_value);
			arenaTakeObjectif(arena, arena->joueurs[i]);
		}
	}
	
	//printf("tick");
	
	//Envoie des messages de tick
	for (size_t i = 0; i < arena->nb_joueur; ++i) {
		sendTick(arena->joueurs[i], arena->joueurs, arena->nb_joueur);
	}
	
	pthread_mutex_unlock(&arena->mutex);
	//printf("UNLOCK\n");
	return 0;

}
