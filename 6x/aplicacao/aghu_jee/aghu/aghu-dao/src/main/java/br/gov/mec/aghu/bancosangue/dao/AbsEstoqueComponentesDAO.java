package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsEstoqueComponentes;

public class AbsEstoqueComponentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsEstoqueComponentes> {

	private static final long serialVersionUID = -5619167983642482933L;

	public List<AbsEstoqueComponentes> listarEstoqueComponentesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsEstoqueComponentes.class);

		criteria.add(Restrictions.eq(AbsEstoqueComponentes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AbsEstoqueComponentes> listarEstoquesComponentes(Integer pacCodigo, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsEstoqueComponentes.class);
		criteria.add(Restrictions.eq(AbsEstoqueComponentes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AbsEstoqueComponentes.Fields.CRIADO_EM.toString(), dthrInicio));

		return executeCriteria(criteria);
	}
	
	public Boolean existeComponentePesoFornecedor(AbsComponentePesoFornecedor absComponentePesoFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsEstoqueComponentes.class);
		criteria.add(Restrictions.eq(AbsEstoqueComponentes.Fields.COMPONENTE_PESO_FORNECEDOR.toString(),absComponentePesoFornecedor));
		return executeCriteriaExists(criteria);
	}

}
