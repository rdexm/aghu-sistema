package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;

public class CirurgiasExposicaoRadiacaoIonizanteVO {
	private String data;
	private Date dataInicioCirurgia;
	private String sciNome;
	private Integer prontuario;
	private String sigla;
	private String nomeEspecialidade;
	private DominioTipoAtuacao tipoAtuacao;
	private Integer matricula;
	private Short vinCodigo;
	private String nomeProf;
	private String procedimento;
	private Short equipamento;
	private DominioFuncaoProfissional funcaoProfissional;
	private String atuacao;
	private String strDataDiaMesAno;
	private String strDataInicioCirurgia;
	private String strProntuario;
	private String strEquipamento;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getDataInicioCirurgia() {
		return dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	public String getSciNome() {
		return sciNome;
	}

	public void setSciNome(String sciNome) {
		this.sciNome = sciNome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public DominioTipoAtuacao getTipoAtuacao() {
		return tipoAtuacao;
	}

	public void setTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		this.tipoAtuacao = tipoAtuacao;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNomeProf() {
		return nomeProf;
	}

	public void setNomeProf(String nomeProf) {
		this.nomeProf = nomeProf;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Short getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(Short equipamento) {
		this.equipamento = equipamento;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(
			DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public String getAtuacao() {
		return atuacao;
	}

	public void setAtuacao(String atuacao) {
		this.atuacao = atuacao;
	}

	public String getStrDataDiaMesAno() {
		return strDataDiaMesAno;
	}

	public void setStrDataDiaMesAno(String strDataDiaMesAno) {
		this.strDataDiaMesAno = strDataDiaMesAno;
	}

	public String getStrDataInicioCirurgia() {
		return strDataInicioCirurgia;
	}

	public void setStrDataInicioCirurgia(String strDataInicioCirurgia) {
		this.strDataInicioCirurgia = strDataInicioCirurgia;
	}

	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public String getStrEquipamento() {
		return strEquipamento;
	}

	public void setStrEquipamento(String strEquipamento) {
		this.strEquipamento = strEquipamento;
	}

	public enum Fields {
		CRG_DATA("data"),
		CRG_DTHR_INICIO_CIRG("dataInicioCirurgia"),
		SCI_NOME("sciNome"), 
		PAC_PRONTUARIO("prontuario"),
		ESP_SIGLA("sigla"),
		ESP_NOME("nomeEspecialidade"),
		TIPO_ATUACAO("tipoAtuacao"),
		MATRICULA("matricula"),
		VINCODIGO("vinCodigo"),
		NOME_PROF("nomeProf"),
		PROCEDIMENTO("procedimento"),
		EQUIPAMENTO("equipamento"),
		PCG_FUNCAO_PROFISSIONAL("funcaoProfissional"),
		ATUACAO("atuacao"),
		STR_DATA_DIA_MES_ANO("strDataDiaMesAno"),
		STR_DATA_INICIO_CIRURGIA("strDataInicioCirurgia"),
		STR_PRONTUARIO("strProntuario"),
		STR_EQUIPAMENTO("strEquipamento");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hashCode = new HashCodeBuilder();
		hashCode
			.append(getStrDataDiaMesAno())
			.append(getStrDataInicioCirurgia())
			.append(getSciNome())
			.append(getStrProntuario())
			.append(getSigla())
			.append(getNomeEspecialidade())
			.append(getMatricula())
			.append(getVinCodigo())
			.append(getNomeProf())
			.append(getProcedimento())
			.append(getEquipamento())
			.append(getStrEquipamento())
			.append(getAtuacao());
		
		return hashCode.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CirurgiasExposicaoRadiacaoIonizanteVO)) {
			return false;
		}
		CirurgiasExposicaoRadiacaoIonizanteVO other = (CirurgiasExposicaoRadiacaoIonizanteVO) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder
				.append(this.getStrDataDiaMesAno(), other.getStrDataDiaMesAno())
				.append(this.getStrDataInicioCirurgia(), other.getStrDataInicioCirurgia())
				.append(this.getSciNome(), other.getSciNome())
				.append(this.getStrProntuario(), other.getStrProntuario())
				.append(this.getSigla(), other.getSigla())
				.append(this.getNomeEspecialidade(), other.getNomeEspecialidade())
				.append(this.getMatricula(), other.getMatricula())
				.append(this.getVinCodigo(), other.getVinCodigo())
				.append(this.getNomeProf(), other.getNomeProf())
				.append(this.getProcedimento(), other.getProcedimento())
				.append(this.getStrEquipamento(), other.getStrEquipamento())
				.append(this.getAtuacao(), other.getAtuacao());

		return equalsBuilder.isEquals();
	}

	@Override
	public String toString() {
		return "CirurgiasExposicaoRadiacaoIonizanteVO [data=" + data
				+ ", dataInicioCirurgia=" + dataInicioCirurgia + ", sciNome="
				+ sciNome + ", prontuario=" + prontuario + ", sigla=" + sigla
				+ ", nomeEspecialidade=" + nomeEspecialidade + ", tipoAtuacao="
				+ tipoAtuacao + ", matricula=" + matricula + ", vinCodigo="
				+ vinCodigo + ", nomeProf=" + nomeProf + ", procedimento="
				+ procedimento + ", equipamento=" + equipamento
				+ ", funcaoProfissional=" + funcaoProfissional + ", atuacao="
				+ atuacao + ", strDataDiaMesAno=" + strDataDiaMesAno
				+ ", strDataInicioCirurgia=" + strDataInicioCirurgia
				+ ", strProntuario=" + strProntuario + ", strEquipamento="
				+ strEquipamento + "]";
	}

}
