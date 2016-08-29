package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;


public class RelatorioAgendamentoConsultaVO  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4995997805581894675L;
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
	private String descricaoSala;
	private String sala;
	private String descricaoEspecialidade;
	private Integer codigoPaciente;
	private Integer prontuario;
	private Integer prontFamilia;
	private String nomePaciente;
	private String nomeSocial;
	private Integer seqGrade;
	private String nomeUsuario;
	private String infoExcedeProgramacao;
	private String profissional;
	private String andar;
	private String alaBloco;
	private String setor;
	
	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
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
	
	public String getNomeReduzidoEspecialidade() {
		return nomeReduzidoEspecialidade;
	}
	
	public void setNomeReduzidoEspecialidade(String nomeReduzidoEspecialidade) {
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
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

	public void setDiaConsulta(String diaConsulta) {
		this.diaConsulta = diaConsulta;
	}

	public String getDiaConsulta() {
		return diaConsulta;
	}

	public void setHoraConsulta(String horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public String getHoraConsulta() {
		return horaConsulta;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setInformacoesConsulta(String informacoesConsulta) {
		this.informacoesConsulta = informacoesConsulta;
	}

	public String getInformacoesConsulta() {
		return informacoesConsulta;
	}

	public void setInfoExcedeProgramacao(String infoExcedeProgramacao) {
		this.infoExcedeProgramacao = infoExcedeProgramacao;
	}

	public String getInfoExcedeProgramacao() {
		return infoExcedeProgramacao;
	}

	public void setSiglaSala(String siglaSala) {
		this.siglaSala = siglaSala;
	}

	public String getSiglaSala() {
		return siglaSala;
	}

	public String getDescricaoSala() {
		return descricaoSala;
	}

	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}

	public String getProfissional() {
		return profissional;
	}

	public Integer getProntFamilia() {
		return prontFamilia;
	}

	public void setProntFamilia(Integer prontFamilia) {
		this.prontFamilia = prontFamilia;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
	
	public String getAlaBloco() {
		return alaBloco;
	}

	public void setAlaBloco(String AlaBloco) {
		this.alaBloco = AlaBloco;
	}

}