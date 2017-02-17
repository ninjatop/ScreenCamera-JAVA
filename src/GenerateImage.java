import ReedSolomon.GenericGF;
import ReedSolomon.ReedSolomonDecoder;
import ReedSolomon.ReedSolomonEncoder;
import ReedSolomon.ReedSolomonException;
import net.fec.openrq.ArrayDataDecoder;
import net.fec.openrq.EncodingPacket;
import net.fec.openrq.OpenRQ;
import net.fec.openrq.encoder.DataEncoder;
import net.fec.openrq.encoder.SourceBlockEncoder;
import net.fec.openrq.parameters.FECParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
* @author CHEN
* @createtime Oct 9, 2016 3:42:30 PM
 *
 * 由文件生成多个二维码
 * 文件->RaptorQ编码->多个RaptorQ包->RS编码->多个RS编码包->扩展为BitSet->多个二维码
 * RaptorQ包由source symbol和repair symbol组成
 * 二维码除BitSet提供的内容外,还有另外的辅助信息

*/
public class GenerateImage {
	protected int WhiteBorderLength = 15;//最外面白色边界
	protected int BlackBorderLenght = 1;//第二层黑色边界
	protected int mixBorderLength = 1;//调色板的边界
	protected int contentWidth = 50;//内容长度
	protected int contentHeight = 50;//内容高度
	protected int blockSize = 8;//小方块大小
	protected int colorTypeNum = 9;//颜色的块数
	protected int deltaNum = 4;//变化的数目
    protected int bitsPerBlock = 2;//每个小方块的bit数目
	protected int []rgbValue ;
	protected double ecLevel = 0.1;//%20用来纠错
    protected int ecNum ;//RS纠错中用于纠错的symbol个数，最后的个数
    protected int ecLength = 12;//一个symbol对应bit数目,应与RS的decoder参数保持一致
    protected int fileByteNum;//文件中byte大小
	protected int frameBitNum ;//每一帧的bit总数目
	FECParameters parameters = null;//临时放在这里
	
	protected int balck;
	protected int white;
	protected String imgPath ="img14/";
	protected String textPath = "colorsequence14/";
	public GenerateImage(){
		initRgbValue();
		frameBitNum = contentHeight * contentWidth * bitsPerBlock * this.deltaNum / (this.deltaNum + 1);
		this.ecNum = calcEcNum(ecLevel);
	}
	/**
	 * 初始化每个颜色值
	 */
	
	public void initRgbValue(){
		this.rgbValue = new int[this.deltaNum * 2 + 1];
		this.rgbValue[0] = geneRGB(150,150,0);
		this.rgbValue[1] = geneRGB(30, 255, 0);
		this.rgbValue[2] = geneRGB(120, 255, 0);
		this.rgbValue[3] = geneRGB(180, 255, 0);
		this.rgbValue[4] = geneRGB(250, 255, 0);
		this.rgbValue[5] = geneRGB(255, 30, 0);
		this.rgbValue[6] = geneRGB(255, 120, 0);
		this.rgbValue[7] = geneRGB(255, 180, 0);
		this.rgbValue[8] = geneRGB(255, 250, 0);
		
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
	protected int calcEcNum(double ecLevel){
		return (int)(frameBitNum / ecLength * ecLevel);
	}

    /**
     * 读取输入文件,生成RaptorQ编码后的byte[]组成的list
     * byte[]的长度为二维码容量减去RS纠错部分
     *
     * @param filePath 输入文件路径
     * @return raptorQ编码后的byte[]组成的list
     */
    private List<byte[]> readFile(String filePath) {
        //一个二维码实际存储的文件信息,最后的8byte为RaptorQ头部
        final int realByteLength = frameBitNum / 8 - ecNum * ecLength / 8 - 8;
        final int NUMBER_OF_SOURCE_BLOCKS=1;
        final double REPAIR_PERCENT=2;
        List<byte[]> buffer = new LinkedList<>();
        Path path = Paths.get(filePath);
        byte[] byteData = null;
        try {
            byteData = Files.readAllBytes(path);
            fileByteNum = byteData.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("file is %d bytes", fileByteNum));
        /*FECParameters */parameters = FECParameters.newParameters(fileByteNum, realByteLength, NUMBER_OF_SOURCE_BLOCKS);
        assert byteData != null;
        DataEncoder dataEncoder = OpenRQ.newEncoder(byteData, parameters);
        System.out.println("RaptorQ parameters: "+parameters.toString());
        for (SourceBlockEncoder sourceBlockEncoder : dataEncoder.sourceBlockIterable()) {
            System.out.println(String.format("source block %d: contains %d source symbols",
                    sourceBlockEncoder.sourceBlockNumber(), sourceBlockEncoder.numberOfSourceSymbols()));
            for (EncodingPacket encodingPacket : sourceBlockEncoder.sourcePacketsIterable()) {
                byte[] encode = encodingPacket.asArray();
                buffer.add(encode);
            }
        }
        //因RaptorQ不保证最后一个source symbol的大小为指定大小,而二维码需要指定大小的内容,所以把最后一个source symbol用repair symbol替代
        buffer.remove(buffer.size() - 1);
        SourceBlockEncoder lastSourceBlock = dataEncoder.sourceBlock(dataEncoder.numberOfSourceBlocks() - 1);
        buffer.add(lastSourceBlock.repairPacket(lastSourceBlock.numberOfSourceSymbols()).asArray());
        int repairNum = (int)(buffer.size()* REPAIR_PERCENT);
        for (int i = 1; i <= repairNum; i++) {
            for (SourceBlockEncoder sourceBlockEncoder : dataEncoder.sourceBlockIterable()) {
                byte[] encode = sourceBlockEncoder.repairPacket(sourceBlockEncoder.numberOfSourceSymbols() + i).asArray();
                buffer.add(encode);
            }
        }
        System.out.println(String.format("generated %d symbols (the last 1 source symbol is dropped)", buffer.size()));
        return buffer;
    }

    /**
     * 对RaptorQ编码后内容进行RS编码,并将编码后的内容转换为BitSet
     *
     * @param byteBuffer raptorQ编码后的byte[]组成的list
     * @return RS编码后转换为BitSet组成的list
     */
    private List<BitSet> RSEncode(List<byte[]> byteBuffer) {
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(GenericGF.AZTEC_DATA_12);
        List<BitSet> bitSets = new LinkedList<>();
        for (byte[] b : byteBuffer) {
            int[] ordered = new int[(int)Math.ceil(frameBitNum / ecLength)];
            for (int i = 0; i < b.length * 8; i++) {
                if ((b[i / 8] & (1 << (i % 8))) > 0) {
                    ordered[i / ecLength] |= 1 << (i % ecLength);
                }
            }
            encoder.encode(ordered, ecNum);
            bitSets.add(toBitSet(ordered, ecLength,frameBitNum - ecLength * ecNum));
        }
        return bitSets;
    }
    /**
     * 将int[]转换为BitSet,只用到每个int的低bitNum位
     *
     * @param data   int[]
     * @param bitNum 用到的int的低bitNum位
     * @return 转换得到的BitSet
     */
    private static BitSet toBitSet(int data[],int bitNum,int numRealBits){
        int cut=(numRealBits-1)/bitNum;
        int index=0;
        BitSet bitSet=new BitSet();
        for(int j = 0;j < data.length; j++){
            int current = data[j];
/*            if(j == cut){
                for(int i = 0;i <= (numRealBits-1) % bitNum; i++){
                    if((current&(1<<i))>0){
                        bitSet.set(index);
                    }
                    index++;
                }
            }else{*/
                for(int i = 0;i < bitNum; i++){
                    if((current & (1 << i ))>0){
                        bitSet.set(index);
                    }
                    index++;
                //}
            }
        }
        return bitSet;
    }
    /**
     * 检查文件夹路径是否存在,不存在则创建
     *
     * @param directory 文件夹路径
     */
    private void checkDirectory(String directory) {
        File folder = new File(directory);
        boolean b = false;
        if (!folder.exists()) {
            b = folder.mkdirs();
        }
        if (b) {
            System.out.println("Directory successfully created");
        } else {
            System.out.println("Directory already exists");
        }
    }


    public void toImg(List<BitSet> bitSets){
        checkDirectory(imgPath);
        checkDirectory(textPath);
        int frameIndex = 0;
        //String head = genHead(fileByteNum);
		for(BitSet bitset :bitSets){
			int imageWidthLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentWidth;
			int imageHeightLenght=WhiteBorderLength*2+BlackBorderLenght*2+mixBorderLength*2+contentHeight;
			Draw img = new Draw(imageWidthLenght,imageHeightLenght,blockSize);
			addWhiteBorder(img);
			addBlackBorder(img);
			addColorBorder(img, frameIndex);
			String head = genHead(fileByteNum);
			addHead(img,head);
			addFrameIndex(img,frameIndex);
			addContent(img, bitset, frameIndex);
			DecimalFormat df=new DecimalFormat("000000");
			img.save(imgPath,df.format(frameIndex)+".png", "png");
			frameIndex ++;
		}
	}
	public static void main(String []args){
		GenerateImage generateImage = new GenerateImage();
        List<byte[]> byteBuffer = generateImage.readFile("book/testSimple.txt");
        List<BitSet> bitSets = generateImage.RSEncode(byteBuffer);
        generateImage.toImg(bitSets);
		//BitSet set = bitSets.get(0);
		/*byte [] out = generateImage.solve(bitSets);
		String result = new String(out);
		System.out.print(result);
		int a = 2;*/

		
		
	}
	public byte[] solve(List<BitSet> bitSetS){
		ArrayDataDecoder dataDecoder = OpenRQ.newDecoder(parameters, 0);
		ReedSolomonDecoder decoder = new ReedSolomonDecoder(GenericGF.AZTEC_DATA_12);
		for(int loop = 0; loop < bitSetS.size(); loop ++) {
			BitSet set = bitSetS.get(loop);
			int[] con = new int[(int) Math.ceil(frameBitNum / ecLength)];
			for (int i = 0; i < set.size(); i++) {
				if (set.get(i)) {
					con[i / ecLength] |= 1 << (i % ecLength);
				}
			}
			try {
				decoder.decode(con, ecNum);
			} catch (ReedSolomonException e) {
				e.printStackTrace();
			}
			int realByteNum = frameBitNum / 8 - ecNum * ecLength / 8;
			byte[] raw = new byte[realByteNum];
			for (int i = 0; i < raw.length * 8; i++) {
				if ((con[i / ecLength] & (1 << (i % ecLength))) > 0) {
					raw[i / 8] |= 1 << (i % 8);
				}
			}
			EncodingPacket encodingPacket;
			encodingPacket = dataDecoder.parsePacket(raw, true).value();
			System.out.println(encodingPacket.encodingSymbolID()+"\t"+encodingPacket.symbolType());
			dataDecoder.sourceBlock(encodingPacket.sourceBlockNumber()).putEncodingPacket(encodingPacket);
			if(dataDecoder.isDataDecoded())
				break;
		}
		byte []out = dataDecoder.dataArray();
		return out;



	}
    /**
     * 将头信息加入到二维码
     *
     * @param img  二维码
     * @param head 头信息
     */
    private void addHead(Draw img, String head) {

        int headTopOffset = WhiteBorderLength + BlackBorderLenght ;
        int headLeftOffset = WhiteBorderLength + BlackBorderLenght + mixBorderLength;
        int headRightOffset = headLeftOffset + contentWidth ;
        if(head.length()>headRightOffset-headLeftOffset){
            System.out.println("Warning: head exceed barcode");
        }
        int i;
        for (i = 0; i < head.length() &&i < headRightOffset-headLeftOffset; i++) {
            if (head.charAt(i) == '0') {
                img.fillBlackBlock(headLeftOffset + i, headTopOffset,1,1);
            }
            else
                img.fillWhiteBlock(headLeftOffset + i, headTopOffset,1,1);
        }

    }
	/**
	 * 将内容加到二维码中
	 * 
	 * @param img	二维码
	 * @throws IOException 
	 * 
	 */
	public void addContent(Draw img, BitSet bitset, int frameIndex) {
	    int a = bitset.size();
		int contentLeftOffset = WhiteBorderLength+BlackBorderLenght+mixBorderLength;
		int contentTopOffset = WhiteBorderLength+BlackBorderLenght+mixBorderLength;
		int contentRightOffset = contentLeftOffset + contentWidth;
		int contentBottomOffset = contentTopOffset + contentHeight;


		int count = 0;
		switch(frameIndex % 2){
			case 0:{//偶数页，对R通道修改
				StringBuffer str = new StringBuffer();
				for(int x = contentTopOffset; x < contentBottomOffset; x = x + this.deltaNum + 1){
					for(int y = contentLeftOffset; y < contentRightOffset; y = y + this.deltaNum + 1){
						for(int i = 0 ;i < this.deltaNum + 1;i ++){
							img.fillBlock(y+i, x, 1, 1,this.rgbValue[i]);
						}

						for(int i = 1; i < this.deltaNum + 1; i++){
							for(int j = 0; j < this.deltaNum + 1; j++){
								int index = 0;
								for(int k=0;k < bitsPerBlock;k++){
									index = (index << 1) + (bitset.get(count + k) ? 1 : 0);
								}
								img.fillBlock(y+j, x+i, 1, 1, this.rgbValue[ index + 1]);
								str.append(index +",");
								 count += this.bitsPerBlock;
							}
						}											
					}
					str.append("\n");
				}
				try {
					FileWriter writer = new FileWriter( new File(textPath+frameIndex+".txt"),true);
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
				for(int x = contentTopOffset; x < contentBottomOffset; x = x + this.deltaNum + 1){
					for(int y = contentLeftOffset; y < contentRightOffset; y = y + this.deltaNum + 1){
						img.fillBlock(y, x, 1, 1,this.rgbValue[0] );
						for(int i = 1 ;i < this.deltaNum + 1;i++){
							img.fillBlock(y+i, x, 1, 1,this.rgbValue[i+this.deltaNum] );
						}

						for(int i = 1; i < this.deltaNum + 1; i++){
							for(int j = 0; j < this.deltaNum + 1; j++){
								int index = 0;
								for(int k=0;k < bitsPerBlock;k++){
									index = (index<<1) + (bitset.get(count + k) ? 1 : 0);
								}
								index += this.deltaNum;
								img.fillBlock(y+j, x+i, 1, 1, this.rgbValue[index + 1]);
								str.append(index +",");
								count += this.bitsPerBlock;
							}
						}
					}
					str.append("\n");
				}
				try {
					FileWriter writer = new FileWriter( new File(textPath+frameIndex+".txt"),true);
					writer.write(str.toString());
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			
		}

	}
	public void addFrameIndex(Draw img, int frameIndex){
		int topOffset = WhiteBorderLength + BlackBorderLenght + mixBorderLength;
		int x = WhiteBorderLength +BlackBorderLenght +mixBorderLength + contentWidth;
		int bottomOffset = topOffset + contentWidth + mixBorderLength;
		String strFrameIndex = genHead(frameIndex);
		if(strFrameIndex.length() > bottomOffset - topOffset){
			System.out.println("Warning: FrameIndex exceed barcode");
		}
		int i;
		for (i = 0; i < strFrameIndex.length() && i < bottomOffset - topOffset; i++) {
			if (strFrameIndex.charAt(i) == '0') {
				img.fillBlackBlock(x, topOffset + i,1,1);
			}
			else
				img.fillWhiteBlock(x, topOffset + i,1,1);
		}
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
			img.fillBlock(leftOffset, y, 1, 1, this.rgbValue[index%this.colorTypeNum]);
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
/*		for(int x=leftOffset+1;x<leftOffset+11;x++){//上面一行
				img.fillBlackBlock(x, topOffset, 1, 1);			
		}*/
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

		/*for(int y=topOffset+1;y<bottomOffset;y++){//右边一列
			if(index % 2 ==0)
				img.fillWhiteBlock(rightOffset-1, y, 1, 1);
			else
				img.fillBlackBlock(rightOffset-1, y, 1, 1);
			index++;
		}*/
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
    /**
     * 生成头信息,即32位的int内容+8位CRC8校验码
     *
     * @param x 内容
     * @return 32位的int内容+8位CRC8校验码
     */
    private String genHead(int x) {
        String pad32 = String.format("%032d", 0);
        String Pad8 = String.format("%08d", 0);
        CRC8 crc = new CRC8();
        crc.update(x);
        String c = Integer.toBinaryString((int) crc.getValue());
        String s = Integer.toBinaryString(x);
        return pad32.substring(s.length()) + s + Pad8.substring(c.length()) + c;
    }

	public int getIndex(boolean a, boolean b, int frameIndex ) {
        if (!a && !b && frameIndex ==0)
            return 1;
        if( !a && !b && frameIndex == 1)
            return 1 + this.deltaNum;
        if (!a && b && frameIndex ==0)
            return 2;
        if( !a && b && frameIndex == 1)
            return 2 + this.deltaNum;
        if (a && !b && frameIndex ==0)
            return 3;
        if( a && !b && frameIndex == 1)
            return 3 + this.deltaNum;
        if (a && b && frameIndex ==0)
            return 4;
        if( a && b && frameIndex == 1)
            return 4 + this.deltaNum;
        return -1;

    }

			


}
