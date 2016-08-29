package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;

public class AgendaTransplanteRetornoVO implements Serializable {

	private static final long serialVersionUID = -3777316506209484192L;
	
	private Integer codPaciente;
	private Integer prontuario;
	private String nomePaciente;
	private Date dataConsulta;
	private Date dataIngresso;
	private Date dataUltimoTransplante;
	private Date dataOcorrenciaTransplante;
	private String descricaoRetorno;
	private Boolean indAusente;
	private DominioSituacaoTmo tipoTMO;
	private DominioTipoAlogenico tipoAlogenico;
	private DominioTipoOrgao tipoOrgao;
	private Short seqEspecialidade;
	private String nomeEspecialidade;
	private String observacaoTransplante;
	private Integer seqTransplante;
	private DominioSituacaoTransplante situacao;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private Short dddFoneRecado;
	private String foneRecado;
	private String contato;

	
	private String corLegenda;
	private Boolean previsao = false;

	public enum Fields {
		
		COD_PACIENTE("codPaciente"),
		PRONTUARIO("prontuario"),
		NOME_PACIENTE("nomePaciente"),
		DATA_CONSULTA("dataConsulta"),
		DATA_INGRESSO("dataIngresso"),
		DATA_ULTIMO_TRANSPLANTE("dataUltimoTransplante"),
		DESCRICAO_RETORNO("descricaoRetorno"),
		IND_AUSENTE("indAusente"),
		TIPO_TMO("tipoTMO"),
		TIPO_ALOGENICO("tipoAlogenico"),
		TIPO_ORGAO("tipoOrgao"),
		SEQ_ESPECIALIDADE("seqEspecialidade"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		OBSERVACAO_TRANSPLANTE("observacaoTransplante"),
		SEQ_TRANSPLANTE("seqTransplante"),
		DDD_FONE_RESIDENCIAL("dddFoneResidencial"),
		FONE_RESIDENCIAL("foneResidencial"),
		DDD_FONE_RECADO("dddFoneRecado"),
		FONE_RECADO("foneRecado"),
		SITUACAO("situacao"),
		DATA_OCORRENCIA_TRANSPLANTE("dataOcorrenciaTransplante"),
		CONTATO("contato");
	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
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
	public Date getDataConsulta() {
		return dataConsulta;
	}
	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}
	public Date getDataConsultaSchedule() {
		// data utilizada para apresentar no schedule, que deve ser de 30 em 30 minutos. 
		// Faz a verificação abaixo para ajusta-la conforme regra citada
		Calendar dataInstancia = Calendar.getInstance();
		dataInstancia.setTime(this.dataConsulta);
		
		int minuto = dataInstancia.get(Calendar.MINUTE);
		if (minuto > 0  && minuto < 30){
			dataInstancia.set(Calendar.MINUTE, 0);
		} else if (minuto > 30 && minuto <=59){
			dataInstancia.set(Calendar.MINUTE, 30);
		}
		
		return dataInstancia.getTime();
	}
	public String getHoraFormatadaSchedule(){
		return new SimpleDateFormat("HH:mm").format(this.dataConsulta);
	}
	public Date getDataIngresso() {
		return dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public Date getDataUltimoTransplante() {
		return dataUltimoTransplante;
	}
	public void setDataUltimoTransplante(Date dataUltimoTransplante) {
		this.dataUltimoTransplante = dataUltimoTransplante;
	}
	public Date getDataOcorrenciaTransplante() {
		return dataOcorrenciaTransplante;
	}
	public void setDataOcorrenciaTransplante(Date dataOcorrenciaTransplante) {
		this.dataOcorrenciaTransplante = dataOcorrenciaTransplante;
	}
	public String getDescricaoRetorno() {
		return descricaoRetorno;
	}
	public void setDescricaoRetorno(String descricaoRetorno) {
		this.descricaoRetorno = descricaoRetorno;
	}
	public Boolean getIndAusente() {
		return indAusente;
	}
	public void setIndAusente(Boolean indAusente) {
		this.indAusente = indAusente;
	}
	public DominioSituacaoTmo getTipoTMO() {
		return tipoTMO;
	}
	public void setTipoTMO(DominioSituacaoTmo tipoTMO) {
		this.tipoTMO = tipoTMO;
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
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getObservacaoTransplante() {
		return observacaoTransplante;
	}
	public void setObservacaoTransplante(String observacaoTransplante) {
		this.observacaoTransplante = observacaoTransplante;
	}
	public Integer getSeqTransplante() {
		return seqTransplante;
	}
	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
	}
	
	public String getTransplante(){
		if (DominioSituacaoTmo.G.equals(this.tipoTMO)){
			return "TMO - " + this.tipoTMO.getDescricao() + StringUtils.SPACE + this.tipoAlogenico.getDescricao();
		}
		else if (DominioSituacaoTmo.U.equals(this.tipoTMO)){
			return "TMO - " + this.tipoTMO.getDescricao();
		}
		else {
			if (this.tipoOrgao != null){
				return "Órgão - " + this.tipoOrgao.getDescricao();
			}
		}
		
		return StringUtils.EMPTY;
	}
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}
	public String getCorLegenda() {
		return corLegenda;
	}
	public void setCorLegenda(String corLegenda) {
		this.corLegenda = corLegenda;
	}
	public Boolean getPrevisao() {
		return previsao;
	}
	public void setPrevisao(Boolean previsao) {
		this.previsao = previsao;
	}
	
	public Short getDddFoneResidencial() {
		return dddFoneResidencial;
	}
	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}
	public Long getFoneResidencial() {
		return foneResidencial;
	}
	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}
	public Short getDddFoneRecado() {
		return dddFoneRecado;
	}
	public void setDddFoneRecado(Short dddFoneRecado) {
		this.dddFoneRecado = dddFoneRecado;
	}
	public String getFoneRecado() {
		return foneRecado;
	}
	public void setFoneRecado(String foneRecado) {
		this.foneRecado = foneRecado;
	}
	public Date getDataConsultaSemHora(){
		Calendar dataInstancia = Calendar.getInstance();
		dataInstancia.setTime(this.getDataConsulta());
		dataInstancia.set(Calendar.HOUR_OF_DAY, 0);
		dataInstancia.set(Calendar.MINUTE, 0);
		dataInstancia.set(Calendar.SECOND, 0);
		dataInstancia.set(Calendar.MILLISECOND, 0);
		
		return dataInstancia.getTime();
	}
	
	public Date getHoraDataConsulta(){
		Calendar dataInstancia = Calendar.getInstance();
		dataInstancia.setTime(this.getDataConsulta());
		
		dataInstancia.set(Calendar.SECOND, 0);
		dataInstancia.set(Calendar.MILLISECOND, 0);
		
		return dataInstancia.getTime();
	}
	
	public Date getHoraFim(){
		Calendar dataInstancia = Calendar.getInstance();
		dataInstancia.setTime(this.getDataConsulta());
		
		int minutos = dataInstancia.get(Calendar.MINUTE);
		if (minutos < 30){
			dataInstancia.add(Calendar.MINUTE, 30 - minutos);
		}
		else {
			dataInstancia.set(Calendar.MINUTE, 0);
			dataInstancia.add(Calendar.HOUR_OF_DAY, 1);
		}
		
		return dataInstancia.getTime();
	}
	
	public String getContato() {
		return contato;
	}
	public void setContato(String contato) {
		this.contato = contato;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AgendaTransplanteRetornoVO){
			AgendaTransplanteRetornoVO objeto = (AgendaTransplanteRetornoVO) obj;
			
			return this.codPaciente.equals(objeto.getCodPaciente())
					&& this.getDataConsultaSemHora().equals(objeto.getDataConsultaSemHora());
		}
		else {
			return false;
		}
	}
	
	public static Comparator<AgendaTransplanteRetornoVO> DataConsultaComparator = new Comparator<AgendaTransplanteRetornoVO>(){
	    public int compare(AgendaTransplanteRetornoVO ag1, AgendaTransplanteRetornoVO ag2) {
		      Date data1 = ag1.getDataConsulta();
		      Date data2 = ag2.getDataConsulta();
	 
		      //ascending order
		      return data1.compareTo(data2);
		    }
	};
}
