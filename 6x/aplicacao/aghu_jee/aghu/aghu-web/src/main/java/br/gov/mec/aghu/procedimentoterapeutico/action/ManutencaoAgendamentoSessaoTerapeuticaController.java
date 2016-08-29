package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AlterarHorariosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametrosAgendamentoSessoesVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TransferirDiaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Controller de #44228 – Realizar Manutenção de Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 */
public class ManutencaoAgendamentoSessaoTerapeuticaController extends ActionController {

	private static final long serialVersionUID = 5909255001398253628L;
	private static final Log LOG = LogFactory.getLog(ManutencaoAgendamentoSessaoTerapeuticaController.class);

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	/*
	 * PARÂMETROS CONVERSAÇÃO
	 */
	private Short tpsSeq; // Id da Sessão
	private Integer pacCodigo; // Código do Paciente
	private Short agsSeq; // Seq do agendamento
	private boolean agendaDetalhada; // Se a tela for chamada pela Agenda Detalhada
	private String voltarPara;

	// SuggestionBox Tipo de Sessão
	private MptTipoSessao tipoSessao; // Tipo de Sessão

	/*
	 * Campos Pesquisa Fonética
	 */
	private AipPacientes paciente;

	/*
	 * LISTAS: ALTERAR HORÁRIOS / DIAS AGENDADOS
	 */
	private List<AlterarHorariosVO> listaAlterarHorarios = new ArrayList<AlterarHorariosVO>();
	private AlterarHorariosVO horarioSelecionado;
	private List<DiasAgendadosVO> listaDiasAgendados = new ArrayList<DiasAgendadosVO>();
	private DiasAgendadosVO diaSelecionado;
	private boolean ativarTransferirDia; // Controla a visibiliade do botão Transferir Dia

	/*
	 * MODAIS
	 */

	private static final String ID_MODAL_JUSTIFICATIVA = "modalJustificativaWG";
	private static final String ID_MODAL_REMARCAR_CICLO = "modalRemarcarCicloWG";
	private static final String ID_MODAL_TRANSFERIR_DIA = "modalTransferirDiaWG";

	private TransferirDiaVO transferenciaDia = new TransferirDiaVO(); // Campos da Modal Transferir Dias
	private boolean exclusaoCiclo; // Determina o tipo de exclusão (Ciclo ou Dia)

	/*
	 * Campos da modal de exclusão para Ciclo ou Dia;
	 */
	private ParametrosAgendamentoSessoesVO parametrosAgendamentoSessoes; // Parâmetros da tela para Integração com Estória #41696
	private MptJustificativa motivo;
	private String justificativa;
	
	//Data do Agendamento
	
	private Date dataMapeamento;
	private String dataDetalhada;
	private Boolean possuiReserva = false;

	/**
	 * Chamado ao carregar a tela
	 */
	public void iniciar() {
		if (this.agendaDetalhada) { // Quando chamado pela Agenda Detalhada
			if (this.tpsSeq == null || this.pacCodigo == null) {
				throw new IllegalArgumentException(); // Informar sessão
			}
			// Pesquisa Tipo de Sessão
			this.tipoSessao = this.procedimentoTerapeuticoFacade.obterTipoSessaoPorChavePrimaria(this.tpsSeq);
			processarBuscaPacientePorCodigo(this.pacCodigo); // Pesquisa Paciente
			this.pesquisaPrincipal(); // Executa C1 automaticamente após selecionar Paciente
		}
	}

	/**
	 * Botão voltar
	 */
	public String voltar() {
		String retorno = this.voltarPara;
		this.possuiReserva = false; 
		limparParametrosTela();
		return retorno;
	}

	/**
	 * Limpa parâmetros da tela para conversações
	 */
	public void limparParametrosTela() {
		this.tpsSeq = null;
		this.pacCodigo = null;
		this.agendaDetalhada = false;
		this.voltarPara = null;
		this.tipoSessao = null;
		this.paciente = null;
		limparListas();
	}

	/*
	 * MÉTODOS SELECTONEMENU
	 */

	/**
	 * SelectOneMenu do Tipo de Sessão
	 * 
	 * @return
	 */
	public List<MptTipoSessao> pesquisarTipoSessao() {
		return this.procedimentoTerapeuticoFacade.pesquisarMptTipoSessaoAtivos();
	}

	/*
	 * MÉTODOS SUGGESTIONBOX
	 */

	/**
	 * SuggestionBox do Motivo na Modal de Justificativa
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<MptJustificativa> pesquisarMotivo(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.pesquisarMotivoManutencaoAgendamentoSessaoTerapeutica(strPesquisa), this.procedimentoTerapeuticoFacade.pesquisarMotivoManutencaoAgendamentoSessaoTerapeuticaCount(strPesquisa));
	}

	/*
	 * PESQUISA FONÉTICA
	 */

	/**
	 * Pesquisa Fonética
	 * 
	 * @param event
	 */
	public void pesquisaPaciente(ValueChangeEvent event) {
		try {
			this.paciente = this.pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			this.pesquisaPrincipal(); // Executa C1 automaticamente após selecionar Paciente
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa Fonética: Tratamento feito para volta do botão pesquisa fonética
	 * 
	 * @param codigoPaciente
	 */
	public void processarBuscaPacientePorCodigo(Integer codigoPaciente) {
		if (codigoPaciente != null) {
			this.setPaciente(pacienteFacade.buscaPaciente(codigoPaciente));
			this.pesquisaPrincipal(); // Executa C1 automaticamente após selecionar Paciente
		} else {
			this.paciente = null;
		}
	}

	/**
	 * limpa todos atributos relacionados as listagens
	 */
	public void limparListas() {
		this.listaAlterarHorarios = new ArrayList<AlterarHorariosVO>();
		this.horarioSelecionado = null;
		this.listaDiasAgendados = new ArrayList<DiasAgendadosVO>();
		this.diaSelecionado = null;
		this.ativarTransferirDia = false;
		this.transferenciaDia = new TransferirDiaVO();
		this.exclusaoCiclo = false;
		this.parametrosAgendamentoSessoes = null;
		this.motivo = null;
		this.justificativa = null;
	}

	/*
	 * MÉTODOS LISTAS
	 */

	/**
	 * C1 e C7: Pesquisar Dados do Paciente e Dias
	 */
	private void pesquisaPrincipal() {
		if (this.paciente != null) {
			this.pesquisarAlterarHorariosPrescricoesPaciente();
			if (!this.listaAlterarHorarios.isEmpty()) {
				if (this.agendaDetalhada) {
					for (AlterarHorariosVO vo : this.listaAlterarHorarios) {
						if (vo.getSeqAgendamento().equals(this.agsSeq)) {
							this.horarioSelecionado = vo;
							break;
						}
					}
				} else {
					this.horarioSelecionado = this.listaAlterarHorarios.get(0);
				}
				pesquisarDiasPrescricao(); // Executa C7 automaticamente
			}
		}
	}

	/**
	 * C1: Pesquisar Dados do Paciente (ALTERAR HORÁRIOS)
	 */
	private void pesquisarAlterarHorariosPrescricoesPaciente() {
		try {
			this.listaAlterarHorarios = this.procedimentoTerapeuticoFacade.pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(this.paciente.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	

	/**
	 * C7: Pesquisar Dias Agendados
	 */
	public void pesquisarDiasPrescricao() {
		if (this.horarioSelecionado != null) {
			this.listaDiasAgendados = this.procedimentoTerapeuticoFacade.pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(this.horarioSelecionado.getSeqAgendamento());
			this.ativarTransferirDia = this.procedimentoTerapeuticoFacade.ativarTransferirDia(listaDiasAgendados);
			if(this.dataMapeamento != null){
				this.dataDetalhada = new SimpleDateFormat("dd/MM/yyyy").format(this.dataMapeamento);
			}
			if (this.agendaDetalhada) {
				for (DiasAgendadosVO vo : this.listaDiasAgendados) {
					String dataListaDias = new SimpleDateFormat("dd/MM/yyyy").format(vo.getDataInicio());
					String dataListaDiasFinal = new SimpleDateFormat("dd/MM/yyyy").format(vo.getDataFim());
					if (vo.getSeqAgendamento().equals(this.agsSeq) && (dataListaDias.equals(this.dataDetalhada) || dataListaDiasFinal.equals(this.dataDetalhada))) {
						this.diaSelecionado = vo;
					}
				}
			}
		}
	}

	/*
	 * MÉTODOS EXCLUSÃO DAS LISTA DE HORÁRIOS E DIAS AGENDADOS
	 */

	/**
	 * Justificativa da Exclusão do Ciclo
	 * 
	 * @param ciclo
	 * @throws ApplicationBusinessException 
	 */
	public void chamarModalJustificativaCiclo(final AlterarHorariosVO ciclo) throws ApplicationBusinessException {
		if(ciclo != null){
			this.horarioSelecionado = ciclo;
			this.exclusaoCiclo = true;
			this.popularParametrosIntegracaoAgendamentoSessoes(); // Integração com Estória #41696
			
			if(this.procedimentoTerapeuticoFacade.existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(this.horarioSelecionado.getSeqAgendamento(),this.paciente.getCodigo()) && this.possuiReserva == true){
				this.procedimentoTerapeuticoFacade.removerCicloManutencaoAgendamentoSessaoTerapeutica(this.horarioSelecionado.getSeqAgendamento(), this.horarioSelecionado != null ? this.horarioSelecionado.getSesSeq() : null, this.motivo != null ? this.motivo.getSeq() : null, this.justificativa);
				this.pesquisarAlterarHorariosPrescricoesPaciente(); // Refaz a pesquisa de Horários
				this.pesquisarDiasPrescricao(); // Refaz a pesquisa de dias
				apresentarMsgNegocio("MENSAGEM_SESSAO_MANUTENCAO_EXCLUSAO_CICLO_SUCESSO");
				return;
			} 
			chamarModalJustificativa();
		}
	}

	/**
	 * Justificativa da Exclusão do Dia
	 * 
	 * @param dia
	 */
	public void chamarModalJustificativaDia(final DiasAgendadosVO dia) {
			this.diaSelecionado = dia;
			this.exclusaoCiclo = false;
			
			if(this.procedimentoTerapeuticoFacade.existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(this.diaSelecionado.getSeqAgendamento(), this.paciente.getCodigo()) && this.possuiReserva == true){
				this.procedimentoTerapeuticoFacade.removerDiaManutencaoAgendamentoSessaoTerapeutica(this.diaSelecionado.getSeqHorario(), this.diaSelecionado.getSeqAgendamento(), this.diaSelecionado != null ? this.diaSelecionado.getSesSeq() : null , this.motivo != null ? this.motivo.getSeq() : null, this.justificativa);
				apresentarMsgNegocio("MENSAGEM_SESSAO_MANUTENCAO_EXCLUSAO_SUCESSO");
				this.pesquisarAlterarHorariosPrescricoesPaciente(); // Refaz a pesquisa de Horários
				this.pesquisarDiasPrescricao(); // Refaz a pesquisa de dias
				return;
			} 
			chamarModalJustificativa();
		
	}

	/**
	 * RN06: Integração com Estória #41696 Agendamento (Ao confirmar a Remarcação do Dia)
	 * @throws ApplicationBusinessException 
	 */
	private void popularParametrosIntegracaoAgendamentoSessoes() throws ApplicationBusinessException {

		this.parametrosAgendamentoSessoes = new ParametrosAgendamentoSessoesVO();
		this.parametrosAgendamentoSessoes.setTipoSessao(this.tipoSessao);
		this.parametrosAgendamentoSessoes.setPaciente(this.paciente);
		if(this.procedimentoTerapeuticoFacade.existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(this.horarioSelecionado.getSeqAgendamento(), this.paciente.getCodigo())){
			this.parametrosAgendamentoSessoes.setPrescricao(this.horarioSelecionado.getPteSeq());
		}
		AghParametros paramValidadePrescr = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_VALIDADE_PRECRICAO_QUIMIO);
		Integer validadePrescrQuimio =  paramValidadePrescr.getVlrNumerico().intValue();
		Date dataCalculada = DateUtil.adicionaDias(DateUtil.truncaData(new Date()),-validadePrescrQuimio);
		
		List<PrescricaoPacienteVO> listaPrescricoes = this.procedimentoTerapeuticoFacade
				.obterListaPrescricoesPorPaciente(this.paciente.getCodigo(), dataCalculada);
		if (!listaPrescricoes.isEmpty()) {
				for (PrescricaoPacienteVO vo : listaPrescricoes) {
					if (!vo.getPteSeq().equals(this.horarioSelecionado.getPteSeq())) {
						return;
					}
				}
			}
		
		// Operações no primeiro registro da lista de Dias
		if (!this.listaDiasAgendados.isEmpty()) {

			// Buscar do primeiro registro de C7, pois todos os horários terão o mesmo tipo de acomodação)
			if (this.listaDiasAgendados.get(0).getTipoLocal() != null) {
				this.parametrosAgendamentoSessoes.setAcomodacao(this.listaDiasAgendados.get(0).getTipoLocal().getTipoLocal());
			}

			// Buscar do primeiro registro de C7, pois todos os horários terão a mesma sala).
			this.parametrosAgendamentoSessoes.setSala(this.procedimentoTerapeuticoFacade.obterMptSalaPorChavePrimaria(this.listaDiasAgendados.get(0).getSeqSala()));
		}
	}

	/**
	 * Justificativa da Exclusão do Ciclo ou Dia
	 * 
	 * @param seqAgendamento
	 * @param sesSeq
	 */
	private void chamarModalJustificativa() {
		this.motivo = null;
		this.justificativa = null;
		openDialog(ID_MODAL_JUSTIFICATIVA);
	}

	/*
	 * MÉTODOS MODAIS DE TRANSFERIR DIA E JUSTIFICATIVA DE EXCLUSÃO
	 */

	/**
	 * Grava Justificativa de Exclusão
	 */
	public void gravarJustificativa() {

		if (this.motivo == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, this.getBundle().getString("LABEL_MOTIVO"));
			return;
		}

		if (StringUtils.isBlank(this.justificativa)) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, this.getBundle().getString("LABEL_JUSTIFICATIVA"));
			return;
		}

		if (this.exclusaoCiclo) {
			this.procedimentoTerapeuticoFacade.removerCicloManutencaoAgendamentoSessaoTerapeutica(this.horarioSelecionado.getSeqAgendamento(), this.horarioSelecionado.getSesSeq(), this.motivo.getSeq(), this.justificativa);
			this.pesquisarAlterarHorariosPrescricoesPaciente(); // Refaz a pesquisa de Horários
			this.pesquisarDiasPrescricao(); // Refaz a pesquisa de dias
			openDialog(ID_MODAL_REMARCAR_CICLO);
		} else {
			this.procedimentoTerapeuticoFacade.removerDiaManutencaoAgendamentoSessaoTerapeutica(this.diaSelecionado.getSeqHorario(), this.diaSelecionado.getSeqAgendamento(), this.diaSelecionado.getSesSeq(), this.motivo.getSeq(), this.justificativa);
			apresentarMsgNegocio("MENSAGEM_SESSAO_MANUTENCAO_EXCLUSAO_SUCESSO");
			this.pesquisarDiasPrescricao(); // Refaz a pesquisa de dias
			closeDialog(ID_MODAL_JUSTIFICATIVA);
		}
	}

	/**
	 * Chamada da modal para Transferir Dia
	 * @throws ApplicationBusinessException 
	 */
	public void chamarModalTransferenciaDia(final DiasAgendadosVO dia) throws ApplicationBusinessException {
		this.diaSelecionado = dia;
		this.transferenciaDia = this.procedimentoTerapeuticoFacade.obterLabelRestricaoDatas(this.listaDiasAgendados);
		openDialog(ID_MODAL_TRANSFERIR_DIA);
	}

	/**
	 * RN07: Transfere dia via botão na modal
	 */
	public void transferirDia() {

		if (this.transferenciaDia.getDataTransferencia() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, this.getBundle().getString("LABEL_DATA"));
			return;
		}

		if (this.transferenciaDia.getTurno() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, this.getBundle().getString("LABEL_TURNO"));
			return;
		}

		if (this.transferenciaDia.getAcomodacao() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, this.getBundle().getString("LABEL_ACOMODACAO"));
			return;
		}

		try {
			this.procedimentoTerapeuticoFacade.transferirDia(this.transferenciaDia, this.horarioSelecionado, this.diaSelecionado);
			closeDialog(ID_MODAL_TRANSFERIR_DIA);
			this.pesquisarAlterarHorariosPrescricoesPaciente(); // Refaz a pesquisa de Horários
			this.pesquisarDiasPrescricao(); // Refaz a pesquisa de dias
			apresentarMsgNegocio("MENSAGEM_SESSAO_AGENDA_TRANSFERIDA");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
    }
	/*
	 * GETTERS AND SETTERS
	 */

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public static Log getLog() {
		return LOG;
	}

	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getAgsSeq() {
		return agsSeq;
	}

	public void setAgsSeq(Short agsSeq) {
		this.agsSeq = agsSeq;
	}

	public boolean isAgendaDetalhada() {
		return agendaDetalhada;
	}

	public void setAgendaDetalhada(boolean agendaDetalhada) {
		this.agendaDetalhada = agendaDetalhada;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AlterarHorariosVO> getListaAlterarHorarios() {
		return listaAlterarHorarios;
	}

	public void setListaAlterarHorarios(List<AlterarHorariosVO> listaAlterarHorarios) {
		this.listaAlterarHorarios = listaAlterarHorarios;
	}

	public AlterarHorariosVO getHorarioSelecionado() {
		return horarioSelecionado;
	}

	public void setHorarioSelecionado(AlterarHorariosVO horarioSelecionado) {
		this.horarioSelecionado = horarioSelecionado;
	}

	public List<DiasAgendadosVO> getListaDiasAgendados() {
		return listaDiasAgendados;
	}

	public void setListaDiasAgendados(List<DiasAgendadosVO> listaDiasAgendados) {
		this.listaDiasAgendados = listaDiasAgendados;
	}

	public DiasAgendadosVO getDiaSelecionado() {
		return diaSelecionado;
	}

	public void setDiaSelecionado(DiasAgendadosVO diaSelecionado) {
		this.diaSelecionado = diaSelecionado;
	}

	public boolean isAtivarTransferirDia() {
		return ativarTransferirDia;
	}

	public void setAtivarTransferirDia(boolean ativarTransferirDia) {
		this.ativarTransferirDia = ativarTransferirDia;
	}

	public TransferirDiaVO getTransferenciaDia() {
		return transferenciaDia;
	}

	public void setTransferenciaDia(TransferirDiaVO transferenciaDia) {
		this.transferenciaDia = transferenciaDia;
	}

	public boolean isExclusaoCiclo() {
		return exclusaoCiclo;
	}

	public void setExclusaoCiclo(boolean exclusaoCiclo) {
		this.exclusaoCiclo = exclusaoCiclo;
	}

	public ParametrosAgendamentoSessoesVO getParametrosAgendamentoSessoes() {
		return parametrosAgendamentoSessoes;
	}

	public void setParametrosAgendamentoSessoes(ParametrosAgendamentoSessoesVO parametrosAgendamentoSessoes) {
		this.parametrosAgendamentoSessoes = parametrosAgendamentoSessoes;
	}

	public MptJustificativa getMotivo() {
		return motivo;
	}

	public void setMotivo(MptJustificativa motivo) {
		this.motivo = motivo;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public void setDataMapeamento(Date dataMapeamento) {
		this.dataMapeamento = dataMapeamento;
	}
	
	public Date getDataMapeamento() {
		return dataMapeamento;
	}

	public Boolean getPossuiReserva() {
		return possuiReserva;
	}

	public void setPossuiReserva(Boolean possuiReserva) {
		this.possuiReserva = possuiReserva;
	}

	
}