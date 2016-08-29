package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.List;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaJn;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class CadastrarCaracteristicasController extends ActionController{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 59874356784L;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private static final String PAGE_CARACTERISTICA_CRUD = "procedimentoterapeutico-cadastrarCaracteristicasCRUD";
	private static final String PAGE_CARACTERISTICA_LIST = "procedimentoterapeutico-cadastrarCaracteristicasList";
	
	private List<MptTipoSessao> listaTipoSessoes;
	
	//Filtros
	private MptTipoSessao tipoSessaoFiltro;
	private String  descricaoFiltro;
	private DominioSituacao situacao;

	private Boolean mostrarTabela;
	private List<MptCaracteristica> listaCaracteristicas;
	private MptCaracteristica onMouseOver = new MptCaracteristica();
	
	private MptCaracteristica acaoSelecao;
	
	private List <MptCaracteristicaJn> listaHistorico;
	private boolean situacaoDiferente;
	
	private Boolean ativoCRUD;
	
	private RapServidores servidorLogado;
	
	private boolean abriuDialog;
	
	//Guarda a ultima alteração feita no registro
	private MptCaracteristicaJn ultimaAlteracao;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		mostrarTabela = Boolean.FALSE;
	}
	
	public void inicio(){
		listaTipoSessoes = procedimentoTerapeuticoFacade.pesquisarTipoSessoesAtivas();
		listaCaracteristicas = procedimentoTerapeuticoFacade.recuperarListaDeCaracteristicas(tipoSessaoFiltro, descricaoFiltro, situacao);
	}
	
	public List<MptTipoSessao> carregarTiposSessao(){
		List<MptTipoSessao> retorno = procedimentoTerapeuticoFacade.pesquisarTipoSessoesAtivas();
		return retorno;
	}
	
	public void pesquisar(){
		mostrarTabela = Boolean.TRUE;
		listaCaracteristicas = procedimentoTerapeuticoFacade.recuperarListaDeCaracteristicas(tipoSessaoFiltro, descricaoFiltro, situacao);
	}
	
	public void limpar(){
		Iterator <UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		mostrarTabela = Boolean.FALSE;
		tipoSessaoFiltro = null;
		descricaoFiltro = null;
		situacao = null;
	}
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public String editar(){
		ativoCRUD = acaoSelecao.getIndSituacao().isAtivo();
		return PAGE_CARACTERISTICA_CRUD;
	}
	
	public String novo(){
		ativoCRUD = Boolean.TRUE;
		acaoSelecao = new MptCaracteristica();
		return PAGE_CARACTERISTICA_CRUD;
	}	
	
	public void fechouDialog(){
		abriuDialog = false;
	}
	
	public String gravar(){
		try {
			if(acaoSelecao.getSeq() != null){
				procedimentoTerapeuticoFacade.validarInativacaoStatusCaracteristica(acaoSelecao, ativoCRUD);
				if(!ativoCRUD && acaoSelecao.getIndSituacao().isAtivo() != ativoCRUD && !abriuDialog){
					abriuDialog = true;
					openDialog("modalConfirmacaoWG");
					return null;
				}
			}
			acaoSelecao.setIndSituacao(ativoCRUD ? DominioSituacao.A : DominioSituacao.I);
			if(acaoSelecao.getSeq() == null){
				List<String> validacoes = procedimentoTerapeuticoFacade.validarPersistenciaCaracteristica(acaoSelecao);
				if(!validacoes.isEmpty()){
					for (String erro : validacoes) {
						apresentarMsgNegocio(Severity.ERROR, erro);
					}
					return null;
				}
				acaoSelecao.setServidor(servidorLogado);
			}
			
			boolean novo = acaoSelecao.getSeq() == null;
			procedimentoTerapeuticoFacade.salvarCaracteristica(acaoSelecao, obterLoginUsuarioLogado());
			if(novo){
				apresentarMsgNegocio("CARACTERISTICA_SALVA_SUCESSO");
			}else{
				apresentarMsgNegocio("CARACTERISTICA_ALTERADA_SUCESSO");
			}
			abriuDialog = false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_CARACTERISTICA_LIST;
	}
	
	public String cancelar(){
		acaoSelecao = new MptCaracteristica();
		mostrarTabela = Boolean.TRUE;
		return PAGE_CARACTERISTICA_LIST;
	}
	
	public void visualizarHistorico(){
		listaHistorico = procedimentoTerapeuticoFacade.pesquisarAlteracoesCaracteristica(acaoSelecao.getSeq());
		if(listaHistorico != null && !listaHistorico.isEmpty() && listaHistorico.get(0) != null){
			ultimaAlteracao =  listaHistorico.get(0);
			verificarSituacaoDiferente();
		}
	}
	
	public void verificarSituacaoDiferente(){
		if(listaHistorico.get(0).getIndSituacao().equals(acaoSelecao.getIndSituacao())){
			situacaoDiferente = false;
		}else{
			situacaoDiferente = true;
		}
	}
	
	//Getter's e Setter's
	public List<MptTipoSessao> getListaTipoSessoes() {
		return listaTipoSessoes;
	}

	public void setListaTipoSessoes(List<MptTipoSessao> listaTipoSessoes) {
		this.listaTipoSessoes = listaTipoSessoes;
	}

	public MptTipoSessao getTipoSessaoFiltro() {
		return tipoSessaoFiltro;
	}

	public void setTipoSessaoFiltro(MptTipoSessao tipoSessaoFiltro) {
		this.tipoSessaoFiltro = tipoSessaoFiltro;
	}

	public String getDescricaoFiltro() {
		return descricaoFiltro;
	}

	public void setDescricaoFiltro(String descricaoFiltro) {
		this.descricaoFiltro = descricaoFiltro;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public List<MptCaracteristica> getListaCaracteristicas() {
		return listaCaracteristicas;
	}

	public void setListaCaracteristicas(List<MptCaracteristica> listaCaracteristicas) {
		this.listaCaracteristicas = listaCaracteristicas;
	}

	public Boolean getMostrarTabela() {
		return mostrarTabela;
	}

	public void setMostrarTabela(Boolean mostrarTabela) {
		this.mostrarTabela = mostrarTabela;
	}

	public MptCaracteristica getOnMouseOver() {
		return onMouseOver;
	}

	public void setOnMouseOver(MptCaracteristica onMouseOver) {
		this.onMouseOver = onMouseOver;
	}

	public MptCaracteristica getAcaoSelecao() {
		return acaoSelecao;
	}

	public void setAcaoSelecao(MptCaracteristica acaoSelecao) {
		this.acaoSelecao = acaoSelecao;
	}

	public List<MptCaracteristicaJn> getListaHistorico() {
		return listaHistorico;
	}

	public void setListaHistorico(List<MptCaracteristicaJn> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}

	public boolean isSituacaoDiferente() {
		return situacaoDiferente;
	}

	public void setSituacaoDiferente(boolean situacaoDiferente) {
		this.situacaoDiferente = situacaoDiferente;
	}

	public Boolean getAtivoCRUD() {
		return ativoCRUD;
	}

	public void setAtivoCRUD(Boolean ativoCRUD) {
		this.ativoCRUD = ativoCRUD;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public MptCaracteristicaJn getUltimaAlteracao() {
		return ultimaAlteracao;
	}

	public void setUltimaAlteracao(MptCaracteristicaJn ultimaAlteracao) {
		this.ultimaAlteracao = ultimaAlteracao;
	}

}
