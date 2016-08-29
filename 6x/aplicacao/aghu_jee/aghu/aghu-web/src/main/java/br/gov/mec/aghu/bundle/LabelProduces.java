package br.gov.mec.aghu.bundle;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@SessionScoped
public class LabelProduces implements Serializable {
	
	private static final long serialVersionUID = 8627630516796240988L;
	
	private IParametroFacade parametroFacade=ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	private String alaLabel = obterValorDoLabel(AghuParametrosEnum.P_AGHU_LABEL_ALA, "Ala"); 
	private String quartoLabel = obterValorDoLabel(AghuParametrosEnum.P_AGHU_LABEL_QUARTO, "Quarto");
	
	public String obterValorDoLabel(AghuParametrosEnum param, String valorDefault) {
		String label = null;
		
		if (this.parametroFacade.verificarExisteAghParametro(param)) {
			label = parametroFacade.getAghParametro(param).getVlrTexto();
		} else {
			label=valorDefault;
		}
		
		return label;
	}


	@Produces @Named("LABEL_QUARTO") 
	public String obterLabelQuarto() {		
		return quartoLabel;
	}
	
	@Produces @Named("LABEL_ALA")
	public String obterLabelAla() {
		return alaLabel;
	}
	
	

}
