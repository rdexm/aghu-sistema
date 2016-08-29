package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AltaPacienteObservacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9142505805813715112L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Campo de entrada.
	 */
	private Integer prontuario;

	/**
	 * Paciente selecionado a partir do prontuário.
	 */
	private AipPacientes paciente;

	/**
	 * Atend. de Urgência do Prontuário.
	 */
	private AinAtendimentosUrgencia atendUrgencia;

	/*
	 * Lov - Tipo Alta Médica
	 */
	/** Código. */
	private String codigo;

	/** Código ou Descrição. */
	private String codDescLov;

	/** Tipo Alta Médica. */
	private AinTiposAltaMedica tipoAltaMedica = null;

	/** Especialidades. */
	private List<AinTiposAltaMedica> tiposAltasMedica = new ArrayList<AinTiposAltaMedica>(
			0);

	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.prontuario = null;
		this.paciente = new AipPacientes();
		this.atendUrgencia = new AinAtendimentosUrgencia();
	}

	/**
	 * Busca Tipo Alta por código.
	 */
	public void buscarTipoAlta() {
		if (!this.codigo.isEmpty()) {
			this.codigo = codigo.toUpperCase();
			this.tipoAltaMedica = this.cadastrosBasicosInternacaoFacade
					.obterTipoAltaMedica(codigo.toUpperCase());
		} else {
			this.tipoAltaMedica = new AinTiposAltaMedica();
		}
	}

	/**
	 * Busca Tipo de Alta por descrição.
	 */
	public List<AinTiposAltaMedica> pesquisarTiposAltas(String param){
		this.tiposAltasMedica = this.cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigoEDescricao(param);
		return this.tiposAltasMedica;
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.init();
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.paciente = pacienteFacade
				.pesquisarPacientePorProntuario(this.prontuario);

		try {
			this.atendUrgencia = this.internacaoFacade
					.obterAtendUrgencia(paciente);
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.WARN,
					e.getLocalizedMessage());
		}
	}

	/**
	 * Metodo que realiza a acao do botao gravar.
	 */
	public void confirmar() {
		// Valida da de alta logo após o usuário selecioná-la.
		try {
			this.internacaoFacade.validaDataAlta(atendUrgencia
					.getDtAltaAtendimento(), atendUrgencia.getDtAtendimento());
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.WARN,
					e.getLocalizedMessage());
		}

		// TODO: Continuar a implementação dessa estória de usuário #580. Foi
		// abortada pois não faz parte do escopo de Internação. Verificar o
		// escopo correto.

	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AinAtendimentosUrgencia getAtendUrgencia() {
		return atendUrgencia;
	}

	public void setAtendUrgencia(AinAtendimentosUrgencia atendUrgencia) {
		this.atendUrgencia = atendUrgencia;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodDescLov() {
		return codDescLov;
	}

	public void setCodDescLov(String codDescLov) {
		this.codDescLov = codDescLov;
	}

	public AinTiposAltaMedica getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	public List<AinTiposAltaMedica> getTiposAltasMedica() {
		return tiposAltasMedica;
	}

	public void setTiposAltasMedica(List<AinTiposAltaMedica> tiposAltasMedica) {
		this.tiposAltasMedica = tiposAltasMedica;
	}

}