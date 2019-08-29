import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class sortFastq{

    private static final int MAX = 2000000; // maximum read 2 million
    private static final int SUBLENGTH = 4; // 4 sub-string per read
    private static String [][] identifiers = new String [MAX][SUBLENGTH];//2D array, single set of read store in one subarray
    private static IndexedSeq [] sequences = new IndexedSeq[MAX];//sequence only, with index
    
    //main class
    public static void main(String[] args) throws FileNotFoundException {

        IndexedSeq [] unsortedSeq = importFile("/Users/rx32940/Documents/sample1k.fastq"); 
        System.out.println(identifiers[100][2]);   
    }

    //function for file import
    public static IndexedSeq [] importFile(String newFile) throws FileNotFoundException {
        
        File file = new File(newFile); // new file
        Scanner reader = new Scanner(file); //reader
        
        IndexedSeq [] unsorted = new IndexedSeq [MAX]; // array with IndexedSeq object

        int i =0;//index of the reads
        int j =0;//index of the line in each read
        
        String current = ""; // current line reading
        while(reader.hasNextLine()){
            
            if(j <4){// still in current read
                current = reader.nextLine(); //read the line
                identifiers[i][j] = current; //save the current line in full table
                if(j==1){// line with seq
                    unsorted[i] = new IndexedSeq(i, current);// save the seq and index to the seq array
                }
            j++;//to next line inside the read
            }  
            else{
                j=0; //to new read, from first line 
                i++; //to new read
            }
        
            
        }


        reader.close();//close the reader

        return unsorted;

    }
    
    //sort the sequence
    public static void quickSort(){


    }
    
}

