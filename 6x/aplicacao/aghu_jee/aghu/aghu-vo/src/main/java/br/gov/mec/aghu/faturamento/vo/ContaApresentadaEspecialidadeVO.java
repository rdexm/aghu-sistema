package br.gov.mec.aghu.faturamento.vo;

import java.util.List;

public class ContaApresentadaEspecialidadeVO{
	private String especialidade; 
	private Short seqEspecialidade; 
	private Integer totalAihEspecialidade;
	private String totalEspecialidade; 
	private List<ContaApresentadaPacienteProcedimentoVO> listaContaApresentadaPacienteProcedimentoVO;
	
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public Integer getTotalAihEspecialidade() {
		return totalAihEspecialidade;
	}
	public void setTotalAihEspecialidade(Integer totalAihEspecialidade) {
		this.totalAihEspecialidade = totalAihEspecialidade;
	}
	public String getTotalEspecialidade() {
		return totalEspecialidade;
	}
	public void setTotalEspecialidade(String  totalEspecialidade) {
		this.totalEspecialidade = totalEspecialidade;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public List<ContaApresentadaPacienteProcedimentoVO> getListaContaApresentadaPacienteProcedimentoVO() {
		return listaContaApresentadaPacienteProcedimentoVO;
	}
	public void setListaContaApresentadaPacienteProcedimentoVO(List<ContaApresentadaPacienteProcedimentoVO> listaContaApresentadaPacienteProcedimentoVO) {
		this.listaContaApresentadaPacienteProcedimentoVO = listaContaApresentadaPacienteProcedimentoVO;
	}
}