package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;

public class PacienteAcolhimentoVO extends ListaPacienteVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5792644491898343921L;

	private Integer codigo;
	private Date dataInicio;
	private String paciente;
	private DominioSexo sexo;
	private Date dataNascimento;
	private Integer prontuario;
	private Integer codigoPaciente;
	private Short numeroCiclo;
	private Integer codigoCiclo;
	private String nomeServidor;
	private String nomeServidorValida;
	private DominioSituacaoSessao situacaoSessao;
	private Boolean medicamentoDomiciliar;
	private Boolean impressaoPulseira;
	private DominioSituacaoAdministracao situacaoAdministracao;
	private Integer codigoAtendimento;
	private Integer primeiraconsulta;
	private Integer indgmr;
	private DominioSituacaoHorarioSessao situacaoHorario;
	private List<MptProtocoloCiclo> listaProtocoloCiclo = new ArrayList<MptProtocoloCiclo>();
	private String colorColunaAmarelo;
	private String colorColunaVerde;
	private String colorLinha;
	private MptExtratoSessao mptExtratoSessao;
	private Short tipoSessaoSeq;
	private List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas = new ArrayList<RegistroIntercorrenciaVO>();
	
	public enum Fields {
		CODIGO("codigo"),
		DATA_INICIO("dataInicio"),
		PACIENTE("paciente"),
		SEXO("sexo"),
		DATANASCIMENTO("dataNascimento"),
		PRONTUARIO("prontuario"),
		NUMERO_CICLO("numeroCiclo"),
		CODIGO_CICLO("codigoCiclo"),
		NOME_SERVIDOR("nomeServidor"),
		NOME_SERVIDOR_VALIDA("nomeServidorValida"),
		SITUACAO_SESSAO("situacaoSessao"),
		MEDICAMENTO_DOMICILIAR("medicamentoDomiciliar"),
		IMPRESSAO_PULSEIRA("impressaoPulseira"),
		SITUACAO_ADMINISTRACAO("situacaoAdministracao"),
		CODIGO_ATENDIMENTO("codigoAtendimento"),
		PRIMEIRA_CONSULTA("primeiraconsulta"),
		INDGMR("indgmr"),
		SITUACAO_HORARIO("situacaoHorario"),
		CODIGO_PACIENTE("codigoPaciente"),
		TIPO_SESSAO_SEQ("tipoSessaoSeq"),
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
	public String getPrescritor() {
		if (!StringUtils.isEmpty(this.nomeServidorValida)) {
			return this.nomeServidorValida;
		} else if (!StringUtils.isEmpty(this.nomeServidor)) {
			return this.nomeServidor;
		}

		return null;
	}
	public String getNomeServidor() {
		return nomeServidor;
	}
	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}
	public String getNomeServidorValida() {
		return nomeServidorValida;
	}
	public void setNomeServidorValida(String nomeServidorValida) {
		this.nomeServidorValida = nomeServidorValida;
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
	public Boolean getImpressaoPulseira() {
		return impressaoPulseira;
	}
	public void setImpressaoPulseira(Boolean impressaoPulseira) {
		this.impressaoPulseira = impressaoPulseira;
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
	public DominioSituacaoHorarioSessao getSituacaoHorario() {
		return situacaoHorario;
	}
	public void setSituacaoHorario(DominioSituacaoHorarioSessao situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
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
	public MptExtratoSessao getMptExtratoSessao() {
		return mptExtratoSessao;
	}
	public void setMptExtratoSessao(MptExtratoSessao mptExtratoSessao) {
		this.mptExtratoSessao = mptExtratoSessao;
	}
	public Short getTipoSessaoSeq() {
		return tipoSessaoSeq;
	}
	public void setTipoSessaoSeq(Short tipoSessaoSeq) {
		this.tipoSessaoSeq = tipoSessaoSeq;
	}
	public List<RegistroIntercorrenciaVO> getListaIntercorrenciasSelecionadas() {
		return listaIntercorrenciasSelecionadas;
	}
	public void setListaIntercorrenciasSelecionadas(
			List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas) {
		this.listaIntercorrenciasSelecionadas = listaIntercorrenciasSelecionadas;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((codigoPaciente == null) ? 0 : codigoPaciente.hashCode());
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
		PacienteAcolhimentoVO other = (PacienteAcolhimentoVO) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		if (codigoPaciente == null) {
			if (other.codigoPaciente != null){
				return false;
			}
		} else if (!codigoPaciente.equals(other.codigoPaciente)){
			return false;
		}
		return true;
	}
}
