package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;

public class ReInternacoesVO implements Comparable<Integer> {

	// int.pac_codigo pac_cod,
	private Integer paccodigo;

	// pac.prontuario ,
	private Integer prontuario;

	// int.seq int_seq,
	private Integer intseq;

	// cth.seq cth_seq,
	private Integer cthseq;

	// dt_int_administrativa,
	private Date datainternacaoadministrativa;

	// dt_alta_administrativa
	private Date dtaltaadministrativa;

	// cth.ind_situacao
	private String indsituacao;

	
	public Integer getPaccodigo() {
		return paccodigo;
	}

	public void setPaccodigo(Integer paccodigo) {
		this.paccodigo = paccodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getIntseq() {
		return intseq;
	}

	public void setIntseq(Integer intseq) {
		this.intseq = intseq;
	}

	public Integer getCthseq() {
		return cthseq;
	}

	public void setCthseq(Integer cthseq) {
		this.cthseq = cthseq;
	}

	public Date getDatainternacaoadministrativa() {
		return datainternacaoadministrativa;
	}

	public void setDatainternacaoadministrativa(
			Date datainternacaoadministrativa) {
		this.datainternacaoadministrativa = datainternacaoadministrativa;
	}

	public Date getDtaltaadministrativa() {
		return dtaltaadministrativa;
	}

	public void setDtaltaadministrativa(Date dtaltaadministrativa) {
		this.dtaltaadministrativa = dtaltaadministrativa;
	}

	public String getIndsituacao() {
		return DominioSituacaoConta.valueOf(indsituacao).getDescricao();
	}

	public void setIndsituacao(String indsituacao) {
		this.indsituacao = indsituacao;
	}

	@Override
	public int compareTo(Integer o) {
		if(o != null && this.getPaccodigo() != null){
			return this.getPaccodigo().compareTo( o );
		}
		
		return 0;
	}

}