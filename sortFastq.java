import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class sortFastq{

    private static final int MAX = 2000000;
    private static final int SUBLENGTH = 4;
    private static String [][] identifiers = new String [MAX][SUBLENGTH];//2D array, single set of read store in one subarray
    private static IndexedSeq [] sequences = new IndexedSeq[MAX];//sequence only, with index
    
    public static void main(String[] args) throws FileNotFoundException {

        IndexedSeq [] unsortedSeq = importFile("/Users/rx32940/Documents/sample1k.fastq"); 
        System.out.println(identifiers[100][2]);   
    }

    public static IndexedSeq [] importFile(String newFile) throws FileNotFoundException {
        
        File file = new File(newFile);
        Scanner reader = new Scanner(file);
        
        IndexedSeq [] unsorted = new IndexedSeq [MAX];

        int i =0;
        int j =0;
        
        String current = "";
        while(reader.hasNextLine()){
            
            if(j <4){
                current = reader.nextLine();
                identifiers[i][j] = current; 
                if(j==1){
                    unsorted[i] = new IndexedSeq(i, current);
                }
            j++;
            }  
            else{
                j=0;
                i++;
            }
        
            
        }


        reader.close();

        return unsorted;

    }

    public static void quickSort(){


    }
    
}

