package br.gov.mec.aghu.paciente.vo;

public class ConvenioExamesLaudosVO {

	private Short codigoConvenioSaude;
	private Byte codigoConvenioSaudePlano;
	private String descricaoConvenio;

	public ConvenioExamesLaudosVO() {
		super();
	}

	public ConvenioExamesLaudosVO(Short codigoConvenioSaude,
			Byte codigoConvenioSaudePlano, String descricaoConvenio) {
		super();
		this.codigoConvenioSaude = codigoConvenioSaude;
		this.codigoConvenioSaudePlano = codigoConvenioSaudePlano;
		this.descricaoConvenio = descricaoConvenio;
	}

	public Short getCodigoConvenioSaude() {
		return codigoConvenioSaude;
	}

	public void setCodigoConvenioSaude(Short codigoConvenioSaude) {
		this.codigoConvenioSaude = codigoConvenioSaude;
	}

	public Byte getCodigoConvenioSaudePlano() {
		return codigoConvenioSaudePlano;
	}

	public void setCodigoConvenioSaudePlano(Byte codigoConvenioSaudePlano) {
		this.codigoConvenioSaudePlano = codigoConvenioSaudePlano;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

}
