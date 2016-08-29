package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.gov.mec.aghu.core.commons.BaseBean;

public class SomatorioAnaliseCustosObjetosCustoVO implements BaseBean{

	private static final long serialVersionUID = -6541731131760654678L;
	private BigDecimal somatorioTotal = BigDecimal.ZERO;
	private BigDecimal somatorioCustoMedio = BigDecimal.ZERO;
	private BigDecimal somatorioCustoMedioAS = BigDecimal.ZERO;
	private BigDecimal somatorioIndiretos = BigDecimal.ZERO;
	private BigDecimal somatorioServicos = BigDecimal.ZERO;
	private BigDecimal somatorioEquipamentos = BigDecimal.ZERO;
	private BigDecimal somatorioInsumos = BigDecimal.ZERO;
	private BigDecimal somatorioPessoal = BigDecimal.ZERO;
	private BigDecimal somatorioDireto = BigDecimal.ZERO;
	private BigDecimal somatorioObjetoCusto = BigDecimal.ZERO;
	private BigDecimal somatorioQuantidadeProduzida = BigDecimal.ZERO;
	
	public BigDecimal getSomatorioTotal() {
		return somatorioTotal;
	}
	public void setSomatorioTotal(BigDecimal somatorioTotal) {
		this.somatorioTotal = somatorioTotal;
	}
	public BigDecimal getSomatorioCustoMedio() {
		return somatorioCustoMedio;
	}
	public void setSomatorioCustoMedio(BigDecimal somatorioCustoMedio) {
		this.somatorioCustoMedio = somatorioCustoMedio;
	}
	public BigDecimal getSomatorioCustoMedioAS() {
		if(somatorioObjetoCusto != null && somatorioQuantidadeProduzida != null) {
			if(somatorioQuantidadeProduzida.compareTo(BigDecimal.ZERO) != 0){
				return somatorioObjetoCusto.divide(somatorioQuantidadeProduzida, RoundingMode.HALF_EVEN);
			}
			return BigDecimal.ZERO; 
		}
		return somatorioCustoMedioAS;
	}
	public void setSomatorioCustoMedioAS(BigDecimal somatorioCustoMedioAS) {
		this.somatorioCustoMedioAS = somatorioCustoMedioAS;
	}
	public BigDecimal getSomatorioIndiretos() {
		return somatorioIndiretos;
	}
	public void setSomatorioIndiretos(BigDecimal somatorioIndiretos) {
		this.somatorioIndiretos = somatorioIndiretos;
	}
	public BigDecimal getSomatorioServicos() {
		return somatorioServicos;
	}
	public void setSomatorioServicos(BigDecimal somatorioServicos) {
		this.somatorioServicos = somatorioServicos;
	}
	public BigDecimal getSomatorioEquipamentos() {
		return somatorioEquipamentos;
	}
	public void setSomatorioEquipamentos(BigDecimal somatorioEquipamentos) {
		this.somatorioEquipamentos = somatorioEquipamentos;
	}
	public BigDecimal getSomatorioInsumos() {
		return somatorioInsumos;
	}
	public void setSomatorioInsumos(BigDecimal somatorioInsumos) {
		this.somatorioInsumos = somatorioInsumos;
	}
	public BigDecimal getSomatorioPessoal() {
		return somatorioPessoal;
	}
	public void setSomatorioPessoal(BigDecimal somatorioPessoal) {
		this.somatorioPessoal = somatorioPessoal;
	}
	public BigDecimal getSomatorioDireto() {
		return somatorioDireto;
	}
	public void setSomatorioDireto(BigDecimal somatorioDireto) {
		this.somatorioDireto = somatorioDireto;
	}
	public BigDecimal getSomatorioObjetoCusto() {
		return somatorioObjetoCusto;
	}
	public void setSomatorioObjetoCusto(BigDecimal somatorioObjetoCusto) {
		this.somatorioObjetoCusto = somatorioObjetoCusto;
	}
	public BigDecimal getSomatorioQuantidadeProduzida() {
		return somatorioQuantidadeProduzida;
	}
	public void setSomatorioQuantidadeProduzida(
			BigDecimal somatorioQuantidadeProduzida) {
		this.somatorioQuantidadeProduzida = somatorioQuantidadeProduzida;
	}
}
