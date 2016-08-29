package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.model.AelDadosCadaveres;


public class AelDadosCadaveresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDadosCadaveres> {

	private static final long serialVersionUID = 7105508563040198600L;

	public List<AelDadosCadaveres> obterAelDadosCadaveresList(
			AelDadosCadaveres filtros, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		
		final DetachedCriteria criteria = obterCriteriaAelDadosCadaceresList(filtros);
		criteria.createAlias(AelDadosCadaveres.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelDadosCadaveres.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		
		if(!StringUtils.isNotEmpty(orderProperty)){
			orderProperty = AelDadosCadaveres.Fields.NOME.toString();
			asc = true;
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	private DetachedCriteria obterCriteriaAelDadosCadaceresList(final AelDadosCadaveres filtros) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		
		if(filtros != null){
			if(filtros.getSeq() != null){
				criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.SEQ.toString(), filtros.getSeq()));
			}
			
			if(StringUtils.isNotEmpty(filtros.getNome())){
				criteria.add(Restrictions.ilike(AelDadosCadaveres.Fields.NOME.toString(), filtros.getNome(), MatchMode.ANYWHERE));
			}
			
			if(filtros.getDtNascimento() != null){
				criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.DT_NASCIMENTO.toString(), filtros.getDtNascimento()));
			}

			if(filtros.getDthrRemocao() != null){
				criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.DTHR_REMOCAO.toString(), filtros.getDthrRemocao()));
			}
			
			if(StringUtils.isNotEmpty(filtros.getCausaObito())){
				criteria.add(Restrictions.ilike(AelDadosCadaveres.Fields.CAUSA_OBITO.toString(), filtros.getCausaObito(),MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}

	public Long obterAelDadosCadaveresListCount(AelDadosCadaveres filtros) {
		return executeCriteriaCount(obterCriteriaAelDadosCadaceresList(filtros));
	}
	
	public AelDadosCadaveres obterDadosCadaveresPorSeqComConvenioSaude(Integer ddvSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		criteria.createAlias(AelDadosCadaveres.Fields.CONVENIO_SAUDE.toString(), AelDadosCadaveres.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.SEQ.toString(), ddvSeq));
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return null;
		}
		else {
			return (AelDadosCadaveres) obj;
		}
	}
	
	public List<AelDadosCadaveres> obterDadosCadaveresList(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			if(CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AelDadosCadaveres.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc(AelDadosCadaveres.Fields.NOME.toString()));
		
		return executeCriteria(criteria);
	}

	public AelDadosCadaveres obterAelDadosCadaveresPorId(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.SEQ.toString(), seq));

		criteria.createAlias(AelDadosCadaveres.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.INNER_JOIN);
		criteria.createAlias(AelDadosCadaveres.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.INNER_JOIN);
		criteria.createAlias(AelDadosCadaveres.Fields.INSTITUICAO_HOSPITALAR_PROCEDENCIA.toString(), "IHP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelDadosCadaveres.Fields.INSTITUICAO_HOSPITALAR_RETIRADA.toString(), "IHR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelDadosCadaveres.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		
		return (AelDadosCadaveres) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * Retorna DadosCadaveres de procedência associados com instituição hospitalar
	 * 
	 * @return Internacao
	 */
	public List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarProcedencia(final Integer ihoSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.INSTITUICAO_HOSPITALAR_PROCEDENCIA_SEQ.toString(), ihoSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * Retorna DadosCadaveres de procedência associados com instituição hospitalar
	 * 
	 * @return Internacao
	 */
	public List<AelDadosCadaveres> pesquisarDadosCadaveresInstituicaoHospitalarRetirada(final Integer ihoSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
		criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.INSTITUICAO_HOSPITALAR_RETIRADA_SEQ.toString(), ihoSeq));
		return executeCriteria(criteria);
	}

	public Long obterDadosCadaveresListCount(String parametro) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(AelDadosCadaveres.class);
			
			if (StringUtils.isNotBlank(parametro)) {
				if(CoreUtil.isNumeroInteger(parametro)) {
					criteria.add(Restrictions.eq(AelDadosCadaveres.Fields.SEQ.toString(), Integer.valueOf(parametro)));
				} else {
					criteria.add(Restrictions.ilike(AelDadosCadaveres.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
				}
			}
			
			return executeCriteriaCount(criteria);
	}
	
}
