import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class sortFastq{

    private static final int MAX = 2000000; // maximum read 2 million
    private static final int SUBLENGTH = 4; // 4 sub-string per read
    private static String [][] identifiers = new String [MAX][SUBLENGTH];//2D array, single set of read store in one subarray
    private static int FASTQ_SIZE=0;
    //main class
    public static void main(String[] args) throws FileNotFoundException {

        IndexedSeq [] unsortedSeq = importFile(args[0]); 
        System.out.println("read in done");

        quickSort(unsortedSeq,0,FASTQ_SIZE); 
        System.out.println("sorting done");

        writeFastq("/Users/rx32940/Documents/sortedSeq.txt", unsortedSeq);  
        
        System.out.println("writing done done");
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

        FASTQ_SIZE = i; //current fastq size
        reader.close();//close the reader

        return unsorted;

    }
    
    	// Hoare Partition
	private static int hoarePartition(int left, int right, IndexedSeq [] seqTobeSort){
		IndexedSeq pivot = seqTobeSort[left];
		left --;
		right ++;

		while (left < right) {

			do {
				left++;
			} while (seqTobeSort[left].getSeq().compareTo(pivot.getSeq()) < 0);

			do {
				right--;
			} while (seqTobeSort[right].getSeq().compareTo(pivot.getSeq()) > 0);

			if (left >= right)
				return right;

			swap(seqTobeSort, left, right);
        }
        return right;
	}

	// quicksort recursive loop
	public static void quickSort(IndexedSeq [] unsortedSeq,int left, int right){
		// base 
		if (left >= right) {
			return;
		}

		// partition with pivot
		int partition = hoarePartition(left, right,unsortedSeq);

		// smaller than pivot
		quickSort(unsortedSeq, left, partition);

		// larger than pivot
		quickSort(unsortedSeq, partition + 1, right);
	}


    //swap function
    private static void swap (IndexedSeq[] current, int i, int j) {
		IndexedSeq temp = current[i];
		current[i] = current[j];
		current[j] = temp;
    }
    
    private static void writeFastq(String newFile,IndexedSeq[] sortedSeq) throws FileNotFoundException {
        
        PrintWriter writer = new PrintWriter("/Users/rx32940/Documents/sortedFastq.txt");
        
        for (int i =0; i <= FASTQ_SIZE; i++){
            for(int j=0;j<4;j++){
                writer.print(identifiers[sortedSeq[i].getIndex()][j] + "\n");
            }
        }
        writer.close();

    }
}