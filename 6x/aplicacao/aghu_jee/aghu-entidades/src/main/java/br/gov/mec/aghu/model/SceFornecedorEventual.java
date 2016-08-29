package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the sce_fornecedor_eventuais database table.
 * 
 */
@Entity
@SequenceGenerator(name="sceFevSq1", sequenceName="AGH.SCE_FEV_SQ1", allocationSize = 1)
@Table(name = "SCE_FORNECEDOR_EVENTUAIS")
public class SceFornecedorEventual extends BaseEntitySeq<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 4476687286759051909L;
private Integer seq;
	private String bairro;
	private Integer cep;
	private Integer ddd;
	private Integer ddi;
	private Timestamp dtCadastramento;
	private Integer fax;
	private Integer fone;
	//private String indSituacao;
	private String logradouro;
	private String nomeFantasia;
	private String nroLogradouro;
	private String razaoSocial;
	private Integer version;
	private Set<SceDocumentoFiscalEntrada> sceDocumentosFiscaisEntradas;
	private DominioSituacao indSituacao;

	public SceFornecedorEventual() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceFevSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getBairro() {
		return this.bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public Integer getCep() {
		return this.cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public Integer getDdd() {
		return this.ddd;
	}

	public void setDdd(Integer ddd) {
		this.ddd = ddd;
	}

	public Integer getDdi() {
		return this.ddi;
	}

	public void setDdi(Integer ddi) {
		this.ddi = ddi;
	}

	@Column(name = "DT_CADASTRAMENTO")
	public Timestamp getDtCadastramento() {
		return this.dtCadastramento;
	}

	public void setDtCadastramento(Timestamp dtCadastramento) {
		this.dtCadastramento = dtCadastramento;
	}

	public Integer getFax() {
		return this.fax;
	}

	public void setFax(Integer fax) {
		this.fax = fax;
	}

	public Integer getFone() {
		return this.fone;
	}

	public void setFone(Integer fone) {
		this.fone = fone;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getLogradouro() {
		return this.logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(name = "NOME_FANTASIA")
	public String getNomeFantasia() {
		return this.nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	@Column(name = "NRO_LOGRADOURO")
	public String getNroLogradouro() {
		return this.nroLogradouro;
	}

	public void setNroLogradouro(String nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	@Column(name = "RAZAO_SOCIAL")
	public String getRazaoSocial() {
		return this.razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// bi-directional many-to-one association to SceDocumentoFiscalEntrada
	@OneToMany(mappedBy = "fornecedorEventual")
	public Set<SceDocumentoFiscalEntrada> getSceDocumentosFiscaisEntradas() {
		return this.sceDocumentosFiscaisEntradas;
	}

	public void setSceDocumentosFiscaisEntradas(
			Set<SceDocumentoFiscalEntrada> sceDocumentosFiscaisEntradas) {
		this.sceDocumentosFiscaisEntradas = sceDocumentosFiscaisEntradas;
	}

	/**
	 * Obtem razão social e nomeFantasia como uma unica string
	 * 
	 * @return
	 */
	@Transient
	public String getRazaoSocialNomeFantasia() {
		return this.razaoSocial + " " + this.nomeFantasia;
	}

	public enum Fields {

		SEQ("seq"), RAZAO_SOCIAL("razaoSocial"), SITUACAO("indSituacao"), FONE(
				"fone"), NOME_FANTASIA("nomeFantasia");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SceFornecedorEventual)) {
			return false;
		}
		SceFornecedorEventual other = (SceFornecedorEventual) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}