package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioConvenio;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;

public class PortalPesquisaCirurgiasParametrosVO implements Serializable {

	private static final long serialVersionUID = -3203682008971744589L;
	
	private Short unfSeq;
	private Short espSeq;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Integer pacCodigo;
	private Integer pciSeqPortal;
	private Date dataInicio;
	private Date dataFim; 
	private Short sala;
	private DominioConvenio convenio;
	
	//22364
	private Integer pacProntuario;
	private String pacNome;
	
	private Short desmarcar;
	private Short desmarcarAdm;
	
	public enum Fields {
		UNF_SEQ("unfSeq"),
		ESP_SEQ("espSeq"),
		PUC_MATRICULA("pucSerMatricula"),
		PUC_VINCULO("pucSerVinCodigo"),
		PUC_UNF_SEQ("pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("pucIndFuncaoProf"),
		PAC_CODIGO("paccodigo"),
		PCI_SEQ_PORTAL("pciSeqPortal"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		SALA("sala"),
		CONVENIO("convenio");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPciSeqPortal() {
		return pciSeqPortal;
	}

	public void setPciSeqPortal(Integer pciSeqPortal) {
		this.pciSeqPortal = pciSeqPortal;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Short getSala() {
		return sala;
	}

	public void setSala(Short sala) {
		this.sala = sala;
	}

	public DominioConvenio getConvenio() {
		return convenio;
	}

	public void setConvenio(DominioConvenio convenio) {
		this.convenio = convenio;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getPacNome() {
		return pacNome;
	}

	public Short getDesmarcar() {
		return desmarcar;
	}

	public void setDesmarcar(Short desmarcar) {
		this.desmarcar = desmarcar;
	}

	public Short getDesmarcarAdm() {
		return desmarcarAdm;
	}

	public void setDesmarcarAdm(Short desmarcarAdm) {
		this.desmarcarAdm = desmarcarAdm;
	}
}

