package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.farmacia.vo.MpmPrescricaoMedVO;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.core.utils.DateUtil;


public class MpmPrescricaoMedicaAuxiliarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoMedica> {

	private static final long serialVersionUID = -1877072641650786475L;

	/**
	 * #5795 - c2
	 * @param seq
	 * @return
	 */
	public List<MpmPrescricaoMedVO> obterDataReferenciaPrescricaoMedica(Integer seq, String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "pme");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pme."+MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()),
						MpmPrescricaoMedVO.Fields.DT_REFERENCIA.toString())
				.add(Projections.property("pme."+MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()),
						MpmPrescricaoMedVO.Fields.DTHR_INICIO.toString())
				.add(Projections.property("pme."+MpmPrescricaoMedica.Fields.DTHR_FIM.toString()),
						MpmPrescricaoMedVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("pme."+MpmPrescricaoMedica.Fields.ATD_SEQ.toString()),
						MpmPrescricaoMedVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("pme."+MpmPrescricaoMedica.Fields.SEQ.toString()),
						MpmPrescricaoMedVO.Fields.SEQ.toString())
				);
		
		criteria.add(Restrictions.eq("pme."+MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), seq));
		if(descricao != null){
			if(StringUtils.isNotBlank(descricao)){
				int dia = Integer.valueOf(descricao.substring(0, 2));
				int mes = Integer.valueOf(descricao.substring(3, 5));
				int ano = Integer.valueOf(descricao.substring(6));
				
				Date data = DateUtil.obterData(ano, mes-1, dia);
				criteria.add(Restrictions.eq("pme."+MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), data));
			}
		}
		
		criteria.addOrder(Order.desc("pme."+MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()));
		criteria.addOrder(Order.desc("pme."+MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MpmPrescricaoMedVO.class));
	
		return executeCriteria(criteria);
	}

	public MpmPrescricaoMedica obterPrescricaoMedicaPorId(Integer seq, Integer atdSeq){
				
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "MPM");
		criteria.add(Restrictions.eq("MPM."+MpmPrescricaoMedica.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("MPM."+MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		
		return (MpmPrescricaoMedica) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #5799
	 * C2 - Consulta para retornar a descrição da unidade funcional
	 */
	public MpmPrescricaoMedica obterValoresPrescricaoMedica(Integer atdSeqPme, Integer seqPme){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "PME");
		
		criteria.add(Restrictions.eq("PME."+MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeqPme));
		criteria.add(Restrictions.eq("PME."+MpmPrescricaoMedica.Fields.SEQ.toString(), seqPme));
		
		return (MpmPrescricaoMedica) executeCriteriaUniqueResult(criteria);
	}
}