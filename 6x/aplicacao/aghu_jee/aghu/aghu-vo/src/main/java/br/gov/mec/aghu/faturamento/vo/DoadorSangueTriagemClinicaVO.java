package br.gov.mec.aghu.faturamento.vo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DoadorSangueTriagemClinicaVO {
	
	private static final Log LOG = LogFactory.getLog(DoadorSangueTriagemClinicaVO.class);

	private String boldata;
	private Short quantidade;
	private Integer sermatricula;
	private Short servincodigo;
	
	
	public String getBoldata() {
		return boldata;
	}

	public void setBoldata(String boldata) {
		this.boldata = boldata;
	}

	public Date getBoldataAsDate() {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
		try {
			return formatter.parse(boldata);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}  
		return null;
	}
	
	public Short getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getSermatricula() {
		return sermatricula;
	}
	public void setSermatricula(Integer sermatricula) {
		this.sermatricula = sermatricula;
	}
	public Short getServincodigo() {
		return servincodigo;
	}
	public void setServincodigo(Short servincodigo) {
		this.servincodigo = servincodigo;
	}
	
}
