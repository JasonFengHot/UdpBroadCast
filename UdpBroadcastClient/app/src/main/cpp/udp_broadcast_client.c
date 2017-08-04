//
// Created by huibin on 03/08/2017.
//


#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>

#include "ismartv_log.h"

#define MAXBUF 65536

void udp_broadcast_client(const char *target, const char *message) {
    int sock, status, buflen, sinlen;
    char buffer[MAXBUF];
    struct sockaddr_in sock_in;
    int yes = 1;

    sinlen = sizeof(struct sockaddr_in);
    memset(&sock_in, 0, sinlen);
    buflen = MAXBUF;

    sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

    sock_in.sin_addr.s_addr = htonl(INADDR_ANY);
    sock_in.sin_port = htons(0);
    sock_in.sin_family = PF_INET;

    status = bind(sock, (struct sockaddr *) &sock_in, sinlen);
    LOGD("Bind Status = %d\n", status);

    status = setsockopt(sock, SOL_SOCKET, SO_BROADCAST, &yes, sizeof(int));
    LOGD("Setsockopt Status = %d\n", status);

    /* -1 = 255.255.255.255 this is a BROADCAST address,
       a local broadcast address could also be used.
       you can comput the local broadcat using NIC address and its NETMASK
    */

//    sock_in.sin_addr.s_addr = htonl(-1); /* send message to 255.255.255.255 */
    sock_in.sin_addr.s_addr = inet_addr(target);
    sock_in.sin_port = htons(10111); /* port number */
    sock_in.sin_family = PF_INET;

    sprintf(buffer,"%s", message);
    buflen = strlen(buffer);
    status = sendto(sock, buffer, buflen, 0, (struct sockaddr *) &sock_in, sinlen);
    LOGD("sendto Status = %d\n", status);

    shutdown(sock, 2);
    close(sock);
}
