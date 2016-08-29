package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pela visualização de itens de requisição de material
 * 
 * @author clayton.bras
 * 
 */

public class VisualizacaoItemRequisicaoMaterialController extends ActionController {

	
	private static final long serialVersionUID = 1160839550462535556L;
	
	private static final String PESQUISAR_ITENS_REQUISICAO = "pesquisarItensRequisicaoMaterial";
	private static final String PESQUISAR_GERAL_REQUISICAO = "consultarGeralRequisicaoMaterial";
	private static final String CONSULTAR_RM = "estoque-consultarRM";
	private static final String ESTORNAR_REQUISICAO_MATERIAL = "estoque-estornarRequisicaoMaterial";

	public enum EnumTargetVisualizarItemRequisicaoMaterial {
		NAO_EXISTEM_ITENS_REQUISICAO_MATERIAL;
	}

	private SceReqMaterial requisicao;
	private SceItemRms itemSelected;
	private List<SceItemRms> listaItens = null;
	private DominioSimNao estornada;
	private DominioSimNao automatica;
	private Integer seqRequisicaoMaterial = null;
	private String origem;
	private Boolean exibirItens = Boolean.FALSE;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.requisicao = estoqueFacade.obterRequisicaoMaterial(getSeqRequisicaoMaterial());
		if (requisicao != null) {
			this.setListaItens(new ArrayList<SceItemRms>(estoqueFacade.pesquisarItensRequisicaoMateriais(getSeqRequisicaoMaterial())));
		
			if(this.requisicao.getEstorno() != null){
				if(this.requisicao.getEstorno()){
					estornada = DominioSimNao.S;
				}else{
					estornada = DominioSimNao.N;
				}
				
				if(this.requisicao.getAutomatica() != null && this.requisicao.getAutomatica()){
					automatica = DominioSimNao.S;
				}else{
					automatica = DominioSimNao.N;
				}	
			}
		}
		if (listaItens != null && listaItens.size() != 0) {
			this.setExibirItens(Boolean.TRUE);

		} else {
			this.setExibirItens(Boolean.FALSE);
			apresentarMsgNegocio(Severity.INFO,EnumTargetVisualizarItemRequisicaoMaterial
					.NAO_EXISTEM_ITENS_REQUISICAO_MATERIAL.toString());
		}
	
	}

	public SceReqMaterial getRequisicao() {
		return requisicao;
	}

	public void setRequisicao(SceReqMaterial requisicao) {
		this.requisicao = requisicao;
	}

	public DominioSimNao getEstornada() {
		return estornada;
	}

	public void setEstornada(DominioSimNao estornada) {
		this.estornada = estornada;
	}

	public Integer getSeqRequisicaoMaterial() {
		return seqRequisicaoMaterial;
	}

	public void setSeqRequisicaoMaterial(Integer seqRequisicaoMaterial) {
		this.seqRequisicaoMaterial = seqRequisicaoMaterial;
	}

	public void setListaItens(List<SceItemRms> listaItens) {
		this.listaItens = listaItens;
	}

	public List<SceItemRms> getListaItens() {
		return listaItens;
	}

	public void setItemSelected(SceItemRms itemSelected) {
		this.itemSelected = itemSelected;
	}

	public SceItemRms getItemSelected() {
		return itemSelected;
	}

	public String getObsItemSelected() {
		if (this.getItemSelected() != null) {
			return this.getItemSelected().getEstoqueAlmoxarifado().getMaterial().getObservacao();
		}
		return null;
	}
	
	public String voltar(){
		if(PESQUISAR_ITENS_REQUISICAO.equals(getOrigem())){
			return PESQUISAR_ITENS_REQUISICAO;
		}else if(PESQUISAR_GERAL_REQUISICAO.equals(getOrigem())){
			return PESQUISAR_GERAL_REQUISICAO;
		}else if(CONSULTAR_RM.equals(getOrigem())){
			return CONSULTAR_RM;
		}
		else if(ESTORNAR_REQUISICAO_MATERIAL.equals(getOrigem())){
			return ESTORNAR_REQUISICAO_MATERIAL;
		}
		return null;
	}

	public void setExibirItens(Boolean exibirItens) {
		this.exibirItens = exibirItens;
	}

	public Boolean getExibirItens() {
		return exibirItens;
	}

	public void setOrigem(String origemPesquisaItens) {
		this.origem = origemPesquisaItens;
	}

	public String getOrigem() {
		return origem;
	}

	public String getInformacaoSituacaoRM(Date data, RapServidores servidor){
		StringBuffer str = new StringBuffer();
		if(data != null)
		{
			str.append(DateUtil.dataToString(data, "dd/MM/yyyy"));
			if(servidor != null && servidor.getPessoaFisica() != null && servidor.getPessoaFisica().getNome() != null){
				str.append(" - ").append(servidor.getPessoaFisica().getNome());
			}
		}else if(servidor != null && servidor.getPessoaFisica() != null && servidor.getPessoaFisica().getNome() != null){
			str.append(servidor.getPessoaFisica().getNome());
		}
		return str.toString(); 
	}
	
	public DominioSimNao[] getDominioSimNaoSelect(){
		return new DominioSimNao[]{DominioSimNao.S, DominioSimNao.N};
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	public DominioSimNao getAutomatica() {
		return automatica;
	}

	public void setAutomatica(DominioSimNao automatica) {
		this.automatica = automatica;
	}
}
