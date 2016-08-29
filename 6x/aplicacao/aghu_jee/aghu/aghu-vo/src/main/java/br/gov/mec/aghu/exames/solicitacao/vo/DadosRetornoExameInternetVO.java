package br.gov.mec.aghu.exames.solicitacao.vo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DadosRetornoExameInternetVO {

	private String soeSeq;
	private BigInteger seqGrupo;
	private String localizador;
	private List<String> descricaoErro;

	public String getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(String soeSeq) {
		this.soeSeq = soeSeq;
	}

	public BigInteger getSeqGrupo() {
		return seqGrupo;
	}

	public void setSeqGrupo(BigInteger seqGrupo) {
		this.seqGrupo = seqGrupo;
	}

	public String getLocalizador() {
		return localizador;
	}

	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}

	public List<String> getDescricaoErro() {
		if (descricaoErro == null) {
			descricaoErro = new ArrayList<String>();
		}
		
		return descricaoErro;
	}

	public void setDescricaoErro(List<String> descricaoErro) {
		this.descricaoErro = descricaoErro;
	}

}
