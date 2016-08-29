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
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptTipoJustificativa;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.vo.JustificativaVO;

public class MptJustificativaDAO extends BaseDao<MptJustificativa>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5462123123121231321L;
	
	private static final String JUS = "JUS.";
	
	private static final String TPJ = "TPJ.";
	
	private static final String TP = "TPJ";
	
	private static final String JU = "JUS";
	
	/**
	 * 
	 */
	
	private DetachedCriteria montarQueryJustificativa(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class);
		
		criteria.createAlias(MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP);
		if (StringUtils.isNotBlank(filtro) && StringUtils.isNumeric(filtro)) {
			criteria.add(Restrictions.eq(MptJustificativa.Fields.SEQ.toString(), Short.valueOf(filtro)));
		} 
		if(StringUtils.isNotBlank(filtro) && !StringUtils.isNumeric(filtro)){
			criteria.add(Restrictions.ilike(MptJustificativa.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(TPJ+MptTipoJustificativa.Fields.SIGLA.toString(), "BLQ"));
			
		return criteria;
	}
	
	public List<MptJustificativa> pesquisarJustificativa(String filtro){
		DetachedCriteria criteria = montarQueryJustificativa(filtro);
		
		criteria.addOrder(Order.asc(MptJustificativa.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarJustificativaCount(String filtro){
		DetachedCriteria criteria = montarQueryJustificativa(filtro);
		
		return executeCriteriaCount(criteria);
		
	}
	
	private DetachedCriteria montarQueryMotivoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class);
		criteria.createAlias(MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP);
		if (StringUtils.isNotBlank(filtro)) {
			criteria.add(Restrictions.ilike(MptJustificativa.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MptJustificativa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(TPJ+MptTipoJustificativa.Fields.SIGLA.toString(), "CAN"));
		return criteria;
	}

	public List<MptJustificativa> pesquisarMotivoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		DetachedCriteria criteria = montarQueryMotivoManutencaoAgendamentoSessaoTerapeutica(filtro);
		criteria.addOrder(Order.asc(MptJustificativa.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarMotivoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro) {
		DetachedCriteria criteria = montarQueryMotivoManutencaoAgendamentoSessaoTerapeutica(filtro);
		return executeCriteriaCount(criteria);

	}

	/**
	 * 44249 C5 SB4
	 * Consulta para a SuggestionBox Justificativas
	 * @param descricao
	 * @return
	 */
	public List<MptJustificativa> obterListaJustificativaSB(String descricao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(JUS+MptJustificativa.Fields.SEQ.toString()).as(MptJustificativa.Fields.SEQ.toString()))
				.add(Projections.property(JUS+MptJustificativa.Fields.DESCRICAO.toString()).as(MptJustificativa.Fields.DESCRICAO.toString()))
				);
		criteria.setResultTransformer(Transformers.aliasToBean(MptJustificativa.class));
		criteria = obterFiltroListaJustificativaSBCount(criteria, descricao);
		criteria.addOrder(Order.asc(MptJustificativa.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * 44249 C5 SB4
	 * Consulta para a SuggestionBox Justificativas Count
	 * @param descricao
	 * @return
	 */
	public Long obterListaJustificativaSBCount(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		criteria = obterFiltroListaJustificativaSBCount(criteria, descricao);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterFiltroListaJustificativaSBCount(DetachedCriteria criteria, String descricao){
		criteria.createAlias(JUS+MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP);
		criteria.add(Restrictions.eq(JUS+MptJustificativa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(TPJ+MptTipoJustificativa.Fields.SIGLA.toString(), "EXT"));
		if(StringUtils.isNotBlank(descricao)){
			if(CoreUtil.isNumeroShort(descricao)){
				criteria.add(Restrictions.or(Restrictions.eq(JUS+MptJustificativa.Fields.SEQ.toString(), Short.valueOf(descricao)),
						Restrictions.ilike(JUS+MptJustificativa.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE)));
			}else{
				criteria.add(Restrictions.ilike(JUS+MptJustificativa.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}	


	/**
	 * Obter as justificativas para suspensão
	 * #41706
	 * 
	 * @return
	 */
	public List<MptJustificativa> obterJustificativaParaSuspensao(Short tipoSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		criteria.createAlias(MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), "TP");

		criteria.setProjection(Projections.projectionList()
			.add(Projections.property(JUS + MptJustificativa.Fields.SEQ.toString()).as(MptJustificativa.Fields.SEQ.toString()))
			.add(Projections.property(JUS + MptJustificativa.Fields.DESCRICAO.toString()).as(MptJustificativa.Fields.DESCRICAO.toString()))
		);
		
		criteria.add(Restrictions.eq("TP." + MptTipoJustificativa.Fields.SIGLA.toString(), "SUS"));
		criteria.add(Restrictions.eq(JUS + MptJustificativa.Fields.TIPO_SESSAO_SEQ.toString(), tipoSessao));
		criteria.addOrder(Order.asc(JUS + MptJustificativa.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptJustificativa.class));
		
		return executeCriteria(criteria);
	}
	
	
	public List<JustificativaVO> obterJustificativas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MptJustificativa mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		montarQueryJustificativa(mptJustificativa, criteria);
		
		criteria.setResultTransformer(Transformers.aliasToBean(JustificativaVO.class));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarMptJustificativaCount(MptJustificativa mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		montarQueryJustificativa(mptJustificativa, criteria);
		return executeCriteriaCount(criteria);
	}

	private void montarQueryJustificativa(MptJustificativa mptJustificativa,
			DetachedCriteria criteria) {
		criteria.createAlias(JUS+MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP, JoinType.INNER_JOIN);
		criteria.createAlias(JUS+MptJustificativa.Fields.TPS_SEQ.toString(), "TPS", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(JUS + MptJustificativa.Fields.SEQ.toString()).as(JustificativaVO.Fields.SEQ.toString()))
				.add(Projections.property(JUS + MptJustificativa.Fields.DESCRICAO.toString()).as(JustificativaVO.Fields.DESCRICAO.toString()))
				.add(Projections.property(JUS + MptJustificativa.Fields.IND_SITUACAO.toString()).as(JustificativaVO.Fields.IND_SITUACAO.toString()))
				.add(Projections.property("TPS." + MptTipoSessao.Fields.SEQ.toString()).as(JustificativaVO.Fields.SEQ_TPS.toString()))
				.add(Projections.property("TPS." + MptTipoSessao.Fields.DESCRICAO.toString()).as(JustificativaVO.Fields.DESC_TIPO_SES.toString()))
				.add(Projections.property(TPJ + MptTipoJustificativa.Fields.SEQ.toString()).as(JustificativaVO.Fields.SEQ_TPJ.toString()))
				.add(Projections.property(TPJ + MptTipoJustificativa.Fields.DESCRICAO.toString()).as(JustificativaVO.Fields.DESC_TIPO_JUS.toString())));
		
		
		if(mptJustificativa.getIndSituacao() != null){
			criteria.add(Restrictions.eq(JUS+MptJustificativa.Fields.IND_SITUACAO.toString(), mptJustificativa.getIndSituacao()));
		}
		
		if(mptJustificativa.getDescricao() != null && StringUtils.isNotBlank(mptJustificativa.getDescricao())){
			criteria.add(Restrictions.ilike(JUS+MptJustificativa.Fields.DESCRICAO.toString(), mptJustificativa.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(mptJustificativa.getMptTipoSessao() != null){
			criteria.add(Restrictions.eq("TPS."+MptTipoSessao.Fields.SEQ.toString(), mptJustificativa.getMptTipoSessao().getSeq()));
		}
		
		if(mptJustificativa.getMptTipoJustificativa() != null){
			criteria.add(Restrictions.eq(TPJ+MptTipoJustificativa.Fields.SEQ.toString(), mptJustificativa.getMptTipoJustificativa().getSeq()));
		}
		criteria.addOrder(Order.asc(TPJ+MptTipoJustificativa.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(JUS+MptJustificativa.Fields.DESCRICAO.toString()));
	}
	
	public MptJustificativa obterMptJustificativa(MptJustificativa mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		criteria.createAlias(JUS+MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP, JoinType.INNER_JOIN);
		criteria.createAlias(JUS+MptJustificativa.Fields.TPS_SEQ.toString(), "TPS", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(JUS+MptJustificativa.Fields.SEQ.toString(), mptJustificativa.getSeq()));
		return (MptJustificativa) executeCriteriaUniqueResult(criteria);
	}
	
	public MptJustificativa obterMptJustificativaVO(JustificativaVO mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptJustificativa.class, JU);
		criteria.createAlias(JUS+MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), TP, JoinType.INNER_JOIN);
		criteria.createAlias(JUS+MptJustificativa.Fields.TPS_SEQ.toString(), "TPS", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(JUS+MptJustificativa.Fields.SEQ.toString(), mptJustificativa.getSeqJus()));
		return (MptJustificativa) executeCriteriaUniqueResult(criteria);
	}
	
	
}
