package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamPcControlePac;
import br.gov.mec.aghu.model.MamPcHorarioCtrl;
import br.gov.mec.aghu.model.MamPcIntParada;

/**
 * 
 * @author daniel.silva
 * @since 02/10/2012
 *
 */
public class MamPcControlePacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcControlePac> {
	
	private static final long serialVersionUID = 8718458461837916462L;

	/**
	 * Pesquisa Controles de Paciente por atendimento e data hora criação.
	 * 
	 * @see Pesquisa implementada para atender a Q_CONTROLE da estoria #15834
	 * @param atdSeq
	 * @param dthrCriacao
	 * @return
	 */
	public List<MamPcControlePac> pesquisarControlePaciente(Integer atdSeq, Date dthrCriacao) {
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "C");		
		
		dc.createAlias("C.".concat(MamPcControlePac.Fields.MAM_PC_HORARIO_CTRLS.toString()), "H");
		dc.createAlias("H.".concat(MamPcHorarioCtrl.Fields.MAM_PC_INT_PARADAS.toString()), "P");
		
		dc.add(Restrictions.eqProperty("C.".concat(MamPcControlePac.Fields.MAM_PC_INT_PARADAS.toString()), "H.".concat(MamPcHorarioCtrl.Fields.MAM_PC_INT_PARADAS.toString())));
		dc.add(Restrictions.eq("P.".concat(MamPcIntParada.Fields.PIR_ATD_SEQ.toString()), atdSeq));
		dc.add(Restrictions.eq("P.".concat(MamPcIntParada.Fields.PIR_DTHR_CRIACAO.toString()), dthrCriacao));
		
		dc.addOrder(Order.desc("H.".concat(MamPcHorarioCtrl.Fields.DATA_HORA.toString())));
		
		return executeCriteria(dc);
	}
}
