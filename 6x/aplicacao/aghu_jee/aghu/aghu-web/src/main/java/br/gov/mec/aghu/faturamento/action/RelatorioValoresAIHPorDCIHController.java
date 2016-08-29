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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class RelatorioValoresAIHPorDCIHController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6046877517133747804L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioValoresAIHPorDCIHController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCompetencia competencia;

	private Boolean gerouArquivo;

	private String fileName;

	@Inject
	private RelatorioValoresAIHPorDCIHPdfController reportController;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public String imprimirRelatorio() {
		inicializaReportController(true);
		return "";
	}

	public String visualizarRelatorio() {
		inicializaReportController(false);
		return "relatorioValoresAIHPorDCIHPdf";
	}

	private void inicializaReportController(boolean isDirectPrint) {
		reportController.setIsDirectPrint(isDirectPrint);

		if (competencia != null && competencia.getId() != null) {
			reportController.setAno(competencia.getId().getAno());
			reportController.setDtHrInicio(competencia.getId().getDtHrInicio());
			reportController.setMes(competencia.getId().getMes());
		} else {
			reportController.setAno(null);
			reportController.setDtHrInicio(null);
			reportController.setMes(null);
		}

		reportController.inicio();
	}

	public void limpar() {
		competencia = null;
		gerouArquivo = false;
		fileName = null;
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

	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.geraCSVRelatorioValoresAIHPorDCIH(competencia.getId().getAno(), competencia.getId().getMes());
			gerouArquivo = true;
			
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));
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

}