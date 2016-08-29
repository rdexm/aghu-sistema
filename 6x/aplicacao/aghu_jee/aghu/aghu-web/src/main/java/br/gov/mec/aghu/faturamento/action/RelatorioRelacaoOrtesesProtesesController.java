package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

public class RelatorioRelacaoOrtesesProtesesController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8978889159083012610L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioRelacaoOrtesesProtesesController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCompetencia competencia;

	private FatItensProcedHospitalar procedimentoOrteseProtese;

	private String iniciaisPaciente;

	private Boolean gerouArquivo;

	private String fileName;

	private Short phoSeq;
	
	private Date dtIni;
	
	private Date dtFim;

	public enum RelatorioRelacaoDeOPMNaoFaturadaExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS,INICIAIS_INVALIDAS_ORTESES_PROTESES,PREENCHER_DATAS_ORTESES;
	}

	@Inject
	private RelatorioRelacaoDeOrtesesProtesesPdfController reportController;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
		gerouArquivo = false;
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
				return "relatorioRelacaoDeOrtesesProtesesPdf";
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
		if((dtIni == null && dtFim != null)||(dtIni != null && dtFim == null)){
			throw new ApplicationBusinessException(RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.PREENCHER_DATAS_ORTESES);
		}
		reportController.setIsDirectPrint(isDirectPrint);
		reportController.setIniciaisPaciente(iniciaisPaciente);
		reportController.setDtIni(dtIni);
		reportController.setDtFim(dtFim);

		if (competencia != null && competencia.getId() != null) {
			reportController.setDtHrInicio(competencia.getId().getDtHrInicio());
			reportController.setMes(competencia.getId().getMes());
			reportController.setAno(competencia.getId().getAno());
		} else {
			reportController.setDtHrInicio(null);
			reportController.setMes(null);
			reportController.setAno(null);
		}
		
		if (procedimentoOrteseProtese != null) {
			reportController.setProcedimento(procedimentoOrteseProtese.getCodTabela());
		} else {
			reportController.setProcedimento(null);
		}
		reportController.setOrigem("relatorioRelacaoOrtesesProteses");

		reportController.inicio();

	}

	public void limpar() {
		competencia = null;
		procedimentoOrteseProtese = null;
		iniciaisPaciente = null;
		dtIni = null;
		dtFim = null;
		gerouArquivo = false;
		fileName = null;
	}

	public void gerarCSV() {
		try {
			if((dtIni == null && dtFim != null)||(dtIni != null && dtFim == null)){
				throw new ApplicationBusinessException(RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.PREENCHER_DATAS_ORTESES);
			}
			fileName = faturamentoFacade.geraCSVRelatorioRelacaoOrtesesProteses(
								procedimentoOrteseProtese != null ? procedimentoOrteseProtese.getCodTabela() : null, 
								competencia.getId().getAno(),
								competencia.getId().getMes(), 
								competencia.getId().getDtHrInicio(),
								iniciaisPaciente,
								dtIni,
								dtFim);
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

	public Date getDtIni() {
		return dtIni;
	}

	public void setDtIni(Date dtIni) {
		this.dtIni = dtIni;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	
}
