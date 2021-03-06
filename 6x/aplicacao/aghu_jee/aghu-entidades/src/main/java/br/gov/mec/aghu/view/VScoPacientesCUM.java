package br.gov.mec.aghu.view;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * VScoPacientesCUM generated by hbm2java
 */
@Entity
@Table(name = "V_SCO_PACIENTES_CUM", schema = "AGH")
@Immutable
public class VScoPacientesCUM extends BaseEntityId<VScoPacientesCUMId> implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320789889866856591L;
	
	private VScoPacientesCUMId id;
	private String  nomePaciente;
	private String  convenio;
	private String    dtUtilizacao;
	private Integer matCodigo;
	private String  material;
	private Integer quantidade;
	private String  lote;
	private String  tamanho;
	private String  serie;
	private Integer numeroAf;
	private Integer numeroComplemento;
	private String  cnpj;
	private String  fornecedor;
	private Long    nroNfSaidaSapiens;
	private Integer afeAfnNumero;
	private Integer afeNumero;
	

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "RMP_SEQ", column = @Column(name = "RMP_SEQ", nullable = false, length = 9)),
			@AttributeOverride(name = "NUMERO", column = @Column(name = "NUMERO", nullable = false, length = 3)) })
	public VScoPacientesCUMId getId() {
		return this.id;
	}

	public void setId(VScoPacientesCUMId id) {
		this.id = id;
	}
	
		
	
	@Column(name = "NOME_PACIENTE", length = 30)
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Column(name = "CONVENIO", length = 15)
	public String getConvenio() {
		return convenio;
	}
	
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_UTILIZACAO")	*/
	@Column(name = "DT_UTILIZACAO", length = 8)
	public String getDtUtilizacao() {
		return dtUtilizacao;
	}

	public void setDtUtilizacao(String dtUtilizacao) {
		this.dtUtilizacao = dtUtilizacao;
	}

	@Column(name = "MAT_CODIGO",precision = 6, scale = 0)	
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Column(name = "MATERIAL", length = 35)
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	@Column(name = "QTDE",precision = 7, scale = 0)
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	
	@Column(name = "LOTE", length = 20)
	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	@Column(name = "TAMANHO", length = 20)
	public String getTamanho() {
		return tamanho;
	}

	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}

	@Column(name = "SERIE", length = 20)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(name = "NRO_AF",precision = 7, scale = 0)
	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	@Column(name = "NRO_COMPLEMENTO",precision = 3, scale = 0)
	public Integer getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Integer numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	@Column(name = "CNPJ", length = 80)
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	@Column(name = "FORNECEDOR", length = 60)
	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Column(name = "NRO_NF_SAIDA_SAPIENS", precision = 14, scale = 0)
	public Long getNroNfSaidaSapiens() {
		return nroNfSaidaSapiens;
	}

	public void setNroNfSaidaSapiens(Long nroNfSaidaSapiens) {
		this.nroNfSaidaSapiens = nroNfSaidaSapiens;
	}

	@Column(name = "AFE_AFN_NUMERO",precision = 7, scale = 0)
	public Integer getAfeAfnNumero() {
		return afeAfnNumero;
	}

	public void setAfeAfnNumero(Integer afeAfnNumero) {
		this.afeAfnNumero = afeAfnNumero;
	}

	@Column(name = "AFE_NUMERO",precision = 7, scale = 0)
	public Integer getAfeNumero() {
		return afeNumero;
	}

	public void setAfeNumero(Integer afeNumero) {
		this.afeNumero = afeNumero;
	}

	public enum Fields {
		RMP_SEQ ("id.rmpSeq"),
		NOME_PACIENTE("nomePaciente"),
		CONVENIO("convenio"),
		DT_UTILIZACAO("dtUtilizacao"),
		MAT_CODIGO("matCodigo"),
		MATERIAL("material"),
		QTDE("quantidade"),
		NUMERO("id.numero"),
		LOTE("lote"),
		TAMANHO("tamanho"),
		SERIE("serie"),
		NRO_AF("numeroAf"),
		NRO_COMPLEMENTO("numeroComplemento"),
		CNPJ("cnpj"),
		FORNECEDOR("fornecedor"),
		NRO_NF_SAIDA_SAPIENS("nroNfSaidaSapiens"),
		AFE_AFN_NUMERO("afeAfnNumero"),
		AFE_NUMERO("afeNumero");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof VScoPacientesCUM)) {
			return false;
		}
		VScoPacientesCUM other = (VScoPacientesCUM) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}


}