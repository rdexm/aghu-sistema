package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Fachada para o modulo de integração do AGHU com sistema de gerência
 * eletrônica de documentos (Sistema Liquid)
 */
@Stateless
@Modulo(ModuloEnum.PACIENTES)
public class DigitalizacaoPOLFacade extends BaseFacade implements IDigitalizacaoPOLFacade {

	private static final long serialVersionUID = -7607093381789885314L;

	@EJB
	private IntegracaoGEDON integracaoGEDON;

	@Override
	@Secure("#{s:hasPermission('acessoDocsDigitalizadosAtivosPOL','acessar')}")
	public List<DocumentoGEDVO> buscaUrlsDocumentosGEDAtivos(ParametrosGEDVO parametros) throws ApplicationBusinessException{
		return getIntegracaoGEDON().obterUrlsDocumentosDigitalizados(parametros);
	}
	
	@Override
	@Secure("#{s:hasPermission('acessoDocsDigitalizadosInativosPOL','acessar')}")
	public List<DocumentoGEDVO> buscaUrlsDocumentosGEDInativos(ParametrosGEDVO parametros) throws ApplicationBusinessException{
		return getIntegracaoGEDON().obterUrlsDocumentosDigitalizados(parametros);
	}

	@Override
	@Secure("#{s:hasPermission('acessoDocsDigitalizadosAtivosPOL','acessar')}")
	public List<DocumentoGEDVO> buscaUrlsDocumentosGEDAdminstrativos(ParametrosGEDVO parametros) throws ApplicationBusinessException {
		return getIntegracaoGEDON().obterUrlsDocumentosDigitalizados(parametros);
	}

	
	public IntegracaoGEDON getIntegracaoGEDON() {
		return integracaoGEDON;
	} 
}
