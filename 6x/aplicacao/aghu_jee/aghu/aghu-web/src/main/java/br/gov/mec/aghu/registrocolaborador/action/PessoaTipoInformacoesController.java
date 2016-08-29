package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoesId;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PessoaTipoInformacoesController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6965874835654187396L;

	private static final String PESQUISAR_PESSOA_TIPO_INFORMACOES = "pesquisarPessoaTipoInformacoes";

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;	

	private RapPessoaTipoInformacoes pessoaTipoInformacao;

	private Long seq;
	private Short tiiSeq;
	private Integer pesCodigo;
	private String nomePessoa;

	// Lista de valores de tipos de informações
	private RapTipoInformacoes rapTipoInformacoes;

	private FatCbos fatCbo;

	private boolean cbo;
	
	/**
	 * Método que apresenta a tela de edição para alteração ou inclusão.
	 */
	public String iniciar() {
	 

		if (seq != null) {
			try {
				pessoaTipoInformacao = registroColaboradorFacade.obterPessoaTipoInformacoesSemRefresh(new RapPessoaTipoInformacoesId(pesCodigo, tiiSeq, seq));
				if(pessoaTipoInformacao == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return voltar();
				}
				rapTipoInformacoes = pessoaTipoInformacao.getTipoInformacao();
				this.cbo = registroColaboradorFacade.isCbo(tiiSeq);
				
				if(isCbo()){
					fatCbo = faturamentoFacade.obterFatCboPorCodigoVigente(this.pessoaTipoInformacao.getValor());
				}
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.ERROR,e.getCode().toString());
			}
		} else {
			pessoaTipoInformacao = new RapPessoaTipoInformacoes();
			fatCbo = null;
			rapTipoInformacoes = null;
		}
		
		return null;
	
	}
	
	public String salvar() {
		// para quando voltar para pesquisa, atualizar.
		//super.reiniciarPaginator(PessoaTipoInformacoesPaginatorController.class);

		try {
			pessoaTipoInformacao.setTipoInformacao(rapTipoInformacoes);
			
			if(fatCbo != null && fatCbo.getCodigo() != null){
				pessoaTipoInformacao.setValor(fatCbo.getCodigo());
			} 
			
			// Alteração
			if (seq != null && pesCodigo != null && tiiSeq != null) {
				
				registroColaboradorFacade.salvar(pessoaTipoInformacao, true);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_PESSOA_TIPO_INFORMACAO_ALTERADO_COM_SUCESSO");
				
			} else {
				pessoaTipoInformacao.setId(new RapPessoaTipoInformacoesId(pesCodigo, rapTipoInformacoes.getSeq().shortValue(), seq));
				registroColaboradorFacade.salvar(pessoaTipoInformacao, false);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_PESSOA_TIPO_INFORMACAO_INCLUIDO_COM_SUCESSO");
			}

			return voltar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	
	// Suggestions
	/**
	 * Busca lista de Tipos de Informações
	 */
	public List<RapTipoInformacoes> buscarTiposInformacoes(String objPesquisa) {
		return this.returnSGWithCount(cadastrosBasicosFacade.pesquisarTipoInformacoesSuggestion(objPesquisa),buscarTiposInformacoesCount(objPesquisa));
	}
	
	public Long buscarTiposInformacoesCount(String objPesquisa) {
		return cadastrosBasicosFacade.pesquisarTipoInformacoesSuggestionCount(objPesquisa);
	}

	public void atualizarIsCbo(){
		pessoaTipoInformacao.setValor(null);
		fatCbo = null;
		
		try {
			if(rapTipoInformacoes != null && rapTipoInformacoes.getSeq() != null){
				this.cbo = this.registroColaboradorFacade.isCbo(rapTipoInformacoes.getSeq().shortValue());
				
			} else {
				this.cbo = false;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		seq = null;
		fatCbo = null;
		pessoaTipoInformacao = null;
		rapTipoInformacoes = null;
		pesCodigo = null;
		tiiSeq = null;
		return PESQUISAR_PESSOA_TIPO_INFORMACOES;
	}
	
	
	public List<FatCbos> listarCbos(String objPesquisa){
		try {
			return this.returnSGWithCount(this.faturamentoFacade.listarCbos(objPesquisa),listarCbosCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return new ArrayList<FatCbos>();
	}

	public Long listarCbosCount(String objPesquisa){
		return this.faturamentoFacade.listarCbosCount(objPesquisa);
	}


	public RapTipoInformacoes getRapTipoInformacoes() {
		return rapTipoInformacoes;
	}

	public void setRapTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) {
		this.rapTipoInformacoes = rapTipoInformacoes;
	}

	public RapPessoaTipoInformacoes getPessoaTipoInformacao() {
		return pessoaTipoInformacao;
	}

	public void setPessoaTipoInformacao(
			RapPessoaTipoInformacoes pessoaTipoInformacao) {
		this.pessoaTipoInformacao = pessoaTipoInformacao;
	}

	public Integer getPesCodigo() {
		return pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	public Short getTiiSeq() {
		return tiiSeq;
	}

	public void setTiiSeq(Short tiiSeq) {
		this.tiiSeq = tiiSeq;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}


	public boolean isCbo() {
		return cbo;
	}

	public void setCbo(boolean cbo) {
		this.cbo = cbo;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public FatCbos getFatCbo() {
		return fatCbo;
	}

	public void setFatCbo(FatCbos fatCbo) {
		this.fatCbo = fatCbo;
	}		
}