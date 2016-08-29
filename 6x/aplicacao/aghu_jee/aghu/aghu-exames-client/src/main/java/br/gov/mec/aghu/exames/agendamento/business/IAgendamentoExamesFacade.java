package br.gov.mec.aghu.exames.agendamento.business;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioAgendaUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAgenda;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesGrupoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeHorarioExtraVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelExaAgendPacVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExaAgendPac;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.model.VAelUnfExecutaExamesId;
import br.gov.mec.aghu.model.cups.ImpImpressora;

public interface IAgendamentoExamesFacade extends Serializable {
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExameOriginal(final Integer soeSeq, final Short seqp);
	
	public List<Short> obterListaSeqUnFExamesAgendaveis(Integer soeSeq);
	
	public void inserirItemHorarioAgendado(
			AelItemHorarioAgendado itemHorarioAgendado, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, 
			Boolean permiteHoraExtra, Boolean agendaExameMesmoHorario,
			Boolean agendamentoSingular, String sigla, Integer materialAnalise,
			Short unfExecutora, String nomeMicrocomputador) throws BaseException;
	
	public void inserirItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, String nomeMicrocomputador) throws BaseException;
	
	public String obterSugestaoAgendamentoPorPaciente(AipPacientes paciente, Boolean isAmbulatorio);
	
	public String obterSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio);
	
	public List<Date> pesquisarSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio);
	
	public List<ItemHorarioAgendadoVO> pesquisarAgendamentoPacientePorDatas(Integer pacCodigo, Date data1, Date data2);
	
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricao(Object parametro);

	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, DominioSituacaoHorario filtroSituacao,
			Date filtroDtInicio, Date filtroDtFim,
			Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana,
			Date filtroHora, AelTipoMarcacaoExame filtroTipoMarcacao,
			Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq,
			Integer gaeSeqp);

	public Long pesquisarHorarioExameDisponibilidadeCount(
			DominioSituacaoHorario filtroSituacao, Date filtroDtInicio,
			Date filtroDtFim, Boolean filtroHorariosFuturos,
			DominioDiaSemana filtroDiaSemana, Date filtroHora,
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra,
			Boolean filtroExclusivo, Short gaeUnfSeq, Integer gaeSeqp);

	

	public List<GradeExameVO> pesquisarGradeExame(String orderProperty,
			boolean asc, Integer seq, AghUnidadesFuncionais unidadeExecutora,
			DominioSituacao situacao, AelSalasExecutorasExames sala,
			AelGrupoExames grupoExame, VAelUnfExecutaExames exame,
			RapServidores responsavel);

	public Long pesquisarGradeExameCount(Integer seq,
			AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao,
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame,
			VAelUnfExecutaExames exame, RapServidores responsavel);

	public AelHorarioExameDisp refreshHorarioExameDisp(
			AelHorarioExameDisp horarioExameDisp);

	public void refreshTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame);

	public List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePaciente(
			Integer codSolicitacao, Short unfSeq);

	public AelGradeAgendaExame obterGradeExamePorChavePrimaria(
			AelGradeAgendaExameId id);

	public Long obterCountHorarioExameDispTipoMarcacaoAtivaPorGrade(
			Short unfSeq, Integer seqp);

	public Boolean verificarSituacaoHorarioIndisponivel(
			AelHorarioExameDisp horarioExameDisp);

	public void atualizarListaHorarioExameDisp(
			List<AelHorarioExameDisp> listaHorarioExameDisp,
			DominioSituacaoHorario situacaoHorario,
			AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra,
			Boolean exclusivo) throws BaseException;

	public void inserirHorarioExameDisp(DominioSituacaoHorario situacaoHorario,
			AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra,
			Boolean exclusivo, Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda)
			throws BaseException;

	public Boolean removerListaHorarioExameDisp(
			List<AelHorarioExameDisp> listaHorarioExame)
			throws ApplicationBusinessException;

	public List<VAelHrGradeDispVO> pesquisarHorariosLivresParaExame(
			String sigla, Integer matExame, Short unfExame,
			Date dataReativacao, Integer soeSeq, Short seqp, Date data,
			Date hora, Integer grade, AelGrupoExames grupoExame,
			AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor)
			throws ApplicationBusinessException,
			ParseException;

	public List<AelHorarioGradeExame> pesquisaHorariosPorGrade(
			AelGradeAgendaExame grade);

	public void excluirHorarioGrade(AelHorarioGradeExame horarioGrade);

	public void inserirHorarioGradeExame(AelHorarioGradeExame horarioGrade)
			throws ApplicationBusinessException;

	public void atualizarHorarioGradeExame(AelHorarioGradeExame horarioGrade)
			throws ApplicationBusinessException;

	public void removerGradeAgendaExame(AelGradeAgendaExame grade)
			throws ApplicationBusinessException;

	public void persistirGradeAgendaExame(AelGradeAgendaExame grade)
			throws ApplicationBusinessException;

	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short tipoMarcacaoExameSeq,
			String tipoMarcacaoExameDescricao,
			DominioSituacao tipoMarcacaoExameSituacao);

	public Long pesquisarTipoMarcacaoExameCount(Short tipoMarcacaoExameSeq,
			String tipoMarcacaoExameDescricao,
			DominioSituacao tipoMarcacaoExameSituacao);

	public void excluirTipoMarcacaoExame(Short seq) throws ApplicationBusinessException;

	public void persistirTipoMarcacaoExame(
			AelTipoMarcacaoExame tipoMarcacaoExameNew)
			throws ApplicationBusinessException;

	public AelTipoMarcacaoExame obterTipoMarcacaoExamePorSeq(Short seq);

	public void verificarDelecaoTipoMarcacaoExame(Date dataCriadoEm)
			throws ApplicationBusinessException;

	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidadeExcetoMarcadoExecutado(
			DominioSituacaoHorario filtroSituacao, Date filtroDtInicio,
			Date filtroDtFim, Boolean filtroHorariosFuturos,
			DominioDiaSemana filtroDiaSemana, Date filtroHora,
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra,
			Boolean filtroExclusivo, Short gaeUnfSeq, Integer gaeSeqp);

	public List<RelatorioAgendaPorSalaVO> obterAgendasPorSala(
			AghUnidadesFuncionais unidadeExecutora, Date dtAgenda,
			AelSalasExecutorasExames sala, Boolean impHorariosLivres,
			Boolean impEtiquetas, Boolean impTickets);

	public Integer gerarDisponibilidadeHorarios(AelGradeAgendaExame grade,
			Date dataInicio, Date dataFim, Date dataHoraUltimaGrade)
			throws BaseException;

	public VAelUnfExecutaExames obterVAelUnfExecutaExamesPorId(
			VAelUnfExecutaExamesId id);

	public AelUnfExecutaExames obterAelUnfExecutaExamesDAOPorId(
			AelUnfExecutaExamesId id);

	public List<Integer> obterSolicitacoesExame(
			List<RelatorioAgendaPorSalaVO> colecao);

	public List<AelSolicitacaoExames> obterSolicitacoesExamePorSeq(
			List<RelatorioAgendaPorSalaVO> colecao);

	public void validarExamesAgendamentoSelecao(VAelSolicAtendsVO exameVO,
			AipPacientes paciente) throws ApplicationBusinessException;

	public VAelSolicAtendsVO obterVAelSolicAtendsPorSoeSeq(Integer soeSeq)
			throws ApplicationBusinessException;

	public List<AgendamentoExameVO> permiteAgendarExames(List<AgendamentoExameVO> exames, final String login, String labelAgendar)
			throws ApplicationBusinessException;

	public List<AgendamentoExameVO>  verificarExamesNaoSelecComMesmaAmostra(List<AgendamentoExameVO> listaItensExame, AghParametros parametro, String label) throws ApplicationBusinessException;
	
	public List<AelGrupoExames> pesquisarGrupoExame(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AelGrupoExames grupoExame);

	public Long pesquisarGrupoExameCount(AelGrupoExames grupoExame);

	AelGrupoExames obterAelGrupoExamePeloId(final Integer seq);
	AelGrupoExames obterAelGrupoExamePeloId(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	public void excluirGrupoExame(Integer seq) throws ApplicationBusinessException;

	public void inserirGrupoExame(AelGrupoExames grupoExame)
			throws ApplicationBusinessException;

	public void alterarGrupoExame(AelGrupoExames grupoExame)
			throws ApplicationBusinessException;

	public List<ExamesGrupoExameVO> buscarListaExamesGrupoExameVOPorCodigoGrupoExame(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigoGrupoExame);
	
	public Long countBuscarListaExamesGrupoExameVOPorCodigoGrupoExame(Integer codigo);

	public void inserirGrupoExameUnidExame(
			AelGrupoExameUnidExame grupoExameUnidExame)
			throws ApplicationBusinessException;

	public void excluirGrupoExameUnidExame(AelGrupoExameUnidExameId id) throws ApplicationBusinessException;

	public void alterarGrupoExameUnidExame(AelGrupoExameUnidExame grupoExameUnidExame) throws ApplicationBusinessException;

	public AelGrupoExameUnidExame obterGrupoExameUnidExamePorId(
			AelGrupoExameUnidExameId id);

	public List<AgendamentoExameVO> obterExamesParaAgendamento(
			VAelSolicAtendsVO filtro, Short unidadeExecutoraSeq)
			throws ApplicationBusinessException;

	//TODO: DiegoPacheco --> retirar o trecho de código abaixo 
	// qdo a estória #5494 que chama esta estória estiver implementada
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameAtendimentoPacientePorSoeSeqSeqp(
			final Integer soeSeq, final List<Short> listaSeqp);

	public List<VAelSolicAtendsVO> obterSolicitacoesExame(
			VAelSolicAtendsVO filtro) throws ApplicationBusinessException;

	public List<AgendamentoExameVO> obterExamesSelecionados(List<AgendamentoExameVO> exames);

	public void verificaImpressaoTicketExame(
			List<AgendamentoExameVO> examesAgendamentoSelecao)
			throws ApplicationBusinessException;

	public void cancelarItemHorarioAgendadoMarcado(
			AelItemHorarioAgendadoId itemHorarioAgendadoId, Short globalUnfSeq, String nomeMicrocomputador)
			throws BaseException;

	public void cancelarItemHorarioAgendadoMarcadoPorSelecaoExames(
			List<AgendamentoExameVO> examesAgendamentoSelecao,
			Short globalUnfSeq, String nomeMicrocomputador) throws BaseException;
	
	public AelItemHorarioAgendado obterItemHorarioAgendadoPorId(
			AelItemHorarioAgendadoId itemHorarioAgendadoId);
			
	public List<GradeHorarioExtraVO> pesquisarGradeHorarioExtra(Object parametro, Short grade, String sigla, Integer matExame, Short unfExame);
	
	public Long obterExamesAgendamentosPaciente(Integer pacCodigo, AelUnfExecutaExamesId unfExecutaExamesId);
	
	public List<VAelExaAgendPacVO> obterExamesAgendadosDoPaciente(Integer pacCodigo, String siglaExame, Integer matSeqExame, Short unfSeqExame);
	
	public List<VAelExaAgendPac> obterInformacoesExamesAgendadosPaciente(Integer pacCodigo, String exaSigla, Integer manSeq, Short unfSeq, 
			Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda);
	
	public Date obterDataHoraDisponivelParaGradeEUnidadeExecutora(Short unfExecutora, Integer grade, Date dataHora);
	
	public AelHorarioExameDisp obterHorarioExameDisp(Date dthrAgenda, Short gaeUnfSeq, Integer gaeSeqp);

    public AelHorarioExameDisp obterHorarioExameDisponivel(Date dthrAgenda, Short gaeUnfSeq, Integer gaeSeqp);
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Date hedDthrAgenda);
	
	public Short obterTipoMarcacao(DominioOrigemAtendimento origem, Short unfSeq) throws ApplicationBusinessException;
	
	public List<RelatorioAgendaPorUnidadeVO> obterAgendasPorUnidade(AghUnidadesFuncionais unidadeExecutora, Date dtInicio,
			Date dtFim, DominioOrigemPacienteAgenda origem, DominioOrdenacaoRelatorioAgendaUnidade ordenacao) throws ApplicationBusinessException;

	public List<Integer> obterSolicitacoesExameUnidade(List<RelatorioAgendaPorUnidadeVO> colecao);
	
	public void verificarHorarioEscolhido(AelItemHorarioAgendado itemHorarioAgendado, Short seqp, Boolean permiteHoraExtra, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException;
	
	public void identificarAgendamentoExamesEmGrupo(Integer grupoExameSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			Boolean horarioExtra);
	
	public Date gravarHorarioExtra(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, GradeHorarioExtraVO gradeHorarioExtraVO, 
			Short unfExecutora, Date dataHora, AelTipoMarcacaoExame tipoMarcacaoExame, String nomeMicrocomputador) throws BaseException;
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGradeESoeSeq(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Integer soeSeq);
	
	public void inserirItemHorarioAgendado(ItemHorarioAgendadoVO itemHorarioAgendadoVO, VAelHrGradeDispVO vAelHrGradeDispVO, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador) throws BaseException;
	
	public void inserirItemHorarioAgendadoExtra(ItemHorarioAgendadoVO itemHorarioAgendadoVO, AelHorarioExameDisp horarioExameDisp, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador) throws BaseException;
	
	public AelHorarioExameDisp montarHorarioExameDisp(Date dataHora, Short unfExecutora, Integer seqGrade, 
			AelTipoMarcacaoExame tipoMarcacaoExame) throws ApplicationBusinessException;
	
	public void desfazerIdentificacaoAgendamentoExamesEmGrupo(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO);

	public List<ItemHorarioAgendadoVO> obterListaExamesParaAgendamentoEmGrupo(Integer soeSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, Short hedGaeUnfSeq,
			Integer hedGaeSeqp, Short seqp) throws ApplicationBusinessException;
	
	public List<ItemHorarioAgendadoVO> atualizarListaItemHorarioAgendadoVO( List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			Short unfGrade, Integer gradeSeqp, String sala, Integer soeSeq);
	
	public String gerarArquivoAgendas(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO) throws IOException;
	
	public List<RelatorioAgendaPorGradeVO> obterAgendasPorGrade(Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgendaInicial, Date dthrAgendaFinal, Boolean impHorariosLivres, Boolean isPdf);
	
	public List<AelGradeAgendaExame> pesquisarGradeAgendaExamePorSeqpUnfSeq(Object parametro, Short unfSeq);
	
	public List<AelGradeAgendaExame> pesquisarGradeExamePorUnidadeExec(Object parametro,Short unfSeq);
		
	public void validarFiltrosRelatorioAgendas(Date dataInicial, Date dataFinal, AelGradeAgendaExame gradeAgendaExame) 
			throws ApplicationBusinessException;
	
	public void validarPeriodoRelatorioAgendas(Date dataInicial, Date dataFinal) throws ApplicationBusinessException;
	
	public List<Integer> obterListaSoeSeqGradeAgenda(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO);

	public Long obterCountHorarioGradeExameGrade(Short unfSeq, Integer seqp);

	public Boolean validarAgendamentoExamesEmGrupo(List<AgendamentoExameVO> agendamentosExameVO, Integer grupoExameSeq, Short idSelecionado);

	public Boolean isImprimeTicketsAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impTickets);

	public ImpImpressora isImprimeEtiquetasAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impEtiquetas) throws BaseException;

    public boolean validaHabilitaAgendamento(Integer atendimentoSeq, Integer solicitacaoExamesSeq, Integer seqAtendimentoDiverso,
                                             Short unidadeFuncionalSeq, Short unidadeFuncionalUsuarioSeq);

    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosAgendadosMesmoGrupoExames(AghAtendimentos atendimento,
                                                                                          AelItemSolicitacaoExames itemSolicitacaoExames);

    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosDisponiveisAgendamentoConcorrente(Integer codigoPaciente,
                                                                                        String siglaExame,
                                                                                        Integer seqMaterial);

    public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExameNaoAgendados(final Integer soeSeq, final List<Short> listaSeqUnF);
    
    public boolean verificarExistenciaItensSolicitacaoExameNaoAgendados(final Integer soeSeq);
    
    public boolean verificarExistenciaAmostrasComum(List<AgendamentoExameVO> listaItensExame);

	Date obterHorarioExameDataMaisRecentePorGrade(Short unidFuncSeq,
			Integer gaeSeqp);


}