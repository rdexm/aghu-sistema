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
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.RapServidores;

public class MamNotaAdicionalEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamNotaAdicionalEvolucoes> {
	
	private static final long serialVersionUID = -5529825190008603275L;
	
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalPorNumeroConsultaEPendente(Integer consultaNumero){
		List<DominioIndPendenteAmbulatorio> pend = new ArrayList<DominioIndPendenteAmbulatorio>();
		pend.add(DominioIndPendenteAmbulatorio.R);
		pend.add(DominioIndPendenteAmbulatorio.P);
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), pend));
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucoesPorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> obterNotaAdicionalEvolucoesConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		
		criteria.createAlias(MamNotaAdicionalEvolucoes.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		
		DominioIndPendenteAmbulatorio [] pendentes = {DominioIndPendenteAmbulatorio.R, 
				DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V};
		
		criteria.add(Restrictions.in(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), pendentes));
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Integer nevSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamNotaAdicionalEvolucoes.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamNotaAdicionalEvolucoes.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamNotaAdicionalEvolucoes.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(nevSeq!=null){
			criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.SEQ.toString(),nevSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoParaConclusao(Integer consultaNumero, Integer nevSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(nevSeq!=null){
			criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.SEQ.toString(),nevSeq));
		}
		return executeCriteria(criteria);
	}
	
	public Long listarNotasAdicionaisEvolucoesPorNumeroConsultaCount(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);

		criteria.createAlias(MamNotaAdicionalEvolucoes.Fields.CONSULTA.toString(), MamNotaAdicionalEvolucoes.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteriaCount(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEevolucaoParaDescricao(Integer consultaNumero){
		
		Object[] listaPendentes =  new Object[]{DominioIndPendenteAmbulatorio.V,
				DominioIndPendenteAmbulatorio.P,
				DominioIndPendenteAmbulatorio.E,
				DominioIndPendenteAmbulatorio.A};
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "proximo");
		subCriteria.setProjection(
				Projections.projectionList().add(Projections.property(MamNotaAdicionalEvolucoes.Fields.NEV_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("proximo." + MamNotaAdicionalEvolucoes.Fields.NEV_SEQ.toString(), 
				"corrente." + MamNotaAdicionalEvolucoes.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.in("proximo." + MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), listaPendentes));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "corrente");
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		Criterion pendente = Restrictions.eq("corrente."+MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.P);
		Criterion validado = Restrictions.and(
				Restrictions.eq("corrente."+MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V), 
				Restrictions.and(
						Restrictions.isNull("corrente."+MamNotaAdicionalEvolucoes.Fields.DTHR_MOVIMENTO.toString()),Subqueries.notExists(subCriteria)));
		
		criteria.add(Restrictions.or(pendente, validado));
		
		criteria.addOrder(Order.asc("corrente."+MamNotaAdicionalEvolucoes.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/** Pesquisa Nota Adicional Evolucoes por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param seq
	 * @param pendente
	 * @return
	 */
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucoesPorNumeroSeqEPendencia(Integer consultaNumero, Long seq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), pendente));
		}
		if (seq != null){
			criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> listarNotasAdicinaisEvolucoesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);

		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamNotaAdicionalEvolucoes> obterNotaAdicionalEvolucoesConsultaCertificacaoDigital(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		return executeCriteria(criteria);
	}
	
	//#9120
	public List<MamNotaAdicionalEvolucoes> pesquisarNotasAdicionaisEvolucoesPorCodigoPaciente(Integer pacCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);

		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString()));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.RGT_SEQ.toString()));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.TRG_SEQ.toString()));
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));

		criteria.addOrder(Order.desc(MamNotaAdicionalEvolucoes.Fields.DTHR_VALIDA.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarNotasAdicionaisEvolucoesPorCodigoPacienteCount(Integer codigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class);
		
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.CON_NUMERO.toString()));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.RGT_SEQ.toString()));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.TRG_SEQ.toString()));
		criteria.add(Restrictions.eq(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull(MamNotaAdicionalEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * 15818
	 * @param nota
	 * @return
	 */
	public MamNotaAdicionalEvolucoes buscarNotaParaRelatorio(Integer seqNota) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "nota");
		criteria.add(Restrictions.eq("nota."+MamNotaAdicionalEvolucoes.Fields.SEQ.toString(), seqNota));
		
		return (MamNotaAdicionalEvolucoes) executeCriteriaUniqueResult(criteria);
	}

	public List<AghVersaoDocumento> verificaImprime(Integer seqNota, DominioTipoDocumento tipoDoc) {
		List<AghVersaoDocumento> versaoDocumentos = new ArrayList<AghVersaoDocumento>();
		
		DetachedCriteria criteria = criaPesquisaVersaoDocumento(seqNota, tipoDoc);		
		versaoDocumentos = executeCriteria(criteria);
		
		return versaoDocumentos;
	}

	public List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqNota, DominioTipoDocumento tipoDoc) {
		List<AghVersaoDocumento> versaoDocumentos = new ArrayList<AghVersaoDocumento>();
		
		DetachedCriteria criteria = criaPesquisaVersaoDocumento(seqNota, tipoDoc);
		criteria.addOrder(Order.desc("dov." + AghVersaoDocumento.Fields.SEQ.toString()));
		versaoDocumentos = executeCriteria(criteria);
		
		return versaoDocumentos;
	}
	
	public DetachedCriteria criaPesquisaVersaoDocumento(Integer seqNota, DominioTipoDocumento tipoDoc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "dov");
		criteria.createAlias("dov." + AghVersaoDocumento.Fields.DOCUMENTO.toString(), "dok");
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("dok." + AghDocumento.Fields.ATD_SEQ.toString(), seqNota))
				.add(Restrictions.eq("dok." + AghDocumento.Fields.CRG_SEQ.toString(), seqNota))
				.add(Restrictions.eq("dok." + AghDocumento.Fields.FIC_SEQ.toString(), seqNota.longValue()))
				.add(Restrictions.eq("dok." + AghDocumento.Fields.NPO_SEQ.toString(), seqNota)));
		criteria.add(Restrictions.eq("dov." + AghVersaoDocumento.Fields.SITUACAO.toString(), DominioSituacaoVersaoDocumento.A));
		criteria.add(Restrictions.eq("dok." + AghDocumento.Fields.TIPO.toString(), tipoDoc));
		
		return criteria;
	}

	/**
	 * Obtém todas as notas adicionais que façam parte do registro indicado e estejam pendentes ou a exclusão não tenha sido validada.
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoPendenteExcNaoValidParaInternacao(
			Long numRegistro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MamNotaAdicionalEvolucoes.Fields.REGISTRO.toString(), "REG");
		
		criteria.add(Restrictions.eq("REG." + MamRegistro.Fields.SEQ.toString(), numRegistro));
		
		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.E);
		
		criteria.add(Restrictions.in("EVO." + MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString(), situacoes));
		
		criteria.addOrder(Order.asc("EVO." + MamNotaAdicionalEvolucoes.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria);
	}

	/**
	 * Obtém todas as notas adicionais que estejam validadas e não possuam uma nota pai que seja pendente, validada, excluida nao validada ou alterada nao validada
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoValidasSemPaiParaInternacao(
			Long numRegistro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "EVO");
		criteria.createAlias("EVO.".concat(MamNotaAdicionalEvolucoes.Fields.REGISTRO.toString()), "REG");
		
		criteria.add(Restrictions.eq("REG.".concat(MamRegistro.Fields.SEQ.toString()), numRegistro));
		criteria.add(Restrictions.eq("EVO.".concat(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString()), DominioIndPendenteAmbulatorio.V));
		
		//////////// Subquery exists
		
		DetachedCriteria criteria1 = DetachedCriteria.forClass(MamNotaAdicionalEvolucoes.class, "EVO1");
		criteria1.add(Restrictions.eqProperty("EVO1.".concat(MamNotaAdicionalEvolucoes.Fields.NEV_SEQ.toString()), "EVO.".concat(MamNotaAdicionalEvolucoes.Fields.SEQ.toString())));
		
		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		
		situacoes.add(DominioIndPendenteAmbulatorio.P);
		situacoes.add(DominioIndPendenteAmbulatorio.V);
		situacoes.add(DominioIndPendenteAmbulatorio.E);
		situacoes.add(DominioIndPendenteAmbulatorio.A);
		
		criteria1.add(Restrictions.in("EVO.".concat(MamNotaAdicionalEvolucoes.Fields.PENDENTE.toString()), situacoes));
		
		criteria1.setProjection(Projections.rowCount());
		////////////Subquery exists end
		
		criteria.add(Restrictions.not(Subqueries.exists(criteria1)));
		
		criteria.addOrder(Order.asc("EVO.".concat(MamNotaAdicionalEvolucoes.Fields.SEQ.toString())));
		
		return this.executeCriteria(criteria);
	}

	
}