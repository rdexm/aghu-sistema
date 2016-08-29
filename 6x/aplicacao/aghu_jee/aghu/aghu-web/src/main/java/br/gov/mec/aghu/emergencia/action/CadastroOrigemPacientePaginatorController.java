package br.gov.mec.aghu.emergencia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de listagem / cadastro de Origem Paciente da
 * emergência.
 * 
 * @author felipe.rocha
 * 
 */
public class CadastroOrigemPacientePaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 6589546988357451478L;

	@EJB
	private IEmergenciaFacade emergenciaFacade;

	private MamOrigemPaciente mamOrigemPaciente;
	private String descricao;
	private Boolean indSituacao;
	private Long pesquisaSeq;
	private boolean permissaoManterOrigemPaciente;
	private String pesquisaDescricao;
	private DominioSituacao pesquisaIndSituacao;
	// alterar do registro
	private boolean alterar;
	private Integer seq;
	@Inject @Paginator
	private DynamicDataModel<MamOrigemPaciente> dataModel;
	private boolean pesquisaAtiva;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		// atualiza permissao para remover
		this.dataModel.setUserRemovePermission(mostraAcao());
		// atualiza permissao para visualizar
		this.setPermissaoManterOrigemPaciente(getPermissionService()
				.usuarioTemPermissao(obterLoginUsuarioLogado(),
						"manterOrigemPaciente", "gravar"));
		
		
		this.indSituacao = true;
	}

	private void limparCampos(Boolean exibeListaPaciente) {
		this.mamOrigemPaciente = null;
		this.descricao = null;
		this.indSituacao = true;
		if(!exibeListaPaciente) {
			this.pesquisaDescricao = null;
			this.pesquisaIndSituacao = null;
			this.pesquisaSeq = null;
		}
		this.seq = null;
		this.alterar = false;
		this.pesquisaAtiva = exibeListaPaciente;
		this.dataModel.setUserRemovePermission(mostraAcao());
	}

	/**
	 * Ação do botão PESQUISAR da pagina de listagem de especialidades da
	 * emergência.
	 */
	public void pesquisar() {
		this.alterar = false;
		this.pesquisaAtiva = true;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setUserRemovePermission(mostraAcao());
	}

	/**
	 * Ação do botão LIMPAR da pagina de listagem de especialidades da
	 * emergência.
	 */
	public void limparPesquisa() {
		limparCampos(false);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão EXCLUIR da pagina de listagem de origem paciente
	 */
	public void excluir() {
		try {
			if (mamOrigemPaciente != null) {
				this.emergenciaFacade.excluirMamOrigemPaciente(mamOrigemPaciente.getSeq());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_EXCLUSAO_ORIGEM_PACIENTE_SUCESSO");
			}
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão EDITAR da pagina de listagem de origem paciente.
	 * 
	 * @return
	 */
	public void editar() {
		if (mamOrigemPaciente != null) {
			this.descricao = mamOrigemPaciente.getDescricao();
			this.indSituacao = mamOrigemPaciente.getIndSituacao().isAtivo();
			this.alterar = true;
			this.dataModel.setUserRemovePermission(mostraAcao());
		}
	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR da pagina de origem paciente
	 */
	public void inativar() {
		try {
			if (mamOrigemPaciente != null) {
				this.emergenciaFacade
						.ativarInativarMamOrigemPaciente(mamOrigemPaciente);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_ORIGEM_PACIENTE_SITUACAO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.pesquisarOrigensPacienteCount(
				getSeqPesquisaInteger(), getPesquisaIndSituacao(),
				getPesquisaDescricao());
	}

	@Override
	public List<MamOrigemPaciente> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return this.emergenciaFacade.pesquisarOrigensPaciente(firstResult,
				maxResults,
				orderProperty == null ? MamOrigemPaciente.Fields.SEQ.toString()
						: orderProperty, asc, getSeqPesquisaInteger(),
				getPesquisaIndSituacao(), getPesquisaDescricao());
	}

	/**
	 * Ação Adicionar/Atualizat Origem Paciente
	 */
	public void persistir() {
		try {
			if (mamOrigemPaciente == null || !isAlterar()) {
				this.emergenciaFacade.persistirOrigemPaciente(
						getConvertBooleanToDominioSituacao(this.indSituacao),
						this.descricao);
				this.dataModel.reiniciarPaginator();
				limparCampos(true);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ORIGEM_PACIENTE_INSERIDA_SUCESSO");
			} else {
				alterarOrigemPaciente();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void alterarOrigemPaciente() throws ApplicationBusinessException {
		this.emergenciaFacade.alterarOrigemPaciente(
				getConvertBooleanToDominioSituacao(this.indSituacao),
				this.descricao, this.mamOrigemPaciente);
		this.dataModel.reiniciarPaginator();
		limparCampos(true);
		apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EDICAO_ORIGEM_PACIENTE");
		this.alterar = false;
		this.dataModel.setUserRemovePermission(mostraAcao());
	}

	public void cancelarEdicao() {
		limparCampos(true);
	}
	
	/**
	 * Retorna o seqPequisa formato que não estoura caso seja maior que o Integer suporta
	 * @return
	 */
	private Integer getSeqPesquisaInteger(){
		if(getPesquisaSeq() != null){
			if(getPesquisaSeq().intValue() > Integer.MAX_VALUE){
				return 0;
			}
			return getPesquisaSeq().intValue();
		}
		return null;
	}
	
	/**
	 * Método que verifica se deve aparecer a acao
	 */
	public boolean mostraAcao() {
		if (isPermissaoManterOrigemPaciente() && !alterar) {
			return true;
		} else {
			return false;
		}
	}
	

	// ### GETs e SETs ###

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public MamOrigemPaciente getMamOrigemPaciente() {
		return mamOrigemPaciente;
	}

	public void setMamOrigemPaciente(MamOrigemPaciente mamOrigemPaciente) {
		this.mamOrigemPaciente = mamOrigemPaciente;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	private DominioSituacao getConvertBooleanToDominioSituacao(boolean valor) {
		if (valor) {
			return DominioSituacao.A;
		} else {
			return DominioSituacao.I;
		}
	}

	public Boolean getConvertDominioSituacaoToBoolean(DominioSituacao situacao) {
		if (situacao == DominioSituacao.A) {
			return true;
		} else {
			return false;
		}
	}

	public DynamicDataModel<MamOrigemPaciente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamOrigemPaciente> dataModel) {
		this.dataModel = dataModel;
	}

	public Long getPesquisaSeq() {
		return pesquisaSeq;
	}

	public void setPesquisaSeq(Long pesquisaSeq) {
		this.pesquisaSeq = pesquisaSeq;
	}

	public String getPesquisaDescricao() {
		return pesquisaDescricao;
	}

	public void setPesquisaDescricao(String pesquisaDescricao) {
		this.pesquisaDescricao = pesquisaDescricao;
	}

	public DominioSituacao getPesquisaIndSituacao() {
		return pesquisaIndSituacao;
	}

	public void setPesquisaIndSituacao(DominioSituacao pesquisaIndSituacao) {
		this.pesquisaIndSituacao = pesquisaIndSituacao;
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public boolean isPermissaoManterOrigemPaciente() {
		return permissaoManterOrigemPaciente;
	}

	public void setPermissaoManterOrigemPaciente(
			boolean permissaoManterOrigemPaciente) {
		this.permissaoManterOrigemPaciente = permissaoManterOrigemPaciente;
	}

	
	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

}
