package br.gov.mec.aghu.blococirurgico.vo;


public class SuggestionListaCirurgiaVO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8382502209067963353L;

	public SuggestionListaCirurgiaVO() {}
	
	public SuggestionListaCirurgiaVO(String nome, boolean equipeDoUsuario, Integer matricula, Short vinCodigo) {
		super();
		this.nome = nome;
		this.equipeDoUsuario = equipeDoUsuario;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
	}



	private String nome;
	
	private String sigla;
	
	private Short seq;
	
	private Integer matricula;

	private Short vinCodigo;
	
	private Integer eprPciSeq;
	
	private Integer grcSeq;
	
	private boolean equipeDoUsuario;
	

	public enum Fields {
		SEQ("seq"),
		NOME("nome"),
		SIGLA("sigla"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		EPR_PCI_SEQ("eprPciSeq"),
		GRC_SEQ("grcSeq"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getEprPciSeq() {
		return eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	public Integer getGrcSeq() {
		return grcSeq;
	}

	public void setGrcSeq(Integer grcSeq) {
		this.grcSeq = grcSeq;
	}

	public boolean isEquipeDoUsuario() {
		return equipeDoUsuario;
	}

	public void setEquipeDoUsuario(boolean equipeDoUsuario) {
		this.equipeDoUsuario = equipeDoUsuario;
	}

}
