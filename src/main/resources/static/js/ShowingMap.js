function showMapp() {
        var total_x = 20; //Total width
        var total_y = 20; // Total height
        var total_elements = total_x * total_y; //Total of elements in the matrix
        // var map = createArray(total_x, total_y);
        var map_empty = [];
        var basic_elements = [];
        var tolerance = 10; // Number of consecutive blocks of the same type to make it right
        var allow_multiples_seeds = true; //If this is set up to true, once we cannot continue expanding a current seed, we are going to generate a new one.
        var total_options = [];
        let dictionaryy = {};
        //this dictionary will be set from the the getting map from database so we can use it in the clickhandler. 
        let map_information= {};
        //will be used to count the number of total free spaces available through subtraction with total. 
        let waterSpaceCount = {};
        document.getElementById("map_viewer").innerHTML = "";
        for(x = 0 ; x < total_x ; x++){
            var extra = '';
            extra += '<div class="row">';
            for(y = 0 ; y < total_y ; y++){
                extra += '<div class="col element empty" x="' + (x+1) + '" y="' + (y+1) + '" id="spacee_' + (x+1) + '-' + (y+1) + '"></div>';
                map_empty.push((x+1)+","+(y+1));
            }
            extra += '</div>';
            $("#map_viewer").append(extra);
        }
        $.get("/mapRetrieverForMapsComponent", response => {
            const object = JSON.parse(response);
            let map_dictionary_with_objectlocations = JSON.parse(object.data);
            let row = object.row;
            let col = object.col;
            map_information = map_dictionary_with_objectlocations;
            map_dictionary_with_objectlocations[row+","+col][2] = "white_space";
            console.log(map_dictionary_with_objectlocations[row+","+col][2]);
                for(let x = 1; x<total_x+1;x++){
                    for(let y = 1; y<total_y+1;y++){
                    
                        changeElementTypee(map_dictionary_with_objectlocations[x.toString()+","+y.toString()][0],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][1],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][2]);
                    }
                }
            document.getElementById("map_viewer").style.display = "block";
        });   
    };
function changeElementTypee(x,y,cl){ 
    selector = "#spacee_"+x+'-'+y;
    var element = $(selector);
    $.each(basic_elements, function(index,value){
        element.removeClass(value.class);
    });
    element.removeClass('empty').addClass('selected').addClass(cl);
}