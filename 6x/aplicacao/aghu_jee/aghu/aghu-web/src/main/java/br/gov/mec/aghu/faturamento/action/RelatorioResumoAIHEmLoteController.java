package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioResumoAIHEmLoteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4827968302586220107L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioResumoAIHEmLoteController.class);

	private Log getLog() {
		return LOG;
	}

	private Date dtInicial;

	private Date dtFinal = new Date();

	private Integer cthSeq;

	private String iniciaisPaciente;

	private String fileName;

	private Boolean reapresentada;

	private Boolean autorizada;

	private boolean gerouPDF = false;

	@Inject
	private RelatorioResumoAIHEmLotePdfController reportController;

	public enum RelatorioResumoAIHEmLoteControllerExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public String imprimirRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(true);
				return "";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioResumoAIHEmLoteControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String visualizarRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(false);
				return "relatorioResumoAIHEmLotePdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioResumoAIHEmLoteControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setDirectPrint(isDirectPrint);
		
		reportController.setCthSeq(cthSeq);
		reportController.setDtFinal(dtFinal);
		reportController.setDtInicial(dtInicial);
		reportController.setAutorizada(autorizada);
		reportController.setReapresentada(reapresentada);
		reportController.setIniciaisPaciente(iniciaisPaciente);
		
		reportController.inicio();

	}

	public void dispararDownload() {
		if (!StringUtils.isEmpty(fileName)) {
			try {
				download(fileName, fileName, "application/pdf");
				gerouPDF = false;
			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF, e,
						e.getLocalizedMessage()));
			}
		}
		fileName = null;
	}

	public void gerarPdf() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(false);
				fileName = reportController.gerarPdf();
				if (StringUtils.isNotBlank(fileName)) {
					gerouPDF = true;
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioResumoAIHEmLoteControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limpar() {
		cthSeq = null;
		dtFinal = null;
		dtInicial = null;
		iniciaisPaciente = null;
		fileName = null;
		gerouPDF = false;
		reapresentada = false;
		autorizada = false;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Boolean getAutorizada() {
		return autorizada;
	}

	public void setAutorizada(Boolean autorizada) {
		this.autorizada = autorizada;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isGerouPDF() {
		return gerouPDF;
	}

	public void setGerouPDF(boolean gerouPDF) {
		this.gerouPDF = gerouPDF;
	}

}
