/**
 * @file vehicule.h
 * @author ZHOU
 * @date 05/03/2019
 * @brief Fichier d'entete pour vehicule
 */
 
#ifndef _VEHICULE_H_
#define _VEHICULE_H_

#include <pthread.h>

#define PI 3.14159265358979323846

/**
 * @brief La structure vehicule pour gerer les informations sur un vehicule de l'arene
 *
 * @see Arene
 */
struct vehicule_t{
	double x; /**< la position x sur l'arene */
	double y;/**< la position y sur l'arene */
	
	double thrustit; /**< la puissance de propulsion */
	double turnit; /**< la vitesse de rotation */
	
	double dx; /**< la vitesse de deplacement sur l'axe x de l'arene */
	double dy; /**< la vitesse de deplacement sur l'axe y de l'arene */
	
	double angle; /**< l'angle dans lequel le vehicule regarde */
	
	double radius;
	
	pthread_mutex_t mutex;
	pthread_mutexattr_t Attr;

};

/**
 * @brief Cree une nouvelle structure vehicule
 * 
 * @return La nouvelle instane de la structure vehicule en cas de reussite, NULL sinon
 */
struct vehicule_t* newVehicule();

/**
 * @brief Libere l'espace memoire utilise par un vehicule
 * 
 * @return 0 si la liberation est reussi, -1 sinon
 */
int free_vehicule(struct vehicule_t* vehicule);

/**
 * @brief Fait avancer le vehicule selon sont dx et dy
 *
 * @param vehicule le vehicule a deplacer
 *
 * @return 0 si le deplacement c'est bien effectue, -1 sinon
 */
int vehicule_move(struct vehicule_t* vehicule);


/**
 * @brief Tourne le vehicule dans le sens horaire
 * 
 * @param vehicule le vehicule a tourner
 *
 * @return 0 si la rotation c'est bien effectuee, -1 sinon
 */
int vehicule_clock(struct vehicule_t* vehicule);

/**
 * @brief Tourne le vehicule dans le sens anti-horaire
 * 
 * @param vehicule le vehicule a tourner
 *
 * @return 0 si la rotation c'est bien effectuee, -1 sinon
 */
int vehicule_anticlock(struct vehicule_t* vehicule);

/**
 * @brief Applique une force de poussee sur le vehicule pour le faire acceleter dans la direction ou il regarde
 * 
 * @param le vehicule a propulser
 *
 * @return 0 si la propulsion c'est bien passee, -1 sinon
 */
int vehicule_thrust(struct vehicule_t* vehicule);

#endif
