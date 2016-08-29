package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.MamRecCuidPreferidoVO;
import br.gov.mec.aghu.model.MamRecCuidPreferido;
import br.gov.mec.aghu.model.RapServidores;


public class MamRecCuidPreferidoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRecCuidPreferido> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7369627428940936805L;
	
	
	private static final String ATIVO = "A";

	
	public List<MamRecCuidPreferidoVO> listarCuidadosPreferidos(RapServidores servidor,boolean ativo) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(MamRecCuidPreferido.class);

			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(MamRecCuidPreferido.Fields.SEQ.toString()), MamRecCuidPreferidoVO.Fields.SEQ.toString())
					.add(Projections.property(MamRecCuidPreferido.Fields.DESCRICAO.toString()), MamRecCuidPreferidoVO.Fields.DESCRICAO.toString())
				);
		
			criteria.add(Restrictions.eq(MamRecCuidPreferido.Fields.RAP_SERVIDORES.toString(), servidor));
			if(ativo){
				criteria.add(Restrictions.eq(MamRecCuidPreferido.Fields.IND_SITUACAO.toString(), ATIVO));
			}
			
			criteria.setResultTransformer(Transformers.aliasToBean(MamRecCuidPreferidoVO.class));
			return  executeCriteria(criteria);
	}
	
	
			
}


