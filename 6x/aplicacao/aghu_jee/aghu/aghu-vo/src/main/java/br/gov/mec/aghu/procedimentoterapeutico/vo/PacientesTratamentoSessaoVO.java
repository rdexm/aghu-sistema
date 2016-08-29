package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PacientesTratamentoSessaoVO implements BaseBean {

	private static final long serialVersionUID = 5042224637248980795L;
	
	private Short ciclo;
	private Integer matricula;
	private Short codigo;
	private Integer matriculaValida;
	private Short codigoValida;
	private String responsavel1;
	private String responsavel2;
	private Integer cloSeq;
	private Integer lote;
	private String paciente;
	private Integer prontuario;
	private String especialidade;
	private String convenio;
	private Long apac;
	private Short quarto;
	private String unidade;
	private Date inicioCiclo;
	private Short horasCiclo;
	private String tituloProtocolo;		
	private Integer primeiraConsulta;
	
	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public Integer getMatriculaValida() {
		return matriculaValida;
	}

	public void setMatriculaValida(Integer matriculaValida) {
		this.matriculaValida = matriculaValida;
	}

	public Short getCodigoValida() {
		return codigoValida;
	}

	public void setCodigoValida(Short codigoValida) {
		this.codigoValida = codigoValida;
	}

	public String getResponsavel1() {
		return responsavel1;
	}

	public void setResponsavel1(String responsavel1) {
		this.responsavel1 = responsavel1;
	}

	public String getResponsavel2() {
		return responsavel2;
	}

	public void setResponsavel2(String responsavel2) {
		this.responsavel2 = responsavel2;
	}

	public Integer getCloSeq() {
		return cloSeq;
	}

	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}

	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public Long getApac() {
		return apac;
	}

	public void setApac(Long apac) {
		this.apac = apac;
	}

	public Short getQuarto() {
		return quarto;
	}

	public void setQuarto(Short quarto) {
		this.quarto = quarto;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Short getHorasCiclo() {
		return horasCiclo;
	}

	public void setHorasCiclo(Short horasCiclo) {
		this.horasCiclo = horasCiclo;
	}

	public String getTituloProtocolo() {
		return tituloProtocolo;
	}

	public void setTituloProtocolo(String tituloProtocolo) {
		this.tituloProtocolo = tituloProtocolo;
	}
	
	public Date getInicioCiclo() {
		return inicioCiclo;
	}

	public void setInicioCiclo(Date inicioCiclo) {
		this.inicioCiclo = inicioCiclo;
	}

	public Integer getPrimeiraConsulta() {
		return primeiraConsulta;
	}

	public void setPrimeiraConsulta(Integer primeiraConsulta) {
		this.primeiraConsulta = primeiraConsulta;
	}

	public enum Fields {

		CICLO("ciclo"),
		PRIMEIRA_CONSULTA("primeiraConsulta");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	
}
