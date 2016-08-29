package br.gov.mec.aghu.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghLogAplicacaoDAO;
import br.gov.mec.aghu.model.AghLogAplicacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AghLogAplicacaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AghLogAplicacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghLogAplicacaoDAO aghLogAplicacaoDAO;

	private static final long serialVersionUID = -3283385863100664121L;

	/**
	 * Quantidade de dias que servirá de corte na remoção de dados da tabela
	 * AGH_LOG_APLICACAO.
	 */
	private static final Integer LIMITE_DIAS_APAGAR_LOG_APLICACAO = 3;

	public void removerLogsAplicacao() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -LIMITE_DIAS_APAGAR_LOG_APLICACAO);

		AghLogAplicacaoDAO aghLogAplicacaoDAO = this.getAghLogAplicacaoDAO();

		aghLogAplicacaoDAO.removerLogsAplicacaoAntesData(DateUtil
				.truncaData(cal.getTime()));
		aghLogAplicacaoDAO.flush();
	}

	/**
	 * Busca log da aplicação de acordo com o filtro informado.
	 * 
	 * @param usuario
	 * @param dthrCriacaoIni
	 * @param dthrCriacaoFim
	 * @param classe
	 * @param nivel
	 * @param mensagem
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<AghLogAplicacao> pesquisarAghLogAplicacao(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem, Integer firstResult,
			Integer maxResult) {

		AghLogAplicacaoDAO aghLogAplicacaoDAO = this.getAghLogAplicacaoDAO();

		return aghLogAplicacaoDAO.pesquisarAghLogAplicacao(usuario,
				dthrCriacaoIni, dthrCriacaoFim, classe, nivel, mensagem,
				firstResult, maxResult);
	}

	/**
	 * Busca número de registros de log da aplicação de acordo com o filtro
	 * informado.
	 * 
	 * @param usuario
	 * @param dthrCriacaoIni
	 * @param dthrCriacaoFim
	 * @param classe
	 * @param nivel
	 * @param mensagem
	 * @return
	 */
	public Long pesquisarAghLogAplicacaoCount(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem) {

		AghLogAplicacaoDAO aghLogAplicacaoDAO = this.getAghLogAplicacaoDAO();

		return aghLogAplicacaoDAO.pesquisarAghLogAplicacaoCount(usuario,
				dthrCriacaoIni, dthrCriacaoFim, classe, nivel, mensagem);
	}

	protected AghLogAplicacaoDAO getAghLogAplicacaoDAO() {
		return aghLogAplicacaoDAO;
	}

}
