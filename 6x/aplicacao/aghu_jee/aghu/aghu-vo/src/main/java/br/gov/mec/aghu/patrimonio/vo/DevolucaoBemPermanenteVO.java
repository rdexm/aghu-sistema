package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;


public class DevolucaoBemPermanenteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3376468285325083379L;

	private Long pbpSeq;
	
	private String pbpNrSerie;
	
	private Long pbpNrBem;

	private Integer sirpNrpSeq;
	
	private Integer sirpNroItem;
	
	private Integer sirpEslSeq; 
	
	private Integer matCodigo;
	
	private Integer avtSeq;
	
	private String matNome;
	
	private Long pirpSeq;
	
	private boolean selecionado;
	
	private String pbpDetalhamento;

	public enum Fields {

		PBP_SEQ("pbpSeq"),
		AVT_SEQ("avtSeq"),
		PBP_NUMERO_SERIE("pbpNrSerie"),
		PBP_NUMERO_BEM("pbpNrBem"),
		SIRP_NUMERO_SEQ("sirpNrpSeq"),
		SIRP_NUMERO_ITEM("sirpNroItem"),
		SIRP_ESL("sirpEslSeq"),
		MAT_CODIGO("matCodigo"),
		MAT_NOME("matNome"),
		SELECIONADO("selecionado"),
		PIRP_SEQ("pirpSeq"),
		PBP_DETALHAMENTO("pbpDetalhamento");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	public String obterRecebItem() {

		String recebItem = StringUtils.EMPTY;

		if (sirpNrpSeq != null && sirpNroItem != null) {
			recebItem = sirpNrpSeq.toString() + "/" + sirpNroItem.toString();
		} else if (sirpNrpSeq != null && sirpNroItem == null) {
			recebItem = sirpNrpSeq.toString();
		} else if (sirpNrpSeq == null && sirpNroItem != null) {
			recebItem = sirpNroItem.toString();
		}
		return recebItem;
	}
	
	public String obterCodigoMaterial() {
		String codigoMaterial = "";
		
		if (matCodigo != null && matNome != null) {
			codigoMaterial = matCodigo.toString() + "/" + matNome;
		} else if (matCodigo != null && matNome == null) {
			codigoMaterial = matCodigo.toString();
		} else if (matCodigo == null && matNome != null) {
			codigoMaterial = matNome;
		}
		return codigoMaterial;
	}

	public Long getPbpSeq() {
		return pbpSeq;
	}

	public void setPbpSeq(Long pbpSeq) {
		this.pbpSeq = pbpSeq;
	}

	public String getPbpNrSerie() {
		return pbpNrSerie;
	}

	public void setPbpNrSerie(String pbpNrSerie) {
		this.pbpNrSerie = pbpNrSerie;
	}

	public Long getPbpNrBem() {
		return pbpNrBem;
	}

	public void setPbpNrBem(Long pbpNrBem) {
		this.pbpNrBem = pbpNrBem;
	}

	public Integer getSirpNrpSeq() {
		return sirpNrpSeq;
	}

	public void setSirpNrpSeq(Integer sirpNrpSeq) {
		this.sirpNrpSeq = sirpNrpSeq;
	}

	public Integer getSirpNroItem() {
		return sirpNroItem;
	}

	public void setSirpNroItem(Integer sirpNroItem) {
		this.sirpNroItem = sirpNroItem;
	}

	public Integer getSirpEslSeq() {
		return sirpEslSeq;
	}

	public void setSirpEslSeq(Integer sirpEslSeq) {
		this.sirpEslSeq = sirpEslSeq;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public Long getPirpSeq() {
		return pirpSeq;
	}

	public void setPirpSeq(Long pirpSeq) {
		this.pirpSeq = pirpSeq;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getPbpDetalhamento() {
		return pbpDetalhamento;
	}

	public void setPbpDetalhamento(String pbpDetalhamento) {
		this.pbpDetalhamento = pbpDetalhamento;
	}

	public Integer getAvtSeq() {
		return avtSeq;
	}

	public void setAvtSeq(Integer avtSeq) {
		this.avtSeq = avtSeq;
	}
	
}