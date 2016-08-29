package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class AvaliacaoPropostasParecerTecnicoPaginatorController extends ActionController implements ActionPaginator {

	private static final String PARECER_CRUD = "parecerCRUD";

	private static final String ANALISE_TECNICA_ITEM_PROPOSTA_FORNECEDOR_CRUD = "analiseTecnicaItemPropostaFornecedorCRUD";

	private static final String AVALIACAO_PAC_PARECER_TECNICO_LIST = "avaliacaoPacParecerTecnicoList";

	@Inject @Paginator
	private DynamicDataModel<PropFornecAvalParecerVO> dataModel;
	
	private static final long serialVersionUID = -729445225588120797L;
	
	private enum AvaliacaoPropostasParecerTecnicoPaginatorControllerExceptionCode implements BusinessExceptionCode {
		TITLE_TOOLTIP_VENC_DATA_VENCIDA
	}

	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IParecerFacade parecerFacade;
	
	private Boolean refazerPesquisa = Boolean.FALSE; 
	
	// filtros
	Integer numeroPac;
	String descricaoPac;
	ScoModalidadeLicitacao modalidadePac;	
	DominioSituacaoParecer situacaoParecer;
	List<DominioSituacaoParecer> listaSituacaoParecer;
	ScoLicitacao scoLicitacao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void iniciar() {
	 

	 

		
		if(!this.isAtivo()){
			this.setScoLicitacao(this.pacFacade.obterLicitacao(this.numeroPac));
			this.limparPesquisar();
		}
		
		if(this.getRefazerPesquisa()){
			this.pesquisarNovamente();
		}
	
	}	
	
	public void setaValorListaSituacaoParecer(){

		listaSituacaoParecer = new ArrayList<DominioSituacaoParecer>();
		if (DominioSituacaoParecer.AN.equals(getSituacaoParecer())){			
			listaSituacaoParecer.add(DominioSituacaoParecer.PD);
			listaSituacaoParecer.add(DominioSituacaoParecer.EA);
			listaSituacaoParecer.add(DominioSituacaoParecer.SP);
		}
		else {
			listaSituacaoParecer.add(getSituacaoParecer());
		}		
	}
	
	public void limparPesquisar(){
		this.setSituacaoParecer(DominioSituacaoParecer.AN);
		this.setDescricaoPac(this.getScoLicitacao().getDescricao());
		this.setModalidadePac(this.getScoLicitacao().getModalidadeLicitacao());				
		this.pesquisar();
		
	}
	
	public void pesquisar() {		
		this.setaValorListaSituacaoParecer();	
		this.reiniciarPaginator();
	}
	public void pesquisarNovamente(){
		this.setRefazerPesquisa(Boolean.FALSE);		
		this.reiniciarPaginator();
		
	}
	
	

	// suggestions
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter);
	}	
	
	
	// paginator

	@Override
	public Long recuperarCount() {
		return  this.parecerFacade.contarItensPropostaFornecedorPAC(this.getNumeroPac(), this.getListaSituacaoParecer());	
	}

	@Override
	public List<PropFornecAvalParecerVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		
		return this.parecerFacade.pesquisarItensPropostaFornecedorPAC(firstResult, maxResult, order, asc, this.getNumeroPac(), this.getListaSituacaoParecer());
		
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public Boolean isDataVencida(Date data){
		if (data !=null){
			return (data.before(new Date()));
		}
		return false;
		
	}
	public String obterMensagemDataVencida(Date data){
		if (isDataVencida(data)){
				return this.getBundle().getString(AvaliacaoPropostasParecerTecnicoPaginatorControllerExceptionCode.TITLE_TOOLTIP_VENC_DATA_VENCIDA.toString());		
		}
		return "";
	}
	
	public String voltar(){
		return AVALIACAO_PAC_PARECER_TECNICO_LIST;
	}
	
	public String avaliarMarcaItem(){
		return ANALISE_TECNICA_ITEM_PROPOSTA_FORNECEDOR_CRUD;
	}
	
	public String verificarParecerTecnico(){
		return PARECER_CRUD;
	}
	
	
	// Getters/Setters
	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IPacFacade getPacFacade() {
		return pacFacade;
	}

	public void setPacFacade(IPacFacade pacFacade) {
		this.pacFacade = pacFacade;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public ScoModalidadeLicitacao getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(ScoModalidadeLicitacao modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public DominioSituacaoParecer getSituacaoParecer() {
		return situacaoParecer;
	}

	public void setSituacaoParecer(DominioSituacaoParecer situacaoParecer) {
		this.situacaoParecer = situacaoParecer;
	}

	public List<DominioSituacaoParecer> getListaSituacaoParecer() {
		return listaSituacaoParecer;
	}

	public void setListaSituacaoParecer(
			List<DominioSituacaoParecer> listaSituacaoParecer) {
		this.listaSituacaoParecer = listaSituacaoParecer;
	}

	public ScoLicitacao getScoLicitacao() {
		return scoLicitacao;
	}

	public void setScoLicitacao(ScoLicitacao scoLicitacao) {
		this.scoLicitacao = scoLicitacao;
	}

	public Boolean getRefazerPesquisa() {
		return refazerPesquisa;
	}

	public void setRefazerPesquisa(Boolean refazerPesquisa) {
		this.refazerPesquisa = refazerPesquisa;
	}
	
	public DynamicDataModel<PropFornecAvalParecerVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PropFornecAvalParecerVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}