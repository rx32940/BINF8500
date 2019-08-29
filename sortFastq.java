import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class sortFastq{
    
    public static void main(String[] args) throws FileNotFoundException{

        ArrayList<String> identifiers = new ArrayList<String>();
        ArrayList<String> sequences = new ArrayList<String>();

        File file = new File("/Users/MACBOOK/Downloads/sample1k.fastq");
        Scanner reader = new Scanner(file);

        int i =0;
        String current = "";
        do{
            i++;
            current = reader.nextLine();
            System.out.println(current);
            
            if(i%3 == 0)
                sequences.add(current);
            else
                identifiers.add(current);     
        }
        while(current == null);

        reader.close();
        System.out.println(Arrays.toString(sequences.toArray()));

        
    }


    
}