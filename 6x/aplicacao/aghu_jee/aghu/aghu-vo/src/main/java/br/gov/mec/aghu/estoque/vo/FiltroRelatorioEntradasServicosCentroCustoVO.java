package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoConsultaRelatorioEntrada;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

/**
 * Classe de filtro para os Relatórios de Entrada de Serviços/Materiais
 * #35688, #35689
 * @author rafael.silvestre
 */
public class FiltroRelatorioEntradasServicosCentroCustoVO {

	private Date periodoInicial;
	
	private Date periodoFinal;
	
	private ScoServico scoServico;
	
	private ScoMaterial scoMaterial;
	
	private FccCentroCustos fccCentroCusto;
	
	private DominioTipoConsultaRelatorioEntrada tipoEntrada;
	
	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public ScoServico getScoServico() {
		return scoServico;
	}

	public void setScoServico(ScoServico scoServico) {
		this.scoServico = scoServico;
	}

	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	public FccCentroCustos getFccCentroCusto() {
		return fccCentroCusto;
	}

	public void setFccCentroCusto(FccCentroCustos fccCentroCusto) {
		this.fccCentroCusto = fccCentroCusto;
	}
	
	public DominioTipoConsultaRelatorioEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(DominioTipoConsultaRelatorioEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}
}
