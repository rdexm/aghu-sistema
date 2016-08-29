package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoLembrete;
import br.gov.mec.aghu.model.MamLembrete;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_LEMBRETE.
 * 
 */
public class MamLembreteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamLembrete> {

	private static final long serialVersionUID = 1786271252585879887L;
	
	public List<MamLembrete> obterLembretePorNumeroConsulta(final Integer numero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLembrete.class, "LEM1");		
		criteria.add(Restrictions.eq("LEM1."+MamLembrete.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull("LEM1."+MamLembrete.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.eq("LEM1."+MamLembrete.Fields.IND_PENDENTE.toString(), DominioSituacaoLembrete.V));
		
		//subcriteria	
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamLembrete.class, "LEM2");		
		subCriteria.setProjection(Projections.property("LEM2."+MamLembrete.Fields.SEQ.toString()));		
		subCriteria.add(Restrictions.eqProperty("LEM1." + MamLembrete.Fields.LEM_SEQ.toString(), "LEM2."+ MamLembrete.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.in("LEM2."+MamLembrete.Fields.IND_PENDENTE.toString(), new DominioSituacaoLembrete[] { DominioSituacaoLembrete.P, DominioSituacaoLembrete.V, DominioSituacaoLembrete.E, DominioSituacaoLembrete.A}));
		
		criteria.add(Subqueries.notExists(subCriteria));
		return executeCriteria(criteria);	
	}
	
	public List<MamLembrete> obterLembretes(Integer numero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLembrete.class);
		criteria.add(Restrictions.eq(MamLembrete.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.in(MamLembrete.Fields.IND_PENDENTE.toString(), new DominioSituacaoLembrete[] { DominioSituacaoLembrete.P, DominioSituacaoLembrete.E}));
		criteria.add(Restrictions.isNull(MamLembrete.Fields.DTHR_VALIDA_MVTO.toString()));
		
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamLembrete buscarLembretePorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLembrete.class);

		criteria.add(Restrictions.eq(MamLembrete.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamLembrete> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * #11893
	 * C1 - Consulta principal de seleção dos registros de resumo de caso que ocorre ao abrir a tela. 
	 */
	public List<MamLembrete> obterResumoDeCaso(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLembrete.class, "LEM");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("LEM." + MamLembrete.Fields.DESCRICAO.toString()),MamLembrete.Fields.DESCRICAO.toString())); 
		
		criteria.add(Restrictions.eq("LEM." + MamLembrete.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.in("LEM." + MamLembrete.Fields.IND_PENDENTE.toString(), new DominioSituacaoLembrete[] { DominioSituacaoLembrete.V, DominioSituacaoLembrete.P}));
		criteria.add(Restrictions.isNull("LEM." + MamLembrete.Fields.DTHR_VALIDA_MVTO.toString()));
		StringBuilder subQuery = new StringBuilder(500);
		subQuery.append("  coalesce(this_.LEM_SEQ,0) NOT IN ");
		subQuery.append(" ( SELECT lem1.lem_seq FROM AGH.mam_lembretes lem1 WHERE lem1.lem_seq = this_.seq AND lem1.ind_pendente IN ('P','V','E','A','R') ) ");
		
		criteria.add(Restrictions.sqlRestriction(subQuery.toString()));

		criteria.addOrder(Order.desc("LEM."+ MamLembrete.Fields.DTHR_CRIACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamLembrete.class));
		
		return executeCriteria(criteria);	
	}
	
}
