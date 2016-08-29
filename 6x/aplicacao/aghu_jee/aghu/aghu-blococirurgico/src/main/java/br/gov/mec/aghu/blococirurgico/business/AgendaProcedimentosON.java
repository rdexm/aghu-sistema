package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcProfCirurgiasRN.MbcProfCirurgiasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProcEspDAO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaPrincipalTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaQuantidadeEquipeResponsavelVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelProjetoProcedimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.VAinConvenioPlano;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe responsável pelas regras de FORMS de #22460: Agendar procedimentos eletivo, urgência ou emergência
 * @author aghu
 */
@Stateless
@SuppressWarnings({"PMD.ExcessiveClassLength","PMD.CouplingBetweenObjects"})
public class AgendaProcedimentosON extends BaseBusiness {
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private VMbcProcEspDAO vMbcProcEspDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private MbcAnestesiaCirurgiasRN mbcAnestesiaCirurgiasRN;

	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IExamesFacade iExamesFacade;

	@EJB
	private MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN;

	@EJB
	private AgendaProcedimentosFuncoesON agendaProcedimentosFuncoesON;

	@EJB
	private MbcCompSangProcCirgRN mbcCompSangProcCirgRN;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private AgendaProcedimentosInserirON agendaProcedimentosInserirON;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;

	@EJB
	private CirurgiaRemoverPacienteRN cirurgiaRemoverPacienteRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private MbcFichaAnestesiasRN mbcFichaAnestesiasRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private FatPaTuTrCnVmBcRN fatPaTuTrCnVmBcRN;

	@EJB
	private AvisoAgendamentoCirurgiaON avisoAgendamentoCirurgiaON;

	@EJB
	private AgendaProcedimentosAtualizarON agendaProcedimentosAtualizarON;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;
	private static final long serialVersionUID = -171704594916619663L;

	public enum AgendaProcedimentosONExceptionCode implements BusinessExceptionCode {
		MBCP_TEMPO_PREVISTO_MAIOR_MIN_PROCEDIMENTOS, MBC_00949, MBC_00950, MBC_00951, 
		MBC_00952, MBC_01151, MBC_01152, MBC_01334, MBCP_COLISAO_ANESTESISTA_JA_ESCALADO, 
		MBC_01380, MBC_01376, MBC_01378, MBC_01153, MBC_01154, MBC_00467, MBC_00506, MBC_00507, MBC_00495,
		PROBLEMA_GERAR_DISPONIBILIDADE;
	}

	public AlertaModalVO agendarProcedimentosParte1(final boolean emEdicao, CirurgiaTelaVO vo, final String nomeMicrocomputador)
			throws BaseException {
				final AlertaModalVO alertaVO = new AlertaModalVO();
				alertaVO.setAlerta(this.validarTempoMinimo(vo));
				
				if (StringUtils.isNotEmpty(alertaVO.getAlerta())) {
					alertaVO.setTitulo("Aviso de alteração do tempo previsto");
					alertaVO.setExibirModalValidacaoTempoMinimoPrevisto(true);
					return alertaVO;
				}
				// Em caso de inserção ou alteração executar RN2 e RN15
				this.preCommit(vo); // RN15
				this.pressionarCommit(vo, alertaVO); // RN2
				
				if (StringUtils.isNotEmpty(alertaVO.getAlerta())) {
					alertaVO.setTitulo("Confirmar operação");
					alertaVO.setExibirModalAlertaGravar(true);
				}
				return alertaVO;
			}
			
	private void setarUtilizacaoSala(MbcCirurgias cirurgiaSalva, CirurgiaTelaVO vo){
				
				// Obtem os valores necessários para processar a utilização da sala
				final Date valorData = cirurgiaSalva.getDataPrevisaoInicio();
				final MbcSalaCirurgica valorSalaUnidade = cirurgiaSalva.getSalaCirurgica(); // v_sala e v_unidade
				final DominioNaturezaFichaAnestesia valorNatureza = cirurgiaSalva.getNaturezaAgenda();
				
				for (CirurgiaTelaProfissionalVO profissionalVO : vo.getListaProfissionaisVO()) { 
					if(profissionalVO.getIndResponsavel()){
						
						if (valorData!=null) {
								DominioUtilizacaoSala utilizacao = this.getMbcProfCirurgiasRN().verificarUtilizacaoSala(valorData, 
										valorSalaUnidade.getId().getSeqp(), valorSalaUnidade.getId().getUnfSeq(),
										profissionalVO.getServidorPuc().getId().getMatricula(), 
										profissionalVO.getServidorPuc().getId().getVinCodigo(), valorNatureza);

							cirurgiaSalva.setUtilizacaoSala(utilizacao);	
						}
						else{
							cirurgiaSalva.setUtilizacaoSala(DominioUtilizacaoSala.NPR);	
						}
						
					}
				}
			}
	/**
	 * RN1 - PARTE 2
	 * 
	 * @param emEdicao
	 * @param vo
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void agendarProcedimentosParte2(final boolean emEdicao, CirurgiaTelaVO vo, AlertaModalVO alertaVOfinal, final String nomeMicrocomputador) throws BaseException {
		if(!this.getMbcProfCirurgiasRN().validaProfissionalResp(vo.getListaProfissionaisVO())) {
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00334);
		}
		if (Boolean.TRUE.equals(emEdicao)) { // Se for uma ALTERAÇÃO executar RN34
			this.getAgendaProcedimentosAtualizarON().preUpdate(vo); // RN34
		} else { // Se for uma INSERÇÃO executar RN21
			this.getAgendaProcedimentosInserirON().preInsert(vo); // RN21
		}
		
		// PERSISTE CIRURGIA - entidades nao final 
		MbcCirurgias cirurgiaSalva = this.getCirurgiaPersistencia(vo, DominioSituacaoCirurgia.AGND);
		//#39883 antes de verificar setar o campo utilização da sala
		this.setarUtilizacaoSala(cirurgiaSalva, vo);

		cirurgiaSalva = this.getMbcCirurgiasRN().persistirCirurgia(cirurgiaSalva, nomeMicrocomputador, servidorLogadoFacade.obterServidorLogado().getDtFimVinculo());

		// Se foi modificada a LISTA DE PROCEDIMENTOS gravar em MBC_PROC_ESP_POR_CIRURGIAS e executar as RNs
		this.verificarAlteracoesListaProcedimentos(cirurgiaSalva, vo); // RN77, RN89, RN90, RN91, RN92
		// Se foi modificada a LISTA DE PROFISSIONAIS gravar em MBC_PROF_CIRURGIAS e executar as RNs
		cirurgiaSalva = this.verificarAlteracoesListaProfissionais(cirurgiaSalva, vo); // RN106, RN107, RN92
		// Se foi modificada a LISTA DE ANESTESIAS gravar em MBC_ANESTESIA_CIRURGIAS e executar as RNs
		this.verificarAlteracoesListaAnestesias(cirurgiaSalva, vo); // RN100, RN102, RN105, RN101

		// Se for uma ALTERAÇÃO na MBC_CIRURGIAS e paciente for trocado executar RN14
		if (Boolean.TRUE.equals(emEdicao) && vo.getPacienteAntigo() != null) {
			// RN14
			this.getCirurgiaRemoverPacienteRN().removerPaciente(vo.getPacienteAntigo(), vo.getCirurgia().getPaciente());
		} else if (!Boolean.TRUE.equals(emEdicao)){ // Se for uma INSERÇÃO na MBC_CIRURGIAS executar RN44 e RN99
			this.getMbcCirurgiasRN().atualizarSolicitacaoCirurgia(cirurgiaSalva); // RN44
			getFatPaTuTrCnVmBcRN().inserirExtratoCirurgia(cirurgiaSalva); // RN99
		}
		vo.getCirurgia().setSeq(cirurgiaSalva.getSeq()); // SETA O SEQUENCIAL NO VO para renderizar a tela corretamente
		this.postCommit(vo, cirurgiaSalva, alertaVOfinal);
		this.postDatabaseCommit(vo); // RN16 (Chama 3 outras estórias)
	}

	/*
	 * Métodos que verificam alterações nas listas de procedimentos, profissionais e anestesias.
	 */

	public void verificarAlteracoesListaProcedimentos(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		for (MbcProcEspPorCirurgiasId idRemover : vo.getListaProcedimentosRemovidos()) { // REMOVE
			// RN91, RN92 (MBCP_ENFORCE_PPC_RULES)
			MbcProcEspPorCirurgias originalRemovido = this.getMbcProcEspPorCirurgiasDAO().obterPorChavePrimaria(idRemover);
			if (originalRemovido != null) {
				this.getMbcProcEspPorCirurgiasRN().removerMbcProcEspPorCirurgias(originalRemovido);
			}
		}
		for (CirurgiaTelaProcedimentoVO procedimentoVO : vo.getListaProcedimentosVO()) { // INSERE OU ALTERA
			// RN77, RN89, RN90, RN92 (MBCP_ENFORCE_PPC_RULES)
			MbcProcEspPorCirurgias procEspPorCirurgia = this.prepararProcedimentoPersistencia(cirurgia, procedimentoVO);
			this.getMbcProcEspPorCirurgiasRN().persistirProcEspPorCirurgias(procEspPorCirurgia);
		}
	}

	public MbcCirurgias verificarAlteracoesListaProfissionais(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		for (MbcProfCirurgiasId idRemover : vo.getListaProfissionaisRemovidos()) { // REMOVE
			MbcProfCirurgias originalRemovido = this.getMbcProfCirurgiasDAO().obterPorChavePrimaria(idRemover);
			if (originalRemovido != null) {
				this.getMbcProfCirurgiasRN().removerMbcProfCirurgias(originalRemovido, false);
			}
		}
		for (CirurgiaTelaProfissionalVO profissionalVO : vo.getListaProfissionaisVO()) { // INSERE OU ALTERA
			// RN106, RN107
			MbcProfCirurgias profCirurgia = this.prepararProfissionalPersistencia(cirurgia, profissionalVO);
			profCirurgia.setCirurgia(cirurgia); //garante que sempre haverá uma cirurgia #37870
			profCirurgia = this.getMbcProfCirurgiasRN().persistirProfissionalCirurgias(profCirurgia);
			cirurgia = profCirurgia.getCirurgia();
		}
		return cirurgia;
	}

	public void verificarAlteracoesListaAnestesias(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		for (MbcAnestesiaCirurgiasId idRemover : vo.getListaAnestesiasRemovidas()) { // REMOVE
			MbcAnestesiaCirurgias originalRemovido = this.getMbcAnestesiaCirurgiasDAO().obterPorChavePrimaria(idRemover);
			if (originalRemovido != null) {
				// RN101, RN100 (MBCP_ENFORCE_ANC_RULES)
				this.getMbcAnestesiaCirurgiasRN().removerMbcAnestesiaCirurgias(originalRemovido);
			}
		}
		for (CirurgiaTelaAnestesiaVO anestesiaVO : vo.getListaAnestesiasVO()) { // INSERE OU ALTERA
			// RN102, RN105, RN100 (MBCP_ENFORCE_ANC_RULES)
			MbcAnestesiaCirurgias anestesiaCirurgia = this.obterAnestesiaPersistencia(cirurgia, anestesiaVO);
			this.getMbcAnestesiaCirurgiasRN().persistir(anestesiaCirurgia);
			this.getMbcAnestesiaCirurgiasDAO().flush();
		}
	}
	
	protected MbcCirurgias getCirurgiaPersistencia(MbcCirurgias cirurgia) {
		MbcCirurgias retorno = null;
		MbcCirurgias original = this.getMbcCirurgiasDAO().obterOriginal(cirurgia.getSeq());
		if (original != null) { // ATUALIZAR
			Integer version = original.getVersion();
			retorno = original;
			try {
				PropertyUtils.copyProperties(retorno, cirurgia);
				retorno.setVersion(version);
				retorno.setAtendimento(cirurgia.getAtendimento());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return retorno;
	}

	protected MbcCirurgias getCirurgiaPersistencia(CirurgiaTelaVO vo) {
		return this.getCirurgiaPersistencia(vo, vo.getCirurgia().getSituacao());
	}

	public MbcCirurgias getCirurgiaPersistencia(CirurgiaTelaVO vo, final DominioSituacaoCirurgia situacaoCirurgia) {
		MbcCirurgias retorno = null;
		MbcCirurgias original = this.getMbcCirurgiasDAO().obterOriginal(vo.getCirurgia().getSeq());
		if (original != null) { // ATUALIZAR
			retorno = original;
			try {
				
				PropertyUtils.copyProperties(retorno, vo.getCirurgia());
				retorno.setDataPrevisaoInicio(vo.getCirurgia().getAgenda().getDthrPrevInicio());
				retorno.setDataPrevisaoFim(vo.getCirurgia().getAgenda().getDthrPrevFim());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else { // INSERIR
			retorno = new MbcCirurgias();
			try {
				PropertyUtils.copyProperties(retorno, vo.getCirurgia());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			// Seta atributos com valores padrão
			retorno.setSeq(null);
			retorno.setSituacao(situacaoCirurgia);
			retorno.setContaminacao(false);
			retorno.setDigitaNotaSala(false);
			retorno.setPrecaucaoEspecial(false);
			retorno.setUtilizaO2(false);
			retorno.setUtilizaProAzot(false);
			retorno.setTemDescricao(false);
			retorno.setOverbooking(false);
			retorno.setAplicaListaCirurgiaSegura(false);
			Calendar dataMomento = Calendar.getInstance();
			dataMomento.setTime(vo.getCirurgia().getData());
			if (retorno.getDataPrevisaoInicio() != null) {
				Calendar dataInicio = Calendar.getInstance();
				dataInicio.setTime(retorno.getDataPrevisaoInicio());
				dataInicio.set(Calendar.DAY_OF_MONTH, dataMomento.get(Calendar.DAY_OF_MONTH));
				dataInicio.set(Calendar.MONTH, dataMomento.get(Calendar.MONTH));
				dataInicio.set(Calendar.YEAR, dataMomento.get(Calendar.YEAR));
				retorno.setDataPrevisaoInicio(dataInicio.getTime());
			}
			if (retorno.getDataPrevisaoFim() != null) {
				Calendar dataFim = Calendar.getInstance();
				dataFim.setTime(retorno.getDataPrevisaoFim());
				dataFim.set(Calendar.DAY_OF_MONTH, dataMomento.get(Calendar.DAY_OF_MONTH));
				dataFim.set(Calendar.MONTH, dataMomento.get(Calendar.MONTH));
				dataFim.set(Calendar.YEAR, dataMomento.get(Calendar.YEAR));
				retorno.setDataPrevisaoFim(dataFim.getTime());
			}
		}
		return retorno;
	}

	public MbcProcEspPorCirurgias prepararProcedimentoPersistencia(MbcCirurgias cirurgia, CirurgiaTelaProcedimentoVO procedimentoVO) {
		MbcProcEspPorCirurgias retorno = null;
		
		boolean isUpdate = getMbcProcEspPorCirurgiasDAO().obterOriginal(procedimentoVO.getId()) != null;
		if (isUpdate) { // ATUALIZAR
			retorno = this.getMbcProcEspPorCirurgiasDAO().obterPorChavePrimaria(procedimentoVO.getId());
			try {
				PropertyUtils.copyProperties(retorno, procedimentoVO);
				if (procedimentoVO.getPhiSeq() != null) {
					retorno.setProcedHospInterno(
							this.getFaturamentoFacade().obterProcedimentoHospitalarInterno(procedimentoVO.getPhiSeq())
					);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else { // INSERIR
			retorno = new MbcProcEspPorCirurgias();
			retorno.setCirurgia(cirurgia);
			if(procedimentoVO.getPhiSeq() != null){
				retorno.setProcedHospInterno(this.getFaturamentoFacade().
												obterProcedimentoHospitalarInterno(procedimentoVO.getPhiSeq()));
			}
			retorno.setMbcEspecialidadeProcCirgs(procedimentoVO.getMbcEspecialidadeProcCirgs());
			retorno.setIndRespProc(procedimentoVO.getId().getIndRespProc());
			
			MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
			id.setCrgSeq(cirurgia.getSeq());
			id.setEprEspSeq(procedimentoVO.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
			id.setEprPciSeq(procedimentoVO.getMbcEspecialidadeProcCirgs().getId().getPciSeq());
			id.setIndRespProc(procedimentoVO.getId().getIndRespProc());
			retorno.setId(id);
			
			retorno.setIndPrincipal(procedimentoVO.getIndPrincipal());
			retorno.setProcedimentoCirurgico(procedimentoVO.getProcedimentoCirurgico());
			retorno.setQtd(procedimentoVO.getQtd());
			retorno.setSituacao(procedimentoVO.getSituacao());
			retorno.setCid(procedimentoVO.getCid());				
		}
		return retorno;
	}

	protected MbcProfCirurgias prepararProfissionalPersistencia(MbcCirurgias cirurgia, CirurgiaTelaProfissionalVO profissionalVO) {
		MbcProfCirurgias retorno = null;
		
		MbcProfCirurgias original = this.getMbcProfCirurgiasDAO().obterPorChavePrimaria(profissionalVO.getId());
		if (original != null) { // ATUALIZAR
			retorno = original;
			try {
				PropertyUtils.copyProperties(retorno, profissionalVO);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		} else { // INSERIR
			retorno = new MbcProfCirurgias();
			try {
				PropertyUtils.copyProperties(retorno, profissionalVO);
				retorno.getId().setCrgSeq(cirurgia.getSeq());
				retorno.setCirurgia(cirurgia);
				retorno.setUnidadeFuncional(cirurgia.getUnidadeFuncional());
				retorno.setIndInclEscala(false);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		}
		
		return retorno;
	}

	private MbcAnestesiaCirurgias obterAnestesiaPersistencia(MbcCirurgias cirurgia, CirurgiaTelaAnestesiaVO anestesiaVO) {
		MbcAnestesiaCirurgias retorno = null;
		
		MbcAnestesiaCirurgias original = this.getMbcAnestesiaCirurgiasDAO().obterPorChavePrimaria(anestesiaVO.getId());
		if (original != null) { // ATUALIZAR
			retorno = original;
			try {
				PropertyUtils.copyProperties(retorno, anestesiaVO);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		} else { // INSERIR
			retorno = new MbcAnestesiaCirurgias();
			try {
				PropertyUtils.copyProperties(retorno, anestesiaVO);
				retorno.getId().setCrgSeq(cirurgia.getSeq());
				retorno.setCirurgia(cirurgia);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		}
		
		return retorno;
	}

	/**
	 * ORADB PROCEDURE EVT_PRE_COMMIT
	 * <p>
	 * RN15
	 * <p>
	 * 
	 * @param vo
	 * @return
	 * @throws BaseException
	 */
	public void preCommit(CirurgiaTelaVO vo) throws BaseException {
		AelProjetoPesquisas projetoPesquisa = vo.getCirurgia().getProjetoPesquisa();

		if (projetoPesquisa != null) {
			Boolean valorIndVoucherEletronico = projetoPesquisa.getVoucherEletronico();

			Date dtInicio = DateUtil.truncaData(projetoPesquisa.getDtInicio());
			Date dataAtual = DateUtil.truncaData(new Date());
			Date dtFim = (Date) CoreUtil.nvl(DateUtil.truncaData(projetoPesquisa.getDtFim()), dataAtual);

			// O CURSOR C_PROJ foi migrado neste trecho
			Boolean isIntervaloDataAtual = DateUtil.entre(dataAtual, dtInicio, dtFim);

			if (isIntervaloDataAtual && Boolean.FALSE.equals(valorIndVoucherEletronico)) {
				// Esse projeto não está liberado para uso eletrônico. Entrar em contato com o GPPG.
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01380);
			}

			Integer eprPciSeq = vo.obterProcedimentoPrincipal().getId().getEprPciSeq();
			MbcProcedimentoCirurgicos procedimentoCirurgicos = this.getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(eprPciSeq);
			AelProjetoProcedimento valorExiste = this.getExamesFacade().obterProjetoProcedimentoAtivoPorId(projetoPesquisa.getSeq(), procedimentoCirurgicos.getSeq());

			if (valorExiste == null) {

				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01376, eprPciSeq);
			}
		}

		if (vo.getCirurgia().getPaciente() != null && vo.getCirurgia().getProjetoPesquisa() != null) {
			AelProjetoPacientes valorPacienteCad = this.getExamesFacade().obterProjetoPacienteCadastradoDataProjetoPesquisa(vo.getCirurgia().getPaciente().getCodigo(),
					projetoPesquisa.getSeq());

			if (valorPacienteCad == null) {
				// O Paciente não está cadastrado para esse projeto de pesquisa!
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01378);
			}
		}

	}

	/**
	 * ORADB PROCEDURE EVT_POST_FORMS_COMMIT
	 * <p>
	 * RN7
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public void postCommit(CirurgiaTelaVO vo, MbcCirurgias cirurgia, AlertaModalVO alertaVOfinal) throws BaseException {

		alertaVOfinal.setSangueSolicitado(false);

		// RN8
		if (cirurgia.getDigitaNotaSala()) {
			getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.NOTA);
		} else {
			getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.AGND);
		}
		getMbcProfCirurgiasRN().verificarResponsavel(cirurgia.getSeq()); // RN9
		getMbcCirurgiasVerificacoesRN().verificarNecessidadeAnestesista(cirurgia.getSeq()); // RN10
		getMbcCirurgiasRN().atualizaProfNelt(cirurgia.getSeq(), cirurgia.getNaturezaAgenda(), cirurgia.getUnidadeFuncional(), cirurgia.getSalaCirurgica()); // RN11
		if (cirurgia.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ELE)) {
			if (getMbcSolicHemoCirgAgendadaDAO().mbcSolicHemoCirgAgendadaCountPorCrgSeq(cirurgia.getSeq()) == 0) {
				Boolean incluiu = getMbcCompSangProcCirgRN().atualizarSangue(cirurgia.getSeq(), vo.obterProcedimentoPrincipal().getProcedimentoCirurgico().getSeq(),
						cirurgia.getEspecialidade().getSeq());
				if (incluiu) {
					alertaVOfinal.setSangueSolicitado(true);
				}
			}
		}
		AghParametros paramUnidadeCo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CO);
		Integer vlrNumerico = paramUnidadeCo.getVlrNumerico().intValue();
		if (vlrNumerico.equals(cirurgia.getUnidadeFuncional().getSeq().intValue())) {
			getMbcFichaAnestesiasRN().vincularCirurgiaFicha(cirurgia.getSeq(), cirurgia.getPaciente().getCodigo(),
					vo.obterProcedimentoPrincipal().getProcedimentoCirurgico().getSeq(), cirurgia.getData(), cirurgia.getUnidadeFuncional().getSeq());
		}
	}

	/**
	 * ORADB PROCEDURE MBCP_VERIF_PAC_COD_E_ATD
	 * RN31: Verifica alterações no código do paciente
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public void verificarPacienteCodigoAlterado(CirurgiaTelaVO vo) throws BaseException {

		if (!CoreUtil.igual(vo.getCirurgia().getPaciente(), vo.getPacienteAntigo())) {

			final AghAtendimentos atendimento = vo.getCirurgia().getAtendimento();

			if (atendimento != null) {

				if (DominioOrigemAtendimento.C.equals(atendimento.getOrigem())) {

					atendimento.setPaciente(vo.getCirurgia().getPaciente());
					atendimento.setProntuario(vo.getProntuario());

					this.getAghuFacade().atualizarAghAtendimentos(atendimento, true);

				} else if (!CoreUtil.igual(vo.getCirurgia().getPaciente(), atendimento.getPaciente())) {
					// Paciente da cirurgia é diferente do paciente do atendimento associado. Não é possível alterar o paciente
					throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00495);
				}

			} else if (DominioOrigemPacienteCirurgia.I.equals(vo.getCirurgia().getOrigemPacienteCirurgia())) {

				if (DominioNaturezaFichaAnestesia.ELE.equals(vo.getCirurgia().getNaturezaAgenda())) {

					// Verificar se executou a ESCALA DO DIA
					List<MbcControleEscalaCirurgica> listaControleEscalaCirurgica = this.getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(
							vo.getCirurgia().getUnidadeFuncional().getSeq(), vo.getCirurgia().getData());

					if (!listaControleEscalaCirurgica.isEmpty()) {
						AghAtendimentos atendimentoPaciente = this.getAghuFacade().obterAtendimentoPacientePorOrigem(vo.getCirurgia().getPaciente().getCodigo(),
								DominioOrigemAtendimento.I, DominioOrigemAtendimento.U);

						if (atendimentoPaciente != null) {
							vo.getCirurgia().setAtendimento(atendimentoPaciente);
						}
					}
				} else {
					AghAtendimentos atendimentoPaciente = this.getAghuFacade().obterAtendimentoPacientePorOrigem(vo.getCirurgia().getPaciente().getCodigo(),
							DominioOrigemAtendimento.I, DominioOrigemAtendimento.U);

					if (atendimentoPaciente != null) {
						vo.getCirurgia().setAtendimento(atendimentoPaciente);
					}
				}
			}
		}
	}

	/**
	 * ORADB PROCEDURE MBCP_VALIDA_TEMPO_MIN
	 * RN22: Validar tempo mínimo
	 * 
	 * @param vo
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public String validarTempoMinimo(CirurgiaTelaVO vo) throws BaseException {
		String mensagemRetorno = null; // Retorno da mensagem de confirmação na tela
		MbcProcedimentoCirurgicos procedimentoCirurgicos = this.getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(vo.obterProcedimentoPrincipal().getId().getEprPciSeq());

		final String valorDescricao = procedimentoCirurgicos.getDescricao();
		final Short valorTempoMinimo = procedimentoCirurgicos.getTempoMinimo();

		if (vo.getCirurgia().getTempoPrevistoHoras() != null && vo.getCirurgia().getTempoPrevistoMinutos() != null) {

			final Integer validacaoPrevisaoHorasMinutos = (((Integer) CoreUtil.nvl(Integer.valueOf(vo.getCirurgia().getTempoPrevistoHoras()), Integer.valueOf("0"))) * Integer.valueOf("60"))
					+ ((Integer) CoreUtil.nvl(Integer.valueOf(vo.getCirurgia().getTempoPrevistoMinutos()), Integer.valueOf("0")));
			final Integer validacaoTempoMinimo = this.getAgendaProcedimentosFuncoesON().getValorTempoMinimo(valorTempoMinimo);

			if (validacaoPrevisaoHorasMinutos < validacaoTempoMinimo) {

				Short valorTempoPrevHrs = (Short) CoreUtil.nvl(vo.getCirurgia().getTempoPrevistoHoras(), Short.valueOf("0"));
				Byte valorTempoPrevMin = (Byte) CoreUtil.nvl(vo.getCirurgia().getTempoPrevistoMinutos(), Byte.valueOf("0"));
				Short valorTempoMinHrs = this.getAgendaProcedimentosFuncoesON().getValorTempoMinimoHoras(valorTempoMinimo);

				vo.getCirurgia().setTempoPrevistoHoras(valorTempoMinHrs);

				Byte valorTempoMinMin = this.getAgendaProcedimentosFuncoesON().getValorTempoMinimoMinuto(valorTempoMinimo);

				vo.getCirurgia().setTempoPrevistoMinutos(valorTempoMinMin);

				mensagemRetorno = "Tempo previsto alterado de " + StringUtil.adicionaZerosAEsquerda(valorTempoPrevHrs, 2) + ":"
				+ StringUtil.adicionaZerosAEsquerda(valorTempoPrevMin, 2) + " para " + StringUtil.adicionaZerosAEsquerda(valorTempoMinHrs, 2) + ":"
				+ StringUtil.adicionaZerosAEsquerda(valorTempoMinMin, 2) + ", tempo mínimo do procedimento " + valorDescricao + ". Clique no botão gravar novamente.";
			}
		}
		return mensagemRetorno;
	}

	/**
	 * ORADB PROCEDURE EVT_KEY_COMMIT
	 * RN2
	 * 
	 * @param vo
	 * @param alertaVO
	 * @return
	 * @throws BaseException
	 */
	public void pressionarCommit(CirurgiaTelaVO vo, final AlertaModalVO alertaVO) throws BaseException {

		String alerta = null; // Retorno da mensagem de confirmação na tela
		AipPacientes paciente = vo.getCirurgia().getPaciente();
		// Verifica NÚMERO DA AGENDA
		if (vo.getCirurgia().getNumeroAgenda() != null) {
			// Verifica se já existe uma cirurgia para o paciente no MESMO DIA considerando o NÚMERIO DA AGENDA
			MbcCirurgias cirurgiaAgendada = this.getMbcCirurgiasDAO().obterCirurgiaAgendadaPacienteMesmoDia(vo.getDataCirurgiaTruncada(), paciente.getCodigo(),
					vo.getCirurgia().getUnidadeFuncional().getSeq(), vo.getCirurgia().getNumeroAgenda());

			if (cirurgiaAgendada != null) { // Quando existir cirurgias agendadas...
				alerta = this.getAgendaProcedimentosFuncoesON().getMensagemPacienteComCirurgiaJaAgendada(cirurgiaAgendada); // Reutilização para gerar a
				alertaVO.setAlerta(alerta); // SHOW_ALERT aqui!
			}
		} else {
			// Verifica se já existe uma cirurgia para o paciente no MESMO DIA
			MbcCirurgias cirurgiaAgendada = this.getMbcCirurgiasDAO().obterCirurgiaAgendadaPacienteMesmoDia(vo.getCirurgia().getData(), paciente.getCodigo(),
					vo.getCirurgia().getUnidadeFuncional().getSeq());

			if (cirurgiaAgendada != null) {
				alerta = this.getAgendaProcedimentosFuncoesON().getMensagemPacienteComCirurgiaJaAgendada(cirurgiaAgendada);// Reutilização para gerar a
				alertaVO.setAlerta(alerta); // SHOW_ALERT aqui!
			}
		}
		// Chamada para PROCEDURE MBCP_VER_PAC_INTERNADO Chamada da procedure MBCP_VER_PAC_INTERNADO
		this.verificarPacienteInternado(vo, alertaVO); // Verificar o CONVÊNIO/PLANO se o paciente estiver internado

		// PROIBE AGENDAR cirurgia eletiva pré-escala SUS, somente médico pelo portal
		if (this.isHCPA() && DominioNaturezaFichaAnestesia.ELE.equals(vo.getCirurgia().getNaturezaAgenda())) {

			DominioGrupoConvenio valorGrupoConvenio = vo.getCirurgia().getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();

			if (valorGrupoConvenio == null) {
				// Assinale apenas um profissional para RESPONSÁVEL pela cirurgia
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01151);
			} else {
				if (DominioGrupoConvenio.S.equals(valorGrupoConvenio)) { // Convênio SUS
					// Verificar se executou a ESCALA DO DIA
					List<MbcControleEscalaCirurgica> listaControleEscalaCirurgica = this.getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(
							vo.getCirurgia().getUnidadeFuncional().getSeq(), vo.getDataCirurgiaTruncada());

					if (listaControleEscalaCirurgica.isEmpty()) { // NÃO EXECUTOU ESCALA PARA O DIA

						// Chamada da MBCP_BUSCA_PRINCIPAL_TELA
						PesquisaPrincipalTelaVO pesquisaPrincipalTelaVO = this.pesquisarPrincipalTela(vo.getListaProcedimentosVO());

						MbcProcedimentoCirurgicos procedimentoCirurgicos = this.getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(
								pesquisaPrincipalTelaVO.getProcedimentoPrincipal().getId().getEprPciSeq());

						if (DominioTipoProcedimentoCirurgico.CIRURGIA.equals(procedimentoCirurgicos.getTipo())) {
							// O item principal é CIRURGIA
							throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01334);
						}
					}
				}
			}
		}
		// ATENÇÃO: Conforme a documentação de análise a chamada da PROCEDURE MBCP_VER_COLISAO foi desativada
		
		CirurgiaTelaProfissionalVO profissionalSelecionado = vo.obterProfissionalResponsavel();
		if (DominioFuncaoProfissional.ANC.equals(profissionalSelecionado.getFuncaoProfissional())
				|| DominioFuncaoProfissional.ANP.equals(profissionalSelecionado.getFuncaoProfissional())) {
			// Chamada da PROCEDURE MBCP_VER_COLISAO_ANESTESISTA
			this.verificarColisaoAnestesista(vo);
		}

	}

	/**
	 * ORADB PROCEDURE MBCP_VER_PAC_INTERNADO
	 * RN3: Verifica paciente internado
	 * 
	 * @param vo
	 * @param alertaVO
	 * @throws BaseException
	 */
	public void verificarPacienteInternado(CirurgiaTelaVO vo, final AlertaModalVO alertaVO) throws BaseException {
		Date diaCirurgia = DateUtil.truncaData(vo.getDataCirurgiaTruncada());
		Date datahHoje = DateUtil.truncaData(new Date());
		if (DateUtil.validaDataMenor(diaCirurgia, datahHoje)) {
			// Se o agendamento da cirurgia for RETROATIVO, não verificar a internação
			return;
		}
		// Pesquisa atendimentos de INTERNAÇÃO OU URGÊNCIA para o paciente
		AinInternacao internacao = null;
		AinAtendimentosUrgencia atendimentoUrgencia = null;
		List<AghAtendimentos> listaAtendimentos = this.getAghuFacade().pesquisaAtendimentoPacienteInternadoUrgencia(vo.getCirurgia().getPaciente().getCodigo());
		if (listaAtendimentos.isEmpty()) {
			return;
		} else {
			internacao = listaAtendimentos.get(0).getInternacao();
			atendimentoUrgencia = listaAtendimentos.get(0).getAtendimentoUrgencia();
		}
		
		FatConvenioSaudePlano convenioSaudePlano = null;
		String valorSituacao = null;
		// Se estiver INTERNADO busca o convenio da internação
		if (internacao != null) {
			AinInternacao internacaoComPacienteInternado = this.getInternacaoFacade().obterInternacaoPorSequencialPaciente(internacao.getSeq(),
					vo.getCirurgia().getPaciente().getCodigo());
			if (internacaoComPacienteInternado == null) {
				return;
			} else {
				convenioSaudePlano = internacaoComPacienteInternado.getConvenioSaudePlano();
				valorSituacao = "INTERNADO";
			}
		}
		
		// Se estiver em ATENDIMENTO DE URGÊNCIA busca o convênio do atendimento
		if (atendimentoUrgencia != null) {
			atendimentoUrgencia = this.getInternacaoFacade().obterAtendimentoUrgenciaPorSequencialPaciente(atendimentoUrgencia.getSeq(),
			vo.getCirurgia().getPaciente().getCodigo());
			if (atendimentoUrgencia == null) {
				return;
			} else {
				convenioSaudePlano = atendimentoUrgencia.getConvenioSaudePlano();
				valorSituacao = "em atendimento de URGÊNCIA";
			}
		}
		// Verifica se o convenio da cirurgia está igual ao convenio do atendimento
		if (CoreUtil.igual(vo.getCirurgia().getConvenioSaudePlano(), convenioSaudePlano)) {
			return;
		} else {
			VAinConvenioPlano viewConvenioPlano = this.getInternacaoFacade().obterVAinConvenioPlanoPeloId(convenioSaudePlano.getId().getSeq(),
					convenioSaudePlano.getId().getCnvCodigo());
			String valorConvenioPlano = viewConvenioPlano.getId().getConvenioPlano() != null ? viewConvenioPlano.getId().getConvenioPlano() : "XX";
			final String alerta = "Paciente se encontra " + valorSituacao + " no convênio " + valorConvenioPlano + ". Deseja alterar o convênio desta cirurgia?";
			alertaVO.setAlerta(alerta); // SHOW_ALERT aqui!
			alertaVO.setCancelarAlertaContinuaProcesso(true);
			alertaVO.setConvenioEncontradoCodigo(convenioSaudePlano.getId().getCnvCodigo()); // Vide: copy (v_csp_cnv_codigo_atd,'crg.csp_cnv_codigo');
			alertaVO.setConvenioEncontradoCodigoSeq(convenioSaudePlano.getId().getSeq());
		}
	}

	/**
	 * ORADB PROCEDURE MBCP_VER_COLISAO_ANESTESISTA
	 * RN6: Verifica a colisão de anestesista
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public void verificarColisaoAnestesista(CirurgiaTelaVO vo) throws BaseException {
		Integer valorCrgSeq = vo.getCirurgia().getSeq();
		if (valorCrgSeq == null) {
			valorCrgSeq = 0;
		}
		Date valorDthrPrevInicio = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getDataPrevisaoInicio()); // Data
		Date valorDthrPrevFim = this.getAgendaProcedimentosFuncoesON().obterDataHoraPrevisaoFim(vo.getCirurgia().getData(), vo.getCirurgia().getTempoPrevistoHoras(),
				vo.getCirurgia().getTempoPrevistoMinutos()); // Data de previsão final
		// Verifica colisao de horários com outra CIRURGIA/PDT
		if (vo.getCirurgia().getDataPrevisaoInicio() != null) {
			// Pesquisa e verifica colisão de horários com outra cirurgia/PDT agendada na mesma UNF/DATA/SALA
			List<MbcCirurgias> listaColisao = this.getMbcCirurgiasDAO().pesquisarColisaoHorariosAnestesista(vo.getCirurgia().getData(), valorCrgSeq,
					vo.obterProfissionalResponsavel().getId().getPucSerMatricula(), vo.obterProfissionalResponsavel().getId().getPucSerVinCodigo());
			for (MbcCirurgias colisao : listaColisao) {
				if (DateUtil.validaDataMaiorIgual(valorDthrPrevInicio, colisao.getDataInicioCirurgia())
						&& DateUtil.validaDataMenor(valorDthrPrevInicio, colisao.getDataFimCirurgia())) {
					throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBCP_COLISAO_ANESTESISTA_JA_ESCALADO, colisao.getUnidadeFuncional().getDescricao(), colisao
							.getSalaCirurgica().getId().getSeqp(), DateUtil.obterDataFormatada(colisao.getDataInicioCirurgia(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO),
							DateUtil.obterDataFormatada(colisao.getDataFimCirurgia(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				}
			}
		}
		if (valorDthrPrevFim != null) {
			// Pesquisa e verifica colisão de horários com outra cirurgia/PDT agendada na mesma UNF/DATA/SALA
			List<MbcCirurgias> listaColisao = this.getMbcCirurgiasDAO().pesquisarColisaoHorariosAnestesista(vo.getCirurgia().getData(), valorCrgSeq,
					vo.obterProfissionalResponsavel().getId().getPucSerMatricula(), vo.obterProfissionalResponsavel().getId().getPucSerVinCodigo());
			for (MbcCirurgias colisao : listaColisao) {
				boolean validacao1 = DateUtil.validaDataMaior(valorDthrPrevFim, colisao.getDataInicioCirurgia())
				&& DateUtil.validaDataMenorIgual(valorDthrPrevFim, colisao.getDataFimCirurgia());
				boolean validacao2 = DateUtil.validaDataMenor(valorDthrPrevInicio, colisao.getDataInicioCirurgia())
				&& DateUtil.validaDataMaior(valorDthrPrevFim, colisao.getDataFimCirurgia());
				if (validacao1 || validacao2) {
					throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBCP_COLISAO_ANESTESISTA_JA_ESCALADO, colisao.getUnidadeFuncional().getDescricao(), colisao
							.getSalaCirurgica().getId().getSeqp(), DateUtil.obterDataFormatada(colisao.getDataInicioCirurgia(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO),
							DateUtil.obterDataFormatada(colisao.getDataFimCirurgia(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				}
			}
		}
	}

	/**
	 * ORADB PROCEDURE MBCP_BUSCA_EQUIPE_RESP
	 * 
	 * RN23: Contabiliza a quantidade de servidores responsáveis
	 * 
	 * @param listaProfissionaisVO
	 * @return
	 * @throws BaseException
	 */
	public PesquisaQuantidadeEquipeResponsavelVO pesquisarQuantidadeEquipeResponsavel(List<CirurgiaTelaProfissionalVO> listaProfissionaisVO) throws BaseException {
		PesquisaQuantidadeEquipeResponsavelVO vo = new PesquisaQuantidadeEquipeResponsavelVO();
		int contaResponsaveis = 0;
		for (CirurgiaTelaProfissionalVO profissionalVO : listaProfissionaisVO) {
			if (Boolean.TRUE.equals(profissionalVO.getIndResponsavel())) {
				vo.setProfissionalResponsavel(profissionalVO); // Seta profissional responsável no VO
				vo.setContaResponsaveis(++contaResponsaveis);
			}
		}
		return vo;
	}

	/**
	 * ORADB PROCEDURE MBCP_BUSCA_PRINCIPAL_TELA
	 * RN4: Contabiliza a quantidade de procedimentos por especialidade cirúrgica
	 * 
	 * @param listaProcedimentosVO
	 * @return
	 * @throws BaseException
	 */
	public PesquisaPrincipalTelaVO pesquisarPrincipalTela(List<CirurgiaTelaProcedimentoVO> listaProcedimentosVO) throws BaseException {
		PesquisaPrincipalTelaVO vo = new PesquisaPrincipalTelaVO();
		int contaProcedimentos = 0;
		for (CirurgiaTelaProcedimentoVO procedimentoVO : listaProcedimentosVO) {
			if (Boolean.TRUE.equals(procedimentoVO.getIndPrincipal())) {
				vo.setProcedimentoPrincipal(procedimentoVO); // Seta procedimento especialidade no VO
				vo.setQtdeProc(procedimentoVO.getQtd()); // Seta a quantidade do procedimento especialidade
				vo.setContaProcedimentos(++contaProcedimentos);
			}
		}
		
		if (vo.getContaProcedimentos() == null) {
			vo.setQtdeProc(Byte.valueOf("0"));
			vo.setContaProcedimentos(0);
		}

		return vo;
	}

	/**
	 * ORADB PROCEDURE EVT_POST_DATABASE_COMMIT
	 * RN16
	 * 
	 * @param vo
	 * @return
	 * @throws BaseException
	 */
	public void postDatabaseCommit(CirurgiaTelaVO vo) throws BaseException {
		// TODO Executar RN16. (que chama três outras estórias).
		//RN 17 - Estória #26775
		this.getAvisoAgendamentoCirurgiaON().gerarAvisoSamis(vo.getCirurgia().getSeq());
	}

	public List<VMbcProcEsp> pesquisarProcedimentosEspecialidadeProjeto(final String filtro, final Short espSeq, final Integer pjqSeq) {
		List<VMbcProcEsp> listaVMbcProcEsp = this.getVMbcProcEspDAO().pesquisarProcEspPorEspecialidadeProjetoPesquisa(filtro, espSeq);
		List<VMbcProcEsp> resultado = new LinkedList<VMbcProcEsp>();
		for (VMbcProcEsp item : listaVMbcProcEsp) {
			if (pjqSeq != null) {
				// Se for selecionado um projeto de pesquisa no formulário principal só permite procedimentos que fazem parte deste projeto de pesquisa
				AelProjetoProcedimento aelProjetoProcedimento = this.getExamesFacade().obterProjetoProcedimentoAtivoPorId( pjqSeq, item.getId().getPciSeq());
				if (aelProjetoProcedimento != null) {
					resultado.add(item);
				}
			} else {
				resultado.add(item);
			}
		}
		return resultado;
	}
	public Long pesquisarProcedimentosEspecialidadeProjetoCount(final String filtro, final Short espSeq, final Integer pjqSeq) {
		List<VMbcProcEsp> listaVMbcProcEsp = this.pesquisarProcedimentosEspecialidadeProjeto(filtro, espSeq, pjqSeq);
		return Long.valueOf(listaVMbcProcEsp.size());
	}

	public List<CirurgiaTelaProcedimentoVO> pesquisarProcedimentosAgendaProcedimentos(Integer crgSeq, DominioIndRespProc indRespProc) {
		List<CirurgiaTelaProcedimentoVO> resultado = new ArrayList<CirurgiaTelaProcedimentoVO>();
		// CURSOR C8: Pesquisa procedimentos
		List<MbcProcEspPorCirurgias> listaProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAgendadosPorCirurgia(crgSeq, indRespProc);
		for (MbcProcEspPorCirurgias procEspPorCirurgia : listaProcEspPorCirurgias) {
			// CURSOR C9: Pesquisa procedimentos especialidade na VIEW V_MBC_PROC_ESP
			List<VMbcProcEsp> listaVMbcProcEsp = this.getVMbcProcEspDAO().pesquisarProcedimentosAgendadosPorEspecialidadeProcedimento(procEspPorCirurgia.getId().getEprEspSeq(),
					procEspPorCirurgia.getId().getEprPciSeq());
			if (!listaVMbcProcEsp.isEmpty()) {
				CirurgiaTelaProcedimentoVO vo = new CirurgiaTelaProcedimentoVO();
				try {
					getMbcProcEspPorCirurgiasDAO().initialize(procEspPorCirurgia.getCid());
					getMbcProcEspPorCirurgiasDAO().initialize(procEspPorCirurgia.getMbcEspecialidadeProcCirgs());
					if (procEspPorCirurgia.getMbcEspecialidadeProcCirgs() != null) {
						getMbcProcEspPorCirurgiasDAO().initialize(procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getEspecialidade());
					}
					
					PropertyUtils.copyProperties(vo, procEspPorCirurgia);
					vo.setIndPrincipal(procEspPorCirurgia.getIndPrincipal());
					
					if(vo.getMbcEspecialidadeProcCirgs() != null && vo.getMbcEspecialidadeProcCirgs().getEspecialidade() != null){
					   vo.getMbcEspecialidadeProcCirgs().setEspecialidade(iAghuFacade.obterAghEspecialidadesPorChavePrimaria(vo.getMbcEspecialidadeProcCirgs().getEspecialidade().getSeq()));
					}
					
					if (procEspPorCirurgia.getProcedHospInterno()!=null){
						vo.setPhiSeq(procEspPorCirurgia.getProcedHospInterno().getSeq());
					}	
					if (procEspPorCirurgia.getIndPrincipal()){
						vo.setLado(procEspPorCirurgia.getCirurgia().getAgenda().getLadoCirurgia());	
					}
					if (procEspPorCirurgia.getProcedimentoCirurgico() != null) {
						vo.setProcedimentoCirurgico(procEspPorCirurgia.getProcedimentoCirurgico());
					}
					vo.setId(procEspPorCirurgia.getId());
					vo.setSeqPhi(procEspPorCirurgia.getProcedimentoCirurgico().getSeq());
					vo.setDescricaoPhi(procEspPorCirurgia.getProcedimentoCirurgico().getDescricao());
					vo.setSigla(procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getEspecialidade().getSigla());
					vo.setMbcEspecialidadeProcCirgs(procEspPorCirurgia.getMbcEspecialidadeProcCirgs());
					vo.getMbcEspecialidadeProcCirgs().setEspecialidade(procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getEspecialidade());
					vo.setIndContaminacao(procEspPorCirurgia.getProcedimentoCirurgico().getIndContaminacao());
					vo.setRegimeProcedSus(procEspPorCirurgia.getProcedimentoCirurgico().getRegimeProcedSus());
					vo.setTempoMinimo(procEspPorCirurgia.getProcedimentoCirurgico().getTempoMinimo());
					//acessa via lazy para estar disponívl na controller
					procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				resultado.add(vo);
			}
		}
		return resultado;
	}

	public List<CirurgiaTelaProfissionalVO> pesquisarProfissionaisAgendaProcedimentos(Integer crgSeq) {
		List<CirurgiaTelaProfissionalVO> resultado = new ArrayList<CirurgiaTelaProfissionalVO>();
		// CURSOR C10
		List<MbcProfCirurgias> listaProfCirurgias = this.getMbcProfCirurgiasDAO().pesquisarProfCirurgiasPorCrgSeq(crgSeq);
		for (MbcProfCirurgias profCirurgia : listaProfCirurgias) {
			// CURSOR C11 PARTE 1: Pesquisa profissionais que atuam na unidade
			List<MbcProfAtuaUnidCirgs> listaProfAtuaUnidCirgs = this.getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionaisAtuaCirurgiaAgendaProcedimentos(profCirurgia);
			VRapServidorConselho servidorConselho = null;
			Integer serMatricula = null;
			Short serVinCodigo = null;
			for (MbcProfAtuaUnidCirgs profAtuaUnidCirg : listaProfAtuaUnidCirgs) {
				serMatricula = profAtuaUnidCirg.getId().getSerMatricula();
				serVinCodigo = profAtuaUnidCirg.getId().getSerVinCodigo();
				// CURSOR C11 PARTE 2: Pesquisa se o profissional faz parte de algum conselho
				servidorConselho = this.getRegistroColaboradorFacade().obterVRapServidorConselhoPeloId(serMatricula, serVinCodigo, null);

				if (servidorConselho != null) {
					break;
				}
			}
			if (servidorConselho != null) {
				CirurgiaTelaProfissionalVO vo = new CirurgiaTelaProfissionalVO();
				try {
					PropertyUtils.copyProperties(vo, profCirurgia);
					vo.setIndRealizou(profCirurgia.getIndRealizou());
					vo.setIndResponsavel(profCirurgia.getIndResponsavel());
					// Busca especialidade
					vo.setEspecialidade(buscarEspecialidades(serMatricula, serVinCodigo));
				} catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
				vo.setServidorConselho(servidorConselho);
				resultado.add(vo);
			}
		}
		return resultado;
	}
	
	private String buscarEspecialidades(Integer serMatricula, Short serVinCodigo) {
		return this.getAghuFacade().obterEspecialidadeConcatenadasProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
	}


	public List<CirurgiaTelaAnestesiaVO> pesquisarAnestesiasAgendaProcedimentos(Integer crgSeq) {
		List<CirurgiaTelaAnestesiaVO> resultado = new ArrayList<CirurgiaTelaAnestesiaVO>();
		List<MbcAnestesiaCirurgias> listaAnestesiaCirurgias = this.getMbcAnestesiaCirurgiasDAO().pesquisarAnestesiaCirurgiaTipoAnestesiaAtivoPorCrgSeq(crgSeq);
		for (MbcAnestesiaCirurgias anestesiaCirurgia : listaAnestesiaCirurgias) {
			CirurgiaTelaAnestesiaVO vo = new CirurgiaTelaAnestesiaVO();
			try {
				PropertyUtils.copyProperties(vo, anestesiaCirurgia);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			resultado.add(vo);
		}
		return resultado;
	}
	
		

	
	
	
	public AghAtendimentos obterAtendimentoVigentePacienteInternado(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg){
		List<AghAtendimentos> listAtendimento =  this.getAghuFacade().pesquisarAtendimentoRegistroCirurgiaRealizada(atdSeq, pacCodigo, dthrInicioCirg);
		for (AghAtendimentos aghAtendimentos : listAtendimento) {
			if(aghAtendimentos.getOrigem() == DominioOrigemAtendimento.U || aghAtendimentos.getOrigem() == DominioOrigemAtendimento.I 
					&& aghAtendimentos.getIndPacAtendimento() == DominioPacAtendimento.S ){
				return aghAtendimentos;
			}
		}
		return null;
	}
	
	public boolean isLateralidade(final Integer procedimentoCirurgicoSeq){		
		return getMbcProcedimentoCirurgicoDAO().listarProcedimentosCirurgicosPorSeqLateralidade(procedimentoCirurgicoSeq, Boolean.TRUE).isEmpty();
	}
	
	
	public boolean temTarget(String componente, String target) throws ApplicationBusinessException {
		return getICascaFacade().usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), componente, target);
	}

	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.iInternacaoFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.iExamesFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}
	
	protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return this.iBlocoCirurgicoCadastroApoioFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	private CirurgiaRemoverPacienteRN getCirurgiaRemoverPacienteRN() {
		return cirurgiaRemoverPacienteRN;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

	protected VMbcProcEspDAO getVMbcProcEspDAO() {
		return vMbcProcEspDAO;
	}

	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}

	protected AgendaProcedimentosInserirON getAgendaProcedimentosInserirON() {
		return agendaProcedimentosInserirON;
	}

	protected AgendaProcedimentosAtualizarON getAgendaProcedimentosAtualizarON() {
		return agendaProcedimentosAtualizarON;
	}

	protected AgendaProcedimentosFuncoesON getAgendaProcedimentosFuncoesON() {
		return agendaProcedimentosFuncoesON;
	}
	
	protected AvisoAgendamentoCirurgiaON getAvisoAgendamentoCirurgiaON() {
		return avisoAgendamentoCirurgiaON;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}

	protected FatPaTuTrCnVmBcRN getFatPaTuTrCnVmBcRN() {
		return fatPaTuTrCnVmBcRN;
	}

	protected MbcProcEspPorCirurgiasRN getMbcProcEspPorCirurgiasRN() {
		return mbcProcEspPorCirurgiasRN;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}

	protected MbcAnestesiaCirurgiasRN getMbcAnestesiaCirurgiasRN() {
		return mbcAnestesiaCirurgiasRN;
	}

	protected MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN() {
		return mbcCirurgiasVerificacoesRN;
	}

	protected MbcCompSangProcCirgRN getMbcCompSangProcCirgRN() {
		return mbcCompSangProcCirgRN;
	}

	protected MbcFichaAnestesiasRN getMbcFichaAnestesiasRN() {
		return mbcFichaAnestesiasRN;
	}
}