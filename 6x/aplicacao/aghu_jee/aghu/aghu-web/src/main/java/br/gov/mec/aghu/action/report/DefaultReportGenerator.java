package br.gov.mec.aghu.action.report;

import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.Dependent;

import br.gov.mec.aghu.report.AghuReportGenerator;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Report Generator a ser usado em conjunto com o action report na migração do
 * código legado de forma a diminuir o refactor necessário.
 * 
 * Nos métodos de obtenção de dados específicos de cada relatório, delega para a
 * ActionReport no qual foi injetado.
 * 
 * @author geraldo
 * 
 */
@Dependent
public class DefaultReportGenerator extends AghuReportGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5166695954758387962L;

	public void setActionReport(ActionReport actionReport) {
		this.actionReport = actionReport;
	}

	private ActionReport actionReport;

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return actionReport.recuperarColecao();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return actionReport.recuperarArquivoRelatorio();
	}

	@Override
	protected Map<String, Object> recuperarParametros() {
		return actionReport.recuperarParametros();
	}

	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected String recuperarCaminhoLogo() throws ApplicationBusinessException {
		return super.recuperarCaminhoLogo();
	}

	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected String recuperarCaminhoLogo2()
			throws ApplicationBusinessException {
		return super.recuperarCaminhoLogo2();
	}

	/**
	 * Método que retorna a entidade pai do relatório, a ser usada na geração da
	 * pendência de assinatura. Deve ser sobrescrito nas controllers do
	 * relatórios que vão gerar pendência de assinatura.
	 * 
	 * @return
	 */
	protected BaseBean getEntidadePai() {
		return actionReport.getEntidadePai();
	}

}
