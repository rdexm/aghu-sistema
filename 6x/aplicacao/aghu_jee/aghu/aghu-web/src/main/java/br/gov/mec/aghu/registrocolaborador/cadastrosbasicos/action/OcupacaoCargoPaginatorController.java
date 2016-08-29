package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class OcupacaoCargoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -177257847343004509L;

	private static final String CADASTRAR_OCUPACAO_CARGO = "cadastrarOcupacaoCargo";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	// formulário de pesquisa
	private RapCargos cargo;
	private Integer codigo;
	private String descricao;
	private DominioSituacao situacao;

	@Inject @Paginator
	private DynamicDataModel<RapOcupacaoCargo> dataModel;
	
	private RapOcupacaoCargo selecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		String carCodigo = cargo == null ? null : cargo.getCodigo();
		return cadastrosBasicosFacade.pesquisarOcupacaoCargoCount(codigo, carCodigo, descricao, situacao);
	}

	@Override
	public List<RapOcupacaoCargo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		String carCodigo = cargo == null ? null : cargo.getCodigo();
		return cadastrosBasicosFacade.pesquisarOcupacaoCargo(codigo, carCodigo, descricao, situacao, firstResult, maxResult,  RapOcupacaoCargo.Fields.DESCRICAO.toString(), true);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String editar(){
		return CADASTRAR_OCUPACAO_CARGO;
	}

	public String inserir(){
		return CADASTRAR_OCUPACAO_CARGO;
	}

	public void excluir() {
		try {
			cadastrosBasicosFacade.remover(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_OCUPACOES_CARGO", selecionado.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.cargo = null;
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
	}
	
	public List<RapCargos> pesquisarCargosPorDescricao(String descricao) {
		return cadastrosBasicosFacade.pesquisarCargosPorDescricao((String) descricao);
	}

	public RapCargos getCargo() {
		return cargo;
	}

	public void setCargo(RapCargos cargo) {
		this.cargo = cargo;
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<RapOcupacaoCargo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapOcupacaoCargo> dataModel) {
		this.dataModel = dataModel;
	}

	public RapOcupacaoCargo getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapOcupacaoCargo selecionado) {
		this.selecionado = selecionado;
	}
}
