/**
 *  aghu-pendencias.js -> Funções de customização da central de pendencias
 *  Autor: Cristiano Quadros  
 */


var pendencia = {
    custom : {
            closed: false,
            collapsible:false,
            minimizable:false,
            resizable:true,
            shadow:true,
            inline:false,
    },        

    resolve : function(id, title, url, bloqueante){
            if (bloqueante){
                var div="<div id='"+id+"' class='easyui-window' title='"+title+"' style='width:700px;height:450px;padding:4px;'></div>";
                var idFrame = util.createIdFrame(title.toLowerCase());
                var content = '<iframe id="'+idFrame+'" name="'+idFrame+'" scrolling="no" frameborder="0" border="no" src="'+url+'" class="frame-class"></iframe>';

                if (jQuery('div#'+id).length==0){
                        jQuery('body').append(div);
                        jQuery('div#'+id).append(content);
                }        
                jQuery('div#'+id).window(jQuery.extend({},pendencia.custom,{
                            modal: false,
                            closable: true,
                    }));

                pendencia.maximize(id);

            }else{
               PF('centralpendenciasWG').hide();               
               tab.addTab(id, title, url, null, '1');                        
            }                        
    },

    maximize : function(windowId){
        jQuery('div#'+windowId).window('maximize');
    }
};
