import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ProtocolException;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.Random;

import javax.tools.JavaCompiler;

import org.w3c.dom.css.CSSCharsetRule;

/**
* @author CHEN
* @createtime Oct 9, 2016 3:42:30 PM
*/
public class GenerateImage {
	protected int WhiteBorderLength = 20;//最外面白色边界
	protected int BlackBorderLenght = 1;//第二层黑色边界
	protected int mixBorderLength = 1;//调色板的边界
	protected int contentWidth = 180;//内容长度
	protected int contentHeight = 100;//内容高度
	protected int blockSize = 8;//小方块大小
	protected int colorTypeNum = 9;//颜色的块数
	protected int deltaNum = 4;//变化的数目
	protected int []rgbValue ;
	
	protected int balck;
	protected int white;
	public GenerateImage(){
		
		initRgbValue();		
	}
	/**
	 * 初始化每个颜色值
	 */
	
	public void initRgbValue(){
		this.rgbValue = new int[this.deltaNum * 2 + 1];
		this.rgbValue[0] = geneRGB(130,130,0);	
		this.rgbValue[1] = geneRGB(50, 130, 0);
		this.rgbValue[2] = geneRGB(100, 130, 0);
		this.rgbValue[3] = geneRGB(160, 130, 0);
		this.rgbValue[4] = geneRGB(210, 130, 0);
		this.rgbValue[5] = geneRGB(130, 50, 0);
		this.rgbValue[6] = geneRGB(130, 100, 0);
		this.rgbValue[7] = geneRGB(130, 160, 0);
		this.rgbValue[8] = geneRGB(130, 210, 0);
		
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
	public void toImg(){//50张图片
		File file = new File("img8/");
		if(!file.exists())
			file.mkdir();
		for(int i=0;i<50;i++){
			int imageWidthLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentWidth;
			int imageHeightLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentHeight;
			Draw img = new Draw(imageWidthLenght,imageHeightLenght,blockSize);
			addWhiteBorder(img);
			addBlackBorder(img);
			addColorBorder(img,i);
			addContent(img,i);
			DecimalFormat df=new DecimalFormat("000000");
			img.save("img8/",df.format(i)+".png", "png");
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
		int contentRightOffset = contentLeftOffset + contentWidth;
		int contentBottomOffset = contentTopOffset + contentHeight;
		Random random = new Random();
		switch(frameIndex%2){
			case 0:{//偶数页，对R通道修改
				StringBuffer str = new StringBuffer();
				for(int x = contentLeftOffset; x < contentBottomOffset; x = x + this.deltaNum + 1){	
					for(int y = contentTopOffset; y < contentRightOffset; y = y + this.deltaNum + 1){
						for(int i = 0 ;i < this.deltaNum + 1;i++){
							img.fillBlock(y+i, x, 1, 1,this.rgbValue[i]);
						}

						for(int i = 1; i < this.deltaNum + 1; i++){
							for(int j = 0; j < this.deltaNum + 1; j++){								
								int index = random.nextInt(this.deltaNum) ;									
								img.fillBlock(y+j, x+i, 1, 1, this.rgbValue[index + 1]);				
								str.append(index+",");
							}
						}											
					}
					str.append("\n");
				}
				try {
					File file = new File("colorsequence8/");
					if(!file.exists())
						file.mkdir();
					FileWriter writer = new FileWriter( new File("colorsequence8/"+frameIndex+".txt"),true);
					writer.write(str.toString());
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case 1:{
				StringBuffer str = new StringBuffer();
				for(int x = contentLeftOffset; x < contentBottomOffset; x = x + this.deltaNum + 1){	
					for(int y = contentTopOffset; y < contentRightOffset; y = y + this.deltaNum + 1){
						img.fillBlock(y, x, 1, 1,this.rgbValue[0] );
						for(int i = 1 ;i < this.deltaNum + 1;i++){
							img.fillBlock(y+i, x, 1, 1,this.rgbValue[i+this.deltaNum] );
						}

						for(int i = 1; i < this.deltaNum + 1; i++){
							for(int j = 0; j < this.deltaNum + 1; j++){								
								int index = random.nextInt(this.deltaNum) + this.deltaNum ;									
								img.fillBlock(y+j, x+i, 1, 1, this.rgbValue[index + 1]);				
								str.append(index+",");
							}
						}
					}
					str.append("\n");
				}
				try {
					File file = new File("colorsequence8/");
					if(!file.exists())
						file.mkdir();
					FileWriter writer = new FileWriter( new File("colorsequence8/"+frameIndex+".txt"),true);
					writer.write(str.toString());
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			
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
		int rightOffset = leftOffset + contentWidth + 2* mixBorderLength;
		int bottomOffset = topOffset + contentHeight + 2*mixBorderLength;
		int index=0;
		for(int y=topOffset;y<bottomOffset;y++){//左边一列
			//if()
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
		for(int x=leftOffset+1;x<leftOffset+11;x++){//上面一行			
				img.fillBlackBlock(x, topOffset, 1, 1);			
		}
		index=0;
		for(int x=leftOffset+(11-temp.length());x<leftOffset+11;x++){
			if(temp.charAt(index)=='1')
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
		int rightOffset = leftOffset + contentWidth + 2*mixBorderLength + 2*BlackBorderLenght;
		int bottomOffset = topOffset + contentHeight + 2*mixBorderLength + 2*BlackBorderLenght;
		img.fillBlackBlock(leftOffset, topOffset, contentWidth+2*mixBorderLength+2*BlackBorderLenght, BlackBorderLenght);	
		img.fillBlackBlock(leftOffset, bottomOffset-BlackBorderLenght, contentWidth+2*mixBorderLength+2*BlackBorderLenght, BlackBorderLenght);
		img.fillBlackBlock(leftOffset, topOffset, BlackBorderLenght, contentHeight+2*mixBorderLength+2*BlackBorderLenght);	
		img.fillBlackBlock(rightOffset-BlackBorderLenght, topOffset, BlackBorderLenght,contentHeight+2*mixBorderLength+2*BlackBorderLenght);
	}
	/**
	 * 把白色边框加入二维码
	 * 
	 * 
	 */
	public void addWhiteBorder(Draw img){
		int leftOffset = 0;
		int topOffset = 0;
		int rightOffset = contentWidth + 2*mixBorderLength + 2*BlackBorderLenght + 2*WhiteBorderLength;
		int bottomOffset = contentHeight + 2*mixBorderLength + 2*BlackBorderLenght + 2*WhiteBorderLength;
		img.fillWhiteBlock(leftOffset, topOffset, contentWidth+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength, WhiteBorderLength);	
		img.fillWhiteBlock(leftOffset, bottomOffset-WhiteBorderLength, contentWidth+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength, WhiteBorderLength);
		img.fillWhiteBlock(leftOffset, topOffset, WhiteBorderLength, contentHeight+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength);	
		img.fillWhiteBlock(rightOffset-WhiteBorderLength, topOffset, WhiteBorderLength,contentHeight+2*mixBorderLength+2*BlackBorderLenght+2*WhiteBorderLength);
		
	}
	public String genBit(int index){
		switch(index){
			case 1:case 5:
				return "00";				
			case 2:case 6:
				return "01";				
			case 3:case 7:
				return "10";
			case 4:case 8:
				return "11";
		}
		return null;
			
	}

}
