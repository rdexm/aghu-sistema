package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


public class AltasAmbulatoriasPolVO implements BaseBean{
	private static final long serialVersionUID = -5450524202483991447L;
	
	private Integer seq;
	private Integer prontuario;
	private Date dtHrInicio;
	private Integer intSeq;
	private Integer apqSeq;
	private Integer numero;
	private Integer pacCodigo;
	private Date dtConsulta;
	private Integer grdSeq;
	private Short grdSerVinCodigo;
	private Integer grdSerMatricula;
	private Integer eqpSeq;
	private Short eqpSerVinCodigo;
	private Integer eqpSerMatricula;
	private Short espSeq;
	
	private String espNomeEspecialidade;
	private Date dataOrd;
	private String espNomeAgenda;
	private Short espSeqPai;
	
	private String textoAltaCompleto;
	
	private String nomeProfissional;
	
	//Varíaveis de controle da função p_visualiza_alta
	private String vAssinado;
	private Long vAlta;
	
	public enum Fields {
	  SEQ("seq"), 
	  PRONTUARIO("prontuario"),
	  DTHR_INICIO("dtHrInicio"),
	  INT_SEQ("intSeq"),
	  APE_SEQ("apqSeq"),
	  NUMERO("numero"),
	  PAC_CODIGO("pacCodigo"),
	  DT_CONSULTA("dtConsulta"),
	  GRD_SEQ("grdSeq"),
	  GRD_SER_VIN_CODIGO("grdSerVinCodigo"),
	  GRD_SER_MATRICULA("grdSerMatricula"),
	  EQP_SEQ("eqpSeq"),
	  EQP_SER_VIN_CODIGO("eqpSerVinCodigo"),
	  EQP_SER_MATRICULA("eqpSerMatricula"),
	  ESP_SEQ("espSeq"),
	  ESP_NOME_ESPECIALIDADE("espNomeEspecialidade"),
	  DATA_ORD("dataOrd"),
	  ESP_NOME_AGENDA("espNomeAgenda"),
	  ESP_SEQ_PAI("espSeqPai");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	public Integer getSeq() {
		return seq;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Date getDtHrInicio() {
		return dtHrInicio;
	}


	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}


	public Integer getIntSeq() {
		return intSeq;
	}


	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}


	public Integer getApqSeq() {
		return apqSeq;
	}


	public void setApqSeq(Integer apqSeq) {
		this.apqSeq = apqSeq;
	}


	public Integer getNumero() {
		return numero;
	}


	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public Date getDtConsulta() {
		return dtConsulta;
	}


	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}


	public Integer getGrdSeq() {
		return grdSeq;
	}


	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}


	public Short getGrdSerVinCodigo() {
		return grdSerVinCodigo;
	}


	public void setGrdSerVinCodigo(Short grdSerVinCodigo) {
		this.grdSerVinCodigo = grdSerVinCodigo;
	}


	public Integer getGrdSerMatricula() {
		return grdSerMatricula;
	}


	public void setGrdSerMatricula(Integer grdSerMatricula) {
		this.grdSerMatricula = grdSerMatricula;
	}


	public Integer getEqpSeq() {
		return eqpSeq;
	}


	public void setEqpSeq(Integer eqpSeq) {
		this.eqpSeq = eqpSeq;
	}


	public Short getEqpSerVinCodigo() {
		return eqpSerVinCodigo;
	}


	public void setEqpSerVinCodigo(Short eqpSerVinCodigo) {
		this.eqpSerVinCodigo = eqpSerVinCodigo;
	}


	public Integer getEqpSerMatricula() {
		return eqpSerMatricula;
	}


	public void setEqpSerMatricula(Integer eqpSerMatricula) {
		this.eqpSerMatricula = eqpSerMatricula;
	}


	public Short getEspSeq() {
		return espSeq;
	}


	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}


	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apqSeq == null) ? 0 : apqSeq.hashCode());
		result = prime * result + ((eqpSeq == null) ? 0 : eqpSeq.hashCode());
		result = prime * result
				+ ((eqpSerMatricula == null) ? 0 : eqpSerMatricula.hashCode());
		result = prime * result
				+ ((eqpSerVinCodigo == null) ? 0 : eqpSerVinCodigo.hashCode());
		result = prime * result
				+ ((espNomeAgenda == null) ? 0 : espNomeAgenda.hashCode());
		result = prime
				* result
				+ ((espNomeEspecialidade == null) ? 0 : espNomeEspecialidade
						.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}


	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof AltasAmbulatoriasPolVO)){
			return false;
		}	
		AltasAmbulatoriasPolVO other = (AltasAmbulatoriasPolVO) obj;
		if (apqSeq == null) {
			if (other.apqSeq != null){
				return false;
			}	
		} else if (!apqSeq.equals(other.apqSeq)){
			return false;
		}	
		if (eqpSeq == null) {
			if (other.eqpSeq != null){
				return false;
			}	
				
		} else if (!eqpSeq.equals(other.eqpSeq)){
			return false;
		}	
		if (eqpSerMatricula == null) {
			if (other.eqpSerMatricula != null){
				return false;
			}	
		} else if (!eqpSerMatricula.equals(other.eqpSerMatricula)){
			return false;
		}	
		if (eqpSerVinCodigo == null) {
			if (other.eqpSerVinCodigo != null){
				return false;
			}	
		} else if (!eqpSerVinCodigo.equals(other.eqpSerVinCodigo)){
			return false;
		}	
		if (espNomeAgenda == null) {
			if (other.espNomeAgenda != null){
				return false;
			}	
		} else if (!espNomeAgenda.equals(other.espNomeAgenda)){
			return false;
		}	
		if (espNomeEspecialidade == null) {
			if (other.espNomeEspecialidade != null){
				return false;
			}	
		} else if (!espNomeEspecialidade.equals(other.espNomeEspecialidade)){
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


	public String getEspNomeEspecialidade() {
		return espNomeEspecialidade;
	}


	public void setEspNomeEspecialidade(String espNomeEspecialidade) {
		this.espNomeEspecialidade = espNomeEspecialidade;
	}


	public Date getDataOrd() {
		return dataOrd;
	}


	public void setDataOrd(Date dataOrd) {
		this.dataOrd = dataOrd;
	}


	public String getEspNomeAgenda() {
		return espNomeAgenda;
	}


	public void setEspNomeAgenda(String espNomeAgenda) {
		this.espNomeAgenda = espNomeAgenda;
	}


	public Short getEspSeqPai() {
		return espSeqPai;
	}


	public void setEspSeqPai(Short espSeqPai) {
		this.espSeqPai = espSeqPai;
	}

	public String getTextoAltaCompleto() {
		return textoAltaCompleto;
	}


	public void setTextoAltaCompleto(String textoAltaCompleto) {
		this.textoAltaCompleto = textoAltaCompleto;
	}


	public String getNomeProfissional() {
		return nomeProfissional;
	}


	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}


	public String getvAssinado() {
		return vAssinado;
	}


	public void setvAssinado(String vAssinado) {
		this.vAssinado = vAssinado;
	}


	public Long getvAlta() {
		return vAlta;
	}


	public void setvAlta(Long vAlta) {
		this.vAlta = vAlta;
	}
}
