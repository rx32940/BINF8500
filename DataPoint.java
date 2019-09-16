public class DataPoint{

    private int clusterID;
    private int [] Dimensions;
    
    public DataPoint(){
        this.clusterID=0;
        this.Dimensions=new int [0][0];
    }

    public DataPoint(String [] Dimensions){
        // need to convert string [] to in []
    }

    public void assignCluster(int clusterID){
        this.clusterID = clusterID;
    }

    public double distanceToCentriod(DataPoint centroid){
        
        //calculate euclidean distance to centriod
        double distance;
        return distance;
    }
}