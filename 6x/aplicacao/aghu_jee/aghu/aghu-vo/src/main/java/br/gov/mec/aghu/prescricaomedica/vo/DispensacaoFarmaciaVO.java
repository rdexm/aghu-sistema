package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.cups.ImpImpressora;

public class DispensacaoFarmaciaVO implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -657375201658876850L;
	private String prescricaoMedicaSeq;
	private String dthrInicio;
	private String dthrFim;
	private String prontuario;
	private String localID;
	private String especialidade;
	private String medicamento;
//	private String qtdSolicitada;
	private String serVinCodigo;
	private String solicitante;
	private String nroRegistro;
	private String sigla;
	private String criadoEm;
	private String equipe;
	//private String codigoMedicamento;
	private String nomePaciente;
	private String local;
	private Integer ordemTela;
	private String statusItem;
	private MpmPrescricaoMdto prescricaoMdto;
	private List<String> farmacias = new ArrayList<String>();
	private List<String> impressoras = new ArrayList<String>();
	private String farmacia;
	private String impressora;
	private String clinica;
	private List<ItemDispensacaoFarmaciaVO> itensDispensacaoFarmacia;

	/**
	 * Alteração no VO para adoção de <code>ImpImpressora</code> ao invés de
	 * String com caminho de rede.
	 */
	private Set<ImpImpressora> impressorasCups = new HashSet<ImpImpressora>();
	private ImpImpressora impressoraCups;

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getPrescricaoMedicaSeq() {
		return prescricaoMedicaSeq;
	}

	public void setPrescricaoMedicaSeq(String prescricaoMedicaSeq) {
		this.prescricaoMedicaSeq = prescricaoMedicaSeq;
	}

	public String getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(String dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public String getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(String dthrFim) {
		this.dthrFim = dthrFim;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getLocalID() {
		return localID;
	}

	public void setLocalID(String localID) {
		this.localID = localID;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(String serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getNroRegistro() {
		return nroRegistro;
	}

	public void setNroRegistro(String nroRegistro) {
		this.nroRegistro = nroRegistro;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}


	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}

	public String getStatusItem() {
		return statusItem;
	}

	public void setStatusItem(String statusItem) {
		this.statusItem = statusItem;
	}

	public MpmPrescricaoMdto getPrescricaoMdto() {
		return prescricaoMdto;
	}

	public void setPrescricaoMdto(MpmPrescricaoMdto prescricaoMdto) {
		this.prescricaoMdto = prescricaoMdto;
	}

	public List<String> getFarmacias() {
		return farmacias;
	}

	public void setFarmacias(List<String> farmacias) {
		this.farmacias = farmacias;
	}

	@Deprecated
	public List<String> getImpressoras() {
		return impressoras;
	}

	@Deprecated
	public void setImpressoras(List<String> impressoras) {
		this.impressoras = impressoras;
	}

	public String getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}

	public String getImpressora() {
		return impressora;
	}

	public void setImpressora(String impressora) {
		this.impressora = impressora;
	}

	public Set<ImpImpressora> getImpressorasCups() {
		return impressorasCups;
	}

	public void setImpressorasCups(Set<ImpImpressora> impressorasCups) {
		this.impressorasCups = impressorasCups;
	}
	
	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}
	
	

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public List<ItemDispensacaoFarmaciaVO> getItensDispensacaoFarmacia() {
		return itensDispensacaoFarmacia;
	}

	public void setItensDispensacaoFarmacia(
			List<ItemDispensacaoFarmaciaVO> itensDispensacaoFarmacia) {
		this.itensDispensacaoFarmacia = itensDispensacaoFarmacia;
	}

	public DispensacaoFarmaciaVO copiar() {
		try {
			return (DispensacaoFarmaciaVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

	public ImpImpressora getImpressoraCups() {
		return impressoraCups;
	}

	public void setImpressoraCups(ImpImpressora impressoraCups) {
		this.impressoraCups = impressoraCups;
	}

}
