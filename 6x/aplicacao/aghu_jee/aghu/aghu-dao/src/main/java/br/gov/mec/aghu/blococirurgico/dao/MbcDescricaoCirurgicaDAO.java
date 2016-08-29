package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;

public class MbcDescricaoCirurgicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDescricaoCirurgica> {

	private static final long serialVersionUID = -5250570352348704762L;
	
	public Integer obterQtdeMbcDescricaoCirurgicaPorCirurgia(final Integer crgSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class,"A");
		criteria.add(Restrictions.eq("A."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.setProjection(Projections.property("A."+MbcDescricaoCirurgica.Fields.SEQP.toString()));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class,"B");
		subCriteria.add(Restrictions.ne("B."+MbcDescricaoCirurgica.Fields.SITUACAO.toString(), DominioSituacaoDescricaoCirurgia.CON));
		subCriteria.setProjection(Projections.property("B."+MbcDescricaoCirurgica.Fields.SEQP.toString()));
		
		subCriteria.add(Restrictions.eqProperty("B."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), 
											    "A."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		final List<Object> result = executeCriteria(criteria);
		return result.size();
	}
	
	public MbcDescricaoCirurgica obterDescricaoCirurgicaEAtendimentoPorId(MbcDescricaoCirurgicaId descricaoCirurgicaId){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class, "descCir");
		criteria.createAlias("descCir." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "cir");
		criteria.createAlias("cir." + MbcCirurgias.Fields.ATENDIMENTO, "atd", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("descCir." + MbcDescricaoCirurgica.Fields.CRG_SEQ, descricaoCirurgicaId.getCrgSeq()));
		criteria.add(Restrictions.eq("descCir." + MbcDescricaoCirurgica.Fields.SEQP, descricaoCirurgicaId.getSeqp()));
		
		return (MbcDescricaoCirurgica) executeCriteriaUniqueResult(criteria);
	}

	public Long obterQuantidadeMbcDescricaoCirurgicaPorCirurgia(final Integer crgSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		return executeCriteriaCount(criteria);
	}

	public List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		return executeCriteria(obterCriteriaListarDescricaoCirurgicaPorSeqCirurgiaSituacao(crgSeq, situacao));
	}

	public Long listarDescricaoCirurgicaPorSeqCirurgiaSituacaoCount(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		return executeCriteriaCount(obterCriteriaListarDescricaoCirurgicaPorSeqCirurgiaSituacao(crgSeq, situacao));
	}
	
	private DetachedCriteria obterCriteriaListarDescricaoCirurgicaPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SITUACAO.toString(), situacao));
		
		return criteria;
	}
	
	public List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		
		criteria.addOrder(Order.asc(MbcDescricaoCirurgica.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Efetua busca de MbcDescricaoCirurgica
	 * Consulta C1 #18527
	 * @param crgSeq
	 * @param seqp
	 * @return
	 */
	public MbcDescricaoCirurgica buscarMbcDescricaoCirurgica(Integer crgSeq, Short seqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class, "dcg");
		criteria.createAlias("dcg." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "crg", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "cnv", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "atd", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.SEQP.toString(), seqp));
		
		return (MbcDescricaoCirurgica) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Efetua busca de MbcDescricaoCirurgica
	 * Consulta C1 #18527
	 * @param crgSeq
	 * @param seqp
	 * @return
	 */
	public List<MbcDescricaoCirurgica> listarMbcDescricaoCirurgica(Integer crgSeq, Short seqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class, "dcg");
		criteria.createAlias("dcg." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "crg", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "cnv", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", Criteria.INNER_JOIN);
		criteria.createAlias("crg." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "atd", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		if(seqp != null){
			criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.SEQP.toString(), seqp));
		}	
		
		return  executeCriteria(criteria);
	}
	
	/**
	 * Efetua busca de MbcDescricaoCirurgica
	 * Consulta c12 #18527
	 * @param dcgCrgSeq
	 * @return
	 */
	public MbcDescricaoCirurgica buscarDescricaoCirurgica(Integer crgSeq, Short seqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class, "dcg");
		criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		if(seqp != null){
			criteria.add(Restrictions.eq("dcg."+MbcDescricaoCirurgica.Fields.SEQP.toString(), seqp));
		}	
		
		return (MbcDescricaoCirurgica) executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterCountDescricaoCirurgicaPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		return executeCriteriaCount(criteria);
	}
	
	public Long obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		
		criteria.setProjection(Projections.distinct(Projections.property(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString())));
		
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_MATRICULA.toString(), servidorMatricula));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_VIN_CODIGO.toString(), servidorVinCodigo));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcDescricaoCirurgica> pesquisarDescricaoCirurgicaPorCrgSeqEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_VIN_CODIGO.toString(), serVinCodigo));
		criteria.addOrder(Order.desc(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Short obterMaiorSeqpDescricaoCirurgicaPorCrgSeqEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SERVIDOR_VIN_CODIGO.toString(), serVinCodigo));
		criteria.setProjection(Projections.max(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public Short obterMaiorSeqpDescricaoCirurgicaPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.setProjection(Projections.max(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public Short obterMenorSeqpDescricaoCirurgicaPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.setProjection(Projections.min(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	//Buscar os procedimentos realizados da descrição cirurgica quando existir descrição
	public List<ProcedimentosPOLVO> pesquisarProcedimentosComDescricaoPOL(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class, "dcg");
		criteria.createAlias("dcg." + MbcDescricaoCirurgica.Fields.MBC_PROC_DESCRICOES.toString(), "pod", Criteria.INNER_JOIN);
		criteria.createAlias("pod." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci", Criteria.INNER_JOIN);
		criteria.createAlias("dcg." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "crg", Criteria.INNER_JOIN);
				
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("crg." + MbcCirurgias.Fields.PAC_CODIGO.toString()),ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DATA.toString()), ProcedimentosPOLVO.Fields.DATA.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosPOLVO.Fields.DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SITUACAO.toString()), ProcedimentosPOLVO.Fields.SITUACAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.SEQ.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.EPR_PCI_SEQ.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString()), ProcedimentosPOLVO.Fields.TEM_DESCRICAO.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), ProcedimentosPOLVO.Fields.DIGITA_NOTA_SALA.toString())
				.add(Projections.property("crg." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()),ProcedimentosPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.or(Restrictions.eq("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString(), Boolean.TRUE),
				Restrictions.isNull("crg." + MbcCirurgias.Fields.TEM_DESCRICAO.toString())));		
		criteria.add(Restrictions.eq("dcg." + MbcDescricaoCirurgica.Fields.SITUACAO.toString(),
				DominioSituacaoDescricaoCirurgia.CON));
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(),
				DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));
		
		criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.PAC_CODIGO.toString(),codigo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));
		
		return executeCriteria(criteria);
	}	
	public List<MbcDescricaoCirurgica> pesquisarDescricaoCirurgicaPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.addOrder(Order.desc(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MbcDescricaoCirurgica> listarDescCirurgicaPorSeqESituacaoOrdenadasPorSeqp(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoCirurgica.class);
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcDescricaoCirurgica.Fields.SITUACAO.toString(), situacao));
		criteria.addOrder(Order.asc(MbcDescricaoCirurgica.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Web Service #38487
	 * Utilizado na estória #26325
	 * @param pacCodigo
	 * @param listaUnfSeq
	 * @param dataCirurgia
	 * @return
	 */
	public List<Object[]> obterCirurgiaDescCirurgicaPaciente(Integer pacCodigo, List<Short> listaUnfSeq, Date dataCirurgia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.DESCRICOES_CIRURGIAS.toString(), "DCG", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("CRG." + MbcCirurgias.Fields.SEQ.toString()))
				.add(Projections.property("DCG." + MbcDescricaoCirurgica.Fields.CRG_SEQ.toString()))
				.add(Projections.property("DCG." + MbcDescricaoCirurgica.Fields.SEQP.toString()));	

		criteria.setProjection(projection);	
		
		if(dataCirurgia != null){
			criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia, new Date()));
		}
		
		criteria.add(Restrictions.ne("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		
		if(listaUnfSeq != null && !listaUnfSeq.isEmpty()){
			criteria.add(Restrictions.in("CRG." + MbcCirurgias.Fields.UNF_SEQ.toString(), listaUnfSeq));
		}
		
		if(pacCodigo != null){
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		
		criteria.addOrder(Order.asc("CRG." + MbcCirurgias.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
}
