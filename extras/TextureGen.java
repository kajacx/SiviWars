/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gedit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kajacx
 */
public class TextureGen {

    final int width = 64, height = 64;
    final int chunkSize = 4; //kinda inclusive
    final int numberOfTiles = 32;
    final float minVal = 0.6f;
    final float maxVal = 0.9f;
    /*final float epsilon = (maxVal - minVal) / 10; //max change
     final float epsilon2 = epsilon * 2;*/
    final float[][] tempArr = new float[width + 1][height + 1];
    //c:\kajacx\programming\yet_another\SiviWars\SiviWars-android\assets\textures\ground_textures\
    //C:/kajacx/img/testtextures
    final String savePath = "c:\\kajacx\\programming\\yet_another\\SiviWars\\SiviWars-android\\assets\\textures\\ground_textures\\";
    final String extension = "jpg";
    final String fileName = "thirdtest";

    float randomInRange() {
        return (float) (Math.random() * (maxVal - minVal) + minVal);
    }

    void generateMainFrame() {
        tempArr[0][0] = tempArr[width][0] = tempArr[0][height] =
                tempArr[width][height] = randomInRange();

        //top and bottom
        for (int i = chunkSize; i < width; i += chunkSize) {
            tempArr[i][0] = tempArr[i][height] = randomInRange();
        }
        for (int start = 0; start < width; start += chunkSize) {
            float actual = tempArr[start][0];
            float add = (tempArr[start + chunkSize][0] - tempArr[start][0]) / chunkSize;
            for (int i = 1; i < chunkSize; i++) {
                actual += add;
                tempArr[start + i][0] = tempArr[start + i][height] = actual;
            }
        }

        //left and right
        for (int j = chunkSize; j < height; j += chunkSize) {
            tempArr[0][j] = tempArr[width][j] = randomInRange();
        }
        for (int start = 0; start < height; start += chunkSize) {
            float actual = tempArr[0][start];
            float add = (tempArr[0][start + chunkSize] - tempArr[0][start]) / chunkSize;
            for (int j = 1; j < chunkSize; j++) {
                actual += add;
                tempArr[0][start + j] = tempArr[width][start + j] = actual;
            }
        }
    }

    void generateInnerAxises() {
        for (int i = chunkSize; i < width; i += chunkSize) {
            for (int j = chunkSize; j < height; j += chunkSize) {
                tempArr[i][j] = randomInRange();
            }
        }

        //horizontal (left to right)
        for (int j = chunkSize; j < height; j += chunkSize) {
            for (int start = 0; start < width; start += chunkSize) {
                float actual = tempArr[start][j];
                float add = (tempArr[start + chunkSize][j] - tempArr[start][j]) / chunkSize;
                for (int i = 1; i < chunkSize; i++) {
                    actual += add;
                    tempArr[start + i][j] = actual;
                }
            }
        }

        //vertical (top to bottom)
        for (int i = chunkSize; i < width; i += chunkSize) {
            for (int start = 0; start < height; start += chunkSize) {
                float actual = tempArr[i][start];
                float add = (tempArr[i][start + chunkSize] - tempArr[i][start]) / chunkSize;
                for (int j = 1; j < chunkSize; j++) {
                    actual += add;
                    tempArr[i][start + j] = actual;
                }
            }
        }
    }

    /**
     * positons of fixed point
     *
     * @param startX
     * @param startY
     */
    void fillChunk(int startX, int startY) {
        for (int i = 1; i < chunkSize; i++) {
            for (int j = 1; j < chunkSize; j++) {
                float tmp = 0;
                float weightTotal = 0;
                float weightAcctual;

                //right
                weightAcctual = 1f / (chunkSize - i);
                tmp += tempArr[startX + chunkSize][startY + j] * weightAcctual;
                weightTotal += weightAcctual;

                //bot
                weightAcctual = 1f / (chunkSize - j);
                tmp += tempArr[startX + i][startY + chunkSize] * weightAcctual;
                weightTotal += weightAcctual;

                //left
                weightAcctual = 1f / i;
                tmp += tempArr[startX][startY + j] * weightAcctual;
                weightTotal += weightAcctual;

                //top
                weightAcctual = 1f / j;
                tmp += tempArr[startX + i][startY] * weightAcctual;
                weightTotal += weightAcctual;

                tmp /= weightTotal;
                tempArr[startX + i][startY + j] = tmp;
            }
        }
    }

    void fillChunks() {
        for (int i = 0; i < width; i += chunkSize) {
            for (int j = 0; j < height; j += chunkSize) {
                fillChunk(i, j);
            }
        }
    }

    void arrDump() {
        char[] lineCh = new char[3 * (width + 1) + 4 * (width / chunkSize + 1) - 1];
        for (int i = 0; i < lineCh.length; i++) {
            if (i % (4 + 3 * chunkSize) == 0 || i % (4 + 3 * chunkSize) == 5) {
                lineCh[i] = '+';
            } else {
                lineCh[i] = '-';
            }
        }
        String line = new String(lineCh);
        for (int j = 0; j <= height; j++) {
            if (j % chunkSize == 0) {
                System.out.println(line);
            }
            for (int i = 0; i <= width; i++) {
                if (i % chunkSize == 0) {
                    System.out.print("| ");
                }
                System.out.format("%02d ", (int) (tempArr[i][j] * 100)); //%.3f
                if (i % chunkSize == 0) {
                    System.out.print("| ");
                }
            }
            System.out.println();
            if (j % chunkSize == 0) {
                System.out.println(line);
            }
        }
        System.out.println();
    }

    BufferedImage createBI() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int shade = (int) (255 * tempArr[i][j]);
                g.setColor(new Color(shade, shade, shade));
                g.fillRect(i, j, 1, 1);
            }
        }
        g.dispose();
        return bi;
    }

    void export(int serial) {
        BufferedImage bi = createBI();
        String fullname = String.format("%s/%s%03d.%s", savePath, fileName, serial, extension);
        try {
            ImageIO.write(bi, extension, new File(fullname));
        } catch (IOException ex) {
            Logger.getLogger(TextureGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void main() {
        generateMainFrame();

        //for (int i = 0; i < numberOfTiles; i++) {
            generateInnerAxises();
            fillChunks();
            /*export(i);
        }//*/
        arrDump();
    }

    public static void main(String[] args) {
        new TextureGen().main();
    }
}
