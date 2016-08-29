package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.AnamneseAutorelacaoVO;
import br.gov.mec.aghu.ambulatorio.vo.ResponsavelAnamneseVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;


public class MamAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAnamneses> {

	private static final long serialVersionUID = 9067672371147519278L;

	public List<MamAnamneses> obterAnamnesesPelaConsulta(Integer numero) {
		List<MamAnamneses> anamneses = new ArrayList<MamAnamneses>(0);
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),numero));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(), new DominioIndPendenteAmbulatorio[] {DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V}));
		
		List<MamAnamneses> anamnesesQ1 = executeCriteria(criteria); 
		
		criteria = DetachedCriteria
		.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),numero));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.ANA_SEQ.toString()));
		criteria.add(Restrictions.eq(MamAnamneses.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		
		List<MamAnamneses> anamnesesQ2 = executeCriteria(criteria); 

		anamneses.addAll(anamnesesQ1);
		anamneses.addAll(anamnesesQ2);
		
		return anamneses;
	}

	public MamAnamneses obterAnamneseAtivaPorNumeroConsulta(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class, "ANA");
		criteria.createAlias("ANA." + MamAnamneses.Fields.ITENS_ANAMNESES.toString(), "IAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ANA." + MamAnamneses.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.in("ANA." + MamAnamneses.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.isNull("ANA." + MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.isEmpty("ANA." +MamAnamneses.Fields.ANAMNESES.toString()));
		List<MamAnamneses> anamneses = executeCriteria(criteria);
		MamAnamneses anamnese = null;
		if (!anamneses.isEmpty()) {
			anamnese = anamneses.get(0);
			for(MamItemAnamneses item: anamnese.getItensAnamneses()){
				Hibernate.initialize(item);
			}
		}
		return anamnese;
	}
	
	public List<MamAnamneses> obterAnamnesesPorConsultaIndPendenteSemMvto(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long obterQuantidadeAnamnesesPeloNumeroConsultaEIndPendente(Integer conNumero, DominioIndPendenteAmbulatorio[] indPendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(),indPendente));
		
		return executeCriteriaCount(criteria);
	}

	public List<MamAnamneses> obterAnamnesesPeloNumeroConsultaEIndPendente(Integer conNumero, DominioIndPendenteAmbulatorio[] indPendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(),indPendente));
		
		return executeCriteria(criteria);
	}

	public List<MamAnamneses> obterAnamnesesPorNumeroConsultaEIndPendente(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P}));
		
		return executeCriteria(criteria);
	}
	
	public Long listarAnamnesesPorNumeroConsultaCount(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);

		criteria.createAlias(MamAnamneses.Fields.CONSULTA.toString(), MamAnamneses.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteriaCount(criteria);
	}
	
	public List<MamAnamneses> pesquisarAnamneseParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Long anaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamAnamneses.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamAnamneses.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamAnamneses.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamAnamneses.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(anaSeq!=null){
			criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(),anaSeq));
		}
		return executeCriteria(criteria);
	}
	
	public MamAnamneses obterUltimaAnamneseExcluidaPorConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq(MamAnamneses.Fields.PENDENTE.toString(),DominioIndPendenteAmbulatorio.E));
		criteria.addOrder(Order.desc(MamAnamneses.Fields.DTHR_MOVIMENTO.toString()));
		
		List<MamAnamneses> anamneses = executeCriteria(criteria);
		MamAnamneses anamneseRetorno = null;
		
		if (!anamneses.isEmpty()){
			anamneseRetorno = anamneses.get(0);
		}
		return anamneseRetorno;
	}
	
	/** Pesquisa Anamnese por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param anaSeq
	 * @param pendente
	 * @return
	 */
	public List<MamAnamneses> pesquisarAnamnesePorNumeroSeqEPendencia(Integer consultaNumero, Long anaSeq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamAnamneses.Fields.IND_PENDENTE.toString(), pendente));
		}
		if (anaSeq != null){
			criteria.add(Restrictions.eq(MamAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		}
		return executeCriteria(criteria);
	}
	
	/** Pesquisa Anamnese por anaSeq
	 * 
	 * @param anaSeq
	 * @return
	 */
	public List<MamAnamneses> pesquisarAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		return executeCriteria(criteria);
	}
	
	public List<MamAnamneses> pesquisarAnamneseParaConclusao(Integer consultaNumero, Long anaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamAnamneses.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(anaSeq!=null){
			criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(),anaSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamAnamneses> pesquisarAnamnesePorNumeroConsulta(Integer consultaNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), consultaNumero));
		return executeCriteria(criteria);
	}

	public List<MamAnamneses> listarAnamnesesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);

		criteria.add(Restrictions.eq(MamAnamneses.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamAnamneses> pesquisarAnamnesesPorAtendimento(Integer atdSeq) {
		DetachedCriteria dc = criarCriteriaPesquisarAnamnesePorAtendimentoOuTriagem(atdSeq, null);
		
		return executeCriteria(dc);
	}

	private DetachedCriteria criarCriteriaPesquisarAnamnesePorAtendimentoOuTriagem(Integer atdSeq, Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"ANA");
		
		dc.createAlias("ANA.".concat(MamAnamneses.Fields.REGISTRO.toString()), "RGT");
		
		if (atdSeq != null) {
			dc.add(Restrictions.eq("RGT.".concat(MamRegistro.Fields.ATD_SEQ.toString()), atdSeq));			
		}
		if (trgSeq != null) {
			dc.add(Restrictions.eq("ANA.".concat(MamAnamneses.Fields.TRG_SEQ.toString()), trgSeq));
		}
		dc.add(Restrictions.eq("RGT.".concat(MamRegistro.Fields.IND_SITUACAO.toString()), DominioSituacaoRegistro.VA));
		dc.add(Restrictions.eq("ANA.".concat(MamAnamneses.Fields.IND_PENDENTE.toString()), DominioIndPendenteAmbulatorio.V));
		dc.add(Restrictions.isNull("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString())));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(getClazz(), "ANA1");
		
		subquery.setProjection(Projections.property("ANA1.".concat(MamAnamneses.Fields.ANA_SEQ.toString())));
		subquery.add(Restrictions.eqProperty("ANA.".concat(MamAnamneses.Fields.SEQ.toString()), "ANA1.".concat(MamAnamneses.Fields.ANA_SEQ.toString())));
		subquery.add(Restrictions.in("ANA1.".concat(MamAnamneses.Fields.IND_PENDENTE.toString()), new DominioIndPendenteAmbulatorio[] {
			DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.V,
			DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.A}));

		dc.add(Subqueries.notExists(subquery));
		
		return dc;
	}

	/**
	 * Pesquisa utilizada para alimentar Relatório Anamneses da Internação.
	 * #14953 - Imprimir anamnese e evolução da internação
	 * @param atdSeq
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	public List<RelatorioAnaEvoInternacaoVO> pesquisarAnamnesesPorAtendimentoRelatorioAnaEvoInternacao(Integer atdSeq) {
		DetachedCriteria dc = criarCriteriaPesquisarAnamnesePorAtendimentoOuTriagem(atdSeq, null);		
		
		dc.addOrder(Order.desc("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA.toString())));
		
		inserirProjectionRelatorioAnamnese(dc);
		
		return executeCriteria(dc);
	}
	
	/**
	 * Pesquisa utilizada para alimentar Relatório Anamneses da Emergência.
	 * #17315 - Botão evolução / anamnese do atendimento de emergência
	 * Q_ANA
	 * @param seqTriagem
	 * @param dataInicio
	 * @param dataFim
	 * @return List<RelatorioAnaEvoInternacaoVO>
	 */
	public List<RelatorioAnaEvoInternacaoVO> pesquisarAnamnesesPorTriagemDataHoraValida(Long trgSeq, Date dataInicio, Date dataFim) {
		DetachedCriteria dc = criarCriteriaPesquisarAnamnesePorAtendimentoOuTriagem(null, trgSeq);		
		
		if(dataInicio != null) {
			dc.add(Restrictions.ge("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA.toString()), dataInicio));	
		}
		if(dataFim != null) {
			dc.add(Restrictions.lt("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA.toString()), dataFim));	
		}
		
		dc.addOrder(Order.desc("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA.toString())));
		
		inserirProjectionRelatorioAnamnese(dc);
		
		return executeCriteria(dc);
	}
	
	private void inserirProjectionRelatorioAnamnese(DetachedCriteria dc) {
		ProjectionList pl = Projections.projectionList();
		
		pl.add(Projections.property("ANA.".concat(MamAnamneses.Fields.DTHR_VALIDA.toString())), RelatorioAnaEvoInternacaoVO.Fields.DTHR_VALIDA_ANA.toString());
		pl.add(Projections.property("ANA.".concat(MamAnamneses.Fields.SEQ.toString())), RelatorioAnaEvoInternacaoVO.Fields.ANA_SEQ.toString());
		pl.add(Projections.property("ANA.".concat(MamAnamneses.Fields.RGT_SEQ.toString())), RelatorioAnaEvoInternacaoVO.Fields.RGT_SEQ.toString());
		pl.add(Projections.property("ANA.".concat(MamAnamneses.Fields.PAC_CODIGO.toString())), RelatorioAnaEvoInternacaoVO.Fields.COD_PAC.toString());
		
		dc.setProjection(pl);
		dc.setResultTransformer(Transformers.aliasToBean(RelatorioAnaEvoInternacaoVO.class));
	}
	
	public List<MamAnamneses> obterAnamnesePorTriagemERegistro(Long trgSeq, Long rgtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class, "t1");

		criteria.add(Restrictions.eq("t1." + MamAnamneses.Fields.TRG_SEQ.toString(), trgSeq));

		if(rgtSeq == null){
			criteria.add(Restrictions.eqProperty("t1." + MamAnamneses.Fields.RGT_SEQ.toString(), "t1." + MamEvolucoes.Fields.RGT_SEQ.toString()));
		}else{
			criteria.add(Restrictions.eq("t1." + MamAnamneses.Fields.RGT_SEQ.toString(), rgtSeq));
		}			

		criteria.add(Restrictions.eq("t1." + MamAnamneses.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull("t1." + MamAnamneses.Fields.DTHR_VALIDA.toString()));
		criteria.add(Subqueries.propertyNotIn("t1." + MamAnamneses.Fields.SEQ.toString(), subCriteriaAnamneses()));

		return executeCriteria(criteria);	
	}
	
	private DetachedCriteria subCriteriaAnamneses() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class,"t2");

		criteria.setProjection(Projections
				.projectionList().add(Projections.property("t2." + MamAnamneses.Fields.ANA_SEQ.toString())));
		
		criteria.add(Restrictions.eqProperty("t2." + MamAnamneses.Fields.ANA_SEQ.toString(), "t1.seq"));
		
		List<DominioIndPendenteAmbulatorio> pendente = new ArrayList<DominioIndPendenteAmbulatorio>();
		pendente.add(DominioIndPendenteAmbulatorio.P);
		pendente.add(DominioIndPendenteAmbulatorio.V);
		pendente.add(DominioIndPendenteAmbulatorio.E);
		pendente.add(DominioIndPendenteAmbulatorio.A);
		
		criteria.add(Restrictions.in("t2." + MamAnamneses.Fields.PENDENTE.toString(), pendente));
		
		return criteria;
	}

	public List<DominioFuncaoProfissional> obterMamAnamnesesPorCirurgia(Integer crgSeq) {

		DetachedCriteria subquery = DetachedCriteria.forClass(MamAnamneses.class, "MAN");
		subquery.createAlias("MAN." + MamAnamneses.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		subquery.createAlias("ATD." + AghAtendimentos.Fields.CIRURGIAS.toString(), "CRG", JoinType.INNER_JOIN);
		subquery.setProjection(Projections.projectionList().add(Projections.property("MAN." + MamAnamneses.Fields.SEQ.toString())));
		subquery.add(Restrictions.and(
				Restrictions.eqProperty("MAN." + MamAnamneses.Fields.SERVIDOR_MATRICULA.toString(), "MPA." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()),
				Restrictions.eqProperty("MAN." + MamAnamneses.Fields.SERVIDOR_VIN_CODIGO.toString(), "MPA." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())));
		subquery.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "MPA");
		criteria.setProjection(Projections.projectionList().add(Projections.property("MPA." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString())));
		criteria.add(Subqueries.exists(subquery));
		
		DominioFuncaoProfissional[] funcoes = { DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF };
		criteria.add(Restrictions.in("MPA." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), funcoes));
		return executeCriteria(criteria);
	}
	
	public MamAnamneses obterAnamneseCorrenteValidadaPorAtendimento(Integer seqAtendimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.ATENDIMENTO.toString(), seqAtendimento));
		criteria.add(Restrictions.eq(MamAnamneses.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNotNull(MamAnamneses.Fields.DTHR_VALIDA.toString()));
		criteria.add(Restrictions.or(Restrictions.isEmpty(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()), 
				Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString())));
		return (MamAnamneses) executeCriteriaUniqueResult(criteria);
	}

	
	private DetachedCriteria obterCriteriaAnamenesePorCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class,"ANA");
		criteria.createAlias("ANA." + MamAnamneses.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CIRURGIAS.toString(), "CRG", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return criteria;
	}
		
	public List<MamAnamneses> listarAnamenesePorCirurgia(Integer crgSeq){
		return executeCriteria(obterCriteriaAnamenesePorCirurgia(crgSeq));
	}
	
	public List<MamAnamneses> pesquisarAnamnesesVerificarPrescricao(int dias, final Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.createAlias(MamAnamneses.Fields.ATENDIMENTO.toString(), "ATD");

		criteria.add(Restrictions.eq(MamAnamneses.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));

		Calendar dataCalculada = Calendar.getInstance();
		dataCalculada.setTime(new Date());
		dataCalculada.add(Calendar.DAY_OF_YEAR, -dias);
		criteria.add(Restrictions.ge(MamAnamneses.Fields.DTHR_CRIACAO.toString(), dataCalculada.getTime()));

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		return executeCriteria(criteria);
	}


    /**
     * cursor CURSOR cur_pend_ana_int
     * @return
     */
    public List<MamAnamneses> buscaPendenciaAnamnesePorAtendimento(Integer atdSeq,
                                                                   String serMatricula,
                                                                   String serVinCodigo,
                                                                   Long hrRecAnaIntPend ){
        StringBuffer hql = new StringBuffer(470);

        hql.append(" select rgt."+MamRegistro.Fields.SEQ+", rgt."+MamRegistro.Fields.IND_SITUACAO +", ana." +MamAnamneses.Fields.IND_PENDENTE);

        hql.append(" from mam_anamneses ana, mam_registros rgt ");

        hql.append(" rgt."+MamRegistro.Fields.ATENDIMENTO.toString() + " = :atdSeq");
        hql.append(" AND ana.registro.seq = rgt."+MamRegistro.Fields.SEQ.toString()+" AND ");
        hql.append(" ana."+RapServidores.Fields.MATRICULA.toString()+ "=:serMatricula");
        hql.append(" AND ana."+RapServidores.Fields.CODIGO_VINCULO.toString()+ "=:serVinCodigo");
        Date sysDate = getCurrentDate();
        hql.append(" AND ((" +sysDate+ " - ana."+MamAnamneses.Fields.DTHR_CRIACAO.toString()+") * 24 ) <= :hrRecAnaIntPend");


        Query query = createHibernateQuery(hql.toString());
        query.setInteger("atdSeq",atdSeq);
        query.setString("serMatricula", serMatricula);
        query.setString("serVinCodigo", serVinCodigo);
        query.setLong("hrRecAnaIntPend", hrRecAnaIntPend);
        return query.list();
    }
    
    public String obterSituacaoAnamnesePorSeq(Long anaSeq){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
    	criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(), anaSeq));
    	criteria.setProjection(Projections.property(MamAnamneses.Fields.IND_PENDENTE.toString()));
    	criteria.setResultTransformer(Transformers.aliasToBean(MamAnamneses.class));
    	return (String)executeCriteriaUniqueResult(criteria);
    }
    
    public Long obterAnaSeqAnamnesePorSeq(Long anaSeq){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
    	criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(), anaSeq));
    	criteria.setProjection(Projections.property(MamAnamneses.Fields.ANA_SEQ.toString()));
    	
    	return (Long)executeCriteriaUniqueResult(criteria);
    }

    /**
     * #50743 - C1 - Consulta que retorna a anamnese relacionada à consulta do paciente.
     */
	public List<MamAnamneses> pesquisarMamAnamnesesPaciente(Integer numero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(), 
				new DominioIndPendenteAmbulatorio[] {DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V}));
		criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		return executeCriteria(criteria);
	}

	// #50745 
	public MamAnamneses obterAnamnesePorSeqEIndPendente(Long seq, DominioIndPendenteAmbulatorio indPendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MamAnamneses.Fields.IND_PENDENTE.toString(), indPendente));
		return (MamAnamneses) executeCriteriaUniqueResult(criteria);
	}

	// #50745 
	public MamAnamneses obterAnamnesePorConNumeroESeq(Integer conNumero, Long seq) {
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V };
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class, "ANA");
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.in("ANA." + MamAnamneses.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		criteria.add(Restrictions.isNull("ANA." + MamAnamneses.Fields.DTHR_MOVIMENTO.toString()));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MamAnamneses.class, "ANA1");
		subquery.setProjection(Projections.property("ANA1." + MamAnamneses.Fields.ANA_SEQ.toString()));
		subquery.add(Restrictions.eqProperty("ANA1." + MamAnamneses.Fields.ANA_SEQ.toString(), "ANA." + MamAnamneses.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.or(Restrictions.eq("ANA." + MamAnamneses.Fields.SEQ.toString(), seq), Subqueries.propertyNotIn("ANA." + MamAnamneses.Fields.SEQ.toString(), subquery)));
		
		List<MamAnamneses> lista = executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	// #50745
	public List<MamAnamneses> obterListaAnamnesePorConNumeroESeq(Integer conNumero, Long seq) {
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V };
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
		criteria.add(Restrictions.eq(MamAnamneses.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		return executeCriteria(criteria);
	}
	/**
	 * Consulta correspondente a cursor na Procedure @ORADB
	 * mamk_int_visualiza.mamp_int_v_reg_ana Consulta: select seq, ind_pendente,
	 * dthr_valida, dthr_criacao from mam_anamneses where rgt_seq = c_rgt_seq
	 * and ind_pendente in ('P','V') and dthr_mvto is null
	 * 
	 * @param rgtSeq
	 * @return
	 */
    public List<MamAnamneses> obterAnamnesePorRgtSeqIndPendentePV(Long rgtSeq){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
    	List<DominioIndPendenteAmbulatorio> listIn = Arrays.asList(
    			DominioIndPendenteAmbulatorio.P,
    			DominioIndPendenteAmbulatorio.V);
    	
    	criteria.add(Restrictions.eq(MamAnamneses.Fields.RGT_SEQ.toString(), rgtSeq));
		criteria.add(Restrictions.in(MamAnamneses.Fields.IND_PENDENTE.toString(), listIn));
    	criteria.add(Restrictions.isNull(MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
    	
    	return executeCriteria(criteria);
    }
    
    
    /**
     * #50937
     * Cursor: c_rgt 
     * @param anaSeq
     * @return rgtSeq
     */
    public Long obterRgtSeqPorAnaSeq(Long anaSeq){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class);
    	
    	criteria.setProjection(Projections.property(MamAnamneses.Fields.RGT_SEQ.toString()));
    	criteria.add(Restrictions.eq(MamAnamneses.Fields.SEQ.toString(), anaSeq));
    	return (Long) executeCriteriaUniqueResult(criteria);
    }
    
    /**
     * #50937
     * Cursor: c_ana
     * @param anaSeq
     * @return
     */
    public ResponsavelAnamneseVO obterResponsavelAnamnesePorSeq(Long anaSeq){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class, "ANA");
    	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_MATRICULA.toString()), ResponsavelAnamneseVO.Fields.MATRICULA.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_VIN_CODIGO.toString()), ResponsavelAnamneseVO.Fields.VINCULO.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), ResponsavelAnamneseVO.Fields.MATRICULA_VALIDA.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), ResponsavelAnamneseVO.Fields.VINCULO_VALIDA.toString())				
				.add(Projections.property("ANA." + MamAnamneses.Fields.DTHR_VALIDA.toString()), ResponsavelAnamneseVO.Fields.DTHR_VALIDA.toString())
		);
		
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.SEQ.toString(), anaSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResponsavelAnamneseVO.class));
    	
    	return (ResponsavelAnamneseVO) executeCriteriaUniqueResult(criteria);
    }

	// #50745 - C1
	public AnamneseAutorelacaoVO obterAnamneseComAutorelacaoPorSeq(Long pAnaSeq) {
		DominioIndPendenteAmbulatorio[] filtroIndPendente = { DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P};
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAnamneses.class, "ANA");
		criteria.createAlias("ANA." + MamAnamneses.Fields.ANAMNESES.toString(), "ANA_A", JoinType.LEFT_OUTER_JOIN,
				Restrictions.in("ANA." + MamAnamneses.Fields.IND_PENDENTE.toString(), filtroIndPendente));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ANA." + MamAnamneses.Fields.PENDENTE.toString()), AnamneseAutorelacaoVO.Fields.PENDENTE.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), AnamneseAutorelacaoVO.Fields.SERVIDOR_MATRICULA_VALIDA.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), AnamneseAutorelacaoVO.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property("ANA_A." + MamAnamneses.Fields.SERVIDOR_MATRICULA_VALIDA.toString()), AnamneseAutorelacaoVO.Fields.SERVIDOR_MATRICULA_VALIDA_RELACAO.toString())
				.add(Projections.property("ANA_A." + MamAnamneses.Fields.SERVIDOR_VIN_CODIGO_VALIDA.toString()), AnamneseAutorelacaoVO.Fields.SERVIDOR_VIN_CODIGO_VALIDA_RELACAO.toString())
		);
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.SEQ.toString(), pAnaSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AnamneseAutorelacaoVO.class));
		
		return (AnamneseAutorelacaoVO) executeCriteriaUniqueResult(criteria);
	}
	
}