package br.gov.mec.aghu.estoque.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterValidadeMaterialController extends ActionController {
	
	private static final long serialVersionUID = 178440529421019170L;
	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";
	private static final String PESQUISAR_VALIDADE_MATERIAL = "estoque-pesquisarValidadeMaterial";

	@EJB
	private IEstoqueFacade estoqueFacade;

	private SceEstoqueAlmoxarifado estalm;
	private SceLoteDocumento loteDoc;
	private Integer estAlmoxSeq;
	private Integer loteDocumentoSeq;
	private SceTipoMovimento tipoMovimento;

	private List<SceValidade> listaValidade = new ArrayList<SceValidade>();
	private Date dtValidade;
	private Long dtValidadeNumerica;
	private Long dtValidadeNumericaExcluir;
	private SceValidade validade;

	private String origem;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() throws ApplicationBusinessException {
		this.estalm = this.estoqueFacade.buscaSceEstoqueAlmoxarifadoPorId(this.estAlmoxSeq);

		if(this.estalm.getIndControleValidade()){
			this.listaValidade = this.estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(this.estalm.getSeq());
		}
		
		this.listaValidade = this.estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(this.estalm.getSeq());
		
		this.cancelarEdicao();
	}
	


	/**
	 * Adiciona/Grava validade na lista
	 */
	public void adicionar(){
		
		final boolean isNovo = this.validade.getId() == null;
		
		try{

			if(isNovo){
				
				this.validade.setId(new SceValidadeId(this.estalm.getSeq(), this.dtValidade));
				
				// INSERE validade
				this.estoqueFacade.inserirValidadeMaterial(this.validade);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INSERT_CTRL_VAL_MATERIAL");

			} else {
				
				// ATUALIZAR validade
				this.estoqueFacade.atualizarValidadeMaterial(this.validade);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_UPDATE_CTRL_VAL_MATERIAL");
				
			}

			this.cancelarEdicao();

			this.listaValidade = this.estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(estalm.getSeq());

		} catch (BaseException e) {
			
			if(isNovo){
				this.validade.setId(null);
			}
			
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Edita item de validade selecionado na lista
	 * @param dtValidadeNumerica
	 */
	public void editar(final Long dtValidadeNumerica){
		
		this.dtValidadeNumerica = dtValidadeNumerica;

		for (SceValidade validade : listaValidade) {
			if(this.dtValidadeNumerica != null && this.dtValidadeNumerica.equals(validade.getId().getDataLong())){
				this.validade = validade;
				this.dtValidade = this.validade.getId().getData();
			}
		}
	}
	
	/**
	 * Cancela edição do item de validade selecionado
	 */
	public void cancelarEdicao(){
		this.validade = new SceValidade();
		this.dtValidadeNumerica = null;
		this.dtValidade = null;
	}
	
	/**
	 * Remove item de validade da lista e do banco
	 */
	public void excluir(){
		
		try{

			final SceValidade validade = this.estoqueFacade.obterValidadePorDataValidadeEstoqueAlmoxarifado(new Date(dtValidadeNumericaExcluir), estalm.getSeq());
			
			// REMOVE validade
			this.estoqueFacade.excluirValidadeMaterial(validade);
			
			this.listaValidade = this.estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(this.estalm.getSeq());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVE_CTRL_VAL_MATERIAL");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	/**
	 * Verifica se o item de validade foi selecionado na lista
	 * @param validade
	 * @return
	 */
	public boolean isItemSelecionado(final SceValidade validade){
		
		if(this.validade != null && this.validade.equals(validade)){
			return true;
		}
		
		return false;
	}


	/**
	 * Ação do botão voltar/cancelar
	 * @return
	 */
	public String voltar(){
		loteDoc = new SceLoteDocumento();
		loteDocumentoSeq = null;
		listaValidade = null;
		estAlmoxSeq = null;
		loteDocumentoSeq = null;
		
		
		if(this.origem != null){
			if(origem.equals("pesquisarValidadeMaterial")){
				return PESQUISAR_VALIDADE_MATERIAL;		
			}
			else if(origem.equals("pesquisarEstoqueAlmoxarifado")){
				return PESQUISAR_ESTOQUE_ALMOXARIFADO;
			}
		}
		return null;
	}

	/*
	 * Getters e setters
	 */

	public SceEstoqueAlmoxarifado getEstalm() {
		return estalm;
	}

	public void setEstalm(SceEstoqueAlmoxarifado estalm) {
		this.estalm = estalm;
	}

	public Integer getEstAlmoxSeq() {
		return estAlmoxSeq;
	}

	public void setEstAlmoxSeq(Integer estAlmoxSeq) {
		this.estAlmoxSeq = estAlmoxSeq;
	}

	public SceLoteDocumento getLoteDoc() {
		return loteDoc;
	}

	public void setLoteDoc(SceLoteDocumento loteDoc) {
		this.loteDoc = loteDoc;
	}

	public Integer getLoteDocumentoSeq() {
		return loteDocumentoSeq;
	}

	public void setLoteDocumentoSeq(Integer loteDocumentoSeq) {
		this.loteDocumentoSeq = loteDocumentoSeq;
	}

	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public List<SceValidade> getListaValidade() {
		return listaValidade;
	}

	public void setListaValidade(List<SceValidade> listaValidade) {
		this.listaValidade = listaValidade;
	}

	public Date getDtValidade() {
		return dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}

	public Long getDtValidadeNumerica() {
		return dtValidadeNumerica;
	}

	public void setDtValidadeNumerica(Long dtValidadeNumerica) {
		this.dtValidadeNumerica = dtValidadeNumerica;
	}

	public SceValidade getValidade() {
		return validade;
	}

	public void setValidade(SceValidade validade) {
		this.validade = validade;
	}

	public Long getDtValidadeNumericaExcluir() {
		return dtValidadeNumericaExcluir;
	}

	public void setDtValidadeNumericaExcluir(Long dtValidadeNumericaExcluir) {
		this.dtValidadeNumericaExcluir = dtValidadeNumericaExcluir;
	}


	public String getOrigem() {
		return origem;
	}


	public void setOrigem(String origem) {
		this.origem = origem;
	}
}