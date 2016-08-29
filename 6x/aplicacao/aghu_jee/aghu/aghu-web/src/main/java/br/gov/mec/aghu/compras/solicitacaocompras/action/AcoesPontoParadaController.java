package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações do criação e edição de Fontes de Recurso
 * 
 */
public class AcoesPontoParadaController extends ActionController {

	private static final String FASES_SOLICITACAO_COMPRA_LIST = "fasesSolicitacaoCompraList";

	private static final long serialVersionUID = -5861595457653656517L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	// Objetos
	private ScoAcoesPontoParada acoesPontoParada;
	private ScoPontoParadaSolicitacao pontoParada;
	private ScoScJn fases;	
	private List<ScoAcoesPontoParada> listHistoricoFases;

	//variaveis de tela
	private Integer numero;
	private Integer seq;
	private Short codigoPontoParada;
	private String descricaoPontoParada;
	private String responsavel;
	private Date data;
	private String situacao;
	private Boolean mostrarMsg;
	private Boolean modoEdicao;
	private String textoAcao;
	private ScoAcoesPontoParada itemEmEdicao;
	private Long seqExclusao;
	private Long seqPrimeiroRegistro;
	
	public void inicializaVariaveis() {
		this.setListHistoricoFases(new ArrayList<ScoAcoesPontoParada>());	
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 
		
		this.inicializaVariaveis();
		this.buscaDadosFase();
		this.setListHistoricoFases(this.listHistoricoFases());		
	
	}
	
	public List<ScoAcoesPontoParada> listHistoricoFases() {
		List<ScoAcoesPontoParada> result = this.solicitacaoComprasFacade.listarAcoesPontoParada(numero, codigoPontoParada, DominioTipoSolicitacao.SC);

		if (result.size() == 0) {
			result = new ArrayList<ScoAcoesPontoParada>();		
			this.setMostrarMsg(true);
			this.setSeqPrimeiroRegistro(null);
		}
		else {
			this.setMostrarMsg(false);
			this.setSeqPrimeiroRegistro(result.get(0).getSeq());
		}		
		
		return result;		
	}
	
	public void cancelarEdicao() {
		this.itemEmEdicao = null;
		this.textoAcao = null;
	}
	
	public void editarAcao(ScoAcoesPontoParada acao) {
		this.itemEmEdicao = acao;
		this.textoAcao = acao.getAcao();
	}
	
	public void removerAcao() {
		if (seqExclusao != null) {
			this.solicitacaoComprasFacade.removerScoAcoesPontoParada(seqExclusao);
			this.cancelarEdicao();
			this.inicio();
		}
	}
	
	public void atualizarAcao() {
		try {
			RapServidores servidor = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			if (this.itemEmEdicao == null) {
				ScoAcoesPontoParada acao = new ScoAcoesPontoParada();
				acao.setAcao(this.textoAcao);
				acao.setDataAcao(new Date());
				acao.setPontoParadaSolicitacao(this.pontoParada);
				acao.setServidor(servidor);
				acao.setSlcNumero(this.numero);
				this.solicitacaoComprasFacade.persistirScoAcoesPontoParada(acao);	
				
			} else {
				this.itemEmEdicao.setAcao(this.textoAcao);
				this.solicitacaoComprasFacade.persistirScoAcoesPontoParada(this.itemEmEdicao);
			}
			
			this.cancelarEdicao();
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void buscaDadosFase() {
		ScoScJn result = this.solicitacaoComprasFacade.obterFaseSolicitacaoCompra(numero,codigoPontoParada, seq);

		if (result != null) {
		    
			if (result.getPontoParadaSolicitacao() != null) {
				this.pontoParada = result.getPontoParadaSolicitacao();
				this.setDescricaoPontoParada(result.getPontoParadaSolicitacao().getDescricao());
			}
			
			if (result.getServidor() != null) {
				this.setResponsavel(result.getServidor().getPessoaFisica().getNome());
			}
			
			if (result.getDataAlteracao() != null) {
				this.setData(result.getDataAlteracao());
			}	
		}
	}	
	
	public String voltar() {		
		return FASES_SOLICITACAO_COMPRA_LIST;
	}

	//Get

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getCodigoPontoParada() {
		return codigoPontoParada;
	}

	public void setCodigoPontoParada(Short codigoPontoParada) {
		this.codigoPontoParada = codigoPontoParada;
	}
	
	public String getDescricaoPontoParada() {
		return descricaoPontoParada;
	}

	public void setDescricaoPontoParada(String descricaoPontoParada) {
		this.descricaoPontoParada = descricaoPontoParada;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Boolean getMostrarMsg() {
		return mostrarMsg;
	}

	public void setMostrarMsg(Boolean mostrarMsg) {
		this.mostrarMsg = mostrarMsg;
	}

	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public List<ScoAcoesPontoParada> getListHistoricoFases() {
		return listHistoricoFases;
	}

	public void setListHistoricoFases(List<ScoAcoesPontoParada> listHistoricoFases) {
		this.listHistoricoFases = listHistoricoFases;
	}

	public ScoAcoesPontoParada getAcoesPontoParada() {
		return acoesPontoParada;
	}

	public void setAcoesPontoParada(ScoAcoesPontoParada acoesPontoParada) {
		this.acoesPontoParada = acoesPontoParada;
	}

	public ScoScJn getFases() {
		return fases;
	}

	public void setFases(ScoScJn fases) {
		this.fases = fases;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Long getSeqPrimeiroRegistro() {
		return seqPrimeiroRegistro;
	}

	public void setSeqPrimeiroRegistro(Long seqPrimeiroRegistro) {
		this.seqPrimeiroRegistro = seqPrimeiroRegistro;
	}

	public String getTextoAcao() {
		return textoAcao;
	}

	public void setTextoAcao(String textoAcao) {
		this.textoAcao = textoAcao;
	}

	public ScoAcoesPontoParada getItemEmEdicao() {
		return itemEmEdicao;
	}

	public void setItemEmEdicao(ScoAcoesPontoParada itemEmEdicao) {
		this.itemEmEdicao = itemEmEdicao;
	}

	public Long getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Long seqExclusao) {
		this.seqExclusao = seqExclusao;
	}
	
}