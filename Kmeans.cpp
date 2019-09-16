#include<iostream>
#include<fstream>
#include<string>

using namespace std;

int main(int argc,char **argv){
    
    string **allPoints=new string*[50];

    ifstream data(argv[1]);

    int a=0;
    if(!data){
        cout<<"Error opening data file"<<endl;
        system("pause");
        return -1;
    }

    int i =0;
    while(!data.eof()){
        getline(data,allPoints[i][a],'\n');
        cout<<allPoints[a]<<"\n";
    }
}

class DataPoint{
    private:
        double *dimensions;
        int clusterID;
    
    public:
        DataPoint(){
            double *dimension;
            clusterID=0;
        }
        DataPoint(double *dimensions){

        }
        void assignCluster(int cluster){
            this->clusterID = cluster;
        }
};
