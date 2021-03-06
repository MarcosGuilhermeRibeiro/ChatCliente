package com.marcos.ui;

import com.marcos.servico.ClienteServico;
import com.marcos.entidade.Mensagem;
import com.marcos.entidade.Mensagem.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcos
 */
public class ChatPainel extends javax.swing.JFrame {

    public ChatPainel() {
        initComponents();

        rbUDP.setSelected(true);
        btDesconectar.setEnabled(false);
        btEnviar.setEnabled(false);

    }

    String apelido = "";

    DatagramSocket clientSocket;
    InetAddress IPAddress; //Armazena endereco do Servidor
    int porta = 5555; //Porta de conexao do grupo multicast
    boolean conectou = false; //Registra se o usuario esta logado ou nao

    private void conectou() {
        rbTCP.setEnabled(false);
        rbUDP.setEnabled(false);
        btDesconectar.setEnabled(false);
        jtApelido.setEnabled(false);

        btConectar.setEnabled(false);

        btEnviar.setEnabled(true);
        btDesconectar.setEnabled(true);
    }

    Thread receberUDPThread; //Thread para receber mensagens via TCP
    Thread threadTimesTamp;

    private void conectarUDP() { //Metodo para conexao via UDP

        System.out.println("Iniciando Conexao com o grupo...");
        try {

            //IPAddress = InetAddress.getByName("239.255.255.255");
            IPAddress = InetAddress.getByName("127.0.0.1");
            clientSocket = new DatagramSocket();

            byte[] sendData = new byte[1024];
            String sentence;

            sentence = "status";
            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5556);
            receberUDPThread = new Thread(new UDPThread());
            receberUDPThread.start();

            threadTimesTamp = new Thread(new ThreadTimesTamp());
            threadTimesTamp.start();

            try {
                clientSocket.send(sendPacket);
                System.out.println("Mensagem Enviada");

            } catch (IOException ex) {
                Logger.getLogger(ChatPainel.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Servidor inacessível");
        }/*catch (Exception e) {
            System.out.println("Excecao: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Servidor inacessível");
        }*/

    }

    //Thread Para Receber Mensagens UDPs
    public class ThreadTimesTamp implements Runnable {

        public void run() {
            try {
                Thread.currentThread().sleep(5000); // 5 segundos
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (conectou == false) {
                JOptionPane.showMessageDialog(null, "Servidor Inacessível!");
                receberUDPThread.stop();
            }

        }
    }

    //Metodo que recebe uma mensagem por parametro e a envia ao grupo multicast
    void EnviarMensagemUDP(String mensagem) {

        byte[] sendData = new byte[1024];
        String sentence;

        sentence = mensagem;
        sendData = sentence.getBytes();

        try {
            IPAddress = InetAddress.getByName("127.0.0.1");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5556);

            clientSocket.send(sendPacket);
            System.out.println("Mensagem Enviada");

        } catch (IOException ex) {
            Logger.getLogger(ChatPainel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        jtmsgEnviar.setText("");

    }

    //Thread Para Receber Mensagens UDPs
    public class UDPThread implements Runnable {

        MulticastSocket serverSocket = null;

        public void run() {

            try {
                IPAddress = InetAddress.getByName("239.255.255.255");
                serverSocket = new MulticastSocket(5555);
                serverSocket.joinGroup(IPAddress);
            } catch (SocketException ex) {
                Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            }

            EnviarMensagemUDP(jtApelido.getText() + " entrou na sala");
            while (true) {

                System.out.println("Aguardando nova Mensagem");
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                } catch (IOException ex) {
                    Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                //serverSocket.close();

                System.out.println("Mensagem recebida de: " + receivePacket.getAddress());

                String sentence = new String(receivePacket.getData());

                sentence = sentence.trim();
                if (sentence.equals("ok") && !conectou) {
                    threadTimesTamp.stop();
                    conectou = true;
                    ignorar = true;
                    System.out.println("Conexao estabelecida");

                    conectou();

                    //EnviarMensagemUDP(jtApelido.getText() + " entrou na sala");
                    jtMensagemGrupo.append("Voce entrou na sala" + "\n");

                    /*Se o cliente conectar ao grupo, inicia-se uma thread para 
              Receber as mensagens*/
                } else {

                    if (ignorar) { //Se a mensagem foi enviada por mim mesmo, ignorar
                        ignorar = false;
                    } else {
                        if (conectou && !sentence.equals("ok")) {
                            jtMensagemGrupo.append(sentence + "\n");
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btConectar = new javax.swing.JButton();
        rbTCP = new javax.swing.JRadioButton();
        rbUDP = new javax.swing.JRadioButton();
        tfServer = new javax.swing.JTextField();
        jtApelido = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btDesconectar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtMensagemGrupo = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtmsgEnviar = new javax.swing.JTextArea();
        btEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 153, 255));

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados de conexão"));

        jLabel3.setText("IP do Servidor");

        btConectar.setText("Entrar");
        btConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConectarActionPerformed(evt);
            }
        });

        rbTCP.setText("TCP");
        rbTCP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rbTCPMouseClicked(evt);
            }
        });
        rbTCP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTCPActionPerformed(evt);
            }
        });

        rbUDP.setText("UDP");
        rbUDP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rbUDPMouseClicked(evt);
            }
        });
        rbUDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbUDPActionPerformed(evt);
            }
        });

        tfServer.setText("127.0.0.1");

        jLabel1.setText("Seu Apelido:");

        btDesconectar.setText("Sair");
        btDesconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDesconectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbTCP)
                    .addComponent(rbUDP))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel3)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfServer, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addComponent(jtApelido))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btConectar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btDesconectar)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbTCP)
                    .addComponent(jLabel3)
                    .addComponent(tfServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbUDP)
                        .addContainerGap(2, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtApelido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btConectar)
                            .addComponent(btDesconectar)))))
        );

        jPanel1.setBackground(new java.awt.Color(0, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Chat"));

        jtMensagemGrupo.setEditable(false);
        jtMensagemGrupo.setColumns(20);
        jtMensagemGrupo.setRows(5);
        jScrollPane1.setViewportView(jtMensagemGrupo);

        jtmsgEnviar.setColumns(20);
        jtmsgEnviar.setRows(5);
        jScrollPane2.setViewportView(jtmsgEnviar);

        btEnviar.setText("Enviar");
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Thread para receber mensagens via TCP
    private class TCPThread implements Runnable {

        private ObjectInputStream input;

        public TCPThread(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Mensagem message = null;

            try {
                while ((message = (Mensagem) input.readObject()) != null) {
                    Action action = message.getAction();

                    //Cliente Conectou
                    if (action.equals(Action.CONNECT)) {
                        connected(message);

                        //Cliente desconectou
                    } else if (action.equals(Action.DISCONECT)) {
                        socket.close();

                        //Cliente recebeu mensagem do grupo
                    } else if (action.equals(Action.SEND_ALL)) {
                        jtMensagemGrupo.append(message.getText() + "\n");

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChatPainel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

//Metodo para quando o usuario se conectar via TCP
    private void connected(Mensagem message) {
        if (message.getText().equalsIgnoreCase("NO")) {
            JOptionPane.showMessageDialog(this, "Conexao nao realizada, nome ja esta sendo utilizado\n");
            return;
        } else {
            conectou();
        }

        this.mensagem = message;

    }

    private Socket socket = null;
    private Mensagem mensagem; //Mensagem trafegada pelo protocolo TCP
    private ClienteServico servico;

    public void conectarTCP() {
        String name = jtApelido.getText();
        if (!name.isEmpty()) {
            this.mensagem = new Mensagem();
            this.mensagem.setAction(Action.CONNECT);
            this.mensagem.setName(name);

            this.servico = new ClienteServico();
            this.socket = this.servico.conect();
            this.servico.send(mensagem);

            new Thread(new TCPThread(this.socket)).start();

        }
    }

    //Controle do evento precionar do botao conectar
    private void btConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConectarActionPerformed
        // TODO add your handling code here:
        apelido = jtApelido.getText();

        if (apelido.isEmpty()) { //Apelido nao informado
            JOptionPane.showMessageDialog(rootPane, "Entre com um apelido!");
            return;
        }

        ignorar = false;

        if (rbTCP.isSelected()) {
            conectarTCP();
            jtMensagemGrupo.append("Voce entrou na sala" + "\n");
        } else {
            conectarUDP();

        }
    }//GEN-LAST:event_btConectarActionPerformed

    private void rbTCPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbTCPMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_rbTCPMouseClicked

    private void rbTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTCPActionPerformed
        // TODO add your handling code here:
        rbUDP.setSelected(false);
        tfServer.setEnabled(true);
    }//GEN-LAST:event_rbTCPActionPerformed

    private void rbUDPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbUDPMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_rbUDPMouseClicked

    private void rbUDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUDPActionPerformed
        // TODO add your handling code here:
        rbTCP.setSelected(false);

    }//GEN-LAST:event_rbUDPActionPerformed

    boolean ignorar = false;
    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEnviarActionPerformed
        // TODO add your handling code here:
        jtMensagemGrupo.append("Voce disse: " + jtmsgEnviar.getText() + "\n");
        ignorar = true;

        if (rbUDP.isSelected()) {
            String mensagem = apelido + " Disse: " + jtmsgEnviar.getText();
            EnviarMensagemUDP(mensagem);
        } else {
            enviarMensagemTCP();
        }

    }//GEN-LAST:event_btEnviarActionPerformed

    //Metodo para enviar mensagem via TCP
    public void enviarMensagemTCP() {
        String text = jtmsgEnviar.getText();
        String name = mensagem.getName();
        mensagem = new Mensagem();
        mensagem.setName(name);

        mensagem.setAction(Action.SEND_ALL);

        if (!text.isEmpty()) {
            mensagem.setText(text);

            servico.send(mensagem);
        }
        jtmsgEnviar.setText("");
    }

    private void btDesconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDesconectarActionPerformed
        // TODO add your handling code here:
        conectou = false;
        ignorar = true;
        String mensagem = apelido + " saiu da sala";
        jtMensagemGrupo.append("Voce saiu da sala\n");

        if (rbUDP.isSelected()) {
            EnviarMensagemUDP(mensagem);
            clientSocket.close();
            receberUDPThread.stop();

        } else {
            desconectarTCP();
        }

        rbTCP.setEnabled(true);
        rbUDP.setEnabled(true);
        btDesconectar.setEnabled(true);
        jtApelido.setEnabled(true);
        btConectar.setEnabled(true);

        btEnviar.setEnabled(false);
        btDesconectar.setEnabled(false);

    }//GEN-LAST:event_btDesconectarActionPerformed

    //Desconectar do servidor
    public void desconectarTCP() {
        String text = jtApelido.getText() + " saiu da sala";
        String name = mensagem.getName();
        mensagem = new Mensagem();
        mensagem.setName(name);

        mensagem.setAction(Action.DISCONECT);

        servico.send(mensagem);

        jtmsgEnviar.setText("");
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConectar;
    private javax.swing.JButton btDesconectar;
    private javax.swing.JButton btEnviar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jtApelido;
    private javax.swing.JTextArea jtMensagemGrupo;
    private javax.swing.JTextArea jtmsgEnviar;
    private javax.swing.JRadioButton rbTCP;
    private javax.swing.JRadioButton rbUDP;
    private javax.swing.JTextField tfServer;
    // End of variables declaration//GEN-END:variables
}
