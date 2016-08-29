package br.gov.mec.aghu.internacao.business.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioLocalPaciente;

public class InternacaoAtendimentoUrgenciaPacienteVO {

	private Date dataInicio;
	private DominioLocalPaciente localPaciente;
	private String leitoId;
	private Short numeroQuarto;
	private Short seqUnidadeFuncional;
	private Date dataAltaMedica;
	private Integer seqAtendimentoUrgencia;
	private AtualizarPacienteTipo sigla;
	private String codigoTipoAltaMedica;
	private Date dataObito;

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public DominioLocalPaciente getLocalPaciente() {
		return localPaciente;
	}

	public void setLocalPaciente(DominioLocalPaciente localPaciente) {
		this.localPaciente = localPaciente;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Date getDataAltaMedica() {
		return dataAltaMedica;
	}

	public void setDataAltaMedica(Date dataAltaMedica) {
		this.dataAltaMedica = dataAltaMedica;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public AtualizarPacienteTipo getSigla() {
		return sigla;
	}

	public void setSigla(AtualizarPacienteTipo sigla) {
		this.sigla = sigla;
	}

	public String getCodigoTipoAltaMedica() {
		return codigoTipoAltaMedica;
	}

	public void setCodigoTipoAltaMedica(String codigoTipoAltaMedica) {
		this.codigoTipoAltaMedica = codigoTipoAltaMedica;
	}

	public Date getDataObito() {
		return dataObito;
	}

	public void setDataObito(Date dataObito) {
		this.dataObito = dataObito;
	}
	
	public String getDescricaoValores() {		
		StringBuilder sb = new StringBuilder(140);
		sb.append("dthr_inicio: " ).append( this.dataInicio)
		.append(" | ind_local_paciente: " ).append( this.localPaciente.name())
		.append(" | lto_lto_id: " ).append( this.leitoId)
		.append(" | qrt_numero: " ).append( this.numeroQuarto)
		.append(" | unf_seq: " ).append( this.seqUnidadeFuncional)
		.append(" | dthr_alta_medica: " ).append( this.dataAltaMedica)
		.append(" | atu_seq: " ).append( this.seqAtendimentoUrgencia)
		.append(" | sigla: " ).append( this.sigla)
		.append(" | tam_codigo: " ).append( this.codigoTipoAltaMedica);
		
		return sb.toString();
	}
}
