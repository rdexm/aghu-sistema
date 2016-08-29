package br.gov.mec.aghu.core.components;

import javax.faces.component.UIComponentBase;

public class UICoreComponentBase extends UIComponentBase {
	public static final String AGHU_FAMILY="br.gov.aghu.core.components"; 
   
	@Override
    public String getFamily() {        
        return AGHU_FAMILY;
    }
    
}