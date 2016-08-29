package br.gov.mec.aghu.patrimonio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da tela PesquisarAceitesTecnicos
 * 
 * @author rafael.nascimento
 */
public class RegistrarAceiteTecnicoPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8795392750923509957L;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private static final String ACAO_GRAVAR = "gravar";
	private static final String ACAO_FINALIZAR = "finalizar";
	private static final String ACAO_CERTIFICAR = "certificar";
	private static final String ACAO_IMPRIMIR = "imprimir";
	private static final String ACAO_PESQUISAR = "pesquisar";
	private static final String ACAO_EXCLUIR = "excluir";
	private static final String ACAO_EXECUTAR = "executar";
	private static final String PERMISSAO_GRAVAR_ACEITE_TECNICO = "gravarAceiteTecnico";
	private static final String PERMISSAO_FINALIZAR_ACEITE_TECNICO = "finalizarAceiteTecnico";
	private static final String PERMISSAO_CERTIFICAR_ACEITE_TECNICO = "CertificarAceiteTecnico";
	private static final String PERMISSAO_IMPRIMIR_ACEITE_TECNICO = "imprimirAceiteTecnico";
	private static final String PERMISSAO_PESQUISAR_ACEITE_TECNICO = "pesquisarAceiteTecnico";
	private static final String PERMISSAO_EXCLUIR_ACEITE_TECNICO = "excluirAceiteTecnico";
	private static final String PERMISSAO_CHEFE_CENTRO_CUSTO = "chefeCentroCusto";
	private static final String PERMISSAO_CHEFE_AREA_TECNICA_AVALIACAO = "chefeAreaTecnicaAvaliacao";
	private static final String PERMISSAO_CHEFIA_PATRIMONIO_AREA_TECNICA_AVALIACAO = "chefiaPatrimonioAreaTecnicaAvaliacao";
	private static final String PAGE_REGISTRAR_ACEITE_TECNICO = "patrimonio-registrarAceiteTecnicoCRUD";
	
	private boolean permissaoGravarAceiteTecnico;
	private boolean permissaoFinalizarAceiteTecnico;
	private boolean permissaoCertificarAceiteTecnico;
	private boolean permissaoImprimirAceiteTecnico;
	private boolean permissaoPesquisarAceiteTecnico;
	private boolean permissaoExcluirAceiteTecnico;
	private boolean permissaoChefeCentroCusto;
	private boolean permissaoChefeAreaTecnicaAvaliacao;
	private boolean permissaochefiaPatrimonioAreaTecnicaAvaliacao;
	private boolean permissaoParaEditar;
	private boolean vinculoCentroCusto;
	private AvaliacaoTecnicaVO filtro;
	private AvaliacaoTecnicaVO itemSelecionado;
	private AceiteTecnicoParaSerRealizadoVO aceiteTecRealizadoVO;
	private RapServidores servidor;
	private String loginUsuarioLogado;
	
	@Inject	@Paginator
	private DynamicDataModel<AvaliacaoTecnicaVO> dataModel;
	
	@Inject
	private RegistrarAceiteTecnicoController registrarAceiteTecnicoController;
	
	//METODOS
	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	/**
	 * Metodo invocado ao carregar a tela.
	 */
	public void iniciar() {
		resetarVariaveis();
		carregarPermissoes();
		preencherFiltro();
	}

	/**
	 * Inicializa variaveis da tela.
	 */
	private void resetarVariaveis() {
		if (this.filtro == null) {
			this.filtro = new AvaliacaoTecnicaVO();
		}
	}
	
	/**
	 * Carrega o filtro.
	 */
	private void preencherFiltro() {
		if (this.dataModel == null || !this.dataModel.getPesquisaAtiva()) {
			this.servidor = servidorLogadoFacade.obterServidorLogadoSemCache();
			
			PtmAreaTecAvaliacao areaCentroCusto = patrimonioFacade.obterAreaTecPorServidor(this.servidor);
			if(areaCentroCusto != null && areaCentroCusto.getFccCentroCustos() != null){
				this.filtro.setCentroCusto(areaCentroCusto.getFccCentroCustos());
			}else if(servidor.getCentroCustoAtuacao() != null){
				filtro.setCentroCusto(servidor.getCentroCustoAtuacao());
			}else if(servidor.getCentroCustoDesempenho() != null){
				filtro.setCentroCusto(servidor.getCentroCustoDesempenho());
			}
			
			if(this.permissaoChefeCentroCusto || 
					this.permissaoChefeAreaTecnicaAvaliacao || 
						this.permissaochefiaPatrimonioAreaTecnicaAvaliacao){
				this.vinculoCentroCusto = Boolean.TRUE;
			}
			
			if(this.aceiteTecRealizadoVO != null){
				if(this.aceiteTecRealizadoVO.getItemRecebimento() != null && this.aceiteTecRealizadoVO.getRecebimento() != null){
					filtro.setItemRecebimento(new PtmItemRecebProvisorios());
					filtro.getItemRecebimento().setNroItem(this.aceiteTecRealizadoVO.getItemRecebimento());
					filtro.getItemRecebimento().setNrpSeq(this.aceiteTecRealizadoVO.getRecebimento());
					this.pesquisar();
				}
			}
		}
	}

	/**
	 * Verifica as permissões que o usuario possui.
	 */
	private void carregarPermissoes() {
		this.loginUsuarioLogado = obterLoginUsuarioLogado();
		this.permissaoGravarAceiteTecnico = usuarioTemPermissao(PERMISSAO_GRAVAR_ACEITE_TECNICO, ACAO_GRAVAR);
		this.permissaoFinalizarAceiteTecnico = usuarioTemPermissao(PERMISSAO_FINALIZAR_ACEITE_TECNICO, ACAO_FINALIZAR);
		this.permissaoCertificarAceiteTecnico = usuarioTemPermissao(PERMISSAO_CERTIFICAR_ACEITE_TECNICO, ACAO_CERTIFICAR);
		this.permissaoImprimirAceiteTecnico = usuarioTemPermissao(PERMISSAO_IMPRIMIR_ACEITE_TECNICO, ACAO_IMPRIMIR);
		this.permissaoPesquisarAceiteTecnico = usuarioTemPermissao(PERMISSAO_PESQUISAR_ACEITE_TECNICO, ACAO_PESQUISAR);
		this.permissaoExcluirAceiteTecnico = usuarioTemPermissao(PERMISSAO_EXCLUIR_ACEITE_TECNICO, ACAO_EXCLUIR);
		this.permissaoChefeCentroCusto = usuarioTemPermissao(PERMISSAO_CHEFE_CENTRO_CUSTO, ACAO_EXECUTAR);
		this.permissaoChefeAreaTecnicaAvaliacao = usuarioTemPermissao(PERMISSAO_CHEFE_AREA_TECNICA_AVALIACAO, ACAO_EXECUTAR);
		this.permissaochefiaPatrimonioAreaTecnicaAvaliacao = usuarioTemPermissao(PERMISSAO_CHEFIA_PATRIMONIO_AREA_TECNICA_AVALIACAO, ACAO_EXECUTAR);
		
		if(this.permissaoImprimirAceiteTecnico || this.permissaoCertificarAceiteTecnico ||
				this.permissaoFinalizarAceiteTecnico ||	this.permissaoGravarAceiteTecnico){
			this.permissaoParaEditar = true;
		}
	}
	
	/**
	 * Invoca metodo da arquitetura para validar permissão do usuario.
	 */
	private boolean usuarioTemPermissao(String permissao, String acao) {
		return this.permissionService.usuarioTemPermissao(this.loginUsuarioLogado, permissao, acao);
	}
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);
	}
	
	@Override
	public Long recuperarCount() {
		return this.patrimonioFacade.listarAceitetecnicoCount(this.filtro, this.vinculoCentroCusto, this.servidor);
	}
	
	@Override
	public List<AvaliacaoTecnicaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.patrimonioFacade.listaAceiteTecnico(firstResult, maxResult, orderProperty, asc, this.filtro, this.vinculoCentroCusto, this.servidor);
	}
	
	public String patrimonioTruncado(List<DevolucaoBemPermanenteVO> itens, Integer tamanhoMaximo) {
		String resultado = "";
		StringBuilder patrimonioBuilder = new StringBuilder();
		resultado = concatenarPatrimonio(itens, resultado, patrimonioBuilder);
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		return resultado;
	}
	
	public String hintPatrimonio(List<DevolucaoBemPermanenteVO> listaNumeroBens) {
		String resultado = "";
		StringBuilder patrimonioBuilder = new StringBuilder();
		resultado = concatenarPatrimonio(listaNumeroBens, resultado, patrimonioBuilder);
		return resultado;
	}

	private String concatenarPatrimonio(List<DevolucaoBemPermanenteVO> listaNumeroBens, String resultado, StringBuilder patrimonioBuilder) {
		if (listaNumeroBens == null || listaNumeroBens.isEmpty()) {
			return resultado;
		} else {
			boolean primeira = true;
			for (DevolucaoBemPermanenteVO numeroBem : listaNumeroBens) {
				if (numeroBem.getPbpNrBem() != null) {
					if (primeira) {
						resultado = patrimonioBuilder.append(numeroBem.getPbpNrBem()).toString();
						primeira = false;
					} else {
						resultado = patrimonioBuilder.append(" , " + numeroBem.getPbpNrBem()).toString();
					}
				}
			}
		}
		return resultado;
	}
	
	/**
	 * Metodos para atualizar o filtro.
	 */
	public void refreshFromMarca() {
		filtro.setMarcaComercial(null);
	}
	public void refreshFromModelo() {
		filtro.setMarcaModelo(null);
	}
	
	public void refreshFromCentroCusto() {
		filtro.setCentroCusto(null);
	}
	
	public void refreshFromResponsavelTec() {
		filtro.setResponsavelTecnico(null);
	}
	
	public void refreshFromItemReceb() {
		filtro.setItemRecebimento(null);
	}
	
	public void refreshFromPatrimonio() {
		filtro.setPatrimonio(null);
	}
	
	/**
	 * Ação do botão Limpar
	 */
	public void limpar(){
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		
		this.filtro.setSituacao(null);
		this.filtro.setStatus(null);
		this.filtro.setDtFim(null);
		this.filtro.setDtInicio(null);
		this.filtro.setCentroCusto(null);
		this.filtro.setPatrimonio(null);
		this.filtro.setMarcaComercial(null);
		this.filtro.setMarcaModelo(null);
		this.filtro.setResponsavelTecnico(null);
		this.filtro.setItemRecebimento(null);
		this.itemSelecionado = new AvaliacaoTecnicaVO();
		this.dataModel.setPesquisaAtiva(false);
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
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
	
	/**
	 * Encaminha para tela de cadastro do aceite tecnico
	 * @return
	 */
	public String inserir(){
		this.registrarAceiteTecnicoController.setModoEdicao(false);
		return PAGE_REGISTRAR_ACEITE_TECNICO;
	}

	public String alterar(){
		return PAGE_REGISTRAR_ACEITE_TECNICO;
	}
	
	public void excluirAvaliacaoTecnica(){
		try {
			if(DominioSituacaoAceiteTecnico.N.equals(itemSelecionado.getIndSituacaoAvaliacaoTec())){
				this.patrimonioFacade.excluirAvaliacaoTecnica(itemSelecionado.getSeqAvaliacaoTec(), this.servidor);
				apresentarMsgNegocio(Severity.INFO, "ACEITE_TECNICO_EXCLUIDO_COM_SUCESSO");
			}else if(DominioSituacaoAceiteTecnico.F.equals(itemSelecionado.getIndSituacaoAvaliacaoTec())){
				apresentarMsgNegocio(Severity.ERROR, "FALHA_EXCLUIR_ACEITE_TECNICO_FINALIZADO");
			}else if(DominioSituacaoAceiteTecnico.C.equals(itemSelecionado.getIndSituacaoAvaliacaoTec())){
				apresentarMsgNegocio(Severity.ERROR, "FALHA_EXCLUIR_ACEITE_TECNICO_CERTIFICADO");
			}else{
				apresentarMsgNegocio(Severity.INFO, "FALHA_EXCLUIR_ACEITE_TECNICO");
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	/**
	 * Obtem descrição truncada.
	 */
	public String truncar(String descricao, Integer tamanho) {
		if (descricao.length() > tamanho) {
			descricao = StringUtils.abbreviate(descricao, tamanho);
		}
		return descricao;
	}
	
	/** SUGGESTIONS*/
	public List<ScoMarcaComercial> listarMarcaComercialPorCodigoDescricao(String param) {
		return returnSGWithCount(this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoSemLucene(param), 
				this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoCount(param));
	}
	
	public List<ScoMarcaModelo> listarMarcaModeloPorCodigoDescricao(String param){
		return returnSGWithCount(this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoSemLucene(param, this.filtro.getMarcaComercial(), null),
				this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoCount(param, this.filtro.getMarcaComercial(), null));
	}
	
	public Long listarMarcaModeloPorCodigoDescricaoCount(){
		return this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoCount(null, this.filtro.getMarcaComercial(), null);
	}
	
	public List<FccCentroCustos> listarCentroCustosPorCodigoDescricao(String param) {
		return returnSGWithCount(this.patrimonioFacade.pesquisarCentroCustosPorServidorLogadoDescricao(param, this.servidor),
				this.patrimonioFacade.pesquisarCentroCustosPorServidorLogadoDescricaoCount(param, this.servidor));
	}
	
	public List<RapServidores> listarResponsavelTecnico(String param) {
		return returnSGWithCount(this.registroColaboradorFacade.obterServidorPorMatriculaVinculoOuNome(param),
								 this.registroColaboradorFacade.obterServidorPorMatriculaVinculoOuNomeCount(param));
	}
	
	public List<PtmBemPermanentes> listarRecebItemPatrimonio(String param) {
		return returnSGWithCount(this.patrimonioFacade.obterPatrimonioPorNrBemDetalhamento(param),
				this.patrimonioFacade.obterPatrimonioPorNrBemDetalhamentoCount(param));
	}
	
	public List<PtmItemRecebProvisorios> listarItemRecebimento(String param) {
		return returnSGWithCount(this.patrimonioFacade.listarPatrimonioPorItemReceb(param, servidor),
				this.patrimonioFacade.listarPatrimonioPorItemRecebCount(param, servidor));
	}
	
	//GETTERS AND SETTERS
	public IPermissionService getPermissionService() {
		return permissionService;
	}
	public void setPermissionService(IPermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public boolean isPermissaoGravarAceiteTecnico() {
		return permissaoGravarAceiteTecnico;
	}
	public void setPermissaoGravarAceiteTecnico(boolean permissaoGravarAceiteTecnico) {
		this.permissaoGravarAceiteTecnico = permissaoGravarAceiteTecnico;
	}
	public boolean isPermissaoFinalizarAceiteTecnico() {
		return permissaoFinalizarAceiteTecnico;
	}
	public void setPermissaoFinalizarAceiteTecnico(
			boolean permissaoFinalizarAceiteTecnico) {
		this.permissaoFinalizarAceiteTecnico = permissaoFinalizarAceiteTecnico;
	}
	public boolean isPermissaoCertificarAceiteTecnico() {
		return permissaoCertificarAceiteTecnico;
	}
	public void setPermissaoCertificarAceiteTecnico(
			boolean permissaoCertificarAceiteTecnico) {
		this.permissaoCertificarAceiteTecnico = permissaoCertificarAceiteTecnico;
	}
	public boolean isPermissaoImprimirAceiteTecnico() {
		return permissaoImprimirAceiteTecnico;
	}
	public void setPermissaoChefeCentroCusto(
			boolean permissaoChefeCentroCusto) {
		this.permissaoChefeCentroCusto = permissaoChefeCentroCusto;
	}
	public boolean isPermissaoChefeCentroCusto() {
		return permissaoChefeCentroCusto;
	}
	public void setPermissaoChefeAreaTecnicaAvaliacao(
			boolean permissaoChefeAreaTecnicaAvaliacao) {
		this.permissaoChefeAreaTecnicaAvaliacao = permissaoChefeAreaTecnicaAvaliacao;
	}
	public boolean isPermissaoChefeAreaTecnicaAvaliacao() {
		return permissaoChefeAreaTecnicaAvaliacao;
	}
	public void setPermissaoImprimirAceiteTecnico(
			boolean permissaoImprimirAceiteTecnico) {
		this.permissaoImprimirAceiteTecnico = permissaoImprimirAceiteTecnico;
	}
	public boolean isPermissaoPesquisarAceiteTecnico() {
		return permissaoPesquisarAceiteTecnico;
	}
	public void setPermissaoPesquisarAceiteTecnico(
			boolean permissaoPesquisarAceiteTecnico) {
		this.permissaoPesquisarAceiteTecnico = permissaoPesquisarAceiteTecnico;
	}
	public boolean isPermissaoExcluirAceiteTecnico() {
		return permissaoExcluirAceiteTecnico;
	}
	public void setPermissaoExcluirAceiteTecnico(
			boolean permissaoExcluirAceiteTecnico) {
		this.permissaoExcluirAceiteTecnico = permissaoExcluirAceiteTecnico;
	}
	public boolean isPermissaochefiaPatrimonioAreaTecnicaAvaliacao() {
		return permissaochefiaPatrimonioAreaTecnicaAvaliacao;
	}
	public void setPermissaochefiaPatrimonioAreaTecnicaAvaliacao(
			boolean permissaochefiaPatrimonioAreaTecnicaAvaliacao) {
		this.permissaochefiaPatrimonioAreaTecnicaAvaliacao = permissaochefiaPatrimonioAreaTecnicaAvaliacao;
	}
	public boolean isPermissaoParaEditar() {
		return permissaoParaEditar;
	}
	public void setPermissaoParaEditar(boolean permissaoParaEditar) {
		this.permissaoParaEditar = permissaoParaEditar;
	}
	public boolean isVinculoCentroCusto() {
		return vinculoCentroCusto;
	}
	public void setVinculoCentroCusto(boolean vinculoCentroCusto) {
		this.vinculoCentroCusto = vinculoCentroCusto;
	}
	public DynamicDataModel<AvaliacaoTecnicaVO> getDataModel() {
		return dataModel;
	}
	public void setDataModel(DynamicDataModel<AvaliacaoTecnicaVO> dataModel) {
		this.dataModel = dataModel;
	}
	public AvaliacaoTecnicaVO getFiltro() {
		return filtro;
	}
	public void setFiltro(AvaliacaoTecnicaVO filtro) {
		this.filtro = filtro;
	}
	public AvaliacaoTecnicaVO getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(AvaliacaoTecnicaVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	public AceiteTecnicoParaSerRealizadoVO getAceiteTecRealizadoVO() {
		return aceiteTecRealizadoVO;
	}
	public void setAceiteTecRealizadoVO(
			AceiteTecnicoParaSerRealizadoVO aceiteTecRealizadoVO) {
		this.aceiteTecRealizadoVO = aceiteTecRealizadoVO;
	}
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	public String getLoginUsuarioLogado() {
		return loginUsuarioLogado;
	}
	public void setLoginUsuarioLogado(String loginUsuarioLogado) {
		this.loginUsuarioLogado = loginUsuarioLogado;
	}
}