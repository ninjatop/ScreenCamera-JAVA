
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 1:Graphics类是所有图形上下文的抽象基类。
 * 
 * 2:Graphics2D继承了Graphics类，实现了功能更加强大的绘图操作的集合。
 * 由于Graphics2D类是Graphics类的扩展，也是推荐使用的java绘图类
 * 所以本章主要介绍使用Graphics2D类实现JAVA绘图
 * 
 * 3:Graphics类使用的不同的方法实现不同的绘制
 * @author biexiansheng
 *
 */
public class DrawCircle extends JFrame{

    private final int OVAL_WIDTH=80;//圆形的宽
    private final int OVAL_HEIGHT=80;//圆形的高
    public DrawCircle(){
        super();
        initialize();//调用初始化方法
    }
    //初始化方法
    private void initialize(){
        this.setSize(300, 200);//设置窗体的大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置窗体的关闭方式
        setContentPane(new DrawPanel());//设置窗体面板为绘图面板对象
        this.setTitle("绘图实例2");//设置窗体标题
    }
    
    class DrawPanel extends JPanel{
        public void paint(Graphics g){
            super.paint(g);
            g.drawOval(10, 10, OVAL_WIDTH, OVAL_HEIGHT);//绘制第1个圆形
            g.drawOval(80, 10, OVAL_WIDTH, OVAL_HEIGHT);//绘制第2个圆形
            g.drawOval(150, 10, OVAL_WIDTH, OVAL_HEIGHT);//绘制第3个圆形
            g.drawOval(50, 70, OVAL_WIDTH, OVAL_HEIGHT);//绘制第4个圆形
            g.drawOval(120, 70, OVAL_WIDTH, OVAL_HEIGHT);//绘制第5个圆形
            
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DrawCircle dc=new DrawCircle();//初始化对象且调用构造方法
        dc.setVisible(true);//窗体可视化
    }

}