package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

public class CuidadosEnfermagemVO implements ResultTransformer {
	
	private static final long serialVersionUID = -1829159803094626937L;

	private Integer phiSeq;	
   
	private  Integer ocvSeq;
	
	private Short tfqSeq;
   
	private Short frequencia;
   
	private Date dataHoraInicio;
	
	private Date dataHoraFim;
	
	private Long quantidadeCuidados;
	
	public CuidadosEnfermagemVO() {
		
	}
		
    public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	
	public Short getTfqSeq() {
		return tfqSeq;
	}

	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public Date getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Date dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}
	
	public Date getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(Date dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}

	public Long getQuantidadeCuidados() {
		return quantidadeCuidados;
	}
	
	public void setQuantidadeCuidados(Long quantidadeCuidados) {
		this.quantidadeCuidados = quantidadeCuidados;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		
		CuidadosEnfermagemVO vo = new CuidadosEnfermagemVO();
		// phi.seq as phi_seq,
		if (tuple[0] != null) {
			vo.setPhiSeq((Integer) tuple[0]);
		}
		
		// ocv.seq as ocv_seq,
		if(tuple[1] != null){
			vo.setOcvSeq((Integer) tuple[1]);
		}
		
		// prc.tfq_seq,
		if(tuple[2] != null){
			vo.setTfqSeq((Short) tuple[2]);
		}
		
		// prc.frequencia,
		if(tuple[3] != null){
			vo.setFrequencia((Short) tuple[3]);
		}
		// pen.dthr_inicio,
		if(tuple[4] != null){
			vo.setDataHoraInicio((Date) tuple[4]);
		}
		
		// pen.dthr_fim,
		if(tuple[4] != null){
			vo.setDataHoraFim((Date) tuple[5]);
		}
		
		// count(*) as qtd_cuidados
		if(tuple[5] != null){
			vo.setQuantidadeCuidados((Long) tuple[6]);
		}
		return vo;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List collection) {
		return collection;
	}
}
