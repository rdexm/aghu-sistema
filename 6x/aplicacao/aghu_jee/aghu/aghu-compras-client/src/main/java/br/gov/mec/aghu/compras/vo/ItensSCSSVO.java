package br.gov.mec.aghu.compras.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ItensSCSSVO implements BaseBean {

	private static final long serialVersionUID = -7508510158367709313L;
	private Integer numero;
	private String descricao;
	private Date dtSolicitacao;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicacao;
	private ScoPontoParadaSolicitacao pontoParadaProxima;
	private ScoPontoParadaSolicitacao ppAnterior;
	private Boolean efetivada;
	private ScoItemLicitacao pac;
	private List<ScoAutorizacaoForn> listaAutorizacaoForn;
	private DominioTipoSolicitacao tipoSolicitacao;
	private ScoMaterial material;
	private ScoServico servico;
	private Integer qtdVinculadas;
	private Boolean exclusao;
	private String nomeServidorExclusao;
	private Date dataExclusao;
	private String nomeServidorGeracao;
	private Date dataAutorizacao;
	private String nomeServidorAutorizador;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String hintDtExclusao;
	private String hintDtSolicitacao;
	private Integer seqCcReq;
	private String descricaoCcReq;
	private Integer seqCcAplic;
	private String descricaoCcAplic;
	private Short seqPontoParadaProxima;
	private String descricaoPontoParadaProxima;
	private Short seqPpAnterior;
	private String descricaoPPAnterior;
	private Integer lctNumero;
	private Integer itlNumero;
	private String descricaoModalidadeLicitacao;
	private String descricaoModalidadePregao;
	private Integer numeroAf;
	private Integer complementoAf;
	private String numeroEComplementoAf;
	private String nomeFantasiaFornAf;
	private String descricaoMaterial;
	private String descricaoSc;
	private Integer qtdAnexo;

	public enum Fields {

		NUMERO("numero"), DESCRICAO("descricao"), DT_SOLICITACAO("dtSolicitacao"), EFETIVADA("efetivada"), TIPO_SOLICITACAO(
				"tipoSolicitacao"), QTD_SOLICITACOES_VINCULADAS("qtdVinculadas"), EXCLUSAO("exclusao"), NOME_SERVIDOR_EXCLUSAO(
				"nomeServidorExclusao"), DT_EXCLUSAO("dataExclusao"), NOME_SERVIDOR_GERACAO("nomeServidorGeracao"), DT_AUTORIZACAO(
				"dataAutorizacao"), NOME_SERVIDOR_AUTORIZADOR("nomeServidorAutorizador"), CODIGO_MATERIAL("codigoMaterial"), NOME_MATERIAL(
				"nomeMaterial"), HINT_DT_EXCLUSAO("hintDtExclusao"), HINT_DT_SOLICITACAO("hintDtSolicitacao"), SEQ_CC_REQ("seqCcReq"), NOME_CC_REQ(
				"descricaoCcReq"), SEQ_CC_APLIC("seqCcAplic"), NOME_CC_APLIC("descricaoCcAplic"), SEQ_PONTO_PARADA_ATUAL(
				"seqPontoParadaProxima"), NOME_PONTO_PARADA_ATUAL("descricaoPontoParadaProxima"), SEQ_PONTO_PARADA_ANTERIOR("seqPpAnterior"), NOME_PONTO_PARADA_ANTERIOR(
				"descricaoPPAnterior"), LCT_NUMERO("lctNumero"), ITL_NUMERO("itlNumero"), DESCRICAO_MODALIDADE_LICITACAO(
				"descricaoModalidadeLicitacao"), DESCRICAO_MODALIDADE_PREGAO("descricaoModalidadePregao"), NUMERO_AF("numeroAf"), COMPLEMENTO_AF(
				"complementoAf"), NRO_E_COMPLEMENTO_AF("numeroEComplementoAf"), NOME_FANTASIA_FORN_AF("nomeFantasiaFornAf"), DESCRICAO_MATERIAL(
				"descricaoMaterial"), DESCRICAO_SC("descricaoSc"), QTD_ANEXO("qtdAnexo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDtSolicitacao() {
		return dtSolicitacao;
	}

	public void setDtSolicitacao(Date dtSolicitacao) {
		this.dtSolicitacao = dtSolicitacao;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public ScoPontoParadaSolicitacao getPontoParadaProxima() {
		return pontoParadaProxima;
	}

	public void setPontoParadaProxima(ScoPontoParadaSolicitacao pontoParadaProxima) {
		this.pontoParadaProxima = pontoParadaProxima;
	}

	public Boolean getEfetivada() {
		return efetivada;
	}

	public void setEfetivada(Boolean efetivada) {
		this.efetivada = efetivada;
	}

	public ScoItemLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoItemLicitacao pac) {
		this.pac = pac;
	}

	public List<ScoAutorizacaoForn> getListaAutorizacaoForn() {
		return listaAutorizacaoForn;
	}

	public void setListaAutorizacaoForn(List<ScoAutorizacaoForn> listaAutorizacaoForn) {
		this.listaAutorizacaoForn = listaAutorizacaoForn;
	}

	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
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

	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	public ScoPontoParadaSolicitacao getPpAnterior() {
		return ppAnterior;
	}

	public void setPpAnterior(ScoPontoParadaSolicitacao ppAnterior) {
		this.ppAnterior = ppAnterior;
	}

	public String getNomeServidorExclusao() {
		return nomeServidorExclusao;
	}

	public void setNomeServidorExclusao(String nomeServidorExclusao) {
		this.nomeServidorExclusao = nomeServidorExclusao;
	}

	public Date getDataExclusao() {
		return dataExclusao;
	}

	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
	}

	public String getNomeServidorGeracao() {
		return nomeServidorGeracao;
	}

	public void setNomeServidorGeracao(String nomeServidorGeracao) {
		this.nomeServidorGeracao = nomeServidorGeracao;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public String getNomeServidorAutorizador() {
		return nomeServidorAutorizador;
	}

	public void setNomeServidorAutorizador(String nomeServidorAutorizador) {
		this.nomeServidorAutorizador = nomeServidorAutorizador;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getHintDtExclusao() {
		return hintDtExclusao;
	}

	public void setHintDtExclusao(String hintDtExclusao) {
		this.hintDtExclusao = hintDtExclusao;
	}

	public String getHintDtSolicitacao() {
		return hintDtSolicitacao;
	}

	public void setHintDtSolicitacao(String hintDtSolicitacao) {
		this.hintDtSolicitacao = hintDtSolicitacao;
	}

	public Integer getSeqCcReq() {
		return seqCcReq;
	}

	public void setSeqCcReq(Integer seqCcReq) {
		this.seqCcReq = seqCcReq;
	}

	public String getDescricaoCcReq() {
		return descricaoCcReq;
	}

	public void setDescricaoCcReq(String descricaoCcReq) {
		this.descricaoCcReq = descricaoCcReq;
	}

	public Integer getSeqCcAplic() {
		return seqCcAplic;
	}

	public void setSeqCcAplic(Integer seqCcAplic) {
		this.seqCcAplic = seqCcAplic;
	}

	public String getDescricaoCcAplic() {
		return descricaoCcAplic;
	}

	public void setDescricaoCcAplic(String descricaoCcAplic) {
		this.descricaoCcAplic = descricaoCcAplic;
	}

	public Short getSeqPontoParadaProxima() {
		return seqPontoParadaProxima;
	}

	public void setSeqPontoParadaProxima(Short seqPontoParadaProxima) {
		this.seqPontoParadaProxima = seqPontoParadaProxima;
	}

	public String getDescricaoPontoParadaProxima() {
		return descricaoPontoParadaProxima;
	}

	public void setDescricaoPontoParadaProxima(String descricaoPontoParadaProxima) {
		this.descricaoPontoParadaProxima = descricaoPontoParadaProxima;
	}

	public Short getSeqPpAnterior() {
		return seqPpAnterior;
	}

	public void setSeqPpAnterior(Short seqPpAnterior) {
		this.seqPpAnterior = seqPpAnterior;
	}

	public String getDescricaoPPAnterior() {
		return descricaoPPAnterior;
	}

	public void setDescricaoPPAnterior(String descricaoPPAnterior) {
		this.descricaoPPAnterior = descricaoPPAnterior;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Integer itlNumero) {
		this.itlNumero = itlNumero;
	}

	public String getDescricaoModalidadeLicitacao() {
		return descricaoModalidadeLicitacao;
	}

	public void setDescricaoModalidadeLicitacao(String descricaoModalidadeLicitacao) {
		this.descricaoModalidadeLicitacao = descricaoModalidadeLicitacao;
	}

	public String getDescricaoModalidadePregao() {
		return descricaoModalidadePregao;
	}

	public void setDescricaoModalidadePregao(String descricaoModalidadePregao) {
		this.descricaoModalidadePregao = descricaoModalidadePregao;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Integer getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Integer complementoAf) {
		this.complementoAf = complementoAf;
	}

	public String getNumeroEComplementoAf() {
		return numeroEComplementoAf;
	}

	public void setNumeroEComplementoAf(String numeroEComplementoAf) {
		this.numeroEComplementoAf = numeroEComplementoAf;
	}

	public String getNomeFantasiaFornAf() {
		return nomeFantasiaFornAf;
	}

	public void setNomeFantasiaFornAf(String nomeFantasiaFornAf) {
		this.nomeFantasiaFornAf = nomeFantasiaFornAf;
	}

	public Integer getQtdVinculadas() {
		return qtdVinculadas;
	}

	public void setQtdVinculadas(Integer qtdVinculadas) {
		this.qtdVinculadas = qtdVinculadas;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getDescricaoSc() {
		return descricaoSc;
	}

	public void setDescricaoSc(String descricaoSc) {
		this.descricaoSc = descricaoSc;
	}

	public Integer getQtdAnexo() {
		return qtdAnexo;
	}

	public void setQtdAnexo(Integer qtdAnexo) {
		this.qtdAnexo = qtdAnexo;
	}
}
