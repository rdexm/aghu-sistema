package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItemProcHospTranspId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProcedimentoRealizadoXTransplantePaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2519848507055761335L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
		
	@EJB
	private IParametroFacade parametroFacade;
	
	private FatItensProcedHospitalar filtro = new FatItensProcedHospitalar();
	
	private FatItensProcedHospitalar entidade = new FatItensProcedHospitalar();
	
	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;
	
	private FatProcedimentosHospitalares tabela = null;
	
	private FatTipoTransplante transplante = null;
	
	private String descricaoTransplante;
	
	private static final String PAGE_CADASTRO_PROCEDIMENTO_COM_TRANSPLANTE = "faturamento-procedimentoRealizadoTransplanteCRUD";
	
	private static final String MENSAGEM_SUCESSO_EXCLUSAO = "MENSAGEM_SUCESSO_EXCLUSAO_PROCEDIMENTO_COM_TRANSPLANTE";
	
	//Variavel para o selection do serverDataTable(Hover da lista)
	private FatItensProcedHospitalar procedimentosComTransplanteSelection = new FatItensProcedHospitalar();
	
	@PostConstruct
	public void inicializar(){
		begin(conversation);
		obterTabelaPadraoSUS();
	}
	
	public void iniciar() {
		
		if(filtro ==  null){
			filtro = new FatItensProcedHospitalar();
		}
		
		if(filtro.getId() == null){
			filtro.setId(new FatItensProcedHospitalarId());
		}
		
		if(filtro.getItemProcHospTransp() == null){			
			filtro.setItemProcHospTransp(new FatItemProcHospTransp());
			filtro.getItemProcHospTransp().setFatTipoTransplante(new FatTipoTransplante());
		}
		
		if(filtro.getItemProcHospTransp().getId() == null){
			filtro.getItemProcHospTransp().setId(new FatItemProcHospTranspId());
		}
			
	}

	/**
	 * consulta principal 
	 * */
	@Override
	public List<FatItensProcedHospitalar> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return faturamentoFacade.pesquisarProcedimentosHospitalaresTransplante(firstResult, 
				maxResult, orderProperty, asc, filtro);
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarProcedimentosHospitalaresTransplanteCount(filtro);		
	}
	
	//ação pesquisar
	public void pesquisar(){
		setProcedimentosComTransplanteSelection(new FatItensProcedHospitalar());
		preencherFiltrosPesquisa();
		atualizarDataModel();
	}
	
	private void preencherFiltrosPesquisa() {

		if(transplante != null  && transplante.getCodigo() != null && filtro.getItemProcHospTransp() != null){
			filtro.getItemProcHospTransp().setFatTipoTransplante(transplante);
		} else {
			filtro.getItemProcHospTransp().setFatTipoTransplante(null);
		}
		
		if(tabela != null && tabela.getSeq() != null && filtro.getItemProcHospTransp() != null && filtro.getItemProcHospTransp().getId() != null){
			filtro.getItemProcHospTransp().getId().setIphPhoSeq(tabela.getSeq());
		}
	}
	
	public void atualizarDataModel(){
		dataModel.reiniciarPaginator();	
	}
	
	//ação limpar
	public void limpar(){
		setProcedimentosComTransplanteSelection(new FatItensProcedHospitalar());
		filtro = new FatItensProcedHospitalar();
		iniciar();
		limparFiltrosSuggestionBox();
		dataModel.limparPesquisa();	

		obterTabelaPadraoSUS();
		
	}	
	
	private void limparFiltrosSuggestionBox() {
		limparFiltroTabela();
		limparFiltroTransplante();
	}
	
	/**
	 * incluir 
	 */
	public String incluir(){
		setProcedimentosComTransplanteSelection(new FatItensProcedHospitalar());
		return PAGE_CADASTRO_PROCEDIMENTO_COM_TRANSPLANTE;
	}
	

	/**
	 * excluir 
	 */
	public void excluir(){
		setProcedimentosComTransplanteSelection(new FatItensProcedHospitalar());
		if (entidade != null && entidade.getItemProcHospTransp() != null 
				&& entidade.getItemProcHospTransp().getId() != null 
				&& entidade.getItemProcHospTransp().getId().getIphSeq() != null
				&& entidade.getItemProcHospTransp().getId().getIphPhoSeq() != null) {
			
			
			faturamentoFacade.excluirFatItemHospTransp(entidade.getItemProcHospTransp());
			
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EXCLUSAO);
		}
	}
	
	/**
	 * relacionar 
	 */
	public String relacionar(){
		
		return incluir();
	}

	/**
	 * consultas suggestionBox
	 * */

	//TABELA
	public List<FatProcedimentosHospitalares> pesquisarTabelas(String filtro){
		return  this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricao(filtro),pesquisarTabelasCount(filtro));
	}
	
	public Long pesquisarTabelasCount(String filtro){
		return faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(filtro);
	}
	
	public void limparFiltroTabela(){
		tabela = null;
	}
	
	
	/**
	 * Método que obtém da tabela de parâmentro o valor númerico.
	 */
	public void obterTabelaPadraoSUS(){
		
		try {
			
			Short procedimentoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			tabela = this.faturamentoFacade.obterProcedimentoHospitalar(procedimentoSeq);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obterDescricaoTransplante(FatItensProcedHospitalar item){
		
		FatItemProcHospTransp transp = faturamentoFacade.obterDescricaoTransplantePorProcedHospitalar(item.getItemProcHospTransp().getId());
		
		if(transp != null && StringUtils.isNotBlank(transp.getFatTipoTransplante().getDescricao())){
			return transp.getFatTipoTransplante().getDescricao();
		}
		
		return ""; 
	}
	
	//TRANSPLANTE
	public List<FatTipoTransplante> pesquisarTransplantes(String filtro){
		return  this.returnSGWithCount(faturamentoFacade.pesquisarProcedimentosTransplante(filtro),pesquisarTransplantesCount(filtro));
	}
	
	public Long pesquisarTransplantesCount(String filtro){
		return faturamentoFacade.pesquisarProcedimentosTransplanteCount(filtro);
	}
	
	public void limparFiltroTransplante(){
		transplante = null;
	}
	
	
	public String getDescricaoTruncada() {
		return StringUtils.abbreviate(tabela.getDescricao(), 20);
	}
	
	/**
	 * método de hint 
	 */
	
	public String obterHintDescricao(String descricao) {

		if (StringUtils.isNotBlank(descricao) && descricao.length() > 50) {
			descricao = StringUtils.abbreviate(descricao, 50);
		}
		return descricao;
	}

	/**
	 * Getters e Setters
	 * */

	public FatItensProcedHospitalar getFiltro() {
		return filtro;
	}
	
	public void setFiltro(FatItensProcedHospitalar filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

	public FatProcedimentosHospitalares getTabela() {
		return tabela;
	}

	public void setTabela(FatProcedimentosHospitalares tabela) {
		this.tabela = tabela;
	}

	public FatTipoTransplante getTransplante() {
		return transplante;
	}

	public void setTransplante(FatTipoTransplante transplante) {
		this.transplante = transplante;
	}

	public FatItensProcedHospitalar getEntidade() {
		return entidade;
	}

	public void setEntidade(FatItensProcedHospitalar entidade) {
		this.entidade = entidade;
	}

	public String getDescricaoTransplante() {
		return descricaoTransplante;
	}

	public void setDescricaoTransplante(String descricaoTransplante) {
		this.descricaoTransplante = descricaoTransplante;
	}

	public FatItensProcedHospitalar getProcedimentosComTransplanteSelection() {
		return procedimentosComTransplanteSelection;
	}

	public void setProcedimentosComTransplanteSelection(
			FatItensProcedHospitalar procedimentosComTransplanteSelection) {
		this.procedimentosComTransplanteSelection = procedimentosComTransplanteSelection;
	}

}
