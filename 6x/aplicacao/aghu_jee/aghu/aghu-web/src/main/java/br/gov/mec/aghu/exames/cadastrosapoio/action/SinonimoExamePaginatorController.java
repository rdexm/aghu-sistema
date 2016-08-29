package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSinonimoExameId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class SinonimoExamePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterSinonimosExame", "executar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterSinonimosExame", "executar"));
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelSinonimoExame> dataModel;

	private static final long serialVersionUID = 688886314098895163L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IPermissionService permissionService;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Variáveis para controle de edição
	private AelExames exame;

	// private String sigla;
	private DominioSituacao indSituacao;
	private Short codigoSinonimo;
	private AelSinonimoExame sinonimo;

	private boolean primeiraPesquisa = true;

	private AelSinonimoExame sinonimoRemover;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (this.codigoSinonimo != null) {
			this.sinonimo = this.examesFacade.obterAelSinonimoExamePorId(new AelSinonimoExameId(this.exame.getSigla(), codigoSinonimo));
		}

		// Inclusão
		if (this.sinonimo == null) {
			this.sinonimo = new AelSinonimoExame();
			this.sinonimo.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo
			AelSinonimoExameId id = new AelSinonimoExameId();
			id.setExaSigla(this.exame.getSigla());
			this.sinonimo.setId(id);
		}

		if (this.primeiraPesquisa) {
			this.dataModel.reiniciarPaginator();
		}
	
	}

	public void limparPesquisa() {
		this.indSituacao = null;
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.pesquisarSinonimosExamesCount(exame);
	}

	@Override
	public List<AelSinonimoExame> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AelSinonimoExame> result = this.cadastrosApoioExamesFacade.pesquisarSinonimosExames(exame, firstResult, maxResult, orderProperty, asc);
		if (result == null) {
			result = new ArrayList<AelSinonimoExame>();
		}

		this.primeiraPesquisa = false;

		return result;
	}

	public void confirmar() throws ApplicationBusinessException {
		try {
			if (validaCamposRequeridosEmBranco(this.sinonimo)) {

				transformarTextosCaixaAlta();

				// Determina o tipo ação e tipo de mensagem de confirmação
				if (this.sinonimo.getId() == null || this.sinonimo.getId().getSeqp() == null) {
					this.cadastrosApoioExamesFacade.inserirAelSinonimoExame(this.sinonimo);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_SINONIMO_EXAMES", this.sinonimo.getNome());

				} else {

					if (verificarAlteradoOutroUsuario(this.sinonimo)) {
						this.cancelarEdicao();
						return;
					}

					this.cadastrosApoioExamesFacade.atualizarAelSinonimoExame(this.sinonimo, true);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SINONIMO_EXAMES", this.sinonimo.getNome());

				}

				this.sinonimo = new AelSinonimoExame();
				this.sinonimo.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo
				AelSinonimoExameId id = new AelSinonimoExameId();
				id.setExaSigla(this.exame.getSigla());
				this.sinonimo.setId(id);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {

			this.codigoSinonimo = null;
		}
	}

	private void transformarTextosCaixaAlta() {
		this.sinonimo.setNome(this.sinonimo.getNome() == null ? null : this.sinonimo.getNome().trim().toUpperCase());
	}

	/**
	 * Testa campos em branco
	 * 
	 * @param plano
	 * @return
	 */
	private boolean validaCamposRequeridosEmBranco(AelSinonimoExame sinonimo) {
		boolean retorno = true;
		if (sinonimo != null) {
			if (StringUtils.isBlank(sinonimo.getNome())) {
				sinonimo.setNome(null);
				retorno = false;
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Nome");
			}
		}
		return retorno;
	}

	/**
	 * Método que realiza a ação do botão cancelar EDIÇÃO na tela de sinônimo
	 */
	public void cancelarEdicao() {
		this.sinonimo = new AelSinonimoExame();
		this.sinonimo.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo
		AelSinonimoExameId id = new AelSinonimoExameId();
		id.setExaSigla(this.exame.getSigla());
		this.sinonimo.setId(id);
		this.codigoSinonimo = null;
        this.sinonimo.setNome(null);
		this.dataModel.reiniciarPaginator();

	}

	/**
	 * Voltar da tela
	 */
	public String voltar() {

		this.exame = null;
		this.indSituacao = null;
		this.codigoSinonimo = null;
		this.sinonimo = null;
		this.primeiraPesquisa = true;
		this.sinonimoRemover = null;

		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

    public void editar(){

             if (this.codigoSinonimo != null) {

                   this.sinonimo = this.examesFacade.obterAelSinonimoExamePorId(new AelSinonimoExameId(this.exame.getSigla(), codigoSinonimo));
                   this.sinonimo.setNome(this.sinonimo.getNome());
                   this.sinonimo.setIndSituacao(this.sinonimo.getIndSituacao());
               }
          }

	public void excluir() {

		try {

			if (verificarAlteradoOutroUsuario(this.sinonimoRemover)) {
				return;
			}

			if (this.sinonimoRemover != null) {
				String nome = sinonimoRemover.getNome();
				this.cadastrosApoioExamesFacade.removerAelSinonimosExames(this.sinonimoRemover);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_SINONIMO_EXAMES", nome);
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_SINONIMO_EXAMES");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {

			this.sinonimo = new AelSinonimoExame();
			this.sinonimo.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo
			AelSinonimoExameId id = new AelSinonimoExameId();
			id.setExaSigla(this.exame.getSigla());
			this.sinonimo.setId(id);
			this.codigoSinonimo = null;
			this.sinonimoRemover = null;
			this.dataModel.reiniciarPaginator();
		}
	}

	private boolean verificarAlteradoOutroUsuario(AelSinonimoExame entidade) {
		if (entidade == null || this.examesFacade.obterAelSinonimoExamePorId(entidade.getId()) == null) {
			apresentarMsgNegocio(Severity.INFO, "OPTIMISTIC_LOCK");
			this.dataModel.reiniciarPaginator();
			return true;
		}
		return false;
	}

	/*
	 * Getters and Setters
	 */

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public AelSinonimoExame getSinonimo() {
		return sinonimo;
	}

	public void setSinonimo(AelSinonimoExame sinonimo) {
		this.sinonimo = sinonimo;
	}

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public Short getCodigoSinonimo() {
		return codigoSinonimo;
	}

	public void setCodigoSinonimo(Short codigoSinonimo) {
		this.codigoSinonimo = codigoSinonimo;
	}

	public DynamicDataModel<AelSinonimoExame> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelSinonimoExame> dataModel) {
		this.dataModel = dataModel;
	}

	public AelSinonimoExame getSinonimoRemover() {
		return sinonimoRemover;
	}

	public void setSinonimoRemover(AelSinonimoExame sinonimoRemover) {
		this.sinonimoRemover = sinonimoRemover;
	}

}