package br.gov.mec.aghu.sicon.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RetornoIntegracaoController extends ActionController {

	private static final long serialVersionUID = -4275847264416724996L;
	
	private static final String PAGE_GERENCIAR_INTEGRACAO_SICON = "gerenciarIntegracaoSicon";
	private static final String PAGE_RETORNO_INTEGRACAO_VISUALIZAR_PDF = "retornoIntegracaoVisualizarPdf";

	/** The sicon facade. */
	@EJB
	private ISiconFacade siconFacade;

	/** The sco contrato. */
	private ScoContrato scoContrato;

	/** log */
	private ScoLogEnvioSicon logEnvioSicon;

	/** logs */
	private List<ScoLogEnvioSicon> logsContratos;
	private List<ScoLogEnvioSicon> logsAditivos;
	private List<ScoLogEnvioSicon> logsRescicao;

	/** parâmetro número do contrato */
	private Integer contSeq;
	
	private String tabSelecionada;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void init() {
	 

		try {
            if 	(getContSeq()==null) {
    			this.apresentarMsgNegocio(Severity.ERROR,
				   "MSG_SELECIONAR_CONTRATO");
            } else {
    			this.scoContrato = siconFacade.getContrato(getContSeq());
    			this.logsContratos = this.pesquisarRetornoIntegracaoContratos(this.scoContrato);
    	        this.logsAditivos  = this.pesquisarRetornoIntegracaoAditivos(this.scoContrato);
    	        this.logsRescicao = this.pesquisarRetornoIntegracaoRescicao(this.scoContrato);
            }

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	
	}

	/**
	 * Voltar.
	 * 
	 * @return the string
	 */
	public String voltar() {
		return PAGE_GERENCIAR_INTEGRACAO_SICON;
	}
	
	public String visualizarPDF(){
		return PAGE_RETORNO_INTEGRACAO_VISUALIZAR_PDF;
	}

	/**
	 * Gets the cpf cnpj.
	 * 
	 * @return the cpf cnpj
	 */
	public String getCpfCnpj() {

		if (this.scoContrato != null
				&& this.scoContrato.getFornecedor() != null
				&& this.scoContrato.getFornecedor().getCgc() != null) {
			return CoreUtil.formatarCNPJ(this.scoContrato.getFornecedor()
					.getCgc());
		} else if (this.scoContrato != null
				&& this.scoContrato.getFornecedor() != null
				&& this.scoContrato.getFornecedor().getCpf() != null) {
			return CoreUtil.formataCPF(this.scoContrato.getFornecedor()
					.getCpf());
		}
		
		return "";
	}
	
    /**
     * Pesquisar retorno de integração para envio de contratos
     * @param contrato
     * @return List<ScoLogEnvioSicon>
     */
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoContratos(
			ScoContrato contrato) {
		return this.siconFacade.pesquisarRetornoIntegracaoContratos(contrato) ;
	}

    /**
     * Pesquisar retorno de integração para envio dos aditivos
     * @param contrato
     * @return List<ScoLogEnvioSicon>
     */
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoAditivos(
			ScoContrato contrato) {
		return this.siconFacade.pesquisarRetornoIntegracaoAditivos(contrato) ;
	}

    /**
     * Pesquisar retorno de integração para envio da rescição
     * @param contrato
     * @return List<ScoLogEnvioSicon>
     */	
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoRescicao(
			ScoContrato contrato) {
		return this.siconFacade.pesquisarRetornoIntegracaoRescicao(contrato) ;
	}
	

	/**
	 * Gets the sco contrato.
	 * 
	 * @return the sco contrato
	 */
	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	/**
	 * Sets the sco contrato.
	 * 
	 * @param scoContrato
	 *            the new sco contrato
	 */
	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}

	public ScoLogEnvioSicon getLogEnvioSicon() {
		return logEnvioSicon;
	}

	public void setLogEnvioSicon(ScoLogEnvioSicon logEnvioSicon) {
		this.logEnvioSicon = logEnvioSicon;
	}

	public String getTabSelecionada() {
		return tabSelecionada;
	}

	public void setTabSelecionada(String tabSelecionada) {
		this.tabSelecionada = tabSelecionada;
	}

	public List<ScoLogEnvioSicon> getLogsContratos() {
		return logsContratos;
	}

	public void setLogsContratos(List<ScoLogEnvioSicon> logsContratos) {
		this.logsContratos = logsContratos;
	}

	public List<ScoLogEnvioSicon> getLogsAditivos() {
		return logsAditivos;
	}

	public void setLogsAditivos(List<ScoLogEnvioSicon> logsAditivos) {
		this.logsAditivos = logsAditivos;
	}

	public List<ScoLogEnvioSicon> getLogsRescicao() {
		return logsRescicao;
	}

	public void setLogsRescicao(List<ScoLogEnvioSicon> logsRescicao) {
		this.logsRescicao = logsRescicao;
	}

	public Integer getContSeq() {
		return contSeq;
	}

	public void setContSeq(Integer contSeq) {
		this.contSeq = contSeq;
	}

}
