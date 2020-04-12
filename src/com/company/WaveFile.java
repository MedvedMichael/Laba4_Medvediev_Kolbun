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
        int headerLengthInBytes = 44;
        File file = new File(path);
        byte[] header = new byte[headerLengthInBytes];

        byte[] byteInput = new byte[(int) file.length() - headerLengthInBytes];
        short[] input = new short[(int) (byteInput.length / 2f)];
        double kLength = 1.23456789;

        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(header, 0, header.length);
            fis.read(byteInput, headerLengthInBytes, byteInput.length - headerLengthInBytes - 1);
            ByteBuffer.wrap(byteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] headerInt = new int[header.length / 4];
        ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(headerInt);
//        System.out.println(headerInt[headerInt.length - 1]);
        int newSize = (int) Math.ceil(headerInt[headerInt.length - 1] * kLength);
        headerInt[1] = 36 + newSize;
        headerInt[headerInt.length - 1] = newSize;


        ByteBuffer headerOutBuf = ByteBuffer.allocate(header.length);
        headerOutBuf.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < headerInt.length; i++)
            headerOutBuf.putInt(headerInt[i]);


        System.out.println(input.length);
//        int newLength = (int) Math.ceil(byteInput.length * kLength);

        double[] newArrayX = new double[input.length];
        for (int i = 0; i < newArrayX.length; i++) {
            newArrayX[i] = i * kLength;
        }

        int newLength = newSize / 2;

        System.out.println(newLength);
//        SplineItem[] splines = getSplines(newArrayX,input);

        //System.out.println( byteInput.length+ " L " + newLength);
        ByteBuffer outBuf = ByteBuffer.allocate(2 * newLength);
        outBuf.order(ByteOrder.LITTLE_ENDIAN);

//        System.out.println(Arrays.toString(newArrayX));


        //Это вариант с кубическими сплайнами, который выдает металлический скрежет
//        int counter = 0;
//        int delta = 0;
//        while(counter < newArrayX.length){
//            while(counter+delta != newArrayX[counter])
//            {
//                outBuf.putShort(getValueInterpolated(splines,counter+delta));
//                delta++;
//            }
//            outBuf.putShort(input[counter]);
//            counter++;
//        }

        for (int x = 0; x < newLength; x++) {
            int index = 0;
            while (index < newArrayX.length && newArrayX[index] < x) {
                index++;
            }

            if (index >= newArrayX.length-1) {
                outBuf.putShort(input[input.length - 1]);
                continue;
            }

            if (newArrayX[index] == x) {
                outBuf.putShort(input[index]);
                continue;
            }


            double x1 = newArrayX[index - 1];
            double x2 = newArrayX[index];

            short y1 = input[index - 1];
            short y2 = input[index];

            double k = (y2 - y1) / (x2 - x1);

            // (x-x1)/(x2-x1) = (y-y1)/(y2-y1)
            short y = (short) Math.floor((x - x1) * (y2 - y1) / (x2 - x1) + y1);
            outBuf.putShort(y);
//             System.out.println(index);
        }


        // рабочая версия!!! НЕ ТРОГАТЬ!!!
//        short lastValue = 0;
//        for (int i = 0; i < input.length; i++) {
//            short sample = input[i];
//            if ((int) Math.round((i + 1) * kLength) > i + 1) {
//                int temp = (int) ((i + 1) * kLength) - (int) (i * kLength) - 1;
//                for (int j = 0; j < temp; j++) {
//                    short tempValue = (short) Math.floor(lastValue + (short) (((float) (j + 1)) / ((float) (temp + 1)) * (sample - lastValue)));
//                    outBuf.putShort(tempValue);
//                }
//
//            }
//            outBuf.putShort(sample);
//            lastValue = sample;
//        }

        try {
            FileOutputStream fos = new FileOutputStream("test2.wav");
            fos.write(headerOutBuf.array(), 0, header.length);
            fos.write(outBuf.array(), headerLengthInBytes, outBuf.array().length - headerLengthInBytes - 1);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SplineItem[] getSplines(int[] arrayX, short[] arrayY) {
        SplineItem[] splines = new SplineItem[arrayX.length];
        Arrays.fill(splines, new SplineItem(0, 0, 0, 0, 0));
        for (int i = 0; i < arrayX.length; i++) {
            splines[i].x = arrayX[i];
            splines[i].a = arrayY[i];
        }

        double[] alpha = new double[arrayX.length - 1];
        double[] beta = new double[arrayX.length - 1];


        for (int i = 1; i < arrayX.length - 1; i++) {
            double hi = arrayX[i] - arrayX[i - 1];
            double hi1 = arrayX[i + 1] - arrayX[i];
            double C = 2.0 * (hi + hi1);
            double F = 6.0 * ((arrayY[i + 1] - arrayY[i]) / hi1 - (arrayY[i] - arrayY[i - 1]) / hi);
            double z = (hi * alpha[i - 1] + C);
            alpha[i] = -hi1 / z;
            beta[i] = (F - hi * beta[i - 1]) / z;
        }

        for (int i = arrayX.length - 2; i > 0; i--)
            splines[i].c = alpha[i] * splines[i + 1].c + beta[i];

        for (int i = arrayX.length - 1; i > 0; i--) {
            double hi = arrayX[i] - arrayX[i - 1];
            splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
            splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (arrayY[i] - arrayY[i - 1]) / hi;
        }
        return splines;
    }


    private short getValueInterpolated(SplineItem[] splines, int x) {
        SplineItem spline;
        if (x <= splines[0].x)
            spline = splines[0];
        else if (x >= splines[splines.length - 1].x)
            spline = splines[splines.length - 1];
        else {
            int i = 0;
            int j = splines.length - 1;
            while (i + 1 < j) {
                int k = i + (j - i) / 2;
                if (x <= splines[k].x)
                    j = k;
                else
                    i = k;
            }
            spline = splines[j];
        }

        double dx = x - spline.x;
        return (short) (spline.a + (spline.b + (spline.c / 2.0 + spline.d * dx / 6.0) * dx) * dx);
    }


}

