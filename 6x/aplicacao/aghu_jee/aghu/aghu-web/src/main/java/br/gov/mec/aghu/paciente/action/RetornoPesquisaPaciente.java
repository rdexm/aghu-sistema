package br.gov.mec.aghu.paciente.action;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.action.AgrupamentoFamiliarController;
import br.gov.mec.aghu.ambulatorio.action.ConsultaPacientePaginatorController;
import br.gov.mec.aghu.ambulatorio.action.ListarConsultasPorGradeController;
import br.gov.mec.aghu.blococirurgico.action.AgendaProcedimentosController;
import br.gov.mec.aghu.blococirurgico.action.LaudoAIHPaginatorController;
import br.gov.mec.aghu.blococirurgico.action.PesquisaCirurgiaRealizadaNotaConsumoPaginatorController;
import br.gov.mec.aghu.blococirurgico.action.PesquisarPacientesCirurgiaController;
import br.gov.mec.aghu.blococirurgico.action.RegistroCirurgiaRealizadaController;
import br.gov.mec.aghu.blococirurgico.action.RelatorioEtiquetasIdentificacaoController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.CadastroPlanejamentoPacienteAgendaController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.PesquisaAgendaCirurgiaController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.PortalPesquisaCirurgiasController;
import br.gov.mec.aghu.estoque.action.GeracaoRequisicaoMaterialController;
import br.gov.mec.aghu.exames.action.ExameLimitadoAtendController;
import br.gov.mec.aghu.exames.action.GestaoCartasRecoletaPaginatorController;
import br.gov.mec.aghu.exames.agendamento.action.ListarExamesAgendamentoSelecaoController;
import br.gov.mec.aghu.exames.cadastrosapoio.action.AtendimentoDiversoController;
import br.gov.mec.aghu.exames.cadastrosapoio.action.AtendimentoDiversoPaginatorController;
import br.gov.mec.aghu.exames.cadastrosapoio.action.ListarExamesCriteriosSelecionadosController;
import br.gov.mec.aghu.exames.coleta.action.InformacaoAmostraController;
import br.gov.mec.aghu.exames.coleta.action.PesquisaSolicitacaoDiversosController;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.exames.protocoloentrega.action.PesquisaProtocoloEntregaExamesController;
import br.gov.mec.aghu.exames.protocolopaciente.action.ProtocolarPacienteController;
import br.gov.mec.aghu.exames.protocolopaciente.action.ProtocolarPacientePaginatorController;
import br.gov.mec.aghu.farmacia.action.EnviarInformacaoPrescribenteController;
import br.gov.mec.aghu.farmacia.action.RelatorioMedicamentosPrescritosPorPacienteController;
import br.gov.mec.aghu.farmacia.dispensacao.action.PesquisaDispensacaoMdtosPaginatorController;
import br.gov.mec.aghu.farmacia.dispensacao.action.PesquisaPacientesEmAtendimentoPaginatorController;
import br.gov.mec.aghu.faturamento.action.ConsultarContaHospitalarPaginatorController;
import br.gov.mec.aghu.faturamento.action.EncerramentoPreviaContaController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuario.action.MovimentarProntuarioPaginatorController;
import br.gov.mec.aghu.prescricaomedica.action.ConsultaMedicamentoAvaliadosController;
import br.gov.mec.aghu.prescricaomedica.action.ManterJustificativaLaudosController;
import br.gov.mec.aghu.prescricaomedica.action.VerificarPrescricaoMedicaController;
import br.gov.mec.aghu.procedimentoterapeutico.action.AgendamentoSessaoController;
import br.gov.mec.aghu.procedimentoterapeutico.action.AgendamentoSessaoExtraController;
import br.gov.mec.aghu.procedimentoterapeutico.action.ImprimirTicketAgendamentoController;
import br.gov.mec.aghu.procedimentoterapeutico.action.ManutencaoAgendamentoSessaoTerapeuticaController;
import br.gov.mec.aghu.procedimentoterapeutico.action.VisualizarPacientesListaEsperaController;
import br.gov.mec.aghu.transplante.action.PacientesListaOrgaosPaginatorController;
import br.gov.mec.aghu.transplante.action.PacientesListaTMOPaginatorController;
import br.gov.mec.aghu.transplante.action.RelatorioExtratoTransplantePorPacienteController;
import br.gov.mec.aghu.transplante.action.RelatorioTempoSobrevidaPacientesTransplanteController;


public class RetornoPesquisaPaciente implements Serializable {
	

	private static final long serialVersionUID = -24114292442516775L;
	
	private static final String PESQUISA_CONSULTAS_PACIENTE = "ambulatorio-pesquisaConsultasPaciente";
	private static final String LISTAR_CONSULTAS_POR_GRADE = "ambulatorio-listarConsultasPorGrade";
	private static final String ESTOQUE_GERACAO_REQUISICAO_MATERIAL = "estoque-geracaoRequisicaoMaterial";
	private static final String EXAMES_LISTAR_AGENDAMENTOS = "exames-listarAgendamento";
	private static final String EXAMES_ATENDIMENTOS_DIVERSOS_LIST= "exames-atendimentoDiversoList";
	private static final String EXAMES_ATENDIMENTOS_DIVERSOS = "exames-atendimentoDiverso";
	private static final String PAGINA_MANTER_JUSTIFICATIVA_LAUDOS = "prescricaomedica-manterJustificativaLaudos";
	private static final String PAGINA_VERIFICAR_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	private static final String PAGE_EXAMES_PESQUISA_PROTOCOLAR_PACIENTE = "exames-pesquisaProtocolarPaciente";
	private static final String PAGE_EXAMES_PROTOCOLAR_PACIENTE_CRUD = "exames-protocolarPacienteCRUD";
	private static final String PAGE_EXAMES_LIBERA_LIMITACAO_EXAME_SOLICITACAO = "exames-liberaLimitacaoExameSolicitacao";
	private static final String PAGE_EXAMES_INFORMACAO_COLETA_AMOSTRA= "exames-informacaoColetaAmostra";
	private static final String PAGE_EXAMES_LISTAR_EXAMES_CRITERIOS_SELECIONADOS = "exames-listarExamesCriteriosSelecionados";
	private static final String PAGE_EXAMES_GESTAO_CARTAS_RECOLETA_LIST = "exames-gestaoCartasRecoletaList";
	private static final String PAGE_EXAMES_PESQUISA_EXAMES = "exames-pesquisaExames";
	private static final String PAGE_EXAMES_PESQUISA_ENTREGA_EXAMES = "exames-pesquisaEntregaExames";
	private static final String PESQUISA_PACIENTES_EM_ATENDIMENTO ="farmacia-pesquisarPacientesEmAtendimentoList";
	private static final String PESQUISAR_DISPENSACAO_MDTOS_LIST = "farmacia-pesquisarDispensacaoMdtosList";
	private static final String RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE ="farmacia-relatorioMedicamentosPrescritosPorPaciente";
	private static final String PRESCRICAO_SITUACAO_DISPENSACAO_LIST = "farmacia-prescricaoSituacaoDispensacaoList";
	private static final String RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX = "farmacia-relatorioMedicamentosDispensadosPorBox";
	private static final String VISUALIZAR_CUSTO_PACIENTE = "sig-visualizarCustoPaciente";
	private static final String PAGE_ENCERRAMENTO_PREVIA_CONTA = "faturamento-encerramentoPreviaConta";
	private static final String PAGE_CONSULTA_CONTA_HOSPITALAR = "faturamento-consultarContaHospitalarList";
	private static final String PAGE_AGRUPAMENTO_FAMILIAR = "ambulatorio-agrupamentoFamiliar";
	private static final String PAGE_ENVIAR_INFORMACAO_PRESCRIBENTE = "farmacia-enviarInformacaoPrescribente";
	private static final String PAGE_AGENDAMENTO_SESSAO_EXTRA = "procedimentoterapeutico-agendamentoSessaoExtra";
	private static final String PAGE_CONSULTA_MEDICAMENTOS_AVALIADOS = "prescricaomedica-consultaMedicamentosAvaliados";
	private static final String PESQUISA_AGENDA_CIRURGIA = "blococirurgico-pesquisaAgendaCirurgia";
	private static final String PESQUISAR_PACIENTE_CIRURGIA = "blococirurgico-pesquisarPacientesCirurgia";
	private static final String CADASTRO_PLANEJAMENTO_PACIENTE_AGENDA = "blococirurgico-planejamentoPacienteAgendaCRUD";
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	private static final String PORTAL_PESQUISA_CIRURGIAS = "blococirurgico-portalPesquisaCirurgias";
	private static final String PAGE_PESQUISA_CIRURGIA_REALIZADA_NOTA_CONSUMO = "blococirurgico-pesquisaCirurgiaRealizadaNotaConsumo";
	private static final String PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO = "blococirurgico-registroCirurgiaRealizada";
	private static final String PAGE_BLOCO_LAUDO_AIH = "blococirurgico-laudoAIH";
	private static final String PAGE_BLOCO_RELATORIO_IDENTIFICACAO = "blococirurgico-relatorioEtiquetasIdentificacao";
	private static final String PAGE_AGENDAMENTO_SESSAO = "procedimentoterapeutico-agendamentoSessao";	
	private static final String PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA= "procedimentoterapeutico-manutencaoAgendamentoSessaoTerapeutica";
	private static final String PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS =  "exames-pesquisarSolicitacaoDiversos";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA = "procedimentoterapeutico-visualizarPacientesListaEspera";
	private static final String PAGE_IMPRESSAO_TICKET_AGENDAMENTO = "procedimentoterapeutico-impressaoTicketAgendamento";
	private static final String PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES = "transplante-relatorioTempoSobrevidaPacientesTransplantes"; 
	private static final String PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE = "transplante-relatorioExtratoTransplantePorPaciente"; 
	private static final String PAGE_PACIENTES_LISTAS_TMO = "transplante-pacientesListaTMO"; 
	private static final String PAGE_PACIENTES_LISTAS_ORGAO = "transplante-pacientesListaOrgao";
	private static final String PAGE_MOVIMENTAR_PRONTUARIO = "paciente-movimentaProntuario"; 
	private static final String PAGE_PESQUISA_PROTOCOLO_ENTREGA_EXAMES = "exames-pesquisaProtocoloEntregaExames";

	
	@Inject
	private ManterJustificativaLaudosController manterJustificativaLaudosController;
	
	@Inject
	private VerificarPrescricaoMedicaController verificarPrescricaoMedicaController;
	
	@Inject
	private ListarConsultasPorGradeController listarConsultasPorGradeController;
	
	@Inject
	private GeracaoRequisicaoMaterialController geracaoRequisicaoMaterialController;
	
	@Inject
	private ListarExamesAgendamentoSelecaoController listarExamesAgendamentoSelecaoController;
	
	@Inject
	private AtendimentoDiversoPaginatorController atendimentoDiversoPaginatorController;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController cadastroPlanejamentoPacienteAgendaController;
	
	@Inject
	private AtendimentoDiversoController atendimentoDiversoController;
	
	@Inject
	private ProtocolarPacientePaginatorController protocolarPacientePaginatorController;
	
	@Inject
	private ProtocolarPacienteController protocolarPacienteController;
	
	@Inject
	private ExameLimitadoAtendController exameLimitadoAtendController;
	
	@Inject
	private InformacaoAmostraController informacaoAmostraController;
	
	@Inject
	private GestaoCartasRecoletaPaginatorController gestaoCartasRecoletaPaginatorController;

	@Inject
	private	ListarExamesCriteriosSelecionadosController listarExamesCriteriosSelecionadosController;
	
	@Inject
	private PesquisaExameController pesquisaExameController;
	
	@Inject
	private PesquisaDispensacaoMdtosPaginatorController pesquisaDispensacaoMdtosPaginatorController; 

	@Inject
	private RelatorioMedicamentosPrescritosPorPacienteController relatorioMedicamentosPrescritosPorPacienteController;
	
	@Inject
	private RegistroCirurgiaRealizadaController registroCirurgiaRealizadaController;
	
	@Inject
	private ConsultarContaHospitalarPaginatorController consultarContaHospitalarPaginatorController;
	
	@Inject
	private EncerramentoPreviaContaController encerramentoPreviaContaController;

	@Inject
	private AgendaProcedimentosController agendaProcedimentosController;
	
	@Inject
	private AgendamentoSessaoController agendamentoSessaoController;
	
	@Inject
	private ManutencaoAgendamentoSessaoTerapeuticaController manutencaoAgendamentoSessaoTerapeuticaController;
	
	@Inject
	private PortalPesquisaCirurgiasController portalPesquisaCirurgiasController;
	
	@Inject
	private RelatorioEtiquetasIdentificacaoController relatorioEtiquetasIdentificacaoController;
	
	@Inject
	private RelatorioTempoSobrevidaPacientesTransplanteController tempoSobrevidaPacientesTransplantesController;	
	
	@Inject
	private RelatorioExtratoTransplantePorPacienteController relatorioExtratoTransplantePorPacienteController;
	
	@Inject
	private PesquisaPacientesEmAtendimentoPaginatorController pesquisaPacientesEmAtendimentoPaginatorController;
	
	@Inject 
	private AgrupamentoFamiliarController agrupamentoFamiliarController;
	
	@Inject
	private EnviarInformacaoPrescribenteController enviarInformacaoPrescribenteController;	

	@Inject
	private ConsultaMedicamentoAvaliadosController consultaMedicamentoAvaliadosController;
		
	@Inject
	private PesquisaSolicitacaoDiversosController pesquisaSolicitacaoDiversosController;
	
	@Inject
	private ImprimirTicketAgendamentoController imprimirTicketAgendamentoController;
	
	@Inject
	private AgendamentoSessaoExtraController agendamentoSessaoExtraController;
	
	@Inject
	private VisualizarPacientesListaEsperaController visualizarPacientesListaEsperaController;
	
	@Inject
	private PacientesListaTMOPaginatorController pacientesListaTMOPaginatorController; 
	@Inject
	private PacientesListaOrgaosPaginatorController pacientesListaOrgaosPaginatorController; 
	
	@Inject
	private ConsultaPacientePaginatorController consultaPacientePaginatorController;
	
	@Inject
	private PesquisarPacientesCirurgiaController pesquisarPacientesCirurgiaController;
	
	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	
	@Inject
	private LaudoAIHPaginatorController laudoAIHPaginatorController;
	
	@Inject
	private PesquisaCirurgiaRealizadaNotaConsumoPaginatorController pesquisaCirurgiaRealizadaNotaConsumoPaginatorController;
	
	@Inject
	private MovimentarProntuarioPaginatorController movimentarProntuarioPaginatorController;
	
	@Inject
	private PesquisaProtocoloEntregaExamesController pesquisaProtocoloEntregaExamesController;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	
	public String execute(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (cameFrom.equalsIgnoreCase("manterJustificativaLaudos") ){
			this.manterJustificativaLaudosController.setPacCodigoFonetica(codigoPaciente);
			return PAGINA_MANTER_JUSTIFICATIVA_LAUDOS;
		} else {
			return getPaginaVerificarPrescricaoMedicaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPaginaVerificarPrescricaoMedicaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGINA_VERIFICAR_PRESCRICAO_MEDICA.equalsIgnoreCase(cameFrom) ) {
			verificarPrescricaoMedicaController.setPacCodigoFonetica(codigoPaciente);
			return PAGINA_VERIFICAR_PRESCRICAO_MEDICA;
		} else {
			return getPesquisaConsultasPacientePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaConsultasPacientePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PESQUISA_CONSULTAS_PACIENTE.equalsIgnoreCase(cameFrom) ) {
			consultaPacientePaginatorController.setPacCodigoFonetica(codigoPaciente);
			return PESQUISA_CONSULTAS_PACIENTE;
		} else {
			return getPesquisaProtocoloEntregaExamesPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaProtocoloEntregaExamesPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_PESQUISA_PROTOCOLO_ENTREGA_EXAMES.equalsIgnoreCase(cameFrom) ) {
			pesquisaProtocoloEntregaExamesController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_PESQUISA_PROTOCOLO_ENTREGA_EXAMES;
		} else {
			return getListarConsultasPorGradePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getListarConsultasPorGradePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (LISTAR_CONSULTAS_POR_GRADE.equalsIgnoreCase(cameFrom) ) {
			listarConsultasPorGradeController.setPacCodigoFonetica(codigoPaciente);
			return LISTAR_CONSULTAS_POR_GRADE;
		} else {
			return getPesquisaAgendaCirurgiaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaAgendaCirurgiaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PESQUISA_AGENDA_CIRURGIA.equalsIgnoreCase(cameFrom) ) {
			if (codigoPaciente != null) {
				pesquisaAgendaCirurgiaController.setPacCodigo(codigoPaciente);
				pesquisaAgendaCirurgiaController.carregaPaciente(pacienteFacade.buscaPaciente(codigoPaciente));
			}
			return PESQUISA_AGENDA_CIRURGIA;
		} else {
			return getEstoqueGeracaoRequisicaoMaterialPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getEstoqueGeracaoRequisicaoMaterialPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (ESTOQUE_GERACAO_REQUISICAO_MATERIAL.equalsIgnoreCase(cameFrom) ){
			geracaoRequisicaoMaterialController.setPacCodigoFonetica(codigoPaciente);
			return ESTOQUE_GERACAO_REQUISICAO_MATERIAL;
		} else {
			return getExamesListarAgendamentosPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesListarAgendamentosPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (EXAMES_LISTAR_AGENDAMENTOS.equalsIgnoreCase(cameFrom)) {
			listarExamesAgendamentoSelecaoController.setPacCodigoFonetica(codigoPaciente);
			return EXAMES_LISTAR_AGENDAMENTOS;
		} else {
			return getExamesAtendimentosDiversosListPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesAtendimentosDiversosListPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (EXAMES_ATENDIMENTOS_DIVERSOS_LIST.equalsIgnoreCase(cameFrom)) {
			atendimentoDiversoPaginatorController.setPacCodigoFonetica(codigoPaciente);
			return 	EXAMES_ATENDIMENTOS_DIVERSOS_LIST;
		} else {
			return getExamesAtendimentosDiversosPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesAtendimentosDiversosPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (EXAMES_ATENDIMENTOS_DIVERSOS.equalsIgnoreCase(cameFrom)) {
			atendimentoDiversoController.setPacCodigoFonetica(codigoPaciente);
			return EXAMES_ATENDIMENTOS_DIVERSOS;
		} else {
			return getExamesPesquisaProtocolarPacientePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesPesquisaProtocolarPacientePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_PESQUISA_PROTOCOLAR_PACIENTE.equalsIgnoreCase(cameFrom)) {
			this.protocolarPacientePaginatorController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_EXAMES_PESQUISA_PROTOCOLAR_PACIENTE;
		} else {
			return getExamesProtocolarPacienteCrudPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesProtocolarPacienteCrudPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_PROTOCOLAR_PACIENTE_CRUD.equalsIgnoreCase(cameFrom)) {
			this.protocolarPacienteController.setPacCodigoFonetica(codigoPaciente);
			return 	PAGE_EXAMES_PROTOCOLAR_PACIENTE_CRUD;
		} else {
			return getExamesLiberaLimitacaoExameSolicitacaoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesLiberaLimitacaoExameSolicitacaoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_LIBERA_LIMITACAO_EXAME_SOLICITACAO.equalsIgnoreCase(cameFrom)) {
			this.exameLimitadoAtendController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_EXAMES_LIBERA_LIMITACAO_EXAME_SOLICITACAO;
		} else {
			return getPesquisarPacienteCirurgiaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisarPacienteCirurgiaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PESQUISAR_PACIENTE_CIRURGIA.equalsIgnoreCase(cameFrom)) {
			if (codigoPaciente != null) {
				pesquisarPacientesCirurgiaController.setPacCodigoFonetica(codigoPaciente);
				pesquisarPacientesCirurgiaController.carregaPaciente(pacienteFacade.buscaPaciente(codigoPaciente));
			}
			return PESQUISAR_PACIENTE_CIRURGIA;
		}  else {
			return getCadastroPlanejamentoPacienteAgendaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getCadastroPlanejamentoPacienteAgendaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (CADASTRO_PLANEJAMENTO_PACIENTE_AGENDA.equalsIgnoreCase(cameFrom)) {
			cadastroPlanejamentoPacienteAgendaController.setPacCodigoFonetica(codigoPaciente);
			return CADASTRO_PLANEJAMENTO_PACIENTE_AGENDA;
		} else {
			return getExamesInformacaoColetaAmostraPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesInformacaoColetaAmostraPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_INFORMACAO_COLETA_AMOSTRA.equalsIgnoreCase(cameFrom)) {
			this.informacaoAmostraController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_EXAMES_INFORMACAO_COLETA_AMOSTRA;
		} else {
			return getExamesListarExamesCriteriosSelecionadosPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesListarExamesCriteriosSelecionadosPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_LISTAR_EXAMES_CRITERIOS_SELECIONADOS.equalsIgnoreCase(cameFrom)) {
		    this.listarExamesCriteriosSelecionadosController.setPacCodigoFonetica(codigoPaciente);
		    return PAGE_EXAMES_LISTAR_EXAMES_CRITERIOS_SELECIONADOS;
		} else {
			return getExamesGestaoCartasRecoletaListPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesGestaoCartasRecoletaListPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_GESTAO_CARTAS_RECOLETA_LIST.equalsIgnoreCase(cameFrom)) {
		   this.gestaoCartasRecoletaPaginatorController.setPacCodigo(codigoPaciente);
		   return PAGE_EXAMES_GESTAO_CARTAS_RECOLETA_LIST;
		} else {
			return getExamesPesquisaExamesPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesPesquisaExamesPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_PESQUISA_EXAMES.equalsIgnoreCase(cameFrom)) {
			this.pesquisaExameController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_EXAMES_PESQUISA_EXAMES;
		} else {
			return getExamesPesquisaEntregaExamesPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getExamesPesquisaEntregaExamesPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_EXAMES_PESQUISA_ENTREGA_EXAMES.equalsIgnoreCase(cameFrom)) {
			this.pesquisaExameController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_EXAMES_PESQUISA_ENTREGA_EXAMES;
		} else {
			return getPesquisarDispensacaoMdtosSListPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisarDispensacaoMdtosSListPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PESQUISAR_DISPENSACAO_MDTOS_LIST.equalsIgnoreCase(cameFrom)) {
			pesquisaDispensacaoMdtosPaginatorController.setPacCodigoFonetica(codigoPaciente);
			return PESQUISAR_DISPENSACAO_MDTOS_LIST;
		} else {
			return getRelatorioMedicamentosPrescritosPorPacientePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getRelatorioMedicamentosPrescritosPorPacientePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		 if(RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE.equalsIgnoreCase(cameFrom)) {
			relatorioMedicamentosPrescritosPorPacienteController.setCodPaciente(codigoPaciente);				
			return RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE;				
		} else {
			return getPrescricaoSituacaoDispensacaoListPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	
	private String getPrescricaoSituacaoDispensacaoListPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PRESCRICAO_SITUACAO_DISPENSACAO_LIST.equalsIgnoreCase(cameFrom)) {
			return PRESCRICAO_SITUACAO_DISPENSACAO_LIST;
		} else {
			return getRelatorioMedicamentosDispensadosPorBoxPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getRelatorioMedicamentosDispensadosPorBoxPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX.equalsIgnoreCase(cameFrom)) {
			return RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX;
		} else {
			return getPesquisaPacientesEmAtendimentoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaPacientesEmAtendimentoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PESQUISA_PACIENTES_EM_ATENDIMENTO.equalsIgnoreCase(cameFrom)) {
			pesquisaPacientesEmAtendimentoPaginatorController.setPacCodigo(codigoPaciente);
			return PESQUISA_PACIENTES_EM_ATENDIMENTO;
		} else {
			return getVisualizarCustoPacientePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getVisualizarCustoPacientePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(VISUALIZAR_CUSTO_PACIENTE.equalsIgnoreCase(cameFrom)) {
			return VISUALIZAR_CUSTO_PACIENTE;
		} else {
			return getEncerramentoPreviaContaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getEncerramentoPreviaContaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_ENCERRAMENTO_PREVIA_CONTA.equalsIgnoreCase(cameFrom)) {
			encerramentoPreviaContaController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_ENCERRAMENTO_PREVIA_CONTA;
		} else {
			return getAgendamentoProcedimentoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getAgendamentoProcedimentoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(AGENDA_PROCEDIMENTOS.equalsIgnoreCase(cameFrom)) {
			if(!isBotaoVoltar) {
				agendaProcedimentosController.processarBuscaPacientePorCodigo(codigoPaciente);	
			}
			return AGENDA_PROCEDIMENTOS;
		} else {
			return getAgendamentoSessaoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getAgendamentoSessaoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_AGENDAMENTO_SESSAO.equalsIgnoreCase(cameFrom)) {
			if(!isBotaoVoltar) {
				agendamentoSessaoController.processarBuscaPacientePorCodigo(codigoPaciente);
			}
			return PAGE_AGENDAMENTO_SESSAO;
		} else {
			return getManutencaoAgendamentoSessaoTerapeuticaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getManutencaoAgendamentoSessaoTerapeuticaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		
		if(PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA.equalsIgnoreCase(cameFrom)) {
			if(!isBotaoVoltar) {
				manutencaoAgendamentoSessaoTerapeuticaController.processarBuscaPacientePorCodigo(codigoPaciente);
			}
			return PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA;	
		} else {
			return getPesquisaCirurgiaRealizadaNotaConsumoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaCirurgiaRealizadaNotaConsumoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		
		if (PAGE_PESQUISA_CIRURGIA_REALIZADA_NOTA_CONSUMO.equalsIgnoreCase(cameFrom) ) {
			this.pesquisaCirurgiaRealizadaNotaConsumoPaginatorController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_PESQUISA_CIRURGIA_REALIZADA_NOTA_CONSUMO;
		} else {
			return getRegistroCirurgiaRealizadaNotaConsumoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getRegistroCirurgiaRealizadaNotaConsumoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		
		if (PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO.equalsIgnoreCase(cameFrom) ) {
			registroCirurgiaRealizadaController.setPacCodigoFonetica(codigoPaciente);
			registroCirurgiaRealizadaController.inicio();
			return PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO;		
		} else {
			return getConsultaContaHospitalarPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getConsultaContaHospitalarPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_CONSULTA_CONTA_HOSPITALAR.equalsIgnoreCase(cameFrom) ) {
			consultarContaHospitalarPaginatorController.setCodigo(codigoPaciente);
			consultarContaHospitalarPaginatorController.inicio();
			return PAGE_CONSULTA_CONTA_HOSPITALAR;
		} else {
			return getBlocoLaudoAIHPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getBlocoLaudoAIHPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(cameFrom) ) {
			laudoAIHPaginatorController.setPacCodigoFonetica(codigoPaciente);				
			return PAGE_BLOCO_LAUDO_AIH;				
		} else {
			return getPesquisaCirurgiasPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisaCirurgiasPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		
		if(PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(cameFrom)) {
			if(!isBotaoVoltar){
				portalPesquisaCirurgiasController.processarBuscaPacientePorCodigo(codigoPaciente);	
			}
			return PORTAL_PESQUISA_CIRURGIAS;
		}  else {
			return getBlocoRelatorioIdentificacaoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getBlocoRelatorioIdentificacaoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		
		if (PAGE_BLOCO_RELATORIO_IDENTIFICACAO.equalsIgnoreCase(cameFrom)) {
			relatorioEtiquetasIdentificacaoController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_BLOCO_RELATORIO_IDENTIFICACAO;
		} else {
			return getEnviarInformacaoPresPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}

	private String getEnviarInformacaoPresPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(cameFrom.equals(PAGE_ENVIAR_INFORMACAO_PRESCRIBENTE)) {
			enviarInformacaoPrescribenteController.setPacCodigo(codigoPaciente);
			return PAGE_ENVIAR_INFORMACAO_PRESCRIBENTE;
		} else {
			return getImpressaoTicketAgendamentoPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getImpressaoTicketAgendamentoPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_IMPRESSAO_TICKET_AGENDAMENTO.equalsIgnoreCase(cameFrom)) {
			if(!isBotaoVoltar) {
				imprimirTicketAgendamentoController.processarBuscaPacientePorCodigo(codigoPaciente);
			}
			return PAGE_IMPRESSAO_TICKET_AGENDAMENTO;			
		} else {
			return getAgrupamentoFamiliarPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getAgrupamentoFamiliarPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_AGRUPAMENTO_FAMILIAR.equalsIgnoreCase(cameFrom)) {
			agrupamentoFamiliarController.setPacCodigoFonetica(codigoPaciente);
			return PAGE_AGRUPAMENTO_FAMILIAR;
		} else {
			return getExamesPesquisarSolicitacaoDiversosPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}	
	}
	
	private String getExamesPesquisarSolicitacaoDiversosPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS.equalsIgnoreCase(cameFrom)) {
			 pesquisaSolicitacaoDiversosController.setCodPac(codigoPaciente);
			 return PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS;
		} else {
			return getAgendamentoSessaoExtraPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getAgendamentoSessaoExtraPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_AGENDAMENTO_SESSAO_EXTRA.equalsIgnoreCase(cameFrom)) {
			agendamentoSessaoExtraController.setPacCodigo(codigoPaciente);
			return PAGE_AGENDAMENTO_SESSAO_EXTRA;
		} else {
			return getPesquisarPacientesAgendadosPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getPesquisarPacientesAgendadosPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
		} else {
			return getTempoSobrevidaPacienteTransplantePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getTempoSobrevidaPacienteTransplantePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES.equalsIgnoreCase(cameFrom)) {		
			if(!isBotaoVoltar){
				this.tempoSobrevidaPacientesTransplantesController.processarBuscaPacientePorCodigo(codigoPaciente);
			}
			return PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES;	
		} else {
			return getRelatorioExtratoTranplantePacientePage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getRelatorioExtratoTranplantePacientePage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE.equalsIgnoreCase(cameFrom)) {		
			if(!isBotaoVoltar) {
				this.relatorioExtratoTransplantePorPacienteController.setPacCodigo(codigoPaciente);
				this.relatorioExtratoTransplantePorPacienteController.obterPaciente();
			}
			return PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE;
		} else {
			return getVisualizarPacientesListaEsperaPage(cameFrom, codigoPaciente, isBotaoVoltar);
		}
	}
	
	private String getVisualizarPacientesListaEsperaPage(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if(PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA.equalsIgnoreCase(cameFrom)) {
			visualizarPacientesListaEsperaController.obterPacientePorPacCodigo(codigoPaciente);
			return PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA;
		} else {
				if(cameFrom.equals(PAGE_CONSULTA_MEDICAMENTOS_AVALIADOS)) {
					if(codigoPaciente!=null) {
						consultaMedicamentoAvaliadosController.setCodigoPaciente(codigoPaciente);
						consultaMedicamentoAvaliadosController.setPesquisaFonetica(true);
						consultaMedicamentoAvaliadosController.setProntuario(pacienteFacade.buscaPaciente(codigoPaciente).getProntuario());
				}
				return PAGE_CONSULTA_MEDICAMENTOS_AVALIADOS;
			} else {
				return getMovimentarProntuarioPacientes(cameFrom, codigoPaciente, isBotaoVoltar);
			}
		}
	}
	
	private String getMovimentarProntuarioPacientes(String cameFrom, Integer codigoPaciente, boolean isBotaoVoltar) {
		if (PAGE_MOVIMENTAR_PRONTUARIO.equalsIgnoreCase(cameFrom) ) {
			movimentarProntuarioPaginatorController.setPacCodigoFonetica(codigoPaciente);				
			return PAGE_MOVIMENTAR_PRONTUARIO;				
		} else {
			return getPacientesPageListaPage(cameFrom, codigoPaciente);
		}
	}
	
	private String getPacientesPageListaPage(String cameFrom, Integer codigoPaciente){
		if(PAGE_PACIENTES_LISTAS_TMO.equalsIgnoreCase(cameFrom)) {		// #50814
			pacientesListaTMOPaginatorController.setPacCodigo(codigoPaciente);
			return PAGE_PACIENTES_LISTAS_TMO;
		} else if(PAGE_PACIENTES_LISTAS_ORGAO.equalsIgnoreCase(cameFrom)) {		// #50816
			pacientesListaOrgaosPaginatorController.setPacCodigo(codigoPaciente);
			return PAGE_PACIENTES_LISTAS_ORGAO;
		}
		return cameFrom;
	}

}
