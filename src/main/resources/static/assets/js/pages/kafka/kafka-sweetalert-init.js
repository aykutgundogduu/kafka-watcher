
function getConfirmation(object)
{
    var attributes = object.attributes;
    var action = attributes["data-action"].nodeValue
    var method = attributes["data-method"].nodeValue 
    var clusterName = attributes["data-cluster-name"].nodeValue
    var topicName = attributes["data-topic-name"].nodeValue

    //manipulate for first param
    var requestParams = "?1=1"

    if(method == undefined || action == undefined)
    {
      console.log("Invalid dom elementer attributes")
      return false;
    }

    if(clusterName)
    {
      requestParams += "&" + clusterName
    }
    if(topicName)
    {
      requestParams += "&" + topicName
    }

    action += requestParams

    Swal.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        icon: "error",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, do it!"
      }).then(async (result) => {
        if (result.isConfirmed) {
          var response = await fetch(action, {
          method: method,
          }).catch((error) => {
              showToast(data,"error");
          });

          if(response.status == 200)
          {
              response.text().then((data) => {
                  showToast(data,"success");
              });
          }
          else {
              response.text().then((data) =>{
                  showToast(data,"error");
              } )
          }
          }
      });
}