package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CentroCustoComplementarController extends ActionController {

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final long serialVersionUID = 3789034059345093434L;
	
	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;
	
	private FccCentroCustos centroCusto;
	private Integer codigoCentroCusto;
	private List<SigObjetoCustoCcts> listCentroCustoOC;
	private List<SigObjetoCustoCcts> listCentroCustoOCExclusao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		this.setCentroCusto(null);
		this.setCodigoCentroCusto(null);
		this.iniciarListCentroCustoOC();
		this.setListCentroCustoOCExclusao(new ArrayList<SigObjetoCustoCcts>());
	
	}
	
	private void iniciarListCentroCustoOC() {
		this.setListCentroCustoOC(new ArrayList<SigObjetoCustoCcts>());
		
		this.getListCentroCustoOC().add(0, this.manterObjetosCustoController.getObjetoCustoVersao().getSigObjetoCustoCctsPrincipal());
		
		for(SigObjetoCustoCcts objetoCustoCcts : this.manterObjetosCustoController.getListaObjetoCustoCcts()){
			if(objetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.C)){
				getListCentroCustoOC().add(objetoCustoCcts);
			}
		}
	}
	
	private SigObjetoCustoCcts obterObjetoCustoCcts(Integer codigoCentroCusto, boolean isAdicao){
		for(SigObjetoCustoCcts objetoCustoCcts : this.getListCentroCustoOC()){
			if(objetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P) && objetoCustoCcts.getFccCentroCustos() != null && isAdicao){
				if(objetoCustoCcts.getFccCentroCustos().getCodigo().intValue() ==  codigoCentroCusto.intValue()){
					return objetoCustoCcts;
				}
			}else if(!objetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P)){
				if(objetoCustoCcts.getFccCentroCustos().getCodigo().intValue() ==  codigoCentroCusto.intValue()){
					return objetoCustoCcts;
				}
			}
		}
		return null;
	}


	public void adicionar() throws ApplicationBusinessException {
		//Se já existe um objetoCustoCcts com o mesmo centro de custo, então exibe uma mensagem de erro
		if(this.obterObjetoCustoCcts(this.getCentroCusto().getCodigo(), true) != null){
			this.apresentarMsgNegocio(Severity.ERROR, "CENTRO_CUSTO_ESTA_CADASTRADO");
		}
		else{
			SigObjetoCustoCcts objetoCustoCcts = new SigObjetoCustoCcts();
			objetoCustoCcts.setFccCentroCustos(this.getCentroCusto());//Centro de custo selecionado
			objetoCustoCcts.setIndTipo(DominioTipoObjetoCustoCcts.C);//Tipo colaborador
			objetoCustoCcts.setControleCCComplementar(true);
			this.getListCentroCustoOC().add(objetoCustoCcts);
			this.setCentroCusto(null);
		}
	}
	
	public void excluir(){
		if(this.getCodigoCentroCusto() != null){	
			SigObjetoCustoCcts objetoCustoCcts = this.obterObjetoCustoCcts(this.getCodigoCentroCusto(), false);
			if(objetoCustoCcts != null){
				if(!objetoCustoCcts.getControleCCComplementar()){
					this.getListCentroCustoOCExclusao().add(objetoCustoCcts);
				}
				this.getListCentroCustoOC().remove(objetoCustoCcts);
				this.setCodigoCentroCusto(null);
			}
		}
	}
	
	public void excluir(Integer codigoCentroCusto){
		if(codigoCentroCusto != null){	
			SigObjetoCustoCcts objetoCustoCcts = this.obterObjetoCustoCcts(codigoCentroCusto, false);
			if(objetoCustoCcts != null){
				if(!objetoCustoCcts.getControleCCComplementar()){
					this.getListCentroCustoOCExclusao().add(objetoCustoCcts);
				}
				this.getListCentroCustoOC().remove(objetoCustoCcts);
				this.setCodigoCentroCusto(null);
			}
		}
	}

	
	public boolean verificaSeHaAlteracao(){
		boolean retorno = false;
		if(this.getListCentroCustoOCExclusao() != null && !this.getListCentroCustoOCExclusao().isEmpty() || this.getListCentroCustoOCExclusao().size() != 0 ){
			retorno = true;
		}
		//validar itens adcionados
		if(this.getListCentroCustoOC() != null && this.getListCentroCustoOC().size() != this.manterObjetosCustoController.getListaObjetoCustoCcts().size()){
			retorno = true;
		}
		return retorno;
	}
	
	public String verificaAlteracaoNaoSalva(){
		if(verificaSeHaAlteracao()){
			return null;
		}
		//se não houver alterações chama o cancelar
		return this.cancelar();
	}

	public String cancelar(){
		return MANTER_OBJETOS_CUSTO;
	}
	
	
	//Efetuar o controle do incluir
	public String confirmarAlteracoes() throws ApplicationBusinessException{
		
		if(!verificaSeHaAlteracao()){
			this.apresentarMsgNegocio(Severity.ERROR, "NENHUM_CENTRO_CUSTO_ADICIONADO");
			return null;
		}else {
		
			this.manterObjetosCustoController.setListaObjetoCustoCcts(listCentroCustoOC);
			this.manterObjetosCustoController.setListaObjetoCustoCctsExclusao(listCentroCustoOCExclusao);

			this.manterObjetosCustoController.setEvitarExecucaoMetodoInicial(true);//Não executa o método inicial
			this.manterObjetosCustoController.setPossuiAlteracaoCampos(true);//Marca uma alteração não salva
			this.apresentarMsgNegocio(Severity.INFO, "CENTROS_CUSTO_GRAVADO_SUCESSO", this.manterObjetosCustoController.getObjetoCustoVersao().getSigObjetoCustos().getNome());

			return MANTER_OBJETOS_CUSTO;
		}
	}
	
	public boolean isVisualizar(){
		this.manterObjetosCustoController.setEvitarExecucaoMetodoInicial(true);//Não executa o método inicial
		return this.manterObjetosCustoController.isVisualizar();
	}
	
	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.manterObjetosCustoController.pesquisarCentroCusto(paramPesquisa);
	}
	
	//Getters and Setters
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public List<SigObjetoCustoCcts> getListCentroCustoOCExclusao() {
		return listCentroCustoOCExclusao;
	}

	public void setListCentroCustoOCExclusao(
			List<SigObjetoCustoCcts> listCentroCustoOCExclusao) {
		this.listCentroCustoOCExclusao = listCentroCustoOCExclusao;
	}

	public List<SigObjetoCustoCcts> getListCentroCustoOC() {
		return listCentroCustoOC;
	}

	public void setListCentroCustoOC(List<SigObjetoCustoCcts> listCentroCustoOC) {
		this.listCentroCustoOC = listCentroCustoOC;
	}
}