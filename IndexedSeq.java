/*
    This class is an IndexedSeq object.
    - The purpose of the object is to keep the original index of the sequence during sorting.
    - Array with identifiers and sequences can be arranged directly with the order of the original indices
    after sorting

    parameters: original index before sorting, sequence at the index

*/
public class IndexedSeq{
    private int index;
    private String sequence;

    public IndexedSeq(int index, String sequence){
        this.index = index;
        this.sequence=sequence;
    }

    public int getIndex(){
        return index;
    }

    public String getSeq(){
        return sequence;

    }

}