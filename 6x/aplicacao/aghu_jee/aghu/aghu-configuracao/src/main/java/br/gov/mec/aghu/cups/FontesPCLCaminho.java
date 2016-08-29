package br.gov.mec.aghu.cups;

import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class FontesPCLCaminho {
	
	@Inject 
	private IParametroFacade parametroFacade;

	
	/**
	 * Retorna o Caminho da Fonte Selecionada.<br>
	 *  
	 */
	public String getCaminhoFonte(FontesPCL fontePCL) throws ApplicationBusinessException {
//		return ( "C:/aghu/jboss-5.1.0.GA/bin/etiquetas/fontes/" + nomeArquivoFonte );
		String nomeArquivoFonte = fontePCL.getNomeArquivoFonte();
		
		String caminhoRelativo = getParametroFacade().buscarAghParametro( AghuParametrosEnum.P_CUPS_DIRETORIO_RELATORIO ).getVlrTexto();		
		
		return ( System.getProperty("user.home") + caminhoRelativo + "etiquetas/fontes/" + nomeArquivoFonte );
		
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
}
