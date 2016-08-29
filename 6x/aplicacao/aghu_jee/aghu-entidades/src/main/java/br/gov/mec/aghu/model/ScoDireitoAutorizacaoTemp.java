package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityNumero;

@Entity()
@Table(name = "SCO_DIREITOS_AUTORIZACOES_TEMP", schema="AGH")
@SequenceGenerator(name="scoDatSq1", sequenceName="SCO_DAT_SQ1", allocationSize = 1)
public class ScoDireitoAutorizacaoTemp extends BaseEntityNumero<Integer> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3160452320306299369L;
	private Integer numero;
	private Date dtInicio;
	private Date dtFim;
	private Integer version;
	private ScoPontoServidor scoPontoServidor;	
	private FccCentroCustos centroCusto;	
	private RapServidores servidor;	

	// construtores
	
	
	
	// getters & setters
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "scoDatSq1")
	@Column(name = "NUMERO"  , length= 5 , nullable = false  )	
	public Integer getNumero(){
		return this.numero;
	}
	
	public void setNumero(Integer numero){
		this.numero = numero;
	}

	@Column(name = "DT_INICIO"  , nullable = false  )	 
	public Date getDtInicio(){
		return this.dtInicio;
	}
	
	public void setDtInicio(Date dtInicio){
		this.dtInicio = dtInicio;
	}
		
	@Column(name = "DT_FIM"  , nullable = false  )	 
	public Date getDtFim(){
		return this.dtFim;
	}
	
	public void setDtFim(Date dtFim){
		this.dtFim = dtFim;
	}
		
	@Version
	@Column(name = "VERSION", nullable = false) 
	public Integer getVersion(){
		return this.version;
	}
	
	public void setVersion(Integer version){
		this.version = version;
	}
		

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "PSR_PPS_CODIGO", referencedColumnName = "PPS_CODIGO", nullable = true),
		@JoinColumn(name = "PSR_SER_MATRICULA", referencedColumnName = "SER_MATRICULA", nullable = true),
		@JoinColumn(name = "PSR_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO", nullable = true)})
	public ScoPontoServidor getScoPontoServidor() {
		return scoPontoServidor;
	}

	public void setScoPontoServidor(ScoPontoServidor scoPontoServidor) {
		this.scoPontoServidor = scoPontoServidor;
	}

	public ScoDireitoAutorizacaoTemp(){
	}

	public ScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp){
			
		this.dtInicio = scoDireitoAutorizacaoTemp.dtInicio;
		this.dtFim = scoDireitoAutorizacaoTemp.dtFim;
		this.version = scoDireitoAutorizacaoTemp.version;
		this.scoPontoServidor = scoDireitoAutorizacaoTemp.scoPontoServidor;
		this.centroCusto = scoDireitoAutorizacaoTemp.centroCusto;
		this.servidor = scoDireitoAutorizacaoTemp.servidor;
	}

	
	@ManyToOne
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCusto(){
		return centroCusto;
	}
	
	public void setCentroCusto(FccCentroCustos centroCusto ){
		this.centroCusto = centroCusto;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = true)})
	
	public RapServidores getServidor(){
		return servidor;
	}
	
	public void setServidor(RapServidores servidor ){
		this.servidor = servidor;
	}

	// outros

	public String toString() {
		return new ToStringBuilder(this).append("numero",this.numero).toString();}

	
		
	public boolean equals(Object other) {
		if (other instanceof ScoDireitoAutorizacaoTemp){
			ScoDireitoAutorizacaoTemp castOther = (ScoDireitoAutorizacaoTemp) other;
			return new EqualsBuilder()
				.append(this.numero, castOther.getNumero())
			.isEquals();
		}
		else {
			return false;
		}	
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.numero)
		.toHashCode();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public enum Fields {
			NUMERO("numero"), 
			DT_INICIO("dtInicio"), 
			DT_FIM("dtFim"), 
			VERSION("version"),
			SCO_PONTOS_SERVIDORES("scoPontoServidor"), 
			FCC_CENTRO_CUSTOS("centroCusto"), 
			RAP_SERVIDORES("servidor"), 
			MATRICULA("servidor.id.matricula"),
			VIN_CODIGO("servidor.id.vinCodigo"),
			PSR_PPS_CODIGO("scoPontoServidor.id.codigoPontoParadaSolicitacao"),
			PSR_SER_MATRICULA("scoPontoServidor.id.matricula"),
			PSR_SER_VIN_CODIGO("scoPontoServidor.id.vinCodigo");
			
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}	
	
}