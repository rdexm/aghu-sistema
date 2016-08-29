package br.gov.mec.aghu.model;

// Generated 21/06/2010 19:33:41 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "V_AIN_DISP_VAGAS", schema = "AGH")
@Immutable
public class VAinDispVagas extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = 2426352060993127264L;

	private Short ufUnfSeq;

	private String ufAndar;
	
	private String ufAla;
	
	private String ufAndarAlaDescricao;
	
	private AghClinicas clinica;
	
	private Short vciCapacInternacao;

	private Long leitosIndisp;
	
	private Long totalInt;
	
	private Long leitosReservados;

	public enum Fields {

		UF_UNF_SEQ("ufUnfSeq"), CLINICA("clinica"),
		CLC_CODIGO("clinica.codigo"),
		VCI_CAPAC_INTERNACAO("vciCapacInternacao"),
		LEITOS_INDISP("leitosIndisp"),
		TOTAL_INT("totalInt"),
		LEITOS_RESERVADOS("leitosReservados");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	

	@Id
	@Column(name = "VUF_UNF_SEQ", precision = 4, scale = 0)
	public Short getUfUnfSeq() {
		return ufUnfSeq;
	}

	public void setUfUnfSeq(Short ufUnfSeq) {
		this.ufUnfSeq = ufUnfSeq;
	}

	@Column(name = "VUF_ANDAR", precision = 2)
	public String getUfAndar() {
		return ufAndar;
	}

	public void setUfAndar(String ufAndar) {
		this.ufAndar = ufAndar;
	}

	@Column(name = "VUF_ALA", length = 1)
	public String getUfAla() {
		return ufAla;
	}

	public void setUfAla(String ufAla) {
		this.ufAla = ufAla;
	}


	@Column(name = "VUF_ANDAR_ALA_DESCRICAO", length = 105)
	public String getUfAndarAlaDescricao() {
		return ufAndarAlaDescricao;
	}

	public void setUfAndarAlaDescricao(String ufAndarAlaDescricao) {
		this.ufAndarAlaDescricao = ufAndarAlaDescricao;
	}

	@Column(name = "VCI_CAPAC_INTERNACAO", precision = 3, scale = 0)
	public Short getVciCapacInternacao() {
		return vciCapacInternacao;
	}

	public void setVciCapacInternacao(Short vciCapacInternacao) {
		this.vciCapacInternacao = vciCapacInternacao;
	}

	@Column(name = "LEITOS_INDISP", precision = 11, scale = 0)
	public Long getLeitosIndisp() {
		return leitosIndisp;
	}

	public void setLeitosIndisp(Long leitosIndisp) {
		this.leitosIndisp = leitosIndisp;
	}

	@Column(name = "TOTAL_INT", precision = 11, scale = 0)
	public Long getTotalInt() {
		return totalInt;
	}

	public void setTotalInt(Long totalInt) {
		this.totalInt = totalInt;
	}

	
	@Column(name = "LEITOS_RESERVADOS", precision = 11, scale = 0)
	public Long getLeitosReservados() {
		return leitosReservados;
	}

	public void setLeitosReservados(Long leitosReservados) {
		this.leitosReservados = leitosReservados;
	}
	
	@Transient
	public Long getPdrvVciCapacInternacao(){
		final Long vVciCapacInternacao = getVciCapacInternacao() != null ? getVciCapacInternacao() : Long.valueOf("0");
		final Long vLeitosIndisp = getLeitosIndisp() != null ? getLeitosIndisp() : Long.valueOf("0");
		final Long vTotalInt = getTotalInt() != null ? getTotalInt() : Long.valueOf("0");
		
		return vVciCapacInternacao - vLeitosIndisp - vTotalInt;
	}
	
	@JoinColumn(name = "CLC_CODIGO", referencedColumnName = "CODIGO")
	@ManyToOne
	public AghClinicas getClinica() {
		return this.clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUfUnfSeq() == null) ? 0 : getUfUnfSeq().hashCode());
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
		if (!(obj instanceof VAinDispVagas)) {
			return false;
		}
		VAinDispVagas other = (VAinDispVagas) obj;
		if (getUfUnfSeq() == null) {
			if (other.getUfUnfSeq() != null) {
				return false;
			}
		} else if (!getUfUnfSeq().equals(other.getUfUnfSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Short getSeq(){ return this.getUfUnfSeq();} 
 public void setSeq(Short seq){ this.setUfUnfSeq(seq);}
}
