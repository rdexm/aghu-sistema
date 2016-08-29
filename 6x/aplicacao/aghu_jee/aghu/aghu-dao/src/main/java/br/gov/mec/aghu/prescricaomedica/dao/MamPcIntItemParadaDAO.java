package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamPcIntItemParada;


public class MamPcIntItemParadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcIntItemParada> {
	
	private static final long serialVersionUID = -8927104182609654006L;

	/**
	 * Método para pesquisa de entidades MamPcIntItemParada por atendimento.
	 * @param seqAtendimento
	 * @return
	 */
	public List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPcIntItemParada.class);
		criteria.add(Restrictions.eq(MamPcIntItemParada.Fields.PIR_ATD_SEQ.toString(), atendimento.getSeq()));
		
		List<MamPcIntItemParada> lista = this.executeCriteria(criteria);

		return lista;

	}
	
	public List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(Integer atdSeq, Date dthrCriacao, Float ordem, Boolean isOrdemMaiorIgual) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
		
		dc.add(Restrictions.eq(MamPcIntItemParada.Fields.PIR_ATD_SEQ.toString(), atdSeq));
		dc.add(Restrictions.eq(MamPcIntItemParada.Fields.PIR_DTHR_CRIACAO.toString(), dthrCriacao));
		
		if (!isOrdemMaiorIgual) {
			dc.add(Restrictions.lt(MamPcIntItemParada.Fields.ORDEM.toString(), ordem));
		} else {
			dc.add(Restrictions.ge(MamPcIntItemParada.Fields.ORDEM.toString(), ordem));
		}
		
		dc.addOrder(Order.asc(MamPcIntItemParada.Fields.ORDEM.toString()));
		dc.addOrder(Order.asc(MamPcIntItemParada.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(dc);
	}

}
