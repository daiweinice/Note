//由于我们已经通过npm将包下载到了node_modules, 所以我们可以直接导入
import $ from "jquery"

$(function(){
    $('li:odd').css('backgroundColor', 'pink');
    $('li:even').css('backgroundColor', 'lightgreen');
})