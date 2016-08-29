package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;

public class MamProcedimentoRealizadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamProcedimentoRealizado> {

	private static final long serialVersionUID = 5034982621504167185L;


	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),
				consultaNumero));
		return executeCriteria(criteria);
	}

	public List<MamProcedimentoRealizado> pesquisarProcedimentoPorConsultaProcedimento(Integer consultaNumero, Integer prdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),	consultaNumero));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), prdSeq));
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoPendentePorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),
				consultaNumero));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PENDENTE.toString(),
				DominioIndPendenteAmbulatorio.P));
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoPorConsultaProcedimentoPendente(Integer consultaNumero, Integer prdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),	consultaNumero));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), prdSeq));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoPorConsultaFaturamento(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.createAlias(MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString(), MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString());
		criteria.createAlias(MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString()+"."+MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString(), MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString()+"."+MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString());
		criteria.createAlias(MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString()+"."+MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString()+"."+MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString(), MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString()+"."+MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString()+"."+MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString());
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),	consultaNumero));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(MamProcedimentoRealizado.Fields.DTHR_MOVIMENTO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoPorConsultaProcedimentoValidado(Integer consultaNumero, Integer prdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(),	consultaNumero));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), prdSeq));
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		return executeCriteria(criteria);
	}	
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Long polSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamProcedimentoRealizado.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamProcedimentoRealizado.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamProcedimentoRealizado.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamProcedimentoRealizado.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(polSeq!=null){
			criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.SEQ.toString(),polSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoPorConsultaExcetoSeqInformado(Integer consultaNumero, Integer prdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.ne(MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), prdSeq));
		return executeCriteria(criteria);
	}
	
	/** Pesquisa Pendencia Procedimento Realizado por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param seq
	 * @param pendente
	 * @return
	 */
	public List<MamProcedimentoRealizado> pesquisarPendenciaProcedimentoRealizadoPorNumeroSeqEPendencia(Integer consultaNumero, Long seq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.PENDENTE.toString(), pendente));
		}
		if (seq != null){
			criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamProcedimentoRealizado> pesquisarProcedimentoRealizadoParaConclusao(Integer consultaNumero, Long polSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamProcedimentoRealizado.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(polSeq!=null){
			criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.SEQ.toString(),polSeq));
		}
		return executeCriteria(criteria);
	}
	
	
	public DetachedCriteria montarCriteriaUnion1(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class);
		criteria.createAlias(MamProcedimentoRealizado.Fields.CID.toString(), "cid", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamProcedimentoRealizado.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		return criteria;
	}
	
	public DetachedCriteria montarCriteriaUnion2(Integer consultaNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, "atual");
		criteria.createAlias(MamProcedimentoRealizado.Fields.CID.toString(), "cid", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.isNull("atual."+MamProcedimentoRealizado.Fields.DTHR_VALIDA_MOVIMENTO.toString()));
		criteria.add(Restrictions.eq("atual."+MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq("atual."+MamProcedimentoRealizado.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.eq("atual."+MamProcedimentoRealizado.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria criteriaSub = DetachedCriteria.forClass(MamProcedimentoRealizado.class, "sub");
		criteriaSub.add(Restrictions.eqProperty("sub." + MamProcedimentoRealizado.Fields.PROCEDIMENTO_REALIZADO_SEQ.toString(), "atual." + MamProcedimentoRealizado.Fields.SEQ.toString()));
		criteriaSub.add(Restrictions.in("sub."+MamProcedimentoRealizado.Fields.PENDENTE.toString(), 
				new Object[]{DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.V,DominioIndPendenteAmbulatorio.E,DominioIndPendenteAmbulatorio.A}));
		criteriaSub.setProjection(Projections.property("sub." +MamProcedimentoRealizado.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.notExists(criteriaSub));
		
		
			
		return criteria;
	}
	
	public Set<MamProcedimentoRealizado> pesquisarProcedimentoPorNumeroConsulta(Integer consultaNumero){
		List<MamProcedimentoRealizado> procedimentosRealizadosList1 = executeCriteria(montarCriteriaUnion1(consultaNumero));
		List<MamProcedimentoRealizado> procedimentosRealizadosList2 = executeCriteria(montarCriteriaUnion2(consultaNumero));
		
		Set<MamProcedimentoRealizado> procedimentosRealizadosSet = new HashSet<MamProcedimentoRealizado>();
		procedimentosRealizadosSet.addAll(procedimentosRealizadosList1);
		procedimentosRealizadosSet.addAll(procedimentosRealizadosList2);
		
		return procedimentosRealizadosSet;
	}
	
}