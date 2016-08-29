package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioTransplanteTmoSituacaoVO implements Serializable {
	
	private static final long serialVersionUID = 1227235810396639189L;
	
	private String pacNome;
	private Integer seqTransplantes;
	private Integer pacProntuario;
	private Integer pacCodigo;
	private DominioSexo pacSexo;
	private DominioSituacaoTransplante situacaoTransplante;
	private DominioSituacaoTmo situacaoTmo;
	private DominioTipoAlogenico tipoAlogenico; 
	private Date dataIngresso;
	private Date dataOcorrencia;
	private Date dataNascimento;
	private String observacoes;
	private Integer criterioPriorizacaoSeq;
	private Integer gravidade;
	private Integer criticidade;
	private String status;
	private Short ordemSituacao;
	private String idade;
	private Integer permanencia;
	private Integer escore;
	
	public enum Fields {
		
		PAC_NOME("pacNome"),
		SEQ_TRANSPLANTES("seqTransplantes"),
		PAC_PRONTUARIO("pacProntuario"),
		PAC_CODIGO("pacCodigo"),
		PAC_SEXO("pacSexo"),
		SITUACAO_TRANSPLANTE("situacaoTransplante"),
		SITUACAO_TMO("situacaoTmo"),
		TIPO_ALOGENICO("tipoAlogenico"),
		DATA_INGRESSO("dataIngresso"),
		DATA_OCORRENCIA("dataOcorrencia"),
		DATA_NASCIMENTO("dataNascimento"),
		OBSERVACOES("observacoes"),
		CRITERIO_PRIORIZACAO_SEQ("criterioPriorizacaoSeq"),
		GRAVIDADE("gravidade"),
		CRITICIDADE("criticidade"),
		STATUS("status");
		
		private String field;
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	private void calcularIdade(){
		Integer idadeDias, idadeMeses, idadeAnos;
		idadeDias = DateUtil.getIdadeDias(dataNascimento);
		if(idadeDias > 29){
			idadeMeses = DateUtil.getIdadeMeses(dataNascimento);
			if(idadeMeses > 11){
				idadeAnos =  DateUtil.getIdade(dataNascimento);
				if(idadeAnos > 1){
					idade = idadeAnos.toString().concat(" anos");
				}else{
					idade = idadeAnos.toString().concat(" ano");
				}
			}else{
				if(idadeMeses > 1){
					idade = idadeMeses.toString().concat(" meses");
				}else{
					idade = idadeMeses.toString().concat(" mês");
				}
			}
		}else{
			if(idadeDias > 1){
				idade = idadeDias.toString().concat(" dias");
			}else{
				idade = idadeDias.toString().concat(" dia");
			}
		}
	}
	
	public String getDescricaoPacSexo(){
		if(pacSexo == null){
			return "";
		}
		return pacSexo.getDescricao();
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getSeqTransplantes() {
		return seqTransplantes;
	}

	public void setSeqTransplantes(Integer seqTransplantes) {
		this.seqTransplantes = seqTransplantes;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public String getPacProntuarioFormatado(){
		if(pacProntuario != null){
			String str = pacProntuario.toString().substring(0, pacProntuario.toString().length() - 1);
			return (str.concat("/").concat(pacProntuario.toString().substring(pacProntuario.toString().length() - 1)));
		}else{
			return "";
		}
	}
	
	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
	
	public DominioSexo getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(DominioSexo pacSexo) {
		this.pacSexo = pacSexo;
	}

	public Short getOrdemSituacao() {
		return ordemSituacao;
	}

	public void setOrdemSituacao(Short ordemSituacao) {
		this.ordemSituacao = ordemSituacao;
	}

	public DominioSituacaoTransplante getSituacaoTransplante() {
		return situacaoTransplante;
	}
	
	public String getNameSituacaoTransplante(){
		return situacaoTransplante != null ? situacaoTransplante.name() : null; 
	}
	
	public String getDescricaoSituacaoTransplante(){
		return situacaoTransplante != null ? situacaoTransplante.retornarDescricaoCompleta() : null; 
	}

	public void setSituacaoTransplante(DominioSituacaoTransplante situacao) {
		//O porque destes numeros pode ser encontrado no documento da estoria 48373
		if(DominioSituacaoTransplante.A.equals(situacao)){
			ordemSituacao = 1;
		}else if(DominioSituacaoTransplante.E.equals(situacao)){
			ordemSituacao = 2;
		}else if(DominioSituacaoTransplante.T.equals(situacao)){
			ordemSituacao = 3;
		}else if(DominioSituacaoTransplante.I.equals(situacao)){
			ordemSituacao = 4;
		}else if(DominioSituacaoTransplante.S.equals(situacao)){
			ordemSituacao = 5;
		}else{
			ordemSituacao = 9;
		}
		this.situacaoTransplante = situacao;
	}

	public DominioSituacaoTmo getSituacaoTmo() {
		return situacaoTmo;
	}
	
	public String getDescricaoSituacaoTmo(){
		return situacaoTmo != null ? situacaoTmo.getDescricao() : ""; 
	}

	public void setSituacaoTmo(DominioSituacaoTmo situacaoTmo) {
		this.situacaoTmo = situacaoTmo;
	}
	
	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}
	
	public String getDescricaoTipoAlogenico(){
		return tipoAlogenico != null ? tipoAlogenico.getDescricao() : ""; 
	}
	
	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}
	
	public DominioSituacaoTmo getTipoTransplante(){
		return situacaoTmo;
	}
	
	public String getDescricaoTipoTransplante(){
		if(DominioSituacaoTmo.G.equals(situacaoTmo)){
			return situacaoTmo.getDescricao().concat(" - ").concat(tipoAlogenico.getDescricao());
		}else{
			return situacaoTmo.getDescricao();
		}
	}

	public Date getDataIngresso() {
		return dataIngresso;
	}

	public void setDataIngresso(Date dataIngresso) {
		this.permanencia = (int) ((new Date().getTime() - dataIngresso.getTime()) / (1000*60*60*24));
		this.dataIngresso = dataIngresso;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
		calcularIdade();
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
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

	public Integer getCriterioPriorizacaoSeq() {
		return criterioPriorizacaoSeq;
	}

	public void setCriterioPriorizacaoSeq(Integer criterioPriorizacaoSeq) {
		this.criterioPriorizacaoSeq = criterioPriorizacaoSeq;
	}

	public Integer getGravidade() {
		return gravidade;
	}

	public void setGravidade(Integer gravidade) {
		this.gravidade = gravidade;
	}

	public Integer getCriticidade() {
		return criticidade;
	}

	public void setCriticidade(Integer criticidade) {
		this.criticidade = criticidade;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getEscore() {
		return escore;
	}

	public void setEscore(Integer escore) {
		this.escore = escore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqTransplantes == null) ? 0 : seqTransplantes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;}
		if (obj == null){
			return false;}
		if (getClass() != obj.getClass()){
			return false;}
		RelatorioTransplanteTmoSituacaoVO other = (RelatorioTransplanteTmoSituacaoVO) obj;
		if (seqTransplantes == null) {
			if (other.seqTransplantes != null){
				return false;}
		} else if (!seqTransplantes.equals(other.seqTransplantes)){
			return false;}
		return true;
	}
}
