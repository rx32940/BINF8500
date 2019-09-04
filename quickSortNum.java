import java.util.Arrays;

class QuickSort
{
    	// Quick Sort using Hoare's Partitioning scheme
	public static void main(String[] args)
	{
        int [] unsortedSeq = new int [20]; 
        
        int [] list = {5,2,5,3,8,9,7};
        
        int i =0;
        for (i =0; i < list.length; i++){
            unsortedSeq[i]=list[i];   
        }
        
        System.out.println(i);
        int SIZE = i-1; //for loop added size one more time
        quickSort(unsortedSeq,0,SIZE);  


		System.out.println(Arrays.toString(unsortedSeq));
    }
	

	// Hoare Partition
	private static int hoarePartition(int left, int right, int [] seqTobeSort){
		int pivot = seqTobeSort[left];
		left --;
		right ++;

		while (left < right) {

			do {
				left++;
			} while (seqTobeSort[left] < pivot);

			do {
				right--;
			} while (seqTobeSort[right] > pivot);

			if (left >= right)
				return right;

			swap(seqTobeSort, left, right);
        }
        return right;
	}

	// quicksort recursive loop
	public static void quickSort(int [] unsortedSeq,int left, int right){
		// base 
		if (left >= right) {
			return;
		}

		// partition with pivot
		int pivot = hoarePartition(left, right,unsortedSeq);

		// smaller than pivot
		quickSort(unsortedSeq, left, pivot);

		// larger than pivot
		quickSort(unsortedSeq, pivot + 1, right);
	}


    //swap function
    private static void swap (int[] current, int i, int j) {
		int temp = current[i];
		current[i] = current[j];
		current[j] = temp;
	}
}