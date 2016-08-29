package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.RapServidores;

public class MamNotaAdicionalAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamNotaAdicionalAnamneses> {

	private static final long serialVersionUID = -5437599678456125475L;

	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesPorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalAnamneses> obterNotaAdicionalAnamnesesConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		
		criteria.createAlias(MamNotaAdicionalAnamneses.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		
		DominioIndPendenteAmbulatorio [] pendentes = {DominioIndPendenteAmbulatorio.R, 
				DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V};
		
		criteria.add(Restrictions.in(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), pendentes));
		return executeCriteria(criteria);
	}
	
	
	
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Long naaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamNotaAdicionalAnamneses.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamNotaAdicionalAnamneses.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamNotaAdicionalAnamneses.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(naaSeq!=null){
			criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.SEQ.toString(),naaSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesParaConclusao(Integer consultaNumero, Integer naaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(naaSeq!=null){
			criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.SEQ.toString(),naaSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamneseParaDescricao(Integer consultaNumero){
		
		Object[] listaPendentes =  new Object[]{DominioIndPendenteAmbulatorio.V,
				DominioIndPendenteAmbulatorio.P,
				DominioIndPendenteAmbulatorio.E,
				DominioIndPendenteAmbulatorio.A};
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class, "proximo");
		subCriteria.setProjection(
				Projections.projectionList().add(Projections.property(MamNotaAdicionalAnamneses.Fields.NAA_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("proximo." + MamNotaAdicionalAnamneses.Fields.NAA_SEQ.toString(), 
				"corrente." + MamNotaAdicionalAnamneses.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.in("proximo." + MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), listaPendentes));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class, "corrente");
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		Criterion pendente = Restrictions.eq("corrente."+MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.P);
		Criterion validado = Restrictions.and(
				Restrictions.eq("corrente."+MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V), 
				Restrictions.and(
						Restrictions.isNull("corrente."+MamNotaAdicionalAnamneses.Fields.DTHR_MOVIMENTO.toString()),Subqueries.notExists(subCriteria)));
		
		criteria.add(Restrictions.or(pendente, validado));
		
		criteria.addOrder(Order.asc("corrente."+MamNotaAdicionalAnamneses.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}

	public Long listarNotasAdicionaisAnamnesesPorNumeroConsultaCount(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);

		criteria.createAlias(MamNotaAdicionalAnamneses.Fields.CONSULTA.toString(), MamNotaAdicionalAnamneses.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteriaCount(criteria);
	}

	/** Pesquisa Nota Adicional Anamnese por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param anaSeq
	 * @param pendente
	 * @return
	 */
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesePorNumeroSeqEPendencia(Integer consultaNumero, Long anaSeq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), pendente));
		}
		if (anaSeq != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.NAA_SEQ.toString(), anaSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalAnamneses> obterNotaAdicionalAnamnesesConsultaCertificacaoDigital(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtém todas as notas adicionais que façam parte do registro indicado e estejam pendentes ou a exclusão não tenha sido validada.
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesPendenteExcNaoValidParaInternacao(
			Long numRegistro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class, "ANA");
		criteria.createAlias("ANA." + MamNotaAdicionalAnamneses.Fields.REGISTRO.toString(), "REG");
		
		criteria.add(Restrictions.eq("REG." + MamRegistro.Fields.SEQ.toString(), numRegistro));
		
		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.E);
		
		criteria.add(Restrictions.in("ANA." + MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), situacoes));
		
		criteria.addOrder(Order.asc("ANA." + MamNotaAdicionalAnamneses.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria);
	}

	/**
	 * Obtém todas as notas adicionais que estejam validadas e não possuam uma nota pai que seja pendente, validada, excluida nao validada ou alterada nao validada
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	public List<MamNotaAdicionalAnamneses>  pesquisarNotaAdicionalAnamnesesValidasSemPaiParaInternacao(
			Long numRegistro) {
			
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "ANA");
		criteria.createAlias("ANA.".concat(MamNotaAdicionalAnamneses.Fields.REGISTRO.toString()), "REG");
		
		criteria.add(Restrictions.eq("REG.".concat(MamRegistro.Fields.SEQ.toString()), numRegistro));
		criteria.add(Restrictions.eq("ANA.".concat(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString()), DominioIndPendenteAmbulatorio.V));
		
		//////////// Subquery exists
		
		DetachedCriteria criteria1 = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class, "ANA1");
		criteria1.add(Restrictions.eqProperty("ANA1.".concat(MamNotaAdicionalAnamneses.Fields.NAA_SEQ.toString()), "ANA.".concat(MamNotaAdicionalAnamneses.Fields.SEQ.toString())));
		
		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.V);
		situacoes.add(DominioIndPendenteAmbulatorio.E);
		situacoes.add(DominioIndPendenteAmbulatorio.A);
		
		criteria1.add(Restrictions.in("ANA.".concat(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString()), situacoes));
		
		criteria1.setProjection(Projections.rowCount());
		////////////Subquery exists end
		
		criteria.add(Restrictions.not(Subqueries.exists(criteria1)));
		
		criteria.addOrder(Order.asc("ANA.".concat(MamNotaAdicionalAnamneses.Fields.SEQ.toString())));
		
		return this.executeCriteria(criteria);
		
	}
	
	// #50745 - cursor das notas adicionais da anamnese
	public Long obterCountNotaAdicionalAnamnesePorConNumero(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), conNumero));
		return executeCriteriaCount(criteria);
	}

	// #50745
	public List<MamNotaAdicionalAnamneses> obterListaNotaAdicionalAnamnesePorConNumero(Integer conNumero) {
		List<DominioIndPendenteAmbulatorio> filtroPendente = new ArrayList<DominioIndPendenteAmbulatorio>();
		filtroPendente.add(DominioIndPendenteAmbulatorio.R);
		filtroPendente.add(DominioIndPendenteAmbulatorio.P);
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalAnamneses.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalAnamneses.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.in(MamNotaAdicionalAnamneses.Fields.PENDENTE.toString(), filtroPendente));
		return executeCriteria(criteria);
	}
}
