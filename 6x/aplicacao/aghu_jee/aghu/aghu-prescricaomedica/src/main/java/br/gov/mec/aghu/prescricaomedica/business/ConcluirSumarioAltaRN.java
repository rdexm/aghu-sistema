package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.ConcluirSumarioAltaON.ConcluirSumarioAltaONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 *  Responsavel pelas regras para conclusao de um Sumario de Alta.
 *  
 * @author rcorvalao
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ConcluirSumarioAltaRN extends BaseBusiness {

	private static final String PARAMETRO_INVALIDO = "Parametro Invalido!!!";

	@EJB
	private ManterObtCausaAntecedenteRN manterObtCausaAntecedenteRN;
	
	@EJB
	private ConfirmarPrescricaoMedicaRN confirmarPrescricaoMedicaRN;
	
	@EJB
	private ManterAltaDiagMtvoInternacaoRN manterAltaDiagMtvoInternacaoRN;
	
	@EJB
	private ManterAltaEstadoPacienteRN manterAltaEstadoPacienteRN;
	
	@EJB
	private ManterAltaMotivoRN manterAltaMotivoRN;
	
	@EJB
	private ManterObtGravidezAnteriorON manterObtGravidezAnteriorON;
	
	@EJB
	private ManterObtCausaDiretaRN manterObtCausaDiretaRN;
	
	@EJB
	private ManterAltaEvolucaoRN manterAltaEvolucaoRN;
	
	@EJB
	private ManterAltaPlanoRN manterAltaPlanoRN;
	
	@EJB
	private ManterObitoNecropsiaRN manterObitoNecropsiaRN;
	
	@EJB
	private ManterAltaDiagPrincipalRN manterAltaDiagPrincipalRN;
	
	@EJB
	private LaudoRN laudoRN;
	
	private static final Log LOG = LogFactory.getLog(ConcluirSumarioAltaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MpmTipoLaudoDAO mpmTipoLaudoDAO;

    @Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8044991089719028969L;

	/**
	 * Regra 03: Validação do campo Data/Hora da Alta, o mesmo não poderá ser nulo.<br>
	 * 
	 * @param altaSumario
	 * @return <code>null</code> caso seja uma data hora valida, em outros casos retorna uma ApplicationBusinessException.
	 * 
	 */
	protected ApplicationBusinessException verificarDataHoraAlta(MpmAltaSumario altaSumario) {
		
		if (altaSumario == null || altaSumario.getId() == null || altaSumario.getTipo() == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		ApplicationBusinessException returnValue = null;
		
		MpmAltaSumario altaSumarioBD = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaSumarioBD.getDthrAlta() == null) {
			
			if (DominioIndTipoAltaSumarios.ALT.equals(altaSumarioBD.getTipo())) {
				
				returnValue = new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02858);
			
			} else if (altaSumarioBD.getDthrAlta() == null && DominioIndTipoAltaSumarios.OBT.equals(altaSumarioBD.getTipo())) {
			
				returnValue = new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02869);
			
			} else {
			
				returnValue = new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02858);
			
			}
		
		}
		
		return returnValue;
	
	}
	
	/**
	 * Validações para conclusao do Sumario de Alta.
	 * 
	 * Form: MPMF_SUMARIO_ALTA
	 * Pll: MPMF_SUMARIO_ALTA
	 * Procedure: MPMC_OK -> RN_ASUP_VER_ALTA
	 * 
	 * @ORADB Package mpmk_asu_rn.rn_asup_ver_alta
	 * 
	 * @param altaSumario
	 */
	public void validacoesConclusaoSumarioAlta(MpmAltaSumario altaSumario) throws BaseException {
		BaseListException erros = new BaseListException();
		
		erros.add(this.verificarDataHoraAlta(altaSumario));
		erros.add(this.validarAltaDiagMtvoInternacao(altaSumario.getId()));
		erros.add(this.validarAltaDiagPrincipal(altaSumario.getId()));
		erros.add(this.validarAltaEvolucao(altaSumario.getId()));
		
		if (altaSumario.getTipo().equals(DominioIndTipoAltaSumarios.ALT)) {
			
			erros.add(this.validarAltaEstadoPaciente(altaSumario.getId()));
			erros.add(this.validarMotivoAltaPaciente(altaSumario.getId()));
			erros.add(this.validarAltaPlano(altaSumario.getId()));
			
		} else if (altaSumario.getTipo().equals(DominioIndTipoAltaSumarios.OBT)) {
			
			erros.add(this.validarCausaDiretaObito(altaSumario.getId()));
			erros.add(this.validarCausaAntecedenteObito(altaSumario.getId()));
			erros.add(this.validarNecropsiaObito(altaSumario.getId()));
			erros.add(this.validarGravidezAnteriorObito(altaSumario));
		}
		
		if (erros.hasException()) {
			throw erros;
		}
		
	}
	
	/**
	 * Método responsável pela validação
	 * da Gravidez Anterior Obito.
	 * 
	 * @author lsamberg
	 * 
	 * @param altaSumarioId
	 * @return
	 *  
	 */
	protected BaseException validarGravidezAnteriorObito(MpmAltaSumario altaSumario) {
		if(altaSumario.getSexo().equals(DominioSexo.M)){
			if(this.getManterObtGravidezAnteriorON().validaNenhumaGravidezAnteriorMasculina(altaSumario.getId())){
				return null;
			}else{
				return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_03342);
			}
		}else{
			if(this.getManterObtGravidezAnteriorON().validaAoMenosUmaGravidezAnterior(altaSumario.getId())){
				
				if(this.getManterObtGravidezAnteriorON().validarMaisDeUmaGravidezAnterior(altaSumario.getId())){
					return null;
				}else{
					return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_03341);
				}
				
			}else{
				return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_03340);
			}
		}
	}

	/**
	 * Método responsável pela validação
	 * da Necropsia.
	 * 
	 * @author lsamberg
	 * 
	 * @param altaSumarioId
	 * @return
	 *  
	 */
	protected BaseException validarNecropsiaObito(MpmAltaSumarioId altaSumarioId) {
		
		if(this.getManterObitoNecropsiaRN().validaAoMenosUmaNecropsia(altaSumarioId)){
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02716);
		}
		
	}

	/**
	 * Método responsável pela validação
	 * da Causa Antecedente de Obito.
	 * 
	 * @author lsamberg
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws ApplicationBusinessException 
	 *  
	 */
	protected BaseException validarCausaAntecedenteObito(MpmAltaSumarioId altaSumarioId) throws ApplicationBusinessException {
		
		if (this.getManterObtCausaAntecedenteRN().validarAoMenosUmaCausaAntecedente(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02715);
		}
		
	}

	/**
	 * Método responsável pela validação
	 * da Causa Direta de Obito.
	 * 
	 * @author lsamberg
	 * 
	 * @param altaSumarioId
	 * @return
	 *  
	 */
	protected BaseException validarCausaDiretaObito(MpmAltaSumarioId altaSumarioId) {
		
		if(this.getManterObtCausaDiretaRN().validarAoMenosUmaCausaDireta(altaSumarioId)){
			
			if(this.getManterObtCausaDiretaRN().validarMaisDeUmaCausaDireta(altaSumarioId)){
				return null;
			}else{
				return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02719);
			}
			
		}else{
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02714);
		}
				
	}

	/**
	 * Método responsável pela validação
	 * da alta do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 *  
	 */
	protected ApplicationBusinessException validarMotivoAltaPaciente(MpmAltaSumarioId altaSumarioId) {
		
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaMotivoRN().validarMotivoAltaPaciente(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02720);
		}
	}
	
	
	/**
	 * Método que verifica a validação 
	 * do plano da alta do paciente. Deve 
	 * pelo menos ter um registro associado.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws BaseException
	 */
	protected ApplicationBusinessException validarAltaPlano(MpmAltaSumarioId altaSumarioId) {
		
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaPlanoRN().validarAltaPlano(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02722);
		}
	}
	
	/**
	 * Método que verifica a validação
	 * do estado clínico do paciente. Deve
	 * pelo menos ter um registro associado 
	 * ao sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws BaseException
	 */
	protected ApplicationBusinessException validarAltaEstadoPaciente(MpmAltaSumarioId altaSumarioId) {
		
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaEstadoPacienteRN().validarAltaEstadoPaciente(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02723);
		}
	}
	
	
	/**
	 * Método que valida a verificação
	 * da evolução da alta do paciente. Deve
	 * ter pelo menos um registro associado ao
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws BaseException
	 */
	protected ApplicationBusinessException validarAltaEvolucao(MpmAltaSumarioId altaSumarioId) {
		
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaEvolucaoRN().validarAltaEvolucao(altaSumarioId)) {
			
		
			return null;
		
		} else {
	
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02724);
	
		}

	}
	
	
	/**
	 * 
	 * Método que verifica a validação
	 * do diagnóstico de mtvo, da internação 
	 * da alta do paciente. Deve pelo menos 
	 * ter um registro ativo associado ao 
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws BaseException
	 */
	protected ApplicationBusinessException validarAltaDiagMtvoInternacao(MpmAltaSumarioId altaSumarioId) {
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaDiagMtvoInternacaoRN().validarAltaDiagMtvoInternacao(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02717);
	
		}
	}
	
	
	/**
	 * Método que verifica a validação
	 * do diagnóstico principal da alta 
	 * do paciente. Deve pelo menos ter 
	 * um registro ativo associado ao sumário 
	 * do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 * @throws BaseException
	 */
	protected ApplicationBusinessException validarAltaDiagPrincipal(MpmAltaSumarioId altaSumarioId) {
		
		if (altaSumarioId == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		if (this.getManterAltaDiagPrincipalRN().validarAltaDiagPrincipal(altaSumarioId)) {
			return null;
		} else {
			return new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MPM_02718);
		}
	}
	
	/**
	 * Busca o <b>valor</b> do parametro P_LAUDO_MENOR_PERM.<br>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghParametros getParametroSistemaLaudoMenorPermanencia() throws ApplicationBusinessException {
		return this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LAUDO_MENOR_PERM);
	}
	
	/**
	 * Retorna o Convenio de um Atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */
	private FatConvenioSaudePlano getFatConvenioSaudePlano(AghAtendimentos atendimento) {
		return this.getConfirmarPrescricaoMedicaRN().obterConvenioAtendimento(atendimento);
	}
	
	/**
	 * Verifica se o convenio eh SUS.<br>
	 * <b>Atraves do relacionamento:</b><br>
	 * mpmAltaSumario.aghAtendimento busca o convenio -> FatConvenioSaudePlano<br>
	 * <b>fatConvenioSaudePlano.fatConvenioSaude.grupoConvenio</b> for igual a <b>SUS</b>.
	 * 
	 * @param altaSumario
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean ehConvenioSUS(MpmAltaSumario altaSumario) {
		if (altaSumario == null || altaSumario.getAtendimento() == null) {
			throw new IllegalArgumentException("Parametro AltaSumario invalido!!!");
		}
		
		FatConvenioSaudePlano convenio = null;
		FatConvenioSaude convenioSaude = null;
		
		convenio = this.getFatConvenioSaudePlano(altaSumario.getAtendimento());
		
		if (convenio == null && altaSumario.getAtendimento().getOrigem().equals(DominioOrigemAtendimento.N)) {
			convenio = this.getFatConvenioSaudePlano(altaSumario.getAtendimento().getAtendimentoMae());
		}
		
		if (convenio != null) {
			convenioSaude = convenio.getConvenioSaude();
			return DominioGrupoConvenio.S == convenioSaude.getGrupoConvenio();
		}
		
		return false;
		
	}
	
	/**
	 * Verifica se existe Laudo menor permanencia pendente de Justificativa para a AltaSumario.<br>
	 * Utiliza o parametro de sistema P_LAUDO_MENOR_PERM.<br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean existeLaudoMenorPermanenciaPendenteJustificativa(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario == null || altaSumario.getAtendimento() == null) {
			throw new IllegalArgumentException();
		}
		
		AghParametros parametro = this.getParametroSistemaLaudoMenorPermanencia();
		Short menorPermanencia = (parametro != null && parametro.getVlrNumerico() != null) ? parametro.getVlrNumerico().shortValue() : 0;

        AghAtendimentos atendimentos = aghAtendimentoDAO.merge(altaSumario.getAtendimento());
		List<MpmLaudo> listaLaudoMenorPermanenciaPendente = this.getMpmLaudoDAO().buscaLaudoMenorPermaneciaPendenteJustificativa(atendimentos, menorPermanencia);
		
		return (listaLaudoMenorPermanenciaPendente != null && !listaLaudoMenorPermanenciaPendente.isEmpty());
	}
	
	/**
	 * 
	 * @ORADB Procedure MPMP_GERA_LAUDOS. Forms MPMF_SUMARIO_ALTA.
	 * 
	 * @param altaSumario
	 * @throws BaseException 
	 */
	public void gerarLaudos(MpmAltaSumario altaSumario) throws BaseException {
		//Busca convênio do atendimento
		FatConvenioSaudePlano convenio = this.getFatConvenioSaudePlano(altaSumario.getAtendimento());
		
		// Busca laudos na agh_parametros
		AghParametros parametro = this.getParametroSistemaLaudoMenorPermanencia();
		Short vLaudoMenorPerm = parametro.getVlrNumerico().shortValue(); 
		
		if (convenio != null) {
			
			// Rotina de laudo permanencia menor
			MpmTipoLaudo tipoLaudo = this.getMpmTipoLaudoDAO().buscaTipoLaudoMenorPermanencia(vLaudoMenorPerm, convenio.getId().getCnvCodigo(), convenio.getId().getSeq());
			if (tipoLaudo != null) {
				AinInternacao internacao = altaSumario.getAtendimento().getInternacao();

				Date dthrInternacao = new Date();
				Short quantDiasFaturamento = 0;

				if (internacao != null) {
					FatItensProcedHospitalarId chavePrimaria = new FatItensProcedHospitalarId(internacao.getIphPhoSeq(), internacao.getIphSeq());
					FatItensProcedHospitalar itemProcedHospitalar = this.getFaturamentoFacade().obterItemProcedHospitalarPorChavePrimaria(chavePrimaria);

					quantDiasFaturamento = itemProcedHospitalar.getQuantDiasFaturamento() == null ? 0 : itemProcedHospitalar.getQuantDiasFaturamento();
					dthrInternacao = internacao.getDthrInternacao();
				}

				Integer diffDatas = DateUtil.diffInDaysInteger(new Date(), dthrInternacao);
				Integer dias = quantDiasFaturamento / 2;
				if (diffDatas < dias) {
					MpmLaudo laudo = this.getMpmLaudoDAO().buscaMpmLaudo(altaSumario.getAtendimento(), vLaudoMenorPerm);

					if (laudo == null) {
						MpmTipoLaudo tl = this.getMpmTipoLaudoDAO().obterPorChavePrimaria(vLaudoMenorPerm);

						MpmLaudo laudoNovo = new MpmLaudo();

						laudoNovo.setDthrInicioValidade(dthrInternacao);
						laudoNovo.setContaDesdobrada(Boolean.FALSE);
						laudoNovo.setImpresso(Boolean.FALSE);
						laudoNovo.setLaudoManual(Boolean.FALSE);
						laudoNovo.setAtendimento(altaSumario.getAtendimento());
						laudoNovo.setTipoLaudo(tl);

						this.getLaudoRN().inserirLaudo(laudoNovo);
						this.getMpmLaudoDAO().flush();

					}

				}// IF dias de faturamento.
			}// IF tipoLaudo
			
		}
	}
	
	
	/**
	 * 	Verifica se exitem procedimentos especiais com Laudo/Justificativa para serem impressos.<br>
	 * 
	 *  @ORADB FUNCTION MPMC_VER_LAUDOS.<br> 
	 *  
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean existeProcedimentosComLaudoJustificativaParaImpressao(AghAtendimentos atendimento) throws BaseException {
		if (atendimento == null || atendimento.getSeq() == null) {
			throw new IllegalArgumentException(PARAMETRO_INVALIDO);
		}
		
		// Busca parametros de sistemas das situações de exames
		AghParametros objParamSituacaoLiberado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		String vLI = (objParamSituacaoLiberado != null && objParamSituacaoLiberado.getVlrTexto() != null) ? objParamSituacaoLiberado.getVlrTexto() : "";
		AghParametros objParamSituacaoAreaExecutora = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		String vAE = (objParamSituacaoAreaExecutora != null && objParamSituacaoAreaExecutora.getVlrTexto() != null) ? objParamSituacaoAreaExecutora.getVlrTexto() : "";
		AghParametros objParamSituacaoExecutando = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
		String vEX = (objParamSituacaoExecutando != null && objParamSituacaoExecutando.getVlrTexto() != null) ? objParamSituacaoExecutando.getVlrTexto() : "";
		
		// Busca mpm_tipo_laudos e fat_proced_hosp_internos
		List<FatProcedHospInternos> list = getFaturamentoFacade().buscaProcedimentosComLaudoJustificativaParaImpressao(atendimento);
		boolean temTipoLaudoOuProcedHospInterno = (list != null && !list.isEmpty());
		
		// Busca dados de ael_solicitacao_exames
		boolean temSolicitacaoExames = false;
				
		List<String> listSituacaoCodigo = Arrays.asList(vAE, vEX, vLI);
		List<AelSolicitacaoExames> listaSolicitacaoExame = getExamesLaudosFacade().pesquisarSolicitacaoExamePorAtendimento(atendimento.getSeq(), listSituacaoCodigo);
		
		if (listaSolicitacaoExame != null) {
			
			IExamesLaudosFacade facade = getExamesLaudosFacade();
			List<Integer> listaSoeSeq = new LinkedList<Integer>();
			for (AelSolicitacaoExames soe : listaSolicitacaoExame) {
				DominioOrigemAtendimento origemAtd = facade.buscaLaudoOrigemPacienteRN(soe.getSeq());
				if (DominioOrigemAtendimento.I == origemAtd) {
					listaSoeSeq.add(soe.getSeq());
				}
			}
			if (!listaSoeSeq.isEmpty()) {
				List<AelItemSolicitacaoExames> iseList = getExamesLaudosFacade().buscaItemSolicitacaoExamesComRespostaQuestao(listaSoeSeq);
				temSolicitacaoExames = (iseList != null && !iseList.isEmpty());
			}
		}
		
		return (temTipoLaudoOuProcedHospInterno || temSolicitacaoExames);
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	/**
	 * Este método verifica se o hospital
	 * tem ambulatório ou não.
	 * 
	 * @author gfmenezes
	 * 	
	 * @return
	 * @throws BaseException
	 */
	public Boolean existeAmbulatorio() throws BaseException {
		
		final AghParametros aghParametros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TEM_AMBULATORIA);
		Boolean status = false;
		
		if(aghParametros != null && 
				DominioSimNao.S.toString().equals(aghParametros.getVlrTexto())){
			status = true;
		}
		
		return status;
	}
	
	/**
	 * @ORADB MAMK_CONCLUIR.MAMP_CONC_REC_ALTA
	 * Concluir os receituários.
	 * 
	 * @param altaSumario
	 * @param receita
	 * @throws ApplicationBusinessException 
	 */
	public void assinaRelatorioReceitas(MpmAltaSumario altaSumario, MamReceituarios receita) throws BaseException {
		if (receita != null) {
			List<MamReceituarios> receitas = this.getAmbulatorioFacade().buscarReceituariosPorAltaSumarioNaoAssinados(altaSumario, receita);
			for (MamReceituarios rec : receitas) {
				if (DominioIndPendenteAmbulatorio.R.equals(rec.getPendente())) {
					this.assinaTrataRascunhoReceita(rec);
				} else if (DominioIndPendenteAmbulatorio.P.equals(rec.getPendente())) {
					this.assinaTrataPendenteReceita(rec);
				} else if (DominioIndPendenteAmbulatorio.E.equals(rec.getPendente())) {
					this.assinaTrataExclusaoReceita(rec);
				}
			}// FOR
		}//IF
	}
	
	/**
	 * 
	 * <b>p_trata_exclusao</b>
	 * 
	 * @param rec
	 * @throws ApplicationBusinessException  
	 */
	private void assinaTrataExclusaoReceita(MamReceituarios rec) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		rec.setPendente(DominioIndPendenteAmbulatorio.V);
		rec.setDthrValidaMvto(new Date());
		rec.setServidorValidaMovimento(servidorLogado);
		
		getAmbulatorioFacade().atualizarReceituario(rec);
	}

	/**
	 * 
	 * <b>p_trata_pendente</b>
	 * 
	 * @param rec
	 * @throws ApplicationBusinessException  
	 */
	private void assinaTrataPendenteReceita(MamReceituarios rec) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		rec.setPendente(DominioIndPendenteAmbulatorio.V);
		rec.setDthrValida(new Date());
		rec.setServidorValida(servidorLogado);
		
		IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
		ambulatorioFacade.atualizarReceituario(rec);
		
		//Quando tem autorelacionamente significa que houve um alteração.
		MamReceituarios recAssociado = rec.getReceituario();
		if (recAssociado != null) {
			recAssociado.setPendente(DominioIndPendenteAmbulatorio.V);
			recAssociado.setDthrValidaMvto(new Date());
			recAssociado.setServidorValidaMovimento(servidorLogado);
			
			ambulatorioFacade.atualizarReceituario(recAssociado);
		}
	}

	/**
	 * 
	 * <b>p_trata_rascunho</b>
	 * 
	 * @param rec
	 * @throws BaseException 
	 */
	private void assinaTrataRascunhoReceita(MamReceituarios rec) throws BaseException {
		
		// EXCLUI FILHOS
		Set<MamItemReceituario> itensReceituario = rec.getMamItemReceituario();
		for (MamItemReceituario itemReceituario : itensReceituario) { 
			itemReceituario.setReceituario(rec);
			this.getAmbulatorioFacade().removerItemReceituario(itemReceituario);
		}
		
		MamReceituarios autorelRec = rec.getReceituario();
		if (autorelRec != null) {

			autorelRec.setDthrMvto(null);
			autorelRec.setServidorMovimento(null);
			this.getAmbulatorioFacade().atualizarReceituario(autorelRec);
			
		}
		
		//EXCLUI PRÓPRIO RECEITUÁRIO
		this.getAmbulatorioFacade().removerReceituario(rec);
		
		
	}

	protected LaudoRN getLaudoRN() {
		return laudoRN;
	}
	
	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	protected MpmTipoLaudoDAO getMpmTipoLaudoDAO() {
		return mpmTipoLaudoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	protected ConfirmarPrescricaoMedicaRN getConfirmarPrescricaoMedicaRN() {
		return confirmarPrescricaoMedicaRN;
	}
	
	protected ManterAltaMotivoRN getManterAltaMotivoRN() {
		return manterAltaMotivoRN;
	}
	
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}
	
	protected ManterAltaPlanoRN getManterAltaPlanoRN() {
		return manterAltaPlanoRN;
	}

	protected ManterAltaEstadoPacienteRN getManterAltaEstadoPacienteRN() {
		return manterAltaEstadoPacienteRN;
	}
	
	protected ManterAltaEvolucaoRN getManterAltaEvolucaoRN() {
		return manterAltaEvolucaoRN;
	}
	
	protected ManterAltaDiagMtvoInternacaoRN getManterAltaDiagMtvoInternacaoRN() {
		return manterAltaDiagMtvoInternacaoRN;
	}
	
	protected ManterAltaDiagPrincipalRN getManterAltaDiagPrincipalRN() {
		return manterAltaDiagPrincipalRN;
	}

	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN() {
		return manterObtCausaDiretaRN;
	}
	
	protected ManterObtCausaAntecedenteRN getManterObtCausaAntecedenteRN() {
		return manterObtCausaAntecedenteRN;
	}
	
	protected ManterObitoNecropsiaRN getManterObitoNecropsiaRN() {
		return manterObitoNecropsiaRN;
	}
	
	protected ManterObtGravidezAnteriorON getManterObtGravidezAnteriorON() {
		return manterObtGravidezAnteriorON;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
