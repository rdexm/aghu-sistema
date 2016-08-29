package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class IndisponibilidadeSalaAcomodacaoPaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -684848484848484L;

	private static final String PAGE_CADASTRAR_INDISPONIBILIDADE = "/pages/procedimentoterapeutico/indisponibilidadeSalaOuAcomodacaoCRUD.xhtml";
	
	private static final Log LOG = LogFactory.getLog(IndisponibilidadeSalaAcomodacaoPaginatorController.class);
	
	@EJB
	IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	IServidorLogadoFacade servidorLogadoFacade;
	
	private MptLocalAtendimento localAtendimento; 
	
	private List<MptTipoSessao> listaTipoSessao = null;

	private MptTipoSessao tipoSessaoCombo;
	
	private List<MptLocalAtendimento> listaAcomodacoes = null;
	
	private MptLocalAtendimento acomodacaoCombo;
	
	private List<MptSalas> listaSalas = null;
	
	private MptSalas salaCombo;
	
	private MptBloqueio filtro = new MptBloqueio();
	
	private Boolean voltou = Boolean.FALSE;

	private Boolean cancelar = Boolean.FALSE;
	
	private MptBloqueio bloqueio = new MptBloqueio();
	

	public Boolean getCancelar() {
		return cancelar;
	}

	public void setCancelar(Boolean cancelar) {
		this.cancelar = cancelar;
	}

	@Inject @Paginator
	private DynamicDataModel<MptBloqueio> dataModel;
	
	public enum IndisponibilidadeSalaAcomodacaoNegocioExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_SESSAO, MENSAGEM_SALA_ISA
	}
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void iniciar() {	
		//verificar permissão de acesso
		if(voltou){
			pesquisar();
			cancelar = Boolean.FALSE;
		}else{
		inicializarFiltro();
		carregarComboTipoSessao();}
	}
	
	private void inicializarFiltro(){
		filtro = new MptBloqueio();
		
		filtro.setTipoSessao(new MptTipoSessao());
		filtro.setSala(new MptSalas());
		filtro.setLocalAtendimento(new MptLocalAtendimento());		
	}
	
	//Tipo Sessao
	private void carregarComboTipoSessao(){
		listaTipoSessao = procedimentoTerapeuticoFacade.buscarTipoSessao();
//		selecionarTipoSessao();
	}
	
	public void selecionarTipoSala(){
		if(listaTipoSessao != null){
			if (filtro.getTipoSessao() != null && filtro.getTipoSessao().getSeq() != null){
				limparFiltroLocalAtendimento();
				limparFiltroSala();
				listaSalas = procedimentoTerapeuticoFacade.buscarSala(filtro.getTipoSessao().getSeq());
				if (listaSalas.isEmpty()){
					limparFiltroLocalAtendimento();
					limparFiltroSala();
				}
			}else{
			limparFiltroSala();
			limparFiltroLocalAtendimento();
			}
		}
	}

	public void selecionarAcomodacao(){
		if(listaSalas != null){
			if (filtro.getSala() != null && filtro.getSala().getSeq() != null){
				listaAcomodacoes = procedimentoTerapeuticoFacade.buscarLocalAtendimento(filtro.getSala().getSeq());
			}else{
				limparFiltroLocalAtendimento();
			}
		}		
	}
	
	/**
	 * Realiza a consulta principal 
	 */
	public void pesquisar(){
		try{
			if(filtro != null && filtro.getTipoSessao() == null){
				throw new ApplicationBusinessException(IndisponibilidadeSalaAcomodacaoNegocioExceptionCode.MENSAGEM_TIPO_SESSAO);				
			}				

			atualizarDataModel();
		} catch (BaseException e) {
			LOG.error("Exceção capturada em 'IndisponibilidadeSalaAcomodacaoPaginatorController', parametros nulos.");
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	private void atualizarDataModel(){
		dataModel.reiniciarPaginator();	
	}
	
	//hint
	public String obterHintDescricao(String descricao) {

		if (StringUtils.isNotBlank(descricao) && descricao.length() > 20) {
			descricao = StringUtils.abbreviate(descricao, 20);
		}
		return descricao;
	}
	
	// ação limpar	
	public void limpar(){
		filtro = new MptBloqueio();
		limparFiltrosCombos();
		carregarComboTipoSessao();
		dataModel.limparPesquisa();
		voltou = Boolean.FALSE;
		iniciar();
	}	
	
	private void limparFiltrosCombos(){
		limparFiltroTipoSessao();
		limparFiltroSala();
		limparFiltroLocalAtendimento();
	}
	
	public void limparFiltroTipoSessao(){
		filtro.setTipoSessao(new MptTipoSessao());
		listaTipoSessao = null;
	}
	
	public void limparFiltroSala(){
		filtro.setSala(new MptSalas());
		listaSalas = null;
	}
	
	public void limparFiltroLocalAtendimento(){
		filtro.setLocalAtendimento(new MptLocalAtendimento());
		listaAcomodacoes = null;
	}
	
	/**
	 * Carrega a lista da grid
	 */
	@Override
	public List<MptBloqueio> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return procedimentoTerapeuticoFacade.pesquisarIndisponibilidadeSalaAcomodacao(
				firstResult, maxResult, orderProperty, asc, this.filtro);
	}
	
	@Override
	public Long recuperarCount() {		
		return procedimentoTerapeuticoFacade.pesquisarIndisponibilidadeSalaAcomodacaoCount(this.filtro);
	}
	
	/**
	 * INCLUIR 
	 */
	public String incluir(){
		
		return PAGE_CADASTRAR_INDISPONIBILIDADE;
	}
	
	public void excluir(MptBloqueio item){
		procedimentoTerapeuticoFacade.removerMptBloqueio(item); 
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REMOVER_SUCESSO_INDISPONIBILIDADE", item.getDescricao());
		pesquisar();
	}
	
	public String editar(){
		return PAGE_CADASTRAR_INDISPONIBILIDADE;
	}
	
	/**
	 * Trunca nome da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}		
	
	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public MptLocalAtendimento getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(MptLocalAtendimento localAtendimento) {
		this.localAtendimento = localAtendimento;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public MptTipoSessao getTipoSessaoCombo() {
		return tipoSessaoCombo;
	}

	public List<MptLocalAtendimento> getListaAcomodacoes() {
		return listaAcomodacoes;
	}

	public void setListaAcomodacoes(List<MptLocalAtendimento> listaAcomodacoes) {
		this.listaAcomodacoes = listaAcomodacoes;
	}

	public void setTipoSessaoCombo(MptTipoSessao tipoSessaoCombo) {
		this.tipoSessaoCombo = tipoSessaoCombo;
	}

	public MptLocalAtendimento getAcomodacaoCombo() {
		return acomodacaoCombo;
	}

	public void setAcomodacaoCombo(MptLocalAtendimento acomodacaoCombo) {
		this.acomodacaoCombo = acomodacaoCombo;
	}

	public List<MptSalas> getListaSalas() {
		return listaSalas;
	}

	public void setListaSalas(List<MptSalas> listaSalas) {
		this.listaSalas = listaSalas;
	}

	public MptSalas getSalaCombo() {
		return salaCombo;
	}

	public void setSalaCombo(MptSalas salaCombo) {
		this.salaCombo = salaCombo;
	}

	public MptBloqueio getFiltro() {
		return filtro;
	}

	public void setFiltro(MptBloqueio filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<MptBloqueio> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MptBloqueio> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getVoltou() {
		return voltou;
	}

	public void setVoltou(Boolean voltou) {
		this.voltou = voltou;
	}	
	
	public MptBloqueio getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(MptBloqueio bloqueio) {
		this.bloqueio = bloqueio;
	}
}
