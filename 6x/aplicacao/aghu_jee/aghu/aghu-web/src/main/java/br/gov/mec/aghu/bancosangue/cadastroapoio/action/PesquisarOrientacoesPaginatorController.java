package br.gov.mec.aghu.bancosangue.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarOrientacoesPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 4282877415523802264L;

	private static final String MANTER_ORIENTACOES = "manterOrientacoes";
	private static final String MANTER_COMPONENTES_SANGUINEOS = "prescricaomedica-manterComponentesSanguineos";
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	
	
	// Filtros pesquisa
	private Short seqOrientacoes;
	private AbsComponenteSanguineo componenteSanguineo;
	private AbsOrientacaoComponentes absOrientacaoComponentes;
	private String descricaoOrientacoes;
	private DominioSituacao situacaoOrientacoes;
	private String componenteSanguineoCodigo;

	
	@Inject @Paginator
	private DynamicDataModel<AbsOrientacaoComponentes> dataModel;
	
	private AbsOrientacaoComponentes selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

	 

		if(componenteSanguineoCodigo != null){
			componenteSanguineo = bancoDeSangueFacade.obterComponenteSanguineosPorCodigo(componenteSanguineoCodigo);
			dataModel.reiniciarPaginator();
		}
	
	}
	
	
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(String param) {
		return  this.returnSGWithCount(bancoDeSangueFacade.obterComponenteSanguineos(param),pesquisarComponenteSanguineoCount(param));
	}
	
	public Long pesquisarComponenteSanguineoCount(String param) {
		return bancoDeSangueFacade.obterComponenteSanguineosCount(param);
	}

	public String voltarManterComponentesSanguineos(){
		return MANTER_COMPONENTES_SANGUINEOS;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){		
		dataModel.limparPesquisa();
		descricaoOrientacoes = null;
		situacaoOrientacoes = null;
		seqOrientacoes = null;
	}
	
	public String inserir(){
		return MANTER_ORIENTACOES;
	}
	
	public String editar(){
		return MANTER_ORIENTACOES;
	}

	@Override
	public List<AbsOrientacaoComponentes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(componenteSanguineo == null){
			componenteSanguineo = bancoDeSangueFacade.obterComponenteSanguineosPorCodigo(componenteSanguineoCodigo);
		}
		return bancoDeSangueFacade.listaOrientacoes(firstResult, maxResult, orderProperty, true, seqOrientacoes, descricaoOrientacoes, situacaoOrientacoes, componenteSanguineo.getCodigo());
	}

	@Override
	public Long recuperarCount() {
		if(componenteSanguineo == null){
			componenteSanguineo = bancoDeSangueFacade.obterComponenteSanguineosPorCodigo(componenteSanguineoCodigo);
		}
		return bancoDeSangueFacade.pesquisarOrientacaoComponentesCount(seqOrientacoes, descricaoOrientacoes, situacaoOrientacoes, componenteSanguineo.getCodigo());
	}
	
	public Short getSeqOrientacoes() {
		return seqOrientacoes;
	}

	public void setSeqOrientacoes(Short seqOrientacoes) {
		this.seqOrientacoes = seqOrientacoes;
	}

	public String getDescricaoOrientacoes() {
		return descricaoOrientacoes;
	}

	public void setDescricaoOrientacoes(String descricaoOrientacoes) {
		this.descricaoOrientacoes = descricaoOrientacoes;
	}

	public DominioSituacao getSituacaoOrientacoes() {
		return situacaoOrientacoes;
	}

	public void setSituacaoOrientacoes(DominioSituacao situacaoOrientacoes) {
		this.situacaoOrientacoes = situacaoOrientacoes;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineoCodigo(String componenteSanguineoCodigo) {
		this.componenteSanguineoCodigo = componenteSanguineoCodigo;
	}

	public String getComponenteSanguineoCodigo() {
		return componenteSanguineoCodigo;
	}

	public void setAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) {
		this.absOrientacaoComponentes = absOrientacaoComponentes;
	}

	public AbsOrientacaoComponentes getAbsOrientacaoComponentes() {
		return absOrientacaoComponentes;
	}

	public DynamicDataModel<AbsOrientacaoComponentes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AbsOrientacaoComponentes> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsOrientacaoComponentes getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AbsOrientacaoComponentes selecionado) {
		this.selecionado = selecionado;
	} 
}