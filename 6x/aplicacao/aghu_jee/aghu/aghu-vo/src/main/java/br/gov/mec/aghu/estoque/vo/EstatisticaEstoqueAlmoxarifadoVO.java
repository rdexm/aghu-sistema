package br.gov.mec.aghu.estoque.vo;

import java.util.Date;



public class EstatisticaEstoqueAlmoxarifadoVO {
	
	private Integer qtdePendenteConfirm;
	private Integer qtdePontoPerdido;
	private String qtdeConsumoMedio;
	private String qtdeConsumoPonderado;
	private String classifABC;
	private Integer tempoReposicao;
	private String calculaPontoPedido;
	private String endereco;
	private String duracaoLocalDias;
	private String duracaoGeralDias;
	private Integer espacoDisponivel;
	private Integer saldoBloqueado;
	private Integer qtdeDisponivel;
	private Integer qtdeBloqueadoProblema;
	private Integer qtdeRecebProvisorio;
	private Integer saldoTodosAlmox;
	private Integer qtdeBloqueadoDispensacao;
	private Integer qtdEntregaPendente;
	
	private Integer saldoBloqueadoTerceiro;
	private Integer qtdeDisponivelTerceiro;
	private Integer saldoTodosAlmoxTerceiro;

	private Date dataUltimaCompra;
	private Date dataUltimoConsumo;
	private Date dataUltimaCompraFundoFixo;
	private QtdeRpVO qtdeRp;
	
	private EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO historicoConsumo;
	
	public Integer getQtdePendenteConfirm() {
		return qtdePendenteConfirm;
	}
	public void setQtdePendenteConfirm(Integer qtdePendenteConfirm) {
		this.qtdePendenteConfirm = qtdePendenteConfirm;
	}
	public Integer getQtdePontoPerdido() {
		return qtdePontoPerdido;
	}
	public void setQtdePontoPerdido(Integer qtdePontoPerdido) {
		this.qtdePontoPerdido = qtdePontoPerdido;
	}
	public String getQtdeConsumoMedio() {
		return qtdeConsumoMedio;
	}
	public void setQtdeConsumoMedio(String qtdeConsumoMedio) {
		this.qtdeConsumoMedio = qtdeConsumoMedio;
	}
	public String getQtdeConsumoPonderado() {
		return qtdeConsumoPonderado;
	}
	public void setQtdeConsumoPonderado(String qtdeConsumoPonderado) {
		this.qtdeConsumoPonderado = qtdeConsumoPonderado;
	}
	public String getClassifABC() {
		return classifABC;
	}
	public void setClassifABC(String classifABC) {
		this.classifABC = classifABC;
	}
	public Integer getTempoReposicao() {
		return tempoReposicao;
	}
	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}
	public String getCalculaPontoPedido() {
		return calculaPontoPedido;
	}
	public void setCalculaPontoPedido(String calculaPontoPedido) {
		this.calculaPontoPedido = calculaPontoPedido;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getDuracaoLocalDias() {
		return duracaoLocalDias;
	}
	public void setDuracaoLocalDias(String duracaoLocalDias) {
		this.duracaoLocalDias = duracaoLocalDias;
	}
	public Integer getEspacoDisponivel() {
		return espacoDisponivel;
	}
	public void setEspacoDisponivel(Integer espacoDisponivel) {
		this.espacoDisponivel = espacoDisponivel;
	}
	public Integer getSaldoBloqueado() {
		return saldoBloqueado;
	}
	public void setSaldoBloqueado(Integer saldoBloqueado) {
		this.saldoBloqueado = saldoBloqueado;
	}
	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}
	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}
	public String getDuracaoGeralDias() {
		return duracaoGeralDias;
	}
	public void setDuracaoGeralDias(String duracaoGeralDias) {
		this.duracaoGeralDias = duracaoGeralDias;
	}
	public Integer getQtdeBloqueadoProblema() {
		return qtdeBloqueadoProblema;
	}
	public void setQtdeBloqueadoProblema(Integer qtdeBloqueadoProblema) {
		this.qtdeBloqueadoProblema = qtdeBloqueadoProblema;
	}
	public Integer getQtdeRecebProvisorio() {
		return qtdeRecebProvisorio;
	}
	public void setQtdeRecebProvisorio(Integer qtdeRecebProvisorio) {
		this.qtdeRecebProvisorio = qtdeRecebProvisorio;
	}
	public Integer getSaldoTodosAlmox() {
		return saldoTodosAlmox;
	}
	public void setSaldoTodosAlmox(Integer saldoTodosAlmox) {
		this.saldoTodosAlmox = saldoTodosAlmox;
	}
	public Integer getSaldoBloqueadoTerceiro() {
		return saldoBloqueadoTerceiro;
	}
	public void setSaldoBloqueadoTerceiro(Integer saldoBloqueadoTerceiro) {
		this.saldoBloqueadoTerceiro = saldoBloqueadoTerceiro;
	}
	public Integer getQtdeDisponivelTerceiro() {
		return qtdeDisponivelTerceiro;
	}
	public void setQtdeDisponivelTerceiro(Integer qtdeDisponivelTerceiro) {
		this.qtdeDisponivelTerceiro = qtdeDisponivelTerceiro;
	}
	public Integer getSaldoTodosAlmoxTerceiro() {
		return saldoTodosAlmoxTerceiro;
	}
	public void setSaldoTodosAlmoxTerceiro(Integer saldoTodosAlmoxTerceiro) {
		this.saldoTodosAlmoxTerceiro = saldoTodosAlmoxTerceiro;
	}
	public Date getDataUltimaCompra() {
		return dataUltimaCompra;
	}
	public void setDataUltimaCompra(Date dataUltimaCompra) {
		this.dataUltimaCompra = dataUltimaCompra;
	}
	public Date getDataUltimoConsumo() {
		return dataUltimoConsumo;
	}
	public void setDataUltimoConsumo(Date dataUltimoConsumo) {
		this.dataUltimoConsumo = dataUltimoConsumo;
	}
	public Date getDataUltimaCompraFundoFixo() {
		return dataUltimaCompraFundoFixo;
	}
	public void setDataUltimaCompraFundoFixo(Date dataUltimaCompraFundoFixo) {
		this.dataUltimaCompraFundoFixo = dataUltimaCompraFundoFixo;
	}
	public EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO getHistoricoConsumo() {
		return historicoConsumo;
	}
	public void setHistoricoConsumo(EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO historicoConsumo) {
		this.historicoConsumo = historicoConsumo;
	}
	public Integer getQtdeBloqueadoDispensacao() {
		return qtdeBloqueadoDispensacao;
	}
	public void setQtdeBloqueadoDispensacao(Integer qtdeBloqueadoDispensacao) {
		this.qtdeBloqueadoDispensacao = qtdeBloqueadoDispensacao;
	}	
	public QtdeRpVO getQtdeRp() {
		return qtdeRp;
	}

	public void setQtdeRp(QtdeRpVO qtdeRp) {
		this.qtdeRp = qtdeRp;
	}
	public Integer getQtdEntregaPendente() {
		return qtdEntregaPendente;
	}
	public void setQtdEntregaPendente(Integer qtdEntregaPendente) {
		this.qtdEntregaPendente = qtdEntregaPendente;
	}	
}
