package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class GradeConsultasVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1072544585714693926L;

	private static final String hifen = "-";

	private Date dtConsulta;
	private String diaSemanaConsulta;
	private Boolean selecionado = Boolean.FALSE;
	private String condAtendimento;
	private String situacaoConsulta;
	private Integer numeroConsulta;
	private Integer prontuarioPaciente;
	private Integer codigoPaciente;
	private String nomePaciente;
	private String descricaoRetorno;
	private Date dtAlteradaConsulta;
	private String nomeServidor;
	
	public enum Fields {
		DATA_CONSULTA("dtConsulta"),
		DIA_SEMANA_CONSULTA("diaSemanaConsulta"),
		DESCRICAO_COND_ATENDIMENTO("condAtendimento"),
		SITUACAO_CONSULTA("situacaoConsulta"),
		NUMERO_CONSULTA("numeroConsulta"),
		PRONTUARIO_PAC("prontuarioPaciente"),
		CODIGO_PAC("codigoPaciente"),
		NOME_PAC("nomePaciente"),
		DESCRICAO_RETORNO("descricaoRetorno"),
		DATA_ALTERADA_CONSULTA("dtAlteradaConsulta"),
		NOME_SERVIDOR("nomeServidor")
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

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	
	public String getPacienteFormatado(){
		StringBuilder pacienteFormat = new StringBuilder();
		if(getCodigoPaciente()!= null){
			pacienteFormat.append(getCodigoPaciente()+hifen);
		}
		if(getNomePaciente() != null){
			if(getNomePaciente().length() > 25){
				pacienteFormat.append(getNomePaciente().substring(0, 25).concat("..."));
			}else{
				pacienteFormat.append(getNomePaciente());
			}
		}
		return pacienteFormat.toString();
	}

	public String getDiaSemanaConsulta() {
		Calendar diaSemana = Calendar.getInstance();
		diaSemana.setTime(dtConsulta);
		Integer dia = diaSemana.get(Calendar.DAY_OF_WEEK);
		diaSemanaConsulta = getDescricaoAbrev(dia);
		return diaSemanaConsulta;
	}
	
	public String getDescricaoAbrev(Integer value) {
			
		switch (value) {
		case 1:
			return "DOM";
		case 2:
			return "SEG";
		case 3:
			return "TER";
		case 4:
			return "QUA";
		case 5:
			return "QUI";
		case 6:
			return "SEX";
		case 7:
			return "SAB";
		default:
			return "";
		}
	}

	public String getCondAtendimento() {
		return condAtendimento;
	}

	public void setCondAtendimento(String condAtendimento) {
		this.condAtendimento = condAtendimento;
	}

	public String getSituacaoConsulta() {
		return situacaoConsulta;
	}

	public void setSituacaoConsulta(String situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	
	public String getProtuarioPacienteString(){
		if(getProntuarioPaciente() != null){
			if(String.valueOf(getProntuarioPaciente()).length() > 1){
				String palavra = getProntuarioPaciente().toString();				
				char letra = palavra.charAt(palavra.length() - 1);
				return (palavra.substring(0, palavra.length() - 1) + "/" + letra);	
			}
			else{
				return String.valueOf(getProntuarioPaciente());
			}
		}
		return null;
	}
	
	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getDescricaoRetorno() {
		return descricaoRetorno;
	}

	public void setDescricaoRetorno(String descricaoRetorno) {
		this.descricaoRetorno = descricaoRetorno;
	}

	public Date getDtAlteradaConsulta() {
		return dtAlteradaConsulta;
	}

	public void setDtAlteradaConsulta(Date dtAlteradaConsulta) {
		this.dtAlteradaConsulta = dtAlteradaConsulta;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((numeroConsulta == null) ? 0 : numeroConsulta.hashCode());
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
		GradeConsultasVO other = (GradeConsultasVO) obj;
		if (numeroConsulta == null) {
			if (other.numeroConsulta != null){
				return false;
			}
		} else if (!numeroConsulta.equals(other.numeroConsulta)){
			return false;
		}
		return true;
	}

	public void setDiaSemanaConsulta(String diaSemanaConsulta) {
		this.diaSemanaConsulta = diaSemanaConsulta;
	}
}
