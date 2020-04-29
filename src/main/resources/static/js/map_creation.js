var total_x = 20; //Total width
		var total_y = 20; // Total height
		var total_elements = total_x * total_y; //Total of elements in the matrix
		var map = createArray(total_x, total_y);
		var map_empty = [];
		var basic_elements = [];
		var tolerance = 12; // Number of consecutive blocks of the same type to make it right
		var allow_multiples_seeds = true; //If this is set up to true, once we cannot continue expanding a current seed, we are going to generate a new one.
		var total_options = [];
		let counter  =0;
		let my_var = 0;
		let dictionaryy = {};
	
		$(document).ready(function(){ //Document start
			document.getElementById("map_table").style.display = "none"
				
				
		});
		function makeInitialMap(){
			console.log("in function");
			document.getElementById("map_table").style.display = "none"
			document.getElementById("map_table").innerHTML = "";
			fillTable();
			setBasicElements();
			fullfillTerrain();
			for(i=0;i<tolerance;i++) cleanUpMap();
			console.log(counter);
			const postParameters = {
				dictionary_data: JSON.stringify(dictionaryy)
			};
			console.log("we are here about to send over the data to make the map");
			$.post("/mapMaker", postParameters, response => {
				const object = JSON.parse(response);
				const dictionaryyy = JSON.parse(object.data)
				console.log(dictionaryyy["19,18"][0])
				console.log(Object.keys(dictionaryyy).length);
				for(let x = 1; x<total_x+1;x++){
					for(let y = 1; y<total_y+1;y++){
						console.log(dictionaryyy[x.toString()+","+y.toString()]);
						changeElementType(dictionaryyy[x.toString()+","+y.toString()][0],dictionaryyy[x.toString()+","+y.toString()][1],dictionaryyy[x.toString()+","+y.toString()][2]);
					}
				}
				document.getElementById("map_table").style.display = "block";
			});
			
		};
		function makeMap(){
			if(my_var%2 === 0){
				document.getElementById("map_table").innerHTML = "";
				fillTable();
				setBasicElements();
				fullfillTerrain();
				for(i=0;i<tolerance;i++) cleanUpMap();
				my_var++;
				console.log(counter);
			}else{
				console.log("In the else statement");
				document.getElementById("map_table").innerHTML = "";
				fillTable();
				setBasicElements();
				console.log(Object.keys(dictionaryy).length);
				for(let x = 1; x<total_x+1;x++){
					for(let y = 1; y<total_y+1;y++){
						changeElementType(dictionaryy[x.toString()+","+y.toString()][0],dictionaryy[x.toString()+","+y.toString()][1],dictionaryy[x.toString()+","+y.toString()][2]);
					}
				}
				my_var++;
			}
			document.getElementById("map_table").style.display = "block";
		};
		function makeMapFromDataBase(){
			document.getElementById("map_table").innerHTML = "";
			fillTable();
			setBasicElements();
			const postParameters = {
				dictionary_data: "placeholder"
			};
			$.post("/mapRetriever", postParameters, response => {
				console.log("YESSIR");
				const object = JSON.parse(response);
				let dictionaryyy = JSON.parse(object.data)
				console.log(dictionaryyy["1,1"]);
				for(let x = 1; x<total_x+1;x++){
					for(let y = 1; y<total_y+1;y++){
						
						changeElementType(dictionaryyy[x.toString()+","+y.toString()][0],dictionaryyy[x.toString()+","+y.toString()][1],dictionaryyy[x.toString()+","+y.toString()][2]);
					}
				}
				document.getElementById("map_table").style.display = "block";
				});
		};
		function closeMap(){
			document.getElementById("map_table").style.display = "none";
		}
		function cleanUpMap(){
		// logM("Cleaning the map");
			for(x=0;x<total_x;x++){
				for(y=0;y<total_y;y++){
					// logM("CLEAN UP - " + x + " - " + y + " - " +  map[x][y]);
					var found = false; // We are going to set this as true if we found a coincidence.
					if(x > 0){
						var new_x = x - 1;
						if(map[new_x][y] == map[x][y]){
							found = true;
							// logM("We found a coincidence in: " + new_x + "," + y);
						}else{
							// logM("No coincidences in: " + new_x + "," + y);
						}
					}
					if(x < (total_x)-1){
						var new_x = x + 1;
						if(map[new_x][y] == map[x][y]){
							found = true;
							// logM("We found a coincidence in: " + new_x + "," + y);
						}else{
							// logM("No coincidences in: " + new_x + "," + y);
						}
					}
					if(y > 0){
						var new_y = y - 1;
						if(map[x][new_y] == map[x][y]){
							found = true;
							// logM("We found a coincidence in: " + x + "," + new_y);
						}else{
							// logM("No coincidences in: " + x + "," + new_y);
						}
					}
					
					if(y <  (total_y - 1)){
						var new_y = y + 1;
						if(map[x][new_y] == map[x][y]){
							found = true;
							// logM("We found a coincidence in: " + x + "," + new_y);
						}else{
							// logM("No coincidences in: " + x + "," + new_y);
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

						
						//alert("Found item at: " + x + ' (x=' + ( x + 1 )+ ')- ' + y + ' (y = ' + (y+1) + ') and will switch for the one at: ' + random_x + '(x = ' + (random_x + 1 ) + ')-' + random_y + '( y = ' + (random_y + 1) + ')');
						changeElementType(x+1,y+1,map[random_x][random_y]);
						let new_x  = x+1
						let new_y = y+1
						dictionaryy[new_x+","+new_y] = [new_x,new_y,map[random_x][random_y]];
						counter++;
					
					}
					
					
					
					
				}
			}
		}
		
		function fullfillTerrain(){
			full = false;
			// logM("Fullfiling Terrain");
			//for( i = 0; i<2000 ; i++ ){
			while( full == false ){
					addSeedToRandom( getRandom(0,(basic_elements.length)) );
					var numItems = $('.empty').length
					if(numItems == 0){
						full = true;
					}
			}
			for(i = 0 ; i < basic_elements.length ; i++){
				// logM("Elements of type " + basic_elements[i].class + ": " + basic_elements[i].added.length);
			}
		}
		function fillTable(){ // Creates the basic table with the array
				// logM("Starting to generate the map");
				for(x = 0 ; x < total_x ; x++){
					var extra = '';
					extra += '<div class="row">';
					for(y = 0 ; y < total_y ; y++){
						extra += '<div class="col element empty" x="' + (x+1) + '" y="' + (y+1) + '" id="space_' + (x+1) + '-' + (y+1) + '"></div>';
						map_empty.push((x+1)+","+(y+1));
					}
					extra += '</div>';
					// logM("Printing row");
					print_row_table(extra);
				}	
		}
		
		function setBasicElements(){ // Sets the basic elements. In the example are 4 of them
			// logM("Filling basic elements");
			var pre_text = 'Element: ';
			//Forest
			var v = new Object();
			v.class = 'forest_space';
			v.letter = 'f';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
			// logM(pre_text + v.class);
			//Water
			var v = new Object();
			v.class = 'water_space';
			v.letter = 'w';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
			// logM(pre_text + v.class);
			//Dessert
			var v = new Object();
			v.class = 'dessert_space';
			v.letter = 'd';
			v.max = 10;
			v.added = [];
			basic_elements.push(v);
			// logM(pre_text + v.class);
			//Sabana
			var v = new Object();
			v.class = 'sabana_space';
			v.letter = 's';
			v.max = 30;
			v.added = [];
			basic_elements.push(v);
			// logM(pre_text + v.class);
		}
		
		function print_row_table(extra){
			$("#map_table").append(extra);
		}
		
		// function logM(t){ //Function to print messages to console without issues
		// 	try{
		// 		console.log(t);
		// 	}catch(err){}
		// }
		// function logE(t){ //Function to print messages to console without issues
		// 	try{
		// 		console.error(t);
		// 	}catch(err){}
		// }
		
		function getRandom(min,max){
			return Math.floor((Math.random() * max) + min)
		}
		
		function addSeedToRandom(type, number){ // Adding the seed to the map. This is kind of the main function
			// logM("Adding Random Seed");
			
			var type_selected = basic_elements[type];
			// total_options.append()
			if(typeof type_selected == "undefined" || type_selected == null){ // If it is invalid, then we return with nothing.
				// logM("Error on the type selection");
				return;
			}
			
			//Now that we have the seed, we have to determine if there is already much of this type in the map. If the answer is yes, then we dont add anymore of this kind of seed.
			var total_elements_added = basic_elements[type].added.length;
			var porcent_added = total_elements_added * 100 / total_elements;
			
			// logM("Porcent of elements added: " + porcent_added);
			if(parseFloat(porcent_added) > (parseFloat(basic_elements[type].max) + parseFloat(10)  )  ){
				// logM("Elements added of this type reach the maximun porcent permitted.");
				return;
			}
			
			// logM("Already added for the type " + type_selected.class + ': '+total_elements_added);
			
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
			// logM("Expanding element: " + random_element + ' that has added ' + total_added + ' elements');
			var x = random_element.split(",")[0];
			var y = random_element.split(",")[1];
			// logM("Equivalent: X = " + x + ' Y = ' + y);
			//The following two lines are made to get a random space around the main element.
			
			var tries = 0; //This is the actual number of tries.
			//There is a change where all the attemps targeted the same point, this is a reasonable case
			//becase we want the generation as random as we can
			var added = false;
			while(tries < max_tries){
				var new_x = (parseInt(x) + (getRandom(0,3)-1)); 
				var new_y = (parseInt(y) + (getRandom(0,3)-1));
				// logM("New Equivalent: X = " + new_x + ' Y = ' + new_y);
				selector = "#space_" + new_x + "-" + new_y;
				// logM("Looking for selector: " + selector);
				if($(selector).hasClass("empty")){
					// logM("New equivalent empty. Seting up class");
					changeElementType(new_x,new_y,basic_elements[type].class);
					//storing in array and dictionary so we can get a repeat. 
					total_options.push([new_x,new_y,basic_elements[type].class])
					dictionaryy[new_x+","+new_y] = [new_x,new_y,basic_elements[type].class];
					console.log(total_options[0])
					// counter++;
					tries = max_tries + 1;
					basic_elements[type].added.push(new_x+","+new_y);
					added = true;
				}else{
					// logM("New equivalent not empty");
				}
				tries++;
			}
			
			if(added == false && allow_multiples_seeds){ //If it is false, then we have to generate a new seed somewhere else. I am still thinking about the need of this one.
				// logE("We couldn't extend. We are generating a new seed");
				setRandomSeedClass(type);
			}
		}
		
		function setRandomSeedClass(type){ // We took one type of basic element and one class and we set it up
			// logM("Setting up random seed");
			var added = false;
			while(added == false){
				random_number_of_empty = getRandom(0,
					map_empty.length);
				// logM("Total empty spaces: " + map_empty.length);
				// logM("Random seed element number: "  + random_number_of_empty);
				random_empty = map_empty[random_number_of_empty];
				// logM("Random seed element: "  + random_empty);
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
					// counter++;
					added = true;
					basic_elements[type].added.push(x+","+y);
					// logM("Removing " + random_empty + " from the list of empty spaces");
					map_empty.splice( map_empty.indexOf(random_empty), 1 );
				}else{
					// logM("Couldn't find an empty space. Trying again");
				}
			}
			// logM("Added to x: " + x );
			// logM("Added to y: " + y );
		}
		
		function changeElementType(x,y,cl){ // This will change a value given in X and Y to a certain class. This should be only a visual change.
				// logM("Changing element " + x + ',' + y + ' to ' + cl);
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
		