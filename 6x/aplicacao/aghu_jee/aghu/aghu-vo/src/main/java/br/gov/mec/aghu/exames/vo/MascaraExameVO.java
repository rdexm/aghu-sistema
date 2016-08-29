package br.gov.mec.aghu.exames.vo;

public class MascaraExameVO {

	private Integer soeSeq;

	private Short seqp;

	private Integer codigoPaciente;

	private Integer prontuarioPaciente;

	private String nomePaciente;

	private String siglaExame;

	private String descricaoExame;

	private Integer seqMaterialAnalise;

	private String descricaoMaterialAnalise;

	public MascaraExameVO(Integer soeSeq, Short seqp, Integer codigoPaciente, Integer prontuarioPaciente, String nomePaciente,
			String siglaExame, String descricaoExame, Integer seqMaterialAnalise, String descricaoMaterialAnalise) {
		super();
		this.soeSeq = soeSeq;
		this.seqp = seqp;
		this.codigoPaciente = codigoPaciente;
		this.prontuarioPaciente = prontuarioPaciente;
		this.nomePaciente = nomePaciente;
		this.siglaExame = siglaExame;
		this.descricaoExame = descricaoExame;
		this.seqMaterialAnalise = seqMaterialAnalise;
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public Integer getSeqMaterialAnalise() {
		return seqMaterialAnalise;
	}

	public void setSeqMaterialAnalise(Integer seqMaterialAnalise) {
		this.seqMaterialAnalise = seqMaterialAnalise;
	}

	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}

	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}

}
