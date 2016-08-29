package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatExcecaoPercentual;

public class FatExcecaoPercentualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatExcecaoPercentual> {

	private static final long serialVersionUID = -7242187455471186241L;

	protected DetachedCriteria obterCriteriaPorIph(final Short iphPhoSeq,
			final Integer iphSeq) {
		
		DetachedCriteria result = DetachedCriteria.forClass(FatExcecaoPercentual.class);
		result.add(Restrictions.eq(FatExcecaoPercentual.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatExcecaoPercentual.Fields.IPH_SEQ.toString(), iphSeq));
		
		return result;
	}
	
	public List<FatExcecaoPercentual> obterListaPorIph(final Short iphPhoSeq,final Integer iphSeq) {
		final DetachedCriteria criteria = this.obterCriteriaPorIph(iphPhoSeq, iphSeq);
		return this.executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public FatExcecaoPercentual buscarExcecoesPercentuais(Byte sequencia, Integer cthSeq, Integer seqp) {
		StringBuilder hql = new StringBuilder(150);
		hql.append(" select epe from ");
		hql.append(FatExcecaoPercentual.class.getSimpleName()).append(" epe, ");
		hql.append(FatEspelhoAih.class.getSimpleName()).append(" eai ");
		hql.append(" where epe.");
		hql.append(FatExcecaoPercentual.Fields.IPH_PHO_SEQ.toString());
		hql.append(" = eai.");
		hql.append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.toString());
		hql.append(" and epe.");
		hql.append(FatExcecaoPercentual.Fields.IPH_SEQ.toString());
		hql.append(" = eai.");
		hql.append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.toString());
		hql.append(" and epe.");
		hql.append(FatExcecaoPercentual.Fields.SEQP.toString());
		hql.append(" = :sequencia ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");

		Query query = createQuery(hql.toString());
		query.setParameter("sequencia", sequencia);
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("seqp", seqp);
		query.setMaxResults(1);

		List<FatExcecaoPercentual> list = query.getResultList();
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 2132
	 */
	public List<FatExcecaoPercentual> listarExcecaoPercentual(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FatExcecaoPercentual fatExcecaoPercentual) {
		final DetachedCriteria criteria = this.obterCriteriaPorIph(fatExcecaoPercentual.getId().getIphPhoSeq(), fatExcecaoPercentual.getId().getIphSeq());
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarExcecaoPercentualCount(FatExcecaoPercentual fatExcecaoPercentual) {
		DetachedCriteria criteria = this.obterCriteriaPorIph(fatExcecaoPercentual.getId().getIphPhoSeq(),fatExcecaoPercentual.getId().getIphSeq());
		return this.executeCriteriaCount(criteria);
	}
	
	public void excluirExcecaoPercentual(FatExcecaoPercentual fatExcecaoPercentual) {
		DetachedCriteria criteria = obterCriteriaPorSeqP(fatExcecaoPercentual.getId().getIphPhoSeq(), fatExcecaoPercentual.getId().getIphSeq(), fatExcecaoPercentual.getId().getSeqp());
		FatExcecaoPercentual excecaoPercentual = (FatExcecaoPercentual) this.executeCriteriaUniqueResult(criteria);
		
		this.remover(excecaoPercentual);
		flush();
	}

	public void alterarExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) {
		this.atualizar(excecaoPercentual);
		flush();
	}
	
	protected DetachedCriteria obterCriteriaPorSeqP(final Short iphPhoSeq, final Integer iphSeq, final Byte seqp) {
		
		DetachedCriteria result = DetachedCriteria.forClass(FatExcecaoPercentual.class);
		result.add(Restrictions.eq(FatExcecaoPercentual.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatExcecaoPercentual.Fields.IPH_SEQ.toString(), iphSeq));
		result.add(Restrictions.eq(FatExcecaoPercentual.Fields.SEQP.toString(), seqp));
		
		return result;
	}
}
