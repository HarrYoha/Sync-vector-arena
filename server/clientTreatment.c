/*
 * ClientTreatment.c
 *
 *  Created on: 9 avr. 2019
 *      Author: ubuntu
 */

#include <stdio.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <stdlib.h>

#include "message_parser.h"
#include "joueur.h"
#include "arena.h"


char* receiveMessage(int sock) {
	char* message = calloc(20, sizeof(char));
	size_t current = 0;
	size_t max = 19;
	char c = 0;

	//printf("waiting message...\n");
	
	do {
		if (current == max) {
			max *= 2;
			char* tmp = realloc(message, max*sizeof(char));
						
			if (tmp == NULL) {
				perror("ERREUR : realloc clientTreatment#receiveMessage");
				return NULL;
			}
			
			message = tmp;

			for(size_t i = current+1 ; i < max ; ++i){
				message[i] = 0;
			}
		}

		if(read(sock, &c, 1) == -1){
			fprintf(stderr, "ERREUR fin de lecture");
			return NULL;
		}
		

		message[current++] = c;
	} while (c != '\n');
	
	//printf("#%s#", message);

	return message;
}

void* sendTickServer(void* args){

	struct arena_t** arene = getArena();
	
	for(size_t i = 0 ; i < (*arene)->nb_joueur ; ++i){
		sendTick((*arene)->joueurs[i], (*arene)->joueurs, (*arene)->nb_joueur);
	}

	return NULL;
}

void* handle_connection(void* args) {
	
	printf("Handle_connection\n");
	
	int sockfd = *((int*) args);

	char* message = receiveMessage(sockfd);
	char** parsed = NULL;
	size_t nb_part = getMessage(message, &parsed);
	
	if(nb_part == 0){
		fprintf(stderr, "ERREUR : clientTreatment handle_connection#getMessage");
		free(message);
		close(sockfd);
		
		return NULL;
	}
		
	if(strcmp(parsed[0], "CONNECT") != 0){
		fprintf(stderr, "Le premier message doit etre un message CONNECT");
		free(message);
		
		for(int i = 0 ; i < nb_part ; ++i){
			free(parsed[i]);
		}
		
		return NULL;
	}
	
	//printf("CONNECT received\n");
	
	struct joueur_t* player = newJoueur(parsed[1], sockfd);
	
	//TODO n'ajouter que si le nom n'est pas encore prit
	
	//printf("getting arena...\n");
	struct arena_t** arene = getArena();
	//printf("OK\n");
	
	//printf("Adding player to arena...\n");
	arenaAddPlayer(*arene, player);
	printf("OK\n");
	
	while(1){
		
		//printf("waiting for message\n");
		message = receiveMessage(sockfd);
		//printf("received\n");
		
		if(message == NULL){
			fprintf(stderr, "Client [%s] deconnecte\n", player->nom);
			arenaRemovePlayer(*arene, player);
			break;
		}
		
		//fprintf(stderr, "%s: %s\n", player->nom, message);
		
		nb_part = getMessage(message, &parsed);
		
		if(nb_part == 0){
			fprintf(stderr, "ERREUR : clientTreatment handle_connection#getMessage");
			break;
		}
		
		if(strcmp(parsed[0], "NEWCOM") == 0){
			receiveNewcom(player, parsed[1]);
		}else{ 
			if(strcmp(parsed[0], "EXIT") == 0){
				arenaRemovePlayer(*arene, player);
				break;
			}else{
				fprintf(stderr, "Commande %s inconnu par %s", message, player->nom);
			}
		}
	}
	
	close(sockfd);

	return NULL;
}

