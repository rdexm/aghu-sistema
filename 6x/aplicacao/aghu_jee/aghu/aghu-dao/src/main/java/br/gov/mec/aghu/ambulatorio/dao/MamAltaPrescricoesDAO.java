package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamAltaPrescricoes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolPrescricaoVO;

public class MamAltaPrescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAltaPrescricoes> {

	private static final long serialVersionUID = -111471144302085631L;

	public List<MamAltaPrescricoes> procurarAltaPrescricoesBySumarioAltaEIndSelecionado(
			MamAltaSumario altaSumario, DominioSimNao indSelecionado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaPrescricoes.class);
		
		criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.ALTA_SUMARIO.toString(), altaSumario));

		if(indSelecionado != null){
			criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.IND_SELECIONADO.toString(), DominioSimNao.S.equals(indSelecionado)));
		}

		return executeCriteria(criteria);
	}

	public Long pesquisarAltaPrescricoesCount(Long seqMamAltaSumario) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaPrescricoes.class);
		
		criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.ALTA_SUMARIO_SEQ.toString(), seqMamAltaSumario));
		criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.IND_SELECIONADO.toString(), true));
		
		return executeCriteriaCount(criteria);
	}

	public List<AltaAmbulatorialPolPrescricaoVO> recuperarAltaAmbPolPrescricaoList(Long seqMamAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaPrescricoes.class);
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MamAltaPrescricoes.Fields.SEQ.toString()), AltaAmbulatorialPolPrescricaoVO.Fields.SEQ.toString())
		.add(Projections.property(MamAltaPrescricoes.Fields.DESCRICAO.toString()), AltaAmbulatorialPolPrescricaoVO.Fields.DESCRICAO.toString());

		criteria.setProjection(projection);	
		
		criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.ALTA_SUMARIO_SEQ.toString(), seqMamAltaSumario));
		criteria.add(Restrictions.eq(MamAltaPrescricoes.Fields.IND_SELECIONADO.toString(), true));
		
		criteria.addOrder(Order.asc(AltaAmbulatorialPolPrescricaoVO.Fields.SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AltaAmbulatorialPolPrescricaoVO.class));

		return executeCriteria(criteria);
	}
	
}
