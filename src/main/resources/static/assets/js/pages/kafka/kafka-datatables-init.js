document.addEventListener('DOMContentLoaded', function () {

    $('#alternative-pagination-metrics').DataTable();
    $('#alternative-pagination-dashboard').DataTable();
    $('#alternative-pagination-settings').DataTable();
    $('#alternative-pagination-consumer-groups').DataTable();
    $('#alternative-pagination-consumer-partitions').DataTable();
    $('#alternative-pagination-producers').DataTable();
    $('#alternative-pagination-producer-partitions').DataTable();

});


function fecthMessages(startOffset,endOffset,topicName,clusterName,startIndex,countRow,searchString)
{
    let jsonValue = {
        "startOffset": startOffset,
        "endOffset" : endOffset,
        "topicName" : topicName,
        "clusterName" : clusterName,
        "startIndex" : startIndex,
        "countRow" : countRow,
        "searchString" : searchString
    }
    
    let messagingTable= new DataTable('#alternative-pagination-topic-messages',{
        ajax: {
            type: 'POST',
            url: '/api/v1/message/fetch',
            data: function(d) {
                return JSON.stringify(jsonValue);
            },
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            dataSrc: function(json) {
                return json.messages;  
            }
        },
        columns: [
            { data: 'topicName',title: 'Topic Name'},
            { data: 'partition',title: 'Partition'},
            { data: 'offset',title: 'Offset'},
            { data: 'leaderEpoch',title: 'Leader Epoch'},
            { data: 'value',title: 'Message'}
        ],
        processing: true,
        serverSide: false
    });
}

//modal data datables
document.addEventListener('DOMContentLoaded', function () {
  let table = new DataTable('#model-datatables', {
      responsive: {
            details: {
                display: $.fn.dataTable.Responsive.display.modal( {
                    header: function ( row ) {
                        var data = row.data();
                        return 'Details for '+data[0]+' '+data[1];
                    }
                } ),
                renderer: $.fn.dataTable.Responsive.renderer.tableAll( {
                    tableClass: 'table'
                } )
            }
        }
    });
    
}); 