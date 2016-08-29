package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * DAO para <code>AghClinicas</code>
 * 
 * @author riccosta
 * 
 */

public class AghClinicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghClinicas> {

	private static final long serialVersionUID = -4527925880644111055L;

	/**
	 * Obter Clinicas.
	 * 
	 * @return
	 */
	public List<AghClinicas> obterTodasClinicas() {
		DetachedCriteria cri = DetachedCriteria.forClass(AghClinicas.class);

		cri.addOrder(Order.asc(AghClinicas.Fields.CODIGO.toString()));
		return executeCriteria(cri);
	}

	public Long pesquisarClinicasCount(String filtro) {
		DetachedCriteria criteria = createPesquisaClinicasCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaClinicasCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion descricaoCriterion = Restrictions.ilike(
					AghClinicas.Fields.DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(
						AghClinicas.Fields.CODIGO.toString(),
						Integer.valueOf(filtro)), descricaoCriterion));
			} else {
				criteria.add(descricaoCriterion);
			}
		}

		return criteria;
	}

	
	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AghClinicas> pesquisarClinicasSolInternacao(String strPesquisa) {
		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para tipos
		// numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException). Se usado Criteria e
		// Restrictions.like para a descricao
		// e for recebido um numero por parametro, também ocorre ClassCastException.
		StringBuilder hql = new StringBuilder(50);

		hql.append("from AghClinicas c ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
			hql.append("where str(c.codigo) = '").append(strPesquisa)
					.append("' ");
			hql.append("or upper(c.descricao) like '%").append(strPesquisa)
					.append("%' ");
		}

		hql.append("order by c.codigo");

		List<AghClinicas> li = (List<AghClinicas>) createHibernateQuery(
				hql.toString()).list();

		return li;
	}

	// public List<AghClinicas> pesquisarClinicas(String filtro) {
	// DetachedCriteria criteria = createPesquisaClinicasCriteria(filtro);
	// return executeCriteria(criteria, 0, 25, null, false);
	// }

	/**
	 * Pesquisa <b>AghClinicas</b> com codigo igual a <i>strPesquisa</i><br>
	 * ou descricao contendo <i>strPesquisa</i>.
	 * 
	 * @dbtables AghClinicas select
	 * @param strPesquisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AghClinicas> pesquisarClinicas(String strPesquisa) {
		final String hql = montarHQLPesquisarClinicas(strPesquisa) + "order by c.descricao";

		final List<AghClinicas> li = (List<AghClinicas>) createHibernateQuery(hql).list();

		return li;
	}

	/*
	 * Optou-se por não alterar o método acima criando um count especifico para o mesmo 
	 */
	@SuppressWarnings("unchecked")
	public Integer pesquisarClinicasHQLCount(String strPesquisa) {
		final String hql = montarHQLPesquisarClinicas(strPesquisa);
		final List<AghClinicas> li = (List<AghClinicas>) createHibernateQuery(hql).list();
		return li.size();
	}
	
	private String montarHQLPesquisarClinicas(String strPesquisa) {
		StringBuilder hql = new StringBuilder(50);

		hql.append("from AghClinicas c ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {

			strPesquisa = CoreUtil.retirarCaracteresInvalidos(strPesquisa);

			strPesquisa = strPesquisa.toUpperCase();
			hql.append("where str(c.codigo) = '").append(strPesquisa)
					.append("' ");
			hql.append("or upper(c.descricao) like '%").append(strPesquisa)
					.append("%' ");
		}
		
		return hql.toString();
	}

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AghClinicas> pesquisarClinicasOrdernadoPorCodigo(
			String strPesquisa) {
		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para tipos
		// numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException). Se usado Criteria e
		// Restrictions.like para a descricao
		// e for recebido um numero por parametro, também ocorre ClassCastException.
		StringBuilder hql = new StringBuilder(50);

		hql.append("from AghClinicas c ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {

			strPesquisa = CoreUtil.retirarCaracteresInvalidos(strPesquisa);

			strPesquisa = strPesquisa.toUpperCase();
			hql.append("where str(c.codigo) = '").append(strPesquisa)
					.append("' ");
			hql.append("or upper(c.descricao) like '%").append(strPesquisa)
					.append("%' ");
		}

		hql.append("order by c.codigo");

		List<AghClinicas> li = (List<AghClinicas>) createHibernateQuery(
				hql.toString()).list();

		return li;
	}

	/**
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @return
	 */
	public AghClinicas obterClinica(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghClinicas.class);
		cri.add(Restrictions.idEq(codigo));

		return (AghClinicas) executeCriteriaUniqueResult(cri);
	}

	private DetachedCriteria createPesquisaCriteria(Integer codigo,
			String descricao, Integer codigoSUS) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AghClinicas.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}

		if (codigoSUS != null) {
			criteria.add(Restrictions.eq(
					AghClinicas.Fields.CODIGO_SUS.toString(), codigoSUS));
		}

		return criteria;
	}

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	//@Restrict("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo,
			String descricao, Integer codigoSUS) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao,
				codigoSUS);
		criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	public Long pesquisaCount(Integer codigo, String descricao,
			Integer codigoSUS) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao,
				codigoSUS);
		return executeCriteriaCount(criteria);
	}

	public AghClinicas obterClinicaPelaDescricaoExata(String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (descricao != null && !descricao.equalsIgnoreCase("")) {
			criteria.add(Restrictions.eq(
					AghClinicas.Fields.DESCRICAO.toString(), descricao).ignoreCase());
		}
		return (AghClinicas) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	//@Restrict("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisarClinicasPorCodigoEDescricao(
			Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AghClinicas.class);

			_criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));

			_criteria.add(Restrictions.eq(
					AghClinicas.Fields.SITUACAO.toString(), DominioSituacao.A));

			List<AghClinicas> list = executeCriteria(_criteria);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AghClinicas.Fields.DESCRICAO.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(AghClinicas.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	
	public List<AghClinicas> obterClinicaCapacidadeReferencial(
			String strPesquisa) {
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(AghClinicas.class);

			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));

			criteria.add(Restrictions.gt(
					AghClinicas.Fields.CAPACIDADE_REFERENCIAL.toString(), 1));

			List<AghClinicas> result = executeCriteria(criteria);

			if (result != null && result.size() == 1) {
				return result;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AghClinicas.Fields.DESCRICAO.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.gt(
				AghClinicas.Fields.CAPACIDADE_REFERENCIAL.toString(), 1));

		criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	public List<AghClinicas> pesquisarClinicasPorCodigoOuDescricao(
			Object objPesquisa, Boolean ordCodigo, Boolean ordDesc) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					Integer.valueOf((String) objPesquisa)));
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(
						AghClinicas.Fields.DESCRICAO.toString(),
						(String) objPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (ordCodigo) {
			criteria.addOrder(Order.asc(AghClinicas.Fields.CODIGO.toString()));
		}

		if (ordDesc) {
			criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));
		}

		return executeCriteria(criteria);
	}

	public Long pesquisarClinicasPorCodigoOuDescricaoCount(Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					Integer.valueOf((String) objPesquisa)));
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(
						AghClinicas.Fields.DESCRICAO.toString(),
						(String) objPesquisa, MatchMode.ANYWHERE));
			}
		}

		return executeCriteriaCount(criteria);
	}
	
	public List<AghClinicas> pesquisarClinicasPorCodigoOuDescricaoSuggestionBox(
			Object objPesquisa, Boolean ordCodigo, Boolean ordDesc) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghClinicas.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(),
					Integer.valueOf((String) objPesquisa)));
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(
						AghClinicas.Fields.DESCRICAO.toString(),
						(String) objPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (ordCodigo) {
			criteria.addOrder(Order.asc(AghClinicas.Fields.CODIGO.toString()));
		}

		if (ordDesc) {
			criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));
		}

		return executeCriteria(criteria, 0, 100, null);
	}

	//@Restrict("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> listarPorNomeOuCodigo(Object paramPesquisa) {
		return pesquisarClinicasOrdernadoPorCodigo((String) paramPesquisa);
	}

	private DetachedCriteria createPesquisaSituacaoLeitosCriteria(AghClinicas clinica) {
		return createPesquisaSituacaoLeitosCriteria(clinica, null, null);
	}

	private DetachedCriteria createPesquisaSituacaoLeitosCriteria(AghClinicas clinica, String orderProperty, Boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghClinicas.class);

		if (clinica != null) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(), clinica.getCodigo()));
		}

		criteria.add(Restrictions.gt(AghClinicas.Fields.CAPACIDADE_REFERENCIAL.toString(), 1));

		if (StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		} else {
			criteria.addOrder(Order.asc(AghClinicas.Fields.DESCRICAO.toString()));
		}

		return criteria;
	}

	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica, String orderProperty, Boolean asc) {
		DetachedCriteria criteria = createPesquisaSituacaoLeitosCriteria(clinica, orderProperty, asc);
		return executeCriteria(criteria);
	}
	
	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica) {
		DetachedCriteria criteria = createPesquisaSituacaoLeitosCriteria(clinica);
		return executeCriteria(criteria);
	}
	
	private Query clinicaReferencial(AghClinicas clinica, boolean count) throws ApplicationBusinessException {
		StringBuffer sql4 = new StringBuffer(400);
		if (count) {
			sql4.append(" select count (*) ");
		} else {
			sql4.append(" select new br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO (");
			sql4.append("	cli.codigo, 0, 'TOT', cli.capacReferencial, ih.mediaPermanencia, ih.percentualOcupacao ");
			sql4.append(" ) ");
		}
		sql4.append(" from AghClinicas cli ");
		sql4.append(" join cli.indicadoresHospitalares as ih ");
		sql4.append(" where cli = :clinica ");
		sql4.append(" and ih.tipoUnidade = :tipoUnidade ");
		sql4.append(" and ih.serVinCodigo is null ");
		sql4.append(" and ih.serMatricula is null ");
		sql4.append(" and ih.unidadeFuncional.seq is null ");
		sql4.append(" and ih.especialidade is null ");
		sql4.append(" and ih.competenciaInternacao = (");
		sql4.append(" 	select max(ih2.competenciaInternacao) ");
		sql4.append(" 	from AinIndicadoresHospitalares ih2  ");
		sql4.append(" 	where ih2.clinica = cli ");
		sql4.append(" ) ");

		Query query = createHibernateQuery(sql4.toString());
		query.setParameter("clinica", clinica);
		query.setParameter("tipoUnidade", DominioTipoUnidade.U);

		return query;
	}

	public Long countClinicaReferencial(AghClinicas clinica) throws ApplicationBusinessException {
		// Count do SQL #4 da view
		Query query = this.clinicaReferencial(clinica, true);
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PesquisaReferencialClinicaEspecialidadeVO> listaClinicaferencial(AghClinicas clinica) throws ApplicationBusinessException {
		// SQL #4 da view
		Query query = this.clinicaReferencial(clinica, false);
		return query.list();
	}
	
	public List<AghClinicas> pesquisarClinicasPorMotivoDesdobramento(Short codigoMotivoDesdobramento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghClinicas.class, "CLI");
		criteria.createAlias("CLI." + AghClinicas.Fields.MOTIVO_DESDOBRAMENTO_CLINICA.toString(), "MDC", JoinType.INNER_JOIN);		
		criteria.add(Restrictions.eq("MDC." + FatMotivoDesdobrClinica.Fields.MDS_SEQ.toString(), codigoMotivoDesdobramento));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Padrão correto da suggestion: Primeiro por código, se nao achar, por descricao. 
	 * @param param
	 * @return
	 */
	public List<AghClinicas> buscarClinicasSb(Object param) {
		List<AghClinicas> retorno = new ArrayList<AghClinicas>();
		DetachedCriteria criteria = DetachedCriteria.forClass(AghClinicas.class);
		
		//Busca por codigo
		if (param != null && CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq(AghClinicas.Fields.CODIGO.toString(), Integer.valueOf(param.toString())));
			retorno =  executeCriteria(criteria);
		}
		
		//Se não encontrar, busca por descrição.
		if (retorno == null || retorno.isEmpty()) {
			criteria = DetachedCriteria.forClass(AghClinicas.class);
			if (param != null) {
				criteria.add(Restrictions.ilike(AghClinicas.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
			}
			criteria.addOrder(Order.asc(AghClinicas.Fields.CODIGO.toString()));
			retorno =  executeCriteria(criteria);
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Padrão correto da suggestion COUNT: Primeiro por código, se nao achar, por descricao. 
	 * @param param
	 * @return
	 */
	public Long buscarClinicasSbCount(Object param) {
		List<AghClinicas> retorno = buscarClinicasSb(param);
		Long count = Long.valueOf(0);
		if (retorno != null) {
			count = Long.valueOf(retorno.size());
		}
		return count;
	}
	
}
