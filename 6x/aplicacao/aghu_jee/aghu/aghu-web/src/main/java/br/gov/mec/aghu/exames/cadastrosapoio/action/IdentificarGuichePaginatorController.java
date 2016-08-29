package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class IdentificarGuichePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelIdentificarGuicheVO> dataModel;

	private AelIdentificarGuicheVO parametroSelecionado;

	private static final long serialVersionUID = 4187465647690536801L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AelUnidExecUsuario usuarioUnidadeExecutora;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private Short codigo;
	// private Short seqEdicao;
	private String descricao;
	private DominioSimNao ocupado;

	public void iniciar() {
	 

		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			this.usuarioUnidadeExecutora = null;
		}

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		pesquisar();
	
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		// Limpa filtro
		this.codigo = null;
		this.descricao = null;
		this.ocupado = null;
		this.parametroSelecionado = null;

		pesquisar();
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {

			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
			this.dataModel.reiniciarPaginator();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void persistirIdentificacaoUnidadeExecutoraNula() {
		this.dataModel.setPesquisaAtiva(false);
	}

	@Override
	public List<AelIdentificarGuicheVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (orderProperty == null || ("").equals(orderProperty)) {
			orderProperty = AelCadGuiche.Fields.DESCRICAO.toString();
			asc = true;
		}

		return examesPatologiaFacade.pesquisarAelCadGuiche(firstResult, maxResult, orderProperty, asc, createFiltros());
	}

	@Override
	public Long recuperarCount() {
		return examesPatologiaFacade.pesquisarAelCadGuicheCount(createFiltros());
	}

	private AelCadGuiche createFiltros() {
		AelCadGuiche filtros = new AelCadGuiche();
		filtros.setSeq(codigo);
		filtros.setDescricao(descricao);
		filtros.setOcupado(ocupado);
		filtros.setIndSituacao(DominioSituacao.A);
		filtros.setUnidadeFuncional(unidadeExecutora);
		return filtros;
	}

	public void persistirIdentificacaoGuiche() {
		persistirIdentificacaoGuiche(this.parametroSelecionado.getSeq());
		this.parametroSelecionado = null;
	}

	public void persistirIdentificacaoGuiche(final Short seq) {

		try {

			final AelCadGuiche guiche = examesPatologiaFacade.obterAelCadGuiche(seq);

			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			examesPatologiaFacade.persistirIdentificacaoGuiche(guiche, unidadeExecutora, DominioSituacao.A, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_CAD_GUICHE_UPDATE_SUCESSO", guiche.getDescricao());

			this.dataModel.reiniciarPaginator();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR, e));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getOcupado() {
		return ocupado;
	}

	public void setOcupado(DominioSimNao ocupado) {
		this.ocupado = ocupado;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DynamicDataModel<AelIdentificarGuicheVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelIdentificarGuicheVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AelIdentificarGuicheVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AelIdentificarGuicheVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}