package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.List;

public class RelSumarizadoNotifTecRecebProvVO implements Serializable {
	
	private static final long serialVersionUID = 3750629534350451192L;
	
	private Integer recebimento;	
	private Integer itemRecebimento;
	private Integer esl;
	private Integer af;
	private Short complemento;
	private Integer quantidade;
	private Integer quantidadeRet;
	private String razaoSocial;
	private String cpfCnpj;
	private Long notaFiscal;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String areaTenicacAvaliacao;
	private String centroCusto;
	private Integer nroCC;
	private String recebimentoItemFormatado;
	private String afComplementoFormatado;
	private String codigoNomeMaterialFormatado;
	private List<RelatorioRecebimentoProvisorioVO> listaNotificacoesTecnicas;
	
	public Integer getRecebimento() {
		return recebimento;
	}
	public void setRecebimento(Integer recebimento) {
		this.recebimento = recebimento;
	}
	public Integer getItemRecebimento() {
		return itemRecebimento;
	}
	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}
	public Integer getEsl() {
		return esl;
	}
	public void setEsl(Integer esl) {
		this.esl = esl;
	}
	public Integer getAf() {
		return af;
	}
	public void setAf(Integer af) {
		this.af = af;
	}
	public Short getComplemento() {
		return complemento;
	}
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getQuantidadeRet() {
		return quantidadeRet;
	}
	public void setQuantidadeRet(Integer quantidadeRet) {
		this.quantidadeRet = quantidadeRet;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public Long getNotaFiscal() {
		return notaFiscal;
	}
	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
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
	public String getAreaTenicacAvaliacao() {
		return areaTenicacAvaliacao;
	}
	public void setAreaTenicacAvaliacao(String areaTenicacAvaliacao) {
		this.areaTenicacAvaliacao = areaTenicacAvaliacao;
	}
	public String getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(String centroCusto) {
		this.centroCusto = centroCusto;
	}
	public Integer getNroCC() {
		return nroCC;
	}
	public void setNroCC(Integer nroCC) {
		this.nroCC = nroCC;
	}	
	public String getRecebimentoItemFormatado() {
		return recebimentoItemFormatado;
	}
	public void setRecebimentoItemFormatado(String recebimentoItemFormatado) {
		this.recebimentoItemFormatado = recebimentoItemFormatado;
	}	
	public String getAfComplementoFormatado() {
		return afComplementoFormatado;
	}
	public void setAfComplementoFormatado(String afComplementoFormatado) {
		this.afComplementoFormatado = afComplementoFormatado;
	}	
	public String getCodigoNomeMaterialFormatado() {
		return codigoNomeMaterialFormatado;
	}
	public void setCodigoNomeMaterialFormatado(String codigoNomeMaterialFormatado) {
		this.codigoNomeMaterialFormatado = codigoNomeMaterialFormatado;
	}
	public List<RelatorioRecebimentoProvisorioVO> getListaNotificacoesTecnicas() {
		return listaNotificacoesTecnicas;
	}
	public void setListaNotificacoesTecnicas(
			List<RelatorioRecebimentoProvisorioVO> listaNotificacoesTecnicas) {
		this.listaNotificacoesTecnicas = listaNotificacoesTecnicas;
	}

}
