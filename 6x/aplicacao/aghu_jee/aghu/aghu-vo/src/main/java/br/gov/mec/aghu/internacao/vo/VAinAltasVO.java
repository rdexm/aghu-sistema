package br.gov.mec.aghu.internacao.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.commons.BaseBean;

@SuppressWarnings("ucd")
public class VAinAltasVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1985035447094386093L;
	private BigDecimal matricula; 
	private String senha;
	private String local;
	private String equipe;
	private Integer prontuario;
	private String nomePac;
	private Integer matriculaServidorProfessor;
	private Short vinCodigoServidorProfessor;
	private Integer  codClinica;
	private Short seqEspecialidade;
	private String siglaEspecialidade;
	private Date dthrInicioInternacao;
	private Short qrtNumero;
	private String ltoLtoId; 
	private String andar;
	private AghAla indAla;
	private Date dthrAltaMedica;
	private Date dtSaidaPaciente;
	private DominioIndTipoAltaSumarios indTipo;
	private DominioSimNao altaObito;
	private Boolean indPacienteInternado;
	private Short seqUnidadeFuncional;
	private String descricaoTipoAltaMedica;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private Integer codigoPacienteAlta;
	private String observacaoPacienteAlta;
	private Integer seqInternacao;
	
	public VAinAltasVO() {
	}

	public VAinAltasVO(Integer prontuario,
			String nomePac,
			Integer matriculaServidorProfessor,
			Short vinCodigoServidorProfessor,
			Integer  codClinica,
			Short seqEspecialidade,
			String siglaEspecialidade,
			Date dthrInicioInternacao,
			Short qrtNumero,
			String ltoLtoId, 
			String andar,
			AghAla a,
			Date dthrAltaMedica, 
			Date dtSaidaPaciente,
			DominioIndTipoAltaSumarios indTipo,
			Boolean indPacienteInternado,
			Short seqUnidadeFuncional,
			String descricaoTipoAltaMedica,
			Short cspCnvCodigo,
			Byte cspSeq,
			Integer codigoPacienteAlta,
			String observacaoPacienteAlta,
			Integer seqInternacao){
		
		this.prontuario = prontuario;
		this.nomePac = nomePac;
		this.matriculaServidorProfessor = matriculaServidorProfessor;
		this.vinCodigoServidorProfessor = vinCodigoServidorProfessor;
		this.codClinica = codClinica;
		this.seqEspecialidade = seqEspecialidade;
		this.siglaEspecialidade = siglaEspecialidade;
		this.dthrInicioInternacao = dthrInicioInternacao;
		this.qrtNumero = qrtNumero;
		this.ltoLtoId = ltoLtoId; 
		this.andar = andar;
		this.indAla = a;
		this.dthrAltaMedica = dthrAltaMedica;
		this.dtSaidaPaciente = dtSaidaPaciente; 
		this.indTipo = indTipo;
		this.indPacienteInternado = indPacienteInternado; 
		this.seqUnidadeFuncional = seqUnidadeFuncional;
		this.descricaoTipoAltaMedica = descricaoTipoAltaMedica;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.codigoPacienteAlta = codigoPacienteAlta;
		this.observacaoPacienteAlta = observacaoPacienteAlta;
		this.seqInternacao = seqInternacao;
		
		if(DominioIndTipoAltaSumarios.OBT.equals(this.indTipo)){
			this.setAltaObito(DominioSimNao.S);
		}else{
			this.setAltaObito(DominioSimNao.N);
		}
	}


	//GETTERs e SETTERs
	public BigDecimal getMatricula() {
		return this.matricula;
	}


	public void setMatricula(BigDecimal matricula) {
		this.matricula = matricula;
	}


	public String getSenha() {
		return this.senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}


	public String getLocal() {
		return this.local;
	}


	public void setLocal(String local) {
		this.local = local;
	}


	public String getEquipe() {
		return this.equipe;
	}


	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}


	public Integer getProntuario() {
		return this.prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public String getNomePac() {
		return this.nomePac;
	}


	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}


	public Integer getMatriculaServidorProfessor() {
		return this.matriculaServidorProfessor;
	}


	public void setMatriculaServidorProfessor(Integer matriculaServidorProfessor) {
		this.matriculaServidorProfessor = matriculaServidorProfessor;
	}


	public Short getVinCodigoServidorProfessor() {
		return this.vinCodigoServidorProfessor;
	}


	public void setVinCodigoServidorProfessor(Short vinCodigoServidorProfessor) {
		this.vinCodigoServidorProfessor = vinCodigoServidorProfessor;
	}


	public Integer getCodClinica() {
		return this.codClinica;
	}


	public void setCodClinica(Integer codClinica) {
		this.codClinica = codClinica;
	}


	public Short getSeqEspecialidade() {
		return this.seqEspecialidade;
	}


	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}


	public String getSiglaEspecialidade() {
		return this.siglaEspecialidade;
	}


	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}


	public Date getDthrInicioInternacao() {
		return this.dthrInicioInternacao;
	}


	public void setDthrInicioInternacao(Date dthrInicioInternacao) {
		this.dthrInicioInternacao = dthrInicioInternacao;
	}


	public Short getQrtNumero() {
		return this.qrtNumero;
	}


	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}


	public String getLtoLtoId() {
		return this.ltoLtoId;
	}


	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}


	public String getAndar() {
		return this.andar;
	}


	public void setAndar(String andar) {
		this.andar = andar;
	}


	public AghAla getIndAla() {
		return this.indAla;
	}


	public void setIndAla(AghAla a) {
		this.indAla = a;
	}


	public Date getDthrAltaMedica() {
		return this.dthrAltaMedica;
	}


	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}


	public Date getDtSaidaPaciente() {
		return this.dtSaidaPaciente;
	}


	public void setDtSaidaPaciente(Date dtSaidaPaciente) {
		this.dtSaidaPaciente = dtSaidaPaciente;
	}


	public DominioIndTipoAltaSumarios getIndTipo() {
		return this.indTipo;
	}


	public void setIndTipo(DominioIndTipoAltaSumarios indTipo) {
		this.indTipo = indTipo;
	}


	public Boolean getIndPacienteInternado() {
		return this.indPacienteInternado;
	}


	public DominioSimNao getAltaObito() {
		return this.altaObito;
	}


	public void setAltaObito(DominioSimNao altaObito) {
		this.altaObito = altaObito;
	}


	public void setIndPacienteInternado(Boolean indPacienteInternado) {
		this.indPacienteInternado = indPacienteInternado;
	}


	public Short getSeqUnidadeFuncional() {
		return this.seqUnidadeFuncional;
	}


	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}


	public String getDescricaoTipoAltaMedica() {
		return this.descricaoTipoAltaMedica;
	}

	public void setDescricaoTipoAltaMedica(String descricaoTipoAltaMedica) {
		this.descricaoTipoAltaMedica = descricaoTipoAltaMedica;
	}

	public Short getCspCnvCodigo() {
		return this.cspCnvCodigo;
	}


	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}


	public Byte getCspSeq() {
		return this.cspSeq;
	}


	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}


	public void setCodigoPacienteAlta(Integer codigoPacienteAlta) {
		this.codigoPacienteAlta = codigoPacienteAlta;
	}

	public Integer getCodigoPacienteAlta() {
		return this.codigoPacienteAlta;
	}

	public String getObservacaoPacienteAlta() {
		return this.observacaoPacienteAlta;
	}


	public void setObservacaoPacienteAlta(String observacaoPacienteAlta) {
		this.observacaoPacienteAlta = observacaoPacienteAlta;
	}


	public Integer getSeqInternacao() {
		return this.seqInternacao;
	}


	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}
	
}
