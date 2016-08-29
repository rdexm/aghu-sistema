package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorAtosMedicosVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.model.VRapPessoaServidorId;
import br.gov.mec.aghu.sig.custos.vo.SigValorReceitaVO;
import br.gov.mec.aghu.core.utils.DateUtil;


public class FatAtoMedicoAihDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAtoMedicoAih> {

	private static final String COALESCE_VAL2 = "   Coalesce(Val.";
	private static final String P_CTH_SEQ = "   = :P_CTH_SEQ ";
	private static final String AS2 = "    ) AS ";
	private static final String AND_AMA = "     AND AMA.";
	private static final String END = "      END ";
	private static final String COALESCE_VAL = "    COALESCE(VAL.";
	private static final String COALESCE_AMA = "      COALESCE(AMA.";
	private static final String IPHO = " = ipho.";
	private static final String AGH = "    AGH.";
	private static final String AMA = "    AMA.";
	private static final String ICT = " = ICT.";
	private static final String THEN = " then '";
	private static final String AND = " and ";
	private static final String AND_IPH_SUB2 = "    AND IPH_SUB2.";
	private static final String WHEN_IPHO = "           WHEN IPHO.";
	private static final String WHERE = " where ";
	private static final String FROM = " FROM ";
	private static final String _0 = ",0) + ";
	private static final String STR = " = ";
	private static final String AS = " AS ";
	private static final String AAM = "AAM.";
	private static final long serialVersionUID = -4601134665624927079L;

	public List<FatAtoMedicoAih> listarFatAtoMedicoEspelho(final Integer cthSeq, final int firstResult, final int maxResults, final String orderProperty, final boolean asc) {
		final DetachedCriteria criteria = montaCriteriaListarFatAtoMedicoEspelho(cthSeq);
		
		criteria.createAlias(FatAtoMedicoAih.Fields.TAO.toString(), "TAO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatAtoMedicoAih.Fields.TIV.toString(), "TIV", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()));
		criteria.addOrder(Order.asc(FatAtoMedicoAih.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc(FatAtoMedicoAih.Fields.EAI_SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long listarFatAtoMedicoEspelhoCount(final Integer cthSeq) {
		final DetachedCriteria criteria = montaCriteriaListarFatAtoMedicoEspelho(cthSeq);
		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria montaCriteriaListarFatAtoMedicoEspelho(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		}
		return criteria;
	}
	
	/**
	 * Lista de <code>FatAtoMedicoAih</code> pelo campo <code>EAI_CTH_SEQ</code>
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatAtoMedicoAih> listarPorCth(final Integer cthSeq) {
		final DetachedCriteria criteria = montaCriteriaListarFatAtoMedicoEspelho(cthSeq);
		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorCthOrdenadoPorSeqArqSus(final Integer cthSeq) {
		
		DetachedCriteria result = null;
		
		result = this.montaCriteriaListarFatAtoMedicoEspelho(cthSeq);
		result.addOrder(Order.asc(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()));
		
		return result;		
	}

	public List<FatAtoMedicoAih> listarPorCthOrdenadoPorSeqArqSus(final Integer cthSeq) {
		
		List<FatAtoMedicoAih> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorCthOrdenadoPorSeqArqSus(cthSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	protected DetachedCriteria obterCriteriaPorCthIph(
            Integer eaiCthSeq, 
			Short iphPhoSeq,
			Integer iphSeq) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_SEQ.toString(), iphSeq));
		
		return result;
	}
	
	public List<FatAtoMedicoAih> obterListaPorCthIph(
            Integer eaiCthSeq, 
			Short iphPhoSeq,
			Integer iphSeq) {
		
		List<FatAtoMedicoAih> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorCthIph(eaiCthSeq, iphPhoSeq, iphSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}

	protected DetachedCriteria obterCriteriaPorEai(Integer eaiSeq, Integer eaiCthSeq) {
		
		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatAtoMedicoAih.class);		
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_SEQ.toString(), eaiSeq));
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		
		return result;		
	}
	
	public Long obterQtdPorEai(Integer eaiSeq, Integer eaiCthSeq) {
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorEai(eaiSeq, eaiCthSeq);
		return this.executeCriteriaCount(criteria);
	}
	
	public Long countPorCthCodSusTaoSeq(Long codSus, Integer cthSeq, Byte...taoSeqs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString(), codSus));
		criteria.createAlias(FatAtoMedicoAih.Fields.TAO.toString(), FatAtoMedicoAih.Fields.TAO.toString());
		criteria.add(Restrictions.in(FatAtoMedicoAih.Fields.TAO_SEQ.toString(), taoSeqs));
		return executeCriteriaCount(criteria);
	}

	public Byte buscaProximaSeq(final Integer cthSeq, final Integer eaiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.setProjection(Projections.max(FatAtoMedicoAih.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_SEQ.toString(), eaiSeq));
		Byte seqp = (Byte) this.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}

		return seqp;
	}

	protected DetachedCriteria obterCriteriaPorIphCodSusCthSeq(final Long iphCodSus, final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString(), iphCodSus));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		return criteria;
	}

	public List<FatAtoMedicoAih> listarPorIphCodSusCthSeq(
			final Long iphCodSus,
			final Integer cthSeq) {
		
		List<FatAtoMedicoAih> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorIphCodSusCthSeq(iphCodSus, cthSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}


	/**
	 * 
	 * @param phoSeq
	 * @param seq
	 * @param iphCodSus
	 * @param cthSeq
	 * @param sgrGrpSeq
	 * @param situacao
	 * @return
	 */
	public List<Integer> listarPorIphCodSusCthGrpSit(final Short phoSeq, final Integer seq, final DominioSituacao situacao, final Integer cthSeq, final Short sgrGrpSeq) {
		final String formaOrganizacao = FatAtoMedicoAih.Fields.IPH.toString() + "." + FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString();
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.createAlias(FatAtoMedicoAih.Fields.IPH.toString(), FatAtoMedicoAih.Fields.IPH.toString());
		criteria.createAlias(formaOrganizacao, formaOrganizacao);	
		
		criteria.add(Restrictions.eq(formaOrganizacao + "." + FatFormaOrganizacao.Fields.ID_SGR_GRP_SEQ.toString(), sgrGrpSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH.toString() + "." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH.toString() + "." + FatItensProcedHospitalar.Fields.SEQ.toString(), seq));
		// criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString(), iphCodSus));	// MARINA 18/08/2011
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH.toString() + "." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), situacao));


		criteria.setProjection(Projections.property(FatAtoMedicoAih.Fields.EAI_SEQ.toString()));
		
		return this.executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorIphCodSusCthSeqEaiSeq(
			final Long iphCodSus,
			final Integer cthSeq,
			final Integer eaiSeq) {
	
		DetachedCriteria result = null;
		
		result = this.obterCriteriaPorIphCodSusCthSeq(iphCodSus, cthSeq);
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_SEQ.toString(), eaiSeq));
		
		return result;
	}
	
	public List<FatAtoMedicoAih> listarPorIphCodSusCthSeqEaiSeq(
			final Long iphCodSus,
			final Integer cthSeq,
			final Integer eaiSeq) {
		
		List<FatAtoMedicoAih> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorIphCodSusCthSeqEaiSeq(iphCodSus, cthSeq, eaiSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	public VRapPessoaServidor obterPessoasFisicasCPF(Long cpf) {
		 final DetachedCriteria criteria =
		 DetachedCriteria.forClass(VRapPessoaServidor.class);
		 criteria.setProjection(Projections.projectionList().add(Projections.property(VRapPessoaServidor.Fields.NOME.toString())).add(Projections.property(VRapPessoaServidor.Fields.MATRICULA.toString())).add(Projections.property(VRapPessoaServidor.Fields.VINCODIGO.toString())));
		 criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.CPF.toString(),
		 cpf));
		 criteria.add(Restrictions.eq(VRapPessoaServidor.Fields.SITUACAO.toString(),
		 DominioSituacao.A.toString()));
//		 criteria.addOrder(Order.asc(VRapPessoaServidor.Fields.SITUACAO.toString()));
		 List<Object> result = executeCriteria(criteria);
		 if(result == null || result.isEmpty()){
		 return null;
		 }
		 Object [] ret = (Object[]) result.get(0);
		 return new VRapPessoaServidor(new VRapPessoaServidorId(0, ((Short)ret[2]).shortValue(), ((Integer)ret[1]).intValue(), null, ret[0].toString(), null, null, null));
	}
	
		protected DetachedCriteria obterCriteriaPorCthSeqIphFogSgrGrpSeq(Integer cthSeq, Short fogSgrGrpSeq) {
		
		DetachedCriteria result = null;
		
		// and EAI_CTH_SEQ = P_CTH_SEQ
		result = this.montaCriteriaListarFatAtoMedicoEspelho(cthSeq);
		// iph.fog_sgr_grp_seq = AGHC_OBTEM_PARAMETRO('P_GRUPO_OPM') 
		result.createAlias(FatAtoMedicoAih.Fields.IPH.toString(), FatAtoMedicoAih.Fields.IPH.toString());
		result.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH.toString() + "." + FatItensProcedHospitalar.Fields.SUBGRUPO_GRP_SEQ.toString(), fogSgrGrpSeq));
		
		return result;
	}
	
	protected DetachedCriteria obterCriteriaPorCthSeqIphFogSgrGrpSeqOrdenadoPorSeqp(Integer cthSeq, Short fogSgrGrpSeq) {
		
		DetachedCriteria result = null;
		
		result = this.obterCriteriaPorCthSeqIphFogSgrGrpSeq(cthSeq, fogSgrGrpSeq);
		// order by SEQP;  		
		result.addOrder(Order.asc(FatAtoMedicoAih.Fields.SEQP.toString()));
		
		return result;
	}

	public List<FatAtoMedicoAih> listarPorCthSeqIphFogSgrGrpSeqOrdenadoPorSeqp(Integer cthSeq, Short fogSgrGrpSeq) {
		
		List<FatAtoMedicoAih> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorCthSeqIphFogSgrGrpSeqOrdenadoPorSeqp(cthSeq, fogSgrGrpSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {
		Query query = createQuery("delete " + FatAtoMedicoAih.class.getName() + 
				 WHERE + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameter("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatAtoMedicoAih relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq){
		Query query = createQuery("delete " + FatAtoMedicoAih.class.getName() + 
												 	 WHERE + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatAtoMedicoAih relacionados a uma conta hospitalar e data de prévia não nula.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerRnCthcAtuEncPrv(Integer cthSeq){
		Query query = createQuery("delete " + FatAtoMedicoAih.class.getName() + 
												 	 WHERE + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString() + " = :cthSeq " +
												 	 "   and " + FatAtoMedicoAih.Fields.DATA_PREVIA.toString() + " is not null ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	public Integer atualizarFatAtoMedicoAihs(final String hql){
		Query query = createQuery(hql);
		return query.executeUpdate();
	}

	/**
	 * <p>
	 * ORADB: <code>FATP_GERA_SEQUENCIA_ATOS</code>
	 * ORADB: <code>FATC_BUSCA_PROCED_PR_CTA</code>
	 * ORADB: <code>FATC_BUSCA_INSTR_REG</code>
	 * ORADB: <code>FATC_BUSCA_VALORES_CTH</code>
	 * </p>
	 * 
	 * Detalhes
import org.hibernate.type.StringType;

import org.hibernate.type.IntegerType;

import org.hibernate.type.LongType;

import org.hibernate.type.ByteType;

import org.hibernate.type.DoubleType;
 importantes:
	 * 
	 *  ********************************************************************************************************************
	 * - ***Sempre que se alterar EXECUTAR O TESTE DE DAO: FatAtoMedicoAihDAOTest para garantir o funcionamento do cursor***
	 *  ********************************************************************************************************************
	 * 
	 * - Retorna apenas a chave PK da tabela (FatAtosMedicos) e colunas necessárias para ordenação;
	 * - Ordenação alterou-se para nome das colunas, uma vez que indices nao bateriam com remocao de colunas desnecessárias;
	 * - LPad realizado em VO, não em sql;
	 * - Uitilizado distinct para a função FATC_BUSCA_PROCED_PR_CTA, pois na implementação da mesma, estava retornando apenas a primeira coluna
	 *   mesmo que sua execução resulta-se em mais de uma.
	 * - Constantes definidas em VO;
	 * @param codRegistro 
	 * 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<CursorAtosMedicosVO> obterCursorAtosMedicosVOs( final Integer cthSeq,     final Short iphPhoSeq, 
															    final String indSituacao, final String  codRegistro, Boolean pSeqOnco){
		boolean isOracle = isOracle();
		final StringBuilder sql = new StringBuilder(3100);

		final StringBuilder fatc_busca_proced_pr_cta_fields = new StringBuilder(421);
		fatc_busca_proced_pr_cta_fields.append(" LPAD(LTRIM(TO_CHAR(VFATC.IPH_R_COD_TABELA, '9999999999')), 10, '0') || LPAD(LTRIM(TO_CHAR(CAST(COALESCE(AMA.COMPETENCIA_UTI, '0') AS INTEGER), '999999')), 6, '0') || LPAD(LTRIM(TO_CHAR(CAST(COALESCE(AMA.").append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name()).append(", '0') AS INTEGER), '999999')), 6, '0') || LPAD(LTRIM(TO_CHAR(COALESCE(AMA.SEQP_VINCULADO, VFATC.AMA_SEQP), '99')), 2, '0') ||  LPAD(LTRIM(TO_CHAR(COALESCE(AMA.")
									   .append(FatAtoMedicoAih.Fields.SEQP_VINCULADO.name()).append(", VFATC.AMA_SEQP), '99')), 2, '0') || VFATC.IND_PROTESE ");
		
		final StringBuilder fatc_busca_proced_pr_cta = new StringBuilder(469);
		fatc_busca_proced_pr_cta.append(FROM)
								.append("        AGH.V_FAT_BUSCA_PROCED_PR_CTA VFATC  WHERE  1=1 AND VFATC.ICT_IPH_PHO_SEQ_COMPATIBILIZA = AMA.")
								.append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name())
		 					    .append("    AND VFATC.ICT_IPH_SEQ_COMPATIBILIZA = AMA.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name());
		 					    
		if(isOracle){
			fatc_busca_proced_pr_cta.append(" AND TRUNC(CTH.DT_ALTA_ADMINISTRATIVA) BETWEEN TRUNC(VFATC.CPX_DT_INICIO_VALIDADE) AND TRUNC(NVL(VFATC.CPX_DT_FIM_VALIDADE,SYSDATE)) ");
			
		} else {
			// and   trunc(p_alta) between trunc(cpx.dt_inicio_validade) and trunc(nvl(cpx.dt_fim_validade,sysdate))  
			fatc_busca_proced_pr_cta.append(" AND DATE_TRUNC('DAY', CTH.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.name())
									.append(") BETWEEN DATE_TRUNC('DAY', VFATC.CPX_DT_INICIO_VALIDADE) AND DATE_TRUNC('DAY', COALESCE(VFATC.CPX_DT_FIM_VALIDADE,NOW())) ");
		}
		 					    
		fatc_busca_proced_pr_cta.append(" AND VFATC.AMA_EAI_CTH_SEQ     = AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
								.append(" AND VFATC.AMA_SEQP            = COALESCE(AMA.").append(FatAtoMedicoAih.Fields.SEQP_VINCULADO.name())
								.append(",VFATC.AMA_SEQP) AND VFATC.AMA_COMPETENCIA_UTI = COALESCE(AMA.").append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name()).append(",VFATC.AMA_COMPETENCIA_UTI) "); 

		if(isOracle){
			fatc_busca_proced_pr_cta.append(" AND ROWNUM <=1 ");
		} else {
			fatc_busca_proced_pr_cta.append(" LIMIT 1 ");
		}
		
		// Utilizado para montar coluna VALOR_PRINC
		final StringBuilder fatc_busca_instr_reg = new StringBuilder(235);
		fatc_busca_instr_reg.append("SELECT FPRIN_SUB3.").append(FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.name())
							.append(" FROM  AGH.").append(FatProcedimentoRegistro.class.getAnnotation(Table.class).name()).append(" FPRIN_SUB3 ")
						    .append(" WHERE 1=1 AND FPRIN_SUB3.")
						    .append(FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.name()).append("  = IPH_SUB2.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
							.append(" AND FPRIN_SUB3.").append(FatProcedimentoRegistro.Fields.IPH_SEQ.name()).append("      = IPH_SUB2.").append(FatItensProcedHospitalar.Fields.SEQ.name())
							.append(" AND FPRIN_SUB3.").append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" = :P_FPRIN_COD_REGISTRO ");

		// Utilizado para montar coluna VALOR_PRINC
		final StringBuilder fatc_busca_valores_cth = new StringBuilder(900);
		
		
		fatc_busca_valores_cth.append(" SELECT " )
							  .append("   DISTINCT ( COALESCE( (AAM_SUB2.").append(FatAtoMedicoAih.Fields.QUANTIDADE.name()).append(" * (IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.name()).append(" + IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.name()).append(") ),0) ) AS VALOR ")
							  .append("  FROM ")
							  .append(AGH).append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH_SUB2, ")
							  .append(AGH).append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AAM_SUB2, ")
							  .append(AGH).append(FatVlrItemProcedHospComps.class.getAnnotation(Table.class).name()).append(" IPC_SUB2 ")
							  .append("  WHERE  1=1 ")
							  .append(AND_IPH_SUB2).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = AAM_SUB2.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name())
							  .append(AND_IPH_SUB2).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = AAM_SUB2.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name())
							  .append(AND_IPH_SUB2).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.name())
							  .append(AND_IPH_SUB2).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.IPH_SEQ.name())
							  
																// ama.iph_cod_sus --> CONSULTA PRINCIPAL
							  .append("    AND AAM_SUB2.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				
							  .append(AND_IPH_SUB2).append(FatItensProcedHospitalar.Fields.IND_COBRANCA_DIARIAS.name()).append(" = :PRM_IND_COBRANCA_DIARIAS ")
							  .append("    AND AAM_SUB2.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :PRM_CTH ")
							  .append("    AND 'S' = (CASE WHEN ( ").append(fatc_busca_instr_reg.toString()).append(") IS NOT NULL THEN 'S' ELSE 'N' END) ") 

							  			 // AMA.COMPETENCIA_UTI --> CONSULTA PRINCIPAL
							  .append(" AND AMA.").append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name()).append(" BETWEEN TO_CHAR(IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.name()).append(", 'YYYYMM') AND ")
							  .append("TO_CHAR(COALESCE(IPC_SUB2.").append(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.name()).append(", ")
							  											.append(isOracle ? " SYSDATE " : " NOW() ").append("), 'YYYYMM') ");
							if(isOracle){
								fatc_busca_valores_cth.append(" AND ROWNUM <=1 ");
							} else {
								fatc_busca_valores_cth.append(" LIMIT 1 ");
							}

		
		sql
		 .append(" SELECT ") 
		 .append(AMA).append(FatAtoMedicoAih.Fields.EAI_SEQ.name()).append(AS).append(CursorAtosMedicosVO.Fields.EAI_SEQ.toString()).append(", ")
		 .append(AMA).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(AS).append(CursorAtosMedicosVO.Fields.EAI_CTH_SEQ.toString())
		 .append(", AMA.").append(FatAtoMedicoAih.Fields.SEQP.name()).append(AS).append(CursorAtosMedicosVO.Fields.SEQ_P.toString()).append(", ")
		 .append(AMA).append(FatAtoMedicoAih.Fields.TIV_SEQ.name()).append(AS).append(CursorAtosMedicosVO.Fields.TIV_SEQ.toString()).append(", ")
		 .append(AMA).append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(AS).append(CursorAtosMedicosVO.Fields.TAO_SEQ.toString())
		 .append(", IPH.DESCRICAO   AS ").append(CursorAtosMedicosVO.Fields.AM_PPH_DESCRICAO.toString())
		 .append(", ( CASE WHEN(SELECT ").append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" FROM AGH.").append(FatProcedimentoRegistro.class.getAnnotation(Table.class).name()).append(WHERE).append(FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.IPH_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" = '").append(CursorAtosMedicosVO.Registro.PRINCIPAL1.getIndice()).append("') is not null then '").append(CursorAtosMedicosVO.Registro.PRINCIPAL1.getDescricao())
		 .append("' WHEN(SELECT ").append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" FROM AGH.").append(FatProcedimentoRegistro.class.getAnnotation(Table.class).name()).append(WHERE).append(FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.IPH_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" = '").append(CursorAtosMedicosVO.Registro.ESPECIAL2.getIndice()).append("') is not null then '").append(CursorAtosMedicosVO.Registro.ESPECIAL2.getDescricao())
		 .append("' WHEN(SELECT ").append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" FROM AGH.").append(FatProcedimentoRegistro.class.getAnnotation(Table.class).name()).append(WHERE).append(FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.IPH_SEQ.name()).append(IPHO).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(AND).append(FatProcedimentoRegistro.Fields.COD_REGISTRO.name()).append(" = '").append(CursorAtosMedicosVO.Registro.SECUNDARIO3.getIndice()).append("') is null then '").append(CursorAtosMedicosVO.Registro.SECUNDARIO3.getDescricao())
		 .append("' ELSE '").append(CursorAtosMedicosVO.Registro.SECUNDARIO4.getDescricao()).append("' ") 
		 .append(END)
		 .append(AS2).append(CursorAtosMedicosVO.Fields.REGISTRO.toString()).append(",  ( CASE WHEN IPHO.").append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Financ.FAEC1.getIndice()).append(THEN).append(CursorAtosMedicosVO.Financ.FAEC1.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Financ.ALTA2.getIndice()).append(THEN).append(CursorAtosMedicosVO.Financ.ALTA2.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Financ.MEDIA3.getIndice()).append(THEN).append(CursorAtosMedicosVO.Financ.MEDIA3.getDescricao())
		 .append("' ELSE '").append(CursorAtosMedicosVO.Financ.XXXXX4.getDescricao()).append("' ")
		 .append(END)
		 .append(AS2).append(CursorAtosMedicosVO.Fields.FINANC.toString()).append(", ( CASE WHEN IPHO.").append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Complexidade.ALTA.getIndice()).append(THEN).append(CursorAtosMedicosVO.Complexidade.ALTA.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Complexidade.MEDIA5.getIndice()).append(THEN).append(CursorAtosMedicosVO.Complexidade.MEDIA5.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Complexidade.MEDIA6.getIndice()).append(THEN).append(CursorAtosMedicosVO.Complexidade.MEDIA6.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Complexidade.MEDIA7.getIndice()).append(THEN).append(CursorAtosMedicosVO.Complexidade.MEDIA7.getDescricao()).append("' ")
		 .append(WHEN_IPHO).append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(STR).append(CursorAtosMedicosVO.Complexidade.MEDIA8.getIndice()).append(THEN).append(CursorAtosMedicosVO.Complexidade.MEDIA8.getDescricao())
		 .append("' ELSE '").append(CursorAtosMedicosVO.Complexidade.XXXXXX.getDescricao()).append("' ")
		 .append(END)             
		 .append(AS2).append(CursorAtosMedicosVO.Fields.COMPLEXIDADE.toString())
		      
 		  // Marina 26/06/2012 - Chamado - 73613
		  // decode((nvl(AMA.VALOR_SERV_HOSP,0) + nvl(AMA.VALOR_SERV_PROF,0) + nvl(AMA.VALOR_SADT,0) + nvl(AMA.VALOR_PROCEDIMENTO,0)),0, 
		  // 		AGH.FATC_BUSCA_VALORES_CTH(c_cth_seq, ama.competencia_uti, ama.iph_cod_sus, (nvl(val.VLR_SERV_HOSPitalar,0) + nvl(val.VLR_SERV_PROFissional,0) + nvl(val.VLR_SADT,0) + nvl(val.VLR_PROCEDIMENTO,0))), 
		  //        (nvl(val.VLR_SERV_HOSPitalar,0) + nvl(val.VLR_SERV_PROFissional,0) + nvl(val.VLR_SADT,0) + nvl(val.VLR_PROCEDIMENTO,0))
		  //       ) valor_princ ,
		 
		 .append(", ( CASE WHEN  ( COALESCE(AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name()).append(_0)
		 				.append("  COALESCE(AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name()).append(_0)
		 				.append("  COALESCE(AMA.").append(FatAtoMedicoAih.Fields.VALOR_SADT.name()).append(_0)
		 				.append("  COALESCE(AMA.").append(FatAtoMedicoAih.Fields.VALOR_PROCEDIMENTO.name())
		 				.append(",0)) = 0 THEN  ( CASE WHEN (").append(fatc_busca_valores_cth.toString())
		 					.append(") > 0 THEN (").append(fatc_busca_valores_cth.toString()).append(") ELSE ( ")
										.append(COALESCE_VAL2).append(FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.name()).append(",0)   + ")
										.append(COALESCE_VAL2).append(FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.name()).append(_0)
										.append(COALESCE_VAL2).append(FatVlrItemProcedHospComps.Fields.VLR_SADT.name()).append(",0)              + ")
										.append(COALESCE_VAL2).append(FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.name())
									.append(",0) ) END ) ELSE  (")
					.append(COALESCE_VAL).append(FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.name()).append(",0)   +")
					.append(COALESCE_VAL).append(FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.name()).append(",0) +")
					.append(COALESCE_VAL).append(FatVlrItemProcedHospComps.Fields.VLR_SADT.name()).append(",0)              +")
					.append(COALESCE_VAL).append(FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.name())
					.append(",0)   ) END  ) AS ").append(CursorAtosMedicosVO.Fields.VALOR_PRINC.toString())
		 
		 /* AGH.fatc_busca_proced_pr_cta_new( AMA.EAI_SEQ,     AMA.EAI_CTH_SEQ,
		  * 								  AMA.IPH_PHO_SEQ, AMA.IPH_SEQ,
		  * 								  AMA.IPH_COD_SUS, AMA.COMPETENCIA_UTI,
		  * 								  trunc(CTH.DT_ALTA_ADMINISTRATIVA),
		  * 								  ama.seqp_vinculado, ama.seqp,
		  * 								  AMA.TAO_SEQ
		  * 								) as sequencia, */
		 .append(", (  CASE WHEN ( SELECT VFATC.IND_PROTESE ").append( fatc_busca_proced_pr_cta.toString() ).append(" ) IS NULL THEN ")
		 
		 // v_retorno := lpad(p_cod_tabela,10,'0')||lpad(nvl(p_competencia,0),6,'0')||lpad(nvl(p_seqp_vinculado,p_seqp),2,'0')||v_tao_seq;
		 .append("        ( LPAD(LTRIM(TO_CHAR(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(",'9999999999')),10,'0') || ")
		 		.append("  LPAD(LTRIM(TO_CHAR(CAST(COALESCE(AMA.").append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name()).append(",'0') AS INTEGER),'999999')),6,'0') || ")
		 		.append("  LPAD(LTRIM(TO_CHAR(COALESCE(AMA.").append(FatAtoMedicoAih.Fields.SEQP_VINCULADO.name()).append(",AMA.").append(FatAtoMedicoAih.Fields.SEQP.name()).append("),'99')),2,'0') || ")
		 		.append("  ( CASE WHEN AMA.").append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(" = 6 THEN 2 ELSE 1 END )") // if p_tao_seq = 6 then v_tao_seq := 2; else v_tao_seq := 1; end if;
		 		.append(") ")
		 .append("  ELSE ( SELECT ").append(fatc_busca_proced_pr_cta_fields).append(fatc_busca_proced_pr_cta.toString()).append(" ) END ")	       
		 .append(" ) AS ").append(CursorAtosMedicosVO.Fields.SEQUENCIA.toString()).append(", ")
		 
		 // decode(ama.tao_seq,1,1,6,2,3) ato_cir,
		 .append("    (  ")
		 .append("      CASE WHEN AMA.").append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(" = 1 THEN 1 ")
		 .append("           WHEN AMA.").append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(" = 6 THEN 2 ") 
		 .append("           ELSE 3 ")
		 .append(END)
		 .append(AS2).append(CursorAtosMedicosVO.Fields.ATO_CIR.toString()).append(", ")
		    
		 // nvl(ama.valor_serv_hosp,0) + nvl(ama.valor_serv_prof,0) + nvl(ama.valor_sadt,0) + nvl(ama.valor_procedimento,0) valor_total      
		 .append("    (  ")
		 .append(COALESCE_AMA).append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name()).append(", 0) + ")
		 .append(COALESCE_AMA).append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name()).append(", 0) + ")
		 .append(COALESCE_AMA).append(FatAtoMedicoAih.Fields.VALOR_SADT.name()).append(", 0)      + ")
		 .append(COALESCE_AMA).append(FatAtoMedicoAih.Fields.VALOR_PROCEDIMENTO.name()).append(", 0) ")
		 .append(AS2).append(CursorAtosMedicosVO.Fields.VALOR_TOTAL.toString()).append(",  ")

		 .append("    CTH.").append(FatContasHospitalares.Fields.CSP_CNV_CODIGO.name()).append(AS).append(CursorAtosMedicosVO.Fields.CSP_CNV_CODIGO.toString()).append(",  ")
		 .append("    CTH.").append(FatContasHospitalares.Fields.CSP_SEQ.name()).append(AS).append(CursorAtosMedicosVO.Fields.CSP_SEQ.toString())
		 
		 .append(FROM)
		 .append(AGH).append(FatVlrItemProcedHospComps.class.getAnnotation(Table.class).name()).append(" VAL, ")   
		 .append(AGH).append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPHO, ")
		 .append(AGH).append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH , ")
		 .append(AGH).append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AMA,  ")
		 .append(AGH).append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH  ")

		 .append(" WHERE 1=1  ")
		 .append("    AND CTH.").append(FatContasHospitalares.Fields.SEQ.name()).append(" = :PRM_CTH ")
		 .append("    AND AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = CTH.").append(FatContasHospitalares.Fields.SEQ.name())
		 .append("    AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name())
		 .append("    AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = AMA.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name())
		 .append("    AND IPH.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO  ")
		 .append("    AND AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" <> :PRM_IPH_PHO_SEQ ") // nao imprime sadt
		    
		 /* and ipho.cod_tabela = to_number(substr(fatc_busca_proced_pr_cta_new( AMA.EAI_SEQ,     AMA.EAI_CTH_SEQ,
		  																		 AMA.IPH_PHO_SEQ, AMA.IPH_SEQ,
		  																		 AMA.IPH_COD_SUS, AMA.COMPETENCIA_UTI,
		  																		 trunc(CTH.DT_ALTA_ADMINISTRATIVA),
		  																		 ama.seqp_vinculado, ama.seqp,
		  																		 AMA.TAO_SEQ
		  																	   ),1,10
		  									      )
		  								   ) 
       */
		 
		 .append(" AND IPHO.").append(FatItensProcedHospitalar.Fields.COD_TABELA.name()).append(" = ( CASE WHEN ( SELECT VFATC.IPH_R_COD_TABELA ")
		                          .append(     fatc_busca_proced_pr_cta.toString() ).append(" ) IS NULL THEN ")
		                          .append(AMA).append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
		                          .append(" ELSE ( SELECT VFATC.IPH_R_COD_TABELA ").append(fatc_busca_proced_pr_cta.toString())
		                          .append(" ) END  ")	       
		                       .append(" ) ")
		 
		 .append(" AND IPHO.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO ")
		 .append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.name()).append(" = IPHO.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
		 .append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.IPH_SEQ.name()).append(" = IPHO.").append(FatItensProcedHospitalar.Fields.SEQ.name())
		 .append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.name()).append(" IS NULL ")
		 
		  // order by 20,21,22,23 desc,24,25
		 // Ney 05/09/2011 (Não estava classificando descendente pelo valor dos materiais compativeis)
		 .append(" ORDER BY ");
		
		if(Boolean.TRUE.equals(pSeqOnco)){
			sql.append(CursorAtosMedicosVO.Fields.SEQ_P.toString()).append(", ");
		}
		
		sql					  .append(CursorAtosMedicosVO.Fields.REGISTRO.toString()).append(", ")
		                      .append(CursorAtosMedicosVO.Fields.COMPLEXIDADE.toString()).append(", ") 
		                      .append(CursorAtosMedicosVO.Fields.VALOR_PRINC.toString()).append(" DESC, ")
		                      .append(CursorAtosMedicosVO.Fields.SEQUENCIA.toString()).append(", ")
		                      .append(CursorAtosMedicosVO.Fields.VALOR_TOTAL.toString()).append(" DESC, ")
		                      .append(CursorAtosMedicosVO.Fields.TAO_SEQ.toString());
		 
		final SQLQuery query = createSQLQuery(sql.toString());

		query.setParameter("PRM_CTH", cthSeq);
		query.setParameter("PRM_IPH_PHO_SEQ", iphPhoSeq);
		query.setParameter("P_FPRIN_COD_REGISTRO", codRegistro);
		query.setParameter("PRM_IND_COBRANCA_DIARIAS", DominioCobrancaDiaria.S.toString());
		query.setParameter("PRM_IND_SITUACAO", indSituacao);
		
		
		final List<CursorAtosMedicosVO> result = query.addScalar(CursorAtosMedicosVO.Fields.EAI_SEQ.toString(), IntegerType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.EAI_CTH_SEQ.toString(), IntegerType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.SEQ_P.toString(), ByteType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.TIV_SEQ.toString(), ByteType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.TAO_SEQ.toString(), ByteType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.AM_PPH_DESCRICAO.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.REGISTRO.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.FINANC.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.COMPLEXIDADE.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.VALOR_PRINC.toString(), DoubleType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.SEQUENCIA.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.ATO_CIR.toString(), IntegerType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.VALOR_TOTAL.toString(), DoubleType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.CSP_CNV_CODIGO.toString(), ShortType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.CSP_SEQ.toString(), ByteType.INSTANCE)

												      .setResultTransformer(Transformers.aliasToBean(CursorAtosMedicosVO.class)).list();
		
		return result;
	}

	/**
	 * DAOTest: FatAtoMedicoAihDAOTest.obterQtdMaximaExecucao (Retestar qd alterado) eSchweigert 17/09/2012
	 */
	public Long obterQtdMaximaExecucao( final Short phoSeq, final Integer iphSeq, final Integer eaiCthSeq, 
			   							final Byte taoSeq, final String competenciaUTI, final Date competencia){

		final StringBuilder sql = new StringBuilder(790);

		sql.append("SELECT ")
		   .append("    SUM(AMA.").append(FatAtoMedicoAih.Fields.QUANTIDADE.name()).append(" * ICT.").append(FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.name()).append(") AS QUANTIDADE_MAXIMA ")
		   .append(FROM)
   		   .append("     AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AMA, ")
   		   .append("     AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name()).append(" ICT, ")
   		   .append("     AGH.").append(FatCompetenciaCompatibilid.class.getAnnotation(Table.class).name()).append(" CPX ")
		   .append(" WHERE 1=1 ")
		   .append("     AND ICT.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name()).append(" = :PRM_IPH_PHO_SEQ ")
		   .append("     AND ICT.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name()).append("     = :PRM_IPH_SEQ ")
		   .append("     AND ICT.").append(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.name()).append(" IN (:PRM_IND_COMPAT_EXCLUS) ")
		   .append("     AND CPX.").append(FatCompetenciaCompatibilid.Fields.IPH_PHO_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name())
		   .append("     AND CPX.").append(FatCompetenciaCompatibilid.Fields.IPH_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_SEQ.name())
		   .append("     AND CPX.").append(FatCompetenciaCompatibilid.Fields.SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.CPX_SEQ.name())
		   .append(AND_AMA).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :PRM_CTH_SEQ ")
		   .append(AND_AMA).append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name())
		   .append(AND_AMA).append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_SEQ.name())
		   .append(AND_AMA).append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(" = :PRM_TAO_SEQ");
			
		if(competenciaUTI != null){
			sql.append(" AND AMA.").append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name()).append(" = :PRM_COMPETENCIA_UTI");
		}
		
		if(isOracle()){
			// AND TRUNC(:PRM_COMPETENCIA) BETWEEN TRUNC(CPX.DT_INICIO_VALIDADE) AND TRUNC(NVL(CPX.DT_FIM_VALIDADE,SYSDATE))
			sql.append(" AND :PRM_COMPETENCIA BETWEEN TRUNC(CPX.").append(FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name()).append(") AND TRUNC(NVL(CPX.").append(FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()).append(",SYSDATE)) ");
		} else {
			sql.append(" AND :PRM_COMPETENCIA BETWEEN DATE_TRUNC('day', CPX.").append(FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name()).append(") AND DATE_TRUNC('day', (COALESCE(CPX.").append(FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()).append(",now()))) ");			
		}
	   
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setShort("PRM_IPH_PHO_SEQ", phoSeq);
		query.setInteger("PRM_IPH_SEQ", iphSeq);
		query.setInteger("PRM_CTH_SEQ", eaiCthSeq);
		query.setByte("PRM_TAO_SEQ", taoSeq);
		query.setDate("PRM_COMPETENCIA", DateUtil.truncaData(competencia));
		query.setParameterList("PRM_IND_COMPAT_EXCLUS", new String[] {DominioIndCompatExclus.PCI.toString(),DominioIndCompatExclus.ICP.toString() });
		
		if(competenciaUTI != null){
			query.setString("PRM_COMPETENCIA_UTI",competenciaUTI);
		}
	   
		@SuppressWarnings("unchecked")
		List<Long> val = query.addScalar("QUANTIDADE_MAXIMA", LongType.INSTANCE).list();
		
		if(val != null && !val.isEmpty()){
			return val.get(0);
		}
		
		return null;
	}
	
	public List<FatAtoMedicoAih> obterFatAtoMedicoAihPorCthIphCodSus(final Integer cthSeq, final Long iphCodSus, boolean nullSeqCompatibilidade,
																	 final String order, boolean asc) {
		return obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, iphCodSus, nullSeqCompatibilidade, order, asc, false);
	}
				
	public List<FatAtoMedicoAih> obterFatAtoMedicoAihPorCthIphCodSus(final Integer cthSeq, final Long iphCodSus, boolean nullSeqCompatibilidade,
																	 final String order, boolean asc, final boolean orderComposto) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
			criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
			
			if(iphCodSus != null){
				criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString(), iphCodSus));
			}
			
			if(nullSeqCompatibilidade){
				criteria.add(Restrictions.isNull(FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()));
			}
			
			if(orderComposto){
				// Marina 16/04/2013
				criteria.addOrder(Order.asc(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()));
				criteria.addOrder(Order.desc(FatAtoMedicoAih.Fields.TAO_SEQ.toString()));
			} else if(order != null){
				if(asc){
					criteria.addOrder(Order.asc(order));
				} else {
					criteria.addOrder(Order.desc(order));
				}
			}
				
			return executeCriteria(criteria);
		}
			 	
	/**
	 * TODO confirmar
	 */
	public List<CursorAtosMedicosVO> obterNiveisAtosMedico(Integer cthSeq, Short iphPhoSeq, Integer iphSeq){
		final StringBuilder sql = new StringBuilder(799);
		
		sql.append("SELECT DISTINCT AAM.").append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.SEQ_ARQ_SUS.toString())
			.append(", AAM.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.NIVEL.toString())
			.append(", AAM_1.").append(FatAtoMedicoAih.Fields.LOTE_OPM.name()).append(AS).append(CursorAtosMedicosVO.Fields.LOTE_OPM.toString())
			.append(", AAM_1.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.NIVEL1.toString())
			.append(", AAM_2.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.NIVEL2.toString())
			.append(", AAM_3.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.NIVEL3.toString())
			.append(", AAM_4.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(AS).append(CursorAtosMedicosVO.Fields.NIVEL4.toString())
			
		.append(FROM)
		.append(" AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
		.append(" AAM INNER JOIN AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name())
		.append(" ICT ON AAM.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND AAM.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" INNER JOIN AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name())
		.append(" ICT_1  ON ICT_1.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name()).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND ICT_1.").append(FatCompatExclusItem.Fields.IPH_SEQ.name()).append("    = ICT.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" INNER JOIN ").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
		.append(" AAM_1 ON AAM_1.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = ICT_1.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND AAM_1.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" = ICT_1.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" RIGHT OUTER JOIN AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name())
		.append(" ICT_2   ON ICT_2.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name()).append(" = ICT_1.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND ICT_2.").append(FatCompatExclusItem.Fields.IPH_SEQ.name()).append("    = ICT_1.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" RIGHT OUTER JOIN ").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
		.append(" AAM_2  ON AAM_2.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = ICT_2.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND AAM_2.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" = ICT_2.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" RIGHT OUTER JOIN AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name())
		.append(" ICT_3  ON ICT_3.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name()).append(" = ICT_2.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND ICT_3.").append(FatCompatExclusItem.Fields.IPH_SEQ.name()).append("    = ICT_2.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
		
		.append(" RIGHT OUTER JOIN ").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
		.append(" AAM_3   ON AAM_3.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = ICT_3.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND AAM_3.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" = ICT_3.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" RIGHT OUTER JOIN AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name())
		.append(" ICT_4  ON ICT_4.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name()).append(" = ICT_3.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND ICT_4.").append(FatCompatExclusItem.Fields.IPH_SEQ.name()).append("    = ICT_3.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" RIGHT OUTER JOIN ").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
		.append(" AAM_4  ON AAM_4.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = ICT_4.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
		.append("  AND AAM_4.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" = ICT_4.").append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
			
		.append(" WHERE 1=1  AND ICT.").append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name())
		.append(" = :P_IPH_PHO_SEQ  AND ICT.").append(FatCompatExclusItem.Fields.IPH_SEQ.name())
		.append("   = :P_IPH_SEQ  AND AAM.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(P_CTH_SEQ)
		.append("  AND AAM_1.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(P_CTH_SEQ)
		.append("  AND AAM_2.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(P_CTH_SEQ)
		.append("  AND AAM_3.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(P_CTH_SEQ)
		.append("  AND AAM_4.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(P_CTH_SEQ)
			
		.append(" ORDER BY AAM_1.").append(FatAtoMedicoAih.Fields.LOTE_OPM.name());
			
		final SQLQuery query = createSQLQuery(sql.toString());
	
		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setShort("P_IPH_PHO_SEQ", iphPhoSeq);
		query.setInteger("P_IPH_SEQ", iphSeq);
			
		final List<CursorAtosMedicosVO> result = query.addScalar(CursorAtosMedicosVO.Fields.SEQ_ARQ_SUS.toString(), ShortType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.LOTE_OPM.toString(), StringType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.NIVEL.toString(), LongType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.NIVEL1.toString(), LongType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.NIVEL2.toString(), LongType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.NIVEL3.toString(), LongType.INSTANCE)
												      .addScalar(CursorAtosMedicosVO.Fields.NIVEL4.toString(), LongType.INSTANCE)
												      .setResultTransformer(Transformers.aliasToBean(CursorAtosMedicosVO.class)).list();
			
		return result;
	}	
	
	public Object[] obterQtdMaximaExecucao( final Short iphPhoSeq, final Integer iphSeq,
			final Integer eaiCthSeq, final Date competencia,
			final Byte codigoSus, final int tipo,
			final DominioIndCompatExclus ...compatExclus) {
		
		final String ponto = ".";
		final String AliasAMA = "AMA";
		final String AliasIPH = "IPH";
		final String aliasCPX = "CPX";
		final String aliasICT = "ICT";
		
		final String aliasHibernateICT = "ICT3_";
		final String aliasHibernateCPX = "CPX2_";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class, AliasAMA);

		criteria.createAlias(AliasAMA + ponto + FatAtoMedicoAih.Fields.IPH.toString(), AliasIPH);
		criteria.createAlias(AliasIPH + ponto + FatItensProcedHospitalar.Fields.FAT_COMPETENCIA_COMPATIBILIDS.toString(), aliasCPX);
		criteria.createAlias(AliasIPH + ponto + FatItensProcedHospitalar.Fields.FAT_COMPAT_EXCLUS_ITEMS.toString(), aliasICT);
		
		criteria.add(Restrictions.eq(aliasICT + ponto + FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(aliasICT + ponto + FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeq));
		criteria.add(Restrictions.in(aliasICT + ponto + FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), compatExclus));
		criteria.add(Restrictions.eqProperty(aliasICT + ponto
				+ FatCompatExclusItem.Fields.COMPETENCIA_COMPATIBILIDADE_SEQ.toString(),
				aliasCPX + ponto
						+ FatCompetenciaCompatibilid.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(AliasAMA + ponto + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		
		if (isOracle()) {
			criteria.add(Restrictions.sqlRestriction(" TRUNC (?) BETWEEN TRUNC(" 
					+ aliasHibernateCPX + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() + " ) AND TRUNC(NVL(" 
					+ aliasHibernateCPX + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", SYSDATE)) ",
					(Object) competencia, TimestampType.INSTANCE));
		} else {
			criteria.add(Restrictions.sqlRestriction(" DATE_TRUNC ('day',cast( ? as TIMESTAMP)) BETWEEN DATE_TRUNC('day', " 
					+ aliasHibernateCPX + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() +  ") AND DATE_TRUNC('day', (COALESCE(" 
					+ aliasHibernateCPX + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", now()))) ",
					(Object) competencia, TimestampType.INSTANCE));
		}

		criteria.add(Restrictions.sqlRestriction( tipo +" = ( " +
				" CASE WHEN COALESCE({alias}."+FatAtoMedicoAih.Fields.TAO_SEQ.name()+",0) = 1 THEN 1 " +
				"      WHEN COALESCE({alias}."+FatAtoMedicoAih.Fields.TAO_SEQ.name()+",0) = 7 THEN 1 " +
				"   ELSE 0 END" + " ) "	));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(AliasAMA + ponto + FatAtoMedicoAih.Fields.TAO_SEQ.toString()))
				.add(Projections.groupProperty(AliasAMA + ponto + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()))
				.add(Projections.sqlProjection(" SUM({alias}."+ FatAtoMedicoAih.Fields.QUANTIDADE.name() + " * "+
											   " 	COALESCE(" + aliasHibernateICT + ponto + FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.name() + ",0)" +
											       ") AS " + FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.name(), 
											    new String[] { FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.name()}, 
											    new Type[]{LongType.INSTANCE}
											  )
					) );

		List<Object> lst = executeCriteria(criteria);
		if(!lst.isEmpty()){
			return (Object[]) lst.get(0);
		}

		return null; 
	}	

	// 2280 - C4
	public List<ContaApresentadaPacienteProcedimentoVO> buscarAtosMedicos(Integer codigoAtoOPME, Integer eaiCthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class, "AAM");
		criteria.createAlias(AAM + FatAtoMedicoAih.Fields.IPH.toString() , "IPH");
		
		criteria.add(Restrictions.eq(AAM + FatAtoMedicoAih.Fields.IND_MODO_COBRANCA.toString(), DominioModoCobranca.V));
		criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}.IND_CONSISTENTE, 'D') <> 'R' "));
		criteria.add(Restrictions.eq(AAM + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		if(codigoAtoOPME != null) {
			criteria.add(Restrictions.ne(AAM + FatAtoMedicoAih.Fields.TAO_SEQ.toString(), codigoAtoOPME.byteValue()));
		}
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.groupProperty(AAM + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.CTH_SEQ.toString())
				.add(Projections.groupProperty(AAM + FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.COD_SUS.toString())
				.add(Projections.groupProperty("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.sum(AAM + FatAtoMedicoAih.Fields.QUANTIDADE.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.QUANTIDADE.toString())
				.add(Projections.sum(AAM + FatAtoMedicoAih.Fields.VALOR_SADT.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SADT.toString())
				.add(Projections.sum(AAM + FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SERV_HOSP.toString())
				.add(Projections.sum(AAM + FatAtoMedicoAih.Fields.VALOR_SERV_PROF.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SERV_PROF.toString())
				;
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ContaApresentadaPacienteProcedimentoVO.class));
		return executeCriteria(criteria);
	}
	
	// 32238 - C4
	public List<SigValorReceitaVO> obterValoresReceitaAtosMedicos(List<Integer> listaCthSeq, DominioModoCobranca modoCobranca) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class, "aam");
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.groupProperty("aam." + FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()), SigValorReceitaVO.Fields.IPH_COD_SUS.toString())
				.add(Projections.sum("aam." + FatAtoMedicoAih.Fields.QUANTIDADE.toString()), SigValorReceitaVO.Fields.QTDE.toString())
				.add(Projections.sum("aam." + FatAtoMedicoAih.Fields.VALOR_PROCED.toString()), SigValorReceitaVO.Fields.VALOR_PROCED_REALIZ.toString())
				.add(Projections.sum("aam." + FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.toString()), SigValorReceitaVO.Fields.VALOR_SH_REALIZ.toString())
				.add(Projections.sum("aam." + FatAtoMedicoAih.Fields.VALOR_SERV_PROF.toString()), SigValorReceitaVO.Fields.VALOR_SP_REALIZ.toString());
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.in("aam." + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), listaCthSeq));
		criteria.add(Restrictions.eq("aam." + FatAtoMedicoAih.Fields.IND_MODO_COBRANCA.toString(), modoCobranca));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SigValorReceitaVO.class));
		return executeCriteria(criteria);
	}
	public Short obterQtdrealizada( final Integer cthSeq){

		final StringBuilder sql = new StringBuilder(790);

		sql.append("SELECT ")
		   .append("    AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(" AS CODIGO, ")
		   .append("    AMA.").append(FatAtoMedicoAih.Fields.QUANTIDADE.name()).append(" AS QUANTIDADE")
		   .append(" FROM ")
   		   .append("     AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AMA, ")
   		   .append("     AGH.").append(FatEspelhoAih.class.getAnnotation(Table.class).name()).append(" EAI ")
		   .append(" WHERE 1=1 ")
		   .append("     AND EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" = :CTH_SEQ ")
		   .append("     AND EAI.").append(FatEspelhoAih.Fields.SEQP.name()).append("     = 1 ")
		   .append("     AND EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append("  = AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
		   .append("     AND EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append("  = AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
		   .append("     AND AMA.").append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(" = 1");
			
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setInteger("CTH_SEQ", cthSeq);
	   
		@SuppressWarnings("unchecked")
		List<Object[]> val = query.addScalar("CODIGO", LongType.INSTANCE).
				addScalar("QUANTIDADE", ShortType.INSTANCE).list();
		
		if(val != null && !val.isEmpty()){
			return "4".equals(StringUtils.substring(((Long)val.get(0)[0]).toString(), 0, 1)) ? ((Short)val.get(0)[1]) : Short.valueOf("1");
		}

		
		return Short.valueOf("1");
	}

	public List<FatAtoMedicoAih> listarServicosProf(Integer cthSeq, Short sgrGrpSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.createAlias(FatAtoMedicoAih.Fields.IPH.toString(), "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.CARACTERISTICA, "CIH");
		criteria.createAlias("CIH." + FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(), "TCT");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString(), "FOG");	
		
		criteria.add(Restrictions.ne("FOG." + FatFormaOrganizacao.Fields.ID_SGR_GRP_SEQ.toString(), sgrGrpSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq("TCT." + FatTipoCaractItens.Fields.CARACTERISTICA.toString(), DominioFatTipoCaractItem.DEVE_SER_AUTORIZADO_PELA_SMS.getDescricao()));

		return this.executeCriteria(criteria);
	}

	public List<FatAtoMedicoAih> listarOpme(Integer cthSeq, Short sgrGrpSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAih.class);
		criteria.createAlias(FatAtoMedicoAih.Fields.IPH.toString(), "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.CARACTERISTICA, "CIH");
		criteria.createAlias("CIH." + FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(), "TCT");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString(), "FOG");	
		
		criteria.add(Restrictions.eq("FOG." + FatFormaOrganizacao.Fields.ID_SGR_GRP_SEQ.toString(), sgrGrpSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq("TCT." + FatTipoCaractItens.Fields.CARACTERISTICA.toString(), DominioFatTipoCaractItem.DEVE_SER_AUTORIZADO_PELA_SMS.getDescricao()));

		criteria.addOrder(Order.asc(FatAtoMedicoAih.Fields.SEQP.toString()));
		
		return this.executeCriteria(criteria);
	}	
}
