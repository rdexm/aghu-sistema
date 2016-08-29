package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamPcIntParada;

public class MamPcIntParadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcIntParada> {
	
	private static final long serialVersionUID = 1031804647918727294L;

	/**
	 * Método para pesquisa de entidades MamPcIntItemParada por atendimento.
	 * @param seqAtendimento
	 * @return
	 */
	public List<MamPcIntParada> pesquisarPcIntParadaPorAtendimento(AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPcIntParada.class);
		criteria.add(Restrictions.eq(MamPcIntParada.Fields.AGH_ATENDIMENTOS.toString(), atendimento));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa pelo ID do Atendimento
	 * @param atendimento
	 * @return
	 * @author bruno.mourao
	 * @since 11/09/2012
	 */
	public List<MamPcIntParada> pesquisarParadaInternacaoPorAtendimento(Integer atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPcIntParada.class,"MIP");
		
		criteria.createAlias("MIP."+ MamPcIntParada.Fields.AGH_ATENDIMENTOS.toString(), "ATD");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atendimento));
		return this.executeCriteria(criteria);
	}

}
