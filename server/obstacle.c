#include "obstacle.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "coords.h"

struct obstacle_t* newObstacle(){
	struct obstacle_t* retour = malloc(sizeof(struct obstacle_t));

	if(retour == NULL){
		perror("ERREUR malloc newObstacle");
		return NULL;
	}
	
	retour->x = (((double)rand()/(double)(RAND_MAX)) * 2)-1;
	retour->y = (((double)rand()/(double)(RAND_MAX)) * 2)-1;
	
	retour->radius = 0.1;
	
	return retour;
	
}

char* obstaclesToString(struct obstacle_t** obstacles, size_t nb){
	char** obsString = malloc(nb * sizeof(char*));

	
	for (size_t i = 0; i < nb; ++i) {
		obsString[i] = coordToString(obstacles[i]->x, obstacles[i]->y);
	}

	size_t totalSize = 0;
	for (size_t i = 0; i < nb; ++i) {
		totalSize += strlen(obsString[i]);
		totalSize += 1;
	}

	char* retour = calloc(totalSize, 1);

	for (size_t i = 0; i < nb; ++i) {
		strcat(retour, obsString[i]);
		if (i != nb - 1) {
			strcat(retour, "|");
		}
	}


	for (size_t i = 0; i < nb; ++i) {
		free(obsString[i]);
	}
	free(obsString);

	return retour;
	
}