/**
This is the method that will send a post request with the information from the input fields to validate whether or not it is valid.
It will send a post request with the info and receieve a message back in return. 
*/
function createAccountHandler(){
	let email = $("#email");
	let username = $("#username");
	let password = $("#pass");
	let re_password = $("#re_password");
	const message = $("#message_for_account");
    const postParameters = {
    //getting the input fields from the inputs. 
	    email: email.val(),
	    username: username.val(),
	    password: password.val(),
	    re_password: re_password.val()
	};
        $.post("/new_user_form", postParameters, response => {
            // Do something with the response here
            const object = JSON.parse(response);
            const message_to_player = object.message;
            const canCreate = object.canCreate;
            message.empty()
            if(canCreate === "badUser"){
            	username.val("");
            	message.append(message_to_player);
            }else if(canCreate === "badPassword"){
            	password.val("");
            	re_password.val("");
            	message.append(message_to_player);
            }else if(canCreate === "badUser"){
            	message.append(message_to_player);
            	username.val("");
            }
            else if(canCreate === "badEmail"){
            	message.append(message_to_player);
            	email.val("");
            }
            else{
    	  		let locationURL = window.location.href;
				locationURL = locationURL.replace("create_account", "new_user")
				window.location.replace(locationURL);
            }
            
        });
};
function selectedCropControl(){
    let selected_item = $(".cropOptions");
    console.log(selected_item.val());
    const postParameters = {
        crop : selected_item.val()
    }
    $.post("/cropSelection", postParameters, response => {
        let responseObject = JSON.parse(response)
        selected_item.val("");
    })
};