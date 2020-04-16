$(document).ready(function() {
  const submit = $('#connectSubmit');
  const notif = $('#notificationBar');
  const output = $('#connectOutput');
  const actor1 = $('#actor1');
  const actor2 = $('#actor2');
  
  const loadSmall = $('#loadSmall');
  const loadLarge = $('#loadLarge');
  
  submit.click( function() {
    const name1 = actor1.val();
    const name2 = actor2.val();
    let postParams = {"actor1": name1, "actor2": name2}
    console.log(postParams);
    $.post('timdb/connect', postParams, response => {
      let resObj = JSON.parse(response);
      if (resObj.error) {
        newNotif(resObj.error, true);
      } else {
    	let existingNotifs = document.getElementById('notificationBar').children;
    		for(let i=0; i<existingNotifs.length; i++){
    		 if(existingNotifs[i].textContent.startsWith('xERROR:')) {
    			 existingNotifs[i].remove();
    		 }
    		}
        output.empty();
        formatList(resObj.actors, resObj.films);
      }
    });
  });
  
  loadSmall.click( function() {
    let postParams = {"dbfile": "data/timdb/smallTimdb.sqlite3"}
    $.post('timdb/mdb', postParams, response => {
      let status = JSON.parse(response).status;
      if (status == "success") {
        newNotif("Small preset database succesfully loaded");
      } else {
        newNotif("ERROR: " + status, true);
      }
    });
  });
  
  loadLarge.click( function() {
    let postParams = {"dbfile": "data/timdb/timdb.sqlite3"}
    $.post('timdb/mdb', postParams, response => {
      let status = JSON.parse(response).status;
      if (status == "success") {
        newNotif("Large preset database succesfully loaded");
      } else {
        newError("ERROR: " + status);
      }
    });
  });
  
  function formatList(actors, films) {
    if (films.length > 0) {
      for (i=1;i<actors.length;i++) {
        let link1 = $('<a/>').attr('href', '/timdb/actor/' + actors[i-1].id)
          .attr('target', '_blank').text(actors[i-1].name)
        let link2 = $('<a/>').attr('href', '/timdb/actor/' + actors[i].id)
          .attr('target', '_blank').text(actors[i].name)
        let link3 = $('<a/>').attr('href', '/timdb/film/' + films[i-1].id)
          .attr('target', '_blank').text(films[i-1].name)
        output.append( $('<li/>').append(link1, " -> ", link2, " in ", link3) );
      }
    } else {
      let link1 = $('<a/>').attr('href', '/timdb/actor/' + actors[0].id)
          .attr('target', '_blank').text(actors[0].name)
      let link2 = $('<a/>').attr('href', '/timdb/actor/' + actors[1].id)
          .attr('target', '_blank').text(actors[1].name)
      output.append( $('<li/>').append(link1, " -/- ", link2, " not connected") );
    }
  }
  
  function newNotif(msg, isError=false) { 
	let existingNotifs = document.getElementById('notificationBar').children;
	for(let i=0; i<existingNotifs.length; i++){
		if(existingNotifs[i].textContent === 'x' + msg) {
			return;
		}
		else {
			existingNotifs[i].remove();
		}
	}
	
    let newMsg = $('<div/>').text(msg);
    let msgClass = 'msgNotif';
    
    if (isError) {
      msgClass = 'errorNotif';
      let closeButton = $('<button/>').text('x').one('click', function() {
          $(this).parent('.' + msgClass).remove();
        });
        newMsg.prepend(closeButton);
    }
    
    newMsg.addClass(msgClass);
    notif.append(newMsg);
  }
});