package br.gov.mec.aghu.compras.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolitacaoAF;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;

public class FiltroProgrGeralEntregaAFVO {

	private Integer nroAF;
	private Short cp;
	private Integer afp;
	private FccCentroCustos centroCustoSol;
	private FccCentroCustos centroCustoApp;
	private DominioSimNao planejada;
	private DominioSimNao empenho;
	private DominioSimNao estocavel;
	private DominioClassifABC curvaABC;
	private Date previsaoDtInicial;
	private Date previsaoDtFinal;
	private ScoModalidadeLicitacao modalidade;
	private DominioSimNao efetivada;
	private DominioSimNao assinada;
	private DominioSimNao envForn;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Date vencimento;
	private DominioTipoSolitacaoAF tipoItem = DominioTipoSolitacaoAF.M;
	private ScoMaterial material;
	private ScoServico servico;
	private ScoGrupoMaterial grupoMaterial;
	private ScoGrupoServico grupoServico;
	private Integer nroSolicitacao;
	
	private ScoFornecedor fornecedor;
	private String descSolicitacao;
	private Integer numeroFornecedorPadrao;
	
	private static final Log LOG = LogFactory.getLog(FiltroProgrGeralEntregaAFVO.class);
	
	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public Integer getAfp() {
		return afp;
	}

	public void setAfp(Integer afp) {
		this.afp = afp;
	}

	public FccCentroCustos getCentroCustoSol() {
		return centroCustoSol;
	}

	public void setCentroCustoSol(FccCentroCustos centroCustoSol) {
		this.centroCustoSol = centroCustoSol;
	}

	public FccCentroCustos getCentroCustoApp() {
		return centroCustoApp;
	}

	public void setCentroCustoApp(FccCentroCustos centroCustoApp) {
		this.centroCustoApp = centroCustoApp;
	}

	public DominioSimNao getPlanejada() {
		return planejada;
	}

	public void setPlanejada(DominioSimNao planejada) {
		this.planejada = planejada;
	}

	public DominioSimNao getEmpenho() {
		return empenho;
	}

	public void setEmpenho(DominioSimNao empenho) {
		this.empenho = empenho;
	}

	public DominioSimNao getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioSimNao estocavel) {
		this.estocavel = estocavel;
	}

	public DominioClassifABC getCurvaABC() {
		return curvaABC;
	}

	public void setCurvaABC(DominioClassifABC curvaABC) {
		this.curvaABC = curvaABC;
	}

	public Date getPrevisaoDtInicial() {
		return previsaoDtInicial;
	}

	public void setPrevisaoDtInicial(Date previsaoDtInicial) {
		this.previsaoDtInicial = previsaoDtInicial;
	}

	public Date getPrevisaoDtFinal() {
		return previsaoDtFinal;
	}

	public void setPrevisaoDtFinal(Date previsaoDtFinal) {
		this.previsaoDtFinal = previsaoDtFinal;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public DominioSimNao getEfetivada() {
		return efetivada;
	}

	public void setEfetivada(DominioSimNao efetivada) {
		this.efetivada = efetivada;
	}

	public DominioSimNao getAssinada() {
		return assinada;
	}

	public void setAssinada(DominioSimNao assinada) {
		this.assinada = assinada;
	}

	public DominioSimNao getEnvForn() {
		return envForn;
	}

	public void setEnvForn(DominioSimNao envForn) {
		this.envForn = envForn;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public DominioTipoSolitacaoAF getTipoItem() {
		return tipoItem;
	}

	public void setTipoItem(DominioTipoSolitacaoAF tipoItem) {
		this.tipoItem = tipoItem;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public Integer getNroSolicitacao() {
		return nroSolicitacao;
	}

	public void setNroSolicitacao(Integer nroSolicitacao) {
		this.nroSolicitacao = nroSolicitacao;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public Date getDataCompetencia() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/yyyy");
		try {
			return dateFormatter.parse("01/" + dateFormatter.format(new Date()));
		} catch (ParseException e) {
			LOG.error("Erro ao gerar data competencia");
		}
		return null;
	}

	public void setDescSolicitacao(String descSolicitacao) {
		this.descSolicitacao = descSolicitacao;
	}

	public String getDescSolicitacao() {
		return descSolicitacao;
	}

	public Integer getNumeroFornecedorPadrao() {
		return numeroFornecedorPadrao;
	}

	public void setNumeroFornecedorPadrao(Integer numeroFornecedorPadrao) {
		this.numeroFornecedorPadrao = numeroFornecedorPadrao;
	}

}
