#include "coords.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "joueur.h"
#include "vehicule.h"

char* coordToString(double x, double y) {
	char tmp1[50];
	char tmp2[50];

	sprintf(tmp1, "%.6f", x);
	sprintf(tmp2, "%.6f", y);

	char* retour = calloc(1 + strlen(tmp1) + 1 + strlen(tmp2) + 1, 1);

	strcat(retour, "X");
	strcat(retour, tmp1);
	strcat(retour, "Y");
	strcat(retour, tmp2);

	return retour;
}

char* directionToString(double dx, double dy) {
	char tmp1[50];
	char tmp2[50];

	sprintf(tmp1, "%.6f", dx);
	sprintf(tmp2, "%.6f", dy);

	char* retour = calloc(2 + strlen(tmp1) + 2 + strlen(tmp2) + 1, 1);

	strcat(retour, "VX");
	strcat(retour, tmp1);
	strcat(retour, "VY");
	strcat(retour, tmp2);

	return retour;
}

char* angleToString(double angle) {
	char tmp1[50];

	sprintf(tmp1, "%.6f", angle);

	char* retour = calloc(1 + strlen(tmp1) + 1, 1);

	strcat(retour, "T");
	strcat(retour, tmp1);

	return retour;
}

char* coordsToString(struct joueur_t** players, size_t nb_player) {

	char** coords = malloc(nb_player * sizeof(char*));

	for (size_t i = 0; i < nb_player; ++i) {
		pthread_mutex_lock(&players[i]->mutex);
	}
	
	for (size_t i = 0; i < nb_player; ++i) {
		coords[i] = coordToString(players[i]->vehicule->x,
				players[i]->vehicule->y);
	}

	size_t totalSize = 0;
	for (size_t i = 0; i < nb_player; ++i) {
		totalSize += strlen(players[i]->nom);
		totalSize += 1;
		totalSize += strlen(coords[i]);
		totalSize += 1;
	}

	char* retour = calloc(totalSize, 1);

	for (size_t i = 0; i < nb_player; ++i) {
		strcat(retour, players[i]->nom);
		strcat(retour, ":");
		strcat(retour, coords[i]);
		if (i != nb_player - 1) {
			strcat(retour, "|");
		}
	}
	
	for (size_t i = 0; i < nb_player; ++i) {
		pthread_mutex_unlock(&players[i]->mutex);
	}


	for (size_t i = 0; i < nb_player; ++i) {
		free(coords[i]);
	}
	free(coords);

	return retour;

}

char* vcoordsToString(struct joueur_t** players, size_t nb_player) {

	char** coords = malloc(nb_player * sizeof(char*));
	char** directions = malloc(nb_player * sizeof(char*));
	char** angle = malloc(nb_player * sizeof(char*));
	
	for (size_t i = 0; i < nb_player; ++i) {
		pthread_mutex_lock(&players[i]->mutex);
	}


	for (size_t i = 0; i < nb_player; ++i) {
		coords[i] = coordToString(players[i]->vehicule->x,
				players[i]->vehicule->y);
	}
	
	for (size_t i = 0; i < nb_player; ++i) {
		directions[i] = directionToString(players[i]->vehicule->dx,
				players[i]->vehicule->dy);
	}
	
	for (size_t i = 0; i < nb_player; ++i) {
		angle[i] = angleToString(players[i]->vehicule->angle);
	}

	size_t totalSize = 0;
	for (size_t i = 0; i < nb_player; ++i) {
		totalSize += strlen(players[i]->nom);
		totalSize += 1; // symbole ':'
		totalSize += strlen(coords[i]);
		totalSize += strlen(directions[i]);
		totalSize += strlen(angle[i]);
		totalSize += 1;
	}

	char* retour = calloc(totalSize, 1);

	for (size_t i = 0; i < nb_player; ++i) {
		strcat(retour, players[i]->nom);
		strcat(retour, ":");
		strcat(retour, coords[i]);
		strcat(retour, directions[i]);
		strcat(retour, angle[i]);
		if (i != nb_player - 1) {
			strcat(retour, "|");
		}
	}
	
	for (size_t i = 0; i < nb_player; ++i) {
		pthread_mutex_lock(&players[i]->mutex);
	}


	for (size_t i = 0; i < nb_player; ++i) {
		free(coords[i]);
	}
	
	free(coords);
	free(directions);
	free(angle);
	
	// printf("RETURNING %s\n", retour);

	return retour;

}
