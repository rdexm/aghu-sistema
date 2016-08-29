package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioContasPreenchimentoLaudosController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1001621489203836551L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioContasPreenchimentoLaudosController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AghUnidadesFuncionais unidadeFuncional;

	private Date dtPrevia;

	private String iniciaisPaciente;

	private Boolean gerouArquivo;

	private String fileName;

	@Inject
	private RelatorioContasPreenchimentoLaudosPdfController reportController;

	public enum RelatorioContasPreenchimentoLaudosControllerExceptionCode implements BusinessExceptionCode {
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
						RelatorioContasPreenchimentoLaudosControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
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
				return "relatorioContasPreenchimentoLaudosPdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioContasPreenchimentoLaudosControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void inicializaReportController(boolean isDirectPrint) {

		reportController.setIsDirectPrint(isDirectPrint);
		reportController.setDtPrevia(dtPrevia);
		if (unidadeFuncional != null) {
			reportController.setUnfSeq(unidadeFuncional.getSeq());
		} else {
			reportController.setUnfSeq(null);
		}
		reportController.setIniciaisPaciente(iniciaisPaciente);

		reportController.inicio();

	}

	public void gerarCSV() {
		try {
			final Short unfSeq = unidadeFuncional != null ? unidadeFuncional.getSeq() : null;
			fileName = faturamentoFacade.geraCSVRelatorioContasPreenchimentoLaudos(dtPrevia, unfSeq, iniciaisPaciente);
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
		dtPrevia = null;
		fileName = null;
		iniciaisPaciente = null;
		unidadeFuncional = null;
		gerouArquivo = false;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(strPesquisa),pesquisarUnidadeFuncionalCount(strPesquisa));
	}

	public Long pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacaoCount(strPesquisa);
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

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDtPrevia() {
		return dtPrevia;
	}

	public void setDtPrevia(Date dtPrevia) {
		this.dtPrevia = dtPrevia;
	}
}