package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.action.RelatoriosExcelController.DividaHospitalMessages;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioDividaResumoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioDividaResumoController extends ActionReport {

	private static final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 8711102200832645601L;

	// Logger
	private static final Log LOG = LogFactory.getLog(RelatorioDividaResumoController.class);	

	// Constante da página responsável pela pesquisa
	private static final String PAGE_RELATORIO_DIVIDA_RESUMO = "relatorioDividaResumo";
	private static final String PAGE_RELATORIO_DIVIDA_RESUMO_PDF = "relatorioDividaResumoPdf";
	private static final String NOME_RELATORIO = "FCPR_TOT_DIVID_RESUM";
	
	// Nome do arquivo CSV
	private String fileName;
	// Data para a pesquisa
	private Date dataFinal;	
	// Coleção com os títulos
	private List<RelatorioDividaResumoVO> colecao;	
	// Flag que indica se o arquivo foi gerado corretamente
	private Boolean gerouArquivo;

	@EJB
	protected IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}	

	/**
	 * Método responsável pela visualização do relatório
	 * 
	 * @return página de referência do pdf
	 * @throws BaseException
	 */
	public String visualizarRelatorio() throws BaseException {
		String retorno = PAGE_RELATORIO_DIVIDA_RESUMO_PDF;
		try {
			pesquisar();
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			retorno = null;
		}
		if (colecao == null || colecao.isEmpty()) {
			retorno = null;
			apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO.toString());
		}
		return retorno;
	}

	/**
	 * Método que retorna o modelo do relatório
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/contaspagar/relatorioDividaResumo.jasper";
	}

	/**
	 * Método responsável por fornecer os dados do header do relatório
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("dataFinal", DateUtil.obterDataFormatada(this.dataFinal, "dd/MM/yyyy"));
		params.put("nomeRelatorio", NOME_RELATORIO);
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/contaspagar/");
		try {
			params.put("nomeHospital", this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		DocumentoJasper documento;
		try {
			documento = gerarDocumento(true);
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Faz a pesquisa dos titulos pendentes dados os valores preenchidos pelo
	 * usuário
	 */
	public List<RelatorioDividaResumoVO> recuperarColecao() throws ApplicationBusinessException {
		List<RelatorioDividaResumoVO> lista = new ArrayList<RelatorioDividaResumoVO>();	
		try {
			//caso o usuario nao tenha preenchido a data
			Date dataParam = new Date();
			if (this.dataFinal!=null) {
				dataParam = this.dataFinal;
			}			
			RelatorioDividaResumoVO vo = new RelatorioDividaResumoVO();
			List<RelatorioDividaResumoVO> colecaoAtrasados = this.comprasFacade.pesquisarDividaResumoAtrasados(dataParam);
			List<RelatorioDividaResumoVO>  colecaoAVencer = this.comprasFacade.pesquisarDividaResumoAVencer(dataParam);
			vo.setListaRelatorioAtrasados(colecaoAtrasados);
			vo.setListaRelatorioAVencer(colecaoAVencer);
			if((colecaoAtrasados != null && !colecaoAtrasados.isEmpty()) || (colecaoAVencer != null && !colecaoAVencer.isEmpty())) {
				lista.add(vo);		
			}			
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		}
		return lista;
	}

	/**
	 * Retorna uma lista preenchida com os VO's do relatorio
	 * 
	 * @throws BaseException
	 */
	public void pesquisar() throws BaseException {
		colecao = recuperarColecao();
	}

	/**
	 * Método responsável pela impressão.
	 */
	@Override
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
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
	 * Método responsável por retornar a página de pesquisa
	 * 
	 * @return página de pesquisa
	 */
	public String voltar() {
		return PAGE_RELATORIO_DIVIDA_RESUMO;
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
			Date dataParam = new Date();
			if (this.dataFinal!=null) {
				dataParam = this.dataFinal;
			}			
			fileName = comprasFacade.gerarCSVDividaResumo(dataParam);
			if (fileName != null) {
				this.gerouArquivo = Boolean.TRUE;
			} else {
				this.gerouArquivo = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (Exception e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		}
	}

	/**
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void dispararDownloadArquivo() throws ApplicationBusinessException {
		if (this.fileName != null && !this.fileName.isEmpty()) {
			try {
				String header = fileName.substring((fileName.lastIndexOf('/') == -1 ? fileName.lastIndexOf('\\') : fileName.lastIndexOf('/')) + 1);
				this.download(fileName, header);
				this.gerouArquivo = Boolean.FALSE;
			} catch (IOException e) {
				throw new ApplicationBusinessException(DividaHospitalMessages.MENSAGEM_ERRO_GERAR_ARQUIVO.toString(), Severity.ERROR);
			}
		}
	}

	/**
	 * Método responsável por limpar a tela de pesquisa
	 */
	public String limpar() {
		this.dataFinal = null;
		return PAGE_RELATORIO_DIVIDA_RESUMO;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the dataFinal
	 */
	public Date getDataFinal() {
		return dataFinal;
	}

	/**
	 * @param dataFinal the dataFinal to set
	 */
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	/**
	 * @return the colecao
	 */
	public List<RelatorioDividaResumoVO> getColecao() {
		return colecao;
	}

	/**
	 * @param colecao the colecao to set
	 */
	public void setColecao(List<RelatorioDividaResumoVO> colecao) {
		this.colecao = colecao;
	}
}
