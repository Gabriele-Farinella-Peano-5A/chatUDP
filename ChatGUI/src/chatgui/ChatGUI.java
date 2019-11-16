package chatgui;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author farinella.gabriele
 */
public class ChatGUI extends JFrame implements ActionListener{
    private static DatagramSocket socket;
    private static String IP_address = "127.0.0.1";
    private static InetAddress address;
    private static int UDP_port;
    private String username;
    
    JPanel Cont = new JPanel();
    
    JMenuBar menu = new JMenuBar();
    JMenuItem AddUsername = new JMenuItem("Aggiungi Username");
    
    private static JTextArea chatArea = new JTextArea();
    JScrollPane scroll = new JScrollPane(chatArea);
    
    JTextField messaggioField = new JTextField("Inserisci qui il messaggio");
    JButton invia = new JButton("Invia");
    
    public ChatGUI()throws InterruptedException{
        
        this.setTitle("Chat UDP");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(2,1));
        
        menu.add(AddUsername);
        this.setJMenuBar(menu);
        
        Cont.setLayout(new GridLayout(2,2));
        Cont.setBorder(new EmptyBorder(45, 45, 45, 45));
        messaggioField.setBorder(new EmptyBorder(10, 10, 10, 10));
        Cont.add(messaggioField);
        Cont.add(invia);
        chatArea.setEditable(false);
        chatArea.setBorder(new EmptyBorder(60, 60, 60, 60));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroll);
        this.add(Cont);
        
        messaggioField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                messaggioField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        
        invia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(username == null){
                    JOptionPane.showMessageDialog(null, "Non hai inserito l'username!");
                }else{
                    inviaPacchetto(messaggioField.getText(),username);
                    messaggioField.setText("");
                }
                
            }
        });
        
        AddUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               username = JOptionPane.showInputDialog("Inserisci il tuo nickname: ");
               if(username != null){
                  chatArea.append("Username " + "''" + username + "''"+ " inserito correttamente!");
                  chatArea.append("\n");
               }
            }
        });
        
        Thread ascolta = new Thread() {
	public void run() {
                  riceviPacchetto();
            }
	};	
        ascolta.start();
    }
    
    public static void inviaPacchetto(String messaggio, String username){
        byte[] buffer;
        DatagramPacket userDatagram;

        try {
                messaggio = username + ": " + messaggio; 

                buffer = messaggio.getBytes("UTF-8");

                userDatagram = new DatagramPacket(buffer, buffer.length, address, UDP_port);
                socket.send(userDatagram);
        } catch (IOException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void riceviPacchetto(){
        byte[] buffer = new byte[100];
        String received;
        DatagramPacket serverDatagram;

        try {
            serverDatagram = new DatagramPacket(buffer, buffer.length);
            while (!Thread.interrupted()){
                socket.receive(serverDatagram);
                received = new String(serverDatagram.getData(), 0, serverDatagram.getLength(), "ISO-8859-1");
                chatArea.append(received+"\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
            socket.close();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws UnknownHostException, SocketException{
        
        address = InetAddress.getByName(IP_address);
        UDP_port = 1077;
        
        socket = new DatagramSocket();
        
        Runnable r = new Runnable() {
             public void run() {
                 try { 
                     new ChatGUI().setVisible(true);
                 } catch (InterruptedException ex) {
                     Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         };
         EventQueue.invokeLater(r);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
    }
}