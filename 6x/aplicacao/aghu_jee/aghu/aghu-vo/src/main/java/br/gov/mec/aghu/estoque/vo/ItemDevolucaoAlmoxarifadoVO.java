package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDasId;
import br.gov.mec.aghu.model.SceLoteDocumento;

public class ItemDevolucaoAlmoxarifadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1321000008680495391L;

	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer nroFornecedor;
	private String nomeFornecedor;
	private Integer quantidade;
	private String unidadeMedida;
	private Integer dalSeq;
	private Integer ealSeq;
	private List<SceDocumentoValidade> listaValidades = new ArrayList<SceDocumentoValidade>();
	private List<SceLoteDocumento> listaLoteDocumento = new ArrayList<SceLoteDocumento>();
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;
	private SceItemDasId id; // Deve estar setado somente quando o item DA for persistido
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getNroFornecedor() {
		return nroFornecedor;
	}
	public void setNroFornecedor(Integer nroFornecedor) {
		this.nroFornecedor = nroFornecedor;
	}
	public String getNomeFornecedor() {
		return nomeFornecedor;
	}
	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	public List<SceDocumentoValidade> getListaValidades() {
		return listaValidades;
	}
	public void setListaValidades(List<SceDocumentoValidade> listaValidades) {
		this.listaValidades = listaValidades;
	}
	public Integer getDalSeq() {
		return dalSeq;
	}
	public void setDalSeq(Integer dalSeq) {
		this.dalSeq = dalSeq;
	}
	public Integer getEalSeq() {
		return ealSeq;
	}
	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}
	public List<SceLoteDocumento> getListaLoteDocumento() {
		return listaLoteDocumento;
	}
	public void setListaLoteDocumento(List<SceLoteDocumento> listaLoteDocumento) {
		this.listaLoteDocumento = listaLoteDocumento;
	}
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}
	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}
	public SceItemDasId getId() {
		return id;
	}
	public void setId(SceItemDasId id) {
		this.id = id;
	}
	
}
