package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.PdtMedicUsual;

public class PdtMedicUsualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtMedicUsual>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1746452516756396029L;

	public List<PdtMedicUsual> pesquisarPdtMedicUsual(PdtMedicUsual pdtMedicUsual) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtMedicUsual.class, "pmu");
		
		criteria.createAlias("pmu.".concat(PdtMedicUsual.Fields.AFA_MEDICAMENTO.toString()), "med");
		
		criteria.createAlias("med.".concat(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString()), "umm", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(PdtMedicUsual.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), pdtMedicUsual.getAghUnidadesFuncionais()));
		
		criteria.add(Restrictions.eq("med.".concat(AfaMedicamento.Fields.IND_SITUACAO.toString()), DominioSituacaoMedicamento.A));
		
		criteria.addOrder(Order.asc("med." + AfaMedicamento.Fields.DESCRICAO.toString()));
				
		return executeCriteria(criteria);
	}

}
