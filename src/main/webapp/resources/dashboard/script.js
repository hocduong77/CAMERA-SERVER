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
    })
})