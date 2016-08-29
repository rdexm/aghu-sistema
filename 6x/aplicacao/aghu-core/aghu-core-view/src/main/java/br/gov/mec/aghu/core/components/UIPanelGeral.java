package br.gov.mec.aghu.core.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(value = "panelGeral", createTag=true, tagName = "panelGeral")
public class UIPanelGeral extends UICoreComponentBase {
 
	public static final String COMPONENT_FAMILY = "br.gov.mec.aghu.core.components.UIPanelGeral";
	public static final String COMPONENT_TYPE = "br.gov.mec.aghu.core.components.UIPanelGeral";
	
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
 
    	String id = (String) getAttributes().get("id");
    	String legend = (String) getAttributes().get("legend");
    	String style = (String) getAttributes().get("style");
    	
    	ResponseWriter writer = context.getResponseWriter();    	
    	writer.startElement("fieldset", this);
    	writer.writeAttribute("class", "ui-fieldset ui-widget ui-widget-content ui-corner-all ui-hidden-container aghu-ui-panel-geral", "class");
		if (style!=null){
			writer.writeAttribute("style", style, "style");
		}    	
    	if (id!=null){
    		writer.writeAttribute("id", id, "id");    		
    	}
    	if (legend!=null){
    		writer.startElement("legend", this);
    		writer.writeAttribute("class", "ui-fieldset-legend ui-corner-all ui-state-default", "class");
    		writer.append(legend);
    		writer.endElement("legend");
    	}
    	writer.startElement("div", this);
		writer.writeAttribute("class", "ui-fieldset-content", "class");
    }

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("fieldset");		
		super.encodeEnd(context);
	}
    
}