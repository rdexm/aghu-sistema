package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;

public class MpmServRecomendacaoAltaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmServRecomendacaoAlta> {

	
	private static final long serialVersionUID = 26613901667735319L;

	public MpmServRecomendacaoAlta obter(Integer seqp, Integer serMatricula, Short serVinCodigo){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmServRecomendacaoAlta.class);
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_MAT.toString(), serMatricula));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_VIN.toString(), serVinCodigo));

		return (MpmServRecomendacaoAlta) this.executeCriteriaUniqueResult(criteria);
	}

}
