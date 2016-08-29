package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.CursorCProcDescricaoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcDescricoesId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoMaterial;

public class MbcProcDescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProcDescricoes> {

	private static final long serialVersionUID = 3058256310045528401L;


	/**
	 * Efetua busca de MbcProcDescricoes
	 * Consulta MbcProcDescricoes por DCG_CRG_SEQ e SEQP
	 * Consulta C5 #18527
	 * @param dcgCrgSeq
	 * @param seqp
	 * @return
	 */
	public List<MbcProcDescricoes> buscarProcDescricoes(Integer dcgCrgSeq, Integer dCgseqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "pod");
		criteria.createAlias("pod." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_SEQP.toString(), dCgseqp.shortValue()));
		
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Consulta MbcProcDescricoes por DCG_CRG_SEQ e DCG_SEQP
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @return
	 */
	public MbcProcDescricoes buscarProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "pod");
		criteria.createAlias("pod." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		List<MbcProcDescricoes> procDescricoes = executeCriteria(criteria);
		MbcProcDescricoes procDescricoesRetorno = null;
		
		if (!procDescricoes.isEmpty()){
			procDescricoesRetorno = procDescricoes.get(0);
		}
		return procDescricoesRetorno;
	}
	
	public List<MbcProcDescricoes> obterProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp, final String orderBy){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class);
		criteria.createAlias(MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString());
		
		criteria.add(Restrictions.eq(MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq(MbcProcDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.addOrder(Order.asc(orderBy));
		
		return executeCriteria(criteria);
	}
	
	public Long obterProcDescricoesCount(Integer dcgCrgSeq, Short dcgSeqp){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "pod");
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("pod."+MbcProcDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		return executeCriteriaCount(criteria);
	}
	
	
	public List<CursorCProcDescricaoVO> obterCursorCProcDescricaoVO(Integer dcgCrgSeq, DominioSituacao situacao){
		
		final StringBuilder sql = new StringBuilder(300);

		sql.append(" SELECT DISTINCT ")		
		   .append("   POD.").append(MbcProcDescricoes.Fields.DCG_CRG_SEQ.name()).append(" as ").append(CursorCProcDescricaoVO.Fields.DCG_CRG_SEQ.toString())	
		   .append(" , POD.").append(MbcProcDescricoes.Fields.PCI_SEQ.name()).append(" as ").append(CursorCProcDescricaoVO.Fields.PCI_SEQ.toString())
		   .append(" , POD.").append(MbcProcDescricoes.Fields.IND_CONTAMINACAO.name()).append(" as ").append(CursorCProcDescricaoVO.Fields.CONTAMINACAO.toString())
//--,        ESP.SEQ, -- TIRADO EM 07/07/2004 POR TETI
//---        ESP.ESP_SEQ
		   
		   .append(" FROM ")
		   .append("       AGH.").append(MbcCirurgias.class.getAnnotation(Table.class).name()).append(" CRG ")		
		   .append("     , AGH.").append(MbcProcDescricoes.class.getAnnotation(Table.class).name()).append(" POD ")		
		   .append("     , AGH.").append(MbcEspecialidadeProcCirgs.class.getAnnotation(Table.class).name()).append(" EPR ")		
		   .append("     , AGH.").append(AghEspecialidades.class.getAnnotation(Table.class).name()).append(" ESP ")		
		   
		   .append(" WHERE 1=1 ")
		   .append("  AND POD.").append(MbcProcDescricoes.Fields.DCG_CRG_SEQ.name()).append(" = :PRM_C_DCG_CRG_SEQ ")
		   .append("  AND POD.").append(MbcProcDescricoes.Fields.DCG_CRG_SEQ.name()).append(" = CRG.").append(MbcCirurgias.Fields.SEQ.name())
		   .append("  AND EPR.").append(MbcEspecialidadeProcCirgs.Fields.ESP_SEQ.name()).append(" = ESP.").append(AghEspecialidades.Fields.SEQ.name())
		   .append("  AND EPR.").append(MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.name()).append(" = POD.").append(MbcProcDescricoes.Fields.PCI_SEQ.name())
		   
		   .append("  AND COALESCE(ESP.").append(AghEspecialidades.Fields.ESP_SEQ.name()).append(",ESP.")
		   					.append(AghEspecialidades.Fields.SEQ.name()).append(") = CRG.").append(MbcCirurgias.Fields.ESP_SEQ.name())
		   
   		   .append("  AND EPR.").append(MbcEspecialidadeProcCirgs.Fields.SITUACAO.name()).append(" = :PRM_SITUACAO ")

   		   .append(" ORDER BY 1,2 ");
		
		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("PRM_C_DCG_CRG_SEQ", dcgCrgSeq);
		query.setParameter("PRM_SITUACAO", situacao.toString());
		
		final List<CursorCProcDescricaoVO> vos = query.addScalar(CursorCProcDescricaoVO.Fields.DCG_CRG_SEQ.toString(), IntegerType.INSTANCE)
											          .addScalar(CursorCProcDescricaoVO.Fields.PCI_SEQ.toString(),IntegerType.INSTANCE)
											          .addScalar(CursorCProcDescricaoVO.Fields.CONTAMINACAO.toString(),StringType.INSTANCE)
											 
											          .setResultTransformer(Transformers.aliasToBean(CursorCProcDescricaoVO.class)).list();
		
		return vos;
	}
	
	public Integer obterProximoSeqp(MbcProcDescricoesId id){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "POD");
		
		criteria.add(Restrictions.eq(MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), id.getDcgCrgSeq()));
		criteria.add(Restrictions.eq(MbcProcDescricoes.Fields.DCG_SEQP.toString(), id.getDcgSeqp()));
		
		criteria.setProjection(Projections.max(MbcProcDescricoes.Fields.SEQP.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria, false);
	}
	
	public List<MbcProcDescricoes> listarProcDescricoesComProcedimentoAtivo(Integer dcgCrgSeq, Short dcgSeqp){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "POD");
		criteria.createAlias(MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PROC");
		
		criteria.add(Restrictions.eq("POD."+MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("POD."+MbcProcDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("PROC." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("PROC."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa, Short videoLaparoscopia,
			Date dtRealizCrgVideo, Date dtRealizCrgOrtese, Date dtRealizCrgSemVideo) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "POD");
		criteria.createAlias("POD." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI");
		criteria.createAlias("POD." + MbcProcDescricoes.Fields.DESCRICAO_CIRURGICA.toString(), "DCG");
		criteria.createAlias("DCG." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString())
						, ProcedRealizadoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.IND_CONTAMINACAO.toString())
						, ProcedRealizadoVO.Fields.IND_CONTAMINACAO.toString())
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString())
						, ProcedRealizadoVO.Fields.DTHR_INICIO_CIRG.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString())
						, ProcedRealizadoVO.Fields.DCG_CRG_SEQ.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.DCG_SEQP.toString())
						, ProcedRealizadoVO.Fields.DCG_SEQP.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.SEQP.toString())
						, ProcedRealizadoVO.Fields.SEQP.toString()));
		
		if (strPesquisa != null && !strPesquisa.isEmpty()) {
			criteria.add(Restrictions.ilike("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq("DCG." + MbcDescricaoCirurgica.Fields.SITUACAO.toString(), DominioSituacaoDescricaoCirurgia.CON));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		DetachedCriteria subSelectProcedGrupo = this.obterCriteriaProcedGrupo(videoLaparoscopia, dtRealizCrgVideo);
		DetachedCriteria subSelectRmrPacientes = this.obterCriteriaRmrPacientes();
		
		Criterion crit1 = Subqueries.propertyIn("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), subSelectProcedGrupo);
		Criterion crit2 = Subqueries.propertyIn("CRG." + MbcCirurgias.Fields.SEQ.toString(), subSelectRmrPacientes);
		Criterion crit3 = Restrictions.ge("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dtRealizCrgOrtese);
		Criterion crit4 = Restrictions.ge("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dtRealizCrgSemVideo);
		
		criteria.add(Restrictions.or(crit1,
				Restrictions.or(Restrictions.and(crit2, crit3), crit4)));
		
		criteria.addOrder(Order.asc("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()))
			.addOrder(Order.desc("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedRealizadoVO.class));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaProcedGrupo(Short videoLaparoscopia, Date dtRealizCrgVideo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoPorGrupo.class, "PGR");
		criteria.createAlias("PGR." + MbcProcedimentoPorGrupo.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.setProjection(Projections.property("PGR." + MbcProcedimentoPorGrupo.Fields.PCI_SEQ.toString()));
		criteria.add(Restrictions.eq("PGR." + MbcProcedimentoPorGrupo.Fields.GPC_SEQ.toString(), videoLaparoscopia));
		criteria.add(Restrictions.ge("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dtRealizCrgVideo));
		
		return criteria;
	}
	
	private DetachedCriteria obterCriteriaRmrPacientes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class, "IPS");
		criteria.createAlias("IPS." + SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(), "RMP");
		criteria.createAlias("IPS." + SceItemRmps.Fields.SCE_ESTQ_ALMOX.toString(), "EAL");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.setProjection(Projections.property("RMP." + SceRmrPaciente.Fields.CRG_SEQ.toString()));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_CCIH.toString(), DominioSimNao.S));
		
		return criteria;
	}
	
	public ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "POD");
		criteria.createAlias("POD." + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI");
		criteria.createAlias("POD." + MbcProcDescricoes.Fields.DESCRICAO_CIRURGICA.toString(), "DCG");
		criteria.createAlias("DCG." + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString())
						, ProcedRealizadoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PCI." + MbcProcedimentoCirurgicos.Fields.IND_CONTAMINACAO.toString())
						, ProcedRealizadoVO.Fields.IND_CONTAMINACAO.toString())
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString())
						, ProcedRealizadoVO.Fields.DTHR_INICIO_CIRG.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString())
						, ProcedRealizadoVO.Fields.DCG_CRG_SEQ.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.DCG_SEQP.toString())
						, ProcedRealizadoVO.Fields.DCG_SEQP.toString())
				.add(Projections.property("POD." + MbcProcDescricoes.Fields.SEQP.toString())
						, ProcedRealizadoVO.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eq("POD." + MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("POD." + MbcProcDescricoes.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("POD." + MbcProcDescricoes.Fields.SEQP.toString(), seqp));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedRealizadoVO.class));
		
		return (ProcedRealizadoVO) executeCriteriaUniqueResult(criteria);
	}

}