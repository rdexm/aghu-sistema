package br.gov.mec.aghu.sig.custos.vo;

import br.gov.mec.aghu.dominio.DominioSigTipoAlertaDetalhado;
import br.gov.mec.aghu.core.commons.BaseBean;

public class VisualizarAlertasProcessamentoVO implements BaseBean {

	private static final long serialVersionUID = -3845213605608508206L;

	private String tipo;
	private DominioSigTipoAlertaDetalhado dominioSigTipoAlertaDetalhado;
	private String objCusto;
	private String atividade;
	private Integer codigoFCC;
	private String descricaoFCC;
	private Integer quantidade;
	private double percentual;

	public static VisualizarAlertasProcessamentoVO createCentroCusto(Object[] objects) {
		VisualizarAlertasProcessamentoVO vo = new VisualizarAlertasProcessamentoVO();

		if (objects[0] != null) {
			vo.setTipo(objects[0].toString());
		}
		if (objects[1] != null) {
			vo.setObjCusto(objects[1].toString());
		}
		if (objects[2] != null) {
			vo.setAtividade(objects[2].toString());
		}
		if (objects[3] != null) {
			vo.setCodigoFCC(Integer.valueOf(objects[3].toString()));
		}
		if (objects[4] != null) {
			vo.setDescricaoFCC(objects[4].toString());
		}

		return vo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getObjCusto() {
		return objCusto;
	}

	public void setObjCusto(String objCusto) {
		this.objCusto = objCusto;
	}

	public String getAtividade() {
		return atividade;
	}

	public void setAtividade(String atividade) {
		this.atividade = atividade;
	}

	public Integer getCodigoFCC() {
		return codigoFCC;
	}

	public void setCodigoFCC(Integer codigoFCC) {
		this.codigoFCC = codigoFCC;
	}

	public String getDescricaoFCC() {
		return descricaoFCC;
	}

	public void setDescricaoFCC(String descricaoFCC) {
		this.descricaoFCC = descricaoFCC;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public double getPercentual() {
		return percentual;
	}

	public void setPercentual(double percentual) {
		this.percentual = percentual;
	}

	public DominioSigTipoAlertaDetalhado getDominioSigTipoAlertaDetalhado() {
		return dominioSigTipoAlertaDetalhado;
	}

	public void setDominioSigTipoAlertaDetalhado(DominioSigTipoAlertaDetalhado dominioSigTipoAlertaDetalhado) {
		this.dominioSigTipoAlertaDetalhado = dominioSigTipoAlertaDetalhado;
	}

}
