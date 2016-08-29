package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatProcedimentoHospitalarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedimentosHospitalares> {

	private static final long serialVersionUID = 3491596114079543036L;

	private DetachedCriteria obterDetachedCriteriaFatProcedimentosHospitalares(){
		return DetachedCriteria.forClass(FatProcedimentosHospitalares.class);
	}

	/**
	 * Lista procedimentos hospitalares por descricao e seq.
	 * @param objPesquisa
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarContultaProcedimentosHospitalaresPorSeqEDescricao(final Object objPesquisa){

		final DetachedCriteria criteria = obterDetachedCriteriaFatProcedimentosHospitalares();

		if(CoreUtil.isNumeroShort(objPesquisa)) {
			if(objPesquisa instanceof Short) {
				criteria.add(Restrictions.eq(FatProcedimentosHospitalares.Fields.SEQ.toString(), (Short)objPesquisa));
			}
			else {
				criteria.add(Restrictions.eq(FatProcedimentosHospitalares.Fields.SEQ.toString(), Short.valueOf((String)objPesquisa)));
			}
		}
		else {
			if(!StringUtils.isEmpty((String)objPesquisa)) {
				criteria.add(Restrictions.ilike(FatProcedimentosHospitalares.Fields.DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}

		return criteria;

	}

	/**
	 * Lista procedimentos hospitalares por descricao e seq.
	 * @param objPesquisa
	 * @return List<FatProcedimentosHospitalares>
	 */
	public List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricao(final Object objPesquisa) {

		final DetachedCriteria criteria = this.montarContultaProcedimentosHospitalaresPorSeqEDescricao(objPesquisa);

		return executeCriteria(criteria, 0, 100, null, true);

	}
	/**
	 * Count de procedimentos hospitalares filtrando por descricao e seq.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarProcedimentosHospitalaresPorSeqEDescricaoCount(final Object objPesquisa){

		final DetachedCriteria criteria = this.montarContultaProcedimentosHospitalaresPorSeqEDescricao(objPesquisa);

		return executeCriteriaCount(criteria);

	}

	/**
	 * Lista procedimentos hospitalares por descricao e seq, ordena pelo campo passado como parâmetro
	 * @param objPesquisa
	 * @param ordem
	 * @return List<FatProcedimentosHospitalares>
	 */
	public List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(final Object objPesquisa, String ordem) {

		final DetachedCriteria criteria = this.montarContultaProcedimentosHospitalaresPorSeqEDescricao(objPesquisa);
		criteria.addOrder(Order.asc(ordem));
		
		return executeCriteria(criteria);

	}

}
