package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.GrupoReportRotinaCciVO;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 */

public class PesquisaProgramacaoRotinasRelatoriosController extends ActionController {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3347360203777734262L;


	private static final String REDIRECIONA_MANTER_PROGRAMACAO_ROTINAS_RELATORIOS = "cadastroProgramacaoRotinasRelatorios";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private CadastroProgramacaoRotinasRelatoriosController cadastroProgramacaoRotinasRelatoriosController;

	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	private DominioPeriodicidade periodicidade;

	private GrupoReportRotinaCciVO grupoReportRotinaCciSelecionado;
	
	private List<GrupoReportRotinaCciVO> listaProgramacaoRotinasRelatorios;
	private Boolean pesquisaAtiva;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	public void inicio() {
		if (listaProgramacaoRotinasRelatorios == null) {
			listaProgramacaoRotinasRelatorios = new ArrayList<GrupoReportRotinaCciVO>();
			pesquisaAtiva = Boolean.FALSE;
		}
	}
	public void pesquisar() {
		pesquisaAtiva = Boolean.TRUE;
		listaProgramacaoRotinasRelatorios = controleInfeccaoFacade.pesquisarGrupoReportRotinaCci(codigo, descricao, situacao, periodicidade);
	}
	public void limparPesquisa() {
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.periodicidade = null;
		pesquisaAtiva = Boolean.FALSE;
		listaProgramacaoRotinasRelatorios = new ArrayList<GrupoReportRotinaCciVO>();
	}

	public String editar() {
		this.cadastroProgramacaoRotinasRelatoriosController.setGrupoReportRotinaCciVOSelecionado(this.grupoReportRotinaCciSelecionado);
		return REDIRECIONA_MANTER_PROGRAMACAO_ROTINAS_RELATORIOS;
	}
	
	public void excluir()  {
		try {
			controleInfeccaoFacade.removerGrupoReportRotinaCci(grupoReportRotinaCciSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_GRUPO_ROTINA",grupoReportRotinaCciSelecionado.getSeq() + "-" + grupoReportRotinaCciSelecionado.getDescricao());
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	public String novo(){
		return REDIRECIONA_MANTER_PROGRAMACAO_ROTINAS_RELATORIOS;
	}
	
	// ### GETs e SETs ###

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigoOrigem) {
		this.codigo = codigoOrigem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricaoOrigem) {
		this.descricao = descricaoOrigem;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacaoOrigem) {
		this.situacao = situacaoOrigem;
	}

	public DominioPeriodicidade getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(DominioPeriodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}

	public GrupoReportRotinaCciVO getGrupoReportRotinaCciSelecionado() {
		return grupoReportRotinaCciSelecionado;
	}

	public void setGrupoReportRotinaCciSelecionado(
			GrupoReportRotinaCciVO grupoReportRotinaCciSelecionado) {
		this.grupoReportRotinaCciSelecionado = grupoReportRotinaCciSelecionado;
	}

	public List<GrupoReportRotinaCciVO> getListaProgramacaoRotinasRelatorios() {
		return listaProgramacaoRotinasRelatorios;
	}

	public void setListaProgramacaoRotinasRelatorios(
			List<GrupoReportRotinaCciVO> listaProgramacaoRotinasRelatorios) {
		this.listaProgramacaoRotinasRelatorios = listaProgramacaoRotinasRelatorios;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}
}
