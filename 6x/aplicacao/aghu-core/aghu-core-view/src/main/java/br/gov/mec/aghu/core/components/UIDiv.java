package br.gov.mec.aghu.core.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "div", createTag=true, tagName = "div")
public class UIDiv extends UICoreComponentBase {
   
	public static final String COMPONENT_FAMILY = "br.gov.mec.aghu.core.components.UIDiv";
	public static final String COMPONENT_TYPE = "br.gov.mec.aghu.core.components.UIDiv";

	@Override
    public void encodeBegin(FacesContext context) throws IOException {
 
    	 String id = (String) getAttributes().get("id");
    	 String style = (String) getAttributes().get("style");
    	 String styleClass = (String) getAttributes().get("styleClass");
    	 String title = (String) getAttributes().get("title");
    	
    	ResponseWriter writer = context.getResponseWriter();    	
    	writer.startElement("div", this);
    	if (id!=null){
    		writer.writeAttribute("id", id, "id");    		
    	}
    	if (style!=null){
    		writer.writeAttribute("style", style, "style");    		
    	}
    	if (styleClass!=null){
    		writer.writeAttribute("class", styleClass, "class");    		
    	}
    	if (title!=null){
    		writer.writeAttribute("title", title, "title");    		
    	}    	
    }

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");		
		super.encodeEnd(context);
	}
    
}