package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class RelatorioCirurgiaComRetornoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8903427772411585409L;

	private Integer crgSeq;
	private Date data;
	private Date dataInicio;
	private Short sala;
	private Short nroAgenda;
	private Integer prontuario;
	private String nomePaciente;
	private Short codigoConvenio;
	private String convenio;
	private Byte codigoPlano;
	private String plano;
	private DominioOrigemPacienteCirurgia origemPaciente;
	private String nomeMedicoResponsavel;
	private String nomeUsualMedicoResponsavel;
	private String especialidade;
	private String tipoAnestesia;
	private Byte codigoDestinoPac;
	private String destinoPac;
	private String anestesista;
	
	private String procedimentos;
	
	public Integer getCrgSeq() {
		return crgSeq;
	}
	
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Short getSala() {
		return sala;
	}

	public void setSala(Short sala) {
		this.sala = sala;
	}

	public Short getNroAgenda() {
		return nroAgenda;
	}

	public void setNroAgenda(Short nroAgenda) {
		this.nroAgenda = nroAgenda;
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

	public String getPaciente() {
		return (prontuario!=null) ? CoreUtil.formataProntuario(prontuario) + " - " + nomePaciente : nomePaciente;
	}
	
	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getPlano() {
		return plano;
	}

	public void setPlano(String plano) {
		this.plano = plano;
	}

	public DominioOrigemPacienteCirurgia getOrigemPaciente() {
		return origemPaciente;
	}

	public String getOrigem() {
		return (origemPaciente!=null) ? (DominioOrigemPacienteCirurgia.A.equals(origemPaciente) ? "AMB": "INT") : null;
	}

	public void setOrigemPaciente(DominioOrigemPacienteCirurgia origemPaciente) {
		this.origemPaciente = origemPaciente;
	}

	public String getNomeMedicoResponsavel() {
		return nomeMedicoResponsavel;
	}

	public void setNomeMedicoResponsavel(String nomeMedicoResponsavel) {
		this.nomeMedicoResponsavel = nomeMedicoResponsavel;
	}

	public String getNomeUsualMedicoResponsavel() {
		return nomeUsualMedicoResponsavel;
	}

	public void setNomeUsualMedicoResponsavel(String nomeUsualMedicoResponsavel) {
		this.nomeUsualMedicoResponsavel = nomeUsualMedicoResponsavel;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(String tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public Byte getCodigoDestinoPac() {
		return codigoDestinoPac;
	}

	public void setCodigoDestinoPac(Byte codigoDestinoPac) {
		this.codigoDestinoPac = codigoDestinoPac;
	}

	public String getDestino() {
		return ((codigoDestinoPac != null) ? codigoDestinoPac + "-" : "") + ((destinoPac != null)?destinoPac : "");
	}
	
	public String getDestinoPac() {
		return destinoPac;
	}

	public void setDestinoPac(String destinoPac) {
		this.destinoPac = destinoPac;
	}

	public String getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(String anestesista) {
		this.anestesista = anestesista;
	}

	public String getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(String procedimentos) {
		this.procedimentos = procedimentos;
	}

	public String getMedicos() {
		return ((nomeUsualMedicoResponsavel!=null)?nomeUsualMedicoResponsavel:StringUtils.substring(nomeMedicoResponsavel, 0, 15)) + ((anestesista!=null)?"<BR/>" + anestesista + " (anestesista)":"");
	}
	
	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public Byte getCodigoPlano() {
		return codigoPlano;
	}

	public void setCodigoPlano(Byte codigoPlano) {
		this.codigoPlano = codigoPlano;
	}



	public enum Fields {
		CRG_SEQ("crgSeq"),
		DATA("data"),
		DATA_INCIO("dataInicio"),
		SALA("sala"),
		NRO_AGENDA("nroAgenda"),
		PRONTUARIO("prontuario"),
		NOME("nomePaciente"),
		CODIGO_CONVENIO("codigoConvenio"),
		CODIGO_PLANO("codigoPlano"),
		CONVENIO("convenio"),
		PLANO("plano"),
		ORIGEM("origemPaciente"),
		NOME_MEDICO("nomeMedicoResponsavel"),
		NOME_USUAL_MEDICO("nomeUsualMedicoResponsavel"),
		ESPECIALIDADE("especialidade"),
		TIPO_ANESTESIA("tipoAnestesia"),
		CODIGO_DESTINO("codigoDestinoPac"),
		DESTINO("destinoPac");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
