package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.FatUnidadeFuncionalCnesVO;
import br.gov.mec.aghu.model.FatCnesUf;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.model.FatServicos;

public class FatCnesUfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCnesUf>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5571331411203090661L;
	
	// #41079 C1 
	public List<FatUnidadeFuncionalCnesVO> pesquisarFatUnidadeFuncionalCnes(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCnesUf.class, "CNESUF");
		criteria.createAlias("CNESUF." + FatCnesUf.Fields.FCS.toString(), "CLA");
		criteria.createAlias("CLA." + FatServClassificacoes.Fields.FSE.toString(), "SER");
		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("CNESUF." + FatCnesUf.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("CNESUF." + FatCnesUf.Fields.SEQ.toString()), FatUnidadeFuncionalCnesVO.Fields.CNES_SEQ.toString())
				.add(Projections.property("CNESUF." + FatCnesUf.Fields.UNF_SEQ.toString()), FatUnidadeFuncionalCnesVO.Fields.CNES_UNF_SEQ.toString())
				.add(Projections.property("SER." + FatServicos.Fields.CODIGO.toString()), FatUnidadeFuncionalCnesVO.Fields.SERV_CODIGO.toString())
				.add(Projections.property("CLA." + FatServClassificacoes.Fields.SEQ.toString()), FatUnidadeFuncionalCnesVO.Fields.CLA_SEQ.toString())
				.add(Projections.property("CLA." + FatServClassificacoes.Fields.CODIGO.toString()), FatUnidadeFuncionalCnesVO.Fields.CLA_CODIGO.toString())
				.add(Projections.property("CLA." + FatServClassificacoes.Fields.DESCRICAO.toString()), FatUnidadeFuncionalCnesVO.Fields.CLA_DESCRICAO.toString())
				;
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(FatUnidadeFuncionalCnesVO.class));
		
		criteria.addOrder(Order.asc("SER." + FatServicos.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("CLA." + FatServicos.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("CLA." + FatServicos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public List<FatCnesUf> obterFatCnesUfPorUnfSeqFcsSeq(Short unfSeq, Integer fcsSeq, Short cnesSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCnesUf.class, "CNESUF");
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("CNESUF." + FatCnesUf.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (fcsSeq != null) {
			criteria.add(Restrictions.eq("CNESUF." + FatCnesUf.Fields.FCS_SEQ.toString(), fcsSeq));
		}
		if (cnesSeq != null) {
			criteria.add(Restrictions.eq("CNESUF." + FatCnesUf.Fields.SEQ.toString(), cnesSeq));
		}
		
		return  executeCriteria(criteria);
	}

}
