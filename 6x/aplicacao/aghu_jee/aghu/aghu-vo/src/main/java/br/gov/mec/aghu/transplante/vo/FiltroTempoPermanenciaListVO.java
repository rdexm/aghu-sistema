package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaFilaTransplante;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;

public class FiltroTempoPermanenciaListVO {

	/**
	 * VO de #41790 - Relatório de tempo de permanência de pacientes em lista
	 */
	
	// ORDENAÇÃO
	private DominioOrdenacaoPesquisaFilaTransplante ordenacao; 
	
	// TIPO TRANSPLANTE
	private DominioTipoTransplanteCombo tipoTransplante;
	
	// TIPO TMO
	private DominioSituacaoTmo tipoTMO;
	
	// TIPO TMO ALOGÊNICO
	private DominioTipoAlogenico tipoAlogenico;
	
	// TIPO ÓRGÃOS
	private DominioTipoOrgao tipoOrgao;

	private String prontuarioNome;
	
	private Date dataFim;
	
	private Date dataInicio;
	
	
	public String getProntuarioNome() {
		return prontuarioNome;
	}

	public void setProntuarioNome(String prontuarioNome) {
		this.prontuarioNome = prontuarioNome;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public DominioOrdenacaoPesquisaFilaTransplante getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante ordenacao) {
		this.ordenacao = ordenacao;
	}

	public DominioTipoTransplanteCombo getTipoTransplante() {
		return tipoTransplante;
	}

	public void setTipoTransplante(DominioTipoTransplanteCombo tipoTransplante) {
		this.tipoTransplante = tipoTransplante;
	}

	public DominioSituacaoTmo getTipoTMO() {
		return tipoTMO;
	}

	public void setTipoTMO(DominioSituacaoTmo tipoTMO) {
		this.tipoTMO = tipoTMO;
	}

	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}

	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}

	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}
}
