// BEGIN REDACT
 /**
 This method will output all the friends of this current user whenever it is clicked. 
It sends a post request to the backend that will then send the list of friends as a json.
 */
function openFriendList(form){
  document.getElementById(form).style.display = "block";
  const suggestionList = $("#list_of_friends");
  suggestionList.empty();
  const postParameters = {
    //TODO: get the text inside the input box
    text: "placeholder"
  };
    $.post("/friendLoader", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        const list = object.list.split(",")
        const arrayLength = list.length;
        //showing a list of the friends when the button is clicked. 
        for (let i = 0; i < arrayLength; i++) {
          suggestionList.append("<li>"+list[i]+"</li>");
          
        } 
    });
};
//Below is where we are going to take care of adding a friend and setting a post request with the user we are trying to add.
$(document).ready(() => {
  //This is where we will send a post request with the user we are trying to add.
  const submit = $("#add_friend_button");
  const input = $("#addfriendstext");
  const message = $("#message_for_friend_status");
  submit.click(function(event){
    const postParameters = {
      //TODO: get the text inside the input box
      text: input.val()

    };
    //send the post and show the message from the backend. 
      $.post("/adding_friend", postParameters, response => {
      // Do something with the response here
      const object = JSON.parse(response);
      const message_to_player = object.message;
      //message to output to the user about if we are adding the friend or not. 
      message.empty()
      message.append(message_to_player);
      });  
  });
});
//Method for opening the div forms.
function openForm(form) {
  document.getElementById(form).style.display = "block";
};
//method for closing the two div forms
function closeForm(form) {
  document.getElementById(form).style.display = "none";
  };
