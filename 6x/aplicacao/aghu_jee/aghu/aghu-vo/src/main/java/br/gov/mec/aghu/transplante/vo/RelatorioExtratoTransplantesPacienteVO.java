package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;



public class RelatorioExtratoTransplantesPacienteVO {
	
	
	
	
	/**
	 * Dados do paciente
	 */
	private Integer prontuario;
	private Integer pacCodigo;
	private String nome;
	private DominioSexo sexo;
	private Date dtNascimento;

	private Date dataIngresso;
	private DominioSituacaoTmo tipoTmo;
	private DominioTipoAlogenico tipoAlogenico;
	private DominioTipoOrgao tipoOrgao;
	
	private DominioSituacaoTransplante situacao;
	private Date dataOcorrencia;
	
	private Date dataObito;
		
	private String nomeResponsavel;
	private String descricaoMotivo;
	
	private String sexoFormatado;
	private String idadeFormatada;
	private String tipoTmoFormatado;
	private String tipoAlogenicoFormatado;
	private String tipoOrgaoFormatado;
	
	private String colunaTipo;
	
	private String prontuarioFormatado;
	private String situacaoFormatado;
	
	private Integer permanencia;
	
	public enum Fields{
		PRONTUARIO("prontuario"),
		NOME("nome"),
		PAC_CODIGO("pacCodigo"),
		SEXO("sexo"),
		DT_NASCIMENTO("dtNascimento"),
		DT_INGRESSO("dataIngresso"),
		DT_OBITO("dataObito"),
		TIPO_TMO("tipoTmo"),
		TIPO_ALOGENICO("tipoAlogenico"),
		TIPO_ORGAO("tipoOrgao"),
		SITUACAO("situacao"),
		NOME_RESPONSAVEL("nomeResponsavel"),
		DT_OCORRENCIA("dataOcorrencia"),
		DESCRICAO_MOTIVO("descricaoMotivo");
		
		private String fields;
		
		private Fields(String s){
			this.fields = s;
		}
		
		@Override
		public String toString(){
			return this.fields;
		}
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public DominioSexo getSexo() {
		return sexo;
	}
	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public Date getDataIngresso() {
		return dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}
	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}
	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}
	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}
	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}

	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
	public String getDescricaoMotivo() {
		return descricaoMotivo;
	}
	public void setDescricaoMotivo(String descricaoMotivo) {
		this.descricaoMotivo = descricaoMotivo;
	}
	
	
	public String getSexoFormatado() {
		return sexoFormatado;
	}
	public void setSexoFormatado(String sexoFormatado) {
		this.sexoFormatado = sexoFormatado;
	}
	public String getIdadeFormatada() {
		return idadeFormatada;
	}
	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}
	public String getTipoTmoFormatado() {
		return tipoTmoFormatado;
	}
	public void setTipoTmoFormatado(String tipoTmoFormatado) {
		this.tipoTmoFormatado = tipoTmoFormatado;
	}
	public String getTipoAlogenicoFormatado() {
		return tipoAlogenicoFormatado;
	}
	public void setTipoAlogenicoFormatado(String tipoAlogenicoFormatado) {
		this.tipoAlogenicoFormatado = tipoAlogenicoFormatado;
	}
	public String getTipoOrgaoFormatado() {
		return tipoOrgaoFormatado;
	}
	public void setTipoOrgaoFormatado(String tipoOrgaoFormatado) {
		this.tipoOrgaoFormatado = tipoOrgaoFormatado;
	}
	public String getSituacaoFormatado() {
		return situacaoFormatado;
	}
	public void setSituacaoFormatado(String situacaoFormatado) {
		this.situacaoFormatado = situacaoFormatado;
	}
	
	public String getColunaTipo() {
		return colunaTipo;
	}
	public void setColunaTipo(String colunaTipo) {
		this.colunaTipo = colunaTipo;
	}
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
	public Date getDataObito() {
		return dataObito;
	}
	public void setDataObito(Date dataObito) {
		this.dataObito = dataObito;
	}
	
	public Integer getPermanencia() {
		return permanencia;
	}
	public void setPermanencia(Integer permanencia) {
		this.permanencia = permanencia;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

}
