import java.util.*;
import java.io.*;

public class Alignment{

    // should be user input
    public static int mismatch=0;
    public static int match =1;
    public static int gap=-1;
    public static int steps=0;
    public static int finalScore=0;
   
        public static void main(String [] args){
       
        // filenames should be user input
        char [] seq1  = readSeq(args[0]);
        char [] seq2 = readSeq(args[1]);
        
        match= Integer.parseInt(args[2]);
        mismatch = Integer.parseInt(args[3]);
        gap = Integer.parseInt(args[4]);

        System.out.println("Scores: \n \t Match: "  + match + ", Mismatch: " + mismatch + ", Gap: " + gap + "\n");

        // matrix keeps score
        int [][] scoreMatrix = scoreMatrix(seq1,seq2);

        finalScore = scoreMatrix[seq1.length][seq2.length];
        System.out.println("Alignment Score: " + finalScore + "\n");

        char [] traceBack = tracePath(scoreMatrix,seq1,seq2);
        writeAlignment(traceBack, seq1, seq2);
    }

    public static void writeAlignment(char [] traceBack,char [] seq1, char [] seq2){

       
        int curPos = steps -1; //curPos in steps traceBack array, backward
        String seq1Str="";
        String seq2Str="";
        String alignment="";
        int seq1Pos = 0;
        int seq2Pos =0;
        int [] rowLab1 = new int[(curPos/60)+1];
        int [] rowLab2 = new int[(curPos/60)+1];
        int rowPos = 0;
        int k=0;
     

        while(curPos != -1){
            
            if(rowPos % 60 == 0){
                rowLab1[k] = seq1Pos+1;
                rowLab2[k] = seq2Pos+1;
                k++;
            }

            //System.out.print(rowLab1[z-1] + "   "+rowLab2[z-1]);
            switch(traceBack[curPos]){
                case 'm':
                    seq1Str = seq1Str + seq1[seq1Pos++];
                    seq2Str = seq2Str + seq2[seq2Pos++];
                    rowPos++;
                    alignment = alignment + "*";
                    curPos--;
                    break;
                case 'n':
                    seq1Str = seq1Str + seq1[seq1Pos++];
                    seq2Str = seq2Str + seq2[seq2Pos++];
                    rowPos++;
                    alignment = alignment + " ";
                    curPos--;
                    break;
                case '2':
                    seq1Str = seq1Str + seq1[seq1Pos++];
                    seq2Str = seq2Str + "-";
                    rowPos++;
                    alignment = alignment + " ";
                    curPos--;
                    break;
                case '1':
                    seq1Str = seq1Str + "-";
                    seq2Str = seq2Str + seq2[seq2Pos++];
                    rowPos++;
                    alignment = alignment + " ";
                    curPos--;
                    break;
            }

            

        }

        int printPos = alignment.length();
        int rowIndex = 1;
        int begin=0;
        int end=0;
        int z=0;
        while(printPos > 0){
            if(printPos > 60){
                begin =rowIndex-1;
                end = begin + 60;
            }else{
                begin = rowIndex-1;
                end = rowIndex + printPos-1;
            }
            // System.out.println("begin:" + begin + "end:" + end);
            String rowSeq1 = seq1Str.substring(begin, end);
            String rowAlign = alignment.substring(begin, end);
            String rowSeq2 = seq2Str.substring(begin, end);
           
            System.out.println(rowLab1[z] + ":\t" + rowSeq1 + "\n" + "\t"+rowAlign + "\n" + rowLab2[z] + ":\t"  + rowSeq2 + "\n");
           // writer.write(rowIndex + ":\t" + rowSeq1 + "\n" + "\t"+rowAlign + "\n" + rowIndex + ":\t"  + rowSeq2 + "\n\n");
            z++;
            printPos -= 60;
            rowIndex = rowIndex + 60;

        }


    }

    public static char [] readSeq(String filename){
        
        String seqStr1 = "";
        char [] seq1= new char[0];

        try{
            BufferedReader reader1 = new BufferedReader(new FileReader(filename));
           
            
            String str = reader1.readLine(); // read the first line
            

            if(str.charAt(0) != '>'){ // if no header
                seqStr1 = str; // start from line 1
            }

           // read until file is null
            while((str = reader1.readLine()) != null){
                
                seqStr1= seqStr1 + str;
            }
            

            seq1 = seqStr1.toCharArray(); // str to char []
           
        reader1.close();
    
        }catch(Exception e){
            System.out.println("File not Found");
    }

    return seq1;
    }


    public static int [][] scoreMatrix(char[] seq1, char[] seq2){
        int [][] scoreMatrix = new int [seq1.length+1][seq2.length+1];
        
        scoreMatrix = initializeMatrix(seq1,seq2);

        for(int i =1; i <= seq1.length; i++){//iterate through each row, compare ith nt of seq1 with all seq2 nts
            for(int j=1; j<=seq2.length;j++){
                scoreMatrix[i][j]=score(scoreMatrix,seq1,seq2,i,j);
                
            }

        }
        

        return scoreMatrix;
    }
    
    //  function to initilize score matrix with gap score only
    public static int [][] initializeMatrix(char[] seq1, char[] seq2){
        int verLength = seq1.length; //seq1 as y axis
        int horLength = seq2.length; // seq2 as x axis
        int [][] scoreMatrix = new int [verLength+1][horLength+1];
        
        // initialize with first row and first col with gap scores
        
        if (verLength <= horLength){ // if more cols (seq2 length is longer)
            
            scoreMatrix[0][0] = 0; // match between two gaps
            
            for(int i =1; i <= horLength; i++){ // calculate gap score horzonitally
                scoreMatrix[0][i] = scoreMatrix[0][i-1] + gap; // fill in score Matrix
            }

            for(int j=1; j <= verLength; j++){ // assign cumulative gap score vertically
                scoreMatrix[j][0] = scoreMatrix[0][j];
            }
            
            
        }else if (horLength < verLength){// if more rows (seq1 length is longer)

            scoreMatrix[0][0] = 0;

            for (int i =1; i <= verLength; i++){
                scoreMatrix[i][0] = scoreMatrix[i-1][0] + gap;
            }

            for (int j =1; j <= horLength; j++){
                scoreMatrix[0][j]= scoreMatrix[j][0];
            }
        }
        

        return scoreMatrix;// return initialized matrix with gap score
    }

    public static int score(int [][] scoreMatrix,char [] seq1, char [] seq2, int x, int y){
        int up=0;
        int left=0;
        int ver=0;
        int optimal=0;

                
        up = scoreMatrix[x-1][y] + gap;// if gap in seq2, query upward
        left = scoreMatrix[x][y-1] + gap;//if gap in seq1, query left
                
        if(isMatch(seq1,seq2,x-1,y-1)) //  query vertical means aligning, -1 because seq array is 1 index smaller
            ver=scoreMatrix[x-1][y-1]+ match; // if nts match
        else
            ver=scoreMatrix[x-1][y-1] + mismatch; // if not match

        // check for max between three scores
        optimal =  Math.max(Math.max(up,left),ver);

        return optimal;

    }

    public static boolean isMatch(char [] seq1, char [] seq2, int x, int y){
        
        
        return Character.toLowerCase(seq1[x]) == Character.toLowerCase(seq2[y]); // index from scoreMatrix is 1 index larger than seq arrays because of the gap row and col added at the beginning

    }

    public static char [] tracePath(int [][] scoreMatrix, char [] seq1, char [] seq2){
        
        
        int seq1Length = scoreMatrix.length-1;
        int seq2Length = scoreMatrix[0].length-1;

        int curX = seq1Length;
        int curY = seq2Length;
        int cur = scoreMatrix[curX][curY];
        int i = 0; // current step when tracing back, in backward order
        char [] traceBack = new char [seq1Length + seq2Length];
        
    do{
    
        
        int ifGap = cur +1;
        int left = scoreMatrix[curX][curY-1];
        int up = scoreMatrix[curX-1][curY];

        int ifMismatch = cur;
        int ifMatch = cur-1;
        int ver = scoreMatrix[curX-1][curY-1];
        
        if(isMatch(seq1,seq2,curX-1,curY-1) && ver== ifMatch){//seq array 1 index lesser
            
            traceBack[i] = 'm';
            curX--;
            curY--;
            
        }else if(ver == ifMismatch){
            
            traceBack[i] = 'n';
            curX--;
            curY--;
            
        }else if(ifGap ==left){
            
            traceBack[i] = '1';
            curY--;
            
        }else if(ifGap == up){
            
            traceBack[i] ='2';
            curX--;
            
        }else{
            System.out.println("error had occur");
            break;
        }
        
        
        cur = scoreMatrix[curX][curY];
        i++;

        if(curX == 0 && curY != 0){
            while(curY != 0) {
                traceBack[i]='1';
                curY--;
                i++;
            }
            
        }else if(curY == 0 && curX != 0){
            while(curX != 0) {
                traceBack[i]='2';
                curX--;
                i++;
            }
        }

    }while(curX !=0 || curY != 0);
    
    steps = i; // total step to traceback
    return traceBack;

    }
}