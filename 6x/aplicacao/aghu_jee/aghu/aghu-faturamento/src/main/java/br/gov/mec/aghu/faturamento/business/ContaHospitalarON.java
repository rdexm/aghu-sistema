package br.gov.mec.aghu.faturamento.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.faturamento.vo.ProtocolosAihsVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuReintVO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ContaHospitalarON extends BaseBusiness {


private static final String CONTA_ = "Conta ";

@EJB
private ContaHospitalarRN contaHospitalarRN;

	private static final Log LOG = LogFactory.getLog(ContaHospitalarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@EJB
	private ISchedulerFacade schedulerFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private enum ContaHospitalarONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_HIBERNATE_VALIDATION
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3034871499667990395L;

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitado(
			Object objPesquisa, Integer phiSeq) throws BaseException {
		return getContaHospitalarRN().listarAssociacaoProcedimentoSUS(
				objPesquisa, phiSeq, null, true);
	}

	public Long listarAssociacaoProcedimentoSUSSolicitadoCount(
			Object objPesquisa) throws BaseException {
		return getContaHospitalarRN().listarAssociacaoProcedimentoSUSCount(
				objPesquisa, null, null, true);
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitadoPorPHI(
			Object objPesquisa, Integer phiSeq) throws BaseException {
		return getContaHospitalarRN().listarAssociacaoProcedimentoSUSPorPHI(
				objPesquisa, phiSeq, null, true);
	}

	public Long listarAssociacaoProcedimentoSUSSolicitadoPorPHICount(
			Object objPesquisa) throws BaseException {
		return getContaHospitalarRN()
				.listarAssociacaoProcedimentoSUSPorPHICount(objPesquisa, null,
						null, true);
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSRealizado(
			Object objPesquisa, Integer phiSeq, Integer cthSeq)
			throws BaseException {
		return getContaHospitalarRN().listarAssociacaoProcedimentoSUS(
				objPesquisa, phiSeq, cthSeq, false);
	}

	public Long listarAssociacaoProcedimentoSUSRealizadoCount(
			Object objPesquisa, Integer phiSeq, Integer cthSeq)
			throws BaseException {
		return getContaHospitalarRN().listarAssociacaoProcedimentoSUSCount(
				objPesquisa, phiSeq, cthSeq, false);
	}

	/**
	 * Método de sobrecarga para permitir controle da alteração da
	 * especialidade, pois quando da edição de uma "Conta Hospitalar para
	 * Cobrança sem Internação" e alteração do campo Data Hora Final, com uma
	 * especialidade válida, não deve-se alterar a mesma, procedimento padrão
	 * para a atualização de uma Conta Hospitalar
	 * (FATK_CTH4_RN_UN.RN_CTHC_VER_ESP_CTA).
	 * 
	 * 
	 * Por default, chamadas antigas permanecem executando a rotina
	 * (FATK_CTH4_RN_UN.RN_CTHC_VER_ESP_CTA).
	 */
	public FatContasHospitalares persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {
		return persistirContaHospitalar(newCtaHosp, oldCtaHosp, flush, flush, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor);
	}

	/**
	 * Método para persistir uma conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @param flush
	 * @param servidorLogado 
	 * @throws BaseException
	 */
	public FatContasHospitalares persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			final Boolean alterarEspecialidade, String nomeMicrocomputador,
			RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		if (newCtaHosp.getSeq() != null) {
			return this.atualizarContaHospitalar(newCtaHosp, oldCtaHosp, flush, alterarEspecialidade, nomeMicrocomputador, dataFimVinculoServidor);
		} else {
			return inserirContaHospitalar(newCtaHosp, flush, alterarEspecialidade, dataFimVinculoServidor);
		}
	}

	public FatContasHospitalares inserirContaHospitalar(
			FatContasHospitalares newCtaHosp, boolean flush,
			final Date dataFimVinculoServidor)
			throws BaseException {
		return this.inserirContaHospitalar(newCtaHosp, flush, Boolean.TRUE, dataFimVinculoServidor);
	}

	/**
	 * Método para inserir uma conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @throws ApplicationBusinessException
	 */
	public FatContasHospitalares inserirContaHospitalar(FatContasHospitalares newCtaHosp, boolean flush, final Boolean alterarEspecialidade,
			final Date dataFimVinculoServidor) throws BaseException {

		contaHospitalarRN.executarAntesDeInserirContaHospitalar(newCtaHosp, alterarEspecialidade, dataFimVinculoServidor);
		
		newCtaHosp = fatContasHospitalaresDAO.merge(newCtaHosp);
		if (flush) {
			fatContasHospitalaresDAO.flush();
		}
		
		return newCtaHosp;
	}

	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void atualizarContaHospitalar(FatContasHospitalares newCtaHosp,
			FatContasHospitalares oldCtaHosp, boolean flush,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		this.atualizarContaHospitalar(newCtaHosp, oldCtaHosp, flush,
				Boolean.TRUE, nomeMicrocomputador,
				dataFimVinculoServidor);
	}
	
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void atualizarContaHospitalarProtocolosAih(ProtocolosAihsVO item, boolean flush, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws BaseException {
		String sim = "S";
		String nao = "N";
		if(item.isUpdate()){
			FatContasHospitalares entity = faturamentoFacade.obterProtocoloAihPorId(item.getConta());
			entity.setDtEnvioSms(item.getDataenviosms());
			if(item.isEnvioBoolean()){
				entity.setIndEnviadoSms(sim);
			}else{
				entity.setIndEnviadoSms(nao);
			}			
			this.atualizarContaHospitalar(entity, faturamentoFacade.obterProtocoloAihPorId(item.getConta()), flush,	Boolean.TRUE, 
					nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * Método para atualizar uma conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @param flush
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public FatContasHospitalares atualizarContaHospitalar(FatContasHospitalares newCtaHosp,
			FatContasHospitalares oldCtaHosp, boolean flush,
			final Boolean alterarEspecialidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		try {
			contaHospitalarRN.executarAntesDeAtualizarContaHospitalar(newCtaHosp, oldCtaHosp, alterarEspecialidade, dataFimVinculoServidor);

			newCtaHosp = this.getFatContasHospitalaresDAO().merge(newCtaHosp);
			if (flush) {
				this.getFatContasHospitalaresDAO().flush();
			}
			contaHospitalarRN.executarStatementAposAtualizarContaHospitalar(newCtaHosp, oldCtaHosp, dataFimVinculoServidor);
			contaHospitalarRN.executarAposAtualizarContaHospitalar(newCtaHosp, oldCtaHosp, nomeMicrocomputador, dataFimVinculoServidor);	
			return newCtaHosp;
		} catch (final ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "
								+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(ContaHospitalarONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION, mensagem);
		}
	}

	public void converterContaEmSemCobertura(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (DominioSituacaoConta.F.equals(situacao)) {
			final FatContasHospitalares oldCtaHosp, newCtaHosp;

			newCtaHosp = this.getFatContasHospitalaresDAO()
					.obterPorChavePrimaria(cthSeqSelected);

			try {
				oldCtaHosp = this.clonarContaHospitalar(newCtaHosp);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new BaseException(
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}

			newCtaHosp.setIndSituacao(DominioSituacaoConta.N);
			this.persistirContaHospitalar(newCtaHosp, oldCtaHosp, true,
					nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

		} else {
			// TODO adicionar numero da conta
			throw new BaseException(
					FaturamentoExceptionCode.ERRO_AO_CONVERTER_CONTA_SEM_COBERTURA);
		}
	}

	public void faturarContaUmDia(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (DominioSituacaoConta.F.equals(situacao)) {
			final FatContasHospitalares oldCtaHosp, newCtaHosp;

			try {
				newCtaHosp = this.getFatContasHospitalaresDAO()
						.obterPorChavePrimaria(cthSeqSelected);
				oldCtaHosp = this.clonarContaHospitalar(newCtaHosp);
				newCtaHosp.setIndAutorizaFat(DominioSimNao.S.toString());

				this.persistirContaHospitalar(newCtaHosp, oldCtaHosp, true,
						nomeMicrocomputador,
						servidorLogado, dataFimVinculoServidor);

			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new BaseException(
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}

		} else {
			// TODO adicionar numero da conta
			throw new BaseException(
					FaturamentoExceptionCode.ERRO_AO_FATURAR_CONTA_UM_DIA);
		}
	}

	/**
	 * Método para remover uma conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @throws ApplicationBusinessException
	 */
	public void removerContaHospitalar(FatContasHospitalares newCtaHosp,
			FatContasHospitalares oldCtaHosp,
			final Date dataFimVinculoServidor) throws BaseException {
		ContaHospitalarRN contaHospitalarRN = this.getContaHospitalarRN();
		FatContasHospitalaresDAO contasHospitalaresDAO = this
				.getFatContasHospitalaresDAO();

		// contasHospitalaresDAO.removerSemFlush(newCtaHosp);
		contasHospitalaresDAO.remover(newCtaHosp);
		contaHospitalarRN.executarStatementAposDeletarContaHospitalar(
				newCtaHosp, oldCtaHosp, dataFimVinculoServidor);
		contaHospitalarRN.executarAposDeletarContaHospitalar(newCtaHosp);

		contasHospitalaresDAO.flush();
	}

	/**
	 * Método para clonar um objeto FatContasHospitalares.
	 * 
	 * @param contaHospitalar
	 * @return Objeto FatContasHospitalares clonado.
	 * @throws Exception
	 */
	@SuppressWarnings({ "PMD.SignatureDeclareThrowsException",
			"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public FatContasHospitalares clonarContaHospitalar(final FatContasHospitalares contaHospitalar) throws Exception {
		return getFatContasHospitalaresDAO().obterOriginal(contaHospitalar.getSeq());
		
		/*
		 
		 eSchweigert 16/12/2013
		 
		 Alterado pois estava gerando um erro com objeto desatachado (Programa dar Alta Pacientes internados).
		 
		FatContasHospitalares cloneContaHospitalar = (FatContasHospitalares) BeanUtils
				.cloneBean(contaHospitalar);

		if (contaHospitalar.getContaHospitalar() != null) {
			FatContasHospitalares cta = new FatContasHospitalares();
			cta.setSeq(contaHospitalar.getContaHospitalar().getSeq());
			cloneContaHospitalar.setContaHospitalar(cta);
		}
		if (contaHospitalar.getContaHospitalarReapresentada() != null) {
			FatContasHospitalares cta = new FatContasHospitalares();
			cta.setSeq(contaHospitalar.getContaHospitalarReapresentada()
					.getSeq());
			cloneContaHospitalar.setContaHospitalarReapresentada(cta);
		}
		if (contaHospitalar.getConvenioSaude() != null) {
			FatConvenioSaude conv = new FatConvenioSaude();
			conv.setCodigo(contaHospitalar.getConvenioSaude().getCodigo());
			cloneContaHospitalar.setConvenioSaude(conv);
		}
		if (contaHospitalar.getConvenioSaudePlano() != null) {
			FatConvenioSaudePlano csp = new FatConvenioSaudePlano();
			FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
			id.setCnvCodigo(contaHospitalar.getConvenioSaudePlano().getId()
					.getCnvCodigo());
			id.setSeq(contaHospitalar.getConvenioSaudePlano().getId().getSeq());
			csp.setId(id);
			cloneContaHospitalar.setConvenioSaudePlano(csp);
		}
		if (contaHospitalar.getProcedimentoHospitalarInterno() != null) {
			FatProcedHospInternos phi = new FatProcedHospInternos();
			phi.setSeq(contaHospitalar.getProcedimentoHospitalarInterno()
					.getSeq());
			cloneContaHospitalar.setProcedimentoHospitalarInterno(phi);
		}
		if (contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null) {
			FatProcedHospInternos phir = new FatProcedHospInternos();
			phir.setSeq(contaHospitalar
					.getProcedimentoHospitalarInternoRealizado().getSeq());
			cloneContaHospitalar
					.setProcedimentoHospitalarInternoRealizado(phir);
		}
		if (contaHospitalar.getSituacaoSaidaPaciente() != null) {
			FatSituacaoSaidaPaciente ssp = new FatSituacaoSaidaPaciente();
			FatSituacaoSaidaPacienteId id = new FatSituacaoSaidaPacienteId();
			id.setMspSeq(contaHospitalar.getSituacaoSaidaPaciente().getId()
					.getMspSeq());
			id.setSeq(contaHospitalar.getSituacaoSaidaPaciente().getId()
					.getSeq());
			ssp.setId(id);
			cloneContaHospitalar.setSituacaoSaidaPaciente(ssp);
		}
		if (contaHospitalar.getTipoClassifSecSaude() != null) {
			FatTipoClassifSecSaude tcss = new FatTipoClassifSecSaude();
			tcss.setSeq(contaHospitalar.getTipoClassifSecSaude().getSeq());
			cloneContaHospitalar.setTipoClassifSecSaude(tcss);
		}
		if (contaHospitalar.getMotivoDesdobramento() != null) {
			FatMotivoDesdobramento md = new FatMotivoDesdobramento();
			md.setSeq(contaHospitalar.getMotivoDesdobramento().getSeq());
			cloneContaHospitalar.setMotivoDesdobramento(md);
		}
		if (contaHospitalar.getDocumentoCobrancaAih() != null) {
			FatDocumentoCobrancaAihs dca = new FatDocumentoCobrancaAihs();
			dca.setCodigoDcih(contaHospitalar.getDocumentoCobrancaAih()
					.getCodigoDcih());
			cloneContaHospitalar.setDocumentoCobrancaAih(dca);
		}
		if (contaHospitalar.getExclusaoCritica() != null) {
			FatExclusaoCritica ec = new FatExclusaoCritica();
			ec.setSeq(contaHospitalar.getExclusaoCritica().getSeq());
			cloneContaHospitalar.setExclusaoCritica(ec);
		}
		if (contaHospitalar.getTipoAih() != null) {
			FatTipoAih ta = new FatTipoAih();
			ta.setSeq(contaHospitalar.getTipoAih().getSeq());
			cloneContaHospitalar.setTipoAih(ta);
		}
		if (contaHospitalar.getAih() != null) {
			FatAih aih = new FatAih();
			aih.setNroAih(contaHospitalar.getAih().getNroAih());
			cloneContaHospitalar.setAih(aih);
		}
		if (contaHospitalar.getDiariaUtiDigitada() != null) {
			FatDiariaUtiDigitada dud = new FatDiariaUtiDigitada();
			dud.setCthSeq(contaHospitalar.getDiariaUtiDigitada().getCthSeq());
			cloneContaHospitalar.setDiariaUtiDigitada(dud);
		}
		if (contaHospitalar.getValorContaHospitalar() != null) {
			FatValorContaHospitalar vch = new FatValorContaHospitalar();
			vch.setCthSeq(contaHospitalar.getValorContaHospitalar().getCthSeq());
			cloneContaHospitalar.setValorContaHospitalar(vch);
		}
		if (contaHospitalar.getEspecialidade() != null) {
			AghEspecialidades esp = new AghEspecialidades();
			esp.setSeq(contaHospitalar.getEspecialidade().getSeq());
			cloneContaHospitalar.setEspecialidade(esp);
		}
		if (contaHospitalar.getServidorManuseado() != null) {
			RapServidores serv = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(contaHospitalar.getServidorManuseado().getId()
					.getMatricula());
			id.setVinCodigo(contaHospitalar.getServidorManuseado().getId()
					.getVinCodigo());
			serv.setId(id);
			cloneContaHospitalar.setServidorManuseado(serv);
		}
		if (contaHospitalar.getServidorTemProfResponsavel() != null) {
			RapServidores serv = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(contaHospitalar.getServidorTemProfResponsavel()
					.getId().getMatricula());
			id.setVinCodigo(contaHospitalar.getServidorTemProfResponsavel()
					.getId().getVinCodigo());
			serv.setId(id);
			cloneContaHospitalar.setServidorTemProfResponsavel(serv);
		}
		if (contaHospitalar.getAcomodacao() != null) {
			AinAcomodacoes ac = new AinAcomodacoes();
			ac.setSeq(contaHospitalar.getAcomodacao().getSeq());
			cloneContaHospitalar.setAcomodacao(ac);
		}
		if (contaHospitalar.getServidor() != null) {
			RapServidores serv = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(contaHospitalar.getServidor().getId()
					.getMatricula());
			id.setVinCodigo(contaHospitalar.getServidor().getId()
					.getVinCodigo());
			serv.setId(id);
			cloneContaHospitalar.setServidor(serv);
		}

		return cloneContaHospitalar;
		*/
	}

	/**
	 * Busca situação SMS
	 * 
	 * FUNCTION busca_sit_sms(p_cth_seq IN NUMBER)
	 * 
	 * @param pCthSeq
	 * @return
	 */
	public String buscaSitSms(Integer pCthSeq) {
		FatContasHospitalares fatContasHospitalares = getFatContasHospitalaresDAO()
				.obterPorChavePrimaria(pCthSeq);
		String pAutorizado = fatContasHospitalares.getIndAutorizadoSms();
		String pEnviado = fatContasHospitalares.getIndEnviadoSms();

		if (pAutorizado == null || pAutorizado.equals("")) {
			pAutorizado = "N";
		}

		if (pEnviado == null || pEnviado.equals("")) {
			pEnviado = "N";
		}

		if (pEnviado.equals("N") && pAutorizado.equals("N")) {
			return "";
		}

		if (pEnviado.equals("S") && pAutorizado.equals("N")) {
			return "Em Avaliação";
		}

		if (pEnviado.equals("S") && pAutorizado.equals("S")) {
			return "Autorizado";
		}

		if (pEnviado.equals("N") && pAutorizado.equals("S")) {
			return "Autor.EManual";
		}

		return "";
	}

	protected ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	/**
	 * Método para validar a reabertura de contas hospitalares
	 * 
	 * @param cthSeqSelected
	 *            - código da conta a ser reaberta
	 * @param situacao
	 *            - situação da conta a ser reaberta (Devendo ser E - Encerrada)
	 * 
	 * @throws BaseException
	 */
	public Boolean validarReaberturaContaHospitalar(
			final Integer cthSeqSelected, final DominioSituacaoConta situacao)
			throws BaseException {

		// Apenas reabre conta com situação igual a E
		if (DominioSituacaoConta.E.equals(situacao)) {

			final FatContasHospitalares ctaHosp = this
					.getFatContasHospitalaresDAO().obterPorChavePrimaria(
							cthSeqSelected);

			// Verifica se a conta já foi gerada no arquivo para entrega ao sus
			if (ctaHosp != null && ctaHosp.getDocumentoCobrancaAih() == null) {
				// qms$handle_ofg45_messages('E',false,'Conta/dcih não
				// encontrada:
				// '||name_in('CTH.SEQ')||'/'||name_in('cth.dci_codigo_dcih'));
				throw new BaseException(
						FaturamentoExceptionCode.ERRO_VALIDAR_REABERTURA_CH_2,
						ctaHosp.getSeq(),
						(ctaHosp.getDocumentoCobrancaAih() != null ? ctaHosp
								.getDocumentoCobrancaAih().getCodigoDcih() : ""));

			} else if (ctaHosp != null
					&& ctaHosp.getDocumentoCobrancaAih() != null
					&& ctaHosp.getDocumentoCobrancaAih().getDtApresentacao() != null) {

				return (DominioSimNao.S.toString()).equalsIgnoreCase(ctaHosp
						.getIndAutorizadoSms());

			} else {
				return false;
			}
		} else {
			throw new BaseException(
					FaturamentoExceptionCode.ERRO_VALIDAR_REABERTURA_CH_1,
					cthSeqSelected);
		}
	}

	public void reabrirContaHospitalarEmLote(String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		getContaHospitalarRN().rnCthcAtuReabreEmLote(nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * 
	 * ORADB <code>FATF_ATU_COMP_INT.reabre_contas_nao_autorizadas</code>
	 * 
	 * @param cthSeqSelected
	 * @return
	 * @throws BaseException
	 */
	public Boolean reabrirContaHospitalarNaoAutorizadas(String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		try {
			reabrirContaHospitalarEmLote(nomeMicrocomputador, dataFimVinculoServidor);
			//getFaturamentoFacade().commit();
		} catch (BaseException e) {
			logError("Erros na execução fatk_cth_rn.rn_cthc_atu_reabre em "+ new Date());
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}

	public List<Integer> getContasEncerramentoEmLote() {
		return getFatContasHospitalaresDAO()
				.obterContasGrupoSUSComDataAltaNaoNula();
	}
	
	private void adicionarLogProcessamentoConta(final AghJobDetail job, final String appendLog){
		if(job != null) {
			getSchedulerFacade().adicionarLog(job, appendLog);
		}
	}

	/**
	 * ORADB: FATK_CTH4_RN_UN.RN_CTHP_ATU_ENC_AUT2
	 * 
	 * @param isScheduled
	 *            - caso true é encerramento automático senão via tela
	 * @param cth
	 *            - conta a ser encerrada, válido apenas a partir da execução
	 *            não automática
	 * @throws BaseException
	 */
	public Boolean encerrarContasHospitalares(final boolean isScheduled,
			final Integer cth, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final AghJobDetail job) throws BaseException {

		if(isScheduled){
			final List<String> seqsOK = new ArrayList<String>();
			final List<String> seqsNOK = new ArrayList<String>();
			final Date dataInicioEncerramento = new Date();
			final Integer quantidadeTotalContas = getContasEncerramentoEmLote().size();
			Integer quantidadeContasProcessadas = 0;
			
			if (quantidadeTotalContas > 0) {
				getFaturamentoFacade().enviaEmailInicioEncerramentoCTHs(quantidadeTotalContas, dataInicioEncerramento);
				this.adicionarLogProcessamentoConta(job, "Iniciando processamento de " + quantidadeTotalContas + " contas.");
			}
			
			for (Integer seq : getContasEncerramentoEmLote()) {
				
				++quantidadeContasProcessadas;
				this.adicionarLogProcessamentoConta(job, "Processando conta " + seq + " (" + quantidadeContasProcessadas + " / " + quantidadeTotalContas + ").");
				
				try {
					if( getContaHospitalarRN().rnCthcAtuGeraEsp(seq, false, nomeMicrocomputador, dataFimVinculoServidor) ){
						seqsOK.add(CONTA_ + seq + " encerrada com sucesso.<br />");
						logInfo(CONTA_ + seq + " encerrada com sucesso.");
						this.adicionarLogProcessamentoConta(job, "Conta encerrada com sucesso.");
					} else {
						seqsNOK.add(CONTA_ + seq + " não encerrada por conter erros.<br />");
						logError(CONTA_ + seq + " não encerrada por conter erros.");
						this.adicionarLogProcessamentoConta(job, "Conta não encerrada por conter erros.");
					}
				} catch (Exception e) {
					final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
					//TODO faturamentoFacade.rollback();
					faturamentoFacade.clearSemFlush();
					
					seqsNOK.add(CONTA_ + seq + " não encerrada por conter erros. " + (e.getMessage() != null ? e.getMessage() : "") + "<br />");
					logError(CONTA_ + seq + " não encerrada por conter erros.");
					this.adicionarLogProcessamentoConta(job, "Conta não encerrada por conter erros. " + (e.getMessage() != null ? e.getMessage() : ""));
				} 
				
			}
			
			getSchedulerFacade().adicionarLog(job, "Finalizando processamento de " + quantidadeTotalContas + " contas.");
			try {
				getFaturamentoFacade().enviaEmailResultadoEncerramentoCTHs(seqsOK, seqsNOK, dataInicioEncerramento);
				getSchedulerFacade().adicionarLog(job, "E-mail enviado com sucesso.");
			} catch (Exception e) {
				getSchedulerFacade().adicionarLog(job, "Falha ao enviar e-mail, verifique se os parâmetros estão configurados corretamente.");
			}
			
			return true;
			
		} else {
			if( getContaHospitalarRN().rnCthcAtuGeraEsp(cth, false, nomeMicrocomputador, dataFimVinculoServidor) ){
				logInfo(CONTA_ + cth + " encerrada.");
				return true;
			} else {
				logError(CONTA_ + cth + " não encerrada por conter erros.");
				return false;
			}
		}
	}

	public void reinternarContaHospitalar(Integer cthSeq, Integer pacCodigo,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		RnCthcAtuReintVO vo = null;
		try {
			vo = getContaHospitalarRN()
					.rnCthcAtuReint(cthSeq, pacCodigo, nomeMicrocomputador,
							dataFimVinculoServidor);
		} catch (BaseException e) {
			logError("Exceção BaseException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.ERRO_EXECUTAR_REINTERNACAO,
					e.getMessage());
		}

		if (vo != null && !Boolean.TRUE.equals(vo.getRetorno())) {
			if (DominioSituacaoConta.E.equals(vo.getSituacao())) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.ERRO_EXECUTAR_REINTERNACAO_CONTA_ANTERIOR_DO_PACIENTE_JA_ESTA_ENCERRADA);
			} else {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.ERRO_EXECUTAR_REINTERNACAO_CONTA_NAO_TEVE_REINTERNACAO,
						String.valueOf(cthSeq));
			}
		}
	}

	public List<ContasPreenchimentoLaudosVO> obterContasPreenchimentoLaudos(
			final Date dtUltimaPrevia, final Short unfSeq,
			final String iniciaisPaciente) {

		final List<ContasPreenchimentoLaudosVO> result = getFatContasHospitalaresDAO()
				.obterContasPreenchimentoLaudos(dtUltimaPrevia,
						iniciaisPaciente);

		if (unfSeq != null && result != null && !result.isEmpty()) {
			/*
			 * Quando o parametro for null, a condicao abaixo SEMPRE será igual,
			 * pois esta chamando a função passando os mesmos valores
			 * (ain.unf_seq,ain.QRT_NUMERO,ain.LTO_LTO_ID), por isso executará
			 * apenas quando for diferente de null
			 * 
			 * and ainc_ret_unf_seq(ain.unf_seq,ain.QRT_NUMERO,ain.LTO_LTO_ID) =
			 * decode
			 * (:p_unf,null,ainc_ret_unf_seq(ain.unf_seq,ain.QRT_NUMERO,ain
			 * .LTO_LTO_ID),:p_unf)
			 */
			final List<ContasPreenchimentoLaudosVO> resultFinal = new ArrayList<ContasPreenchimentoLaudosVO>();

			for (ContasPreenchimentoLaudosVO reg : result) {
				if (CoreUtil.igual(
						unfSeq,
						getPesquisaInternacaoFacade().aincRetUnfSeq(
								reg.getUnfSeq(), reg.getNrQuarto(),
								reg.getLeitoId()))) {
					resultFinal.add(reg);
				}
			}

			return resultFinal;

		} else {
			return result;
		}
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public List<ResumoCobrancaAihVO> pesquisarEspelhoAih(Integer cthSeq, Boolean previa) {

		List<ResumoCobrancaAihVO> vos = getFatEspelhoAihDAO().pesquisarEspelhoAih(cthSeq);

		final List<ResumoCobrancaAihVO> result = new ArrayList<ResumoCobrancaAihVO>();

		for (ResumoCobrancaAihVO vo : vos) {
			if (vo != null) {
				populaVOPart1(vo);
				populaVOPart2(vo, previa);
			}
			result.add(vo);
		}

		return result;
	}
	
	
	private void populaVOPart1(ResumoCobrancaAihVO vo) {
		FatCaractFinanciamento item = getFaturamentoFacade().obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(vo.getPhoSeqRealz(), vo.getSeqRealz(),
				vo.getIphCodSusRealz());
		vo.setDetalhe1((item != null) ? item.getDescricao() : " ");

		if (vo.getProntuario() != null) {
			AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(vo.getProntuario());
			vo.setCodPaciente(paciente.getCodigo());
		}

		if (vo.getCthSeqReapresentada() != null) {
			FatContasHospitalares contaReapr = getFaturamentoFacade().obterContaHospitalar(vo.getCthSeqReapresentada());
			vo.setDetalhe2((contaReapr.getMotivoRejeicao() != null) ? contaReapr.getMotivoRejeicao().getDescricao() : " ");
		}

		FatContasHospitalares conta = getFaturamentoFacade().obterContaHospitalar(vo.getCthSeq());
		vo.setDetalhe3((conta.getEspecialidade() != null) ? conta.getEspecialidade().getNomeReduzido() : " ");

		if (vo.getEspecialidadeAih() != null) {
			AghClinicas clinica = getAghuFacade().obterClinica(Integer.valueOf(vo.getEspecialidadeAih()));
			vo.setEspecialidade(clinica.getDescricao());
		}
	}
	

	private void populaVOPart2(ResumoCobrancaAihVO vo, Boolean previa) {
		if (vo.getDciCpeMes() != null && vo.getDciCpeAno() != null) {
			String mesAno = StringUtils.leftPad(vo.getDciCpeMes().toString(), 2, "0") + vo.getDciCpeAno().toString();
			vo.setDtApresentacao(mesAno);
		}

		if (vo.getDciCpeAno() != null) {
			String modulo = vo.getDciCpeAno() + "02" + StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");
			Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
			vo.setDv(dv);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			Date dataAtual = new Date();
			String modulo = sdf.format(dataAtual) + "02" + StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");
			Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
			vo.setDv(dv);
		}

		if (!previa) {
			if (vo.getIndImprimeEspelho() != null && vo.getIndImprimeEspelho()) {
				vo.setReimpressao("REIMPRESSÃO");
			}
			vo.setListaServicos(getFaturamentoFacade().listarAtosMedicos(vo.getSeqp(), vo.getCthSeq()));
		} else {
			vo.setListaServicos(getFaturamentoFacade().listarAtosMedicosPrevia(vo.getSeqp(), vo.getCthSeq()));
		}
	}
	
	public Integer persistirContasCobradasEmLote(List<String> listCodigoDCHI, String servidorLogado, RapServidores servidorManuseado) {
		return fatContasHospitalaresDAO.persistirContasCobradasEmLote(listCodigoDCHI, servidorLogado, servidorManuseado);
	}

	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}
	
	protected ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}

}