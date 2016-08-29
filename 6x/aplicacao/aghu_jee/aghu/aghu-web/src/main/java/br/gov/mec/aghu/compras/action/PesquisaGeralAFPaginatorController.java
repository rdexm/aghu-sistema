package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioOpcoesResultadoAF;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


/**
 * Classe responsável por realizar a paginação de Pesquisa AF
 * 
 * @author ihaas
 * 
 */
public class PesquisaGeralAFPaginatorController extends ActionController implements ActionPaginator {

	private static final String COMPRAS_AUTORIZACAO_FORNECIMENTO_CRUD = "compras-autorizacaoFornecimentoCRUD";

	private static final long serialVersionUID = 6461505320953124314L;
	
	
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	private FiltroPesquisaGeralAFVO filtro = new FiltroPesquisaGeralAFVO();
	private boolean sliderPesquisaAvancadaAberto = false;
	private Boolean habilitarBotaoExcel = false;
	private String fileName;
	private Boolean gerouArquivo = Boolean.FALSE;
	private Boolean pollGeracaoArquivo = Boolean.FALSE;
	
	private List<PesquisaGeralAFVO> listaPesquisaGeralAF = new ArrayList<PesquisaGeralAFVO>();
	private List<PesquisaGeralIAFVO> listaPesquisaGeralIAF = new ArrayList<PesquisaGeralIAFVO>();
	private List<PesquisaGeralPAFVO> listaPesquisaGeralPAF = new ArrayList<PesquisaGeralPAFVO>();
	private List<PesquisaGeralPIAFVO> listaPesquisaGeralPIAF = new ArrayList<PesquisaGeralPIAFVO>();
	
	@Inject @Paginator
	private DynamicDataModel<PesquisaGeralAFVO> dataModelPesquisaGeralAF;
	@Inject @Paginator
	private DynamicDataModel<PesquisaGeralIAFVO> dataModelPesquisaGeralIAF;
	@Inject @Paginator
	private DynamicDataModel<PesquisaGeralPAFVO> dataModelPesquisaGeralPAF;
	@Inject @Paginator
	private DynamicDataModel<PesquisaGeralPAFVO> dataModelPesquisaGeralPIAF;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		if (this.filtro.getOpcaoResultado() == null) {
			this.filtro.setOpcaoResultado(DominioOpcoesResultadoAF.POR_AF);
		}
	
	}
	
	
	public List<ScoModalidadeLicitacao> getPesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacao(filter);
	}
	
	public List<RapServidores> getListarServidores(String objPesquisa) {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidorPorMatriculaNome(objPesquisa),getListarServidoresCount(objPesquisa));
	}

	public Long getListarServidoresCount(String objPesquisa) {
		return this.registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(objPesquisa);
	}
	
	public List<VScoFornecedor> getPesquisarFornecedores(String param) {
		List<ScoFornecedor> lista = this.comprasFacade.listarFornecedoresAtivos(param);
		List<VScoFornecedor> listaView = new ArrayList<VScoFornecedor>(0);
		for (ScoFornecedor scoFornecedor : lista) {
			VScoFornecedor viewFornecedor = new VScoFornecedor();
			viewFornecedor.setCgcCpf(scoFornecedor.getCnpjCpf());
			viewFornecedor.setNumeroFornecedor(scoFornecedor.getNumero());
			viewFornecedor.setRazaoSocial(scoFornecedor.getRazaoSocial());
			listaView.add(viewFornecedor);
		}
		return this.returnSGWithCount(listaView,getPesquisarFornecedoresCount(param));
	}
	
	public Long getPesquisarFornecedoresCount(String param) {
		return this.comprasFacade.listarFornecedoresAtivosCount(param);
	}
	
	public List<ScoGrupoMaterial> getListarGrupoMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter),getListarGrupoMateriaisCount(filter));
	}
	
	public Long getListarGrupoMateriaisCount(String filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}
	
	public List<ScoMaterial> getListarMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filter, null, true),getListarMateriaisCount(filter));
	}

	public Long getListarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}
	
	public List<ScoGrupoServico> listarGrupoServico(String filter){
		return this.comprasFacade.listarGrupoServico(filter);
	}
	
	public List<ScoServico> listarServicos(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarServicos(filter),listarServicosCount(filter));
	}
	public Long listarServicosCount(String filter){
		return this.comprasFacade.listarServicosCount(filter);
	}
	
	public List<FccCentroCustos> listarCentroCustos(String filter) {
		String srtPesquisa = (String) filter;
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(srtPesquisa),listarCentroCustosCount(filter));
	}
	
	public Long listarCentroCustosCount(String filter) {
		String srtPesquisa = (String) filter;
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(srtPesquisa);
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(String filter) {
		return cadastrosBasicosOrcamentoFacade
				.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(String objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupo(this.filtro.getGrupoNaturezaDespesa(), objParam);
	}

	public Long pesquisarNaturezaDespesaPorGrupoCount(Object objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupoCount(this.filtro.getGrupoNaturezaDespesa(), objParam);
	}
	
	public void limparSuggestionNaturezaDespesa() {
		this.filtro.setNaturezaDespesa(null);
	}
	
	public void atualizarFiltros() {
		if (this.filtro.getNumeroAF() == null) {
			this.filtro.setComplemento(null);
			this.filtro.setSequencia(null);
			this.filtro.setNumeroAFP(null);
			this.filtro.setItem(null);
		}
	}
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public String obterDocFormatado(ScoFornecedor fornecedor) {
		return this.comprasFacade.obterCnpjCpfFornecedorFormatado(fornecedor).concat(" - ").concat(fornecedor.getRazaoSocial());
	}
	
	public String obterServMaterial(ScoMaterial material, ScoServico servico, boolean truncado) {
		String retorno = "";
		if (material != null) {
			retorno = material.getCodigoENome();
		} else if (servico != null) {
			retorno = servico.getCodigoENome();
		}
		if (truncado && retorno.length() > 30) {
			retorno = retorno.substring(0, 30) + "...";
		}
		return retorno;
	}
	

	public void gerarArquivo() throws IOException, ApplicationBusinessException {
		pollGeracaoArquivo = Boolean.TRUE;
		if (this.filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AF)) {
			try {
				fileName = comprasFacade.geraArquivoConsultaPorAF(this.filtro);
				gerouArquivo = Boolean.TRUE;
				
			} catch(IOException e) {
				gerouArquivo = Boolean.FALSE;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
			
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_ITEM_AF)) {
			try {
				fileName = comprasFacade.geraArquivoConsultaPorIAF(this.filtro);
				gerouArquivo = Boolean.TRUE;
				
			} catch(IOException e) {
				gerouArquivo = Boolean.FALSE;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
			
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AFP)) {
			try {
				fileName = comprasFacade.geraArquivoConsultaPorPAF(this.filtro);
				gerouArquivo = Boolean.TRUE;
				
			} catch(IOException e) {
				gerouArquivo = Boolean.FALSE;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
			
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_PARCELA)) {
			try {
				fileName = comprasFacade.geraArquivoConsultaPorPIAF(this.filtro);
				gerouArquivo = Boolean.TRUE;
				
			} catch(IOException e) {
				gerouArquivo = Boolean.FALSE;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
		pollGeracaoArquivo = Boolean.FALSE;
		this.executarDownload();
	}
	

	
	public void executarDownload() {
		if (fileName != null) {

			try {
				this.download(fileName);
				
				fileName = null;
				gerouArquivo = Boolean.FALSE;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	@Override
	public List<?> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {	
		if (this.filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AF)) {
			listaPesquisaGeralAF = comprasFacade.listarAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
					this.filtro);
			
			if (listaPesquisaGeralAF == null) {
				listaPesquisaGeralAF = new ArrayList<PesquisaGeralAFVO>();
			}
			return listaPesquisaGeralAF;
			
		} 
		else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_ITEM_AF)) {
			listaPesquisaGeralIAF = comprasFacade.listarItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
					this.filtro);
			
			if (listaPesquisaGeralIAF == null) {
				listaPesquisaGeralIAF = new ArrayList<PesquisaGeralIAFVO>();
			}
			return listaPesquisaGeralIAF;
			
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AFP)) {
			listaPesquisaGeralPAF = comprasFacade.listarPedidosAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
					this.filtro);
			
			if (listaPesquisaGeralPAF == null) {
				listaPesquisaGeralPAF = new ArrayList<PesquisaGeralPAFVO>();
			}
			return listaPesquisaGeralPAF;
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_PARCELA)) {
			listaPesquisaGeralPIAF = comprasFacade.listarParcelasItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
					this.filtro);
			
			if (listaPesquisaGeralPIAF == null) {
				listaPesquisaGeralPIAF = new ArrayList<PesquisaGeralPIAFVO>();
			}
			return listaPesquisaGeralPIAF;
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		Long count = null;
		if (this.filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AF)) {
			count = this.autFornecimentoFacade.listarAutorizacoesFornecimentoFiltradoCount(this.filtro);
			if (count > 0) {
				this.habilitarBotaoExcel = true;
			}
			return count;
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_ITEM_AF)) {
			count = this.autFornecimentoFacade.listarItensAutorizacoesFornecimentoFiltradoCount(this.filtro);
			if (count > 0) {
				this.habilitarBotaoExcel = true;
			}
			return count;
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_AFP)) {
			count = this.autFornecimentoFacade.listarPedidosAutorizacoesFornecimentoFiltradoCount(this.filtro);
			if (count > 0) {
				this.habilitarBotaoExcel = true;
			}			
		} else if (filtro.getOpcaoResultado().equals(DominioOpcoesResultadoAF.POR_PARCELA)) {
			count = this.autFornecimentoFacade.listarParcelasItensAutorizacoesFornecimentoFiltradoCount(this.filtro);
			if (count > 0) {
				this.habilitarBotaoExcel = true;
			}
		}
		return count;
	}
	
	public List<FccCentroCustos> listarCentroCustosSolic(Object objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}
	
	public Long listarCentroCustosSolicCount(Object objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}
	
	public DominioModalidadeEmpenho[] listarDominioModalidadeEmpenho() {
		List<DominioModalidadeEmpenho> listaModalidadeEmpenho = new LinkedList<DominioModalidadeEmpenho>(Arrays.asList(DominioModalidadeEmpenho.values()));
		 // Remove valor(es) nao utilizado(s) no julgamento do PAC
		//listaModalidadeEmpenho.remove(DominioModalidadeEmpenho.IMPORTACAO);
		
		return listaModalidadeEmpenho.toArray(new DominioModalidadeEmpenho[listaModalidadeEmpenho.size()]);
	}
	
	public void pesquisar() {
		this.habilitarBotaoExcel = false;
		this.limparListas();
		if (this.filtro.getOpcaoResultado() == null) {
			this.filtro.setOpcaoResultado(DominioOpcoesResultadoAF.POR_AF);
		}
		try {
			this.comprasFacade.validaPesquisaGeralAF(this.filtro);
			switch (this.filtro.getOpcaoResultado()) {
				case POR_AF:
					this.dataModelPesquisaGeralAF.reiniciarPaginator();
					break;
				case POR_ITEM_AF:
					this.dataModelPesquisaGeralIAF.reiniciarPaginator();
					break;
				case POR_AFP:
					this.dataModelPesquisaGeralPAF.reiniciarPaginator();
					break;
				case POR_PARCELA:
					this.dataModelPesquisaGeralPIAF.reiniciarPaginator();
					break;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa(){
		this.habilitarBotaoExcel = false;
		this.setAtivo(false);
		this.filtro = new FiltroPesquisaGeralAFVO();
		this.filtro.setOpcaoResultado(DominioOpcoesResultadoAF.POR_AF);
		this.sliderPesquisaAvancadaAberto = false;
		this.limparListas();
	}
	
	private void limparListas() {
		this.listaPesquisaGeralAF = new ArrayList<PesquisaGeralAFVO>();
		this.listaPesquisaGeralIAF = new ArrayList<PesquisaGeralIAFVO>();
		this.listaPesquisaGeralPAF = new ArrayList<PesquisaGeralPAFVO>();
		this.listaPesquisaGeralPIAF = new ArrayList<PesquisaGeralPIAFVO>();
	}
	
	public String redirecionarAutorizacaoFornecimentoCRUD(){
		return COMPRAS_AUTORIZACAO_FORNECIMENTO_CRUD;
	}
	
	public DominioSituacaoAutorizacaoFornecimento getDominioSituacaoAutorizacaoFornecimento(String valor){
		return DominioSituacaoAutorizacaoFornecimento.getInstance(valor);
	}
	
	public String getDescricaoDominioSituacaoAutorizacaoFornecimento(String valor) {
		return DominioSituacaoAutorizacaoFornecimento.getInstance(valor).getCodigoEDescricao();
	}
	
	public boolean isAtivo() {
		if(this.filtro.getOpcaoResultado() != null){
			switch (this.filtro.getOpcaoResultado()) {
				case POR_AF:
					return this.dataModelPesquisaGeralAF.getPesquisaAtiva();
				case POR_ITEM_AF:
					return this.dataModelPesquisaGeralIAF.getPesquisaAtiva();
				case POR_AFP: 
					return this.dataModelPesquisaGeralPAF.getPesquisaAtiva();
				case POR_PARCELA:
					return this.dataModelPesquisaGeralPIAF.getPesquisaAtiva();
			}
		}
		return false;
	}

	public void setAtivo(boolean ativo) {
		switch (this.filtro.getOpcaoResultado()) {
			case POR_AF:
				this.dataModelPesquisaGeralAF.setPesquisaAtiva(ativo);
				break;
			case POR_ITEM_AF:
				this.dataModelPesquisaGeralIAF.setPesquisaAtiva(ativo);
				break;
			case POR_AFP: 
				this.dataModelPesquisaGeralPAF.setPesquisaAtiva(ativo);
				break;
			case POR_PARCELA:
				this.dataModelPesquisaGeralPIAF.setPesquisaAtiva(ativo);
				break;
		}
	}

	public FiltroPesquisaGeralAFVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPesquisaGeralAFVO filtro) {
		this.filtro = filtro;
	}

	public boolean isSliderPesquisaAvancadaAberto() {
		return sliderPesquisaAvancadaAberto;
	}

	public void setSliderPesquisaAvancadaAberto(boolean sliderPesquisaAvancadaAberto) {
		this.sliderPesquisaAvancadaAberto = sliderPesquisaAvancadaAberto;
	}

	public Boolean getHabilitarBotaoExcel() {
		return habilitarBotaoExcel;
	}

	public void setHabilitarBotaoExcel(Boolean habilitarBotaoExcel) {
		this.habilitarBotaoExcel = habilitarBotaoExcel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public List<PesquisaGeralAFVO> getListaPesquisaGeralAF() {
		return listaPesquisaGeralAF;
	}

	public void setListaPesquisaGeralAF(List<PesquisaGeralAFVO> listaPesquisaGeralAF) {
		this.listaPesquisaGeralAF = listaPesquisaGeralAF;
	}

	public List<PesquisaGeralIAFVO> getListaPesquisaGeralIAF() {
		return listaPesquisaGeralIAF;
	}

	public void setListaPesquisaGeralIAF(
			List<PesquisaGeralIAFVO> listaPesquisaGeralIAF) {
		this.listaPesquisaGeralIAF = listaPesquisaGeralIAF;
	}

	public List<PesquisaGeralPAFVO> getListaPesquisaGeralPAF() {
		return listaPesquisaGeralPAF;
	}

	public void setListaPesquisaGeralPAF(
			List<PesquisaGeralPAFVO> listaPesquisaGeralPAF) {
		this.listaPesquisaGeralPAF = listaPesquisaGeralPAF;
	}

	public List<PesquisaGeralPIAFVO> getListaPesquisaGeralPIAF() {
		return listaPesquisaGeralPIAF;
	}

	public void setListaPesquisaGeralPIAF(
			List<PesquisaGeralPIAFVO> listaPesquisaGeralPIAF) {
		this.listaPesquisaGeralPIAF = listaPesquisaGeralPIAF;
	}
	
	public Boolean getPollGeracaoArquivo() {
		return pollGeracaoArquivo;
	}

	public void setPollGeracaoArquivo(Boolean pollGeracaoArquivo) {
		this.pollGeracaoArquivo = pollGeracaoArquivo;
	}


	public Boolean validarPoll() {
		return true;
	}

	public DynamicDataModel<PesquisaGeralAFVO> getDataModelPesquisaGeralAF() {
		return dataModelPesquisaGeralAF;
	}

	public void setDataModelPesquisaGeralAF(
			DynamicDataModel<PesquisaGeralAFVO> dataModelPesquisaGeralAF) {
		this.dataModelPesquisaGeralAF = dataModelPesquisaGeralAF;
	}

	public DynamicDataModel<PesquisaGeralIAFVO> getDataModelPesquisaGeralIAF() {
		return dataModelPesquisaGeralIAF;
	}

	public void setDataModelPesquisaGeralIAF(
			DynamicDataModel<PesquisaGeralIAFVO> dataModelPesquisaGeralIAF) {
		this.dataModelPesquisaGeralIAF = dataModelPesquisaGeralIAF;
	}

	public DynamicDataModel<PesquisaGeralPAFVO> getDataModelPesquisaGeralPAF() {
		return dataModelPesquisaGeralPAF;
	}

	public void setDataModelPesquisaGeralPAF(
			DynamicDataModel<PesquisaGeralPAFVO> dataModelPesquisaGeralPAF) {
		this.dataModelPesquisaGeralPAF = dataModelPesquisaGeralPAF;
	}

	public DynamicDataModel<PesquisaGeralPAFVO> getDataModelPesquisaGeralPIAF() {
		return dataModelPesquisaGeralPIAF;
	}

	public void setDataModelPesquisaGeralPIAF(
			DynamicDataModel<PesquisaGeralPAFVO> dataModelPesquisaGeralPIAF) {
		this.dataModelPesquisaGeralPIAF = dataModelPesquisaGeralPIAF;
	}	
}