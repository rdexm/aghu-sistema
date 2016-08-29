package br.gov.mec.aghu.internacao.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalizacaoPaciente;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProcedenciaPacientes;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioTipoDadosAltaPaciente;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.faturamento.vo.AinMovimentosInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.ReInternacoesVO;
import br.gov.mec.aghu.indicadores.vo.ReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.business.vo.EscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.business.vo.FlagsValidacaoDadosAltaPacienteVO;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PacientesInternadosUltrapassadoVO;
import br.gov.mec.aghu.internacao.vo.AltasPorPeriodoVO;
import br.gov.mec.aghu.internacao.vo.AltasPorUnidadeVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaPorEtniaVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaVO;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.PacienteIdadeCIDVO;
import br.gov.mec.aghu.internacao.vo.PacienteUnidadeFuncionalVO;
import br.gov.mec.aghu.internacao.vo.PacientesAniversariantesVO;
import br.gov.mec.aghu.internacao.vo.PacientesComObitoVO;
import br.gov.mec.aghu.internacao.vo.PesquisaSituacoesLeitosVO;
import br.gov.mec.aghu.internacao.vo.ProcedenciaPacientesInternadosVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasDiaVO;
import br.gov.mec.aghu.internacao.vo.RelatorioBoletimInternacaoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioSolicitacaoInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ResponsaveisPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.ValidaContaTrocaConvenioVO;
import br.gov.mec.aghu.internacao.vo.VerificaPermissaoVO;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinCrachaAcompanhantes;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinQuartos.Fields;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAinConvenioPlano;

@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation"})
@Local
public interface IInternacaoFacade extends Serializable {
	
	public void verificarEscalaProfissional(String nomeMicrocomputador, final RapServidores servidorLogado, final Date dataFimVinculoServidor) 
			throws ApplicationBusinessException;
	
	public void atualizaPrevisaoAltaInternacao(AinInternacao internacao,
			Date dtPrevAlta, Boolean confirmacaoModalPrevPlanoAlta, Boolean considerarValorModalPlanoPrevAlta, Boolean bloquearData) throws BaseException;

	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica);

	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(
			Object strParam, Integer matriculaProfessor,
			Short vinCodigoProfessor);

	/**
	 * Este método realiza a pesquisa de CRM Professores para a tela de
	 * internação
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , convenioPlano, strParam
	 * @return listaLOV
	 */
	
	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(Short espSeq,
			String espSigla, Short cnvCodigo, Boolean indVerfEscalaProfInt,
			String strParam, Integer matriculaProfessor,
			Short vinCodigoProfessor);

	/**
	 * Este método monta e retorna apenas um objeto ProfessorCrmInternacaoVO
	 * 
	 * @param professorEspecialidade
	 *            , cnvCodigo
	 * @return ProfessorCrmInternacaoVO
	 */
	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(
			RapServidores servidorProfessor, AghEspecialidades especialidade,
			Short cnvCodigo);

	public AinInternacao obterInternacao(Integer seqInternacao);
	
	public AinInternacao obterInternacaoPorChavePrimaria(Integer seqInternacao, Enum[] innerArgs, Enum[] leftArgs);
	
	Integer obterCodigoCaraterInternacaoPorAinInternacao(Integer seq);

	
	public AinInternacao obterAinInternacaoPorChavePrimaria(
			Integer chavePrimaria, Enum... fields);

	public AinInternacao obterAinInternacaoPorChavePrimariaProcessamentoCusto(Integer chavePrimaria);
	
	public List<Object[]> pesquisarDatas(Integer prontuario, Date dataInternacao);

	/**
	 * Primeiro MovimentosInternacao filtrando pelo maior seq de Internacao de uma
	 * ContaInternacao e também pela DthrLancamento.
	 * 
	 * @param cthSeq
	 * @param dthrLancamento
	 * @return List<AinMovimentosInternacao>
	 */
	public AinMovimentosInternacao buscarPrimeiroMovimentosInternacaoPorMaiorInternacaoDeContaInternacaoEDthrLancamento(
			Integer cthSeq, Date dthrLancamento);

	public List<AinMovimentosInternacao> listarMovimentosInternacao(
			Integer intSeq, Date dtIniAtd, Date dtFimCta,
			Integer[] codigosTipoMovimentoInternacao);

	public List<AinDiariasAutorizadas> pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(
			Integer seqInternacao);

	public void atualizarInternacao(AinInternacao internacao,
			String nomeMicrocomputador,
			RapServidores servidorLogado, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, boolean umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL)
			throws BaseException;

	public void atualizarInternacaoComFlush(AinInternacao internacao,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario)
			throws BaseException;
	
	/**
	 * Busca o Servidor pela matricula e vinculo e então chama o método atualizarInternacao passando o mesmo
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
	String atualizarServidorProfessorInternacao(final Integer seqInternacao, final Integer matriculaProfessor, final Short vinCodigoProfessor, String nomeMicrocomputador,
			final Integer matriculaServidorLogado, final Short vinCodigoServidorLogado);

	void estornarAltaPaciente(final Integer seqInternacao, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores servidor) throws BaseException;
	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException
	 * 
	 *  Invocar este método caso tenha algum flush antes do final da transacao
	 */
	public void atualizarInternacao(AinInternacao internacao,
			AinInternacao internacaoOld, String nomeMicrocomputador, RapServidores servidor,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException;

	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException
	 * 
	 *  Invocar este método caso tenha algum flush antes do final da transacao
	 */
	
	public void atualizarInternacao(AinInternacao internacao,
			AinInternacao internacaoOld, boolean flush,
			String nomeMicrocomputador,RapServidores servidor,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException;

	
	public void inserirInternacao(AinInternacao internacao,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario)
			throws BaseException;

	public boolean gravarDataAltaPaciente(
			FlagsValidacaoDadosAltaPacienteVO flagsValidacaoDadosAltaPacienteVO, VerificaPermissaoVO verificaPermissaoVO,
			DominioTipoDadosAltaPaciente tipoDadoAltaPaciente,
			AinInternacao internacao, AinTiposAltaMedica tipoAltaMedica,
			AghInstituicoesHospitalares instituicaoHospitalar,
			Date dthrAltaMedica, Date dtSaidaPaciente, Integer docObito,
			boolean dtSaidaPacBD, boolean dthrAltaMedicaBD,
			boolean tipoAltaMedicaBD, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	/**
	 * traducao da function ainc_alta_medica
	 * 
	 * @param internacaoSeq
	 * @return
	 */
	
	public DadosAltaSumarioVO buscarDadosAltaMedica(Integer internacaoSeq)
			throws ApplicationBusinessException;

	public Long pesquisaInternacoesParaDarAltaPacienteCount(
			Integer prontuario);

	public List<AinInternacao> pesquisaInternacoesParaDarAltaPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer prontuario);

	public List<AinMovimentosInternacaoVO> listarMotivosInternacaoVOOrdenadoPorDthrLancamento(
			Integer intSeq, Date dtInicio, Date dtFim,
			Integer[] tiposMovimentoIgnorados);

	public List<AinMovimentosInternacao> listarMotivosInternacaoPorInternacaoECriadoem(
			Integer intSeq, Date criadoEm, String colunaOrder, Boolean order);

	public AinDiariasAutorizadas buscarPrimeiraDiariaAutorizada(Integer cthSeq);

	public AinResponsaveisPaciente buscaResponsaveisPaciente(Integer intSeq);
	
	
	public AinResponsaveisPaciente obterResponsaveisPaciente(Integer intSeq);
	
	public void atualizaResponsaveisPaciente(AinResponsaveisPaciente responsavelPaciente);

	public List<AinLeitos> obterLeitosAtivosPorUnf(Object strPesquisa,
			Short unfSeq);

	public Long obterLeitosAtivosPorUnfCount(Object strPesquisa, Short unfSeq);

	public List<AinQuartos> listarQuartosPorUnidadeFuncionalEDescricao(
			String descricao, Short unfSeq, Boolean orderAsc,
			Fields[] atributosOrder);

	public Long sugereNumeroDoCracha(final String usuario) throws ApplicationBusinessException;
	
	public AinAtendimentosUrgencia pesquisaAtendimentoUrgencia(
			Integer sequence, Integer prontuario);

	public AinInternacao pesquisaInternacao(Integer sequence, Integer prontuario);

	public Long pesquisaAtendimentoUrgenciaCount(Integer sequence,
			Integer prontuario);

	public Long pesquisaInternacaoCount(Integer sequence, Integer prontuario);

	public List<AinAtendimentosUrgencia> listarAtendimentosUrgencia(
			Integer sequence, Integer prontuario);

	public List<AinInternacao> listarInternacoes(Integer sequence,
			Integer prontuario);

	public PesquisaSituacoesLeitosVO pesquisaSituacoesLeitos(AghClinicas clinica);

	public SituacaoLeitosVO pesquisaSituacaoSemLeitos();

	public SituacaoLeitosVO pesquisaCapacidadeInstaladaLeitos(
			AghClinicas clinica);

	public AinInternacao obterUltimaInternacaoPaciente(Integer codigo);

	public AinQuartos pesquisaQuartoPorNumero(Short qrtNumero);

	public AinQuartos pesquisaQuartoPorLeitoID(String ltoLtoId);

	/**
	 * 
	 * @param seqInternacao
	 * @param codigoObservacaoPacAlta
	 * @throws ApplicationBusinessException
	 */
	
	public void associarObservacaoPacAlta(Integer seqInternacao,
			Integer codigoObservacaoPacAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeInternacao();
	Long pesquisarInternacoesAvisoSamisUnidadeInternacaoCount();

	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeLeito();
	Long pesquisarInternacoesAvisoSamisUnidadeLeitoCount();

	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeQuarto();
	Long pesquisarInternacoesAvisoSamisUnidadeQuartoCount();

	public Short recuperarEnderecoCorrespondencia(Integer pacCodigo);

	public void inserirMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto);

	public void removerMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto)
			throws ApplicationBusinessException;

	public DominioSexoDeterminante obterSexoDeterminanteQuarto(Short numero);

	public DominioSimNao obterIndConSexoQuarto(Short numero);

	public Short obterNumeroQuartoPorLeito(String leitoID);

	public void verificarPermissaoAltaPaciente(Integer intSeq,
			Date intDthrAltaMedica, VerificaPermissaoVO vo);

	
	public List<RelatorioAltasDiaVO> pesquisaAltasDia(Date dataAlta);
	
	public List<AltasPorPeriodoVO> pesquisarAltasNoPeriodo(Date dataAltaInicio, Date dataAltaFinal) throws ApplicationBusinessException;

	
	public List<PacienteUnidadeFuncionalVO> pesquisaPacientesPorUnidadesFuncionais(
			AghUnidadesFuncionais unidadeFuncional,
			DominioOrdenacaoRelatorioPacientesUnidade ordenacao);

	public List<PacienteUnidadeFuncionalVO> pesquisaPacientesPorUnidadesFuncionaisComAcompanhantes(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao)
			throws ApplicationBusinessException;
	
	Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional);
	
	public List<AltasPorUnidadeVO> pesquisaAltasPorUnidade(
			Date dataDeReferencia, DominioGrupoConvenio grupoConvenio,
			Integer codigoUnidadesFuncionais) throws ApplicationBusinessException;

	/**
	 * Realiza a pesquisa de pacientes com obito.
	 * 
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param idadeInicial
	 * @param idadeFinal
	 * @param sexo
	 * @return
	 */
	
	public List<PacientesComObitoVO> pesquisaPacientesComObito(
			Date dtInicialReferencia, Date dtFinalReferencia,
			Integer idadeInicial, Integer idadeFinal, DominioSexo sexo);

	/**
	 * Realiza a pesquisa de procedencia de pacientes internados
	 * @param localizacaoPaciente
	 * @param ordenacaoProcedenciaPacientes
	 * @return
	 */
	
	public List<ProcedenciaPacientesInternadosVO> pesquisaProcedenciaPacientesInternados(
			DominioLocalizacaoPaciente localizacaoPaciente,
			DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes);

	/**
	 * Realiza a pesquisa de pacientes aniversáriantes.
	 * 
	 * @param {Date} dtReferencia
	 * @return {List} PacientesAniversariantesVO
	 */
	
	public List<PacientesAniversariantesVO> pesquisaPacientesAniversariantes(
			Date dtReferencia);

	/**
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @param intSeq
	 * @return
	 */
	
	public AinInternacao obterInternacaoPorSeq(Integer intSeq, Enum... inners);


	/**
	 * Método resposável por buscar Instituicoes Hospitalares, conforme a string
	 * passada como parametro, que é comparada com o codigo e a nome da
	 * Instituicao Hospitalar É utilizado pelo converter
	 * AghInstituicoesHospitalaresConverter.
	 * 
	 * @param nome
	 *            ou codigo
	 * @return Lista de AghInstituicoesHospitalares
	 */
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(
			Object objPesquisa);

	public void insereExtratoLeito(AinLeitos leito, RapServidores digita,
			RapServidores professor, Date dataHoraInternacao,
			Date dataHoraUltimoEvento, AinInternacao internacao, boolean ocupa)
			throws ApplicationBusinessException;

	public void registraExtratoLeito(String leitoID, Integer seqInternacao,
			Date dataHoraAltaMedica, Date dataHoraUltimoEvento,
			DominioTransacaoAltaPaciente transacao) throws ApplicationBusinessException;

	public Long pesquisarProfissionaisEscalaCount(Short vinculo,
			Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio)
			throws ApplicationBusinessException;

	
	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(
			Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException;

	/**
	 * Método utilizado para obter uma instância de AinEscalasProfissionalInt
	 * pela chave primária composta.
	 * 
	 * @param pecPreSerMatricula
	 * @param pecPreSerVinCodigo
	 * @param pecCnvCodigo
	 * @param pecPreEspSeq
	 * @param dtInicio
	 * @return
	 */
	public AinEscalasProfissionalInt obterProfissionalEscala(
			Integer pecPreSerMatricula, Integer pecPreSerVinCodigo,
			Integer pecCnvCodigo, Integer pecPreEspSeq, Date dtInicio);

	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	public List<RapServidores> pesquisarProfissionaisSubstitutos(
			Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV) throws ApplicationBusinessException;

	/**
	 * Método utilizado para adicionar uma nova escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	
	public void incluirEscala(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws BaseException;

	/**
	 * Método utilizado para alterar uma escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	
	public void alterarEscala(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws BaseException;

	public Long pesquisarEscalaCount(Short vinculo, Integer matricula,
			Integer seqEspecialidade, Short codigoConvenio, Date dataInicio,
			Date dataFim);

	
	public List<EscalaIntenacaoVO> montarEscalaVO(Short vinculo,
			Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException;

	public void verificaPacienteInternado(Integer intSeq)
			throws ApplicationBusinessException;

	
	public List<AinInternacao> pesquisaInternacoesParaEstornarAltaPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer prontuario);

	public Long pesquisaInternacoesParaEstornarAltaPacienteCount(
			Integer prontuario);

	/**
	 * Método usado para remover os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	
	public void removerAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacao)
			throws ApplicationBusinessException;

	/**
	 * ORADB: procedure AINP_ENFORCE_INT_RULES (p_event = 'DELETE')
	 * 
	 * @param intSaved
	 * @throws ApplicationBusinessException
	 */
	public void enforceDelete(AinInternacao intSaved, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	public void desatacharObjetoInternacao(AinInternacao internacao);

	/**
	 * ORADB: Trigger AINT_INT_ARD
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public void posDelete(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(Short cnvCodigo);

	
	public AinAtendimentosUrgencia obterAtendimentoUrgencia(
			Integer ainAtendimentoUrgenciaSeq);

	/**
	 * Método que obtém a Origem de internação para quando o paciente já se
	 * encontra em atendimento de emergência.
	 * 
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	public AghOrigemEventos obterOrigemInternacaoAtendimentoEmergencia(
			Short unfSeq) throws ApplicationBusinessException;

	/**
	 * Método que obtem um procedimentoHospitalar pelo seu id
	 * 
	 * @param phoSeq
	 *            , seq
	 * @return FatItensProcedHospitalar
	 */
	public FatItensProcedHospitalar obterItemProcedimentoHospitalar(
			Short phoSeq, Integer seq);

	
	public FatConvenioSaudePlano obterConvenioSaudePlano(
			FatConvenioSaudePlanoId id);

	/**
	 * Método responsável pela persistência de uma internação.
	 * 
	 * @param internacao
	 *            , listaResponsaveis, listaResponsaveisExcluidos
	 * @throws ApplicationBusinessException
	 */
	
	public AinInternacao persistirInternacao(AinInternacao internacao,
			List<AinCidsInternacao> cidsInternacao,
			List<AinCidsInternacao> cidsInternacaoExcluidos,
			List<AinResponsaveisPaciente> listaResponsaveis,
			List<AinResponsaveisPaciente> listaResponsaveisExcluidos,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException;

	/**
	 * Método que valida quando o CNRAC deve ser informado para paciente com
	 * endereço em outro estado
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public boolean validarPacienteOutroEstado(AipPacientes paciente,
			Integer iphSeq, Short iphPhoSeq) throws ApplicationBusinessException;

	/**
	 * Este método realiza a pesquisa de Convênios para a tela de internação
	 * 
	 * @param strParam
	 * @return listaLOV
	 */
	
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoInternacao(
			Object strParam);

	/**
	 * Pesquisa de projeto de pesquisa por descrição ou código
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			String strPesquisa, Integer codigoPaciente);

	
	public List<AinTiposCaraterInternacao> pesquisarCaraterInternacao(
			String strPesquisa);

	public AghProfEspecialidades sugereProfessorEspecialidade(Short espSeq,
			Short cnvCodigo);

	/**
	 * ORADB ainp_verifica_cirurgia Método que obtém o procedimento cirúrgico de
	 * um paciente, caso haja algum no período de até 7 dias antes da data de
	 * internação.
	 * 
	 * @param pacCodigo
	 * @param dtInternacao
	 * @return
	 */
	
	public MbcCirurgias obterProcedimentoCirurgicoInternacao(Integer pacCodigo,
			Date dtInternacao);

	/**
	 * Método delegado para a RN de Solicitação Internação
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq);

	/**
	 * Método que obtém a UF correspondente ao HU
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterUfSedeHU() throws ApplicationBusinessException;

	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @param especialidade
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean consistirClinicaMensagemConfirmacao(AinLeitos leito,
			AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional,
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário (somente para
	 * especialidade este)
	 * 
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean consistirEspecialidadeMensagemConfirmacao(AinLeitos leito,
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	/**
	 * Regra que verifica se deve ser informado o número CERIH para a internação
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarInformarNumeroCERIH(Integer iphSeq,
			Short iphPhoSeq, Short cnvCodigo, Integer seqInternacao)
			throws BaseException;

	/**
	 * Consiste para que seja informada a acomodação autorizada para internações
	 * de convênio (AGUARDANDO MELHOR LOCAL PARA CHAMADA DESTE MÉTODO)
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarAcomodacaoInternacaoConvenio(
			AinInternacao internacao);

	/**
	 * ORADB ainp_consiste_matr_conv Verifica se deve ser informada a matrícula
	 * do convênio do paciente
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	
	public boolean verificarConvenioSaudePaciente(AipPacientes paciente,
			Short cnvCodigo);

	/**
	 * Método para verificar se o convenio é um convenio SUS. Esse método é a
	 * implementação da procedura AINC_CONVENIO_SUS do form AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            do convenio
	 * @return true/false
	 */
	public Boolean verificarConvenioSus(Short codigoConvenio);

	/**
	 * Método para executar as regras de triggers de AIN_CIDS_INTERNACAO.
	 * 
	 * ORADB Trigger AINT_CDI_ASI
	 * 
	 * @param cidInternacao
	 * @param operacao
	 * @throws BaseException
	 */
	public void executarTriggersCid(AinCidsInternacao cidInternacao,
			DominioOperacoesJournal operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	/**
	 * ORADB ainp_sugere_prof_esp
	 */
	public AghProfEspecialidades verificarEscalaProfessor(Short espSeq,
			Short cnvCodigo);

	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure FATP_CTH_PERM_TRC_CNV_AGHU
	 */
	public ValidaContaTrocaConvenioVO validarContaTrocaConvenio(
			final Integer internacaoSeq, final Date dataInternacao,
			final Short cnvCodigo, final Integer phiSeq)
			throws ApplicationBusinessException;

	public AinInternacao obterInternacaoAnterior(Integer internacao);

	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure CONV.FFC_CALCULO_CONTA
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Integer calcularConta(final Integer conta, final Short cnvCodigo,
			final String desconto) throws ApplicationBusinessException;

	/**
	 * Método para atualizar o convênio/plano dos exames
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_EXME
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConvenioPlanoExames(Integer seqAtendimento,
			Short codigoConvenio, Byte seqConvenioSaudePlano, Date data, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	/**
	 * Método para atualizar o convênio/plano das cirurgias
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_CIRG
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConvenioPlanoCirurgias(Integer seqAtendimento,
			Short codigoConvenio, Byte seqConvenioSaudePlano, Date data)
			throws ApplicationBusinessException;

	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			String strPesquisa);

	
	public ConvenioPlanoVO obterConvenioPlanoVO(Short cnvCodigo, Byte seq);

	/**
	 * Método que atualiza a conta hospitalar da internação quando a acomodação
	 * for informada
	 * 
	 * @param contaHospitalar
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarContaHospitalar(FatContasHospitalares contaHospitalar);

	
	public AinInternacao alterarConvenioInternacao(AinInternacao internacao,
			FatContasHospitalares contaHospitalar,
			final Integer seqContaHospitalarOld, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean encerraConta) throws BaseException;

	
	public void substituirPacienteAtendimentoUrgencia(
			AipPacientes pacienteOrigem, AipPacientes pacienteDestino,
			AinAtendimentosUrgencia atendimentoUrgencia, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	
	public AinAtendimentosUrgencia obterAtendUrgencia(AipPacientes paciente)
			throws ApplicationBusinessException;

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	public void validaDataAlta(Date dtAlta, Date dtAtendimento)
			throws ApplicationBusinessException;

	/**
	 * Obtem lista de crachás por acompanhante.
	 * 
	 * @param acompanhanteInternacao
	 * @return Lista de crachás.
	 */
	
	public List<AinCrachaAcompanhantes> obterCrachasAcompanhantes(
			AinAcompanhantesInternacao acompanhanteInternacao);

	/**
	 * Valida datas.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void validaCamposAtualizaAcompanhantes(Date dtInicio, Date dtFim,
			String nomeAcomp) throws ApplicationBusinessException;

	/**
	 * Método usado para persistir os cracha dos acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirCrachaAcompanhantes(
			List<AinCrachaAcompanhantes> crachasAcompanhantes,
			AinAcompanhantesInternacao acompanhanteInternacao,
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao) throws ApplicationBusinessException;

	/**
	 * Método usado para persistir os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao) throws ApplicationBusinessException;

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_SEXO_QUARTO
	 * 
	 * Atualiza Sexo Quarto
	 * 
	 * Coloca null no sexo de ocupação do quarto quando este se tornar liberado
	 * após a saída de um paciente.
	 */
	public void atualizarSexoQuarto(String paramLeitoId, Short paramNumeroQuarto)
			throws ApplicationBusinessException;

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 */
	public String obtemLocalOrigem(Integer internacaoSeq, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL .
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinal(Integer internacaoSeq, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO.UNF_SEQ_ORIGEM
	 * 
	 * Obtem local de origem (sequence).
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Short obtemLocalOrigemSeq(Integer internacaoSeq, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obtemLocalDestino(Integer internacaoSeq, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_T .
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinalT(Integer internacaoSeq, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_LTO
	 * 
	 * Obtem data final leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinalLeito(String leitoId, Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_NOVA .
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinalNova(Integer atendimentoUrgenciaSeq,
			Date dataHora, Integer movimentoSeq, Date criadoEm);

	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obtemLocalOrigemUrgencia(Integer atendimentoUrgSeq,
			Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL .
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinalUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_T .
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date obtemDthrFinalTUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora);

	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obtemLocalDestinoUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora);

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_FIN
	 * 
	 * Obtem data final.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date buscaDthrFinalInternacao(Integer intSeq);

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_MOV
	 * 
	 * Obtem local de origem.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obtemOrigemMovimentacao(Integer intSeq);

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_LTO
	 * 
	 * Obtem data final leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date buscaDthrLeito(String leitoId);

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_ATU
	 * 
	 * Obtem data atendimento de urgencia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date buscaDthrAtendimentoUrgencia(Integer atendimentoUrgenciaSeq);

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_ATU
	 * 
	 * Obtem local de origem do atendimento de urgencia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obtemOrigemAtendimentoUrgencia(Integer atendimentoUrgenciaSeq);

	/**
	 * Método para retornar o código do tipo de movimento do leito
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.COD_BLOQUEIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Short obterCodigoBloqueioLB(String leitoId, Date data);

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public DominioSimNao obterSituacaoPacienteLB(String leitoId, Date data);

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataInicialLB(String leitoId, Date data);

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_FINAL_L
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataFinalLancamentoLB(String leitoId, Date data);

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public DominioSimNao obterSituacaoPacienteLD(String leitoId, Date data);

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataInicialLD(String leitoId, Date data);

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_FINAL_L
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataFinalLancamentoLD(String leitoId, Date data);

	

	public void testVerificarEscalaProfissional(Date date, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Método para chamada da impressão do Boletim de Internação. Este método é
	 * a implementação do procedure AINP_CHAMA_BOLETIM do form
	 * AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            da internação
	 * @return RelatorioBoletimInternacaoVO com todos campos setados de acordo
	 *         com busca para geração do relatório
	 */
	
	public RelatorioBoletimInternacaoVO imprimirRelatorioBoletimInternacao(
			Integer seqInternacao) throws ApplicationBusinessException;

	
	public List<RelatorioSolicitacaoInternacaoVO> obterSolicitacoesInternacao(
			Date criadoEm,
			DominioSituacaoSolicitacaoInternacao indSitSolicInternacao,
			AghClinicas clinica, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio)
			throws ApplicationBusinessException;

	
	public List<ResponsaveisPacienteVO> obterRelatorioResponsaveisPaciente(
			Integer intSeq) throws ApplicationBusinessException;

	
	public Map<String, Object> getParematrosRelatorioLocalizadorPaciente(
			Integer codPaciente);

	public AinLeitos obterLeitoPorId(String leitoID);

	public AinQuartos obterQuartoPorId(Short numero);

	public Boolean habilitarDadosControle(final Integer cPacCodigo,
			final Integer atdSeq);

	public void evict(BaseEntity object);

	public List<AinQuartos> pesquisaQuartos(String strPesquisa);

	public Long listarQuartosPorUnidadeFuncionalEDescricaoCount(String descricao,
			Short unfSeq, Boolean orderAsc);

	public AinInternacao obrterInternacaoPorPacienteInternado(Integer pacCodigo);

	public List<AinInternacao> pesquisarInternacaoIndependenteEspecialidade(
			Integer pacCodigo, Date dtConsulta, Date dataLimite, Short pgdSeq);

	public List<AinInternacao> pesquisarInternacaoMesmaEspecialidade(
			Integer codigo, Date dtConsulta, Date dataLimite, Short pgdSeq);

	public List<AinInternacao> pesquisarInternacaoOutrasEspecialidades(
			Integer pacCodigo, Date dtConsulta, Short espSeqGen,
			Date dataLimite, Short pgdSeq);

	public List<AinInternacao> pesquisarInternacaoEspecialidadeDiferente(
			Integer pacCodigo, Date dtConsulta, Short espSeqGen,
			Date dataLimite, Short pgdSeq);

	public AinAtendimentosUrgencia obterUltimoAtendimentosUrgencia(
			Integer aipPacientesCodigo);

	public List<AinAtendimentosUrgencia> pesquisarAinAtendimentosUrgencia(
			Integer pacCodigo);

	public List executarCursorAtu(Integer atuSeq);

	public List<Date> executarCursorAtu2(Integer atuSeq);

	public List executarCursorUrgencia(Integer atuSeq);

	public AinAtendimentosUrgencia obterAinAtendimentosUrgenciaPorChavePrimaria(
			Integer seq);

	public void persistirAinExtratoLeitos(AinExtratoLeitos ainExtratoLeitos);

	public List<AinExtratoLeitos> listarExtratosLeitosPorCodigoPaciente(
			Integer pacCodigo);

	public List executarCursorInt(Integer intSeq);

	public List<Date> executarCursorInt2(Integer intSeq);

	public List<AinInternacao> pesquisarInternacoesPorPaciente(
			AipPacientes paciente);

	public List<AinInternacao> pesquisarAinInternacao(Integer pacCodigo);

	public AinQuartos obterAinQuartosPorChavePrimaria(Short numeroQuarto);

	public AinLeitos obterAinLeitosPorChavePrimaria(String leitoID);

	public List<AinSolicitacoesInternacao> listarSolicitacoesInternacaoPorCodigoPaciente(
			Integer pacCodigo);

	public List<AhdHospitaisDia> listarHospitaisDiaPorCodigoPaciente(
			Integer pacCodigo);

	public AhdHospitaisDia obterUltimoAtendimentoHospitaisDia(
			Integer aipPacientesCodigo);

	public AhdHospitaisDia obterAhdHospitaisDiaPorChavePrimaria(Integer seq);

	public List executarCursorHod(Integer hodSeq);

	public List<Date> executarCursorHod2(Integer atuSeq);

	public void persistirAhdHospitaisDia(AhdHospitaisDia ahdHospitaisDia);

	public List<ProcEfet> listarProcEfetPorCtacvNro(Integer ctacvNro);

	public void removerProcEfet(ProcEfet procEfet, boolean flush);

	public void desatacharProcEfet(ProcEfet procEfet);

	public void inserirProcEfet(ProcEfet procEfet, boolean flush);

	public List<VAinConvenioPlano> pesquisarConveniosAtivos(
			final Object objPesquisa);

	public VAinConvenioPlano obterVAinConvenioPlanoAtivoPeloId(
			final byte plano, final short cnvCodigo);

	public VAinConvenioPlano obterVAinConvenioPlanoSus(
			final AghParametros paramPlano, final AghParametros paramConvenio)
			throws BaseException;

	public String obterCaracteristicaLeito(String pLtoiD, String pTipo);

	public List<AinTiposCaraterInternacao> pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
			Object filtros);

	public Long pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(
			Object filtros);

	public String obterDescTipoMvtoPorCodigo(Short codBloq);

	public Date obterUltimoIndicadorHospitalarGerado();

	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(
			Date mesCompetencia, Date mesCompetenciaFim,
			DominioTipoIndicador tipoIndicador,
			AghUnidadesFuncionais unidadeFuncional);

	public boolean existeIndicadorParaCompetencia(Date anoMesCompetencia,
			DominioTipoIndicador tipoIndicador);
	
	public void removerIndicadorPorCompetenciaTipoIndicador(Date anoMesCompetencia,
			DominioTipoIndicador tipoIndicador);
	
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorAreaFuncional(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map);
	
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorClinica(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map);
	
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorEspecialidade(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map);
	
	public Integer obterIndicadoresResumidosSeq();

	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaGeral(
			Integer quantidadeDiasMes);

	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaPorUnidadeFuncional(
			Integer quantidadeDiasMes);

	public void obterSaidasGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map);

	public void obterSaidasPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map);

	public void obterSaidasPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map);

	public void obterSaidasPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map);

	public void obterPacientesDiaGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterPacientesDiaPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterPacientesDiaPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterPacientesDiaPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterObitosGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterObitosPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterObitosPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void obterObitosPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException;

	public void inserirAinIndicadorHospitalarResumido(
			AinIndicadorHospitalarResumido ainIndicadorHospitalarResumido);

	public List<AinIndicadoresHospitalares> pesquisarQuery5IndicadoresClinica(
			Date mes);

	public List<AinIndicadoresHospitalares> pesquisarQuery4IndicadoresClinica(
			Date mes);

	public List<AinIndicadoresHospitalares> pesquisarQuery3IndicadoresClinica(
			Date mes);

	public List<AinIndicadoresHospitalares> pesquisarQuery2IndicadoresClinica(
			Date mes);

	public List<AinIndicadoresHospitalares> pesquisarQuery1IndicadoresClinica(
			Date mes);

	public List<AinIndicadoresHospitalares> pesquisarIndicadoresGeraisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia);

	public List<AinIndicadoresHospitalares> pesquisarIndicadoresTotaisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia);

	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mesCompetencia);

	public Integer obterNumeroOcorrenciasIndicadoresUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia);

	public void removerPorCompetencia(Date anoMesComp);

	public List<AinLeitos> obterCapacUnidPriv();

	public void inserirAinIndicadoresHospitalares(
			AinIndicadoresHospitalares ainIndicadoresHospitalares);

	public List<AinExtratoLeitos> pesquisarExtratosLeito(Date mesCompetencia);

	public List<Object[]> pesquisarExtratosLeitoPorTipoMovs(
			Date mesCompetencia, Object[] dominioMvtsLeitos);

	public abstract List<ReInternacoesVO> obterReInternacoesVO();

	public abstract String obterLeitoContaHospitalar(final Integer cthSeq);

	public Date obterDataInternacao(final Integer intSeq);

	public List<Integer> obterListNumeroQuartoPorCodigoClinica(
			Integer codigoClinica);

	public AinQuartos obterQuartoDescricao(String descricao);

	public List<AinLeitos> pesquisarLeitosAtivos();

	public int contarLeitosAtivosPorListaQuartos(List<Integer> listNroQuarto);

	public List<Object[]> obterLeitosTipoMovsLeitos();

	public List<AinMovimentosInternacao> pesquisarMovimentoInternacao(
			final Integer seqInternacao, final Date dataLancamento,
			final Date dataAlta);

	public List<Object> pesquisarMovtsInternacao(
			final List<Integer> tipoMovimentoInternacaoList,
			final Date mesCompetencia, final Date ultimoDiaMes,
			final Date diaMaxMesMaisUm);

	public AinMovimentosInternacao obterMovimentoInternacao(final Integer seq);

	public Date obterDataAtendimento(Integer atuSeq);
	
	public List<ReferencialClinicaEspecialidadeVO> pesquisarReferencialClinicaEspecialidade(
			Integer codigoClinica, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);	
	
	String obterTelefonePaciente(Integer pacCodigo);
	
	public AinInternacao obterInternacaoConvenio(final Integer intSeq);
	
	public AinAtendimentosUrgencia obterAtendimentoUrgenciaConvenio(final Integer atuSeq);
	
	public AhdHospitaisDia obterHospitalDiaConvenio(final Integer hodSeq);

	Long pesquisarInternacoesPorPacienteCount(AipPacientes paciente);

	public String obterDescricaoConvenioPlano(Byte cspSeq,Short cspCnvCodigo);

	AinInternacao obterInternacaoPorAtendimentoPacCodigo(Integer codigo);
	
	Boolean verificarCaracteristicaEspecialidade(Short seq, DominioCaracEspecialidade caracteristica);
	
	AinInternacao obterInternacaoPorSequencialPaciente(final Integer seq, final Integer pacCodigo);
		
	AinAtendimentosUrgencia obterAtendimentoUrgenciaPorSequencialPaciente(final Integer seq, final Integer pacCodigo);
	
	VAinConvenioPlano obterVAinConvenioPlanoPeloId(byte plano, short cnvCodigo);

	String obterConvenioPlano(Byte cspSeq, Short cspCnvCodigo);

	/**
	 * Método que retorna a lista de movimentos de uma internacao que tenha a sua data de lancamento menor que a dtSolicitacaoExame recebida 
	 * e a data de lancamento entre as datas dtInicioProcessamento e dtFimProcessamento.
	 * 
	 * @author rmalvezzi
	 * @param seqInternacao						Identificador da internação;
	 * @param dtInicioProcessamento				Data do Início do Processamento;
	 * @param dtFimProcessamento				Data de Fim do Processamento
	 * @param dtSolicitacaoExame				Data da solicitação do examete, se for diferente de nula o resultado é ordenado DESC;
	 * @return List<AinMovimentosInternacao> 	Lista dos movimentos filtrados pelos paramêtros ordenada DESC se dtSolicitacaoExame 
	 * 										 	for diferente de nula caso o contrario ASC;
	 */
	List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtInicioProcessamento, Date dtFimProcessamento, Date dtHoraLancamento);
		
	/**
	 * Método que retorna a lista de movimentos de uma internacao que tenha a sua data de lancamento menor que a data recebida.
	 * 
	 * @author rmalvezzi
	 * @param seqInternacao						Identificador da internação;
	 * @param dtInicioProcessamento				Data do Início do Processamento;	
	 * @return 									List<AinMovimentosInternacao> 	
	 */
	List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtInicioProcessamento);
	
	public void verificarNecessidadeInformarDiaria(Integer seqInternacao, Short codigoConvenio) throws ApplicationBusinessException;

	public AinInternacao obterAinInternacaoPorOrigemEAtdSeq(DominioOrigemAtendimento i, Integer seq);
	
	public AinInternacao obterAinInternacaoPorAtdSeq(Integer atdSeq);
	
	public String obterPostoSaudePaciente(Integer pacCodigo);
	
	List<AinEscalasProfissionalInt> pesquisarProfissionalEscala(Integer pecPreSerMatricula, Short pecPreSerVinCodigo, Short pecCnvCodigo, Short pecPreEspSeq);
	
	public List<BaixasDiaVO> pesquisaBaixasDia(Date dataReferencia,	DominioGrupoConvenio grupoConvenio, AghOrigemEventos origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException;

	public List<BaixasDiaPorEtniaVO> pesquisaBaixasDiaPorEtnia(Date dataReferencia,	DominioGrupoConvenio grupoConvenio, AghOrigemEventos origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException;

	List<AinInternacao> pesquisarInternacoesPorPacientePOL(AipPacientes paciente);

	AinLeitos obterLeitoComAla(String leitoID);

	/**
	 * Obter código do atendimento de urgência pelo número da consulta
	 * 
	 * Web Service #38477
	 * 
	 * @param conNumero
	 * @return
	 */
	Integer obterSeqAtendimentoUrgenciaPorConsulta(final Integer conNumero);

	/**
	 * Obter os dados de uma internação de urgência
	 * 
	 * Web Service #38823
	 * 
	 * @param atuSeq
	 * @return
	 */
	AinInternacao obterInternacaoPorAtendimentoUrgencia(Integer atuSeq);
	
	List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarOrigem(Integer ihoSeqOrigem);
	
	List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarTransferencia(Integer ihoSeqOrigem);
	
	AinAtendimentosUrgencia persistirAtendimentoUrgente(Integer numeroConsulta, Integer codigoPaciente, String microComputador) throws ApplicationBusinessException, NumberFormatException, BaseException;

	void processarEnforceAtendimentoUrgencia(final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, final TipoOperacaoEnum tipoOperacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;
	
	void executaRotinasInsercao(final AinAtendimentosUrgencia atendimentoUrgencia, final AinAtendimentosUrgencia atendimentoUrgenciaAntigo)
			throws BaseException;
	
	Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta) throws ApplicationBusinessException;
	
	Boolean verificarPacienteIngressoSOPorConsulta(Integer numeroConsulta) throws ApplicationBusinessException;
	
	void finalizarConsulta(Integer numeroConsulta, Integer codigoPaciente, Integer matricula, Short vinCodigo, String hostName) throws ApplicationBusinessException, BaseException;
	
	void validarAtendimentoUrgenciaAntesAtualizar(final AinAtendimentosUrgencia atendimentoUrgencia, 
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, String usuario) throws ApplicationBusinessException;

	void geraMovimentoAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia, final Integer codigoMovimentacao,
			final Date dataHoraLancamento) throws ApplicationBusinessException;
	
	Integer obtemCodigoMovimento(final String parametro) throws ApplicationBusinessException;
	
	void atualizaMovimentoInternacao(final AinAtendimentosUrgencia atendimentoUrgencia,	final AinAtendimentosUrgencia atendimentoUrgenciaAntigo,
			final Integer codigoMovimentacao, final Date dtAltaAtendimento) throws ApplicationBusinessException;

	void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException, BaseException;
	
	// #8424 - ManterEquipesPorProfissional
	void excluirEquipePorProfissional(AghProfissionaisEquipeId equipePorProfissionalId);
	void persistirEquipePorProfissional(RapServidores servidorLogado, AghEquipes equipe);
	List<AghProfissionaisEquipe> pesquisarListaEquipesPorProfissionalPorServidorPaginado(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, RapServidores servidorSelecionado);
	Long pesquisarListaEquipesPorProfissionalPorServidorCount(RapServidores servidor);
	public List<AghEquipes> pesquisarListaAghEquipes(String filtro);
	public Long pesquisarListaAghEquipesCount(String filtro);
	// #8424 - ManterEquipesPorProfissional fim	
		
		public List<AinLeitos> pesquisarLeitosPorUnidadeFuncional(String param, Short unfSeq);
		
		public Long pesquisarLeitosPorUnidadeFuncionalCount(Object param, Short unfSeq);
	
	public AinTiposCaraterInternacao obterAinTiposCaraterInternacao(Integer seq);
	
	public Boolean exibirModalPlanoPrevAlta(AinInternacao internacao) throws ApplicationBusinessException;
		
		public List<AinLeitos> pesquisarLeitosPorUnidadeFuncional(Object param, Short unfSeq);

		Boolean verificarLeitoExiste(String idLeito);

	List<AinLeitos> pesquisarLeitosPorSeqUnf(List<Short> unfs, String leito);

	public Long pesquisarLeitosPorSeqUnfCount(List<Short> unfs, String leito);
	
	 void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo,
				Short gestacaoSeqp, String nivelContaminacao,
				Date dataInicioProcedimento, Short salaCirurgicaSeqp,
				Short tempoDuracaoProcedimento, Short anestesiaSeqp,
				Short equipeSeqp, String tipoNascimento) throws BaseException,ApplicationBusinessException;
	 
	 public AinInternacao obterInternacaoPaciente(Integer seqInternacao);
	 
	 public AinInternacao obterInternacaoPacientePorSeq(Integer seqInternacao);

	 
	 List<PacientesInternadosUltrapassadoVO> obterUnidadesQtdPacientesInternadosExcedentes();

	 public AinResponsaveisPaciente obterResponsaveisPacientePorNome(String nome);
	
	public List<PacienteIdadeCIDVO> pesquisarPacientesPorIdadeECID(Integer idadeInicial, Integer idadeFinal, AghCid cid) throws ApplicationBusinessException;
}
