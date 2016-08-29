package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmModeloBasicoPrescricaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoPrescricao> {

	private static final long serialVersionUID = -8920344830040136002L;

	/**
	 * Retorna os modelos básicos de prescrição cadastrados para o servidor
	 * fornecido.
	 * 
	 * @param servidor
	 * @return
	 */
	public List<MpmModeloBasicoPrescricao> listar(RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoPrescricao.class);
		criteria.add(Restrictions.eq(MpmModeloBasicoPrescricao.Fields.SERVIDOR.toString(), servidor));
		criteria.addOrder(Order.asc(MpmModeloBasicoPrescricao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Busca modelo basico de prescrição
	 * 
	 * @param {Integer} seq
	 * @return {MpmModeloBasicoPrescricao}
	 * @throws ApplicationBusinessException
	 */
	public MpmModeloBasicoPrescricao obterModeloBasicoPrescricaoPeloId(
			Integer seq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoPrescricao.class);
		criteria.add(Restrictions.eq(
				MpmModeloBasicoPrescricao.Fields.SEQ.toString(), seq));
		return (MpmModeloBasicoPrescricao) executeCriteriaUniqueResult(criteria);

	}
	
	public MpmModeloBasicoPrescricao obterModeloBasicoPrescricaoComServidorPorId(
			Integer seq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoPrescricao.class, "MBP");
		criteria.createAlias("MBP." + MpmModeloBasicoPrescricao.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MpmModeloBasicoPrescricao.Fields.SEQ.toString(), seq));
		
		return (MpmModeloBasicoPrescricao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna os modelos básicos públicos
	 * @param descricaoModelo
	 * @param descricaoCentroCusto
	 * @return
	 */
	public List<MpmModeloBasicoPrescricao> pequisarModelosPublicos(String descricaoModelo, String descricaoCentroCusto, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = obterCriteriaModeloBasicoPrescricao(
				descricaoModelo, descricaoCentroCusto);
		
		criteria.addOrder(Order.asc(MpmModeloBasicoPrescricao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Retorna os modelos básicos públicos
	 * 
	 * @return
	 */
	public Long pequisarModelosPublicosCount(String descricaoModelo, String descricaoCentroCusto) {
		
		DetachedCriteria criteria = obterCriteriaModeloBasicoPrescricao(descricaoModelo, descricaoCentroCusto);

		return executeCriteriaCount(criteria);
		
	}
	
	private DetachedCriteria obterCriteriaModeloBasicoPrescricao(String descricaoModelo, String descricaoCentroCusto) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoPrescricao.class, "MBP");
			
		criteria.createAlias("MBP." + MpmModeloBasicoPrescricao.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "CCA", JoinType.FULL_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "CCL", JoinType.FULL_JOIN);
			
		if (descricaoCentroCusto != null) {
			criteria.add(Restrictions.or(Restrictions.eq("CCA." + FccCentroCustos.Fields.DESCRICAO.toString(), descricaoCentroCusto),
					Restrictions.eq("CCL." + FccCentroCustos.Fields.DESCRICAO.toString(), descricaoCentroCusto)));
		}
		
		if (descricaoModelo != null && StringUtils.isNotBlank(descricaoModelo)) {
			criteria.add(Restrictions.ilike("MBP." + MpmModeloBasicoPrescricao.Fields.DESCRICAO.toString(), descricaoModelo , MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(MpmModeloBasicoPrescricao.Fields.IND_PUBLICO.toString(), Boolean.TRUE));
		return criteria;
	}

}
