/**
 * 
 */
package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CsePerfilProcessos;

/**
 * @author aghu
 *
 */
public class CsePerfilProcessosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CsePerfilProcessos> {
	
	private static final long serialVersionUID = -683561398841272811L;

	/**
	 * 
	 * 
	 * @param indConsulta
	 * @param indSituacao
	 * @param paramSeqProcConsPol
	 * @return
	 */
	public List<CsePerfilProcessos> pesquisarPerfisProcesso(Boolean indConsulta, 
															DominioSituacao indSituacao, 
															Short paramSeqProcConsPol) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CsePerfilProcessos.class);
		if(indConsulta != null) {
			criteria.add(Restrictions.eq(CsePerfilProcessos.Fields.IND_CONSULTA.toString(), indConsulta));
		}
		criteria.add(Restrictions.eq(CsePerfilProcessos.Fields.SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.eq(CsePerfilProcessos.Fields.ROC_SEQ.toString(), paramSeqProcConsPol));
		return executeCriteria(criteria);
	}

}
