package br.gov.mec.aghu.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoAtendimento;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "V_AEL_ARCO_SOLICITACAO_AGHU", schema = "AGH")
@Immutable
public class VAelArcoSolicitacaoAghu implements BaseEntity {

	private static final long serialVersionUID = -2120197098599585093L;

	@Id
	@Column(name = "SEQ")
	private Integer seq;

	@Column(name = "ATD_SEQ")
	private Integer atdSeq;

	@Column(name = "CSP_CNV_CODIGO")
	private Short cspCnvCodigo;

	@Column(name = "CSP_SEQ")
	private Byte cspSeq;

	@Column(name = "CNV_DESCRICAO")
	private String cnvDescricao;

	@Column(name = "INFORMACOES_CLINICAS")
	private String informacoesClinicas;

	@Column(name = "RECEM_NASCIDO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	private Boolean recemNascido;

	@Column(name = "UNF_SEQ")
	private Short unfSeq;

	@Column(name = "ATD_QRT_NUMERO")
	private Short atdQrtNumero;

	@Column(name = "ATD_UNF_SEQ")
	private Short atdUnfSeq;

	@Column(name = "ATD_UNF_DESCRICAO")
	private String atdUnfDescricao;

	@Column(name = "CRIADO_EM")
	private Date criadoEm;

	@Column(name = "TIPO", length = 3)
    @Enumerated(EnumType.STRING)
	private DominioTipoAtendimento tipoAtendimento;

	@Column(name = "ATD_CON_NUMERO")
	private Integer atdConNumero;

	@Column(name = "ATD_LTO_LTO_ID")
	private String atdLtoLtoId;

	@Column(name = "SER_MATRICULA")
	private Integer serMatricula;

	@Column(name = "SER_VIN_CODIGO")
	private Short serVinCodigo;

	@Column(name = "SER_MATRICULA_RESPONS")
	private Integer serMatriculaRespons;

	@Column(name = "SER_VIN_CODIGO_RESPONS")
	private Short serVinCodigoRespons;

	@Column(name = "UNF_SEQ_AREA_EXEC")
	private Short unfSeqAreaExec;

	@Column(name = "USA_ANTIMICROBIANOS", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	private Boolean usaAntimicrobianos;

	@Column(name = "IND_TRANSPLANTE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	private Boolean indTransplante;

	@Column(name = "ATD_IND_PAC_ATENDIMENTO", length = 1)
	@Enumerated(EnumType.STRING)
	private DominioPacAtendimento atdIndPacAtendimento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ATD_DTHR_INICIO")
	public Date atdDthrInicio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ATD_DTHR_FIM")
	public Date atdDthrFim;

	@Column(name = "ATD_INT_SEQ")
	private Integer atdIntSeq;

	@Column(name = "CAD_SEQ")
	private Integer cadSeq;

	@Column(name = "DCA_BOL_NUMERO")
	private Integer dcaBolNumero;

	@Column(name = "DCA_BOL_BSA_CODIGO")
	private Short dcaBolBsaCodigo;

	@Temporal(TemporalType.DATE)
	@Column(name = "DCA_BOL_DATA")
	private Date dcaBolData;

	@Column(name = "AMD_SEQP")
	private Short amdSeqp;

    ///////colunas incluidas
    @Column(name = "ATD_PRONTUARIO")
    private Integer atendimentoProntuario;

    @Column(name = "ATD_PAC_CODIGO")
    private Integer atendimentoPacienteCodigo;

    @Column(name = "ATD_PAC_NOME")
    private Integer atendimentoPacienteNome;

    @Column(name = "ATD_ORIGEM")
    private Integer atendimentoOrigem;

    @Column(name = "ATD_LOCAL_PAC")
    private String atendimentoLocalPaciente;

	@Transient
	private String nomePaciente;
	
	@Transient
	private String prontuario;

	@Transient
	private String leito;

	public enum Fields {

		SEQ("seq"), ;

		private final String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(final Integer seq) {
		this.seq = seq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(final Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(final Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(final Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public String getCnvDescricao() {
		return cnvDescricao;
	}

	public void setCnvDescricao(final String cnvDescricao) {
		this.cnvDescricao = cnvDescricao;
	}

	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(final String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	public Boolean getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(final Boolean recemNascido) {
		this.recemNascido = recemNascido;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(final Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getAtdQrtNumero() {
		return atdQrtNumero;
	}

	public void setAtdQrtNumero(final Short atdQrtNumero) {
		this.atdQrtNumero = atdQrtNumero;
	}

	public Short getAtdUnfSeq() {
		return atdUnfSeq;
	}

	public void setAtdUnfSeq(final Short atdUnfSeq) {
		this.atdUnfSeq = atdUnfSeq;
	}

	public String getAtdUnfDescricao() {
		return atdUnfDescricao;
	}

	public void setAtdUnfDescricao(final String atdUnfDescricao) {
		this.atdUnfDescricao = atdUnfDescricao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public DominioTipoAtendimento getTipoAtendimento() {
		return tipoAtendimento;
	}

	public void setTipoAtendimento(final DominioTipoAtendimento tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}

	public Integer getAtdConNumero() {
		return atdConNumero;
	}

	public void setAtdConNumero(final Integer atdConNumero) {
		this.atdConNumero = atdConNumero;
	}

	public String getAtdLtoLtoId() {
		return atdLtoLtoId;
	}

	public void setAtdLtoLtoId(final String atdLtoLtoId) {
		this.atdLtoLtoId = atdLtoLtoId;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(final Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(final Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatriculaRespons() {
		return serMatriculaRespons;
	}

	public void setSerMatriculaRespons(final Integer serMatriculaRespons) {
		this.serMatriculaRespons = serMatriculaRespons;
	}

	public Short getSerVinCodigoRespons() {
		return serVinCodigoRespons;
	}

	public void setSerVinCodigoRespons(final Short serVinCodigoRespons) {
		this.serVinCodigoRespons = serVinCodigoRespons;
	}

	public Short getUnfSeqAreaExec() {
		return unfSeqAreaExec;
	}

	public void setUnfSeqAreaExec(final Short unfSeqAreaExec) {
		this.unfSeqAreaExec = unfSeqAreaExec;
	}

	public Boolean getUsaAntimicrobianos() {
		return usaAntimicrobianos;
	}

	public void setUsaAntimicrobianos(final Boolean usaAntimicrobianos) {
		this.usaAntimicrobianos = usaAntimicrobianos;
	}

	public Boolean getIndTransplante() {
		return indTransplante;
	}

	public void setIndTransplante(final Boolean indTransplante) {
		this.indTransplante = indTransplante;
	}

	public DominioPacAtendimento getAtdIndPacAtendimento() {
		return atdIndPacAtendimento;
	}

	public void setAtdIndPacAtendimento(final DominioPacAtendimento atdIndPacAtendimento) {
		this.atdIndPacAtendimento = atdIndPacAtendimento;
	}

	public Date getAtdDthrInicio() {
		return atdDthrInicio;
	}

	public void setAtdDthrInicio(final Date atdDthrInicio) {
		this.atdDthrInicio = atdDthrInicio;
	}

	public Date getAtdDthrFim() {
		return atdDthrFim;
	}

	public void setAtdDthrFim(final Date atdDthrFim) {
		this.atdDthrFim = atdDthrFim;
	}

	public Integer getAtdIntSeq() {
		return atdIntSeq;
	}

	public void setAtdIntSeq(final Integer atdIntSeq) {
		this.atdIntSeq = atdIntSeq;
	}

	public Integer getCadSeq() {
		return cadSeq;
	}

	public void setCadSeq(final Integer cadSeq) {
		this.cadSeq = cadSeq;
	}

	public Integer getDcaBolNumero() {
		return dcaBolNumero;
	}

	public void setDcaBolNumero(final Integer dcaBolNumero) {
		this.dcaBolNumero = dcaBolNumero;
	}

	public Short getDcaBolBsaCodigo() {
		return dcaBolBsaCodigo;
	}

	public void setDcaBolBsaCodigo(final Short dcaBolBsaCodigo) {
		this.dcaBolBsaCodigo = dcaBolBsaCodigo;
	}

	public Date getDcaBolData() {
		return dcaBolData;
	}

	public void setDcaBolData(final Date dcaBolData) {
		this.dcaBolData = dcaBolData;
	}

	public Short getAmdSeqp() {
		return amdSeqp;
	}

	public void setAmdSeqp(final Short amdSeqp) {
		this.amdSeqp = amdSeqp;
	}

	public void setNomePaciente(final String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setProntuario(final String prontuario) {
		this.prontuario = prontuario;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setLeito(final String leito) {
		this.leito = leito;
	}

	public String getLeito() {
		return leito;
	}

    public Integer getAtendimentoProntuario() {
        return atendimentoProntuario;
    }

    public void setAtendimentoProntuario(Integer atendimentoProntuario) {
        this.atendimentoProntuario = atendimentoProntuario;
    }

    public Integer getAtendimentoPacienteCodigo() {
        return atendimentoPacienteCodigo;
    }

    public void setAtendimentoPacienteCodigo(Integer atendimentoPacienteCodigo) {
        this.atendimentoPacienteCodigo = atendimentoPacienteCodigo;
    }

    public Integer getAtendimentoPacienteNome() {
        return atendimentoPacienteNome;
    }

    public void setAtendimentoPacienteNome(Integer atendimentoPacienteNome) {
        this.atendimentoPacienteNome = atendimentoPacienteNome;
    }

    public Integer getAtendimentoOrigem() {
        return atendimentoOrigem;
    }

    public void setAtendimentoOrigem(Integer atendimentoOrigem) {
        this.atendimentoOrigem = atendimentoOrigem;
    }

    public String getAtendimentoLocalPaciente() {
        return atendimentoLocalPaciente;
    }

    public void setAtendimentoLocalPaciente(String atendimentoLocalPaciente) {
        this.atendimentoLocalPaciente = atendimentoLocalPaciente;
    }


}
