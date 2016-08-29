package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CirurgiasCanceladasVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7102047377830110820L;
	
	private Integer cirSeq;
	private Integer agdSeq;
	private AipPacientes paciente;
	private Short unfSeq;
	private AghEspecialidades especialidade;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private RapServidores pucServidor;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Date dataAgenda;
	private String pciDescricao;
	private String equipe; 
	private String regime;
	private Short sciUnfSeq;
	private Short sciSeqp;
	private Short cspCnvCodigo;
	private String descConv;
	private Byte cspSeq;
	private Date dtCancelamento;
	private Integer nroCancelamentos;
	private String motivoCancelamento;
	private Short mtcSeq;
	private Integer eprPciSeq;
	private String unfSigla;
	private DominioOrigemPacienteCirurgia origemPacienteCirurgia;
	private String origemIntLocal;
	private String nomeEspecialidade;
	private Short seqEspecialidade;
	private String nomePaciente;
	private Integer prontuario;
	private String siglaEspecialidade;
	
	public enum Fields {
		CIR_SEQ ("cirSeq"),
		AGD_SEQ ("agdSeq"),
		EQUIPE ("equipe"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		SEQ_ESPECIALIDADE("seqEspecialidade"),
		NOME_PACIENTE("nomePaciente"),
		SIGLA_ESPECIALIDADE("siglaEspecialidade"),
		PRONTUARIO("prontuario"),
		DT_CANCELAMENTO ("dtCancelamento"),
		MOTIVO_CANCELAMENTO ("motivoCancelamento"),
		NRO_CANCELAMENTOS ("nroCancelamentos"),
		DESC_PROCED ("pciDescricao"),
		REGIME("regime"),  //regime
		UNF_SEQ("unfSeq"),
		UNF_SIGLA ("unfSigla"), // local
		ESPECIALIDADE("especialidade"),
		PUC_SER_MATRICULA ("pucSerMatricula"),
		PUC_SER_VIN_CODIGO ("pucSerVinCodigo"),
		PUC_SERVIDOR ("pucServidor"), // servidor_PUC
		PUC_UNF_SEQ ("pucUnfSeq"),
		PUC_IND_FUNCAO_PROF ("pucIndFuncaoProf"),
		PACIENTE ("paciente"), // paciente
		DT_AGENDA ("dataAgenda"),
		SCI_UNF_SEQ ("sciUnfSeq"),
		SCI_SEQP ("sciSeqp"),
		CSP_CNV_CODIGO ("cspCnvCodigo"),
		CSP_SEQ ("cspSeq"),
		MTC_SEQ("mtcSeq"),
		EPR_PCI_SEQ ("eprPciSeq"),
		DESC_CONV ("descConv"), // convenio
		ORIGEM_PAC_CIRG("origemPacienteCirurgia"),
		ORIGEM_INT_LOCAL("origemIntLocal");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	
	public Integer getAgdSeq() {
		return agdSeq;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}


	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}


	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public RapServidores getPucServidor() {
		return pucServidor;
	}

	public void setPucServidor(RapServidores pucServidor) {
		this.pucServidor = pucServidor;
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


	public Date getDataAgenda() {
		return dataAgenda;
	}


	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}


	public String getPciDescricao() {
		return pciDescricao;
	}


	public void setPciDescricao(String pciDescricao) {
		this.pciDescricao = pciDescricao;
	}


	public String getEquipe() {
		return equipe;
	}


	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}


	public String getRegime() {
		return regime;
	}


	public void setRegime(String regime) {
		this.regime = regime;
	}


	public Short getSciUnfSeq() {
		return sciUnfSeq;
	}


	public void setSciUnfSeq(Short sciUnfSeq) {
		this.sciUnfSeq = sciUnfSeq;
	}


	public Short getSciSeqp() {
		return sciSeqp;
	}


	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}


	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}


	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}


	public Byte getCspSeq() {
		return cspSeq;
	}


	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}


	public Date getDtCancelamento() {
		return dtCancelamento;
	}


	public void setDtCancelamento(Date dtCancelamento) {
		this.dtCancelamento = dtCancelamento;
	}


	public void setNroCancelamentos(Integer nroCancelamentos) {
		this.nroCancelamentos = nroCancelamentos;
	}


	public Integer getNroCancelamentos() {
		return nroCancelamentos;
	}


	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}


	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}


	public Short getMtcSeq() {
		return mtcSeq;
	}


	public void setMtcSeq(Short mtcSeq) {
		this.mtcSeq = mtcSeq;
	}


	public Integer getEprPciSeq() {
		return eprPciSeq;
	}


	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}


	public void setDescConv(String descConv) {
		this.descConv = descConv;
	}


	public String getDescConv() {
		return descConv;
	}


	public String getUnfSigla() {
		return unfSigla;
	}


	public void setUnfSigla(String unfSigla) {
		this.unfSigla = unfSigla;
	}


	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		this.origemPacienteCirurgia = origemPacienteCirurgia;
	}

	public DominioOrigemPacienteCirurgia getOrigemPacienteCirurgia() {
		return origemPacienteCirurgia;
	}

	public void setOrigemIntLocal(String origemIntLocal) {
		this.origemIntLocal = origemIntLocal;
	}

	public String getOrigemIntLocal() {
		return origemIntLocal;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setCirSeq(Integer cirSeq) {
		this.cirSeq = cirSeq;
	}

	public Integer getCirSeq() {
		return cirSeq;
	}
	

	
	public String getDtCancelamentoOrder() {
		if(dtCancelamento != null){
			return DateUtil.dataToString(getDtCancelamento(), "yyyyMMdd");
		}
		return null;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
}
	
