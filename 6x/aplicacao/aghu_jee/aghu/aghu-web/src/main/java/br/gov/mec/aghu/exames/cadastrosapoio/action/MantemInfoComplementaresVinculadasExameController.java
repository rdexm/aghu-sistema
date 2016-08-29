package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MantemInfoComplementaresVinculadasExameController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterInformacoesComplementaresVinculadasExame", "executar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterInformacoesComplementaresVinculadasExame", "executar"));
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelExamesQuestionario> dataModel;

	private static final long serialVersionUID = -374090823391361988L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IPermissionService permissionService;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelExames exame;
	private AelMateriaisAnalises materialAnalise;
	private AelExamesQuestionario exameQuestionario;

	// Suggestion box e parâmetro selecionado
	private AelQuestionarios questionario;

	private Integer nroVias;
	private boolean situacao;

	private boolean modoEdicao;
	private boolean colorir;
	private boolean exibePainelCancelaAlteracao;

	// parametros recebidos
	private String emaExaSigla;
	private Integer emaManSeq;

	/**
	 * Mensagens
	 * 
	 * @author aghu
	 * 
	 */
	private enum MantemInfoComplementaresVinculadasExameControllerMessages {
		MENSAGEM_SUCESSO_INSERIR_EXAME_QUESTIONARIO, MENSAGEM_SUCESSO_ALTERAR_EXAME_QUESTIONARIO, MENSAGEM_SUCESSO_REMOVER_EXAME_QUESTIONARIO;
	}

	/**
	 * Executado quando a tela é acessada
	 */
	public void iniciar() {
	 

		
		this.limpar();
		this.carregarDadosIniciais();
		this.dataModel.reiniciarPaginator();
		
	
	}

	/**
	 * Preenche os campos (desabilitados) de acordo com os parametros recebidos da estoria 2233
	 */
	private void carregarDadosIniciais() {

		final Enum[] fetchArgsLeftJoin = {AelExamesMaterialAnalise.Fields.EXAME, AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE};

		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setExaSigla(emaExaSigla);
		id.setManSeq(emaManSeq);

		exameMaterialAnalise = examesFacade.obterAelExamesMaterialAnalisePorId(id, null, fetchArgsLeftJoin);
		exame = exameMaterialAnalise.getAelExames();
		materialAnalise = exameMaterialAnalise.getAelMateriaisAnalises();
		situacao = Boolean.TRUE;
	}

	@Override
	public List<AelExamesQuestionario> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return questionarioExamesFacade.buscarAelExamesQuestionario(emaExaSigla, emaManSeq, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return examesFacade.buscarAelExamesQuestionarioCount(emaExaSigla, emaManSeq);
	}

	/**
	 * Suggestion box questionario
	 * 
	 * @param param
	 * @return
	 */
	public List<AelQuestionarios> pesquisarQuestionarios(String param) {
		return this.returnSGWithCount(examesFacade.pesquisarAelQuestionarios(param),
				this.pesquisarAelQuestionariosCount(param));
	}
	
	public Long pesquisarAelQuestionariosCount(String param) {
		return examesFacade.pesquisarAelQuestionariosCount(param);
	}

	/**
	 * Insere novo registro ou atualiza registro já existente
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void gravar() throws ApplicationBusinessException {

		String msgConfirmacao = "";
		if (modoEdicao) {
			msgConfirmacao = MantemInfoComplementaresVinculadasExameControllerMessages.MENSAGEM_SUCESSO_ALTERAR_EXAME_QUESTIONARIO.toString();
		} else {
			exameQuestionario = new AelExamesQuestionario();
			msgConfirmacao = MantemInfoComplementaresVinculadasExameControllerMessages.MENSAGEM_SUCESSO_INSERIR_EXAME_QUESTIONARIO.toString();
		}

		exameQuestionario.setAelQuestionarios(questionario);
		exameQuestionario.setExamesMaterialAnalise(exameMaterialAnalise);
		exameQuestionario.setNroVias(nroVias);

		if (situacao) {
			exameQuestionario.setSituacao(DominioSituacao.A);
		} else {
			exameQuestionario.setSituacao(DominioSituacao.I);
		}

		try {

			questionarioExamesFacade.persistirAelExamesQuestionario(exameQuestionario);

			if (StringUtils.isNotBlank(msgConfirmacao)) {
				this.apresentarMsgNegocio(Severity.INFO, msgConfirmacao, exameMaterialAnalise.getNomeUsualMaterial());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
		this.dataModel.reiniciarPaginator();
		
	}

	/**
	 * Exclui o registro selecionado e atualiza a grid de pesquisa
	 */
	public void excluir() {
		try {
			String descricao = exameQuestionario.getExamesMaterialAnalise().getNomeUsualMaterial();
			questionarioExamesFacade.excluirAelExamesQuestionario(this.exameQuestionario.getId());
			// questionarioExamesFacade.flush();
			this.apresentarMsgNegocio(Severity.INFO, MantemInfoComplementaresVinculadasExameControllerMessages.MENSAGEM_SUCESSO_REMOVER_EXAME_QUESTIONARIO.toString(), descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		limpar();
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Habilita o modo edição
	 * 
	 * @param obj
	 */
	public void editar() {
		this.nroVias = this.exameQuestionario.getNroVias();
		this.questionario = this.exameQuestionario.getAelQuestionarios();
		if (this.exameQuestionario.getSituacao() != null) {
			if (DominioSituacao.A.equals(this.exameQuestionario.getSituacao())) {
				this.situacao = Boolean.TRUE;
			} else {
				this.situacao = Boolean.FALSE;
			}
		}
		setModoEdicao(Boolean.TRUE);
		setColorir(Boolean.TRUE);
	}

	/**
	 * Cancela a edição
	 */
	public void cancelarEdicao() {
		this.limpar();
		//this.//setIgnoreInitPageConfig(true);
	}

	/**
	 * Seleciona o registro que poderá ser excluido apos confirmação
	 * 
	 * @param obj
	 */
	public void selecionar(AelExamesQuestionario obj) {
		this.exameQuestionario = obj;
	}

	/**
	 * Retorna para a estoria chamadora
	 * 
	 * @return
	 */
	public String voltar() {
		limpar();
		this.exameMaterialAnalise = null;
		this.exame = null;
		this.materialAnalise = null;
		this.exameQuestionario = null;
		this.questionario = null;
		this.nroVias = null;
		this.situacao = false;
		this.modoEdicao = false;
		this.colorir = false;
		this.exibePainelCancelaAlteracao = false;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.dataModel.limparPesquisa();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * 
	 */
	private void limpar() {
		setNroVias(null);
		setQuestionario(null);
		setModoEdicao(Boolean.FALSE);
		setColorir(Boolean.FALSE);
		setSituacao(Boolean.TRUE);
		setExibePainelCancelaAlteracao(Boolean.FALSE);
	}

	/**
	 * Verifica a linha selecionada
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isRegistroSelecionado(AelExamesQuestionario obj) {
		return exameQuestionario != null && exameQuestionario.equals(obj) && colorir;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public AelMateriaisAnalises getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(AelMateriaisAnalises materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public AelQuestionarios getQuestionario() {
		return questionario;
	}

	public void setQuestionario(AelQuestionarios questionario) {
		this.questionario = questionario;
	}

	public Integer getNroVias() {
		return nroVias;
	}

	public void setNroVias(Integer nroVias) {
		this.nroVias = nroVias;
	}

	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isColorir() {
		return colorir;
	}

	public void setColorir(boolean colorir) {
		this.colorir = colorir;
	}

	public boolean isExibePainelCancelaAlteracao() {
		return exibePainelCancelaAlteracao;
	}

	public void setExibePainelCancelaAlteracao(boolean exibePainelCancelaAlteracao) {
		this.exibePainelCancelaAlteracao = exibePainelCancelaAlteracao;
	}

	public AelExamesQuestionario getExameQuestionario() {
		return exameQuestionario;
	}

	public void setExameQuestionario(AelExamesQuestionario exameQuestionario) {
		this.exameQuestionario = exameQuestionario;
	}

	public DynamicDataModel<AelExamesQuestionario> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelExamesQuestionario> dataModel) {
		this.dataModel = dataModel;
	}
}
