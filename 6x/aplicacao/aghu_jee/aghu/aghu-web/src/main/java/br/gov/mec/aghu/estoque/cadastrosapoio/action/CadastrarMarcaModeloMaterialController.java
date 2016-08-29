package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.vo.MarcaModeloMaterialVO;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastrarMarcaModeloMaterialController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3361353126606100916L;

	@EJB
	protected IComprasFacade comprasFacade;
	
	private ScoMaterial material;
	private List<MarcaModeloMaterialVO> listaMarcaModelo;
	private MarcaModeloMaterialVO itemExclusao;
	
	private ScoMarcaModelo marcaModelo;
	private String descricaoModelo;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisarMarcaModeloMaterial(){
		
		try {
			
			Integer codigo = null;
			Integer seqp = null;
			
			if(this.marcaModelo != null){
				codigo = this.marcaModelo.getId().getMcmCodigo();
				seqp = this.marcaModelo.getId().getSeqp();
			}
		
			this.listaMarcaModelo = this.comprasFacade.pesquisarMarcaModeloMaterial(material.getCodigo(), codigo, seqp);
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar(){
		this.setMaterial(null);
		this.setMarcaModelo(null);
		this.setDescricaoModelo(null);
		this.setListaMarcaModelo(null);
	}
	
	public List<ScoMaterial> pesquisarListaMateriais(String material){
		return  this.comprasFacade.listarScoMateriais(material, null, true);
	}
	
	public List<ScoMarcaModelo> pesquisarMarcaModelo(String marcaModelo){
		return  this.comprasFacade.pesquisarMacaModelo(marcaModelo);
	}
	
	public void verificarModelo(){
		if(this.marcaModelo != null){
			Integer seqp = this.marcaModelo.getId().getSeqp();
			String descricao = this.marcaModelo.getDescricao();
			this.setDescricaoModelo(seqp.toString().concat(" - ").concat(descricao));
		} else {
			this.setDescricaoModelo(null);
		}
	}
	
	public void adicionarMarcaModelo(){
		try {
			this.comprasFacade.adicionarMarcaModelo(this.marcaModelo, this.material);
			this.setMarcaModelo(null);
			this.setDescricaoModelo(null);
			this.pesquisarMarcaModeloMaterial();
			this.apresentarMsgNegocio(Severity.INFO, "MSG_M6_MARCA_MODELO");
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir(){
		this.comprasFacade.excluirMarcaModelo(this.itemExclusao, this.material);
		this.pesquisarMarcaModeloMaterial();
		this.apresentarMsgNegocio(Severity.INFO, "MSG_M5_MARCA_MODELO");
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public List<MarcaModeloMaterialVO>  getListaMarcaModelo() {
		return listaMarcaModelo;
	}

	public void setListaMarcaModelo(List<MarcaModeloMaterialVO>  listaMarcaModelo) {
		this.listaMarcaModelo = listaMarcaModelo;
	}

	public MarcaModeloMaterialVO getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MarcaModeloMaterialVO itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public ScoMarcaModelo getMarcaModelo() {
		return marcaModelo;
	}

	public void setMarcaModelo(ScoMarcaModelo marcaModelo) {
		this.marcaModelo = marcaModelo;
	}

	public String getDescricaoModelo() {
		return descricaoModelo;
	}

	public void setDescricaoModelo(String descricaoModelo) {
		this.descricaoModelo = descricaoModelo;
	}
	
}
