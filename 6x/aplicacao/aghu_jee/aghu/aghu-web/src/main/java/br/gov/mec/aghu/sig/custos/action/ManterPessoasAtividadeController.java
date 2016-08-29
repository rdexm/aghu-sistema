package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.SigAtividadePessoaRestricoes;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;


public class ManterPessoasAtividadeController extends ActionController {

	
	private static final long serialVersionUID = 3574267144865608494L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private ManterAtividadesController manterAtividadesController;

	private SigAtividadePessoas sigAtividadePessoas;
	private List<SigAtividadePessoas> listaPessoas;
	private List<SigAtividadePessoas> listaPessoasExcluir;

	private boolean edicao;
	private Integer indexOfObjEdicao;

	private Integer seqGrupoOcupacaoPessoaExclusao;

	private boolean possuiAlteracao;

	private SigAtividades atividade;

	private final Integer ABA_1 = 0;

	private static final Log LOG = LogFactory.getLog(ManterPessoasAtividadeController.class);
	
	
	private SigAtividadePessoas sigAtividadePessoasRestricao;
	private List<SigAtividadePessoaRestricoes> restricoes;
	
	private SigCategoriaRecurso categoriaRecursoPessoa;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		categoriaRecursoPessoa = custosSigFacade.obterCategoriaRecursoPorChavePrimaria(2);
	}
	
	public void iniciarAbaPessoal(Integer seqAtividade) {
		this.setSigAtividadePessoas(new SigAtividadePessoas());
		this.setListaPessoas(new ArrayList<SigAtividadePessoas>());
		this.setListaPessoasExcluir(new ArrayList<SigAtividadePessoas>());
		this.setEdicao(false);
		setPossuiAlteracao(false);

		// alteração
		if (seqAtividade != null) {
			atividade = custosSigFacade.obterAtividade(seqAtividade);
			List<SigAtividadePessoas> listResult = custosSigFacade.pesquisarPessoasPorSeqAtividade(seqAtividade);
			if (listResult != null && !listResult.isEmpty()) {
				for (SigAtividadePessoas sigAtividadePessoas : listResult) {
					sigAtividadePessoas.setEmEdicao(Boolean.FALSE);
					sigAtividadePessoas.setListAtividadePessoaRestricoes(custosSigFacade.listarAtividadePessoaRestricoes(sigAtividadePessoas));
				}
				this.setListaPessoas(listResult);
			}
		}
	}

	public List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(String paramPesquisa) throws BaseException {
		List<SigGrupoOcupacoes> listaResultado = new ArrayList<SigGrupoOcupacoes>();
		listaResultado = this.custosSigCadastrosBasicosFacade.pesquisarGrupoOcupacao(paramPesquisa, this.manterAtividadesController.getFccCentroCustos());
		return listaResultado;
	}

	public List<SigDirecionadores> getListaDirecionadores() {
		List<SigDirecionadores> listaDirecionadores = new ArrayList<SigDirecionadores>();
		listaDirecionadores = custosSigCadastrosBasicosFacade.pesquisarDirecionadores(Boolean.FALSE, Boolean.TRUE);
		return listaDirecionadores;
	}

	public void editarPessoa(SigAtividadePessoas sigAtividadePessoas, Integer indPessoa) {
		this.setPossuiAlteracao(true);
		this.setEdicao(true);
		this.setIndexOfObjEdicao(indPessoa);
		sigAtividadePessoas.setEmEdicao(Boolean.TRUE);
		try {
			this.setSigAtividadePessoas((SigAtividadePessoas) sigAtividadePessoas.clone());
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe SigAtividadePessoas " + "não implementa a interface Cloneable.", e);
		}
		this.getSigAtividadePessoas().setEmEdicao(Boolean.TRUE);
	}

	public void visualizarPessoa(SigAtividadePessoas sigAtividadePessoas) {
		this.setSigAtividadePessoas(sigAtividadePessoas);
	}

	public void excluirPessoa() {
		this.setPossuiAlteracao(true);
		if (seqGrupoOcupacaoPessoaExclusao != null) {
			for (int i = 0; i < this.getListaPessoas().size(); i++) {
				SigAtividadePessoas sigAtividadePessoasExcluir = this.getListaPessoas().get(i);
				if (sigAtividadePessoasExcluir.getSigGrupoOcupacoes().getSeq().equals(seqGrupoOcupacaoPessoaExclusao)) {
					if (sigAtividadePessoasExcluir.getSeq() != null) {
						
						if (atividade != null && custosSigFacade.verificaAtividadeEstaVinculadaAoObjetoCusto(atividade)) {
							this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_PESSOA_ATIVIDADE_OBJETO_CUSTO");
							manterAtividadesController.setTabSelecionada(ABA_1);
							return;
						}
						this.getListaPessoasExcluir().add(sigAtividadePessoasExcluir);
					}
					this.getListaPessoas().remove(i);
					break;
				}
			}
		}
	}

	public void adicionarPessoa() {
		try {
			setPossuiAlteracao(true);
			custosSigFacade.validarInclusaoPessoaAtividade(this.getSigAtividadePessoas(), this.getListaPessoas());
			if (this.getSigAtividadePessoas().getSeq() == null) {
				this.getSigAtividadePessoas().setCriadoEm(new Date());
			}
			this.getSigAtividadePessoas().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.getListaPessoas().add(this.getSigAtividadePessoas());
			Collections.sort(this.getListaPessoas(), new Comparator<SigAtividadePessoas>() {
				public int compare(SigAtividadePessoas sap1, SigAtividadePessoas sap2) {
					return sap1.getSigGrupoOcupacoes().getDescricao().compareTo(sap2.getSigGrupoOcupacoes().getDescricao());
				}
			});
			this.setSigAtividadePessoas(new SigAtividadePessoas());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	public void gravarPessoa() {
		try {
			setPossuiAlteracao(true);
			custosSigFacade.validarAlteracaoPessoaAtividade(this.getSigAtividadePessoas(), this.getListaPessoas());
			this.setEdicao(false);
			this.getSigAtividadePessoas().setEmEdicao(Boolean.FALSE);
			this.getSigAtividadePessoas().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.getListaPessoas().set(this.getIndexOfObjEdicao(), this.getSigAtividadePessoas());
			this.setSigAtividadePessoas(new SigAtividadePessoas());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicaoPessoa() {
		this.setEdicao(false);
		this.getListaPessoas().get(this.getIndexOfObjEdicao()).setEmEdicao(Boolean.FALSE);
		this.setSigAtividadePessoas(new SigAtividadePessoas());
	}
	
	public void	editarRestricoes(SigAtividadePessoas sigAtividadePessoas) {
		
		this.sigAtividadePessoasRestricao = sigAtividadePessoas;
		this.restricoes = new ArrayList<SigAtividadePessoaRestricoes>();
		
		List<AacPagador> pagadores = this.ambulatorioFacade.listarPagadoresAtivos();		
		
		for (AacPagador pagador : pagadores) {

			SigAtividadePessoaRestricoes novaRestricao = new SigAtividadePessoaRestricoes();
			novaRestricao.setPagador(pagador);
			
			for (SigAtividadePessoaRestricoes restricao : sigAtividadePessoas.getListAtividadePessoaRestricoes()) {
				if(restricao.getPagador().equals(pagador)){					
					novaRestricao.setPercentual(restricao.getPercentual());
					break;
				}
			}
			
			restricoes.add(novaRestricao);
		}
	}
	
	public void salvarRestricoes() throws ApplicationBusinessException{
		
		for (SigAtividadePessoaRestricoes restricao : this.restricoes){
			
			boolean possuiRestricao = false;
			for (SigAtividadePessoaRestricoes restricaoExistente : sigAtividadePessoasRestricao.getListAtividadePessoaRestricoes()){
				if(restricaoExistente.getPagador().equals(restricao.getPagador())){
					possuiRestricao = true;
					restricaoExistente.setPercentual(restricao.getPercentual());
					break;
				}
			}
			
			if(!possuiRestricao && restricao.getPercentual() != null){
				restricao.setCategoriaRecurso(categoriaRecursoPessoa);
				restricao.setCriadoEm(new Date());
				restricao.setSigAtividadePessoas(sigAtividadePessoas);
				restricao.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				sigAtividadePessoasRestricao.getListAtividadePessoaRestricoes().add(restricao);
			}
		}
		setPossuiAlteracao(true);
	}
	
	public String listarRestricoesComoMensagem(SigAtividadePessoas sigAtividadePessoas){
		
		StringBuffer sbRestricoes =  new StringBuffer("");
		if(sigAtividadePessoas != null && sigAtividadePessoas.getListAtividadePessoaRestricoes() != null){
			for (int i = 0; i < sigAtividadePessoas.getListAtividadePessoaRestricoes().size(); i++){
				SigAtividadePessoaRestricoes restricao  = sigAtividadePessoas.getListAtividadePessoaRestricoes().get(i);
				
				if(restricao.getPercentual() != null){
					sbRestricoes.append(restricao.getPagador().getDescricao());
					
					if(i < sigAtividadePessoas.getListAtividadePessoaRestricoes().size() - 1){
						if(sigAtividadePessoas.getListAtividadePessoaRestricoes().get(sigAtividadePessoas.getListAtividadePessoaRestricoes().size() - 1).getPercentual() != null){
							sbRestricoes.append(", ");
						}
					}
				}
			}
		}
		
		return sbRestricoes.toString();
	}
	
	// gets e sets

	public SigAtividadePessoas getSigAtividadePessoas() {
		return sigAtividadePessoas;
	}

	public void setSigAtividadePessoas(SigAtividadePessoas sigAtividadePessoas) {
		this.sigAtividadePessoas = sigAtividadePessoas;
	}

	public List<SigAtividadePessoas> getListaPessoas() {
		return listaPessoas;
	}

	public void setListaPessoas(List<SigAtividadePessoas> listaPessoas) {
		this.listaPessoas = listaPessoas;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getIndexOfObjEdicao() {
		return indexOfObjEdicao;
	}

	public void setIndexOfObjEdicao(Integer indexOfObjEdicao) {
		this.indexOfObjEdicao = indexOfObjEdicao;
	}

	public List<SigAtividadePessoas> getListaPessoasExcluir() {
		return listaPessoasExcluir;
	}

	public void setListaPessoasExcluir(List<SigAtividadePessoas> listaPessoasExcluir) {
		this.listaPessoasExcluir = listaPessoasExcluir;
	}

	public Integer getSeqGrupoOcupacaoPessoaExclusao() {
		return seqGrupoOcupacaoPessoaExclusao;
	}

	public void setSeqGrupoOcupacaoPessoaExclusao(Integer seqGrupoOcupacaoPessoaExclusao) {
		this.seqGrupoOcupacaoPessoaExclusao = seqGrupoOcupacaoPessoaExclusao;
	}

	public void setPossuiAlteracao(boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public boolean isPossuiAlteracao() {
		return possuiAlteracao;
	}
	
	public List<SigAtividadePessoaRestricoes> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(List<SigAtividadePessoaRestricoes> restricoes) {
		this.restricoes = restricoes;
	}

}
