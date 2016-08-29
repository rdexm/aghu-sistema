package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaFarmaco;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaFarmaco;

public class MbcOcorrenciaFichaFarmacoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcOcorrenciaFichaFarmaco> {
	
	
	private static final long serialVersionUID = -8473406767509563812L;

	public List<MbcOcorrenciaFichaFarmaco> listarMbcFichaFarmacosByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrenciaFichaFarmaco.class, "ocorrenciaFichaFarmaco");
		
		criteria.createAlias(MbcOcorrenciaFichaFarmaco.Fields.MBC_FICHA_FARMACO.toString(), "FFA");
		criteria.createAlias("FFA." + MbcFichaFarmaco.Fields.MBC_FICHA_ANESTESIAS.toString(), "FIC"); 
		
		criteria.add(Restrictions.eq("FIC." + MbcFichaAnestesias.Fields.SEQ.toString() , seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
		
	}

	public MbcOcorrenciaFichaFarmaco obterMbcOcorrenciaFichaFarmacoBySeq(
			Integer seqMbcOcorrenciaFichaFarmaco) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrenciaFichaFarmaco.class, "OCO");
		
		criteria.createAlias(MbcOcorrenciaFichaFarmaco.Fields.MBC_FICHA_FARMACO.toString(), "FFA");
		criteria.createAlias("FFA." + MbcFichaFarmaco.Fields.MEDICAMENTO.toString(), "MED", criteria.LEFT_JOIN); 
		criteria.createAlias("FFA." + MbcFichaFarmaco.Fields.FORMA_DOSAGEM.toString(), "FDS", criteria.LEFT_JOIN);
		criteria.createAlias("MED." + AfaMedicamento.Fields.TPR.toString(), "TPR", criteria.LEFT_JOIN);
		criteria.createAlias("FDS." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "UMM", criteria.LEFT_JOIN);
		criteria.createAlias("FFA." + MbcFichaFarmaco.Fields.UNIDADE_MEDIDA_MEDICA.toString(), "UMM2", criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(MbcOcorrenciaFichaFarmaco.Fields.SEQ.toString(), seqMbcOcorrenciaFichaFarmaco));
		
		List<MbcOcorrenciaFichaFarmaco> fichas = executeCriteria(criteria);
		return !fichas.isEmpty() ? fichas.get(0) : null;
	}
}
