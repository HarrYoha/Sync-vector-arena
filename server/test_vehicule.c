#include <stdio.h>
#include <stdlib.h>

#include "vehicule.h"

int main(){

	struct vehicule_t* vehicule = new_vehicule();
	
	if(vehicule == NULL){
		fprintf(stderr, "ERREUR");
		exit(EXIT_FAILURE);
	}
	
	printf("%f, %f\n", vehicule->x, vehicule->y);
	
	vehicule_thrust(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);
		
	vehicule_clock(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);

	vehicule_move(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);	
	
	vehicule_thrust(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);
		
	vehicule_clock(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);

	vehicule_move(vehicule);
	
	printf("%f, %f\n", vehicule->x, vehicule->y);	
	
	exit(EXIT_SUCCESS);
}
