package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PacienteAguardandoTransplanteOrgaoVO implements Serializable {
	
	private static final long serialVersionUID = 1337235810395539181L;

	private String descricaoDoencaBase;
	private String pacNome;
	private Integer pacProntuario;
	private DominioSexo pacSexo;
	private Date pacDtNascimento;
	private DominioTipoOrgao tipoOrgao;
	private Date dataIngresso;
	private Date pacDataObito;
	private Integer codigoReceptor;
	private Integer seqTransplante;
	private String idade;
	private String tempoPermanencia;

	private Boolean temGrm;
	private Boolean temDiabetes;
	private Boolean temHIV;
	private Boolean temHepatiteB;
	private Boolean temHepatiteC;
	private Date dataRetirada;
	private Integer permanencia;
	
	private Boolean existeResultadoExame;
	
	private Date dataRegistroInativado;
	
	public void alterarParametros(){
		this.idade = obterIdadePaciente(this.pacDtNascimento);
		this.tempoPermanencia = obterPermanenciaPaciente(this.dataIngresso).toString().concat(" dias");
	}
	public void alterarParametrosAbaInativo(){
		this.idade = obterIdadePaciente(this.pacDtNascimento);
		this.tempoPermanencia = obterPermanenciaPacienteInativo(this.dataIngresso).toString().concat(" dias");
	}
	
	public String obterIdadePaciente(Date  dataNascimento){
		Integer idade = DateUtil.getIdadeDias(dataNascimento);
		if(idade > 365){
			return DateUtil.getIdade(dataNascimento) + " anos";
		} else if(idade > 30){
			return DateUtil.getIdadeMeses(dataNascimento) + " meses";
		} else if(idade <= 30){
			return idade + " dias";
		}
		return DateUtil.getIdade(dataNascimento).toString();
	}
	
	public Integer obterPermanenciaPaciente(Date  dataIngresso){
		
		return DateUtil.obterQtdDiasEntreDuasDatas(dataIngresso, new Date());
	}
	public Integer obterPermanenciaPacienteInativo(Date  dataIngresso){
		
		return DateUtil.obterQtdDiasEntreDuasDatas(dataIngresso,this.dataRegistroInativado);
	}
	
	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public DominioSexo getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(DominioSexo pacSexo) {
		this.pacSexo = pacSexo;
	}

	public Date getPacDtNascimento() {
		return pacDtNascimento;
	}

	public void setPacDtNascimento(Date pacDtNascimento) {
		this.pacDtNascimento = pacDtNascimento;
	}

	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public Date getDataIngresso() {
		return dataIngresso;
	}

	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}

	public Date getPacDataObito() {
		return pacDataObito;
	}

	public void setPacDataObito(Date pacDataObito) {
		this.pacDataObito = pacDataObito;
	}

	public enum Fields {
		
		NOME("pacNome"),
		PRONTUARIO("pacProntuario"),
		DESCRICAO("descricaoDoencaBase"),
		SEXO("pacSexo"),
		DATA_NASCIMENTO("pacDtNascimento"),
		TIPO_ORGAO("tipoOrgao"),
		DATA_INGRESSO("dataIngresso"),
		DATA_OBITO("pacDataObito"),
		TEM_DIABETES("temDiabetes"),
		TEM_HIV("temHIV"),
		TEM_HEPATITE_B("temHepatiteB"),
		TEM_HEPATITE_C("temHepatiteC"),
		CODIGO_RECEPTOR("codigoReceptor"),
		SEQ("seqTransplante"),
		DATA_RETIRADA("dataRetirada"),
		PERMANENCIA("permanencia"),
		DATA_REGISTRO_INATIVO("dataRegistroInativado")
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

	public Boolean getTemDiabetes() {
		return temDiabetes;
	}

	public void setTemDiabetes(Boolean temDiabetes) {
		this.temDiabetes = temDiabetes;
	}

	public Boolean getTemHIV() {
		return temHIV;
	}

	public void setTemHIV(Boolean temHIV) {
		this.temHIV = temHIV;
	}

	public Boolean getTemHepatiteB() {
		return temHepatiteB;
	}

	public void setTemHepatiteB(Boolean temHepatiteB) {
		this.temHepatiteB = temHepatiteB;
	}

	public Boolean getTemHepatiteC() {
		return temHepatiteC;
	}

	public void setTemHepatiteC(Boolean temHepatiteC) {
		this.temHepatiteC = temHepatiteC;
	}

	public Boolean getTemGrm() {
		return temGrm;
	}

	public void setTemGrm(Boolean temGrm) {
		this.temGrm = temGrm;
	}

	public Integer getCodigoReceptor() {
		return codigoReceptor;
	}

	public void setCodigoReceptor(Integer codigoReceptor) {
		this.codigoReceptor = codigoReceptor;
	}

	public Integer getSeqTransplante() {
		return seqTransplante;
	}

	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqTransplante());
		umHashCodeBuilder.append(this.getPacNome());
		umHashCodeBuilder.append(this.getPacProntuario());
		umHashCodeBuilder.append(this.getPacDtNascimento());
		umHashCodeBuilder.append(this.getDataIngresso());
		umHashCodeBuilder.append(this.getCodigoReceptor());
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
		PacienteAguardandoTransplanteOrgaoVO other = (PacienteAguardandoTransplanteOrgaoVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqTransplante(), other.getSeqTransplante());
		umEqualsBuilder.append(this.getPacNome(), other.getPacNome());
		umEqualsBuilder.append(this.getPacProntuario(), other.getPacProntuario());
		umEqualsBuilder.append(this.getPacDtNascimento(), other.getPacDtNascimento());
		umEqualsBuilder.append(this.getDataIngresso(), other.getDataIngresso());
		umEqualsBuilder.append(this.getCodigoReceptor(), other.getCodigoReceptor());
        return umEqualsBuilder.isEquals();
	}

	public Date getDataRetirada() {
		return dataRetirada;
	}

	public void setDataRetirada(Date dataRetirada) {
		this.dataRetirada = dataRetirada;
	}

	public Integer getPermanencia() {
		return permanencia;
	}

	public void setPermanencia(Integer permanencia) {
		this.permanencia = permanencia;
	}

	public String getDescricaoDoencaBase() {
		return descricaoDoencaBase;
	}

	public void setDescricaoDoencaBase(String descricaoDoencaBase) {
		this.descricaoDoencaBase = descricaoDoencaBase;
	}

	public String getTempoPermanencia() {
		return tempoPermanencia;
	}

	public void setTempoPermanencia(String tempoPermanencia) {
		this.tempoPermanencia = tempoPermanencia;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public Boolean getExisteResultadoExame() {
		return existeResultadoExame;
	}

	public void setExisteResultadoExame(Boolean existeResultadoExame) {
		this.existeResultadoExame = existeResultadoExame;
	}

	public Date getDataRegistroInativado() {
		return dataRegistroInativado;
	}

	public void setDataRegistroInativado(Date dataRegistroInativado) {
		this.dataRegistroInativado = dataRegistroInativado;
	}
	
}
