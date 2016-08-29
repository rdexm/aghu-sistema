package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.core.action.ActionController;
import javax.inject.Inject;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarCaracteristicasDeComplexidadePaginatorController extends
	ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -911347480278070865L;

	@PostConstruct
	protected void inicializar() {
	this.begin(conversation);
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String PAGE_CADASTRAR_CARACTERISTICAS_COMPLEXIDADE = "cadastrarCaracteristicasDeComplexidade";
	
	@Inject @Paginator
	private DynamicDataModel<FatCaractComplexidade> dataModel;
	
	// Filtro principal da pesquisa
	private FatCaractComplexidade filtro;
	
	private FatCaractComplexidade caractComplexidade;
	
	private FatCaractComplexidade caractComplexidadePai;
	
	private DominioSituacao situacao = null;
	
	private Integer seqSus;
	
	/**
	 * Carregar o filtro 
	 */
	private void carregarFiltro(){
		
		this.filtro = new FatCaractComplexidade();
		
		if (this.caractComplexidade != null) {
			this.filtro = this.caractComplexidade;
		}
		
		this.filtro.setFatCaractComplexidade(this.caractComplexidadePai);
		this.filtro.setIndSituacao(situacao);
		this.filtro.setSeqSus(seqSus);
		
	}
	
	/**
	 * Carrega lista do grid
	 */
	@Override
	public List<FatCaractComplexidade> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		carregarFiltro();
		return this.faturamentoFacade.pesquisarCaracteristicasDeComplexidade(firstResult,maxResult, orderProperty, asc, this.filtro);
	}
	
	@Override
	public Long recuperarCount() {
		
		carregarFiltro();
		return this.faturamentoFacade.pesquisarCaracteristicasDeComplexidadeCount(this.filtro);
	}
	

	/**
	 * Lista SuggestionBox Caracteristicas De Complexidade Codigo
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FatCaractComplexidade> listaCaracteristicasDeComplexidade(String caractComplexidade) {
		
		return  this.returnSGWithCount(this.faturamentoFacade.listaCaracteristicasDeComplexidade(caractComplexidade),listaCaracteristicasDeComplexidadeCount(caractComplexidade));
	}

	public Long listaCaracteristicasDeComplexidadeCount(String caractComplexidade) {
		
		return this.faturamentoFacade.listaCaracteristicasDeComplexidadeCount(caractComplexidade);
	}
	
	/**
	 * Lista SuggestionBox Caracteristicas De Complexidade Codigo Pai
	 * 
	 * @param caractComplexidade
	 * @return
	 */
	public List<FatCaractComplexidade> listaCaracteristicasDeComplexidadePai(String caractComplexidade) {
		
		return  this.returnSGWithCount(this.faturamentoFacade.listaCaracteristicasDeComplexidade(caractComplexidade),listaCaracteristicasDeComplexidadePaiCount(caractComplexidade));
	}

	public Long listaCaracteristicasDeComplexidadePaiCount(String caractComplexidade) {
		
		return this.faturamentoFacade.listaCaracteristicasDeComplexidadeCount(caractComplexidade);
	}
	
	
	/**
	* Pesquisa
	*/
	public void pesquisar() {
	
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	* Limpar os campos das tela e remove a grid e botão novo
	*/
	public void limpar() {
		
		this.filtro = new FatCaractComplexidade();
		this.dataModel.limparPesquisa();
		this.caractComplexidade = null;
		this.caractComplexidadePai = null;
		this.situacao = null;
	}
	
	
	/**
	 * Abrir a tela de inclusão
	 *  
	 * @return
	 */
	public String incluir(){
		
		return PAGE_CADASTRAR_CARACTERISTICAS_COMPLEXIDADE;
	}
		
	/**
	 * Ativa a CaracteristicasDeComplexidade
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 */
	public void ativarCaracteristica(FatCaractComplexidade caractComplexidade) throws ApplicationBusinessException{
		
		caractComplexidade.setIndSituacao(DominioSituacao.A);
		alterarSituacao(caractComplexidade);
	}
	
	/**
	 * Desativa a CaracteristicasDeComplexidade
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 */
	public void desativarCaracteristica(FatCaractComplexidade caractComplexidade) throws ApplicationBusinessException{
		
		caractComplexidade.setIndSituacao(DominioSituacao.I);
		alterarSituacao(caractComplexidade);		
	}
	
	/**
	 * Persisti a alteração feita pelo metodo ativarCaracteristica ou desativarCaracteristica
	 * 
	 * @param caractComplexidade
	 * @throws ApplicationBusinessException
	 */
	private void alterarSituacao(FatCaractComplexidade caractComplexidade) throws ApplicationBusinessException{
		
		try {
			this.faturamentoFacade.persistirCaracteristicasDeComplexidade(caractComplexidade, true);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CARACT_COMPLEXIDADE", caractComplexidade.getDescricao().toUpperCase());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
		
	}
	

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<FatCaractComplexidade> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<FatCaractComplexidade> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the filtro
	 */
	public FatCaractComplexidade getFiltro() {
		return filtro;
	}

	/**
	 * @param filtro the filtro to set
	 */
	public void setFiltro(FatCaractComplexidade filtro) {
		this.filtro = filtro;
	}

	/**
	 * @return the caractComplexidade
	 */
	public FatCaractComplexidade getCaractComplexidade() {
		return caractComplexidade;
	}

	/**
	 * @param caractComplexidade the caractComplexidade to set
	 */
	public void setCaractComplexidade(FatCaractComplexidade caractComplexidade) {
		this.caractComplexidade = caractComplexidade;
	}

	/**
	 * @return the caractComplexidadePai
	 */
	public FatCaractComplexidade getCaractComplexidadePai() {
		return caractComplexidadePai;
	}

	/**
	 * @param caractComplexidadePai the caractComplexidadePai to set
	 */
	public void setCaractComplexidadePai(FatCaractComplexidade caractComplexidadePai) {
		this.caractComplexidadePai = caractComplexidadePai;
	}

	/**
	 * @return the situacao
	 */
	public DominioSituacao getSituacao() {
		return situacao;
	}

	/**
	 * @param situacao the situacao to set
	 */
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getSeqSus() {
		return seqSus;
	}

	public void setSeqSus(Integer seqSus) {
		this.seqSus = seqSus;
	}
	
}

