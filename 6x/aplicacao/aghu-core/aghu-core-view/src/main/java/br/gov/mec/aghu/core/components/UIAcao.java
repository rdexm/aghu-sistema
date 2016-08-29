package br.gov.mec.aghu.core.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "acao", createTag=true, tagName = "acao")
public class UIAcao extends UICoreComponentBase {
   
	public static final String COMPONENT_FAMILY = "br.gov.mec.aghu.core.components.UIAcao";
	public static final String COMPONENT_TYPE = "br.gov.mec.aghu.core.components.UIAcao";
	
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
 
    	 String id = (String) getAttributes().get("id");
    	 String style = (String) getAttributes().get("style");
    	 Boolean newLine = true;
    	 if (getAttributes().get("newLine")!=null){
    		 newLine=Boolean.valueOf((String)getAttributes().get("newLine"));
    	 }    	
    	ResponseWriter writer = context.getResponseWriter();    	
    	writer.startElement("div", this);
    	if (id!=null){
    		writer.writeAttribute("id", id, "id");    		
    	}
    	if (style!=null){
    		writer.writeAttribute("style", style, "style");    		
    	}
    	if (newLine){
    		writer.writeAttribute("class", "ui-widget-content aghu-acao", "class");
    	}else{
    		writer.writeAttribute("class", "ui-widget-content aghu-acao-lateral", "class");
    	}
    }

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");		
		super.encodeEnd(context);
	}
    
}