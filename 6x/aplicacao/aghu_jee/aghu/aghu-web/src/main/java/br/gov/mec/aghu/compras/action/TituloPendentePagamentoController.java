/**
 *throws JRException, IOException, DocumentException 
 */
package br.gov.mec.aghu.compras.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.action.RelatoriosExcelController.DividaHospitalMessages;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

/**
 * @author joao.pan
 *
 */
public class TituloPendentePagamentoController extends ActionReport {

	private static final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6508704319382115458L;

	// Logger
	private static final Log LOG = LogFactory.getLog(TituloPendentePagamentoController.class);

	private static final String MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP = "application/zip";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;

	// Constante da pagina responsavel pela visualização do relatório
	private static final String PAGE_RELATORIO_TITULO_PENDENTE_PAGAMENTO_PDF = "relatorioTituloPendentePagamentoPdf";
	
	// Constante da página responsável pela pesquisa
	private static final String PAGE_RELATORIO_TITULO_PENDENTE_PAGAMENTO = "relatorioTituloPendentePagamento";
		
	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}

	@EJB
	protected IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private ArquivoURINomeQtdVO arqVo;

	private Date dataInicioVencimentoTitulo;
	private Date dataFimVencimentoTitulo;
	private Date dataInicioEmissaoDocumentoFiscal;
	private Date dataFimEmissaoDocumentoFiscal;
	private ScoFornecedor fornecedor;

	List<TituloPendenteVO> colecao = new ArrayList<TituloPendenteVO>();

	private Boolean gerouArquivo;

	/**
	 * Método responsável pela impressão.
	 */
	@Override
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (JRException e) {
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
	}

	/**
	 * Metódo por gerar as dependências a impressão
	 * 
	 * @return
	 */
	public String imprimirRelatorio() {
		try {
			pesquisar();
			directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
		return null;
	}

	/**
	 * Método responsável pela visualização do relatório
	 * 
	 * @return página de referência do pdf
	 * @throws BaseException
	 */
	public String visualizarRelatorio() throws BaseException {
		try {
			pesquisar();
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			return null;
		}
		if (colecao.isEmpty()) {
			return null;
		}
		
		return PAGE_RELATORIO_TITULO_PENDENTE_PAGAMENTO_PDF;
	}

	/**
	 * Método que retorna o modelo do relatório
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/contaspagar/relatorioTituloPendenteDePagamento.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException {
		DocumentoJasper documento;

		try {
			documento = gerarDocumento(true);
			return criarStreamedContentPdf(documento.getPdfByteArray(false));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Método responsável por fornecer os dados do header do relatório
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual",	DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		String hospital;
		try {
			hospital = this.parametroFacade.buscarAghParametro(
					AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			params.put("hospitalLocal", hospital);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return params;
	}

	/**
	 * Método responsável por retornar a página de pesquisa
	 * 
	 * @return página de pesquisa
	 */
	public String voltar() {
		return PAGE_RELATORIO_TITULO_PENDENTE_PAGAMENTO;
	}

	/**
	 * Gera o relatório Excel preenchido e dispara o download
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void gerarRelatorio() throws ApplicationBusinessException {
		gerarArquivoParcial();
		if (gerouArquivo) {
			dispararDownloadArquivo();
		}
	}

	/**
	 * Preenche o relatório com os dados pesquisados pelo usuário
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void gerarArquivoParcial() throws ApplicationBusinessException {

		try {
			arqVo = comprasFacade.gerarCSVTituloPendentePagamento(
					dataInicioVencimentoTitulo, dataFimVencimentoTitulo,
					dataInicioEmissaoDocumentoFiscal,
					dataFimEmissaoDocumentoFiscal, fornecedor);
			if (arqVo != null) {
				this.gerouArquivo = Boolean.TRUE;
			} else {
				this.gerouArquivo = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR,
						DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO
								.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e.getMessage(),
					Severity.ERROR);
		}

	}

	/**
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void dispararDownloadArquivo() throws ApplicationBusinessException {

		if (this.arqVo != null) {
			try {
				this.internDispararDownload(this.arqVo.getUri(),
						this.arqVo.getNome(),
						MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP);
				this.gerouArquivo = Boolean.FALSE;

			} catch (IOException e) {
				throw new ApplicationBusinessException(
						DividaHospitalMessages.MENSAGEM_ERRO_GERAR_ARQUIVO
								.toString(),
						Severity.ERROR);
			}
		}
	}

	/**
	 * Dispara o download dado o tipo de midia e o arquivo
	 * 
	 * @param arquivo
	 * @param nomeArq
	 * @param mimeType
	 * @throws IOException
	 */
	protected void internDispararDownload(final URI arquivo,
			final String nomeArq, final String mimeType) throws IOException {

		FacesContext facesContext = null;
		HttpServletResponse response = null;
		ServletOutputStream out = null;
		FileInputStream in = null;
		byte[] buffer = null;
		int len = 0;

		facesContext = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) facesContext.getExternalContext()
				.getResponse();
		response.reset();
		response.setContentType(mimeType);
		response.setContentLength((int) (new File(arquivo)).length());
		response.addHeader("Content-disposition", "attachment; filename=\""
				+ nomeArq + "\"");
		response.addHeader("Cache-Control", "no-cache");
		buffer = new byte[BUFFER_SIZE_EQ_1M];
		// committing status and headers
		response.flushBuffer();
		out = response.getOutputStream();
		in = new FileInputStream(new File(arquivo));
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		out.flush();
		out.close();
		in.close();
		facesContext.responseComplete();
	}

	/**
	 * Método responsável por limpar a tela de pesquisa
	 */
	public String limpar() {
		this.fornecedor = null;
		this.dataInicioVencimentoTitulo = null;
		this.dataFimVencimentoTitulo = null;
		this.dataInicioEmissaoDocumentoFiscal = null;
		this.dataFimEmissaoDocumentoFiscal = null;
		return PAGE_RELATORIO_TITULO_PENDENTE_PAGAMENTO;
	}

	/**
	 * Método que preenche a suggestion box de fornecedores.
	 * 
	 * @return Uma {@link List} preenchida com os fornecedores.
	 */
	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.returnSGWithCount((List<ScoFornecedor>) comprasFacade
				.listarFornecedoresPorNumeroCnpjRazaoSocial(param),pesquisarFornecedoresCount(param));
	}

	/**
	 * Método que retorna a quantidade de registros de fornecedor para
	 * paginação.
	 * 
	 * @return Um {@link Long} que representa o número de registros da pesquisa.
	 */
	public Long pesquisarFornecedoresCount(String param) {
		return comprasFacade.listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(param);
	}

	/**
	 * Faz a pesquisa dos titulos pendentes dados os valores preenchidos pelo usuário
	 */
	@Override
	public Collection<TituloPendenteVO> recuperarColecao()
			throws ApplicationBusinessException {

			return this.colecao;
	}

	/**
	 * Retorna uma lista preenchida com os VO's do relatorio
	 * 
	 * @throws BaseException
	 */
	public void pesquisar() throws BaseException {
		colecao = (ArrayList<TituloPendenteVO>) comprasFacade
				.pesquisarTituloPendentePagamento(
						this.dataInicioVencimentoTitulo,
						this.dataFimVencimentoTitulo,
						this.dataInicioEmissaoDocumentoFiscal,
						this.dataFimEmissaoDocumentoFiscal, this.fornecedor);
	}

	/**
	 * @return the dataInicioVencimentoTitulo
	 */
	public Date getDataInicioVencimentoTitulo() {
		return dataInicioVencimentoTitulo;
	}

	/**
	 * @param dataInicioVencimentoTitulo
	 *            the dataInicioVencimentoTitulo to set
	 */
	public void setDataInicioVencimentoTitulo(Date dataInicioVencimentoTitulo) {
		this.dataInicioVencimentoTitulo = dataInicioVencimentoTitulo;
	}

	/**
	 * @return the dataFimVencimentoTitulo
	 */
	public Date getDataFimVencimentoTitulo() {
		return dataFimVencimentoTitulo;
	}

	/**
	 * @param dataFimVencimentoTitulo
	 *            the dataFimVencimentoTitulo to set
	 */
	public void setDataFimVencimentoTitulo(Date dataFimVencimentoTitulo) {
		this.dataFimVencimentoTitulo = dataFimVencimentoTitulo;
	}

	/**
	 * @return the dataInicioEmissaoDocumentoFiscal
	 */
	public Date getDataInicioEmissaoDocumentoFiscal() {
		return dataInicioEmissaoDocumentoFiscal;
	}

	/**
	 * @param dataInicioEmissaoDocumentoFiscal
	 *            the dataInicioEmissaoDocumentoFiscal to set
	 */
	public void setDataInicioEmissaoDocumentoFiscal(
			Date dataInicioEmissaoDocumentoFiscal) {
		this.dataInicioEmissaoDocumentoFiscal = dataInicioEmissaoDocumentoFiscal;
	}

	/**
	 * @return the dataFimEmissaoDocumentoFiscal
	 */
	public Date getDataFimEmissaoDocumentoFiscal() {
		return dataFimEmissaoDocumentoFiscal;
	}

	/**
	 * @param dataFimEmissaoDocumentoFiscal
	 *            the dataFimEmissaoDocumentoFiscal to set
	 */
	public void setDataFimEmissaoDocumentoFiscal(
			Date dataFimEmissaoDocumentoFiscal) {
		this.dataFimEmissaoDocumentoFiscal = dataFimEmissaoDocumentoFiscal;
	}

	/**
	 * @return the fornecedor
	 */
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	/**
	 * @param fornecedor
	 *            the fornecedor to set
	 */
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public List<TituloPendenteVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<TituloPendenteVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}
	
	
	
}
