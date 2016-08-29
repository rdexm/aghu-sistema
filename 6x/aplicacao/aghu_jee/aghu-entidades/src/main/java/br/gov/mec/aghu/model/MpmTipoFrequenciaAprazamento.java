package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mpmTfqSq1", sequenceName="AGH.MPM_TFQ_SQ1", allocationSize = 1)
@Table(name = "MPM_TIPO_FREQ_APRAZAMENTOS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SIGLA"))
public class MpmTipoFrequenciaAprazamento extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5621711750922188673L;

	private enum MpmTipoFrequenciaAprazamentoExceptionCode implements
	BusinessExceptionCode {
		FATOR_CONVERSAO_HORAS_NEGATIVO, APRAZAMENTO_CONTINUO_INCONSISTENTE, FREQUENCIA_SINTAXE_INCONSISTENTE, FREQUENCIA_SINTAXE_INCONSISTENTE_INFORMAR_SINTAXE, 
	}

	private static final String SINTAXE_MARCADOR = "#";

	private Short seq;
	private RapServidores servidor;
	private String sigla;
	private String descricao;
	private BigDecimal fatorConversaoHoras;
	private Date criadoEm;
	private String sintaxe;
	private DominioFormaCalculoAprazamento indFormaAprazamento;
	private Boolean indDigitaFrequencia;
	private Boolean indUsoHemoterapia;
	private Boolean indUsoQuimioterapia;
	private DominioSituacao indSituacao;
	@Transient
	private String descricaoFormatada;

	private Set<MpmItemDietaSumario> itensDietaSumarios = new HashSet<MpmItemDietaSumario>(
			0);
	private Set<MpmItemCuidadoSumario> itensCuidadoSumarios = new HashSet<MpmItemCuidadoSumario>(
			0);
	private Set<MpmAprazamentoFrequencia> aprazamentoFrequencias = new HashSet<MpmAprazamentoFrequencia>(
			0);
	private Set<MpmCuidadoUsual> cuidadoUsuais = new HashSet<MpmCuidadoUsual>(0);
	private Set<MpmPrescricaoCuidado> prescricaoCuidados = new HashSet<MpmPrescricaoCuidado>(
			0);
	private Set<MpmItemMdtoSumario> itensMedicamentoSumarios = new HashSet<MpmItemMdtoSumario>(
			0);
	private Set<MpmItemPrescricaoDieta> itensPrescricaoDietas = new HashSet<MpmItemPrescricaoDieta>(
			0);
	private Set<MpmHorarioInicAprazamento> horarioInicioAprazamentos = new HashSet<MpmHorarioInicAprazamento>(
			0);
	private Set<MpmPrescricaoMdto> prescricaoMedicamentos = new HashSet<MpmPrescricaoMdto>(
			0);
	private Set<MpmItemHemoterapiaSumario> itensHemoterapiaSumarios = new HashSet<MpmItemHemoterapiaSumario>(
			0);
	private Set<MpmModeloBasicoMedicamento> modeloBasicoMedicamentos = new HashSet<MpmModeloBasicoMedicamento>(
			0);
	private Set<MpmItemModeloBasicoDieta> itensModeloBasicoDietas = new HashSet<MpmItemModeloBasicoDieta>(
			0);
	private Set<MpmModeloBasicoCuidado> modeloBasicoCuidados = new HashSet<MpmModeloBasicoCuidado>(
			0);
	private Set<MpaUsoOrdCuidado> usoOrdCuidados = new HashSet<MpaUsoOrdCuidado>();

	private Set<AbsItensSolHemoterapicas> itensSolHemoterapicas = new HashSet<AbsItensSolHemoterapicas>();

	private Set<EpeCuidados> cuidados = new HashSet<EpeCuidados>(0);
	private Set<EpePrescricoesCuidados> epePrescricoesCuidados = new HashSet<EpePrescricoesCuidados>(
			0);
	private Set<MpaUsoOrdItemHemoters> usoOrdItemHemoters = new HashSet<MpaUsoOrdItemHemoters>(
			0);
	private Set<VMpaUsoOrdMdtos> viewMpaUsoOrdMdtos = new HashSet<VMpaUsoOrdMdtos>(
			0);
	private Set<VMpmCuidado> viewMpmCuidado = new HashSet<VMpmCuidado>(0);
	private Set<VMpmMdtos> viewMpmMdtos = new HashSet<VMpmMdtos>(0);
	private Set<VMpmPrescrMdtos> viewMpmPrescrMdtos = new HashSet<VMpmPrescrMdtos>(
			0);

	// construtores

	public MpmTipoFrequenciaAprazamento() {
	}
	
	public MpmTipoFrequenciaAprazamento(Short seq) {
		this.seq = seq;
	}

	public MpmTipoFrequenciaAprazamento(Short seq, RapServidores servidor,
			String sigla, String descricao, Boolean indDigitaFrequencia,
			BigDecimal fatorConversaoHoras,
			DominioFormaCalculoAprazamento indFormaAprazamento,
			Boolean indUsoHemoterapia, DominioSituacao indSituacao,
			Date criadoEm, Boolean indUsoQuimioterapia) {
		this.seq = seq;
		this.servidor = servidor;
		this.sigla = sigla;
		this.descricao = descricao;
		this.indDigitaFrequencia = indDigitaFrequencia;
		this.fatorConversaoHoras = fatorConversaoHoras;
		this.indFormaAprazamento = indFormaAprazamento;
		this.indUsoHemoterapia = indUsoHemoterapia;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.indUsoQuimioterapia = indUsoQuimioterapia;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public MpmTipoFrequenciaAprazamento(Short seq, RapServidores servidor,
			String sigla, String descricao, Boolean indDigitaFrequencia,
			BigDecimal fatorConversaoHoras,
			DominioFormaCalculoAprazamento indFormaAprazamento,
			Boolean indUsoHemoterapia, DominioSituacao indSituacao,
			Date criadoEm, String sintaxe, Boolean indUsoQuimioterapia,
			Set<MpmItemDietaSumario> itensDietaSumarios,
			Set<MpmItemCuidadoSumario> itensCuidadoSumarios,
			Set<MpmAprazamentoFrequencia> aprazamentoFrequencias,
			Set<MpmCuidadoUsual> cuidadoUsuais,
			Set<MpmModeloBasicoMedicamento> modeloBasicoMedicamentos,
			Set<MpmPrescricaoCuidado> prescricaoCuidados,
			Set<MpmItemMdtoSumario> itensMedicamentoSumarios,
			Set<MpmItemPrescricaoDieta> itensPrescricaoDietas,
			Set<MpmHorarioInicAprazamento> horarioInicioAprazamentos,
			Set<MpmItemModeloBasicoDieta> itensModeloBasicoDietas,
			Set<MpmPrescricaoMdto> prescricaoMedicamentos,
			Set<MpmItemHemoterapiaSumario> itensHemoterapiaSumarios,
			Set<MpmModeloBasicoCuidado> modeloBasicoCuidados) {
		this.seq = seq;
		this.servidor = servidor;
		this.sigla = sigla;
		this.descricao = descricao;
		this.indDigitaFrequencia = indDigitaFrequencia;
		this.fatorConversaoHoras = fatorConversaoHoras;
		this.indFormaAprazamento = indFormaAprazamento;
		this.indUsoHemoterapia = indUsoHemoterapia;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.sintaxe = sintaxe;
		this.indUsoQuimioterapia = indUsoQuimioterapia;
		this.itensDietaSumarios = itensDietaSumarios;
		this.itensCuidadoSumarios = itensCuidadoSumarios;
		this.aprazamentoFrequencias = aprazamentoFrequencias;
		this.cuidadoUsuais = cuidadoUsuais;
		this.modeloBasicoMedicamentos = modeloBasicoMedicamentos;
		this.prescricaoCuidados = prescricaoCuidados;
		this.itensMedicamentoSumarios = itensMedicamentoSumarios;
		this.itensPrescricaoDietas = itensPrescricaoDietas;
		this.horarioInicioAprazamentos = horarioInicioAprazamentos;
		this.itensModeloBasicoDietas = itensModeloBasicoDietas;
		this.prescricaoMedicamentos = prescricaoMedicamentos;
		this.itensHemoterapiaSumarios = itensHemoterapiaSumarios;
		this.modeloBasicoCuidados = modeloBasicoCuidados;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings({"unused", "PMD.NPathComplexity"})
	private void convertTextoMaiusculo() {
		sintaxe = sintaxe != null ? sintaxe.toUpperCase() : null;
		descricao = descricao != null ? descricao.toUpperCase() : null;
		sigla = sigla != null ? sigla.toUpperCase() : null;

		// @ORADB constrain agh.mpm_tfq_ck4
		if (this.getFatorConversaoHoras() != null && this.getFatorConversaoHoras().compareTo(BigDecimal.ZERO) < 0) {
			throw new BaseRuntimeException(MpmTipoFrequenciaAprazamentoExceptionCode.FATOR_CONVERSAO_HORAS_NEGATIVO);
		}

		// @ORADB constrain agh.mpm_tfq_ck5
		if (DominioFormaCalculoAprazamento.C.equals(
				this.getIndFormaAprazamento())
				&& (!BigDecimal.ZERO.equals(this.getFatorConversaoHoras()) || this
						.getIndDigitaFrequencia())){
			throw new BaseRuntimeException(MpmTipoFrequenciaAprazamentoExceptionCode.APRAZAMENTO_CONTINUO_INCONSISTENTE);
			
		}
		
		// @ORADB constrain agh.mpm_tfq_ck7
		if (this.getIndDigitaFrequencia()){
			if (this.getSintaxe() == null){
				throw new BaseRuntimeException(MpmTipoFrequenciaAprazamentoExceptionCode.FREQUENCIA_SINTAXE_INCONSISTENTE_INFORMAR_SINTAXE);
			}			
		}else{
			if (this.getSintaxe() != null){
				throw new BaseRuntimeException(MpmTipoFrequenciaAprazamentoExceptionCode.FREQUENCIA_SINTAXE_INCONSISTENTE);
			}
			
		}
			

	}

	// getters & setters

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmTfqSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "SIGLA", unique = true, nullable = false, length = 3)
	@Length(max = 3)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 30)
	@Length(max = 30)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_DIGITA_FREQUENCIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDigitaFrequencia() {
		return this.indDigitaFrequencia;
	}

	public void setIndDigitaFrequencia(Boolean indDigitaFrequencia) {
		this.indDigitaFrequencia = indDigitaFrequencia;
	}

	@Column(name = "FATOR_CONVERSAO_HORAS", nullable = false, precision = 10, scale = 5)
	public BigDecimal getFatorConversaoHoras() {
		return this.fatorConversaoHoras;
	}

	public void setFatorConversaoHoras(BigDecimal fatorConversaoHoras) {
		this.fatorConversaoHoras = fatorConversaoHoras;
	}

	@Column(name = "IND_FORMA_APRAZAMENTO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioFormaCalculoAprazamento getIndFormaAprazamento() {
		return this.indFormaAprazamento;
	}

	public void setIndFormaAprazamento(
			DominioFormaCalculoAprazamento indFormaAprazamento) {
		this.indFormaAprazamento = indFormaAprazamento;
	}

	@Column(name = "IND_USO_HEMOTERAPIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoHemoterapia() {
		return this.indUsoHemoterapia;
	}

	public void setIndUsoHemoterapia(Boolean indUsoHemoterapia) {
		this.indUsoHemoterapia = indUsoHemoterapia;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SINTAXE", length = 25)
	@Length(max = 25)
	public String getSintaxe() {
		return this.sintaxe;
	}

	public void setSintaxe(String sintaxe) {
		this.sintaxe = sintaxe;
	}

	@Column(name = "IND_USO_QUIMIOTERAPIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoQuimioterapia() {
		return this.indUsoQuimioterapia;
	}

	public void setIndUsoQuimioterapia(Boolean indUsoQuimioterapia) {
		this.indUsoQuimioterapia = indUsoQuimioterapia;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmItemDietaSumario> getItensDietaSumarios() {
		return this.itensDietaSumarios;
	}

	public void setItensDietaSumarios(
			Set<MpmItemDietaSumario> itensDietaSumarios) {
		this.itensDietaSumarios = itensDietaSumarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmItemCuidadoSumario> getItensCuidadoSumarios() {
		return this.itensCuidadoSumarios;
	}

	public void setItensCuidadoSumarios(
			Set<MpmItemCuidadoSumario> itensCuidadoSumarios) {
		this.itensCuidadoSumarios = itensCuidadoSumarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmAprazamentoFrequencia> getAprazamentoFrequencias() {
		return this.aprazamentoFrequencias;
	}

	public void setAprazamentoFrequencias(
			Set<MpmAprazamentoFrequencia> aprazamentoFrequencias) {
		this.aprazamentoFrequencias = aprazamentoFrequencias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpmTipoFreqAprazamentos")
	public Set<MpmCuidadoUsual> getCuidadoUsuais() {
		return this.cuidadoUsuais;
	}

	public void setCuidadoUsuais(Set<MpmCuidadoUsual> cuidadoUsuais) {
		this.cuidadoUsuais = cuidadoUsuais;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<MpmModeloBasicoMedicamento> getModeloBasicoMedicamentos() {
		return this.modeloBasicoMedicamentos;
	}

	public void setModeloBasicoMedicamentos(
			Set<MpmModeloBasicoMedicamento> modeloBasicoMedicamentos) {
		this.modeloBasicoMedicamentos = modeloBasicoMedicamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpmTipoFreqAprazamentos")
	public Set<MpmPrescricaoCuidado> getPrescricaoCuidados() {
		return this.prescricaoCuidados;
	}

	public void setPrescricaoCuidados(
			Set<MpmPrescricaoCuidado> prescricaoCuidados) {
		this.prescricaoCuidados = prescricaoCuidados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmItemMdtoSumario> getItensMedicamentoSumarios() {
		return this.itensMedicamentoSumarios;
	}

	public void setItensMedicamentoSumarios(
			Set<MpmItemMdtoSumario> itensMedicamentoSumarios) {
		this.itensMedicamentoSumarios = itensMedicamentoSumarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmItemPrescricaoDieta> getItensPrescricaoDietas() {
		return this.itensPrescricaoDietas;
	}

	public void setItensPrescricaoDietas(
			Set<MpmItemPrescricaoDieta> itensPrescricaoDietas) {
		this.itensPrescricaoDietas = itensPrescricaoDietas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmHorarioInicAprazamento> getHorarioInicioAprazamentos() {
		return this.horarioInicioAprazamentos;
	}

	public void setHorarioInicioAprazamentos(
			Set<MpmHorarioInicAprazamento> horarioInicioAprazamentos) {
		this.horarioInicioAprazamentos = horarioInicioAprazamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<MpmItemModeloBasicoDieta> getItensModeloBasicoDietas() {
		return this.itensModeloBasicoDietas;
	}

	public void setItensModeloBasicoDietas(
			Set<MpmItemModeloBasicoDieta> itensModeloBasicoDietas) {
		this.itensModeloBasicoDietas = itensModeloBasicoDietas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmPrescricaoMdto> getPrescricaoMedicamentos() {
		return this.prescricaoMedicamentos;
	}

	public void setPrescricaoMedicamentos(
			Set<MpmPrescricaoMdto> prescricaoMedicamentos) {
		this.prescricaoMedicamentos = prescricaoMedicamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<MpmItemHemoterapiaSumario> getItensHemoterapiaSumarios() {
		return this.itensHemoterapiaSumarios;
	}

	public void setItensHemoterapiaSumarios(
			Set<MpmItemHemoterapiaSumario> itensHemoterapiaSumarios) {
		this.itensHemoterapiaSumarios = itensHemoterapiaSumarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<MpmModeloBasicoCuidado> getModeloBasicoCuidados() {
		return this.modeloBasicoCuidados;
	}

	public void setModeloBasicoCuidados(
			Set<MpmModeloBasicoCuidado> modeloBasicoCuidados) {
		this.modeloBasicoCuidados = modeloBasicoCuidados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<EpeCuidados> getCuidados() {
		return this.cuidados;
	}

	public void setCuidados(Set<EpeCuidados> cuidados) {
		this.cuidados = cuidados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<EpePrescricoesCuidados> getEpePrescricoesCuidados() {
		return this.epePrescricoesCuidados;
	}

	public void setEpePrescricoesCuidados(
			Set<EpePrescricoesCuidados> epePrescricoesCuidados) {
		this.epePrescricoesCuidados = epePrescricoesCuidados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<MpaUsoOrdItemHemoters> getUsoOrdItemHemoters() {
		return this.usoOrdItemHemoters;
	}

	public void setUsoOrdItemHemoters(
			Set<MpaUsoOrdItemHemoters> usoOrdItemHemoters) {
		this.usoOrdItemHemoters = usoOrdItemHemoters;
	}

	// outros

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<VMpaUsoOrdMdtos> getViewMpaUsoOrdMdtos() {
		return viewMpaUsoOrdMdtos;
	}

	public void setViewMpaUsoOrdMdtos(Set<VMpaUsoOrdMdtos> viewMpaUsoOrdMdtos) {
		this.viewMpaUsoOrdMdtos = viewMpaUsoOrdMdtos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<VMpmCuidado> getViewMpmCuidado() {
		return viewMpmCuidado;
	}

	public void setViewMpmCuidado(Set<VMpmCuidado> viewMpmCuidado) {
		this.viewMpmCuidado = viewMpmCuidado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<VMpmMdtos> getViewMpmMdtos() {
		return viewMpmMdtos;
	}

	public void setViewMpmMdtos(Set<VMpmMdtos> viewMpmMdtos) {
		this.viewMpmMdtos = viewMpmMdtos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<VMpmPrescrMdtos> getViewMpmPrescrMdtos() {
		return viewMpmPrescrMdtos;
	}

	public void setViewMpmPrescrMdtos(Set<VMpmPrescrMdtos> viewMpmPrescrMdtos) {
		this.viewMpmPrescrMdtos = viewMpmPrescrMdtos;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmTipoFrequenciaAprazamento)) {
			return false;
		}
		MpmTipoFrequenciaAprazamento castOther = (MpmTipoFrequenciaAprazamento) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), 
		SIGLA("sigla"), 
		DESCRICAO("descricao"), 
		FATOR_CONVERSAO_HORAS("fatorConversaoHoras"), 
		CRIADO_EM("criadoEm"), 
		SINTAXE("sintaxe"), 
		SERVIDOR("servidor"), 
		IND_DIGITA_FREQUENCIA("indDigitaFrequencia"), 
		IND_FORMA_APRAZAMENTO("indFormaAprazamento"), 
		IND_USO_HEMOTERAPIA("indUsoHemoterapia"), 
		IND_SITUACAO("indSituacao"), 
		IND_USO_QUIMIOTERAPIA("indUsoQuimioterapia"), 
		APRAZAMENTO_FREQUENCIAS("aprazamentoFrequencias"),
		APRAZAMENTO_FREQUENCIAS_RAP_SERVIDOR("aprazamentoFrequencias.servidor"), 
		APRAZAMENTO_FREQUENCIAS_RAP_SERVIDOR_PESSOA_FISICA("aprazamentoFrequencias.servidor.pessoaFisica"),
		CUIDADOS_USUAIS("cuidadoUsuais"),
		HORARIO_INICIO_APRAZAMENTO("horarioInicioAprazamentos"),
		ITEM_CUIDADO_SUMARIO("itensCuidadoSumarios"),
		ITEN_DIETA_SUMARIO("itensDietaSumarios"),
		ITEM_HEMOTERAPIA_SUMARIO("itensHemoterapiaSumarios"),
		ITEM_MEDICAMENTO_SUMARIO("itensMedicamentoSumarios"),
		ITEM_MODELO_BASICO("itensModeloBasicoDietas"),
		ITEM_PRESCRICAO_DIETA("itensPrescricaoDietas"),
		MODELO_BASICO_CUIDADOS("modeloBasicoCuidados"),
		MODELO_BASICO_MEDICAMENTOS("modeloBasicoMedicamentos"),
		PRESCRICAO_CUIDADOS("prescricaoCuidados"),
		PRESCRICAO_MEDICAMENTOS("prescricaoMedicamentos"),
		USO_ORD_CUIDADOS("usoOrdCuidados"),
		ITEM_SOL_HEMOTERAPICAS("itensSolHemoterapicas"),
		CUIDADOS("cuidados"),
		EPE_PRESCRICAO_CUIDADOS("epePrescricoesCuidados"),
		USO_ORD_ITEM_HEMOTER("usoOrdItemHemoters"),
		VIEW_USO_ORD_MDTOS("viewMpaUsoOrdMdtos"),
		VIEW_CUIDADOS("viewMpmCuidado"),
		VIEW_MDTOS("viewMpmMdtos"),
		VIEW_PRESCR_MDTOS("viewMpmPrescrMdtos");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	/**
	 * rcorvalao 30/09/2010
	 * 
	 * @param frequencia
	 * @return
	 */
	@Transient
	public String getSintaxeFormatada(Short frequencia) {
		String returnValue = "null";

		String strFreq = frequencia != null ? frequencia.toString() : "";

		if (StringUtils.isNotBlank(this.getSintaxe())) {
			returnValue = this.getSintaxe().replaceAll(SINTAXE_MARCADOR,
					strFreq);
		}

		return returnValue;
	}

	@Transient
	public String getDescricaoSintaxeFormatada(Short frequencia) {
		return (this.sintaxe == null || frequencia == null ? this.descricao
				: getFrequenciaFormatada(sintaxe, frequencia)).toUpperCase();
	}

	@Transient
	private String getFrequenciaFormatada(String sintaxe, Short frequencia) {
		String returnValue = sintaxe.replaceAll("#",frequencia != null ? frequencia.toString() : "").toUpperCase();
		this.setDescricaoFormatada(returnValue);
		return returnValue;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFrequenciaAprazamento")
	public Set<MpaUsoOrdCuidado> getUsoOrdCuidados() {
		return usoOrdCuidados;
	}

	public void setUsoOrdCuidados(Set<MpaUsoOrdCuidado> usoOrdCuidados) {
		this.usoOrdCuidados = usoOrdCuidados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoFreqAprazamento")
	public Set<AbsItensSolHemoterapicas> getItensSolHemoterapicas() {
		return itensSolHemoterapicas;
	}

	public void setItensSolHemoterapicas(
			Set<AbsItensSolHemoterapicas> itensSolHemoterapicas) {
		this.itensSolHemoterapicas = itensSolHemoterapicas;
	}
	
	@Transient
	public String getDescricaoSintaxeFormatadaNotCase(Short frequencia) {
		return (this.sintaxe == null || frequencia == null ? this.descricao
				: getFrequenciaFormatada(sintaxe, frequencia));
	}
	
	@Transient
	public String getDescricaoFormatada() {
		return descricaoFormatada;
	}

	public void setDescricaoFormatada(String descricaoFormatada) {
		this.descricaoFormatada = descricaoFormatada;
	}

}
