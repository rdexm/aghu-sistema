package br.gov.mec.aghu.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioCategoriaTabela;
import br.gov.mec.aghu.dominio.DominioMesFeriado;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.solicitacao.vo.AtendimentoSolicExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.faturamento.vo.ReimpressaoLaudosProcedimentosVO;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghAtendimentoJn;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacaoId;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisJn;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.AghDocumentosAssinados;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidadesJn;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnidsId;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghLogAplicacao;
import br.gov.mec.aghu.model.AghNodoPol;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenioId;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.AghTabelasSistema;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionaisJn;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VAghUnidFuncionalId;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoAtendimentoEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioPOLVO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosRegistroCivilVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalPacienteVO;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.vo.AghAtendimentosPacienteCnsVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.vo.AtendimentoNascimentoVO;
import br.gov.mec.aghu.vo.ContagemQuimioterapiaVO;
import br.gov.mec.aghu.vo.DadosPacientesEmAtendimentoVO;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface IAghuFacade extends Serializable {
	

	List<AghAla> buscarTodasAlas();

	List<AghAtendimentos> listarAtendimentoEmAnadamentoConsulta(
			Integer numeroConsulta);
	
	public AghAtendimentos obterAghAtendimentosPorChavePrimaria(Integer seq);
	
	public Long pesquisarAghUnidadesFuncionaisCount(
			final AghUnidadesFuncionais aghUnidadesFuncionais);
	
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionaisAtivasColetaveis(final Object parametro);
	
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionaisAtivasCirurgicas(final Object parametro);	
	
	Map<String, String> obterMapaEspecialidadeConcatenadasProfCirurgiaoPorServidor(List<Integer> serMatricula, List<Short> serVinCodigo);
	
	
	public Long pesquisarAghHorariosUnidFuncionalCount(final Short unfSeq,
			final DominioTipoDia tipoDia, final Date hrInicial,
			final Date hrFinal, final Boolean indPlantao);
	
	
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionais(
			final Object parametro);
	
	// #5443 - Identificar Unidades executoras
	
	public List<AghUnidadesFuncionais> pesquisarDadosBasicosUnidadesExecutoras(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AghUnidadesFuncionais elemento);
	
	public AghUnidadesFuncionais obterAghUnidFuncionaisPeloId(final Short codigo);
	
	public AghUnidadesFuncionais obterAghUnidFuncionaisPorUnfSeq(final Short unfSeq);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorCodigoOuDescricao(
			final Object elemento);
	
	public Long pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(
			final Object elemento);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorSeqDescricao(
			String parametro);

    public List<AghUnidadesFuncionais> obterUnidadesFuncionaisListaUnidadesSolicitacao(String parametro, List<Short> seqUnidades);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasExecutoraColeta(
			final Object parametro);

	
	
	public List<AghUnidadesFuncionais> pesquisarUnidExecPorCodDescCaractExames(
			final Object elemento);
	

	AghAtendimentos obterAghAtendimentoPorChavePrimaria(Integer chavePrimaria);
	
	Date obterDthrFimAtendimento(Integer atdseq);
	
	AghAtendimentos obterOriginal(AghAtendimentos aghAtendimento);
	
	AghCaixaPostalAplicacao inserirAghCaixaPostalAplicacao(AghCaixaPostalAplicacao caixaPostalAp, boolean flush);
	
	AghCaixaPostalServidor obterAghCaixaPostalServidor(AghCaixaPostalServidorId id);
	
	AghCaixaPostalServidor atualizarAghCaixaPostalServidor(AghCaixaPostalServidor caixaPostalServ);
	
	void persistirAghCaixaPostalServidor(AghCaixaPostalServidor caixaPostalServ);
	
	void persistirAghCaixaPostal(AghCaixaPostal caixaPostal);
	
	void atualizarAghCaixaPostal(AghCaixaPostal caixaPostal);
	
	List<AghParametroAplicacao> pesquisarParametroAplicacaoPorCaixaPostalAplicacao(AghCaixaPostalAplicacaoId id);
	
	AghParametroAplicacao inserirAghParametroAplicacao(AghParametroAplicacao elemento, boolean flush);
	
	boolean isOracle();
	
	List<Short> obterUnidadesFuncionaisHierarquicasPorCaract(Short seq);

	List<Short> obterUnidadesFuncionaisHierarquicasPorCaract2(Short seq, ConstanteAghCaractUnidFuncionais cacuf);
	
	AghUnidadesFuncionais obterUnidadeFuncionalComCaracteristica(final Short seq);
	
	List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String seqs);

	List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim(
			Integer codigo, Date dtIni, Date dtFim);

	Long pesquisarAtendimentoAmbPorPacCodigoCount(Integer codigo,
			String seqs);

	void removerLogsAplicacao(Date expiration, String cron) throws ApplicationBusinessException;
	
	List<AghAtendimentos> pesquisarAtendimentoAmbCronologicoPorPacCodigo(
			Integer codigo);

	List<AghEspecialidades> pesquisarPorNomeSiglaInternaUnidade(String parametro);

	List<SuggestionListaCirurgiaVO> pesquisarEspecialidadesCirurgicas(final String filtro, final AghUnidadesFuncionais unidade, final Date data, final DominioSituacao indSituacao);
	
	Long pesquisarPorNomeSiglaInternaUnidadeCount(String parametro);

	List<AghUnidadesFuncionais> listarUnidadeFuncionalPorFuncionalSala(
			Object objPesquisa);

	List<VAacSiglaUnfSala> pesquisarSalasUnidadeFuncional(Object objPesquisa,
			AghUnidadesFuncionais unidadeFuncional);

	List<VAacSiglaUnfSala> pesquisarSalasUnidadesFuncionais(
			List<AghUnidadesFuncionais> undsFuncionais, DominioSituacao situacao);

	AghUnidadesFuncionais obterUnidadeFuncionalPorChavePrimaria(
			AghUnidadesFuncionais unidadeFuncional);

	
	List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(Object parametro,
			Integer maxResults);

	List<AghEspecialidades> pesquisarEspecialidades(Object objPesquisa);
	
	Long pesquisarEspecialidadesCount(Object objPesquisa);

	List<AghEspecialidades> pesquisarEspecialidadesPorNomeOuSigla(
			String parametro);

	List<AghEspecialidades> pesquisarEspecialidadeFluxogramaPorNomeOuSigla(
			String parametro);

	List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSigla(
			String parametro);
	
	List<AghEspecialidades> pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(
			String parametro);
	
	Long pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(
			String parametro);		
	
	List<AghEspecialidades> listarEspecialidadePorNomeOuSigla(String parametro);
	
	Long listarEspecialidadePorNomeOuSiglaCount(String parametro);
	
	List<AghEspecialidades> pesquisarEspecialidadeAtivaPorNomeOuSigla(String parametro, Integer maxResults);
	
	Integer pesquisarEspecialidadeAtivaPorNomeOuSiglaCount(String parametro);
	
	Integer pesquisarEspecialidadePrincipalAtivaPorNomeOuSiglaCount(String parametro) ;
	
	/**
	 * Consulta atendimentos filtrando pelo id da conta hospitalar de
	 * determinada conta internacao.
	 * 
	 * @param seqCth
	 * @return List<AghAtendimentos>
	 */
	List<AghAtendimentos> obterAtendimentosDeContasInternacaoPorContaHospitalar(
			Integer seqCth);

	List<AghCaractUnidFuncionais> listarCaracteristicasUnidadesFuncionais(
			Short unfSeq, ConstanteAghCaractUnidFuncionais[] caracteristicas,
			Integer firstResult, Integer maxResults);

	AghCid obterAghCidsPorChavePrimaria(Integer chavePrimaria);

	void removerAghCid(AghCid aghCid);

	AghCid atualizarAghCid(AghCid aghCid);

	AghCid inserirAghCid(AghCid aghCid);

	String buscaPrimeiroCidContaSituacao(Integer seq, DominioSituacao situacao);

	AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria);
	
	AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria, Enum... alias);
	AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria);
	
	AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria, Enum[] aliasInner, Enum[] aliasLeft);

	List<AghArquivoProcessamento> pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(String sigla, List<Integer> arquivos);
	
	void associarAghCaractUnidFuncionais(AghCaractUnidFuncionais acuf);
	AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria, boolean inicializarCaracteristicas, Enum[] aliasInner, Enum[] aliasLeft);
	void desassociarAghCaractUnidFuncionais(AghCaractUnidFuncionaisId id);
	
	AghSistemas obterAghSistema(String sigla);

	AghArquivoProcessamento persistirAghArquivoProcessamento(
			AghArquivoProcessamento aghArquivo);

	AghArquivoProcessamento obterArquivoNaoProcessado(String sigla, String nome);

	AghArquivoProcessamento obterArquivoNaoProcessado(AghSistemas sistema,
			String nome);

	List<AghArquivoProcessamento> pesquisarArquivosAbortados(
			final AghSistemas sistema, Integer minutosVencimento);

	String pesquisarLogsPorArquivosIds(List<Integer> arquivos, Integer maxLength);

	AghArquivoProcessamento obterAghArquivoProcessamentoPorChavePrimaria(
			Integer seq);

	List<ReimpressaoLaudosProcedimentosVO> listarReimpressaoLaudosProcedimentosVO(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacCodigo, Integer pacProntuario);

	Long listarReimpressaoLaudosProcedimentosVOCount(Integer pacCodigo,
			Integer pacProntuario);

	Long pesquisarClinicasCount(String filtro);

	List<AghEspecialidades> pesquisarEspecialidadesAgendas(String filtro);

	Long pesquisarEspecialidadesAgendasCount(String filtro);

	/**
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param buscarPorCodigo
	 * @param buscarPorDescricao
	 * @param orderAsc
	 * @param atributosOrder
	 * @param listaCaracteristicas
	 * @return
	 */
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			Boolean buscarPorCodigo, Boolean buscarPorDescricao,
			Boolean orderAsc,
			List<AghUnidadesFuncionais.Fields> atributosOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas);

	/**
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param buscarPorCodigo
	 * @param buscarPorDescricao
	 * @param orderAsc
	 * @param atributosOrder
	 * @param listaCaracteristicas
	 * @return
	 */
	Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
			Object parametroPesquisa, DominioSituacao situacao,
			Boolean buscarPorCodigo, Boolean buscarPorDescricao,
			Boolean orderAsc,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas);

	/**
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param atributoOrder
	 * @param listaCaracteristicas
	 * @return
	 */
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			AghUnidadesFuncionais.Fields atributoOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas);

	List<AghCaractUnidFuncionais> listarCaractUnidFuncionaisEUnidadeFuncional(String objPesquisa, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais);
	
	List<Short> verificaPrimeiraPrescricaoMedicaPacienteUnidFuncional(Integer atdSeq, String parametro);
	
	List<Short> pesquisarUnidadesFuncionaisPorCaracteristica(String parametro);
	
	/**
	 * Pesquisa Unidades funcionais por código/ descrição/ Ala e Andar, filtrando também por características
	 * específicas
	 * @param parametroPesquisa
	 * @param situacao
	 * @param List<atributoOrder>
	 * @param listaCaracteristicas
	 * @return List<AghUnidadesFuncionais>
	 */
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			List<AghUnidadesFuncionais.Fields> atributoOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas);

	Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
			String parametroPesquisa, DominioSituacao situacao,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisColetaPorCodigoDescricao(final Object parametroPesquisa);

	List<AghAtendimentos> listarAtendimentosPorNumeroConsulta(
			Integer consultaNumero);

	List<AghAtendimentos> listarAtendimentosPorConsultaComInternacaoAtendumentoUrgenciaAtendumentoPacienteExternoNulo(
			Integer numeroConsulta);

	Long listarCaracteristicasEspecialidadesCount(Short espSeq,
			DominioCaracEspecialidade caracteristica);

	/**
	 * @author gandriotti
	 * @return
	 */
	String obterCgcHospital();

	/**
	 * @author gandriotti
	 * @return
	 */
	List<AghInstituicoesHospitalares> listarInstHospPorIndLocalAtivo();

	List<ConstanteAghCaractUnidFuncionais> listarCaractUnidFuncionais(
			Short unfSeq, ConstanteAghCaractUnidFuncionais[] caracteristicas,
			Integer firstResult, Integer maxResults);

	AghAtendimentos buscarAtendimentosPorCodigoInternacao(Integer intSeq);

	List<Date> listarDthrInicioAtendimentosPorCodigoInternacao(Integer intSeq,
			Integer firstResult, Integer maxResults);

	AghImpressoraPadraoUnids obterImpressora(Short unfSeq,
			TipoDocumentoImpressao tipo);

	/**
	 * Método responsável por criar um novo documento de contingênica, além de
	 * deletar os documentos com prazo vencido.
	 * 
	 * @param documentoContigencia
	 * @throws BaseException
	 */
	//void persistirDocumentoContigencia(AghDocumentoContingencia documentoContigencia) throws BaseException;

	List<AghDocumentoContingencia> pesquisarDocumentoContingenciaPorUsuarioNomeTipo(
			String login, String nomeDocumento, DominioMimeType tipo,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	Long obterCountDocumentoContingenciaPorUsuarioNomeTipo(String login,
			String nomeDocumento, DominioMimeType tipo);

	
	List<AghEspecialidades> listarPorSigla(Object paramPesquisa);

	boolean unidadeFuncionalPossuiCaracteristica(Short seq,
			ConstanteAghCaractUnidFuncionais... caracteristicas);

	List<AghUnidadesFuncionais> buscarSeqsUnidadesFuncionaisDisponivelLocalDispensacao(
			Short... unfsSolicitantesMdto);

	Long buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(
			Short... idSUnfsSolicitantesMdto);

	List<AghUnidadesFuncionais> buscarUnidadesFuncionaisDisponivelLocalDispensacao(
			Short[] idSUnfsSolicitantesMdto, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	List<AghUnidadesFuncionais> obterListaUnidadesFuncionaisAtivasPorCaracteristica(
			ConstanteAghCaractUnidFuncionais caracteristica);

	AghAtendimentos obterAtendimento(Integer atdSeq,
			DominioPacAtendimento pacAtendimento,
			List<DominioOrigemAtendimento> origensAtendimento);

	String pegaHorarioDaUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional);

	List<AghAtendimentos> pesquisarAtendimentosaPorUnidadeDataReferencia(
			AghUnidadesFuncionais unidadeFuncional, Date dataDeReferencia);
	
	List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla);

	List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla, boolean apenasAtivas);

	List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(
			Object paramPesquisa, boolean ordemPorSigla);

	Long listarEspecialidadesSolicitacaoProntuarioCount(
			Object paramPesquisa, boolean ordemPorSigla);

	AghAtendimentos obterAtendimentoPorNumeroConsulta(Integer pConNumero);

	List<AghAtendimentos> pesquisaAtendimento(Integer prontuario, String leitoID)
			throws ApplicationBusinessException;

	AghClinicas obterClinicaPorChavePrimaria(final Integer codigo);
	
	/**
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico,
	 *         nomeUsual, espSeq, cpf), onde cada elemento da lista armazena uma
	 *         especialidade do medico.
	 */
	
	List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(Integer matricula,
			Short vinCodigo);

	AghCaractUnidFuncionais obterAghCaractUnidFuncionais(
			AghCaractUnidFuncionaisId id);

	/**
	 * Metodo auxiliar para validacao de datas.
	 * 
	 * @param pacCodigo
	 * @return Date
	 */
	Date getDataHoraFim(Integer pacCodigo);

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getRazaoSocial()
	 */
	String getRazaoSocial();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getEndereco()
	 */
	String getEndereco();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getCidade()
	 */
	String getCidade();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getCgc()
	 */
	String getCgc();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getFax()
	 */
	String getFax();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getUasg()
	 */
	Integer getUasg();

	/**
	 * @return
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getCcustAtuacao()
	 */
	Integer getCcustAtuacao(RapServidores servidor);

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
	
	List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(
			Object objPesquisa);

	List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(
			Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer[] tiposQualificacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException;

	Long pesquisarProfissionaisEscalaCount(Short vinculo, Integer matricula,
			String conselhoProfissional, String nomeServidor,
			String siglaEspecialidade, Short codigoConvenio,
			String descricaoConvenio, Integer[] tiposQualificacao)
			throws ApplicationBusinessException;

	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR c_gestacoes)
	 * @param intSeq
	 * @return
	 */
	AghAtendimentos obterAtendimentoGestacao(Integer intSeq);

	AghAtendimentos obterAtendimentoComPaciente(Integer seqAtendimento);
	
	/**
	 * Pesquisa por atendimentos de gestações
	 * 
	 * @param intSeq
	 * @return
	 */
	List<AghAtendimentos> pesquisarAtendimentosGestacoes(Integer intSeq);

	AghAtendimentos obterAtendimentoExames(Integer intSeq);

	/**
	 * Método que obtém o atendimento da internação
	 * 
	 * @param intSeq
	 * @return
	 */
	AghAtendimentos obterAtendimentoInternacao(Integer intSeq);

	void removerAghAtendimentos(AghAtendimentos aghAtendimentos, boolean flush);

	AghAtendimentos atualizarAghAtendimentos(AghAtendimentos aghAtendimentos,
			boolean flush);

	List<AghAtendimentoPacientes> pesquisarAghAtendimentoPacientesPorAtendimento(
			AghAtendimentos atendimento);

	void removerAghAtendimentoPacientes(
			AghAtendimentoPacientes aghAtendimentoPacientes, boolean flush);

	List<AghAtendimentos> pesquisarAtendimentosMaeGestacao(
			AghAtendimentos atendimentoGestacao);

	AghOrigemEventos obterAghOrigemEventosPorChavePrimaria(Short seq);

	AghInstituicoesHospitalares obterAghInstituicoesHospitalaresPorChavePrimaria(
			Integer seq);

	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query antes do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghEspecialidades select
	 * @dbtables AghProfEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	List<Object[]> obterCriteriaProfessoresInternacaoUnion1(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	List<Object[]> obterProfessoresInternacao(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query depois do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
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
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	List<Object[]> obterCriteriaProfessoresInternacaoUnion2(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	AghProfEspecialidades obterAghProfEspecialidadesPorChavePrimaria(
			AghProfEspecialidadesId id);
	
	AghProfEspecialidades obterProfEspecialidadeComServidorAtivoProgramado(AghProfEspecialidadesId id);

	List<AghCid> pesquisarSubCid(String codigoCid);

	List<AghAtendimentos> pesquisarAtendimentosGestacoesEmAtendimento(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<AghAtendimentos> pesquisarAtendimentosGestacoesFinalizados(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<AghAtendimentos> pesquisarAtendimentosEmAtendimentoPeloAtendimentoMae(
			AghAtendimentos atendimentoMae);

	List<AghCaractUnidFuncionais> listaCaracteristicasUnidadesFuncionaisPaciente(
			Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);

	List<Object[]> pesquisarDadosPacienteAtendimento(Integer codPaciente);

	/**
	 * @param codigo
	 * @return
	 */
	AghCid obterCid(String codigo);

	/**
	 * @param seqCid
	 * @return
	 */
	List<AghCid> pesquisarCidsRelacionados(Integer seqCid);

	/**
	 * @param seqGrupoCid
	 * @return
	 */
	List<AghCid> pesquisarCidsPorGrupo(Integer seqGrupoCid);

	Long pesquisaPorCidCount(Integer seq, String codigo, String descricao,
			DominioSituacao situacaoPesquisa);

	List<AghCid> pesquisaPorCid(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer seq, String codigo,
			String descricao, DominioSituacao situacaoPesquisa);

	List<AghCid> pesquisarCidsSemSubCategoriaPorCodigoDescricaoOuId(
			String descricao, Integer limiteRegistros);

	List<AghCid> pesquisarCidsComSubCategoriaCodigoPorDescricaoOuId(
			String descricao, Integer limiteRegistros);

	
	List<AghEspecialidades> listarEspecialidadePorSiglaENome(
			Object paramPesquisa);

	
	List<AghEspecialidades> listarEspecialidadeAtivasNaoInternas(
			Object paramPesquisa);

	/**
	 * Lista especialidades ATIVAS pela siga ou descricao ordenando os
	 * resultados pela sigla ou descricao.
	 * 
	 * @param paramPesquisa
	 *            - sigla ou descricao
	 * @return
	 */
	List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla);

	
	List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			String paramPesquisa, boolean apenasSiglaSePossivel);

	
	List<AghEspecialidades> listarEspecialidadeTransPaciente(
			Object paramPesquisa, Integer idade);

	
	List<AghEspecialidades> listarPorSiglaAtivas(Object paramPesquisa);

	
	List<AghEspecialidades> listarEspecialidadeAtualizaSolicitacaoInternacao(
			Object paramPesquisa, Integer idade);

	/**
	 * Lista apenas as especialidades ativas que permitem consultoria
	 * pesquisando por sigla e descrição.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	
	List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			Object paramPesquisa);

	List<AghTabelasSistema> pesquisarTabelasSistema(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String nome, AghCoresTabelasSistema cor,
			DominioCategoriaTabela categoria, String versao, String origem);

	Long pesquisarTabelasSistemaCount(Integer seq, String nome,
			AghCoresTabelasSistema cor, DominioCategoriaTabela categoria,
			String versao, String origem);

	AghTabelasSistema obterTabelaSistemaPeloId(Integer seq);

	void removerTabelaSistema(AghTabelasSistema tabelaSistema)
			throws BaseException;

	void persistirTabelaSistema(AghTabelasSistema tabelaSistema)
			throws BaseException;

	List<AghCoresTabelasSistema> pesquisarCoresTabelasSistema(Object parametro);
	
	
	
	/**
	 * Busca log da aplicação de acordo com o filtro informado.
	 * 
	 * @param usuario
	 * @param dthrCriacaoIni
	 * @param dthrCriacaoFim
	 * @param classe
	 * @param nivel
	 * @param mensagem
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<AghLogAplicacao> pesquisarAghLogAplicacao(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem, Integer firstResult,
			Integer maxResult);

	/**
	 * Busca número de registros de log da aplicação de acordo com o filtro
	 * informado.
	 * 
	 * @param usuario
	 * @param dthrCriacaoIni
	 * @param dthrCriacaoFim
	 * @param classe
	 * @param nivel
	 * @param mensagem
	 * @return
	 */
	Long pesquisarAghLogAplicacaoCount(String usuario, Date dthrCriacaoIni,
			Date dthrCriacaoFim, String classe, String nivel, String mensagem);

	List<AtendimentosVO> pesquisarAtendimentosInternacao(
			Integer numeroProntuario, Date date, Short vlrOrigem);

	List<AtendimentosVO> pesquisarAtendimentosInternacaoEmergencia(
			Integer numeroProntuario, Date date, Short vlrOrigem);

	List<AtendimentosVO> pesquisarAtendimentosSO(Integer numeroProntuario,
			Date date);
	
	List<AghProfEspecialidades> listarEspecialidadesPorServidor(RapServidores usuarioResponsavel);
	
	List<AghProfEspecialidades> listarProfEspecialidadesPorEspSeq(Short espSeq);
	
	List<AtendimentosVO> pesquisarAtendimentosConsultas(
			Integer numeroProntuario, Date date);

	List<AtendimentosVO> pesquisarAtendimentosCirurgias(
			Integer numeroProntuario, Date date);

	List<AtendimentosVO> pesquisarAtendimentosPacExterno(
			Integer numeroProntuario, Date date);

	List<AtendimentosVO> pesquisarAtendimentosNascimentos(
			Integer numeroProntuario, Date date);

	List<AtendimentosVO> pesquisarAtendimentosNeonatologia(
			Integer numeroProntuario, Date date);

	String buscarFuncaoCracha(final Integer matricula, final Short codigoVinculo)
			throws ApplicationBusinessException;

	/**
	 * Verifica se é o banco de produção
	 * 
	 * @return Verdadeiro se for o banco de produção do HCPA
	 */
	Boolean isProducaoHCPA();

	String buscarDescricaoOcupacao(Integer codigoOcupacao)
			throws ApplicationBusinessException;

	AghImpressoraPadraoUnids obterImpressora(Short unfSeq, String ipComputador,
			TipoDocumentoImpressao tipo);

	List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(
			String paramString);
	
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica(final String filtro,
			final ConstanteAghCaractUnidFuncionais[] caracteristicasUnideFuncional, final DominioSituacao situacao, Boolean order);
	
	List<PacienteInternadoVO> listarControlePacientesInternados(
			RapServidores servidor);

	AghAtendimentos obterAtendimentoVigente(AipPacientes paciente);
	
	void finalizarAghArquivoProcessamento(List<Integer> seqs);
	
	AghAtendimentos obterAtendimentoVigenteDetalhado(AipPacientes paciente);
	
	List<AghAtendimentos> pesquisarAtendimentoVigente(AinLeitos leito);

	List<Object[]> listarAtendimentosPacienteTratamentoPorCodigo(
			Integer pacCodigo, Integer pTipoTratamento);

	List<Object[]> listarAtendimentosPacienteEmAndamentoPorCodigo(
			Integer pacCodigo);

	List<ImpImpressora> listarImpImpressorasPorTipoDocumentoImpressao(
			TipoDocumentoImpressao tipoImpressora);

	boolean existeAtendimentoComSolicitacaoExame(AacConsultas consulta);

	List<AghAtendimentos> pesquisarAtendimentosPorNumeroConsulta(
			Integer numeroConsulta);

	List<AghAtendimentos> listarAtendimentosPorConsulta(Integer consultaNumero);

	AghCaractEspecialidades obterCaracteristicaEspecialidadePorChavePrimaria(
			AghCaractEspecialidadesId caractEspecialidadesId);
	
	public List<AghFeriados> pesquisarFeriadoPaginado(Date data,
			DominioMesFeriado mes, Integer ano, DominioTurno turno,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public Long countFeriado(Date data, DominioMesFeriado mes, Integer ano,
			DominioTurno turno);

	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores usuarioResponsavel);
	
	public AghFeriados obterFeriado(Date data);

	public void removerFeriado(Date idExclusao)
			throws ApplicationBusinessException;

	public void persistirFeriado(AghFeriados feriado,
			DominioOperacoesJournal operacao) throws BaseException;

	List<AghProfEspecialidades> listaProfEspecialidades(RapServidores servidor,
			Short espSeq);
	
	boolean verificarExisteProfEspecialidadePorServidorEspSeq(RapServidores servidor, Short espSeq);

	void gerarCheckOut(Integer seq, Integer pacCodigo,
			String tipoAltaMedicaCodigoOld, String tipoAltaMedicaCodigo,
			Short unfSeqOld, Short unfSeq, Boolean pacienteInternadoOld,
			Boolean pacienteInternacao) throws ObjetosOracleException;

	void atualizarSituacaoTriagem(Integer pacCodigo)
			throws ObjetosOracleException;
	
	void mpmkPprRnRnPprpDelUopProt(final Integer atdSeq, final Long seq) throws ObjetosOracleException;

	List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidadePorEspecialidade(
			Short espSeq);	

	List<AghEspecialidades> getListaEspecialidadesServico(String parametro,
			FccCentroCustos servico);

	List<Object[]> pesquisaInformacoesUnidadesFuncionaisEscalaCirurgias(final Short seq);

	Long obterQtdFeriadosEntreDatasSemFindeSemTurno(Date inicio, Date fim);
	
	public List<AghEspecialidades> getListaEspecialidades(String parametro);

	public List<AghEspecialidades> getListaEspecialidadesTodasSituacoes(
			String parametro);

	public List<AghEspecialidades> getListaTodasEspecialidades(String parametro);

	public List<AghEspecialidades> pesquisarEspecialidades(String parametro);

	public List<AghEspecialidades> pesquisarEspecialidadesAtivas(
			String parametro);

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasOrigemSumario(
			String parametro, Integer atdSeq, RapServidores servidorLogado);

	List<AghProfEspecialidades> listaProfEspecialidadesEquipe(
			AghEspecialidades especialidade, RapServidores servidorEquipe);

	List executarCursorAtdMae(Integer atqSeqMae);

	Integer executarCursorAtdPac(Integer atdSeq);

	List<AghAtendimentos> listarAtendimentos(Integer pacCodigo,
			Byte seqTipoTratamento);

	List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(
			DominioOrigemAtendimento dominioOrigemAtendimento,
			Integer consultaNumero);

	List<AghAtendimentos> listarAtendimentosPorMcoGestacoes(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<AghAtendimentos> listarAtendimentosPorSeqAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia);

	List<AghAtendimentos> listarAtendimentosPorSeqHospitalDia(
			Integer seqHospitalDia);

	List<AghAtendimentos> listarAtendimentosPorSeqInternacao(
			Integer seqInternacao);

	List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(
			AipPacientes paciente);

	AghAtendimentos obterAtendimentoPeloSeq(Integer atdSeq);
	
	AghAtendimentos obterAghAtendimentosComInternacaoEAtendimentoUrgencia(final Integer atdSeq);

	List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(
			Integer prontuario);

	List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(
			Integer seqHospitalDia);

	List<AghAtendimentos> obterAtendimentoPorSeqInternacao(Integer seqInternacao);

	AghAtendimentos obterAtendimentoEmAndamento(Integer seq);

	List<AghAtendimentos> obterAtendimentosPorPaciente(Integer codigoPaciente, Integer atdSeq,
			DominioPacAtendimento indPacAtendimento, ConstanteAghCaractUnidFuncionais... caracteristica);

	AghAtendimentos obterAtendimentoPorPaciente(Integer codigoPaciente,
			DominioPacAtendimento indPacAtendimento, String caracteristica);

	List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia);

	List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(
			AghAtendimentos aghAtendimentos);

	AghAtendimentos obterUltimoAtendimento(Integer aipPacientesCodigo);

	AghAtendimentos obterUltimoAtendimentoGestacao(AipPacientes pacienteMae);

	List<AghAtendimentos> pesquisarAghAtendimentos(Integer pacCodigo,
			Boolean origemCirurgia);

	List<AghAtendimentos> pesquisarAtendimentoPacienteTipoAtendimento(
			Integer codigoPaciente);

	List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, Date dthrInicio, Date dthrFim,
			Integer numeroConsulta, Short seqEspecialidade);

	Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo,
			Date dthrInicio, Date dthrFim, Integer numeroConsulta,
			Short seqEspecialidade);

	List<AghAtendimentos> pesquisarAtendimentosPorInternacao(
			Integer seqInternacao);

	List<AghAtendimentos> pesquisarAtendimentosPorPaciente(
			Integer codigoPaciente);

	List<AghAtendimentos> pesquisarAtendimentosPorPacienteGestacao(
			Integer codigoPacienteGestacao, Short seqGestacao);

	List<AghAtendimentos> pesquisarTipoAtendimento(Integer codigoPaciente,
			DominioTipoTratamentoAtendimento tipoTratamento, Date dataInicio,
			Date dataFim);

	Integer recuperarCodigoPaciente(Integer altanAtdSeq)
			throws ApplicationBusinessException;

	AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPor(
			AghUnidadesFuncionais unfExecutora, DominioTipoDia tipoDia,
			Date dataHoraProgramada);
	
	void removerAghAtendimentos(AghAtendimentos aghAtendimentos);

	void inserirAghAtendimentos(AghAtendimentos aghAtendimentos, boolean flush);

	List<AghNodoPol> recuperarAghNodoPolPorOrdem();

	List<AghEspecialidades> pesquisarSolicitarProntuarioEspecialidade(
			String strPesquisa);

	List<AghDocumentosAssinados> listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(
			Integer crgSeq);

	AghAtendimentoPacientes obterAtendimentoPaciente(Integer altanAtdSeq,
			Integer apaSeq) throws ApplicationBusinessException;

	Integer obterValorSequencialIdAghAtendimentoPacientes();

	void inserirAghAtendimentoPacientes(
			AghAtendimentoPacientes aghAtendimentoPacientes);

	Integer recuperarAtendimentoPaciente(Integer altanAtdSeq)
			throws ApplicationBusinessException;

	boolean verificarAghAtendimentoPacientes(Integer altanAtdSeq, Integer apaSeq)
			throws ApplicationBusinessException;

	List listarLocalDataUltExamePorHistoricoPacienteVO(
			HistoricoPacienteVO historicoVO);

	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(
			String filtro);

	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
			Object filtro, Object[] caracteristicas);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas);

	

	
	
	List<AghFeriados> obterListaFeriadosEntreDatas(Date dataInicio, Date dataFim);

	
	
	List<AghAtendimentoPacientes> listarAtendimentosPacientesPorCodigoPaciente(Integer pacCodigo);

	void persistirAghAtendimentoPacientes(AghAtendimentoPacientes aghAtendimentoPacientes);
	
	AghAtendimentos obterAtendimentoPorConsulta(Integer numero);

	AghProfEspecialidades findProfEspecialidadesById(Integer serMatricula,
			Short serVinCodigo, Short espSeq);

	List<AghEquipes> getListaEquipesAtivas(String objPesquisa);
	
	Long getListaEquipesAtivasCount(String objPesquisa);
	
	List<AghEquipes> getPesquisaEquipesAtivas(String objPesquisa);

	List<AghEquipes> getListaEquipes(String objPesquisa);

	List<AghCid> listarCidPorProcedimento(Object parametro, Integer phiSeq);

	Long listarCidPorProcedimentoCount(Object parametro, Integer phiSeq);

	List<AghUnidadesFuncionais> listaUnidadeFuncionalComSiglaNaoNulla(
			Object param, DominioSituacao situacao, Integer maxResults,
			String order);

	Long listaUnidadeFuncionalComSiglaNaoNullaCount(Object param,
			DominioSituacao situacao);

	
	
	List<AghCaixaPostal> pesquisarMensagemPendenciasCaixaPostal(final RapServidores servidor);
	
	AghHorariosUnidFuncional inserirAghHorariosUnidFuncional(AghHorariosUnidFuncional unidFunc, boolean flush);
	
	AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPorId(AghUnidadesFuncionais unidadeFuncional, DominioTipoDia tipoDia, String horaProgramada);
	AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPorId(AghHorariosUnidFuncionalId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	
	void removerAghHorariosUnidFuncional (AghHorariosUnidFuncionalId id);

	List<Object[]> pesquisarEspCrmVOAmbulatorioEquip(Object parametro,
			AghEquipes equipe, AghEspecialidades especialidade)
			throws ApplicationBusinessException;

	List<Object[]> pesquisarEspCrmVOAmbulatorioEspecialidade(Object parametro,
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	List<AghEquipes> pesquisarEquipesPorEspecialidadeServidores(
			Object objPesquisa, AghEspecialidades especialidade,
			DominioSituacao situacao) throws ApplicationBusinessException;

	

	public abstract List<AghUnidadesFuncionais> obterUnidadesFuncionais(
			Object param);
	
	public abstract void atualizarAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais)
			throws ApplicationBusinessException;

	public List<AghUnidadesFuncionais> listarUnidadeFarmacia();
	
	public AghImpressoraPadraoUnids obterImpressoraPadraoUnidPorAtendimento(AghAtendimentos atendimento);

	void inserirAghAtendimentoJn(AghAtendimentoJn aghAtendimentoJn);

	void inserirAghAtendimentoJn(AghAtendimentoJn aghAtendimentoJn, boolean flush);
	
	
	public abstract String pesquisarDescricaoCidPrincipal(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp);

	
	public abstract List<String> pesquisarDescricaoCidSecundario(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp);

	/**
	 * Método para obter lista de <code>AghCids</code> a partir do codigo e
	 * descrição.
	 * 
	 * @param codigo
	 * @param descricao
	 * @return List de <code>AghCids</code>
	 */
	
	public abstract List<AghCid> obterCids(final String codDesc, final boolean filtroSituacaoAtiva);

	public abstract Long obterCidsCount(final String codDesc, final boolean filtroSituacaoAtiva);

	/**
	 * Método para obter a quantidade de Procedimento associados ao CID..
	 * 
	 * @param cidCodigo
	 * @return Integer
	 */
	public abstract Long pesquisarProcedimentosParaCidCount(
			final String cidCodigo);

	/**
	 * Método que obtém a lista de Procedimentos associados ao CID como
	 * parâmetro.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	
	public abstract List<CidVO> pesquisarProcedimentosParaCid(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final String codCid);



	/**
	 * Método para buscar Cids através de sua descrição.
	 * 
	 * @param descricao
	 * @return
	 */
	
	
	public abstract List<AghCid> pesquisarCidsPorDescricaoOuId(
			final String descricao, final Integer limiteRegistros);

	/**
	 * Método para retornar todos os CIDs que não possuem sub-categorias. A
	 * pesquisa é feita através da descrição e ID da entidade AghCids.
	 * 
	 * @param descricao
	 * @return
	 */
	
	public abstract List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(
			final String descricao, final Integer limiteRegistros)
			throws ApplicationBusinessException;

	/**
	 * Método para retornar todos os CIDs que não possuem sub-categorias. A
	 * pesquisa é feita através da descrição e ID da entidade AghCids.
	 * 
	 * Ordenado por descrição.
	 * 
	 * @param descricao
	 * @return
	 */
	
	public abstract List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(
			final String descricao, final Integer limiteRegistros)
			throws ApplicationBusinessException;

	/**
	 * Método para pesquisar todos os cids de uma internação.
	 * 
	 * @param seqInternacao
	 * @return Lista de objetos AinCidsInternacao
	 */
	
	
	public abstract List<AinCidsInternacao> pesquisarCidsInternacao(
			final Integer seqInternacao);

	/**
	 * Método para obter um objeto AghCids através do seu ID.
	 * 
	 * @param seq
	 * @return
	 */
	
	
	public abstract AghCid obterCid(final Integer seq);

	/**
	 * @param codCid
	 * @return 
	 *  
	 */
	public abstract void validarCodigoCid(final String codCid)
			throws ApplicationBusinessException;

	/**
	 * DEPRECATED = Usar AghuFacade.verificarCaracteristicaUnidadeFuncional pois retorna Boolean e não DominioSimNao.
	 */
	@Deprecated
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);
	
	public Boolean verificarCaracteristicaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);

	String obterDescricaoVAghUnidFuncional(Short seq);

	AghCaractUnidFuncionais buscarCaracteristicaPorUnidadeCaracteristica(Short seqUnidadeFunc, ConstanteAghCaractUnidFuncionais areaFechada);

//	List<AghAtendimentos> listarAtendimentosComInternacaoEmAndamentoPorPaciente(Integer codigo);
//
//	List<AghAtendimentos> listarAtendimentoPorAtendimentoPacienteExterno(AghAtendimentosPacExtern atendimentoPacExterno);
//
//	AghAtendimentos obterAghAtendimentoOriginal(Integer seq);
		
	
	

	public List<AghAla> pesquisaAlaList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String codigo, String descricao);

	public Long pesquisaAlaListCount(String codigo, String descricao);

	public AghAla obterAghAlaPorChavePrimaria(String codigo);

	void excluirAghAla(AghAla aghAla);

	void atualizarAghAla(AghAla aghAla);

	void persistirAghAla(AghAla aghAla);

	public AghProfissionaisEspConvenio obterAghProfissionaisEspConvenioPorChavePrimaria(
			AghProfissionaisEspConvenioId id);

	public void desatacharAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio);

	public void removerAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio,
			boolean flush);

	public void persistirAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio);

	public void atualizarAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio);

	public void limparEntityManager();

	public List<EspCrmVO> pesquisarEspCrmVO(final Object strPesquisa,
			final AghEspecialidades especialidade,
			final DominioSimNao ambulatorio, final Integer[] tiposQualificacao)
			throws ApplicationBusinessException;

	public EspCrmVO obterEspCrmVO(final Object strPesquisa,
			final AghEspecialidades especialidade,
			final DominioSimNao ambulatorio, final RapServidores servidor,
			final Integer[] tiposQualificacao) throws ApplicationBusinessException;

	
	public EspCrmVO obterCrmENomeMedicoPorServidor(
			final RapServidores servidor,
			final AghEspecialidades especialidade,
			final Integer qualificacaoMedicina) throws ApplicationBusinessException;

	public List<AghProfEspecialidades> listarProfEspecialidades(
			Integer matricula, Integer codigoVinculo,
			Integer codigoEspecialidade);

	public Integer obterQtdContaInternados(Short serVinCodigo,
			Integer serMatricula, Short espSeq);

	void persistirAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades);
	
	void atualizarAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades);
	
	void mergeAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades);

	public List<ProfConveniosListVO> pesquisaProfConvenioslist(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer vinCodigo, Integer matricula, String nome,
			Long cpf, String siglaEspecialidade);

	public Integer pesquisaProfConveniosListCount(Integer vinCodigo,
			Integer matricula, String nome, Long cpf, String siglaEspecialidade);

	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	public List<Object[]> pesquisarReferencialEspecialidadeProfissonalGridVO(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghEspecialidades especialidade)
			throws ApplicationBusinessException;

	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla);

	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla);

	public AghGrupoCids obterGrupoCidPorSigla(String sigla);

	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq);

	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo,
			Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo);

	boolean containsAghGrupoCids(AghGrupoCids aghGrupoCids);

	AghGrupoCids mergeAghGrupoCids(AghGrupoCids aghGrupoCids);

	void removerAghGrupoCids(AghGrupoCids aghGrupoCids);

	void atualizarAghGrupoCids(AghGrupoCids aghGrupoCids);

	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo,
			String descricaoGrupo, DominioSituacao situacaoGrupo);

	void inserirAghGrupoCids(AghGrupoCids aghGrupoCids, boolean flush);

	public Integer obterSeqAnterior(Integer seq, Integer cpcSeq);

	public String obterDescricaoAnterior(Integer seq, Integer cpcSeq);

	public Date obterDataCriacaoAnterior(Integer seq, Integer cpcSeq);

	public Integer obterMatriculaServidorAnterior(Integer seq, Integer cpcSeq);

	public Short obterVinCodigoServidorAnterior(Integer seq, Integer cpcSeq);

	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(
			Integer seqCapituloCid);

	public Integer obterCpcSeqAnterior(Integer seq, Integer cpcSeq);

	public List<AghCaractUnidFuncionais> pesquisarCaracteristicaUnidadeFuncional(
			Short seq, ConstanteAghCaractUnidFuncionais caracteristica);

	AghCaractUnidFuncionais obterAghCaractUnidFuncionaisPorChavePrimaria(
			AghCaractUnidFuncionaisId id);

	public List<AghCid> listarCids(Integer gcdCpcSeq, Integer gcdSeq);
	
	List<AghCid> listarAghCidPorIdadeSexo(List<Long> listaCodSsm, Integer idade, String cidCodigo,
			DominioSexoDeterminante sexo);
	
	List<AghCid> listarCidPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao);
	
	Long listarCidPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao);
	
	/**
	 * 
	 * @param objPesquisa
	 * @return
	 */
	
	public List<AghClinicas> pesquisarClinicasPorCodigoEDescricao(
			Object objPesquisa);

	/**
	 * Metodo para pesquisa paginada.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	public Long pesquisaClinicaCount(final Integer codigoPesquisa,
			final String descricaoPesquisa, final Integer codigoSUSPesquisa);

	/**
	 * Metodo para pesquisa paginada.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	
	public List<AghClinicas> pesquisaClinica(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer codigoPesquisa,
			final String descricaoPesquisa, final Integer codigoSUSPesquisa);

	
	public List<AghClinicas> listarClinicasPorNomeOuCodigo(
			final Object strPesquisa);

	public List<AghClinicas> pesquisarClinicasPorCodigoOuDescricao(
			final Object objPesquisa, final boolean ordenarPorCodigo,
			final boolean ordenarPorDesc);

	public Long pesquisarClinicasPorCodigoOuDescricaoCount(
			final Object objPesquisa);

	
	public List<AghClinicas> obterClinicaCapacidadeReferencial(
			final String paramPesquisa);

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	
	public List<AghClinicas> pesquisarClinicasSolInternacao(
			final String strPesquisa);

	/**
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @return
	 */
	
	public AghClinicas obterClinica(final Integer codigo);

	/**
	 * Pesquisa <b>AghClinicas</b> com codigo igual a <i>strPesquisa</i><br>
	 * ou descricao contendo <i>strPesquisa</i>.
	 * 
	 * @dbtables AghClinicas select
	 * @param strPesquisa
	 * @return
	 */
	
	public List<AghClinicas> pesquisarClinicas(final String strPesquisa);

	
	public Integer pesquisarClinicasHQLCount(final String strPesquisa);

	public Long countClinicaReferencial(AghClinicas clinica)
			throws ApplicationBusinessException;

	public List<PesquisaReferencialClinicaEspecialidadeVO> listaClinicaferencial(
			AghClinicas clinica) throws ApplicationBusinessException;

	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica,
			String orderProperty, Boolean asc);

	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica);

	public AghClinicas obterClinicaPelaDescricaoExata(String descricao);

	void inserirAghClinicas(AghClinicas aghClinicas);

	void atualizarAghClinicasDepreciado(AghClinicas aghClinicas);

	void removerAghClinicas(AghClinicas aghClinicas);

	public List<AghEspecialidades> pesquisarEspecialidadesPorClinica(
			AghClinicas clinica);

	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(
			Object paramPesquisa);

	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(
			Object strPesquisa);

	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa);

	public Long countEspecialidadeReferencial(AghClinicas clinica)
			throws ApplicationBusinessException;

	public List<PesquisaReferencialClinicaEspecialidadeVO> listaEspecialidadeReferencial(
			AghClinicas clinica) throws ApplicationBusinessException;

	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(
			Object paramPesquisa);

	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(
			Object paramPesquisa);

	public List<AghEspecialidades> pesquisarEspecialidades(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short codigoEspecialidade, String nomeEspecialidade,
			String siglaEspecialidade, Short codigoEspGenerica,
			Integer centroCusto, Integer clinica, DominioSituacao situacao);

	public Long pesquisarEspecialidadesCount(Short codigo,
			String nomeEspecialidade, String siglaEspecialidade,
			Short codigoEspGenerica, Integer centroCusto, Integer clinica,
			DominioSituacao situacao);

	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(
			String strPesquisa, Integer maxResults);

	public List<AghEspecialidades> pesquisarTodasEspecialidades(String strPesquisa);

	public Long pesquisarTodasEspecialidadesCount(String strPesquisa);
	
	public Long pesquisarEspecialidadeGenericaCount(String strPesquisa);

	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(
			String strPesquisa, Short idadePaciente,
			DominioSimNao indUnidadeEmergencia, Integer maxResults);

	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(
			String strPesquisa, Short idadePaciente, Integer maxResults);
	
	public List<AghEspecialidades> pesquisarEspecialidadesSemEspSeq(String strPesquisa);

	public Long pesquisarEspecialidadesSemEspSeqCount(String strPesquisa);

	public AghEspecialidades obterEspecialidade(Short seq);

	void desatacharAghEspecialidades(AghEspecialidades aghEspecialidades);

	void atualizarAghEspecialidades(AghEspecialidades aghEspecialidades,
			boolean flush);

	void persistirAghEspecialidades(AghEspecialidades aghEspecialidades);
	
	public Long pesquisarEspecialidadePorSeqOuSiglaOuNomeCount(Object objPesquisa);

	public List<AghEspecialidades> pesquisarEspecialidadePorSeqOuSiglaOuNome(
			Object strPesquisa);

	public AghEspecialidades obterEspecialidadePorSigla(String sigla);

	public List<AghEspecialidades> obterEspecialidadePediatria();

	public SituacaoLeitosVO pesquisaSituacaoSemLeitos();
	
	public List<AinInternacao> listarInternacoesDoAtendimento(
			final Integer seqAtendimento, final Date novaData);

	public List<Object[]> obterCensoUnion19(final Short unfSeq,
			final Short unfSeqMae, final Date data,
			final DominioSituacaoUnidadeFuncional status);

	public List<VAinAltasVO> pesquisaPacientesComAlta(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Date dataInicial, final Date dataFinal,
			final boolean altaAdministrativa, final DominioTipoAlta tipoAlta,
			final DominioOrdenacaoPesquisaPacComAlta ordenacao,
			final Short unidFuncSeq, final Short espSeq, final String tamCodigo);

	public Long pesquisaPacientesComAltaCount(final Date dataInicial,
			final Date dataFinal, final boolean altaAdministrativa,
			final DominioTipoAlta tipoAlta, final Short unidFuncSeq,
			final Short espSeq, final String tamCodigo);

	public List<Object[]> pesquisarProcedenciaPaciente(final Short convSusPadrao);

	public AghAtendimentos obtemAtendimentoPaciente(final AipPacientes paciente);

	public AghAtendimentos buscaAtendimentoPorNumeroConsulta(
			final Integer numeroConsulta);

	public boolean possuiRecemNascido(final AghAtendimentos atendimento);

	public AghCapitulosCid obterAghCapitulosCidPorChavePrimaria(Integer seq);
	
	public AghCapitulosCid obterAghCapitulosCidPorNumero(Short numero, Integer seq);

	public DominioSituacao obterSituacaoCapituloCid(Integer seqCapitulo);

	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo();

	public Long obterCapituloCidCount(Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao);

	public String obterDescricaoAnterior(Integer seq);

	public Integer obterMatriculaServidorAnterior(Integer seq);

	public Short obterVinCodigoServidorAnterior(Integer seq);

	public List<AghCapitulosCid> pesquisarAghCapitulosCid(Integer firstResult,
			Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao);

	public Date obterDataCriacaoAnterior(Integer seq);

	public Integer obterSeqAnterior(Integer seq);

	void inserirAghCapitulosCid(AghCapitulosCid aghCapitulosCid);
	
	public void atualizarAghCapitulosCid(AghCapitulosCid aghCapitulosCid); 

	void removerAghCapitulosCid(AghCapitulosCid aghCapitulosCid);

	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(final String nomePesquisaFonetica, final String leitoPesquisaFonetica,
			final String quartoPesquisaFonetica, final AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada);
	
	/**
	 * Retorna os atendimentos para apresentação na lista de pacientes do
	 * profissional fornecido <br>
	 * <b>Módulo:</b> Prescrição.
	 * 
	 * @param servidor
	 * @return
	 */
	public List<AghAtendimentos> listarPaciente(final RapServidores servidor, List<RapServidores> pacAtdEqpResp, boolean mostrarPacientesCpa);
	
	/**
	 * 
	 * @param gsoPacCodigo
	 * @param seqp
	 * @return
	 */
	public AghAtendimentos obterAtendimentoRecemNascido(final Integer gsoPacCodigo, final Byte seqp);
	
	/**
	 * 
	 * @param gsoPacCodigo
	 * @param seqp
	 * @return
	 */
	public AghAtendimentos obterAtendimentoRecemNascido(final Integer gsoPacCodigo);
	
	/**
	 * Busca um AghAtendimentos pelo id, nao atachado.<br>
	 * NAO utiliza evict.<br>
	 * Objeto retornado contera os valores existentes na base.<br>
	 * 
	 * @param novoAtdSeq
	 * @return
	 */
	public AghAtendimentos obterAtendimentoOriginal(final Integer novoAtdSeq);
	
	Date obterDataFimAtendimento(final Integer atdSeq);
	Date obterDataInicioAtendimento(final Integer atdSeq);
	
	public List<AghAtendimentos> pesquisarPorProntuarioPacienteLista(final Integer prontuario);
	
	public AghAtendimentos obterAtendimentoPorAtendimentoUrgenciaVigente(final Integer cAtuSeq);
	
	public void refreshAndLock(AghAtendimentos atendimento);
	
	public AghAtendimentos obterPorAtendimentoVigente(final Integer cAtdSeq);
	
	public AghAtendimentos obterAtendimentoPorHospDiaVigente(final Integer cHodSeq);	
	
	public AghAtendimentos obterAtendimentoPorInternacaoVigente(final Integer cIntSeq);
	
	public List<AghAtendimentos> buscaAtendimentosSumarioPrescricao(
			final Date dataInicio, final Date dataFim)
			throws ApplicationBusinessException;

	/**
	 * Obtem atendimento a partir de int_seq
	 * @author andremachado	
	 * @param c_int_seq
	 * @return
	 */
	
	public AghAtendimentos obterAtendimentoPorIntSeq(final Integer cIntSeq);
	
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(final AinLeitos leito);
	
	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorLeito(final AinLeitos leito);
	
	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorProntuario(final Integer prontuario);
	
	Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo);
	
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagem(final AipPacientes paciente);

	void persistirAghCaractUnidFuncionaisJn(
			AghCaractUnidFuncionaisJn aghCaractUnidFuncionaisJn);

	void persistirAghUnidadesFuncionaisJn(
			AghUnidadesFuncionaisJn aghUnidadesFuncionaisJn);
	
	/**
	 * Recupera a entidade AghEquipe por chave primária.
	 * 
	 * @param seqEquipe
	 * @return
	 */
	
	public AghEquipes obterEquipe(final Integer seqEquipe);
	
	public AghEquipes obterEquipeNotRestrict(final Integer seqEquipe);
	
	/**
	 * Método responsável pela persistência (inclusão e edição) do pojo
	 * AghEquipe.
	 * 
	 * @param aghEquipe
	 * @throws ApplicationBusinessException
	 */
	public void persistirEquipe(final AghEquipes aghEquipe,List<RapServidoresVO> profissionaisEquipe)
			throws ApplicationBusinessException;
	
	public void removerEquipe(final AghEquipes aghEquipe);	

	/**
	 * Conta a quantidade de equipes de acordo com os critérios de pesquisa
	 * passados por parâmetro.
	 * 
	 * @param codigoPesquisaEquipe
	 * @param nomePesquisaEquipe
	 * @param responsavelPesquisaEquipe
	 * @param ativoPesquisaEquipe
	 * @param placarRiscoNeonatalPesquisaEquipe
	 * @return
	 */
	public Long pesquisaEquipesCount(final Integer codigoPesquisaEquipe,
			final String nomePesquisaEquipe,
			final RapServidoresVO responsavelPesquisaEquipe,
			final DominioSituacao ativoPesquisaEquipe,
			final DominioSimNao placarRiscoNeonatalPesquisaEquipe);

	/**
	 * Pesquisa Equipes de acordo com os critérios de pesquisa passados por
	 * parâmetro.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigoPesquisaEquipe
	 * @param nomePesquisaEquipe
	 * @param responsavelPesquisaEquipe
	 * @param ativoPesquisaEquipe
	 * @param placarRiscoNeonatalPesquisaEquipe
	 * @return
	 */
	
	public List<AghEquipes> pesquisarEquipes(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer codigoPesquisaEquipe,
			final String nomePesquisaEquipe,
			final RapServidoresVO responsavelPesquisaEquipe,
			final DominioSituacao ativoPesquisaEquipe,
			final DominioSimNao placarRiscoNeonatalPesquisaEquipe);

	/**
	 * Pesquisa equipes Ativas por nome ou por código.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	
	public List<AghEquipes> pesquisarEquipesAtivasPorNomeOuCodigo(
			final Object paramPesquisa, int quantidadeParaRetorno);

	/**
	 * Pesquisa equipes porm nome ou por código.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	
	public List<AghEquipes> pesquisarEquipesPorNomeOuCodigo(
			final Object paramPesquisa, int quantidadeParaRetorno);

	/**
	 * Pesquisa equpes por nome ou descrição
	 * 
	 * @param seqDesc
	 * @return
	 */
	
	public List<AghEquipes> pesquisarEquipesPorNomeOuDescricao(
			final String seqDesc);

	void inserirAghEspecialidadesJn(AghEspecialidadesJn aghEspecialidadesJn,
			boolean flush);

	
	public String obterDescricaoUnidadeFuncional(Short codigo);

	public AghUnidadesFuncionais obterUnidadeFuncional(Short seq);

	public Object[] obterAndarAlaPorSeq(Short seq);

	void excluirAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais);

	void persistirAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(
			Object parametro,
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional, Integer maxResult);

	Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(Object objPesquisa);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa);

	List<AghUnidadesFuncionais> pesquisaUnidadesFuncionais(AghUnidadesFuncionais unidadeFuncional);


	List<AghAtendimentos> pesquisaAtendimentos(AghUnidadesFuncionais unidadeFuncional, boolean ordemPorNome);
	
	List<br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO> pesquisaAtendimentos(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao);
	Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional);

	AghUnidadesFuncionais obterUnidadeFuncionalQuarto(Short numero);

	AghUnidadesFuncionais obterAghUnidadesFuncionaisOriginal(
			AghUnidadesFuncionais aghUnidadesFuncionais);

	Long pesquisaAghUnidadesFuncionaisCount(Short codigo,
			String descricao, String sigla, AghClinicas clinica,
			FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(
			String srtPesquisa);
	
	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final String srtPesquisa, Integer maxResults);
	
	Long pesquisarUnidadeFuncionalVOPorCodigoEDescricaoCount(final String srtPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorUnidEmergencia(
			String strPesquisa, boolean ordemPorDescricao);

	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacaoCount(
			Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(
			Object objPesquisa);

	public AghUnidadesFuncionais obterUnidadeFuncionalPorUnidEmergencia(
			Short codigo);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
			Object objPesquisa);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
			Object objPesquisa, boolean apenasAtivos);

	AghUnidadesFuncionais atualizarAghUnidadesFuncionaisSemException(
			AghUnidadesFuncionais aghUnidadesFuncionais);

	public AghUnidadesFuncionais obterUnidadeFuncionalPorSigla(String sigla);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			Short seq, Boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos,
			boolean caracteristicasInternacaoOuEmergencia,
			boolean caracteristicaUnidadeExecutora);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String strPesquisa, Boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos,
			boolean caracteristicasInternacaoOuEmergencia,
			boolean caracteristicaUnidadeExecutora);

	public List<AghUnidadesFuncionais> pesquisaAghUnidadesFuncionais(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short codigo, String descricao, String sigla,
			AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala);

	public List<AghUnidadesFuncionais> pesquisarUnidadesPaiPorDescricao(
			Object parametro);
	
	Long pesquisarUnidadesPaiPorDescricaoCount(String param);

	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricao(
			Object parametro, boolean orderDescricao);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricaoFilha(
			Object parametro, boolean orderDescricao, Short unidadeFuncionalFilha);

	public List<AghUnidadesFuncionais> pesquisaPacienteInternado(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigo, boolean situacao);

	public List<AghUnidadesFuncionais> pesquisaAtendimentoUrgencia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigo, boolean situacao);

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais();
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivas();

	public Long pesquisarOrigemEventosCount(Short seqOrigemInternacao,
			String descricaoOrigemInternacao);

	public List<AghOrigemEventos> pesquisarOrigemEventos(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short seqOrigemInternacao, String descricaoOrigemInternacao);

	public AghOrigemEventos obterOrigemEventos(Short seq);

	void persistirAghOrigemEventos(AghOrigemEventos aghOrigemEventos);

	void atualizarAghOrigemEventos(AghOrigemEventos aghOrigemEventos);

	void excluirAghOrigemEventos(AghOrigemEventos aghOrigemEventos);

	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(
			Object objPesquisa);

	void inserirAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush);

	void atualizarAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush);

	void removerAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush);

	public String recuperarNomeInstituicaoLocal();
	
	public AghInstituicoesHospitalares verificarInstituicaoLocal(Integer seq);

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param codCidade
	 * @return
	 */
	
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer codigo, final String descricao,
			final Integer codCidade);

	/**
	 * 
	 * @param codigo
	 * @param descricao
	 * @param codCidade
	 * @return
	 */
	public Long obterCountInstituicaoCodigoDescricao(final Integer codigo,
			final String descricao, final Integer codCidade);

	public Integer recuperarCnesInstituicaoHospitalarLocal();

	public AghInstituicoesHospitalares recuperarInstituicaoHospitalarLocal();

	/**
	 * Retorna uma lista de instituições ordenado por descrição
	 * 
	 * @param parametro
	 * @return
	 */
	
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoOuDescricaoOrdenado(
			final Object parametro);

	/**
	 * Retorna uma Instituição Hospitalar com base na chave primária.
	 * 
	 * @param seq
	 * @return
	 */
	
	public AghInstituicoesHospitalares obterInstituicaoHospitalarPorChavePrimaria(
			final Integer seq);

	AghImpressoraPadraoUnids obterAghImpressoraPadraoUnidsPorChavePrimaria(
			AghImpressoraPadraoUnidsId id);

	void removerAghImpressoraPadraoUnids(
			AghImpressoraPadraoUnids aghImpressoraPadraoUnids, boolean flush);

	public Long pesquisaImpressorasCount(Short unfSeq);

	public List<ImpImpressora> listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(
			Short unfSeq, TipoDocumentoImpressao tipoImpressora);

	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(
			Short seq);
	
	public List<AghImpressoraPadraoUnids> listarAghImpressoraPadraoUnids(
			Short seq, TipoDocumentoImpressao tipoDocumentoImpressao);
	
	
	AghAtendimentos obterAtendimentoPorNumeroConsultaLeitoProntuario(
			Integer prontuarioPac, Integer consultaPac, String string);

	void inserirAghImpressoraPadraoUnids(
			AghImpressoraPadraoUnids aghImpressoraPadraoUnids, boolean flush);

	public Long pesquisaTiposUnidadeFuncionalCount(Integer codigo,
			String descricao);

	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao);

	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			String descricao);

	/**
	 * @dbtables AghTiposUnidadeFuncional select
	 * @param aghTiposUnidadeFuncionalCodigo
	 * @return
	 */
	
	public AghTiposUnidadeFuncional obterTiposUnidadeFuncional(
			final Integer aghTiposUnidadeFuncionalCodigo);

	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(
			String strPesquisa);

	public boolean validarTipoUnidadeFuncionalComUnidadeFuncional(
			AghTiposUnidadeFuncional tiposUnidadeFuncional);

	void inserirAghTiposUnidadeFuncional(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional, boolean flush);

	void atualizarAghTiposUnidadeFuncionalDepreciado(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional);

	void removerAghTiposUnidadeFuncional(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional);

	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(
			Short seq, DominioCaracEspecialidade caracteristica);

	public List<AghAtendimentoPacientes> listarAtendimentosPacientes(
			Integer pacCodigo, Integer seqAtendimento);

	public List<AghAtendimentos> buscarAtendimentosPaciente(Integer pacCodigo, Integer atd);
	
	public List<AghEspecialidades> pesquisarPorSiglaIndices(String parametro);
	
	public List<AghEspecialidades> pesquisarPorNomeIndices(String parametro);
	
	/**
	 * Lista utilizada no sumário de alta
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param intSeq
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesPorInternacao(Integer atdSeq,
			Short espSeq, Integer intSeq) throws ApplicationBusinessException;
	
	/**
	 * Lista utilizada no sumário de alta
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param atuSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesPorAtendUrgencia(Integer atdSeq,
			Short espSeq, Integer atuSeq) throws ApplicationBusinessException;
	
	public List<AghEspecialidades> pesquisarPorIdSigla(Object parametro);
	
	public Long pesquisarPorIdSiglaCount(Object parametro);
	
	public List<AghEspecialidades> pesquisarPorNomeIdSiglaIndices(Object parametro);

	public List<AghEspecialidades> pesquisarEspecialidadesPorServidor(final RapServidores servidor);

	public List<AghProfEspecialidades> pesquisarConsultoresPorEspecialidade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, final AghEspecialidades especialidade);

	public Long pesquisarConsultoresPorEspecialidadeCount(AghEspecialidades especialidade);

	public Long pesquisarPorNomeIdSiglaIndicesCount(Object parametro);	
	
	public List<AghEspecialidades> pesquisarPorSiglaAtivas(Object parametro);
	
	public List<AghEspecialidades> pesquisarPorSiglaAtivasIndices(Object parametro);

	Integer obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(
			Integer pCthSeq);

	AghAtendimentos buscarAtendimentoContaParto(Integer seq);

	Long verificaBebeInternadoCount(Integer vAtdSeq);

	Integer verificaSSMPartoCount(Integer seq, Long[] codigosTabela);

	DominioRNClassificacaoNascimento verificarAbo(final Integer pIntSeq);
	
	DadosRegistroCivilVO obterDadosRegistroCivil(final Integer pIntSeq);

	String buscaCodigoCidSecundarioConta(Integer seq, DominioPrioridadeCid s);
	
	public List<AghCid> pesquisarPorNomeCodigoAtiva(String parametro);
	
	Long pesquisarPorNomeCodigoAtivaCount(String parametro);
	
	public AghAtendimentoPacientes obterAghAtendimentoPacientesPorChavePrimaria(AghAtendimentoPacientesId id);
	
	/**
	 * Retorna true se a unidade funcional possui a caracteristica fornecida.
	 * 
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	
	public boolean validarCaracteristicaDaUnidadeFuncional(
			final Short unfSeq,
			final ConstanteAghCaractUnidFuncionais caracteristica);
	
	/**
	 * Retorna uma map com os AghCaractUnidFuncionais filtradas por atendimentos e caracteristica.<br>
	 * Retorna true se a unidade funcional do Atendimento possui a caracteristica fornecida.<br>
	 * Key do Map eh formada da seguinte forma: <b>unfSeq:caracteristica</b>
	 * 
	 * @param atdSeqs
	 * @param caracteristica
	 * @return
	 */
	
	public Map<String, AghCaractUnidFuncionais> validarCaracteristicaDaUnidadeFuncional(
			final List<Integer> atdSeqs, 
			final ConstanteAghCaractUnidFuncionais caracteristica);

	
	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param Object param
	 * @return List<AghCid>
	 */
	public List<AghCid> obterCidPorNomeCodigoAtivaPaginado(String param);
	
	Long obterCidPorNomeCodigoAtivaCount(String param);
	
	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param Integer param
	 * @return AghCid
	 */
	public AghCid obterCidUnico(Integer param);
	
	public void refreshUnidadesFuncionais(AghUnidadesFuncionais unidade);
	
	public List<AghUnidadesFuncionais> listarUnidadeExecutora(Object param);

	AghClinicas pesquisarAghClinicasPorCodigo(Integer codigo);

	AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(String valorParam);

	Long pesquisarAtendimentoParaSolicitacaoExamesCount(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente);

	Long pesquisarAtendimentosPacienteInternadoCount(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente);

	List<AghAtendimentos> pesquisarAtendimentoParaSolicitacaoExames(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	List<AghAtendimentos> pesquisarAtendimentosPacientesInternados(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisPorSequencialOuDescricao(
			String parametro);

	List<Short> listarUnidadesFuncionaisPorUnfSeq(List<Short> listUnfSeq);

	

	List<AghAtendimentos> listarAtendimentosPorAtendimentoPacienteExterno(
			AghAtendimentosPacExtern atendimentoPacExterno);

	List<AghAtendimentos> listarAtendimentosComInternacaoEmAndamentoPorPaciente(
			Integer codigo);
	
	

	List<AghAtendimentos> pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(
			Integer conNumero, Date dthrRealizado);

	String obterCidExamePorItemSolicitacaoExames(Integer iseSoeSeq,
			Short iseSeqp, Integer pQuestaoCid10);

	String obterCodigoAghCidsPorPhiSeq(Integer phiSeq);

	DominioSexoDeterminante obterRestricaoSexo(String codigoCid);

	Integer buscaCidPorCodigoComPonto(String cidProcessado);

	AghFeriados obterFeriadoSemTurno(Date dtExecucao);

	List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(
			FatCompetencia competencia) throws ApplicationBusinessException;

	Long obterQtdFeriadosEntreDatas(Date inicio, Date fim);

	void inserirCentroObstetrico(Integer pAtdSeq, Integer pCth, Date pDtInter,
			Date pDtAlta, Integer pOpcao) throws ApplicationBusinessException;
	
	
	
	public BigDecimal capacidadeProf(Short pEsp, Short pVin, Integer pMatr);
	
	public List<Object> obterCapacUnid();
	
	public List<AghEspecialidades> carregarCapacidadeEspecialidade();
	
	public List<AghClinicas> obterTodasClinicas();	
	
	AghAtendimentos obterAtendimentoPorTriagem(Long trgSeq);
	
	List<AghAtendimentos> verificarSePacienteTemAtendimento(Integer pacCodigo,
			Integer numDiasPassado, Integer numDiasFuturo);

	List<AghAtendimentos> buscarPacienteInternado(Integer pacCodigo);

	List<AghAtendimentos> buscarPacientesAlta(Integer pacCodigo, Integer pNroDiasPas);
	
	public List<NodoAtendimentoEmergenciaPOLVO> pesquisarNodosMenuAtendimentosEmergenciaPol(Integer codPaciente);
	
	AghNodoPol obterAghNodoPolPorNome(String nome);	
	
	public List<AghUnidadesFuncionais> listarUnidadeFuncionalComSala(String parametro);

	
	public List<AghUnidadesFuncionais> pesquisarConsultasPaciente(Integer pacCodigo, Integer nroDiasFuturo, Integer nroDiasPassado , Integer paramReteronoConsAgendada);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPaciente(DominioPacAtendimento indpacAtendimento, Integer nroDias, Integer pacCodigo, Boolean centroDeCustoNotNull); 

	public 	List<AghUnidadesFuncionais> pesquisarCirurgiasUnidadesFuncionais(Integer pacCodigo, Integer nroDiasFuturo, Integer nroDiasPassado);
	
//	public List<AipPacientes> validarPermissoesPOL(List<AipPacientes> pacientes, Boolean acessoLivrePOL, Boolean acessoComissaoPOL, Boolean acessoEspecialPOL, Boolean acessoAdminPOL, Boolean acessoMonitorProjPesqPOL, Boolean acessoPesquisadorProjPesqPOL, String paginaOrigem, BaseListException listaExcept) throws ApplicationBusinessException;
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(String strPesquisa);
	
	public AghEspecialidades obterEspecialidadePorChavePrimaria(Short seq);

	//public List<AghAtendimentos> pesquisarEmergenciaEspecialidade(Integer pacCodigo);

	//public Integer getAghGrupoCidsNextVal(SequenceID id);

	
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaractCentralRecebimento(Short seq);
	
	List<AghAtendimentos> pesquisarAghAtendimentosPorPacienteEConsulta(Integer codPaciente, Integer numConsulta);	
	
	public AghEspecialidades obterEspecialidadePorServidor(Integer matricula, Short vinCodigo);

	public AghAtendimentos obterUltimoAtendimentoPorCodigoEOrigem(
			Integer prontuario, DominioOrigemAtendimento... origemAtendimentos);
	
	List<AghAtendimentos> obterAtendimentoPorCodigoEOrigem(final Integer codPaciente);

	AghAtendimentos obterAtendimentoPorCodigoPacienteNumeroConsulta(Integer pPacCodigo, Integer pConNumero);
	
	List<AghAtendimentos> listarAtendimentosPorPacCodGestacaoSeq(Integer gsoPacCodigo, Short gsoSeqp, Integer pepPacCodigo);

	AghCid obterAghCidPorSeq(Integer seqCid);

	public AghAtendimentos obterAtendimentoComUnidadeFuncional(Integer atdSeq);

	public AghAtendimentos obterAtendimentoPorPacienteEOrigem(Integer pacCodigo);

	public SumarioQuimioPOLVO listarDadosCabecalhoSumarioPrescricaoQuimio(
			Integer atdSeq, Integer apaSeq);
	
	List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(Integer seq, String sitCodigoLi,String sitCodigoAe);
	
	List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo( final String sitCodigoLi, final String sitCodigoAe,
		    																		 final Integer ppeSeqp,  final Integer ppePleSeq,
		    																		 final Integer asuApaAtdSeq, final Integer asuApaSeq,
		    																		 final Short apeSeqp);

	public List<AghCaixaPostalAplicacao> pesquisarCaixaPostalPorCxtSeq(Long cxtSeq);
	
	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String strPesquisa);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqSigla(Object parametro);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorDescricao(Object parametro);
	
	Long pesquisarUnidadeFuncionalPorSeqSiglaCount(Object parametro);

	Long pesquisarUnidadeFuncionalPorDescricaoCount(Object parametro);
	
	void excluirMensagemCxPostServidor(AghCaixaPostalServidorId id);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(String parametro, Boolean somenteAtivos);
	
	Long pesquisarUnidadeFuncionalPorSeqDescricaoCount(String parametro, Boolean somenteAtivos);
	
	void persistirDocumentoContigencia(AghDocumentoContingencia documentoContigencia) throws BaseException;

	public List<AghCid> buscarCidsPorExameMaterial(String sigla, Integer manSeq);

	public List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao, Integer limiteRegistros, DominioSexoDeterminante sexoPac, String sigla, Integer manSeq);

	public Long contarCidsPorDescricaoOuId(String descricao, DominioSexoDeterminante sexoPac);
	
	Date buscaDataHoraBancoDeDados();

	boolean verificarExisteAtendimentoAmbCronologicoPorPacCodigo(Integer codigo);

	List<AghAtendimentos> obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(
			DominioPacAtendimento pacAtendimento, Integer pacCodigo, List<DominioOrigemAtendimento> origens);
	
	public List<AghUnidadesFuncionais> pesquisaUnidadesFilhasVinculadas(final Short unidadeFuncionalPaiSeq, final DominioSituacao situacao);

	Long listarUnidadeExecutoraCount(Object param);

	List<AghCaractUnidFuncionais> pesquisarCaracteristicaComAtendimentoPorPaciente(
			Integer pacCodigo, Date dtHrFim,
			List<DominioOrigemAtendimento> origensAtendimento,
			ConstanteAghCaractUnidFuncionais caracteristica);
	
	List<VAghUnidFuncional> obterUnidFuncionalPorCaracteristicaInternacaoOuEmergencia(Object parametro);
	
	List<LocalPacienteVO> pesquisarUnidFuncionalPorCaracteristica(String param);
	
	Long pesquisarUnidFuncionalPorCaracteristicaCount(String param);
	
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(String filtro);
	
    Long pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(Object filtro);
	
	VAghUnidFuncional obterVAghUnidFuncionalPorChavePrimaria(VAghUnidFuncionalId unidFuncionalId);
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisExecutoraCirurgias(Object filtro, Boolean asc, String order);

	Long pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(
			Object strPesquisa);

	Long pesquisarEspecialidadesPorNomeOuSiglaCount(String paramPesquisa);
	
	Short obterMenorTempoMinimoUnidadeFuncionalCirurgia(ConstanteAghCaractUnidFuncionais caractUnidFuncional);

	Long pesquisarEspecialidadesCount(String parametro);

	List<AghEspecialidades> pesquisarEspecialidadePrincipalAtivaPorNomeOuSigla(String parametro, Integer maxResults);

	AghAtendimentos obterAtendimentoContrEscCirurg(Integer aipPacientesCodigo);
	
	List<Integer> listarSeqAtdPorOrigem(Integer pacCodigo, DominioOrigemAtendimento[] origens);
	
	AghFeriados obterFeriadoSemTurnoDataTruncada(Date dataCirurgia);
	
	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasPorCaracteristica(final ConstanteAghCaractUnidFuncionais caractUnidFuncional);
	
	List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorCaracteristica(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais);
	
	Long listarUnidadesFuncionaisPorCaracteristicaCount(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais);

	List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(
			String strPesquisa,
			ConstanteAghCaractUnidFuncionais unidExecutoraCirurgias, Boolean somenteAtivos);

	Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(
			String strPesquisa,
			ConstanteAghCaractUnidFuncionais unidExecutoraCirurgias, Boolean somenteAtivos);

	List<AghCid> pesquisarCidPorCodigoDescricao(Integer maxResult, Object objParam);

	AghAtendimentos buscarDadosPacientePorAtendimento(Integer seqAtendimento, DominioOrigemAtendimento... origem);
	
	List<AghProfissionaisEspConvenio> obterAghProfissionaisEspConvenioPorProfissionalEsp(Integer preSerMatricula, Short preSerVinCodigo, Short preEspSeq);
	
	Long pesquisarCidPorCodigoDescricaoCount(final Object objParam);

	List<AghCid> pesquisarCidAtivosUsuaisEquipe(Integer matriculaEquipe, Short vinCodigoEquipe);

	AghAtendimentos obterAtendimentoPorPacienteEOrigemDthrFim(Integer pacCodigo,
			DominioOrigemAtendimento origem, Date dthrFimCirg);

	Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo, final DominioOrigemAtendimento[] origens, final DominioPacAtendimento indPacAtendimento);

	Long listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(
			String strPesquisa, ConstanteAghCaractUnidFuncionais unidInternacao, Boolean ativo);

	List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorSeqDescricaoAndarAla(
			String strPesquisa, ConstanteAghCaractUnidFuncionais unidInternacao, Boolean ativo);

	AghAtendimentos obterAtendimentoPorPacienteEDthr(Integer pacCodigo,Date dthrFim);
	
	public AghCid obterAghCidPorChavePrimaria(Integer seq);
	
	public List<AghProfissionaisEquipe> pesquisarServidoresPorEquipe(Integer eqpSeq);

	public List<AghCid> listarAghCidPorCodigoDescricao(
			String codigoOuDescricao, Integer seq, DominioSituacao situacao,
			boolean somenteCodigo, boolean somenteDescricao,
			Integer firstResult, Integer maxResults, boolean asc,
			AghCid.Fields... ordersProperty);

	public Long listarAghCidPorCodigoDescricaoCount(
			String codigoOuDescricao, Integer seq, DominioSituacao situacao,
			boolean somenteCodigo, boolean somenteDescricao);

	List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasCirurgias(String objPesquisa);

	Long pesquisarUnidadesExecutorasCirurgiasCount(String objPesquisa);

	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(
			Object filtro);
	
	Long pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgiasCount(
			Object filtro);
	
	List<AghAtendimentos> pesquisaAtendimentoPacienteInternadoUrgencia(Integer pacCodigo);
	
	List<AghAtendimentos> pesquisaAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento origem, Date dthrInicio);
	
	List<AghAtendimentos> pesquisarAtendimentoRegistroCirurgiaRealizada(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg);

	Integer pesquisarAtendimentoRegistroCirurgiaRealizadaCount(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg);
	
	/**
	 * Busca atendimento que são Internações dentro de um intervalo e que não foram calculadas ainda. 
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto 	Filtro (intervalor e calculo)
	 * @return						Lista de atendimento que satisfaz as codições
	 */
	List<AghAtendimentos> buscaInternacoesDentroIntervaloNaoCalculado(SigProcessamentoCusto processamentoCusto);
	
	AghAtendimentos obterAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento... origem);
	
	List<AghEspecialidades> pesquisarPorNomeOuSiglaEspSeqNulo(String parametro);
	
	Long pesquisarPorNomeOuSiglaEspSeqNuloCount(String parametro);

	AghEspecialidades obterEspecialidadePorEquipe(Integer eqpSeq);
	
	List<AghProfEspecialidades> pesquisarProfEspecialidadesCirurgiao(Short pEsp, Short pVin, Integer pMatr);

	List<AghProfEspecialidades> pesquisarEspecialidadeMesmaEspecialidadeCirurgia(final Integer crgSeq, final Short crgEspMae);

	 List<AghUnidadesFuncionais> pesquisarUnidadeExecutoraMonitorCirurgia(final Object parametro);
	 Integer pesquisarUnidadeExecutoraMonitorCirurgiaCount(final Object parametro);

	List<AghAtendimentos> obterUnidadesAlta(Integer codigo,
			Date dataUltInternacao, Boolean buscarDtInt);
	
	Long listarCaractUnidFuncionaisEUnidadeFuncionalCount(String objPesquisa, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais);
	
	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao(String seqOuAndarAlaDescricao);
	
	Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoCount(String seqOuAndarAlaDescricao);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMae(String seqOuAndarAlaDescricao);
	
	Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMaeCount(String seqOuAndarAlaDescricao);
	
	List<Short> pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador);

	List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica,
			Date dataCirurgia);

	AghAtendimentos buscarAtendimentoPorSeq(Integer seq);

	Boolean verificarUnidadeFuncionalAtendimentos(Short unfSeq);
	
	Integer obterAtendimentoPorConNumero(Integer conNumero);
	
	List<AghEspecialidades> pesquisarPorNomeOuSiglaExata(String parametro);
	
	public RapPessoasFisicas obterEspecialidadeInternacaoServidorChefePessoaFisica(final Short seq);

	Boolean isHCPA();
	
	List<AghEspecialidades> obterEspecialidadePorProfissionalAmbInt(String param, Integer matricula, Short vinCodigo);

	Long obterEspecialidadePorProfissionalAmbIntCount(String param,	Integer matricula, Short vinCodigo);
	
	List<AghEspecialidades> pesquisarEspecialidadeLaudoAih(Object pesquisa, Integer conNumero, Integer idadePaciente);
	
	AghCid obterCidPermiteCidSecundario (Integer cidSeq);
	
	List<AghCid> listarCidSecundarioPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, String cidCodigo,
			Short paramTabelaFaturPadrao, Integer caracteristica);
	
	Long listarCidSecundarioPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, String cidCodigo,
			Short paramTabelaFaturPadrao, Integer caracteristica);

	Long obterProfessoresInternacaoCount(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	List<Object[]> obterProfessoresInternacaoTodos(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);

	Long obterProfessoresInternacaoTodosCount(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor);
	
	List<AghEquipes> pesquisarEquipeRespLaudoAih(
			Long seq, RapServidores servidorRespInternacao);

	List<AghEspecialidades> obterEspecialidadeProfCirurgiaoPorServidor(Integer matricula, Short vinCodigo);
	
	String obterEspecialidadeConcatenadasProfCirurgiaoPorServidor(Integer serMatricula, Short serVinCodigo);
	
	List<AghNodoPol> recuperarAghNodoPolPorOrdem(String[] tipos);
	
	List<AghEspecialidades> pesquisarEspecPorNomeSiglaListaSeq(List<Short> especialidadeId, String parametro);
	
	Long pesquisarEspecPorNomeSiglaListaSeqCount(List<Short> especialidadeId, String parametro);
	
	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * @param listSeqs
	 * @return
	 */
	List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs);
	
	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * @param listSeqs
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	
	/**
	 * Retorna o número de especialidades ativas de acordo com os seqs
	 * 
	 * @param listSeqs
	 * @return
	 */
	Long pesquisarEspecialidadesAtivasPorSeqsCount(List<Short> listSeqs);
		
	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 */
	List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults);
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 */
	Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param);
	
	/**
	 * Retorna as especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * @param param
	 * @param listSeqs
	 * @return
	 */
	List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(String param, List<Short> listSeqs);
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * @param param
	 * @param listSeqs
	 * @return
	 */
	Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(String param, List<Short> listSeqs);

	Long pesquisarAghUnidadesFuncionaisPorSequencialOuDescricaoCount(
			String parametro);

	boolean possuiCaracteristicaPorUnidadeEConstante(Short seqUnidadeFuncional,
			ConstanteAghCaractUnidFuncionais caracteristica);
	
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisPorCodigoDescricao(final Object parametro, final boolean orderDescricao);
	
	/**
	 * Retornar os Cids pesquisados por seq
	 * 
	 * Web Service #36118
	 * 
	 * @param listSeq
	 * @return
	 * @throws ServiceException
	 */
	List<AghCid> pesquisarCidPorSeq(List<Integer> listSeq);
	

	/**
	 * Retornar os Cids pesquisados por codigo e/ou descricao
	 * 
	 * Web Service #36117
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	List<AghCid> pesquisarCidPorCodigoDescricao(String param);

	
	/**
	 * Buscar a última consulta da gestante tendo como parâmetro o sequencial da gestação e o código do paciente
	 * 
	 * Web Service #36620
	 * 
	 * @param gsoSeqp
	 * @param pacCodigo
	 * @return
	 */
	Integer obterDadosGestantePorGestacaoPaciente(final Short gsoSeqp, final Integer gsoPacCodigo);
	
	/**
	 * Buscar os dados da Gestante tendo como parâmetro o número da consulta
	 * 
	 * Web Service #36608
	 * 
	 * @param nroConsulta
	 * @return
	 */
	AghAtendimentos obterDadosGestantePorConsulta(final Integer nroConsulta);
	
	/**
	 * Retornar os Cids pesquisados por codigo e/ou descricao
	 * 
	 * Web Service #36117
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarCidPorCodigoDescricaoCount(String param);
	
	/**
	 * Busca nome da especialidade pelo seq
	 * 
	 * Web Service #38510
	 * 
	 * @param seq
	 * @return
	 * @throws ServiceException
	 */
	String pesquisarNomeEspecialidadePorSeq(Short seq);
	
	/**
	 * Buscar as Equipes ativas do CO
	 * 
	 * Web Service #38731
	 * 
	 * @return
	 */
	List<AghEquipes> pesquisarEquipesAtivasDoCO();
	
	/**
	 * Buscar a equipe sendo informado matícula e vínculo
	 * 
	 * Web Service #38721
	 * 
	 * @return
	 */
	List<AghEquipes> pesquisarEquipesPorMatriculaVinculo(final Integer matricula, final Short vinCodigo);

	/**
	 * Buscar unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(final String parametro, int firstResult, int maxResults, String orderProperty, boolean asc);

	/**
	 * Buscar count de unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @return
	 */
	Long pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(final String parametro);

	/**
	 * Retornar os Cids ativos por seq
	 * 
	 * Web Service #37939
	 * 
	 * @param listSeq
	 * @return
	 */
	List<AghCid> pesquisarCidAtivosPorSeq(List<Integer> listSeq);
	
	/**
	 * Retornar os dados do Paciente Em Atendimento
	 * 
	 * Web Services #34389, #34391 e #34337
	 * 
	 * @param conNumero
	 * @return
	 */
	DadosPacientesEmAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero);
		
	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(
			Integer atdSeq, 
			List<DominioOrigemAtendimento> origensInternacao,
			List<DominioOrigemAtendimento> origensAmbulatorio, 
			Integer prontuario,	Integer qtdHorasLimiteAtendimento);
	
	List<AghAtendimentos> pesquisarAtendimentosInternacaoNaoUrgencia(final Integer seqInternacao);
	Collection<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion1(
			Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq);

	Collection<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion2(
			Date dataInicio, Date dataFim, Integer pacCodigo, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq);

	String pesquisarNroRegConselho(Integer matriculaResp, Short vinCodigoResp);
	
	List<AghCid> pesquisarCidPorCodDescricaoPorSexo(String param, DominioSexo sexo);
	
	Long pesquisarCidPorCodDescricaoPorSexoCount(String param, DominioSexo sexo);
	
	AghUnidadesFuncionais buscarUnidadeInternacaoAtiva(Short unfSeqAgendada);
	
	List<AghAtendimentos> buscarAtendimentos(Integer pacCodigo, Date dataPrevisao);
	
	List<ContagemQuimioterapiaVO> buscarCuidadosPrescricaoQuimioterapia(Date dataInicio, Date dataFim);

	List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosPaciente(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,AipPacientes paciente, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis);

	Long obterAghAtendimentosPorFiltrosPacienteCount(AipPacientes paciente, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis);

	List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosCompetencia(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, SigProcessamentoCusto competencia, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> obterListaReponsaveisPorListaDeEquipes, Boolean pacienteComAlta);
	
	Long obterAghAtendimentosPorFiltrosCompetenciaCount(SigProcessamentoCusto competencia, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> obterListaReponsaveisPorListaDeEquipes, Boolean pacienteComAlta);

	List<VAghUnidFuncionalVO> listarUnidadesFuncionaisMae(String seqOuAndarAlaDescricao);

	List<AghAtendimentos> pesquisarAtendimentosAmbulatoriaisPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica);
	
	List<AghAtendimentos> pesquisarAtendimentosCirurgicosPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica);
	
	List<AghAtendimentos> pesquisarAtendimentosExternoPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica);
	
	List<AghAtendimentos> pesquisarAtendimentosPorItemSolicitacaoExame(
			Integer iseSoeSeq, Short iseSeqp, List<DominioOrigemAtendimento> origensAtendimento);
	
	List<AghAtendimentos> obterAghAtendimentoPorDadosPaciente(Integer codigoPaciente, Integer prontuario);
			
	List<AghAtendimentos> pesquisarAtendimentos(Integer pacCodigo, Integer atdSeq, 
			DominioPacAtendimento pacAtendimento, List<DominioOrigemAtendimento> origens);
	
	Long listarEspecialidadesSolicitacaoProntuarioCount(Object paramPesquisa);
	
	Long pesquisarEquipeAtivaCount(Object paramPesquisa);
	
	List<AghEquipes> pesquisarEquipeAtivaCO(String parametro);
	
	Long listarEspecialidadesAtivasPorNomeOuSiglaCount(String parametro);
	
	Long pesquisarEquipeAtivaCOCount(String parametro);

	List<AghEspecialidades> listarEspecialidadesAtivasPorNomeOuSigla(String parametro);
	
	/**
	 * #34722 - Consulta utilizada para verificar se determinada unidade funcional possui determinada característica associada
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	Boolean existeCaractUnidFuncionaisPorSeqCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);
	
	List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(final String strPesquisa, final boolean semEtiologia, final String tipo);
	Long pesquisarUnidadesAtivasCount(final String strPesquisa, final boolean semEtiologia, final String tipo);


	public Long pesquisarCaracteristicasEspecialidadesCount(AghEspecialidades especialidade);

	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(Integer firstResult, Integer maxResult, String orderProperty, AghEspecialidades especialidade, boolean ordenacao);

	public void removerCaracteristicaEspecialidade(AghCaractEspecialidades acAghCaractEspecialidade) throws BaseException;

	public void desassociarCaracteristicaEspecialidade(AghCaractEspecialidadesId id);
	
	public void persistirAghCaractEspecialidade(AghCaractEspecialidades aghCaractEspecialidades) throws BaseException;

	public void atualizarAghCaractEspecialidade(AghCaractEspecialidades aghCaractEspecialidades, AghCaractEspecialidades aghCaractEspecialidadeOld) throws BaseException;

	public AghCaractEspecialidades obterCaracteristicaEspecialidadePorId(AghCaractEspecialidadesId id);

	public AghCaractEspecialidades obterCaracteristicaEspecialidade(AghCaractEspecialidades aghCaractEspecialidades);

	public AghCaractEspecialidades obterCaracteristicaEspecialidadeOld(AghCaractEspecialidades aghCaractEspecialidades);

	AghAtendimentos obterAghAtendimentoPorSeq(Integer seq);
	
	/**
	 * Web Service #38488
	 * Consulta utilizada para obter o UNF_SEQ de unidades funcionais com característica de unidade executora de cirurgias e Centro Obstétrico.
	 * @return UNF_SEQ
	 */
	List<Short> pesquisarUnidFuncExecutora();
	
	/**
	 * Web Service #38475
	 * 
	 * Obter código do atendimento pelo número da consulta
	 * 
	 */
	AghAtendimentos buscarAtendimentoPorConNumero(Integer conNumero);
	

	/**
	 * Web Service #40702
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @return
	 */
	AghAtendimentos obterAtendimentoPorPacienteDataInicio(final Integer pacCodigo, final Date dthrInicio);

	/**
	 * Web Service #40705
	 * 
	 * Atualizar a data início do atendimento com a Data de Nascimento do Recém-Nascido na tabela AGH_ATENDIMENTOS
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @param dthrNascimento
	 */
	void atualizarAtendimentoDthrNascimento(final Integer pacCodigo, final Date dthrInicio, final Date dthrNascimento);
	
	/**
	 * # 39006 - Serviço que obtem AghAtendimentos
	 * @param seq
	 * @return
	 */
	AghAtendimentos obterAghAtendimentosPorSeq(Integer seq);

	AghEspecialidades buscarEspecialidadePorConNumero(Integer conNumero);
	
	void persistirAghProfissionalEquipe(AghProfissionaisEquipe equipePorProfissional);

	void removerAghProfissionaisEquipe(AghProfissionaisEquipe equipePorProfissional);

	List<AghProfissionaisEquipe> pesquisarEquipesPorProfissionalPaginado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			RapServidores servidor);

	Long countPesquisarEquipesPorProfissionalPaginado(RapServidores servidor);

	Long pesquisarListaEquipesCount(String objPesquisa);

	List<AghEquipes> pesquisarListaEquipes(String objPesquisa);

	AghProfissionaisEquipe obterAghProfissionaisEquipePorChavePrimaria(AghProfissionaisEquipeId equipePorProfissionalid);
	
	Integer countNumLinhasTabela(String nomeTabela);

	boolean verificarTabelaExistente(String nome);
	
	Boolean existePacienteInternadoListarPacientesCCIH(final Integer codigoPaciente);
	
	List<AghUnidadesFuncionais> obterUnidadesFuncionaisSB(Object param);
	
	List<AghEspecialidades> pesquisarPorNomeOuSiglaOrderByNomeReduzido(String parametro);

	Long pesquisarPorNomeOuSiglaOrderByNomeReduzidoCount(String param);

	Long obterUnidadesFuncionaisSBCount(String param);
	AghTabelasSistema obterTabelaSistema(Integer seq);

	Long pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(String objPesquisa);

	Long pesquisarEspecialidadesCirurgicasCount(String strPesquisa,
			AghUnidadesFuncionais unidade, Date dtProcedimento,
			DominioSituacao a);

	Long pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount(
			String strPesquisa,
			ConstanteAghCaractUnidFuncionais[] caracteristicasUnidadeFuncional,
			DominioSituacao situacao, Boolean order);

	List<AghEspecialidadeVO> pesquisarAghEspecialidadePorServidor(RapServidores servidor);
	List<AghEspecialidadeVO> pesquisarEspecialidadesConsultoria(String pesquisa, Boolean indPesquisaConsultoriaAmbulatorio, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	Long pesquisarEspecialidadesConsultoriaCount(String pesquisa, Boolean indPesquisaConsultoriaAmbulatorio);
	List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao,
			Integer limiteRegistros, Boolean semJoin);
	
	AghAtendimentos obterAghAtendimentoPorChavePrimaria(Integer chavePrimaria, Enum[] aliasInner, Enum[] aliasLeft);
	
	AghAtendimentos obterAghAtendimentoParaSolicitacaoConsultoria(Integer chavePrimaria);
	
	List<EquipeVO> pesquisarEquipesConsultoriaAmbulatorial(final String pesquisa, Short espSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	Long pesquisarEquipesConsultoriaAmbulatorialCount(final String pesquisa, Short espSeq);
	
	void persistirProfEspecialidades(AghProfEspecialidades profEspecialidades, RapServidores servidorLogado, Short espSeq) throws ApplicationBusinessException;
	
	List<AghEspecialidades> pesquisarSelecaoEspecialidades(String parametro);
	Long pesquisarSelecaoEspecialidadesCount(String parametro);
	
	List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSiglaAtivo(String param);
	Long pesquisarEspecialidadePorNomeOuSiglaAtivoCount(String param);
	/**
	 * #27521 #8236 #8233
	 * Busca as especialidades por sigla ou nome ordenadas por sigla
	 * para sugestionBox de especialidades.
	 * @param parametro
	 * @return
	 */
	public List<AghEspecialidades> obterListaEspSiglaOuNomeOrdSigla(String parametro);
	
	/**
	 * Obtém informações de Atendimento relacionado a uma Justificativa de Uso a partir do Id do Atendimento.
	 * @ORADB CGFK$QRY_JUM_MPM_JUM_ATD_FK1
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Informações de Atendimento
	 */
	AghAtendimentos obterAtendimentoJustificativaUsoPorId(Integer atdSeq);
	
	Long obterUnidadesFuncionaisSBCount(Object param);
	
	AghAtendimentos obterAtendimentoPorPacienteDataInicioOrigem(final Integer pacCodigo, final Date dthrInicio, DominioOrigemAtendimento[] dominioOrigemAtendimento) ;
	Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigo);
	
	Integer obterUltimaConsultaDaPaciente(Integer pacCodigo, List<ConstanteAghCaractUnidFuncionais> caracts);

	List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacaoEDataDeInternacao(
			Integer seq, Integer gsoPacCodigo, Short gsoSeqp,
			Date dataInternacaoAdministrativa, Date dtAltaAdministrativa);
	
	List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacao(
			Integer internacaoSeq, Integer gsoPacCodigo, Short gsoSeqp);
	
	List<CidVO> obterCidPorDominioVivoMorto(DominioVivoMorto vivo, DominioVivoMorto morto);
	
	List<AghAtendimentosPacienteCnsVO> buscarAtendimentosFetchAipPacientesDadosCns(Integer adtSeq);
	
	AghAtendimentos pesquisarAtendimentosComOrigemUrgencia(final Integer pacCodigo, final Short gsoSeqp);
	
	AghAtendimentos pesquisarAtendimentosComOrigemAmbulatorial(final Integer pacCodigo, final Short gsoSeqp);
	
	
	List<AghCid> pesquisarCidPorCodigoDescricaoSGB(String param);
	
	Long contarCidsPorDescricaoOuIdPassandoExame(String string,
			DominioSexoDeterminante dominioSexoDeterminante, String sigla,
			Integer manSeq);
	
	List<AghCid> pesquisarCidsPorDescricaoOuIdPassandoExame(
			final String descricao, final Integer limiteRegistros,
			final DominioSexoDeterminante sexoPac, final String sigla,
			final Integer manSeq);

	boolean verificarPacienteInternadoCaracteristicaControlePrevisao(List<Short> unidadesFuncionaisSeq, Integer atendimentoSeq) throws ApplicationBusinessException;
	
	/**
	 * 42803
	 */
	LaudoSolicitacaoAutorizacaoProcedAmbVO obterEquipePorSeq(Integer seq);

	AtendimentoSolicExameVO obterAtendimentoSolicExameVO(Integer atdSeq); 
	List<AghUnidadesFuncionais> obterUnidadesFuncionaisComLeitosEPodemSolicitarExames(
			Object parametro);

	Long obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(
			Object parametro);
	List<AghCid> obterCidPorNomeCodigoAtivaPaginado(String param, SigProcessamentoCusto competencia);
		List<AghEspecialidadeVO> pesquisarEspecialidadesPorSiglaNomeCodigo(String pesquisa)throws ApplicationBusinessException;
	Long pesquisarEspecialidadesPorSiglaNomeCodigoCount(String pesquisa) throws ApplicationBusinessException;
	List<AghEquipes> pesquisarEquipes(String pesquisa);
	Long pesquisarEquipesCount(String pesquisa);
	Long obterCidPorNomeCodigoAtivaCount(String param, SigProcessamentoCusto processamentoCusto);
	
	List<AghAtendimentos> pesquisarPacientesInternados(AipPacientes aipPaciente, AinQuartos quarto, AinLeitos leito, AghUnidadesFuncionais unidadeFuncional);
	
	public Integer obterAghAtendimentoPorProntuario(Integer prontuario);

	List<AghAtendimentos> listarAtendimentosPorNumeroConsultaOrdenado(Integer conNumero);

	String obterDescricaoEquipe(Integer matricula, Short vinCodigo);

	AghAtendimentos obterAtendimentoAtual(AipPacientes paciente);

	List<AghCaractUnidFuncionais> pesquisarCaracteristicasUnidadeFuncionalPorCaracteristica(
			List<ConstanteAghCaractUnidFuncionais> listaCaracteristicas);
	
	List<AghEspecialidades> listarEspecialidadesPorServidor(Integer matricula, Short vinCodigo);

	AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(Integer pacCodigo);
	
	void gravarPesquisaMenuLog(String nome, String url,
			RapServidores servidorLogado);

	List<AghHorariosUnidFuncional> pesquisarAghHorariosUnidFuncional(
			Short unfSeq, DominioTipoDia tipoDia, Date hrInicial, Date hrFinal,
			Boolean indPlantao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	AghAtendimentos obterAghAtendimentosAnamneseEvolucao(Integer seqAtendimento);
	
	public List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas);

	List<Integer> pesquisarIdsArquivosAbortados(AghSistemas sistema,
			Integer minutosVencimento);

	AghArquivoProcessamento obterArquivoNaoProcessadoVO(AghSistemas sistema,
			String nome);

	Long pesquisarListaAghEspecialidadeCount(String parametro);

	List<AghEspecialidades> pesquisarListaAghEspecialidade(String parametro);

	Long pesquisarListaAghCidCount(String parametro);

	List<AghCid> pesquisarListaAghCid(String parametro);

	void atualizarAghArquivoProcessamento(Integer seq, Date date,
			Integer percent, Date fimProcessamento);

	String pesquisarLogsPorArquivosIds(List<Integer> arquivos);	
	
	public Short obtemUnfSeqPorAlmoxarifado(Short almSeq);

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(
			String strPesquisa, String ordernarPorAndar, boolean apenasAtivos,
			Object[] caracteristicas);

}