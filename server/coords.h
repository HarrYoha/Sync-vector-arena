#ifndef COORDS_H_
#define COORDS_H_

#include "joueur.h"
#include <stddef.h>

char* coordToString(double x, double y);

char* coordsToString(struct joueur_t** players, size_t nb_player);

char* vcoordsToString(struct joueur_t** players, size_t nb_player);

#endif /* COORDS_H_ */
