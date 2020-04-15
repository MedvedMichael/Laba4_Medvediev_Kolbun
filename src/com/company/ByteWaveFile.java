package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteWaveFile {
    private int headerLengthInBytes;
    byte[] header;
    byte[] byteInput;

    ByteWaveFile(String pathToFile, int headerLengthInBytes){
        this.headerLengthInBytes = headerLengthInBytes;
    }

    private void parseFile(String pathToFile){
        File file = new File(pathToFile);
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(header, 0, header.length);
            fis.read(byteInput, headerLengthInBytes, byteInput.length - headerLengthInBytes - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int[] getHeader(){
        int[] headerInt = new int[header.length / 4];
        ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(headerInt);
        return headerInt;
    }

    short[] getData(){
        short[] input = new short[(int) (byteInput.length / 2f)];
        ByteBuffer.wrap(byteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input);
        return input;
    }
}
