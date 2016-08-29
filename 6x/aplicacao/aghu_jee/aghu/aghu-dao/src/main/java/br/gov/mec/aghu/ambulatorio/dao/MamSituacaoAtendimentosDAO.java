package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;

public class MamSituacaoAtendimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamSituacaoAtendimentos> {

	private static final long serialVersionUID = 1725208329346449539L;

	public List<MamSituacaoAtendimentos> pesquisarSituacoesAtendimentosPorConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = getCriteriaPesquisarSituacoesAtdPorConsulta(numeroConsulta);
		return this.executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarSituacoesAtdPorConsulta(
			Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSituacaoAtendimentos.class);
		criteria.createAlias(MamSituacaoAtendimentos.Fields.EXTRATO_CONTROLES.toString(), "exc");
		criteria.createAlias("exc." + MamExtratoControles.Fields.CONTROLE.toString(), "ctl");
		criteria.createAlias("ctl." + MamControles.Fields.CONSULTA.toString(), "consulta");
		criteria.add(Restrictions.eq("consulta." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		criteria.addOrder(Order.desc("exc." + MamExtratoControles.Fields.SEQP.toString()));
		return criteria;
	}
	
	public List<MamSituacaoAtendimentos> pesquisarSituacoesAtendimentosPorConsultaCur2(Integer numeroConsulta) {
		DetachedCriteria criteria = getCriteriaPesquisarSituacoesAtdPorConsulta(numeroConsulta);
		criteria.add(Restrictions.or(Restrictions.eq(MamSituacaoAtendimentos.Fields.AGUARDANDO.toString(), Boolean.TRUE), Restrictions.eq(MamSituacaoAtendimentos.Fields.AGENDADO.toString(), Boolean.TRUE)));
		return this.executeCriteria(criteria);
	}
	
	public List<MamSituacaoAtendimentos> listarSituacoesAtendimentos(Integer numeroConsulta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSituacaoAtendimentos.class);
		
		criteria.createAlias("ctl." + MamControles.Fields.CONSULTA.toString(), "consulta");
		criteria.createAlias(MamSituacaoAtendimentos.Fields.CONTROLES.toString(), "ctl");
		
		criteria.add(Restrictions.eq("consulta." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return this.executeCriteria(criteria);
	}

}
