package br.gov.mec.aghu.controlepaciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
public class EcpGrupoControleDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpGrupoControle> {

	private static final long serialVersionUID = -2330568756627811554L;

	public List<EcpGrupoControle> pesquisarGruposControle(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short ordem, DominioSituacao situacao , DominioTipoGrupoControle tipo) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(EcpGrupoControle.class);
		
		if (seq != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SEQ.toString(), seq));
		}
		if ((descricao != null) && (StringUtils.isNotBlank(descricao))) {
			criteria.add(Restrictions.ilike(
					EcpGrupoControle.Fields.DESCRICAO.toString(), descricao.trim(),
					MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SITUACAO.toString(), situacao));
		}
		
		if (tipo != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.TIPO.toString(), tipo));
		}
		
		criteria.addOrder(Order.asc(EcpGrupoControle.Fields.ORDEM.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	public Long listarGruposControleCount(Integer _seq, String _descricao,
			Short _ordem, DominioSituacao _situacao, DominioTipoGrupoControle tipo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpGrupoControle.class);

		if (_seq != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SEQ.toString(), _seq));
		}
		if ((_descricao != null) && (StringUtils.isNotBlank(_descricao))) {
			criteria.add(Restrictions.ilike(
					EcpGrupoControle.Fields.DESCRICAO.toString(), _descricao,
					MatchMode.ANYWHERE));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SITUACAO.toString(), _situacao));
		}
		
		if (tipo != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.TIPO.toString(), tipo));
		}

		return this.executeCriteriaCount(criteria);
	}

	public List<EcpGrupoControle> listarGruposAtivos() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpGrupoControle.class);
		criteria.add(Restrictions.eq(
				EcpGrupoControle.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EcpGrupoControle.Fields.ORDEM
				.toString()));
		return this.executeCriteria(criteria);
	}

	/**
	 * Lista todos os grupos de controle cadastrados
	 * 
	 * @return listagem gerada pela criteria
	 */
	public List<EcpGrupoControle> listarGruposControle(
			Integer _grupoControleSeq, String _descricao, Short _ordem,
			DominioSituacao _situacao, DominioTipoGrupoControle tipo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpGrupoControle.class);

		if (_grupoControleSeq != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SEQ.toString(), _grupoControleSeq));
		}
		if (_descricao != null) {
			criteria.add(Restrictions.ilike(
					EcpGrupoControle.Fields.DESCRICAO.toString(), _descricao,
					MatchMode.ANYWHERE));
		}
		if (_ordem != null) {
				criteria.add(Restrictions.eq(
						EcpGrupoControle.Fields.ORDEM.toString(), _ordem));	
		
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.SITUACAO.toString(), _situacao));
		}
		
		if (tipo != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.TIPO.toString(), tipo));
		}

		criteria.addOrder(Order.asc(EcpGrupoControle.Fields.ORDEM.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Lista todos os itens de controle que referenciam um determinado grupo de
	 * controle.
	 * 
	 * @param _grupoControle
	 *            Chave do grupo de controle que se quer pesquisar
	 * @return Lista com todos os itens referenciados no grupo informado pela
	 *         chave {@code _grupoControle}
	 */
	public List<EcpItemControle> listarItensGrupoControle(
			EcpGrupoControle _grupoControle) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpItemControle.class);

		criteria.add(Restrictions.eq(EcpItemControle.Fields.GRUPO.toString(),
				_grupoControle));

		return this.executeCriteria(criteria);
	}

	public EcpGrupoControle obterDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpGrupoControle.class);

		if (descricao != null && !descricao.equalsIgnoreCase("")) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.DESCRICAO.toString(), descricao));
		}
		return (EcpGrupoControle) executeCriteriaUniqueResult(criteria);
	}

	public EcpGrupoControle obterOrdem(Short ordem) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpGrupoControle.class);

		if (ordem != null) {
			criteria.add(Restrictions.eq(
					EcpGrupoControle.Fields.ORDEM.toString(), ordem));
		}
		return (EcpGrupoControle) executeCriteriaUniqueResult(criteria);
	}

}
