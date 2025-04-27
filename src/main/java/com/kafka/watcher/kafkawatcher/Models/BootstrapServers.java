package com.kafka.watcher.kafkawatcher.Models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.unit.DataSize;

public class BootstrapServers {

    private List<Partition> isrPartitions = new ArrayList<>();
    private List<Partition> leaderPartitions = new ArrayList<>();
    private List<Partition> replicaPartitions = new ArrayList<>();


    public List<Partition> getIsrPartitions() {
        return isrPartitions;
    }
    public void setIsrPartitions(List<Partition> isrPartitions) {
        this.isrPartitions = isrPartitions;
    }
    public void addIsrPartitition(Partition isrPartition)
    {
        this.isrPartitions.add(isrPartition);
    }

    public Long getIsrPartitionsSize(){
        return DataSize.ofBytes(this.isrPartitions.stream().mapToLong(f -> f.getReplicaSize()).sum()).toGigabytes();
    }

    public void clearIsrPatitions(){
        this.isrPartitions.clear();
    }
    public List<Partition> getLeaderPartition() {
        return leaderPartitions;
    }

    public void setLeaderPartition(List<Partition> leaderPartition) {
        this.leaderPartitions = leaderPartition;
    }

    public void addLeaderPartition(Partition leadePartition)
    {
        this.leaderPartitions.add(leadePartition);
    }

    public Long getLeaderPartitionsSize(){
        return DataSize.ofBytes(this.leaderPartitions.stream().mapToLong(f -> f.getReplicaSize()).sum()).toGigabytes();
    }


    public void clearLeaderPartitions()
    {
        this.leaderPartitions.clear();
    }

    public List<Partition> getReplicaPartitions() {
        return replicaPartitions;
    }

    public void setReplicaPartition(List<Partition> replicaPartitions) {
        this.replicaPartitions = replicaPartitions;
    }

    public void addReplicaPartition(Partition replicaPartition)
    {
        this.replicaPartitions.add(replicaPartition);
    }

    public Long getReplicaPartitionsSize(){
        return DataSize.ofBytes(this.replicaPartitions.stream().mapToLong(f -> f.getReplicaSize()).sum()).toGigabytes();
    }

    public void clearReplicaPartitions(){
        this.replicaPartitions.clear();
    }

    private boolean isConnected;
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    private String ipAddress;
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getIpAndPort(){
        return this.ipAddress.toString().replace("/", "").concat(":"+getPort());
    }
    
    private int port;
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String dns;
    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    private int nodeId;
    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    private String stringNodeId;
    public String getStringNodeId() {
        return stringNodeId;
    }
    public void setStringNodeId(String stringNodeId) {
        this.stringNodeId = stringNodeId;
    }

    private String rack;
    public String getRack() {
        return rack;
    }
    public void setRack(String rack) {
        this.rack = rack;
    }
    public void setNodeId(int id,String stringNodeId,int port, List<BootstrapServerConfig> config){
        if(this.port == port) 
        {
            this.setStringNodeId(stringNodeId);
            this.setNodeId(id);
            this.setBootstrapServerConfigs(config);
        }
    }

    private String errorString;
    public String getErrorString() {
        return errorString;
    }
    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
    public Long getTotalBtyes() {
        return totalBtyes;
    }
    public void setTotalBtyes(Long totalBtyes) {
        this.totalBtyes = totalBtyes;
    }

    private Long totalBtyes;
    public Long getUsableBytes() {
        return usableBytes;
    }
    public void setUsableBytes(Long usableBytes) {
        this.usableBytes = usableBytes;
    }

    private Long usableBytes;

    private Long totalDiskUsage;
    public Long getTotalDiskUsage() {
        this.totalDiskUsage = getTotalGB() - getUsableGB();
        return this.totalDiskUsage;
    }

    private Long totalGB=Long.valueOf("0");
    public Long getTotalGB() {
        return totalGB;
    }
    public void setTotalGB(Long totalGB) {
        this.totalGB = totalGB;
    }

    private Long usableGB = Long.valueOf("0");
    public Long getUsableGB() {
        return usableGB;
    }
    public void setUsableGB(Long usableGB) {
        this.usableGB = usableGB;
    }

    private String LogDir;
    public String getLogDir() {
        return LogDir;
    }
    public void setLogDir(String logdir) {
        this.LogDir = logdir;
    }

    private List<BootstrapServerConfig> bootstrapServerConfigs;
    public List<BootstrapServerConfig> getBootstrapServerConfigs() {
        return bootstrapServerConfigs;
    }
    public void setBootstrapServerConfigs(List<BootstrapServerConfig> bootstrapServerConfigs) {
        this.bootstrapServerConfigs = bootstrapServerConfigs;
    }

}
