package br.gov.mec.aghu.faturamento.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoProcedimento;

public class ProdutividadeFisiatriaVO {
	
	private Integer atendimento;
	
	private Date criadoEm;
	
	private String fisioterapeuta;
	
	private String medicoSolicitante;
		
	private Integer seqProcedimento;
	
	private String descricaoProcedimento;
	
	private String andarAla;
	
	private DominioTipoProcedimento tipoProcedimento;
	
	private Date dataRealizado;

	public Integer getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Integer atendimento) {
		this.atendimento = atendimento;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getFisioterapeuta() {
		return fisioterapeuta;
	}

	public void setFisioterapeuta(String fisioterapeuta) {
		this.fisioterapeuta = fisioterapeuta;
	}

	public String getMedicoSolicitante() {
		return medicoSolicitante;
	}

	public void setMedicoSolicitante(String medicoSolicitante) {
		this.medicoSolicitante = medicoSolicitante;
	}

	public Integer getSeqProcedimento() {
		return seqProcedimento;
	}

	public void setSeqProcedimento(Integer seqProcedimento) {
		this.seqProcedimento = seqProcedimento;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public String getAndarAla() {
		return andarAla;
	}

	public void setAndarAla(String andarAla) {
		this.andarAla = andarAla;
	}

	public DominioTipoProcedimento getTipoProcedimento() {
		return tipoProcedimento;
	}

	public void setTipoProcedimento(DominioTipoProcedimento tipoProcedimento) {
		this.tipoProcedimento = tipoProcedimento;
	}

	public Date getDataRealizado() {
		return dataRealizado;
	}

	public void setDataRealizado(Date dataRealizado) {
		this.dataRealizado = dataRealizado;
	}
	
	public String getCriadoEmFormatado() {
		
		if (this.criadoEm != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");			
			return sdf.format(this.criadoEm);
		}
		
		return "";
	}
	
	public String getDataRealizadoFormatado() {
		
		if (this.dataRealizado != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
			return sdf.format(this.dataRealizado);
		}
		
		return "";
	}
	
}
