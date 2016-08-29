package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;

public class ListarTransplantesVO implements Serializable {
	
	private static final long serialVersionUID = -833108087905971479L;
	private Integer codigoMtxTransplante;
	private Integer codigoPaciente;
	private Integer prontuarioPaciente;
	private String nomePaciente;
	private DominioSexo sexoPaciente;
	private Date dataNascimentoPaciente;
	private Date dataObitoPaciente;
	private Integer criterioSeq;
	private String criterioStatus;
	private Integer transplanteCriterioSeq;
	private DominioSituacaoTmo transplanteTipoTmo;
	private DominioTipoAlogenico transplanteTipoAlogenico;
	private Integer codigoPacienteReceptor;
	private Date dataInclusao;
	private Date dataTransplante;
	private Integer temGermeMultiresistente;
	private Integer temDiabetes;
	private Integer temHIV;
	private Integer temHepatiteB;
	private Integer temHepatiteC;
	private Double escore;
	private Integer permanencia;
	private DominioSituacaoTransplante situacaoTransplante;
	private Integer coeficiente;
	private Integer selecioneAba;
	private Date dataSituacao;
	private Date dataSituacaoAtual;
	private Integer verificarMaterialBiologico;
	private Integer trpSeq;
	private Integer codigoPacienteDoador;
	
	
	public Integer getCodigoPacienteDoador() {
		return codigoPacienteDoador;
	}
	public void setCodigoPacienteDoador(Integer codigoPacienteDoador) {
		this.codigoPacienteDoador = codigoPacienteDoador;
	}
	public Integer getCodigoMtxTransplante() {
		return codigoMtxTransplante;
	}
	public void setCodigoMtxTransplante(Integer codigoMtxTransplante) {
		this.codigoMtxTransplante = codigoMtxTransplante;
	}
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public DominioSexo getSexoPaciente() {
		return sexoPaciente;
	}
	public void setSexoPaciente(DominioSexo sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}
	public Date getDataNascimentoPaciente() {
		return dataNascimentoPaciente;
	}
	public void setDataNascimentoPaciente(Date dataNascimentoPaciente) {
		this.dataNascimentoPaciente = dataNascimentoPaciente;
	}
	public Date getDataObitoPaciente() {
		return dataObitoPaciente;
	}
	public void setDataObitoPaciente(Date dataObitoPaciente) {
		this.dataObitoPaciente = dataObitoPaciente;
	}
	public Integer getCriterioSeq() {
		return criterioSeq;
	}
	public void setCriterioSeq(Integer criterioSeq) {
		this.criterioSeq = criterioSeq;
	}
	public String getCriterioStatus() {
		return criterioStatus;
	}
	public void setCriterioStatus(String criterioStatus) {
		this.criterioStatus = criterioStatus;
	}
	public Integer getTransplanteCriterioSeq() {
		return transplanteCriterioSeq;
	}
	public void setTransplanteCriterioSeq(Integer transplanteCriterioSeq) {
		this.transplanteCriterioSeq = transplanteCriterioSeq;
	}
	public DominioSituacaoTmo getTransplanteTipoTmo() {
		return transplanteTipoTmo;
	}
	public void setTransplanteTipoTmo(DominioSituacaoTmo transplanteTipoTmo) {
		this.transplanteTipoTmo = transplanteTipoTmo;
	}
	public DominioTipoAlogenico getTransplanteTipoAlogenico() {
		return transplanteTipoAlogenico;
	}
	public void setTransplanteTipoAlogenico(
			DominioTipoAlogenico transplanteTipoAlogenico) {
		this.transplanteTipoAlogenico = transplanteTipoAlogenico;
	}
	public Integer getCodigoPacienteReceptor() {
		return codigoPacienteReceptor;
	}
	public void setCodigoPacienteReceptor(Integer codigoPacienteReceptor) {
		this.codigoPacienteReceptor = codigoPacienteReceptor;
	}
	public Date getDataInclusao() {
		return dataInclusao;
	}
	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	public Date getDataTransplante() {
		return dataTransplante;
	}
	public void setDataTransplante(Date dataTransplante) {
		this.dataTransplante = dataTransplante;
	}
	public Integer getTemGermeMultiresistente() {
		return temGermeMultiresistente;
	}
	public void setTemGermeMultiresistente(Integer temGermeMultiresistente) {
		this.temGermeMultiresistente = temGermeMultiresistente;
	}
	public Integer getTemDiabetes() {
		return temDiabetes;
	}
	public void setTemDiabetes(Integer temDiabetes) {
		this.temDiabetes = temDiabetes;
	}
	public Integer getTemHIV() {
		return temHIV;
	}
	public void setTemHIV(Integer temHIV) {
		this.temHIV = temHIV;
	}
	public Integer getTemHepatiteB() {
		return temHepatiteB;
	}
	public void setTemHepatiteB(Integer temHepatiteB) {
		this.temHepatiteB = temHepatiteB;
	}
	public Integer getTemHepatiteC() {
		return temHepatiteC;
	}
	public void setTemHepatiteC(Integer temHepatiteC) {
		this.temHepatiteC = temHepatiteC;
	}
	public Double getEscore() {
		return escore;
	}
	public void setEscore(Double escore) {
		this.escore = escore;
	}
	public Integer getPermanencia() {
		return permanencia;
	}
	public void setPermanencia(Integer permanencia) {
		this.permanencia = permanencia;
	}
	
	public DominioSituacaoTransplante getSituacaoTransplante() {
		return situacaoTransplante;
	}
	public void setSituacaoTransplante(
			DominioSituacaoTransplante situacaoTransplante) {
		this.situacaoTransplante = situacaoTransplante;
	}
	public Integer getCoeficiente() {
		return coeficiente;
	}
	public void setCoeficiente(Integer coeficiente) {
		this.coeficiente = coeficiente;
	}
	public Integer getSelecioneAba() {
		return selecioneAba;
	}
	public void setSelecioneAba(Integer selecioneAba) {
		this.selecioneAba = selecioneAba;
	}
	public Date getDataSituacao() {
		return dataSituacao;
	}
	public void setDataSituacao(Date dataSituacao) {
		this.dataSituacao = dataSituacao;
	}
	public Date getDataSituacaoAtual() {
		return dataSituacaoAtual;
	}
	public void setDataSituacaoAtual(Date dataSituacaoAtual) {
		this.dataSituacaoAtual = dataSituacaoAtual;
	}
	public Integer getVerificarMaterialBiologico() {
		return verificarMaterialBiologico;
	}
	public void setVerificarMaterialBiologico(Integer verificarMaterialBiologico) {
		this.verificarMaterialBiologico = verificarMaterialBiologico;
	}
	public Integer getTrpSeq() {
		return trpSeq;
	}
	public void setTrpSeq(Integer trpSeq) {
		this.trpSeq = trpSeq;
	}
	
	public enum Fields {
		CODIGO_TRANSPLANTE("codigoMtxTransplante"),
		CODIGO_PACIENTE("codigoPaciente"),
		PRONTUARIO_PACIENTE("prontuarioPaciente"), 
		NOME_PACIENTE("nomePaciente"), 
		SEXO_PACIENTE("sexoPaciente"),
		DT_NASCIMENTO_PACIENTE("dataNascimentoPaciente"),
		DT_OBITO_PACIENTE("dataObitoPaciente"),
		CRITERIO_SEQ("criterioSeq"),
		CRITERIO_STATUS("criterioStatus"),
		TRANSPLANTE_CRITERIO_SEQ("transplanteCriterioSeq"),
		TIPO_TRANSPLANTE_TMO("transplanteTipoTmo"),
		TIPO_ALOGENICO("transplanteTipoAlogenico"),
		CODIGO_PACIENTE_RECEPTOR("codigoPacienteReceptor"),
		DATA_INCLUSAO("dataInclusao"),
		DATA_TRANSPLANTE("dataTransplante"),
		TEM_GRM("temGermeMultiresistente"),
		TEM_DIABETES("temDiabetes"),
		TEM_HIV("temHIV"),
		TEM_HEPATITE_B("temHepatiteB"),
		TEM_HEPATITE_("temHepatiteC"),
		ESCORE("escore"),
		PERMANENCIA("permanencia"),
		COEFICIENTE("coeficiente"),
		DATA_SITUACAO("dataSituacao"),
		DATA_SITUACAO_ATUAL("dataSituacaoAtual"),
		MATERIAL_BIOLOGICO("verificarMaterialBiologico"),
		TRP_SEQ("trpSeq"),
		SITUACAO_TRANSPLANTE("situacaoTransplante"),
		CODIGO_PACIENTE_DOADOR("codigoPacienteDoador");
		
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	public String retonarTipoTransplante(){
		if (this.transplanteTipoTmo != null && this.transplanteTipoTmo.equals(DominioSituacaoTmo.U)) {
			return this.transplanteTipoTmo.getDescricao();
		}else{
			if (this.transplanteTipoTmo != null && this.transplanteTipoTmo.equals(DominioSituacaoTmo.G)) {
				if (this.transplanteTipoAlogenico != null) {
					return this.transplanteTipoTmo.getDescricao() +" - "+ this.transplanteTipoAlogenico.getDescricao();
				}
			}
			return "";
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getTrpSeq());
		umHashCodeBuilder.append(this.getNomePaciente());
		umHashCodeBuilder.append(this.getProntuarioPaciente());
		umHashCodeBuilder.append(this.getDataNascimentoPaciente());
		umHashCodeBuilder.append(this.getDataInclusao());
		umHashCodeBuilder.append(this.getCodigoPacienteReceptor());
        return umHashCodeBuilder.toHashCode();
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
		ListarTransplantesVO other = (ListarTransplantesVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getTrpSeq(), other.getTrpSeq());
		umEqualsBuilder.append(this.getNomePaciente(), other.getNomePaciente());
		umEqualsBuilder.append(this.getProntuarioPaciente(), other.getProntuarioPaciente());
		umEqualsBuilder.append(this.getDataNascimentoPaciente(), other.getDataNascimentoPaciente());
		umEqualsBuilder.append(this.getDataInclusao(), other.getDataInclusao());
		umEqualsBuilder.append(this.getCodigoPacienteReceptor(), other.getCodigoPacienteReceptor());
        return umEqualsBuilder.isEquals();
	}

}
