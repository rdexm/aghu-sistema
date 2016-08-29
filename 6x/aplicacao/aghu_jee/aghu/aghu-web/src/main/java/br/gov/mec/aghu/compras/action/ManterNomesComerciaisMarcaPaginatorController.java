package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterNomesComerciaisMarcaPaginatorController extends ActionController implements ActionPaginator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503907181939987785L;

	@EJB
	private IComprasFacade comprasFacade;
	
	private ScoMarcaComercial marcaComercial;
	private String nome;
	private boolean active;
	private boolean editionMode;
	private boolean insertionMode;
	private ScoNomeComercial nomeSobEdicao;
	@Inject @Paginator
	private DynamicDataModel<AipCidades> dataModel;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		setActive(Boolean.TRUE);
		setInsertionMode(Boolean.TRUE);
		setEditionMode(Boolean.FALSE);
	
	}
	
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
		setMarcaComercial(null);
		setNome(null);
		setActive(Boolean.TRUE);
		setNomeSobEdicao(null);
		setInsertionMode(Boolean.TRUE);
		setEditionMode(Boolean.FALSE);
	}
	
	public void adicionar(){
		if(marcaComercial == null){
			apresentarMsgNegocio(Severity.ERROR, "Um valor é obrigatório para o campo Marca.");
		}else{
			executaAdicionar();
		}
	}

	private void executaAdicionar() {
		try {
			comprasFacade.cadastrarNomeComercialDaMarca(marcaComercial, getNome(), isActive());
//			comprasFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_NOMES_COMERCIAS_MARCA_M4");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void setEditonMode(ScoNomeComercial item){
		nomeSobEdicao = item;
		setNome(item.getNome());
		setActive(buscaActive(item.getSituacao()));
		setEditionMode(Boolean.TRUE);
		setInsertionMode(Boolean.FALSE);
	}
	
	public void alterar(){
		try {
			comprasFacade.alterarNovoNomeComercialDaMarca(nomeSobEdicao, getNome(), isActive());
//			comprasFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_NOMES_COMERCIAS_MARCA_M5");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelar(){
		nomeSobEdicao = null;
		setNome(null);
		setActive(Boolean.TRUE);
		setEditionMode(Boolean.FALSE);
		setInsertionMode(Boolean.TRUE);
	}
	
	private boolean buscaActive(DominioSituacao situacao) {
		return DominioSituacao.A.equals(situacao);
	}
	
	@Override
	public Long recuperarCount() {
		return comprasFacade.buscaMarcasComeriaisCount(marcaComercial);
	}

	@Override
	public List<ScoNomeComercial> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<ScoNomeComercial> list =  comprasFacade.buscaMarcasComeriais(firstResult, maxResult, orderProperty, asc, marcaComercial);
		if(list.size() > 0){
			this.dataModel.setPesquisaAtiva(Boolean.TRUE);
		}
		return list;
	}
	
	public List<ScoMarcaComercial> pesquisarMarcas(String param) {
		return this.comprasFacade.obterMarcas(param);
	}

	public List<ScoFornecedor> pesquisarFornecedores(Object param) {
		return this.comprasFacade.listarFornecedoresAtivos(param, 0, 100, null, false);
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setNomeSobEdicao(ScoNomeComercial nomeSobEdicao) {
		this.nomeSobEdicao = nomeSobEdicao;
	}

	public ScoNomeComercial getNomeSobEdicao() {
		return nomeSobEdicao;
	}

	public void setEditionMode(boolean editionMode) {
		this.editionMode = editionMode;
	}

	public boolean isEditionMode() {
		return editionMode;
	}

	public void setInsertionMode(boolean insertionMode) {
		this.insertionMode = insertionMode;
	}

	public boolean isInsertionMode() {
		return insertionMode;
	}

	public DynamicDataModel<AipCidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipCidades> dataModel) {
		this.dataModel = dataModel;
	}

}

