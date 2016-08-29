package br.gov.mec.aghu.exames.agendamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoExamePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2591480721486704500L;

	private static final String GRUPO_EXAME_CRUD = "grupoExameCRUD";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	// Campos de filtro para pesquisa
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private DominioSimNao indAgendaExameMesmoHorario;
	private DominioSimNao indCalculaTempo;
	private AghUnidadesFuncionais unidadeExecutora;
	

	@Inject @Paginator
	private DynamicDataModel<AelGrupoExames> dataModel;
	
	private AelGrupoExames selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	private AelGrupoExames getElementoFiltroPesquisa(){
		
		final AelGrupoExames elementoFiltroPesquisa = new AelGrupoExames();
		
		elementoFiltroPesquisa.setSeq(this.codigo);
		this.descricao = StringUtils.trim(this.descricao);
		elementoFiltroPesquisa.setDescricao(this.descricao);
		elementoFiltroPesquisa.setSituacao(this.indSituacao);
		if(this.indAgendaExameMesmoHorario != null) {
			elementoFiltroPesquisa.setAgendaExMesmoHor(this.indAgendaExameMesmoHorario.isSim());
		}
		if(this.indCalculaTempo != null) {
			elementoFiltroPesquisa.setCalculaTempo(this.indCalculaTempo.isSim());
		}
		elementoFiltroPesquisa.setUnidadeFuncional(this.unidadeExecutora);
		
		return elementoFiltroPesquisa;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.agendamentoExamesFacade.pesquisarGrupoExameCount(this.getElementoFiltroPesquisa());
	}
	
	@Override
	public List<AelGrupoExames> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.agendamentoExamesFacade.pesquisarGrupoExame(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa());
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigo = null;
		this.descricao = null;
		this.indSituacao = null;
		this.indAgendaExameMesmoHorario = null;
		this.indCalculaTempo = null;
		this.unidadeExecutora = null;
	}
	
	public void excluir() {
		try {
			agendamentoExamesFacade.excluirGrupoExame(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO,"GRUPO_EXAME_MENSAGEM_SUCESSO_EXCLUIR_SUCESSO");
			dataModel.reiniciarPaginator();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	
	public String inserir(){
		return GRUPO_EXAME_CRUD;
	}
	
	public String editar(){
		return GRUPO_EXAME_CRUD;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
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

	public DominioSimNao getIndAgendaExameMesmoHorario() {
		return indAgendaExameMesmoHorario;
	}

	public void setIndAgendaExameMesmoHorario(
			DominioSimNao indAgendaExameMesmoHorario) {
		this.indAgendaExameMesmoHorario = indAgendaExameMesmoHorario;
	}

	public DominioSimNao getIndCalculaTempo() {
		return indCalculaTempo;
	}

	public void setIndCalculaTempo(DominioSimNao indCalculaTempo) {
		this.indCalculaTempo = indCalculaTempo;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

	public DynamicDataModel<AelGrupoExames> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoExames> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoExames getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoExames selecionado) {
		this.selecionado = selecionado;
	}
}