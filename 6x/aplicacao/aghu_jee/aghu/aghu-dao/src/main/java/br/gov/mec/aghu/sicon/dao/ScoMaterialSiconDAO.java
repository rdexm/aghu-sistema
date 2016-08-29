package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;

/**
 * @modulo sicon.cadastrosbasicos
 * @author cvagheti
 *
 */
public class ScoMaterialSiconDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMaterialSicon> {

	private static final long serialVersionUID = -6715554457564295574L;

	/**
	 * Retorna listagem de registros da tabela {@code ScoMaterialSicon.}
	 * 
	 * @param _firstResult
	 * @param _maxResult
	 * @param _orderProperty
	 * @param _asc
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Lista com os registros encontrados.
	 */
	public List<ScoMaterialSicon> pesquisarMateriaisSicon(Integer _firstResult,
			Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, ScoMaterial _material,
			DominioSituacao _situacao, ScoGrupoMaterial _grupoMaterial) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		// se um grupo foi selecionado, tem que filtrar os materiais
		// pertencentes a esse grupo

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if (_material != null) {

			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.MATERIAL.toString(), _material));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.SITUACAO.toString(), _situacao));
		}
		
		criteria.createAlias(ScoMaterialSicon.Fields.MATERIAL.toString(),
				"material");
		
		if (_grupoMaterial != null) {
			criteria.add(Restrictions.eq("material."
					+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
					_grupoMaterial));
		}
		
		criteria.addOrder(Order.asc("material." + ScoMaterial.Fields.NOME.toString()));

		return this.executeCriteria(criteria, _firstResult, _maxResult,
				_orderProperty, _asc);
	}

	public ScoMaterialSicon pesquisarMaterialSicon(Integer _codigoSicon,
			ScoMaterial _material, ScoGrupoMaterial _grupoMaterial,
			DominioSituacao _situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		// se um grupo foi selecionado, tem que filtrar os materiais
		// pertencentes a esse grupo

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if (_material != null) {

			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.MATERIAL.toString(), _material));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.SITUACAO.toString(), _situacao));
		}
		if (_grupoMaterial != null) {
			criteria.createAlias(ScoMaterialSicon.Fields.MATERIAL.toString(),
					"material");

			criteria.add(Restrictions.eq("material."
					+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
					_grupoMaterial));
		}

		criteria.createAlias(ScoMaterialSicon.Fields.MATERIAL.toString(),
				"material");

		criteria.addOrder(Order.asc("material." + ScoMaterial.Fields.NOME.toString()));

		return (ScoMaterialSicon) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Recupera a contagem de registros encontrados em {@code ScoMaterialSicon.}
	 * 
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Número indicando o total de registros na tabela
	 */
	public Long listarMaterialSiconCount(Integer _codigoSicon,
			ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if (_material != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.MATERIAL.toString(), _material));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.SITUACAO.toString(), _situacao));
		}
		if (_grupoMaterial != null) {
			criteria.createAlias(ScoMaterialSicon.Fields.MATERIAL.toString(),
					"material");

			criteria.add(Restrictions.eq("material."
					+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
					_grupoMaterial));
		}

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Obter um registro de {@code ScoMaterialSicon} a partir do
	 * {@code codigoSicon}.
	 * 
	 * @param _codigoSicon
	 * @return Registro único a partir do código informado.
	 */
	public ScoMaterialSicon obterPorCodigoSicon(Integer _codigoSicon) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}

		List<ScoMaterialSicon> result = executeCriteria(criteria);

		if (result != null && result.size() > 0) {
			return result.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ScoMaterialSicon> obterPorMaterial(ScoMaterial material) {
		if (material == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		criteria.add(Restrictions.eq(
				ScoMaterialSicon.Fields.MATERIAL.toString(), material));

		return this.executeCriteria(criteria);
	}

	/**
	 * Traz a listagem filtrada de todos os registros de
	 * {@code ScoGrupoMaterial}.
	 * 
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Listagem filtrada.
	 */
	public List<ScoMaterialSicon> listarMateriaisSicon(Integer _codigoSicon,
			ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMaterialSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if (_material != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.MATERIAL.toString(), _material));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoMaterialSicon.Fields.SITUACAO.toString(), _situacao));
		}
		if (_grupoMaterial != null) {
			criteria.createAlias(ScoMaterialSicon.Fields.MATERIAL.toString(),
					"material");

			criteria.add(Restrictions.eq("material."
					+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
					_grupoMaterial));
		}

		criteria.addOrder(Order.asc(ScoMaterialSicon.Fields.CODIGO_SICON
				.toString()));

		return this.executeCriteria(criteria);
	}
}
