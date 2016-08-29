package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSimNao;

public class ProgramacaoEntregaItemAFVO {
	private Integer nroAF;
	private Short cp;
	private Integer numero;
	private Short item;
	private String sit;
	private Integer grupo;
	private String material;
	private Boolean est;
	//private DominioSimNao estDominio;
	private String curva;
	private Integer tempRepOriginal;
	private Integer pPedidoOriginal;
	private Boolean ppCalOriginal;
	private Boolean contrValOriginal;
	private Integer tempRep;
	private Integer pPedido;
	private Boolean ppCal;
	private Boolean contrVal;
	private Integer qDisp;
	private Integer qEst;
	private Integer qBloq;
	//private Integer durEst;
	private Integer qAF;
	private Integer qRecebida;
	private Short prazoEntg;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String descricaoGrupo;
	private DominioClassifABC classificacaoABC;
	private DominioClassifABC subClassificacaoABC;
	private String corFundoLinha;
	private String corFundoPPedido;
	
	public Integer getNroAF() {
		return nroAF;
	}
	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}
	public Short getCp() {
		return cp;
	}
	public void setCp(Short cp) {
		this.cp = cp;
	}
	public Short getItem() {
		return item;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public String getSit() {
		return sit;
	}
	public void setSit(String sit) {
		this.sit = sit;
	}
	public Integer getGrupo() {
		return grupo;
	}
	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}
	public String getMaterial() {
		if(codigoMaterial!=null && descricaoMaterial!=null){
			material = codigoMaterial +" - "+ descricaoMaterial;
		}
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public Boolean getEst() {
		return est;
	}
	public void setEst(Boolean est) {
		this.est = est;
	}
	public String getCurva() {
		if(classificacaoABC != null && subClassificacaoABC != null) {
			curva = classificacaoABC.toString().concat(subClassificacaoABC.toString());
		}
		return curva;
	}
	public void setCurva(String curva) {
		this.curva = curva;
	}
	public Integer getTempRep() {
		return tempRep;
	}
	public void setTempRep(Integer tempRep) {
		this.tempRep = tempRep;
	}
	public Integer getpPedido() {
		return pPedido;
	}
	public void setpPedido(Integer pPedido) {
		this.pPedido = pPedido;
	}
	public Boolean getPpCal() {
		return ppCal;
	}
	public void setPpCal(Boolean ppCal) {
		this.ppCal = ppCal;
	}
	public Boolean getContrVal() {
		return contrVal;
	}
	public void setContrVal(Boolean contrVal) {
		this.contrVal = contrVal;
	}
	
	public Integer getqDisp() {
		return qDisp;
	}
	public void setqDisp(Integer qDisp) {
		this.qDisp = qDisp;
	}
	public Integer getqEst() {
		return qEst;
	}
	public void setqEst(Integer qEst) {
		this.qEst = qEst;
	}
	public Integer getDurEst() {
		if(pPedido > 0) {
			return (qDisp + qBloq * tempRep)/pPedido;
		} else {
			return 0;
		}
	}
	/*public void setDurEst(Integer durEst) {
		this.durEst = durEst;
	}*/
	public Integer getqAF() {
		if (qRecebida != null) {
			qAF = qAF-qRecebida;
		}
		return qAF < 0 ? 0 : qAF;
	}
	public void setqAF(Integer qAF) {
		this.qAF = qAF;
	}
	public Short getPrazoEntg() {
		return prazoEntg;
	}
	public void setPrazoEntg(Short prazoEntg) {
		this.prazoEntg = prazoEntg;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Integer getqRecebida() {
		return qRecebida;
	}
	public void setqRecebida(Integer qRecebida) {
		this.qRecebida = qRecebida;
	}
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
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public Integer getqBloq() {
		return qBloq;
	}
	public void setqBloq(Integer qBloq) {
		this.qBloq = qBloq;
	}
	public DominioClassifABC getClassificacaoABC() {
		return classificacaoABC;
	}
	public void setClassificacaoABC(DominioClassifABC classificacaoABC) {
		this.classificacaoABC = classificacaoABC;
	}
	public DominioClassifABC getSubClassificacaoABC() {
		return subClassificacaoABC;
	}
	public void setSubClassificacaoABC(DominioClassifABC subClassificacaoABC) {
		this.subClassificacaoABC = subClassificacaoABC;
	}
	public DominioSimNao getEstDominio() {
		return DominioSimNao.getInstance(est);
	}
	/*public void setEstDominio(DominioSimNao estDominio) {
		this.estDominio = estDominio;
	}*/
	public Integer getTempRepOriginal() {
		return tempRepOriginal;
	}
	public void setTempRepOriginal(Integer tempRepOriginal) {
		this.tempRepOriginal = tempRepOriginal;
	}
	public Integer getpPedidoOriginal() {
		return pPedidoOriginal;
	}
	public void setpPedidoOriginal(Integer pPedidoOriginal) {
		this.pPedidoOriginal = pPedidoOriginal;
	}
	public Boolean getPpCalOriginal() {
		return ppCalOriginal;
	}
	public void setPpCalOriginal(Boolean ppCalOriginal) {
		this.ppCalOriginal = ppCalOriginal;
	}
	public Boolean getContrValOriginal() {
		return contrValOriginal;
	}
	public void setContrValOriginal(Boolean contrValOriginal) {
		this.contrValOriginal = contrValOriginal;
	}
	public String getCorFundoLinha() {
		return corFundoLinha;
	}
	public void setCorFundoLinha(String corFundoLinha) {
		this.corFundoLinha = corFundoLinha;
	}
	public String getCorFundoPPedido() {
		return corFundoPPedido;
	}
	public void setCorFundoPPedido(String corFundoPPedido) {
		this.corFundoPPedido = corFundoPPedido;
	}
	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}
	public String getDescricaoGrupoHint() {
		if(grupo!=null){
			descricaoGrupo = grupo +" - "+descricaoGrupo;
		}
		return descricaoGrupo;
	}
	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}
	
}
