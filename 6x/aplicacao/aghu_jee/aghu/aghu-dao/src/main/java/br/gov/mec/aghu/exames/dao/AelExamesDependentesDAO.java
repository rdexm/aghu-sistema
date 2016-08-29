package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.CancelarExamesAreaExecutoraVO;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMaterialAp;

public class AelExamesDependentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesDependentes>{

	private static final long serialVersionUID = 9077281428518664958L;


	public List<AelExamesDependentes> buscarAelExamesDependentesPorMaterial(AelExamesMaterialAnalise material) {
		return executeCriteria(obterCriteriaBuscarAelExamesDependentesPorMaterial(material));
	}
	
	public Long contarAelExamesDependentesPorMaterial(final AelExamesMaterialAnalise material) {
		return this.executeCriteriaCount(obterCriteriaBuscarAelExamesDependentesPorMaterial(material));
	}
	
	private DetachedCriteria obterCriteriaBuscarAelExamesDependentesPorMaterial(final AelExamesMaterialAnalise material){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(), material.getId().getExaSigla()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(), material.getId().getManSeq()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public List<AelExamesDependentes> obterAelExamesDependentesSiglaEhDependenteESeqEhDependente(final AelExamesMaterialAnalise material){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA_EH_DEPENDENTE.toString(), material.getId().getExaSigla()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ_EH_DEPENDENTE.toString(), material.getId().getManSeq()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelExamesDependentes.class);
	}

	public List<CancelarExamesAreaExecutoraVO> buscarPorExameMaterial(Integer soeSeq, Short seqp) {
	
		List<CancelarExamesAreaExecutoraVO> retorno = new ArrayList<CancelarExamesAreaExecutoraVO>();
		StringBuilder hql = new StringBuilder(200);

		hql.append(" SELECT  ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		.append("        ,ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString())
		.append("        ,exd.").append(AelExamesDependentes.Fields.IND_CANC_AUTOMATICO.toString())
		.append(" FROM ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise,")
		.append( AelExamesDependentes.class.getSimpleName()).append(" exd")
		.append(" where ")
		.append("   ise.").append(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()).append('.').append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq ")
		.append("   and ise.").append(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()).append('.').append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" = :seqp ")
		.append("   and exd.").append(AelExamesDependentes.Fields.EMA_EXA_SIGLA_DEPENDENTE.toString()).append("= ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())
		.append("   and exd.").append(AelExamesDependentes.Fields.EMA_MAN_SEQ_DEPENDENTE.toString()).append(" = ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());

		Query query = this.createQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("seqp", seqp);

		@SuppressWarnings("unchecked")
		List<Object[]> listaObjetos = query.getResultList();
		//Iterator<Object[]> it = listaObjetos.listIterator();
		CancelarExamesAreaExecutoraVO examesDependentes = null;
		
		for(Object[] obj :listaObjetos) { 
			
			examesDependentes = new CancelarExamesAreaExecutoraVO();
			
			if (obj[0] != null) { 
				examesDependentes.setSoeSeq((Integer)obj[0]);
			}
			if (obj[1] != null) { 
				examesDependentes.setSeqp((Short)obj[1]);
			}
			
			if (obj[2] != null) { 
				examesDependentes.setIndCancelaAutomatico((DominioSimNao)obj[2]);
			}
			
			retorno.add(examesDependentes);
			
		}
		
				
				
		return retorno;
	}

	/*
	 *   select ise.ise_soe_seq
       , ise.ise_seqp
       , ise.ufe_ema_exa_sigla
       , ise.ufe_ema_man_seq
	    from ael_exames_dependentes exd
	       , ael_material_aps lur
	       , ael_item_solicitacao_exames ise
	   where --lur.lux_seq = c_lux_seq
	         lur.seq = 110295
	     and lur.ise_soe_seq = ise.ise_soe_seq
	     and lur.ise_seqp = ise.ise_seqp 
	     and  exd.ema_exa_sigla_eh_dependente =  ise.ufe_ema_exa_sigla
	     and  exd.ema_man_seq_eh_dependente   =  ise.ufe_ema_man_seq
	     and  exd.IND_CANC_LAUDO_UNICO        =  'S'
	     and  exd.ind_situacao                =  'A'
	 */
	
	@SuppressWarnings("unchecked")
	public List<ConsultaItensPatologiaVO> listaExamesDependentes(Long lurSeq) {
		
		List<ConsultaItensPatologiaVO> result = null;
		StringBuffer hql = null;
		org.hibernate.Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select ");

		hql.append("ise.").append(AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString());
		hql.append(" as iseSoeSeq");

		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.ISE_SEQP.toString());
		hql.append(" as iseSeqp");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" as ufeEmaExaSigla");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" as ufeEmaManSeq");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" as sitCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AelExamesDependentes.class.getName());
		hql.append(" as exd ");
		hql.append(", ").append(AelMaterialAp.class.getName());
		hql.append(" as lur ");
		hql.append(", ").append(AelItemSolicitacaoExames.class.getName());
		hql.append(" as ise ");
		
		//where
		hql.append(" where lur.");
		hql.append(AelMaterialAp.Fields.SEQ.toString());
		hql.append("  = :lurSeq ");

		hql.append(" and lur.").append(AelMaterialAp.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString());
		
		hql.append(" and lur.").append(AelMaterialAp.Fields.ISE_SEQP.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.ISE_SEQP.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.EMA_EXA_SIGLA_EH_DEPENDENTE.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.EMA_MAN_SEQ_EH_DEPENDENTE.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.IND_CANC_LAUDO_UNICO.toString());
		hql.append(" = :indCancLaudoUnico");

		hql.append(" and exd.").append(AelExamesDependentes.Fields.SITUACAO.toString());
		hql.append(" = :situacao");

		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("lurSeq", lurSeq);
		query.setParameter("indCancLaudoUnico", DominioSimNao.S);
		query.setParameter("situacao", DominioSituacao.A);
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensPatologiaVO.class));
		result = query.list();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaItensPatologiaVO> listaExamesDependentesSoeSeqESeqp(Long lurSeq) {
		
		List<ConsultaItensPatologiaVO> result = null;
		StringBuffer hql = null;
		org.hibernate.Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select ");

		hql.append("ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(" as iseSoeSeq");

		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		hql.append(" as iseSeqp");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" as ufeEmaExaSigla");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" as ufeEmaManSeq");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" as sitCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AelExamesDependentes.class.getName());
		hql.append(" as exd ");
		hql.append(", ").append(AelMaterialAp.class.getName());
		hql.append(" as lur ");
		hql.append(", ").append(AelItemSolicitacaoExames.class.getName());
		hql.append(" as ise ");
		
		//where
		hql.append(" where lur.");
		hql.append(AelMaterialAp.Fields.SEQ.toString());
		hql.append("  = :lurSeq ");

		hql.append(" and lur.").append(AelMaterialAp.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString());
		
		hql.append(" and lur.").append(AelMaterialAp.Fields.ISE_SEQP.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.ISE_SEQP.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.EMA_EXA_SIGLA_EH_DEPENDENTE.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.EMA_MAN_SEQ_EH_DEPENDENTE.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		
		hql.append(" and exd.").append(AelExamesDependentes.Fields.IND_CANC_LAUDO_UNICO.toString());
		hql.append(" = :indCancLaudoUnico");

		hql.append(" and exd.").append(AelExamesDependentes.Fields.SITUACAO.toString());
		hql.append(" = :situacao");

		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("lurSeq", lurSeq);
		query.setParameter("indCancLaudoUnico", DominioSimNao.S);
		query.setParameter("situacao", DominioSituacao.A);
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensPatologiaVO.class));
		result = query.list();
		
		return result;
	}
	

	public List<AelExamesDependentes> listaExamesDependentesPorExaSiglaEManSeq(String exaSigla, Integer manSeq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(), manSeq));
		
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.IND_CANC_LAUDO_UNICO.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}


}
