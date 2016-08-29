package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpAgenciaBancoId;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptPrescricaoPacienteId;
import br.gov.mec.aghu.model.MptProcedimentoInternacao;
import br.gov.mec.aghu.model.MptProcedimentoInternacaoId;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class ItemContaHospitalarON extends AbstractFatDebugLogEnableRN {
	
	@EJB
	private ItemContaHospitalarRN itemContaHospitalarRN;

	private static final Log LOG = LogFactory.getLog(ItemContaHospitalarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3880157110989996213L;

	/**
	 * Método para persistir um item de conta hospitalar.
	 * 
	 * @param newItemCtaHosp
	 * @param oldItemCtaHosp
	 * @throws BaseException
	 */
	public void persistirItemContaHospitalar(final FatItemContaHospitalar newItemCtaHosp, final FatItemContaHospitalar oldItemCtaHosp, RapServidores servidorLogado,final Date dataFimVinculoServidor)
			throws BaseException {
		persistirItemContaHospitalar(newItemCtaHosp, oldItemCtaHosp, true, servidorLogado,dataFimVinculoServidor);
	}

	/**
	 * Método para atualizar um item de conta hospitalar. Busca a original e chama o persistir
	 */
	public void atualizarItemContaHospitalarSemValidacoesForms(final FatItemContaHospitalar item,	final boolean flush, 
			Date dataFimVinculoServidor, RapServidores servidorLogado,
			final Boolean pPrevia) throws BaseException {
		final FatItemContaHospitalar old = getFatItemContaHospitalarDAO().obterOriginal(item);
		atualizarItemContaHospitalarSemValidacoesForms(item, old, flush, servidorLogado,dataFimVinculoServidor, pPrevia, null);
	}
	
	
	/**
	 * Método para persistir um item de conta hospitalar.
	 * 
	 * @param newItemCtaHosp
	 * @param oldItemCtaHosp
	 * @throws BaseException
	 */
	public void persistirItemContaHospitalar(final FatItemContaHospitalar newItemCtaHosp, final FatItemContaHospitalar oldItemCtaHosp,
			final boolean flush, RapServidores servidorLogado,Date dataFimVinculoServidor) throws BaseException {
		AghParametros aghParametrosCbo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO);
		Short cboSeq = 0;
		if (aghParametrosCbo != null && aghParametrosCbo.getVlrNumerico() != null) {
			cboSeq = Short.valueOf(aghParametrosCbo.getVlrNumerico().shortValue());
		}

		//TODO: Revisar este if. Colocamos ele aqui segundo orientação do Ney porque as validações abaixo não devem ser feitas quando
		//se tratar de uma inclusão de itemContaHospitalar
		if(newItemCtaHosp.getId().getSeq() != null){
			if (newItemCtaHosp.getServidor() != null) {//TODO rever o ultimo parametro
				validaServResp(newItemCtaHosp.getServidor().getId().getMatricula(), newItemCtaHosp.getServidor().getId().getVinCodigo(), cboSeq);
			}
			
			if (newItemCtaHosp.getServidorAnest() != null) {//TODO rever o ultimo parametro
				validaServAnest(newItemCtaHosp.getServidorAnest().getId().getMatricula(), newItemCtaHosp.getServidorAnest().getId().getVinCodigo(), cboSeq);
			}
		}

		fatCompletaPhi(newItemCtaHosp, servidorLogado);

		//		if(flush){
			if (newItemCtaHosp.getId().getSeq() != null) {
				this.atualizarItemContaHospitalarSemValidacoesForms(newItemCtaHosp, oldItemCtaHosp,flush, servidorLogado, dataFimVinculoServidor, flush, flush);
			} else {
				this.inserirItemContaHospitalarSemValidacoesForms(newItemCtaHosp, flush, servidorLogado,dataFimVinculoServidor, null);
			}
		//		} else {
		//			if(newItemCtaHosp.getId().getSeq() != null){
		//				this.atualizarItemContaHospitalarSemValidacoesForms(newItemCtaHosp, oldItemCtaHosp, false);
		//			}else{
		//				this.inserirItemContaHospitalarSemValidacoesForms(newItemCtaHosp, true);
		//			}
		//		}
	}

	
	/**
	 * Método para inserir um item de conta hospitalar.
	 * 
	 * @param newItemCtaHosp
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void inserirItemContaHospitalarSemValidacoesForms(final FatItemContaHospitalar newItemCtaHosp, final boolean flush, RapServidores servidorLogado,
				final Date dataFimVinculoServidor, final Boolean pPrevia) throws BaseException {
		
		final ItemContaHospitalarRN itemContaHospitalarRN = this.getItemContaHospitalarRN();

		itemContaHospitalarRN.executarAntesInserirItemContaHospitalar(newItemCtaHosp,servidorLogado, dataFimVinculoServidor, pPrevia);
		
		// Somente executa trigger quando não for Encerramento
		// pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia)){
			itemContaHospitalarRN.executarAntesInserirItemContaHospitalar(newItemCtaHosp.getIseSoeSeq(), newItemCtaHosp.getIseSeqp(),
					newItemCtaHosp.getPrescricaoProcedimento() != null ? newItemCtaHosp.getPrescricaoProcedimento().getId().getAtdSeq() : null,
							newItemCtaHosp.getPrescricaoProcedimento() != null ? newItemCtaHosp.getPrescricaoProcedimento().getId().getSeq() : null,
									newItemCtaHosp.getPrescricaoNpt() != null ? newItemCtaHosp.getPrescricaoNpt().getId().getAtdSeq() : null,
											newItemCtaHosp.getPrescricaoNpt() != null ? newItemCtaHosp.getPrescricaoNpt().getId().getSeq() : null);
		}
		
		getFatItemContaHospitalarDAO().persistir(newItemCtaHosp);
		if (flush){
			getFatItemContaHospitalarDAO().flush();
		}
	}
	
	/**
	 * Metodo para inserir um item de conta hospitalar.
	 * 
	 * @param newItemCtaHosp
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void inserirItemContaHospitalarSemValidacoesForms(final FatItemContaHospitalar newItemCtaHosp, RapServidores servidorLogado,
			final Date dataFimVinculoServidor, final Boolean pPrevia) throws BaseException {
		inserirItemContaHospitalarSemValidacoesForms(newItemCtaHosp, true, servidorLogado,dataFimVinculoServidor, pPrevia);
	}

	/**
	 * Método para atualizar um item de conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarItemContaHospitalarSemValidacoesForms(final FatItemContaHospitalar newItemCtaHosp, RapServidores servidorLogado,
			final FatItemContaHospitalar oldItemCtaHosp, final Date dataFimVinculoServidor) throws BaseException {
		atualizarItemContaHospitalarSemValidacoesForms(newItemCtaHosp, oldItemCtaHosp, true, servidorLogado, dataFimVinculoServidor, null, null);
	}

	/**
	 * Método para atualizar um item de conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizarItemContaHospitalarSemValidacoesForms(final FatItemContaHospitalar newItemCtaHosp,
			final FatItemContaHospitalar oldItemCtaHosp, final boolean flush, RapServidores servidorLogado, 
			final Date dataFimVinculoServidor, final Boolean pPrevia, final Boolean pReabertura) throws BaseException {
		final ItemContaHospitalarRN itemContaHospitalarRN = this.getItemContaHospitalarRN();

		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();

		itemContaHospitalarRN.executarAntesAtualizarItemContaHospitalar(newItemCtaHosp, dataFimVinculoServidor, pPrevia);

		final Integer newIseSoeSeq = newItemCtaHosp.getIseSoeSeq();
		final Short newIseSeqp = newItemCtaHosp.getIseSeqp();
		final Integer newPnpAtdSeq = newItemCtaHosp.getPrescricaoNpt() != null ? newItemCtaHosp.getPrescricaoNpt().getId().getAtdSeq() : null;
		final Integer newPnpSeq = newItemCtaHosp.getPrescricaoNpt() != null ? newItemCtaHosp.getPrescricaoNpt().getId().getSeq() : null;
		final Integer newPprAtdSeq = newItemCtaHosp.getPrescricaoProcedimento() != null ? newItemCtaHosp.getPrescricaoProcedimento().getId()
				.getAtdSeq() : null;
		final Long newPprSeq = newItemCtaHosp.getPrescricaoProcedimento() != null ? newItemCtaHosp.getPrescricaoProcedimento().getId().getSeq()
				: null;

		Integer oldIseSoeSeq = null;
		Short oldIseSeqp = null;
		Integer oldPnpAtdSeq = null;
		Integer oldPnpSeq = null;
		Integer oldPprAtdSeq = null;
		Long oldPprSeq = null;
		if (oldItemCtaHosp != null) {
			oldIseSoeSeq = oldItemCtaHosp.getIseSoeSeq();
			oldIseSeqp = oldItemCtaHosp.getIseSeqp();
			oldPnpAtdSeq = oldItemCtaHosp.getPrescricaoNpt() != null ? oldItemCtaHosp.getPrescricaoNpt().getId().getAtdSeq() : null;
			oldPnpSeq = oldItemCtaHosp.getPrescricaoNpt() != null ? oldItemCtaHosp.getPrescricaoNpt().getId().getSeq() : null;
			oldPprAtdSeq = oldItemCtaHosp.getPrescricaoProcedimento() != null ? oldItemCtaHosp.getPrescricaoProcedimento().getId().getAtdSeq() : null;
			oldPprSeq = oldItemCtaHosp.getPrescricaoProcedimento() != null ? oldItemCtaHosp.getPrescricaoProcedimento().getId().getSeq() : null;
			oldItemCtaHosp.setAlteradoEm(new Date());
			oldItemCtaHosp.setAlteradoPor(servidorLogado.getUsuario());
		}

		itemContaHospitalarRN.executarAntesAtualizarItemContaHospitalar(oldIseSoeSeq, newIseSoeSeq, oldIseSeqp, newIseSeqp, oldPnpAtdSeq,
				newPnpAtdSeq, oldPnpSeq, newPnpSeq, oldPprAtdSeq, newPprAtdSeq, oldPprSeq, newPprSeq, pPrevia);

		fatItemContaHospitalarDAO.atualizar(newItemCtaHosp);
		if (flush){
			fatItemContaHospitalarDAO.flush();
		}

		// Somente deve fazer Journal quando não for Encerramento
		// pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia) && !Boolean.TRUE.equals(pReabertura)) {
			itemContaHospitalarRN.executarAposAtualizarItemContaHospitalar(newItemCtaHosp, oldItemCtaHosp);
		}
	}

	/**
	 * Método para remover um item de conta hospitalar.
	 * 
	 * @param newCtaHosp
	 * @param oldCtaHosp
	 * @throws ApplicationBusinessException
	 */
	public void removerContaHospitalar(final FatItemContaHospitalar oldCtaHosp, final Boolean pPrevia) throws ApplicationBusinessException {

		getFatItemContaHospitalarDAO().remover(oldCtaHosp);
		getFatItemContaHospitalarDAO().flush();
		// Somente deve fazer Journal quando não for Encerramento
	    // pPrevia = FALSE significa Encerramento
		if (!Boolean.FALSE.equals(pPrevia)) {
			getItemContaHospitalarRN().executarAposExcluirItemContaHospitalar(oldCtaHosp);
		}

	}

	/**
	 * validaServResp
	 * 
	 * @param ichSerMatriculaResp
	 * @param ichSerVinCodigoResp
	 * @param aghcAmbiente
	 * @throws ApplicationBusinessException
	 */
	public void validaServResp(final Integer ichSerMatriculaResp, final Short ichSerVinCodigoResp,
			Short vCboSeq) throws ApplicationBusinessException {

		validaServRespServAnest(ichSerMatriculaResp, ichSerVinCodigoResp, vCboSeq, false);

	}

	/**
	 * validaServAnest
	 * 
	 * @param ichSerMatriculaAnest
	 * @param ichSerVinCodigoAnest
	 * @param aghcAmbiente
	 * @throws ApplicationBusinessException
	 */
	public void validaServAnest(final Integer ichSerMatriculaAnest, final Short ichSerVinCodigoAnest,
			final Short vCboSeq) throws ApplicationBusinessException {

		validaServRespServAnest(ichSerMatriculaAnest, ichSerVinCodigoAnest, vCboSeq, true);

	}

	/**
	 * validaServRespServAnest
	 * 
	 * @param ichSerMatricula
	 * @param ichSerVinCodigo
	 * @param aghcAmbiente
	 * @param anest
	 * @throws ApplicationBusinessException
	 */
private void validaServRespServAnest(final Integer ichSerMatricula, final Short ichSerVinCodigo,
			final Short vCboSeq, final boolean anest) throws ApplicationBusinessException {

		//		Short vCboSeq;
		String vPesNome;
		Integer vPesCodigo;

		if (ichSerMatricula != null && ichSerVinCodigo != null) {
			//			if (aghcAmbiente.equals('P'))
			//				vCboSeq = 7;
			//			else if (aghcAmbiente.equals('E'))
			//				vCboSeq = 7;
			//			else
			//				vCboSeq = 2;

			final IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
			final RapServidoresId id = new RapServidoresId(ichSerMatricula, ichSerVinCodigo);
			final RapServidores rapServ = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(id);

			if (rapServ == null) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_01112);
			} else {
				vPesCodigo = rapServ.getPessoaFisica().getCodigo();
				vPesNome = rapServ.getPessoaFisica().getNome();

				final List<RapPessoaTipoInformacoes> cbos = getRegistroColaboradorFacade().listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(vPesCodigo, vCboSeq);

				if (cbos == null || cbos.isEmpty()) {
					if (!anest) {
						throw new ApplicationBusinessException(FaturamentoExceptionCode.SEM_CBO, vPesNome);
					} else {
						throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_01113);
					}
				}
			}
		}
	}

	/**
	 * Completa Itens da Conta Hospitalar
	 * 
	 * @param fatItemContHosp
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void fatCompletaPhi(final FatItemContaHospitalar fatItemContHosp, RapServidores servidorLogado) throws BaseException {
		final String phis[] = this.buscarVlrArrayAghParametro(AghuParametrosEnum.P_COMPLETA_PHIS);
		final Date dataFimVinculoServidor = new Date();
		
		Date vDthrAlta = null;
		Date vDthrInt = null;
		FatContasHospitalares contaHospitalar = fatItemContHosp.getContaHospitalar();
		if (contaHospitalar != null) {
			try {
				contaHospitalar.getDataInternacaoAdministrativa();
			} catch (final LazyInitializationException lie) {
				contaHospitalar = getFatContasHospitalaresDAO().obterFatContaHospitalar(contaHospitalar.getSeq());
			}
			vDthrInt = contaHospitalar.getDataInternacaoAdministrativa();
			vDthrAlta = contaHospitalar.getDtAltaAdministrativa();
		}
		if (vDthrAlta == null) {
			vDthrAlta = new Date();
		}
		final Integer vPhiSeq = fatItemContHosp.getProcedimentoHospitalarInterno().getSeq();
		Date vDthrRealizado = fatItemContHosp.getDthrRealizado();
		final Short vQtdInicial = fatItemContHosp.getQuantidadeRealizada();

		boolean igual = false;
		for (int x = 0; x < phis.length; x++) {
			if (vPhiSeq.equals(Integer.valueOf(phis[x]))) {
				igual = true;
			}
		}

		if (igual) {
			Integer vCompletos = 1;

			while (vCompletos < vQtdInicial) {
				final FatItemContaHospitalar newRecord = new FatItemContaHospitalar();
				final FatItemContaHospitalarId id = new FatItemContaHospitalarId(fatItemContHosp.getId().getCthSeq(), null);
				newRecord.setId(id);
				newRecord.setContaHospitalar(contaHospitalar);
				newRecord.setProcedimentoHospitalarInterno(fatItemContHosp.getProcedimentoHospitalarInterno());
				newRecord.setCriadoEm(new Date());
				newRecord.setCriadoPor(servidorLogado.getUsuario());
				newRecord.setIndSituacao(fatItemContHosp.getIndSituacao());
				newRecord.setIndOrigem(fatItemContHosp.getIndOrigem());
				newRecord.setLocalCobranca(fatItemContHosp.getLocalCobranca());

				vDthrRealizado = DateUtil.adicionaDias(vDthrRealizado, 1);

				if (DateUtil.truncaData(vDthrRealizado).equals(DateUtil.truncaData(vDthrAlta))) {
					vDthrRealizado = vDthrAlta;
				}
				fatItemContHosp.setQuantidadeRealizada((short) 1);
				// newRecord.setQuantidadeRealizada((short) 1);

				if ((vDthrRealizado.before(vDthrAlta) || vDthrRealizado.equals(vDthrAlta))
						&& (vDthrRealizado.after(vDthrInt))
						&& (fatItemContHosp.getProcedimentoHospitalarInterno() != null && fatItemContHosp.getProcedimentoHospitalarInterno().getSeq() != null)) {

					newRecord.setIchType(fatItemContHosp.getIchType());
					// FatProcedHospInternos procHospInt = new
					// FatProcedHospInternos();
					// newRecord.setProcedimentoHospitalarInterno(procHospInt);

					newRecord.getProcedimentoHospitalarInterno().setSeq(vPhiSeq);
					newRecord.setDthrRealizado(vDthrRealizado);
					newRecord.setQuantidadeRealizada((short) 1);

					inserirItemContaHospitalarSemValidacoesForms(newRecord, true, servidorLogado, dataFimVinculoServidor, null);
					// persiste a nova entidade
					// itemContaHospitalarDAO.inserir(newRecord);
				}

				vCompletos++;
			}
		}
	}

	/**
	 * Método para clonar um objeto FatItensContaHospitalar.
	 * 
	 * @param FatItemContaHospitalar
	 * @return Objeto FatItensContaHospitalar clonado.
	 * @throws Exception
	 */
	@SuppressWarnings({"PMD.SignatureDeclareThrowsException","PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public FatItemContaHospitalar clonarItemContaHospitalar(final FatItemContaHospitalar ich) throws Exception {
		final FatItemContaHospitalar cloneIch = (FatItemContaHospitalar) BeanUtils.cloneBean(ich);

		if (ich.getProcedimentoInternacao() != null) {
			final MptProcedimentoInternacao pi = new MptProcedimentoInternacao();
			final MptProcedimentoInternacaoId id = new MptProcedimentoInternacaoId();
			id.setAtdSeq(ich.getProcedimentoInternacao().getId().getAtdSeq());
			id.setSeq(ich.getProcedimentoInternacao().getId().getSeq());

			pi.setId(id);
			cloneIch.setProcedimentoInternacao(pi);
		}
		if (ich.getServidoresAlterado() != null) {
			final RapServidores serv = new RapServidores();
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula(ich.getServidoresAlterado().getId().getMatricula());
			id.setVinCodigo(ich.getServidoresAlterado().getId().getVinCodigo());

			serv.setId(id);
			cloneIch.setServidoresAlterado(serv);
		}
		if (ich.getItemRmps() != null) {
			final SceItemRmps ir = new SceItemRmps();
			final SceItemRmpsId id = new SceItemRmpsId();
			id.setNumero(ich.getItemRmps().getId().getNumero());
			id.setRmpSeq(ich.getItemRmps().getId().getRmpSeq());

			ir.setId(id);
			cloneIch.setItemRmps(ir);
		}
		if (ich.getProcedimentoHospitalarInterno() != null) {
			final FatProcedHospInternos phi = new FatProcedHospInternos();
			phi.setSeq(ich.getProcedimentoHospitalarInterno().getSeq());
			cloneIch.setProcedimentoHospitalarInterno(phi);
		}
		if (ich.getServidorAnest() != null) {
			final RapServidores serv = new RapServidores();
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula(ich.getServidorAnest().getId().getMatricula());
			id.setVinCodigo(ich.getServidorAnest().getId().getVinCodigo());

			serv.setId(id);
			cloneIch.setServidorAnest(serv);
		}
		if (ich.getUnidadesFuncional() != null) {
			final AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
			unf.setSeq(ich.getUnidadesFuncional().getSeq());
			cloneIch.setUnidadesFuncional(unf);
		}
		if (ich.getServidor() != null) {
			final RapServidores serv = new RapServidores();
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula(ich.getServidor().getId().getMatricula());
			id.setVinCodigo(ich.getServidor().getId().getVinCodigo());

			serv.setId(id);
			cloneIch.setServidor(serv);
		}
		if (ich.getProcedimentoAmbRealizado() != null) {
			final FatProcedAmbRealizado par = new FatProcedAmbRealizado();
			par.setSeq(ich.getProcedimentoAmbRealizado().getSeq());
			cloneIch.setProcedimentoAmbRealizado(par);
		}
		if (ich.getPrescricaoNpt() != null) {
			final MpmPrescricaoNpt pn = new MpmPrescricaoNpt();
			final MpmPrescricaoNptId id = new MpmPrescricaoNptId();
			id.setAtdSeq(ich.getPrescricaoNpt().getId().getAtdSeq());
			id.setSeq(ich.getPrescricaoNpt().getId().getSeq());

			pn.setId(id);
			cloneIch.setPrescricaoNpt(pn);
		}
		if (ich.getAgenciaBanco() != null) {
			final FcpAgenciaBanco ab = new FcpAgenciaBanco();
			final FcpAgenciaBancoId id = new FcpAgenciaBancoId();
			id.setBcoCodigo(ich.getAgenciaBanco().getId().getBcoCodigo());
			id.setCodigo(ich.getAgenciaBanco().getId().getCodigo());

			ab.setId(id);
			cloneIch.setAgenciaBanco(ab);
		}
		if (ich.getContaHospitalar() != null) {
			final FatContasHospitalares ch = new FatContasHospitalares();
			ch.setSeq(ich.getContaHospitalar().getSeq());
			cloneIch.setContaHospitalar(ch);
		}
		if (ich.getServidorCriado() != null) {
			final RapServidores serv = new RapServidores();
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula(ich.getServidorCriado().getId().getMatricula());
			id.setVinCodigo(ich.getServidorCriado().getId().getVinCodigo());

			serv.setId(id);
			cloneIch.setServidorCriado(serv);
		}
		if (ich.getPrescricaoPaciente() != null) {
			final MptPrescricaoPaciente pp = new MptPrescricaoPaciente();
			final MptPrescricaoPacienteId id = new MptPrescricaoPacienteId();
			id.setAtdSeq(ich.getPrescricaoPaciente().getId().getAtdSeq());
			id.setSeq(ich.getPrescricaoPaciente().getId().getSeq());

			pp.setId(id);
			cloneIch.setPrescricaoPaciente(pp);
		}
		if (ich.getPrescricaoProcedimento() != null) {
			final MpmPrescricaoProcedimento pp = new MpmPrescricaoProcedimento();
			final MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId();
			id.setAtdSeq(ich.getPrescricaoProcedimento().getId().getAtdSeq());
			id.setSeq(ich.getPrescricaoProcedimento().getId().getSeq());

			pp.setId(id);
			cloneIch.setPrescricaoProcedimento(pp);
		}
		if (ich.getProcEspPorCirurgias() != null) {
			final MbcProcEspPorCirurgias pepc = new MbcProcEspPorCirurgias();
			final MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
			MbcProcEspPorCirurgias procEspPorCir = getBlocoCirurgicoFacade().obterMbcProcEspPorCirurgiasPorChavePrimaria(ich.getProcEspPorCirurgias().getId());
			id.setCrgSeq(procEspPorCir.getCirurgia().getSeq());
			id.setEprEspSeq(procEspPorCir.getId().getEprEspSeq());
			id.setEprPciSeq(procEspPorCir.getId().getEprPciSeq());
			id.setIndRespProc(procEspPorCir.getId().getIndRespProc());

			pepc.setId(id);
			cloneIch.setProcEspPorCirurgias(pepc);
		}
		return cloneIch;
	}

	@Override
	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	@Override
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	@Override
	protected ItemContaHospitalarRN getItemContaHospitalarRN() {
		return itemContaHospitalarRN;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.blocoCirurgicoFacade;
	}
	
}
