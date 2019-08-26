
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

#include "scores.h"
#include "joueur.h"

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

char* scoresToString(struct joueur_t** joueurs, size_t nb_joueur) {
	
	pthread_mutex_lock(&mutex);
	
	size_t totalSize = 0;

	char tmp[50];

	
	for (size_t i = 0; i < nb_joueur; ++i) {
		totalSize += strlen(joueurs[i]->nom);
		totalSize += 1;
		sprintf(tmp, "%d", joueurs[i]->points);
		totalSize += strlen(tmp);
		totalSize += 1; // | ou \n
	}

	char* retour = calloc(totalSize, 1);


	for(size_t i = 0 ; i < nb_joueur ; ++i){
		strcat(retour, joueurs[i]->nom);
		strcat(retour, ":");
		sprintf(tmp, "%d", joueurs[i]->points);
		strcat(retour, tmp);
		if(i != nb_joueur-1){
			strcat(retour , "|");
		}
	}
	
	pthread_mutex_unlock(&mutex);

	return retour;
}
