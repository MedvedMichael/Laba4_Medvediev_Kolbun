package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class WaveFile {
    WaveFile(String path) {
        parseFile(path);
    }

    private void parseFile(String path) {
        File file = new File(path);
        byte[] header = new byte[44];

        byte[] byteInput = new byte[(int) file.length() - 44];
        short[] input = new short[(int) (byteInput.length / 2f)];
        double kLength = 3.125;

        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(header, 0, header.length);
            fis.read(byteInput, 44, byteInput.length - 45);
            ByteBuffer.wrap(byteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] headerInt = new int[header.length / 4];
        ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(headerInt);
//        System.out.println(headerInt[10]);
        int newSize = (int) Math.round(headerInt[10] * kLength);
        headerInt[1] = 36 + newSize;
        headerInt[10] = newSize;
//        System.out.println((char)(headerShort[0]) + ((char)headerShort[1]) + ((char)headerShort[2]) + ((char)headerShort[3]));
//        System.out.println((header[41]));
//        header[41] = (byte) (Math.ceil(header[41]*kLength));
//        header[6] = (byte)1;
//        header[42] = (byte)1;
//        header[5] = header[41];
        System.out.println();

        ByteBuffer headerOutBuf = ByteBuffer.allocate(header.length);
        headerOutBuf.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < headerInt.length; i++)
            headerOutBuf.putInt(headerInt[i]);


        int newLength = (int) Math.round(input.length * kLength);
        //System.out.println( byteInput.length+ " L " + newLength);
        ByteBuffer outBuf = ByteBuffer.allocate(2 * newLength);
        outBuf.order(ByteOrder.LITTLE_ENDIAN);

        short lastValue = Byte.MAX_VALUE / 2;
        for (int i = 0; i < input.length; i++) {
            short sample = input[i];
//            System.out.print(sample + " ");
            if ((int) ((i + 1) * kLength) > i + 1) {
                int temp = (int) ((i + 1) * kLength) - (int) (i * kLength) - 1;
//                System.out.print(temp + " ");
                for (int j = 0; j < temp; j++) {
//                    System.out.print(((float)(j+1))/((float)(temp+1)) + " ");
                    short tempValue = (short) (lastValue + (short) (((float) (j + 1)) / ((float) (temp + 1)) * (sample - lastValue)));
//                    System.out.print(tempValue + " ");
                    outBuf.putShort(tempValue);
                }
            }
            outBuf.putShort(sample);
            lastValue = sample;
//            System.out.println();
        }

        try {
            FileOutputStream fos = new FileOutputStream("test2.wav");
            fos.write(headerOutBuf.array(), 0, header.length);
            fos.write(outBuf.array(), 44, outBuf.array().length - 45);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
