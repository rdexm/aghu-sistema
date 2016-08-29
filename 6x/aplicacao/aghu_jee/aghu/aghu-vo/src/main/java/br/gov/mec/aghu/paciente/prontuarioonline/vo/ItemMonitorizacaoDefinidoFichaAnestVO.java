package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;


public class ItemMonitorizacaoDefinidoFichaAnestVO {
	
	private Long seqMbcFichaAnestesia;
	private Integer matCodigoMedicamento;
	private String descricaoMedicamento;
	private String siglaViaAdministracao;
	private Double doseMbcFichaMedicPreAnest;
	private Integer seqUnidadeMedidaMedica;
	private String descricaoAfaTipoApresMdto;
	private String descricaoUnidadeMedidaMedica;
	private DominioSimNao medicacaoPreAnestesica;

	public enum Fields {
		SEQ_FICHA_ANESTESIA("seqMbcFichaAnestesia"),
		MAT_CODIGO_MEDICAMENTO("matCodigoMedicamento"),
		DESCRICAO_MEDICAMENTO("descricaoMedicamento"),
		SIGLA_VIA_ADMINISTRACAO("siglaViaAdministracao"),
		DOSE_FICHA_MEDIC_ANEST("doseMbcFichaMedicPreAnest"),
		SEQ_UNIDADE_MEDIDA_MEDICA("seqUnidadeMedidaMedica"),
		DESCRICAO_TIPO_APRES_MDTO("descricaoAfaTipoApresMdto"),
		DESCRICAO_UNIDADE_MEDIDA_MEDICA("descricaoUnidadeMedidaMedica"),
		MEDICACAO_PRE_ANESTESICA("medicacaoPreAnestesica");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getSeqMbcFichaAnestesia() {
		return seqMbcFichaAnestesia;
	}

	public Integer getMatCodigoMedicamento() {
		return matCodigoMedicamento;
	}

	public String getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public String getSiglaViaAdministracao() {
		return siglaViaAdministracao;
	}

	public Double getDoseMbcFichaMedicPreAnest() {
		return doseMbcFichaMedicPreAnest;
	}

	public Integer getSeqUnidadeMedidaMedica() {
		return seqUnidadeMedidaMedica;
	}

	public String getDescricaoAfaTipoApresMdto() {
		return descricaoAfaTipoApresMdto;
	}

	public String getDescricaoUnidadeMedidaMedica() {
		return descricaoUnidadeMedidaMedica;
	}

	public void setSeqMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		this.seqMbcFichaAnestesia = seqMbcFichaAnestesia;
	}

	public void setMatCodigoMedicamento(Integer matCodigoMedicamento) {
		this.matCodigoMedicamento = matCodigoMedicamento;
	}

	public void setDescricaoMedicamento(String descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public void setSiglaViaAdministracao(String siglaViaAdministracao) {
		this.siglaViaAdministracao = siglaViaAdministracao;
	}

	public void setDoseMbcFichaMedicPreAnest(Double doseMbcFichaMedicPreAnest) {
		this.doseMbcFichaMedicPreAnest = doseMbcFichaMedicPreAnest;
	}

	public void setSeqUnidadeMedidaMedica(Integer seqUnidadeMedidaMedica) {
		this.seqUnidadeMedidaMedica = seqUnidadeMedidaMedica;
	}

	public void setDescricaoAfaTipoApresMdto(String descricaoAfaTipoApresMdto) {
		this.descricaoAfaTipoApresMdto = descricaoAfaTipoApresMdto;
	}

	public void setDescricaoUnidadeMedidaMedica(String descricaoUnidadeMedidaMedica) {
		this.descricaoUnidadeMedidaMedica = descricaoUnidadeMedidaMedica;
	}

	public DominioSimNao getMedicacaoPreAnestesica() {
		return medicacaoPreAnestesica;
	}

	public void setMedicacaoPreAnestesica(DominioSimNao medicacaoPreAnestesica) {
		this.medicacaoPreAnestesica = medicacaoPreAnestesica;
	}
}
