package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ColetaViaSistemaVO implements java.io.Serializable {

	private static final long serialVersionUID = -1845214605607508307L;

	private Integer pmuSeq;
	private String filtro;
	private Integer cctCodigo;
	private BigDecimal qtde;
	
	
	public ColetaViaSistemaVO() {
	}
	
	public ColetaViaSistemaVO(Object[] objects) {
		if (objects[0] != null) {
			this.setPmuSeq(Integer.parseInt(objects[0].toString())); 
		}
		
		if (objects[1] != null) {
			this.setFiltro(objects[1].toString()); 
		}
		
		if (objects[2] != null) {
			this.setCctCodigo(Integer.parseInt(objects[2].toString())); 
		}
		
		if (objects[3] != null) {
			this.setQtde(new BigDecimal(objects[3].toString())); 
		}
	}

	public Integer getPmuSeq() {
		return pmuSeq;
	}

	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}
	
	public static List<ColetaViaSistemaVO> transformar(List<Object[]> listResult){
		
		List<ColetaViaSistemaVO> lista = new ArrayList<ColetaViaSistemaVO>();
		
		if (listResult != null && !listResult.isEmpty()) {
			for (Object[] objects : listResult) {
				lista.add(new ColetaViaSistemaVO(objects));
			}
		}
		
		return lista;
	}
}