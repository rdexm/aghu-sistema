package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de pesquisa de gestação
 * 
 * @author luismoura
 * 
 */
public class PesquisaGestacaoController extends ActionController {

	private static final long serialVersionUID = 5479506544352584157L;

	private static final String PAGE_REGISTRO_PERINATAL = "registrarGestacao.xhtml";
	private final String PAGE_LISTA_PACIENTES_EMERGENCIA = "/pages/emergencia/listaPacientesEmergencia.xhtml";
	private final String REDIRECT_LISTA_PACIENTES_AGUARDANDO = "listaPacientesEmergencia";
	private final String REDIRECT_LISTA_PACIENTES_EM_ATENDIMENTO = "emergencia-pacientesEmergenciaAbaEmAtendimento";
	private final String REDIRECT_LISTA_PACIENTES_ATENDIDOS = "emergencia-pacientesEmergenciaAbaAtendidos";
	private static final Integer ABA_GESTACAO_ATUAL = 0;

	// ----- FAÇADES
	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	// ----- CONTROLLERS
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;

	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	// ----- FILTROS
	private Integer nroProntuario;
	private Integer nroConsulta;
	
	// Parametro recebido da lista de aguardando
	private Integer conNumeroParam;

	// ----- RESULTADO DA CONSULTA
	private PacienteProntuarioConsulta gestante = new PacienteProntuarioConsulta();
	private String idadeFormatada;
	private List<McoGestacoes> dataModel = new ArrayList<McoGestacoes>();
	private Long trgSeq; //informação oriunda da aba aguardando da lista pacientes emergencia, botão atender

	// ----- ITEM SELECIONADO
	private McoGestacoes gestacao;

	// ----- CONTROLE DE TELA
	private boolean pesquisaAtiva = false;
	private String voltarPara;
	
	// ----- SE VEIO DA ABA AGUARDANDO
	private boolean abaAguardando;
	
	private boolean manterGestacao;

	private boolean permManterGestacoes;

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		//limparDadosGestante();
		limparPesquisa();
		
		if (this.abaAguardando) {
			this.nroProntuario = null;
			this.nroConsulta = this.conNumeroParam;
			this.pesquisar();
			this.abaAguardando = false;
			this.manterGestacao = false;
		} 
		
		//tela de manter gestacao
		if (this.manterGestacao) {
			this.manterGestacao = false;
			this.pesquisar();
		}
				
		String usuarioLogado = super.obterLoginUsuarioLogado();
		this.setPermManterGestacoes(getPermissionService().usuarioTemPermissao(usuarioLogado, "manterGestacoes", "executar"));
	}

	/**
	 * Ação do botão PESQUISAR
	 */
	public void pesquisar() {
		try {
			if (this.nroProntuario == null && this.nroConsulta == null) {
				super.apresentarMsgNegocio(Severity.ERROR, "ERRO_PRONTUARIO_E_NUMEROCONSULTA_NAO_INFORMADO");
				return;
			}
			this.gestante = this.emergenciaFacade.obterDadosGestante(this.nroProntuario, this.nroConsulta);
			if(this.nroProntuario == null){
				this.nroProntuario = this.gestante.getProntuario();
			}
			this.idadeFormatada = this.emergenciaFacade.getIdadeFormatada(this.gestante.getDtNascimento());
			if (this.nroProntuario != null) {	
				if (this.nroConsulta == null) {
					this.apresentarMsgNegocio(Severity.WARN, "ERRO_PACIENTE_SEM_CONSULTA_CO");
				} else {
					this.gestante.setConsulta(this.nroConsulta);
				}
			}
			this.dataModel = this.emergenciaFacade.pesquisarMcoGestacoesPorPaciente(this.gestante.getCodigo());
			this.pesquisaAtiva = true;
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limpar() {
		this.limparFiltro();
		this.limparDadosGestante();
		this.limparPesquisa();
	}

	private void limparFiltro() {
		this.nroProntuario = null;
		this.nroConsulta = null;
	}

	private void limparDadosGestante() {
		if (!manterGestacao) {
			this.gestante = new PacienteProntuarioConsulta();
			this.idadeFormatada = null;
		}
	}

	private void limparPesquisa() {
		this.dataModel.clear();
		this.pesquisaAtiva = false;
	}

	/**
	 * Ação do botão REGISTRO PERINATAL
	 */
	public String irRegistroPerinatal() {
		try {
			this.passarParametros();
			this.setManterGestacao(true);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_REGISTRO_PERINATAL;
	}

	/**
	 * Ação do botão NOVA GESTAÇÃO
	 */
	public String irNovaGestacao() {
		this.gestacao = null;
		try {
			this.passarParametros();
			this.setManterGestacao(true);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_REGISTRO_PERINATAL;
	}

	/**
	 * Passa os parâmetros para a rela de registro perinatal
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void passarParametros() throws ApplicationBusinessException {
		Short seqp = this.gestacao != null ? this.gestacao.getId().getSeqp() : null;
		Integer pacCodigo = this.gestante != null ? this.gestante.getCodigo() : null;
		String nome = this.gestante != null ? this.gestante.getNome() : null;
		registrarGestacaoController.prepararTela(pacCodigo, seqp, gestante.getConsulta(), nome, this.idadeFormatada, gestante.getProntuario());
		registrarGestacaoController.setTrgSeq(trgSeq);
		registrarGestacaoController.setAbaDestino(ABA_GESTACAO_ATUAL);
	}

	public String cancelar() {
		if (REDIRECT_LISTA_PACIENTES_EM_ATENDIMENTO.equals(this.voltarPara)) {
			listaPacientesEmergenciaPaginatorController.setAbaSelecionada(3);
			listaPacientesEmergenciaPaginatorController.pesquisarPacientesEmAtendimento();
			return PAGE_LISTA_PACIENTES_EMERGENCIA;
		} else if (REDIRECT_LISTA_PACIENTES_AGUARDANDO.equals(this.voltarPara)){
			listaPacientesEmergenciaPaginatorController.setAbaSelecionada(1);
			listaPacientesEmergenciaPaginatorController.pesquisarPacientesAcolhimento();
			return PAGE_LISTA_PACIENTES_EMERGENCIA;
		} else if (REDIRECT_LISTA_PACIENTES_ATENDIDOS.equals(this.voltarPara)){
			listaPacientesEmergenciaPaginatorController.setAbaSelecionada(4);
			listaPacientesEmergenciaPaginatorController.pesquisarPacientesAtendidos();
			return PAGE_LISTA_PACIENTES_EMERGENCIA;
		}
		return null;
	}
	
	public void carregarDadosPaciente() {
		try {
			this.gestante = this.emergenciaFacade.obterDadosGestante(this.nroProntuario, this.nroConsulta);
			if (gestante != null) {
				//não buscar consulta mais atual quando o numero ja tiver sido informado
				if (this.nroConsulta == null) {
					this.nroConsulta = this.ambulatorioFacade.obterUltimaConsultaGestantePorPaciente(this.gestante.getCodigo());
					this.gestante.setConsulta(nroConsulta);
				} else {
					this.nroConsulta = this.gestante.getConsulta();
				}
				
				this.idadeFormatada = this.emergenciaFacade.getIdadeFormatada(this.gestante.getDtNascimento());
				this.nroProntuario = this.gestante.getProntuario();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ----- GETs e SETs

	public Integer getNroProntuario() {
		return nroProntuario;
	}

	public void setNroProntuario(Integer nroProntuario) {
		this.nroProntuario = nroProntuario;
	}

	public Integer getNroConsulta() {
		return nroConsulta;
	}

	public void setNroConsulta(Integer nroConsulta) {
		this.nroConsulta = nroConsulta;
	}

	public PacienteProntuarioConsulta getGestante() {
		return gestante;
	}

	public void setGestante(PacienteProntuarioConsulta gestante) {
		this.gestante = gestante;
	}

	public List<McoGestacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<McoGestacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}

	public McoGestacoes getGestacao() {
		return gestacao;
	}

	public void setGestacao(McoGestacoes gestacao) {
		this.gestacao = gestacao;
	}

	public boolean isAbaAguardando() {
		return abaAguardando;
	}

	public void setAbaAguardando(boolean abaAguardando) {
		this.abaAguardando = abaAguardando;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getConNumeroParam() {
		return conNumeroParam;
	}

	public void setConNumeroParam(Integer conNumeroParam) {
		this.conNumeroParam = conNumeroParam;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public boolean isPermManterGestacoes() {
		return permManterGestacoes;
	}

	public void setPermManterGestacoes(boolean permManterGestacoes) {
		this.permManterGestacoes = permManterGestacoes;
	}

	public boolean isManterGestacao() {
		return manterGestacao;
	}

	public void setManterGestacao(boolean manterGestacao) {
		this.manterGestacao = manterGestacao;
	}
}