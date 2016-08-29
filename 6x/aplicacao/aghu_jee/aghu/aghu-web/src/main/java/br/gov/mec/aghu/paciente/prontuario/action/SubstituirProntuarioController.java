package br.gov.mec.aghu.paciente.prontuario.action;

import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Classe responsável por controlar as ações da tela de substituição de
 * prontuário
 * 
 * @author joao.birk
 * 
 */
public class SubstituirProntuarioController extends ActionController {

	private static final long serialVersionUID = 1755993948046292200L;

	private static final Log LOG = LogFactory.getLog(SubstituirProntuarioController.class);

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AipPacientes aipPacienteOrigem;
	private AipPacientes aipPacienteDestino;
	private String nomePacienteOrigem;
	private String nomePacienteDestino;
	private Boolean pesquisaOrigem;
	private Boolean pesquisaDestino;
	private Integer prontuarioOrigem;
	private Integer codigoOrigem;
	private Integer prontuarioDestino;
	private Integer codigoDestino;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public void substituir() {
		if(!this.getRequestParameter("prontuario_origem").isEmpty()) {
			prontuarioOrigem = Integer.valueOf(this.getRequestParameter("prontuario_origem"));
		} else {
			prontuarioOrigem = null;
		}
		codigoOrigem = Integer.valueOf(this.getRequestParameter("id_prontuario_origem"));
		if(!this.getRequestParameter("prontuario_destino").isEmpty()) {
			prontuarioDestino = Integer.valueOf(this.getRequestParameter("prontuario_destino"));
		} else {
			prontuarioDestino = null;
		}
		codigoDestino = Integer.valueOf(this.getRequestParameter("id_prontuario_destino"));				
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto() != null ? super.getEnderecoIPv4HostRemoto().getHostName() : "";
			} catch (UnknownHostException e) {
				LOG.error("Exceção capturada: ", e);
			}
			aipPacienteOrigem = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioOrigem, codigoOrigem, null);
			aipPacienteDestino = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioDestino, codigoDestino, null);
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.pacienteFacade.substituirProntuario(aipPacienteOrigem.getProntuario(), aipPacienteDestino.getProntuario(),
					aipPacienteOrigem.getDtIdentificacao(), aipPacienteDestino.getDtIdentificacao(), aipPacienteOrigem.getCodigo(),
					aipPacienteDestino.getCodigo(), nomeMicrocomputador,servidorLogado , new Date());
			apresentarMsgNegocio(Severity.INFO, "AIP_00097");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			limpar();
		}
	}
	
	public void showModal() {
		try {
			aipPacienteOrigem = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioOrigem, codigoOrigem, null);
			aipPacienteDestino = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioDestino, codigoDestino, null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		openDialog("modalConfirmacaoSubstituicaoWG");
	}

	public void limpar() {
		aipPacienteOrigem = new AipPacientes();
		aipPacienteDestino = new AipPacientes();
		pesquisaOrigem = null;
		pesquisaDestino = null;
		prontuarioOrigem = null;
		codigoOrigem = null;
		prontuarioDestino = null;
		codigoDestino = null;
		nomePacienteDestino = null;
		nomePacienteOrigem = null;
	}

	public void pesquisarPacienteOrigem() {
		try {
			if (aipPacienteOrigem == null) {
				aipPacienteOrigem = new AipPacientes();
			}
			if (pesquisaOrigem == null) {
				pesquisaOrigem = false;
			}
			Integer prontuario = aipPacienteOrigem.getProntuario();
			Integer codigo = aipPacienteOrigem.getCodigo();
			String nomePacienteOrigem_ = aipPacienteOrigem.getNome();
			aipPacienteOrigem = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioOrigem, codigoOrigem, null);
			if (aipPacienteOrigem != null) {
				popularPacienteOrigem();
			} else {
				pesquisaOrigem = false;
				aipPacienteOrigem = new AipPacientes();
				aipPacienteOrigem.setProntuario(prontuario);
				aipPacienteOrigem.setCodigo(codigo);
				aipPacienteOrigem.setNome(nomePacienteOrigem_);
				apresentarMsgNegocio(Severity.INFO, "AIP_PACIENTE_NAO_ENCONTRADO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			pesquisaOrigem = false;
		} finally {
			if (codigoDestino != null || prontuarioDestino != null) {
				try {
					aipPacienteDestino = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioDestino, codigoDestino, null);
					if (aipPacienteDestino != null) {
						popularPacienteDestino();
					}
				} catch (ApplicationBusinessException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	public void pesquisarPacienteDestino() {
		try {
			if (aipPacienteDestino == null) {
				aipPacienteDestino = new AipPacientes();
			}
			if (pesquisaDestino == null) {
				pesquisaDestino = false;
			}
			Integer prontuario = aipPacienteDestino.getProntuario();
			Integer codigo = aipPacienteDestino.getCodigo();
			String nomePacienteDestino_ = aipPacienteDestino.getNome();
			aipPacienteDestino = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioDestino, codigoDestino, null);
			if (aipPacienteDestino != null) {
				popularPacienteDestino();
			} else {
				pesquisaDestino = false;
				aipPacienteDestino = new AipPacientes();
				aipPacienteDestino.setProntuario(prontuario);
				aipPacienteDestino.setCodigo(codigo);
				aipPacienteDestino.setNome(nomePacienteDestino_);
				apresentarMsgNegocio(Severity.INFO, "AIP_PACIENTE_NAO_ENCONTRADO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			pesquisaDestino = false;
		} finally {
			if (codigoOrigem != null || prontuarioOrigem != null) {
				try {
					aipPacienteOrigem = pacienteFacade.obterPacientePorCodigoEProntuario(prontuarioOrigem, codigoOrigem, null);
					if (aipPacienteOrigem != null) {
						popularPacienteOrigem();
					}
				} catch (ApplicationBusinessException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	private void popularPacienteOrigem() {
		pesquisaOrigem = true;
		prontuarioOrigem = aipPacienteOrigem.getProntuario();
		codigoOrigem = aipPacienteOrigem.getCodigo();
		nomePacienteOrigem = aipPacienteOrigem.getNome();
	}

	private void popularPacienteDestino() {
		pesquisaDestino = true;
		prontuarioDestino = aipPacienteDestino.getProntuario();
		codigoDestino = aipPacienteDestino.getCodigo();
		nomePacienteDestino = aipPacienteDestino.getNome();
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public AipPacientes getAipPacienteOrigem() {
		return aipPacienteOrigem;
	}

	public void setAipPacienteOrigem(AipPacientes aipPacienteOrigem) {
		this.aipPacienteOrigem = aipPacienteOrigem;
	}

	public AipPacientes getAipPacienteDestino() {
		return aipPacienteDestino;
	}

	public void setAipPacienteDestino(AipPacientes aipPacienteDestino) {
		this.aipPacienteDestino = aipPacienteDestino;
	}

	public String getNomePacienteOrigem() {
		return nomePacienteOrigem;
	}

	public void setNomePacienteOrigem(String nomePacienteOrigem) {
		this.nomePacienteOrigem = nomePacienteOrigem;
	}

	public String getNomePacienteDestino() {
		return nomePacienteDestino;
	}

	public void setNomePacienteDestino(String nomePacienteDestino) {
		this.nomePacienteDestino = nomePacienteDestino;
	}

	public Boolean getPesquisaOrigem() {
		return pesquisaOrigem;
	}

	public void setPesquisaOrigem(Boolean pesquisaOrigem) {
		this.pesquisaOrigem = pesquisaOrigem;
	}

	public Boolean getPesquisaDestino() {
		return pesquisaDestino;
	}

	public void setPesquisaDestino(Boolean pesquisaDestino) {
		this.pesquisaDestino = pesquisaDestino;
	}

	public Integer getProntuarioOrigem() {
		return prontuarioOrigem;
	}

	public void setProntuarioOrigem(Integer prontuarioOrigem) {
		this.prontuarioOrigem = prontuarioOrigem;
	}

	public Integer getCodigoOrigem() {
		return codigoOrigem;
	}

	public void setCodigoOrigem(Integer codigoOrigem) {
		this.codigoOrigem = codigoOrigem;
	}

	public Integer getProntuarioDestino() {
		return prontuarioDestino;
	}

	public void setProntuarioDestino(Integer prontuarioDestino) {
		this.prontuarioDestino = prontuarioDestino;
	}

	public Integer getCodigoDestino() {
		return codigoDestino;
	}

	public void setCodigoDestino(Integer codigoDestino) {
		this.codigoDestino = codigoDestino;
	}

}
