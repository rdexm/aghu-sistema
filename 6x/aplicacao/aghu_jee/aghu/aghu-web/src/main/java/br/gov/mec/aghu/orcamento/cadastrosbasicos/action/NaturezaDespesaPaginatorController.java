package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class NaturezaDespesaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654533755902L;

	private static final String NATUREZA_DESPESA_CRUD = "naturezaDespesaCRUD";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@Inject
	private SecurityController securityController;

	// filtros
	private FsoGrupoNaturezaDespesa grupoNatureza;
	private String descricaoNatureza;
	private DominioSituacao indSituacao;
	
	
	// atributos utilizados na exclusao
	private Integer idGrupoNatureza;
	private Byte idCodigoNatureza;
	private Boolean confirmaExclusao;
	
	
	@Inject @Paginator
	private DynamicDataModel<FsoNaturezaDespesa> dataModel;
	
	private FsoNaturezaDespesa selecionado;

	@PostConstruct
	protected void inicializar() {
		Boolean permissaoCadastrarApoioFinanceiroGravar = securityController.usuarioTemPermissao("cadastrarApoioFinanceiro","gravar");
		this.dataModel.setUserEditPermission(permissaoCadastrarApoioFinanceiroGravar);
		this.dataModel.setUserRemovePermission(permissaoCadastrarApoioFinanceiroGravar);
		this.begin(conversation);
	} 
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.grupoNatureza = null;
		this.descricaoNatureza = null;
		this.indSituacao = null;
		this.idCodigoNatureza = null;
		this.idGrupoNatureza = null;
		this.confirmaExclusao = false;
	}

	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaPorCodigoEDescricao(final String parametro)  throws BaseException {
		return cadastrosBasicosOrcamentoFacade.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(parametro);
	}
	
	public String getDescricaoGrupoNaturezaFormatada(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		if (grupoNaturezaDespesa != null) {
			return grupoNaturezaDespesa.getCodigo().toString() + " - " + grupoNaturezaDespesa.getDescricao();
		} else {
			return "";
		}
	}
	
	public String getDescricaoNaturezaFormatada(FsoNaturezaDespesa naturezaDespesa) {
		if (naturezaDespesa != null) {
			return naturezaDespesa.getId().getCodigo().toString() + " - " + naturezaDespesa.getDescricao();
		} else {
			return "";
		}
	}

	public String inserir() {
		return NATUREZA_DESPESA_CRUD;
	}

	public String editar() {
		return NATUREZA_DESPESA_CRUD;
	}
	
	public void excluir() {
		try {
			cadastrosBasicosOrcamentoFacade.excluirNaturezaDespesa(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NATUREZA_DESPESA_M06");
			setConfirmaExclusao(false);
			
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			setConfirmaExclusao(false);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosOrcamentoFacade.countPesquisaListaNaturezaDespesa(this.grupoNatureza, this.descricaoNatureza, this.indSituacao);
	}

	@Override
	public List<FsoNaturezaDespesa> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return cadastrosBasicosOrcamentoFacade.pesquisarListaNaturezaDespesa(firstResult,  maxResults, orderProperty, asc, this.getGrupoNatureza(), 
																				this.getDescricaoNatureza(), this.indSituacao);
	}

	public String getDescricaoNatureza() {
		return descricaoNatureza;
	}

	public void setDescricaoNatureza(String descricaoNatureza) {
		this.descricaoNatureza = descricaoNatureza;
	}

	public FsoGrupoNaturezaDespesa getGrupoNatureza() {
		return grupoNatureza;
	}

	public void setGrupoNatureza(FsoGrupoNaturezaDespesa grupoNatureza) {
		this.grupoNatureza = grupoNatureza;
	}

	public Integer getIdGrupoNatureza() {
		return idGrupoNatureza;
	}

	public void setIdGrupoNatureza(Integer idGrupoNatureza) {
		this.idGrupoNatureza = idGrupoNatureza;
	}

	public Byte getIdCodigoNatureza() {
		return idCodigoNatureza;
	}

	public void setIdCodigoNatureza(Byte idCodigoNatureza) {
		this.idCodigoNatureza = idCodigoNatureza;
	}

	public Boolean getConfirmaExclusao() {
		return confirmaExclusao;
	}

	public void setConfirmaExclusao(Boolean confirmaExclusao) {
		this.confirmaExclusao = confirmaExclusao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DynamicDataModel<FsoNaturezaDespesa> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FsoNaturezaDespesa> dataModel) {
		this.dataModel = dataModel;
	}

	public FsoNaturezaDespesa getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FsoNaturezaDespesa selecionado) {
		this.selecionado = selecionado;
	}
}
