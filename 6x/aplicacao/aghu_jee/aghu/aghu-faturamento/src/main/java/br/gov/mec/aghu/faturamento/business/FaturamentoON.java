package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioMensagemEstornoAIH;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatPendenciaContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimento2DAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContasHospPacientesDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.DesdobrarContaHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.FatcVerCarPhiCnvVO;
import br.gov.mec.aghu.faturamento.vo.FatpTabCodBarrasVO;
import br.gov.mec.aghu.faturamento.vo.FatpTabCodeVO;
import br.gov.mec.aghu.faturamento.vo.ListarContasHospPacientesPorPacCodigoVO;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatDiariaUtiDigitada;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatMotivoPendencia;
import br.gov.mec.aghu.model.FatPendenciaContaHosp;
import br.gov.mec.aghu.model.FatPendenciaContaHospId;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.NcssTypeCount", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class FaturamentoON extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@EJB
	private FaturamentoRN faturamentoRN;

	@EJB
	private ContaHospitalarON contaHospitalarON;

	@EJB
	private ItemContaHospitalarON itemContaHospitalarON;

	private static final Log LOG = LogFactory.getLog(FaturamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private FatPendenciaContaHospDAO fatPendenciaContaHospDAO;
	
	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private EmailUtil emailUtil;

	@Inject
	private FatAihDAO fatAihDAO;

	@Inject
	private FatLogErrorDAO fatLogErrorDAO;

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;

	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;

	@Inject
	private VFatAssociacaoProcedimento2DAO vFatAssociacaoProcedimento2DAO;

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;

	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;

	@Inject
	private VFatContasHospPacientesDAO vFatContasHospPacientesDAO;

	@Inject
	private FatArqEspelhoProcedAmbDAO fatArqEspelhoProcedAmbDAO;

	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	@Inject
	private FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO;
	
	@EJB
	ProdutividadeFisiatriaRN produtividadeFisiatriaRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6552109995019034561L;

	private enum FaturamentoONExceptionCode implements BusinessExceptionCode {
		INFORME_DATA_DESDOBRAMENTO, ERRO_EXECUTAR_DESDOBRAMENTO, ERRO_NAO_FOI_POSSIVEL_EFETUAR_DESDOBRAMENTO, ERRO_NAO_FOI_POSSIVEL_ESTORNAR_DESDOBRAMENTO_CONTA, CONVENIO_PLANO_PROCEDIMENTO_INCOMPATIVEIS, COMPETENCIA_NAO_SELECIONADA, COMPETENCIA_SEM_DATA_FINAL;
	}

	/**
	 * Método para inserir uma nova conta hospitalar no banco de dados.
	 * 
	 * @param contaHospitalar
	 * @throws BaseException
	 */
	public void inserirContaInternacao(FatContasInternacao contaInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getFaturamentoRN().inserirContaInternacao(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Método para incluir um objeto FatCidsContaHospitalar
	 * 
	 * @param cidContaHospitalar
	 * @param dataFimVinculoServidor
	 * @throws BaseException
	 */
	public void persistirCidContaHospitalar(FatCidContaHospitalar cidContaHospitalar, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		this.getFaturamentoRN().persistirCidContaHospitalar(cidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public List<FatProcedHospInternos> pesquisarFatProcedHospInternos(Integer codigoMaterial, Integer seqProcedimentoCirurgico,
			Short seqProcedimentoEspecialDiverso) {
		return this.getFaturamentoRN().pesquisarFatProcedHospInternos(codigoMaterial, seqProcedimentoCirurgico,
				seqProcedimentoEspecialDiverso);
	}

	public List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUS(Integer seqAtendimento)
			throws ApplicationBusinessException {
		return this.getFaturamentoRN().buscarJustificativasLaudoProcedimentoSUS(seqAtendimento);
	}
	
	public List<SubRelatorioMudancaProcedimentosVO> buscarMudancaProcedimentos(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		return this.getFaturamentoRN().buscarMudancaProcedimentos(seqAtendimento, apaSeq, seqp);
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public void criarFatLogErrors(String erro, String modulo, Integer cthSeq, Long capAtmNumero, Byte capSeqp, Long codItemSus1,
			Long codItemSus2, Date dataPrevia, Byte icaSeqp, Short ichSeqp, Short iphPhoSeq, Short iphPhoSeqRealizado, Integer iphSeq,
			Integer iphSeqRealizado, Integer pacCodigo, Integer phiSeq, Integer phiSeqRealizado, Long pmrSeq, Integer prontuario,
			Long codItemSusRealizado, Long codItemSusSolicitado, Short iphPhoSeqItem1, Short iphPhoSeqItem2, Integer iphSeqItem1,
			Integer iphSeqItem2, Integer phiSeqItem1, Integer phiSeqItem2, String programa, Short serVinCodigoProf, Integer serMatriculaProf,
			FatMensagemLog fatMensagemLog) {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if(fatMensagemLog != null){
				fatMensagemLog = getFatMensagemLogDAO().obterFatMensagemLogPorCodigo(fatMensagemLog.getCodigo());
			}
			
			FatLogError fatLogError = new FatLogError(new Date(), servidorLogado.getUsuario(), erro, modulo, cthSeq, capAtmNumero, capSeqp,
					codItemSus1, codItemSus2, dataPrevia, icaSeqp, ichSeqp, iphPhoSeq, iphPhoSeqRealizado, iphSeq, iphSeqRealizado,
					pacCodigo, phiSeq, phiSeqRealizado, pmrSeq, prontuario, codItemSusRealizado, codItemSusSolicitado, iphPhoSeqItem1,
					iphPhoSeqItem2, iphSeqItem1, iphSeqItem2, phiSeqItem1, phiSeqItem2, programa, serVinCodigoProf, serMatriculaProf,fatMensagemLog);
			
			getFaturamentoFacade().persistirLogError(fatLogError);
		} catch (Exception e) {
			logDebug("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: " + e.getMessage());
		}
	}
	
	/**
	 * Busca leito da ultima internação
	 * 
	 * @param contaHospitalar
	 * @return
	 */
	public String buscaLeito(FatContasHospitalares contaHospitalar) {
		if (contaHospitalar == null) {
			return null;
		}
		if (!fatContasHospitalaresDAO.contains(contaHospitalar)){
			contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(contaHospitalar.getSeq());
			//contaHospitalar = fatContasHospitalaresDAO.merge(contaHospitalar);
		}
		Date dataHora = null;
		String leito = null;
		for (FatContasInternacao contaInternacao : contaHospitalar.getContasInternacao()) {
			Date dataHoraInternacao = contaInternacao.getInternacao() != null ? contaInternacao.getInternacao().getDthrInternacao() : null;
			if (dataHora == null || dataHora.before(dataHoraInternacao)) {
				dataHora = dataHoraInternacao;
				leito = (contaInternacao.getInternacao() != null && contaInternacao.getInternacao().getLeito() != null) ? contaInternacao
						.getInternacao().getLeito().getLeitoID() : null;
			}
		}
		return leito;
	}

	public void desdobrarContaHospitalar(Integer cthSeq, FatMotivoDesdobramento motivoDesdobramento, Date dataHoraDesdobramento,
			Boolean contaConsideradaReapresentada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if (dataHoraDesdobramento == null) {
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.INFORME_DATA_DESDOBRAMENTO);
		}

		DesdobrarContaHospitalarVO voRetorno = null;
		try {
			voRetorno = this.getFaturamentoRN().desdobrarContaHospitalar(cthSeq, motivoDesdobramento, dataHoraDesdobramento,
					contaConsideradaReapresentada, nomeMicrocomputador, dataFimVinculoServidor);
		} catch (Exception e) {
			logError("Erro ao executar desdobramento", e);
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.ERRO_EXECUTAR_DESDOBRAMENTO, e.getMessage());
		}

		if (Boolean.FALSE.equals(voRetorno.getRetorno())) {
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.ERRO_NAO_FOI_POSSIVEL_EFETUAR_DESDOBRAMENTO,
					voRetorno.getMensagem());
		}
	}

	public void estornarDesdobramento(Integer cthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		Boolean ok = this.fatcRnCthcAtuEstDesd(cthSeq, nomeMicrocomputador, dataFimVinculoServidor);

		if (Boolean.FALSE.equals(ok)) {
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.ERRO_NAO_FOI_POSSIVEL_ESTORNAR_DESDOBRAMENTO_CONTA);
		}
	}

	/**
	 * ORADB PLL FUNCTION FATC_RN_CTHC_ATU_EST_DESD
	 * 
	 * @param pCthSeq
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Boolean fatcRnCthcAtuEstDesd(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		Boolean flag = Boolean.FALSE;
		Boolean flagPend = Boolean.FALSE;
		FatContasHospitalares pCth = getFaturamentoFacade().obterContaHospitalar(pCthSeq);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final FaturamentoRN faturamentoRN = this.getFaturamentoRN();
		if (pCth == null || CoreUtil.notIn(pCth.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)) {
			return false;
		}

		FatContasHospitalares vCth = getFaturamentoFacade().buscarCthGerada(pCthSeq);
		Integer vCthSeq = null;
		Date vDtAltaAdm = null;
		if (vCth != null) {
			vCthSeq = vCth.getSeq();
			vDtAltaAdm = vCth.getDtAltaAdministrativa();
		} else {
			return false;
		}

		FatContasHospitalares pCthOld = null;
		try {
			pCthOld = getFaturamentoFacade().clonarContaHospitalar(pCth);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
		}

		FatContasHospitalares vCthOld = null;
		try {
			vCthOld = getFaturamentoFacade().clonarContaHospitalar(vCth);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
		}

		getFatEspelhoItemContaHospDAO().removerPorContaHospitalar(vCthSeq);
		getFatAtoMedicoAihDAO().removerPorCth(vCthSeq);
		getFatCampoMedicoAuditAihDAO().removerPorCth(vCthSeq);
		getFatEspelhoAihDAO().removerPorCth(vCth.getSeq());
		this.flush();

		if (vCth.getValorContaHospitalar() != null) {
			faturamentoRN.excluirFatValorContaHospitalar(vCth.getValorContaHospitalar());
			vCth.setValorContaHospitalar(null);
		}

		if (vCth.getPerdaItensConta() != null && !vCth.getPerdaItensConta().isEmpty()) {
			for (FatPerdaItemConta fatPerdaItemConta : vCth.getPerdaItensConta()) {
				faturamentoRN.excluirFatPerdaItemConta(fatPerdaItemConta);
				// getFaturamentoFacade().evict(fatPerdaItemConta);
			}
			vCth.getPerdaItensConta().clear();
		}

		if (vCth.getContaSugestaoDesdobrs() != null && !vCth.getContaSugestaoDesdobrs().isEmpty()) {
			for (FatContaSugestaoDesdobr fatContaSugestaoDesdobr : vCth.getContaSugestaoDesdobrs()) {
				faturamentoRN.excluirFatContaSugestaoDesdobr(fatContaSugestaoDesdobr);
			}
			vCth.getContaSugestaoDesdobrs().clear();
		}

		if (vCth.getAihs() != null && !vCth.getAihs().isEmpty()) {
			for (FatAih fatAih : vCth.getAihs()) {
				FatAih fatAihOld = null;
				try {
					fatAihOld = faturamentoRN.clonarFatAih(fatAih);
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}
				fatAih.setContaHospitalar(null);
				fatAih.setIndSituacao(DominioSituacaoAih.U);
				faturamentoRN.atualizarFatAih(fatAih, fatAihOld);
			}
			vCth.getAihs().clear();
		}

		pCth.setMotivoDesdobramento(null);
		pCth.setDtAltaAdministrativa(vDtAltaAdm);
		pCth.setIndSituacao(vDtAltaAdm == null ? DominioSituacaoConta.A : DominioSituacaoConta.F);
		getFaturamentoFacade().persistirContaHospitalar(pCth, pCthOld, false, nomeMicrocomputador, dataFimVinculoServidor);

		pCth = getFaturamentoFacade().obterContaHospitalar(pCthSeq);
		pCthOld = null;
		try {
			pCthOld = getFaturamentoFacade().clonarContaHospitalar(pCth);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
		}

		// Marina 28/02/2011
		if (vCth.getDiariaUtiDigitada() != null) {
			FatDiariaUtiDigitada newDiaria = faturamentoRN.clonarFatDiariaUtiDigitada(vCth.getDiariaUtiDigitada());
			newDiaria.setCthSeq(pCthSeq);
			newDiaria.setContaHospitalar(pCth);
			faturamentoRN.removerFatDiariaUtiDigitada(vCth.getDiariaUtiDigitada(), false);
			vCth.setDiariaUtiDigitada(null);
			faturamentoRN.inserirFatDiariaUtiDigitada(newDiaria, false);
			pCth.setDiariaUtiDigitada(newDiaria);

		}

		Short vMaxItem = getFatItemContaHospitalarDAO().obterProximoSeq(pCthSeq);
		List<FatItemContaHospitalar> listIchGerada = getFaturamentoFacade().listarIchGerada(vCthSeq);
		if (listIchGerada != null && !listIchGerada.isEmpty()) {
			for (FatItemContaHospitalar rIchGer : listIchGerada) {
				flag = Boolean.FALSE;

				List<FatItemContaHospitalar> listIchDesdobrada = getFaturamentoFacade().listarIchDesdobrada(pCthSeq);
				if (listIchDesdobrada != null && !listIchDesdobrada.isEmpty()) {
					for (FatItemContaHospitalar rIchDes : listIchDesdobrada) {
						if (CoreUtil.igual(Boolean.TRUE, flag)) {
							break;
						}

						if (CoreUtil.igual(rIchGer.getQuantidadeRealizada(), rIchDes.getQuantidadeRealizada())
								&& CoreUtil.igual(rIchGer.getIndOrigem(), rIchDes.getIndOrigem())
								&& CoreUtil.igual(rIchGer.getDthrRealizado(), rIchDes.getDthrRealizado())
								&& CoreUtil.igual(rIchGer.getProcedimentoHospitalarInterno(), rIchDes.getProcedimentoHospitalarInterno())
								&& CoreUtil.igual(rIchGer.getUnidadesFuncional(), rIchDes.getUnidadesFuncional())
								&& CoreUtil.igual(rIchGer.getIseSoeSeq(), rIchDes.getIseSoeSeq())
								&& CoreUtil.igual(rIchGer.getIseSeqp(), rIchDes.getIseSeqp())
								&& CoreUtil.igual(rIchGer.getMopCrgSeq(), rIchDes.getMopCrgSeq())
								&& CoreUtil.igual(rIchGer.getMopMatCodigo(), rIchDes.getMopMatCodigo())
								&& CoreUtil.igual(rIchGer.getProcEspPorCirurgias(), rIchDes.getProcEspPorCirurgias())
								&& CoreUtil.igual(rIchGer.getItemRmps(), rIchDes.getItemRmps())
								&& CoreUtil.igual(rIchGer.getCmoMcoSeq(), rIchDes.getCmoMcoSeq())
								&& CoreUtil.igual(rIchGer.getCmoEcoBolNumero(), rIchDes.getCmoEcoBolNumero())
								&& CoreUtil.igual(rIchGer.getCmoEcoBolBsaCodigo(), rIchDes.getCmoEcoBolBsaCodigo())
								&& CoreUtil.igual(rIchGer.getCmoEcoBolData(), rIchDes.getCmoEcoBolData())
								&& CoreUtil.igual(rIchGer.getCmoEcoCsaCodigo(), rIchDes.getCmoEcoCsaCodigo())
								&& CoreUtil.igual(rIchGer.getCmoSequencia(), rIchDes.getCmoSequencia())
								&& CoreUtil.igual(rIchGer.getCmoEcoSeqp(), rIchDes.getCmoEcoSeqp())
								&& CoreUtil.igual(rIchGer.getPrescricaoProcedimento(), rIchDes.getPrescricaoProcedimento())
								&& CoreUtil.igual(rIchGer.getPrescricaoNpt(), rIchDes.getPrescricaoNpt())) {

							// FatItemContaHospitalar rIchDesOld = null;
							try {
								// rIchDesOld =
								getFaturamentoFacade().clonarItemContaHospitalar(rIchDes);
							} catch (Exception e) {
								logError(EXCECAO_CAPTURADA, e);
								FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
							}
							rIchDes.setIndSituacao(DominioSituacaoItenConta.A);
							getItemContaHospitalarON().atualizarItemContaHospitalarSemValidacoesForms(rIchDes, null, false,
									servidorLogado, dataFimVinculoServidor, null, null);
							flag = Boolean.TRUE;
						}
					}
				}

				if (CoreUtil.igual(Boolean.FALSE, flag)) {
					FatItemContaHospitalar rIchGerNew = null;
					try {
						rIchGerNew = getFaturamentoFacade().clonarItemContaHospitalar(rIchGer);
					} catch (Exception e) {
						logError(EXCECAO_CAPTURADA, e);
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
					}

					getFaturamentoFacade().removerItemContaHospitalar(rIchGer);

					rIchGerNew.getId().setSeq(vMaxItem);
					rIchGerNew.getId().setCthSeq(pCthSeq);
					rIchGerNew.setContaHospitalar(pCth);
					rIchGerNew.setProcedimentoInternacao(rIchGer.getProcedimentoInternacao());
					rIchGerNew.setServidoresAlterado(rIchGer.getServidoresAlterado());
					rIchGerNew.setItemRmps(rIchGer.getItemRmps());
					rIchGerNew.setProcedimentoHospitalarInterno(rIchGer.getProcedimentoHospitalarInterno());
					rIchGerNew.setServidorAnest(rIchGer.getServidorAnest());
					rIchGerNew.setUnidadesFuncional(rIchGer.getUnidadesFuncional());
					rIchGerNew.setServidor(rIchGer.getServidor());
					rIchGerNew.setProcedimentoAmbRealizado(rIchGer.getProcedimentoAmbRealizado());
					rIchGerNew.setPrescricaoNpt(rIchGer.getPrescricaoNpt());
					rIchGerNew.setAgenciaBanco(rIchGer.getAgenciaBanco());
					rIchGerNew.setServidorCriado(rIchGer.getServidorCriado());
					rIchGerNew.setPrescricaoPaciente(rIchGer.getPrescricaoPaciente());
					rIchGerNew.setPrescricaoProcedimento(rIchGer.getPrescricaoProcedimento());
					rIchGerNew.setProcEspPorCirurgias(rIchGer.getProcEspPorCirurgias());
					rIchGerNew.setCirurgia(rIchGer.getCirurgia());
					getItemContaHospitalarON().inserirItemContaHospitalarSemValidacoesForms(rIchGerNew, false, servidorLogado, dataFimVinculoServidor, null);
					vMaxItem++;
				}
			}
		}

		getFatLogErrorDAO().removerPorCth(vCthSeq);

		if (vCth.getContasInternacao() != null && !vCth.getContasInternacao().isEmpty()) {
			for (FatContasInternacao fatContaInternacao : vCth.getContasInternacao()) {
				faturamentoRN.removerContaInternacao(fatContaInternacao, false, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
		vCth.setContasInternacao(null);

		if (vCth.getItensContaHospitalar() != null && !vCth.getItensContaHospitalar().isEmpty()) {
			for (FatItemContaHospitalar fatItensContaHospitalar : vCth.getItensContaHospitalar()) {
				getFaturamentoFacade().removerItemContaHospitalar(fatItensContaHospitalar);
			}
		}
		vCth.setItensContaHospitalar(null);

		Short vSeqpMae = obterProximoSeqPendenciaContaHosp(pCthSeq);
		Short vSeqpMax = vSeqpMae;

		if (vCth.getPendenciasContaHosp() != null && !vCth.getPendenciasContaHosp().isEmpty()) {
			for (FatPendenciaContaHosp fatPendenciaContaHosp : vCth.getPendenciasContaHosp()) {
				FatPendenciaContaHosp fatPendenciaContaHospNew = new FatPendenciaContaHosp();
				FatPendenciaContaHospId id = new FatPendenciaContaHospId(pCthSeq, vSeqpMax);
				fatPendenciaContaHospNew.setContaHospitalar(pCth);
				fatPendenciaContaHospNew.setId(id);
				fatPendenciaContaHospNew.setFatMotivoPendencia(fatPendenciaContaHosp.getFatMotivoPendencia());
				fatPendenciaContaHospNew.setUnfSeq(fatPendenciaContaHosp.getUnfSeq());
				fatPendenciaContaHospNew.setIndSituacao(fatPendenciaContaHosp.getIndSituacao());
				faturamentoRN.inserir(fatPendenciaContaHospNew);
				vSeqpMax++;
				flagPend = Boolean.TRUE;
			}
		}

		if (CoreUtil.igual(Boolean.TRUE, flagPend)) {
			while (vSeqpMae > 0) {
				FatPendenciaContaHospId id = new FatPendenciaContaHospId(vCthSeq, vSeqpMae);
				FatPendenciaContaHosp fatPendenciaContaHosp = getFatPendenciaContaHospDAO().obterPorChavePrimaria(id);
				if (fatPendenciaContaHosp != null) {
					faturamentoRN.remover(fatPendenciaContaHosp);
				}
				vSeqpMae--;
			}
		}

		getFaturamentoFacade().removerContaHospitalar(vCth, vCthOld, dataFimVinculoServidor);

		return true;
	}

	public List<ListarContasHospPacientesPorPacCodigoVO> listarContasHospPacientesPorPacCodigo(Integer pacCodigo, Short tipoGrupoContaSUS,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException {
		List<ListarContasHospPacientesPorPacCodigoVO> lista = new ArrayList<ListarContasHospPacientesPorPacCodigoVO>();

		List<VFatContasHospPacientes> listaVFatContasHospPacientes = getVFatContasHospPacientesDAO()
				.listarVFatContasHospPacientesPorPacCodigo(pacCodigo, firstResult, maxResult, orderProperty, asc);
		if (listaVFatContasHospPacientes != null && !listaVFatContasHospPacientes.isEmpty()) {
			VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();

			for (VFatContasHospPacientes vFatContasHospPacientes : listaVFatContasHospPacientes) {
				ListarContasHospPacientesPorPacCodigoVO vo = new ListarContasHospPacientesPorPacCodigoVO();

				vo.setViewContaHospitalar(vFatContasHospPacientes);

				List<VFatAssociacaoProcedimento> procedimentosSolicitados = vFatAssociacaoProcedimentoDAO.listarVFatAssociacaoProcedimento(
						vFatContasHospPacientes.getCthPhiSeq(), vFatContasHospPacientes.getCthCspCnvCodigo(),
						vFatContasHospPacientes.getCthCspSeq(), tipoGrupoContaSUS);
				if (procedimentosSolicitados != null && !procedimentosSolicitados.isEmpty()) {
					vo.setProcedimentoSolicitado(procedimentosSolicitados.get(0));
				}

				List<VFatAssociacaoProcedimento> procedimentosRealizados = vFatAssociacaoProcedimentoDAO.listarVFatAssociacaoProcedimento(
						vFatContasHospPacientes.getCthPhiSeqRealizado(), vFatContasHospPacientes.getCthCspCnvCodigo(),
						vFatContasHospPacientes.getCthCspSeq(), tipoGrupoContaSUS);
				if (procedimentosRealizados != null && !procedimentosRealizados.isEmpty()) {
					vo.setProcedimentoRealizado(procedimentosRealizados.get(0));
				}

				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB: PLL P_BUSCA_NRO_AIH
	 * 
	 * @param medicoAuditor
	 * @param dataHoraEmissao
	 * @param procedimentoSolicitado
	 * @param contaHospitalar
	 * @throws BaseException
	 */
	public void pBuscaNroAih(FatContasHospitalares fatContahosp, FatContasHospitalares oldContaHospitalar, Date dataHoraEmissao,
			RapServidores medicoAuditor, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		try {
			Integer matricula = medicoAuditor.getId().getMatricula();
			Short vinCodigo = medicoAuditor.getId().getVinCodigo();

			// busca nro de aih disponivel
			FatAih fatAih = getFatAihDAO().buscarMinAih(DominioSituacaoAih.U);
			if (fatAih == null) {
				FaturamentoExceptionCode.NAO_ENCONTROU_AIH_DISPONIVEL.throwException();
			}

			// procura se o nro ja foi usado
			Boolean exists = getFatContasHospitalaresDAO().existsFatContasHospitalares(fatAih.getNroAih(), DominioSituacaoConta.R);
			if (exists) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.NRO_AIH_JA_EXISTENTE, String.valueOf(fatAih.getNroAih()));
			}

			// faz o update
			fatContahosp.setAih(fatAih);
			getContaHospitalarON().atualizarContaHospitalar(fatContahosp, oldContaHospitalar, true, nomeMicrocomputador,
					dataFimVinculoServidor);

			FatAih oldFatAih = null;
			try {
				oldFatAih = getFaturamentoRN().clonarFatAih(fatAih);
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
			}
			fatAih.setContaHospitalar(fatContahosp);
			fatAih.setIndSituacao(DominioSituacaoAih.A);
			fatAih.setSerMatricula(matricula);
			fatAih.setSerVinCodigo(vinCodigo);
			fatAih.setDthrEmissao(dataHoraEmissao);
			getFaturamentoRN().atualizarFatAih(fatAih, oldFatAih);
		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			logError("Exceção capturada", e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_AIH_NAO_ASSOCIADA, e.getMessage());
		}
	}

	/**
	 * ORADB: PLL P_VALIDA_MATRICULA_AUDITOR
	 * 
	 * @param phiSeq
	 * @param matricula
	 * @param vinCodigo
	 * @throws BaseException
	 */
	public void pValidaMatriculaAuditor(Integer phiSeq, Integer matricula, Short vinCodigo) throws BaseException {
		if (matricula != null && vinCodigo != null && phiSeq == null) {
			FaturamentoExceptionCode.AUDITOR_SUS_SEM_SSM.throwException();
		}
		if (matricula != null && vinCodigo != null) {
			if (obterMedicoAuditor(vinCodigo, matricula) == null) {
				FaturamentoExceptionCode.MATRICULA_INVALIDA_AUDITOR_SUS.throwException();
			}
		}
	}

	private FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	private FatLogErrorDAO getFatLogErrorDAO() {
		return fatLogErrorDAO;
	}

	private FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	private FatAihDAO getFatAihDAO() {
		return fatAihDAO;
	}

	private FatCampoMedicoAuditAihDAO getFatCampoMedicoAuditAihDAO() {
		return fatCampoMedicoAuditAihDAO;
	}

	private FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}

	private FatEspelhoItemContaHospDAO getFatEspelhoItemContaHospDAO() {
		return fatEspelhoItemContaHospDAO;
	}

	private ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}

	private FatPendenciaContaHospDAO getFatPendenciaContaHospDAO() {
		return fatPendenciaContaHospDAO;
	}

	private VFatContasHospPacientesDAO getVFatContasHospPacientesDAO() {
		return vFatContasHospPacientesDAO;
	}

	private VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	private VFatAssociacaoProcedimento2DAO getVFatAssociacaoProcedimento2DAO() {
		return vFatAssociacaoProcedimento2DAO;
	}

	private ContaHospitalarON getContaHospitalarON() {
		return contaHospitalarON;
	}

	public void informarSolicitado(VFatContasHospPacientes contaHospitalar, VFatAssociacaoProcedimento procedimentoSolicitado, Long numeroAIH,
			Date dataHoraEmissao, RapServidores medicoAuditor, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		FatContasHospitalares fatContahosp = this.getFaturamentoFacade().obterContaHospitalar(contaHospitalar.getCthSeq());

		FatContasHospitalares oldContaHospitalar = null;
		try {
			oldContaHospitalar = this.getFaturamentoFacade().clonarContaHospitalar(fatContahosp);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
		}

		if (procedimentoSolicitado != null) {
			fatContahosp.setProcedimentoHospitalarInterno(getFatProcedHospInternosDAO().obterPorChavePrimaria(
					procedimentoSolicitado.getId().getPhiSeq()));
		}

		if (medicoAuditor != null) {
			this.pValidaMatriculaAuditor(procedimentoSolicitado != null ? procedimentoSolicitado.getId().getPhiSeq() : null, medicoAuditor
					.getId().getMatricula(), medicoAuditor.getId().getVinCodigo());

			if (numeroAIH != null) {
				FatAih fatAih = this.getFaturamentoFacade().obterFatAihPorChavePrimaria(numeroAIH);

				if (fatAih != null) {
					fatContahosp.setAih(fatAih);
					getContaHospitalarON().atualizarContaHospitalar(fatContahosp, oldContaHospitalar, true, nomeMicrocomputador,
							dataFimVinculoServidor);

					FatAih oldFatAih = null;
					try {
						oldFatAih = getFaturamentoRN().clonarFatAih(fatAih);
					} catch (Exception e) {
						logError(EXCECAO_CAPTURADA, e);
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
					}
					fatAih.setContaHospitalar(fatContahosp);
					fatAih.setIndSituacao(DominioSituacaoAih.A);
					fatAih.setSerMatricula(medicoAuditor.getId().getMatricula());
					fatAih.setSerVinCodigo(medicoAuditor.getId().getVinCodigo());
					fatAih.setDthrEmissao(dataHoraEmissao);

					getFaturamentoRN().atualizarFatAih(fatAih, oldFatAih);
				} else {

					fatAih = new FatAih();

					fatAih.setNroAih(numeroAIH);
					fatAih.setIndSituacao(DominioSituacaoAih.A);
					fatAih.setContaHospitalar(fatContahosp);
					fatAih.setDthrEmissao(dataHoraEmissao);
					fatAih.setSerMatricula(medicoAuditor.getId().getMatricula());
					fatAih.setSerVinCodigo(medicoAuditor.getId().getVinCodigo());

					this.getFaturamentoRN().inserirFatAih(fatAih, false);

					fatContahosp.setAih(fatAih);
				}
			} else {
				// Busca aih e associa a conta
				this.pBuscaNroAih(fatContahosp, oldContaHospitalar, dataHoraEmissao, medicoAuditor, nomeMicrocomputador,
						dataFimVinculoServidor);
				return;
			}

			this.getContaHospitalarON().atualizarContaHospitalar(fatContahosp, oldContaHospitalar, true, nomeMicrocomputador,
					dataFimVinculoServidor);
		}
	}

	/**
	 * ORADB: FATF_ATU_CTH_AIH_2.P_ESTORNA_AIH
	 * 
	 * @param seq
	 *            - Número da conta hospitalar
	 * @param nrAIH
	 *            - Número AIH associado a conta hospitalar
	 * 
	 * @throws ApplicationBusinessException
	 */
	public DominioMensagemEstornoAIH validaEstornoAIH(final Integer seq, final Long nrAIH) throws ApplicationBusinessException {
		final FatContasHospitalares cth = getFatContasHospitalaresDAO().obterFatContaHospitalarParaEstornoAIH(seq, nrAIH);

		if (cth != null) {
			final DominioSituacaoConta dominios[] = { DominioSituacaoConta.A, DominioSituacaoConta.C, DominioSituacaoConta.F,
					DominioSituacaoConta.R };
			if (Arrays.binarySearch(dominios, cth.getIndSituacao()) > 0) {
				if (cth.getIndImpAih() != null && cth.getIndImpAih() == Boolean.TRUE) {
					return DominioMensagemEstornoAIH.I;

				} else {
					return DominioMensagemEstornoAIH.N;
				}
			}
			if (cth.getIndSituacao() != null) {
				FaturamentoExceptionCode.ERRO_AIH_NAO_PODE_SER_ESTORNADA.throwException();
			}
		}

		return DominioMensagemEstornoAIH.C;
	}

	/**
	 * ORADB: FATF_ATU_CTH_AIH_2.P_ESTORNA_AIH
	 * 
	 * @throws BaseException
	 */
	public void estornarAIH(final Integer seq, final Long nrAIH, final Boolean reaproveitarAIH, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		FatContasHospitalaresDAO fatContasHospitalaresDAO = this.getFatContasHospitalaresDAO();
		FatAihDAO fatAihDAO = this.getFatAihDAO();

		final FatContasHospitalares cth = fatContasHospitalaresDAO.obterPorChavePrimaria(seq);
		FatContasHospitalares oldCth = null;
		try {
			oldCth = this.getFaturamentoFacade().clonarContaHospitalar(cth);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
		}

		cth.setAih(null);
		cth.setIndImpAih(null);
		cth.setIndAutorizadoSms(DominioSimNao.N.toString());

		getContaHospitalarON().atualizarContaHospitalar(cth, oldCth, true, nomeMicrocomputador, dataFimVinculoServidor);
		fatContasHospitalaresDAO.flush();

		final List<FatAih> result = fatAihDAO.obterFatAihsParaReaproveitamentoEstorno(nrAIH);
		if (result != null) {
			for (FatAih fatAih : result) {
				FatAih oldFatAih = null;
				try {
					oldFatAih = getFaturamentoRN().clonarFatAih(fatAih);
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				if (reaproveitarAIH) {
					fatAih.setIndSituacao(DominioSituacaoAih.U);
				} else {
					fatAih.setIndSituacao(DominioSituacaoAih.B);
				}

				fatAih.setDthrEmissao(null);
				fatAih.setSerVinCodigo(null);
				fatAih.setSerMatricula(null);

				getFaturamentoRN().atualizarFatAih(fatAih, oldFatAih);
			}
		}
	}

	public void inserirFatPendenciaContaHospitalar(final Integer cthSeq, final FatMotivoPendencia motivoPendencia, final Short unfSeq,
			final DominioSituacao indSituacao) throws ApplicationBusinessException {

		final FatPendenciaContaHosp fpch = new FatPendenciaContaHosp();
		fpch.setIndSituacao(indSituacao);
		fpch.setContaHospitalar(getFaturamentoFacade().obterContaHospitalar(cthSeq));
		fpch.setFatMotivoPendencia(motivoPendencia);
		fpch.setId(new FatPendenciaContaHospId(cthSeq, obterProximoSeqPendenciaContaHosp(cthSeq)));
		fpch.setUnfSeq(unfSeq);

		this.getFaturamentoRN().inserir(fpch);
	}

	Short obterProximoSeqPendenciaContaHosp(final Integer cthSeq) {
		return getFatPendenciaContaHospDAO().obterProximoSeq(cthSeq);
	}

	public void alterarFatPendenciaContaHospitalar(final FatPendenciaContaHosp pojo) throws ApplicationBusinessException {
		this.getFaturamentoRN().atualizar(pojo);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persistirFatArqEspelhoProcedAmb(FatArqEspelhoProcedAmb elemento, boolean flush) {
		if (elemento.getSeq() == null) {
			getFatArqEspelhoProcedAmbDAO().persistir(elemento);
			if (flush) {
				getFatArqEspelhoProcedAmbDAO().flush();
			}
		} else {
			getFatArqEspelhoProcedAmbDAO().atualizar(elemento);
			if (flush) {
				getFatArqEspelhoProcedAmbDAO().flush();
			}
		}
	}

	/**
	 * Inicialmente baseado na view do Oracle V_FAT_MEDICOS_AUDITORES,
	 * atualizado em 31/10/2013 para buscar servidores que possuem o tipo de
	 * informacao "AUDITOR SUS"
	 * 
	 * @param filtro
	 *            Nome ou parte do médico auditor
	 * @return Médicos auditores
	 * @throws ApplicationBusinessException
	 */
	public List<RapServidores> pesquisarAuditores(final String filtro) throws ApplicationBusinessException {
		final String tipoInformacao = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_INFO_COMPL_AUDITOR_SUS);
		return getRegistroColaboradorFacade().pesquisarServidoresPorTipoInformacao(filtro, DominioSituacaoVinculo.A, tipoInformacao);
	}

	public Long pesquisarAuditoresCount(final String filtro) throws ApplicationBusinessException {
		final String tipoInformacao = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_INFO_COMPL_AUDITOR_SUS);
		return getRegistroColaboradorFacade().pesquisarServidoresPorTipoInformacaoCount(filtro, DominioSituacaoVinculo.A, tipoInformacao);
	}

	public RapServidores obterMedicoAuditor(final Short vinculo, final Integer matricula) throws ApplicationBusinessException {
		return obterMedicoAuditor(getRegistroColaboradorFacade().obterServidor(vinculo, matricula));
	}

	public RapServidores obterMedicoAuditor(final RapServidores servidor) throws ApplicationBusinessException {
		final String tipoInformacao = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_INFO_COMPL_AUDITOR_SUS);

		if (servidor == null) {
			return null;
		}

		List<RapServidores> servidores = getRegistroColaboradorFacade().pesquisarServidoresPorTipoInformacao(
				servidor.getId().getMatricula().toString(), DominioSituacaoVinculo.A, tipoInformacao);

		if (!servidores.isEmpty()) {
			return servidores.get(0);
		} else {
			return null;
		}
	}

	private FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected FaturamentoRN getFaturamentoRN() {
		return faturamentoRN;
	}

	public void atualizarFatContaSugestaoDesdobr(FatContaSugestaoDesdobr contaSugestaoDesdobr, boolean flush) throws BaseException {
		this.getFaturamentoRN().atualizarFatContaSugestaoDesdobr(contaSugestaoDesdobr, flush);
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	protected FatArqEspelhoProcedAmbDAO getFatArqEspelhoProcedAmbDAO() {
		return fatArqEspelhoProcedAmbDAO;
	}

	public void enviaEmailResultadoEncerramentoAmbulatorio(final String msg) throws ApplicationBusinessException {
		final IParametroFacade parametroFacade = getParametroFacade();

		final String remetentes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		final String destinatariosParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_FATURAMENTO_AMBULATORIO)
				.getVlrTexto();

		final List<String> destinatarios = Arrays.asList(destinatariosParam.split(";"));

		final String titulo = "Execucao do Faturamento do Ambulatório em " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date());

		getEmailUtil().enviaEmail(remetentes, destinatarios, null, titulo, msg);
	}

	public void enviaEmailResultadoEncerramentoAmbulatorio(final String msg, String remetente, List<String> destinatarios)
			throws ApplicationBusinessException {
		final String titulo = "Execucao do Faturamento do Ambulatório em " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date());

		getEmailUtil().enviaEmail(remetente, destinatarios, null, titulo, msg);
	}

	/**
	 * ORADB: FATK_CTH4_RN_UN.ENVIA_EMAIL
	 * 
	 * @param seqsOK
	 *            - Lista de contas encerradas
	 * @param seqsNOK
	 *            - Lista de contas não encerradas
	 * @throws ApplicationBusinessException
	 */
	public void enviaEmailResultadoEncerramentoCTHs(final List<String> resultOK, final List<String> resultNOK,
			final Date dataInicioEncerramento) throws ApplicationBusinessException {
		final IParametroFacade parametroFacade = getParametroFacade();

		final String remetentes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		final String destinatariosParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_FATURAMENTO_INTERNACAO)
				.getVlrTexto();
		final List<String> destinatarios = Arrays.asList(destinatariosParam.split(";"));

		final String titulo = "Execucao de Encerramento Automatico de Contas Hospitalares em "
				+ DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date());

		final String msg = "<strong><h2>Encerramento de Contas Hospitalares iniciado em "
				+ DateUtil.obterDataFormatadaHoraMinutoSegundo(dataInicioEncerramento) + ", finalizado em "
				+ DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " </h2></strong><br /><br /><br />"
				+ ((resultOK.isEmpty()) ? "" : "<strong>**** Contas Encerradas com Sucesso *****</strong><br />" + resultOK.toString())
				+ ((resultNOK.isEmpty()) ? "" : "<strong>**** Contas Não Encerradas *****</strong><br />" + resultNOK.toString());

		getEmailUtil().enviaEmailHtml(remetentes, destinatarios, null, titulo, msg);
	}
	
	/**
	 * @throws AGHUNegocioException
	 */
	public void enviaEmailInicioEncerramentoCTHs(final Integer quantidadeContas, final Date dataInicioEncerramento) throws ApplicationBusinessException{
		final IParametroFacade parametroFacade = getParametroFacade();
		
		final String remetentes   = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		final String destinatariosParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_FATURAMENTO_INTERNACAO).getVlrTexto(); 
		final List<String> destinatarios = Arrays.asList(destinatariosParam.split(";"));
		
		final String titulo = "Iniciando Encerramento de Contas Hospitalares em "+ DateUtil.obterDataFormatadaHoraMinutoSegundo(dataInicioEncerramento);
		
		final String msg = "Iniciando Execucao de Encerramento de Contas Hospitalares em "+ DateUtil.obterDataFormatadaHoraMinutoSegundo(dataInicioEncerramento) +
				"<br /><br /><br /> Total de contas: " + quantidadeContas;
		
		getEmailUtil().enviaEmail(remetentes, destinatarios, null, titulo, msg);
	}	

	/**
	 * Calcula a idade na epoca da data passada por parâmetro
	 * 
	 * @param dataNascInput
	 * @param data
	 * @return
	 */
	public Integer calculaIdade(final Date dataNascInput, final Date data) {
		final Calendar dateOfBirth = new GregorianCalendar();
		dateOfBirth.setTime(dataNascInput);
		// Cria um objeto calendar com a data atual
		final Calendar dataBase = Calendar.getInstance();
		dataBase.setTime(data);
		// Obtém a idade baseado no ano
		int age = dataBase.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

		dateOfBirth.add(Calendar.YEAR, age);
		if (dataBase.before(dateOfBirth)) {
			age--;
		}
		return age;
	}

	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	/**
	 * ORADB: FATC_VER_CAR_PHI_CNV
	 * 
	 * A partir de um phi da tabela pmr verifica nas tabelas do faturamento se o
	 * procedimento possui a característica desejada associada ao iph, retorna
	 * v_vlr_number, v_vlr_char, v_vlr_date, e se o phi é de aparelho
	 */
	public FatcVerCarPhiCnvVO fatcVerCarPhiCnv(final Short cspCnvCodigo, final Byte cspSeq, final Integer phi,
			final String pCaracteristica, final Short cpgGrcSeq) {
		final FatcVerCarPhiCnvVO result = new FatcVerCarPhiCnvVO();

		final List<FatcVerCarPhiCnvVO> cvapr = getVFatAssociacaoProcedimento2DAO().obterFatcVerCarPhiCnvVO(cpgGrcSeq, phi, cspSeq,
				cspCnvCodigo);
		if (!cvapr.isEmpty()) {
			final List<FatTipoCaractItens> cCaract = getFatTipoCaractItensDAO().listarTipoCaractItensPorCaracteristica(pCaracteristica);

			if (!cCaract.isEmpty()) {
				final FatCaractItemProcHosp cCarac = getFatCaractItemProcHospDAO().obterPorItemProcHospTipoCaract(
						new FatItensProcedHospitalarId(cvapr.get(0).getIphPhoSeq(), cvapr.get(0).getIphSeq()), cCaract.get(0).getSeq());
				if (cCarac != null) {
					result.setValorChar(cCarac.getValorChar());
					result.setValorData(cCarac.getValorData());
					result.setValorNumerico(cCarac.getValorNumerico());
					result.setResult(true);
				}
			}
		}

		return result;
	}
	
	/**
	 * ORADB fatf_atu_proc_amb.fmb CURSOR C_CNV_PHI(p_convenio NUMBER, p_plano NUMBER, p_phi_seq NUMBER)
	 * Incidente #39454
	 * Nota Consumo - botão Faturamento Ambulatorial- Não mostrava mensagem para o usuário
	 * quando  Convênio/Plano e Procedimento são incompatíveis!
	 * @throws AGHUNegocioException 
	 */
	public void verificaCompatibilidadeConvenioComProcedimento(Integer phiSeq,Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq )
			throws ApplicationBusinessException {
		
		VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();	
		final List<VFatAssociacaoProcedimento>  result = vFatAssociacaoProcedimentoDAO.listarVFatAssociacaoProcedimento( phiSeq,
																										     cpgCphCspCnvCodigo, 
																										     cpgCphCspSeq);
		if (result.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.CONVENIO_PLANO_PROCEDIMENTO_INCOMPATIVEIS);
		}
		
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	/**
	 * Valida seleção de competência
	 * @param competencia
	 * @return Arquivo gerado.
	 * @throws ApplicationBusinessException
	 */
	public ArquivoURINomeQtdVO obterDadosGeracaoArquivoProdutividadeFisiatria(FatCompetencia competencia) throws ApplicationBusinessException {
		
		if (competencia == null) {
			throw new ApplicationBusinessException(FaturamentoONExceptionCode.COMPETENCIA_NAO_SELECIONADA);
		}
		
		return produtividadeFisiatriaRN.obterDadosProdutividadeFisiatria(competencia);
	}

	public FatMensagemLogDAO getFatMensagemLogDAO() {
		return fatMensagemLogDAO;
	}

	public void setFatMensagemLogDAO(FatMensagemLogDAO fatMensagemLogDAO) {
		this.fatMensagemLogDAO = fatMensagemLogDAO;
	}
	
		/** #42803
	 * 
	 *  ORADB: FATP_TAB_COD_BARRAS
	 *
	 *  Utilizado para geração do código de barras presento no controle de frequencia e no laudo
	 */
	
	public String gerarCodigoBarras(String tipoBarras, String pCodigo){ 
		
		FatpTabCodBarrasVO fatpTabCodBarras = new FatpTabCodBarrasVO();
		fatpTabCodBarras.setCodigo(pCodigo);
		
		//calcula codigo de barras padrao code 128
		if(tipoBarras.equals("CODE128")) {
			
			fatpTabCodBarras.setTamanhoString(fatpTabCodBarras.getCodigo().trim().length());			
			if(fatpTabCodBarras.getTamanhoString() == null ){
				fatpTabCodBarras.setTamanhoString(0);
			}
			
			fatpTabCodBarras.setCampoString(fatpTabCodBarras.getCodigo().trim());
			fatpTabCodBarras.setCalculo(104);
			
			if(fatpTabCodBarras.getTamanhoString() > 0 ){
				List<FatpTabCodeVO> lista = carregarListaTabCodeVO();
				Integer tamanho  = fatpTabCodBarras.getTamanhoString();
				for(int i=1; tamanho > 0;i++ ){
					fatpTabCodBarras.setCalculoAux(consultarValorTabCodeVO(fatpTabCodBarras.getCampoString().substring(0,i), lista));
					fatpTabCodBarras.setCalculo(fatpTabCodBarras.getCalculo() + (fatpTabCodBarras.getCalculoAux() * i ));
					tamanho --;
				}
				
				fatpTabCodBarras.setResto(fatpTabCodBarras.getCalculo() - ((int)(fatpTabCodBarras.getCalculo()/103) * 103));
				
				fatpTabCodBarras.setCalculoAuxDv(consultarCaracterTabCodeVO(fatpTabCodBarras.getResto(), lista));
				fatpTabCodBarras.setDigito(fatpTabCodBarras.getCalculoAuxDv());
			}
			
			fatpTabCodBarras.setBarras("Ì" + fatpTabCodBarras.getCodigo() + fatpTabCodBarras.getDigito() + "Ì");
		}
		
		return fatpTabCodBarras.getBarras();
	}
	
	
	/** #42803
	 * 
	 *  ORADB: FATP_TAB_COD128
	 *
	 *  carregar tabela código de barras padrão code128
	 */
	public List<FatpTabCodeVO> carregarListaTabCodeVO() {
		
		List<FatpTabCodeVO> lista = new ArrayList<FatpTabCodeVO>();
		
		lista.add(new FatpTabCodeVO("SPACE", 0, " "));
		lista.add(new FatpTabCodeVO("!", 1, "!"));
		lista.add(new FatpTabCodeVO("\"", 2, "\""));
		lista.add(new FatpTabCodeVO("#", 3, "#"));
		lista.add(new FatpTabCodeVO("$", 4, "$"));
		lista.add(new FatpTabCodeVO("%", 5, "%"));
		lista.add(new FatpTabCodeVO("&", 6, "&"));
		lista.add(new FatpTabCodeVO("''", 7, "¢"));
		lista.add(new FatpTabCodeVO("(", 8, "("));
		lista.add(new FatpTabCodeVO(")", 9, ")"));
		lista.add(new FatpTabCodeVO("*", 10, "*"));
		lista.add(new FatpTabCodeVO("+", 11, "+"));
		lista.add(new FatpTabCodeVO(",", 12, ","));
		lista.add(new FatpTabCodeVO("-", 13, "-"));
		lista.add(new FatpTabCodeVO(".", 14, "."));
		lista.add(new FatpTabCodeVO("/", 15, "/"));
		lista.add(new FatpTabCodeVO("0", 16, "0"));
		lista.add(new FatpTabCodeVO("1", 17, "1"));
		lista.add(new FatpTabCodeVO("2", 18, "2"));
		lista.add(new FatpTabCodeVO("3", 19, "3"));
		lista.add(new FatpTabCodeVO("4", 20, "4"));
		lista.add(new FatpTabCodeVO("5", 21, "5"));
		lista.add(new FatpTabCodeVO("6", 22, "6"));
		lista.add(new FatpTabCodeVO("7", 23, "7"));
		lista.add(new FatpTabCodeVO("8", 24, "8"));
		lista.add(new FatpTabCodeVO("9", 25, "9"));
		lista.add(new FatpTabCodeVO(":", 26, ":"));
		lista.add(new FatpTabCodeVO(";", 27, ";"));
		lista.add(new FatpTabCodeVO("<", 28, "<"));
		lista.add(new FatpTabCodeVO("=", 29, "="));
		lista.add(new FatpTabCodeVO(">", 30, ">"));
		lista.add(new FatpTabCodeVO("?", 31, "?"));
		lista.add(new FatpTabCodeVO("@", 32, "@"));
		lista.add(new FatpTabCodeVO("A", 33, "A"));
		lista.add(new FatpTabCodeVO("B", 34, "B"));
		lista.add(new FatpTabCodeVO("C", 35, "C"));
		lista.add(new FatpTabCodeVO("D", 36, "D"));
		lista.add(new FatpTabCodeVO("E", 37, "E"));
		lista.add(new FatpTabCodeVO("F", 38, "F"));
		lista.add(new FatpTabCodeVO("G", 39, "G"));
		lista.add(new FatpTabCodeVO("H", 40, "H"));
		lista.add(new FatpTabCodeVO("I", 41, "I"));
		lista.add(new FatpTabCodeVO("J", 42, "J"));
		lista.add(new FatpTabCodeVO("K", 43, "K"));
		lista.add(new FatpTabCodeVO("L", 44, "L"));
		lista.add(new FatpTabCodeVO("M", 45, "M"));
		lista.add(new FatpTabCodeVO("N", 46, "N"));
		lista.add(new FatpTabCodeVO("O", 47, "O"));
		lista.add(new FatpTabCodeVO("P", 48, "P"));
		lista.add(new FatpTabCodeVO("Q", 49, "Q"));
		lista.add(new FatpTabCodeVO("R", 50, "R"));
		lista.add(new FatpTabCodeVO("S", 51, "S"));
		lista.add(new FatpTabCodeVO("T", 52, "T"));
		lista.add(new FatpTabCodeVO("U", 53, "U"));
		lista.add(new FatpTabCodeVO("V", 54, "V"));
		lista.add(new FatpTabCodeVO("W", 55, "W"));
		lista.add(new FatpTabCodeVO("X", 56, "X"));
		lista.add(new FatpTabCodeVO("Y", 57, "Y"));
		lista.add(new FatpTabCodeVO("Z", 58, "Z"));
		lista.add(new FatpTabCodeVO("[", 59, "["));
		lista.add(new FatpTabCodeVO("\\", 60, "\\"));
		lista.add(new FatpTabCodeVO("]", 61, "]"));
		lista.add(new FatpTabCodeVO("^", 62, "^"));
		lista.add(new FatpTabCodeVO("_", 63, "_"));
		lista.add(new FatpTabCodeVO("`", 64, "`"));
		lista.add(new FatpTabCodeVO("a", 65, "a"));
		lista.add(new FatpTabCodeVO("b", 66, "b"));
		lista.add(new FatpTabCodeVO("c", 67, "c"));
		lista.add(new FatpTabCodeVO("d", 68, "d"));
		lista.add(new FatpTabCodeVO("e", 69, "e"));
		lista.add(new FatpTabCodeVO("f", 70, "f"));
		lista.add(new FatpTabCodeVO("g", 71, "g"));
		lista.add(new FatpTabCodeVO("h", 72, "h"));
		lista.add(new FatpTabCodeVO("i", 73, "i"));
		lista.add(new FatpTabCodeVO("j", 74, "j"));
		lista.add(new FatpTabCodeVO("k", 75, "k"));
		lista.add(new FatpTabCodeVO("l", 76, "l"));
		lista.add(new FatpTabCodeVO("m", 77, "m"));
		lista.add(new FatpTabCodeVO("n", 78, "n"));
		lista.add(new FatpTabCodeVO("o", 79, "o"));
		lista.add(new FatpTabCodeVO("p", 80, "p"));
		lista.add(new FatpTabCodeVO("q", 81, "q"));
		lista.add(new FatpTabCodeVO("r", 82, "r"));
		lista.add(new FatpTabCodeVO("s", 83, "s"));
		lista.add(new FatpTabCodeVO("t", 84, "t"));
		lista.add(new FatpTabCodeVO("u", 85, "u"));
		lista.add(new FatpTabCodeVO("v", 86, "v"));
		lista.add(new FatpTabCodeVO("w", 87, "w"));
		lista.add(new FatpTabCodeVO("x", 88, "x"));
		lista.add(new FatpTabCodeVO("y", 89, "y"));
		lista.add(new FatpTabCodeVO("z", 90, "z"));
		lista.add(new FatpTabCodeVO("{", 91, "{"));
		lista.add(new FatpTabCodeVO("|", 92, "|"));
		lista.add(new FatpTabCodeVO("}", 93, "}"));
		lista.add(new FatpTabCodeVO("~", 94, "~"));
		lista.add(new FatpTabCodeVO("DEL", 95, "Ã"));
		lista.add(new FatpTabCodeVO("FNC3", 96, "Ä"));
		lista.add(new FatpTabCodeVO("FNC2", 97, "Å"));
		lista.add(new FatpTabCodeVO("SHIFT", 98, "Æ"));
		lista.add(new FatpTabCodeVO("Code C", 99, "Ç"));
		lista.add(new FatpTabCodeVO("FNC4", 100, "È"));
		lista.add(new FatpTabCodeVO("Code A", 101, "É"));
		lista.add(new FatpTabCodeVO("FNC1", 102, "Ê"));
		lista.add(new FatpTabCodeVO("-", 103, "Ë"));
		lista.add(new FatpTabCodeVO("START", 104, "Ì"));
		lista.add(new FatpTabCodeVO("-", 105, "Í"));
		lista.add(new FatpTabCodeVO("STOP", null, "Î"));
		
		return lista;
	}
	
	public Integer consultarValorTabCodeVO(String pSubset, List<FatpTabCodeVO> lista) {
		
		if (pSubset != null) {
			for (FatpTabCodeVO vo : lista) {
				if (pSubset.equals(vo.getSubset())) {
					return vo.getCodigo();
				}
			}
		}
		return 0;
	}
	
	public String consultarCaracterTabCodeVO(Integer pCodigo, List<FatpTabCodeVO> lista) {
		
		if (pCodigo != null) {
			for (FatpTabCodeVO vo : lista) {
				if (pCodigo.equals(vo.getCodigo())) {
					return vo.getCaracter();
				}
			}
		}
		return null;
	}
	
}
