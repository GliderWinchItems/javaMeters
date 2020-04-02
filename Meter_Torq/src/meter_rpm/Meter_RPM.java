/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package meter_rpm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.SwingUtilities;

/**
 *
 * @author deh
 */
public class Meter_RPM {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                
          String ip;
        ip = "127.0.0.1";   // Default ip address
        //String ip = new String("10.1.1.80");
        int port = new Integer (32123); // Default port
        //
        int rpmscale = new Integer (1); // Default scale number pulses per revolution
        
    /* Deal with the arguments on the command line */
        if (args.length > 3){
            System.out.format("Only two or three args allowed, we saw %d\n", args.length);
            System.out.format("127.0.0.1 3213 [ip address and port number)]\n");
            System.out.format("127.0.0.1 3213 8 [ip address and port number and RPM scale (1 - 8)\n");
            System.exit(-1);
        } 
        if (args.length == 2){
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        if (args.length == 3){
            ip = args[0];
            port = Integer.parseInt(args[1]);
            rpmscale = Integer.parseInt(args[2]);
            System.out.format("Program sees command line arguments as: %s %d %d\n",ip,port,rpmscale);
        }
        Socket socket = new Socket(ip, port);
          BufferedReader in = 
            new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
          
          final Stackoverflow so;
                so = new Stackoverflow(25, "TORQUE: COMMANDED and REPORTED");
          
          Canmsg2 can1; 
            can1 = new Canmsg2();    // Received CAN message
            int ret;
            int[] eng;
            eng = new int[2];   
        double[] engd = new double[2];
        
   // ======== Endless loop ====================================================
        while (true) {
           String msg = in.readLine();         // Get a line from socket
     //         System.out.format("%s\n",msg);
           ret = can1.convert_msgtobin(msg);   // Convert ascii/hex msg to binary byte array
           if (ret != 0){ // Did the conversion pass all the checks?
                System.out.format("Input conversion error: %d\n", ret); // No show the error code
                continue;
           }
           // Torque commanded
           if (can1.id == 0x46600000){ /* CANID_DMOC_CMD_TORQ Gevcur sends */
             eng[1] = can1.get_1int16LE(0);   // Little Endian: Extract two bytes
             engd[1] = ((float)eng[1] - 30000.0) * 0.1; // Apply offset; scale Nm
  
            }
           // Speed reported/actual
           if (can1.id == 0x47400000){ /* 0x23A CANID_DMOC_ACTUALTORQ:I16,   DMOC: Actual Torque: payload-30000 */
             eng[0] = can1.get_1int16LE(0);   // Little Endian: Extract two bytes
             engd[0] = ((float)eng[0] - 20000);
             
//for (int i = 0; i < 6; i++) System.out.format("%02x ",can1.pb[i+6]);
//  System.out.format(" %08x %d %8.0f\n",eng[0],eng[0],engd[0]);
  

           }
            /* Scale readings for display purposes. */
            final double scaled0 = engd[1];// Torque
            final double scaled1 = engd[0];// Torque
  //System.out.format("%8.0f  %8.0f\n",engd[1],engd[0]);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run(){
                    so.setValue(scaled1, scaled0);
                }
            });                       
        }   
    }
}

