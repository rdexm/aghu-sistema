package br.gov.mec.aghu.exames.pesquisa.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoColeta;

@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class VAelExamesAtdDiversosVO implements java.io.Serializable {

	private static final long serialVersionUID = 5916293869901598014L;

	private Integer soeSeq;
	private Short seqp;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Date criadoEm;
	private String sitCodigo;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Short ufeSeq;
	private Boolean indLiberaResultAutom;
	private String descricaoUsualExame;
	private String descricaoMaterial;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short moqSeq;
	private Boolean indPermiteIncluirResultado;
	private Date dataHoraEvento;
	private DominioTipoColeta tipoColeta;
	private String nomeUsualMaterial;
	// private String localizacao;
	private Date dthrProgramada;
	private String iseDescMatAnalise;
	private String einTipo;
	private String indCci;
	private Long numeroAp;
	private Integer pjqSeq;
	private Integer laeSeq;
	private Integer ccqSeq;
	private Integer ddvSeq;
	private DominioIndImpressoLaudo indImpressoLaudo;
	private Integer cadSeq;
	private Integer atvPacCodigo;
	private String pacOruAccNumber;
	private String prontuario;
	private String nomePaciente;
	private String sexoPaciente;
	private DominioOrigemAtendimento origem;
	private Integer idade;
	private Integer pacCodigo;
	private String sitDescricao;
	private String alterado;
	private boolean verResultado = false;

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

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getSitCodigo() {
		return sitCodigo;
	}

	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Short getUfeSeq() {
		return ufeSeq;
	}

	public void setUfeSeq(Short ufeSeq) {
		this.ufeSeq = ufeSeq;
	}

	public Boolean getIndLiberaResultAutom() {
		return indLiberaResultAutom;
	}

	public void setIndLiberaResultAutom(Boolean indLiberaResultAutom) {
		this.indLiberaResultAutom = indLiberaResultAutom;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Short getMoqSeq() {
		return moqSeq;
	}

	public void setMoqSeq(Short moqSeq) {
		this.moqSeq = moqSeq;
	}

	public Boolean getIndPermiteIncluirResultado() {
		return indPermiteIncluirResultado;
	}

	public void setIndPermiteIncluirResultado(Boolean indPermiteIncluirResultado) {
		this.indPermiteIncluirResultado = indPermiteIncluirResultado;
	}

	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}

	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}

	public Date getDthrProgramada() {
		return dthrProgramada;
	}

	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	public String getIseDescMatAnalise() {
		return iseDescMatAnalise;
	}

	public void setIseDescMatAnalise(String iseDescMatAnalise) {
		this.iseDescMatAnalise = iseDescMatAnalise;
	}

	public String getEinTipo() {
		return einTipo;
	}

	public void setEinTipo(String einTipo) {
		this.einTipo = einTipo;
	}

	public String getIndCci() {
		return indCci;
	}

	public void setIndCci(String indCci) {
		this.indCci = indCci;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Integer getPjqSeq() {
		return pjqSeq;
	}

	public void setPjqSeq(Integer pjqSeq) {
		this.pjqSeq = pjqSeq;
	}

	public Integer getLaeSeq() {
		return laeSeq;
	}

	public void setLaeSeq(Integer laeSeq) {
		this.laeSeq = laeSeq;
	}

	public Integer getCcqSeq() {
		return ccqSeq;
	}

	public void setCcqSeq(Integer ccqSeq) {
		this.ccqSeq = ccqSeq;
	}

	public Integer getDdvSeq() {
		return ddvSeq;
	}

	public void setDdvSeq(Integer ddvSeq) {
		this.ddvSeq = ddvSeq;
	}

	public DominioIndImpressoLaudo getIndImpressoLaudo() {
		return indImpressoLaudo;
	}

	public void setIndImpressoLaudo(DominioIndImpressoLaudo indImpressoLaudo) {
		this.indImpressoLaudo = indImpressoLaudo;
	}

	public Integer getCadSeq() {
		return cadSeq;
	}

	public void setCadSeq(Integer cadSeq) {
		this.cadSeq = cadSeq;
	}

	public String getPacOruAccNumber() {
		return pacOruAccNumber;
	}

	public void setPacOruAccNumber(String pacOruAccNumber) {
		this.pacOruAccNumber = pacOruAccNumber;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getSexoPaciente() {
		return sexoPaciente;
	}

	public void setSexoPaciente(String sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}

	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public VAelExamesAtdDiversosVO() {
	}

	public void setAtvPacCodigo(Integer atvPacCodigo) {
		this.atvPacCodigo = atvPacCodigo;
	}

	public Integer getAtvPacCodigo() {
		return atvPacCodigo;
	}
	
	public String getSitDescricao() {
		return sitDescricao;
	}

	public void setSitDescricao(String sitDescricao) {
		this.sitDescricao = sitDescricao;
	}

	public String getAlterado() {
		return alterado;
	}

	public void setAlterado(String alterado) {
		this.alterado = alterado;
	}
	
	public boolean isVerResultado() {
		return verResultado;
	}

	public void setVerResultado(boolean verResultado) {
		this.verResultado = verResultado;
	}

	public enum Fields {
		SOE_SEQ("soeSeq"), SEQP("seqp"), ISE_SOE_SEQ("iseSoeSeq"), ISE_SEQP("iseSeqp"), CRIADO_EM("criadoEm"), SIT_CODIGO("sitCodigo"), UFE_EMA_EXA_SIGLA(
				"ufeEmaExaSigla"), UFE_EMA_MAN_SEQ("ufeEmaManSeq"), UFE_UNF_SEQ("ufeSeq"), IND_LIBERA_RESULT_AUTOM("indLiberaResultAutom"), NOME_USUAL_EXAME(
				"descricaoUsualExame"), DESCRICAO_MATERIAL("descricaoMaterial"), PRONTUARIO("prontuario"), NOME_PACIENTE("nomePaciente"), SEXO_PACIENTE(
				"sexoPaciente"), ORIGEM("origem"), IDADE("idade"), PAC_CODIGO("pacCodigo"), SER_MATRICULA("serMatricula"), SER_VIN_CODIGO("serVinCodigo"), MOC_SEQ(
				"moqSeq"), IND_PERMITE_INCLUIR_RESULTADO("indPermiteIncluirResultado"), DTHR_EVENTO("dataHoraEvento"), TIPO_COLETA("tipoColeta"), NOME_EXAME_MATERIAL(
				"nomeUsualMaterial"),
		// LOCALIZACAO("soeSeq"),
		DTHR_PROGRAMADA("dthrProgramada"), ISE_DESC_MAT_ANALISE("iseDescMatAnalise"), EIN_TIPO("einTipo"), IND_CCI("indCci"), NUMERO_AP("numeroAp"), PJQ_SEQ(
				"pjqSeq"), LAE_SEQ("laeSeq"), CCQ_SEQ("ccqSeq"), DDV_SEQ("ddvSeq"), IND_IMPRESSO_LAUDO("indImpressoLaudo"), CAD_SEQ("cadSeq"), PAC_CODIGO2(
				"atvPacCodigo"), PAC_ORU_ACC_NUMBER("pacOruAccNumber"), ;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	@SuppressWarnings({"PMD.CyclomaticComplexity"})
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atvPacCodigo == null) ? 0 : atvPacCodigo.hashCode());
		result = prime * result + ((cadSeq == null) ? 0 : cadSeq.hashCode());
		result = prime * result + ((ccqSeq == null) ? 0 : ccqSeq.hashCode());
		result = prime * result + ((criadoEm == null) ? 0 : criadoEm.hashCode());
		result = prime * result + ((dataHoraEvento == null) ? 0 : dataHoraEvento.hashCode());
		result = prime * result + ((ddvSeq == null) ? 0 : ddvSeq.hashCode());
		result = prime * result + ((descricaoMaterial == null) ? 0 : descricaoMaterial.hashCode());
		result = prime * result + ((descricaoUsualExame == null) ? 0 : descricaoUsualExame.hashCode());
		result = prime * result + ((dthrProgramada == null) ? 0 : dthrProgramada.hashCode());
		result = prime * result + ((einTipo == null) ? 0 : einTipo.hashCode());
		result = prime * result + ((idade == null) ? 0 : idade.hashCode());
		result = prime * result + ((indCci == null) ? 0 : indCci.hashCode());
		result = prime * result + ((indImpressoLaudo == null) ? 0 : indImpressoLaudo.hashCode());
		result = prime * result + ((indLiberaResultAutom == null) ? 0 : indLiberaResultAutom.hashCode());
		result = prime * result + ((indPermiteIncluirResultado == null) ? 0 : indPermiteIncluirResultado.hashCode());
		result = prime * result + ((iseDescMatAnalise == null) ? 0 : iseDescMatAnalise.hashCode());
		result = prime * result + ((iseSeqp == null) ? 0 : iseSeqp.hashCode());
		result = prime * result + ((iseSoeSeq == null) ? 0 : iseSoeSeq.hashCode());
		result = prime * result + ((laeSeq == null) ? 0 : laeSeq.hashCode());
		result = prime * result + ((moqSeq == null) ? 0 : moqSeq.hashCode());
		result = prime * result + ((nomePaciente == null) ? 0 : nomePaciente.hashCode());
		result = prime * result + ((nomeUsualMaterial == null) ? 0 : nomeUsualMaterial.hashCode());
		result = prime * result + ((numeroAp == null) ? 0 : numeroAp.hashCode());
		result = prime * result + ((origem == null) ? 0 : origem.hashCode());
		result = prime * result + ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result + ((pacOruAccNumber == null) ? 0 : pacOruAccNumber.hashCode());
		result = prime * result + ((pjqSeq == null) ? 0 : pjqSeq.hashCode());
		result = prime * result + ((prontuario == null) ? 0 : prontuario.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
		result = prime * result + ((serMatricula == null) ? 0 : serMatricula.hashCode());
		result = prime * result + ((serVinCodigo == null) ? 0 : serVinCodigo.hashCode());
		result = prime * result + ((sexoPaciente == null) ? 0 : sexoPaciente.hashCode());
		result = prime * result + ((sitCodigo == null) ? 0 : sitCodigo.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
		result = prime * result + ((tipoColeta == null) ? 0 : tipoColeta.hashCode());
		result = prime * result + ((ufeEmaExaSigla == null) ? 0 : ufeEmaExaSigla.hashCode());
		result = prime * result + ((ufeEmaManSeq == null) ? 0 : ufeEmaManSeq.hashCode());
		result = prime * result + ((ufeSeq == null) ? 0 : ufeSeq.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings({"PMD.NcssMethodCount", "PMD.CyclomaticComplexity"})
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VAelExamesAtdDiversosVO other = (VAelExamesAtdDiversosVO) obj;
		if (atvPacCodigo == null) {
			if (other.atvPacCodigo != null) {
				return false;
			}
		} else if (!atvPacCodigo.equals(other.atvPacCodigo)) {
			return false;
		}
		if (cadSeq == null) {
			if (other.cadSeq != null) {
				return false;
			}
		} else if (!cadSeq.equals(other.cadSeq)) {
			return false;
		}
		if (ccqSeq == null) {
			if (other.ccqSeq != null) {
				return false;
			}
		} else if (!ccqSeq.equals(other.ccqSeq)) {
			return false;
		}
		if (criadoEm == null) {
			if (other.criadoEm != null) {
				return false;
			}
		} else if (!criadoEm.equals(other.criadoEm)) {
			return false;
		}
		if (dataHoraEvento == null) {
			if (other.dataHoraEvento != null) {
				return false;
			}
		} else if (!dataHoraEvento.equals(other.dataHoraEvento)) {
			return false;
		}
		if (ddvSeq == null) {
			if (other.ddvSeq != null) {
				return false;
			}
		} else if (!ddvSeq.equals(other.ddvSeq)) {
			return false;
		}
		if (descricaoMaterial == null) {
			if (other.descricaoMaterial != null) {
				return false;
			}
		} else if (!descricaoMaterial.equals(other.descricaoMaterial)) {
			return false;
		}
		if (descricaoUsualExame == null) {
			if (other.descricaoUsualExame != null) {
				return false;
			}
		} else if (!descricaoUsualExame.equals(other.descricaoUsualExame)) {
			return false;
		}
		if (dthrProgramada == null) {
			if (other.dthrProgramada != null) {
				return false;
			}
		} else if (!dthrProgramada.equals(other.dthrProgramada)) {
			return false;
		}
		if (einTipo == null) {
			if (other.einTipo != null) {
				return false;
			}
		} else if (!einTipo.equals(other.einTipo)) {
			return false;
		}
		if (idade == null) {
			if (other.idade != null) {
				return false;
			}
		} else if (!idade.equals(other.idade)) {
			return false;
		}
		if (indCci == null) {
			if (other.indCci != null) {
				return false;
			}
		} else if (!indCci.equals(other.indCci)) {
			return false;
		}
		if (indImpressoLaudo != other.indImpressoLaudo) {
			return false;
		}
		if (indLiberaResultAutom == null) {
			if (other.indLiberaResultAutom != null) {
				return false;
			}
		} else if (!indLiberaResultAutom.equals(other.indLiberaResultAutom)) {
			return false;
		}
		if (indPermiteIncluirResultado == null) {
			if (other.indPermiteIncluirResultado != null) {
				return false;
			}
		} else if (!indPermiteIncluirResultado.equals(other.indPermiteIncluirResultado)) {
			return false;
		}
		if (iseDescMatAnalise == null) {
			if (other.iseDescMatAnalise != null) {
				return false;
			}
		} else if (!iseDescMatAnalise.equals(other.iseDescMatAnalise)) {
			return false;
		}
		if (iseSeqp == null) {
			if (other.iseSeqp != null) {
				return false;
			}
		} else if (!iseSeqp.equals(other.iseSeqp)) {
			return false;
		}
		if (iseSoeSeq == null) {
			if (other.iseSoeSeq != null) {
				return false;
			}
		} else if (!iseSoeSeq.equals(other.iseSoeSeq)) {
			return false;
		}
		if (laeSeq == null) {
			if (other.laeSeq != null) {
				return false;
			}
		} else if (!laeSeq.equals(other.laeSeq)) {
			return false;
		}
		if (moqSeq == null) {
			if (other.moqSeq != null) {
				return false;
			}
		} else if (!moqSeq.equals(other.moqSeq)) {
			return false;
		}
		if (nomePaciente == null) {
			if (other.nomePaciente != null) {
				return false;
			}
		} else if (!nomePaciente.equals(other.nomePaciente)) {
			return false;
		}
		if (nomeUsualMaterial == null) {
			if (other.nomeUsualMaterial != null) {
				return false;
			}
		} else if (!nomeUsualMaterial.equals(other.nomeUsualMaterial)) {
			return false;
		}
		if (numeroAp == null) {
			if (other.numeroAp != null) {
				return false;
			}
		} else if (!numeroAp.equals(other.numeroAp)) {
			return false;
		}
		if (origem != other.origem) {
			return false;
		}
		if (pacCodigo == null) {
			if (other.pacCodigo != null) {
				return false;
			}
		} else if (!pacCodigo.equals(other.pacCodigo)) {
			return false;
		}
		if (pacOruAccNumber == null) {
			if (other.pacOruAccNumber != null) {
				return false;
			}
		} else if (!pacOruAccNumber.equals(other.pacOruAccNumber)) {
			return false;
		}
		if (pjqSeq == null) {
			if (other.pjqSeq != null) {
				return false;
			}
		} else if (!pjqSeq.equals(other.pjqSeq)) {
			return false;
		}
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		if (serMatricula == null) {
			if (other.serMatricula != null) {
				return false;
			}
		} else if (!serMatricula.equals(other.serMatricula)) {
			return false;
		}
		if (serVinCodigo == null) {
			if (other.serVinCodigo != null) {
				return false;
			}
		} else if (!serVinCodigo.equals(other.serVinCodigo)) {
			return false;
		}
		if (sexoPaciente == null) {
			if (other.sexoPaciente != null) {
				return false;
			}
		} else if (!sexoPaciente.equals(other.sexoPaciente)) {
			return false;
		}
		if (sitCodigo == null) {
			if (other.sitCodigo != null) {
				return false;
			}
		} else if (!sitCodigo.equals(other.sitCodigo)) {
			return false;
		}
		if (soeSeq == null) {
			if (other.soeSeq != null) {
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)) {
			return false;
		}
		if (tipoColeta != other.tipoColeta) {
			return false;
		}
		if (ufeEmaExaSigla == null) {
			if (other.ufeEmaExaSigla != null) {
				return false;
			}
		} else if (!ufeEmaExaSigla.equals(other.ufeEmaExaSigla)) {
			return false;
		}
		if (ufeEmaManSeq == null) {
			if (other.ufeEmaManSeq != null) {
				return false;
			}
		} else if (!ufeEmaManSeq.equals(other.ufeEmaManSeq)) {
			return false;
		}
		if (ufeSeq == null) {
			if (other.ufeSeq != null) {
				return false;
			}
		} else if (!ufeSeq.equals(other.ufeSeq)) {
			return false;
		}
		return true;
	}

}
