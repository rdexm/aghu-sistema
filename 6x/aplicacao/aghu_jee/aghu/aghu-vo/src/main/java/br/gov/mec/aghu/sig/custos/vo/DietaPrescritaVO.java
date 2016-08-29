package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

public class DietaPrescritaVO implements ResultTransformer {
	
	private static final long serialVersionUID = -1829159803094626937L;

	private Integer intSeq;	 //codigo internacao
   
	private Short unfSeq;    //codigo unidade de internacao
	
	private Date dataInicioPrescricao;
	
	private Date dataFimPrescricao;
	
	private Short hauSeq; //codigo habito alimentar usual
    
	private Short refSeq; //codigo refeicao 
   
	public DietaPrescritaVO(){
		
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Date getDataInicioPrescricao() {
		return dataInicioPrescricao;
	}

	public void setDataInicioPrescricao(Date dataInicioPrescricao) {
		this.dataInicioPrescricao = dataInicioPrescricao;
	}

	public Date getDataFimPrescricao() {
		return dataFimPrescricao;
	}

	public void setDataFimPrescricao(Date dataFimPrescricao) {
		this.dataFimPrescricao = dataFimPrescricao;
	}

	public Short getHauSeq() {
		return hauSeq;
	}

	public void setHauSeq(Short hauSeq) {
		this.hauSeq = hauSeq;
	}

	public Short getRefSeq() {
		return refSeq;
	}

	public void setRefSeq(Short refSeq) {
		this.refSeq = refSeq;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		
		DietaPrescritaVO vo = new DietaPrescritaVO();
		if (tuple[0] != null) {
			vo.setIntSeq((Integer) tuple[0]);
		}
		
		if(tuple[1] != null){
			vo.setUnfSeq((Short) tuple[1]);
		}
		
		if(tuple[2] != null){
			vo.setDataInicioPrescricao((Date) tuple[2]);
		}
		
		if(tuple[3] != null){
			vo.setDataFimPrescricao((Date) tuple[3]);
		}

		if(tuple[4] != null){
			vo.setHauSeq((Short) tuple[4]);
		}
		
		if(tuple[5] != null){
			vo.setRefSeq((Short) tuple[5]);
		}
		
		return vo;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List collection) {
		return collection;
	}
}
