package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ArquivoSUSVO implements Serializable {

	private static final long serialVersionUID = -2162207109640771284L;

	public ArquivoSUSVO() {
		linhas = new ArrayList<String>();
	}

	private String nomeArquivo;
	private List<String> linhas;
	
	public void addLinha(String linha){
		linhas.add(linha);
	}
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	public List<String> getLinhas() {
		return linhas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nomeArquivo == null) ? 0 : nomeArquivo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ArquivoSUSVO other = (ArquivoSUSVO) obj;
		if (nomeArquivo == null) {
			if (other.nomeArquivo != null) {
				return false;
			}
		} else if (!nomeArquivo.equals(other.nomeArquivo)) {
			return false;
		}
		return true;
	}

}