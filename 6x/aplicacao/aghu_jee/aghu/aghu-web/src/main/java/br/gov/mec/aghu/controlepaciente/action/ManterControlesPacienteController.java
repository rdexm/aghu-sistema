package br.gov.mec.aghu.controlepaciente.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.emergencia.action.ListarControlesPacienteController;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterControlesPacienteController extends ActionController {

	private static final String MODAL_CONFIRMACAO_PENDENCIA_WG = "modalConfirmacaoPendenciaWG";

	private static final long serialVersionUID = 5642636638621951056L;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaController;
	
	@Inject
	private ListarControlesPacienteController listarControlesPacienteController;
	
	private static final String MANTER_CONTROLES_PACIENTE = "manterControlesPaciente";
	private static final String LISTA_PACIENTES_EMERGENCIA = "/pages/emergencia/listaPacientesEmergencia.xhtml";
	private static final String LISTAR_PACIENTES_INTERNADOS = "controlepaciente-listarPacientesInternados"; // para botão voltar default listaPacientes
	
	private EcpControlePaciente controlePaciente;
	private EcpHorarioControle horario;

	private List<SelectItem> listaGrupos;
	private int seqGrupo;
	private EcpGrupoControle grupoSelecionado = new EcpGrupoControle();
	private List<EcpGrupoControle> grupos = new ArrayList<EcpGrupoControle>();

	private boolean exibeGrupo = false;
	private boolean iniciouTela = false;

	// paciente e leito
	private AinLeitos leito;
	private AipPacientes paciente;

	// informações do Atendimento
	private Integer atendimentoSeq;
	
	private Long trgSeq;
	private Integer pacCodigo;
	private Short unfSeq;

	private Long horarioSeq;
	private AghAtendimentos atendimento;

	private Date horaControle;
	private Date dataControle;

	private List<EcpControlePaciente> controlesPaciente;

	// controle a ser exlcuido
	private long horarioExcluirSeq;

	private String voltarPara; 

	private String textoTooltip;
	
	private boolean confirmaPendencias;
	private boolean modificouTela;
	
	private Integer codigoPacienteVisualizar;

	private static enum Acao {
		APAGAR, RELOGIO, GRUPO, VOLTAR, ATUALIZA_LISTA_DATA, ATUALIZA_LISTA_HORA
	};
	
	private Acao acao = null;
	private EcpGrupoControle grupoAnteriorSelecionado;
	private Date horaControleAntiga;
	private Date dataControleAntiga;
	
	private Integer cid;
	
	private static final Log LOG = LogFactory.getLog(ManterControlesPacienteController.class);
	private Boolean fromAmbulatorio;
	private String labelZona;
	private VAacSiglaUnfSalaVO zona;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciar() {
		if(!iniciouTela){
			iniciouTela = true;
			horario = null;
			
			this.listaGrupos = this.listarGruposAtivos();
			if (atendimentoSeq != null) {
				this.atendimento = this.aghuFacade.obterAtendimentoPeloSeq(atendimentoSeq);

				if (atendimento == null) {
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return voltar();
				}
			}
			//Programação #8572 Editar Controles a partir da matriz de Visualização
			if (this.horarioSeq != null) {
				this.horario = controlePacienteFacade.obterHorarioPeloId(horarioSeq.longValue());

				if (horario == null) {
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return voltar();
				}
			}
			carregarAtendimento();
			if (this.horario == null) {
				criaNovoHorario(null);
				this.controlesPaciente = listarControles();
			} else {
				this.atualizaLista();
			}
			modificouTela = false;
			setConfirmaPendencias(false);
			if(this.horarioSeq == null && trgSeq != null) { // Se não for edicao e vier da emergencia carrega tela com horario atual selecionado
				verificaPendenciasHoraAtual();
			}
		}
		
		return null;
	
	}


    public SelectItem[] getDescricaoDominioSimNao() {

        SelectItem[] items = new SelectItem[DominioSimNao.values().length];
        int i = 0;
        for(DominioSimNao g: DominioSimNao.values()) {
            items[i++] = new SelectItem(g, g.getDescricao());
        }
        return items;
    }

	/**
	 * Retorna lista de itens de determinada data + hora + paciente
	 */
	public List<EcpControlePaciente> listarControles() {
		this.modificouTela = false;
		setConfirmaPendencias(false);
		
		List<EcpControlePaciente> result = new ArrayList<EcpControlePaciente>();
		List<EcpControlePaciente> existentes = new ArrayList<EcpControlePaciente>();
		EcpControlePaciente controleAdicionar = null;
		boolean achou;
		Date dataHoraMontada = this.montaDataHora(this.horaControle,this.dataControle);
		
		// busca um horario de paciente com esta data
		
		EcpHorarioControle horario = this.controlePacienteFacade.obterHorariopelaDataHora(paciente, dataHoraMontada);
		
		if (horario != null && horario.getSeq() != null) {
			this.horario = horario;
			existentes = this.controlePacienteFacade.listaControlesHorario(horario, grupoSelecionado);
		} else {
			criaNovoHorario(dataHoraMontada);
		}
		
		for (EcpItemControle item : cadastrosBasicosControlePacienteFacade.listarItensControleAtivos(grupoSelecionado)) {
			achou = false;
			if (!existentes.isEmpty()) {
				for (EcpControlePaciente cont : existentes) {
					if (item.equals(cont.getItem())) {
						controleAdicionar = cont;
						achou = true;
					}
				}
			}
			
			if (!achou) {
				controleAdicionar = new EcpControlePaciente();
				controleAdicionar.setHorario(this.horario);
				controleAdicionar.setItem(item);
			}
			result.add(controleAdicionar);
		}
		return result;
	}

	// chamado quando troca data ou hora sempre ira posicionar no 1o grupo
	public void atualizaLista() {
		this.grupoSelecionado = this.grupos.get(0);
		this.seqGrupo = this.grupoSelecionado.getSeq();
		this.controlesPaciente = this.listarControles();
		if (this.horaControle != null) {
			exibeGrupo = true;
		} else {
			exibeGrupo = false;
		}
	}
	
	public void verificaPendenciasAtualizarListaHora() {
		setConfirmaPendencias(modificouTela);
		atualizaDataHora();
		if (isConfirmaPendencias()) {
			this.acao = Acao.ATUALIZA_LISTA_HORA;
			this.openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
		} else {
			this.atualizaLista();	
		}
	}

	public void verificaPendenciasAtualizarListaData() {
		setConfirmaPendencias(modificouTela);
		atualizaDataHora();
		if (isConfirmaPendencias()) {
			this.acao = Acao.ATUALIZA_LISTA_DATA;
			this.openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
		} else {
			this.atualizaLista();	
		}
	}
	
	private void atualizaDataHora() {
		Date dataHoraFormatada = this.montaDataHora(this.horaControle,
				this.dataControle);
		if (horario == null) {
			criaNovoHorario(dataHoraFormatada);
		} else {
			horario.setDataHora(dataHoraFormatada);
		}
	}	
	
	// chamado quando troca o grupo
	public void atualizaListaDoGrupo() {
		this.controlesPaciente = this.listarControles();
	}
	
	public void verificaPendenciasAtualizarListaDoGrupo() {
		setConfirmaPendencias(modificouTela);
		if (isConfirmaPendencias()) {
			this.acao = Acao.GRUPO;
			this.openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
		} else { 
			atualizaListaDoGrupo();
		}
	}
	
	public String selecionouPaciente() {
		Date agora = new Date();
		criaNovoHorario(agora);
		this.dataControle = agora;
		this.controlePaciente = new EcpControlePaciente();
		
		return MANTER_CONTROLES_PACIENTE;
	}

	public void gravarEmergencia() {
		try {
			String anot = this.horario.getAnotacoes();
			horario.setAnotacoes(StringUtils.trimToNull(anot));
			horario.setTrgSeq(trgSeq);
			if(trgSeq != null && unfSeq != null) {
				horario.setPaciente(paciente);
				horario.setUnidadeFuncional(aghuFacade.obterUnidadeFuncional(unfSeq));
			}
			this.controlePacienteFacade.gravar(this.horario, this.controlesPaciente);

			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_CONTROLES", grupoSelecionado.getDescricao(), horario.getHoraMinuto(), this.paciente.getNome());

			atualizaListaDoGrupo();
			mostraMensagensForaNormalidade();

			this.modificouTela = false;
			setConfirmaPendencias(false);
		} catch (BaseException e) {
			if (e instanceof BaseListException) {
				apresentarExcecaoNegocio((BaseListException)e);
			} else {
				apresentarExcecaoNegocio(e);
			}
		}		
	}
	
	
	public void gravar() {
		try {
            String anot = this.horario.getAnotacoes();
            horario.setAnotacoes(StringUtils.trimToNull(anot));
            horario.setTrgSeq(trgSeq);
            if (trgSeq != null && unfSeq != null) {
                horario.setPaciente(paciente);
                horario.setUnidadeFuncional(aghuFacade.obterUnidadeFuncional(unfSeq));
            }
            this.controlePacienteFacade.gravar(this.horario, this.controlesPaciente);

            apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_CONTROLES", grupoSelecionado.getDescricao(), horario.getHoraMinuto(),
                    this.paciente.getNome());

            atualizaListaDoGrupo();
            mostraMensagensForaNormalidade();

            //Rotina que grava contigência de internação e coloca na fila para geração de pdf chama package no banco oracle - André Luiz Machado - 18/01/2012
            if (trgSeq == null) {
                dispararGeracaoContigencia();
            }

            this.modificouTela = false;
            setConfirmaPendencias(false);

        } catch (BaseListException e) {
            apresentarExcecaoNegocio(e);
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
         }
	}
   
	/**
	 * Rotina que grava contigência de internação e coloca na fila para geração de pdf
	 * chama package no banco oracle - André Luiz Machado - 11/01/2012
	 */
	private void dispararGeracaoContigencia() throws ApplicationBusinessException {
		this.controlePacienteFacade.dispararGeracaoContigencia(this.horario);
	}
	
	public void mostraMensagensForaNormalidade() throws ApplicationBusinessException {
		this.modificouTela = false;
		setConfirmaPendencias(false);
		// mostra mensagem item fora do limite da normalidade
		for (EcpControlePaciente controle : this.controlesPaciente) {
			String msg = this.controlePacienteFacade.mensagemItemForaLimitesNormalidade(this.horario, controle);
			if (StringUtils.isNotBlank(msg)) {
				apresentarMsgNegocio(Severity.WARN, msg);
				controle.setTextoItemForaNormalidade(msg);
			}
		}
	}

	public void montaTextoTooltip(Long seq) {
		String result = "";
		EcpControlePaciente controle = this.controlePacienteFacade
				.obterControlePeloId(seq);
		try {
			result = this.controlePacienteFacade.mensagemItemForaLimitesNormalidade(controle.getHorario(),controle);
			this.textoTooltip = result;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicaoEmergencia() {
		limparAtendimento();
		iniciouTela = false;
		
		if (voltarPara.equals("listarPacientesEmergenciaAbaAcolhimento")) {
			this.listaPacientesEmergenciaController.setAbaSelecionada(1);
			this.listaPacientesEmergenciaController.pesquisarPacientesAcolhimento();
			this.redirecionarPaginaPorAjax(LISTA_PACIENTES_EMERGENCIA);
		} else if (voltarPara.equals("listarPacientesEmergenciaAbaAguardando")) {
			this.listaPacientesEmergenciaController.setAbaSelecionada(2);
			this.listaPacientesEmergenciaController.pesquisarPacientesAguardandoAtendimento();
			this.listarControlesPacienteController.setPacCodigo(pacCodigo);
			this.listarControlesPacienteController.setTrgSeq(trgSeq);
			this.listarControlesPacienteController.buscaMonitorizacoes();
			this.redirecionarPaginaPorAjax(LISTA_PACIENTES_EMERGENCIA);
		} else {
			this.listarControlesPacienteController.setPacCodigo(pacCodigo);
			this.listarControlesPacienteController.setTrgSeq(trgSeq);
			this.listarControlesPacienteController.buscaMonitorizacoes();
			this.redirecionarPaginaPorAjax("/pages/emergencia/realizarAcolhimentoPacienteCRUD.xhtml");
		}
	}
	
	private void redirecionarPaginaPorAjax(String caminhoPagina){
		try{
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, caminhoPagina)); 
			extContext.redirect(url);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);		
		}
	}	

	private void criaNovoHorario(Date dataHora) {
		this.horario = new EcpHorarioControle();
		this.horario.setAtendimento(atendimento);
		this.horario.setDataHora(dataHora);
		this.horario.setTrgSeq(trgSeq);
	}

	private Date montaDataHora(Date hora, Date data) {

		if (hora != null && data != null) {
			// Pega o horário
			Calendar calendarioOrigemHora = Calendar.getInstance();
			calendarioOrigemHora.setTime(hora);

			// Pega a data
			Calendar calendarioDestinoData = Calendar.getInstance();
			calendarioDestinoData.setTime(data);

			// Relaciona a hora com a data
			calendarioDestinoData.set(Calendar.HOUR_OF_DAY,calendarioOrigemHora.get(Calendar.HOUR_OF_DAY));
			calendarioDestinoData.set(Calendar.MINUTE, calendarioOrigemHora.get(Calendar.MINUTE));
			calendarioDestinoData.set(Calendar.SECOND, 0);
			calendarioDestinoData.set(Calendar.MILLISECOND, 0);

			// retorna data e hora
			return (calendarioDestinoData.getTime());
		}
		return null;
	}

	public List<SelectItem> listarGruposAtivos() {
		List<SelectItem> lista = new LinkedList<SelectItem>();
		List<EcpGrupoControle> todos = this.cadastrosBasicosControlePacienteFacade.listarGruposControleAtivos();
		List<EcpItemControle> itens;
		
		for (EcpGrupoControle grupo : todos) {
			// caso o paciente esteja em consulta ou acolhimento da emergencia deve listar apenas grupos do tipo monitorizacao
			if((trgSeq != null && DominioTipoGrupoControle.MN.equals(grupo.getTipo())) || trgSeq == null) {
				// veirifica se o grupo possui itens ativos
				itens = this.cadastrosBasicosControlePacienteFacade.listarItensControleAtivos(grupo);
				
				if (!itens.isEmpty()) {
					lista.add(new SelectItem(grupo.getSeq(), grupo.getDescricao()));
					grupos.add(grupo);
					// seleciona o primeiro grupo e monta sua lista de itens...
					if (this.grupoSelecionado == null || this.grupoSelecionado.getSeq() == null) {
						this.grupoSelecionado = grupo;
						this.seqGrupo = grupo.getSeq();
					}
				}
			}
		}
		return lista;
	}

	public void changeComboGrupo(ValueChangeEvent event) {
		Integer seq = null;
		if (event != null && event.getNewValue() != null) {
			seq = (Integer) event.getNewValue();
		}
		if (seq != null) {
			EcpGrupoControle grupoControle = this.cadastrosBasicosControlePacienteFacade.obterGrupoControle(seq);
			if (grupoControle != null) {
				this.grupoAnteriorSelecionado = this.grupoSelecionado;
				this.grupoSelecionado = grupoControle;
			}
		}
	}
	
	public String desfazerAlteracoes() {
		if (this.horarioSeq == null) {
			criaNovoHorario(null);
			this.horaControle = null;
			this.dataControle = new Date();
			this.controlesPaciente = this.listarControles();
			this.exibeGrupo = false;
		} else {
			this.horario = controlePacienteFacade.obterHorarioPeloId(horarioSeq.longValue());
			setDataControle(horario.getDataHora());
			setHoraControle(horario.getDataHora());
			atualizaLista();
		}
		modificouTela = false;
		setConfirmaPendencias(false);
		return MANTER_CONTROLES_PACIENTE;
	}

	public void horaAtual() {
		setHoraControle(new Date());
		atualizaLista();
	}
	
	public void verificaPendenciasHoraAtual() {
		setConfirmaPendencias(modificouTela);
		if (isConfirmaPendencias()) {
			this.acao = Acao.RELOGIO;
			openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
		} else { 	
			this.horaAtual();
		}
	}

	public String limparAtendimento() {
		this.atendimento = null;
		this.leito = null;
		this.paciente = null;
		this.horario = null;
		this.controlePaciente = null;
		this.horaControle = null;
		this.horaControleAntiga = null;
		Date agora = new Date();
		this.dataControle = new Date(agora.getTime());
		this.dataControleAntiga = new Date(agora.getTime()); 
		this.grupoSelecionado = this.grupos.get(0);
		this.seqGrupo = this.grupoSelecionado.getSeq();
		exibeGrupo = false;
		horarioSeq = null;
		atendimentoSeq = null;
		atualizaLista();
		return MANTER_CONTROLES_PACIENTE;
	}
	
	public String verificaPendenciasLimparAtendimento() {
		setConfirmaPendencias(modificouTela);
		if (isConfirmaPendencias()) {
			this.acao = Acao.APAGAR;
			this.openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
			return MANTER_CONTROLES_PACIENTE;
		}
		return limparAtendimento();
	}

	public String voltar() {
		limparAtendimento();
		iniciouTela = false;
		
		if (this.paciente != null) {
			codigoPacienteVisualizar = paciente.getCodigo();
		}
		
		if(voltarPara != null){
			return voltarPara;
		} else {
			return LISTAR_PACIENTES_INTERNADOS;
		}
	}
	
	public void definirModificaoTela() {
		this.modificouTela = true;
	}
	
	public void definirModificacaoTelaFalse() {
		this.modificouTela = false;
		setConfirmaPendencias(false);
	}
	
	public String verificaPendenciasVoltar() {
		setConfirmaPendencias(modificouTela);
		if (isConfirmaPendencias()) {
			this.acao = Acao.VOLTAR;
			this.openDialog(MODAL_CONFIRMACAO_PENDENCIA_WG);
			return null;
		}
		return voltar();
	}
	
	public String executarAcaoConfirmada() {
		if (acao != null) { 
			switch (acao) {
				case APAGAR: 				limparAtendimento(); 	break;
				case GRUPO:  				atualizaListaDoGrupo();	break;
				case RELOGIO: 				horaAtual();			break;
				
				case ATUALIZA_LISTA_DATA:
				case ATUALIZA_LISTA_HORA: 	atualizaLista();		break;
				case VOLTAR:				return voltar();
				default:					break;
			}
		}
		acao = null;
		modificouTela = false;
		setConfirmaPendencias(false);
		return MANTER_CONTROLES_PACIENTE;
	}
	
	public String cancelarAcaoPendente() {
		if (acao != null) { 
			switch (acao) {
				case APAGAR:
					if(atendimento != null) {
						this.paciente = this.atendimento.getPaciente();
						this.leito = this.atendimento.getLeito();
					} else if(trgSeq != null && pacCodigo != null) {
						this.paciente = this.pacienteFacade.obterPaciente(pacCodigo);
					}
					break;
				case GRUPO:
					this.grupoSelecionado = this.grupoAnteriorSelecionado;
					this.seqGrupo = grupoSelecionado.getSeq();
					break;
				case RELOGIO:
					break;
				case ATUALIZA_LISTA_DATA:
					this.dataControle = dataControleAntiga;
					break;
				case ATUALIZA_LISTA_HORA:
					this.horaControle = horaControleAntiga;
					break;
				case VOLTAR:
				default: break;
			}
		}
		acao = null;
		setConfirmaPendencias(false);
		return MANTER_CONTROLES_PACIENTE;
	}

	public List<AipPacientes> pesquisarPaciente(String objParam)
			throws ApplicationBusinessException {
		String strPesquisa = (String) objParam;
		Integer prontuario = null;
		AghAtendimentos atendimento = null;
		if (!StringUtils.isBlank(strPesquisa)) {
			prontuario = Integer.valueOf(strPesquisa);
			atendimento = prescricaoMedicaFacade.obterAtendimentoPorProntuario(prontuario);
			if (atendimento != null) {
				this.setAtendimento(atendimento);
				this.carregarAtendimento();
			}
		}

		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		if (atendimento != null) {
            paciente =atendimento.getPaciente();
            pacientes.add(paciente);

		}
		return pacientes;
	}

	public List<AinLeitos> pesquisarLeito(String objParam)
			throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		try {
			String strPesquisa = (String) objParam;
			if (!StringUtils.isBlank(strPesquisa)) {
				strPesquisa = strPesquisa.toUpperCase();
			}
			AghAtendimentos atendimento = prescricaoMedicaFacade
					.obterAtendimentoPorLeito(strPesquisa);

			this.setAtendimento(atendimento);
			this.carregarAtendimento();
            leito = atendimento.getLeito();
			leitos.add(leito);
			return leitos;

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}

	}

	private void carregarAtendimento() {
		if (this.atendimento != null && this.atendimento.getSeq() != null) {
			this.paciente = this.atendimento.getPaciente();
			this.leito = this.atendimento.getLeito();
		} else if(trgSeq != null && pacCodigo != null) {
			this.paciente = this.pacienteFacade.obterPaciente(pacCodigo);
		}
		if (this.horario == null) {
			Date agora = new Date();
			criaNovoHorario(agora);
			setDataControle(agora);
			this.controlePaciente = new EcpControlePaciente();
		} else {
			setDataControle(horario.getDataHora());
			setHoraControle(horario.getDataHora());
		}
	}

	public void excluirControle(Long controleExcluirSeq) {
		EcpControlePaciente controleExcluir = this.controlePacienteFacade.obterControlePeloId(controleExcluirSeq);
		
		if (controleExcluir != null) {
			String desc = controleExcluir.getItem().getDescricaoEditada();
			EcpHorarioControle horarioControle = controleExcluir.getHorario();
			try {
				this.controlePacienteFacade.excluir(controleExcluir);
				// quando é o último controle o sistema exclui o horário) -> se o
				// horário não existe mais, prepara um novo horário para inclusão
				horarioControle = this.controlePacienteFacade.obterHorarioPeloId(horarioControle.getSeq());
				if (horarioControle == null) {
					criaNovoHorario(null);
					setHoraControle(null);
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_CONTROLE_COM_HORARIO", desc);
				} else {
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_CONTROLE", desc);
				}
				this.atualizaListaDoGrupo();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
			}
		}
	}

	public void excluirHorario() {
		EcpHorarioControle horarioExcluir = this.controlePacienteFacade.obterHorarioPeloId(horarioExcluirSeq);
		if (horarioExcluir != null) {
			String horaMin = horarioExcluir.getHoraMinuto();
			
			try {
				this.controlePacienteFacade.excluir(horarioExcluir);
				criaNovoHorario(null);
				setHoraControle(null);
				this.atualizaLista();
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_HORARIO", horaMin);

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
			}
		}
	}

	// getters and setters
	public EcpControlePaciente getControlePaciente() {
		return controlePaciente;
	}

	public void setControlePaciente(EcpControlePaciente controlePaciente) {
		this.controlePaciente = controlePaciente;
	}

	public EcpHorarioControle getHorario() {
		return horario;
	}

	public void setHorario(EcpHorarioControle horario) {
		this.horario = horario;
	}

	public List<SelectItem> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<SelectItem> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public int getSeqGrupo() {
		return seqGrupo;
	}

	public void setSeqGrupo(int seqGrupo) {
		this.seqGrupo = seqGrupo;
	}

	public EcpGrupoControle getGrupoSelecionado() {
		return grupoSelecionado;
	}

	public void setGrupoSelecionado(EcpGrupoControle grupoSelecionado) {
		this.grupoSelecionado = grupoSelecionado;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Date getHoraControle() {
		return horaControle;

	}

	public void setHoraControle(Date horaControleNova) {
		this.horaControleAntiga = (this.horaControle == null ? null : new Date(horaControle.getTime()));
		this.horaControle = horaControleNova;
	}

	public Date getDataControle() {
		return dataControle;
	}

	public void setDataControle(Date dataControleNova) {
		this.dataControleAntiga = (this.dataControle == null ? null : new Date(dataControle.getTime()));
		this.dataControle = dataControleNova;
	}

	public Long getHorarioSeq() {
		return horarioSeq;
	}

	public void setHorarioSeq(Long horarioSeq) {
		this.horarioSeq = horarioSeq;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<EcpControlePaciente> getControlesPaciente() {
		return controlesPaciente;
	}

	public void setControlesPaciente(List<EcpControlePaciente> controlesPaciente) {
		this.controlesPaciente = controlesPaciente;
	}

	public boolean isExibeGrupo() {
		return exibeGrupo;
	}

	public void setExibeGrupo(boolean exibeGrupo) {
		this.exibeGrupo = exibeGrupo;
	}

	public long getHorarioExcluirSeq() {
		return horarioExcluirSeq;
	}

	public void setHorarioExcluirSeq(long horarioExcluirSeq) {
		this.horarioExcluirSeq = horarioExcluirSeq;
	}

	public String getTextoTooltip() {
		return textoTooltip;
	}

	public void setTextoTooltip(String textoTooltip) {
		this.textoTooltip = textoTooltip;
	}

	public String getTelaOrigem() {
		return voltarPara;
	}

	public void setTelaOrigem(String telaOrigem) {
		this.voltarPara = telaOrigem;
	}

	public void setConfirmaPendencias(boolean confirmaPendencias) {
		this.confirmaPendencias = confirmaPendencias;
	}

	public boolean isConfirmaPendencias() {
		return confirmaPendencias;
	}

	public Integer getCodigoPacienteVisualizar() {
		return codigoPacienteVisualizar;
	}

	public void setCodigoPacienteVisualizar(Integer codigoPacienteVisualizar) {
		this.codigoPacienteVisualizar = codigoPacienteVisualizar;
	}

	public boolean isModificouTela() {
		return modificouTela;
	}

	public void setModificouTela(boolean modificouTela) {
		this.modificouTela = modificouTela;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public Boolean getFromAmbulatorio() {
		return fromAmbulatorio;
	}

	public void setFromAmbulatorio(Boolean fromAmbulatorio) {
		this.fromAmbulatorio = fromAmbulatorio;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public VAacSiglaUnfSalaVO getZona() {
		return zona;
	}

	public void setZona(VAacSiglaUnfSalaVO zona) {
		this.zona = zona;
	}
}