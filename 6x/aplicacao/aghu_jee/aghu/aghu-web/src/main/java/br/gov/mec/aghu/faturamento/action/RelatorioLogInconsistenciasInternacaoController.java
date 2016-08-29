package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioLogInconsistenciasInternacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4964982940911374231L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioLogInconsistenciasInternacaoController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Date dtCriacaoIni;

	private Date dtCriacaoFim;

	private Date dtPrevia;

	private Integer pacProntuario;

	private String inconsistencia;

	private Integer cthSeq;

	private String iniciaisPaciente;

	private DominioGrupoProcedimento grupoProcedimento;

	private Boolean reapresentada;

	private Boolean gerouArquivo;

	private String fileName;

	public enum RelatorioLogInconsistenciasInternacaoExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@Inject
	private RelatorioLogInconsistenciasInternacaoPdfController reportController;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public void imprimirRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(true);
				
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioLogInconsistenciasInternacaoExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String visualizarRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(false);
				return "relatorioLogInconsistenciasInternacaoPdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioLogInconsistenciasInternacaoExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setIsDirectPrint(isDirectPrint);
		reportController.setCthSeq(cthSeq);
		reportController.setPacProntuario(pacProntuario);
		reportController.setGrupoProcedimento(grupoProcedimento);
		reportController.setDtCriacaoFim(dtCriacaoFim);
		reportController.setDtCriacaoIni(dtCriacaoIni);
		reportController.setDtPrevia(dtPrevia);
		reportController.setIniciaisPaciente(iniciaisPaciente);
		reportController.setInconsistencia(inconsistencia);
		reportController.setReapresentada(reapresentada);
		reportController.setOrigem("relatorioLogInconsistenciasInternacao");
		reportController.inicio();

	}

	public void gerarCSV() {
		try {

			fileName = faturamentoFacade.geraCSVRelatorioLogInconsistenciasInternacao(dtCriacaoIni, dtCriacaoFim, dtPrevia, pacProntuario,
					cthSeq, inconsistencia, iniciaisPaciente, reapresentada, grupoProcedimento);

			gerouArquivo = true;

		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName);
				gerouArquivo = false;
				fileName = null;

			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}

	public void limpar() {
		dtCriacaoIni = null;
		dtCriacaoFim = null;
		dtPrevia = null;
		pacProntuario = null;
		cthSeq = null;
		iniciaisPaciente = null;
		grupoProcedimento = null;
		reapresentada = false;
		inconsistencia = null;
		gerouArquivo = false;
		fileName = null;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDtCriacaoIni() {
		return dtCriacaoIni;
	}

	public void setDtCriacaoIni(Date dtCriacaoIni) {
		this.dtCriacaoIni = dtCriacaoIni;
	}

	public Date getDtCriacaoFim() {
		return dtCriacaoFim;
	}

	public void setDtCriacaoFim(Date dtCriacaoFim) {
		this.dtCriacaoFim = dtCriacaoFim;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getInconsistencia() {
		return inconsistencia;
	}

	public void setInconsistencia(String inconsistencia) {
		this.inconsistencia = inconsistencia;
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

	public Date getDtPrevia() {
		return dtPrevia;
	}

	public void setDtPrevia(Date dtPrevia) {
		this.dtPrevia = dtPrevia;
	}

	public DominioGrupoProcedimento getGrupoProcedimento() {
		return grupoProcedimento;
	}

	public void setGrupoProcedimento(DominioGrupoProcedimento grupoProcedimento) {
		this.grupoProcedimento = grupoProcedimento;
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

}
