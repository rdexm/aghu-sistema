package br.gov.mec.aghu.certificacaodigital.action;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.certificacaodigital.vo.RelatorioControlePendenciasVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioControlePendenciasController extends ActionReport  {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioControlePendenciasController.class);
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6681388852019274198L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	//Filtros
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustos;
	private DominioOrdenacaoRelatorioControlePendencias ordenacao;
	
	private Boolean gerouArquivo;
	private String fileName;
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioControlePendenciasVO> colecao = new ArrayList<RelatorioControlePendenciasVO>(0);

	@Override
	public Collection<RelatorioControlePendenciasVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	
	/**
	 * Impressao direta via CUPS.
	 * 
	 * @throws BaseException
	 * @throws SystemException
	 * @throws IOException
	 */
	@Override
	public void directPrint() {
		// Valida dados de entrada e prepara dados para geração do relatório.
		String retorno;
		try {
			retorno = this.print();
			if (retorno == null) {
				return;
			}
		} catch (BaseException | SystemException | IOException | DocumentException | JRException e1) {
			LOG.error(e1.getMessage(),e1);
		}

		

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {

		try{
			
			this.colecao = certificacaoDigitalFacade.pesquisaPendenciaAssinaturaDigital(this.getRapServidores(), 
					this.getFccCentroCustos(), this.getOrdenacao());
			if(this.colecao == null || this.colecao.isEmpty()){
				apresentarMsgNegocio(Severity.WARN, "MSG_DADOS_RELATORIO_NAO_ENCONTRADOS");
				return null;
			}

		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		return null;
		}
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return "relatorioControlePendenciasPdf";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/certificacaodigital/report/relatorioControlePendencias.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AGHR_PEND_CERTIF");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}

	
	public List<RapServidores> pesquisarServidores(String parametro) throws BaseException {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidorCertificadoDigital(parametro, this.getFccCentroCustos()),
				this.pesquisarServidoresCount(parametro));
	}

	private Long pesquisarServidoresCount(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidorCertificadoDigitalCount(parametro, this.getFccCentroCustos());
	}

	public void selecionarServidor() {
		if (this.getFccCentroCustos() == null) {
			if (this.getRapServidores().getCentroCustoAtuacao() != null) {
				this.setFccCentroCustos(this.getRapServidores().getCentroCustoAtuacao());
			} else {
				this.setFccCentroCustos(this.getRapServidores().getCentroCustoLotacao());
			}
		}
	}

	public void limparDadosServidor() {
		this.setRapServidores(null);
		this.setFccCentroCustos(null);
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String parametro) throws BaseException {
		List<FccCentroCustos> listResult = new ArrayList<FccCentroCustos>();
		
		if(this.getRapServidores() != null ){
			if (this.getRapServidores().getCentroCustoAtuacao() != null) {
				listResult.add(this.getRapServidores().getCentroCustoAtuacao());
			} else {
				listResult.add(this.getRapServidores().getCentroCustoLotacao());
			}
		}else {
			listResult = this.centroCustoFacade.pesquisarCentroCustos(parametro);
		}
		return this.returnSGWithCount(listResult,this.pesquisarCentroCustoCount(parametro));
	}

	private Number pesquisarCentroCustoCount(String parametro) {
		if(this.getRapServidores() != null &&
				(this.getRapServidores().getCentroCustoAtuacao() != null || this.getRapServidores().getCentroCustoLotacao() != null) ){
			return 1L;
		}else {
			return this.centroCustoFacade.pesquisarCentroCustosCount(parametro);
		}
	}

	public void limparDadosCentroCusto() {
		this.setFccCentroCustos(null);
	}
	
	public void limparPesquisa(){
		this.limparDadosServidor();
		this.limparDadosCentroCusto();
		this.setOrdenacao(null);
	}

	public void gerarCSV() {
		try {

			fileName = certificacaoDigitalFacade
					.geraCSVRelatorioControlePendencias(
							this.getRapServidores(), this.getFccCentroCustos(),
							this.getOrdenacao());
			gerouArquivo = true;

		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseRuntimeException());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				//certificacaoDigitalFacade.downloadedCSVRelatorioControlePendencias(fileName);
													
				final FacesContext fc = FacesContext.getCurrentInstance();
				final String nomeDownload = DominioNomeRelatorio.PENDENCIAS_ASSINATURA_DIGITAL + "_" + new SimpleDateFormat("MM_yyyy").format(new Date()) + EXTENSAO;
				if (fc != null && fc.getExternalContext() != null
						&& fc.getExternalContext().getResponse() != null) {
					final HttpServletResponse response = (HttpServletResponse) fc
							.getExternalContext().getResponse();

					response.setContentType("text/csv");
					response.setHeader("Content-Disposition", "attachment;filename="
							+ nomeDownload);
					response.getCharacterEncoding();

					final OutputStream out = response.getOutputStream();
					final Scanner scanner = new Scanner(new FileInputStream(fileName),
							ENCODE);

					while (scanner.hasNextLine()) {
						out.write(scanner.nextLine().getBytes(ENCODE));
						out.write(System.getProperty("line.separator").getBytes(ENCODE));
					}

					scanner.close();
					out.flush();
					out.close();
					fc.responseComplete();
				}		
				
				gerouArquivo = false;
				fileName = null;

			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseRuntimeException());
			}
		}
	}

	// getters e setters

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public DominioOrdenacaoRelatorioControlePendencias getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoRelatorioControlePendencias ordenacao) {
		this.ordenacao = ordenacao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
