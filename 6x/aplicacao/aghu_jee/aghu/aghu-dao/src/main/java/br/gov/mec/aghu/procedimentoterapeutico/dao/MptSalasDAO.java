package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptSalas;

public class MptSalasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptSalas> {

	private static final long serialVersionUID = 6082584819984406090L;

	public List<MptSalas> listarMptSalas(Short tpsSeq, Short espSeq, String descricao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaListarMptSalas(tpsSeq, espSeq, descricao);
		criteria.addOrder(Order.asc(MptSalas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}
	
	public Long listarMptSalasCount(Short tpsSeq, Short espSeq, String descricao) {
		DetachedCriteria criteria = obterCriteriaListarMptSalas(tpsSeq, espSeq, descricao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarMptSalas(Short tpsSeq, Short espSeq, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class, "SA");
		criteria.createAlias("SA." + MptSalas.Fields.TIPO_SESSAO.toString(), "TS");
		
		if (descricao != null) {
			criteria.add(Restrictions.ilike("SA." + MptSalas.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (tpsSeq != null) {
			criteria.add(Restrictions.eq("SA." + MptSalas.Fields.TPS_SEQ.toString(), tpsSeq));
		}
		if (espSeq != null) {
			criteria.add(Restrictions.eq("SA." + MptSalas.Fields.ESP_SEQ.toString(), espSeq));
		}
		
		return criteria;
	}

	public List<MptSalas> listarSalas(Short tpsSeq, String param) {
		DetachedCriteria criteria = obterCriteriaListarSalas(tpsSeq, param);
		criteria.addOrder(Order.asc(MptSalas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarSalasCount(Short tpsSeq, String param) {
		DetachedCriteria criteria = obterCriteriaListarSalas(tpsSeq, param);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarSalas(Short tpsSeq, String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);
		
		if (StringUtils.isNumeric(param)) {
			criteria.add(Restrictions.eq(MptSalas.Fields.SEQ.toString(), Short.valueOf(param)));
		} else if (!StringUtils.isEmpty(param)) {
			criteria.add(Restrictions.ilike(MptSalas.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MptSalas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptSalas.Fields.TPS_SEQ.toString(), tpsSeq));
		return criteria;
	}

	public MptSalas obterMptSalaPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class, "SA");
		criteria.createAlias("SA." + MptSalas.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SA." + MptSalas.Fields.TIPO_SESSAO.toString(), "TS");
		criteria.createAlias("SA." + MptSalas.Fields.ESPECIALIDADE.toString(), "ES");
		criteria.add(Restrictions.eq(MptSalas.Fields.SEQ.toString(), seq));
		return (MptSalas) executeCriteriaUniqueResult(criteria);
	}
	
	
		

	/**
	 * Carrega o ComboBox Sala. (C3)
	 * @param parametro
	 * @param codigoTipoSessao
	 * @return List<MptSalas>
	 */
	public List<MptSalas> buscarSala(Short codigoTipoSessao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class, "SAL");
		
		criteria.add(Restrictions.eq("SAL."+MptSalas.Fields.TPS_SEQ.toString(), codigoTipoSessao));
		
		criteria.addOrder(Order.asc("SAL."+MptSalas.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
		
	//C2 #44466 - Monta o criteria
	private DetachedCriteria criteriaObterSalaPorTipoSessao(Short seqTipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);
		criteria.add(Restrictions.and(
				Restrictions.eq(MptSalas.Fields.TPS_SEQ.toString(), seqTipoSessao), 
				Restrictions.eq(MptSalas.Fields.IND_SITUACAO.toString(), DominioSituacao.A)));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptSalas.Fields.DESCRICAO.toString())
						.as(MptSalas.Fields.DESCRICAO.toString()))
				.add(Projections.property(MptSalas.Fields.SEQ.toString())
						.as(MptSalas.Fields.SEQ.toString())));
		
		criteria.addOrder(Order.asc(MptSalas.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptSalas.class));
		return criteria;
	}
	/**C2 #44466 - Retorna lista com a descricao das salas por tipo de sessão **/
	public List<MptSalas> obterSalaPorTipoSessao(Short seqTipoSessao){
		DetachedCriteria criteria = criteriaObterSalaPorTipoSessao(seqTipoSessao);
		return executeCriteria(criteria);
	}
	
	public MptSalas obterSalaPorSeqAtiva(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);
		criteria.add(Restrictions.eq(MptSalas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptSalas.Fields.SEQ.toString(), seq));
		
		return (MptSalas) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44249 C3
	 * Consulta para a Salas
	 * @param tpsSeq
	 * @return
	 */
	public List<MptSalas> obterListaSalasPorTpsSeq(Short tpsSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);

		criteria.add(Restrictions.eq(MptSalas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptSalas.Fields.TPS_SEQ.toString(), tpsSeq));
		
		criteria.addOrder(Order.asc(MptSalas.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #46834 - C4
	 * @param tpsSeq
	 * @return List<MptSalas>
	 */
	public List<MptSalas> obterListaSalasPorTipoSessao(Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptSalas.Fields.SEQ.toString()), MptSalas.Fields.SEQ.toString())
				.add(Projections.property(MptSalas.Fields.DESCRICAO.toString()), MptSalas.Fields.DESCRICAO.toString()));						

		criteria.add(Restrictions.eq(MptSalas.Fields.TPSSEQ.toString(), tpsSeq));
		
		criteria.addOrder(Order.asc(MptSalas.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptSalas.class));
		
		return executeCriteria(criteria);
	}
}