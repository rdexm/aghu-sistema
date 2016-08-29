package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class AtestadoVO implements Serializable, Cloneable {

	private static final long serialVersionUID = 1973453830524934530L;
	
	private Long seq;
	private Date dthrCons;
	private String nomeAcompanhante;
	private Date dataInicial;
	private Date dataFinal;
	private String observacao;
	private String acometidoDe;
	private String layout;
	private String titulo;
	private String codigoCid;
	private String descricaoCid;
	private Integer prontuario;
	private String nomePaciente;
	private String indMotivoUsoFGTS;
	private String estagioClinicoGeral;
	private String mensagem;
	private String tipoAtestado;
	private Byte numeroVias;
	private Byte periodo;
	private String especialidade;
	private String tituloFormatado;
	private String cidFormatado;
	private String declaracaoFormatada;
	private String complementoEnderecoFormatado;
	private String nomeMedico;
	private String siglaConselho;
	private String numeroRegistroConselho;
	private String siglaNumeroConselhoFormatado;
	
	private String enderecoHospital;
	private String cepHospital;
	private String cidadeHospital;
	private String ufHospital;
	private String telefoneHospital;
	
	// Descrição do tipo do atestado
	private String descricaoAtestado;
	
	private byte ordem;
	
	private Boolean duplo;
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	public enum Fields {
		SEQ("seq"), DTHR_CONS("dthrCons"), NOME_ACOMPANHANTE("nomeAcompanhante"), DATA_INICIAL("dataInicial"),  DATA_FINAL("dataFinal"),
		OBSERVACAO("observacao"), ACOMETIDO_DE("acometidoDe"), LAYOUT("layout"), TITULO("titulo"),NRO_VIAS("numeroVias"), PERIODO("periodo"),
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
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
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
	public String getIndMotivoUsoFGTS() {
		return indMotivoUsoFGTS;
	}
	public void setIndMotivoUsoFGTS(String indMotivoUsoFGTS) {
		this.indMotivoUsoFGTS = indMotivoUsoFGTS;
	}
	public String getEstagioClinicoGeral() {
		return estagioClinicoGeral;
	}
	public void setEstagioClinicoGeral(String estagioClinicoGeral) {
		this.estagioClinicoGeral = estagioClinicoGeral;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public String getTipoAtestado() {
		return tipoAtestado;
	}
	public void setTipoAtestado(String tipoAtestado) {
		this.tipoAtestado = tipoAtestado;
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
	public String getSiglaConselho() {
		return siglaConselho;
	}
	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}
	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}
	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
	}
	public String getSiglaNumeroConselhoFormatado() {
		return siglaNumeroConselhoFormatado;
	}
	public void setSiglaNumeroConselhoFormatado(String siglaNumeroConselhoFormatado) {
		this.siglaNumeroConselhoFormatado = siglaNumeroConselhoFormatado;
	}
	public String getDescricaoAtestado() {
		return descricaoAtestado;
	}
	public void setDescricaoAtestado(String descricaoAtestado) {
		this.descricaoAtestado = descricaoAtestado;
	}
	public Byte getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Byte periodo) {
		this.periodo = periodo;
	}
	public byte getOrdem() {
		return ordem;
	}
	public void setOrdem(byte ordem) {
		this.ordem = ordem;
	}
	public Boolean getDuplo() {
		return duplo;
	}
	public void setDuplo(Boolean duplo) {
		this.duplo = duplo;
	}
		
}
