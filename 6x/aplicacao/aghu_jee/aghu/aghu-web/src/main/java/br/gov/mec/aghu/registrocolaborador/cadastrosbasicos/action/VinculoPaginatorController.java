package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class VinculoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<RapVinculos> dataModel;

	private static final long serialVersionUID = 7278472006144256029L;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	
	private RapVinculos vinculo = new RapVinculos();
	
	private Short codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer vinCodigo;
	private boolean exibirNovo;
	private Integer codigoVinculo;

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarVinculosCount(codigo, descricao,
				indSituacao);
	}

	@Override
	public List<RapVinculos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		return cadastrosBasicosFacade.pesquisarVinculos(codigo, descricao, indSituacao, firstResult, maxResult,
				orderProperty, asc);
	}

	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.dataModel.limparPesquisa();
		this.vinculo = new RapVinculos();
		this.codigo = null;
		this.descricao = null;
		this.indSituacao = null;
	}

	public String cancelarPesquisa() {
		this.dataModel.reiniciarPaginator();
		return "cancelado";
	}

	/**
	 * Método chamado na tela de pesquisa de cidade quando o usuário clicar no botão exluir da grid
	 * com a lista de Vínculos
	 * 
	 * @param Código da cidade a ser removida
	 */
	public void excluir() {
		try {
			
			cadastrosBasicosFacade.excluirVinculo(codigoVinculo.shortValue());
			
			//Exibr mensagem de exclusão com sucesso e fecha janela de confirmação
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_VINCULO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String inserirEditar(){
		return "cadastrarVinculo.xhtml";
	}
	
	/**
	 * Método chamado na tela de confirmação de exclusão de um registro de vínculo quando o usuário clicar
	 * no botão "Cancelar", cancelando a operação de para exclusão do registro de Vínculos
	 */
	public void cancelarModal() {		
		this.exibirNovo = true;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public RapVinculos getVinculo() {
		return vinculo;
	}

	public void setVinculo(RapVinculos vinculo) {
		this.vinculo = vinculo;
	}

	public boolean isExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public Integer getCodigoVinculo() {
		return codigoVinculo;
	}

	public void setCodigoVinculo(Integer codigoVinculo) {
		this.codigoVinculo = codigoVinculo;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	 


	public DynamicDataModel<RapVinculos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapVinculos> dataModel) {
	 this.dataModel = dataModel;
	}
}
