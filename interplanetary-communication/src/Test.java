import com.casualcoding.reedsolomon.EncoderDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.common.reedsolomon.Util;

public class Test {

    public static void main(String[] args){
        EncoderDecoder encoderDecoder = new EncoderDecoder();

        try {

            String message = new String("EncoderDecoder Example");

            byte[] data = message.getBytes();

            byte[] encodedData = encoderDecoder.encodeData(data, 5);

            System.out.println(String.format("Message: %s", Util.toHex(data)));
            System.out.println(String.format("Encoded Message: %s", Util.toHex(encodedData)));

            encodedData[0] = (byte)(Integer.MAX_VALUE & 0xFF); // Intentionally screw up the first 2 bytes
            encodedData[1] = (byte)(Integer.MAX_VALUE & 0xFF);

            System.out.println(String.format("Flawed Encoded Message: %s", Util.toHex(encodedData)));

            byte[] decodedData = encoderDecoder.decodeData(encodedData, 5);

            System.out.println(String.format("Decoded/Repaired Message: %s", Util.toHex(decodedData)));


        } catch (EncoderDecoder.DataTooLargeException e) {
            e.printStackTrace();
        } catch (ReedSolomonException e) {
            e.printStackTrace();
        }
    }
}
