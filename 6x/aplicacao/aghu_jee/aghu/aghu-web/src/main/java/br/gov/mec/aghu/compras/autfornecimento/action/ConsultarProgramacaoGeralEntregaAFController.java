package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.action.ManterAutorizacaoFornecimentoPendenteEntregaController;
import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ValidacaoFiltrosVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioVizAutForn;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;


public class ConsultarProgramacaoGeralEntregaAFController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ProgrGeralEntregaAFVO> dataModel;

	private static final Log LOG = LogFactory.getLog(ConsultarProgramacaoGeralEntregaAFController.class);

	private static final long serialVersionUID = -6037663462279199072L;
	
	private static final String PESQUISA_PLANEJAMENTO_ENTREGA_ITEM_AF = "pesquisarPlanjProgEntregaItensAF.xhtml";
	
	private static final String EDITAR_AF = "autorizacaoFornecimentoCRUD.xhtml";
	
	private static final String VISUALIZAR_PARCELAS = "compras-manterAutorizacaoFornecimentoPendenteEntrega";
	
	private static final String IMPRIMIR_PREV_PROGRAMACAO = "imprimirPrevisaoProgramacaoPdf.xhtml";
	

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@Inject
	private ImprimirPrevisaoProgramacaoController imprimirPrevisaoProgramacaoController;

	@Inject
	private ManterAutorizacaoFornecimentoPendenteEntregaController manterAFController;

	@Inject
	private PesqusarPlanjProgrEntregaItensAfController planjProgrEntregaItensAfController;

	private FiltroProgrGeralEntregaAFVO filtro = new FiltroProgrGeralEntregaAFVO();
	private List<ProgrGeralEntregaAFVO> itens = new ArrayList<ProgrGeralEntregaAFVO>();
	private ProgrGeralEntregaAFVO item = new ProgrGeralEntregaAFVO();
	private ValidacaoFiltrosVO validacaoVO = new ValidacaoFiltrosVO();

	private Boolean pesquisou = Boolean.FALSE;
	private Integer seqItem = null;

	// URL para botão voltar
	private String voltarParaUrl;

	private List<ParcelasAFVO> colecao;

	private ProgrGeralEntregaAFVO selecionado = new ProgrGeralEntregaAFVO();

	public String voltar() {
		return "voltar";
	}

	public void getPesquisar() {
		try{
			itens = new ArrayList<ProgrGeralEntregaAFVO>();
			if(filtro.getPrevisaoDtInicial()!=null && filtro.getPrevisaoDtFinal()!=null){
				autFornecimentoFacade.validarDatas(filtro.getPrevisaoDtInicial(), filtro.getPrevisaoDtFinal());
			}
			dataModel.reiniciarPaginator();
			pesquisou = true;
			item = new ProgrGeralEntregaAFVO();
			seqItem = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void getLimpar() {
		filtro = new FiltroProgrGeralEntregaAFVO();
		itens = new ArrayList<ProgrGeralEntregaAFVO>();
		item = new ProgrGeralEntregaAFVO();
		setValidacaoVO(new ValidacaoFiltrosVO());
		this.dataModel.limparPesquisa();
		pesquisou = false;
		seqItem = null;
	}

	public void getGerarArquivoCSVConsultaProgramacaoGeralEntregaAF() {
		try {
			List<ProgrGeralEntregaAFVO> resultadoPesquisa = new ArrayList<ProgrGeralEntregaAFVO>();

			resultadoPesquisa = autFornecimentoFacade.pesquisarItensProgGeralEntregaAF(filtro,null,null);

			if (!resultadoPesquisa.isEmpty()) {
				for(ProgrGeralEntregaAFVO entregaAFVO :resultadoPesquisa){
					autFornecimentoFacade.pesquisarInfoComplementares(entregaAFVO);
				}
				File file= comprasFacade.gerarArquivoCSVConsultaProgramacaoGeralEntregaAF(resultadoPesquisa);	
				String fileName = file.getAbsolutePath();
				String header = fileName.substring((fileName.lastIndexOf('/') == -1 ? fileName.lastIndexOf('\\') : fileName.lastIndexOf('/')) + 1);
				this.download(fileName, header);
				
				//comprasFacade.downloaded(file);
				file.delete();
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e, e.getLocalizedMessage()));
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e, e.getLocalizedMessage()));
		}		
	}

	public void selecionarItem() {
		if (item != null) {
			item.setCorPadrao("");
		}
		seqItem = selecionado.getSeq();
		item = selecionado;
		item.setCorPadrao("#EEE8AA");
		this.autFornecimentoFacade.pesquisarInfoComplementares(item);
	}

	public void validarVisualizacaoFiltros() {
		setValidacaoVO(autFornecimentoFacade.validarVisualizacaoFiltros(filtro,
				getValidacaoVO()));
	}

	public String editarAF(ProgrGeralEntregaAFVO param) {
		this.item = param;
		return EDITAR_AF;
	}

	private ParcelasAFPendEntVO criarParcelasAFPendEntVO(
			ProgrGeralEntregaAFVO param) {
		ParcelasAFPendEntVO vo = new ParcelasAFPendEntVO();
		vo.setNumeroAF(param.getAf());
		vo.setComplemento(param.getCp());
		return vo;
	}

	private PesquisarPlanjProgrEntregaItensAfFiltroVO criarPlanejProgrEntregaItensVO(
			ProgrGeralEntregaAFVO param) {
		PesquisarPlanjProgrEntregaItensAfFiltroVO vo = new PesquisarPlanjProgrEntregaItensAfFiltroVO();
		vo.setNumeroAF(param.getAf());
		vo.setComplemento(param.getCp());
		return vo;
	}

	public String visualizarParcelas(ProgrGeralEntregaAFVO param) {
		manterAFController.setAfSelecionadoVO(criarParcelasAFPendEntVO(param));
		manterAFController.setVoltarParaUrl("consultarProgramacaoGeralEntregaAF.xhtml");
		return VISUALIZAR_PARCELAS;
	}

	public String consultarProgramacao(ProgrGeralEntregaAFVO param) {
		planjProgrEntregaItensAfController.setFiltro(criarPlanejProgrEntregaItensVO(param));
		planjProgrEntregaItensAfController.getFiltro().setVisualizarAutForn(DominioVizAutForn.T);
		return PESQUISA_PLANEJAMENTO_ENTREGA_ITEM_AF;
	}

	public String imprimirPrevisaoProgramacao(ProgrGeralEntregaAFVO param) throws DocumentException {
		String retorno = "";
		try {
			
			colecao = comprasFacade.recuperaAFParcelas(param.getNroAF());
			if(colecao==null || colecao.isEmpty()){
				this.apresentarMsgNegocio(Severity.INFO, "MSG_REGISTRO_NAO_LOCALIZADO");
			}else{
				imprimirPrevisaoProgramacaoController.setProgrGeralEntregaAFVO(param);
				imprimirPrevisaoProgramacaoController.setColecao(colecao);
				imprimirPrevisaoProgramacaoController.print();
				retorno = IMPRIMIR_PREV_PROGRAMACAO;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.debug(e.getMessage());
		} catch (SystemException e) {
			LOG.debug(e.getMessage());
		} catch (IOException e) {
			LOG.debug(e.getMessage());
		}

	return retorno;

	}

	public DominioModalidadeEmpenho[] listarDominioModalidadeEmpenho() {
		List<DominioModalidadeEmpenho> listaModalidadeEmpenho = new LinkedList<DominioModalidadeEmpenho>(Arrays.asList(DominioModalidadeEmpenho.values()));
		 // Remove valor(es) nao utilizado(s) no julgamento do PAC
		return listaModalidadeEmpenho.toArray(new DominioModalidadeEmpenho[listaModalidadeEmpenho.size()]);
	}

	/*
	 * Buscas das SB;
	 */
	public List<FccCentroCustos> pesquisarCCApp(String param) {
		return this.returnSGWithCount(this.centroCustoFacade
		.pesquisarCentroCustosPorCodigoDescricao((String) param),pesquisarCCAppCount(param));
	}

	public Long pesquisarCCAppCount(String param) {
		return this.centroCustoFacade
		.pesquisarCentroCustosPorCodigoDescricaoCount((String) param);
	}

	public List<FccCentroCustos> pesquisarCCSolicitante(String param) {
		return this.returnSGWithCount(this.centroCustoFacade
		.pesquisarCentroCustosPorCodigoDescricao((String) param),pesquisarCCSolicitanteCount(param));
	}

	public Long pesquisarCCSolicitanteCount(String param) {
		return this.centroCustoFacade
		.pesquisarCentroCustosPorCodigoDescricaoCount((String) param);
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(
			String objPesquisa) throws BaseException {
		return this.returnSGWithCount(this.comprasFacade
		.getListaMaterialByNomeOrDescOrCodigo(objPesquisa),listarMateriaisCount(objPesquisa));
	}

	public Long listarMateriaisCount(String param) {
		return this.comprasFacade.listarScoMateriaisAtivosCount(param, null,
				Boolean.TRUE);
	}

	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade
		.obterGrupoMaterialPorSeqDescricao(objPesquisa), this.obterGruposCount(objPesquisa));
	}
	
	public Long obterGruposCount(String pesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricaoCount(pesquisa);
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(
			String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null,
				100, null, true),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
	}

	public List<ScoModalidadeLicitacao> pesquisarModalidades(String pesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade
				.listarModalidadeLicitacaoAtivas(pesquisa), this.comprasCadastrosBasicosFacade.pesquisarModalidadesCount(pesquisa));
	}

	public Long pesquisarModalidadesCount(String pesquisa) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadesCount(pesquisa);
	}
	
	public List<ScoServico> listarServicosAtivos(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarServicosAtivos(param), this.listarServicosAtivosCount(param));
	}

	public Long listarServicosAtivosCount(String param) {
		return this.comprasFacade.listarServicosCount(param);
	}

	public List<ScoGrupoServico> listarGrupoServico(String pesquisa) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoServico(pesquisa), this.listarGrupoServicoCount(pesquisa));
	}

	public Long listarGrupoServicoCount(String pesquisa) {
		return this.comprasFacade.pesquisarGrupoServicoCount(pesquisa);
	}

	public void setFiltro(FiltroProgrGeralEntregaAFVO filtro) {
		this.filtro = filtro;
	}

	public FiltroProgrGeralEntregaAFVO getFiltro() {
		return filtro;
	}

	public void setItens(List<ProgrGeralEntregaAFVO> itens) {
		this.itens = itens;
	}

	public List<ProgrGeralEntregaAFVO> getItens() {
		return itens;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public Boolean isPesquisou() {
		return pesquisou;
	}

	public void setSeqItem(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public Integer getSeqItem() {
		return seqItem;
	}

	public void setItem(ProgrGeralEntregaAFVO item) {
		this.item = item;
	}

	public ProgrGeralEntregaAFVO getItem() {
		return item;
	}

	public void setValidacaoVO(ValidacaoFiltrosVO validacaoVO) {
		this.validacaoVO = validacaoVO;
	}

	public ValidacaoFiltrosVO getValidacaoVO() {
		return validacaoVO;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	@Override
	public Long recuperarCount() {
		Long cont = (long) 0; 
		try {
			cont = autFornecimentoFacade.countItensProgGeralEntregaAF(filtro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cont;
	}

	@Override
	public List<ProgrGeralEntregaAFVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		try {
			itens = autFornecimentoFacade.pesquisarItensProgGeralEntregaAF(filtro, firstResult,maxResults);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return itens;
	} 


	public DynamicDataModel<ProgrGeralEntregaAFVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ProgrGeralEntregaAFVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public ProgrGeralEntregaAFVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ProgrGeralEntregaAFVO selecionado) {
		this.selecionado = selecionado;
	}

}
