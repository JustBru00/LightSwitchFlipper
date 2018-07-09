package com.gmail.justbru00.lightswitchflipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class NetworkServerManager {

	private static ServerSocket listener;
	private static int clientNumber = 0;
	private static ArrayList<ClientManager> clients = new ArrayList<ClientManager>();
	
	public static void startServer() {
		
		try {
			Messager.info("Creating ServerSocket...");
			listener = new ServerSocket(2018);
			Messager.info("DONE");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		try {
			while (LightSwitchFlipperMain.RUNNING) {
			Messager.info("Waiting for any CLIENT");
			// Create client handler
			ClientManager client = new ClientManager(listener.accept(), clientNumber++);
			client.start();
			clients.add(client);
			}
		} catch (SocketException e2){
			Messager.warn("Socket Closed");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public static void closeServer() {
		
		for (ClientManager c : clients) {
			c.shutdown();
		}
		
		try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Private thread that handles responses.
	 * 
	 *
	 */
    private static class ClientManager extends Thread {
        private Socket socket;
        private int clientNumber;
        private boolean stop = false;
        private BufferedReader in;
        private PrintWriter out;

        public ClientManager(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            Messager.info("New connection with client# " + clientNumber + " at " + socket);
        }
        
        public void shutdown() {
        	stop = true;
        	try {
				in.close();
				socket.close();
			} catch (IOException e) {
				Messager.warn("Uhh.. Couldn't close input stream.");
				e.printStackTrace();
			}
        	out.close();        	
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back DONE <cmdname>
         */
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
               in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
               out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("CONNECTION ACCEPTED");

                // Get messages from the client, line by line; 
                while (!stop) {
                    String input = in.readLine();
                    if (input == null || input.equalsIgnoreCase("DISCONNECT")) {
                        break;
                    }                     
                   
                    if (input.equalsIgnoreCase("HOUSEFULL")) {
                    	// TODO 
                    	out.println("DONE HOUSEFULL");
                    	Messager.info("Client #" + clientNumber + " commanded HOUSEFULL");
                    } else if (input.equalsIgnoreCase("HOUSEHALF")) {
                    	// TODO 
                    	out.println("DONE HOUSEHALF");
                    	Messager.info("Client #" + clientNumber + " commanded HOUSEHALF");
                    } else if (input.equalsIgnoreCase("HOUSEOUT")) {
                    	// TODO
                    	out.println("DONE HOUSEOUT");
                    	Messager.info("Client #" + clientNumber + " commanded HOUSEOUT");
                    } else if (input.equalsIgnoreCase("HOUSEWARN")) {
                    	// TODO If house is at full go half -> full -> half
                    	out.println("DONE HOUSEWARN");
                    	Messager.info("Client #" + clientNumber + " commanded HOUSEWARN");
                    } else if (input.equalsIgnoreCase("PING")) {
                    	out.println("PONG");
                    	Messager.info("-> RECEIVED PING -- Sent PONG <-");
                    } else {
                    	out.println("UNKNOWN COMMAND");
                    }
                }
            } catch (IOException e) {
               Messager.warn("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Messager.critical("Couldn't close a socket, what's going on?");
                }
               Messager.info("Connection with client# " + clientNumber + " closed");
            }
        }
	
}
}
