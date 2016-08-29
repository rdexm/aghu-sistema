package br.gov.mec.aghu.paciente.prontuario.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AltaObitoSumarioVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4503852307002880912L;
	
	private Boolean indObito;
	private Integer apaSeq;
	private Integer apaAtdSeq;
	private Short seqp;
	private String leito;
    private String nome;
    private Integer prontuario;
    private Short quarto;
    private AghUnidadesFuncionais unidadeFuncional;
    private Date dthrFim;
	   
	public enum Fields {
		IND_OBITO("indObito"), 
		APA_SEQ("apaSeq"),
		APA_ATD_SEQ("apaAtdSeq"),
		SEQP("seqp"),
		LTO_LTO_ID("leito"),
		NOME("nome"),
		PRONTUARIO("prontuario"),
		QRT_NUMERO("quarto"),
		UNF_SEQ("unidadeFuncional"),
		DTHR_FIM("dthrFim");

			private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return this.fields;
			}

		}


	public Boolean getIndObito() {
		return indObito;
	}


	public void setIndObito(Boolean indObito) {
		this.indObito = indObito;
	}


	public Integer getApaSeq() {
		return apaSeq;
	}


	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}


	public Integer getApaAtdSeq() {
		return apaAtdSeq;
	}


	public void setApaAtdSeq(Integer apaAtdSeq) {
		this.apaAtdSeq = apaAtdSeq;
	}


	public Short getSeqp() {
		return seqp;
	}


	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}


	public String getLeito() {
		return leito;
	}


	public void setLeito(String leito) {
		this.leito = leito;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Short getQuarto() {
		return quarto;
	}


	public void setQuarto(Short quarto) {
		this.quarto = quarto;
	}


	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}


	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public Date getDthrFim() {
		return dthrFim;
	}


	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}	
	
}
