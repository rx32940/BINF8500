import java.util.Arrays;
import java.util.Random;

public class Sequence{
    
    protected int startMotifPos;// stores position if the new motifs enters the max score finding loop
    protected int endMotifPos;
    protected int tempMotifStart;// stores start pos for each update
    protected int tempMotifEnd;
    protected int seqLength;
    protected char [] sequences; 
   

    public Sequence(){

    }
    public Sequence(String curSeq){

        this.sequences = curSeq.toCharArray();
        this.seqLength = sequences.length;


    }


    public void setMotifPos(int startPos,int currentMotifLength){
        this.startMotifPos = startPos;
        this.endMotifPos = startPos + currentMotifLength -1 ; //inclusive
    }

    public void setTempPos(int startPosTemp,int currentMotifLength){
        this.tempMotifStart = startPosTemp;
        this.tempMotifEnd = startPosTemp + currentMotifLength -1 ; //inclusive
    }

    public int getStartPosition(){
        return startMotifPos;
    }

    public int getEndPosition(){
        return endMotifPos;
    }

    public int getTempStart(){
        return tempMotifStart;
    }

    public int getTempEnd(){
        return tempMotifEnd;
    }

    public char [] getCurrentMotif(int start,int end){
        
        char [] motif = Arrays.copyOfRange(this.sequences, start, end+1);// fct is exclusive, thus need to add 1
        return motif;
    }

    public char [] getMotif(){
        
        char [] motif = Arrays.copyOfRange(this.sequences, startMotifPos, endMotifPos+1);//exclusive
        return motif;
    }

    public double [] scanSequence(double [][] curPSSM,int currentMotifLength){ // this function scan through all motifs in a sequence
        
        int numOfScans = seqLength-currentMotifLength-1;
        double [] potentialMotifScores = new double [numOfScans]; // store all potential motif scores
        // the score is stored in a accumulated fashion 
    
        char [] curSeq = new char [currentMotifLength];
        int scan = 0;
        int startScan =0;
        while(scan < numOfScans){
            curSeq= this.getCurrentMotif(startScan ,startScan + currentMotifLength-1);
            startScan++;
            
            //get forward and reverse score
            double score =0;
            int i=0;
            while (i < currentMotifLength){
                
                switch(curSeq[i]){
                    case 'A': 
                    score += curPSSM[i][0]; // complementary strand A -> T, G -> C
                    break;
                    case 'T': 
                    score += curPSSM[i][1]; //the score for comp strand, score for position last become socre for position 1 for the complementary nt.
                    break;
                    case 'C': 
                    score += curPSSM[i][2]; 
                    break;
                    case 'G': 
                    score += curPSSM[i][3]; 
                    break;

                }
                i++; 
            }

            if (scan != 0) // accumulated distribution
                potentialMotifScores[scan] = potentialMotifScores[scan-1] + Math.exp(score);
            else
                potentialMotifScores[scan] = Math.exp(score);
            scan++;  
    }
    return potentialMotifScores;

}  


public void getNewMotif(double [][] curPSSM, int currentMotifLength){ // 

    int newMotifStartPos = 0; // find where the updated motif starting position
    double [] PotentialMotifScores = this.scanSequence(curPSSM,currentMotifLength); // aggregated distribution from all potential scores through scanning
    int numOfScans = PotentialMotifScores.length; 
    Random rand = new Random();
    double nextMotif = rand.nextDouble()* PotentialMotifScores[numOfScans -1];  // random score with proportion to aggregated distribution
    

    for (int i =0; i < numOfScans; i++){ // see where the random number falls in the distribution
        if (nextMotif <= PotentialMotifScores[i]){
            newMotifStartPos = i; // index of the motif score is the start pos of the scanned motif
            break;
        }
    }

    this.setTempPos(newMotifStartPos,currentMotifLength);
    

} 



}