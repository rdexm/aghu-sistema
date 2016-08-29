package br.gov.mec.aghu.prescricaoenfermagem.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpePrescCuidMedicamento;

public class EpePrescCuidMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpePrescCuidMedicamento> {

	private static final long serialVersionUID = 2449083543086742348L;

	// #4961 - Manter medicamentos x cuidados
	// C5
	public Boolean verificarCuidadoRelacionadoPrescricao(Short cuiSeq, Integer medMatCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescCuidMedicamento.class);
		criteria.add(Restrictions.eq(EpePrescCuidMedicamento.Fields.CME_CUI_SEQ.toString(), cuiSeq));
		criteria.add(Restrictions.eq(EpePrescCuidMedicamento.Fields.CME_MED_MAT_CODIGO.toString(), medMatCodigo));
		return executeCriteriaExists(criteria);
	}

}