package br.gov.mec.aghu.compras.vo;



import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPregao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;




public class FiltroConsSCSSVO {
	
		
	
	/*** Filtro ***/
	private Integer numero;
	private Integer numeroFinal;
	private DominioTipoSolicitacao tipoSolicitacao;	
	private FccCentroCustos centroCustoSolicitante;	
	private FccCentroCustos centroCustoAplicacao;	
	private DominioSimNao efetivada;
	private Boolean indEfetivada;
	private Date dataInicioSolicitacao;
	private Date dataFimSolicitacao;
	private Date dataInicioAutorizacao;
	private Date dataFimAutorizacao;
	private Date dataInicioAnalise;
	private Date dataFimAnalise;
	private String descricao;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private DominioSimNao indEstocavel;
	private ScoServico servico;
	private ScoGrupoServico grupoServico;
	private ScoPontoParadaSolicitacao pontoParadaAtual;
	private ScoPontoParadaSolicitacao pontoParadaAnterior;
	private DominioSimNao exclusao;
	private DominioSimNao devolucao;
	private DominioSimNao urgente;
	private DominioSimNao prioridade;
	private DominioSimNao exclusivo;
	private DominioSimNao geracaoAutomatica;
	private Boolean indExclusao;
	private Boolean indDevolucao;
	private Boolean indUrgente;
	private Boolean indPrioridade;
	private Boolean indExclusivo;
	private Boolean indGeracaoAutomatica;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private Integer nroProjeto;
	private Integer nroInvestimento;
	private FsoNaturezaDespesa naturezaDespesa;
	private FsoVerbaGestao verbaGestao;
	private Integer numeroPAC;
	private String descricaoPAC;
	private Date dataInicioAberturaPropostaPAC;
	private Date dataFimAberturaPropostaPAC;
	private DominioTipoLicitacao tipoPAC;
	private Boolean indExclusaoPAC;
	private DominioSimNao exclusaoPAC;
	private Integer numDocPAC;
	private Integer numEditalPAC;
	private Integer anoComplementoPAC;
	private Integer artigoPAC;
	private String incisoArtigoPAC;
	private ScoModalidadeLicitacao modalidadeLicitacaoPAC;
	private DominioTipoPregao tipoPregaoPAC;
	private RapServidores servidorGestorPAC;
	private Integer numeroAF;
	private Short nroComplementoAF;
	private Date dataInicioGeracaoAF;
	private Date dataFimGeracaoAF;
	private Date dataInicioPrevEntregaAF;
	private Date dataFimPrevEntregaAF;
	private Boolean indExclusaoAF;
	private DominioSimNao exclusaoAF;
	private ScoFornecedor fornecedorAF;
	private String nroEmpenhoAF;
	private Integer nroContratoAF;
	private RapServidores servidorGestorAF;
	private Boolean pesquisarPorPAC;
	private Boolean pesquisarPorAF;
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}
	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}
	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}
	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}
	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	
	public DominioSimNao getEfetivada() {
		return efetivada;
	}
	public void setEfetivada(DominioSimNao efetivada) {
		this.efetivada = efetivada;
	}
	public Boolean getIndEfetivada() {
		return indEfetivada;
	}
	public void setIndEfetivada(Boolean indEfetivada) {
		this.indEfetivada = indEfetivada;
	}
	public Date getDataInicioSolicitacao() {
		return dataInicioSolicitacao;
	}
	public void setDataInicioSolicitacao(Date dataInicioSolicitacao) {
		this.dataInicioSolicitacao = dataInicioSolicitacao;
	}
	public Date getDataFimSolicitacao() {
		return dataFimSolicitacao;
	}
	public void setDataFimSolicitacao(Date dataFimSolicitacao) {
		this.dataFimSolicitacao = dataFimSolicitacao;
	}
	
	public Date getDataInicioAutorizacao() {
		return dataInicioAutorizacao;
	}
	public void setDataInicioAutorizacao(Date dataInicioAutorizacao) {
		this.dataInicioAutorizacao = dataInicioAutorizacao;
	}
	public Date getDataFimAutorizacao() {
		return dataFimAutorizacao;
	}
	public void setDataFimAutorizacao(Date dataFimAutorizacao) {
		this.dataFimAutorizacao = dataFimAutorizacao;
	}
	public Date getDataInicioAnalise() {
		return dataInicioAnalise;
	}
	public void setDataInicioAnalise(Date dataInicioAnalise) {
		this.dataInicioAnalise = dataInicioAnalise;
	}
	public Date getDataFimAnalise() {
		return dataFimAnalise;
	}
	public void setDataFimAnalise(Date dataFimAnalise) {
		this.dataFimAnalise = dataFimAnalise;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public DominioSimNao getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(DominioSimNao indEstocavel) {
		this.indEstocavel = indEstocavel;
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
	public ScoPontoParadaSolicitacao getPontoParadaAtual() {
		return pontoParadaAtual;
	}
	public void setPontoParadaAtual(ScoPontoParadaSolicitacao pontoParadaAtual) {
		this.pontoParadaAtual = pontoParadaAtual;
	}
	public ScoPontoParadaSolicitacao getPontoParadaAnterior() {
		return pontoParadaAnterior;
	}
	public void setPontoParadaAnterior(ScoPontoParadaSolicitacao pontoParadaAnterior) {
		this.pontoParadaAnterior = pontoParadaAnterior;
	}
	public DominioSimNao getExclusao() {
		return exclusao;
	}
	public void setExclusao(DominioSimNao exclusao) {
		this.exclusao = exclusao;
	}
	public DominioSimNao getGeracaoAutomatica() {
		return geracaoAutomatica;
	}
	public void setGeracaoAutomatica(DominioSimNao geracaoAutomatica) {
		this.geracaoAutomatica = geracaoAutomatica;
	}
	public Boolean getIndGeracaoAutomatica() {
		return indGeracaoAutomatica;
	}
	public void setIndGeracaoAutomatica(Boolean indGeracaoAutomatica) {
		this.indGeracaoAutomatica = indGeracaoAutomatica;
	}
	public DominioSimNao getDevolucao() {
		return devolucao;
	}
	public void setDevolucao(DominioSimNao devolucao) {
		this.devolucao = devolucao;
	}
	public Boolean getIndUrgente() {
		return indUrgente;
	}
	public void setIndUrgente(Boolean indUrgente) {
		this.indUrgente = indUrgente;
	}
	public DominioSimNao getUrgente() {
		return urgente;
	}
	public void setUrgente(DominioSimNao urgente) {
		this.urgente = urgente;
	}
	public DominioSimNao getPrioridade() {
		return prioridade;
	}
	public void setPrioridade(DominioSimNao prioridade) {
		this.prioridade = prioridade;
	}
	public DominioSimNao getExclusivo() {
		return exclusivo;
	}
	public void setExclusivo(DominioSimNao exclusivo) {
		this.exclusivo = exclusivo;
	}
	public Boolean getIndExclusao() {
		return indExclusao;
	}
	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	public Boolean getIndDevolucao() {
		return indDevolucao;
	}
	public void setIndDevolucao(Boolean indDevolucao) {
		this.indDevolucao = indDevolucao;
	}
	public Boolean getIndPrioridade() {
		return indPrioridade;
	}
	public void setIndPrioridade(Boolean indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
	public Boolean getIndExclusivo() {
		return indExclusivo;
	}
	public void setIndExclusivo(Boolean indExclusivo) {
		this.indExclusivo = indExclusivo;
	}
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}
	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}
	public Integer getNroProjeto() {
		return nroProjeto;
	}
	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}
	public Integer getNroInvestimento() {
		return nroInvestimento;
	}
	public void setNroInvestimento(Integer nroInvestimento) {
		this.nroInvestimento = nroInvestimento;
	}
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	public Integer getNumeroPAC() {
		return numeroPAC;
	}
	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}
	public String getDescricaoPAC() {
		return descricaoPAC;
	}
	public void setDescricaoPAC(String descricaoPAC) {
		this.descricaoPAC = descricaoPAC;
	}
	public Date getDataInicioAberturaPropostaPAC() {
		return dataInicioAberturaPropostaPAC;
	}
	public void setDataInicioAberturaPropostaPAC(Date dataInicioAberturaPropostaPAC) {
		this.dataInicioAberturaPropostaPAC = dataInicioAberturaPropostaPAC;
	}
	public Date getDataFimAberturaPropostaPAC() {
		return dataFimAberturaPropostaPAC;
	}
	public void setDataFimAberturaPropostaPAC(Date dataFimAberturaPropostaPAC) {
		this.dataFimAberturaPropostaPAC = dataFimAberturaPropostaPAC;
	}
	public DominioTipoLicitacao getTipoPAC() {
		return tipoPAC;
	}
	public void setTipoPAC(DominioTipoLicitacao tipoPAC) {
		this.tipoPAC = tipoPAC;
	}
	public Boolean getIndExclusaoPAC() {
		return indExclusaoPAC;
	}
	public void setIndExclusaoPAC(Boolean indExclusaoPAC) {
		this.indExclusaoPAC = indExclusaoPAC;
	}
	public DominioSimNao getExclusaoPAC() {
		return exclusaoPAC;
	}
	public void setExclusaoPAC(DominioSimNao exclusaoPAC) {
		this.exclusaoPAC = exclusaoPAC;
	}
	public Integer getNumDocPAC() {
		return numDocPAC;
	}
	public void setNumDocPAC(Integer numDocPAC) {
		this.numDocPAC = numDocPAC;
	}
	public Integer getNumEditalPAC() {
		return numEditalPAC;
	}
	public void setNumEditalPAC(Integer numEditalPAC) {
		this.numEditalPAC = numEditalPAC;
	}
	public Integer getAnoComplementoPAC() {
		return anoComplementoPAC;
	}
	public void setAnoComplementoPAC(Integer anoComplementoPAC) {
		this.anoComplementoPAC = anoComplementoPAC;
	}
	public Integer getArtigoPAC() {
		return artigoPAC;
	}
	public void setArtigoPAC(Integer artigoPAC) {
		this.artigoPAC = artigoPAC;
	}
	public String getIncisoArtigoPAC() {
		return incisoArtigoPAC;
	}
	public void setIncisoArtigoPAC(String incisoArtigoPAC) {
		this.incisoArtigoPAC = incisoArtigoPAC;
	}
	public ScoModalidadeLicitacao getModalidadeLicitacaoPAC() {
		return modalidadeLicitacaoPAC;
	}
	public void setModalidadeLicitacaoPAC(
			ScoModalidadeLicitacao modalidadeLicitacaoPAC) {
		this.modalidadeLicitacaoPAC = modalidadeLicitacaoPAC;
	}
	public DominioTipoPregao getTipoPregaoPAC() {
		return tipoPregaoPAC;
	}
	public void setTipoPregaoPAC(DominioTipoPregao tipoPregaoPAC) {
		this.tipoPregaoPAC = tipoPregaoPAC;
	}
	public RapServidores getServidorGestorPAC() {
		return servidorGestorPAC;
	}
	public void setServidorGestorPAC(RapServidores servidorGestorPAC) {
		this.servidorGestorPAC = servidorGestorPAC;
	}
	public Integer getNumeroAF() {
		return numeroAF;
	}
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	public Short getNroComplementoAF() {
		return nroComplementoAF;
	}
	public void setNroComplementoAF(Short nroComplementoAF) {
		this.nroComplementoAF = nroComplementoAF;
	}
	public Date getDataInicioGeracaoAF() {
		return dataInicioGeracaoAF;
	}
	public void setDataInicioGeracaoAF(Date dataInicioGeracaoAF) {
		this.dataInicioGeracaoAF = dataInicioGeracaoAF;
	}
	public Date getDataFimGeracaoAF() {
		return dataFimGeracaoAF;
	}
	public void setDataFimGeracaoAF(Date dataFimGeracaoAF) {
		this.dataFimGeracaoAF = dataFimGeracaoAF;
	}
	public Date getDataInicioPrevEntregaAF() {
		return dataInicioPrevEntregaAF;
	}
	public void setDataInicioPrevEntregaAF(Date dataInicioPrevEntregaAF) {
		this.dataInicioPrevEntregaAF = dataInicioPrevEntregaAF;
	}
	public Date getDataFimPrevEntregaAF() {
		return dataFimPrevEntregaAF;
	}
	public void setDataFimPrevEntregaAF(Date dataFimPrevEntregaAF) {
		this.dataFimPrevEntregaAF = dataFimPrevEntregaAF;
	}
	public Boolean getIndExclusaoAF() {
		return indExclusaoAF;
	}
	public void setIndExclusaoAF(Boolean indExclusaoAF) {
		this.indExclusaoAF = indExclusaoAF;
	}
	public DominioSimNao getExclusaoAF() {
		return exclusaoAF;
	}
	public void setExclusaoAF(DominioSimNao exclusaoAF) {
		this.exclusaoAF = exclusaoAF;
	}
	public ScoFornecedor getFornecedorAF() {
		return fornecedorAF;
	}
	public void setFornecedorAF(ScoFornecedor fornecedorAF) {
		this.fornecedorAF = fornecedorAF;
	}
	public String getNroEmpenhoAF() {
		return nroEmpenhoAF;
	}
	public void setNroEmpenhoAF(String nroEmpenhoAF) {
		this.nroEmpenhoAF = nroEmpenhoAF;
	}
	public Integer getNroContratoAF() {
		return nroContratoAF;
	}
	public void setNroContratoAF(Integer nroContratoAF) {
		this.nroContratoAF = nroContratoAF;
	}
	public RapServidores getServidorGestorAF() {
		return servidorGestorAF;
	}
	public void setServidorGestorAF(RapServidores servidorGestorAF) {
		this.servidorGestorAF = servidorGestorAF;
	}
	public Boolean getPesquisarPorPAC() {
		return pesquisarPorPAC;
	}
	public void setPesquisarPorPAC(Boolean pesquisarPorPAC) {
		this.pesquisarPorPAC = pesquisarPorPAC;
	}
	public Boolean getPesquisarPorAF() {
		return pesquisarPorAF;
	}
	public void setPesquisarPorAF(Boolean pesquisarPorAF) {
		this.pesquisarPorAF = pesquisarPorAF;
	}
	public Integer getNumeroFinal() {
		return numeroFinal;
	}
	public void setNumeroFinal(Integer numeroFinal) {
		this.numeroFinal = numeroFinal;
	}
	
		
}
