package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.RapServidores;

public class MamControlesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamControles> {
	
	private static final long serialVersionUID = 510465055484661400L;

	/**
	 * Depreciado porque o mapeamento entre MamControles e AACConsultas é de 1 para 1
	 * utilizar obterMamControlePorNumeroConsulta
	 * @param consultaNumero
	 * @return
	 */
	@Deprecated
	public List<MamControles> pesquisarControlePorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = getCriteriaObterMamControlePorNumeroConsulta(consultaNumero);
		return executeCriteria(criteria);
	}
	
	public MamControles obterMamControlePorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = getCriteriaObterMamControlePorNumeroConsulta(consultaNumero);
		criteria.createAlias(MamControles.Fields.SITUACAO_ATENDIMENTOS.toString(), "situacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamControles.Fields.SERVIDOR_ATUALIZA.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);
		return (MamControles) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria getCriteriaObterMamControlePorNumeroConsulta(
			Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamControles.class);
		criteria.add(Restrictions.eq(MamControles.Fields.CON_NUMERO.toString(), consultaNumero));
		return criteria;
	}

	public Long listaControlesPorNumeroConsultaCount(Integer numeroConsulta) {
		DetachedCriteria criteria = createListaControlesPorNumeroConsultaCriteria(numeroConsulta);
		return executeCriteriaCount(criteria);
	}

	public List<MamControles> listaControlesPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = createListaControlesPorNumeroConsultaCriteria(numeroConsulta);
		return executeCriteria(criteria);
	}

	private DetachedCriteria createListaControlesPorNumeroConsultaCriteria(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamControles.class);

		criteria.createAlias(MamControles.Fields.CONSULTA.toString(), MamControles.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamControles.Fields.CON_NUMERO.toString(), numeroConsulta));

		return criteria;
	}
	
	/** Pesquisa Controles por Numero
	 * 
	 * @param numeroConsulta
	 * @return
	 */
	public List<MamControles> pesquisarControlesPorNumeroConsultaESituacao(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamControles.class);

		if (numeroConsulta != null){
			criteria.add(Restrictions.eq(MamControles.Fields.CON_NUMERO.toString(), numeroConsulta));
		}
		return  executeCriteria(criteria);
	}
	
}
