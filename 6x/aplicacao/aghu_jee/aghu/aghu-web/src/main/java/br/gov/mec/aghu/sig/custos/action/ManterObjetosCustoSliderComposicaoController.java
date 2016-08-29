package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;


public class ManterObjetosCustoSliderComposicaoController extends ActionController {

	private static final long serialVersionUID = 7802493109305306946L;

	private static final Log LOG = LogFactory.getLog(ManterObjetosCustoSliderComposicaoController.class);
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;

	@Inject
	private ManterObjetosCustoSliderPHIController manterObjetosCustoSliderPHIController;

	@Inject
	private ManterObjetosCustoSliderDirecionadoresController manterObjetosCustoSliderDirecionadoresController;

	private boolean possuiAlteracaoComposicao;
	private Integer indexOfComposicao;
	private boolean edicaoComposicao;
	private SigObjetoCustoComposicoes objetoCustoComposicao;
	private List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes;
	private List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoesExclusao;
	private String itemSelecionadoComposicao = "0";
	private Integer seqAtividadeExclusao;
	private Integer seqServicoExclusao;
	private boolean sugestionSelecionadaAtividade;
	private List<SigDirecionadores> listaDirecionadores;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	protected void iniciaSliderComposicoes(Integer seqObjetoCustoVersao) {
		this.setObjetoCustoComposicao(new SigObjetoCustoComposicoes());
		this.getObjetoCustoComposicao().setIndSituacao(DominioSituacao.A);
		this.setListaObjetoCustoComposicoes(new ArrayList<SigObjetoCustoComposicoes>());
		this.setListaObjetoCustoComposicoesExclusao(new ArrayList<SigObjetoCustoComposicoes>());
		this.setEdicaoComposicao(Boolean.FALSE);
		this.setPossuiAlteracaoComposicao(false);
		this.setItemSelecionadoComposicao("0");
		if (seqObjetoCustoVersao != null) {
			List<SigObjetoCustoComposicoes> listResult = custosSigFacade.pesquisarComposicoesPorObjetoCustoVersao(seqObjetoCustoVersao);
			if (listResult != null && !listResult.isEmpty()) {
				this.setListaObjetoCustoComposicoes(listResult);
			}
		}
	}

	public void adicionarComposicao() {
		try {
			if (this.getListaObjetoCustoComposicoes() != null && !this.getListaObjetoCustoComposicoes().isEmpty()) {
				if (this.manterObjetosCustoController.isObjetoCustoAssistencial()) {
					this.custosSigFacade.validarInclusaoComposicaoObjetoCustoAssistencial(this.getObjetoCustoComposicao(),
							this.getListaObjetoCustoComposicoes());
				} else {
					this.custosSigFacade.validarInclusaoComposicaoObjetoCustoApoio(this.getObjetoCustoComposicao(), this.getListaObjetoCustoComposicoes());
				}
			}
			this.getObjetoCustoComposicao().setCriadoEm(new Date());
			this.getObjetoCustoComposicao().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.getListaObjetoCustoComposicoes().add(this.getObjetoCustoComposicao());
			this.setObjetoCustoComposicao(new SigObjetoCustoComposicoes());
			this.getObjetoCustoComposicao().setIndSituacao(DominioSituacao.A);
			this.setPossuiAlteracaoComposicao(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicaoComposicao() {
		this.setEdicaoComposicao(Boolean.FALSE);
		this.getListaObjetoCustoComposicoes().get(indexOfComposicao).setEmEdicao(Boolean.FALSE);
		this.setObjetoCustoComposicao(new SigObjetoCustoComposicoes());
		this.getObjetoCustoComposicao().setIndSituacao(DominioSituacao.A);
	}

	public void editarComposicao(SigObjetoCustoComposicoes objetoEditado) {
		this.setIndexOfComposicao(this.getListaObjetoCustoComposicoes().indexOf(objetoEditado));
		try {
			objetoEditado.setEmEdicao(Boolean.TRUE);
			this.setObjetoCustoComposicao((SigObjetoCustoComposicoes) objetoEditado.clone());
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe SigObjetoCustoComposicoes " + "não implementa a interface Cloneable.", e);
		}
		this.getObjetoCustoComposicao().setEmEdicao(Boolean.TRUE);
		this.setEdicaoComposicao(Boolean.TRUE);
		this.setPossuiAlteracaoComposicao(true);
		if (this.getObjetoCustoComposicao().getSigAtividades() != null) {
			this.setItemSelecionadoComposicao("0");
		} else {
			this.setItemSelecionadoComposicao("1");
		}
	}

	public List<SigAtividades> pesquisarAtividadesComposicaoAssitencial(String objPesquisa) {
		return this.custosSigFacade.listarAtividadesRestringindoCentroCusto(this.manterObjetosCustoController.getFccCentroCustos(), objPesquisa);
	}

	public void gravarComposicao() {
		this.setEdicaoComposicao(Boolean.FALSE);
		try {
			this.getObjetoCustoComposicao().setRapServidores(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			this.getObjetoCustoComposicao().setRapServidores(null);
		}
		this.getObjetoCustoComposicao().setEmEdicao(Boolean.FALSE);
		this.getListaObjetoCustoComposicoes().set(this.getIndexOfComposicao(), this.getObjetoCustoComposicao());
		this.setObjetoCustoComposicao(new SigObjetoCustoComposicoes());
		this.getObjetoCustoComposicao().setIndSituacao(DominioSituacao.A);
	}

	public void gravarComposicaoBanco() {
		for (SigObjetoCustoComposicoes composicao : this.getListaObjetoCustoComposicoesExclusao()) {
			if (composicao.getSeq() != null) {
				this.custosSigFacade.excluirComposicoesObjetoCusto(composicao);
			}
		}
		for (SigObjetoCustoComposicoes composicao : this.getListaObjetoCustoComposicoes()) {
			composicao.setSigObjetoCustoVersoes(this.manterObjetosCustoController.getObjetoCustoVersao());
			this.custosSigFacade.persistComposicoesObjetoCusto(composicao);
		}
		
		this.setPossuiAlteracaoComposicao(true);
	}

	public void posSelectionProdutosComposicao() {
		this.getObjetoCustoComposicao().setSigAtividades(null);
	}

	public void posSelectionAtividadesComposicao() {
		this.getObjetoCustoComposicao().setSigObjetoCustoVersoesCompoe(null);
	}

	public List<SigDirecionadores> getListaDirecionadores() {
		this.listaDirecionadores = new ArrayList<SigDirecionadores>();
		this.listaDirecionadores = this.custosSigCadastrosBasicosFacade.pesquisarDirecionadoresTipoATAB(Boolean.TRUE);
		return this.listaDirecionadores;
	}

	public boolean isNroExecucoesObrigatorio() {
		if (this.getObjetoCustoComposicao() != null && this.getObjetoCustoComposicao().getSigDirecionadores() != null
				&& this.getObjetoCustoComposicao().getSigDirecionadores().getIndNroExecucoes()
				&& this.getObjetoCustoComposicao().getNroExecucoes() == null) {
			return true;
		} else {
			return false;
		}
	}

	public List<SigAtividades> pesquisarAtividadesComposicaoApoio(String objPesquisa) {
		List<FccCentroCustos> listaCentroCustos = new ArrayList<FccCentroCustos>();
		for (SigObjetoCustoCcts sigObjetoCustoCcts : this.manterObjetosCustoController.getListaObjetoCustoCcts()) {
			listaCentroCustos.add(sigObjetoCustoCcts.getFccCentroCustos());
		}
		return  custosSigFacade.listAtividadesAtivas(listaCentroCustos, objPesquisa);
	}

	public List<SigObjetoCustoVersoes> pesquisarProdutosComposicao(String objPesquisa) {		
		return this.custosSigFacade.pesquisarObjetoCustoIsProdutoServico(this.manterObjetosCustoController.getFccCentroCustos(), objPesquisa);
	}

	public void excluirComposicao() {
		try {
			this.setPossuiAlteracaoComposicao(true);
			for (int i = 0; i < this.getListaObjetoCustoComposicoes().size(); i++) {
				SigObjetoCustoComposicoes objetoExcluido = this.getListaObjetoCustoComposicoes().get(i);
				if (objetoExcluido.getSigAtividades() != null && seqAtividadeExclusao != null
						&& objetoExcluido.getSigAtividades().getSeq().intValue() == seqAtividadeExclusao.intValue()) {
					if (objetoExcluido.getSeq() != null) {
						this.custosSigFacade.validarExclusaoComposicaoObjetoCusto(this.manterObjetosCustoController.getObjetoCustoVersao());
						getListaObjetoCustoComposicoesExclusao().add(objetoExcluido);
					}
					getListaObjetoCustoComposicoes().remove(i);
					break;
				}
				if (objetoExcluido.getSigObjetoCustoVersoesCompoe() != null && seqServicoExclusao != null
						&& objetoExcluido.getSigObjetoCustoVersoesCompoe().getSeq().intValue() == seqServicoExclusao.intValue()) {
					if (objetoExcluido.getSeq() != null) {
						custosSigFacade.validarExclusaoComposicaoObjetoCusto(this.manterObjetosCustoController.getObjetoCustoVersao());
						getListaObjetoCustoComposicoesExclusao().add(objetoExcluido);
					}
					getListaObjetoCustoComposicoes().remove(i);
					break;
				}
			}
			this.setSeqAtividadeExclusao(null);
			this.setSeqServicoExclusao(null);
		} catch (ApplicationBusinessException e) {
			this.setSeqAtividadeExclusao(null);
			this.setSeqServicoExclusao(null);
			apresentarExcecaoNegocio(e);
		}
	}

	public String verificaAlteracaoNaoSalvaCopia() {
		if (!this.manterObjetosCustoController.isObjetoCustoAssistencial()) {
			this.apresentarMsgNegocio(Severity.INFO, "Adicionar a funcionalidade que está descrita na estória #23059.");
			return null;
		}
		if (this.manterObjetosCustoSliderPHIController.isPossuiAlteracaoPhi() || this.isPossuiAlteracaoComposicao()
				|| this.manterObjetosCustoSliderDirecionadoresController.isPossuiAlteracaoDirecionadorRateio()
				|| this.manterObjetosCustoController.isPossuiAlteracaoCampos()) {
			return null;
		} else {
			this.manterObjetosCustoController.setSeqObjetoCustoVersao(null);
			return this.manterObjetosCustoController.iniciarCopia();
		}
	}

	public String selecionaTitleCentroCustoObjetoCusto(SigObjetoCustoComposicoes composicao) {
		if(composicao != null && composicao.getSigAtividades() != null){
			SigAtividadeCentroCustos atividadeCentroCustos= custosSigFacade.obterCentroCustoPorAtividade(composicao.getSigAtividades().getSeq());
			if(atividadeCentroCustos != null){
				return atividadeCentroCustos.getFccCentroCustos().getDescricao();
			}
		}
		return null;
	}

	public String selecionaNomeCentroCustoObjetoCusto(SigObjetoCustoComposicoes composicao) {
		if(composicao != null && composicao.getSigAtividades() != null){
			SigAtividadeCentroCustos atividadeCentroCustos= custosSigFacade.obterCentroCustoPorAtividade(composicao.getSigAtividades().getSeq());
			if(atividadeCentroCustos != null){
				return atividadeCentroCustos.getFccCentroCustos().getCodigo().toString();
			}
		}
		return null;
	}
	
	public void visualizarManterAtividade(Integer seqAtividade){
		Map<String,Object> options = new HashMap<String, Object>();
	    options.put("modal", true);
	    options.put("contentWidth", 1024);
	    
	    Map<String,List<String>> param = new HashMap<String, List<String>>();	    
	    param.put("seqAtividade", Arrays.asList(seqAtividade.toString()));
	    
	    RequestContext.getCurrentInstance().openDialog("manterAtividades", options, param);		
	}

	public boolean possuiCalculo(SigAtividades atividade) {
		return this.custosSigFacade.possuiCalculo(atividade);
	}

	public boolean isPossuiAlteracaoComposicao() {
		return possuiAlteracaoComposicao;
	}

	public void setPossuiAlteracaoComposicao(boolean possuiAlteracaoComposicao) {
		this.possuiAlteracaoComposicao = possuiAlteracaoComposicao;
	}

	public Integer getIndexOfComposicao() {
		return indexOfComposicao;
	}

	public void setIndexOfComposicao(Integer indexOfComposicao) {
		this.indexOfComposicao = indexOfComposicao;
	}

	public SigObjetoCustoComposicoes getObjetoCustoComposicao() {
		return objetoCustoComposicao;
	}

	public void setObjetoCustoComposicao(SigObjetoCustoComposicoes objetoCustoComposicao) {
		this.objetoCustoComposicao = objetoCustoComposicao;
	}

	public List<SigObjetoCustoComposicoes> getListaObjetoCustoComposicoes() {
		return listaObjetoCustoComposicoes;
	}

	public void setListaObjetoCustoComposicoes(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes) {
		this.listaObjetoCustoComposicoes = listaObjetoCustoComposicoes;
	}

	public String getItemSelecionadoComposicao() {
		return itemSelecionadoComposicao;
	}

	public void setItemSelecionadoComposicao(String itemSelecionadoComposicao) {
		this.itemSelecionadoComposicao = itemSelecionadoComposicao;
	}

	public List<SigObjetoCustoComposicoes> getListaObjetoCustoComposicoesExclusao() {
		return listaObjetoCustoComposicoesExclusao;
	}

	public void setListaObjetoCustoComposicoesExclusao(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoesExclusao) {
		this.listaObjetoCustoComposicoesExclusao = listaObjetoCustoComposicoesExclusao;
	}

	public boolean isEdicaoComposicao() {
		return edicaoComposicao;
	}

	public void setEdicaoComposicao(boolean edicaoComposicao) {
		this.edicaoComposicao = edicaoComposicao;
	}

	public Integer getSeqAtividadeExclusao() {
		return seqAtividadeExclusao;
	}

	public void setSeqAtividadeExclusao(Integer seqAtividadeExclusao) {
		this.seqAtividadeExclusao = seqAtividadeExclusao;
	}

	public Integer getSeqServicoExclusao() {
		return seqServicoExclusao;
	}

	public void setSeqServicoExclusao(Integer seqServicoExclusao) {
		this.seqServicoExclusao = seqServicoExclusao;
	}

	public boolean isSugestionSelecionadaAtividade() {
		this.sugestionSelecionadaAtividade = (this.getItemSelecionadoComposicao().equals("0"));
		return this.sugestionSelecionadaAtividade;
	}

	public void setSugestionSelecionadaAtividade(boolean sugestionSelecionadaAtividade) {
		this.sugestionSelecionadaAtividade = sugestionSelecionadaAtividade;
	}

}