var toast_duration = 3000
var closable = true
var gravity = "top"
var position = "right"


function showToast(message,type){
    const backgroundColors = {
        notice: "bg-warning",
        alert: "bg-danger",
        error: "bg-danger",
        success: "bg-success"
    };

    if(message)
    {
        Toastify({
            text: message,
            duration: toast_duration,
            className: backgroundColors[type],
            close: closable,
            gravity: gravity,
            position: position
        }).showToast();
    }
}