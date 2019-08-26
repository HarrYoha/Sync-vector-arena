/**
 * @file message_parser.c
 * @author ZHOU
 * @date 05/03/2019
 * @brief Implementation du message_parser
 */
 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "message_parser.h"


const char* message_delimiter = "/";

size_t getMessage(char* inMessage, char*** retour){
	
	char* token;
	*retour = calloc(1, 0);
	
	int nb_part = 0;
	
	token = strtok(inMessage, message_delimiter);
	
	
	while(token != NULL){
		if(strcmp(token, "\n") == 0){
			break;
		}
		
		if((*retour = realloc(*retour, ++nb_part * sizeof(char*))) == NULL){
			perror("realloc : message_parser#getMessage");
			return -1;
		}
		
		(*retour)[nb_part-1] = malloc(strlen(token) + 1);
		strcpy((*retour)[nb_part-1], token);
		
		token = strtok(NULL, message_delimiter);
	}
	
	
	return nb_part;
}
