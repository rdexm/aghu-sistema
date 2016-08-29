package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ProtocoloItensMedicamentoVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3597150508081609892L;
	
	private Long pimSeq;
	private String vdmDescricao;
	private String pimObservacao;
	private BigDecimal pimDose;
	private VMpmDosagem dosagem;
	private Integer medMatCodigo;
	private VMpmDosagem mpmDosagem;
	private String ummDescricao;
	private Integer ummSeq;
	private Integer fdsSeq;
	private Boolean edita;
	
	/**
	 * Grid
	 */
	private ListaAfaDescMedicamentoTipoUsoMedicamentoVO medicamentoSb;
	private Boolean indMedicamentoPadronizado;
	private String complementoMedicamento;
	private BigDecimal doseMedicamento;
	private VMpmDosagem comboUnidade;
	
	/**
	 * #50323
	 */
	private List<ParametroDoseUnidadeVO> listaParametroCalculo = new ArrayList<ParametroDoseUnidadeVO>();
	
	public enum Fields {

		PIM_SEQ("pimSeq"), 
		VDM_DESCRICAO("vdmDescricao"), 
		PIM_OBSERVACAO("pimObservacao"), 
		PIM_DOSE("pimDose"), 
		MED_MAT_CODIGO("medMatCodigo"),
		UMM("ummDescricao"),
		UMM_SEQ("ummSeq"),
		FDS_SEQ("fdsSeq"),
		UMM_DESCRICAO("dosagem.seqUnidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Long getPimSeq() {
		return pimSeq;
	}

	public void setPimSeq(Long pimSeq) {
		this.pimSeq = pimSeq;
	}

	public String getVdmDescricao() {
		return vdmDescricao;
	}

	public void setVdmDescricao(String vdmDescricao) {
		this.vdmDescricao = vdmDescricao;
	}

	public String getPimObservacao() {
		return pimObservacao;
	}

	public void setPimObservacao(String pimObservacao) {
		this.pimObservacao = pimObservacao;
	}

	public BigDecimal getPimDose() {
		return pimDose;
	}

	public void setPimDose(BigDecimal pimDose) {
		this.pimDose = pimDose;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public VMpmDosagem getMpmDosagem() {
		return mpmDosagem;
	}

	public void setMpmDosagem(VMpmDosagem mpmDosagem) {
		this.mpmDosagem = mpmDosagem;
	}

	public VMpmDosagem getDosagem() {
		return dosagem;
	}

	public void setDosagem(VMpmDosagem dosagem) {
		this.dosagem = dosagem;
	}

	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO getMedicamentoSb() {
		return medicamentoSb;
	}

	public void setMedicamentoSb(
			ListaAfaDescMedicamentoTipoUsoMedicamentoVO medicamentoSb) {
		this.medicamentoSb = medicamentoSb;
	}

	public Boolean getIndMedicamentoPadronizado() {
		return indMedicamentoPadronizado;
	}

	public void setIndMedicamentoPadronizado(Boolean indMedicamentoPadronizado) {
		this.indMedicamentoPadronizado = indMedicamentoPadronizado;
	}

	public String getComplementoMedicamento() {
		return complementoMedicamento;
	}

	public void setComplementoMedicamento(String complementoMedicamento) {
		this.complementoMedicamento = complementoMedicamento;
	}

	public BigDecimal getDoseMedicamento() {
		return doseMedicamento;
	}

	public void setDoseMedicamento(BigDecimal doseMedicamento) {
		this.doseMedicamento = doseMedicamento;
	}

	public VMpmDosagem getComboUnidade() {
		return comboUnidade;
	}

	public void setComboUnidade(VMpmDosagem comboUnidade) {
		this.comboUnidade = comboUnidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((medMatCodigo == null) ? 0 : medMatCodigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ProtocoloItensMedicamentoVO other = (ProtocoloItensMedicamentoVO) obj;
		if (medMatCodigo == null) {
			if (other.medMatCodigo != null){
				return false;
			}
		} else if (!medMatCodigo.equals(other.medMatCodigo)){
			return false;
		}
		return true;
	}

	public String getUmmDescricao() {
		return ummDescricao;
	}

	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}

	public Integer getUmmSeq() {
		return ummSeq;
	}

	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}

	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}

	public List<ParametroDoseUnidadeVO> getListaParametroCalculo() {
		return listaParametroCalculo;
	}

	public void setListaParametroCalculo(
			List<ParametroDoseUnidadeVO> listaParametroCalculo) {
		this.listaParametroCalculo = listaParametroCalculo;
	}
	
	public Boolean getEdita() {
		return edita;
	}

	public void setEdita(Boolean edita) {
		this.edita = edita;
	}
}
