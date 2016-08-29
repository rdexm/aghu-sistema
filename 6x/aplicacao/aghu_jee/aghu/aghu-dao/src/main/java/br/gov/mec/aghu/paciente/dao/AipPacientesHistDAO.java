package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AipPacientesHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacientesHist> {

	private static final long serialVersionUID = 3320466843693477168L;

	public List<AipPacientesHist> pesquisaPacientesHistorico(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String nome) throws ApplicationBusinessException {
		DetachedCriteria criteria = createPesquisaPacientesHistoricoCriteria(nome);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisaPacientesHistoricoCount(String nome) throws ApplicationBusinessException {
		return executeCriteriaCount(createPesquisaPacientesHistoricoCriteria(nome));
	}

	private DetachedCriteria createPesquisaPacientesHistoricoCriteria(String nome) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientesHist.class);

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AipPacientesHist.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		return criteria;
	} 

	
	public Object[] obterPacienteHistAnterior(Integer codigo) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AipPacientesHist.class);
		
		criteriaDadosAnteriores.setProjection(Projections.projectionList()
				.add(
						Projections.property(AipPacientesHist.Fields.CODIGO
								.toString())).add(
						Projections.property(AipPacientesHist.Fields.NOME
								.toString())).add(
						Projections.property(AipPacientesHist.Fields.PRNT_ATIVO
								.toString())).add(
						Projections.property(AipPacientesHist.Fields.PRONTUARIO
								.toString())));
		
		criteriaDadosAnteriores.add(Restrictions.eq(
				AipPacientesHist.Fields.CODIGO.toString(), codigo));
		
		return (Object[]) executeCriteriaUniqueResult(criteriaDadosAnteriores);
	}

	/**
	 * Método para criar detachedCriteria aser usada por
	 * pesquisarHistoricoPacientesExcluidos() e
	 * obterHistoricoPacientesExcluidosCount()
	 * 
	 * @param codigo
	 * @param prontuario
	 * @param nome
	 * @return DetachedCriteria
	 */
	private DetachedCriteria criarCriteriaHistoricoPacientesExcluidos(Integer codigo, Integer prontuario, String nome) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesHist.class);

		if (codigo != null) {
			cri.add(Restrictions.eq(AipPacientesHist.Fields.CODIGO.toString(), codigo));
		}

		if (prontuario != null) {
			cri.add(Restrictions.eq(AipPacientesHist.Fields.PRONTUARIO.toString(), prontuario));
		}

		if (nome != null && !"".equalsIgnoreCase(nome.trim())) {
			cri.add(Restrictions.ilike(AipPacientesHist.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		return cri;
	}

	public List<AipPacientesHist> pesquisarHistoricoPacientesExcluidos(Integer firstResult, Integer maxResult, Integer codigo,
			Integer prontuario, String nome) {
		DetachedCriteria criteria = criarCriteriaHistoricoPacientesExcluidos(codigo, prontuario, nome);
		criteria.addOrder(Order.asc(AipPacientesHist.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public Long obterHistoricoPacientesExcluidosCount(Integer codigo, Integer prontuario, String nome) {
		return executeCriteriaCount(criarCriteriaHistoricoPacientesExcluidos(codigo, prontuario, nome));
	}
	
	public AipPacientesHist obterHistoricoPaciente(Integer prontuario, Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesHist.class);

		if (prontuario != null) {
			cri.add(Restrictions.eq("prontuario", prontuario));
		}

		if (codigo != null) {
			cri.add(Restrictions.eq("codigo", codigo));
		}

		return (AipPacientesHist) executeCriteriaUniqueResult(cri);
	}
	
	public AipPacientesHist pesquisarPacientePorProntuario(Integer nroProntuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientesHist.class);
		criteria.add(Restrictions.eq(AipPacientesHist.Fields.PRONTUARIO.toString(), nroProntuario));
		return (AipPacientesHist) executeCriteriaUniqueResult(criteria);
	}
}
