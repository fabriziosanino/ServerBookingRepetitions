$(document).ready(function(){

  console.log("inizo");


  $("#nav-tab a").click(function(){

    $("#nav-tab a").each(function (){
      $(this).removeClass("active");
    })
    $(this).addClass("active");
  });
});
