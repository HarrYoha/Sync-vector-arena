#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <sys/socket.h>
#include <unistd.h>


#include "arena.h"
#include "clientTreatment.h"


#define PORT 8080



int main(int argc, char *argv[]) {
	struct sockaddr_in server_address;
	int connfd;
	socklen_t len;

	int fd_server = socket(AF_INET, SOCK_STREAM, 0);

	if (fd_server < 0) {
		perror("socket failed");
		exit(1);
	}
	bzero(&server_address, sizeof(server_address));

	// assign IP, PORT
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr = htonl(INADDR_ANY);
	server_address.sin_port = htons(PORT);

	// Binding newly created socket to given IP and verification
	if ((bind(fd_server, (struct sockaddr *) &server_address,
			sizeof(server_address))) != 0) {
		printf("socket bind failed...\n");
		exit(0);
	} else {
		printf("Socket successfully binded..\n");
	}

	// Now server is ready to listen and verification
	if ((listen(fd_server, 5)) != 0) {
		printf("Listen failed...\n");
		exit(0);
	} else {
		printf("Server listening..\n");
	}

	len = sizeof(struct sockaddr);
	
	struct arena_t** arene = getArena();

	*arene = newArena(1, 1, 20, 60);

	
	pthread_t thread_arena;
	if(pthread_create(&thread_arena, NULL, startArena, *arene) != 0){
		perror("ERREUR : pthread_create arena");
		close(fd_server);
		return -1;
	}

	
	while (1) {
		struct sockaddr client_address;
		// Accept the data packet from client_addressent and verification
		connfd = accept(fd_server, (struct sockaddr*) &client_address, &len);
		if (connfd < 0) {
			perror("server acccept failed...\n");
			continue;
		} else {
			printf("new client\n");
		}

		int* send = malloc(sizeof(int));
		*send = connfd;
		pthread_t th;
		if(pthread_create(&th, NULL, handle_connection, send) != 0){
			perror("ERREUR : pthread_create");
			close(connfd);
		}
	}

	close(fd_server);

}

