package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAgenda;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HistoricoSessaoJnVO implements BaseBean{

	private static final long serialVersionUID = 7572685453312424378L;
	
	private Integer seqJn;
	private String descricao;  	//DESC_TIPO
	private Date dataAlteracao; //DATA_ALT
	private String user; 		//JN_USER
	private String descricaoJn;	//DESC_TIPO_JN
	private String descricaoUnidade; //DESC_UNID_FUNC
	private DominioTipoAgenda tipoAgenda; //TIPO_AGENDA
	private Date tempoFixo; 	//TEMPO
	private Integer tempoDisponivel; //DISP
	private Boolean indApac; 	//APAC
	private Boolean indConsentimento; //CONSENT
	private Boolean indFrequencia; //FREQ
	private DominioSituacao indSituacao; //SITUACAO
	
	public enum Fields {
		SEQ_JN("seqJn"),
		DESCRICAO("descricao"),
		DATA_ALTERACAO("dataAlteracao"),
		JN_USER("user"),
		DESCRICAO_JN("descricaoJn"),
		DESCRICAO_UNIDADE("descricaoUnidade"),
		TIPO_AGENDA("tipoAgenda"),
		TEMPO_FIXO("tempoFixo"),
		TEMPO_DISPONIVEL("tempoDisponivel"),
		IND_APAC("indApac"),
		IND_CONSENTIMENTO("indConsentimento"),
		IND_FREQUENCIA("indFrequencia"),
		IND_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDescricaoJn() {
		return descricaoJn;
	}
	public void setDescricaoJn(String descricaoJn) {
		this.descricaoJn = descricaoJn;
	}
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
	public DominioTipoAgenda getTipoAgenda() {
		return tipoAgenda;
	}
	public void setTipoAgenda(DominioTipoAgenda tipoAgenda) {
		this.tipoAgenda = tipoAgenda;
	}
	public Date getTempoFixo() {
		return tempoFixo;
	}
	public void setTempoFixo(Date tempoFixo) {
		this.tempoFixo = tempoFixo;
	}
	public Boolean getIndApac() {
		return indApac;
	}
	public void setIndApac(Boolean indApac) {
		this.indApac = indApac;
	}
	public Boolean getIndConsentimento() {
		return indConsentimento;
	}
	public void setIndConsentimento(Boolean indConsentimento) {
		this.indConsentimento = indConsentimento;
	}
	public Boolean getIndFrequencia() {
		return indFrequencia;
	}
	public void setIndFrequencia(Boolean indFrequencia) {
		this.indFrequencia = indFrequencia;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Integer getTempoDisponivel() {
		return tempoDisponivel;
	}
	public void setTempoDisponivel(Integer tempoDisponivel) {
		this.tempoDisponivel = tempoDisponivel;
	}
	public Integer getSeqJn() {
		return seqJn;
	}
	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}
}
