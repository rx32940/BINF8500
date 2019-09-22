library(ggplot2)
library(tidyr)
library(scales)
setwd("/Users/rachel/Downloads/kmeans/kmeans_results")

archaea_files <- list.files("./Archaea/")
bacteria_files <- list.files("./Bacteria/")
BA_files <- list.files("./B+A/")


# do this loop three times with files for different dataset
i <- 1
for (file in archaea_files){ # change *_files for different dataset analysis 
 
  filename <- paste("./Archaea/",file,sep="")
  options(digits = 7)
  current_line <- readLines(filename,n=5)
  WCSS <- as.double(unlist(strsplit(current_line[3]," "))[3])
  AIC <- as.double(unlist(strsplit(current_line[4]," "))[3])
  BIC <- as.double(unlist(strsplit(current_line[5]," "))[3])
  stats <- data.frame(k=as.integer(i),WCSS=WCSS,AIC=AIC,BIC=BIC)
  if(i==1){
    archaea_stats <- stats
  } else
  {
    archaea_stats<-rbind(archaea_stats,stats)
  }
  
  i<- i+1
}


i <- 1
for (file in bacteria_files){ # change *_files for different dataset analysis 
  
  filename <- paste("./Bacteria/",file,sep="")
  options(digits = 7)
  current_line <- readLines(filename,n=5)
  WCSS <- as.double(unlist(strsplit(current_line[3]," "))[3])
  AIC <- as.double(unlist(strsplit(current_line[4]," "))[3])
  BIC <- as.double(unlist(strsplit(current_line[5]," "))[3])
  stats <- data.frame(k=as.integer(i),WCSS=WCSS,AIC=AIC,BIC=BIC)
  if(i==1){
    bacteria_stats <- stats
  } else
  {
    bacteria_stats<-rbind(bacteria_stats,stats)
  }
  
  i<- i+1
}

# do this loop three times with files for different dataset
i <- 1
for (file in BA_files){ # change *_files for different dataset analysis 
  
  filename <- paste("./B+A/",file,sep="")
  options(digits = 7)
  current_line <- readLines(filename,n=5)
  WCSS <- as.double(unlist(strsplit(current_line[3]," "))[3])
  AIC <- as.double(unlist(strsplit(current_line[4]," "))[3])
  BIC <- as.double(unlist(strsplit(current_line[5]," "))[3])
  stats <- data.frame(k=as.integer(i),WCSS=WCSS,AIC=AIC,BIC=BIC)
  if(i==1){
    ba_stats <- stats
  } else
  {
    ba_stats<-rbind(ba_stats,stats)
  }
  
  i<- i+1
}

# save all_stats to each of this variables separately after loop
archaea_stats <- gather(archaea_stats,"WCSS","AIC","BIC",key="stats",value="values") 
bacteria_stats <- gather(bacteria_stats,"WCSS","AIC","BIC",key="stats",value="values")
ba_stats <- gather(ba_stats,"WCSS","AIC","BIC",key="stats",value="values")

archaea_plot <- ggplot(data=archaea_stats,aes(x=k,y=values))+
  geom_line(aes(color=stats))+
  geom_point(aes(color=stats)) +
  scale_x_continuous(breaks=archaea_stats$k)+
  ggtitle("Archaea WCSS/AIC/BIC")
archaea_plot
ggsave("./archaea_plot.png")

bacteria_plot <- ggplot(data=bacteria_stats,aes(x=k,y=values))+
  geom_line(aes(color=stats))+
  geom_point(aes(color=stats)) +
  scale_x_continuous(breaks=archaea_stats$k)+
  ggtitle("Bacteria WCSS/AIC/BIC")
bacteria_plot
ggsave("./bacteria_plot.png")

ba_plot <- ggplot(data=ba_stats,aes(x=k,y=values))+
  geom_line(aes(color=stats))+
  geom_point(aes(color=stats))+
  scale_x_continuous(breaks=archaea_stats$k)+
  ggtitle("Bacteria + Archaea WCSS/AIC/BIC")
ba_plot
ggsave("./ba_plot.png")
