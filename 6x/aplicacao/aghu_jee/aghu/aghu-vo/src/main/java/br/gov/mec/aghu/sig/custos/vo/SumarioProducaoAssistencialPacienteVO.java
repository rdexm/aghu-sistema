package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;

public class SumarioProducaoAssistencialPacienteVO {

	private SigObjetoCustoVersoes objetoCustoVersao;
	private FatProcedHospInternos phi;
	private FccCentroCustos centroCusto;
	private BigDecimal qtde;
	private AacPagador pagador;

	public SumarioProducaoAssistencialPacienteVO() {
	}

	public SumarioProducaoAssistencialPacienteVO(Object[] obj) {

		if (obj[0] != null) {
			this.setObjetoCustoVersao((SigObjetoCustoVersoes) obj[0]);
		}

		if (obj[1] != null) {
			this.setPhi((FatProcedHospInternos) obj[1]);
		}

		if (obj[2] != null) {
			this.setCentroCusto((FccCentroCustos) obj[2]);
		}

		if (obj[3] != null) {
			this.setQtde((BigDecimal) obj[3]);
		}

	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes ocv) {
		this.objetoCustoVersao = ocv;
	}

	public FatProcedHospInternos getPhi() {
		return phi;
	}

	public void setPhi(FatProcedHospInternos phi) {
		this.phi = phi;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos cct) {
		this.centroCusto = cct;
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}
	
	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	
	public enum Fields {

		OBJETO_CUSTO_VERSAO("objetoCustoVersao"),
		PHI("phi"),
		CENTRO_CUSTO("centroCusto"),
		QUANTIDADE("qtde"),
		PAGADOR("pagador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
