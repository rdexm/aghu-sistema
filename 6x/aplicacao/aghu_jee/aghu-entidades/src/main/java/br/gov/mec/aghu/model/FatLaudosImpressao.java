package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="fatLaudoSq1", sequenceName="AGH.FAT_LAUDO_SQ1", allocationSize = 1)
@Table(name = "FAT_LAUDOS_IMPRESSAO", schema = "AGH")
public class FatLaudosImpressao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 1741709489202421909L;
	
	private Integer seq;
	private AacConsultas consulta;
	private Integer pacCodigo;
	private DominioSituacao indSituacao;
	private String tipoSinalizacao;
	private Date dataImpressao;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatLaudoSq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer numero) {
		this.seq = numero;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CON_NUMERO", nullable = false)
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	@Column(name = "PAC_CODIGO", nullable = false)
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_IMPRESSAO")
	public Date getDataImpressao() {
		return dataImpressao;
	}

	public void setDataImpressao(Date dataImpressao) {
		this.dataImpressao = dataImpressao;
	}

	@Column(name = "TIPO_SINALIZACAO", length = 1)
	public String getTipoSinalizacao() {
		return tipoSinalizacao;
	}

	public void setTipoSinalizacao(String tipoSinalizacao) {
		this.tipoSinalizacao = tipoSinalizacao;
	}

	public enum Fields {
		SEQ("seq"), 
		CON_NUMERO("consulta.numero"),
		PAC_CODIGO("pacCodigo"),
		IND_SITUACAO("indSituacao"),
		TIPO_SINALIZACAO("tipoSinalizacao"),
		DTHR_IMPRESSAO("dataImpressao");

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
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (!super.equals(obj)){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		FatLaudosImpressao other = (FatLaudosImpressao) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}

}