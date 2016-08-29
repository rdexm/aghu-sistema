package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioConsultoriaAmbulatorialVO implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1908823086548695482L;
	private Long seq;
	private Date dthrCons;
	private String observacaoAdicional;
	private String nomeAcompanhante;
	private Date data;
	private String observacao;
	private String urgente;
	private String acometidoDe;
	private String layout;
	private String titulo;
	private String codigoCid;
	private String descricaoCid;
	private Integer prontuario;
	private String nomePaciente;	
	private String mensagem;
	private String tipoInterconsultas;
	private Byte numeroVias;
	private String nomeMedico;
	private String siglaConselhoMedico;
	private String numeroRegistroConselhoMedico;
	private String assinaturaMedico;
	private String especialidade;
	private String especialidadeAgenda;
	private String especialidadeMedico;
	private String equipe;
	private String nomeProfissional;
	private String tituloFormatado;
	private String cidFormatado;
	private String declaracaoFormatada;
	private String complementoEnderecoFormatado;
	private String enderecoHospital;
	private String cepHospital;
	private String cidadeHospital;
	private String ufHospital;
	private String telefoneHospital;
	
	
	public enum Fields {
		SEQ("seq"), DTHR_CONS("dthrCons"), NOME_ACOMPANHANTE("nomeAcompanhante"), DATA("data"),
		OBSERVACAO("observacao"), ACOMETIDO_DE("acometidoDe"), LAYOUT("layout"), TITULO("titulo"),
		CODIGO_CID("codigoCid"), DESCRICAO_CID("descricaoCid"), PRONTUARIO("prontuario"), NOME_PACIENTE("nomePaciente"),
		IND_MOTIVO_USO_FGTS("indMotivoUsoFGTS"), ESTAGIO_CLINICO_GERAL("estagioClinicoGeral");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
		
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Date getDthrCons() {
		return dthrCons;
	}
	public void setDthrCons(Date dthrCons) {
		this.dthrCons = dthrCons;
	}
	public String getNomeAcompanhante() {
		return nomeAcompanhante;
	}
	public void setNomeAcompanhante(String nomeAcompanhante) {
		this.nomeAcompanhante = nomeAcompanhante;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}	
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getUrgente() {
		return urgente;
	}
	public void setUrgente(String urgente) {
		this.urgente = urgente;
	}
	public String getAcometidoDe() {
		return acometidoDe;
	}
	public void setAcometidoDe(String acometidoDe) {
		this.acometidoDe = acometidoDe;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getCodigoCid() {
		return codigoCid;
	}
	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}
	public String getDescricaoCid() {
		return descricaoCid;
	}
	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
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
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public String getTipoInterconsultas() {
		return tipoInterconsultas;
	}
	public void setTipoInterconsultas(String tipoInterconsultas) {
		this.tipoInterconsultas = tipoInterconsultas;
	}
	public Byte getNumeroVias() {
		return numeroVias;
	}
	public void setNumeroVias(Byte numeroVias) {
		this.numeroVias = numeroVias;
	}
	public String getEnderecoHospital() {
		return enderecoHospital;
	}
	public void setEnderecoHospital(String enderecoHospital) {
		this.enderecoHospital = enderecoHospital;
	}
	public String getCepHospital() {
		return cepHospital;
	}
	public void setCepHospital(String cepHospital) {
		this.cepHospital = cepHospital;
	}
	public String getCidadeHospital() {
		return cidadeHospital;
	}
	public void setCidadeHospital(String cidadeHospital) {
		this.cidadeHospital = cidadeHospital;
	}
	public String getUfHospital() {
		return ufHospital;
	}
	public void setUfHospital(String ufHospital) {
		this.ufHospital = ufHospital;
	}
	public String getTelefoneHospital() {
		return telefoneHospital;
	}
	public void setTelefoneHospital(String telefoneHospital) {
		this.telefoneHospital = telefoneHospital;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getTituloFormatado() {		
		return tituloFormatado;
	}
	public void setTituloFormatado(String tituloFormatado) {
		this.tituloFormatado = tituloFormatado;
	}
	public String getCidFormatado() {				
		return cidFormatado;
	}
	public void setCidFormatado(String cidFormatado) {
		this.cidFormatado = cidFormatado;
	}
	public String getDeclaracaoFormatada() {		
		return declaracaoFormatada;
	}
	public void setDeclaracaoFormatada(String declaracaoFormatada) {
		this.declaracaoFormatada = declaracaoFormatada;
	}
	public String getComplementoEnderecoFormatado() {
		return complementoEnderecoFormatado;
	}
	public void setComplementoEnderecoFormatado(String complementoEnderecoFormatado) {
		this.complementoEnderecoFormatado = complementoEnderecoFormatado;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	public String getEspecialidadeAgenda() {
		return especialidadeAgenda;
	}
	public void setEspecialidadeAgenda(String especialidadeAgenda) {
		this.especialidadeAgenda = especialidadeAgenda;
	}
	public String getEspecialidadeMedico() {
		return especialidadeMedico;
	}
	public void setEspecialidadeMedico(String especialidadeMedico) {
		this.especialidadeMedico = especialidadeMedico;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getNomeProfissional() {
		return nomeProfissional;
	}
	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}
	public String getSiglaConselhoMedico() {
		return siglaConselhoMedico;
	}
	public String getNumeroRegistroConselhoMedico() {
		return numeroRegistroConselhoMedico;
	}
	public void setSiglaConselhoMedico(String siglaConselhoMedico) {
		this.siglaConselhoMedico = siglaConselhoMedico;
	}
	public void setNumeroRegistroConselhoMedico(String numeroRegistroConselhoMedico) {
		this.numeroRegistroConselhoMedico = numeroRegistroConselhoMedico;
	}
	public String getObservacaoAdicional() {
		return observacaoAdicional;
	}
	public void setObservacaoAdicional(String observacaoAdicional) {
		this.observacaoAdicional = observacaoAdicional;
	}
	public String getAssinaturaMedico() {
		return assinaturaMedico;
	}
	public void setAssinaturaMedico(String assinaturaMedico) {
		this.assinaturaMedico = assinaturaMedico;
	}
		
}
