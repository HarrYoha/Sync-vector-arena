#ifndef SERVER_CLIENTTREATMENT_H_
#define SERVER_CLIENTTREATMENT_H_

/**
 * Attend et lit un message provenant du joueur
 */
char* receiveMessage(struct joueur_t* j);

void* sendTickServer(void* args);

void* handle_connection(void* args);

#endif /* SERVER_CLIENTTREATMENT_H_ */
