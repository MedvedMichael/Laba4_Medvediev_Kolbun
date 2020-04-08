package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WaveFile {
    WaveFile(String path) {
        parseFile(path);
    }

    private void parseFile(String path) {
        File file = new File(path);
        byte[] header = new byte[44];
        byte[] byteInput = new byte[(int) file.length() - 44];
        short[] input = new short[(int) (byteInput.length / 2f)];
        double kLength = 1.95;

        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(header, 0, header.length);
            fis.read(byteInput, 44, byteInput.length - 45);
            ByteBuffer.wrap(byteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int newLength = (int) Math.round(input.length * kLength);
        //System.out.println( byteInput.length+ " L " + newLength);
        ByteBuffer outBuf = ByteBuffer.allocate(2*newLength);
        outBuf.order(ByteOrder.LITTLE_ENDIAN);

        short lastValue = 0;
        for (int i = 0; i < input.length; i++) {
            short sample = input[i];
            System.out.print(sample + " ");
            if((int)((i+1)*kLength) > i+1){
                int temp = (int)((i+1)*kLength) - (int)(i*kLength) - 1;
                System.out.print(temp + " ");
                for(int j=0;j<temp;j++){
                    System.out.print(((float)(j+1))/((float)(temp+1)) + " ");
                    short tempValue = (short)(lastValue + (short)(((float)(j+1))/((float)(temp+1))*(sample - lastValue)));
                    System.out.print(tempValue + " ");
                    outBuf.putShort(tempValue);
                }
            }
            outBuf.putShort(sample);
            lastValue = sample;
            System.out.println();
        }

        try {
            FileOutputStream fos = new FileOutputStream("test.wav");
            fos.write(header, 0, header.length);
            fos.write(outBuf.array(), 44, outBuf.array().length - 45);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
