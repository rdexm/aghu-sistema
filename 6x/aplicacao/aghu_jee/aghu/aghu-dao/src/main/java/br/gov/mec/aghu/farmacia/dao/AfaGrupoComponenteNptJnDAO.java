package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;


/**
 * @author marcelo.corati
 *
 */
public class AfaGrupoComponenteNptJnDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoComponNptJn> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4804267401912493291L;


	public List<AfaGrupoComponNptJn> listarPorSeqGrupo(Short gcnSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoComponNptJn.class);
	
		criteria.add(Restrictions.eq(AfaGrupoComponNptJn.Fields.SEQ.toString(), gcnSeq));
		
		//criteria.setResultTransformer(Transformers.aliasToBean(AfaGrupoComponNptJn.class));
		return this.executeCriteria(criteria);
	}
	

		
}
