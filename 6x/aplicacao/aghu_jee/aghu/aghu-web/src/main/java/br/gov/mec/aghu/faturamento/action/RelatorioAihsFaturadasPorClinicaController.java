package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAihsFaturadasPorClinicaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8978889159083012610L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioAihsFaturadasPorClinicaController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCompetencia competencia;

	private String iniciaisPaciente;

	private Boolean gerouArquivo;

	private String fileName;	

	public enum RelatorioRelacaoDeOPMNaoFaturadaExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS,INICIAIS_INVALIDAS_ORTESES_PROTESES,PREENCHER_DATAS_ORTESES;
	}

	@Inject
	private RelatorioAihsFaturadasPorClinicaPdfController reportController;

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
						RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.INICIAIS_INVALIDAS_ORTESES_PROTESES.toString());
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
				return "relatorioAihsFaturadasPorClinicaPdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.INICIAIS_INVALIDAS_ORTESES_PROTESES.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) throws ApplicationBusinessException {
		reportController.setIsDirectPrint(isDirectPrint);
		reportController.setIniciaisPaciente(iniciaisPaciente);

		if (competencia != null && competencia.getId() != null) {
			reportController.setDtHrInicio(competencia.getId().getDtHrInicio());
			reportController.setMes(competencia.getId().getMes());
			reportController.setAno(competencia.getId().getAno());
		} else {
			reportController.setDtHrInicio(null);
			reportController.setMes(null);
			reportController.setAno(null);
		}
		reportController.setOrigem("relatorioAihsFaturadasPorClinica");

		reportController.inicio();
	}

	public void limpar() {
		competencia = null;
		iniciaisPaciente = null;
		gerouArquivo = false;
		fileName = null;
	}

	public void gerarCSV() {
		try {

			fileName = faturamentoFacade.geraCSVRelatorioAihsFaturadasPorClinica(
								competencia.getId().getAno(),
								competencia.getId().getMes(), 
								competencia.getId().getDtHrInicio(),
								iniciaisPaciente
								);
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
