/**
 * Created by Quyen.Tran on 7/31/2016.
 */
$(document).ready(function(){
    $('.play-stop').click(function () {
        var isCurr = $(this);
        if(isCurr.hasClass('curr-play')) {
            isCurr.removeClass('curr-play').addClass('curr-stop');
        } else {
            isCurr.removeClass('curr-stop').addClass('curr-play');
        }
    });
    $('.record').click(function () {
        var curr_active = $(this);
        if(curr_active.hasClass('active')) {
            curr_active.removeClass('active');
        } else {
            curr_active.addClass('active');
        }
    });
    
    
    $(document).ready(function(){
        fancyPopup();
    });

    function fancyPopup() {
        // Declare some variables.
        var el = "";
        var posterPath = "";
        var replacement = "";
        var videoTag = "";
        var fancyBoxId = "";
        var posterPath = "";
        var videoTitle = "";

        // Loop over each video tag.
        $("video").each(function () {
            // Reset the variables to empty.
            el = "";
            posterPath = "";
            replacement = "";
            videoTag = "";
            fancyBoxId = "";
            posterPath = "";
            videoTitle = "";

            // Get a reference to the current object.
            el = $(this);

            // Set some values we'll use shortly.
            fancyBoxId = this.id + "_fancyBox";
            videoTag = el.parent().html();      // This gets the current video tag and stores it.
            posterPath = el.attr("poster");
            videoTitle = "Play Video " + this.id;

            
            // Concatenate the linked image that will take the place of the <video> tag.
            //replacement = "<a title='" + videoTitle + "' id='" + fancyBoxId + "' href='javascript:;'><img src='" +
            //    posterPath + "' class='img-link'/></a>"

            // Replace the parent of the current element with the linked image HTML.
            //el.parent().replaceWith(replacement);

            /*
            Now attach a Fancybox to this item and set its attributes. 
               
            This entire function acts as an onClick handler for the object to
            which it's attached (hence the "end click function" comment).
            */
            $("[id=" + fancyBoxId + "]").fancybox(
            {
                'content': videoTag,
                'title': videoTitle,
                'autoDimensions': true,
                'padding': 5,
                'showCloseButton': true,
                'enableEscapeButton': true,
                'titlePosition': 'outside',
            }); // end click function
        });
    }
   /* $('.video').fancybox({
    	
    });*/
})