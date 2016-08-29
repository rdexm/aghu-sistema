package br.gov.mec.aghu.certificacaodigital.business;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoPendenciaEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * @author gandriotti
 *
 */

@Stateless
public class CentralPendenciasBean	extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CentralPendenciasBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	protected enum MensagemPendenciaEnum {		
		MENSAGEM_ALERTA_PENDENCIA;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2911464557570724822L;
		
	protected static final String URL_PENDENCIAS_CERTIFICACAO_DIGITAL =	"/certificacaodigital/listarPendenciasAssinatura.xhtml";
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	protected ICascaFacade cascaFacade;

	protected TipoPendenciaEnum getTipoPendenciaCertificacaoDigital(boolean excedeLim, int qtdPend) {
	
		TipoPendenciaEnum result = null;
		
		result = TipoPendenciaEnum.IRRELEVANTE;
		if (excedeLim) {
			result = TipoPendenciaEnum.INTRUSIVA;
		} else {
			if (qtdPend > 0) {
				result = TipoPendenciaEnum.LEMBRETE;
			}
		}
		
		return result;
	}
	
	protected String getUrlMenu() {
		
		String result = null;
		
		//TODO migração arquitetura ver método
		//result = cascaFacade.obterRedirectUrlPorMenuUrl(URL_PENDENCIAS_CERTIFICACAO_DIGITAL);
		
		return result;
	}

	protected PendenciaVO getPendenciaCertificacaoDigital() {				

		PendenciaVO result = null;
		TipoPendenciaEnum tipo = null;
		String url = null;
		String mensagem = null;
		boolean excedeLim = false;
		Long qtdPend = 0l;
		int qtdDias = 0;
		
		if (this.certificacaoDigitalFacade.verificarCertificacaoDigitalHabilitado()) {
			// coletando dados
			excedeLim = this.certificacaoDigitalFacade.verificarNecessidadeResolverPendencias();
			qtdPend = this.certificacaoDigitalFacade.obterQuantidadeCertificadosPendentes();
			qtdDias = this.certificacaoDigitalFacade.obterDiasPendenciaMaisAntiga();
			// tipo
			tipo = this.getTipoPendenciaCertificacaoDigital(excedeLim, qtdPend.intValue());
			// url
			url = this.getUrlMenu();
			// mensagem
			mensagem = MessageFormat.format(getResourceBundleValue(
					MensagemPendenciaEnum.MENSAGEM_ALERTA_PENDENCIA.name()), 
					qtdPend.intValue(),
					Integer.valueOf(qtdDias));
			// vo
			result = new PendenciaVO(tipo, url, mensagem);			
		}
		
		return result;
	}

	protected List<PendenciaVO> getListaPendencias() {
		
		List<PendenciaVO> result = null;
		PendenciaVO vo = null;
		
		result = new LinkedList<PendenciaVO>();
		vo = this.getPendenciaCertificacaoDigital();
		if (vo != null) {
			result.add(vo);			
		}

		return result;
	}
}
