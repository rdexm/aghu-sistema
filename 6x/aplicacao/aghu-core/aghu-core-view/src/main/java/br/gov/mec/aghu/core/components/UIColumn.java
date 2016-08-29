package br.gov.mec.aghu.core.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "column", createTag=true, tagName = "column")
public class UIColumn extends UICoreComponentBase {
   
	public static final String COMPONENT_FAMILY = "br.gov.mec.aghu.core.components.UIColumn";
	public static final String COMPONENT_TYPE = "br.gov.mec.aghu.core.components.UIColumn";
	
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
 
    	 String id = (String) getAttributes().get("id");
    	 String style = (String) getAttributes().get("style");
    	 String width = (String) getAttributes().get("width");
    	 String type = (String) getAttributes().get("type");
    	 
    	 if (style==null){
    		 style="";
    	 }
    	 if (width!=null){
    		 style = "width:"+width+";".concat(style);
    	 }
    	ResponseWriter writer = context.getResponseWriter();    	
    	writer.startElement("div", this);
    	if (id!=null){
    		writer.writeAttribute("id", id, "id");    		
    	}
    	if (style!=null && !style.isEmpty()){
    		writer.writeAttribute("style", style, "style");    		
    	}
    	if ("table".equalsIgnoreCase(type)){
    		writer.writeAttribute("class", "aghu-column aghu-column-table", "class");
    	}else if("button".equalsIgnoreCase(type)){
    		writer.writeAttribute("class", "aghu-column aghu-column-button", "class");
    	}else{
    		writer.writeAttribute("class", "aghu-column", "class");
    	}
    }

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");		
		super.encodeEnd(context);
	}
    
}