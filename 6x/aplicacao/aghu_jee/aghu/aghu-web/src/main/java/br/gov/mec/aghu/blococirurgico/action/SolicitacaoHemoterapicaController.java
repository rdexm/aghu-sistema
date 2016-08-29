package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitacaoHemoterapicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = -7102047377830610820L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	private Integer pCrgSeq;
	
	private Boolean edicao; 
	
	private MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendada = new MbcSolicHemoCirgAgendada();
	private MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao;
	private MbcSolicHemoCirgAgendada id;
	
	private MbcProcEspPorCirurgias mbcProcEspPorCirurgia;
		
	private List<MbcSolicHemoCirgAgendada> listaSolicHemoterapicos;
	
	private final String PAGE_EDITAR_SOLICITACAO_HEMOTERAPICA = "solicitacaoHemoterapicaCRUD";

	protected void inicializaParametros(Integer parametroCrgSeq) {
		edicao = cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "detalheRegistroDeCirurgiasAbaSolHemoterapica","editar");
		this.pCrgSeq = parametroCrgSeq;
		pesquisar();
	}
	
	public void pesquisar() {
		listaSolicHemoterapicos = blocoCirurgicoFacade.pesquisarSolicHemoterapica(pCrgSeq);
		blocoCirurgicoFacade.refreshListMbcSolicHemoCirgAgendada(listaSolicHemoterapicos);
		mbcProcEspPorCirurgia = blocoCirurgicoFacade.pesquisarTipagemSanguinea(pCrgSeq);

	}
	
	public void excluir() {
		try {
			String msgRetorno = this.blocoCirurgicoFacade.excluirMbcSolicHemoCirgAgendada(mbcSolicHemoCirgAgendadaDelecao);			
			this.apresentarMsgNegocio(Severity.INFO,msgRetorno);
			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		return PAGE_EDITAR_SOLICITACAO_HEMOTERAPICA;
	}
	
	public void selecionaMbcSolicHemo(MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao){
		this.mbcSolicHemoCirgAgendadaDelecao = mbcSolicHemoCirgAgendadaDelecao;
	}

	public String encaminharSolicitacaoHemoterapicaCRUD(){
		return PAGE_EDITAR_SOLICITACAO_HEMOTERAPICA;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}
	
	public MbcSolicHemoCirgAgendada getMbcSolicHemoCirgAgendada() {
		return mbcSolicHemoCirgAgendada;
	}

	public void setMbcSolicHemoCirgAgendada(
			MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendada) {
		this.mbcSolicHemoCirgAgendada = mbcSolicHemoCirgAgendada;
	}

	public MbcSolicHemoCirgAgendada getMbcSolicHemoCirgAgendadaDelecao() {
		return mbcSolicHemoCirgAgendadaDelecao;
	}

	public void setMbcSolicHemoCirgAgendadaDelecao(
			MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao) {
		this.mbcSolicHemoCirgAgendadaDelecao = mbcSolicHemoCirgAgendadaDelecao;
	}

	public MbcSolicHemoCirgAgendada getId() {
		return id;
	}

	public void setId(MbcSolicHemoCirgAgendada id) {
		this.id = id;
	}

	public List<MbcSolicHemoCirgAgendada> getListaSolicHemoterapicos() {
		return listaSolicHemoterapicos;
	}

	public void setListaSolicHemoterapicos(
			List<MbcSolicHemoCirgAgendada> listaSolicHemoterapicos) {
		this.listaSolicHemoterapicos = listaSolicHemoterapicos;
	}

	public void setpCrgSeq(Integer pCrgSeq) {
		this.pCrgSeq = pCrgSeq;
	}

	public Integer getpCrgSeq() {
		return pCrgSeq;
	}

	public void setMbcProcEspPorCirurgia(MbcProcEspPorCirurgias mbcProcEspPorCirurgia) {
		this.mbcProcEspPorCirurgia = mbcProcEspPorCirurgia;
	}

	public MbcProcEspPorCirurgias getMbcProcEspPorCirurgia() {
		return mbcProcEspPorCirurgia;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	
}