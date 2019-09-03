
public class quickSortNumber{

    private static int SIZE = 0;
    //main class
    public static void main(String[] args) {

        int [] unsortedSeq = new int [20]; 
        
        int [] list = {5,2,5,3,8,9,7,10,25};
        
        int i =0;
        for (i =0; i < list.length; i++){
            unsortedSeq[i]=list[i];   
        }
        
        SIZE = i-1; //for loop added size one more time
        quickSort(unsortedSeq,0,SIZE);                       
    }

    //sort the sequence
    public static void quickSort(int [] unsortedSeq,int left, int right){
        
        if(left >= right){
            return; //base, subarray sorted
        }

        // pick middle seq as the pivot from the unosrted seq to avoid sorted list
        int pivot = unsortedSeq[SIZE/2]; 
        int boundry = hoarePartition(left,right,pivot,unsortedSeq);
        
        quickSort(unsortedSeq,left,boundry-1);
        quickSort(unsortedSeq, boundry, right);

        

    }
    
    //return the index for to indicate the partition position of the new subarray 
    private static int hoarePartition(int left, int right, int pivot, int [] seqTobeSort){
        
        while(left <= right){ //while left pointer is still at the left of the right pointer
            
            //use do while so don't need to check left <= right twice
            do{
                left++;
            }
            while(seqTobeSort[left] < pivot);
            
            do{
                right--;
            }
            while(seqTobeSort[right] > pivot);
            
            
            swap(seqTobeSort,left,right); //else swap left and right seq
            
        }
        return right;

    }

    private static void swap(int [] current, int i, int j){
        int temp = current[i];
        current[i] = current[j];
        current[j] = temp;
    }
}

