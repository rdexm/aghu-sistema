package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoColeta;

public class RelatorioMapaHematologiaVO {

	private Integer soeSeq;
	private Integer nroMapa;
	private Integer nroInterno;

	private Integer amoSoeSeq;
	private Integer amoSeqp;
	private Integer iseSoeSeq;
	private Integer iseSeqp;
	private Short unfSeq;
	
	private String prontuario;	
	private String  paciente;
	private String  origem;
	private String  material;
	private Date    dthrEvento;
	
	private String infoClinicas;
	private String exame;
	private String idade;
	private String quarto;
	
	private String cpUnfDesc;
	
	private DominioTipoColeta tipoColeta;

	public Integer getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Number amoSeqp) {

		/* Fiz isso pois estava estourando uma excpetion de parametro illegal, 
		 * certas vezes recebia um Integer e o método esperava um short,
		 * o contrário também. Ver possibilidade de remover isso. 
		 */
		if(amoSeqp != null){
			if(amoSeqp instanceof Integer){
				this.amoSeqp = (Integer) amoSeqp;
			} else {
				this.amoSeqp = Integer.valueOf(amoSeqp.toString());
			}
		} else {
			amoSeqp = null;
		}
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

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

	public Integer getAmoSoeSeq() {
		return amoSoeSeq;
	}

	public void setAmoSoeSeq(Integer amoSoeSeq) {
		this.amoSoeSeq = amoSoeSeq;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Integer getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Integer iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
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

	public String getInfoClinicas() {
		return infoClinicas;
	}

	public void setInfoClinicas(String infoClinicas) {
		this.infoClinicas = infoClinicas;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public String getCpUnfDesc() {
		return cpUnfDesc;
	}

	public void setCpUnfDesc(String cpUnfDesc) {
		this.cpUnfDesc = cpUnfDesc;
	}

	public String getTipoColeta() {
		return (tipoColeta!=null)?tipoColeta.getDescricao():null;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public enum Fields {
		SOE_SEQ("soeSeq"),
		NRO_MAPA("nroMapa"),
		NRO_INTERNO("nroInterno"),
		AMO_SOE_SEQ("amoSoeSeq"),
		AMO_SEQP("amoSeqp"),	
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQP("iseSeqp"),
		PRONTUARIO("prontuario"),
		PACIENTE("paciente"),
		ORIGEM("origem"),
		MATERIAL("material"),
		DTHR_EVENTO("dthrEvento"), 
		UNF_SEQ("unfSeq"),
		EXAME("exame"), 
		INFORMACOES_CLINICAS("infoClinicas"),
		TIPO_COLETA("tipoColeta");
		
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
