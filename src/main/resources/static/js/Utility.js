
function buildJsonFromFormData(selector) {
    var ary = $(selector).serializeArray();
    var obj = {};
    for (var a = 0; a < ary.length; a++)
        obj[ary[a].name] = ary[a].value;
    return JSON.stringify(obj);
}




function makePostRequest(url, data, loader, callback) {
    var ctx = window.location.protocol + "//" + window.location.host;
    $.ajax({
        url : ctx + url,
        type : "POST",
        data : data,
        dataType : "json",
        contentType : "application/json",
        beforeSend : function() {
            $(loader).show();
        },
        success : function(data) {
            callback(data); // return data in callback
        },
        complete : function() {
            $(loader).hide();
        },
        error : function(xhr, status, error) {
            console.log(xhr.responseText); // error occur
            var errorResp = JSON.parse(xhr.responseText);
            document.getElementById("response").innerHTML = errorResp.responseMessage;
            document.getElementById("success").innerHTML = "";
        }
    });
}
