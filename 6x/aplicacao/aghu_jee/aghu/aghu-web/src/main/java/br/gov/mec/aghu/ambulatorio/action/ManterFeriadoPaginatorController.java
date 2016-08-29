package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioMesFeriado;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ManterFeriadoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 489784539469310649L;
	
	@EJB
	private IAghuFacade aghuFacade;

	private final String PAGE_FERIADO_CRUD = "manterFeriadoCRUD";
	
	//filtros
	private Date data;
	private DominioTurno turno;
	private DominioMesFeriado mes;
	private Integer ano;
	
	//controle de exibicao do botao novo e parametro de exclusao
	private boolean exibirBotaoNovo;
	private Long seqExclusao;
	
	private AghFeriados feriadoSelecionado;
	
	@Inject @Paginator
	private DynamicDataModel<AghFeriados> dataModel;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Método responsavel por realizar a pesquisa.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.setExibirBotaoNovo(Boolean.TRUE);
	}
	
	/**
	 * Limpa os campos de pesquisa e oculta o botao novo.
	 */
	public void limparPesquisa() {
		this.data = null;
		this.turno = null;
		this.mes = null;
		this.ano = null;
		
		this.dataModel.limparPesquisa();
		this.setExibirBotaoNovo(false);
	}
	
	public String editarFeriado() {
		return PAGE_FERIADO_CRUD;
	}
	
	public void excluir() {
		try {
			this.aghuFacade.removerFeriado((this.feriadoSelecionado.getData()));
			this.dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "MSG_FERIADO_EXCLUIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public List<AghFeriados> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.aghuFacade.pesquisarFeriadoPaginado(this.data, this.mes, this.ano, this.turno,
				firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.aghuFacade.countFeriado(this.data, this.mes, this.ano, this.turno);
	}

	
	public String iniciarInclusao() {
		return PAGE_FERIADO_CRUD;
	}
	
	public Boolean verificaDataMenorIgual(Date data) {
		if(DateUtil.validaDataMenorIgual(data, new Date())) {
			return true;
		}
		return false;
	}
		
	public String obterDiaSemana(Date data) {
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(data);
		return diaSemana.getDescricao();
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}


	public Long getSeqExclusao() {
		return seqExclusao;
	}


	public void setSeqExclusao(Long seqExclusao) {
		this.seqExclusao = seqExclusao;
	}


	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}


	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}


	public DominioMesFeriado getMes() {
		return mes;
	}


	public void setMes(DominioMesFeriado mes) {
		this.mes = mes;
	}


	public Integer getAno() {
		return ano;
	}


	public void setAno(Integer ano) {
		this.ano = ano;
	}


	public DynamicDataModel<AghFeriados> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<AghFeriados> dataModel) {
		this.dataModel = dataModel;
	}


	public AghFeriados getFeriadoSelecionado() {
		return feriadoSelecionado;
	}


	public void setFeriadoSelecionado(AghFeriados feriadoSelecionado) {
		this.feriadoSelecionado = feriadoSelecionado;
	}
}
