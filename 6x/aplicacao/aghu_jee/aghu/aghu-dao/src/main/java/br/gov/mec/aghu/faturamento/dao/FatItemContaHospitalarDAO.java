package br.gov.mec.aghu.faturamento.dao;

import static org.hibernate.criterion.Projections.projectionList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.vo.CursorAtoFatcBuscaServClassVO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoObrigatorioVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaQtdCompVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiasCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorIchPhiMatVO;
import br.gov.mec.aghu.faturamento.vo.CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.ItemParaTrocaConvenioVO;
import br.gov.mec.aghu.faturamento.vo.ListarItensContaHospitalarRnCthcAtuAgrupichVO;
import br.gov.mec.aghu.faturamento.vo.MenorDataValidacaoApacVO;
import br.gov.mec.aghu.faturamento.vo.RateioValoresSadtPorPontosVO;
import br.gov.mec.aghu.internacao.vo.FatItemContaHospitalarVO;
import br.gov.mec.aghu.internacao.vo.RelatorioIntermediarioLancamentosContaVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAtoObrigatorioProced;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class FatItemContaHospitalarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatItemContaHospitalar> {

	private static final long serialVersionUID = 2019652688519924189L;
	public static final String NOTA_FISCAL = "notaFiscal";
	public static final String NUMERO_FORNECEDOR = "numeroFornecedor";
	public static final String RMR_SEQ = "sceRmrPaciente";
	public static final String PROCED = "PROCED.";
	public static final String ICH = "ICH.";	

	private DetachedCriteria obterDetachedCriteriaFatItemContaHospitalar() {
		return DetachedCriteria.forClass(FatItemContaHospitalar.class);
	}
	
	public List<FatItemContaHospitalar> listarItensContaHospitalar(final Integer firstResult, final Integer maxResult, String orderProperty,
			final boolean asc, final Integer cthSeq, final Integer procedimentoSeq, final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao, final DominioIndOrigemItemContaHospitalar origem, final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "ICH");
		
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() , "PROC_INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString() , "UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.SERVIDOR.toString() , "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString() , "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.SERVIDOR_ANEST.toString() , "SERV_ANEST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ANEST." + RapServidores.Fields.PESSOA_FISICA.toString() , "PESS_ANEST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), "CTH", JoinType.LEFT_OUTER_JOIN);
		
		final DetachedCriteria subQueryConvenioCodigo = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioCodigo.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.CODIGO.toString())));
		subQueryConvenioCodigo.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioCodigo.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.SEQ.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ));

		final DetachedCriteria subQueryConvenioSeq = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioSeq.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.SEQ.toString())));
		subQueryConvenioSeq.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioSeq.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.CODIGO.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO));

		final DetachedCriteria subQuery = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "PROCED");

		subQuery.setProjection(Projections.projectionList().add(
					Projections.property(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())));
		
		if (grupoSUS != null) {
		    subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS.getVlrNumerico().shortValue()));
		}
		
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eqProperty(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), ICH				
				+ FatItemContaHospitalar.Fields.PHI_SEQ.toString()));

		if (procedimentoSeq != null) {
			subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), procedimentoSeq));
		}
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), subQueryConvenioCodigo));
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), subQueryConvenioSeq));

		criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		if (removerFiltro == null || !removerFiltro) {
			criteria.add(Subqueries.propertyIn(ICH + FatItemContaHospitalar.Fields.PHI_SEQ.toString(), subQuery));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.UNF_SEQ.toString(), unfSeq));
		}

		if (origem != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), origem));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (dtRealizado != null) {
			final Calendar calIni = Calendar.getInstance();
			calIni.setTime(dtRealizado);
			calIni.set(Calendar.HOUR_OF_DAY, 0);
			calIni.set(Calendar.MINUTE, 0);
			calIni.set(Calendar.SECOND, 0);

			final Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(dtRealizado);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.SECOND, 59);

			criteria.add(Restrictions.between(ICH + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), calIni.getTime(), calEnd.getTime()));
		}

		if(StringUtils.isEmpty(orderProperty)){
			orderProperty = FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString();
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<FatItemContaHospitalar> listarItensContaHospitalar(final Integer cthSeq, final Integer procedimentoSeq, final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao, final DominioIndOrigemItemContaHospitalar origem, final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "ICH");
		
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() , "PROC_INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString() , "UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.SERVIDOR.toString() , "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString() , "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.SERVIDOR_ANEST.toString() , "SERV_ANEST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ANEST." + RapServidores.Fields.PESSOA_FISICA.toString() , "PESS_ANEST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), "CTH", JoinType.LEFT_OUTER_JOIN);
		
		final DetachedCriteria subQueryConvenioCodigo = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioCodigo.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.CODIGO.toString())));
		subQueryConvenioCodigo.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioCodigo.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.SEQ.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ));

		final DetachedCriteria subQueryConvenioSeq = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioSeq.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.SEQ.toString())));
		subQueryConvenioSeq.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioSeq.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.CODIGO.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO));

		final DetachedCriteria subQuery = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "PROCED");

		subQuery.setProjection(Projections.projectionList().add(
					Projections.property(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())));
		
		if (grupoSUS != null) {
		    subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS.getVlrNumerico().shortValue()));
		}
		
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eqProperty(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), ICH				
				+ FatItemContaHospitalar.Fields.PHI_SEQ.toString()));

		if (procedimentoSeq != null) {
			subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), procedimentoSeq));
		}
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), subQueryConvenioCodigo));
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), subQueryConvenioSeq));

		criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		
		if (removerFiltro == null || !removerFiltro) {
			criteria.add(Subqueries.propertyIn(ICH + FatItemContaHospitalar.Fields.PHI_SEQ.toString(), subQuery));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.UNF_SEQ.toString(), unfSeq));
		}

		if (origem != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), origem));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (dtRealizado != null) {
			final Calendar calIni = Calendar.getInstance();
			calIni.setTime(dtRealizado);
			calIni.set(Calendar.HOUR_OF_DAY, 0);
			calIni.set(Calendar.MINUTE, 0);
			calIni.set(Calendar.SECOND, 0);

			final Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(dtRealizado);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.SECOND, 59);

			criteria.add(Restrictions.between(ICH + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), calIni.getTime(), calEnd.getTime()));
		}
		
		criteria.addOrder(Order.asc(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()));
		
		return executeCriteria(criteria);
	}

	public Long listarItensContaHospitalarCount(final Integer cthSeq, final Integer procedimentoSeq, final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao, final DominioIndOrigemItemContaHospitalar origem, final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "ICH");

		final DetachedCriteria subQueryConvenioCodigo = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioCodigo.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.CODIGO.toString())));
		subQueryConvenioCodigo.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioCodigo.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.SEQ.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ));

		final DetachedCriteria subQueryConvenioSeq = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		subQueryConvenioSeq.setProjection(Projections.projectionList().add(Projections.property(FatConvenioSaudePlano.Fields.SEQ.toString())));
		subQueryConvenioSeq.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		subQueryConvenioSeq.add(Restrictions.eqProperty(FatConvenioSaudePlano.Fields.CODIGO.toString(), PROCED
				+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO));

		final DetachedCriteria subQuery = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "PROCED");

		subQuery.setProjection(Projections.projectionList().add(
					Projections.property(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())));
		
		if (grupoSUS != null) {
		   subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS.getVlrNumerico().shortValue()));
		}
		
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.IPH_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SITUACAO.toString(), DominioSituacao.A));
		subQuery.add(Restrictions.eqProperty(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), ICH
				+ FatItemContaHospitalar.Fields.PHI_SEQ.toString()));

		if (procedimentoSeq != null) {
			subQuery.add(Restrictions.eq(PROCED + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), procedimentoSeq));
		}
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), subQueryConvenioCodigo));
		subQuery.add(Subqueries.propertyIn(PROCED + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), subQueryConvenioSeq));

		criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		if (removerFiltro == null || !removerFiltro) {
			criteria.add(Subqueries.propertyIn(ICH + FatItemContaHospitalar.Fields.PHI_SEQ.toString(), subQuery));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.UNF_SEQ.toString(), unfSeq));
		}

		if (origem != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), origem));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ICH + FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (dtRealizado != null) {
			final Calendar calIni = Calendar.getInstance();
			calIni.setTime(dtRealizado);
			calIni.set(Calendar.HOUR_OF_DAY, 0);
			calIni.set(Calendar.MINUTE, 0);
			calIni.set(Calendar.SECOND, 0);

			final Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(dtRealizado);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.SECOND, 59);

			criteria.add(Restrictions.between(ICH + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), calIni.getTime(), calEnd.getTime()));
		}

		return executeCriteriaCount(criteria);
	}

	/**
	 * Metodo para buscar especialidade filtrando Por Conta Hospitalar,
	 * Procedimento Hospitalar Interno Realizado e por ind_origem do Item Conta
	 * Hospitalar = 'BCC'
	 * 
	 * @param cthSeq
	 * @param phiSeqRealizado
	 * @return FatItemContaHospitalar
	 */
	public FatItemContaHospitalar buscarItensContaHospitalarPorCtaHospEProcedHospInternoRealizado(final Integer cthSeq, final Integer phiSeqRealizado) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatItemContaHospitalar();

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
				+ FatProcedHospInternos.Fields.SEQ.toString(), phiSeqRealizado));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioIndOrigemItemContaHospitalar.BCC));

		final List<FatItemContaHospitalar> lista = executeCriteria(criteria, 0, 1, null, true);

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null;
	}

	public FatItemContaHospitalar obterItensContaHospitalarPorContaHospitalarePHI(final Integer cthSeq, final Integer phiSeq) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatItemContaHospitalar();

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
				+ FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));

		final List<FatItemContaHospitalar> lista = executeCriteria(criteria, 0, 1, null, true);

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null;
	}

	/**
	 * Consulta para listar FatItemContaHospitalar filtrando por indSituacao
	 * ativa, por ContaHospitalar e agrupando o resultado pelo
	 * procedimentoHospitalarInterno e pela dataRealizado do
	 * ItemContaHospitalar.
	 * 
	 * PS: Utilizado HQL devido ao TRUNC no WHERE
	 * 
	 * @param cthSeq
	 * @return List<FatItemContaHospitalarVO>
	 */
	public List<FatItemContaHospitalarVO> listarFatItemContaHospitalarPorContaHospitalar(final Integer cthSeq) {
		final StringBuilder hql = new StringBuilder(400);

		hql.append(" SELECT phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(", ");
		hql.append(" cast(day(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ) as integer) , ");
		hql.append(" cast (month(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ) as integer) , ");
		hql.append(" cast (year(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(") as integer) , ");
		hql.append(" cast ( COUNT(*) as integer ) ");
		hql.append(" FROM ");
		hql.append(FatItemContaHospitalar.class.getSimpleName());
		hql.append(" ich ");
		hql.append(" JOIN ich.");
		hql.append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" phi ");
		hql.append(" WHERE ich.");
		hql.append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" = :indSituacao ");
		hql.append(" AND phi.");
		hql.append(FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL.toString());
		hql.append(" is not null ");
		hql.append(" AND ich.");
		hql.append(FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString());
		hql.append('.');
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" GROUP BY phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" , ");
		hql.append(" day(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ), ");
		hql.append(" month(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ), ");
		hql.append(" year(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(") ");
		hql.append(" HAVING COUNT(*) > 1 ");

		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("indSituacao", DominioSituacao.A);
		query.setParameter("cthSeq", cthSeq);

		final List<Object[]> listaObjetos = query.list();
		final List<FatItemContaHospitalarVO> lista = new ArrayList<FatItemContaHospitalarVO>();
		final Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			final Object[] obj = it.next();
			final FatItemContaHospitalarVO vo = new FatItemContaHospitalarVO();

			if (obj[0] != null) {
				vo.setPhiSeq((Integer) obj[0]);
			}

			if (obj[1] != null && obj[2] != null && obj[3] != null) {
				final Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, (Integer) obj[1]);
				c.set(Calendar.DAY_OF_MONTH, (Integer) obj[2]);
				c.set(Calendar.DAY_OF_MONTH, (Integer) obj[3]);
				vo.setDthrRealizado(c.getTime());
			}

			if (obj[4] != null) {
				vo.setCount((Integer) obj[4]);
			}

			lista.add(vo);
		}

		return lista;
	}

	/**
	 * Listar ItensContaHospitalar ativos por ProcedHospInterno, DthrRealizado e
	 * ContaHosp.
	 * 
	 * PS: Utilizado HQL devido ao TRUNC no WHERE
	 * 
	 * @param phiSeq
	 * @param dthrRealizado
	 * @param cthSeq
	 * @return List<FatItemContaHospitalar>
	 */
	public List<FatItemContaHospitalar> listarItensContaHospAtivaPorProcedHospInternoDthrRealizadoEContaHosp(final Integer phiSeq,
			final Date dthrRealizado, final Integer cthSeq) {
		final StringBuilder hql = new StringBuilder(200);

		hql.append(" FROM ");
		hql.append(FatItemContaHospitalar.class.getSimpleName());
		hql.append(" ich ");
		hql.append(" WHERE ");
		hql.append(" ich.");
		hql.append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" = :indSituacao ");
		hql.append(" AND ich.");
		hql.append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append('.');
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :phiSeq ");
		hql.append(" AND day(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ) =:dia ");
		hql.append(" AND month(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ) =:mes ");
		hql.append(" AND year(ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" ) =:ano ");
		hql.append(" AND ich.");
		hql.append(FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString());
		hql.append('.');
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("indSituacao", DominioSituacao.A);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("cthSeq", cthSeq);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(dthrRealizado);
		query.setInteger("dia", cal.get(Calendar.DATE));
		query.setInteger("mes", cal.get(Calendar.MONTH) + 1);
		query.setInteger("ano", cal.get(Calendar.YEAR));

		return query.list();
	}

	/**
	 * Listar ItensContaHospitalar Com Origem 'Afa' e também filtrando pelo seq
	 * da contaHospitalar.
	 * 
	 * @param cthSeq
	 * @return List<FatItemContaHospitalar>
	 */
	public List<FatItemContaHospitalar> listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(final Integer cthSeq) {

		final DetachedCriteria criteria = obterDetachedCriteriaFiltrandoPorOrigemEPorContaHospitalarSeq(cthSeq,
				DominioIndOrigemItemContaHospitalar.AFA);

		return executeCriteria(criteria);
	}

	/**
	 * Listar ItensContaHospitalar Com Origem 'ANU' e também filtrando pelo seq
	 * da contaHospitalar e pelo seq dos procedimento hosp interno.
	 * 
	 * @param cthSeq
	 * @return List<FatItemContaHospitalar>
	 */
	public List<FatItemContaHospitalar> listarItensContaHospitalarComOrigemAnuFiltrandoPorContaHospitalarEProcedHospInt(final Integer cthSeq,
			final Integer phiSeq) {

		final DetachedCriteria criteria = obterDetachedCriteriaFiltrandoPorOrigemEPorContaHospitalarSeq(cthSeq,
				DominioIndOrigemItemContaHospitalar.ANU);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
				+ FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterDetachedCriteriaFiltrandoPorOrigemEPorContaHospitalarSeq(final Integer cthSeq,
			final DominioIndOrigemItemContaHospitalar origem) {
		final DetachedCriteria criteria = this.obterDetachedCriteriaFatItemContaHospitalar();
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString() + "." + FatContasHospitalares.Fields.SEQ.toString(),
				cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), origem));

		return criteria;
	}

	/**
	 * Obtem próxima sequencia
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Short obterProximoSeq(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.setProjection(Projections.max(FatItemContaHospitalar.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		Short result = (Short) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = (short) 0;
		}
		result++;

		return result;
	}

	public Short buscaMinSeq(final Integer cthSeq, final Integer phiSeq) {
		Short retorno = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.setProjection(projectionList().add(Projections.min(FatItemContaHospitalar.Fields.SEQ.toString())));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		final List<Object> lista = this.executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && lista.size() > 0) {
			retorno = (Short) lista.get(0);
		}

		return retorno;
	}

	public FatItemContaHospitalar obterItensContasHospitalaresEspecialidadeCirurgia(final Integer cthSeq, final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.isNotNull(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
				+ FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString()));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Long verificaProcedimentoHospitalarInternoCount(final Integer cthSeq, final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		return executeCriteriaCount(criteria);
	}

	public List<FatItemContaHospitalar> listarItensContaHospitalarAtivos(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.A, DominioSituacaoItenConta.V }));

		return executeCriteria(criteria);
	}

	public List<Short> listarItensContaHospitalar(final Integer cthSeq, final Integer[] phis) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		
		criteria.setProjection(Projections.property(FatItemContaHospitalar.Fields.SEQ.toString()));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phis));

		return executeCriteria(criteria, 0, 1, null, false);
	}

	public List<FatItemContaHospitalar> listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(final Integer cthSeq,
			final Integer phiSeq, final Integer phiTomografiaSeq, final Integer[] phiSeqsTomografia) {

		final List<FatItemContaHospitalar> listaItensContaHospitalar = new ArrayList<FatItemContaHospitalar>();

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.or(
				Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq),
				Restrictions.and(
						Restrictions.in(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeqsTomografia),
						Restrictions.sqlRestriction(" ? = ? ", new Object[] { phiSeq, phiTomografiaSeq }, new Type[] { IntegerType.INSTANCE,
								IntegerType.INSTANCE }))));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		List<FatItemContaHospitalar> lst = executeCriteria(criteria, true);
		if (lst != null && !lst.isEmpty()) {
			listaItensContaHospitalar.addAll(lst);
		}

		// union

		criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.or(
				Restrictions.eq(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
						+ FatProcedHospInternos.Fields.PHI_SEQ.toString(), phiSeq),
				Restrictions.and(
						Restrictions.in(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeqsTomografia),
						Restrictions.sqlRestriction(" ? = ? ", new Object[] { phiSeq, phiTomografiaSeq }, new Type[] { IntegerType.INSTANCE,
								IntegerType.INSTANCE }))));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		lst = executeCriteria(criteria, true);
		if (lst != null && !lst.isEmpty()) {
			for (final FatItemContaHospitalar FatItemContaHospitalar : lst) {
				if (!listaItensContaHospitalar.contains(FatItemContaHospitalar)) {
					listaItensContaHospitalar.add(FatItemContaHospitalar);
				}
			}
		}

		Collections.sort (listaItensContaHospitalar, new listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoesComparator());
		
		return listaItensContaHospitalar;
	}
	
	private class listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoesComparator implements Comparator<FatItemContaHospitalar> {  
	     public int compare(FatItemContaHospitalar p1,FatItemContaHospitalar p2) {
	    	 return p1.getId().getSeq().compareTo(p2.getId().getSeq());
	     }  
	}  

	public List<CursorIchPhiMatVO> obterItensContasHospitalaresMat(final Integer cthSeq, final Integer phiSeq) {
		
		final StringBuilder sql = new StringBuilder(500);
		
		sql.append("SELECT")
		   .append(" ICH1.SEQ AS ").append(CursorIchPhiMatVO.Fields.SEQ.toString()).append(", ") 
		   .append(" ICH1.PHI_SEQ AS ").append(CursorIchPhiMatVO.Fields.PHI_SEQ.toString()).append(", ") 
		   .append(" ICH1.IPS_RMP_SEQ AS ").append(CursorIchPhiMatVO.Fields.IPS_RMP_SEQ.toString()).append(", ") 
		   .append(" ICH1.IPS_NUMERO AS ").append(CursorIchPhiMatVO.Fields.IPS_NUMERO.toString()) 
		   
		   .append(" FROM ")
   		   .append("     AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH1  ")
   		   
		   .append(" WHERE 1=1 ")
		   .append(" AND ICH1.IND_SITUACAO = :PRM_IND_SITUACAO ")
		   .append(" AND ICH1.PHI_SEQ = :PRM_PHI_SEQ ")
		   .append(" AND ICH1.CTH_SEQ = :PRM_CTH_SEQ ")
		   
		   .append(" UNION ")
		   
		   .append("SELECT")
		   .append(" ICH.SEQ AS ").append(CursorIchPhiMatVO.Fields.SEQ.toString()).append(", ") 

		   // phi.phi_seq phi_seq ETB 16062008 substituido pela linha de baixo
		   .append(" ICH.PHI_SEQ AS ").append(CursorIchPhiMatVO.Fields.PHI_SEQ.toString()).append(", ") 
		   .append(" ICH.IPS_RMP_SEQ AS ").append(CursorIchPhiMatVO.Fields.IPS_RMP_SEQ.toString()).append(", ") 
		   .append(" ICH.IPS_NUMERO AS ").append(CursorIchPhiMatVO.Fields.IPS_NUMERO.toString()) 
		   
		   .append(" FROM ")
   		   .append("     AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH,  ")
   		   .append("     AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI  ")
   		   
		   .append(" WHERE 1=1 ")
		   .append(" AND ICH.IND_SITUACAO = :PRM_IND_SITUACAO ")
		   .append(" AND ICH.CTH_SEQ = :PRM_CTH_SEQ ")
		   .append(" AND ICH.PHI_SEQ = PHI.SEQ ")
		   .append(" AND PHI.PHI_SEQ = :PRM_PHI_SEQ ")
		;
		
		final SQLQuery query = createSQLQuery(sql.toString());

		query.setParameter("PRM_IND_SITUACAO", DominioSituacaoItenConta.A.toString());
		query.setParameter("PRM_CTH_SEQ", cthSeq);
		query.setParameter("PRM_PHI_SEQ", phiSeq);
		query.setCacheable(true);
		
		final List<CursorIchPhiMatVO> result = query.addScalar(CursorIchPhiMatVO.Fields.SEQ.toString(),ShortType.INSTANCE)
												    .addScalar(CursorIchPhiMatVO.Fields.PHI_SEQ.toString(),IntegerType.INSTANCE)
												    .addScalar(CursorIchPhiMatVO.Fields.IPS_RMP_SEQ.toString(),IntegerType.INSTANCE)
												    .addScalar(CursorIchPhiMatVO.Fields.IPS_NUMERO.toString(),ShortType.INSTANCE)
												    .setResultTransformer(Transformers.aliasToBean(CursorIchPhiMatVO.class)).list();
		return result;
	}

	/**
	 * Busca primeiro item de conta de cirurgias não canceladas (situacao
	 * diferente de C ou D)
	 * 
	 * @param pCthSeq
	 * @param phiSeq
	 * @return
	 */
	public FatItemContaHospitalar buscarPrimeiroItemContaCirurgiasNaoCanceladas(final Integer pCthSeq, final Integer phiSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));

		criteria.add(Restrictions.or(Restrictions.isNotNull(FatItemContaHospitalar.Fields.CIRURGIA.toString()),
				Restrictions.isNotNull(FatItemContaHospitalar.Fields.PROCEDIMENTO_AMB_REALIZADO.toString())));

		final DetachedCriteria subQuery = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		subQuery.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		subQuery.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		subQuery.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));
		subQuery.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.C, DominioSituacaoItenConta.D })));
		subQuery.setProjection(Projections.property(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()));

		criteria.add(Subqueries.propertyEq(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), subQuery));

		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Busca primeiro item de conta de todas as cirurgias para o horário do
	 * realizado da conta com situacao ativo (A), pontuação (P) ou valor (V)
	 * 
	 * @param pCthSeq
	 * @param pPhiRealizado
	 * @return
	 */
	public FatItemContaHospitalar buscarPrimeiroItemContaCirurgiasAPVHorarioConta(final Integer pCthSeq, final Integer pPhiRealizado) {

		final DetachedCriteria subQuery = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		subQuery.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		subQuery.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		subQuery.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), pPhiRealizado));
		subQuery.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.A, DominioSituacaoItenConta.P, DominioSituacaoItenConta.V }));
		subQuery.setProjection(Projections.property(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()));

		final List<Date> listaDatas = executeCriteria(subQuery, 0, 1, null, true);
		final Date dtRealizado = (listaDatas != null && !listaDatas.isEmpty() ? listaDatas.get(0) : null);

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		criteria.add(Restrictions.or(Restrictions.isNotNull(FatItemContaHospitalar.Fields.CIRURGIA.toString()),
				Restrictions.isNotNull(FatItemContaHospitalar.Fields.PROCEDIMENTO_AMB_REALIZADO.toString())));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), dtRealizado));

		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Busca primeiro item de conta de todas as cirurgias com situacao ativo
	 * (A), pontuação (P) ou valor (V)
	 * 
	 * @param pCthSeq
	 * @param p_phi_realizado
	 * @return
	 */
	public FatItemContaHospitalar buscarPrimeiroItemContaCirurgiasAPV(final Integer pCthSeq, final Integer pPhiRealizado) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), pPhiRealizado));
		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.A, DominioSituacaoItenConta.P, DominioSituacaoItenConta.V }));
		criteria.add(Restrictions.or(Restrictions.isNotNull(FatItemContaHospitalar.Fields.CIRURGIA.toString()),
				Restrictions.isNotNull(FatItemContaHospitalar.Fields.PROCEDIMENTO_AMB_REALIZADO.toString())));
		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Busca o anestesista dos itens de conta de todas as cirurgias
	 * 
	 * @param pCthSeq
	 * @param pPhiSeq
	 * @return
	 */
	public RapServidores buscarAnestesistaItensContaCirurgias(final Integer pCthSeq, final Integer pPhiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), pPhiSeq));
		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0).getServidor();
		}

		return null;
	}

	public FatItemContaHospitalar buscarItemContaHospitalarChaveCirurgia(final Integer cthSeq, final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.createAlias(FatItemContaHospitalar.Fields.CIRURGIA.toString(), FatItemContaHospitalar.Fields.CIRURGIA.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.isNotNull(FatItemContaHospitalar.Fields.PPC_CRG_SEQ.toString()));

		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<FatItemContaHospitalar> obterFatItemContaHospitalarPorIndOrigem( final Integer pCthSeq, 
											   final DominioIndOrigemItemContaHospitalar indOrigem,
											   final Date dtRealizado){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioSituacaoItenConta.A));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCthSeq));
		criteria.add(Restrictions.between( FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), 
										   DateUtil.truncaData(dtRealizado), 
										   DateUtil.truncaDataFim(dtRealizado)));

		return executeCriteria(criteria);
	}
	
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ListarItensContaHospitalarRnCthcAtuAgrupichVO> listarItensContaHospitalarRnCthcAtuAgrupich(final Integer cthSeq,
			final Integer phiTomografia, final DominioSituacaoItenConta situacao, final Integer[] phiSeqsTomografia) {
		final StringBuilder hql = new StringBuilder(750);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.ListarItensContaHospitalarRnCthcAtuAgrupichVO(");
		
		hql.append(" coalesce(phi.").append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(" , ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" ), ");
		
		hql.append(" sum(ich."); // quantidadeRealizada
		hql.append(FatItemContaHospitalar.Fields.QUANTIDADE_REALIZADA.toString());
		hql.append("), ");

		hql.append(" min(") // matricula
		   		.append(" case when ")
		   			.append(" coalesce(ich.").append(FatItemContaHospitalar.Fields.SER_VIN_CODIGO_RESP.toString()).append(",0) = 0")
		   				.append(" then (")
		   					.append(" concat(lpad(ich.").append(FatItemContaHospitalar.Fields.SER_VIN_CODIGO_CRIADO.toString()).append(", 3, '0'),")
		   			   		   .append(" lpad(ich.").append(FatItemContaHospitalar.Fields.SER_MATRICULA_CRIADO.toString()).append(", 7, '0')" )
		   							.append(")  ")
		   					.append(") ")
		   		.append(" else (")
		   				.append("  concat(lpad(ich.").append(FatItemContaHospitalar.Fields.SER_VIN_CODIGO_RESP.toString()).append(", 3, '0')," )
		   					.append(" lpad(ich.").append(FatItemContaHospitalar.Fields.SER_MATRICULA_RESP.toString()).append(", 7, '0')" )
		   				.	append(") ")
		   			.append(") ")
		   		.append(" end ")
		.append(" ), ");

//		hql.append(" min(") // solicitacao
//			.append(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString())
//			.append(", 8, '0'),").append(" lpad(ich.");
//		hql.append(FatItemContaHospitalar.Fields.ISE_SEQP.toString());
//		hql.append(", 3, '0'), ");
		
		hql.append(" min(concat(lpad(ich.") // solicitacao
		   .append(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString())
		   .append(", 8, '0'),").append(" lpad(ich.");
		hql.append(FatItemContaHospitalar.Fields.ISE_SEQP.toString());
		hql.append(", 3, '0'))), ");
		
		//Ney em 28/08/2011  Portaria 203 + Novos CBOs
		//Passa a adicionar ao agrupamento o ano mes de dthr_realizado
		//cth.dt_alta_administrativa
		//ich.dthr_realizado
		hql.append(" ich.");	// dthrRealizado
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(", ");
		
		hql.append(" cth.");	// dtAltaAdministrativa
		hql.append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString());
		hql.append(", ");
		
		hql.append(" cth.");	// cspCnvCodigo
		hql.append(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString());
		hql.append(" )");
		
		hql.append(" from ");
		hql.append(FatItemContaHospitalar.class.getSimpleName());
		hql.append(" as ich ");
		hql.append(" join ich.");
		hql.append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" as phi ");
		
		hql.append(" join ich.");
		hql.append(FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString());
		hql.append(" as cth ");
		
		hql.append(" where ");
	
		hql.append(" ich.");
		hql.append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao ");
		hql.append(" and ich.");
		hql.append(FatItemContaHospitalar.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");
		
		hql.append(" and cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");
		
		hql.append(" group by ");
		hql.append(" coalesce(phi.").append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString())
			.append(" , ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" ) ");		
		
		hql.append(',');
		hql.append(" ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(',');
		hql.append(" cth.");
		hql.append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString());
		hql.append(", ");
		hql.append(" cth.");
		hql.append(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString());
				
		hql.append(" order by ");
		
		hql.append(" coalesce(phi.").append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(" , ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" ) ");		
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("situacao", situacao);

		return query.list();
	}

	public DetachedCriteria criteriaPorCthPhiSituacao(final Integer pCth, final Integer pPhi, final DominioSituacaoItenConta situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacao));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCth));

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), pPhi));

		return criteria;
	}

	/**
	 * Lista de FatItemContaHospitalar por cth, phi e situacao
	 * 
	 * @param pCth
	 * @param pPhi
	 * @param situacao
	 * @return
	 */
	public List<FatItemContaHospitalar> listarPorCthPhiSituacao(final Integer pCth, final Integer pPhi, final DominioSituacaoItenConta situacao) {
		final DetachedCriteria criteria = this.criteriaPorCthPhiSituacao(pCth, pPhi, situacao);
		return executeCriteria(criteria);
	}

	/**
	 * Primeiro FatItemContaHospitalar por cth, phi e situacao
	 * 
	 * @param pCth
	 * @param pPhi
	 * @param situacao
	 * @return
	 */
	public FatItemContaHospitalar buscarPrimeiroPorCthPhiSituacao(final Integer pCth, final Integer pPhi, final DominioSituacaoItenConta situacao) {
		final DetachedCriteria criteria = this.criteriaPorCthPhiSituacao(pCth, pPhi, situacao);
		final List<FatItemContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Lista de FatItemContaHospitalar por cth
	 * 
	 * @param pCth
	 * @return
	 */
	public List<FatItemContaHospitalar> listarPorCth(final Integer pCth) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), pCth));
		return executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> listarItensContaHospitalar(final Integer cthSeq, final DominioSituacaoItenConta situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacao));

		return executeCriteria(criteria);
	}

	/**
	 * Lista <code>FatItemContaHospitalar</code> pelo <code>CTH_SEQ</code> e
	 * pelas <code>situacoes</code>
	 * 
	 * @param cthSeq
	 * @param situacoes
	 * @return
	 */
	public List<FatItemContaHospitalarVO> listarPorCthSituacoes(final Integer cthSeq, final DominioSituacaoItenConta... situacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacoes));
		
		criteria.setProjection(Projections.projectionList()
				 					.add(Projections.property(FatItemContaHospitalar.Fields.QUANTIDADE_REALIZADA.toString()),FatItemContaHospitalarVO.Fields.QUANTIDADE_REALIZADA.toString())
				 					.add(Projections.property(FatItemContaHospitalar.Fields.PHI_SEQ.toString()),FatItemContaHospitalarVO.Fields.PHI_SEQ.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(FatItemContaHospitalarVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * 
	 * ORADB: RN_CTHC_VER_DADPARTO.C_EXIGE_REG_CIVIL
	 * 
	 * Lista FatItemContaHospitalar por CTH e PHI
	 * 
	 * @param cthSeq
	 * @param phiSeq
	 * @return
	 */
	public List<FatItemContaHospitalar> listarPorCthPhi(final Integer cthSeq, final Integer phiSeq, final DominioSituacaoItenConta indSituacao) {
		
		final DetachedCriteria criteria = obterDetachedCriteriaFatItemContaHospitalar();
		
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
				+ FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));
		
		// Marina 27/09/2012 - chamado 80628
		criteria.add(Restrictions.ne(FatItemContaHospitalar.Fields.IND_SITUACAO.toString() , indSituacao));

		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorSolicitacao(final Integer soeSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		result.add(Restrictions.eq(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString(), soeSeq));

		return result;
	}

	public List<FatItemContaHospitalar> obterListaPorSolicitacao(final Integer soeSeq) {

		List<FatItemContaHospitalar> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacao(soeSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	public Object[] buscarPrimeiroMinDthrRealizado(final Integer cthSeq, final Date pDataNova) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.lt(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), pDataNova));

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty(FatItemContaHospitalar.Fields.PHI_SEQ.toString()))
				.add(Projections.alias(Projections.min(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()), "dataLimite")));

		criteria.addOrder(Order.asc("dataLimite"));

		final List<Object[]> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		
		return null;
	}

	public FatItemContaHospitalarVO buscarPrimeiroMaxDthrRealizado(final Integer cthSeq, final Date pDataNova) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.sqlRestriction("{alias}.DTHR_REALIZADO > ?", pDataNova, TimestampType.INSTANCE));

		criteria.add(Restrictions.ne(FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioIndOrigemItemContaHospitalar.MPM));
		
		criteria.setProjection(Projections.projectionList()				
				.add(Projections.alias(Projections.groupProperty(FatItemContaHospitalar.Fields.PHI_SEQ.toString()), FatItemContaHospitalarVO.Fields.PHI_SEQ.toString()))
				.add(Projections.alias(Projections.max(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()), FatItemContaHospitalarVO.Fields.DTHR_REALIZADO.toString()))
		);

		criteria.addOrder(Order.desc(FatItemContaHospitalarVO.Fields.DTHR_REALIZADO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(FatItemContaHospitalarVO.class));

		final List<FatItemContaHospitalarVO> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * busca exames com dthr_realizado posterior a dthr_alta_medica
	 * 
	 * @param cthSeq
	 * @param pDataNova
	 * @return
	 */
	public List<FatItemContaHospitalar> listarItemExa(final Integer cthSeq, final Date pDataNova) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.gt(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), pDataNova));

		criteria.add(Restrictions.isNotNull(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString()));
		criteria.add(Restrictions.isNotNull(FatItemContaHospitalar.Fields.ISE_SEQP.toString()));

		criteria.addOrder(Order.desc(FatItemContaHospitalar.Fields.CTH_SEQ.toString())).addOrder(
				Order.desc(FatItemContaHospitalar.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> listarItemPendente(final Integer cthSeq, final Date pDataNova) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.gt(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), pDataNova));

		criteria.add(Restrictions.isNull(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString()));
		criteria.add(Restrictions.isNull(FatItemContaHospitalar.Fields.ISE_SEQP.toString()));

		criteria.addOrder(Order.desc(FatItemContaHospitalar.Fields.CTH_SEQ.toString())).addOrder(
				Order.desc(FatItemContaHospitalar.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<ItemParaTrocaConvenioVO> listarItensParaTrocaConvenio(final Integer internacaoSeq, final Date dtInternacaoAdministrativa,
			final DominioSituacaoItenConta situacaoItemContaHospitalar, final DominioSituacaoConta[] situacoesContaHospitalar) {
		final StringBuilder hql = new StringBuilder(350);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.ItemParaTrocaConvenioVO(ich, cth, atd)");
		hql.append(" from ");
		hql.append(FatContasInternacao.class.getSimpleName());
		hql.append(" as coi ");
		hql.append(" join coi.");
		hql.append(FatContasInternacao.Fields.INTERNACAO.toString());
		hql.append(" as int ");
		hql.append(" join int.");
		hql.append(AinInternacao.Fields.ATENDIMENTO.toString());
		hql.append(" as atd ");
		hql.append(" join coi.");
		hql.append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		hql.append(" as cth ");
		hql.append(" join cth.");
		hql.append(FatContasHospitalares.Fields.ITENS_CONTA_HOSPITALAR.toString());
		hql.append(" as ich ");
		hql.append(" where int.").append(AinInternacao.Fields.SEQ.toString()).append(" = :internacaoSeq");
		hql.append(" and cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()).append(" = :dtInternacaoAdministrativa ");
		hql.append(" and cth.").append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" in (:situacoesContaHospitalar) ");
		hql.append(" and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :situacaoItemContaHospitalar ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("internacaoSeq", internacaoSeq);
		query.setParameter("dtInternacaoAdministrativa", dtInternacaoAdministrativa);
		query.setParameter("situacaoItemContaHospitalar", situacaoItemContaHospitalar);
		query.setParameterList("situacoesContaHospitalar", situacoesContaHospitalar);

		return query.list();
	}

	public FatItemContaHospitalar obterPorCthSeqPhiSeqEIphSeq(final Integer cthSeq, final Integer iphSeq, final Short iphPhoSeq) {

		final StringBuilder sql = new StringBuilder(250);
		
		sql.append(" SELECT ")
		.append(" ICH.").append("SEQ").append(", ICH.").append("CTH_SEQ")
		.append(" FROM AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH ")
		.append(" , AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VFAT ")
		.append(" WHERE ICH.").append("CTH_SEQ").append(" = :P_CTH_SEQ   ")
		.append("   AND VFAT.").append("PHI_SEQ").append(" =  ICH.").append("PHI_SEQ")
		.append("   AND VFAT.").append("IPH_SEQ").append(" =  :P_IPH_SEQ")
		.append("   AND VFAT.").append("IPH_PHO_SEQ").append(" =  :P_IPH_PHO_SEQ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setInteger("P_IPH_SEQ", iphSeq);
		query.setInteger("P_IPH_PHO_SEQ", iphPhoSeq);

		
		final List<Object[]> vos = query.addScalar("SEQ", ShortType.INSTANCE)
											          .addScalar("CTH_SEQ",IntegerType.INSTANCE).list();
		
		if(!vos.isEmpty()) {
			return this.obterPorChavePrimaria(new FatItemContaHospitalarId((Integer)vos.get(0)[1], (Short)vos.get(0)[0]));
		}
		
		return null;
	}
	
	public List<FatItemContaHospitalar> listarPorContaHospitalarItemSolicitacaoExame(final Short iseSeqp, final Integer iseSoeSeq,
			final Integer chtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), chtSeq));

		return executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> listarPorItemSolicitacaoExame(final Short iseSeqp, final Integer iseSoeSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.toString(), iseSoeSeqp));

		return executeCriteria(criteria);
	}

	public Date buscaMinDataHoraRealizado(final Integer cthSeq, final Integer phiSeq, final DominioSituacaoItenConta[] situacoesIgnoradas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());

		criteria.setProjection(Projections.min(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacoesIgnoradas)));

		return (Date) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FatItemContaHospitalar> listarItensContaHospitalarParaDesdobrar(final Integer cthSeq, final Date dataHoraDesdobramento,
			final DominioSituacaoItenConta[] situacoesIgnoradas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacoesIgnoradas)));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.gt(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), dataHoraDesdobramento));

		return executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> listarIchGerada(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));
		criteria.add(Restrictions.ne(FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioIndOrigemItemContaHospitalar.AFA)); // ETB
																																		// 25082003
																																		// não
																																		// levar
																																		// afa
																																		// prox.
																																		// cta
		return executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> listarIchDesdobrada(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.D));

		return executeCriteria(criteria);
	}

	public Short buscaMaxSeqMaisUm(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.setProjection(Projections.max(FatItemContaHospitalar.Fields.SEQ.toString()));

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		Short count = (Short) this.executeCriteriaUniqueResult(criteria);

		if (count == null) {
			count = 1;
		} else {
			count++;
		}

		return count;
	}

	public List<FatItemContaHospitalar> listarItensContaHospitalarPorCthSituacoesIgnoradas(final Integer cthSeq,
			final DominioSituacaoItenConta... situacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		}

		if (situacoes != null) {
			criteria.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacoes)));
		}

		return executeCriteria(criteria);
	}

	public Date buscaMinDtrhRealizadoCadastroSugestaoDesdobramento(final Integer cthSeq, final Integer phiSeqRealizado, final Short cspCnvCodigo,
			final Byte cspSeq, final DominioSituacaoItenConta indSituacao, final DominioSituacao iphIndSituacao, final Boolean iphIndInternacao) {
		final StringBuilder hql = new StringBuilder(250);

		hql.append(" select min(ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()).append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich, ");
		hql.append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" as asp ");
		hql.append(" where ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" and ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" = :phiSeqRealizado ");
		hql.append(" and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacao ");
		hql.append(" and ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" = asp.")
				.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
		hql.append(" and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cspCnvCodigo ");
		hql.append(" and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString()).append(" = :iphIndSituacao ");
		hql.append(" and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString()).append(" = :iphIndInternacao ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("phiSeqRealizado", phiSeqRealizado);
		query.setParameter("indSituacao", indSituacao);
		query.setParameter("cspCnvCodigo", cspCnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("iphIndSituacao", iphIndSituacao);
		query.setParameter("iphIndInternacao", iphIndInternacao);

		return (Date) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listarCursorCirurgiasCadastroSugestaoDesdobramentoVO(final Integer cthSeq,
			final Short cnvCodigo, final Byte cspSeq, final DominioSituacaoItenConta indSituacaoItemConta,
			final ConstanteAghCaractUnidFuncionais[] caracteristicas, final DominioSituacao situacaoItemProcedHospitalar,
			final Boolean indInternacao, final Integer firstResult, final Integer maxResults) {
		final StringBuilder hql = new StringBuilder(480);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorCirurgiasCadastroSugestaoDesdobramentoVO(");
		hql.append("	  ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append("	, unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
		hql.append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" as phi ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString()).append(" as unf ");
		hql.append("	join unf.").append(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()).append(" as cuf ");
		hql.append(" 	join phi.").append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString()).append(" as cgi ");
		hql.append(" 	join cgi.").append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString()).append(" as iph ");
		hql.append(" where ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacaoItemConta ");
		hql.append(" 	and phi.").append(FatProcedHospInternos.Fields.PCI_SEQ.toString()).append(" is not null ");
		hql.append(" 	and cuf.").append(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).append(" in (:caracteristicas) ");
		hql.append(" 	and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" 	and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" 	and iph.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()).append(" = :situacaoItemProcedHospitalar ");
		hql.append(" 	and iph.").append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()).append(" = :indInternacao ");
		hql.append(" order by ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("indSituacaoItemConta", indSituacaoItemConta);
		query.setParameter("situacaoItemProcedHospitalar", situacaoItemProcedHospitalar);
		query.setParameter("indInternacao", indInternacao);
		query.setParameterList("caracteristicas", caracteristicas);

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO> listarItensAtivosCadastroSugestaoDesdobramento(final Integer cthSeq,
			final DominioSituacaoItenConta indSituacaoItemConta) {
		final StringBuilder hql = new StringBuilder(230);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO(");
		hql.append("	  phi.").append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append("	, ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" as phi ");
		hql.append(" where ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacaoItemConta ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("indSituacaoItemConta", indSituacaoItemConta);

		return query.list();
	}

	public Date buscaMenorDthrRealizadoCursorItensUF(final Integer cthSeq, final Short cnvCodigo, final Byte cspSeq,
			final DominioSituacaoItenConta indSituacaoItemConta, final ConstanteAghCaractUnidFuncionais caracteristica,
			final DominioSituacao situacaoItemProcedHospitalar, final Boolean indInternacao) {
		final StringBuilder hql = new StringBuilder(330);

		hql.append(" select min(ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()).append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString()).append(" as unf ");
		hql.append("	join unf.").append(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()).append(" as cuf, ");
		hql.append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" as asp ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" as phi ");
		hql.append(" where ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" 	and cuf.").append(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).append(" = :caracteristica ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacaoItemConta ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString()).append(" = asp.")
				.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString()).append(" = :indInternacao ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString()).append(" = :situacaoItemProcedHospitalar ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("indSituacaoItemConta", indSituacaoItemConta);
		query.setParameter("situacaoItemProcedHospitalar", situacaoItemProcedHospitalar);
		query.setParameter("indInternacao", indInternacao);
		query.setParameter("caracteristica", caracteristica);

		return (Date) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO> listarMaxCirurgiaCadastroSugestaoDesdobramento(final Integer cthSeq,
			final Short cnvCodigo, final Byte cspSeq, final DominioSituacaoItenConta indSituacaoItemConta,
			final DominioSituacao situacaoItemProcedHospitalar, final Boolean indInternacao, final Integer firstResult, final Integer maxResults) {
		final StringBuilder hql = new StringBuilder(450);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO(");
		hql.append("	  ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append("	, unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
		hql.append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich ");
		hql.append(" 	left join ich.").append(FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString()).append(" as unf ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" as phi ");
		hql.append(" 	join phi.").append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString()).append(" as cgi ");
		hql.append(" 	join cgi.").append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString()).append(" as iph ");
		hql.append(" where ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacaoItemConta ");
		hql.append(" 	and phi.").append(FatProcedHospInternos.Fields.PCI_SEQ.toString()).append(" is not null ");
		hql.append(" 	and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" 	and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" 	and iph.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()).append(" = :situacaoItemProcedHospitalar ");
		hql.append(" 	and iph.").append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()).append(" = :indInternacao ");
		hql.append(" order by ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()).append(" desc ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("indSituacaoItemConta", indSituacaoItemConta);
		query.setParameter("situacaoItemProcedHospitalar", situacaoItemProcedHospitalar);
		query.setParameter("indInternacao", indInternacao);

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listarCursorCirurgiasCadastroSugestaoDesdobramentoCirurgica(final Integer cthSeq,
			final Short cnvCodigo, final Byte cspSeq, final DominioSituacaoItenConta indSituacaoItemConta,
			final ConstanteAghCaractUnidFuncionais[] caracteristicas, final DominioSituacao situacaoItemProcedHospitalar,
			final Boolean indInternacao, final Integer firstResult, final Integer maxResults) {
		final StringBuilder hql = new StringBuilder(460);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorCirurgiasCadastroSugestaoDesdobramentoVO(");
		hql.append("	  ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append("	, unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
		hql.append(')');
		hql.append(" from ").append(FatItemContaHospitalar.class.getSimpleName()).append(" as ich ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString()).append(" as unf ");
		hql.append("	join unf.").append(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()).append(" as cuf ");
		hql.append(" 	join ich.").append(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" as phi ");
		hql.append(" , ").append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" as asp ");
		hql.append(" where phi.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" = asp.")
				.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
		hql.append("	and ich.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" 	and ich.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :indSituacaoItemConta ");
		hql.append(" 	and phi.").append(FatProcedHospInternos.Fields.PCI_SEQ.toString()).append(" is not null ");
		hql.append(" 	and cuf.").append(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).append(" in (:caracteristicas) ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString()).append(" = :situacaoItemProcedHospitalar ");
		hql.append(" 	and asp.").append(VFatAssociacaoProcedimento.Fields.IPH_IND_INTERNACAO.toString()).append(" = :indInternacao ");
		hql.append(" order by ich.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("indSituacaoItemConta", indSituacaoItemConta);
		query.setParameter("situacaoItemProcedHospitalar", situacaoItemProcedHospitalar);
		query.setParameter("indInternacao", indInternacao);
		query.setParameterList("caracteristicas", caracteristicas);

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query.list();
	}

	/**
	 * Obter Itens Conta Hospitalar por ItemRmps
	 * 
	 * @param ipsRmpSeq
	 * @param ipsNumero
	 * @return
	 */
	public List<FatItemContaHospitalar> obterItensContaHospitalarPorItemRmps(final Integer ipsRmpSeq, final Short ipsNumero) {
		return executeCriteria(criarCriteriaObterItensContaHospitalarPorItemRmps(ipsRmpSeq, ipsNumero));
	}
	
	private DetachedCriteria criarCriteriaObterItensContaHospitalarPorItemRmps(final Integer ipsRmpSeq, final Short ipsNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		if (ipsRmpSeq != null) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IPS_RMP_SEQ.toString(), ipsRmpSeq));
		}

		if (ipsNumero != null) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IPS_NUMERO.toString(), ipsNumero));
		}
		return criteria;
	}
	
	public FatItemContaHospitalar obterItemContaHospitalarLazyPorId(final FatItemContaHospitalarId id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);

		criteria.createAlias(
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(),
				FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				FatItemContaHospitalar.Fields.ITEM_RMPS.toString(),
				FatItemContaHospitalar.Fields.ITEM_RMPS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				FatItemContaHospitalar.Fields.ITEM_RMPS.toString() + "." + SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(),
				SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				SceItemRmps.Fields.SCE_RMR_PACIENTE.toString() + "." + SceRmrPaciente.Fields.SCO_FORNECEDOR.toString(),
				SceRmrPaciente.Fields.SCO_FORNECEDOR.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				FatItemContaHospitalar.Fields.ITEM_RMPS.toString() + "." + SceItemRmps.Fields.ITENS_CONTAS_HOSPITALAR.toString(),
				SceItemRmps.Fields.ITENS_CONTAS_HOSPITALAR.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		if(id != null) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.SEQ.toString(), id.getSeq()));
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), id.getCthSeq()));
		}
		
		return (FatItemContaHospitalar) executeCriteriaUniqueResult(criteria);
	}
	
	public Map<String, Integer> buscarDadosInicias(final Integer cthSeq) {
		final List<Object> result = executeCriteria(criarCriteriaBuscarDadosInicias(cthSeq));
		return trataResult(result);
	}

	private DetachedCriteria criarCriteriaBuscarDadosInicias(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceRmrPaciente.class, "PAC");
		criteria.createAlias(SceRmrPaciente.Fields.SCE_ITEM_RMPS.toString(), "ITM", Criteria.INNER_JOIN);
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("ITM." + SceItemRmps.Fields.NOTA_FISCAL.toString()), NOTA_FISCAL)
				.add(Projections.property("PAC." + SceRmrPaciente.Fields.SEQ.toString()), RMR_SEQ)
				.add((Projections.property(SceRmrPaciente.Fields.SCO_FORNECEDOR.toString() + "." + ScoFornecedor.Fields.NUMERO.toString())),
						NUMERO_FORNECEDOR));

		final DetachedCriteria subCriteria1 = DetachedCriteria.forClass(SceItemRmps.class, "MAX_ITEM");
		subCriteria1.setProjection(Projections.projectionList().add(Projections.max(SceItemRmps.Fields.NUMERO.toString())));
		subCriteria1.add(Restrictions.eqProperty(SceItemRmps.Fields.RMP_SEQ.toString(), "PAC." + SceRmrPaciente.Fields.SEQ.toString()));

		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(FatContasInternacao.class);
		subCriteria2.setProjection(Projections.projectionList().add(Projections.property(FatContasInternacao.Fields.INT_SEQ.toString())));
		subCriteria2.add(Restrictions.eq(
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.SEQ.toString(), cthSeq));

		criteria.add(Subqueries.propertyEq("ITM." + SceItemRmps.Fields.NUMERO.toString(), subCriteria1));
		criteria.add(Subqueries.propertyEq("PAC." + SceRmrPaciente.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.SEQ.toString(),
				subCriteria2));
		return criteria;
	}

	private Map<String, Integer> trataResult(final List<Object> result) {
		final Map<String, Integer> retorno = new HashMap<String, Integer>();
		if (result != null && !result.isEmpty()) {
			for (final Object lin : result) {
				final Object[] col = (Object[]) lin;
				retorno.put(NOTA_FISCAL, col[0] == null ? null : (Integer) col[0]);
				retorno.put(RMR_SEQ, col[1] == null ? null : (Integer) col[1]);
				retorno.put(NUMERO_FORNECEDOR, col[2] == null ? null : (Integer) col[2]);

			}
		}
		return retorno;
	}
	
	public List<RelatorioIntermediarioLancamentosContaVO> obterItensContaParaRelatorioIntermediarioLancamentos(final Integer cthSeq,
			Map<AghuParametrosEnum, AghParametros> parametros) throws ApplicationBusinessException {
		
		final StringBuilder sql = new StringBuilder(1500);
		
		sql.append(" select ")
	       .append("         view1.cod_tabela as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.COD_SUS.toString())
	       .append("       , itencth.phi_seq as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.COD_PHI.toString())
	       .append(" 	   , phi.descricao as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.DESCRICAO.toString())
	       .append("       , sum (itencth.quantidade_realizada) as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.QUANTIDADE.toString())
	       .append("       , to_char(itencth.dthr_realizado, 'dd/MM/yyyy HH24:mi') as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.DATA_HORA_REALIZADO.toString())
	       .append("       , (CASE WHEN unf.seq IS NOT NULL THEN unf.seq || ' - ' || unf.descricao ELSE NULL END) as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.UNIDADE_REALIZADORA.toString())  
	       .append(" 	   , itencth.ser_matricula_resp as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.MATRICULA_RESPONSAVEL.toString())
	       .append("       , itencth.ser_vin_codigo_resp as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.VIN_COD_RESPONSAVEL.toString())
	       .append(" 	   , itencth.ser_matricula_anest as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.MATRICULA_ANESTESISTA.toString())
	       .append(" 	   , itencth.ser_vin_codigo_anest as ").append(RelatorioIntermediarioLancamentosContaVO.Fields.VIN_COD_ANESTESISTA.toString())

	       .append("  from  " )
	       .append("       agh.fat_itens_conta_hospitalar itencth ")
	       .append("       		inner join agh.fat_proced_hosp_internos phi ")
	       .append("				on phi.seq = itencth.phi_seq ")
	       .append("       		left join agh.agh_unidades_funcionais unf ")
	       .append("       			on unf.seq = itencth.unf_seq ")
	       .append("  			left join agh.v_fat_associacao_procedimentos view1 ") 
	       .append(" 	        	on  view1.phi_seq                = phi.seq ") 
	       .append("  				and view1.cpg_cph_csp_cnv_codigo = :prmCspCnvCodigo ")
	       .append("				and view1.cpg_cph_csp_seq        = :prmCspSeq ")
	       .append("				and view1.cpg_grc_seq            = :prmCpgGrcSeq ")
	       
	       .append(" where 1=1 ") 
	       .append("       and itencth.cth_seq = :prmCTH ")
	       .append("       and itencth.IND_SITUACAO = :prmIndSituacao ")
	       .append("       and itencth.ind_origem = '" + DominioIndOrigemItemContaHospitalar.DIG.toString() +"' ")
	       
	       .append(" group by view1.cod_tabela, ")
	       .append("   itencth.phi_seq, ")
   		   .append("   phi.descricao, ")
	       .append("   " + RelatorioIntermediarioLancamentosContaVO.Fields.QUANTIDADE.toString() + ",")
	       .append("   to_char(itencth.dthr_realizado, 'dd/MM/yyyy HH24:mi'), ")
	       .append("   (CASE WHEN unf.seq IS NOT NULL THEN unf.seq || ' - ' || unf.descricao ELSE NULL END),")
	       .append("   itencth.ser_matricula_resp, ")
	       .append("   itencth.ser_vin_codigo_resp, ")
	       .append("   itencth.ser_matricula_anest, ")
	       .append("   itencth.ser_vin_codigo_anest ")
		
	       .append(" order by " + RelatorioIntermediarioLancamentosContaVO.Fields.DATA_HORA_REALIZADO.toString() + " desc, " +
	    		   RelatorioIntermediarioLancamentosContaVO.Fields.DESCRICAO.toString());
		
		final SQLQuery q = createSQLQuery(sql.toString());

		final AghParametros planoSUS = parametros.get(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		final AghParametros convenioSUS = parametros.get(AghuParametrosEnum.P_CONVENIO_SUS);
		final AghParametros grupoSUS = parametros.get(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
			
		q.setInteger("prmCspCnvCodigo", convenioSUS.getVlrNumerico().intValue() );
		q.setInteger("prmCspSeq", planoSUS.getVlrNumerico().byteValue() );
		q.setInteger("prmCpgGrcSeq", grupoSUS.getVlrNumerico().shortValue() );
		q.setInteger("prmCTH", cthSeq );
		q.setString("prmIndSituacao", DominioSituacaoItenConta.A.toString());
		
		final List<RelatorioIntermediarioLancamentosContaVO> result = q.addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.COD_SUS.toString(), LongType.INSTANCE)
								   									   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.COD_PHI.toString(), IntegerType.INSTANCE)
								   									   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.DESCRICAO.toString(), StringType.INSTANCE)
								   									   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.QUANTIDADE.toString(), IntegerType.INSTANCE)
								   									   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.DATA_HORA_REALIZADO.toString(), StringType.INSTANCE)
								   									   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.UNIDADE_REALIZADORA.toString(), StringType.INSTANCE)
																	   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.MATRICULA_RESPONSAVEL.toString(), IntegerType.INSTANCE)
																	   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.VIN_COD_RESPONSAVEL.toString(), ShortType.INSTANCE)
																	   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.MATRICULA_ANESTESISTA.toString(), IntegerType.INSTANCE)
																	   .addScalar(RelatorioIntermediarioLancamentosContaVO.Fields.VIN_COD_ANESTESISTA.toString(), ShortType.INSTANCE)
																	   
		   .setResultTransformer(Transformers.aliasToBean(RelatorioIntermediarioLancamentosContaVO.class)).list();
		
		return result;
	}

	/**
	 * Método que pesquisa os ítens de conta hospitalar
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatItemContaHospitalar> pesquisarItensContaHospitalarPorCthSituacao(Integer cthSeq,
			DominioSituacaoItenConta situacaoConta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		if (situacaoConta != null) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacaoConta));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Usado SQL Nativo pq o greatest não funciona com HQL
	 * 
	 * @param cthSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codTabela
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CursorBuscaQtdCompVO> buscarQuantidadePorCompetencia(Integer cthSeq, 
			Short iphPhoSeq, Integer iphSeq, Long codTabela) {
		
		final StringBuilder sql = new StringBuilder(550);
		
		sql.append("select greatest(" +
						"to_char(ich.dthr_realizado,'YYYYMM'),'201104',TO_CHAR(ich.dthr_realizado,'YYYYMM')" +
					") anomes " +
					",sum(ich.quantidade_realizada) qtd"); 
		//from
		sql.append(" from agh." + FatItemContaHospitalar.class.getAnnotation(Table.class).name() + " ich");

		//where
		sql.append(" where ich.cth_seq = :cthSeq");
		sql.append(" and ich.phi_seq in (");
		
			sql.append(" select v_ass.phi_seq");
			sql.append(" from  agh."  + VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name() + " v_ass");
			sql.append(" where v_ass.iph_pho_seq = :iphPhoSeq");
			sql.append(" and v_ass.iph_seq = :iphSeq");
			sql.append(" and v_ass.cod_tabela =  :codTabela)");
			
		sql.append(" and ich.ind_situacao <> 'C'");                
		sql.append(" group by ");
		sql.append(" greatest(to_char(ich.dthr_realizado, 'YYYYMM'), '201104', TO_CHAR(ich.dthr_realizado, 'YYYYMM'))");
		sql.append(" ORDER BY 1 DESC");       

		
		// query
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);
		query.setParameter("codTabela", codTabela);

		List<CursorBuscaQtdCompVO> result = query.addScalar("anomes", IntegerType.INSTANCE)
					  .addScalar("qtd", ShortType.INSTANCE)
					  .setResultTransformer(Transformers.aliasToBean(CursorBuscaQtdCompVO.class)).list();

		return result;
		
	}		
	
	/*@SuppressWarnings("unchecked")
	public List<CursorBuscaQtdCompVO> buscarQuantidadePorCompetencia(Integer cthSeq, 
			Short iphPhoSeq, Integer iphSeq, Long codTabela) {
		
		List<CursorBuscaQtdCompVO> result = null;
		StringBuilder hql = null;
		Query query = null;
		
		hql = new StringBuilder();
		hql.append(" select greatest("); //1,2) as anoMes ");
			hql.append("to_char(ich." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString() + ",'YYYYMM')");
			hql.append(",'201104'");
			hql.append(",to_char(ich." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString() + ",'YYYYMM')");
		hql.append(") as c");
		
		hql.append(", sum(ich.");
		hql.append(FatItemContaHospitalar.Fields.QUANTIDADE_REALIZADA.toString());
		hql.append(") as qtd");

		// from
		hql.append(" from ");
		hql.append(FatItemContaHospitalar.class.getName());
		hql.append(" as ich");

		// where
		hql.append(" where ich.");
		hql.append(FatItemContaHospitalar.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq");

		hql.append(" and ich.");
		hql.append(FatItemContaHospitalar.Fields.PHI_SEQ.toString());
		hql.append(" in(");
		
			//sub-query
			hql.append("select vAss.");
			hql.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
			
			//from
			hql.append(" from ");
			hql.append(VFatAssociacaoProcedimento.class.getName());
			hql.append(" as vAss");
			
			//where
			hql.append(" where vAss.");
			hql.append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString());
			hql.append(" = :iphPhoSeq");
			
			hql.append(" and vAss.");
			hql.append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString());
			hql.append(" = :iphSeq");
			
			hql.append(" and vAss.");
			hql.append(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString());
			hql.append(" = :codTabela");
			
		hql.append(')');
		hql.append(" and ich.");
		hql.append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" <> 'C'");
		
		//group by
		hql.append(" group by 1");

		//order by
		hql.append(" order by 1 desc ");
		
		// query
		query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);
		query.setParameter("codTabela", codTabela);

		query.setResultTransformer(Transformers.aliasToBean(CursorBuscaQtdCompVO.class));
		
		result = query.list();

		return result;
		
	}	*/
	
	/**
	 * Usado SQL Nativo pq o greatest não funciona com HQL
	 * 
	 * @param cthSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codTabela
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CursorBuscaQtdCompVO> buscarExcecaoPorAutoRelacionamentoPhi(Integer cthSeq, 
			Short iphPhoSeq, Integer iphSeq) {
		
		StringBuilder sql = new StringBuilder(550);
		
		sql.append("select greatest(" ).append("to_char(ich.dthr_realizado,'YYYYMM'),'201104',TO_CHAR(ich.dthr_realizado,'YYYYMM')" )
		.append(") anomes " ).append(",sum(ich.quantidade_realizada) qtd")
		//from
		.append(" from agh." ).append( FatItemContaHospitalar.class.getAnnotation(Table.class).name() ).append( " ich")
		.append(", agh." ).append( FatProcedHospInternos.class.getAnnotation(Table.class).name() ).append( " phi")
		.append(", agh." ).append( VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name() ).append( " v")

		//where
		.append(" where v.iph_pho_seq = :iphPhoSeq")
		.append(" and v.iph_seq = :iphSeq")
		.append(" and phi.phi_seq = v.phi_seq ")
		.append(" and ich.cth_seq = :cthSeq")
		.append(" and ich.phi_seq = phi.seq")
		.append(" and ich.ind_situacao <> 'C'")
		.append(" and ich.phi_seq != phi.phi_seq")
		
		//group by
		.append(" group by ")
		.append(" greatest(to_char(ich.dthr_realizado, 'YYYYMM'), '201104', TO_CHAR(ich.dthr_realizado, 'YYYYMM'))")
		.append(" ORDER BY 1 DESC");       
		
		// query
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);

		List<CursorBuscaQtdCompVO> result = query.addScalar("anomes", IntegerType.INSTANCE)
					  .addScalar("qtd", ShortType.INSTANCE)
					  .setResultTransformer(Transformers.aliasToBean(CursorBuscaQtdCompVO.class)).list();

		return result;
	}
	
	/**
	 * Usado SQL Nativo pq o greatest não funciona com HQL
	 * 
	 * @param cthSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codTabela
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CursorBuscaQtdCompVO> buscarExcecao(Integer cthSeq, 
			Short iphPhoSeq, Integer iphSeq) {
		
		StringBuilder sql = new StringBuilder(650);
		
		sql.append("select greatest(" +
						"to_char(ich.dthr_realizado,'YYYYMM'),'201104',TO_CHAR(ich.dthr_realizado,'YYYYMM')" +
					") anomes " +
					",sum(ich.quantidade_realizada) qtd"); 
		//from
		sql.append(" from agh." + FatItemContaHospitalar.class.getAnnotation(Table.class).name() + " ich");
		sql.append(", agh." + VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name() + " v");
		sql.append(", agh." + FatContasHospitalares.class.getAnnotation(Table.class).name() + " cth");
		sql.append(", agh." + FatExcCnvGrpItemProc.class.getAnnotation(Table.class).name() + " egi");

		//where
		sql.append(" where egi.iph_pho_seq = :iphPhoSeq");
		sql.append(" and egi.iph_seq = :iphSeq");
		sql.append(" and cth.seq = :cthSeq");
		sql.append(" and v.phi_seq = cth.phi_seq_realizado");
		sql.append(" and EGI.IPH_PHO_SEQ_REALIZADO = v.iph_pho_seq");
		sql.append(" and EGI.IPH_SEQ_REALIZADO = v.iph_seq");
		sql.append(" and ich.cth_seq = cth.seq");
		sql.append(" and ich.phi_seq = egi.phi_seq");
		sql.append(" and ich.ind_situacao <> 'C'");
		
		//group by
		sql.append(" group by ");
		sql.append(" greatest(to_char(ich.dthr_realizado, 'YYYYMM'), '201104', TO_CHAR(ich.dthr_realizado, 'YYYYMM'))");
		sql.append(" ORDER BY 1 DESC");       
		
		// query
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);

		List<CursorBuscaQtdCompVO> result = query.addScalar("anomes", IntegerType.INSTANCE)
					  .addScalar("qtd", ShortType.INSTANCE)
					  .setResultTransformer(Transformers.aliasToBean(CursorBuscaQtdCompVO.class)).list();

		return result;
	}		

	/**
	 * Implementa cursor <code>c_ato</code>
	 * 
	 * @param iphPhoSeqCobrado
	 * @param iphSeqCobrado
	 * @param conta
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<CursorAtoObrigatorioVO> listarAtoObrigatorio(Short iphPhoSeqCobrado, Integer iphSeqCobrado, Integer conta) {

		StringBuilder hql = new StringBuilder(700);
		hql.append(" select ama.");
		hql.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString());
		hql.append(" as eaiCthSeq");

		hql.append(", iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" as codTabela");

		hql.append(", iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(" as descricao");

		hql.append(", iph_ato.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" as codTabelaAto");

		hql.append(", iph_ato.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(" as descricaoAto");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.toString());
		hql.append(" as iphPhoSeq");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ.toString());
		hql.append(" as iphSeq");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.toString());
		hql.append(" as iphPhoSeqCobrado");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.toString());
		hql.append(" as iphSeqCobrado");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.TIV_SEQ.toString());
		hql.append(" as tivSeq");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.TAO_SEQ.toString());
		hql.append(" as taoSeq");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.QUANTIDADE.toString());
		hql.append(" as quantidade");

		hql.append(", aop.");
		hql.append(FatAtoObrigatorioProced.Fields.TIPO_QUANTIDADE.toString());
		hql.append(" as tipoQuantidade");

		hql.append(", ich.");
		hql.append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		hql.append(" as dthrRealizado");

		// from
		hql.append(" from ");
		hql.append(FatItemContaHospitalar.class.getName());
		hql.append(" as ich");
		hql.append(", ").append(VFatAssociacaoProcedimento.class.getName());
		hql.append(" as vap");
		hql.append(", ").append(FatItensProcedHospitalar.class.getName());
		hql.append(" as iph");
		hql.append(", ").append(FatItensProcedHospitalar.class.getName());
		hql.append(" as iph_ato");
		hql.append(", ").append(FatAtoMedicoAih.class.getName());
		hql.append(" as ama");
		hql.append(", ").append(FatAtoObrigatorioProced.class.getName());
		hql.append(" as aop");

		// where
		hql.append(" where aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.toString());
		hql.append(" = :iphPhoSeqCobrado ");
		
		hql.append(" and aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.toString());
		hql.append(" = :iphSeqCobrado ");
		
		hql.append(" and ama.");
		hql.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString());
		hql.append(" = :conta ");
		
		hql.append(" and ama.");
		hql.append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.toString());

		hql.append(" and ama.");
		hql.append(FatAtoMedicoAih.Fields.IPH_SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ.toString());

		hql.append(" and iph_ato.");
		hql.append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.toString());

		hql.append(" and iph_ato.");
		hql.append(FatItensProcedHospitalar.Fields.SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.toString());

		hql.append(" and iph.");
		hql.append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.toString());

		hql.append(" and iph.");
		hql.append(FatItensProcedHospitalar.Fields.SEQ.toString());
		hql.append(" = aop.");
		hql.append(FatAtoObrigatorioProced.Fields.IPH_SEQ.toString());

		hql.append(" and ich.");
		hql.append(FatItemContaHospitalar.Fields.CTH_SEQ.toString());
		hql.append(" = ama.");
		hql.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString());

		hql.append(" and ich.");
		hql.append(FatItemContaHospitalar.Fields.PHI_SEQ.toString());
		hql.append(" = vap.");
		hql.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());

		hql.append(" and vap.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString());
		hql.append(" = ama.");
		hql.append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString());

		hql.append(" and vap.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString());
		hql.append(" = ama.");
		hql.append(FatAtoMedicoAih.Fields.IPH_SEQ.toString());
		
		// query
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("iphPhoSeqCobrado", iphPhoSeqCobrado);
		query.setParameter("iphSeqCobrado", iphSeqCobrado);
		query.setParameter("conta", conta);

		query.setResultTransformer(Transformers.aliasToBean(CursorAtoObrigatorioVO.class));
		List<CursorAtoObrigatorioVO> result = query.list();

		return result;
	}

	/**
	 * Retorna uma lista de FatItemContaHospitalar
	 * por cancelamento de ICH.
	 * 
	 * Obs: incluido criteria de filtro dos campos pnp_seq
	 * e ppr_seq
	 * 
	 * @param atdSeq
	 * @param pnpSeq
	 * @param pprSeq
	 * @param dtHrRealizado
	 * @param tipo
	 * @return
	 */
	public FatItemContaHospitalar obterItemContaHospitalarPorCancelamentoICH(Integer atdSeq, Integer pnpSeq, Long pprSeq, Date dtHrRealizado, String tipo) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatItemContaHospitalar();

		if(tipo.equals("PNP")) {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PNP_ATD_SEQ.toString(), atdSeq));
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PNP_SEQ.toString(), pnpSeq));
		} else {
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PPR_ATD_SEQ.toString(), atdSeq));
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PPR_SEQ.toString(), pprSeq));
		}
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), dtHrRealizado)); 
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.A));

		final List<FatItemContaHospitalar> lista = executeCriteria(criteria, 0, 1, null, true);

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null;
	}
	
	/**
	 * 
	 * ORADB: cursor: FATK_CTH2_RN_UN.RN_CTHC_ATU_ENC_PRV.C_MULT_PROC
	 * DAOTest: FatItemContaHospitalarDAOTest.obterQtdeProcCirurgicosMesmaDataUnidRealiz (Retestar qd alterado) eSchweigert 17/09/2012
	 * 
	 * 	Marina 08/08/2012 - Chamado 76507
	 * 
	 * Quando houver, nos itens da conta, mais de um procedimento cirúrgico com mesma data e hora e mesma unidade realizadora, 
	 * só permitir o encerramento da conta se o realizado da conta possuir uma caracterítica específica
	 */
	public List<Long> obterQtdeProcCirurgicosMesmaDataUnidRealiz( final Integer cthSeq, final DominioSituacaoItenConta situacaoItemConta, 
																	 final DominioSituacao situacaoIPH, final Short cpgCphCspCnvCodigo,
																	 final Byte cpgCphCspSeq, final Short iphPhoSeq, final Short fogSgrGrpSeq, 
																	 final Boolean indInternacao, 
																	 final DominioIndOrigemItemContaHospitalar ...origens){

		final StringBuilder hql = new StringBuilder(600);
		
		hql.append(" select ")
		.append("    count(ICH.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()).append(") ")
		
		.append(" from ")
		.append(     FatItemContaHospitalar.class.getName()).append(" as ICH")
		.append("  ,").append(VFatAssociacaoProcedimento.class.getName()).append(" as VAS ")
		.append("  , ").append(FatItensProcedHospitalar.class.getName()).append(" as IPH ")
		
		.append(" where 1=1 ")
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :conta ")
		
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.PHI_SEQ.toString())
						 .append(" = VAS.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())
						 
		.append("   and VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString())
					     .append(" = IPH.").append(FatItensProcedHospitalar.Fields.SEQ.toString())
					     
		.append("   and VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString())
						 .append(" = IPH.").append(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString())
						 
		.append("   and VAS.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :CPG_CPH_CSP_CNV_CODIGO ")
		.append("   and VAS.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :CPG_CPH_CSP_SEQ ")
		.append("   and VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()).append(" = :IPH_PHO_SEQ ")
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.IND_ORIGEM.toString()).append(" IN (:IND_ORIGEM) ")
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString()).append(" = :IND_SITUACAO ")
		
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.IPS_RMP_SEQ.toString()).append(" IS NULL ")
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.IPS_NUMERO.toString()).append(" IS NULL ")
		.append("   and ICH.").append(FatItemContaHospitalar.Fields.IPS_IRR_EAL_SEQ.toString()).append(" IS NULL ")
		
		.append("   and IPH.").append(FatItensProcedHospitalar.Fields.GRUPO_SEQ.toString()).append(" = :FOG_SGR_GRP_SEQ ")
		.append("	and IPH.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()).append(" = :IND_SITUACAO_IPH ")
		.append("	and IPH.").append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()).append(" = :IND_INTERNACAO ")
		
		.append(" GROUP BY ICH.").append(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString());
		
		final Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("conta", cthSeq);

		query.setParameter("IND_INTERNACAO", indInternacao);
		query.setParameter("CPG_CPH_CSP_CNV_CODIGO", cpgCphCspCnvCodigo);
		query.setParameter("CPG_CPH_CSP_SEQ", cpgCphCspSeq);
		query.setParameter("IPH_PHO_SEQ", iphPhoSeq);
		query.setParameterList("IND_ORIGEM", origens);
		query.setParameter("IND_SITUACAO", situacaoItemConta);
		query.setParameter("FOG_SGR_GRP_SEQ", fogSgrGrpSeq);
		query.setParameter("IND_SITUACAO_IPH", situacaoIPH);
		
		return query.list();
	}
	
	/**
	 * ORADB: cursor: FATK_CTH2_RN_UN.RN_CTHC_ATU_ENC_PRV.C_ICH_PHI_APAC
	 * DAOTest: FatItemContaHospitalarDAO.obterMenorDataValidacaoApac (Retestar qd alterado) eSchweigert 17/09/2012
	 * 
	 * 	Ney 20/10/2011 Buscar menor data do item para validar apac
	 * @param dthrRealizado 
	 */
	public List<MenorDataValidacaoApacVO> obterMenorDataValidacaoApac( final Integer cthSeq, final DominioSituacaoItenConta situacao, 
												   final Integer phi,    final Date dthrRealizado){

		final StringBuilder sql = new StringBuilder(1000);

		final String restriction;
		final String column;
		
		if(isOracle()){
			column =" TRUNC(ICH." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.name() + ") AS " + MenorDataValidacaoApacVO.Fields.DTHR_REALIZADO.toString();
			restriction = "   AND ICH." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.name() + " >= TRUNC(:dthr_realizado,'month') ";
	    	
	    } else {
	    	column = " DATE_TRUNC('day', ICH." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.name() +") AS " + MenorDataValidacaoApacVO.Fields.DTHR_REALIZADO.toString();
	    	restriction = "   AND EXTRACT(MONTH FROM ICH." + FatItemContaHospitalar.Fields.DTHR_REALIZADO.name() + ") >= :dthr_realizado_mes ";
	    }

		sql.append(" SELECT ")
		   .append(column)
		
		   .append(" FROM ")
		   .append("     AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH ")
			
		   .append(" WHERE 1=1 ")
		   .append(  	restriction )
		   .append("   AND ICH." + FatItemContaHospitalar.Fields.CTH_SEQ.name() + " = :conta ")
		   .append("   AND ICH." + FatItemContaHospitalar.Fields.PHI_SEQ.name() + " = :phiSeq ")
		   .append("   AND ICH." + FatItemContaHospitalar.Fields.IND_SITUACAO.name() + " = :situacao ")		
		
		   .append(" UNION ")
		   .append(" SELECT ")
		   .append(column)
		
		   .append(" FROM ")
		   .append("     AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH, ")
		   .append("     AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI ")
		
		   .append(" WHERE 1=1 ")
		   .append("   AND " + "ICH." + FatItemContaHospitalar.Fields.PHI_SEQ.name() + " = " + "PHI."+ FatProcedHospInternosPai.Fields.SEQ.name())
		   .append(    restriction )
		   .append("   AND PHI." + FatProcedHospInternosPai.Fields.PHI_SEQ.name() + " = :phiSeq ")
		   .append("   AND ICH." + FatItemContaHospitalar.Fields.CTH_SEQ.name() + " = :conta ")
		   .append("   AND ICH." + FatItemContaHospitalar.Fields.IND_SITUACAO.name() + " = :situacao ")
		   
		   .append(" ORDER BY 1");
					 
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setInteger("conta", cthSeq);
		query.setString("situacao", situacao.toString());
		query.setInteger("phiSeq", phi);

		if(isOracle()){
			query.setTimestamp("dthr_realizado", dthrRealizado);
		} else {
			query.setParameter("dthr_realizado_mes", Integer.parseInt(DateUtil.obterDataFormatada(dthrRealizado, "MM")));
		}
		
		query.addScalar(MenorDataValidacaoApacVO.Fields.DTHR_REALIZADO.toString(), TimestampType.INSTANCE)
		.setResultTransformer(Transformers.aliasToBean(MenorDataValidacaoApacVO.class));
		
		return query.list();
	}
	
	/**
	 * ORADB: FATK_CTH2_RN_UN.C_EXAME_ANATO
	 */
	public List<FatItemContaHospitalar> listarExamesAnatos(final Integer chtSeq, final DominioSituacaoItenConta[] arraySituacao, final String[] sitCodigos, Integer ...phis) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		
		criteria.createAlias(FatItemContaHospitalar.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");

		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), chtSeq));
		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phis));
		criteria.add(Restrictions.not(Restrictions.in("ISE."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), sitCodigos)));
		// -- MARINA 29/04/2013
		criteria.add(Restrictions.not(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), arraySituacao)));
		
		return executeCriteria(criteria);
	}
	
	public List<CursorAtoFatcBuscaServClassVO> obterCursorAtoFatcBuscaServClass(final Short iphPhoSeqCobrado, final Integer iphSeqCobrado, final Integer cthSeq, final Short cpgCphCspCnvCodigo, final Byte ...cpgCphCspSeq){
		final StringBuilder sql = new StringBuilder(700);
		
		sql.append(" SELECT  ")
		   .append("  AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.EAI_CTH_SEQ.toString())
			 
	       .append(" ,IPH.").append(FatItensProcedHospitalar.Fields.COD_TABELA.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.COD_TABELA.toString())
			 
	       .append(" ,IPH.").append(FatItensProcedHospitalar.Fields.DESCRICAO.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.DESCRICAO_IPH.toString())
			 
	       .append(" ,IPH_ATO.").append(FatItensProcedHospitalar.Fields.COD_TABELA.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.COD_TABELA_ATO.toString())
			 
	       .append(" ,IPH_ATO.").append(FatItensProcedHospitalar.Fields.DESCRICAO.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.DESCRICAO_ATO.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IPH_PHO_SEQ.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IPH_SEQ.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IPH_PHO_SEQ_COBRADO.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IPH_SEQ_COBRADO.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.TIV_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.TIV_SEQ.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.TAO_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.TAO_SEQ.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.QUANTIDADE.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.QUANTIDADE.toString())
			 
	       .append(" ,AOP.").append(FatAtoObrigatorioProced.Fields.TIPO_QUANTIDADE.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.TIPO_QUANTIDADE.toString())
			 
	       .append(" ,ICH.").append(FatItemContaHospitalar.Fields.IND_ORIGEM.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IND_ORIGEM.toString())
			 
	       .append(" ,ISE.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.UFE_UNF_SEQ.toString())
			 
	       .append(" ,ICH.").append(FatItemContaHospitalar.Fields.IPS_RMP_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.IPS_RMP_SEQ.toString())
			 
	       .append(" ,ICH.").append(FatItemContaHospitalar.Fields.UNF_SEQ.name())
			 			    .append(" AS ").append(CursorAtoFatcBuscaServClassVO.Fields.UNF_SEQ.toString())
			 
	        
	       .append(" FROM   ")
		   .append("  AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH ")
		   .append("   LEFT JOIN AGH.").append(AelItemSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" ISE ON ")
		   							   .append("     ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.name())
		   							   .append(" AND ISE.").append(AelItemSolicitacaoExames.Fields.SEQP.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.ISE_SEQP.name())
		   								   
		   .append(" , AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAP ")
	       .append(" , AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH ")
	       .append(" , AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH_ATO ")
	       .append(" , AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AMA ")
	       .append(" , AGH.").append(FatAtoObrigatorioProced.class.getAnnotation(Table.class).name()).append(" AOP ")
	       
	       .append(" WHERE 1=1 ")
	       .append("  AND AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.name()).append(" = :P_IPH_PHO_SEQ_COBRADO ")
	       .append("  AND AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.name()).append(" = :P_IPH_SEQ_COBRADO ")
	       .append("  AND AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :P_CTH_SEQ ")
	       .append("  AND AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.name())
	       .append("  AND AMA.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ.name())
	       .append("  AND IPH_ATO.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.name())
	       .append("  AND IPH_ATO.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.name())
	       .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.name())
	       .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = AOP.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ.name())
	       .append("  AND ICH.").append(FatItemContaHospitalar.Fields.CTH_SEQ.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
	       .append("  AND VAP.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.PHI_SEQ.name())
	       .append("  AND VAP.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name())
	       .append("  AND VAP.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name())
	       
	       .append("  AND VAP.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :P_CPG_CPH_CSP_CNV_CODIGO ")
	       .append("  AND VAP.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" IN (:P_CPG_CPH_CSP_SEQ) ");
		

		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setInteger("P_IPH_SEQ_COBRADO", iphSeqCobrado);
		query.setShort("P_IPH_PHO_SEQ_COBRADO", iphPhoSeqCobrado);
		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setShort("P_CPG_CPH_CSP_CNV_CODIGO", cpgCphCspCnvCodigo);
		query.setParameterList("P_CPG_CPH_CSP_SEQ", cpgCphCspSeq);

		query.addScalar(CursorAtoFatcBuscaServClassVO.Fields.EAI_CTH_SEQ.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.COD_TABELA.toString(), LongType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.DESCRICAO_IPH.toString(), StringType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.COD_TABELA_ATO.toString(), LongType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.DESCRICAO_ATO.toString(), StringType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IPH_PHO_SEQ_COBRADO.toString(), ShortType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IPH_SEQ_COBRADO.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.TIV_SEQ.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.TAO_SEQ.toString(), ByteType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.QUANTIDADE.toString(), ShortType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.TIPO_QUANTIDADE.toString(), StringType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IND_ORIGEM.toString(), StringType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.UFE_UNF_SEQ.toString(), ShortType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.UNF_SEQ.toString(), ShortType.INSTANCE)
		     .addScalar(CursorAtoFatcBuscaServClassVO.Fields.IPS_RMP_SEQ.toString(), IntegerType.INSTANCE)
		     .setResultTransformer(Transformers.aliasToBean(CursorAtoFatcBuscaServClassVO.class));

		return query.list();
	}
	
	public List<FatItemContaHospitalar> listarContaHospitalarPorCirurgia(Integer ppcCrgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(
				FatItemContaHospitalar.Fields.PPC_CRG_SEQ.toString(), ppcCrgSeq));
		
		return this.executeCriteria(criteria);
	}
		
	public List<FatItemContaHospitalar> listarContaHospitalarPorSceRmrPacientes(Integer ppcCrgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(SceRmrPaciente.class);
		subCriteria.setProjection(
				Projections.projectionList().add(
						Property.forName(SceRmrPaciente.Fields.SEQ.toString())));
		subCriteria.add(Restrictions.eq(SceRmrPaciente.Fields.CRG_SEQ.toString(), ppcCrgSeq));
		criteria.add(Subqueries.propertyIn(
				FatItemContaHospitalar.Fields.IPS_RMP_SEQ.toString(), subCriteria));
				
		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * @param procEspPorCirurgias
	 * @return
	 */
	public List<FatItemContaHospitalar> listarContaHospitalarPorProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgias) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		
		criteria.createAlias(FatItemContaHospitalar.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "ppc", DetachedCriteria.INNER_JOIN);
		
		MbcProcEspPorCirurgiasId id = procEspPorCirurgias.getId();
		
		criteria.add(Restrictions.eq("ppc.".concat(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()), procEspPorCirurgias.getCirurgia().getSeq()));
		criteria.add(Restrictions.eq("ppc.".concat(MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString()), id.getEprEspSeq()));
		criteria.add(Restrictions.eq("ppc.".concat(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()), id.getEprPciSeq()));
		criteria.add(Restrictions.eq("ppc.".concat(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), id.getIndRespProc()));
		
		return this.executeCriteria(criteria);
	}

	public List<FatItemContaHospitalar> buscarPorCirurgiaESituacao(Integer crgSeq, DominioSituacaoItenConta[] situacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "ICH");
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PPC_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), situacoes));
		return executeCriteria(criteria);
	}

	public Long obterCountItensPorContaItemRmpsEQuantidade(final Integer cthSeq, final Integer ipsRmpSeq, final Short ipsNumero, final Short qtde) {
		final DetachedCriteria criteria =criarCriteriaObterItensContaHospitalarPorItemRmps(ipsRmpSeq, ipsNumero);
		
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.QUANTIDADE.toString(), qtde));
		
		return executeCriteriaCount(criteria);
	}
	
	public Integer removerPorPHI(final Integer phiSeq) {
		Query query = this.createHibernateQuery("delete " + FatItemContaHospitalar.class.getName() + 
				 " where " + FatItemContaHospitalar.Fields.PHI_SEQ.toString() + " = :phiSeq ");
		query.setParameter("phiSeq", phiSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Utilizado somente no Encerramento de Competência
	 * @param cthSeq
	 * @param newSituacao
	 * @param inSituacoes
	 * @return
	 */
	public Integer alterarSituacaoEmLoteSemValidacoes(Integer cthSeq,
			DominioSituacaoItenConta newSituacao,
			List<DominioSituacaoItenConta> inSituacoes, String alteradoPor,
			Integer matricula, Short vinculo) {
		javax.persistence.Query query = this.createQuery("update " + FatItemContaHospitalar.class.getName() + " set " +
				FatItemContaHospitalar.Fields.IND_SITUACAO.toString() + " = :newSituacao" + 
				" , " + FatItemContaHospitalar.Fields.ALTERADO_POR.toString() + " = :alteradoPor" +
				" , " + FatItemContaHospitalar.Fields.SER_MATRICULA_ALTERADO.toString() + " = :matricula" +
				" , " + FatItemContaHospitalar.Fields.SER_VIN_CODIGO_ALTERADO.toString() + " = :vinculo" +
				" , " + FatItemContaHospitalar.Fields.ALTERADO_EM.toString() + " = :alteradoEm" +
				" where " + FatItemContaHospitalar.Fields.CTH_SEQ.toString() + " = :cthSeq" +
				" and " + FatItemContaHospitalar.Fields.IND_SITUACAO.toString() + " in (:inSituacoes) ");
		query.setParameter("newSituacao", newSituacao);
		query.setParameter("alteradoPor", alteradoPor);
		query.setParameter("matricula", matricula);
		query.setParameter("vinculo", vinculo);
		query.setParameter("alteradoEm", new Date());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("inSituacoes", inSituacoes);
		return query.executeUpdate();
	}	
	
	//#1295 C1
	public List<Integer> buscarContasProcedProfissionalVinculoIncorreto(Short[] vinculos, DominioSituacaoConta[] situacoesConta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "ICH");
		criteria.createAlias("ICH." + FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		
		criteria.add(Restrictions.in("ICH." + FatItemContaHospitalar.Fields.SER_VIN_CODIGO_RESP.toString(), vinculos));
		criteria.add(Restrictions.in("CTH." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoesConta));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("ICH." + FatItemContaHospitalar.Fields.CTH_SEQ.toString())), "id.cthSeq"));
		return executeCriteria(criteria);
	}
	
	public List<SubRelatorioMudancaProcedimentosVO> buscarMudancaProcedimentos(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		StringBuilder hql = new StringBuilder(1300);
		hql.append("SELECT DISTINCT FE.DATA_SAIDA DT_SAIDA, "
				).append( " CASE WHEN FE.IPH_COD_SUS_REALIZ = FE.IPH_COD_SUS_SOLIC "
				).append( " THEN (NULL) "
				).append( " ELSE (FE.IPH_COD_SUS_SOLIC) END MUD_PROC_SOLIC, "
				
				).append( " CASE WHEN FE.IPH_COD_SUS_REALIZ = FE.IPH_COD_SUS_SOLIC "
				).append( " THEN (NULL) "
				).append( " ELSE (FE.IPH_COD_SUS_REALIZ) END MUD_PROC_REALIZ, "
				
				).append( " CASE WHEN FE.IPH_COD_SUS_REALIZ = FE.IPH_COD_SUS_SOLIC "
				).append( " THEN (NULL) "
				).append( " ELSE (IPHS.DESCRICAO) END MUD_DSCR_SOLIC, "
				
				).append( " CASE WHEN FE.IPH_COD_SUS_REALIZ = FE.IPH_COD_SUS_SOLIC "
				).append( " THEN (NULL) "
				).append( " ELSE (IPHR.DESCRICAO) END MUD_DSCR_REALIZ "
				).append( " from AGH_ATENDIMENTOS ATD, "
				).append( " AIP_PACIENTES PAC, "
				).append( " MPM_ALTA_SUMARIOS ASU, "
				).append( " FAT_ITENS_PROCED_HOSPITALAR IPHR, "
				).append( " FAT_ITENS_PROCED_HOSPITALAR IPHS, "
				).append( " FAT_ESPELHOS_AIH FE, "
				).append( " FAT_CONTAS_HOSPITALARES CTH, "
				).append( " fat_contas_internacao coi, "
				).append( " ain_internacoes ain "
				).append( " where coi.int_seq = ain.seq "
				).append( " and coi.int_seq = atd.int_seq "
				).append( " and cth.seq = coi.cth_seq "
				).append( " and ATD.PAC_CODIGO = PAC.CODIGO "
				).append( " and PAC.PRONTUARIO = FE.PAC_PRONTUARIO " 
				).append( " and ASU.APA_ATD_SEQ = ATD.SEQ "
				).append( " and ASU.PRONTUARIO = FE.PAC_PRONTUARIO " 
				).append( " and FE.CTH_SEQ = CTH.SEQ "
				).append( " and FE.SEQP = 1 "
				).append( " AND IPHS.PHO_SEQ = FE.IPH_PHO_SEQ_SOLIC "
				).append( " AND IPHS.SEQ = FE.IPH_SEQ_SOLIC "
				).append( " AND IPHR.PHO_SEQ = FE.IPH_PHO_SEQ_REALIZ "
				).append( " AND IPHR.SEQ = FE.IPH_SEQ_REALIZ "
				).append( " AND ASU.APA_ATD_SEQ = :seqAtendimento "
				).append( " AND CTH.IND_SITUACAO IN ('A','F','E') ");
		if (apaSeq != null) {
			hql.append(" AND ASU.APA_SEQ = :apaSeq ");
		}
		
		if (seqp != null) {
			hql.append(" AND ASU.SEQP = :seqp ");
		}
		
		javax.persistence.Query query = this.createNativeQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		if (apaSeq != null) {
			query.setParameter("apaSeq", apaSeq);
		}
		
		if (seqp != null) {
			query.setParameter("seqp", seqp);
		}
		
		List<Object[]> results = query.getResultList();
		
		List<SubRelatorioMudancaProcedimentosVO> retorno = new ArrayList<SubRelatorioMudancaProcedimentosVO>();
		for (Object[] objectResult : results) {
			SubRelatorioMudancaProcedimentosVO vo = new SubRelatorioMudancaProcedimentosVO();
			
			if (objectResult[0] != null) {
				vo.setDataSaidaFormatada(DateUtil.obterDataFormatada((Date) objectResult[0], "dd/MM/yyyy"));
			}
			
			if (objectResult[1] != null) {
				vo.setMudProcSolic(((Number) objectResult[1]).intValue());
			}
			
			if (objectResult[2] != null) {
				vo.setMudProcRealiz(((Number) objectResult[2]).intValue());
			}
			
			if (objectResult[3] != null) {
				vo.setMudDescrSolic((String) objectResult[3]);
			}
			
			if (objectResult[4] != null) {
				vo.setMudDescrRealiz((String) objectResult[4]);
			}
			retorno.add(vo);
		}
		return retorno;
	}
	
	public List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUSProcedFisioterapia(Integer seqAtendimento,
			Integer tctSeq, Integer tptSeq, Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq,
			Short cpgGrcSeq, Short iphPhoSeq, Integer codNutricaoEnteral) {
		
		StringBuilder hql = new StringBuilder(2000);
		hql.append("select distinct 8, "
				+ "vas.cod_tabela, "
				+ "vas.iph_descricao, "
				+ "cip.valor_char ");
		hql.append(" from agh.FAT_ITENS_CONTA_HOSPITALAR ich "
				+ " inner join agh.AGH_ATENDIMENTOS atd on atd.seq = ich.pif_atd_seq "
				+ " inner join agh.MPT_PROCEDIMENTOS_INTERNACAO mpi on mpi.atd_seq = atd.seq "
				+ " inner join agh.FAT_PROCED_TRATAMENTOS fpt on fpt.phi_seq = mpi.phi_seq "
				+ " inner join agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS vas on vas.phi_seq = fpt.phi_seq "
				+ " inner join agh.FAT_CARACT_ITEM_PROC_HOSP cip on cip.iph_pho_seq = vas.iph_pho_seq and cip.iph_seq = vas.iph_seq "
				+ " WHERE atd.seq = :seqAtendimento "
				+ " and cip.tct_seq = :tctSeq "
				+ " and ich.pif_atd_seq is not null "
				+ " and fpt.tpt_seq = :tptSeq "
				+ " and vas.cpg_cph_csp_cnv_codigo = :cpgCphCspCnvCodigo "
				+ " and vas.cpg_cph_csp_seq = :cpgCphCspSeq "
				+ " and vas.cpg_grc_seq = :cpgGrcSeq "
				+ " and vas.iph_pho_seq = :iphPhoSeq ");
		//#51731
		hql.append("UNION ");
		hql.append("select distinct 9, "
				+ "vas.cod_tabela, "
				+ "vas.iph_descricao, "
				+ "cip.valor_char ");
		hql.append(" from  agh.AGH_ATENDIMENTOS atd "
				+ " inner join agh.FAT_CONTAS_INTERNACAO  coi "
				+ " on   atd.int_seq = coi.int_seq "
				+ " inner join agh.FAT_ITENS_CONTA_HOSPITALAR ich "
				+ " on  coi.cth_seq = ich.cth_seq "
				+ " inner join agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS vas "
				+ " on ich.phi_seq = vas.phi_seq "
				+ " inner join agh.FAT_CARACT_ITEM_PROC_HOSP cip "
				+ " on cip.IPH_PHO_SEQ = vas.IPH_PHO_SEQ "
				+ " and cip.IPH_SEQ = vas.IPH_SEQ "
				+ " where atd.seq = :seqAtendimento "
				+ " and VAS.CPG_CPH_CSP_CNV_CODIGO = :cpgCphCspCnvCodigo "
				+ " and VAS.CPG_CPH_CSP_SEQ = :cpgCphCspSeq "
				+ " and VAS.CPG_GRC_SEQ = :cpgGrcSeq "
				+ " and VAS.IPH_PHO_SEQ = :iphPhoSeq "
				+ " and  cip.tct_seq = :codNutricaoEnteral ");
		
		javax.persistence.Query query = this.createNativeQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("tctSeq", tctSeq);
		query.setParameter("tptSeq", tptSeq);
		query.setParameter("cpgCphCspCnvCodigo", cpgCphCspCnvCodigo);
		query.setParameter("cpgCphCspSeq", cpgCphCspSeq);
		query.setParameter("cpgGrcSeq", cpgGrcSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		//#51731
		query.setParameter("codNutricaoEnteral", codNutricaoEnteral);
		
		List<Object[]> results = query.getResultList();
		
		List<SubRelatorioLaudosProcSusVO> retorno = new ArrayList<SubRelatorioLaudosProcSusVO>();
		for (Object[] objectResult : results) {
			SubRelatorioLaudosProcSusVO vo = new SubRelatorioLaudosProcSusVO();
			
			vo.setOrdem(((Number) objectResult[0]).intValue());
			vo.setProcedimentoCodigo(((Number) objectResult[1]).longValue());
			vo.setProcedimentoDescricao((String) objectResult[2]);
			
			if (objectResult[3] != null) {
				vo.setJustificativa((String) objectResult[3]);
			}
			retorno.add(vo);
		}
		return retorno;
	}
	
	public List<RateioValoresSadtPorPontosVO> obterRateioValoresSadtPorPontos(Date dtHrInicio, Integer ano, Integer mes) throws ApplicationBusinessException {
		
		final Short[] inIphPhoSeq = {1, 2, 3, 4, 5, 6};
		
		final Short cpgCphCspCnvCodigo = 1;
		final Byte cpgCphCspSeq = 1;
		
		final DetachedCriteria criteria = obterDetachedCriteriaFatItemContaHospitalar();

		criteria.createAlias(FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI");
		criteria.createAlias(FatItemContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		criteria.createAlias("PHI." + FatProcedHospInternosPai.Fields.CONV_GRUPO_ITENS_PROCED.toString(), "CGI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH");
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "DCI");
		
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), mes));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), ano));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), dtHrInicio));

		criteria.add(Restrictions.eqProperty("CTH." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "DCI." + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString()));
		criteria.add(Restrictions.isNull("CTH." + FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString()));
		
		criteria.add(Restrictions.eqProperty(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), "CTH." + FatContasHospitalares.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacaoItenConta.P));
		
		criteria.add(Restrictions.eqProperty("CGI." + FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), FatItemContaHospitalar.Fields.PHI_SEQ.toString()));
		criteria.add(Restrictions.in("CGI." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), inIphPhoSeq));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		
		criteria.add(Restrictions.eqProperty("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(), "CGI." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), "CGI." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()));
		
		criteria.add(Restrictions.eqProperty("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), FatItemContaHospitalar.Fields.UNF_SEQ.toString()));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))
				.add(Projections.groupProperty("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
				.add(Projections.sum("IPH." + FatItensProcedHospitalar.Fields.PONTOS_SADT.toString()), FatItensProcedHospitalar.Fields.PONTOS_SADT.toString()));

		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(RateioValoresSadtPorPontosVO.class.getConstructor(Short.class, String.class, Long.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return executeCriteria(criteria);
	}
	
	public List<FatItemContaHospitalar> listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(final Integer cthSeq, DominioIndOrigemItemContaHospitalar origem) {

		final DetachedCriteria criteria = obterDetachedCriteriaFiltrandoPorOrigemEPorContaHospitalarSeq(cthSeq, origem);

		return executeCriteria(criteria);
	}
	
	public Short buscarMax(Integer seq){
			Short proxSequencia = null;
			DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), seq));

			ProjectionList projection = Projections.projectionList();
			projection.add(Projections.sqlProjection("COALESCE(MAX(SEQ),0) + 1 as proximo", new String[]{"proximo"}, new Type[] { ShortType.INSTANCE }), "proximo");		
			criteria.setProjection(projection);
			proxSequencia = (Short) executeCriteriaUniqueResult(criteria);
			return proxSequencia;
	}
	
	//50635 - C4
	public List<FatItemContaHospitalar> obterContaHospitalarPorCthSeqEData(Integer cthSeq, Date dthrRealizado){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		
		if(cthSeq != null){
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		}
		if(dthrRealizado != null){
			criteria.add(Restrictions.gt(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), dthrRealizado));
		}
			return  executeCriteria(criteria);
	}
	
	//50635 - C5
	public Boolean isContaNovaItensExistente(Integer vContaNova, FatItemContaHospitalar fatItemContaHospitalar){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		if(vContaNova != null){
			criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), vContaNova));
		}
		if(fatItemContaHospitalar != null){
			if(fatItemContaHospitalar.getProcedimentoHospitalarInterno().getSeq() != null){
				criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), fatItemContaHospitalar.getProcedimentoHospitalarInterno().getSeq()));
			}
			if(fatItemContaHospitalar.getDthrRealizado() != null){
				criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString(), fatItemContaHospitalar.getDthrRealizado()));
			}
			if(fatItemContaHospitalar.getIndSituacao() != null){
				criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), fatItemContaHospitalar.getIndSituacao()));
			}
		}
		
		return executeCriteriaExists(criteria);
		
	}
}
