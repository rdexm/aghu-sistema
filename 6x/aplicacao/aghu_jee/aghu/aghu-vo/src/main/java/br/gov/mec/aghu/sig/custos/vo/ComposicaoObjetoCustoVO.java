package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;


public class ComposicaoObjetoCustoVO implements Comparable<ComposicaoObjetoCustoVO>{

	private String nome;
	private String tipo;
	private String descricaoTipo;
	private String versao;
	private String dataInicio;
	private String dataFim;
	private String situacao;
	private String tipoComposicao;
	
	private List<ItemComposicaoPorAtividadeObjetoCustoVO> itensComposicaoPorAtividade;
	private List<ItemComposicaoPorRecursoObjetoCustoVO> itensComposicaoPorRecurso;
	private List<ItemPhiObjetoCustoVO> itensPhi;
	private List<ItemDirecionadorRateioObjetoCustoVO> itensDirecionadorRateio;
	private List<ItemClienteObjetoCustoVO> itensCliente;

	@Override
	public int compareTo(ComposicaoObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(other.getTipo(), this.getTipo() )
				.append(this.getNome(), other.getNome())
				.append(this.getVersao(), other.getVersao())
				.toComparison(); 
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public String getTipoComposicao() {
		return tipoComposicao;
	}

	public void setTipoComposicao(String tipoComposicao) {
		this.tipoComposicao = tipoComposicao;
	}

	public String getDescricaoTipo() {
		return descricaoTipo;
	}

	public void setDescricaoTipo(String descricaoTipo) {
		this.descricaoTipo = descricaoTipo;
	}

	public List<ItemComposicaoPorAtividadeObjetoCustoVO> getItensComposicaoPorAtividade() {
		return itensComposicaoPorAtividade;
	}

	public void setItensComposicaoPorAtividade(
			List<ItemComposicaoPorAtividadeObjetoCustoVO> itensComposicaoPorAtividade) {
		this.itensComposicaoPorAtividade = itensComposicaoPorAtividade;
	}

	public List<ItemComposicaoPorRecursoObjetoCustoVO> getItensComposicaoPorRecurso() {
		return itensComposicaoPorRecurso;
	}

	public void setItensComposicaoPorRecurso(
			List<ItemComposicaoPorRecursoObjetoCustoVO> itensComposicaoPorRecurso) {
		this.itensComposicaoPorRecurso = itensComposicaoPorRecurso;
	}

	public List<ItemPhiObjetoCustoVO> getItensPhi() {
		return itensPhi;
	}

	public void setItensPhi(List<ItemPhiObjetoCustoVO> itensPhi) {
		this.itensPhi = itensPhi;
	}

	public List<ItemDirecionadorRateioObjetoCustoVO> getItensDirecionadorRateio() {
		return itensDirecionadorRateio;
	}

	public void setItensDirecionadorRateio(
			List<ItemDirecionadorRateioObjetoCustoVO> itensDirecionadorRateio) {
		this.itensDirecionadorRateio = itensDirecionadorRateio;
	}

	public List<ItemClienteObjetoCustoVO> getItensCliente() {
		return itensCliente;
	}

	public void setItensCliente(List<ItemClienteObjetoCustoVO> itensCliente) {
		this.itensCliente = itensCliente;
	}	
}
