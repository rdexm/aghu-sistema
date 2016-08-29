package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterAnticoagulantePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7125574343156680508L;

	private static final String MANTER_ANTICOAGULANTE_CRUD = "manterAnticoagulanteCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	//Campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;

	@Inject @Paginator
	private DynamicDataModel<AelAnticoagulante> dataModel;
	
	private AelAnticoagulante selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		codigo = null;
		descricao = null;
		indSituacao = null;
	}
	
	@Override
	public List<AelAnticoagulante> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarDadosBasicosAnticoagulantes(firstResult, maxResult, AelAnticoagulante.Fields.DESCRICAO.toString(), true, new AelAnticoagulante(codigo, descricao, indSituacao, null, null));
	}
	
	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarAelAnticoagulanteCount(new AelAnticoagulante(codigo, descricao, indSituacao, null, null));
	}
	
	public void excluir()  {
		try {
			cadastrosApoioExamesFacade.removerAnticoagulante(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ANTICOAGULANTE", selecionado.getDescricao());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_ANTICOAGULANTE_CRUD;
	}

	public void editar(AelAnticoagulante aelAnticoagulante) {
		try {
			cadastrosApoioExamesFacade.ativarInativarAnticoagulante(aelAnticoagulante);

			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ANTICOAGULANTE", aelAnticoagulante.getDescricao());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	public boolean isActive(AelAnticoagulante anticoagulante) {
		return (anticoagulante.getIndSituacao() == DominioSituacao.A);
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

	public DynamicDataModel<AelAnticoagulante> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelAnticoagulante> dataModel) {
		this.dataModel = dataModel;
	}

	public AelAnticoagulante getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelAnticoagulante selecionado) {
		this.selecionado = selecionado;
	}
}