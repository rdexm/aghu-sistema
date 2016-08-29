package br.gov.mec.aghu.internacao.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.internacao.business.InternacaoUtilRN.InternacaoUtilRNExceptionCode;
import br.gov.mec.aghu.internacao.business.vo.AtualizarEventosVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarIndicadoresInternacaoVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarPacienteTipo;
import br.gov.mec.aghu.internacao.business.vo.LeitoQuartoUnidadeFuncionalVO;
import br.gov.mec.aghu.internacao.business.vo.RegistrarMovimentoInternacoesVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinAcompanhantesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoJnDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentosAtendUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMvtoInternacaoDAO;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.transferir.business.ITransferirPacienteFacade;
import br.gov.mec.aghu.internacao.vo.ValidaContaTrocaConvenioVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinCidsInternacaoId;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinInternacaoJn;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosAtendUrgencia;
import br.gov.mec.aghu.model.AinMovimentosAtendUrgenciaId;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipConveniosSaudePacienteId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/*
 * Classe que implementa as triggers da tabela AIN_INTERNACOES
 */
@SuppressWarnings({ "deprecation", "PMD.AghuTooManyMethods", "PMD.AtributoEmSeamContextManager",
		"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength",
		"PMD.CouplingBetweenObjects", "PMD.NcssTypeCount" })
@Stateless
public class InternacaoRN extends BaseBusiness {

	private static final String COMMA_INT_SEQ_EQUALS = ", INT_SEQ = ";

	@EJB
	private SubstituirProntuarioAtendimentoRN substituirProntuarioAtendimentoRN;
	
	@EJB
	private AtualizaInternacaoRN atualizaInternacaoRN;
	
	@EJB
	private ValidaInternacaoRN validaInternacaoRN;
	
	@EJB
	private AtendimentoUrgenciaRN atendimentoUrgenciaRN;
	
	@EJB
	private VerificaInternacaoRN verificaInternacaoRN;
	
	@EJB
	private InternacaoUtilRN internacaoUtilRN;
	
	@EJB
	private DarAltaPacienteON darAltaPacienteON;
	
	@EJB
	private CadastroInternacaoON cadastroInternacaoON;
	
	private static final Log LOG = LogFactory.getLog(InternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;
	
	@Inject
	private AinInternacaoJnDAO ainInternacaoJnDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private ITransferirPacienteFacade transferirPacienteFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;
	
	@Inject
	private AinMovimentosAtendUrgenciaDAO ainMovimentosAtendUrgenciaDAO;
	
	@Inject
	private FatConvenioSaudePlanoDAO convenioSaudePlanoDAO;

	@Inject 
	private AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private FatConvenioSaudeDAO convenioSaudeDAO;
	
	@Inject
	AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;

	@Inject
	AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4681247280908209985L;

//	private static final String ENTITY_MANAGER = "entityManager";

	private static final Integer MAIOR_DE_IDADE = 18; 
	
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum InternacaoRNExceptionCode implements BusinessExceptionCode {
		AIN_00534, AIN_00283, AIN_00668, AIN_00740, AIN_00739, AIN_00660, AIN_00659, AIN_00686, 
		AIN_00688, AIN_00722, RAP_00175, AIN_00716, AIN_00342, AIN_00717, AIN_00718, AIN_00719,
		AIN_00720, AIN_00721, AIN_00738, MENSAGEM_ERRO_ALTA_SEM_SAIDA_PACIENTE, AIN_00737, 
		AIN_00618, AIN_00884, AIN_00360, AIN_00361, AIN_00363, AIN_00894, CONTA_ENCERRADA, AIN_00838, ERRO_ENCERRA_RONDA,
		ERRO_NEGOCIO_ORA, AIN_00391, INDICADORES_INTERNACAO_INVALIDOS, AIP_00013, MENSAGEM_LAUDOS_NAO_CADASTRADOS_PARA_INTERNACAO, 
		MENSAGEM_ESPECIALIDADE_NAO_CADASTRADA_NO_LAUDO_ENCONTRADO, MENSAGEM_SERVIDOR_NAO_CADASTRADO_NO_LAUDO_ENCONTRADO,
		MENSAGEM_PACIENTE_INFORMADO_NAO_CADASTRADO;
	}

	public AinInternacao obterInternacaoAnterior(final Integer seqInternacao) {
		final List<Object[]> listaObjetos = this.getAinInternacaoDAO().obterInformacoesInternacaoAnterior(seqInternacao);
		return this.popularInternacaoAnterior(listaObjetos.get(0), seqInternacao);
	}
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public AinInternacao popularInternacaoAnterior(final Object[] vetorObject, final Integer seqInternacao){
		
		AinInternacao internacao = new AinInternacao();
		internacao.setSeq(seqInternacao);
		if(vetorObject[0]!=null){
			final String tamCodigo = (String)vetorObject[0];
			internacao.setTipoAltaMedica(cadastrosBasicosInternacaoFacade.obterTipoAltaMedica(tamCodigo));
		}
		if(vetorObject[1]!=null){
			internacao.setDthrAltaMedica((Date)vetorObject[1]);
		}
		if(vetorObject[2]!=null){
			internacao.setDtSaidaPaciente((Date)vetorObject[2]);
		}
		if(vetorObject[3]!=null && vetorObject[4]!=null){
			final Short cspCnvCodigo = (Short) vetorObject[3];
			final Byte seq = (Byte) vetorObject[4];
			internacao.setConvenioSaudePlano(faturamentoApoioFacade.obterConvenioSaudePlano(cspCnvCodigo, seq));
		}
		if(vetorObject[5]!=null){
			final Integer pacCodigo = (Integer) vetorObject[5];
			internacao.setPaciente(pacienteFacade.obterPaciente(pacCodigo));
		}
		if(vetorObject[6]!=null && vetorObject[7]!=null){
			final Integer seq = (Integer) vetorObject[6]; 
			final Short phoSeq = (Short) vetorObject[7];
			internacao.setItemProcedimentoHospitalar(faturamentoFacade.obterItemProcedHospitalar(phoSeq, seq));
		}
		if(vetorObject[8]!=null){
			internacao.setDthrPrimeiroEvento((Date)vetorObject[8]);
		}
		if(vetorObject[9]!=null){
			internacao.setDthrUltimoEvento((Date)vetorObject[9]);
		}
		if(vetorObject[10]!=null){
			internacao.setDtPrevAlta((Date)vetorObject[10]);
		}
		if(vetorObject[11]!=null){
			final String leitoID = (String) vetorObject[11];
			internacao.setLeito(cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitoID));
		}
		if(vetorObject[12]!=null){
			final Short numero = (Short) vetorObject[12]; 
			internacao.setQuarto(cadastrosBasicosInternacaoFacade.obterQuarto(numero));
		}
		if(vetorObject[13]!=null){
			final Short unfSeq = (Short) vetorObject[13];
			internacao.setUnidadesFuncionais(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		}
		if(vetorObject[14]!=null){
			final Short espSeq = (Short) vetorObject[14];
			internacao.setEspecialidade(cadastrosBasicosInternacaoFacade.obterEspecialidade(espSeq));
		}
		if(vetorObject[15]!=null){
			internacao.setIndLocalPaciente((DominioLocalPaciente)vetorObject[15]);
		}
		if(vetorObject[16]!=null && vetorObject[17]!=null){
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula((Integer) vetorObject[16]);
			id.setVinCodigo((Short) vetorObject[17]);
			internacao.setServidorProfessor(registroColaboradorFacade.obterRapServidoresPorChavePrimaria(id));
		}
		if(vetorObject[18]!=null){
			internacao.setDthrInternacao((Date)vetorObject[18]);
		}
		if(vetorObject[19]!=null){
			internacao.setIndPacienteInternado((Boolean)vetorObject[19]);
		}
		if(vetorObject[20]!=null){
			final Integer seq = (Integer) vetorObject[20]; 
			internacao.setProjetoPesquisa(examesLaudosFacade.obterProjetoPesquisa(seq));
		}
		if(vetorObject[21]!=null && vetorObject[22]!=null){
			final RapServidoresId id = new RapServidoresId();
			id.setMatricula((Integer) vetorObject[21]);
			id.setVinCodigo((Short) vetorObject[22]);
			internacao.setServidorDigita(registroColaboradorFacade.obterRapServidoresPorChavePrimaria(id));
		}
		if(vetorObject[23]!=null){
			final Boolean envProntUnidInt = (Boolean) vetorObject[23]; 
			internacao.setEnvProntUnidInt(envProntUnidInt);
		}
		if(vetorObject[24]!=null){
			final Integer codigo = (Integer) vetorObject[24];
			final AinTiposCaraterInternacao tipoCaraterInternacao = this.getCadastroInternacaoON().obterTipoCaraterInternacao(codigo);
			internacao.setTipoCaracterInternacao(tipoCaraterInternacao);
		}
		if(vetorObject[25]!=null){
			final Short seq = (Short) vetorObject[25];
			final AghOrigemEventos origemEvento = this.getCadastroInternacaoON().obterOrigemEvento(seq);
			internacao.setOrigemEvento(origemEvento);
		}
		if(vetorObject[26]!=null){
			final Boolean indSaidaPaciente = (Boolean) vetorObject[26]; 
			internacao.setIndSaidaPac(indSaidaPaciente);
		}
		if(vetorObject[27]!=null){
			final Boolean indDifClasse = (Boolean) vetorObject[27]; 
			internacao.setIndDifClasse(indDifClasse);
		}
		if(vetorObject[28]!=null){
			final Integer seq = (Integer) vetorObject[28]; 
			final AinAtendimentosUrgencia atendimentoUrgencia = this.getCadastroInternacaoON().obterAtendimentoUrgencia(seq); 
			internacao.setAtendimentoUrgencia(atendimentoUrgencia);
		}
		if(vetorObject[29]!=null){
			final Integer seq = (Integer) vetorObject[29]; 
			final AghInstituicoesHospitalares instituicaoHospitalar = this.getCadastroInternacaoON().obterInstituicaoHospitalar(seq); 
			internacao.setInstituicaoHospitalar(instituicaoHospitalar);
		}
		if(vetorObject[30]!=null){
			final Integer seq = (Integer) vetorObject[30]; 
			final AghInstituicoesHospitalares instituicaoHospitalarTransferencia = this.getCadastroInternacaoON().obterInstituicaoHospitalar(seq); 
			internacao.setInstituicaoHospitalarTransferencia(instituicaoHospitalarTransferencia);
		}
		
		if(vetorObject[31]!=null){
			final String justificativaDel = (String) vetorObject[31]; 
			internacao.setJustificativaAltDel(justificativaDel);
		}
		
		if(vetorObject[32]!=null){
			final Integer codigo = (Integer) vetorObject[32];
			final AinObservacoesPacAlta observacoesPacAlta = cadastrosBasicosInternacaoFacade.obterObservacoesPacAlta(codigo);
			internacao.setObservacaoPacienteAlta(observacoesPacAlta);
		}
		
		if(vetorObject[33]!=null){
			final String indConsMarcada = (String) vetorObject[33];
			internacao.setIndConsMarcada(indConsMarcada);
		}
		
		if(vetorObject[34]!=null){
			final Integer docObito = (Integer) vetorObject[34];
			internacao.setDocObito(docObito);
		}
		
		if(vetorObject[35]!=null){
			final Date dataHoraProntuarioBuscado = (Date) vetorObject[35];
			internacao.setDataHoraProntuarioBuscado(dataHoraProntuarioBuscado);
		}
		
		if(vetorObject[36]!=null){
			final DominioSimNao indAltaManual = (DominioSimNao) vetorObject[36];
			internacao.setIndAltaManual(indAltaManual);
		}
		
		if(vetorObject[37]!=null){
			final String prontuarioLegal = (String) vetorObject[37];
			internacao.setProntuarioLegal(prontuarioLegal);
		}
		return internacao;
	}

	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarInternacao(final AinInternacao internacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario
			, RapServidores servidor
			, boolean umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL) throws BaseException {
		//this.atualizarInternacao(internacao, null, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor);
		this.atualizarInternacao(internacao, null, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor, false, umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL);
	}
	
	public void estornarAltaPaciente(final Integer seqInternacao, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores servidor) throws BaseException{
		
		try {
			final AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
			final AinInternacao internacaoOld = (AinInternacao) BeanUtils.cloneBean(internacao);
			
			internacao.setTipoAltaMedica(null);
			internacao.setDthrAltaMedica(null);
			internacao.setDtSaidaPaciente(null);
			internacao.setInstituicaoHospitalarTransferencia(null);
			internacao.setDocObito(null);
			
			atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor);
		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.ERRO_NEGOCIO_ORA);
		}
	}
	
	/**
	 * Busca o Servidor pela matricula e vinculo e então chama o método atualizarInternacao passando o mesmo Se deu alguma exceção negocial retorna a mensagem
	 * da exceção, senão null.
	 * 
	 * Web Service #38824
	 * 
	 * @param seqInternacao
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * @param nomeMicrocomputador
	 * @param matriculaServidorLogado
	 * @param vinCodigoServidorLogado
	 * @return
	 */
	public String atualizarServidorProfessorInternacao(final Integer seqInternacao, final Integer matriculaProfessor, final Short vinCodigoProfessor, String nomeMicrocomputador,
			final Integer matriculaServidorLogado, final Short vinCodigoServidorLogado) {
		try {
			final RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidor(vinCodigoServidorLogado, matriculaServidorLogado);
			final RapServidores servidorProfessor = getRegistroColaboradorFacade().obterServidor(vinCodigoProfessor, matriculaProfessor);
			final AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
			if (internacao != null) {
				internacao.setServidorProfessor(servidorProfessor);				
				this.atualizarInternacao(internacao, null, nomeMicrocomputador, new Date(), false, servidorLogado, false);
				this.flush();
			}
			return null;
		} catch (BaseException e) {
			String message = getResourceBundleValue(e.getCode().toString(), e.getParameters());
			return message;
		}
	}

	/**
	 * Busca o Servidor logado e chama o método atualizarInternacao passando o mesmo
	 * 
	 * @param internacao
	 * @param internacaoOld
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @param substituirProntuario
	 * @throws BaseException
	 */
	public void atualizarInternacao(final AinInternacao internacao, AinInternacao internacaoOld, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, RapServidores servidorLogado) throws BaseException {
		this.atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidorLogado, false);
	}
	
	/**
	 * Estorno da Internação 
	 * 
	 * @param internacao
	 * @param internacaoOld
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @param substituirProntuario
	 * @throws BaseException
	 */
	public void atualizarInternacaoComEstorno(final AinInternacao internacao, AinInternacao internacaoOld, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		this.atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidorLogado, true);
	}
	
	
	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException
	 * 
	 *  Invocar este método caso tenha algum flush antes do final da transacao
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public void atualizarInternacao(final AinInternacao internacao,
			AinInternacao internacaoOld, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, RapServidores servidorLogado, final Boolean isEstornoInternacao) throws BaseException {

		this.atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador
				, dataFimVinculoServidor, substituirProntuario, servidorLogado, isEstornoInternacao, false);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	private void atualizarInternacao(final AinInternacao internacao,
			AinInternacao internacaoOld, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, RapServidores servidorLogado, final Boolean isEstornoInternacao, boolean umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL) throws BaseException {
		
		if(internacaoOld == null) {
			internacaoOld = this.obterInternacaoAnterior(internacao.getSeq());
		}
		
		final Date dataLimite = null;
		Date dataInternacao = null;
		final Integer procedimentoHospitalarInterno = null;
		String oldTamCodigo = null;
		String tamCodigo = null;
		Short cnvCodigo = null;
		Short cnvCodigoOld = null;
		Integer codigoPaciente = null;
		Integer codigoPacienteOld = null;
		Integer iphSeqOld = null;
		Short phoSeqOld = null;
		Integer iphSeq = null;
		Short phoSeq = null;
		Byte convenioSaudePlanoSeq = null;
		Byte convenioSaudePlanoSeqOld = null;
		Integer matriculaOld = null;
		Integer matricula = null;
		Short vinCodigoOld = null;
		Short vinCodigo = null;
		Integer atendimentoUrgenciaSeq = null;
		Integer projetoPesquisaSeq = null;
		
		if(internacaoOld.getTipoAltaMedica()!=null){
			oldTamCodigo = internacaoOld.getTipoAltaMedica().getCodigo(); 
		}
		if(internacao.getTipoAltaMedica() != null) {
			tamCodigo = internacao.getTipoAltaMedica().getCodigo();
		}
		DominioTransacaoAltaPaciente transacao = this.getAtualizaInternacaoRN().defineTransacao(oldTamCodigo, tamCodigo, internacaoOld.getDthrAltaMedica(), internacao.getDthrAltaMedica(), internacaoOld.getDtSaidaPaciente(), internacao.getDtSaidaPaciente());
		
		//Parametros declarados nas triggers porem nao utilizados
		//AghParametros p1 = aghParametrosON.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);
		//AghParametros p2 = aghParametrosON.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H);
		final AghParametros altaObitoMaior24 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ALTA_OBITO_MAIOR_24HS);
		final AghParametros altaObitoMenor24 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ALTA_OBITO_MENOR_24HS);
		final AghParametros transferenciaInexistente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TRANSFERENCIA_INEXISTENTE);
		final AghParametros convenioAposAltaCnv = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_APOS_ALTA_CNV);
		final AghParametros convenioAposAltaAlt = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_APOS_ALTA_ALT);
		final AghParametros altaMedica = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ALTA_MEDICA);
		final AghParametros convenioSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
		 
		if(internacao.getConvenioSaudePlano()!= null && internacao.getConvenioSaudePlano().getId()!=null){
			cnvCodigo = internacao.getConvenioSaudePlano().getId().getCnvCodigo();
		}
		
		if(internacaoOld.getConvenioSaudePlano()!= null && internacaoOld.getConvenioSaudePlano().getId()!=null){
			cnvCodigoOld = internacaoOld.getConvenioSaudePlano().getId().getCnvCodigo();
		}
		
		if(transacaoEstornaAlta(transacao)&& cnvCodigo !=null && cnvCodigo > convenioSus.getVlrNumerico().intValue()){
			this.getAtualizaInternacaoRN().atualizaDataAltaConvenio(internacao.getSeq(), internacao.getDthrAltaMedica(), internacaoOld.getDthrAltaMedica());
		}
		
		/*
		 * IF    aink_util.modificados (:old.pac_codigo,:new.pac_Codigo)
 		   OR    aink_util.modificados (:old.dthr_internacao,:old.dthr_internacao)
 		   A segunda parte da condição foi omitida por comparar valores iguais (old.dthr_internacao)
 		   e não apresentar alteração no comportamento original da aplicação
		 */
		if(internacao.getPaciente()!=null){
			codigoPaciente = internacao.getPaciente().getCodigo();
		}
		
		if(internacaoOld.getPaciente()!=null){
			codigoPacienteOld = internacaoOld.getPaciente().getCodigo();
		}
		if (!substituirProntuario && internacaoUtilRN.modificados(codigoPacienteOld,
				codigoPaciente)){
			this.getVerificaInternacaoRN().verificarObitoPaciente(codigoPaciente, internacao.getDthrInternacao(), DominioOperacoesJournal.UPD);
		}
		if(internacaoOld.getItemProcedimentoHospitalar()!=null && internacaoOld.getItemProcedimentoHospitalar().getId()!=null){
			iphSeqOld = internacaoOld.getItemProcedimentoHospitalar().getId().getSeq();
			phoSeqOld = internacaoOld.getItemProcedimentoHospitalar().getId().getPhoSeq();	
		}
		
		if(internacao.getItemProcedimentoHospitalar()!=null && internacao.getItemProcedimentoHospitalar().getId()!=null){
			iphSeq = internacao.getItemProcedimentoHospitalar().getId().getSeq();
			phoSeq = internacao.getItemProcedimentoHospitalar().getId().getPhoSeq();
		}
		
		if (internacaoUtilRN.modificados(phoSeqOld,phoSeq)||
				(internacaoUtilRN.modificados(iphSeqOld,iphSeq))){
			this.getVerificaInternacaoRN().verificarItemProcedimentoHospitalar(phoSeq,iphSeq);
		}
		if(internacao.getDthrAltaMedica()!=null && internacao.getDtSaidaPaciente()==null){
			throw new ApplicationBusinessException(
					InternacaoRN.InternacaoRNExceptionCode.MENSAGEM_ERRO_ALTA_SEM_SAIDA_PACIENTE);
		}
		if(transacaoDeAlta(transacao)){
			this.getVerificaInternacaoRN().verificarProntuario(codigoPaciente);	
			if((((tamCodigo!=null && tamCodigo.equals(altaObitoMaior24.getVlrTexto()))
					||
					(tamCodigo!=null && tamCodigo.equals(altaObitoMenor24.getVlrTexto())))&&
					(cnvCodigo!=null && cnvCodigo>convenioSus.getVlrNumerico().intValue()))||
					(((tamCodigo!=null && !tamCodigo.equals(altaObitoMaior24.getVlrTexto()))
							&&
							!(tamCodigo!=null && tamCodigo.equals(altaObitoMenor24.getVlrTexto()))))&&
							internacao.getDocObito()!=null)	{
							this.getValidaInternacaoRN().verificarDocumentoObito(tamCodigo, internacao.getDocObito());
			}
		}
		
		if (internacaoUtilRN.modificados(internacaoOld.getDthrPrimeiroEvento(),
			internacao.getDthrPrimeiroEvento())||
			internacaoUtilRN.modificados(internacaoOld.getDthrUltimoEvento(),
			internacao.getDthrUltimoEvento())){
				
			final Date dtAlta = transferirPacienteFacade.getDthrAltaMedica(internacao.getSeq());
			if(dtAlta != null && internacao.getDthrUltimoEvento().after(dtAlta)) {
				internacao.setDthrUltimoEvento(dtAlta);
			}
		
			AtualizarEventosVO atualizarEventosVO = this.getAtualizaInternacaoRN().atualizarEventos(internacao.getDthrPrimeiroEvento(), internacao.getDthrUltimoEvento());
			internacao.setDthrPrimeiroEvento(atualizarEventosVO.getDthrPrimeiroEvento()); 
			internacao.setDthrUltimoEvento(atualizarEventosVO.getDthrUltimoEvento());
		}
		if (!substituirProntuario && (internacaoUtilRN.modificados(oldTamCodigo,
				tamCodigo)||
				internacaoUtilRN.modificados(internacaoOld.getDtSaidaPaciente(),
						internacao.getDtSaidaPaciente()))){
				this.getVerificaInternacaoRN().verificarAlta(tamCodigo, internacao.getDtSaidaPaciente());	
		}
		
		if (!substituirProntuario && internacaoUtilRN.modificados(internacaoOld.getDtSaidaPaciente(),
				internacao.getDtSaidaPaciente())){
			this.getVerificaInternacaoRN().verificarDataSaidaPaciente(internacao.getDtSaidaPaciente());
		}
		if (!substituirProntuario && internacaoUtilRN.modificados(internacaoOld.getDtPrevAlta(),
				internacao.getDtPrevAlta())){
			this.getValidaInternacaoRN().verificarPrevisaoAlta(internacao.getDtPrevAlta(), internacaoOld.getDtPrevAlta());
		}
		if (internacaoUtilRN.modificados(oldTamCodigo,
				tamCodigo)){
			AtualizarIndicadoresInternacaoVO atInternacaoVO = this.getAtualizaInternacaoRN().atualizarIndicadoresInternacao(internacao.getDthrAltaMedica(), tamCodigo, internacao.getIndSaidaPac());	
			internacao.setIndPacienteInternado(atInternacaoVO.getIndPacienteInternado());
			internacao.setIndSaidaPac(atInternacaoVO.getIndSaidaPaciente());
		}
		
		if (internacaoUtilRN.modificados(internacaoOld.getDtSaidaPaciente(),
				internacao.getDtSaidaPaciente())){
			this.encerrarRonda(internacao.getSeq(), internacao.getDtSaidaPaciente());
			final Boolean saidaPaciente  = this.getAtualizaInternacaoRN().atualizarIndicadorSaidaPaciente(internacao.getDtSaidaPaciente());
			final AtualizarIndicadoresInternacaoVO atualizarIndicadoresInternacaoVO = this.getAtualizaInternacaoRN().atualizarIndicadoresInternacao(internacao.getDthrAltaMedica(), tamCodigo, saidaPaciente);
			internacao.setIndSaidaPac(atualizarIndicadoresInternacaoVO.getIndSaidaPaciente());
			internacao.setIndPacienteInternado(atualizarIndicadoresInternacaoVO.getIndPacienteInternado());

		}
		
		if (internacaoUtilRN.modificados(internacaoOld.getDthrAltaMedica(),
				internacao.getDthrAltaMedica())){
			this.getVerificaInternacaoRN().verificarDataAlta(internacao.getDthrAltaMedica());
			this.getVerificaInternacaoRN().verificarContaPaciente(internacao.getSeq(), internacao.getDthrAltaMedica(), internacaoOld.getDthrAltaMedica(), altaMedica.getVlrTexto(), dataLimite, procedimentoHospitalarInterno);
		}
		final AinLeitos leitoOld = internacaoOld.getLeito();
		final AinLeitos leito = internacao.getLeito();
		String leitoID = null;
		String leitoIDOld = null;
		if(leito!=null){
			leitoID = leito.getLeitoID();
		}
		if(leitoOld!=null){
			leitoIDOld = leitoOld.getLeitoID();
		}
		final AinQuartos quartoOld = internacaoOld.getQuarto();
		final AinQuartos quarto = internacao.getQuarto();
		Short quartoNumero = null; 
		Short quartoNumeroOld = null;
		if(quarto != null) {
			quartoNumero = quarto.getNumero();
		}
		if(quartoOld != null) {
			quartoNumeroOld = quartoOld.getNumero();
		}
		final AghUnidadesFuncionais unidadeFuncionalOld = internacaoOld.getUnidadesFuncionais();
		final AghUnidadesFuncionais unidadeFuncional = internacao.getUnidadesFuncionais();
		Short unidadeFuncionalSeq = null;
		Short unidadeFuncionalSeqOld = null;
		if(unidadeFuncional!=null){
			unidadeFuncionalSeq = unidadeFuncional.getSeq();
		}
		if(unidadeFuncionalOld!=null){
			unidadeFuncionalSeqOld = unidadeFuncionalOld.getSeq();
		}
		final AghEspecialidades especialidadeOld = internacaoOld.getEspecialidade();
		final AghEspecialidades especialidade = internacao.getEspecialidade();
		Short especialidadeSeq = null;
		Short especialidadeSeqOld = null;
		if(especialidade!=null){
			especialidadeSeq = especialidade.getSeq();
		}
		if(especialidadeOld!=null){
			especialidadeSeqOld = especialidadeOld.getSeq();
		}
		
		/*
		 * Techo comentado conforme solicitado na melhoria #50951
		 * if (!substituirProntuario && (internacaoUtilRN.modificados(leitoIDOld,leitoID)||
				internacaoUtilRN.modificados(quartoNumeroOld,quartoNumero)||
						internacaoUtilRN.modificados(unidadeFuncionalSeqOld,unidadeFuncionalSeq)||
								internacaoUtilRN.modificados(especialidadeSeqOld,especialidadeSeq))){
				this.verificarBloqueioNeurologia(leitoID, quartoNumero, unidadeFuncionalSeq, especialidadeSeq);	
		}*/
		if (!substituirProntuario && internacaoUtilRN.modificados(leitoIDOld,leitoID)){
			this.getVerificaInternacaoRN().verificarSituacaoLeito(leitoID);
		}
		
		if (internacaoUtilRN.modificados(leitoIDOld,leitoID)||
				internacaoUtilRN.modificados(quartoNumeroOld,quartoNumero)||
						internacaoUtilRN.modificados(unidadeFuncionalSeqOld,unidadeFuncionalSeq)||
								internacaoUtilRN.modificados(internacaoOld.getIndLocalPaciente(),
										internacao.getIndLocalPaciente())){
			final DominioLocalPaciente localPaciente = this.getAtualizaInternacaoRN().atualizarIndicadorLocalizacao(leitoID, quartoNumero, unidadeFuncionalSeq);
			internacao.setIndLocalPaciente(localPaciente);
		}
		if(internacaoOld.getConvenioSaudePlano()!=null && internacaoOld.getConvenioSaudePlano().getId()!=null){
			convenioSaudePlanoSeqOld = internacaoOld.getConvenioSaudePlano().getId().getSeq();
		}

		if(internacao.getConvenioSaudePlano()!=null && internacao.getConvenioSaudePlano().getId()!=null){
			convenioSaudePlanoSeq = internacao.getConvenioSaudePlano().getId().getSeq();
		}

		if ((internacaoUtilRN.modificados(cnvCodigoOld, cnvCodigo)||
				internacaoUtilRN.modificados(convenioSaudePlanoSeqOld,convenioSaudePlanoSeq)||
						internacaoUtilRN.modificados(phoSeqOld,
								phoSeq))&&internacao.getConvenioSaudePlano()==null){
			this.getVerificaInternacaoRN().verificarConvenio(cnvCodigo, convenioSaudePlanoSeq);
		}
		
		if (!substituirProntuario && internacaoUtilRN.modificados(cnvCodigoOld,cnvCodigo)){
			this.getVerificaInternacaoRN().verificarConvenioAposAlta(cnvCodigo, internacao.getDthrAltaMedica(), convenioAposAltaCnv.getVlrTexto());
		}
		
		if (!substituirProntuario && internacaoUtilRN.modificados(internacaoOld.getDthrAltaMedica(),
				internacao.getDthrAltaMedica())){
			this.getVerificaInternacaoRN().verificarConvenioAposAlta(cnvCodigo, internacao.getDthrAltaMedica(), convenioAposAltaAlt.getVlrTexto());
		}
		
		if(internacaoOld.getServidorProfessor()!=null && internacaoOld.getServidorProfessor().getId()!=null) {
			matriculaOld = internacaoOld.getServidorProfessor().getId().getMatricula();
			vinCodigoOld = internacaoOld.getServidorProfessor().getId().getVinCodigo();
		}
		
		if(internacao.getServidorProfessor()!=null && internacao.getServidorProfessor().getId()!=null) {
			matricula = internacao.getServidorProfessor().getId().getMatricula();
			vinCodigo = internacao.getServidorProfessor().getId().getVinCodigo();
		}

		if (!substituirProntuario && (internacaoUtilRN.modificados(matriculaOld,matricula)||
				internacaoUtilRN.modificados(vinCodigoOld,vinCodigo)||
						internacaoUtilRN.modificados(leitoIDOld,leitoID)||
								internacaoUtilRN.modificados(quartoNumeroOld,quartoNumero)||
										internacaoUtilRN.modificados(unidadeFuncionalSeqOld,unidadeFuncionalSeq)||
												internacaoUtilRN.modificados(especialidadeSeqOld,especialidadeSeq)||
														internacaoUtilRN.modificados(cnvCodigoOld,
																cnvCodigo)||
																internacao.getDthrInternacao().
																compareTo(internacaoOld.getDthrInternacao()) != 0
																		)){


			dataInternacao = internacao.getDthrUltimoEvento();
			
			this.getVerificaInternacaoRN().verificarEscalaProfessor(matricula, 
					vinCodigo, leitoID, 
					quartoNumero, unidadeFuncionalSeq, 
					especialidadeSeq, cnvCodigo,
					dataInternacao);
		}
			if (!substituirProntuario && internacaoUtilRN.modificados(leitoIDOld,leitoID)){
				this.getVerificaInternacaoRN().verificarLeitoAtivo(leitoID);
				this.getVerificaInternacaoRN().verificarMedidaPreventiva(leitoID, codigoPaciente);
			}
			if (!substituirProntuario && internacaoUtilRN.modificados(quartoNumeroOld,quartoNumero)){
				this.getVerificaInternacaoRN().verificarQuartoAtivo(quartoNumero);
			}
			if (!substituirProntuario && internacaoUtilRN.modificados(unidadeFuncionalSeqOld,
					unidadeFuncionalSeq)){
				this.getVerificaInternacaoRN().verificarUnidadeFuncionalAtivo(unidadeFuncionalSeq);
			}
			if (!substituirProntuario && (internacaoUtilRN.modificados(especialidadeSeqOld,especialidadeSeq)||
					internacaoUtilRN.modificados(codigoPacienteOld,
							codigoPaciente))
					){
				this.getVerificaInternacaoRN().verificarFaixaIdade(especialidadeSeq, codigoPaciente, internacao.getDthrInternacao());
				
			}
			if (!substituirProntuario && internacaoUtilRN.modificados(cnvCodigoOld,cnvCodigo)) {
				this.getVerificaInternacaoRN().verificarPermissaoConvenio(cnvCodigo);
			}
			
			if(internacao.getAtendimentoUrgencia()!=null){
				atendimentoUrgenciaSeq = internacao.getAtendimentoUrgencia().getSeq();
			}
			
			if (!substituirProntuario && (internacaoUtilRN.modificados(codigoPacienteOld,codigoPaciente)||
					internacaoUtilRN.modificados(internacaoOld.getDthrInternacao(),
							internacao.getDthrInternacao())||
					internacaoUtilRN.modificados(internacaoOld.getDthrAltaMedica(),
							internacao.getDthrAltaMedica()))
					){
				this.getVerificaInternacaoRN().verificarSobreposicaoPeriodoInternacao(atendimentoUrgenciaSeq, codigoPaciente,
						internacao.getDthrInternacao(), internacao.getDthrAltaMedica());
			}
			
			if (!substituirProntuario && (internacaoUtilRN.modificados(codigoPacienteOld,
					codigoPaciente)||
					internacaoUtilRN.modificados(phoSeqOld,phoSeq)||
					internacaoUtilRN.modificados(iphSeqOld,iphSeq))
					){
				this.getVerificaInternacaoRN().verificarSexoIdadePacienteProcedimento(codigoPaciente,
						phoSeq, 
						iphSeq,
						internacao.getDthrInternacao());
			}
			
			if (!substituirProntuario && internacaoUtilRN.modificados(internacaoOld.getDtPrevAlta(),
					internacao.getDtPrevAlta())
					){
				this.getValidaInternacaoRN().verificarDataPrevisaoAlta(internacao.getDtPrevAlta());
			}
			
			if (servidorLogado != null) {
				internacao.setServidorDigita(servidorLogado);
		} else {
			throw new ApplicationBusinessException(
					InternacaoRNExceptionCode.RAP_00175);
		}		
			
			
			if (internacaoUtilRN.modificados(internacaoOld.getDthrAltaMedica(),
					internacao.getDthrAltaMedica())
					){
				this.getAtualizaInternacaoRN().rnIntpAtuPlacar(codigoPaciente, especialidadeSeq);
			}
			
			if (!substituirProntuario && internacaoUtilRN.modificados(codigoPacienteOld,
					codigoPaciente)){
				this.getVerificaInternacaoRN().verificarProntuarioPaciente(codigoPaciente);
			}
			
			if (!substituirProntuario && (internacaoUtilRN.modificados(oldTamCodigo,
					tamCodigo)&&
					((tamCodigo!=null && tamCodigo.equals(altaObitoMaior24.getVlrTexto()))||
							(tamCodigo != null && tamCodigo.equals(altaObitoMenor24.getVlrTexto())))&&
							(cnvCodigo !=null && cnvCodigo >convenioSus.getVlrNumerico().intValue())||
							((tamCodigo!=null && !tamCodigo.equals(altaObitoMaior24.getVlrTexto()))&&
									(tamCodigo !=null && !tamCodigo.equals(altaObitoMenor24.getVlrTexto())))&&
									internacao.getDocObito()!=null)){
				this.getValidaInternacaoRN().verificarDocumentoObito(tamCodigo, internacao.getDocObito());
			}
				
			if(internacaoOld.getUnidadesFuncionais()!=null){
				unidadeFuncionalSeqOld = internacaoOld.getUnidadesFuncionais().getSeq(); 
			}
			ambulatorioFacade.gerarCheckOut(internacao.getSeq(),codigoPaciente,oldTamCodigo,
					tamCodigo, pesquisaInternacaoFacade.aincRetUnfSeq(internacaoOld.getUnidadesFuncionais(), internacaoOld.getQuarto(), internacaoOld.getLeito()),
					pesquisaInternacaoFacade.aincRetUnfSeq(internacao.getUnidadesFuncionais(), internacao.getQuarto(), internacao.getLeito()),
					internacaoOld.getIndPacienteInternado(), internacao.getIndPacienteInternado());
			if(internacao.getProjetoPesquisa()!=null){
				projetoPesquisaSeq = internacao.getProjetoPesquisa().getSeq();
			}
			
			if(internacaoUtilRN.modificados(internacaoOld.getProjetoPesquisa(),
					internacao.getProjetoPesquisa())){
				
			FatConvenioSaudePlano fatConvenioSaudePlano = this.getValidaInternacaoRN().atualizarConvenioProjetoPesquisa(projetoPesquisaSeq);
			if (fatConvenioSaudePlano != null) {
				internacao.setConvenioSaudePlano(fatConvenioSaudePlano);
				internacao.setConvenioSaude(fatConvenioSaudePlano.getConvenioSaude());				
			}
		}
			
		transacao = this.getAtualizaInternacaoRN().defineTransacao(oldTamCodigo, tamCodigo, internacaoOld.getDthrAltaMedica(), internacao.getDthrAltaMedica(), internacaoOld.getDtSaidaPaciente(), internacao.getDtSaidaPaciente());
		
		final String tipoMovimentoInternacaoDefinido = this.getAtualizaInternacaoRN().defineTipoMovimentoInternacao(internacao.getServidorProfessor(), internacaoOld.getServidorProfessor(), internacao.getLeito(), internacaoOld.getLeito(), internacao.getQuarto(), internacaoOld.getQuarto(), internacao.getUnidadesFuncionais(), internacaoOld.getUnidadesFuncionais(), internacao.getEspecialidade(), internacaoOld.getEspecialidade());
		//Valores declarados nas triggers porem nao utilizados
		//String tipoAltaObito = p1.getVlrTexto();
		//String tipoAltaObitoMais48h = p2.getVlrTexto();
		if (!tipoMovimentoInternacaoDefinido
				.equals(transferenciaInexistente.getVlrTexto())) {
			this.getAtualizaInternacaoRN().verificarSolicitacaoInternacao(codigoPaciente, substituirProntuario);
		}
		if (internacaoUtilRN.modificados(codigoPacienteOld,
				codigoPaciente)
				|| internacaoUtilRN.modificados(cnvCodigoOld,
						cnvCodigo)
				|| internacaoUtilRN.modificados(convenioSaudePlanoSeqOld, convenioSaudePlanoSeq)) {
			
			this.getAtualizaInternacaoRN().insereConvenios(codigoPaciente, cnvCodigo,convenioSaudePlanoSeq);
		}
		
		if(cnvCodigo!=null && cnvCodigo.equals(convenioSus.getVlrNumerico().shortValue())&& cnvCodigoOld!=null && !cnvCodigoOld.equals(convenioSus.getVlrNumerico().shortValue())){
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacaoOld.getEspecialidade(), internacaoOld.getServidorProfessor(), 1);
		}
		if(cnvCodigo!=null && !cnvCodigo.equals(convenioSus.getVlrNumerico().shortValue())&& cnvCodigoOld!=null && cnvCodigoOld.equals(convenioSus.getVlrNumerico().shortValue())){
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacaoOld.getEspecialidade(), internacaoOld.getServidorProfessor(), -1);
		}
		
		
		if (transacaoDeAlta(transacao)) {
			this.getAtualizaInternacaoRN().atualizarSolicitacaoTransferencia(internacao.getSeq(), internacao.getDthrAltaMedica());
			this.getAtualizaInternacaoRN().atualizaAltaObitoProcessaAlta(internacao.getTipoAltaMedica(), 
					internacao.getPaciente(), internacao.getDthrAltaMedica(), nomeMicrocomputador, dataFimVinculoServidor);
		}
		if (transacaoEstornaAlta(transacao)) {
			this.getAtualizaInternacaoRN().atualizarSolicitacaoTransferenciaPacienteEstornoAlta(internacao.getSeq(), internacaoOld.getDthrAltaMedica());
			this.getAtualizaInternacaoRN().atualizaAltaObitoEstornoAlta(internacaoOld.getTipoAltaMedica(), internacaoOld.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor);
		}
		if (transacaoAlteraAlta(transacao)) {
			if (internacaoUtilRN.modificados(oldTamCodigo,
					tamCodigo)
					|| internacaoUtilRN.modificados(internacaoOld
							.getDthrAltaMedica(), internacao
							.getDthrAltaMedica())) {
				this.getAtualizaInternacaoRN().atualizaAltaObitoAlteracaoAlta(internacao.getTipoAltaMedica(), 
						internacao.getPaciente(), internacao.getDthrAltaMedica(), nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
		if (internacaoUtilRN.modificados(especialidadeSeqOld,
				especialidadeSeq)
				|| internacaoUtilRN.modificados(vinCodigoOld,
						vinCodigo)
				|| internacaoUtilRN.modificados(matriculaOld,
						matricula)) {
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacaoOld.getEspecialidade(), internacaoOld.getServidorProfessor(), -1);
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacao.getEspecialidade(), internacao.getServidorProfessor(), 1);
		}
		if (transacaoDeAlta(transacao)) {
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacaoOld.getEspecialidade(), internacaoOld.getServidorProfessor(), -1);
		}
		if (transacaoEstornaAlta(transacao)) {
			this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacao.getEspecialidade(), internacao.getServidorProfessor(), 1);
		}
		if ((internacaoUtilRN.modificados(phoSeqOld,phoSeq) || internacaoUtilRN
				.modificados(iphSeqOld, iphSeq))
				&& cnvCodigo
						.equals(convenioSus.getVlrNumerico().shortValue())) {
			
			if (Boolean.FALSE.equals(umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL)){				
				this.getValidaInternacaoRN().atualizarSsmConta(internacao.getSeq(), phoSeq, iphSeq, nomeMicrocomputador, dataFimVinculoServidor);
			
			}
		}
		
		journalUpdate(internacaoOld, internacao, transacao);
		
		// Alteração na #46010. #47522 não realizar merge quando é estorno da internação, pois pode existir referências deletadas do objeto neste ponto.
		if(!isEstornoInternacao) {
			this.ainInternacaoDAO.merge(internacao);
		}
		
		this.enforceUpdate(internacaoOld, internacao, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		
		this.verificarIndicadoresInternacao(internacao);
		
	}
	
	private boolean transacaoAlteraAlta(DominioTransacaoAltaPaciente transacao) {
		return transacao!=null && transacao.equals(DominioTransacaoAltaPaciente.ALTERA_ALTA);
	}
	private boolean transacaoEstornaAlta(DominioTransacaoAltaPaciente transacao) {
		return transacao!=null && transacao.equals(DominioTransacaoAltaPaciente.ESTORNA_ALTA);
	}
	private boolean transacaoDeAlta(DominioTransacaoAltaPaciente transacao) {
		return transacao!=null && transacao.equals(DominioTransacaoAltaPaciente.PROCESSA_ALTA);
	}
	
	/**
	 * Este método interrompe a execução da alta se verificar que os campos ind_paciente_internado e
	 * ind_saida_pac estão setados para 'S' (situação que não pode ocorrer)
	 * Este método foi criado com o objetivo de descobrir a causa do erro relatado nas tarefas
	 * #15897 e #19889.
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void verificarIndicadoresInternacao(AinInternacao internacao) throws ApplicationBusinessException{
		if (internacao.getIndPacienteInternado() && internacao.getIndSaidaPac()){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.INDICADORES_INTERNACAO_INVALIDOS);
		}
	}
	
	/**
	 * ORADB: Procedure AINP_ENCERRA_RONDA
	 * @return 
	 *  
	 * 
	 * 
	 */
	//TODO verificar necessidade de implementacao desta procedure
	public void encerrarRonda(final Integer aciIntSeq, final Date dthrFim) throws ApplicationBusinessException{
		final List<AghAtendimentos> listaAtendimentos =  this.getAghuFacade().listarAtendimentosPorSeqInternacao(aciIntSeq);
		if(listaAtendimentos.size()==0){
			throw new ApplicationBusinessException(
					InternacaoRNExceptionCode.AIN_00884);
			
		}
		//OBS: O comentário abaixo encontrava-se na proc AINP_ENCERRA_RONDA em 1/7/10. Danilo.
		/* AGHU
		Bloco comentado de compatibilidade com sistema externo RONDA.
		Esta compatibilidade nãos será migrada para o sistema, porém deve ser 
		considerada na implementação para o HCPA*/
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().encerrarRonda(aciIntSeq, dthrFim, servidorLogado);
		} catch (final ObjetosOracleException e) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.ERRO_ENCERRA_RONDA, e);
		}
	
	}

	private ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	/**
	 * ORADB: Trigger AINT_INT_ARD
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public void posDelete(final AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getAtualizaInternacaoRN().atualizaAltaObitoEstornoAlta(
			internacao.getTipoAltaMedica(), 
			internacao.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor
		);
		if (internacao.getTipoAltaMedica() == null) {
			this.getAtualizaInternacaoRN().deletaProfissionaisEspecialidades (
				internacao.getEspecialidade(),
				internacao.getServidorProfessor(),
				-1
			);
		}
		
	}
	
	private void journalUpdate(final AinInternacao internacaoOld, final AinInternacao internacao,  DominioTransacaoAltaPaciente tpTransacao ) {
		if (
			!Objects.equals(internacaoOld.getSeq(), internacao.getSeq()) ||
			!Objects.equals(internacaoOld.getPaciente(), internacao.getPaciente()) ||
			!Objects.equals(internacaoOld.getEspecialidade(), internacao.getEspecialidade()) ||
			!Objects.equals(internacaoOld.getServidorDigita(), internacao.getServidorDigita()) ||
			!Objects.equals(internacaoOld.getServidorProfessor(), internacao.getServidorProfessor()) ||
			!Objects.equals(internacaoOld.getDthrInternacao(), internacao.getDthrInternacao()) ||
			!Objects.equals(internacaoOld.getEnvProntUnidInt(),	internacao.getEnvProntUnidInt()) ||
			!Objects.equals(internacaoOld.getTipoCaracterInternacao(), internacao.getTipoCaracterInternacao()) ||
			!Objects.equals(internacaoOld.getConvenioSaudePlano(), internacao.getConvenioSaudePlano()) ||
			!Objects.equals(internacaoOld.getOrigemEvento(), internacao.getOrigemEvento()) ||
			!Objects.equals(internacaoOld.getIndSaidaPac(), internacao.getIndSaidaPac()) ||
			!Objects.equals(internacaoOld.getIndDifClasse(), internacao.getIndDifClasse()) ||
			!Objects.equals(internacaoOld.getIndPacienteInternado(), internacao.getIndPacienteInternado()) ||
			!Objects.equals(internacaoOld.getIndLocalPaciente(), internacao.getIndLocalPaciente()) ||
			!Objects.equals(internacaoOld.getLeito(), internacao.getLeito()) ||
			!Objects.equals(internacaoOld.getQuarto(), internacao.getQuarto()) || 
			!Objects.equals(internacaoOld.getUnidadesFuncionais(), internacao.getUnidadesFuncionais()) ||
			!Objects.equals(internacaoOld.getTipoAltaMedica(), internacao.getTipoAltaMedica()) ||
			!Objects.equals(internacaoOld.getAtendimentoUrgencia(), internacao.getAtendimentoUrgencia()) ||
			!Objects.equals(internacaoOld.getDtPrevAlta(), internacao.getDtPrevAlta()) ||
			!Objects.equals(internacaoOld.getDthrAltaMedica(), internacao.getDthrAltaMedica()) ||
			!Objects.equals(internacaoOld.getDtSaidaPaciente(), internacao.getDtSaidaPaciente()) ||
			!Objects.equals(internacaoOld.getInstituicaoHospitalar(), internacao.getInstituicaoHospitalar()) ||
			!Objects.equals(internacaoOld.getInstituicaoHospitalarTransferencia(), internacao.getInstituicaoHospitalarTransferencia()) ||
			!Objects.equals(internacaoOld.getJustificativaAltDel(),	internacao.getJustificativaAltDel()) ||
			!Objects.equals(internacaoOld.getDthrPrimeiroEvento(), internacao.getDthrPrimeiroEvento()) ||
			!Objects.equals(internacaoOld.getDthrUltimoEvento(), internacao.getDthrUltimoEvento()) ||
			!Objects.equals(internacaoOld.getItemProcedimentoHospitalar(), internacao.getItemProcedimentoHospitalar()) 
		) {
			final AinInternacaoJn internacaoJn = obtemInternacaoJn(internacaoOld, DominioOperacoesJournal.UPD, tpTransacao);
			this.getAinInternacaoJnDAO().persistir(internacaoJn);
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private AinInternacaoJn obtemInternacaoJn (final AinInternacao internacao, final DominioOperacoesJournal operacao, DominioTransacaoAltaPaciente transacao) {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AinInternacaoJn internacaoJn = BaseJournalFactory.getBaseJournal(operacao, AinInternacaoJn.class, servidorLogado.getUsuario());
		
		internacaoJn.setSeq(internacao.getSeq());
		internacaoJn.setPacCodigo(internacao.getPaciente().getCodigo());
		internacaoJn.setEspSeq(internacao.getEspecialidade() == null ? null : internacao.getEspecialidade().getSeq());
		tratarServidor(internacao, internacaoJn);
		tratarProfessor(internacao, internacaoJn);
		internacaoJn.setDthrInternacao(internacao.getDthrInternacao());
		internacaoJn.setEnvProntUnidInt(internacao.getEnvProntUnidInt());
		tratarTipoCaraterInternacao(internacao, internacaoJn);
		tratarConvenioSaudePlano(internacao, internacaoJn);
		internacaoJn.setOevSeq(internacao.getOrigemEvento() == null ? null : internacao.getOrigemEvento().getSeq());
		internacaoJn.setIndSaidaPac(internacao.getIndSaidaPac());
		internacaoJn.setIndDifClasse(internacao.getIndDifClasse());
		internacaoJn.setIndPacienteInternado(internacao.getIndPacienteInternado());
		internacaoJn.setIndLocalPaciente(internacao.getIndLocalPaciente());
		internacaoJn.setLtoLtoId(internacao.getLeito() == null ? null : internacao.getLeito().getLeitoID());
		internacaoJn.setQrtNumero(internacao.getQuarto() == null ? null : internacao.getQuarto().getNumero());
		internacaoJn.setUnfSeq(internacao.getUnidadesFuncionais() == null ? null : internacao.getUnidadesFuncionais().getSeq());
		internacaoJn.setTamCodigo(internacao.getTipoAltaMedica() == null ? null : internacao.getTipoAltaMedica().getCodigo());
		internacaoJn.setAtuSeq(internacao.getAtendimentoUrgencia() == null ? null : internacao.getAtendimentoUrgencia().getSeq());
		internacaoJn.setDtPrevAlta(internacao.getDtPrevAlta());
		internacaoJn.setDthrAltaMedica(internacao.getDthrAltaMedica());
		internacaoJn.setDtSaidaPaciente(internacao.getDtSaidaPaciente());
		tratarInstituicaoHospitalar(internacao, internacaoJn);
		tratarInstituicaoHospitalarTransferencia(internacao, internacaoJn);
		internacaoJn.setJustificativaAltDel(internacao.getJustificativaAltDel());
		internacaoJn.setDthrPrimeiroEvento(internacao.getDthrPrimeiroEvento());
		internacaoJn.setDthrUltimoEvento(internacao.getDthrUltimoEvento());
		tratarItemProcedimentoHospitalar(internacao, internacaoJn);
		internacaoJn.setDthrAvisoSamis(internacao.getDataHoraAvisoSamis());
		if(transacao != null){

			if (transacao.equals(DominioTransacaoAltaPaciente.ESTORNA_ALTA)) {
				internacaoJn.setTipoTransacao(DominioTransacaoAltaPaciente.ESTORNA_ALTA.toString());
			} else if (transacao.equals(DominioTransacaoAltaPaciente.ALTERA_ALTA)) {
				internacaoJn.setTipoTransacao(DominioTransacaoAltaPaciente.ALTERA_ALTA.toString());
			} else if (transacao.equals(DominioTransacaoAltaPaciente.PROCESSA_ALTA)) {
				internacaoJn.setTipoTransacao(DominioTransacaoAltaPaciente.PROCESSA_ALTA.toString());
			}
		} else {
			internacaoJn.setTipoTransacao(DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA.getDescricao());
		}
		
		return internacaoJn;
	}
	
	private void tratarItemProcedimentoHospitalar(final AinInternacao internacao,AinInternacaoJn internacaoJn) {
		FatItensProcedHospitalar itemProcedimentoHospitalar = internacao.getItemProcedimentoHospitalar();
		internacaoJn.setIphSeq(itemProcedimentoHospitalar== null ? null : itemProcedimentoHospitalar.getId().getSeq());
		internacaoJn.setIphPhoSeq(itemProcedimentoHospitalar == null ? null : itemProcedimentoHospitalar.getId().getPhoSeq());
	}
	private void tratarInstituicaoHospitalarTransferencia(final AinInternacao internacao,AinInternacaoJn internacaoJn) {
		AghInstituicoesHospitalares instituicaoHospitalarTransferencia = internacao.getInstituicaoHospitalarTransferencia();
		internacaoJn.setIhoSeqTransferencia(instituicaoHospitalarTransferencia == null ? null : instituicaoHospitalarTransferencia.getSeq());
	}
	private void tratarInstituicaoHospitalar(final AinInternacao internacao, AinInternacaoJn internacaoJn) {
		AghInstituicoesHospitalares instituicaoHospitalar = internacao.getInstituicaoHospitalar();
		internacaoJn.setIhoSeqOrigem(instituicaoHospitalar == null ? null : instituicaoHospitalar.getSeq());
	}
	private void tratarConvenioSaudePlano(AinInternacao internacao, AinInternacaoJn internacaoJn) {
		FatConvenioSaudePlano convenioSaudePlano = internacao.getConvenioSaudePlano();
		internacaoJn.setCspCnvCodigo(convenioSaudePlano == null ? null : convenioSaudePlano.getId().getCnvCodigo());
		internacaoJn.setCspSeq(convenioSaudePlano == null ? null : convenioSaudePlano.getId().getSeq());
	}
	private void tratarTipoCaraterInternacao(AinInternacao internacao, AinInternacaoJn internacaoJn) {
		AinTiposCaraterInternacao tipoCaracterInternacao = internacao.getTipoCaracterInternacao();
		internacaoJn.setTciSeq(tipoCaracterInternacao == null ? null : tipoCaracterInternacao.getCodigo());
	}
	private void tratarProfessor(AinInternacao internacao, AinInternacaoJn internacaoJn) {
		RapServidores professor = internacao.getServidorProfessor();
		internacaoJn.setSerMatriculaProfessor(professor == null ? null : professor.getId().getMatricula());
		internacaoJn.setSerVinCodigoProfessor(professor == null ? null : professor.getId().getVinCodigo());
	}
	private void tratarServidor(AinInternacao internacao, AinInternacaoJn internacaoJn) {
		RapServidores servidor = internacao.getServidorDigita();
		internacaoJn.setSerMatriculaDigita(	servidor == null ? null : servidor.getId().getMatricula());
		internacaoJn.setSerVinCodigoDigita(	servidor == null ? null : servidor.getId().getVinCodigo());
	}
	
	/**
	 * ORADB: Procedure AINP_INT_ATU_SOL_TRANS
	 * 
	 * @param seq
	 * @param dthrAltaMedica
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSituacaoSolicitacaoTransferencia(final Integer seq,
			final Date dthrAltaMedica) throws ApplicationBusinessException {
		final IParametroFacade parametroFacade = this.getParametroFacade();
		final AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
		
		AghParametros aghParametros;
		DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoAtendida = null;
		DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoPendente = null;
		DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoCancelada = null;

		aghParametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_ATENDIDA);
		if (aghParametros != null && aghParametros.getVlrTexto() != null) {
			indSituacaoSolicitacaoAtendida = DominioSituacaoSolicitacaoInternacao.valueOf(aghParametros.getVlrTexto());
		}

		aghParametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_PENDENTE);
		if (aghParametros != null && aghParametros.getVlrTexto() != null) {
			indSituacaoSolicitacaoPendente = DominioSituacaoSolicitacaoInternacao.valueOf(aghParametros.getVlrTexto());
		}

		aghParametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_CANCELADA);
		if (aghParametros != null && aghParametros.getVlrTexto() != null) {
			indSituacaoSolicitacaoCancelada = DominioSituacaoSolicitacaoInternacao.valueOf(aghParametros.getVlrTexto());
		}

		if (indSituacaoSolicitacaoAtendida == null) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00659);
		} else if (indSituacaoSolicitacaoPendente == null) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00660);
		} else if (indSituacaoSolicitacaoCancelada == null) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00739);
		}

		final List<AinSolicTransfPacientes> list = carregaSolicitacoesTransfereciaPaciente(seq, indSituacaoSolicitacaoPendente,
				indSituacaoSolicitacaoAtendida);

		for (final AinSolicTransfPacientes ainSolicTransfPacientes : list) {
			final AinSolicTransfPacientes ainSolicTransfPacientesOriginal = ainSolicTransfPacientesDAO
					.obterPorChavePrimaria(ainSolicTransfPacientes.getSeq());
			ainSolicTransfPacientesDAO.desatachar(ainSolicTransfPacientesOriginal);

			ainSolicTransfPacientes.setObservacao("Cancelada por alta do paciente");
			ainSolicTransfPacientes.setCriadoEm(dthrAltaMedica);
			ainSolicTransfPacientes.setIndSitSolicLeito(indSituacaoSolicitacaoCancelada);

			atualizarSolicitacoesTransferenciaPacientes(ainSolicTransfPacientes, ainSolicTransfPacientesOriginal);
			ainSolicTransfPacientesDAO.flush();
		}
	}

	// Implementa o cursor c_atd da procedure procedure AINP_ENFORCE_INT_RULES
	private AghAtendimentos obtemAtendimentoPaciente(final AipPacientes paciente) {
		return this.getAghuFacade().obtemAtendimentoPaciente(paciente);
	}
	
	/**
	 * ORADB: procedure AINP_ENFORCE_INT_RULES (p_event = 'INSERT')
	 * @param intNew
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void enforceInsert(
		final AinInternacao intNew, String nomeMicrocomputador, final Date dataFimVinculoServidor
	) throws BaseException {
		AghAtendimentos atendimento;  // v_atd_seq, v_atd_dthr_inicio, v_lto_atu, v_gso_pac_codigo, v_gso_seqp
		if (intNew.getAtendimentoUrgencia() != null) {
			// CURSOR atd_atu
			atendimento = intNew.getAtendimentoUrgencia().getAtendimento();
		} else {
			// CURSOR c_atd
			atendimento = obtemAtendimentoPaciente(intNew.getPaciente());
		}
		
		if (atendimento != null) {
			logInfo("Encontrado atendimento SEQ = "+atendimento.getSeq()+". INT_SEQ = "+intNew.getSeq());
		} else {
			logInfo("Atendimento não encontrado. INT_SEQ = "+intNew.getSeq());
		}
		
		if (
			intNew.getAtendimentoUrgencia() == null ||
			(atendimento != null && !Objects.equals(atendimento.getLeito(), intNew.getLeito()))
		) {
			logInfo("Incluindo extrato leito. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().insereExtratoLeito (
				intNew.getLeito(),
				intNew.getServidorDigita(),
				intNew.getServidorProfessor(),
				intNew.getDthrInternacao(),
				null,
				intNew,
				true);
		}
		//TODO: Tarefa #14529 - Troca do uso do atributo mcoGestacoes para o gsoPacCodigo e gsoSeqp
		//Passando estes atributos para o método atualizaInformacoesLeito ao invés do objeto mcogestacoes 
		//possibilita o funcionamento tanto para o HCPA quanto aos HUs que ainda não possuem a perinatologia implantada.
		if (atendimento != null
				&& atendimento.getGsoPacCodigo() != null
				&& intNew.getPaciente().getCodigo().equals(
						atendimento.getGsoPacCodigo())) {
			logInfo("Atualizando informações do leito. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().atualizaInformacoesLeito(
				atendimento.getGsoPacCodigo(), // v_gso_pac_codigo, v_gso_seqp 
				atendimento.getGsoSeqp(),
				intNew.getLeito(),
				intNew.getQuarto(),
				intNew.getUnidadesFuncionais(), nomeMicrocomputador, dataFimVinculoServidor
			);
		}
		
		RegistrarMovimentoInternacoesVO rmiVO; // v_qrt_numero, v_unf_seq, v_lto_id
		/* IER52,IER46 */
		// AINK_INT_ATU.RN_INT_REG_MOV_INT
		logInfo("Registrando movimento internação. INT_SEQ = "+intNew.getSeq());
		rmiVO = this.getAtualizaInternacaoRN().registrarMovimentoInternacoes ( 
			intNew.getSeq(),
			intNew.getServidorDigita() == null ? null : intNew.getServidorDigita().getId().getMatricula(),
			intNew.getServidorDigita() == null ? null : intNew.getServidorDigita().getId().getVinCodigo(),
			intNew.getDthrInternacao(),
			intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
			intNew.getServidorProfessor() == null ? null : intNew.getServidorProfessor().getId().getMatricula(),
			intNew.getServidorProfessor() == null ? null : intNew.getServidorProfessor().getId().getVinCodigo(),
			intNew.getUnidadesFuncionais() == null ? null : intNew.getUnidadesFuncionais().getSeq(),
			intNew.getEspecialidade() == null ? null : intNew.getEspecialidade().getSeq(),
			intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
			intNew.getDthrInternacao(), 
			null, 
			null,
			DominioTransacaoAltaPaciente.INTERNACAO,
			intNew.getDtSaidaPaciente());
		
		// v_lto_id
		final AinLeitos leito = rmiVO.getLeitoID() == null ? null : 
			this.getAinLeitosDAO().obterPorChavePrimaria(rmiVO.getLeitoID());
		
		// v_qrt_numero
		final AinQuartos quarto = rmiVO.getNumeroQuarto() == null ? null : 
			this.getAinQuartosDAO().obterPorChavePrimaria(rmiVO.getNumeroQuarto());
		
		// v_unf_seq
		final AghUnidadesFuncionais unidadeFuncional = rmiVO.getSeqUnidadeFuncional() == null ? null :
			this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(rmiVO.getSeqUnidadeFuncional());
		
		/* CRE31 */
		logInfo("Atualizando atendimentos. INT_SEQ = "+intNew.getSeq());
		this.getAtualizaInternacaoRN().atualizaAtendimentos(
			intNew.getAtendimentoUrgencia() == null ? null : intNew.getAtendimentoUrgencia().getSeq(),
			intNew.getPaciente().getCodigo(),
			intNew.getDthrInternacao(),
			intNew
		);
		
		/* CRE32 */
		logInfo("Incluindo documentos internação. INT_SEQ = "+intNew.getSeq());
		this.getAtualizaInternacaoRN().inserirDocumentosInternacao ( // aink_int_atu.Insere_Docs_Internacao
			intNew.getSeq(),
			intNew.getConvenioSaudePlano() == null ? null : intNew.getConvenioSaudePlano().getId().getCnvCodigo(),
			intNew.getConvenioSaudePlano() == null ? null : intNew.getConvenioSaudePlano().getId().getSeq()
		);

		/* IER41, UPD20 */
		logInfo("Verificando sexo paciente. INT_SEQ = "+intNew.getSeq());
		this.getAtualizaInternacaoRN().verificarSexoPaciente( // aink_int_atu.verifica_sexo_paciente
			intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
			intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
			intNew.getPaciente().getCodigo());
		
		atualizarAtendimento(intNew, atendimento,leito,quarto,unidadeFuncional, nomeMicrocomputador, dataFimVinculoServidor);
		
		logInfo("Atualizando paciente PAC_CODIGO = "+intNew.getPaciente().getCodigo()+". INT_SEQ = "+intNew.getSeq());
		this.getValidaInternacaoRN().atualizarPaciente(intNew.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor,null);
		
		// Só permite internar paciente p projeto de pesquisa qdo saldo 
		// DO projeto > 0 ou se permite intercor internação ='S' e tiver justificativa
		logInfo("Verificando saldo do projeto. INT_SEQ = "+intNew.getSeq());
		this.getValidaInternacaoRN().verificarSaldoProjeto ( // aink_int_rn.RN_INTP_VER_SAL_PROJ
				intNew.getProjetoPesquisa(),
				intNew.getPaciente(),
				intNew
		);
	}

	private void atualizarAtendimento(AinInternacao intNew, AghAtendimentos atendimento, 
			AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional,
			String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade
				.obterCirurgiaPorDtInternacaoEOrigem(intNew.getPaciente().getCodigo(), intNew.getDthrInternacao(), DominioOrigemAtendimento.C);
		AghAtendimentos atd = null;
		if (cirurgia != null) {
			atd = cirurgia.getAtendimento();
		}
		
		if (
				intNew.getAtendimentoUrgencia() != null ||
				(atendimento != null && DominioOrigemAtendimento.N.equals(atendimento.getOrigem()))
				) {			
			// aink_int_rn.rn_intp_atu_atlz_atd
			logInfo("Atualizando atendimento de urgência da internação. INT_SEQ = "+intNew.getSeq());
			this.getValidaInternacaoRN().atualizarAtendimentoUrgenciaInternacao (
					intNew.getAtendimentoUrgencia(),
					intNew,
					intNew.getPaciente(),
					leito,
					quarto,
					unidadeFuncional,
					intNew.getEspecialidade(),
					intNew.getServidorProfessor(), 
					nomeMicrocomputador
					);
		} else if (atd != null && atd.getOrigem().equals(DominioOrigemAtendimento.C)) {
			//#44845
			AghAtendimentos atendimentoAnterior = this.getPacienteFacade().clonarAtendimento(atd);
			atd.setOrigem(DominioOrigemAtendimento.I);
			atd.setInternacao(intNew);
			atd.setUnidadeFuncional(unidadeFuncional);
			atd.setQuarto(quarto);
			atd.setEspecialidade(intNew.getEspecialidade());
			atd.setServidor(intNew.getServidorProfessor());
			atd.setServidorMovimento(servidorLogadoFacade.obterServidorLogado());
			atd.setDthrIngressoUnidade(intNew.getDthrInternacao());
			
			if(CoreUtil.isMenorDatas(intNew.getDthrInternacao(), atd.getDthrInicio())){
				atd.setDthrInicio(intNew.getDthrInternacao());
			}
			
			atd.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
			atd.setLeito(leito);

			this.getPacienteFacade().persistirAtendimento(atd,
					atendimentoAnterior, nomeMicrocomputador, dataFimVinculoServidor);
		}
		else {
			// aink_int_rn.rn_intp_atu_agh_atd
			logInfo("Incluindo atendimento da internação. INT_SEQ = "+intNew.getSeq());
			this.getValidaInternacaoRN().incluirAtendimetnoInternacao (
					intNew.getPaciente(),
					intNew.getDthrInternacao(),
					intNew,
					intNew.getDthrAltaMedica(),
					leito,
					quarto,
					unidadeFuncional,
					intNew.getEspecialidade(),
					intNew.getServidorProfessor(),
					intNew.getServidorDigita(), nomeMicrocomputador
					);
		}
	}
	
	/**
	 * ORADB: CURSOR c_doador da procedure AINP_ENFORCE_INT_RULES
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	// Teste unitário que testa este método: InternacaoEnforceTest.testDoadores
	public boolean pacienteDoador(final AipPacientes paciente) throws ApplicationBusinessException {
//		SELECT 1
//		FROM   ain_internacoes int,
//			   mbc_proc_esp_por_cirurgias ppc,
//			   mbc_cirurgias crg
//		WHERE  CRG.PAC_CODIGO = p_pac_codigo
//		AND    crg.situacao <> 'CANC'
//		AND    ppc.crg_seq = crg.seq
//		AND    ppc.ind_resp_proc = 'AGND'
//		AND    ppc.situacao = 'A'
//		AND   DECODE(PPC.EPR_PCI_SEQ+0,2228,'S','N') = 'S'
//		AND  int.pac_codigo = crg.pac_codigo
//		AND CRG.data BETWEEN TRUNC(int.dthr_internacao) AND TRUNC(NVL(int.dt_saida_paciente,SYSDATE));
		if (paciente.getMbcCirurgias() == null) {
			return false;
		}
		final Integer codProcRetiradaMultiplosOrgaos = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_PROC_RETIRADA_MULTIPLOS_ORGAOS
				).getVlrNumerico().intValueExact();
		// CRG.PAC_CODIGO = p_pac_codigo
		
		paciente.setInternacoes(new HashSet<AinInternacao>()); 
		paciente.getInternacoes().addAll(ainInternacaoDAO.listarInternacoes(paciente.getCodigo(), paciente.getProntuario()));
		
		for (final MbcCirurgias cirurgia : blocoCirurgicoFacade.listarCirurgiasPorCodigoPaciente(paciente.getCodigo())) {
			if (
				// crg.situacao <> 'CANC'
				!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {
				 // int.pac_codigo = crg.pac_codigo
				for (final AinInternacao internacao : paciente.getInternacoes()) { 
					DateUtils.truncate(internacao.getDthrInternacao(), Calendar.DATE);
					final Date d1 = DateUtils.truncate(internacao.getDthrInternacao(), Calendar.DATE);
					final Date d2 = internacao.getDtSaidaPaciente() != null ?
						DateUtils.truncate(internacao.getDtSaidaPaciente(), Calendar.DATE) :
						DateUtils.truncate(new Date(), Calendar.DATE)
					; 
					if (
						//CRG.data BETWEEN TRUNC(int.dthr_internacao) AND 
						//                 TRUNC(NVL(int.dt_saida_paciente,SYSDATE))
						cirurgia.getData().compareTo(d1) >= 0 &&
						cirurgia.getData().compareTo(d2) <= 0
					) {
						// ppc.crg_seq = crg.seq
						for (final MbcProcEspPorCirurgias pepc : cirurgia.getProcEspPorCirurgias()) {
							if (
								// ppc.ind_resp_proc = 'AGND'
								pepc.getId().getIndRespProc().equals(DominioIndRespProc.AGND) &&
								// ppc.situacao = 'A'
								pepc.getSituacao().equals(DominioSituacao.A) && 
								// DECODE(PPC.EPR_PCI_SEQ+0,2228,'S','N') = 'S'
								pepc.getId().getEprPciSeq().equals(codProcRetiradaMultiplosOrgaos) 
							) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * ORADB: procedure AINP_ENFORCE_INT_RULES (p_event = 'UPDATE')
	 * 
	 * @param intSaved
	 * @param intNew
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void enforceUpdate (
		final AinInternacao intSaved,
		final AinInternacao intNew, 
		final String nomeMicrocomputador, final Date dataFimVinculoServidor,
		final Boolean substituirProntuario
	) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final IParametroFacade parametroFacade = this.getParametroFacade();
		final IPesquisaInternacaoFacade pesquisaInternacaoFacade = this.getPesquisaInternacaoFacade();
		final IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		Short maxDiariaUti = null;
		if (intNew.getItemProcedimentoHospitalar() != null) {
			//TODO: RETIRAR ESTE REFRESH QUANDO AS EXCEPTIONS COM ROLLBACK DOS CRUDS DA PRESCRIÇÃO JÁ TIVEREM
			//SIDO REMOVIDAS.
			intNew.setItemProcedimentoHospitalar(faturamentoFacade.obterItemProcedHospitalarPorChavePrimaria(intNew.getItemProcedimentoHospitalar().getId()));
			//nao entendi o comentario acima, por algum motivo em alguns casos, o objeto vem desatachado, a consulta acima vai reatachar ele
			//e a linha abaixo vai dar um refresh no caso de alguma trigger no banco ter alterado o objeto
			faturamentoFacade.refreshItensProcedimentoHospitalar(intNew.getItemProcedimentoHospitalar());
			maxDiariaUti = intNew.getItemProcedimentoHospitalar().getMaxDiariaUti(); // max_diaria_uti
		}
				
		logInfo("Define transação. INT_SEQ = "+intNew.getSeq());
		final DominioTransacaoAltaPaciente transacao = this.getAtualizaInternacaoRN().defineTransacao (
			intSaved.getTipoAltaMedica() == null ? null : intSaved.getTipoAltaMedica().getCodigo(),  
			intNew.getTipoAltaMedica() == null ? null : intNew.getTipoAltaMedica().getCodigo(),
			intSaved.getDthrAltaMedica(),
			intNew.getDthrAltaMedica(),
			intSaved.getDtSaidaPaciente(),
			intNew.getDtSaidaPaciente()
		);
		
		logInfo("Define tipo movimento da internação. INT_SEQ = "+intNew.getSeq());
		final String tipoMovInternacaoDefinido = this.getAtualizaInternacaoRN().defineTipoMovimentoInternacao (
			intNew.getServidorProfessor(), intSaved.getServidorProfessor(),
			intNew.getLeito(), intSaved.getLeito(),
			intNew.getQuarto(),	intSaved.getQuarto(),
			intNew.getUnidadesFuncionais(),	intSaved.getUnidadesFuncionais(),
			intNew.getEspecialidade(), intSaved.getEspecialidade()
		);
		
		logInfo("Obtendo TMI. INT_SEQ = "+intNew.getSeq());
		final Integer codMvtoIntDefinido = this.getInternacaoUtilRN().obtemTmi(tipoMovInternacaoDefinido);
		
		if (transacaoEstornaAlta(transacao)) {
			logInfo("Verificando estorno de alta. INT_SEQ = "+intNew.getSeq());
			this.getValidaInternacaoRN().verificaEstornaAlta(intNew);
		}
		if (!Objects.equals(intSaved.getDtSaidaPaciente(), intNew.getDtSaidaPaciente())) {
			//aink_int_rn.rn_intp_atu_fim_atd
			logInfo("Atualizando fim do atendimento. INT_SEQ = "+intNew.getSeq());
			this.getValidaInternacaoRN().atualizarFimAtendimento (
					intNew, intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor
			);
			//TODO: Tarefa #14529 - Troca do uso do atributo mcoGestacoes para o gsoPacCodigo e gsoSeqp
			//Passando estes atributos para o método atualizaDataFim ao invés do objeto mcogestacoes 
			//possibilita o funcionamento tanto para o HCPA quanto aos HUs que ainda não possuem a perinatologia implantada.
			
			AghAtendimentos atendimento = intNew.getAtendimento();
			if(atendimento != null) {
				if (atendimento.getGsoPacCodigo() != null && intNew.getPaciente().getCodigo().equals(atendimento.getGsoPacCodigo())) {
					logInfo("Atualizando data fim do paciente. INT_SEQ = "+intNew.getSeq());
					this.getAtualizaInternacaoRN().atualizaDataFim(
						atendimento.getGsoPacCodigo(), // v_gso_pac_codigo, v_gso_seqp
						atendimento.getGsoSeqp(),
						intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor
					);
					
				} else if (possuiRecemNascido(atendimento)) {
					logInfo("Atualizando data fim do recém nascido. INT_SEQ = "+intNew.getSeq());
					this.getAtualizaInternacaoRN().atualizaDataFimRecemNascido(atendimento, intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor);				
				}
			}
		}
		
		//#24815 - Verifica se o paciente destino não vai ficar com mais de uma internação vigente (ind_paciente_internado='S')
		if (substituirProntuario){
			this.getVerificaInternacaoRN()
					.validarInternacoesSubstituirProntuario(
							intSaved.getIndPacienteInternado(),
							intNew.getPaciente().getCodigo(), 
							intNew.getSeq());
		}
		
		//#24815 - Não validar o intervalo quando a operação for SUBSTITUIR PRONTUÁRIO
		if ((!Objects.equals (intSaved.getDthrInternacao(),intNew.getDthrInternacao()) ||
			!Objects.equals (intSaved.getDthrAltaMedica(),intNew.getDthrAltaMedica()) ||
			!Objects.equals (intSaved.getPaciente(),intNew.getPaciente()))
			&& !substituirProntuario){
			/* VER13 */
			logInfo("Verificando intervalo válido da internação. INT_SEQ = "+intNew.getSeq());
			this.getVerificaInternacaoRN().verificarIntervaloValidoInternacao( // AINK_INT_VER.RN_INT_VER_SOBRE_IN2
				intNew.getSeq(),
				intNew.getPaciente().getCodigo(),
				intNew.getDthrInternacao(),
				intNew.getDthrAltaMedica()
			);
		}
		
		if (
			!Objects.equals (
				intSaved.getDthrInternacao(),
				intNew.getDthrInternacao()
			) ||
			!Objects.equals (
				intSaved.getPaciente(),
				intNew.getPaciente()
			)
		) {
			/* CRE31 */
			logInfo("Atualizando atendimentos. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().atualizaAtendimentos (
				intNew.getAtendimentoUrgencia() == null ? null : intNew.getAtendimentoUrgencia().getSeq(),
				intNew.getPaciente().getCodigo(),
				intNew.getDthrInternacao(),
				intNew
			);
		}
		
		if ( 
			!Objects.equals (
				intSaved.getLeito(),
				intNew.getLeito()
			)
		) {
			/* IER53 */
			logInfo("Incluindo extrato leito. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().insereExtratoLeito(
					intNew.getLeito(),
					intNew.getServidorDigita(),
					intNew.getServidorProfessor(),
					intNew.getDthrInternacao(),
					intNew.getDthrUltimoEvento(),
					intNew,
					true
			);
			
			/* IER43 */
			logInfo("Registrando extrato leito. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().registraExtratoLeito(
					intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID(),
					intSaved.getSeq(),
					intSaved.getDthrAltaMedica(),
					intNew.getDthrUltimoEvento(),
					transacao
			);
		}
		
		if (transacaoAlteraAlta(transacao) &&  !ObjectUtils.equals(intSaved.getDthrAltaMedica(), intNew.getDthrAltaMedica())) {
			/* UPD16 */
			logInfo("Atualizando data de lançamento do movimento de alta. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().atualizaDataLancamentoMovimentoAlta (
				intNew.getSeq(),
				intNew.getDthrAltaMedica()
			);
			
			logInfo("Atualizando data de lançamento do extrato leito. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().atualizarDataLanctoExtratoLeito(
				intNew.getSeq(),
				intNew.getDthrAltaMedica(),
				intSaved.getDthrAltaMedica()
			);
			/* Verifica se não é doação de cadaver para alterar a conta com a
			data da alta mdca - ETB 11/10/04 */
			if (!pacienteDoador(intNew.getPaciente())) { // não é doador etb 11/10/04
				logInfo("Atualizando alta da conta. INT_SEQ = "+intNew.getSeq());
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew, intNew.getDthrAltaMedica(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
		}
		
		/* Verifica se é doação de cadaver para alterar a conta com a
		data de saída ao invés da data da alta - ETB 11/10/04 */
		if (transacaoAlteraAlta(transacao) &&!ObjectUtils.equals(intSaved.getDtSaidaPaciente(), intNew.getDtSaidaPaciente())) {
			if (pacienteDoador(intNew.getPaciente())) { // é doador etb 11/10/04
				logInfo("Atualizando alta da conta para doador. INT_SEQ = "+intNew.getSeq());
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew, intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
		}
		
		if (transacaoEstornaAlta(transacao)) {
			// Verifica se o leito em que o paciente estava esta DESOCUPADO
			if (intNew.getLeito() != null) {
				logInfo("Verificando situação do leito. INT_SEQ = "+intNew.getSeq());
				this.getValidaInternacaoRN().verificaSituacaoLeito(intNew.getLeito()); // AINK_INT_RN.RN_INTP_VER_SIT_LTO
			}
			/* UPD21, DEL04 */
			logInfo("Excluindo lançamento da alta. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().excluiLctoAlta (
				intNew,
				intNew.getLeito()
			);
			
			/*  UPD19, DEL03 */
			logInfo("Excluindo registro de alta. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().excluiRegistroAlta (
				intNew, 
				intNew.getDtSaidaPaciente()
			);
			/* Verifica se é doação de cadaver para encerrar a conta com a
			data de saída ao invés da data da alta - ETB 13/10/04 */
			if (!pacienteDoador(intNew.getPaciente())) { // IF C_DOADOR%NOTFOUND THEN
				logInfo("Atualizando alta da conta com dthr da alta médica. INT_SEQ = "+intNew.getSeq());
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew, intNew.getDthrAltaMedica(), nomeMicrocomputador, dataFimVinculoServidor
				);
			} 
			else {
				logInfo("Atualizando alta da conta com dt de saída do paciente. INT_SEQ = "+intNew.getSeq());
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew, intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
			/*Teti 26-nov-2003- estorna alta de RN*/
			//TODO: Tarefa #14529 - Troca do uso do atributo mcoGestacoes para o gsoPacCodigo e gsoSeqp
			//Passando estes atributos para o método atualizaDataFim ao invés do objeto mcogestacoes 
			//possibilita o funcionamento tanto para o HCPA quanto aos HUs que ainda não possuem a perinatologia implantada.
			AghAtendimentos atendimento = intNew.getAtendimento();
			if (atendimento != null &&  
				atendimento.getGsoPacCodigo() != null && 
				intNew.getPaciente().getCodigo().equals(atendimento.getGsoPacCodigo())
			) {
				// AINK_INT_ATU.RN_INTP_ATU_EST_RN
				logInfo("Atualizando estado do atendimento. INT_SEQ = "+intNew.getSeq());
				this.getAtualizaInternacaoRN().atualizaEstadoAtendimento(
					atendimento.getGsoPacCodigo(), atendimento.getGsoSeqp(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
		}
		if (
			!Objects.equals(intSaved.getLeito(), intNew.getLeito()) ||
			!Objects.equals(intSaved.getQuarto(), intNew.getQuarto())
		) {
			logInfo("Atualizando sexo do quarto. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().atualizarSexoQuarto (
				intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID(),
				intSaved.getQuarto() == null ? null : intSaved.getQuarto().getNumero()
			);
			/* IER41, UPD20 */
			logInfo("Verificando sexo do paciente. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().verificarSexoPaciente (
				intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
				intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
				intNew.getPaciente().getCodigo()
			);
		}
		
		if (
			!Objects.equals(
				intSaved.getConvenioSaudePlano(), 
				intNew.getConvenioSaudePlano()
			) 			
		) {
			/* CRE32 */
			logInfo("Verificando sexo do paciente. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().inserirDocumentosInternacao(
				intNew.getSeq(),
				intNew.getConvenioSaudePlano().getId().getCnvCodigo(),
				intNew.getConvenioSaudePlano().getId().getSeq()
			);
			/* ATU15 */
			logInfo("Excluindo documentos da internação. INT_SEQ = "+intNew.getSeq());
			this.getAtualizaInternacaoRN().excluirDocumentosInternacao(
				intSaved.getSeq(),
				intSaved.getConvenioSaudePlano().getId().getCnvCodigo(),
				intSaved.getConvenioSaudePlano().getId().getSeq()
			);			
		}
		
		if (transacaoDeAlta(transacao)) {
			/* IER43 */
			this.getAtualizaInternacaoRN().registraExtratoLeito (
				intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
				intNew.getSeq(),
				intNew.getDthrAltaMedica(),
				intNew.getDtSaidaPaciente(),
				transacao
			);
			this.getAtualizaInternacaoRN().atualizarSexoQuarto (
				intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID(),
				intSaved.getQuarto() == null ? null : intSaved.getQuarto().getNumero()
			);
			/*IER46 */
			this.getAtualizaInternacaoRN().registrarMovimentoInternacoes (
					intNew.getSeq(),
					intNew.getServidorDigita().getId().getMatricula(),
					intNew.getServidorDigita().getId().getVinCodigo(),
					intNew.getDthrInternacao(),
					intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
					intNew.getServidorProfessor().getId().getMatricula(),
					intNew.getServidorProfessor().getId().getVinCodigo(),
					intNew.getUnidadesFuncionais() == null ? null : intNew.getUnidadesFuncionais().getSeq(),
					intNew.getEspecialidade().getSeq(),
					intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
					intNew.getDthrAltaMedica(),
					intNew.getDthrUltimoEvento(),
					codMvtoIntDefinido,
					transacao,
					intNew.getDtSaidaPaciente()
			);
				
			AghAtendimentos atendimento = intNew.getAtendimento();
			if(atendimento != null) {			
				try {
					this.getPesquisaExamesFacade().cancelarExamesNaAlta(atendimento, intNew.getTipoAltaMedica(), nomeMicrocomputador);
				}
				catch (final InactiveModuleException e) {
					logWarn(e.getMessage());
					//faz chamada nativa
					getObjetosOracleDAO().executaCancelaExamesAlta(atendimento.getSeq(), intNew.getTipoAltaMedica().getCodigo(), servidorLogado);
				}
			
				try {
					this.getBancoDeSangueFacade().cancelaAmostrasHemoterapicas(atendimento, intNew.getTipoAltaMedica(), nomeMicrocomputador);
				}
				catch (final InactiveModuleException e) {
					logWarn(e.getMessage());
					//faz chamada nativa
					getObjetosOracleDAO().executaCancelaAmostrasHemoterapicas(atendimento.getSeq(), intNew.getTipoAltaMedica().getCodigo(), servidorLogado);
				}
			}
			
			/* Verifica se é doação de cadaver para encerrar a conta com a
			data de saída ao invés da data da alta - ETB 11/10/04 */
			intNew.setPaciente(pacienteFacade.obterPaciente(intNew.getPaciente().getCodigo()));
			if (!pacienteDoador(intNew.getPaciente())) { // IF C_DOADOR%NOTFOUND
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew,	intNew.getDthrAltaMedica(), nomeMicrocomputador, dataFimVinculoServidor
				);
			} 
			else {
				this.getValidaInternacaoRN().atualizaAltaConta (
					intNew,	intNew.getDtSaidaPaciente(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
			this.getValidaInternacaoRN().atualizaAltaControleInfeccao (
				atendimento, intNew.getTipoAltaMedica(), intNew.getDthrAltaMedica()
			);
		}
		
		/* Geração dos movimentos de transferência DEVERA SER retirada da procedure
		e ser chamada na PLL do processo de transferência, para que
		seja possível a alteração dos campos da internação para correção e
	   não transferência  */
		/*if ( 
			!Objects.equals(intSaved.getLeito(), intNew.getLeito()) ||
			!Objects.equals(intSaved.getEspecialidade(),intNew.getEspecialidade()) ||
			!Objects.equals(intSaved.getServidorProfessor(),intNew.getServidorProfessor()) ||
			!Objects.equals(intSaved.getQuarto(),intNew.getQuarto()) ||
			!Objects.equals(intSaved.getUnidadesFuncionais(),intNew.getUnidadesFuncionais())
		) { */
		if (!transacaoDeAlta(transacao)) {
			if (!"'TRANSFERENCIA INEXISTENTE'".equals(tipoMovInternacaoDefinido)) {
				/* IER52,IER46 */
				// AINK_INT_ATU.RN_INT_REG_MOV_INT
				this.getAtualizaInternacaoRN().registrarMovimentoInternacoes ( 
					intNew.getSeq(),
					intNew.getServidorDigita() == null ? null : intNew.getServidorDigita().getId().getMatricula(),
					intNew.getServidorDigita() == null ? null : intNew.getServidorDigita().getId().getVinCodigo(),
					intNew.getDthrInternacao(),
					intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
					intNew.getServidorProfessor() == null ? null : intNew.getServidorProfessor().getId().getMatricula(),
					intNew.getServidorProfessor() == null ? null : intNew.getServidorProfessor().getId().getVinCodigo(),
					intNew.getUnidadesFuncionais() == null ? null : intNew.getUnidadesFuncionais().getSeq(),
					intNew.getEspecialidade() == null ? null : intNew.getEspecialidade().getSeq(),
					intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
					null, 
					intNew.getDthrUltimoEvento(), 
					codMvtoIntDefinido,
					transacao,
					null
				);
			}
		}	
		
		if (!DateUtil.isDatasIguais(intSaved.getDthrInternacao(), intNew.getDthrInternacao())) {
			/* IER52 */
			// AINK_INT_ATU.ATUALIZA_DT_LCTO_MVTO_INT
			this.getAtualizaInternacaoRN().atualizaDataInternacaoMovimentosInternacao (
				intNew.getSeq(), intNew.getDthrInternacao()
			);
			this.getAtualizaInternacaoRN().atualizarDataLanctoExtratoLeito(
				intNew.getSeq(), intNew.getDthrInternacao(),
				intSaved.getDthrInternacao()
			);
			/*
			 * Atualiza a dthr_inicio da agh_atendimentos
			 * se não for atendimento de urgência e nem 
			 * recém-nascido no hospital 
			 */
			//TODO: Tarefa #14529 - Substituição do uso do atributo mcoGestacoes no if pelo gsoPacCodigo
			AghAtendimentos atendimento = intNew.getAtendimento();
			if (
				intNew.getAtendimentoUrgencia() == null && 
				atendimento != null && atendimento.getGsoPacCodigo() == null
			) {
				// aink_int_rn.rn_intp_atu_inic_atd
				this.getValidaInternacaoRN().atualizarDataInicioAtendimentoInternacao (
						intNew.getSeq(), null,
						intNew.getDthrInternacao(), nomeMicrocomputador
				);
				// aink_int_rn.rn_intp_atu_int_cnta
				this.getValidaInternacaoRN().atualizarContaInternacao (  
						intNew.getSeq(), 
						intNew.getDthrInternacao(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
			else {
				//TODO: Tarefa #14529 - Substituição do udo do atributo mcoGestacoes no if pelo gsoPacCodigo
				if (
					intNew.getAtendimentoUrgencia() == null && 
					atendimento != null && atendimento.getGsoPacCodigo() != null
				) {
					if (intNew.getPaciente().getCodigo().equals(atendimento.getGsoPacCodigo())) {
						// aink_int_rn.rn_intp_atu_inic_atd
						this.getValidaInternacaoRN().atualizarDataInicioAtendimentoInternacao(
								intNew.getSeq(), null,
								intNew.getDthrInternacao(), nomeMicrocomputador
						);
						// aink_int_rn.rn_intp_atu_int_cnta
						this.getValidaInternacaoRN().atualizarContaInternacao (  
								intNew.getSeq(), 
								intNew.getDthrInternacao(), nomeMicrocomputador, dataFimVinculoServidor
						);
					}
				}
			}
		}
		if (!Objects.equals(intSaved.getPaciente(),intNew.getPaciente())) {
			/* IER41, UPD20 */
			this.getAtualizaInternacaoRN().verificarSexoPaciente(
				intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID(),
				intSaved.getQuarto() == null ? null : intSaved.getQuarto().getNumero(),
				intNew.getPaciente().getCodigo()
			);
			if (intNew.getAtendimentoUrgencia() == null) {
				// aink_int_rn.rn_intp_atu_pac_atd
				this.getValidaInternacaoRN().atualizarPacienteAtendimento (
						null, intNew, intNew.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario
				);
			}
		}
		/* Chamadas incluídas em função da tabela atendimentos */
		if (!Objects.equals(intSaved.getEspecialidade(), intNew.getEspecialidade())) {
			// aink_int_rn.rn_intp_atu_esp_atd
			this.getValidaInternacaoRN().atualizarAtendimentoEspecialidade (
					intNew.getSeq(), 
					intNew.getEspecialidade() == null ? null : intNew.getEspecialidade().getSeq(), 
					nomeMicrocomputador, dataFimVinculoServidor
			);
		}
		if (!Objects.equals(intSaved.getServidorProfessor(), intNew.getServidorProfessor())) {
			// aink_int_rn.rn_intp_atu_prof_atd
			this.getValidaInternacaoRN().atualizarProfessorAtendimento (
					intNew.getSeq(), 
					intNew.getServidorProfessor(), 
					nomeMicrocomputador, dataFimVinculoServidor
			);
		}
		if ( 
			!Objects.equals(intSaved.getLeito(), intNew.getLeito()) ||
			!Objects.equals(intSaved.getQuarto(),intNew.getQuarto()) ||
			!Objects.equals(intSaved.getUnidadesFuncionais(),intNew.getUnidadesFuncionais())
		) {
			// aink_int_rn.rn_intp_atu_lcal_atd
			final LeitoQuartoUnidadeFuncionalVO lqufVO = this.getValidaInternacaoRN().atualizarLocalAtendimento(
					intNew,
					intNew.getLeito(),
					intNew.getQuarto(),
					intNew.getUnidadesFuncionais(), 
					nomeMicrocomputador, dataFimVinculoServidor
			);
			// aink_int_rn.rn_intp_atu_sol_tran
			this.getValidaInternacaoRN().atualizarSolicitacaoTransferencia (
					intNew.getSeq(),
					intNew.getLeito() == null ? null : intNew.getLeito().getLeitoID(),
					intNew.getQuarto() == null ? null : intNew.getQuarto().getNumero(),
					intNew.getUnidadesFuncionais() == null ? null : intNew.getUnidadesFuncionais().getSeq()
			);
			final Integer idadePediatrica = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_IDADE_PEDIATRICA
			).getVlrNumerico().intValueExact();
			final Short unid9Sul = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_UNID_9SUL
			).getVlrNumerico().shortValueExact();
			
			final Short codUnidFuncNew = pesquisaInternacaoFacade.aincRetUnfSeq(
					lqufVO.getUnidadeFuncional() == null ? null : lqufVO.getUnidadeFuncional().getSeq(),
					lqufVO.getQuarto() == null ? null : lqufVO.getQuarto().getNumero(),
					lqufVO.getLeito() == null ? null : lqufVO.getLeito().getLeitoID()
			);
			final Short codUnidFuncSaved = pesquisaInternacaoFacade.aincRetUnfSeq(
				intSaved.getUnidadesFuncionais() == null ? null : intSaved.getUnidadesFuncionais().getSeq(),
				intSaved.getQuarto() == null ? null : intSaved.getQuarto().getNumero(),
				intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID() 
			);
			
			Integer idade = null;
			if (intNew.getAtendimento() != null) {
				idade = intNew.getAtendimento().getPaciente().getIdade(intNew.getAtendimento().getDthrInicio()); // idade
			}

			if (
				pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional(
					codUnidFuncNew,
					ConstanteAghCaractUnidFuncionais.LAUDO_ACOMP
				).equals(DominioSimNao.S) &&
				!pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional(
					codUnidFuncSaved,
					ConstanteAghCaractUnidFuncionais.LAUDO_ACOMP
				).equals(DominioSimNao.S) && (
					!codUnidFuncNew.equals(unid9Sul) || (
						codUnidFuncNew.equals(unid9Sul) &&
						idade.intValue() < idadePediatrica.intValue()
					)
				) &&
				// fatk_iph_rn.rn_iphc_ver_diaacomp
				faturamentoFacade.verificaDiariaAcompanhante (
						intNew.getItemProcedimentoHospitalar()
				)
			) {
				AghAtendimentos atendimento = intNew.getAtendimento();
				if (atendimento != null) {
					// MPMP_GERA_LAUDO_AIN
					try {
						prescricaoMedicaFacade.geraLaudoInternacao (
							atendimento, 
							intNew.getDthrUltimoEvento(),
							AghuParametrosEnum.P_LAUDO_ACOMPANHANTE,
							intNew.getConvenioSaudePlano()
						);
					}
					catch(final InactiveModuleException e) {
						logWarn(e.getMessage());
						//faz chamada nativa
						getObjetosOracleDAO().executaGeraLaudoInternacao(atendimento.getSeq(), 
								 intNew.getDthrUltimoEvento(), 
								 AghuParametrosEnum.P_LAUDO_ACOMPANHANTE.toString(), 
								 intNew.getConvenioSaudePlano().getId().getCnvCodigo(), 
								 intNew.getConvenioSaudePlano().getId().getSeq(), servidorLogado);
					}
				}
			}
			if (
				(maxDiariaUti == null || maxDiariaUti > 0) && // NVL(v_max_diaria_uti,1) > 0
				pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional (
					codUnidFuncNew,
					ConstanteAghCaractUnidFuncionais.LAUDO_CTI
				).equals(DominioSimNao.S) &&
				!pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional(
					codUnidFuncSaved,
					ConstanteAghCaractUnidFuncionais.LAUDO_CTI
				).equals(DominioSimNao.S)					
			) {
				AghAtendimentos atendimento = intNew.getAtendimento();
				if (atendimento != null) {				
					// MPMP_GERA_LAUDO_AIN
					try {
						prescricaoMedicaFacade.geraLaudoInternacao (
							atendimento, 
							intNew.getDthrUltimoEvento(),
							AghuParametrosEnum.P_LAUDO_UTI,
							intNew.getConvenioSaudePlano()
						);
					}
					catch (final InactiveModuleException e) {
						logWarn(e.getMessage());
						//faz chamada nativa
						getObjetosOracleDAO().executaGeraLaudoInternacao(atendimento.getSeq(), 
																		 intNew.getDthrUltimoEvento(), 
																		 AghuParametrosEnum.P_LAUDO_UTI.toString(), 
																		 intNew.getConvenioSaudePlano().getId().getCnvCodigo(), 
																		 intNew.getConvenioSaudePlano().getId().getSeq(), servidorLogado);
					}
				}
			}
			//TODO: Tarefa #14529 - Troca do uso do atributo mcoGestacoes para o gsoPacCodigo e gsoSeqp
		//Passando estes atributos para o método atualizaInformacoesLeito ao invés do objeto mcogestacoes 
		//possibilita o funcionamento tanto para o HCPA quanto aos HUs que ainda não possuem a perinatologia implantada.
			AghAtendimentos atendimento = intNew.getAtendimento();
			if (atendimento != null && atendimento.getGsoPacCodigo() != null && intNew.getPaciente().getCodigo().equals(atendimento.getGsoPacCodigo())) {
				this.getAtualizaInternacaoRN().atualizaInformacoesLeito (
					atendimento.getGsoPacCodigo(),
					atendimento.getGsoSeqp(),
					lqufVO.getLeito(),
					lqufVO.getQuarto(),
					lqufVO.getUnidadeFuncional(), nomeMicrocomputador, dataFimVinculoServidor
				);
			}
		}
		if (!Objects.equals(intSaved.getPaciente(),intNew.getPaciente())) {
			AghAtendimentos atendimento = intNew.getAtendimento();
			if (atendimento != null) {
			// aipp_subs_pront_atd
				try {
					this.getSubstituirProntuarioAtendimentoRN().substituirProntuarioAtendimento(
							intSaved.getPaciente(),
							intNew.getPaciente(),
							atendimento, nomeMicrocomputador,servidorLogado ,dataFimVinculoServidor
					);
				}
				catch (InactiveModuleException e) {
					getObjetosOracleDAO().executaSubstituirProntuarioAtendimento(intSaved.getPaciente().getCodigo(), intNew.getPaciente().getCodigo(), atendimento.getSeq(), servidorLogado);
				}
			}
		}
		if (
			!Objects.equals(intSaved.getPaciente(),intNew.getPaciente()) ||
			!Objects.equals(intSaved.getDthrInternacao(),intNew.getDthrInternacao()) ||
			!Objects.equals(intSaved.getDthrAltaMedica(),intNew.getDthrAltaMedica()) ||
			!Objects.equals(intSaved.getLeito(), intNew.getLeito()) ||
			!Objects.equals(intSaved.getQuarto(),intNew.getQuarto()) ||
			!Objects.equals(intSaved.getUnidadesFuncionais(),intNew.getUnidadesFuncionais())
		) {
			if (!Objects.equals(intSaved.getPaciente(),intNew.getPaciente())) {
				this.getValidaInternacaoRN().atualizarPaciente (
					intNew.getPaciente(),
					AtualizarPacienteTipo.PAC, nomeMicrocomputador, dataFimVinculoServidor, transacao);
			} else {
				this.getValidaInternacaoRN().atualizarPaciente (
					intNew.getPaciente(),
					AtualizarPacienteTipo.INT, nomeMicrocomputador, dataFimVinculoServidor, transacao);
			}
		}
		if (!Objects.equals(intSaved.getPaciente(),intNew.getPaciente())) {
			// chamada para ajustar datas/local da internacao do paciente
			this.getValidaInternacaoRN().atualizarPaciente(intSaved.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, transacao);
		}
		// Só permite internar paciente p projeto de pesquisa qdo saldo 
		// do projeto > 0 ou se permite internação ='S' e tiver justificativa
		if (!Objects.equals(intSaved.getProjetoPesquisa(),intNew.getProjetoPesquisa())) {
			this.getValidaInternacaoRN().verificarSaldoProjeto ( // aink_int_rn.RN_INTP_VER_SAL_PROJ
				intNew.getProjetoPesquisa(), 
				intNew.getPaciente(), 
				intNew
			);
		}
		
	}
	
	/**
	 * ORADB: procedure AINP_ENFORCE_INT_RULES (p_event = 'DELETE')
	 * 
	 * @param intSaved
	 * @throws ApplicationBusinessException
	 */
	public void enforceDelete(
		final AinInternacao intSaved, String nomeMicrocomputador, final Date dataFimVinculoServidor
	) throws ApplicationBusinessException {
		// aink_int_rn.rn_intp_atu_paciente
		this.getValidaInternacaoRN().atualizarPaciente(intSaved.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, null);
		
		/* IER41,UPD20*/
		this.getAtualizaInternacaoRN().verificarSexoPaciente (
			intSaved.getLeito() == null ? null : intSaved.getLeito().getLeitoID(),
			intSaved.getQuarto() == null ? null : intSaved.getQuarto().getNumero(),
			intSaved.getPaciente().getCodigo()
		);
	}

	private List<AinSolicTransfPacientes> carregaSolicitacoesTransfereciaPaciente(final Integer seq,
			final DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoPendente,
			final DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoAtendida) {
		return this.getAinSolicTransfPacientesDAO().carregaSolicitacoesTransfereciaPaciente(seq, indSituacaoSolicitacaoPendente,
				indSituacaoSolicitacaoAtendida);
	}

	/**
	 * ORADB: AINT_STP_ARU, AINT_STP_ASU, AINT_STP_BSU, AINT_STP_BRU Triggers de
	 * update da tabela AIN_SOLIC_TRANSF_PACIENTES.
	 * 
	 * @param ainSolicTransfPacientes
	 *  
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizarSolicitacoesTransferenciaPacientes(
			final AinSolicTransfPacientes ainSolicTransfPacientes,
			final AinSolicTransfPacientes ainSolicTransfPacientesOriginal)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Atualiza a Data e Hora do atendimento da solicitação
		if (DominioSituacaoSolicitacaoInternacao.C.equals(ainSolicTransfPacientes.getIndSitSolicLeito())) {
			ainSolicTransfPacientes.setDthrAtendimentoSolicitacao(new Date());
		}

		boolean atualizaDataHora = false;
		if (ainSolicTransfPacientesOriginal.getLeito() == null
				&& ainSolicTransfPacientes.getLeito() != null) {
			atualizaDataHora = true;
		}
		if (!atualizaDataHora
				&& (ainSolicTransfPacientesOriginal.getAinQuartos() == null && ainSolicTransfPacientes
						.getAinQuartos() != null)) {
			atualizaDataHora = true;
		}
		if (!atualizaDataHora
				&& (ainSolicTransfPacientesOriginal.getUnidadeFuncional() == null && ainSolicTransfPacientes
						.getUnidadeFuncional() != null)) {
			atualizaDataHora = true;
		}

		if (atualizaDataHora) {
			ainSolicTransfPacientes.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.valueOf("A"));
			ainSolicTransfPacientes.setDthrAtendimentoSolicitacao(new Date());
		}

		
		// Consiste troca das situações da solicitação de transferência
		if (DominioSituacaoSolicitacaoInternacao.A.equals(ainSolicTransfPacientesOriginal
				.getIndSitSolicLeito())
				&& "P".equals(ainSolicTransfPacientes.getIndSitSolicLeito())) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00686);
		}

		if (DominioSituacaoSolicitacaoInternacao.E.equals(ainSolicTransfPacientesOriginal
				.getIndSitSolicLeito())
				&& DominioSituacaoSolicitacaoInternacao.P.equals(ainSolicTransfPacientes
						.getIndSitSolicLeito())) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00688);
		}

		// Atualizar servidor responsável pelo cancelamento da solicitação
		if (DominioSituacaoSolicitacaoInternacao.C.equals(ainSolicTransfPacientes.getIndSitSolicLeito())
				&& ainSolicTransfPacientes.getServidorCancelador() == null) {
			if (servidorLogado != null) {
				ainSolicTransfPacientes.setServidorCancelador(servidorLogado);
			} else {
				throw new ApplicationBusinessException(
						InternacaoRNExceptionCode.RAP_00175);
			}
		}

		// Não permite atualização de leito,quarto, unid, após atribuição
		boolean lancarAin00722 = false;
		if (ainSolicTransfPacientesOriginal.getLeito() != null
				&& ainSolicTransfPacientes.getLeito() != null
				&& !ainSolicTransfPacientesOriginal.getLeito().equals(ainSolicTransfPacientes
						.getLeito())) {
			lancarAin00722 = true;
		}
		if (!lancarAin00722
				&& (ainSolicTransfPacientesOriginal.getAinQuartos() != null
						&& ainSolicTransfPacientes.getAinQuartos() != null && !ainSolicTransfPacientesOriginal
						.getAinQuartos().equals(ainSolicTransfPacientes
						.getAinQuartos()))) {
			lancarAin00722 = true;
		}
		if (!lancarAin00722
				&& (ainSolicTransfPacientesOriginal.getUnidadeFuncional() != null
						&& ainSolicTransfPacientes.getUnidadeFuncional() != null && !ainSolicTransfPacientesOriginal
						.getUnidadeFuncional()
						.equals(ainSolicTransfPacientes.getUnidadeFuncional()))) {
			lancarAin00722 = true;
		}

		if (lancarAin00722) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00722);
		}

		insereRegistroReservaLeito(ainSolicTransfPacientes,
				ainSolicTransfPacientesOriginal);
	}

	/**
	 * Insere automaticamente registro de reserva para o leito atribuido.
	 * IMPORTANTE: Utilizar apenas em operações de update!
	 * 
	 * ORADB AINP_ENFORCE_STP_RULES
	 * 
	 * @param ainSolicTransfPacientes
	 * @param ainSolicTransfPacientesOriginal
	 * @throws ApplicationBusinessException
	 */
	public void insereRegistroReservaLeito(
			final AinSolicTransfPacientes ainSolicTransfPacientes,
			final AinSolicTransfPacientes ainSolicTransfPacientesOriginal)
			throws ApplicationBusinessException {
		final IParametroFacade parametroFacade = this.getParametroFacade();
		final ILeitosInternacaoFacade leitosInternacaoFacade = this.getLeitosInternacaoFacade();
		final AinLeitosDAO ainLeitosDAO = this.getAinLeitosDAO();
		final AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO = this.getAinTiposMovimentoLeitoDAO();
		
		AghParametros aghParametros;
		if (ainSolicTransfPacientesOriginal.getLeito() == null
				&& ainSolicTransfPacientes.getLeito() != null) {
			ainSolicTransfPacientes.getInternacao()
					.getSeq();

			aghParametros = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_JUSTIF_RESERVA_LEITO_SOLIC_TRANSF);

			if (aghParametros == null || aghParametros.getVlrTexto() == null) {
				throw new ApplicationBusinessException(
						InternacaoRNExceptionCode.AIN_00716);
			}

			final String justificativaReservaLeito = aghParametros.getVlrTexto();

			aghParametros = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_RESERVADO);

			if (aghParametros == null || aghParametros.getVlrNumerico() == null) {
				throw new ApplicationBusinessException(
						InternacaoRNExceptionCode.AIN_00342);
			}
			final Short codigoMvtoLeitoReservado = aghParametros.getVlrNumerico()
					.shortValue();
			
			// Verifica qual quarto pertence o leito
			final AinLeitos leito = ainLeitosDAO.obterPorChavePrimaria(ainSolicTransfPacientes.getLeito());
			
			// Insere registro de reserva
			leitosInternacaoFacade.inserirExtrato(leito, ainTiposMovimentoLeitoDAO.obterPorChavePrimaria(codigoMvtoLeitoReservado),
					null, null, justificativaReservaLeito, new Date(), null, ainSolicTransfPacientes.getInternacao().getPaciente(),
					null, null, null, null);
			
			if (leito == null) {
				throw new ApplicationBusinessException(
						InternacaoRNExceptionCode.AIN_00718);
			}

			if (leito.getQuarto() == null) {
				throw new ApplicationBusinessException(
						InternacaoRNExceptionCode.AIN_00719);
			}

			ainLeitosDAO.flush();
			
			// Verifica o sexo de ocupação do quarto e se consiste
			if (DominioSimNao.S.equals(leito.getQuarto().getIndConsSexo())
					&& leito.getQuarto().getSexoOcupacao() == null) {
				if (ainSolicTransfPacientes.getInternacao() == null
						|| ainSolicTransfPacientes.getInternacao()
								.getPaciente() == null
						|| ainSolicTransfPacientes.getInternacao()
								.getPaciente().getSexo() == null) {
					throw new ApplicationBusinessException(
							InternacaoRNExceptionCode.AIN_00720);
				}

				final DominioSexo sexo = ainSolicTransfPacientes.getInternacao()
						.getPaciente().getSexo();
				
				// Atualiza o sexo de ocupação do quarto com sexo do paciente
				try {
					leito.getQuarto().setSexoOcupacao(sexo);
					ainLeitosDAO.flush();
				} catch(final Exception e) {
					logError("Exceção capturada: ", e);
					throw new ApplicationBusinessException(
							InternacaoRNExceptionCode.AIN_00721);
				}
			}
		}
	}

	/**
	 * Método para verificar se o paciente a ser internado tem prontuario para
	 * ser internado.
	 * 
	 * ORADB: Procedure AINP_INT_VER_PRONT
	 * 
	 * @param codigoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarExistenciaProntuarioParaInternarPaciente(
			final Integer codigoPaciente) throws ApplicationBusinessException {
		final AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);

		if (paciente == null
				|| !DominioSimNao.S.equals(paciente.getIndGeraProntuario())) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00738);
		}
	}

	/**
	 * ORADB Procedure AINP_INT_ATU_SOL_ESTORNO.
	 * <br><br><b>
	 * Procedure parcialmente convertida. Vide comentários no método...
	 * </b><br><br>
	 * 
	 * Atualizar a situação da solicitação de transferência quando do estorno da
	 * alta do paciente.<br> Torna pendente ou atendida a solicitação cancelada com
	 * data/hora igual a da alta solicitaçãoes de transferência
	 * 
	 * @author Marcelo Tocchetto
	 * @throws ApplicationBusinessException
	 */
	public void ainpIntAtuSolEstorno(final Integer seqInternacao, final Date dthrAltaMedica) throws ApplicationBusinessException {
		try {
			final InternacaoUtilRN internacaoUtilRN = this.getInternacaoUtilRN();
			final AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();

			String indSituacaoSolicAtendida = null;
			String indSituacaoSolicPendente = null;
			String indSituacaoSolicCancelada = null;
			indSituacaoSolicAtendida = internacaoUtilRN.pegaParametro("P_IND_SIT_SOLIC_ATENDIDA", indSituacaoSolicAtendida,
					InternacaoUtilRNExceptionCode.AIN_00659);
			indSituacaoSolicPendente = internacaoUtilRN.pegaParametro("P_IND_SIT_SOLIC_PENDENTE", indSituacaoSolicPendente,
					InternacaoUtilRNExceptionCode.AIN_00660);
			indSituacaoSolicCancelada = internacaoUtilRN.pegaParametro("P_IND_SIT_SOLIC_CANCELADA", indSituacaoSolicCancelada,
					InternacaoUtilRNExceptionCode.AIN_00739);

			final String observacao = "Atualizada devido a estorno de alta";
			List<AinSolicTransfPacientes> listaSolicitacoesTransferencia = ainSolicTransfPacientesDAO
					.listarSolicTransfPacientesComLeitoQuartoEUnidadeFuncionalNulos(seqInternacao, dthrAltaMedica,
							DominioSituacaoSolicitacaoInternacao.valueOf(indSituacaoSolicCancelada));
			for (final AinSolicTransfPacientes ainSolicTransfPacientes : listaSolicitacoesTransferencia) {
				final AinSolicTransfPacientes ainSolicTransfPacientesOriginal = ainSolicTransfPacientesDAO
						.obterPorChavePrimaria(ainSolicTransfPacientes.getSeq());
				ainSolicTransfPacientesDAO.desatachar(ainSolicTransfPacientesOriginal);

				ainSolicTransfPacientes.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.valueOf(indSituacaoSolicPendente));
				ainSolicTransfPacientes.setObservacao(observacao);

				atualizarSolicitacoesTransferenciaPacientes(ainSolicTransfPacientes, ainSolicTransfPacientesOriginal);
			}

			ainSolicTransfPacientesDAO.flush();
			listaSolicitacoesTransferencia.clear();

			listaSolicitacoesTransferencia = ainSolicTransfPacientesDAO.listarSolicTransfPacientesComLeitoQuartoOuUnidadeFuncionalNotNull(
					seqInternacao, dthrAltaMedica, DominioSituacaoSolicitacaoInternacao.valueOf(indSituacaoSolicCancelada));
			for (final AinSolicTransfPacientes ainSolicTransfPacientes : listaSolicitacoesTransferencia) {
				final AinSolicTransfPacientes ainSolicTransfPacientesOriginal = ainSolicTransfPacientesDAO
						.obterPorChavePrimaria(ainSolicTransfPacientes.getSeq());
				ainSolicTransfPacientesDAO.desatachar(ainSolicTransfPacientesOriginal);

				ainSolicTransfPacientes.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.valueOf(indSituacaoSolicAtendida));
				ainSolicTransfPacientes.setObservacao(observacao);

				atualizarSolicitacoesTransferenciaPacientes(ainSolicTransfPacientes, ainSolicTransfPacientesOriginal);
			}

			ainSolicTransfPacientesDAO.flush();
			listaSolicitacoesTransferencia.clear();
		} catch (final RuntimeException e) {
			tratarException(e, null);
		}
	}

	private void tratarException(final Throwable e, final Throwable eAnterior) throws ApplicationBusinessException {
		final String msg = e.getMessage();
		if (msg.indexOf("AIN-00391") >= 0) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00391);
		} else {
			final Throwable en = e.getCause();
			if (en != null && (eAnterior == null || !en.equals(eAnterior))) {
				tratarException(en, e);
			}
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.ERRO_NEGOCIO_ORA, msg);
		}
	}

	/**
	 * ORADB AINT_ATU_BRU
	 * 
	 * @param atendimentoUrgencia
	 * @param usuarioLogado 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validarAtendimentoUrgenciaAntesAtualizar(
			final AinAtendimentosUrgencia atendimentoUrgencia, final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, String usuarioLogado) throws ApplicationBusinessException {
		// Verifica retroatividade permitida
		if (this.getInternacaoUtilRN().modificados (atendimentoUrgenciaAntigo.getDtAtendimento(), atendimentoUrgencia.getDtAtendimento())) {
			this.getVerificaInternacaoRN().verificarDataInternacaoDiasVolta(atendimentoUrgencia.getDtAtendimento());
		}
		
		if (atendimentoUrgencia.getDtAltaAtendimento() != null && atendimentoUrgenciaAntigo.getDtAltaAtendimento() == null) {
			// Atualização  da alta
			atendimentoUrgencia.setIndPacienteEmAtendimento(false);
		} else if (atendimentoUrgencia.getDtAltaAtendimento() == null && atendimentoUrgenciaAntigo.getDtAltaAtendimento() != null) {
			// Estorno  da alta
			atendimentoUrgencia.setIndPacienteEmAtendimento(true);
		}
		
		final boolean leitoInformado = atendimentoUrgencia.getLeito() != null;
		final boolean quartoInformado = atendimentoUrgencia.getQuarto() != null;
		final boolean unidadeFuncionalInformada = atendimentoUrgencia.getUnidadeFuncional() != null;
		
		// Atualiza indicador de localização do paciente
		if (leitoInformado) {
			atendimentoUrgencia.setIndLocalPaciente(DominioLocalPaciente.L);
		} else if (quartoInformado) {
			atendimentoUrgencia.setIndLocalPaciente(DominioLocalPaciente.Q);
		} else if (unidadeFuncionalInformada) {
			atendimentoUrgencia.setIndLocalPaciente(DominioLocalPaciente.U);
		}
		
		final Short seqUnidadeFuncional = unidadeFuncionalInformada ? atendimentoUrgencia.getUnidadeFuncional().getSeq() : null;
		final Short numeroQuarto = quartoInformado ? atendimentoUrgencia.getQuarto().getNumero() : null;
		final String leitoID = leitoInformado ? atendimentoUrgencia.getLeito().getLeitoID() : null;
		
		getAtendimentoUrgenciaRN().validarLocalInternacao(atendimentoUrgencia
				.getLeito(), atendimentoUrgencia.getQuarto(),
				atendimentoUrgencia.getUnidadeFuncional());
		
		// Chama regra q atualiza a triagem do ambulatório
		Short seqUnidadeFuncionalAntigo = null;
		if (atendimentoUrgenciaAntigo.getUnidadeFuncional() != null) {
			seqUnidadeFuncionalAntigo = atendimentoUrgenciaAntigo.getUnidadeFuncional().getSeq();
		}
		Short numeroQuartoAntigo = null;
		if (atendimentoUrgenciaAntigo.getQuarto() != null) {
			numeroQuartoAntigo = atendimentoUrgenciaAntigo.getQuarto().getNumero();
		}
		String leitoIDAntigo = null;
		if (atendimentoUrgenciaAntigo.getLeito() != null) {
			leitoIDAntigo = atendimentoUrgenciaAntigo.getLeito().getLeitoID();
		}
		final Integer seqAtendimentoUrgencia = atendimentoUrgencia.getSeq();
		final Integer codigoPaciente = atendimentoUrgencia.getPaciente().getCodigo();
		String codigoTipoAltaMedicaAntigo = null;
		if (atendimentoUrgenciaAntigo.getTipoAltaMedica() != null) {
			codigoTipoAltaMedicaAntigo = atendimentoUrgenciaAntigo.getTipoAltaMedica().getCodigo();
		}
		String codigoTipoAltaMedica = null; 
		if (atendimentoUrgencia.getTipoAltaMedica() != null) {
			codigoTipoAltaMedica = atendimentoUrgencia.getTipoAltaMedica().getCodigo();
		}
		final boolean indPacienteEmAtendimentoAntigo = atendimentoUrgenciaAntigo.getIndPacienteEmAtendimento();
		final boolean indPacienteEmAtendimento = atendimentoUrgencia.getIndPacienteEmAtendimento();
		
		this.gerarCheckOutAtu(seqUnidadeFuncional, numeroQuarto, leitoID,
				seqUnidadeFuncionalAntigo, numeroQuartoAntigo, leitoIDAntigo,
				seqAtendimentoUrgencia, codigoPaciente,
				codigoTipoAltaMedicaAntigo, codigoTipoAltaMedica,
				indPacienteEmAtendimentoAntigo, indPacienteEmAtendimento, usuarioLogado);
	}
	
	/**
	 * ORADB Procedure MAMP_CHECK_OUT_ATU
	 * @param usuarioLogado 
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void gerarCheckOutAtu(final Short seqUnidadeFuncional,
			final Short numeroQuarto, final String leitoID,
			final Short seqUnidadeFuncionalAntigo,
			final Short numeroQuartoAntigo, final String leitoIDAntigo,
			final Integer seqAtendimentoUrgencia, final Integer codigoPaciente,
			final String codigoTipoAltaMedicaAntigo,
			final String codigoTipoAltaMedica,
			final boolean indPacienteEmAtendimentoAntigo,
			final boolean indPacienteEmAtendimento, String usuarioLogado)
			throws ApplicationBusinessException {

		if (!ainInternacaoJnDAO.isOracle()) {
			return;
		}

		final IPesquisaInternacaoFacade pesquisaInternacaoFacade = this
				.getPesquisaInternacaoFacade();

		final Short codTipoAltaMedicaAntigo = pesquisaInternacaoFacade
				.aincRetUnfSeq(seqUnidadeFuncionalAntigo, numeroQuartoAntigo,
						leitoIDAntigo);
		final Short codTipoAltaMedica = pesquisaInternacaoFacade.aincRetUnfSeq(
				seqUnidadeFuncional, numeroQuarto, leitoID);

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.ainAcompanhantesInternacaoDAO.gerarCheckoutAtu(
				seqUnidadeFuncional, numeroQuarto, leitoID,
				seqUnidadeFuncionalAntigo, numeroQuartoAntigo, leitoIDAntigo,
				seqAtendimentoUrgencia, codigoPaciente,
				codigoTipoAltaMedicaAntigo, codigoTipoAltaMedica,
				indPacienteEmAtendimentoAntigo, indPacienteEmAtendimento,
				servidorLogado.getUsuario(), codTipoAltaMedicaAntigo, codTipoAltaMedica);

	}
	
	/**
	 * ORADB AINT_ATU_ARU
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @throws ApplicationBusinessException
	 */
	/*@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void validarAtendimentoUrgenciaAposAtualizar(
			final AinAtendimentosUrgencia atendimentoUrgencia, final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) throws ApplicationBusinessException {
		return;
	}*/
	
	/**
	 * ORADB AINP_ENFORCE_ATU_RULES
	 * 
	 * @param atendimentoUrgencia 
	 * @param atendimentoUrgenciaAntigo 
	 * @param tipoOperacao 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void processarEnforceAtendimentoUrgencia(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo,
			final TipoOperacaoEnum tipoOperacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		consisteSobreposicaoData(atendimentoUrgencia.getSeq(),
				atendimentoUrgencia.getPaciente(), atendimentoUrgencia
						.getDtAtendimento(), atendimentoUrgencia
						.getDtAltaAtendimento());
		
		if (TipoOperacaoEnum.UPDATE.equals(tipoOperacao)) {
			executaRotinasAtualizacao(atendimentoUrgencia, atendimentoUrgenciaAntigo, nomeMicrocomputador, dataFimVinculoServidor);
		} else  if (TipoOperacaoEnum.INSERT.equals(tipoOperacao)) {
			executaRotinasInsercao(atendimentoUrgencia, atendimentoUrgenciaAntigo);
		} else if (TipoOperacaoEnum.DELETE.equals(tipoOperacao)) {
			this.getValidaInternacaoRN().atualizarPaciente(atendimentoUrgenciaAntigo.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, null);
		}		

	}
	
	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.EXECUTA_ROTINAS_INSERCAO
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @throws BaseException 
	 *  
	 */
	public void executaRotinasInsercao(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) throws BaseException {
		if (atendimentoUrgencia.getLeito()!= null) {
			ocupaLeito(atendimentoUrgencia, atendimentoUrgenciaAntigo);
		}
		
		final Integer codigoMovimentacao = obtemCodigoMovimento(AghuParametrosEnum.P_COD_MVTO_INT_INGR_SO.toString());
		geraMovimentoAtendimento(atendimentoUrgencia, codigoMovimentacao, atendimentoUrgencia.getDtAtendimento());
		gerarConvenioPaciente(atendimentoUrgencia, atendimentoUrgenciaAntigo);
		geraAtendimento(atendimentoUrgencia, atendimentoUrgenciaAntigo);

		getAtendimentoUrgenciaRN().atualizarUltimaInternacaoPaciente(
				atendimentoUrgencia.getPaciente(), atendimentoUrgencia
						.getDtAtendimento(), atendimentoUrgencia.getLeito(),
				atendimentoUrgencia.getQuarto(), atendimentoUrgencia
						.getUnidadeFuncional());
	}
	
	/**
	 * RN06 #26857
	 * ORADB AINP_ENFORCE_ATU_RULES.GERA_ATENDIMENTO
	 * 
	 * @ORADB AINP_ENFORCE_ATU_RULES.GERA_ATENDIMENTO e aink_atu_rn.rn_atup_atu_atlz_atd
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @throws BaseException 
	 *  
	 */
	private void geraAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AghAtendimentos atendimentos = aghAtendimentoDAO.obterAtendimentoPorNumeroConsulta(atendimentoUrgencia.getConsulta().getNumero());
		
		AghAtendimentos atendimentoOriginal = aghAtendimentoDAO.obterOriginal(atendimentos.getSeq());
		
		Integer clcCodigo = null;
		if(atendimentoUrgencia.getEspecialidade().getSeq() != null ){
			AghEspecialidades especialidades = aghEspecialidadesDAO.buscarEspecialidadesPorSeq(atendimentoUrgencia.getEspecialidade().getSeq());
			clcCodigo = especialidades.getClinica().getCodigo();
		}
		else{
			clcCodigo = atendimentoUrgencia.getClinica().getCodigo();
		}
		
		Integer prontuario = aipPacientesDAO.obterProntuarioPorPacCodigo(atendimentoUrgencia.getPaciente().getCodigo());
		
		if(prontuario == null){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIP_00013);
		}
	
		Integer codPediatria = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_CLINICA_PEDIATRIA);
		
		Boolean isPediatrico = Boolean.FALSE;
		if(clcCodigo.equals(codPediatria)){
			isPediatrico = Boolean.TRUE;
		}
		
		atendimentos.setIndPacPediatrico(isPediatrico);
		
		atualizaAtendimento(atendimentoUrgencia, atendimentos, prontuario);
		
		defineLeitoQuartoUnidadeFuncional(atendimentos, atendimentoUrgencia);
		
		pacienteFacade.atualizarAtendimento(atendimentos, atendimentoOriginal, null, servidorLogado, new Date());
		
	}
	private void atualizaAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia, AghAtendimentos atendimentos, Integer prontuario) {
		atendimentos.setPaciente(atendimentoUrgencia.getPaciente());
		atendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		atendimentos.setEspecialidade(atendimentoUrgencia.getEspecialidade());
		atendimentos.setIndPacAtendimento(DominioPacAtendimento.S);
		atendimentos.setDthrFim(null);
		atendimentos.setProntuario(prontuario);
		atendimentos.setDthrIngressoUnidade(atendimentoUrgencia.getDtAtendimento());
		atendimentos.setDthrInicio(atendimentoUrgencia.getDtAtendimento());
	}
	
	private void defineLeitoQuartoUnidadeFuncional(AghAtendimentos atendimentos, final AinAtendimentosUrgencia atendimentoUrgencia) {
		if(atendimentoUrgencia.getLeito() != null){
			atendimentos.setUnidadeFuncional(atendimentoUrgencia.getLeito().getUnidadeFuncional());
			atendimentos.setQuarto(atendimentoUrgencia.getLeito().getQuarto());
			atendimentos.setLeito(atendimentoUrgencia.getLeito());
		}
		
		else if (atendimentoUrgencia.getQuarto() != null){
			atendimentos.setQuarto(atendimentoUrgencia.getQuarto());
			atendimentos.setLeito(atendimentoUrgencia.getLeito());
			atendimentos.setUnidadeFuncional(atendimentoUrgencia.getQuarto().getUnidadeFuncional());
		}
		
		else{
			atendimentos.setQuarto(atendimentoUrgencia.getQuarto());
			atendimentos.setLeito(atendimentoUrgencia.getLeito());
			atendimentos.setUnidadeFuncional(atendimentoUrgencia.getQuarto().getUnidadeFuncional());
		}
	}

	/**
	 * Método para implementar as chamadas de triggers do Oracle na inserção da
	 * internação.
	 * 
	 * @param internacao
	 * @throws BaseException 
	 */
	public void inserirInternacao(final AinInternacao internacao,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, Boolean substituirProntuario) throws BaseException {
		if (internacao != null && internacao.getLeito() != null) {
			final AinLeitos leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(internacao.getLeito().getLeitoID());
			this.getAinLeitosDAO().refresh(leito);
			internacao.setLeito(leito);
		}

		// Chamada de trigger "before each row"
		logInfo("Realizando validações pré inclusão. INT_SEQ = "+internacao.getSeq());
		this.validarInternacaoAntesInserir(internacao);

		// Chamada de trigger "after each row"
		logInfo("Realizando validações pós inclusão. INT_SEQ = "+internacao.getSeq());
		this.validarInternacaoAposInserir(internacao, substituirProntuario);
		
		// Comando para inserir a internacao no contexto de persistência do
		// hibernate, já que no método enforceInsert() são salvos objetos que se
		// relacionam com a internação e precisam executar flush().
		ainInternacaoDAO.persistir(internacao);

		// Chamada de trigger "after statement" (enforce)
		logInfo("Realizando chamada das regras que estavam na procedure de ENFORCE. INT_SEQ = "+internacao.getSeq());
		this.enforceInsert(internacao, nomeMicrocomputador, dataFimVinculoServidor);
		
		logInfo("Finalizando inclusão da internação. INT_SEQ = "+internacao.getSeq());
	}

	/**
	 * Método para implementar regras da trigger executada após a inserção de
	 * internação.
	 * 
	 * ORADB Trigger AINT_INT_ARI
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void validarInternacaoAposInserir(final AinInternacao internacao, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		
		final Integer pacCodigo = internacao.getPaciente().getCodigo();

		this.getAtualizaInternacaoRN().verificarSolicitacaoInternacao(pacCodigo, substituirProntuario);

		Short codigoConvenio = null;
		Byte seqConvenio = null;
		if (internacao.getConvenioSaudePlano() != null) {
			codigoConvenio = internacao.getConvenioSaudePlano().getId()
					.getCnvCodigo();
			seqConvenio = internacao.getConvenioSaudePlano().getId().getSeq();
		}
		this.getAtualizaInternacaoRN().insereConvenios(pacCodigo,
				codigoConvenio, seqConvenio);

		this.getAtualizaInternacaoRN().atualizaSolicitacoesPendentes(pacCodigo, substituirProntuario);

		final Integer numeroPacientes = 1;
		this.getAtualizaInternacaoRN().atualizaProfissionaisEspecialidades(internacao
				.getEspecialidade(), internacao.getServidorProfessor(),
				numeroPacientes);
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * internação.
	 * 
	 * ORADB Trigger AINT_INT_BRI
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void validarInternacaoAntesInserir(final AinInternacao internacao)
			throws ApplicationBusinessException {
		final IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		final IPesquisaInternacaoFacade pesquisaInternacaoFacade = this.getPesquisaInternacaoFacade();
		
		final Integer pacCodigo = internacao.getPaciente().getCodigo();

		// aink_int_rn.rn_intp_ver_prnt
		logInfo("Validando verificações do paciente a ser internado. INT_SEQ = "+internacao.getSeq());
		this.getValidaInternacaoRN().verificarPacienteInternacao(pacCodigo);

		// aink_int_ver.rn_int_ver_obito_pac
		logInfo("Validando verificações de óbito do paciente. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarObitoPaciente(pacCodigo, internacao
				.getDthrInternacao(), DominioOperacoesJournal.INS);

		// aink_int_atu.atualiza_ind_internacao
		final String codigoAltaMedica = internacao.getTipoAltaMedica() == null ? null
				: internacao.getTipoAltaMedica().getCodigo();
		logInfo("Atualizando indicadores da internação. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		AtualizarIndicadoresInternacaoVO atInternacaoVO = this.getAtualizaInternacaoRN().atualizarIndicadoresInternacao(internacao.getDthrAltaMedica(), codigoAltaMedica, internacao.getIndSaidaPac());	
		internacao.setIndPacienteInternado(atInternacaoVO.getIndPacienteInternado());
		internacao.setIndSaidaPac(atInternacaoVO.getIndSaidaPaciente());
		

		// aink_int_atu.atualiza_eventos
		logInfo("Atualizando eventos. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		AtualizarEventosVO atualizarEventosVO = this.getAtualizaInternacaoRN().atualizarEventos(internacao.getDthrPrimeiroEvento(), internacao.getDthrUltimoEvento());
		internacao.setDthrPrimeiroEvento(atualizarEventosVO.getDthrPrimeiroEvento()); 
		internacao.setDthrUltimoEvento(atualizarEventosVO.getDthrUltimoEvento());

		// aink_int_ver.verifica_alta
		logInfo("Validando verificações alta. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarAlta(codigoAltaMedica, internacao
				.getDtSaidaPaciente());

		// aink_int_ver.verifica_data_saida_paciente
		logInfo("Validando verificações data de saída paciente. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarDataSaidaPaciente(internacao
				.getDtSaidaPaciente());

		// aink_int_atu.atualiza_seq_internacao
		logInfo("Atualizando INT_SEQ. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getAtualizaInternacaoRN().atualizarSeqInternacao(internacao);

		// aink_int_ver.verifica_data_alta(:new.seq,:new.dthr_alta_medica);
		logInfo("Validando verificações data de alta. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarDataAlta(internacao.getDthrAltaMedica());

		// rn_intp_bloq_neuro
		final String leitoId = internacao.getLeito() == null ? null : internacao
				.getLeito().getLeitoID();
		final Short numeroQuarto = internacao.getQuarto() == null ? null : internacao
				.getQuarto().getNumero();
		Short seqUnidadeFuncional = internacao.getUnidadesFuncionais() == null ? null
				: internacao.getUnidadesFuncionais().getSeq();
		final Short seqEspecialidade = internacao.getEspecialidade() == null ? null
				: internacao.getEspecialidade().getSeq();
		/*
		 * Trecho comentado conforme solicitado na melhoria #50951
		 * logInfo("Validando verificações bloqueio neuroloia. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.verificarBloqueioNeurologia(leitoId, numeroQuarto,
				seqUnidadeFuncional, seqEspecialidade);*/

		// aink_int_atu.atualiza_ind_localizacao
		logInfo("Atualizando indicador localização. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		DominioLocalPaciente dominioLocalPaciente = this.getAtualizaInternacaoRN().atualizarIndicadorLocalizacao(leitoId, numeroQuarto, seqUnidadeFuncional);
		internacao.setIndLocalPaciente(dominioLocalPaciente);
		
		// aink_int_ver.verifica_leito_ativo
		logInfo("Validando verificações leito ativo. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarLeitoAtivo(leitoId);
		
		logInfo("Validando leito Situacao. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		if (internacao.getAtendimentoUrgencia() == null
				|| (internacao.getAtendimentoUrgencia().getLeito() != null && !leitoId
						.equals(internacao.getAtendimentoUrgencia().getLeito()
								.getLeitoID()))) {
			this.getVerificaInternacaoRN().verificarLeito(leitoId);
		}
		
		// aink_int_ver.verifica_quarto_ativo
		logInfo("Validando verificações quarto ativo. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarQuartoAtivo(numeroQuarto);

		// aink_int_ver.verifica_unid_func_ativa
		logInfo("Validando verificações unidade funcional ativo. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN()
				.verificarUnidadeFuncionalAtivo(seqUnidadeFuncional);

		// aink_int_ver.verifica_faixa_idade
		logInfo("Validando verificações faixa de idade. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarFaixaIdade(seqEspecialidade, pacCodigo,
				internacao.getDthrInternacao());

		// aink_int_ver.verifica_permissao_convenio
		final Short codigoConvenio = internacao.getConvenioSaudePlano() == null ? null
				: internacao.getConvenioSaudePlano().getId().getCnvCodigo();
		logInfo("Validando verificações permissão convênio. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarPermissaoConvenio(codigoConvenio);

		// aink_int_ver.Verifica_Data_Int_Dias_Volta
		logInfo("Validando verificações data internação dias volta. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarDataInternacaoDiasVolta(internacao
				.getDthrInternacao());

		// aink_int_ver.rn_int_ver_sobre_in2
		logInfo("Validando verificações intervalo válido internação. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarIntervaloValidoInternacao(internacao
				.getSeq(), pacCodigo, internacao.getDthrInternacao(),
				internacao.getDthrAltaMedica());

		// aink_int_atu.Atualiza_Servidor
		final RapServidoresId idServidorDigitador = internacao.getServidorDigita() == null ? null
				: internacao.getServidorDigita().getId();
		logInfo("Atualizando servidor digitador. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getAtualizaInternacaoRN().atualizarServidor(idServidorDigitador);

		// aink_int_rn.rn_int_ver_prev_alta
		logInfo("Validando verificações data previsão de alta. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getValidaInternacaoRN()
				.verificarDataPrevisaoAlta(internacao.getDtPrevAlta());

		if (internacao.getAtendimentoUrgencia() != null
				&& internacao.getAtendimentoUrgencia().getSeq() != null
				&& internacao.getLeito() != null
				&& internacao.getLeito().getLeitoID() != null) {
			logInfo("Validando verificações medida preventiva. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
			this.getVerificaInternacaoRN().verificarMedidaPreventiva(internacao
					.getLeito().getLeitoID(), pacCodigo);
		}

		// aink_int_ver.rn_intp_ver_rn_sprnt
		logInfo("Validando verificações prontuário do paciente. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		this.getVerificaInternacaoRN().verificarProntuarioPaciente(pacCodigo);

		// Chama regra q atualiza a triagem do ambulatório
		// MAMP_CHECK_OUT_INT
		seqUnidadeFuncional = internacao.getUnidadesFuncionais() == null ? null
				: internacao.getUnidadesFuncionais().getSeq();
		final String tipoAlta = internacao.getTipoAltaMedica() == null ? null
				: internacao.getTipoAltaMedica().getCodigo();
		logInfo("Gerando check-out. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		ambulatorioFacade.gerarCheckOut(internacao.getSeq(), pacCodigo, null,
				tipoAlta, null, pesquisaInternacaoFacade.aincRetUnfSeq(internacao.getUnidadesFuncionais(), internacao.getQuarto(), internacao.getLeito()),
				null, internacao.getIndPacienteInternado());

		// SE FOR UM PAC DA EMERGÊNCIA, ATUALIZA A SITUAÇÃO DA TRIAGEM PARA EM
		// ATENDIMENTO
		// MAMP_ATU_TRG_EMATEND
		logInfo("Atualizando situação da triagem. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		ambulatorioFacade.atualizarSituacaoTriagem(pacCodigo);

		// Qdo pjq_seq is not null, popular o convênio da INT pegando do
		// ael_projeto_pesquisas
		// AINK_INT_RN.RN_INTP_ATU_CNV_PROJ(:new.pjq_seq,
		// :new.csp_cnv_codigo,:new.csp_seq);
		final Integer seqProjetoPesquisa = internacao.getProjetoPesquisa() == null ? null
				: internacao.getProjetoPesquisa().getSeq();
		logInfo("Atualizando convênio do projeto de pesquisa. PAC_CODIGO = "+ pacCodigo+COMMA_INT_SEQ_EQUALS+internacao.getSeq());
		FatConvenioSaudePlano fatConvenioSaudePlano = this.getValidaInternacaoRN().atualizarConvenioProjetoPesquisa(seqProjetoPesquisa);
		if (fatConvenioSaudePlano != null) {
			internacao.setConvenioSaudePlano(fatConvenioSaudePlano);
			internacao.setConvenioSaude(fatConvenioSaudePlano.getConvenioSaude());				
		}
	}


	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.GERA_CONVENIO_PACIENTE
	 * @ORADB AINP_ENFORCE_ATU_RULES.GERA_CONVENIO_PACIENTE
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 *  
	 */
	private void gerarConvenioPaciente(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) throws ApplicationBusinessException {
	

		FatConvenioSaudePlano convenioSaudePlano = convenioSaudePlanoDAO.obterConvenioPlanoInternacao(atendimentoUrgencia.getConvenioSaudePlano().getId().getCnvCodigo());
		
		Byte cspSeq = convenioSaudePlano != null ? convenioSaudePlano.getId().getSeq() : atendimentoUrgencia.getCspSeq();
		
		if(aipConveniosSaudePacienteDAO.pesquisarAtivosPorPacienteCodigoSeqConvenio(atendimentoUrgencia.getPaciente().getCodigo(),
				atendimentoUrgencia.getConvenioSaudePlano().getId().getCnvCodigo(), cspSeq).size() == 0) {
			Short seq = aipConveniosSaudePacienteDAO.obterValorSeqPlanoCodPaciente(atendimentoUrgencia.getPaciente().getCodigo());
			
			this.persitirConvenioPaciente(atendimentoUrgencia, cspSeq, seq);
		}
	}
	
	private void persitirConvenioPaciente(final AinAtendimentosUrgencia atendimentoUrgencia, Byte cspSeq, Short seq) {
		AipConveniosSaudePaciente aipConveniosSaudePaciente = new AipConveniosSaudePaciente();
		AipConveniosSaudePacienteId id = new AipConveniosSaudePacienteId();
		id.setPacCodigo(atendimentoUrgencia.getPaciente().getCodigo());
		id.setSeq(seq);
		
		aipConveniosSaudePaciente.setId(id);
		aipConveniosSaudePaciente.setConvenio(atendimentoUrgencia.getConvenioSaudePlano());
		aipConveniosSaudePaciente.getConvenio().getId().setSeq(cspSeq);
		aipConveniosSaudePaciente.setCriadoEm(new Date());
		aipConveniosSaudePaciente.setSituacao(DominioSituacao.A);
		aipConveniosSaudePaciente.setMatricula(null);
		aipConveniosSaudePaciente.setEncerradoEm(null);
		
		aipConveniosSaudePacienteDAO.persistir(aipConveniosSaudePaciente);
		aipConveniosSaudePacienteDAO.flush();
	}

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.OCUPA_LEITO
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 */
	private void ocupaLeito(final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) {
		// TODO Auto-generated method stub

	}

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.EXECUTA_ROTINAS_ATUALIZACAO
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void executaRotinasAtualizacao(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final IPesquisaExamesFacade pesquisaExamesFacade = this.getPesquisaExamesFacade();
		final InternacaoUtilRN internacaoUtilRN = this.getInternacaoUtilRN();
		
		final List<AinInternacao> internacoes = this.getAinInternacaoDAO()
				.listarInternacoesPorAtendimentoUrgencia(atendimentoUrgenciaAntigo);

		AinInternacao internacao = null;
		if (internacoes != null && !internacoes.isEmpty()) {
			// Pega apenas a primeira internação, conforma o cursor migrado.
			internacao = internacoes.get(0);
		}
		
		AghAtendimentos atendimento = buscaAtendimentoPorNumeroConsulta(atendimentoUrgencia.getConsulta().getNumero());
		if (internacaoUtilRN
				.modificados(atendimentoUrgenciaAntigo.getDtAtendimento(),
						atendimentoUrgencia.getDtAtendimento())) {
			
			getAtendimentoUrgenciaRN().atualizarDataLancamentoExtratoLeito(
					atendimentoUrgencia.getSeq(), atendimentoUrgencia
							.getDtAltaAtendimento(), atendimentoUrgenciaAntigo
							.getDtAltaAtendimento());
			
			getAtendimentoUrgenciaRN().atualizarUltimaInternacaoPaciente(
					atendimentoUrgencia.getPaciente(),
					atendimentoUrgenciaAntigo.getDtAtendimento(),
					atendimentoUrgencia.getDtAtendimento());

//			atendimento = buscaAtendimentoPorNumeroConsulta(atendimentoUrgencia.getConsulta().getNumero());

			this.getValidaInternacaoRN().atualizarContaInternacao(internacao.getSeq(), atendimentoUrgencia.getDtAtendimento(), nomeMicrocomputador, dataFimVinculoServidor);
			
			this.getValidaInternacaoRN().atualizarConvenioPlanoExames(atendimento
					.getSeq(), internacao.getConvenioSaude().getCodigo(),
					internacao.getConvenioSaudePlano().getId().getSeq(),
					atendimentoUrgencia.getDtAtendimento(), nomeMicrocomputador);

			this.getValidaInternacaoRN().atualizarConvenioPlanoCirurgias(atendimento
					.getSeq(), internacao.getConvenioSaude().getCodigo(),
					internacao.getConvenioSaudePlano().getId().getSeq(),
					atendimentoUrgencia.getDtAtendimento());
		}
		
		final String codigoTipoAltaObito = obtemCodigoAlta(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);
		final String codigoTipoAltaObito48h = obtemCodigoAlta(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H);
		
		if (atendimentoUrgencia.getDtAltaAtendimento() != null && atendimentoUrgenciaAntigo.getDtAltaAtendimento() == null) {
			// Atualização da alta
			final Integer codigoMovimentacao = obtemCodigoMovimento(AghuParametrosEnum.P_COD_MVTO_INT_ALTA.toString());
			geraMovimentoAtendimento(atendimentoUrgencia, codigoMovimentacao, atendimentoUrgencia.getDtAltaAtendimento());

			if (atendimentoUrgenciaAntigo.getLeito() != null) {
				// desocupa o leito
				desocupaLeito(atendimentoUrgencia, atendimentoUrgenciaAntigo);
			}
			
			if (internacao == null) {
				getAtendimentoUrgenciaRN().atualizarUltimaAltaPaciente(atendimentoUrgencia.getPaciente(), nomeMicrocomputador, atendimentoUrgencia.getDtAltaAtendimento());
				getAtendimentoUrgenciaRN().atualizarFimAtendimento(atendimentoUrgencia, nomeMicrocomputador, atendimentoUrgencia.getDtAltaAtendimento());
				pesquisaExamesFacade.cancelarExamesNaAlta(atendimento, atendimentoUrgencia.getTipoAltaMedica(), nomeMicrocomputador);
			}
		} else if (atendimentoUrgencia.getDtAltaAtendimento() == null && atendimentoUrgenciaAntigo.getDtAltaAtendimento() != null) {
			// Estorno  da alta
			final Integer codigoMovimentacao = obtemCodigoMovimento(AghuParametrosEnum.P_COD_MVTO_INT_ALTA.toString());
			
			deletaMovimentoInternacao(atendimentoUrgencia, atendimentoUrgenciaAntigo, codigoMovimentacao, atendimentoUrgenciaAntigo.getDtAltaAtendimento());
			
			this.getValidaInternacaoRN().atualizarPaciente(atendimentoUrgencia.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, null);
			
			if (internacao == null) {
				getAtendimentoUrgenciaRN().atualizarFimAtendimento(atendimentoUrgencia, nomeMicrocomputador, atendimentoUrgencia.getDtAltaAtendimento());
			}
		} else if (!atendimentoUrgencia.getDtAltaAtendimento().equals(atendimentoUrgenciaAntigo.getDtAltaAtendimento())) {
			// Alteração  da  data  da  alta
			final Integer codigoMovimentacaoInternacaoAlta = obtemCodigoMovimento(AghuParametrosEnum.P_COD_MVTO_INT_ALTA.toString());
			
			atualizaMovimentoInternacao(atendimentoUrgencia, atendimentoUrgenciaAntigo, codigoMovimentacaoInternacaoAlta, atendimentoUrgenciaAntigo.getDtAltaAtendimento());
			
			getAtendimentoUrgenciaRN().atualizarDataLancamentoExtratoLeito(
					atendimentoUrgencia.getSeq(), atendimentoUrgencia
							.getDtAltaAtendimento(), atendimentoUrgenciaAntigo
							.getDtAltaAtendimento());
			
			this.getValidaInternacaoRN().atualizarDataAltaPaciente(atendimentoUrgencia
					.getPaciente().getCodigo(), atendimentoUrgenciaAntigo
					.getDtAltaAtendimento(), atendimentoUrgencia
					.getDtAltaAtendimento(), nomeMicrocomputador);
			
			if (atendimentoUrgencia.getTipoAltaMedica() != null) {
				if (atendimentoUrgencia.getTipoAltaMedica().getCodigo().equals(
						codigoTipoAltaObito)
						|| atendimentoUrgencia.getTipoAltaMedica().getCodigo()
								.equals(codigoTipoAltaObito48h)) {
					atualizaObitoPaciente(atendimentoUrgencia, atendimentoUrgencia.getDtAltaAtendimento());
				}
			}
			
			if (internacao == null) {
				getAtendimentoUrgenciaRN().atualizarFimAtendimento(atendimentoUrgencia, nomeMicrocomputador, atendimentoUrgencia.getDtAltaAtendimento());
			}
		}
		
		// Atualização do tipo da alta
		final String codigoTipoAltaNormal = obtemCodigoAlta(AghuParametrosEnum.P_COD_TIPO_ALTA_NORMAL);
		
		if (atendimentoUrgencia.getTipoAltaMedica() != null
				&& (atendimentoUrgenciaAntigo.getTipoAltaMedica() == null || atendimentoUrgenciaAntigo
						.getTipoAltaMedica().getCodigo().equals(
								codigoTipoAltaNormal))) {
			if (atendimentoUrgencia.getTipoAltaMedica() != null
					&& (atendimentoUrgencia.getTipoAltaMedica().getCodigo()
							.equals(codigoTipoAltaObito) || atendimentoUrgencia
							.getTipoAltaMedica().getCodigo().equals(
									codigoTipoAltaObito48h))) {
				atualizaObitoPaciente(atendimentoUrgencia, atendimentoUrgencia
						.getDtAltaAtendimento());
			}
		} else if (atendimentoUrgenciaAntigo.getTipoAltaMedica() != null
				&& (atendimentoUrgencia.getTipoAltaMedica() == null || atendimentoUrgencia
						.getTipoAltaMedica().getCodigo().equals(
								codigoTipoAltaNormal))) {
			if (atendimentoUrgenciaAntigo.getTipoAltaMedica().getCodigo()
					.equals(codigoTipoAltaObito)
					|| atendimentoUrgenciaAntigo.getTipoAltaMedica()
							.getCodigo().equals(codigoTipoAltaObito48h)) {
				atualizaObitoPaciente(atendimentoUrgencia, null);
			}
		}
		
		if (internacaoUtilRN.modificados(
				atendimentoUrgenciaAntigo.getLeito(),
				atendimentoUrgencia.getLeito())
				|| internacaoUtilRN.modificados(
						atendimentoUrgenciaAntigo.getQuarto(),
						atendimentoUrgencia.getQuarto())
				|| internacaoUtilRN.modificados(
						atendimentoUrgenciaAntigo.getUnidadeFuncional(),
						atendimentoUrgencia.getUnidadeFuncional())) {

			getAtendimentoUrgenciaRN().atualizarLeitoNoAtendimento(atendimentoUrgencia, atendimentoUrgenciaAntigo, nomeMicrocomputador, dataFimVinculoServidor);
			
			this.getValidaInternacaoRN().atualizarPaciente(atendimentoUrgencia.getPaciente(), nomeMicrocomputador, dataFimVinculoServidor, null);
		}
	}
	
	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.ATUALIZA_OBITO_PACIENTE
	 * 
	 * @param atendimentoUrgencia
	 * @param dataObito
	 * @throws ApplicationBusinessException
	 */
	private void atualizaObitoPaciente(
			final AinAtendimentosUrgencia atendimentoUrgencia, final Date dataObito) throws ApplicationBusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.ATUALIZA_MOVIMENTO_INT
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @param codigoMovimentacao
	 * @param dtAltaAtendimento
	 * @throws ApplicationBusinessException
	 */
	public void atualizaMovimentoInternacao(final AinAtendimentosUrgencia atendimentoUrgencia, 
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, final Integer codigoMovimentacao, 
			final Date dtAltaAtendimentoAntiga) throws ApplicationBusinessException {
		
		Integer seq = atendimentoUrgencia.getSeq();			
		List<AinMovimentosAtendUrgencia> lista = ainMovimentosAtendUrgenciaDAO.pesquisarMovimentosAtendimentosUrgencia(seq, codigoMovimentacao, dtAltaAtendimentoAntiga);
		
		for(AinMovimentosAtendUrgencia objeto : lista){
			objeto.setDthrLancamento(atendimentoUrgencia.getDtAltaAtendimento());  
			ainMovimentosAtendUrgenciaDAO.merge(objeto);
		}
	}
	

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.DELETA_MOVIMENTO_INT
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @param codigoMovimentacao
	 * @param dtAltaAtendimento
	 * @throws ApplicationBusinessException
	 */
	private void deletaMovimentoInternacao(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo,
			final Integer codigoMovimentacao, final Date dtAltaAtendimento) throws ApplicationBusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.DESOCUPA_LEITO
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @throws ApplicationBusinessException
	 */
	private void desocupaLeito(final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo) throws ApplicationBusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @ORADB AINP_ENFORCE_ATU_RULES.GERA_MOVIMENTO_ATEND
	 * 
	 * @param atendimentoUrgencia
	 * @param codigoMovimentacao
	 * @param dtAltaAtendimento
	 * @throws ApplicationBusinessException 
	 */
	public void geraMovimentoAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia, final Integer codigoMovimentacao,
			final Date dataHoraLancamento) throws ApplicationBusinessException {
		
		AinMovimentosAtendUrgencia movimentoAtendimentoUrgencia = montarMovimentoAtendimentoUrgencia(atendimentoUrgencia);
		
		this.prePersistirMovimentoAtendimentoUrgencia(movimentoAtendimentoUrgencia, atendimentoUrgencia);
		
		ainMovimentosAtendUrgenciaDAO.persistir(movimentoAtendimentoUrgencia);
		ainMovimentosAtendUrgenciaDAO.flush();
	}
	
	private AinMovimentosAtendUrgencia montarMovimentoAtendimentoUrgencia(final AinAtendimentosUrgencia atendimentoUrgencia)
			throws ApplicationBusinessException {
		AinMovimentosAtendUrgencia movimentoAtendimentoUrgencia = new AinMovimentosAtendUrgencia();

		AinMovimentosAtendUrgenciaId id = new AinMovimentosAtendUrgenciaId();
		id.setSeqAtendimentoUrgencia(atendimentoUrgencia.getSeq());

		movimentoAtendimentoUrgencia.setId(id);

		AinTiposMvtoInternacao tipoMovimentoInternacao = ainTiposMvtoInternacaoDAO.obterTiposMvtoInternacao(parametroFacade
				.buscarValorInteiro(AghuParametrosEnum.P_COD_MVTO_INT_INGR_SO));

		movimentoAtendimentoUrgencia.setTipoMovimentoInternacao(tipoMovimentoInternacao);

		movimentoAtendimentoUrgencia.setDthrLancamento(atendimentoUrgencia.getDtAtendimento());
		movimentoAtendimentoUrgencia.setClinica(atendimentoUrgencia.getClinica());

		return movimentoAtendimentoUrgencia;
	}

	
	/**
	 * @ORADB AINT_MPU_BRI – Executa antes de inserir na tabela ain_movimentos_atend_urgencia
	 * @param movimentoAtendimentoUrgencia
	 * @param atendimentoUrgencia
	 * @return AinMovimentosAtendUrgencia
	 */
	private AinMovimentosAtendUrgencia prePersistirMovimentoAtendimentoUrgencia(AinMovimentosAtendUrgencia movimentoAtendimentoUrgencia, AinAtendimentosUrgencia atendimentoUrgencia){
	
		movimentoAtendimentoUrgencia.getId().setCriadoEm(new Date());
		
		movimentoAtendimentoUrgencia.setServidor(getServidorLogadoFacade().obterServidorLogado());

		if (atendimentoUrgencia.getLeito() != null) {
			
			Short unfSeq = null;
			
			if(atendimentoUrgencia.getLeito().getUnidadeFuncional() != null && atendimentoUrgencia.getLeito().getUnidadeFuncional().getUnfSeq() != null){
				unfSeq = atendimentoUrgencia.getLeito().getUnidadeFuncional().getSeq();
			}
			
			movimentoAtendimentoUrgencia.setUnidadeFuncional(aghUnidadesFuncionaisDAO.obterOriginal(unfSeq));
			
			Short numeroQuarto = null;
			
			if(atendimentoUrgencia.getLeito().getQuarto() != null && atendimentoUrgencia.getLeito().getQuarto().getNumero() != null){
				numeroQuarto = atendimentoUrgencia.getLeito().getQuarto().getNumero();
			}
			
			movimentoAtendimentoUrgencia.setQuarto(ainQuartosDAO.pesquisaQuartoPorNumero(numeroQuarto));
			movimentoAtendimentoUrgencia.setLeito(atendimentoUrgencia.getLeito());
		}
		
		else if (atendimentoUrgencia.getQuarto() != null) {
			movimentoAtendimentoUrgencia.setQuarto(atendimentoUrgencia.getQuarto());
			movimentoAtendimentoUrgencia.setLeito(atendimentoUrgencia.getLeito());
			
			Short unfSeq = null;
			
			if(atendimentoUrgencia.getQuarto().getUnidadeFuncional() != null && atendimentoUrgencia.getQuarto().getUnidadeFuncional().getSeq() != null){
				unfSeq = atendimentoUrgencia.getQuarto().getUnidadeFuncional().getSeq();
			}
			
			movimentoAtendimentoUrgencia.setUnidadeFuncional(aghUnidadesFuncionaisDAO.obterOriginal(unfSeq));
		}
		
		else {
			movimentoAtendimentoUrgencia.setQuarto(atendimentoUrgencia.getQuarto());
			movimentoAtendimentoUrgencia.setLeito(atendimentoUrgencia.getLeito());
			movimentoAtendimentoUrgencia.setUnidadeFuncional(atendimentoUrgencia.getUnidadeFuncional());
		}
		return movimentoAtendimentoUrgencia;
	}
	

	/**
	 * ORADB AINP_ENFORCE_ATU_RULES.CONSISTE_SOBREPOSICAO_DATA
	 * 
	 * Executada para as rotinas de insercao e update para consistir a sobreposicao de periodos.
	 * 
	 * @param seqAtendimentoUrgencia
	 * @param paciente
	 * @param dataHoraInternacao
	 * @param dataHoraAltaMedica
	 * @throws ApplicationBusinessException
	 */
	public void consisteSobreposicaoData(final Integer seqAtendimentoUrgencia,
			final AipPacientes paciente, final Date dataHoraInternacao,
			final Date dataHoraAltaMedica) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!"AGH".equalsIgnoreCase(servidorLogado.getUsuario())) {
			final Date auxDataHoraAltaMedica = dataHoraAltaMedica != null ? dataHoraAltaMedica : new Date();

			final List<AinAtendimentosUrgencia> atendimentosUrgencia = this.getAinAtendimentosUrgenciaDAO()
					.listarAtendimentosUrgenciaParaTestarSobreposicaoDatas(seqAtendimentoUrgencia, paciente, dataHoraInternacao,
							auxDataHoraAltaMedica);

			if (atendimentosUrgencia != null && !atendimentosUrgencia.isEmpty()) {
				throw new ApplicationBusinessException(
						InternacaoRN.InternacaoRNExceptionCode.AIN_00668);
			}
		}
	}
	
	/**
	 * Código referente ao cursor c_agh_atendimento
	 * 
	 * @param numeroConsulta
	 * @return
	 */
	private AghAtendimentos buscaAtendimentoPorNumeroConsulta(final Integer numeroConsulta) {
		return this.getAghuFacade().buscaAtendimentoPorNumeroConsulta(numeroConsulta);
	}
	
	/**
	 * AINP_ENFORCE_ATU_RULES.OBTEM_CODIGO_MVTO
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer obtemCodigoMovimento(final String parametro) throws ApplicationBusinessException {
			final AghParametros aghParametros = this.getParametroFacade().obterAghParametroPorNome(parametro);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			return aghParametros.getVlrNumerico().intValue();
		} else {
			throw new ApplicationBusinessException(
					InternacaoRN.InternacaoRNExceptionCode.AIN_00283);
		}
	}
	
	/**
	 * AINP_ENFORCE_ATU_RULES.OBTEM_CODIGO_ALTA
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obtemCodigoAlta(final AghuParametrosEnum parametro) throws ApplicationBusinessException {
			final AghParametros aghParametros = this.getParametroFacade().buscarAghParametro(parametro);
		if (aghParametros != null && aghParametros.getVlrTexto() != null) {
			return aghParametros.getVlrTexto();
		} else {
			throw new ApplicationBusinessException(
					InternacaoRN.InternacaoRNExceptionCode.AIN_00534);
		}
	}

	/**
	 * Método para validar o local da internação (leito/quarto/unidade
	 * funcional) se a especialidade for neurologia. As regras desse método são
	 * específicas para o HCPA, assim só serão chamadas se o parâmetro
	 * PARAMETRO_HU = 'HCPA'
	 * 
	 * ORADB: Procedure RN_INTP_BLOQ_NEURO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarBloqueioNeurologia(final String leitoId,
			final Short numeroQuarto, final Short seqUnidadeFuncional,
			final Short seqEspecialidade)
			throws ApplicationBusinessException {

		if (!isHCPA() ||  !ainInternacaoJnDAO.isOracle()) {		
				return;			
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ainAcompanhantesInternacaoDAO.verificarBloqueioNeurologia(leitoId,
				numeroQuarto, seqUnidadeFuncional, seqEspecialidade, servidorLogado.getUsuario());

	}
	
	
	/**
	 * Método que busca valores de leito e acomodação para o relatório de
	 * hospitalização de paciente.
	 * 
	 * ORADB: Function FFC_F_VALR_ACOM
	 */
	public Double verificarValorAcomodacao(final Integer codAcom,
			final Date dthrInternacao) throws ApplicationBusinessException {		
		
		if (!ainInternacaoJnDAO.isOracle()) {
		    return null;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return ainAcompanhantesInternacaoDAO.verificarValorAcomodacao(codAcom, dthrInternacao, servidorLogado.getUsuario());
		
	}


	/**
	 * @return the internacaoUtilRN
	 */
	protected InternacaoUtilRN getInternacaoUtilRN() {
		return internacaoUtilRN;
	}

	/**
	 * @return the verificaInternacaoRN
	 */
	public VerificaInternacaoRN getVerificaInternacaoRN() {
		return verificaInternacaoRN;
	}

	/**
	 * Método para executar as regras de triggers de AIN_CIDS_INTERNACAO.
	 * 
	 * ORADB Trigger AINT_CDI_ASI
	 * 
	 * @param cidInternacao
	 * @param operacao
	 * @throws BaseException 
	 */
	public void executarTriggersCid(final AinCidsInternacao cidInternacao,
			final DominioOperacoesJournal operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if (cidInternacao != null && operacao != null) {
			this.executarEnforceCids(cidInternacao, operacao, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * Método para executar regras da enforce chamada na inserção de CID de
	 * internação.
	 * 
	 * ORADB Procedure AINP_ENFORCE_CDI_RULES
	 * 
	 * @param cidInternacao
	 * @param operacao
	 * @throws BaseException 
	 */
	private void executarEnforceCids(final AinCidsInternacao cidInternacao,
			final DominioOperacoesJournal operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		if (DominioOperacoesJournal.INS.equals(operacao)) {
			final List<FatContasInternacao> contasInternacao = this.getFaturamentoFacade()
					.pesquisarContaInternacaoPorInternacao(cidInternacao
							.getInternacao().getSeq());
			if (contasInternacao != null && contasInternacao.size() > 1) {
				return;

			} else if (contasInternacao != null && contasInternacao.size() == 1) {
				this.validarContasInternacaoIgualUm(cidInternacao, nomeMicrocomputador, dataFimVinculoServidor);
			} else if (contasInternacao != null && contasInternacao.size() == 0) {
				this.validarContasInternacaoIgualZero(cidInternacao, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
	}

	private void validarContasInternacaoIgualUm(final AinCidsInternacao cidInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		final List<FatContasInternacao> contasInternacaoNaoManuseadas = faturamentoFacade
				.obterContaInternacaoNaoManuseada(cidInternacao.getInternacao()
						.getSeq());

		if (contasInternacaoNaoManuseadas != null
				&& contasInternacaoNaoManuseadas.size() == 1) {
			final FatContasInternacao contaInternacaoManuseada = contasInternacaoNaoManuseadas
					.get(0);

			final FatCidContaHospitalar cidContaHospitalar = new FatCidContaHospitalar();
			final FatCidContaHospitalarId idCidContaHospitalar = new FatCidContaHospitalarId();
			idCidContaHospitalar.setCthSeq(contaInternacaoManuseada
					.getContaHospitalar().getSeq());
			idCidContaHospitalar.setCidSeq(cidInternacao.getCid().getSeq());
			idCidContaHospitalar.setPrioridadeCid(cidInternacao
					.getPrioridadeCid());
			cidContaHospitalar.setId(idCidContaHospitalar);

			try {
				//Correção incidente #31132
				boolean cidExistente = false;
				List<FatCidContaHospitalar> listaCidContaHospitalar = faturamentoFacade.pesquisarCidContaHospitalar(contaInternacaoManuseada.getContaHospitalar().getSeq());
		  		for(final FatCidContaHospitalar cidContaHospitalarExistente: listaCidContaHospitalar){
					if (cidContaHospitalarExistente.equals(cidContaHospitalar)) {
						cidExistente = true;
						break;
					}
		  		}
		  		if(!cidExistente){
		  			faturamentoFacade.persistirCidContaHospitalar(cidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
		  		}
			}
			catch(final InactiveModuleException e) {
				logWarn(e.getMessage());
			}

		} else if (contasInternacaoNaoManuseadas.size() > 1) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00360);
		}
	}

	/** ORADB: GERA_CONTA_HOSPITALAR */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void validarContasInternacaoIgualZero(final AinCidsInternacao cidInternacao, final String nomeMicrocomputador, 
			  final Date dataFimVinculoServidor) 
										throws BaseException {
			
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final IParametroFacade parametroFacade = this.getParametroFacade();
		final IPesquisaInternacaoFacade pesquisaInternacaoFacade = this.getPesquisaInternacaoFacade();
		final IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		Byte seqTipoAih = null;

		final AinInternacao internacao = this.obterAtendimentoInternacaoCid(cidInternacao.getInternacao().getSeq());

		if (internacao == null) {
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00361);
		}
		
		// Busca atendiemento, pois o mesmo é salvo em algum momento na execução
		// da enforce, porém não é associado a internação.
		final AghAtendimentos atendimento = this.getDarAltaPacienteON().obterAtendimentoDaInternacao(internacao.getSeq());
		internacao.setAtendimento(atendimento);
		
		// Atribuido manualmente valor, pois não estava sendo carregado pela
		// criteria do método que carrega a internação.
		final FatItensProcedHospitalar itemProcedimentoHospitalar = faturamentoFacade
				.obterItemProcedHospitalar(internacao.getIphPhoSeq(),internacao.getIphSeq());
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);

		// if v_GRUPO_CONVENIO = 'S' then
		if (internacao.getConvenioSaudePlano() != null && internacao.getConvenioSaudePlano().getConvenioSaude() != null
				&& DominioGrupoConvenio.S.equals(internacao.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio())) {

			try {
				final AghParametros codigoSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
				final FatTipoAih tipoAih = faturamentoFacade.obterItemAihPorCodigoSus(codigoSus.getVlrNumerico().shortValue());
				seqTipoAih = tipoAih.getSeq();

			} catch (final Exception e) {
				logError(e.getMessage(), e);
				throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00361);
			}
		}

		// Verifica a data a ser inserida como data de internacao da conta hospitalar
		Date dataInternacao = null;
		if (internacao.getAtendimentoUrgencia() != null) {
			dataInternacao = internacao.getAtendimentoUrgencia().getDtAtendimento();
		} else {
			dataInternacao = internacao.getDthrInternacao();
		}

		// Código referente a toda inserção da FatContasHospitalares, setando seus campos
		FatContasInternacao contaInternacao = null;
		try {
			/* SELECIONA PROCEDIMENTO HOSPITALAR INTERNO  
			Foi colocado rownum=1 porque o faturamento é feito a partir do campo cod_tabela da tabela 
			fat_item_proced_hospitalares e o procedimento hospitalar interno é usado apenas internamente no sistema, não 
			importando qual seja, mas que exista 			   
			-- desativado até surgir necessidade para faturamento dados_faturamento;
			INSERIR REGISTRO NA TABELA CONTA HOSPITALAR */			
			final FatContasHospitalares contaHospitalar = this.inserirContaHospitalar(
					internacao, seqTipoAih, dataInternacao, nomeMicrocomputador, dataFimVinculoServidor);

			/*
				INSERIR REGISTRO NA TABELA ITEM CONTA HOSPITALAR 
				--  insere_item_conta;
				-- desativado até surgir necessidade para faturamento INSERIR REGISTRO NA TABELA CONTA INTERNACAO 
				-- mantido para comtemplar necessidade da existencia de conta se necessário lançar ACH (Adiantamento conta)
			*/
			contaInternacao = this.inserirContaInternacao( internacao, contaHospitalar, nomeMicrocomputador, 
															dataFimVinculoServidor);
			 
			// cfv(24/09/2001) busca sangue inseridos antes da internacao deve ser feito apenas na internacao
			// fatk_cth4_rn_un.rn_cthp_busca_sangue(v_numero_conta);
			faturamentoFacade.buscarSangue(contaInternacao.getContaHospitalar().getCthSeq());
			
			faturamentoFacade.incluiMateriais(contaInternacao.getContaHospitalar().getCthSeq(), dataFimVinculoServidor);
			
			// Marina 17/01/2013
			//fatk_cth4_rn_un.rn_cthp_busca_materiais(v_numero_conta);
		} catch (final InactiveModuleException e) {
			logWarn(e.getMessage());
		}

		geraLaudos( faturamentoFacade, parametroFacade, pesquisaInternacaoFacade, 
					prescricaoMedicaFacade, internacao, atendimento, dataInternacao);

		geraCCH(contaInternacao, cidInternacao, nomeMicrocomputador, dataFimVinculoServidor);		

		if (atendimento != null && atendimento.getSeq() != null) {
			getValidaInternacaoRN().atualizarConvenioPlanoExames( atendimento.getSeq(),
					internacao.getConvenioSaudePlano().getId().getCnvCodigo(),
					internacao.getConvenioSaudePlano().getId().getSeq(),
					dataInternacao, nomeMicrocomputador);
			
			getValidaInternacaoRN().atualizarConvenioPlanoCirurgias( atendimento.getSeq(),
					internacao.getConvenioSaudePlano().getId().getCnvCodigo(), 
					internacao.getConvenioSaudePlano().getId().getSeq(),
					dataInternacao);
		}

	}
	
	/** ORADB: GERA_LAUDOS */
	private void geraLaudos( final IFaturamentoFacade faturamentoFacade,
							 final IParametroFacade parametroFacade, final IPesquisaInternacaoFacade pesquisaInternacaoFacade,
							 final IPrescricaoMedicaFacade prescricaoMedicaFacade, final AinInternacao internacao, 
							 final AghAtendimentos atendimento,	Date dataInternacao) throws ApplicationBusinessException {
		
		final Long idadePediatrica = parametroFacade.buscarValorLong(AghuParametrosEnum.P_IDADE_PEDIATRICA);
		final Short unidadeFuncional9Sul = parametroFacade.buscarValorShort(AghuParametrosEnum.P_UNID_9SUL);		
		
		// Chamada única para verificar o seqUnidadeFuncional válido, já que é
		// usado em vários testes nos IFs abaixo
		final Short seqUnidadeFuncional = obterSeqUnidadeFuncionalValido(internacao);

		try {
			Long idade = null;
			if (atendimento != null && atendimento.getDthrInicio() != null
					&& internacao.getPaciente().getDtNascimento() != null) {
				idade = CoreUtil.calcularAnosEntreDatas( atendimento.getDthrInicio(),
						                                 internacao.getPaciente().getDtNascimento());
			}

			if (!unidadeFuncional9Sul.equals(seqUnidadeFuncional)
					|| unidadeFuncional9Sul.equals(seqUnidadeFuncional)
					&& idade != null && idade < idadePediatrica) {

				final DominioSimNao existeCaracteristicaAcompanhante = pesquisaInternacaoFacade
						.verificarCaracteristicaDaUnidadeFuncional(seqUnidadeFuncional,
								                                   ConstanteAghCaractUnidFuncionais.LAUDO_ACOMP);
				if (DominioSimNao.S.equals(existeCaracteristicaAcompanhante)
						&& faturamentoFacade.verificaDiariaAcompanhante(internacao.getItemProcedimentoHospitalar())) {
					try {
						prescricaoMedicaFacade.geraLaudoInternacao(atendimento,	dataInternacao,
								                                   AghuParametrosEnum.P_LAUDO_ACOMPANHANTE, internacao.getConvenioSaudePlano());
					}
					catch (final InactiveModuleException e) {
						logWarn(e.getMessage());
						//faz chamada nativa
						RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
						getObjetosOracleDAO().executaGeraLaudoInternacao(atendimento.getSeq(), 
								 dataInternacao, 
								 AghuParametrosEnum.P_LAUDO_ACOMPANHANTE.toString(), 
								 internacao.getConvenioSaudePlano().getId().getCnvCodigo(), 
								 internacao.getConvenioSaudePlano().getId().getSeq(), 
								 servidorLogado);
					}
				}
			}
			
			Integer diariaMaximaCti = 1;
			if (internacao.getItemProcedimentoHospitalar() != null
					&& internacao.getItemProcedimentoHospitalar().getMaxDiariaUti() != null) {
				diariaMaximaCti = internacao.getItemProcedimentoHospitalar().getMaxDiariaUti().intValue();
			}

			final DominioSimNao existeCaracteristicaCti = pesquisaInternacaoFacade
					.verificarCaracteristicaDaUnidadeFuncional(seqUnidadeFuncional,ConstanteAghCaractUnidFuncionais.LAUDO_CTI);

			if (DominioSimNao.S.equals(existeCaracteristicaCti) && diariaMaximaCti > 0) {
				try {
					prescricaoMedicaFacade.geraLaudoInternacao(atendimento,
							dataInternacao, AghuParametrosEnum.P_LAUDO_UTI,	internacao.getConvenioSaudePlano());
				}
				catch (final InactiveModuleException e) {
					logWarn(e.getMessage());
				}
			}

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.AIN_00363);
		}
	}

	/**
	 * Método para setar os campos do objeto FatContasHospitalares, persistir o
	 * mesmo e retornar o mesmo sincronizado com a session do hibernate.
	 * 
	 * @param atendimento
	 * @param seqTipoAih
	 * @param dataInternacao
	 * @return
	 * @throws BaseException 
	 */
	private FatContasHospitalares inserirContaHospitalar(
			final AinInternacao internacao, final Byte seqTipoAih, final Date dataInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		FatContasHospitalares contaHospitalar = new FatContasHospitalares();

		contaHospitalar.setDataInternacaoAdministrativa(dataInternacao);
		contaHospitalar
				.setConvenioSaude(internacao.getConvenioSaude() == null ? null
						: internacao.getConvenioSaude());
		contaHospitalar.setConvenioSaudePlano(internacao
				.getConvenioSaudePlano());

		contaHospitalar.setServidor(internacao.getServidorDigita());
		contaHospitalar.setServidorManuseado(internacao.getServidorDigita());

		final List<FatConvGrupoItemProced> convGrupoItensProcedList = faturamentoFacade.pesquisarConvenioGrupoItemProcedimento(internacao.getIphSeq(), internacao.getIphPhoSeq());

		FatConvGrupoItemProced convGrupoItensProced = null;
		if (convGrupoItensProcedList != null
				&& convGrupoItensProcedList.size() > 0) {
			convGrupoItensProced = convGrupoItensProcedList.get(0);
		}

		if (convGrupoItensProced != null) {
			contaHospitalar
					.setProcedimentoHospitalarInterno(convGrupoItensProced
							.getProcedimentoHospitalarInterno());
		}

		contaHospitalar.setServidorTemProfResponsavel(null);
		contaHospitalar.setContaManuseada(false);
		contaHospitalar.setIndSituacao(DominioSituacaoConta.A);
		contaHospitalar.setTipoAih((seqTipoAih != null)?faturamentoFacade.obterTipoAih(seqTipoAih):null);
		contaHospitalar.setEspecialidade(internacao.getEspecialidade());

		contaHospitalar.setConvenioSaude(internacao.getConvenioSaudePlano()
				.getConvenioSaude());

		// Insere conta hospitalar
		contaHospitalar = faturamentoFacade.persistirContaHospitalar(contaHospitalar, null, nomeMicrocomputador, dataFimVinculoServidor);

		return contaHospitalar;
	}

	/**
	 * Método para setar os campos do objeto FatContasInternacao, persistir o
	 * mesmo e retornar o mesmo sincronizado com a session do hibernate.
	 * 
	 * @param atendimento
	 * @param contaHospitalar
	 * @return
	 * @throws BaseException 
	 */
	private FatContasInternacao inserirContaInternacao(
			final AinInternacao internacao, final FatContasHospitalares contaHospitalar, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final FatContasInternacao contaInternacao = new FatContasInternacao();
		contaInternacao.setInternacao(internacao);
		contaInternacao.setContaHospitalar(contaHospitalar);

		//Adiciona na lista de contas da internacao
		internacao.addFatContaInternacao(contaInternacao);
		
		// Insere conta internacao
		this.getFaturamentoFacade().inserirContaInternacao(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);

		return contaInternacao;
	}

	/** ORADB: GERA_CCH	 */
	private void geraCCH(final FatContasInternacao contaInternacao,
			final AinCidsInternacao cidInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		if (contaInternacao != null) {
			final FatCidContaHospitalar cidContaHospitalar = new FatCidContaHospitalar();
			final FatCidContaHospitalarId cidContaHospitalarId = new FatCidContaHospitalarId();
			cidContaHospitalarId.setCthSeq(contaInternacao.getContaHospitalar().getSeq());
			cidContaHospitalarId.setCidSeq(cidInternacao.getCid().getSeq());
			cidContaHospitalarId.setPrioridadeCid(cidInternacao.getPrioridadeCid());
			cidContaHospitalar.setId(cidContaHospitalarId);
				
			this.getFaturamentoFacade().inserirCidContaHospitalar(cidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	private Short obterSeqUnidadeFuncionalValido(final AinInternacao internacao) {
		final Short unidFuncional = internacao.getUnidadesFuncionais() == null ? null
				: internacao.getUnidadesFuncionais().getSeq();
		final Short numeroQuarto = internacao.getQuarto() == null ? null : internacao
				.getQuarto().getNumero();
		final String leitoId = internacao.getLeito() == null ? null : internacao
				.getLeito().getLeitoID();

		// Chamada única para verificar o seqUnidadeFuncional válido, já que é
		// usado em vários testes nos IFs abaixo
		final Short seqUnidadeFuncional = this.getPesquisaInternacaoFacade().aincRetUnfSeq(
				unidFuncional, numeroQuarto, leitoId);

		return seqUnidadeFuncional;
	}

	/**
	 * Método para buscar dados de internação (como no cursor com o nome 'int'
	 * da enforce de CIDs)
	 * 
	 * @param seqInternacao
	 * @return
	 */
	private AinInternacao obterAtendimentoInternacaoCid(final Integer seqInternacao) {
		if (seqInternacao == null) {
			return null;
		} else {
			return this.getAinInternacaoDAO().obterAtendimentoInternacaoCid(seqInternacao);
		}
	}
	
	/**
  	 * ORADB ainp_sugere_prof_esp
  	 */
	public AghProfEspecialidades verificarEscalaProfessor(final Short espSeq, final Short cnvCodigo){
  		final List<AinEscalasProfissionalInt> listaEscalas = this.getAinEscalasProfissionalIntDAO().listarEscalasProfissionaisInt(cnvCodigo, espSeq, new Date());
  		
  		AghProfEspecialidades profEspecialidade = null;
  		Integer maiorValor = null;
  		for(final AinEscalasProfissionalInt escala: listaEscalas){
  			final Integer vContaPacRefl = verificarContaPaciente(escala.getId().getPecPreSerVinCodigo(), escala.getId().getPecPreSerMatricula(), escala.getId().getPecPreEspSeq());
  			final Integer valorCapacReferencial = escala.getProfEspecialidade().getCapacReferencial() != null ? escala.getProfEspecialidade().getCapacReferencial(): 0;
  			final Integer ordenacao = valorCapacReferencial - vContaPacRefl;
  			if (maiorValor == null){
  				maiorValor = ordenacao;
  				profEspecialidade = escala.getProfEspecialidade();
  			}
  			else{
  				if (ordenacao > maiorValor){
  					maiorValor = ordenacao;
  					profEspecialidade = escala.getProfEspecialidade();
  				}
  			}
  		}
  		
  		return profEspecialidade;
  	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL
	 */
	public Integer verificarContaPaciente(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){

		//-----------------CURSOR conta_internados--------------------------
		final Integer vContaInternados = this.obterQtdContaInternados(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_bloqueios---------------------------
		final Long vContaBloqueios = this.obterQtdContaBloqueios(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_cti1--------------------------------
		final Integer vContaCti1 = this.obterQtdContaCti1(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_cti2--------------------------------
		final Integer vContaCti2 = this.obterQtdContaCti2(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_cti3--------------------------------
		final Integer vContaCti3 = this.obterQtdContaCti3(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_aptos-------------------------------
		//(COMENTADA NA FUNCTION)
		//Integer vContaAptos = this.obterContaAptos(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_outras_clinicas---------------------
		final Integer vContaOutrasClinicas = this.obterContaOutrasClinicas(serVinCodigo, serMatricula, espSeq);
		
		//-----------------CURSOR conta_outras_unidades---------------------
		final Integer vContaOutrasUnidades = this.obterContaOutrasUnidades(serVinCodigo, serMatricula, espSeq);
		
		
		//REALIZA CÁLCULO
		final Integer vContaPacRefl = vContaInternados.intValue() - vContaBloqueios.intValue() - vContaCti1.intValue()  
		- vContaCti2.intValue() - vContaCti3.intValue() - vContaOutrasClinicas.intValue() - vContaOutrasUnidades.intValue();	
		
		return vContaPacRefl;
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_internados)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterQtdContaInternados(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAghuFacade().obterQtdContaInternados(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_bloqueios)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Long obterQtdContaBloqueios(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterQtdContaBloqueios(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti1)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterQtdContaCti1(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterQtdContaCti1(serVinCodigo, serMatricula, espSeq);
	}

	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti2)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterQtdContaCti2(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterQtdContaCti2(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti3)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterQtdContaCti3(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterQtdContaCti3(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_outras_clinicas)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterContaOutrasClinicas(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterContaOutrasClinicas(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_outras_unidades)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	private Integer obterContaOutrasUnidades(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		return this.getAinInternacaoDAO().obterContaOutrasUnidades(serVinCodigo, serMatricula, espSeq);
	}
	
	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure CONV.FFC_CALCULO_CONTA
	 *  
	 */
	public Integer calcularConta(final Integer conta, final Short cnvCodigo,
			final String desconto) throws ApplicationBusinessException {
		
		if(!ainInternacaoJnDAO.isOracle()) {
		    return 0;
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
	return this.ainAcompanhantesInternacaoDAO.calcularConta(conta, cnvCodigo, desconto, servidorLogado.getUsuario());
	}
	
	
	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure FATP_CTH_PERM_TRC_CNV_AGHU
	 */
	public ValidaContaTrocaConvenioVO validarContaTrocaConvenio(
			final Integer internacaoSeq, final Date dataInternacao,
			final Short cnvCodigo, final Integer phiSeq)
			throws ApplicationBusinessException {
		
		if(!ainInternacaoJnDAO.isOracle()) {
			//Retornado valor default para possibilitar geracao de conta no Postgres
			final ValidaContaTrocaConvenioVO vo = new ValidaContaTrocaConvenioVO();
			vo.setRetorno(1);
		    return vo;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return this.ainAcompanhantesInternacaoDAO.validarContaTrocaConvenio(internacaoSeq, dataInternacao, cnvCodigo, phiSeq, servidorLogado.getUsuario());
	}
	
	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Function FFC_F_VALR_ACOM_CNVN
	 */
	public Double calcularValorAcomodacaoConvenio(
			final Integer codigoAcomodacao, final Short codigoConvenio,
			final Byte codigoPlano, final Date data,
			final Integer numeroAcompanhantes)
			throws ApplicationBusinessException {

		if (!ainInternacaoJnDAO.isOracle()) {
			return null;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return this.ainAcompanhantesInternacaoDAO.calcularValorAcomodacaoConvenio(
				codigoAcomodacao, codigoConvenio, codigoPlano, data,
				numeroAcompanhantes, servidorLogado.getUsuario());
	}

	/**
	 * Método que verifica se uma dada internação possui acompanhantes.
	 * 
	 * ORADB Function AINC_VERIF_ACOMP_INT
	 * 
	 * @param intSeq
	 * @return boolean
	 */
	public int verificarNumeroAcompanhantes(final Integer intSeq) {
		return this.getAinAcompanhantesInternacaoDAO().verificarNumeroAcompanhantes(intSeq);
	}
	
	/**
	 * Método temporário para verificar se existe recem-nascido vinculado ao atendimento.
	 * @param atendimento
	 * @return
	 */
	private boolean possuiRecemNascido(final AghAtendimentos atendimento) {
		return this.getAghuFacade().possuiRecemNascido(atendimento);
	}
	
	public void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException, BaseException {
		
		AinAtendimentosUrgencia atendimentoUrgencia = obterAtendimentoUrgencia(pacCodigo, numeroConsulta, hostName);
	
		Integer seqAtendimento = aghAtendimentoDAO.obterAtendimentoPorConNumero(numeroConsulta);

		AghAtendimentos atendimento = aghAtendimentoDAO.obterAtendimentoPeloSeq(seqAtendimento);
		
		atendimentoUrgencia.setAtendimento(atendimento);
		
		//Consulta C3 - #27542 - Obtém os laudos AIH da consulta selecionada.
		List<MamLaudoAih> listMamLaudoAih = ambulatorioFacade.pesquisarLaudosAihPorConsultaPaciente(numeroConsulta, pacCodigo);

		validarMamLaudoAih(listMamLaudoAih);		
		MamLaudoAih laudoAih = listMamLaudoAih.get(0);		
		
		//Parâmetro internacao referente ao insert I2 - #27542 - Realiza internação da paciente
		AinInternacao internacao = montarObjetoInternacao(pacCodigo, laudoAih, matricula, vinCodigo, atendimentoUrgencia); 
		
		//Parâmetro cidsInternacao referente ao insert I3 - #27542 - Adiciona CID para internação.
		List<AinCidsInternacao> cidsInternacao = montarListaCidsInternacao(laudoAih);	
		
		List<AinCidsInternacao> cidsInternacaoExcluidos = new ArrayList<AinCidsInternacao>();
		
		//Parâmetro listaResponsaveis referente ao insert I4 - #27542 - Insere responsável pela internação de paciente menor de idade.
		List<AinResponsaveisPaciente> listaResponsaveis = montarListaDeResponsaveis(matricula, vinCodigo, trgSeq, pacCodigo); 
		
		List<AinResponsaveisPaciente> listaResponsaveisExcluidos = new ArrayList<AinResponsaveisPaciente>();
		
		final Date dataFimVinculoServidor = new Date();
		final Boolean substituirProntuario = Boolean.FALSE;
		
		internacao.setPaciente(pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo));
		
		this.getCadastroInternacaoON().persistirInternacao(internacao, cidsInternacao, cidsInternacaoExcluidos, listaResponsaveis, 
				listaResponsaveisExcluidos, hostName, dataFimVinculoServidor, substituirProntuario);					
	}
	
	private AinAtendimentosUrgencia obterAtendimentoUrgencia(Integer pacCodigo, Integer numeroConsulta, String hostName) throws BaseException {
		//Consulta C6 - #27542 - Obtém código do atendimento de urgência
		Integer seq = this.getAinAtendimentosUrgenciaDAO().obterSeqAtendimentoUrgenciaPorConsulta(numeroConsulta);
		AinAtendimentosUrgencia atendimentoUrgencia = null;
		
		if(seq != null){
			atendimentoUrgencia = this.getAinAtendimentosUrgenciaDAO().obterPorChavePrimaria(seq);							
		} else {
			//Ingressa o paciente em sala de observação conforme #26857 – RN01.
			atendimentoUrgencia = this.getPacienteFacade().ingressarPacienteEmergenciaObstetricaSalaObservacao(numeroConsulta, pacCodigo, hostName);
		}
		return atendimentoUrgencia;
	}
	
	private void validarMamLaudoAih(List<MamLaudoAih> listMamLaudoAih) throws ApplicationBusinessException {
		if(listMamLaudoAih == null || listMamLaudoAih.isEmpty()){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.MENSAGEM_LAUDOS_NAO_CADASTRADOS_PARA_INTERNACAO);
		}
		
		MamLaudoAih laudoAih = listMamLaudoAih.get(0);
		if(laudoAih.getEspecialidade() == null){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.MENSAGEM_ESPECIALIDADE_NAO_CADASTRADA_NO_LAUDO_ENCONTRADO);
		}
		if(laudoAih.getServidorRespInternacao() == null){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.MENSAGEM_SERVIDOR_NAO_CADASTRADO_NO_LAUDO_ENCONTRADO);
		}
	}
	
	private AinInternacao montarObjetoInternacao(Integer pacCodigo, MamLaudoAih laudoAih, Integer matricula,
			Short vinCodigo, AinAtendimentosUrgencia atendimentoUrgencia) throws ApplicationBusinessException {
		
		AinInternacao internacao = new AinInternacao();
		
		internacao.setPacCodigo(pacCodigo);		
		internacao.setEspecialidade(laudoAih.getEspecialidade());
		RapServidores rapServidorDigita = this.getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(matricula, vinCodigo));
		internacao.setServidorDigita(rapServidorDigita);
		internacao.setServidorProfessor(laudoAih.getServidorRespInternacao());
		internacao.setDthrInternacao(new Date());
		internacao.setEnvProntUnidInt(false);
		
		final AghParametros caraterInternacaoAutomatica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CARATER_INTERNACAO_AUTOMATICA);
		final AinTiposCaraterInternacao tipoCaraterInternacao = this.getCadastroInternacaoON().obterTipoCaraterInternacao(caraterInternacaoAutomatica.getVlrNumerico().intValue());
		internacao.setTipoCaracterInternacao(tipoCaraterInternacao);
		
		final AghParametros convenioInternacaoAutomatica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_INTERNACAO_AUTOMATICA);
		final FatConvenioSaude convenioSaude = convenioSaudeDAO.obterPorChavePrimaria(convenioInternacaoAutomatica.getVlrNumerico().shortValue());
		internacao.setConvenioSaude(convenioSaude);
		
		final AghParametros planoInternacaoAutomatica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PLANO_INTERNACAO_AUTOMATICA);
		final FatConvenioSaudePlano convenioSaudePlano = convenioSaudePlanoDAO.obterPorChavePrimaria(new FatConvenioSaudePlanoId(convenioInternacaoAutomatica.getVlrNumerico().shortValue(), planoInternacaoAutomatica.getVlrNumerico().byteValue()));
		internacao.setConvenioSaudePlano(convenioSaudePlano);
		
		final AghParametros origemInternacaoAutomatica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ORIGEM_INTERNACAO_AUTOMATICA);
		final AghOrigemEventos origemEvento = this.getCadastroInternacaoON().obterOrigemEvento(origemInternacaoAutomatica.getVlrNumerico().shortValue());
		internacao.setOrigemEvento(origemEvento);
		
		internacao.setIndSaidaPac(false);
		internacao.setIndDifClasse(true);
		internacao.setIndPacienteInternado(true);
		internacao.setIndLocalPaciente(DominioLocalPaciente.U);
		
		final AghParametros unidadeObstetricaInternacaoAutomatica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_OBSTETRICA_INTERNACAO_AUTOMATICA);
		final AghUnidadesFuncionais unidadeFuncional = aghUnidadesFuncionaisDAO.obterPorChavePrimaria(unidadeObstetricaInternacaoAutomatica.getVlrNumerico().shortValue());
		internacao.setUnidadesFuncionais(unidadeFuncional);
		
		if(laudoAih.getFatItemProcedHospital() != null && laudoAih.getFatItemProcedHospital().getId() != null){
			internacao.setIphSeq(laudoAih.getFatItemProcedHospital().getId().getSeq());
			internacao.setIphPhoSeq(laudoAih.getFatItemProcedHospital().getId().getPhoSeq());
		}
		
		internacao.setAtendimentoUrgencia(atendimentoUrgencia);	

		return internacao;			
	}
	
	private List<AinCidsInternacao> montarListaCidsInternacao(MamLaudoAih laudoAih) {
		List<AinCidsInternacao> cidsInternacao = new ArrayList<AinCidsInternacao>();
		
		AinCidsInternacao cidInternacao = new AinCidsInternacao();
		
		AinCidsInternacaoId id = new AinCidsInternacaoId();
		
		cidInternacao.setCid(laudoAih.getAghCid());

		id.setCidSeq(cidInternacao.getCid().getSeq());
		
		cidInternacao.setId(id);
		
		cidInternacao.setPrioridadeCid(DominioPrioridadeCid.P);
		
		cidsInternacao.add(cidInternacao);
		
		return cidsInternacao;
	}	
	
	private List<AinResponsaveisPaciente> montarListaDeResponsaveis(Integer matricula, Short vinCodigo, Long trgSeq, Integer pacCodigo) throws ApplicationBusinessException {
		
		List<AinResponsaveisPaciente> listaResponsaveis = new ArrayList<AinResponsaveisPaciente>();
		
		AipPacientes paciente = this.getPacienteFacade().obterPaciente(pacCodigo);
		if(paciente == null){
			throw new ApplicationBusinessException(InternacaoRNExceptionCode.MENSAGEM_PACIENTE_INFORMADO_NAO_CADASTRADO);
		}
		
		if(paciente.getIdade() < MAIOR_DE_IDADE){
			AinResponsaveisPaciente responsavelPaciente = new AinResponsaveisPaciente();
			
			RapServidores servidor = this.getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(matricula, vinCodigo));
			responsavelPaciente.setServidor(servidor);	
			
			responsavelPaciente.setTipoResponsabilidade(DominioTipoResponsabilidade.C);
			responsavelPaciente.setCriadoEm(new Date());
			
			listaResponsaveis.add(responsavelPaciente);
		}

		return listaResponsaveis;		
	}
		
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return this.pesquisaExamesFacade;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected ITransferirPacienteFacade getTransferirPacienteFacade() {
		return this.transferirPacienteFacade;
	}

	protected CadastroInternacaoON getCadastroInternacaoON(){
		return cadastroInternacaoON;
	}
	
	protected DarAltaPacienteON getDarAltaPacienteON(){
		return darAltaPacienteON;
	}

	protected AtendimentoUrgenciaRN getAtendimentoUrgenciaRN(){
		return atendimentoUrgenciaRN;
	}
	
	protected AtualizaInternacaoRN getAtualizaInternacaoRN() {
		return atualizaInternacaoRN;
	}

	protected SubstituirProntuarioAtendimentoRN getSubstituirProntuarioAtendimentoRN() {
		return substituirProntuarioAtendimentoRN;
	}

	protected ValidaInternacaoRN getValidaInternacaoRN() {
		return validaInternacaoRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}	

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AinAcompanhantesInternacaoDAO getAinAcompanhantesInternacaoDAO() {
		return ainAcompanhantesInternacaoDAO;
	}
	
	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}

	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO() {
		return ainEscalasProfissionalIntDAO;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected AinInternacaoJnDAO getAinInternacaoJnDAO() {
		return ainInternacaoJnDAO;
	}

	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO() {
		return ainSolicTransfPacientesDAO;
	}

	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}