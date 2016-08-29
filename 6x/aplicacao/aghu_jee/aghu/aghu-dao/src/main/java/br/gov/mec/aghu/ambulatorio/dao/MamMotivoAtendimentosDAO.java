package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamMotivoAtendimento;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_MOTIVO_ATENDIMENTO.
 * 
 */
public class MamMotivoAtendimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamMotivoAtendimento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4868730947460137745L;

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamMotivoAtendimento buscarMovitoAtendimentoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamMotivoAtendimento.class);

		criteria.add(Restrictions.eq(MamMotivoAtendimento.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamMotivoAtendimento> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
