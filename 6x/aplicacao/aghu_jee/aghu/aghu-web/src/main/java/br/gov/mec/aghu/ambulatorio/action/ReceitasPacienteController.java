package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;

public class ReceitasPacienteController extends ActionController {

	private static final long serialVersionUID = 4741798407953603408L;
	private static final Log LOG = LogFactory.getLog(ReceitasPacienteController.class);
	private static final String LABEL_REGISTRO_EXCLUIDO = "LABEL_REGISTRO_EXCLUIDO";
	private static final String MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO = "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO";
	private static final String DESCR_FORMULA = "FÓRMULA:";
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IPermissionService permissionService;
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;
	private boolean permissaoAbaCuidados;
	private String loginUsuario;
	private Integer selectedTab;
	private String cameFrom;
	private String labelZona;
	private Boolean readonlyReceitaGeral;
	private Boolean readonlyReceitaEspecial;
	private Integer numeroConsulta;
	private AacConsultas consultaSelecionada;
	private MamReceituarios receitaGeral;
	private MamReceituarios receitaEspecial;
	private MamItemReceituario itemReceitaGeral;
	private MamItemReceituario itemReceitaEspecial;
	private List<MamItemReceituario> itemReceitaGeralList;
	private List<MamItemReceituario> itemReceitaEspecialList;
	private VAfaDescrMdto descricaoMedicamento;
	private VAfaDescrMdto descricaoMedicamentoEspecial;
	private Integer viasGeral;
	private Integer viasEspecial;
	private String validadeUso;
	private DominioMotivoPendencia motivoPendencia;
	private Boolean mostrarModalImpressao;
	private String finalizarDescricao;
	private String descricaoReceitaGeral;
	private String descricaoReceitaEspecial;
	private Boolean habilitaFinalizar;
	private Boolean voltarListaPacientes = false;
	private boolean confirmaValidade = false;
	private Integer sliderAtual;
	private Boolean habilitaReceita = false;
	private Boolean gravaReceita = false;
	private String banco = null;
	private String urlBaseWebForms = null;
	private Boolean isHcpa = false;
	private Boolean isUbs = false;		
	private RapServidores servidorLogadoSemFimVinculo;	
	@Inject
	private BuscarReceitasController buscarReceitasController;
	@Inject
	private CadastroCuidadosPacienteController cadastroCuidadosPacienteController;
	private static final String BUSCAR_RECEITAS = "ambulatorio-buscarReceitas";
	private static final String RECEITAS_PACIENTE = "ambulatorio-receitasPaciente";
	private static final String LISTA_PACIENTES_EMERGENCIA = "emergencia-listaPacientesEmergencia";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String voltar(){
		if ("emergencia-pacientesEmergenciaAbaEmAtendimento".equals(this.cameFrom)) {
			listaPacientesEmergenciaPaginatorController.setAbaSelecionada(3);
			listaPacientesEmergenciaPaginatorController.pesquisarPacientesEmAtendimento();
			return LISTA_PACIENTES_EMERGENCIA;
		}
		else {
			return cameFrom;
		}
	}
	
	public void iniciar() {
		loginUsuario =this.obterLoginUsuarioLogado();
		permissaoAbaCuidados = this.permissionService.usuarioTemPermissao(loginUsuario, "realizarCuidadosAmbulatorio","executar");
		processarMicroEUserLogado();
		processarReceituarios();
		mostrarModalImpressao = false;
		finalizarDescricao = "";
		iniciaReceitas();
		this.motivoPendencia = DominioMotivoPendencia.POS;
		this.carregarParametros();
		cadastroCuidadosPacienteController.setConsultaSelecionada(consultaSelecionada);
		cadastroCuidadosPacienteController.iniciar();
	}
	
	private void processarReceituarios() {
		if(consultaSelecionada != null) {
			List<MamReceituarios> receituarios = this.ambulatorioFacade.pesquisarReceituariosPorConsulta(consultaSelecionada);
			consultaSelecionada.setReceituarios(new HashSet<MamReceituarios>(receituarios));
		}
	}

	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}	
	
	private void processarMicroEUserLogado() {
		try {
			super.getEnderecoRedeHostRemoto();
			servidorLogadoSemFimVinculo = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			if (this.labelZona == null) {
				this.labelZona = "Zona";
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
		}
	}

	private void iniciaReceitas() {
		receitaGeral = new MamReceituarios();
		receitaEspecial = new MamReceituarios();
		novoItemReceitaGeral();
		novoItemReceitaEspecial();
		descricaoMedicamento = null;
		descricaoMedicamentoEspecial = null;
		itemReceitaGeralList = new ArrayList<MamItemReceituario>();
		itemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		descricaoReceitaGeral = "";
		descricaoReceitaEspecial = "";
		viasGeral = 2;
		viasEspecial = 2;
		validadeUso = null;
		try {		
				if (consultaSelecionada.getReceituarios() != null
						&& !consultaSelecionada.getReceituarios().isEmpty()) {
					MamReceituarios receituarioGeral = ambulatorioFacade
								.primeiroReceituarioPorConsultaETipo(consultaSelecionada.getNumero(), DominioTipoReceituario.G);
				if (receituarioGeral != null) {
					receitaGeral = receituarioGeral;
					if (receitaGeral.getPendente().equals(DominioIndPendenteAmbulatorio.V) && !receitaGeral.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
						readonlyReceitaGeral = true;
					}
					itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
					viasGeral = receitaGeral.getNroVias() != null ? receitaGeral.getNroVias().intValue() : null;
				}

				MamReceituarios receituarioEspecial = this.ambulatorioFacade.primeiroReceituarioPorConsultaETipo(this.consultaSelecionada.getNumero(), DominioTipoReceituario.E);
				if (receituarioEspecial != null) {
					receitaEspecial = receituarioEspecial;
					if (receitaEspecial.getPendente().equals(DominioIndPendenteAmbulatorio.V) && !receitaEspecial.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
						readonlyReceitaEspecial = true;
					}
					itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
					viasEspecial = receitaEspecial.getNroVias() != null ? receitaEspecial.getNroVias().intValue() : null;
				}
			}
			this.habilitaReceita = this.ambulatorioFacade.validarProcessoConsultaReceita();
			this.gravaReceita = this.ambulatorioFacade.validarProcessoExecutaReceita();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void novoItemReceitaGeral() {
		itemReceitaGeral = new MamItemReceituario();
		itemReceitaGeral.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		itemReceitaGeral.setIndInterno(DominioSimNao.S);
	}

	private void novoItemReceitaEspecial() {
		itemReceitaEspecial = new MamItemReceituario();
		itemReceitaEspecial.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		itemReceitaEspecial.setIndInterno(DominioSimNao.S);
	}

	public void atualizaReceituarioGeral() {
		atualizaReceituarioGeral(false);
	}

	public void atualizaReceituarioGeral(boolean insereItemJunto) {
		List<MamItemReceituario> novoItemReceitaGeralList = new ArrayList<MamItemReceituario>();
		if (!insereItemJunto && itemReceitaGeralList.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_INSIRA_ITENS_RECEITA");
			return;
		}
		try {
			novoItemReceitaGeralList = this.ambulatorioFacade.atualizarReceituarioGeral(receitaGeral, this.consultaSelecionada, viasGeral, itemReceitaGeralList);
			if (!novoItemReceitaGeralList.isEmpty()) {
				itemReceitaGeralList = novoItemReceitaGeralList;
			}
			apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_ALTERACAO_ITEM_RECEITA");
		}
	}

	public void atualizaReceituarioEspecial() {
		atualizaReceituarioEspecial(false);
	}

	public void atualizaReceituarioEspecial(boolean insereItemJunto) {
		List<MamItemReceituario> novoItemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		if (!insereItemJunto && itemReceitaEspecialList.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_INSIRA_ITENS_RECEITA");
		}
		try {
			novoItemReceitaEspecialList = this.ambulatorioFacade.atualizarReceituarioEspecial(receitaEspecial, this.consultaSelecionada, viasEspecial, itemReceitaEspecialList);
			if (!novoItemReceitaEspecialList.isEmpty()) {
				itemReceitaEspecialList = novoItemReceitaEspecialList;
			}
			apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_ALTERACAO_ITEM_RECEITA");
		}
	}

	public void excluirReceituarioEspecial() {
		try {
			this.ambulatorioFacade.excluirReceituarioEspecial(receitaEspecial, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Receitas Especiais");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void excluirReceituarioGeral() {
		try {
			this.ambulatorioFacade.excluirReceituarioGeral(receitaGeral, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Receitas Gerais");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void inserirReceitaGeral() {
		Boolean receituarioValidado = false;
		Short itemReceitaGeralSeqp = null;
		if (StringUtils.isEmpty(descricaoReceitaGeral)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_DESCRICAO_RECEITA", "descricaoReceita_tab1");
			return;
		}
		try {
			if (receitaGeral.getPendente() != null && receitaGeral.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
				receituarioValidado = true;
				if (itemReceitaGeral.getId() != null) {
					itemReceitaGeralSeqp = itemReceitaGeral.getId().getSeqp();
				}
			}
			this.ambulatorioFacade.validaValidadeItemReceitaEmMeses(itemReceitaGeral);
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaGeral);
			atualizaReceituarioGeral(true);
			if (receituarioValidado && itemReceitaGeralSeqp != null) {
				for (MamItemReceituario itemReceita : itemReceitaGeralList) {
					if (itemReceita.getId().getSeqp().equals(itemReceitaGeralSeqp)) {
						itemReceitaGeral = itemReceita;
						receitaGeral = itemReceitaGeralList.get(0).getReceituario();
						break;
					}
				}
			} else if (receituarioValidado && itemReceitaGeralSeqp == null) {
				receitaGeral = itemReceitaGeralList.get(0).getReceituario();
			}
			itemReceitaGeral.setDescricao(descricaoReceitaGeral);
			if (itemReceitaGeral.getId() == null) {
				itemReceitaGeral.setIndSituacao(DominioSituacao.A);
				itemReceitaGeral.setReceituario(receitaGeral);
				Integer ordem = itemReceitaGeralList.size() + 1;
				itemReceitaGeral.setOrdem(ordem.byteValue());
				this.ambulatorioFacade.inserirItem(receitaGeral, itemReceitaGeral);
			} else {
				this.ambulatorioFacade.atualizarItem(itemReceitaGeral);
			}
			novoItemReceitaGeral();
			descricaoReceitaGeral = "";
			processarReceituarios();
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void inserirReceitaEspecial() {
		Boolean receituarioValidado = false;
		Short itemReceitaEspecialSeqp = null;
		if (StringUtils.isEmpty(descricaoReceitaEspecial)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_DESCRICAO_RECEITA", "descricaoReceita_tab2");
			return;
		}
		try {
			this.ambulatorioFacade.validaValidadeItemReceitaEmMeses(itemReceitaEspecial);
			if (receitaEspecial.getPendente() != null && receitaEspecial.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
				receituarioValidado = true;
				if (itemReceitaEspecial.getId() != null) {
					itemReceitaEspecialSeqp = itemReceitaEspecial.getId().getSeqp();
				}
			}
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaEspecial);
			atualizaReceituarioEspecial(true);
			if (receituarioValidado && itemReceitaEspecialSeqp != null) {
				for (MamItemReceituario itemReceita : itemReceitaEspecialList) {
					if (itemReceita.getId().getSeqp().equals(itemReceitaEspecialSeqp)) {
						itemReceitaEspecial = itemReceita;
						receitaEspecial = itemReceitaEspecialList.get(0).getReceituario();
						break;
					}
				}
			} else if (receituarioValidado && itemReceitaEspecialSeqp == null) {
				receitaEspecial = itemReceitaEspecialList.get(0).getReceituario();
			}
			itemReceitaEspecial.setDescricao(descricaoReceitaEspecial);
			if (itemReceitaEspecial.getId() == null) {
				itemReceitaEspecial.setIndSituacao(DominioSituacao.A);
				itemReceitaEspecial.setReceituario(receitaEspecial);
				Integer ordem = itemReceitaEspecialList.size() + 1;
				itemReceitaEspecial.setOrdem(ordem.byteValue());
				this.ambulatorioFacade.inserirItem(receitaEspecial, itemReceitaEspecial);
				itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
			} else {
				this.ambulatorioFacade.atualizarItem(itemReceitaEspecial);
			}
			novoItemReceitaEspecial();
			descricaoReceitaEspecial = "";
			processarReceituarios();
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void limparReceitaEspecial() {
		if (itemReceitaEspecial.getId() != null) {
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaEspecial);
		}
		novoItemReceitaEspecial();
		itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		descricaoReceitaEspecial = "";
		descricaoMedicamentoEspecial = null;
	}

	public void limparReceitaGeral() {
		if (itemReceitaGeral.getId() != null) {
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaGeral);
		}
		novoItemReceitaGeral();
		itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		descricaoReceitaGeral = "";
		descricaoMedicamento = null;
	}

	public void editarReceitaGeral() {
		descricaoReceitaGeral = itemReceitaGeral.getDescricao();
	}

	public void excluirReceitaGeral() {
		try {
			this.ambulatorioFacade.excluirReceitaGeral(receitaGeral, itemReceitaGeral, this.consultaSelecionada, viasGeral, itemReceitaGeralList);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Item Receita Geral");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_EXCLUSAO_ITEM_RECEITA");
		}
	}

	public void excluirReceitaEspecial() {
		try {
			this.ambulatorioFacade.excluirReceitaEspecial(receitaEspecial, itemReceitaEspecial, this.consultaSelecionada, viasEspecial, itemReceitaEspecialList);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Item Receita Especial");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_EXCLUSAO_ITEM_RECEITA");
		}
	}

	public void upItemReceitaGeral(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaGeralList.get(ordem);
			MamItemReceituario itemAcima = itemReceitaGeralList.get(ordem - 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAcima.getOrdem());
			itemAcima.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAcima);
			itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void downItemReceitaGeral(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaGeralList.get(ordem);
			MamItemReceituario itemAbaixo = itemReceitaGeralList.get(ordem + 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAbaixo.getOrdem());
			itemAbaixo.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAbaixo);
			itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void upItemReceitaEspecial(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaEspecialList.get(ordem);
			MamItemReceituario itemAcima = itemReceitaEspecialList.get(ordem - 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAcima.getOrdem());
			itemAcima.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAcima);
			itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void downItemReceitaEspecial(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaEspecialList.get(ordem);
			MamItemReceituario itemAbaixo = itemReceitaEspecialList.get(ordem + 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAbaixo.getOrdem());
			itemAbaixo.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAbaixo);
			itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarReceitaEspecial() {
		descricaoReceitaEspecial = itemReceitaEspecial.getDescricao();
	}

	public void atualizaValidadeGeral() {
		itemReceitaGeral.setValidadeMeses(Byte.valueOf("6"));
	}

	public void atualizaValidadeEspecial() {
		itemReceitaEspecial.setValidadeMeses(Byte.valueOf("6"));
	}

	public void verificaTipoEspecial(MamItemReceituario item) {
		if (DominioTipoPrescricaoReceituario.F.equals(item.getTipoPrescricao()) && (StringUtils.isBlank(descricaoReceitaEspecial))) {
				descricaoReceitaEspecial = DESCR_FORMULA;
		} else 	if (descricaoReceitaEspecial.equalsIgnoreCase(DESCR_FORMULA)) {
				descricaoReceitaEspecial = "";
			}
		}

	public void verificaTipoGeral(MamItemReceituario item) {
		if (DominioTipoPrescricaoReceituario.F.equals(item.getTipoPrescricao()) && (StringUtils.isBlank(descricaoReceitaGeral))) {
				descricaoReceitaGeral = DESCR_FORMULA;
		} else if (descricaoReceitaGeral.equalsIgnoreCase(DESCR_FORMULA)) {
				descricaoReceitaGeral = "";
			}
		}
	
	public String getIdadeFormatada() {
		int idade = this.consultaSelecionada.getPaciente().getIdade();
		if (Boolean.TRUE.equals(this.consultaSelecionada.getGradeAgendamenConsulta().getEspecialidade().getIndEspPediatrica()) || idade < 12) {
			Calendar dtNasc = Calendar.getInstance();
			dtNasc.setTime(DateUtil.truncaData(this.consultaSelecionada.getPaciente().getDtNascimento()));
			Calendar hoje = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
			int anos = 0, meses = 0, dias = 0;
			if (hoje.get(Calendar.YEAR) > dtNasc.get(Calendar.YEAR)) {
				anos = hoje.get(Calendar.YEAR) - dtNasc.get(Calendar.YEAR);
				dtNasc.add(Calendar.YEAR, anos);
				if (hoje.before(dtNasc)) {
					anos -= 1;
					dtNasc.add(Calendar.YEAR, -1);
				}
			}
			boolean isDia = false;
			while (dtNasc.before(hoje)) {
				if (!isDia) {
					meses += 1;
					dtNasc.add(Calendar.MONTH, 1);
					if (hoje.before(dtNasc)) {
						meses -= 1;
						dtNasc.add(Calendar.MONTH, -1);
						isDia = true;
					}
				} else {
					dias += 1;
					dtNasc.add(Calendar.DATE, 1);
				}
			}
			StringBuffer idadeStr = new StringBuffer();
			idadeStr = calculaAnoMesesDiasIdade(anos, meses, dias, idadeStr);
			return idadeStr.toString();
		} else {
			return idade > 1 ? idade + " anos" : idade + " ano";
		}
	}

	private StringBuffer calculaAnoMesesDiasIdade(int anos, int meses, int dias,StringBuffer idadeStr) {
		if (anos > 0) {
			idadeStr.append(anos);
			if (anos > 1) {
				idadeStr.append(" anos ");
			} else {
				idadeStr.append(" ano ");
			}
		}
		if (meses > 0) {
			if (anos > 0) {
				idadeStr.append(" e ");
			}
			idadeStr.append(meses);
			if (meses > 1) {
				idadeStr.append(" meses ");
			} else {
				idadeStr.append(" mês ");
			}
		}
		if (dias > 0) {
			if (anos == 0) {
				if (meses > 0) {
					idadeStr.append(" e ");
				}
				idadeStr.append(dias);
				if (dias > 1) {
					idadeStr.append(" dias ");
				} else {
					idadeStr.append(" dia ");
				}
			}
		}
		return idadeStr;
	}

	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosReceitaVO((String) strPesquisa),obterMedicamentosReceitaVOCount(strPesquisa));
	}

	public Long obterMedicamentosReceitaVOCount(String strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosReceitaVOCount((String) strPesquisa);
	}
	
	public List<VAfaDescrMdto> obterMedicamentosEspeciaisReceitaVO(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosReceitaVO((String) strPesquisa),obterMedicamentosReceitaVOCount(strPesquisa));
	}

	public Long obterMedicamentosEspeciaisReceitaVOCount(String strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosReceitaVOCount((String) strPesquisa);
	}

	public void atualizarDescMedicamentoGeral() {
		atualizarDescMedicamento(itemReceitaGeral, DominioTipoReceituario.G);
	}

	public void atualizarDescMedicamentoEspecial() {
		this.descricaoMedicamento = this.descricaoMedicamentoEspecial;
		atualizarDescMedicamento(itemReceitaEspecial, DominioTipoReceituario.E);
		descricaoMedicamento = null;
	}

	public void atualizarDescMedicamento(MamItemReceituario item, DominioTipoReceituario tipo) {
		StringBuilder descricao = new StringBuilder("");
		if (StringUtils.isNotBlank(descricaoMedicamento.getId().getDescricaoMat())) {
			descricao.append(descricaoMedicamento.getId().getDescricaoMat());
		}
		if (StringUtils.isNotBlank(descricaoMedicamento.getConcentracaoFormatada())) {
			descricao.append(' ').append(descricaoMedicamento.getConcentracaoFormatada());
		}
		if (descricaoMedicamento.getUnidadeMedidaMedicas() != null && StringUtils.isNotBlank(descricaoMedicamento.getUnidadeMedidaMedicas().getDescricao())) {
			descricao.append(' ').append(descricaoMedicamento.getUnidadeMedidaMedicas().getDescricao());
		}
		item.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		if (DominioTipoReceituario.G.equals(tipo)) {
			descricaoReceitaGeral = descricao.toString();
		} else {
			descricaoReceitaEspecial = descricao.toString();
		}
	}
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidades((String) parametro);
	}
	
	public String getProntuarioFormatado() {
		return this.consultaSelecionada != null ? CoreUtil.formataProntuarioRelatorio(this.consultaSelecionada.getPaciente().getProntuario()) : null;
	}

	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
	}
	
	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String comeFrom) {
		this.cameFrom = comeFrom;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getFinalizarDescricao() {
		return finalizarDescricao;
	}

	public void setFinalizarDescricao(String finalizarDescricao) {
		this.finalizarDescricao = finalizarDescricao;
	}

	public String getItemReceitaDescricao(MamItemReceituario item) {
		StringBuffer str = new StringBuffer(32);
		if (StringUtils.isNotBlank(item.getDescricao())) {
			str.append(item.getDescricao());
		}
		if (StringUtils.isNotBlank(item.getQuantidade())) {
			str.append(" - ").append(item.getQuantidade());
		}
		if (StringUtils.isNotBlank(item.getFormaUso())) {
			str.append(". ").append(item.getFormaUso());
		}
		if (item.getIndInternoEnum() != null) {
			str.append(", Uso ").append(item.getIndInternoEnum().getDescricao());
		}
		if (item.getIndUsoContinuoBoolean()) {
			str.append(", Contínuo");
		}
		if (item.getValidadeMeses() != null) {
			str.append(". Validade: ").append(item.getValidadeMeses()).append(" mese(s)");
		}
		str.append('.');
		return str.toString();
	}

	public void atualizaValidade() {
		byte meses = (byte) 6;
		if (this.sliderAtual.equals(0)) {
			this.itemReceitaGeral.setValidadeMeses(meses);
		} else {
			this.itemReceitaEspecial.setValidadeMeses(meses);
		}
		this.confirmaValidade = false;
	}

	public void naoAtualizaValidade() {
		this.confirmaValidade = false;
	}

	public void verificaValidade() {
		this.confirmaValidade = false;
		Boolean geralUsoContinuo = itemReceitaGeral.getIndUsoContinuoBoolean();
		if (geralUsoContinuo && this.itemReceitaGeral.getValidadeMeses() == null) {
				this.confirmaValidade = true;
				this.openDialog("modalConfirmacaoValidadeWG");
			}
		sliderAtual = 0;
	}

	public void verificaValidadeEspecial() {
		this.confirmaValidade = false;
		if (itemReceitaEspecial.getIndUsoContinuoBoolean() && this.itemReceitaEspecial.getValidadeMeses() == null) {
				this.confirmaValidade = true;
			}
		sliderAtual = 1;
	}
	
	public String buscarReceita(){
		buscarReceitasController.setConsultaSelecionada(consultaSelecionada);
		buscarReceitasController.setIdadeFormatada(getIdadeFormatada());
		if(StringUtils.isNotEmpty(cameFrom)){
			buscarReceitasController.setVoltaPara(cameFrom);
		} else{ 
			buscarReceitasController.setVoltaPara(RECEITAS_PACIENTE);
		}
		return BUSCAR_RECEITAS;
	}
	
	public DominioMotivoPendencia getMotivoPendencia() {
		return motivoPendencia;
	}

	public void setMotivoPendencia(DominioMotivoPendencia motivoPendencia) {
		this.motivoPendencia = motivoPendencia;
	}

	public Boolean getMostrarModalImpressao() {
		return mostrarModalImpressao;
	}

	public void setMostrarModalImpressao(Boolean mostrarModalImpressao) {
		this.mostrarModalImpressao = mostrarModalImpressao;
	}

	public MamItemReceituario getItemReceitaEspecial() {return itemReceitaEspecial;}

	public void setItemReceitaEspecial(MamItemReceituario itemReceitaEspecial) {
		this.itemReceitaEspecial = itemReceitaEspecial;
	}

	public List<MamItemReceituario> getItemReceitaGeralList() {
		return itemReceitaGeralList;
	}

	public void setItemReceitaGeralList(List<MamItemReceituario> itemReceitaGeralList) {
		this.itemReceitaGeralList = itemReceitaGeralList;
	}

	public MamItemReceituario getItemReceitaGeral() {
		return itemReceitaGeral;
	}

	public void setItemReceitaGeral(MamItemReceituario itemReceitaGeral) {
		this.itemReceitaGeral = itemReceitaGeral;
	}

	public List<MamItemReceituario> getItemReceitaEspecialList() {
		return itemReceitaEspecialList;
	}

	public void setItemReceitaEspecialList(List<MamItemReceituario> itemReceitaEspecialList) {
		this.itemReceitaEspecialList = itemReceitaEspecialList;
	}

	public MamReceituarios getReceitaGeral() {
		return receitaGeral;
	}

	public void setReceitaGeral(MamReceituarios receitaGeral) {
		this.receitaGeral = receitaGeral;
	}

	public MamReceituarios getReceitaEspecial() {
		return receitaEspecial;
	}

	public void setReceitaEspecial(MamReceituarios receitaEspecial) {
		this.receitaEspecial = receitaEspecial;
	}

	public Boolean getHabilitaFinalizar() {
		return habilitaFinalizar;
	}

	public void setHabilitaFinalizar(Boolean habilitaFinalizar) {
		this.habilitaFinalizar = habilitaFinalizar;
	}

	public Integer getViasGeral() {
		return viasGeral;
	}

	public void setViasGeral(Integer viasGeral) {
		this.viasGeral = viasGeral;
	}

	public Integer getViasEspecial() {
		return viasEspecial;
	}

	public void setViasEspecial(Integer viasEspecial) {
		this.viasEspecial = viasEspecial;
	}

	public Boolean getVoltarListaPacientes() {
		return voltarListaPacientes;
	}

	public void setVoltarListaPacientes(Boolean voltarListaPacientes) {
		this.voltarListaPacientes = voltarListaPacientes;
	}

	public VAfaDescrMdto getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(VAfaDescrMdto descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public Integer getSizeItensGeral() {
		return itemReceitaGeralList.size();
	}

	public Integer getSizeItensEspecial() {
		return itemReceitaEspecialList.size();
	}

	public String getDescricaoReceitaGeral() {
		return descricaoReceitaGeral;
	}

	public void setDescricaoReceitaGeral(String descricaoReceitaGeral) {
		this.descricaoReceitaGeral = descricaoReceitaGeral;
	}

	public String getDescricaoReceitaEspecial() {
		return descricaoReceitaEspecial;
	}

	public void setDescricaoReceitaEspecial(String descricaoReceitaEspecial) {
		this.descricaoReceitaEspecial = descricaoReceitaEspecial;
	}

	public String getValidadeUso() {
		return validadeUso;
	}

	public void setValidadeUso(String validadeUso) {
		this.validadeUso = validadeUso;
	}

	public Boolean getHabilitaReceita() {
		return habilitaReceita;
	}

	public void setHabilitaReceita(Boolean habilitaReceita) {
		this.habilitaReceita = habilitaReceita;
	}

	public Boolean getGravaReceita() {
		return gravaReceita;
	}

	public void setGravaReceita(Boolean gravaReceita) {
		this.gravaReceita = gravaReceita;
	}

	public Boolean getReadonlyReceitaGeral() {
		return readonlyReceitaGeral;
	}

	public void setReadonlyReceitaGeral(Boolean readonlyReceitaGeral) {
		this.readonlyReceitaGeral = readonlyReceitaGeral;
	}

	public Boolean getReadonlyReceitaEspecial() {
		return readonlyReceitaEspecial;
	}

	public void setReadonlyReceitaEspecial(Boolean readonlyReceitaEspecial) {
		this.readonlyReceitaEspecial = readonlyReceitaEspecial;
	}

	public void setConfirmaValidade(boolean confirmaValidade) {
		this.confirmaValidade = confirmaValidade;
	}

	public boolean isConfirmaValidade() {
		return confirmaValidade;
	}

	public void setSliderAtual(Integer sliderAtual) {
		this.sliderAtual = sliderAtual;
	}

	public Integer getSliderAtual() {
		return sliderAtual;
	}
	
	public String getBanco() {return banco;}

	public void setBanco(String banco) {this.banco = banco;}

	public String getUrlBaseWebForms() {return urlBaseWebForms;}

	public void setUrlBaseWebForms(String urlBaseWebForms) {this.urlBaseWebForms = urlBaseWebForms;}

	public Boolean getIsHcpa() {return isHcpa;}

	public void setIsHcpa(Boolean isHcpa) {this.isHcpa = isHcpa;}

	public Boolean getIsUbs() {return isUbs;}

	public void setIsUbs(Boolean isUbs) {this.isUbs = isUbs;}

	public RapServidores getServidorLogadoSemFimVinculo() {return servidorLogadoSemFimVinculo;}

	public void setServidorLogadoSemFimVinculo(RapServidores servidorLogadoSemFimVinculo) {
		this.servidorLogadoSemFimVinculo = servidorLogadoSemFimVinculo;
	}

	public CadastroCuidadosPacienteController getCadastroCuidadosPacienteController() {return cadastroCuidadosPacienteController;}

	public void setCadastroCuidadosPacienteController(CadastroCuidadosPacienteController cadastroCuidadosPacienteController) {
		this.cadastroCuidadosPacienteController = cadastroCuidadosPacienteController;
	}

	public boolean isPermissaoAbaCuidados() {return permissaoAbaCuidados;}

	public void setPermissaoAbaCuidados(boolean permissaoAbaCuidados) {this.permissaoAbaCuidados = permissaoAbaCuidados;}

	public String getLoginUsuario() {return loginUsuario;}

	public void setLoginUsuario(String loginUsuario) {this.loginUsuario = loginUsuario;}

	public VAfaDescrMdto getDescricaoMedicamentoEspecial() {return descricaoMedicamentoEspecial;}

	public void setDescricaoMedicamentoEspecial(VAfaDescrMdto descricaoMedicamentoEspecial) {this.descricaoMedicamentoEspecial = descricaoMedicamentoEspecial;}
}