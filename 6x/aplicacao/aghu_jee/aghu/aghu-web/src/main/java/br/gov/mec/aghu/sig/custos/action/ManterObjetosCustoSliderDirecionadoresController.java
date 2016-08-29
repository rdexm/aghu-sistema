package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;


public class ManterObjetosCustoSliderDirecionadoresController extends ActionController {	

	private static final long serialVersionUID = 2027278005508842323L;
	private static final BigDecimal BIG_DECIMAL_CEM = new BigDecimal(100);

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;
	
	@Inject
	private ManterObjetosCustoSliderClientesController manterObjetosCustoSliderClientesController;

	private Integer posicaoDirecionadorRateio;
	private Boolean edicaoDirecionadorRateio;
	private SigObjetoCustoDirRateios objetoCustoDirRateio;
	private List<SigObjetoCustoDirRateios> listaObjetoCustoDirRateios;
	private List<SigObjetoCustoDirRateios> listaObjetoCustoDirRateiosExclusao;
	private boolean possuiAlteracaoDirecionadorRateio;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicializarSliderDirecionadorRateio(Integer seqObjetoCustoVersao) {
		this.setListaObjetoCustoDirRateios(custosSigFacade.pesquisarDirecionadorePorObjetoCustoVersao(seqObjetoCustoVersao));
		this.setListaObjetoCustoDirRateiosExclusao(new ArrayList<SigObjetoCustoDirRateios>());
		inicializarObjetoCustoDirRateio();
		this.setPosicaoDirecionadorRateio(null);
		this.setEdicaoDirecionadorRateio(false);
		this.setPossuiAlteracaoDirecionadorRateio(false);		
	}
	
	public void inicializarObjetoCustoDirRateio() {
		this.setObjetoCustoDirRateio(new SigObjetoCustoDirRateios());
		this.getObjetoCustoDirRateio().setSituacao(DominioSituacao.A);
	}

	public List<SigDirecionadores> getListaDirecionadoresRateio() {
		return this.custosSigCadastrosBasicosFacade.pesquisarDirecionadores(DominioSituacao.A, DominioTipoDirecionadorCustos.RT);
	}

	public void adicionarDirecionadorRateio() {
		try {
			custosSigFacade.validarAlteracaoListaDirecionadorRateioObjetoCusto(this.getListaObjetoCustoDirRateios(), this.getObjetoCustoDirRateio(), null, null);
			
			//#Melhoria em Produção #54642 - Adicionar automaticamente um registro de cliente
			if(this.getObjetoCustoDirRateio().getDirecionadores().getIndColetaSistema() != null && this.getObjetoCustoDirRateio().getDirecionadores().getIndColetaSistema()){
				if(manterObjetosCustoSliderClientesController.getListaClientes() == null || manterObjetosCustoSliderClientesController.getListaClientes().isEmpty()){
					manterObjetosCustoSliderClientesController.getSigObjetoCustoClientes().setDirecionadores(this.getObjetoCustoDirRateio().getDirecionadores());
					manterObjetosCustoSliderClientesController.getSigObjetoCustoClientes().setIndTodosCct(true);
					manterObjetosCustoSliderClientesController.adicionarCliente();
				}
			}
			
			inicializarObjetoCustoDirRateio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarDirecionadorRateio(Integer posicaoDirecionadorRateio) {
		this.setPosicaoDirecionadorRateio(posicaoDirecionadorRateio);
		SigObjetoCustoDirRateios objetoCustoDirRateio = this.getListaObjetoCustoDirRateios().get(posicaoDirecionadorRateio);
		objetoCustoDirRateio.setEmEdicao(true);
		this.getObjetoCustoDirRateio().setDirecionadores(objetoCustoDirRateio.getDirecionadores());
		this.getObjetoCustoDirRateio().setPercentual(objetoCustoDirRateio.getPercentual());
		this.getObjetoCustoDirRateio().setSituacao(objetoCustoDirRateio.getSituacao());
		this.setEdicaoDirecionadorRateio(true);
		this.setPossuiAlteracaoDirecionadorRateio(true);
	}

	public void gravarDirecionadorRateio() {
		try {
			custosSigFacade.validarAlteracaoListaDirecionadorRateioObjetoCusto(this.getListaObjetoCustoDirRateios(), this.getObjetoCustoDirRateio(),
					this.getPosicaoDirecionadorRateio(), this.manterObjetosCustoSliderClientesController.getListaClientes());
			this.setEdicaoDirecionadorRateio(false);
			inicializarObjetoCustoDirRateio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicaoDirecionadorRateio() {
		this.getListaObjetoCustoDirRateios().get(this.getPosicaoDirecionadorRateio()).setEmEdicao(false);
		this.setPosicaoDirecionadorRateio(null);
		inicializarObjetoCustoDirRateio();
		this.setEdicaoDirecionadorRateio(false);
	}

	public void excluirDirecionadorRateio() throws ApplicationBusinessException {
		if (this.custosSigFacade.validarExclusaoDirecionadorRateio(this.manterObjetosCustoController.getObjetoCustoVersao().getIndSituacao(),
				this.manterObjetosCustoController.getObjetoCustoVersao().getDataInicio())) {
			
			SigObjetoCustoDirRateios dirRateio = this.getListaObjetoCustoDirRateios().get(this.getPosicaoDirecionadorRateio());
			
			//#Melhoria em Produção #54642 - Remover automaticamente o registro de cliente
			if(dirRateio.getDirecionadores().getIndColetaSistema() != null && dirRateio.getDirecionadores().getIndColetaSistema()){
				
				if(this.manterObjetosCustoSliderClientesController.getListaClientes().size() == 1){
					if(this.manterObjetosCustoSliderClientesController.getListaClientes().get(0).getDirecionadores().equals(dirRateio.getDirecionadores())){
						this.manterObjetosCustoSliderClientesController.excluirCliente(this.manterObjetosCustoSliderClientesController.getListaClientes().get(0));
					}
				}
			}
			
			if(this.custosSigFacade.validarExclusaoDirecionadorRateioAssociadoCliente( this.manterObjetosCustoSliderClientesController.getListaClientes(), dirRateio.getDirecionadores())){
			
				this.getListaObjetoCustoDirRateiosExclusao().add(dirRateio);
				this.getListaObjetoCustoDirRateios().remove(dirRateio);
				this.setPossuiAlteracaoDirecionadorRateio(true);
				this.setPosicaoDirecionadorRateio(null);
			}
			else{
				this.openDialog("modalExclusaoDirecionadorRateioAssociadoClienteWG");
			}
		} else {
			this.openDialog("modalExclusaoDirecionadorRateioNaoPermitidaWG");
		}
	}
	
	public void gravarDirecionadorRateioBanco() throws ApplicationBusinessException {
		this.custosSigFacade.persistirListaDirecionadorRateioObjetoCusto(this.getListaObjetoCustoDirRateios(), this.getListaObjetoCustoDirRateiosExclusao(),
				this.manterObjetosCustoController.getObjetoCustoVersao());
		this.setPossuiAlteracaoDirecionadorRateio(false);
	}
	
	public void visualizarCadastroDirecionadores(Integer seqDirecionador){
		Map<String,Object> options = new HashMap<String, Object>();
	    options.put("modal", true);
	    options.put("contentWidth", 900);
	    
	    Map<String,List<String>> param = new HashMap<String, List<String>>();	    
	    param.put("seqDirecionador", Arrays.asList(seqDirecionador.toString()));
	    
	    RequestContext.getCurrentInstance().openDialog("manterDirecionadorAtividade", options, param);		
	}
	
	public void verificarPercentualDirecionador(){
		if(this.objetoCustoDirRateio!= null && this.objetoCustoDirRateio.getPercentual() != null && this.objetoCustoDirRateio.getPercentual().compareTo(BIG_DECIMAL_CEM)>0){
			this.objetoCustoDirRateio.setPercentual(BIG_DECIMAL_CEM);
		}
	}

	public List<SigObjetoCustoDirRateios> getListaObjetoCustoDirRateios() {
		return listaObjetoCustoDirRateios;
	}

	public void setListaObjetoCustoDirRateios(List<SigObjetoCustoDirRateios> listaObjetoCustoDirRateios) {
		this.listaObjetoCustoDirRateios = listaObjetoCustoDirRateios;
	}

	public List<SigObjetoCustoDirRateios> getListaObjetoCustoDirRateiosExclusao() {
		return listaObjetoCustoDirRateiosExclusao;
	}

	public void setListaObjetoCustoDirRateiosExclusao(List<SigObjetoCustoDirRateios> listaObjetoCustoDirRateiosExclusao) {
		this.listaObjetoCustoDirRateiosExclusao = listaObjetoCustoDirRateiosExclusao;
	}

	public SigObjetoCustoDirRateios getObjetoCustoDirRateio() {
		return objetoCustoDirRateio;
	}

	public void setObjetoCustoDirRateio(SigObjetoCustoDirRateios objetoCustoDirRateio) {
		this.objetoCustoDirRateio = objetoCustoDirRateio;
	}

	public Integer getPosicaoDirecionadorRateio() {
		return posicaoDirecionadorRateio;
	}

	public void setPosicaoDirecionadorRateio(Integer posicaoDirecionadorRateio) {
		this.posicaoDirecionadorRateio = posicaoDirecionadorRateio;
	}

	public Boolean getEdicaoDirecionadorRateio() {
		return edicaoDirecionadorRateio;
	}

	public void setEdicaoDirecionadorRateio(Boolean edicaoDirecionadorRateio) {
		this.edicaoDirecionadorRateio = edicaoDirecionadorRateio;
	}

	public boolean isPossuiAlteracaoDirecionadorRateio() {
		return possuiAlteracaoDirecionadorRateio;
	}

	public void setPossuiAlteracaoDirecionadorRateio(boolean possuiAlteracaoDirecionadorRateio) {
		this.possuiAlteracaoDirecionadorRateio = possuiAlteracaoDirecionadorRateio;
	}
}