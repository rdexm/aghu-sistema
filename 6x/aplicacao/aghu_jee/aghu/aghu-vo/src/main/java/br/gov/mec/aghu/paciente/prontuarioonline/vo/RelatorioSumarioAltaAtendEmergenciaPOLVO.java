package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioSumarioAltaAtendEmergenciaPOLVO implements Serializable  {
	
	private static final long serialVersionUID = -1641914531145284600L;

//	Principal
	private String assinatura;
	private Integer asuApaAtdSeq;
	private Integer asuApaSeq;
	private Short asuSeqp;
	private List<LinhaReportVO> descAtendimento;
	private String descPlanoConvenio;
	private List<LinhaReportVO> descTriagem;
	private Date dtNascimento;
	private Date dthrAlta;
	private Date dthrTriagem;
	private String endereco;
	private String idade;
	private Boolean indNoConsultorio;
	private String indTipo;
	private String matriculaConvenio;
	private String nome;
	private String posto;
	private String previa;
	private String prontuario;
	private String queixaPrincipal;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private String sexo;
	private Date dataAtual;
	private String tituloRelatorio;
	private List<RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO> evolucao;

	//	Getters and Setters
	public Integer getAsuApaAtdSeq() {
		return asuApaAtdSeq;
	}
	public void setAsuApaAtdSeq(Integer asuApaAtdSeq) {
		this.asuApaAtdSeq = asuApaAtdSeq;
	}
	public Integer getAsuApaSeq() {
		return asuApaSeq;
	}
	public void setAsuApaSeq(Integer asuApaSeq) {
		this.asuApaSeq = asuApaSeq;
	}
	public Short getAsuSeqp() {
		return asuSeqp;
	}
	public void setAsuSeqp(Short asuSeqp) {
		this.asuSeqp = asuSeqp;
	}
	public Boolean getIndNoConsultorio() {
		return indNoConsultorio;
	}
	public void setIndNoConsultorio(Boolean indNoConsultorio) {
		this.indNoConsultorio = indNoConsultorio;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public Date getDthrTriagem() {
		return dthrTriagem;
	}
	public void setDthrTriagem(Date dthrTriagem) {
		this.dthrTriagem = dthrTriagem;
	}
	public Date getDthrAlta() {
		return dthrAlta;
	}
	public void setDthrAlta(Date dthrAlta) {
		this.dthrAlta = dthrAlta;
	}
	public String getIndTipo() {
		return indTipo;
	}
	public void setIndTipo(String indTipo) {
		this.indTipo = indTipo;
	}
	public String getPrevia() {
		return previa;
	}
	public void setPrevia(String previa) {
		this.previa = previa;
	}
	public String getPosto() {
		return posto;
	}
	public void setPosto(String posto) {
		this.posto = posto;
	}
	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}
	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}
	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}
	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}
	public String getAssinatura() {
		return assinatura;
	}
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	public Date getDataAtual() {
		return dataAtual;
	}
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	public String getDescPlanoConvenio() {
		return descPlanoConvenio;
	}
	public void setDescPlanoConvenio(String descPlanoConvenio) {
		this.descPlanoConvenio = descPlanoConvenio;
	}
	public String getMatriculaConvenio() {
		return matriculaConvenio;
	}
	public void setMatriculaConvenio(String matriculaConvenio) {
		this.matriculaConvenio = matriculaConvenio;
	}
	public String getTituloRelatorio() {
		return tituloRelatorio;
	}
	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}
	public String getQueixaPrincipal() {
		return queixaPrincipal;
	}
	public void setQueixaPrincipal(String queixaPrincipal) {
		this.queixaPrincipal = queixaPrincipal;
	}
	public List<LinhaReportVO> getDescTriagem() {
		return descTriagem;
	}
	public void setDescTriagem(List<LinhaReportVO> descTriagem) {
		this.descTriagem = descTriagem;
	}
	public List<LinhaReportVO> getDescAtendimento() {
		return descAtendimento;
	}
	public void setDescAtendimento(List<LinhaReportVO> descAtendimento) {
		this.descAtendimento = descAtendimento;
	}
	public void setEvolucao(List<RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO> evolucao) {
		this.evolucao = evolucao;
	}
	public List<RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO> getEvolucao() {
		return evolucao;
	}
}