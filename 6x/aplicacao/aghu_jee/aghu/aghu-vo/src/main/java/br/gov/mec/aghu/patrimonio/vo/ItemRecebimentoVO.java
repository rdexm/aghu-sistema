package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;

public class ItemRecebimentoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6380798975171814074L;

	private Long irpSeq;	
	private Integer recebimento;
	private Integer itemRecebimento;
	private Integer eSL; //union
	private Integer aF;
	private Short complemento;
	private Integer nroSolicCompras;
	private Integer codigo;
	private String material;
	private Integer areaTecAvaliacao;
	private String nomeAreaTecAvaliacao;
	private Integer status;
	private Long notaFiscal;
	private String razaoSocial;
	private Long cnpj;
	private Long cpf;
	
	private String recebItem;
	private String afComplemento;
	private String cnpjCpfRazaoSocial;
	private String codigoMaterial;
	private DominioStatusAceiteTecnico statusFormatada;

	public enum Fields {
		
		IRPSEQ("irpSeq"),
		RECEBIMENTO("recebimento"),
		ITEM_RECEBIMENTO("itemRecebimento"),
		ESL("eSL"),
		AF("aF"),
		COMPLEMENTO("complemento"),
		NRO_SOLIC_COMPRAS("nroSolicCompras"),
		CODIGO("codigo"),
		MATERIAL("material"),
		AREA_TEC_AVALIACAO("areaTecAvaliacao"),
		NOME_AREA_TEC_AVALIACAO("nomeAreaTecAvaliacao"),
		STATUS("status"),
		NOTA_FISCAL("notaFiscal"),
		RAZAO_SOCIAL("razaoSocial"),
		CNPJ("cnpj"),
		CPF("cpf");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Long getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(Long irpSeq) {
		this.irpSeq = irpSeq;
	}

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

	public Integer geteSL() {
		return eSL;
	}

	public void seteSL(Integer eSL) {
		this.eSL = eSL;
	}

	public Integer getaF() {
		return aF;
	}

	public void setaF(Integer aF) {
		this.aF = aF;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNroSolicCompras() {
		return nroSolicCompras;
	}

	public void setNroSolicCompras(Integer nroSolicCompras) {
		this.nroSolicCompras = nroSolicCompras;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getAreaTecAvaliacao() {
		return areaTecAvaliacao;
	}

	public void setAreaTecAvaliacao(Integer areaTecAvaliacao) {
		this.areaTecAvaliacao = areaTecAvaliacao;
	}

	public String getNomeAreaTecAvaliacao() {
		return nomeAreaTecAvaliacao;
	}

	public void setNomeAreaTecAvaliacao(String nomeAreaTecAvaliacao) {
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRecebItem() {
		return recebItem;
	}

	public void setRecebItem(String recebItem) {
		this.recebItem = recebItem;
	}

	public String getAfComplemento() {
		return afComplemento;
	}

	public void setAfComplemento(String afComplemento) {
		this.afComplemento = afComplemento;
	}

	public String getCnpjCpfRazaoSocial() {
		return cnpjCpfRazaoSocial;
	}

	public void setCnpjCpfRazaoSocial(String cnpjCpfRazaoSocial) {
		this.cnpjCpfRazaoSocial = cnpjCpfRazaoSocial;
	}

	public String getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(String codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public DominioStatusAceiteTecnico getStatusFormatada() {
		return statusFormatada;
	}

	public void setStatusFormatada(DominioStatusAceiteTecnico statusFormatada) {
		this.statusFormatada = statusFormatada;
	}
}