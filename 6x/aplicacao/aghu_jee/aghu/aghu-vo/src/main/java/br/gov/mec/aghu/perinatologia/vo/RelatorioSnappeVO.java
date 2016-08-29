package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioSnappeVO implements Serializable{
	
	private static final long serialVersionUID = 7470586429386840685L;
	
	private String nomeIdentificacao;
	private String idadeIdentificacao;
	private String dataIdentificacao;
	private Date criadoEm;
	private Date dataNascimento;
	private String sexoIdentificacao;
	private String pesoNascimentoIdentificacao;
	private Short leitoIdentificacao;
	private Integer prontuario;
	private String prontuarioIdentificacao;
	private String alturaNascimentoIdentificacao;
	private String pressaoArterialMediaFator;
	private String temperaturaFator;
	private String razaoPo2Fi2Fator;
	private String phSangueFator;
	private String convulsoesMultiplasFator;
	private String volumeUrinarioFator;
	private String pesoNascimentoFator;
	private String pigFator;
	private String apgarFator;
	private Integer pressaoArterialEscore;
	private Integer pressaoArterialMediaEscore;
	private Integer temperaturaEscore;
	private Integer razaoPo2Fi2Escore;
	private Integer phSangueEscore;
	private Integer convulsoesMultiplasEscore;
	private Integer volumeUrinarioEscore;
	private Integer pesoNascimentoEscore;
	private Integer pigEscore;
	private Integer apgarEscore;
	private Short totalEscore;
	private String infResponsavel;
	private String dtNascimento;
	
	public String getNomeIdentificacao() {
		return nomeIdentificacao;
	}
	public void setNomeIdentificacao(String nomeIdentificacao) {
		this.nomeIdentificacao = nomeIdentificacao;
	}
	public String getIdadeIdentificacao() {
		return idadeIdentificacao;
	}
	public void setIdadeIdentificacao(String idadeIdentificacao) {
		this.idadeIdentificacao = idadeIdentificacao;
	}
	public String getDataIdentificacao() {
		return dataIdentificacao;
	}
	public void setDataIdentificacao(String dataIdentificacao) {
		this.dataIdentificacao = dataIdentificacao;
	}
	public String getSexoIdentificacao() {
		return sexoIdentificacao;
	}
	public void setSexoIdentificacao(String sexoIdentificacao) {
		this.sexoIdentificacao = sexoIdentificacao;
	}
	public String getPesoNascimentoIdentificacao() {
		return pesoNascimentoIdentificacao;
	}
	public void setPesoNascimentoIdentificacao(String pesoNascimentoIdentificacao) {
		this.pesoNascimentoIdentificacao = pesoNascimentoIdentificacao;
	}
	public Short getLeitoIdentificacao() {
		return leitoIdentificacao;
	}
	public void setLeitoIdentificacao(Short leitoIdentificacao) {
		this.leitoIdentificacao = leitoIdentificacao;
	}
	public String getProntuarioIdentificacao() {
		return prontuarioIdentificacao;
	}
	public void setProntuarioIdentificacao(String prontuarioIdentificacao) {
		this.prontuarioIdentificacao = prontuarioIdentificacao;
	}
	public String getAlturaNascimentoIdentificacao() {
		return alturaNascimentoIdentificacao;
	}
	public void setAlturaNascimentoIdentificacao(
			String alturaNascimentoIdentificacao) {
		this.alturaNascimentoIdentificacao = alturaNascimentoIdentificacao;
	}
	public String getPressaoArterialMediaFator() {
		return pressaoArterialMediaFator;
	}
	public void setPressaoArterialMediaFator(String pressaoArterialMediaFator) {
		this.pressaoArterialMediaFator = pressaoArterialMediaFator;
	}
	public String getTemperaturaFator() {
		return temperaturaFator;
	}
	public void setTemperaturaFator(String temperaturaFator) {
		this.temperaturaFator = temperaturaFator;
	}
	public String getRazaoPo2Fi2Fator() {
		return razaoPo2Fi2Fator;
	}
	public void setRazaoPo2Fi2Fator(String razaoPo2Fi2Fator) {
		this.razaoPo2Fi2Fator = razaoPo2Fi2Fator;
	}
	public String getPhSangueFator() {
		return phSangueFator;
	}
	public void setPhSangueFator(String phSangueFator) {
		this.phSangueFator = phSangueFator;
	}
	public String getConvulsoesMultiplasFator() {
		return convulsoesMultiplasFator;
	}
	public void setConvulsoesMultiplasFator(String convulsoesMultiplasFator) {
		this.convulsoesMultiplasFator = convulsoesMultiplasFator;
	}
	public String getVolumeUrinarioFator() {
		return volumeUrinarioFator;
	}
	public void setVolumeUrinarioFator(String volumeUrinarioFator) {
		this.volumeUrinarioFator = volumeUrinarioFator;
	}
	public String getPesoNascimentoFator() {
		return pesoNascimentoFator;
	}
	public void setPesoNascimentoFator(String pesoNascimentoFator) {
		this.pesoNascimentoFator = pesoNascimentoFator;
	}
	public String getPigFator() {
		return pigFator;
	}
	public void setPigFator(String pigFator) {
		this.pigFator = pigFator;
	}
	public String getApgarFator() {
		return apgarFator;
	}
	public void setApgarFator(String apgarFator) {
		this.apgarFator = apgarFator;
	}
	public Integer getPressaoArterialEscore() {
		return pressaoArterialEscore;
	}
	public void setPressaoArterialEscore(Integer pressaoArterialEscore) {
		this.pressaoArterialEscore = pressaoArterialEscore;
	}
	public Integer getPressaoArterialMediaEscore() {
		return pressaoArterialMediaEscore;
	}
	public void setPressaoArterialMediaEscore(Integer pressaoArterialMediaEscore) {
		this.pressaoArterialMediaEscore = pressaoArterialMediaEscore;
	}
	public Integer getTemperaturaEscore() {
		return temperaturaEscore;
	}
	public void setTemperaturaEscore(Integer temperaturaEscore) {
		this.temperaturaEscore = temperaturaEscore;
	}
	public Integer getRazaoPo2Fi2Escore() {
		return razaoPo2Fi2Escore;
	}
	public void setRazaoPo2Fi2Escore(Integer razaoPo2Fi2Escore) {
		this.razaoPo2Fi2Escore = razaoPo2Fi2Escore;
	}
	public Integer getPhSangueEscore() {
		return phSangueEscore;
	}
	public void setPhSangueEscore(Integer phSangueEscore) {
		this.phSangueEscore = phSangueEscore;
	}
	public Integer getConvulsoesMultiplasEscore() {
		return convulsoesMultiplasEscore;
	}
	public void setConvulsoesMultiplasEscore(Integer convulsoesMultiplasEscore) {
		this.convulsoesMultiplasEscore = convulsoesMultiplasEscore;
	}
	public Integer getVolumeUrinarioEscore() {
		return volumeUrinarioEscore;
	}
	public void setVolumeUrinarioEscore(Integer volumeUrinarioEscore) {
		this.volumeUrinarioEscore = volumeUrinarioEscore;
	}
	public Integer getPesoNascimentoEscore() {
		return pesoNascimentoEscore;
	}
	public void setPesoNascimentoEscore(Integer pesoNascimentoEscore) {
		this.pesoNascimentoEscore = pesoNascimentoEscore;
	}
	public Integer getPigEscore() {
		return pigEscore;
	}
	public void setPigEscore(Integer pigEscore) {
		this.pigEscore = pigEscore;
	}
	public Integer getApgarEscore() {
		return apgarEscore;
	}
	public void setApgarEscore(Integer apgarEscore) {
		this.apgarEscore = apgarEscore;
	}
	public Short getTotalEscore() {
		return totalEscore;
	}
	public void setTotalEscore(Short totalEscore) {
		this.totalEscore = totalEscore;
	}
	public String getInfResponsavel() {
		return infResponsavel;
	}
	public void setInfResponsavel(String infResponsavel) {
		this.infResponsavel = infResponsavel;
	}
	public String getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {
		
		NOME_IDENTIFICACAO("nomeIdentificacao"), 
		IDADE_IDENTIFICACAO("idadeIdentificacao"), 
		DATA_IDENTIFICACAO("dataIdentificacao"), 
		SEXO_IDENTIFICACAO("sexoIdentificacao"),
		PESO_NASCIMENTO_IDENTIFICACAO("pesoNascimentoIdentificacao"), 
		LEITO_IDENTIFICACAO("leitoIdentificacao"), 
		PRONTUARIO("prontuario"),
		PRONTUARIO_IDENTIFICACAO("prontuarioIdentificacao"),
		ALTURA_NASCIMENTO_IDENTIFICACAO("alturaNascimentoIdentificacao"), 
		PRESSAO_ARTERIAL_MEDIA_FATOR("pressaoArterialMediaFator"), 
		TEMPERATURA_FATOR("temperaturaFator"), 
		RAZAO_PO2_FI2_FATOR("razaoPo2Fi2Fator"), 
		PH_SANGUE_FATOR("phSangueFator"), 
		CONVULSOES_MULTIPLAS_FATOR("convulsoesMultiplasFator"), 
		VOLUME_URINARIO_FATOR("volumeUrinarioFator"), 
		PESO_NASCIMENTO_FATOR("pesoNascimentoFator"), 
		PIG_FATOR("pigFator"), 
		APGAR_FATOR("apgarFator"), 
		PRESSAO_ARTERIAL_ESCORE("pressaoArterialEscore"), 
		PRESSAO_ARTERIAL_MEDIA_ESCORE("pressaoArterialMediaEscore"), 
		TEMPERATURA_ESCORE("temperaturaEscore"), 
		RAZAO_PO2_FI2_ESCORE("razaoPo2Fi2Escore"), 
		PH_SANGUE_ESCORE("phSangueEscore"), 
		CONVULSOES_MULTIPLAS_ESCORE("convulsoesMultiplasEscore"), 
		VOLUME_URINARIO_ESCORE("volumeUrinarioEscore"), 
		PESO_NASCIMENTO_ESCORE("pesoNascimentoEscore"), 
		PIG_ESCORE("pigEscore"), 
		APGAR_ESCORE("apgarEscore"), 
		TOTAL_ESCORE("totalEscore"), 
		INF_RESPONSAVEL("infResponsavel"),
		DATA_NASCIMENTO("dataNascimento"),
		DT_NASCIMENTO("dtNascimento"),
		CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
