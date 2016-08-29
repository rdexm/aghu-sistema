package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;

public class AinTiposAltaMedicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinTiposAltaMedica> {
	
	private static final long serialVersionUID = 7778732196597507087L;

	/**
	 * Retorna um TipoAltaMedica com base na chave primária.
	 * 
	 * @param seq
	 * @return
	 */
	public AinTiposAltaMedica obterTipoAltaMedica(String codigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposAltaMedica.class);
		criteria.add(Restrictions.eq(AinTiposAltaMedica.Fields.CODIGO.toString(), codigo));
		return (AinTiposAltaMedica) executeCriteriaUniqueResult(criteria);

	}

	/**
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaTiposAltaMedica(
			String codigo, MpmMotivoAltaMedica motivoAltaMedica,
			DominioSituacao indSituacao) {
		DetachedCriteria criteria = obterCriteriaTiposAltaMedica();

		if (!StringUtils.isBlank(codigo)) {
			criteria.add(Restrictions.ilike(
					AinTiposAltaMedica.Fields.CODIGO.toString(), codigo));
		}
		if (motivoAltaMedica != null && motivoAltaMedica.getSeq() != null) {
			criteria.add(Restrictions.eq(
					AinTiposAltaMedica.Fields.MOTIVO_ALTA_MEDICA.toString(),
					motivoAltaMedica.getSeq()));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(
					AinTiposAltaMedica.Fields.IND_SITUACAO.toString(),
					indSituacao));
		}
		return criteria;
	}

	/**
	 * @return
	 */
	private DetachedCriteria obterCriteriaTiposAltaMedica() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposAltaMedica.class);
		criteria.createAlias(AinTiposAltaMedica.Fields.MOTIVO_ALTA_MEDICAS.toString(), AinTiposAltaMedica.Fields.MOTIVO_ALTA_MEDICAS.toString(), JoinType.LEFT_OUTER_JOIN);
		return criteria;
	}
	
	/**
	 * Busca tipos de altas médicas, conformes os parâmetros passados.
	 * 
	 * @param codigo
	 * @param motivoAltaMedica
	 * @param indSituacao
	 * @return List<AinTiposAltaMedica>
	 */
	public List<AinTiposAltaMedica> pesquisaTiposAltaMedica(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigo, MpmMotivoAltaMedica motivoAltaMedica,
			DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaPesquisaTiposAltaMedica(
				codigo, motivoAltaMedica, situacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisaTiposAltaMedicaCount(String codigo,
			MpmMotivoAltaMedica motivoAltaMedica, DominioSituacao situacao) {
		return executeCriteriaCount(obterCriteriaPesquisaTiposAltaMedica(
				codigo, motivoAltaMedica, situacao));
	}

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a descrição
	 * passada como parêmetro. É utilizado pelo converter
	 * MpmMotivoAltaMedicasConverter.
	 * 
	 * @param descricao
	 * @return MpmMotivoAltaMedicas
	 */
	public MpmMotivoAltaMedica pesquisarMotivosAltaMedicaPorDescricao(String descricao) {
		MpmMotivoAltaMedica motivoAltaMedica = null;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmMotivoAltaMedica.class);

		if (descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.ilike(
					MpmMotivoAltaMedica.Fields.DESCRICAO.toString(), descricao));

			motivoAltaMedica = (MpmMotivoAltaMedica) executeCriteriaUniqueResult(criteria);
		}

		return motivoAltaMedica;
	}

	/**
	 * Metodo que busca objetos MotivosAltasMedicas pela descricao OU seq.
	 */
	@SuppressWarnings("unchecked")
	public List<MpmMotivoAltaMedica> pesquisarMotivosAltaMedica(String strPesq) {
		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para tipos
		// numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException). Se usado Criteria e
		// Restrictions.like para a descricao
		// e for recebido um numero por parametro, também ocorre ClassCastException.
		StringBuilder hql = new StringBuilder(50);

		hql.append("from MpmMotivoAltaMedica m ");

		if (strPesq != null && !"".equals(strPesq)) {
			strPesq = strPesq.toUpperCase();
			hql.append("where str(m.seq) = '").append(strPesq).append("' ");
			hql.append("or upper(m.descricao) like '%").append(strPesq)
					.append("%' ");
		}

		hql.append("order by m.seq");

		List<MpmMotivoAltaMedica> li = (List<MpmMotivoAltaMedica>) createHibernateQuery(hql.toString()).list();

		return li;
	}
	
	
	
	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(String objPesquisa) {
		return pesquisarTipoAltaMedicaPorCodigoEDescricao(objPesquisa, null);
	}
	
	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parâmetro, que é comparada com o codigo e a descricao do
	 * Tipo Alta Médica É utilizado pelo converter AinTiposAltaMedicaConverter.
	 * 
	 * @param descricao
	 *            ou codigo, idsFiltrados (ids de tipos alta medica que podem
	 *            estar entre os retornados pela consulta)
	 * @return Lista de Tipos Alta Médica
	 */
	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(String objPesquisa, String[] idsFiltrados) {
		String strPesquisa = objPesquisa;
		List<AinTiposAltaMedica> list = null;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposAltaMedica.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AinTiposAltaMedica.Fields.CODIGO.toString(), strPesquisa,
					MatchMode.ANYWHERE));
			if (idsFiltrados != null && idsFiltrados.length > 0) {
				criteria.add(Restrictions.in(
						AinTiposAltaMedica.Fields.CODIGO.toString(),
						idsFiltrados));
			}
			list = executeCriteria(criteria);
		}

		if (list != null && list.size() > 0) {
			return list;
		}

		DetachedCriteria crit = DetachedCriteria
				.forClass(AinTiposAltaMedica.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			crit.add(Restrictions.ilike(
					AinTiposAltaMedica.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		if (idsFiltrados != null && idsFiltrados.length > 0) {
			crit.add(Restrictions.in(
					AinTiposAltaMedica.Fields.CODIGO.toString(), idsFiltrados));
		}
		list = executeCriteria(crit);

		return list;

	}

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parêmetro, que é comparada com o codigo
	 * 
	 * @param codigo
	 *            , idsIgnorados (ids de tipos alta medica que não devem ser
	 *            retornados pela consulta)
	 * @return Tipo Alta Médica
	 */
	public AinTiposAltaMedica pesquisarTipoAltaMedicaPorCodigo(String objPesquisa, String[] idsIgnorados) {
		String strPesquisa = objPesquisa;
		AinTiposAltaMedica tam = null;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposAltaMedica.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AinTiposAltaMedica.Fields.CODIGO.toString(), strPesquisa,
					MatchMode.EXACT));

			if (idsIgnorados != null && idsIgnorados.length > 0) {
				criteria.add(Restrictions.not(Restrictions.in(
						AinTiposAltaMedica.Fields.CODIGO.toString(),
						idsIgnorados)));
			}
			tam = (AinTiposAltaMedica) executeCriteriaUniqueResult(criteria);
		}

		return tam;
	}
	
	
}
