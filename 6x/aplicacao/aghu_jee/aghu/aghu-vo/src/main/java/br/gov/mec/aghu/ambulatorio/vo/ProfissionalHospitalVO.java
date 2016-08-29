package br.gov.mec.aghu.ambulatorio.vo;

public class ProfissionalHospitalVO {

	private String profissional;
	
	private String vinculo;
	
	private Integer matricula;
	
	private Short vinCodigo;
	
	private String conselho;
	
	private String registro;

	public ProfissionalHospitalVO() {
		super();
	}

	public ProfissionalHospitalVO(String profissional, String vinculo,
			Integer matricula, Short vinCodigo, String conselho, String registro) {
		super();
		this.profissional = profissional;
		this.vinculo = vinculo;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.conselho = conselho;
		this.registro = registro;
	}

	public String getProfissional() {
		return profissional;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}

	public String getVinculo() {
		return vinculo;
	}

	public void setVinculo(String vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}
	
	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public enum Fields {
		
		PROFISSIONAL("profissional"),
		VINCULO("vinculo"),
		CONSELHO("conselho"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		REGISTRO("registro");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
