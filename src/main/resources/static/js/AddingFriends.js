// BEGIN REDACT
/**
 This method will output all the friends of this current user whenever it is clicked from the home page.
 It sends a post request to the backend that will then send the list of friends as a json.
 */
function openFriendList(form){
    document.getElementById(form).style.display = "block";
    document.getElementById("friendsContainer").className = "friendsActivated";
    //getting both of the friends and pending friends list using the id.
    const friendList = $("#list_of_friends");
    const pendinglist = $("#list_of_friends_pending");
    friendList.empty();
    pendinglist.empty();
    const postParameters = {
        //getting the text the user inputted. 
        text: "placeholder"
    };
    console.log("we are here about to do friend loader")
    //sending a post request to the back end to get the infromation about the users friend list
    $.post("/friendLoader", postParameters, response => {
        // parse the response so it is a javasccript object. 
        const object = JSON.parse(response);
        //splitting based on the commas so it turns into a list of friends.
        const list = object.list.split(",")
        const arrayLength = list.length;
        //showing a list of the friends when the button is clicked by looping through the friends.
        for (let i = 0; i < arrayLength-1; i++) {
            //appending to the friend list ul element.
            friendList.append("<li id=\"addedfriends\">"+list[i]+"</li>");
        }
    });
    //code for the pending below. Sending  a post reqest to the  backend. 
    $.post("/friendPendingLoader", postParameters, response => {
        // parsin the response here.
        const object = JSON.parse(response);
        const list = object.list.split(",")
        const arrayLength = list.length;
        //showing a list of the pending friends when the friends button is clicked
        for (let i = 0; i < arrayLength-1; i++) {
            //appending to the pending ul list element.
            pendinglist.append("<li class=\"pending\" id=\""+list[i]+"\">"+list[i]+"</li>");
        } 
        //getting the pending list element li. 
        let elements = document.getElementsByClassName("pending");
        for (let e of elements){
          console.log(e);
          //adding an onclick function that mimics accepting the friend. 
          e.addEventListener("click", function(event) {
            const postParams = {
                //getting the text the user inputted and this will be sent as a post request argument.
                text: event.currentTarget.innerHTML
            };
            //sending a post request to the backend with the information and it will return the info back so we add to the friends list. 
            $.post("/friendAccepted", postParams, response => {
                //parsing the response here since it is in JSON form. 
                const object = JSON.parse(response);
                //appending to the friends list and removing the element through its unique id. 
                friendList.append("<li id=\"addedfriends\">"+object.list+"</li>");
                document.getElementById(object.list).remove();
            });
          });
        } 
    });
};
/*
This is where we are going to take care of adding a friend and setting a post request with the user we are trying to add.
*/
function sendAddRequest() {
    //This is where we will send a post request with the user we are trying to add.
    const submit = $("#add_friend_button");
    //getting the input jquery element
    const input = $("#addfriendstext");
    //getting the message jquery element to output a message. 
    const message = $("#message_for_friend_status");
        const postParameters = {
            //getting the text from the input which is the user we want to add.
            text: input.val()
        };
        console.log(postParameters.text);
        //send the post and show the message from the backend and process whether we can add this friend or not. 
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
    document.getElementById("friendsContainer").className = "";
    document.getElementById(form).style.display = "none";

};
function closeAddForm(form) {
    document.getElementById(form).style.display = "none";
}

