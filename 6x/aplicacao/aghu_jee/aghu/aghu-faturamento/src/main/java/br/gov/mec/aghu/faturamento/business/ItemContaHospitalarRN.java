package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarJn;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.CyclomaticComplexity"})
@Stateless
public class ItemContaHospitalarRN extends AbstractFatDebugLogEnableRN {
	
	@EJB
	private ContaHospitalarRN contaHospitalarRN;
	
	@EJB
	private FaturamentoFatkIchRN faturamentoFatkIchRN;
	
	private static final Log LOG = LogFactory.getLog(ItemContaHospitalarRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatItemContaHospitalarJnDAO fatItemContaHospitalarJnDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2538537256623946089L;

	private enum ItemContaHospitalarRNExceptionCode implements
			BusinessExceptionCode {
		MPM_00164, MPM_00102, RAP_00111
		, RAP_00175;
	}

	/**
	 * ORADB Trigger FATT_ICH_ARD
	 * 
	 * @param fatItensContaHospitalar
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void executarAposExcluirItemContaHospitalar(
			FatItemContaHospitalar fat) throws ApplicationBusinessException {

		executarInsert(fat, DominioOperacoesJournal.DEL);

	}

	/**
	 * ORADB Trigger FATT_ICH_ARU
	 * 
	 * @param FatItemContaHospitalar
	 *            newFat (entidade atualizada)
	 * @param FatItemContaHospitalar
	 *            oldFat (entidade antes de atualizar)
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void executarAposAtualizarItemContaHospitalar(
			FatItemContaHospitalar newFat, FatItemContaHospitalar oldFat)
			throws ApplicationBusinessException {

		if (oldFat != null) {

			if (CoreUtil.modificados(oldFat.getId().getCthSeq(), newFat.getId()
					.getCthSeq())
					|| CoreUtil.modificados(oldFat.getId().getSeq(), newFat
							.getId().getSeq())
					|| CoreUtil.modificados(
							oldFat.getProcedimentoHospitalarInterno(),
							newFat.getProcedimentoHospitalarInterno())
					|| CoreUtil.modificados(oldFat.getCriadoEm(),
							newFat.getCriadoEm())
					|| CoreUtil.modificados(oldFat.getCriadoPor(),
							newFat.getCriadoPor())
					|| CoreUtil.modificados(oldFat.getAlteradoEm(),
							newFat.getAlteradoEm())
					|| CoreUtil.modificados(oldFat.getValor(),
							newFat.getValor())
					|| CoreUtil.modificados(oldFat.getAlteradoEm(),
							newFat.getAlteradoEm())
					|| CoreUtil.modificados(oldFat.getAgenciaBanco(),
							newFat.getAgenciaBanco())
					|| CoreUtil.modificados(oldFat.getNumRecibo(),
							newFat.getNumRecibo())
					|| CoreUtil.modificados(oldFat.getNumConta(),
							newFat.getNumConta())
					|| CoreUtil.modificados(oldFat.getNumCheque(),
							newFat.getNumCheque())
					|| CoreUtil.modificados(oldFat.getNumCheque(),
							newFat.getNumCheque())
					|| CoreUtil.modificados(oldFat.getSerieCheque(),
							newFat.getSerieCheque())
					|| CoreUtil.modificados(oldFat.getNome(), newFat.getNome())
					|| CoreUtil.modificados(oldFat.getCpf(), newFat.getCpf())
					|| CoreUtil.modificados(oldFat.getIndSituacao(),
							newFat.getIndSituacao())
					|| CoreUtil.modificados(oldFat.getIseSoeSeq(),
							newFat.getIseSoeSeq())
					|| CoreUtil.modificados(oldFat.getIseSeqp(),
							newFat.getIseSeqp())
					|| CoreUtil.modificados(oldFat.getQuantidade(),
							newFat.getQuantidade())
					|| CoreUtil.modificados(oldFat.getIchValor(),
							newFat.getIchValor())
					|| CoreUtil.modificados(oldFat.getQuantidadeRealizada(),
							newFat.getQuantidadeRealizada())
					|| CoreUtil.modificados(oldFat.getIndOrigem(),
							newFat.getIndOrigem())
					|| CoreUtil.modificados(oldFat.getLocalCobranca(),
							newFat.getLocalCobranca())
					|| CoreUtil.modificados(oldFat.getDthrRealizado(),
							newFat.getDthrRealizado())
					|| CoreUtil.modificados(oldFat.getUnidadesFuncional(),
							newFat.getUnidadesFuncional())
					|| CoreUtil.modificados(oldFat.getIndModoCobranca(),
							newFat.getIndModoCobranca())
					|| CoreUtil.modificados(oldFat.getMopMatCodigo(),
							newFat.getMopMatCodigo())
					|| CoreUtil.modificados(oldFat.getMopCrgSeq(),
							newFat.getMopCrgSeq())
					|| CoreUtil.modificados(oldFat.getProcEspPorCirurgias(),
							newFat.getProcEspPorCirurgias())
					|| CoreUtil.modificados(oldFat.getItemRmps(),
							newFat.getItemRmps())
					|| CoreUtil.modificados(oldFat.getIpsIrrRmrSeq(),
							newFat.getIpsIrrRmrSeq())
					|| CoreUtil.modificados(oldFat.getIpsIrrEalSeq(),
							newFat.getIpsIrrEalSeq())
					|| CoreUtil.modificados(
							oldFat.getProcedimentoAmbRealizado(),
							newFat.getProcedimentoAmbRealizado())
					|| CoreUtil.modificados(oldFat.getCmoMcoSeq(),
							newFat.getCmoMcoSeq())
					|| CoreUtil.modificados(oldFat.getCmoEcoBolNumero(),
							newFat.getCmoEcoBolNumero())
					|| CoreUtil.modificados(oldFat.getCmoEcoBolBsaCodigo(),
							newFat.getCmoEcoBolBsaCodigo())
					|| CoreUtil.modificados(oldFat.getCmoEcoCsaCodigo(),
							newFat.getCmoEcoCsaCodigo())
					|| CoreUtil.modificados(oldFat.getCmoSequencia(),
							newFat.getCmoSequencia())
					|| CoreUtil.modificados(oldFat.getCmoEcoSeqp(),
							newFat.getCmoEcoSeqp())
					|| CoreUtil.modificados(oldFat.getPrescricaoProcedimento(),
							newFat.getPrescricaoProcedimento())
					|| CoreUtil.modificados(oldFat.getPrescricaoNpt(),
							newFat.getPrescricaoNpt())
					|| CoreUtil.modificados(oldFat.getPrescricaoPaciente(),
							newFat.getPrescricaoPaciente())
					|| CoreUtil.modificados(oldFat.getPaoSeq(),
							newFat.getPaoSeq())
					|| CoreUtil.modificados(oldFat.getServidor(),
							newFat.getServidor())) {

				executarInsert(oldFat, DominioOperacoesJournal.UPD);
			}
		}

	}

	/**
	 * ORADB Trigger FATT_ICH_BASE_BRI
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param pprAtdSeq
	 * @param pprSeq
	 * @param pnpAtdSeq
	 * @param pnpSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesInserirItemContaHospitalar(Integer iseSoeSeq,
			Short iseSeqp, Integer pprAtdSeq, Long pprSeq, Integer pnpAtdSeq,
			Integer pnpSeq) throws ApplicationBusinessException {

		// -- Testa integridade com AEL_ITEM_SOLICITACAO_EXAMES
		fatpVerItemSolic(iseSoeSeq, iseSeqp);

		// -- Testa integridade com MPM_PRESCRICAO_PROCEDIMENTOS
		fatpVerPrcrProced(pprAtdSeq, pprSeq);

		// -- Testa integridade com MPM_PRESCRICAO_NPTS
		fatpVerPrcrNpts(pnpAtdSeq, pnpSeq);
	}

	/**
	 * ORADB Procedure FATP_VER_ITEM_SOLIC
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void fatpVerItemSolic(Integer iseSoeSeq, Short iseSeqp) 
		throws ApplicationBusinessException {
		if (iseSoeSeq != null && iseSeqp != null) {
			//AelItemSolicitacaoExameDAO dao = getAelItemSolicitacaoExameDAO.class);
			AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId(iseSoeSeq, iseSeqp);
			AelItemSolicitacaoExames resultado = getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(id);
			if (resultado == null) {
				// se nao tiver resultado
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00705);
			}
		}
	}

	/**
	 * ORADB Procedure FATP_VER_PRCR_PROCED
	 * 
	 * @param pprAtdSeq
	 * @param pprSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void fatpVerPrcrProced(Integer pprAtdSeq, Long pprSeq)
			throws ApplicationBusinessException {

		if (pprAtdSeq == null && pprSeq == null) {
			return;
		}
		
		MpmPrescricaoProcedimento resultado = getPrescricaoMedicaFacade().obterProcedimentoPorId(pprAtdSeq, pprSeq);
		if (resultado == null) {
			throw new ApplicationBusinessException(ItemContaHospitalarRNExceptionCode.MPM_00164, pprAtdSeq.toString(), pprSeq.toString());
		}
	}

	/**
	 * ORADB Procedure FATP_VER_PRCR_NPTS
	 * 
	 * @param pnpAtdSeq
	 * @param pnpSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void fatpVerPrcrNpts(Integer pnpAtdSeq, Integer pnpSeq)
			throws ApplicationBusinessException {

		if (pnpAtdSeq == null && pnpSeq == null) {
			return;
		}
		MpmPrescricaoNptId id = new MpmPrescricaoNptId(pnpAtdSeq, pnpSeq);
		MpmPrescricaoNpt resultado = getPrescricaoMedicaFacade()
				.obterPrescricaoNptPorChavePrimaria(id);
		if (resultado == null) {
			throw new ApplicationBusinessException(
					ItemContaHospitalarRNExceptionCode.MPM_00102);
		}

	}

	/**
	 * ORADB Trigger FATT_ICH_BASE_BRU
	 * 
	 * @param oldIseSoeSeq
	 * @param newIseSoeSeq
	 * @param oldIseSeqp
	 * @param newIseSeqp
	 * @param oldPnpAtdSeq
	 * @param newPnpAtdSeq
	 * @param oldPnpSeq
	 * @param newPnpSeq
	 * @param oldPprAtdSeq
	 * @param newPprAtdSeq
	 * @param oldPprSeq
	 * @param newPprSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	public void executarAntesAtualizarItemContaHospitalar(Integer oldIseSoeSeq,
			Integer newIseSoeSeq, Short oldIseSeqp, Short newIseSeqp,
			Integer oldPnpAtdSeq, Integer newPnpAtdSeq, Integer oldPnpSeq,
			Integer newPnpSeq, Integer oldPprAtdSeq, Integer newPprAtdSeq,
			Long oldPprSeq, Long newPprSeq, Boolean pPrevia)
			throws ApplicationBusinessException {
		if (oldIseSoeSeq == null) {
			oldIseSoeSeq = 0;
		}
		if (newIseSoeSeq == null) {
			newIseSoeSeq = 0;
		}
		if (oldIseSeqp == null) {
			oldIseSeqp = 0;
		}
		if (newIseSeqp == null) {
			newIseSeqp = 0;
		}
		if (oldPnpAtdSeq == null) {
			oldPnpAtdSeq = 0;
		}
		if (newPnpAtdSeq == null) {
			newPnpAtdSeq = 0;
		}
		if (oldPnpSeq == null) {
			oldPnpSeq = 0;
		}
		if (newPnpSeq == null) {
			newPnpSeq = 0;
		}
		if (oldPprAtdSeq == null) {
			oldPprAtdSeq = 0;
		}
		if (newPprAtdSeq == null) {
			newPprAtdSeq = 0;
		}
		if (oldPprSeq == null) {
			oldPprSeq = 0l;
		}
		if (newPprSeq == null) {
			newPprSeq = 0l;
		}
		
		// Somente executa trigger quando não for Encerramento
		// pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia)){
			// -- Testa integridade com AEL_ITEM_SOLICITACAO_EXAMES
			if (!CoreUtil.igual(oldIseSoeSeq, newIseSoeSeq)
					|| !CoreUtil.igual(oldIseSeqp, newIseSeqp)) {
				fatpVerItemSolic(newIseSoeSeq, newIseSeqp);
			}
			// -- Testa integridade com MPM_PRESCRICAO_PROCEDIMENTOS
			if (!CoreUtil.igual(oldPprAtdSeq, newPprAtdSeq)
					|| !CoreUtil.igual(oldPprSeq, newPprSeq)) {
				fatpVerPrcrProced(newPprAtdSeq, newPprSeq);
			}
			// -- Testa integridade com MPM_PRESCRICAO_NPTS
			if (!CoreUtil.igual(oldPnpAtdSeq, newPnpAtdSeq)
					|| !CoreUtil.igual(oldPnpSeq, newPnpSeq)) {
				fatpVerPrcrNpts(newPnpAtdSeq, newPnpSeq);
			}
		}
	}

	/**
	 * ORADB Trigger FATT_ICH_BRD
	 * 
	 * @param oldIndSituacao
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void executarAntesExcluirItemContaHospitalar(
			DominioSituacaoItenConta oldIndSituacao)
			throws ApplicationBusinessException {

		if (oldIndSituacao.equals(DominioSituacaoItenConta.P)
				|| oldIndSituacao.equals(DominioSituacaoItenConta.V)
				|| oldIndSituacao.equals(DominioSituacaoItenConta.R)
				|| oldIndSituacao.equals(DominioSituacaoItenConta.N)
				|| oldIndSituacao.equals(DominioSituacaoItenConta.T)) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FATT_ICH_BRD);
		}
	}

	/**
	 * ORADB Trigger FATT_ICH_BRI
	 * 
	 * @param newFatItemContaHosp
	 * @param servidorLogado
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void executarAntesInserirItemContaHospitalar(
			FatItemContaHospitalar newFatItemContaHosp, RapServidores servidorLogado, final Date dataFimVinculoServidor, final Boolean pPrevia) throws BaseException {

		FatProcedHospInternosDAO fatProcedHospInternosDAO = getFatProcedHospInternosDAO();
		FatProcedHospInternos fatProcedHospInternos;
		Integer vIdade = 0;

		/*
		 * Essa parte do código-fonte não será utilizada String vMensagem,
		 * vAssunto;
		 * 
		 * if (cmoEcoSeqp == null && cmoEcoBolNumero != null) { vAssunto =
		 * "TRIGGER FATURAMENTO - BANCO DE SANGUE"; vMensagem =
		 * DateUtil.obterDataFormatada(new Date(), "dd/MM/yyy HH:mm:ss");
		 * vMensagem += " - Conta: " + cthSeq.toString() + " - " +
		 * seq.toString() + " Bolsa: "; vMensagem += cmoEcoBolNumero + " - " +
		 * cmoEcoBolBsaCodigo + " - "; vMensagem += cmoEcoBolData + " - " +
		 * cmoEcoCsaCodigo + " - "; vMensagem += cmoEcoSeqp + " - " +
		 * cmoSequencia;
		 * 
		 * EmailUtil email = new EmailUtil();
		 * email.enviaEmail("RFOSSATI@HCPA.UFRGS.BR", "RFOSSATI@HCPA.UFRGS.BR",
		 * "", vAssunto, vMensagem); }
		 */
		
		// Somente executa trigger quando não for Encerramento
		// pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia)){
			/* ATRIBUI VALOR A IND_SITUACAO E LOCAL_COBRANCA */
			if (newFatItemContaHosp.getIndSituacao() == null) {
				newFatItemContaHosp.setIndSituacao(DominioSituacaoItenConta.A);
			}
			if (newFatItemContaHosp.getLocalCobranca() == null) {
				newFatItemContaHosp.setLocalCobranca(DominioLocalCobranca.I);
			}
	
			/*
			 * VERIFICA SE O PHI ESTÁ ATIVO SO VERIFICA PHI ATIVO PARA CTH NAO
			 * DESDOBR E NAO REAPRES
			 */
			if (!getContaHospitalarRN().rnCthcVerReapres(
					newFatItemContaHosp.getId().getCthSeq())
					&& !getContaHospitalarRN().rnCthcVerDesdobr(
							newFatItemContaHosp.getId().getCthSeq())) {
				if (!rnPhicVerAtivo(newFatItemContaHosp
						.getProcedimentoHospitalarInterno().getSeq())) {
					throw new ApplicationBusinessException(
							FaturamentoExceptionCode.FAT_00625);
				}
			}
	
			/* VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO */
			getContaHospitalarRN()
					.verificarFiltroConvPlano(
							getFatContasHospitalaresDAO().obterPorChavePrimaria(
									newFatItemContaHosp.getId().getCthSeq()),
							null,
							null,
							null,
							null,
							newFatItemContaHosp.getProcedimentoHospitalarInterno()
									.getSeq(), null, null,
							newFatItemContaHosp.getIndOrigem().toString());
	
			// -- verifica se a data de realizacao do item esta dentro do periodo da
			// conta
			if (newFatItemContaHosp.getDthrRealizado() != null
					&& newFatItemContaHosp.getIndSituacao().equals(
							DominioSituacaoItenConta.A)) {
				getFaturamentoFatkIchRN().rnIchpVerDtrealiz(
						newFatItemContaHosp.getId().getCthSeq(),
						newFatItemContaHosp.getDthrRealizado(),
						newFatItemContaHosp.getIndOrigem());
			}
			/*-- verifica se o item que esta sendo cancelado nao e' o proced realizado da conta*/
			if (newFatItemContaHosp.getProcedimentoHospitalarInterno().getSeq() != null
					&& newFatItemContaHosp.getIndSituacao().equals(
							DominioSituacaoItenConta.C)) {
				getFaturamentoFatkIchRN().rnIchpVerPhiReal(
						newFatItemContaHosp.getId().getCthSeq(),
						newFatItemContaHosp.getProcedimentoHospitalarInterno()
								.getSeq());
			}
			// gera chave primaria se for null
			if (newFatItemContaHosp.getId().getSeq() == null) {
				newFatItemContaHosp.getId().setSeq(
						getFatItemContaHospitalarDAO().obterProximoSeq(
								newFatItemContaHosp.getId().getCthSeq()));
			}
		}
		newFatItemContaHosp.setCriadoEm(new Date());
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (newFatItemContaHosp.getCriadoPor() == null
				|| newFatItemContaHosp.getCriadoPor().equals("")) {
			newFatItemContaHosp.setCriadoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		}
		/*--Marina 23/06/2008
		 dados do servidor que digita 
		:new.ser_vin_codigo_criado  := agh_config.get_vinculo;
		:new.ser_matricula_criado   := agh_config.get_matricula */
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		newFatItemContaHosp.setServidorCriado(servidorLogado);
		if (newFatItemContaHosp.getServidorCriado() == null
				|| newFatItemContaHosp.getServidorCriado().getId()
						.getMatricula() == null) {
			throw new ApplicationBusinessException(
					ItemContaHospitalarRNExceptionCode.RAP_00175);
		}
		if (DominioItemConsultoriaSumarios.ACH.equals(newFatItemContaHosp
				.getIchType())) {

			Integer vVlrNumerico = null;
			
			vVlrNumerico = this
					.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_PHI_ADIANT_CONTA);
			
			fatProcedHospInternosDAO = getFatProcedHospInternosDAO();
			fatProcedHospInternos = fatProcedHospInternosDAO
					.obterPorChavePrimaria(vVlrNumerico);

			newFatItemContaHosp
					.setProcedimentoHospitalarInterno(fatProcedHospInternos);
			newFatItemContaHosp
					.setIndOrigem(DominioIndOrigemItemContaHospitalar.AIN);
		}
		if (DominioItemConsultoriaSumarios.ACH.equals(newFatItemContaHosp
				.getIchType())) {
			if (newFatItemContaHosp.getCpf() != null
					&& newFatItemContaHosp.getNome() == null) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00120);
			}
		}

		Integer phiSeqNutEntNeonat = this
				.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_NEONATOLOGIA);
		Integer phiSeqNutEntPediat = this
				.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_PEDIATRIA);
		Integer phiSeqNutEntAdult = this
				.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_ADULTO);

		if (newFatItemContaHosp.getProcedimentoHospitalarInterno() != null) { // verifica
																				// se
																				// não
																				// está
																				// nullo
			if (newFatItemContaHosp.getProcedimentoHospitalarInterno().getSeq() == phiSeqNutEntNeonat
					|| newFatItemContaHosp.getProcedimentoHospitalarInterno()
							.getSeq() == phiSeqNutEntPediat
					|| newFatItemContaHosp.getProcedimentoHospitalarInterno()
							.getSeq() == phiSeqNutEntAdult) {

				vIdade = fatcBuscaIdadePac(newFatItemContaHosp.getId()
						.getCthSeq());
				if (vIdade == 0) {
					fatProcedHospInternos = fatProcedHospInternosDAO
							.obterPorChavePrimaria(phiSeqNutEntNeonat);
					newFatItemContaHosp
							.setProcedimentoHospitalarInterno(fatProcedHospInternos);
				} else if (vIdade < 13) {
					fatProcedHospInternos = fatProcedHospInternosDAO
							.obterPorChavePrimaria(phiSeqNutEntPediat);
					newFatItemContaHosp
							.setProcedimentoHospitalarInterno(fatProcedHospInternos);
				} else {
					fatProcedHospInternos = fatProcedHospInternosDAO
							.obterPorChavePrimaria(phiSeqNutEntAdult);
					newFatItemContaHosp
							.setProcedimentoHospitalarInterno(fatProcedHospInternos);
				}
			}

			Integer phiSeqNutParPediat = this
					.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_PARENTERAL_PEDIATRICA);
			Integer phiSeqNutParNeonat = this
					.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_PARENTERAL_NEONATOLOGIA);
			Integer phiSeqNutParAdulto = this
					.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_PARENTERAL_ADULTO);

			if (newFatItemContaHosp.getProcedimentoHospitalarInterno().getSeq() == phiSeqNutParPediat
					|| newFatItemContaHosp.getProcedimentoHospitalarInterno()
							.getSeq() == phiSeqNutParNeonat
					|| newFatItemContaHosp.getProcedimentoHospitalarInterno()
							.getSeq() == phiSeqNutParAdulto) {

				Integer matrRespFarmVinc = this
						.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_SER_MATRICULA_RESP_FARMACEUTICO_RESPONSAVEL_VINCULO);
				Short vinCodRespMatr = this
						.buscarVlrShortAghParametro(AghuParametrosEnum.P_SER_VIN_CODIGO_RESP_FARMACEUTICO_RESPONSAVEL_MATRICULA);

				RapServidoresId id = new RapServidoresId(matrRespFarmVinc,
						vinCodRespMatr);
				RapServidores rapServidores = getRegistroColaboradorFacade()
						.obterRapServidoresPorChavePrimaria(id);
				newFatItemContaHosp.setServidor(rapServidores);
			}
		}
	}

	/**
	 * ORADB Function FATC_BUSCA_IDADE_PAC
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Integer fatcBuscaIdadePac(Integer cthSeq) {

		VFatContaHospitalarPacDAO fatContaDAO = getVFatContaHospitalarPacDAO();
		VFatContaHospitalarPac vFatCtnHosp = fatContaDAO
				.obterPorChavePrimaria(cthSeq);

		if (vFatCtnHosp.getPaciente() == null) {
			return null;
		}
		AipPacientes aipPac = getPacienteFacade()
				.obterAipPacientesPorChavePrimaria(
						vFatCtnHosp.getPaciente().getCodigo());

		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(
				DateUtil.truncaData(aipPac.getDtNascimento()),
				DateUtil.truncaData(vFatCtnHosp.getCthDtIntAdministrativa()));

		Integer meses = DateUtil.obterQtdMesesEntreDuasDatas(
				DateUtil.truncaData(aipPac.getDtNascimento()),
				DateUtil.truncaData(vFatCtnHosp.getCthDtIntAdministrativa())) % 12;

		Integer idade;

		if (anos == 0) {
			if (meses == 0) {
				idade = 0;
			} else {
				idade = 1;
			}
		} else {
			idade = anos;
		}
		return idade;
	}

	/**
	 * ORADB Tigger FATT_ICH_BRU
	 * 
	 * @param newFatItemContaHosp
	 * 
	 * @throws BaseException
	 */
	public void executarAntesAtualizarItemContaHospitalar(
			FatItemContaHospitalar newFatItemContaHosp, final Date dataFimVinculoServidor, final Boolean pPrevia) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Somente executa trigger quando não for Encerramento
		// pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia)){
			getContaHospitalarRN()
					.verificarFiltroConvPlano(
							getFatContasHospitalaresDAO().obterPorChavePrimaria(
									newFatItemContaHosp.getId().getCthSeq()),
							null,
							null,
							null,
							null,
							newFatItemContaHosp.getProcedimentoHospitalarInterno()
									.getSeq(), null, null,
							newFatItemContaHosp.getIndOrigem().toString());
			logDebug(
					"phi "
							+ newFatItemContaHosp
									.getProcedimentoHospitalarInterno().getSeq());
			if (newFatItemContaHosp.getDthrRealizado() != null
					&& newFatItemContaHosp.getIndSituacao().equals(
							DominioSituacaoItenConta.A)) {
				getFaturamentoFatkIchRN().rnIchpVerDtrealiz(
						newFatItemContaHosp.getId().getCthSeq(),
						newFatItemContaHosp.getDthrRealizado(),
						newFatItemContaHosp.getIndOrigem());
			}
			if (newFatItemContaHosp.getProcedimentoHospitalarInterno().getSeq() != null
					&& newFatItemContaHosp.getIndOrigem().equals(
							DominioSituacaoItenConta.C)) {
				getFaturamentoFatkIchRN().rnIchpVerPhiReal(
						newFatItemContaHosp.getId().getCthSeq(),
						newFatItemContaHosp.getProcedimentoHospitalarInterno()
								.getSeq());
			}
			if (newFatItemContaHosp.getIchType().equals(
					DominioItemConsultoriaSumarios.ACH)
					&& newFatItemContaHosp.getCpf() != null
					&& newFatItemContaHosp.getNome() == null) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00120);
			}
		}
		
		newFatItemContaHosp.setAlteradoEm(new Date());
		newFatItemContaHosp.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		newFatItemContaHosp.setServidoresAlterado(servidorLogado);

		/*
		 * fatp_atu_ich_cirg_bull está comentado, logo o código-fonte abaixo não
		 * faz nada
		 * 
		 * atualiza faturamento no bull --- FGi 31/07 para o desligamento do
		 * Bull if :new.ind_situacao = 'C' then fatp_atu_ich_cirg_bull
		 * (:new.cth_seq, :new.ppc_crg_seq, :new.phi_seq, :new.dthr_realizado,
		 * 'D'); end if; if :new.ind_situacao = 'A' and :old.ind_situacao = 'C'
		 * then fatp_atu_ich_cirg_bull (:new.cth_seq, :new.ppc_crg_seq,
		 * :new.phi_seq, :new.dthr_realizado, 'I'); end if;
		 */
	}

	/**
	 * ORADB Procedure FATK_CTH_RN.RN_CTHC_VER_REAPRES
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public boolean rnCthcVerReapres(Integer pSeq)
			throws ApplicationBusinessException {
		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		FatContasHospitalares fat = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pSeq);
		if (fat == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ORADB Procedure FATK_PHI_RN.RN_PHIC_VER_ATIVO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public boolean rnPhicVerAtivo(Integer pSeq)
			throws ApplicationBusinessException {

		FatProcedHospInternosDAO fatProcedHospInternosDAO = getFatProcedHospInternosDAO();
		FatProcedHospInternos fat = fatProcedHospInternosDAO
				.obterPorChavePrimaria(pSeq);
		return fat.getSituacao().isAtivo();
	}

	/**
	 * Executa o Insert na entidade FatItemContaHospitalarJn
	 * 
	 * @param fatItensContaHospitalar
	 * @param operacao
	 *            = DominioOperacoesJournal.DEL, DominioOperacoesJournal.INS,
	 *            DominioOperacoesJournal.UPD
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void executarInsert(FatItemContaHospitalar fat,
			DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		FatItemContaHospitalarJnDAO itemContaHopitalarJnDAO = getFatItemContaHospitalarJnDAO();

		FatItemContaHospitalarJn fatItemContaHospitalarJn = new FatItemContaHospitalarJn();

		fatItemContaHospitalarJn.setNomeUsuario(servidorLogado.getUsuario());

		fatItemContaHospitalarJn.setOperacao(operacao);

		fatItemContaHospitalarJn.setCthSeq(fat.getId().getCthSeq());
		fatItemContaHospitalarJn.setSeq(fat.getId().getSeq());
		fatItemContaHospitalarJn.setIchType(fat.getIchType());
		if (fat.getProcedimentoHospitalarInterno() != null) {
			fatItemContaHospitalarJn.setPhiSeq(fat
					.getProcedimentoHospitalarInterno().getSeq());
		}
		fatItemContaHospitalarJn.setCriadoEm(fat.getCriadoEm());
		fatItemContaHospitalarJn.setCriadoPor(fat.getCriadoPor());
		fatItemContaHospitalarJn.setAlteradoPor(fat.getAlteradoPor());
		fatItemContaHospitalarJn.setAlteradoEm(fat.getAlteradoEm());
		fatItemContaHospitalarJn.setValor(fat.getValor());
		if (fat.getAgenciaBanco() != null) {
			fatItemContaHospitalarJn.setAgbBcoCodigo(fat.getAgenciaBanco()
					.getId().getBcoCodigo());
			fatItemContaHospitalarJn.setAgbCodigo(fat.getAgenciaBanco().getId()
					.getCodigo());
		}
		fatItemContaHospitalarJn.setNumRecibo(fat.getNumRecibo());
		fatItemContaHospitalarJn.setNumConta(fat.getNumConta());
		fatItemContaHospitalarJn.setNumCheque(fat.getNumCheque());
		fatItemContaHospitalarJn.setSerieCheque(fat.getSerieCheque());
		fatItemContaHospitalarJn.setNome(fat.getNome());
		fatItemContaHospitalarJn.setCpf(fat.getCpf());
		fatItemContaHospitalarJn.setIndSituacao(fat.getIndSituacao());
		fatItemContaHospitalarJn.setIseSoeSeq(fat.getIseSoeSeq());
		fatItemContaHospitalarJn.setIseSeqp(fat.getIseSeqp());
		fatItemContaHospitalarJn.setQuantidade(fat.getQuantidade());
		fatItemContaHospitalarJn.setIchValor(fat.getIchValor());
		fatItemContaHospitalarJn.setQuantidadeRealizada(fat
				.getQuantidadeRealizada());
		fatItemContaHospitalarJn.setIndOrigem(fat.getIndOrigem());
		fatItemContaHospitalarJn.setLocalCobranca(fat.getLocalCobranca());
		fatItemContaHospitalarJn.setDthrRealizado(fat.getDthrRealizado());
		if (fat.getUnidadesFuncional() != null) {
			fatItemContaHospitalarJn.setUnfSeq(fat.getUnidadesFuncional()
					.getSeq());
		}
		fatItemContaHospitalarJn.setIndModoCobranca(fat.getIndModoCobranca());
		fatItemContaHospitalarJn.setMopMatCodigo(fat.getMopMatCodigo());
		fatItemContaHospitalarJn.setMopCrgSeq(fat.getMopCrgSeq());
		if (fat.getProcEspPorCirurgias() != null) {
			if (fat.getProcEspPorCirurgias().getCirurgia() != null) {
				fatItemContaHospitalarJn.setPpcCrgSeq(fat
						.getProcEspPorCirurgias().getCirurgia().getSeq());
			}
			if (fat.getProcEspPorCirurgias().getProcedimentoCirurgico() != null) {
				fatItemContaHospitalarJn.setPpcEprPciSeq(fat
						.getProcEspPorCirurgias().getProcedimentoCirurgico()
						.getSeq());
			}
			if (fat.getProcEspPorCirurgias().getMbcEspecialidadeProcCirgs() != null) {
				fatItemContaHospitalarJn.setPpcEprEspSeq(fat
						.getProcEspPorCirurgias()
						.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
			}
			fatItemContaHospitalarJn.setPpcIndRespProc(fat
					.getProcEspPorCirurgias().getId().getIndRespProc());
		}
		if (fat.getItemRmps() != null) {
			fatItemContaHospitalarJn.setIpsRmpSeq(fat.getItemRmps().getId()
					.getRmpSeq());
		}
		fatItemContaHospitalarJn.setIpsIrrRmrSeq(fat.getIpsIrrRmrSeq());
		fatItemContaHospitalarJn.setIpsIrrEalSeq(fat.getIpsIrrEalSeq());
		if (fat.getItemRmps() != null) {
			fatItemContaHospitalarJn.setIpsNumero(fat.getItemRmps().getId()
					.getNumero());
		}
		if (fat.getProcedimentoAmbRealizado() != null) {
			fatItemContaHospitalarJn.setPmrSeq(fat
					.getProcedimentoAmbRealizado().getSeq());
		}
		fatItemContaHospitalarJn.setCmoMcoSeq(fat.getCmoMcoSeq());
		fatItemContaHospitalarJn.setCmoEcoBolNumero(fat.getCmoEcoBolNumero());
		fatItemContaHospitalarJn.setCmoEcoBolBsaCodigo(fat
				.getCmoEcoBolBsaCodigo());
		fatItemContaHospitalarJn.setCmoEcoCsaCodigo(fat.getCmoEcoCsaCodigo());
		fatItemContaHospitalarJn.setCmoSequencia(fat.getCmoSequencia());
		fatItemContaHospitalarJn.setCmoEcoSeqp(fat.getCmoEcoSeqp());
		if (fat.getPrescricaoProcedimento() != null) {
			fatItemContaHospitalarJn.setPprAtdSeq(fat
					.getPrescricaoProcedimento().getId().getSeq().intValue());
			fatItemContaHospitalarJn.setPprSeq(fat.getPrescricaoProcedimento()
					.getId().getSeq().intValue());
		}
		if (fat.getPrescricaoNpt() != null) {
			/*
			 * Voltado a versão de utilizar "getId().getAtdSeq()" pois
			 * referenciar o atendimento da prescrição médica não está
			 * funcionando para todos os casos no oracle.
			 */
			fatItemContaHospitalarJn.setPnpAtdSeq(fat.getPrescricaoNpt()
					.getId().getAtdSeq());
			fatItemContaHospitalarJn.setPteAtdSeq(fat.getPrescricaoNpt()
					.getId().getSeq());
		}
		if (fat.getPrescricaoPaciente() != null) {
			fatItemContaHospitalarJn.setPteSeq(fat.getPrescricaoPaciente()
					.getId().getSeq());
		}
		fatItemContaHospitalarJn.setPaoSeq(fat.getPaoSeq());
		if (fat.getServidor() != null) {
			fatItemContaHospitalarJn.setSerMatriculaResp(fat.getServidor()
					.getId().getMatricula());
			fatItemContaHospitalarJn.setSerVinCodigoResp(fat.getServidor()
					.getId().getVinCodigo());
		}

		itemContaHopitalarJnDAO.persistir(fatItemContaHospitalarJn); 
		itemContaHopitalarJnDAO.flush();
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected FatItemContaHospitalarJnDAO getFatItemContaHospitalarJnDAO() {
		return fatItemContaHospitalarJnDAO;
	}

	

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	

	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	protected VFatContaHospitalarPacDAO getVFatContaHospitalarPacDAO() {
		return vFatContaHospitalarPacDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	protected FaturamentoFatkIchRN getFaturamentoFatkIchRN() {
		return faturamentoFatkIchRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
