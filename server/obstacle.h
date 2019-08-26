#ifndef _OBSTACLE_H_
#define _OBSTACLE_H_

#include <stddef.h>

struct obstacle_t{
	double x; 
	double y;

	double radius;
};

struct obstacle_t* newObstacle();

char* obstaclesToString(struct obstacle_t**, size_t nb);
	
#endif