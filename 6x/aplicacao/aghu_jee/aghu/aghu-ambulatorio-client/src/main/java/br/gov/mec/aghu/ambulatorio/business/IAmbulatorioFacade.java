package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.exception.GenericJDBCException;

import br.gov.mec.aghu.ambulatorio.pesquisa.vo.ConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.ArquivosEsusVO;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.CabecalhoRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasDeOutrosConveniosVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.ConverterConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.DisponibilidadeHorariosVO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeRelacionadaVO;
import br.gov.mec.aghu.ambulatorio.vo.EvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.ExamesLiberadosVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaBloqueioConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaRetornoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroEspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.GeraEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.GerarDiariasProntuariosSamisVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.ambulatorio.vo.MamConsultorAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRecCuidPreferidoVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRelatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghEspecialidadesAtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghPerfilProcessoVO;
import br.gov.mec.aghu.ambulatorio.vo.PesquisarConsultasPendentesVO;
import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedHospEspecialidadeVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedimentoAtendimentoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.ambulatorio.vo.ReceitasGeralEspecialVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultoriaAmbulatorialVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioControleFrequenciaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioSolicitacaoProcedimentoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatoriosInterconsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.SolicitaInterconsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.TDataVO;
import br.gov.mec.aghu.ambulatorio.vo.TransferirExamesVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioConsultaGenerica;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoProfissionalAnamnese;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoUnidadeFuncionalSala;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.StatusPacienteAgendado;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.FatConsultaPrhVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.faturamento.vo.TipoProcedHospitalarInternoVO;
import br.gov.mec.aghu.faturamento.vo.TriagemRealizadaEmergenciaVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolDiagnosticoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolEvolucaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolPrescricaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.paciente.vo.AipEnderecoPacienteVO;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.paciente.vo.SolicitanteVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemReceitaMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoAmbulatorioVO;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.view.VMamReceitas;
import br.gov.mec.aghu.vo.RapServidoresVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IAmbulatorioFacade extends Serializable {

	public void copiarEscolhidos(Long evoSeq, List<ExamesLiberadosVO> examesSelecionados);
	
	public List<ExamesLiberadosVO> montarTelaExamesLiberados(Integer pacCodigo, Integer numeroConsulta) throws ApplicationBusinessException;

	public void atualizarIndPrcrImpressaoTratamentoFisiatrico(Integer atdSeq, Integer pteSeq);
	
	public void atualizarIndImpressaoReceituarioCuidado(Long seq);
	
	public void atualizarIndImpressaoAtestado(Long seq);

	public void atualizarDataInicioAtendimento(Integer numeroConsulta, Date dataInicio, String nomeMicrocomputador) throws BaseException;

	public void atualizarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes,
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld) throws BaseException;

	public void persistirMamAtestado(MamAtestados atestado, Boolean imprimeAtestado) throws ApplicationBusinessException;

	public void excluirMamAtestado(Long atsSeq) throws ApplicationBusinessException;
	
	public void validarPreAtualizarAtestado(MamAtestados atestado, MamAtestados atestadoOld) throws ApplicationBusinessException;

	public void atualizarAlergias(MamAlergias alergia, MamAlergias alergiaOld) throws ApplicationBusinessException;

	@Deprecated
	public AacConsultas atualizarConsulta(AacConsultas consulta,
											AacConsultas consultaAnterior, String nomeMicrocomputador, final Date dataFimVinculoServidor,
											final Boolean substituirProntuario) throws NumberFormatException,
											BaseException;

	public AacConsultas atualizarConsulta(AacConsultas consulta,
			AacConsultas consultaAnterior, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws NumberFormatException,
			BaseException;
	
	public AacConsultas refreshConsulta(AacConsultas consulta);

	@Deprecated
	public AacConsultas inserirConsulta(AacConsultas consulta, String nomeMicrocomputador, Boolean substituirProntuario)
			throws NumberFormatException, BaseException;

	/**
	 * Método responsável por realizar a persistência de um diagnóstico.
	 * 
	 * @param diagnostico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public MamDiagnostico persistirDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException;

	/**
	 * 
	 * @param consulta
	 * @throws BaseException
	 */
	public void cancelarAtendimento(AacConsultas consulta, String nomeMicrocomputador) throws BaseException;
	public void cancelarAtendimentoSituacao(Integer conNumero, String nomeMicrocomputador) throws BaseException,GenericJDBCException ;
	public boolean verificaExtratoPacAtendidoOuFechado(Integer conNumero) throws ApplicationBusinessException;
	/**
	 * 
	 * @param consulta
	 * @throws BaseException
	 */
	public void finalizarAtendimento(AacConsultas consulta, Boolean possuiProcedimentoRealizado, String nomeMicrocomputador)
			throws BaseException;
	public void finalizarAtendimento(Integer consultaNumero,String nomeMicrocomputador) 
			throws BaseException;
	public Boolean concluirBlocoNaoAssina(AacConsultas consulta, String tipoCorrente) throws BaseException;

	/**
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void persistirProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException;

	public void persistirCaracteristica(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException;

	public void persistirCaracteristicaJn(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException;

	public void validaRegrasAtendimento(AacConsultas consulta, Boolean supervisionar, Boolean atender, String nomeMicrocomputador)
			throws ApplicationBusinessException, ApplicationBusinessException;

	public void atualizaControleAguardandoLivre(Integer numeroConsulta, Date dthrMovimento, String nomeMicrocomputador)
			throws ApplicationBusinessException, ApplicationBusinessException;
	
	public void inserirAtestadoAmbulatorio(MamAtestados atestados) throws BaseException;
	
	public List<RelatorioAtestadoVO> recuperarInfConsultaAtestados(List<MamAtestados> atestado) throws ApplicationBusinessException;
	
	public List<MamAtestados> listarAtestado(MamAtestados atestado);
	
	public void atualizaControleAguardandoUso(Integer numeroConsula, Date dthrMovimento) throws ApplicationBusinessException;

	public void persistirSituacao(AacGradeSituacao gradeSituacao) throws ApplicationBusinessException;

	public void removerProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException;

	public void removerCaracteristica(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException;

	public void removerSituacao(AacGradeSituacao situacao) throws ApplicationBusinessException;
	
	public List<MamTipoAtestado> listarTodos();

	/**
	 * Lista os diagnósticos Ativos e validados de acordo com paciente e cid.
	 * 
	 * @param paciente
	 * @param cid
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosPorPacienteCid(AipPacientes paciente, AghCid cid);

	/**
	 * Método que retorna os diagnósticos ativos relativos a um cidAtendimento.
	 * 
	 * @param cidAtendimento
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosAtivosPorCidAtendimento(MpmCidAtendimento cidAtendimento);

	/**
	 * Retorna os diagnósticos de um atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */

	public List<MamDiagnostico> listardiagnosticosPorAtendimento(AghAtendimentos atendimento);

	/**
	 * @return ORADB: Procedure MAMP_CHECK_OUT_INT Adicionado , pois chama
	 *         função do Oracle
	 */

	public void gerarCheckOut(Integer seq, Integer pacCodigo, String tipoAltaMedicaCodigoOld, String tipoAltaMedicaCodigo, Short unfSeqOld,
			Short unfSeq, Boolean pacienteInternadoOld, Boolean pacienteInternacao) throws ApplicationBusinessException;

	/**
	 * ORADB: Procedure MAMP_ATU_TRG_EMATEND Adicionado , pois chama função do
	 * Oracle
	 */

	public void atualizarSituacaoTriagem(Integer pacCodigo) throws ApplicationBusinessException;

	public void validarSelecaoImpressaoConsultaAmbulatorio(Integer numero, Boolean selecionar) throws BaseException;

	public List<AacGradeProcedHospitalar> listarProcedimentosGrade(Integer grdSeq);

	public List<AacCaracteristicaGrade> listarCaracteristicasGrade(Integer grdSeq);

	public List<AacGradeSituacao> listarSituacoesGrade(Integer grdSeq);

	/**
	 * 
	 * @param especialidade
	 * @return
	 */
	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidades(Object parametro, AghEspecialidades especialidade);

	/**
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	public Long listarProcedimentosEspecialidadesCount(Object parametro, AghEspecialidades especialidade);

	/**
	 * Lista os procedimento das especialidades, incluindo procedimentos de
	 * especialidades genéricas
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	public List<ProcedHospEspecialidadeVO> listarProcedimentosEspecialidadesComGenericas(Object parametro, AghEspecialidades especialidade);

	/**
	 * Count para listar procedimento das especialidades, incluindo
	 * procedimentos de especialidades genéricas
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	public Integer listarProcedimentosEspecialidadesComGenericasCount(Object parametro, AghEspecialidades especialidade);

	/**
	 * 
	 */
	public List<String> listarCaracteristicas();

	public List<AacNivelBusca> pesquisarNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento);

	/**
	 * 
	 * @return
	 */
	public AacGradeAgendamenConsultas salvarGradeAgendamentoConsulta(AacGradeAgendamenConsultas entity, AacGradeAgendamenConsultas oldEntity)
			throws ApplicationBusinessException;

	public void validaHorarioSobreposto(AacHorarioGradeConsulta entity, AacGradeAgendamenConsultas grade)
			throws ApplicationBusinessException;

	public void salvarHorarioGradeConsulta(AacHorarioGradeConsulta entity,
			AacGradeAgendamenConsultas entityPai) throws ApplicationBusinessException;

	public List<AacHorarioGradeConsulta> pesquisarHorariosPorGrade(AacGradeAgendamenConsultas grade);

	public void excluirHorarioGradeConsulta(AacHorarioGradeConsulta entity) throws ApplicationBusinessException;

	public List<GradeAgendamentoVO> listarAgendamentoConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento, Boolean filtroEnviaSamis, DominioSituacao filtroSituacao,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores profissional, AelProjetoPesquisas filtroProjeto,
			Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim, Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao);

	public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorarios(Integer seq, Short unfSeq,
			AghEspecialidades especialidade, AghEquipes equipe,
			RapServidores profissional, AacPagador pagador,
			AacTipoAgendamento autorizacao, AacCondicaoAtendimento condicao,
			Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana diaSemana, Boolean disponibilidade,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException;

	public List<AacFormaEspecialidade> listaFormasEspecialidadePorFormaAgendaneto(AacFormaAgendamento formaAgendamento);

	public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorariosEmergencia(String orderProperty, boolean asc)
			throws ApplicationBusinessException;

	public Long listarAgendamentoConsultasCount(Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento,
			Boolean filtroEnviaSamis, DominioSituacao filtroSituacao, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe,
			RapServidores profissional, AelProjetoPesquisas filtroProjeto, Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim,
			Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao);

	public Integer listarDisponibilidadeHorariosCount(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana dia, Boolean disponibilidade, Boolean visualizarPrimeirasConsultasSMS);

	public Long listarDisponibilidadeHorariosEmergenciaCount();

	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim);

	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim, Boolean utilizarDataFim);

	public Long listarConsultasPacienteCount(Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral,
			Integer filtroCodConsultaAnterior, Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio,
			Date filtroDtFim);

	public List<VAacSiglaUnfSalaVO> pesquisarZonas(String objPesquisa);

	public List<VAacSiglaUnfSalaVO> pesquisarTodasZonas(String objPesquisa);

	public AacGradeAgendamenConsultas obterGrade(Integer seq);
	
	AacGradeAgendamenConsultas obterAacGradeAgendamenConsultas(Integer seq, Enum[] innerJoins, Enum[] leftJoins);

	public AacRetornos obterRetorno(Integer seq);

	public AacUnidFuncionalSalas obterUnidadeFuncional(Short unfSeq, Byte sala);

	public boolean verificarGradeConsulta(Integer seq);

	public void removerGradeConsulta(Integer seqGrade) throws ApplicationBusinessException;

	/**
	 * Método que retorna a lista de condições de atendimento ativas.
	 * 
	 * @return
	 */
	public List<AacCondicaoAtendimento> listarCondicaoAtendimento();

	/**
	 * Método que retorna as situações de consulta ativas pela
	 * descrição/situação.
	 * 
	 * @param objPesquisa
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacao(String objPesquisa);

	/**
	 * Método que retorna as situações de consulta ativas.
	 * 
	 * @return
	 */
	public List<AacSituacaoConsultas> obterSituacoesAtivas();

	/**
	 * Método que retorna as situações de consulta ativas (exceto as Marcadas).
	 * 
	 * @param objPesquisa
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacaoSemMarcada(String objPesquisa);

	/**
	 * Método que retorna a situação da consulta pelo Id.
	 * 
	 * @param objPesquisa
	 * @return AacSituacaoConsultas
	 */
	public AacSituacaoConsultas pesquisarSituacaoConsultaPeloId(String objPesquisa);

	/**
	 * Método que retorna a lista de pagadores com agendamento.
	 * 
	 * @return
	 */
	public List<AacPagador> obterListaPagadoresComAgendamento();

	/**
	 * Método que retorna a lista de autorizações ativas.
	 * 
	 * @return
	 */
	public List<AacTipoAgendamento> obterListaAutorizacoesAtivas();

	public int gerarDisponibilidade(AacGradeAgendamenConsultas entity, Date dataInicial, Date dataFinal, String nomeMicrocomputador)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NumberFormatException,
			BaseException;

	public void alterarDisponibilidadeConsultas(List<Integer> consultas, AacSituacaoConsultas novaSituacao, String nomeMicrocomputador)
			throws BaseException;

	public void manterConsulta(AacConsultas consultaAnterior, AacConsultas consulta, final AacFormaAgendamentoId idFormaAgendamento,
			final boolean emergencia, final String nomeMicrocomputador, boolean cameFromInterconsultas) throws BaseException;

	List<String> validarItensPreManterConsulta(final AacConsultas consulta) throws ApplicationBusinessException;

	public FatConvenioSaudePlano popularPagador(Integer numeroConsulta) throws ApplicationBusinessException;
	
	public void verificarConsultaExcedenteDiaBloqueado(
			AacConsultas consulta)
			throws ApplicationBusinessException;

	public Boolean verificarConsultaDiaNaoProgramado(
			AacConsultas consulta);
	
	public String obterCabecalhoListaConsultasPorGrade(AacGradeAgendamenConsultas grade);
	
	public VAacConvenioPlano obterVAacConvenioPlanoAtivoPorId(Short cspCnvCodigo, Byte cspSeq);

	public Boolean verificarCaracEspecialidade(AghEspecialidades especialidade, DominioCaracEspecialidade caracteristica);

	public List<AacConsultas> obterConsultasNaoRealizadasPorGrade(AacGradeAgendamenConsultas grade, Short pgdSeq, Short tagSeq,
			Short caaSeq, Boolean emergencia, Date dtConsulta, Date mesInicio, Date mesFim, Date horaConsulta,
			DominioDiaSemana diaSemana, boolean excluirPrimeiraConsulta, boolean visualizarPrimeirasConsultasSMS)
			throws ApplicationBusinessException;

	public List<AacConsultas> listarHorariosConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia, Date filtroHoraConsulta,
			AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio,
			Date filtroDtFim, Integer grdSeq);

	public List<Integer> listarHorariosConsultasSeqs(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta,
			DominioDiaSemana filtroDia, Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio, Date filtroDtFim, Integer grdSeq);

	public Long listarHorariosConsultasCount(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia,
			Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao,
			Date filtroDtInicio, Date filtroDtFim, Integer grdSeq);

	public List<AacConsultas> listarHorariosConsultasNaoMarcadas(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta,
			DominioDiaSemana filtroDia, Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio, Date filtroDtFim, Integer grdSeq);

	public AacConsultas obterConsultasMarcada(Integer nroConsulta, Boolean relacionaFormaAgendamento);

	public List<AacFormaAgendamento> pesquisaFormaAgendamentoPorStringEFormaAgendamento(String parametro, AacPagador pagador,
			AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento);

	public Long pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount(String parametro, AacPagador pagador,
			AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento);

	public List<AacConsultas> listarConsultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno,
			FatProcedHospInternosVO procedimento);

	public List<ConsultasAgendaVO> listarConsultasAgendaScrooler(FccCentroCustos filtroServico, Integer filtroGrdSeq,
			Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador,
			AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao,
			DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim,
			RapServidores filtroProfissional, AacRetornos filtroRetorno, Integer firstResult, Integer maxResult, FatProcedHospInternosVO procedimento);

	public void validarPesquisaDisponibilidadeHorarios(Integer seq, AghEspecialidades especialidade, Date horaConsulta, Date dtConsulta,
			Date mesinicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, DominioTurno turno) throws ApplicationBusinessException;

	public Long listarConsultasAgendaCount(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno,
			FatProcedHospInternosVO procedimento);

	public List<ConsultaAmbulatorioVO> consultaPacientesAgendados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,   
			Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas,
			VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
			RapServidores profissional, StatusPacienteAgendado status) throws ApplicationBusinessException;

	public Long consultaPacientesAgendadosCount(Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas,
			VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
			RapServidores profissional, StatusPacienteAgendado status) throws ApplicationBusinessException;

	public AipPacientes obterPacienteConsulta(AacConsultas consulta);

	public AacConsultas obterConsultaPorNumero(Integer numero);

	AacConsultas obterConsultaPorNumero(Integer numero, Enum[] innerJoin, Enum[] leftJoin);

	public AacConsultas obterAacConsultasAtenderPacientesAgendados(Integer numero);
	
	public boolean obterAlaPorNumeroConsulta(Integer numero);

	public List<AacConsultasJn> obterHistoricoConsultasPorNumero(Integer numero);

	public AacMotivos obterDescricaoMotivoPorCodigo(Short mtoSeq);

	public AacRetornos obterDescricaoRetornoPorCodigo(Integer retSeq);

	boolean excluirListaConsultas(List<Integer> consultas, Integer grdSeq, String nomeMicrocomputador, boolean substituirProntuario)
			throws BaseException;

	void excluirConsultasAghAtendimento(Integer seqAtendimento, String nomeMicrocomputador, Boolean substituirProntuario)
			throws BaseException;

	public List<String> validaDadosPaciente(Integer codigo);

	public Boolean verificarTrocaPacienteConsulta(Integer codigo);

	public Boolean necessitaRecadastramentoPaciente(Integer pacienteCodigo, Date dtRecadastroPaciente, Date dtConsulta);

	public void registraChegadaPaciente(Integer numeroConsulta, String nomeMicrocomputador, Integer retSeq) throws BaseException;

	public void desmarcaChegadaPaciente(Integer consultaNumero, String nomeMicrocomputador) throws ApplicationBusinessException;

	public void verificaPacienteOutraConsulta(Integer numeroConsulta) throws BaseException;

	public void atualizaControleConsultaSituacao(AacConsultas consulta, MamSituacaoAtendimentos situacao, String nomeMicrocomputador)
			throws ApplicationBusinessException, ApplicationBusinessException;

	public List<VMamProcXCid> pesquisarCidsPorPrdSeqCidSeq(Integer prdSeq, Integer cidSeq);

	public void validarQuantidadeProcedimentoAtendimentoConsulta(Byte quantidade) throws ApplicationBusinessException;

	public void verificarCidProcedimentoAtendimento(Integer consultaNumero, Integer prdSeq, Integer cidSeq)
			throws ApplicationBusinessException, ApplicationBusinessException;

	public MamProcedimentoRealizado alterarQuantidadeProcedimentoAtendimento(Integer consultaNumero, Integer prdSeq, Byte quantidade,
			Boolean carga) throws BaseException, CloneNotSupportedException;

	public MamProcedimentoRealizado alterarCidProcedimentoAtendimento(MamProcedimentoRealizado procedimentoRealizado, String cidCodigo,
			Boolean carga) throws ApplicationBusinessException, CloneNotSupportedException;

	public Boolean existeConsultasAgendadas(AacGradeAgendamenConsultas grade);

	public void adicionarProcedimentoConsulta(AacConsultas consultaSelecionada, AacConsultaProcedHospitalar procedimentoConsulta,
			AghCid cid, Integer phiSeq, Short espSeq, Byte quantidade, List<AacConsultaProcedHospitalar> listaProcedimentosHospConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, final Boolean aack_prh_rn_v_apac_diaria,
			final Boolean aack_aaa_rn_v_protese_auditiva, final Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException;

	public List<AacConsultaProcedHospitalar> buscarConsultaProcedHospPorNumeroConsulta(Integer numeroConsulta);

	public MamSituacaoAtendimentos obterSituacaoAtendimentos(Short seq);

	public void removerProcedimentoConsulta(AacConsultaProcedHospitalar procedimentoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException;

	public List<VAacConvenioPlano> getListaConvenios(String parametro);

	public Long getListaConveniosCount(String parametro);

	public List<AacFormaAgendamento> listarFormasAgendamentos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AacPagador pagador, AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento);

	public Long listarFormasAgendamentosCount(AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento);

	public List<AacPagador> pesquisarPagadores(String filtro);

	public Long pesquisarPagadoresCount(String filtro);

	public List<AacTipoAgendamento> pesquisarTiposAgendamento(String filtro);

	public Long pesquisarTiposAgendamentoCount(String filtro);

	public List<AacCondicaoAtendimento> pesquisarCondicoesAtendimento(String filtro);

	public Long pesquisarCondicoesAtendimentoCount(String filtro);

	AacFormaAgendamento obterAacFormaAgendamentoPorChavePrimaria(AacFormaAgendamentoId id);

	AacFormaAgendamento obterAacFormaAgendamentoPorChavePrimaria(AacFormaAgendamentoId id, Enum[] innerJoins, Enum[] leftJois);

	public List<RelatorioAgendamentoConsultaVO> obterAgendamentoConsulta(Integer nroConsulta) throws ApplicationBusinessException;

	public void marcarFaltaPacientes(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala, DominioTurno turno,
			AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional, String nomeMicrocomputador)
			throws NumberFormatException, BaseException;
	public void marcarFaltaPaciente(Integer consultaNumero, String nomeMicrocomputador, boolean chegou,Integer codSituacaoAtend) 
			throws NumberFormatException, BaseException;
	
	public void salvarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException;

	public void alterarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException;

	Long obterQuantidadeAacNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento);

	public void removerFormaAgendamento(AacFormaAgendamentoId id) throws ApplicationBusinessException;

	AacNivelBusca obterAacNivelBuscaPorChavePrimaria(AacNivelBuscaId id);

	AacNivelBusca obterAacNivelBuscaPorChavePrimaria(AacNivelBuscaId id, Enum[] innerJoins, Enum[] leftJoins);

	public void salvarNivelBusca(AacNivelBusca nivelBusca) throws BaseException;

	public void alterarNivelBusca(AacNivelBusca nivelBusca) throws BaseException;

	public void salvarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException;

	public void alterarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException;

	public AacFormaEspecialidade obterAacFormaEspecialidadePorChavePrimaria(AacFormaEspecialidadeId id);

	AacFormaEspecialidade obterAacFormaEspecialidadePorChavePrimaria(AacFormaEspecialidadeId id, Enum[] innerJoins, Enum[] leftJoins);

	public void removerNivelBusca(AacNivelBuscaId id);

	public void removerFormaEspecialidade(AacFormaEspecialidadeId id) throws ApplicationBusinessException;

	public void clear();

	public Short buscaProximoSeqpAacNivelBusca(Short fagCaaSeq, Short fagTagSeq, Short fagPgdSeq);

	public List<AacConsultas> listarConsultasParaLiberar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer prontuarioPaciente, Integer codigoPaciente, Integer grade, AghEspecialidades especialidade, Date dataConsulta,
			String situacaoConsulta, Integer nroConsulta);

	public Long listarConsultasParaLiberarCount(Integer prontuarioPaciente, Integer codigoPaciente, Integer grade,
			AghEspecialidades especialidade, Date dataConsulta, String situacaoConsulta, Integer nroConsulta);

	public void liberarConsulta(Integer numeroConsulta, String nomeMicrocomputador, Boolean possuiReconsulta) throws BaseException;

	public List<AacUnidFuncionalSalas> listarSalasPorUnidadeFuncionalSalaTipoSituacao(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Short unidadeFuncional, Byte sala, DominioTipoUnidadeFuncionalSala tipo,
			DominioSituacao situacao);

	public Long listarSalasPorUnidadeFuncionalSalaTipoSituacaoCount(Short unidadeFuncional, Byte sala,
			DominioTipoUnidadeFuncionalSala tipo, DominioSituacao situacao);

	public void persistirZonaSala(AacUnidFuncionalSalas oldZonaSala, AacUnidFuncionalSalas newZonaSala, DominioOperacoesJournal operacao)
			throws BaseException;

	public void removerZonaSala(Short unfSeq, Byte sala) throws BaseException;

	public void verificarSituacaoAtendimento(Short seqAtendimento) throws ApplicationBusinessException;

	public AacCondicaoAtendimento obterCondicaoAtendimentoPorCodigo(Short fagCaaSeq);

	public AacTipoAgendamento obterTipoAgendamentoPorCodigo(Short fagTagSeq);

	public AacPagador obterPagadorPorCodigo(Short fagPgdSeq);

	public AtestadoVO obterDadosAtestado(Long seq);

	public List<DocumentosPacienteVO> obterListaDocumentosPaciente(Integer conNumero, Integer gradeSeq, Boolean verificarProcesso) throws ApplicationBusinessException;

	public List<DocumentosPacienteVO> obterListaDocumentosPacienteAnamneseEvolucao(Integer conNumero);

	// --[NOTAS ADICIONAIS]
	public MamNotaAdicionalEvolucoes inserirNotaAdicionalEvolucoes(String notaAdicional, AacConsultas consulta)
			throws ApplicationBusinessException;

	public List<MamNotaAdicionalEvolucoes> obterNotaAdicionalEvolucoesConsulta(Integer consultaNumero);

	public void excluiNotaAdicionalEvolucao(MamNotaAdicionalEvolucoes notaAdicional) throws ApplicationBusinessException;

	public MamNotaAdicionalAnamneses inserirNotaAdicionalAnamneses(String notaAdicional, AacConsultas consulta)
			throws ApplicationBusinessException;

	public List<MamNotaAdicionalAnamneses> obterNotaAdicionalAnamnesesConsulta(Integer consultaNumero);

	public void excluiNotaAdicionalAnamnese(MamNotaAdicionalAnamneses notaAdicional) throws ApplicationBusinessException;

	public List<VMamProcXCid> listarCidPorProcedimentoAtendimento(String parametro, Integer prdSeq);

	public Long listarCidPorProcedimentoAtendimentoCount(String parametro, Integer prdSeq);

	public List<ProcedimentoAtendimentoConsultaVO> listarProcedimentosAtendimento(Integer consultaNumero, Short espSeq, Short paiEspSeq,
			Boolean inicio) throws BaseException;

	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.</br>
	 * 
	 * <b>Para manter a compatibilidade com o que já havia sido implementado,
	 * foi mantido o método sem o parâmetro de tipo, este assume por default o
	 * CapitalizeEnum.PRIMEIRA</b>
	 * 
	 * @param descCid
	 */

	public String obterDescricaoCidCapitalizada(String descCid);

	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.
	 * 
	 * @param descCid
	 * @param tipo
	 * @return
	 */

	public String obterDescricaoCidCapitalizada(String descCid, CapitalizeEnum tipo);

	public String obtemDescricaoConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException;

	public String obtemDescricaoConsultaAtual(AacConsultas consulta) throws ApplicationBusinessException;

	public Integer getTinSeqEvolucao() throws ApplicationBusinessException;

	public Integer getTinSeqAnamnese() throws ApplicationBusinessException;

	public String getDescricaoItemAnamnese() throws ApplicationBusinessException;

	public String getDescricaoItemEvolucao() throws ApplicationBusinessException;

	public MamItemAnamneses primeiroItemAnamnesesPorAnamneses(Long anaSeq);

	List<MamItemAnamneses> pesquisarItemAnamnesesPorAnamneses(Long anaSeq);

	public MamItemEvolucoes primeiroItemEvolucoesPorEvolucao(Long evoSeq);

	List<MamItemEvolucoes> pesquisarItemEvolucoesPorEvolucao(Long evoSeq);

	public Boolean existeConsultaPacientesAgendados(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional);

	public void salvarAnamnese(String texto, AacConsultas consulta) throws ApplicationBusinessException;

	public void salvarEvolucao(String texto, AacConsultas consulta) throws ApplicationBusinessException;

	public MamEvolucoes obterEvolucaoAtivaPorNumeroConsulta(Integer conNumero);

	public void editarNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicional, String descricaoAdicional, AacConsultas consulta)
			throws ApplicationBusinessException;

	public void editarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicional, String descricaoAdicional, AacConsultas consulta)
			throws ApplicationBusinessException;

	public RelatorioAnamneseEvolucaoVO retornarRelatorioAnamneseEvolucao(Integer conNumero, DominioAnamneseEvolucao tipo)
			throws ApplicationBusinessException;
	
	public void excluiAnamese(AacConsultas consulta, MamAnamneses anamneseAtual) throws ApplicationBusinessException,
	ApplicationBusinessException;
	
	public void excluiEvolucao(AacConsultas consulta, MamEvolucoes evolucaoAtual) throws ApplicationBusinessException,
	ApplicationBusinessException;

	public MamAnamneses obterAnamneseAtivaPorNumeroConsulta(Integer conNumero);

	public MamReceituarios primeiroReceituarioPorConsultaETipo(Integer consultaNumero, DominioTipoReceituario tipo);

	public void executarOperacoesAposSelecionarProcedimento(ProcedimentoAtendimentoConsultaVO procedimentoVO, Integer consultaNumero)
			throws BaseException;

	public BigDecimal buscarSeqNenhumProcedimento() throws ApplicationBusinessException;

	public Boolean verificarNenhumProcedimento(Integer prdSeq, BigDecimal seqProcNenhum) throws ApplicationBusinessException;

	public void mampPend(Integer conNumero, Date dthrMvto, Short satSeq, String nomeMicrocomputador) throws ApplicationBusinessException;

	public Short buscaSituacaoPendencia(DominioMotivoPendencia situacao) throws ApplicationBusinessException;

	public Boolean existeDocumentosImprimirPaciente(AacConsultas consulta, Boolean verificarProcesso) throws ApplicationBusinessException;

	public void integraProcedimento(Integer seq) throws BaseException;

	public Boolean verificaUsuarioElaboraAnamnese() throws ApplicationBusinessException;

	public Boolean verificaUsuarioElaboraEvolucao() throws ApplicationBusinessException;

	public void atualizarIndImpressaoAnamnese(Long seqAnamnese);

	public void atualizarIndImpressaoEvolucao(Long seqEvolucao);

	public void atualizarIndImpressaoReceituario(Long seqReceita) throws ApplicationBusinessException;

	public void desatacharVMamProcXCid(VMamProcXCid vMamProcXCid);

	public List<GerarDiariasProntuariosSamisVO> pesquisarMapaDesarquivamento(Date dataDiaria) throws ApplicationBusinessException,
			ApplicationBusinessException;
	
	public void movimentarProntuariosParaDesarquivamento(Date dataDiaria, String usuarioLogado, Boolean exibeMsgProntuarioJaMovimentado) throws ApplicationBusinessException;

	public void inicioDiaria(Date dataDiaria) throws ApplicationBusinessException;

	public void fimDiaria(Date dataDiaria) throws ApplicationBusinessException;

	public List<AacRetornos> getListaRetornos(String objPesquisa);

	public List<MamItemExame> pesquisarItemExamePorDescricaoOuSeq(Object param, String ordem, Integer maxResults);

	public void atualizarRetornoConsulta(Integer numeroConsulta, AacRetornos retorno, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws NumberFormatException, BaseException;

	public VAacConvenioPlano obterConvenioPlanoPorId(Short cnvCodigo, Byte plano);

	public Long pesquisarItemExamePorDescricaoOuSeq(Object param);

	public List<MamItemMedicacao> listarTodosItensMedicacao(String ordem);

	public List<MamLaudoAih> obterLaudoAihPorTrgSeq(long trgSeq);

	public List<MamTrgEncInterno> obterTrgEncInternoPorConsulta(AacConsultas consulta);

	public Boolean validarProcessoConsultaAnamnese() throws ApplicationBusinessException;

	public Boolean validarProcessoConsultaEvolucao() throws ApplicationBusinessException;

	public Boolean validarProcessoConsultaProcedimento() throws ApplicationBusinessException;

	public Boolean validarProcessoConsultaReceita() throws ApplicationBusinessException;

	public Boolean validarProcessoExecutaAnamnese() throws ApplicationBusinessException;

	public Boolean validarProcessoExecutaEvolucao() throws ApplicationBusinessException;
	
	public Boolean validarProcessoConsultaAtestado() throws ApplicationBusinessException;

	public Boolean validarProcessoExecutaProcedimento() throws ApplicationBusinessException;

	public Boolean validarProcessoExecutaReceita() throws ApplicationBusinessException;

	public Boolean validarProcessoExecutaSolicitacaoExame() throws ApplicationBusinessException;

	public List<MamItemReceituario> atualizarReceituarioGeral(MamReceituarios receitaGeral, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException, CloneNotSupportedException;

	public List<MamItemReceituario> atualizarReceituarioEspecial(MamReceituarios receitaEspecial, AacConsultas consultaSelecionada,
			Integer viasEspecial, List<MamItemReceituario> itemReceitaEspecialList) throws BaseException, CloneNotSupportedException;

	public void excluirReceituarioEspecial(MamReceituarios receitaEspecial, AacConsultas consultaSelecionada) throws BaseException;

	public void excluirReceituarioGeral(MamReceituarios receitaGeral, AacConsultas consultaSelecionada) throws BaseException;

	public void excluirReceitaGeral(MamReceituarios receitaGeral, MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException, CloneNotSupportedException;

	public void excluirReceitaEspecial(MamReceituarios receitaEspecial, MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasEspecial, List<MamItemReceituario> itemReceitaEspecialList) throws BaseException, CloneNotSupportedException;

	public List<MamDiagnostico> buscarDiagnosticosAtivosPaciente(Integer codigo) throws ApplicationBusinessException;

	public List<MamDiagnostico> buscarDiagnosticosResolvidosPaciente(Integer codigo) throws ApplicationBusinessException;

	public void salvarListaDiganosticosPaciente(List<DiagnosticosPacienteVO> listaVO) throws ApplicationBusinessException;

	public void excluirDiagnosticosPorCidAtendimentos(AghAtendimentos atendimento) throws ApplicationBusinessException;

	public void validarInsercaoDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException;

	public void validarAtualizacaoDiagnostico(MamDiagnostico diagnostico, RapServidores rapServidores) throws ApplicationBusinessException;

	public MamDiagnostico inserirDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException;

	public void atualizarDiagnostico(MamDiagnostico diagnostico, MamDiagnostico diagnosticoOld) throws BaseException;

	public void atualizarConsulta(AacConsultas consulta) throws ApplicationBusinessException;

	public void desatacharItemReceituario(MamItemReceituario itemReceituario);

	public void atualizarInterconsultas(final Integer pacCodigo, final Character tipo);

	public AacConsultaProcedHospitalar inserirAacConsultaProcedHospitalar(final AacConsultaProcedHospitalar novo, String nomeMicrocomputador)
			throws BaseException;

	public void importarArquivo(String consulta, String nomeArquivo, Integer numeroLinha, StringBuilder msgErroProcedimento)
			throws ApplicationBusinessException;

	public void limparConsultasSisreg();

	public void tratarErrosImportacaoConsultas(String nomeArquivo, StringBuilder msgErroProcedimento) throws ApplicationBusinessException;

	public StringBuilder marcarConsultas(List<AacConsultasSisreg> consultasSisreg, Integer totalConsultas, String nomeMicrocomputador);

	public List<AacConsultasSisreg> obterConsultasSisreg();

	public File exportarLogSisreg(StringBuilder log);

	public boolean possuiConsultasPorGradeAgendamento(Integer grdSeq);

	public boolean possuiHorariosGradeConsultaPorGradeAgendamento(Integer grdSeq);

	public DominioSituacao verificarSituacaoGrade(Integer grdSeq, Date data);

	public String obterTextoAgendamentoConsulta(String hospitalLocal, String unidadeFuncional, String sala, AacConsultas consulta)
			throws ApplicationBusinessException;

	public List<AacCondicaoAtendimento> pesquisarCondicaoAtendimentoPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao, String filtroSigla, DominioConsultaGenerica filtroGenericaAmb,
			DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac, DominioSituacao filtroSituacaoCondicaoAtendimento);

	public Long countCondicaoAtendimentoPaginado(Short filtroSeq, String filtroDescricao, String filtroSigla,
			DominioConsultaGenerica filtroGenericaAmb, DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac,
			DominioSituacao filtroSituacaoCondicaoAtendimento);

	public void persistirCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException;

	public void atualizarCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException;

	public void removerCondicaoAtendimento(Short codigoCondicaoAtendimento) throws ApplicationBusinessException;

	public File geraArquivoConsulta(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno,
			FatProcedHospInternosVO procedimento)	throws IOException, ApplicationBusinessException;
	
	public List<RelatorioConsultasAgendaVO> recuperarInformacoesConsultaParaRelPDF(
			List<AacConsultas> consultas);
	
	List<AacConsultas> carregarArquivoRass(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException;

	public File geraArquivoRass(List<AacConsultas> consultas, Date filtroDtInicio, Date filtroDtFim,  Short filtroUslUnfSeq, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException;
	
	List<AacConsultas> carregarArquivoEsus(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException;
	
	public ArquivosEsusVO gerarArquivoEsus(List<AacConsultas> consultas, Date filtroDtInicio, Date filtroDtFim,  AghEspecialidades especialidade)
			throws IOException, ApplicationBusinessException, BaseException;
		
	public void verificarUnidadeCapsRetornoAtendido(Short filtroUslUnfSeq, AacRetornos filtroRetorno) throws ApplicationBusinessException;
	
	public void verificarAgendasEsus(AghEspecialidades filtroEspecialidade, AacRetornos filtroRetorno) throws ApplicationBusinessException;

	public DominioTurno defineTurno(Date data) throws ApplicationBusinessException, ApplicationBusinessException;

	public DominioTurno defineTurnoAtual() throws ApplicationBusinessException, ApplicationBusinessException;

	public Boolean verificaGradeTipoSisreg(Integer seq);

	public DataInicioFimVO definePeriodoTurno(DominioTurno turno) throws ApplicationBusinessException;

	public void processarProcedimentoConsultaPorRetorno(Integer consultaNumero, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, AacRetornos retorno,
			Boolean aack_prh_rn_v_apac_diaria, 
			Boolean aack_aaa_rn_v_protese_auditiva, 
			Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException;

	public List<DocumentosPacienteVO> obterListaDocumentosPacienteParaCertificacao(Integer conNumero) throws ApplicationBusinessException;

	public StringBuffer visualizaConsulta(Integer numero, Integer codigo) throws BaseException;

	public List<MamDiagnostico> listarDiagnosticosPorCidAtendimentoSeq(Integer ciaSeq);

	public MamDiagnostico obterDiagnosticoPorChavePrimaria(Long seq);

	public List<MamAltaSumario> pesquisarAltasSumariosParaAltasAmbulatoriais(Integer numeroConsulta);

	public MamAltaSumario obterMamAltaSumarioPorId(Long seq);

	public List<MamAltaDiagnosticos> procurarAltaDiagnosticosBySumarioAltaEIndSelecionado(MamAltaSumario altaSumario,
			DominioSimNao indSelecionado);

	public List<MamAltaEvolucoes> procurarAltaEvolucoesBySumarioAlta(MamAltaSumario altaSumario);

	public List<MamAltaPrescricoes> procurarAltaPrescricoesBySumarioAltaEIndSelecionado(MamAltaSumario altaSumario,
			DominioSimNao indSelecionado);

	public MamControles obterMamControlePorNumeroConsulta(Integer numeroConsulta);

	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(Long seqMamAltaSumario);

	public Long pesquisarAltaPrescricoesCount(Long seqMamAltaSumario);

	public List<AltaAmbulatorialPolEvolucaoVO> recuperarAltaAmbPolEvolucaoList(Long seqMamAltaSumario);

	public List<AltaAmbulatorialPolDiagnosticoVO> recuperarAltaAmbPolDiagnosticoList(Long seqMamAltaSumario);

	public List<AltaAmbulatorialPolPrescricaoVO> recuperarAltaAmbPolPrescricaoList(Long seqMamAltaSumario);

	public Boolean validarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pSeqProcesso) throws ApplicationBusinessException;

	public void inserirNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes registroInclusao) throws ApplicationBusinessException;

	public Boolean verificarExibicaoNodoAltasAmbulatoriais(Integer codPaciente);

	public Short retornarCodigoProcessoEvolucao() throws ApplicationBusinessException;

	public void atualizarMamNotaAdicionalEvolucao(MamNotaAdicionalEvolucoes notaAdicionalEvolucao);

	public String obterAssinaturaTexto(MamAnamneses anamnese, MamEvolucoes evolucao, MamNotaAdicionalAnamneses notaAdicionalAnamnese,
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao) throws ApplicationBusinessException;

	public String obterIdentificacaoResponsavel(MamAnamneses anamnese, MamEvolucoes evolucao) throws ApplicationBusinessException;

	public List<AacPagador> pesquisaPagadoresComAgendamento();

	public List<AacTipoAgendamento> pesquisaTipoAgendamentoComAgendamentoEPagador(AacPagador pagador);

	public List<AacCondicaoAtendimento> pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(AacPagador pagador, AacTipoAgendamento tipo);

	public AacFormaAgendamento findFormaAgendamento(AacPagador pagador, AacTipoAgendamento tipo, AacCondicaoAtendimento condicao);

	AacConsultas obterConsulta(Integer numero);

	String verificaSituacaoGrade(AacGradeAgendamenConsultas grade, Date data) throws ApplicationBusinessException;

	Boolean existeSolicitacaoExame(AacConsultas consulta);

	Boolean existeAtendimentoEmAndamento(AacConsultas consulta);

	void preAtualizar(AacConsultas consulta) throws BaseException;

	AacConsultas clonarConsulta(AacConsultas consulta) throws ApplicationBusinessException;

	List<AacProcedHospEspecialidades> listarProcedimentosEspecialidadesPorEspecialidade(Short espSeq);

	void persistirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp, DominioOperacoesJournal operacao) throws BaseException;

	Boolean validaGradeAgendamento(AacProcedHospEspecialidades procEsp) throws BaseException;

	void excluirProcedimentoEspecialidade(Short espSeq, Integer phiSeq);

	List<AacConsultasJn> listaConsultasJn(Integer numeroConsulta, String indSituacaoConsulta);

	List<Object[]> listarConsultasPacientesParaPassivarProntuario(Integer pacCodigo, Calendar dthr);

	List<AacConsultas> pesquisaConsultasPorPacienteDataConsulta(AipPacientes paciente, Date dataUltimaConsulta);

	List<AacMotivos> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short codigo, String descricao,
			DominioSituacao situacao);

	AacMotivos obterMotivoConsulta(Short aacMotivosCodigo);

	void persistirMotivoConsulta(AacMotivos aacMotivos) throws ApplicationBusinessException;

	Long pesquisaCount(Short codigoPesquisaMotivoConsulta, String descricaoPesquisaMotivoConsulta,
			DominioSituacao situacaoPesquisaMotivoConsulta);

	List<AacAtendimentoApacs> listarAtendimentosApacsPorCodigoPaciente(Integer pacCodigo);

	public void persistirAtendimentoApacs(AacAtendimentoApacs apac);

	AacCaracteristicaGrade obterCaracteristicaGradePorChavePrimaria(AacCaracteristicaGradeId chave);

	Long listarConsultasPorComNumeroDescricaoProcedimentoCount(Object objPesquisa);

	List<AacConsultaProcedHospitalar> listarConsultasPorComNumeroDescricaoProcedimento(Object objPesquisa);

	AacConsultaProcedHospitalar obterConsultaProcedHospitalar(AacConsultaProcedHospitalarId pk);

	Integer obterPhiSeqPorNumeroConsulta(Integer conNumero, Boolean indConsulta);
	
	AacConsultaProcedHospitalar obterConsultaProcedGHospPorNumeroEInd(Integer conNumero, Boolean indConsulta);

	List<Object[]> listarAtendimentosPacienteAmbulatorioPorCodigo(Integer pacCodigo, Date inicio, Date fim);

	List<FatConsultaPrhVO> buscarFatConsultaPrhVOAcompanhamento(Integer codPaciente, Long numeroAtm, Date dtInicio, Date dtFim);

	List<FatConsultaPrhVO> buscarFatConsultaPrhVO(Integer codPaciente, Long numeroAtm, Integer codigoTratamento, Date dtInicio, Date dtFim);

	List<Integer> obterNumeroDasConsultas(final Integer pacCodigo, final Date dtTransplante, final Date dtInicio, final Date dtFim,
			final String ttrCodigo, final DominioIndAbsenteismo absenteismo);

	List<AacConsultas> buscarConsultaPorProcedAmbRealizadoEspecialidade(Integer codPaciente, Date dtInicioCompetencia, Date dtInicio,
			Date dtFim, Short cnvCodigo, Integer codigoTratamento, Byte cpeMes, Short cpeAno);

	List<AacConsultas> buscarApacAssociacao(Integer codPaciente, Date dtInicio, Date dtFim, Short cnvCodigo, Integer codigoTratamento);

	List<Object[]> obterPacientes(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator);

	List<Object[]> obterPacConsultas(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator);

	List<AacConsultas> pesquisarAacConsultasPorCodigoEData(Integer pacCodigo, Date data);

	List<AacConsultas> executarCursorConsulta(Integer cConNumero) throws ApplicationBusinessException;

	List<AacConsultas> pesquisarConsultasPorPaciente(AipPacientes paciente);

	List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(Integer pacCodigo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	List<AacGradeAgendamenConsultas> executaCursorGetCboExame(Integer conNumero);

	List<AacGradeAgendamenConsultas> listarGradesAgendamentoConsultaPorEspecialidade(AghEspecialidades especialidade);

	List<AacMotivos> obterListaMotivosAtivos();

	AacPagador obterPagador(Short codigo);

	void persistirSisPrenatal(AacSisPrenatal elemento);

	void removerSisPrenatal(AacSisPrenatal elemento);

	List<AacSisPrenatal> pesquisarAacSisPrenatal(Integer pacCodigo);

	AacUnidFuncionalSalas obterUnidFuncionalSalasPeloId(Short unfSeq, Byte sala);

	List<MamAlergias> pesquisarMamAlergiasPorPaciente(Integer pacCodigo);

	List<MamAreaAtuacao> listarAreasAtuacaoPorCodigoLogradouroESituacao(Integer codigoLogradouro, DominioSituacao situacao);

	List<MamAreaAtuacao> listarAreasAtuacaoPorDescricaoLogradouroESituacao(String descricaoLogradouro, DominioSituacao situacao);

	MamAreaAtuacao obterAreaAtuacaoAtivaPorNomeLogradouro(String logradouro);

	MamAreaAtuacao obterAreaAtuacaoAtivaPorCodigoLogradouro(Integer codigoLogradouro);

	List<MamConcatenacao> pesquisarConcatenacaoAtivaPorIdQuestao(Integer qusQutSeq, Short qusSeq);

	List<MamAtestados> listarAtestadosPorCodigoPaciente(Integer pacCodigo);

	String getPrimeiraDescricaoLocal(Short seq);

	MamNotaAdicionalEvolucoes buscarNotaParaRelatorio(Integer seqNota);

	public MamNotaAdicionalEvolucoes obterNotaAdicionalEvolucoesPorChavePrimaria(Integer seq);

	public MamNotaAdicionalEvolucoes obterNotaAdicionalEvolucoesOriginal(Integer seq);

	List<MamProcedimento> pesquisarProcedimentosComProcedEspecialDiverso(Short seq);

	Boolean existeDadosImprimirRelatorioEvolucao(Integer atdSeq);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPip(Integer pipPacCodigo, Short pipSeqp);

	public void atualizarAnamnese(MamRespostaAnamneses anamnese);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPdp(Integer pdpPacCodigo, Short pdpSeqp);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPlp(Integer plpPacCodigo, Short plpSeqp);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigo(Integer pepPacCodigo);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigo(Integer atpPacCodigo);

	List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPip(Integer pipPacCodigo, Short pipSeqp);

	public void atualizarRespostaEvolucao(MamRespostaEvolucoes evolucao) throws ApplicationBusinessException;

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPdp(Integer pdpPacCodigo, Short pdpSeqp);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPlp(Integer plpPacCodigo, Short plpSeqp);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigo(Integer atpPacCodigo);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigo(Integer pepPacCodigo);

	List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm);

	List<MamTmpAlturas> listaTmpAlturasPorPacCodigo(Integer pacCodigo);

	public void atualizarTmpAltura(MamTmpAlturas altura);

	List<MamTmpPaDiastolicas> listaTmpPaDiastolicasPorPacCodigo(Integer pacCodigo);

	public void atualizarTmpPaDiastolicas(MamTmpPaDiastolicas diastolica);

	List<MamTmpPaSistolicas> listaTmpPaSistolicasPorPacCodigo(Integer pacCodigo);

	public void atualizarTmpPaSistolicas(MamTmpPaSistolicas sistolica);

	List<MamTmpPerimCefalicos> listaTmpPerimCefalicosPorPacCodigo(Integer pacCodigo);

	public void atualizarTmpPerimCefalicos(MamTmpPerimCefalicos cefalico);

	List<MamTmpPesos> listaTmpPesosPorPacCodigo(Integer pacCodigo);

	public void atualizarTmpPeso(MamTmpPesos peso);

	List<TriagemRealizadaEmergenciaVO> listarTriagemRealizadaEmergencia(Date vDtHrInicio, Date vDtHrFim);

	List<Object[]> listarAtendimentosPacienteTriagemPorCodigo(Integer pacCodigo);

	public void atualizarAltaSumerio(MamAltaSumario mamAltaSumario);

	List<MamAnamneses> listarAnamnesesPorCodigoPaciente(Integer pacCodigo);

	List<MamDiagnostico> pesquisarDiagnosticosPorPaciente(AipPacientes paciente);

	List<MamDiagnostico> listarDiagnosticoValidadoPorAtendimento(Integer atdSeq);

	List<MamDiagnostico> listarDiagnosticosPorFatRelDiagnosticoEPaciente(EpeFatRelDiagnosticoId id, Integer pacCodigo);

	List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer pacCodigo);

	List<MamEvolucoes> listarEvolucoesPorCodigoPaciente(Integer pacCodigo);

	List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(Integer codigoPaciente);

	void removerItemReceituario(MamItemReceituario itemReceituario) throws ApplicationBusinessException;

	List<Object[]> buscarConfiguracaoImpressaoItensReceituario(MamReceituarios receituarios);

	List<ItemReceitaMedicaVO> obterItemReceituarioPorImpressaoValidade(MamReceituarios receituario, Byte grupoImpressao, Byte validadeMeses);

	void versionarAltaReceituario(MpmAltaSumario altaSumarioNovo, Short antigoAsuSeqp) throws ApplicationBusinessException;

	void removerReceituario(MpmAltaSumario altaSumario) throws ApplicationBusinessException;

	void excluir(MamReceituarios receituarios) throws BaseException;

	void excluirReceituario(MamReceituarios receituarios) throws BaseException;

	void preValidar(MamItemReceituario item, String operacao) throws BaseException;

	void validaValidadeItemReceitaEmMeses(MamItemReceituario item) throws BaseException;

	void gravarMamReceituario(MamReceituarios receituario, List<MamItemReceituario> novos, List<MamItemReceituario> alterados,
			List<MamItemReceituario> excluidos) throws BaseException, CloneNotSupportedException;

	void inserirItem(MamReceituarios receituario, MamItemReceituario item) throws ApplicationBusinessException;

	void atualizarItem(MamItemReceituario item) throws ApplicationBusinessException;

	void excluirItem(MamItemReceituario item) throws ApplicationBusinessException;

	void gravar(MamReceituarios receituario, List<MamItemReceituario> novos) throws BaseException;

	MamReceituarios obterReceituarioPeloSeq(Long atdSeq);

	MamReceituarios obterMamReceituario(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, DominioTipoReceituario tipo);

	List<MamItemReceituario> buscarItensReceita(MpmAltaSumario altaSumario, DominioTipoReceituario tipo);

	List<MamItemReceituario> buscarItensReceita(MamReceituarios receituario);

	void inserir(MamReceituarios receituario) throws BaseException;

	List<MamItemReceituario> obterItensReceitaOrdenadoPorSeqp(MamReceituarios receituario);

	Boolean existeDadosImprimirRelatorioAnamneses(Integer atdSeq);

	public MamNotaAdicionalAnamneses obterNotaAdicionalAnamnesePorChavePrimaria(Integer seqAna);

	List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesPendenteExcNaoValidParaInternacao(Long numRegistro);

	List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesValidasSemPaiParaInternacao(Long numRegistro);

	List<MamNotaAdicionalEvolucoes> listarNotasAdicinaisEvolucoesPorCodigoPaciente(Integer pacCodigo);

	List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqNota, DominioTipoDocumento tipoDoc);

	List<AghVersaoDocumento> verificaImprime(Integer seqNota, DominioTipoDocumento tipoDoc);

	public List<MamNotaAdicionalEvolucoes> pesquisarNotasAdicionaisEvolucoesPorCodigoPaciente(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	public Long pesquisarNotasAdicionaisEvolucoesPorCodigoPacienteCount(Integer codigo);

	List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoPendenteExcNaoValidParaInternacao(Long numRegistro);

	List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoValidasSemPaiParaInternacao(Long numRegistro);

	List<MamReceituarios> listarReceituariosPorPaciente(AipPacientes paciente);

	List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoInternacao(Integer atdSeq, String tipoRelatorio, Date dataInical,
			Date dataFinal) throws ApplicationBusinessException;

	List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoEmergencia(Long trgSeq, Date dtHrValidaInicio, Date dtHrValidaFim,
			DominioGrupoProfissionalAnamnese grupoProssional, Integer conNumero, CseCategoriaProfissional categoriaProfissional)
			throws ApplicationBusinessException;

	public List<AacAtendimentoApacs> listarAtendimentoApacPorPtrPacCodigo(Integer ptrPacCodigo);
	
	List<AacAtendimentoApacs> obterDataInicioAtendimentoExistente(Integer pacCodigo, Short espSeq, DominioTipoTratamentoAtendimento indTipoTratamento);

	public void persistirAacAtendimentoApacs(AacAtendimentoApacs aacAtendimentoApacs);

	public List<SolicitanteVO> executaCursorSolicitante(Short unfSeq, Byte sala, Date dtConsulta) throws ApplicationBusinessException,
			ApplicationBusinessException;

	List<AacUnidFuncionalSalas> obterListaSalasPorUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, Object param);

	public List<AacUnidFuncionalSalas> obterListaSalasPeloNumeroSala(Object param);

	VAacSiglaUnfSala obterVAacSiglaUnfSalaPeloId(byte sala, short unfSeq);

	public List<MamReceituarios> buscarReceituariosPorAltaSumario(MpmAltaSumario altaSumario);

	List<MamReceituarios> pesquisarReceituariosPorConsulta(AacConsultas consulta);

	void removerReceituario(MamReceituarios receituario) throws ApplicationBusinessException;

	void atualizarReceituario(MamReceituarios receituario) throws ApplicationBusinessException;

	List<ReceitaMedicaVO> imprimirReceita(MpmAltaSumario altaSumario, boolean atualizar) throws ApplicationBusinessException, BaseException;

	List<ReceitaMedicaVO> imprimirReceitaPorSeq(Long receitaSeq, Boolean imprimiu) throws ApplicationBusinessException, BaseException;

	public List<MamReceituarios> buscarReceituariosPorAltaSumarioNaoAssinados(MpmAltaSumario altaSumario, MamReceituarios receita);

	Long pesquisarConsultaCountSituacaoConsulta(FiltroConsultaBloqueioConsultaVO filtroConsulta);

	List<AacSituacaoConsultas> pesquisarConsultaPaginadaSituacaoConsulta(FiltroParametrosPadraoConsultaVO filtroPadrao,
			FiltroConsultaBloqueioConsultaVO filtroConsulta);

	AacSituacaoConsultas obterSituacaoConsultaPeloId(String situacao);

	void persistirRegistroSituacaoConsulta(AacSituacaoConsultas registro)
			throws ApplicationBusinessException;

	void atualizarRegistroSituacaoConsulta(AacSituacaoConsultas registro) throws ApplicationBusinessException;

	public List<MamTrgEncInterno> obterPorConsultaOrderDesc(Integer numero);

	public List<MamEvolucoes> obterEvolucaoPorTriagemERegistro(Long trgSeq, Long rgtSeq);

	public List<MamAnamneses> obterAnamnesePorTriagemERegistro(Long trgSeq, Long rgtSeq);

	List<AacConsultas> verificarSePacienteTemConsulta(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo, Integer paramReteronoConsAgendada);

	String mpmcMinusculo(String pString, Integer pTipo);

	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo);

	List<Long> buscaSeqTipoItemEvolucao(final Integer atdSeq, final Date data, final Integer seqMTIE, final Integer catSeq);

	public String obterIdadeMesDias(Date dtNascimento, Date dtReferencia);

	public void validaCRMAmbulatorio(AghProfEspecialidades prof, AacGradeAgendamenConsultas grade) throws ApplicationBusinessException;

	public MamTipoEstadoPaciente obterEstadoAtual(Integer atdSeq, Date dataValidacao);

	public List<AacRetornos> getListaRetornosAtivos(String objPesquisa);

	List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullByConsulta(Integer conNumero, DominioIndPendenteDiagnosticos... indPendentes);

	public List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullAndAlsSeqNull(Integer numero,
			DominioIndPendenteDiagnosticos indPendenteDiag);

	Long recuperarAltaAmbuPolImpressaoVOCount(Long seqMamAltaSumario);

	public MamAnamneses atualizarAnamnese(MamAnamneses anamnese) throws ApplicationBusinessException;

	public MamEvolucoes atualizarEvolucao(MamEvolucoes evolucao) throws ApplicationBusinessException;

	public List<AacConsultas> listarConsultasPorCodigoPaciente(Integer pacCodigo);

	public List<MamEmgEspecialidades> listarTodasMamEmgEspecialidade();

	public Boolean verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(Integer conNumero);

	Long pesquisarDiagnosticosPorPacienteCount(AipPacientes paciente);

	Long pesquisarConsultasPorPacienteCount(AipPacientes paciente);

	AacConsultas obterAacConsulta(Integer numeroConsulta);

	public String formataString(String nome, int i);

	List<MamDiagnostico> listarDiagnosticosPorCirurgia(Integer crgSeq);

	MamDiagnostico obterDiagnosticoOriginal(Long seq);

	MamRegistro obterMamRegistroPorChavePrimaria(Long rgtSeq);

	public Boolean pacienteEmAtendimentoEmergenciaTerreo(Integer pacCodigo);

	public Boolean pacienteEmAtendimentoEmergenciaUltimosDias(Integer pacCodigo, Integer dias);

	public void existeGradeAgendamentoConsultaComEquipe(AghEquipes equipe) throws BaseException;

	public List<AacConsultas> obterConsultaAnamnesesPorDataConsPacEspIndPendente(final Date dataCorrente, final Integer paciente,
			final Short especiliada, final DominioIndPendenteAmbulatorio indPendente);

	public List<AacConsultas> obterConsultaEvolucoesPorDataConsPacEspIndPendente(final Date dataConsulta, final Integer paciente,
			final Short especiliada, final DominioIndPendenteAmbulatorio indPendente);

	public List<VFatSsmInternacaoVO> obterListaProcedimentosLaudoAih(Object param, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException;

	public Long obterListaProcedimentosLaudoAihCount(Object param, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException;

	List<AghCid> obterListaCidLaudoAih(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException;

	Long obterListaCidLaudoAihCount(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException;

	List<AghCid> obterListaCidSecundarioLaudoAih(Object param, AipPacientes paciente, String cidCodigo) throws ApplicationBusinessException;

	Long obterListaCidSecundarioLaudoAihCount(Object param, AipPacientes paciente, String cidCodigo) throws ApplicationBusinessException;

	void protegerLiberarRegistroLai(Long laiSeq, DominioIndPendenteLaudoAih indPendente, RapServidores servidorValida)
			throws ApplicationBusinessException;

	Boolean salvarMamLaudoAih(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException;

	Boolean atualizarMamLaudoAih(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException;

	List<MamLaudoAih> obterLaudoPorSeqEPaciente(Long seq, Integer pacCodigo);

	public List<MamLaudoAih> listarLaudosAIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente);

	public Long listarLaudosAIHCount(AipPacientes paciente);

	public MamLaudoAih obterMamLaudoAihPorChavePrimaria(Long seq);

	public void gravarMamLaudoAih(MamLaudoAih mamLaudoAih);

	public List<VFatSsmInternacaoVO> pesquisarInternacoesPacienteByLaudo(Long seqMamLaudoAih, Short paramTabelaFaturPadrao);

	MamLaudoAih obterLaudoAIHPorChavePrimaria(Long laudoAIHSeq);

	void atualizarConsultaVO(ConsultaAmbulatorioVO vo);

	void verificarEnderecoPaciente(Integer codPac) throws ApplicationBusinessException;

	boolean validarDadosPacienteAmbulatorio(Integer codigo);

	List<AacConsultas> pesquisarConsultasPorPacientePOL(AipPacientes paciente);

	AacGradeAgendamenConsultas obterGradesAgendaPorEspecialidadeDataLimiteParametros(Short espSeq, Short pPagadorSUS, Short pTagDemanda,
			Short pCondATEmerg, Date dataLimite);

	GradeAgendaVo obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(Short espSeq, 
			Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg , Date dataLimite) throws ApplicationBusinessException;

	Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero);
	
	AacSituacaoConsultas obterSituacaoMarcada();

	Long pesquisarConsultasPorEspecialidade(Short espSeq, List<Integer> consultasPacientesEmAtendimento);

	List<AacConsultas> obterConsultasPorNumero(List<Integer> numeros);

	List<ConsultaEspecialidadeAlteradaVO> obterConsultasEspecialidadeAlterada(Short espSeq, Integer grdSeq, Integer conNumero);

	List<GradeAgendamentoAmbulatorioVO> listarQuantidadeConsultasAmbulatorio(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual);

	List<GradeAgendamentoAmbulatorioServiceVO> listarQuantidadeConsultasAmbulatorioVO(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda,
			Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) throws ApplicationBusinessException;

	List<AacConsultas> pesquisarPorConsultaPorNumeroConsultaEspecialidade(List<Integer> conNumero, Short espSeq);

	AacConsultas obterAacConsultasJoinGradeEEspecialidade(final Integer numeroConsulta);

	Integer inserirConsultaEmergencia(Date dataConsulta, Integer gradeSeq, Integer pacCodigo, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, String nomeMicrocomputador) throws BaseException;

	Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio,
			String indSitConsulta, String nomeMicrocomputador) throws BaseException;
	
	/**
	 * Buscar consulta do paciente tem consulta no CO
	 * 
	 * Web Service #36972
	 * 
	 * @param pacCodigo
	 * @param unfSeq1
	 * @param unfSeq2
	 * @return
	 */
	List<Integer> pesquisarConsultaPorPacienteUnidadeFuncional(final Integer pacCodigo, final Short unfSeq1, final Short unfSeq2);

	/**
	 * Buscar a data da consulta anterior a consulta atual, sendo informado o
	 * código do paciente e o sequencial da gestação.
	 * 
	 * Web Service #37687
	 * 
	 * @param pacCodigo
	 * @param conNumero
	 * @param gsoSeqp
	 * @return
	 */
	List<Date> pesquisarPorPacienteConsultaGestacao(Integer pacCodigo, Integer conNumero, Short gsoSeqp);

	/**
	 * Obter laudos aih de uma determinada consulta
	 * 
	 * Web Service #38473
	 * 
	 * @param conNumero
	 * @param pacCodigo
	 * @return
	 */
	List<MamLaudoAih> pesquisarLaudosAihPorConsultaPaciente(final Integer conNumero, final Integer pacCodigo);

	List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(Integer codigoPac, Integer atdSeq);

	List<Long> buscaSeqTipoItemEvolucaoPrescEnf(final Integer atdSeq, final Date data, final Integer seqMTIE, final Integer catSeq);

	Boolean verificarAtendimentoHCPA();

	AacRetornos obterRetornoConsultaPeloId(Integer seq);

	void persistirRetornoConsulta(AacRetornos retornoConsutla, DominioSimNao dominioSimNao, DominioSituacao situacao,
			DominioIndAbsenteismo dominioIndAbsenteismo) throws ApplicationBusinessException;

	List<AacRetornos> pesquisarConsultaPaginadaRetornoConsulta(FiltroParametrosPadraoConsultaVO filtroPadrao,
			FiltroConsultaRetornoConsultaVO filtroConsulta);

	void atualizarRetornoConsulta(AacRetornos retornoConsutla, DominioSituacao situacao, DominioIndAbsenteismo dominioIndAbsenteismo)
			throws ApplicationBusinessException;

	Long pesquisarConsultaCountRetornoConsulta(FiltroConsultaRetornoConsultaVO filtroConsulta);

	void removerRegistroRetornoConsulta(Integer seq) throws ApplicationBusinessException;

	// Inicio manterEspecialidadePMPA
	void excluirEspecialidadePmpa(AacEspecialidadePmpa aacEspecialidadePmpa) throws ApplicationBusinessException;

	void persistirEspecialidadePmpa(AacEspecialidadePmpa aacEspecialidadePmpa) throws BaseException;

	public Long countEspecialidadePmpaPaginado(Short codigoEspecialidadePmpa, Long seqEspecialidadePmpa);

	public List<AacEspecialidadePmpa> listarEspecialidadePmpaPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seqEspecialidadePmpa, Long codigoEspecialidadePmpa);

	public AacEspecialidadePmpa obterAacEspecialidadePmpaPorChavePrimaria(Short seq, Long codigo);

	// Fim manterEspecialidadePMPA

	// Início manterOrgaoPagador
	void atualizarPagador(AacPagador aacPagador) throws ApplicationBusinessException;

	void excluirPagador(Short aacPagadorCodigo) throws ApplicationBusinessException;

	void persistirPagador(AacPagador aacPagador) throws ApplicationBusinessException;

	public Long countPagadorPaginado(Short codigoOrgaoPagador, String descricaoOrgaoPagador, DominioSituacao situacaoOrgaoPagador,
			DominioGrupoConvenio convenioOrgaoPagador);

	public List<AacPagador> pesquisarPagadorPaginado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigoOrgaoPagador, String descricaoOrgaoPagador, DominioSituacao situacaoOrgaoPagador,
			DominioGrupoConvenio convenioOrgaoPagador);

	void persistirTipoAgendamento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException;

	void atualizarTipoAgendamento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException;

	void removerTipoAgendamento(Short tipoAgendamento) throws ApplicationBusinessException;

	List<AacTipoAgendamento> pesquisarTipoAgendamentoPaginado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigoTipoAutorizacao, String descricaoTipoAutorizacao, DominioSituacao situacaoTipoAutorizacao);

	Long countTipoAgendamentoPaginado(Short codigoTipoAutorizacao, String descricaoTipoAutorizacao, DominioSituacao situacaoTipoAutorizacao);

	// Fim manterOrgaoPagador

	List<Integer> buscaSeqTipoItemEvolucaoPorCategoria(Integer seq);

	List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(Integer codigoPac, Integer atdSeq,
			List<DominioOrigemAtendimento> origensInternacao, List<DominioOrigemAtendimento> origensAmbulatorio);

	void atualizarMotivoConsulta(AacMotivos aacMotivos) throws ApplicationBusinessException;

	void removerMotivoConsulta(Short accMotivoConsultaCodigo) throws ApplicationBusinessException;
	
	public void verificarEPersistirTipoItemAnamneses(MamTipoItemAnamneses tipoItemAnamnese) throws BaseException;
	
	public List<DiagnosticoEtiologiaVO> listarAtualizarMamDignosticos(Integer atdSeq);

	public List<AghAtendimentos> pesquisarAtendimentoParaAltaAmbulatorial(

			Integer codigo, Integer atdSeq);

	public MamAltaSumario pesquisarAltasSumariosPorNumeroConsulta(Integer numeroConsulta);

	public List<MamAltaDiagnosticos> pesquisarAltaDiagnosticosPorSumarioAlta(Long altaSumarioSeq);

	public MamAltaSumario persistirMamAltaSumario(AipPacientes paciente,

			AghAtendimentos atendimento, String loginUsuarioLogado)throws ApplicationBusinessException;

	public void persistirMamAltaEvolucoes(MamAltaEvolucoes evolucao);

	public void persistirMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico) throws ApplicationBusinessException;
	
	public void removerMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico);

	public void persistirMamAltaPrescricoes(MamAltaPrescricoes prescricao);

	public void removerMamAltaPrescricoes(MamAltaPrescricoes prescricao);

	public void desbloquearMamAltaSumario(MamAltaSumario altaSumario);

	public Long pesquisarListaCategoriaProfissionalCount(Object filtro);
	
	public List<CseCategoriaProfissional> pesquisarListaCategoriaProfissional(Object filtro);
	
	// Início ManterTipoSolicitacaooProcedimentos #12160
	public List<MamTipoSolProcedimento> pesquisarTipoSolicitacaoProcedimentosPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MamTipoSolProcedimento tipoSolicitacaoProcedimento);

	public MamTipoSolProcedimento obterTipoSolicitacaoProcedimentoPorCodigo(Short codigoTipoSolicitacaoProcedimento);
	
	public void excluirTipoSolicitacaoProcedimentos(Short seq) throws ApplicationBusinessException;
	
	public void persistirTipoSolicitacaoProcedimentos(MamTipoSolProcedimento tipoSolicitacaoProcedimento) throws BaseException;
	// Início ManterItensAnamnese
	public List<MamTipoItemAnamneses> pesquisarListaTipoItemAnamnesesPorCategoriaProfissional(
			Integer seqCategoriaProfissional);

	public Long pesquisarListaTipoItemAnamnesesPorCategoriaProfissionalCount(
			Integer seq);
	// Fim ManterItensAnamnese

	public Long countTipoSolicitacaoProcedimentosPaginado(
			MamTipoSolProcedimento tipoSolicitacaoProcedimento);
	
	AacConsultas inserirConsulta(AacConsultas consulta,
			String nomeMicrocomputador, Boolean substituirProntuario,
			Boolean aack_prh_rn_v_apac_diaria,
			Boolean aack_aaa_rn_v_protese_auditiva,
			Boolean fatk_cap_rn_v_cap_encerramento)
			throws NumberFormatException, BaseException;
	
	Short obtemCodigoEspecialidadeGradePeloNumeroConsulta(Integer conNumero);
	
	List<Integer> pesquisarConsultasPorEspecialidade(Short espSeq);
	
	AacConsultas atualizarConsulta(AacConsultas consultaAnterior, AacConsultas consulta, Boolean marcacaoConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario) throws NumberFormatException, BaseException;
	
	Integer obterAtendimentoPorConNumero(Integer conNumero);

	/**
	 * Realiza a liberação de consultas por óbito.
	 * 
	 * ORADB Procedure AGHP_ATU_PAC_OBITO.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @param nomeMicrocomputador - Nome do micro que está realizando a operação
	 * @throws BaseException 
	 */
	void liberarConsultaPorObito(Integer codigoPaciente, String nomeMicrocomputador) throws BaseException;

	public List<AacConsultas> pesquisarConsultasAnterioresPacienteByEspecialidade(Integer consultaAtual, Integer codPaciente, Date dtInicio, Date dtFim, Short espSeq, Integer retSeq);
	
	public Integer obterAtdSeqPorNumeroConsulta(Integer numeroConsulta);
	
	public void atualizarConsultaRetorno(AacConsultas consulta) throws ApplicationBusinessException;
	
	public List<AipPacientes> listarConsultasParaLiberarObito(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes aipPacientes);

	public Long listarConsultasParaLiberarObitoCount(
			AipPacientes aipPacientes);
	
	public List<ConsultasDeOutrosConveniosVO> pesquisarConsultasDeOutrosConvenios(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Date mesAno) throws ApplicationBusinessException;

	public Long pesquisarConsultasDeOutrosConveniosCount(Date mesAno) throws ApplicationBusinessException;

	MamTipoItemEvolucao tipoItemEvolucaoPorSeq(Integer seq);

	void salvarItenEvolucao(MamTipoItemEvolucao tipoItemEvolucao)
			throws BaseException, ApplicationBusinessException;

	List<MamTipoItemEvolucao> pesquisarListaTipoItemEvoulucaoPorCategoriaProfissional(
			Integer seq);

	boolean validarAlteracaoCamposInalteraveis(
			MamTipoItemEvolucao tipoItemEvolucao)
			throws ApplicationBusinessException;
	
	void excluirAtestadoAcompanhamento(MamAtestados atestado) throws ApplicationBusinessException;
	
	void excluirAtestadoFgts(MamAtestados atestado) throws ApplicationBusinessException;
	
	void validarDatasAtestado(Date dataInicial, Date dataFinal) throws ApplicationBusinessException;
	
	MamTipoAtestado obterTipoAtestadoPorSeq(Short seq);
	
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeEspecialidade(Object parametro);
	
	public Long pesquisarPorSiglaOuNomeEspecialidadeCount(Object parametro);
	
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeGestaoInterconsultas(Object parametro);

	public Long pesquisarPorSiglaOuNomeGestaoInterconsultasCount(Object parametro);
	
	public List<MamInterconsultas> listaInterconsultas(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos);
	
	public Long listaInterconsultasCount(MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos);
	
	public void excluirInterconsultas(MamInterconsultas excluirInterconsultas) throws ApplicationBusinessException;

	public void avisarInterconsultas(MamInterconsultas interconsultas, String foiAvisado) throws ApplicationBusinessException;
		
	public void validarDatas(Date dataInicial, Date dataFinal) throws ApplicationBusinessException;
	
	public void gravarInterconsultas(MamInterconsultas mamInterconsultas) throws ApplicationBusinessException;
	
	/**
	 * Consulta as unidades funcionais por sigla ou descrição e filtra por característica zona_ambulatorio.
	 * @param parametro Valor para sigla ou descrição.
	 * @return Lista com unidades funcionais filtradas.
	 */
	public List<AghUnidadesFuncionais> obterSetorPorSiglaDescricaoECaracteristica(Object pesquisa);
	
	/**
	 * Consulta os dados para a construção do relatório de Agenda de Consultas
	 * @param dataInicio 
	 * @param dataFim
	 * @param seqGrade Sequencial da Grade de Agendamento de Consultas
	 * @param seqEspecialidade Sequencial da Especialidade
	 * @param seqUnidadeFuncional Sequencial da Unidade Funcional
	 * @param turno Valor referente ao turno
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws BaseListException
	 */
	public List<CabecalhoRelatorioAgendaConsultasVO> carregarRelatorioAgendaConsultas(Date dataInicio, Date dataFim, Integer seqGrade, Short seqEspecialidade, Short seqUnidadeFuncional, 
			DominioTurno turno) throws ApplicationBusinessException, BaseListException;
	
	public List<AacRetornos> obterTodosRetornosRelatorioAgenda(Integer retornoMaximo);
	
	public void salvarPagadores(ConverterConsultasVO novaConsulta, Integer numero);
	
	public void salvarInterconsulta(MamInterconsultas parametroSelecionado);
	
	/**
	 * Monta o arquivo CSV do relatório de agenda de consultas
	 * @param listaCabecalho lista com os dados para preenchimento do arquivo CSV
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws IOException
	 */
	public String gerarCSVAgendaConsultas(List<CabecalhoRelatorioAgendaConsultasVO> listaCabecalho) throws ApplicationBusinessException, IOException;
	
	public AacGradeAgendamenConsultas copiarGradeAgendamento(AacGradeAgendamenConsultas gradeAgendamenConsultaCopia, AacGradeAgendamenConsultas gradeAgendamenConsultasOriginal)
			throws ApplicationBusinessException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException;

	public AacGradeAgendamenConsultas clonarAgendamenConsultaCopia(AacGradeAgendamenConsultas gradeAgendamenConsultas) throws IllegalAccessException, InvocationTargetException;

	public void validarHorariosAgendados(AacGradeAgendamenConsultas gradeAgendamenConsultas) throws ApplicationBusinessException;

	public void validarHorariosSobrepostos(AacGradeAgendamenConsultas gradeAgendamenConsultas) throws ApplicationBusinessException;
	
	List<ConverterConsultasVO> obterConsultaPagadoresCadastrados(String sbPagadores);
	
	Long obterConsultaPagadoresCadastradosCount(String sbPagadores);
	
	/**
	 * Realiza a busca por consultas pendentes.
	 * 
	 * @param usuario - Usuário logado
	 * @param data - Data pesquisada
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 * @param turno - Turno da consulta
	 * @param paramSeqSituacaoAtendimento - Valor do parâmetro P_SEQ_SIT_EM_ATEND
	 * @param paramDiasReabrirPendente - Valor do parâmetro P_DIAS_REABRIR_PENDENTE
	 * 
	 * @return Lista de consultas pendentes
	 */
	public List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentes(RapServidores usuario, Date data, AghEspecialidades especialidade, AghEquipes equipe,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno, Short paramSeqSituacaoAtendimento,
			Integer paramDiasReabrirPendente);
	
	List<MamConsultorAmbulatorioVO> pesquisarConsultorAmbulatorioPorServidor(RapServidores servidor);
	
	void persistirConsultorAmbulatorio(MamConsultorAmbulatorio mamConsultorAmbulatorio) throws ApplicationBusinessException;
	
	MamConsultorAmbulatorio obterMamConsultorAmbulatorioPorId(Integer seq);

	public List<ConsultaAmbulatorioVO> consultaAbaPacientesAusentes(Date dtPesquisa, Short zonaUnfSeq, VAacSiglaUnfSala zonaSala, AghEspecialidades especialidade,
			AghEquipes equipe, RapServidores profissional)	throws ApplicationBusinessException;

	AghAtendimentos obterAtendimentoPorSeq(Integer seq);
	
	public void persistirControleImpressaoLaudo(ConsultaAmbulatorioVO consulta) throws ApplicationBusinessException;
	
	public String obterNomeResponsavelMarcacaoConsulta(Integer consulta);
	
	void atualizarAacConsultas(Integer conNumero, Integer pacCodigo, Short cspCnvCodigo, Short cspSeq, String stcSituacao, Integer retSeq, Short caaSeq, 
			Short tagSeq, Short pgdSeq, String nomeMicrocomputador) throws BaseException;
	
	List<MamAnamneses> pesquisarAnamnesesVerificarPrescricao(int dias, final Integer prontuario);

    public Long geraRegistroDeAtendimentoVersao2(Integer atdSeq,
                                                 String pIndPesqPend,
                                                 String pTipoPendPesq,
                                                 String pIndPedeSituacao,
                                                 DominioSituacaoRegistro pSituacaoGerar)  throws ApplicationBusinessException;

	
	public List<VAacConvenioPlano> pesquisarCovenioPlanoSGB(String pesquisa);
	
	public Long pesquisarCovenioPlanoSGBCount(String pesquisa);
	
	/**
	 *42801
	 *
	 *Consulta que carrega a lista de declarações para impressao
	 *
	 * @param tipoImpressao
	 * @return lista vo
	 */
	public List<RelatorioControleFrequenciaVO> pesquisarControleFrequencia(
			Boolean tipoImpressaoCF, Boolean tipoImpressaoLS);

	public String obterDataLocalFormula(Integer nroConsulta);

	public String obterMesAnoAtual(Integer nroConsulta);

	public AipPacientes obterPacientePorProntuario(Integer nroProntuario);

	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracao(Integer numeroConsulta);
	
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracaoConsulta(Integer numeroConsulta);
	
	List<Integer> obterGradesComHorariosDisponiveis(Short espSeq, Date dataInicio, Date dataFim);
	
	List<AgendamentoAmbulatorioVO> obterHorariosDisponiveisAmbulatorio(Short espSeq, Integer grdSeq, Date dataInicio, Date dataFim);

	public List<AacGradeAgendamenConsultas> listarGradeUnidFuncionalECaracteristicas(Integer numeroConsulta);
	
	RelatorioSolicitacaoProcedimentoVO obterInformacoesRelatorioSolicProcedimento(Integer conNumero);
	
	RelatorioSolicitacaoProcedimentoVO obterDadosRelatorioSolicProcedimento(Long seq);
	
	List<ConselhoProfissionalServidorVO> obterRegistroMedico(Integer vMatricula, Short vVinculo) throws ApplicationBusinessException;
		
	public void atualizarIndImpressaoInterconsultas(Long seq);
	
	public RelatorioConsultoriaAmbulatorialVO obterDadosConsultariaAmbulatorial(Long seq, Short espSeq) throws ApplicationBusinessException;

	Long consultarCMCEpaciente(final Integer pacCodigo,final Integer conNumero);
	
	MamRelatorioVO obterMamRelatorioVOPorSeq(Long seq);
	
	void atualizarIndImpressoRelatorioMedico(Long seq);
	
	String obterEspecialidade(Integer matricula, Short vinCodigo);

	void atualizarIndImpressaoLaudoAIH(Long seq);
	
	void atualizarIndImpressaoAltaAmb(Long seq);
	
	void atualizarIndImpressoSolicitacaoProcedimento(Long seq);
	
	public String obterCodigoFormulaPaciente(Integer nroProntuario);

	public Integer getAghAtendimentosParametroVOQueryBuilder(Integer codConsulta);
	
	public List<ParametrosAghEspecialidadesAtestadoVO> getAghEspecialidadesParametroVOQueryBuilder(Integer codConsulta);
	
	public List<ParametrosAghPerfilProcessoVO> getAghPerfilProcessoParametroVOQueryBuilder(RapServidores usuarioLogado);
	
	public List<CseProcessos> getAghCseUsuarioParametroVOQueryBuilder(RapServidores usuarioLogado, Integer codConsulta);

	public AacConsultas obterAgendamentoConsultaPorFiltros(
			Integer consultaNumero, Short unidadeNumero);

	public RelatorioAgendamentoConsultaVO popularTicketAgendamentoConsulta(
			AacConsultas consulta) throws ApplicationBusinessException;

	public RapServidores pesquisaServidorCseUsuarioPorServidor(RapServidores servidor);


	public List<AacConsultasJn> pesquisarUsuariosMarcadorConsulta(
			Integer numeroConsulta);
	
	public List<RelatoriosInterconsultasVO> carregarRelatorioInterconsultas(Date dataInicial, Date dataFinal, 
			DominioSituacaoInterconsultasPesquisa situacaoFiltro, String ordenar, AghEspecialidades agenda);
	
	public List<RelatoriosInterconsultasVO> carregarRelatorioPacientesInterconsultas(Date dataInicial, Date dataFinal, AghEspecialidades agenda);
    
	AipPacientes obterPacienteFull(Integer pacCodigo);
	
	AipGrupoFamiliarPacientes obterProntuarioFamiliaPaciente(Integer pacCodigo);
	
	Long obterFamiliaresVinculadosCount(AipPacientes pacienteContexto);
	
	Long obterProntuariosSugeridosVinculadosCount(AipPacientes paciente,AipEnderecosPacientes endereco);
	
	Long obterProntuariosSugeridosNaoVinculadosCount(AipPacientes paciente,AipEnderecosPacientes endereco);
	
	List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc,AipPacientes paciente,AipEnderecosPacientes endereco);
	
	public Date obterDataConsultaPorNumero(Integer numeroConsulta);

	List<VAacSiglaUnfSalaVO> pesquisarListaSetorSala(String pesquisa) throws ApplicationBusinessException;
	Long pesquisarListaSetorSalaCount(String pesquisa) throws ApplicationBusinessException;
	List<AghEquipes> pesquisarEquipeAtiva(String pesquisa);
	List<GradeVO> obterAacGradeAgendamentoConsultas(FiltroGradeConsultasVO filtro);
	List<GradeConsultasVO> pesquisarConsultasPorGrade(Integer grade, FiltroGradeConsultasVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	Long pesquisarConsultasPorGradeCount(Integer grade, FiltroGradeConsultasVO filtro);
	void trocarConsultaGrade(Integer oldGrade, Integer newGrade, List<GradeConsultasVO> listaConsultasSelecionadas) throws ApplicationBusinessException;
	List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosNaoVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc,AipPacientes paciente,AipEnderecosPacientes endereco);
	 
	List<PacienteGrupoFamiliarVO> obterFamiliaresVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, AipPacientes pacienteContexto);
	
	void desvincularPacienteGrupoFamiliar(Integer pacCodigo) throws ApplicationBusinessException;
	
	void vincularPacienteGrupoFamiliar(Integer pacCodigo,Integer agfSeq) throws ApplicationBusinessException;
	
	void vincularAmbosPacienteGrupoFamiliar(Integer pacCodigoContexto,Integer pacCodigoSugerido, Integer prontuarioInformado) throws ApplicationBusinessException;
	
	void atualizarPacientePesquisadoGrupoFamiliar(Integer pacientePesquisado,Integer agfSeq) throws ApplicationBusinessException;
	

	Integer trocarSolicitacoes(Integer oldAtdSeq, Integer newAtdSeq);

	List<TransferirExamesVO> obterDeConsulta(Integer numero);

	void cancelarAtestadosAltaSumario(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) throws ApplicationBusinessException;
	
	List<MamAtestados> listarAtestadosPorPacienteTipo(Integer atdSeq, Short tasSeq,MpmAltaSumario mpmAltaSumario);
	
	MamAtestados obterAtestadoEAghCidPorSeq(Long seq);
	
	
	List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp,
			Short tasSeq, Object[] situacoes);
	
	List<VAacConvenioPlano> obterListaConvenios();
	
	List<TransferirExamesVO> obterParaConsultas(Integer numero, Integer codigoPaciente);
	
	List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp);
	
	List<MamAtestados> obterAtestadosPendentes(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq);
	
	public Long obterCMCE(Integer nroConsulta);
	
	public MamTipoAtestado obterMamTipoAtestadoPorChavePrimaria(Short seq);

	public Long contarPesquisarConsultasPorGrade(ConsultasGradeVO filtro);

	MamAtestados obterMamAtestadoPorChavePrimaria(Long atsSeq);

	List<AacCondicaoAtendimento> obterListaCondicaoAtendimento(String parametro) throws ApplicationBusinessException;
	Long obterListaCondicaoAtendimentoCount(String parametro) throws ApplicationBusinessException;
	List<AacPagador> obterListaPagadores(String filtro);
	Long obterListaPagadoresCount(String filtro);
	List<AacTipoAgendamento> obterListaTiposAgendamento(String filtro);
	Long obterListaTiposAgendamentoCount(String filtro);

	List<RelatorioGuiaAtendimentoUnimedVO> imprimirGuiaAtendimentoUnimed(Integer conNumero) throws ApplicationBusinessException;
	
	List<EspecialidadeDisponivelVO> obterListaEspecialidadesDisponiveis(FiltroEspecialidadeDisponivelVO filtro,Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc);
	Long obterListaEspecialidadesDisponiveisCount(FiltroEspecialidadeDisponivelVO filtro);
	void validarCamposPreenchidos(FiltroEspecialidadeDisponivelVO filtro) throws ApplicationBusinessException;
	List<BigDecimal> obterSomatorioQuantidade(FiltroEspecialidadeDisponivelVO filtro);
	//#43342
	public List<FatProcedHospInternosVO> listarFatProcedHospInternosPorEspOuEspGrad(Integer grdSeq, AghEspecialidades especialidade, String parametro);
	//#43342
	public Long listarFatProcedHospInternosPorEspOuEspGradCount(Integer grdSeq, AghEspecialidades especialidade, String parametro);
	
	public String obterEspecialidade(Integer conNumero);
	//#47786
	public String obterEspecialidadePorConsulta(Integer conNumero);
	
	//48207
    public MptPrescricaoPaciente obterVinculoMatriculaResponsavel(Integer atdSeq, Integer seq);
	
	String obterTextoAgendamentoConsulta(String hospitalLocal,
			String unidadeFuncional, String sala, AacConsultas consulta,
			boolean flag) throws ApplicationBusinessException;
	
	
	public List<AghEspecialidades> pesquisarAgendaInterconsulta(String parametro, AacConsultas consulta, Integer idadePaciente) throws BaseException;
	
    public Long pesquisarAgendaInterconsultaCount(String parametro, AacConsultas consulta, Integer idadePaciente);
	
	public List<EquipeVO> pesquisarEquipeInterconsulta(String parametro) throws BaseException;
	
    public Long pesquisarEquipeInterconsultaCount(String parametro);

	public List<RapServidoresVO> pesquisarServidorInterconsulta(String parametro) throws BaseException;
		
    public Long pesquisarServidorInterconsultaCount(String parametro);
	
	public Integer obterCodigoPacienteOrigem(Integer pOrigem,  Integer conNumero);
	
	public Date obterDataNascimentoAnterior(Integer codigoPaciente);
	
	public Date obterDtPrevisaoInterconsulta(Short espSeq, Short caaSeq) throws ApplicationBusinessException;
		
	void inserirSolicitacaoInterconsulta(List<SolicitaInterconsultaVO> solicitaInterconsultaVO, Integer numConsulta) throws ApplicationBusinessException, ParseException;
	
	Boolean verificarInterconsulta(SolicitaInterconsultaVO interconsultaVO) throws ApplicationBusinessException;
	
	List<MamAtestados> obterAtestadosPorAtdSeqTipo(Integer atdSeq, Short tasSeq);
	
	List<MamAtestados> obterAtestadosPorSumarioAltaTipo(Integer apaAtdSeq, Integer apaSeq, Short seqP,  Short tasSeq);
	
	public void desatacharConsulta(AacConsultas consulta);

	public Long listarDisponibilidadeHorariosCount(Integer seq,
			Short unfSeq, AghEspecialidades especialidade, AghEquipes equipe,
			RapServidores profissional, AacPagador pagador,
			AacTipoAgendamento autorizacao, AacCondicaoAtendimento condicao,
			Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana diaSemana, Boolean disponibilidade,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO definePeriodoTurno,
			List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS);

	public void verificarExisteConsultaMesmoDiaTurno(AacConsultas consulta) throws ApplicationBusinessException;
	
	public AacGradeAgendamenConsultas obterGradeAgendamentoParaMarcacaoConsultaEmergencia(Integer seq);
	
	List<MamEvolucoes> pesquisarMamEvolucoesPaciente(Integer numero);

	List<MamTipoItemEvolucao> pesquisarTipoItemEvolucaoBotoes(Integer cagSeq);

	List<MamItemEvolucoes> pesquisarItemEvolucaoPorEvolucaoTipoItem(Long evoSeq, Integer tieSeq);
	
	Boolean verificaColar() throws ApplicationBusinessException;

	public StringBuilder obterEmergenciaVisTriagemCon(Long pTrgSeq, String pModoVis) throws ApplicationBusinessException;
	
	boolean reabrirConsulta(PesquisarConsultasPendentesVO consultaPendenteVO,String nomeMicrocomputador)
			throws ApplicationBusinessException;

	void chamaPortal(PesquisarConsultasPendentesVO consultaPendenteVO,
			String pHostname) throws ApplicationBusinessException, BaseException;
	
	Long obterApacNumero(Integer numeroConsulta);
	FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Integer codigoPaciente) throws ApplicationBusinessException;
	public List<ProfissionalHospitalVO> obterListaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	public Long obterCountProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho);

	VFatAssociacaoProcedimento obterCodigoTabelaEDescricao(Integer phiSeq,Short iphPhoSeq, Short convenioSus, Byte planoSus, Short cpgGrcSeq);
	CidVO pesquisarCodigoDescricaoCidPorAghParametro(Integer conNumero,String[] parametros);
	FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Long ps7, Long ps8, Integer codigoPaciente);
	AacConsultas obternomeEspecialidadeDataConsulta(final Integer numeroConsulta);
	FatConvGrupoItemProced obterCodigoTabelaDescricaoPorPhiSeq(Integer phiSeq);
	CidVO pesquisarJustificativaFoto(Integer conNumero);
	MamSolicitacaoRetorno obterMamSolicitacaoRetornoPorChavePrimaria(Long seq);
	void atualizarIndImpressaoSolicitacaoRetorno(Long seq);
	String mamcTicketReceita(Integer pSorSeq, String pOrigem);

	Boolean verificarInterconsultaAux(SolicitaInterconsultaVO interconsultaVO) throws ApplicationBusinessException;

	List<TransferirExamesVO> obterSolicitacoesExames(Integer numero);

	List<ConsultasGradeVO> pesquisarConsultasPorGrade(ConsultasGradeVO filtro, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc);
	
	String visualizarConsultaAtual(Integer conNumero) throws ApplicationBusinessException;
	
	TipoProcedHospitalarInternoVO verificaIdadePaciente(Integer codigoPaciente, Date dataFim) throws ApplicationBusinessException;
	
	/**
	 * #11942
	 * @param atestados
	 * @throws ApplicationBusinessException
	 */
	void gravarAtestado(MamAtestados atestados) throws ApplicationBusinessException;
	
	/**
	 * #11942
	 * @param atestado
	 */
	 void excluirAtestadoComparecimento(MamAtestados atestado);
	 
	/**
	 * #11942
	 * @param consulta
	 */
	 void acaoFinalizarAtendimento(AacConsultas consulta);
	 
	/**
	 * #11942
	 * @param consulta
	 */
	 void acaoCancelarAtendimento(AacConsultas consulta);
	 
	void gravarEvolucao(Integer conNumero, Long pEvoSeq,List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException;
	
	void gravarOkEvolucao(Integer conNumero, Long pEvoSeq,List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException;
	
	void excluirEvolucao(Integer conNumero, Long pEvoSeq, EvolucaoVO botaoSelecionado) throws ApplicationBusinessException;

	void gravarMotivoPendente(Integer conNumero, String motivoPendencia,
			String nomeMicrocomputador, Long evoSeq) throws ApplicationBusinessException;

	 List<AacPagador> listarPagadoresAtivos();
	 
	 /**
	  * #11946
	  */
	 public void persistirMamAtestadoAmbulatorio(MamAtestados atestado) throws ApplicationBusinessException;	
	 List<MamAtestados> listarAtestadosPorPacienteTipoAtendimento(Integer consulta, Short tasSeq);		
	 void excluirAtestadoFgtsPis(MamAtestados atestado) throws ApplicationBusinessException;
	 
	 /**
	  * #11942
	  * @param atestado
	  * @throws ApplicationBusinessException
	  */
	 void validarCamposPreenchidosAtestadoComparecimento(MamAtestados atestado) throws ApplicationBusinessException;
	 
	 MamTipoAtestado obterTipoAtestadoOriginal(Short seqTipo);
	 
	void validarHoraInicioFimAtestado(Date horaInicio, Date horaFim) throws ApplicationBusinessException;
	 
	BigDecimal obterParametroAtestadoAcompanhamento() throws ApplicationBusinessException;
	
	List<String> validarCamposAtestadoAcompanhamento(MamAtestados atestado);
	
	BigDecimal obterParametroAtestadoAtestadoMedico() throws ApplicationBusinessException;
	
	//#1944
	void validarCamposPreenchidosAtestadoMedico(MamAtestados atestado) throws ApplicationBusinessException;
	
	BigDecimal obterParametroAtestadoComparecimento() throws ApplicationBusinessException;
	
	//C1 #11942 #11943 
	List<MamAtestados> obterAtestadosDaConsulta(AacConsultas consulta, short tasSeq);
	
	//c1 #11944
	List<MamAtestados> obterAtestadosDaConsultaComCid(AacConsultas consulta, short tasSeq);
	
	List<ReceitasGeralEspecialVO> gerarDados(Integer pacCodigo);
	
	/**
	 * #52025 - Consultas utilizadas em PACKAGE BODY MAMK_FUNCAO_EDICAO 
	 */
	public DominioCor obterCurCorPacPorCodigo(Integer codigo);
	
	/**
	 * @ORADB MAMC_RESPONDEU_CUST
	 * 
	 * @param tipo
	 * @param qutSeq
	 * @param seqp
	 * @param chave
	 */
	Boolean verificarCustomizacaoRespondida(Character tipo, Integer qutSeq, Short seqp, Long chave);
	
	public DominioGrauInstrucao obterCurGrauPacPorCodigo(Integer codigo);
	
	public String obterCurNacionalidadePorCodigo(Integer codigo);
	
	public String obterCurCidadePorCodigo(Integer codigo);
	
	public String obterCurProfissaoPorCodigo(Integer codigo);
	
	public DominioSexo obterCurSexoPacPorCodigo(Integer codigo);
	
	public String obterNomePacientePorCodigoPac(Integer codigo);
	
	public CursorPacVO obterCurPacPorCodigo(Integer codigo);
	
	public String obterCurPacNomePaiPorCodigo(Integer codigo);
	
	public String obterCurPacNomeMaePorCodigo(Integer codigo);
	
	public List<AipEnderecoPacienteVO> obterAipEnderecoVOPorCodigo(Integer codigo);
	
	public Integer obterCddCodigoPorCodigo(Integer codigo);
	
	public String obterNomeAipCidadesPorCodigo(Integer codigo);	
	
	public String obterCurFuePorSeq(Short seq);
	
	VFatAssociacaoProcedimento obterDescricaoPorCodTabela(Long codTabela);
	List<AghEspecialidades> obterEspecialidadesPorSiglaOUNomeEspecialidade(String parametro);
	
	BigDecimal obterParametroRenovacaoReceita() throws ApplicationBusinessException;
	
	void validarCamposPreenchidosRenovacaoReceita(MamAtestados atestado) throws ApplicationBusinessException;
	
	public List<TDataVO> pGeraDadosData(Integer pPacCodigo);
	
	public List<TDataVO> pGerarDadosEspecialidade(Integer pPacCodigo);
	
	Long obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(String parametro);
	
	List<VRapServidorConselho> obterVRapServidorConselhoPorNumConselhoOuNome(String parametro);
	
	Long obterVRapServidorConselhoPorNumConselhoOuNomeCount(String parametro);
	
	List<FccCentroCustos> obterCentroCustoPorCodigoDescricao(String parametro);
		
	Long obterCentroCustoPorCodigoDescricaoCount(String parametro);
	
	List<AghEquipes> obterEquipesPorSeqOuNome(String parametro);
	
	Long obterEquipesPorSeqOuNomeCount(String parametro);
	
	List<RelatorioProgramacaoGradeVO> obterRelatorioProgramacaoGrade(Date dataInicio, Date dataFim, Integer grade, String sigla, Integer seqEquipe, Integer servico, VRapServidorConselho conselho);

	String obterCSVProgramacaoGrade(Date dataInicio, Date dataFim, Integer grade, String sigla, Integer seqEquipe, Integer servico, VRapServidorConselho conselho) throws IOException, ApplicationBusinessException;
	
	LaudoSolicitacaoAutorizacaoProcedAmbVO obterCidOtorrinoNumeroConsulta(Integer numeroConsulta);

	LaudoSolicitacaoAutorizacaoProcedAmbVO obterNomeCPFProfissResponsavel(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO, Integer numeroConsulta);

	LaudoSolicitacaoAutorizacaoProcedAmbVO preencheCamposdaJustificativaDoProcedSolicitado(Integer numeroConsulta) throws ApplicationBusinessException;
	
	public List<MamLembrete> obterResumoDeCaso(Integer numero);
	
	Boolean validarValorMinimo(MamAtestados atestado) throws ApplicationBusinessException;

	Boolean validarValorMinimoPeriodo(MamAtestados atestado) throws ApplicationBusinessException;
	

	List<VMamReceitas> obterListaVMamReceitas(Integer pacCodigo, Integer conNumero, Integer atdSeq, Date dtCriacao, DominioTipoReceituario dominioTipoReceituario);
	
	List<MamReceituarios> obterListaSeqTipoMamReceituarios(Integer atdSeq, Integer apaAtdSeq, Integer apaSeq, Integer seqp, Long trgSeq, Long rgtSeq, Integer conNumero,
			DominioTipoReceituario dominioTipoReceituario);
	
	Short obterValorMaxSeqP(Long rctSeq);
	
	void gravarReceitas(Integer conNumero, VMamReceitas registro, MamReceituarios receituario,
			DominioTipoReceituario dominioTipoReceituario) throws ApplicationBusinessException;
	
	AacConsultas buscaConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException;
	
	AipPacientes obterPacienteOriginal(Integer codigo);
	
	VRapServidorConselho obterRapServidorConselhoOriginal(VRapServidorConselhoId id);
	
	Integer obterUltimaConsultaGestantePorPaciente(Integer pacCodigo);
	
	public Boolean verificarConsultaJaMarcada(AacConsultas consulta);
	public List<AacConsultas> verificarConsultaPossuiReconsultasVinculadas(Integer numeroConsulta);
	
	
	MamValorValidoQuestao obterSeqValorSituacaoMamValValidoQuestao(Integer qusQutSeq, Short qusSeqP);
	
	EspecialidadeRelacionadaVO obterDadosEspecialidadesRelacionadoAConsulta(Integer conNumero);
	
	List<PreGeraItemQuestVO> pPreGeraItemQuest(Long pEvoSeq, Short espSeq, Integer pTieSeq, Integer pPacCodigo, String indTipoPac);
	
	String mamCGetTipoPac(Integer conNumero);
	
	GeraEvolucaoVO geraEvolucao(Integer pConNumero, Long pParameterEvoSeq) throws ApplicationBusinessException;
	
	String mamcExecFncEdicao(Short fueSeq, Integer codigo);
	
	BigDecimal obterParametroAtestadoMarcacao() throws ApplicationBusinessException;
	
	void validarValorMinimoNumeroVias(MamAtestados atestado) throws ApplicationBusinessException;

	/**
	 * #50743 - C1 - Consulta que retorna a anamnese relacionada à consulta do paciente.
	 */
	List<MamAnamneses> pesquisarMamAnamnesesPaciente(Integer numero);

	/**
	 * #50743 - C2 - Consulta que retorna os botões que serão apresentados para o perfil do usuário logado. 
	 * 				 Cada botão corresponde a um item de anamnese.
	 */
	List<MamTipoItemAnamneses> pesquisarTipoItemAnamneseBotoes(Integer cagSeq);

	/**
	 * #50743 - C3 - Consulta que retorna cada item da anamnese, para cada tipo.
	 */
	List<MamItemAnamneses> pesquisarItemAnamnesePorAnamneseTipoItem(Long anaSeq, Integer seq);

	/**
	 * #50745 - Ação dos botões Gravar e Ok da tela de Anamnese do Paciente
	 */
	void gravarAnamnese(Integer conNumero, Long anaSeq, List<AnamneseVO> listaBotoes) throws ApplicationBusinessException;

	/**
	 * #50745 - Ação do botão Excluir da tela de Anamnese do Paciente 
	 */
	void excluirAnamnese(Integer conNumero, Long anaSeq) throws ApplicationBusinessException;
	
	/**
	 * #50745 - Ação do botão Pendente da tela de Anamnese do Paciente
	 */
	void gravarAnamneseMotivoPendente(Integer conNumero, Long anaSeq, String motivoPendencia, String nomeMicrocomputador) throws ApplicationBusinessException;

	MamReceituarioCuidado mamReceituarioCuidadoPorNumeroConsulta(
			Integer numeroConsulta, DominioIndPendenteAmbulatorio pendente);

	List<MamItemReceitCuidado> listarMamItensReceituarioCuidadoPorNumeroConsulta(
			Integer numeroConsulta);

	MamReceituarioCuidado verificaRequisitosReceituarioCuidado(AacConsultas consulta);

	void adicionarEditarMamItemReceitCuidado(
			MamReceituarioCuidado receituarioAtual,
			MamReceituarioCuidado receituarioAnterior,
			MamItemReceitCuidado itemNovo,
			AacConsultas consulta);

	void excluirmamItemReceitCuidado(MamItemReceitCuidado item);

	MamReceituarioCuidado obterUltimoMamReceituarioCuidadoPorNumeroConsulta(
			Integer numeroConsulta);

	void procedimentosReceituarioCuidadoFinalizarAtendimento(
			Integer numeroConsulta,MamReceituarioCuidado receituarioAtenrior);

	void procedimentosReceituarioCuidadoCancelarAtendimento(
			Integer numeroConsulta,MamReceituarioCuidado anterior);// 

	List<MamRecCuidPreferidoVO> listarCuidadosPreferidos(
			RapServidores servidor, boolean ativo);

	void selecionarCuidadosEntrePreferidosUsuario(
			List<MamRecCuidPreferidoVO> listaMamRecCuidPreferido,
			MamReceituarioCuidado receituarioAtual, AacConsultas consulta);

	void copiaCuidadoPreferidosOutroUsuario(
			VMamDiferCuidServidores servidorOrigem, RapServidores servidorLogado);

	List<VMamDiferCuidServidores> pesquisarVMamDiferCuidServidores(RapServidores servidorLogado,
			String nomeMatriculaVincodigo);

	Long countVMamDiferCuidServidores(RapServidores servidorLogado,String nomeMatriculaVincodigo);

	VMamPessoaServidores obterVMamPessoaServidores(
			RapServidores servidores);
	List<PreGeraItemQuestVO> obterListaPreGeraItemQuestVO(Long evoSeq, Integer tieSeq, String indTipoPac);
	
	void excluirRespostaEItemEvolucao(Long evoSeq);

	/**
	 * Busca os Atendimentos por paciente.
	 * 
	 */
	List<AghAtendimentos> localizarAtendimentoPorPaciente(Integer codigoPaciente);
	
	/**
	 * Consulta C1 da estória #50937
	 * 
	 * @param atendimentoSeq
	 * @param criadoEm
	 * @return
	 */
	List<MamRegistro> obterRegistroAnamnesePorAtendSeqCriadoEm(Integer atendimentoSeq, Date criadoEm);
	
	/**
	 * Consulta C2 da estória #50937
	 * 
	 * @param atendimentoSeq
	 * @param criadoEm
	 * @return
	 */
	List<MamRegistro> obterRegistroEvolucoesPorAtendSeqCriadoEm(Integer atendimentoSeq, Date criadoEm);
	
	/**
	 * @ORADB mamk_int_visualiza.mamp_int_v_reg_ana
	 * @return
	 */
	String obterAnamnesePorMamRegistroSeq(Long rgtSeq, Integer categoriaProfSeq) throws ApplicationBusinessException;
	
	/**
	 * Obter categoria por sequencial
	 * #50931
	 * @return
	 */
	CseCategoriaProfissional obterCategoriaPorSeq(Integer seqCat);
	
	/**
	 * Obter os diagnósticos do prontuário do paciente
	 * #50931 - C5
	 * @param prontuario
	 * @param dataFiltro
	 * @return
	 */
	List<MamDiagnostico> listarDiagnosticosPortalPacienteInternado(Integer pacCodigo, Date dataFiltro);
	
	/**
	 * Realiza exclusão de diagnóstico
	 * #50931
	 * @param mamDiagnostico
	 */
	void excluirDiagnostico(MamDiagnostico mamDiagnostico) throws ApplicationBusinessException;

	public void obterListaDocumentosPacienteAtestados(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException;
	
	public void obterListaReceituarioCuidado(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Short espSeq, Boolean verificarProcesso) throws ApplicationBusinessException;

	MamTipoItemEvolucao obterMamTipoItemEvolucaoPorChavePrimaria(Integer seq);

	MamTipoItemAnamneses obterMamTipoItemAnamnesesPorChavePrimaria(Integer seq);

	void gerarMovimentacaoProntuario(AacConsultas consulta, RapServidores servidorLogado,
			Boolean exibeMsgProntuarioJaMovimentado) throws ApplicationBusinessException;

	List<MamLaudoAih> obterLaudosDoPaciente(Integer pacCodigo);
	
	public AghAtendimentos buscarAtendimentoPossuiMesmoLeitoUnidFuncional(AghAtendimentos atendimento,AghUnidadesFuncionais unidadeFuncional);

}