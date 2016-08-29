package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class RelatorioMapaUroculturaVO implements Serializable {
	
	private static final long serialVersionUID = -5583530048966155556L;
	
	private Integer soeSeq;
	private Integer nroMapa;
	private Integer nroInterno;

	private Integer amoSoeSeq;
	private Integer amoSeqp;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Short unfSeq;
	
	private String  material;
	private Date    dthrEvento;
	private Date    dthrEvento1;
	private String  anticoagulante;
	
	private String cpUnfDesc;
	
	private String nroFrascoFabricante;
	private Integer ufeEmaManSeq;
	private String paciente;
	private String sexo;
	private Integer idade;
	private String informacoesClinicas;
	
	public Integer getNroMapa() {
		return nroMapa;
	}

	public void setNroMapa(Integer nroMapa) {
		this.nroMapa = nroMapa;
	}

	public Integer getNroInterno() {
		return nroInterno;
	}

	public void setNroInterno(Integer nroInterno) {
		this.nroInterno = nroInterno;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Date getDthrEvento() {
		return dthrEvento;
	}

	public void setDthrEvento(Date dthrEvento) {
		this.dthrEvento = dthrEvento;
	}

	public Date getDthrEvento1() {
		return dthrEvento1;
	}

	public void setDthrEvento1(Date dthrEvento1) {
		this.dthrEvento1 = dthrEvento1;
	}

	public String getAnticoagulante() {
		return anticoagulante;
	}

	public void setAnticoagulante(String anticoagulante) {
		this.anticoagulante = anticoagulante;
	}



	public Integer getAmoSoeSeq() {
		return amoSoeSeq;
	}

	public void setAmoSoeSeq(Integer amoSoeSeq) {
		this.amoSoeSeq = amoSoeSeq;
	}

	public Integer getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Integer amoSeqp) {
		this.amoSeqp = amoSeqp;
	}
	
	public String getSolicitacaoAmostra() {
		NumberFormat fSolicit = new DecimalFormat("0000000");
		NumberFormat fAmostra = new DecimalFormat("000");

		StringBuffer retorno = new StringBuffer();
		retorno.append(fSolicit.format(amoSoeSeq))
		.append('/')
		.append(fAmostra.format(amoSeqp));
		
		return retorno.toString();
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getCpUnfDesc() {
		return cpUnfDesc;
	}

	public void setCpUnfDesc(String cpUnfDesc) {
		this.cpUnfDesc = cpUnfDesc;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public String getNroFrascoFabricante() {
		return nroFrascoFabricante;
	}

	public void setNroFrascoFabricante(String nroFrascoFabricante) {
		this.nroFrascoFabricante = nroFrascoFabricante;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public String getPaciente() {
		if (paciente.length() > 30) {
			return paciente.substring(0, 30);
		}
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public String getInformacoesClinicas() {
		if (informacoesClinicas != null && informacoesClinicas.length() > 375) {
			return informacoesClinicas.substring(0, 375);
		}
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}
	
	public enum Fields {
		NRO_MAPA("nroMapa"),
		NRO_INTERNO("nroInterno"),
		AMO_SOE_SEQ("amoSoeSeq"),
		AMO_SEQP("amoSeqp"),	
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQP("iseSeqp"),
		ORIGEM("origem"),
		MATERIAL("material"),
		DTHR_EVENTO("dthrEvento"), 
		UNF_SEQ("unfSeq"), 
		DTHR_EVENTO1("dthrEvento1"),
		ANTICOAGULANTE("anticoagulante"),
		TIPO_COLETA("tipoColeta"),
		NRO_FRASCO_FABRICANTE("nroFrascoFabricante"),
		UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
		PACIENTE("paciente"),
		SEXO("sexo"),
		IDADE("idade"),
		INFORMACOES_CLINICAS("informacoesClinicas")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
}
