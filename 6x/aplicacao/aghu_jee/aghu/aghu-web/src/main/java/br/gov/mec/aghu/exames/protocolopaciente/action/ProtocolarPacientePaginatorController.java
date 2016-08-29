package br.gov.mec.aghu.exames.protocolopaciente.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProtocolarPacientePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelProtocoloInternoUnids> dataModel;

	private AelProtocoloInternoUnids parametroSelecionado;

	private static final long serialVersionUID = 3273310399035831904L;

	private static final String PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	private static final String PAGE_EXAMES_PROTOCOLAR_PACIENTE_CRUD = "exames-protocolarPacienteCRUD";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	List<AelProtocoloInternoUnids> protocolarPacienteList = new LinkedList<AelProtocoloInternoUnids>();

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private Integer numeroProtocolo;
	private Integer codigoPaciente;
	private Integer prontuario;
	private String nomePaciente;
	private AipPacientes paciente;

	private Integer pacCodigoFonetica;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	// private Integer pacCodigoExclusao;
	// private Short unfSeqExclusao;

	public void iniciar() {
	 


		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuário
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		this.verificarPacienteProntuario();
	
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Metodo que realiza a acao de <br>
	 * pesquisar na tela.
	 */
	public void pesquisar() {
		setarPaciente();
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}
	
	public void setarPaciente(){
		if(this.paciente!=null){
			this.codigoPaciente = this.paciente.getCodigo();
			this.prontuario = this.paciente.getProntuario();
		}
	}

	/**
	 * Metodo que limpa os campos <br>
	 * de pesquisa na tela.
	 */
	public void limparPesquisa() {
		this.paciente = null;
		this.numeroProtocolo = null;
		this.codigoPaciente = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.pacCodigoFonetica = null;
		this.exibirBotaoNovo = false;
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.examesLaudosFacade.pesquisarProtocoloInternoCount(this.getUnidadeExecutora().getSeq(), this.numeroProtocolo, this.codigoPaciente, this.prontuario);
	}

	@Override
	public List<AelProtocoloInternoUnids> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return this.examesLaudosFacade.pesquisarProtocoloInterno(this.getUnidadeExecutora().getSeq(), this.numeroProtocolo, this.codigoPaciente, this.prontuario, firstResult, maxResult,
				orderProperty, asc);
	}

	public String redirecionarPesquisaFonetica() {
		return PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}

	public void excluir() {
		try {

			AelProtocoloInternoUnids protocoloInterno = this.examesLaudosFacade.obterProtocoloInterno(this.parametroSelecionado.getId().getPacCodigo(), this.parametroSelecionado.getId()
					.getUnidadeFuncional().getSeq(), null);
			this.examesLaudosFacade.excluirAelProtocoloInternoUnids(protocoloInterno);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_PROTOCOLAR_PACIENTE");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} finally {
			this.parametroSelecionado = null;
		}
	}
	
	public String editar(){
		return PAGE_EXAMES_PROTOCOLAR_PACIENTE_CRUD;
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Persiste a unidade executora do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void verificarPacienteProntuario() {
		if (this.pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(this.pacCodigoFonetica);
		}

		if (paciente != null) {
			this.prontuario = paciente.getProntuario();
			this.nomePaciente = paciente.getNome();
			this.codigoPaciente = paciente.getCodigo();
		}
	}

	// ** GET/SET **/
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getNumeroProtocolo() {
		return numeroProtocolo;
	}

	public void setNumeroProtocolo(Integer numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public List<AelProtocoloInternoUnids> getProtocolarPacienteList() {
		return protocolarPacienteList;
	}

	public void setProtocolarPacienteList(List<AelProtocoloInternoUnids> protocolarPacienteList) {
		this.protocolarPacienteList = protocolarPacienteList;
	}

	public DynamicDataModel<AelProtocoloInternoUnids> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelProtocoloInternoUnids> dataModel) {
		this.dataModel = dataModel;
	}

	public AelProtocoloInternoUnids getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AelProtocoloInternoUnids parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
}
