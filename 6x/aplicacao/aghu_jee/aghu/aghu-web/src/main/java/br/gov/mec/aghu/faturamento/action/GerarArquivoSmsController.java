package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

public class GerarArquivoSmsController extends ActionController {
	
	private static final long serialVersionUID = -3549799538041685844L;
	
	private static final Log LOG = LogFactory.getLog(GerarArquivoSmsController.class);

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private RelatorioProtocoloAihController reportController;
	
	private Date data;
	
	@PostConstruct
	public void inicio() {
		begin(conversation, true);
	}
	
	private enum GerarArquivoSmsControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS
	}
	
	public void listener(final FileUploadEvent e) {
		try {
			UploadedFile item = e.getFile();
			faturamentoFacade.processarRetornoSms(item.getInputstream(), obterNomeMicrocomputador());
			apresentarMsgNegocio(Severity.INFO, "MSG_ARQ_SMS_PROCESSADO_SUCESSO");
		} catch (Exception e1) {
			LOG.error(e1.getMessage(),e1);
			apresentarExcecaoNegocio(new ApplicationBusinessException(GerarArquivoSmsControllerExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS, e1.getMessage()));
		}
	}
	
	private String obterNomeMicrocomputador()  throws ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
			new ApplicationBusinessException(GerarArquivoSmsControllerExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS);
		}
		return nomeMicrocomputador;

	}

	public void validarGerarArquivo() {
		gerarArquivo();
	}
	
	private void download(byte[] fileOut, String fileName, String contentType, String encoding) throws IOException {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType(contentType);
		response.setHeader("Content-Disposition","attachment;filename=" + fileName + "; charset=" + encoding);
		
		response.getCharacterEncoding();

		final OutputStream out = response.getOutputStream();

		out.write(fileOut);
		out.flush();
		out.close();
		fc.responseComplete();
	}
	
	public void gerarArquivo() {
		try {
			super.closeDialog("modalConfirmacaoWG");
			this.download(faturamentoFacade.gerarArquivoSms(data).toByteArray(), DateUtil.dataToString(data, "ddMMyyyy")+ "." + DominioMimeType.TXT.getExtension() , DominioMimeType.TXT.getContentType(), "ISO-8859-1");
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e1) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_ARQ_SMS");
		}
	}
	
	public void imprimirRelatorio() throws BaseException, JRException, SystemException, IOException {
		reportController.setData(data);
		reportController.directPrint();
	}

	public void dispararDownloadPDF() {
		try {
			reportController.setData(data);
			DocumentoJasper documento = reportController.buscarDocumentoGerado();
					
			final File file = File.createTempFile("PROT_AIH" +  DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_MM) +   DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY), ".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();
			
			download(file, DominioMimeType.PDF.getContentType());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		return "pesquisarProtocolosAihs";
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
}
