document.addEventListener('DOMContentLoaded', function () {

    $('#alternative-pagination-metrics').DataTable();
    $('#alternative-pagination-dashboard').DataTable();
    $('#alternative-pagination-settings').DataTable();
    $('#alternative-pagination-consumer-groups').DataTable();
    $('#alternative-pagination-consumer-partitions').DataTable();
    $('#alternative-pagination-producers').DataTable();
    $('#alternative-pagination-producer-partitions').DataTable();


    //Set find message trigger
    $('.fetch-message').submit(function(e)
    {
        e.preventDefault(); //Prevent the normal submission action
        var form = this
        
        var dataTableId = form.getAttribute('data-target-datatable-id')
        var clusterName = form.querySelector('[name="clusterName"]').value
        var topicName = form.querySelector('[name="topicName"]').value
        var startOffset = form.querySelector('[name="startOffset"]').value
        var endOffset = form.querySelector('[name="endOffset"]').value
        var rowCount = form.querySelector('[name="rowCount"]').value
        var startIndex = form.querySelector('[name="startIndex"]').value
        var searchString = form.querySelector('[name="searchString"]').value

        fecthMessages(dataTableId,startOffset,endOffset,topicName,clusterName,startIndex,rowCount,searchString)

    });
});



function fecthMessages(targetDataTable,startOffset,endOffset,topicName,clusterName,startIndex,rowCount,searchString)
{
    let jsonValue = {
        "startOffset": startOffset,
        "endOffset" : endOffset,
        "topicName" : topicName,
        "clusterName" : clusterName,
        "startIndex" : startIndex,
        "countRow" : rowCount,
        "searchString" : searchString
    }
    //alternative-pagination-topic-messages
    let messagingTable= new DataTable('#'+ targetDataTable,{
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
        serverSide: false,
        destroy: true
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