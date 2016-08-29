package br.gov.mec.aghu.model;
import java.io.Serializable;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "SCO_CRIT_REAJ_CONTRATOS", schema="AGH")
@SequenceGenerator(name = "scoRconSq1", sequenceName = "AGH.SCO_RCON_SQ1", allocationSize = 1)
public class ScoCriterioReajusteContrato extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6022998639861802388L;
	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Date alteradoEm;
	private RapServidores servidor;		
	private Integer version;
	
	// construtores
	
	public ScoCriterioReajusteContrato(){
	}


	// getters & setters
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoRconSq1")
	public Integer getSeq(){
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", length= 80, nullable = false)
	public String getDescricao(){
		return this.descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
		
	@Column(name = "IND_SITUACAO", length= 1)	 
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao(){
		return this.situacao;
	}
	
	public void setSituacao(DominioSituacao situacao){
		this.situacao = situacao;
	}
		
	@Column(name = "CRIADO_EM")	 
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm(){
		return this.criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm){
		this.criadoEm = criadoEm;
	}
		
	@Column(name = "ALTERADO_EM")	 
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getAlteradoEm(){
		return this.alteradoEm;
	}
	
	public void setAlteradoEm(Date alteradoEm){
		this.alteradoEm = alteradoEm;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor(){
		return servidor;
	}
	
	public void setServidor(RapServidores servidor ){
		this.servidor = servidor;
	}
	
	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("seq",this.seq)
		.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoCriterioReajusteContrato)){
			return false;
		}
		ScoCriterioReajusteContrato castOther = (ScoCriterioReajusteContrato) other;
		return new EqualsBuilder()
			.append(this.seq, castOther.getSeq())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.seq)
		.toHashCode();
	}

	public enum Fields {
			SEQ("seq"), 
			DESCRICAO("descricao"), 
			SITUACAO("situacao"), 
			CRIADO_EM("criadoEm"), 
			ALTERADO_EM("alteradoEm"), 
			SERVIDOR("servidor")			
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}	
	
}