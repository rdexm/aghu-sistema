package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

public class PesquisaAutFornPedidosVO {
	
	/*** Filtros ***/
	private Integer numeroPAC;
	private Integer numeroAFP;
	
	private Short nroComplementoAF;
	
	private Boolean indImpressa;
	private Boolean indEnviada;
	
	private DominioSimNao impressa;
	private DominioSimNao enviada;
	
	private Date dataInicioEnvio;
	private Date dataFimEnvio;

	private ScoFornecedor fornecedor;
	private RapServidores servidorGestor;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private ScoServico servico;
	private ScoGrupoServico grupoServico;
	
	public Integer getNumeroPAC() {
		return numeroPAC;
	}
	
	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}
	
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}

	public Short getNroComplementoAF() {
		return nroComplementoAF;
	}
	
	public void setNroComplementoAF(Short nroComplementoAF) {
		this.nroComplementoAF = nroComplementoAF;
	}
	
	public Boolean getIndImpressa() {
		return indImpressa;
	}
	
	public void setIndImpressa(Boolean indImpressa) {
		this.indImpressa = indImpressa;
	}

	public Boolean getIndEnviada() {
		return indEnviada;
	}
	
	public void setIndEnviada(Boolean indEnviada) {
		this.indEnviada = indEnviada;
	}
	
	public DominioSimNao getImpressa() {
		return impressa;
	}
	
	public void setImpressa(DominioSimNao impressa) {
		this.impressa = impressa;
	}

	public DominioSimNao getEnviada() {
		return enviada;
	}
	
	public void setEnviada(DominioSimNao enviada) {
		this.enviada = enviada;
	}

	public Date getDataInicioEnvio() {
		return dataInicioEnvio;
	}
	
	public void setDataInicioEnvio(Date dataInicioEnvio) {
		this.dataInicioEnvio = dataInicioEnvio;
	}
	
	public Date getDataFimEnvio() {
		return dataFimEnvio;
	}
	
	public void setDataFimEnvio(Date dataFimEnvio) {
		this.dataFimEnvio = dataFimEnvio;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}
	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}
}