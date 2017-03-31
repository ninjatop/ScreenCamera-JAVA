import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.text.DecimalFormat;

/**
 * Created by CHEN on 2017/3/14.
 */
public class HistogramMatching {
    public static void main(String [] args){
        try {
            BufferedImage srcImg = ImageIO.read(new File("histogram/111.jpg"));
            BufferedImage matchingImg = ImageIO.read(new File("histogram/112.png"));
            BufferedImage dstImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), srcImg.getType());
            dstImg.setData(srcImg.getData());
            HistogramMatching histogram = new HistogramMatching();
            if(histogram.HistogramMatching(srcImg, matchingImg, dstImg)){
                ImageIO.write(dstImg, "jpg", new File("histogram/114.jpg"));
            }
            else{
                System.out.println("匹配失败");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean HistogramMatching(BufferedImage srcImg, BufferedImage matchingImg, BufferedImage dstImg) {
        if (srcImg == null || matchingImg == null) {
            dstImg = null;
            return false;
        }
        /*dstImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), srcImg.getType());
        dstImg.setData(srcImg.getData());*/
        BufferedImage tempSrcBmp = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), srcImg.getType());
        tempSrcBmp.setData(srcImg.getData());
        BufferedImage tempMatchingBmp = new BufferedImage(matchingImg.getWidth(), matchingImg.getHeight(), matchingImg.getType());
        tempMatchingBmp.setData(matchingImg.getData());
        double[] srcCpR = new double[256];
        double[] srcCpG = new double[256];
        double[] srcCpB = new double[256];
        double[] matchCpB = new double[256];
        double[] matchCpG = new double[256];
        double[] matchCpR = new double[256];
        //分别计算两幅图像的累计概率分布
        getCumulativeProbabilityRGB(tempSrcBmp, srcCpR, srcCpG, srcCpB);
        getCumulativeProbabilityRGB(tempMatchingBmp, matchCpR, matchCpG, matchCpB);

        double diffAR = 0, diffBR = 0, diffAG = 0, diffBG = 0, diffAB = 0, diffBB = 0;
        short kR = 0, kG = 0, kB = 0;
        //逆映射函数
        short[] mapPixelR = new short[256];
        short[] mapPixelG = new short[256];
        short[] mapPixelB = new short[256];
        //分别计算RGB三个分量的逆映射函数
        //R
        for (int i = 0; i < 256; i++) {
            diffBR = 1;
            for (int j = kR; j < 256; j++) {
                //找到两个累计分布函数中最相似的位置
                diffAR = Math.abs(srcCpR[i] - matchCpR[j]);
                if (diffAR - diffBR < 1.0E-08) {//当两概率之差小于0.000000001时可近似认为相等
                    diffBR = diffAR;
                    //记录下此时的灰度级
                    kR = (short)j;
                }
                else {
                    kR = (short)Math.abs(j - 1);
                    break;
                }
            }
            if (kR == 255) {
                for (int l = i; l < 256; l++) {
                    mapPixelR[l] = kR;
                }
                break;
            }
            mapPixelR[i] = kR;
        }
/*        for(int i = 0; i <256; i++){
            diffBR = 1;
            kR = 0;
            for(int j = 0; j <256; j++){
                diffAR = Math.abs(srcCpR[i] - matchCpR[j]);
                if(diffAR <= diffBR){
                    kR = (short)j;
                    diffBR = diffAR;
                }
            }
            mapPixelR[i] = kR;
        }*/
        //G
        for (int i = 0; i < 256; i++) {
            diffBG = 1;
            for (int j = kG; j < 256; j++) {
                diffAG = Math.abs(srcCpG[i] - matchCpG[j]);
                if (diffAG - diffBG < 1.0E-08) {
                    diffBG = diffAG;
                    kG = (short)j;
                }
                else {
                    kG = (short) Math.abs(j - 1);
                    break;
                }
            }
            if (kG == 255) {
                for (int l = i; l < 256; l++) {
                    mapPixelG[l] = kG;
                }
                break;
            }
            mapPixelG[i] = kG;
        }
       /* for(int i = 0; i <256; i++){
            diffBG = 1;
            kG = 0;
            for(int j = 0; j <256; j++){
                diffAG = Math.abs(srcCpG[i] - matchCpG[j]);
                if(diffAG <= diffBG){
                    kG = (short)j;
                    diffBG = diffAG;
                }
            }
            mapPixelG[i] = kG;
        }*/
        //B
        for (int i = 0; i < 256; i++) {
            diffBB = 1;
            for (int j = kB; j < 256; j++) {
                diffAB = Math.abs(srcCpB[i] - matchCpB[j]);
                if (diffAB - diffBB < 1.0E-08) {
                    diffBB = diffAB;
                    kB = (short)j;
                }
                else {
                    kB = (short)Math.abs(j - 1);
                    break;
                }
            }
            if (kB == 255) {
                for (int l = i; l < 256; l++) {
                    mapPixelB[l] = kB;
                }
                break;
            }
            mapPixelB[i] = kB;
        }
        /*for(int i = 0; i <256; i++){
            diffBB = 1;
            kB = 0;
            for(int j = 0; j <256; j++){
                diffAB = Math.abs(srcCpB[i] - matchCpB[j]);
                if(diffAB <= diffBB){
                    kB = (short)j;
                    diffBB = diffAB;
                }
            }
            mapPixelB[i] = kB;
        }*/
        //映射变换

        for (int i = 0; i < dstImg.getHeight(); i++) {
            for (int j = 0; j < dstImg.getWidth(); j++) {
                int []oldRGB = getRGB(dstImg.getRGB(j ,i));
                int []newRGB = new int[]{mapPixelR[oldRGB[0]], oldRGB[1], oldRGB[2]};
                int rgb = geneRGB(newRGB[0],newRGB[1],newRGB[2]);
                dstImg.setRGB(j, i, rgb);
            }
        }
        return true;
    }


    /// <summary>
/// 计算各个图像分量的累计概率分布
/// </summary>
/// <param name="srcBmp">原始图像</param>
/// <param name="cpR">R分量累计概率分布</param>
/// <param name="cpG">G分量累计概率分布</param>
/// <param name="cpB">B分量累计概率分布</param>
    public void getCumulativeProbabilityRGB(BufferedImage srcImg, double[] cpR, double[] cpG, double[] cpB) {
        if (srcImg == null) {
            cpB = cpG = cpR = null;
            return;
        }
        //cpR = new double[256];
        //cpG = new double[256];
        //cpB = new double[256];
        int[] hR = new int[256];
        int[] hG = new int[256];
        int[] hB = new int[256];
        double[] tempR = new double[256];
        double[] tempG = new double[256];
        double[] tempB = new double[256];
        getHistogramRGB(srcImg, hR, hG, hB);
        int totalPxl = srcImg.getHeight() * srcImg.getWidth();
        tempR[0] = hR[0];
        tempG[0] = hG[0];
        tempB[0] = hB[0];
        for (int i = 1; i < 256; i++) {
            tempR[i] = tempR[i - 1] + hR[i];
            tempG[i] = tempG[i - 1] + hG[i];
            tempB[i] = tempB[i - 1] + hB[i];


            cpR[i] = (tempR[i] / totalPxl);
            cpG[i] = (tempG[i] / totalPxl);
            cpB[i] = (tempB[i] / totalPxl);
        }
    }


    public void getHistogramRGB(BufferedImage srcImg, int[] hR, int[] hG, int[] hB) {
        if (srcImg == null) {
            hR = hB = hG = null;
            return;
        }
        //hR = new int[256];
        //hB = new int[256];
        //hG = new int[256];
        for (int i = 0; i < srcImg.getHeight(); i++) {
            for (int j = 0; j < srcImg.getWidth(); j++) {
                int color = srcImg.getRGB(j,i);
                int []rgb = getRGB(color);
                hR[rgb[0]]++;
                hG[rgb[1]]++;
                hB[rgb[2]]++;
            }
        }
        return;
    }
    public int []getRGB(int color){
        return new int[]{(color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF};
    }
    public int geneRGB(int[]rgb){
        DecimalFormat df=new DecimalFormat("00000000");
        String a1 =df.format(Integer.parseInt(Integer.toBinaryString(rgb[0])));
        String b1 =df.format(Integer.parseInt(Integer.toBinaryString(rgb[1])));
        String c1 =df.format(Integer.parseInt(Integer.toBinaryString(rgb[2])));
        return Integer.valueOf(a1+b1+c1, 2);
    }
    public int geneRGB(int r, int g, int b){
        DecimalFormat df=new DecimalFormat("00000000");
        String a1 =df.format(Integer.parseInt(Integer.toBinaryString(r)));
        String b1 =df.format(Integer.parseInt(Integer.toBinaryString(g)));
        String c1 =df.format(Integer.parseInt(Integer.toBinaryString(b)));
        return Integer.valueOf(a1+b1+c1, 2);
    }












}
