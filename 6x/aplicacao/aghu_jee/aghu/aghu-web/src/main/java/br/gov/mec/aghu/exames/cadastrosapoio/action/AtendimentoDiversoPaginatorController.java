package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class AtendimentoDiversoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelAtendimentoDiversos> dataModel;

	private static final long serialVersionUID = 1261995388391265335L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;	
	
	@Inject
	private SecurityController securityController;

	
	private AelAtendimentoDiversos filtros = new AelAtendimentoDiversos();
	
	private Integer seqExclusao;
	
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
	private Integer pacCodigo;

	private static final String PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	
	private AelAtendimentoDiversos aelAtendimentoDiversos;
	
	public void iniciar() {

		atualizaSuggestionsTipoAtendimento();
		
		// Obtem o USUARIO da unidade executora
		if(unidadeExecutora == null){
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				this.usuarioUnidadeExecutora = null;
			}			
			// Resgata a unidade executora associada ao usuario
			if(this.usuarioUnidadeExecutora != null){
				this.unidadeExecutora =  this.usuarioUnidadeExecutora.getUnfSeq();
			}
		}
		
		if (pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			if(paciente != null){
				prontuario = paciente.getProntuario();
				codPac = paciente.getCodigo();
			}
		}
		
		this.dataModel.setUserEditPermission(securityController.usuarioTemPermissao("cadastrarAtendimentoDiversos", "executar"));
		this.dataModel.setUserRemovePermission(securityController.usuarioTemPermissao("cadastrarAtendimentoDiversos", "executar"));
	}

	public void excluir() {
		try {
		
			cadastrosApoioExamesFacade.excluirAelAtendimentoDiversos(aelAtendimentoDiversos);
		
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ATENDIMENTO_DIVERSOS", aelAtendimentoDiversos.getSeq());
			this.dataModel.reiniciarPaginator();
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisar() {
		if(paciente != null){
			filtros.setAipPaciente(paciente);
		}
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		filtros = new AelAtendimentoDiversos();
		paciente = null;
		pacCodigoFonetica = null;
		codPac = null;
		prontuario = null;
//		renderizaProjPesquisa = true;
//		renderizaLabExterno = true;
//		renderizaContrQualidade = true;
//		renderizaCadaver = true;
		atualizaSuggestionsTipoAtendimento();
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public void atualizaSuggestionsTipoAtendimento(){
		renderizaProjPesquisa = filtros.getAelProjetoPesquisas() == null;
		renderizaLabExterno = filtros.getAelLaboratorioExternos() == null;
		renderizaContrQualidade = filtros.getAelCadCtrlQualidades() == null;
		renderizaCadaver = filtros.getAelDadosCadaveres() == null;
		
		if(!renderizaProjPesquisa){
			renderizaProjPesquisa = true;
			renderizaLabExterno = false;
			renderizaContrQualidade = false;
			renderizaCadaver = false;
			
		} else if(!renderizaLabExterno){
			renderizaProjPesquisa = false;
			renderizaLabExterno = true;
			renderizaContrQualidade = false;
			renderizaCadaver = false;
			
		} else if(!renderizaContrQualidade){
			renderizaProjPesquisa = false;
			renderizaLabExterno = false;
			renderizaContrQualidade = true;
			renderizaCadaver = false;
			
		} else if(!renderizaCadaver){
			renderizaProjPesquisa = false;
			renderizaLabExterno = false;
			renderizaContrQualidade = false;
			renderizaCadaver = true;
		}
	}
	

	@Override
	public List<AelAtendimentoDiversos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.examesLaudosFacade.pesquisarAelAtendimentoDiversos(filtros, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {		
			
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void persistirIdentificacaoUnidadeExecutoraNula() {
		this.dataModel.setPesquisaAtiva(false);
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesLaudosFacade.pesquisarAelAtendimentoDiversosCount(filtros);
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

	public List<AelProjetoPesquisas> obterProjetosPesquisa(String parametro) {
		return this.returnSGWithCount(this.questionarioExamesFacade
				.pesquisarProjetosPesquisaPorNumeroOuNome(parametro), this
				.obterProjetosPesquisaCount((String) parametro));
	}

	public Long obterProjetosPesquisaCount(String parametro) {
		return questionarioExamesFacade
				.pesquisarProjetosPesquisaPorNumeroOuNomeCount((String) parametro);
	}

	public List<AelLaboratorioExternos> obterLaboratoriosExternos(
			String parametro) {
		return this.returnSGWithCount(this.examesFacade
				.obterLaboratorioExternoList((String) parametro), this
				.obterLaboratoriosExternosCount((String) parametro));
	}

	public Long obterLaboratoriosExternosCount(String parametro) {
		return examesFacade
				.obterLaboratorioExternoListCount((String) parametro);
	}

	public List<AelCadCtrlQualidades> obterControlesQualidade(String parametro) {
		return this.returnSGWithCount(this.questionarioExamesFacade
				.obterCadCtrlQualidadesList((String) parametro), this
				.obterControlesQualidadeCount((String) parametro));
	}

	public Long obterControlesQualidadeCount(String parametro) {
		return questionarioExamesFacade
				.obterCadCtrlQualidadesListCount((String) parametro);
	}

	public List<AelDadosCadaveres> obterCadaveres(String parametro) {
		return this.returnSGWithCount(this.questionarioExamesFacade
				.obterDadosCadaveresList((String) parametro), this
				.obterCadaveresListCount((String) parametro));
	}

	public Long obterCadaveresListCount(String parametro) {
		return questionarioExamesFacade
				.obterDadosCadaveresListCount((String) parametro);
	}
	
	public String redirecionarPesquisaFonetica(){
		return PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}	
	
	public AelAtendimentoDiversos getFiltros() {
		return filtros;
	}

	public void setFiltros(AelAtendimentoDiversos filtros) {
		this.filtros = filtros;
	}

	public Integer getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Integer seqExclusao) {
		this.seqExclusao = seqExclusao;
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
		public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}



	public DynamicDataModel<AelAtendimentoDiversos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelAtendimentoDiversos> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public String redirecionarEdicao(){
		return "atendimentoDiverso";
	}

	public AelAtendimentoDiversos getAelAtendimentoDiversos() {
		return aelAtendimentoDiversos;
	}

	public void setAelAtendimentoDiversos(
			AelAtendimentoDiversos aelAtendimentoDiversos) {
		this.aelAtendimentoDiversos = aelAtendimentoDiversos;
	}
}