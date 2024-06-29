package com.kafka.watcher.kafkawatcher.Models;

import java.util.List;

public class BootstrapServers {

    private List<Partition> isrPartitions;
    private List<Partition> leaderPartition;
    private List<Partition> replicaPartition;
    public List<Partition> getIsrPartitions() {
        return isrPartitions;
    }
    public void setIsrPartitions(List<Partition> isrPartitions) {
        this.isrPartitions = isrPartitions;
    }
    public List<Partition> getLeaderPartition() {
        return leaderPartition;
    }
    public void setLeaderPartition(List<Partition> leaderPartition) {
        this.leaderPartition = leaderPartition;
    }
    public List<Partition> getReplicaPartition() {
        return replicaPartition;
    }
    public void setReplicaPartition(List<Partition> replicaPartition) {
        this.replicaPartition = replicaPartition;
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
