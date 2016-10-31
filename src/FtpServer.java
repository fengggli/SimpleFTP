
import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by lifeng on 10/26/16
 * encription, check sum generation and buffer copy
 * some contents come from here: http://siberean.livejournal.com/14788.html
 */


public class FtpServer {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java FtpServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        while(true){
            try (
                    ServerSocket serverSocket = new ServerSocket(portNumber);
                    Socket clientSocket = serverSocket.accept();
                    OutputStream os = clientSocket.getOutputStream();
                    PrintWriter out =
                            new PrintWriter(os, true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));

                    ServerSocket serverSocketData = new ServerSocket(3434);
            ) {

                String inputLine, outputLine;

                // Initiate conversation with client
                FtpProtocol protocol = new FtpProtocol();
                outputLine = protocol.processInput(null);
                out.println(outputLine);

                String filePath;
                String fileName;

                System.out.println("client get connected!");


                while ((inputLine = in.readLine()) != null) {
                    outputLine = protocol.processInput(inputLine);
                    out.println(outputLine);

                    // because we want message sent before real data
                    if (outputLine.startsWith("#send")) {
                        StringTokenizer tokenizer = new StringTokenizer(outputLine);
                        String header = tokenizer.nextToken();
                        int numFiles = Integer.parseInt(tokenizer.nextToken());

                        // this is the conformation to be send to client
                        String fileInformation;

                        dataTransform dt = new dataTransform();


                        for (int i = 0; i < numFiles; i++) {
                            // send all the  files
                            filePath = tokenizer.nextToken();
                            File f = new File(filePath);

                            fileName = f.getName();
                            long length = f.length();
                            String checksum;
                            String ackString;
                            boolean fileSent = false;


                            try {
                                checksum = dt.createChecksum(filePath);

                                // send the file information before data transfer(file length and checksum)
                                fileInformation = fileName + " " + length + " " + checksum;
                                out.println(fileInformation);

                            } catch (Exception e) {
                                System.out.println("Exception when creating checksum");
                                System.out.println(e.getMessage());
                            }

                            int maxTries = 3;
                            int numTries = 0;
                            while(numTries < maxTries){


                                // then we can start to send file
                                try (
                                        // create a new socket for data transfer
                                        Socket clientSocketData = serverSocketData.accept();
                                        OutputStream osData = clientSocketData.getOutputStream();

                                        // cypher defined here
                                        // send file
                                        InputStream fis = new FileInputStream(filePath);
                                ) {
                                    int count = dataTransform.encrypt(fis, osData, "password12345678");
                                    //int count = dt.mySend(fis, osData);
                                    if (count == length) {
                                        System.out.println("Send file " + fileName + " with " + count + " bytes");
                                    } else {
                                        System.out.println("file length = " + Long.toString(length) + "but " + count + " bytes are sent");
                                    }
                                } catch (IOException e) {
                                    System.out.println("IO Exception when sending file");
                                    System.out.println(e.getMessage());
                                } catch (Exception e) {
                                    System.out.println("cipher Creation Exception when sending file");
                                    System.out.println(e.getMessage());
                                }
                                ackString = in.readLine();
                                if(ackString.equals("ackfile")){
                                    fileSent = true;
                                    break;
                                }
                                numTries += 1;
                            }
                        }
                    }
                    if (outputLine.endsWith("Bye."))
                        break;
                }

            } catch (Exception e) {
                System.out.println("Exception when creating encryption");
                System.out.println(e.getMessage());
            }
            System.out.println("client exit, server keep listening... CTL+C to terminate server process");
        }
    }
}
