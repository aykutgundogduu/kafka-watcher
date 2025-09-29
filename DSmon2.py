
# Script to fetch JDBC Data Source Runtime MBean statistics via WLST for multiple datasources and log results to a file

import time
import os
from java.util import Date
from java.text import SimpleDateFormat

# Connection details (replace with your values)
serverUrl = 't3://192.168.1.31:7003'  # e.g., t3://localhost:7001
username = 'weblogic'             # e.g., weblogic
password = 'Welcome1'             # e.g., welcome1
# List of datasource names to monitor (add as many as needed)
datasourcesToMonitor = ['WLSSchemaDataSource', 'LocalSvcTblDataSource', 'opss-data-source','SOALocalTxDataSource']  # e.g., ['myDataSource1', 'myDataSource2']
outputDir = 'd:\\Doc\\Work\\Allianz\\scripts'         # e.g., /tmp/jdbc_stats.log
monitoringInterval = 10                   # Interval in seconds between checks (adjust as needed)
monitoringDuration = 600                 # Total duration in seconds (e.g., 2 hours, adjust as needed)

# Function to get current timestamp using Java classes
def getTimestamp():
    date = Date()
    formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(date)

# Function to get current time in seconds (equivalent to time.time())
def currentTimeSeconds():
    return System.currentTimeMillis() / 1000

# Function to sleep for a specified number of seconds (using Java Thread.sleep)
def sleepSeconds(seconds):
    java.lang.Thread.sleep(seconds * 1000)

# Function to write to log file (compatible with Jython 2.6)
def writeToLog(filePath, message):
    f = None
    try:
        f = open(filePath, 'a')
        f.write(message + '\n')
    finally:
        if f is not None:
            f.close()

# Validate and normalize output directory for Windows compatibility
if not os.path.exists(outputDir):
    print 'Directory ' + outputDir + ' does not exist. Attempting to create it...'
    try:
        os.makedirs(outputDir)
        print 'Directory created successfully.'
    except Exception, e:
        print 'Failed to create directory ' + outputDir + '. Error: ' + str(e)
        print 'Please ensure you have permissions to create directories in this location.'
        print 'Alternatively, specify a different output directory path where you have write access (e.g., C:\\Temp).'
        outputDir = raw_input('Enter a new output directory path: ')
        if not os.path.exists(outputDir):
            print 'New directory ' + outputDir + ' does not exist. Please create it manually or choose another path.'
            exit()

# Connect to the WebLogic Server
print 'Connecting to WebLogic Server at ' + serverUrl
connect(username, password, serverUrl)

# Navigate to the runtime MBean tree to get server name
serverRuntime()
serverName = get('Name')
print 'Connected to server: ' + serverName

# Dictionary to store last logged data for each datasource to avoid duplicates
lastLogData = {}
# Dictionary to store log file paths for accessible datasources
logFiles = {}
# List of accessible datasources
accessibleDatasources = []

# Check accessibility of each datasource and initialize log files only for accessible ones
for datasourceName in datasourcesToMonitor:
    jdbcService = getMBean('/JDBCServiceRuntime/' + serverName + '/JDBCDataSourceRuntimeMBeans/' + datasourceName)
    if jdbcService is None:
        print getTimestamp() + ' | Error: Could not find DataSource ' + datasourceName + ' on server ' + serverName + '. Skipping...'
    else:
        accessibleDatasources.append(datasourceName)
        # Form log file name based on server name and datasource name (replace invalid characters for filenames)
        safeDsName = datasourceName.replace(':', '_').replace('/', '_').replace('\\', '_')
        safeServerName = serverName.replace(':', '_').replace('/', '_').replace('\\', '_')
        logFile = os.path.join(outputDir, safeServerName + '_' + safeDsName + '_stats.log')
        logFiles[datasourceName] = logFile
        lastLogData[datasourceName] = ''
        # Write header to log file
        writeToLog(logFile, '=== JDBC Data Source Monitoring for ' + datasourceName + ' on ' + serverName + ' Started at ' + getTimestamp() + ' ===')
        writeToLog(logFile, 'Timestamp           | Active Current | Active High | Total Conn | Current Capacity | Highest Available | Waiting')
        writeToLog(logFile, '---------------------------------------------------------------------------------------------')
        print 'Logging stats for ' + datasourceName + ' to ' + logFile

# Check if there are any accessible datasources to monitor
if not accessibleDatasources:
    print getTimestamp() + ' | Error: No accessible datasources found on server ' + serverName + '. Exiting...'
    disconnect()
    exit()

# Calculate end time for monitoring
startTime = currentTimeSeconds()
endTime = startTime + monitoringDuration

# Loop to monitor statistics until duration is reached
while currentTimeSeconds() < endTime:
    try:
        # Navigate to the runtime MBean tree
        serverRuntime()
        
        # Loop through only accessible datasources to collect stats
        for datasourceName in accessibleDatasources:
            # Get log file for this datasource
            logFile = logFiles[datasourceName]
            
            # Get the JDBC Service Runtime MBean for the current datasource
            jdbcService = getMBean('/JDBCServiceRuntime/' + serverName + '/JDBCDataSourceRuntimeMBeans/' + datasourceName)
            
            # Prepare log message starting with timestamp
            logMessage = getTimestamp()
            
            if jdbcService is None:
                # If datasource becomes unavailable during monitoring, log an error
                logMessage = logMessage + ' | ' + 'N/A'.rjust(14) + ' | ' + '-'.rjust(11) + ' | ' + '-'.rjust(10) + ' | ' + '-'.rjust(16) + ' | ' + '-'.rjust(17) + ' | ' + '-'.rjust(7)
                print getTimestamp() + ' | Error: DataSource ' + datasourceName + ' became unavailable on server ' + serverName
            else:
                # Fetch JDBC Data Source statistics
                activeCurrent = str(jdbcService.getActiveConnectionsCurrentCount())
                activeHigh = str(jdbcService.getActiveConnectionsHighCount())
                totalConnections = str(jdbcService.getConnectionsTotalCount())
                currCapacity = str(jdbcService.getCurrCapacity())
                highestAvailable = str(jdbcService.getHighestNumAvailable())
                waitingCount = str(jdbcService.getWaitingForConnectionCurrentCount())
                
                # Append stats to log message with aligned columns
                logMessage = (logMessage + ' | ' + activeCurrent.rjust(14) + ' | ' + activeHigh.rjust(11) + ' | ' + 
                              totalConnections.rjust(10) + ' | ' + currCapacity.rjust(16) + ' | ' + highestAvailable.rjust(17) + ' | ' + waitingCount.rjust(7))
            
            # Log only if data changed for this datasource
            if logMessage != lastLogData[datasourceName]:
                writeToLog(logFile, logMessage)
                print logMessage + ' (for ' + datasourceName + ' on ' + serverName + ')'
                lastLogData[datasourceName] = logMessage
        
        # Wait for the specified interval before the next check
        sleepSeconds(monitoringInterval)
    
    except Exception, e:
        errorMsg = getTimestamp() + ' | Error occurred: ' + str(e)
        print errorMsg
        # Write error to all log files of accessible datasources
        for datasourceName in accessibleDatasources:
            logFile = logFiles[datasourceName]
            writeToLog(logFile, errorMsg)
        break

# Finalize
for datasourceName in accessibleDatasources:
    logFile = logFiles[datasourceName]
    writeToLog(logFile, '=== Monitoring Ended at ' + getTimestamp() + ' ===')
print 'Monitoring complete. Results logged to separate files in ' + outputDir

# Disconnect from the server
disconnect()
exit()