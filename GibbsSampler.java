import java.util.*;
import java.io.*;

public class GibbsSampler{


 
    public final static int SETS = 300;// number of sets, one set with 5 updates and 6 different adjustment checked
    public final static int SEEDS = 100;
    public final static int PLATU = 50;
    public static String [] seqNames = new String [100];
    public static Sequence [] sequences = new Sequence [100];
    public static int numSeq =0;
    public static int seqLength;
    public static int initialLength;
    public static int currentMotifLength;
    public static char [][] curBestMotifs;

    
   
    public static void main(String [] args){

        String filePath="/Users/rachel/Downloads/H.pyloriRpoN-sequences-10-300nt.fasta";
        initialLength = 22;
        currentMotifLength = initialLength;
        readSequences(filePath);
       
        double max = runSeed(); // save the first seed score as max
        int bestLength = currentMotifLength;
        double curSeedScore = max; // current seed score
        char[][] bestMotifs = getMotifs(currentMotifLength);
        int [][] bestMotifPos = getMotifPos();
        for (int i = 1 ; i < SEEDS-1; i++){
            curSeedScore = runSeed();
            
            //System.out.println("current seed score" + curSeedScore);
            //System.out.println("Current motif length" + currentMotifLength);

            if (curSeedScore > max){ // find the max score
                max = curSeedScore;
                bestLength = currentMotifLength;
               bestMotifs = getMotifs(bestLength); 
               bestMotifPos = getMotifPos(); 
               
                
            }
        }
        System.out.println("\nInput file: "+filePath.substring(filePath.lastIndexOf("/")+1) + "\nInitial Motif Length: " + initialLength);
        System.out.println("Final Motif Length: " + bestLength + "\nFinal Score: " + max + "\n");
        System.out.println("Motif sequences and locations:\n");
        printMotifs(bestMotifs,bestMotifPos);
        System.out.println("Maximum overall score" + max);
        System.out.println("Current motif length" + bestLength);
        


    
    }

    public static void printMotifs(char [][] bestMotifs,int [][] bestMotifPos){

        
        StringBuilder str = new StringBuilder();
        for (int i =0 ; i < numSeq; i++){
            str.append(bestMotifs[i]).append(" ").append(bestMotifPos[i][0]+1).append("-").append(bestMotifPos[i][1]+1).append("\t").append(seqNames[i]).append("\n").toString();
        }
        System.out.println(str);

    }

    // for print results
    public static char [][] getMotifs(int currentMotifLength){
        char [][] motifs = new char [numSeq][currentMotifLength];
        
        for (int i = 0 ; i < numSeq; i++){
            motifs[i]= sequences[i].getMotif();
            
        }
        return motifs;
    }
    public static int [][] getMotifPos(){
        
        int [][] bestMotifPos = new int [numSeq][2];
        for(int i =0; i < numSeq; i++){

            bestMotifPos[i][0] = sequences[i].getStartPosition();
            bestMotifPos[i][1] = sequences[i].getEndPosition();

        }

        return bestMotifPos;
    }

    public static double runSeed(){
        double curSeedScore = 0;
        
       initialMotifs(); // assign initial motif positions (random)
        
       // add burn in, no adjustment, just update
       // what is plateau, run to the cycle limit or reach the plateau

        // run sets numOfSets-1 times
           curSeedScore = runSet();// 5 update + 1 adjustment
        return curSeedScore;
    }

    /* this is the set will update 5 times with new motif position,
     check the score for shifting a position left and right 
     check for a motif length one nt longer and shorter
     */
    public static double runSet(){
       
        double maxScore = updateAllMotifs();
        double currentScore = maxScore;
        curBestMotifs = getMotifs(currentMotifLength);
        int nPlatu =0;
        for (int j =0 ; j < SETS && nPlatu < PLATU; j++){
          
            for(int i = 0; i < 4 && nPlatu < PLATU; i++){// do 4 update here w/o storing the final score
                
                currentScore = updateAllMotifs();
                

                if(currentScore > maxScore){
                    maxScore = currentScore;
                    curBestMotifs = getMotifs(currentMotifLength);
                    resetMaxMotif(); 
                   // System.out.println("reset the plateau");
                    nPlatu = 0;
                    continue;
                }

                resetMotifPos(); // set final pos to temp pos so update always starts from the current best location
                nPlatu ++;
            }

            double [] allPossibleMutatedScore = new double [7];
            allPossibleMutatedScore[0] = maxScore; // store the score only at fifth time as one of the possible motif versions

            // go through all possible adjustments and save their score
            for (int i = 1; i <= 6; i++){// get score for rest of the possible adjustments
                allPossibleMutatedScore[i] = adjust(i);
            }

            // find the index from the array with the max score
            int maxIndex = 0;
            for (int i = 1; i < 7 ; i++){
                if(allPossibleMutatedScore[i] > maxScore){
                    
                    maxIndex = i; // the index with highest score indicate the best scoring adjustment
                    
                }

            }
            

            /* 
            check which adjustment has the highest score
            and adjust currentMotif length along with motif position for each sequences
            set the new motif for each sequence
            */

            if (maxIndex == 0){ // if the max score stays, nPlat ++
                nPlatu ++;
               // continue;
            }

            if (maxIndex == 1){ //shift 1 left
                maxScore = allPossibleMutatedScore[1];
                
                nPlatu = 0;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition()-1, currentMotifLength);
                }

                curBestMotifs = getMotifs(currentMotifLength);
                resetMotifPos(); 
                

            }else if (maxIndex ==  2){//shift 1 right
                maxScore = allPossibleMutatedScore[2];
                nPlatu = 0;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition()+1, currentMotifLength);
                }

                curBestMotifs = getMotifs(currentMotifLength);
                resetMotifPos(); 

            }else if (maxIndex ==  3){//extend 1 right
                maxScore = allPossibleMutatedScore[3];
                nPlatu = 0;
                currentMotifLength += 1;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition(), currentMotifLength);
                }

                curBestMotifs = getMotifs(currentMotifLength);
                resetMotifPos(); 
                
            }else if (maxIndex ==  4){//extend 1 left
                maxScore = allPossibleMutatedScore[4];
                nPlatu = 0;
                currentMotifLength += 1;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition()-1, currentMotifLength);
                }
                curBestMotifs = getMotifs(currentMotifLength);
                resetMotifPos(); 
                
            }else if (maxIndex ==  5){//minus 1 right
                maxScore = allPossibleMutatedScore[5];
                nPlatu = 0;
                
                currentMotifLength += -1;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition(), currentMotifLength);
                    resetMotifPos(); 
                }

                curBestMotifs = getMotifs(currentMotifLength);
                
            }else if (maxIndex == 6){//minus 1 left
                maxScore = allPossibleMutatedScore[6];
                nPlatu = 0;

                currentMotifLength += -1;
                for (int i = 0; i < numSeq; i++){
                    sequences[i].setMotifPos(sequences[i].getStartPosition()+1, currentMotifLength);
                }

                curBestMotifs = getMotifs(currentMotifLength);
                resetMotifPos(); 
                
            }
        
           // System.out.println("current set score " + maxScore);
            //System.out.println("current motif length" + currentMotifLength);
        }
        return maxScore;
    
    }

   
    public static void readSequences(String seqFile){

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(seqFile)));
            StringBuilder curSeq = new StringBuilder();
            String curStr = "";
            
            while((curStr = reader.readLine()) != null ){
                
                if(curStr.charAt(0) != '>'){// if not a header, add to sequence string
                    curSeq.append(curStr);
                    continue;
                
                }else if(numSeq !=0){// if not the firsr header
                    
                    sequences[numSeq-1]=new Sequence (curSeq.toString());//add the seq from the last header to major array
                    seqNames[numSeq++]=curStr;//set header name to header's array
                    //to next sequence
                    curSeq=new StringBuilder();// reset the current str container
                    continue;

                }else {
                    
                    seqNames[numSeq++]=curStr;// if the first header, not need to reset nor store from last loop
                    continue;
                }  
                
            }
            sequences[numSeq-1]= new Sequence (curSeq.toString()); // last seq, because no next header check, didn't got added to the main array
            seqLength = curSeq.length();
            reader.close();
            
        }catch(Exception e){

            System.out.println("The input file is not existed.");

        }
    }

    public static void initialMotifs(){

        Random rand = new Random();
        int startPos = 0;
        for (int i =0; i < numSeq; i++){
            startPos = rand.nextInt(seqLength);
            sequences[i].setMotifPos(startPos,currentMotifLength);
            sequences[i].setTempPos(startPos, currentMotifLength);
        }

    }

    public static double log2(double value){
        return Math.log(value)/Math.log(2);
    }

    public static double [][] getPSSM(int currentSeq,int curMotifLength, int startChange, int endChange){ //currentSeq will be skipped when getting a pssm


        double [][] logScore = new double [curMotifLength][4];
        double psudocount = 0.25;
        double psudoTotal = (numSeq-1)+(psudocount*4);//add all motifs excep for the current one
        
        char [][] curMotifs = new char [numSeq-1][curMotifLength]; // store all current motifs except for the current one
        
        // extract all motifs from each sequences
        int curSeqIndex =0; // go through all sequences
        int curMotifIndex=0; // store into the motif array
        while (curSeqIndex < numSeq){
            if(curSeqIndex == currentSeq){
                curSeqIndex++;
                continue;
            }
            curMotifs[curMotifIndex] = sequences[curSeqIndex].getCurrentMotif(sequences[curSeqIndex].getTempStart() + startChange, sequences[curSeqIndex].getTempEnd()+ endChange);
            curMotifIndex++;
            curSeqIndex++;
        }


        for(int i=0; i < curMotifLength; i++){// go through each position in motifs
            double A=0;
            double T=0;
            double C=0;
            double G=0;
            
           
            for(int j = 0 ; j < numSeq-1; j++){ // skip current sequence
                
                switch(Character.toUpperCase(curMotifs[j][i])){
                    case 'A': A++; break;
                    case 'T': T++; break;
                    case 'C': C++; break;
                    case 'G': G++; break;        
                }
            }
          
           logScore[i][0] = log2(((A+psudocount)/psudoTotal)/.25);
           logScore[i][1] = log2(((T+psudocount)/psudoTotal)/.25);
           logScore[i][2] = log2(((C+psudocount)/psudoTotal)/.25);
           logScore[i][3] = log2(((G+psudocount)/psudoTotal)/.25);


        }
    
    return logScore;
    }

    public static double [][] getMaxPSSM(char [][] bestMotifs){
        
        double [][] logScore = new double [curMotifLength][4];
        double psudocount = 0.25;
        double psudoTotal = (numSeq-1)+(psudocount*4);//add all motifs excep for the current one
        
        char [][] curMotifs = new char [numSeq-1][curMotifLength]; // store all current motifs except for the current one
        
        // extract all motifs from each sequences
        int curSeqIndex =0; // go through all sequences
        int curMotifIndex=0; // store into the motif array
        while (curSeqIndex < numSeq){
            if(curSeqIndex == currentSeq){
                curSeqIndex++;
                continue;
            }
            curMotifs[curMotifIndex] = bestMotifs[curSeqIndex];
            curMotifIndex++;
            curSeqIndex++;
        }


        for(int i=0; i < curMotifLength; i++){// go through each position in motifs
            double A=0;
            double T=0;
            double C=0;
            double G=0;
            
           
            for(int j = 0 ; j < numSeq-1; j++){ // skip current sequence
                
                switch(Character.toUpperCase(curMotifs[j][i])){
                    case 'A': A++; break;
                    case 'T': T++; break;
                    case 'C': C++; break;
                    case 'G': G++; break;        
                }
            }
          
           logScore[i][0] = log2(((A+psudocount)/psudoTotal)/.25);
           logScore[i][1] = log2(((T+psudocount)/psudoTotal)/.25);
           logScore[i][2] = log2(((C+psudocount)/psudoTotal)/.25);
           logScore[i][3] = log2(((G+psudocount)/psudoTotal)/.25);


        }
    
    return logScore;

    }
    
    public static double updateAllMotifs(){ // this function returns the overall score after all motifs updated

        double overallScore = 0;
        updateNewMotif(); // where every sequence get a new motif 
        double [][] curPSSM;
        
        // calculate the overall score with the new motifs for all sequences
        int curStart;
        int curEnd;
        for (int i = 0; i < numSeq; i++){
            
            curStart=sequences[i].getTempStart();
            curEnd=sequences[i].getTempEnd();
            
            curPSSM = getPSSM(i,currentMotifLength,0,0); // get score table with the current sequences skipped
            overallScore += getCurrentScore(curPSSM,i,curStart,curEnd,currentMotifLength); // calculate score
            
        }

      //System.out.println("score for after all motif updated once: " + overallScore);

        return overallScore;
    }

    public static void updateNewMotif(){ // this function updates the new position for motif in each sequence

        double [][] curMotif;
        for (int i =0; i < numSeq; i++){
            //System.out.print("before adjustment: start: " + sequences[i].getStartPosition() + "; end: " + sequences[i].getEndPosition() + "; ");
            curMotif=getPSSM(i,currentMotifLength,0,0);
            sequences[i].getNewMotif(curMotif,currentMotifLength); // set new motif for each sequence
            //System.out.println("after adjustment: start: " + sequences[i].getStartPosition() + "; end: " + sequences[i].getEndPosition());
        }
    }

    public static void resetMaxMotif(){ // set final motif pos with the value of temp motif 
        for(int i =0 ; i < numSeq; i++){
            sequences[i].setMotifPos(sequences[i].getTempStart(), currentMotifLength);
        }
    }

    public static void resetMotifPos(){// if temp motif does not give a better score reset the temp with previous best motif for next update
        for(int i = 0 ; i < numSeq ; i ++){
            sequences[i].setTempPos(sequences[i].getStartPosition(), currentMotifLength);
        }

    }

    /*
    This function returns the overall score when all the motifs for each sequence are updated
    */
    public static double getCurrentScore(double [][] currentPSSM, int curMotifIndex, int start, int end,int curMotifLength){

       
        char [] curMotif = sequences[curMotifIndex].getCurrentMotif(start, end);
        double curMotifScore = 0;
            
        for (int j =0; j < curMotifLength; j++){
                
                switch(Character.toUpperCase(curMotif[j])){
                    case 'A': 
                    curMotifScore += currentPSSM[j][0]; 
                    break;
                    case 'T': 
                    curMotifScore += currentPSSM[j][1]; 
                    break;
                    case 'C': 
                    curMotifScore += currentPSSM[j][2]; 
                    break;
                    case 'G': 
                    curMotifScore += currentPSSM[j][3]; 
                    break;

                }
        }

        
        return curMotifScore;

    }

    /*
    PSSM for check shifted and mutated motifs
    */
    public static double adjust(int changeOption){
       
        double updateScore = 0; // if cannot update make sure this adjustment has low score
        boolean canUpdate = true;
        int startChange=0;
        int endChange=0;
        int updateMotifLength = currentMotifLength;
        
        /*
        choose which possible adjustment the motif can change
        */
        if (changeOption == 1){//if motif shift one nt left
           startChange = -1;
           endChange = -1;
       }else if (changeOption == 2){//if motif shift one nt right
           startChange = 1;
           endChange = 1;
       }else if (changeOption == 3){//extend 1 nt from right
           startChange = 0;
           endChange=1;
           updateMotifLength++;
       }else if (changeOption ==4){//extend 1 from left
            startChange = -1;
            endChange = 0;
            updateMotifLength++;
       }else if (changeOption == 5){//shorten 1 nt from right
            startChange = 0;
            endChange = -1;
            updateMotifLength--;
       }else if (changeOption == 6){//shorten 1 nt from left
            startChange = 1;
            endChange =0;
            updateMotifLength--;
       }

       
       int curMotifStartPos; // the start position of motif from the current sequence 
        int curMotifEndPos; // the start position of motif from the current sequence

    if(changeOption == 1 || changeOption == 2 || changeOption == 3 || changeOption == 4){
       for (int i = 0; i < numSeq; i ++){// go through all sequences
           curMotifStartPos = sequences[i].getStartPosition();
            curMotifEndPos = sequences[i].getEndPosition();

            if (curMotifStartPos == 0){// if the current motif start with beginning of the sequence
                if (changeOption == 1 || changeOption == 4){ // then motif cannot adjust by shifting to the left nor extend from left
                    //updateScore = -1000; // set update score to -1
                    canUpdate = false;
                    break; // no need to set pssm
                }
            }else if (curMotifEndPos == seqLength-1){ // if current motif end with end of the sequence
                if (changeOption == 2 || changeOption == 3){// motif cannot adjust by shifting right nor extend from right 
                    //updateScore = -1000;
                    canUpdate = false;
                    break; // this adjustment is not applicable to this motif
                }
            }
       }
    }    
       if (canUpdate == true){// if the start or end position allow the current adjustment option
            /*
            get PSSM and score for each adjustment for with all seq excep for current one
            */
            updateScore = 0; // if can start update, add update score from 0
            int curStart; // the start position of motif from the current sequence 
            int curEnd; // the start position of motif from the current sequence
            double [][] curPssm;
            for(int i = 0; i < numSeq; i++){// get overall updated score
                curStart = sequences[i].getStartPosition()+startChange;
                curEnd = sequences[i].getEndPosition()+ endChange;

                curPssm = getPSSM(i,updateMotifLength,startChange,endChange); // each sequence need a pssm exclude it self for score
                updateScore += getCurrentScore(curPssm, i, curStart, curEnd,updateMotifLength); // calculate score with current pssm
            }
            

    }
   // System.out.println("score after " + changeOption +  " adjustment been checked" + updateScore);
    return updateScore;
    
}
  
}
