package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.ambulatorio.vo.ProcedHospEspecialidadeVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AacProcedHospEspecialidadesDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacProcedHospEspecialidades> {

	

	private static final long serialVersionUID = 7635630078387204639L;

	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidadesPorEspecialidade(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacProcedHospEspecialidades.class,"PHE");

		criteria.createAlias("PHE."+AacProcedHospEspecialidades.Fields.PHI.toString(),
				"PHI", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq(
				"PHE."+AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));	
		
		criteria.addOrder(Order.asc("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * @ORADB VIEW V_AAC_PROCED_ESPECIALIDADES
	 */
	protected DetachedCriteria listarProcedimentosEspecialidades() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacProcedHospEspecialidades.class);
		criteria.createAlias(AacProcedHospEspecialidades.Fields.ESPECIALIDADE
				.toString(), AacProcedHospEspecialidades.Fields.ESPECIALIDADE
				.toString());
		criteria.createAlias(AacProcedHospEspecialidades.Fields.PHI.toString(),
				AacProcedHospEspecialidades.Fields.PHI.toString());
		criteria.add(Restrictions.eq(
				AacProcedHospEspecialidades.Fields.CONSULTA.toString(), false));
		return criteria;
	}

	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidadesConsulta(
			Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacProcedHospEspecialidades.class);
		criteria.add(Restrictions.eq(
				AacProcedHospEspecialidades.Fields.CONSULTA.toString(), true));
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ
				.toString(), espSeq));
		return executeCriteria(criteria);
	}

	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidades(Object parametro, AghEspecialidades especialidade) {
		DetachedCriteria criteria = this.listarProcedimentosEspecialidades();
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.PHI.toString() + "." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.ESPECIALIDADE.toString()+ "." + AghEspecialidades.Fields.SEQ.toString(), especialidade.getSeq()));
		if (parametro != null) {
			String strParametro = (String) parametro;
			Integer seq = null;
			
			if (CoreUtil.isNumeroInteger(strParametro)){
				seq = Integer.valueOf(strParametro);
			}		

			if (seq != null) {
				criteria.add(Restrictions.eq(
						AacProcedHospEspecialidades.Fields.PHI.toString() + "."
								+ FatProcedHospInternos.Fields.SEQ.toString(),
						seq));
			} else {
				if (StringUtils.isNotBlank(strParametro)) {
					criteria.add(Restrictions.ilike(
							AacProcedHospEspecialidades.Fields.PHI.toString()
									+ "."
									+ FatProcedHospInternos.Fields.DESCRICAO
											.toString(), strParametro
									.toUpperCase(), MatchMode.ANYWHERE));
				}
			}
		}
		return executeCriteria(criteria);

	}
	
	public Long listarProcedimentosEspecialidadesCount(
			Object parametro, AghEspecialidades especialidade) {
		DetachedCriteria criteria = this.listarProcedimentosEspecialidades();
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.PHI
				.toString()
				+ "." + FatProcedHospInternos.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq(
				AacProcedHospEspecialidades.Fields.ESPECIALIDADE.toString()
						+ "." + AghEspecialidades.Fields.SEQ.toString(),
				especialidade.getSeq()));
		if (parametro != null) {
			String strParametro = (String) parametro;
			Integer seq = null;
			
			if (CoreUtil.isNumeroInteger(seq)){
				seq = Integer.valueOf(strParametro);
			}		

			if (seq != null) {
				criteria.add(Restrictions.eq(
						AacProcedHospEspecialidades.Fields.PHI.toString() + "."
								+ FatProcedHospInternos.Fields.SEQ.toString(),
						seq));
			} else {
				if (StringUtils.isNotBlank(strParametro)) {
					criteria.add(Restrictions.ilike(
							AacProcedHospEspecialidades.Fields.PHI.toString()
									+ "."
									+ FatProcedHospInternos.Fields.DESCRICAO
											.toString(), strParametro
									.toUpperCase(), MatchMode.ANYWHERE));
				}
			}
		}
		return executeCriteriaCount(criteria);

	}
	
	private Query criarQueryListarProcedimentosEspecialidadesComGenericas(
			Object parametro, AghEspecialidades especialidade, boolean count) {
		
		String strParametro = null;
		StringBuffer hql = new StringBuffer(330);
		
		if (parametro != null) {
			strParametro = (String) parametro;
		}
		
		if (count) {
			hql.append("select count(distinct php.id.phiSeq)");
		} else {
			hql.append("select distinct php.id.phiSeq, phi." + FatProcedHospInternos.Fields.DESCRICAO 
					+ ", phi." + FatProcedHospInternos.Fields.SITUACAO);
		}
		
		hql.append(" from AacProcedHospEspecialidades php,");
		hql.append(" FatProcedHospInternos phi,");
		hql.append(" AghEspecialidades esp");
		hql.append(" where esp.seq = :espSeq");
		hql.append(" and (php.especialidade.seq = esp.seq  or  php.especialidade.seq = esp.especialidade.seq)");
		hql.append(" and phi.seq = php.procedHospInterno.seq");
		hql.append(" and phi.situacao = '" + DominioSituacao.A.toString() + "'");
		
		if (StringUtils.isNotBlank(strParametro)) {
			if (StringUtils.isNumeric(strParametro)) {
				Integer intParam = Integer.valueOf(strParametro);
				hql.append(" and phi.seq = ").append(intParam);
			}
			else {
				hql.append(" and phi.").append(FatProcedHospInternos.Fields.DESCRICAO).append(" like '%").append(strParametro.toUpperCase()).append("%'");	
			}		
		}		
		
		if (!count) {
			hql.append(" order by php.id.phiSeq");	
		}
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("espSeq", especialidade.getSeq());		

		return query;
	}
	
	public List<ProcedHospEspecialidadeVO> listarProcedimentosEspecialidadesComGenericas(
			Object parametro, AghEspecialidades especialidade) {
		Query query = criarQueryListarProcedimentosEspecialidadesComGenericas(parametro, especialidade, false);
		
		List<Object[]> valores = query.list();
		List<ProcedHospEspecialidadeVO> lstRetorno = new ArrayList<ProcedHospEspecialidadeVO>();

		if(valores != null && valores.size() > 0){

			for (Object[] objects : valores) {

				ProcedHospEspecialidadeVO vo = new ProcedHospEspecialidadeVO();

				if(objects[0] != null){
					vo.setPhiSeq(Integer.parseInt(objects[0].toString()));
				}
				if(objects[1] != null){
					vo.setDescricao(objects[1].toString());
				}
				if(objects[2] != null){
					vo.setSituacao(objects[2].toString().equals(DominioSituacao.A.toString()) ? DominioSituacao.A : DominioSituacao.I);
				}
				
				lstRetorno.add(vo);
			}
		}
		
		return lstRetorno;
	}
	
	public Integer listarProcedimentosEspecialidadesComGenericasCount(
			Object parametro, AghEspecialidades especialidade) {
		Query query = criarQueryListarProcedimentosEspecialidadesComGenericas(parametro, especialidade, true);
		Long count = 0L;
		count = (Long) query.uniqueResult();
		
		return count.intValue();
	}
	
	public Long listarProcedimentosEspecialidadeEspGenericaCount(Integer phiSeq, Short espSeq, Short espSeqGen) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacProcedHospEspecialidades.class);
		List<Short> especialidades = new ArrayList<Short>();
		especialidades.add(espSeq);
		especialidades.add(espSeqGen);
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.in(AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ.toString(), especialidades));
		return executeCriteriaCount(criteria);
	}

	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidadeComConsultaPorEspSeq(
			final Short espSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacProcedHospEspecialidades.class);
		
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.CONSULTA.toString(), true));
		
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		
		return executeCriteria(criteria);

	}
	
	public Long quantidadeProcedHospEspecialidadesConsultaPorEspSeq(final Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacProcedHospEspecialidades.class);
		
		criteria.setProjection(Projections.count(AacProcedHospEspecialidades.Fields.PHI_SEQ.toString()));

		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.CONSULTA.toString(), true));
		
		criteria.add(Restrictions.eq(AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		
		return (Long)executeCriteriaUniqueResult(criteria);
	}
	
}