package br.gov.mec.aghu.model;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "RAP_HIST_CCUSTO_DESEMPENHO", schema = "AGH")
public class RapHistCCustoDesempenho extends BaseEntityId<RapHistCCustoDesempenhoId> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6875089932308161532L;

	private RapHistCCustoDesempenhoId id;

	private Date dtFim;
	private Date criadoEm;
	private Date alteradoEm;
	private String observacao;

	private RapServidores servidorCriado;
	private RapServidores servidorAlterado;

	public RapHistCCustoDesempenho(){		
	}
	
	public RapHistCCustoDesempenho(RapHistCCustoDesempenhoId id){
		this.id = id;
	}
	
	public RapHistCCustoDesempenho(
			RapHistCCustoDesempenhoId id,
			Date dtFim,	Date criadoEm, Date alteradoEm, String observacao,
			RapServidores servidorCriado,
			RapServidores servidorAlterado) {
		
		this.id = id;
		this.dtFim = dtFim;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.observacao = observacao;		
		this.servidorCriado = servidorCriado;
		this.servidorAlterado = servidorAlterado;
	}

	@EmbeddedId()
	@AttributeOverrides({
		@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, length = 7)),
		@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)),
		@AttributeOverride(name = "cctCodigo", column = @Column(name = "CCT_CODIGO", nullable = false, length = 6)),
		@AttributeOverride(name = "dtInicio", column = @Column(name = "DT_INICIO", nullable = false, length = 7))
	})
	public RapHistCCustoDesempenhoId getId(){
		return this.id;
	}
	
	public void setId(RapHistCCustoDesempenhoId id){
		this.id = id;
	}

	@Column(name = "DT_FIM")	 
	public Date getDtFim(){
		return this.dtFim;
	}
	
	public void setDtFim(Date dtFim){
		this.dtFim = dtFim;
	}
		
	@Column(name = "CRIADO_EM", nullable = false)	 
	public Date getCriadoEm(){
		return this.criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm){
		this.criadoEm = criadoEm;
	}
		
	@Column(name = "ALTERADO_EM")	 
	public Date getAlteradoEm(){
		return this.alteradoEm;
	}
	
	public void setAlteradoEm(Date alteradoEm){
		this.alteradoEm = alteradoEm;
	}
		
	@Column(name = "OBSERVACAO", length = 60)	 
	public String getObservacao(){
		return this.observacao;
	}
	
	public void setObservacao(String observacao){
		this.observacao = observacao;
	}
	
	/*
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores rapServidores) {
		this.servidor = rapServidores;
	}
	*/
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_CRIADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCriado(){
		return servidorCriado;
	}
	
	public void setServidorCriado(RapServidores servidor ){
		this.servidorCriado = servidor;
	}
	
	/*
	@ManyToOne
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCusto(){
		return centroCusto;
	}
	
	public void setCentroCusto(FccCentroCustos centroCusto ){
		this.centroCusto = centroCusto;
	}
	*/
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado(){
		return servidorAlterado;
	}
	
	public void setServidorAlterado(RapServidores servidor ){
		this.servidorAlterado = servidor;
	}		

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("rapHistCCustoDesempenhoId",this.id)
		.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapHistCCustoDesempenho)) {
			return false;
		}
		RapHistCCustoDesempenho castOther = (RapHistCCustoDesempenho) other;
		return new EqualsBuilder()
			.append(this.id, castOther.getId())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
		.toHashCode();
	}

	public enum Fields {
			DT_FIM("dtFim"), 
			CRIADO_EM("criadoEm"), 
			ALTERADO_EM("alteradoEm"), 
			OBSERVACAO("observacao"), 
			RAP_SERVIDOR_CRIADO("servidorCriado"), 
			RAP_SERVIDOR_ALTERADO("servidorAlterado"),
			CODIGO_SERVIDOR("id.serVinCodigo"),
			MATRICULA_SERVIDOR("id.serMatricula"),
			CODIGO_CENTRO_CUSTO("id.cctCodigo"),
			DT_INICIO("id.dtInicio");
		
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