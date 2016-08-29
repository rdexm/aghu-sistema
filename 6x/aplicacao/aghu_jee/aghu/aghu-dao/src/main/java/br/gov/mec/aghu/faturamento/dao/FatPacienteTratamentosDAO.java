package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatPacienteTratamentos;

public class FatPacienteTratamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPacienteTratamentos> {

	private static final long serialVersionUID = 316722662300009521L;

	public List<FatPacienteTratamentos> listarPacientesTratamentosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTratamentos.class);

		criteria.add(Restrictions.eq(FatPacienteTratamentos.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

}
