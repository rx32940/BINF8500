import java.io.*;
import java.util.*;

public class kmeans{
    
    private static int [][] data = new int [10][10];
    public static void main(String[] args){
        readFile("/Users/rachel/Downloads/Archaea.txt");
        System.out.println("test");
        
    }

    public static void readFile(String fileName){
        try{
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            int i =0; 
            String [] allPoints=new String [1000]; // how to solve dynamic array problem
            String current="";
            while ((current = reader.readLine()) != null){

                    if(i!=0){
                        allPoints[i-1]=current; //data table with row names

                        // data point object, takes string [] as input, need to convert to an int array
                        DataPoint point = new DataPoint(allPoints[i-1].substring(allPoints[i-1].indexOf("\t")).split("\t")); 
                    }

                
                
                i++;
            }

            System.out.println(allPoints[0]);
        }catch(IOException e){
            e.printStackTrace();
        }
        

    }
}