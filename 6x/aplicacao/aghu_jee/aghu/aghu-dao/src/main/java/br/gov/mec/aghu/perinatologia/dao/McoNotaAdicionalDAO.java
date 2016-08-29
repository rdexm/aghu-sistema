package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.model.McoNotaAdicional;

public class McoNotaAdicionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoNotaAdicional> {

	private static final long serialVersionUID = -1849033448352949993L;

	
	
	public Integer obterMaxSeqpMcoNotaAdicional(Integer gsoPacCodigo, Short gsoSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class,"NAD");
		
		criteria.add(Restrictions.eq("NAD." + McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq("NAD." + McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.setProjection(Projections.max("NAD." + McoNotaAdicional.Fields.SEQP.toString()));
		
		Object maxSeqp = this.executeCriteriaUniqueResult(criteria);
		if (maxSeqp != null) {
			return (Integer) maxSeqp + 1;
		}		
		return 1;		
	}
	
	public McoNotaAdicional obterMcoNotaAdicional(Integer pacCodigo, Short gsoSeqp, Byte seqp, Integer conNumero, DominioEventoNotaAdicional evento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class);
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		if (conNumero != null) {
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.CON_NUMERO.toString(), conNumero));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.SEQP.toString(), seqp.intValue()));
		}
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.EVENTO.toString(), evento));
		criteria.addOrder(Order.desc(McoNotaAdicional.Fields.CRIADO_EM.toString()));
		
		List<McoNotaAdicional> listaNotasAdicionais = executeCriteria(criteria);

		if (listaNotasAdicionais != null && listaNotasAdicionais.size() > 0) {
			return listaNotasAdicionais.get(0);
		}		
		
		return null;
	}
	
	public List<McoNotaAdicional> pesquisarMcoNotaAdicional(Integer pacCodigo,
			Short gsoSeqp, Integer conNumero,
			DominioEventoNotaAdicional evento, McoNotaAdicional.Fields ... orders) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class);
		
		if(pacCodigo != null){
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		}
		if(gsoSeqp != null){
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		}
		
		if (conNumero != null) {
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.CON_NUMERO.toString(), conNumero));
		}
		
		if(evento != null){
			criteria.add(Restrictions.eq(McoNotaAdicional.Fields.EVENTO.toString(), evento));
		}
		
		for(McoNotaAdicional.Fields order: orders){
			criteria.addOrder(Order.desc(order.toString()));
		}
		
		return executeCriteria(criteria);
	}

	public List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoEvento(Integer pacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class);
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.EVENTO.toString(), DominioEventoLogImpressao.MCOR_NASCIMENTO));
		criteria.addOrder(Order.desc(McoNotaAdicional.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	public List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class);
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.CON_NUMERO.toString(), conNumero));
		
		return executeCriteria(criteria);
	}	
	
	public McoNotaAdicional obterNotaAdicionalPorPacienteGestacaoSeqp(Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNotaAdicional.class);
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.SEQP.toString(), Integer.valueOf(seqp.toString())));
		criteria.add(Restrictions.eq(McoNotaAdicional.Fields.EVENTO.toString(), DominioEventoLogImpressao.MCOR_EX_FISICO_RN));
		
		return (McoNotaAdicional) executeCriteriaUniqueResult(criteria);
	}
}