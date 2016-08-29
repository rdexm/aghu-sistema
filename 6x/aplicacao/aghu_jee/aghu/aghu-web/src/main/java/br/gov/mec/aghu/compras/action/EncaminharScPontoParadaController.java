package br.gov.mec.aghu.compras.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class EncaminharScPontoParadaController extends ActionController {

	private static final long serialVersionUID = -3664447362389157225L;

	private static final String MANTER_SOLICITACAO_COMPRA = "compras-manterSolicitacaoCompra";

	private Integer numeroSolicitacaoCompra;
	
	private Integer matricula;

	private Short vinCodigo;

	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private List<ScoPontoParadaSolicitacao> scoPontoParadaSolicitacaos;
	
	private List<RapServidores> rapServidores;
	
	private ScoPontoParadaSolicitacao scoPontoParadaSolicitacao;
	
	private Short numeroScoPontoParadaSolicitacao;
	
	private Boolean desabilitaBotaoComprador;
	
	private Boolean desabilitaTabelaComprador;
	
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	
	private RapServidores rapServidor;
	
	private Short pPpsComprador;
	
	private Integer pOcupacaoComprador;
	
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Estoria 5232 - RN9
	 */
	public void iniciar() {
	 

	 

		iniciarValoresDesfault();
		try {
			carregaDados();
			if (!isQuantidadePontoParadaServidorMaiorQueUm()) {
				ScoSolicitacaoDeCompra solicitacaoCompraOld = solicitacaoComprasFacade.clonarSolicitacaoDeCompra(solicitacaoDeCompra);
				comprasCadastrosBasicosFacade.enviarSolicitacaoCompras(getSolicitacaoDeCompra(), solicitacaoCompraOld, getScoPontoParadaSolicitacao());
				solicitacaoComprasFacade.persistirSolicitacaoDeCompra(solicitacaoDeCompra, solicitacaoCompraOld);
			} else {
				setScoPontoParadaSolicitacaos(carregarListaPontoParada());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	public String cancelar(){
		return MANTER_SOLICITACAO_COMPRA;
	}
	
	private List<RapServidores> carregarListaRapServidoresComprador() {
		return registroColaboradorFacade.pesquisarServidoresCompradorAtivo(null, null, getpOcupacaoComprador());
	}
	
	private List<ScoPontoParadaSolicitacao> carregarListaPontoParada() {
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorEnviadoPara(getSolicitacaoDeCompra().getPontoParadaProxima().getCodigo());
	}
	
	/**
	 * Estoria 5232 - RN12
	 * Verifica se a quantidade do ponto de parada do servidor é maior que 1
	 */
	private Boolean isQuantidadePontoParadaServidorMaiorQueUm() throws ApplicationBusinessException {
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		
		return comprasCadastrosBasicosFacade.isQuantidadePontoParadaServidorMaiorQueUm(servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
	}
	
	private void carregaDados() throws ApplicationBusinessException {
		recuperaScoSolicitacaoDeCompra();
		
		ScoPontoParadaSolicitacao ppsComprador = this.comprasCadastrosBasicosFacade.obterPontoParadaPorTipo(DominioTipoPontoParada.CP);
		setpPpsComprador(ppsComprador.getCodigo());
		
		AghParametros ocupacaoComprador = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_OCUPACAO_COMPRADOR);
		setpOcupacaoComprador(ocupacaoComprador.getVlrNumerico().intValue());
	}

	private void recuperaScoSolicitacaoDeCompra() {
		setSolicitacaoDeCompra(solicitacaoComprasFacade.obterSolicitacaoDeCompra(getNumeroSolicitacaoCompra()));		
	}
	
	/**
	 * Estoria 5232 - RN7
	 */
	private void verificaPontoParadaSolicitacaoSelecionado() {
		if (getScoPontoParadaSolicitacao() != null) {
			if ( getScoPontoParadaSolicitacao().getCodigo().equals(getpPpsComprador())) {
				setDesabilitaBotaoComprador(Boolean.FALSE);
				setRapServidores(carregarListaRapServidoresComprador());
				
			} else {
				setDesabilitaBotaoComprador(Boolean.TRUE);
			}
		} else {
			iniciarValoresDesfault();
		}
	}
	
	private void iniciarValoresDesfault() {
		setDesabilitaTabelaComprador(Boolean.TRUE);
		setDesabilitaBotaoComprador(Boolean.TRUE);
	}
	
	public void obterPontoParadaVerificarAcaoPermitida(){
		setScoPontoParadaSolicitacao(comprasCadastrosBasicosFacade.obterPontoParada(getNumeroScoPontoParadaSolicitacao()));
		verificaPontoParadaSolicitacaoSelecionado();
	}
	
	public void obterRapServidorAcaoPermitida(){
		setRapServidor(registroColaboradorFacade.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(getMatricula(), getVinCodigo()));
	}
	
	public void ativarTabelaComprador() {
		setDesabilitaTabelaComprador(Boolean.FALSE);
	}
	
	/**
	 * Estoria 5232 - RN10
	 * @return
	 */
	public void gravar() {
		try {
			ScoSolicitacaoDeCompra solicitacaoCompraOld = solicitacaoComprasFacade.clonarSolicitacaoDeCompra(solicitacaoDeCompra);
			comprasCadastrosBasicosFacade.enviarSolicitacaoCompras(getSolicitacaoDeCompra(), solicitacaoCompraOld, getScoPontoParadaSolicitacao());
			solicitacaoComprasFacade.persistirSolicitacaoDeCompra(solicitacaoDeCompra, solicitacaoCompraOld);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEncaminharComprador() {
		setRapServidor(null);
		setDesabilitaTabelaComprador(Boolean.TRUE);
	}
	
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}

	public List<ScoPontoParadaSolicitacao> getScoPontoParadaSolicitacaos() {
		return scoPontoParadaSolicitacaos;
	}

	public void setScoPontoParadaSolicitacaos(List<ScoPontoParadaSolicitacao> scoPontoParadaSolicitacaos) {
		this.scoPontoParadaSolicitacaos = scoPontoParadaSolicitacaos;
	}

	public ScoPontoParadaSolicitacao getScoPontoParadaSolicitacao() {
		return scoPontoParadaSolicitacao;
	}

	public void setScoPontoParadaSolicitacao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		this.scoPontoParadaSolicitacao = scoPontoParadaSolicitacao;
	}

	public Short getNumeroScoPontoParadaSolicitacao() {
		return numeroScoPontoParadaSolicitacao;
	}
	
	public void setNumeroScoPontoParadaSolicitacao(	Short numeroScoPontoParadaSolicitacao) {
		this.numeroScoPontoParadaSolicitacao = numeroScoPontoParadaSolicitacao;
	}
	
	public Boolean getDesabilitaBotaoComprador() {
		return desabilitaBotaoComprador;
	}
	
	public void setDesabilitaBotaoComprador(Boolean desabilitaBotaoComprador) {
		this.desabilitaBotaoComprador = desabilitaBotaoComprador;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	public Short getpPpsComprador() {
		return pPpsComprador;
	}

	public void setpPpsComprador(Short pPpsComprador) {
		this.pPpsComprador = pPpsComprador;
	}

	public Boolean getDesabilitaTabelaComprador() {
		return desabilitaTabelaComprador;
	}

	public void setDesabilitaTabelaComprador(Boolean desabilitaTabelaComprador) {
		this.desabilitaTabelaComprador = desabilitaTabelaComprador;
	}

	public Integer getpOcupacaoComprador() {
		return pOcupacaoComprador;
	}

	public void setpOcupacaoComprador(Integer pOcupacaoComprador) {
		this.pOcupacaoComprador = pOcupacaoComprador;
	}

	public RapServidores getRapServidor() {
		return rapServidor;
	}

	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}

	public List<RapServidores> getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(List<RapServidores> rapServidores) {
		this.rapServidores = rapServidores;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
}

