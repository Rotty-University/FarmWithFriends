		var total_x = 20; //Total width
		var total_y = 20; // Total height
		var total_elements = total_x * total_y; //Total of elements in the matrix
		var map = createArray(total_x, total_y);
		var map_empty = [];
		var basic_elements = [];
		var tolerance = 10; // Number of consecutive blocks of the same type to make it right
		var allow_multiples_seeds = true; //If this is set up to true, once we cannot continue expanding a current seed, we are going to generate a new one.
		var total_options = [];
		let counter  =0;
		let my_var = 0;
		let dictionaryy = {};
		//this dictionary will be set from the the getting map from database so we can use it in the clickhandler. 
		let map_information= {};
		//will be used to count the number of total free spaces available through subtraction with total. 
		let waterSpaceCount = {};
		
		/**
		This is the method that will take care of when the user clicks on the map to track where their map will be. This function will update the map
		so that visually, the map location turns black and it will send a post request to the backend so that the map will be logically updated in the 
		backend. 
		*/
		function mapClickHandler(){
			//this is message for the user
			document.getElementById("message_for_user_on_click").innerHTML = "";
			console.log(event.pageX);
			console.log(event.pageY);
			//storing the map object
			let map_obj = $("#map_table");
			console.log(map_obj.offset());
			//calculating the row and column values.
			const col_val = event.pageX - map_obj.offset().left;
  			const row_val = event.pageY - map_obj.offset().top;
  			//Getting the indices of the row and column.
  			let row_num = Math.floor(row_val/20);
  			let col_num = Math.floor(col_val/20);
  			//if it is water space, then output a message saying we can't click here. 
  			if(map_information[(row_num+1)+','+(col_num+1)][2] === 'water_space' ){
  				document.getElementById("message_for_user_on_click").append("Sorry! Can't have your farm here. It's water!. Please pick somewhere else.");
  			}
  			//if it is a black space, output a message saying it is already occupied. 
  			else if(map_information[(row_num+1)+','+(col_num+1)][2] === 'black_space'){
				document.getElementById("message_for_user_on_click").append("Sorry! Can't have your farm here. it is already occupied.");
  			}
  			//the space is valid and they can place their map here. 
  			else{
  				//farm type and other setters.
  				let farm_type = map_information[(row_num+1)+','+(col_num+1)][2];
  				//setting it to black space so it is now occupied. 
  				map_information[(row_num+1)+','+(col_num+1)][2] = 'black_space';
  				changeElementType(row_num+1, col_num+1, "black_space");
  				//sending  a post with this map to update it in the database.
  				const postParameters = {
  					dictionary_data: JSON.stringify(map_information),
  					row: (row_num+1),
  					col: (col_num+1),
  					new_user: 'false',
  					farmtype: farm_type
  				}
  				//sending a post request to the clickmaphandler to redirect to the home page. 
  				$.post("/clickOnMap" , postParameters, response =>{
  					window.location.replace("http://localhost:4567/home");
  				});
  				

  			}
  			

		}
		/**
		This function will be called when we need a new map built if the other map is filled up already. The backend will send a signal for when this map
		should be created and this map will be stored in the database in the backend. 
		**/
		function makeMapWhenNoFreeSpace(){
			document.getElementById("map_table").style.display = "none"
			document.getElementById("map_table").innerHTML = "";
			fillTable();
			setBasicElements();
			fullfillTerrain();
			for(i=0;i<tolerance;i++) cleanUpMap();
			const postParameters = {
				dictionary_data: JSON.stringify(dictionaryy),
				free_space: (total_y*total_x)- Object.keys(waterSpaceCount).length,
				water_space_data : JSON.stringify(waterSpaceCount)
			};
			console.log("we are here about to send over the data to make the map");
			$.post("/mapMaker", postParameters, response => {
				const object = JSON.parse(response);
				const dictionaryyy = JSON.parse(object.data)
				console.log(Object.keys(dictionaryyy).length);
				for(let x = 1; x<total_x+1;x++){
					for(let y = 1; y<total_y+1;y++){
						changeElementType(dictionaryyy[x.toString()+","+y.toString()][0],dictionaryyy[x.toString()+","+y.toString()][1],dictionaryyy[x.toString()+","+y.toString()][2]);
					}
				}
				document.getElementById("map_table").style.display = "block";
			});
			map_information = dictionaryy;
			dictionaryy = {};
			waterSpaceCount = {};
		};
		// function makeMap(){
		// 	if(my_var%2 === 0){
		// 		document.getElementById("map_table").innerHTML = "";
		// 		fillTable();
		// 		setBasicElements();
		// 		fullfillTerrain();
		// 		for(i=0;i<tolerance;i++) cleanUpMap();
		// 		my_var++;
		// 	}else{
		// 		console.log("In the else statement");
		// 		document.getElementById("map_table").innerHTML = "";
		// 		fillTable();
		// 		setBasicElements();
		// 		console.log(Object.keys(dictionaryy).length);
		// 		for(let x = 1; x<total_x+1;x++){
		// 			for(let y = 1; y<total_y+1;y++){
		// 				changeElementType(dictionaryy[x.toString()+","+y.toString()][0],dictionaryy[x.toString()+","+y.toString()][1],dictionaryy[x.toString()+","+y.toString()][2]);
		// 			}
		// 		}
		// 		my_var++;
		// 		dictionaryy = {};
		// 		waterSpaceCount = {};
		// 	}
		// 	document.getElementById("map_table").style.display = "block";
		// };
		function makeMapFromDataBase(){
			document.getElementById("map_table").innerHTML = "";
			fillTable();
			setBasicElements();
			const postParameters = {
				dictionary_data: "placeholder"
			};
			$.post("/mapRetriever", postParameters, response => {
				console.log("In the mapRetriever");
				const object = JSON.parse(response);
				let map_dictionary_with_objectlocations = JSON.parse(object.data);
				let mapNeededVariable = object.mapNeeded;
				map_information = map_dictionary_with_objectlocations;
				console.log(mapNeededVariable);
				if(mapNeededVariable === "false"){
					for(let x = 1; x<total_x+1;x++){
						for(let y = 1; y<total_y+1;y++){
						
							changeElementType(map_dictionary_with_objectlocations[x.toString()+","+y.toString()][0],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][1],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][2]);
						}
					}
				}else{
					makeMapWhenNoFreeSpace();
				}
				//showing the map.
				document.getElementById("map_table").style.display = "block";
				});
		};
		/*
		This method will close the map when we don't need it anymore.
		*/
		function closeMap(){
			document.getElementById("map_table").style.display = "none";
		}
		function cleanUpMap(){
			for(x=0;x<total_x;x++){
				for(y=0;y<total_y;y++){
					var found = false; // We are going to set this as true if we found a coincidence.
					if(x > 0){
						var new_x = x - 1;
						if(map[new_x][y] == map[x][y]){
							found = true;
						}else{
						}
					}
					if(x < (total_x)-1){
						var new_x = x + 1;
						if(map[new_x][y] == map[x][y]){
							found = true;
						}else{
						}
					}
					if(y > 0){
						var new_y = y - 1;
						if(map[x][new_y] == map[x][y]){
							found = true;
						}else{
						}
					}
					
					if(y <  (total_y - 1)){
						var new_y = y + 1;
						if(map[x][new_y] == map[x][y]){
							found = true;
						}else{
						}
					}
					
					if(found == false){ // This means it is all alone
						
						var options = [];
						
						// TOP
						v = new Object();
						v.x = -1;
						v.y = 0;
						options.push(v);
						
						// RIGHT
						v = new Object();
						v.x = 0;
						v.y = +1;
						options.push(v);
						
						// BOTTOM
						v = new Object();
						v.x = +1;
						v.y = 0;
						options.push(v);
						
						// LEFT
						v = new Object();
						v.x = 0;
						v.y = -1;
						options.push(v);
						
						random_option = getRandom(0,4);
						random_option_selected = options[random_option];
						random_x = x + random_option_selected.x;
						random_y = y + random_option_selected.y;
						
						if(random_x < 0){
							random_x = 1;
						}
						if(random_y < 0){
							random_y = 1;
						}
						if(random_x >= total_x){
							random_x = total_x - 1;
						}
						if(random_y >= total_y){
							random_y = total_y - 2;
						}
						changeElementType(x+1,y+1,map[random_x][random_y]);
						let new_xx  = x+1
						let new_yy = y+1
						dictionaryy[new_xx+","+new_yy] = [new_xx,new_yy,map[random_x][random_y]];
						//checking if this location was in dictionary as water space but has now changed. 
						if(waterSpaceCount.hasOwnProperty(new_xx+","+new_yy) && map[random_x][random_y] != 'water_space'){
							delete waterSpaceCount[new_xx+","+new_yy];
							console.log("IN HERE");
						}
						//adding to the dictionary if it is of water space so we know much non-farmable space there is. 
						if(map[random_x][random_y] === 'water_space'){
							waterSpaceCount[new_xx+","+new_yy] = 1;
						}
					
					}	
				}
			}
		}
		
		function fullfillTerrain(){
			full = false;
			while( full == false ){
					addSeedToRandom( getRandom(0,(basic_elements.length)) );
					var numItems = $('.empty').length
					if(numItems == 0){
						full = true;
					}
			}
			for(i = 0 ; i < basic_elements.length ; i++){
			}
		}
		function fillTable(){ // Creates the basic table with the array
				for(x = 0 ; x < total_x ; x++){
					var extra = '';
					extra += '<div class="row">';
					for(y = 0 ; y < total_y ; y++){
						extra += '<div class="col element empty" x="' + (x+1) + '" y="' + (y+1) + '" id="space_' + (x+1) + '-' + (y+1) + '"></div>';
						map_empty.push((x+1)+","+(y+1));
					}
					extra += '</div>';
					print_row_table(extra);
				}	
		}
		
		function setBasicElements(){ // Sets the basic elements. In the example are 4 of them
			var pre_text = 'Element: ';
			//Forest
			var v = new Object();
			v.class = 'forest_space';
			v.letter = 'f';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
			//Water
			var v = new Object();
			v.class = 'water_space';
			v.letter = 'w';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
			//Dessert
			var v = new Object();
			v.class = 'dessert_space';
			v.letter = 'd';
			v.max = 10;
			v.added = [];
			basic_elements.push(v);
			//Sabana
			var v = new Object();
			v.class = 'sabana_space';
			v.letter = 's';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
		}
		
		function print_row_table(extra){
			$("#map_table").append(extra);
		}
		
		
		function getRandom(min,max){
			return Math.floor((Math.random() * max) + min)
		}
		
		function addSeedToRandom(type, number){ // Adding the seed to the map. This is kind of the main function
			
			var type_selected = basic_elements[type];
			if(typeof type_selected == "undefined" || type_selected == null){ // If it is invalid, then we return with nothing.
				// logM("Error on the type selection");
				return;
			}
			
			//Now that we have the seed, we have to determine if there is already much of this type in the map. If the answer is yes, then we dont add anymore of this kind of seed.
			var total_elements_added = basic_elements[type].added.length;
			var porcent_added = total_elements_added * 100 / total_elements;
			
			if(parseFloat(porcent_added) > (parseFloat(basic_elements[type].max) + parseFloat(10)  )  ){
				return;
			}
			
			if(total_elements_added > 0){ //If there is already items, we have to see if we can continue the seed of one of them.
				//If it fails finding an element (block) to add the class, then we have to generate a new seed.
				extendSeedClass(type)
			}else{ // This is if we need to set up a new seed in the map
				setRandomSeedClass(type);
			}
		}
		
		function extendSeedClass(type){ //This function will take one random seed and try to extend it. If not, it will call the setRandomSeedClass function
			// logM("Trying to extend seed class");
			var max_tries = 8; // The max tries is actually the number of spaces around the main seed to search for
			var total_added = basic_elements[type].added.length;
			var random_element = basic_elements[type].added[getRandom(0,total_added)];
			var x = random_element.split(",")[0];
			var y = random_element.split(",")[1];
			//The following two lines are made to get a random space around the main element.
			var tries = 0; //This is the actual number of tries.
			//There is a change where all the attemps targeted the same point, this is a reasonable case
			//becase we want the generation as random as we can
			var added = false;
			while(tries < max_tries){
				var new_x = (parseInt(x) + (getRandom(0,3)-1)); 
				var new_y = (parseInt(y) + (getRandom(0,3)-1));
				selector = "#space_" + new_x + "-" + new_y;
				if($(selector).hasClass("empty")){
					changeElementType(new_x,new_y,basic_elements[type].class);
					//storing in array and dictionary so we can get a repeat. 
					total_options.push([new_x,new_y,basic_elements[type].class])
					dictionaryy[new_x+","+new_y] = [new_x,new_y,basic_elements[type].class];
					if(basic_elements[type].class === 'water_space'){
						waterSpaceCount[new_x+","+new_y] = 1;
					}
					tries = max_tries + 1;
					basic_elements[type].added.push(new_x+","+new_y);
					added = true;
				}else{
				}
				tries++;
			}
			
			if(added == false && allow_multiples_seeds){ //If it is false, then we have to generate a new seed somewhere else. I am still thinking about the need of this one.
				setRandomSeedClass(type);
			}
		}
		
		function setRandomSeedClass(type){ // We took one type of basic element and one class and we set it up
			var added = false;
			while(added == false){
				random_number_of_empty = getRandom(0,
					map_empty.length);
				random_empty = map_empty[random_number_of_empty];
				x = random_empty.split(",")[0];
				y = random_empty.split(",")[1];
				x = parseInt(x);
				y = parseInt(y)
				selector = "#space_"+x+'-'+y;
				var element = $(selector);
				
				if(element.hasClass('empty')){
					changeElementType(x,y,basic_elements[type].class);
					total_options.push([x,y,basic_elements[type].class]);
					dictionaryy[x+","+y] = [x,y,basic_elements[type].class];
					if(basic_elements[type].class === 'water_space'){
						console.log('true');
						waterSpaceCount[x+","+y] = 1;
					}
					added = true;
					basic_elements[type].added.push(x+","+y);
					map_empty.splice( map_empty.indexOf(random_empty), 1 );
				}else{
				}
			}
		}
		
		function changeElementType(x,y,cl){ // This will change a value given in X and Y to a certain class. This should be only a visual change.
				selector = "#space_"+x+'-'+y;
				var element = $(selector);
				$.each(basic_elements, function(index,value){
					element.removeClass(value.class);
				});
				element.removeClass('empty').addClass('selected').addClass(cl);
				map[(x-1)][(y-1)] = cl;
		}
		
		function createArray(length) { //create the new array
			var arr = new Array(length || 0),
				i = length;

			if (arguments.length > 1) {
				var args = Array.prototype.slice.call(arguments, 1);
				while(i--) arr[length-1 - i] = createArray.apply(this, args);
			}

			return arr;
		}
		