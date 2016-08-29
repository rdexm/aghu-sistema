package br.gov.mec.aghu.blococirurgico.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcCirurgiasRN.MbcCirurgiasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.AgfaAdtDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRefCodeDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * Classe responsável por prover os métodos de negócios de verificações de MbcCirurgias.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcCirurgiasVerificacoesRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasVerificacoesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcRefCodeDAO mbcRefCodeDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private AgfaAdtDAO agfaAdtDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;
	
	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private MbcControleEscalaCirurgicaRN mbcControleEscalaCirurgicaRN;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IExamesFacade iExamesFacade;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 112998731137460724L;
	
	/**
	 * ORADB CONSTRAINTS Verificar as restrições de MBC_CIRURGIAS
	 * 
	 * @param cirurgia
	 */
	public void verificarRestricoesMbcCirurgias(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date dtIni = null;
		Date dtFim = null;
			 
		try {
		
			if(cirurgia.getDataInicioCirurgia()!=null){	dtIni = format.parse(format.format(cirurgia.getDataInicioCirurgia())); }
			if(cirurgia.getDataFimCirurgia()!=null){ dtFim = format.parse(format.format( cirurgia.getDataFimCirurgia())); }
	
		} catch (Exception e) {
			logError(e);
		}	
			
	 	if (!(CoreUtil.maior(cirurgia.getNumeroAgenda(), 0))) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_CRG_CK10);
		}
		if (!(cirurgia.getTempoPrevistoMinutos() == null || CoreUtil.menor(cirurgia.getTempoPrevistoMinutos(), 60))) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_CRG_CK11);
		}
		if (!(cirurgia.getAsa() == null || cirurgia.getAsa().getCodigo() < 6)) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_CRG_CK12);
		}
		this.getVerificarRestricoesComparacaoDatas(cirurgia.getDataInicioAnestesia(), cirurgia.getDataFimAnestesia(), MbcCirurgiasRNExceptionCode.MBC_CRG_CK13);
		
		//this.getVerificarRestricoesComparacaoDatas(cirurgia.getDataInicioCirurgia(), cirurgia.getDataFimCirurgia(), MbcCirurgiasRNExceptionCode.MBC_CRG_CK14);
		if(DateUtil.validaDataMaiorIgual(dtIni, dtFim)){
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_CRG_CK14);
		}
		
		this.getVerificarRestricoesComparacaoDatas(cirurgia.getDataEntradaSala(), cirurgia.getDataSaidaSala(), MbcCirurgiasRNExceptionCode.MBC_CRG_CK15);
		this.getVerificarRestricoesComparacaoDatas(cirurgia.getDataPrevisaoInicio(), cirurgia.getDataPrevisaoFim(), MbcCirurgiasRNExceptionCode.MBC_CRG_CK16);
		this.getVerificarRestricoesComparacaoDatas(cirurgia.getDataEntradaSr(), cirurgia.getDataSaidaSr(), MbcCirurgiasRNExceptionCode.MBC_CRG_CK17);
	}
	
	/**
	 * Verifica restrições através das datas
	 * 
	 * @param data1
	 * @param data2
	 * @param rnExceptionCode
	 * @throws ApplicationBusinessException
	 */
	private void getVerificarRestricoesComparacaoDatas(Date data1, Date data2, MbcCirurgiasRNExceptionCode rnExceptionCode) throws ApplicationBusinessException {

		boolean mbcCrgCk17Teste1 = data1 == null && data2 == null;
		boolean mbcCrgCk17Teste2 = data1 != null && data2 == null;
		boolean mbcCrgCk17Teste3 = data1 != null && data2 != null && data1.before(data2);

		if (!(mbcCrgCk17Teste1 || mbcCrgCk17Teste2 || mbcCrgCk17Teste3)) {
			throw new ApplicationBusinessException(rnExceptionCode);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_alt_elet
	 * 
	 * @param cirurgia
	 * @param V_VEIO_GERA_ESCALA_D
	 * @throws ApplicationBusinessException
	 */
	public void verificarAlteracaoEletiva(MbcCirurgias cirurgia, boolean V_VEIO_GERA_ESCALA_D) throws ApplicationBusinessException {

		if (!V_VEIO_GERA_ESCALA_D) {
			// Verifica se natureza do agendamento é eletiva
			if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {

				// Verifica permissoes
				boolean cirurNaoPrevista = getICascaFacade().usuarioTemPermissao(obterLoginUsuarioLogado(), "agendarCirurgiaNaoPrevista");
				boolean cirurAposEscala = getICascaFacade().usuarioTemPermissao(obterLoginUsuarioLogado(), "alterarCirurgiaAposEscala");
				boolean permiteAtualizarAcompanharCirurgia = getICascaFacade().usuarioTemPermissao(obterLoginUsuarioLogado(), "permiteAtualizarAcompanharCirurgia");

				// Verifica se existe uma escala definitiva
				boolean existeDefinitiva = getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(cirurgia.getUnidadeFuncional().getSeq(), cirurgia.getData(),
						DominioTipoEscala.D);

				if (existeDefinitiva && !(cirurNaoPrevista || cirurAposEscala || permiteAtualizarAcompanharCirurgia)) {
					// Não é permitido alterar cirurgia. Já foi executada a escala definitiva para esta data.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00438);
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_obrig_ns
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarObrigacaoNotaSala(MbcCirurgias cirurgia) throws ApplicationBusinessException {

		if (cirurgia.getDataDigitacaoNotaSala() != null && !DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

			// Se a unidade funcional possuir característica de ‘Bloco’ então:
			AghUnidadesFuncionais uniFunc = getAghuFacade().obterUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq());
			boolean isBloco = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(uniFunc.getSeq(), ConstanteAghCaractUnidFuncionais.BLOCO);
			if (isBloco) {

				if (cirurgia.getDataEntradaSala() == null) {
					// Data/hora entrada na sala deve ser informada.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00278);
				}

				if (cirurgia.getDataSaidaSala() == null) {
					// Data/hora saída da sala deve ser informada.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00279);
				}

				if (cirurgia.getDataInicioCirurgia() == null) {
					// Data/hora início da cirurgia deve ser informada.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00280);
				}

				if (cirurgia.getDataFimCirurgia() == null) {
					// Data/hora final da cirurgia deve ser informada.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00281);
				}
			}
		}
	}

	/**
	 * ORADB PROCEDURE mbck_crg_rn.rn_crgp_atu_origem
	 * 
	 * @param origemPacienteCirurgia
	 * @param dataHoraDigitaSala
	 * @param atendimento
	 * @param naturezaAgenda
	 * @param unidadesFuncionais
	 * @param data
	 * @param pacientes
	 * @param dthrInicioCirurgia
	 * @param especialidade
	 * @param servidor
	 * @throws BaseException 
	 */
	public AghAtendimentos atualizarOrigem(DominioOrigemPacienteCirurgia origemPacienteCirurgia, final Date dataHoraDigitaSala, final AghAtendimentos atendimento,
			final DominioNaturezaFichaAnestesia naturezaAgenda, final AghUnidadesFuncionais unidadesFuncionais, final Date data, final AipPacientes pacientes,
			final Date dthrInicioCirurgia, final AghEspecialidades especialidade) throws BaseException {
		
		if (origemPacienteCirurgia != null && origemPacienteCirurgia.equals(DominioOrigemPacienteCirurgia.I)) {
			
			return atualizarOrigemInternacao(dataHoraDigitaSala, atendimento, naturezaAgenda, unidadesFuncionais, data, pacientes);
			
		} 
		
		if (origemPacienteCirurgia != null && origemPacienteCirurgia.equals(DominioOrigemPacienteCirurgia.A)) {
			
			return atualizarOrigemAmbulatorio(origemPacienteCirurgia, dataHoraDigitaSala, atendimento, naturezaAgenda, unidadesFuncionais, data, pacientes, dthrInicioCirurgia, especialidade);
			
		}
		
		return atendimento; 
	}
	
	/**
	 * Divisão do método atualizarOrigem
	 * @param dataHoraDigitaSala
	 * @param atendimento
	 * @param naturezaAgenda
	 * @param unidadesFuncionais
	 * @param data
	 * @param pacientes
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AghAtendimentos atualizarOrigemInternacao(final Date dataHoraDigitaSala, final AghAtendimentos atendimento,
			final DominioNaturezaFichaAnestesia naturezaAgenda, final AghUnidadesFuncionais unidadesFuncionais, final Date data, final AipPacientes pacientes) throws ApplicationBusinessException {
		
		if (dataHoraDigitaSala != null) {
			
			if (atendimento == null) {
				
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00437);
				
			}
			
			if (atendimento.getOrigem().equals(DominioOrigemAtendimento.I) || atendimento.getOrigem().equals(DominioOrigemAtendimento.U)) {
				
				if (!(pacientes != null && pacientes.getCodigo().equals(atendimento.getPaciente().getCodigo()))) {
					
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00436);
					
				}
				
			} else {
				
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00435);
				
			}
			
		} else {
			
			if (naturezaAgenda.equals(DominioNaturezaFichaAnestesia.ELE)) {
				
				MbcControleEscalaCirurgica controleEscalaCirurgica = getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(unidadesFuncionais.getSeq(), data, DominioTipoEscala.D);
				
				if (controleEscalaCirurgica != null) {
					
					List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisaAtendimentoPacienteInternadoUrgencia(pacientes.getCodigo());
					
					if (listaAtendimentos != null && !listaAtendimentos.isEmpty()) {
						
						return listaAtendimentos.get(0);
						
					}
					
				}
				
			} else {
				
				List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisaAtendimentoPacienteInternadoUrgencia(pacientes.getCodigo());
				
				if (listaAtendimentos != null && !listaAtendimentos.isEmpty()) {
					
					return listaAtendimentos.get(0);
					
				}
				
			}
			
		}
		
		return atendimento;
		
	}
	
	/**
	 * Divisão do método atualizarOrigem
	 * @param dataHoraDigitaSala
	 * @param atendimento
	 * @param naturezaAgenda
	 * @param unidadesFuncionais
	 * @param data
	 * @param pacientes
	 * @return
	 * @throws BaseException 
	 */
	private AghAtendimentos atualizarOrigemAmbulatorio(DominioOrigemPacienteCirurgia origemPacienteCirurgia, final Date dataHoraDigitaSala, final AghAtendimentos atendimento,
			final DominioNaturezaFichaAnestesia naturezaAgenda, final AghUnidadesFuncionais unidadesFuncionais, final Date data, final AipPacientes pacientes,
			final Date dthrInicioCirurgia, final AghEspecialidades especialidade) throws BaseException {
		
		if (dataHoraDigitaSala != null) {
			
			List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisaAtendimentoPacientePorOrigem(pacientes.getCodigo(), DominioOrigemAtendimento.C, dthrInicioCirurgia);

			if (listaAtendimentos == null || listaAtendimentos.isEmpty()) {
				
				Integer atdSeq = getMbcControleEscalaCirurgicaRN().inserirAtendimentoCirurgiaAmbulatorio(atendimento, pacientes, dthrInicioCirurgia, unidadesFuncionais, especialidade, null);
				return getAghuFacade().obterAtendimentoPeloSeq(atdSeq);
			
			} else {
				
				return listaAtendimentos.get(0);
				
			}
			
		} else {
			
			if (naturezaAgenda.equals(DominioNaturezaFichaAnestesia.ELE)) {
				
				MbcControleEscalaCirurgica controleEscalaCirurgica = getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(unidadesFuncionais.getSeq(), data, DominioTipoEscala.D);
				
				if (controleEscalaCirurgica != null) {
					
					List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisaAtendimentoPacientePorOrigem(pacientes.getCodigo(), DominioOrigemAtendimento.U, null);
					
					if (listaAtendimentos == null || listaAtendimentos.isEmpty()) {
						
						List<AghAtendimentos> listaAtendimentosCirurgico = getAghuFacade().pesquisaAtendimentoPacientePorOrigem(pacientes.getCodigo(), DominioOrigemAtendimento.C, dthrInicioCirurgia);
						
						if (listaAtendimentosCirurgico == null || listaAtendimentosCirurgico.isEmpty()) {
							
							Integer atdSeq = getMbcControleEscalaCirurgicaRN().inserirAtendimentoCirurgiaAmbulatorio(atendimento, pacientes, dthrInicioCirurgia, unidadesFuncionais, especialidade, null);
							return getAghuFacade().obterAtendimentoPeloSeq(atdSeq);
							
						} else {
							
							return listaAtendimentosCirurgico.get(0);
							
						}
						
					} else {
						
						return listaAtendimentos.get(0);
						
					}
					
				}
				
			} else {
				
				List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisaAtendimentoPacientePorOrigem(pacientes.getCodigo(), DominioOrigemAtendimento.U, null);
				
				if (listaAtendimentos == null || listaAtendimentos.isEmpty()) {
					
					List<AghAtendimentos> listaAtendimentosCirurgico = getAghuFacade().pesquisaAtendimentoPacientePorOrigem(pacientes.getCodigo(), DominioOrigemAtendimento.C, dthrInicioCirurgia);
					
					if (listaAtendimentosCirurgico == null || listaAtendimentosCirurgico.isEmpty()) {
						
						Integer atdSeq = getMbcControleEscalaCirurgicaRN().inserirAtendimentoCirurgiaAmbulatorio(atendimento, pacientes, dthrInicioCirurgia, unidadesFuncionais, especialidade, null);
						return getAghuFacade().obterAtendimentoPeloSeq(atdSeq);
					
					} else {
						
						return listaAtendimentosCirurgico.get(0);
						
					}
					
				} else {
					
					return listaAtendimentos.get(0);
					
				}
				
			}
			
		}
		
		return atendimento;
		
	}

	/**
	 * Verifica se os campos data, nro_agenda, criado_em e servidor foram alterados
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void verificarCamposNaoAlteraveis(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		// RN7 do documento de análise
		if (!DateValidator.validarMesmoDia(cirurgia.getData(), cirurgiaOld.getData())) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00283);
		}

		// RN8 do documento de análise
		if (CoreUtil.modificados(cirurgia.getNumeroAgenda(), cirurgiaOld.getNumeroAgenda())) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00284);
		}

		// RN9 do documento de análise
		if (!DateValidator.validarMesmoDia(cirurgia.getCriadoEm(), cirurgiaOld.getCriadoEm())) {	
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00285);
		}

		// RN10 do documento de análise	
		if (cirurgia.getServidor().getId().getMatricula() == null) {
			cirurgia.setServidor(servidorLogadoFacade.obterServidorLogado());
		}

		if (CoreUtil.modificados(cirurgia.getServidor().getId(), cirurgiaOld.getServidor().getId())) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00393);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_cancelam
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void varificaCancelamento(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		// RN12.1 Sejá estava cancelada e passou para outra situação, então joga msg
		if (DominioSituacaoCirurgia.CANC.equals(cirurgiaOld.getSituacao()) && CoreUtil.modificados(cirurgia.getSituacao(), cirurgiaOld.getSituacao())) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00318);
		}

		// RN12.2
		AghParametros parametroLiberaCancNotaDigi = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIBERA_CANC_NOTA_DIGIT);
		if (parametroLiberaCancNotaDigi.getVlrTexto() != null && parametroLiberaCancNotaDigi.getVlrTexto().equals("N")
				&& DominioSituacaoCirurgia.RZDA.equals(cirurgiaOld.getSituacao()) && CoreUtil.modificados(cirurgia.getSituacao(), cirurgiaOld.getSituacao())
				&& cirurgiaOld.getDigitaNotaSala()) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00510);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_sala
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarSalaCirurgica(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// RN16.1 Se situacao for diferente de 'CANC','AGND','PREP' e sci_seqp for nulo MBC_00347
		if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()) && !DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao())
				&& !DominioSituacaoCirurgia.PREP.equals(cirurgia.getSituacao()) && cirurgia.getSalaCirurgica() == null) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00347);
		}

		// RN16.2 Se sci_seqp for diferente de nulo e unf_seq for diferente de sci_unf_seq MBC_00348
		if (cirurgia.getSalaCirurgica() != null && cirurgia.getUnidadeFuncional() == null
				|| !cirurgia.getUnidadeFuncional().getSeq().equals(cirurgia.getSalaCirurgica().getId().getUnfSeq())) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00348);
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_ver_sl_ativa
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarSalaCirurgicaAtiva(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getSalaCirurgica() != null) {
			if (!cirurgia.getSalaCirurgica().getSituacao().isAtivo()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01326);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_tempos
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarTempoCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {

		Short v_tempo_hrs = 0;
		Short v_tempo_min = 0;

		if (cirurgia.getTempoPrevistoHoras() != null) {
			v_tempo_hrs = cirurgia.getTempoPrevistoHoras();
		}

		if (cirurgia.getTempoPrevistoMinutos() != null) {
			v_tempo_min = cirurgia.getTempoPrevistoMinutos() != null ? cirurgia.getTempoPrevistoMinutos().shortValue() : 0;
		}

		// c_unf
		AghUnidadesFuncionais c_unf = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(cirurgia.getUnidadeFuncional().getSeq());

		final Short v_tempo_minimo = c_unf.getTempoMinimoCirurgia();
		final Short v_tempo_maximo = c_unf.getTempoMaximoCirurgia();

		final Integer v_tempo_cirurgia = (v_tempo_hrs * 60) + v_tempo_min;

		if (CoreUtil.menor(v_tempo_cirurgia, v_tempo_minimo)) {
			// Tempo da cirurgia não pode ser menor que o tempo de ocupação padrão daunidade
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00367);
		}

		if (CoreUtil.maior(v_tempo_cirurgia, v_tempo_maximo)) {
			// Tempo da cirurgia não pode ser menor que o tempo de ocupação padrão da unidade
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00368);
		}

	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_convenio
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarConvenio(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getConvenioSaudePlano() == null) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00219);
		}

		if (!cirurgia.getConvenioSaude().getSituacao().isAtivo()) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00220);
		}

		if (!cirurgia.getConvenioSaudePlano().getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01349);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_demora
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarDemoraSR(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getMotivoDemoraSalaRecuperacao() != null) {
			if (cirurgia.getDataEntradaSr() == null) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00223);
			}

			if (!cirurgia.getMotivoDemoraSalaRecuperacao().getSituacao().isAtivo()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00221_2);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_atraso
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarAtraso(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getMotivoAtraso() != null) {
			if (!cirurgia.getMotivoAtraso().getSituacao().isAtivo()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00232);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_mot_canc
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarMotivoCancelamento(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getMotivoCancelamento() != null) {
			if (!cirurgia.getMotivoCancelamento().getSituacao().isAtivo()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00222);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_destino
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarDestino(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getDestinoPaciente() != null) {
			MbcDestinoPaciente destinoPaciente = this.mbcDestinoPacienteDAO.obterPorChavePrimaria(cirurgia.getDestinoPaciente().getSeq());			
			if (!destinoPaciente.getSituacao().isAtivo()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00242);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_dest_pac
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarDestinoPaciente(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {

		final Short unfSeq = cirurgia.getUnidadeFuncional().getSeq();
		boolean isBloco = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.BLOCO);
		boolean isHemodinamica = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.HEMODINAMICA);
		boolean isCca = 	this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.CCA);

		AghParametros parametroDestPaciente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);

		if (isBloco || isHemodinamica || isCca) {

			// Verifico se passou de uma situação para outra e se a nova é realizada ou cancelada
			if (CoreUtil.modificados(cirurgia.getSituacao(), cirurgiaOld.getSituacao())
					&& (DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()))) {

				// Se foi realizada e não foi informado o destino do paciente MBC_00394
				if (cirurgia.getDestinoPaciente() == null && DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao())) {
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00394);
				}
			}

			// Verifico se o destino do paciente é salá de recuperação
			if (cirurgia.getDestinoPaciente() != null && cirurgia.getDestinoPaciente().getSeq().equals(parametroDestPaciente.getVlrNumerico().byteValue())) {
				if (cirurgia.getDataEntradaSr() == null) {
					cirurgia.setDataEntradaSr(new Date());
				}
				cirurgia.setDataSaidaSr(null);
			} else {
				// Verifico se a SR antiga é igual ao valor do parametro && diferente da nova SR
				if (cirurgiaOld.getDestinoPaciente() != null && cirurgia.getDestinoPaciente() != null && cirurgiaOld.getDestinoPaciente().getSeq().equals(parametroDestPaciente.getVlrNumerico().byteValue())
						&& !CoreUtil.igual(cirurgiaOld.getDestinoPaciente(), cirurgia.getDestinoPaciente())) {
					if (cirurgia.getDataSaidaSr() == null){
						cirurgia.setDataSaidaSr(new Date());
					}
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_sltc_exm
	 * 
	 * @param cirurgia
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSolicitacoesExames(MbcCirurgias cirurgia, final String nomeMicrocomputador) throws ApplicationBusinessException {
		if (cirurgia.getAtendimento() != null && DominioOrigemAtendimento.C.equals(cirurgia.getAtendimento().getOrigem())) {
			List<AelSolicitacaoExames> solics = getExamesFacade().buscarSolicitacaoExamesPorAtendimento(cirurgia.getAtendimento());
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			if (!solics.isEmpty()) {
				for (AelSolicitacaoExames aelSolicitacaoExames : solics) {
					// Atualização do convenio/plano de cada solicitação
					aelSolicitacaoExames.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
					try {
						// atualizo no banco passando pelas regras das solicitações.
						getSolicitacaoExameFacade().atualizar(aelSolicitacaoExames, null, nomeMicrocomputador, servidorLogado);
					} catch (BaseException e) {
						logError(e);
						throw new ApplicationBusinessException(e);
					}
				}
			}
		}
	}
	
	/**
	 * ORADB mbck_crg_rn2.rn_crgp_ver_nec_anes
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void verificarNecessidadeAnestesista(Integer crgSeq) throws ApplicationBusinessException {
		
		List<MbcTipoAnestesias> tipoAnestesias = getMbcAnestesiaCirurgiasDAO().listarTipoAnestesias(crgSeq, true);
		
		if (tipoAnestesias.isEmpty()) {
		
			List<MbcProfCirurgias> anestesistas = getMbcProfCirurgiasDAO().pesquisarProfCirurgiasAnestesistaPorCrgSeq(crgSeq);
			
			if (anestesistas != null && !anestesistas.isEmpty()) {
				
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00391);
				
			}
			
		}
		
	}

	/**
	 * Verifica se existe procedimento principal e seleciona o mesmo pra ser usado em outra regra.
	 * 
	 * @param cirurgia
	 * @param espProcdCirur
	 * @param v_qtde_proc
	 * @param v_pci_tipo
	 * @throws ApplicationBusinessException
	 */
	public void verificarProcedimentoPrincipal(MbcCirurgias cirurgia, MbcEspecialidadeProcCirgs espProcdCirur, Byte v_qtde_proc, DominioTipoProcedimentoCirurgico v_pci_tipo)
	throws ApplicationBusinessException {
		Set<MbcProcEspPorCirurgias> procCirurgicos = cirurgia.getProcEspPorCirurgias();
		if (procCirurgicos != null) {
			for (MbcProcEspPorCirurgias procCirurgico : procCirurgicos) {
				if (DominioIndRespProc.AGND.equals(procCirurgico.getId().getIndRespProc()) && procCirurgico.getIndPrincipal() && procCirurgico.getSituacao().isAtivo()) {
					espProcdCirur = procCirurgico.getMbcEspecialidadeProcCirgs();

					v_qtde_proc = procCirurgico.getQtd();
					v_pci_tipo = procCirurgico.getProcedimentoCirurgico().getTipo();
					break;
				}
			}
		}

		if (espProcdCirur == null && v_qtde_proc == null && v_pci_tipo == null) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00977);
		}
	}

	/**
	 * Deixa a data somente com a hora e minuto que é o importante
	 * 
	 * @param data
	 * @return
	 */
	public int getDiaSemana(Date data) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(data);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Monta uma data somente com hora e minuto
	 * 
	 * @param hora
	 * @param min
	 * @return
	 */
	public Date getHoraSemData(int hora, int min) {
		Calendar c = Calendar.getInstance();
		c.clear();

		c.set(Calendar.YEAR, 1970);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MILLISECOND, 0);

		c.set(Calendar.HOUR, hora);
		c.set(Calendar.MINUTE, min);
		return c.getTime();
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_ver_alt_unid
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
//	 "ALTERAR UNIDADE CIRURGICA"
	public void verificarAlteracaoUnidadeCirurgica(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		boolean permAlterarUnidade = getICascaFacade().usuarioTemPermissao(obterLoginUsuarioLogado(), "permiteAlterarUnidadeCirurgica");
		if (!permAlterarUnidade) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01101);
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_ver_cnv_proj
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarProjetoPesquisa(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getProjetoPesquisa() != null) {

			AghParametros parametroPgdSeqPesquisa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PGD_SEQ_PESQUISA);

			if (cirurgia.getConvenioSaude() != null
					&& (cirurgia.getConvenioSaude().getPagador() == null || !cirurgia.getConvenioSaude().getPagador().getSeq()
							.equals(parametroPgdSeqPesquisa.getVlrNumerico().shortValue()))) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01369);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_atu_cota_pjq
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void atualizarCotaProcedimentos(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		getMbcProcEspPorCirurgiasDAO().atualizarCotaProcedimetosCirurgiaCallableStatement(cirurgia.getSeq(),
				cirurgia.getProjetoPesquisa() != null ? cirurgia.getProjetoPesquisa().getSeq() : null);
	}

	/**
	 * rn_crgp_atu_agfa
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSistemaImagensAGFA(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		if (cirurgiaOld != null && cirurgiaOld.getPaciente() != null && cirurgia.getPaciente() != null) {
			
			getAgfaAdtDAO().atualizarInformacoesPacienteAGFACallableStatement(cirurgiaOld.getPaciente().getCodigo(), cirurgia.getPaciente().getCodigo(), cirurgia.getSeq());
			
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_cirg_regime
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarCirurgiaRegime(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getConvenioSaude().getGrupoConvenio().equals(DominioGrupoConvenio.S) && cirurgia.getOrigemPacienteCirurgia() != null) {

			List<MbcProcEspPorCirurgias> listaProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO()
			.listarMbcProcEspPorCirurgiasPorCrgSeq(cirurgia.getSeq(), false, true, false);

			for (MbcProcEspPorCirurgias procEspPorCirurgia : listaProcEspPorCirurgias) {
				MbcProcedimentoCirurgicos procedimento = procEspPorCirurgia.getProcedimentoCirurgico();
				// se for diferente de A=ambulatorio vai ser igual a um dos tres 'H','9','I'
				if (procedimento.getRegimeProcedSus() != null && DominioOrigemPacienteCirurgia.A.equals(cirurgia.getOrigemPacienteCirurgia())
						&& !DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO.equals(procedimento.getRegimeProcedSus())) {
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01826, procedimento.getRegimeProcedSus().getDescricao());
				}
			}
		}
	}

	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected MbcRefCodeDAO getMbcRefCodeDAO() {
		return mbcRefCodeDAO;
	}

	protected IExamesFacade getExamesFacade() {
		return iExamesFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.iSolicitacaoExameFacade;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected AgfaAdtDAO getAgfaAdtDAO() {
		return agfaAdtDAO;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	private MbcControleEscalaCirurgicaRN getMbcControleEscalaCirurgicaRN(){
		return mbcControleEscalaCirurgicaRN;
	} 

	
}