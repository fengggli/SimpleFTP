

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Created by lifeng on 10/26/16.
 * this is the client end of ftp.
 * check out the README file for more detailed decription
 */


public class FtpClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java FtpClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                InputStream is = kkSocket.getInputStream();
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null) {
                if (fromServer.endsWith("Bye."))
                    break;
                if (fromServer.startsWith("#send")) {
                    // should be passed from the message
                    // do not
                    // message will be #send filename file_length checksum
                    StringTokenizer tokenizer = new StringTokenizer(fromServer);
                    // read header
                    String header = tokenizer.nextToken();
                    int numFiles = Integer.parseInt(tokenizer.nextToken());

                    int numCorrect = 0;
                    int numReceived;
                    for (numReceived = 0; numReceived < numFiles; numReceived++) {
                        // read a new line
                        String fileInfo = in.readLine();
                        StringTokenizer tokenizerFile = new StringTokenizer(fileInfo);


                        // send each file
                        String fileName = tokenizerFile.nextToken();
                        long fileLength = Long.parseLong(tokenizerFile.nextToken());
                        String checksum = tokenizerFile.nextToken();

                        String receivePath = "./" + fileName;

                        int maxTries = 3;
                        int numTries = 0;
                        boolean fileSent = false;
                      
                        int count = 0;
                        while(numTries < maxTries){
                        // get the file content
                            try (
                                    // a new socket for data transfer
                                    Socket socketData = new Socket(hostName, 3434);
                                    InputStream isData = socketData.getInputStream();
                                    OutputStream fos = new FileOutputStream(receivePath);
                            ) {
                                System.out.println("start receiving " + fileName);
                                dataTransform dt = new dataTransform();
                                count = dataTransform.decrypt(isData, fos, "password12345678");
                                //int count = dt.myReceive(isData, fos, fileLength);
                                fos.close();

                                // check file
                                String receivedChecksum = dt.createChecksum(receivePath);
                                if (receivedChecksum.equals(checksum) == false) {
                                    System.out.println("    checksum not correct!");
                                    System.out.println("    should be:" + checksum);
                                    System.out.println("    but now is:" + receivedChecksum);
                                    System.out.println("retry for the " + (numTries+1) + " time");
                                    out.println("errorfile");

                                } else {
                                    out.println("ackfile");
                                    fileSent = true;
                                    break;
                                }

                            } catch (Exception e) {
                                System.out.println("Exception when Receive the file");
                                e.printStackTrace(System.out);
                            }
                            numTries += 1;
                        }
                        if(fileSent == true){
                            System.out.println("    File No." + (numReceived + 1) + " of " + numFiles + " files: " + fileName
                                            + " is downloaded and checksum " +checksum + " verified. (" + count + " bytes read)\n");
                            numCorrect +=1 ;
                        }else{
                            System.out.println("    File No." + (numReceived + 1) + " of " + numFiles + " files: " + fileName
                                            + " sending failure after " + numTries + " tries");
                            // also close the socket connection
                        }
                    }

                    if (numCorrect == numFiles) {
                        out.println("#ack");
                    } else {
                        out.println("#err");
                        System.out.println("transmission error happens, now exit");
                        System.exit(-1);
                    }

                } else {
                    System.out.println("Server: " + fromServer);
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        out.println(fromUser);
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
