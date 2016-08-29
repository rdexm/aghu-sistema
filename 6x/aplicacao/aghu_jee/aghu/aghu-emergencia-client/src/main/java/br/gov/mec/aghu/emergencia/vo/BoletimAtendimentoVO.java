package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BoletimAtendimentoVO implements Serializable {

	private static final long serialVersionUID = 5696509444357422254L;
	
	private Integer numeroConsulta;
	private String dataConsulta;
	private String horaConsulta;
	private String diaConsulta;
	private Short serVinCodigoConsultado;
	private Integer serMatriculaConsultado;
	private String descricaoCentroCusto;
	private String informacoesConsulta;
	private String nomeReduzidoEspecialidade;
	private String nomeEquipe;
	private String siglaSala;
	private String sala;
	private String descricaoEspecialidade;
	private Integer codigoPaciente;
	private Integer prontuario;
	private String nomePaciente;
	private Integer seqGrade;
	private String nomeUsuario;
	private String infoExcedeProgramacao;
	private String profissional;
	private String nomeEspecialidade;
	private String descricaoSala;
	private String idade;
	private String gravidade;
	private String protocolo;
	private String queixaPrincipal;
	private String fluxograma;
	private String descritor;
	private BigDecimal peso;
	private BigDecimal altura;
	private String sinaisVitais;
	private List<SubFormularioSinaisVitaisVO> sinaisAferidos;
	
	public BigDecimal getAltura() {
		return altura;
	}
	
	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public String getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(String dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getHoraConsulta() {
		return horaConsulta;
	}

	public void setHoraConsulta(String horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public String getDiaConsulta() {
		return diaConsulta;
	}

	public void setDiaConsulta(String diaConsulta) {
		this.diaConsulta = diaConsulta;
	}

	public Short getSerVinCodigoConsultado() {
		return serVinCodigoConsultado;
	}

	public void setSerVinCodigoConsultado(Short serVinCodigoConsultado) {
		this.serVinCodigoConsultado = serVinCodigoConsultado;
	}

	public Integer getSerMatriculaConsultado() {
		return serMatriculaConsultado;
	}

	public void setSerMatriculaConsultado(Integer serMatriculaConsultado) {
		this.serMatriculaConsultado = serMatriculaConsultado;
	}

	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}

	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}

	public String getInformacoesConsulta() {
		return informacoesConsulta;
	}

	public void setInformacoesConsulta(String informacoesConsulta) {
		this.informacoesConsulta = informacoesConsulta;
	}

	public String getNomeReduzidoEspecialidade() {
		return nomeReduzidoEspecialidade;
	}

	public void setNomeReduzidoEspecialidade(String nomeReduzidoEspecialidade) {
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getSiglaSala() {
		return siglaSala;
	}

	public void setSiglaSala(String siglaSala) {
		this.siglaSala = siglaSala;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getDescricaoEspecialidade() {
		return descricaoEspecialidade;
	}

	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getInfoExcedeProgramacao() {
		return infoExcedeProgramacao;
	}

	public void setInfoExcedeProgramacao(String infoExcedeProgramacao) {
		this.infoExcedeProgramacao = infoExcedeProgramacao;
	}

	public String getProfissional() {
		return profissional;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getDescricaoSala() {
		return descricaoSala;
	}

	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getGravidade() {
		return gravidade;
	}

	public void setGravidade(String gravidade) {
		this.gravidade = gravidade;
	}
	
	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public String getSinaisVitais() {
		return sinaisVitais;
	}

	public void setSinaisVitais(String sinaisVitais) {
		this.sinaisVitais = sinaisVitais;
	}

	public List<SubFormularioSinaisVitaisVO> getSinaisAferidos() {
		return sinaisAferidos;
	}

	public void setSinaisAferidos(List<SubFormularioSinaisVitaisVO> sinaisAferidos) {
		this.sinaisAferidos = sinaisAferidos;
	}

	public String getQueixaPrincipal() {
		return queixaPrincipal;
	}

	public void setQueixaPrincipal(String queixaPrincipal) {
		this.queixaPrincipal = queixaPrincipal;
	}

	public String getFluxograma() {
		return fluxograma;
	}

	public void setFluxograma(String fluxograma) {
		this.fluxograma = fluxograma;
	}

	public String getDescritor() {
		return descritor;
	}

	public void setDescritor(String descritor) {
		this.descritor = descritor;
	}
}
