package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioOutrosFarmacos;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaPrincFarmacoId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.view.VAfaDescrMdto;

public class AltaSumarioInfoComplVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5600227126079006874L;
	
	private MpmAltaSumarioId idAltaSumario;
	
	private MpmAltaPrincFarmacoId idAltaPrincFarmaco;
	private MpmAltaSumarioId idAltaComplFarmaco;
	private VAfaDescrMdto vAfaDescrMdto;
	private String complemento;
	private String descricaoMedicamento;
	private DominioOutrosFarmacos radioInformacoesComplementares;
	
	private Boolean ehListaDescrMdtoAtivos;
	private Boolean ehDescricaoMedicamento;
	private Boolean emEdicao = false;
	
	private AfaMedicamento medicamento;
		

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(String descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public Boolean getEhListaDescrMdtoAtivos() {
		return ehListaDescrMdtoAtivos;
	}

	public void setEhListaDescrMdtoAtivos(Boolean ehListaDescrMdtoAtivos) {
		this.ehListaDescrMdtoAtivos = ehListaDescrMdtoAtivos;
	}

	public Boolean getEhDescricaoMedicamento() {
		return ehDescricaoMedicamento;
	}

	public void setEhDescricaoMedicamento(Boolean ehDescricaoMedicamento) {
		this.ehDescricaoMedicamento = ehDescricaoMedicamento;
	}

	public DominioOutrosFarmacos getRadioInformacoesComplementares() {
		return radioInformacoesComplementares;
	}

	public void setRadioInformacoesComplementares(
			DominioOutrosFarmacos radioInformacoesComplementares) {
		this.radioInformacoesComplementares = radioInformacoesComplementares;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	/**
	 * @param vAfaDescrMdto the vAfaDescrMdto to set
	 */
	public void setvAfaDescrMdto(VAfaDescrMdto vAfaDescrMdto) {
		this.vAfaDescrMdto = vAfaDescrMdto;
	}

	/**
	 * @return the vAfaDescrMdto
	 */
	public VAfaDescrMdto getvAfaDescrMdto() {
		return vAfaDescrMdto;
	}

	/**
	 * @param medicamento the medicamento to set
	 */
	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	/**
	 * @return the medicamento
	 */
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	/**
	 * @param idAltaSumario the idAltaSumario to set
	 */
	public void setIdAltaSumario(MpmAltaSumarioId idAltaSumario) {
		this.idAltaSumario = idAltaSumario;
	}

	/**
	 * @return the idAltaSumario
	 */
	public MpmAltaSumarioId getIdAltaSumario() {
		return idAltaSumario;
	}

	public MpmAltaPrincFarmacoId getIdAltaPrincFarmaco() {
		return idAltaPrincFarmaco;
	}

	public void setIdAltaPrincFarmaco(MpmAltaPrincFarmacoId idAltaPrincFarmaco) {
		this.idAltaPrincFarmaco = idAltaPrincFarmaco;
	}

	public MpmAltaPrincFarmaco getModelMpmAltaPrincFarmaco() {
		MpmAltaPrincFarmaco mpmAltaPrincFarmaco = new MpmAltaPrincFarmaco();
		
		if (StringUtils.isNotBlank(this.descricaoMedicamento)) {
			mpmAltaPrincFarmaco.setDescMedicamento(this.descricaoMedicamento);
		} else if (this.getvAfaDescrMdto() != null 
				&& this.getvAfaDescrMdto().getMedicamento() != null) {
			mpmAltaPrincFarmaco.setDescMedicamento(this.getvAfaDescrMdto().getMedicamento().getDescricaoEditada());
			mpmAltaPrincFarmaco.setMedicamento(this.getvAfaDescrMdto().getMedicamento());
		}
		
		mpmAltaPrincFarmaco.setIndCarga(false);
		mpmAltaPrincFarmaco.setIndSituacao(DominioSituacao.A);
		return mpmAltaPrincFarmaco;
	}

	public MpmAltaSumarioId getIdAltaComplFarmaco() {
		return idAltaComplFarmaco;
	}

	public void setIdAltaComplFarmaco(MpmAltaSumarioId idAltaComplFarmaco) {
		this.idAltaComplFarmaco = idAltaComplFarmaco;
	}

}