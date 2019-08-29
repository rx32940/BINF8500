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