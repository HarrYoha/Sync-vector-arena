
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

#include "coords.h"
#include "scores.h"
#include "vehicule.h"
#include "obstacle.h"
#include "joueur.h"

struct joueur_t* newJoueur(char* nom, int sock) {
	struct joueur_t* retour = malloc(sizeof(struct joueur_t));
	retour->nom = malloc(strlen(nom) + 1);
	strcpy(retour->nom, nom);
	retour->vehicule = newVehicule();
	retour->points = 0;
	retour->socket = sock;
	
	pthread_mutexattr_init(&retour->Attr);
	pthread_mutexattr_settype(&retour->Attr, PTHREAD_MUTEX_RECURSIVE);
	pthread_mutex_init(&retour->mutex, &retour->Attr);
	
	printf("Nouveau joueur %s\n", retour->nom);

	return retour;
}

int receiveNewcom(struct joueur_t* player, char* comm){
	char* angle = strtok(&comm[1], "T");
	char* thrust = strtok(NULL, "T");
	
	double angleValue = 0.0f;
	unsigned nb_thrust = 0;
	
	sscanf(angle, "%lf", &angleValue);
	sscanf(thrust, "%u", &nb_thrust);
	
	//printf("%lf %u\n", angleValue, nb_thrust);
//	printf("rec newcom LOCK\n");	
	pthread_mutex_lock(&player->vehicule->mutex);
	

	player->vehicule->angle += angleValue;
	for(unsigned i = 0 ; i < nb_thrust ; ++i){
		vehicule_thrust(player->vehicule);
	}
	
	pthread_mutex_unlock(&player->vehicule->mutex);
	//printf("rec newcom UNLOCK\n");
	
	
	return 0;
	
}

/**
 * Envoie un message au joueur
 */
static int sendMessage(struct joueur_t* j, char* message, size_t length) {
	
	// fprintf(stderr, "SEND : %s : %s\n", j->nom, message);
	
	write(j->socket, message, length);

	return 0;
}

int sendWelcomeMessage(struct joueur_t* j, char* phase,
		struct joueur_t** others, size_t nb_others, double x, double y, struct obstacle_t** obstacles, size_t nb_obs) {

	char* WELCOME = "WELCOME/";
	char* messagePoints = scoresToString(others, nb_others);
	char* messageCoord = coordToString(x, y);
	char* messageObstacles = obstaclesToString(obstacles, nb_obs);

	/*
	 * WELCOME/phase/points/coord/\n\0
	 */
	size_t msgSize = strlen(WELCOME) + 1 + strlen(phase) + 1
			+ strlen(messagePoints) + 1 + strlen(messageCoord) + 1 + strlen(messageObstacles) + 1 + 1 + 1;

	char* message = calloc(msgSize, 1);

	strcat(message, WELCOME);
	strcat(message, phase);
	strcat(message, "/");
	strcat(message, messagePoints);
	strcat(message, "/");
	strcat(message, messageCoord);
	strcat(message, "/");
	strcat(message, messageObstacles);
	strcat(message, "/\n");

	sendMessage(j, message, strlen(message));
	
	printf("%s\n", message);
	
	free(message);
	
	
	
	return 0;
}

int sendDeniedMessage(int sock) {
	char* msg = "DENIED/";
	write(sock, msg, strlen(msg));
	return 0;
}

int sendNewPlayerMessage(struct joueur_t* j, char* newPlayerName) {
	char* msg_ = "NEWPLAYER/";
	char* msg = calloc(strlen(msg_) + strlen(newPlayerName) + 3, 1);
	strcat(msg, msg_);
	strcat(msg, newPlayerName);
	strcat(msg, "/\n");
sendMessage(j, msg, strlen(msg));

	free(msg);
	
	return 0;
}

int sendPlayerLeftMessage(struct joueur_t* j, char* leftPlayerName) {
	char* msg_ = "PLAYERLEFT/";
	char* msg = calloc(strlen(msg_) + strlen(leftPlayerName) + 3, 1);
	strcat(msg, msg_);
	strcat(msg, leftPlayerName);
	strcat(msg, "/\n");
sendMessage(j, msg, strlen(msg));

	free(msg);
	
	return 0;
}

int sendTick(struct joueur_t* j, struct joueur_t** players, size_t nb_players) {
	char* msg_ = "TICK/";
	char* messageCrds = vcoordsToString(players, nb_players);

	char* msg = calloc(strlen(msg_) + strlen(messageCrds) + 3, 1);
	strcat(msg, msg_);
	strcat(msg, messageCrds);
	strcat(msg, "/\n");
	
	//printf("SENDING %s\n", msg);

	sendMessage(j, msg, strlen(msg));
	
	free(msg);
	
	return 0;
	
}

int sendNewObj(struct joueur_t* j, double x, double y, struct joueur_t** players, size_t nb_players) {
	char* msg_ = "NEWOBJ/";
	char* messageCrd = coordToString(x, y);
	char* messagePoints = scoresToString(players, nb_players);

	size_t msgSize = strlen(msg_) + 1 + strlen(messageCrd) + 1
			+ strlen(messagePoints) + 3;

	char* msg = calloc(msgSize, 1);
	strcat(msg, msg_);
	strcat(msg, messageCrd);
	strcat(msg, "/");
	strcat(msg, messagePoints);
	strcat(msg, "/\n");

	sendMessage(j, msg, strlen(msg));

	free(msg);
	
	return 0;
}

int sendSessionMessage(struct joueur_t* j, struct joueur_t** players, size_t nb_players, double x, double y, struct obstacle_t** obstacle, size_t nb_obs) {
	char* msg_ = "SESSION/";
	char* messageCrd = coordToString(x, y);
	char* messageCrds = coordsToString(players, nb_players);
	char* messageObs = obstaclesToString(obstacle, nb_obs);

	size_t msgSize = strlen(msg_) + strlen(messageCrds) + 1 + strlen(messageCrd) + 1 + strlen(messageObs) + 3;

	char* msg = calloc(msgSize, 1);
	strcat(msg, msg_);
	strcat(msg, messageCrds);
	strcat(msg, "/");
	strcat(msg, messageCrd);
	strcat(msg, "/");
	strcat(msg, messageObs);
	strcat(msg, "/\n");

	sendMessage(j, msg, strlen(msg));
	
	printf("%s\n", msg);
	free(msg);
	
	return 0;
}

int sendWinnerMessage(struct joueur_t* j, struct joueur_t** players, size_t nb_players) {
	char* msg_ = "WINNER/";
	char* messagePoints = scoresToString(players, nb_players);

	char* msg = calloc(strlen(msg_) + strlen(messagePoints) + 3, 1);
	strcat(msg, msg_);
	strcat(msg, messagePoints);
	strcat(msg, "/\n");
	
	sendMessage(j, msg, strlen(msg));

	free(msg);
	
	return 0;
}
