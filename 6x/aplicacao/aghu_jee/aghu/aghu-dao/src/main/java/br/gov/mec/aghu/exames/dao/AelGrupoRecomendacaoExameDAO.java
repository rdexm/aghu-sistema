package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExameId;

public class AelGrupoRecomendacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoRecomendacaoExame> {

	private static final long serialVersionUID = -3826280658425401746L;

	
	public List<AelGrupoRecomendacaoExame> obterAelGrupoRecomendacaoExamesPorAelGrupoRecomendacao(AelGrupoRecomendacao grupo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoRecomendacaoExame.class);
		
		criteria.add(Restrictions.eq(AelGrupoRecomendacaoExame.Fields.GRUPO_RECOMENDACAO.toString(), grupo));
		criteria.createAlias(AelGrupoRecomendacaoExame.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias(AelGrupoRecomendacaoExame.Fields.GRUPO_RECOMENDACAO.toString(), "GR", JoinType.INNER_JOIN);
		criteria.createAlias(AelGrupoRecomendacaoExame.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.INNER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "EMA_EX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}
	
	@Override
	protected void obterValorSequencialId(AelGrupoRecomendacaoExame elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		if (elemento.getGrupoRecomendacao() == null || elemento.getExameMaterialAnalise() == null) {
			throw new IllegalArgumentException("Associacoes (AelGrupoRecomendacao, AelExamesMaterialAnalise) nao estao corretamente informadas!!");
		}
		
		AelGrupoRecomendacaoExameId id = new AelGrupoRecomendacaoExameId();
		id.setGrmSeq(elemento.getGrupoRecomendacao().getSeq());
		id.setEmaExaSigla(elemento.getExameMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExameMaterialAnalise().getId().getManSeq());
		
		elemento.setId(id);
	}

	/*public AelGrupoRecomendacaoExame obterOriginal(AelGrupoRecomendacaoExame entity) {
		if (entity == null || entity.getId() == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelGrupoRecomendacaoExame.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelGrupoRecomendacaoExame.Fields.GRUPO_RECOMENDACAO.toString());
		hql.append(", o.").append(AelGrupoRecomendacaoExame.Fields.ID.toString());
		hql.append(", o.").append(AelGrupoRecomendacaoExame.Fields.SERVIDOR.toString());
		hql.append(" from ").append(AelGrupoRecomendacaoExame.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelGrupoRecomendacaoExame.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", entity.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		AelGrupoRecomendacaoExame returnValue = null; 
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AelGrupoRecomendacaoExame();
			for (Object[] listFileds : lista) {
				returnValue.setCriadoEm( (Date) listFileds[0]);
				returnValue.setGrupoRecomendacao( (AelGrupoRecomendacao) listFileds[1]);
				returnValue.setId( (AelGrupoRecomendacaoExameId) listFileds[2]);
				returnValue.setServidor( (RapServidores) listFileds[3]);
			}
		}		
		
		return returnValue;
	}*/

	public List<AelGrupoRecomendacaoExame> getGrupoRecomendacaoExames(AelGrupoRecomendacao entity) {
		if (entity == null || entity.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoRecomendacaoExame.class);
		criteria.add(Restrictions.eq(AelGrupoRecomendacaoExame.Fields.GRM_SEQ.toString(), entity.getSeq()));
		
		return executeCriteria(criteria);
	}
	
	

}
