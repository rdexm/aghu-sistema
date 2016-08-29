package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ReferencialEspecialidadeProfissonalGridVO extends ReferencialEspecialidadeProfissonalViewVO implements BaseBean{
	
	// Campos da grid no form AINF_PES_REF_ESP_PRO
	private String equipe; // Equipe
	private String crm; // Crm do profissional
	private String pac; // Pacientes do referencial
	private String pacElet; // Pacientes eletivos
	private String pacUrg;	// Pacientes origem urgencia
	private String blq ; // Quantidade de bloqueios
	private String dif;	// Diferença entre pacientes internos e referencial
	private String cti; // Quantidade de internações em CTI
	private String aptos; // Quantidade de internações em aptos
	private String outrasUn; // Total de internações em unidades que não pertencem ao referencial
	private String outrasClin; // Total de internações ocupando leitos de outras clinicas
	private String total;
	
	/**
	 * Construtor padrão da classe.
	 */
	public ReferencialEspecialidadeProfissonalGridVO() {
	}

	public ReferencialEspecialidadeProfissonalGridVO(
			ReferencialEspecialidadeProfissonalViewVO referencialEPViewVO) {
		setOrdem(referencialEPViewVO.getOrdem()); 
		setEspSeq(referencialEPViewVO.getEspSeq()); 
		setEspVinculo(referencialEPViewVO.getEspVinculo()); 
		setEspMatricula(referencialEPViewVO.getEspMatricula());
		setCapacReferencial(referencialEPViewVO.getCapacReferencial());
		setInhMediaPermanencia(referencialEPViewVO.getInhMediaPermanencia());
		setInhMediaPacienteDia(referencialEPViewVO.getInhMediaPacienteDia());
		setInhPercentualOcupacao(referencialEPViewVO.getInhPercentualOcupacao());
	}

	public String getEquipe() {
		return this.equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getCrm() {
		return this.crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getPac() {
		return this.pac;
	}

	public void setPac(String pac) {
		this.pac = pac;
	}

	public String getPacElet() {
		return this.pacElet;
	}

	public void setPacElet(String pacElet) {
		this.pacElet = pacElet;
	}

	public String getPacUrg() {
		return this.pacUrg;
	}

	public void setPacUrg(String pacUrg) {
		this.pacUrg = pacUrg;
	}

	public String getBlq() {
		return this.blq;
	}

	public void setBlq(String blq) {
		this.blq = blq;
	}

	public String getDif() {
		return this.dif;
	}

	public void setDif(String dif) {
		this.dif = dif;
	}

	public String getCti() {
		return this.cti;
	}

	public void setCti(String cti) {
		this.cti = cti;
	}

	public String getAptos() {
		return this.aptos;
	}

	public void setAptos(String aptos) {
		this.aptos = aptos;
	}

	public String getOutrasUn() {
		return this.outrasUn;
	}

	public void setOutrasUn(String outrasUn) {
		this.outrasUn = outrasUn;
	}

	public String getOutrasClin() {
		return this.outrasClin;
	}

	public void setOutrasClin(String outrasClin) {
		this.outrasClin = outrasClin;
	}

	public String getTotal() {
		return this.total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
}