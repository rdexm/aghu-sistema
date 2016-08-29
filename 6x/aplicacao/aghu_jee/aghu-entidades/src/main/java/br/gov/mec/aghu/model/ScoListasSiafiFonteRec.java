package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;



/**
 * The persistent class for the sco_listas_siafi_fonte_rec database table.
 * 
 */
@Entity
@Table(name="SCO_LISTAS_SIAFI_FONTE_REC")
public class ScoListasSiafiFonteRec extends BaseEntityId<ScoListasSiafiFonteRecId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -1709821608942165369L;
private ScoListasSiafiFonteRecId id;
	private Integer anoEmpenho;
	private Date dtFinalEmpenho;
	private Boolean empSaldoAf;
	private DominioSituacao situacao;
	private Integer seqLista;
	private Integer ultNumeroLista;
	private Integer ultNumeroListaSiafi;
	private Integer version;
	
	public enum Fields{
		DT_INICIAL_EMPENHO("id.dtInicialEmpenho"),
		DT_FINAL_EMPENHO("dtFinalEmpenho"),
		ANO("anoEmpenho"),
		FRF_CODIGO("id.frfCodigo"),
		SITUACAO("situacao");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

    public ScoListasSiafiFonteRec() {
    }


	@EmbeddedId
	public ScoListasSiafiFonteRecId getId() {
		return this.id;
	}

	public void setId(ScoListasSiafiFonteRecId id) {
		this.id = id;
	}
	

	@Column(name="ANO_EMPENHO")
	public Integer getAnoEmpenho() {
		return this.anoEmpenho;
	}

	public void setAnoEmpenho(Integer anoEmpenho) {
		this.anoEmpenho = anoEmpenho;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FINAL_EMPENHO")
	public Date getDtFinalEmpenho() {
		return this.dtFinalEmpenho;
	}

	public void setDtFinalEmpenho(Date dtFinalEmpenho) {
		this.dtFinalEmpenho = dtFinalEmpenho;
	}


	@Column(name="IND_EMP_SALDO_AF")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEmpSaldoAf() {
		return this.empSaldoAf;	
	}

	public void setEmpSaldoAf(Boolean empSaldoAf) {
		this.empSaldoAf = empSaldoAf;
	}


	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}


	@Column(name="SEQ_LISTA")
	public Integer getSeqLista() {
		return this.seqLista;
	}

	public void setSeqLista(Integer seqLista) {
		this.seqLista = seqLista;
	}


	@Column(name="ULT_NUMERO_LISTA")
	public Integer getUltNumeroLista() {
		return this.ultNumeroLista;
	}

	public void setUltNumeroLista(Integer ultNumeroLista) {
		this.ultNumeroLista = ultNumeroLista;
	}


	@Column(name="ULT_NUMERO_LISTA_SIAFI")
	public Integer getUltNumeroListaSiafi() {
		return this.ultNumeroListaSiafi;
	}

	public void setUltNumeroListaSiafi(Integer ultNumeroListaSiafi) {
		this.ultNumeroListaSiafi = ultNumeroListaSiafi;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoListasSiafiFonteRec)) {
			return false;
		}
		ScoListasSiafiFonteRec other = (ScoListasSiafiFonteRec) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}