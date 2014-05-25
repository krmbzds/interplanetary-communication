import com.casualcoding.reedsolomon.EncoderDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.common.reedsolomon.Util;

import java.util.Arrays;
import java.util.Random;

public class Proxy {
    public EncoderDecoder encoderDecoder = new EncoderDecoder();
    private Integer padding = null;
    private Config cfg = new Config();
    private Integer realDataSize = Integer.parseInt(cfg.getProperty("realDataSize"));
    private Integer errorCorrSize = Integer.parseInt(cfg.getProperty("errorCorrSize"));
    private Integer corruptionFactor = Integer.parseInt(cfg.getProperty("corruptionFactor"));

    public Proxy() {

    }

    public byte[][] splitChunk(byte[] completeChunk) {

        // Divides byte array into smaller chunks
        Integer numberOfChunks = (int) Math.ceil((double) completeChunk.length / realDataSize);
        padding = completeChunk.length % realDataSize;

        byte[][] chunks = new byte[numberOfChunks][realDataSize];
        byte[][] encodedChunk = new byte[numberOfChunks][realDataSize];

        for (int i = 0; i < numberOfChunks; i++) {

            if (i != numberOfChunks - 1) {
                for (int j = 0; j < realDataSize; j++) {
                    chunks[i][j] = completeChunk[(i * realDataSize) + j];
                }
            } else {
                for (int j = 0; j < realDataSize; j++) {
                    if (j < padding - 1) {
                        chunks[i][j] = completeChunk[(i * realDataSize) + j];
                    } else {
                        chunks[i][j] = 0; // Padding beyond fullChunk
                    }
                }
            }

        }

        for (int i = 0; i < numberOfChunks; i++) {
            try {
                encodedChunk[i] = encoderDecoder.encodeData(chunks[i], errorCorrSize);
            } catch (EncoderDecoder.DataTooLargeException e) {
                e.printStackTrace();
            }
        }

    return encodedChunk;
    }

    public byte[][] corruptChunk(byte[][] chunks) {

        byte[][] corruptedChunks = deepCopy(chunks);

        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks[i].length; j++) {
                Random rand = new Random();

                int probability = rand.nextInt(100) + 1;

                if (probability < corruptionFactor) {
                    // Screw with the byte
                    corruptedChunks[i][j] = 0;
                }
            }
        }
        return corruptedChunks;
    }

    public byte[][] correctChunk(byte[][] corruptChunk) {

        byte[][] correctedChunk = deepCopy(corruptChunk);

        for (int i = 0; i < corruptChunk.length; i++) {
            try {
                // Try to decode the message using FEC algorithm
                correctedChunk[i] = encoderDecoder.decodeData(corruptChunk[i], errorCorrSize);
            } catch (ReedSolomonException e) {
                correctedChunk[i] = correctedChunk[i];
            } catch (EncoderDecoder.DataTooLargeException e) {
                correctedChunk[i] = correctedChunk[i];
            }
        }
        return correctedChunk;
    }

    public byte[] mergeChunks(byte[][] correctedChunk) {

        Integer chunkLength = correctedChunk.length;
        Integer chunkSize = chunkLength * realDataSize + padding;
        byte[] singleChunk = new byte[chunkSize];

        for (int i = 0; i < chunkLength; i++) {

            if ( i != chunkLength - 2) {
                for (int j = 0; j < correctedChunk[i].length; j++) {
                    singleChunk[i * realDataSize + j] = correctedChunk[i][j];
                }
            } else {
                for (int j = 0; j < correctedChunk[i].length; j++) {
                    if (j < padding - 1) {
                        singleChunk[i * realDataSize + j] = correctedChunk[i][j];
                    }
                }
            }

        }

        return singleChunk;

    }

    public static byte[][] deepCopy(byte[][] original) {
        if (original == null) {
            return null;
        }

        final byte[][] result = new byte[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }
}
