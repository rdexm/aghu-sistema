package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioViewMonitorPendenciasExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class MonitorPendenciasExamesFiltrosPesquisaVO {

	private AghUnidadesFuncionais unidadeFuncionalExames;
	private Date dataReferenciaIni;
	private Date dataReferenciaFim;
	private Integer numeroUnicoIni;
	private Integer numeroUnicoFim;
	private Date dataDia;
	private DominioViewMonitorPendenciasExames viewMonitorPendenciasExames;
	private Integer quantidadeMaximaRegistros;

	public AghUnidadesFuncionais getUnidadeFuncionalExames() {
		return unidadeFuncionalExames;
	}

	public void setUnidadeFuncionalExames(AghUnidadesFuncionais unidadeFuncionalExames) {
		this.unidadeFuncionalExames = unidadeFuncionalExames;
	}

	public Date getDataReferenciaIni() {
		return dataReferenciaIni;
	}

	public void setDataReferenciaIni(Date dataReferenciaIni) {
		this.dataReferenciaIni = dataReferenciaIni;
	}

	public Date getDataReferenciaFim() {
		return dataReferenciaFim;
	}

	public void setDataReferenciaFim(Date dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}

	public Integer getNumeroUnicoIni() {
		return numeroUnicoIni;
	}

	public void setNumeroUnicoIni(Integer numeroUnicoIni) {
		this.numeroUnicoIni = numeroUnicoIni;
	}

	public Integer getNumeroUnicoFim() {
		return numeroUnicoFim;
	}

	public void setNumeroUnicoFim(Integer numeroUnicoFim) {
		this.numeroUnicoFim = numeroUnicoFim;
	}

	public Date getDataDia() {
		return dataDia;
	}

	public void setDataDia(Date dataDia) {
		this.dataDia = dataDia;
	}

	public DominioViewMonitorPendenciasExames getViewMonitorPendenciasExames() {
		return viewMonitorPendenciasExames;
	}

	public void setViewMonitorPendenciasExames(DominioViewMonitorPendenciasExames viewMonitorPendenciasExames) {
		this.viewMonitorPendenciasExames = viewMonitorPendenciasExames;
	}
	
	public Integer getQuantidadeMaximaRegistros() {
		return quantidadeMaximaRegistros;
	}
	
	public void setQuantidadeMaximaRegistros(Integer quantidadeMaximaRegistros) {
		this.quantidadeMaximaRegistros = quantidadeMaximaRegistros;
	}
}
