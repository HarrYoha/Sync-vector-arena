/**
 * @file message_parser.h
 * @author ZHOU
 * @date 05/03/2019
 * @brief Entete de message_parser
 * 
 * A utiliser pour parser les messages
 */

#ifndef _MESSAGE_PARSER_H_
#define _MESSAGE_PARSER_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * Parse un message ne un tableau contenant les elements du massage
 * 
 * @param inMessage le message d'entree
 * @param retour la ou mettre le message parse
 *
 * @return le nombre de parties du message si tout s'est bien passe, 0 sinon
 */
size_t getMessage(char* inMessage, char*** retour);

#endif