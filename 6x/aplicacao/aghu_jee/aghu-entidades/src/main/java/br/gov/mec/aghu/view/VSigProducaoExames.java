package br.gov.mec.aghu.view;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_SIG_PRODUCAO_EXAMES", schema="AGH")
@Immutable
public class VSigProducaoExames extends BaseEntityId<VSigProducaoExamesId> implements java.io.Serializable {

	private static final long serialVersionUID = -8919832235385944132L;
	
	private VSigProducaoExamesId id;
	private Integer nroDiasProducao;
	private Integer qtdeExames;
	private Date dataLiberado;
	
	
	@EmbeddedId
	@AttributeOverrides( {
		@AttributeOverride(name = "cctCodigo", column = @Column(name = "CCT_CODIGO")),
		@AttributeOverride(name = "phiSeq", column = @Column(name = "PHI_SEQ")),
		@AttributeOverride(name = "origem", column = @Column(name = "ORIGEM", length = 1)) })
	public VSigProducaoExamesId getId() {
		return id;
	}
	
	public void setId(VSigProducaoExamesId id) {
		this.id = id;
	}
	
	
	@Column(name="nro_dias_producao")
	public Integer getNroDiasProducao() {
		return nroDiasProducao;
	}
	
	public void setNroDiasProducao(Integer nroDiasProducao) {
		this.nroDiasProducao = nroDiasProducao;
	}
	
	@Column(name="qtde_exames")
	public Integer getQtdeExames() {
		return qtdeExames;
	}
	
	public void setQtdeExames(Integer qtdeExames) {
		this.qtdeExames = qtdeExames;
	}

	@Column(name="data_liberado")
	public Date getDataLiberado() {
		return dataLiberado;
	}

	public void setDataLiberado(Date dataLiberado) {
		this.dataLiberado = dataLiberado;
	}
	
	public enum Fields {
		CCT_CODIGO("id.cctCodigo"),
		PHI_SEQ("id.phiSeq"),
		ORIGEM("id.origem"),
		NRO_DIAS_PRODUCAO("nroDiasProducao"),
		QTDE_EXAMES("qtdeExames"),
		DATA_LIBERADO("dataLiberado");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		VSigProducaoExames other = (VSigProducaoExames) obj;
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
