package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelDataRespostaProtocolos;

public class AelDataRespostaProtocolosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDataRespostaProtocolos> {
	
	private static final long serialVersionUID = 9035628058629833397L;

	public List<AelDataRespostaProtocolos> listarDatasRespostasProtocolosPorPpjPacCodigo(Integer ppjPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDataRespostaProtocolos.class);
		
		criteria.add(Restrictions.eq(AelDataRespostaProtocolos.Fields.PPJ_PAC_CODIGO.toString(), ppjPacCodigo));
		
		return executeCriteria(criteria);
	}
	
}
