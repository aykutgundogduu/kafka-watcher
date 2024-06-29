
function createModel(elementId)
{
  model = {}
  model.elementId = elementId
  model.total= {}
  return model;
}

function setModelValues(model)
{
  if(document.getElementById(model.elementId) == null) return null;
  model.series= getChartDataArray(model.elementId,"data-series")
  model.labels= getChartDataArray(model.elementId,"data-labels")
  model.colors= getChartDataArray(model.elementId,"data-colors")
  model.total.text= getChartDataArray(model.elementId,"data-total-text")
  model.format = getChartDataArray(model.elementId,"data-format")
  model.seriesName = getChartDataArray(model.elementId,"data-series-name")
  

  return model;
}

// get data labels array from the string
function getChartDataArray(chartId,elementTag) {
    if (document.getElementById(chartId) !== null) {
      var data = document.getElementById(chartId).getAttribute(elementTag);
      if (data) {
        try{
          data = JSON.parse(data);
          return data.map(function (value) {
            var newValue = value
            if (String(newValue).indexOf(",") === -1) {
              var data = getComputedStyle(document.documentElement).getPropertyValue(
                newValue
              );
              if (data) return data;
              else return newValue;
            } 
          });
        }
        catch(err)
        {
          console.warn(err)
          return data;
        }

      } else {
        console.warn(elementTag +' Attribute not found on:', chartId);
      }
    }
}  


clusterDiskSegmentModel = setModelValues(createModel("clusterDiskSegmentPie"))
if(clusterDiskSegmentModel)
{
  renderPieChartsByElement(clusterDiskSegmentModel)
}
//Render chart which element has data tag with 
function renderPieChartsByElement(model)
{
    var options = {
        series: model.series,
        labels: model.labels,
        chart: {
          type: "donut",
          height: 224,
        },
    
        plotOptions: {
          pie: {
            size: 100,
            offsetX: 0,
            offsetY: 0,
            donut: {
              size: "70%",
              labels: {
                show: true,
                name: {
                  show: true,
                  fontSize: "18px",
                  offsetY: -1,
                },
                value: {
                  show: true,
                  fontSize: "20px",
                  color: "#343a40",
                  fontWeight: 500,
                  offsetY: 5,
                  formatter: function (val) {
                    return val + model.format + " " ;
                  },
                },
                total: {
                  show: true,
                  fontSize: "13px",
                  label: model.total.text,
                  color: "#9599ad",
                  fontWeight: 500,
                  formatter: function (w) {
                    return (
                      w.globals.seriesTotals.reduce(function (a, b) {
                        return a + b;
                      }, 0) + " " + model.format 
                    );
                  },
                },
              },
            },
          },
        },
        dataLabels: {
          enabled: false,
        },
        legend: {
          show: false,
        },
        yaxis: {
          labels: {
            formatter: function (value) {
              return value + " " + model.format ;
            },
          },
        },
        stroke: {
          lineCap: "round",
          width: 2,
        },
        colors: model.colors,
      };
      var chart = new ApexCharts(document.querySelector("#"+model.elementId), options);
      chart.render();
}




// Messages by topics charts
messageByTopicsModel = setModelValues(createModel("messagesByTopics"))
if (messageByTopicsModel) {
    renderBarChart(messageByTopicsModel);
}
// Partitions by topic charts
messageByTopicsModel = setModelValues(createModel("partitionsByTopics"))
if (messageByTopicsModel != null) {
    renderBarChart(messageByTopicsModel);
}

function renderBarChart(model){
    var options = {
      series: [{
          data: model.series,
          name: model.seriesName,
      }],
      chart: {
          type: 'bar',
          height: 436,
          toolbar: {
              show: false,
          }
      },
      plotOptions: {
          bar: {
              borderRadius: 4,
              horizontal: true,
              distributed: true,
              dataLabels: {
                  position: 'top',
              },
          }
      },
      dataLabels: {
          enabled: true,
          offsetX: 32,
          style: {
              fontSize: '12px',
              fontWeight: 400,
              colors: ['#adb5bd']
          }
      },

      legend: {
          show: false,
      },
      grid: {
          show: false,
      },
      xaxis: {
          categories: model.labels,
      },
  };
  var chart = new ApexCharts(document.querySelector("#" + model.elementId), options);
  chart.render();
}
