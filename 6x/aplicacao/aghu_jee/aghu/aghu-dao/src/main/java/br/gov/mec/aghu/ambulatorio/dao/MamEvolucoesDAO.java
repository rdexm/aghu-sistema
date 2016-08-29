package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.EvolucaoAutorelacaoVO;
import br.gov.mec.aghu.ambulatorio.vo.ResponsavelAnamneseVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;


public class MamEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamEvolucoes> {

	private static final long serialVersionUID = -8352100008523100505L;

	public List<MamEvolucoes> obterEvolucoesPelaConsulta(Integer numero) {
		List<MamEvolucoes> evolucoes = new ArrayList<MamEvolucoes>(0);
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),numero));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(), new DominioIndPendenteAmbulatorio[] {DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V}));
		
		List<MamEvolucoes> evolucoesQ1 = executeCriteria(criteria); 
		
		criteria = DetachedCriteria
		.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),numero));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.EVO_SEQ.toString()));
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		
		List<MamEvolucoes> evolucoesQ2 = executeCriteria(criteria); 

		evolucoes.addAll(evolucoesQ1);
		evolucoes.addAll(evolucoesQ2);
		
		return evolucoes;
	}
	
	public MamEvolucoes obterEvolucaoAtivaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = obterCriteriaEvolucaoAtivaPorNumeroConsulta(consultaNumero);
		MamEvolucoes evolucao = (MamEvolucoes) executeCriteriaUniqueResult(criteria);
		if(evolucao!= null){
			for (MamItemEvolucoes item: evolucao.getItensEvolucoes()){
				Hibernate.initialize(item);
			}
		}
		return evolucao;
	}
	
	public MamEvolucoes obterEvolucaoQuestionarioAtivaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = obterCriteriaEvolucaoQuestionarioAtivaPorNumeroConsulta(consultaNumero);
		MamEvolucoes evolucao = (MamEvolucoes) executeCriteriaUniqueResult(criteria);
		if(evolucao != null){
			for (MamRespostaEvolucoes item: evolucao.getRespostasEvolucoes()){
				Hibernate.initialize(item);
			}
		}
		return evolucao;
	}
	
	public List<MamEvolucoes> pesquisarEvolucoesAtivaPorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = obterCriteriaEvolucaoAtivaPorNumeroConsulta(consultaNumero);
		return  executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaEvolucaoAtivaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MamEvolucoes.Fields.ITENS_EVOLUCOES.toString(),"itens");
		criteria.createAlias("EVO." + MamEvolucoes.Fields.SERVIDOR.toString(),"SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(),"PES");
		criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.CON_NUMERO.toString(),consultaNumero));
		criteria.add(Restrictions.isEmpty("EVO." + MamEvolucoes.Fields.EVOLUCOES.toString()));
		criteria.add(Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.isNull("EVO." + MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		return criteria;
	}
	
	private DetachedCriteria obterCriteriaEvolucaoQuestionarioAtivaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MamEvolucoes.Fields.RESPOSTA.toString(),"RES");
		criteria.createAlias("EVO." + MamEvolucoes.Fields.SERVIDOR.toString(),"SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(),"PES");
		criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.CON_NUMERO.toString(),consultaNumero));
		criteria.add(Restrictions.isEmpty("EVO." + MamEvolucoes.Fields.EVOLUCOES.toString()));
		criteria.add(Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.isNull("EVO." + MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		return criteria;
	}
	
	public List<MamEvolucoes> obterEvolucoesPorConsultaIndPendenteSemMvto(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long obterQuantidadeEvolucoesPeloNumeroConsultaEIndPendente(Integer conNumero, DominioIndPendenteAmbulatorio[] indPendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(),indPendente));
		
		return executeCriteriaCount(criteria);
	}

	public List<MamEvolucoes> obterEvolucoesPeloNumeroConsultaEIndPendente(Integer conNumero, DominioIndPendenteAmbulatorio[] indPendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(),indPendente));
		
		return executeCriteria(criteria);
	}

	public List<MamEvolucoes> obterEvolucoesPorNumeroConsultaEIndPendente(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P}));
		
		return executeCriteria(criteria);
	}

	public Long listarEvolucoesPorNumeroConsultaCount(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);

		criteria.createAlias(MamEvolucoes.Fields.CONSULTA.toString(), MamEvolucoes.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteriaCount(criteria);
	}
	
	public List<MamEvolucoes> pesquisarEvolucaoParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Long evoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamEvolucoes.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamEvolucoes.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamEvolucoes.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamEvolucoes.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(evoSeq!=null){
			criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(),evoSeq));
		}
		return executeCriteria(criteria);
	}


	/** Pesquisa Evolucoes por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param seq
	 * @param pendente
	 * @return
	 */
	public List<MamEvolucoes> pesquisarEvolucoesPorNumeroSeqEPendencia(Integer consultaNumero, Long seq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamEvolucoes.Fields.PENDENTE.toString(), pendente));
		}
		if (seq != null){
			criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}	
	

	/** Pesquisa Evolucoes por evoSeq
	 * 
	 * @param anaSeq
	 * @return
	 */
	public List<MamEvolucoes> pesquisarMamEvolucoesPorEvoSeq(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		return executeCriteria(criteria);
	}	
	
	// #49956 C1
	public List<MamEvolucoes> pesquisarMamEvolucoesPaciente(Integer numero) {
		List<DominioIndPendenteAmbulatorio> pend = new ArrayList<DominioIndPendenteAmbulatorio>();
		pend.add(DominioIndPendenteAmbulatorio.R);
		pend.add(DominioIndPendenteAmbulatorio.P);
		pend.add(DominioIndPendenteAmbulatorio.V);
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(), pend));
		criteria.add(Restrictions.isNull(MamEvolucoes.Fields.DTHR_MOVIMENTO.toString()));
		return executeCriteria(criteria);
	}	
	
	public List<MamEvolucoes> pesquisarMamEvolucoesPorConNumero(Long evoSeq, Integer numero) {
		List<DominioIndPendenteAmbulatorio> pend = new ArrayList<DominioIndPendenteAmbulatorio>();
		pend.add(DominioIndPendenteAmbulatorio.R);
		pend.add(DominioIndPendenteAmbulatorio.P);
		pend.add(DominioIndPendenteAmbulatorio.V);
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(), evoSeq));
		criteria.add(Restrictions.in(MamEvolucoes.Fields.IND_PENDENTE.toString(), pend));
		return executeCriteria(criteria);
	}	

	public List<MamEvolucoes> pesquisarEvolucaoParaConclusao(Integer consultaNumero, Long evoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamEvolucoes.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(evoSeq!=null){
			criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(),evoSeq));
		}
		return executeCriteria(criteria);
	}
	
	public MamEvolucoes obterUltimaEvolucaoExcluidaPorConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.PENDENTE.toString(),DominioIndPendenteAmbulatorio.E));
		criteria.addOrder(Order.desc(MamEvolucoes.Fields.DTHR_MOVIMENTO.toString()));
		
		List<MamEvolucoes> evolucoes = executeCriteria(criteria);
		MamEvolucoes evolucaoRetorno = null;
		
		if (!evolucoes.isEmpty()){
			evolucaoRetorno = evolucoes.get(0);
		}
		return evolucaoRetorno;
	}
	
	public List<MamEvolucoes> pesquisarEvolucaoPorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.CON_NUMERO.toString(), consultaNumero));
		return executeCriteria(criteria);
	}

	public List<MamEvolucoes> listarEvolucoesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);

		criteria.add(Restrictions.eq(MamEvolucoes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamEvolucoes> pesquisarEvolucoesPorAtendimento(Integer atdSeq) {
		DetachedCriteria dc = criarCriteriaPesquisarEvolucoesPorAtendimentoOuTriagem(atdSeq, null);
		return executeCriteria(dc);
	}

	private DetachedCriteria criarCriteriaPesquisarEvolucoesPorAtendimentoOuTriagem(Integer atdSeq, Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"EVO");
		
		dc.createAlias("EVO.".concat(MamEvolucoes.Fields.REGISTRO.toString()), "RGT");
		
		if (atdSeq != null) {
			dc.add(Restrictions.eq("RGT.".concat(MamRegistro.Fields.ATD_SEQ.toString()), atdSeq));			
		}
		if (trgSeq != null) {			
			dc.add(Restrictions.eq("EVO.".concat(MamEvolucoes.Fields.TRG_SEQ.toString()), trgSeq));
		}
		
		dc.add(Restrictions.eq("RGT.".concat(MamRegistro.Fields.IND_SITUACAO.toString()), DominioSituacaoRegistro.VA));
		dc.add(Restrictions.eq("EVO.".concat(MamEvolucoes.Fields.IND_PENDENTE.toString()), DominioIndPendenteAmbulatorio.V));
		dc.add(Restrictions.isNull("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString())));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(getClazz(), "EVO1");
		
		subquery.setProjection(Projections.property("EVO1.".concat(MamEvolucoes.Fields.EVO_SEQ.toString())));
		subquery.add(Restrictions.eqProperty("EVO.".concat(MamEvolucoes.Fields.SEQ.toString()), "EVO1.".concat(MamEvolucoes.Fields.EVO_SEQ.toString())));
		subquery.add(Restrictions.in("EVO1.".concat(MamEvolucoes.Fields.IND_PENDENTE.toString()), new DominioIndPendenteAmbulatorio[] {
			DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.V,
			DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.A}));
		
		dc.add(Subqueries.notExists(subquery));
		
		return dc;
	}
	
	/**
	 * Utilizada para alimentar Relatório Evoluções da Internação.
	 * #14953 - Imprimir anamnese e evolução da internação
	 * @param atdSeq
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	private void incluirProjecaoEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(DetachedCriteria dc) {
		
		ProjectionList pl = Projections.projectionList();
		
		pl.add(Projections.property("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString())), RelatorioAnaEvoInternacaoVO.Fields.DTHR_VALIDA_ANA.toString());
		pl.add(Projections.property("EVO.".concat(MamEvolucoes.Fields.SEQ.toString())), RelatorioAnaEvoInternacaoVO.Fields.EVO_SEQ.toString());
		pl.add(Projections.property("RGT.".concat(MamRegistro.Fields.SEQ.toString())), RelatorioAnaEvoInternacaoVO.Fields.RGT_SEQ.toString());
		pl.add(Projections.property("EVO.".concat(MamEvolucoes.Fields.PAC_CODIGO.toString())), RelatorioAnaEvoInternacaoVO.Fields.COD_PAC.toString());
		
		dc.setProjection(pl);
		dc.setResultTransformer(Transformers.aliasToBean(RelatorioAnaEvoInternacaoVO.class));
	}
	
	/**
	 * Pesquisa utilizada para alimentar Relatório Evoluções da Internação.
	 * #14953 - Imprimir anamnese e evolução da internação
	 * @param atdSeq
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	public List<RelatorioAnaEvoInternacaoVO> pesquisarEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(Integer atdSeq) {
		DetachedCriteria dc = criarCriteriaPesquisarEvolucoesPorAtendimentoOuTriagem(atdSeq, null);		

		dc.addOrder(Order.desc("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString())));
		
		incluirProjecaoEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(dc);
		
		dc.addOrder(Order.desc("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString())));
		
		return executeCriteria(dc);
	}
	
	/**
	 * Pesquisa utilizada para alimentar Relatório Evoluções da Internação em um período informado pelo usuário
	 * Refatorado devido a issue  #24060
	 * @param atdSeq
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	public List<RelatorioAnaEvoInternacaoVO> pesquisarEvolucoesPorAtendimentoRelatorioAnaEvoInternacaoPeriodo(Integer atdSeq, Date dataInicial, Date dataFinal) {
		DetachedCriteria dc = criarCriteriaPesquisarEvolucoesPorAtendimentoOuTriagem(atdSeq, null);		
		
		dc.add(Restrictions.between("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString()), dataInicial, dataFinal));
		
		incluirProjecaoEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(dc);
		
		dc.addOrder(Order.desc("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString())));
		
		return executeCriteria(dc);
	}
	
	public List<MamEvolucoes> obterEvolucaoPorTriagemERegistro(Long trgSeq, Long rgtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "t1");
		
		criteria.add(Restrictions.eq("t1." + MamEvolucoes.Fields.TRG_SEQ.toString(), trgSeq));
		
		if(rgtSeq == null){
			criteria.add(Restrictions.eqProperty("t1." + MamEvolucoes.Fields.RGT_SEQ.toString(), "t1." + MamEvolucoes.Fields.RGT_SEQ.toString()));
		}else{
			criteria.add(Restrictions.eq("t1." + MamEvolucoes.Fields.RGT_SEQ.toString(), rgtSeq));
		}			
		
		criteria.add(Restrictions.eq("t1." + MamEvolucoes.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull("t1." + MamEvolucoes.Fields.DTHR_VALIDA.toString()));
		criteria.add(Subqueries.propertyNotIn("t1." + MamEvolucoes.Fields.SEQ.toString(), subCriteriaEvolucoes()));
		
		return executeCriteria(criteria);	
		
	}
	
	private DetachedCriteria subCriteriaEvolucoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class,"t2");

		criteria.setProjection(Projections
				.projectionList().add(Projections.property("t2." + MamEvolucoes.Fields.EVO_SEQ.toString())));
		
		criteria.add(Restrictions.eqProperty("t2." + MamEvolucoes.Fields.EVO_SEQ.toString(), "t1.seq"));
		
		List<DominioIndPendenteAmbulatorio> pendente = new ArrayList<DominioIndPendenteAmbulatorio>();
		pendente.add(DominioIndPendenteAmbulatorio.P);
		pendente.add(DominioIndPendenteAmbulatorio.V);
		pendente.add(DominioIndPendenteAmbulatorio.E);
		pendente.add(DominioIndPendenteAmbulatorio.A);
		
		criteria.add(Restrictions.in("t2." + MamEvolucoes.Fields.PENDENTE.toString(), pendente));
		
		//criteria.addOrder(Order.asc("t2." + MamEvolucoes.Fields.DTHR_VALIDA.toString()));	

		return criteria;
	}	
	
	/**
	 * Pesquisa utilizada para alimentar Relatório Evolução da Emergência.
	 * #17315 - Botão evolução / anamnese do atendimento de emergência
	 * Q_EVO
	 * @param seqTriagem
	 * @param dataInicio
	 * @param dataFim
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	public List<RelatorioAnaEvoInternacaoVO> pesquisarEvolucaoAtendimentoEmergencia(Long trgSeq, Date dataInicio, Date dataFim) {
		
		DetachedCriteria dc = criarCriteriaPesquisarEvolucoesPorAtendimentoOuTriagem(null, trgSeq);
		
		if(dataInicio != null) {
			dc.add(Restrictions.ge("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString()), dataInicio));	
		}
		if(dataFim != null) {
			dc.add(Restrictions.lt("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString()), dataFim));	
		}
		
		incluirProjecaoEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(dc);
		
		dc.addOrder(Order.desc("EVO.".concat(MamEvolucoes.Fields.DTHR_VALIDA.toString())));

		return executeCriteria(dc);
	}
	
	public List<DominioFuncaoProfissional> obterMamEvolucoesPorCirurgia(Integer crgSeq) {

		DetachedCriteria subquery = DetachedCriteria.forClass(MamEvolucoes.class, "MEV");
		subquery.createAlias("MEV." + MamEvolucoes.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		subquery.createAlias("ATD." + AghAtendimentos.Fields.CIRURGIAS.toString(), "CRG", JoinType.INNER_JOIN);
		subquery.setProjection(Projections.projectionList().add(Projections.property("MEV." + MamEvolucoes.Fields.SEQ.toString())));
		subquery.add(Restrictions.and(
				Restrictions.eqProperty("MEV." + MamEvolucoes.Fields.SERVIDOR_MATRICULA.toString(), "MPA." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()),
				Restrictions.eqProperty("MEV." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO.toString(), "MPA." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())));
		subquery.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "MPA");
		criteria.setProjection(Projections.projectionList().add(Projections.property("MPA." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString())));
		criteria.add(Subqueries.exists(subquery));
		
		DominioFuncaoProfissional[] funcoes = { DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF };
		criteria.add(Restrictions.in("MPA." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), funcoes));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaEvolcoesPorCirurgia(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class,"MEV");
		criteria.createAlias("MEV." + MamEvolucoes.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CIRURGIAS.toString(), "CRG", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return criteria;
	}

	public List<MamEvolucoes> listarEvolcoesPorCirurgia(Integer crgSeq){
		return executeCriteria(obterCriteriaEvolcoesPorCirurgia(crgSeq));
	}

	// #49998 - curEvo
	public MamEvolucoes obterEvolucoesPorConNumeroEEvoSeq(Integer conNumero, Long evoSeq) {
		DetachedCriteria subquery = DetachedCriteria.forClass(MamEvolucoes.class, "EVO1");
		subquery.setProjection(Projections.projectionList().add(Projections.property("EVO1." + MamEvolucoes.Fields.EVO_SEQ.toString())));
		subquery.add(Restrictions.eqProperty("EVO1." + MamEvolucoes.Fields.EVO_SEQ.toString(), "EVO." + MamEvolucoes.Fields.SEQ.toString()));

		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
//		criteria.setProjection(Projections.projectionList()
//				.add(Projections.property("EVO." + MamEvolucoes.Fields.SEQ.toString()))
//				.add(Projections.property("EVO." + MamEvolucoes.Fields.EVO_SEQ.toString()))
//				.add(Projections.property("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString()))
//				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_MATRICULA_VALIDA.toString()))
//				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()))
//		);
		
		criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.CON_NUMERO.toString(), conNumero));
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V };
		criteria.add(Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		criteria.add(Restrictions.isNull("EVO." + MamEvolucoes.Fields.DTHR_MOVIMENTO.toString()));
		
		criteria.add(Restrictions.or(
				Restrictions.eq("EVO." + MamEvolucoes.Fields.SEQ.toString(), evoSeq),
				Subqueries.propertyNotIn("EVO." + MamEvolucoes.Fields.SEQ.toString(), subquery)
		));
		
		List<MamEvolucoes> lista = executeCriteria(criteria, 0, 1, null, true);
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
	}
	
	// #49998 - c1
	public EvolucaoAutorelacaoVO obeterEvolucaoComAutorelacaoPorSeq(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P};
		criteria.createAlias("EVO." + MamEvolucoes.Fields.EVOLUCAO .toString(), "EVO_A", JoinType.LEFT_OUTER_JOIN,
				Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("EVO." + MamEvolucoes.Fields.PENDENTE.toString()) ,EvolucaoAutorelacaoVO.Fields.PENDENTE.toString())
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), EvolucaoAutorelacaoVO.Fields.SERVIDOR_MATRICULA_VALIDA.toString())
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), EvolucaoAutorelacaoVO.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property("EVO_A." + MamEvolucoes.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), EvolucaoAutorelacaoVO.Fields.SERVIDOR_MATRICULA_VALIDA_RELACAO.toString())
				.add(Projections.property("EVO_A." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), EvolucaoAutorelacaoVO.Fields.SERVIDOR_VIN_CODIGO_VALIDA_RELACAO.toString())
		);
		criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.SEQ.toString(), evoSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(EvolucaoAutorelacaoVO.class));
		
		return (EvolucaoAutorelacaoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MamEvolucoes> obterEvolucoesPorConNumeroEDthrMvtoEEvoSeq(Integer conNumero, Date dthrMvto, Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SEQ.toString()))
				.add(Projections.property("EVO." + MamEvolucoes.Fields.EVO_SEQ.toString()))
				.add(Projections.property("EVO." + MamEvolucoes.Fields.PENDENTE.toString()))
		);
		
		criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.CON_NUMERO.toString(), conNumero));
		
		if (dthrMvto != null) {
			criteria.add(Restrictions.or(Restrictions.ge("EVO." + MamEvolucoes.Fields.DTHR_CRIACAO.toString(), dthrMvto),
					Restrictions.and(Restrictions.isNotNull("EVO." + MamEvolucoes.Fields.DTHR_MOVIMENTO.toString()),
							Restrictions.ge("EVO." + MamEvolucoes.Fields.DTHR_MOVIMENTO.toString(), dthrMvto))));
		}
		
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.E};
		criteria.add(Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		
		if (evoSeq != null) {
			criteria.add(Restrictions.eq("EVO." + MamEvolucoes.Fields.SEQ.toString(), evoSeq));
		}
		
		return executeCriteria(criteria);
	}
	
	public MamEvolucoes pesquisarMamEvolucoesPendente(Long evoSeq,DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.IND_PENDENTE.toString(),pendente));
		
		return (MamEvolucoes) executeCriteriaUniqueResult(criteria);
	}

	
	//CUR_EVO
	public MamEvolucoes obterEvolucaoPorSeq(Long evoSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class);
		criteria.add(Restrictions.eq(MamEvolucoes.Fields.SEQ.toString(), evoSeq));

		return (MamEvolucoes) executeCriteriaUniqueResult(criteria);
	}

	@Override
	public MamEvolucoes obterPorChavePrimaria(Object pk) {
		MamEvolucoes evolucaoRetorno = super.obterPorChavePrimaria(pk);
		return evolucaoRetorno;
	}
	
	/**
	 * #50937
	 * Cursor: c_evo
	 * @param evoSeq
	 * @return
	 */
	public ResponsavelAnamneseVO obterEvolucaoResponsavelPorSeq(Long evoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_MATRICULA.toString()), ResponsavelAnamneseVO.Fields.MATRICULA.toString())
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO.toString()), ResponsavelAnamneseVO.Fields.VINCULO.toString())
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), ResponsavelAnamneseVO.Fields.MATRICULA_VALIDA.toString())
				.add(Projections.property("EVO." + MamEvolucoes.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), ResponsavelAnamneseVO.Fields.VINCULO_VALIDA.toString())				
				.add(Projections.property("EVO." + MamEvolucoes.Fields.DTHR_VALIDA.toString()), ResponsavelAnamneseVO.Fields.DTHR_VALIDA.toString())
		);
		
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.SEQ.toString(), evoSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResponsavelAnamneseVO.class));
				
		return (ResponsavelAnamneseVO) executeCriteriaUniqueResult(criteria);
	}
}
