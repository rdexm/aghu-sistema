package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CidadePaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -8318079049985688338L;
	private static final String REDIRECT_MANTER_CIDADE = "cidadeCRUD";

	private static final String REDIRECT_UF = "ufList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AipCidades> dataModel;
	
	private AipCidades aipCidades = new AipCidades();
	private AipCidades cidadeSelecionado;
	
	private Integer aipCidadeCodigo;
	private Boolean operacaoConcluida = false;
	
	private Boolean voltarUF = false;
	
	/**
	 * Atributo referente ao campo de filtro de cadastro de logradouros da Cidade na
	 * tela de pesquisa.
	 */
	private DominioSimNao cadastraLogradourosPesquisaCidade;
	
	
	@PostConstruct
	public void init(){
		this.begin(this.conversation);	
		this.aipCidades = new AipCidades();
	}
	
	/**
	 * Método para pesquisar cidades
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	/**
	 * Método para limpar campos da tela, grid e botão "Novo"
	 */
	public void limpar() {
		dataModel.limparPesquisa();
		this.aipCidades = new AipCidades();
	}
	
	public String prepararEdicao(){
		return REDIRECT_MANTER_CIDADE;
	}
	
	/**
	 * Método chamado na tela de pesquisa de cidade quando o usuário clicar no botão exluir da grid
	 * com a lista de Cidades
	 * 
	 * @param Código da cidade a ser removida
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void excluir() {
		try {
			//this.limpar();--nao eh invocado o limpar pois ele esconde a grid e o botao 'novo'.
			dataModel.reiniciarPaginator();
			
			//Obtem cidade e remove a mesma
			this.cadastrosBasicosPacienteFacade.excluirCidade(cidadeSelecionado.getCodigo());
			
			//Exibr mensagem de exclusão com sucesso e fecha janela de confirmação
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_CIDADE", cidadeSelecionado.getNome());
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	/**
	 * Método chamado na tela de confirmação de exclusão de um registro de cidade quando o usuário clicar
	 * no botão "Cancelar", cancelando a operação de para exclusão do registro de Cidade
	 */
	public void cancelarModal() {
		this.operacaoConcluida = true;
	}

	
	@Override
	public Long recuperarCount() {
		if (cadastraLogradourosPesquisaCidade != null){
			aipCidades.setIndLogradouro(cadastraLogradourosPesquisaCidade.isSim());
		}
		else{
			aipCidades.setIndLogradouro(null);
		}
		return cadastrosBasicosPacienteFacade.obterCidadeCount(aipCidades.getCodigo(), aipCidades.getCodIbge(), 
				   aipCidades.getNome(), aipCidades.getIndSituacao(), aipCidades.getCep(), 
				   aipCidades.getCepFinal(), aipCidades.getAipUf());
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public List<AipCidades> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		if (cadastraLogradourosPesquisaCidade != null){
			aipCidades.setIndLogradouro(cadastraLogradourosPesquisaCidade.isSim());
		}
		else{
			aipCidades.setIndLogradouro(null);
		}
		return cadastrosBasicosPacienteFacade.pesquisarCidades(firstResult, maxResult, aipCidades.getCodigo(), aipCidades.getCodIbge(), 
				   aipCidades.getNome(), aipCidades.getIndSituacao(), aipCidades.getCep(), 
				   aipCidades.getCepFinal(), aipCidades.getAipUf());
	}
	
	public String redirecionarListarUF() {
		return REDIRECT_UF;
	}
	
	public String redirecionaIncluirCidade() {
		return REDIRECT_MANTER_CIDADE;
	}

	public String getNomeUFSelecionada()	{
		if (this.aipCidades.getAipUf() == null || StringUtils.isBlank(this.aipCidades.getAipUf().getNome())){
			return "";
		}
		return this.aipCidades.getAipUf().getNome();
	}
	
	/**
	 * Método que retorna uma coleção de UFs p/ preencher a suggestion box, de acordo com filtro informado.
	 */	
	public List<AipUfs> pesquisarUFs(final String parametro) {
		return this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(parametro);
	}

	
	public Integer getAipCidadeCodigo() {
		return aipCidadeCodigo;
	}

	public void setAipCidadeCodigo(final Integer aipCidadeCodigo) {
		this.aipCidadeCodigo = aipCidadeCodigo;
	}

	public Boolean getOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(final Boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public DominioSimNao getCadastraLogradourosPesquisaCidade() {
		return cadastraLogradourosPesquisaCidade;
	}

	public void setCadastraLogradourosPesquisaCidade(
			final DominioSimNao cadastraLogradourosPesquisaCidade) {
		this.cadastraLogradourosPesquisaCidade = cadastraLogradourosPesquisaCidade;
	} 

	public DynamicDataModel<AipCidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipCidades> dataModel) {
		this.dataModel = dataModel;
	}

	public AipCidades getCidadeSelecionado() {
		return cidadeSelecionado;
	}

	public void setCidadeSelecionado(AipCidades cidadeSelecionado) {
		this.cidadeSelecionado = cidadeSelecionado;
	}

	public Boolean getVoltarUF() {
		return voltarUF;
	}

	public void setVoltarUF(Boolean voltarUF) {
		this.voltarUF = voltarUF;
	}

	public AipCidades getAipCidades() {
		return aipCidades;
	}

	public void setAipCidades(AipCidades aipCidades) {
		this.aipCidades = aipCidades;
	}
		
}
