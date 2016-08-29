package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
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

public class RelatorioRelacaoDeOPMNaoFaturadaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8978889159083012610L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioRelacaoDeOPMNaoFaturadaController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCompetencia competencia;

	private FatItensProcedHospitalar procedimentoRealizado;

	private FatItensProcedHospitalar procedimentoOrteseProtese;

	private String iniciaisPaciente;

	private Boolean reapresentada;

	private Boolean gerouArquivo;

	private String fileName;

	private Short phoSeq;

	public enum RelatorioRelacaoDeOPMNaoFaturadaExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@Inject
	private RelatorioRelacaoDeOPMNaoFaturadaPdfController reportController;

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
						RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
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
				return "relatorioRelacaoDeOPMNaoFaturadaPdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setIsDirectPrint(isDirectPrint);

		reportController.setIniciaisPaciente(iniciaisPaciente);

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
		if (procedimentoRealizado != null) {
			reportController.setProcedimento(procedimentoRealizado.getCodTabela());
		} else {
			reportController.setProcedimento(null);
		}
		if (procedimentoOrteseProtese != null) {
			reportController.setSSM(procedimentoOrteseProtese.getCodTabela());
		} else {
			reportController.setSSM(null);
		}
		reportController.setOrigem("relatorioRelacaoDeOPMNaoFaturada");

		reportController.inicio();

	}

	public void limpar() {
		iniciaisPaciente = null;
		competencia = null;
		procedimentoRealizado = null;
		procedimentoOrteseProtese = null;
		reapresentada = false;
		gerouArquivo = false;
		fileName = null;
	}

	public void gerarCSV() {
		try {

			fileName = faturamentoFacade.geraCSVRelatorioRelacaoDeOPMNaoFaturada(
					procedimentoOrteseProtese != null ? procedimentoOrteseProtese.getCodTabela() : null, competencia.getId().getAno(),
					competencia.getId().getMes(), competencia.getId().getDtHrInicio(),
					procedimentoRealizado != null ? procedimentoRealizado.getCodTabela() : null, iniciaisPaciente, reapresentada);
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

	// ## SUGGESTION BOX ##
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(String objPesquisa) {
		if (phoSeq == null) {
			creatPhoSeq();
		}
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(objPesquisa, phoSeq),listarFatItensProcedHospitalarCount(objPesquisa));
	}

	public Long listarFatItensProcedHospitalarCount(String objPesquisa) {
		if (phoSeq == null) {
			creatPhoSeq();
		}
		return this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, phoSeq);
	}

	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarOrteseProtese(String objPesquisa) {
		if (phoSeq == null) {
			creatPhoSeq();
		}
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(objPesquisa, phoSeq),listarFatItensProcedHospitalarOrteseProteseCount(objPesquisa));
	}

	public Long listarFatItensProcedHospitalarOrteseProteseCount(String objPesquisa) {
		if (phoSeq == null) {
			creatPhoSeq();
		}
		return this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProteseCount(objPesquisa, phoSeq);
	}

	private void creatPhoSeq() {
		try {
			if (phoSeq == null) {
				phoSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico().shortValue();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
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

	public FatItensProcedHospitalar getProcedimentoRealizado() {
		return procedimentoRealizado;
	}

	public void setProcedimentoRealizado(FatItensProcedHospitalar procedimentoRealizado) {
		this.procedimentoRealizado = procedimentoRealizado;
	}

	public FatItensProcedHospitalar getProcedimentoOrteseProtese() {
		return procedimentoOrteseProtese;
	}

	public void setProcedimentoOrteseProtese(FatItensProcedHospitalar procedimentoOrteseProtese) {
		this.procedimentoOrteseProtese = procedimentoOrteseProtese;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}
}
