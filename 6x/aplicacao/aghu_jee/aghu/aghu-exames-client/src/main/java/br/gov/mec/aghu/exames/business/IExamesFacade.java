package br.gov.mec.aghu.exames.business;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaLoteExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaResultadoCargaInterfaceVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaClinicaVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.AmostraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ResultadoExamePim2VO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostraRecebidaVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelExamesXAelParametroCamposLaudoVO;
import br.gov.mec.aghu.exames.vo.AelExtratoAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExameRelLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.vo.AelMotivoCancelaExamesVO;
import br.gov.mec.aghu.exames.vo.AtendimentoExternoVO;
import br.gov.mec.aghu.exames.vo.CartaRecoletaVO;
import br.gov.mec.aghu.exames.vo.ExameDisponivelFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameEspecialidadeSelecionadoFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameSelecionadoFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exames.vo.LiberacaoLimitacaoExameVO;
import br.gov.mec.aghu.exames.vo.MascaraExameVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesVO;
import br.gov.mec.aghu.exames.vo.PendenciaExecucaoVO;
import br.gov.mec.aghu.exames.vo.PesquisaResultadoCargaVO;
import br.gov.mec.aghu.exames.vo.RelatorioAgendamentoProfissionalVO;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPendentesVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaBioquimicaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaEpfVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHematologiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHemoculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaUroculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarVO;
import br.gov.mec.aghu.exames.vo.RelatorioTicketAreaExecutoraVO;
import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.exames.vo.SecaoConfExameVO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.faturamento.vo.AtendPacExternPorColetasRealizadasVO;
import br.gov.mec.aghu.faturamento.vo.AtendimentoCargaColetaVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesHistoricoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.ResultadoExamesVO;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;

@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation"})
public interface IExamesFacade extends Serializable {

	static final String ARQUIVOS_IMPORTACAO_EXAMES = "arquivos";

	AelMateriaisAnalises obterMaterialAnalisePeloId(final Integer codigo);
	/**  * #47146 - Método para chamada da Function 1  */
	String obterResultadoExame(AelResultadoExame ree);

	List<AelMateriaisAnalises> pesquisarMateriasAnalise(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelMateriaisAnalises elemento);

	AelAtendimentoDiversos obterAelAtendimentoDiversosPorChavePrimaria(final Integer seq);

	AghMedicoExterno obterMedicoExternoPorId(final Integer seq);
	
	public AghMedicoExterno obterMedicoExternoPorPK(final Integer seq);

	Long pesquisarAmostraItemExamesCount(AghUnidadesFuncionais unidadeExecutora, Integer solicitacao, Integer amostra, AelEquipamentos equipamento, String sigla,
			DominioSimNao enviado);

	Boolean realizarCargaInterfaceamento(Set<AelAmostraItemExamesVO> listAmostraItemExamesVO, String nomeMicrocomputador ) throws BaseException;

	List<AelSalasExecutorasExames> obterSalaExecutoraExamesPorUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, Object param);

	void atualizarAghAtendimentosPacExtern(AghAtendimentosPacExtern aghAtendimentosPacExtern, String nomeMicrocomputador, RapServidores servidor) throws BaseException;

	List<AghAtendimentosPacExtern> listarAtendimentosPacExternPorCodigoPaciente(Integer pacCodigo);

	AghAtendimentosPacExtern obterAghAtendimentosPacExternPorChavePrimaria(Integer seq);

	Long pesquisarMateriasAnaliseCount(final AelMateriaisAnalises elemento);

	void persistirAelAnatomoPatologico(final AelAnatomoPatologico aelAnatomoPatologico ) throws BaseException;

	AelRegiaoAnatomica obterRegiaoAnatomicaPeloId(final Integer codigo);

	List<AelRegiaoAnatomica> pesquisarRegioesAnatomicas(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelRegiaoAnatomica elemento);

	Long pesquisarRegioesAnatomicasCount(final AelRegiaoAnatomica elemento);
	
	AelConfigMapa obterAelConfigMapaPorSeq(final Short seq);

	List<AelExamesMaterialAnalise> buscarAelExamesMaterialAnalisePorAelExames(final AelExames aelExames);

	AelExamesMaterialAnalise buscarAelExamesMaterialAnalisePorId(final String exaSigla, final Integer manSeq);

	List<AelTipoAmostraExame> buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(final String emaExaSigla, final Integer emaManSeq);

	AelExames obterAelExamesPeloId(final String sigla);

	boolean verificarExistenciaSolicitacoesExameComRetornoPeloNumConsulta(final Integer numero);

	AelSolicitacaoExames obterAelSolicitacaoExamesPeloId(final Integer seq);

	Long pesquisarDadosBasicosExamesCount(final AelExames aelExames, AghUnidadesFuncionais unidade);

	List<AelExames> pesquisarDadosBasicosExames(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, final AelExames elemento,
			final AghUnidadesFuncionais unidade);

	AelSinonimoExame obterAelSinonimoExamePorId(final AelSinonimoExameId id);

	void atualizarAelResultadoExame(AelResultadoExame elemento, String nomeMicrocomputador ) throws BaseException;
	
	void inserirAelResultadoExame(AelResultadoExame elemento) throws BaseException;

	void inserirAelNotaAdicional(AelNotaAdicional notaAdicional ) throws BaseException;

	void inserirNotaAdicional(final AelNotaAdicional notaAdicional ) throws BaseException;

	List<AelUnidMedValorNormal> pesquisarDadosBasicosUnidMedValorNormal(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelUnidMedValorNormal elemento);

	Long pesquisarAelUnidMedValorNormalCount(final AelUnidMedValorNormal aelUnidMed);

	AelUnidMedValorNormal obterAelUnidMedValorNormalPeloId(final Integer codigo);
	
	List<VRapServCrmAelVO> obterListaResponsavelLiberacao(String paramPesquisa, boolean filtrarMatricula) throws ApplicationBusinessException;
	
	/**
	 * Realiza a pesquisa de registros da tabela AEL_RECIPIENTES_COLETA.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	List<AelRecipienteColeta> pesquisaRecipienteColetaList(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelRecipienteColeta elemento);

	Long countRecipienteColeta(final AelRecipienteColeta elemento);

	AelRecipienteColeta obterRecipienteColetaPorId(final Integer codigo);

	List<AelMateriaisAnalises> listarAelMateriaisAnalises(final Object parametro);

	List<AelMateriaisAnalises> listarAelMateriaisAnalisesAtivoColetavel(final Object parametro);

	List<AelRecipienteColeta> listarAelRecipienteAtivoColetavel(final Object parametro);

	List<AelAnticoagulante> listarAelAnticoagulanteAtivo(final Object parametro);

	List<AelModeloCartas> listarAelModeloCartas(final Object parametro);

	List<MateriaisColetarEnfermagemVO> pesquisarRelatorioMateriaisColetaEnfermagem(final AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException;

	List<MateriaisColetarEnfermagemAmostraVO> pesquisarMateriaisColetaEnfermagemAmostra(AghUnidadesFuncionais unidadeFuncional, Integer prontuarioPaciente, Integer soeSeq,
			DominioSituacaoAmostra situacao) throws ApplicationBusinessException;

	List<MateriaisColetarEnfermagemAmostraItemExamesVO> pesquisarMateriaisColetarEnfermagemPorAmostra(Integer amoSoeSeq, Short amoSeqp);

	List<AelIntervaloColeta> listarIntervalosColetaPorExameMaterial(final String siglaExame, final Integer codigoMaterial);

	List<AelRefCode> obterCodigosPorDominio(final String dominio);

	void inserirAelExameConselhoProfs(final AelExameConselhoProfs aelExameConselhoProfs) throws ApplicationBusinessException;

	void inserirAelCopiaResultados(final AelCopiaResultados aelCopiaResultados ) throws ApplicationBusinessException;

	AelVersaoLaudo obterVersaoLaudoComDependencias(final AelVersaoLaudoId versaoLaudoPK);
		
	void atualizarAelCopiaResultados(final AelCopiaResultados aelCopiaResultados ) throws ApplicationBusinessException;

	void removerAelCopiaResultados(final AelCopiaResultados aelCopiaResultados) throws BaseException;

	AelGrupoRecomendacao obterAelGrupoRecomendacaoPeloId(final Integer seq);
	
	AelGrupoRecomendacao obterAelGrupoRecomendacaoPeloId(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	AelExamesMaterialAnalise obterAelExamesMaterialAnalisePorId(final AelExamesMaterialAnaliseId id);

	void inserirAelOrdExameMatAnalise(final AelOrdExameMatAnalise aelOrdExameMatAnalise ) throws ApplicationBusinessException;

	// Anticoagulantes
	List<AelAnticoagulante> pesquisarDadosBasicosAnticoagulantes(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelAnticoagulante elemento);

	Long pesquisarAelAnticoagulanteCount(final AelAnticoagulante aelUnidMed);

	AelAnticoagulante obterAelAnticoagulantePeloId(final Integer codigo);

	List<AelUnfExecutaExames> buscaListaAelUnfExecutaExames(final String sigla, final Integer manSeq, final Boolean ativo);

	RelatorioFichaTrabalhoPatologiaVO obterSolicitacaoAtendimento(final Integer soeSeq, final Short unfSeq);
	                                               
	List<RelatorioFichaTrabalhoPatologiaClinicaVO> obterFichaTrabPorExame(Integer amoSoeSeq, Short amoSoeSeqP, Boolean recebeAmostra, Short unfSeq);

	RelatorioFichaTrabalhoPatologiaVO obterFichaTrabAmostra(Integer soeSeq, Short seqP, Short unfSeq) throws ApplicationBusinessException;

	AelSitItemSolicitacoes pesquisaSituacaoItemExame(final String valor);

	AelUnidExecUsuario obterUnidExecUsuarioPeloId(final RapServidoresId id);

	List<AacConsultas> listarSituacaoExames();

	/** #5962 Atendiment paciente externo - inicio **/
	List<AghMedicoExterno> obterMedicoExternoList(final String parametro);

	Long obterMedicoExternoListCount(final String parametro);

	List<AelLaboratorioExternos> obterLaboratorioExternoList(final String parametro);

	List<AtendimentoExternoVO> obterAtendimentoExternoList(final Integer codigoPaciente);

	List<FatConvenioSaudePlano> listarConvenioSaudePlanos(final String parametro);

	Integer listarConvenioSaudePlanosCount(final String parametro);

	AghAtendimentosPacExtern gravarAghAtendimentoPacExtern(final AghAtendimentosPacExtern atendimentosPacExtern, String nomeMicrocomputador, RapServidores servidorLogado)
			throws BaseException;

	List<AelRecomendacaoExame> obterRecomendacoesExames(final String sigla, final Integer manSeq);

	AelRecomendacaoExame obterAelRecomendacaoExamePorID(final AelRecomendacaoExameId id);

	List<AelExamesProva> obterAelExamesProva(final String sigla, final Integer manSeq);

	AelExamesProva obterAelExamesProvaPorId(final AelExamesProvaId id);

	List<AelPermissaoUnidSolic> buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq);

	List<AelExamesEspecialidade> buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq);

	List<AelExameHorarioColeta> listaHorariosColetaExames(final String sigla, final Integer manSeq);

	AelExameHorarioColeta obterExameHorarioColetaPorID(final AelExameHorarioColetaId id);

	AelUnfExecutaExames obterAelUnidadeExecutoraExamesPorID(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq);

	FluxogramaLaborarorialVO pesquisarFluxograma(final Short especialidade, final Integer prontuario , boolean historicos) throws ApplicationBusinessException;

	String verificaNormalidade(final FluxogramaLaborarorialDadosVO dadoVO, final Date dataEvento) throws ParseException;

	List<AelServidoresExameUnid> buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq);

	/**
	 * Conselhos Profissionais que Solicitam Exame
	 */
	List<RapConselhosProfissionais> listarConselhosProfsExame(final Object parametro) throws ApplicationBusinessException;

	List<AelExameConselhoProfs> listaConselhosProfsExame(final String sigla, final Integer manSeq);

	AelExameConselhoProfs obterConselhosProfsExamePorID(final AelExameConselhoProfsId id);

	void removerAelExameConselhoProfs(final AelExameConselhoProfs exameConselhoProfs);

	List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(final Integer codSolicitacao, Short unfSeq) throws ApplicationBusinessException;

	List<PendenciaExecucaoVO> pesquisaExamesPendentesExecucao(final Short p_unf_seq, final Integer p_grt_seq, final Date dtInicial, final Date dtFinal,
			final Integer numUnicoInicial, final Integer numUnicoFinal) throws ApplicationBusinessException;

	List<AelSitItemSolicitacoes> pesquisarSituacaoExame(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelSitItemSolicitacoes elemento);

	Long pesquisarSituacaoExameCount(final AelSitItemSolicitacoes elemento);

	AelSitItemSolicitacoes obterSituacaoExamePeloId(final String codigo);

	List<AelSitItemSolicitacoes> listarTodosPorSituacaoEMostrarSolicExames(final DominioSituacao indSituacao, final Boolean indMostraSolicitarExames);

	List<AelItemSolicitacaoExames> pesquisarCarregarArquivoLaudoResultadoExame(final AghUnidadesFuncionais unidadeExecutora, final AelSolicitacaoExames solicitacaoExame,
			final Short seqp) throws BaseException;

	List<AelItemSolicitacaoExames> pesquisarInformarSolicitacaoExameDigitacaoController(final Integer solicitacaoExameSeq, final Integer amostraSeqp,
			final Short seqUnidadeFuncional) throws ApplicationBusinessException;

	List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExames(final Integer iseSoeSeq, final Short iseSeqp);

	List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExamesHist(final Integer iseSoeSeq, final Short iseSeqp);

	void persistirVisualizacaoDownloadAnexo(final Integer iseSoeSeq, final Short iseSeqp ) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void persistirVisualizacaoDownloadAnexoHist(final Integer iseSoeSeq, final Short iseSeqp ) throws ApplicationBusinessException,
			ApplicationBusinessException;

	String obterDescricaoVAghUnidFuncional(final Short seq) throws BaseException;

	Boolean existeDocumentoAnexado(final Integer iseSoeSeq, final Short iseSeqp);

	AelDocResultadoExame obterDocumentoAnexado(final Integer iseSoeSeq, final Short iseSeqp);

	void inserirAelDocResultadoExame(final AelDocResultadoExame doc, final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador )
			throws BaseException;

	void removerAelDocResultadoExame(final AelDocResultadoExame doc, final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador )
			throws BaseException;

	AelItemSolicitacaoExames buscaItemSolicitacaoExamePorId(final Integer soeSeq, final Short seqp);

	AelItemSolicExameHist buscaItemSolicitacaoExamePorIdHist(final Integer soeSeq, final Short seqp);

	AelItemSolicExameHist buscaItemSolicitacaoExamePorIdHistOrigemPol(final Integer soeSeq, final Short seqp);

	List<AelNotaAdicional> pesquisarNotaAdicionalPorSolicitacaoEItem(final Integer soeSeq, final short seqp);

	void insereVisualizacaoItemSolicitacao(final AelItemSolicConsultado itemSolicConsultado , Boolean flush)throws ApplicationBusinessException ;

	void insereVisualizacaoItemSolicitacaoHist(final AelItemSolicConsultadoHist itemSolicConsultado , Boolean flush) throws ApplicationBusinessException ;

	List<AelSolicitacaoExames> buscarAelSolicitacaoExames(final String valor);

	List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(final String valor);

	List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(final String valor, final String... codigosRestritivos);
	
	List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesParaExamesPendentes(String parametro);
	
	Long buscarListaAelSitItemSolicitacoesParaExamesPendentesCount(String parametro);

	void excluirPedidosExamesPorAtendimento(final AghAtendimentos atendimento);

	AelAmostras buscarAmostrasPorId(final Integer soeSeq, final Short seqp);

	String cancelarEdicaoFrasco(final AelAmostrasId amostrasId);

	List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp);

	List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final Integer soeSeq, final Short amostraSeqp);

	List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final Integer soeSeq, final Short amostraSeqp)
			throws BaseException;

	List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp) throws BaseException;

	List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostra(final Integer amoSoeSeq, final Integer amoSeqp);

	List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostraComApNotNull(final Integer amoSoeSeq, final Integer amoSeqp);

	ImprimeEtiquetaVO receberAmostra(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, final String nroFrascoFabricante,
			final List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException;

	void verificarModoInterfaceamento(final AelAmostras amostra, final boolean cancelaInterfaceamento, String nomeMicrocomputador ) throws BaseException;

	Integer imprimirEtiquetaAmostra(final AelAmostras amostra, final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador ) throws BaseException;

	ImprimeEtiquetaVO receberAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException;

	boolean voltarAmostra(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador )
			throws BaseException;

	boolean voltarSituacaoAmostraSolicitacao(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador )
			throws BaseException;

	String comporMensagemConfirmacaoImpressaoEtiquetas(final AelAmostras amostra) throws BaseException;

	List<AelExamesMaterialAnalise> listarExamesMaterialAnalise(final Object objPesquisa);

	List<AelGrupoExameUsual> obterGrupoPorCodigoDescricao(final Object objPesquisa);

	Long listarExamesMaterialAnaliseCount(final Object objPesquisa);

	// 2244 - Solicitar exames em lote
	List<AelLoteExameUsual> getLoteDefaultEspecialidade(final AghEspecialidades especialidade);

	AelLoteExameUsual getLoteExameUsualPorSeq(final Short seq);

	
	Long pesquisaLotesPorParametrosCount(final PesquisaLoteExamesFiltroVO filtro);

	List<AelLoteExameUsual> pesquisaLotesPorParametros(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final PesquisaLoteExamesFiltroVO filtro);

	List<AelLoteExameUsual> getLoteDefaultGrupo();

	List<AelLoteExameUsual> getLoteDefaultUnidade(final SolicitacaoExameVO solicitacaoExameVO);

	List<AelLoteExame> pesquisaLotesExamesPorLoteExameUsual(final Short leuSeq);

	void persistirAelAmostraItemExames(final AelAmostraItemExames itemAmostra, final Boolean flush, String nomeMicrocomputador )
			throws BaseException;

	void inserirAmostraItemExame(final AelAmostraItemExames itemAmostra) throws BaseException;

	void removerAmostraItemExame(final AelAmostraItemExames amostraItemExames);

	void atualizarAelAmostraItemExames(final AelAmostraItemExames itemAmostra, final Boolean flush, final Boolean atualizaItemSolic, String nomeMicrocomputador) throws BaseException;

	void persistirAelAmostra(final AelAmostras amostra, final Boolean flush ) throws BaseException;

	void atualizarAmostra(final AelAmostras amostra, final Boolean flush ) throws BaseException;

	void inserirAmostra(final AelAmostras amostra ) throws BaseException;

	void removerAmostra(final AelAmostras amostra) throws BaseException;

	boolean validarPermissaoAlterarExameSituacao(final AelMatrizSituacao matrizSituacao, final RapServidores userLogado);

	AelControleNumeroUnico atualizarControleNumeroUnicoUp(final AelControleNumeroUnico controleNumeroUnicoUp) throws BaseException;

	void inserirExtratoItemSolicitacao(final AelExtratoItemSolicitacao extrato, final boolean flush) throws BaseException;

	List<AelExtratoItemSolicitacaoVO> pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacao(
			final Integer iseSoeSeq, final Short iseSeqp, Boolean isHist);

	void atualizarHorarioExameDisp(final AelHorarioExameDisp horarioExame) throws BaseException;

	void inserirHorarioExameDisp(final AelHorarioExameDisp horarioExameDisp ) throws BaseException;

	void removerHorarioExameDisp(final AelHorarioExameDisp horarioExameDisp);

	void removerItemHorarioAgendado(final AelItemHorarioAgendado horariosAgendado, Boolean flush, String nomeMicrocomputador, AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws BaseException;

	void cancelarHorariosPorItemSolicitacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador )
			throws BaseException;

	void cancelarHorariosExamesAgendados(AelItemHorarioAgendado itemHorarioAgendado, Short globalUnfSeq, String nomeMicrocomputador )
			throws BaseException;

	void persistirItemSolicCartas(final AelItemSolicCartas aelItemSolicCartas, final Boolean flush ) throws ApplicationBusinessException,
			ApplicationBusinessException;

	List<RelatorioTicketAreaExecutoraVO> pesquisarRelatorioTicketAreaExecutora(final Integer soeSeq, final Short unfSeq, String nomeMicrocomputador )
			throws BaseException;

	List<AelProjetoPesquisas> pesquisarTodosProjetosPesquisa(final String strPesquisa);
	
	List<AelProjetoPesquisas> pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNome(Object objPesquisa);
	
	Long pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNomeCount(Object objPesquisa);

	Long pesquisarTodosProjetosPesquisaCount(final String strPesquisa);

	RelatorioPacientesInternadosExamesRealizarVO pesquisarRelatorioPacientesInternadosExamesRealizar(final AghUnidadesFuncionais unidadeFuncional,
			final DominioSimNao imprimeRecomendacoesExame, final Boolean jejumNpo, final Boolean preparo, final Boolean dietaDiferenciada, final Boolean unidadeEmergencia,
			final Boolean todos) throws ApplicationBusinessException;

	List<TicketExamesPacienteVO> pesquisarRelatorioSolicitacaoExamesCertificacaoDigital(final Integer codSolicitacao) throws ApplicationBusinessException;

	String buscarLaudoProntuarioPaciente(final IAelSolicitacaoExames solicitacaoExames);

	String buscarLaudoProntuarioPaciente(final AelSolicitacaoExames solicitacaoExames);

	String buscarLaudoProntuarioPacienteHist(final AelSolicitacaoExamesHist solicitacaoExames);

	String buscarLaudoNomePaciente(final IAelSolicitacaoExames solicitacaoExames);
	
	String buscarLaudoNomePaciente(final AelSolicitacaoExames solicitacaoExames);

	String buscarLaudoNomePacienteHist(final AelSolicitacaoExamesHist solicitacaoExames);
	
	String buscarLaudoNomeMaeRecemNascido(final AelSolicitacaoExames solicitacaoExames);

	String buscarLaudoNomeMaeRecemNascido(final IAelSolicitacaoExames solicitacaoExames);

	Integer buscarLaudoCodigoPaciente(final AelSolicitacaoExames solicitacaoExames);

	Integer buscarLaudoCodigoPacienteHist(final AelSolicitacaoExamesHist solicitacaoExames);

	Date buscarLaudoDataNascimento(final AelSolicitacaoExames solicitacaoExames);
	
	String buscarNomeServ(final Integer matricula, final Short vinCodigo);
	
	String buscarNroRegistroConselho(final Short vinCodigo, final Integer matricula, final boolean verificaDataFimVinculo);
	
	List<AelExtratoAmostrasVO> pesquisarAelExtratosAmostrasVOPorAmostra(
			final Integer amoSoeSeq, final Short amoSeqp, Boolean isHist);

	List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(final Object parametro, final AghUnidadesFuncionais unidadeExecutora);
	
	List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorNumeroEUnidade(final Object parametro, final AghUnidadesFuncionais unidadeExecutora);

	List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(final Object parametro, final AghUnidadesFuncionais unidadeExecutora);
	
	List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidadeAtivos(final Object parametro, final AghUnidadesFuncionais unidadeExecutora);

	List<VAelUnfExecutaExames> pesquisarExamePorDescricaoOuSigla(final Object parametro);

	List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(final Object parametro);

	Long pesquisaSalasExecutorasExamesCount(final AelSalasExecutorasExames filtro);

	List<AelSalasExecutorasExames> pesquisaSalasExecutorasExames(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelSalasExecutorasExames filtro);

	/*
	 * Listar Amostras Exames
	 * 
	 * @throws BaseException
	 */
	List<AelAmostrasVO> listarAmostrasExamesVO(final Integer soqSeq,
			Boolean isHist) throws BaseException;
	
	List<AelAmostrasVO> listarAmostrasExamesVOPorAtendimento(final Short hedGaeUnfSeq,
			final Integer hedGaeSeqp, final Date hedDthrAgenda, Boolean isHist) throws BaseException;

List<AelAmostraExamesVO> listarItensAmostra(final Integer soeSeq,
			final Short iseSeqp, final Integer amoSeqp, Boolean isHist) throws BaseException;

	Long pesquisarMotivoCancelamentoCount(final AelMotivoCancelaExames aelMotivoCancelaExames);

	List<AelMotivoCancelaExames> pesquisarMotivoCancelamento(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelMotivoCancelaExames aelMotivoCancelaExames);

	AelMotivoCancelaExames obterMotivoCancelamentoPeloId(final Short codigoMotivoCancelamentoExclusao);

	
	Long pesquisarAelGrupoExameTecnicasCount(final AelGrupoExameTecnicas elemento);

	boolean possuiExameCancelamentoSolicitante(final Integer soeSeq, final Short iseSoeSeq);

	
	List<AelGrupoExameTecnicas> pesquisarAelGrupoExameTecnicas(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelGrupoExameTecnicas elemento);

	AelGrupoExameTecnicas obterAelGrupoExameTecnicasPeloId(final Integer seq);

	AelGrpTecnicaUnfExames buscarAelGrpTecnicaUnfExamesPorId(final AelGrpTecnicaUnfExamesId id);

	List<AelGrupoExameTecnicas> obterGrupoExameTecnicasPorDescricao(final Object objPesquisa);

	List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoLiberado(final Object solicitacaoExameSeq, final String situacaoExameLiberado);

	Long pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(final Object solicitacaoExameSeq, final String situacaoExameLiberado);

	AelCampoLaudo obterCampoLaudoPorSeq(final Integer seq);

	Long pesquisarCampoLaudoCount(final AelCampoLaudo campoLaudo);

	Long pesquisarCampoLaudoFluxogramaCount(final AelCampoLaudo campoLaudo);

	List<AelCampoLaudo> pesquisarCampoLaudo(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, final AelCampoLaudo campoLaudo);

	List<AelCampoLaudo> pesquisarCampoLaudoFluxograma(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelCampoLaudo campoLaudo);

	List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(final Object objPesquisa);

	List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificadoPorSeqDescricao(final Object objPesquisa);

	AelCampoLaudo obterAelCampoLaudoId(final Integer seq);

	List<AelCampoLaudo> pesquisarAelCampoLaudoTipo(final DominioTipoCampoCampoLaudo tipo, final Object pesquisa);

	/*
	 * #5366 Manter Valores de Normalidade do Campo Laudo
	 */
	List<AelValorNormalidCampo> pesquisarNormalidadesCampoLaudo(final Integer seq);

	void inserirValoresNormalidadeCampo(final AelValorNormalidCampo valorNormalidCampo ) throws BaseException;

	void atualizarValoresNormalidadeCampo(final AelValorNormalidCampo valorNormalidCampo) throws BaseException;

	List<AelSinonimoCampoLaudo> pesquisarSinonimoCampoLaudoPorCampoLaudo(final AelCampoLaudo campoLaudo);

	List<AelMetodo> pesquisarMetodo(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, final AelMetodo elemento);

	Long pesquisarMetodoCount(final AelMetodo aelMetodo);

	AelMetodo obterAelMetodoId(final Short codigo);

	void atualizarMetodo(final AelMetodo metodo ) throws BaseException;

	void inserirMetodo(final AelMetodo metodo ) throws BaseException;

	void ativarInativarAleMetodo(AelMetodo metodo) throws BaseException;
	
	AelResultadosPadrao obterResultadosPadraoPorSeq(final Integer seq);

	Long pesquisarResultadosPadraoCount(final AelResultadosPadrao resultadosPadrao);

	List<AelResultadosPadrao> pesquisarResultadosPadrao(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelResultadosPadrao resultadosPadrao);

	List<AelCopiaResultados> pesquisarCopiaResultadosPorExameMaterialAnalise(final AelExamesMaterialAnalise exameMaterialAnalise);

	AelCopiaResultados obterAelCopiaResultadosId(final AelCopiaResultadosId id);

	void persistirExamesFluxogramaSelecionados(final RapServidores servidor, final List<ExameSelecionadoFluxogramaVO> listaExamesSelecionados) throws BaseException;

	List<ExameDisponivelFluxogramaVO> pesquisarExamesDisponiveisFluxograma();

	boolean pertenceAoFluxograma(final Integer campoLaudoSeq);

	List<ExameSelecionadoFluxogramaVO> pesquisarExamesSelecionadosPorServidorLogado() throws ApplicationBusinessException;

	void persistirExamesFluxogramaSelecionadosPorEspecialidade(final AghEspecialidades especialidade, 
			final List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesSelecionados, 
			List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesRemovidos)
			throws BaseException;

	List<ExameEspecialidadeSelecionadoFluxogramaVO> pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(final AghEspecialidades especialidade);

	/**
	 * 
	 * ORADB TRIGGER AELT_AIX_BRI
	 * 
	 * @param entity
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	AelInformacaoSolicitacaoUnidadeExecutora inserirInformacaoSolicitacaoUnidadeExecutoraRN(final AelInformacaoSolicitacaoUnidadeExecutora entity )
			throws ApplicationBusinessException;

	/**
	 * oradb aelc_busca_conv_plan
	 * 
	 * @param seq
	 * @param cnvCodigo
	 * @return
	 */
	String buscarConvenioPlano(final FatConvenioSaudePlano convenioSaudePlano);

	/**
	 * ORADB: AELC_GET_PROJ_ATEND ESCHWEIGERT -> (23/04/2012) Deve ser público e mapeado para outros programas poderem utilizar.
	 * 
	 * @param atendimento
	 * @param atendimentoDiversos
	 * @return
	 */
	String aelcGetProjAtend(final Integer atdSeq, final Integer atvSeq);

	AelVersaoLaudo obterVersaoLaudoPorChavePrimaria(final AelVersaoLaudoId versaoLaudoPK);

	AelExameGrupoCaracteristica obterAelExameGrupoCaracteristicaPorId(final AelExameGrupoCaracteristicaId grpCaractId);

	List<AelResultadoCodificado> pesquisaResultadosCodificadosPorParametros(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final ResultadosCodificadosVO filtroResultado);

	List<AelGrupoResultadoCodificado> pesquisaGrupoResultadosCodificadosPorParametros(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final ResultadosCodificadosVO filtroResultado);

	void persistirResultadoCodificado(final AelResultadoCodificado resultado ) throws BaseException;

	void persistirGrupoResultadoCodificado(final AelGrupoResultadoCodificado grupoResultado ) throws BaseException;

	void removerResultadoCodificado(final AelResultadoCodificadoId resultado) throws BaseException;

	void removerGrupoResultadoCodificado(final Integer grupoResultado) throws BaseException;

	AelResultadoCodificado obterResultadoCodificadoPorId(final AelResultadoCodificadoId resulCodId);

	AelGrupoResultadoCodificado obterGrupoResultadoCodificadoPorSeq(final Integer grupoResulCodId);

	Long pesquisaResultadosCodificadosPorParametrosCount(final ResultadosCodificadosVO filtroResultado);

	Long pesquisaGrupoResultadosCodificadosPorParametrosCount(final ResultadosCodificadosVO filtroResultado);

	Long listarMascarasExamesVOCount(final Integer soeSeq, final Short seqp, final AelExames exame, final AelMateriaisAnalises materialAnalise, final AipPacientes paciente);

	List<MascaraExameVO> listarMascarasExamesVO(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, final Integer soeSeq,
			final Short seqp, final AelExames exame, final AelMateriaisAnalises materialAnalise, final AipPacientes paciente);

	AelItemSolicitacaoExameRelLaudoUnicoVO obterAelItemSolicitacaoExameRelLaudoUnicoVOPorLuxSeq(final Long seq);

	AelSalasExecutorasExames obterSalaExecutoraExamesPorId(final AelSalasExecutorasExamesId id);

	Long listarExamesCount(final String strPesquisa);

	List<AelExames> listarExames(final String strPesquisa);

	Long listarMateriaisAnaliseCount(final String strPesquisa);

	List<AelMateriaisAnalises> listarMateriaisAnalise(final String strPesquisa);

	List<AelUnidMedValorNormal> pesquisarUnidadesValorNormal(final Object parametroConsulta);

	List<AelUnidMedValorNormal> pesquisarUnidadesValorNormal();

	List<AelMetodoUnfExame> obterAelMetodoUnfExamePorUnfExecutaExames(final AelUnfExecutaExames unfExecutaExames);

	List<AelMetodo> obterMetodosPorSerDescricao(final Object parametro);

	Long pesquisarResultadosCaracteristicasCount(final AelResultadoCaracteristica resultadoCaracteristica);

	List<AelResultadoCaracteristica> pesquisarCaracteristicasResultados(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final AelResultadoCaracteristica elemento);
/*
	List<DesenhoMascaraExameVO> buscaDesenhosMascarasExamesVO(final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq, final Integer velSeqp, Boolean isHist, Map<Integer, NumeroApTipoVO> solicNumeroAp)
			throws BaseException;

	List<DoubleRangeValidator> obterListaValidatorsValoresNormalidade(AelParametroCamposLaudo parametroCampoLaudo, AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException;

	String retornaParametroCampoLaudoTextoLivreSemTag(String textoLivre);

	List<DesenhoMascaraExameVO> buscarDesenhosMascarasExamesResultadoPadraoVO(AelVersaoLaudo versaoLaudo) throws BaseException;

	List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesResultadoPadrao(final AelResultadosPadrao resultadoPadrao, final Integer velSeqp, final Integer solicitacaoExameSeq,
			final Short itemSolicitacaoExameSeq) throws BaseException;

	DesenhoMascaraExameVO buscaPreviaMascarasExamesVO(final List<AelParametroCamposLaudo> parametrosPrevia, final AelVersaoLaudo versaoLaudo, Boolean isPdf)
			throws BaseException;

	List<DesenhoMascaraExameVO> buscaDesenhosRelatorioMascarasExamesVO(final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq,
			final DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo, final Boolean isPdf, Boolean isHist, Boolean ultimoItem, List<Short> seqps, Map<Integer, NumeroApTipoVO> solicNumeroAp) throws BaseException;
*/
	AelResultadoCaracteristica obterAelResultadoCaracteristicaId(final Integer codigo);

	List<AelParametroCamposLaudo> pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(AelVersaoLaudo versaoLaudo);

//	public String obterStyleParametroCampoLaudo(AelParametroCamposLaudo campo, TipoMascaraExameEnum tipoMascaraExame);

	List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(final Object parametro);

	void inserirHorarioExameSemFlush(final AelHorarioExameDisp horarioExameDisp ) throws BaseException;

	List<AelPatologista> buscarPatologistasPorAnatomoPatologicos(final Long seq);
	
	List<AelPatologista> listarPatologistaPorSeqNome(final Object valor);
	
	Long listarPatologistaPorSeqNomeCount(final Object valor);
	
	List<RelatorioExamesPendentesVO> obterListaExamesPendentes(Date dataInicial, Date dataFinal,
			String situacao, Integer[] patologistaSeq, DominioSituacaoExamePatologia situacaoExmAnd) throws ApplicationBusinessException;

	ConvenioExamesLaudosVO rnAelpBusConvAtv(final Integer atvSeq);

	void anexarDocumentoLaudo(final AelDocResultadoExame doc, final AghUnidadesFuncionais unidadeExecutora ) throws BaseException;

	void removerDocumentoLaudo(final AelDocResultadoExame doc, final AghUnidadesFuncionais unidadeExecutora ) throws BaseException;

	List<PesquisaResultadoCargaVO> pesquisarLwsComSolicitacaoExames(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException;

	Long pesquisarLwsComSolicitacaoExamesCount(final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException;

	List<AelItemSolicitacaoExames> obterSitCodigoPorNumeroConsultaESituacao(Integer conNumero, String situacao);

	List<AelItemSolicitacaoExames> obterItemSolicitacaoExamesPorNumeroConsultaESituacao(Integer conNumero, String situacaoPendente, String situacaoCancelado);

	List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacao(Integer numeroConsulta, String situacaoPendente, String situacaoCancelado);

	List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoPendente(Integer numeroConsulta, String situacaoPendente);

	List<Integer> obterSeqSolicitacoesExamesPorConsulta(Integer numeroConsulta, String vSituacaoPendente, String vSituacaoCancelado);

	AelSolicitacaoExames obterAelSolicitacaoExamePorChavePrimaria(Integer seq);

	String obterLaudoSexoPaciente(AelSolicitacaoExames solicitacaoExame);

	List<Integer> obterSeqSolicitacoesExamesPorConsultaCertificacaoDigital(Integer numeroConsulta, String vSituacaoPendente);

	List<VAelExameMatAnalise> listarVExamesMaterialAnalise(final Object objPesquisa);

	VAelExameMatAnalise obterVAelExameMaterialAnalise(AelExamesMaterialAnaliseId id);

	Long listarVExamesMaterialAnaliseCount(final Object objPesquisa);

	List<AelItemSolicitacaoExames> listarItemSolicitacaoExameMarcado(Integer conNumero, String situacaoCancelado);

	void persistirProjetoPaciente(AelProjetoPacientes projetoPaciente);

	AelProjetoPesquisas obterProjetoPesquisaSituacaoData(Integer pjqSeq);

	List<AelSolicitacaoExames> buscarSolicitacaoExamesPorAtendimento(AghAtendimentos atendimento);

	Boolean verificarExistenciaRespostasExamesQuestionario(String cExaSigla, Integer cManSeq, Short cCspCnvCodigo, DominioOrigemAtendimento cOrigemAtend);

	boolean verificarRespostaQuestao(Integer iseSoeSeq, Short iseSeqp);

	List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorItemSolicitacaoExameParaCancelamento(Integer conNumero, Date dthrMovimento, Integer soeSeq);

	List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExame(Integer solicitacaoExameSeq);

	List<AelItemSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoCancelado(Integer numeroConsulta, String vSituacaoCancelado);

	List<AelExtratoItemSolicitacao> pesquisarExtratoItemSolicitacaoConclusao(Integer iseSoeSeq, Short iseSeqp);

	List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameParaConclusao(Integer conNumero, Integer solicitacaoExameSeq, Boolean usaDistinct);

	AelItemSolicitacaoExames obteritemSolicitacaoExamesPorChavePrimaria(AelItemSolicitacaoExamesId id);

    AelItemSolicitacaoExames obterItemSolicitacaoExamePorChavePrimaria(AelItemSolicitacaoExamesId id);

	List<AelPedidoExame> pesquisarAelPedidoExamePorZonaESala(Short unfSeq, Byte sala);

	List<AelGrupoXMaterialAnalise> pesquisarGrupoXMateriaisAnalisesPorGrupo(Integer gmaSeq);

	void persistirAelItemSolicConsultado(AelItemSolicConsultado aelItemSolicConsultado);

	List<ExameAmostraColetadaVO> obterDadosPorAmostrasColetadas(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec);

	public List<ExameAmostraColetadaVO> obterDadosPorAmostrasColetadasHist(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec);

	Date obterDataExame(Integer soeSeq, Short seqp, String sitCodigoLiberado, String sitCodigoAreaExec);

	List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePol(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec);

	AelProjetoPesquisas buscarProjetoPesquisas(Integer dcgCrgSeq);

	AelUnfExecutaExames obterAelUnfExecutaExames(String emaExaSigla, Integer emaManSeq, Short unfSeq);

	List<VAelUnfExecutaExames> pesquisarPorSiglaMaterialOuExame(String filtro);

	Long pesquisarPorSiglaMaterialOuExameCount(String filtro);

	List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(Short unfSeq, String sigla, String descricaoMaterial, String descricaoUsualExame, String indSituacaoUfe,
			Integer firstResult, Integer maxResults, String orderProperty, Boolean asc);

	Long pesquisarExamePorSeqUnidadeExecutoraCount(Short unfSeq, String sigla, String descricaoMaterial, String descricaoUsualExame, String indSituacaoUfe);

	List<AelItemSolicitacaoExames> listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(Integer seqCirurgia, String[] codigos);

	List<AelItemSolicitacaoExames> buscarItemSolicitacaoExamesPorCirurgia(Integer seqMbcCirurgia, String pSituacaoLiberado, String pSituacaoExecutado);

	List<AelItemSolicitacaoExames> pesquisarSolicitacaoExames(Integer crgSeq, String[] situacao);

	List<VAelExamesLiberados>  buscaExamesLiberadosPeloPaciente(Integer codigoPaciente, Short unfSeq);
	
	List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimento(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises);

	List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises);

	List<ProcedimentosPOLVO> pesquisarExamesProcPOL(Integer codigo);

	List<AelProjetoPesquisas> pesquisarProjetosPesquisa(String strPesquisa);

	/**
	 * Obter laudos para popular suggestionBox #6389
	 * 
	 * @author lucasbuzzo
	 * @param Object
	 *            param
	 * @return List<AelCampoLaudo>
	 */
	List<AelCampoLaudo> obterLaudo(Object param);

	Long pesquisarLaudoCount(Object param);

	List<AelPedidoExame> buscarPedidosExamePeloAtendimento(Integer seqAtendimento);

	void removerAelPedidoExame(AelPedidoExame aelPedidoExame, boolean flush);

	void removerAelItemPedidoExame(AelItemPedidoExame aelItemPedidoExame, boolean flush);

	AelItemSolicitacaoExames obterItemSolicitacaoExameOriginal(AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId);

	List<AtendPacExternPorColetasRealizadasVO> listarAtendPacExternPorColetasRealizadas(Date vDtHrInicio, Date vDtHrFim, Integer codSangue, Integer codHemocentro,
			String situacaoCancelado);

	AtendimentoCargaColetaVO listarAtendimentoParaCargaColetaRD(Integer seq, Boolean false1);

	AelExtratoItemSolicitacao obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(Integer soeSeq, Short seqp, String sitCodigo);

	List<AelItemSolicitacaoExames> obterPorSolicitacaoSitCodigo(Integer soeSeq, String[] sitCodigo);

	List<AelAmostraItemExamesVO> listarAmostraItemExamesTodos(AghUnidadesFuncionais unidadeExecutora, Integer solicitacao, Integer amostra, AelEquipamentos equipamento,
			String sigla, DominioSimNao enviado);

	List<AelAmostraItemExamesVO> pesquisarAmostraItemExames(AghUnidadesFuncionais unidadeExecutora, Integer solicitacao, Integer amostra, AelEquipamentos equipamento,
			String sigla, DominioSimNao enviado, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	AelSolicitacaoExames obterSolicitacaoExamePorAtendimento(Integer seqAtendimento);

	List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda);
	
	List<String> obterDescricaoMaterialAnalise(final Integer atdSeq);

	List<AelProjetoPacientes> buscarProjetosPesquisaComPaciente(Integer codigoPaciente, Integer servidorMatricula, Short servidorVinCodigo, String funcao);

	List<AelProjetoPacientes> pesquisarProjetoEquipesComPaciente(Integer pacCodigo, Integer matricula, Short vinCodigo, String funcao);

	List<AelProjetoPesquisas> pesquisarProjetosPacientes(Integer pacCodigo, Integer matricula, short vinCodigo, Date dataInicio, Date dataFim);

	AelAmostrasVO obterAelAmostraVO(Integer soeSeq, Short seqp);

	List<AelItemSolicitacaoExamesVO> listarItemSolicitacaoExamesVO(Integer soeSeq, Short seqp);

	void ajustarNumeroUnicoAelAmostra(AelAmostras aelAmostraOrigem, AelAmostras aelAmostraDestino ) throws BaseException;

	List<AelControleNumeroMapa> obterPorDataNumeroUnicoEOrigem(final AelConfigMapa aelConfigMapa, final Date dtEmissao);

	AelConfigMapa obterAelConfigMapa(final Short seq);
	
	AelConfigMapa obterAelConfigMapa(final Short seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	List<AelConfigMapa> pesquisarAelConfigMapa(final AelConfigMapa filtros);

	List<RelatorioMapaBioquimicaVO> obterMapasBioquimicaVo(final AelConfigMapa aelConfigMapa, final DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa,
			String nomeMicrocomputador ) throws BaseException;

	List<RelatorioMapaHemoculturaVO> obterMapasHemoculturaVO(final AelConfigMapa aelConfigMapa, final DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa,
			String nomeMicrocomputador ) throws BaseException;

	List<RelatorioMapaUroculturaVO> obterMapasUroculturaVO(final AelConfigMapa aelConfigMapa, final DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa,
			String nomeMicrocomputador ) throws BaseException;

	void persistirAelConfigMapa(AelConfigMapa aelConfigMapa ) throws BaseException;

	void removerAelConfigMapa(Short aelConfigMapa ) throws BaseException;

	Long pesquisarAelConfigMapaExamesPorAelConfigMapaCount(final AelConfigMapa configMapa, final String sigla, final String exame, final String material,
			final DominioSituacao situacao);

	List<AelConfigMapaExames> pesquisarAelConfigMapaExamesPorAelConfigMapa(final AelConfigMapa configMapa, final String sigla, final String exame, final String material,
			final DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	List<AelConfigMapa> pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa(final AghUnidadesFuncionais unidadeFuncional, final String mapa,
			final DominoOrigemMapaAmostraItemExame origem, final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc);

	Long pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount(final AghUnidadesFuncionais unidadeFuncional, final String mapa, final DominoOrigemMapaAmostraItemExame origem);

	AelConfigMapaExames obterAelConfigMapaExamesPorId(final AelConfigMapaExamesId id);

	void persistirAelConfigMapaExames(AelConfigMapaExames aelConfigMapaExames ) throws BaseException;

	AelDocResultadoExamesHist obterDocumentoAnexadoHist(Integer iseSoeSeq, Short iseSeqp);

	List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiversoHist(Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame, List<Integer> gruposMateriaisAnalises);

	List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoHist(Integer codigoPaciente, Short unfSeq, DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises);

	void removerAelConfigMapaExames(AelConfigMapaExamesId aelConfigMapaExames ) throws BaseException;

	List<AelNotaAdicional> obterListaNotasAdicionaisPeloItemSolicitacaoExame(Integer iseSoeSeq, Short iseSeqp);

	List<RelatorioMapaEpfVO> obterMapasEpfVO(final AelConfigMapa aelConfigMapa, final DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa,
			String nomeMicrocomputador ) throws BaseException;

	List<RelatorioMapaHematologiaVO> obterMapasHematologiaVO(AelConfigMapa aelConfigMapa, DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa,
			String nomeMicrocomputador ) throws BaseException;

	List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePolHist(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec);

	String geraCSVRelatorioMapaLaminasVO(final Date dtReferencia, final AelCestoPatologia cesto) throws IOException, ApplicationBusinessException;

	AelVersaoLaudo criarNovaVersaoLaudo(AelVersaoLaudo versaoLaudo ) throws BaseException;

	void persistirLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException;

	void inserirLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException;

	void atualizarLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException;

	Short obterIdModuloLisHis() throws ApplicationBusinessException;

	Short obterIdModuloGestaoAmostra() throws ApplicationBusinessException;

	public Long pesquisaGrupoExamesUsuaisCount(Integer seq, String descricao, DominioSituacao situacao);

	public List<AelGrupoExameUsual> pesquisaGrupoExamesUsuais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq, String descricao,
			DominioSituacao situacao);

	public AelGrupoExameUsual obterAelGrupoExameUsualPorId(Integer seq);

	public void inserirAelGrupoExameUsual(AelGrupoExameUsual grupoExameUsualNew) throws ApplicationBusinessException;

	public void alterarAelGrupoExameUsual(AelGrupoExameUsual grupoExameUsualNew) throws ApplicationBusinessException;

	public void excluirAelGrupoExameUsual(Integer seq) throws ApplicationBusinessException;

	public List<LiberacaoLimitacaoExameVO> pesquisarLiberacaoLimitacaoExames(Integer atdSeq);

	public AelExamesLimitadoAtend obterAelExamesLimitadoAtendPorId(AelExamesLimitadoAtendId id);

	public void removerExameLimitadoAtend(AelExamesLimitadoAtend examesLimitadoAtend)throws ApplicationBusinessException;

	public void persistirExameLimitadoAtend(AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException;

	public void atualizarExameLimitadoAtend(AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException;

	public AghAtendimentos obterAtendimentoPorLeito(String param) throws ApplicationBusinessException;

	public AghAtendimentos obterAtendimentoAtualPorProntuario(Integer prontuario) throws ApplicationBusinessException;

	public List<AelItemSolicitacaoExamesVO> listarMascarasAtivasPorExame(String emaExaSigla, Integer emaManSeq, Integer soeSeq, Short seqp);

	AelGrpTecnicaUnfExamesVO obterAelGrpTecnicaUnfExamesVO(Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq);

	List<AelGrupoTecnicaCampo> pesquisarCampoLaudoPendencia(Integer grtSeq, String ufeEmaExaSigla, Short ufeUnfSeq);

	public void gravarResultadoPadraoCampoLaudo(AelVersaoLaudo versaoLaudo, AelResultadosPadrao resultadosPadrao,
			Map<AelParametroCamposLaudo, Object> mapaParametroCamposLaudoTelaValores ) throws BaseException;

	List<AelCampoLaudo> pesquisarAelCampoLaudoSuggestion(final Object pesquisa);

	boolean anexarDocumentoLaudoAutomatico(AelItemSolicitacaoExames itemSolicitacaoExame ) throws BaseException;

	String extrairNomeExtensaoDocumentoLaudoAnexo(AelItemSolicitacaoExames itemSolicitacaoExame);

	Integer obterTamanhoMaximoBytesUploadLaudo();

	List<AelMotivoCancelaExamesVO> listarMotivoCancelamentoExamesAtivos(final AelMotivoCancelaExames motivoCancelaExamesFiltro);

	List<AelOrdExameMatAnalise> pesquisarAelOrdExameMatParaPOL(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, AelExames exame,
			AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2);

	Long pesquisarAelOrdExameMatParaPOLCount(AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2);

	AelOrdExameMatAnalise recuperaAelOrdExameMatAnalisePorMaterial(AelExamesMaterialAnaliseId aelExamesMaterialAnaliseId);

	void atualizarAelOrdExameMatAnalise(AelOrdExameMatAnalise aelOrdExameMatAnalise ) throws BaseException;

	public Integer laudoIdadePaciente(Integer soeSeq);

	Long listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigoCount(DominioSituacaoCartaColeta situacao, Date dtInicio, Date dtFim, Integer iseSoeSeq, Integer pacCodigo);

	List<AelItemSolicCartas> listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			DominioSituacaoCartaColeta situacao, Date dtInicio, Date dtFim, Integer iseSoeSeq, Integer pacCodigo);

	AelItemSolicCartas obterAelItemSolicCartas(AelItemSolicCartasId id);

	List<AelModeloCartas> listarAelModeloCartasAtivas(Object parametro);

	List<AbsMotivoRetornoCartas> listarAelRetornoCartaAtivas(Object parametro);

	void atualizarAelItemSolicCartas(AelItemSolicCartas item, String nomeMicrocomputador ) throws BaseException;

	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(String parametro);

	public boolean possuiNotasAdicionaisItemSolicitacaoExameHist(Integer soeSeq, Short seqp);

	public List<AelNotasAdicionaisHist> pesquisarNotasAdicionaisItemSolicitacaoExameHist(Integer soeSeq, Short seqp);

	List<RelatorioAgendamentoProfissionalVO> pesquisarRelatorioAgendamentoProfissional(Date dataReferenciaIni, Date dataReferenciaFim) throws BaseException;

	List<MonitorPendenciasExamesVO> pesquisarMonitorPendenciasExames(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws BaseException;

	void verificarFiltrosPesquisa(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws BaseException;

	public VAelUnfExecutaExames obterVAelUnfExecutaExames(String emaExaSigla, Integer emaManSeq, Short unfSeq);

	List<CartaRecoletaVO> obterCartaParaImpressao(Short iseSeqp, Integer iseSoeSeq, Short seqp);

	public RelatorioExamesPacienteVO montarRelatorio(Integer prontuario, Integer atdSeq, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido,
			String pertenceSumarioRodape, Integer pacCodigo) throws BaseException;

	void validarPesquisaCartasRecoleta(DominioSituacaoCartaColeta situacao, Date dtInicio, Date dtFim, Integer iseSoeSeq, Integer pacCodigo) throws BaseException;

	public List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(final Object parametro, final Short unfSeq);

	void remover(AelQuestionariosConvUnidId elemento) throws ApplicationBusinessException;

	List<VAelUnfExecutaExames> pesquisarPorSiglaOuMaterialOuExameOuUnidade(Object parametro);

	Long pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount(Object parametro);

	List<AelExigenciaExame> pesquisarExigenciaExame(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelUnfExecutaExames unfExecutaExames,
			AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao);

	Long pesquisarExigenciaExameCount(AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao);

	AelExigenciaExame obterAelExigenciaExame(Integer seq);
	
	AelExigenciaExame obterAelExigenciaExame(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	
	AelSitItemSolicitacoes obterSituacaoItemSolicitacaoParaSituacaoExame(final String codigo);

	void persistirAelExigenciaExame(AelExigenciaExame exigencia ) throws BaseException;

	public AelTipoAmostraExame obterAelTipoAmostraExame(String emaExaSigla, Integer emaManSeq, Integer manSeq, DominioOrigemAtendimento origemAtendimento);

	AelTipoAmostraExame obterAelTipoAmostraExame(AelTipoAmostraExameId id);
	
	public List<AelExtratoItemSolicitacaoVO> listarExtatisticaPorResultadoExame(Date dtHrInicial, Date dtHoraFinal, Short unfSeq) throws ApplicationBusinessException;
	
	String obterNomeArquivoSecretariaNotificacao(Boolean isCD4, Date dataIni);

	String gerarArquivoSecretaria(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException;

	String gerarArquivoSecretariaCarga(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException;

	public void efetuarDownloadArquivoSecretaria(String fileName, Date dataInicio, Boolean infantil) throws BaseException;

	Long buscarAelExamesQuestionarioCount(String emaExaSigla, Integer emaManSeq);

	List<AelQuestionarios> pesquisarAelQuestionarios(Object param);

	List<RelatorioEstatisticaTipoTransporteVO> pesquisarRelatorioEstatisticaTipoTransporte(AghUnidadesFuncionais unidadeExecutora, DominioOrigemAtendimento origem,
			Date dataInicial, Date dataFinal) throws BaseException;

	Map<Integer, Integer> obterTotaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista);

	Map<Integer, BigDecimal> obterPercentuaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista);

	Integer obterTotalGeralTurnos(List<RelatorioEstatisticaTipoTransporteVO> lista);

	public String obterNomeAtendDiv(Integer atdDivseq);

	List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(Short unfSeq, String sigla, String descricaoMaterial, String descricaoUsualExame, String indSituacaoUfe);

	void setTimeout(final Integer timeout) throws ApplicationBusinessException;

	void commit(Integer timeout) throws ApplicationBusinessException;

	List<AelSitItemSolicitacoes> listarSituacoesItensSolicitacaoAtivos();

	public String obterOrigemIg(AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException;

	public List<AelAmostras> listarAmostrasPorAgendamento(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda);

	List<AelDocResultadoExamesHist> obterDocumentoAnexadoHistPorItemSolicitacaoExameHist(Integer iseSoeSeq, Short iseSeqp);

	List<AelRespostaQuestao> pesquisarRespostaPorConsultaPendente(Integer conNumero, String vSituacaoPendente);

	void validarNumeroFrascoSolicitacao(final AghUnidadesFuncionais unidadeExecutora, List<AelAmostrasVO> listaAmostras) throws BaseException;

	void atualizarIndImpressaoQuestionario(AelRespostaQuestao respostaQuestao);

	AelProjetoProcedimento obterProjetoProcedimentoPorChavePrimaria(Integer pjqSeq, Integer pciSeq);

	AelProjetoIntercProc obterProjetoIntercProcProjetoPacienteQuantidadeEfetivado(Integer pjqSeq, Integer pacCodigo, Integer pciSeq);

	void persistirProjetoIntercProc(AelProjetoIntercProc projetoIntercProc ) throws BaseException;
	
	AelProjetoProcedimento obterProjetoProcedimentoAtivoPorId(Integer pjqSeq, Integer pciSeq); 

	List<RelatorioMateriaisRecebidosNoDiaVO> pesquisarMateriaisRecebidosNoDia(
			Short unfSeq, Date dtInicial, Date dtFinal) throws ApplicationBusinessException;

	RelatorioExamesPacienteVO montarRelatorioPlanoContingenciaSumarioExames(
			Integer asuApaAtdSeq, Integer asuApaSeq, Short apeSeqp)
			throws BaseException;

	List<LinhaReportVO> pesquisarNotaAdicionalPorSolicitacaoEItemVO(
			Integer solicitacao, Short seqp, Boolean isHist);
	
	void persistirProjetoProcedimento(AelProjetoProcedimento projetoProcedimento ) throws BaseException;

	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorSituacaoAmostras(Object objPesquisa, List<DominioSituacaoAmostra> situacaoAmostras);

	public AelProjetoPacientes obterProjetoPacienteCadastradoDataProjetoPesquisa(Integer pacCodigo, Integer pjqSeq);
	
	public List<AelExameInternetStatus> listarExameInternetStatus(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataHoraInicial, Date dataHoraFinal,
			DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla,
			String nroRegConselho, Long cnpjContratante) throws ParseException;

	public Long listarExameInternetStatusCount(Date dataHoraInicial,
			Date dataHoraFinal, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla,
			String nroRegConselho, Long cnpjContratante) throws ParseException;

	String buscarLaudoNomeMaeRecemNascidoHist(
			AelSolicitacaoExamesHist solicitacaoExames);
	/**
	 * Busca o maior valor do AelConfigExLaudoUnico presente no BD
	 * 
	 * @param lu2Seq <Integer>
	 * @return Integer
	 */
	Integer buscarMaxVersaoConfPorLu2Seq(Integer lu2Seq);

	void verificaStatusExame(AelAmostras amostra)
			throws ApplicationBusinessException;
	
	/**
	 * Busca uma lista de AelSecaoConfExames acordando com o LU2_SEQ informado e versão
	 * 
	 * @param lu2Seq <Integer>
	 * @param versaoConf <Integer>
	 * @return List<AelSecaoConfExames>
	 */
	List<AelSecaoConfExames> buscarPorLu2SeqEVersaoConf(Integer lu2Seq,
			Integer versaoConf);
	/**
	 * Busca ConfigExameLaudoUnico acordando com o LU2_SEQ
	 * 
	 * @param lu2Seq <Integer>
	 * @return AelConfigExLaudoUnico
	 */
	AelConfigExLaudoUnico bucarConfigExameLaudoUnicoOriginal(Integer lu2Seq);

	/**
	 * Salva a lista de AelSecaoConfExameVO.
	 * 
	 * @param lista List<SecaoConfExameVO>
	 * @param lu2Seq <Integer>
	 * @param servidor <RapServidores>
	 * @throws ApplicationBusinessException
	 */
	void salvarSecaoConfExames(List<SecaoConfExameVO> lista, Integer lu2Seq, RapServidores servidor)  throws ApplicationBusinessException ;
	
	/**
	 * Método utilizado para habilitar e desabilitar a coluna seção obrigatória da Configuração dos Exames.
	 * Utilizado para liberar o Laudo ou para uma etapa intermediária.
	 * @param ativo
	 * @param obrigatorio
	 * @return
	 */
	boolean habilitaSecaoObrigatoria(boolean ativo, boolean obrigatorio);

	String nameHeaderEfetuarDownloadArquivoSecretaria(Date dataInicio, Boolean infantil);
	
	//QUALIDADE
	List<AelAmostraRecebidaVO> gravarAmostras(final List<AmostraVO> listaAmostras, final Integer solicitacaoNumero, final RapServidores servidorLogado,
			final AghUnidadesFuncionais unidadeExecutora, final List<ExameAndamentoVO> listaExamesAndamento,
			final String nomeMicrocomputador, final Map<AghuParametrosEnum, String> situacao) throws ApplicationBusinessException, BaseException;
	
	List<ExameAndamentoVO> obterExamesAndamento(Integer pacCodigo, Short unidadeSeq) throws BaseException;
	
	//#22049
	List<AelPatologista> pesquisarPatologistasResponsaveis(Object param);
	
	Long pesquisarPatologistasResponsaveisCount(Object param);
	
	AelConfigExLaudoUnico obterTipoExameAmostra(Integer solicitacaoNumero, Short seqp);
	
	List<AmostraVO> obterAmostrasSolicitacao(Integer solicitacaoNumero, List<Integer> numerosAmostras, final Map<AghuParametrosEnum, String> situacoes);
	
	void adicionarPatologistaResponsavel(List<AelPatologista> listaPatologistasResponsaveis, AelPatologista novoPatologistaResponsavel)
			throws ApplicationBusinessException;
	
	void confirmarPatologistasResponsaveis(List<AmostraVO> listaAmostras, List<AelPatologista> listaPatologistasResponsaveis);
	
	void agruparAmostras(List<AmostraVO> listaAmostraSelecionadas, Set<ExameAndamentoVO> listaExamesSelecionados, List<AmostraVO> listaAmostras,
			List<AelPatologista> listaPatologistasResponsaveis) throws ApplicationBusinessException;
	
	void desagruparAmostras(List<AmostraVO> listaAmostraSelecionadas, List<AmostraVO> listaAmostras, AmostraVO amostraVO);

	List<Integer> getSeqpAmostrasRecebidas(List<AelAmostrasVO> listaAmostrasRecebidas);
	
	void selecionarExameAndamento(ExameAndamentoVO exameAndamento, List<ExameAndamentoVO> listaExamesAndamento, Set<ExameAndamentoVO> listaExamesSelecionados);
	
	boolean selecionarAmostraExibirPatologistaResponsavel(AmostraVO amostraVO, List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas);
	
	void desfazerSelecaoTodasAmostras(List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas);
	
	List<AelTmpIntervaloColeta> pesquisarTempoPorIntervaloColeta(Short codigoIntervaloColeta);

	void selecionarTodasAmostras(boolean allChecked, List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas);
	
	AelSolicitacaoExames obterAelSolicitacaoExamePorChavePrimaria(Integer seq,
			Enum fetchArgs);

	AelSolicitacaoExamesHist obterAelSolicitacaoExameHistPorChavePrimaria(Integer seq,
			Enum fetchArgs);	

	AelItemSolicitacaoExames obteritemSolicitacaoExamesPorChavePrimaria(
			AelItemSolicitacaoExamesId id, Enum [] innerFields, Enum [] leftFields);
	
	AelItemSolicExameHist obteritemSolicitacaoExamesHistPorChavePrimaria(
			AelItemSolicExameHistId id, Enum [] innerFields, Enum [] leftFields);

	void excluirPatologista(List<AmostraVO> listaAmostras, AelPatologista patologistaResponsavelSelecionado);

	AelCampoLaudo obterCampoLaudoPorId(Integer seq, boolean left,
			Enum... fechArgs);


	AelAmostras buscarAmostrasComRecepientePorId(Integer soeSeq, Short seqp);
	
	/**
	 * #34384 - Obter solicitação de exames	
	 * @param atdSeq
	 * @return
	 */
	List<AelSolicitacaoExames> obterSolicitacaoExamesPorAtendimento(
			Integer atdSeq);


	List<AelExtratoItemCartas> buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(
			AelItemSolicCartas aelItemSolicCartas);


	AelItemSolicCartas obterAelItemSolicCartasComPaciente(
			AelItemSolicCartasId id);


	List<AelCampoLaudo> pesquisarAelCampoLaudoSB(
			DominioTipoCampoCampoLaudo tipo, Object pesquisa);


	Long pesquisarAelCampoLaudoSBCount(
			DominioTipoCampoCampoLaudo tipo, Object pesquisa);	

	/**
	 * Listar os itens de solicitação de exame do paciente.
	 * 
	 * Web Service #37238
	 * 
	 * @param siglaExame
	 * @param seqMaterial
	 * @param codPaciente
	 * @return
	 */
	List<AelItemSolicitacaoExamesId> listarAelItemSolicitacaoExamesPorSiglaMaterialPaciente(String siglaExame, Integer seqMaterial, Integer codPaciente);

	/**
	 * Consulta C29 do Web Service #39353
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	List<AelResultadoExame> pesquisarAelResultadoExamePorAelItemSolicitacaoExames(final Integer soeSeq, final Short seqp);

	/**
	 * Consulta C30 do Web Service #39353
	 * 
	 * @param gtcSeq
	 * @param seqp
	 * @return
	 */
	String obterAelResultadoCodificadoDescricao(final Integer gtcSeq, final Integer seqp);

	/**
	 * Consulta C31 de Web Service #39353
	 * 
	 * @param seq
	 * @return
	 */
	String obterAelResultadoCaracteristicaDescricao(final Integer seq);

	
	/**
	 * Buscar os dados de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	List<AelUnidExameSignificativo> pesquisarUnidadesFuncionaisExamesSignificativosPerinato(final Short unfSeq, final String siglaExame,
			final Integer seqMatAnls, final Boolean indCargaExame, final int firstResult, final int maxResults);

	/**
	 * Count de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @return
	 */
	Long pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(final Short unfSeq, final String siglaExame, final Integer seqMatAnls, final Boolean indCargaExame);

	/**
	 * Buscar os dados de exames e material de analise ativos por descrição ou sigla
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	List<AelExamesMaterialAnalise> pesquisarAtivosPorSiglaOuDescricao(String parametro, Integer maxResults);

	/**
	 * Count dos dados de exames e material de analise ativos por descrição ou sigla
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 */
	Long pesquisarAtivosPorSiglaOuDescricaoCount(String parametro);
	
	/**
	 * Persistir dados de exames significativos
	 * 
	 * Web Service #36157
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 * @param data
	 * @param matricula
	 * @param vinCodigo
	 * @param indPreNatal
	 */
	void persistirAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Date data, Integer matricula, Short vinCodigo, Boolean indPreNatal, Boolean indCargaExame);
	
	/**
	 * Excluir dados de exames significativos
	 * 
	 * Web Service #36158
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 */
	void removerAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq);
	
	/**
	 * Obter dados de exames significativos para uma unidade
	 * 
	 * @param unfSeq
	 * @param indCargaExame
	 * @return
	 */
	List<AelUnidExameSignificativo> pesquisarAelUnidExameSignificativoPorUnfSeq(final Short unfSeq, final Boolean indCargaExame);
	
	/**
	 * Buscar os dados de exames e material de analise por sigla e sequencial de material de análise
	 * 
	 * Web Service #37700
	 * 
	 * @param sigla
	 * @param seqMatAnls
	 * @return
	 */
	List<AelExamesMaterialAnalise> pesquisarExamesPorSiglaMaterialAnalise(String sigla, Integer seqMatAnls);

	/**
	 * Verifica a existência de Solicitação de Exame para a Consulta informada.
	 * 
	 * @param numero - Número da Consulta
	 * @return Flag indicando a existência do registro
	 */
	public boolean verificarExistenciaSolicitacaoExamePorNumConsulta(Integer numero);
	
	void confirmarPatologistasResponsaveis(List<AmostraVO> listaAmostraSelecionadas, AelPatologista patologistaResponsavel);
	
	public Boolean habilitaBotaoTecnica(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain);
	
	public void buscaSecoesConfiguracaoObrigatorias(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException;

	 List<AelGrupoRecomendacaoExame> obterAelGrupoRecomendacaoExamesPorAelGrupoRecomendacao(AelGrupoRecomendacao grupo);
	
	Boolean verificaPacienteEmProjetoPesquisa(Integer pacCodigo);

	Date obterDataPrimeiraSolicitacaoExamePeloNumConsulta(Integer conNumero);
	
	//#42108 - #25685 - C2
	List<AelRegiaoAnatomica> pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(String param);
	Long pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(String param);
	//#42109 - #25685 - C3
	Boolean verificarRegioesPorSeqAchadoDescricao(List<Integer> seqs, String descricao);
	//#42109 - #25685 - C4
	List<AelRegiaoAnatomica> buscarRegioesAnatomicas(String descricao);
	

	/**
	 * #41772
	 * @author marcelo.deus
	 * @param obterSeq
	 * @return
	 */
	AelMateriaisAnalises buscarMaterialAnalisePorSeq(Integer obterSeq);

	ResultadoExamePim2VO obterExamePim2(Integer atdSeq, String[] listaCampos);

	List<ResultadoExamesVO> obterResultadoExamesSaps3(Integer atdSeq,
			String param1, Integer param2, Integer param3);
	
	String buscarLocalizadorExamesInternet(Integer atendimentoSeq);

	Boolean gerarPCT(Integer atdSeq, Boolean urgente,
			DominioSituacaoColeta situacaoColeta, Integer shaSeq,
			String nomeMicrocomputador, RapServidores servidorLogado,
			RapServidores responsavel) throws BaseException;

	String obterConvenioAtendimento(Integer atdSeq);
	Long obterLaboratorioExternoListCount(String parametro);
	
	/**
	 * Retorna dados para geração de laudos de exames. Os itens de exames
	 * fornecidos devem ser sempre do mesmo paciente.
	 * 
	 * @param itemIds
	 *            lista dos id de items de exame
	 * @throws MECBaseException 
	 * @throws AGHUNegocioException 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	ExamesListaVO buscarDadosLaudo(
			List<IAelItemSolicitacaoExamesId> itemIds) throws ApplicationBusinessException, BaseException;
	
	/**
	 * Executa laudo para os exames com os ids fornecidos.
	 * 
	 * @param itemIds
	 * @param tipoLaudo
	 *            se null executa laudo de visualização
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 * @throws AGHUNegocioException
	 * @throws MECBaseException
	 */
	public ResultadoLaudoVO executaLaudo(
			List<IAelItemSolicitacaoExamesId> itemIds,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException;

	/**
	 * Executa laudo com os dados fornecidos.
	 * 
	 * @param dadosLaudo
	 * @param tipoLaudo
	 *            se null executa laudo de visualização
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 * @throws AGHUNegocioException
	 * @throws MECBaseException
	 */
	public ResultadoLaudoVO executaLaudo(ExamesListaVO dadosLaudo,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException;
	
	public ExamesListaVO buscarDadosLaudoPrevia(
			List<AelParametroCamposLaudo> parametrosPrevia,
			AelVersaoLaudo versaoLaudo) throws BaseException;
		
	public String getEnderecoPaciente(Integer pacCodigo);
	public List<AghResponsavel> listarResponsavel(String parametro);
	
	public List<AghPaisBcb> listarPaisesBcb(String parametro, AghPaisBcb paisBrasil);
	
	public AghPaisBcb obterAghPaisBcb(Integer seq);	
	
	AelExamesMaterialAnalise obterAelExamesMaterialAnalisePorId(
			AelExamesMaterialAnaliseId id, Enum[] fetchArgsInnerJoin,
			Enum[] fetchArgsLeftJoin);

	Long pesquisarAelQuestionariosCount(String param);
	Boolean possuiExameDeImagem(AelItemSolicitacaoExamesId id);	
	//public String obterFatorRhExamesRealizados(Integer pacCodigo);
	public String obterFatorFatorSanguinioExamesRealizados(Integer pacCodigo);
	public String obterFatorRhExamesRealizados(Integer pacCodigo);
	

	ExamesListaVO buscarDadosLaudoFPreview(List<AelParametroCamposLaudo> parametrosPrevia, AelVersaoLaudo versaoLaudo) throws BaseException;
	void persistirResultados(
			Map<AelParametroCamposLaudo, Object> valoresCampos, Integer soeSeq,
			Short seqp, RapServidores servidorResponsavelLiberacao,
			Boolean usuarioLiberaExame, String nomeMicrocomputador,
			AelItemSolicitacaoExames itemOriginal) throws BaseException;
	void anularResultado(Integer soeSeq, Short seqp, Boolean usuarioAnulaExame,
			String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal)
			throws BaseException;
	void liberarExames(AelItemSolicitacaoExames itemSolicitacao,
			String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal)
			throws BaseException;
			
	Boolean hasAelExameApItemSolicPorSolicitacao(final Integer iseSoeSeq);
	
	List<ImprimeEtiquetaVO> gerarEtiquetasAmostrasRecebidasUnf(List<AelAmostrasVO> listaAmostras, AghUnidadesFuncionais unidadeExecutora);
	
	public List<AelExames> obterAelExamesPorSiglaDescricao(String _filtro);
	public Long obterAelExamesPorSiglaDescricaoCount(String _filtro);	
	
	public List<AelExamesXAelParametroCamposLaudoVO> obterAelCampoLaudoPorNome(String nome, String siglaExame);
	public Long obterAelCampoLaudoPorNomeCount(String nome, String siglaExame);
	Short mbcLocalPacMbc(Integer atdSeq) throws ApplicationBusinessException;
	public TicketExamesPacienteVO geraTicketExamesPacienteVO(AelItemSolicitacaoExames itemExame)  throws ApplicationBusinessException ;
	
	public void verificarSetarMaiorTempoJejum(List<TicketExamesPacienteVO> tickets);
	
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidadeAtivos(Object parametro, AghUnidadesFuncionais unidadeExecutora);
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamesComExame(Integer soeSeq, Short seqp);
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamesComAtendimento(Integer soeSeq, Short seqp);
	
	public List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(Integer codSolicitacao, Short unfSeq,
			Set<Short> listaUnfSeq) throws ApplicationBusinessException;
	
	public Boolean pesquisarExamesResultadoNaoVisualizado(Integer atdSeq);
	
	public String buscaAmoSeqParaUmaSolicitacao(Integer iseSoeSeq, Short iseSeqp, 	Integer amoSoeSeq);
	
	public void setCaminhoLogo(String caminho);
	
	public void pesquisarUnidadeFuncionalColeta() throws ApplicationBusinessException;

}
