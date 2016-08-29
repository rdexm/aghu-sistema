package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PdtInstrumentalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtInstrumental> {

	private static final long serialVersionUID = -5523526478655923691L;
	
	public List<PdtInstrumental> listarPdtInstrumentalAtivaPorDescricao(Object strPesquisa) {
		
		DetachedCriteria criteria = criarCriteriaListarPdtInstrumentalAtivaPorDescricacao(strPesquisa);
		criteria.addOrder(Order.asc(PdtInstrumental.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<PdtInstrumental> listarPdtInstrumentalPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = criarCriteriaListarPdtInstrumentalPorSeqDescricaoSituacao(
				seq, descricao, situacao);
		criteria.addOrder(Order.asc(PdtInstrumental.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long listarPdtInstrumentalPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = criarCriteriaListarPdtInstrumentalPorSeqDescricaoSituacao(
				seq, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarPdtInstrumentalPorSeqDescricaoSituacao(
			Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrumental.class);
		if(seq != null){
			criteria.add(Restrictions.eq(PdtInstrumental.Fields.SEQ.toString(), seq));
		}
		if(descricao != null){
			criteria.add(Restrictions.ilike(PdtInstrumental.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(PdtInstrumental.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public Long listarPdtInstrumentalAtivaPorDescricaoCount(Object strPesquisa) {
		
		DetachedCriteria criteria = criarCriteriaListarPdtInstrumentalAtivaPorDescricacao(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarPdtInstrumentalAtivaPorDescricacao(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrumental.class);
		criteria.add(Restrictions.eq(PdtInstrumental.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(PdtInstrumental.Fields.SEQ.toString(), Integer.valueOf(strPesquisa.toString())));
		}else if(strPesquisa != null && !strPesquisa.toString().isEmpty()){
			criteria.add(Restrictions.ilike(PdtInstrumental.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public List<PdtInstrumental> pesquisarPdtInstrumental(final String strPesquisa, final Short deqSeq) {
		final DetachedCriteria criteria = createCriteriaPesquisarPdtInstrumental(strPesquisa, deqSeq);
		return executeCriteria(criteria, 0, 100, PdtInstrumental.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarPdtInstrumentalCount(final String strPesquisa, final Short deqSeq) {
		final DetachedCriteria criteria = createCriteriaPesquisarPdtInstrumental(strPesquisa, deqSeq);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria createCriteriaPesquisarPdtInstrumental(
			final String strPesquisa, final Short deqSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrumental.class, "INST");
		

		/* 
	SELECT L_PIN.IND_SITUACAO L_PIN_IND_SITUACAO ,
	  L_PIN.SEQ PIN_SEQ ,
	  L_PIN.DESCRICAO L_PIN_DESCRICAO
	FROM Pdt_Instrumentais L_Pin
	Where 
	  ( 
	    ( L_PIN.IND_SITUACAO = 'A'
	      AND L_PIN.SEQ IN (SELECT PIN_SEQ FROM PDT_INSTR_POR_EQUIPS IEQ WHERE DEQ_SEQ = :PDD.DEQ_SEQ) 
	    ) Or
	     
	    ( AND L_PIN.IND_SITUACAO = 'A' 
	      NOT EXISTS      (SELECT PIN_SEQ FROM PDT_INSTR_POR_EQUIPS IEQ WHERE DEQ_SEQ = :PDD.DEQ_SEQ) 
	    )
	  )
		 */
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("INST."+PdtInstrumental.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			
		} else if(strPesquisa != null && !strPesquisa.isEmpty()){
			criteria.add(Restrictions.ilike("INST."+PdtInstrumental.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtInstrPorEquip.class, "IEQ");
		subCriteria.setProjection(Projections.property("IEQ."+PdtInstrPorEquip.Fields.PIN_SEQ.toString()));
		subCriteria.add(Restrictions.eq("IEQ."+PdtInstrPorEquip.Fields.DEQ_SEQ.toString(), deqSeq));
		
		criteria.add(
					  Restrictions.or(
							  			Restrictions.and(Restrictions.eq(PdtInstrumental.Fields.IND_SITUACAO.toString(), DominioSituacao.A),
							  							  Subqueries.exists(subCriteria)
							  							),
							  							
			  							Restrictions.and(Subqueries.notExists(subCriteria),
			  										   	 Restrictions.eq(PdtInstrumental.Fields.IND_SITUACAO.toString(), DominioSituacao.A)
			  										   	)
							  		 )
				    );
		return criteria;
	}
}
