package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ProtocoloEntregaNotasDeConsumoVO implements Serializable{

	private static final long serialVersionUID = -6888112874668516297L;
	
	private Integer crgSeq;
	private Short numeroAgenda;
	private String nome;
	private Integer prontuario;
	private DominioSituacaoCirurgia situacao;
	private Boolean digitaNotaSala;
	
	public ProtocoloEntregaNotasDeConsumoVO() {
		super();
	}
	
	public ProtocoloEntregaNotasDeConsumoVO(Integer crgSeq,
											Short numeroAgenda,
											String nome,
											Integer prontuario,
											DominioSituacaoCirurgia situacao,
											Boolean digitaNotaSala){
		super();
		this.crgSeq = crgSeq;
		this.numeroAgenda = numeroAgenda;
		this.nome = nome;
		this.prontuario = prontuario;
		this.situacao = situacao;
		this.digitaNotaSala = digitaNotaSala;
	}
	
	public enum Fields {
		
		CRG_SEQ("crgSeq"),
		NUMERO_AGENDA("numeroAgenda"),
		NOME("nome"),
		PRONTUARIO("prontuario"),
		SITUACAO("situacao"),
		IND_DIGT_NOTA_SALA("digitaNotaSala");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	//Getters and Setters

	public Short getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getProntuario() {
		return CoreUtil.formataProntuarioRelatorio(prontuario);
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioSituacaoCirurgia getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}

	public String getDigitaNotaSala() {
		if (digitaNotaSala) {
			return "S";
		} else {
			return "N";
		}
		
	}

	public void setDigitaNotaSala(Boolean digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

}
