package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterUnidadeMedidaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -6432690140585569207L;

	private static final String MANTER_UNIDADE_MEDIDA_CRUD = "manterUnidadeMedidaCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	//Campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	
	@Inject @Paginator
	private DynamicDataModel<AelUnidMedValorNormal> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		codigo = null;
		descricao = null;
		indSituacao = null;
		dataModel.limparPesquisa();
	}

	public void editar(AelUnidMedValorNormal aelUnidMedValorNormal) {
		try {
			cadastrosApoioExamesFacade.ativarInativarAelUnidMedValorNormal(aelUnidMedValorNormal);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_UNIDADE_MEDIDA", aelUnidMedValorNormal.getDescricao());
			dataModel.reiniciarPaginator();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_UNIDADE_MEDIDA_CRUD;
	}

	public boolean isActive(AelUnidMedValorNormal unidadeMedida) {
		return (unidadeMedida.getIndSituacao() == DominioSituacao.A);
	}
	
	@Override
	public List<AelUnidMedValorNormal> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarDadosBasicosUnidMedValorNormal( firstResult, maxResult, AelUnidMedValorNormal.Fields.DESCRICAO.toString(), true, 
																	 new AelUnidMedValorNormal(codigo, descricao, indSituacao, null, null));
	}
	
	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarAelUnidMedValorNormalCount(new AelUnidMedValorNormal(codigo, descricao, indSituacao, null, null));
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
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

	public DynamicDataModel<AelUnidMedValorNormal> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelUnidMedValorNormal> dataModel) {
		this.dataModel = dataModel;
	}
}