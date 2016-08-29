package br.gov.mec.aghu.ambulatorio.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamMotivoAtendimento;

public class MamMotivoAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamMotivoAtendimento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2221910185864790404L;

	/**
	 * #44179 - CURSOR cur_rgt_pend
	 * @author thiago.cortes
	 * @return 'S'
	 */
	public String curMoe(Long trgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamMotivoAtendimento.class,"MOE");
		if(trgSeq != null){
			criteria.add(Restrictions.eq(MamMotivoAtendimento.Fields.TRIAGEM.toString(),trgSeq));
		}
		criteria.add(Restrictions.eq(MamMotivoAtendimento.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull(MamMotivoAtendimento.Fields.DTHR_VALIDA_MVTO.toString()));
		if(executeCriteriaExists(criteria)){
			return "S";
		}
		return StringUtils.EMPTY;
	}
}
