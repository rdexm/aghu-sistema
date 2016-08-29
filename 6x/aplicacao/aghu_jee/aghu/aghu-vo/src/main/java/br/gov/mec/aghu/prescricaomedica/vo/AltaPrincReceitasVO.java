package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class AltaPrincReceitasVO implements Serializable {

	private static final long serialVersionUID = -8867762877253896301L;
	
	private String descricao;
	private String quantidade;
	private String descReceita;
	private Short asuSeqp;
	private DominioSimNao indInterno;
	private DominioSimNao indUsoContinuo;
	private DominioSituacao indSituacao;
	private String indCarga;
	private String receita; 
	private Integer apaAtdSeq;
	private Integer apaSeq;
	private Short seqp;
	
	
	
	public enum Fields {
		DESCRICAO("descricao"),
		IND_INTERNO("indInterno"),
		IND_USO_CONTINUO("indUsoContinuo"),
		DESC_RECEITA("descReceita"),
		ASU_SEQP("asuSeqp"),
		ASU_APA_SEQ("apaSeq"), 
		ASU_APA_ATD_SEQ("apaAtdSeq"),
		SEQP("seqp"),
		IND_CARGA("indCarga"),
		IND_SITUACAO("indSituacao"),
		QUANTIDADE("quantidade");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}
	public DominioSimNao getIndInterno() {
		return indInterno;
	}
	public void setIndInterno(DominioSimNao indInterno) {
		this.indInterno = indInterno;
	}
	public DominioSimNao getIndUsoContinuo() {
		return indUsoContinuo;
	}
	public void setIndUsoContinuo(DominioSimNao indUsoContinuo) {
		this.indUsoContinuo = indUsoContinuo;
	}
	public String getDescReceita() {
		return descReceita;
	}
	public void setDescReceita(String descReceita) {
		this.descReceita = descReceita;
	}
	public Short getAsuSeqp() {
		return asuSeqp;
	}
	public void setAsuSeqp(Short asuSeqp) {
		this.asuSeqp = asuSeqp;
	}
	public String getIndCarga() {
		return indCarga;
	}
	public void setIndCarga(String indCarga) {
		this.indCarga = indCarga;
	}
	public String getReceita() {
	String interno = " -- Uso Interno";
	String continuo = " -- Uso Continuo";	
	String traco = " -- ";
		if(this.quantidade != null){
			if(this.indInterno == DominioSimNao.S && this.indUsoContinuo == DominioSimNao.S){
				this.setReceita(this.descricao.concat(traco).concat(this.quantidade).concat(interno).concat(continuo));
			}else if(this.indInterno == DominioSimNao.S && this.indUsoContinuo != DominioSimNao.S){
				this.setReceita(this.descricao.concat(traco).concat(this.quantidade).concat(interno));
			}else if(this.indInterno != DominioSimNao.S && this.indUsoContinuo == DominioSimNao.S){
				this.setReceita(this.descricao.concat(traco).concat(this.quantidade).concat(continuo));
			}else if(this.indInterno != DominioSimNao.S && this.indUsoContinuo != DominioSimNao.S){
				this.setReceita(this.descricao.concat(traco).concat(this.quantidade));
			}
		}else{
			if(this.indInterno == DominioSimNao.S && this.indUsoContinuo == DominioSimNao.S){
				this.setReceita(this.descricao.concat(interno).concat(continuo));
			}else if(this.indInterno == DominioSimNao.S && this.indUsoContinuo != DominioSimNao.S){
				this.setReceita(this.descricao.concat(interno));
			}else if(this.indInterno != DominioSimNao.S && this.indUsoContinuo == DominioSimNao.S){
				this.setReceita(this.descricao.concat(continuo));
			}else if(this.indInterno != DominioSimNao.S && this.indUsoContinuo != DominioSimNao.S){
				this.setReceita(this.descricao);
		}
	}
		return receita;
}
	
	
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
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public void setReceita(String receita) {
		this.receita = receita;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.descReceita;
		}
		
}
