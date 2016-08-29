package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.DiagnosticosPrePosOperatoriosVO;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfDescricoes;

public class MbcDiagnosticoDescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDiagnosticoDescricao> {

	private static final long serialVersionUID = -2072061942620967849L;

	public List<MbcDiagnosticoDescricao> listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(
			Integer dcgCrgSeq, DominioClassificacaoDiagnostico classificacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class);

		if (dcgCrgSeq != null){
			criteria.add(Restrictions.eq(MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		}
		if (classificacao != null){
			criteria.add(Restrictions.eq(MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString(), classificacao));
		}
		return executeCriteria(criteria);
	}
	
	public List<MbcDiagnosticoDescricao> pesquisarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(
			Integer dcgCrgSeq, DominioClassificacaoDiagnostico classificacao) {
		
		String aliasDdc = "ddc";
		String aliasCrg = "crg";
		String aliasCid = "cid";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class, aliasDdc);
		
		criteria.createAlias(aliasDdc + ponto + MbcDiagnosticoDescricao.Fields.CIRURGIA.toString(), aliasCrg);
		criteria.createAlias(aliasDdc + ponto + MbcDiagnosticoDescricao.Fields.CID.toString(), aliasCid);

		if (dcgCrgSeq != null){
			criteria.add(Restrictions.eq(MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		}
		if (classificacao != null){
			criteria.add(Restrictions.eq(MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString(), classificacao));
		}
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.PAC_CODIGO.toString()));
		criteria.addOrder(Order.asc(aliasCid + ponto + AghCid.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Efetua busca de List<MbcDiagnosticoDescricao>
	 * Consulta C9 #18527
	 */
	private DetachedCriteria obterCriteriaBuscarMbcDiagnosticoDescricao(
			Integer dcgCrgSeq, Short dcgSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class, "ddc");
		
		criteria.createAlias("ddc." + AghCid.Fields.CID.toString(), "cid", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		if(dcgSeqp != null){
			criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_SEQP.toString(), dcgSeqp));
		}
		return criteria;
	}
	
	public List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(Integer dcgCrgSeq, Short dcgSeqp){
		
		DetachedCriteria criteria = obterCriteriaBuscarMbcDiagnosticoDescricao(
				dcgCrgSeq, dcgSeqp);	
		
		criteria.addOrder(Order.desc("ddc."+MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString()));
		criteria.addOrder(Order.desc("ddc."+MbcDiagnosticoDescricao.Fields.DESTACAR.toString()));
		criteria.addOrder(Order.asc("cid." + AghCid.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaBuscarMbcDiagnosticoDescricao(
			final Integer dcgCrgSeq, final Short dcgSeqp,
			DominioClassificacaoDiagnostico classificacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class, "ddc");

		criteria.createAlias("ddc." + AghCid.Fields.CID.toString(), "cid", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString(), classificacao));
		return criteria;
	}
	
	public List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(final Integer dcgCrgSeq, final Short dcgSeqp, DominioClassificacaoDiagnostico classificacao){
		
		final DetachedCriteria criteria = obterCriteriaBuscarMbcDiagnosticoDescricao(dcgCrgSeq, dcgSeqp, classificacao);
		
		criteria.addOrder(Order.desc("ddc."+MbcDiagnosticoDescricao.Fields.DESTACAR.toString()));
		criteria.addOrder(Order.asc("cid." + AghCid.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	public List<MbcDiagnosticoDescricao> buscarMbcDiagnosticosDescricoes(final Integer dcgCrgSeq, final Short dcgSeqp){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class,"ddc");

		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		return executeCriteria(criteria);
	}
	
	public Long obterQuantidadeDiagnosticoDescricaoPorCirurgiaCidClassificacao(final Integer crgSeq, Integer cidSeq, DominioClassificacaoDiagnostico classificacao){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class, "ddc");

		criteria.add(Restrictions.eq("ddc."+MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("ddc." + AghCid.Fields.CID_SEQ.toString(), cidSeq));
		criteria.add(Restrictions.eq(MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString(), classificacao));
		
		return executeCriteriaCount(criteria);
	}
	
	public Integer buscarCidSeqMbcDiagnosticosDescricoes(Integer crgSeq, Integer phiSeq, DominioTipoPlano validade){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDiagnosticoDescricao.class,"DDC");
		criteria.setProjection(Projections.property("DDC." + MbcDiagnosticoDescricao.Fields.CID_SEQ.toString()));
		
		DetachedCriteria subCriteriaTpc = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subCriteriaTpc.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		subCriteriaTpc.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(), 
				"DDC." + MbcDiagnosticoDescricao.Fields.CID_SEQ.toString()));
		subCriteriaTpc.add(Restrictions.eq("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		subCriteriaTpc.add(Restrictions.or(Restrictions.isNull("TPC." + FatProcedHospIntCid.Fields.VALIDADE.toString()), 
				Restrictions.eq("TPC." + FatProcedHospIntCid.Fields.VALIDADE.toString(), validade)));

		criteria.add(Restrictions.eq("DDC." + MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Subqueries.exists(subCriteriaTpc));
		criteria.addOrder(Order.desc("DDC."+MbcDiagnosticoDescricao.Fields.CLASSIFICACAO.toString()));
		criteria.addOrder(Order.desc("DDC."+MbcDiagnosticoDescricao.Fields.DESTACAR.toString()));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(1);*/
		List<Object> listaResult = executeCriteria(criteria, 0, 1, null, false);
		
		return (Integer) listaResult.get(0);
	}
	
	public List<DiagnosticosPrePosOperatoriosVO> obterDiagnosticosPrePosOperatorio(
			final Date dataInicial, final Date dataFinal) {

		StringBuilder sql = new StringBuilder(600);

		sql.append(" SELECT ").append(" PAC.").append(AipPacientes.Fields.PRONTUARIO.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.PRONTUARIO.toString())
		.append(", CRG.").append(MbcCirurgias.Fields.PAC_CODIGO.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.PAC_CODIGO.toString())
		.append("	,CRG.").append(MbcCirurgias.Fields.SEQ.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.NRO_CIRURGIA.toString())
		.append("	,CRG.").append(MbcCirurgias.Fields.DATA.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.MES.toString())
		.append("	,DCG.").append(MbcDescricaoCirurgica.Fields.SEQP.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.DCG_SEQP.toString())
		.append("	,PFD.").append(MbcProfDescricoes.Fields.SER_VIN_CODIGO_PROF.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.SER_VIN_CODIGO_PROF.toString())
		.append("	,PFD.").append(MbcProfDescricoes.Fields.SER_MATRICULA_PROF.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.SER_MATRICULA_PROF.toString())
		.append("	,ESP.").append(AghEspecialidades.Fields.NOME_ESPECIALIDADE.name()).append(" AS ").append(DiagnosticosPrePosOperatoriosVO.Fields.ESPECIALIDADE.toString())
		 
		.append(" FROM ")
		.append("   AGH.").append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC ")
		.append("   ,AGH.").append(MbcDescricaoCirurgica.class.getAnnotation(Table.class).name()).append(" DCG ")
		.append("   ,AGH.").append(MbcProcEspPorCirurgias.class.getAnnotation(Table.class).name()).append(" PPCD ")
		.append("   ,AGH.").append(MbcCirurgias.class.getAnnotation(Table.class).name()).append(" CRG ")
		.append("   ,AGH.").append(AghEspecialidades.class.getAnnotation(Table.class).name()).append(" ESP ")
		.append("   ,AGH.").append(MbcDiagnosticoDescricao.class.getAnnotation(Table.class).name()).append(" DDC ")
		.append("   ,AGH.").append(MbcProfDescricoes.class.getAnnotation(Table.class).name()).append(" PFD ")

		.append(" WHERE 1=1")  
		.append("	AND CRG.").append(MbcCirurgias.Fields.DATA.name()).append(" BETWEEN ").append(":P_DT_INICIAL").append(" AND ").append(":P_DT_FINAL")
		.append("	AND CRG.").append(MbcCirurgias.Fields.SITUACAO.name()).append(" = ").append(":P_SITUACAO_CIRURGIA_RZDA")
		.append("	AND CRG.").append(MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.name()).append(" = ").append(":P_IND_DIGT_NOTA_SALA")
		.append("	AND CRG.").append(MbcCirurgias.Fields.IND_TEM_DESCRICAO.name()).append(" = ").append(":P_IND_TEM_DESCRICAO")
		.append("	AND PPCD.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.name()).append(" = ").append("CRG.").append(MbcCirurgias.Fields.SEQ.name())
		.append("	AND PPCD.").append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.name()).append(" = ").append(":P_IND_RESP_PROC")
		.append("	AND PPCD.").append(MbcProcEspPorCirurgias.Fields.SITUACAO.name()).append(" = ").append(":P_SITUACAO")		
		.append("	AND DCG.").append(MbcDescricaoCirurgica.Fields.CRG_SEQ.name()).append(" = ").append("CRG.").append(MbcCirurgias.Fields.SEQ.name())
		.append("	AND DCG.").append(MbcDescricaoCirurgica.Fields.ESP_SEQ.name()).append(" = ").append("ESP.").append(AghEspecialidades.Fields.SEQ.name())
		.append("	AND DCG.").append(MbcDescricaoCirurgica.Fields.SITUACAO.name()).append(" = ").append(":P_SITUACAO_DESCRICAO_CIRURGIA")
		.append("	AND DDC.").append(MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.name()).append(" = ").append("CRG.").append(MbcCirurgias.Fields.SEQ.name())		
		.append("	AND DDC.").append(MbcDiagnosticoDescricao.Fields.DCG_CRG_SEQ.name()).append(" = ").append("PFD.").append(MbcProfDescricoes.Fields.DCG_CRG_SEQ.name())
		.append("	AND PFD.").append(MbcProfDescricoes.Fields.TIPO_ATUACAO.name()).append(" = ").append(":P_TIPO_ATUACAO")
		.append("	AND PAC.").append(AipPacientes.Fields.CODIGO.name()).append(" = ").append("CRG.").append(MbcCirurgias.Fields.PAC_CODIGO.name());

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setTimestamp("P_DT_INICIAL", dataInicial);
		query.setTimestamp("P_DT_FINAL", dataFinal);
		query.setString("P_SITUACAO_CIRURGIA_RZDA", DominioSituacaoCirurgia.RZDA.toString());
		query.setString("P_IND_DIGT_NOTA_SALA", DominioSimNao.S.toString());
		query.setString("P_IND_TEM_DESCRICAO", DominioSimNao.S.toString());
		query.setString("P_IND_RESP_PROC", DominioIndRespProc.DESC.toString());
		query.setString("P_SITUACAO", DominioSituacao.A.toString());
		query.setString("P_SITUACAO_DESCRICAO_CIRURGIA", DominioSituacaoDescricaoCirurgia.CON.toString());
		query.setString("P_TIPO_ATUACAO", DominioTipoAtuacao.RESP.toString());

		query.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.PRONTUARIO.toString(), IntegerType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.PAC_CODIGO.toString(), IntegerType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.NRO_CIRURGIA.toString(), IntegerType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.MES.toString(), DateType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.DCG_SEQP.toString(), ShortType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.SER_VIN_CODIGO_PROF.toString(), ShortType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.SER_MATRICULA_PROF.toString(), IntegerType.INSTANCE)
				.addScalar(DiagnosticosPrePosOperatoriosVO.Fields.ESPECIALIDADE.toString(), StringType.INSTANCE)

				.setResultTransformer(Transformers.aliasToBean(DiagnosticosPrePosOperatoriosVO.class));
		
		return query.list();

	}

	public List<MbcDiagnosticoDescricao> obterMbccRetDiagDesc(
			Integer dcgCrgSeq, Short dcgSeqp) {
		return executeCriteria(obterCriteriaBuscarMbcDiagnosticoDescricao(dcgCrgSeq, dcgSeqp));
	}
	
	public List<MbcDiagnosticoDescricao> obterMbccDiagPrePos(Integer dcgCrgSeq,
			Short dcgSeqp, DominioClassificacaoDiagnostico classificacao) {
		return executeCriteria(obterCriteriaBuscarMbcDiagnosticoDescricao(dcgCrgSeq, dcgSeqp, classificacao));
	}

}
