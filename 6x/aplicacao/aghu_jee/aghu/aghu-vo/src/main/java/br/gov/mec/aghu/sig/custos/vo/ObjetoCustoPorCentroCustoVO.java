package br.gov.mec.aghu.sig.custos.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ObjetoCustoPorCentroCustoVO implements BaseBean {

	private static final long serialVersionUID = -367305007838142547L;
		
	private String centroProducaoNome;
	private Integer cctCodigo;
	private String cctDescricao;
	private DominioSituacao cctIndSituacao;
	private Integer contElaboracao;
	private Integer contProgramacao;
	private Integer contAtivo;
	private Integer contInativo;
	
	public static ObjetoCustoPorCentroCustoVO create(Object[] objects){
		ObjetoCustoPorCentroCustoVO vo = new ObjetoCustoPorCentroCustoVO();
		
		if (objects[0] != null) {
			vo.setCentroProducaoNome(objects[0].toString());
		}
		if (objects[1] != null) {
			vo.setCctCodigo(Integer.parseInt(objects[1].toString()));
		}
		if (objects[2] != null) {
			vo.setCctDescricao(objects[2].toString());
		}
		if (objects[3] != null) {
			vo.setCctIndSituacao((DominioSituacao.valueOf(objects[3].toString())));
		}
		if (objects[4] != null) {
			vo.setContElaboracao(Integer.parseInt(objects[4].toString()));
		}
		if (objects[5] != null) {
			vo.setContProgramacao(Integer.parseInt(objects[5].toString()));
		}
		if (objects[6] != null) {
			vo.setContAtivo(Integer.parseInt(objects[6].toString()));
		}
		if (objects[7] != null) {
			vo.setContInativo(Integer.parseInt(objects[7].toString()));
		}
		
		return vo;
	}
	
	public String getCentroProducaoNome() {
		return centroProducaoNome;
	}
	
	public void setCentroProducaoNome(String centroProducaoNome) {
		this.centroProducaoNome = centroProducaoNome;
	}
	
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	
	public String getCctDescricao() {
		return cctDescricao;
	}
	
	public void setCctDescricao(String cctDescricao) {
		this.cctDescricao = cctDescricao;
	}
	
	public DominioSituacao getCctIndSituacao() {
		return cctIndSituacao;
	}
	
	public void setCctIndSituacao(DominioSituacao cctIndSituacao) {
		this.cctIndSituacao = cctIndSituacao;
	}
	
	public Integer getContElaboracao() {
		return contElaboracao;
	}
	
	public void setContElaboracao(Integer contElaboracao) {
		this.contElaboracao = contElaboracao;
	}
	
	public Integer getContProgramacao() {
		return contProgramacao;
	}
	
	public void setContProgramacao(Integer contProgramacao) {
		this.contProgramacao = contProgramacao;
	}
	
	public Integer getContAtivo() {
		return contAtivo;
	}
	
	public void setContAtivo(Integer contAtivo) {
		this.contAtivo = contAtivo;
	}
	
	public Integer getContInativo() {
		return contInativo;
	}
	
	public void setContInativo(Integer contInativo) {
		this.contInativo = contInativo;
	}
	
	
	
}
