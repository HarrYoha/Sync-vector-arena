/**
 * @file vehicule.h
 * @author ZHOU
 * @date 05/03/2019
 * @brief Inplementation pour vehicule
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>
 
#include "vehicule.h"
#include "arena.h"

#define MAX_SPEED 0.05

struct vehicule_t* newVehicule(){
	struct vehicule_t* vehicule = malloc(sizeof(struct vehicule_t));
	
	if(vehicule == NULL){
		perror("MALLOC ERROR: vehicule#new_vehicule");
		return NULL;
	}
	
	vehicule->x = (((double)rand()/(double)(RAND_MAX)) * 2)-1; /**< la position x sur l'arene */
	vehicule->y = (((double)rand()/(double)(RAND_MAX)) * 2)-1;/**< la position y sur l'arene */
	
	vehicule->thrustit = 0.005; /**< la puissance de propulsion */
	vehicule->turnit = 6 * (PI/180); /**< la vitesse de rotation */
	
	vehicule->dx = 0; /**< la vitesse de deplacement sur l'axe x de l'arene */
	vehicule->dy = 0; /**< la vitesse de deplacement sur l'axe y de l'arene */
	
	vehicule->radius = 0.1;
	
	vehicule->angle = 0;
	pthread_mutexattr_init(&vehicule->Attr);
pthread_mutexattr_settype(&vehicule->Attr, PTHREAD_MUTEX_RECURSIVE);
	pthread_mutex_init(&vehicule->mutex, &vehicule->Attr);
	
	return vehicule;
}


int free_vehicule(struct vehicule_t* vehicule){
	free(vehicule);
	
	return 0;
}


int vehicule_move(struct vehicule_t* vehicule){
	
	//printf("move LOCK\n");
	pthread_mutex_lock(&vehicule->mutex);
	
	vehicule->x += vehicule->dx;
	vehicule->y += vehicule->dy;
	
	struct arena_t** arene = getArena();
	
	//printf("getwidth\n");
	if(vehicule->x < -(*arene)->width){
		vehicule->x = (*arene)->width;
	}
		
	if(vehicule->y < -(*arene)->height){
		vehicule->y = (*arene)->height;
	}
	if(vehicule->x > (*arene)->width){
		vehicule->x = -(*arene)->width;
	}
	if(vehicule->y > (*arene)->height){
		vehicule->y = -(*arene)->height;
	}
	//printf("getwidth OK\n");
	
	
	pthread_mutex_unlock(&vehicule->mutex);
	//printf("move UNLOCK\n");
	return 0;
}

int vehicule_clock(struct vehicule_t* vehicule){
	
	pthread_mutex_lock(&vehicule->mutex);
	
	vehicule->angle -= vehicule->turnit;
	
	if(vehicule->angle < 0){
		vehicule->angle += 2*PI;
	}
	pthread_mutex_unlock(&vehicule->mutex);
	
	return 0;
}

int vehicule_anticlock(struct vehicule_t* vehicule){
	
	pthread_mutex_lock(&vehicule->mutex);
	
	vehicule->angle += vehicule->turnit;
	
	if(vehicule->angle > 2*PI){
		vehicule->angle -= 2*PI;
	}
	pthread_mutex_unlock(&vehicule->mutex);
	return 0;
}

int vehicule_thrust(struct vehicule_t* vehicule){
	
	pthread_mutex_lock(&vehicule->mutex);
	
	vehicule->dx += cos(vehicule->angle) * vehicule->thrustit;
	vehicule->dy += sin(vehicule->angle) * vehicule->thrustit;
	
	if(vehicule->dx > MAX_SPEED){
		vehicule->dx = MAX_SPEED;
	}
	
	if(vehicule->dx < -MAX_SPEED){
		vehicule->dx = -MAX_SPEED;
	}

	if(vehicule->dy > MAX_SPEED){
		vehicule->dy = MAX_SPEED;
	}
	
	if(vehicule->dy < -MAX_SPEED){
		vehicule->dy = -MAX_SPEED;
	}
	
	
	pthread_mutex_unlock(&vehicule->mutex);
	
	return 0;
}
