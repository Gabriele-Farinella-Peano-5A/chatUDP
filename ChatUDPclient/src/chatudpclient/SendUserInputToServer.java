/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatudpclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author farinella.gabriele
 */
public class SendUserInputToServer implements Runnable {
    DatagramSocket socket;
    InetAddress address;
    int UDP_port;
    
    SendUserInputToServer(DatagramSocket socket, InetAddress address, int UDP_port) {
        this.socket = socket;
        this.address = address;
        this.UDP_port = UDP_port;
    }
    /**
     *
     */
    @Override
    public void run() {

        byte[] buffer;
        String messaggio;
        String username; //dichiaro una variabile di tipo stringa in cui salvare l'username dell'utente
        Scanner tastiera = new Scanner(System.in);
        DatagramPacket userDatagram;

        try {
            System.out.print("Inserisci il tuo nickname: ");//chiedo l'inserimento del nickname
            username = tastiera.nextLine();//inserisco nella variabile "username" quello che ha inserito l' utente
            System.out.println("Il tuo nickname e': " + username);//messaggio che informa l'utente che ha inserito l'username nel modo corretto
            System.out.print("> ");
            do {
                //Leggo da tastiera il messaggio utente vuole inviare
                messaggio = tastiera.nextLine();
                //concateno l'username con il messaggio(questa fase si ripete per ogni messaggio)
                messaggio = username.concat(": " + messaggio); 

                //Trasformo in array di byte la stringa che voglio inviare
                buffer = messaggio.getBytes("UTF-8");

                // Costruisco il datagram (pacchetto UDP) di richiesta 
                // specificando indirizzo e porta del server a cui mi voglio collegare
                // e il messaggio da inviare che a questo punto si trova nel buffer
                userDatagram = new DatagramPacket(buffer, buffer.length, address, UDP_port);
                // spedisco il datagram
                socket.send(userDatagram);
            } while (messaggio.compareTo("quit") != 0); //se utente digita quit il tread termina
        } catch (IOException ex) {
            Logger.getLogger(ChatUDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}