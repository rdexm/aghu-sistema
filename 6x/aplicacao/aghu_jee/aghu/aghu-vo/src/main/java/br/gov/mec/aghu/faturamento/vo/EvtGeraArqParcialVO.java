package br.gov.mec.aghu.faturamento.vo;

import java.net.URI;
import java.util.List;

import br.gov.mec.aghu.model.FatEspelhoAih;

public class EvtGeraArqParcialVO {

	private final List<FatEspelhoAih> listaEspelhos;
	private final List<URI> listaURIArquivos;
	private final URI uriLog;
	private final String prefixoArquivoSaida;
	private final int qtdRegistros;

	public EvtGeraArqParcialVO(final List<FatEspelhoAih> listaEspelhos, final List<URI> listaURIArquivos, final URI uriLog, final String prefixoArquivoSaida,
			final int qtdRegistros) {

		super();

		if (listaEspelhos == null) {
			throw new IllegalArgumentException("Parametro listaEspelhos nao informado!!!");
		}
		if (listaURIArquivos == null) {
			throw new IllegalArgumentException("Parametro listaURIArquivos nao informado!!!");
		}
		if (uriLog == null) {
			throw new IllegalArgumentException("Parametro uriLog nao informado!!!");
		}
		if (prefixoArquivoSaida == null) {
			throw new IllegalArgumentException("Parametro prefixoArquivoSaida nao informado!!!");
		}
		if (prefixoArquivoSaida.trim().isEmpty()) {
			throw new IllegalArgumentException("Parametro prefixoArquivoSaida nao informado!!!");
		}
		this.listaEspelhos = listaEspelhos;
		this.listaURIArquivos = listaURIArquivos;
		this.uriLog = uriLog;
		this.prefixoArquivoSaida = prefixoArquivoSaida.trim();
		this.qtdRegistros = qtdRegistros;
	}

	public List<FatEspelhoAih> getListaEspelhos() {

		return this.listaEspelhos;
	}

	public List<URI> getListaURIArquivos() {

		return this.listaURIArquivos;
	}

	public URI getUriLog() {

		return this.uriLog;
	}

	public String getPrefixoArquivoSaida() {

		return this.prefixoArquivoSaida;
	}

	public int getQtdRegistros() {

		return this.qtdRegistros;
	}
}
