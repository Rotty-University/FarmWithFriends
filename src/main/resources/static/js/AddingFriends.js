// BEGIN REDACT
/**

 This method will output all the friends of this current user whenever it is clicked.
 It sends a post request to the backend that will then send the list of friends as a json.
 */
function openFriendList(form){
    document.getElementById(form).style.display = "block";
    document.getElementById("friendsContainer").className = "friendsActivated";
    const suggestionList = $("#list_of_friends");
    const pendinglist = $("#list_of_friends_pending");
    suggestionList.empty();
    const postParameters = {
        //TODO: get the text inside the input box
        text: "placeholder"
    };
    console.log("we are here about to do friend loader")
    $.post("/friendLoader", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        const list = object.list.split(",")
        const arrayLength = list.length;
        //showing a list of the friends when the button is clicked.
        for (let i = 0; i < arrayLength-1; i++) {
            suggestionList.append("<li id=\"addedfriends\">"+list[i]+"</li>");
        }
    });
    //code for the pending below
    //         $.post("/friendPendingLoader", postParameters, response => {
    //     // Do something with the response here
    //     const object = JSON.parse(response);
    //     const list = object.list.split(",")
    //     const arrayLength = list.length;
    //     //showing a list of the friends when the button is clicked. 
    //     for (let i = 0; i < arrayLength; i++) {
    //       pendingList.append("<li class=\"pending\">"+list[i]+"</li>");
          
    //     } 
    // });
};
//Below is where we are going to take care of adding a friend and setting a post request with the user we are trying to add.
function sendAddRequest() {
    //This is where we will send a post request with the user we are trying to add.
    const submit = $("#add_friend_button");
    // console.log(submit.innerHTML);
    const input = $("#addfriendstext");
    const message = $("#message_for_friend_status");
    // submit.click(function(event){
        const postParameters = {
            //TODO: get the text inside the input box
            text: input.val()
        };
        console.log(postParameters.text);
        //send the post and show the message from the backend.
        $.post("/adding_friend", postParameters, response => {
            // Do something with the response here
            const object = JSON.parse(response);
            const message_to_player = object.message;
            //message to output to the user about if we are adding the friend or not.
            message.empty()
            message.append(message_to_player);
        });
    // });
};

//Method for opening the div forms.
function openForm(form) {
    document.getElementById(form).style.display = "block";
};
//method for closing the two div forms
function closeForm(form) {
    document.getElementById(form).style.display = "none";

};

//     document.getElementById("friendsContainer").className = "";
// };

function closeAddForm(form) {
    document.getElementById(form).style.display = "none";
}

