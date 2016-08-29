package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.MciMicroorgPatologiaExame;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;

public class MciMicroorgPatologiaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMicroorgPatologiaExame> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1646719479044693255L;


	// #1326 c2
	public  List<MciMicroorgPatologiaExame> buscarMicroorgExamesPorPatologia(MciMicroorganismoPatologia microorganismoPatologia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMicroorgPatologiaExame.class, "MPE");
		criteria.createAlias("MPE." + MciMicroorgPatologiaExame.Fields.MCI_MICROORGANISMO_PATOLOGIAS.toString(), "MPT");
		criteria.createAlias("MPE." + MciMicroorgPatologiaExame.Fields.AEL_RESULTADO_CODIFICADO.toString(), "RCD");


		return executeCriteria(criteria);
	}


}
