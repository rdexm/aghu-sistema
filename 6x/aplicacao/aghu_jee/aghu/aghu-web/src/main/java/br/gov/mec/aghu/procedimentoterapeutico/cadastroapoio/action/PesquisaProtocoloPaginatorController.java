package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MptFavoritosServidor;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.SituacaoVersaoProtocoloVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaProtocoloPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2710363395014451734L;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	@Paginator
	private DynamicDataModel<ProtocoloVO> dataModel;
	
	private MptTipoSessao tipoSessao;
	
	private AfaMedicamento medicamento;
	
	private String descricaoProtocolo;
	
	private List<MptTipoSessao> listaMptTipoSessao;
	
	private DominioSituacaoProtocolo situacaoProtocolo;
	
	private ProtocoloVO filtro;
	
	private RapServidores servidorLogado;
	
	private ProtocoloVO parametroSelecionado;
	
	private ProtocoloVO itemExclusao;
	
	private final DominioSituacaoProtocolo situacaoProtocoloConstrucao = DominioSituacaoProtocolo.C;
	
	private final DominioSituacaoProtocolo situacaoProtocoloEmHomologacao = DominioSituacaoProtocolo.H;
	
	private final DominioSituacaoProtocolo situacaoProtocoloLiberada = DominioSituacaoProtocolo.L;
	
	private final DominioSituacaoProtocolo situacaoProtocoloInativo = DominioSituacaoProtocolo.I;
	
	private ProtocoloVO selecaoGrid;
	
	private boolean habilitarBotoes;
	
	@Inject
	private CadastrarProtocoloController cadastrarProtocoloController;
	
	private List<SituacaoVersaoProtocoloVO> historicoAlteracoes = new ArrayList<SituacaoVersaoProtocoloVO>();	
	private String tituloDescricao;
	private boolean fromBack = false;
	private Integer protocolo;
	
	//#44287
	private boolean habilitaNovaVersao = false;
	//#44279
	private boolean habilitaHomologarProtocolo = false;

	private static final String PAGE_CADASTRA_PROTOCOLO = "procedimentoterapeutico-cadastraProtocolo";
	
	private static final String PAGE_RELACIONAR_PROTOCOLO = "cadastrarRelacionamentoProtocolo.xhtml";
	
	@Inject
	private CadastrarRelacionamentoProtocoloPaginatorController cadastrarRelacionamentoProtocoloPaginatorController;

	private Boolean gravarOk = false;
	
	@PostConstruct
	protected void inicializar() {
		this.listarTipoSessao();		
		
		this.begin(conversation);
	}
	
	/**
	 * Método chamado na iniciação da tela.
	 */
	public void iniciar() {		
		if ((dataModel == null) || (dataModel != null && !dataModel.getPesquisaAtiva())) {
			medicamento = null;
			situacaoProtocolo = DominioSituacaoProtocolo.A;
			descricaoProtocolo = null;
			
			this.servidorLogado = servidorLogadoFacade.obterServidorLogado();
			List<MptFavoritosServidor> listaFavoritos = procedimentoTerapeuticoFacade.obterFavoritosTipoSessao(this.servidorLogado.getId().getMatricula(), this.servidorLogado.getId().getVinCodigo());
			
			if (listaFavoritos != null && !listaFavoritos.isEmpty()) {
				tipoSessao = procedimentoTerapeuticoFacade.obterTipoSessaoPorId(listaFavoritos.get(0).getSeqTipoSessao());
			} else {
				tipoSessao = null;
			}
		}
		if(this.gravarOk == true){
			this.gravarOk = false;
			pesquisar();
		}
		habilitarBotoes = true;
		habilitaNovaVersao = false;
		habilitaHomologarProtocolo = false;
	}

	@Override
	public Long recuperarCount() {
		if (!filtro.getIndSituacaoVersaoProtocoloSessao().equals(DominioSituacaoProtocolo.I)) {
			return procedimentoTerapeuticoFacade.contarPesquisarProtocolosAtivos(filtro);
		} else {
			return procedimentoTerapeuticoFacade.contarPesquisarProtocolosInativos(filtro);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProtocoloVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		if (!filtro.getIndSituacaoVersaoProtocoloSessao().equals(DominioSituacaoProtocolo.I)) {
			return procedimentoTerapeuticoFacade.pesquisarProtocolosAtivos(filtro, firstResult, maxResults, orderProperty, asc);
		} else {
			return procedimentoTerapeuticoFacade.pesquisarProtocolosInativos(filtro, firstResult, maxResults, orderProperty, asc);
		}
	}

	/**
	 * Ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.selecaoGrid = null;
		this.habilitaNovaVersao = false;
		if (this.validarFiltro()) {
			return;
		}
		selecaoGrid = null;
		habilitarBotoes = true;
		habilitaHomologarProtocolo = false;
		this.preencherFiltro();
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Valida filtros.
	 * @return Boolean.TRUE se achou inválido;
	 */
	private boolean validarFiltro() {
		boolean achouInvalido = false;
		if (this.tipoSessao == null) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_PESQUISA_PROTOCOLO_TIPO_SESSAO_OBRIGATORIO");
			achouInvalido = true;
		}		
		if (this.situacaoProtocolo == null) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_PESQUISA_PROTOCOLO_SITUACAO_OBRIGATORIO");
			achouInvalido = true;
		}		
		return achouInvalido;
	}
	
	/**
	 * Preenche filtro da pesquisa.
	 */
	private void preencherFiltro() {
		this.filtro = new ProtocoloVO();
		this.filtro.setSeqTipoSessao(this.tipoSessao.getSeq());
		this.filtro.setTituloProtocoloSessao(this.descricaoProtocolo);
		this.filtro.setIndSituacaoVersaoProtocoloSessao(situacaoProtocolo);
		
		if (medicamento != null) {
			this.filtro.setCodMedicamento(medicamento.getMatCodigo());
		}
	}
	
	/**
	 * Preenche combo de tipo sessão
	 * @return lista de tipo sessão.
	 */
	public List<MptTipoSessao> listarTipoSessao(){
		this.listaMptTipoSessao =  procedimentoTerapeuticoFacade.listarMptTiposSessao();
		return this.listaMptTipoSessao;
	}
	
	/**
	 * Método usado no suggestionBox de medicamento.
	 * @param objPesquisa
	 * @return Quantidade de registros.
	 */
	public Long obterMedicamentosAtivosCount(String objPesquisa) {
		return procedimentoTerapeuticoFacade.obterMedicamentosAtivosCount(objPesquisa);
	}
	
	/**
	 * Preenche suggestionBox de medicamento.
	 * @param objPesquisa
	 * @return lista de medicamentos.
	 */
	public List<AfaMedicamento> obterMedicamentosAtivos(String objPesquisa) {
		return this.returnSGWithCount(procedimentoTerapeuticoFacade.obterMedicamentosAtivos(objPesquisa), this.obterMedicamentosAtivosCount(objPesquisa));
	}
	
	/**
	 * Prepara exclusão.
	 * @param itemExclusao
	 */
	public void prepararExclusao(ProtocoloVO itemExclusao) {
		this.itemExclusao = itemExclusao;
		RequestContext.getCurrentInstance().execute("PF('modal_excluir').show()");
	}
	
	/**
	 * Exclui protocolo.
	 */
	public void excluir() {
		procedimentoTerapeuticoFacade.excluirVersaoProtocolo(itemExclusao);
		this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_EXCLUIDO");
	}
	
	/**
	 * Obtém descrição truncada.
	 * @param item
	 * @param tamanhoMaximo
	 * @return Descrição truncada
	 */
	public String obterDescricaoTruncada(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}
	
	/**
	 * Ação do botão Limpar
	 */
	public void limpar() {		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {			
			limparValoresSubmetidos(componentes.next());
		}
		
		this.dataModel.limparPesquisa();
		iniciar();		
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public String pagProtocolo(){
		return PAGE_CADASTRA_PROTOCOLO;
	}
	
	public String cadastrarProtocolo(){
		Boolean novo = true;
		this.cadastrarProtocoloController.setItemSelecionado(tipoSessao);
		this.cadastrarProtocoloController.setNovo(novo);
		return pagProtocolo();
	}
	
	public String editarProtocolo(){
		Boolean novo = false;
		this.cadastrarProtocoloController.setItemSelecionado(tipoSessao);
		this.cadastrarProtocoloController.setParametroSelecionado(parametroSelecionado);
		this.cadastrarProtocoloController.setNovo(novo);
		return pagProtocolo();
	}
	
	public void obterHistoricoAlteracoes(ProtocoloVO item){
		this.parametroSelecionado = item;
		if(item != null){
			this.protocolo = item.getSeqProtocoloSessao();
		}
		historicoAlteracoes = procedimentoTerapeuticoFacade.obterSituacaoVersaoProtocoloSelecionado(item.getSeqProtocoloSessao());
		if(historicoAlteracoes != null && !historicoAlteracoes.isEmpty()){
			tituloDescricao = historicoAlteracoes.get(0).getTituloProtocoloSessao();
		}
		openDialog("modalHistoricoDaVersaoWG");		
	}
	
	public String exibirTelaRelacionamento(ProtocoloVO item){
		String tela = null;
		if(!this.procedimentoTerapeuticoFacade.verificarStatusProtocolo(item.getSeqProtocoloSessao(), item.getSeqVersaoProtocoloSessao())){
			this.apresentarMsgNegocio(Severity.INFO, "LABEL_MS05");
			cadastrarRelacionamentoProtocoloPaginatorController.setImpedirRelacionamento(false);
		}else{
			cadastrarRelacionamentoProtocoloPaginatorController.setImpedirRelacionamento(false);
			tela = PAGE_RELACIONAR_PROTOCOLO;
		}
		cadastrarRelacionamentoProtocoloPaginatorController.setDescricaoTitulo(item.getTituloProtocoloSessao());
		cadastrarRelacionamentoProtocoloPaginatorController.setSeqProtocolo(item.getSeqProtocoloSessao());
		return tela;	
	}
	
	
	public String acessarCadastroTratamento(SituacaoVersaoProtocoloVO item){
		Boolean novo = false;
		cadastrarProtocoloController.setParametroSelecionado(this.parametroSelecionado);
//		cadastrarProtocoloController.getParametroSelecionado().setSeqProtocoloSessao(item.getSeq());
		cadastrarProtocoloController.setModoVisualizacao(Boolean.TRUE);
//		cadastrarProtocoloController.getParametroSelecionado().setSeqVersaoProtocoloSessao(item.getSeqVersaoProtocoloSessao());
		cadastrarProtocoloController.getParametroSelecionado().setIndSituacaoVersaoProtocoloSessao(item.getIndSituacaoVersaoProtocoloSessao());
		cadastrarProtocoloController.setSituacaoProtocolo(item.getIndSituacaoVersaoProtocoloSessao());
		cadastrarProtocoloController.getParametroSelecionado().setVersao(item.getVersao());
		cadastrarProtocoloController.setNovo(novo);
		cadastrarProtocoloController.setItemSelecionado(tipoSessao);
		cadastrarProtocoloController.setExibeLupa(Boolean.TRUE);
		this.fromBack = true;
		return pagProtocolo();
	}
	
	public Boolean verificarExistenciaProtocolo(ProtocoloVO item){
		return this.procedimentoTerapeuticoFacade.verificarExistenciaProtocolo(item.getSeqProtocoloSessao());
	}
	
	/**
	 * Método que desabilita os botoes da lateral da grid.
	 */
	public void habilitarBotoes(){
		habilitarBotoes = false;
		verificarSituacao();
		habilitarHomologarProtocolo();
	}
	
	public void verificarSituacao(){
		this.habilitaNovaVersao = false;
		boolean temPermissao = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "criarNovaVersao", "criar");
		if(!temPermissao){
			habilitaNovaVersao = false;
			return;
		}else{
			if(selecaoGrid != null){
				boolean existeOutraVersao = procedimentoTerapeuticoFacade.verificarExistenciaOutraVersaoProtocoloSessao(selecaoGrid.getSeqProtocoloSessao(), tipoSessao.getSeq(), selecaoGrid.getSeqVersaoProtocoloSessao());
				if(selecaoGrid.getIndSituacaoVersaoProtocoloSessao().equals(situacaoProtocoloLiberada) && !existeOutraVersao){
					habilitaNovaVersao =  true;
					return;
				}else{
					if(selecaoGrid.getIndSituacaoVersaoProtocoloSessao().equals(situacaoProtocoloInativo) && !existeOutraVersao){
						habilitaNovaVersao = true;
						return;
					}
				}
			}	
		}
	}
	
	public String redirecionarNovaVersao(){
		this.habilitaNovaVersao = false;
		return PAGE_CADASTRA_PROTOCOLO;
	}

	/**
	 * Copiar um protocolo
	 * @return String
	 */
	public String copiarProtocolo(){
		this.cadastrarProtocoloController.setHabilitarVersaoCopiarProtocolo(true);
		cadastrarProtocoloController.setSeqVersaoProtocoloSessao(selecaoGrid.getSeqVersaoProtocoloSessao());		
		cadastrarProtocoloController.setCopiarProtocolo(true);
		if (this.selecaoGrid.getDiasTratamento() != null) {
			this.cadastrarProtocoloController.setDiasTratamento(this.selecaoGrid.getDiasTratamento());
		} else {
			this.cadastrarProtocoloController.setDiasTratamento(Short.valueOf("0"));
		}
		return PAGE_CADASTRA_PROTOCOLO;
	}
	
	public void habilitarHomologarProtocolo(){
		boolean visualizar = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarHomologacaoProtocolo", "visualizar");
		boolean homologar = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "homologarProtocolo", "alterar");
		if(visualizar || homologar){
			if(selecaoGrid.getIndSituacaoVersaoProtocoloSessao().equals(situacaoProtocoloEmHomologacao)){
				habilitaHomologarProtocolo = true;
			}else{
				habilitaHomologarProtocolo = false;
			}
		}else{
			habilitaHomologarProtocolo = false;
		}
	}	
	
	//GETs e SETs
	
	public String getTituloDescricao() {
		if(tituloDescricao != null && tituloDescricao.length() > 50){
			return tituloDescricao.substring(0,50);
		}
		return tituloDescricao;
	}

	public void setTituloDescricao(String tituloDescricao) {
		this.tituloDescricao = tituloDescricao;
	}
	
	public List<SituacaoVersaoProtocoloVO> getHistoricoAlteracoes() {
		return historicoAlteracoes;
	}
	
	public void setHistoricoAlteracoes(List<SituacaoVersaoProtocoloVO> historicoALteracoes) {
		this.historicoAlteracoes = historicoALteracoes;
	}
	
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public String getDescricaoProtocolo() {
		return descricaoProtocolo;
	}

	public void setDescricaoProtocolo(String descricaoProtocolo) {
		this.descricaoProtocolo = descricaoProtocolo;
	}

	public List<MptTipoSessao> getListaMptTipoSessao() {
		return listaMptTipoSessao;
	}

	public void setListaMptTipoSessao(List<MptTipoSessao> listaMptTipoSessao) {
		this.listaMptTipoSessao = listaMptTipoSessao;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DominioSituacaoProtocolo getSituacaoProtocolo() {
		return situacaoProtocolo;
	}

	public void setSituacaoProtocolo(DominioSituacaoProtocolo situacaoProtocolo) {
		this.situacaoProtocolo = situacaoProtocolo;
	}

	public DynamicDataModel<ProtocoloVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ProtocoloVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ProtocoloVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ProtocoloVO filtro) {
		this.filtro = filtro;
	}

	public ProtocoloVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(ProtocoloVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	
	public DominioSituacaoProtocolo getSituacaoProtocoloConstrucao() {
		return situacaoProtocoloConstrucao;
	}

	public ProtocoloVO getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(ProtocoloVO itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public DominioSituacaoProtocolo getSituacaoProtocoloEmHomologacao() {
		return situacaoProtocoloEmHomologacao;
	}

	public DominioSituacaoProtocolo getSituacaoProtocoloLiberada() {
		return situacaoProtocoloLiberada;
	}

	public DominioSituacaoProtocolo getSituacaoProtocoloInativo() {
		return situacaoProtocoloInativo;
	}

	public ProtocoloVO getSelecaoGrid() {
		return selecaoGrid;
	}

	public void setSelecaoGrid(ProtocoloVO selecaoGrid) {
		this.selecaoGrid = selecaoGrid;
	}
	
	public boolean isHabilitaNovaVersao() {
		return habilitaNovaVersao;
	}

	public void setHabilitaNovaVersao(boolean habilitaNovaVersao) {
		this.habilitaNovaVersao = habilitaNovaVersao;
	}

	public boolean isHabilitarBotoes() {
		return habilitarBotoes;
	}

	public void setHabilitarBotoes(boolean habilitarBotoes) {
		this.habilitarBotoes = habilitarBotoes;
	}

	public boolean isFromBack() {
		return fromBack;
	}

	public void setFromBack(boolean fromBack) {
		this.fromBack = fromBack;
	}

	public Integer getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Integer protocolo) {
		this.protocolo = protocolo;
	}
	
	public boolean isHabilitaHomologarProtocolo() {
		return habilitaHomologarProtocolo;
	}

	public void setHabilitaHomologarProtocolo(boolean habilitaHomologarProtocolo) {
		this.habilitaHomologarProtocolo = habilitaHomologarProtocolo;
	}

	public Boolean getGravarOk() {
		return gravarOk;
	}

	public void setGravarOk(Boolean gravarOk) {
		this.gravarOk = gravarOk;
	}
	
}
