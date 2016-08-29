package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAIHFaturadaPorPacienteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7789849386247806960L;

	private static final Log LOG = LogFactory.getLog(RelatorioAIHFaturadaPorPacienteController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private FatCompetencia competencia;

	private AghClinicas clinica;

	private String iniciaisPaciente;

	private Boolean reapresentada;

	private Boolean gerouArquivo;

	private String fileName;
	
	public enum RelatorioAIHFaturadaPorPacienteExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}
	

	@Inject
	private RelatorioAIHFaturadaPorPacientePdfController reportController;

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
						RelatorioAIHFaturadaPorPacienteExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String visualizarRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				inicializaReportController(false);
				return "relatorioAIHFaturadaPorPacientePdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioAIHFaturadaPorPacienteExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setIsDirectPrint(isDirectPrint);
		if (competencia != null && competencia.getId() != null) {
			reportController.setDtHrInicio(competencia.getId().getDtHrInicio());
			reportController.setMes(competencia.getId().getMes());
			reportController.setAno(competencia.getId().getAno());
		} else {
			reportController.setAno(null);
			reportController.setDtHrInicio(null);
			reportController.setMes(null);
		}
		if (clinica != null) {
			reportController.setClinica(clinica.getCodigo());
		} else {
			reportController.setClinica(null);
		}
		reportController.setIniciaisPaciente(iniciaisPaciente);
		reportController.setReapresentada(reapresentada);

		reportController.inicio();

	}

	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.geraCSVRelatorioAIHFaturadaPorPaciente(competencia.getId().getDtHrInicio(), competencia.getId()
					.getAno(), competencia.getId().getMes(), iniciaisPaciente, reapresentada, clinica != null ? clinica.getCodigo() : null);
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
		iniciaisPaciente = null;
		competencia = null;
		reapresentada = false;
		gerouArquivo = false;
		fileName = null;
		clinica = null;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa)),pesquisarCompetenciasCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	public Long pesquisarClinicasCount(String filtro) {
		return this.aghuFacade.pesquisarClinicasCount((String) filtro);
	}

	public List<AghClinicas> pesquisarClinicas(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarClinicas((String) filtro),pesquisarClinicasCount(filtro));
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
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

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}
}