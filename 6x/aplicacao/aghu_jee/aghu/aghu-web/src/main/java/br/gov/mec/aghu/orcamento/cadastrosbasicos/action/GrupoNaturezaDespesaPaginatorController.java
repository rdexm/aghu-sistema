package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoNaturezaDespesaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8281168632208027272L;

	private static final String GRUPO_NATUREZA_DESPESA_CRUD = "grupoNaturezaDespesaCRUD";
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	private FsoGrupoNaturezaDespesaCriteriaVO criteria;

	@Inject @Paginator
	private DynamicDataModel<FsoGrupoNaturezaDespesa> dataModel;
	
	private FsoGrupoNaturezaDespesa selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		criteria = new FsoGrupoNaturezaDespesaCriteriaVO();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		criteria = new FsoGrupoNaturezaDespesaCriteriaVO();
	}
	
	public void pesquisar() {
		if (criteria.getDescricao() != null) {
			criteria.setDescricao(StringUtils.trimToNull(criteria.getDescricao()));
		}
		
		dataModel.reiniciarPaginator();
	}
	
	public String inserir() {
		return GRUPO_NATUREZA_DESPESA_CRUD;
	}
	
	public String editar() {
		return GRUPO_NATUREZA_DESPESA_CRUD;
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosOrcamentoFacade.contarGruposNaturezaDespesa(criteria);
	}

	@Override
	public List<FsoGrupoNaturezaDespesa> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return cadastrosBasicosOrcamentoFacade.pesquisarGruposNaturezaDespesa(criteria, first, max, FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), true);
	}

	public void excluir() {
		try {
			cadastrosBasicosOrcamentoFacade.excluir(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO, "GRUPO_NATUREZA_DESPESA_EXCLUIDO_SUCESSO", selecionado.getDescricao());
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public DynamicDataModel<FsoGrupoNaturezaDespesa> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FsoGrupoNaturezaDespesa> dataModel) {
		this.dataModel = dataModel;
	}

	public FsoGrupoNaturezaDespesaCriteriaVO getCriteria() {
		return criteria;
	}

	public FsoGrupoNaturezaDespesa getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FsoGrupoNaturezaDespesa selecionado) {
		this.selecionado = selecionado;
	}
}