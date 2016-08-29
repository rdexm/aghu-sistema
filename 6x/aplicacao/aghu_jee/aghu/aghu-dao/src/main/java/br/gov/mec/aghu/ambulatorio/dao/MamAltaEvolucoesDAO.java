package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MamAltaEvolucoes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolEvolucaoVO;

public class MamAltaEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAltaEvolucoes> {

	private static final long serialVersionUID = -1377294506151398127L;

	public List<MamAltaEvolucoes> procurarAltaEvolucoesBySumarioAlta(
			MamAltaSumario altaSumario) {
		DetachedCriteria criteria = getCriteriaProcurarAltaEvolucoesBySumarioAlta(altaSumario);
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaProcurarAltaEvolucoesBySumarioAlta(
			MamAltaSumario altaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaEvolucoes.class);
		criteria.add(Restrictions.eq(MamAltaEvolucoes.Fields.ALTA_SUMARIO.toString(), altaSumario));
		return criteria;
	}

	public List<AltaAmbulatorialPolEvolucaoVO> recuperarAltaAmbPolEvolucaoList(Long seqMamAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaEvolucoes.class);
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MamAltaEvolucoes.Fields.SEQ.toString()), AltaAmbulatorialPolEvolucaoVO.Fields.SEQ.toString())
		.add(Projections.property(MamAltaEvolucoes.Fields.DESCRICAO.toString()), AltaAmbulatorialPolEvolucaoVO.Fields.DESCRICAO.toString());

		criteria.setProjection(projection);	
		
		criteria.add(Restrictions.eq(MamAltaEvolucoes.Fields.ALTA_SUMARIO_SEQ.toString(), seqMamAltaSumario));
		
		criteria.addOrder(Order.asc(AltaAmbulatorialPolEvolucaoVO.Fields.SEQ.toString()));
				
		criteria.setResultTransformer(Transformers.aliasToBean(AltaAmbulatorialPolEvolucaoVO.class));

		return executeCriteria(criteria);
	}
	
}
