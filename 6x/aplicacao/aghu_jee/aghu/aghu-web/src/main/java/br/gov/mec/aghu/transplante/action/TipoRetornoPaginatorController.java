package br.gov.mec.aghu.transplante.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;

public class TipoRetornoPaginatorController extends ActionController {
	
	private static final long serialVersionUID = -3383285917265871203L;
	
	private static final String TIPO_RETORNO_CRUD = "transplante-tipoRetornoCRUD";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	private MtxTipoRetorno mtxTipoRetornoSelect;
	private MtxTipoRetorno mtxTipoRetorno = new MtxTipoRetorno();
	private List<MtxTipoRetorno> listaMtxTipoRetorno;
	private boolean exibirGridPesquisa;
	private DominioSimNao dominioSimNao;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		if(exibirGridPesquisa){//Se a grid está na tela, ele está vindo da tela de adicionar/editar um tipo de retorno, então atualiza a lista
			pesquisar();
		}
	}
	
	public void pesquisar(){
		exibirGridPesquisa = true;
		mtxTipoRetorno.setIndSituacao(dominioSimNao != null ? DominioSituacao.getInstance(dominioSimNao.isSim()) : null);
		listaMtxTipoRetorno = transplanteFacade.pesquisarTipoRetorno(mtxTipoRetorno);
	}
	
	public String gravarAtualizar(){
		return TIPO_RETORNO_CRUD;
	}
	
	public String truncarDescricao(String descricao, int tamMax) {
		if(descricao.length() > tamMax){
			return StringUtils.abbreviate(descricao, tamMax); 
		}
		return descricao;
	}
	
	public void limpar(){
		mtxTipoRetorno = new MtxTipoRetorno();
		exibirGridPesquisa = false;
		listaMtxTipoRetorno.clear();
		dominioSimNao = null;
	}
	
	public MtxTipoRetorno getMtxTipoRetornoSelect() {
		return mtxTipoRetornoSelect;
	}

	public void setMtxTipoRetornoSelect(MtxTipoRetorno mtxTipoRetornoSelect) {
		this.mtxTipoRetornoSelect = mtxTipoRetornoSelect;
	}

	public MtxTipoRetorno getMtxTipoRetorno() {
		return mtxTipoRetorno;
	}

	public void setMtxTipoRetorno(MtxTipoRetorno mtxTipoRetorno) {
		this.mtxTipoRetorno = mtxTipoRetorno;
	}

	public List<MtxTipoRetorno> getListaMtxTipoRetorno() {
		return listaMtxTipoRetorno;
	}

	public void setListaMtxTipoRetorno(List<MtxTipoRetorno> listaMtxTipoRetorno) {
		this.listaMtxTipoRetorno = listaMtxTipoRetorno;
	}

	public boolean isExibirGridPesquisa() {
		return exibirGridPesquisa;
	}
	public void setExibirGridPesquisa(boolean exibirGridPesquisa) {
		this.exibirGridPesquisa = exibirGridPesquisa;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}
	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}
}
