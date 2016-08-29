package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class TipoAfastamentoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<RapTipoAfastamento> dataModel;

	private static final Log LOG = LogFactory.getLog(TipoAfastamentoPaginatorController.class);

	private static final long serialVersionUID = 8636370828270120670L;
	
	private static final String TIPO_AFASTAMENTO_FORM = "cadastrarTipoAfastamento";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private String codigo;
	private DominioSituacao indSituacao;
	private String descricao;
	private RapTipoAfastamento tipoAfastamentoSelecionado;

	
	/**
	 * Atributo para definir codigo de exclusao
	 */
	private String codigoParaExclusao;
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarTipoAfastamentoCount(codigo,descricao, getIndSituacao());
	}

	@Override
	public List<RapTipoAfastamento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		 
		List<RapTipoAfastamento> rapTipoAfastamentos = 
			cadastrosBasicosFacade.pesquisarTipoAfastamento(codigo, descricao, indSituacao, firstResult, maxResult, 
					RapTipoAfastamento.Fields.DESCRICAO.toString(), true);
		
		if (rapTipoAfastamentos == null) {
			rapTipoAfastamentos = new ArrayList<RapTipoAfastamento>();
		}
		
		return rapTipoAfastamentos;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		codigo = null;
		descricao = null;
		indSituacao = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String editar(){
		return TIPO_AFASTAMENTO_FORM;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa.
	 */
	public void excluir() {
		this.dataModel.reiniciarPaginator();

		try {
			RapTipoAfastamento tipoAfastamento = this.cadastrosBasicosFacade.obterTipoAfastamento(tipoAfastamentoSelecionado.getCodigo());

			if (tipoAfastamento != null) {
				this.cadastrosBasicosFacade.excluirTipoAfastamento(tipoAfastamento);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_AFASTAMENTO", tipoAfastamento.getCodigo());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_TIPO_AFASTAMENTO");
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	// getters & setters

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigoParaExclusao(String codigoParaExclusao) {
		this.codigoParaExclusao = codigoParaExclusao;
	}

	public String getCodigoParaExclusao() {
		return codigoParaExclusao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setIndInterno(DominioSituacao indInterno) {
		this.setIndSituacao(indInterno);
	}

	public DominioSituacao getIndInterno() {
		return getIndSituacao();
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public DynamicDataModel<RapTipoAfastamento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapTipoAfastamento> dataModel) {
	 this.dataModel = dataModel;
	}

	public RapTipoAfastamento getTipoAfastamentoSelecionado() {
		return tipoAfastamentoSelecionado;
	}

	public void setTipoAfastamentoSelecionado(RapTipoAfastamento tipoAfastamentoSelecionado) {
		this.tipoAfastamentoSelecionado = tipoAfastamentoSelecionado;
	}
}
