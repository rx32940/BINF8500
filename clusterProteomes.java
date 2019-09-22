public class clusterProteomes{
    
    public static void main(String [] args){

        int k = Integer.parseInt(args[1]);
        Kmeans run = new Kmeans(args[0],k);
    }


    
}