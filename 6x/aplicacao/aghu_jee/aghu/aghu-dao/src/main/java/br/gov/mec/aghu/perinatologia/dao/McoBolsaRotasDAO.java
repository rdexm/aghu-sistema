package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoBolsaRotasDAO extends BaseDao<McoBolsaRotas> {

	private static final long serialVersionUID = 5148772214667324202L;

	public List<McoBolsaRotas> listarBolsasRotas(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoBolsaRotas.class);

		criteria.add(Restrictions.eq(McoBolsaRotas.Fields.ID_CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoBolsaRotas.Fields.ID_SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}
	
	public McoBolsaRotas buscarBolsaRotas(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoBolsaRotas.class);
		
		criteria.add(Restrictions.eq(McoBolsaRotas.Fields.ID_CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoBolsaRotas.Fields.ID_SEQUENCE.toString(), seqp));
		
		return (McoBolsaRotas) executeCriteriaUniqueResult(criteria);
	}

	public List<McoBolsaRotas> listarBolsasRotasPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoBolsaRotas.class);

		criteria.add(Restrictions.eq(McoBolsaRotas.Fields.ID_CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

}
