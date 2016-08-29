package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.LocaisOrigemInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author israel.haas
 */

public class CadastroOrigemInfeccaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3765828490362728206L;
	
	private static final Log LOG = LogFactory.getLog(CadastroOrigemInfeccaoController.class);
	
	private static final String REDIRECIONA_LISTAR_ORIGEM_INFECCAO = "pesquisaOrigemInfeccao";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private PesquisaOrigemInfeccaoPaginatorController pesquisaOrigemInfeccaoPaginatorController;
	
	private OrigemInfeccoesVO origemSelecionada;
	
	private Boolean ativo;
	
	private AghUnidadesFuncionais unidadeFuncionalPadrao;
	private AghUnidadesFuncionais unidadeFuncionalOrigem;
	
	private List<LocaisOrigemInfeccaoVO> listaLocaisOrigem;
	private LocaisOrigemInfeccaoVO localOrigem;
	private LocaisOrigemInfeccaoVO localOrigemExclusao;
	
	private String descricaoExclusao;
	
	private boolean modoEdicao;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
		localOrigem = new LocaisOrigemInfeccaoVO();
	}

	public void inicio() {
	 

	 

		this.modoEdicao = false;
		this.setAtivo(this.origemSelecionada.getSituacao().isAtivo());
		if (this.origemSelecionada.getUnfSeq() != null) {
			this.setUnidadeFuncionalPadrao(this.aghuFacade.obterUnidadeFuncional(this.origemSelecionada.getUnfSeq()));
		}
		this.setListaLocaisOrigem(this.controleInfeccaoFacade.listarLocaisOrigemInfeccoes(this.origemSelecionada.getCodigoOrigem()));
		this.setLocalOrigem(new LocaisOrigemInfeccaoVO());
		this.localOrigem.setCodigoOrigem(this.origemSelecionada.getCodigoOrigem());
		this.localOrigem.setSituacao(Boolean.TRUE);
	
	}
	

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPadrao(String param) {
		String strPesquisa = (String) param;
		return returnSGWithCount(this.controleInfeccaoFacade.pesquisarUnidadesAtivas(strPesquisa, false, this.origemSelecionada.getCodigoOrigem()), this.aghuFacade.pesquisarUnidadesAtivasCount(strPesquisa, false, this.origemSelecionada.getCodigoOrigem()));
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalOrigem(String param) {
		String strPesquisa = (String) param;
		return returnSGWithCount(this.controleInfeccaoFacade.pesquisarUnidadesAtivas(strPesquisa, true, this.origemSelecionada.getCodigoOrigem()), this.aghuFacade.pesquisarUnidadesAtivasCount(strPesquisa, true, this.origemSelecionada.getCodigoOrigem()));
	}
	
	public void confirmar() {
		this.origemSelecionada.setSituacao(this.getAtivo() ? DominioSituacao.A : DominioSituacao.I);
		if (this.unidadeFuncionalPadrao != null) {
			this.origemSelecionada.setUnfSeq(this.unidadeFuncionalPadrao.getSeq());
		}
		this.controleInfeccaoFacade.atualizarOrigemInfeccao(this.origemSelecionada);
			
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ORIGEM_INFECCAO",
				this.origemSelecionada.getDescricao());
	}
	
	public void adicionarLocalOrigem() {
		this.localOrigem.setUnfSeq(this.unidadeFuncionalOrigem.getSeq());
		this.localOrigem.setDescricaoLocal(this.unidadeFuncionalOrigem.getDescricao());
		this.localOrigem.setIndSituacao(localOrigem.getSituacao() ? DominioSituacao.A : DominioSituacao.I);
			
		this.controleInfeccaoFacade.inserirLocalOrigemInfeccao(this.localOrigem);
			
		this.setListaLocaisOrigem(this.controleInfeccaoFacade.listarLocaisOrigemInfeccoes(this.origemSelecionada.getCodigoOrigem()));
			
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_LOCAL_ORIGEM",
				this.localOrigem.getDescricaoLocal());
			
		this.cancelarEdicao();
	}
	
	public void alterarLocalOrigem() {
		this.localOrigem.setIndSituacao(this.localOrigem.getSituacao() ? DominioSituacao.A : DominioSituacao.I);
		this.controleInfeccaoFacade.alterarLocalOrigemInfeccao(this.localOrigem);
		
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_LOCAL_ORIGEM",
				this.localOrigem.getDescricaoLocal());
		
		this.cancelarEdicao();
	}
	
	public void excluir() {
		try {
			String msgRetorno = this.controleInfeccaoFacade.excluirLocalOrigemInfeccao(this.localOrigemExclusao);
			
			if (msgRetorno != null) {
				apresentarMsgNegocio(Severity.WARN, msgRetorno);
				
			} else {
				this.setListaLocaisOrigem(this.controleInfeccaoFacade.listarLocaisOrigemInfeccoes(this.origemSelecionada.getCodigoOrigem()));
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_LOCAL_ORIGEM",
						this.getDescricaoExclusao());
			}
			this.cancelarEdicao();
			
		} catch (ApplicationBusinessException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(LocaisOrigemInfeccaoVO item) {
		this.modoEdicao = true;
		this.setLocalOrigem(item);
		this.setUnidadeFuncionalOrigem(this.aghuFacade.obterUnidadeFuncional(this.localOrigem.getUnfSeq()));
	}
	
	public String cancelar() {
		limparTudo();
		LOG.info("Cancelado");
		this.pesquisaOrigemInfeccaoPaginatorController.reiniciarPaginator();
		return REDIRECIONA_LISTAR_ORIGEM_INFECCAO;
	}
	
	public void cancelarEdicao() {
		this.modoEdicao = false;
		this.setUnidadeFuncionalOrigem(null);
		this.setLocalOrigem(new LocaisOrigemInfeccaoVO());
		this.setDescricaoExclusao(null);
		this.localOrigem.setSituacao(Boolean.TRUE);
		this.localOrigem.setCodigoOrigem(this.origemSelecionada.getCodigoOrigem());
		this.setListaLocaisOrigem(this.controleInfeccaoFacade.listarLocaisOrigemInfeccoes(this.origemSelecionada.getCodigoOrigem()));
	}
	
	private void limparTudo() {
		this.setOrigemSelecionada(null);
		this.setUnidadeFuncionalPadrao(null);
		this.setUnidadeFuncionalOrigem(null);
		this.setLocalOrigem(new LocaisOrigemInfeccaoVO());
		this.setDescricaoExclusao(null);
	}
	
	// ### GETs e SETs ###

	public OrigemInfeccoesVO getOrigemSelecionada() {
		return origemSelecionada;
	}

	public void setOrigemSelecionada(OrigemInfeccoesVO origemSelecionada) {
		this.origemSelecionada = origemSelecionada;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPadrao() {
		return unidadeFuncionalPadrao;
	}

	public void setUnidadeFuncionalPadrao(
			AghUnidadesFuncionais unidadeFuncionalPadrao) {
		this.unidadeFuncionalPadrao = unidadeFuncionalPadrao;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalOrigem() {
		return unidadeFuncionalOrigem;
	}

	public void setUnidadeFuncionalOrigem(
			AghUnidadesFuncionais unidadeFuncionalOrigem) {
		this.unidadeFuncionalOrigem = unidadeFuncionalOrigem;
	}

	public List<LocaisOrigemInfeccaoVO> getListaLocaisOrigem() {
		return listaLocaisOrigem;
	}

	public void setListaLocaisOrigem(List<LocaisOrigemInfeccaoVO> listaLocaisOrigem) {
		this.listaLocaisOrigem = listaLocaisOrigem;
	}

	public LocaisOrigemInfeccaoVO getLocalOrigem() {
		return localOrigem;
	}

	public void setLocalOrigem(LocaisOrigemInfeccaoVO localOrigem) {
		this.localOrigem = localOrigem;
	}

	public LocaisOrigemInfeccaoVO getLocalOrigemExclusao() {
		return localOrigemExclusao;
	}

	public void setLocalOrigemExclusao(LocaisOrigemInfeccaoVO localOrigemExclusao) {
		this.localOrigemExclusao = localOrigemExclusao;
	}

	public String getDescricaoExclusao() {
		return descricaoExclusao;
	}

	public void setDescricaoExclusao(String descricaoExclusao) {
		this.descricaoExclusao = descricaoExclusao;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
}
