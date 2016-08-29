package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MbcFichaFarmaco;

public class MbcFichaFarmacoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaFarmaco> {
	
	private static final long serialVersionUID = 6720736428579057208L;

	public List<MbcFichaFarmaco> listarFichaFarmacoPorFicSeq(Long ficSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFarmaco.class, "fichaFarmacos");
		
		criteria.createAlias(MbcFichaFarmaco.Fields.MEDICAMENTO.toString(), "medicamento", Criteria.LEFT_JOIN);
		criteria.createAlias("medicamento." + AfaMedicamento.Fields.TPR.toString(), "tprSigla", Criteria.LEFT_JOIN); 
		
		criteria.createAlias(MbcFichaFarmaco.Fields.FORMA_DOSAGEM.toString(), "formaDosagem", Criteria.LEFT_JOIN);
		criteria.createAlias("formaDosagem." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "unidadeMedica", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq(MbcFichaFarmaco.Fields.FIQ_SEQ.toString(), ficSeq));

		return executeCriteria(criteria);
		
		
	}
	


}
