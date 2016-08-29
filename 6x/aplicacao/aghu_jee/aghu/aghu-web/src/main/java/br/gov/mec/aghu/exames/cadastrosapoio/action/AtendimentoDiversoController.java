package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class AtendimentoDiversoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -1691228141864387692L;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AelAtendimentoDiversos aelAtendimentoDiversos;

	// param
	private String voltarPara;

	private AipPacientes paciente;
	private Integer prontuario;
	private Integer pacCodigoFonetica;
	private Integer codPac;

	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AghUnidadesFuncionais unidadeExecutora;

	private boolean renderizaProjPesquisa;
	private boolean renderizaLabExterno;
	private boolean renderizaContrQualidade;
	private boolean renderizaCadaver;

	public void ajustaValores() {
		if (paciente != null) {
			aelAtendimentoDiversos.setDtNascimento(null);
			aelAtendimentoDiversos.setNomePaciente(null);
			aelAtendimentoDiversos.setSexo(null);
		}
	}

	public void ajustaValores1() {
		if (aelAtendimentoDiversos.getNomePaciente() != null) {
			prontuario = null;
			codPac = null;
			paciente = null;
		}
	}

	public void pesquisaPaciente(ValueChangeEvent event) {
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(
					event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				ajustaValores();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void inicio() {

		paciente = null;
		prontuario = null;
		codPac = null;

		// Obtem o USUARIO da unidade executora
		if (unidadeExecutora == null) {
			try {
				this.usuarioUnidadeExecutora = this.examesFacade
						.obterUnidExecUsuarioPeloId(registroColaboradorFacade
								.obterServidorAtivoPorUsuario(
										this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora = null;
			}

			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora
						.getUnfSeq();
			}
		}

		if (pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade
					.obterPacientePorCodigo(pacCodigoFonetica);

			if (paciente != null) {
				prontuario = paciente.getProntuario();
				codPac = paciente.getCodigo();
				ajustaValores();
				pacCodigoFonetica = null;
			}
			return;
		}

		if (aelAtendimentoDiversos != null) {
			paciente = aelAtendimentoDiversos.getAipPaciente();
			if (paciente != null) {
				prontuario = paciente.getProntuario();
				codPac = paciente.getCodigo();
			}

		} else {
			this.aelAtendimentoDiversos = new AelAtendimentoDiversos();
			aelAtendimentoDiversos.setOrigemAmostra(DominioOrigemAmostra.H);
		}

		atualizaSuggestionsTipoAtendimento();

	}

	public String gravar() {

		try {
			aelAtendimentoDiversos.setAipPaciente(paciente);
			boolean isUpdate = aelAtendimentoDiversos.getSeq() != null;
			this.cadastrosApoioExamesFacade
					.persistirAelAtendimentoDiversos(aelAtendimentoDiversos);

			if (isUpdate) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_ATENDIMENTO_DIVERSOS",
						this.aelAtendimentoDiversos.getSeq());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_ATENDIMENTO_DIVERSOS",
						this.aelAtendimentoDiversos.getSeq());
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return voltar();
	}

	public void atualizaSuggestionsTipoAtendimento() {
		renderizaProjPesquisa = aelAtendimentoDiversos.getAelProjetoPesquisas() == null;
		renderizaLabExterno = aelAtendimentoDiversos.getAelLaboratorioExternos() == null;
		renderizaContrQualidade = aelAtendimentoDiversos.getAelCadCtrlQualidades() == null;
		renderizaCadaver = aelAtendimentoDiversos.getAelDadosCadaveres() == null;

		if (!renderizaProjPesquisa) {
			renderizaProjPesquisa = true;
			renderizaLabExterno = false;
			renderizaContrQualidade = false;
			renderizaCadaver = false;

		} else if (!renderizaLabExterno) {
			renderizaProjPesquisa = false;
			renderizaLabExterno = true;
			renderizaContrQualidade = false;
			renderizaCadaver = false;

		} else if (!renderizaContrQualidade) {
			renderizaProjPesquisa = false;
			renderizaLabExterno = false;
			renderizaContrQualidade = true;
			renderizaCadaver = false;

		} else if (!renderizaCadaver) {
			renderizaProjPesquisa = false;
			renderizaLabExterno = false;
			renderizaContrQualidade = false;
			renderizaCadaver = true;
		}
	}

	public List<AelProjetoPesquisas> obterProjetosPesquisa(String parametro) {
		return returnSGWithCount(
				questionarioExamesFacade
						.pesquisarProjetosPesquisaPorNumeroOuNome((String) parametro),
				this.obterProjetosPesquisaCount(parametro));
	}

	public Long obterProjetosPesquisaCount(String parametro) {
		return questionarioExamesFacade
				.pesquisarProjetosPesquisaPorNumeroOuNomeCount((String) parametro);
	}

	public List<AelLaboratorioExternos> obterLaboratoriosExternos(
			String parametro) {
		return returnSGWithCount(
				examesFacade.obterLaboratorioExternoList((String) parametro),
				this.obterLaboratoriosExternosCount(parametro));
	}

	public Long obterLaboratoriosExternosCount(String parametro) {
		return examesFacade
				.obterLaboratorioExternoListCount((String) parametro);
	}

	public List<AelCadCtrlQualidades> obterControlesQualidade(String parametro) {
		return returnSGWithCount(
				questionarioExamesFacade
						.obterCadCtrlQualidadesList((String) parametro),
				this.obterControlesQualidadeCount(parametro));
	}

	public Long obterControlesQualidadeCount(String parametro) {
		return questionarioExamesFacade
				.obterCadCtrlQualidadesListCount((String) parametro);
	}

	public List<AelDadosCadaveres> obterCadaveres(String parametro) {
		return returnSGWithCount(
				questionarioExamesFacade
						.obterDadosCadaveresList((String) parametro),
				this.obterCadaveresCount(parametro));
	}

	public Long obterCadaveresCount(String parametro) {
		return questionarioExamesFacade
				.obterDadosCadaveresListCount((String) parametro);
	}

	public String redirecionarPesquisaFonetica() {
		return "paciente-pesquisaPacienteComponente";
	}

	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(
			Object param) {
		return this.internacaoFacade
				.pesquisarInstituicaoHospitalarPorCodigoENome(param);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa) {
		return aghuFacade.listarPorSigla((String) objPesquisa);
	}
	
	public Long pesquisarEspecialidadesCount(String objPesquisa) {
		return aghuFacade.pesquisarEspecialidadePorNomeOuSiglaAtivoCount(objPesquisa);
	}
	
	public List<FccCentroCustos> pesquisarServicos(String objPesquisa) {
		return returnSGWithCount(centroCustoFacade
				.pesquisarCentroCustosPorCodigoDescricao((String) objPesquisa), this.pesquisarServicosCount(objPesquisa));
	}
	
	public Long pesquisarServicosCount(String objPesquisa) {
		return centroCustoFacade
				.pesquisarCentroCustosPorCodigoDescricaoCount((String) objPesquisa);
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(
			String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade
				.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa),
				this.obterAghUnidadesFuncionaisExecutorasCount(objPesquisa));
	}
	
	public Long obterAghUnidadesFuncionaisExecutorasCount(String objPesquisa) {
		return this.aghuFacade
				.pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(objPesquisa);
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			this.cadastrosApoioExamesFacade
					.persistirIdentificacaoUnidadeExecutora(
							this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		this.aelAtendimentoDiversos = null;

		if (StringUtils.isBlank(this.voltarPara)) {
			this.voltarPara = "atendimentoDiversoList";
		}
		return this.voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelAtendimentoDiversos getAelAtendimentoDiversos() {
		return aelAtendimentoDiversos;
	}

	public void setAelAtendimentoDiversos(
			AelAtendimentoDiversos aelAtendimentoDiversos) {
		this.aelAtendimentoDiversos = aelAtendimentoDiversos;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public boolean isRenderizaProjPesquisa() {
		return renderizaProjPesquisa;
	}

	public void setRenderizaProjPesquisa(boolean renderizaProjPesquisa) {
		this.renderizaProjPesquisa = renderizaProjPesquisa;
	}

	public boolean isRenderizaLabExterno() {
		return renderizaLabExterno;
	}

	public void setRenderizaLabExterno(boolean renderizaLabExterno) {
		this.renderizaLabExterno = renderizaLabExterno;
	}

	public boolean isRenderizaContrQualidade() {
		return renderizaContrQualidade;
	}

	public void setRenderizaContrQualidade(boolean renderizaContrQualidade) {
		this.renderizaContrQualidade = renderizaContrQualidade;
	}

	public boolean isRenderizaCadaver() {
		return renderizaCadaver;
	}

	public void setRenderizaCadaver(boolean renderizaCadaver) {
		this.renderizaCadaver = renderizaCadaver;
	}
}
