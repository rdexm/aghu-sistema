package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRegiaoAnatomicaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5023901496085142883L;

	private static final String MANTER_REGIAO_ANATOMICA_CRUD = "manterRegiaoAnatomicaCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AelRegiaoAnatomica> dataModel;
	
	private AelRegiaoAnatomica selecionado;
	
	//Campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	
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
	
	@Override
	public List<AelRegiaoAnatomica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarRegioesAnatomicas(firstResult, maxResult, AelRegiaoAnatomica.Fields.DESCRICAO.toString(), true,
														new AelRegiaoAnatomica(codigo, descricao, indSituacao, null));
	}
	
	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarRegioesAnatomicasCount(new AelRegiaoAnatomica(codigo, descricao, indSituacao, null));
	}
	
	public void excluir()  {
		try {
			
			cadastrosApoioExamesFacade.removerRegiaoAnatomica(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_REGIAO_ANATOMICA", selecionado.getDescricao());
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_REGIAO_ANATOMICA_CRUD;
	} 
	
	public void editar(AelRegiaoAnatomica regiao) {

		try {
			regiao.setIndSituacao(regiao.getIndSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A);
			
			//Submete o procedimento para ser persistido
			cadastrosApoioExamesFacade.persistirRegiaoAnatomica(regiao);

			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_REGIAO_ANATOMICA", regiao.getDescricao());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean estaAtiva(AelRegiaoAnatomica regiao) {
		return regiao.getIndSituacao() == DominioSituacao.A;
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

	public DynamicDataModel<AelRegiaoAnatomica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelRegiaoAnatomica> dataModel) {
		this.dataModel = dataModel;
	}

	public AelRegiaoAnatomica getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelRegiaoAnatomica selecionado) {
		this.selecionado = selecionado;
	}
}