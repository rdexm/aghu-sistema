package br.gov.mec.aghu.sicon.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe para o Controller do cadastro de contratos manuais.
 * 
 * @author agerling
 * 
 */

public class GerenciaContratoManualController extends ActionController {

	private static final Log LOG = LogFactory.getLog(GerenciaContratoManualController.class);

	private static final String PAGE_RETORNO_AGH = "retornoAGH";
	private static final String PAGE_MANTER_CONTRATO_MANUAL = "manterContratoManual";
	private static final String PAGE_MANTER_RESCISAO_CONTRATO = "manterRescisaoContrato";
	private static final String PAGE_MANTER_ITENS_CONTRATO = "manterItensContrato";
	private static final String PAGE_MANTER_ADITIVO_CONTRATUAL = "manterAditivoContratual";

	private static final long serialVersionUID = -8209082339666317995L;
	private ScoContrato contrato;
	private String xmlContrato;
	private String xmlResponse;

	@EJB
	ISiconFacade siconFacade;

	private DadosEnvioVO dadosEnvioVO;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		contrato = new ScoContrato();
		xmlContrato = "";
	
	}

	public void enviarSicon() {
		try {
			siconFacade.integrarSIASG(dadosEnvioVO, contrato.getSeq(), false);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public String cancelarGerenciamento() {
		return PAGE_RETORNO_AGH;
	}

	public String editarContratoManual() {
		return PAGE_MANTER_CONTRATO_MANUAL;
	}

	public String goRescicao() {
		return PAGE_MANTER_RESCISAO_CONTRATO;
	}

	public String goManterItens() {
		return PAGE_MANTER_ITENS_CONTRATO;
	}

	public String goManterAditivos() {
		return PAGE_MANTER_ADITIVO_CONTRATUAL;
	}

	public void goEnviarContrato() {
		try {
			siconFacade.gerarXml(contrato.getSeq(), "");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public String getXmlContrato() {
		return xmlContrato;
	}

	public void setXmlContrato(String xmlContrato) {
		this.xmlContrato = xmlContrato;
	}

	public String getXmlResponse() {
		return xmlResponse;
	}

	public void setXmlResponse(String xmlResponse) {
		this.xmlResponse = xmlResponse;
	}

	public DadosEnvioVO getDadosEnvioVO() {
		return dadosEnvioVO;
	}

	public void setDadosEnvioVO(DadosEnvioVO dadosEnvioVO) {
		this.dadosEnvioVO = dadosEnvioVO;
	}

}
