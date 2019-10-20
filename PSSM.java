import java.io.*;
import java.util.*;

import javax.lang.model.util.Elements.Origin;

public class PSSM{

    public static int trainMotifNum =0;
    public static int motifLength;
    public static char [][] motifs = new char [100][10];
    public static char [] sequences = {'A'};
    public static double [][] scoreTable;
    public static double [][] reverseScoreTable;
    public static double [][] numFreq;
    public static double [][] probFreq;
    public static int numNT=0;
    public static double GC =0;
    public static double cutoff;
    public static double psudocount =0.25;

    


    public static void main(String [] args){
        
        double start = System.nanoTime();
        readFile(args[0],args[1]);
        double end = System.nanoTime();

        
        
        
        double start1 = System.nanoTime();
        scoreTable = getScoreTable();
        double end1 = System.nanoTime();
        

        double start2 = System.nanoTime();
        printPSSM();
        double end2 = System.nanoTime();
        


        cutoff = Double.parseDouble(args[2]);
        System.out.println("\n Matches with score " + cutoff + " or higher found in " +  args[1].substring(args[1].lastIndexOf("/")+1) +  " (length "+ sequences.length + " bp\n");
        double start3 = System.nanoTime();
        findMotif();
        double end3 = System.nanoTime();
        
        
        System.out.println("\nTime to read file:" + (end-start)/Math.pow(10,9));
        System.out.println("Time to get score table:" + (end1-start1)/Math.pow(10,9));
        System.out.println("Time to printPSSM:" + (end2-start2)/Math.pow(10,9));
        System.out.println("Time to get motifs:" + (end3-start3)/Math.pow(10,9));
    }

    public static void readFile(String  motifFile, String seqFile){

        int i = 0;
        char [][] newMotifs= new char [100][];
        char [] newSeq = null;
        double GCcount=0;
        
        try {
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(motifFile)));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(seqFile)));
        
        // read motif sequences
        String curStr = "";
        
        while ((curStr = reader1.readLine()) != null){

                if (curStr.charAt(0) == '>' ){//check for fasta file headers
                    continue;
                }

                newMotifs[i++] = curStr.toCharArray();

               
            }
            
            reader1.close();
            
           
            // read seq file
            
            //check for fasta file header
            if((char)(reader2.read()) == '>'){
                reader2.readLine();
            }
            else{//if no header, move pointer back to the beg of the file
                reader2.reset();
            }
            
            int curValue;
            char curChar;
            int seqPos = 0;
            String seqStr = "";
            StringBuilder strbuilder = new StringBuilder(seqStr);
            while((curValue=reader2.read())!=-1){
                
                curChar = (char)curValue;

                if(curChar != '\n'){
                    strbuilder. append((char)curChar);
                    if(curChar == 'G' || curChar == 'C')
                        GCcount++;
                }

                

            }

            
            newSeq = strbuilder.toString().toCharArray();
            numNT = newSeq.length;
                
            reader2.close();
        
        } catch(Exception e){
            System.out.println(e);
        }
            
            GC = GCcount/numNT;
            trainMotifNum = i; //total length of the training dataset
            motifs = newMotifs;
            sequences = newSeq;

    }



    public static double [][] getScoreTable(){
        motifLength = motifs[0].length;
      
        numFreq = new double [motifLength][4];
        probFreq = new double [motifLength][4];
        double [][] logScore = new double [motifLength][4];
        
        

        for(int i=0; i < motifLength; i++){
            double A=0;
            double T=0;
            double C=0;
            double G=0;
            double ATSingle=(1-GC)/2; //A/T estimated percentage through GC content
            double GCSingle=GC/2;
            double psudoTotal = trainMotifNum+psudocount*4;//motif length with psudocount added
            for(int j = 0 ; j < trainMotifNum; j++){
                switch(motifs[j][i]){
                    case 'a': A++; break;
                    case 't': T++; break;
                    case 'c': C++; break;
                    case 'g': G++; break;        
                }
            }
          

            // freqency table
            numFreq[i][0]=A;
            numFreq[i][1]=T;
            numFreq[i][2]=C;
            numFreq[i][3]=G;

            probFreq[i][0]= (A+psudocount)/psudoTotal;
            probFreq[i][1]= (T+psudocount)/psudoTotal;
            probFreq[i][2]= (C+psudocount)/psudoTotal;
            probFreq[i][3]= (G+psudocount)/psudoTotal;
            

           logScore[i][0] = log2((probFreq[i][0])/ATSingle);
           logScore[i][1] = log2((probFreq[i][1])/ATSingle);
           logScore[i][2] = log2((probFreq[i][2])/GCSingle);
           logScore[i][3] = log2((probFreq[i][3])/GCSingle);


        }
    return logScore;
    }

    public static void printPSSM(){
        
        System.out.println("frequency matrix: \n pos.\tA\tT\tC\tG");
        for (int i =0; i < motifLength; i++){
            int pos = i+1;
            StringBuilder str = new StringBuilder();
            System.out.println(str.append(pos).append("\t").append((int)numFreq[i][0]).append("\t").append((int)numFreq[i][1]).append("\t").append((int)numFreq[i][2]).append("\t").append((int)numFreq[i][3]).toString()); 
        }

        System.out.println("\nprobability matrix: \n pos.\tA\tT\tC\tG");
        for (int i =0; i < motifLength; i++){
            int pos = i+1;
            StringBuilder str = new StringBuilder();
            System.out.println(str.append(pos).append("\t").append(String.format("%.3f", probFreq[i][0])).append("\t").append(String.format("%.3f", probFreq[i][0])).append("\t").append(String.format("%.3f", probFreq[i][0])).append("\t").append(String.format("%.3f", probFreq[i][0])).toString()); 
        }

        System.out.println("\nPSSM: \n pos.\tA\tT\tC\tG");
        for (int i =0; i < motifLength; i++){
            int pos = i+1;
            StringBuilder str = new StringBuilder();
            System.out.println(str.append(pos).append("\t").append(String.format("%.3f", scoreTable[i][0])).append("\t").append(String.format("%.3f", scoreTable[i][0])).append("\t").append(String.format("%.3f", scoreTable[i][0])).append("\t").append(String.format("%.3f", probFreq[i][0])).toString()); 
        }

        System.out.println("\nTraining set motif score:");
        int i=0;
        while(i < trainMotifNum){
            StringBuilder str = new StringBuilder();
            double score =0;   
            int j=0;
            char [] curMotif = new char[motifLength];
            while (j < motifLength){
                char upper = Character.toUpperCase(motifs[i][j]);
                switch(upper){
                    case 'A': 
                    score += scoreTable[j][0]; 
                    break;
                    case 'T': 
                    score += scoreTable[j][1]; 
                    break;
                    case 'C': 
                    score += scoreTable[j][2]; 
                    break;
                    case 'G': 
                    score += scoreTable[j][3]; 
                    break;

                }
                curMotif[j]=upper;
                
                j++; 
            }
            System.out.println(str.append(curMotif).append("\t").append(String.format("%.3f",score)).toString());
            i++;
        }
    }

    public static double log2(double value){
        return Math.log(value)/Math.log(2);
    }

    public static void findMotif(){
        int start =0;
        int endLoop = numNT-motifLength;
        char [] curSeq = new char [motifLength];
        //double curScore=0;
        while(start < endLoop){
            System.arraycopy(sequences, start,curSeq,0,motifLength);
            
            //get forward and reverse score
            double score =0;
            double reverseScore =0;
            int i=0;
            int reversePos = 0;
            while (i < motifLength){
                reversePos = motifLength-1-i;
                switch(curSeq[i]){
                    case 'A': 
                    score += scoreTable[i][0]; reverseScore += scoreTable[reversePos][1];// complementary strand A -> T, G -> C
                    break;
                    case 'T': 
                    score += scoreTable[i][1]; reverseScore += scoreTable[reversePos][0];//the score for comp strand, score for position last become socre for position 1 for the complementary nt.
                    break;
                    case 'C': 
                    score += scoreTable[i][2]; reverseScore += scoreTable[reversePos][3];
                    break;
                    case 'G': 
                    score += scoreTable[i][3]; reverseScore += scoreTable[reversePos][2];
                    break;


                }
                i++; 
            }

            
            StringBuilder str = new StringBuilder();
            StringBuilder str2 = new StringBuilder();
            
            if(score >= cutoff)
                System.out.println(str.append(start+1).append("\t").append(start+motifLength).append("\t").append("+\t").append(curSeq).append("\t").append(String.format("%.3f", score)).toString());
            
            if(reverseScore >= cutoff)
                System.out.println(str2.append(start+1).append("\t").append(start+motifLength).append("\t").append("-\t").append(curSeq).append("\t").append(String.format("%.3f", reverseScore)).toString());

            start++;
        }
    }
    
}
