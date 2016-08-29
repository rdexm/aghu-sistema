package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.internacao.pesquisa.vo.PacientesInternadosUltrapassadoVO;
import br.gov.mec.aghu.model.VAinInternacoesExcedentes;

public class VAinInternacoesExcedentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinInternacoesExcedentes> {

	private static final long serialVersionUID = -8076238914061504406L;

	public List<PacientesInternadosUltrapassadoVO> obterUnidadesQtdPacientesInternadosExcedentes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinInternacoesExcedentes.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(VAinInternacoesExcedentes.Fields.UNIDADE_FUNCIONAL.toString())
						, PacientesInternadosUltrapassadoVO.Fields.DESCRICAO_UNIDADE.toString())
				.add(Projections.count(VAinInternacoesExcedentes.Fields.DTHR_INTERNACAO.toString())
						, PacientesInternadosUltrapassadoVO.Fields.NRO_PACIENTES.toString()));
		
		criteria.addOrder(Order.desc(PacientesInternadosUltrapassadoVO.Fields.NRO_PACIENTES.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesInternadosUltrapassadoVO.class));
		
		return executeCriteria(criteria);
	}

}
