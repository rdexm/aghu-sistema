package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaGas;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaGas;


public class MbcOcorrenciaFichaGasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcOcorrenciaFichaGas> {
	
	
	private static final long serialVersionUID = 3739704305925482917L;

	public List<MbcOcorrenciaFichaGas> listarMbcOcorrenciaFichaGas(
			Integer seqMbcFichaGas) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrenciaFichaGas.class);
		criteria.add(Restrictions.eq(MbcOcorrenciaFichaGas.Fields.MBC_FICHA_GASES.toString() + "." + MbcFichaGas.Fields.SEQ.toString(), seqMbcFichaGas));
		criteria.addOrder(Order.asc(MbcOcorrenciaFichaGas.Fields.DTHR_OCORRENCIA.toString()));
		
		return executeCriteria(criteria);
		
	}

	public List<MbcOcorrenciaFichaGas> listarMbcFichaGasByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrenciaFichaGas.class, "ocorrenciaFichaGas");
		
		criteria.createAlias(MbcOcorrenciaFichaGas.Fields.MBC_FICHA_GASES.toString(), "fichaGases");
		criteria.createAlias("fichaGases." + MbcFichaGas.Fields.MBC_FICHA_ANESTESIAS.toString(), "fichaAnestesia"); 
		
		criteria.add(Restrictions.eq("fichaAnestesia." + MbcFichaAnestesias.Fields.SEQ.toString() , seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}
}
