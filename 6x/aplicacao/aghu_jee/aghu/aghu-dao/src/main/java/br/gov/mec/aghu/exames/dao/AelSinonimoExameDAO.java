package br.gov.mec.aghu.exames.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSinonimoExameId;

public class AelSinonimoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSinonimoExame> {

	private static final long serialVersionUID = -8007992758485517503L;

	@Override
	protected void obterValorSequencialId(AelSinonimoExame elemento) {
		// TODO Auto-generated method stub

		if (elemento == null || elemento.getId() == null || elemento.getId().getExaSigla() == null) {
			throw new IllegalArgumentException("sigla do exame não foi informada corretamente.");
		}

		Short seqp = buscaMaxSeqSinonimoExame(elemento.getId().getExaSigla());
		elemento.getId().setSeqp(seqp);
	}
	
	/**
	 * Busca o maior sequencial associado a MpmServRecomendacaoAlta
	 * @param id
	 * @return
	 */
	private Short buscaMaxSeqSinonimoExame(String sigla) {
		Short returnValue = null;

		StringBuilder sql = new StringBuilder(100);
		sql.append("select max(sinonimo.id.seqp)+1 as maxSeq ");
		sql.append("from ").append(AelSinonimoExame.class.getName()).append(" sinonimo");
		sql.append(" where sinonimo.").append(AelSinonimoExame.Fields.EXA_SIGLA).append(" = :sigla ");

		Query query = createQuery(sql.toString());
		query.setParameter("sigla", sigla);

		Object maxSeq = query.getSingleResult();

		if (maxSeq == null) {
			returnValue = Short.valueOf("1");
		} else {
			returnValue = ((Integer)maxSeq).shortValue(); 
		}

		return returnValue;
	}

	public AelSinonimoExame buscarSinonimoPrincipalPorAelExames(AelExames aelExames) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.SEQ_P.toString(), Short.valueOf("1")));

		return (AelSinonimoExame)executeCriteriaUniqueResult(criteria);
		
	}

	/**
	 * Retorna AelSinonimoExame pelo seu Id
	 * @param id
	 * @return
	 */
	public AelSinonimoExame obterAelSinonimoExamePorId(AelSinonimoExameId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.EXA_SIGLA.toString(), id.getExaSigla()));
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.SEQ_P.toString(), id.getSeqp()));

		return (AelSinonimoExame)executeCriteriaUniqueResult(criteria);
		
	}
	
	public AelSinonimoExame validaUnicoSinonimoInserir(AelSinonimoExame sinonimo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.NOME.toString(), sinonimo.getNome()));

		return (AelSinonimoExame)executeCriteriaUniqueResult(criteria);
	}
	
	
	public AelSinonimoExame validaUnicoSinonimoAtualizar(AelSinonimoExame sinonimo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.NOME.toString(), sinonimo.getNome()));
		criteria.add(Restrictions.or(Restrictions.ne(AelSinonimoExame.Fields.EXA_SIGLA.toString(), sinonimo.getId().getExaSigla()), Restrictions.ne(AelSinonimoExame.Fields.SEQ_P.toString(), sinonimo.getId().getSeqp())));
		return (AelSinonimoExame)executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisarSinonimosExamesCount(AelExames aelExames) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));

		return executeCriteriaCount(criteria);
	}
	
	public List<AelSinonimoExame> pesquisarSinonimosExames(AelExames aelExames, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<AelSinonimoExame> pesquisarSinonimosExames(AelExames aelExames){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoExame.class);
		criteria.add(Restrictions.eq(AelSinonimoExame.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));

		return executeCriteria(criteria);
	}

}