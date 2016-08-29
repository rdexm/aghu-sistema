package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidadeHist;

public class VAelPesquisaPolExameUnidadeHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicitacaoExames> {
	
	private static final long serialVersionUID = 4238242609895834805L;

	/**
	 * Busca dados necessários para montar a árvore do POL,
	 * de Laboratórios e Serviços Hist.
	 * 
	 * @param {Integer} codigoPaciente
	 * @return {List<VAelPesquisaPolExameUnidadeHist>}
	 */
	public List<VAelPesquisaPolExameUnidadeHist> buscarArvoreLaboratorioServicosDeExamesHist(Integer codigoPaciente) {
		DetachedCriteria criteria = montaCriteriaLaboratorioServicos(codigoPaciente);

		return executeCriteria(criteria);
	}
	
	protected DetachedCriteria montaCriteriaLaboratorioServicos(Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelPesquisaPolExameUnidadeHist.class);
		criteria.add(Restrictions.eq(VAelPesquisaPolExameUnidadeHist.Fields.PAC_CODIGO.toString(), codigoPaciente));
		return criteria;
	}

}
