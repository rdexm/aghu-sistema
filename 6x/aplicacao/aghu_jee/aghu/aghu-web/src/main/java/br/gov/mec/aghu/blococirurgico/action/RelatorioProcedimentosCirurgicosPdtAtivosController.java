package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioProcedimentosCirurgicosPdtAtivosController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -2867911187962704659L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private ProcedimentosCirurgicosPdtAtivosController procedimentosCirurgicosPdtAtivosController;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private String fileName;
	private Boolean gerouArquivo = Boolean.FALSE;
	private Boolean isDirectPrint = gerouArquivo = Boolean.FALSE;
	private Integer numeroCopias;
	
	private static final String RELATORIO = "procedimentosCirurgicosPdtAtivos";
	private static final String RELATORIO_PDF = "procedimentosCirurgicosPdtAtivosPdf";
	
	private static final Log LOG = LogFactory.getLog(RelatorioProcedimentosCirurgicosPdtAtivosController.class);
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private Short obterEspSeq() {
		return procedimentosCirurgicosPdtAtivosController.obterEspSeq();
	}
	
	private Integer obterProcedimentoSeq(){
		return procedimentosCirurgicosPdtAtivosController.obterProcedimentoSeq();
	}
	
	public void gerarCSV() {

		try {
			fileName = blocoCirurgicoFacade.gerarCSVProcedimentosCirurgicosPdtAtivos(obterEspSeq(), obterProcedimentoSeq());
			gerouArquivo = Boolean.TRUE;

		} catch (ApplicationBusinessException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao capturada: ",e);
		} catch (IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}

	public void executarDownload() {
		if (fileName != null) {

			try {
				this.download(
						fileName,
						DominioNomeRelatorio.MBCR_PROCED_ESPECIAL.toString() + DominioNomeRelatorio.EXTENSAO_CSV, DominioMimeType.CSV.getContentType());
				
				fileName = null;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	public String directPrint(Integer numeroCopias) {
			try {
				blocoCirurgicoFacade.validarNroDeCopiasRelProcedCirgPdtAtivos(numeroCopias);
				for (int i = 0; i < numeroCopias; i++) {
					directPrint();
				}
				return RELATORIO;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			}
		return null;
	}
	
	public String voltar(){
		return RELATORIO;
	}

	@Override
	public Collection<ProcedimentosCirurgicosPdtAtivosVO> recuperarColecao() throws ApplicationBusinessException {
		
		 Collection<ProcedimentosCirurgicosPdtAtivosVO> colecao = null;
		
		try {
			colecao = blocoCirurgicoFacade.obterProcedimentosCirurgicosPdtAtivosVO(obterEspSeq(), obterProcedimentoSeq());
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return colecao;

	} 

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 */
	public void directPrint() throws ApplicationBusinessException {
		
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio()  {
		return "br/gov/mec/aghu/blococirurgico/report/RelatorioProcedimentosCirurgicosPdtAtivos.jasper";
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print()throws JRException, IOException, DocumentException{
		try {
			recuperarColecao();
		
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
		
		return RELATORIO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		
	}

	private String obterNomeInstituicaoHospitalar() {
		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		return (nomeHospital != null) ? nomeHospital.toUpperCase() : nomeHospital;
	}
	
	public Map<String, Object> recuperarParametros() {	
		
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeHospital", obterNomeInstituicaoHospitalar());
		params.put("nomeRelatorio", StringUtils.upperCase(this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_PDF_TITLE_RELATORIO")));
		params.put("espcialidadeHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_ESPECIALIDADE"));
		params.put("procedimentoHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_PROCEDIMENTO"));
		params.put("contaminacaoHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_CONTAMINACAO"));
		params.put("phiHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_PHI"));
		params.put("ambHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_AMB"));
		params.put("intHeader", this.getBundle().getString("PROCED_CIR_PDT_ATIVOS_HEADER_PDF_INT"));
		return params;
	}

	//Getters and Setters
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
	
	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Integer getNumeroCopias() {
		return numeroCopias;
	}

	public void setNumeroCopias(Integer numeroCopias) {
		this.numeroCopias = numeroCopias;
	}

}
