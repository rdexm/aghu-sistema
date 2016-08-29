package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioApAnterior;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelVersaoLaudo;

public class AelExameApItemSolicDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameApItemSolic> {


	
	private static final long serialVersionUID = 7598110450007861005L;

	public boolean hasAelExameApItemSolicPorItemSolicitacaoExame(final AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId) {
		/*
						CURSOR c_lul (c_ise_soe_seq     ael_exame_ap_item_solics.ise_soe_seq%type,
					              c_ise_seqp        ael_exame_ap_item_solics.ise_seqp%type)
					  final IS
					   SELECT 'S'
					     final FROM ael_exame_ap_item_solics
					    final WHERE ise_soe_seq = c_ise_soe_seq
					      final AND ise_seqp    = c_ise_seqp;
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class);
		if (aelItemSolicitacaoExamesId != null) {
			if (aelItemSolicitacaoExamesId.getSoeSeq() != null) {
				criteria.add(Restrictions.eq(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString(), aelItemSolicitacaoExamesId.getSoeSeq()));
			}
			if (aelItemSolicitacaoExamesId.getSeqp() != null) {
				criteria.add(Restrictions.eq(AelExameApItemSolic.Fields.ISE_SEQP.toString(), aelItemSolicitacaoExamesId.getSeqp()));
			}
		}
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<AelExameApItemSolic> listarAelExameApItemSolicPorItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class);		
		criteria.add(Restrictions.eq(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelExameApItemSolic.Fields.ISE_SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}	
	
	public List<AelExameApItemSolic> listarAelExameApItemSolicPorLumSeq(Long lumSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class);
		criteria.createAlias(AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("lux." + AelExameAp.Fields.LUM_SEQ.toString(), lumSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<AelExameApItemSolic> listarAelExameApItemSolicPorLuxSeq(Long luxSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class);
		criteria.createAlias(AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("lux." + AelExameAp.Fields.SEQ.toString(), luxSeq));
		
		criteria.addOrder(Order.asc(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString()));
		criteria.addOrder(Order.asc(AelExameApItemSolic.Fields.ISE_SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Boolean hasAelExameApItemSolicPorSolicitacao(final Integer iseSoeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class);
		criteria.add(Restrictions.eq(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		return executeCriteriaCount(criteria) > 0;
	}	

	/*SELECT 'S'
  FROM ael_unf_executa_exames   	ufe,
       ael_item_solicitacao_exames 	ise,
	 ael_exame_ap_item_solics 	lul
 WHERE lul.lux_seq  		= c_lux_seq
   AND lul.ise_soe_seq 		= ise.soe_seq
   AND lul.ise_seqp		= ise.seqp
   AND ise.ufe_ema_exa_sigla	= ufe.ema_exa_sigla
   AND ise.ufe_ema_man_seq  	= ufe.ema_man_seq
   AND ise.ufe_unf_seq      	= ufe.unf_seq
   AND ufe.ind_num_ap_anterior= 'O'; --Obrigatório*/
	public List<AelExameApItemSolic> listarAelExameApItemSolicPorLuxObrigatorio(Long luxSeq) {
	
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class, "lul");
		
		criteria.createAlias(AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise", Criteria.INNER_JOIN);
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES, "ufe", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("lul." + AelExameApItemSolic.Fields.LUX_SEQ.toString(), luxSeq));
		criteria.add(Restrictions.eq("ufe." + AelUnfExecutaExames.Fields.IND_NUM_AP_ANTERIOR.toString(), DominioApAnterior.O));
		
		return executeCriteria(criteria);
	}

	public List<ConsultaItensPatologiaVO> listaExamesComVersaoLaudo(Long luxSeq, Integer calcSeq, String[] sitCodigo) {
		
		List<ConsultaItensPatologiaVO> result = null;
		StringBuffer hql = null;
		org.hibernate.Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select ");

		hql.append("lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		hql.append(" as iseSoeSeq");

		hql.append(", lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		hql.append(" as iseSeqp");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" as ufeEmaExaSigla");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" as ufeEmaManSeq");
		
		hql.append(", pcl.").append(AelParametroCamposLaudo.Fields.VEL_SEQP.toString());
		hql.append(" as velSeqp");

		hql.append(", pcl.").append(AelParametroCamposLaudo.Fields.CAL_SEQ.toString());
		hql.append(" as calSeq");

		hql.append(", pcl.").append(AelParametroCamposLaudo.Fields.SEQP.toString());
		hql.append(" as seqp");

		//from
		hql.append(" from ");
		hql.append(AelExameApItemSolic.class.getName());
		hql.append(" as lul ");
		hql.append(", ").append(AelItemSolicitacaoExames.class.getName());
		hql.append(" as ise ");
		hql.append(", ").append(AelVersaoLaudo.class.getName());
		hql.append(" as vel ");
		hql.append(", ").append(AelParametroCamposLaudo.class.getName());
		hql.append(" as pcl ");
		
		//where
		hql.append(" where lul.");
		hql.append(AelExameApItemSolic.Fields.LUX_SEQ.toString());
		hql.append("  = :luxSeq ");

		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		
		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		
		hql.append(" and vel.").append(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		hql.append(" and vel.").append(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());

		hql.append(" and vel.").append(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" = ");
		hql.append(" pcl.").append(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString());
		
		hql.append(" and vel.").append(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString());
		hql.append(" = ");
		hql.append(" pcl.").append(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString());

		hql.append(" and vel.").append(AelVersaoLaudo.Fields.SEQP.toString());
		hql.append(" = ");
		hql.append(" pcl.").append(AelParametroCamposLaudo.Fields.VEL_SEQP.toString());
		
		hql.append(" and vel.").append(AelVersaoLaudo.Fields.SITUACAO.toString());
		hql.append(" = :situacao");

		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" in (:situacaoCodigo)");

		hql.append(" and pcl.").append(AelParametroCamposLaudo.Fields.CAL_SEQ.toString());
		hql.append(" = :calSeq");
		
		hql.append(" order by");
		hql.append(" lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		hql.append(" , lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("luxSeq", luxSeq);
		query.setParameter("calSeq", calcSeq);
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameterList("situacaoCodigo", sitCodigo);
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensPatologiaVO.class));
		result = query.list();
		
		return result;
	}
	
	public List<AelItemSolicitacaoExameLaudoUnicoVO> obterAelItemSolicitacaoExamesPorLuxSeq(final Long luxSeq){
		/*
		 cursor c_itens (c_lux_seq ael_exame_aps.seq%type)
   is
   select distinct
          ise.soe_seq,
          ise.seqp
     from ael_item_solicitacao_exames ise,
          ael_exame_ap_item_solics lul
    where lul.lux_seq = c_lux_seq
      and ise.soe_seq = lul.ise_soe_seq
      and ise.seqp    = lul.ise_seqp
 order by ise.soe_seq,
          ise.seqp;
		 */
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameApItemSolic.class, "lul");
		criteria.createAlias(AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise", Criteria.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),AelItemSolicitacaoExameLaudoUnicoVO.Fields.SOE_SEQ.toString())
																		        .add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()),AelItemSolicitacaoExameLaudoUnicoVO.Fields.SEQP.toString())
											       )
				              );

		criteria.add(Restrictions.eq("lul."+AelExameApItemSolic.Fields.LUX_SEQ.toString(), luxSeq));

		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelItemSolicitacaoExameLaudoUnicoVO.class));

		return executeCriteria(criteria);
	}
	
	public List<AelItemSolicitacaoExameLaudoUnicoVO> obterAelItemSolicitacaoExamesHistPorLuxSeq(final Long luxSeq){
		/*
		    select distinct
          ise.soe_seq,
          ise.seqp
     from agh.ael_item_solic_exames_hist ise,
          agh.ael_exame_ap_item_solics lul
    where  lul.lux_seq = 253
      and ise.soe_seq = lul.ise_soe_seq
      and ise.seqp    = lul.ise_seqp
 Order By Ise.Soe_Seq,
          ise.seqp;  
		 */
		
		StringBuffer hql = new StringBuffer(130);
		
		hql.append(" select ");

		hql.append(" distinct ise." ).append( AelItemSolicExameHist.Fields.SOE_SEQ.toString()).append(" as ").append(AelItemSolicitacaoExameLaudoUnicoVO.Fields.SOE_SEQ.toString());
		hql.append("        , ise." ).append( AelItemSolicExameHist.Fields.SEQP.toString()).append(" as ").append(AelItemSolicitacaoExameLaudoUnicoVO.Fields.SEQP.toString());
		
		hql.append(" from ");
		hql.append(AelItemSolicExameHist.class.getName()).append(" as ise, ");
		hql.append(AelExameApItemSolic.class.getName()).append(" as lul ");
		
		//where
		hql.append(" where lul.");
		hql.append(AelExameApItemSolic.Fields.LUX_SEQ.toString());
		hql.append("  = :luxSeq ");

		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicExameHist.Fields.SOE_SEQ.toString());
		
		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicExameHist.Fields.SEQP.toString());
		
		//query
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("luxSeq", luxSeq);
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensPatologiaVO.class));
		
		return query.list();
	}
}
