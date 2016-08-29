package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.administracao.business.VAinPesqPacOV;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.CidInternacaoVO;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações da tela de
 * pesquisaDetalheInternação
 * 
 */


public class PesquisaDetalheInternacaoController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1247426422143648283L;
	private Integer intSeq;
	private Short numero;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	private Integer codInternacao;

	@Inject @Paginator
	private DynamicDataModel<CidInternacaoVO> dataModel;
	
	
	/**
	 * Atendimento Urgência para edição
	 */
	private AinAtendimentosUrgencia ainAtendimentosUrgencia = new AinAtendimentosUrgencia();

	/**
	 * Atributos dos campo de filtro da pesquisa do Atendimento Urgência.
	 */
	private Boolean exibirBotaoDetalharInternacao;

	private VAinPesqPacOV vAinPesqPacOV = new VAinPesqPacOV();
	
	
	private static final String PESQUISA_LEITOS = "pesquisaLeitos";
	

	/**
	 * Metodo executado na inicializacao da pagina. Se paciente existe no
	 * contexto, pega seus dados para executar a pesquisa.
	 * 
	 * @throws AGHUNegocioException
	 */
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void inicio(){
	 

		try {
			if (this.intSeq != null) {
				this.vAinPesqPacOV = pesquisaInternacaoFacade.pesquisaDetalheInternacao(intSeq);
				this.setCodInternacao(vAinPesqPacOV.getSeq());
				pesquisaCidsInternacao();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public void detalharAtendimentoUrgencia() {

		try {
			this.ainAtendimentosUrgencia = (pesquisaInternacaoFacade
					.obterDetalheAtendimentoUrgencia(this.vAinPesqPacOV
							.getSeqAntendimentoUrgencia()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return	pesquisaInternacaoFacade.pesquisaCidsInternacaoCount(codInternacao);
	}

	
	@Override
	public List<CidInternacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return pesquisaInternacaoFacade.pesquisaCidsInternacao(firstResult, maxResult, orderProperty, asc, codInternacao);
	}
	
	public String retornarPesquisarLeito(){
		return PESQUISA_LEITOS;
	}
	
	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public void pesquisaCidsInternacao() {
		dataModel.reiniciarPaginator();
	}

	// ### GETs e SETs ###

	public VAinPesqPacOV getvAinPesqPacOV() {
		return vAinPesqPacOV;
	}

	public void setvAinPesqPacOV(VAinPesqPacOV vAinPesqPacOV) {
		this.vAinPesqPacOV = vAinPesqPacOV;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	public void setPesquisaInternacaoFacade(
			IPesquisaInternacaoFacade pesquisaInternacaoFacade) {
		this.pesquisaInternacaoFacade = pesquisaInternacaoFacade;
	}

	public AinAtendimentosUrgencia getAinAtendimentosUrgencia() {
		return ainAtendimentosUrgencia;
	}

	public void setAinAtendimentosUrgencia(
			AinAtendimentosUrgencia ainAtendimentosUrgencia) {
		this.ainAtendimentosUrgencia = ainAtendimentosUrgencia;
	}

	public Boolean getExibirBotaoDetalharInternacao() {
		return exibirBotaoDetalharInternacao;
	}

	public void setExibirBotaoDetalharInternacao(
			Boolean exibirBotaoDetalharInternacao) {
		this.exibirBotaoDetalharInternacao = exibirBotaoDetalharInternacao;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Integer getCodInternacao() {
		return codInternacao;
	}

	public void setCodInternacao(Integer codInternacao) {
		this.codInternacao = codInternacao;
	}

	public DynamicDataModel<CidInternacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CidInternacaoVO> dataModel) {
		this.dataModel = dataModel;
	}

}
