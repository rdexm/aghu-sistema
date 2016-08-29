package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class VFatContaHospitalarPacDAO extends BaseDao<VFatContaHospitalarPac> {

	/*
	 * private DetachedCriteria obterDetachedCriteriaVFatContaHospitalarPac(){
	 * return DetachedCriteria.forClass(VFatContaHospitalarPac.class); }
	 */

	private static final long serialVersionUID = -6465569720546592878L;

	/**
	 * Cria a criteria do filtro e situação A, F ou E
	 * 
	 * @param pacProntuario
	 *            Prontuário do paciente
	 * @param cthNroAih
	 *            AIH do paciente
	 * @param pacCodigo
	 *            Código do paciente
	 * @param cthSeq
	 *            Código da conta
	 * @param situacoes
	 *            Situações da conta hospitalar
	 * @return
	 */
	private DetachedCriteria createCriteria(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class);

		criteria.createAlias(VFatContaHospitalarPac.Fields.PACIENTE.toString(), VFatContaHospitalarPac.Fields.PACIENTE.toString());
		criteria.setFetchMode(VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(VFatContaHospitalarPac.Fields.CONVENIO_SAUDE_PLANO.toString(), FetchMode.JOIN);

		if (pacProntuario != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_PRONTUARIO.toString(), pacProntuario));
		}
		if (cthNroAih != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_NRO_AIH.toString(), cthNroAih));
		}
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		if (cthSeq != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), cthSeq));
		}

		if (situacoes != null) {
			criteria.add(Restrictions.in(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString(), situacoes));
		}

		return criteria;
	}
	
	/**
	 *  Obtem a conta hospitalar de um determinado atendimento, disponibilizado pela nota de consumo
	 */
	public List<FatContasHospitalares> pesquisaContaHospitalarParaNotaConsumoDaCirurgia(
			Integer seqAtendimento) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "cch");
		criteria.createCriteria("cch." + FatContasHospitalares.Fields.CONTA_INTERNACAO, "cci", JoinType.INNER_JOIN);
		criteria.createCriteria("cci." + FatContasInternacao.Fields.INTERNACAO, "int", JoinType.INNER_JOIN);
		criteria.createCriteria("int." + AinInternacao.Fields.ATENDIMENTO, "atd", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.SEQ, seqAtendimento));

		DominioSituacaoConta[] situacoes = new DominioSituacaoConta[] {DominioSituacaoConta.A, DominioSituacaoConta.F};
		criteria.add(Restrictions.in("cch." + FatContasHospitalares.Fields.IND_SITUACAO.toString(),  situacoes));

		return executeCriteria(criteria);

	}

	/**
	 * Busca uma lista de VFatContaHospitalarPac pelo filtro e situação A, F ou
	 * E
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param pacProntuario
	 * @param cthNroAih
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public List<VFatContaHospitalarPac> pesquisarAbertaFechadaEncerrada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq) {

		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, new DominioSituacaoConta[] {
				DominioSituacaoConta.A, DominioSituacaoConta.F, DominioSituacaoConta.E });

		criteria.addOrder(Order.desc(VFatContaHospitalarPac.Fields.CTH_SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Busca uma lista de VFatContaHospitalarPac pelo filtro e situação A e F
	 * ordenados pela data de alta administrativa
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param pacProntuario
	 * @param cthNroAih
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public List<VFatContaHospitalarPac> pesquisarAbertaOuFechadaOrdenadaPorDtIntAdm(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer pacProntuario, Integer cthSeq, Integer codigo, DominioSituacaoConta situacao) {

		DominioSituacaoConta[] situacoes;

		if (situacao != null) {
			situacoes = new DominioSituacaoConta[] { situacao };
		} else {
			situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F };

		}

		DetachedCriteria criteria = this.createCriteria(pacProntuario, null, codigo, cthSeq, situacoes);

		criteria.addOrder(Order.asc(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Faz um count em VFatContaHospitalarPac pelo filtro e situação A e F
	 * 
	 * @param pacProntuario
	 * @param cthNroAih
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public Long pesquisarAbertaOuFechadaCount(Integer pacProntuario, Integer cthSeq, Integer codigo, DominioSituacaoConta situacao) {

		DominioSituacaoConta[] situacoes;

		if (situacao != null) {
			situacoes = new DominioSituacaoConta[] { situacao };
		} else {
			situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F };

		}

		DetachedCriteria criteria = this.createCriteria(pacProntuario, null, codigo, cthSeq, situacoes);

		return executeCriteriaCount(criteria);
	}

	public List<VFatContaHospitalarPac> pesquisarPorPacCodigo(Integer pacCodigo) {

		DetachedCriteria criteria = this.createCriteria(null, null, pacCodigo, null, new DominioSituacaoConta[] { DominioSituacaoConta.A,
				DominioSituacaoConta.F });

		return executeCriteria(criteria);
	}

	public Long pesquisarPorPacCodigoCount(Integer pacCodigo) {

		DetachedCriteria criteria = this.createCriteria(null, null, pacCodigo, null, new DominioSituacaoConta[] { DominioSituacaoConta.A,
				DominioSituacaoConta.F });

		return executeCriteriaCount(criteria);
	}

	public List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(Integer pacProntuario, Long cthNroAih, Integer pacCodigo,
			Integer cthSeq, DominioSituacaoConta[] situacoes, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarVFatContaHospitalarPacCount(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes);

		return executeCriteriaCount(criteria);
	}

	/**
	 * Busca o número de elementos da lista de VFatContaHospitalarPac pelo
	 * filtro e situação A, F ou E
	 * 
	 * @param pacProntuario
	 * @param cthNroAih
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public Long pesquisarAbertaFechadaEncerradaCount(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq) {

		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, new DominioSituacaoConta[] {
				DominioSituacaoConta.A, DominioSituacaoConta.F, DominioSituacaoConta.E });

		return executeCriteriaCount(criteria);
	}

	/**
	 * Busca o paciente da lista de VFatContaHospitalarPac pelo filtro e
	 * situação A, F ou E
	 * 
	 * @param pacProntuario
	 * @param cthNroAih
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public AipPacientes pesquisarAbertaFechadaEncerradaPaciente(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq) {

		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, new DominioSituacaoConta[] {
				DominioSituacaoConta.A, DominioSituacaoConta.F, DominioSituacaoConta.E });

		List<VFatContaHospitalarPac> result = executeCriteria(criteria, 0, 1, VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), true);
		if (result != null && !result.isEmpty()) {
			return result.get(0).getPaciente();
		}
		return null;
	}

	public List<VFatContaHospitalarPac> pesquisarContaHospitalar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer prontuario, Integer contaHospitalar, String codigoDcih, Long numeroAih, Date competencia, Integer codigo,
			final DominioSituacaoConta[] situacoes) {

		DetachedCriteria criteria = montaCriteriaContaHospitalar(prontuario, contaHospitalar, codigoDcih, numeroAih, competencia, codigo);

		if (situacoes != null) {
			criteria.add(Restrictions.in(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString(), situacoes));
		}

		criteria.addOrder(Order.desc(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public VFatContaHospitalarPac buscarPrimeiraContaHospitalarPaciente(Integer contaHospitalar) {

		DetachedCriteria criteria = montaCriteriaContaHospitalar(null, contaHospitalar, null, null, null, null);
		List<VFatContaHospitalarPac> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public VFatContaHospitalarPac buscarPrimeiraAihPaciente(Long numeroAih) {
		
		DetachedCriteria criteria = montaCriteriaContaHospitalar(null, null, null, numeroAih, null, null);
		List<VFatContaHospitalarPac> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public Long pesquisarContaHospitalarCount(Integer prontuario, Integer contaHospitalar, String codigoDcih, Long numeroAih,
			Date competencia, Integer codigo, final DominioSituacaoConta[] situacoes) {

		DetachedCriteria criteria = montaCriteriaContaHospitalar(prontuario, contaHospitalar, codigoDcih, numeroAih, competencia, codigo);

		if (situacoes != null) {
			criteria.add(Restrictions.in(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString(), situacoes));
		}

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montaCriteriaContaHospitalar(Integer prontuario, Integer contaHospitalar, String codigoDcih, Long numeroAih,
			Date competencia, Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class);

		criteria.createAlias(VFatContaHospitalarPac.Fields.AIH.toString(), "AIH", JoinType.LEFT_OUTER_JOIN);

		if (prontuario != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_PRONTUARIO.toString(), prontuario));
		}

		if (contaHospitalar != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), contaHospitalar));
		}

		if (codigoDcih != null) {
			criteria.createAlias(VFatContaHospitalarPac.Fields.DOCUMENTO_COBRANCA_AIH.toString(),
					VFatContaHospitalarPac.Fields.DOCUMENTO_COBRANCA_AIH.toString());

			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CODIGO_DCIH.toString(), codigoDcih));
		}

		if (numeroAih != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_NRO_AIH.toString(), numeroAih));
		}

		if (competencia != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(competencia);
			Short mes = (short) (data.get(Calendar.MONTH) + 1);
			Short ano = (short) data.get(Calendar.YEAR);

			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.DCI_CPE_ANO.toString(), ano));

			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.DCI_CPE_MES.toString(), mes));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString(), codigo));
		}

		return criteria;
	}

	/**
	 * Busca primeira conta e internacoes do paciente c/mesmo ssm realizado
	 * 
	 * @param pCthSeq
	 * @param pPacCodigo
	 * @param pPhiRealiz
	 * @param pDtIntAdm
	 * @param pDias
	 * @return
	 */
	public Integer buscarPrimeiraCthIntPacSsmRealizado(Integer pCthSeq, Integer pPacCodigo, Integer pPhiRealiz, Date pDtIntAdm, Byte pDias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class);

		criteria.add(Restrictions.ne(VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), pCthSeq));
		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString(), pPacCodigo));
		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_PHI_SEQ_REALIZADO.toString(), pPhiRealiz));
		criteria.add(Restrictions.isNotNull(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()));
		criteria.add(Restrictions.le(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), pDtIntAdm));

		// AND (pDtIntAdm - cth_dt_alta_administrativa) <= pDias
		// =
		// AND cth_dt_alta_administrativa >= (pDtIntAdm - pDias)
		Calendar pDtIntAdmpDias = Calendar.getInstance();
		pDtIntAdmpDias.setTime(pDtIntAdm);
		pDtIntAdmpDias.add(Calendar.DAY_OF_YEAR, -pDias);
		criteria.add(Restrictions.ge(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), pDtIntAdmpDias.getTime()));

		criteria.add(Restrictions.in(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString(), new DominioSituacaoConta[] {
				DominioSituacaoConta.A, DominioSituacaoConta.F, DominioSituacaoConta.E }));

		criteria.setProjection(Projections.property(VFatContaHospitalarPac.Fields.CTH_SEQ.toString()));

		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public VFatContaHospitalarPac buscarPrimeiraContaHospitalaresParaReinternar(Integer cthSeq, Integer pacCodigo,
			Date dataInternacaoAdministrativa, Byte vDias, Short cspCnvCodigo, Byte cspCodigo, DominioSituacaoConta[] situacoes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInternacaoAdministrativa);
		cal.add(Calendar.DAY_OF_MONTH, -vDias);

		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class);

		criteria.createAlias(VFatContaHospitalarPac.Fields.CONVENIO_SAUDE_PLANO.toString(),
				VFatContaHospitalarPac.Fields.CONVENIO_SAUDE_PLANO.toString());

		criteria.add(Restrictions.ne(VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString(), pacCodigo));

		criteria.add(Restrictions.le(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), dataInternacaoAdministrativa));

		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_CSP_CNV_CODIGO.toString(), cspCnvCodigo));

		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_CSP_SEQ.toString(), cspCodigo));

		criteria.add(Restrictions.in(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString(), situacoes));

		criteria.add(Restrictions.ge(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), cal.getTime()));

		criteria.addOrder(Order.desc(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()));

		List<VFatContaHospitalarPac> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Long pesquisarVFatContaHospitalarPacCount(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes, String codigoDcih, Date dataInternacaoAdm, Date dataAltaAdm,
			VFatAssociacaoProcedimento procedimentoSolicitado, VFatAssociacaoProcedimento procedimentoRealizado) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes, codigoDcih,
				dataInternacaoAdm, dataAltaAdm, procedimentoSolicitado, procedimentoRealizado);

		return executeCriteriaCount(criteria);
	}
	
	public List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(Integer pacProntuario, Long cthNroAih, Integer pacCodigo,
			Integer cthSeq, DominioSituacaoConta[] situacoes, String codigoDcih, Date dataInternacaoAdm, Date dataAltaAdm,
			VFatAssociacaoProcedimento procedimentoSolicitado, VFatAssociacaoProcedimento procedimentoRealizado, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes, codigoDcih,
				dataInternacaoAdm, dataAltaAdm, procedimentoSolicitado, procedimentoRealizado);
 
		criteria.createAlias(VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR.toString(),
				VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(),
				VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContaHospitalarPac.Fields.MOTIVO_REJEICAO_CONTA.toString(),
				VFatContaHospitalarPac.Fields.MOTIVO_REJEICAO_CONTA.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString(),
				VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString() + "."
				+ FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), 
				VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString() + "."
						+ FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria createCriteria(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes, String codigoDcih, Date dataInternacaoAdm, Date dataAltaAdm,
			VFatAssociacaoProcedimento procedimentoSolicitado, VFatAssociacaoProcedimento procedimentoRealizado) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes);

		if (StringUtils.isNotBlank(codigoDcih)) {
			criteria.createAlias(VFatContaHospitalarPac.Fields.DOCUMENTO_COBRANCA_AIH.toString(),
					VFatContaHospitalarPac.Fields.DOCUMENTO_COBRANCA_AIH.toString());
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CODIGO_DCIH.toString(), codigoDcih));
		}

		if (dataInternacaoAdm != null) {
			criteria.add(Restrictions.between(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString(),
					DateUtil.obterDataComHoraInical(dataInternacaoAdm), DateUtil.obterDataComHoraFinal(dataInternacaoAdm)));
		}

		if (dataAltaAdm != null) {
			criteria.add(Restrictions.between(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(),
					DateUtil.obterDataComHoraInical(dataAltaAdm), DateUtil.obterDataComHoraFinal(dataAltaAdm)));
		}

		if (procedimentoSolicitado != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_PHI_SEQ.toString(), procedimentoSolicitado.getId().getPhiSeq()));
		}

		if (procedimentoRealizado != null) {
			criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.CTH_PHI_SEQ_REALIZADO.toString(), procedimentoRealizado.getId()
					.getPhiSeq()));
		}

		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<VFatContaHospitalarPac> listarContasHospitalaresCadastroSugestaoDesdobramento(Integer pacCodigo, Date data,
			Short cspCnvCodigo, Byte cspSeq, DominioSituacaoConta[] situacoesContaHospitalar, Integer firstResult, Integer maxResults) {
		StringBuffer hql = new StringBuffer(260);
		hql.append(" select v ");
		hql.append(" from ").append(VFatContaHospitalarPac.class.getSimpleName()).append(" v ");
		hql.append(" where v.").append(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo ");
		hql.append(" 	and v.").append(VFatContaHospitalarPac.Fields.CTH_CSP_CNV_CODIGO.toString()).append(" = :cspCnvCodigo ");
		hql.append(" 	and v.").append(VFatContaHospitalarPac.Fields.CTH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" 	and v.").append(VFatContaHospitalarPac.Fields.IND_SITUACAO.toString()).append(" in(:situacoesContaHospitalar) ");
		hql.append(" 	and :data between v.").append(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString());
		hql.append(" 		and coalesce(v.").append(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()).append(", :auxData)) ");
		hql.append(" 	and and not exists ( ");
		hql.append(" 		select 1 ");
		hql.append(" 		from ").append(FatContasHospitalares.class.getSimpleName()).append(" f ");
		hql.append(" 		where f.").append(FatContasHospitalares.Fields.CTH_SEQ.toString()).append(" = v.")
				.append(VFatContaHospitalarPac.Fields.CTH_SEQ.toString());
		hql.append(" 	) ");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, 1);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("cspCnvCodigo", cspCnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameterList("situacoesContaHospitalar", situacoesContaHospitalar);
		query.setParameter("data", data);
		query.setParameter("auxData", DateUtil.truncaData(cal.getTime()));

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query.list();
	}

	public VFatContaHospitalarPac obterContaHospitalarPaciente(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class, "VFCTH");

		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR.toString(), "CTH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.AIH.toString(), "AIH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI",
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(), "PHIR",
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.FAT_ESPELHO_AIH.toString(), "FEA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VFCTH." + VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString(), "SSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SSP." + FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), "MSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.EXCLUSAO_CRITICA.toString(), "ECC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "CNI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CNI." + FatContasInternacao.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + AinInternacao.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("VFCTH." + VFatContaHospitalarPac.Fields.CTH_SEQ.toString(), seq));

		List<VFatContaHospitalarPac> listaResultado = executeCriteria(criteria);

		if (listaResultado == null || listaResultado.isEmpty()) {
			return null;
		}

		return listaResultado.get(0);
	}
	
	/**
	 * Consulta {@link VFatContaHospitalarPac} vinculadas ao {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @return {@link List} de {@link VFatContaHospitalarPac}
	 */
	public List<VFatContaHospitalarPac> listarVFatContaHospitalarPacPorFatSituacaoSaidaPaciente(FatSituacaoSaidaPaciente entity) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContaHospitalarPac.class);

		criteria.createAlias(VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString(), 
				VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString());

		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString()
				+ "." + FatSituacaoSaidaPaciente.Fields.SEQ.toString(), entity.getId().getSeq()));
		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.SITUACAO_SAIDA_PACIENTE.toString()
				+ "." + FatSituacaoSaidaPaciente.Fields.MSP_SEQ.toString(), entity.getId().getMspSeq()));
		
		return executeCriteria(criteria);
	}
}
