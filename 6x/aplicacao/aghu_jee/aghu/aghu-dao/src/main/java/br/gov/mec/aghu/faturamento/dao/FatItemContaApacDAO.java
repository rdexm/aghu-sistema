package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioModuloFatContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoItemContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;

public class FatItemContaApacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatItemContaApac> {

	private static final long serialVersionUID = -994995423536691728L;

	protected DetachedCriteria obterCriteriaPorSolicitacao(Integer soeSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatItemContaApac.class);
		result.add(Restrictions.eq(FatItemContaApac.Fields.ISE_SOE_SEQ.toString(), soeSeq));

		return result;
	}

	public List<FatItemContaApac> obterListaPorSolicitacao(Integer soeSeq) {

		List<FatItemContaApac> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacao(soeSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	protected DetachedCriteria obterCriteriaPorSolicitacaoNaoPouE(Integer soeSeq) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorSolicitacao(soeSeq);
		result.add(Restrictions.ne(FatItemContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoItemContaApac.P));
		result.add(Restrictions.ne(FatItemContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoItemContaApac.E));

		return result;
	}

	public List<FatItemContaApac> obterListaPorSolicitacaoNaoPouE(Integer soeSeq) {

		List<FatItemContaApac> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacaoNaoPouE(soeSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	public List<FatItemContaApac> listarItemContaApacPorPrhConsultaSituacao(Integer consultaNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		criteria.add(Restrictions.eq(FatItemContaApac.Fields.PRH_CON_NUMERO.toString(), consultaNumero));

		criteria.add(Restrictions.in(FatItemContaApac.Fields.IND_SITUACAO.toString(), new DominioSituacaoItemContaApac[] {
				DominioSituacaoItemContaApac.E, DominioSituacaoItemContaApac.P }));

		return executeCriteria(criteria);
	}

	public List<FatItemContaApac> buscarItensConta(final DominioModuloFatContaApac pModulo, final Date pDthrInicio, final Byte pMes, final Short pAno) {
		return executeCriteria(obterCriteriaItensConta(pModulo, pDthrInicio, pMes, pAno));
	}

	protected DetachedCriteria obterCriteriaItensConta(final DominioModuloFatContaApac pModulo, final Date pDthrInicio, final Byte pMes,
			final Short pAno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class, "ent");
		criteria.createAlias("ent." + FatItemContaApac.Fields.FAT_CONTA_APAC.toString(), "conta");
		criteria.add(Restrictions.eq("conta." + FatContaApac.Fields.CPE_MODULO.toString(), pModulo));
		criteria.add(Restrictions.eq("conta." + FatContaApac.Fields.CPE_DTHR_INICIO.toString(), pDthrInicio));
		criteria.add(Restrictions.eq("conta." + FatContaApac.Fields.CPE_MES.toString(), pMes));
		criteria.add(Restrictions.eq("conta." + FatContaApac.Fields.CPE_ANO.toString(), pAno));
		criteria.add(Restrictions.eq("conta." + FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.E));
		return criteria;
	}

	public List<FatItemContaApac> obterItemContaApacPorPmrSeq(Long pmrSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		criteria.add(Restrictions.eq(FatItemContaApac.Fields.PMR_SEQ.toString(), pmrSeq));
		
		return executeCriteria(criteria);
	}
	
	
	public List<FatItemContaApac> listarFatItemContaApacPorPpcCrgSeq(Integer ppcCrgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		
		criteria.add(Restrictions.eq(FatItemContaApac.Fields.PPC_CRG_SEQ.toString(), ppcCrgSeq));
		
		return this.executeCriteria(criteria);
	}
	
	
	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Integer ppcCrgSeq) {
		criteria.add(Restrictions.eq(
				FatItemContaApac.Fields.PPC_CRG_SEQ.toString(), ppcCrgSeq));
		criteria.add(Restrictions.in(
				FatItemContaApac.Fields.IND_SITUACAO.toString(), 
					new DominioSituacaoItemContaApac[] {
			DominioSituacaoItemContaApac.E, DominioSituacaoItemContaApac.P }));
		return criteria;
	}
	
	public List<FatItemContaApac> listarItemContaApac(Integer ppcCrgSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		criteria = this.obterCriterioConsulta(criteria, ppcCrgSeq);
		
		return executeCriteria(criteria);
	}

	public List<FatItemContaApac> buscarPorCirurgia(Integer crgSeq,
			DominioSituacaoItemContaApac[] indSituacoes) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		criteria.add(Restrictions.eq(FatItemContaApac.Fields.PPC_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(FatItemContaApac.Fields.IND_SITUACAO.toString(), indSituacoes));
		return executeCriteria(criteria);
	}

	/**
	 * Realiza a busca por Itens de Procedimentos transferidos, relacionados à Consulta informada.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @return Lista de Itens de Conta
	 */
	public List<FatItemContaApac> pesquisarItensProcedimentoTransferidoPorConsulta(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class, "ICA");

		criteria.createAlias("ICA." + FatItemContaApac.Fields.PROCED_AMB_REALIZADO, "PMR");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.CONSULTA, "CON");

		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO, numeroConsulta));
		criteria.add(Restrictions.in("PMR." + FatProcedAmbRealizado.Fields.IND_SITUACAO,
				new Object[] {
					DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO,
					DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO_APAP }));

		return executeCriteria(criteria);
	}
	
	/**
	 * Realiza a busca por Itens de Procedimentos transferidos, relacionados aos
	 * Atendimento, Procedimento Interno e Data de realização informados.
	 * 
	 * @param numeroAtendimento - Número do Atendimento
	 * @param seqProcedimento - Código do Procedimento interno
	 * @param dthrRealizado - Data e hora da realização
	 * @return Lista de Itens de Conta
	 */
	public List<FatItemContaApac> pesquisarItensProcedimentoPorAtendimentoProcedimentoInternoDataRealizacao(
			Long numeroAtendimento, Integer seqProcedimento, Date dthrRealizado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class, "ICA");
		
		criteria.createAlias("ICA." + FatItemContaApac.Fields.PROCED_HOSP_INTERNO, "PHI");
		
		criteria.add(Restrictions.eq("ICA." + FatItemContaApac.Fields.CAP_ATM_NUMERO, numeroAtendimento));
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.SEQ, seqProcedimento));
		criteria.add(Restrictions.eq("ICA." + FatItemContaApac.Fields.DT_HR_REALIZADO, dthrRealizado));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.ISE_SEQP));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.ISE_SOE_SEQ));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.PRH_CON_NUMERO));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.PRH_PHI_SEQ));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.PPC_EPR_PCI_SEQ));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.PPC_EPR_ESP_SEQ));
		criteria.add(Restrictions.isNull("ICA." + FatItemContaApac.Fields.PPC_IND_RESP_PROC));
		
		return executeCriteria(criteria);
	}
	
	public FatItemContaApac obterOriginalPorPmrSeq(Long pmrSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaApac.class);
		criteria.add(Restrictions.eq(FatItemContaApac.Fields.PMR_SEQ.toString(), pmrSeq));
		
		return (FatItemContaApac) executeCriteriaUniqueResult(criteria);
	}
}
