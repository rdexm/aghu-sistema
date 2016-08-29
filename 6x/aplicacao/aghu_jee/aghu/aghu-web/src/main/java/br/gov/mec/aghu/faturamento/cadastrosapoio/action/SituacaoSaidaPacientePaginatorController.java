package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da tela Detalhar Motivos de Saída de Pacientes
 * 
 * @author rafael.silvestre
 */
public class SituacaoSaidaPacientePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3148674509460714658L;
	
	private static final String PAGE_CADASTRAR_SITUACAO_SAIDA_PACIENTE = "faturamento-situacaoSaidaPacienteCRUD";
	
	private static final String PAGE_PESQUISAR_MOTIVO_SAIDA_PACIENTE = "faturamento-motivoSaidaPacienteList";
	
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_SITUACAO_SAIDA_PACIENTE";
	
	private static final String MENSAGEM_SUCESSO_EXCLUSAO = "MENSAGEM_SUCESSO_EXCLUSAO_SITUACAO_SAIDA_PACIENTE";
	
	private static final String MANTER_CADASTROS_BASICOS_FATURAMENTO = "manterCadastrosBasicosFaturamento";
	
	private static final String EXECUTAR = "executar";

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject 
	@Paginator
	private DynamicDataModel<FatSituacaoSaidaPaciente> dataModel;
	
	private FatMotivoSaidaPaciente fatMotivoSaidaPaciente;
	
	private FatSituacaoSaidaPaciente entitySelecionado;

	@PostConstruct
	protected void inicializar() {

		begin(conversation);
	}
	
	/**
	 * Valida se o usuário possui permissão para editar/excluir registros.
	 * Realiza consulta das entidades cadastradas.
	 */
	public void iniciar() {
	
		if (super.isValidInitMethod()) {
		
		
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), MANTER_CADASTROS_BASICOS_FATURAMENTO, EXECUTAR);
		this.getDataModel().setUserEditPermission(permissao);
		this.getDataModel().setUserRemovePermission(permissao);
		this.dataModel.reiniciarPaginator();
		}
	}
	
	@Override
	public List<FatSituacaoSaidaPaciente> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return this.faturamentoApoioFacade.recuperarListaPaginadaSituacaoSaidaPaciente(firstResult, maxResult, orderProperty, asc, this.fatMotivoSaidaPaciente);
	}

	@Override
	public Long recuperarCount() {
		
		return faturamentoApoioFacade.recuperarCountMotivoSaidaPaciente(this.fatMotivoSaidaPaciente);
	}
	
	/**
	 * Trunca texto da Grid caso o mesmo ultrapasse o tamanho indicado.
	 * 
	 * @param item {@link String}
	 * @param tamanhoMaximo {@link Integer}
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {

		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	
	/**
	 * Ação do link Editar
	 */
	public String editar() {
		
		return incluir();
	}
	
	/**
	 * Ação do link Ativar/Inativar
	 */
	public void ativarInativar() {
		
		if (this.entitySelecionado != null) {
			
			if (DominioSituacao.A.equals(this.entitySelecionado.getSituacao())) {
				
				this.entitySelecionado.setSituacao(DominioSituacao.I);
				
			} else {
				
				this.entitySelecionado.setSituacao(DominioSituacao.A);
			}
			
			try {
				
				this.faturamentoApoioFacade.gravarSituacaoSaidaPaciente(this.entitySelecionado);
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				
			} catch (ApplicationBusinessException e) {
				
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Ação do botão Novo 
	 */
	public String incluir() {
		
		return PAGE_CADASTRAR_SITUACAO_SAIDA_PACIENTE;
	}
	
	/**
	 * Método responsável pela navegação para a página anterior.
	 * 
	 * @return
	 */
	public String voltar() {
		
		this.fatMotivoSaidaPaciente = null;
		
		return PAGE_PESQUISAR_MOTIVO_SAIDA_PACIENTE;
	}

	/**
	 * Ação do link Excluir
	 */
	public void excluir() {
		
		if (this.entitySelecionado != null && this.entitySelecionado.getId() != null) {
		
			try {
				
				this.faturamentoApoioFacade.removerSituacaoSaidaPaciente(this.entitySelecionado.getId());
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EXCLUSAO);
				
			} catch (ApplicationBusinessException e) {
				
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 *
	 * GETs e SETs
	 * 
	 */
	public FatMotivoSaidaPaciente getFatMotivoSaidaPaciente() {
		return fatMotivoSaidaPaciente;
	}

	public void setFatMotivoSaidaPaciente(FatMotivoSaidaPaciente fatMotivoSaidaPaciente) {
		this.fatMotivoSaidaPaciente = fatMotivoSaidaPaciente;
	}

	public DynamicDataModel<FatSituacaoSaidaPaciente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatSituacaoSaidaPaciente> dataModel) {
		this.dataModel = dataModel;
	}

	public FatSituacaoSaidaPaciente getEntitySelecionado() {
		return entitySelecionado;
	}

	public void setEntitySelecionado(FatSituacaoSaidaPaciente entitySelecionado) {
		this.entitySelecionado = entitySelecionado;
	}

}
