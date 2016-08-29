package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class InstituicaoQualificadoraPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -7763433464146755773L;

	private static final String CADASTRAR_INSTITUICAO_QUALIFICADORA = "cadastrarInstituicaoQualificadora";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private Integer codigo;
	private DominioSimNao indInterno;
	private String descricao;
	

	@Inject @Paginator
	private DynamicDataModel<RapInstituicaoQualificadora> dataModel;
	
	private RapInstituicaoQualificadora selecionado;
	
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarInstituicaoQualificadoraCount(codigo,getDescricao(), indInterno, null);
	}

	@Override
	public List<RapInstituicaoQualificadora> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		return cadastrosBasicosFacade.pesquisarInstituicaoQualificadora(codigo,getDescricao(), indInterno, null, firstResult, maxResult, RapInstituicaoQualificadora.Fields.DESCRICAO.toString(), true);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String editar(){
		return CADASTRAR_INSTITUICAO_QUALIFICADORA;
	}

	public String inserir(){
		return CADASTRAR_INSTITUICAO_QUALIFICADORA;
	}

	public void limpar() {
		dataModel.limparPesquisa();
		codigo = null;
		descricao = null;
		indInterno = null;
	}

	public void excluir() {
		try {
			this.cadastrosBasicosFacade.excluirInstituicaoQualificadora(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_INSTITUICAO_QUALIFICADORA", selecionado.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// getters & setters
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setIndInterno(DominioSimNao indInterno) {
		this.indInterno = indInterno;
	}

	public DominioSimNao getIndInterno() {
		return indInterno;
	}

	public DynamicDataModel<RapInstituicaoQualificadora> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<RapInstituicaoQualificadora> dataModel) {
		this.dataModel = dataModel;
	}

	public RapInstituicaoQualificadora getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapInstituicaoQualificadora selecionado) {
		this.selecionado = selecionado;
	}
}
