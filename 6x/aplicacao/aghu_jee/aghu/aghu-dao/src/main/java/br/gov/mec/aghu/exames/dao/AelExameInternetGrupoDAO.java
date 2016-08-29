package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameInternetGrupo;
import br.gov.mec.aghu.model.AelExameInternetGrupoArea;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public class AelExameInternetGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameInternetGrupo> {
	
	private static final long serialVersionUID = 3257162530007732360L;

	private DetachedCriteria obterCriteria() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameInternetGrupo.class);
		return criteria;
    }

    
	public AelExameInternetGrupo buscarExameInterGrupo(Integer seqExameInternetGrupo){
		
		final DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExameInternetGrupo.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelExameInternetGrupo.Fields.SEQ.toString(), seqExameInternetGrupo));
		
		return (AelExameInternetGrupo)executeCriteriaUniqueResult(criteria);
		
	}
	
	public AelExameInternetGrupo buscarExameInternetGrupoArea(Integer soeSeq, Short seqp) {
		
		final StringBuilder hql = new StringBuilder(150);
		
		hql.append("select gei");
		hql.append(" from ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ");
		hql.append(", ").append(AelExameInternetGrupoArea.class.getSimpleName()).append(" ggu ");
		hql.append(", ").append(AelExameInternetGrupo.class.getSimpleName()).append(" gei ");
		hql.append(" where ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq ");
		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append("  = :seqp ");
		hql.append(" and ggu.").append(AelExameInternetGrupoArea.Fields.UNIDADE_FUNCIONAL_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
		hql.append(" and ggu.").append(AelExameInternetGrupoArea.Fields.EXAME_INTERNET_GRUPO_SEQ.toString());
		hql.append(" = ");
		hql.append(" gei.").append(AelExameInternetGrupo.Fields.SEQ.toString());
		
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("seqp", seqp);		
        				
		return (AelExameInternetGrupo) query.uniqueResult();
	}
	
}
