package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.compras.contaspagar.business.exception.ComprasExceptionCode;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.impl.RegistroCsvDividaHospitalNaturezaDespesa;
import br.gov.mec.aghu.compras.contaspagar.impl.RegistroCsvPagamentosRealizados;
import br.gov.mec.aghu.compras.contaspagar.impl.RegistroCsvTituloBloqueado;
import br.gov.mec.aghu.compras.contaspagar.vo.BuscaDivHospNatDespVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloBloqueadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloNrVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.compras.vo.FiltroRelatoriosExcelVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.estoque.dao.FcpValorTributosDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.FcpPagamento;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por prover os metodos de negócio genéricos para a entidade
 * de Título.
 *
 */
@Stateless
public class ConsultarTituloRN extends BaseBusiness {

	private static final String ATE = " até ";

	private static final String PAGO = "PAGO";

	private static final String NAOPAGO = "NAOPAGO";

	/** Identificador Único */
	private static final long serialVersionUID = -120076933313133538L;

	/** Constante de log */
	private static final Log LOG = LogFactory.getLog(ConsultarTituloRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/** Injeção do objeto de título da camada de dados */
	@Inject
	private FcpTituloDAO tituloDAO;

	/** Injeção do objeto de verbas de gestão da camada de dados */
	@Inject
	private FsoVerbaGestaoDAO verbaGestaoDAO;

	/** Injeção do objeto de natureza de despesa da camada de dados */
	@Inject
	private FsoNaturezaDespesaDAO naturezaDespesaDAO;

	/** Injeção do objeto de centroCusto da camada de dados */
	@Inject
	private FccCentroCustosDAO centroCustoDAO;
	
	@Inject
	private FcpValorTributosDAO fcpValorTributosDAO;

	private enum ConsultarTituloRNException implements BusinessExceptionCode {
		DATA_PAGAMENTO_INVALIDA, MENSAGEM_ERRO_DATA_INICIAL_MAIOR_DATA_FINAL_CONSULTAR_TITULOS;
	
	}
	
	/** Prefixo do nome do arquivo csv dos boletos bloqueados */
	private static final String MAGIC_STRING_PREFIXO_ARQ_CSV_BOL_BLQ = "TITULOS_BLOQUEADOS_";
	
	/** Prefixo do nome do arquivo csv dívida natureza despesa */
	private static final String MAGIC_STRING_PREFIXO_ARQ_CSV_NAT_DIV = "DIVIDA_NATUREZA_DESPESA_";
	
	/** Prefixo do nome do arquivo csv dos pagamentos realizados */
	private static final String MAGIC_STRING_PREFIXO_ARQ_CSV_PAG_REAL = "PAGAMENTOS_PERIODO_";
	
	/** EJB do objeto de geração de arquivo CSV */
	@EJB
	private GeracaoArquivoCsvRN geracaoArquivoCsvRN;

	/**
	 * Método que retonar uma {@link List} de {@link TituloProgramadoVO} que reflete os {@link FcpTitulo} a serem pagos.
	 * @param dataPagamento
	 * @return	Coleção com os títulos com pagamento programado.
	 */
	public List<TituloProgramadoVO> obterListaPagamentosProgramados(Date dataPagamento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<TituloProgramadoVO> listaTitulosProgramados = getTituloDAO().pesquisarPagamentosProgramados(dataPagamento, firstResult, maxResult, orderProperty, asc);

		return listaTitulosProgramados;
	}
	
	public Long pesquisarPagamentosProgramadosCount(Date dataPagamento){
		return getTituloDAO().pesquisarPagamentosProgramadosCount(dataPagamento);
	}

	/**
	 * Método de atualização do {@link FcpTitulo}.
	 * 
	 * @param titulo
	 *            {@link FcpTitulo} a ser atualizado.
	 * @throws ApplicationBusinessException
	 */
	public void atualizarTitulo(FcpTitulo titulo) throws ApplicationBusinessException {
		this.getTituloDAO().atualizar(titulo);
	}

	/**
	 * Método que obtém a lista de VOs de Títulos.
	 * 
	 * @param parametros
	 * @return List TituloVO
	 */
	public List<TituloVO> pesquisarTitulos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroConsultaTitulosVO filtros) throws BaseException {
		validarFiltroConsultaTitulos(filtros);
		return this.getTituloDAO().pesquisarTitulos(firstResult, maxResult, orderProperty, asc, filtros);
	}

	public void validarFiltroConsultaTitulos(FiltroConsultaTitulosVO filtros) throws ApplicationBusinessException {
		if (filtros.getDataInicial() != null && filtros.getDataFinal() != null) {
			if (DateUtil.validaDataMaior(filtros.getDataInicial(), filtros.getDataFinal())) {
				throw new ApplicationBusinessException(ConsultarTituloRNException.MENSAGEM_ERRO_DATA_INICIAL_MAIOR_DATA_FINAL_CONSULTAR_TITULOS);
			}
		}
	}
	
	public Long pesquisarTitulosCount(FiltroConsultaTitulosVO filtro) throws ApplicationBusinessException {
		validarFiltroConsultaTitulos(filtro);
		return getTituloDAO().pesquisarTitulosCount(filtro);
	} 

	/**
	 * Método para preencher a suggestion box de verbas da tela de consulta de
	 * títulos.
	 * 
	 * @return {@link List} carregado com as verbas.
	 */
	public List<FsoVerbaGestao> listarVerbaGestaoPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getVerbaGestaoDAO().listarVerbaGestaoPorSituacao(strPesquisa);
	}

	/**
	 * Método para preencher a suggestion box de despesas da tela de consulta de
	 * títulos.
	 * 
	 * @return {@link List} carregado com as despesas.
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getNaturezaDespesaDAO().listarNaturezaDespesaPorSituacao(strPesquisa);
	}

	public List<FcpTipoDocumentoPagamento> listarDocumentosPorSituacaoSuggestionBox(final String strPesquisa) {
		return this.getCentroCustoDAO().listarDocumentosPorSituacao(strPesquisa);
	}

	/**
	 * Método para obter o valor to total de registros da consulta por situação.
	 * 
	 * @return Númerico que representa o total de registros da consulta por
	 *         situação.
	 */
	public Long countListarVerbaGestaoPorSituacao(final String strPesquisa) {
		return this.getVerbaGestaoDAO().countListarVerbaGestaoPorSituacao(strPesquisa);
	}

	/**
	 * Método para obter o valor to total de registros da consulta por situação.
	 * 
	 * @return Númerico que representa o total de registros da consulta por
	 *         situação.
	 */
	public Long countListarNaturezaDespesaPorSituacao(final String strPesquisa) {
		return this.getNaturezaDespesaDAO().countListarNaturezaDespesaPorSituacao(strPesquisa);
	}

	/**
	 * Método para obter o valor to total de registros da consulta por situação.
	 * 
	 * @return Númerico que representa o total de registros da consulta por
	 *         situação.
	 */
	public Long countListarDocumentosPorSituacao(final String strPesquisa) {
		return this.getCentroCustoDAO().countListarDocumentosPorSituacao(strPesquisa);
	}

	/**
	 * Método responsável por atualizar a data de pagamento do título.
	 * @param tituloSeq
	 * @param dataPagamento
	 * @param validarData Indica se deve validar a data de pagamento, quando for setar uma data de pagamento.
	 * @throws ApplicationBusinessException
	 */
	public void alterarTituloPagamento(Integer tituloSeq, Date dataPagamento, boolean validarData) throws ApplicationBusinessException {

		if (validarData) {
			if (dataPagamento != null && CoreUtil.isMaiorOuIgualDatas(DateUtil.truncaData(dataPagamento), DateUtil.truncaData(new Date()))) {
				FcpTitulo fcpTitulo = this.getTituloDAO().obterPorChavePrimaria(tituloSeq);
				fcpTitulo.setDtProgPag(dataPagamento);
				this.getTituloDAO().merge(fcpTitulo);
			} else {
				throw new ApplicationBusinessException(ConsultarTituloRNException.DATA_PAGAMENTO_INVALIDA);
			}
		} else {
			// Quando validarData for false, deve setar null na DtProgPag
			FcpTitulo fcpTitulo = this.getTituloDAO().obterPorChavePrimaria(tituloSeq);
			fcpTitulo.setDtProgPag(null);
			this.getTituloDAO().merge(fcpTitulo);
		}
	}

	public List<String> pesquisarTributosNotaRecebimentoTitulo(Integer notaRecebimentoNumero) {
		return this.getFcpValorTributosDAO().pesquisarTributosNotaRecebimentoTitulo(notaRecebimentoNumero);
	}

	
	
	/**
	 * Método responsável por retornar todos os títulos bloqueados
	 * 
	 * @return Lista de títulos bloqueados
	 * */
	public List<TituloBloqueadoVO> retornarTitulosBloqueados() {
		return this.getCentroCustoDAO().pesquisarTitulosBloqueadosPorBo(DominioSituacaoTitulo.BLQ);
	}
	
	/**
	 * Método responsável por obter o prefixo do nome de um arquivo a 
	 * ser gerado e criar o nome do arquivo, por meio de seu prefixo e a data da geração do arquivo
	 * 
	 * @param prefix String com o prefixo do nome do arquivo
	 * @param data Date com a data de geração do arquivo
	 * @return Nome do arquivo, com seu prefixo e data de geração
	 * */
	protected static String obterPrefixoNomeArquivo(String prefix, final Date data) {

		String result = null;

		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String formattedDate = format.format(data);
		result = prefix + formattedDate + "-";

		return result;
	}
	
	
	
	private String getTitleTituloBloqueado() throws ApplicationBusinessException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		String dateFormatted = dateFormat.format(today);
		return dateFormatted;
		
	}
	
	private String getTitlePagamentosRealizados() throws ApplicationBusinessException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		String dateFormatted = dateFormat.format(today);
		return dateFormatted;
		
	}
	
	/**
	 * Método responsável por montar o título do arquivo texto das contas por período
	 * @param fornecedor
	 * @param dataDividaInicial
	 * @param dataDividaFinal
	 * @return Título do arquivo
	 */
	private String getTitleContasPeriodo(ScoFornecedor fornecedor, Date dataDividaInicial, Date dataDividaFinal) throws ApplicationBusinessException {
		String msg = getResourceBundleValue("CONTAS_PERIODO_TITLE");
		StringBuffer sv = new StringBuffer(msg);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		//Se não houver data inicial preenchida pelo usuário
		if(dataDividaInicial == null){
			//Se não estiver sido preenchida pelpo usuário
			if(dataDividaFinal == null) {
				Calendar calendar = Calendar.getInstance();
				Date today = calendar.getTime();
				String dateFormatted = dateFormat.format(today);
				String temp = ATE + dateFormatted;
				sv.append(temp);

			//Se a data final estiver sido preenchida pelo usuário
			} else {				
				String dateFormatted = dateFormat.format(dataDividaFinal);
				String temp = ATE + dateFormatted;
				sv.append(temp);
			}
		//Se a data inicial for preenchida pelo usuário
		} else {
			if(dataDividaFinal != null){
				//Se a data final for menor que a data inicial
				if(dataDividaFinal.before(dataDividaInicial)){
					throw new ApplicationBusinessException("Data final não pode ser menor que a data inicial", Severity.ERROR);

				} else {
					sv.append(" de ").append(dateFormat.format(dataDividaInicial));
					sv.append(ATE).append(dateFormat.format(dataDividaFinal));

				}
			} else {
				sv.append(" de ").append(dateFormat.format(dataDividaInicial));
				//Coloco a data final como a data de hoje
				dataDividaFinal = Calendar.getInstance().getTime();
				sv.append(ATE).append(dateFormat.format(dataDividaFinal));
			}
		}

		//Se houver fornecedor e o seu número concatene no title
		if(fornecedor != null && fornecedor.getNumero() != null){
			sv.append(" - Fornecedor: ").append(fornecedor.getRazaoSocial());
		}
		
		return sv.toString();
	}
	
	/**
	 * Método responsável por gerar o arquivo csv com os títulos bloqueados
	 * 
	 * @return Retorna um objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do arquivo CSV para download
	 */
	public ArquivoURINomeQtdVO gerarArquivoTextoTitulosBloqueados() throws ApplicationBusinessException {
		ArquivoURINomeQtdVO result = null;
		URI uriCsv = null;
		List<TituloBloqueadoVO> listaVo = null;
		GeracaoArquivoCsvRN geraRn = null;
		String prefixo = null;
		String nomeCsv = null;
		
		//Título do arquivo (primeira linha)
		String titulo = getTitleTituloBloqueado();
		
		try {
			prefixo = obterPrefixoNomeArquivo(MAGIC_STRING_PREFIXO_ARQ_CSV_BOL_BLQ, new Date());
		
			listaVo = this.retornarTitulosBloqueados();
			
			if (listaVo != null && listaVo.size() > 0) {			
				geraRn = this.getGeracaoArquivoCsvRN();
				uriCsv = geraRn.gerarDadosEmArquivo(RegistroCsvTituloBloqueado.class, listaVo, prefixo, titulo);
				nomeCsv = GeracaoArquivoCsvRN.obterNomeArquivo(uriCsv, true);
				result = new ArquivoURINomeQtdVO(uriCsv, nomeCsv, listaVo.size(), 1);
			} else {	
				listaVo = null;
			}
			
		} catch (IOException e) {
			LOG.error("Erro ao gerar o arquivo de títulos bloqueados. ",e);
			throw new ApplicationBusinessException(ComprasExceptionCode.ERRO_GERAR_ARQUIVO, Severity.ERROR);
			
		}
		
		return result;
	}
	
	/**
	 * Método responsável por gerar o arquivo texto CSV das contas por período e fornecedor
	 * 
	 * @param fornecedor fornecedor dos títulos
	 * @param dataDividaInicial Data inicial da apuração das dívidas
	 * @param dataDividaFinal Data final da apuração das dívidas
	 * @return Retornar um objeto com a uri e informações para download do arquivo gerado
	 * */
	public ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo(FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException {
		ArquivoURINomeQtdVO result = null;
		URI uriCsv = null;
		List<BuscaDivHospNatDespVO> listaVo = null;
		GeracaoArquivoCsvRN geraRn = null;
		String prefixo = null;
		String nomeCsv = null;
		String titulo = getTitleContasPeriodo(filtroRelatoriosExcelVO.getFornecedor(), filtroRelatoriosExcelVO.getDataInicioDivida(), filtroRelatoriosExcelVO.getDataFimDivida());
		
		Date dataInicioDivida = null;
		Date dataPagamento = null;
		
		if (filtroRelatoriosExcelVO.getDataInicioDivida() != null) {
			dataInicioDivida = DateUtil.obterDataComHoraFinal(filtroRelatoriosExcelVO.getDataInicioDivida());
		}
		// RN para geracao, a data final tem que ser preenchida para tratamento dos pagamentos
		if(filtroRelatoriosExcelVO.getDataFimDivida() == null) {
			dataPagamento = DateUtil.obterDataComHoraFinal(new Date());
		} else {
			dataPagamento = DateUtil.obterDataComHoraFinal(filtroRelatoriosExcelVO.getDataFimDivida());
		}
		try {
			prefixo = obterPrefixoNomeArquivo(MAGIC_STRING_PREFIXO_ARQ_CSV_NAT_DIV, new Date());
			listaVo = this.getCentroCustoDAO()
					.pesquisarBuscaDividaHispitalNaturezaDespesaCriteria(filtroRelatoriosExcelVO.getFornecedor(), dataInicioDivida, dataPagamento);
			if(listaVo != null && listaVo.size() > 0) {	
											
				List<BuscaDivHospNatDespVO> resultado = new ArrayList<BuscaDivHospNatDespVO>();
				List<BuscaDivHospNatDespVO> novaLista = new ArrayList<BuscaDivHospNatDespVO>(listaVo);
				BuscaDivHospNatDespVO atual = novaLista.remove(0);
				resultado.add(atual);
				
				for(BuscaDivHospNatDespVO vo : novaLista) {
					// Verifica se foi pago
					if(!buscarRetornoPagamento(vo.getSeq(), dataPagamento)){
						continue; // foi pago, nao adiciona na lista
					}
					
					if((!vo.getAno().equals(atual.getAno())) || (!vo.getAfnNtdGndCodigo().equals(atual.getAfnNtdGndCodigo()))
							|| (!vo.getAfnNtdCodigo().equals(atual.getAfnNtdCodigo())) || (!vo.getNtdCodigo().equals(atual.getNtdCodigo()))
							|| (!vo.getNtpCodigo().equals(atual.getNtpCodigo()))) {
						atual = vo;
						resultado.add(atual);
						continue;
					}
					atual.setValor(vo.getValor() + atual.getValor());
				}
				
				geraRn = this.getGeracaoArquivoCsvRN();
				uriCsv = geraRn.gerarDadosEmArquivo(RegistroCsvDividaHospitalNaturezaDespesa.class, resultado, prefixo, titulo);
				nomeCsv = GeracaoArquivoCsvRN.obterNomeArquivo(uriCsv, true);
				result = new ArquivoURINomeQtdVO(uriCsv, nomeCsv, listaVo.size(), 1);
			} else {
				result = null;
			}
		} catch (IOException e) {
			LOG.error("Erro gerando arquivo", e);
			throw new ApplicationBusinessException(ComprasExceptionCode.ERRO_GERAR_ARQUIVO, Severity.ERROR);
		}

		return result;
	} 
	
	/** Método para gerar o arquivo CSV dos pagamentos realizados em um período
	 * 
	 * @param dataDividaInicial Data inicial da busca dos pagamentos realizados
	 * @param dataDividaFinal Data final da busca dos pagamentos realizados
	 * 
	 * @return Retorno da lista de vo's dos pagamentos realizados
	 * */
	public ArquivoURINomeQtdVO gerarArquivoTextoPagamentosRealizadosPeriodo (FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException {
		ArquivoURINomeQtdVO result = null;
		URI uriCsv = null;
		List<PagamentosRealizadosPeriodoVO> listaVo = null;
		GeracaoArquivoCsvRN geraRn = null;
		String prefixo = null;
		String nomeCsv = null;
		
		//Título do arquivo (primeira linha)
		String titulo = getTitlePagamentosRealizados();
		
		try {
			prefixo = obterPrefixoNomeArquivo(MAGIC_STRING_PREFIXO_ARQ_CSV_PAG_REAL, new Date());
		
			listaVo = this.retornarPagamentosRealizados(filtroRelatoriosExcelVO.getDataInicioDivida(), filtroRelatoriosExcelVO.getDataFimDivida());
			
			if (listaVo != null && listaVo.size() > 0) {			
				geraRn = this.geracaoArquivoCsvRN;
				uriCsv = geraRn.gerarDadosEmArquivo(RegistroCsvPagamentosRealizados.class, listaVo, prefixo, titulo);
				nomeCsv = GeracaoArquivoCsvRN.obterNomeArquivo(uriCsv, true);
				result = new ArquivoURINomeQtdVO(uriCsv, nomeCsv, listaVo.size(), 1);
			} else {	
				listaVo = null;
			}
			
		} catch (IOException e) {
			LOG.error("Erro ao gerar o arquivo de títulos bloqueados. ",e);
			throw new ApplicationBusinessException(ComprasExceptionCode.ERRO_GERAR_ARQUIVO, Severity.ERROR);
			
		}
		
		return result;
	}
	
	private List<PagamentosRealizadosPeriodoVO> retornarPagamentosRealizados( Date dataDividaInicial, Date dataDividaFinal) {
		return this.tituloDAO.pesquisarPagamentosRealizadosPeriodo(dataDividaInicial, dataDividaFinal);
	}

	/**
	 * Método responsável por bucar o retrono de um pagamento
	 * 
	 * @param tituloPagamento número do título do pagamento
	 * @param dataPagamento data do pagamento
	 * @return true se o status do pagamento for não pago ou false se paga.
	 **/
	private boolean buscarRetornoPagamento(Integer tituloPagamento, Date dataPagamento) {
		String indicadorEstorno = "N";
		String situacaoEstorno = NAOPAGO;
		
		List<FcpPagamento> retornoPagos =  this.getCentroCustoDAO().createEstornoPagoNaoPagoCriteria(tituloPagamento, "N", null);
		List<FcpPagamento> retornoNaoPagos =  this.getCentroCustoDAO().createEstornoPagoNaoPagoCriteria(tituloPagamento, "S", "dtPag");

			if (retornoPagos.isEmpty()) {
				if (retornoNaoPagos.isEmpty()) {
					situacaoEstorno = NAOPAGO;
					indicadorEstorno = "S";
				}
			}
			for (FcpPagamento retornoNaoPago : retornoNaoPagos) {
				if (retornoNaoPago.getDtPagamento().compareTo(dataPagamento) < 0 || retornoNaoPago.getDtPagamento().compareTo(dataPagamento) == 0) {
					situacaoEstorno = PAGO;
					indicadorEstorno = "S";
				} else {
					situacaoEstorno = NAOPAGO;
					indicadorEstorno = "S";
				}
				if (dataPagamento.compareTo(retornoNaoPago.getDtPagamento()) > 0 || dataPagamento.compareTo(retornoNaoPago.getDtPagamento()) == 0) {
							situacaoEstorno = PAGO;
							indicadorEstorno = "S";
				}
			}
		
		if (indicadorEstorno.equals("N")) {
			List<FcpPagamento> retorno = this.getCentroCustoDAO().createEstornoPagoNaoPagoCriteria(tituloPagamento, null, "numero");
			if (retorno == null) {
				situacaoEstorno = NAOPAGO;
				indicadorEstorno = "S";
			} else {
				int index = 0;
				while(retorno != null && indicadorEstorno.equals("N")) {
				    if(retorno.get(index).getIndEstorno().equals("S")) {
				        if(dataPagamento.after(retorno.get(index).getDtPagamento()) && dataPagamento.before(retorno.get(index).getDtEstorno())) {
				            if(retorno.get(index).getDtPagamento().compareTo(dataPagamento) < 0 || retorno.get(index).getDtPagamento().compareTo(dataPagamento) == 0) {
				                if(retorno.get(index).getDtEstorno().compareTo(dataPagamento) > 0) {
				                     situacaoEstorno = PAGO;
				                     indicadorEstorno = "S";
				    			} else {
				                     situacaoEstorno = NAOPAGO;
				                     indicadorEstorno = "S";
				    			}
				    		}
				    	}
				    } else {
				         if(retorno.get(index).getDtPagamento().compareTo(dataPagamento) < 0 || retorno.get(index).getDtPagamento().compareTo(dataPagamento) == 0) {
				             situacaoEstorno = PAGO;
				             indicadorEstorno = "S";
				         } else {
				             situacaoEstorno = NAOPAGO;
				             indicadorEstorno = "S";
				         }
				    }
				    index++;
				}
			}
		}
		return situacaoEstorno.equals(NAOPAGO);
	}
	
	/**
	 * Método responsável por recuperar lista de titulos com nota de recebimento.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tituloNrVO
	 * @return
	 * @throws BaseException
	 */
	public List<TituloNrVO> pesquisarListaTitulosNR(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, TituloNrVO tituloNrVO) throws ApplicationBusinessException {
		return this.getTituloDAO().pesquisarTituloNr(firstResult, maxResult, orderProperty, asc, tituloNrVO);
	}
	
	/**
	 * Método responsável por recuperar o número de registros de titulos com nota de recebimento.
	 * @param tituloNrVO
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarCountTitulosNR(TituloNrVO tituloNrVO) throws ApplicationBusinessException {
		return this.getTituloDAO().pesquisarTituloNrCount(tituloNrVO);
	} 

	public FcpTituloDAO getTituloDAO() {
		return tituloDAO;
	}

	public void setTituloDAO(FcpTituloDAO tituloDAO) {
		this.tituloDAO = tituloDAO;
	}

	public FsoVerbaGestaoDAO getVerbaGestaoDAO() {
		return verbaGestaoDAO;
	}

	public void setVerbaGestaoDAO(FsoVerbaGestaoDAO verbaGestaoDAO) {
		this.verbaGestaoDAO = verbaGestaoDAO;
	}

	public FsoNaturezaDespesaDAO getNaturezaDespesaDAO() {
		return naturezaDespesaDAO;
	}

	public void setNaturezaDespesaDAO(FsoNaturezaDespesaDAO naturezaDespesaDAO) {
		this.naturezaDespesaDAO = naturezaDespesaDAO;
	}

	public FccCentroCustosDAO getCentroCustoDAO() {
		return centroCustoDAO;
	}

	public void setCentroCustoDAO(FccCentroCustosDAO centroCustoDAO) {
		this.centroCustoDAO = centroCustoDAO;
	}

	public FcpValorTributosDAO getFcpValorTributosDAO() {
		return fcpValorTributosDAO;
	}

	public void setFcpValorTributosDAO(FcpValorTributosDAO fcpValorTributosDAO) {
		this.fcpValorTributosDAO = fcpValorTributosDAO;
	}

	public GeracaoArquivoCsvRN getGeracaoArquivoCsvRN() {
		return geracaoArquivoCsvRN;
	}

	public void setGeracaoArquivoCsvRN(GeracaoArquivoCsvRN geracaoArquivoCsvRN) {
		this.geracaoArquivoCsvRN = geracaoArquivoCsvRN;
	}
	
}
