package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghLogAplicacao;

public class AghLogAplicacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghLogAplicacao> {

	private static final long serialVersionUID = -2871348395188255576L;

	public void removerLogsAplicacaoAntesData(Date dataCorte) {
		StringBuffer hql = new StringBuffer(32);
		hql.append("delete from ");
		hql.append(AghLogAplicacao.class.getName());

		if (dataCorte != null) {
			hql.append(" where ");
			hql.append(AghLogAplicacao.Fields.DTHR_CRIACAO.toString());
			hql.append(" < :dataCorte ");

		}

		Query query = createHibernateQuery(hql.toString());

		if (dataCorte != null) {
			query.setParameter("dataCorte", dataCorte);
		}

		query.executeUpdate();
	}

	/**
	 * Cria criteria que busca log da aplicação de acordo com o filtro
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
	private DetachedCriteria criteriaPesquisarAghLogAplicacao(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghLogAplicacao.class);

		if (usuario != null) {
			criteria.add(Restrictions.eq(
					AghLogAplicacao.Fields.USUARIO.toString(), usuario));
		}

		if (dthrCriacaoIni != null) {
			criteria.add(Restrictions.ge(
					AghLogAplicacao.Fields.DTHR_CRIACAO.toString(),
					dthrCriacaoIni));
		}

		if (dthrCriacaoFim != null) {
			criteria.add(Restrictions.le(
					AghLogAplicacao.Fields.DTHR_CRIACAO.toString(),
					dthrCriacaoFim));
		}

		if (classe != null) {
			criteria.add(Restrictions.ilike(
					AghLogAplicacao.Fields.CLASSE.toString(), classe,
					MatchMode.ANYWHERE));
		}

		if (nivel != null) {
			criteria.add(Restrictions.eq(
					AghLogAplicacao.Fields.NIVEL.toString(), nivel));
		}

		if (mensagem != null) {
			criteria.add(Restrictions.ilike(
					AghLogAplicacao.Fields.MENSAGEM.toString(), mensagem,
					MatchMode.ANYWHERE));
		}

		return criteria;
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

		DetachedCriteria criteria = this.criteriaPesquisarAghLogAplicacao(
				usuario, dthrCriacaoIni, dthrCriacaoFim, classe, nivel,
				mensagem);

		criteria.addOrder(Order.asc(AghLogAplicacao.Fields.DTHR_CRIACAO
				.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
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

		DetachedCriteria criteria = this.criteriaPesquisarAghLogAplicacao(
				usuario, dthrCriacaoIni, dthrCriacaoFim, classe, nivel,
				mensagem);

		return executeCriteriaCount(criteria);
	}

}
