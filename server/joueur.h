#ifndef _JOUEUR_H_
#define _JOUEUR_H_

#include <stddef.h>
#include <pthread.h>

#include "obstacle.h"

struct joueur_t {
	char* nom;
	struct vehicule_t* vehicule;
	unsigned points;
	int socket;
	
	pthread_mutex_t mutex;
	pthread_mutexattr_t Attr;
	
};

/**
 * Prepare un nouveau joueur
 */
struct joueur_t* newJoueur(char* nom, int sock);

/**
 * Traite un message de type NEWCOM
 */ 
int receiveNewcom(struct joueur_t* player, char* comm);

/**
 * Envoie un message de bienvenue au joueur
 */
int sendWelcomeMessage(struct joueur_t* j, char* phase, struct joueur_t** others, size_t nb_joueur, double x, double y, struct obstacle_t** obstacles, size_t nb_obs);

/**
 * Envoie un message Denied
 */
int sendDeniedMessage(int sock);

/**
 * Envoie un message de connexion d'un nouveau joueur
 */
int sendNewPlayerMessage(struct joueur_t* j, char* newplayername);

/**
 * Envoie un message de deconnexion d'un joueur
 */
int sendPlayerLeftMessage(struct joueur_t* j, char* leftPlayerName);

/**
 * Envoie un message de tick de seveur
 */
int sendTick(struct joueur_t* j, struct joueur_t**, size_t);

/**
 * Envoie un message newObj
 */
int sendNewObj(struct joueur_t* j, double x, double y, struct joueur_t**, size_t);

/**
 * Envoie un message de début de session
 */
int sendSessionMessage(struct joueur_t* j, struct joueur_t**, size_t, double x, double y, struct obstacle_t** obstacles, size_t nb_obs);

/**
 * Envoie un message de fin de session
 */
int sendWinnerMessage(struct joueur_t* j, struct joueur_t**, size_t);

#endif
