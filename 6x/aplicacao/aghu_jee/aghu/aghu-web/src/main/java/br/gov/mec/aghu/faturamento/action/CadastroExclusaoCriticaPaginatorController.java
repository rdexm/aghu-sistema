package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.core.action.ActionController;
import javax.inject.Inject;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar a pesquisa de exclusão de crítica
 * @author marcelo.deus
 *
 */

public class CadastroExclusaoCriticaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3949953843397157298L;
	
	private static final String PAGE_CADASTRO_EXCLUSAO_CRITICA = "cadastroExclusaoCriticaCRUD";

	private FatExclusaoCritica fatExclusaoCriticaFiltro = new FatExclusaoCritica();
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject @Paginator
	private DynamicDataModel<FatExclusaoCritica> dataModel;
	private String codigo;
	private DominioSituacao situacao;
	private DominioSimNao cns;
	private DominioSimNao telefone;
	private DominioSimNao cbo;
	private DominioSimNao quantidade;
	private DominioSimNao idadeMaior;
	private DominioSimNao idadeMenor;
	private DominioSimNao permanenciaMenor;
	private Boolean editando = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar(){
		begin(conversation);
	}
	
	public void iniciar(){

		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterCadastrosBasicosFaturamento", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);	
	}
	
	public void pesquisar(){
		configurarFiltroPesquisa();
		atualizarDataModel();
	}
	
	public void limpar(){
		this.fatExclusaoCriticaFiltro = new FatExclusaoCritica();
		this.cbo = null;
		this.cns = null;
		this.codigo = null;
		this.idadeMaior = null;
		this.idadeMenor = null;
		this.permanenciaMenor = null;
		this.quantidade = null;
		this.situacao = null;
		this.telefone = null;
		this.dataModel.limparPesquisa();
	}
	
	public String editar(){
		this.editando = Boolean.TRUE;
		return incluir();
		
	}
	
	public String incluir(){
		return PAGE_CADASTRO_EXCLUSAO_CRITICA;
	}
	
	public String excluir() throws BaseException {
		try {
			this.faturamentoApoioFacade.removerExclusaoCritica(this.fatExclusaoCriticaFiltro.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_EXCLUSAO_CRITICA", this.fatExclusaoCriticaFiltro.getCodigo());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}
		this.fatExclusaoCriticaFiltro = new FatExclusaoCritica();
		
		return null;
	}

	public void atualizarDataModel(){
		dataModel.reiniciarPaginator();	
	}
	
	public void configurarFiltroPesquisa(){
		if(this.codigo != null){
			this.fatExclusaoCriticaFiltro.setCodigo(codigo);
		} 
		if(this.cns != null){
			this.fatExclusaoCriticaFiltro.setCns(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(cns)));
		}
		if(this.cbo != null){
			this.fatExclusaoCriticaFiltro.setCbo(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(cbo)));
		}
		if(this.telefone != null){
			this.fatExclusaoCriticaFiltro.setTelefone(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(telefone)));
		}
		if(this.idadeMaior != null){
			this.fatExclusaoCriticaFiltro.setIdMaior(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(idadeMaior)));
		}
		if(this.idadeMenor != null){
			this.fatExclusaoCriticaFiltro.setIdMenor(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(idadeMenor)));
		}
		if(this.quantidade != null){
			this.fatExclusaoCriticaFiltro.setQtd(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(quantidade)));
		}
		if(this.permanenciaMenor != null){
			this.fatExclusaoCriticaFiltro.setPermMenor(DominioSituacao.getInstance(DominioSimNao.getBooleanInstance(permanenciaMenor)));
		}
		if(this.situacao != null){
			this.fatExclusaoCriticaFiltro.setIndSituacao(situacao);
		}
	}
		
	public void ativarCaracteristica(FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException{
		fatExclusaoCritica.setIndSituacao(DominioSituacao.A);
		alterarSituacao(fatExclusaoCritica);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_EXCLUSAO_CRITICA");
	}
	
	public void desativarCaracteristica(FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException{
		fatExclusaoCritica.setIndSituacao(DominioSituacao.I);
		alterarSituacao(fatExclusaoCritica);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_EXCLUSAO_CRITICA");
	}

	private void alterarSituacao(FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException{
		this.faturamentoApoioFacade.alterarSituacaoExclusaoCritica(fatExclusaoCritica);
		atualizarDataModel();
	}

	@Override
	public List<FatExclusaoCritica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		configurarFiltroPesquisa();	
		return this.faturamentoApoioFacade.listarExclusaoCritica(firstResult, maxResult, orderProperty, asc, this.fatExclusaoCriticaFiltro);
	}

	@Override
	public Long recuperarCount() {
		configurarFiltroPesquisa();
		return this.faturamentoApoioFacade.pesquisarExclusaoCriticaCount(fatExclusaoCriticaFiltro);
	}

	/**
	 * GETTERS E SETTERS
	 */
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioSimNao getCns() {
		return cns;
	}

	public void setCns(DominioSimNao cns) {
		this.cns = cns;
	}

	public DominioSimNao getTelefone() {
		return telefone;
	}

	public void setTelefone(DominioSimNao telefone) {
		this.telefone = telefone;
	}

	public DominioSimNao getCbo() {
		return cbo;
	}

	public void setCbo(DominioSimNao cbo) {
		this.cbo = cbo;
	}

	public DominioSimNao getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(DominioSimNao quantidade) {
		this.quantidade = quantidade;
	}

	public DominioSimNao getIdadeMaior() {
		return idadeMaior;
	}

	public void setIdadeMaior(DominioSimNao idadeMaior) {
		this.idadeMaior = idadeMaior;
	}

	public DominioSimNao getIdadeMenor() {
		return idadeMenor;
	}

	public void setIdadeMenor(DominioSimNao idadeMenor) {
		this.idadeMenor = idadeMenor;
	}

	public DominioSimNao getPermanenciaMenor() {
		return permanenciaMenor;
	}

	public void setPermanenciaMenor(DominioSimNao permanenciaMenor) {
		this.permanenciaMenor = permanenciaMenor;
	}

	public DynamicDataModel<FatExclusaoCritica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatExclusaoCritica> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public FatExclusaoCritica getFatExclusaoCriticaFiltro() {
		return fatExclusaoCriticaFiltro;
	}

	public void setFatExclusaoCriticaFiltro(FatExclusaoCritica fatExclusaoCriticaFiltro) {
		this.fatExclusaoCriticaFiltro = fatExclusaoCriticaFiltro;
	}
}
