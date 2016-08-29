package br.gov.mec.aghu.internacao.action;

import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.internacao.administracao.business.IAdministracaoInternacaoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@Named
@SessionScoped
public class AtualizarDataObitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3696844422893454054L;
	
	private static final Log LOG = LogFactory.getLog(AtualizarDataObitoController.class);

	/**
	 * Campo de pesquisa
	 */
	private Integer prontuario;

	/**
	 * Campo de pesquisa
	 */
	private Integer codigo;

	/**
	 * Objeto obtido na pesquisa.
	 */
	private AipPacientes paciente;

	/**
	 * Data de Obito.
	 */
	private Date dataObito;

	@Inject
	private IAdministracaoInternacaoFacade administracaoInternacaoFacade;

	/**
	 * Tipo de data de obito.
	 */
	private DominioTipoDataObito tipoDataObito;

	/**
	 * Método invocado na criação do componente Seam.
	 */
	@PostConstruct
	public void inicio() {
		this.prontuario = null;
		this.codigo = null;
		this.paciente = null;
		this.tipoDataObito = null;
		this.dataObito = null;
	}

	/**
	 * Metodo que realiza a acao do botao pesquisar.
	 */
	public void pesquisar() {
		try {
			this.administracaoInternacaoFacade.validaCampos(prontuario, codigo);

			this.paciente = administracaoInternacaoFacade.buscarDadosPaciente(
					prontuario, codigo, null);

			this.tipoDataObito = null;
			this.dataObito = null; 

			if (this.paciente != null) {
				if (this.paciente.getTipoDataObito() != null) {
					this.tipoDataObito = paciente.getTipoDataObito();
				}
				if (this.paciente.getDtObitoExterno() != null) {
					this.dataObito = paciente.getDtObitoExterno();
				}
			}

		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.WARN, e.getLocalizedMessage());
		}
	}

	/**
	 * Atualiza data de óbito externo para NULL quando IGN é selecionado.
	 */
	public void atualizarDataObito() {
		if (tipoDataObito == null || (tipoDataObito != null && this.tipoDataObito.equals(DominioTipoDataObito.IGN))) {
			this.dataObito = null;
		}
	}

	/**
	 * Metodo que realiza a acao do botao gravar.
	 * @throws UnknownHostException 
	 */
	public void confirmar() {
		try {

			// Valida data de obito externo com data de óbito já existente.
			this.administracaoInternacaoFacade.validarDataObito(dataObito, tipoDataObito, paciente);

			// Associar tipoDataObito e dataObito.
			this.paciente.setTipoDataObito(tipoDataObito);
			this.paciente.setDtObitoExterno(dataObito);
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = getEnderecoIPv4HostRemoto().toString();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.administracaoInternacaoFacade.atualizarDataObito(paciente, nomeMicrocomputador, new Date());
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_PACIENTE", this.paciente.getNome());

			// Limpa campos.
			this.limparPesquisa();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.prontuario = null;
		this.codigo = null;
		this.paciente = null;
		this.tipoDataObito = null;
		this.dataObito = null;
	}

	// ### GETs e SETs ###
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Date getDataObito() {
		return dataObito;
	}

	public void setDataObito(Date dataObito) {
		this.dataObito = dataObito;
	}

	public DominioTipoDataObito getTipoDataObito() {
		return tipoDataObito;
	}

	public void setTipoDataObito(DominioTipoDataObito tipoDataObito) {
		this.tipoDataObito = tipoDataObito;
	}
}