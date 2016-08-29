package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de listagem de situações da emergência.
 * 
 * @author luismoura
 * 
 */
public class SituacaoEmergenciaPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 3601546988357451505L;

	private final String PAGE_CAD_SIT_EMERG = "situacaoEmergenciaCRUD";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private Long codigo;
	private String descricao;
	private DominioSituacao indSituacao;

	@Inject @Paginator
	private DynamicDataModel<MamSituacaoEmergencia> dataModel;

	private MamSituacaoEmergencia situacaoEmergencia;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		situacaoEmergencia = new MamSituacaoEmergencia();
		indSituacao = DominioSituacao.A;
	}

	/**
	 * Ação do botão PESQUISAR da pagina de listagem de situações da emergência.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Ação do botão LIMPAR da pagina de listagem de situações da emergência.
	 */
	public void limparPesquisa() {
		this.codigo = null;
		this.descricao = null;
		this.indSituacao = DominioSituacao.A;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão EXCLUIR da pagina de listagem de situações da emergência.
	 */
	public void excluir() {
		try {
			if (situacaoEmergencia != null && situacaoEmergencia.getSeq() != null) {
				this.emergenciaFacade.excluirMamSituacaoEmergencia(situacaoEmergencia);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_SIT_EMERGS");
			}
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão NOVO da pagina de listagem de situações da emergência.
	 * 
	 * @return
	 */
	public String novo() {
		this.setSituacaoEmergencia(null);
		return PAGE_CAD_SIT_EMERG;
	}

	/**
	 * Ação do botão EDITAR da pagina de listagem de situações da emergência.
	 * 
	 * @return
	 */
	public String editar() {
		return PAGE_CAD_SIT_EMERG;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Short codShort = null;
		if(codigo != null){
			if(codigo.intValue() > Short.MAX_VALUE){
				return 0l;
			}
			codShort = codigo.shortValue();
		}
		return this.emergenciaFacade.pesquisarSituacoesEmergenciaCount(codShort, descricao, indSituacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MamSituacaoEmergencia> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		Short codShort = null;
		if(codigo != null){
			if(codigo.intValue() > Short.MAX_VALUE){
				return new ArrayList<MamSituacaoEmergencia>();
			}
			codShort = codigo.shortValue();
		}
		return this.emergenciaFacade.pesquisarSituacoesEmergencia(firstResult, maxResults, orderProperty, asc, codShort, descricao, indSituacao);
	}

	// ### GETs e SETs ###

	public DynamicDataModel<MamSituacaoEmergencia> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamSituacaoEmergencia> dataModel) {
		this.dataModel = dataModel;
	}

	public MamSituacaoEmergencia getSituacaoEmergencia() {
		return situacaoEmergencia;
	}

	public void setSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) {
		this.situacaoEmergencia = situacaoEmergencia;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
}
