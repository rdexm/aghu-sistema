package br.gov.mec.aghu.blococirurgico.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.model.MamLaudoAih;

public class RelatorioLaudoAIHVO {

	private MamLaudoAih mamLaudoAih;
	private String nomeHospital;
	private Integer cnes;
	private Date dtProvavelInternacao;
	private Date dtProvavelCirurgia;
	private String equipe;
	private String nomePaciente;
	private Integer prontuario;
	private Date dataNascimento;
	private String sexo;
	private String nomeMae;
	private String telefone;
	private String endereco;
	private String municipio;
	private BigDecimal codigoIbge;
	private String uf;
	private BigDecimal cep;
	private String sinaisSintomas;//
	private String condicoes;//
	private String resultadosProvas;//
	private String cidDescricao;//
	private String cidCodigo;//
	private String cidCodigoSec;//
	private String descricaoProcedimento;//
	private String prioridade;//
	private String nomeMedico;//
	private Long codTabela;//
	private Date hora;//
	private String cpfMedSolic;//
	private String conselho;//
	private String nroConselho;//
	private String nroConsulta;//
	private String corPaciente;
	private String etnia;
	private String nomeResponsavelPaciente;
	
	public RelatorioLaudoAIHVO(){
		
	}

	public String getNomeHospital() {
		return nomeHospital;
	}

	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}

	public Integer getCnes() {
		return cnes;
	}

	public void setCnes(Integer cnes) {
		this.cnes = cnes;
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

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getSinaisSintomas() {
		return sinaisSintomas;
	}

	public void setSinaisSintomas(String sinaisSintomas) {
		this.sinaisSintomas = sinaisSintomas;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public BigDecimal getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(BigDecimal codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public BigDecimal getCep() {
		return cep;
	}

	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}

	public String getCidDescricao() {
		return cidDescricao;
	}

	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}

	public String getCidCodigoSec() {
		return cidCodigoSec;
	}

	public void setCidCodigoSec(String cidCodigoSec) {
		this.cidCodigoSec = cidCodigoSec;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public String getCpfMedSolic() {
		return cpfMedSolic;
	}

	public void setCpfMedSolic(String cpfMedSolic) {
		this.cpfMedSolic = cpfMedSolic;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}
	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public Date getDtProvavelInternacao() {
		return dtProvavelInternacao;
	}

	public Date getDtProvavelCirurgia() {
		return dtProvavelCirurgia;
	}

	public String getCondicoes() {
		return condicoes;
	}

	public String getResultadosProvas() {
		return resultadosProvas;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public String getPrioridade() {
		return prioridade;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public String getNroConselho() {
		return nroConselho;
	}

	public void setDtProvavelInternacao(Date dtProvavelInternacao) {
		this.dtProvavelInternacao = dtProvavelInternacao;
	}

	public void setDtProvavelCirurgia(Date dtProvavelCirurgia) {
		this.dtProvavelCirurgia = dtProvavelCirurgia;
	}

	public void setCondicoes(String condicoes) {
		this.condicoes = condicoes;
	}

	public void setResultadosProvas(String resultadosProvas) {
		this.resultadosProvas = resultadosProvas;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public void setNroConselho(String nroConselho) {
		this.nroConselho = nroConselho;
	}

	public String getNroConsulta() {
		return nroConsulta;
	}

	public void setNroConsulta(String nroConsulta) {
		this.nroConsulta = nroConsulta;
	}
	
	public MamLaudoAih getMamLaudoAih() {
		return mamLaudoAih;
	}

	public void setMamLaudoAih(MamLaudoAih mamLaudoAih) {
		this.mamLaudoAih = mamLaudoAih;
	}

	public String getCorPaciente() {
		return corPaciente;
	}

	public void setCorPaciente(String corPaciente) {
		this.corPaciente = corPaciente;
	}

	public String getEtnia() {
		return etnia;
	}

	public void setEtnia(String etnia) {
		this.etnia = etnia;
	}

	public String getNomeResponsavelPaciente() {
		return nomeResponsavelPaciente;
	}

	public void setNomeResponsavelPaciente(String nomeResponsavelPaciente) {
		this.nomeResponsavelPaciente = nomeResponsavelPaciente;
	}
	
	
	

}
