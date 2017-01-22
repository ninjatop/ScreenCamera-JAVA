import java.io.File;
import java.io.IOException;

/**
* @author CHEN
* @create_time Oct 24, 2016 8:21:16 PM
*/


public class ImgToVideo {
    public static void main(String[] args){
        //runFFMpeg("/Users/zhantong/Desktop/test1",5,"/Users/zhantong/Downloads/SnowLeopard_Lion_Mountain_Lion_Mavericks_Yosemite_El-Captain_02.02.2016/ffmpeg");
        String imgDir = "C:\\Users\\CHEN\\IdeaProjects\\ScreenCamera-JAVA\\img13\\";
    	runFFMpeg(imgDir, 28);
    	//String command = "cmd.exe /c ffmpeg -framerate 30 -i C:\\Users\\CHEN\\workspace\\ScreenCamera\\img\\%06d.png -c:v libx264 -r 30 -pix_fmt yuv420p C:\\Users\\CHEN\\workspace\\ScreenCamera\\img\\out_framerate.mp4";

    }
    public static void runFFMpeg(String imgDir,int framerate){
    	String command=String.format("ffmpeg -framerate %d -i %s -c:v libx264 -r %d -pix_fmt yuv420p %sout_framerate_%d.mp4",framerate,imgDir+"%06d.png",framerate,imgDir,framerate);
    	try {
			Runtime.getRuntime().exec("cmd.exe /c"+command);
        	//Runtime.getRuntime().exec("cmd.exe /c start notepad");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void runFFMpeg(String imgDir,int framerate,String ffmpegDir){
        String command=String.format("ffmpeg -framerate %d -i %s -c:v libx264 -r %d -pix_fmt yuv420p out_framerate_%d.mp4",framerate,"%06d.png",framerate,framerate);
        String[] envp=new String[]{String.format("ffmpeg=%s",ffmpegDir)};
        File dir=new File(imgDir);
        runCommand(command,envp,dir);
    }
    public static void runCommand(String command,String[] envp,File dir){
        Process p=null;
        try {
        	StringBuilder str = new StringBuilder("cmd.exe /c ");
        	str.append(command);
        	
        	
        	
            p=Runtime.getRuntime().exec(command,envp,dir);
        }catch (IOException e){
            e.printStackTrace();
        }
        /*
        InputStreamReader ir=new InputStreamReader(p.getInputStream());
        LineNumberReader input=new LineNumberReader(ir);
        String line;
        try {
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }*/
    }
}
