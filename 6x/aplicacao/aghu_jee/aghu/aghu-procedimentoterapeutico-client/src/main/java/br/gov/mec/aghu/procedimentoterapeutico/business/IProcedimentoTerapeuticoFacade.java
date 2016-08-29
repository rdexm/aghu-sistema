package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.controleinfeccao.vo.HistoricoAcomodacaoJnVO;
import br.gov.mec.aghu.controleinfeccao.vo.HistoricoSalaJnVO;
import br.gov.mec.aghu.dominio.DominioControleFrequenciaSituacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.model.AfaDiluicaoMdto;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaJn;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptCidTratTerapeutico;
import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.model.MptControleFrequencia;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptFavoritosServidor;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptItemCuidadoSumario;
import br.gov.mec.aghu.model.MptItemMdtoSumario;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptParamCalculoPrescricao;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptSessaoExtra;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoJustificativa;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioItensPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.TratamentoTerapeuticoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AcomodacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AfaFormaDosagemVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentosPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AlterarHorariosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ConsultaDadosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiaPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HistoricoSessaoJnVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HomologarProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaAfaDescMedicamentoTipoUsoMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.JustificativaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaEsperaRetirarPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ManterFavoritosUsuarioVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaJnVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.NovaVersaoProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacientesTratamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PercentualOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacientePTVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProcedimentosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistrarControlePacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TaxaOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TransferirDiaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.VincularIntercorrenciaTipoSessaoVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.protocolos.vo.ProtocoloAssociadoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloSessaoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.SituacaoVersaoProtocoloVO;
import br.gov.mec.aghu.view.VMpmDosagem;

@Local
public interface IProcedimentoTerapeuticoFacade extends Serializable {

	void atualizaMptAgendaPrescricao(MptAgendaPrescricao agendaPrescricao,
			boolean flush) throws ApplicationBusinessException;

	List<MptAgendaPrescricao> listarAgendasPrescricaoPorNumeroConsulta(
			Integer pConNumero);

	public List<MptTratamentoTerapeutico> pesquisarTratamentosTerapeuticosPorPaciente(
			AipPacientes paciente);

	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorCodigoPaciente(
			Integer pacCodigo);

	void persistirMptTratamentoTerapeutico(
			MptTratamentoTerapeutico mptTratamentoTerapeutico);
	
	void inserirMptTratamentoTerapeuticoDialise(Short unfSeqDialise, Short espSeqDialise, Integer pacCodigo,
			Integer tipoTratDialise, Short cspCnvCodigo, Byte cspSeq, Integer matriculaResp, Short vinCodigoResp, String nomeMicrocomputador) throws BaseException;

	public List<MptParamCalculoPrescricao> pesquisarMptParamCalculoPrescricoes(
			Integer pesoPacCodigo, Integer alturaPacCodigo);
	
	public Long listarProfCredAssinatLaudoCount(Integer matricula,
			Integer vinculo, Integer esp, Short convenio);

	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticos(
			Integer pacCodigo, AghAtendimentos atendimento);
	
	public List<MptItemPrescricaoMedicamento> listarItensPrescricaoMedicamento(
			Integer atdSeq, Integer pteSeq, Integer pmoSeq);

	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public Long listarSessoesFisioterapiaCount(Integer trpSeq);

	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorPacienteTptSeq(
			Integer codPaciente, Integer tptSeq);

	public MptTratamentoTerapeutico obterTratamentoTerapeutico(
			Integer seqTratamentoTerQuimio);

	public List<MptCidTratTerapeutico> listarCidTratamentoQuimio(
			Integer seqTratamentoTerQuimio);

	List<MptTratamentoTerapeutico> listarSessoesFisioterapia(
			Integer codPaciente, Integer tptSeq);

	public List<MptPrescricaoPaciente> listarPeriodosSessoesQuimio(Integer apaAtdSeq);

	public Integer obterPrescricaoPacienteporTrpSeq(Integer trpSeq);

	public List<SumarioQuimioItensPOLVO> listarDadosPeriodosSumarioPrescricaoQuimio(
			Date dtInicial, Date dtFinal, Integer apaAtdSeq, Integer apaSeq);

	public List<String> listarTituloProtocoloAssistencial(Integer atdSeq,
			Date dtInicio, Date dtFim);

	List<MptPrescricaoPaciente> pesquisarPrescricoesPacientePorAtendimento(Integer atdSeq);

	List<MptPrescricaoCuidado> pesquisarPrescricoesCuidado(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda);

	List<MptDataItemSumario> pesquisarDataItemSumario(Integer intAtdSeq, Integer apaSeq, Integer itqSeq);
	
	void persistirMptDataItemSumario(MptDataItemSumario itemSum);

	MptPrescricaoCuidado obterMptPrescricaoCuidadoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pcoSeq, Boolean join1, Boolean join2);

	List<MptItemPrescricaoSumario> pesquisarItemPrescricaoSumario(Integer intAtdSeq, Integer apaSeq, String sintaxe, DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum);

	void persistirMptItemPrescricaoSumario(MptItemPrescricaoSumario itemPrescSum);

	void persistirMptItemCuidadoSumario(MptItemCuidadoSumario itemCuidSum);
	
	List<MptPrescricaoMedicamento> listarPrescricoesMedicamento(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, Boolean solucao);
	
	MptPrescricaoMedicamento obterMptPrescricaoMedicamentoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pmoSeq, Boolean solucao);
	
	List<MptItemPrescricaoMedicamento> listarItensPrescricaoMdtoJoin(Integer atdSeq, Integer pteSeq, Integer pmoSeq);

	void persistirMptItemMdtoSumario(MptItemMdtoSumario itemMdtoSum);

	List<MptPrescricaoMedicamento> listarPrescricaoMedicamentoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda, Boolean solucao);

	List<MptPrescricaoCuidado> listarPrescricaoCuidadoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda);


	void atualizarAgendaPrescricaoPorCirurgiaCallableStatement(final Integer cirurgiaSeq) throws ApplicationBusinessException;

	Long pesquisarTratamentosTerapeuticosPorPacienteCount(
			AipPacientes paciente);
	
	MptAgendaPrescricao obterDataAgendaPrescricaoAtiva(Integer crgSeq, Short unfSeq);
	
	void atualizarMptAgendaPrescricao(MptAgendaPrescricao mptAgendaPrescricao);	
	
	
	MptTratamentoTerapeutico pesquisarTratamentoTerapeuticoPorSeq(Integer tprSeq);
	
	TratamentoTerapeuticoVO buscarTratamentoTerapeuticoPorSeq(Integer trpSeq);
	
	List<MptTipoSessao> listarMptTipoSessao(String descricao, Short unfSeq, DominioSituacao situacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarMptTipoSessaoCount(String descricao, Short unfSeq, DominioSituacao situacao);
	
	MptTipoSessao obterMptTipoSessaoPorSeq(Short seq);
	
	void inserirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException;
	
	void atualizarMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException;
	
	void excluirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException;
	
	List<MptTipoSessao> listarTiposSessao(String param);
	
	Long listarTiposSessaoCount(String param);
	
	List<MptTipoSessao> listarTiposSessaoPorUnfSeq(Short unfSeq);
	
	List<MptLocalAtendimento> listarMptLocaisAtendimentoPorSala(Short salaSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarMptLocaisAtendimentoPorSalaCount(Short salaSeq);

	List<MptSalas> listarMptSalas(Short tpsSeq, Short espSeq, String descricao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarMptSalasCount(Short tpsSeq, Short espSeq, String descricao);
	
	void persistirMptSala(MptSalas mptSala) throws ApplicationBusinessException;
	
	void persistirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException;
	
	void excluirMptSala(MptSalas mptSala) throws ApplicationBusinessException;
	
	void excluirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException;
	
	List<MptSalas> listarSalas(Short tpsSeq, String param);
	
	Long listarSalasCount(Short tpsSeq, String param);
	
	MptSalas obterMptSalaPorSeq(Short seq);
	
	void validarMptDiaTipoSessao(Boolean segunda, Boolean terca, Boolean quarta, Boolean quinta, Boolean sexta,
			Boolean sabado, Boolean domingo, Short tpsSeq) throws ApplicationBusinessException;
	
	List<MptDiaTipoSessao> obterDiasPorTipoSessao(Short tpsSeq);
	
	void validarConflitosPeriodo(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite) throws ApplicationBusinessException;
	
	void validarMptTurnoTipoSessao(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite, Short tpsSeq) throws ApplicationBusinessException;
	
	List<MptTurnoTipoSessao> obterTurnosPorTipoSessao(Short tpsSeq);
	
	List<PrescricaoPacienteVO> obterListaPrescricoesPorPaciente(Integer pacCodigo, Date dataCalculada);
	
	List<CadIntervaloTempoVO> listarIntervalosTempoPrescricao(Integer lote, Integer cloSeq);
	
	void validarFiltrosAgendamentoSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			DominioTurno turno, Date dataInicio, Date dataFim, Date dthrHoraInicio) throws ApplicationBusinessException;
	
	List<HorariosAgendamentoSessaoVO> sugerirAgendaSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			Short salaSeq, Short espSeq, Integer pacCodigo, Integer prescricao, DominioTipoLocal acomodacao, DominioTurno turno,
			Date dataInicio, Date dataFim, Date dthrHoraInicio, String[] diasSelecionados, boolean isSugestao) throws ApplicationBusinessException;
	
	List<PercentualOcupacaoVO> preencherPercentualOcupacao(Short salSeq, Short tpsSeq, List<HorariosAgendamentoSessaoVO> horariosAgendados);
	
	MptAgendamentoSessao agendarSessao(List<HorariosAgendamentoSessaoVO> horariosGerados, Short tpsSeq, Short salaSeq, DominioTurno turno,
			Integer pacCodigo, Short vpaPtaSeq, Short vpaSeqp, DominioTipoLocal acomodacao, Date dataInicio,
			Date dataFim, String nomeMicrocomputador) throws BaseException;
	
	List<AcomodacaoVO> obterListaHorariosAgendadosPorAcomodacao(Short tpsSeq, Short salSeq, Date dataMapeamento);
	
	List<HistoricoSalaJnVO> obterHistoricoSalaJn(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Short localSeq);
	
	List<HistoricoAcomodacaoJnVO> obterHistoricoAcomodacaoJn(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Short localSeq);
	
	Long obterHistoricoSalaJnCount(Short salaSeq);
	
	Long obterHistoricoAcomodacaoJnCount(Short localSeq);
	
	MptLocalAtendimento obterMptLocalAtendimentoPorSeq(Short localSeq);

	//46468
	List<MptTipoSessao> pesquisarTipoSessoesAtivas();
	
	//46468
	List<MptCaracteristica> recuperarListaDeCaracteristicas(MptTipoSessao tipoSessaoFiltro, String  descricaoFiltro, DominioSituacao situacao);
	
	List<MptTipoSessao> listarMptTiposSessao();
	
	void inserirProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, Short diasTratamento) throws ApplicationBusinessException;

	MptVersaoProtocoloSessao inserirCopiaProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException;

	MptProtocoloSessao verificaProtocoloPorDescricao(String descrProtocolo);
		    
	List<ProtocoloVO> pesquisarProtocolosAtivos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc);
	
	Long contarPesquisarProtocolosAtivos(ProtocoloVO filtro);
	
	List<ProtocoloVO> pesquisarProtocolosInativos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc);
	
	Long contarPesquisarProtocolosInativos(ProtocoloVO filtro);
	
	Long obterMedicamentosAtivosCount(String objPesquisa);
	
	List<AfaMedicamento> obterMedicamentosAtivos(String objPesquisa);
	
	List<MptFavoritosServidor> obterFavoritosTipoSessao(Integer matriculaServidor, Short vinCodigoServidor);
	
	MptTipoSessao obterTipoSessaoPorId(Short id);
	
	List<MptVersaoProtocoloSessao> pesquisarVersoesProtocolo(Integer seqProtocoloSessao);
	
	List<DominioSituacaoProtocolo> verificarSituacaoProtocolo(Integer seqProtocolo);
	
    MptVersaoProtocoloSessao obterSituacaoProtocolo(Integer seqProtocolo);
    
	void atualizarProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, Integer seqProtocolo, DominioSituacaoProtocolo situacaoProtocolo, Integer seqVersaoProtocoloSessao) throws ApplicationBusinessException;
	
	MptProtocoloSessao obterMptProtocoloSessaoPorSeq(Integer seqProtocolo);
	
	public ProtocoloSessaoVO obterItemVersaoProtocolo(Integer seqVersaoProtocolo);
	
	void excluirVersaoProtocolo(ProtocoloVO itemExclusao);
	
	List<MptTipoSessao> listarTiposSessaoCombo();

	String pesquisarProtocoloGrid(Integer cloSeq);

	String nomeResponsavel(String nome1, String nome2);

	Long pesquisarAgendmento(Integer lote, Integer consulta);	

	List<PacientesTratamentoSessaoVO> pesquisarPacientesTratamentoSessao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MptTipoSessao tipoSessao, Date periodoInicial,
			Date periodoFinal, Boolean checkAberto, Boolean checkFechado,
			Boolean checkPrimeiro, Boolean checkSessao, Integer codigo,
			Integer tipoFiltroPersonalizado, Integer parametro);

	MptFavoritoServidor pesquisarTipoSessaoUsuario(Integer matricula, Short vinCodigo);

	MptTipoSessao pesquisarTipoSessaaoFavorito(MptTipoSessao tipoSessao);

	Long pesquisarPacientesTratamentoSessaoCount(MptTipoSessao tipoSessao,
			Date periodoInicial, Date periodoFinal, Boolean checkAberto,
			Boolean checkFechado, Boolean checkPrimeiro, Boolean checkSessao,
			Integer codigoPaciente, Integer tipoFiltroPersonalizado, Integer parametro);
	//46468
	List<MptCaracteristicaJn> pesquisarAlteracoesCaracteristica(Short carSeq);
		
	
	
	void salvarCaracteristica(MptCaracteristica caracteristica, String usuarioLogado) throws ApplicationBusinessException;
	
	MptCaracteristicaTipoSessao obterVinculoCaracteristicaTipoSessao(MptCaracteristica mptCaracteristica, MptTipoSessao mptTipoSessao);
	
	MptCaracteristicaTipoSessao obterCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao);
	
	void adicionarMptCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristica mptCaracteristica) throws ApplicationBusinessException;
	
	void excluirMptCaracteristicaTipoSessao(MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao);
	
	void inserirMptCaracteristica(MptCaracteristica mptCaracteristica);
	
	List<MptCaracteristica> obterListaCaracteristicaDescricao(String strPesquisa);
	
	Long listarCaracteristicaCount(String strPesquisa);
	
	MptCaracteristica obterMptCaracteristicaPorSeq(MptCaracteristica mptCaracteristica);
	
	Long obterCaracteristicaDescricaoOuSiglaCount(String strPesquisa);
	
	List<MptCaracteristicaTipoSessao> obterCaracteristicaTipoSessaoPorTpsSeq(MptTipoSessao mptTipoSessao);
	
	MptCaracteristicaTipoSessao obterCaracteristicaTipoSessaoPorCarSeq(MptCaracteristica mptCaracteristica);

	//47027 - INICIO
	public List<VincularIntercorrenciaTipoSessaoVO> carregarVinculosTipoIntercorreciaTipoSessao(MptTipoSessao tipoSessao, String descricao, Integer firstResult, Integer maxResults);
	
	public Long carregarVinculosTipoIntercorreciaTipoSessaoCount(MptTipoSessao tipoSessao, String descricao);
	
	public List<MptTipoIntercorrencia> carregarTiposIntercorrencia(String pesquisa);
	
	public Long carregarTiposIntercorrenciaCount(String pesquisa);
	
	public Long verificarExistenciaVinculoIntercorrenciaTipoSessao(Short seqTipoIntercorrencia, Short seqTipoSessao);
	
	public boolean excluirVinculoIntercorrrenciaTipoSessao(Short seq);
	public Boolean verificarVinculo(Short seqTipoIntercorrencia, Short seqTipoSessao);
	public boolean adicionarVinculoIntercorrrenciaTipoSessao(MptTipoIntercorrencia tipoIntercorrencia, MptTipoSessao tipoSessao) throws ApplicationBusinessException;
	//47027 - FIM
	
	void salvarTipoIntercorrente(MptTipoIntercorrencia tipoIntercorrente, boolean situacao, boolean emEdicao) throws ApplicationBusinessException;
	
	boolean verificarDescricaoExistente(String descricao);
	
	boolean verificarVinculoPorTipoIntercorrente(Short seq);
	
	List<MptTipoIntercorrenciaJnVO> obterHistoricoTiposIntercorrentes(Integer seq);
	
	Long listarTiposIntercorrentesCount(String descricao, DominioSituacao situacao);
	
	List<MptTipoIntercorrencia> listarTiposIntercorrentes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			String descricao, DominioSituacao situacao);

	//#46468
	List<String> validarPersistenciaCaracteristica(MptCaracteristica caracteristica);
	
	void validarInativacaoStatusCaracteristica(MptCaracteristica caracteristica, Boolean situacao)throws ApplicationBusinessException;
	
	List<MptTurnoTipoSessao> obterTurnosPorTipoSessaoOrdenado(Short tpsSeq);

	MptFavoritoServidor obterFavoritoPorServidor(RapServidores servidor);

	List<MapaDisponibilidadeVO> consultarMapaDisponibilidade(Short tipoSessaoSeq, Short salaSeq, Date dataInicio, DominioTurno turno)
			throws ParseException;
	
	
	public List<MpaProtocoloAssistencial> buscarProtocolo(Object parametro, Short codigoTipoSessao);

	public Long buscarProtocoloCount(Object parametro, Short codigoSala);
	
	public List<MptLocalAtendimento> buscarLocalAtendimento(Object parametro, Short codigoSala);

	public Long buscarLocalAtendimentoCount(Object parametro, Short salaCombo);
	
	public List<MptTipoSessao> buscarTipoSessao();
	
	public List<MptSalas> buscarSala(Short codigoTipoSessao);
	
	public MptTurnoTipoSessao obterHorariosTurnos(Short tipoSessaoSeq, DominioTurno turno);

	public MptFavoritoServidor pesquisarFavoritosServidor(RapServidores usuarioLogado);
	
	public List<ListaPacienteAgendadoVO> pesquisarListaPacientes(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial);
	
	public Long pesquisarListaPacientesCount(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial);	
	
	public List<ListaPacienteAguardandoAtendimentoVO> pesquisarListaPacientesAguardandoAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial);
	
	public String validarCampos(Date dataInicio, Short tipoSessao) throws BaseListException;
	
	public void validarCampoTipoSessao(Short tipoSessao) throws ApplicationBusinessException;
	
	public void validarCampoSala(Short sala) throws ApplicationBusinessException;
	
	public String validarListagem(List<ListaPacienteAgendadoVO> listagem) throws ApplicationBusinessException;
		
	public String validarListagemAba3(List<ListaPacienteAguardandoAtendimentoVO> listagem) throws ApplicationBusinessException;
	
	public List<ListaPacienteEmAtendimentoVO> pesquisarListaPacientesEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial);

	public String validarListagemAba4(List<ListaPacienteEmAtendimentoVO> listagem) throws ApplicationBusinessException;
		
	public void chegouPaciente(ListaPacienteAgendadoVO paciente);

	public void emAtendimento(ListaPacienteAguardandoAtendimentoVO paciente);

	public void voltarAcolhimento(ListaPacienteAguardandoAtendimentoVO parametroSelecionado);

	public void concluirAtendimento(Integer seqSessao);

	public void voltarAguardandoAte(ListaPacienteEmAtendimentoVO parametroSelecionado);

	public void medicamentoDomiciliar(ListaPacienteEmAtendimentoVO medDomiciliar);

	public BigDecimal tempoManipulacao();
	
	public Date pesquisarHoraInicio(Integer seqSessao);
	
	public void validarHorario(MptSessao MptSessao) throws ApplicationBusinessException;
	
	public void inserir(MptSessao mptSessao, Date dataInicio);

	/**
	 * #44227	  
	 */
	List<MptBloqueio> pesquisarIndisponibilidadeSalaAcomodacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MptBloqueio filtro);

	Long pesquisarIndisponibilidadeSalaAcomodacaoCount(MptBloqueio filtro);

	List<MptLocalAtendimento> buscarLocalAtendimento(Short seq);

	void persistirMptBloqueio(MptBloqueio bloqueio) throws ApplicationBusinessException, BaseException;
	
	void atualizarMptBloqueio(MptBloqueio bloqueio);

	List<MptJustificativa> buscarJustificativa(String filtro);

	Long buscarJustificativaCount(String filtro);
	
	void removerMptBloqueio(MptBloqueio item);
	
	public List<MptTipoSessao>obterTipoSessaoDescricao();
	public List<MptSalas> obterSalaPorTipoSessao(Short seqTipoSessao);
	public ManterFavoritosUsuarioVO obterFavoritoPorPesCodigo(RapServidores servidor);
	
	public void removerMptFavoritoServidor(Integer favoritoSeq) throws BaseException;
	public ManterFavoritosUsuarioVO adicionarFavorito (MptSalas sala,  MptTipoSessao tipoSessao, ManterFavoritosUsuarioVO favorito) throws BaseListException;
	public void persistirMptFavoritoServidor(ManterFavoritosUsuarioVO favoritoVO, RapServidores servidor);
	
	public List<HistoricoSessaoJnVO> obterHistoricoSessaoJn(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short sessaoSeq);
	public Long obterHistoricoSessaoJnCount(Short sessaoSeq);
	public MptTipoSessao obterTipoSessaoOriginal(Short sessaoSeq);
	
	MptSalas obterSalaPorSeqAtiva(Short seq);
	
	MptTipoSessao obterMptTipoSessaoPorSeqAtivo(Short seq);
		
	MptTipoSessao obterTipoSessaoPorChavePrimaria(Short seq);
	
	List<MptJustificativa> pesquisarMotivoManutencaoAgendamentoSessaoTerapeutica(String filtro);
	
	Long pesquisarMotivoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro);
	
	List<AlterarHorariosVO> pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(final Integer codigoPaciente) throws ApplicationBusinessException;
	
	List<DiasAgendadosVO> pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(final Short agsSeq);

	List<MptTipoSessao> pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeutica(String filtro);

	Long pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro);
	
	void removerDiaManutencaoAgendamentoSessaoTerapeutica(final Short seqHorario, final Short seqAgendamento, final Integer seqSessao, final Short seqMotivo, final String justificativa);
	
	void removerCicloManutencaoAgendamentoSessaoTerapeutica(final Short seqAgendamento, final Integer seqSessao, final Short seqMotivo, final String justificativa);

	MptSalas obterMptSalaPorChavePrimaria(Short seq);
	
	List<MptTipoSessao> pesquisarMptTipoSessaoAtivos();
	
	TransferirDiaVO obterLabelRestricaoDatas(List<DiasAgendadosVO> dias) throws ApplicationBusinessException;
	
	boolean ativarTransferirDia(List<DiasAgendadosVO> dias);
	
	void transferirDia(TransferirDiaVO transferencia, AlterarHorariosVO ciclo, DiasAgendadosVO dia) throws ApplicationBusinessException;
	
	//42292
	public List<ImpressaoTicketAgendamentoVO> obterListaRegistroHorariosSessao(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta);	
	public List<ImpressaoTicketAgendamentoVO> obterListaCiclo(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta);
	public String obterProtocolos(List<Integer> listCloSeq);
	public String obterTextoTicket(AipPacientes paciente, List<Integer> codCiclo) throws ApplicationBusinessException;
	
	public List<ReservasVO> obterHorariosSessaoPorPacientePrescricaoReservas(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta);
	public List<ReservasVO> obterListaCicloReservas(Integer codPaciente, List<Integer> listCicloSeq);

	public String obterTextoTicketReservadas(AipPacientes paciente, List<Integer> codCiclo, Short seqAgendamento) throws ApplicationBusinessException;
	public List<ReservasVO> pesquisarConsultarDiasReservas(Short codigoAgsSessao);
	
	public List<ReservasVO> pesquisarConsultasReservas(Integer codigoPaciente, Integer consulta);
	
	MptFavoritoServidor obterServidorSelecionadoPorMatriculaVinculo(Integer matricula, Short vinculo);
	
	List<MptSalas> obterListaSalasPorTpsSeq(Short tpsSeq);
	
	List<MptTipoSessao> obterListaTipoSessaoPorIndSituacaoAtiva();
	
	List<DiaPrescricaoVO> obterDiaPrescricaoVO(Integer lote, Integer cloSeq);
	
	List<ProtocoloPrescricaoVO> obterProtocoloPrescricaoVOPorCloSeq(Integer cloSeq);
	
	List<MptJustificativa> obterListaJustificativaSB(String descricao);
	
	Long obterListaJustificativaSBCount(String descricao);
	
	List<PrescricaoPacientePTVO> obterListaPrescricaoPacientePTVO(Integer pacCodigo);
	
	void validarCampoObrigatorioAgendamentoSessaoExtra(MptTipoSessao tipoSessao, Date dataInicio, DiaPrescricaoVO diaPrescricaoVOSelecionado) throws ApplicationBusinessException;
	
	void persistirMptAgendamentoSessao(MptAgendamentoSessao mptAgendamentoSessao);
	
	void persistirMptHorarioSessao(MptHorarioSessao horarioSessao);
	
	void persistirMptSessaoExtra(MptSessaoExtra mptSessaoExtra);
	
	MptSessao obterMptSessaoPorChavePrimaria(Integer seq);
	
	Date relacionarDiaSelecionado(Date horaInicio, DiaPrescricaoVO diaPrescricaoVO) throws ApplicationBusinessException;
	
	void persistirExtratoSessao(MptExtratoSessao mptExtratoSessao);
	//#41705
	public boolean exibirColunaApac(Short tipoSessaoSeq);	
	
	public List<AgendamentosPacienteVO> obterAgendamentosPaciente(Integer pacCodigo);
	
	public List<ExtratoSessaoVO>pesquisarExtratoSessao(Integer pacCodigo);
	
	List<TaxaOcupacaoVO> consultarTotalOcupacaoPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, String horaInicio, String horaFim, Short tpsSeq, List<Short> sequenciaisSala);
	
	List<MptSalas> obterListaSalasPorTipoSessao(Short tpsSeq);
	
	MptTurnoTipoSessao obterTurnoTipoSessaoPorTurnoTpsSeq(DominioTurno turno, Short tpsSeq);
	
	List<TaxaOcupacaoVO> consultarTotalOcupacaoSalaPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim);
	
	List<TaxaOcupacaoVO> consultarTotalOcupacaoPoltronaPorDiaPeriodoSala(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim, List<Short> sequenciaisSala);
	
	List<MptFavoritoServidor> obterFavoritos(Integer matriculaServidor, Short vinCodigoServidor);
	
	/**
	 * Marca a opção de atendimento domiciliar
	 * #41706
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void marcarMedicamentoDomiciliar(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException;
	
	/**
	 * Obter as justificativas para suspensão
	 * #41706
	 * 
	 * @param tipoSessao
	 * @return
	 */
	List<MptJustificativa> obterJustificativaParaSuspensao(Short tipoSessao);
	
	/**
	 * Confirma a suspensão da sessão de um paciente
	 * #41706
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void confirmarSuspensaoSessao(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException;
	
	/**
	 * Volta o paciente para o status Agendado
	 * #41706
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void voltarParaAgendado(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException;
	
	/**
	 * Concluir o acolhimento
	 * #41706
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void concluirAcolhimento(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException;
	
	/**
	 * Valida a quantidade de registros retornados na consulta
	 * #41706
	 * 
	 * @param listagem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String validarListagemAcolhimento(List<PacienteAcolhimentoVO> listagem) throws ApplicationBusinessException;
	
	/**
	 * Obtem os pacientes para acolhimento (triagem)
	 * #41706
	 * 
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return
	 */
	List<PacienteAcolhimentoVO> obterPacientesParaAcolhimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao,
			Short sala, MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao,MpaProtocoloAssistencial mpaProtocoloAssistencial);

	/**
	 * Registra a impressão da pulseira para o paciente
	 * 
	 * #41706
	 * @param paciente
	 */
	void registrarImpressaoPulseira(PacienteAcolhimentoVO paciente);
	
	//41718
		List<MptTipoJustificativa> listarMptTipoJustificativa(String strPesquisa);
		Long listarMptTipoJustificativaCount(String param);
		Long listarMptJustificativaCount(MptJustificativa mptJustificativa);
		List<JustificativaVO> obterJustificativas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MptJustificativa mptJustificativa);
		MptJustificativa obterMptJustificativaVO(JustificativaVO mptJustificativa);
		MptJustificativa obterMptJustificativa(MptJustificativa mptJustificativa);
		void excluirJustificativa(MptJustificativa mptJustificativa)
				throws ApplicationBusinessException;
		
		void editarJustificativa(MptJustificativa mptJustificativa)
				throws ApplicationBusinessException;

		void adicionarJustificativa(MptJustificativa mptJustificativa)
				throws ApplicationBusinessException;
		

	List<ListaEsperaRetirarPacienteVO> pesquisarListaEsperaPacientes(AghParametros aghParametros, MptTipoSessao tipoSessao, Date dataPrescricao, Date dataSugerida, AghEspecialidades especialidade, AipPacientes paciente);

	String buscarNomeResponsavel(String nome1, String nome2);

	String buscarProtocolos(Integer cloSeq);

	List<CadIntervaloTempoVO> consultarDiasAgendamento(Integer lote);
	
	AipPacientes obterPacientePorPacCodigo(Integer pacCodigo);
	
	//#41736
	public AipPacientes dadosPacienteRelatorio(Integer prontuario);
	public ConsultaDadosAPACVO dadosPacienteAPAC(Date data, Integer horSessaoSeq);
	public List<ProcedimentosAPACVO>pesquisarprocedimentoApac(Long numeroApac);
	public void registraControleFrequencia(MptControleFrequencia mptControleFrequencia);
	public List<AipPacientes> obtemPacientePorCodigo(Integer codigoPaciente);
	public void registrarControleFrequenciaRelatorio(String dataAgendado,Integer codPaciente, Integer seqSessao, Long numeroApac, Byte capSeqApac, DominioControleFrequenciaSituacao dominioControleFrequencia, Date dataReferencia, RapServidores servidorLogado);
	
	//#45909
	public List<String> obterSiglaCaracteristicaPorTpsSeq(Short tpsSeq);
	public boolean exibirColuna(List<String> listaSiglas , String sigla);

	/**
	 * Valida a quantidade de registros retornados na consulta
	 * #41709
	 * 
	 * @param listagem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String validarListagemConcluido(List<PacienteConcluidoVO> listagem) throws ApplicationBusinessException;
	
	/**
	 * Obtem os pacientes com atendimento concluído
	 * #41709
	 * 
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return
	 */
	List<PacienteConcluidoVO> obterPacientesAtendimentoConcluido(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao,
			Short sala, MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao,MpaProtocoloAssistencial mpaProtocoloAssistencial);
	
	/**
	 * Volta o paciente para o status Em Atendimento
	 * #41709
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void voltarParaAtendimento(PacienteConcluidoVO paciente) throws ApplicationBusinessException;
	
	/**
	 * Marca a opção de atendimento domiciliar
	 * #41709
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	void marcarMedicamentoDomiciliar(PacienteConcluidoVO paciente) throws ApplicationBusinessException;
	
	List<MptTipoIntercorrenciaVO> obterIntercorrencias();
	List<RegistroIntercorrenciaVO> obterRegistroIntercorrenciaPorPaciente(Integer codigoPaciente);
	void gravarIntercorrencia(String descricao, Short tpiSeq, Integer sesSeq) throws ApplicationBusinessException;
	
	//#41711
	public List<RegistrarControlePacienteVO> carregarRegistrosIntercorrencia(Integer pacCodigo, Integer sesSeq);

	/**
	 * Obtém horários reservados para o Paciente informado.
	 * 
	 * @param pacCodigo - Código do Paciente
	 * @param listaHorarios - Lista de horários registrados na prescrição
	 * @return Lista de horários reservados
	 */
	List<HorarioReservadoVO> obterReservasPacienteParaConfirmacaoCancelamento(Integer pacCodigo, List<CadIntervaloTempoVO> listaHorarios);

	/**
	 * Cancela os horários reservados informados.
	 * 
	 * @param horariosReservados - Horários reservados
	 */
	void cancelarReservas(List<HorarioReservadoVO> horariosReservados);
	
	/**
	 * Confirma os horários reservados informados.
	 * 
	 * @param horariosReservados
	 * @param tpsSeq
	 * @param salaSeq
	 * @param espSeq
	 * @param prescricao
	 * @param nomeMicrocomputador
	 * 
	 * @throws BaseException
	 */
	void confirmarReservas(List<HorarioReservadoVO> horariosReservados, Short tpsSeq, Short salaSeq, Short espSeq, Integer prescricao,
			String nomeMicrocomputador, List<CadIntervaloTempoVO> listaPrescricoes) throws BaseException;

	/**
	 * Envia aviso de Farmácia para preparação de quimioterápico.
	 * #41703
	 * @param paciente
	 */
	void liberarQuimioterapia(Integer codigo, Integer codigoAtendimento, Short seqTipoSessao) throws ApplicationBusinessException;

	//#41704
	Boolean verificarPacienteAtrasadoSessao(Short seqHorario, Integer codPaciente);
	void registrarAusenciaPaciente(Integer seqSessao);
	void voltarStatusAgendado(Integer seqSessao);
	//#41703
	public boolean verificarExisteSessao(Integer seqSessao);

	//46834 ajuste documentação
	TaxaOcupacaoVO obterNomeSala(Short salSeq);

	Long removerItensRN07(Integer lote, Integer cloSeq);
	
	List<Short> obterLocaisAtendimentoPorSala(Short salaSeq);
	
	MptSalas obterSalaPorId(Short seqSala);
	
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarListaTratamento(Integer seqVersaoProtocoloSessao);
	
	void excluirTratamento(List<ProtocoloMedicamentoSolucaoCuidadoVO> lista,ProtocoloMedicamentoSolucaoCuidadoVO tratamento) throws ApplicationBusinessException;
	
	List<MedicamentosVO> pesquisarMedicamentos(String objPesquisa, Boolean padronizacao);
	
	Long pesquisarMedicamentosCount(String objPesquisa, Boolean padronizacao);
	
	List<AfaViaAdministracao> pesquisarVia(String objPesquisa, Boolean todasVias, MedicamentosVO madMatCodigo);
	
	Long pesquisarViaCount(String objPesquisa, Boolean todasVias, MedicamentosVO madMatCodigo);
	
	List<MpmTipoFrequenciaAprazamento> pesquisarFrequenciaAprazamento(String objPesquisa);
	
	Long pesquisarFrequenciaAprazamentoCount(String objPesquisa);
	
	List<AfaFormaDosagemVO> pesquisaFormaDosagem(Integer matCodigo);
	
	List<AfaTipoVelocAdministracoes> pesquisaTipoVelocAdministracoes();
		
	AfaMedicamento obterAfaMedicamentoPorChavePrimaria(Integer madMatCodigo);
	
	AfaFormaDosagem obterAfaFormaDosagemPorChavePrimaria(Integer fdsSeq);
	
	void inserirMedicamento(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, Integer vpsSeq,  ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionado, List<ParametroDoseUnidadeVO> listaParam, List<ParametroDoseUnidadeVO> listaParamExcluidos)throws ApplicationBusinessException;
		
	List<String> pesquisaInformacaoFormacologicaAux(Integer madMatCodigo);
		
	List<MpmCuidadoUsual> listarCuidados(String strPesquisa);

	Number listarCuidadosCount(String strPesquisa);

	List<MpmTipoFrequenciaAprazamento> listarAprazamentos(String strPesquisa);

	Number listarAprazamentosCount(String strPesquisa);

	void persistirProtocoloCuidados(MptProtocoloCuidados mptProtocoloCuidados);

	List<MptProtocoloSessao> listarProtocolos(String strPesquisa, Integer seqProtocolo);

	Long listarProtocolosCount(String strPesquisa, Integer seqProtocolo);

	List <ProtocoloAssociadoVO> listarProtocolosAssociacao(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,Integer seqProtocolo);

	Long listarProtocolosAssociacaoCount(Integer seqProtocolo);	
	
	List<SituacaoVersaoProtocoloVO> obterSituacaoVersaoProtocoloSelecionado(Integer seqProtocolo);
	
	List<ProtocoloMedicamentoSolucaoCuidadoVO> consultaCuidadosPorVersaoProtocolo(Integer seqVersaoProtocolo);

	List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarSolucoesPorVersaoProtocolo(Integer seqVersaoProtocolo);
	
	MptProtocoloMedicamentos obterMedicamentoPorChavePrimaria(Long seq);
	
	MptProtocoloItemMedicamentos obterItemMedicamentoPorChavePrimaria(Long seq);
	
	MedicamentosVO obterMedicamento(String objPesquisa, Boolean padronizacao, Integer matCodigo);
	
	AfaFormaDosagemVO obterFormaDosagem(Integer seq);
		
	AfaViaAdministracao obterVia(String sigla);
	
	MpmTipoFrequenciaAprazamento obterAprazamento(Short seq);
	
	AfaTipoVelocAdministracoes obterUnidadeInfusao(Short seq);
	
	void moverOrdemCima(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO anterior);
	
	void moverOrdemBaixo(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO posterior);	
	
	/**
	 * @author marcelo.deus
	 * #44281
	 */
	List<ListaAfaDescMedicamentoTipoUsoMedicamentoVO> listarSbMedicamentos(String param, Boolean indPadronizado);
	Long listarSbMedicamentosCount(String param, Boolean indPadronizado);
	List<AfaViaAdministracao> listarSbViaAdministracaoMedicamento(String param,	List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias);
	Long listarSbViaAdministracaoMedicamentoCount(String param, List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias);
	List<VMpmDosagem> listarComboUnidade(Integer codMedicamento);
	List<MpmTipoFrequenciaAprazamento> listarSuggestionTipoFrequenciaAprazamento(String param);
	Long listarSuggestionTipoFrequenciaAprazamentoCount(String param);
	List<AfaTipoVelocAdministracoes> listarComboUnidadeInfusao();
	List<ProtocoloItensMedicamentoVO> listarItensMedicamentoProtocolo(Long codSolucao);
	MptProtocoloMedicamentos buscarSolucaoParaEdicao(Long codSolucao);
	BigDecimal calcularValorVolumeMl(ProtocoloItensMedicamentoVO protocoloItensMedicamentoVO);
	VMpmDosagem buscarMpmDosagemPorSeqMedicamentoSeqDosagem(Integer medMatCodigo, Integer seqDose);
	MpmUnidadeMedidaMedica obterUnidadeMedidaPorSeq(Integer seqDosagem, Integer seqMed);
	AfaDiluicaoMdto buscarAfaDiluicaoMdtoPorMatCodigoEData(Integer medMatCodigo, Date date);
	void verificarInsercaoSolucao(MptProtocoloMedicamentos solucao,	List<ProtocoloItensMedicamentoVO> listaMedicamentos) throws ApplicationBusinessException;
	void persistirSolucao(MptProtocoloMedicamentos solucao);
	void persistirItemMedicamentoSolucao(MptProtocoloItemMedicamentos itemMedicamento);
	void persistirSolucaoEditada(MptProtocoloMedicamentos solucao);
	MptVersaoProtocoloSessao obterVersaoProtocoloSessaoPorSeq(Integer seq);
	void excluirProtocoloItemMedicamentoDaSolucao(Long ptmSeq);
	MpmUnidadeMedidaMedica obterUnidadeMedidaMedicamentoPorSigla(String ummDescricao);
	List<MptProtocoloMedicamentosDia> obterProtocoloMdtoDiaModificado (Long ptmSeq);
	void atualizarProtocoloSolucaoDia(MptProtocoloMedicamentosDia solucao);
	void alterarSolucao(MptProtocoloMedicamentos solucao,
		List<ProtocoloItensMedicamentoVO> listaMedicamentos, 
		List<ProtocoloItensMedicamentoVO> listaMedicamentosExclusao)throws ApplicationBusinessException ;
	void excluirProtocoloSolucaoPorSeq(Long pimSeq);
	
	void persistirRelacionamento(MptProtocoloAssociacao associacao);

	List<MptProtocoloAssociacao> buscarAssociacoes(Integer seqProtocolo);

	Boolean verificarStatusProtocolo(Integer seqProtocolo, Integer seqVersaoProtocoloSessao);	

	MptProtocoloSessao buscarProtocoloPesquisa(Integer seqProtocolo);

	Integer gerarNovoAgrupador();

	void excluirRelacionamento(Integer seq);

	void excluirCuidados(Integer seqCuidadoExclusao);

	MptProtocoloCuidados obterCuidadoParaEdicao(Integer seqEdicao);

	void atualizarCuidadoProtocolo(MptProtocoloCuidados mptProtocoloCuidados);
	
	void atualizarTodosDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia);
	void atualizarDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia);
	void atualizarDiaModificadoMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloMedicamentosDia listaMptProtocoloMedicamentosDia);
	List<MptProtocoloCuidadosDia> verificarDiaCuidado(Integer seqEdicao);

	void alterarDias(MptProtocoloCuidadosDia item);
	
	void fazerCheckCelula(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia);
	
	MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDia(Long ptmSeq, Short dia);
	
	MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(Long ptmSeq, Short dia);
	
	MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDia(Integer pcuSeq, Short dia);
	
	MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(Integer pcuSeq, Short dia);

	MpmCuidadoUsual obterCuidadoPorSeq(Integer cduSeq);

	void atualizarMptProtocoloCuidadosDia(MptProtocoloCuidadosDia diaEdicao);
	
	Boolean verificarExistenciaProtocolo(Integer seqProtocoloSessao);
	
	//44287
	public NovaVersaoProtocoloVO carregarItensVersaoProtocoloPorVpsSeq(Integer versaoProtocoloSeq);
	public Integer carregarNumeroUltimaVersaoPorProtocoloSeq(Integer seqProtocoloSessao);
	public MptVersaoProtocoloSessao inserirNovaVersaoProtocoloSessao(Integer seqProtocolo, Integer versao, Integer qtdCiclos, DominioSituacaoProtocolo indSituacao, 
		List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes,
		List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException;
	public boolean verificarExistenciaOutraVersaoProtocoloSessao(Integer protocoloSessaoSeq, Short tipoSessaoSeq, Integer vpsSeq);
	
	void atualizarCampoOrdem(List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, Short ordem);

	List<AfaMedicamento> pesquisaDiluente(Integer medMatCodigo);
	
	List<MptProtocoloAssociacao> buscarAssociacoesPorAgrupador(Integer agrupador);

	//#50323
	void validarCamposObrigatoriosParametroDose(ParametroDoseUnidadeVO paramDoseVO, Boolean desabilitarCampoUnidade) throws BaseListException;
	Boolean validarAdicaoParametroDoseParteUm(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO);
	void validarPesoOuIdadeParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException ;
	void validarSobreposicaoDeFaixaParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException;
	void persistirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose);
	void atualizarParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose);
	List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamentoSolucao(ProtocoloItensMedicamentoVO medicamentoSelecionado);
	void preInserirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose);
	void desatacharMptProtocoloMdto(MptProtocoloMedicamentos solucao);
	void validarPesoOuIdadeMinimoMaiorQueMaximo(ParametroDoseUnidadeVO paramDose) throws ApplicationBusinessException;
	void excluirParametroDoseMedicamento(Integer seq);

	//#50324
	List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamento(Integer medMatCodigo, Long seqItemProtocoloMdtos);
	void removerParametroDose(ParametroDoseUnidadeVO item);
	void validarCamposObrigatoriosMedicamento(MedicamentosVO medicamentosVO,MptProtocoloMedicamentos mptProtocoloMedicamentos,MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, AfaFormaDosagemVO unidadeSelecionada,
			List<ParametroDoseUnidadeVO> listaParam,AfaViaAdministracao viaSelecionada,MpmTipoFrequenciaAprazamento aprazamentoSelecionado,	DominioUnidadeHorasMinutos unidHorasCorrer,
			AfaTipoVelocAdministracoes unidadeInfusaoSelecionada) throws BaseListException;
//	Boolean validarAdicaoParametroAposExclusao(List<ParametroDoseUnidadeVO> listaParam);
	List<AfaFormaDosagemVO> pesquisarFormaDosagemParametroDose(Integer medMatCodigo);
	
	ListaAfaDescMedicamentoTipoUsoMedicamentoVO obterSbMedicamentos(Integer medMatCodigo, String descricaoMat);

	//44279
	public List<HomologarProtocoloVO> obterListaMedicamentosProtocolo(Integer seqVersaoProtocoloSessao);
	public void gravarHomologacaoProtocolo(Integer vpsSeq, String justificativa, DominioSituacaoProtocolo situacao, List<HomologarProtocoloVO> listaMedicamentosProtocolo) throws ApplicationBusinessException;
	
	MptProtocoloMedicamentos obterMedicamentoPorId(Long seq);


	
	Boolean existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Short seqAtd, Integer codigoPaciente);
	
	List<AlterarHorariosVO> pesquisarReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Integer codigoPaciente);
	
}
