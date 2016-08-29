package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.internacao.leitos.action.AtenderTransferenciaPacientePaginatorController;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.GerarPDFSinanVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * #45293 Prescrição: Gerar PDF Na Visualizar Justificativa Do Uso De Medicamento.
 * 
 */
public class GerarPdfSinanController extends ActionReport {

	private static final long serialVersionUID = -7722678727827452483L;
	
	private static final Log LOG = LogFactory.getLog(GerarPdfSinanController.class);

	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/prescricaomedica/report/relatorioFichaNotificacaoInvestigacao.jasper";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AtenderTransferenciaPacientePaginatorController atenderTransferenciaPacientePaginatorController;
	
	private Integer atdSeq;
	
	private List<GerarPDFSinanVO> listaPdfSinan;
	private GerarPDFSinanVO pdfSinanVO;

	/**
	 * Inicializa a conversação.
	 */
	@PostConstruct
	public void init() {

		begin(conversation);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.action.report.ActionReport#recuperarColecao()
	 */
	@Override
	protected Collection<GerarPDFSinanVO> recuperarColecao() throws ApplicationBusinessException {

		pdfSinanVO = prescricaoMedicaFacade.gerarPdfSinan(atdSeq);
		
		pdfSinanVO.setProntuarioFormatado(obterProntuarioFormatado(pdfSinanVO));
		listaPdfSinan.add(pdfSinanVO);
		return listaPdfSinan;
	}
	
	/**
	 * Método para formatar a máscara do prontuário.
	 * @param pdfSinanVO
	 * @return String
	 */
	private String obterProntuarioFormatado(GerarPDFSinanVO pdfSinanVO){
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(pdfSinanVO.getProntuario());
		
		return this.atenderTransferenciaPacientePaginatorController.getProntuarioFormatado(paciente);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.action.report.ActionReport#recuperarArquivoRelatorio()
	 */
	@Override
	protected String recuperarArquivoRelatorio() {

		return URL_DOCUMENTO_JASPER;
	}

	/**
	 * Realiza o download do PDF.	
	 * @throws IOException
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws DocumentException
	 */
	public void downloadPdf() {
		try {
			listaPdfSinan = new ArrayList<GerarPDFSinanVO>();
			
			DocumentoJasper documento = gerarDocumento();
			
			final FacesContext fc = FacesContext.getCurrentInstance();
			final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
	        response.reset();
			response.setContentType("text/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=" + atdSeq + "_NotificacaoTB" + ".pdf");
	      	response.getCharacterEncoding();
	        response.flushBuffer();
			
	        ServletOutputStream out = response.getOutputStream();
			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();
			
			fc.responseComplete();
		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MSG_ERRO_IMPRIMIR_PDF");
		}
	}

	/*
	 * Getters and Setters
	 */

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public List<GerarPDFSinanVO> getListaPdfSinan() {
		return listaPdfSinan;
	}

	public void setListaPdfSinan(List<GerarPDFSinanVO> listaPdfSinan) {
		this.listaPdfSinan = listaPdfSinan;
	}

	public GerarPDFSinanVO getPdfSinanVO() {
		return pdfSinanVO;
	}

	public void setPdfSinanVO(GerarPDFSinanVO pdfSinanVO) {
		this.pdfSinanVO = pdfSinanVO;
	}
	
}
