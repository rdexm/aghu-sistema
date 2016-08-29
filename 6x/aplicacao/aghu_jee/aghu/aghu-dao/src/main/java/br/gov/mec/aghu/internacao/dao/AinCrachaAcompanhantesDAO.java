package br.gov.mec.aghu.internacao.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinCrachaAcompanhantes;

public class AinCrachaAcompanhantesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinCrachaAcompanhantes> {

	private static final long serialVersionUID = -6199928645476415066L;
	
	private static final Log LOG = LogFactory.getLog(AinCrachaAcompanhantesDAO.class);

	/**
	 * Retorna a criteria de recuperação de
	 * <code>AinAcompanhantesInternacao</code>.
	 * 
	 * @param codigo
	 *            do
	 * @return o DetachedCriteria para ser usado em outros métodos
	 */
	private DetachedCriteria criarCriteriaAinCrachaAcompanhantes(AinAcompanhantesInternacao acompanhanteInternacao) {

		if (acompanhanteInternacao == null || acompanhanteInternacao.getId() == null) {
			// Nunca deveria ser nulo.
			throw new IllegalArgumentException("Acompanhante da Internação não informado.");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AinCrachaAcompanhantes.class);
		criteria.add(Restrictions.eq(AinCrachaAcompanhantes.Fields.ACOMPANHANTE_SEQ.toString(), acompanhanteInternacao.getId()
				.getSeq()));
		
		criteria.add(Restrictions.eq(AinCrachaAcompanhantes.Fields.INT_SEQ.toString(), acompanhanteInternacao.getId()
				.getIntSeq()));

		return criteria;
	}

	public List<AinCrachaAcompanhantes> pesquisarAinCrachaAcompanhantes(AinAcompanhantesInternacao acompanhanteInternacao) {
		DetachedCriteria criteria = this.criarCriteriaAinCrachaAcompanhantes(acompanhanteInternacao);
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem sequence da tabela
	 * 
	 * @param crachaAcompanhante
	 */
	public Byte obterValorSeqId(AinCrachaAcompanhantes crachaAcompanhante, Byte aciSeq, Integer aciIntSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinCrachaAcompanhantes.class);

		if (crachaAcompanhante.getId() != null && aciSeq != null && aciIntSeq != null) {
			criteria.add(Restrictions.eq(AinCrachaAcompanhantes.Fields.INT_SEQ.toString(), aciIntSeq));
			criteria.add(Restrictions.eq(AinCrachaAcompanhantes.Fields.ACOMPANHANTE_SEQ.toString(), aciSeq));
			criteria.setProjection(Projections.max(AinCrachaAcompanhantes.Fields.SEQ.toString()));
		} else {
			return (byte) 1;
		}

		Byte obj = (Byte) executeCriteriaUniqueResult(criteria);

		if (obj == null) {
			return (byte) 1;
		}

		return (byte) (obj.intValue() + 1);
	}

	public Long sugereNumeroDoCracha(final String usuarioLogado) {
		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RAPC_BUSCA_CRACHA_LIVRE_RONDA;
		//
		final List<Long> result = new ArrayList<Long>();
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					StringBuilder call = new StringBuilder("{? = call ");
					call.append(storedProcedureName);
					call.append("()}");

					statement = connection.prepareCall(call.toString());

					statement.registerOutParameter(1,
							java.sql.Types.NUMERIC);

					statement.execute();

					Long resultado = statement.getLong(1);

					result.add(resultado);

				} catch (Exception e) {
					LOG.error(String.format(
							"Erro ao gerar nÃºmero do crachÃ¡!",
							usuarioLogado, e.getMessage()), e);
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};

		try {
			doWork(work);			
		} catch (Exception e) {
			LOG.error(String.format("Erro ao gerar nÃºmero do crachÃ¡!",
					usuarioLogado, e.getMessage()), e);
			return null;
		}
		
		if (work.getException() != null){
			LOG.error(String.format("Erro ao gerar nÃºmero do crachÃ¡!",
					usuarioLogado, work.getException().getMessage()), work.getException());
			return null;
		}
		
		return result.get(0);
	}
	
	
	
	public void alterarPermissoes(final Long numCra,
			final Integer permissaoAcesso, String usuarioLogado){
		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RAPP_ATU_CONTROLE_RONDA;
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					StringBuilder call = new StringBuilder("{call ");
					call.append(storedProcedureName);
					call.append("(?,?)}");

					statement = connection.prepareCall(call.toString());

					statement.setInt(1, permissaoAcesso);

					statement.setLong(2, numCra);

					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
		
		try {
			doWork(work);
		} catch (Exception e) {
			LOG.error(
					String.format("Erro nas permissÃµes!", usuarioLogado,
							e.getMessage()), e);
			return;
		}
		
		if (work.getException() != null){
			LOG.error(
					String.format("Erro nas permissÃµes!", usuarioLogado,
							work.getException().getMessage()), work.getException());
			return;
		}
	}
	
}
