package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItemProcHospTranspId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProcedimentoRealizadoXTransplanteController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2519848507055761335L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
		
	private FatItemProcHospTransp cadastro = new FatItemProcHospTransp();
	
	private FatProcedimentosHospitalares tabela = null;
	
	private FatItensProcedHospitalar procedimentoHospitalar = null;
	
	private FatItensProcedHospitalar entidade = new FatItensProcedHospitalar();
	
	private FatItensProcedHospitalar procedRecuperado = null;
	
	private FatTipoTransplante transplante = null;
	
	private boolean modoEdicao;
	
	private boolean procedHabilitado;
	
	private static final String PAGE_PESQUISA_PROCEDIMENTO_COM_TRANSPLANTE = "faturamento-procedimentoRealizadoTransplanteList";
	
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_CADASTRO_PROCEDIMENTO_COM_TRANSPLANTE";
	
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_COM_TRANSPLANTE";
	
	@PostConstruct
	public void inicializar(){
		begin(conversation);
	}
			
	/**
	 * Valida se a tela foi acessada para edição ou inclusão de um registro
	 */
	public void iniciar() {
		
		if (!this.modoEdicao && entidade.getId() == null){//se inclusão			
			limpar();
		}else if(this.modoEdicao){// se edição
			preencherObjetosTelaEdicao();
		}
	
	}
	
	/** ação de gravação
	 * @throws BaseException 
	 * @throws ApplicationBusinessException */
	public String gravar(){			
		preencherObjetoPersistencia();
		
		try {

			if(!modoEdicao){
				faturamentoFacade.persistirItemProcedHospTransp(cadastro, procedRecuperado, entidade);
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);

				limpar();
			}else{
				faturamentoFacade.atualizarItemProcedHospTransp(cadastro);
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
			}
			
		}  catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}	
		
		return null;
	}

	private void preencherObjetoPersistencia() {
		
		if(tabela != null && tabela.getSeq() != null && cadastro.getId() != null){
			cadastro.getId().setIphPhoSeq(tabela.getSeq());
		}
		
		if(procedimentoHospitalar != null && procedimentoHospitalar.getSeq() != null 
				&& cadastro.getId() != null ){
			cadastro.getId().setIphSeq(Integer.valueOf(procedimentoHospitalar.getSeq()));
		}
		
		if(transplante != null  && transplante.getCodigo() != null 
				&& cadastro.getFatTipoTransplante() != null ){
			cadastro.setFatTipoTransplante(transplante);
		}
		
		//compõe o objeto para a persistência	
		if((entidade != null && entidade.getId() != null) || (procedimentoHospitalar != null && procedimentoHospitalar.getId() != null)){
			
			if(modoEdicao){ // se edição
				procedRecuperado = faturamentoFacade.obterItemProcedHospitalar(entidade.getId().getPhoSeq(), entidade.getId().getSeq());
			}else{ 
				procedRecuperado = faturamentoFacade.obterItemProcedHospitalar(procedimentoHospitalar.getId().getPhoSeq(), procedimentoHospitalar.getId().getSeq());				
			}
		}
		
		if(procedRecuperado != null){
			entidade = procedRecuperado;
			procedRecuperado.setItemProcHospTransp(cadastro);
			procedRecuperado.setIphSeq(cadastro.getId().getIphSeq());
		}

	}
	
	private void preencherObjetosTelaEdicao(){		
		tabela = entidade.getProcedimentoHospitalar();
		procedimentoHospitalar = entidade;
		transplante = entidade.getItemProcHospTransp().getFatTipoTransplante();
		
		inicializarObjetos();
	}

	
	//ação limpar
	private void limpar(){
		cadastro = new FatItemProcHospTransp();
		inicializarObjetos();
		limparFiltrosSuggestionBox();
		obterTabelaPadraoSUS();
	}	
	
	private void inicializarObjetos() {
		
		if(cadastro ==  null){
			cadastro = new FatItemProcHospTransp();
		}
		
		if(cadastro.getId() == null){
			cadastro.setId(new FatItemProcHospTranspId());
		}
		
		if(cadastro.getFatTipoTransplante() == null){
			cadastro.setFatTipoTransplante(new FatTipoTransplante());
		}
	}

	private void limparFiltrosSuggestionBox() {
		limparFiltroTabela();
		limparFiltroTransplante();
		limparFiltroProcedHospitalar();
	}
	

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {
		
		limpar();
		
		entidade = new FatItensProcedHospitalar();
		this.modoEdicao = Boolean.FALSE;
		
		return PAGE_PESQUISA_PROCEDIMENTO_COM_TRANSPLANTE;
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
		limparFiltroProcedHospitalar();
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
	
	//PROCEDIMENTO HOSPITALAR
	public List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalares(String filtro){
		return  this.returnSGWithCount(faturamentoFacade.pesquisarProcedimentosHospitalaresComInternacao(Short.valueOf(tabela.getSeq()), filtro),pesquisarProcedimentosHospitalaresCount(filtro));
	}
	
	public Long pesquisarProcedimentosHospitalaresCount(String filtro){
		return faturamentoFacade.pesquisarProcedimentosHospitalaresComInternacaoCount(Short.valueOf(tabela.getSeq()), filtro);
	}
	
	public void limparFiltroProcedHospitalar(){
		procedimentoHospitalar = null;
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
	
	/**
	 * Getters e Setters
	 * */
	public FatItemProcHospTransp getFiltro() {
		return cadastro;
	}

	public void setFiltro(FatItemProcHospTransp filtro) {
		this.cadastro = filtro;
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

	public FatItensProcedHospitalar getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(
			FatItensProcedHospitalar procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public FatItensProcedHospitalar getEntidade() {
		return entidade;
	}

	public void setEntidade(FatItensProcedHospitalar entidade) {
		this.entidade = entidade;
	}

	public boolean isProcedHabilitado() {
		return procedHabilitado;
	}

	public void setProcedHabilitado(boolean procedHabilitado) {
		this.procedHabilitado = procedHabilitado;
	}

	public FatItemProcHospTransp getCadastro() {
		return cadastro;
	}

	public void setCadastro(FatItemProcHospTransp cadastro) {
		this.cadastro = cadastro;
	}

}
