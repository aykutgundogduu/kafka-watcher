

async function submitForm(form,event){
    event.preventDefault();
    var action = form.getAttribute("action")
    var method = form.getAttribute("method")

    var response = await fetch(action, {
        method: method,
        body: new FormData(form)
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

    return false;
}



// Numeric form input için gerekli
isData();

function isData() {
    var plus = document.getElementsByClassName('plus');
    var minus = document.getElementsByClassName('minus');
    var product = document.getElementsByClassName("product");

    if (plus) {
        Array.from(plus).forEach(function (e) {
            e.addEventListener('click', function (event) {
                // if(event.target.previousElementSibling.value )
                if (parseInt(e.previousElementSibling.value) < event.target.previousElementSibling.getAttribute('max')) {
                    event.target.previousElementSibling.value++;
                    if (product) {
                        Array.from(product).forEach(function (x) {
                            updateQuantity(event.target);
                        })
                    }

                }
            });
        });
    }

    if (minus) {
        Array.from(minus).forEach(function (e) {
            e.addEventListener('click', function (event) {
                if (parseInt(e.nextElementSibling.value) > event.target.nextElementSibling.getAttribute('min')) {
                    event.target.nextElementSibling.value--;
                    if (product) {
                        Array.from(product).forEach(function (x) {
                            updateQuantity(event.target);
                        })
                    }
                }
            });
        });
    }
}
setSubmitEventToButtonOutsideFromForm();
function setSubmitEventToButtonOutsideFromForm(){
    var submitButtons = document.getElementsByClassName('submit');

    if(submitButtons)
    {
        Array.from(submitButtons).forEach(function(e){
            e.addEventListener('click',function(event) {
                //Find form in button's parent
                form= e.parentNode.parentNode.getElementsByTagName("form")[0];
                if(form)
                {
                    form.requestSubmit();
                }
            });
        });
    }
}