import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
* @author CHEN
* @create_time Oct 9, 2016 2:04:42 PM
*/
public class Draw {
	private int blockSize;//小方块的边长
	private int imageWidth;//图片宽小方块个数
	private int imageHeight;//图片长小方块个数
	private Graphics2D g;
	private BufferedImage img;
	public Draw(){
		blockSize = 6;
		imageWidth = 21*6;
		imageHeight = 21*6;
		img = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_3BYTE_BGR);
		g=img.createGraphics();
		initGraphics();
	}
	public Draw(int imageWidthLength,int imageHeightLength,int blockSize){
		this.blockSize = blockSize;
		this.imageHeight = blockSize*imageHeightLength;
		this.imageWidth = blockSize*imageWidthLength;
		img = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_3BYTE_BGR);
		g=img.createGraphics();
		initGraphics();
	}
	private void initGraphics(){
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, imageWidth, imageHeight);
	}
	public void fillRedBlock(int x,int y,int width,int height){
		g.setColor(Color.RED);
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void fillGreenBlock(int x, int y, int width, int height){
		g.setColor(Color.GREEN);
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void fillBlueBlock(int x, int y, int width, int height){
		g.setColor(Color.BLUE);
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void fillBlackBlock(int x, int y,int width, int height){
		g.setColor(Color.BLACK);
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void fillWhiteBlock(int x,int y, int width, int height){
		g.setColor(Color.white);
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void fillBlock(int x,int y, int width,int height,int rgb){
		g.setColor(new Color(rgb));
		g.fillRect(x*blockSize, y*blockSize, width*blockSize, height*blockSize);
	}
	public void save(String filePath,String fileName,String imgFormat){
		g.dispose();
        img.flush();
        File file=new File(filePath);
        if(file.exists()==false)
        	file.mkdirs();
        file = new File(filePath+fileName);
        try {
			ImageIO.write(img,imgFormat,file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	public void paint(Graphics g1){		
		super.paint(g1);
		
		g1.fillRect(5, 15, 50, 75);
	}
	public static void main(String []args){
		JFrame jFrame = new JFrame();
		jFrame.add(new Draw());
		jFrame.setTitle("Test");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.setSize(500,500);
		jFrame.setVisible(true);
		
		
	}*/
}
