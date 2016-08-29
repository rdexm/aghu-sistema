package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PdtTecnicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtTecnica> {

	private static final long serialVersionUID = -3166724440305885023L;
	
	/**
	 * 
	 * Busca as Técnica por Descrição ou Seq
	 * 
	 * @return Lista de técnicas
	 */	
	public List<PdtTecnica> pesquisarTecnicaPorDescricaoOuSeq(Object strPesquisa) {
		DetachedCriteria cri = criarCriteriaTecnicaPorDescricaoOuSeq(strPesquisa);
		
		cri.addOrder(Order.asc(PdtTecnica.Fields.DESCRICAO.toString()));

		return executeCriteria(cri);
	}
	
	private DetachedCriteria criarCriteriaTecnicaPorDescricaoOuSeq(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(PdtTecnica.class);

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(PdtTecnica.Fields.SEQ.toString(), Integer.valueOf(strPesquisa.toString())));
		}else if(strPesquisa != null && !strPesquisa.toString().isEmpty()){
			cri.add(Restrictions.ilike(PdtTecnica.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
		}

		cri.add(Restrictions.eq(PdtTecnica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		return cri;
	}
	
	public List<PdtTecnica> listarPdtTecnicaPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = criarCriteriaListarPdtTecnicaPorSeqDescricaoSituacao(seq,descricao,situacao);
		criteria.addOrder(Order.asc(PdtTecnica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarPdtTecnicaPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = criarCriteriaListarPdtTecnicaPorSeqDescricaoSituacao(seq,descricao,situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarPdtTecnicaPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtTecnica.class);
		if(seq != null) {
			criteria.add(Restrictions.eq(PdtTecnica.Fields.SEQ.toString(), seq));
		}
		if(descricao != null) {
			criteria.add(Restrictions.ilike(PdtTecnica.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq(PdtTecnica.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}

	public List<PdtTecnica> listarPdtTecnicaPorDptSeq(final Integer dptSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtTecnica.class,"TEC");
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtTecnicaPorProc.class,"TPP");
		subCriteria.setProjection(Projections.property("TPP."+PdtTecnicaPorProc.Fields.DTE_SEQ.toString()));
		subCriteria.add(Restrictions.eq("TPP."+PdtTecnicaPorProc.Fields.DPT_SEQ.toString(), dptSeq));
		
		
		criteria.add(Restrictions.eq("TEC."+PdtTecnica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Property.forName("TEC."+PdtTecnica.Fields.SEQ.toString()).in(subCriteria));
		criteria.addOrder(Order.asc("TEC."+PdtTecnica.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
}
