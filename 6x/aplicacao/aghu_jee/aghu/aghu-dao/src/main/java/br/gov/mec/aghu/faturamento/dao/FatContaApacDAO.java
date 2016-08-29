package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloFatContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.faturamento.vo.FatPeriodoApacVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaApac;

public class FatContaApacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatContaApac> {

	private static final long serialVersionUID = -9097572223305339126L;
	private static final String ALIAS_CAP = "cap";
	private static final String ALIAS_ATM = "atm";
	private static final String PONTO = ".";
	
	/*
	 * c_apacs ( p_modulo IN VARCHAR2, p_cpe_dt_hr_inicio IN DATE, p_cpe_mes IN
	 * NUMBER, p_cpe_ano IN NUMBER) IS SELECT cap.* , atm.ind_tipo_tratamento
	 * FROM aac_atendimento_apacs atm, fat_conta_apacs cap WHERE
	 * cap.ind_situacao = 'A' AND cap.cpe_modulo = p_modulo AND
	 * cap.cpe_dt_hr_inicio = p_cpe_dt_hr_inicio AND cap.cpe_mes = p_cpe_mes AND
	 * cap.cpe_ano = p_cpe_ano AND atm.numero = cap.atm_numero;
	 */
	private DetachedCriteria criarCriteriaFatContaApacAtivaPorModuloDtInicioMesAno(final DominioModuloCompetencia modulo, final Date dthrInicio,
			final Byte cpeMes, final Short cpeAno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class);
		criteria.add(Restrictions.eq(FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CPE_MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CPE_DTHR_INICIO.toString(), dthrInicio));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CPE_ANO.toString(), cpeAno));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CPE_MES.toString(), cpeMes));

		return criteria;
	}

	public List<FatContaApac> buscarFatContaApacAtivaPorModuloDtInicioMesAno(final DominioModuloCompetencia modulo, final Date dthrInicio,
			final Byte cpeMes, final Short cpeAno) {
		return executeCriteria(criarCriteriaFatContaApacAtivaPorModuloDtInicioMesAno(modulo, dthrInicio, cpeMes, cpeAno));
	}

	/**
	 * Este método deve ser revisado quando forem migradas as APACS, ver com Milena a melhor forma de tratar esta consulta.
	 * 
	 * @param tipoTrat  --> parâmetro chumbado em alguns pontos 
	 * @see FatkCth2RN.rnCthcAtuEncPrv, chamada (faturamentoFatkCap2RN.rnCapcVerCapPac)
	 * @author eScwheigert
	 * @since 03/09/2012
	 */
	public Long countApac(Integer pacCodigo, DominioModuloCompetencia pModulo, Date pDtRealiz, Byte tipoTrat, DominioSituacaoContaApac indSituacao) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class, ALIAS_CAP);
		
		criteria.createAlias(ALIAS_CAP + PONTO + FatContaApac.Fields.AAC_ATENDIMENTO_APACS.toString(), ALIAS_ATM);
		
		criteria.add(Restrictions.eq(ALIAS_CAP + PONTO + FatContaApac.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(ALIAS_ATM + PONTO + AacAtendimentoApacs.Fields.IND_TIPO_TRATAMENTO.toString(), tipoTrat));
		criteria.add(Restrictions.ne(ALIAS_CAP + PONTO + FatContaApac.Fields.IND_SITUACAO.toString(), indSituacao));
		
		if (DominioModuloCompetencia.INT.equals(pModulo)) {
			if (this.isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" ? >= ( {alias}" + PONTO + FatContaApac.Fields.DT_INICIO_VALIDADE.name() + " -3 ) ", pDtRealiz, TimestampType.INSTANCE));
			} else {
				criteria.add(Restrictions.sqlRestriction("? >=( {alias}" + PONTO + FatContaApac.Fields.DT_INICIO_VALIDADE.name() + " + interval ' - 3 day' )", pDtRealiz, TimestampType.INSTANCE));
			}
		} else {
			criteria.add(Restrictions.ge(ALIAS_CAP + PONTO + FatContaApac.Fields.DT_INICIO_VALIDADE.toString(), pDtRealiz));
		}
		
		if (this.isOracle()) {
			criteria.add(
					Restrictions.sqlRestriction("( ? <= COALESCE( {alias}" + PONTO + FatContaApac.Fields.DT_FIM_VALIDADE.name() + ", SYSDATE ))", pDtRealiz, TimestampType.INSTANCE));
		} else {
			criteria.add(
					Restrictions.sqlRestriction("( ? <= COALESCE( {alias}" + PONTO + FatContaApac.Fields.DT_FIM_VALIDADE.name() + ", now() ))", pDtRealiz, TimestampType.INSTANCE));
		}
		
		return executeCriteriaCount(criteria);
	}

	// --- dados da APAC para associar itens
	public List<FatPeriodoApacVO> buscarDadosApacAssociacao(final Long atmNumero, final Byte seqp, final Date dataInicio,
			final DominioModuloCompetencia modulo, final Byte cpeMes, final Short cpeAno, final String nomeParametro) {

		/*
		 * CURSOR c_periodo_apac ( p_atm_numero IN NUMBER, p_seqp IN NUMBER,
		 * p_cpe_dt_hr_inicio IN DATE, p_cpe_modulo IN VARCHAR, p_cpe_mes IN
		 * NUMBER, p_cpe_ano IN NUMBER) IS SELECT atd.seq atd_seq,
		 * atd.ind_tipo_tratamento, cap.pac_codigo, cap.dt_inicio_validade,
		 * NVL(cap.dt_obito_alta, NVL(cap.dt_fim_validade, -- Milena -
		 * Outubro/2004 (PAR.) DECODE(CPE.IND_SITUACAO,'A',PAR.vlr_data,
		 * CPE.DT_HR_FIM))) dt_fim_validade -- Milena - JANEIRO/2005 (DECODE)
		 * FROM fat_conta_apacs cap, aac_atendimento_apacs atm, agh_atendimentos
		 * atd, agh_parametros par, FAT_COMPETENCIAS CPE WHERE cap.atm_numero =
		 * p_atm_numero AND cap.seqp = p_seqp AND cap.cpe_dt_hr_inicio =
		 * p_cpe_dt_hr_inicio AND cap.cpe_modulo = p_cpe_modulo AND cap.cpe_mes
		 * = p_cpe_mes AND cap.cpe_ano = p_cpe_ano AND atm.numero =
		 * cap.atm_numero AND atm.atd_seq = atd.seq AND par.nome LIKE
		 * 'P_DT_FIM_COMP%' -- AND PAR.VLR_TEXTO IN ('APAN','APAC') -- Marina
		 * 01/02/2011 and PAR.VLR_TEXTO = p_cpe_modulo -- Marina 01/02/2011 --
		 * Milena janeiro/2005 incluindo fat_competencias AND CPE.dt_hr_inicio =
		 * p_cpe_dt_hr_inicio -- Marina 01/02/2011 AND CPE.MES = p_cpe_mes AND
		 * CPE.ANO = p_cpe_ANO AND CPE.MODULO = CAP.CPE_MODULO;
		 */

		// REALIZAR PROJEÇÕES
		final StringBuilder hql = new StringBuilder(500).append("select ");
		
		 hql.append("  atd.").append(AghAtendimentos.Fields.SEQ.toString())
			.append(", atd.").append(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString())
			.append(", cap.").append(FatContaApac.Fields.COD_PACIENTE.toString())
			.append(", cap.").append(FatContaApac.Fields.DT_INICIO_VALIDADE.toString())
			.append(", cap.").append(FatContaApac.Fields.DT_OBITO_ALTA.toString())
			.append(", cap.").append(FatContaApac.Fields.DT_FIM_VALIDADE.toString())
			.append(", cpe.").append(FatCompetencia.Fields.IND_SITUACAO.toString())
			.append(", par.").append(AghParametros.Fields.VLR_DATA.toString())
			.append(", cpe.").append(FatCompetencia.Fields.DT_HR_FIM.toString())
			
			.append(" from ")
			.append(FatContaApac.class.getName()).append(" cap, ")
			.append(AacAtendimentoApacs.class.getName()).append(" atm, ")
			.append(AghAtendimentos.class.getName()).append(" atd, ")
			.append(AghParametros.class.getName()).append(" par, ")
			.append(FatCompetencia.class.getName()).append(" cpe ")
			
			.append(" where ")
			.append("       cap.").append(FatContaApac.Fields.ATM_CODIGO.toString()).append(" = :atmNumero ") 
			.append("   and cap.").append(FatContaApac.Fields.SEQP.toString()).append(" = :seqp ") 
			.append("   and cap.").append(FatContaApac.Fields.CPE_DTHR_INICIO.toString()).append(" = :dataInicio ") 
			.append("   and cap.").append(FatContaApac.Fields.CPE_MES.toString()).append(" = :cpeMes ") 
			.append("   and cap.").append(FatContaApac.Fields.CPE_ANO.toString()).append(" = :cpeAno ") 
			.append("   and cap.").append(FatContaApac.Fields.ATM_CODIGO.toString()).append(" = atm.").append(AacAtendimentoApacs.Fields.NUMERO.toString())
			.append("   and atm.").append(AacAtendimentoApacs.Fields.ATD_SEQ.toString()).append(" = atd.").append(AghAtendimentos.Fields.SEQ.toString())
			.append("   and par.").append(AghParametros.Fields.NOME.toString()).append(" like :nomeParametro ")
			.append("   and par.").append(AghParametros.Fields.VLR_TEXTO.toString()).append(" = :modulo ")
			.append("   and cpe.").append(FatCompetencia.Fields.DT_HR_INICIO.toString()).append(" = :dataInicio ")
			.append("   and cpe.").append(FatCompetencia.Fields.MES.toString()).append(" = :cpeMes ")
			.append("   and cpe.").append(FatCompetencia.Fields.ANO.toString()).append(" = :cpeAno ")
			.append("   and cpe.").append(FatCompetencia.Fields.MODULO.toString()).append(" = :modulo ");
		 
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("atmNumero", atmNumero);
		query.setParameter("seqp", seqp);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("cpeMes", cpeMes);
		query.setParameter("cpeAno", cpeAno);
		query.setParameter("nomeParametro", nomeParametro);
		query.setParameter("modulo", modulo.toString());

		return processaProjecao(query.list());
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<FatPeriodoApacVO> processaProjecao(@SuppressWarnings("rawtypes") List list) {
		if (list == null) {
			return new ArrayList<FatPeriodoApacVO>(0);
		}
		List<FatPeriodoApacVO> retorno = new ArrayList<FatPeriodoApacVO>(list.size());
		for (Object obj : list) {
			FatPeriodoApacVO periodoApacVO = new FatPeriodoApacVO();
			// atd.seq atd_seq, atd.ind_tipo_tratamento ind_tipo_tratamento,
			// cap.pac_codigo pac_codigo, cap.dt_inicio_validade
			// dt_inicio_validade, cap.dt_obito_alta dt_obito_alta,
			// cap.dt_fim_validade dt_fim_validade, CPE.IND_SITUACAO
			// IND_SITUACAO, PAR.vlr_data vlr_data, CPE.DT_HR_FIM DT_HR_FIM
			Object[] res = (Object[]) obj;
			if (res[0] != null) {
				periodoApacVO.setAtdSeq((Integer) res[0]);
			}
			if (res[1] != null) {
				periodoApacVO.setIndTipoTratamento((DominioTipoTratamentoAtendimento) res[1]);
			}
			if (res[2] != null) {
				periodoApacVO.setCodigoPaciente((Integer) res[2]);
			}
			if (res[3] != null) {
				periodoApacVO.setDtInicioValidadeContaApac((Date) res[3]);
			}
			if (res[4] != null) {
				periodoApacVO.setDtObitoAltaContaApac((Date) res[4]);
			}
			if (res[5] != null) {
				periodoApacVO.setDtFimValidadeContaApac((Date) res[5]);
			}
			if (res[6] != null) {
				periodoApacVO.setIndSituacaoCompetencia((DominioSituacaoCompetencia) res[6]);
			}
			if (res[7] != null) {
				periodoApacVO.setVlrDataParametro((Date) res[7]);
			}
			if (res[8] != null) {
				periodoApacVO.setDtHrFimCompetencia((Date) res[8]);
			}
			retorno.add(periodoApacVO);
		}
		return retorno;
	}

	public List<FatContaApac> listarContasApacsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class);

		criteria.add(Restrictions.eq(FatContaApac.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * 42011
	 * @param pacCodigo
	 * @param modulo
	 * @return
	 */
	public Boolean existeContaApac(Integer pacCodigo, DominioModuloFatContaApac modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class);

		criteria.add(Restrictions.eq(FatContaApac.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CPE_MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.A));
		criteria.add(Restrictions.eq(FatContaApac.Fields.IND_CTRL_FREQUENCIA.toString(), "N"));

		return executeCriteriaExists(criteria);
	}
	
	/**
	 * 42010
	 * @param pacCodigo
	 * @return
	 */
	public FatContaApac buscaContaApac(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class);
		
		criteria.add(Restrictions.eq(FatContaApac.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatContaApac.Fields.CAP_TYPE.toString(), "APT"));
		criteria.add(Restrictions.eq(FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.A));
		
		return (FatContaApac) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 41736
	 * @param numeroApac
	 * @param capSeqApac
	 * @return
	 */
	public FatContaApac buscaContaApacPorChave(Long numeroApac, Byte capSeqApac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class);
		
		criteria.add(Restrictions.eq(FatContaApac.Fields.ATM_CODIGO.toString(), numeroApac));
		criteria.add(Restrictions.eq(FatContaApac.Fields.SEQP.toString(), capSeqApac));
		
		return (FatContaApac) executeCriteriaUniqueResult(criteria);
	}
}
