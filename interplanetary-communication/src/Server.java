import com.google.zxing.common.reedsolomon.Util;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server {
    private DatagramSocket socket = null;
    private FileEvent fileEvent = null;
    private Config cfg = new Config();
    private Integer port = Integer.parseInt(cfg.getProperty("port"));
    private Integer realDataSize = Integer.parseInt(cfg.getProperty("realDataSize"));
    private Integer errorCorrSize = Integer.parseInt(cfg.getProperty("errorCorrSize"));
    private Integer corruptionFactor = Integer.parseInt(cfg.getProperty("corruptionFactor"));

    public Server() {

    }

    public void createAndListenSocket() {

        while (true) {
            try {
                socket = new DatagramSocket(port);
                byte[] incomingData = new byte[1024 * 64];
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
                        //e.printStackTrace();
                        continue;
                    }

                    createAndWriteFile();   // writing the file to hard disk
                    InetAddress IPAddress = incomingPacket.getAddress();
                    int port = incomingPacket.getPort();
                    String reply = "Thank you for the message";
                    byte[] replyBytea = reply.getBytes();
                    DatagramPacket replyPacket =
                            new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
                    socket.send(replyPacket);
                }

            } catch (SocketException e) {
                //e.printStackTrace();
                continue;
            } catch (IOException e) {
                //e.printStackTrace();
                continue;
            }
        }
    }

    public void createAndWriteFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        String outputFile = fileEvent.getDestinationDirectory() + corruptionFactor + "-" + realDataSize + "-" + errorCorrSize + "-" + timeStamp + "_" + fileEvent.getFilename();
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