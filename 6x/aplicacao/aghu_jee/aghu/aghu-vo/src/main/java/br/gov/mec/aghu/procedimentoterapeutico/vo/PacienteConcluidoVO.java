package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricaoSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;

public class PacienteConcluidoVO extends ListaPacienteVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5792644291894347921L;

	private Integer codigo;
	private Date dataInicio;
	private Date dataChegada;
	private Date dataTriagem;
	private Date dataAtendimento;
	private String paciente;
	private DominioSexo sexo;
	private Date dataNascimento;
	private Integer prontuario;
	private Integer codigoPaciente;
	private Short numeroCiclo;
	private Integer codigoCiclo;
	private String nomeServidorResponsavel;
	private DominioSituacaoSessao situacaoSessao;
	private Boolean medicamentoDomiciliar;
	private Boolean impressaoPulseira;
	private DominioSituacaoAdministracao situacaoAdministracao;
	private Integer codigoAtendimento;
	private Integer primeiraconsulta;
	private Integer indgmr;
	private Short dia;
	private List<MptProtocoloCiclo> listaProtocoloCiclo = new ArrayList<MptProtocoloCiclo>();
	private String colorColunaAmarelo;
	private String colorColunaVerde;
	private String colorLinha;
	private Short seqAgendamento;
	private Integer seqSessao;
	private DominioSituacaoPrescricaoSessao situacaoLm;
	private List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas = new ArrayList<RegistroIntercorrenciaVO>();
	
	public enum Fields {
		CODIGO("codigo"),
		DATA_INICIO("dataInicio"),
		DATA_CHEGADA("dataChegada"),
		DATA_TRIAGEM("dataTriagem"),
		DATA_ATENDIMENTO("dataAtendimento"),
		PACIENTE("paciente"),
		SEXO("sexo"),
		DATANASCIMENTO("dataNascimento"),
		PRONTUARIO("prontuario"),
		NUMERO_CICLO("numeroCiclo"),
		CODIGO_CICLO("codigoCiclo"),
		RESPONSAVEL("nomeServidorResponsavel"),
		SITUACAO_SESSAO("situacaoSessao"),
		SEQ_SESSAO("seqSessao"),
		MEDICAMENTO_DOMICILIAR("medicamentoDomiciliar"),
		IMPRESSAO_PULSEIRA("impressaoPulseira"),
		SITUACAO_ADMINISTRACAO("situacaoAdministracao"),
		CODIGO_ATENDIMENTO("codigoAtendimento"),
		PRIMEIRA_CONSULTA("primeiraconsulta"),
		INDGMR("indgmr"),
		DIA("dia"),
		SITUACAO_LM("situacaoLm"),
		CODIGO_PACIENTE("codigoPaciente"),
		SEQ_AGENDAMENTO("seqAgendamento"),
		PROTOCOLO_CICLO("listaProtocoloCiclo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public DominioSexo getSexo() {
		return sexo;
	}
	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Short getNumeroCiclo() {
		return numeroCiclo;
	}
	public void setNumeroCiclo(Short numeroCiclo) {
		this.numeroCiclo = numeroCiclo;
	}
	public Integer getCodigoCiclo() {
		return codigoCiclo;
	}
	public void setCodigoCiclo(Integer codigoCiclo) {
		this.codigoCiclo = codigoCiclo;
	}
	public DominioSituacaoSessao getSituacaoSessao() {
		return situacaoSessao;
	}
	public void setSituacaoSessao(DominioSituacaoSessao situacaoSessao) {
		this.situacaoSessao = situacaoSessao;
	}
	public Boolean getMedicamentoDomiciliar() {
		return medicamentoDomiciliar;
	}
	public void setMedicamentoDomiciliar(Boolean medicamentoDomiciliar) {
		this.medicamentoDomiciliar = medicamentoDomiciliar;
	}
	public DominioSituacaoAdministracao getSituacaoAdministracao() {
		return situacaoAdministracao;
	}
	public void setSituacaoAdministracao(
			DominioSituacaoAdministracao situacaoAdministracao) {
		this.situacaoAdministracao = situacaoAdministracao;
	}
	public Integer getCodigoAtendimento() {
		return codigoAtendimento;
	}
	public void setCodigoAtendimento(Integer codigoAtendimento) {
		this.codigoAtendimento = codigoAtendimento;
	}
	public Integer getPrimeiraconsulta() {
		return primeiraconsulta;
	}
	public void setPrimeiraconsulta(Integer primeiraconsulta) {
		this.primeiraconsulta = primeiraconsulta;
	}
	public Integer getIndgmr() {
		return indgmr;
	}
	public void setIndgmr(Integer indgmr) {
		this.indgmr = indgmr;
	}
	public List<MptProtocoloCiclo> getListaProtocoloCiclo() {
		return listaProtocoloCiclo;
	}
	public void setListaProtocoloCiclo(List<MptProtocoloCiclo> listaProtocoloCiclo) {
		this.listaProtocoloCiclo = listaProtocoloCiclo;
	}
	public String getColorColunaAmarelo() {
		return colorColunaAmarelo;
	}
	public void setColorColunaAmarelo(String colorColunaAmarelo) {
		this.colorColunaAmarelo = colorColunaAmarelo;
	}
	public String getColorColunaVerde() {
		return colorColunaVerde;
	}
	public void setColorColunaVerde(String colorColunaVerde) {
		this.colorColunaVerde = colorColunaVerde;
	}
	public String getColorLinha() {
		return colorLinha;
	}
	public void setColorLinha(String colorLinha) {
		this.colorLinha = colorLinha;
	}
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public Short getDia() {
		return dia;
	}
	public void setDia(Short dia) {
		this.dia = dia;
	}
	public String getNomeServidorResponsavel() {
		return nomeServidorResponsavel;
	}
	public void setNomeServidorResponsavel(String nomeServidorResponsavel) {
		this.nomeServidorResponsavel = nomeServidorResponsavel;
	}
	public Date getDataChegada() {
		return dataChegada;
	}
	public void setDataChegada(Date dataChegada) {
		this.dataChegada = dataChegada;
	}
	public Date getDataTriagem() {
		return dataTriagem;
	}
	public void setDataTriagem(Date dataTriagem) {
		this.dataTriagem = dataTriagem;
	}
	public Date getDataAtendimento() {
		return dataAtendimento;
	}
	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	public Boolean getImpressaoPulseira() {
		return impressaoPulseira;
	}
	public void setImpressaoPulseira(Boolean impressaoPulseira) {
		this.impressaoPulseira = impressaoPulseira;
	}
	public Short getSeqAgendamento() {
		return seqAgendamento;
	}
	public void setSeqAgendamento(Short seqAgendamento) {
		this.seqAgendamento = seqAgendamento;
	}
	public Integer getSeqSessao() {
		return seqSessao;
	}
	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}
	public List<RegistroIntercorrenciaVO> getListaIntercorrenciasSelecionadas() {
		return listaIntercorrenciasSelecionadas;
	}
	public void setListaIntercorrenciasSelecionadas(
			List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas) {
		this.listaIntercorrenciasSelecionadas = listaIntercorrenciasSelecionadas;
	}
	public DominioSituacaoPrescricaoSessao getSituacaoLm() {
		return situacaoLm;
	}
	public void setSituacaoLm(DominioSituacaoPrescricaoSessao situacaoLm) {
		this.situacaoLm = situacaoLm;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((seqSessao == null) ? 0 : seqSessao.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		PacienteConcluidoVO other = (PacienteConcluidoVO) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		if (seqSessao == null) {
			if (other.seqSessao != null){
				return false;
			}
		} else if (!seqSessao.equals(other.seqSessao)){
			return false;
		}
		return true;
	}
}
