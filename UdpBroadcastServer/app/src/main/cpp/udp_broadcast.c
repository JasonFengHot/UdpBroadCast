//
// Created by huibin on 03/08/2017.
//

#include "udp_broadcast.h"
#include "ismartv_log.h"

#include <stdio.h>
#include <sys/socket.h>  /* for socket(), connect(), sendto(), and recvfrom() */
#include <netinet/in.h>  /* for sockaddr_in and inet_addr() */
#include <string.h>      /* for memset() */
#include <unistd.h>      /* for close() */

#define MAXBUF 65536

void send_udp_broadcast() {
    int sock, status, buflen;
    unsigned sinlen;
    char buffer[MAXBUF];
    struct sockaddr_in sock_in;
    int yes = 1;

    sinlen = sizeof(struct sockaddr_in);
    memset(&sock_in, 0, sinlen);

    sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

    sock_in.sin_addr.s_addr = htonl(INADDR_ANY);
    sock_in.sin_port = htons(10111);
    sock_in.sin_family = PF_INET;

    status = bind(sock, (struct sockaddr *) &sock_in, sinlen);
    LOGD("Bind Status = %d\n", status);

    status = getsockname(sock, (struct sockaddr *) &sock_in, &sinlen);
    LOGD("Sock port %d\n", htons(sock_in.sin_port));

    buflen = MAXBUF;
    memset(buffer, 0, buflen);
    status = recvfrom(sock, buffer, buflen, 0, (struct sockaddr *) &sock_in, &sinlen);
    LOGD("Receive Status = %d, Message = %s", status, buffer);
    shutdown(sock, 2);
    close(sock);
}