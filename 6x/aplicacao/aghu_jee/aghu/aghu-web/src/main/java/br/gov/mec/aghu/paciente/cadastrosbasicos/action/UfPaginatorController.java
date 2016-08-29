package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de UFs.
 * 
 * @author david.laks
 */


public class UfPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4163341416942316642L;

	private static final String REDIRECT_MANTER_UF = "ufCRUD";

	private static final String REDIRECT_CIDADE = "cidadeList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private Conversation conversation;
	
	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}
	
	@Inject @Paginator
	private DynamicDataModel<AipUfs> dataModel;

	/**
	 * Atributo referente ao campo de filtro de sigla da UF na tela de pesquisa.
	 */
	private String siglaPesquisaUF;

	/**
	 * Atributo referente ao campo de filtro de nome da UF na tela de pesquisa.
	 */
	private String nomePesquisaUF;

	/**
	 * Atributo referente ao campo de filtro de sigla do país da UF na tela de
	 * pesquisa.
	 */
	private AipPaises paisPesquisaUF;

	/**
	 * Atributo referente ao campo de filtro de cadastro de cidades da UF na
	 * tela de pesquisa.
	 */
	private DominioSimNao cadastraCidadesPesquisaUF;

	/**
	 * Sigla (pk) da UF, obtido via page parameter.
	 */
	private String aipSiglaUF;
	
	
	private AipUfs ufSelecionado;

	public List<AipPaises> pesquisarPaisesPorDescricao(String strPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarPaisesPorDescricao((String) strPesquisa);
	}
	
	public String prepararEdicao(){
		return REDIRECT_MANTER_UF;
	}

//	@Restrict("#{s:hasPermission('uf','pesquisar')}") 
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.siglaPesquisaUF = null;
		this.nomePesquisaUF = null;
		this.paisPesquisaUF = null;
		this.cadastraCidadesPesquisaUF = null;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de UFs.
	 */
	public void excluir() {
		this.dataModel.reiniciarPaginator();
		
		try{
			if (ufSelecionado != null) {
				this.cadastrosBasicosPacienteFacade.removerUF(this.ufSelecionado.getSigla());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_UF", ufSelecionado.getNome());
				
				
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_UF_INVALIDA");
			}
			this.aipSiglaUF = null;
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		String siglaPais = this.paisPesquisaUF == null ? null : this.paisPesquisaUF.getSigla();

		return cadastrosBasicosPacienteFacade.pesquisaUFsCount(this.siglaPesquisaUF, this.nomePesquisaUF, siglaPais, this.cadastraCidadesPesquisaUF);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AipUfs> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		String siglaPais = this.paisPesquisaUF == null ? null : this.paisPesquisaUF.getSigla();

		List<AipUfs> result = cadastrosBasicosPacienteFacade.pesquisaUFs(firstResult, maxResults,
				orderProperty, asc, this.siglaPesquisaUF, this.nomePesquisaUF,
				siglaPais, this.cadastraCidadesPesquisaUF);

		if (result == null) {
			result = new ArrayList<AipUfs>();
		}

		return result;
	}

	

	public void limparPais() {
		this.paisPesquisaUF = null;
	}

	public boolean isMostrarLinkExcluirPais() {
		return this.getPaisPesquisaUF() != null;
	}
	
	public String redirecionarInclusao(){
		return REDIRECT_MANTER_UF;
	}
	
	public String redirecionaCidade() {
		return REDIRECT_CIDADE;
	}
	
	public String getSiglaPesquisaUF() {
		return siglaPesquisaUF;
	}

	public void setSiglaPesquisaUF(String siglaPesquisaUF) {
		this.siglaPesquisaUF = siglaPesquisaUF;
	}

	public String getNomePesquisaUF() {
		return nomePesquisaUF;
	}

	public void setNomePesquisaUF(String nomePesquisaUF) {
		this.nomePesquisaUF = nomePesquisaUF;
	}

	public AipPaises getPaisPesquisaUF() {
		return paisPesquisaUF;
	}

	public void setPaisPesquisaUF(AipPaises paisPesquisaUF) {
		this.paisPesquisaUF = paisPesquisaUF;
	}

	public String getAipSiglaUF() {
		return aipSiglaUF;
	}

	public void setAipSiglaUF(String aipSiglaUF) {
		this.aipSiglaUF = aipSiglaUF;
	}

	public DominioSimNao getCadastraCidadesPesquisaUF() {
		return cadastraCidadesPesquisaUF;
	}

	public void setCadastraCidadesPesquisaUF(
			DominioSimNao cadastraCidadesPesquisaUF) {
		this.cadastraCidadesPesquisaUF = cadastraCidadesPesquisaUF;
	}

	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}

	public AipUfs getUfSelecionado() {
		return ufSelecionado;
	}

	public void setUfSelecionado(AipUfs ufSelecionado) {
		this.ufSelecionado = ufSelecionado;
	} 

	public DynamicDataModel<AipUfs> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipUfs> dataModel) {
		this.dataModel = dataModel;
	}

}
