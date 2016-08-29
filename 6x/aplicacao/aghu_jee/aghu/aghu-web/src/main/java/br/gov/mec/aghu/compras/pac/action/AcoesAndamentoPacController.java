package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class AcoesAndamentoPacController extends ActionController {

	private static final String ANDAMENTO_PAC_LIST = "andamentoPacList";

	private static final long serialVersionUID = -5861595457653656517L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	// Objetos
	private ScoAcoesPontoParada acoesPontoParada;	
	private List<ScoAcoesPontoParada> listHistoricoFases;
	private ScoAndamentoProcessoCompra andamentoPac;

	//variaveis de tela
	private Integer numeroPac;
	private ScoLocalizacaoProcesso localizacaoPac;
	private String responsavel;
	private Date dataEntrada;
	private Date dataSaida;
	
	private Boolean mostrarMsg;
	private Boolean modoEdicao;
	private String textoAcao;
	private ScoAcoesPontoParada itemEmEdicao;
	private Long seqExclusao;
	private Long seqPrimeiroRegistro;
	private Integer seqAndamento;

	public void inicializaVariaveis() {
		this.setListHistoricoFases(new ArrayList<ScoAcoesPontoParada>());	
		textoAcao = null;
		itemEmEdicao = null;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.executarAcoesIniciar();
		//this.//setIgnoreInitPageConfig(true);
	}
	
	
	private void executarAcoesIniciar(){
		this.inicializaVariaveis();
		this.buscaDadosFase();
		this.setListHistoricoFases(this.listHistoricoFases());	
		
	}
	
	public List<ScoAcoesPontoParada> listHistoricoFases() {
		List<ScoAcoesPontoParada> result = this.pacFacade.listarAcoesPontoParadaPac(seqAndamento);

		if (result.size() == 0) {
			result = new ArrayList<ScoAcoesPontoParada>();		
			this.setMostrarMsg(true);
			this.seqPrimeiroRegistro = null;
		}
		else {
			this.setMostrarMsg(false);
			this.seqPrimeiroRegistro = result.get(0).getSeq();
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
			this.executarAcoesIniciar();
		}
	}
	
	public void atualizarAcao() {
		try {
			ScoPontoParadaSolicitacao ppsLicitacao = this.comprasCadastrosBasicosFacade.obterPontoParadaPorTipo(DominioTipoPontoParada.LI);
			
			if (ppsLicitacao != null) {
				RapServidores servidor = registroColaboradorFacade
						.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
								new Date());
	
				if (this.itemEmEdicao == null) {
					ScoAcoesPontoParada acao = new ScoAcoesPontoParada();
					acao.setAcao(this.textoAcao);
					acao.setDataAcao(new Date());
					acao.setServidor(servidor);
					acao.setPontoParadaSolicitacao(ppsLicitacao);
					acao.setAndamentoPac(this.andamentoPac);
					this.solicitacaoComprasFacade.persistirScoAcoesPontoParada(acao);	
					
				} else {
					this.itemEmEdicao.setAcao(this.textoAcao);
					this.solicitacaoComprasFacade.persistirScoAcoesPontoParada(this.itemEmEdicao);
				}
			
			}
			this.cancelarEdicao();
			this.executarAcoesIniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void buscaDadosFase() {
		this.andamentoPac = this.pacFacade.obterAndamentoPac(seqAndamento);

		if (this.andamentoPac != null) {
		    
			this.numeroPac = this.andamentoPac.getLicitacao().getNumero();
			
			if (this.andamentoPac.getLocalizacaoProcesso() != null) {
				this.localizacaoPac = this.andamentoPac.getLocalizacaoProcesso();
			}
			
			if (this.andamentoPac.getServidor() != null) {
				this.setResponsavel(this.andamentoPac.getServidor().getPessoaFisica().getNome());
			}
			
			if (this.andamentoPac.getDtEntrada() != null) {
				this.dataEntrada = this.andamentoPac.getDtEntrada();
			}
			
			if (this.andamentoPac.getDtSaida() != null) {
				this.dataSaida = this.andamentoPac.getDtSaida();
			}	
		}
	}	
	
	public String voltar() {		
		return ANDAMENTO_PAC_LIST;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public Boolean getMostrarMsg() {
		return mostrarMsg;
	}

	public void setMostrarMsg(Boolean mostrarMsg) {
		this.mostrarMsg = mostrarMsg;
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

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
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

	public Long getSeqPrimeiroRegistro() {
		return seqPrimeiroRegistro;
	}

	public void setSeqPrimeiroRegistro(Long seqPrimeiroRegistro) {
		this.seqPrimeiroRegistro = seqPrimeiroRegistro;
	}

	public Integer getSeqAndamento() {
		return seqAndamento;
	}

	public void setSeqAndamento(Integer seqAndamento) {
		this.seqAndamento = seqAndamento;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public ScoLocalizacaoProcesso getLocalizacaoPac() {
		return localizacaoPac;
	}

	public void setLocalizacaoPac(ScoLocalizacaoProcesso localizacaoPac) {
		this.localizacaoPac = localizacaoPac;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}	
}