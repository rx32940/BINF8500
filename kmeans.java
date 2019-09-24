import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Kmeans{
    
    private static DataPoint [] allPoints = new DataPoint[1000]; //array of all datapoint objects
    private static String [] allStrings=new String [1000]; //String to keep the original string file format for later print
    private static int k=1; // num of clusters
    private static Cluster [] allClusters= new Cluster[k]; // initial and final Cluster after each clustering step
    private static int numOfDataPoints=0;
    private static int numOfDimensions=0;

    public static void main(String [] args){
        
       
        //for testing purpose
        // k=2;
        // String inputFile = "/Users/rachel/Downloads/kmeans/data/Archaea.txt"; // input
        // String outputFile = inputFile.substring(0,inputFile.indexOf(".")) + "_k" + k + ".txt"; //output
        String inputFile = args[1]; // input
        int endKRange=Integer.parseInt(args[0]); 
        readFile(inputFile); //read file

        System.out.println("k \t\t WCSS \t\t AIC \t\t BIC"); // print out header for stdout 
        for(int j =1; j <= endKRange; j++){ // run k's from 1-k (user input)
            k=j; // assign value to current k
        
            String outputFile = inputFile.substring(0,inputFile.indexOf(".")) + "_k" + k + ".txt"; //output

            
            double trialsForEachK = 300 * Math.pow(k,1.5);// different number of trials need to run for different k 
            int numOfTrials= (int) trialsForEachK;//num of Kmeans ran for the minimum wcss

            double [] allWCSS = new double [numOfTrials]; // keep the all WCSS from all trials
            Cluster [][] clusterFromTrials = new Cluster [numOfTrials][k]; // keeps all resulting clusters from trials ran
            
            
            // keep all trials WCSS in an array to find the min
            for (int i =0; i < numOfTrials; i++) {
                allWCSS[i] = runKmeans(); // run kmeans
                clusterFromTrials[i]=allClusters; // current version of allClusters saved into clusterFromTrials
            }

            // find the min WCSS and the specific kmeans trial number
            double minWCSS = allWCSS[0]; 
            int indexMin =0;
            Cluster [] minClusters = new Cluster [k];
            for (int i =0; i < numOfTrials; i++){
            if(allWCSS[i] < minWCSS){
                    minWCSS = allWCSS[k];
                    indexMin =i;
            }
            }
            
        
            minClusters = clusterFromTrials[indexMin]; // get the clusters from the trial with the minimum WCSS
            
            writeClusters(k,minWCSS,minClusters,outputFile);//write the cluster into file
        }
    }

    // run kmeans
    public static double runKmeans() {   
        
        allClusters = new Cluster [k];
        
        // initialize all clusters
        for (int i =0; i < k; i++){
            allClusters[i] = new Cluster();
        }

        
        normalize(allPoints); // normalize data points to 0-1 
        
        
        double wcss = 0; 
        boolean noEmptyCluster = true;
        
        // re-initialize and assign clusters to ensure to empty clusters
        do{
                wcss=0;
                initialize(allPoints); // assign each cluster a centriod
                wcss = assign(allClusters); // // assign all data points to each cluster and return the wcss
                

            //check if there is empty cluster
            noEmptyCluster = true; 
            for (int i =0; i < k; i++){
                if(allClusters[i].isEmpty()==true) // if any clusters is empty
                    noEmptyCluster = false; // there is an empty cluster
                
            }
        }while(noEmptyCluster != true);


        double temp =0;
        while (temp != wcss){ //when centriod stops updating
         
            wcss =temp;
            temp=update();
         
        }
     
        return temp; // the result converges
    }    

    /*
    read the file
    */
    public static void readFile(String fileName){
        try{
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            int i =0; 
            
            String current="";
            while ((current = reader.readLine()) != null){

                    if(i!=0){
                        allStrings[i-1]=current; //data table with row names

                        // obj point takes in a double array as the dimesions by converting string input to double array
                        allPoints[i-1] = new DataPoint(i-1,Arrays.stream(allStrings[i-1].substring(allStrings[i-1].indexOf("\t")+1).split("\t")).mapToDouble(Double::parseDouble).toArray()); 
                        
                        numOfDataPoints++; //num of data points
                    }
                
                i++;
            }

            // num of dimensions for each data point
            numOfDimensions = allPoints[0].getDimensions().length;
            reader.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
        

    }

    /*
    write output file
    */
    public static void writeClusters(int numOfClusters,double minWCSS,Cluster [] finalCluster,String outputFile){

        try{

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            double AIC =getAIC(minWCSS);
            double BIC =getBIC(minWCSS);

            writer.write("K = " + numOfClusters + "\n\n");
            writer.write("WCSS = " + minWCSS + "\n");
            writer.write("AIC = " + AIC + "\n");
            writer.write("BIC = " + BIC + "\n\n\n");

            for(int i =0; i< k; i ++){ // cluster
                int clusterNum = i+1;
                writer.write("Data points in Cluster " + clusterNum + ": \n\n");
                for(int j =0; j < finalCluster[i].getSize();j++){ //points in cluster
                    writer.write(allStrings[finalCluster[i].getPoints()[j].getOrgIndex()] + "\n"); // extract their original string with their original index
                }
                writer.write("\n\n\n\n" );
            }        

            System.out.println(numOfClusters + "\t" + minWCSS + "\t" + AIC + "\t"  + BIC);
            writer.close();
        
        }catch(IOException e){
            e.printStackTrace();
        }
        
        

    }

    /*
    normaliza data to 0-1
    */
    public static void normalize(DataPoint [] points){

        double [] mean = mean(allPoints);
        double [] std = std(mean,allPoints);
        double [][] normData = new double [numOfDataPoints][numOfDimensions];// temp storage of normalized data

        //normalize each datapoints column by column
        for (int i = 0; i < numOfDimensions; i++){
            for(int j =0; j < numOfDataPoints; j++){
                normData[j][i] =(allPoints[j].getDimensions()[i] - mean[i])/std[i];
                //System.out.println(allPoints[j].getDimensions()[i] + "     " + mean[i] + "     "+ std[i]);
            }
        }
        

        // assign new Dimensions to each datapoint by object
        for (int i =0; i < numOfDataPoints; i++){
            allPoints[i].setDimensions(normData[i]);
        }
        
        
    }

    //find mean of all datapoints for each dimension (column)
    private static double [] mean(DataPoint [] points){

        double sum = 0;
        double [] mean = new double [numOfDimensions];

        //find mean
        for(int i =0; i < numOfDimensions;i++){
            sum=0;
            for(int j=0; j < numOfDataPoints;j++){
                sum += points[j].getDimensions()[i];
            }
        
            mean[i] = sum/numOfDataPoints;
            
        }


        
        return mean;

    }

    //find std of all datapoints for each dimension (column)
    private static double [] std(double [] mean, DataPoint [] points){
        double [] std = new double [numOfDimensions];

        // std
        double sumDiff =0;//sum of different between each points within a dimension(column) and the mean
        double [] meanDiff = new double [numOfDimensions]; //find the mean of the differences for each dimension
        for (int i =0; i<numOfDimensions; i++){
            sumDiff=0;
            for(int j =0; j< numOfDataPoints;j++){
               sumDiff += Math.pow(points[j].getDimensions()[i] - mean[i],2);
            }
                
            meanDiff[i] = sumDiff/numOfDataPoints;
            std[i] = Math.sqrt(meanDiff[i]); //std
        }


        return std;

    }

    public static void initialize(DataPoint [] points){
           
        //randomly choose a datapoint as the first centroid
           Random random = new Random();
           allClusters[0].assignCentriod(allPoints[random.nextInt(numOfDataPoints)]); //assign centriod to first cluster
           
        //kmeans++ initialization

        for (int z =1 ; z < k; z++){

            double [] DistToCenSq  = new double [numOfDataPoints];

            // creating weight distribution for squared dist, each number is the cumulative sum of the previous numbers
            DistToCenSq[0] = Math.pow(allPoints[0].distanceToCentriod(allClusters[z-1].getCentriod()),2);
            for(int i =1 ; i < numOfDataPoints; i++){
                DistToCenSq[i] += DistToCenSq[i-1] + Math.pow(allPoints[i].distanceToCentriod(allClusters[z-1].getCentriod()),2);
                //System.out.println(DistToCenSq[i]);
            }
            
            //cumulated squared distances 
            double sumDis = DistToCenSq[numOfDataPoints-1];
            double newRand = random.nextDouble()*sumDis; //generate random number to match the data point corresponded interval in the weighted distribution.

            int indexOfDataPoint=0;
            //loop through the aggrated distribution of the squared dist
            for(int i =0; i < numOfDataPoints; i++){
                // if the rand value falls between the interval that belongs to the a certain datapoint
                if(newRand < DistToCenSq[i]){
                    indexOfDataPoint =i; // index of the data point
                    break; // escape out of the loop
                }
            }

            allClusters[z].assignCentriod(allPoints[indexOfDataPoint]);


        }

    }

    /*
    assign points to each cluster base on its distances from centriod of each cluster
    returns wcss after assigning
    */

    public static double assign(Cluster [] newClusters){

        double wcss =0;

        double minDis;
        int nearestClusterID=0; // cluster ID with the closest centriod
        double curDis;
        for(int i =0; i < numOfDataPoints; i ++){ // allPoints
            minDis = allPoints[i].distanceToCentriod(newClusters[0].getCentriod()); // distances to first centriod
            nearestClusterID =0;// id of first cluster
            for (int j =1; j < k; j++){ // compare to distances from centriods of other clusters
                curDis = allPoints[i].distanceToCentriod(newClusters[j].getCentriod()); 
                if(curDis < minDis){ // find the minimum distances out of  centriods of all clusters
                    minDis = curDis;
                    nearestClusterID=j; // keep track of the cluster ID of the closest centriod
                }
                    
            }
            newClusters[nearestClusterID].addPoints(allPoints[i]);//add current point to the cluster with the nearestClusterI
            allPoints[i].assignCluster(nearestClusterID);//assign current point obj cluster id
            wcss += Math.pow(minDis,2);// find wcss
        }

        allClusters=newClusters; // assign cluster passed in to the global variable
        return wcss; // this conclude one clustering behavior by forming a k new sets of clusters
    }

    /*
    update new centriod dimension with the average of all datapoints in the current cluster
    */
    public static double update(){

        DataPoint [] ptsInCluster;
        double [][] newCenDim= new double[k][numOfDimensions];// dimensions of the new centriods for all clusters
        double sumAllPts =0; // sum of one dimension of all points in a cluster 
        int clusterSize =0;
        
        for(int i =0; i < k; i++){// iterate through all clusters
            ptsInCluster = allClusters[i].getPoints();
            
            clusterSize = allClusters[i].getSize();
            
                //newCenDim[i]= new double[numOfDimensions]; //maybe not necessary?
                for(int j =0; j < numOfDimensions ; j++){//iterate through each dimensiomn
                    
                    sumAllPts=0; // clear for adding up this dimensions from all points in the cluster
                
                    for(int k=0; k < clusterSize; k++){// iterate through each point in the cluster
                        sumAllPts += ptsInCluster[k].getDimensions()[j];
                    }
                    newCenDim[i][j]=sumAllPts/clusterSize; // find the average of the dimension from all points
                }

        }
        
        Cluster [] newClusters = new Cluster [k]; // use new cluster arrays instead of keep updating one

        for(int i =0; i < k; i++){ // initiating new clusters with updated centriod
            newClusters[i] = new Cluster(i,new DataPoint(1000,newCenDim[i]));// the new cen is the average of the points in the cluster, no orgIndex, thus set to 1000
        }
        
       
        
        return assign(newClusters);// pass in the new clusters with new centriods to point assignment step

    }
    /*
    get AIC
    */
    public static double getAIC(double minWCSS){
        return 2*k*numOfDimensions + minWCSS;
    }

    /*
    get BIC
    */
    public static double getBIC(double minWCSS){
        return Math.log(numOfDataPoints) * k * numOfDimensions + minWCSS;
    }
}