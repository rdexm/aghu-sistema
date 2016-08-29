package br.gov.mec.aghu.ambulatorio.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AacConsultaProcedHospitalarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultaProcedHospitalar> {
	
//	private static final Short RETORNO_CONSULTA_PACIENTE_ATENDIDO = 10;
//	private static final Short RETORNO_CONSULTA_REFERENCIA_DEVOLVIDA = 90;

	private static final long serialVersionUID = -4634745152262220249L;

	/**
	 * Método que obtem o número do Procedimento Hospitalar Interno anterior da
	 * consulta/ProcHospitalar para um número de consulta e um indicados de
	 * consulta.
	 * 
	 * @param id
	 * @return
	 */
	public Integer obterPhiSeqPorNumeroConsulta(final Integer conNumero, final Boolean indConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);
		criteria.setProjection(Projections.projectionList().add(Projections.property(AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString())));
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), conNumero));
		if (indConsulta != null) {
			criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), indConsulta));
		}
		List<Integer> listaPhi = executeCriteria(criteria);
		if (!listaPhi.isEmpty()){
			return listaPhi.get(0);
		}
		return null;
	}

	public List<AacConsultaProcedHospitalar> buscarConsultaProcedHospPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);
		criteria.createAlias(AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString(), "PHI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultaProcedHospitalar.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.addOrder(Order.desc(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AacConsultaProcedHospitalar> buscarConsultaProcedHospPorNumeroConsultaIndicadorConsultaSim(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}
	
	public List<AacConsultaProcedHospitalar> buscarConsultaProcedHospPorNumeroConsultaIndicadorConsultaNao(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}	

	public List<AacConsultaProcedHospitalar> listarConsultasPorComNumeroDescricaoProcedimento(final Object objPesquisa) {
		final DetachedCriteria criteria = obterCriteriaListarConsultas(objPesquisa);
		criteria.addOrder(Order.asc(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString()));

		final List<AacConsultaProcedHospitalar> result = executeCriteria(criteria, 0, 50, new HashMap<String, Boolean>());

		return result;
	}

	public Long listarConsultasPorComNumeroDescricaoProcedimentoCount(final Object objPesquisa) {
		final DetachedCriteria criteria = obterCriteriaListarConsultas(objPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarConsultas(Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);

		if (objPesquisa != null && !StringUtils.isEmpty(objPesquisa.toString())) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.or(
						Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), Integer.valueOf(objPesquisa.toString())),
						Restrictions.eq(AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString(), Integer.valueOf(objPesquisa.toString()))));
			} else {
				criteria.createAlias(AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString(),
						AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString());
				criteria.add(Restrictions.ilike(AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString() + "."
						+ FatProcedHospInternos.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	
	public AacConsultaProcedHospitalar obterConsultaProcedGHospPorNumeroEInd(Integer conNumero, Boolean indConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class);
		criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), conNumero));
		if (indConsulta != null) {
			criteria.add(Restrictions.eq(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), indConsulta));
		}
		return (AacConsultaProcedHospitalar) executeCriteriaUniqueResult(criteria);
	}


}
