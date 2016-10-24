
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 1:Graphics��������ͼ�������ĵĳ�����ࡣ
 * 
 * 2:Graphics2D�̳���Graphics�࣬ʵ���˹��ܸ���ǿ��Ļ�ͼ�����ļ��ϡ�
 * ����Graphics2D����Graphics�����չ��Ҳ���Ƽ�ʹ�õ�java��ͼ��
 * ���Ա�����Ҫ����ʹ��Graphics2D��ʵ��JAVA��ͼ
 * 
 * 3:Graphics��ʹ�õĲ�ͬ�ķ���ʵ�ֲ�ͬ�Ļ���
 * @author biexiansheng
 *
 */
public class DrawCircle extends JFrame{

    private final int OVAL_WIDTH=80;//Բ�εĿ�
    private final int OVAL_HEIGHT=80;//Բ�εĸ�
    public DrawCircle(){
        super();
        initialize();//���ó�ʼ������
    }
    //��ʼ������
    private void initialize(){
        this.setSize(300, 200);//���ô���Ĵ�С
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//���ô���Ĺرշ�ʽ
        setContentPane(new DrawPanel());//���ô������Ϊ��ͼ������
        this.setTitle("��ͼʵ��2");//���ô������
    }
    
    class DrawPanel extends JPanel{
        public void paint(Graphics g){
            super.paint(g);
            g.drawOval(10, 10, OVAL_WIDTH, OVAL_HEIGHT);//���Ƶ�1��Բ��
            g.drawOval(80, 10, OVAL_WIDTH, OVAL_HEIGHT);//���Ƶ�2��Բ��
            g.drawOval(150, 10, OVAL_WIDTH, OVAL_HEIGHT);//���Ƶ�3��Բ��
            g.drawOval(50, 70, OVAL_WIDTH, OVAL_HEIGHT);//���Ƶ�4��Բ��
            g.drawOval(120, 70, OVAL_WIDTH, OVAL_HEIGHT);//���Ƶ�5��Բ��
            
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DrawCircle dc=new DrawCircle();//��ʼ�������ҵ��ù��췽��
        dc.setVisible(true);//������ӻ�
    }

}