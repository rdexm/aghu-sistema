package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPesoClienteVO;


public class PesquisarObjetoCustoPesoClientesPaginatorController extends ActionController implements ActionPaginator{

	

	private static final String MANTER_PESO_CLIENTES_OBJETO_CUSTO = "manterPesoClientesObjetoCusto";

	@Inject @Paginator
	private DynamicDataModel<ObjetoCustoPesoClienteVO> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarObjetoCustoPesoClientesPaginatorController.class);

	private static final long serialVersionUID = -775517844945902487L;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private FccCentroCustos centroCusto;
	private SigDirecionadores direcionador;
	private DominioSituacaoVersoesCustos situacao;
	private String nome;
	private Integer dirSeq;
	private Integer ocvSeq;
	private SigObjetoCustoClientes sigObjetoCustoClientes;

	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
		
		if (this.getCentroCusto() == null) {
			try {
				RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (servidor.getCentroCustoAtuacao() != null) {
					this.setCentroCusto(servidor.getCentroCustoAtuacao());
				} else {
					this.setCentroCusto(servidor.getCentroCustoLotacao());
				}
			} catch (ApplicationBusinessException e) {
				this.setCentroCusto(null);
			}
		}
	}
	
	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		return listaResultado;
	}
	
	public void limparCentroCusto(){
		this.setCentroCusto(null);
	}
	
	public void limpar(){
		this.setCentroCusto(null);
		this.setDirecionador(null);
		this.setNome(null);
		this.setSituacao(null);
		this.setAtivo(false);
	}
	
	public void pesquisar(){
		this.reiniciarPaginator();
	}
	
	public String verificaIndTodosCC(ObjetoCustoPesoClienteVO objetoCustoPesoCliente) throws BaseException{
		
		if(objetoCustoPesoCliente.getDirecionador().getIndTipoCalculo() == DominioTipoCalculoObjeto.PE && (objetoCustoPesoCliente.getDirecionador().getIndColetaSistema() == null || !objetoCustoPesoCliente.getDirecionador().getIndColetaSistema()) ){
			
			this.setSigObjetoCustoClientes(this.custosSigCadastrosBasicosFacade.validaIndicacaoTodosCC(objetoCustoPesoCliente.getOcvSeq(),objetoCustoPesoCliente.getDirecionador().getSeq()));
			
			if(this.getSigObjetoCustoClientes() != null){
				this.openDialog("modalValidaTodosCCWG");
			}else {
				return this.editar(false);
			}
		}
		else{
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_EDICAO_NAO_PERMITIDA_PESO_CLIENTE");
		}
		
		return null;
	}
	
	public String editar(boolean indTodosCC) throws BaseException{
		if(indTodosCC){
			RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			this.custosSigCadastrosBasicosFacade.associarCentrosCustoClientes(this.getSigObjetoCustoClientes(), servidorLogado);
		}
		return MANTER_PESO_CLIENTES_OBJETO_CUSTO;
	}
	
	public String visualizar(){
		return MANTER_PESO_CLIENTES_OBJETO_CUSTO;
	}
	
	@Override
	public List<ObjetoCustoPesoClienteVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigCadastrosBasicosFacade.pesquisarObjetoCustoPesoCliente(firstResult, maxResult, orderProperty, asc, 
				this.getCentroCusto(), this.getDirecionador(), this.getNome(), this.getSituacao());
	}
	
	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.pesquisarObjetoCustoPesoClienteCount(this.getCentroCusto(), this.getDirecionador(), this.getNome(), this.getSituacao());
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public List<SigDirecionadores> listarDirecionadores(){
		return this.custosSigCadastrosBasicosFacade.pesquisarDirecionadores(DominioTipoDirecionadorCustos.RT, DominioTipoCalculoObjeto.PE,  Boolean.FALSE);
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	public DominioSituacaoVersoesCustos getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersoesCustos situacao) {
		this.situacao = situacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getDirSeq() {
		return dirSeq;
	}

	public void setDirSeq(Integer dirSeq) {
		this.dirSeq = dirSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public SigObjetoCustoClientes getSigObjetoCustoClientes() {
		return sigObjetoCustoClientes;
	}

	public void setSigObjetoCustoClientes(
			SigObjetoCustoClientes sigObjetoCustoClientes) {
		this.sigObjetoCustoClientes = sigObjetoCustoClientes;
	}

	public DynamicDataModel<ObjetoCustoPesoClienteVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ObjetoCustoPesoClienteVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
