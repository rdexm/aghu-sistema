package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidade;

public class VAelPesquisaPolExameUnidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicitacaoExames> {
	
	private static final long serialVersionUID = -1297276977086904486L;

	/**
	 * Busca dados necessários para montar a árvore do POL,
	 * de Laboratórios e Serviços.
	 * 
	 * @param {Integer} codigoPaciente
	 * @return {List<VAelPesquisaPolExameUnidade>}
	 */
	public List<VAelPesquisaPolExameUnidade> buscaArvoreLaboratorioServicosDeExames(Integer codigoPaciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelPesquisaPolExameUnidade.class);
		criteria.add(Restrictions.eq(VAelPesquisaPolExameUnidade.Fields.PAC_CODIGO.toString(), codigoPaciente));

		return executeCriteria(criteria);
		
	}

}
