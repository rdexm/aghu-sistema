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
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAihsPorProcedimentoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7862753136953944091L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioAihsPorProcedimentoController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatItensProcedHospitalar procedimentoInicial;

	private FatItensProcedHospitalar procedimentoFinal;

	private FatCompetencia competencia;

	private String iniciais;

	private Boolean reapresentada;

	private Boolean gerouArquivo;

	private String fileName;

	private Boolean todosProcedimentos;

	private Long codTabIni;

	private Long codTabFim;

	public enum RelatorioAihsPorProcedimentoControllerExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@Inject
	private RelatorioAihsPorProcedimentoPdfController reportController;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public String imprimirRelatorio() {
		if (validacoesCampos()) {
			inicializaReportController(true);
			return "";
		}
		return null;
	}

	public String visualizarRelatorio() {
		if (validacoesCampos()) {
			inicializaReportController(false);
			return "relatorioAihsPorProcedimentoPdf";
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setIsDirectPrint(isDirectPrint);

		reportController.setIniciaisPaciente(iniciais);

		if (procedimentoFinal != null) {
			reportController.setProcedimentoFinal(procedimentoFinal.getCodTabela());
		} else {
			reportController.setProcedimentoFinal(null);
		}

		if (procedimentoInicial != null) {
			reportController.setProcedimentoInicial(procedimentoInicial.getCodTabela());
		} else {
			reportController.setProcedimentoInicial(null);
		}

		reportController.setTodosProcedimentos(todosProcedimentos);

		reportController.setReapresentada(reapresentada);

		if (competencia != null && competencia.getId() != null) {
			reportController.setDtHrInicio(competencia.getId().getDtHrInicio());
			reportController.setMes(competencia.getId().getMes());
			reportController.setAno(competencia.getId().getAno());
		} else {
			reportController.setDtHrInicio(null);
			reportController.setMes(null);
			reportController.setAno(null);
		}

		reportController.inicio();

	}

	private void obterProcedimentos() {
		codTabIni = (Boolean.TRUE.equals(todosProcedimentos)) ? 1 : procedimentoInicial.getCodTabela();

		if (Boolean.TRUE.equals(todosProcedimentos)) {
			codTabFim = 999999999L;

		} else if (procedimentoFinal != null) {
			codTabFim = procedimentoFinal.getCodTabela();

		} else {
			codTabFim = null;
		}
	}

	private boolean validacoesCampos() {
		try {
			if (!CoreUtil.validaIniciaisPaciente(iniciais)) {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioAihsPorProcedimentoControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
				return false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		}
		
		obterProcedimentos();

		if (codTabIni != null && codTabFim != null) {
			if (codTabFim.longValue() < codTabIni.longValue()) {
				apresentarMsgNegocio(Severity.ERROR, "PROCEDIMENTO_FINAL_MENOR_QUE_PROCEDIMENTO_INICIAL");
				return false;
			}
		}

		return true;
	}

	public void clearProcedimentos() {
		procedimentoInicial = null;
		procedimentoFinal = null;
	}

	public void gerarCSV() {
		try {
			if (validacoesCampos()) {
				obterProcedimentos();

				fileName = faturamentoFacade.geraCSVRelatorioAIHPorProcedimento(codTabIni, codTabFim, competencia.getId().getDtHrInicio(),
						competencia.getId().getMes(), competencia.getId().getAno(), iniciais, reapresentada);
				gerouArquivo = true;
			}

		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));

		} catch (BaseException e) {
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
		competencia = null;
		procedimentoInicial = null;
		procedimentoFinal = null;
		todosProcedimentos = false;
		iniciais = null;
		reapresentada = false;
		gerouArquivo = false;
		fileName = null;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa));

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public List<FatItensProcedHospitalar> pesquisarItensProc(final String obj) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarIPHPorConvenioSaudePlanoConvProcedHosp(obj),pesquisarItensProcCount(obj));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatItensProcedHospitalar>(0);
		}
	}

	public Long pesquisarItensProcCount(final String obj) {
		try {
			return faturamentoFacade.listarIPHPorConvenioSaudePlanoConvProcedHospCount(obj);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public FatItensProcedHospitalar getProcedimentoInicial() {
		return procedimentoInicial;
	}

	public void setProcedimentoInicial(FatItensProcedHospitalar procedimentoInicial) {
		this.procedimentoInicial = procedimentoInicial;
	}

	public FatItensProcedHospitalar getProcedimentoFinal() {
		return procedimentoFinal;
	}

	public void setProcedimentoFinal(FatItensProcedHospitalar procedimentoFinal) {
		this.procedimentoFinal = procedimentoFinal;
	}

	public String getIniciais() {
		return iniciais;
	}

	public void setIniciais(String iniciais) {
		this.iniciais = iniciais;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
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

	public Boolean getTodosProcedimentos() {
		return todosProcedimentos;
	}

	public void setTodosProcedimentos(Boolean todosProcedimentos) {
		this.todosProcedimentos = todosProcedimentos;
	}
}