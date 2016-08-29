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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "SCO_TIPO_CONTRATO_SICON", schema="AGH")
@SequenceGenerator(name = "scoTconSq1", sequenceName = "AGH.SCO_TCON_SQ1", allocationSize = 1)
public class ScoTipoContratoSicon extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9167225655775648831L;
	private Integer seq;
	private Integer codigoSicon;
	private String descricao;
	private Boolean indAditivo;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer version;
	private RapServidores servidor;	
	private Boolean indModalidade;
	private Boolean indInsereItens;

	// construtores
	
	public ScoTipoContratoSicon(){
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoTconSq1")
	public Integer getSeq(){
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}

	@Column(name = "CODIGO_SICON", length= 7)
	public Integer getCodigoSicon(){
		return this.codigoSicon;
	}
	
	public void setCodigoSicon(Integer codigoSicon){
		this.codigoSicon = codigoSicon;
	}
		
	@Column(name = "DESCRICAO", length= 80)
	public String getDescricao(){
		return this.descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
		
	@Column(name = "IND_ADITIVO", length= 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAditivo(){
		return this.indAditivo;
	}
	
	public void setIndAditivo(Boolean indAditivo){
		this.indAditivo = indAditivo;
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
		
	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion(){
		return this.version;
	}
	
	public void setVersion(Integer version){
		this.version = version;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor(){
		return servidor;
	}
	
	public void setServidor(RapServidores servidor ){
		this.servidor = servidor;
	}
	
	@Column(name = "IND_MODALIDADE", length= 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndModalidade(){
		return this.indModalidade;
	}

	public void setIndModalidade(Boolean indModalidade){
		this.indModalidade = indModalidade;
	}

	@Column(name = "IND_INS_ITEM", length= 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInsereItens(){
		return this.indInsereItens;
	}

	public void setIndInsereItens(Boolean indInsereItens){
		this.indInsereItens = indInsereItens;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("seq",this.seq)
		.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoSicon == null) ? 0 : codigoSicon.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoTipoContratoSicon other = (ScoTipoContratoSicon) obj;
		if (codigoSicon == null) {
			if (other.codigoSicon != null){
				return false;
			}
		} else if (!codigoSicon.equals(other.codigoSicon)){
			return false;
		}
		if (descricao == null) {
			if (other.descricao != null){
				return false;
			}
		} else if (!descricao.equals(other.descricao)){
			return false;
		}
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}



	public enum Fields {
			SEQ("seq"), 
			CODIGO_SICON("codigoSicon"), 
			DESCRICAO("descricao"), 
			IND_ADITIVO("indAditivo"), 
			SITUACAO("situacao"), 
			CRIADO_EM("criadoEm"), 
			ALTERADO_EM("alteradoEm"), 
			VERSION("version"),
			SERVIDOR("servidor"),
			IND_MODALIDADE("indModalidade"), 
			IND_INS_ITENS("indInsereItens")
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
	
	@Transient
	public String obtemCodigoSicon() {
		if (getDescricao().equalsIgnoreCase("CONTRATO")) {	
			return "50";
		} else if (getDescricao().equalsIgnoreCase("CREDENCIAMENTO")) {		
			return "51";
		} else if (getDescricao().equalsIgnoreCase("COMODATO")) {		
			return "52";
		} else if (getDescricao().equalsIgnoreCase("ARRENDAMETO")) {	
			return "53";
		} else if (getDescricao().equalsIgnoreCase("CONCESSÃO")) {	
			return "54";
		} else if (getDescricao().equalsIgnoreCase("TERMO ADITIVO")) {	
			return "55";
		} else if (getDescricao().equalsIgnoreCase("TERMO DE ADESÃO")) { 
			return "56";
		} 
		
		/**
		50 para contrato;
		51 para credenciamento;
		52 para comodato;
		53 para arrendamento;
		54 para concessão;
		55 para termo aditivo;
		56 para termo de adesão.
		*/
		
		return null;
	}
	
}