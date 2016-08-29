package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;

public class SumarioQuimioPOLVO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7629433709683809562L;
	

	private Integer apaAtdSeq;
	private Integer apaSeq;
	private String pacNome;
	private DominioSexo pacSexo;
	private Integer pacProntuario;
	private String sessao;
	private String atdLtoId;
	private Short atdQrtNumero;
	private Short atdUnfSeq;
	private Date atdDthrInicioTrat;
	private Date atdDthrFimTrat;
	private Date pacDtNascimento;
	private Date dthrInternacao;
	private Date intDthrAlta;
	private String pesNome; //Equipe
	private String espNomeEspecialidade;//Especialidade
	private String equipeEsp;
	private String ltoLtoId;
	private String unidade;
	
	//Atributos que não fazem parte do distinct
	private Integer atdIntSeq;
	private Integer atdAtuSeq;
	private Integer atdHodSeq;
	private Short serVinCodigo;
	private Integer serMatricula;
	private String pacProntuarioFormatado;
	
	public enum Fields {
		APA_ATD_SEQ("apaAtdSeq"),
		APA_SEQ("apaSeq"),
		PAC_NOME("pacNome"),
		PAC_SEXO("pacSexo"),
		PAC_PRONTUARIO("pacProntuario"),
		ATD_LTO_ID("atdLtoId"),
		ATD_QRT_NUMERO("atdQrtNumero"),
		ATD_UNF_SEQ("atdUnfSeq"),
		ATD_DTHR_INICIO_TRAT("atdDthrInicioTrat"),
		ATD_DTHR_FIM_TRAT("atdDthrFimTrat"),
		PAC_DT_NASCIMENTO("pacDtNascimento"),
		INT_DTHR_ALTA("intDthrAlta"),
		PES_NOME("pesNome"),
		ESP_NOME_ESPECIALIDADE("espNomeEspecialidade"),
		ATD_INT_SEQ("atdIntSeq"),
		ATD_ATU_SEQ("atdAtuSeq"),
		ATD_HOD_SEQ("atdHodSeq"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	// Getters and Setters

	public Integer getApaAtdSeq() {
		return apaAtdSeq;
	}

	public void setApaAtdSeq(Integer apaAtdSeq) {
		this.apaAtdSeq = apaAtdSeq;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public DominioSexo getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(DominioSexo pacSexo) {
		this.pacSexo = pacSexo;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getSessao() {
		return sessao;
	}

	public void setSessao(String sessao) {
		this.sessao = sessao;
	}

	public String getAtdLtoId() {
		return atdLtoId;
	}

	public void setAtdLtoId(String atdLtoId) {
		this.atdLtoId = atdLtoId;
	}

	public Short getAtdQrtNumero() {
		return atdQrtNumero;
	}

	public void setAtdQrtNumero(Short atdQrtNumero) {
		this.atdQrtNumero = atdQrtNumero;
	}

	public Short getAtdUnfSeq() {
		return atdUnfSeq;
	}

	public void setAtdUnfSeq(Short atdUnfSeq) {
		this.atdUnfSeq = atdUnfSeq;
	}

	public Date getAtdDthrInicioTrat() {
		return atdDthrInicioTrat;
	}

	public void setAtdDthrInicioTrat(Date atdDthrInicioTrat) {
		this.atdDthrInicioTrat = atdDthrInicioTrat;
	}

	public Date getAtdDthrFimTrat() {
		return atdDthrFimTrat;
	}

	public void setAtdDthrFimTrat(Date atdDthrFimTrat) {
		this.atdDthrFimTrat = atdDthrFimTrat;
	}

	public Date getPacDtNascimento() {
		return pacDtNascimento;
	}

	public void setPacDtNascimento(Date pacDtNascimento) {
		this.pacDtNascimento = pacDtNascimento;
	}

	public Date getDthrInternacao() {
		return dthrInternacao;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public Date getIntDthrAlta() {
		return intDthrAlta;
	}

	public void setIntDthrAlta(Date intDthrAlta) {
		this.intDthrAlta = intDthrAlta;
	}

	public String getPesNome() {
		return pesNome;
	}

	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}

	public String getEspNomeEspecialidade() {
		return espNomeEspecialidade;
	}

	public void setEspNomeEspecialidade(String espNomeEspecialidade) {
		this.espNomeEspecialidade = espNomeEspecialidade;
	}

	public String getEquipeEsp() {
		return equipeEsp;
	}

	public void setEquipeEsp(String equipeEsp) {
		this.equipeEsp = equipeEsp;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Integer getAtdIntSeq() {
		return atdIntSeq;
	}

	public void setAtdIntSeq(Integer atdIntSeq) {
		this.atdIntSeq = atdIntSeq;
	}

	public Integer getAtdAtuSeq() {
		return atdAtuSeq;
	}

	public void setAtdAtuSeq(Integer atdAtuSeq) {
		this.atdAtuSeq = atdAtuSeq;
	}

	public Integer getAtdHodSeq() {
		return atdHodSeq;
	}

	public void setAtdHodSeq(Integer atdHodSeq) {
		this.atdHodSeq = atdHodSeq;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public void setPacProntuarioFormatado(String pacProntuarioFormatado) {
		this.pacProntuarioFormatado = pacProntuarioFormatado;
	}

	public String getPacProntuarioFormatado() {
		return pacProntuarioFormatado;
	}
}