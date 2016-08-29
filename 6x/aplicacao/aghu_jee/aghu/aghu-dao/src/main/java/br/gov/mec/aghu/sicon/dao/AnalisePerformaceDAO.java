package br.gov.mec.aghu.sicon.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AnalisePerformaceDAO extends BaseDao<AinMovimentosInternacao> {

	private static final long serialVersionUID = -9109613555360476823L;
	
	private static final Log LOG = LogFactory.getLog(AnalisePerformaceDAO.class);

	public List<AinMovimentosInternacao> executarPrimeiraConsulta() {
		Calendar c = Calendar.getInstance();
		c.set(2013, 05, 01, 0, 0, 0);

		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.gt(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), c.getTime()));		
		return this.executeCriteria(criteria, 0, 10000, null);
	}

	public AinMovimentosInternacao executarSegundaConsulta(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.SEQ.toString(), seq));
		return (AinMovimentosInternacao) this.executeCriteriaUniqueResult(criteria);
	}

	public void executarProcedimentoC3() {
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.TESTE_PERFORMACE_C3;

		AghWork work = new AghWork("rmalvezzi") {
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					cs = connection.prepareCall("{call " + nomeObjeto + "()}");
					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};

		try {
			this.doWork(work);
		} catch (final Exception e) {
			LOG.error("Erro na chamada");
		}
	}
}
