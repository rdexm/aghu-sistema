package br.gov.mec.aghu.emergencia.action;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.perinatologia.action.RegistrarGestacaoController;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAtendidosVO;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da aba Atendidos
 * 
 * @author felipe.rocha
 */
public class PacientesEmergenciaAbaAtendidosController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;

	private static final Integer ABA_GESTACAO_ATUAL = 0, ABA_ATENDIDOS = 4;	
	private static final Integer TIPO_MODAL_ESTORNADO = 1;
	private static final Integer TIPO_MODAL_OBITO = 2;
	private static final Integer TIPO_REDIRECIONA = 3;
	
	private static final String REDIRECT_GESTACAO_ATUAL = "/pages/perinatologia/registrarGestacao.xhtml";
	
	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;

	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;

	private boolean habilitaBotaoOkModal;
	private boolean habilitaBotaoSimNaoModal;
	private String mensagemModal;
	private MamPacientesAtendidosVO pacAtendidos;
	
	private List<MamPacientesAtendidosVO> listaPacientesAtendidos = new LinkedList<MamPacientesAtendidosVO>();

	private Short espSeq;
	
	private String hostName = "Não foi possível obter o hostName.";
	
	
	
	public void pesquisarPacientesAtendidos(){
		try {
			MamUnidAtendem mamUnidAtendem = this.listaPacientesEmergenciaPaginatorController.getMamUnidAtendem();
			// lista especialidades
			this.listaPacientesEmergenciaPaginatorController.listaEspecialidadeEmergencia();
			if (mamUnidAtendem != null) {
				if (getEspSeq() == null) {
					setListaPacientesAtendidos(this.emergenciaFacade.listarPacientesAtendidos(mamUnidAtendem.getSeq(), null));
				} else {
					setListaPacientesAtendidos(this.emergenciaFacade.listarPacientesAtendidos(mamUnidAtendem.getSeq(),  getEspSeq()));
				}
			}
			this.listaPacientesEmergenciaPaginatorController.setAbaSelecionada(ABA_ATENDIDOS);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	private void obterHostName(){
		try {
	    	setHostName(super.getEnderecoIPv4HostRemoto().getHostName());
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,
					"Não foi possível obter hostName");
		}
	}
	public String reabrirPacienteAtendidos(){
		obterHostName();
		if (getPacAtendidos() != null) {
			try {
				Map<Integer, String> resultado = this.emergenciaFacade.reabrirPacienteAtendidos(getPacAtendidos().getTrgSeq(),getPacAtendidos().getAtdSeq(),getPacAtendidos().getUnfSeq(),getPacAtendidos().getPacNome(), getEspSeq(),getHostName());
				if (resultado != null && resultado.size() > 0) {
					if (resultado.containsKey(TIPO_REDIRECIONA)) {

						this.registrarGestacaoController.setNumeroConsulta(pacAtendidos.getConNumero());
						this.registrarGestacaoController.setNomePaciente(pacAtendidos.getPacNome());
						this.registrarGestacaoController.setIdadeFormatada(pacAtendidos.getPacIdade().toString());
						this.registrarGestacaoController.setProntuario(pacAtendidos.getPacProntuario());
						this.registrarGestacaoController.setSeqp(this.emergenciaFacade.obterUltimaGestacaoRegistrada(pacAtendidos.getPacCodigo()));
						this.registrarGestacaoController.setAbaSelecionada(ABA_GESTACAO_ATUAL);
						this.registrarGestacaoController.setPacCodigo(pacAtendidos.getPacCodigo());
						return REDIRECT_GESTACAO_ATUAL;
					
					} else if (resultado.containsKey(TIPO_MODAL_ESTORNADO)) {
						mensagemModal = resultado.get(TIPO_MODAL_ESTORNADO);
						setHabilitaBotaoOkModal(false);
						setHabilitaBotaoSimNaoModal(true);
					} else if (resultado.containsKey(TIPO_MODAL_OBITO)) {
						mensagemModal = resultado.get(TIPO_MODAL_OBITO);
						setHabilitaBotaoOkModal(true);
						setHabilitaBotaoSimNaoModal(false);
					}
				}

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		pesquisarPacientesAtendidos();
		return 	null;
		
	}
	

	public void desbloqueioSumarioAlta() {
		obterHostName();
		if (getPacAtendidos() != null) {
			try {
				limpaModal();
				this.emergenciaFacade.desbloqueioSumarioAlta(getPacAtendidos().getAtdSeq(), getHostName());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void limpaModal(){
		setHabilitaBotaoOkModal(false);
		setHabilitaBotaoSimNaoModal(false);
		setMensagemModal(null);
	}
	
	//gets e sets

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public MamPacientesAtendidosVO getPacAtendidos() {
		return pacAtendidos;
	}

	public void setPacAtendidos(MamPacientesAtendidosVO pacAtendidos) {
		this.pacAtendidos = pacAtendidos;
	}

	public List<MamPacientesAtendidosVO> getListaPacientesAtendidos() {
		return listaPacientesAtendidos;
	}

	public void setListaPacientesAtendidos(List<MamPacientesAtendidosVO> listaPacientesAtendidos) {
		this.listaPacientesAtendidos = listaPacientesAtendidos;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public boolean isHabilitaBotaoOkModal() {
		return habilitaBotaoOkModal;
	}

	public void setHabilitaBotaoOkModal(boolean habilitaBotaoOkModal) {
		this.habilitaBotaoOkModal = habilitaBotaoOkModal;
	}

	public boolean isHabilitaBotaoSimNaoModal() {
		return habilitaBotaoSimNaoModal;
	}

	public void setHabilitaBotaoSimNaoModal(boolean habilitaBotaoSimNaoModal) {
		this.habilitaBotaoSimNaoModal = habilitaBotaoSimNaoModal;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}




}