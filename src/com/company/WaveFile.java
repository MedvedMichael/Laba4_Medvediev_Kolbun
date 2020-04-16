package com.company;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class WaveFile {
    int[] headerInt;
    short[] dataShort;
    private double kLength;

    WaveFile(String path) {
        getParameters(path);

    }

    private void getParameters(String path) {
        ByteWaveFile temp = new ByteWaveFile(path, 44);
        headerInt = temp.getHeader();
        dataShort = temp.getData();
    }

    void changeAudioLength(double kLength) {
        int size = headerInt[headerInt.length - 1];
        int newSize = Math.abs((int) Math.round(size * kLength));
        headerInt[headerInt.length - 1] = newSize;
        headerInt[1] = 36 + newSize;


        double[] newArrayX = new double[dataShort.length];

        for (int i = 0; i < newArrayX.length; i++) {
            newArrayX[i] = i * Math.abs(kLength);

        }

        if (kLength < 0) {
            for (int i = 0; i < newArrayX.length / 2; i++) {
                short temp = dataShort[i];
                dataShort[i] = dataShort[newArrayX.length - i - 1];
                dataShort[newArrayX.length - i - 1] = temp;
            }
        }


        int newLength = Math.abs((int) Math.round(dataShort.length * kLength));

        short[] newData = getInterpolatedData(newArrayX, newLength);

        dataShort = newData;


    }

    private short[] getInterpolatedData(double[] newArrayX, int newLength) {
        short[] newArrayY = new short[newLength];
        int index = 0;

        for (int x = 0; x < newLength; x++) {

            while (index < newArrayX.length && newArrayX[index] < x) {
                index++;
            }

            if (index >= newArrayX.length - 1) {
                newArrayY[x] = dataShort[dataShort.length - 1];
                continue;
            }

            if (newArrayX[index] == x) {
                newArrayY[x] = dataShort[index];
                continue;
            }

            if (index == 0) {

                newArrayY[x] = dataShort[dataShort.length - 1];
                continue;
            }

            if (index == dataShort.length - 1) {

                newArrayY[x] = dataShort[0];
                continue;
            }


            double x1 = newArrayX[index - 1];
            double x2 = newArrayX[index];

            short y1 = dataShort[index - 1];
            short y2 = dataShort[index];

            short y = (short) Math.floor((x - x1) * (y2 - y1) / (x2 - x1) + y1);
            newArrayY[x] = y;
        }

        return newArrayY;
    }

    void fileRecording(String pathOutput) {

        int fileLength = headerInt.length * 4 + dataShort.length * 2;
        ByteBuffer outBuf = ByteBuffer.allocate(fileLength);
        outBuf.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < headerInt.length; i++) {
            outBuf.putInt(headerInt[i]);
        }
        for (int i = 0; i < dataShort.length; i++) {
            outBuf.putShort(dataShort[i]);
        }
        try {
            FileOutputStream fos = new FileOutputStream(pathOutput);
            fos.write(outBuf.array(), 0, fileLength);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
