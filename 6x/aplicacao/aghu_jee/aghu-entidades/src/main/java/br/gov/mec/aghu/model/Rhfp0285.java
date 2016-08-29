package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "RHFP0285", schema = "AGH")
public class Rhfp0285 extends BaseEntityId<Rhfp0285Id> implements java.io.Serializable {

	/**
	 * #42229 Incluido Starh Dependentes
	 */
	private Rhfp0285Id id;
	private static final long serialVersionUID = -2402084174193876728L;
	private Date dataFimDep;
	private Date dataInicioDep;
	private Long nroCarteira;
	private Integer idadeDe;
	private Integer codDependencia;
	private Integer idadeAte; 

	public Rhfp0285() {

	}
	public Rhfp0285(Rhfp0285Id id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "codContrato", column = @Column(name = "COD_CONTRATO", nullable = false)),
			@AttributeOverride(name = "codPlanoSaude", column = @Column(name = "COD_PLANO_SAUDE", nullable = false)),
			@AttributeOverride(name = "dataInicio", column = @Column(name = "DATA_INICIO", nullable = false)),
			@AttributeOverride(name = "codFunc", column = @Column(name = "COD_FUNC", nullable = false)),
			@AttributeOverride(name = "codPessoa", column = @Column(name = "COD_PESSOA", nullable = false))
			})
	
	public Rhfp0285Id getId() {
		return this.id;
	}
	public void setId(Rhfp0285Id id) {
		this.id = id;
	}

	public enum Fields {

		COD_CONTRATO("id.codContrato"), 
		COD_PLANO_SAUDE("id.codPlanoSaude"), 
		DATA_INICIO("id.dataInicio"),
		NRO_CARTEIRA("nroCarteira"),
		COD_PESSOA("id.codPessoa"),
		DATA_INICIO_DEP("dataInicioDep"),
		DATA_FIM_DEP("dataInicioDep");

		public String getFields() {
			return fields;
		}

		public void setFields(String fields) {
			this.fields = fields;
		}

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Column(name = "NUM_CARTEIRA")
	public Long getNroCarteira() {
		return nroCarteira;
	}

	public void setNroCarteira(Long nroCarteira) {
		this.nroCarteira = nroCarteira;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FIM_DEP", length = 7)
	public Date getDataFimDep() {
		return dataFimDep;
	}

	public void setDataFimDep(Date dataFimDep) {
		this.dataFimDep = dataFimDep;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO_DEP", length = 7)
	public Date getDataInicioDep() {
		return dataInicioDep;
	}

	public void setDataInicioDep(Date dataInicioDep) {
		this.dataInicioDep = dataInicioDep;
	}
	@Column(name = "IDADE_DE")
	public Integer getIdadeDe() {
		return idadeDe;
	}
	public void setIdadeDe(Integer idadeDe) {
		this.idadeDe = idadeDe;
	}
	@Column(name = "COD_DEPENDENCIA")
	public Integer getCodDependencia() {
		return codDependencia;
	}
	public void setCodDependencia(Integer codDependencia) {
		this.codDependencia = codDependencia;
	}
	@Column(name = "IDADE_ATE")
	public Integer getIdadeAte() {
		return idadeAte;
	}
	public void setIdadeAte(Integer idadeAte) {
		this.idadeAte = idadeAte;
	}

}
