package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para a tela Manter Motivo de Retorno para Carta de Recoleta.
 */
public class ManutencaoMotivoRetornoCartaRecoletaController extends ActionController {

	private static final long serialVersionUID = -8157970589423748402L;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;	
	
	// Filtros da pesquisa
	private Integer filtroSeq;
	private String filtroDescricaoRetorno;
	private DominioSituacao filtroIndSituacao;
	
	// Campos para inserção/edição
	private Integer seq;
	private String descricaoRetorno;
	private Boolean indCancelaExame;
	private Boolean indAvisaSolicitante;
	private Boolean indSituacao;
	
	private Boolean editando;
	private Boolean ativo;
	
	private AelRetornoCarta retornoCarta;
	private List<AelRetornoCarta> listaRetornoCarta;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		carregarValoresDefault();
		retornoCarta = new AelRetornoCarta();
	}
	
	private void carregarValoresDefault() {
		indSituacao = Boolean.TRUE;
	}
	
	public void pesquisar() {
		listaRetornoCarta = cadastrosApoioExamesFacade.pesquisarRetornoCarta(filtroSeq, filtroDescricaoRetorno, filtroIndSituacao);
		carregarValoresDefault();
		ativo = Boolean.TRUE;
	}
	
	public void gravar() {
		retornoCarta.setDescricao(descricaoRetorno);
		retornoCarta.setIndCancelaExame(indCancelaExame);
		retornoCarta.setIndAvisaSolicitante(indAvisaSolicitante);
		retornoCarta.setIndSituacao(obterSituacaoSelecionada(indSituacao));
		
		try {
			cadastrosApoioExamesFacade.persistirRetornoCarta(retornoCarta);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_MOTIVO_RETORNO_CARTA_RECOLETA", retornoCarta.getDescricao());
			limparEdicao();
			pesquisar();
		} catch (BaseException  e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		filtroSeq = null;
		filtroDescricaoRetorno = null;
		filtroIndSituacao = null;		
		listaRetornoCarta = null;
		ativo = Boolean.FALSE;
		limparEdicao();
	}
	
	public void limparEdicao() {
		seq = null;
		descricaoRetorno = null;
		indCancelaExame = null;
		indAvisaSolicitante = null;
		indSituacao = Boolean.TRUE;
		retornoCarta = new AelRetornoCarta();
		editando = Boolean.FALSE;
	}
	
	public void ativarInativar(AelRetornoCarta retCarta) {
		Boolean novaIndSituacao = !retCarta.getIndSituacao().isAtivo();
		retCarta.setIndSituacao(obterSituacaoSelecionada(novaIndSituacao));
		
		try {
			cadastrosApoioExamesFacade.persistirRetornoCarta(retCarta);
			
			String keyMensagem = null;
			if (retCarta.getIndSituacao().isAtivo()) {
				keyMensagem = "MENSAGEM_SUCESSO_ATIVAR_MOTIVO_RETORNO_CARTA_RECOLETA";
			} else {
				keyMensagem = "MENSAGEM_SUCESSO_DESATIVAR_MOTIVO_RETORNO_CARTA_RECOLETA";
			}
			
			this.apresentarMsgNegocio(Severity.INFO, keyMensagem, retCarta.getDescricao());
			pesquisar();
		} catch (BaseException  e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(AelRetornoCarta retornoCarta) {
		seq = retornoCarta.getSeq();
		descricaoRetorno = retornoCarta.getDescricao();
		indCancelaExame = retornoCarta.getIndCancelaExame();
		indAvisaSolicitante = retornoCarta.getIndAvisaSolicitante();
		indSituacao = retornoCarta.getIndSituacao().isAtivo();
		this.retornoCarta = retornoCarta;
		editando = Boolean.TRUE;
	}
	
	public void cancelarEdicao() {
		limparEdicao();
		carregarValoresDefault();
	}
	
	private DominioSituacao obterSituacaoSelecionada(Boolean booleanIndSituacao) {
		if (booleanIndSituacao == null) {
			return null;
		} else {
			return DominioSituacao.getInstance(booleanIndSituacao);	
		}
	}

	public Integer getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Integer filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricaoRetorno() {
		return filtroDescricaoRetorno;
	}

	public void setFiltroDescricaoRetorno(String filtroDescricaoRetorno) {
		this.filtroDescricaoRetorno = filtroDescricaoRetorno;
	}

	public DominioSituacao getFiltroIndSituacao() {
		return filtroIndSituacao;
	}

	public void setFiltroIndSituacao(DominioSituacao filtroIndSituacao) {
		this.filtroIndSituacao = filtroIndSituacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricaoRetorno() {
		return descricaoRetorno;
	}

	public void setDescricaoRetorno(String descricaoRetorno) {
		this.descricaoRetorno = descricaoRetorno;
	}

	public Boolean getIndCancelaExame() {
		return indCancelaExame;
	}

	public void setIndCancelaExame(Boolean indCancelaExame) {
		this.indCancelaExame = indCancelaExame;
	}

	public Boolean getIndAvisaSolicitante() {
		return indAvisaSolicitante;
	}

	public void setIndAvisaSolicitante(Boolean indAvisaSolicitante) {
		this.indAvisaSolicitante = indAvisaSolicitante;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AelRetornoCarta getRetornoCarta() {
		return retornoCarta;
	}

	public void setRetornoCarta(AelRetornoCarta retornoCarta) {
		this.retornoCarta = retornoCarta;
	}

	public List<AelRetornoCarta> getListaRetornoCarta() {
		return listaRetornoCarta;
	}

	public void setListaRetornoCarta(List<AelRetornoCarta> listaRetornoCarta) {
		this.listaRetornoCarta = listaRetornoCarta;
	}
}
