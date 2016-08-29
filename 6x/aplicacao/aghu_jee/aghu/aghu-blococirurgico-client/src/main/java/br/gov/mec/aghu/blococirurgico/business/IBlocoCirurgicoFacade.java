package br.gov.mec.aghu.blococirurgico.business;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PesquisaAgendarProcedimentosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoTurnosSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RegimeProcedimentoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.TempoSalaAgendaVO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.AvalOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaComPacEmTransOperatorioVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.FichaPreOperatoriaVO;
import br.gov.mec.aghu.blococirurgico.vo.MamLaudoAihVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcEquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcFichaTipoAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProcEspPorCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProfAtuaUnidCirgsVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaConcluidaHojeVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaPreparoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesCirurgiaUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmListaDeProcedimentosCanceladosVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.PendenciaWorkflowVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisarPacientesCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentoCirurgicoPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProtocoloEntregaNotasDeConsumoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaComRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasIndicacaoExamesVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasReservaHemoterapicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioControleChamadaPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHSolicVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioNotasDeConsumoDaSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesComCirurgiaPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEntrevistarVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProfissionaisUnidadeCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioRegistroDaNotaDeSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLotePesquisaVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLoteVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitarReceberOrcMatNaoLicitadoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizaCirurgiaCanceladaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.controleinfeccao.vo.ProcedimentoCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoMbcPosicionamento;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamento;
import br.gov.mec.aghu.dominio.DominioOcorrFichaFluido;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProtocoloEntregaNotasConsumo;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.dominio.DominioStatusRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoEventoMbcPrincipal;
import br.gov.mec.aghu.dominio.DominioTipoFluidoAdministrado;
import br.gov.mec.aghu.dominio.DominioTipoFluidoPerdido;
import br.gov.mec.aghu.dominio.DominioTipoInducaoManutencao;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.dominio.DominioTipoPendenciaCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exames.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.model.MbcDestinoPaciente.Fields;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemMonitorizacaoDefinidoFichaAnestVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


@SuppressWarnings("PMD.ExcessiveClassLength")
public interface IBlocoCirurgicoFacade extends Serializable {

	
	void atualizarCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException;

	/**
	 * Método para buscar cirurgias por ID de atendimento.
	 * 
	 * @param seqAtendimento
	 * @param dataFimCirurgia
	 * @return
	 */
	List<MbcCirurgias> pesquisarCirurgiasPorAtendimento(Integer seqAtendimento, Date dataFimCirurgia);

	
	List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(Object filtro);

	MbcProcedimentoCirurgicos obterMbcProcedimentoCirurgicosPorId(Integer seq);

	/**
	 * Lista Tipos de anestesia que necessitam anestesista
	 * 
	 * @param crgSeq
	 * @param necessitaAnestesista
	 * @return
	 */
	List<MbcTipoAnestesias> listarTipoAnestesias(Integer crgSeq, Boolean necessitaAnestesista);

	/**
	 * verifica se o paciente tem cirurgia sem nota digitada
	 * 
	 * @param pacCodigo
	 * @return
	 */
	Boolean pacienteTemCirurgiaSemNota(Integer pacCodigo, Date dataInternacao, Date dataAlta);

	/**
	 * Implementa o cursor <code>c_get_cbo_cirg</code>
	 * 
	 * @param crgSeq
	 * @param resp
	 * @return
	 */
	RapServidores buscaRapServidorDeMbcProfCirurgias(Integer crgSeq, DominioSimNao resp);

	List<SuggestionListaCirurgiaVO> pesquisarSuggestionEquipe(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel);

	List<String> obterNomeEquipeCirurgica(final Integer grcSeq);

	List<SuggestionListaCirurgiaVO> pesquisarSuggestionProcedimento(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final Short eprEspSeq,
			final DominioSituacao situacao, final boolean indPrincipal);

	List<CirurgiaVO> pesquisarCirurgias(final TelaListaCirurgiaVO tela, String orderProperty, final Integer crgSeq) throws ApplicationBusinessException, ApplicationBusinessException;

	TelaListaCirurgiaVO inicializarTelaListaCirurgiaVO(final String nomeMicrocomputador, boolean carregaUnidadeFuncionalFuncionario) 
			throws ApplicationBusinessException, ApplicationBusinessException;

	//void setTimeout(final Integer timeout) throws ApplicationBusinessException;

	//void commit(Integer timeout) throws ApplicationBusinessException;
	
	/**
	 * Implementa o cursor <code>c_get_cbo_anest</code>
	 * 
	 * @param crgSeq
	 * @param funcoes
	 * @return
	 */
	RapServidores buscaRapServidorDeMbcProfCirurgias(Integer crgSeq, DominioFuncaoProfissional[] funcoes);

	Short buscarRnCthcAtuEncPrv(Integer pacCodigo, Date dthrPci, DominioIndRespProc indRespProc);

	/**
	 * Implementa o cursor <code>c_get_cbo_anest_desc</code>
	 * 
	 * @param crgSeq
	 * @param tipoAtuacao
	 * @return
	 */
	RapServidores buscaServidorProfPorCrgSeqETipoAtuacao(Integer crgSeq, DominioTipoAtuacao tipoAtuacao);

	MbcCirurgias obterCirurgiaPorChavePrimaria(Integer crgSeq);

	List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicos(Object objPesquisa);

	Long listarMbcProcedimentoCirurgicosCount(Object objPesquisa);
	
	void validarContaminacaoProcedimentoCirurgico(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException;

	List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listarCirurgiasAgendadasCadastroSugestaoDesdobramento(Date dthrInicioCirurgiaIni, Date dthrInicioCirurgiaFim,
			ConstanteAghCaractUnidFuncionais[] caracteristicas, DominioGrupoConvenio grupoConvenio, DominioTipoPlano indTipoPlano, DominioSituacaoCirurgia situacaoCirurgia);

	
	List<MbcCirurgias> listarCirurgiasPorHistoricoPacienteVO(HistoricoPacienteVO historicoVO);

	
	MbcCirurgias obterProcedimentoCirurgicoInternacaoUltimaSemana(Integer pacCodigo, Date dtInternacao, DominioSituacaoCirurgia situacaoCirurgia);

	MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventosComFicha(Long seqFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrencia, Short seqMbcEventoPrincipal);

	MbcFichaFarmaco obterMbcFichaFarmaco(Integer seqMbcFichaFarmaco);

	List<MbcFichaTecnicaEspecial> pesquisarMbcFichasTecnicasEspeciaisComTecnicaEspecial(Long seqMbcFichaAnestesia);

	List<ItemMonitorizacaoDefinidoFichaAnestVO> pesquisarItensMonitorizacaoDefinidosFicha(Long seqMbcFichaAnest);

	List<MbcFichaFluidoPerdido> pesquisarFichasFluidosPerdidosByFichaAnestesia(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido);

	Long obterSomaVolumeTotalFluidoPerdido(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido);

	MbcFichaOrgaoTransplante obterFichasOrgaosTransplantes(Long seqMbcFichaAnestia, Short seqMbcOrgaoTransplantado);

	MbcOcorrenciaFichaEvento obterOcorrenciaFichaEvento(Long seqMbcFichaAnest, Short seqMbcOrgaoTransplantado, DominioOcorrenciaFichaEvento tipoOcorrencia);

	List<MbcMonitorizacao> pesquisarMbcMonitorizacoesComFichaAnestesia(Long seqMbcFichaAnestesia, Boolean fichaDefMonitorado);

	List<MbcFichaFluidoAdministrados> pesquisarMbcFichaFluidoAdministrado(Long seqMbcFichaAnest, DominioTipoFluidoAdministrado tipoFluidoPerdido);

	MbcOcorrFichaFluidoAdms obterMbcOcorrFichaFluidoAdm(Integer seqMbcFichaFluidoAdm, DominioOcorrFichaFluido ocorrFichaFluido, Boolean permaneceNoPos);

	
	MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventoComEventoPrincipal(Long seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrenciaFichaEvento);

	
	List<MbcFichaGas> listarMbcFichaGasesComMaterial(Long seqMbcFichaAnestesia);

	
	List<MbcOcorrenciaFichaGas> listarMbcOcorrenciaFichaGas(Integer seqMbcFichaGas);

	
	List<MbcOcorrenciaFichaFarmaco> listarMbcFichaFarmacosByMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	
	List<MbcOcorrenciaFichaGas> listarMbcFichaGasByMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	
	void persistirMbcTmpFichaFarmaco(MbcTmpFichaFarmaco mbcTmpFichaFarmaco);

	
	List<MbcFichaFarmaco> listarFichaFarmacoPorFicSeq(Long seqMbcFichaAnestesia);

	
	List<Object[]> pesquisarTipoItemMonitorizacaoComMedicao(Long seqMbcFichaAnestesia);

	
	List<MbcFichaMedMonitorizacao> pesquisarFichaMedMonitorizacaoComMedicao(Long seqMbcFichaAnestesia, Short seqMbcItemMonitorizacoes);

	MbcFichaAnestesias obterMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaFinal> pesquisarMbcFichasFinais(Long seqMbcFichaAnestesia);

	
	List<MbcFichaPaciente> pesquisarMbcFichasPacientes(Long seqMbcFichaAnestesia);

	BigDecimal calcularSuperficieCorporalDoPaciente(Integer codPaciente);

	List<MbcFichaInicial> pesquisarMbcFichasIniciais(Long seqMbcFichaAnestesia);

	List<MbcFichaTipoAnestesia> pesquisarMbcFichasTipoAnestesias(Long seqMbcFichaAnestesia);

	Long getCountMbcFichaNeonatologiaByMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	MbcCirurgias obterCirurgiaPorChavePrimaria(Integer crgSeq, Enum[] inner, Enum[] left);
	
	Long getCountMbcFichaGrandePorteByMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	
	MbcOcorrenciaFichaFarmaco obterMbcOcorrenciaFichaFarmacoBySeq(Integer seqMbcOcorrenciaFichaFarmaco);

	MbcFichaAnestesias obterMbcFichaAnestesiaByConsulta(Integer numeroConsulta);

	List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasComServidorAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaExame> pesquisarMbcFichasExamesComItemSolicitacaoExame(Long seqMbcFichaAnestesia);

	List<MbcFichaInducaoManutencao> pesquisarMbcInducaoManutencaoByFichaAnestesia(Long seqMbcFichaAnestesia, DominioTipoInducaoManutencao tipoInducaoManutencao,
			Boolean fichaInducaoManutSelecionado);

	List<MbcFichaMedMonitorizacao> pesquisarMbcFichaMedMonitorizacaoComMbcTipoItemMonit(Long seqMbcFichaAnestesia);

	List<MbcTmpFichaFarmaco> pesquisarMbcTmpFichaFarmacoByFichaAnestesiaESessao(Integer seqMbcFichaAnestesia, String vSessao);

	List<MbcFichaNeonatologia> pesquisarMbcNeonatologiasByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaAnestesiaRegional> pesquisarMbcFichaAnestesiaRegionalByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean ivRegional, Boolean bloqueioNervoPlexo,
			Boolean intercostais, Boolean neuroeixo);

	List<MbcFichaBloqNervoPlexos> pesquisarMbcFichaBloqNervoPlexosByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcAnestRegionalNeuroeixos> pesquisarMbcAnestRegNeuroeixoByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcNeuroeixoNvlPuncionados> pesquisarMbcNeuroNvlPuncionadosByMbcAnestRegionalNeuroeixos(Integer seqMbcNeuroEixoNvlPuncionados);

	List<MbcFichaMaterial> pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean materialNeuroEixo, Boolean materialViaAerea);

	List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean executorBloqueio);

	Long getCountMbcFichaViaAereaByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaViaAerea> pesquisarMbcFichaViaAereaByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaEspecifIntubacoes> pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(Long seqMbcFichaAnestesia, String tipo);

	List<MbcFichaVentilacao> pesquisarMbcFichaVentilacaoByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaPosicionamento> pesquisarMbcFichaPosicionamentosByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean posicionamentoFicha,
			DominioGrupoMbcPosicionamento grupoMbcPosionamento);

	Long obterSomaVolumeTotalFluidoAdministrado(Long seqMbcFichaAnest, Boolean agrupaTipoFluidoAdm);

	List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEvento(Long seqMbcFichaAnestesia, List<DominioTipoEventoMbcPrincipal> tipoEventosPrincipal,
			List<DominioTipoOcorrenciaFichaFarmaco> tipoOcorrenciasFichaFarmaco);

	Date obterDtOcorrenciaMaxMbcFichaOcorrenciaEvento(Integer seqMbcOcorrenciaFichaEvento, DominioTipoOcorrenciaFichaFarmaco tipoOcorrenciasFichaEvento);

	List<MbcFichaFinal> pesquisarMbcFichasFinais(Long seqMbcFichaAnestesia, Boolean nenhumEventoAdverso);

	List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEventoComMbcEventoAdverso(Long seqMbcFichaAnestesia);

	List<MbcFichaGrandePorte> pesquisarMbcFichaGrandePorteByMbcFichaAnestesia(Long seqMbcFichaAnestesia);

	List<MbcFichaOrgaoTransplante> pesquisarMbcFichaOrgaoTransplanteByFichaAnestesia(Long seqMbcFichaAnestesia);

	List<Object[]> listarAtendimentosPacienteCirurgiaInternacaoPorCodigo(Integer pacCodigo, DominioSituacaoCirurgia situacaoCirurgia, DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean situacaoCirurgiaDiferente);

	List<Object[]> listarAtendimentosPacienteCirurgiaAmbulatorioPorCodigo(Integer pacCodigo);

	List<MbcAgendas> listarAgendarPorCodigoPaciente(Integer pacCodigo);

	MbcAgendas obterAgendaPorChavePrimaria(Integer seqMbcAgenda);

	List<CirurgiasInternacaoPOLVO> pesquisarAgendasProcCirurgicosInternacaoPOL(Integer codigo);

	List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(Integer codigo);

	List<ProcedimentosPOLVO> pesquisarProcedimentosPortalPOL(Integer codigo);

	
	List<EscalaCirurgiasVO> pesquisaEscalaCirurgia(AghUnidadesFuncionais unidadesFuncionais, Date dataCirurgia, DominioStatusRelatorio status)
	throws ApplicationBusinessException;

	MbcAnestesiaDescricoes buscarAnestesiaDescricoes(Integer dcgCrgSeq, Short seqp);

	void inserirAnestesiaDescricoes(final MbcAnestesiaDescricoes anestesiaDescricao) throws ApplicationBusinessException;

	void alterarAnestesiaDescricoes(final MbcAnestesiaDescricoes anestesiaDescricao, MbcTipoAnestesias tipoAnestesia) throws ApplicationBusinessException,
	ApplicationBusinessException;

	/**
	 * Método para buscar cirurgias através do seq de um atendimento
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	List<MbcCirurgias> pesquisarCirurgiaPorAtendimento(Integer seqAtendimento);

	List<Date> buscarDataCirurgias(final Integer codPaciente, final Date dtRealizado, final Date dtRealizadoFimMes, final DominioSituacaoCirurgia situacaoCirurgia,
			final Boolean indDigtNotaSala, final Short cnvCodigo, final DominioIndRespProc dominioIndRespProc, final DominioSituacao situacao, final Integer phiSeq);

	List<MbcCirurgias> listarCirurgias(AipPacientes paciente, AghAtendimentos atendimento);

	List<MbcCirurgias> pesquisarCirurgiasPorPaciente(AipPacientes paciente);

	List<MbcCirurgias> listarCirurgiasPorCodigoPaciente(Integer pacCodigo);

	void persistirCirurgia(MbcCirurgias mbcCirurgias, RapServidores servidorLogado) throws BaseException;

	/**
	 * verifica se escala do portal de agendamento tem cirurgia
	 * 
	 * @author Angela Gallassini
	 * @param agdSeq
	 * @param dtAgenda
	 * @return boolean
	 */
	Long verificarSeEscalaPortalAgendamentoTemCirurgia(Integer agdSeq, Date dtAgenda);

	boolean procedimentoCirurgicoExigeInternacao(AghAtendimentos atendimento);

	/**
	 * Consulta equivalente ao cursor cur_acr da procedure MPMP_LISTA_CIRG_REALIZADAS, contida no arquivo MPMF_SUMARIO_ALTA.pll.
	 * 
	 * @param atdSeq
	 *            Seq do atendimento.
	 * @return Registros encontrados.
	 */
	List<Object[]> pesquisarRealizadasPorProcedimentoEspecial(Integer atdSeq);

	
	List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao);
	Long listarDescricaoCirurgicaPorSeqCirurgiaSituacaoCount(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao);

	
	List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgia(Integer crgSeq);

	/**
	 * Efetua busca de MbcDescricaoCirurgica Consulta C1 #18527
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 */
	MbcDescricaoCirurgica buscarMbcDescricaoCirurgica(Integer crgSeq, Short seqp);

	/**
	 * Efetua busca de MbcDescricaoCirurgica Consulta c12 #18527
	 * 
	 * @param dcgCrgSeq
	 * @return
	 */
	MbcDescricaoCirurgica buscarDescricaoCirurgica(Integer crgSeq, Short seqp);

	/**
	 * Efetua busca de List<MbcDescricaoItens> Consulta C4 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @return
	 */
	MbcDescricaoItens buscarDescricaoItens(Integer dcgCrgSeq, Short dcgSeqp);

	void alterarMbcDescricaoItens(MbcDescricaoItens mbcDescricaoItens) throws ApplicationBusinessException, ApplicationBusinessException;

	/**
	 * Efetua busca de MbcDescricaoTecnicas Consulta C7 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param seqp
	 * @return
	 */
	MbcDescricaoTecnicas buscarDescricaoTecnicas(Integer dcgCrgSeq, Short seqp);

	/**
	 * Efetua busca de List<MbcDiagnosticoDescricao> Consulta C9 #18527
	 */
	List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(Integer dcgCrgSeq, Short dcgSeqp);

	List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(final Integer dcgCrgSeq, final Short dcgSeqp, DominioClassificacaoDiagnostico classificacao);

	List<MbcDiagnosticoDescricao> listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(Integer dcgCrgSeq, DominioClassificacaoDiagnostico classificacao);

	
	MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq);

	List<MbcFichaAnestesias> listarFichasAnestesiasPorCodigoPacienteComGsoPacCodigoNulo(Integer pacCodigo);

	List<MbcFichaAnestesias> listarFichasAnestesias(Integer pacCodigo, Integer gsoPacCodigo, Short gsoSequence);

	
	List<MbcFichaAnestesias> listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(Integer crgSeq, DominioIndPendenteAmbulatorio pendente);

	List<MbcFichaAnestesias> pesquisarMbcFichaAnestesiasAtdGso(Integer atdSeq, Integer gsoPacCodigo, Short gsoSeqp);

	/**
	 * Efetua busca de MbcFiguraDescricoes Consulta C10 #18527
	 * 
	 * @param dcgCrgSeq
	 * @return
	 */
	MbcFiguraDescricoes buscarFiguraDescricoes(Integer dcgCrgSeq, Short seqp);

	Integer obterMaxFDCSeqp(Integer crgSeq, Short seqp);

	/**
	 * Efetua busca de MbcImagemDescricoes Consulta C11 #18527
	 * 
	 * @param fdcDcgCrgSeq
	 * @param fdcDcgSeqp
	 * @param fdcSeqp
	 * @return
	 */
	MbcImagemDescricoes buscarImagemDescricoes(Integer fdcDcgCrgSeq, Short fdcDcgSeqp, Integer fdcSeqp);

	/**
	 * Efetua busca em MbcNotaAdicionais Consulta C13 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @return
	 */
	MbcNotaAdicionais buscarNotaAdicionais(Integer dcgCrgSeq, Short dcgSeqp);

	/**
	 * Efetua busca em MbcNotaAdicionais Consulta C14 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param seqp2
	 * @param dcgSeqp
	 * @param seqp
	 * @param integer
	 * @return
	 */
	MbcNotaAdicionais buscarNotaAdicionais1(Integer dcgCrgSeqC1, Short seqpC1, Integer dcgCrgSeq, Short dcgSeqp, Integer seqp);

	/**
	 * Efetua busca em MBC_NOTA_ADICIONAIS Consulta C15 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @param seqp
	 * @return
	 */
	MbcNotaAdicionais buscarNotaAdicionais2(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp);

	Integer obterMaxNTASeqp(Integer crgSeq, Short seqp);

	Integer obterNTASeqp(Integer crgSeq, Short seqp);

	/**
	 * Efetua busca de MbcProcDescricoes Consulta C5 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param seqp
	 * @return
	 */
	List<MbcProcDescricoes> buscarProcDescricoes(Integer dcgCrgSeq, Integer seqp);

	List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicos(final Object param, final String order, final Integer maxResults, final DominioSituacao situacao);

	Long pesquisarProcedimentosCirurgicosCount(final Object param, final DominioSituacao situacao);

	List<MbcProcedimentoCirurgicos> buscaProcedimentoCirurgicosRealizadoLeito(Object objPesquisa);

	List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicos(final Object objPesquisa);

	List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicos(final Object objPesquisa, final Integer maxResults);

	Long listarProcedimentoCirurgicosCount(final Object objPesquisa);

	List<MbcEquipamentoCirurgico> buscaEquipamentosCirurgicos(Object objPesquisa);

	Long buscaEquipamentosCirurgicosCount(Object objPesquisa);

	MbcProcedimentoCirurgicos obterProcedimentoCirurgico(final Integer seq);

	abstract List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgias(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq,
			final DominioIndRespProc indRespProc);

	abstract Long pesquisarMbcProcEspPorCirurgiasCount(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq, final DominioIndRespProc indRespProc);

	List<MbcProcEspPorCirurgias> retornaProcEspCirurgico(MbcCirurgias cirurgia);

	List<MbcProcEspPorCirurgias> obterProcedimentosEspeciaisPorCirurgia(Integer codigoPaciente);

	List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo);

	String obterPacOruAccNummer(Integer seq);

	List<MbcProcEspPorCirurgias> obterProcedimentosCirurgicos(Integer atdSeq, Integer pacCodigo) throws ApplicationBusinessException;

	MbcProcEspPorCirurgias obterMbcProcEspPorCirurgiasPorChavePrimaria(MbcProcEspPorCirurgiasId id);

	List<MbcProcEspPorCirurgias> pesquisarProcEspSemProcDiagTerapPorDdtSeqEDdtCrgSeq(Integer ddtSeq, Integer ddtCrgSeq);

	List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(Short espSeq, Integer pciSeq);

	List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(Short espSeq, Integer pciSeq);

	void persistirProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException;

	MbcProfCirurgias buscaRapServidorPorCrgSeqEIndResponsavel(Integer crgSeq, DominioSimNao resp);

	MbcProfCirurgias retornaResponsavelCirurgia(MbcCirurgias cirurgia);

	/**
	 * Efetua busca de MbcProfDescricoes Consulta C3 #18527
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeq
	 * @return
	 */
	
	List<MbcProfDescricoes> buscarProfDescricoes(Integer dcgCrgSeq, Short dcgSeq);

	List<MbcFichaProcedimento> obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(Long seqMbcFichaAnestesia, DominioSituacaoExame situacaoProcedimento);

	
	void removerMbcTmpFichaFarmacoAnteriores(Integer qtdeDias, Boolean flush);

	/**
	 * Estoria 9070 Querys (Q_LE e Q_CA) de acordo com a situacao informada
	 * 
	 * @param pacCodigo
	 * @param situacao
	 * @return
	 */
	List<MbcAgendas> pesquisarCirurgiasListaDeEspera(Integer pacCodigo, DominioSituacaoAgendas situacao);

	List<MbcCirurgias> verificarSePacienteTemCirurgia(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo);

	List<MbcAgendas> pesquisarCirurgiaAgendadaPorPaciente(Integer pacCodigo, Integer nroDiasPraFrente, Integer nroDiasPraTras);

	
	MbcProcDescricoes buscarProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp);

	MbcCirurgias obterCirurgiaPorSeq(Integer seq);

	MbcTipoAnestesias obterMbcTipoAnestesiaPorChavePrimaria(Short seq);

	MbcCirurgias obterCirurgiaProjetpPesquisa(Integer seq);

	MbcFichaAnestesias obterMbcFichaAnestesiaByMcoGestacao(McoGestacoes mcoGestacoes, Integer atdSeq, DominioIndPendenteAmbulatorio pendente);

	
	List<MbcCirurgias> pesquisarCirurgiasPacienteDataAtualEDiaSeguinte(AipPacientes paciente);
	
	void atualizarOrigemPacienteEAtendimentoDaCirurgia(MbcCirurgias cirurgia);
	
	List<MbcCirurgias> pesquisarCirurgiasPacienteDataEntreDias(AipPacientes paciente);
 

	List<MbcAgendas> pesquisarCirurgiasAgendadasPorResponsavel(MbcCirurgias cirurgia, MbcProfCirurgias profCirurgia);

	
	List<PlanejamentoCirurgicoVO> pesquisarRelatorioPlanejamentoCirurgico(Date dataCirurgia, Short seqEspecialidade, Integer pacCodigo, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf);

	MbcCirurgias verificarSeAgendamentoTemCirurgiaRealizada(Integer agdSeq);

	MbcDescricaoItens obterMbcDescricaoItensPorId(final MbcDescricaoItensId id);

	MbcDescricaoItens obterMbcDescricaoItensOriginal(final MbcDescricaoItensId mdi);

	void desatacharMbcDescricaoItensOriginal(final MbcDescricaoItens elemento);

	LinhaReportVO obterDataInicioFimCirurgia(Integer crgSeq, Date dthrInicioCirg);

	AghUnidadesFuncionais obterUnidadeFuncionalCirurgia(final String nomeMicrocomputador) throws ApplicationBusinessException;

	List<MbcCidUsualEquipe> pesquisarCidUsualEquipe(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, Short eqpSeq);

	Long pesquisarCidUsualEquipeCount(Short eqpSeq);

	List<MbcEquipamentoCirurgico> pesquisarEquipamentoCirurgico(final Short codigo, final String descricao, final DominioSituacao situacao, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final Boolean asc);

	Long pesquisarEquipamentoCirurgicoCount(final Short codigo, final String descricao, final DominioSituacao situacao);

	List<MbcEquipamentoCirurgico> pesquisarEquipamentoCirurgicoModuloCustos(final Short codigo, final String descricao, final DominioSituacao situacao, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final Boolean asc);

	Long pesquisarEquipamentoCirurgicoCountModuloCusto(final Short codigo, final String descricao, final DominioSituacao situacao);

	MbcEquipamentoCirurgico obterEquipamentoCirurgicoByID(final Short codigo);

	Long pesquisarMotivosDemoraSalaRecCount(Short codigo, String descricao, DominioSituacao situacao);

	List<MbcMotivoDemoraSalaRec> pesquisarMotivosDemoraSalaRec(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult);

	/*
	 * Integer pesquisarMbcDisponibilidadeHorarioCount( AghUnidadesFuncionais unidadeFuncional, MbcSalaCirurgica salaCirurgica, AghEspecialidades especialidade, Date dataAgenda);
	 */

	List<PesquisaAgendarProcedimentosVO> pesquisarMbcDisponibilidadeHorario(AghUnidadesFuncionais unidadeFuncional, MbcSalaCirurgica salaCirurgica,
			AghEspecialidades especialidade, Date dataAgenda)throws ApplicationBusinessException;

	MbcMotivoCancelamento obterMotivoCancelamentoPorChavePrimaria(Short seq);

	List<MbcMotivoCancelamento> pesquisarMotivosCancelamento(Short codigo, String descricao, Boolean erroAgendamento, Boolean destSr, DominioMotivoCancelamento classificacao,
			DominioSituacao situacao, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc);

	Long pesquisarMotivosCancelamentoCount(Short codigo, String descricao, Boolean erroAgendamento, Boolean destSr, DominioMotivoCancelamento classificacao,
			DominioSituacao situacao);

	List<MbcQuestao> listarMbcQuestoes(Short mtcSeq);

	List<MbcValorValidoCanc> listarMbcValorValidoCancPorQuestao(Short qesMtcSeq, Integer qesSeq);

	void popularCancelamentoCirurgia(final TelaListaCirurgiaVO tela, final CirurgiaVO crg) throws ApplicationBusinessException;
	
	void popularListaValoresQuestaoEComplemento(final CirurgiaVO crg, final Short mtcSeq);
	
	Boolean verificarCancelamentoCirurgiaComDescricao(final Integer crgSeq);
	
	void validarQuestaoValorValidoEComplemento(MbcQuestao questao, MbcValorValidoCanc valorValidoCanc, String complementoCanc) throws ApplicationBusinessException;

	MbcValorValidoCanc obterMbcValorValidoCancById(MbcValorValidoCancId id);

	void verificarPermissaoCancelamentoCirurgia(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO) throws ApplicationBusinessException;
	
	void verificarCirurgiaPossuiDescricaoCirurgica(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO) throws ApplicationBusinessException;

	void executarEventoBotaoPressionadoListaCirurgias(final CirurgiaVO crgVO);

	void removerEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica) throws BaseException;

	void inserirControleEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica, String nomeMicrocomputador) throws BaseException;

	List<MbcFichaAnestesias> listarFichasAnestesiasPorItemSolicExame(Integer soeSeq, Short seqp, DominioIndPendenteAmbulatorio indPendAmbulat, Boolean dthrMvtoNull);

	List<MbcCirurgiaVO> listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(Integer ppcCrgSeq, Integer phiSeq, Boolean isItemCirg);

	void atualizarControleEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica, String nomeMicrocomputador) throws BaseException;

	MbcCirurgias obterMbcCirurgiaPorSituacaoRealizada(Integer crgSeq);

	List<MbcCirurgiaVO> listarItensOrteseProtese(Integer ppcCrgSeq);

	/*List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(String strPesquisa, Boolean somenteAtivos);

	Integer listarUnidadesFuncionaisPorUnidadeExecutoraCount(String strPesquisa, Boolean somenteAtivos);*/

	List<LinhaReportVO> listarUnidadesFuncionaisPorUnidInternacao(String strPesquisa, Boolean ativo);

	Long listarUnidadesFuncionaisPorUnidInternacaoCount(String strPesquisa, Boolean ativo);

	List<RelatorioPacientesComCirurgiaPorUnidadeVO> listarPacientesComCirurgia(Short seqUnidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia);

	List<RelatorioNotasDeConsumoDaSalaVO> listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(Short seqUnidadeCirurgica, Date dataCirurgia, Short numeroAgenda, Boolean nsDigitada) throws ApplicationBusinessException;
	
	List<RelatorioRegistroDaNotaDeSalaVO> listarRegistroNotaDeSalaMateriais(Integer crgSeq);
	
	List<ProtocoloEntregaNotasDeConsumoVO> listarRelatorioProtocoloEntregaNotasDeConsumo(Short unfSeq, Date data, 
			DominioSimNao pacienteSus, DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao);
	
	List<NotificacoesGeraisVO> listarNotificacoesCirurgia(final Integer codigoPaciente);
	
	List<NotificacoesGeraisVO> listarNotificacoesCirurgiaParaBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq);
		
	String obterIdadePorDataNascimento(Date dtNascimentoPaciente);

	Object executarDescreverCirurgiaOuOutra(final TelaListaCirurgiaVO telaVO, final CirurgiaVO cirurgiaVO) throws ApplicationBusinessException;

	Object executarEdicaoDescricaoCirurgica(final TelaListaCirurgiaVO tela, final CirurgiaVO cirurgiaVO) throws ApplicationBusinessException;

	boolean habilitarNotasAdicionais(final Integer crgSeq, final Short dcgSeqp) throws ApplicationBusinessException, ApplicationBusinessException;

	List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(Integer serMatricula, Short serVinCodigo, Short unfSeq, List<String> listaSigla,
			DominioSituacao situacao);

	List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(Object objPesquisa, Short unfSeq, DominioFuncaoProfissional indFuncaoProf,
			List<String> listaSigla, DominioSituacao situacao);

	List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(Object objPesquisa, Short unfSeq,
			List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao);

	List<MbcProfCirurgias> pesquisarProfCirurgiasAnestesistaPorCrgSeq(Integer crgSeq);

	List<MbcProfCirurgias> pesquisarProfCirurgiasEnfermeiroPorCrgSeq(Integer crgSeq);

	void inserirProfDescricoes(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profAtuaUnidCirgConselhoVO)
	throws ApplicationBusinessException;

	List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao);

	List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, List<DominioTipoAtuacao> listaTipoAtuacao);

	List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpServidorProfETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, Integer servidorMatricula,
			Short servidorVinCodigo, DominioTipoAtuacao tipoAtuacao);

	List<MbcProfDescricoes> pesquisarProfDescricoesOutrosPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao);

	void excluirProfDescricoes(final MbcProfDescricoes profDescricoes) throws ApplicationBusinessException;

	MbcDescricaoItens montarDescricaoItens(MbcDescricaoCirurgica descricaoCirurgica,
			String achadosOperatorios, String observacao,
			DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue,
			Integer volumePerdaSangue, String intercorrenciaClinica
			);

	void persistirDescricaoItens(MbcDescricaoItens descricaoItens) throws BaseException;

	void validarIntercorrenciaAchadosOperatorios(MbcDescricaoItens descricaoItens) throws ApplicationBusinessException;

	boolean atualizarDatasDescricaoCirurgica(final Integer dcgCrgSeq, final Short dcgSeqp, final Date lVdcDataCirurgia, final Date dtiDthrInicioCir, final Date dtiDthrFimCirg,
			final Date dtHrEntradaSala, final Date dtHrSaidaSala, final Short grcUnfSeq, final Short sciSeqp, final boolean alterandoDtInicio, final Short lPciTempoMinimo) throws BaseException;
	
	void validarAgendaProcedimentoAdicionadoExistente(List <MbcAgendaProcedimento> agendaProcedimentoList, MbcAgendaProcedimento agendaProcedimento) throws ApplicationBusinessException;
	
	public TempoSalaAgendaVO validaTempoMinimo(Date tempoSala, VMbcProcEsp procEsp) throws ParseException; 
	
	RegimeProcedimentoAgendaVO populaRegimeSus(DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc, VMbcProcEsp procEsp);

	AipPacientes obterPacientePorLeito(final AinLeitos leito) throws BaseException;

	void validarQtdeAgendaProcedimento(MbcAgendaProcedimento agendaProcedimento) throws ApplicationBusinessException;

	Set<MbcAgendaHemoterapia> buscarAgendaHemoterapia(Integer pciSeq, Short espSeq);

	void validarAgendaOrteseproteseAdicionadoExistente(List<MbcAgendaOrtProtese> agendaOrteseProteseList, MbcAgendaOrtProtese agendaOrteseProtese)
	throws ApplicationBusinessException;

	void validarQtdeOrtProtese(MbcAgendaOrtProtese agendaOrteseProtese) throws ApplicationBusinessException;
	
	MbcDiagnosticoDescricao obterDiagnosticoDescricaoPorChavePrimaria(final MbcDiagnosticoDescricaoId id);

	void inserirDiagnosticoDescricoesPreOperatorio(MbcDiagnosticoDescricao diagnosticoDescricao, 
			List<MbcDiagnosticoDescricao> listaPreOperatorio,
			List<MbcDiagnosticoDescricao> listaPosOperatorio) throws ApplicationBusinessException;

	void inserirDiagnosticoDescricoesPosOperatorio(MbcDiagnosticoDescricao diagnosticoDescricao, List<MbcDiagnosticoDescricao> listaPreOperatorio,
			List<MbcDiagnosticoDescricao> listaPosOperatorio) throws ApplicationBusinessException;
	
	void alterarDiagnosticoDescricoes(MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException;

	void excluirDiagnosticoDescricoes(final MbcDiagnosticoDescricaoId id)
			throws ApplicationBusinessException, ApplicationBusinessException;

	MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgsPorChavePrimaria(MbcProfAtuaUnidCirgsId id);

	Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> listarPacientesEmSalaRecuperacaoPorUnidade(Short seqUnidadeCirurgica, String ordemListagem)
	throws ApplicationBusinessException;

	void desfazCarregamentoDescricaoCirurgica(final Integer crgSeq, final Short crgSeqp) throws ApplicationBusinessException,
	ApplicationBusinessException;

	String pesquisarTituloEscala(DominioTipoEscala tipoEscala, Date dataCirurgia);

	List<EscalaCirurgiasVO> pesquisarEscalaSimples(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia) throws ApplicationBusinessException;

	List<EscalaCirurgiasVO> pesquisarEscalaCirurgica(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia,Integer grupoMat) throws ApplicationBusinessException;

	List<PesquisarPacientesCirurgiaVO> pesquisarPacientesCirurgia(AipPacientes paciente);

	List<MbcProcDescricoes> obterProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp, final String orderBy);

	MbcProcDescricoes obterMbcProcDescricoesPorChavePrimaria(MbcProcDescricoesId id);

	void excluirMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException;
	
	void alterarMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException;

	void inserirMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException;
	
	boolean validarTempoMinimoCirurgia(final Date dtiDthrInicioCir, final Date dtiDthrFimCirg, final Short lPciTempoMinimo);	

	Boolean pFinalizaDescricao(Integer crgSeq, Short dcgSeqp, String nomeMicrocomputador, DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue, String intercorrenciaClinica, Integer volumePerdaSangue, String achadosOperatorios, String descricaoTecnicaDesc) throws BaseException;
	
	void desbloqDocCertificacao(final Integer crgSeq, final DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException, ApplicationBusinessException;

	List<AghEspecialidades> pGeraDadosEsp();

	List<MbcProcedimentoCirurgicos> pGeraDadosProc(Short espSeq);

	List<MbcDescricaoPadrao> buscarDescricaoPadraoPelaEspecialidadeEProcedimento(Short espSeq, Integer procSeq);

	void persistirDescricaoTecnica(MbcDescricaoTecnicas descricaoTecnica) throws BaseException;

	void excluirDescricaoTecnica(MbcDescricaoTecnicas descricaoTecnica) throws BaseException;
	
	void validarTamanhoDescricaoTecnica(String descricaoTecnica) throws ApplicationBusinessException;

	List<MbcProcDescricoes> listarProcDescricoesComProcedimentoAtivo(Integer dcgCrgSeq, Short dcgSeqp);

	List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa, Short videoLaparoscopia,
			Date dtRealizCrgVideo, Date dtRealizCrgOrtese, Date dtRealizCrgSemVideo);
	
	ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp);

	void persistirAgenda(MbcAgendas agenda, RapServidores servidorLogado) throws BaseException;

	Long pesquisarCirurgiasPorPacienteCount(AipPacientes paciente);

	String buscaDescricaoMaterialExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer atdSeq);

	boolean habilitaEncaminhamentoExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer vAtdSeq);

	Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo);

	void persistirMbcFichaAnestesias(MbcFichaAnestesias fichaAnestesia, RapServidores servidorLogado) throws BaseException;

	List<MbcProfCirurgias> pesquisarMbcProfCirurgiasByCirurgia(Integer crgSeq);

	MbcCirurgias obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(Integer crgSeq);

	void persistirMbcNotaAdicionais(final MbcNotaAdicionais notaAdicional) throws ApplicationBusinessException;

	void excluirMbcNotaAdicionais(final MbcNotaAdicionais mbcNotaAdicionais) throws ApplicationBusinessException;

	boolean mbcpImprimeDescrCirurgica(final Boolean assinar) throws BaseException;

	/**
	 * Estória #22470
	 * 
	 * Busca as anotações de acordo com o seq de cirurgia
	 * 
	 * @param seq
	 * @return lista de anotações
	 * 
	 */
	List<MbcCirurgiaAnotacao> obterCirurgiaAnotacaoPorSeqCirurgia(Integer seq);

	/**
	 * Estória #22470
	 * 
	 * Busca a localização do paciente.
	 * 
	 * @param pacCodigo
	 * @return localização
	 */
	String obterQuarto(Integer pacCodigo);
	
	String obterQuarto(Integer pac_codigo, boolean ativarLinhaRelatorio);

	/**
	 * Estória #22470
	 * 
	 * Persiste a anotação.
	 * 
	 * @param anotacao
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	void persistirCirurgiaAnotacao(MbcCirurgiaAnotacao anotacao, Integer mbcCirurgiaCodigo) throws ApplicationBusinessException;

	List<MbcExtratoCirurgia> listarMbcExtratoCirurgiaPorCirurgia(Integer crgSeq);

	String persistirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) throws ApplicationBusinessException;
	
	String excluirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia, String usuarioLogado) throws ApplicationBusinessException;

	List<MbcDestinoPaciente> pesquisarDestinoPacientePorSeqOuDescricao(Object pesquisa, Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Byte param);

	Long pesquisarDestinoPacientePorSeqOuDescricaoCount(String strPesquisa);

	List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(Object objPesquisa, Boolean asc, Byte param, MbcDestinoPaciente.Fields ... fieldsOrder);

	Long pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(Object objPesquisa, Byte param);
	
	
	MbcProcEspPorCirurgias pesquisarTipagemSanguinea(Integer pCrgSeq);

	List<MbcMatOrteseProtCirg> pesquisarOrteseProtesePorCirurgia(Integer pCrgSeq);

	Collection<FichaPreOperatoriaVO> listarProcedimentoPorCirurgia(Short seqUnidadeCirurgica, String unidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia,
			Integer prontuario) throws ApplicationBusinessException;

	List<MbcProcEspPorCirurgias> pesquisarProcedimentoCirurgicoEscalaCirurgica(MbcCirurgias cirurgia);

	List<MbcAnestesiaCirurgias> pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(Integer crgSeq, DominioSituacao situacao);

	String gravarMbcCirurgias(MbcCirurgias cirurgia) throws ApplicationBusinessException, BaseException;

	String gravarAcompanhamentoMbcCirurgias(MbcCirurgias cirurgia, Boolean inserirAtendimento) throws ApplicationBusinessException, BaseException;

	List<MbcSolicHemoCirgAgendada> pesquisarSolicHemoterapica(Integer pCrgSeq);

	Boolean obterTipagemSanguinea(Integer pCrgSeq);
	
	VisualizaCirurgiaCanceladaVO buscarCirurgiaCancelada(Integer agdSeq) throws ApplicationBusinessException;

	String excluirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao) throws BaseException;

	MbcSolicHemoCirgAgendada obterMbcSolicHemoCirgAgendadaById(Integer crgSeq, String csaCodigo);

	String persistirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada solicHemoCirgAgendada) throws BaseException;

	void exluirMbcAnestesiaDescricoes(MbcAnestesiaDescricoes anestesiaDescricao) throws ApplicationBusinessException, ApplicationBusinessException;

	AlertaModalVO agendarProcedimentosParte1(final boolean emEdicao, CirurgiaTelaVO vo, final String nomeMicrocomputador)
	throws BaseException;
	
	void agendarProcedimentosParte2(final boolean emEdicao, CirurgiaTelaVO vo, AlertaModalVO alertaVO, final String nomeMicrocomputador) throws BaseException;	
	
	Date converterTempoPrevistoHoras(Short tempoPrevistoHoras, Byte tempoPrevistoMinutos);
	
	List<HistoricoAgendaVO> buscarAgendaHistoricos(Integer agdSeq);
	
	void refreshListMbcSolicHemoCirgAgendada(
			List<MbcSolicHemoCirgAgendada> listaSolicHemoterapicos);

	boolean containsMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada item);

	void refreshMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada item);
	
	
	List<VMbcProcEsp> pesquisarProcedimentosEspecialidadeProjeto(final String filtro, final Short espSeq, final Integer pjqSeq);

	Long pesquisarProcedimentosEspecialidadeProjetoCount(final String filtro, final Short espSeq, final Integer pjqSeq);
	
	VMbcProcEsp obterProcedimentosAgendadosPorId(Short espSeq, Integer pciSeq, Short seqp);

	List<CirurgiaTelaProcedimentoVO> pesquisarProcedimentosAgendaProcedimentos(Integer crgSeq, DominioIndRespProc indRespProc);
	
	List<CirurgiaTelaProfissionalVO> pesquisarProfissionaisAgendaProcedimentos(Integer crgSeq);
	
	List<CirurgiaTelaAnestesiaVO> pesquisarAnestesiasAgendaProcedimentos(Integer crgSeq);
	
   
	/**
	 * Estória #22466
	 * 
	 * Busca as solicitações especiais de acordo com o seq de cirurgia
	 * 
	 * @param crgSeq
	 * @return lista de solicitações especiais
	 * 
	 */
	List<MbcSolicitacaoEspExecCirg> pesquisarSolicitacaoEspecialidadePorCrgSeq(Integer crgSeq);
	
	MbcSolicitacaoEspExecCirg obterMbcSolicitacaoEspExecCirgPorChavePrimaria(MbcSolicitacaoEspExecCirgId id);
	
	void verificarSolicEspExistente(Integer crgSeq, Short nciSeq) throws ApplicationBusinessException, ApplicationBusinessException;
	
	void persistirSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws ApplicationBusinessException, BaseException;
	
	void atualizarSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException;
	
	void excluirSolicitacaoEspecial(Integer crgSeqExc, Short nciSeqExc, Short seqpExc) throws BaseException;

	
	/**
	 * Estória #22467
	 * 
	 * Persiste material órtese/prótese quando agendar procedimento
	 * 
	 * @param materialOrteseProtese
	 * @param obterLoginUsuarioLogado
	 * @throws BaseException
	 * 
	 */
	void persistirMatOrteseProtese(MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException;
	
	/**
	 * Estória #22467
	 * 
	 * Exclui material órtese/prótese quando agendar procedimento
	 * 
	 * @param materialOrteseProtese
	 * @param obterLoginUsuarioLogado
	 * @throws BaseException
	 * 
	 */	
	void excluirMatOrteseProtese(MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException;
	
	MbcMatOrteseProtCirg obterMbcMatOrteseProtCirgPorChavePrimaria(MbcMatOrteseProtCirgId id);
	
	String mbcImpressao(Integer crgSeq) throws ApplicationBusinessException;
		
	List<MbcDescricaoCirurgica> listarMbcDescricaoCirurgica(Integer crgSeq, Short seqp);
		
	MbcSolicitacaoCirurgiaPosEscala obterSolicitacaoCirurgiaPosEscalPorChavePrimaria(Integer speSeq);

	MbcProfDescricoes obterMbcProfDescricoesPorChavePrimaria(MbcProfDescricoesId mbcProfDescricoesId);
	
	Long obterNumeroCirurgiasAgendadasPorPacienteDia(Integer codigoPaciente, Date dataCirurgia);

	public void persistirMbcAgendaJustificativa(MbcAgendaJustificativa agendaJustificativa) throws BaseException;
	
	List<MbcCirurgias> pesquisarCirurgiasAgendaDataCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, Short agenda, Date data, Integer centroCusto);
	
	Long pesquisarCirurgiasAgendaDataCentroCustoCount(Short agenda, Date data, Integer centroCusto);
	
	List<MbcEquipamentoUtilCirg> pesquisarMbcEquipamentoUtilCirgPorCirurgia(Integer crgSeq) throws ApplicationBusinessException;
	
	MbcEquipamentoUtilCirg getMbcEquipamentosUtilCirgsPorMbcEquipamentoCirurgico(MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion, Short quantidade)  throws ApplicationBusinessException;
	
	void persistirListaMbcEquipamentoUtilCirg(List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirgs, MbcCirurgias cirurgia)  throws ApplicationBusinessException;

	void excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(Integer crgSeq, Short euuSeq) throws ApplicationBusinessException;

	String obterLeitoComoString(Integer codigo);

	Boolean atualizarQuantidadeSeExistirMbcEquipamentoCirurgicoNaListaMbcEquipamentoutilCirg(
			MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion,
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg, Short quantidade) throws ApplicationBusinessException;	
	
	
	List<MaterialPorCirurgiaVO> pesquisarMateriaisPorCirurgia(MbcCirurgias cirurgia, AghParametros pGrMatOrtProt, AghParametros pDispensario) throws ApplicationBusinessException;
	
	String obterMensagemMaterialCadastrado();
	
	/**
	 * Busca os procedimentos cirurgicos de um paciente durante uma internação
	 * @param atdSeq - Sequence do atendimento 
	 * @param dtInicioProcessamento - data Inicio do processamento 
	 * @param dtFimProcessamento - data de Fim do processamento
	 * @return List<{@link MbcProcEspPorCirurgias}> contendo as cirurgias
	 * @author jgugel
	 */
	List<ProcedimentoCirurgicoPacienteVO> buscarCirurgiasDoPacienteDuranteUmaInternacao(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento);
	
	void persistirMaterialPorCirurgia(List<MaterialPorCirurgiaVO> materiais, AghParametros pDispensario) throws ApplicationBusinessException;
	
	void validarMaterialNovo(MbcMaterialPorCirurgia materialPorCirurgia, List<MbcMaterialPorCirurgia> listaMateriais) throws ApplicationBusinessException;
	
	List<MbcCirurgias> pesquisarCirurgiasRealizadasNotaConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcCirurgias elemento);
	
	Long pesquisarCirurgiasRealizadasNotaConsumoCount(MbcCirurgias elemento);
	
	public Short obterMenorSeqpDescricaoCirurgicaPorCrgSeq(Integer crgSeq);

	//void downloadedCSV(final String fileName, final String name, final String contentType)throws IOException;
	
	String geraCSVRelatCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade) throws IOException, ApplicationBusinessException;


	List<RelatorioCirurgiasReservaHemoterapicaVO> recuperarRelatorioCirurgiasReservaHemoterapicaVO(Short seq, Date dataCirurgia, Boolean cirgComSolicitacao) throws ApplicationBusinessException;

	String cfESCALAFormula(Short unfSeq, Date dataCirurgia);  
	
	
	void excluirMaterialPorCirurgia(MaterialPorCirurgiaVO materialPorCirurgia) throws ApplicationBusinessException;
	
	MbcControleEscalaCirurgica obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
			Short unfSeq, Date dataAgenda, DominioTipoEscala tipo);
	
	boolean verificaExistenciaPeviaDefinitivaPorUNFData(Short unf_seq, Date dataPesquisa,DominioTipoEscala tipo );
	
	MbcControleEscalaCirurgica obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(Short unfSeq, Date dataEscala);
	
	List<RelatorioCirurgiasListaEsperaVO> obterListaEsperaPorUnidadeFuncionalEquipeEspecialidade(MbcProfAtuaUnidCirgs equipe, 
			Short espSeq, Short unfSeq, Integer pacCodigo);

	List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(
			Object objPesquisa, boolean b, Byte param, Fields descricao, Fields seq);

	Collection<MbcRelatCirurRealizPorEspecEProfVO> obterCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade) throws ApplicationBusinessException;
	
	void popularProcedimentoHospitalarInterno(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia) throws BaseException;

	void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo, final AlertaModalVO alertaVO,
			final boolean isPressionouBotaoSimModal) throws BaseException;

	void validarSeExisteCirurgiatransOperatorio(MbcCirurgias cirurgia) throws ApplicationBusinessException;	

	// #24941: Registro de cirurgia realizada e nota de consumo
	String validarProcedimentoFaturado(CirurgiaTelaVO vo);
	String validarFaturamentoPacienteTransplantado(CirurgiaTelaVO vo);
	List<String> validarProntuario(CirurgiaTelaVO vo, Integer validarProntuario);
	
	
	List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaENotaProcedimento(String strPesquisa, Short unfSeq, List<String> listConselhoMedico, boolean agenda);
	
	Long pesquisarProfissionaisAgendaENotaProcedimentoCount(String objPesquisa, Short unfSeq, List<String> obterConselhosMedicos, boolean agenda);
	
	
	AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(final boolean emEdicao, final boolean confirmaDigitacaoNS, CirurgiaTelaVO vo, final String nomeMicrocomputador) throws BaseException;
	void popularCodigoSsm(List<CirurgiaTelaProcedimentoVO> listaProcedimentos, MbcCirurgias cirurgia) throws BaseException;

	List<CirurgiaCodigoProcedimentoSusVO> listarCodigoProcedimentos(CirurgiaTelaProcedimentoVO procedimentoVO, DominioOrigemPacienteCirurgia origem) throws ApplicationBusinessException;
	
	List<RelatorioCirurgiaComRetornoVO> listarCirurgiasComRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException;

	Long quantidadeCirurgiasSemRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim);

	String gerarRelatorioCirurgiasComRetornoCSV(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException, IOException;

	//void downloadCSVRelatorioCirurgiasComRetorno(String fileName, String name) throws IOException;
	
	NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiaProcedProfissionalCSV(Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim, String extensaoArquivo) throws IOException, ApplicationBusinessException;
	
	NomeArquivoRelatorioCrgVO gerarRelatoriosTMOEOutrosTransplantesZip(Date dtInicio, Date dtFim, RapPessoasFisicas pessoasFisica,
			String extensaoArquivoRelatorio, String extensaoArquivoDownload) throws IOException, ApplicationBusinessException;

	void gravarTrocaLocalEspecilidadeEquipeSala(MbcAgendas agenda, String comeFrom) throws BaseException;
	
	List<MonitorCirurgiaSalaPreparoVO> pesquisarMonitorCirurgiaSalaPreparo(final Short unfSeq);
	
	List<MonitorCirurgiaSalaCirurgiaVO> pesquisarMonitorPacientesSalaCirurgia(final Short unfSeq);

	List<MonitorCirurgiaSalaRecuperacaoVO> pesquisarMonitorCirurgiaSalaRecuperacao(final Short unfSeq);
	
	List<MonitorCirurgiaConcluidaHojeVO> pesquisarMonitorCirurgiaConcluidaHoje(final Short unfSeq) throws BaseException;

	List<RelatorioControleChamadaPacienteVO> recuperarRelatorioControleChamadaPacienteVO(Short seq, Date dataCirurgia) throws ApplicationBusinessException;

	public void validarIntervaldoEntreDatas(final Date dataInicial, final Date dataFinal) throws ApplicationBusinessException;
	
	public <T extends MonitorCirurgiaVO> List<List<T>> obterListaResultadoPaginado(List<T> resultadoConsulta) throws BaseException ;

	String geraRelCSVCirurgiasExposicaoRadiacaoIonizante(final Date dataInicial, final Date dataFinal, final List<Short> unidadesFuncionais,
			final List<Short> equipamentos) throws ApplicationBusinessException, IOException;

	void validarProfissional(final RapPessoasFisicas rapPessoasFisicas)throws ApplicationBusinessException;

	List<RelatorioCirurgiasIndicacaoExamesVO> recuperarRelatorioCirurgiasIndicacaoExames(
			Short seqUnidCirurgia, Short seqUnidExames, Date dataCirurgia,
			Boolean cirgComSolicitacao) throws ApplicationBusinessException;

	String geraRelCSVDiagnosticosPrePosOperatorio(final Date dataInicial, final  Date dataFinal) throws IOException, ApplicationBusinessException;
	
	List<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO> listarProcedimentosAnestesicosRealizadosPorUnidade(
			Date dataInicial, Date dataFinal, ConstanteAghCaractUnidFuncionais caracteristicasUnidadeFuncional) throws ApplicationBusinessException;
	
	List<RelatorioProcedAgendPorUnidCirurgicaVO> pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
			Short unfSeq, Integer pciSeq, Date dtInicio, Date dtFim);
	
	NomeArquivoRelatorioCrgVO gerarRelatorioProcedAgendPorUnidCirurgicaCSV(Short unfSeq, Integer pciSeq,
			Date dtInicio, Date dtFim, String extensaoArquivo) throws IOException;
	
	void validarIntervaloEntreDatasRelatorioProcedAgendPorUnidCirurgica(
			final Date dataInicio, final Date dataFim) throws ApplicationBusinessException;
	
	List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiasPendenteRetorno(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq) throws ApplicationBusinessException;
	
	NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiasPendenteRetornoCSV(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq, String extensaoArquivo) 
					throws ApplicationBusinessException, IOException;
	
	void validarTipoPendenciaRelatorioCirurgiasPendenteRetorno(DominioTipoPendenciaCirurgia tipoPendenciaCirurgia)
			throws ApplicationBusinessException;
	
	public List<PortalPlanejamentoTurnosSalaVO> buscarTurnosHorariosDisponiveis(MbcAgendas agenda);
	
	List<MbcCirurgias> pesquisarRetornoCirurgiasEmLote(
			Short aghUnidadesFuncionaisSuggestionBox,
			Date dataCirurgia, Short mbcSalaCirurgicaSuggestionBox,
			Integer prontuario);

	void mostrarSliders(RetornoCirurgiaEmLoteVO vo);

	FatConvenioSaudePlano obterPlanoPorId(Byte planoId, Short convenioId);

	List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String objPesquisa);

	Long pesquisarCountConvenioSaudePlanos(String strPesquisa);


	List<MbcAnestesiaCirurgias> obterTipoAnestesia(Integer cirurgiaSeqSelecionada);

	List<MbcProcEspPorCirurgiasVO> buscarListaMbcProcEspPorCirurgiasVO(MbcCirurgias cirurgia) throws BaseException;

	List<MbcSalaCirurgica> pesquisarSalaCirurgia(Object objPesquisa, Short seq);
	
	List<PacientesEmSalaRecuperacaoVO> listarPacientesEmSR(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr) throws ApplicationBusinessException;

	Long listarPacientesEmSRCount(AghUnidadesFuncionais unidadeFuncional,
			Date dataEntradaSr) throws ApplicationBusinessException;

	String setarLocalizacao(Integer pacCodigo);

	String setarCirurgiao(Integer crgSeq);
	
	void validarIntervaloDatasPesquisa(Date dataInicio, Date dataFim) throws BaseException;
	
	public List<PacientesCirurgiaUnidadeVO> obterPacientesCirurgiaUnidade(Short unfSeq, Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo);

	public NomeArquivoRelatorioCrgVO gerarRelatorioPacientesCirurgiaUnidadeCSV(Short unfSeq, Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo, String extensaoArquivo) throws IOException, ApplicationBusinessException;


	List<ProfDescricaoCirurgicaVO> pesquisarAnestesistas(String strPesquisa,AghUnidadesFuncionais unf) throws ApplicationBusinessException;

	Long pesquisarAnestesistasCount(String strPesquisa,AghUnidadesFuncionais unf) throws ApplicationBusinessException;
	
	List<EscalaCirurgiasVO> pesquisarRelatorioEscalaCirurgiasAghu(final AghUnidadesFuncionais unidadesFuncional, final Date dataCirurgia, final String login, final Integer grupoMatCod, MbcSalaCirurgica sala) throws ApplicationBusinessException;

	public Byte buscarIntervaloEscalaCirurgiaProcedimento(MbcAgendas agenda);

	List<RelatorioPacientesEntrevistarVO> pesquisarRelatorioPacientesEntrevistar(
			Short seqUnidCirurgia, Date dataCirurgia,
			ProfDescricaoCirurgicaVO anestesista)
			throws ApplicationBusinessException;

	void removerMbcProcEspPorCirurgias(
			MbcProcEspPorCirurgias procEspPorCirurgia
			) throws BaseException;
	
	List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(String strPesquisa, Short especialidade);
	
	Long listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(String strPesquisa, Short especialidade);
	
	String gerarCSVProcedimentosCirurgicosPdtAtivos(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException, IOException ;
	
	void validarNroDeCopiasRelProcedCirgPdtAtivos(final Integer numeroDeCopias) throws ApplicationBusinessException;
	
	List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final Short especialidade, final Integer procedimento) throws ApplicationBusinessException;
	
	Long obterProcedimentosCirurgicosPdtAtivosCount(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException;
	
	List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosVO(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException;
	
	void removerMbcSolicHemoCirgAgendada(
			MbcSolicHemoCirgAgendada solicHemoCirgAgendada
			) throws BaseException;
	
	List<PacientesEmListaDeProcedimentosCanceladosVO> obterPacientesEmListaDeProcedimentosCancelados(
			MbcProfAtuaUnidCirgs equipe, Short espSeq, Short unfSeq,
			Integer pacCodigo) throws ApplicationBusinessException;

	public void persistirAgendaHemoterapia(MbcAgendaHemoterapia agendaHemoterapia) throws BaseException;

	public void excluirAgendaHemoterapia(final MbcAgendaHemoterapia agendaHemoterapia) throws BaseException;

	public void gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(
			RapServidores pucServidor, MbcProfAtuaUnidCirgs profAtuaUnidCirgs,
			AghEspecialidades especialidade,
			AghUnidadesFuncionais unidadeFuncional, Date dtAgenda,
			MbcAgendas agenda, Date dataInicial, Short sciSeqp,
			Byte ordemOverbooking, Byte proximaOrdemOverbooking,
			Boolean deslocaHorario) throws BaseException;

	public MbcSalaCirurgica obterSalaCirurgicaPorId(MbcSalaCirurgicaId id);
	
	void persistirAnestesiaCirurgias(MbcAnestesiaCirurgias anestesiaCirurgia) throws BaseException;
	
	void persistirProfessorResponsavel(MbcProfCirurgias profCirurgias) throws BaseException;

	void removerMbcAnestesiaCirurgias(MbcAnestesiaCirurgias anestesiaCirurgia) throws BaseException;

	void removerMbcSolicitacaoEspExecCirg(
			MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException;

	void removerMatOrteseProtese(MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException;

	void removerProfessorResponsavel(MbcProfCirurgias profCirurgias
			) throws BaseException;
	
	void mudarSituacao(RetornoCirurgiaEmLotePesquisaVO editada) throws ApplicationBusinessException ;

	void gravarListaRetornoCirurgiaEmLote (MbcCirurgias cirurgiaParaAlterar, String host)  throws ApplicationBusinessException, BaseException ;
	
	public MbcCirurgias obterCirurgiaPorSeqInnerJoinAtendimento(Integer seq);

	List<MbcCirurgias> pesquisarCirurgiasDeReserva(Date dataAgendamento, Integer agdSeq);

	Collection<RelatorioProdutividadePorAnestesistaVO> listarProdutividadePorAnestesista(
			AghUnidadesFuncionais unidadeCirurgica, DominioFuncaoProfissional funcaoProfissional, Date dataInicial,
			Date dataFinal);

	public Boolean verificarMaterialEspecial(MbcAgendas agenda) throws BaseException;
	
	List<RelatorioProfissionaisUnidadeCirurgicaVO> listarProfissionaisPorUnidadeCirurgica(Short seqUnidadeCirurgica, 
			Boolean ativosInativos, Short espSeq, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao);
	
	boolean temTarget(String componente, String target) throws ApplicationBusinessException;
	
	NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiasCanceladasCSV(
			Short unfSeq, Date dtInicio, Date dtFim, String extensaoArquivo)
			throws IOException, ApplicationBusinessException;

	List<CirurgiaComPacEmTransOperatorioVO> pesquisarCirurgiasComPacientesEmTransOperatorio(DominioSituacaoCirurgia situacaoCirurgia,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean atendimentoIsNull, List<CirurgiaComPacEmTransOperatorioVO> cirurgiasComPacientesEmTransOperatorio, AghUnidadesFuncionais unidadeFuncional);	
	
	String verificarColisaoDigitacaoNS(final CirurgiaTelaVO vo) throws ApplicationBusinessException;	
	
	public Boolean validarAvalicaoPreAnestesica(final Integer crgSelecionada) throws ApplicationBusinessException;
	
	public Map<AghAtendimentos, Boolean> validarExitenciaAvalicaoPreAnestesica(final Integer paciente) throws ApplicationBusinessException;

	List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(
			String strPesquisa, Boolean somenteAtivos);

	Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(
			String strPesquisa, Boolean somenteAtivos);
	
	List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Boolean pacientesQueNaoPossuemPrevisaoAlta, Integer pacCodigoFonetica,
			Date dataCirurgia);

	void preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(AghUnidadesFuncionais unidadeFuncional, Integer pacCodigoFonetica,
			VAghUnidFuncionalVO unidadeFuncionalMae, Date dataCirurgia, Boolean carateristicaUnidadeCirurgica) throws ApplicationBusinessException;

	Boolean habilitarAnestesia(Integer crgSeq,
			DominioSituacaoCirurgia dominioSituacaoCirurgia) throws BaseException;
	
	Boolean habilitarAnestesia(CirurgiaVO cirurgia) throws BaseException;
	
	Boolean verificarExistenciaFichaAnestesica(Integer crgSeq) throws BaseException;
	
	String obterUrlFichaAnestesica() throws BaseException;
	
	String identificarAnestesia(Integer crgSeq, String microLogado,String nomePaciente,String procedimento) throws BaseException;

	List<ScoUnidadeMedida> pesquisarUnidadeConsumoEConversaoMateriaisConsumidos(Integer matCodigo, Object param);
	
	Long pesquisarUnidadeConsumoEConversaoMateriaisConsumidosCount(Integer matCodigo, Object param);
	
	Integer obterSeqFichaAnestesica(CirurgiaVO crgSelecionada
			) throws BaseException;
	
	void validarIntervaloEntreDatasCirurgiasCanceladas(Date dataInicio,	Date dataFim) throws ApplicationBusinessException;	

	String mbccIdadeExtFormat(Date dtNascimento, Date data);

	void validarIntervaloDatasPesquisa(Date dataInicial, Date dataFinal,
			AghuParametrosEnum paramPeriodoEntreDatas) throws ApplicationBusinessException;

	List<ProcedimentosPOLVO> pesquisarProcedimentosComDescricaoPOL(
			Integer pacCodigo);

	List<ProcedimentosPOLVO> pesquisarProcedimentosSemDescricaoPOL(
			Integer pacCodigo);
	
	RelatorioLaudoAIHVO pesquisarLaudoAIH(Integer seqAtendimento,
			Integer codigoPac, Long seq, Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ApplicationBusinessException;
	
	boolean habilitaBotaoVisualizarRegistros(Integer atdSeqSelecionado, boolean isCheckSalaRecuperacao, Date crgSelecionada) throws ApplicationBusinessException;

	List<MamLaudoAihVO> listarLaudosAIH(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AipPacientes paciente);

	Long listarLaudosAIHCount(AipPacientes paciente);
	
	public List<MbcCirurgias> pesquisaCirurgiasPorCriterios(Date data, Boolean digitouNotaSala, Integer pacCodigo, Integer atendimentoSeq, DominioOrigemPacienteCirurgia origemPacienteCirurgia);

	List<Object> pesquisarVAinServInternaLaudoAih(Object pesquisa, Short espSeq);
	
	Long pesquisarVAinServInternaLaudoAihCount(Object pesquisa, Short espSeq);
	
	MamLaudoAih imprimirLaudoAih(Long seq) throws ApplicationBusinessException;

	Boolean verificarCirurgiaPossuiHemocomponentes(Integer crgSeq);
	
	/**
	 * Pesqusia salas cirurgicas ativas por SEQ e/ou NOME
	 * 
	 * Web Service #38100
	 * 
	 * @param unfSeq
	 * @param seqp
	 * @param nome
	 * @return
	 */
	List<MbcSalaCirurgica> obterSalasCirurgicasAtivasPorUnfSeqNome(final Short unfSeq, final Short seqp, final String nome);
	
	/**
	 * Count de salas cirurgicas ativas por SEQ e/ou NOME
	 * 
	 * Web Service #38100
	 * 
	 * @param unfSeq
	 * @param seqp
	 * @param nome
	 * @return
	 */
	Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(final Short unfSeq, final Short seqp, final String nome);
	
	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	List<MbcTipoAnestesias> pequisarTiposAnestesiasAtivas(final Short seq, final String descricao, Integer maxResults);

	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	Long pequisarTiposAnestesiasAtivasCount(final Short seq, final String descricao);

	/**
	 * Verifica a existência de Cirurgia para a Consulta informada.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @return Flag indicando a existência do registro
	 */
	public boolean verificarExistenciaCirurgiaPorConsulta(Integer numeroConsulta);

	/**
	 * Realiza uma busca por Cirurgias futuras agendadas, relacionadas ao Paciente informado.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @return Lista de Cirurgias relacionadas
	 */
	public List<MbcCirurgias> pesquisarCirurgiasFuturasAgendadasPorPaciente(Integer codigoPaciente);

	boolean isLateralidade(final Integer procedimentoCirurgicoSeq);
	
	List<Object> pesquisarVAinServInternaMatriculaVinculoEsp(Integer matricula, Short vinCodigo, Short espSeq);

	List<MbcGrupoAlcadaAvalOpms> listarGrupoAlcadaFiltro(
			Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms, 
			DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms,
			Short versao,
			DominioSituacao situacao);

	Long listarGrupoAlcadaFiltroCount(
			Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms, Short versao,
			DominioSituacao situacao);

	void removerMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms itemExclusao) throws BaseException;

	List<CirurgiaCodigoProcedimentoSusVO> listarCodigoProcedimentosSUS(
			Integer pciSeq, Short espSeq, 
			DominioOrigemPacienteCirurgia origemPacienteCirurgia) throws ApplicationBusinessException;

	List<AghCid> pesquisarCidsPorPhiSeq(Integer phiSeq, DominioTipoPlano plano, String filtro) throws ApplicationBusinessException;

	List<MbcCirurgias> pesquisarCirurgiaPorCaractUnidFuncionais(Integer crgSeq,
			ConstanteAghCaractUnidFuncionais caracteristica);
	
	List<RequerenteVO> consultarRequerentes(Object requerente);
	
	List<ExecutorEtapaAtualVO> consultarExecutoresEtapaAtual(Object executor);
	
	List<PendenciaWorkflowVO> buscarPendenciasWorkflowPorUsuarioLogado(RapServidores usuarioLogado);
		
	boolean habilitaUsuarioConectadoAhUnidade(final Integer pIdentificao, final Integer atdSeq);
	boolean verificarProfissionalEstaAtivoEmOutraUnidade(MbcProfAtuaUnidCirgs profissional);
	
	boolean habilitaBotaoPrescrever(CirurgiaVO crg) throws ApplicationBusinessException, ApplicationBusinessException ;
	
	SolicitarReceberOrcMatNaoLicitadoVO pesquisarMatNaoLicitado(Short seqRequisicaoOpme2);
	
	List<MbcItensRequisicaoOpmes> pesquisarMatSolicitacaoOrcamento(Short seqRequisicaoOpme2);
	
	void atualizarDetalheMaterial(MbcItensRequisicaoOpmes itemDetalhado);

	void gravarMateriaisItemOpmes(MbcMateriaisItemOpmes novoMateriaisItens);

	AghEspecialidades buscaAghEspecialidadesPorSeq(Short especialidadeSeq);
	
	List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(Object unidade);
	
	List<EspecialidadeVO> pesquisarEspecialidade(Object especialidade);
	
	List<EquipeVO> pesquisarEquipe(Object equipe);

	MbcAgendas validarUsuarioSituacaoAjustarProcedimento(RequisicoesOPMEsProcedimentosVinculadosVO consAjusProcItem, String string) throws ApplicationBusinessException;

	MbcSolicitacaoEspExecCirg obterMbcSolicitacaoEspExecCirgPorChavePrimaria(
			MbcSolicitacaoEspExecCirgId id, Enum[] inner, Enum[] left);
	
	MbcSolicitacaoEspExecCirg pesquisarMbcSolicitacaoEspNessidadeCirurgicaEUnidadeFuncional(MbcSolicitacaoEspExecCirgId id);
	
	String obterPendenciaFichaAnestesia(Integer atdSeq);

	List<AghCid> pesquisarCidsPorPciSeq(Integer pciSeq, String descricao, DominioTipoPlano plano, String filtro);
	
	List<DescricaoCirurgicaMateriaisConsumidosVO> pesquisarMateriaisConsumidos(Integer cirurgiaSeq);

	void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos);

	void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido);

	String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme);
    
    void executaRotinaParaSetarResponsavelAoConfirmarNotaConsumo(List<CirurgiaTelaProfissionalVO> listaProfissionais, Short seqUnidadeCirurgica) 
			throws ApplicationBusinessException;

	void executarRotinaVincularCidUnicoAoProcedimento(
			List<CirurgiaTelaProcedimentoVO> listaProcedimentos, DominioTipoPlano tipoPlano)throws ApplicationBusinessException;

	
    MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgsId id);
	
	public MbcDestinoPaciente obterDestinoPaciente(Byte seq);
	
	List<Object[]> obterCirurgiaDescCirurgicaPaciente(Integer pacCodigo, List<Short> listaUnfSeq, Date dataCirurgia);
	
	Object[] obterConselhoESiglaVRapServidorConselho(Integer matricula, Short vinCodigo);
	
	public List<MbcEquipeVO> obterProfissionaisAtuamUnidCirurgica(String filtro);
	
	public Long obterProfissionaisAtuamUnidCirurgicaCount(Object filtro);
	
	public List<ProcedimentoCirurgicoVO> listarProcCirurgicosPorGrupo(String filtro);
	
	public Long listarProcCirurgicosPorGrupoCount(Object filtro);
	
	public AghAtendimentos obterAtendimentoVigentePacienteInternado(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg);

	Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(
			Object objPesquisa, Short unfSeq,
			DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla,
			DominioSituacao situacao);

	Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(
			Object objPesquisa, Short unfSeq,
			List<DominioFuncaoProfissional> listaIndFuncaoProf,
			List<String> listaSigla, DominioSituacao situacao);

	Long pesquisarSuggestionEquipeCount(AghUnidadesFuncionais unidade,
			Date dtProcedimento, String filtro, boolean indResponsavel);

	Long pesquisarSuggestionProcedimentoCount(AghUnidadesFuncionais unidade,
			Date dtProcedimento, String filtro, Short eprEspSeq,
			DominioSituacao situacao, boolean indPrincipal);
	
	public MbcProcedimentoCirurgicos obterProcedimentoLancaAmbulatorio(Integer seq);
	
	public List<MbcDescricaoCirurgica> listarDescCirurgicaPorSeqESituacaoOrdenadasPorSeqp(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao);

	
	MbcCirurgias obterCirurgiaPorSeqServidor(Integer seq);
	
	MbcCirurgias obterCirurgiaPorDtInternacaoEOrigem(Integer pacCodigo, Date dtInternacao, DominioOrigemAtendimento origem);

	MbcProfAtuaUnidCirgs buscarNomeResponsavelCirurgia(
			MbcProfCirurgias mbcProfCirurgias);

	MbcProfCirurgias buscarProfissionalResponsavel(
			Integer cirurgiaSeqSelecionada);

	List<MbcCirurgias> listarLocalPacMbc(Integer pacCodigo);
	List<MbcCirurgias> obterCirurgiasPorPacienteEDatas(List<Date> datas, Integer pacCodigo2, List<Short> listaSeqp);
	
	List<RapServidores> listarFichasAnestesias(Integer atedimentoSeq, Integer gsoPacCodigo, Short gsoSequence,DominioIndPendenteAmbulatorio pendente, Integer pciSeq, DominioSituacaoExame situacaoProcedimento);
	List<RequisicoesOPMEsProcedimentosVinculadosVO> pesquisarRequisicaoOpmes(
			Short seqRequisicao,
			Date dataRequisicao,
			RequerenteVO requerente,
			DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,
			ExecutorEtapaAtualVO executorEtapaAtual, Date dataProcedimento,
			UnidadeFuncionalVO unidadeFuncional, EspecialidadeVO especialidade,
			EquipeVO equipe, Integer prontuario, Boolean pesquisarRequisicao,
			Integer nrDias, Integer executorSeq, Integer etapaSeq);
	void inserirAtendimentoPreparoCirurgia(AipPacientes paciente, MbcCirurgias cirurgia, String nomeMicrocomputador)throws BaseException;

	MbcProfAtuaUnidCirgs buscarNomeResponsavelCirurgia(
			Integer cirurgiaSeqSelecionada);

	List<MbcSalaCirurgica> buscarSalasPorAgenda(MbcAgendas agenda);

	List<AvalOPMEVO> verificaObrigRegistroOpmes(MbcAgendas agenda);

	List<MbcMateriaisItemOpmes> buscarItemMaterialPorItemRequisicao(
			MbcItensRequisicaoOpmes itemReq);
	List<MbcExtratoCirurgia> pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(Integer crgSeq, DominioSituacaoCirurgia situacao);
	
	public MbcCirurgias obterCirurgiaAtendimentoCancelada(Integer crgSeq);

	List<PdtViaAereas> obterViasAereasAtivasOrdemDescricao();

	void persistirMbcAvalPreSedacao(MbcAvalPreSedacao mccAvalPreSedacao)
			throws ApplicationBusinessException;

	MbcAvalPreSedacao pesquisarMbcAvalPreSedacaoPorDdtSeq(MbcAvalPreSedacaoId id);

	void inserirProf(Integer dcgCrgSeq, Short dcgSeqp,
			ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO)
			throws ApplicationBusinessException;

	List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(Integer firstResult, Integer maxResult,
			Object objPesquisa, Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao);
	
	ProfDescricaoCirurgicaVO obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(Integer dcgCrgSeq, Short dcgSeqp);

	List<MbcProfAtuaUnidCirgsVO> obterMbcProfAtuaUnidCirgs(Integer equipeSeqp, Short paramValue);

	List<MbcFichaTipoAnestesiaVO> buscarProcedimentoAgendado(Integer seq, Integer pacCodigo, Short gestacaoSeqp, int intValue, int intValue2);
	
//	FatConvenioSaudePlano validarConvenioPorAtendimento(AghAtendimentos atendimendoOrigemUrgencia);
//	
//	FatConvenioSaudePlano validarAtendimentoUrgencia(Integer pacCodigo,Short gestacaoSeqp, AghAtendimentos atendimendoOrigemUrgencia);
	
	void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo
			,Short gestacaoSeqp
			,String nivelContaminacao
			,Date dataInicioProcedimento
			,Short salaCirurgicaSeqp
			,Short tempoDuracaoProcedimento
			,Short anestesiaSeqp
			,Short equipeSeqp
			,String tipoNascimento) throws BaseException;
	
	Long obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo);
	
	List<CirurgiaVO> pesquisarCirurgias(Integer crgSeq);
	
	List<br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO> obterCirurgiasPorPacienteEDatasGestacao(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp);

	void persistirProfDescricaoExecutorAnestesia(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO) throws ApplicationBusinessException;
	
	RelatorioLaudoAIHSolicVO pesquisarLaudoAIHSolic(String materialSolicitado, Integer codigoPac, Integer matricula, Short vinCodigo) throws ApplicationBusinessException;
	
	void verificarPacienteSalaPreparo(Integer crgSeq, String nomeMicrocomputador, Date vinculoServidor) throws ApplicationBusinessException, BaseException;
}