import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ProtocolException;
import java.text.DecimalFormat;
import java.util.Random;

import javax.tools.JavaCompiler;

/**
* @author CHEN
* @createtime Oct 9, 2016 3:42:30 PM
*/
public class GenerateImage {
	protected int WhiteBorderLength = 8;//最外面白色边界
	protected int BlackBorderLenght = 1;//第二层黑色边界
	protected int mixBorderLength = 1;//调色板的边界
	protected int contentLength = 33;//内容长度
	protected int blockSize = 8;//小方块大小
	protected int colorTypeNum = 8;//颜色的块数
	protected int []rgbValue ;
	public GenerateImage(){
		initRgbValue();		
	}
	/**
	 * 初始化每个颜色值
	 */
	public void initRgbValue(){
		this.rgbValue = new int[this.colorTypeNum];
		this.rgbValue[0] = geneRGB(85, 0, 0);
		this.rgbValue[1] = geneRGB(170, 0, 0);
		this.rgbValue[2] = geneRGB(255, 0, 0);
		this.rgbValue[3] = geneRGB(0, 85, 0);
		this.rgbValue[4] = geneRGB(0, 170, 0);	
		
		this.rgbValue[5] = geneRGB(0, 255, 0);
		this.rgbValue[6] = geneRGB(0, 0, 85);
		this.rgbValue[7] = geneRGB(0, 0, 170);
		
		
		/*this.rgbValue[0] = geneRGB(51, 0, 0);
		this.rgbValue[1] = geneRGB(102, 0, 0);
		this.rgbValue[2] = geneRGB(163, 0, 0);
		this.rgbValue[3] = geneRGB(204, 0, 0);
		this.rgbValue[4] = geneRGB(255, 0, 0);	
		
		this.rgbValue[5] = geneRGB(0, 51, 0);
		this.rgbValue[6] = geneRGB(0, 102, 0);
		this.rgbValue[7] = geneRGB(0, 153, 0);
		this.rgbValue[8] = geneRGB(0, 204, 0);
		this.rgbValue[9] = geneRGB(0, 255, 0);
		
		this.rgbValue[10] = geneRGB(0, 0, 51);
		this.rgbValue[11] = geneRGB(0, 0, 102);
		this.rgbValue[12] = geneRGB(0, 0, 153);
		this.rgbValue[13] = geneRGB(0, 0, 204);
		this.rgbValue[14] = geneRGB(0, 0, 255);
		this.rgbValue[15] = geneRGB(0, 0, 0);*/
		
		
		
		
	}
	
	
	public int geneRGB(int r,int g,int b){
		DecimalFormat df=new DecimalFormat("00000000");
		String a1 =df.format(Integer.parseInt(Integer.toBinaryString(r)));
		String b1 =df.format(Integer.parseInt(Integer.toBinaryString(g)));
		String c1 =df.format(Integer.parseInt(Integer.toBinaryString(b)));		
		return Integer.valueOf(a1+b1+c1, 2);
	}
	public void toImg(){//50张图片
		for(int i=0;i<50;i++){
			int imageWidthLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentLength;
			int imageHeightLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentLength;
			Draw img = new Draw(imageWidthLenght,imageHeightLenght,blockSize);
			addWhiteBorder(img);
			addBlackBorder(img);
			addColorBorder(img,i);
			addContent(img,i);
			DecimalFormat df=new DecimalFormat("000000");
			img.save("img2/",df.format(i)+".png", "png");
		}
	}
	public static void main(String []args){
		GenerateImage generateImage = new GenerateImage();
		generateImage.toImg();
		
		
	}
	/**
	 * 将内容加到二维码中
	 * 
	 * @param img	二维码
	 * @throws IOException 
	 * 
	 */
	public void addContent(Draw img, int frameIndex) {
		int contentLeftOffset = WhiteBorderLength+BlackBorderLenght+mixBorderLength;
		int contentTopOffset = WhiteBorderLength+BlackBorderLenght+mixBorderLength;
		int contentRightOffset = contentLeftOffset + contentLength;
		int contentBottomOffset = contentTopOffset + contentLength;
		Random random = new Random();		
		StringBuffer str = new StringBuffer();
		for(int y = contentTopOffset; y < contentBottomOffset; y++){
			int index = random.nextInt(this.colorTypeNum);
			for(int x = contentLeftOffset; x < contentRightOffset; x++){
				
				str.append(index+",");
				img.fillBlock(x, y, 1, 1,this.rgbValue[index]);
			}
			str.append("\n");
		}
		try {
			File file = new File("colorsequence2/");
			if(!file.exists())
				file.mkdir();
			FileWriter writer = new FileWriter( new File("colorsequence2/"+frameIndex+".txt"),true);
			writer.write(str.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for(int x =contentLeftOffset;x<contentRightOffset;x++)//一行红色
			img.fillRedBlock(x, contentTopOffset, 1, 1);
		for(int x =contentLeftOffset;x<contentRightOffset;x++)//一行绿色
			img.fillGreenBlock(x, contentTopOffset+2, 1, 1);
		for(int x =contentLeftOffset;x<contentRightOffset;x++)//一行蓝色
			img.fillBlueBlock(x, contentTopOffset+4, 1, 1);	*/	
	}
	
	
	/**
	 * 将调色边框加到二维码中
	 * 
	 * 
	 */
	public void addColorBorder(Draw img, int frameIndex){
		int leftOffset = WhiteBorderLength+BlackBorderLenght;
		int topOffset = WhiteBorderLength+BlackBorderLenght;
		int rightOffset = leftOffset + contentLength + 2* mixBorderLength;
		int bottomOffset = topOffset + contentLength + 2*mixBorderLength;
		int index=0;
		for(int y=topOffset;y<bottomOffset;y++){//左边一列
			img.fillBlock(leftOffset, y, 1, 1, this.rgbValue[index%this.colorTypeNum]);
			/*switch(index % this.colorTypeNum ){
				case 0:
					img.fillRedBlock(leftOffset, y, 1, 1);
					break;
				case 1:
					img.fillGreenBlock(leftOffset, y, 1, 1);
					break;
				case 2:
					img.fillBlueBlock(leftOffset, y, 1, 1);
					break;
			}*/
			index++;			
		}
		index=0;
		
		String temp = Integer.toBinaryString(frameIndex);		
		for(int x=leftOffset+1;x<rightOffset;x++){//上面一行
			if(index % 2 ==0)
				img.fillWhiteBlock(x, topOffset, 1, 1);
			else
				img.fillBlackBlock(x, topOffset, 1, 1);
			index++;
		}
		for(int x=leftOffset+1;x<leftOffset+10;x++){//上面一行			
				img.fillWhiteBlock(x, topOffset, 1, 1);			
		}
		index=0;
		for(int x=leftOffset+1;x<leftOffset+1+temp.length();x++){
			if(temp.charAt(index)=='0')
				img.fillBlackBlock(x, topOffset, 1, 1);
			else
				img.fillWhiteBlock(x, topOffset, 1, 1);
			index++;
		}
		index=0;
		for(int x=leftOffset+1;x<rightOffset;x++){//下面一行
			if(index % 2 ==0)
				img.fillWhiteBlock(x, bottomOffset-1, 1, 1);
			else
				img.fillBlackBlock(x, bottomOffset-1, 1, 1);
			index++;
		}
		index=0;
		for(int y=topOffset+1;y<bottomOffset;y++){//右边一列
			if(index % 2 ==0)
				img.fillWhiteBlock(rightOffset-1, y, 1, 1);
			else
				img.fillBlackBlock(rightOffset-1, y, 1, 1);
			index++;
		}		
	}
	
	/**
	 * 把黑色边框加入二维码
	 */
	public void addBlackBorder(Draw img){
		int leftOffset = WhiteBorderLength;
		int topOffset = WhiteBorderLength;
		int rightOffset = leftOffset + contentLength + 2*mixBorderLength + 2*BlackBorderLenght;
		int bottomOffset = topOffset + contentLength + 2*mixBorderLength + 2*BlackBorderLenght;
		img.fillBlackBlock(leftOffset, topOffset, contentLength+2*mixBorderLength+2*BlackBorderLenght, BlackBorderLenght);	
		img.fillBlackBlock(leftOffset, bottomOffset-BlackBorderLenght, contentLength+2*mixBorderLength+2*BlackBorderLenght, BlackBorderLenght);
		img.fillBlackBlock(leftOffset, topOffset, BlackBorderLenght, contentLength+2*mixBorderLength+2*BlackBorderLenght);	
		img.fillBlackBlock(rightOffset-BlackBorderLenght, topOffset, BlackBorderLenght,contentLength+2*mixBorderLength+2*BlackBorderLenght);
	}
	/**
	 * 把白色边框加入二维码
	 * 
	 * 
	 */
	public void addWhiteBorder(Draw img){
		int leftOffset = 0;
		int topOffset = 0;
		int rightOffset = contentLength + 2*mixBorderLength + 2*BlackBorderLenght + 2*WhiteBorderLength;
		int bottomOffset = contentLength + 2*mixBorderLength + 2*BlackBorderLenght + 2*WhiteBorderLength;
		img.fillWhiteBlock(leftOffset, topOffset, contentLength+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength, WhiteBorderLength);	
		img.fillWhiteBlock(leftOffset, bottomOffset-WhiteBorderLength, contentLength+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength, WhiteBorderLength);
		img.fillWhiteBlock(leftOffset, topOffset, WhiteBorderLength, contentLength+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength);	
		img.fillWhiteBlock(rightOffset-WhiteBorderLength, topOffset, WhiteBorderLength,contentLength+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength);
		
	}	
}
