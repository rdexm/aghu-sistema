package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.indicadores.vo.ReferencialClinicaEspecialidadeVO;

/**
 * 
 * 
 */
public class ReferencialClinicaEspecialidadeVODAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ReferencialClinicaEspecialidadeVO> {

	private static final long serialVersionUID = -7321130756093036262L;

	public List<ReferencialClinicaEspecialidadeVO> pesquisarReferencialClinicaEspecialidade(
			Integer codigoClinica, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ReferencialClinicaEspecialidadeVO.class);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

}