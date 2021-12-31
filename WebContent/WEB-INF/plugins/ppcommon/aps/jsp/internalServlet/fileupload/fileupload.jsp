<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<script>
(function ( $ ) {

    var global_settings = {};

    var methods = {
        init : function(options) {
            // This is the easiest way to have default options.
            var settings = $.extend({
                // These are the defaults.
                color: "#000000",
                height: "100px",
                width: "100px",
                line_width: 4,
                starting_position: 0,
                percent: 1,
                counter_clockwise: false,
                percentage: true,
                text: ''
            }, options );
            global_settings = settings;


            // Create percentage
            var percentage = $("<div class='progress-percentage'><"+"/div>");
            
            if(!global_settings.percentage) {
                percentage.text(global_settings.percentage);
            }
            $(this).append(percentage);


            // Create text
            var text = $("<div class='progress-text'><"+"/div>");
            
            // Custom text
            if(global_settings.text != "percent") {
                text.text(global_settings.text);
            }
            $(this).append(text);

            // Correct any invalid values
            if(global_settings.starting_position != 100) {
                global_settings.starting_position = global_settings.starting_position % 100;
            }
            if(global_settings.ending_position != 100) {
                global_settings.ending_position = global_settings.ending_position % 100;
            }
            // No 'px' or '%', add 'px'
            appendUnit(global_settings.width);
            appendUnit(global_settings.height);

            // Apply global_settings
            $(this).css({
                "height": global_settings.height,
                "width": global_settings.width
            });
            $(this).addClass("circular-progress-bar");

            // Remove old canvas
            $(this).find("canvas").remove();

            // Put canvas inside this
            $(this).append(createCanvas($(this)));

            // Return allows for chaining
            return this;
        },
        percent : function(value) {
            // Change percent
            global_settings.percent = value;
            // Apply global_settings
            $(this).css({
                "height": global_settings.height,
                "width": global_settings.width
            });
            // Remove old canvas
            $(this).children("canvas").remove();
            // Put canvas inside this
            $(this).append(createCanvas($(this)));

            // Return allows for chaining
            return this;
        },
        animate : function(value, time) {
            // Apply global_settings
            $(this).css({
                "height": global_settings.height,
                "width": global_settings.width
            });

            // Number of intervals, 10ms interval
            var num_of_steps = time / 10;
            // Amount of change each step
            var percent_change = (value - global_settings.percent) / num_of_steps;

            // Variable conflict, rename this
            var scope = $(this);
            var theInterval = setInterval(function() {
                if(global_settings.percent < value) {
                    // Remove old canvas
                    scope.children("canvas").remove();
                    // Increment percent
                    global_settings.percent += percent_change;
                    // Put canvas inside this
                    scope.append(createCanvas(scope));
                } else {
                    clearInterval(theInterval);
                }
            }, 10);

            // Return allows for chaining
            return this;
        }
    };

    $.fn.circularProgress = function(methodOrOptions) {
        if (methods[methodOrOptions]) {
            // Method found
            return methods[methodOrOptions].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof methodOrOptions === 'object' || !methodOrOptions) {
            // Default to "init", object passed in or nothing passed in
            return methods.init.apply( this, arguments );
        } else {
            $.error('Method ' +  methodOrOptions + ' does not exist.');
        }
    };

    /* =========================================================================
        PRIVATE FUNCTIONS
    ========================================================================= */

    // return string without 'px' or '%'
    function removeUnit(apples) {
        if(apples.indexOf("px")) {
            return apples.substring(0, apples.length - 2);
        } else if(canvas_height.indexOf("%")) {
            return apples.substring(0, apples.length - 1);
        }
    };
    // return string with 'px'
    function appendUnit(apples) {
        if(apples.toString().indexOf("px") < -1 && apples.toString().indexOf("%") < -1) {
            return apples += "px";
        }
    };
    // calculate starting position on canvas
    function calcPos(apples, percent) {
        if(percent < 0) {
            // Calculate starting position
            var starting_degree = (parseInt(apples) / 100) * 360;
            var starting_radian = starting_degree * (Math.PI / 180);
            return starting_radian - (Math.PI / 2);
        } else {
            // Calculate ending position
            var ending_degree = ((parseInt(apples) + parseInt(percent)) / 100) * 360;
            var ending_radian = ending_degree * (Math.PI / 180);
            return ending_radian - (Math.PI / 2);
        }
    };
    // Put percentage or custom text inside progress circle
    function insertText(scope) {
        $(".progress-percentage").text(Math.round(global_settings.percent) + "%");
    }
    // create canvas
    function createCanvas(scope) {
        // Remove 'px' or '%'
        var canvas_height = removeUnit(global_settings.height.toString());
        var canvas_width = removeUnit(global_settings.width.toString());

        // Create canvas
        var canvas = document.createElement("canvas");
        canvas.height = canvas_height;
        canvas.width = canvas_width;

        // Create drawable canvas and apply properties
        var ctx = canvas.getContext("2d");
        ctx.strokeStyle = global_settings.color;
        ctx.lineWidth = global_settings.line_width;

        // Draw arc
        ctx.beginPath();

        // Calculate starting and ending positions
        var starting_radian = calcPos(global_settings.starting_position, -1);
        var ending_radian = calcPos(global_settings.starting_position, global_settings.percent);
        // Calculate radius and x,y coordinates
        var radius = 0;
        var xcoord = canvas_width / 2;
        var ycoord = canvas_height / 2;
        // Height or width greater
        if(canvas_height >= canvas_width) {
            radius = canvas_width * 0.9 / 2 - (global_settings.line_width * 2);
        } else {
            radius = canvas_height * 0.9 / 2 - (global_settings.line_width * 2);
        }

        /*
            x coordinate
            y coordinate
            radius of circle
            starting angle in radians
            ending angle in radians
            clockwise (false, default) or counter-clockwise (true)
        */
        ctx.arc(xcoord, ycoord, radius, starting_radian, ending_radian, global_settings.counter_clockwise);
        ctx.stroke();

        // Add text
        if(global_settings.percentage) {
            insertText(scope);
        }

        return canvas;
    };

}( jQuery ));

</script>
<style>

.circular-progress-bar {
    position: relative;
    margin: 0 auto;
}

.progress-percentage, .progress-text {
    position: absolute;
    width: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
}

.progress-percentage {
	font-size: 20px;

}

.progress-text {
    transform: translate(-50%, 0%);
    color: #585858;
    font-size: 16px;
}

.progressmessage{
    text-align: center;
    padding-bottom: 1em;
    color: black;
	font-weight: bold;
}

.modal {
  display: none; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1000; /* Sit on top */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  -webkit-animation-name: fadeIn; /* Fade in the background */
  -webkit-animation-duration: 0.4s;
  animation-name: fadeIn;
  animation-duration: 0.4s
}

/* Modal Content */
.modal-content {
  position: fixed;
  width: 100%;
  background-color: #fefefe;
  -webkit-animation-name: slideIn;
  -webkit-animation-duration: 0.4s;
  animation-name: slideIn;
  animation-duration: 0.4s;
  bottom: 0;
}

/* The Close Button */

.modal-body {padding: 2px 16px;}
</style>


<%--
 * I parametri di questa pagina sono i seguenti:
 *
 *	 inputDescr		name/id dell'input (text) che rappresenta la descrizione del file 								
 *	 inputFile		name/id dell'input (file) che rappresenta il file
 *   actionUrl 		url della action che utilizza l'upload 
 *	
 --%>
<c:set var="inputDescr" value="${param.inputDescr}" />
<c:set var="inputFile" value="${param.inputFile}" /> 
<c:set var="actionUrl" value="${param.actionUrl}" />
<c:set var="hasEncrypt" value="${param.hasEncrypt}" />
<s:url id="refreshUrl" namespace="/do/FrontEnd/FileUpload" action="fileUploadRefresh" />

<c:set var="validFilenameChars"><%= it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities.getValidFilenameChars() %></c:set>


<script type="text/javascript">
<!--//--><![CDATA[//><!--
         
    var hasEncrypt = '${hasEncrypt}';       
	var actionUrl = '${refreshUrl}';
	var validFilenameChars = '${validFilenameChars}';
                 
	$(document).ready(function() {
		

		$('input[type="file"]').on("change", function() {
			var file = $(this)[0].files[0];
			if( isValidFilename(file.name) ) {
				document.getElementById('myModal').style.display="block";
				$(".my-progress-bar").circularProgress({
					line_width: 6,
					color: "#ccc",
					starting_position: 0, 
					percent: 1, 
					percentage: true,
				}).circularProgress({});
				$('#progressmessage').html('<wp:i18n key="LABEL_FILE_UPLOADING" />');
				
				uploadRefreshAsync();
			}
		});	
						
		function uploadRefreshAsync() {

			return $.ajax({
				url: actionUrl,
			    type: 'POST',
			    dataType: 'json'
				})
			  	.done(function(data) {									
				
			  		var percentDone = data.percentDone;
					var color = "#ea0404";
					if(percentDone > 30 && percentDone < 60){
						color = "#fcea20";
					} else if (percentDone >= 60){
						color = "#54c40f";
					}
					
						
					$(".my-progress-bar").circularProgress({
						percent: percentDone,
						color : color
					});
			  		if(data.completed == "false"){
						sleep(1000); 
						uploadRefreshAsync();
					} else {
    					$('#progressmessage').html('<wp:i18n key="LABEL_FILE_UPLOADING_COMPLETING" />');
					}

				})
				.always(function(data) {					
				})
				.fail(function(data) {
				});
		}

		function isValidFilename(filename) {
			var reg = new RegExp("^[" + validFilenameChars + "]+$", "g");
			return (reg.test(filename));
		}
		
	});

function sleep(milliseconds) { 
    let timeStart = new Date().getTime(); 
    while (true) { 
      let elapsedTime = new Date().getTime() - timeStart; 
      if (elapsedTime > milliseconds) { 
        break; 
      } 
    } 
  } 

//--><!]]></script>

<div id="myModal" class="modal">
  <div class="modal-content">
	<div class="my-progress-bar"></div>
	<div id="progressmessage" class="progressmessage"></div>
  </div>
</div>