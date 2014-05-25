import com.google.zxing.common.reedsolomon.Util;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
    private DatagramSocket socket = null;
    private FileEvent fileEvent = null;
    private Config cfg = new Config();
    private Integer port = Integer.parseInt(cfg.getProperty("port"));
    private Integer realDataSize = Integer.parseInt(cfg.getProperty("realDataSize"));
    private Integer errorCorrSize = Integer.parseInt(cfg.getProperty("errorCorrSize"));
    private Integer fullChunkSize = realDataSize + errorCorrSize;

    public Server() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(port);
            byte[] incomingData = new byte[1024 * 128];
            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();

                // Simulate interplanetary communication
                Proxy proxy = new Proxy();

                byte[][] splitChunk = proxy.splitChunk(data);
                byte[][] corruptChunk = proxy.corruptChunk(splitChunk);
                byte[][] correctedChunk = proxy.correctChunk(corruptChunk);
                byte[] singleChunk = proxy.mergeChunks(correctedChunk);


                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(singleChunk);
                    ObjectInputStream is = new ObjectInputStream(in);
                    fileEvent = (FileEvent) is.readObject();
                    if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                        System.out.println("Some issue happened while packing the data @ client side");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                createAndWriteFile();   // writing the file to hard disk
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Thank you for the message";
                byte[] replyBytea = reply.getBytes();
                DatagramPacket replyPacket =
                        new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
                socket.send(replyPacket);
                Thread.sleep(3000);
                System.exit(0);

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createAndWriteFile() {
        String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
        if (!new File(fileEvent.getDestinationDirectory()).exists()) {
            new File(fileEvent.getDestinationDirectory()).mkdirs();
        }
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.createAndListenSocket();
    }
}