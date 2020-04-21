// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */
 /**
 This is the front end logic 

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
      
          console.log(response);
          // Do something with the response here
          const object = JSON.parse(response);
          const list = object.list.split(",")
          const arrayLength = list.length;
          //showing a list of the friends when the button is clicked. 
          for (let i = 0; i < arrayLength; i++) {
            suggestionList.append("<li>"+list[i]+"</li>");
            
          }
          // let elements = document.getElementsByTagName("li")
          // for (let e of elements){
          // console.log(e);
          // e.addEventListener("click", function(event) {
          // input.val(event.currentTarget.innerHTML);
          // });
          // }  
      });
  };
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
  
      console.log(response);
      // Do something with the response here
      const object = JSON.parse(response);
      const message_to_player = object.message;
      //message to output to the user about if we are adding the friend or not. 
      message.empty()
      message.append(message_to_player);
      });  
  });


  });
