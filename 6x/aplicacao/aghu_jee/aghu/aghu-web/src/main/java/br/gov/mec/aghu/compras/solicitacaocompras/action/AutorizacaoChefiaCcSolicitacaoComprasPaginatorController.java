package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.action.ImprimirSolicitacaoDeComprasController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class AutorizacaoChefiaCcSolicitacaoComprasPaginatorController extends ActionController implements ActionPaginator {

	private static final String CONSULTA_SCSS_LIST = "compras-consultaSCSSList";

	private static final String AUTORIZACAO_CHEFIA_CC_SOLICITACAO_SERVICO = "compras-autorizacaoChefiaCcSolicitacaoServico";

	private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	private static final String IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO = "compras-imprimirSolicitacaoDeComprasPdfCadastro";
	
	private static final String ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";
	
	private static final String AUTORIZACAO_CHEFIA_SOLICITACAO_COMPRAS = "compras-autorizacaoChefiaCcSolicitacaoCompras";

	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoDeCompra> dataModel;

	private static final Log LOG = LogFactory.getLog(AutorizacaoChefiaCcSolicitacaoComprasPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6311943654533755902L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ImprimirSolicitacaoDeComprasController imprimirSolicitacaoDeComprasController;
	
	// filtros
	ScoPontoParadaSolicitacao pontoParada;
	Integer numeroSolicitacaoCompra;
	Date dataInicioSolicitacaoCompra, dataFimSolicitacaoCompra;
	Date dataInicioAutorizacaoCompra, dataFimAutorizacaoCompra;
	ScoGrupoMaterial grupoMaterial;
	ScoMaterial material;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;
	DominioSimNao indAutorizada;
	DominioSimNao indUrgente;
	DominioSimNao indPrioritaria;
	DominioSimNao indDevolvido;
	DominioSimNao indExcluido;
	DominioSimNao indExibirSolic;
	
	private Boolean desabilitaSuggestionGrupoMaterial;
	private Boolean desabilitaFiltros;
	private boolean desabilitaBotoes;
	
	// modal de exclusao
	private String observacaoExclusao;
	
	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;
	
	// modal de devolução
	private String justificativaDevolucao;
	
	// controles checkbox
	private List<ScoSolicitacaoDeCompra> listaChecked;
	private List<ScoSolicitacaoDeCompra> allChecked;
	private List<ScoSolicitacaoDeCompra> listaAtual;
	
	private DominioSimNao lastIndDevolvido, lastIndExcluido, lastIndExibirSc;
	private Boolean pontoParadaChefia;

	private Boolean autorizar;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		if (this.dataModel.getRowCount() == 0){
			this.limparPesquisa();
			this.pesquisar();
		}
	}
	
	public void pesquisar() {
		this.limparControleGrid();
		this.limparModalExclusao();
		this.limparModalEncaminhamento(false);
		this.desligarFiltros();
		this.dataModel.reiniciarPaginator();
		this.desabilitaBotoes = false;
	}
	
	private void verificarPontoParadaChefia(){
		setPontoParadaChefia(this.getSolicitacaoComprasFacade().verificarPontoParadaChefia(allChecked));
	}
	
	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (ScoSolicitacaoDeCompra sc : this.allChecked) {
			this.listaChecked.add(sc);
		}
		this.verificarPontoParadaChefia();
	}

	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(ScoSolicitacaoDeCompra sc : listaAtual) {
				if (!this.allChecked.contains(sc)) {
					this.allChecked.add(sc);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(ScoSolicitacaoDeCompra sc : listaAtual) {
				if (this.allChecked.contains(sc)) {
					this.allChecked.remove(sc);
				}
			}
		}
	}
	
	public void selecionarLinha(SelectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
		} else {
			this.allChecked.add(sc);
		}
		this.verificarPontoParadaChefia();
	}

	public void desmarcarLinha(UnselectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
		} else {
			this.allChecked.add(sc);
		}
		this.verificarPontoParadaChefia();
	}

	// métodos das suggestions
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.getComprasFacade().pesquisarGrupoMaterialPorFiltro(parametro);
	}

	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.contarScoMateriaisGrupoAtiva(param, this.getGrupoMaterial(), false);
	}
	
	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return  this.returnSGWithCount(this.comprasFacade.listarScoMateriais(objPesquisa, null, true),listarMateriaisCount(objPesquisa));
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return this.getCentroCustoFacade().pesquisarCentroCustosPorCodigoDescricao(filtro);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String parametro) {		
		return this.getCentroCustoFacade().pesquisarCentroCustoUsuarioGerarSCSuggestion(parametro);
	}
	
	public Boolean possuiAnexo(Integer slcNumero) {		
		return this.solicitacaoComprasFacade.verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SC, slcNumero);
	}
	
	public Boolean verificarDevolvida(Integer slcNumero) {
		return this.solicitacaoComprasFacade.verificarSolicitacaoDevolvidaAutorizacao(slcNumero);
	}
	
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro) throws BaseException {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return this.getComprasCadastrosBasicosFacade().pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(filtro, 
				((ScoPontoParadaSolicitacao) this.getComprasCadastrosBasicosFacade().obterPontoParadaAutorizacao()).getCodigo());
	}
	
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}
	
	
	// botões de ação
	
	public void encaminharItens() {
		try {			
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.getSolicitacaoComprasFacade().encaminharListaSolicitacaoCompras(listaNumero, 
					this.getComprasCadastrosBasicosFacade().obterPontoParadaAutorizacao(), this.getProximoPontoParada(), 
					this.getFuncionarioComprador(), autorizar);
			
			if (autorizar) {
				if (listaNumero.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_AUTORIZADA_ENCAMINHADA_COM_SUCESSO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_AUTORIZADA_ENCAMINHADA_COM_SUCESSO");	
				}
			} else {
				if (listaNumero.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_ENCAMINHADA_COM_SUCESSO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_ENCAMINHADA_COM_SUCESSO");	
				}
			}
			
			this.limparControleGrid();
			this.limparModalEncaminhamento(false);
			this.closeDialog("modalEncaminharScoWG");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}	
	}
	
	public void devolverItens() {
		try {			
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			getSolicitacaoComprasFacade().devolverSolicitacoesCompras(listaNumero, justificativaDevolucao);

			
			if (listaNumero.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_COMPRAS_DEVOLVIDA_COM_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACOES_COMPRAS_DEVOLVIDA_COM_SUCESSO");	
			}
			
			limparControleGrid();
			limparModalDevolucao();
			this.closeDialog("modalDevolverScoWG");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void excluirItens() {
		try {			
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.getSolicitacaoComprasFacade().inativarListaSolicitacaoCompras(listaNumero, this.observacaoExclusao);
			
			if (listaNumero.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SOL_UNICA_COM_SUCESSO", listaNumero.get(0));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_LISTA_COM_SUCESSO");	
			}
			
			this.limparModalExclusao();
			this.limparControleGrid();
			this.closeDialog("modalExclusaoScoWG");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}
	
	public void autorizarItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.getSolicitacaoComprasFacade().autorizarListaSolicitacaoCompras(listaNumero);
			
			if (listaNumero.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SOL_UNICA_COM_SUCESSO", listaNumero.get(0));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SC_LISTA_COM_SUCESSO");	
			}
						
			this.limparControleGrid();
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public String obterTitleAutorizacao() {
		String descricaoPps = "";
		AghParametros parametroPpsPadrao;
		try {
			parametroPpsPadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PPS_PADRAO_AUTORIZA_SC);
			ScoPontoParadaSolicitacao ppsPadrao = this.comprasCadastrosBasicosFacade.obterPontoParada(parametroPpsPadrao.getVlrNumerico().shortValue());
			
			if (ppsPadrao != null) {
				StringBuilder str = new StringBuilder();
				str.append(this.getBundle().getString("TITLE_BOTAO_AUTORIZAR"))
				.append(WordUtils.capitalizeFully(ppsPadrao.getDescricao()));
				descricaoPps = str.toString();
			} else {
				descricaoPps = "";
			}
		} catch (ApplicationBusinessException e) {
			descricaoPps = "";
		}
		return descricaoPps;
	}
	
	
	public String imprimirItens() throws DocumentException {
		List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
		imprimirSolicitacaoDeComprasController.setNumSolicComps(listaNumero);			
		
		try {
			imprimirSolicitacaoDeComprasController.print(null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		this.limparControleGrid();
		this.dataModel.reiniciarPaginator();
		
		return IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO;
	}
	
	public String redirecionarSolitacaoCompra(){
		return SOLICITACAO_COMPRA_CRUD;
	}

	public String autorizarSs() {
		return AUTORIZACAO_CHEFIA_CC_SOLICITACAO_SERVICO;
	}
	
	public String consultarSolicitacoes(){
		return CONSULTA_SCSS_LIST;
	}
	
	public String anexarDocSolicitacaoCompra(){
		return ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	// paginator
	
	@Override
	public Long recuperarCount() {		
		return this.getSolicitacaoComprasFacade().countSolicitacaoComprasAutorizarSc(this.montarFiltroPesquisa());
	}

	@Override
	public List<ScoSolicitacaoDeCompra> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<ScoSolicitacaoDeCompra> listaResultado = this.getSolicitacaoComprasFacade()
				.pesquisarSolicitacaoComprasAutorizarSc(firstResult,
						maxResults, orderProperty, asc,
						this.montarFiltroPesquisa());
		
		listaAtual.clear();
		listaAtual.addAll(listaResultado);
		return listaResultado;
	}
	
	public void limparFiltroAut() {
		if (this.indExibirSolic.isSim()) {
			this.indAutorizada = DominioSimNao.N;
			this.dataInicioAutorizacaoCompra = null;
			this.dataFimAutorizacaoCompra = null;
		}	
	}
	
	public String obterCorLinha(ScoSolicitacaoDeCompra item) {
		String ret = "";

		if (item != null) {
			if (item.getPontoParadaProxima() != null && !Objects.equals(item.getPontoParadaProxima().getTipoPontoParada(), DominioTipoPontoParada.CH)) {
				ret = "background-color:#66FF99;";
			}
			
			if (item.getUrgente()) {
				ret = "background-color:#F8C88D;";
			}
		}
		
		return ret;
	}
	
	public void desligarFiltros() {
		if (this.getNumeroSolicitacaoCompra() != null) {
			Integer scId = numeroSolicitacaoCompra;
			limparFiltros();
			numeroSolicitacaoCompra = scId;
			this.setDesabilitaFiltros(true);
		} else {
			this.setDesabilitaFiltros(false);
		}
	}
	
	public void verificarGrupoMaterialHabilitado() {
		this.desabilitaSuggestionGrupoMaterial = ((this.getMaterial() != null && this.getGrupoMaterial() == null) ? true : false);
	}
	
	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(this.getComprasCadastrosBasicosFacade().verificarPontoParadaComprador(this.proximoPontoParada));
	}
	
	public void limparPesquisa() {
		this.limparFiltros();
		this.limparModalExclusao();
		this.limparControleGrid();
		this.limparModalEncaminhamento(false);
		this.setDesabilitaBotoes(false);
	}
	
	private PesqLoteSolCompVO montarFiltroPesquisa () {
		
		PesqLoteSolCompVO filtrosPesquisa = new PesqLoteSolCompVO();
		
		if (this.getNumeroSolicitacaoCompra() == null) {
			if (DominioSimNao.S.equals(getIndAutorizada())) {
				filtrosPesquisa.setPontoParada(null);
			} else {
				try {
					filtrosPesquisa.setPontoParada(this
							.getComprasCadastrosBasicosFacade()
							.obterPontoParadaAutorizacao());
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			
			filtrosPesquisa.setSolicitacaoAutorizada(this.getIndAutorizada());
		}
		
		filtrosPesquisa.setNumeroSolicitacaoCompra(this.getNumeroSolicitacaoCompra());
		
		if (dataInicioSolicitacaoCompra != null) {
			filtrosPesquisa.setDataInicioSolicitacaoCompra(DateUtil
					.obterDataComHoraInical(dataInicioSolicitacaoCompra));
		}

		if (dataFimSolicitacaoCompra != null) {
			filtrosPesquisa.setDataFimSolicitacaoCompra(DateUtil
					.obterDataComHoraFinal(dataFimSolicitacaoCompra));
		}

		if (dataInicioAutorizacaoCompra != null) {
			filtrosPesquisa.setDataInicioAutorizacaoSolicitacaoCompra(DateUtil
					.obterDataComHoraInical(dataInicioAutorizacaoCompra));
		}

		if (dataFimAutorizacaoCompra != null) {
			filtrosPesquisa.setDataFimAutorizacaoSolicitacaoCompra(DateUtil
					.obterDataComHoraFinal(dataFimAutorizacaoCompra));
		}
		
		if (dataInicioAutorizacaoCompra != null) {
			filtrosPesquisa.setDataInicioAutorizacaoSolicitacaoCompra(DateUtil
					.obterDataComHoraInical(dataInicioAutorizacaoCompra));
		}

		if (dataFimAutorizacaoCompra != null) {
			filtrosPesquisa.setDataFimAutorizacaoSolicitacaoCompra(DateUtil
					.obterDataComHoraFinal(dataFimAutorizacaoCompra));
		}		

		filtrosPesquisa.setSolicitacaoDevolvida(this.getIndDevolvido());
		filtrosPesquisa.setSolicitacaoExcluida(this.getIndExcluido());
		filtrosPesquisa.setGrupoMaterial(this.getGrupoMaterial());
		filtrosPesquisa.setMaterial(this.getMaterial());
		filtrosPesquisa.setCentroCustoSolicitante(this.getCentroCustoSolicitante());
		filtrosPesquisa.setCentroCustoAplicacao(this.getCentroCustoAplicacao());
		filtrosPesquisa.setSolicitacaoUrgente(this.getIndUrgente());
		filtrosPesquisa.setSolicitacaoPrioritaria(this.getIndPrioritaria());
		if (this.getIndExibirSolic() != null) {
			filtrosPesquisa.setExibirScSolicitante(this.getIndExibirSolic().isSim());
		} else {
			filtrosPesquisa.setExibirScSolicitante(true);
		}
		
		return filtrosPesquisa;
	}

	// por favor nao unifique estes metodos, existe um motivo para os mesmos estarem separados.
	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.autorizar = Boolean.FALSE;
		this.voltarPanel = false;
	}
	
	public void limparModalEncaminhamento(Boolean isAutorizacao) {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.autorizar = isAutorizacao;
		this.voltarPanel = false;
	}
	
	public void limparModalDevolucao() {
		this.justificativaDevolucao = null;
		this.voltarPanel = false;
	}
	
	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ScoSolicitacaoDeCompra>());
		}
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ScoSolicitacaoDeCompra>();	
		}
		this.listaChecked.clear();
		this.allChecked.clear();
	}
	
	public void limparModalExclusao() {
		this.observacaoExclusao = null;
		this.voltarPanel = false;
	}
	
	private void limparFiltros() {
		this.setListaChecked(new ArrayList<ScoSolicitacaoDeCompra>());
		this.allChecked = new ArrayList<ScoSolicitacaoDeCompra>();
		this.listaAtual = new ArrayList<ScoSolicitacaoDeCompra>();
		this.pontoParada = null;
		this.numeroSolicitacaoCompra = null;
		this.dataInicioSolicitacaoCompra = null;
		this.dataFimSolicitacaoCompra = null;
		this.dataFimAutorizacaoCompra = null;
		this.dataInicioAutorizacaoCompra = null;
		this.grupoMaterial = null;
		this.material = null;
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;	
		this.indPrioritaria = null;
		this.indDevolvido = null;
		this.indUrgente = null;
		this.desabilitaSuggestionGrupoMaterial = false;
		this.desabilitaFiltros = false;
		setIndAutorizada(DominioSimNao.N);
		this.setIndExibirSolic(DominioSimNao.N);
		this.lastIndExibirSc = DominioSimNao.N;		
		refreshFromAutorizada();
	}

	/**
	 * Melhoria #23734
	 * 
	 * Na consulta padrão para autorização as combos devolvida e excluida devem
	 * ser sempre N.
	 */
	public void refreshFromAutorizada() {	
		if (DominioSimNao.N.equals(getIndAutorizada())) {
			lastIndDevolvido = indDevolvido;
			lastIndExcluido = indExcluido;
//			this.indDevolvido = DominioSimNao.N;
			this.indExcluido = DominioSimNao.N;
			this.indExibirSolic = lastIndExibirSc;
			this.dataInicioAutorizacaoCompra = null;
			this.dataFimAutorizacaoCompra = null;			
		} else {
			indDevolvido = lastIndDevolvido;
			indExcluido = lastIndExcluido;
			lastIndExibirSc = indExibirSolic;
			this.indExibirSolic = DominioSimNao.N;			
		}
	}
	
	// gets and sets
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}
	
	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	public void setCentroCustoFacade(ICentroCustoFacade centroCustoFacade) {
		this.centroCustoFacade = centroCustoFacade;
	}

	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}

	public Date getDataInicioSolicitacaoCompra() {
		return dataInicioSolicitacaoCompra;
	}

	public void setDataInicioSolicitacaoCompra(Date dataInicioSolicitacaoCompra) {
		this.dataInicioSolicitacaoCompra = dataInicioSolicitacaoCompra;
	}

	public Date getDataFimSolicitacaoCompra() {
		return dataFimSolicitacaoCompra;
	}

	public void setDataFimSolicitacaoCompra(Date dataFimSolicitacaoCompra) {
		this.dataFimSolicitacaoCompra = dataFimSolicitacaoCompra;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	
	public static String getAutorizacaoChefiaSolicitacaoCompras() {
		return AUTORIZACAO_CHEFIA_SOLICITACAO_COMPRAS;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}

	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	
	public String getObservacaoExclusao() {
		return observacaoExclusao;
	}

	public void setObservacaoExclusao(String observacaoExclusao) {
		this.observacaoExclusao = observacaoExclusao;
	}
	
	public ScoPontoParadaSolicitacao getProximoPontoParada() {
		return proximoPontoParada;
	}

	public void setProximoPontoParada(ScoPontoParadaSolicitacao proximoPontoParada) {
		this.proximoPontoParada = proximoPontoParada;
	}

	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}
	
	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public String getJustificativaDevolucao() {
		return justificativaDevolucao;
	}

	public void setJustificativaDevolucao(String justificativaDevolucao) {
		this.justificativaDevolucao = justificativaDevolucao;
	}

	public Boolean getDesabilitaSuggestionComprador() {
		return desabilitaSuggestionComprador;
	}

	public void setDesabilitaSuggestionComprador(
			Boolean desabilitaSuggestionComprador) {
		this.desabilitaSuggestionComprador = desabilitaSuggestionComprador;
	}

	public ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}

	public void setSolicitacaoComprasFacade(ISolicitacaoComprasFacade solicitacaoComprasFacade) {
		this.solicitacaoComprasFacade = solicitacaoComprasFacade;
	}

	public Boolean getDesabilitaSuggestionGrupoMaterial() {
		return desabilitaSuggestionGrupoMaterial;
	}

	public void setDesabilitaSuggestionGrupoMaterial(
			Boolean desabilitaSuggestionGrupoMaterial) {
		this.desabilitaSuggestionGrupoMaterial = desabilitaSuggestionGrupoMaterial;
	}

	public DominioSimNao getIndUrgente() {
		return indUrgente;
	}

	public void setIndUrgente(DominioSimNao indUrgente) {
		this.indUrgente = indUrgente;
	}

	public DominioSimNao getIndPrioritaria() {
		return indPrioritaria;
	}

	public void setIndPrioritaria(DominioSimNao indPrioritaria) {
		this.indPrioritaria = indPrioritaria;
	}

	public Boolean getDesabilitaFiltros() {
		return desabilitaFiltros;
	}

	public void setDesabilitaFiltros(Boolean desabilitaFiltros) {
		this.desabilitaFiltros = desabilitaFiltros;
	}

	public boolean isDesabilitaBotoes() {
		return desabilitaBotoes;
	}

	public void setDesabilitaBotoes(boolean desabilitaBotoes) {
		this.desabilitaBotoes = desabilitaBotoes;
	}

	public DominioSimNao getIndAutorizada() {
		return indAutorizada;
	}

	public void setIndAutorizada(DominioSimNao indAutorizada) {
		this.indAutorizada = indAutorizada;
	}

	public DominioSimNao getIndDevolvido() {
		return indDevolvido;
	}

	public void setIndDevolvido(DominioSimNao indDevolvido) {
		this.indDevolvido = indDevolvido;
	}

	public DominioSimNao getIndExcluido() {
		return indExcluido;
	}

	public void setIndExcluido(DominioSimNao indExcluido) {
		this.indExcluido = indExcluido;
	}

	public Boolean getPontoParadaChefia() {
		return pontoParadaChefia;
	}

	public void setPontoParadaChefia(Boolean pontoParadaChefia) {
		this.pontoParadaChefia = pontoParadaChefia;
	}

	public Boolean getAutorizar() {
		return autorizar;
	}

	public void setAutorizar(Boolean autorizar) {
		this.autorizar = autorizar;
	}
	public Date getDataInicioAutorizacaoCompra() {
		return dataInicioAutorizacaoCompra;
	}

	public void setDataInicioAutorizacaoCompra(Date dataInicioAutorizacaoCompra) {
		this.dataInicioAutorizacaoCompra = dataInicioAutorizacaoCompra;
	}

	public Date getDataFimAutorizacaoCompra() {
		return dataFimAutorizacaoCompra;
	}

	public void setDataFimAutorizacaoCompra(Date dataFimAutorizacaoCompra) {
		this.dataFimAutorizacaoCompra = dataFimAutorizacaoCompra;
	}
	
	public DominioSimNao getIndExibirSolic() {
		return indExibirSolic;
	}

	public void setIndExibirSolic(DominioSimNao indExibirSolic) {
		this.indExibirSolic = indExibirSolic;
	}	
	
	public DynamicDataModel<ScoSolicitacaoDeCompra> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoDeCompra> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public DominioSimNao getLastIndExibirSc() {
		return lastIndExibirSc;
	}

	public void setLastIndExibirSc(DominioSimNao lastIndExibirSc) {
		this.lastIndExibirSc = lastIndExibirSc;
	}

	public List<ScoSolicitacaoDeCompra> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ScoSolicitacaoDeCompra> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ScoSolicitacaoDeCompra> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ScoSolicitacaoDeCompra> allChecked) {
		this.allChecked = allChecked;
	}	
}