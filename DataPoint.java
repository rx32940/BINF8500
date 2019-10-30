/*
object for an single data point
*/

public class DataPoint{

    private int clusterID; //Cluster the point belongs to
    private double [] Dimensions; //dimensions of the data point
    private int orgIndex=1000; // original index of the datapoint, for later extracting the original string from the input file for printout
    
    /*
    default constructor
    */
    public DataPoint(){
        this.clusterID=0;
        this.Dimensions=new double [0];
    }
    
    /*
    constructor
    */
    public DataPoint(int index,double [] Dimensions){
        this.clusterID=0;
        this.Dimensions = Dimensions;
        this.orgIndex = index;
        
    }
    
    /*
    get original Index 
    */
    public int getOrgIndex(){
        return this.orgIndex;
    }

    /*
    get ID the point currently belongs to
    */
    public int getClusterID(){

        return this.clusterID;
    }

     /*
    get the Dimensions of the data point
    */
    public double [] getDimensions(){
        
        return Dimensions;
    }

     /*
    assign the datapoint to a cluster
    */
    public void assignCluster(int clusterID){
        this.clusterID = clusterID;
    }

     /*
    calculate the euclidean distance between this point to the centriod the datapoint belongs to
    */
    public double distanceToCentriod(DataPoint centroid){
        
        //calculate euclidean distance to centriod
        double [] pointDim = this.getDimensions();//all dimensions of current point
        double [] cenDim = centroid.getDimensions();//all dimensions of the centroid
        double sum=0;
        
        for (int i = 0; i < pointDim.length; i++){
            sum += Math.pow(pointDim[i] - cenDim[i],2);
        }
        
        return Math.sqrt(sum);
    }


     /*
    assign a new dimension for the point, specifically for normalization step
    */
    public void setDimensions(double [] newDimension){
        this.Dimensions = newDimension;
    }
}