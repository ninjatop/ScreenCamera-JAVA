/**
 * Created by CHEN on 2017/1/6.
 */
public class temp {
    public static void main(String []args){
        int []a = new int[]{1,2,3};
        int []b = a.clone();
        a[0] = 2;
        for(int i = 0; i < 3;i++){
            System.out.print(b[i]+"\t");
        }
        for(int i = 0; i < 3;i++){
            System.out.print(a[i]+"\t");
        }
    }
}
