import java.io.*;

public class Test {
    // Default settings for Test
    private Config cfg = new Config();
    private String numTest = "20";
    private String hostName = "localhost";
    private String port = "59000";
    private String sourceFilePath = "/Users/besirkurtulmus/Desktop/TestImage.jpeg";
    private String destinationPath = "/Users/besirkurtulmus/Desktop/Simulation/";
    private String realDataSize = "128";
    private String corruptionFactor = "5";

    private String readCommand(String question, String config) {
        //  prompt the user to ask the question
        System.out.print( question );

        //  open up standard input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String answer = null;

        //  read the answer from the command-line; need to use try/catch with the
        //  readLine() method
        try {
            answer = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read your answer!");
            System.exit(1);
        }

        if (answer.equals("")) {
            System.out.println("You have selected: " + config);
            return config;
        } else {
            System.out.println("You have selected: " + answer);
            return answer;
        }
    }

    private void getParams() {
        String question_01 = "Please enter a hostname [" + hostName + "]: ";
        hostName = readCommand(question_01, hostName);
        cfg.setProperty("hostName", hostName);

        String question_02 = "Please enter a port number [" + port + "]: ";
        port = readCommand(question_02, port);
        cfg.setProperty("port", port);

        String question_03 = "Please enter source file path [" + sourceFilePath + "]: ";
        sourceFilePath = readCommand(question_03, sourceFilePath);
        cfg.setProperty("sourceFilePath", sourceFilePath);

        String question_04 = "Please enter destination path [" + destinationPath + "]: ";
        destinationPath = readCommand(question_04, destinationPath);
        cfg.setProperty("destinationPath", destinationPath);

        String question_05 = "Please enter real data size (smaller than 256 byte) [" + realDataSize + "]: ";
        realDataSize = readCommand(question_05, realDataSize);
        cfg.setProperty("realDataSize", realDataSize);
        String answer_05b = Integer.toString(256 - Integer.parseInt(realDataSize));
        cfg.setProperty("errorCorrSize", answer_05b);
        System.out.println("Parity byte size has been set to: " + answer_05b);

        String question_06 = "Please enter a corruption factor between 0-100 [" + corruptionFactor + "]: ";
        corruptionFactor = readCommand(question_06, corruptionFactor);
        cfg.setProperty("corruptionFactor", corruptionFactor);

        String question_07 = "How any times do you want to run the test? [" + numTest + "]: ";
        numTest = readCommand(question_07, numTest);

    }

    private void runTest() throws InterruptedException {
        Integer times = Integer.parseInt(numTest);

        for (int i = 0; i < times; i++) {

            // Create the server
            new Thread(new Runnable() {
                public void run() {
                    Server server = new Server();
                    server.createAndListenSocket();
                }
            }).start();


            // Create the client
            new Thread(new Runnable() {
                public void run() {
                    Client client = new Client();
                    client.createConnection();
                }
            }).start();

            while(Thread.isAlive()) {
                Thread.sleep(3000);
            }

        }

    }

    public static void main(String[] args){

        // Get params from user
        Test test = new Test();
        test.getParams();

        // Run the test
        test.runTest();

    }
}
