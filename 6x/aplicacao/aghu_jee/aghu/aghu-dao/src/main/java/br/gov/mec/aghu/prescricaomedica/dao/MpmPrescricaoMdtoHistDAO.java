package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoMdtosHist;

public class MpmPrescricaoMdtoHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoMdtosHist> {
	
	private static final long serialVersionUID = 5812839069173507603L;
	
	public List<MpmPrescricaoMdtosHist> pesquisaMedicamentosHistPOL(Integer atdSeq) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(MpmPrescricaoMdtosHist.class);

		cri.add(Restrictions.eq(MpmPrescricaoMdtosHist.Fields.ATD_SEQ.toString(),
				atdSeq));
		
		cri.add(Restrictions.in(
				MpmPrescricaoMdtosHist.Fields.INDPENDENTE.toString(),
				DominioIndPendenteItemPrescricao.values()));

		cri
				.addOrder(Order.asc(MpmPrescricaoMdtosHist.Fields.DTHR_INICIO
						.toString()));
		
		return executeCriteria(cri);
	}
	
}
