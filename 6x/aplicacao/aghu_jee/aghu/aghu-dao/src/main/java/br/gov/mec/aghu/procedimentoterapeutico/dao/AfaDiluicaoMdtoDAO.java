package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaDiluicaoMdto;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AfaDiluicaoMdtoDAO extends BaseDao<AfaDiluicaoMdto> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1988508031071560452L;

	/**
	 * @author marcelo.deus
	 * #44281 - P1 - cur_dil
	 */
	public AfaDiluicaoMdto buscarDiluicaoMdtoPorCodMaterialEData(Integer codMaterial, Date dataInicio){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDiluicaoMdto.class);
		
		if(!isOracle()){
			criteria.add(Restrictions.eq(AfaDiluicaoMdto.Fields.MED_MAT_CODIGO.toString(), codMaterial))
			.add(Restrictions.sqlRestriction("'" + DateUtil.truncaData(dataInicio) +"'" + 
					"  BETWEEN  dthr_inicio  AND COALESCE(dthr_fim, '" + DateUtil.truncaData(dataInicio) + "' ) "));
		} else {
			criteria.add(Restrictions.eq(AfaDiluicaoMdto.Fields.MED_MAT_CODIGO.toString(), codMaterial))
			.add(Restrictions.sqlRestriction(" TO_DATE('" + DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY") + 
					"') BETWEEN DTHR_INICIO AND NVL(DTHR_FIM, TO_DATE('" + DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY") + " ')) "));
		}
		return (AfaDiluicaoMdto) executeCriteriaUniqueResult(criteria);
	}
}
