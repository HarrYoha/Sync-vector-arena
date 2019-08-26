#ifndef SERVER_SCORES_H_
#define SERVER_SCORES_H_

#include <stddef.h>
#include "joueur.h"

char* scoresToString(struct joueur_t** joueurs, size_t nb_joueur);

#endif /* SERVER_SCORES_H_ */
