package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;

public class CidAtendimentoVO {
	
	private Integer atdSeq; // atd.seq,
	private DominioPacAtendimento indPacAtendimento; // atd.ind_pac_atendimento,
	private DominioOrigemAtendimento origem; // atd.origem,
	private Integer cidSeq; //cia.cid_seq


	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}
	
	public enum Fields {

		ATD_SEQ("atdSeq"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		ORIGEM("origem"),
		CID_SEQ("cidSeq");

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
