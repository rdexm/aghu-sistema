package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action.PesquisaProtocoloPaginatorController;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HomologarProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ModalHomologarProtocoloCRUDController extends ActionController {

	/**
	 * #44279
	 */
	private static final long serialVersionUID = -3963686277474010264L;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private PesquisaProtocoloPaginatorController pesquisaProtocoloPaginatorController;
	
	//Parametros de entrada
	private ProtocoloVO itemVersaoProtocolo;
	
	//Campos da tela
	private MptTipoSessao tipoSessaoCombo;
	private List<HomologarProtocoloVO> listaMedicamentosProtocolo;
	private HomologarProtocoloVO selecionado;
	private List<MptTipoSessao> listaTipoSessao;
	private DominioSituacaoProtocolo situacao;
	private List<DominioSituacaoProtocolo> listaSituacao;
	private String protocolo;
	private String justificativa;
	
	//Campos para cache
	private DominioSituacaoProtocolo situacaoCache;
	private String justificativaCache;
	private List<HomologarProtocoloVO> listaMedicamentosProtocoloCache; 
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);		
	}
	
	public void iniciar(){
		limpar();
		listaTipoSessao = procedimentoTerapeuticoFacade.listarMptTiposSessao();
		if(itemVersaoProtocolo != null){
			listaSituacao = procedimentoTerapeuticoFacade.verificarSituacaoProtocolo(itemVersaoProtocolo.getSeqVersaoProtocoloSessao());
			listaSituacao.remove(DominioSituacaoProtocolo.H);
			this.situacao = null;
			listaMedicamentosProtocolo = procedimentoTerapeuticoFacade.obterListaMedicamentosProtocolo(itemVersaoProtocolo.getSeqVersaoProtocoloSessao());
			this.protocolo = itemVersaoProtocolo.getTituloProtocoloSessao(); 
		}
		carregarCacheFormulario(listaMedicamentosProtocolo);
		openDialog("modalHomologarProtocoloWG");
	}
	
	public void cancelar(){
		limpar();
		this.itemVersaoProtocolo = null;
		this.tipoSessaoCombo = null;
		closeDialog("modalHomologarProtocoloWG");
	}
	
	public void limpar(){
		this.listaTipoSessao = null;
		this.listaMedicamentosProtocolo = null;
		this.selecionado = null;
		this.listaSituacao = null;
		this.situacao = null;
		this.protocolo = null;
		this.justificativa = null;
		this.situacaoCache = null;
		this.justificativaCache = null;
		this.listaMedicamentosProtocoloCache = null;
		
	}
	
	public void gravar(){
		try {
			procedimentoTerapeuticoFacade.gravarHomologacaoProtocolo(itemVersaoProtocolo.getSeqVersaoProtocoloSessao(), justificativa, situacao, listaMedicamentosProtocolo);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_HOMOLOGAR_PROTOCOLO");
			this.pesquisaProtocoloPaginatorController.setGravarOk(true);
			cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void carregarCacheFormulario(List<HomologarProtocoloVO> listaMedicamentosProtocolo){
		this.situacaoCache = this.situacao;
		this.justificativaCache = this.justificativa;
		this.setListaMedicamentosProtocoloCache(listaMedicamentosProtocolo);
	}
	
	public void exibirConfirmacaoCancelamento(){
		if (verificarAlteracoesFormulario()) {
			openDialog("cancelarModalWG");
		}else{
			cancelar();
		}
	}
	
	public boolean verificarAlteracoesFormulario(){
		if(situacaoCache != situacao){
			return true;
		}
		
		if(justificativaCache != justificativa){
			return true;
		}
		
		if(!listaMedicamentosProtocoloCache.equals(listaMedicamentosProtocolo)){
			return true;
		}
		
		return false;
	}
	
	public void retirarEspacosEmBranco(){
		if(StringUtils.isNotEmpty(justificativa)){
			justificativa = StringUtils.stripToNull(justificativa);
		}else{
			justificativa = null;
		}
	}

	//Getters e Setters
	public ProtocoloVO getItemVersaoProtocolo() {
		return itemVersaoProtocolo;
	}
	
	public void setItemVersaoProtocolo(ProtocoloVO itemVersaoProtocolo) {
		this.itemVersaoProtocolo = itemVersaoProtocolo;
	}
	
	public List<HomologarProtocoloVO> getListaMedicamentosProtocolo() {
		return listaMedicamentosProtocolo;
	}

	public void setListaMedicamentosProtocolo(
			List<HomologarProtocoloVO> listaMedicamentosProtocolo) {
		this.listaMedicamentosProtocolo = listaMedicamentosProtocolo;
	}

	public HomologarProtocoloVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(HomologarProtocoloVO selecionado) {
		this.selecionado = selecionado;
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

	public void setTipoSessaoCombo(MptTipoSessao tipoSessaoCombo) {
		this.tipoSessaoCombo = tipoSessaoCombo;
	}

	public DominioSituacaoProtocolo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoProtocolo situacao) {
		this.situacao = situacao;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public List<DominioSituacaoProtocolo> getListaSituacao() {
		return listaSituacao;
	}

	public void setListaSituacao(List<DominioSituacaoProtocolo> listaSituacao) {
		this.listaSituacao = listaSituacao;
	}

	public DominioSituacaoProtocolo getSituacaoCache() {
		return situacaoCache;
	}

	public void setSituacaoCache(DominioSituacaoProtocolo situacaoCache) {
		this.situacaoCache = situacaoCache;
	}

	public String getJustificativaCache() {
		return justificativaCache;
	}

	public void setJustificativaCache(String justificativaCache) {
		this.justificativaCache = justificativaCache;
	}

	public List<HomologarProtocoloVO> getListaMedicamentosProtocoloCache() {
		return listaMedicamentosProtocoloCache;
	}

	public void setListaMedicamentosProtocoloCache(
			List<HomologarProtocoloVO> listaMedicamentosProtocoloCache) {
		this.listaMedicamentosProtocoloCache = listaMedicamentosProtocoloCache;
	}

	public PesquisaProtocoloPaginatorController getPesquisaProtocoloPaginatorController() {
		return pesquisaProtocoloPaginatorController;
	}

	public void setPesquisaProtocoloPaginatorController(
			PesquisaProtocoloPaginatorController pesquisaProtocoloPaginatorController) {
		this.pesquisaProtocoloPaginatorController = pesquisaProtocoloPaginatorController;
	}

}