package br.gov.mec.aghu.core.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "linha", createTag=true, tagName = "linha")
public class UILinha extends UICoreComponentBase {
   
	public static final String COMPONENT_FAMILY = "br.gov.mec.aghu.core.components.UILinha";
	public static final String COMPONENT_TYPE = "br.gov.mec.aghu.core.components.UILinha";
	
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
 
    	 String id = (String) getAttributes().get("id");
    	 String style = (String) getAttributes().get("style");
    	
    	ResponseWriter writer = context.getResponseWriter();    	
    	writer.startElement("div", this);
    	if (id!=null){
    		writer.writeAttribute("id", id, "id");    		
    	}
    	if (style!=null){
    		writer.writeAttribute("style", style, "style");    		
    	}
        writer.writeAttribute("class", "aghu-linha", "class");
    }

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");		
		super.encodeEnd(context);
	}
    
}