package br.gov.mec.aghu.sig.custos.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CompetenciaVO implements BaseBean {
	
	private static final long serialVersionUID = 727144202923432036L;
	
	private Integer processamentoSeq;
	private Date data;
	private SimpleDateFormat sdf;
	
	public CompetenciaVO(){
		this.data = new Date();
		sdf = new SimpleDateFormat("dd/MM/yyyy");
	}
	
	public CompetenciaVO(Date data, String formadoDaData){
		this.data = data;
		if(StringUtils.isNotBlank(formadoDaData)){
			sdf = new SimpleDateFormat(formadoDaData);
		} else {
			sdf = new SimpleDateFormat("dd/MM/yyyy");
		}
	}
	
	public Date getData() {
		if(this.data == null){
			this.data = new Date();
		}
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public String getCompetencia() {
		return this.sdf.format(this.getData());
	}

	public void setProcessamentoSeq(Integer processamentoSeq) {
		this.processamentoSeq = processamentoSeq;
	}

	public Integer getProcessamentoSeq() {
		return processamentoSeq;
	}
	
//	public String getCompetencia(String formatoSimpleDateFormat) {
//		return this.sdf.format(this.getData());
//	}
}
