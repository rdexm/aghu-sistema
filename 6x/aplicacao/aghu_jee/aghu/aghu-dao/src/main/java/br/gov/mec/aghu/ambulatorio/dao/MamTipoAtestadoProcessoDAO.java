package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamTipoAtestadoProcesso;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTipoAtestadoProcessoDAO extends BaseDao<MamTipoAtestadoProcesso>{

	private static final long serialVersionUID = 8822716187786806337L;
	
	/**
	 * #43023 - C2 - P2 cur_processos 
	 * @param tasSeq
	 * @return List<MamTipoAtestadoProcesso>
	 */
	public List<MamTipoAtestadoProcesso> obterListaTipoAtestadoProcessoPorTipoAtestado(Short tasSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoAtestadoProcesso.class);
		
		criteria.add(Restrictions.eq(MamTipoAtestadoProcesso.Fields.ID_TAS_SEQ.toString(), tasSeq));
		criteria.add(Restrictions.eq(MamTipoAtestadoProcesso.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		
		return executeCriteria(criteria);
				
	}

}
