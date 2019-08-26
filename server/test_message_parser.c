/**
 * @file test_message_parser.c
 * @author ZHOU
 * @date 05/03/2019
 * @brief Fichier de test et d'exemple pour message_parser
 */

#include <stdio.h>
#include <stdlib.h>

#include "message_parser.h"

int main(){
	char** parsed = NULL;
	
	const char* src = "TEST/message/encore/une ligne/\n";
	
	printf("%s", src);
	
	char* message = malloc(strlen(src)+1);
	
	if(message == NULL){
		perror("malloc");
		return -1;
	}

	strcpy(message, src);
	message[strlen(src)] = '\0';
	
	size_t nb_part = 0;
	
	if((nb_part = getMessage(message, &parsed)) != 0){
		for(int i = 0 ; i < nb_part ; ++i){
			printf("%s\n", parsed[i]);
		}
		
		for(int i = 0 ; i < nb_part ; ++i){
			free(parsed[i]);
		}
		
		free(parsed);
	}else{
		printf("error\n");
	}
	
	free(message);
	
	exit(EXIT_SUCCESS);
}
