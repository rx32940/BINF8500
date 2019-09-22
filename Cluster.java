/*
object for a cluster
*/

public class Cluster{

    int ClusterID;//Cluster ID
    int numOfPts;//number of points currently in the cluster
    DataPoint [] ptsInCluster = new DataPoint [1000]; // array keep info of the points in the cluster
    DataPoint centriod;// centriod of the current cluster

    /*
    default constructor
    */
    public Cluster(){
        this.ClusterID = 0;
        this.numOfPts = 0;
        this.centriod = new DataPoint();
    }

    /*
    constructor, this constructor is for update()
    new Centriod is assigned, which is not part of the allPoints 
    */
    public Cluster(int ID,DataPoint newCen){
        this.ClusterID = ID;
        this.numOfPts =0;
        this.centriod = newCen;
    }

    /*
    add new point to the cluster
    */
    public void addPoints(DataPoint newPt){
        
        this.ptsInCluster[numOfPts] = newPt;
        numOfPts++;

    }

    /*
    assign centriod to an default cluster object, only for initialization 
    */
    public void assignCentriod(DataPoint assignedCen){
        
        this.centriod = assignedCen;
    
    }

    /*
    get the centriod of the current cluster
    */

    public DataPoint getCentriod(){
        return this.centriod;
    }

    /*
    get the size of the cluster
    */
    public int getSize(){
        return this.numOfPts;
    }

    /*
    get the ID of the cluster
    */
    public int getClusterID(){
        return this.ClusterID;
    }

    /*
    get the points in the cluster
    */
    public DataPoint [] getPoints(){
        return this.ptsInCluster;
    }
    
    /*
    check if the cluster is empty, this prevents empty cluster created after initialization and assignment
    */
    public boolean isEmpty(){
        
        return this.getSize() == 0;
    }
    

}