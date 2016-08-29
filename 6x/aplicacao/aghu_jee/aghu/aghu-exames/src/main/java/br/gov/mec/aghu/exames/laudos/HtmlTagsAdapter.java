package br.gov.mec.aghu.exames.laudos;

public class HtmlTagsAdapter {

	private String origem;
	
	private String destino;

	public HtmlTagsAdapter(String origem, String destino) {
		super();
		this.origem = origem;
		this.destino = destino;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}
	
	
	
}
