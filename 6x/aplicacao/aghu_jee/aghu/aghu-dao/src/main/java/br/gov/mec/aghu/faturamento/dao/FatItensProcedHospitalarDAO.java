package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.ByteType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.vo.CursoPopulaProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.faturamento.vo.BuscarProcedHospEquivalentePhiVO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoMedicoAihVO;
import br.gov.mec.aghu.faturamento.vo.FatGrupoSubGrupoVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedServVO;
import br.gov.mec.aghu.faturamento.vo.GerarArquivoProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ItemProcedimentoHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihServicosVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvProcedHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatProcedServicos;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.model.FatServicos;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;

@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.InsufficientStringBufferDeclaration", "PMD.AvoidDuplicateLiterals", "PMD.ConsecutiveLiteralAppends", "PMD.ConsecutiveAppendsShouldReuse"})
public class FatItensProcedHospitalarDAO extends BaseDao<FatItensProcedHospitalar> {
    
	private static final long serialVersionUID = -3278002556073442891L;
	private final String PONTO =  ".";
	private final String ALIAS_IPH = "iph";	// FAT_ITENS_PROCED_HOSPITALAR iph
	private final String ALIAS_CGI = "cgi";	// FAT_CONV_GRUPO_ITENS_PROCED cgi
	private final String ALIAS_PHI = "phi";	// FAT_PROCED_HOSP_INTERNOS	   phi
	private final String ALIAS_IPC = "icp";	// FAT_VLR_ITEM_PROCED_HOSP_COMPS ipc
	private final String ALIAS_PRH = "prh"; // Procedimento hospitalar
	private final String ALIAS_IPT = "ipt";	// Item procedimento hospitalar transplante
	private final String ALIAS_TTR = "ttr"; // Tipo Transplante
	
	private final String ALIAS_PCI = "pci"; // MBC_PROCEDIMENTO_CIRURGICOS	PCI	-> MbcProcedimentoCirurgicos
	private final String ALIAS_ESP = "esp"; // AGH_ESPECIALIDADES			ESP	-> AghEspecialidades
	private final String ALIAS_SELECT = " SELECT ";

	
	private final Short  CPG_CPH_CSP_CNV_CODIGO = 0;
	private final Byte   CPG_CPH_CSP_SEQ = 0;
	private final Short  CPG_CPH_PHO_SEQ = 0; 
	private final Short  CPG_GRC_SEQ = 0;

	@Override
	protected void obterValorSequencialId(final FatItensProcedHospitalar elemento) {
		final int value = this.getNextVal(SequenceID.FAT_IPH_SQ1).intValue();
		elemento.getId().setSeq(value);
	}

	/**
	 * Migração do cursor <code>c_busca_qtde_lanc</code>
	 * 
	 * @param phoSeq
	 * @param cthSeq
	 * @param indSituacao
	 * @param codTabela
	 * @param phiSeq
	 * @param competencia
	 *            -- Ney 15/07/2011 Portaria 203 Fase 2
	 * @return Object[] onde </br> Object[0] = max_qtd_conta<code>(Short)</code>
	 *         </br> Object[1] = quantidade_realizada<code>(Short)</code>
	 */
	public Object[] buscaQtdeLanc(final Short phoSeq, final Integer cthSeq,
			final DominioSituacaoItenConta[] arrayIndSituacao, final Long codTabela,
			final Integer phiSeq, final Date competencia) {
		final StringBuilder hql = new StringBuilder(326);

		// Ney 15/07/2011 Portaria 203 Fase 2
		// hql.append(" COALESCE(iph.").append(FatItensProcedHospitalar.Fields.MAX_QTD_CONTA.toString()).append(", 0)").append(", ");
		hql.append(ALIAS_SELECT).append(" COALESCE(ipc.")
				.append(FatVlrItemProcedHospComps.Fields.QTD_MAXIMA_EXECUCAO
						.toString()).append(", 0) , COALESCE(ich.")
				.append(FatItemContaHospitalar.Fields.QUANTIDADE_REALIZADA
						.toString()).append(", 0)  FROM ").append(FatItensProcedHospitalar.class.getName())
				.append(" as iph   JOIN iph.")
				.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
						.toString()).append(" as cgi   JOIN iph.")
				.append(FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS
						.toString()).append(" as ipc   JOIN cgi.")
				.append(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO
						.toString()).append(" as phi   JOIN phi.")
				.append(FatProcedHospInternos.Fields.ITENS_CONTA_HOSPITALAR
						.toString()).append(" as ich   WHERE ich.")
				.append(FatItemContaHospitalar.Fields.CTH_SEQ.toString())
				.append(" = :cthSeq    and ich.")
				.append(FatItemContaHospitalar.Fields.PHI_SEQ.toString())
				.append(" = :phiSeq    and ich.")
				.append(FatItemContaHospitalar.Fields.IND_SITUACAO.toString())
				.append(" not in (:arrayIndSituacao)    and iph.")
				.append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString())
				.append(" = :phoSeq    and iph.")
				.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString())
				.append(" = :codTabela")

		// Ney 15/07/2011 Portaria 203 Fase 2
		.append("   and :pCompetencia between ipc.").append(
				FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA
						.toString())
		.append("        and COALESCE(ipc.")
				.append(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA
						.toString()).append(", :pmrNow) ");
		// and p_competencia between ipc.DT_INICIO_COMPETENCIA and
		// nvl(DT_FIM_COMPETENCIA,trunc(sysdate))

		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("cthSeq", cthSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameterList("arrayIndSituacao", arrayIndSituacao);
		query.setParameter("phoSeq", phoSeq);
		query.setParameter("codTabela", codTabela);
		query.setParameter("pCompetencia", competencia);
		query.setParameter("pmrNow", new Date());

		query.setMaxResults(1);

		List<Object[]> list = query.list();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	protected DetachedCriteria obterCriteriaFatItemProcHospAtivosPorPhoSeqSemSeqPorCodTabela(
			final Short phoSeq, final Integer seqExcluido, final Long codTabela) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne(FatItensProcedHospitalar.Fields.SEQ.toString(), seqExcluido));

		return criteria;
	}

	public FatItensProcedHospitalar buscarFatItensProcedHospitalarPorCodProcedimento(Long codProcediemento){

		DetachedCriteria criteria = obterCriteriaConsultarItemTabela(null, null, codProcediemento);
		
		List<FatItensProcedHospitalar> list = executeCriteria(criteria, 0, 1, null, true, CacheMode.NORMAL);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
		
	}
	
	public FatItensProcedHospitalar buscarFatItensProcedHospitalarPorCodProcedimentoFinan(Long codProcediemento){

		DetachedCriteria criteria = obterCriteriaConsultarItemTabelaFinan(codProcediemento);
		
		
		List<FatItensProcedHospitalar> list = executeCriteria(criteria, 0, 1, null, true, CacheMode.NORMAL);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
		
	}
	protected DetachedCriteria obterCriteriaConsultarItemTabela(final String descricao, final Short seq, final Long codTabela) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		if (!StringUtils.isBlank(descricao)) {
			criteria.add(
					Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if (seq != null) {
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), seq));
		}
		
		if (codTabela != null) {
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		}

		return criteria;
	}
	protected DetachedCriteria obterCriteriaConsultarItemTabelaFinan(Long codTabela) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class,"ITEM");
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO.toString(), "FIN", JoinType.LEFT_OUTER_JOIN);
		
		if (codTabela != null) {
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		}

		return criteria;
	}
	
	public List<FatItensProcedHospitalar> obterCursorAih5(Short phoSeq, Long codTabela, DominioSituacao situacao){
		DetachedCriteria criteria = obterCriteriaConsultarItemTabela(null, phoSeq, codTabela);
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		return executeCriteria(criteria);	
	}

	public List<FatItensProcedHospitalar> obterListaFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String order, final boolean asc, final String descricao,
			final Short seq, final Long codTabela) throws BaseException {
		List<FatItensProcedHospitalar> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaConsultarItemTabela(descricao, seq,
				codTabela);
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.SEQ
				.toString()));
		result = this.executeCriteria(criteria, firstResult, maxResult, order,
				asc);

		return result;
	}

	public Long obterListaFatItensProcedHospitalarCount(
			final String descricao, final Short seq, final Long codTabela) {
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaConsultarItemTabela(descricao, seq,
				codTabela);
		return executeCriteriaCount(criteria);
	}

	public int obterQuantidadeFatItemProcHospAtivosPorPhoSeqSemSeqPorCodTabela(
			final Short phoSeq, final Integer seqExcluido, final Long codTabela) {

		Long result = null;
		DetachedCriteria criteria = null;

		criteria = this
				.obterCriteriaFatItemProcHospAtivosPorPhoSeqSemSeqPorCodTabela(
						phoSeq, seqExcluido, codTabela);
		result = this.executeCriteriaCount(criteria);

		return (result != null ? result.intValue() : 0);
	}

	/**
	 * Implementa o cursor <code>c_ver_complex</code>
	 * 
	 * @param pPhoSeq
	 * @param pIphSeq
	 * @param situacao
	 * @return
	 */
	public List<FatItensProcedHospitalar> buscarComplexidade(final Short pPhoSeq,
			final Integer pIphSeq, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SEQ.toString(), pIphSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), pPhoSeq));
		criteria.createAlias(
				FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO
						.toString(),
				FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO
						.toString());
		criteria.add(Restrictions
				.eq(FatItensProcedHospitalar.Fields.SITUACAO_FAT_CARACTERISTICA_FINANCIAMENTO
						.toString(), situacao));
		return executeCriteria(criteria, 0, 1, null, true);
	}

	public FatItensProcedHospitalar obterItemProcedHospitalar(Short phoSeq,
			Integer seq) {
		DetachedCriteria cri = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		cri.add(Restrictions.eq(FatItensProcedHospitalar.Fields.SEQ.toString(),seq));
		cri.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		cri.createAlias(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(),JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(FatItensProcedHospitalar.Fields.CLINICA.toString(), FatItensProcedHospitalar.Fields.CLINICA.toString(),JoinType.LEFT_OUTER_JOIN);
		return (FatItensProcedHospitalar) executeCriteriaUniqueResult(cri, true);
	}

	/**
	 * Monta a consulta para listar FatItensProcedHospitalar filtrando pelo
	 * procedimento hospitalar E/OU seq do próprio Item Procedimento Hospitalar
	 * E/OU código da clínica.
	 * 
	 * @param phoSeq
	 *            (id do Procedimento Hospitalar)
	 * @param codTabela
	 * @param descricao
	 * @param seq
	 *            (do Item Procedimento Hospitalar)
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarConsultaItensProcedHospPorProcedHospPorClinicaPorCodTabelaPorDescricaoEPorSeq(
			final Short phoSeq, final Long codTabela, final String descricao,
			final Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);
		if (phoSeq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		}
		if (codTabela != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					codTabela));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		}

		return criteria;
	}

	/**
	 * Primeira FatItensProcedHospitalar ativos pelo código do acompanhante (
	 * <code>codTabela</code>)
	 * 
	 * @param codTabela
	 * @return
	 */
	public FatItensProcedHospitalar buscarPrimeiraPorAcompanhanteAtivo(
			final Long codTabela) {

		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
				codTabela));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.createAlias(
				FatItensProcedHospitalar.Fields.TIPO_ATO.toString(),
				FatItensProcedHospitalar.Fields.TIPO_ATO.toString());
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.TIPO_ATO.toString() + "."
						+ FatTipoAto.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		criteria.createAlias(
				FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString(),
				FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString());
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString() + "."
						+ FatTiposVinculo.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		List<FatItensProcedHospitalar> list = executeCriteria(criteria, 0, 1,
				null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public FatItensProcedHospitalar buscarPrimeiroItemProcedimentosHospitalares(
			final Long codigoTabela, final DominioSituacao situacao) {
		return this.buscarPrimeiroItemProcedimentosHospitalares(codigoTabela,
				situacao, null, null);
	}

	public FatItensProcedHospitalar buscarPrimeiroItemProcedimentosHospitalares(
			final Long codigoTabela, final DominioSituacao situacao,
			final DominioSituacao situacaoRegistroTipoVinculo,
			final DominioSituacao situacaoRegistroTipoAto) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		if (situacaoRegistroTipoVinculo != null) {
			criteria.createAlias(
					FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString(),
					FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString());

			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.TIPOS_VINCULO.toString()
							+ "." + FatTiposVinculo.Fields.SITUACAO.toString(),
					situacaoRegistroTipoVinculo));
		}

		if (situacaoRegistroTipoAto != null) {
			criteria.createAlias(
					FatItensProcedHospitalar.Fields.TIPO_ATO.toString(),
					FatItensProcedHospitalar.Fields.TIPO_ATO.toString());

			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.TIPO_ATO.toString() + "."
							+ FatTipoAto.Fields.SITUACAO.toString(),
					situacaoRegistroTipoAto));
		}

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
				codigoTabela));

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				situacao));

		List<FatItensProcedHospitalar> list = executeCriteria(criteria, 0, 1,
				null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<ItemProcedimentoHospitalarVO> obterProcedimentosMedicoAudtAIH(
			Integer cthSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(FatItensProcedHospitalar.Fields.DESCRICAO
								.toString()), "descricao")
				.add(Projections.sum("CMA."
						+ FatCampoMedicoAuditAih.Fields.VALOR_ANEST.toString()),
						"totalAnest")
				.add(Projections
						.sum("CMA."
								+ FatCampoMedicoAuditAih.Fields.VALOR_PROCED
										.toString()),
						"totalProcedimento")
				.add(Projections.sum("CMA."
						+ FatCampoMedicoAuditAih.Fields.VALOR_SADT.toString()),
						"totalSADT")
				.add(Projections.sum("CMA."
						+ FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP
								.toString()), "totalServHosp")
				.add(Projections.sum("CMA."
						+ FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF
								.toString()), "totalServProf")
				.add(Projections.groupProperty("CMA."
						+ FatCampoMedicoAuditAih.Fields.IPH_COD_SUS.toString()))
				.add(Projections.groupProperty("CMA."
						+ FatCampoMedicoAuditAih.Fields.EAI_SEQ.toString()))
				.add(Projections.groupProperty("CMA."
						+ FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString()))
				.add(Projections
						.groupProperty(FatItensProcedHospitalar.Fields.DESCRICAO
								.toString())));

		criteria.createAlias(
				FatItensProcedHospitalar.Fields.CAMPOS_MEDICOS_AUDIT_AIH
						.toString(), "CMA");

		criteria.add(Restrictions.ne("CMA."
				+ FatCampoMedicoAuditAih.Fields.IND_CONSISTENTE.toString(),
				DominioTipoItemConta.R));
		criteria.add(Restrictions.eq("CMA."
				+ FatCampoMedicoAuditAih.Fields.IND_MODO_COBRANCA.toString(),
				DominioModoCobranca.V));
		criteria.add(Restrictions.eq("CMA."
				+ FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ItemProcedimentoHospitalarVO.class));

		return executeCriteria(criteria);
	}

	public List<ItemProcedimentoHospitalarVO> obterProcedimentosAtosMedicosAIH(Integer cthSeq, Byte prmTaoSeq) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(FatItensProcedHospitalar.Fields.DESCRICAO
								.toString()), "descricao")
				.add(Projections.sum("AMA."
						+ FatAtoMedicoAih.Fields.VALOR_ANEST.toString()),
						"totalAnest")
				.add(Projections.sum("AMA."
						+ FatAtoMedicoAih.Fields.VALOR_PROCED.toString()),
						"totalProcedimento")
				.add(Projections.sum("AMA."
						+ FatAtoMedicoAih.Fields.VALOR_SADT.toString()),
						"totalSADT")
				.add(Projections.sum("AMA."
						+ FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.toString()),
						"totalServHosp")
				.add(Projections.sum("AMA."
						+ FatAtoMedicoAih.Fields.VALOR_SERV_PROF.toString()),
						"totalServProf")
				.add(Projections.groupProperty("TIV."
						+ FatTiposVinculo.Fields.SEQ.toString()))
				.add(Projections.groupProperty("TAO."
						+ FatTipoAto.Fields.SEQ.toString()))
				.add(Projections.groupProperty("AMA."
						+ FatAtoMedicoAih.Fields.NOTA_FISCAL.toString()))
				.add(Projections.groupProperty("AMA."
						+ FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()))
				.add(Projections.groupProperty("AMA."
						+ FatAtoMedicoAih.Fields.EAI_SEQ.toString()))
				.add(Projections.groupProperty("AMA."
						+ FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()))
				.add(Projections
						.groupProperty(FatItensProcedHospitalar.Fields.DESCRICAO
								.toString())));

		criteria.createAlias(
				FatItensProcedHospitalar.Fields.ATOS_MEDICOS_AIH.toString(),
				"AMA");
		criteria.createAlias("AMA." + FatAtoMedicoAih.Fields.TIV.toString(),
				"TIV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AMA." + FatAtoMedicoAih.Fields.TAO.toString(),
				"TAO", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.ne("AMA."
				+ FatAtoMedicoAih.Fields.IND_CONSISTENTE.toString(),
				DominioTipoItemConta.R));
		criteria.add(Restrictions.or(Restrictions.eq("AMA."
				+ FatAtoMedicoAih.Fields.IND_MODO_COBRANCA.toString(),
				DominioModoCobranca.V), Restrictions.eq("TAO."
				+ FatTipoAto.Fields.SEQ.toString(), prmTaoSeq)));
		criteria.add(Restrictions.eq("AMA."
				+ FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ItemProcedimentoHospitalarVO.class));

		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaAtivosPorCodTabela(
			final Long codTabela, final boolean ativos) {
		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		result.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		
		if (ativos) {
			result.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),	DominioSituacao.A));
		}

		return result;
	}

	public FatItensProcedHospitalar obterAtivosPorId(final Short phoSeq,
			final Integer seq, final boolean ativos) {
		return (FatItensProcedHospitalar) executeCriteriaUniqueResult(obterCriteriaAtivosPorId(
				phoSeq, seq, ativos), true);
	}

	protected DetachedCriteria obterCriteriaAtivosPorId(final Short phoSeq,
			final Integer seq, final boolean ativos) {
		DetachedCriteria result = obterCriteriaAtivosPorPhoSeq(phoSeq, ativos);
		result.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		return result;
	}

	protected DetachedCriteria obterCriteriaAtivosPorPhoSeq(final Short phoSeq,
			final boolean ativos) {
		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		result.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		if (ativos) {
			result.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
		}

		return result;
	}

	public Long obterListaAtivosPorPhoSeqCount(final Short phoSeq) {
		DetachedCriteria criteria = this.obterCriteriaAtivosPorPhoSeq(phoSeq,
				true);
		return this.executeCriteriaCount(criteria);
	}

	public List<FatItensProcedHospitalar> obterListaAtivosPorPhoSeq(
			final Short phoSeq) {

		List<FatItensProcedHospitalar> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaAtivosPorPhoSeq(phoSeq, true);
		result = this.executeCriteria(criteria);

		return result;
	}

	public Boolean isProcedimentoEspecialPorSeqEPhoSeq(final Short phoSeq,
			final Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(FatItensProcedHospitalar.Fields.PROCEDIMENTO_ESPECIAL
								.toString())));

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SEQ.toString(), seq));

		return (Boolean) executeCriteriaUniqueResult(criteria);
	}

	public Boolean isProcedimentoExistentePorCodTabela(final Long codTabela) {
		List<FatItensProcedHospitalar> result = null;
		DetachedCriteria criteria = this.obterCriteriaConsultarItemTabela(null,
				null, codTabela);

		result = this.executeCriteria(criteria);

		return (result == null || result.isEmpty()) ? false : true;
	}

	public List<FatItensProcedHospitalar> listarItensProcedHospPorProcedHospPorCodTabelaPorPhoSeq(
			final Short phoSeq, final Long codTabela) {

		final DetachedCriteria criteria = this
				.montarConsultaItensProcedHospPorProcedHospPorClinicaPorCodTabelaPorDescricaoEPorSeq(
						phoSeq, codTabela, null, null);

		return executeCriteria(criteria);
	}

	public List<FatItensProcedHospitalar> obterItemProcedHospPorPhoSeqPorPorGrupoPorSubGrupoPorSituacao(
			final Short phoSeq, final Short grpSeq, final Byte subGrupo,
			final DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SUBGRUPO_GRP_SEQ.toString(),
				grpSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SUBGRUPO_SUB_GRUPO.toString(),
				subGrupo));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				situacao));

		return executeCriteria(criteria);
	}

	public List<FatItensProcedHospitalar> listarItemProcedHospPorPhoSeqPorSeqPorPorSituacao(
			final Short phoSeq, final Integer seq,
			final DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		if (phoSeq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
					situacao));
		}
		return executeCriteria(criteria);
	}

	public List<FatItensProcedHospitalar> obterItemProcedHospPorPhoSeqPorCodTabelaPorSituacao(
			final Short phoSeq, final Long codTabela,
			final DominioSituacao situacao,
			Integer caracteristicaFinanciamento,
			Integer caracteristicaComplexidade) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
				codTabela));
//		criteria.add(Restrictions.or(
//				Restrictions
//						.isNull(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO_SEQ
//								.toString()),
//				Restrictions
//						.ne(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO_SEQ
//								.toString(), caracteristicaFinanciamento)));
//		criteria.add(Restrictions.or(
//				Restrictions
//						.isNull(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_COMPLEXIDADE_SEQ
//								.toString()),
//				Restrictions
//						.ne(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_COMPLEXIDADE_SEQ
//								.toString(), caracteristicaComplexidade)));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				situacao));

		return this.executeCriteria(criteria);

	}

	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = obterCriteriaListarItensProcedHospitalar(objPesquisa);
		criteria.createAlias(FatItensProcedHospitalar.Fields.CLINICA.toString(), "CLIN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO.toString(), "FIN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), "PRH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_COMPLEXIDADE.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.SEQ
				.toString()));

		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return executeCriteria(criteria, 0, 100, null, true, CacheMode.PUT);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ItensProcedHosp
	 * filtrando pela descricao ou pelo codigo, a partir de uma tabela (phoSeq)
	 * já definido e que estejam ativos.
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(
			Object objPesquisa, Short phoSeq, String order) {
		DetachedCriteria criteria = obterCriteriaListarItensProcedHospitalar(objPesquisa);
		criteria.createAlias(FatItensProcedHospitalar.Fields.CLINICA.toString(), "CLIN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO.toString(), "FIN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), "PRH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_COMPLEXIDADE.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		if (StringUtils.isNotBlank(order)) {
			criteria.addOrder(Order.asc(order));
		}

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ItensProcedHosp filtrando pela descricao ou pelo codigo, a partir de uma
	 * tabela (phoSeq) já definido e que estejam ativos.
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}

	/**
	 * Metodo para utilizado em grid que lista todos FatItensProcedHospitalar
	 * referente ao filtro passado.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param itensProcedimentosHospitalares
	 * @return
	 */
	public List<FatItensProcedHospitalar> listarItensProcedimentosHospitalares(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatItensProcedHospitalar itensProcedimentosHospitalares) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedimentosHospitalares(itensProcedimentosHospitalares);

		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.PHO_SEQ
				.toString()));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.SEQ
				.toString()));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA
				.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}
	
	/**
	 * Metodo que monta a consulta CritÃ©ria
	 * @param itensProcedimentosHospitalares
	 * @return
	 */
	protected DetachedCriteria obterCriteriaListarItensProcedimentosHospitalares(
			FatItensProcedHospitalar itensProcedimentosHospitalares) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		if (itensProcedimentosHospitalares.getProcedimentoHospitalar().getSeq() != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.PHO_SEQ.toString(),
					itensProcedimentosHospitalares.getProcedimentoHospitalar()
							.getSeq()));
		}

		if (itensProcedimentosHospitalares.getId().getSeq() != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.SEQ.toString(),
					itensProcedimentosHospitalares.getId().getSeq()));
		}

		if (itensProcedimentosHospitalares.getCodTabela() != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					itensProcedimentosHospitalares.getCodTabela()));
		}

		if (StringUtils.isNotBlank(itensProcedimentosHospitalares
				.getDescricao())) {
			criteria.add(Restrictions.ilike(
					FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					this.replaceCaracterEspecial(itensProcedimentosHospitalares.getDescricao()),
					MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	/**
	 * Metodo para retirar caracteres que o componente nÃ£o valida.
	 * @param descricao
	 * @return
	 */
	private String replaceCaracterEspecial(String descricao) {
        
	       return descricao.replace("_", "\\_").replace("%", "\\%");
	}

	/**
	 * Metodo de count utilizado em suggestionBox para pesquisar por
	 * ItensProcedHosp filtrando pela descricao ou pelo codigo.
	 * @param itensProcedimentosHospitalares
	 * @return
	 */
	public Long listarItensProcedimentosHospitalaresCount(
			FatItensProcedHospitalar itensProcedimentosHospitalares) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedimentosHospitalares(itensProcedimentosHospitalares);

		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaListarItensProcedHospitalar(
			Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);
		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroLong(strPesquisa)
				|| CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.eq(
					FatItensProcedHospitalar.Fields.SEQ.toString(),
					Integer.valueOf(strPesquisa)), Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					Long.valueOf(strPesquisa))));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	protected DetachedCriteria obterCriteriaIPHPorConvenioSaudePlanoConvProcedHosp(
			Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				FatItensProcedHospitalar.class, "IPH");

		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroLong(strPesquisa)) {
			criteria.add(Restrictions.eq("IPH."
					+ FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					Long.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("IPH."
					+ FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.createAlias(
				"IPH."
						+ FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR
								.toString(), "PROC", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(
				"PROC."
						+ FatProcedimentosHospitalares.Fields.FAT_CONV_PROCED_HOSPITALARES
								.toString(), "CPH", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(
				"CPH."
						+ FatConvProcedHospitalares.Fields.CONVENIO_SAUDE_PLANO
								.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("CNV."
				+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				"CSP", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("CSP."
				+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(),
				DominioGrupoConvenio.S));
		criteria.add(Restrictions.eq("CNV."
				+ FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(),
				DominioTipoPlano.I));

		// criteria.setResultTransformer(Transformers.aliasToBean(FatItensProcedHospitalar.class));

		return criteria;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ItensProcedHosp
	 * filtrando pela descricao ou pelo codigo, para uma determinada tabela
	 * (phoSeq).
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.PHO_SEQ
				.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Metodo de count utilizado em suggestionBox para pesquisar por
	 * ItensProcedHosp filtrando pela descricao ou pelo codigo, para uma
	 * determinada tabela (phoSeq).
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));

		return executeCriteriaCount(criteria);
	}

	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
			final Object objPesquisa, final Short phoSeq) {
		return executeCriteria(
				getBasicCriteriaItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
						objPesquisa, phoSeq, false), 0, 100, null, true);
	}

	private DetachedCriteria getBasicCriteriaItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
			final Object objPesquisa, final Short phoSeq, final Boolean isCount) {
		final DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(),
				Boolean.TRUE));

		if (!isCount) {
			criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.PHO_SEQ
					.toString()));
		}

		return criteria;
	}

	public Long listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProteseCount(
			final Object objPesquisa, final Short phoSeq) {
		return executeCriteriaCount(getBasicCriteriaItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
				objPesquisa, phoSeq, true));
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ItensProcedHosp
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			Object objPesquisa) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA
				.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * lista procedimentos ativos. filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarAtivosPorCodigoOuDescricao(
			Object objPesquisa, FatFormaOrganizacao formaOrganizacao, FatGrupo grupo,
			FatSubGrupo subGrupo) {
		
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		final Short phoSeq = 12;
		String strParametro = (String) objPesquisa;
		Long codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Long.valueOf(strParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					codigo));

		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}

		if (formaOrganizacao != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.FOG_CODIGO.toString(),
					formaOrganizacao.getId().getCodigo()));
		}
		
		if (grupo != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.toString(),
					grupo.getSeq()));
		}
		
		if (subGrupo != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.FOG_SGR_SUB_GRUPO.toString(),
					subGrupo.getId().getSubGrupo()));
		}
		
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(),
				phoSeq));
		
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA
				.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Metodo de count utilizado em suggestionBox para pesquisar por
	 * ItensProcedHosp filtrando pela descricao ou pelo codigo
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public Long listarFatItensProcedHospitalarCount(Object objPesquisa) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		return executeCriteriaCount(criteria);
	}

	public Long obterCodTabelaAtoMedico(final Integer seq, final Short phoSeq,
			final Integer eaiCthSeq, final Long codTabela) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(FatItensProcedHospitalar.Fields.COD_TABELA
						.toString())));

		criteria.createAlias(
				FatItensProcedHospitalar.Fields.ATOS_MEDICOS_AIH.toString(),
				"ATM");

		DetachedCriteria subQuery = DetachedCriteria.forClass(
				FatCompatExclusItem.class, "CEI");
		subQuery.setProjection(Projections.count("CEI."
				+ FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.toString()));
		subQuery.createAlias("CEI."
				+ FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(),
				"IPC");
		subQuery.add(Restrictions.eqProperty("IPC."
				+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), "ATM."
				+ FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString()));
		subQuery.add(Restrictions.eqProperty("IPC."
				+ FatItensProcedHospitalar.Fields.SEQ.toString(), "ATM."
				+ FatAtoMedicoAih.Fields.IPH_SEQ.toString()));
		subQuery.add(Restrictions.eq("IPC."
				+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		subQuery.add(Restrictions.eq("IPC."
				+ FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		subQuery.add(Restrictions.in("CEI."
				+ FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(),
				new DominioIndCompatExclus[] { DominioIndCompatExclus.PCI,
						DominioIndCompatExclus.ICP }));

		criteria.add(Restrictions.eq("ATM."
				+ FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		criteria.add(Subqueries.gt(Long.valueOf(0), subQuery));

		// criteria.addOrder(Order.desc("CEI."+FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.toString()));

		return (Long) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public List<ResumoCobrancaAihServicosVO> listarAtosMedicos(
			final Integer seq, final Integer cthSeq) {

		StringBuffer sql = new StringBuffer(100);
		if (isOracle()) {
			// QUERY NATIVA - ORACLE
			sql.append("SELECT DISTINCT")
			.append(" ATM.IPH_COD_SUS as codigo")
			.append(" ,IPH.DESCRICAO as descricao")
			.append(" ,ATM.QUANTIDADE as quantidade")
			.append(" ,ATM.VALOR_SADT as valorsadt")
			.append(" ,ATM.COMPETENCIA_UTI as competenciauti")
			.append(" ,ATM.VALOR_PROCEDIMENTO as valorprocedimento")
			.append(" ,ATM.VALOR_SERV_PROF as valorsp")
			.append(" ,ATM.VALOR_SERV_HOSP as valorsh")
			.append(" ,ATM.EAI_CTH_SEQ as eaicthseq")
			.append(" ,IPH.PHO_SEQ as iphphoseq")
			.append(" ,IPH.SEQ as iphseq")
			.append(" ,ATM.IPH_COD_SUS as iphcodsus")
			.append(" ,IPH.PHO_SEQ as iphophoseq")
			.append(" ,IPH.SEQ as iphoseq")
			.append(" ,IPH.FCF_SEQ as fcfseq")
			.append(" ,VAL.VLR_SADT as valvalorsadt")
			.append(" ,VAL.VLR_PROCEDIMENTO as valvalorprocedimento")
			.append(" ,VAL.VLR_SERV_PROFISSIONAL as valvalorsp")
			.append(" ,VAL.VLR_SERV_HOSPITALAR as valvalorsh")
			.append(" ,ATM.SEQ_ARQ_SUS as seqarqsus")
			.append(" ,ATM.TAO_SEQ as taoseq")
			.append(" ,ATM.TIV_SEQ as tivseq")
			.append(" FROM agh.FAT_ATOS_MEDICOS_AIH ATM")
			.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR IPH")
			//.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR IPHO")
			.append(" ,agh.FAT_VLR_ITEM_PROCED_HOSP_COMPS VAL")
			.append(" WHERE")
			.append(" ATM.EAI_CTH_SEQ = :cthSeq")
			.append(" AND ATM.IPH_PHO_SEQ <> :iphPhoSeq")
			.append(" AND IPH.PHO_SEQ = ATM.IPH_PHO_SEQ")
			.append(" AND IPH.SEQ = ATM.IPH_SEQ")
			.append(" AND IPH.IND_SITUACAO = :situacao")
			//.append(" AND IPHO.IND_SITUACAO = :situacao")
			.append(" AND VAL.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND VAL.IPH_SEQ = IPH.SEQ")
			.append(" AND VAL.DT_FIM_COMPETENCIA IS NULL")
			.append(" AND ( ")
			.append(" ( ")
			.append(" ( ")
			.append("SELECT")
			.append(" COUNT(COD_REGISTRO)")
			.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO FAT")
			.append(" WHERE")
			.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND FAT.IPH_SEQ = IPH.SEQ")
			.append(" AND COD_REGISTRO = :codRegistro05")
			.append(" ) >= 1")
			.append("AND (")
			.append(" (ATM.VALOR_SERV_HOSP + ATM.VALOR_SERV_PROF")
			.append(" + ATM.VALOR_SADT  + ATM.VALOR_PROCEDIMENTO ) > 0")
			.append(" )")
			.append(" )")
			.append("OR")
			.append(" ( ")
			.append("SELECT")
			.append(" COUNT(COD_REGISTRO)")
			.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO FAT")
			.append(" WHERE")
			.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND FAT.IPH_SEQ = IPH.SEQ")
			.append(" AND COD_REGISTRO = :codRegistro05")
			.append(" ) = 0")
			.append(" )")
			.append(" AND IPH.COD_TABELA = CAST( NVL( ")
			.append("( ")
			.append("SELECT")
			.append(" CASE")
			.append(" WHEN IPH_INNER.COD_TABELA IS NULL")
			.append(" THEN NULL")
			.append(" ELSE ")
			.append(" SUBSTR(lpad(IPH_INNER.COD_TABELA  ,10,'0')||'2',1,10)")
			.append(" END")
			.append(" FROM agh.FAT_ITENS_PROCED_HOSPITALAR IPH_INNER ,")
			.append(" agh.FAT_ATOS_MEDICOS_AIH ATM_INNER ,")
			.append(" agh.FAT_COMPETENCIAS_COMPATIBILID CPX ,")
			.append(" agh.FAT_COMPAT_EXCLUS_ITENS ICT_INNER")
			.append(" WHERE")
			.append(" ICT_INNER.IPH_PHO_SEQ_COMPATIBILIZA = ATM.IPH_PHO_SEQ")
			.append(" AND ICT_INNER.IPH_SEQ_COMPATIBILIZA = ATM.IPH_SEQ")
			.append(" AND ICT_INNER.IND_COMPAT_EXCLUS IN (:indCompatExcls)")
			.append(" AND CPX.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
			.append(" AND CPX.IPH_SEQ = ICT_INNER.IPH_SEQ")
			.append(" AND TRUNC(TO_DATE(NULL)) BETWEEN TRUNC(CPX.DT_INICIO_VALIDADE) AND TRUNC(NVL(CPX.DT_FIM_VALIDADE,SYSDATE))")
			.append(" AND CPX.SEQ = ICT_INNER.CPX_SEQ")
			.append(" AND ATM_INNER.EAI_CTH_SEQ = ATM.EAI_CTH_SEQ")
			.append(" AND ATM_INNER.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
			.append(" AND ATM_INNER.IPH_SEQ = ICT_INNER.IPH_SEQ")
			.append(" AND IPH_INNER.PHO_SEQ = ATM_INNER.IPH_PHO_SEQ")
			.append(" AND IPH_INNER.SEQ = ATM_INNER.IPH_SEQ")
			.append(" AND ROWNUM = 1")
			.append(" )")
			.append(" , SUBSTR(lpad(ATM.IPH_COD_SUS,10,'0')||'1',1,10)) AS NUMBER)")

			.append("  ORDER BY ATM.SEQ_ARQ_SUS");
		} else {
			// QUERY NATIVA - POSTGRE
			sql.append("SELECT DISTINCT")
			.append(" ATM.IPH_COD_SUS as codigo")
			.append(" ,IPH.DESCRICAO as descricao")
			.append(" ,ATM.QUANTIDADE as quantidade")
			.append(" ,ATM.VALOR_SADT as valorsadt")
			.append(" ,ATM.COMPETENCIA_UTI as competenciauti")
			.append(" ,ATM.VALOR_PROCEDIMENTO as valorprocedimento")
			.append(" ,ATM.VALOR_SERV_PROF as valorsp")
			.append(" ,ATM.VALOR_SERV_HOSP as valorsh")
			.append(" ,ATM.EAI_CTH_SEQ as eaicthseq")
			.append(" ,IPH.PHO_SEQ as iphphoseq")
			.append(" ,IPH.SEQ as iphseq")
			.append(" ,ATM.IPH_COD_SUS as iphcodsus")
			.append(" ,IPH.PHO_SEQ as iphophoseq")
			.append(" ,IPH.SEQ as iphoseq")
			.append(" ,IPH.FCF_SEQ as fcfseq")
			.append(" ,VAL.VLR_SADT as valvalorsadt")
			.append(" ,VAL.VLR_PROCEDIMENTO as valvalorprocedimento")
			.append(" ,VAL.VLR_SERV_PROFISSIONAL as valvalorsp")
			.append(" ,VAL.VLR_SERV_HOSPITALAR as valvalorsh")
			.append(" ,ATM.SEQ_ARQ_SUS as seqarqsus")
			.append(" ,ATM.TAO_SEQ as taoseq")
			.append(" ,ATM.TIV_SEQ as tivseq")
			.append(" FROM agh.FAT_ATOS_MEDICOS_AIH as ATM")
			.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR as IPH")
			.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR as IPHO")
			.append(" ,agh.FAT_VLR_ITEM_PROCED_HOSP_COMPS as VAL")
			.append(" WHERE")
			.append(" ATM.EAI_CTH_SEQ = :cthSeq")
			.append(" AND ATM.IPH_PHO_SEQ <> :iphPhoSeq")
			.append(" AND IPH.PHO_SEQ = ATM.IPH_PHO_SEQ")
			.append(" AND IPH.SEQ = ATM.IPH_SEQ")
			.append(" AND IPH.IND_SITUACAO = :situacao")
			//.append(" AND IPHO.IND_SITUACAO = :situacao")
			.append(" AND VAL.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND VAL.IPH_SEQ = IPH.SEQ")
			.append(" AND VAL.DT_FIM_COMPETENCIA IS NULL")
			.append(" AND ( ")
			.append(" ( ")
			.append(" ( ")
			.append("SELECT")
			.append(" COUNT(COD_REGISTRO)")
			.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO as FAT")
			.append(" WHERE")
			.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND FAT.IPH_SEQ = IPH.SEQ")
			.append(" AND COD_REGISTRO = :codRegistro05")
			.append(" ) >= 1")
			.append("AND (")
			.append(" (ATM.VALOR_SERV_HOSP + ATM.VALOR_SERV_PROF")
			.append(" + ATM.VALOR_SADT  + ATM.VALOR_PROCEDIMENTO ) > 0")
			.append(" )")
			.append(" )")
			.append("OR")
			.append(" ( ")
			.append("SELECT")
			.append(" COUNT(COD_REGISTRO)")
			.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO as FAT")
			.append(" WHERE")
			.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
			.append(" AND FAT.IPH_SEQ = IPH.SEQ")
			.append(" AND COD_REGISTRO = :codRegistro05")
			.append(" ) = 0")
			.append(" )")
			.append(" AND IPH.COD_TABELA = CAST( COALESCE( ")
			.append("( ")
			.append("SELECT")
			.append(" CASE")
			.append(" WHEN IPH_INNER.COD_TABELA IS NULL")
			.append(" THEN NULL")
			.append(" ELSE ")
			.append(" SUBSTR(lpad(CAST(IPH_INNER.COD_TABELA AS VARCHAR),10,'0')||'2',1,10) ")
			.append(" END")
			.append(" FROM agh.FAT_ITENS_PROCED_HOSPITALAR IPH_INNER ,")
			.append(" agh.FAT_ATOS_MEDICOS_AIH ATM_INNER ,")
			.append(" agh.FAT_COMPETENCIAS_COMPATIBILID CPX ,")
			.append(" agh.FAT_COMPAT_EXCLUS_ITENS ICT_INNER")
			.append(" WHERE")
			.append(" ICT_INNER.IPH_PHO_SEQ_COMPATIBILIZA = ATM.IPH_PHO_SEQ")
			.append(" AND ICT_INNER.IPH_SEQ_COMPATIBILIZA = ATM.IPH_SEQ")
			.append(" AND ICT_INNER.IND_COMPAT_EXCLUS IN (:indCompatExcls)")
			.append(" AND CPX.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
			.append(" AND CPX.IPH_SEQ = ICT_INNER.IPH_SEQ")
			.append(" AND DATE_TRUNC('DAY', TO_DATE('','DDMMYYYY')) BETWEEN DATE_TRUNC('DAY', CPX.DT_INICIO_VALIDADE) AND DATE_TRUNC('DAY', COALESCE(CPX.DT_FIM_VALIDADE,NOW()))")
			.append(" AND CPX.SEQ = ICT_INNER.CPX_SEQ")
			.append(" AND ATM_INNER.EAI_CTH_SEQ = ATM.EAI_CTH_SEQ")
			.append(" AND ATM_INNER.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
			.append(" AND ATM_INNER.IPH_SEQ = ICT_INNER.IPH_SEQ")
			.append(" AND IPH_INNER.PHO_SEQ = ATM_INNER.IPH_PHO_SEQ")
			.append(" AND IPH_INNER.SEQ = ATM_INNER.IPH_SEQ")
			.append(" ORDER BY ICT_INNER.QUANTIDADE_MAXIMA DESC")
			.append(" LIMIT 1")
			.append(" )")
			.append(" ,  SUBSTR(lpad(CAST(ATM.IPH_COD_SUS AS VARCHAR),10,'0')||'1',1,10)) AS BIGINT)")

			.append(" ORDER BY ATM.SEQ_ARQ_SUS");
		}

		SQLQuery q = createSQLQuery(sql.toString());
		q.setInteger("cthSeq", cthSeq);
		q.setShort("iphPhoSeq", Short.valueOf("6"));
		q.setString("situacao", DominioSituacao.A.toString());
		q.setString("codRegistro05", "05");
		q.setParameterList("indCompatExcls", new String[] {
				DominioIndCompatExclus.PCI.toString(),
				DominioIndCompatExclus.ICP.toString() });

		List<ResumoCobrancaAihServicosVO> listaVO = q
				.addScalar("codigo", LongType.INSTANCE)
				.addScalar("descricao", StringType.INSTANCE)
				.addScalar("quantidade", ShortType.INSTANCE)
				.addScalar("competenciauti", StringType.INSTANCE)
				.addScalar("valorsadt", BigDecimalType.INSTANCE)
				.addScalar("valorprocedimento", BigDecimalType.INSTANCE)
				.addScalar("valorsp", BigDecimalType.INSTANCE)
				.addScalar("valorsh", BigDecimalType.INSTANCE)
				.addScalar("eaicthseq", IntegerType.INSTANCE)
				.addScalar("iphphoseq", ShortType.INSTANCE)
				.addScalar("iphseq", IntegerType.INSTANCE)
				.addScalar("iphcodsus", LongType.INSTANCE)
				.addScalar("iphophoseq", ShortType.INSTANCE)
				.addScalar("iphoseq", IntegerType.INSTANCE)
				.addScalar("fcfseq", IntegerType.INSTANCE)
				.addScalar("valvalorsadt", BigDecimalType.INSTANCE)
				.addScalar("valvalorprocedimento", BigDecimalType.INSTANCE)
				.addScalar("valvalorsp", BigDecimalType.INSTANCE)
				.addScalar("valvalorsh", BigDecimalType.INSTANCE)
				.addScalar("seqarqsus", ShortType.INSTANCE)
				.addScalar("taoseq", IntegerType.INSTANCE)
				.addScalar("tivseq", IntegerType.INSTANCE)
				.setResultTransformer(
						Transformers
								.aliasToBean(ResumoCobrancaAihServicosVO.class))
				.list();

		

		return listaVO;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ResumoCobrancaAihServicosVO> listarAtosMedicosPrevia(
			final Integer seq, final Integer cthSeq) {

		StringBuffer sql = new StringBuffer(50);
		if (isOracle()) {
			// QUERY NATIVA - ORACLE
			sql.append("SELECT DISTINCT");
			sql.append(" ATM.EAI_SEQ as eaiseq");
			sql.append(" ,ATM.SEQP as seqp");
			sql.append(" ,ATM.IPH_COD_SUS as codigo");
			sql.append(" ,IPH.DESCRICAO as descricao");
			sql.append(" ,ATM.QUANTIDADE as quantidade");
			sql.append(" ,ATM.VALOR_SADT as valorsadt");
			sql.append(" ,ATM.VALOR_PROCEDIMENTO as valorprocedimento");
			sql.append(" ,ATM.VALOR_SERV_PROF as valorsp");
			sql.append(" ,ATM.VALOR_SERV_HOSP as valorsh");
			sql.append(" ,ATM.EAI_CTH_SEQ as eaicthseq");
			sql.append(" ,IPH.PHO_SEQ as iphphoseq");
			sql.append(" ,IPH.SEQ as iphseq");
			sql.append(" ,ATM.IPH_COD_SUS as iphcodsus");
			sql.append(" FROM agh.FAT_ATOS_MEDICOS_AIH ATM");
			sql.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR IPH");
			sql.append(" WHERE");
			sql.append(" ATM.EAI_CTH_SEQ = :cthSeq");
			sql.append(" AND ATM.IPH_PHO_SEQ <> :iphPhoSeq");
			sql.append(" AND IPH.PHO_SEQ = ATM.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ = ATM.IPH_SEQ");
			sql.append(" AND ( ");
			sql.append(" ( ");
			sql.append(" ( ");
			sql.append("SELECT");
			sql.append(" COUNT(COD_REGISTRO)");
			sql.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO FAT");
			sql.append(" WHERE");
			sql.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ");
			sql.append(" AND FAT.IPH_SEQ = IPH.SEQ");
			sql.append(" AND COD_REGISTRO = :codRegistro05");
			sql.append(" ) >= 1");
			sql.append("AND (");
			sql.append(" (ATM.VALOR_SERV_HOSP + ATM.VALOR_SERV_PROF");
			sql.append(" + ATM.VALOR_SADT  + ATM.VALOR_PROCEDIMENTO ) > 0");
			sql.append(" )");
			sql.append(" )");
			sql.append("OR");
			sql.append(" ( ");
			sql.append("SELECT");
			sql.append(" COUNT(COD_REGISTRO)");
			sql.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO FAT");
			sql.append(" WHERE");
			sql.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ");
			sql.append(" AND FAT.IPH_SEQ = IPH.SEQ");
			sql.append(" AND COD_REGISTRO = :codRegistro05");
			sql.append(" ) = 0");
			sql.append(" ) ORDER BY ATM.SEQ_ARQ_SUS");
		} else {
			// QUERY NATIVA - POSTGRESQL
			sql.append("SELECT DISTINCT");
			sql.append(" ATM.EAI_SEQ as eaiseq");
			sql.append(" ,ATM.SEQP as seqp");
			sql.append(" ,ATM.IPH_COD_SUS as codigo");
			sql.append(" ,IPH.DESCRICAO as descricao");
			sql.append(" ,ATM.QUANTIDADE as quantidade");
			sql.append(" ,ATM.VALOR_SADT as valorsadt");
			sql.append(" ,ATM.VALOR_PROCEDIMENTO as valorprocedimento");
			sql.append(" ,ATM.VALOR_SERV_PROF as valorsp");
			sql.append(" ,ATM.VALOR_SERV_HOSP as valorsh");
			sql.append(" ,ATM.EAI_CTH_SEQ as eaicthseq");
			sql.append(" ,IPH.PHO_SEQ as iphphoseq");
			sql.append(" ,IPH.SEQ as iphseq");
			sql.append(" ,ATM.IPH_COD_SUS as iphcodsus");
			sql.append(" FROM agh.FAT_ATOS_MEDICOS_AIH as ATM");
			sql.append(" ,agh.FAT_ITENS_PROCED_HOSPITALAR as IPH");
			sql.append(" WHERE");
			sql.append(" ATM.EAI_CTH_SEQ = :cthSeq");
			sql.append(" AND ATM.IPH_PHO_SEQ <> :iphPhoSeq");
			sql.append(" AND IPH.PHO_SEQ = ATM.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ = ATM.IPH_SEQ");
			sql.append(" AND ( ");
			sql.append(" ( ");
			sql.append(" ( ");
			sql.append("SELECT");
			sql.append(" COUNT(COD_REGISTRO)");
			sql.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO as FAT");
			sql.append(" WHERE");
			sql.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ");
			sql.append(" AND FAT.IPH_SEQ = IPH.SEQ");
			sql.append(" AND COD_REGISTRO = :codRegistro05");
			sql.append(" ) >= 1");
			sql.append("AND (");
			sql.append(" (ATM.VALOR_SERV_HOSP + ATM.VALOR_SERV_PROF");
			sql.append(" + ATM.VALOR_SADT  + ATM.VALOR_PROCEDIMENTO ) > 0");
			sql.append(" )");
			sql.append(" )");
			sql.append("OR");
			sql.append(" ( ");
			sql.append("SELECT");
			sql.append(" COUNT(COD_REGISTRO)");
			sql.append(" FROM agh.FAT_PROCEDIMENTOS_REGISTRO as FAT");
			sql.append(" WHERE");
			sql.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ");
			sql.append(" AND FAT.IPH_SEQ = IPH.SEQ");
			sql.append(" AND COD_REGISTRO = :codRegistro05");
			sql.append(" ) = 0");
			sql.append(" ) ORDER BY ATM.SEQ_ARQ_SUS ");
		}

		SQLQuery q = createSQLQuery(sql.toString());
		q.setInteger("cthSeq", cthSeq);
		q.setShort("iphPhoSeq", Short.valueOf("6"));
		q.setString("codRegistro05", "05");

		List<ResumoCobrancaAihServicosVO> listaVO = q
				.addScalar("eaiseq", IntegerType.INSTANCE)
				.addScalar("seqp", ByteType.INSTANCE)
				.addScalar("codigo", LongType.INSTANCE)
				.addScalar("descricao", StringType.INSTANCE)
				.addScalar("quantidade", ShortType.INSTANCE)
				.addScalar("valorsadt", BigDecimalType.INSTANCE)
				.addScalar("valorprocedimento", BigDecimalType.INSTANCE)
				.addScalar("valorsp", BigDecimalType.INSTANCE)
				.addScalar("valorsh", BigDecimalType.INSTANCE)
				.addScalar("eaicthseq", IntegerType.INSTANCE)
				.addScalar("iphphoseq", ShortType.INSTANCE)
				.addScalar("iphseq", IntegerType.INSTANCE)
				.addScalar("iphcodsus", LongType.INSTANCE)
				.setResultTransformer(
						Transformers
								.aliasToBean(ResumoCobrancaAihServicosVO.class))
				.list();

		return listaVO;
	}

	protected DetachedCriteria obterCriteriaDescricaoPhoSeqSeqCodTabela(
			final String descricao, final Short phoSeq, final Integer seq,
			final Long codTabela) {

		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		
		criteria.createAlias(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (!StringUtils.isBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}
		if (phoSeq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		}
		if (codTabela != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					codTabela));
		}

		return criteria;
	}

	public List<FatItensProcedHospitalar> obterListaFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String order, final boolean asc, final String descricao,
			final Integer seq, final Long codTabela, final Short phoSeq)
			throws BaseException {
		List<FatItensProcedHospitalar> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaDescricaoPhoSeqSeqCodTabela(descricao,
				phoSeq, seq, codTabela);

		result = this.executeCriteria(criteria, firstResult, maxResult, order,
				asc);

		return result;
	}

	public Long obterListaFatItensProcedHospitalarCount(
			final String descricao, final Integer seq, final Long codTabela,
			final Short phoSeq) {
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaDescricaoPhoSeqSeqCodTabela(descricao,
				phoSeq, seq, codTabela);

		return executeCriteriaCount(criteria);
	}

	/**
	 * Lista item procedimento hospitalar por phoSeq e seq caso sejam informados
	 * 
	 * @param phoSeq
	 * @param seq
	 * @return
	 */
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarPorPhoSeqECodTabela(
			Short phoSeq, Object codTabela, Integer limiteRegistros) {
		DetachedCriteria criteria = null;

		criteria = obterCriteriaItemProcedHospitalarPorPhoSeqECodTabela(phoSeq,
				codTabela);
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.SEQ
				.toString()));

		return executeCriteria(criteria, 0, limiteRegistros.intValue(), null,
				true);
	}

	private DetachedCriteria obterCriteriaItemProcedHospitalarPorPhoSeqECodTabela(
			Short phoSeq, Object param) {
		DetachedCriteria criteria = null;
		criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);

		if (phoSeq != null) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		}
		if (CoreUtil.isNumeroLong(param)) {
			criteria.add(Restrictions.eq(
					FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
					Long.parseLong(param.toString())));
		} else if (param != null && !param.toString().equals("")) {
			criteria.add(Restrictions.ilike(
					FatItensProcedHospitalar.Fields.DESCRICAO.toString(),
					param.toString(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return criteria;
	}

	/**
	 * Conta nro de itens procedimento hospitalar por phoSeq e seq caso sejam
	 * informados
	 * 
	 * @param phoSeq
	 * @param seq
	 * @return
	 */
	public Long listarFatItensProcedHospitalarPorPhoSeqECodTabelaCount(
			Short phoSeq, Object codTabela) {
		DetachedCriteria criteria = null;

		criteria = obterCriteriaItemProcedHospitalarPorPhoSeqECodTabela(phoSeq,
				codTabela);

		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings({"unchecked"})
	public List<BuscarProcedHospEquivalentePhiVO> buscarProcedHospEquivalentePhiLoadAll(
			List<FatItemContaHospitalar> listaItensContaHospitalar,
			Short qtdRealizada, Short cnvCodigo, Byte cnvCspSeq, Short grcSus) {
		
		final StringBuffer sql = montarQueryProcedHospEquivalentePhi(listaItensContaHospitalar, null,
				qtdRealizada, cnvCodigo, cnvCspSeq, grcSus);
		
		final SQLQuery query = createSQLQuery(sql.toString());
		final List<BuscarProcedHospEquivalentePhiVO> resultProcedHospEquivalentePhiVO = query
			.addScalar("pho", ShortType.INSTANCE)
			.addScalar("seq", IntegerType.INSTANCE)
			.addScalar("qtd", ShortType.INSTANCE)
			.addScalar("grp", StringType.INSTANCE)
			.addScalar("phiSeq", IntegerType.INSTANCE)
			.setResultTransformer(Transformers.aliasToBean(BuscarProcedHospEquivalentePhiVO.class))
			.list();
		
		return resultProcedHospEquivalentePhiVO;
	}
	
	/**
	 * Busca itens proced hospitalar equivalentes ao proced hospitalar interno
	 * ordenados por qtd procedimentos item
	 * 
	 * @param phiSeq
	 * @param qtdRealizada
	 * @param cnvCodigo
	 * @param cnvCspSeq
	 * @param grcSus
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public List<BuscarProcedHospEquivalentePhiVO> buscarProcedHospEquivalentePhi(
			Integer phiSeq, Short qtdRealizada, Short cnvCodigo,
			Byte cnvCspSeq, Short grcSus) {

		final StringBuffer sql = montarQueryProcedHospEquivalentePhi(null, phiSeq,
				qtdRealizada, cnvCodigo, cnvCspSeq, grcSus);

		final SQLQuery query = createSQLQuery(sql.toString());
		//Foi substituido pela concatenacao, pois causava erro no oracle: 
		//13:36:15,542 ERROR [JDBCExceptionReporter] ORA-00932: inconsistent datatypes: expected NUMBER got BINARY
//		query.setParameter("phiSeq", phiSeq);
//		query.setParameter("grcSus", grcSus);
//		query.setParameter("cnvCspSeq", cnvCspSeq);
//		query.setParameter("cnvCodigo", cnvCodigo);
//		query.setParameter("qtdRealizada", qtdRealizada);
//		query.setParameter("situacao", DominioSituacao.A.toString());
		
		final List<BuscarProcedHospEquivalentePhiVO> result = query
				.addScalar("pho", ShortType.INSTANCE)
				.addScalar("seq", IntegerType.INSTANCE)
				.addScalar("qtd", ShortType.INSTANCE)
				.addScalar("grp", StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(BuscarProcedHospEquivalentePhiVO.class))
				.setCacheable(true)
				.list();
		
		return result;
	}

	private StringBuffer montarQueryProcedHospEquivalentePhi(List<FatItemContaHospitalar> listItemContaHospitalar, Integer phiSeq,
			Short qtdRealizada, Short cnvCodigo, Byte cnvCspSeq, Short grcSus) {
		final StringBuffer sql = new StringBuffer(866);
		
		sql.append(ALIAS_SELECT)
			.append("  		cgi.iph_pho_seq pho, ")
			.append("  		cgi.iph_seq seq, ")
			.append("  		iph.qtd_procedimentos_item qtd, ")
			.append("  		SUBSTR(cast(cod_tabela as varchar(50)),1,5) grp ");
		
		if (listItemContaHospitalar != null && !listItemContaHospitalar.isEmpty()) {
			sql.append("        , PHI.SEQ phiSeq");
		}
		
		sql.append("  FROM ")
			.append("       AGH.").append(FatConvGrupoItemProced.class.getAnnotation(Table.class).name()).append(" cgi ")
			.append("     , AGH.").append(FatConvProcedHospitalares.class.getAnnotation(Table.class).name()).append(" cph ")
			.append("     , AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" iph ")
			.append("     , AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" phi ");
		
		if (listItemContaHospitalar != null && !listItemContaHospitalar.isEmpty()) {
			List<Integer> listPhiSeq = new LinkedList<Integer>();
			for (FatItemContaHospitalar regIch : listItemContaHospitalar) {
				listPhiSeq.add(regIch.getProcedimentoHospitalarInterno().getSeq());
			}
			
			String listPhiSeqStr = CoreUtil.criarClausulaIN("PHI.SEQ", "where", listPhiSeq);
			sql.append(listPhiSeqStr);			
		} else {
			sql.append(" WHERE  ");
			sql.append("        PHI.SEQ = ").append(phiSeq);
		}
		
		sql.append("   AND CGI.CPG_GRC_SEQ = ").append(grcSus)
			.append("   AND CGI.PHI_SEQ = PHI.SEQ ")
			.append("   AND CGI.CPG_CPH_CSP_SEQ = ").append(cnvCspSeq)
			.append("   AND CGI.CPG_CPH_CSP_CNV_CODIGO = ").append(cnvCodigo)
			.append("   AND IPH.PHO_SEQ = CGI.IPH_PHO_SEQ ")
			.append("   AND IPH.SEQ = CGI.IPH_SEQ ")
			
			// Milena 23/07/09 troquei 1 por 0 no nvl. problema com quant zerada
			// voltei para 1. Quantidade zerada gera divisão por zero. Preciso verificar mais abaixo.
			.append("   AND (CASE WHEN IPH.QTD_PROCEDIMENTOS_ITEM IS NULL THEN 1 ELSE IPH.QTD_PROCEDIMENTOS_ITEM END) <= ").append(qtdRealizada)
			.append("   AND IPH.IND_SITUACAO    = 'A' ")
			.append("   AND CGI.CPG_CPH_PHO_SEQ = CGI.IPH_PHO_SEQ ")
			.append("   AND CPH.CSP_SEQ = CGI.CPG_CPH_CSP_SEQ")
			.append("   AND CPH.CSP_CNV_CODIGO  = CGI.CPG_CPH_CSP_CNV_CODIGO ")
			.append("	AND cph.pho_seq         = cgi.cpg_cph_pho_seq ")
			/*
				--     and decode(p_internacao,'I',1,'A',iph.cod_tabela) <>  decode(p_internacao,'I',-1,'A',1)
				--     and phi.ind_situacao    = 'A'  -- FGi (06/10/2000)
			 */
			.append(" ORDER BY 4, iph.qtd_procedimentos_item DESC ");
		
		return sql;
	}

	// busca itens proced hospitalar equivalentes ao proc hospitalar interno na
	// tabela de excecao ordenados por qtd procedimentos item
	@SuppressWarnings("unchecked")
	public List<BuscarProcedHospEquivalentePhiVO> buscarProcedHospEquivalentePhiExcecao(
			Short iphPhoRealizSeq, Integer iphRealizSeq, Integer phiSeq,
			Short vGrcSus, Byte cnvCspSeq, Short cnvCodigo, String ttrCodigo,
			Short qtdRealizada, DominioOrigemProcedimento origemProcedimento) {
		StringBuffer sb = new StringBuffer()
				.append(ALIAS_SELECT)
				.append("  		egi.iph_pho_seq pho, ")
				.append("  		egi.iph_seq seq, ")
				.append("  		iph.qtd_procedimentos_item qtd, ")
				.append("  		SUBSTR(cast(cod_tabela as varchar(50)),1,5) grp ")

				.append("  FROM agh.fat_exc_cnv_grp_itens_proc egi, ")
				.append("       agh.fat_conv_proced_hospitalares cph, ")
				.append("       agh.fat_itens_proced_hospitalar iph, ")
				.append("       agh.fat_proced_hosp_internos phi ")

				.append(" WHERE cph.csp_seq = egi.cpg_cph_csp_seq ")
				.append("   AND cph.csp_cnv_codigo  = egi.cpg_cph_csp_cnv_codigo ")
				.append("   AND cph.pho_seq = egi.cpg_cph_pho_seq ")
				.append("   AND iph.pho_seq = egi.iph_pho_seq ")
				.append("   AND iph.seq = egi.iph_seq ")
				.append("   AND (case when iph.qtd_procedimentos_item is null then 1 else iph.qtd_procedimentos_item end) <= :qtdRealizada ");

		/* Comentei pois nunca entrará aqui origemProcedimento será passado como I
		if (DominioOrigemProcedimento.A.equals(origemProcedimento)) {
			sb.append("   AND iph.cod_tabela != 1 ");
			// and decode(p_internacao,'I',1,'A',iph.cod_tabela) <> decode(p_internacao,'I',-1,'A',1)
		}*/

		sb.append("   AND iph.ind_situacao = :situacao ")
		  .append("   AND phi.seq = egi.phi_seq ")
		  .append("   AND egi.iph_pho_seq_realizado = :iphPhoRealizSeq ")
		  .append("   AND egi.iph_seq_realizado = :iphRealizSeq ")
		  .append("   AND egi.phi_seq = :phiSeq ")
		  .append("   AND egi.cpg_grc_seq = :vGrcSus ")
		  .append("   AND egi.cpg_cph_csp_seq = :cnvCspSeq ")
		  .append("   AND egi.cpg_cph_csp_cnv_codigo = :cnvCodigo ");
		
		// and nvl(egi.ttr_codigo,'nulo') = nvl(p_ttr_codigo,'nulo')
		sb.append("  and egi.ttr_codigo= :ttrCodigo");
		
		sb.append(" AND egi.cpg_cph_pho_seq = egi.iph_pho_seq ")

		.append("  ORDER BY 4, iph.qtd_procedimentos_item DESC ");

		SQLQuery query = createSQLQuery(sb.toString());
		query.setShort("qtdRealizada", qtdRealizada);
		query.setString("situacao", DominioSituacao.A.toString());
		query.setInteger("iphPhoRealizSeq", iphPhoRealizSeq);
		query.setInteger("iphRealizSeq", iphRealizSeq);
		query.setInteger("phiSeq", phiSeq);
		query.setShort("vGrcSus", vGrcSus);
		query.setByte("cnvCspSeq", cnvCspSeq);
		query.setShort("cnvCodigo", cnvCodigo);
		query.setString("ttrCodigo", ttrCodigo);

		List<BuscarProcedHospEquivalentePhiVO> result = query
				.addScalar("pho", ShortType.INSTANCE)
				.addScalar("seq", IntegerType.INSTANCE)
				.addScalar("qtd", ShortType.INSTANCE)
				.addScalar("grp", StringType.INSTANCE)
				.setResultTransformer(
						Transformers
								.aliasToBean(BuscarProcedHospEquivalentePhiVO.class))
				.list();
		return result;
	}

	public List<FatItensProcedHospitalar> listarIPHPorConvenioSaudePlanoConvProcedHosp(
			Object objPesquisa) throws BaseException {
		final DetachedCriteria criteria = this
				.obterCriteriaIPHPorConvenioSaudePlanoConvProcedHosp(objPesquisa);

		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA
				.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarIPHPorConvenioSaudePlanoConvProcedHospCount(
			Object objPesquisa) throws BaseException {
		final DetachedCriteria criteria = this
				.obterCriteriaIPHPorConvenioSaudePlanoConvProcedHosp(objPesquisa);

		return executeCriteriaCount(criteria);
	}

	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarTabFatPadrao(Object objPesquisa, short prmPhoSeq) throws BaseException {
		DetachedCriteria criteria = this.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), true));
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), prmPhoSeq));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarFatItensProcedHospitalarTabFatPadraoCount(Object objPesquisa, short prmPhoSeq) throws BaseException {
		DetachedCriteria criteria = this.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), true));
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), prmPhoSeq));

		return executeCriteriaCount(criteria);
	}

	public List<FatItensProcedHospitalar> listarItensProcedHospTabPadraoPlanoInt(
			Object objPesquisa) throws BaseException {

		/*
		 * SELECT IPH.PHO_SEQ, IPH.SEQ FROM FAT_ITENS_PROCED_HOSPITALAR IPH,
		 * FAT_CONV_PROCED_HOSPITALARES CPH, FAT_CONV_SAUDE_PLANOS CSP,
		 * FAT_CONVENIOS_SAUDE CNV WHERE CNV.GRUPO_CONVENIO = 'S' AND CNV.CODIGO
		 * = CSP.CNV_CODIGO AND CSP.IND_TIPO_PLANO = 'I' AND CPH.CSP_CNV_CODIGO
		 * = CNV.CODIGO AND CPH.CSP_SEQ = CSP.SEQ AND IPH.PHO_SEQ = CPH.PHO_SEQ
		 * AND IPH.COD_TABELA = ? AND IPH.DESCRICAO LIKE ‘% ? %’ ORDER BY 1
		 */

		final StringBuffer sql = new StringBuffer(460);
		sql.append(ALIAS_SELECT)
				.append(" IPH.PHO_SEQ as phoseq, IPH.SEQ as seq")
				.append(" FROM AGH.FAT_ITENS_PROCED_HOSPITALAR IPH,")
				.append(" AGH.FAT_CONV_PROCED_HOSPITALARES CPH,")
				.append(" AGH.FAT_CONV_SAUDE_PLANOS CSP,")
				.append(" AGH.FAT_CONVENIOS_SAUDE CNV")
				.append(" WHERE ")
				.append(" CNV.GRUPO_CONVENIO = 'S' AND CNV.CODIGO = CSP.CNV_CODIGO ")
				.append(" AND CSP.IND_TIPO_PLANO = 'I' AND CPH.CSP_CNV_CODIGO = CNV.CODIGO")
				.append(" AND CPH.CSP_SEQ = CSP.SEQ AND IPH.PHO_SEQ = CPH.PHO_SEQ")
				.append(CoreUtil.isNumeroLong(objPesquisa) ? (" AND IPH.COD_TABELA ="
						+ Long.valueOf((String) objPesquisa) + " ")
						: "")
				.append((!CoreUtil.isNumeroLong(objPesquisa) && !StringUtils
						.isEmpty((String) objPesquisa)) ? (" AND IPH.DESCRICAO LIKE '%"
						+ (String) objPesquisa + "%'")
						: "").append(" ORDER BY IPH.COD_TABELA LIMIT 100");

		SQLQuery query = createSQLQuery(sql.toString());

		List<Object[]> listaVO = query.addScalar("phoseq", ShortType.INSTANCE)
				.addScalar("seq", IntegerType.INSTANCE).list();

		List<FatItensProcedHospitalar> lista = new ArrayList<FatItensProcedHospitalar>();
		for (Object[] id : listaVO) {
			lista.add(this.obterPorChavePrimaria(
							new FatItensProcedHospitalarId((Short) id[0],
									(Integer) id[1])));
		}

		return lista;
	}

	public Long listarItensProcedHospTabPadraoPlanoIntCount(
			Object objPesquisa) throws BaseException {

		final StringBuffer sql = new StringBuffer(450);
		sql.append(ALIAS_SELECT)
				.append(" COUNT(*) as cnt")
				.append(" FROM AGH.FAT_ITENS_PROCED_HOSPITALAR IPH,")
				.append(" AGH.FAT_CONV_PROCED_HOSPITALARES CPH,")
				.append(" AGH.FAT_CONV_SAUDE_PLANOS CSP,")
				.append(" AGH.FAT_CONVENIOS_SAUDE CNV")
				.append(" WHERE ")
				.append(" CNV.GRUPO_CONVENIO = 'S' AND CNV.CODIGO = CSP.CNV_CODIGO ")
				.append(" AND CSP.IND_TIPO_PLANO = 'I' AND CPH.CSP_CNV_CODIGO = CNV.CODIGO")
				.append(" AND CPH.CSP_SEQ = CSP.SEQ AND IPH.PHO_SEQ = CPH.PHO_SEQ")
				.append(CoreUtil.isNumeroLong(objPesquisa) ? (" AND IPH.COD_TABELA ="
						+ Long.valueOf((String) objPesquisa) + " ")
						: "")
				.append((!CoreUtil.isNumeroLong(objPesquisa) && !StringUtils
						.isEmpty((String) objPesquisa)) ? (" AND IPH.DESCRICAO LIKE '%"
						+ (String) objPesquisa + "%'")
						: "").append(" LIMIT 100");

		SQLQuery query = createSQLQuery(sql.toString());

		Long count = (Long) query.addScalar("cnt", LongType.INSTANCE)
				.uniqueResult();

		return count;
	}

	public Long listarItensProcedHospitalar(Integer seq, Short phoSeq,
			Boolean internacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatItensProcedHospitalar.class);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.SEQ.toString(), seq));

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(),
				internacao));

		criteria.setProjection(Projections
				.property(FatItensProcedHospitalar.Fields.COD_TABELA.toString()));

		return (Long) executeCriteriaUniqueResult(criteria);
	}


	/**
	 * Ativa Itens proced hospitalar Usado HQL para ficar mais otimizado
	 * 
	 * @param codTabela
	 */
	public void ativarItensProcedHospitalar(Long codTabela) {
		final StringBuilder hql = new StringBuilder();

		hql.append("UPDATE " + FatItensProcedHospitalar.class.getName()
				+ " SET "
				+ FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()
				+ " = :situacao " + " WHERE "
				+ FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()
				+ " = :indSituacao " + " AND "
				+ FatItensProcedHospitalar.Fields.COD_TABELA + " = :codTabela");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("indSituacao", DominioSituacao.I);
		query.setParameter("codTabela", codTabela);
		query.executeUpdate();
	}

	/**
	 * ORADB VIEW V_FAT_ASSOCIACAO_PROCEDIMENTOS Este método implementa a view
	 * descrita acima, porém trazendo apenas o campo CGI.CPG_CPH_CSP_CNV_CODIGO,
	 * necessário na pesquisa de procedimentos de internação quando um CID já
	 * tiver sido informado.
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq){
		final String aliasPHC = "phc";	// FAT_PROCED_HOSP_INT_CID	   phc
		DetachedCriteria criteria = obterCriteriaFatAssociacaoProcedimentos();
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.COD_TABELA.toString())));	
		criteria.createAlias(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.FAT_PROCED_INT_CIDS.toString(), aliasPHC);
		criteria.add(Restrictions.eq(aliasPHC + PONTO + FatProcedHospIntCid.Fields.CID_SEQ.toString(), cidSeq));
		
		List<Long> listaCodTabelas = executeCriteria(criteria);

		return listaCodTabelas;
			
	}
	
	/**
	 * ORADB VIEW V_FAT_ASSOCIACAO_PROCEDIMENTOS
	 * Este método implementa a view descrita acima
	 * 
	 * @return DetachedCriteria
	 */
	
	private DetachedCriteria obterCriteriaFatAssociacaoProcedimentos(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, ALIAS_IPH);

		criteria.createAlias(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString(), ALIAS_CGI);
		criteria.createAlias(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), ALIAS_PHI);
	
		criteria.add(Restrictions.gt(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), CPG_CPH_CSP_CNV_CODIGO));
		criteria.add(Restrictions.gt(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), CPG_CPH_CSP_SEQ));
		criteria.add(Restrictions.gt(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), CPG_CPH_PHO_SEQ));
		criteria.add(Restrictions.gt(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), CPG_GRC_SEQ));
		  
		return criteria;	
	}

	private DetachedCriteria obterCriteriaProcedCrgPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, 
			final Short especialidade,
			final Short iphPhoSeq,
			final Integer procedimento) {

		final String aliasEPR = "epr"; // MBC_ESPECIALIDADE_PROC_CIRGS  EPR -> MbcEspecialidadeProcCirgs
		
		DetachedCriteria criteria = obterCriteriaFatAssociacaoProcedimentos();
  
		criteria.createAlias(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), ALIAS_PCI, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.ESPECIALIDADES_PROCS_CIRGS.toString(), aliasEPR);
		criteria.createAlias(aliasEPR + PONTO + MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), ALIAS_ESP);
		
		criteria.add(Restrictions.eq(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(aliasEPR + PONTO + MbcEspecialidadeProcCirgs.Fields.SITUACAO.toString(), situacao));				
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.in(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), tipos));
		
		if(especialidade != null){
			criteria.add(Restrictions.eq(ALIAS_ESP + PONTO + AghEspecialidades.Fields.SEQ.toString(), especialidade));
		}
		
		if(procedimento != null){
			criteria.add(Restrictions.eq(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.SEQ, procedimento));
		}

		return criteria;
		
	}
	
	private void executarSetProjection(DetachedCriteria criteria){
		Projection projection = Projections.projectionList()
		.add(Projections.distinct(Projections.property(ALIAS_ESP + PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())), ProcedimentosCirurgicosPdtAtivosVO.Fields.ESPECIALIDADE.toString())
		.add(Projections.property(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentosCirurgicosPdtAtivosVO.Fields.PCI_SEQ.toString())
		.add(Projections.property(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosCirurgicosPdtAtivosVO.Fields.DESCRICAO.toString())
		.add(Projections.property(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.IND_CONTAMINACAO.toString()), ProcedimentosCirurgicosPdtAtivosVO.Fields.CONTAMINACAO.toString())
		.add(Projections.property(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.SEQ.toString()), ProcedimentosCirurgicosPdtAtivosVO.Fields.PHI_SEQ.toString());
		
		criteria.setProjection(projection);
	}
	
	private void executarSetResultTransformer(DetachedCriteria criteria){
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosCirurgicosPdtAtivosVO.class));
	}
	
	private void executarAddOrder(DetachedCriteria criteria){
		criteria.addOrder(Order.asc(ALIAS_ESP + PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		criteria.addOrder(Order.asc(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
	}
	
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedCirPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, 
			final Short especialidade, 
			final Short iphPhoSeq, 
			final Integer procedimento){
		
		DetachedCriteria criteria = obterCriteriaProcedCrgPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(tipos, situacao, especialidade, iphPhoSeq, procedimento);
		
		executarSetProjection(criteria);
		executarAddOrder(criteria);
		executarSetResultTransformer(criteria);
		
		return executeCriteria(criteria);	
	}
	
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, 
			Integer maxResult, 
			String orderProperty,
			boolean asc, 
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, 
			final Short especialidade,
			final Short iphPhoSeq, 
			final Integer procedimento) {

		DetachedCriteria criteria = obterCriteriaProcedCrgPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(tipos, situacao, especialidade, iphPhoSeq, procedimento);
		
		executarSetProjection(criteria);
		executarAddOrder(criteria);
		executarSetResultTransformer(criteria);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<Long> obterListaDeCodigoTabela(final Integer phiSeq,
			final DominioSituacao phiIndSituacao,
			final DominioSituacao iphIndSituacao,
			final Short cpgCphCspCnvCodigo, 
			final Byte cpgCphCspSeq, 
			final Short iphPhoSeq) {
		
		
		DetachedCriteria criteria = obterCriteriaFatAssociacaoProcedimentos();
		
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.SITUACAO.toString(), phiIndSituacao));
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), iphIndSituacao));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), iphPhoSeq));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.COD_TABELA.toString())));	
		
		List<Long> listaCodTabelas = executeCriteria(criteria);
		return listaCodTabelas;
	}
	
	/**
	 * ORADB VIEW V_FAT_SSM_INTERNACAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public DetachedCriteria getCriteriaFatSsmInternacaoView(Short phoSeq)
			throws ApplicationBusinessException {
		DetachedCriteria criteriaIPH = DetachedCriteria.forClass(
				FatItensProcedHospitalar.class, "IPH");

		criteriaIPH.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteriaIPH.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(),
				Boolean.TRUE));
		criteriaIPH.createAlias(
				FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS
						.toString(), "IPC");

		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.isNotNull("IPC."
				+ FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR
						.toString()));
		disjunction.add(Restrictions.isNotNull("IPC."
				+ FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL
						.toString()));
		disjunction.add(Restrictions.isNotNull("IPC."
				+ FatVlrItemProcedHospComps.Fields.VLR_SADT.toString()));

		criteriaIPH.add(Restrictions.or(Restrictions.and(Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(),
				true), disjunction), Restrictions.eq(
				FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(),
				false)));

		return criteriaIPH;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Map<Integer, List<ResumoAIHEmLoteServicosVO>> listarAtosMedicosResumoAihEmLote(
			final List<Integer> seqs) {

		final StringBuffer sql = new StringBuffer(2615);

		sql.append("SELECT ")
				.append("  ATM.IPH_COD_SUS         	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.CODIGO.toString())
				.append(" ,IPH.DESCRICAO          	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.DESCRICAO.toString())
				.append(" ,ATM.QUANTIDADE         	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.QUANTIDADE.toString())
				.append(" ,ATM.COMPETENCIA_UTI as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.COMPETENCIA_UTI.toString())
				.append(" ,ATM.VALOR_SADT         	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VALOR_SADT.toString())
				.append(" ,ATM.VALOR_PROCEDIMENTO 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VALOR_PROCEDIMENTO
						.toString())
				.append(" ,ATM.VALOR_SERV_PROF    	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VALOR_SP.toString())
				.append(" ,ATM.VALOR_SERV_HOSP    	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VALOR_SH.toString())
				.append(" ,ATM.EAI_CTH_SEQ 			as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.EAI_CTH_SEQ.toString())
				.append(" ,IPH.PHO_SEQ 				as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.IPH_PHO_SEQ.toString())
				.append(" ,IPH.SEQ 				 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.IPH_SEQ.toString())
				.append(" ,ATM.IPH_COD_SUS 		 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.IPH_COD_SUS.toString())
				.append(" ,IPHO.PHO_SEQ 			 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.IPHO_PHO_SEQ
						.toString())
				.append(" ,IPHO.SEQ 				 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.IPHO_SEQ.toString())
				.append(" ,IPHO.FCF_SEQ 			 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.FCF_SEQ.toString())
				.append(" ,VAL.VLR_SADT 			 	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SADT
						.toString())
				.append(" ,VAL.VLR_PROCEDIMENTO      as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_PROCEDIMENTO
						.toString())
				.append(" ,VAL.VLR_SERV_PROFISSIONAL as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SP
						.toString())
				.append(" ,VAL.VLR_SERV_HOSPITALAR	as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SH
						.toString())
				.append(" ,ATM.SEQ_ARQ_SUS 		    as ")
				.append(ResumoAIHEmLoteServicosVO.Fields.SEQ_ARQ_SUS.toString())
				.append(" ,ATM.TIV_SEQ				as tivseq")
				.append(" ,ATM.TAO_SEQ				as taoseq")
				// CAMPO REGISTRO
				.append(",CASE WHEN (  SELECT COD_REGISTRO ")
				.append("  FROM AGH.")
				.append(FatProcedimentoRegistro.class
						.getAnnotation(Table.class).name())
				.append(" FPRC ")
				.append(" WHERE FPRC.COD_REGISTRO=:prmCodRegistro03 ")
				.append("   AND FPRC.IPH_PHO_SEQ = IPH.PHO_SEQ ")
				.append("   AND FPRC.IPH_SEQ = IPH.SEQ")
				.append(" ) IS NOT NULL THEN '1PRINCIPAL' ELSE NULL END AS ")
				.append(ResumoAIHEmLoteServicosVO.Fields.REGISTRO.toString())

				// CAMPO REGISTRO_ORDER
				.append(", CASE WHEN (  SELECT COD_REGISTRO ")
				.append("  FROM AGH.")
				.append(FatProcedimentoRegistro.class
						.getAnnotation(Table.class).name())
				.append(" FPRC ")
				.append(" WHERE FPRC.COD_REGISTRO=:prmCodRegistro03 ")
				.append("   AND FPRC.IPH_PHO_SEQ = IPH.PHO_SEQ ")
				.append("   AND FPRC.IPH_SEQ = IPH.SEQ")
				.append(" ) IS NOT NULL ")

				.append(" THEN '1' ")
				.append(" WHEN (  SELECT COD_REGISTRO ")
				.append("		   FROM AGH.")
				.append(FatProcedimentoRegistro.class
						.getAnnotation(Table.class).name())
				.append(" FPRCC ")
				.append(" 		  WHERE FPRCC.COD_REGISTRO= :prmCodRegistro04 ")
				.append("   		AND FPRCC.IPH_PHO_SEQ = IPH.PHO_SEQ ")
				.append("   		AND FPRCC.IPH_SEQ = IPH.SEQ")
				.append("  	    ) IS NOT NULL ")
				.append("  THEN '2' ")
				.append("  ELSE '3' ")
				.append(" END AS ")
				.append(ResumoAIHEmLoteServicosVO.Fields.REGISTRO_ORDER
						.toString())

				// CAMPO SEQUENCIA
				.append(", ( SELECT FIPHC.COD_TABELA ")
				.append("     FROM AGH.")
				.append(FatItensProcedHospitalar.class.getAnnotation(
						Table.class).name())
				.append(" FIPHC ")
				.append("   	 INNER JOIN AGH.")
				.append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
				.append(" ATM1_ ON FIPHC.PHO_SEQ=ATM1_.IPH_PHO_SEQ  AND FIPHC.SEQ=ATM1_.IPH_SEQ ")
				.append(" 	WHERE ATM1_.EAI_CTH_SEQ=ATM.EAI_CTH_SEQ ")
				.append("   		AND 0 >  ( ")
				.append("  SELECT ")
				.append(" 		 COUNT(CEI_.QUANTIDADE_MAXIMA)")
				.append("   FROM AGH.")
				.append(FatCompatExclusItem.class.getAnnotation(Table.class)
						.name())
				.append(" CEI_ ")
				.append("    INNER JOIN AGH.")
				.append(FatItensProcedHospitalar.class.getAnnotation(
						Table.class).name())
				.append(" IPC1_ ON CEI_.IPH_PHO_SEQ_COMPATIBILIZA=IPC1_.PHO_SEQ AND CEI_.IPH_SEQ_COMPATIBILIZA=IPC1_.SEQ ")
				.append("  WHERE ")
				.append("        IPC1_.PHO_SEQ=ATM1_.IPH_PHO_SEQ ")
				.append(" 	 AND IPC1_.SEQ=ATM1_.IPH_SEQ ")
				.append("    AND IPC1_.PHO_SEQ= IPH.PHO_SEQ ")
				.append(" 	 AND IPC1_.SEQ=IPH.SEQ")
				.append(" 	 AND CEI_.IND_COMPAT_EXCLUS IN (:prmIndCompactExclu) ")
				.append(" ) ")
				.append(' ')

				.append("  )  AS ")
				.append(ResumoAIHEmLoteServicosVO.Fields.SEQUENCIA.toString())

				.append(" FROM AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" ATM")
				.append(" ,AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH")
				.append(" ,AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPHO")
				.append(" ,AGH.").append(FatVlrItemProcedHospComps.class.getAnnotation(Table.class).name()).append(" VAL")

				// .append(" WHERE").append(" ATM.EAI_CTH_SEQ in (:cthSeq) ")
				.append(CoreUtil.criarClausulaIN("ATM.EAI_CTH_SEQ", "where",
						seqs))
				.append(" AND ATM.IPH_PHO_SEQ <> :prmIphPhoSeq")
				.append(" AND IPH.PHO_SEQ = ATM.IPH_PHO_SEQ")
				.append(" AND IPH.SEQ = ATM.IPH_SEQ")
				.append(" AND IPH.IND_SITUACAO = :prmSituacao")
				.append(" AND IPHO.IND_SITUACAO = :prmSituacao")
				.append(" AND VAL.IPH_PHO_SEQ = IPHO.PHO_SEQ")
				.append(" AND VAL.IPH_SEQ = IPHO.SEQ")
				.append(" AND VAL.DT_FIM_COMPETENCIA IS NULL")
				.append(" AND ( ")
				.append(" ( ")
				.append(" ( ")
				.append("SELECT")
				.append(" COUNT(COD_REGISTRO)")
				.append(" FROM agh.")
				.append(FatProcedimentoRegistro.class
						.getAnnotation(Table.class).name())
				.append(" FAT")
				.append(" WHERE")
				.append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
				.append(" AND FAT.IPH_SEQ = IPH.SEQ")
				.append(" AND COD_REGISTRO = :prmCodRegistro05")
				.append(" ) >= 1")
				.append("AND (")
				.append(" (ATM.VALOR_SERV_HOSP + ATM.VALOR_SERV_PROF")
				.append(" + ATM.VALOR_SADT  + ATM.VALOR_PROCEDIMENTO ) > 0")
				.append(" )")
				.append(" )")
				.append("OR")
				.append(" ( ")
				.append("SELECT")
				.append(" COUNT(COD_REGISTRO)")
				.append(" FROM agh.")
				.append(FatProcedimentoRegistro.class
						.getAnnotation(Table.class).name()).append(" FAT")
				.append(" WHERE").append(" FAT.IPH_PHO_SEQ = IPH.PHO_SEQ")
				.append(" AND FAT.IPH_SEQ = IPH.SEQ")
				.append(" AND COD_REGISTRO = :prmCodRegistro05")
				.append(" ) = 0").append(" )");

		// QUERY NATIVA - ORACLE
		if (isOracle()) {
			sql.append(" AND IPHO.COD_TABELA = CAST( NVL( ")
					.append("( ")
					.append(" SELECT")
					.append("     CASE WHEN IPH_INNER.COD_TABELA IS NULL ")
					.append("       THEN NULL")
					.append("       ELSE SUBSTR(lpad(IPH_INNER.COD_TABELA  ,10,'0')||'2',1,10) ")
					.append("     END")
					.append(" FROM AGH.")
					.append(FatItensProcedHospitalar.class.getAnnotation(
							Table.class).name())
					.append(" IPH_INNER ,")
					.append("      AGH.")
					.append(FatAtoMedicoAih.class.getAnnotation(Table.class)
							.name())
					.append(" ATM_INNER ,")
					.append("      AGH.")
					.append(FatCompatExclusItem.class
							.getAnnotation(Table.class).name())
					.append(" ICT_INNER")
					.append(" WHERE")
					.append("       ICT_INNER.IPH_PHO_SEQ_COMPATIBILIZA = ATM.IPH_PHO_SEQ")
					.append("   AND ICT_INNER.IPH_SEQ_COMPATIBILIZA = ATM.IPH_SEQ")
					.append("   AND ICT_INNER.IND_COMPAT_EXCLUS IN (:prmIndCompatExcls)")
					.append("   AND ATM_INNER.EAI_CTH_SEQ = ATM.EAI_CTH_SEQ")
					.append("   AND ATM_INNER.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
					.append("   AND ATM_INNER.IPH_SEQ = ICT_INNER.IPH_SEQ")
					.append("   AND IPH_INNER.PHO_SEQ = ATM_INNER.IPH_PHO_SEQ")
					.append("   AND IPH_INNER.SEQ = ATM_INNER.IPH_SEQ")
					.append("   AND ROWNUM = 1")
					.append(" )")

					.append(" , SUBSTR(lpad(ATM.IPH_COD_SUS,10,'0')||'1',1,10)) AS NUMBER)");

			// QUERY NATIVA - POSTGRESQL
		} else {
			sql.append(" AND IPHO.COD_TABELA = CAST( COALESCE( ");
			sql.append("( ")
					.append("SELECT")
					.append("   CASE WHEN IPH_INNER.COD_TABELA IS NULL ")
					.append("     THEN NULL ")
					.append("     ELSE SUBSTR(lpad(CAST(IPH_INNER.COD_TABELA AS VARCHAR),10,'0')||'2',1,10) ")
					.append("   END")

					.append(" FROM AGH.")
					.append(FatItensProcedHospitalar.class.getAnnotation(
							Table.class).name())
					.append(" IPH_INNER ,")
					.append(" 	  AGH.")
					.append(FatAtoMedicoAih.class.getAnnotation(Table.class)
							.name())
					.append(" ATM_INNER ,")
					.append("      AGH.")
					.append(FatCompatExclusItem.class
							.getAnnotation(Table.class).name())
					.append(" ICT_INNER")

					.append(" WHERE ")
					.append("       ICT_INNER.IPH_PHO_SEQ_COMPATIBILIZA = ATM.IPH_PHO_SEQ")
					.append("   AND ICT_INNER.IPH_SEQ_COMPATIBILIZA = ATM.IPH_SEQ")
					.append("   AND ICT_INNER.IND_COMPAT_EXCLUS IN (:prmIndCompatExcls)")
					.append("   AND ATM_INNER.EAI_CTH_SEQ = ATM.EAI_CTH_SEQ")
					.append("   AND ATM_INNER.IPH_PHO_SEQ = ICT_INNER.IPH_PHO_SEQ")
					.append("   AND ATM_INNER.IPH_SEQ = ICT_INNER.IPH_SEQ")
					.append("   AND IPH_INNER.PHO_SEQ = ATM_INNER.IPH_PHO_SEQ")
					.append("   AND IPH_INNER.SEQ = ATM_INNER.IPH_SEQ")

					.append(" ORDER BY ICT_INNER.QUANTIDADE_MAXIMA DESC")
					.append(" LIMIT 1").append(" )");

			sql.append(" ,  SUBSTR(lpad(CAST(ATM.IPH_COD_SUS AS VARCHAR),10,'0')||'1',1,10)) AS BIGINT) ");
		}

		sql.append(" order by ATM.SEQ_ARQ_SUS ");
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setShort("prmIphPhoSeq", Short.valueOf("6"));
		q.setString("prmSituacao", DominioSituacao.A.toString());
		q.setString("prmCodRegistro03", "03");
		q.setString("prmCodRegistro04", "04");
		q.setString("prmCodRegistro05", "05");
		q.setParameterList("prmIndCompatExcls", new String[] {
				DominioIndCompatExclus.PCI.toString(),
				DominioIndCompatExclus.ICP.toString() });
		q.setParameterList("prmIndCompactExclu", new String[] {
				DominioIndCompatExclus.PCI.toString(),
				DominioIndCompatExclus.ICP.toString() });

		List<ResumoAIHEmLoteServicosVO> lst = q
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.CODIGO.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.DESCRICAO.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.QUANTIDADE.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.COMPETENCIA_UTI.toString(),StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VALOR_SADT.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VALOR_PROCEDIMENTO.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VALOR_SP.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VALOR_SH.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.EAI_CTH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.IPH_COD_SUS.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.IPHO_PHO_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.IPHO_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.FCF_SEQ.toString(),IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SADT.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_PROCEDIMENTO.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SP.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.VAL_VALOR_SH.toString(),BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteServicosVO.Fields.SEQ_ARQ_SUS.toString(), ShortType.INSTANCE)
				.addScalar("tivseq", IntegerType.INSTANCE)
				.addScalar("taoseq", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(ResumoAIHEmLoteServicosVO.class))
				.list();

		final Map<Integer, List<ResumoAIHEmLoteServicosVO>> result = new HashMap<Integer, List<ResumoAIHEmLoteServicosVO>>();
		for (ResumoAIHEmLoteServicosVO vo : lst) {
			if (!result.containsKey(vo.getEaicthseq())) {
				result.put(vo.getEaicthseq(),
						new ArrayList<ResumoAIHEmLoteServicosVO>());
			}
			result.get(vo.getEaicthseq()).add(vo);
		}

		return result;
	}

	public FatItensProcedHospitalar obterItemProcedimentoHospitalar( Integer iphSeq, Short iphPhoSeq) {
		if (iphSeq == null || iphPhoSeq == null) {
			return null;
		} else {
			DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
			criteria.setFetchMode(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), FetchMode.JOIN);
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(),iphPhoSeq));

			return (FatItensProcedHospitalar) executeCriteriaUniqueResult(criteria);
		}
	}

	public FatGrupoSubGrupoVO obterFatGrupoSubGrupoVOPorCodTabela(
			final Long codTabela) {
		@SuppressWarnings("rawtypes")
		List result = executeCriteria(obterCriteriaFatGrupoSubGrupoVOPorCodTabela(codTabela));
		if (result == null || result.isEmpty()) {
			return null;
		}
		return (FatGrupoSubGrupoVO) result.get(0);
	}

	private DetachedCriteria obterCriteriaFatGrupoSubGrupoVOPorCodTabela(
			final Long codTabela) {
		/*
		 * CURSOR c_busca_grupo (c_cod_tabela
		 * fat_itens_proced_hospitalar.cod_tabela%type) IS SELECT grp.codigo
		 * grupo, sgr.sub_grupo FROM FAT_SUB_GRUPOS sgr, FAT_GRUPOS grp,
		 * fat_itens_proced_hospitalar iph WHERE iph.cod_tabela = c_cod_tabela
		 * AND grp.seq = iph.fog_sgr_grp_seq AND sgr.grp_seq =
		 * iph.fog_sgr_grp_seq AND sgr.sub_grupo = iph.fog_sgr_sub_grupo;
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		criteria.createAlias(FatItensProcedHospitalar.Fields.GRUPO.toString(),"grp");
		criteria.createAlias(FatItensProcedHospitalar.Fields.SUB_GRUPO.toString(), "sgr");
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(),codTabela));
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property("grp."+ FatGrupo.Fields.CODIGO.toString()), FatGrupoSubGrupoVO.Fields.GRUPO.toString())
					.add(Projections.property("sgr."+ FatSubGrupo.Fields.ID_SUB_GRUPO.toString()), FatGrupoSubGrupoVO.Fields.SUB_GRUPO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatGrupoSubGrupoVO.class));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<CursorAtoMedicoAihVO> listarAtosMedicosAih(Integer cthSeq) {

		final StringBuilder hql = new StringBuilder(340);
		hql.append(ALIAS_SELECT)
		   .append("  cth.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_ALTA_ADMINISTRATIVA.toString())
		   .append(", cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_INT_ADMINISTRATIVA.toString())
		   .append(", cth.").append(FatContasHospitalares.Fields.SIA_MSP_SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.MOTIVO.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.IND_COBRANCA_DIARIAS.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IND_COBRANCA_DIARIAS.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.IND_QTD_MAIOR_INTERNACAO.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IND_QTD_MAIOR_INTERNACAO.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IPH_PHO_SEQ.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IPH_SEQ.toString())
		   .append(", ama as ").append(CursorAtoMedicoAihVO.Fields.ATO_MEDICO_AIH.toString());

		// from
		hql.append(" from ")
		   .append(FatItensProcedHospitalar.class.getName()).append(" as iph")
		   .append(", ").append(FatAtoMedicoAih.class.getName()).append(" as ama ")
		   .append(", ").append(FatContasHospitalares.class.getName()).append(" as cth ");
		
		// where
		hql.append(" where 1=1 ")
		   .append(" and ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append(" = ")
		   					.append(" cth.").append(FatContasHospitalares.Fields.SEQ.toString())

		   .append(" and ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append(" = :cthSeq")

		   .append(" and iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()).append(" = ")
		   					.append(" ama.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString())

		   .append(" and iph.").append(FatItensProcedHospitalar.Fields.SEQ.toString()).append(" = ")
		   					.append(" ama.").append(FatAtoMedicoAih.Fields.IPH_SEQ.toString());

		// order
		hql.append(" order by ")
		   .append(" ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString())
		   .append(", ama.").append(FatAtoMedicoAih.Fields.EAI_SEQ.toString())
		   .append(", ama.").append(FatAtoMedicoAih.Fields.SEQP.toString());

		// query
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);

		query.setResultTransformer(Transformers.aliasToBean(CursorAtoMedicoAihVO.class));
		
		List<CursorAtoMedicoAihVO> result = query.list();

		return result;
	}
	

	@SuppressWarnings("unchecked")
	/**
	 * ORADB: Implementa cursor: FATP_SEPARA_ITENS_POR_COMP.RN_DESDOBRA_MULTIPLA.C_REALIZADO
	 * DAOTest: FatItensProcedHospitalarDAOTest.listarAtosMedicosAih (Retestar qd alterado) eSchweigert 17/09/2012|
	 */
	public List<CursorAtoMedicoAihVO> listarAtosMedicosAih(final Integer cthSeq, final Byte taoSeq) {

		final StringBuilder hql = new StringBuilder(447);
		hql.append(ALIAS_SELECT)
		   .append("  cth.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_ALTA_ADMINISTRATIVA.toString())
		   .append(", cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_INT_ADMINISTRATIVA.toString())
		   
		   // TO_NUMBER(TO_CHAR(ADD_MONTHS(CTH.DT_ALTA_ADMINISTRATIVA,0),'DD')) DIA_DA_ALTA, realizado internamente no vo em getDiaDaAlta
		   // TO_CHAR(LAST_DAY(CTH.DT_ALTA_ADMINISTRATIVA),'DD') QTDE_DIAS_MES,              realizado internamente no vo em getQtdeDiasMes
		   // TO_CHAR(CTH.DT_ALTA_ADMINISTRATIVA,'YYYYMM') ANO_MES_ALTA,          		     realizado internamente no vo em getAnoMesAlta
		   // TO_CHAR(CTH.DT_INT_ADMINISTRATIVA,'YYYYMM') ANO_MES_INT,          		     realizado internamente no vo em getAnoMesInt
		   // TO_CHAR(ADD_MONTHS(CTH.DT_INT_ADMINISTRATIVA,0),'DD') DIA_MES_INT,		     realizado internamente no vo em getDiaMesInt
		   .append(", cth.").append(FatContasHospitalares.Fields.SIA_MSP_SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.MOTIVO.toString())
		   
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.IND_COBRANCA_DIARIAS.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IND_COBRANCA_DIARIAS.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.IND_QTD_MAIOR_INTERNACAO.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IND_QTD_MAIOR_INTERNACAO.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IPH_PHO_SEQ.toString())
		   .append(", iph.").append(FatItensProcedHospitalar.Fields.SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.IPH_SEQ.toString())
		   .append(", ama as ").append(CursorAtoMedicoAihVO.Fields.ATO_MEDICO_AIH.toString());

		// from
		hql.append(" from ")
		   .append(FatItensProcedHospitalar.class.getName()).append(" AS IPH")
		   .append(", ").append(FatAtoMedicoAih.class.getName()).append(" AS AMA ")
		   .append(", ").append(FatItensProcedHospitalar.class.getName()).append(" AS IPH_R")
		   .append(", ").append(FatEspelhoAih.class.getName()).append(" AS EAI")
		   .append(", ").append(FatContasHospitalares.class.getName()).append(" AS CTH ");
		
		// where
		hql.append(" where 1=1 ")
		   .append("   and cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :PRM_CTH_SEQ")
		   
		   .append("   and eai.").append(FatEspelhoAih.Fields.CTH_SEQ.toString()).append(" = ")
		   						 .append(" cth.").append(FatContasHospitalares.Fields.SEQ.toString())
		   
		   .append("   and eai.").append(FatEspelhoAih.Fields.SEQP.toString()).append(" = :PRM_SEQP")
		   
		   .append("   and iph_r.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()).append(" = ")
		   						   .append(" eai.").append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.toString())
		   
		   .append("   and iph_r.").append(FatItensProcedHospitalar.Fields.SEQ.toString()).append(" = ")
		   						 .append(" eai.").append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.toString())
		   
		   .append("   and Iph_R.").append(FatItensProcedHospitalar.Fields.IND_CIRURGIA_MULTIPLA.toString()).append(" = :PRM_IND_CIRURGIA_MULTIPLA")
		   
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append(" = ")
		   						 .append(" EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.toString())
		   
		   .append("   and iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()).append(" = ")
		   					     .append(" ama.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString())
		   
		   .append("   and iph.").append(FatItensProcedHospitalar.Fields.SEQ.toString()).append(" = ")
		   					     .append(" ama.").append(FatAtoMedicoAih.Fields.IPH_SEQ.toString())
		   	   
		   .append("   and iph.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()).append(" = :PRM_IND_SITUACAO")
	
		   //  Ney 2012/09/10
		   //.append("   and ama.").append(FatAtoMedicoAih.Fields.TAO_SEQ.toString()).append(" = :PRM_TAO_SEQ")
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.TAO_SEQ.toString()).append(" in (:PRM_TAO_SEQ) ")
		   
		   .append(" order by ama.").append(FatAtoMedicoAih.Fields.SEQP.toString())
		   ;

		// query
		final Query query = createHibernateQuery(hql.toString());
		query.setInteger("PRM_CTH_SEQ", cthSeq);
		query.setInteger("PRM_SEQP", Integer.valueOf(1));
		
		final Byte[] taosSeq = {0,taoSeq};
		query.setParameterList("PRM_TAO_SEQ", taosSeq);
		query.setParameter("PRM_IND_SITUACAO", DominioSituacao.A);
		query.setParameter("PRM_IND_CIRURGIA_MULTIPLA", Boolean.TRUE);
		
		query.setResultTransformer(Transformers.aliasToBean(CursorAtoMedicoAihVO.class));
		
		final List<CursorAtoMedicoAihVO> result = query.list();

		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * ORADB: Implementa cursor: FATP_SEPARA_ITENS_POR_COMP.RN_DESDOBRA_MULTIPLA.C_ATO
	 * DAOTest: FatItensProcedHospitalarDAOTest.listarAtosMedicosAih1 (Retestar qd alterado) eSchweigert 17/09/2012|
	 */
	public List<CursorAtoMedicoAihVO> listarAtosMedicosAih(final Integer cthSeq, final Byte taoSeq, final Byte seqp, final Integer iphSeq, final Short phoSeq) {

		final StringBuffer hql = new StringBuffer(309);
		hql.append(ALIAS_SELECT)
		   .append("  cth.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_ALTA_ADMINISTRATIVA.toString())
		   .append(", cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.DT_INT_ADMINISTRATIVA.toString())
		   
		   
		   // TO_NUMBER(TO_CHAR(ADD_MONTHS(CTH.DT_ALTA_ADMINISTRATIVA,0),'DD')) DIA_DA_ALTA, realizado internamente no vo em getDiaDaAlta
		   // TO_CHAR(LAST_DAY(CTH.DT_ALTA_ADMINISTRATIVA),'DD') QTDE_DIAS_MES,              realizado internamente no vo em getQtdeDiasMes
		   // TO_CHAR(CTH.DT_ALTA_ADMINISTRATIVA,'YYYYMM') ANO_MES_ALTA,          		     realizado internamente no vo em getAnoMesAlta
		   // TO_CHAR(CTH.DT_INT_ADMINISTRATIVA,'YYYYMM') ANO_MES_INT,          		     realizado internamente no vo em getAnoMesInt
		   // TO_CHAR(ADD_MONTHS(CTH.DT_INT_ADMINISTRATIVA,0),'DD') DIA_MES_INT,		     realizado internamente no vo em getDiaMesInt
		   
		   .append(", cth.").append(FatContasHospitalares.Fields.SIA_MSP_SEQ.toString()).append(" as ").append(CursorAtoMedicoAihVO.Fields.MOTIVO.toString())
		   .append(", ama as ").append(CursorAtoMedicoAihVO.Fields.ATO_MEDICO_AIH.toString())
		   .append(", 0 as ").append(CursorAtoMedicoAihVO.Fields.COMP.toString());

		// from
		hql.append(" from ")
		   .append(FatAtoMedicoAih.class.getName()).append(" AS AMA ")
		   .append(", ").append(FatEspelhoAih.class.getName()).append(" AS EAI")
		   .append(", ").append(FatContasHospitalares.class.getName()).append(" AS CTH ");
		
		// where
		hql.append(" where 1=1 ")
		   .append("   and eai.").append(FatEspelhoAih.Fields.CTH_SEQ.toString()).append(" = ")
		   						 .append(" cth.").append(FatContasHospitalares.Fields.SEQ.toString())
		   
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append(" = ")
		   						 .append(" EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.toString())
		   
		   .append("   and cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :PRM_CTH_SEQ")
		   .append("   and eai.").append(FatEspelhoAih.Fields.SEQP.toString()).append(" = :PRM_EAI_SEQP")
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString()).append(" = :PRM_IPH_PHO_SEQ ")
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.IPH_SEQ.toString()).append(" = :PRM_IPH_SEQ ")
		//   .append("   and ama.").append(FatAtoMedicoAih.Fields.SEQP.toString()).append(" = :PRM_SEQP ")
		   .append("   and ama.").append(FatAtoMedicoAih.Fields.TAO_SEQ.toString()).append(" != :PRM_TAO_SEQ ")
		   ;

		// query
		final Query query = createHibernateQuery(hql.toString());
		query.setInteger("PRM_CTH_SEQ", cthSeq);
		query.setInteger("PRM_EAI_SEQP", Integer.valueOf(1));
		//query.setByte("PRM_SEQP", ((byte) (seqp.intValue() + 1)) ); //c_seqp + 1 
		query.setInteger("PRM_IPH_SEQ", iphSeq);
		query.setShort("PRM_IPH_PHO_SEQ", phoSeq);
		query.setByte("PRM_TAO_SEQ", taoSeq);
		
		query.setResultTransformer(Transformers.aliasToBean(CursorAtoMedicoAihVO.class));
		
		final List<CursorAtoMedicoAihVO> result = query.list();

		return result;
	}
	
	public FatItensProcedHospitalar obterPorCodTabelaPhoSeqAtivos(
			final Long codTabela, final Short tabelaFaturPadrao) {
		DetachedCriteria criteria = this.obterCriteriaAtivosPorCodTabela(codTabela, true);
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), tabelaFaturPadrao));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()))
				.add(Projections.property(FatItensProcedHospitalar.Fields.SEQ.toString())));

		final List<Object> result = this.executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		for (final Object res1 : result) {
			return new FatItensProcedHospitalar((Short) ((Object[]) res1)[0], (Integer) ((Object[]) res1)[1]);
		}
		return null;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<GerarArquivoProcedimentoVO> listarTabelaProcedimentoValorEFinancimentoCompetencia(BigDecimal bCpgCphCspCnvCodigo, byte pSusPlanoAmbulatorio, short pTipoGrupoContaSus) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO.toString(), "FCF")
				.createAlias(FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_COMPLEXIDADE.toString(), "FCC")
				.createAlias(FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "VAL")
				.createAlias(FatItensProcedHospitalar.Fields.FAT_ASSOCIACAO_PROCEDIMENTOS.toString(), "VAS", JoinType.LEFT_OUTER_JOIN)
				.createAlias("VAS."+ VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),"PHI", JoinType.LEFT_OUTER_JOIN)
				.createAlias("PHI."+ FatProcedHospInternosPai.Fields.EXAME_MATERIAL.toString(),"EXA", JoinType.LEFT_OUTER_JOIN);
		
		Short pCpgCphCspCnvCodigo =  (bCpgCphCspCnvCodigo != null) ? bCpgCphCspCnvCodigo.shortValue() : null; 

		criteria.add(Restrictions.eq("VAS."+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(),pCpgCphCspCnvCodigo));
		
		criteria.add(Restrictions.eq("VAS."+ VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), pSusPlanoAmbulatorio));
		criteria.add(Restrictions.eq("VAS."+ VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), pTipoGrupoContaSus));
		
		criteria.add(Restrictions.eq("IPH."+ FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.add(Restrictions.isNull("VAL."+ FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("FCF."+ FatCaractFinanciamento.Fields.DESCRICAO.toString()), GerarArquivoProcedimentoVO.Fields.DESCRICAO_FCF.toString());
		pList.add(Projections.property("FCC."+ FatCaractComplexidade.Fields.DESCRICAO.toString()),  GerarArquivoProcedimentoVO.Fields.DESCRICAO_FCC.toString());
		pList.add(Projections.property("IPH."+ FatItensProcedHospitalar.Fields.COD_TABELA.toString()), GerarArquivoProcedimentoVO.Fields.COD_TABELA.toString());
		pList.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), GerarArquivoProcedimentoVO.Fields.DESCRICAO_IPH.toString());
		pList.add(Projections.property("VAS." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()), GerarArquivoProcedimentoVO.Fields.PHI_SEQ.toString());
		pList.add(Projections.property("VAL." + FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.toString()), GerarArquivoProcedimentoVO.Fields.VLR_PROCEDIMENTO.toString());
		pList.add(Projections.property("VAL."+ FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),GerarArquivoProcedimentoVO.Fields.VLR_SERV_PROFISSIONAL.toString());
		pList.add(Projections.property("VAL."+ FatVlrItemProcedHospComps.Fields.VLR_SERV_PROF_AMBULATORIO.toString()),GerarArquivoProcedimentoVO.Fields.VLR_SERV_PROF_AMB.toString());
		pList.add(Projections.property("VAL."+ FatVlrItemProcedHospComps.Fields.VLR_SADT.toString()),GerarArquivoProcedimentoVO.Fields.VLR_SADT.toString());
		pList.add(Projections.property("VAL."+ FatVlrItemProcedHospComps.Fields.VLR_ANESTESIA.toString()), GerarArquivoProcedimentoVO.Fields.VLR_ANESTESIA.toString());
		pList.add(Projections.property("VAS."+ VFatAssociacaoProcedimento.Fields.PHI_DESCRICAO.toString()),GerarArquivoProcedimentoVO.Fields.PHI_DESCRICAO.toString());
		pList.add(Projections.property("PHI."+ FatProcedHospInternosPai.Fields.EMA_EXA_SIGLA.toString()), GerarArquivoProcedimentoVO.Fields.SIGLA_EXAME.toString());
		pList.add(Projections.property("EXA."+ AelExames.Fields.DESCRICAO_USUAL.toString()),GerarArquivoProcedimentoVO.Fields.DESCRICAO_EXAME.toString());

		criteria.setProjection(Projections.distinct(pList));

		criteria.setResultTransformer(Transformers.aliasToBean(GerarArquivoProcedimentoVO.class));

		return executeCriteria(criteria);
	}
	
	private final static String sqlColumn = "lpad(fse3_."+FatServicos.Fields.CODIGO.toString()+",3,'0')||"+
											"lpad(fcs2_."+FatServClassificacoes.Fields.CODIGO.toString()+",3,'0')";
	
	public List<FatProcedServVO> obterFatProcedServVO() throws ApplicationBusinessException {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class,"IPH");
		
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.PROCED_SERVICOS.toString(),"PSC");
		criteria.createAlias("PSC." + FatProcedServicos.Fields.FAT_SERV_CLASSIFICACOES.toString(),"FCS");
		criteria.createAlias("FCS." + FatServClassificacoes.Fields.SERVICO.toString(),"FSE");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IPH."+FatItensProcedHospitalar.Fields.COD_TABELA.toString()), FatProcedServVO.Fields.COD_TABELA.toString())
				.add(Projections.property("IPH."+FatItensProcedHospitalar.Fields.DESCRICAO.toString()), FatProcedServVO.Fields.DESCRICAO.toString())
				
				.add(Projections.property("FSE."+FatServicos.Fields.CODIGO.toString()), FatProcedServVO.Fields.FSE_CODIGO.toString())
				.add(Projections.property("FSE."+FatServicos.Fields.DESCRICAO.toString()), FatProcedServVO.Fields.DESC_SERVICO.toString())
				.add(Projections.sqlGroupProjection(sqlColumn+" AS "+FatProcedServVO.Fields.SERV_CLASS.toString(), null,
													new String[]{FatProcedServVO.Fields.SERV_CLASS.toString()}, 
												    new Type[] {StringType.INSTANCE}), FatProcedServVO.Fields.SERV_CLASS.toString())
				
				.add(Projections.property("FCS."+FatServClassificacoes.Fields.CODIGO.toString()), FatProcedServVO.Fields.FCS_CODIGO.toString())
				.add(Projections.property("FCS."+FatServClassificacoes.Fields.DESCRICAO.toString()), FatProcedServVO.Fields.DESC_CLASSIFICACAO.toString())
				
				.add(Projections.property("PSC."+FatProcedServicos.Fields.DT_COMPETENCIA.toString()), FatProcedServVO.Fields.DT_COMPETENCIA.toString())
				);
		
		criteria.add(Restrictions.eq("PSC."+ FatProcedServicos.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.addOrder(Order.asc("IPH."+FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		criteria.addOrder(OrderBySql.sql(sqlColumn));
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedServVO.class));
		
		return executeCriteria(criteria);
	}
				
    /**
     * ORADB: CURSOR: AGH.FATP_GERA_SEQUENCIA_ATOS_NEW.C_BUSCA_IPH
     * 
     * Coloquei este método aqui pois em VFatAssociacaoProcedimentoDAO esta estourando PMD de classe longa...
     */
    public Integer obterIphSeqPorPhoSeqCodTabela(Short iphPhoSeq, Long codTabela) {
            
            final DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
            criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
            criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), codTabela));
            
            criteria.setProjection(Projections.property(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()));
            
            final List<Integer> iphs = this.executeCriteria(criteria);
            
            if(iphs.isEmpty()){
                    return null;
                    
            } else {
                    return iphs.get(0);
            }
    }
    

	
	public DetachedCriteria getCriteriaProcedimentosConvenioPopularProcedimentoHospitalarInterno(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, ALIAS_IPH);

		criteria.createAlias(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), ALIAS_IPC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString(), ALIAS_CGI);
		criteria.createAlias(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), ALIAS_PHI);
		
		return criteria;
	}
	
	private void getRestrictionsProcedimentosConvenioProcedimentoHospitalarInterno(
			final Date cpeComp, final Short tipoGrupoContaSus, DetachedCriteria criteria) {
		
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), tipoGrupoContaSus));
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.le(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), cpeComp));
		Criterion dtFimCometenciaMaiorIgual = Restrictions.ge(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), cpeComp);
		Criterion dtFimCometenciaMaiorNula = Restrictions.isNull(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString());
		criteria.add(Restrictions.or(dtFimCometenciaMaiorIgual, dtFimCometenciaMaiorNula));
	}
	
	private ProjectionList getProjectionCursorSSMEPHI() {
		return Projections
				.projectionList()
				.add(Projections.property(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.VLR_SERV_HOSPITALAR.toString())
				.add(Projections.property(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.VLR_SERV_PROFISSIONAL.toString())
				.add(Projections.property(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.VLR_SADT.toString())
				.add(Projections.property(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.VLR_ANESTESIA.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.VLR_ANESTESISTA.toString())
				.add(Projections.property(ALIAS_IPC + PONTO + FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.VLR_PROCEDIMENTO.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.COD_TABELA.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.QUANT_DIAS_FATURAMENTO.toString()),
						CursoPopulaProcedimentoHospitalarInternoVO.Fields.QUANT_DIAS_FATURAMENTO.toString());
	}
	
	public List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorSSM(final Integer pciSeq,
			final Date cpeComp, final Short cnvCodigo, final Byte cspSeq,
			final Short tipoGrupoContaSus) {
		
		DetachedCriteria criteria = getCriteriaProcedimentosConvenioPopularProcedimentoHospitalarInterno();

		criteria.add(Restrictions.eq(ALIAS_PHI + PONTO + FatProcedHospInternosPai.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(ALIAS_PHI + PONTO + FatProcedHospInternosPai.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		getRestrictionsProcedimentosConvenioProcedimentoHospitalarInterno(cpeComp, tipoGrupoContaSus, criteria);
		
		criteria.setProjection(getProjectionCursorSSMEPHI());
			
		criteria.setResultTransformer(Transformers.aliasToBean(CursoPopulaProcedimentoHospitalarInternoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorPHI(
			final Integer pciSeq, final Date cpeComp, final Integer phiSeq,
			final Short tipoGrupoContaSus) {
		
		DetachedCriteria criteria = getCriteriaProcedimentosConvenioPopularProcedimentoHospitalarInterno();
		
		criteria.add(Restrictions.eq(ALIAS_PHI + PONTO + FatProcedHospInternosPai.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), phiSeq));
		
		getRestrictionsProcedimentosConvenioProcedimentoHospitalarInterno(cpeComp, tipoGrupoContaSus, criteria);
		
		criteria.setProjection(getProjectionCursorSSMEPHI());
		
		criteria.setResultTransformer(Transformers.aliasToBean(CursoPopulaProcedimentoHospitalarInternoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<FatItensProcedHospitalar> pesquisarPorTabelaOuItemOuProcedimentoOuDescricao(Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		
		String strParametro = (String) objPesquisa;
		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro) && CoreUtil.isNumeroShort(strParametro)) {
			
			Disjunction disjunction = Restrictions.disjunction();

			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.SEQ.toString(), Integer.valueOf(strParametro)));
			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), Short.valueOf(strParametro)));
			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), Long.valueOf(strParametro)));
			disjunction.add(Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			criteria.add(disjunction);
			
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria, 0, 100, null);
	}
	
	public Long pesquisarPorTabelaOuItemOuProcedimentoOuDescricaoCount(Object objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		
		String strParametro = (String) objPesquisa;
		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro) && CoreUtil.isNumeroShort(strParametro)) {
			
			Disjunction disjunction = Restrictions.disjunction();

			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.SEQ.toString(), Integer.valueOf(strParametro)));
			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), Short.valueOf(strParametro)));
			disjunction.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), Long.valueOf(strParametro)));
			disjunction.add(Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			criteria.add(disjunction);
			
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
		}

		return executeCriteriaCount(criteria);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ItensProcedHosp
	 * filtrando pela descricao ou pelo codigo, para uma determinada tabela
	 * (phoSeq). Ordenando por Pho_seq, seq e cod_tabela.
	 * 
	 * @param objPesquisa
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrder(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.PHO_SEQ
				.toString()));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.SEQ
				.toString()));
		criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.COD_TABELA
				.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Metodo de count utilizado em suggestionBox para pesquisar por
	 * ItensProcedHosp filtrando pela descricao ou pelo codigo, para uma
	 * determinada tabela (phoSeq).
	 * 
	 * @param itensProcedimentosHospitalares
	 * @return
	 */
	public Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrderCount(
			Object objPesquisa, Short phoSeq) {
		DetachedCriteria criteria = this
				.obterCriteriaListarItensProcedHospitalar(objPesquisa);

		criteria.add(Restrictions.eq(
				FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));

		return executeCriteriaCount(criteria);
	}
	
	// ##### 41082 #####
	/**
	 * consulta principal
	 */
	public List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresTransplante(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatItensProcedHospitalar filtro) {
		
		DetachedCriteria criteria = montarQueryPesquisaProcedimentosHospitalares(filtro);
		
		if(StringUtils.isBlank(orderProperty)){
			criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.PHO_SEQ.toString())).addOrder(
					Order.asc(FatItensProcedHospitalar.Fields.SEQ.toString())).addOrder(
							Order.asc(ALIAS_IPT + PONTO + FatItemProcHospTransp.Fields.CODIGO_TRANSPLANTE.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarProcedimentosHospitalaresTransplanteCount(
			FatItensProcedHospitalar filtro) {
		return executeCriteriaCount(montarQueryPesquisaProcedimentosHospitalares(filtro));
	}
	
	private DetachedCriteria montarQueryPesquisaProcedimentosHospitalares(FatItensProcedHospitalar filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		
		criteria.createAlias(FatItensProcedHospitalar.Fields.ITEM_PROC_HOSP_TRANSP.toString(), ALIAS_IPT, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IPT + PONTO + FatItemProcHospTransp.Fields.FAT_TIPO_TRANSPLANTE.toString(), ALIAS_TTR, JoinType.INNER_JOIN);
		criteria.createAlias(FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString(), ALIAS_PRH, JoinType.INNER_JOIN);
		
		inserirFiltros(criteria, filtro);
		
		return criteria;
	}	
	
	private void inserirFiltros(DetachedCriteria criteria, FatItensProcedHospitalar filtro) {
		if(filtro.getItemProcHospTransp() != null && filtro.getItemProcHospTransp().getId() != null && filtro.getItemProcHospTransp().getId().getIphPhoSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_IPT + PONTO + FatItemProcHospTransp.Fields.IPH_PHO_SEQ.toString(), filtro.getItemProcHospTransp().getId().getIphPhoSeq()));
		}
		
		if(filtro.getItemProcHospTransp() != null && filtro.getItemProcHospTransp().getId() != null && filtro.getItemProcHospTransp().getId().getIphSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_IPT + PONTO + FatItemProcHospTransp.Fields.IPH_SEQ.toString(), filtro.getItemProcHospTransp().getId().getIphSeq()));
		}	
		
		if(filtro.getCodTabela() != null){
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), filtro.getCodTabela()));
		}
		
		if(StringUtils.isNotBlank(filtro.getDescricao())){
			criteria.add(Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), replaceCaracterEspecial(filtro.getDescricao()), MatchMode.ANYWHERE));
		}

		if(filtro.getItemProcHospTransp() != null && filtro.getItemProcHospTransp().getFatTipoTransplante() != null 
				&& StringUtils.isNotBlank(filtro.getItemProcHospTransp().getFatTipoTransplante().getCodigo())){
			criteria.add(Restrictions.ilike(ALIAS_IPT + PONTO + FatItemProcHospTransp.Fields.CODIGO_TRANSPLANTE.toString(), 
					filtro.getItemProcHospTransp().getFatTipoTransplante().getCodigo(), MatchMode.START));
		}
	}

	/**
	 * #41082 - consultas do suggestionBox de procedimentos hospitalares
	 * consulta procedimentos com internação para determinada tabela
	 * @param seqTabela
	 * @param filtro
	 * @return List<FatProcedimentosHospitalares>
	 */
	public List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresComInternacao(Short seqTabela,
			String filtro) {
		return executeCriteria(montarQueryPesquisaProcedimentosHospitalares(seqTabela, filtro, true), 0, 100, null, true);
	}
	
	public Long pesquisarProcedimentosHospitalaresComInternacaoCount(Short seqTabela,
			String filtro) {
		return executeCriteriaCount(montarQueryPesquisaProcedimentosHospitalares(seqTabela, filtro, false));
	}

	private DetachedCriteria montarQueryPesquisaProcedimentosHospitalares(Short seqTabela, String filtro, boolean ordem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class);
		
		criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), Boolean.TRUE));
		
		if(seqTabela != null){
			criteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), seqTabela));
		}
		
		if (StringUtils.isNotBlank(filtro) && StringUtils.isNumeric(filtro)) {
			criteria.add(
			Restrictions.or(		
					(Restrictions.eq(FatItensProcedHospitalar.Fields.IPH_SEQ.toString(), Integer.valueOf(filtro))),
					(Restrictions.eq(FatItensProcedHospitalar.Fields.COD_TABELA.toString(), Long.valueOf(filtro)))));
		}else if(StringUtils.isNotBlank(filtro)){
			criteria.add((Restrictions.ilike(FatItensProcedHospitalar.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE)));
			
		}
		
		if(ordem){
			criteria.addOrder(Order.asc(FatItensProcedHospitalar.Fields.DESCRICAO.toString()));
		}
		
		return criteria;
	}	
	
}
