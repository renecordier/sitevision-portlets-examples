$(function(){
    $("#fileUpload").change(function(){
        previewImage(this);
    });
});

function previewImage(input){
    var reader = new FileReader();
    
    reader.onload = function(e){
        $("#tempImage").attr("src", e.target.result);
        $("#tempImage").show();
    }
    
    reader.readAsDataURL(input.files[0]);
}